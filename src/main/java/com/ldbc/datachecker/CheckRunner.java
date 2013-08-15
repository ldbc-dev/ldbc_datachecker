package com.ldbc.datachecker;

import java.io.File;

import org.apache.log4j.Logger;

import com.ldbc.datachecker.FailedCheckPolicy.FailedDirectoryCheckPolicy;

public class CheckRunner
{
    private static final Logger logger = Logger.getLogger( CheckRunner.class );

    private final Check check;
    private final File directory;
    private final FailedCheckPolicy policy;

    public CheckRunner( File directory, Check check, FailedCheckPolicy policy ) throws ColumnCheckException
    {
        this.check = check;
        this.directory = directory;
        this.policy = policy;
        if ( false == directory.isDirectory() )
        {
            throw new ColumnCheckException( "Must be a directory" );
        }
    }

    public void check() throws ColumnCheckException, FileCheckException, DirectoryCheckException
    {
        // Directory checks
        logger.info( String.format( "Performing directory checks on %s", directory.getAbsolutePath() ) );
        FailedDirectoryCheckPolicy directoryPolicy = policy.getFailedDirectoryCheckPolicy();
        for ( DirectoryCheck directoryCheck : check.getDirectoryChecks() )
        {
            directoryCheck.checkDirectory( directoryPolicy, directory );
        }

        // Individual file checks
        logger.info( "Performing file checks" );
        FileCheckRunner fileCheckRunner = new FileCheckRunner( policy );
        for ( FileCheck fileCheck : check.getFileChecks() )
        {
            fileCheckRunner.checkFile( fileCheck );
        }
    }
}
