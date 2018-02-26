package com.pm360.cepm360.services.system;

import com.google.gson.reflect.TypeToken;
import com.pm360.cepm360.app.common.activity.DataManagerInterface;
import com.pm360.cepm360.common.json.AnalysisManager;
import com.pm360.cepm360.entity.EPS;
import com.pm360.cepm360.entity.User;
import com.pm360.cepm360.services.common.AsyncTaskPM360;
import com.pm360.cepm360.services.common.Operation;
import com.pm360.cepm360.services.common.RemoteService;

import java.lang.reflect.Type;
import java.util.List;

public class RemoteEPSService {
	// 框架服务类全限定名
	private static final String SERVICE_NAME = "com.pm360.cepm360.services.system.EPSService";
	// 单例类变量
	private static RemoteEPSService gService;
	
	/**
	 * 单例模式
	 * @return
	 */
    public static synchronized RemoteEPSService getInstance() {
        if (gService == null) {
            gService = new RemoteEPSService();
        }
        return gService;
    }
    
    /**
     * 销毁实例对象
     */
    public static void destroyInstance() {
    	gService = null;
    }
    
    private RemoteEPSService() {
    	
    }

    // --------------------------- 接口 ------------------------------------ //
    
    /**
     * 获取EPS组织结构列表
     * @param manager
     */
    public AsyncTaskPM360 getEPSList(final DataManagerInterface manager, User user) {
    	// 初始化返回类型
    	Type type = AnalysisManager.GSON 
				? new TypeToken<List<EPS>>() {}.getType()
						: EPS.class;
 		// 设置调用参数，调用远程服务	
 		return new RemoteService()
 				.setParams( SERVICE_NAME,
 							"getEPS",
 							user.getTenant_id())
 				.call(manager, type, Operation.QUERY);
    }
    
    /**
     * 添加一个EPS
     * @param manager
     * @param eps
     */
    public AsyncTaskPM360 addEPS(final DataManagerInterface manager, EPS eps) {
    	// 初始化返回类型
    	Type type = AnalysisManager.GSON 
				? new TypeToken<List<EPS>>() {}.getType()
						: EPS.class;
 		// 设置调用参数，调用远程服务	
 		return new RemoteService()
 				.setParams( SERVICE_NAME,
 							"addEPS",
 							eps)
 				.call(manager, type, Operation.ADD);
    }
    
    /**
     * 删除一个EPS
     * @param manager
     * @param eps
     */
    public AsyncTaskPM360 deleteEPS(final DataManagerInterface manager, EPS eps) {
 		// 设置调用参数，调用远程服务	
 		return new RemoteService()
 				.setParams( SERVICE_NAME,
 							"deleteEPS",
 							eps.getEps_id())
 				.call(manager, Operation.DELETE);
    }
    
    /**
     * 修改一个EPS
     * @param manager
     * @param eps
     */
    public AsyncTaskPM360 updateEPS(final DataManagerInterface manager, EPS eps) {
 		// 设置调用参数，调用远程服务	
 		return new RemoteService()
 				.setParams( SERVICE_NAME,
 							"updateEPS",
 							eps)
 				.call(manager, Operation.MODIFY);
    }
}
