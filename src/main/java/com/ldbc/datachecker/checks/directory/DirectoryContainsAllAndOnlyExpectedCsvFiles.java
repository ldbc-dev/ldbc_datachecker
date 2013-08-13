package com.ldbc.datachecker.checks.directory;

import java.io.File;
import java.io.FilenameFilter;
import java.util.Arrays;
import java.util.HashSet;
import java.util.Set;

import com.ldbc.datachecker.CheckResult;
import com.ldbc.datachecker.DirectoryCheck;

public class DirectoryContainsAllAndOnlyExpectedCsvFiles implements DirectoryCheck
{
    private final Set<String> expectedCsvFiles;

    public DirectoryContainsAllAndOnlyExpectedCsvFiles( String[] expectedCsvFiles )
    {
        this.expectedCsvFiles = new HashSet<String>( Arrays.asList( expectedCsvFiles ) );
    }

    @Override
    public CheckResult check( String path )
    {
        File directory = new File( path );

        FilenameFilter filenameFilter = new FilenameFilter()
        {
            public boolean accept( File directory, String fileName )
            {
                return fileName.endsWith( ".csv" );
            }
        };

        Set<String> foundCsvFiles = new HashSet<String>();
        for ( File csvFile : directory.listFiles( filenameFilter ) )
        {
            foundCsvFiles.add( csvFile.getAbsolutePath() );
        }

        if ( expectedCsvFiles.equals( foundCsvFiles ) )
        {
            return CheckResult.pass();
        }

        Set<String> expectedAndFound = new HashSet<String>();
        expectedAndFound.addAll( foundCsvFiles );
        expectedAndFound.retainAll( expectedCsvFiles );

        Set<String> foundButNotExpected = new HashSet<String>();
        foundButNotExpected.addAll( foundCsvFiles );
        foundButNotExpected.removeAll( expectedCsvFiles );

        Set<String> expectedButNotFound = new HashSet<String>();
        foundButNotExpected.addAll( expectedCsvFiles );
        foundButNotExpected.removeAll( foundCsvFiles );

        StringBuilder errMsg = new StringBuilder();
        errMsg.append( String.format( "CSV files expected and found: %s\n", expectedAndFound.toString() ) );
        errMsg.append( String.format( "CSV files found but not expected: %s\n", foundButNotExpected.toString() ) );
        errMsg.append( String.format( "CSV files expected but not found: %s", expectedButNotFound.toString() ) );

        return CheckResult.fail( errMsg.toString() );
    }
}
