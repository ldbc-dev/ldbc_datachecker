package com.ldbc.datachecker;

public class ColumnCheckException extends Exception
{
    private static final long serialVersionUID = 2059817646120179168L;

    public ColumnCheckException()
    {
        super();
    }

    public ColumnCheckException( String message, Throwable cause )
    {
        super( message, cause );
    }

    public ColumnCheckException( String message )
    {
        super( message );
    }

    public ColumnCheckException( Throwable cause )
    {
        super( cause );
    }
}
