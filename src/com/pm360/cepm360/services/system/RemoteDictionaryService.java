package com.pm360.cepm360.services.system;

import com.google.gson.reflect.TypeToken;
import com.pm360.cepm360.app.common.activity.DataManagerInterface;
import com.pm360.cepm360.common.json.AnalysisManager;
import com.pm360.cepm360.entity.Dictionary;
import com.pm360.cepm360.services.common.AsyncTaskPM360;
import com.pm360.cepm360.services.common.Operation;
import com.pm360.cepm360.services.common.RemoteService;

import java.lang.reflect.Type;
import java.util.List;

public class RemoteDictionaryService {
	// 字典服务的服务类全限定名
	private static final String SERVICE_NAME = "com.pm360.cepm360.services.system.DictionaryService";
	// 单例实例对象
	private static RemoteDictionaryService gService;
	
	/**
	 * 单例模式
	 * @return
	 */
    public static synchronized RemoteDictionaryService getInstance() {
        if (gService == null) {
            gService = new RemoteDictionaryService();
        }
        return gService;
    }
    
    /**
     * 销毁实例对象
     */
    public static void destroyInstance() {
    	gService = null;
    }
    
    private RemoteDictionaryService() {
    	
    }
    
    /**
     * 添加一个字典或字典类型
     * @param manager
     * @param dictionary
     * @return
     */
    public AsyncTaskPM360 addDictionary(final DataManagerInterface manager, Dictionary dictionary) {
    	// 初始化返回类型
    	Type type = AnalysisManager.GSON 
				? new TypeToken<List<Dictionary>>() {}.getType()
						: Dictionary.class;
				
		// 设置参数，调用远程服务
    	return new RemoteService()
    			.setParams( SERVICE_NAME,
    						"addDictionary",
    						dictionary)
    			.call(manager, type, Operation.ADD);
    }
    
    /**
     * 获取一个字典
     * @param manager
     * @param dictionary
     * @return
     */
    public AsyncTaskPM360 getDictionary(final DataManagerInterface manager, Dictionary dictionary) {
    	// 获取类型
    	Type type = AnalysisManager.GSON 
				? new TypeToken<List<Dictionary>>() {}.getType()
						: Dictionary.class;
		// 设置参数，调用远程服务
    	return new RemoteService()
    			.setParams( SERVICE_NAME,
    						"getDictionary",
    						dictionary.getDictionary_id())
    			.call(manager, type, Operation.QUERY);
    }

    /**
     * 获取所有字典类型
     * @param manager
     * @return
     */
    public AsyncTaskPM360 getModule(final DataManagerInterface manager) {
    	// 获取返回类型
    	Type type = AnalysisManager.GSON 
				? new TypeToken<List<Dictionary>>() {}.getType()
						: Dictionary.class;
    	// 设置参数，调用远程服务
    	return new RemoteService()
    			.setParams( SERVICE_NAME,
    						"getModule")
    			.call(manager, type, Operation.QUERY);
    }
}
