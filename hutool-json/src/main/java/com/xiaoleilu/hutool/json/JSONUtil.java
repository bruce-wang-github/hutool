package com.xiaoleilu.hutool.json;

import java.io.File;
import java.io.IOException;
import java.io.StringWriter;
import java.io.Writer;
import java.nio.charset.Charset;
import java.util.Collection;
import java.util.Enumeration;
import java.util.Map;
import java.util.ResourceBundle;

import com.xiaoleilu.hutool.io.IORuntimeException;
import com.xiaoleilu.hutool.io.file.FileReader;
import com.xiaoleilu.hutool.util.ArrayUtil;
import com.xiaoleilu.hutool.util.ObjectUtil;
import com.xiaoleilu.hutool.util.StrUtil;

/**
 * JSON工具类
 * 
 * @author Looly
 *
 */
public final class JSONUtil {
	
	private JSONUtil() {}
	
	//-------------------------------------------------------------------- Pause start
	/**
	 * 创建JSONObject
	 * @return JSONObject
	 */
	public static JSONObject createObj(){
		return new JSONObject();
	}
	
	/**
	 * 创建 JSONArray
	 * @return JSONArray
	 */
	public static JSONArray createArray(){
		return new JSONArray();
	}
	
	/**
	 * JSON字符串转JSONObject对象
	 * @param jsonStr JSON字符串
	 * @return JSONObject
	 */
	public static JSONObject parseObj(String jsonStr) {
		return new JSONObject(jsonStr);
	}
	
	/**
	 * JSON字符串转JSONObject对象
	 * @param obj Bean对象或者Map
	 * @return JSONObject
	 */
	public static JSONObject parseObj(Object obj) {
		return new JSONObject(obj);
	}

	/**
	 * JSON字符串转JSONArray
	 * @param jsonStr JSON字符串
	 * @return JSONArray
	 */
	public static JSONArray parseArray(String jsonStr) {
		return new JSONArray(jsonStr);
	}
	
	/**
	 * 转换对象为JSON<br>
	 * 支持的对象：<br>
	 * String: 转换为相应的对象<br>
	 * Array Collection：转换为JSONArray<br>
	 * Bean对象：转为JSONObject
	 * @param obj 对象
	 * @return JSON
	 */
	public static JSON parse(Object obj){
		if(null == obj){
			return null;
		}
		
		JSON json = null;
		if(obj instanceof JSON){
			json = (JSON) obj;
		}else if(obj instanceof String){
			String jsonStr = ((String)obj).trim();
			if(jsonStr.startsWith("[")){
				json = parseArray(jsonStr);
			}else{
				json = parseObj(jsonStr);
			}
		}else if(obj instanceof Collection || obj.getClass().isArray()){//列表
			json = new JSONArray(obj);
		}else{//对象
			json = new JSONObject(obj);
		}
		
		return json;
	}
	
	/**
	 * XML字符串转为JSONObject
	 * @param xmlStr XML字符串
	 * @return JSONObject
	 */
	public static JSONObject parseFromXml(String xmlStr){
		return XML.toJSONObject(xmlStr);
	}
	
	/**
	 * Map转化为JSONObject
	 * @param map {@link Map}
	 * @return JSONObject
	 */
	public static JSONObject parseFromMap(Map<?, ?> map){
		return new JSONObject(map);
	}
	
	/**
	 * ResourceBundle转化为JSONObject
	 * @param bundle ResourceBundle文件
	 * @return JSONObject
	 */
	public static JSONObject parseFromResourceBundle(ResourceBundle bundle){
		JSONObject jsonObject = new JSONObject();
		Enumeration<String> keys = bundle.getKeys();
		while (keys.hasMoreElements()) {
			String key = keys.nextElement();
			if (key != null) {
				InternalJSONUtil.propertyPut(jsonObject, key, bundle.getString(key));
			}
		}
		return jsonObject;
	}
	//-------------------------------------------------------------------- Pause end
	
	//-------------------------------------------------------------------- Read start
	/**
	 * 读取JSON
	 * 
	 * @param file JSON文件
	 * @param charset 编码
	 * @return JSON（包括JSONObject和JSONArray）
	 * @throws IORuntimeException
	 */
	public static JSON readJSON(File file, Charset charset) throws IORuntimeException {
		return parse(FileReader.create(file, charset).readString());
	}
	
	/**
	 * 读取JSONObject
	 * 
	 * @param file JSON文件
	 * @param charset 编码
	 * @return JSONObject
	 * @throws IORuntimeException
	 */
	public static JSONObject readJSONObject(File file, Charset charset) throws IORuntimeException {
		return parseObj(FileReader.create(file, charset).readString());
	}
	
	/**
	 * 读取JSONArray
	 * 
	 * @param file JSON文件
	 * @param charset 编码
	 * @return JSONArray
	 * @throws IORuntimeException
	 */
	public static JSONArray readJSONArray(File file, Charset charset) throws IORuntimeException {
		return parseArray(FileReader.create(file, charset).readString());
	}
	//-------------------------------------------------------------------- Read end

	//-------------------------------------------------------------------- toString start
	/**
	 * 转为JSON字符串
	 * @param json JSON
	 * @param indentFactor 每一级别的缩进
	 * @return JSON字符串
	 */
	public static String toJsonStr(JSON json, int indentFactor) {
		return json.toJSONString(indentFactor);
	}
	
	/**
	 * 转为JSON字符串
	 * @param json JSON
	 * @return JSON字符串
	 */
	public static String toJsonStr(JSON json) {
		return json.toJSONString(0);
	}
	
