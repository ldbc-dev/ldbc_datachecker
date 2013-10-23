package com.ldbc.datachecker;

import java.text.Normalizer;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.regex.Pattern;

import org.apache.commons.validator.routines.UrlValidator;

import com.google.common.base.Function;
import com.ldbc.datachecker.FailedCheckPolicy.FailedColumnCheckPolicy;

public abstract class Column<VALUE_TYPE, COLUMN_TYPE extends Column>
{
    private List<ColumnRef<VALUE_TYPE>> saveToColumnRefs = new ArrayList<ColumnRef<VALUE_TYPE>>();
    private List<ColumnRef<VALUE_TYPE>> checkInColumnRefs = new ArrayList<ColumnRef<VALUE_TYPE>>();
    private List<ColumnRef<VALUE_TYPE>> saveToAndCheckUniqueColumnRefs = new ArrayList<ColumnRef<VALUE_TYPE>>();

    public final void check( FailedColumnCheckPolicy policy, String columnString ) throws ColumnCheckException
    {
        try
        {
            VALUE_TYPE value = parse( columnString );

            for ( ColumnRef<VALUE_TYPE> columnRef : saveToColumnRefs )
            {
                columnRef.add( value );
            }

            for ( ColumnRef<VALUE_TYPE> columnRef : saveToAndCheckUniqueColumnRefs )
            {
                boolean duplicate = ( false == columnRef.add( value ) );
                if ( duplicate )
                {
                    String errMsg = String.format( "Value %s part of duplicate entry in ColumnRef[%s]", value,
                            columnRef.getName() );
                    policy.handleFailedColumnCheck( columnString, errMsg );
                }
            }

            for ( ColumnRef<VALUE_TYPE> columnRef : checkInColumnRefs )
            {
                if ( false == columnRef.contains( value ) )
                {
                    String errMsg = String.format( "Value %s not found in ColumnRef[%s]", value, columnRef.getName() );
                    policy.handleFailedColumnCheck( columnString, errMsg );
                }
            }

            doCheck( policy, value );
        }
        catch ( ColumnParseException e )
        {
            policy.handleFailedColumnCheck( columnString,
                    String.format( "Failed to parse [%s] - %s", columnString, e.getMessage() ) );
        }
    }

    public abstract VALUE_TYPE parse( String columnString ) throws ColumnParseException;

    protected abstract void doCheck( FailedColumnCheckPolicy policy, VALUE_TYPE columnValue )
            throws ColumnCheckException;

    public final COLUMN_TYPE saveTo( final ColumnRef<VALUE_TYPE> columnRef )
    {
        saveToColumnRefs.add( columnRef );
        return (COLUMN_TYPE) this;
    }

    public final COLUMN_TYPE checkIn( ColumnRef<VALUE_TYPE> columnRef )
    {
        checkInColumnRefs.add( columnRef );
        return (COLUMN_TYPE) this;
    }

    public final COLUMN_TYPE saveToGroupAndCheckUnique( ColumnRef<VALUE_TYPE> columnRef )
    {
        saveToAndCheckUniqueColumnRefs.add( columnRef );
        return (COLUMN_TYPE) this;
    }

    /*
     * Column API
     */

    public static IntegerColumn isInteger()
    {
        return new IntegerColumn();
    }

    public static LongColumn isLong()
    {
        return new LongColumn();
    }

    public static StringColumn isString()
    {
        return new StringColumn();
    }

    public static UrlColumn isUrl()
    {
        return new UrlColumn();
    }

    public static EmailAddressColumn isEmailAddress()
    {
        return new EmailAddressColumn();
    }

    public static FiniteSetColumn isFiniteSet( String... validValues )
    {
        return new FiniteSetColumn( validValues );
    }

    public static DateColumn isDate( String datePattern )
    {
        return new DateColumn( datePattern );
    }

    /*
     * Column Types
     */

    public abstract static class NumberColumn<T extends Number, C extends NumberColumn> extends Column<T, C>
    {
        private T minVal;
        private T maxVal;
        private T nextExpectedValue;

