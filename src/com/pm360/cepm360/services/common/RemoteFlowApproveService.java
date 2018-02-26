package com.pm360.cepm360.services.common;

import com.google.gson.reflect.TypeToken;
import com.pm360.cepm360.app.common.activity.DataManagerInterface;
import com.pm360.cepm360.common.json.AnalysisManager;
import com.pm360.cepm360.entity.Flow_approval;
import com.pm360.cepm360.entity.Flow_countersign;

import java.lang.reflect.Type;
import java.util.List;

/**
 * 流程审批远程服务
 * 
 * @author Andy
 * 
 */
public class RemoteFlowApproveService {
	// 任务服务的类全限定名
	private static final String SERVICE_NAME = "com.pm360.cepm360.services.common.FlowApproveService";
	// 单例对象类变量
	private static RemoteFlowApproveService gService;

	/**
	 * 单例模式
	 * 
	 * @return
	 */
	public static synchronized RemoteFlowApproveService getInstance() {
		if (gService == null) {
			gService = new RemoteFlowApproveService();
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
	private RemoteFlowApproveService() {

	}

	/**
	 * 获取某个单据的审批信息
	 * 
	 * @param tenant_id_
	 *            公司ID
	 * @param flow_type_id_
	 *            单据所用流程的ID
	 * @param type_id_
	 *            单据ID (例如采购单ID)
	 * @return
	 */
	public AsyncTaskPM360 getApprovalList(final DataManagerInterface manager,
			int tenant_id, int flow_type_id, int type_id) {
		// 初始化返回类型
		Type type = AnalysisManager.GSON ? new TypeToken<List<Flow_approval>>() {
		}.getType()
				: Flow_approval.class;
		// 设置调用参数，调用远程服务
		return new RemoteService().setParams(SERVICE_NAME, "getApprovalList",
				tenant_id, flow_type_id, type_id).call(manager, type,
				Operation.QUERY);
	}

	/**
	 * 获取某个流程下的会签信息
	 * 
	 * @param flow_approval_id
	 *            (1,2,3)
	 * @return
	 */
	public AsyncTaskPM360 getCountersignList(
			final DataManagerInterface manager, String flow_approval_id) {
		// 初始化返回类型
		Type type = AnalysisManager.GSON ? new TypeToken<List<Flow_countersign>>() {
		}.getType()
				: Flow_countersign.class;
		// 设置调用参数，调用远程服务
		return new RemoteService().setParams(SERVICE_NAME,
				"getCountersignList", flow_approval_id).call(manager, type,
				Operation.QUERY);
	}

	/**
	 * 新增审批信息
	 * 
	 * @param flowApprovalJSON
	 * @return
	 */
	public AsyncTaskPM360 addApprovalInfo(final DataManagerInterface manager,
			Flow_approval Flow_approval) {
		// 初始化返回类型
		// Type type = AnalysisManager.GSON ? new
		// TypeToken<List<Flow_approval>>() {
		// }.getType()
		// : Flow_approval.class;
		// 设置调用参数，调用远程服务
		return new RemoteService().setParams(SERVICE_NAME, "addApprovalInfo",
				Flow_approval).call(manager, Operation.ADD);
	}

	/**
	 * 审批
	 * 
	 * @param currentFlowApprovalJSON
	 * @param nextFlowApprovalJSON
	 * @return
	 */
	public AsyncTaskPM360 approval(final DataManagerInterface manager,
			Flow_approval currentFlow_approval, Flow_approval nextFlow_approval) {
		// 初始化返回类型
		// Type type = AnalysisManager.GSON ? new
		// TypeToken<List<Flow_approval>>() {
		// }.getType()
		// : Flow_approval.class;
		// 设置调用参数，调用远程服务
		return new RemoteService().setParams(SERVICE_NAME, "approval",
				currentFlow_approval, nextFlow_approval).call(manager,
				Operation.MODIFY);
	}

	/**
	 * 增加会签
	 * 
	 * @param flow_countersignJSONList
	 * @param nextFlowApprovalJSON
	 * @return
	 */
	public AsyncTaskPM360 addCountersign(final DataManagerInterface manager,
			List<Flow_countersign> Flow_countersignList,
			Flow_approval nextFlow_approval) {
		// 初始化返回类型
		Type type = AnalysisManager.GSON ? new TypeToken<List<Flow_countersign>>() {
		}.getType()
				: Flow_countersign.class;
		// 设置调用参数，调用远程服务
		return new RemoteService().setParams(SERVICE_NAME, "addCountersign",
				Flow_countersignList, nextFlow_approval).call(manager, type,
				Operation.ADD);
	}

	/**
	 * 会签
	 * 
	 * @param flow_countersignJSON
	 * @return
	 */
	public AsyncTaskPM360 countersign(final DataManagerInterface manager,
			Flow_countersign Flow_countersign) {
		// 设置调用参数，调用远程服务
		return new RemoteService().setParams(SERVICE_NAME, "countersign",
				Flow_countersign).call(manager, Operation.MODIFY);
	}

	/**
	 * 移除会签
	 * 
	 * @param flow_countersignJSONList
	 * @param nextFlowApprovalJSON
	 * @return
	 */
	public AsyncTaskPM360 removeCountersign(final DataManagerInterface manager,
			List<Flow_countersign> Flow_countersignList,
			Flow_approval nextFlow_approval) {
		// 设置调用参数，调用远程服务
		return new RemoteService().setParams(SERVICE_NAME, "removeCountersign",
				Flow_countersignList, nextFlow_approval).call(manager,
				Operation.DELETE);
	}
}
