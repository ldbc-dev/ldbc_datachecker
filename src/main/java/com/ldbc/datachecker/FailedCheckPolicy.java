package com.ldbc.datachecker;

import java.io.File;

public interface FailedCheckPolicy
{
    public FailedColumnCheckPolicy getFailedColumnCheckPolicy( FileCheck fileCheck, long lineNumber, String[] row );

    public FailedFileCheckPolicy getFailedFileCheckPolicy();

    public FailedDirectoryCheckPolicy getFailedDirectoryCheckPolicy();

    public abstract static class FailedColumnCheckPolicy
    {
        private final FileCheck fileCheck;
        private final long lineNumber;
        private final String[] row;

        public FailedColumnCheckPolicy( FileCheck fileCheck, long lineNumber, String[] row )
        {
            this.fileCheck = fileCheck;
            this.lineNumber = lineNumber;
            this.row = row;
        }

        protected final FileCheck getFileCheck()
        {
            return fileCheck;
        }

        protected final long getLineNumber()
        {
            return lineNumber;
        }

        protected final String[] getRow()
        {
            return row;
        }

        public abstract void handleFailedColumnCheck( String columnString, String message ) throws ColumnCheckException;
    }

    public abstract static class FailedFileCheckPolicy
    {
        public abstract void handleFailedLineCheck( FileCheck fileCheck, String message, long lineNumber, String[] row )
                throws FileCheckException;

        public abstract void handleFailedFileCheck( FileCheck fileCheck, String message ) throws FileCheckException;
    }

    public abstract static class FailedDirectoryCheckPolicy
    {
        public abstract void handleFailedDirectoryCheck( DirectoryCheck directoryCheck, File directory, String message )
                throws DirectoryCheckException;
    }
}
