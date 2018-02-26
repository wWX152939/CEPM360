package com.pm360.cepm360.services.invitebid;

import com.google.gson.reflect.TypeToken;
import com.pm360.cepm360.app.common.activity.DataManagerInterface;
import com.pm360.cepm360.common.json.AnalysisManager;
import com.pm360.cepm360.entity.ZB_mavin;
import com.pm360.cepm360.entity.ZB_plan;
import com.pm360.cepm360.services.common.AsyncTaskPM360;
import com.pm360.cepm360.services.common.Operation;
import com.pm360.cepm360.services.common.RemoteService;

import java.lang.reflect.Type;
import java.util.List;

/**
 * 远程招标计划服务
 * 
 * @author Andy
 * 
 */
public class RemoteZBPlanService {

	// 任务服务的类全限定名
	private static final String SERVICE_NAME = "com.pm360.cepm360.services.invitebid.ZBPlanService";
	// 单例对象类变量
	private static RemoteZBPlanService gService;

	/**
	 * 单例模式
	 * 
	 * @return
	 */
	public static synchronized RemoteZBPlanService getInstance() {
		if (gService == null) {
			gService = new RemoteZBPlanService();
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
	private RemoteZBPlanService() {

	}

	/**
	 * 查看招标计划
	 * 
	 * @param zb_plan_id
	 * @return
	 */
	public AsyncTaskPM360 getZBPlan(final DataManagerInterface manager,
			int zb_plan_id) {
		// 初始化返回类型
		Type type = AnalysisManager.GSON ? new TypeToken<List<ZB_plan>>() {
		}.getType() : ZB_plan.class;

		// 设置调用参数，调用远程服务
		return new RemoteService().setParams(SERVICE_NAME, "getZBPlan",
				zb_plan_id).call(manager, type, Operation.QUERY);
	}

	/**
	 * 修改招标计划
	 * 
	 * @param ZB_planJSON
	 * @return
	 */
	public AsyncTaskPM360 updateZBPlan(final DataManagerInterface manager,
			ZB_plan ZB_plan) {
		// 设置调用参数，调用远程服务
		return new RemoteService().setParams(SERVICE_NAME, "updateZBPlan",
				ZB_plan).call(manager, Operation.MODIFY);
	}

	/**
	 * 查看评分专家
	 * 
	 * @param zb_plan_id
	 * @return
	 */
	public AsyncTaskPM360 getZBMavinList(final DataManagerInterface manager,
			int zb_plan_id) {
		// 初始化返回类型
		Type type = AnalysisManager.GSON ? new TypeToken<List<ZB_mavin>>() {
		}.getType() : ZB_mavin.class;

		// 设置调用参数，调用远程服务
		return new RemoteService().setParams(SERVICE_NAME, "getZBMavinList",
				zb_plan_id).call(manager, type, Operation.QUERY);
	}

	/**
	 * 添加评分专家
	 * 
	 * @param ZB_mavinJSON
	 * @return
	 */
	public AsyncTaskPM360 addZBMavin(final DataManagerInterface manager,
			ZB_mavin ZB_mavin) {
		// 设置调用参数，调用远程服务
		return new RemoteService().setParams(SERVICE_NAME, "addZBMavin",
				ZB_mavin).call(manager, Operation.ADD);
	}

	/**
	 * 移出评分专家
	 * 
	 * @param zb_mavin_id
	 * @return
	 */
	public AsyncTaskPM360 deleteZBMavin(final DataManagerInterface manager,
			int zb_mavin_id) {
		// 设置调用参数，调用远程服务
		return new RemoteService().setParams(SERVICE_NAME, "deleteZBMavin",
				zb_mavin_id).call(manager, Operation.DELETE);
	}
}
