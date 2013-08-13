package com.ldbc.datachecker;

import java.io.File;
import java.io.FileNotFoundException;
import java.util.Arrays;
import java.util.List;

import org.apache.log4j.Logger;

public class FileCheckRunner
{
    private static final Logger logger = Logger.getLogger( FileCheckRunner.class );
    private final List<FileCheck> fileChecks;
    private final File file;

    public FileCheckRunner( String path, List<FileCheck> fileChecks )
    {
        this.file = new File( path );
        this.fileChecks = fileChecks;
    }

    public CheckResult check()
    {
        logger.info( String.format( "Checking %s", file.getAbsolutePath() ) );

        CsvFileReader reader;
        try
        {
            reader = new CsvFileReader( file );
        }
        catch ( FileNotFoundException e )
        {
            String errMsg = String.format( "File not found [%s]\n", file.getAbsolutePath() );
            return CheckResult.fail( errMsg );
        }

        // Check lines of file
        long line = 0;
        while ( reader.hasNext() )
        {
            String[] row = reader.next();
            for ( FileCheck rowCheck : fileChecks )
            {
                if ( line >= rowCheck.startLine() )
                {
                    CheckResult lineResult = rowCheck.checkLine( row );
                    if ( false == lineResult.isSuccess() )
                    {
                        String errMsg = String.format( "File %s\nLine %s\nContent %s\n%s", file.getAbsolutePath(),
                                line, Arrays.toString( row ), lineResult.getMessage() );
                        return CheckResult.fail( errMsg );
                    }
                }
            }
            line++;
        }

        // Check file
        for ( FileCheck fileCheck : fileChecks )
        {
            CheckResult lineResult = fileCheck.checkFile();
            if ( false == lineResult.isSuccess() )
            {
                String errMsg = String.format( "File %s\n%s", file.getAbsolutePath(), lineResult.getMessage() );
                return CheckResult.fail( errMsg );
            }
        }

        return CheckResult.pass();
    }
}
