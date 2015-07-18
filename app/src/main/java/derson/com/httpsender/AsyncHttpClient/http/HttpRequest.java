package derson.com.httpsender.AsyncHttpClient.http;

import android.content.Context;

import com.hupu.android.cache.CacheParams;
import com.hupu.android.global.HPHttpFactory;
import com.hupu.android.net.AsyncHttpClient.RequestParams;

/**
 * 请求接口
 * 
 * @author yangzhi
 *
 * modify by chengli 07/15/2015
 */
public interface HttpRequest {
	/**
	 * HTTP异步post请求 异步解析返回结果
	 * @param reqType
	 * @param httpRes
	 * @param url
	 * @param params
	 * @param callback
	 * @param clientHeaderMap
	 * @param contentType
	 */
	public RequestHandle doPost(Context context, int reqType, HttpFactory httpRes, String url,
                                  RequestParams params, HttpCallback callback);

    /**
     * HTTP异步get请求 同步解析返回结果
     * @param context
     * @param reqType
     * @param url
     * @param params
     * @param callback
     * @return
     */
    public RequestHandle doPostSync(Context context, int reqType, String url, RequestParams params, HttpCallback callback);
	
	
	/**
	 * HTTP异步get请求 异步解析返回结果
	 * @param reqType
	 * @param httpRes
	 * @param url
	 * @param params
	 * @param callback
	 * @param clientHeaderMap
	 * @param cacheParams
	 */
	public RequestHandle doGet(Context context, int reqType, HttpFactory httpRes, String url,
                                 RequestParams params, HttpCallback callback,
                                 CacheParams cacheParams);

    /**
     * HTTP异步get请求 同步解析返回结果
     * @param context
     * @param reqType
     * @param url
     * @param params
     * @param callback
     * @param cacheParams
     * @return
     */
    public RequestHandle doGetSync(Context context, int reqType, String url, RequestParams params, HttpCallback callback, CacheParams cacheParams);
	
	
	public HttpClient getHttpClient();

}