	/**
	 * 转为JSON字符串
	 * @param json JSON
	 * @return JSON字符串
	 */
	public static String toJsonPrettyStr(JSON json) {
		return json.toJSONString(4);
	}
	
	/**
	 * 转换为JSON字符串
	 * @param obj 被转为JSON的对象
	 * @return JSON字符串
	 */
	public static String toJsonStr(Object obj){
		return toJsonStr(parse(obj));
	}
	
	/**
	 * 转换为格式化后的JSON字符串
	 * @param obj Bean对象
	 * @return JSON字符串
	 */
	public static String toJsonPrettyStr(Object obj){
		return toJsonPrettyStr(parse(obj));
	}
	
	/**
	 * 转换为XML字符串
	 * @param json JSON
	 * @return XML字符串
	 */
	public static String toXmlStr(JSON json){
		return XML.toString(json);
	}
	//-------------------------------------------------------------------- toString end
	
	//-------------------------------------------------------------------- toBean start
	/**
	 * 转为实体类对象，转换异常将被抛出
	 * @param json JSONObject
	 * @param beanClass 实体类对象
	 * @return 实体类对象
	 */
	public static <T> T toBean(JSONObject json, Class<T> beanClass) {
		return toBean(json, beanClass, false);
	}
	
	/**
	 * 转为实体类对象
	 * @param json JSONObject
	 * @param beanClass 实体类对象
	 * @return 实体类对象
	 */
	public static <T> T toBean(JSONObject json, Class<T> beanClass, boolean ignoreError) {
		return null == json ? null : json.toBean(beanClass, ignoreError);
	}
	//-------------------------------------------------------------------- toBean end

	/**
	 * 对所有双引号做转义处理（使用双反斜杠做转义）<br>
	 * 为了能在HTML中较好的显示，会将&lt;/转义为&lt;\/<br>
	 * JSON字符串中不能包含控制字符和未经转义的引号和反斜杠
	 *
	 * @param string A String
	 * @return A String correctly formatted for insertion in a JSON text.
	 */
	public static String quote(String string) {
		StringWriter sw = new StringWriter();
		synchronized (sw.getBuffer()) {
			try {
				return quote(string, sw).toString();
			} catch (IOException ignored) {
				// will never happen - we are writing to a string writer
				return "";
			}
		}
	}

	/**
	 * 对所有双引号做转义处理（使用双反斜杠做转义）<br>
	 *  为了能在HTML中较好的显示，会将&lt;/转义为&lt;\/<br>
	 *  JSON字符串中不能包含控制字符和未经转义的引号和反斜杠
	 * @param string A String
	 * @param writer Writer
	 * @return A String correctly formatted for insertion in a JSON text.
	 * @throws IOException
	 */
	public static Writer quote(String string, Writer writer) throws IOException {
		if (StrUtil.isEmpty(string)) {
			writer.write("\"\"");
			return writer;
		}

		char b;		//back char
		char c = 0; //current char
		String hhhh;
		int i;
		int len = string.length();

		writer.write('"');
		for (i = 0; i < len; i++) {
			b = c;
			c = string.charAt(i);
			switch (c) {
				case '\\':
				case '"':
					writer.write('\\');
					writer.write(c);
					break;
				case '/':
					if (b == '<') {
						writer.write('\\');
					}
					writer.write(c);
					break;
				case '\b':
					writer.write("\\b");
					break;
				case '\t':
					writer.write("\\t");
					break;
				case '\n':
					writer.write("\\n");
					break;
				case '\f':
					writer.write("\\f");
					break;
				case '\r':
					writer.write("\\r");
					break;
				default:
					if (c < ' ' || (c >= '\u0080' && c < '\u00a0') || (c >= '\u2000' && c < '\u2100')) {
						writer.write("\\u");
						hhhh = Integer.toHexString(c);
						writer.write("0000", 0, 4 - hhhh.length());
						writer.write(hhhh);
					} else {
						writer.write(c);
					}
			}
		}
		writer.write('"');
		return writer;
	}
	
	/**
	 * 在需要的时候包装对象<br>
	 * 包装包括：
	 * <ul>
	 * 	<li><code>null</code> -> <code>JSONNull.NULL</code></li>
	 * 	<li>array or collection -> JSONArray</li>
	 * 	<li>map -> JSONObject</li>
	 * 	<li>standard property (Double, String, et al) -> 原对象</li>
	 * 	<li>来自于java包 -> 字符串</li>
	 * 	<li>其它 -> 尝试包装为JSONObject，否则返回<code>null</code></li>
	 * </ul>
	 * 
	 * @param object The object to wrap
	 * @return The wrapped value
	 */
	public static Object wrap(Object object) {
		try {
			if (object == null) {
				return JSONNull.NULL;
			}
			if (object instanceof JSON
					|| JSONNull.NULL.equals(object) 
					|| object instanceof JSONString 
					|| object instanceof CharSequence
					|| object instanceof Number
					|| ObjectUtil.isBasicType(object)) {
				return object;
			}

			if (object instanceof Collection) {
				Collection<?> coll = (Collection<?>) object;
				return new JSONArray(coll);
			}
			if (ArrayUtil.isArray(object)) {
				return new JSONArray(object);
			}
			if (object instanceof Map) {
				Map<?, ?> map = (Map<?, ?>) object;
				return new JSONObject(map);
			}
			Package objectPackage = object.getClass().getPackage();
			String objectPackageName = objectPackage != null ? objectPackage.getName() : "";
			if (objectPackageName.startsWith("java.") || objectPackageName.startsWith("javax.") || object.getClass().getClassLoader() == null) {
				return object.toString();
			}
			return new JSONObject(object);
		} catch (Exception exception) {
			return null;
		}
	}
}
