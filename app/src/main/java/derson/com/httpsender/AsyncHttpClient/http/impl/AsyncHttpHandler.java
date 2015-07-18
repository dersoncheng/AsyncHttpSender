package derson.com.httpsender.AsyncHttpClient.http.impl;


import org.apache.http.Header;

import java.io.UnsupportedEncodingException;

import derson.com.httpsender.AsyncHttpClient.AsyncHttpClient;
import derson.com.httpsender.AsyncHttpClient.AsyncHttpResponseHandler;
import derson.com.httpsender.AsyncHttpClient.RequestParams;
import derson.com.httpsender.AsyncHttpClient.http.HttpCallback;
import derson.com.httpsender.AsyncHttpClient.http.HttpFactory;
import derson.com.httpsender.AsyncHttpClient.toolbox.CacheManager;
import derson.com.httpsender.AsyncHttpClient.toolbox.CacheParams;
import derson.com.httpsender.AsyncHttpClient.toolbox.HPParseError;
import derson.com.httpsender.AsyncHttpClient.toolbox.SenderTaskHelper;
import derson.com.httpsender.AsyncHttpClient.toolbox.parser.ParserCallback;
import derson.com.httpsender.AsyncHttpClient.toolbox.task.MyTask;
import derson.com.httpsender.AsyncHttpClient.toolbox.task.MyTaskItem;
import derson.com.httpsender.AsyncHttpClient.toolbox.task.callback.ParseTaskCallback;

/**
 * AsyncHttp的请求回调
 * 
 * @author yangzhi
 * 
 */
public class AsyncHttpHandler extends AsyncHttpResponseHandler {
	private Builder builder;
	
	public AsyncHttpHandler(Builder builder) {
		// TODO Auto-generated constructor stub
		this.builder = builder;
	}

	@Override
	public void onSuccess(final int statusCode, final Header[] headers,
			final byte[] responseBody) {

		// String isoString = new String(responseBody,"ISO-8859-1");
		// 缓存服务数据
		if(null != builder.cacheParams) {
			CacheManager.setCache(builder.cacheParams.getCacheUrl(), responseBody, builder.cacheParams.getExpireTime(), builder.cacheParams.getCacheType() == CacheParams.CacheSpec.TYPE_CACHE_NO_REFRESH_INTIME);
		}
		try {
			builder.isFinished = false;
			final String srt2 = new String(responseBody, "UTF-8");
			MyTask hpTask = new MyTask();
			MyTaskItem taskItem = new MyTaskItem(new ParseTaskCallback(builder.reqType,
					builder.httpRes, srt2, new ParserCallback() {

						@Override
						public void onSuccess(Object obj) {

							builder.response.onSuccess(srt2, obj, builder.reqType, builder.isUseCache);
							builder.response.onSuccess(srt2, obj, builder.reqType, statusCode,
									headers, responseBody, builder.isUseCache);
							garbageCollection();
							builder.isFinished = true;
						}

						@Override
						public void onFailue(Object obj) {
                            HPParseError error = new HPParseError("数据解析错误", new Throwable(
                                    "Parser obj is error!"));
							builder.response.onFailure(error.getCause(), error.getMessage(), builder.reqType);
							builder.response.onFailure(error.getCause(), error.getMessage(), builder.reqType,
									statusCode, headers, responseBody);
							garbageCollection();
							builder.isFinished = true;
						}

						@Override
						public Object onPaserCompleted(Object obj) {

							return builder.response.onParserCompleted(srt2, obj,
									builder.reqType, builder.isUseCache);
						}
					}));
			hpTask.executeOnExecutor(AsyncHttpClient.getInstance()
					.getThreadPool(), taskItem);
		} catch (UnsupportedEncodingException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
            HPParseError error = new HPParseError("数据解析错误", e.getCause());
			builder.response.onFailure(error.getCause(), error.getMessage(), builder.reqType,
					statusCode, headers, responseBody);
			builder.response.onFailure(error.getCause(), error.getMessage(), builder.reqType);
			garbageCollection();
			builder.isFinished = true;
		} finally {
			SenderTaskHelper.removeRequestHandle(builder.httpRes.getUrl(builder.reqType), builder.params);
		}
	}

	@Override
	public void onFailure(int statusCode, Header[] headers,
			byte[] responseBody, Throwable error) {

		builder.response.onFailure(error, error.getMessage(), builder.reqType);
		builder.response.onFailure(error, error.getMessage(), builder.reqType, statusCode,
				headers, responseBody);
		garbageCollection();
		builder.isFinished = true;
		SenderTaskHelper.removeRequestHandle(builder.httpRes.getUrl(builder.reqType), builder.params);
	}

	@Override
	public void onStart() {

		super.onStart();
		if (null != builder.response) {
			builder.response.onStart(builder.reqType);
		}
	}

	@Override
	public void onRetry(int retryNo) {

		super.onRetry(retryNo);
		if (null != builder.response) {
			builder.response.onRetry(retryNo, retryNo);
		}
	}

	@Override
	public void onFinish() {

		super.onFinish();
		if (null != builder.response) {
			builder.response.onFinish(builder.reqType);
		}
		garbageCollection();
		builder.isFinished = true;
	}

	@Override
	public void onCancel() {

		super.onCancel();
		if (null != builder.response) {
			builder.response.onCancel(builder.reqType);
		}
		SenderTaskHelper.removeRequestHandle(builder.httpRes.getUrl(builder.reqType), builder.params);
	}

	private void garbageCollection() {
		if (builder.isFinished) {
			builder.response = null;
			builder.httpRes = null;
		}
	}
	
	public static class Builder {
		protected HttpCallback response;
		protected int reqType;
		protected HttpFactory httpRes;
		protected boolean isFinished = false;
		protected CacheParams cacheParams;
		protected boolean isUseCache = false;
		protected String cacheUrl= "";
		protected RequestParams params;

		public Builder(HttpCallback response, int reqType,
				HttpFactory httpRes) {
			// TODO Auto-generated constructor stub
			this.response = response;
			this.reqType = reqType;
			this.httpRes = httpRes;
		}
		
		public Builder setCacheParams(CacheParams cache) {
			this.cacheParams = cache;
			return this;
		}
		
		public Builder setCacheUrl(String cacheUrl) {
			this.cacheUrl = cacheUrl;
			return this;
		}
		
		public Builder setRequestParams(RequestParams params) {
			this.params = params;
			return this;
		}
		
		public Builder setUseCache(boolean isUse) {
			this.isUseCache = isUse;
			return this;
		}
		
		public Builder setIsFinished(boolean isFinish) {
			this.isFinished = isFinish;
			return this;
		}
		
		public AsyncHttpHandler create() {
			AsyncHttpHandler asyncHttpHandler = new AsyncHttpHandler(this);
			return asyncHttpHandler;
		}
	}
	
}
