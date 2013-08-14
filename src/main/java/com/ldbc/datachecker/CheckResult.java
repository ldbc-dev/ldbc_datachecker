package com.ldbc.datachecker;

public class CheckResult<T>
{
    private final boolean success;
    private final String message;
    private final T value;

    public static <T1> CheckResult<T1> pass( T1 value )
    {
        return new CheckResult<T1>( true, null, value );
    }

    public static <T1> CheckResult<T1> fail( String message )
    {
        return new CheckResult<T1>( false, message, null );
    }

    private CheckResult( boolean success, String message, T value )
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
        return ( success ) ? "Pass" : String.format( "Fail\n%s", message );
    }
}
