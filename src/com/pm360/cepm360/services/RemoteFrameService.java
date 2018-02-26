
package com.pm360.cepm360.services;

import com.google.gson.reflect.TypeToken;
import com.pm360.cepm360.app.common.activity.DataManagerInterface;
import com.pm360.cepm360.common.json.AnalysisManager;
import com.pm360.cepm360.entity.Project;
import com.pm360.cepm360.entity.User;
import com.pm360.cepm360.services.common.AsyncTaskPM360;
import com.pm360.cepm360.services.common.Operation;
import com.pm360.cepm360.services.common.RemoteService;

import java.lang.reflect.Type;
import java.util.List;

/**
 * 框架服务接口
 * @author Andy
 */
public class RemoteFrameService {
	// 框架服务类全限定名
	private static final String SERVICE_NAME = "com.pm360.cepm360.services.FrameService";
	// 单例类变量
    private static RemoteFrameService gService;

    /**
     * 单例模式
     * @return
     */
    public static synchronized RemoteFrameService getInstance() {
        if (gService == null) {
            gService = new RemoteFrameService();
        }
        return gService;
    }
    
    /**
     * 销毁实例对象
     */
    public static void destroyInstance() {
    	gService = null;
    }

    private RemoteFrameService() {

    }

    /**
     * 获取工程的基本信息
     * 
     * @param manager UI回调接口
     * @return 
     */
    public AsyncTaskPM360 getProjectList(final DataManagerInterface manager, User user) {
    	// 获取返回类型
		Type type = AnalysisManager.GSON 
						? new TypeToken<List<Project>>() {}.getType()
								: Project.class;				
		// 设置参数，调用远程服务
		return new RemoteService()
				.setParams( SERVICE_NAME,
							"getProjectList",
							user.getUser_id())
				.call(manager, type, Operation.QUERY);
    }
    
    /**
     * 登录后获取默认的工程
     * @param manager
     */
    public AsyncTaskPM360 getDefaultProject(final DataManagerInterface manager, User user) {
    	// 获取返回类型
		Type type = AnalysisManager.GSON 
						? new TypeToken<List<Project>>() {}.getType()
								: Project.class;			
		// 设置参数，调用远程服务
		return new RemoteService()
				.setParams( SERVICE_NAME,
							"getDefaultProject",
							user.getUser_id())
				.call(manager, type, Operation.QUERY);	
    }
}
