package derson.com.httpsender.AsyncHttpClient.http.impl;

import android.content.Context;

import com.hupu.android.cache.CacheParams;
import com.hupu.android.global.HPHttpFactory;
import com.hupu.android.net.AsyncHttpClient.RequestParams;
import com.hupu.android.net.http.HPHttpCallback;
import com.hupu.android.net.http.HPHttpClient;
import com.hupu.android.net.http.HPHttpRequest;
import com.hupu.android.net.http.HPRequestHandle;

/**
 * AsyncHttpClient框架的request实现类
 * 
 * @author hupu
 * 
 */
public class AsyncHttpRequestImpl implements HPHttpRequest {
	protected HPHttpClient hupuHttpClient;
	private static volatile AsyncHttpRequestImpl instance;

	private AsyncHttpRequestImpl() {
		hupuHttpClient = AsyncHttpClientImpl.getHupuHttpClient();
	}

	/**
	 * 获取实例
	 * 
	 * @param context
	 *            applicationContext
	 * @return
	 */
	public static AsyncHttpRequestImpl getInstance() {
		if (instance == null)
			synchronized (AsyncHttpRequestImpl.class) {
				if (instance == null) {
					instance = new AsyncHttpRequestImpl();
				}
			}
		return instance;
	}

	@Override
	public HPRequestHandle doPost(Context context, int reqType, HPHttpFactory httpRes,
			String url, RequestParams params, HPHttpCallback callback) {

		return hupuHttpClient.post(reqType, httpRes, context, url, params, callback);
	}

    @Override
    public HPRequestHandle doPostSync(Context context, int reqType, String url, RequestParams params, HPHttpCallback callback) {
        return hupuHttpClient.postSync(reqType, context, url, params, callback);
    }

    @Override
	public HPRequestHandle doGet(Context context, int reqType, HPHttpFactory httpRes,
			String url, RequestParams params, HPHttpCallback callback, CacheParams cacheParams) {

		return hupuHttpClient.get(reqType, httpRes, context, url, params,
				cacheParams, callback);
	}

    @Override
    public HPRequestHandle doGetSync(Context context, int reqType, String url, RequestParams params, HPHttpCallback callback, CacheParams cacheParams) {
        return hupuHttpClient.getSync(reqType, context, url, params, cacheParams, callback);
    }

    @Override
	public HPHttpClient getHttpClient() {

		return hupuHttpClient;
	}

}
