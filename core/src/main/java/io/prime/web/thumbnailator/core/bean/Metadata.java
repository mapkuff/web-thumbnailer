package io.prime.web.thumbnailator.core.bean;

import java.io.File;

public class Metadata
{
    private final File sourceDirectory;

    private final File filteredDirectory;

    public Metadata( final File sourceDirectory, final File filteredDirectory, final boolean databaseEnabled )
    {
        this.sourceDirectory = sourceDirectory;
        this.filteredDirectory = filteredDirectory;
    }

    public File getSourceDirectory()
    {
        return sourceDirectory;
    }

    public File getFilteredDirectory()
    {
        return filteredDirectory;
    }

}
