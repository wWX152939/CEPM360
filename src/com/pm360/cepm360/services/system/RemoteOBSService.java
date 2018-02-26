package com.pm360.cepm360.services.system;

import com.google.gson.reflect.TypeToken;
import com.pm360.cepm360.app.common.activity.DataManagerInterface;
import com.pm360.cepm360.common.json.AnalysisManager;
import com.pm360.cepm360.entity.OBS;
import com.pm360.cepm360.entity.User;
import com.pm360.cepm360.services.common.AsyncTaskPM360;
import com.pm360.cepm360.services.common.Operation;
import com.pm360.cepm360.services.common.RemoteService;

import java.lang.reflect.Type;
import java.util.List;

public class RemoteOBSService {
	// 框架服务类全限定名
	private static final String SERVICE_NAME = "com.pm360.cepm360.services.system.OBSService";
	// 单例类变量
	private static RemoteOBSService gService;
	
	/**
	 * 单例模式
	 * @return
	 */
    public static synchronized RemoteOBSService getInstance() {
        if (gService == null) {
            gService = new RemoteOBSService();
        }
        return gService;
    }
    
    /**
     * 销毁实例对象
     */
    public static void destroyInstance() {
    	gService = null;
    }
    
    private RemoteOBSService() {
    	
    }

    // --------------------------- 接口 ------------------------------------ //
    
    /**
     * 获取OBS组织结构列表
     * @param manager
     */
    public AsyncTaskPM360 getOBSList(final DataManagerInterface manager, User user) {
    	return getOBSList(manager, user.getTenant_id());
    }
    
    /**
     * 获取OBS组织结构列表
     * @param manager
     */
    public AsyncTaskPM360 getOBSList(final DataManagerInterface manager, int tenantId) {
    	// 初始化返回类型
    	Type type = AnalysisManager.GSON 
				? new TypeToken<List<OBS>>() {}.getType()
						: OBS.class;
 		// 设置调用参数，调用远程服务	
 		return new RemoteService()
 				.setParams( SERVICE_NAME,
 							"getOBSList",
 							tenantId)
 				.call(manager, type, Operation.QUERY);
    }
    
    /**
     * 添加一个OBS
     * @param manager
     * @param obs
     */
    public AsyncTaskPM360 addOBS(final DataManagerInterface manager, OBS obs) {
    	// 初始化返回类型
    	Type type = AnalysisManager.GSON 
				? new TypeToken<List<OBS>>() {}.getType()
						: OBS.class;
 		// 设置调用参数，调用远程服务	
 		return new RemoteService()
 				.setParams( SERVICE_NAME,
 							"addOBS",
 							obs)
 				.call(manager, type, Operation.ADD);
    }
    
    /**
     * 删除一个OBS
     * @param manager
     * @param obs
     */
    public AsyncTaskPM360 deleteOBS(final DataManagerInterface manager, OBS obs) {
 		// 设置调用参数，调用远程服务	
 		return new RemoteService()
 				.setParams( SERVICE_NAME,
 							"deleteOBS",
 							obs.getObs_id())
 				.call(manager, Operation.DELETE);
    }
    
    /**
     * 修改一个OBS
     * @param manager
     * @param obs
     */
    public AsyncTaskPM360 updateOBS(final DataManagerInterface manager, OBS obs) {
 		// 设置调用参数，调用远程服务	
 		return new RemoteService()
 				.setParams( SERVICE_NAME,
 							"updateOBS",
 							obs)
 				.call(manager, Operation.MODIFY);
    }
}
