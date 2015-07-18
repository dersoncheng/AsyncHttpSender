package derson.com.httpsender.AsyncHttpClient.toolbox.task;

public interface MyTaskItemCallback<T> extends MyTaskOnCompleted<T> {
	/**
	 * 任务执行
	 * @return
	 */
	public T doTask(T t);

	
	/**
	 * 任务进度改变
	 * @param values
	 */
	public void onTaskProgressChanged(Integer... values);
	
	/**
	 * 任务取消
	 * @param result
	 */
	public void onCancelled(T result);
	
	
//	/**
//	 * 取消任务
//	 */
//	public void cancelTask();
}
