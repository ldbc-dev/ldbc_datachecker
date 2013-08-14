package com.ldbc.datachecker;

import java.io.File;

import org.apache.log4j.Logger;

public class CheckRunner
{
    private static final Logger logger = Logger.getLogger( CheckRunner.class );

    private final Check check;
    private final File directory;
    private final FailedCheckPolicy policy;

    public CheckRunner( File directory, Check check, FailedCheckPolicy policy ) throws CheckException
    {
        this.check = check;
        this.directory = directory;
        this.policy = policy;
        if ( false == directory.isDirectory() )
        {
            throw new CheckException( "Must be a directory" );
        }
    }

    public void check() throws CheckException
    {
        // Directory checks
        logger.info( String.format( "Performing directory checks on %s", directory.getAbsolutePath() ) );
        for ( DirectoryCheck directoryCheck : check.getDirectoryChecks() )
        {
            CheckResult checkResult = directoryCheck.checkDirectory( directory );
            if ( false == checkResult.isSuccess() )
            {
                policy.handleFailedDirectoryCheck( directory, checkResult );
            }
        }

        // Individual file checks
        logger.info( "Performing file checks" );
        for ( FileCheck fileCheck : check.getFileChecks() )
        {
            FileCheckRunner fileCheckRunner = new FileCheckRunner( policy );
            fileCheckRunner.checkFile( fileCheck );
        }
    }
}
