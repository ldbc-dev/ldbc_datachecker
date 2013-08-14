package com.ldbc.datachecker.checks.row;

import static com.ldbc.datachecker.checks.file.Column.*;
import static org.junit.Assert.*;
import static org.hamcrest.CoreMatchers.*;

import java.util.Date;

import org.junit.Test;

import com.ldbc.datachecker.checks.file.Column;

public class ColumnTest
{
    @Test
    public void integerColumnShouldOnlyPassWithInteger()
    {
        // Given
        Column<Integer> column = isInteger();
        Column<Integer> columnWithMinMax = isInteger().withMin( 0 ).withMax( 2 );
        Column<Integer> columnWithConsecutive = isInteger().withConsecutive( 1, 1 );

        // When
        String normalPositive1Int = "1";
        String normalPositive2Int = "2";
        String normalPositive3Int = "3";
        String normalPositive4Int = "4";
        String normalNegative1Int = "-1";
        String biggerThanInt = "2147483648";
        String notAnInt = "a string";

        // Then
        assertThat( column.check( normalPositive1Int ).isSuccess(), is( true ) );
        assertThat( column.check( normalNegative1Int ).isSuccess(), is( true ) );
        assertThat( column.check( biggerThanInt ).isSuccess(), is( false ) );
        assertThat( column.check( notAnInt ).isSuccess(), is( false ) );
        assertThat( columnWithMinMax.check( normalPositive1Int ).isSuccess(), is( true ) );
        assertThat( columnWithMinMax.check( normalNegative1Int ).isSuccess(), is( false ) );
        assertThat( columnWithConsecutive.check( normalPositive1Int ).isSuccess(), is( true ) );
        assertThat( columnWithConsecutive.check( normalPositive2Int ).isSuccess(), is( true ) );
        assertThat( columnWithConsecutive.check( normalPositive3Int ).isSuccess(), is( true ) );
        assertThat( columnWithConsecutive.check( normalPositive4Int ).isSuccess(), is( true ) );
        assertThat( columnWithConsecutive.check( normalPositive3Int ).isSuccess(), is( false ) );
    }

    @Test
    public void longColumnShouldOnlyPassWithLong()
    {
        // Given
        Column<Long> column = isLong();
        Column<Long> columnWithMinMax = isLong().withMin( 0l ).withMax( 2l );

        // When
        String normalPositiveLong = "1";
        String normalNegativeLong = "-1";
        String biggerThanLong = "9223372036854775808";
        String notALong = "a string";

        // Then
        assertThat( column.check( normalPositiveLong ).isSuccess(), is( true ) );
        assertThat( column.check( normalNegativeLong ).isSuccess(), is( true ) );
        assertThat( column.check( biggerThanLong ).isSuccess(), is( false ) );
        assertThat( column.check( notALong ).isSuccess(), is( false ) );
        assertThat( columnWithMinMax.check( normalPositiveLong ).isSuccess(), is( true ) );
        assertThat( columnWithMinMax.check( normalNegativeLong ).isSuccess(), is( false ) );
    }

    @Test
    public void stringColumnShouldOnlyPassWithString()
    {
        // Given
        Column<String> column = isString();
        Column<String> columnWithPattern = isString().withRegex( "string1|string2" );

        // When
        String string1 = "string1";
        String string2 = "string2";
        String string3 = "string3";

        // Then
        assertThat( column.check( string1 ).isSuccess(), is( true ) );
        assertThat( column.check( string2 ).isSuccess(), is( true ) );
        assertThat( column.check( string3 ).isSuccess(), is( true ) );
        assertThat( columnWithPattern.check( string1 ).isSuccess(), is( true ) );
        assertThat( columnWithPattern.check( string2 ).isSuccess(), is( true ) );
        assertThat( columnWithPattern.check( string3 ).isSuccess(), is( false ) );
    }

    public void finiteSetColumnShouldOnlyPassWithFiniteSet()
    {
        // Given
        Column<String> column = isFiniteSet( "one", "two", "three" );

        // When
        String valid1 = "one";
        String valid2 = "two";
        String valid3 = "three";
        String invalid1 = "one|two";
        String invalid2 = "four";
        String invalid3 = "";

        // Then
        assertThat( column.check( valid1 ).isSuccess(), is( true ) );
        assertThat( column.check( valid2 ).isSuccess(), is( true ) );
        assertThat( column.check( valid3 ).isSuccess(), is( true ) );
        assertThat( column.check( invalid1 ).isSuccess(), is( false ) );
        assertThat( column.check( invalid2 ).isSuccess(), is( false ) );
        assertThat( column.check( invalid3 ).isSuccess(), is( false ) );
    }

