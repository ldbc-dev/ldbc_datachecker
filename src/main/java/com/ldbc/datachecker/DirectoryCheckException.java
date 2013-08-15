package com.ldbc.datachecker;

public class DirectoryCheckException extends Exception
{
    private static final long serialVersionUID = 2059817646120179168L;

    public DirectoryCheckException()
    {
        super();
    }

    public DirectoryCheckException( String message, Throwable cause )
    {
        super( message, cause );
    }

    public DirectoryCheckException( String message )
    {
        super( message );
    }

    public DirectoryCheckException( Throwable cause )
    {
        super( cause );
    }
}
