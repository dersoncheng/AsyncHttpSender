package derson.com.httpsender.AsyncHttpClient.toolbox.parser.pasertool;


import org.apache.http.HttpEntity;
import org.apache.http.util.EntityUtils;
import org.json.JSONObject;

import java.io.IOException;

import derson.com.httpsender.AsyncHttpClient.http.HttpFactory;
import derson.com.httpsender.AsyncHttpClient.toolbox.parser.Parser;
import derson.com.httpsender.AsyncHttpClient.toolbox.parser.ParserTool;

/**
 * Json解析工具
 * 
 * @author yangzhi
 * 
 */
public class JsonParserTool implements ParserTool {
	private final String KEY_DATA = "";
	private static volatile JsonParserTool jsonPaserTool;

	private JsonParserTool() {
	}

	public static JsonParserTool getJsonPaserTool() {
		if (jsonPaserTool == null)
			synchronized (JsonParserTool.class) {
				if (jsonPaserTool == null)
					jsonPaserTool = new JsonParserTool();
			}
		return jsonPaserTool;
	}

	public Object parseObj(String s, int type, HttpFactory httpRes) {
		if (s == null || s.trim().toString().equals(""))
			return null;
		Parser jsonPaser = httpRes.getParser(type);
		if (jsonPaser != null) {
			try {
				JSONObject jsonObject = new JSONObject(s);
				if (jsonObject != null) {
					// 非空的数据解
					return jsonPaser.parse(jsonObject);
				}
			} catch (Exception e) {
				e.printStackTrace();
				return null;
			}
		}
		return jsonPaser;
	}

	private String isErr(JSONObject jsonObject) {
		// 错误ID及错误说明（当其存在，result值无效）
		int status = jsonObject.optInt("status");
		if (status != 0) {
			return jsonObject.optString("msg", "");
		}
		return "";
	}

	private boolean isNull(JSONObject jsonObject) {
		String ss = jsonObject.optString(KEY_DATA, null);
		if (ss == null)
			return true;
		if (ss.equals("{}") || ss.equals("[]"))
			return true;
		return false;

	}

	public Object parseObj(HttpEntity en, int type,HttpFactory httpRes) {
		Object obj = null;
		if (en != null) {
			try {
				// Value dynamic =
				// MessagePack.unpack(EntityUtils.toByteArray(en));
				// entity= paserObj(dynamic.toString(), type);
				obj = parseObj(EntityUtils.toString(en), type, httpRes);
				// Log.e("papa", "return=="+dynamic.toString());
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}

		return obj;
	}

}
