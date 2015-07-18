package derson.com.httpsender.AsyncHttpClient.http.impl;

import com.hupu.android.net.AsyncHttpClient.RequestHandle;
import com.hupu.android.net.http.HPRequestHandle;

/**
 * AsyncHttpClient请求返回操作器
 * @author yangzhi
 *
 */
public class AsyncHttpHandle implements HPRequestHandle{
	private RequestHandle requestHandle; 
	
	public AsyncHttpHandle(RequestHandle requestHandle){
		this.requestHandle=requestHandle;
	}
	
	@Override
	public boolean cancel(boolean mayInterruptIfRunning) {

		return requestHandle.cancel(mayInterruptIfRunning);
	}

	@Override
	public boolean isFinished() {

		return requestHandle.isFinished();
	}

	@Override
	public boolean isCancelled() {

		return requestHandle.isCancelled();
	}

	@Override
	public boolean shouldBeGarbageCollected() {

		return requestHandle.shouldBeGarbageCollected();
	}

}
