package derson.com.httpsender.AsyncHttpClient.toolbox.task;

/**
 * 多线程操作类 
 * 特别注意:
 * 1.普通短时间线程直接调用excute(HPTaskItem item)方法
 * 2.操作线程耗时过长（包括可能会运行长时间）均使用executeOnExecutor(HPAsyncTask.THREAD_POOL_LONG,HPTaskItem item);
 * @author yangzhi
 * 
 */
public class MyTask extends MyAsyncTask<MyTaskItem, Integer, MyTaskItem> {

	@Override
	protected MyTaskItem doInBackground(MyTaskItem... params) {

		MyTaskItem taskItem = params[0];
		taskItem.listener.doTask(taskItem);
		return taskItem;
	}

	@Override
	protected void onPostExecute(MyTaskItem result) {

		super.onPostExecute(result);
		result.listener.onTaskCompleted(result);
	}

	@Override
	protected void onCancelled(MyTaskItem result) {

		super.onCancelled(result);
		result.listener.onCancelled(result);
	}

}
