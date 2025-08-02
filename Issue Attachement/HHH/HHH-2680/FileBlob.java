package com.texunatech.sql;

import java.io.*;
import java.sql.Blob;
import java.sql.SQLException;

public class FileBlob implements Blob {
    private File file;

    public FileBlob(File file) {
        this.file = file;
    }

    @Override
    public long length() throws SQLException {
        return file.length();
    }

    @Override
    public byte[] getBytes(long pos, int length) throws SQLException {
        throw new UnsupportedOperationException("This method is unsupported yet");
    }

    @Override
    public InputStream getBinaryStream() throws SQLException {
        try {
            return new BufferedInputStream(new FileInputStream(file));
        } catch (FileNotFoundException e) {
            throw new SQLException("Can't read from file " + file, e);
        }
    }

    @Override
    public long position(byte[] pattern, long start) throws SQLException {
        throw new UnsupportedOperationException("This method is unsupported yet");
    }

    @Override
    public long position(Blob pattern, long start) throws SQLException {
        throw new UnsupportedOperationException("This method is unsupported yet");
    }

    @Override
    public int setBytes(long pos, byte[] bytes) throws SQLException {
        throw new UnsupportedOperationException("This method is unsupported yet");
    }

    @Override
    public int setBytes(long pos, byte[] bytes, int offset, int len) throws SQLException {
        throw new UnsupportedOperationException("This method is unsupported yet");
    }

    @Override
    public OutputStream setBinaryStream(long pos) throws SQLException {
        throw new UnsupportedOperationException("This method is unsupported yet");
    }

    @Override
    public void truncate(long len) throws SQLException {
        throw new UnsupportedOperationException("This method is unsupported yet");
    }

    @Override
    public void free() throws SQLException {
        file.delete();
    }

    @Override
    public InputStream getBinaryStream(long pos, long length) throws SQLException {
        throw new UnsupportedOperationException("This method is unsupported yet");
    }
}
