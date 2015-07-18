package derson.com.httpsender.AsyncHttpClient.toolbox.task;

/**
 * 任务完成回调接口
 * @author yangzhi
 * @param <T> 
 */
public interface MyTaskOnCompleted<T> {
	/**
	 * 任务完成
	 * @param result
	 */
	public void onTaskCompleted(T result);
}
