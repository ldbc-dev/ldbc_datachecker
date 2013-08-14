package com.ldbc.datachecker;

import java.io.FileNotFoundException;

import org.apache.log4j.Logger;

public class FileCheckRunner
{
    private static final Logger logger = Logger.getLogger( FileCheckRunner.class );

    private final FailedCheckPolicy policy;

    public FileCheckRunner( FailedCheckPolicy policy )
    {
        this.policy = policy;
    }

    public void checkFile( FileCheck fileCheck ) throws CheckException
    {
        logger.info( String.format( "Checking[%s] - %s", fileCheck.getClass().getSimpleName(),
                fileCheck.forFile().getName() ) );

        CsvFileReader reader;
        try
        {
            reader = new CsvFileReader( fileCheck.forFile() );
        }
        catch ( FileNotFoundException e )
        {
            String errMsg = String.format( "File not found [%s]\n", fileCheck.forFile().getAbsolutePath() );
            throw new CheckException( errMsg );
        }

        // Check lines of file
        long lineNumber = 0;
        while ( reader.hasNext() )
        {
            String[] line = reader.next();
            if ( lineNumber >= fileCheck.startLine() )
            {
                CheckResult lineResult = fileCheck.checkLine( line );
                if ( false == lineResult.isSuccess() )
                {
                    policy.handleFailedLineCheck( fileCheck, lineResult, lineNumber, line );
                }
            }
            lineNumber++;
        }

        // Check file
        CheckResult fileResult = fileCheck.checkFile();
        if ( false == fileResult.isSuccess() )
        {
            policy.handleFailedFileCheck( fileCheck, fileResult );
        }
    }
}
