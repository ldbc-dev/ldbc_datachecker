package com.ldbc.datachecker.checks.file;

import java.io.File;

import com.ldbc.datachecker.CheckResult;
import com.ldbc.datachecker.FileCheck;

public class ExpectedColumns implements FileCheck
{
    private final int startLine;
    private final File forFile;
    private final Column<?>[] columns;

    public ExpectedColumns( String filename, Column<?>... columns )
    {
        this( filename, 1, columns );
    }

    public ExpectedColumns( String filename, int startLine, Column<?>... columns )
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
    public CheckResult<?> checkLine( String[] stringColumns )
    {
        if ( this.columns.length != stringColumns.length )
        {
            return CheckResult.fail( String.format( "Expected %s columns but found %s", this.columns.length,
                    stringColumns.length ) );
        }
        for ( int i = 0; i < this.columns.length; i++ )
        {
            CheckResult<?> result = this.columns[i].check( stringColumns[i] );
            if ( false == result.isSuccess() )
            {
                return CheckResult.fail( String.format( "Column[%s] - %s - %s\n%s", i,
                        columns[i].getClass().getSimpleName(), stringColumns[i], result.getMessage() ) );
            }
        }
        return CheckResult.pass( null );
    }

    @Override
    public CheckResult<?> checkFile()
    {
        return CheckResult.pass( null );
    }
}
