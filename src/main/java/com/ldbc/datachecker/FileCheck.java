package com.ldbc.datachecker;

import java.io.File;

import com.ldbc.datachecker.FailedCheckPolicy.FailedColumnCheckPolicy;
import com.ldbc.datachecker.FailedCheckPolicy.FailedFileCheckPolicy;

public interface FileCheck
{
    public File forFile();

    public int startLine();

    public void checkLine( FailedFileCheckPolicy filePolicy, FailedColumnCheckPolicy columnPolicy, long lineNumber,
            String[] columns ) throws FileCheckException, ColumnCheckException;

    public void checkFile( FailedFileCheckPolicy filePolicy ) throws FileCheckException;
}
