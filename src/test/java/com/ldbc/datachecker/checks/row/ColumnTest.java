package com.ldbc.datachecker.checks.row;

import static com.ldbc.datachecker.Column.*;
import static org.junit.Assert.*;
import static org.hamcrest.CoreMatchers.*;

import java.util.Calendar;
import java.util.Date;

import org.junit.Test;

import com.ldbc.datachecker.Column;
import com.ldbc.datachecker.ColumnCheckException;
import com.ldbc.datachecker.ColumnRef;
import com.ldbc.datachecker.FailedCheckPolicy;
import com.ldbc.datachecker.checks.file.ExpectedLength;
import com.ldbc.datachecker.failure.TerminateFailedCheckPolicy;

public class ColumnTest
{
    @Test
    public void columnRefChecksShouldDetectWhenValueExists()
    {
        // Given
        ColumnRef<Long> checkInColumnRef = new ColumnRef.LongColumnRef( "checkIn" );
        LongColumn longColumn = isLong().checkIn( checkInColumnRef );

        // When
        checkInColumnRef.add( 2L );

        // Then
        assertThat( columnCheckPassed( longColumn, "1" ), is( false ) );
        assertThat( columnCheckPassed( longColumn, "2" ), is( true ) );
        assertThat( columnCheckPassed( longColumn, "3" ), is( false ) );
    }

    @Test
    public void columnShouldAddValuesToColumnCheck()
    {
        // Given
        ColumnRef<Long> checkInColumnRef = new ColumnRef.LongColumnRef( "checkIn" );

        LongColumn longWriteColumn = isLong().saveTo( checkInColumnRef );
        LongColumn longReadColumn = isLong().checkIn( checkInColumnRef );

        // When
        assertThat( columnCheckPassed( longWriteColumn, "2" ), is( true ) );

        // Then
        assertThat( columnCheckPassed( longReadColumn, "1" ), is( false ) );
        assertThat( columnCheckPassed( longReadColumn, "2" ), is( true ) );
        assertThat( columnCheckPassed( longReadColumn, "3" ), is( false ) );
    }

    @Test
    public void multiColumnShouldAddValuesAndCheckThem()
    {
        // Given
        ColumnRef<Long> multiColumnRef = new ColumnRef.MultiLongColumnRef( "multi", 2, false );
        LongColumn column1 = isLong().saveToGroupAndCheckUnique( multiColumnRef );
        LongColumn column2 = isLong().saveToGroupAndCheckUnique( multiColumnRef );
        LongColumn column3 = isLong();

        // When

        // Then

        // 1 2 (unique)
        assertThat( columnCheckPassed( column1, "1" ), is( true ) );
        assertThat( columnCheckPassed( column2, "2" ), is( true ) );
        assertThat( columnCheckPassed( column3, "3" ), is( true ) );

        // 1 2 (duplicate)
        assertThat( columnCheckPassed( column1, "1" ), is( true ) );
        assertThat( columnCheckPassed( column2, "2" ), is( false ) );
        assertThat( columnCheckPassed( column3, "3" ), is( true ) );
        assertThat( columnCheckPassed( column3, "3" ), is( true ) );
        assertThat( columnCheckPassed( column3, "3" ), is( true ) );

        // 1 2 (duplicate)
        assertThat( columnCheckPassed( column1, "1" ), is( true ) );
        assertThat( columnCheckPassed( column2, "2" ), is( false ) );
        assertThat( columnCheckPassed( column3, "3" ), is( true ) );

        // 1 1 (unique)
        assertThat( columnCheckPassed( column1, "1" ), is( true ) );
        assertThat( columnCheckPassed( column2, "1" ), is( true ) );
        assertThat( columnCheckPassed( column3, "3" ), is( true ) );

        // 1 1 (duplicate)
        assertThat( columnCheckPassed( column1, "1" ), is( true ) );
        assertThat( columnCheckPassed( column2, "1" ), is( false ) );
    }

