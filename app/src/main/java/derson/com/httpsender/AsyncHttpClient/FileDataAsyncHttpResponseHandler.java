package derson.com.httpsender.AsyncHttpClient;

import android.content.Context;
import android.os.Message;
import android.util.Log;

import org.apache.http.Header;
import org.apache.http.HttpEntity;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;

public abstract class FileDataAsyncHttpResponseHandler extends
		AsyncHttpResponseHandler {
	protected final File mFile;
	protected final boolean append;
	private static final String LOG_TAG = "FileDataAsyncHttpResponseHandler";

	protected static final int PROGRESS_DATA_MESSAGE = 6;

	/**
	 * Obtains new FileAsyncHttpResponseHandler and stores response in passed
	 * file
	 * 
	 * @param file
	 *            File to store response within, must not be null
	 */
	public FileDataAsyncHttpResponseHandler(File file) {
		this(file, false);
	}

	/**
	 * Obtains new FileAsyncHttpResponseHandler and stores response in passed
	 * file
	 * 
	 * @param file
	 *            File to store response within, must not be null
	 * @param append
	 *            whether data should be appended to existing file
	 */
	public FileDataAsyncHttpResponseHandler(File file, boolean append) {
		super();
		AssertUtils
				.asserts(file != null,
						"File passed into FileAsyncHttpResponseHandler constructor must not be null");
		this.mFile = file;
		this.append = append;
	}

	/**
	 * Fired when the request progress, override to handle in your own code
	 * 
	 * @param responseBody
	 *            response body received so far
	 */
	public void onProgressData(byte[] responseBody) {
		Log.d(LOG_TAG,
				"onProgressData(byte[]) was not overriden, but callback was received");
	}

	final public void sendProgressDataMessage(byte[] responseBytes) {
		sendMessage(obtainMessage(PROGRESS_DATA_MESSAGE,
				new Object[] { responseBytes }));
	}

	// Methods which emulate android's Handler and Message methods
	@Override
	protected void handleMessage(Message message) {
		super.handleMessage(message);
		Object[] response;

		switch (message.what) {
		case PROGRESS_DATA_MESSAGE:
			response = (Object[]) message.obj;
			if (response != null && response.length >= 1) {
				try {
					onProgressData((byte[]) response[0]);
				} catch (Throwable t) {
					Log.e(LOG_TAG, "custom onProgressData contains an error", t);
				}
			} else {
				Log.e(LOG_TAG, "PROGRESS_DATA_MESSAGE didn't got enough params");
			}
			break;
		}
	}

	/**
	 * Obtains new FileAsyncHttpResponseHandler against context with target
	 * being temporary file
	 * 
	 * @param context
	 *            Context, must not be null
	 */
	public FileDataAsyncHttpResponseHandler(Context context) {
		super();
		this.mFile = getTemporaryFile(context);
		this.append = false;
	}

	/**
	 * Attempts to delete file with stored response
	 * 
	 * @return false if the file does not exist or is null, true if it was
	 *         successfully deleted
	 */
	public boolean deleteTargetFile() {
		return getTargetFile() != null && getTargetFile().delete();
	}

	/**
	 * Used when there is no file to be used when calling constructor
	 * 
	 * @param context
	 *            Context, must not be null
	 * @return temporary file or null if creating file failed
	 */
	protected File getTemporaryFile(Context context) {
		AssertUtils.asserts(context != null,
				"Tried creating temporary file without having Context");
		try {
			// not effective in release mode
			assert context != null;
			return File.createTempFile("temp_", "_handled",
					context.getCacheDir());
		} catch (IOException e) {
			Log.e(LOG_TAG, "Cannot create temporary file", e);
		}
		return null;
	}

	/**
	 * Retrieves File object in which the response is stored
	 * 
	 * @return File file in which the response is stored
	 */
	protected File getTargetFile() {
		assert (mFile != null);
		return mFile;
	}

	@Override
	public final void onFailure(int statusCode, Header[] headers,
			byte[] responseBytes, Throwable throwable) {
		onFailure(statusCode, headers, throwable, getTargetFile());
	}

	/**
	 * Method to be overriden, receives as much of file as possible Called when
	 * the file is considered failure or if there is error when retrieving file
	 * 
	 * @param statusCode
	 *            http file status line
	 * @param headers
	 *            file http headers if any
	 * @param throwable
	 *            returned throwable
	 * @param file
	 *            file in which the file is stored
	 */
	public abstract void onFailure(int statusCode, Header[] headers,
			Throwable throwable, File file);

	@Override
	public final void onSuccess(int statusCode, Header[] headers,
			byte[] responseBytes) {
		onSuccess(statusCode, headers, getTargetFile());
	}

	/**
	 * Method to be overriden, receives as much of response as possible
	 * 
	 * @param statusCode
	 *            http response status line
	 * @param headers
	 *            response http headers if any
	 * @param file
	 *            file in which the response is stored
	 */
	public abstract void onSuccess(int statusCode, Header[] headers, File file);

	@Override
	protected byte[] getResponseData(HttpEntity entity) throws IOException {
		if (entity != null) {
			InputStream instream = entity.getContent();
			long contentLength = entity.getContentLength();
			FileOutputStream buffer = new FileOutputStream(getTargetFile(),
					this.append);
			if (instream != null) {
				try {
					byte[] tmp = new byte[BUFFER_SIZE];
					int l, count = 0;
					// do not send messages if request has been cancelled
					while ((l = instream.read(tmp)) != -1
							&& !Thread.currentThread().isInterrupted()) {
						count += l;
						buffer.write(tmp, 0, l);
						sendProgressDataMessage(copyOfRange(tmp, 0, l));
						sendProgressMessage(count, (int) contentLength);
					}
				} finally {
					AsyncHttpClient.silentCloseInputStream(instream);
					buffer.flush();
					AsyncHttpClient.silentCloseOutputStream(buffer);
				}
			}
		}
		return null;
	}
	
	
	/**
     * Copies elements from {@code original} into a new array, from indexes start (inclusive) to end
     * (exclusive). The original order of elements is preserved. If {@code end} is greater than
     * {@code original.length}, the result is padded with the value {@code (byte) 0}.
     *
     * @param original the original array
     * @param start    the start index, inclusive
     * @param end      the end index, exclusive
     * @return the new array
     * @throws ArrayIndexOutOfBoundsException if {@code start < 0 || start > original.length}
     * @throws IllegalArgumentException       if {@code start > end}
     * @throws NullPointerException           if {@code original == null}
     * @see java.util.Arrays
     * @since 1.6
     */
    public static byte[] copyOfRange(byte[] original, int start, int end) throws ArrayIndexOutOfBoundsException, IllegalArgumentException, NullPointerException {
        if (start > end) {
            throw new IllegalArgumentException();
        }
        int originalLength = original.length;
        if (start < 0 || start > originalLength) {
            throw new ArrayIndexOutOfBoundsException();
        }
        int resultLength = end - start;
        int copyLength = Math.min(resultLength, originalLength - start);
        byte[] result = new byte[resultLength];
        System.arraycopy(original, start, result, 0, copyLength);
        return result;
    }

}
