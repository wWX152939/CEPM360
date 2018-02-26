package com.pm360.cepm360.services.plan;

import com.google.gson.reflect.TypeToken;
import com.pm360.cepm360.app.common.activity.DataManagerInterface;
import com.pm360.cepm360.common.json.AnalysisManager;
import com.pm360.cepm360.entity.Changes;
import com.pm360.cepm360.entity.Project;
import com.pm360.cepm360.services.common.AsyncTaskPM360;
import com.pm360.cepm360.services.common.Operation;
import com.pm360.cepm360.services.common.RemoteService;

import java.lang.reflect.Type;
import java.util.List;

public class RemoteChangeService {
	// 框架服务类全限定名
	private static final String SERVICE_NAME = "com.pm360.cepm360.services.plan.ChangeService";
	// 单例类变量
	private static RemoteChangeService gService;
    // 当前变更
    private Changes mChange;
	
	/**
	 * 单例模式
	 * @return
	 */
    public static synchronized RemoteChangeService getInstance() {
        if (gService == null) {
            gService = new RemoteChangeService();
        }
        return gService;
    }
    
    /**
     * 销毁实例对象
     */
    public static void destroyInstance() {
    	gService = null;
    }
    
    private RemoteChangeService() {
    	
    }
    
    /**
     * 设置变更
     * @param task
     */
    public RemoteChangeService setChange(Changes change) {
    	mChange = change;
    	return this;
    }
    
    /**
     * 获取当前变更
     * @return
     */
    public Changes getChange() {
    	return mChange;
    }
    
    /**
     * 添加一个变更, 注意change里面的project_id要赋值
     * @param manager
     * @param change
     */
    public AsyncTaskPM360 addChange(final DataManagerInterface manager, Changes change) {     	
    	// 初始化返回类型
    	Type type = AnalysisManager.GSON 
				? new TypeToken<List<Changes>>() {}.getType()
						: Changes.class;
				
    	// 设置调用参数，调用远程服务	
    	return new RemoteService()
			.setParams( SERVICE_NAME,
						"addChange",
						change
						).call(manager, type, Operation.ADD);
    }
    
    /**
     * 删除一个变更
     * @param manager
     * @param change
     */
    public AsyncTaskPM360 deleteChange(final DataManagerInterface manager) { 	
    	// 设置调用参数，调用远程服务	
    	return new RemoteService()
			.setParams( SERVICE_NAME,
						"deleteChange",
						mChange.getChange_id()
						).call(manager, Operation.DELETE);
    }
    
    /**
     * 更新一个变更
     * @param manager
     * @param change
     */
    public AsyncTaskPM360 updateChange(final DataManagerInterface manager) { 	
    	// 设置调用参数，调用远程服务	
    	return new RemoteService()
			.setParams( SERVICE_NAME,
						"updateChange",
						mChange
						).call(manager, Operation.MODIFY);
    }
    
    /**
     * 获取变更列表
     * @param manager
     */
    public AsyncTaskPM360 getChangeList(final DataManagerInterface manager, Project project) {
    	// 初始化返回类型
    	Type type = AnalysisManager.GSON 
				? new TypeToken<List<Changes>>() {}.getType()
						: Changes.class;
    	// 设置调用参数，调用远程服务	
    	return new RemoteService()
			.setParams( SERVICE_NAME,
						"getChangesList",
						project.getProject_id()
						).call(manager, type, Operation.QUERY);
    }
    
    /**
     * 版本和变更的发布
     * @param manager
     */
    public AsyncTaskPM360 publish(final DataManagerInterface manager, Project project) {
    	return RemoteTaskService.getInstance().publish(manager, project);
    }
    
    /**
     * 获取版本列表
     * @param manager
     */
    public AsyncTaskPM360 getVersionList(final DataManagerInterface manager, Project project) {
    	return RemoteTaskService.getInstance().getVersionList(manager, project);
    }
    
    /**
     * 获取版本任务列表
     * @param manager
     */
    public AsyncTaskPM360 getVersionTaskList(final DataManagerInterface manager) {
    	return RemoteTaskService.getInstance().getVersionTaskList(manager, mChange);
    }
    
    /**
     * 生成版本
     * @param manager
     */
    public AsyncTaskPM360 generateVersion(final DataManagerInterface manager) {
    	// 设置调用参数，调用远程服务	
    	return new RemoteService()
			.setParams( SERVICE_NAME,
						"generateVersion",
						mChange.getChange_id())
			.call(manager, Operation.MODIFY);
    }
    
    /**
     * 获取最新变更状态和变更ID
     * @param manager
     */
    public AsyncTaskPM360 getRecentlyChangeStatus(final DataManagerInterface manager, Project project) {
    	// 初始化返回类型
    	Type type = AnalysisManager.GSON 
				? new TypeToken<List<Changes>>() {}.getType()
						: Changes.class;
    	// 设置调用参数，调用远程服务	
    	return new RemoteService()
			.setParams( SERVICE_NAME,
						"getRecentlyChangeStatus",
						project.getProject_id())
			.call(manager, type, Operation.QUERY);
    }
}
