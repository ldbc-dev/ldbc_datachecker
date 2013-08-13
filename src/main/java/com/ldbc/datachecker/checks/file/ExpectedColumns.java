package com.ldbc.datachecker.checks.file;

import java.io.File;

import com.ldbc.datachecker.CheckResult;
import com.ldbc.datachecker.FileCheck;

public class ExpectedColumns implements FileCheck
{
    private final int startLine;
    private final File forFile;
    private final Column[] columns;

    public ExpectedColumns( String filename, Column... columns )
    {
        this( filename, 1, columns );
    }

    public ExpectedColumns( String filename, int startLine, Column... columns )
    {
        this.forFile = new File( filename );
        this.startLine = startLine;
        this.columns = columns;
    }

    @Override
    public File forFile()
    {
        return forFile;
    }

    @Override
    public int startLine()
    {
        return startLine;
    }

    @Override
    public CheckResult checkLine( String[] columns )
    {
        if ( this.columns.length != columns.length )
        {
            return CheckResult.fail( String.format( "Expected %s columns but found %s", this.columns.length,
                    columns.length ) );
        }
        for ( int i = 0; i < this.columns.length; i++ )
        {
            CheckResult result = this.columns[i].check( columns[i] );
            if ( false == result.isSuccess() )
            {
                return CheckResult.fail( String.format( "Column %s %s\n%s", i, columns[i], result.getMessage() ) );
            }
        }
        return CheckResult.pass();
    }

    @Override
    public CheckResult checkFile()
    {
        return CheckResult.pass();
    }
}
