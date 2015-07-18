package derson.com.httpsender.AsyncHttpClient.http;

import android.content.Context;

import com.hupu.android.cache.CacheParams;
import com.hupu.android.global.HPHttpFactory;
import com.hupu.android.net.AsyncHttpClient.RequestParams;

import org.apache.http.HttpRequestInterceptor;
import org.apache.http.HttpResponseInterceptor;
import org.apache.http.client.CookieStore;
import org.apache.http.client.RedirectHandler;
import org.apache.http.conn.ssl.SSLSocketFactory;

import java.util.concurrent.ExecutorService;

/**
 * 请求对象模板接口
 * 
 * @author yangzhi
 *
 * modify by chengli 07/16/2015
 * 
 */
public interface HttpClient {
    /**
     * 设置Cookie
     *
     * @param cookieStore
     */
    public void setCookieStore(CookieStore cookieStore);

    /**
     * 获取Cookie
     * @return
     */
    public CookieStore getCookieStore();

    /**
     * 自定义线程池
     *
     * @param threadPool
     */
    public void setThreadPool(ExecutorService threadPool);

	/**
	 * 获得线程池
	 * 
	 * @return
	 */
	public ExecutorService getThreadPool();

	/**
	 * 设置重定向
	 * 
	 * @param enableRedirects
	 * @param enableRelativeRedirects
	 * @param enableCircularRedirects
	 */
	public void setEnableRedirects(final boolean enableRedirects,
                                   final boolean enableRelativeRedirects,
                                   final boolean enableCircularRedirects);

	/**
	 * 设置自定义redirecthandler的实现
	 * 
	 * @param customRedirectHandler
	 */
	public void setRedirectHandler(final RedirectHandler customRedirectHandler);

	/**
	 * 设置UserAgent
	 * 
	 * @param userAgent
	 */
	public void setUserAgent(String userAgent);

	/**
	 * 设置最大连接数
	 * 
	 * @param maxConnections
	 */
	public void setMaxConnections(int maxConnections);

	/**
	 * 设置超时时间
	 * 
	 * @param value
	 */
	public void setTimeout(int value);

	/**
	 * 设置连接时间
	 * 
	 * @param value
	 */
	public void setConnectTimeout(int value);

	/**
	 * 设置返回超时时间
	 * 
	 * @param value
	 */
	public void setResponseTimeout(int value);

	/**
	 * 设置代理
	 * 
	 * @param hostname
	 * @param port
	 */
	public void setProxy(String hostname, int port);

	/**
	 * 通过用户名密码设置代理
	 * 
	 * @param hostname
	 * @param port
	 * @param username
	 * @param password
	 */
	public void setProxy(String hostname, int port, String username,
                         String password);

	/**
	 * 设置SSLSocket的生产工厂
	 * 
	 * @param sslSocketFactory
	 */
	public void setSSLSocketFactory(SSLSocketFactory sslSocketFactory);

    public void addRequestInterceptor(HttpRequestInterceptor interceptor);


    public void addResponseInterceptor(HttpResponseInterceptor interceptor);
    /**
     * 打断所有请求
     *
     * @param context
     * @param mayInterruptIfRunning
     */
    public void cancelRequests(final Context context,
                               final boolean mayInterruptIfRunning);

    /**
     * 每次请求设置ua的网络类型
     * @param networkType
     */
    public void updateUserAgent(int networkType);

    /**
     * 设置网宿代理
     */
    public void setWangsu();

    /**
     * HTTP异步post请求 异步解析返回结果
     * @param reqType
     * @param httpRes
     * @param context
     * @param url
     * @param params
     * @param callback
     * @return
     */
    public RequestHandle post(int reqType, HttpFactory httpRes,
                                Context context, String url, RequestParams params,
                                HttpCallback callback);

    /**
     * HTTP异步post请求 同步解析返回结果
     * @param reqType
     * @param context
     * @param url
     * @param params
     * @param callback
     * @return
     */
    public RequestHandle postSync(int reqType, Context context, String url, RequestParams params, HttpCallback callback);

    /**
     * HTTP异步get请求 异步解析返回结果
     * @param reqType
     * @param httpRes
     * @param context
     * @param url
     * @param params
     * @param cacheParams
     * @param callback
     * @return
     */
    public RequestHandle get(int reqType, HttpFactory httpRes,
                               Context context, String url, RequestParams params,
                               CacheParams cacheParams, HttpCallback callback);

    /**
     * HTTP异步get请求，同步解析返回结果
     * @param reqType
     * @param context
     * @param url
     * @param params
     * @param cacheParams
     * @param callback
     * @return
     */
    public RequestHandle getSync(int reqType, Context context, String url, RequestParams params, CacheParams cacheParams, HttpCallback callback);


}
