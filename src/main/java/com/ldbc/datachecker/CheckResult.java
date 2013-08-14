package com.ldbc.datachecker;

public class CheckResult
{
    private final boolean success;
    private final String message;

    public static CheckResult pass()
    {
        return new CheckResult( true, null );
    }

    public static CheckResult fail( String message )
    {
        return new CheckResult( false, message );
    }

    private CheckResult( boolean success, String message )
    {
        super();
        this.success = success;
        this.message = message;
    }

    public boolean isSuccess()
    {
        return success;
    }

    public String getMessage()
    {
        return message;
    }

    @Override
    public String toString()
    {
        return ( success ) ? "Pass" : String.format( "Fail\n%s", message );
    }
}
