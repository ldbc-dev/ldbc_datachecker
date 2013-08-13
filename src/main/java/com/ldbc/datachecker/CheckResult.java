package com.ldbc.datachecker;

// TODO make message->value, make String->T, using value to hold result
public class CheckResult
{
    private final static CheckResult PASS = new CheckResult( true, "" );
    private final boolean success;
    private final String message;

    public static CheckResult pass()
    {
        return PASS;
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
