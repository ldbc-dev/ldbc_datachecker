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
    private final Logger logger;

    public LoggingFailedCheckPolicy( Logger logger )
    {
        this.logger = logger;
    }

    @Override
    public FailedColumnCheckPolicy getFailedColumnCheckPolicy( FileCheck fileCheck, long lineNumber, String[] row )
    {
        return new LoggingFailedColumnCheckPolicy( logger, fileCheck, lineNumber, row );
    }

    @Override
    public FailedFileCheckPolicy getFailedFileCheckPolicy()
    {
        return new LoggingFailedFileCheckPolicy( logger );
    }

    @Override
    public FailedDirectoryCheckPolicy getFailedDirectoryCheckPolicy()
    {
        return new LoggingFailedDirectoryCheckPolicy( logger );
    }

    public static class LoggingFailedColumnCheckPolicy extends FailedColumnCheckPolicy
    {
        private final Logger logger;

        public LoggingFailedColumnCheckPolicy( Logger logger, FileCheck fileCheck, long lineNumber, String[] row )
        {
            super( fileCheck, lineNumber, row );
            this.logger = logger;
        }

        public void handleFailedColumnCheck( String columnString, String message ) throws ColumnCheckException
        {
            logger.error( String.format( "Check[%s] File[%s] Line[%s] Row[%s] Column[%s] Message[%s]",
                    getFileCheck().getClass().getSimpleName(), getFileCheck().forFile().getAbsolutePath(),
                    getLineNumber(), Arrays.toString( getRow() ), columnString, message ) );
        }
    }

    public static class LoggingFailedFileCheckPolicy extends FailedFileCheckPolicy
    {
        private final Logger logger;

        public LoggingFailedFileCheckPolicy( Logger logger )
        {
            this.logger = logger;
        }

        @Override
        public void handleFailedLineCheck( FileCheck fileCheck, String message, long lineNumber, String[] line )
                throws FileCheckException
        {
            logger.error( String.format( "Check[%s] File[%s] Line[%s] Content[%s] Message[%s]",
                    fileCheck.getClass().getSimpleName(), fileCheck.forFile(), lineNumber, Arrays.toString( line ),
                    message ) );
        }

        @Override
        public void handleFailedFileCheck( FileCheck fileCheck, String message ) throws FileCheckException
        {
            logger.error( String.format( "Check[%s] File[%s] Message[%s]", fileCheck.getClass().getSimpleName(),
                    fileCheck.forFile(), message ) );
        }
    }

    public static class LoggingFailedDirectoryCheckPolicy extends FailedDirectoryCheckPolicy
    {
        private final Logger logger;

        public LoggingFailedDirectoryCheckPolicy( Logger logger )
        {
            this.logger = logger;
        }

        public void handleFailedDirectoryCheck( DirectoryCheck directoryCheck, File directory, String message )
                throws DirectoryCheckException
        {
            logger.error( String.format( "Check[%s] Directory[%s] Message[%s]",
                    directoryCheck.getClass().getSimpleName(), directory.getAbsolutePath(), message ) );
        }
    }
}
