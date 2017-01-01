package io.prime.web.thumbnailator.util.provided;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.List;
import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.io.IOUtils;
import org.apache.commons.lang3.BooleanUtils;
import org.apache.commons.lang3.StringUtils;
import io.prime.web.thumbnailator.bean.Metadata;
import io.prime.web.thumbnailator.bean.MetadataSource;
import io.prime.web.thumbnailator.bean.internal.ImageCreationContext;
import io.prime.web.thumbnailator.bean.internal.ImageFilterContext;
import io.prime.web.thumbnailator.event.EventManager;
import io.prime.web.thumbnailator.exception.FilterNotFoundException;
import io.prime.web.thumbnailator.filter.ThumbnailatorFilter;
import io.prime.web.thumbnailator.filter.ThumbnailatorFilterSource;
import io.prime.web.thumbnailator.util.ImageIdGenerator;
import io.prime.web.thumbnailator.util.ThumbnailatorUtil;
import io.reactivex.Observable;
import io.reactivex.Single;
import net.coobird.thumbnailator.Thumbnails;
import net.coobird.thumbnailator.Thumbnails.Builder;

public class DefaultThumbnailatorUtil implements ThumbnailatorUtil
{
    private MetadataSource metadataSource;

    private ThumbnailatorFilterSource filterSource;

    private ImageIdGenerator imageIdFactory;

    private EventManager<File> creationEvent;

    private void validate(final ImageCreationContext imageCreationContext)
    {
        Assert.isTrue(StringUtils.isNotBlank(imageCreationContext.getFileName()));
        Assert.notNull(imageCreationContext.getInputStream());
    }

    private void validate(final ImageFilterContext imageFilterContext)
    {
        Assert.isTrue(StringUtils.isNotBlank(imageFilterContext.getFilterName()));
        Assert.isTrue(StringUtils.isNotBlank(imageFilterContext.getImageId()));
        // TODO add more validate
    }

    public Single<String> create(final InputStream inputStream, final String fileName)
    {
        return Single.just(new ImageCreationContext(fileName, inputStream))
                     .doOnSuccess(this::validate)
                     .map(this::doCreate);
    }

    private String doCreate(final ImageCreationContext imageCreationContext) throws IOException
    {
        final Metadata metadata = metadataSource.getMetadata();
        final String imageId = imageIdFactory.generate(imageCreationContext.getFileName());
        final File sourceDirectory = metadata.getSourceDirectory();
        final String ImagePath = sourceDirectory.getPath() + File.separator + (imageId.replaceAll("/", File.separator));
        final File imageFile = new File(ImagePath);
        // make sure file doesn't exists
        Assert.isTrue(BooleanUtils.isFalse(imageFile.exists()));
        imageFile.createNewFile();

        final OutputStream fileOutputStream = new FileOutputStream(imageFile);
        try {
            IOUtils.copy(imageCreationContext.getInputStream(), fileOutputStream);
        } finally {
            IOUtils.closeQuietly(imageCreationContext.getInputStream());
            IOUtils.closeQuietly(fileOutputStream);
        }
        creationEvent.notify(imageFile);
        return imageId;
    }

    /**
     *
     * @param imageId
     * @return
     * @throws IOException
     * @throws FileNotFoundException
     *             for unable to get source file
     */
    public Single<File> getSource(final String imageId)
    {
        return Single.just(imageId)
                     .map(this::doGetSource);
    }

    private File doGetSource(final String imageId)
    {
        final Metadata metadata = metadataSource.getMetadata();
        final File sourceDirectory = metadata.getSourceDirectory();
        final String osBasedImageId = imageId.replace('/', File.separatorChar);
        final File result = new File(sourceDirectory.getPath() + File.separator + osBasedImageId);
        Assert.isTrue(result.exists());
        return result;
    }

    /**
     *
     * @param imageId
     * @return
     * @throws IOException
     * @throws FileNotFoundException
     *             for unable to get source file
     */
    public Single<File> getFiltered(final String imageId, final String filterName)
    {
        return Single.just(new ImageFilterContext(filterName, imageId))
                     .doOnSuccess(this::validate)
                     .doOnSuccess(this::generateFilterFile)
                     .doOnSuccess(this::createFilterFileIfNotExists)
                     .map(ImageFilterContext::getResult);
    }

    private void generateFilterFile(final ImageFilterContext imageFilterContext) throws FileNotFoundException
    {
        final Metadata metadata = metadataSource.getMetadata();
        final String path = metadata.getFilteredDirectory()
                                    .getPath();
        final String filePath = new StringBuilder().append(path)
                                                   .append(File.separatorChar)
                                                   .append(imageFilterContext.getFilterName())
                                                   .append(File.separatorChar)
                                                   .append(imageFilterContext.getImageId())
                                                   .toString();
        imageFilterContext.setResult(new File(filePath));
    }

    private void createFilterFileIfNotExists(final ImageFilterContext imageFilterContext)
    {
        final File resultFile = imageFilterContext.getResult();
        if (BooleanUtils.isFalse(resultFile.exists())) {
            final String imageId = imageFilterContext.getImageId();
            synchronized (imageId.intern()) {
                if (BooleanUtils.isFalse(resultFile.exists())) {
                    final String filterName = imageFilterContext.getFilterName();
                    final File source = this.doGetSource(imageId);
                    final File result = imageFilterContext.getResult();
                    final List<ThumbnailatorFilter> filters = filterSource.getFilters()
                                                                          .get(filterName);
                    if (CollectionUtils.isEmpty(filters)) {
                        throw new FilterNotFoundException(filterName);
                    }

                    final Builder<File> builder = Thumbnails.of(source);

                    Observable.fromIterable(filters)
                              .doOnNext(e -> e.filter(builder))
                              .doOnComplete(() -> builder.toFile(result))
                              .subscribe();
                }
            }
        }
    }

}
