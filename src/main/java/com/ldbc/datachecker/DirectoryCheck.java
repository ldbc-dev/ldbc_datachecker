package com.ldbc.datachecker;

import java.io.File;

import com.ldbc.datachecker.FailedCheckPolicy.FailedDirectoryCheckPolicy;

public interface DirectoryCheck
{
    public void checkDirectory( FailedDirectoryCheckPolicy policy, File directory ) throws DirectoryCheckException;
}
