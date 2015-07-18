package derson.com.httpsender.AsyncHttpClient.toolbox.task;

/**
 * 单个任务对象
 * 
 * @author yangzhi
 * 
 */
public class MyTaskItem {
	/** 记录的当前索引. */
	public int position;

	/** 执行完成的回调接口. */
	public MyTaskItemCallback listener;

	/** 执行完成的结果. */
	private Object result;
	
	
	/**
	 * 
	 * @param position 记录的当前索引
	 * @param listener 执行完成的回调接口
	 */
	public MyTaskItem(int position, MyTaskItemCallback listener) {
		super();
		this.position = position;
		this.listener = listener;
	}
	/**
	 * 
	 * @param listener 执行完成的回调接口
	 */
	public MyTaskItem(MyTaskItemCallback listener) {
		super();
		this.listener = listener;
	}

	public int getPosition() {
		return position;
	}

	public void setPosition(int position) {
		this.position = position;
	}

	public MyTaskItemCallback getListener() {
		return listener;
	}

	public void setListener(MyTaskItemCallback listener) {
		this.listener = listener;
	}

	public Object getResult() {
		return result;
	}

	public void setResult(Object result) {
		this.result = result;
	}

}
