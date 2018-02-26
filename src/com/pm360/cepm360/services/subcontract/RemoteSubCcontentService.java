package com.pm360.cepm360.services.subcontract;

import com.google.gson.reflect.TypeToken;
import com.pm360.cepm360.app.common.activity.DataManagerInterface;
import com.pm360.cepm360.common.json.AnalysisManager;
import com.pm360.cepm360.entity.Flow_approval;
import com.pm360.cepm360.entity.P_WBRG;
import com.pm360.cepm360.entity.P_WBRGD;
import com.pm360.cepm360.services.common.AsyncTaskPM360;
import com.pm360.cepm360.services.common.Operation;
import com.pm360.cepm360.services.common.RemoteService;

import java.lang.reflect.Type;
import java.util.List;

/**
 * 分包内容服务
 * 
 * @author Andy
 * 
 */
public class RemoteSubCcontentService {
	// 框架服务类全限定名
	private static final String SERVICE_NAME = "com.pm360.cepm360.services.subcontract.SubCcontentService";
	// 单例类变量
	private static RemoteSubCcontentService gService;

	/**
	 * 单例模式
	 * 
	 * @return
	 */
	public static synchronized RemoteSubCcontentService getInstance() {
		if (gService == null) {
			gService = new RemoteSubCcontentService();
		}
		return gService;
	}

	/**
	 * 销毁实例对象
	 */
	public static void destroyInstance() {
		gService = null;
	}

	private RemoteSubCcontentService() {

	}

	/**
	 * 外包人工内容列表
	 * 
	 * @param tenant_id_
	 * @param task_id_
	 * @param project_id_
	 * @return CGYS
	 */
	public AsyncTaskPM360 getWBRGList(final DataManagerInterface manager,
			int tenant_id, int task_id, int project_id) {
		// 初始化返回类型
		Type type = AnalysisManager.GSON ? new TypeToken<List<P_WBRG>>() {
		}.getType() : P_WBRG.class;
		// 设置调用参数，调用远程服务
		return new RemoteService().setParams(SERVICE_NAME, "getWBRGList",
				tenant_id, task_id, project_id).call(manager, type,
				Operation.QUERY);
	}

	/**
	 * 获取某个消息的外包人工信息
	 * 
	 * @param msgId
	 * @return
	 */
	public AsyncTaskPM360 getWBRGByMsgId(final DataManagerInterface manager,
			int msgId) {
		// 初始化返回类型
		Type type = AnalysisManager.GSON ? new TypeToken<List<P_WBRG>>() {
		}.getType() : P_WBRG.class;
		// 设置调用参数，调用远程服务
		return new RemoteService().setParams(SERVICE_NAME, "getWBRGByMsgId",
				msgId).call(manager, type, Operation.QUERY);
	}

	/**
	 * 查看外包人工单
	 * 
	 * @param wbrg_id
	 * @return
	 */
	public AsyncTaskPM360 getWBRGD(final DataManagerInterface manager,
			int wbrg_id) {
		// 初始化返回类型
		Type type = AnalysisManager.GSON ? new TypeToken<List<P_WBRGD>>() {
		}.getType() : P_WBRGD.class;
		// 设置调用参数，调用远程服务
		return new RemoteService().setParams(SERVICE_NAME, "getWBRGD", wbrg_id)
				.call(manager, type, Operation.QUERY);
	}

	// ---------------------------带审批与不带审批公用--------------------------------------------
	/**
	 * 带审批和不带审批（保存） 【1.新增外包人工数据，状态为 未提交】
	 * 
	 * @param P_WBRGJSON
	 * @param P_WBRGDJSONList
	 * @return
	 */
	public AsyncTaskPM360 addWBRG(final DataManagerInterface manager,
			P_WBRG P_WBRG, List<P_WBRGD> P_WBRGDList) {
		// 设置调用参数，调用远程服务
		return new RemoteService().setParams(SERVICE_NAME, "addWBRG", P_WBRG,
				P_WBRGDList).call(manager, Operation.ADD);
	}

	/**
	 * 带审批和不带审批（修改）【1.更新外包人工表，状态为未提交】
	 * 
	 * @param P_WBRGJSON
	 * @param P_WBRGDJSONList
	 * @return
	 */
	public AsyncTaskPM360 updateWBRG(final DataManagerInterface manager,
			P_WBRG P_WBRG, List<P_WBRGD> P_WBRGDList) {
		// 设置调用参数，调用远程服务
		return new RemoteService().setParams(SERVICE_NAME, "updateWBRG",
				P_WBRG, P_WBRGDList).call(manager, Operation.MODIFY);
	}