        protected Function<T, Boolean> minViolated = Utils.constantFun( false );
        private Function<T, Boolean> maxViolated = Utils.constantFun( false );
        private Function<T, Boolean> consecutiveViolated = Utils.constantFun( false );

        public C withConsecutive( final T firstVal, final T incrementBy )
        {
            nextExpectedValue = firstVal;
            consecutiveViolated = new Function<T, Boolean>()
            {
                @Override
                public Boolean apply( T input )
                {
                    if ( false == input.equals( nextExpectedValue ) ) return true;
                    nextExpectedValue = sum( nextExpectedValue, incrementBy );
                    return false;
                }
            };
            return (C) this;
        }

        public C withMin( final T min )
        {
            minViolated = new Function<T, Boolean>()
            {
                @Override
                public Boolean apply( T input )
                {
                    return lessThan( input, min );
                }
            };
            this.minVal = min;
            return (C) this;
        }

        public C withMax( final T max )
        {
            maxViolated = new Function<T, Boolean>()
            {
                @Override
                public Boolean apply( T input )
                {
                    return greaterThan( input, max );
                }
            };
            this.maxVal = max;
            return (C) this;
        }

        protected abstract T sum( T t1, T t2 );

        protected abstract boolean lessThan( T t1, T t2 );

        protected abstract boolean greaterThan( T t1, T t2 );

        @Override
        protected void doCheck( FailedColumnCheckPolicy policy, T columnValue ) throws ColumnCheckException
        {
            try
            {
                if ( minViolated.apply( columnValue ) || maxViolated.apply( columnValue ) )
                {
                    policy.handleFailedColumnCheck( columnValue.toString(),
                            String.format( "%s outside of range (%s,%s)", columnValue, minVal, maxVal ) );
                }
                if ( consecutiveViolated.apply( columnValue ) )
                {
                    policy.handleFailedColumnCheck( columnValue.toString(), String.format(
                            "Values should be consecutive, expected %s found %s", nextExpectedValue, columnValue ) );
                }

            }
            catch ( NumberFormatException e )
            {
                policy.handleFailedColumnCheck( columnValue.toString(),
                        String.format( "Invalid number format [%s]", columnValue ) );
            }
        }
    }

    public static class IntegerColumn extends NumberColumn<Integer, IntegerColumn>
    {
        @Override
        public Integer parse( String columnString ) throws ColumnParseException
        {
            try
            {
                return Integer.parseInt( columnString );
            }
            catch ( NumberFormatException e )
            {
                throw new ColumnParseException( e.getCause() );
            }
        }

        @Override
        protected Integer sum( Integer t1, Integer t2 )
        {
            return t1 + t2;
        }

        @Override
        protected boolean lessThan( Integer t1, Integer t2 )
        {
            return t1 < t2;
        }

        @Override
        protected boolean greaterThan( Integer t1, Integer t2 )
        {
            return t1 > t2;
        }
    }

    public static class LongColumn extends NumberColumn<Long, LongColumn>
    {

        @Override
        public Long parse( String columnString ) throws ColumnParseException
        {
            try
            {
                return Long.parseLong( columnString );
            }
            catch ( NumberFormatException e )
            {
                throw new ColumnParseException( e.getCause() );
            }
        }

        @Override
        protected Long sum( Long t1, Long t2 )
        {
            return t1 + t2;
        }

        @Override
        protected boolean lessThan( Long t1, Long t2 )
        {
            return t1 < t2;
        }

        @Override
        protected boolean greaterThan( Long t1, Long t2 )
        {
            return t1 > t2;
        }

    }

    public static class StringColumn extends Column<String, StringColumn>
    {
        private Pattern regex = null;
        private boolean keepAccents = true;

        public StringColumn withRegex( String regexString )
        {
            regex = Pattern.compile( regexString );
            return this;
        }

        // TODO remove?
        public StringColumn withAccents( boolean keepAccents )
        {
            this.keepAccents = keepAccents;
            return this;
        }

        private String removeAccents( String string )
        {
            return Normalizer.normalize( string, Normalizer.Form.NFD ).replaceAll( "\\p{InCombiningDiacriticalMarks}+",
                    "" );
        }

