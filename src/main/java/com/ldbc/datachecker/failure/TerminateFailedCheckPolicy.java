package com.ldbc.datachecker.failure;

import java.io.File;
import java.util.Arrays;

import com.ldbc.datachecker.CheckException;
import com.ldbc.datachecker.FailedCheckPolicy;
import com.ldbc.datachecker.FileCheck;
import com.ldbc.datachecker.CheckResult;

public class TerminateFailedCheckPolicy implements FailedCheckPolicy
{
    @Override
    public void handleFailedLineCheck( FileCheck fileCheck, CheckResult checkResult, long lineNumber, String[] line )
            throws CheckException
    {
        String errMsg = String.format( "\nFile %s\nLine %s\nContent %s\n%s", fileCheck.forFile(), lineNumber,
                Arrays.toString( line ), checkResult.getMessage() );
        throw new CheckException( errMsg );
    }

    @Override
    public void handleFailedFileCheck( FileCheck fileCheck, CheckResult checkResult ) throws CheckException
    {
        String errMsg = String.format( "\nFile %s\n%s", fileCheck.forFile().getAbsolutePath(), checkResult.getMessage() );
        throw new CheckException( errMsg );
    }

    @Override
    public void handleFailedDirectoryCheck( File directory, CheckResult checkResult ) throws CheckException
    {
        String errMsg = String.format( "\nDirectory[%s]\n%s", directory.getAbsolutePath(), checkResult.getMessage() );
        throw new CheckException( errMsg );
    }
}
