package com.pm360.cepm360.services.plan;

import com.google.gson.reflect.TypeToken;
import com.pm360.cepm360.app.common.activity.DataManagerInterface;
import com.pm360.cepm360.common.json.AnalysisManager;
import com.pm360.cepm360.entity.Logic;
import com.pm360.cepm360.services.common.AsyncTaskPM360;
import com.pm360.cepm360.services.common.Operation;
import com.pm360.cepm360.services.common.RemoteService;

import java.lang.reflect.Type;
import java.util.List;

public class RemoteLogicService {
	// 任务服务的类全限定名
	private static final String SERVICE_NAME = "com.pm360.cepm360.services.plan.LogicService";
	// 单例对象类变量
    private static RemoteLogicService gService;
    // 当前任务逻辑
    private Logic mLogic;

    /**
     * 单例模式
     * @return
     */
    public static synchronized RemoteLogicService getInstance() {
        if (gService == null) {
            gService = new RemoteLogicService();
        }
        return gService;
    }
    
    /**
     * 销毁实例对象
     */
    public static void destroyInstance() {
    	gService = null;
    }

    /**
     * 禁止外部通过构造函数实例化对象
     */
    private RemoteLogicService() {

    }
    
    /**
     * 设置当前任务逻辑
     * @param logic
     * @return
     */
    public RemoteLogicService setLogic(Logic logic) {
    	mLogic = logic;
    	return this;
    }
    
    /**
     * 获取当前任务逻辑
     * @return
     */
    public Logic getLogic() {
    	return mLogic;
    }
    
    /**
     * 获取任务的前或后置逻辑列表
     * @param manager
     * @param logic_type 前后置类型
     */
    public AsyncTaskPM360 getLogicList(final DataManagerInterface manager, int logic_type) {
    	// 初始化返回类型
    	Type type = AnalysisManager.GSON 
				? new TypeToken<List<Logic>>() {}.getType()
						: Logic.class;
		// 设置调用参数，调用远程服务	
		return new RemoteService()
				.setParams( SERVICE_NAME,
							"getLogicList",
							logic_type,
							RemoteTaskService.getInstance().getTask().getTask_id())
				.call(manager, type, Operation.QUERY);
    }
    
    /**
     * 给一个任务添加一个逻辑任务
     * @param manager
     * @param logic
     */
    public AsyncTaskPM360 addFLogic(final DataManagerInterface manager, Logic logic) {
    	// 初始化返回类型
    	Type type = AnalysisManager.GSON 
				? new TypeToken<List<Logic>>() {}.getType()
						: Logic.class;
		// 设置调用参数，调用远程服务	
		return new RemoteService()
				.setParams( SERVICE_NAME,
							"addFLogic",
							logic)
				.call(manager, type, Operation.ADD);
    }
    
    /**
     * 更新制定的逻辑任务
     * @param manager
     * @param logic
     */
    public AsyncTaskPM360 updateLogic(final DataManagerInterface manager) {
		// 设置调用参数，调用远程服务	
		return new RemoteService()
				.setParams( SERVICE_NAME,
							"updateLogic",
							mLogic)
				.call(manager, Operation.MODIFY);
    }
    
    /**
     * 删除一个逻辑任务
     * @param manager
     * @param logic
     */
    public AsyncTaskPM360 deleteLogic(final DataManagerInterface manager) {
		// 设置调用参数，调用远程服务	
		return new RemoteService()
				.setParams( SERVICE_NAME,
							"deleteLogic",
							mLogic.getLogic_id())
				.call(manager, Operation.DELETE);
    }
}