    @Test
    public void multiColumnShouldAddValuesAndCheckThemWithSort()
    {
        // Given
        ColumnRef<Long> multiColumnRef = new ColumnRef.MultiLongColumnRef( "multi", 2, true );
        LongColumn column1 = isLong().saveToGroupAndCheckUnique( multiColumnRef );
        LongColumn column2 = isLong().saveToGroupAndCheckUnique( multiColumnRef );
        LongColumn column3 = isLong();

        // When

        // Then

        // 1 2 (unique)
        assertThat( columnCheckPassed( column1, "1" ), is( true ) );
        assertThat( columnCheckPassed( column2, "2" ), is( true ) );
        assertThat( columnCheckPassed( column3, "3" ), is( true ) );

        // 1 2 (duplicate)
        assertThat( columnCheckPassed( column1, "1" ), is( true ) );
        assertThat( columnCheckPassed( column2, "2" ), is( false ) );
        assertThat( columnCheckPassed( column3, "3" ), is( true ) );
        assertThat( columnCheckPassed( column3, "3" ), is( true ) );
        assertThat( columnCheckPassed( column3, "3" ), is( true ) );

        // 1 2 (duplicate)
        assertThat( columnCheckPassed( column1, "2" ), is( true ) );
        assertThat( columnCheckPassed( column2, "1" ), is( false ) );
        assertThat( columnCheckPassed( column3, "3" ), is( true ) );

        // 1 1 (unique)
        assertThat( columnCheckPassed( column1, "1" ), is( true ) );
        assertThat( columnCheckPassed( column2, "1" ), is( true ) );
        assertThat( columnCheckPassed( column3, "3" ), is( true ) );

        // 1 1 (duplicate)
        assertThat( columnCheckPassed( column1, "1" ), is( true ) );
        assertThat( columnCheckPassed( column2, "1" ), is( false ) );
    }

    @Test
    public void integerColumnShouldOnlyPassWithInteger()
    {
        // Given
        IntegerColumn column = isInteger();
        IntegerColumn columnWithMinMax = isInteger().withMin( 0 ).withMax( 2 );
        IntegerColumn columnWithConsecutive = isInteger().withConsecutive( 1, 1 );

        // When
        String normalPositive1Int = "1";
        String normalPositive2Int = "2";
        String normalPositive3Int = "3";
        String normalPositive4Int = "4";
        String normalNegative1Int = "-1";
        String biggerThanInt = "2147483648";
        String notAnInt = "a string";

        // Then
        assertThat( columnCheckPassed( column, normalNegative1Int ), is( true ) );
        assertThat( columnCheckPassed( column, biggerThanInt ), is( false ) );
        assertThat( columnCheckPassed( column, notAnInt ), is( false ) );
        assertThat( columnCheckPassed( columnWithMinMax, normalPositive1Int ), is( true ) );
        assertThat( columnCheckPassed( columnWithMinMax, normalNegative1Int ), is( false ) );
        assertThat( columnCheckPassed( columnWithConsecutive, normalPositive1Int ), is( true ) );
        assertThat( columnCheckPassed( columnWithConsecutive, normalPositive2Int ), is( true ) );
        assertThat( columnCheckPassed( columnWithConsecutive, normalPositive3Int ), is( true ) );
        assertThat( columnCheckPassed( columnWithConsecutive, normalPositive4Int ), is( true ) );
        assertThat( columnCheckPassed( columnWithConsecutive, normalPositive3Int ), is( false ) );
    }

    @Test
    public void longColumnShouldOnlyPassWithLong()
    {
        // Given
        LongColumn column = isLong();
        LongColumn columnWithMinMax = isLong().withMin( 0l ).withMax( 2l );

        // When
        String normalPositiveLong = "1";
        String normalNegativeLong = "-1";
        String biggerThanLong = "9223372036854775808";
        String notALong = "a string";

        // Then
        assertThat( columnCheckPassed( column, normalPositiveLong ), is( true ) );
        assertThat( columnCheckPassed( column, normalNegativeLong ), is( true ) );
        assertThat( columnCheckPassed( column, biggerThanLong ), is( false ) );
        assertThat( columnCheckPassed( column, notALong ), is( false ) );
        assertThat( columnCheckPassed( columnWithMinMax, normalPositiveLong ), is( true ) );
        assertThat( columnCheckPassed( columnWithMinMax, normalNegativeLong ), is( false ) );
    }

