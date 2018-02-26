package com.pm360.cepm360.common.json;

import android.annotation.SuppressLint;
import android.provider.ContactsContract.Contacts.Data;
import android.util.Log;

import com.pm360.cepm360.common.util.DateUtils;

import org.apache.http.message.BasicNameValuePair;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.lang.reflect.Field;
import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Type;
import java.math.BigDecimal;
import java.math.BigInteger;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Map;
import java.util.Set;

/**
 * json util class, use in for analyzing json
 * 
 * @author Email : 2015-5-13 15:39:51
 * 
 */
@SuppressLint("DefaultLocale") public class JsonUtils {
	private static final String TAG = "JsonUtil";
	private static final boolean DEBUG = false;

	private JSONObject mJsonObject;
	private JSONArray mJsonArray;

	private enum JSON_TYPE {
		JSON_TYPE_OBJECT, JSON_TYPE_ARRAY, JSON_TYPE_ERROR
	}

	public JsonUtils() {
		
	}
	
	public JsonUtils(String json) {
		this.setJson(json);
	}
	
	/**
	 * 获取JsonUtils实例对象
	 * @param json
	 * @return
	 */
	public static JsonUtils getInstance(String json) {
		JsonUtils util = new JsonUtils(json);
		return util;
	}

	/**
	 * 设置内部JSONObject对象，这里不做空指针处理，请在保证json不为null的情况下调用
	 */
	public void setJson(String json) {
		if (DEBUG) Log.d(TAG, "json = " + json);
		
		if (json.equals("")) {
			Log.e(TAG, "传入的字符串为空字符串！");
			return;
		}
		
		JSON_TYPE jsonType = JSON_TYPE.JSON_TYPE_ERROR;
		jsonType = getJSONType(json);
		if (DEBUG) Log.d(TAG, "jsonType = " + jsonType);

		try {
			if (jsonType == JSON_TYPE.JSON_TYPE_OBJECT) {
				mJsonObject = new JSONObject(json);
			} else if (jsonType == JSON_TYPE.JSON_TYPE_ARRAY) {
				mJsonArray = new JSONArray(json);
				if (DEBUG)
					Log.d(TAG, "jsonArray" + mJsonArray);
			} else {
					Log.e(TAG, "字符串：\"" + json + "\" 是无效的json字符串！");
			}
		} catch (JSONException e) {
			if (DEBUG)
				Log.d(TAG, "create json exception");
			e.printStackTrace();
		}
	}

	/**
	 * 获取Json字符串的类型
	 * @param jsonString
	 * @return
	 */
	public static JSON_TYPE getJSONType(String jsonString) {
		if (jsonString == null || jsonString.isEmpty()) {
			return JSON_TYPE.JSON_TYPE_ERROR;
		}

		char firstChar = jsonString.charAt(0);
		if (firstChar == '{') {
			return JSON_TYPE.JSON_TYPE_OBJECT;
		} else if (firstChar == '[') {
			return JSON_TYPE.JSON_TYPE_ARRAY;
		} else {
			return JSON_TYPE.JSON_TYPE_ERROR;
		}
	}

	/**
	 * get json object
	 * @param json
	 * @return JOSNObject
	 */
	public static JSONObject getJsonObject(String json) {
		JSONObject jsonObject = null;

		try {
			jsonObject = new JSONObject(json);
		} catch (JSONException e) {
			if (DEBUG)
				Log.d(TAG, "create jsonobject exception");
			e.printStackTrace();
		}

		return jsonObject;
	}

	/**
	 * get json Array
	 * @param json
	 * @return JOSNArray
	 */
	public static JSONArray getJsonArray(String json) {
		JSONArray jsonArray = null;

		try {
			jsonArray = new JSONArray(json);
		} catch (JSONException e) {
			if (DEBUG)
				Log.d(TAG, "create jsonArray exception");
			e.printStackTrace();
		}

		return jsonArray;
	}

	/*
	 * ========================================JsonString to Object==============================
	 */
	/**
	 * get String data
	 * @param key
	 * @return String data
	 * @throws JSONException
	 */
	public String getString(String key) throws JSONException {
		if (mJsonObject != null) {
			return mJsonObject.optString(key);
		} else if (mJsonArray != null) {
			JSONObject jsonObject = null;
			for (int i = 0; i < mJsonArray.length(); i++) {
				jsonObject = mJsonArray.getJSONObject(i);			
				String value = jsonObject.optString(key);
				if (value != null)
					return value;
			}
		}
		return null;
	}
	
