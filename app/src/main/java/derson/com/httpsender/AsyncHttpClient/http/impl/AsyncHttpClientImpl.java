package derson.com.httpsender.AsyncHttpClient.http.impl;

import android.content.Context;

import com.hupu.android.cache.CacheManager;
import com.hupu.android.cache.CacheParams;
import com.hupu.android.global.HPHttpFactory;
import com.hupu.android.global.SenderTaskHelper;
import com.hupu.android.net.AsyncHttpClient.AsyncHttpClient;
import com.hupu.android.net.AsyncHttpClient.RequestParams;
import com.hupu.android.net.http.HPHttpCallback;
import com.hupu.android.net.http.HPHttpClient;
import com.hupu.android.net.http.HPRequestHandle;
import com.hupu.android.task.HPTask;
import com.hupu.android.util.HPLog;

import org.apache.http.HttpRequestInterceptor;
import org.apache.http.HttpResponseInterceptor;
import org.apache.http.client.CookieStore;
import org.apache.http.client.RedirectHandler;
import org.apache.http.client.protocol.ClientContext;
import org.apache.http.conn.ssl.SSLSocketFactory;
import org.apache.http.impl.client.DefaultHttpClient;

import java.util.concurrent.ExecutorService;

/**
 * AsyncHttpClient 请求客户端实现对象
 * 
 * @author yangzhi
 * 
 */
public class AsyncHttpClientImpl implements HPHttpClient {
	private AsyncHttpClient client;
	private static volatile AsyncHttpClientImpl instance;

	public static HPHttpClient getHupuHttpClient() {
		if (instance == null)
			synchronized (AsyncHttpClientImpl.class) {
				if (instance == null) {
					instance = new AsyncHttpClientImpl();
				}
			}
		return instance;
	}

	private AsyncHttpClientImpl() {
		client = AsyncHttpClient.getInstance();
		client.setThreadPool(HPTask.THREAD_POOL_EXECUTOR);
	}

    @Override
    public void updateUserAgent(int networkType) {
        client.updateUserAgent(networkType);
    }

    @Override
    public void setWangsu() {
       client.setWangsu();
    }

    @Override
	public HPRequestHandle post(int reqType, HPHttpFactory httpRes,
			Context context, String url, RequestParams params,
			HPHttpCallback callback) {

		HPRequestHandle requestHandle = new AsyncHttpHandle(client.post(
				context, url, params, new AsyncHttpHandler.Builder(callback,
						reqType, httpRes).create()));
		return requestHandle;
	}

    @Override
    public HPRequestHandle postSync(int reqType, Context context, String url, RequestParams params, HPHttpCallback callback) {
        HPRequestHandle requestHandle = new AsyncHttpHandle(client.post(
                context, url, params, new SyncHttpHandler.Builder(callback,
                        reqType).create()));
        return requestHandle;
    }

	@Override
	public HPRequestHandle get(int reqType, HPHttpFactory httpRes,
			Context context, String url, RequestParams params,
			CacheParams cacheParams, HPHttpCallback callback) {

		String token = SenderTaskHelper.generateGetQueryUrlString(url, params,
				null);
		if (null != cacheParams) {
			cacheParams.setCacheUrl(token);
		}
		boolean onlyUseCache = CacheManager.onlyUseCache(reqType, httpRes,
				callback, cacheParams);
		HPLog.d("DEBUG",
				"IT IS  HERE-------------------------------onlyUseCache----"
						+ onlyUseCache);
		if (onlyUseCache) {
			HPLog.d("DEBUG", "IT IS  HERE-----------------------------------"
					+ cacheParams.getCacheType());
			return null;
		}
		HPRequestHandle requestHandle = new AsyncHttpHandle(client.get(context,
				url, params,new AsyncHttpHandler.Builder(callback, reqType, httpRes)
						.setCacheParams(cacheParams).create()));
//		SenderTaskHelper.addRequestHandle(token, requestHandle);
		return requestHandle;
	}

    @Override
    public HPRequestHandle getSync(int reqType, Context context, String url, RequestParams params, CacheParams cacheParams, HPHttpCallback callback) {
        String token = SenderTaskHelper.generateGetQueryUrlString(url, params,
                null);
        if (null != cacheParams) {
            cacheParams.setCacheUrl(token);
        }
        boolean onlyUseCache = CacheManager.onlyUseCacheSync(reqType,
                callback, cacheParams);
        HPLog.d("DEBUG",
                "IT IS  HERE-------------------------------onlyUseCache----"
                        + onlyUseCache);
        if (onlyUseCache) {
            HPLog.d("DEBUG", "IT IS  HERE-----------------------------------"
                    + cacheParams.getCacheType());
            return null;
        }
        HPRequestHandle requestHandle = new AsyncHttpHandle(client.get(context,
                url, params,new SyncHttpHandler.Builder(callback, reqType)
                        .setCacheParams(cacheParams).create()));
        return requestHandle;
    }

	@Override
	public void setCookieStore(CookieStore cookieStore) {

		client.setCookieStore(cookieStore);
	}

	@Override
	public void setThreadPool(ExecutorService threadPool) {

		client.setThreadPool(threadPool);
	}

	@Override
	public ExecutorService getThreadPool() {

		return client.getThreadPool();
	}

	@Override
	public void setEnableRedirects(boolean enableRedirects,
			boolean enableRelativeRedirects, boolean enableCircularRedirects) {

		client.setEnableRedirects(enableRedirects, enableRelativeRedirects,
				enableCircularRedirects);
	}

	@Override
	public void setRedirectHandler(RedirectHandler customRedirectHandler) {

		client.setRedirectHandler(customRedirectHandler);
	}

	@Override
	public void setUserAgent(String userAgent) {

		client.setUserAgent(userAgent);
	}

	@Override
	public void setMaxConnections(int maxConnections) {

		client.setMaxConnections(maxConnections);
	}

	@Override
	public void setTimeout(int value) {

		client.setTimeout(value);
	}

	@Override
	public void setConnectTimeout(int value) {

		client.setConnectTimeout(value);
	}

	@Override
	public void setResponseTimeout(int value) {

		client.setResponseTimeout(value);
	}

	@Override
	public void setProxy(String hostname, int port) {

		client.setProxy(hostname, port);
	}

	@Override
	public void setProxy(String hostname, int port, String username,
			String password) {

		client.setProxy(hostname, port, username, password);
	}

	@Override
	public void setSSLSocketFactory(SSLSocketFactory sslSocketFactory) {

		client.setSSLSocketFactory(sslSocketFactory);
	}

	@Override
	public void cancelRequests(Context context, boolean mayInterruptIfRunning) {

		client.cancelRequests(context, mayInterruptIfRunning);
	}

    @Override
    public void addRequestInterceptor(HttpRequestInterceptor interceptor) {
        ((DefaultHttpClient)client.getHttpClient()).addRequestInterceptor(interceptor);
    }

    @Override
    public void addResponseInterceptor(HttpResponseInterceptor interceptor) {
        ((DefaultHttpClient)client.getHttpClient()).addResponseInterceptor(interceptor);
    }

    @Override
    public CookieStore getCookieStore() {
        return (CookieStore)client.getHttpContext().getAttribute(ClientContext.COOKIE_STORE);
    }
}