    @Test
    public void stringColumnShouldOnlyPassWithString()
    {
        // Given
        StringColumn column = isString();
        StringColumn columnWithPattern = isString().withRegex( "string1|string2" );

        // When
        String string1 = "string1";
        String string2 = "string2";
        String string3 = "string3";

        // Then
        assertThat( columnCheckPassed( column, string1 ), is( true ) );
        assertThat( columnCheckPassed( column, string2 ), is( true ) );
        assertThat( columnCheckPassed( column, string3 ), is( true ) );
        assertThat( columnCheckPassed( columnWithPattern, string1 ), is( true ) );
        assertThat( columnCheckPassed( columnWithPattern, string2 ), is( true ) );
        assertThat( columnCheckPassed( columnWithPattern, string3 ), is( false ) );
    }

    public void finiteSetColumnShouldOnlyPassWithFiniteSet()
    {
        // Given
        FiniteSetColumn column = isFiniteSet( "one", "two", "three" );

        // When
        String valid1 = "one";
        String valid2 = "two";
        String valid3 = "three";
        String invalid1 = "one|two";
        String invalid2 = "four";
        String invalid3 = "";

        // Then
        assertThat( columnCheckPassed( column, valid1 ), is( true ) );
        assertThat( columnCheckPassed( column, valid2 ), is( true ) );
        assertThat( columnCheckPassed( column, valid3 ), is( true ) );
        assertThat( columnCheckPassed( column, invalid1 ), is( false ) );
        assertThat( columnCheckPassed( column, invalid2 ), is( false ) );
        assertThat( columnCheckPassed( column, invalid3 ), is( false ) );
    }

    // 2010-03-11T11:36:58Z
    @Test
    public void urlColumnShouldOnlyPassWithUrl()
    {
        // Given
        UrlColumn urlColumn = isUrl();
        // Column urlColumnWithAsciiConversion = isUrl().withEncoding( "UTF-8"
        // );
        UrlColumn urlColumnWithoutAccents = isUrl().withAccents( false );

        // When
        String validUrl = "http://dbpedia.org/resource/Sao_Paulo";
        String validUrlWithSpecialCharacters = "http://dbpedia.org/resource/São_.Paulo";
        String invalidUrlWithUmlaut = "not a URL";
        String urlWithUmlautAndHyphen = "http://dbpedia.org/resource/PENTA_–_Pena_Transportes_Aéreos";
        String urlWithStangeL = "http://dbpedia.org/resource/Pułtusk_Academy_of_Humanities";

        // Then
        assertThat( columnCheckPassed( urlColumn, validUrl ), is( true ) );
        assertThat( columnCheckPassed( urlColumn, validUrlWithSpecialCharacters ), is( false ) );
        assertThat( columnCheckPassed( urlColumn, invalidUrlWithUmlaut ), is( false ) );
        assertThat( columnCheckPassed( urlColumn, urlWithUmlautAndHyphen ), is( false ) );
        assertThat( columnCheckPassed( urlColumn, urlWithStangeL ), is( false ) );
        // assertThat( urlColumnWithAsciiConversion,validUrl
        // ), is( true ) );
        // assertThat( urlColumnWithAsciiConversion.check(
        // validUrlWithSpecialCharacters ), is( true ) );
        // assertThat( urlColumnWithAsciiConversion,invalidUrlWithUmlaut
        // ), is( false ) );
        assertThat( columnCheckPassed( urlColumnWithoutAccents, validUrl ), is( true ) );
        assertThat( columnCheckPassed( urlColumnWithoutAccents, validUrlWithSpecialCharacters ), is( true ) );
        assertThat( columnCheckPassed( urlColumnWithoutAccents, invalidUrlWithUmlaut ), is( false ) );
        assertThat( columnCheckPassed( urlColumnWithoutAccents, urlWithUmlautAndHyphen ), is( true ) );
        // assertThat( urlColumnWithoutAccents,urlWithStangeL
        // ), is( true ) );
    }

