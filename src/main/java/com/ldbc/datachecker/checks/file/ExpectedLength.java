package com.ldbc.datachecker.checks.file;

import java.io.File;

import com.ldbc.datachecker.FileCheck;
import com.ldbc.datachecker.CheckResult;

public class ExpectedLength implements FileCheck
{
    private final int startLine;
    private final File forFile;
    private final long expectedLineCount;
    private long lineCount;

    public ExpectedLength( String filename, long expectedLineCount )
    {
        this( filename, 1, expectedLineCount );
    }

    public ExpectedLength( String filename, int startLine, long expectedLineCount )
    {
        this.forFile = new File( filename );
        this.startLine = startLine;
        this.expectedLineCount = expectedLineCount;
        this.lineCount = 0;
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
        lineCount++;
        return CheckResult.pass();
    }

    @Override
    public CheckResult checkFile()
    {
        return ( lineCount == expectedLineCount ) ? CheckResult.pass() : CheckResult.fail( String.format(
                "File expected to have %s lines, found %s", expectedLineCount, lineCount ) );
    }
}
