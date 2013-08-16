package com.ldbc.datachecker.failure;

import java.io.File;
import java.util.Arrays;

import org.apache.log4j.Logger;

import com.ldbc.datachecker.ColumnCheckException;
import com.ldbc.datachecker.DirectoryCheck;
import com.ldbc.datachecker.DirectoryCheckException;
import com.ldbc.datachecker.FailedCheckPolicy;
import com.ldbc.datachecker.FileCheck;
import com.ldbc.datachecker.FileCheckException;

public class LoggingFailedCheckPolicy implements FailedCheckPolicy
{
    private final Logger consoleLogger;
    private final Logger csvFileLogger;

    public static LoggingFailedCheckPolicy toConsoleAndFile( Logger consoleLogger, Logger csvFileLogger )
    {
        return new LoggingFailedCheckPolicy( consoleLogger, csvFileLogger );
    }

    public static LoggingFailedCheckPolicy toFileOnly( Logger csvFileLogger )
    {
        return new LoggingFailedCheckPolicy( null, csvFileLogger );
    }

    public static LoggingFailedCheckPolicy toConsoleOnly( Logger consoleLogger )
    {
        return new LoggingFailedCheckPolicy( consoleLogger, null );
    }

    private LoggingFailedCheckPolicy( Logger consoleLogger, Logger csvFileLogger )
    {
        this.consoleLogger = consoleLogger;
        this.csvFileLogger = csvFileLogger;
        if ( null != csvFileLogger )
        {
            this.csvFileLogger.error( "Check;File;Line;Row;Column;Message" );
        }
    }

    @Override
    public FailedColumnCheckPolicy getFailedColumnCheckPolicy( FileCheck fileCheck, long lineNumber, String[] row )
    {
        return new LoggingFailedColumnCheckPolicy( consoleLogger, csvFileLogger, fileCheck, lineNumber, row );
    }

    @Override
    public FailedFileCheckPolicy getFailedFileCheckPolicy()
    {
        return new LoggingFailedFileCheckPolicy( consoleLogger, csvFileLogger );
    }

    @Override
    public FailedDirectoryCheckPolicy getFailedDirectoryCheckPolicy()
    {
        return new LoggingFailedDirectoryCheckPolicy( consoleLogger, csvFileLogger );
    }

    public static class LoggingFailedColumnCheckPolicy extends FailedColumnCheckPolicy
    {
        private final Logger consoleLogger;
        private final Logger csvFileLogger;

        public LoggingFailedColumnCheckPolicy( Logger consoleLogger, Logger csvFileLogger, FileCheck fileCheck,
                long lineNumber, String[] row )
        {
            super( fileCheck, lineNumber, row );
            this.consoleLogger = consoleLogger;
            this.csvFileLogger = csvFileLogger;
        }

        public void handleFailedColumnCheck( String columnString, String message ) throws ColumnCheckException
        {
            if ( null != consoleLogger )
            {
                consoleLogger.error( String.format( "ColumnCheck[%s] File[%s] Line[%s] Row[%s] Column[%s] Message[%s]",
                        getFileCheck().getClass().getSimpleName(), getFileCheck().forFile().getAbsolutePath(),
                        getLineNumber(), Arrays.toString( getRow() ), columnString, message ) );
            }
            if ( null != csvFileLogger )
            {
                // Check;File;Line;Row;Column;Message
                csvFileLogger.error( String.format( "%s;%s;%s;%s;%s;%s", getFileCheck().getClass().getSimpleName(),
                        getFileCheck().forFile().getAbsolutePath(), getLineNumber(), Arrays.toString( getRow() ),
                        columnString, message ) );
            }
        }
    }

    public static class LoggingFailedFileCheckPolicy extends FailedFileCheckPolicy
    {
        private final Logger consoleLogger;
        private final Logger csvFileLogger;

        public LoggingFailedFileCheckPolicy( Logger consoleLogger, Logger csvFileLogger )
        {
            this.consoleLogger = consoleLogger;
            this.csvFileLogger = csvFileLogger;
        }

        @Override
        public void handleFailedLineCheck( FileCheck fileCheck, String message, long lineNumber, String[] row )
                throws FileCheckException
        {
            if ( null != consoleLogger )
            {
                consoleLogger.error( String.format( "FileCheck[%s] File[%s] Line[%s] Row[%s] Message[%s]",
                        fileCheck.getClass().getSimpleName(), fileCheck.forFile(), lineNumber, Arrays.toString( row ),
                        message ) );
            }
            if ( null != csvFileLogger )
            {
                // Check;File;Line;Row;Column;Message
                csvFileLogger.error( String.format( "%s;%s;%s;%s;;%s", fileCheck.getClass().getSimpleName(),
                        fileCheck.forFile(), lineNumber, Arrays.toString( row ), message ) );
            }
        }

        @Override
        public void handleFailedFileCheck( FileCheck fileCheck, String message ) throws FileCheckException
        {
            if ( null != consoleLogger )
            {
                consoleLogger.error( String.format( "FileCheck[%s] File[%s] Message[%s]",
                        fileCheck.getClass().getSimpleName(), fileCheck.forFile(), message ) );
            }
            if ( null != csvFileLogger )
            {
                // Check;File;Line;Row;Column;Message
                consoleLogger.error( String.format( "%s;%s;;;;%s", fileCheck.getClass().getSimpleName(),
                        fileCheck.forFile(), message ) );
            }
        }
    }

    public static class LoggingFailedDirectoryCheckPolicy extends FailedDirectoryCheckPolicy
    {
        private final Logger consoleLogger;
        private final Logger csvFileLogger;

        public LoggingFailedDirectoryCheckPolicy( Logger consoleLogger, Logger csvFileLogger )
        {
            this.consoleLogger = consoleLogger;
            this.csvFileLogger = csvFileLogger;
        }

        public void handleFailedDirectoryCheck( DirectoryCheck directoryCheck, File directory, String message )
                throws DirectoryCheckException
        {
            if ( null != consoleLogger )
            {
                consoleLogger.error( String.format( "DirectoryCheck[%s] Directory[%s] Message[%s]",
                        directoryCheck.getClass().getSimpleName(), directory.getAbsolutePath(), message ) );
            }
            if ( null != csvFileLogger )
            {
                // Check;File;Line;Row;Column;Message
                consoleLogger.error( String.format( "%s;%s;;;;%s", directoryCheck.getClass().getSimpleName(),
                        directory.getAbsolutePath(), message ) );
            }
        }
    }
}
