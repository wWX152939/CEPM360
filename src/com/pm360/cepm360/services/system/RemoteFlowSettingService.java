package com.pm360.cepm360.services.system;

import com.google.gson.reflect.TypeToken;
import com.pm360.cepm360.app.common.activity.DataManagerInterface;
import com.pm360.cepm360.common.json.AnalysisManager;
import com.pm360.cepm360.entity.Flow_setting;
import com.pm360.cepm360.services.common.AsyncTaskPM360;
import com.pm360.cepm360.services.common.Operation;
import com.pm360.cepm360.services.common.RemoteService;

import java.lang.reflect.Type;
import java.util.List;

/**
 * 远程流程模板设置服务
 * 
 * @author Andy
 * 
 */
public class RemoteFlowSettingService {
	// 框架服务类全限定名
	private static final String SERVICE_NAME = "com.pm360.cepm360.services.system.FlowSettingService";
	// 单例类变量
	private static RemoteFlowSettingService gService;

	/**
	 * 单例模式
	 * 
	 * @return
	 */
	public static synchronized RemoteFlowSettingService getInstance() {
		if (gService == null) {
			gService = new RemoteFlowSettingService();
		}
		return gService;
	}

	/**
	 * 销毁实例对象
	 */
	public static void destroyInstance() {
		gService = null;
	}

	private RemoteFlowSettingService() {

	}

	// --------------------------- 接口 ------------------------------------ //

	/**
	 * 获取流程列表
	 * 
	 * @param tenant_id_
	 *            公司ID
	 * @return
	 */
	public AsyncTaskPM360 getFlowSettingList(
			final DataManagerInterface manager, int tenant_id_) {
		// 初始化返回类型
		Type type = AnalysisManager.GSON ? new TypeToken<List<Flow_setting>>() {
		}.getType() : Flow_setting.class;
		// 设置调用参数，调用远程服务
		return new RemoteService().setParams(SERVICE_NAME,
				"getFlowSettingList", tenant_id_).call(manager, type,
				Operation.QUERY);
	}

	/**
	 * 根据流程类型，获取流程信息
	 * 
	 * @param tenant_id_
	 *            公司ID
	 * @param flow_type_
	 *            流程类型
	 * @return
	 */
	public AsyncTaskPM360 getFlowDetail(final DataManagerInterface manager,
			int tenant_id_, String flow_type_) {
		// 初始化返回类型
		Type type = AnalysisManager.GSON ? new TypeToken<List<Flow_setting>>() {
		}.getType() : Flow_setting.class;
		// 设置调用参数，调用远程服务
		return new RemoteService().setParams(SERVICE_NAME, "getFlowDetail",
				tenant_id_, flow_type_).call(manager, type, Operation.QUERY);
	}

	/**
	 * 设置流程
	 * 
	 * @param flowSettingJSON
	 * @return
	 */
	public AsyncTaskPM360 SetFlow(final DataManagerInterface manager,
			Flow_setting Flow_setting) {
		// 设置调用参数，调用远程服务
		return new RemoteService().setParams(SERVICE_NAME, "SetFlow",
				Flow_setting).call(manager, Operation.MODIFY);
	}
}
