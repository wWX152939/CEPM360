package com.pm360.cepm360.services.system;

import com.google.gson.reflect.TypeToken;
import com.pm360.cepm360.app.common.activity.DataManagerInterface;
import com.pm360.cepm360.common.json.AnalysisManager;
import com.pm360.cepm360.entity.Experience;
import com.pm360.cepm360.services.common.AsyncTaskPM360;
import com.pm360.cepm360.services.common.Operation;
import com.pm360.cepm360.services.common.RemoteService;

import java.lang.reflect.Type;
import java.util.List;

public class RemoteExperienceService {
	// 框架服务类全限定名
	private static final String SERVICE_NAME = "com.pm360.cepm360.services.system.ExperienceService";
	// 单例类变量
	private static RemoteExperienceService gService;

	/**
	 * 单例模式
	 * 
	 * @return
	 */
	public static synchronized RemoteExperienceService getInstance() {
		if (gService == null) {
			gService = new RemoteExperienceService();
		}
		return gService;
	}

	/**
	 * 销毁实例对象
	 */
	public static void destroyInstance() {
		gService = null;
	}

	private RemoteExperienceService() {

	}

	/**
	 * 获取风控经验列表
	 * 
	 * @param tenant_id
	 * @return
	 */
	public AsyncTaskPM360 getExperienceList(final DataManagerInterface manager,
			int tenant_id) {
		// 初始化返回类型
		Type type = AnalysisManager.GSON ? new TypeToken<List<Experience>>() {
		}.getType() : Experience.class;
		// 设置调用参数，调用远程服务
		return new RemoteService().setParams(SERVICE_NAME, "getExperienceList",
				tenant_id).call(manager, type, Operation.QUERY);
	}

	/**
	 * 增加风控经验
	 * 
	 * @param ExperienceJSON
	 * @return
	 */
	public AsyncTaskPM360 addExperience(final DataManagerInterface manager,
			Experience Experience) {
		// 设置返回类型
		Type type = AnalysisManager.GSON ? new TypeToken<List<Experience>>() {
		}.getType() : Experience.class;
		// 设置调用参数，调用远程服务
		return new RemoteService().setParams(SERVICE_NAME, "addExperience",
				Experience).call(manager, type, Operation.ADD);
	}

	/**
	 * 删除风控经验
	 * 
	 * @param id
	 * @return
	 */
	public AsyncTaskPM360 deleteExperience(final DataManagerInterface manager,
			int id) {
		// 设置调用参数，调用远程服务
		return new RemoteService().setParams(SERVICE_NAME, "deleteExperience",
				id).call(manager, Operation.DELETE);
	}

	/**
	 * 更新风控经验
	 * 
	 * @param ExperienceJSON
	 * @return
	 */
	public AsyncTaskPM360 updateExperience(final DataManagerInterface manager,
			Experience Experience) {
		// 设置调用参数，调用远程服务
		return new RemoteService().setParams(SERVICE_NAME, "updateExperience",
				Experience).call(manager, Operation.MODIFY);
	}
}
