package com.pm360.cepm360.services.plan;

import com.google.gson.reflect.TypeToken;
import com.pm360.cepm360.app.common.activity.DataManagerInterface;
import com.pm360.cepm360.common.json.AnalysisManager;
import com.pm360.cepm360.entity.Files;
import com.pm360.cepm360.entity.Quality;
import com.pm360.cepm360.services.common.AsyncTaskPM360;
import com.pm360.cepm360.services.common.Operation;
import com.pm360.cepm360.services.common.RemoteService;

import java.lang.reflect.Type;
import java.util.List;

/**
 * 远程质量文明服务
 * 
 * @author Andy
 * 
 */
public class RemoteQualityService {
	// 框架服务类全限定名
	private static final String SERVICE_NAME = "com.pm360.cepm360.services.plan.QualityService";
	// 单例类变量
	private static RemoteQualityService gService;

	/**
	 * 单例模式
	 * 
	 * @return
	 */
	public static synchronized RemoteQualityService getInstance() {
		if (gService == null) {
			gService = new RemoteQualityService();
		}
		return gService;
	}

	/**
	 * 销毁实例对象
	 */
	public static void destroyInstance() {
		gService = null;
	}

	private RemoteQualityService() {

	}

	/**
	 * 获取质量文明列表
	 * 
	 * @param task_id
	 * @return
	 */
	public AsyncTaskPM360 getQualityList(final DataManagerInterface manager,
			int task_id) {
		// 初始化返回类型
		Type type = AnalysisManager.GSON ? new TypeToken<List<Quality>>() {
		}.getType() : Quality.class;
		// 设置调用参数，调用远程服务
		return new RemoteService().setParams(SERVICE_NAME, "getQualityList",
				task_id).call(manager, type, Operation.QUERY);
	}

	/**
	 * 增加质量文明信息
	 * 
	 * @param QualityJSON
	 * @return
	 */
	public AsyncTaskPM360 addQuality(final DataManagerInterface manager,
			Quality Quality) {
		// 设置返回类型
		Type type = AnalysisManager.GSON ? new TypeToken<List<Quality>>() {
		}.getType() : Quality.class;
		// 设置调用参数，调用远程服务
		return new RemoteService().setParams(SERVICE_NAME, "addQuality",
				Quality).call(manager, type, Operation.ADD);
	}

	/**
	 * 删除质量文明信息
	 * 
	 * @param quality_id
	 * @return
	 */
	public AsyncTaskPM360 deleteQuality(final DataManagerInterface manager,
			int quality_id) {
		// 设置调用参数，调用远程服务
		return new RemoteService().setParams(SERVICE_NAME, "deleteQuality",
				quality_id).call(manager, Operation.DELETE);
	}

	/**
	 * 更新质量文明信息
	 * 
	 * @param QualityJSON
	 * @return
	 */
	public AsyncTaskPM360 updateQuality(final DataManagerInterface manager,
			Quality Quality) {
		// 设置调用参数，调用远程服务
		return new RemoteService().setParams(SERVICE_NAME, "updateQuality",
				Quality).call(manager, Operation.MODIFY);
	}

	/**
	 * 获取质量文明下的附件
	 * 
	 * @param quality_id
	 * @return
	 */
	public AsyncTaskPM360 getQualityFiles(final DataManagerInterface manager,
			int quality_id) {
		// 初始化返回类型
		Type type = AnalysisManager.GSON ? new TypeToken<List<Files>>() {
		}.getType() : Files.class;

		// 设置调用参数，调用远程服务
		return new RemoteService().setParams(SERVICE_NAME, "getQualityFiles",
				quality_id).call(manager, type, Operation.QUERY);
	}

}