	/**
	 * 静态方法获取key对应的value
	 * @param key
	 * @param jsonstr
	 * @return
	 * @throws JSONException
	 */
	public static String getString(String key, String jsonstr) {
		JsonUtils jsonUtils = JsonUtils.getInstance(jsonstr);
		if (jsonUtils.mJsonObject != null) {
			return jsonUtils.mJsonObject.optString(key);
		} else if (jsonUtils.mJsonArray != null) {
			JSONObject jsonObject = null;
			for (int i = 0; i < jsonUtils.mJsonArray.length(); i++) {
				try {
					jsonObject = jsonUtils.mJsonArray.getJSONObject(i);
				} catch (JSONException e) {
					e.printStackTrace();
				}			
				String value = jsonObject.optString(key);
				if (value != null)
					return value;
			}
		}
		return null;
	}

	/**
	 * get String data
	 * @param key
	 * @return
	 */
	public int getInt(String key) {
		if (mJsonObject != null) {
			try {
				return mJsonObject.getInt(key);
			} catch (JSONException e) {
				e.printStackTrace();
			}
		}
		return -1;
	}

	/**
	 * get double data
	 * @param key
	 * @return
	 */
	public double getDouble(String key) {
		if (mJsonObject != null) {
			try {
				return mJsonObject.getDouble(key);
			} catch (JSONException e) {
				e.printStackTrace();
			}
		}
		return -1;
	}
	
	/**
	 * 获取对象的简单调用
	 * @param c
	 * @return
	 */
	public Object getObject(Class<?> c) {
		return getObject(null, c);
	}
	
	/**
	 * 静态方法获取object
	 * @param c
	 * @param jsonString
	 * @return
	 */
	public static Object getObject(Class<?> c, String jsonString) {
		JsonUtils jsonUtils = JsonUtils.getInstance(jsonString);
		return jsonUtils.getObject(c);
	}
	
	/**
	 * This Method use in jsonObject get current class with object
	 * 
	 * @param jsonObject
	 * @param key
	 *            query key
	 * @param c
	 *            class
	 * @return object
	 * @throws Exception
	 */
	public Object getObject(String key, Class<?> c) {
		try {
			return getObject(mJsonObject, key, c);
		} catch (Exception e) {
			e.printStackTrace();
		}
		return null;
	}

	/**
	 * This Method use in jsonObject get current class with object
	 * 
	 * @param jsonObject
	 * @param key
	 *            query key
	 * @param c
	 *            class
	 * @param mc
	 * @return object
	 * @throws Exception
	 */
	public Object getObject(JSONObject jsonObject,
							String key,
							Class<?> c) throws Exception {
		if (DEBUG)
			Log.d(TAG, "key == " + key);

		JSONObject jsonObj = null;

		if (jsonObject == null) {
			if (DEBUG)
				Log.d(TAG, "current param jsonobject is null");
			return null;
		}

		if (key != null) {
			jsonObj = jsonObject.getJSONObject(key);
		} else {
			jsonObj = jsonObject;
		}

		if (jsonObj == null) {
			if (DEBUG)
				Log.d(TAG, "in jsonobject not key ");
			return null;
		}

		Object bean = null;
		if (!c.equals(null)) {
			if (DEBUG)
				Log.d(TAG, "jsonObj = " + jsonObj.toString());

			// 创建Class<?>类型的实例
			bean = c.newInstance();

			// 获取Class<?>的类名
			String className = c.getName();
			if (DEBUG)
				Log.d(TAG, "classname = " + className);

			// Java反映射解析Json字符串
			Field[] fs = c.getDeclaredFields();
			for (int i = 0; i < fs.length; i++) {
				Field field = fs[i];
				field.setAccessible(true);

				String keyString;
				keyString = field.getName();
				
				String value = jsonObj.optString(keyString);				
				if (DEBUG)
					Log.d(TAG, field.getName() + "=" + value);
		
				// 获取字域类型
				Class<?> type = field.getType();		
				// 如果value为"null"，或者为""但所属类型不是字符串类型，则直接跳过继续下一循环
				if (value == "null" 
						|| (value.equals("") && !type.equals(String.class)))
					continue;

				if (type.equals(String.class)) {
					field.set(bean, value);					
				} else if (type.equals(int.class)) {
					field.setInt(bean, Integer.parseInt(value));					
				} else if (type.equals(Date.class)) {
					field.set(bean, DateUtils.stringToDate(DateUtils.FORMAT_LONG, value));
				} else if (type.equals(double.class)) {
					field.setDouble(bean, Double.parseDouble(value));
				} else if (type.equals((long.class))) {
					field.setLong(bean, Long.parseLong(value));
				} else if (JSON_TYPE.JSON_TYPE_OBJECT == getJSONType(value)) {
					field.set(bean, getObject(new JSONObject(value), null, type));
				} else if (JSON_TYPE.JSON_TYPE_ARRAY == getJSONType(value)) {
					// Object中包含list，获取泛型类型
					Type type2 = field.getGenericType();
					if (ParameterizedType.class.isInstance(type2)) {
						// 转换为参数化的类型
						ParameterizedType parameterizedType = (ParameterizedType) type2;
						// 获取参数类型
						Type[] types = parameterizedType.getActualTypeArguments();
						// 打印所有参数化类型
						if (DEBUG) {
							for (int j = 0; j < types.length; j++) 
								Log.d(TAG, "type[" + j + "] = " + types[j]);
						}
						// 如果types[0]是class<?>，继续解析
						if (types[0] instanceof Class<?>) {
							field.set(bean, getList(field.getName(), 
													(Class<?>) types[0]));
						}
					}					
				}
			}
		} else {
			if (DEBUG)
				Log.d(TAG, "class is null");
			bean = jsonObj.opt(key);
		}

		return bean;
	}
	
