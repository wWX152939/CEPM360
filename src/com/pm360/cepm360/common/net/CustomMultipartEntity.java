package com.pm360.cepm360.common.net;

import org.apache.http.entity.mime.HttpMultipartMode;
import org.apache.http.entity.mime.MultipartEntity;
import org.apache.http.protocol.HTTP;

import java.io.FilterOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.nio.charset.Charset;

@SuppressWarnings("deprecation")
public class CustomMultipartEntity extends MultipartEntity {	
	public static final String mBoundary = "-------------" + System.currentTimeMillis();
	public static final Charset mEncoding = Charset.forName(HTTP.UTF_8);
	public static final HttpMultipartMode mMode = HttpMultipartMode.BROWSER_COMPATIBLE;
	
	private final ProgressListener mListener;

	public CustomMultipartEntity(final ProgressListener listener) {
		super(mMode, mBoundary, mEncoding);
		mListener = listener;
	}
	
	public CustomMultipartEntity(final HttpMultipartMode mode, final ProgressListener listener) {  
        super(mode, mBoundary, mEncoding);  
        this.mListener = listener;
    }
	
	public CustomMultipartEntity(HttpMultipartMode mode, 
			final String boundary, 
			final ProgressListener listener) {  
		super(mode, boundary, mEncoding);
		this.mListener = listener;
	}
   
    public CustomMultipartEntity(HttpMultipartMode mode, 
    							final String boundary, 
    							final Charset charset, 
    							final ProgressListener listener) {  
        super(mode, boundary, charset);
        this.mListener = listener;
    }

	public static interface ProgressListener {
		void transferred(long num);
	}

	@Override
	public void writeTo(OutputStream outstream) throws IOException {
		super.writeTo(new CountingOutputStream(outstream, this.mListener));
	}

	public static class CountingOutputStream extends FilterOutputStream {

		private final ProgressListener listener;
		private long transferred;

		public CountingOutputStream(final OutputStream out,
				final ProgressListener listener) {
			super(out);
			this.listener = listener;
			this.transferred = 0;
		}

		public void write(byte[] b, int off, int len) throws IOException {
			out.write(b, off, len);
			this.transferred += len;
			this.listener.transferred(this.transferred);
		}

		public void write(int b) throws IOException {
			out.write(b);
			this.transferred++;
			this.listener.transferred(this.transferred);
		}
	}
}
