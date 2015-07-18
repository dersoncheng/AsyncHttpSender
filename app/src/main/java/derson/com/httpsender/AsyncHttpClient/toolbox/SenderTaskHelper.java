package derson.com.httpsender.AsyncHttpClient.toolbox;


import org.apache.http.Header;

import derson.com.httpsender.AsyncHttpClient.RequestParams;
import derson.com.httpsender.AsyncHttpClient.http.RequestHandle;

/**
 * 发送状态中的请求管理器
 * 
 * @author Chengli
 *
 */
public class SenderTaskHelper {

	public static final String TAG = "SenderTaskHelper";
	/** 发送中的http服务handle */
//	private static HashMap<String, HPRequestHandle> sendTasks = new HashMap<String, HPRequestHandle>();

	// static SparseArray<HPRequestHandle> sendTasks = new
	// SparseArray<HPRequestHandle>();

	public static void addRequestHandle(String token, RequestHandle handle) {
//		if (null == sendTasks) {
//			sendTasks = new HashMap<String, HPRequestHandle>();
//		}
//		sendTasks.put(token, handle);
	}

	public static void cancelRequesHandle(String token) {
//		HPLog.e(TAG, token);
//		if (null != sendTasks) {
//			if (sendTasks.containsKey(token)) {
//				HPRequestHandle request = sendTasks.get(token);
//				if (null != request && (!request.isFinished() || !request.isCancelled())) {
//					request.cancel(true);
//					HPLog.e(TAG, "---cancelRequesHandle---" + token);
//				}
//			}
//		}
	}

	public static void cancelRequestHandle(String url, RequestParams params, Header[] headers) {
//		cancelRequesHandle(generateGetQueryUrlString(url, params, headers));
	}

	public static void removeRequestHandle(String token) {
//		if (null != sendTasks) {
//			if (sendTasks.containsKey(token)) {
//				sendTasks.remove(token);
//			}
//		}
	}

	public static void removeRequestHandle(String url, RequestParams params) {
//		removeRequestHandle(generateGetQueryUrlString(url, params, headers));
	}

	public static String generateGetQueryUrlString(String url, RequestParams params, Header[] header) {
		String query = url;
		if (params != null) {
			String paramString = params.getParamString().trim();
			if (!paramString.equals("") && !paramString.equals("?")) {
				query += query.contains("?") ? "&" : "?";
				query += paramString;
			}
		}
		if (null != header) {
			// TODO
		}
		return query;
//		return null;
	}
}
