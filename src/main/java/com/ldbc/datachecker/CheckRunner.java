package com.ldbc.datachecker;

import java.io.File;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

import org.apache.log4j.Logger;

public class CheckRunner
{
    private static final Logger logger = Logger.getLogger( CheckRunner.class );

    private final Check check;
    private final File directory;

    public CheckRunner( File directory, Check check )
    {
        this.check = check;
        this.directory = directory;
        if ( false == directory.isDirectory() )
        {
            throw new RuntimeException( "Must be a directory" );
        }
    }

    public CheckResult check()
    {
        // Directory checks
        logger.info( String.format( "Performing directory checks on %s", directory.getAbsolutePath() ) );
        for ( DirectoryCheck directoryCheck : check.getDirectoryChecks() )
        {
            CheckResult result = directoryCheck.check( directory.getAbsolutePath() );
            if ( false == result.isSuccess() )
            {
                String errMsg = String.format( "Directory[%s]\n%s", directory.getAbsolutePath(), result.getMessage() );
                return CheckResult.fail( errMsg );
            }
        }

        // Individual file checks
        logger.info( "Performing file checks" );
        for ( Entry<String, List<FileCheck>> fileChecksForFile : groupFileChecksByFilename( check.getFileChecks() ).entrySet() )
        {
            String filename = fileChecksForFile.getKey();
            List<FileCheck> fileChecks = fileChecksForFile.getValue();
            FileCheckRunner fileCheckRunner = new FileCheckRunner( filename, fileChecks );
            CheckResult result = fileCheckRunner.check();
            if ( false == result.isSuccess() )
            {
                return CheckResult.fail( result.getMessage() );
            }
        }

        return CheckResult.pass();
    }

    private Map<String, List<FileCheck>> groupFileChecksByFilename( List<FileCheck> fileChecks )
    {
        Map<String, List<FileCheck>> groupedFileChecks = new HashMap<String, List<FileCheck>>();
        for ( FileCheck fileCheck : fileChecks )
        {
            String filename = fileCheck.forFile().getAbsolutePath();
            List<FileCheck> fileChecksForFile = ( groupedFileChecks.containsKey( filename ) ) ? groupedFileChecks.get( filename )
                    : new ArrayList<FileCheck>();
            fileChecksForFile.add( fileCheck );
            groupedFileChecks.put( filename, fileChecksForFile );
        }
        return groupedFileChecks;
    }
}
