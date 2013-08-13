package com.ldbc.datachecker;

import java.util.List;

public interface Check
{
    public List<DirectoryCheck> getDirectoryChecks();

    public List<FileCheck> getFileChecks();
}
