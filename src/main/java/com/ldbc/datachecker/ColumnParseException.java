package com.ldbc.datachecker;

public class ColumnParseException extends Exception
{
    private static final long serialVersionUID = 2059817646120179168L;

    public ColumnParseException()
    {
        super();
    }

    public ColumnParseException( String message, Throwable cause )
    {
        super( message, cause );
    }

    public ColumnParseException( String message )
    {
        super( message );
    }

    public ColumnParseException( Throwable cause )
    {
        super( cause );
    }
}
