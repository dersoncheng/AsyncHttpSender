package derson.com.httpsender.AsyncHttpClient.http;

import org.apache.http.Header;

import java.io.File;

public interface DownloadCallback extends HttpCallback {
	
	/**
	 * 下载失败
	 * @param statusCode
	 * @param headers
	 * @param throwable
	 * @param file
	 */
	public void onFailure(int statusCode, Header[] headers,
                          Throwable throwable, File file);
	
	
	/**
	 * 当进度改变
	 */
	@Override
	public void onProgressChanged(long current, long total);
	
	/**
	 * 下载成功
	 * @param statusCode
	 * @param headers
	 * @param file
	 */
	public void onSuccess(int statusCode, Header[] headers, File file);
	
}