	/**
	 * 获取列表
	 * @param c
	 * @param mc
	 * @return
	 */
	public List<?> getList(Class<?> c) {
		List<?> list = null;
		try {
			list = getList(mJsonArray, null, c);
		} catch (Exception e) {
			e.printStackTrace();
		}
		
		return list;
	}
	
	/**
	 * 静态方式获取对象列表
	 * @param c
	 * @param jsonString
	 * @return
	 */
	public static List<?> getList(Class<?> c, String jsonString) {
		JsonUtils jsonUtils = JsonUtils.getInstance(jsonString);
		return jsonUtils.getList(c);
	}
	
	/**
	 * 获取列表
	 * @param key
	 * @param c
	 * @param mc
	 * @return
	 */
	public List<?> getList(String key, Class<?> c) {
		List<?> list = null;
		try {
			list = getList(mJsonArray, key, c);
		} catch (Exception e) {
			e.printStackTrace();
		}
		
		return list;
	}
	
	/**
	 * 获取列表
	 * @param jsonArray
	 * @param c
	 * @param mc
	 * @return
	 */
	public List<?> getList(JSONArray jsonArray, Class<?> c) {
		List<?> list = null;
		try {
			list = getList(jsonArray, null, c);
		} catch (Exception e) {
			e.printStackTrace();
		}
		
		return list;
	}

	/**
	 * This method use in jsonObject get list object
	 * @param key
	 * @param objectKey
	 * @param c
	 * @param mc
	 * @return list
	 * @throws Exception
	 */
	public List<?> getList(JSONArray jsonArray, String key, Class<?> c) throws Exception {		
		List<Object> list = new ArrayList<Object>();
		// 空字符串或无效的json字符串
		if (jsonArray == null) {
			return list;
		}
		
		if (mJsonObject != null && key != null) {
			// 获取key对应的json数组对象
			jsonArray = mJsonObject.getJSONArray(key);
		}
		// jsonArray有有效数据
		if (jsonArray != null && !jsonArray.isNull(0)) {
			// 列表成员是Java自定义对象
			if (JSON_TYPE.JSON_TYPE_OBJECT 
							== getJSONType(jsonArray.get(0).toString())) {
				// 循环解析每一个数组成员
				for (int i = 0; i < jsonArray.length(); i++) {			
					JSONObject jsObject = jsonArray.getJSONObject(i);
					if (DEBUG)
						Log.d(TAG, "jsObject" + jsObject);
					Object object = getObject(jsObject, null, c);
					list.add(object);
				}
			// 列表元素还是列表
			} else if (JSON_TYPE.JSON_TYPE_ARRAY 
							== getJSONType(jsonArray.get(0).toString())) {
				for (int i = 0; i < jsonArray.length(); i++) {
					JSONArray jsonArray2 = jsonArray.getJSONArray(i);
					if (DEBUG)
						Log.d(TAG, "jsObject" + jsonArray.getJSONArray(i));
					list.add(getList(jsonArray2, null, c));
				}
			// 列表元素是普通对象
			} else {
				for (int i = 0; i < jsonArray.length(); i++) {
					String element = jsonArray.getString(i);
					if (!element.equals("")) {
						if (c.equals(Integer.class)) {
							list.add(Integer.parseInt(jsonArray.getString(i)));
						} else if (c.equals(Data.class)) {
							list.add(DateUtils.stringToDate(DateUtils.FORMAT_LONG, jsonArray.getString(i)));
						} else if (c.equals(Double.class)) {
							list.add(Double.parseDouble(jsonArray.getString(i)));
						} else if (c.equals(String.class)){
							list.add(jsonArray.getString(i));
						}
					}
				}
			}
		}
		return list;
	}

