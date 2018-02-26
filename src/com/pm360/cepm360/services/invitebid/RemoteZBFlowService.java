package com.pm360.cepm360.services.invitebid;

import com.google.gson.reflect.TypeToken;
import com.pm360.cepm360.app.common.activity.DataManagerInterface;
import com.pm360.cepm360.common.json.AnalysisManager;
import com.pm360.cepm360.entity.ZB_flow;
import com.pm360.cepm360.services.common.AsyncTaskPM360;
import com.pm360.cepm360.services.common.Operation;
import com.pm360.cepm360.services.common.RemoteService;

import java.lang.reflect.Type;
import java.util.List;

/**
 * 远程招标流程服务
 * 
 * @author Andy
 * 
 */
public class RemoteZBFlowService {

	// 任务服务的类全限定名
	private static final String SERVICE_NAME = "com.pm360.cepm360.services.invitebid.ZBFlowService";
	// 单例对象类变量
	private static RemoteZBFlowService gService;

	/**
	 * 单例模式
	 * 
	 * @return
	 */
	public static synchronized RemoteZBFlowService getInstance() {
		if (gService == null) {
			gService = new RemoteZBFlowService();
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
	private RemoteZBFlowService() {

	}

	/**
	 * 获取招标流程信息
	 * 
	 * @param tenant_id_
	 * @param project_id_
	 * @return
	 */
	public AsyncTaskPM360 getZBFlow(final DataManagerInterface manager,
			int tenant_id, int project_id) {
		// 初始化返回类型
		Type type = AnalysisManager.GSON ? new TypeToken<List<ZB_flow>>() {
		}.getType() : ZB_flow.class;

		// 设置调用参数，调用远程服务
		return new RemoteService().setParams(SERVICE_NAME, "getZBFlow",
				tenant_id, project_id).call(manager, type, Operation.QUERY);
	}

	/**
	 * 新增招标流程信息
	 * 
	 * @param ZB_flowJSON
	 * @return
	 */
	public AsyncTaskPM360 addZBFlow(final DataManagerInterface manager,
			ZB_flow ZB_flow) {
		// 初始化返回类型
		Type type = AnalysisManager.GSON ? new TypeToken<List<ZB_flow>>() {
		}.getType() : ZB_flow.class;
		// 设置调用参数，调用远程服务
		return new RemoteService()
				.setParams(SERVICE_NAME, "addZBFlow", ZB_flow).call(manager,
						type, Operation.ADD);
	}

	/**
	 * 更新招标流程信息
	 * 
	 * @param ZB_flowJSON
	 * @return
	 */
	public AsyncTaskPM360 updateZBFlow(final DataManagerInterface manager,
			ZB_flow ZB_flow) {
		// 设置调用参数，调用远程服务
		return new RemoteService().setParams(SERVICE_NAME, "updateZBFlow",
				ZB_flow).call(manager, Operation.MODIFY);
	}
}
