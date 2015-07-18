package derson.com.httpsender.AsyncHttpClient.http.impl;


import org.apache.http.Header;

import java.io.UnsupportedEncodingException;

import derson.com.httpsender.AsyncHttpClient.AsyncHttpResponseHandler;
import derson.com.httpsender.AsyncHttpClient.RequestParams;
import derson.com.httpsender.AsyncHttpClient.http.HttpCallback;
import derson.com.httpsender.AsyncHttpClient.toolbox.CacheManager;
import derson.com.httpsender.AsyncHttpClient.toolbox.CacheParams;
import derson.com.httpsender.AsyncHttpClient.toolbox.HPParseError;

/**
 * Created by chengli on 15/7/11.
 */
public class SyncHttpHandler extends AsyncHttpResponseHandler{

    private Builder builder;

    public SyncHttpHandler(Builder builder) {
        // TODO Auto-generated constructor stub
        this.builder = builder;
    }

    @Override
    public void onStart() {
        super.onStart();
        if (null != builder.response) {
            builder.response.onStart(builder.reqType);
        }
    }

    @Override
    public void onSuccess(int statusCode, Header[] headers, byte[] responseBody) {
        if(null != builder.cacheParams) {
            CacheManager.setCache(builder.cacheParams.getCacheUrl(), responseBody, builder.cacheParams.getExpireTime(), builder.cacheParams.getCacheType() == CacheParams.CacheSpec.TYPE_CACHE_NO_REFRESH_INTIME);
        }
        if(null != builder.response) {
            try {
                final String srt2 = new String(responseBody, "UTF-8");
                builder.response.onSuccess(srt2, null, builder.reqType, builder.isUseCache);
                builder.response.onSuccess(srt2, null, builder.reqType, statusCode,
                        headers, responseBody, builder.isUseCache);
                garbageCollection();
                builder.isFinished = true;
            } catch (UnsupportedEncodingException e) {
                e.printStackTrace();
                HPParseError error = new HPParseError("数据解析错误", e.getCause());
                builder.response.onFailure(error.getCause(), error.getMessage(), builder.reqType);
                builder.response.onFailure(error.getCause(), error.getMessage(), builder.reqType,
                        statusCode, headers, responseBody);
                garbageCollection();
                builder.isFinished = true;
            } finally {

            }
        }

    }

    @Override
    public void onFailure(int statusCode, Header[] headers, byte[] responseBody, Throwable error) {
        if(null != builder.response) {
            builder.response.onFailure(error, error.getMessage(), builder.reqType);
            builder.response.onFailure(error, error.getMessage(), builder.reqType, statusCode,
                    headers, responseBody);
            garbageCollection();
            builder.isFinished = true;
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
    }

    private void garbageCollection() {
        if (builder.isFinished) {
            builder.response = null;
        }
    }

    public static class Builder {
        protected HttpCallback response;
        protected int reqType;
        protected CacheParams cacheParams;
        protected boolean isUseCache = false;
        protected String cacheUrl= "";
        protected RequestParams params;
        protected boolean isFinished = false;

        public Builder(HttpCallback response, int reqType) {
            // TODO Auto-generated constructor stub
            this.response = response;
            this.reqType = reqType;
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

        public Builder setIsFinished(boolean isFinish) {
            this.isFinished = isFinish;
            return this;
        }

        public Builder setUseCache(boolean isUse) {
            this.isUseCache = isUse;
            return this;
        }

        public SyncHttpHandler create() {
            SyncHttpHandler syncHttpHandler = new SyncHttpHandler(this);
            return syncHttpHandler;
        }
    }
}
