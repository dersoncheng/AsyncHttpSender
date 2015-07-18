package derson.com.httpsender.AsyncHttpClient.http;

import android.content.Context;

import derson.com.httpsender.AsyncHttpClient.toolbox.parser.Parser;
import derson.com.httpsender.AsyncHttpClient.toolbox.parser.ParserTool;


/**
 * HTTP请求相关元素生产接口 产生请求URL和Parser解析实例和生产HPHttpRequest实例的接口
 * 
 * @author yangzhi
 * 
 */
public interface HttpFactory {
	/**
	 * 根据reqType获取URL
	 * 
	 * @param reqType
	 * @return
	 */
	public String getUrl(int reqType);

	/**
	 * 请求地址需要格式化的
	 * */
	public String getUrl(int mId, String format);

	/**
	 * 根据reqType产生请求request实例
	 * 
	 * @param reqType
	 * @return
	 */
	public HttpRequest createHttpRequest(int reqType, Context context);

	/**
	 * 根据reqType获取返回解析对象
	 * 
	 * @param reqType
	 * @return
	 */
	public Parser getParser(int reqType);

	/**
	 * 根据reqType生产解析工具
	 * 
	 * @param reqType
	 * @return
	 */
	public ParserTool createParserTool(int reqType);
	
}
