package derson.com.httpsender.AsyncHttpClient.http;

/**
 * HTTP请求操作器
 * @author user
 *
 */
public interface RequestHandle {
	/**
	 * 打断(取消)该请求
	 * @param mayInterruptIfRunning
	 * @return
	 */
	public boolean cancel(final boolean mayInterruptIfRunning);
	
	/**
	 * 判断该请求是否结束
	 * @return
	 */
	public boolean isFinished();

	/**
	 * 判断该请求是否被取消
	 * @return
	 */
	public boolean isCancelled();

	/**
	 * 执行垃圾回收
	 * @return
	 */
	public boolean shouldBeGarbageCollected();
}