    @Test
    public void dateColumnShouldOnlyPassWithDate()
    {
        // Given
        String dateFormatString = "yyyy-MM-dd'T'HH:mm:ss'Z'";

        Calendar c = Calendar.getInstance();
        c.set( 2010, Calendar.MARCH, 10 );
        Date min = c.getTime();
        c.set( 2010, Calendar.MARCH, 12 );
        Date max = c.getTime();

        DateColumn dateColumn = isDate( dateFormatString ).withRange( min, max );

        // When
        String validDate = "2010-03-11T11:36:58Z";
        String invalidDateTooEarly = "2010-03-10T11:36:58Z";
        String invalidDateTooLate = "2010-03-13T11:36:58Z";
        String invalidDateWrongFormat = "2010-03-11T11:36:58+0200";

        // Then
        assertThat( columnCheckPassed( dateColumn, validDate ), is( true ) );
        assertThat( columnCheckPassed( dateColumn, invalidDateTooEarly ), is( false ) );
        assertThat( columnCheckPassed( dateColumn, invalidDateTooLate ), is( false ) );
        assertThat( columnCheckPassed( dateColumn, invalidDateWrongFormat ), is( false ) );
    }

    @Test
    public void emailColumnShouldOnlyPassWithEmail()
    {
        // Given
        Column emailColumn = isEmailAddress();

        // When
        String valid1 = "aA1@aA1-_.aA1.ab";
        String valid2 = "aA1@aA1-_.aA1.abc";
        String valid3 = "aA1@aA1-_.aA1.abcd";
        String valid4 = "aA1-_.aA1-_.aA1-_.aA1@aA1-_.aA1-_.aA1-_.aA1.ab";
        String valid5 = "aA1-_.aA1-_.aA1-_.aA1@aA1-_.aA1-_.aA1-_.aA1.abc";
        String valid6 = "aA1-_.aA1-_.aA1-_.aA1@aA1-_.aA1-_.aA1-_.aA1.abcd";

        String invalid1 = "aA1@aA1-_.aA1.a";
        String invalid2 = "aA1@aA1-_.aA1.abcde";
        String invalid3 = "aA1.@aA1-_.aA1.ab";
        String invalid4 = "aA1-@aA1-_.aA1.ab";
        String invalid5 = ".aA1@aA1-_.aA1.ab";
        String invalid6 = "-aA1@aA1-_.aA1.ab";
        String invalid7 = "aA1@.aA1.ab";
        String invalid8 = "aA1@-aA1.ab";
        String invalid9 = "aA1@aA1..ab";
        String invalid10 = "aA1@aA1-.ab";

        // Then
        assertThat( columnCheckPassed( emailColumn, valid1 ), is( true ) );
        assertThat( columnCheckPassed( emailColumn, valid2 ), is( true ) );
        assertThat( columnCheckPassed( emailColumn, valid3 ), is( true ) );
        assertThat( columnCheckPassed( emailColumn, valid4 ), is( true ) );
        assertThat( columnCheckPassed( emailColumn, valid5 ), is( true ) );
        assertThat( columnCheckPassed( emailColumn, valid6 ), is( true ) );

        assertThat( columnCheckPassed( emailColumn, invalid1 ), is( false ) );
        assertThat( columnCheckPassed( emailColumn, invalid2 ), is( false ) );
        assertThat( columnCheckPassed( emailColumn, invalid3 ), is( false ) );
        assertThat( columnCheckPassed( emailColumn, invalid4 ), is( false ) );
        assertThat( columnCheckPassed( emailColumn, invalid5 ), is( false ) );
        assertThat( columnCheckPassed( emailColumn, invalid6 ), is( false ) );
        assertThat( columnCheckPassed( emailColumn, invalid7 ), is( false ) );
        assertThat( columnCheckPassed( emailColumn, invalid8 ), is( false ) );
        assertThat( columnCheckPassed( emailColumn, invalid9 ), is( false ) );
        assertThat( columnCheckPassed( emailColumn, invalid10 ), is( false ) );
    }

    private boolean columnCheckPassed( Column columnCheck, String columnString )
    {
        FailedCheckPolicy policy = new TerminateFailedCheckPolicy();

        boolean checkPassed = true;
        try
        {
            columnCheck.check( policy.getFailedColumnCheckPolicy( new ExpectedLength( "", 1 ), 1, new String[] {} ),
                    columnString );
        }
        catch ( ColumnCheckException e )
        {
            checkPassed = false;
        }
        return checkPassed;
    }
}
