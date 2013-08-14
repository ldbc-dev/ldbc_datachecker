package com.ldbc.datachecker;

public interface DirectoryCheck
{
    public CheckResult<?> check( String path );
}
