package derson.com.httpsender.AsyncHttpClient.http;

import android.content.Context;

import java.io.File;

/**
 * 下载器接口
 * 
 * @author yangzhi
 * 
 */
public interface Downloader {
	
	/**
	 * 下载文件接口
	 * @param context
	 * @param url 
	 * @param filename 下载后保存的文件
	 * @param callback
	 * @param isAppend 是否断点续传
	 */
	public RequestHandle download(Context context, String url, File file,
                                    DownloadCallback callback, boolean isAppend);
	
	/**
	 * 设置是否缓存接口
	 * @return
	 */
	public boolean allowCache();
	
	/**
	 * 判断URL是否可下载接口
	 * @param url
	 * @return
	 */
	public boolean canDownloadUrl(String url);
}
