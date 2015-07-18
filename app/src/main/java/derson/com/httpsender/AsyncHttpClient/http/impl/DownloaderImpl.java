package derson.com.httpsender.AsyncHttpClient.http.impl;

import android.content.Context;

import com.hupu.android.net.AsyncHttpClient.AsyncHttpClient;
import com.hupu.android.net.AsyncHttpClient.FileAsyncHttpResponseHandler;
import com.hupu.android.net.http.HPDownloadCallback;
import com.hupu.android.net.http.HPDownloader;
import com.hupu.android.net.http.HPRequestHandle;

import org.apache.http.Header;

import java.io.File;

/**
 * 默认下载器(AsyncHttpClient实现)
 * 
 * @author yangzhi
 * 
 */
public class DownloaderImpl implements HPDownloader {
	private static DownloaderImpl instance;

	public static DownloaderImpl getInstance() {
		if (instance == null) {
			synchronized (DownloaderImpl.class) {
				if (instance == null) {
					instance = new DownloaderImpl();
				}
			}
		}
		return instance;
	}

	private DownloaderImpl() {
	}

	@Override
	public HPRequestHandle download(Context context, String url, File file,
			final HPDownloadCallback callback, boolean isAppend) {

		return new AsyncHttpHandle(AsyncHttpClient.getInstance().get(context,
				url, new FileAsyncHttpResponseHandler(file, isAppend) {
					
					@Override
					public void onCancel() {

						super.onCancel();
						callback.onCancel(0);
					}
					
					@Override
					public void onStart() {

						super.onStart();
						callback.onStart(0);
					}
					
					@Override
					public void onFinish() {

						super.onFinish();
						callback.onFinish(0);
					}
					
					@Override
					public void onRetry(int retryNo) {

						super.onRetry(retryNo);
						callback.onRetry(0, retryNo);
					}
			
					@Override
					public void onProgress(int bytesWritten, int totalSize) {

						super.onProgress(bytesWritten, totalSize);
							callback.onProgressChanged(bytesWritten, totalSize);
					}

					@Override
					public void onSuccess(int statusCode, Header[] headers,
							File file) {

							callback.onSuccess(statusCode, headers, file);
					}

					@Override
					public void onFailure(int statusCode, Header[] headers,
							Throwable throwable, File file) {

							callback.onFailure(statusCode, headers, throwable, file);
					}
				}));

	}

	@Override
	public boolean allowCache() {

		return false;
	}

	@Override
	public boolean canDownloadUrl(String url) {

		return true;
	}

}
