package derson.com.httpsender.AsyncHttpClient.toolbox.task.callback;


import derson.com.httpsender.AsyncHttpClient.http.HttpFactory;
import derson.com.httpsender.AsyncHttpClient.toolbox.parser.ParserCallback;
import derson.com.httpsender.AsyncHttpClient.toolbox.parser.ParserTool;
import derson.com.httpsender.AsyncHttpClient.toolbox.task.MySimpleTaskCallback;
import derson.com.httpsender.AsyncHttpClient.toolbox.task.MyTaskItem;

/**
 * 解析数据接口回调实现类
 * 
 * @author yangzhi
 * 
 */
public class ParseTaskCallback extends MySimpleTaskCallback<MyTaskItem> {

	private int reqType;
	private ParserCallback parserCallback;
	private String content;
	private HttpFactory httpRes;

	public ParseTaskCallback(int reqType, HttpFactory httpRes,
							 String content, ParserCallback parserCallback) {
		this.reqType = reqType;
		this.parserCallback = parserCallback;
		this.content = content;
		this.httpRes = httpRes;
	}

	@Override
	public void onTaskCompleted(MyTaskItem result) {

		Object object = result.getResult();
		if (object != null)
			parserCallback.onSuccess(object);
		else
			parserCallback.onFailue(null);
		parserCallback = null;
		content = null;
		httpRes = null;
	}

	@Override
	public void onCancelled(MyTaskItem result) {

		super.onCancelled(result);
		parserCallback = null;
		content = null;
		httpRes = null;
	}

	@Override
	public MyTaskItem doTask(MyTaskItem t) {

		ParserTool parserTool = httpRes.createParserTool(reqType);
		Object object = parserTool.parseObj(content, reqType, httpRes);
		Object obj=parserCallback.onPaserCompleted(object);
		if(obj!=null)
			object=obj;
		t.setResult(object);
		return t;
	}

}