    // 2010-03-11T11:36:58Z
    @Test
    public void urlColumnShouldOnlyPassWithUrl()
    {
        // Given
        Column<String> urlColumn = isUrl();
        // Column urlColumnWithAsciiConversion = isUrl().withEncoding( "UTF-8"
        // );
        Column<String> urlColumnWithoutAccents = isUrl().withAccents( false );

        // When
        String validUrl = "http://dbpedia.org/resource/Sao_Paulo";
        String validUrlWithSpecialCharacters = "http://dbpedia.org/resource/São_.Paulo";
        String invalidUrlWithUmlaut = "not a URL";
        String urlWithUmlautAndHyphen = "http://dbpedia.org/resource/PENTA_–_Pena_Transportes_Aéreos";
        String urlWithStangeL = "http://dbpedia.org/resource/Pułtusk_Academy_of_Humanities";

        // Then
        assertThat( urlColumn.check( validUrl ).isSuccess(), is( true ) );
        assertThat( urlColumn.check( validUrlWithSpecialCharacters ).isSuccess(), is( false ) );
        assertThat( urlColumn.check( invalidUrlWithUmlaut ).isSuccess(), is( false ) );
        assertThat( urlColumn.check( urlWithUmlautAndHyphen ).isSuccess(), is( false ) );
        assertThat( urlColumn.check( urlWithStangeL ).isSuccess(), is( false ) );
        // assertThat( urlColumnWithAsciiConversion.check( validUrl
        // ).isSuccess(), is( true ) );
        // assertThat( urlColumnWithAsciiConversion.check(
        // validUrlWithSpecialCharacters ).isSuccess(), is( true ) );
        // assertThat( urlColumnWithAsciiConversion.check( invalidUrlWithUmlaut
        // ).isSuccess(), is( false ) );
        assertThat( urlColumnWithoutAccents.check( validUrl ).isSuccess(), is( true ) );
        assertThat( urlColumnWithoutAccents.check( validUrlWithSpecialCharacters ).isSuccess(), is( true ) );
        assertThat( urlColumnWithoutAccents.check( invalidUrlWithUmlaut ).isSuccess(), is( false ) );
        assertThat( urlColumnWithoutAccents.check( urlWithUmlautAndHyphen ).isSuccess(), is( true ) );
        // assertThat( urlColumnWithoutAccents.check( urlWithStangeL
        // ).isSuccess(), is( true ) );
    }

    @Test
    public void dateColumnShouldOnlyPassWithDate()
    {
        // Given
        Column<Date> dateColumn = isDate( "yyyy-MM-dd'T'HH:mm:ss'Z'" );

        // When
        String validDate = "2010-03-11T11:36:58Z";
        String invalidDate = "2010-03-11T11:36:58+0200";

        // Then
        assertThat( dateColumn.check( validDate ).isSuccess(), is( true ) );
        assertThat( dateColumn.check( invalidDate ).isSuccess(), is( false ) );
    }

    @Test
    public void emailColumnShouldOnlyPassWithEmail()
    {
        // Given
        Column<String> emailColumn = isEmailAddress();

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
        assertThat( emailColumn.check( valid1 ).isSuccess(), is( true ) );
        assertThat( emailColumn.check( valid2 ).isSuccess(), is( true ) );
        assertThat( emailColumn.check( valid3 ).isSuccess(), is( true ) );
        assertThat( emailColumn.check( valid4 ).isSuccess(), is( true ) );
        assertThat( emailColumn.check( valid5 ).isSuccess(), is( true ) );
        assertThat( emailColumn.check( valid6 ).isSuccess(), is( true ) );

        assertThat( emailColumn.check( invalid1 ).isSuccess(), is( false ) );
        assertThat( emailColumn.check( invalid2 ).isSuccess(), is( false ) );
        assertThat( emailColumn.check( invalid3 ).isSuccess(), is( false ) );
        assertThat( emailColumn.check( invalid4 ).isSuccess(), is( false ) );
        assertThat( emailColumn.check( invalid5 ).isSuccess(), is( false ) );
        assertThat( emailColumn.check( invalid6 ).isSuccess(), is( false ) );
        assertThat( emailColumn.check( invalid7 ).isSuccess(), is( false ) );
        assertThat( emailColumn.check( invalid8 ).isSuccess(), is( false ) );
        assertThat( emailColumn.check( invalid9 ).isSuccess(), is( false ) );
        assertThat( emailColumn.check( invalid10 ).isSuccess(), is( false ) );
    }
}
