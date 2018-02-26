package com.pm360.cepm360.common.util;

import android.util.Log;

import java.lang.reflect.Field;
import java.math.BigDecimal;
import java.math.BigInteger;
import java.text.DecimalFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

public class MiscUtils {
	private static final String TAG = "BeanToMap";
	private static final boolean DBG = false;
	
	/**
	 * 将Java的普通对象转换为字符串
	 * @param object
	 * @param formate
	 * @return
	 */
	private static String objectToString(Object object, String formate) {
		StringBuilder value = new StringBuilder();	
		if (object != null) {
			if (object instanceof String 
					|| object instanceof Integer
					|| object instanceof Boolean
					|| object instanceof Short 
					|| object instanceof Long 
					|| object instanceof Float 
					|| object instanceof BigDecimal
					|| object instanceof BigInteger 
					|| object instanceof Byte) {
				value.append(object.toString());
			} else if (object instanceof Double) {
				DecimalFormat decimalFormat = new DecimalFormat("###################.###########");
				String decimalString = decimalFormat.format(object);
				value.append(decimalString);
			} else if (object instanceof Date) {
				String dataString;
				if (formate != null) {
					dataString = DateUtils
							.dateToString(formate, (Date) object);
				} else {
					dataString = DateUtils
							.dateToString(DateUtils.FORMAT_SHORT, (Date) object);
				}
				value.append(dataString);
			}
		}
		return value.toString();
	}
	
	/**
	 * 将自定义Java对象转换为map
	 * @param bean
	 * @return
	 */
	public static Map<String, String> beanToMap(Object bean) {
		return beanToMap(bean, null);
	}
	
	/**
	 * 将自定义Java对象转换为map
	 * @param bean
	 * @param formate Date的解析格式
	 * @return
	 */
	public static Map<String, String> beanToMap(Object bean, String formate) {
		Map<String, String> stringMap = new HashMap<String, String>();
		
		Field fs[] = bean.getClass().getDeclaredFields();
		if (DBG) Log.d(TAG, "bean.getClass = " + bean.getClass().getName());
		
		if (fs != null) {
			for (int i = 0; i < fs.length; i++) {
				try {
					Field field = fs[i];
					field.setAccessible(true);
					String name = fs[i].getName();
					String value = objectToString(field.get(bean), formate);
					if (DBG) Log.d(TAG, "name = " + name 
										+ " value = " + value);
					stringMap.put(name, value);
				} catch (Exception e) {
					e.printStackTrace();
				}
			}
		}
		return stringMap;
	}
	
	/**
	 * 深度拷贝
	 * @param object
	 * @return
	 */
	@SuppressWarnings("deprecation")
	private static Object targetObject(Object object) {
		Object target = null;
		if (object != null) {
			if (object instanceof Integer
					|| object instanceof Float 
					|| object instanceof Boolean
					|| object instanceof Short 
					|| object instanceof Double
					|| object instanceof Long 
					|| object instanceof BigDecimal
					|| object instanceof BigInteger 
					|| object instanceof Byte
					|| object instanceof String) {
				target = object;
			} else if (object instanceof Date) {
				Date date = (Date) object;
				target = new Date(	date.getYear(), 
									date.getMonth(), 
									date.getDate(),
									date.getHours(),
									date.getMinutes(),
									date.getSeconds());
			} else {
				target = clone(object);
			}
		}
		return target;
	}

	/**
	 * 深拷贝
	 * @param src
	 * @return
	 */
	@SuppressWarnings("unchecked")
	public static <T> T clone(T src) {
		T target = null;
		try {
			target = (T) src.getClass().newInstance();
		} catch (InstantiationException e1) {
			e1.printStackTrace();
		} catch (IllegalAccessException e1) {
			e1.printStackTrace();
		}
		
		Field fs[] = target.getClass().getDeclaredFields();
		if (DBG) Log.d(TAG, "bean.getClass = " + src.getClass().getName());
		
		if (fs != null) {
			for (int i = 0; i < fs.length; i++) {
				try {
					Field field = fs[i];
					field.setAccessible(true);
					field.set(target, targetObject(field.get(src)));
				} catch (Exception e) {
					e.printStackTrace();
				}
			}
		}
		return target;
	}
	
	/**
	 * 深拷贝
	 * @param src
	 * @return
	 */
	public static <T> T clone(T target, T src) {		
		Field fs[] = target.getClass().getDeclaredFields();
		if (DBG) Log.d(TAG, "bean.getClass = " + src.getClass().getName());
		
		if (fs != null) {
			for (int i = 0; i < fs.length; i++) {
				try {
					Field field = fs[i];
					field.setAccessible(true);
					field.set(target, targetObject(field.get(src)));
				} catch (Exception e) {
					e.printStackTrace();
				}
			}
		}
		return target;
	}
	
	/**
	 * 字符串数组连接 c = a + b
	 */
	public static String[] concat(String[] a, String[] b) {  
	   String[] c = new String[a.length + b.length];  
	   System.arraycopy(a, 0, c, 0, a.length);  
	   System.arraycopy(b, 0, c, a.length, b.length);  
	   return c;  
	}
	
	/**
	 * 计算日期差，如果参数无效，返回int最小值
	 * @param start
	 * @param end
	 * @return
	 */
	public static int calcDateSub(String start, String end) {
		Date startDate = null;
		Date endDate = null;
		
		if (start != null && !start.isEmpty()
					&& end != null && !end.isEmpty()) {
			startDate = DateUtils.stringToDate(DateUtils.FORMAT_SHORT, start);
			endDate = DateUtils.stringToDate(DateUtils.FORMAT_SHORT, end);
		}
		
		return calcDateSub(startDate, endDate);
	}
	
	/**
	 * 计算日期差，如果参数无效，返回int最小值
	 * @param start
	 * @param end
	 * @return
	 */
	public static int calcDateSub(Date start, Date end) {
		long startTime = 0;
		long endTime = 0;
		
		if (start != null && end != null) {
			startTime = start.getTime(); 
			endTime = end.getTime();
			return (int) ((endTime - startTime) / (1000 * 60 * 60 * 24));
		} else {
			return Integer.MIN_VALUE;
		}
	}
}
