package com.pm360.cepm360.common.json;

import android.util.Log;

import com.google.gson.JsonDeserializationContext;
import com.google.gson.JsonDeserializer;
import com.google.gson.JsonElement;
import com.google.gson.JsonParseException;
import com.google.gson.JsonPrimitive;
import com.google.gson.JsonSerializationContext;
import com.google.gson.JsonSerializer;
import com.pm360.cepm360.common.util.DateUtils;

import java.lang.reflect.Type;
import java.text.SimpleDateFormat;
import java.util.Date;

/**
 * 自定义Date序列化和反序列化适配器
 * @author yuanlu
 *
 */
public class DateSerializerAdapter 
				implements JsonSerializer<Date>, JsonDeserializer<Date> {
	private static final String TAG = "DateSerializerAdapter";
	private static SimpleDateFormat format = null;
	// 初始化类变量
	static {
		if (format == null)
			format = new SimpleDateFormat(DateUtils.FORMAT_SHORT);
	}
	
	@Override
	public JsonElement serialize(Date src, Type arg1,
			JsonSerializationContext arg2) {
		Log.d(TAG, "Enter serialize");
	    return new JsonPrimitive(format.format(src));
	}

	@Override
	public Date deserialize(JsonElement json, Type arg1,
			JsonDeserializationContext arg2) throws JsonParseException {
		Log.d(TAG, "Enter deserialize");
        String dateStr = json.getAsString();
        try {
            return format.parse(dateStr);
        } catch (java.text.ParseException e) {
			e.printStackTrace();
		}
		return null;
	}
}
