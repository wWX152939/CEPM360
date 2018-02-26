package com.pm360.cepm360.common.json;

import android.annotation.SuppressLint;
import android.util.Log;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonDeserializationContext;
import com.google.gson.JsonDeserializer;
import com.google.gson.JsonElement;
import com.google.gson.JsonParseException;
import com.google.gson.JsonPrimitive;
import com.google.gson.JsonSerializationContext;
import com.google.gson.JsonSerializer;
import com.google.gson.reflect.TypeToken;
import com.pm360.cepm360.common.util.DateUtils;

import java.lang.reflect.Type;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;
import java.util.Map;

/**
 * Gson的工具类
 * @author yuanlu
 * @version 1.0
 * @since 2015-4-29 
 */
final public class GsonUtils {
	private static final String TAG = "GsonUtils";
	private static final boolean DEBUG = true;
	private static Gson gson = null;
	
	/**
	 * 创建Gson对象
	 */
    static {
        if (gson == null) {
        	/* 创建Gson对象 */     	
//        	gson = new Gson();
    		gson = new GsonBuilder()
//            		.setVersion(1.1)	// 设置版本
//            		.excludeFieldsWithoutExposeAnnotation()	// 不解析@expose标识的字段
//            		.enableComplexMapKeySerialization()	 	// 使能复杂的Map<key, value>解析方式
//            		.serializeNulls()		// 输出空引用
//        			.setPrettyPrinting()	// 控制缩进，在转换成json字符串时添加缩进功能，一般只有打印时需要
//            		.registerTypeAdapter(Date.class, new DateSerializerAdapter())	// 自定义序列化反序列化Date
            		.setDateFormat(DateUtils.FORMAT_SHORT)
            		.create();
        }
    }

    /**
     * 不需要外部通过构造函数使用GsonUtils
     */
    private GsonUtils() {
    	
    }
    
    /** 
     * 将Java对象转换为json字符串 
     * @param ts 
     * @return JsonString
     */  
    public static String toJson(Object ts) {
        String jsonStr = null;
        if (gson != null) {
        	Log.d(TAG, "gson != null");
            jsonStr = gson.toJson(ts);
            Log.d(TAG, "jsonStr = " + jsonStr);
        }
        return jsonStr;
    }
 
    /** 
     * 将json字符串转换为list 
     * @param jsonStr 
     * @return 
     */  
    public static List<?> jsonToList(String jsonStr) {
    	if (DEBUG) Log.d(TAG, "jsonToList: " + jsonStr);
        List<?> objList = null;  
        if (gson != null) {
            Type type = new TypeToken<List<?>>() {  
            }.getType();  
            objList = gson.fromJson(jsonStr, type);  
        }  
        return objList;
    }
      
    /** 
     * 将json字符串解析为指定类型的list
     * @param jsonStr 
     * @param type 
     * @return 
     */  
    public static List<?> jsonToList(String jsonStr, Type type) {
        List<?> objList = null;  
        if (gson != null) {  
            objList = gson.fromJson(jsonStr, type);  
        }  
        return objList;  
    }
  
    /** 
     * 将json字符串转换为Map
     * @param jsonStr 
     * @return 
     */  
    public static Map<?, ?> jsonToMap(String jsonStr) {
    	if (DEBUG) Log.d(TAG, "jsonToMap: " + jsonStr);
        Map<?, ?> objMap = null;
        if (gson != null) {
            Type type = new TypeToken<Map<?, ?>>() {
            }.getType();
            objMap = gson.fromJson(jsonStr, type);
        }
        return objMap;  
    }
    
    /** 
     * 将json字符串转换为指定类型的map
     * @param jsonStr 
     * @return 
     */  
    public static Map<?, ?> jsonToMap(String jsonStr, Type type) {
        Map<?, ?> objMap = null;
        if (gson != null) {
            objMap = gson.fromJson(jsonStr, type);
        }
        return objMap;
    }
  
    /** 
     * 将json字符串转换为指定类型的Java对象
     * @param jsonStr 
     * @return 
     */  
    public static Object jsonToBean(String jsonStr, Class<?> cl) {
        Object obj = null;
        if (gson != null) {
            obj = gson.fromJson(jsonStr, cl);
        }
        return obj;
    }
    
    /** 
     * 获取指定key的value对象
     * @param jsonStr 
     * @param key
     * @return 
     */  
    public static Object getValueFromMap(String jsonStr, String key) {
        Object rulsObj = null;
        Map<?, ?> rulsMap = jsonToMap(jsonStr);
        if (rulsMap != null && rulsMap.size() > 0) {
            rulsObj = rulsMap.get(key);
        }
        return rulsObj;
    }
    
    /**
     * 获取指定key的value对象
     * @param jsonStr
     * @param key
     * @return
     */
    public static Object getValueFromList(String jsonStr, String key) {
        Object rulsObj = null;
        List<?> rulsList = jsonToList(jsonStr);
        if (rulsList != null && rulsList.size() == 1) {
        	rulsObj = getValueFromMap(toJson(rulsList.get(0)), key);           
        }
        return rulsObj;
    }
    
    /** 
     * 序列化含有Data对象的对象
     * @param ts 
     * @return 
     */  
    @SuppressLint("SimpleDateFormat") 
    public static String objectToJsonDateSerializer(Object ts,
    			final String dateformat) {
        String jsonStr = null;     
        // 创建一个自定义序列化的Gson对象，指定Data类型的序列化方式
        Gson gson = new GsonBuilder()
                .registerTypeHierarchyAdapter(Date.class,
                        new JsonSerializer<Date>() {
							@Override
							public JsonElement serialize(Date src, Type arg1,
									JsonSerializationContext arg2) {
                                SimpleDateFormat format = new SimpleDateFormat(
                                        dateformat);
                                return new JsonPrimitive(format.format(src));
							}
                        }).setDateFormat(dateformat).create();
        if (gson != null) {
            jsonStr = gson.toJson(ts);
        }
        
        return jsonStr;
    }

    /** 
     * 反序列化含有Data的json字符串
     * @param jsonStr 
     * @param cl 
     * @return 
     */  
    @SuppressWarnings("unchecked")
	@SuppressLint("SimpleDateFormat")
    public static <T> T jsonToBeanDateSerializer(String jsonStr, Class<T> cl,
            final String pattern) {
        Object obj = null;       
        // 创建一个自定义序列化的Gson对象，指定Data对象的反序列化方式
        Gson gson = new GsonBuilder()
                .registerTypeAdapter(Date.class, new JsonDeserializer<Date>() {
					@Override
					public Date deserialize(JsonElement json, Type arg1,
							JsonDeserializationContext arg2)
							throws JsonParseException {
						SimpleDateFormat format = new SimpleDateFormat(pattern);
                        String dateStr = json.getAsString();
                        try {
                            return format.parse(dateStr);
                        } catch (java.text.ParseException e) {
							e.printStackTrace();
						}
						return null;
					}  
                }).setDateFormat(pattern).create();
        if (gson != null) {
            obj = gson.fromJson(jsonStr, cl);
        }     
        
        return (T) obj;
    }
    
    /** 
     * Enum的序列化方法
     * @param jsonStr, cl
     * @return Object
     */  
    public static Object jsonToObjectSerializer(String jsonStr, Class<?> cl) {
        Object obj = null;
        Gson gson = new GsonBuilder()
                .registerTypeHierarchyAdapter(PackageState.class, 
                		new EnumSerializer()).create();
        if (gson != null) {
            obj = gson.fromJson(jsonStr, cl);
        }
        
        return obj;
    }
}
