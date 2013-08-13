package com.ldbc.datachecker.checks.directory;

import java.io.File;

import com.ldbc.datachecker.CheckResult;
import com.ldbc.datachecker.DirectoryCheck;

public class IsDirectory implements DirectoryCheck
{
    @Override
    public CheckResult check( String path )
    {
        if ( new File( path ).isDirectory() )
        {
            return CheckResult.pass();
        }
        else
        {
            return CheckResult.fail( String.format( "Not a directory [%s]", path ) );
        }
    }
}
