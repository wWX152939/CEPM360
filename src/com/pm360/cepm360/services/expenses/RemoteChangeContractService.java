package com.pm360.cepm360.services.expenses;

import com.google.gson.reflect.TypeToken;
import com.pm360.cepm360.app.common.activity.DataManagerInterface;
import com.pm360.cepm360.common.json.AnalysisManager;
import com.pm360.cepm360.entity.Contract_change;
import com.pm360.cepm360.entity.Contract_list;
import com.pm360.cepm360.entity.Files;
import com.pm360.cepm360.entity.Flow_approval;
import com.pm360.cepm360.services.common.AsyncTaskPM360;
import com.pm360.cepm360.services.common.Operation;
import com.pm360.cepm360.services.common.RemoteService;

import java.lang.reflect.Type;
import java.util.List;

/**
 * 远程合同变更服务
 * 
 * @author Andy
 * 
 */
public class RemoteChangeContractService {
	// 任务服务的类全限定名
	private static final String SERVICE_NAME = "com.pm360.cepm360.services.expenses.ChangeContractService";
	// 单例对象类变量
	private static RemoteChangeContractService gService;

	/**
	 * 单例模式
	 * 
	 * @return
	 */
	public static synchronized RemoteChangeContractService getInstance() {
		if (gService == null) {
			gService = new RemoteChangeContractService();
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
	private RemoteChangeContractService() {

	}

	/**
	 * 获取某个项目的合同变更列表(工程、采购)生效
	 * 
	 * @param tenant_id
	 * @param project_id
	 * @return
	 */
	public AsyncTaskPM360 getChangeContractList(
			final DataManagerInterface manager, int tenant_id, int project_id) {
		// 初始化返回类型
		Type type = AnalysisManager.GSON ? new TypeToken<List<Contract_change>>() {
		}.getType()
				: Contract_change.class;

		// 设置调用参数，调用远程服务
		return new RemoteService().setParams(SERVICE_NAME,
				"getChangeContractList", tenant_id, project_id).call(manager,
				type, Operation.QUERY);
	}

	/**
	 * 获取某个项目的合同变更列表(工程、采购)未生效
	 * 
	 * @param tenant_id
	 * @param project_id
	 * @return
	 */
	public AsyncTaskPM360 getChangeContractList2(
			final DataManagerInterface manager, int tenant_id, int project_id) {
		// 初始化返回类型
		Type type = AnalysisManager.GSON ? new TypeToken<List<Contract_change>>() {
		}.getType()
				: Contract_change.class;

		// 设置调用参数，调用远程服务
		return new RemoteService().setParams(SERVICE_NAME,
				"getChangeContractList2", tenant_id, project_id).call(manager,
				type, Operation.QUERY);
	}

	/**
	 * 统计某个变更的变更金额
	 * 
	 * @param contract_change_ids
	 *            (变更ID以逗号分开)
	 * @return
	 */
	public AsyncTaskPM360 getChangeContractMoney(
			final DataManagerInterface manager, String contract_change_ids) {
		// 初始化返回类型
		Type type = AnalysisManager.GSON ? new TypeToken<List<Contract_change>>() {
		}.getType()
				: Contract_change.class;

		// 设置调用参数，调用远程服务
		return new RemoteService().setParams(SERVICE_NAME,
				"getChangeContractMoney", contract_change_ids).call(manager,
				type, Operation.QUERY);
	}

	/**
	 * 获取某个合同的变更详细信息
	 * 
	 * @param id
	 * @param tenant_id
	 * @return
	 */
	public AsyncTaskPM360 getChangeContractDetail(
			final DataManagerInterface manager, int id, int tenant_id) {
		// 初始化返回类型
		Type type = AnalysisManager.GSON ? new TypeToken<List<Contract_change>>() {
		}.getType()
				: Contract_change.class;

		// 设置调用参数，调用远程服务
		return new RemoteService().setParams(SERVICE_NAME,
				"getChangeContractDetail", id, tenant_id).call(manager, type,
				Operation.QUERY);
	}

	/**
	 * 根据消息里的type_id(flow_approval_id)查询变更ID
	 * 
	 * @param flow_approval_id
	 * 
	 * @return
	 */
	public AsyncTaskPM360 getChangeContractIDByFlowApprovalID(
			final DataManagerInterface manager, int flow_approval_id) {
		// 初始化返回类型
		Type type = AnalysisManager.GSON ? new TypeToken<List<Flow_approval>>() {
		}.getType()
				: Flow_approval.class;

		// 设置调用参数，调用远程服务
		return new RemoteService().setParams(SERVICE_NAME,
				"getChangeContractIDByFlowApprovalID", flow_approval_id).call(
				manager, type, Operation.QUERY);
	}

	// ---------------------------【不走审批流程，直接保存或者修改。状态为：内部通过（CONTRACT_CHANGE_STATUS[4][0]）】--------------------------------------------
	// ---------------------------【走审批流程，暂时保存或者修改，但不提交。状态为：未提交（CONTRACT_CHANGE_STATUS[2][0]）】--------------------------------------------

	/**
	 * 增加合同变更
	 * 
	 * @param Contract_changeJSON
	 * 
	 * @return
	 */
	public AsyncTaskPM360 addContractChange(final DataManagerInterface manager,
			Contract_change Contract_change) {
		// 初始化返回类型
		Type type = AnalysisManager.GSON ? new TypeToken<List<Contract_change>>() {
		}.getType()
				: Contract_change.class;

		// 设置调用参数，调用远程服务
		return new RemoteService().setParams(SERVICE_NAME, "addContractChange",
				Contract_change).call(manager, type, Operation.ADD);
	}

	/**
	 * 更新合同变更信息
	 * 
	 * @param Contract_changeJSON
	 * 
	 * @return
	 */
	public AsyncTaskPM360 updateChangeContract(
			final DataManagerInterface manager, Contract_change Contract_change) {
		// 设置调用参数，调用远程服务
		return new RemoteService().setParams(SERVICE_NAME,
				"updateChangeContract", Contract_change).call(manager,
				Operation.MODIFY);
	}

	// ---------------------------【走审批流程，新增、修改后直接提交。状态为：内部审批中（CONTRACT_CHANGE_STATUS[3][0]）】--------------------------------------------
	/**
	 * 增加合同变更
	 * 
	 * @param Contract_changeJSON
	 * @param flowApprovalJSON
	 * @return
	 */
	public AsyncTaskPM360 addContractChangeForSubmit(
			final DataManagerInterface manager,
			Contract_change Contract_change, Flow_approval Flow_approval) {
		// 初始化返回类型
		Type type = AnalysisManager.GSON ? new TypeToken<List<Contract_change>>() {
		}.getType()
				: Contract_change.class;

		// 设置调用参数，调用远程服务
		return new RemoteService().setParams(SERVICE_NAME,
				"addContractChangeForSubmit", Contract_change, Flow_approval)
				.call(manager, type, Operation.ADD);
	}

	/**
	 * 更新合同变更信息
	 * 
	 * @param Contract_changeJSON
	 * @param flowApprovalJSON
	 * @return
	 */
	public AsyncTaskPM360 updateChangeContractForSubmit(
			final DataManagerInterface manager,
			Contract_change Contract_change, Flow_approval Flow_approval) {
		// 设置调用参数，调用远程服务
		return new RemoteService()
				.setParams(SERVICE_NAME, "updateChangeContractForSubmit",
						Contract_change, Flow_approval).call(manager,
						Operation.MODIFY);
	}

	// ---------------------------【走审批流程，直接提交。状态为：内部审批中（CONTRACT_CHANGE_STATUS[3][0]）】--------------------------------------------
	/**
	 * 合同变更提交
	 * 
	 * @param Contract_paymentJSON
	 * @param flowApprovalJSON
	 * @return
	 */
	public AsyncTaskPM360 submit(final DataManagerInterface manager,
			Contract_change Contract_change, Flow_approval Flow_approval) {
		// 设置调用参数，调用远程服务
		return new RemoteService().setParams(SERVICE_NAME, "submit",
				Contract_change, Flow_approval).call(manager, Operation.MODIFY);
	}

	/**
	 * 删除某个合同的变更信息
	 * 
	 * @param id
	 * @return
	 */
	public AsyncTaskPM360 deleteChangeContract(
			final DataManagerInterface manager, int id) {
		// 设置调用参数，调用远程服务
		return new RemoteService().setParams(SERVICE_NAME,
				"deleteChangeContract", id).call(manager, Operation.DELETE);
	}

	/**
	 * 获取某个合同变更清单项列表
	 * 
	 * @param contract_change_id
	 * 
	 * @return
	 */
	public AsyncTaskPM360 getChangeList(final DataManagerInterface manager,
			int contract_change_id) {
		// 初始化返回类型
		Type type = AnalysisManager.GSON ? new TypeToken<List<Contract_list>>() {
		}.getType()
				: Contract_list.class;

		// 设置调用参数，调用远程服务
		return new RemoteService().setParams(SERVICE_NAME, "getChangeList",
				contract_change_id).call(manager, type, Operation.QUERY);
	}

	/**
	 * 新增清单项
	 * 
	 * @param Contract_listJSON
	 * 
	 * @return
	 */
	public AsyncTaskPM360 addChangeList(final DataManagerInterface manager,
			Contract_list Contract_list) {
		// 初始化返回类型
		Type type = AnalysisManager.GSON ? new TypeToken<List<Contract_list>>() {
		}.getType()
				: Contract_list.class;

		// 设置调用参数，调用远程服务
		return new RemoteService().setParams(SERVICE_NAME, "addChangeList",
				Contract_list).call(manager, type, Operation.ADD);
	}

	/**
	 * 更新一个清单项
	 * 
	 * @param Contract_listJSON
	 * 
	 * @return
	 */
	public AsyncTaskPM360 updateChangeList(final DataManagerInterface manager,
			Contract_list Contract_list) {
		// 设置调用参数，调用远程服务
		return new RemoteService().setParams(SERVICE_NAME, "updateChangeList",
				Contract_list).call(manager, Operation.MODIFY);
	}

	/**
	 * 删除某个合同的某一个清单项
	 * 
	 * @param id
	 * 
	 * @return
	 */
	public AsyncTaskPM360 deleteChangeList(final DataManagerInterface manager,
			int id) {
		// 设置调用参数，调用远程服务
		return new RemoteService().setParams(SERVICE_NAME, "deleteChangeList",
				id).call(manager, Operation.DELETE);
	}

	/**
	 * 获取某个合同变更下的文档(变更文档、清单项文档)
	 * 
	 * @param contract_change_id
	 * @param contract_change_attachments
	 * @return
	 */
	public void getChangeContractFiles(final DataManagerInterface manager,
			int contract_change_id, String contract_change_attachments) {
		// 初始化返回类型
		Type type = AnalysisManager.GSON ? new TypeToken<List<Files>>() {
		}.getType() : Files.class;

		// 设置调用参数，调用远程服务
		new RemoteService().setParams(SERVICE_NAME, "getChangeContractFiles",
				contract_change_id, contract_change_attachments).call(manager,
				type, Operation.QUERY);
	}
}
