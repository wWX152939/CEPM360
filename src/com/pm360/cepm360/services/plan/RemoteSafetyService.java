package com.pm360.cepm360.services.plan;

import com.google.gson.reflect.TypeToken;
import com.pm360.cepm360.app.common.activity.DataManagerInterface;
import com.pm360.cepm360.common.json.AnalysisManager;
import com.pm360.cepm360.entity.Files;
import com.pm360.cepm360.entity.Safety;
import com.pm360.cepm360.services.common.AsyncTaskPM360;
import com.pm360.cepm360.services.common.Operation;
import com.pm360.cepm360.services.common.RemoteService;

import java.lang.reflect.Type;
import java.util.List;

/**
 * 远程安全监督服务
 * 
 * @author Andy
 * 
 */
public class RemoteSafetyService {
	// 框架服务类全限定名
	private static final String SERVICE_NAME = "com.pm360.cepm360.services.plan.SafetyService";
	// 单例类变量
	private static RemoteSafetyService gService;

	/**
	 * 单例模式
	 * 
	 * @return
	 */
	public static synchronized RemoteSafetyService getInstance() {
		if (gService == null) {
			gService = new RemoteSafetyService();
		}
		return gService;
	}

	/**
	 * 销毁实例对象
	 */
	public static void destroyInstance() {
		gService = null;
	}

	private RemoteSafetyService() {

	}

	/**
	 * 获取安全监督列表
	 * 
	 * @param task_id
	 * @return
	 */
	public AsyncTaskPM360 getSafetyList(final DataManagerInterface manager,
			int task_id) {
		// 初始化返回类型
		Type type = AnalysisManager.GSON ? new TypeToken<List<Safety>>() {
		}.getType() : Safety.class;
		// 设置调用参数，调用远程服务
		return new RemoteService().setParams(SERVICE_NAME, "getSafetyList",
				task_id).call(manager, type, Operation.QUERY);
	}

	/**
	 * 增加安全监督信息
	 * 
	 * @param SafetyJSON
	 * @return
	 */
	public AsyncTaskPM360 addSafety(final DataManagerInterface manager,
			Safety Safety) {
		// 设置返回类型
		Type type = AnalysisManager.GSON ? new TypeToken<List<Safety>>() {
		}.getType() : Safety.class;
		// 设置调用参数，调用远程服务
		return new RemoteService().setParams(SERVICE_NAME, "addSafety", Safety)
				.call(manager, type, Operation.ADD);
	}

	/**
	 * 删除安全监督信息
	 * 
	 * @param safety_id
	 * @return
	 */
	public AsyncTaskPM360 deleteSafety(final DataManagerInterface manager,
			int safety_id) {
		// 设置调用参数，调用远程服务
		return new RemoteService().setParams(SERVICE_NAME, "deleteSafety",
				safety_id).call(manager, Operation.DELETE);
	}

	/**
	 * 更新安全监督信息
	 * 
	 * @param SafetyJSON
	 * @return
	 */
	public AsyncTaskPM360 updateSafety(final DataManagerInterface manager,
			Safety Safety) {
		// 设置调用参数，调用远程服务
		return new RemoteService().setParams(SERVICE_NAME, "updateSafety",
				Safety).call(manager, Operation.MODIFY);
	}

	/**
	 * 获取安全监督下的附件
	 * 
	 * @param safety_id
	 * @return
	 */
	public AsyncTaskPM360 getSafetyFiles(final DataManagerInterface manager,
			int safety_id) {
		// 初始化返回类型
		Type type = AnalysisManager.GSON ? new TypeToken<List<Files>>() {
		}.getType() : Files.class;

		// 设置调用参数，调用远程服务
		return new RemoteService().setParams(SERVICE_NAME, "getSafetyFiles",
				safety_id).call(manager, type, Operation.QUERY);
	}

}
