package derson.com.httpsender.AsyncHttpClient.http;

import org.apache.http.Header;

/**
 * 请求callback;
 * @author yangzhi
 *
 */
public interface HttpCallback {
	/**
	 * 请求成功
	 * @param content   返回值 
	 * @param object    返回的转化对象
	 * @param reqType   请求的唯一识别
	 * @param isCache 是否来自缓存
	 */
	public void onSuccess(String content, Object object, int reqType, boolean isCache);
	
	
	/**
	 * 当解析成功后（在子线程）
	 * @param content
	 * @param object
	 */
	public Object onParserCompleted(String content, Object object, int reqType, boolean isCache);
	
	/**
	 * 请求成功
	 * @param content   返回值 
	 * @param object    返回的转化对象
	 * @param reqType   请求的唯一识别
	 * @param statusCode 返回的状态码
	 * @param headers 返回的headers
	 * @param responseBody 返回原字节数组
	 * @param isCache 是否来自缓存
	 */
	public void onSuccess(String content, Object object, int reqType, int statusCode, Header[] headers, byte[] responseBody, boolean isCache);
	
	/**
	 * 请求失败
	 * @param error    错误
	 * @param content   返回值
	 * @param reqType  请求的唯一识别
	 */
	public void onFailure(Throwable error, String content, int reqType);
	
	/**
	* 请求失败
	 * @param error    错误
	 * @param content   返回值
	 * @param reqType  请求的唯一识别
	 * @param statusCode 返回的错误码
	 * @param headers 返回头
	 * @param responseBody 返回的原字节数组
	 */
	public void onFailure(Throwable error, String content, int reqType, int statusCode, Header[] headers,
                          byte[] responseBody);
	
	/**
	 * 当重试请求
	 * @param reqType
	 * @param retryCount
	 */
	public void onRetry(int reqType, int retryCount);
	
	/**
	 * 当请求开始
	 * @param reqType
	 */
	public void onStart(int reqType);
	
	/**
	 * 当请求完成
	 * @param reqType
	 */
	public void onFinish(int reqType);
	
	/**
	 * 当请求进度改变
	 * @param current
	 * @param total
	 */
	public void onProgressChanged(long current, long total);
	
	/**
	 * 当请求被取消
	 * @param reqType
	 */
	public void onCancel(int reqType);
}
