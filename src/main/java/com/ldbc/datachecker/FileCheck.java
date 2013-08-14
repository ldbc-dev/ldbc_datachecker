package com.ldbc.datachecker;

import java.io.File;

public interface FileCheck
{
    public File forFile();

    public int startLine();

    public CheckResult checkLine( String[] columns );

    public CheckResult checkFile();
}
