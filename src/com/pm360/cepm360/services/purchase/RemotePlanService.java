package com.pm360.cepm360.services.purchase;

import com.google.gson.reflect.TypeToken;
import com.pm360.cepm360.app.common.activity.DataManagerInterface;
import com.pm360.cepm360.common.json.AnalysisManager;
import com.pm360.cepm360.entity.Flow_approval;
import com.pm360.cepm360.entity.P_CG;
import com.pm360.cepm360.entity.P_CGD;
import com.pm360.cepm360.entity.P_CGJH;
import com.pm360.cepm360.entity.P_CGJHD;
import com.pm360.cepm360.services.common.AsyncTaskPM360;
import com.pm360.cepm360.services.common.Operation;
import com.pm360.cepm360.services.common.RemoteService;

import java.lang.reflect.Type;
import java.util.List;

/**
 * 采购计划服务
 * 
 * @author Andy
 * 
 */
public class RemotePlanService {
	// 框架服务类全限定名
	private static final String SERVICE_NAME = "com.pm360.cepm360.services.purchase.PlanService";
	// 单例类变量
	private static RemotePlanService gService;

	/**
	 * 单例模式
	 * 
	 * @return
	 */
	public static synchronized RemotePlanService getInstance() {
		if (gService == null) {
			gService = new RemotePlanService();
		}
		return gService;
	}

	/**
	 * 销毁实例对象
	 */
	public static void destroyInstance() {
		gService = null;
	}

	private RemotePlanService() {

	}

	/**
	 * 采购计划列表
	 * 
	 * @param tenant_id_
	 * @param task_id_
	 * @param project_id_
	 * @return CGJH
	 */
	public AsyncTaskPM360 getCGJHList(final DataManagerInterface manager,
			int tenant_id, int task_id, int project_id) {
		// 初始化返回类型
		Type type = AnalysisManager.GSON ? new TypeToken<List<P_CGJH>>() {
		}.getType() : P_CGJH.class;
		// 设置调用参数，调用远程服务
		return new RemoteService().setParams(SERVICE_NAME, "getCGJHList",
				tenant_id, task_id, project_id).call(manager, type,
				Operation.QUERY);
	}

	/**
	 * 获取某个消息的计划信息
	 * 
	 * @param msgId
	 * @return
	 */
	public AsyncTaskPM360 getCGJHByMsgId(final DataManagerInterface manager,
			int msgId) {
		// 初始化返回类型
		Type type = AnalysisManager.GSON ? new TypeToken<List<P_CGJH>>() {
		}.getType() : P_CGJH.class;
		// 设置调用参数，调用远程服务
		return new RemoteService().setParams(SERVICE_NAME, "getCGJHByMsgId",
				msgId).call(manager, type, Operation.QUERY);
	}

	/**
	 * 查看采购计划单
	 * 
	 * @param cgjh_id
	 * @return
	 */
	public AsyncTaskPM360 getCGJHD(final DataManagerInterface manager,
			int cgjh_id) {
		// 初始化返回类型
		Type type = AnalysisManager.GSON ? new TypeToken<List<P_CGJHD>>() {
		}.getType() : P_CGJHD.class;
		// 设置调用参数，调用远程服务
		return new RemoteService().setParams(SERVICE_NAME, "getCGJHD", cgjh_id)
				.call(manager, type, Operation.QUERY);
	}

	// ---------------------------带审批与不带审批公用--------------------------------------------
	/**
	 * 带审批和不带审批（保存） 【1.只插入采购计划表，状态为未提交】
	 * 
	 * @param P_CGJHJSON
	 * @param P_CGJHDJSONList
	 * @return
	 */
	public AsyncTaskPM360 addCGJH(final DataManagerInterface manager,
			P_CGJH P_CGJH, List<P_CGJHD> cgjhdList) {
		// 设置调用参数，调用远程服务
		return new RemoteService().setParams(SERVICE_NAME, "addCGJH", P_CGJH,
				cgjhdList).call(manager, Operation.ADD);
	}

	/**
	 * 带审批和不带审批（修改）【1.更新采购计划，状态为未提交】
	 * 
	 * @param P_CGJHJSON
	 * @param P_CGJHDJSONList
	 * @return
	 */
	public AsyncTaskPM360 updateCGJH(final DataManagerInterface manager,
			P_CGJH P_CGJH, List<P_CGJHD> cgjhdList) {
		// 设置调用参数，调用远程服务
		return new RemoteService().setParams(SERVICE_NAME, "updateCGJH",
				P_CGJH, cgjhdList).call(manager, Operation.MODIFY);
	}

