package derson.com.httpsender.AsyncHttpClient.toolbox.parser;

/**
 * 异步解析回调接口
 * @author yangzhi
 *
 */
public interface ParserCallback {
	
	public void onSuccess(Object obj);
	
	/**
	 * 当解析完成(子线程)
	 * @param obj
	 */
	public Object onPaserCompleted(Object obj);
	
	public void onFailue(Object obj);

}