	// -----------------------------不带审批流程----------------------------------------------
	/**
	 * 不带审批（新增->提交） 【1.新增外包人工数据，状态为 通过】
	 * 
	 * @param P_WBRGJSON
	 * @return
	 */
	public AsyncTaskPM360 submitNoApprovalForAdd(
			final DataManagerInterface manager, P_WBRG P_WBRG,
			List<P_WBRGD> P_WBRGDList) {
		// 设置调用参数，调用远程服务
		return new RemoteService().setParams(SERVICE_NAME,
				"submitNoApprovalForAdd", P_WBRG, P_WBRGDList).call(manager,
				Operation.ADD);
	}

	/**
	 * 不带审批（修改->提交）【1.更新外包人工状态】
	 * 
	 * @param P_WBRGJSON
	 * @return
	 */
	public AsyncTaskPM360 submitNoApprovalForUpdate(
			final DataManagerInterface manager, P_WBRG P_WBRG) {
		// 设置调用参数，调用远程服务
		return new RemoteService().setParams(SERVICE_NAME,
				"submitNoApprovalForUpdate", P_WBRG).call(manager,
				Operation.MODIFY);
	}

	// -----------------------------带审批流程----------------------------------------------
	/**
	 * 带审批（新增->提交） 【1.更新外包人工状态 2.插入审批流程数据】
	 * 
	 * @param P_WBRGJSON
	 * @param P_WBRGDJSONList
	 * @param flowApprovalJSON
	 * @return
	 */
	public AsyncTaskPM360 submitWithApprovalForAdd(
			final DataManagerInterface manager, P_WBRG P_WBRG,
			List<P_WBRGD> P_WBRGDList, Flow_approval Flow_approval) {

		// 设置调用参数，调用远程服务
		return new RemoteService().setParams(SERVICE_NAME,
				"submitWithApprovalForAdd", P_WBRG, P_WBRGDList, Flow_approval)
				.call(manager, Operation.ADD);
	}

	/**
	 * 带审批（修改->提交）【1.更新外包人工单状态 2.插入审批流程数据】
	 * 
	 * @param P_WBRGJSON
	 * @param P_WBRGDJSONList
	 * @param flowApprovalJSON
	 * @return
	 */
	public AsyncTaskPM360 submitWithApprovalForUpdate(
			final DataManagerInterface manager, P_WBRG P_WBRG,
			List<P_WBRGD> P_WBRGDList, Flow_approval Flow_approval) {
		// 设置调用参数，调用远程服务
		return new RemoteService().setParams(SERVICE_NAME,
				"submitWithApprovalForUpdate", P_WBRG, P_WBRGDList,
				Flow_approval).call(manager, Operation.MODIFY);
	}

	/**
	 * 审批结束（通过）【1.更新外包人工单状态为：通过 2.发消息通知计划提交人】
	 * 
	 * @param P_WBRGJSON
	 * @param currentFlowApprovalJSON
	 * @param nextFlowApprovalJSON
	 * @return
	 */
	public AsyncTaskPM360 passApproval(final DataManagerInterface manager,
			P_WBRG P_WBRG, Flow_approval currentFlow_approval,
			Flow_approval nextFlow_approvalow_approval) {

		// 设置调用参数，调用远程服务
		return new RemoteService().setParams(SERVICE_NAME, "passApproval",
				P_WBRG, currentFlow_approval, nextFlow_approvalow_approval)
				.call(manager, Operation.MODIFY);
	}

	/**
	 * 审批结束（驳回）【1.更新外包人工状态为：驳回 2.发消息通知提交人，被驳回】
	 * 
	 * @param P_WBRGJSON
	 * @param currentFlowApprovalJSON
	 * @param nextFlowApprovalJSON
	 * @return
	 */
	public AsyncTaskPM360 rebutApproval(final DataManagerInterface manager,
			P_WBRG P_WBRG, Flow_approval currentFlow_approval,
			Flow_approval nextFlow_approvalow_approval) {

		// 设置调用参数，调用远程服务
		return new RemoteService().setParams(SERVICE_NAME, "rebutApproval",
				P_WBRG, currentFlow_approval, nextFlow_approvalow_approval)
				.call(manager, Operation.MODIFY);
	}

	/**
	 * 删除外包人工
	 * 
	 * @param wbrg_id
	 * @return
	 */
	public AsyncTaskPM360 deleteWBRG(final DataManagerInterface manager,
			int wbrg_id) {
		// 设置调用参数，调用远程服务
		return new RemoteService().setParams(SERVICE_NAME, "deleteWBRG",
				wbrg_id).call(manager, Operation.DELETE);
	}

}