        @Override
        public String parse( String columnString )
        {
            return columnString;
        }

        @Override
        protected void doCheck( FailedColumnCheckPolicy policy, String columnValue ) throws ColumnCheckException
        {
            if ( null == regex )
            {
                return;
            }
            if ( false == keepAccents )
            {
                columnValue = removeAccents( columnValue );
            }
            if ( false == regex.matcher( columnValue ).matches() )
            {
                policy.handleFailedColumnCheck( columnValue,
                        String.format( "Invalid string pattern, expected: %s", regex.toString() ) );
            }
        }
    }

    public static class EmailAddressColumn extends Column<String, EmailAddressColumn>
    {
        // private final Pattern regex = Pattern.compile(
        // "^[\\d\\w\\.\\-_]+@[[\\d\\w\\-]+\\.]+\\w{2,4}$" );
        private final Pattern regex = Pattern.compile( "^\\w+([\\.\\-]\\w+)*@\\w+([\\.\\-]\\w+)*\\.\\w{2,4}$" );

        @Override
        public String parse( String columnString )
        {
            return columnString;
        }

        @Override
        protected void doCheck( FailedColumnCheckPolicy policy, String columnValue ) throws ColumnCheckException
        {
            if ( false == regex.matcher( columnValue ).matches() )
            {
                policy.handleFailedColumnCheck( columnValue,
                        String.format( "Invalid email address pattern, expected: %s", regex.toString() ) );
            }
        }
    }

    // TODO change to withFiniteSet/isFiniteSet rather than a separate column
    public static class FiniteSetColumn extends Column<String, FiniteSetColumn>
    {
        private Pattern regex = null;

        public FiniteSetColumn( String... validValues )
        {
            this.regex = Pattern.compile( Utils.stringArrayToRegexOR( validValues ) );
        }

        @Override
        public String parse( String columnString )
        {
            return columnString;
        }

        @Override
        protected void doCheck( FailedColumnCheckPolicy policy, String columnValue ) throws ColumnCheckException
        {
            if ( false == regex.matcher( columnValue ).matches() )
            {
                policy.handleFailedColumnCheck( columnValue,
                        String.format( "Invalid string pattern, expected: %s", regex.toString() ) );
            }
        }
    }

    // TODO withRange(start,end) like min/max
    public static class DateColumn extends Column<Date, DateColumn>
    {
        protected Function<Date, Boolean> rangeViolated = Utils.constantFun( false );

        /*
        See http://docs.oracle.com/javase/6/docs/api/java/text/SimpleDateFormat.html 
        
        -- Examples --
        
        "yyyy.MM.dd G 'at' HH:mm:ss z"          2001.07.04 AD at 12:08:56 PDT
        "EEE, MMM d, ''yy"                      Wed, Jul 4, '01
        "h:mm a"                                12:08 PM
        "hh 'o''clock' a, zzzz"                 12 o'clock PM, Pacific Daylight Time
        "K:mm a, z"                             0:08 PM, PDT
        "yyyyy.MMMMM.dd GGG hh:mm aaa"          02001.July.04 AD 12:08 PM
        "EEE, d MMM yyyy HH:mm:ss Z"            Wed, 4 Jul 2001 12:08:56 -0700
        "yyMMddHHmmssZ"                         010704120856-0700
        "yyyy-MM-dd'T'HH:mm:ss.SSSZ"            2001-07-04T12:08:56.235-0700         
        */
        private final SimpleDateFormat dateFormat;
        private Date min;
        private Date max;

        public DateColumn( String datePattern )
        {
            this.dateFormat = new SimpleDateFormat( datePattern );
        }

        public DateColumn withRange( final Date min, final Date max )
        {
            rangeViolated = new Function<Date, Boolean>()
            {
                @Override
                public Boolean apply( Date input )
                {
                    if ( input.compareTo( min ) < 0 ) return true;
                    if ( input.compareTo( max ) > 0 ) return true;
                    return false;
                }
            };
            this.min = min;
            this.max = max;
            return this;
        }

