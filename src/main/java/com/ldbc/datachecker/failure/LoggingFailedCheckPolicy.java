package com.ldbc.datachecker.failure;

import java.io.File;
import java.util.Arrays;

import org.apache.log4j.Logger;

import au.com.bytecode.opencsv.CSVWriter;

import com.ldbc.datachecker.ColumnCheckException;
import com.ldbc.datachecker.DirectoryCheck;
import com.ldbc.datachecker.DirectoryCheckException;
import com.ldbc.datachecker.FailedCheckPolicy;
import com.ldbc.datachecker.FileCheck;
import com.ldbc.datachecker.FileCheckException;

public class LoggingFailedCheckPolicy implements FailedCheckPolicy
{
    private final Logger consoleLogger;
    private final CSVWriter csvWriter;

    public static LoggingFailedCheckPolicy toConsoleAndFile( Logger consoleLogger, CSVWriter csvWriter )
    {
        return new LoggingFailedCheckPolicy( consoleLogger, csvWriter );
    }

    public static LoggingFailedCheckPolicy toFileOnly( CSVWriter csvWriter )
    {
        return new LoggingFailedCheckPolicy( null, csvWriter );
    }

    public static LoggingFailedCheckPolicy toConsoleOnly( Logger consoleLogger )
    {
        return new LoggingFailedCheckPolicy( consoleLogger, null );
    }

    private LoggingFailedCheckPolicy( Logger consoleLogger, CSVWriter csvWriter )
    {
        this.consoleLogger = consoleLogger;
        this.csvWriter = csvWriter;
        if ( null != csvWriter )
        {
            this.csvWriter.writeNext( "Check;File;Line;Row;Column;Message".split( ";" ) );
        }
    }

    @Override
    public FailedColumnCheckPolicy getFailedColumnCheckPolicy( FileCheck fileCheck, long lineNumber, String[] row )
    {
        return new LoggingFailedColumnCheckPolicy( consoleLogger, csvWriter, fileCheck, lineNumber, row );
    }

    @Override
    public FailedFileCheckPolicy getFailedFileCheckPolicy()
    {
        return new LoggingFailedFileCheckPolicy( consoleLogger, csvWriter );
    }

    @Override
    public FailedDirectoryCheckPolicy getFailedDirectoryCheckPolicy()
    {
        return new LoggingFailedDirectoryCheckPolicy( consoleLogger, csvWriter );
    }

    public static class LoggingFailedColumnCheckPolicy extends FailedColumnCheckPolicy
    {
        private final Logger consoleLogger;
        private final CSVWriter csvWriter;

        public LoggingFailedColumnCheckPolicy( Logger consoleLogger, CSVWriter csvWriter, FileCheck fileCheck,
                long lineNumber, String[] row )
        {
            super( fileCheck, lineNumber, row );
            this.consoleLogger = consoleLogger;
            this.csvWriter = csvWriter;
        }

        public void handleFailedColumnCheck( String columnString, String message ) throws ColumnCheckException
        {
            if ( null != consoleLogger )
            {
                consoleLogger.error( String.format( "ColumnCheck[%s] File[%s] Line[%s] Row[%s] Column[%s] Message[%s]",
                        getFileCheck().getClass().getSimpleName(), getFileCheck().forFile().getAbsolutePath(),
                        getLineNumber(), Arrays.toString( getRow() ), columnString, message ) );
            }
            if ( null != csvWriter )
            {
                String[] nextLine = new String[] { getFileCheck().getClass().getSimpleName(),
                        getFileCheck().forFile().getAbsolutePath(), Long.toString( getLineNumber() ),
                        Arrays.toString( getRow() ), columnString, message };
                csvWriter.writeNext( nextLine );
            }
        }
    }

    public static class LoggingFailedFileCheckPolicy extends FailedFileCheckPolicy
    {
        private final Logger consoleLogger;
        private final CSVWriter csvWriter;

        public LoggingFailedFileCheckPolicy( Logger consoleLogger, CSVWriter csvWriter )
        {
            this.consoleLogger = consoleLogger;
            this.csvWriter = csvWriter;
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
            if ( null != csvWriter )
            {
                // Check;File;Line;Row;Column;Message
                String[] nextLine = new String[] { fileCheck.getClass().getSimpleName(), fileCheck.forFile().getName(),
                        Long.toString( lineNumber ), Arrays.toString( row ), message };
                csvWriter.writeNext( nextLine );

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
            if ( null != csvWriter )
            {
                // Check;File;Line;Row;Column;Message
                String[] nextLine = new String[] { fileCheck.getClass().getSimpleName(), fileCheck.forFile().getName(),
                        "", "", "", message };
                csvWriter.writeNext( nextLine );
            }
        }
    }

    public static class LoggingFailedDirectoryCheckPolicy extends FailedDirectoryCheckPolicy
    {
        private final Logger consoleLogger;
        private final CSVWriter csvWriter;

        public LoggingFailedDirectoryCheckPolicy( Logger consoleLogger, CSVWriter csvWriter )
        {
            this.consoleLogger = consoleLogger;
            this.csvWriter = csvWriter;
        }

        public void handleFailedDirectoryCheck( DirectoryCheck directoryCheck, File directory, String message )
                throws DirectoryCheckException
        {
            if ( null != consoleLogger )
            {
                consoleLogger.error( String.format( "DirectoryCheck[%s] Directory[%s] Message[%s]",
                        directoryCheck.getClass().getSimpleName(), directory.getAbsolutePath(), message ) );
            }
            if ( null != csvWriter )
            {
                // Check;File;Line;Row;Column;Message
                String[] nextLine = new String[] { directoryCheck.getClass().getSimpleName(),
                        directory.getAbsolutePath(), "", "", "", message };
                csvWriter.writeNext( nextLine );
            }
        }
    }
}
