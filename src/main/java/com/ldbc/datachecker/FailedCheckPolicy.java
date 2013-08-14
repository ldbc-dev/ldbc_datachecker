package com.ldbc.datachecker;

import java.io.File;

public interface FailedCheckPolicy
{
    public void handleFailedLineCheck( FileCheck fileCheck, CheckResult checkResult, long lineNumber, String[] line )
            throws CheckException;

    public void handleFailedFileCheck( FileCheck fileCheck, CheckResult checkResult ) throws CheckException;

    public void handleFailedDirectoryCheck( File directory, CheckResult checkResult ) throws CheckException;
}