        @Override
        protected void doCheck( FailedColumnCheckPolicy policy, Date columnValue ) throws ColumnCheckException
        {
            if ( rangeViolated.apply( columnValue ) )
            {
                policy.handleFailedColumnCheck( columnValue.toString(), String.format(
                        "Value %s is out of range [%s, %s]", dateFormat.format( columnValue ),
                        dateFormat.format( min ), dateFormat.format( max ) ) );
            }
        }

        @Override
        public Date parse( String columnString ) throws ColumnParseException
        {
            try
            {
                return dateFormat.parse( columnString );
            }
            catch ( ParseException e )
            {
                throw new ColumnParseException( String.format( "%s has invalid date format", columnString ),
                        e.getCause() );
            }
        }
    }

    public static class UrlColumn extends Column<String, UrlColumn>
    {
        // private String encoding = null;
        private boolean keepAccents = true;
        private final UrlValidator urlValidator;
        // private final Pattern accentsPattern = Pattern.compile(
        // "\\p{InCombiningDiacriticalMarks}+" );
        private final Pattern accentsPattern = Pattern.compile( "\\p{Mn}+" );

        public UrlColumn()
        {
            urlValidator = new UrlValidator( new String[] { "http", "https" }, UrlValidator.ALLOW_ALL_SCHEMES );
        }

        // public UrlColumn withEncoding( String encoding )
        // {
        // this.encoding = "UTF-8";
        // return this;
        // }

        // TODO remove?
        public UrlColumn withAccents( boolean keepAccents )
        {
            this.keepAccents = keepAccents;
            return this;
        }

        // /*
        // * TODO should not be necessary, but UrlValidator fails on special
        // characters
        // */
        // public String encodeUrl( String url, String encoding ) throws
        // UnsupportedEncodingException
        // {
        // String[] urlProtocolSplit = url.split( "://" );
        // String protocol = urlProtocolSplit[0];
        // String urlMinusProtocol = urlProtocolSplit[1];
        // String[] domainPathSplit = urlMinusProtocol.split( "/", 2 );
        // String domain = domainPathSplit[0];
        // String[] domainSplit = domain.split( "\\." );
        // String path = domainPathSplit[1];
        // String[] pathSplit = path.split( "/" );
        //
        // StringBuilder encodedDomain = new StringBuilder();
        // for ( int i = 0; i < domainSplit.length - 1; i++ )
        // {
        // encodedDomain.append( URLEncoder.encode( domainSplit[i], encoding )
        // ).append( "." );
        // }
        // encodedDomain.append( domainSplit[domainSplit.length - 1] );
        //
        // StringBuilder encodedPath = new StringBuilder();
        // for ( int i = 0; i < pathSplit.length; i++ )
        // {
        // encodedPath.append( "/" ).append( URLEncoder.encode( pathSplit[i],
        // encoding ) );
        // }
        //
        // return String.format( "%s://%s%s", protocol, encodedDomain,
        // encodedPath );
        // }

        private String removeAccents( String string )
        {
            // TODO dash trick is dirty
            return accentsPattern.matcher( Normalizer.normalize( string, Normalizer.Form.NFD ) ).replaceAll( "" ).replaceAll(
                    "\\-|\\u2013|\\u2014", "\\-" );
        }

        @Override
        public String parse( String columnString )
        {
            return columnString;
        }

        @Override
        protected void doCheck( FailedColumnCheckPolicy policy, String columnValue ) throws ColumnCheckException
        {
            // if ( null != encoding )
            // {
            // try
            // {
            // columnString = encodeUrl( columnString, encoding );
            // }
            // catch ( Exception e )
            // {
            // return ColumnResult.fail( String.format(
            // "URL encoding failed - ",
            // e.getMessage() ) );
            // }
            // }

            if ( false == keepAccents )
            {
                columnValue = removeAccents( columnValue );
            }
            if ( false == urlValidator.isValid( columnValue ) )
            {
                policy.handleFailedColumnCheck( columnValue, String.format( "Invalid URL: %s", columnValue ) );
            }
        }
    }
}
