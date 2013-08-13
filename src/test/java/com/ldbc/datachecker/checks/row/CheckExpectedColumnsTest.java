package com.ldbc.datachecker.checks.row;

import static com.ldbc.datachecker.checks.file.Column.*;
import static org.hamcrest.CoreMatchers.is;
import static org.junit.Assert.assertThat;

import java.io.File;

import org.junit.Test;

import com.ldbc.datachecker.FileCheck;
import com.ldbc.datachecker.checks.file.Column;
import com.ldbc.datachecker.checks.file.ExpectedColumns;

public class CheckExpectedColumnsTest
{
    @Test
    public void shouldCheckExpectedColumns()
    {
        // Given
        String[] line = new String[] { "5", "2147483648", "just a string", "one" };
        String[] longLine = new String[] { "5", "2147483648", "just a string", "one", "this field makes it too long" };
        String expectedFilename = "correct.csv";
        String incorrectFilename = "inCorrect.csv";

        // When
        FileCheck check = new ExpectedColumns( expectedFilename, new Column[] { isInteger(), isLong(), isString(),
                isFiniteSet( "one", "two" ) } );

        // Then
        assertThat( check.checkLine( line ).isSuccess(), is( true ) );
        assertThat( check.checkLine( longLine ).isSuccess(), is( false ) );
    }
}
