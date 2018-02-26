package com.pm360.cepm360.services.cooperation;

import com.google.gson.reflect.TypeToken;
import com.pm360.cepm360.app.common.activity.DataManagerInterface;
import com.pm360.cepm360.common.json.AnalysisManager;
import com.pm360.cepm360.entity.Cooperation;
import com.pm360.cepm360.services.common.AsyncTaskPM360;
import com.pm360.cepm360.services.common.Operation;
import com.pm360.cepm360.services.common.RemoteService;

import java.lang.reflect.Type;
import java.util.List;

/**
 * 远程协作服务
 * 
 * @author Andy
 * 
 */
public class RemoteCooperationService {

	// 任务服务的类全限定名
	private static final String SERVICE_NAME = "com.pm360.cepm360.services.cooperation.CooperationService";
	// 单例对象类变量
	private static RemoteCooperationService gService;

	/**
	 * 单例模式
	 * 
	 * @return
	 */
	public static synchronized RemoteCooperationService getInstance() {
		if (gService == null) {
			gService = new RemoteCooperationService();
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
	private RemoteCooperationService() {

	}

	/**
	 * 获取协作发起列表
	 * 
	 * @param tenant_id
	 * @return
	 */
	public AsyncTaskPM360 getLaunchCooperationList(
			final DataManagerInterface manager, int tenant_id) {
		// 初始化返回类型
		Type type = AnalysisManager.GSON ? new TypeToken<List<Cooperation>>() {
		}.getType() : Cooperation.class;

		// 设置调用参数，调用远程服务
		return new RemoteService().setParams(SERVICE_NAME,
				"getLaunchCooperationList", tenant_id).call(manager, type,
				Operation.QUERY);
	}

	/**
	 * 获取协接受起列表
	 * 
	 * @param tenant_id
	 * @return
	 */
	public AsyncTaskPM360 getAcceptCooperationList(
			final DataManagerInterface manager, int tenant_id) {
		// 初始化返回类型
		Type type = AnalysisManager.GSON ? new TypeToken<List<Cooperation>>() {
		}.getType() : Cooperation.class;

		// 设置调用参数，调用远程服务
		return new RemoteService().setParams(SERVICE_NAME,
				"getAcceptCooperationList", tenant_id).call(manager, type,
				Operation.QUERY);
	}

	/**
	 * 发起协作（只保存）
	 * 
	 * @param CooperationJSON
	 * @return
	 */
	public AsyncTaskPM360 AddCooperation(final DataManagerInterface manager,
			Cooperation Cooperation) {
		// 初始化返回类型
		Type type = AnalysisManager.GSON ? new TypeToken<List<Cooperation>>() {
		}.getType() : Cooperation.class;
		// 设置调用参数，调用远程服务
		return new RemoteService().setParams(SERVICE_NAME, "AddCooperation",
				Cooperation).call(manager, type, Operation.ADD);
	}

	/**
	 * 发起协作（发起）
	 * 
	 * @param CooperationJSON
	 * @return
	 */
	public AsyncTaskPM360 LaunchCooperation(final DataManagerInterface manager,
			Cooperation Cooperation) {
		// 初始化返回类型
		Type type = AnalysisManager.GSON ? new TypeToken<List<Cooperation>>() {
		}.getType() : Cooperation.class;
		// 设置调用参数，调用远程服务
		return new RemoteService().setParams(SERVICE_NAME, "LaunchCooperation",
				Cooperation).call(manager, type, Operation.ADD);
	}

	/**
	 * 更新协作（只更新）
	 * 
	 * @param CooperationJSON
	 * @param action
	 *            (1:更新发起联系人 2：更新接受联系人0：其他)
	 * @return
	 */
	public AsyncTaskPM360 updateCooperation(final DataManagerInterface manager,
			Cooperation Cooperation, String action) {
		// 初始化返回类型
		Type type = AnalysisManager.GSON ? new TypeToken<List<Cooperation>>() {
		}.getType() : Cooperation.class;
		// 设置调用参数，调用远程服务
		return new RemoteService().setParams(SERVICE_NAME, "updateCooperation",
				Cooperation, action).call(manager, type, Operation.MODIFY);
	}

	/**
	 * 更新协作（发起）
	 * 
	 * @param CooperationJSON
	 * @return
	 */
	public AsyncTaskPM360 updateLaunchCooperation(
			final DataManagerInterface manager, Cooperation Cooperation) {
		// 初始化返回类型
		Type type = AnalysisManager.GSON ? new TypeToken<List<Cooperation>>() {
		}.getType() : Cooperation.class;
		// 设置调用参数，调用远程服务
		return new RemoteService().setParams(SERVICE_NAME,
				"updateLaunchCooperation", Cooperation).call(manager, type,
				Operation.MODIFY);
	}

	/**
	 * 协作接受
	 * 
	 * @param CooperationJSON
	 * @return
	 */
	public AsyncTaskPM360 acceptCooperation(final DataManagerInterface manager,
			Cooperation Cooperation) {
		// 初始化返回类型
		Type type = AnalysisManager.GSON ? new TypeToken<List<Cooperation>>() {
		}.getType() : Cooperation.class;
		// 设置调用参数，调用远程服务
		return new RemoteService().setParams(SERVICE_NAME, "acceptCooperation",
				Cooperation).call(manager, type, Operation.MODIFY);
	}

	/**
	 * 删除发起的协作
	 * 
	 * @param cooperation_id
	 * @return
	 */
	public AsyncTaskPM360 deleteCooperation(final DataManagerInterface manager,
			int cooperation_id) {
		// 设置调用参数，调用远程服务
		return new RemoteService().setParams(SERVICE_NAME, "deleteCooperation",
				cooperation_id).call(manager, Operation.DELETE);
	}
}
