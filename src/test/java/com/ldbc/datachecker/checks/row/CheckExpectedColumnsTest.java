package com.ldbc.datachecker.checks.row;

import static com.ldbc.datachecker.Column.*;
import static org.hamcrest.CoreMatchers.is;
import static org.junit.Assert.assertThat;

import org.junit.Test;

import com.ldbc.datachecker.Column;
import com.ldbc.datachecker.ColumnCheckException;
import com.ldbc.datachecker.FailedCheckPolicy;
import com.ldbc.datachecker.FileCheck;
import com.ldbc.datachecker.FileCheckException;
import com.ldbc.datachecker.checks.file.ExpectedColumns;
import com.ldbc.datachecker.failure.TerminateFailedCheckPolicy;

public class CheckExpectedColumnsTest
{
    @Test
    public void shouldCheckExpectedColumns()
    {
        // Given
        String[] row = new String[] { "5", "2147483648", "just a string", "one" };
        String[] longRow = new String[] { "5", "2147483648", "just a string", "one", "this field makes it too long" };
        String expectedFilename = "correct.csv";

        // When
        FileCheck expectedColumnsCheck = new ExpectedColumns( expectedFilename, new Column[] { isInteger(), isLong(),
                isString(), isFiniteSet( "one", "two" ) } );

        // Then
        assertThat( fileCheckPassed( expectedColumnsCheck, row ), is( true ) );
        assertThat( fileCheckPassed( expectedColumnsCheck, longRow ), is( false ) );
    }

    private boolean fileCheckPassed( FileCheck fileCheck, String[] row )
    {
        FailedCheckPolicy policy = new TerminateFailedCheckPolicy();
        long lineNumber = 1;
        boolean checkPassed = true;
        try
        {
            fileCheck.checkLine( policy.getFailedFileCheckPolicy(),
                    policy.getFailedColumnCheckPolicy( fileCheck, lineNumber, row ), lineNumber, row );
        }
        catch ( FileCheckException e )
        {
            checkPassed = false;
        }
        catch ( ColumnCheckException e )
        {
            checkPassed = false;
        }
        return checkPassed;
    }
}
