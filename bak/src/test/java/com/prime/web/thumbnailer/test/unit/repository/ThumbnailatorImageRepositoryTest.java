//package com.prime.web.thumbnailer.test.unit.repository;
//
//import java.lang.reflect.Field;
//
//import javax.persistence.EntityManager;
//import javax.persistence.NoResultException;
//import javax.persistence.PersistenceContext;
//
//import org.junit.Assert;
//import org.junit.BeforeClass;
//import org.junit.FixMethodOrder;
//import org.junit.Test;
//import org.junit.runners.MethodSorters;
//import org.springframework.beans.factory.annotation.Autowired;
//import org.springframework.transaction.annotation.Transactional;
//
//import com.prime.web.thumbnailer.test.unit.AbstractSpringTest;
//
//import io.prime.web.thumbnailator.domain.Image;
//import io.prime.web.thumbnailator.repository.ThumbnailatorImageRepository;
//import io.prime.web.thumbnailator.util.FileNameValidatorImpl;
//import io.prime.web.thumbnailator.util.ImageIdGenerator;
//import io.prime.web.thumbnailator.util.UUIDImageIdGenerator;
//
//@FixMethodOrder(MethodSorters.NAME_ASCENDING)
//@Transactional
//public class ThumbnailatorImageRepositoryTest extends AbstractSpringTest
//{
//	@Autowired
//	private ThumbnailatorImageRepository repository;
//	
//	@PersistenceContext
//	private EntityManager em;
//	
//	private static ImageIdGenerator generator;
//	
//	private static String imageId;
//	
//	@BeforeClass
//	public static void init() throws Exception
//	{
//		generator = new UUIDImageIdGenerator();
//		Field field = UUIDImageIdGenerator.class.getDeclaredField("validator");
//		field.setAccessible(true);
//		field.set(generator, new FileNameValidatorImpl());
//		imageId = generator.generate("sample_image_for_repository.jpg");
//	}
//	
//	@Test
//	public void A_imagePeristingTest()
//	{
//		Image image = new Image();
//		image.setId(imageId);
//		image.setData(new byte[]{11,12,13});
//		this.repository.save(image);
//		this.em.flush();
//	}
//	
//	@Test
//	public void B_imageFechingTest()
//	{
//		this.A_imagePeristingTest();
//		Image image = this.repository.findOne(imageId);
//		Assert.assertNotNull(image);
//	}
//	
//	@Test(expected=NoResultException.class)
//	public void C_imageFechingErrorTest()
//	{
//		this.repository.findOne(imageId + ".bak");
//	}
//	
//	
//}
