package com.ldbc.datachecker;

import java.io.FileNotFoundException;

import org.apache.log4j.Logger;

import com.ldbc.datachecker.FailedCheckPolicy.FailedColumnCheckPolicy;
import com.ldbc.datachecker.FailedCheckPolicy.FailedFileCheckPolicy;

public class FileCheckRunner
{
    private static final Logger logger = Logger.getLogger( FileCheckRunner.class );

    private final FailedCheckPolicy policy;

    public FileCheckRunner( FailedCheckPolicy policy )
    {
        this.policy = policy;
    }

    public void checkFile( FileCheck fileCheck ) throws ColumnCheckException, FileCheckException
    {
        logger.info( String.format( "Checking[%s] - %s", fileCheck.getClass().getSimpleName(),
                fileCheck.forFile().getName() ) );

        FailedFileCheckPolicy filePolicy = policy.getFailedFileCheckPolicy();

        CsvFileReader reader;
        try
        {
            reader = new CsvFileReader( fileCheck.forFile() );
        }
        catch ( FileNotFoundException e )
        {
            String errMsg = String.format( "File not found [%s]\n", fileCheck.forFile().getAbsolutePath() );
            throw new ColumnCheckException( errMsg );
        }

        // Check lines of file
        long lineNumber = 0;
        while ( reader.hasNext() )
        {
            String[] row = reader.next();
            FailedColumnCheckPolicy columnPolicy = policy.getFailedColumnCheckPolicy( fileCheck, lineNumber, row );
            if ( lineNumber >= fileCheck.startLine() )
            {
                fileCheck.checkLine( filePolicy, columnPolicy, lineNumber, row );
            }
            lineNumber++;
        }

        // Check file
        fileCheck.checkFile( filePolicy );
    }
}
