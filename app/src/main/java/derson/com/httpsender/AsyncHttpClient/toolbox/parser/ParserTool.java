package derson.com.httpsender.AsyncHttpClient.toolbox.parser;


import derson.com.httpsender.AsyncHttpClient.http.HttpFactory;

/**
 * 解析工具接口
 * @author yangzhi
 *
 */
public interface ParserTool{
	public Object parseObj(String responseContent, int reqType, HttpFactory httpRes);
}