	/**
	 * Test class field value, equal obj.toString()
	 * 
	 * @param c
	 * @param classObject
	 * @throws IllegalArgumentException
	 * @throws IllegalAccessException
	 */
	public static String getFieldsValue(Class<?> c, Object obj)
			throws IllegalArgumentException, IllegalAccessException {
		StringBuffer sb = new StringBuffer();
		Field[] fs = c.getFields();

		for (int i = 0; i < fs.length; i++) {
			String s = fs[i].getName() + "=" + fs[i].get(obj);
			sb.append(s).append("\n");
		}
		if (DEBUG)
			Log.d(TAG, "getFieldValue = " + sb.toString());

		return sb.toString();
	}
	
	/**
	 * 判断json字符串是否存在指定的key
	 * @param key
	 * @param json
	 * @return
	 */
	public static boolean hasKey(String key, String json) {
		if (JsonUtils.getJSONType(json) == JSON_TYPE.JSON_TYPE_OBJECT) {
			if (DEBUG) Log.d(TAG, "JSON_TYPE.JSON_TYPE_OBJECT");
			try {
				JSONObject jsonObject = new JSONObject(json);
				return jsonObject.has(key);
			} catch (JSONException e) {				
				e.printStackTrace();
			}
		} else if (JsonUtils.getJSONType(json) == JSON_TYPE.JSON_TYPE_ARRAY) {			
			try {
				JSONArray jsonArray = new JSONArray(json);
				if (DEBUG) Log.d(TAG, "JSON_TYPE.JSON_TYPE_ARRAY, length = " + jsonArray.length());
				for (int i = 0; i < jsonArray.length(); i++) {
					String jsonString = jsonArray.getString(i);
					if (hasKey(key, jsonString))
						return true;
				}
			} catch (JSONException e) {
				e.printStackTrace();
			}
		} else {
			if (DEBUG) Log.d(TAG, "Invalid json type string!");
		}
		return false;
	}

	/* =================================== 将各种Java对象转换为Json字符串  =========================== */

	/**
	 * 将Java对象转换为json字符串，常用接口
	 * @param bean
	 * @return
	 */
	public static String toJson(Object bean) {
		return beanToJson(bean);
	}
	
	/**
	 * 将Object转换为Json字符串
	 * @param obj
	 * @return
	 */
	public static String objectToJson(Object obj) {
		StringBuilder json = new StringBuilder();
		
		if (obj == null) {
			json.append("\"\"");
		} else if (obj instanceof String || obj instanceof Integer
				|| obj instanceof Float || obj instanceof Boolean
				|| obj instanceof Short || obj instanceof Double
				|| obj instanceof Long || obj instanceof BigDecimal
				|| obj instanceof BigInteger || obj instanceof Byte) {
			json.append("\"").append(stringToJson(obj.toString())).append("\"");
		} else if (obj instanceof Object[]) {
			json.append(arrayToJson((Object[]) obj));
		} else if (obj instanceof List) {
			json.append(listToJson((List<?>) obj));
		} else if (obj instanceof Map) {
			json.append(mapToJson((Map<?, ?>) obj));
		} else if (obj instanceof Set) {
			json.append(setToJson((Set<?>) obj));
		} else if (obj instanceof BasicNameValuePair) {
			json.append(nameValueToJson((BasicNameValuePair) obj));
		} else if (obj instanceof Date) {
			json.append(dateToJson((Date) obj));
		} else {
			json.append(beanToJson(obj));
		}
		
		return json.toString();
	}
	
	/**
	 * 将Date转换为Json字符串
	 * @param obj
	 * @return
	 */
	public static String dateToJson(Date date) {
		return DateUtils.dateToString(DateUtils.FORMAT_SHORT, date);
	}

