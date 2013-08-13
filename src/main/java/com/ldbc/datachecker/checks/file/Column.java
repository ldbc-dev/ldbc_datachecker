package com.ldbc.datachecker.checks.file;

import java.text.Normalizer;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.regex.Pattern;

import org.apache.commons.validator.routines.UrlValidator;

import com.ldbc.datachecker.CheckResult;

public abstract class Column
{
    public abstract CheckResult check( String columnString );

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

    public abstract static class NumberColumn<T extends Number> extends Column
    {
        private T min;
        private boolean checkMin = false;
        private T max;
        private boolean checkMax = false;
        private boolean checkConsecutive = false;
        private T nextExpectedValue;
        private T incrementBy;

        public NumberColumn<T> withConsecutive( T firstVal, T incrementBy )
        {
            this.checkConsecutive = true;
            this.nextExpectedValue = firstVal;
            this.incrementBy = incrementBy;
            return this;
        }

        public NumberColumn<T> withMin( T min )
        {
            this.checkMin = true;
            this.min = min;
            return this;
        }

        public NumberColumn<T> withMax( T max )
        {
            this.checkMax = true;
            this.max = max;
            return this;
        }

        protected abstract T parse( String columnString ) throws NumberFormatException;

        protected abstract T sum( T t1, T t2 );

        protected abstract boolean lessThan( T t1, T t2 );

        protected abstract boolean greaterThan( T t1, T t2 );

        @Override
        public CheckResult check( String columnString )
        {
            try
            {
                T val = parse( columnString );
                if ( checkMin && lessThan( val, min ) )
                {
                    return CheckResult.fail( String.format( "%s outside of range (%s,%s)", val, min, max ) );
                }
                if ( checkMax && greaterThan( val, max ) )
                {
                    return CheckResult.fail( String.format( "%s outside of range (%s,%s)", val, min, max ) );
                }
                if ( checkConsecutive )
                {
                    if ( false == nextExpectedValue.equals( val ) )
                    {
                        return CheckResult.fail( String.format( "Values should be consecutive, expected %s found %s",
                                nextExpectedValue, val ) );
                    }
                    nextExpectedValue = sum( nextExpectedValue, incrementBy );
                }
            }
            catch ( NumberFormatException e )
            {
                return CheckResult.fail( String.format( "Invalid number format [%s]", columnString ) );
            }
            return CheckResult.pass();
        }
    }

    public static class IntegerColumn extends NumberColumn<Integer>
    {
        @Override
        protected Integer parse( String columnString ) throws NumberFormatException
        {
            return Integer.parseInt( columnString );
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

    public static class LongColumn extends NumberColumn<Long>
    {

        @Override
        protected Long parse( String columnString ) throws NumberFormatException
        {
            return Long.parseLong( columnString );
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

    public static class StringColumn extends Column
    {
        private Pattern regex = null;
        private boolean keepAccents = true;

        public StringColumn withRegex( String regexString )
        {
            regex = Pattern.compile( regexString );
            return this;
        }

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
        public CheckResult check( String columnString )
        {
            if ( null == regex )
            {
                return CheckResult.pass();
            }
            if ( false == keepAccents )
            {
                columnString = removeAccents( columnString );
            }
            return ( regex.matcher( columnString ).matches() ) ? CheckResult.pass() : CheckResult.fail( String.format(
                    "Invalid string pattern, expected: %s", regex.toString() ) );
        }
    }

    public static class FiniteSetColumn extends Column
    {
        private Pattern regex = null;

        public FiniteSetColumn( String... validValues )
        {
            StringBuilder regexString = new StringBuilder();
            for ( int i = 0; i < validValues.length - 1; i++ )
            {
                regexString.append( validValues[i] ).append( "|" );
            }
            regexString.append( validValues[validValues.length - 1] );
            this.regex = Pattern.compile( regexString.toString() );
        }

        @Override
        public CheckResult check( String columnString )
        {
            return ( regex.matcher( columnString ).matches() ) ? CheckResult.pass() : CheckResult.fail( String.format(
                    "Invalid string pattern, expected: %s", regex.toString() ) );
        }
    }

    // TODO withRange(start,end) like min/max
    public static class DateColumn extends Column
    {
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

        public DateColumn( String datePattern )
        {
            this.dateFormat = new SimpleDateFormat( datePattern );
        }

        @Override
        public CheckResult check( String columnString )
        {
            try
            {
                dateFormat.parse( columnString );
                return CheckResult.pass();
            }
            catch ( ParseException e )
            {
                return CheckResult.fail( String.format( "%s has invalid date format\n%s", columnString, e.getMessage() ) );
            }
        }
    }

    public static class UrlColumn extends Column
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
        public CheckResult check( String columnString )
        {
            // if ( null != encoding )
            // {
            // try
            // {
            // columnString = encodeUrl( columnString, encoding );
            // }
            // catch ( Exception e )
            // {
            // return CheckResult.fail( String.format( "URL encoding failed - ",
            // e.getMessage() ) );
            // }
            // }

            if ( false == keepAccents )
            {
                columnString = removeAccents( columnString );
            }

            if ( urlValidator.isValid( columnString ) )
            {
                return CheckResult.pass();
            }
            else
            {
                return CheckResult.fail( String.format( "Invalid URL: %s", columnString ) );
            }
        }
    }
}
