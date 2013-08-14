package com.ldbc.datachecker;

import java.io.FileNotFoundException;
import java.util.Arrays;

import org.apache.log4j.Logger;

public class FileCheckRunner
{
    private static final Logger logger = Logger.getLogger( FileCheckRunner.class );
    private final FileCheck fileCheck;

    public FileCheckRunner( FileCheck fileCheck )
    {
        this.fileCheck = fileCheck;
    }

    public CheckResult<?> check()
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
            return CheckResult.fail( errMsg );
        }

        // Check lines of file
        long line = 0;
        while ( reader.hasNext() )
        {
            String[] row = reader.next();
            if ( line >= fileCheck.startLine() )
            {
                CheckResult<?> lineResult = fileCheck.checkLine( row );
                if ( false == lineResult.isSuccess() )
                {
                    String errMsg = String.format( "File %s\nLine %s\nContent %s\n%s",
                            fileCheck.forFile().getAbsolutePath(), line, Arrays.toString( row ),
                            lineResult.getMessage() );
                    return CheckResult.fail( errMsg );
                }
            }
            line++;
        }

        // Check file
        CheckResult<?> lineResult = fileCheck.checkFile();
        if ( false == lineResult.isSuccess() )
        {
            String errMsg = String.format( "File %s\n%s", fileCheck.forFile().getAbsolutePath(),
                    lineResult.getMessage() );
            return CheckResult.fail( errMsg );
        }

        return CheckResult.pass( null );
    }
}
