package derson.com.httpsender.AsyncHttpClient.toolbox.parser;


import derson.com.httpsender.AsyncHttpClient.http.HttpFactory;

/**
 * 解析工具工厂
 * 
 * @author yangzhi
 * 
 *         paserToolStrategy为具体项目实例化的工厂策略
 */
public class ParserToolFactory {
	private static volatile ParserToolFactory paserToolFactory;
	private PaserToolStrategy paserToolStrategy;

	public static ParserToolFactory getInstance() {
		if (paserToolFactory == null) {
			synchronized (ParserToolFactory.class) {
				if (paserToolFactory == null)
					paserToolFactory = new ParserToolFactory();
			}
		}
		return paserToolFactory;
	}

	private ParserToolFactory() {
	}

	public void setPaserToolStrategy(PaserToolStrategy paserToolStrategy) {
		this.paserToolStrategy = paserToolStrategy;
	}

	public PaserToolStrategy getPaserToolStrategy() {
		return paserToolStrategy;
	}

	/**
	 * 根据请求TYPE获取不同的解析工具
	 * 
	 * @param reqType
	 * @return
	 */
	public ParserTool createPaserTool(int reqType,HttpFactory httpRes) {
		return paserToolStrategy.getPaserToolByReq(reqType,httpRes);
	}

	public interface PaserToolStrategy {
		public ParserTool getPaserToolByReq(int reqType, HttpFactory httpRes);
	}
}