	/**
	 * 将Bean转换为Json字符串
	 * @param bean
	 * @return
	 */
	public static String beanToJson(Object bean) {
		StringBuilder json = new StringBuilder();
		json.append("{");
		
		Field fs[] = bean.getClass().getDeclaredFields();
		if (DEBUG) Log.d(TAG, "bean.getClass = " + bean.getClass().getName());
		if (fs != null) {
			for (int i = 0; i < fs.length; i++) {
				try {
					Field field = fs[i];
					field.setAccessible(true);
					String name = objectToJson(fs[i].getName());
					if (DEBUG) Log.d(TAG, "Name = " + name);
					String value = objectToJson(fs[i].get(bean));
					if (DEBUG) Log.d(TAG, "value = " + value);
					json.append(name);
					json.append(":");
					json.append(value);
					json.append(",");
				} catch (Exception e) {
					e.printStackTrace();
				}
			}
			json.setCharAt(json.length() - 1, '}');
		} else {
			json.append("}");
		}
		
		return json.toString();
	}

	/**
	 * 将list对象列表转换为Json字符串
	 * @param list
	 * @return
	 */
	public static String listToJson(List<?> list) {
		StringBuilder json = new StringBuilder();
		json.append("[");
		
		if (list != null && list.size() > 0) {
			for (Object obj : list) {
				json.append(objectToJson(obj));
				json.append(",");
			}
			json.setCharAt(json.length() - 1, ']');
		} else {
			json.append("]");
		}
		
		return json.toString();
	}

	/**
	 * 将数组转换为Json字符串
	 * @param array
	 * @return
	 */
	public static String arrayToJson(Object[] array) {
		StringBuilder json = new StringBuilder();
		json.append("[");
		
		if (array != null && array.length > 0) {
			for (Object obj : array) {
				json.append(objectToJson(obj));
				json.append(",");
			}		
			json.setCharAt(json.length() - 1, ']');
		} else {
			json.append("]");
		}
		
		return json.toString();
	}

	/**
	 * 将map对象转换为Json字符串
	 * @param map
	 * @return
	 */
	public static String mapToJson(Map<?, ?> map) {
		StringBuilder json = new StringBuilder();
		json.append("{");
		
		if (map != null && map.size() > 0) {
			for (Object key : map.keySet()) {
				json.append(objectToJson(key));
				json.append(":");
				json.append(objectToJson(map.get(key)));
				json.append(",");
			}		
			json.setCharAt(json.length() - 1, '}');
		} else {
			json.append("}");
		}
		
		return json.toString();
	}
	
	/**
	 * 将BasicNameValuePair对象转换为Json字符串
	 * @param map
	 * @return
	 */
	public static String nameValueToJson(BasicNameValuePair pair) {
		StringBuilder json = new StringBuilder();
		json.append("{");
		
		if (pair != null) {
			json.append(objectToJson(pair.getName()));
			json.append(":");
			json.append(objectToJson(pair.getValue()));
		}		
		json.append("}");
		
		return json.toString();
	}

	/**
	 * 将集合对象转换为Json字符串，很少使用
	 * @param set
	 * @return
	 */
	public static String setToJson(Set<?> set) {
		StringBuilder json = new StringBuilder();
		json.append("[");
		
		if (set != null && set.size() > 0) {
			for (Object obj : set) {
				json.append(objectToJson(obj));
				json.append(",");
			}
			json.setCharAt(json.length() - 1, ']');
		} else {
			json.append("]");
		}
		
		return json.toString();
	}

	/**
	 * 将字符串转换为Json格式字符串
	 * @param s
	 * @return
	 */
	@SuppressLint("DefaultLocale") public static String stringToJson(String s) {
		if (s == null)
			return "";
		
		StringBuilder sb = new StringBuilder();
		
		for (int i = 0; i < s.length(); i++) {
			char ch = s.charAt(i);
			switch (ch) {
			case '"':
				sb.append("\\\"");
				break;
			case '\\':
				sb.append("\\\\");
				break;
			case '\b':
				sb.append("\\b");
				break;
			case '\f':
				sb.append("\\f");
				break;
			case '\n':
				sb.append("\\n");
				break;
			case '\r':
				sb.append("\\r");
				break;
			case '\t':
				sb.append("\\t");
				break;
			case '/':
				sb.append("\\/");
				break;
			default:
				if (ch >= '\u0000' && ch <= '\u001F') {
					String ss = Integer.toHexString(ch);
					sb.append("\\u");
					for (int k = 0; k < 4 - ss.length(); k++) {
						sb.append('0');
					}
					sb.append(ss.toUpperCase());
				} else {
					sb.append(ch);
				}
			}
		}
		
		return sb.toString();
	}
}
