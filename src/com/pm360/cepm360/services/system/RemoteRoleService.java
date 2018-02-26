package com.pm360.cepm360.services.system;

import com.google.gson.reflect.TypeToken;
import com.pm360.cepm360.app.common.activity.DataManagerInterface;
import com.pm360.cepm360.common.json.AnalysisManager;
import com.pm360.cepm360.entity.Role;
import com.pm360.cepm360.services.common.AsyncTaskPM360;
import com.pm360.cepm360.services.common.Operation;
import com.pm360.cepm360.services.common.RemoteService;

import java.lang.reflect.Type;
import java.util.List;

public class RemoteRoleService {
	// 框架服务类全限定名
	private static final String SERVICE_NAME = "com.pm360.cepm360.services.system.RoleService";
	// 单例类变量
	private static RemoteRoleService gService;
	
	/**
	 * 单例模式
	 * @return
	 */
    public static synchronized RemoteRoleService getInstance() {
        if (gService == null) {
            gService = new RemoteRoleService();
        }
        return gService;
    }
    
    /**
     * 销毁实例对象
     */
    public static void destroyInstance() {
    	gService = null;
    }
    
    private RemoteRoleService() {
    	
    }

    // --------------------------- 接口 ------------------------------------ //
    
    /**
     * 获取角色组织结构列表
     * @param manager
     */
    public AsyncTaskPM360 getRoleList(final DataManagerInterface manager,Role role) {
    	// 初始化返回类型
    	Type type = AnalysisManager.GSON 
				? new TypeToken<List<Role>>() {}.getType()
						: Role.class;
 		// 设置调用参数，调用远程服务	
 		return new RemoteService()
 				.setParams( SERVICE_NAME,
 							"getRole",
 							role)
 				.call(manager, type, Operation.QUERY);
    }
    
    /**
     * 添加一个角色
     * @param manager
     * @param user
     */
    public AsyncTaskPM360 addRole(final DataManagerInterface manager, Role role) {
    	// 初始化返回类型
    	Type type = AnalysisManager.GSON 
				? new TypeToken<List<Role>>() {}.getType()
						: Role.class;
 		// 设置调用参数，调用远程服务	
 		return new RemoteService()
 				.setParams( SERVICE_NAME,
 							"addRole",
 							role)
 				.call(manager, type, Operation.ADD);
    }
    
    /**
     * 删除一个角色
     * @param manager
     * @param user
     */
    public AsyncTaskPM360 deleteRole(final DataManagerInterface manager, Role role) {
 		// 设置调用参数，调用远程服务	
 		return new RemoteService()
 				.setParams( SERVICE_NAME,
 							"deleteRole",
 							role)
 				.call(manager, Operation.DELETE);
    }
    
    /**
     * 修改一个角色
     * @param manager
     * @param user
     */
    public AsyncTaskPM360 updateRole(final DataManagerInterface manager, Role role) {
 		// 设置调用参数，调用远程服务	
 		return new RemoteService()
 				.setParams( SERVICE_NAME,
 							"updateRole",
 							role)
 				.call(manager, Operation.MODIFY);
    }
}
