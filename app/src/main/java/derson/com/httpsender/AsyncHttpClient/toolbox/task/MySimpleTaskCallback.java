package derson.com.httpsender.AsyncHttpClient.toolbox.task;

/**
 * 如果不想实现全部的监听可以继承该类
 * @author yangzhi
 * @param <T>
 */
public abstract class MySimpleTaskCallback<T> implements MyTaskItemCallback<T> {

	@Override
	public T doTask(T t) {

		return null;
	}

	@Override
	public void onTaskCompleted(T result) {


	}

	@Override
	public void onTaskProgressChanged(Integer... values) {


	}

	@Override
	public void onCancelled(T result) {


	}

}
