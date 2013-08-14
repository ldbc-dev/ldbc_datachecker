package com.ldbc.datachecker;

public class ColumnResult<T>
{
    private final boolean success;
    private final String message;
    private final T value;

    public static <T1> ColumnResult<T1> pass( T1 value )
    {
        return new ColumnResult<T1>( true, null, value );
    }

    public static <T1> ColumnResult<T1> fail( String message )
    {
        return new ColumnResult<T1>( false, message, null );
    }

    private ColumnResult( boolean success, String message, T value )
    {
        super();
        this.success = success;
        this.message = message;
        this.value = value;
    }

    public boolean isSuccess()
    {
        return success;
    }

    public String getMessage()
    {
        return message;
    }

    public T getValue()
    {
        return value;
    }

    @Override
    public String toString()
    {
        return ( success ) ? String.format( "Pass: %s", value ) : String.format( "Fail: %s", message );
    }
}
