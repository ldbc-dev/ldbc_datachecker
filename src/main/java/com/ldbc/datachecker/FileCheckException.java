package com.ldbc.datachecker;

public class FileCheckException extends Exception
{
    private static final long serialVersionUID = 2059817646120179168L;

    public FileCheckException()
    {
        super();
    }

    public FileCheckException( String message, Throwable cause )
    {
        super( message, cause );
    }

    public FileCheckException( String message )
    {
        super( message );
    }

    public FileCheckException( Throwable cause )
    {
        super( cause );
    }
}
