package com.ldbc.datachecker.checks.file;

import java.io.File;

import com.ldbc.datachecker.Column;
import com.ldbc.datachecker.ColumnCheckException;
import com.ldbc.datachecker.FileCheck;
import com.ldbc.datachecker.FailedCheckPolicy.FailedColumnCheckPolicy;
import com.ldbc.datachecker.FailedCheckPolicy.FailedFileCheckPolicy;
import com.ldbc.datachecker.FileCheckException;

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
    public void checkLine( FailedFileCheckPolicy filePolicy, FailedColumnCheckPolicy columnPolicy, long lineNumber,
            String[] stringColumns ) throws FileCheckException, ColumnCheckException
    {
        if ( columns.length != stringColumns.length )
        {
            filePolicy.handleFailedLineCheck( this,
                    String.format( "Expected %s columns but found %s", columns.length, stringColumns.length ),
                    lineNumber, stringColumns );
        }
        for ( int i = 0; i < columns.length; i++ )
        {
            columns[i].check( columnPolicy, stringColumns[i] );
        }
    }

    @Override
    public void checkFile( FailedFileCheckPolicy filePolicy )
    {
    }
}
