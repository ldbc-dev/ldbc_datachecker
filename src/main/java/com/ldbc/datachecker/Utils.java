package com.ldbc.datachecker;

import com.google.common.base.Function;

public class Utils
{
    public static String[] joinArrays( String[] array1, String[] array2 )
    {
        String[] resultArray = new String[array1.length + array2.length];
        System.arraycopy( array1, 0, resultArray, 0, array1.length );
        System.arraycopy( array2, 0, resultArray, array1.length, array2.length );
        return resultArray;
    }

    public static String[] prefixArrayElementsWith( String[] array, String prefix )
    {
        String[] prefixedArray = new String[array.length];
        for ( int i = 0; i < array.length; i++ )
        {
            prefixedArray[i] = prefix + array[i];
        }
        return prefixedArray;
    }

    public static String stringArrayToRegexOR( String[] strings )
    {
        StringBuilder languagesRegex = new StringBuilder();
        for ( int i = 0; i < strings.length - 1; i++ )
        {
            languagesRegex.append( strings[i] ).append( "|" );
        }
        languagesRegex.append( strings[strings.length - 1] );
        return languagesRegex.toString();
    }

    public static <INPUT, OUTPUT> Function<INPUT, OUTPUT> constantFun( final OUTPUT output )
    {
        return new Function<INPUT, OUTPUT>()
        {
            @Override
            public OUTPUT apply( INPUT input )
            {
                return output;
            }

        };
    }
}
