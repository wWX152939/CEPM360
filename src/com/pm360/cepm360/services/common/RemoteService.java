package com.pm360.cepm360.services.common;

import com.pm360.cepm360.app.common.activity.DataManagerInterface;
import com.pm360.cepm360.common.GLOBAL;

import java.io.File;
import java.lang.reflect.Type;
import java.util.HashMap;
import java.util.Map;

public class RemoteService {
	// 参数map
	private Map<String, Object> params = null;
	
	public RemoteService() {
		
	}
	
	/**
	 * 设置远程调用参数
	 * @param className
	 * @param methodName
	 * @param objects
	 * @return
	 */
	public RemoteService setParams(String className, String methodName, Object...objects) {
		params = new HashMap<String, Object>();
		// 添加远程调用类名和方法名
		if (className != null && methodName != null) {
	    	params.put(GLOBAL.CLASS_PARAM, className);
	    	params.put(GLOBAL.METHOD_PARAM, methodName);
		}
    	
    	// 添加远程方法参数
    	for (int i = 0; i < objects.length; i++)
    		params.put("p" + i, objects[i]);
    		
		return this;
	}
	
	/**
	 * 调用远程服务，用于不需要返回数据的调用
	 * @param manager
	 * @param operation
	 */
	public AsyncTaskPM360 call(DataManagerInterface manager, 
								Operation operation) {
		return call(manager, (Type) null, operation);
	}
	
	/**
	 * 调用远程服务
	 * @param manager
	 * @param type 指定要返回数据的类型
	 * @param operation
	 */
	public AsyncTaskPM360 call(DataManagerInterface manager, 
								Type type, 
								Operation operation) {
		AsyncTaskPM360 asyncTaskPM360 = 
				new AsyncTaskPM360(manager, type, operation);
		
		asyncTaskPM360.execute(params);
		return asyncTaskPM360;
	}
	
	/**
	 * 文件上传和下载
	 * @param manager
	 * @param type
	 * @param operation
	 * @param file
	 */
	public AsyncTaskPM360 call(DataManagerInterface manager,
								Operation operation,
								File file) {
		return call(manager, operation, file, null);
	}
	
	/**
	 * 文件上传和下载
	 * @param manager
	 * @param type
	 * @param operation
	 * @param file
	 * @param progressDialog
	 */
	public AsyncTaskPM360 call(DataManagerInterface manager,
								Operation operation,
								File file, 
								Object progress) {
		AsyncTaskPM360 asyncTaskPM360 
				= new AsyncTaskPM360(manager, operation);
		
    	asyncTaskPM360
    			.setProgressObject(progress)
    			.execute(params, file);
    	
    	return asyncTaskPM360;
	}
}
