package com.exactprosystems.ticker.web;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.nio.ByteBuffer;

public class ByteBufferOutputStream extends OutputStream {

	ByteArrayOutputStream buf;

    public ByteBufferOutputStream() {
        this.buf = new ByteArrayOutputStream();
    }

    public void write(int b) throws IOException {
        buf.write((byte) b);
    }

    public void write(byte[] bytes, int off, int len)
            throws IOException {
        buf.write(bytes, off, len);
    }

	public ByteBuffer toByteBuffer() {
		return ByteBuffer.wrap(buf.toByteArray());
	}
	
}
