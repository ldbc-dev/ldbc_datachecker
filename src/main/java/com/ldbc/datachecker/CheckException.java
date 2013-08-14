package com.ldbc.datachecker;

public class CheckException extends Exception
{
    private static final long serialVersionUID = 2059817646120179168L;

    public CheckException()
    {
        super();
    }

    public CheckException( String message, Throwable cause )
    {
        super( message, cause );
    }

    public CheckException( String message )
    {
        super( message );
    }

    public CheckException( Throwable cause )
    {
        super( cause );
    }
}
