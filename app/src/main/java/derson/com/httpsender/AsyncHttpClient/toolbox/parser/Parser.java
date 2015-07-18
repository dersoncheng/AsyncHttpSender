package derson.com.httpsender.AsyncHttpClient.toolbox.parser;

/**
 * 具体解析接口
 * @author yangzhi
 *
 */
public interface Parser<T> {
	/**
	 * 解析方法
	 * @param paserObject   根据具体解析技术  做处理   Json或XML
	 * @throws Exception
	 */
	public  T parse(Object paserObject) throws Exception;
}