	// -----------------------------不带审批流程----------------------------------------------
	/**
	 * 不带审批（新增->提交） 【1.插入采购计划，状态为通过 2.插入采购执行 3.发消息给执行人】
	 * 
	 * @param P_CGJHJSON
	 * @param P_CGJHDJSONList
	 * @return
	 */
	public AsyncTaskPM360 submitNoApprovalForAdd(
			final DataManagerInterface manager, P_CGJH P_CGJH,
			List<P_CGJHD> cgjhdList) {
		// 设置调用参数，调用远程服务
		return new RemoteService().setParams(SERVICE_NAME,
				"submitNoApprovalForAdd", P_CGJH, cgjhdList).call(manager,
				Operation.ADD);
	}

	/**
	 * 不带审批（修改->提交）【1.更新采购计划，状态为通过 2.插入采购执行 3.发消息给执行人】
	 * 
	 * @param P_CGJHJSON
	 * @param P_CGJHDJSONList
	 * @return
	 */
	public AsyncTaskPM360 submitNoApprovalForUpdate(
			final DataManagerInterface manager, P_CGJH P_CGJH,
			List<P_CGJHD> cgjhdList) {
		// 设置调用参数，调用远程服务
		return new RemoteService().setParams(SERVICE_NAME,
				"submitNoApprovalForUpdate", P_CGJH, cgjhdList).call(manager,
				Operation.MODIFY);
	}

	// -----------------------------带审批流程----------------------------------------------
	/**
	 * 带审批（新增->提交） 【1.插入采购计划，状态为审批中2.插入审批流程数据】
	 * 
	 * @param P_CGJHJSON
	 * @param P_CGJHDJSONList
	 * @param flowApprovalJSON
	 * @return
	 */
	public AsyncTaskPM360 submitWithApprovalForAdd(
			final DataManagerInterface manager, P_CGJH P_CGJH,
			List<P_CGJHD> cgjhdList, Flow_approval Flow_approval) {

		// 设置调用参数，调用远程服务
		return new RemoteService().setParams(SERVICE_NAME,
				"submitWithApprovalForAdd", P_CGJH, cgjhdList, Flow_approval)
				.call(manager, Operation.ADD);
	}

	/**
	 * 带审批（修改->提交）【1.更新采购计划，状态为审批中 2.插入审批流程数据】
	 * 
	 * @param P_CGJHJSON
	 * @param P_CGJHDJSONList
	 * @param flowApprovalJSON
	 * @return
	 */
	public AsyncTaskPM360 submitWithApprovalForUpdate(
			final DataManagerInterface manager, P_CGJH P_CGJH,
			List<P_CGJHD> cgjhdList, Flow_approval Flow_approval) {
		// 设置调用参数，调用远程服务
		return new RemoteService()
				.setParams(SERVICE_NAME, "submitWithApprovalForUpdate", P_CGJH,
						cgjhdList, Flow_approval).call(manager,
						Operation.MODIFY);
	}

	/**
	 * 审批结束（通过）【1.更新采购计划状态为：通过 2.插入采购执行 3.发消息通知计划提交人，计划通过 4.发消息通知执行人，执行采购】
	 * 
	 * @param P_CGJSON
	 * @param P_CGDJSONList
	 * @param currentFlowApprovalJSON
	 * @param nextFlowApprovalJSON
	 * @return
	 */
	public AsyncTaskPM360 passApproval(final DataManagerInterface manager,
			P_CG P_CG, List<P_CGD> P_CGDList,
			Flow_approval currentFlow_approval,
			Flow_approval nextFlow_approvalow_approval) {

		// 设置调用参数，调用远程服务
		return new RemoteService().setParams(SERVICE_NAME, "passApproval",
				P_CG, P_CGDList, currentFlow_approval,
				nextFlow_approvalow_approval).call(manager, Operation.ADD);
	}

	/**
	 * 审批结束（驳回）【1.更新采购计划状态为：通过 2.发消息通知计划提交人，计划被驳回】
	 * 
	 * @param P_CGJSON
	 * @param currentFlowApprovalJSON
	 * @param nextFlowApprovalJSON
	 * @return
	 */
	public AsyncTaskPM360 rebutApproval(final DataManagerInterface manager,
			P_CG P_CG, Flow_approval currentFlow_approval,
			Flow_approval nextFlow_approvalow_approval) {

		// 设置调用参数，调用远程服务
		return new RemoteService().setParams(SERVICE_NAME, "rebutApproval",
				P_CG, currentFlow_approval, nextFlow_approvalow_approval).call(
				manager, Operation.ADD);
	}

	/**
	 * 删除采购计划
	 * 
	 * @param cgjh_id
	 * @return
	 */
	public AsyncTaskPM360 deleteCGJH(final DataManagerInterface manager,
			int cgjh_id) {
		// 设置调用参数，调用远程服务
		return new RemoteService().setParams(SERVICE_NAME, "deleteCGJH",
				cgjh_id).call(manager, Operation.DELETE);
	}
}
