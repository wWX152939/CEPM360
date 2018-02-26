package com.pm360.cepm360.services.expenses;

import com.google.gson.reflect.TypeToken;
import com.pm360.cepm360.app.common.activity.DataManagerInterface;
import com.pm360.cepm360.common.json.AnalysisManager;
import com.pm360.cepm360.entity.Contract;
import com.pm360.cepm360.entity.Contract_list;
import com.pm360.cepm360.entity.Contract_payment;
import com.pm360.cepm360.entity.Files;
import com.pm360.cepm360.entity.Flow_approval;
import com.pm360.cepm360.services.common.AsyncTaskPM360;
import com.pm360.cepm360.services.common.Operation;
import com.pm360.cepm360.services.common.RemoteService;

import java.lang.reflect.Type;
import java.util.List;

/**
 * 
 * 远程支出合同服务
 * 
 * @author Andy
 * 
 */
public class RemoteExpensesContractService {
	// 任务服务的类全限定名
	private static final String SERVICE_NAME = "com.pm360.cepm360.services.expenses.ExpensesContractService";
	// 单例对象类变量
	private static RemoteExpensesContractService gService;

	/**
	 * 单例模式
	 * 
	 * @return
	 */
	public static synchronized RemoteExpensesContractService getInstance() {
		if (gService == null) {
			gService = new RemoteExpensesContractService();
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
	private RemoteExpensesContractService() {

	}

	/**
	 * 获取某个项目的支出合同列表(工程、采购)
	 * 
	 * @param tenant_id
	 * @param project_id
	 * @return
	 */
	public AsyncTaskPM360 getExpensesContractList(
			final DataManagerInterface manager, int tenant_id, int project_id) {
		// 初始化返回类型
		Type type = AnalysisManager.GSON ? new TypeToken<List<Contract>>() {
		}.getType() : Contract.class;

		// 设置调用参数，调用远程服务
		return new RemoteService().setParams(SERVICE_NAME,
				"getExpensesContractList", tenant_id, project_id).call(manager,
				type, Operation.QUERY);
	}

	/**
	 * 获取某个项目的租赁合同
	 * 
	 * @param tenant_id
	 * @param project_id
	 * @return
	 */
	public AsyncTaskPM360 getLeaseContractList(
			final DataManagerInterface manager, int tenant_id, int project_id) {
		// 初始化返回类型
		Type type = AnalysisManager.GSON ? new TypeToken<List<Contract>>() {
		}.getType() : Contract.class;

		// 设置调用参数，调用远程服务
		return new RemoteService().setParams(SERVICE_NAME,
				"getLeaseContractList", tenant_id, project_id).call(manager,
				type, Operation.QUERY);
	}

	/**
	 * 获取某个项目的支出合同累计变更(工程、采购)
	 * 
	 * @param contract_code
	 *            （多个合同code逗号形式分开）
	 * @return
	 */
	public AsyncTaskPM360 getExpensesContractByToatlChanges(
			final DataManagerInterface manager, String contract_code) {
		// 初始化返回类型
		Type type = AnalysisManager.GSON ? new TypeToken<List<Contract>>() {
		}.getType() : Contract.class;

		// 设置调用参数，调用远程服务
		return new RemoteService().setParams(SERVICE_NAME,
				"getExpensesContractByToatlChanges", contract_code).call(
				manager, type, Operation.QUERY);
	}

	/**
	 * 获取某个项目的支出合同累计回款(工程、采购)
	 * 
	 * @param contract_id
	 *            （多个合同contract_id逗号形式分开）
	 * @return
	 */
	public AsyncTaskPM360 getExpensesContractByToatlPaid(
			final DataManagerInterface manager, String contract_id) {
		// 初始化返回类型
		Type type = AnalysisManager.GSON ? new TypeToken<List<Contract>>() {
		}.getType() : Contract.class;

		// 设置调用参数，调用远程服务
		return new RemoteService().setParams(SERVICE_NAME,
				"getExpensesContractByToatlPaid", contract_id).call(manager,
				type, Operation.QUERY);
	}

	/**
	 * 获取某个合同得详细信息
	 * 
	 * @param contract_id
	 * @return
	 */
	public AsyncTaskPM360 getExpensesContractDetail(
			final DataManagerInterface manager, int contract_id) {
		// 初始化返回类型
		Type type = AnalysisManager.GSON ? new TypeToken<List<Contract>>() {
		}.getType() : Contract.class;

		// 设置调用参数，调用远程服务
		return new RemoteService().setParams(SERVICE_NAME,
				"getExpensesContractDetail", contract_id).call(manager, type,
				Operation.QUERY);
	}

	/**
	 * 增加合同
	 * 
	 * @param contractJSON
	 * 
	 * @return
	 */
	public AsyncTaskPM360 addExpensesContract(
			final DataManagerInterface manager, Contract contract) {
		// 初始化返回类型
		Type type = AnalysisManager.GSON ? new TypeToken<List<Contract>>() {
		}.getType() : Contract.class;

		// 设置调用参数，调用远程服务
		return new RemoteService().setParams(SERVICE_NAME,
				"addExpensesContract", contract).call(manager, type,
				Operation.ADD);
	}

	/**
	 * 更新合同
	 * 
	 * @param contractJSON
	 * 
	 * @return
	 */
	public AsyncTaskPM360 updateExpensesContract(
			final DataManagerInterface manager, Contract contract) {
		// 设置调用参数，调用远程服务
		return new RemoteService().setParams(SERVICE_NAME,
				"updateExpensesContract", contract).call(manager,
				Operation.MODIFY);
	}

	/**
	 * 删除某个合同
	 * 
	 * @param contract_id
	 * @return
	 */
	public AsyncTaskPM360 deleteExpensesContract(
			final DataManagerInterface manager, int contract_id) {
		// 设置调用参数，调用远程服务
		return new RemoteService().setParams(SERVICE_NAME,
				"deleteExpensesContract", contract_id).call(manager,
				Operation.DELETE);
	}

	/**
	 * 获取某个合同清单项列表
	 * 
	 * @param contract_id
	 * @param contract_code
	 * @param contract_type
	 * @return
	 */
	public AsyncTaskPM360 getExpensesList(final DataManagerInterface manager,
			int contract_id, String contract_code, String contract_type) {
		// 初始化返回类型
		Type type = AnalysisManager.GSON ? new TypeToken<List<Contract_list>>() {
		}.getType()
				: Contract_list.class;

		// 设置调用参数，调用远程服务
		return new RemoteService().setParams(SERVICE_NAME, "getExpensesList",
				contract_id, contract_code, contract_type).call(manager, type,
				Operation.QUERY);
	}

	/**
	 * 新增清单项
	 * 
	 * @param Contract_listJSON
	 * @param contract_type
	 * @return
	 */
	public AsyncTaskPM360 addExpensesList(final DataManagerInterface manager,
			Contract_list Contract_list, String contract_type) {
		// 初始化返回类型
		Type type = AnalysisManager.GSON ? new TypeToken<List<Contract_list>>() {
		}.getType()
				: Contract_list.class;

		// 设置调用参数，调用远程服务
		return new RemoteService().setParams(SERVICE_NAME, "addExpensesList",
				Contract_list, contract_type)
				.call(manager, type, Operation.ADD);
	}

	/**
	 * 更新一个清单项
	 * 
	 * @param Contract_listJSON
	 * @param contract_type
	 * @return
	 */
	public AsyncTaskPM360 updateExpensesList(
			final DataManagerInterface manager, Contract_list Contract_list,
			String contract_type) {
		// 设置调用参数，调用远程服务
		return new RemoteService().setParams(SERVICE_NAME,
				"updateExpensesList", Contract_list, contract_type).call(
				manager, Operation.MODIFY);
	}

	/**
	 * 删除某个合同的某一个清单项
	 * 
	 * @param Contract_listJSON
	 * @param contract_type
	 * @return
	 */
	public AsyncTaskPM360 deleteExpensesList(
			final DataManagerInterface manager, Contract_list Contract_list,
			String contract_type) {
		// 设置调用参数，调用远程服务
		return new RemoteService().setParams(SERVICE_NAME,
				"deleteExpensesList", Contract_list, contract_type).call(
				manager, Operation.DELETE);
	}

	/**
	 * 获取某个合同支付项列表
	 * 
	 * @param contract_id
	 * 
	 * @return
	 */
	public AsyncTaskPM360 getPayList(final DataManagerInterface manager,
			int contract_id) {
		// 初始化返回类型
		Type type = AnalysisManager.GSON ? new TypeToken<List<Contract_payment>>() {
		}.getType()
				: Contract_payment.class;

		// 设置调用参数，调用远程服务
		return new RemoteService().setParams(SERVICE_NAME, "getPayList",
				contract_id).call(manager, type, Operation.QUERY);
	}

	/**
	 * 获取某个合同付款项详细信息
	 * 
	 * @param pay_id
	 * 
	 * @return
	 */
	public AsyncTaskPM360 getPayDetail(final DataManagerInterface manager,
			int pay_id) {
		// 初始化返回类型
		Type type = AnalysisManager.GSON ? new TypeToken<List<Contract_payment>>() {
		}.getType()
				: Contract_payment.class;

		// 设置调用参数，调用远程服务
		return new RemoteService().setParams(SERVICE_NAME, "getPayDetail",
				pay_id).call(manager, type, Operation.QUERY);
	}

	/**
	 * 根据消息里的type_id(flow_approval_id)查询支付项pay_id
	 * 
	 * @param flow_approval_id
	 * @return Flow_approval (type_id)
	 */
	public AsyncTaskPM360 getPayIDByFlowApprovalID(
			final DataManagerInterface manager, int flow_approval_id) {
		// 初始化返回类型
		Type type = AnalysisManager.GSON ? new TypeToken<List<Flow_approval>>() {
		}.getType()
				: Flow_approval.class;

		// 设置调用参数，调用远程服务
		return new RemoteService().setParams(SERVICE_NAME,
				"getPayIDByFlowApprovalID", flow_approval_id).call(manager,
				type, Operation.QUERY);
	}

	// ---------------------------【不走审批流程，直接保存或者修改。状态为：通过（FLOW_APPROVAL_STATUS[2][0]）】--------------------------------------------
	// ---------------------------【走审批流程，暂时保存或者修改，但不提交。状态为：未提交（FLOW_APPROVAL_STATUS[0][0]）】--------------------------------------------

	/**
	 * 新增一个合同支付项
	 * 
	 * @param Contract_paymentJSON
	 * 
	 * @return
	 */
	public AsyncTaskPM360 addPay(final DataManagerInterface manager,
			Contract_payment Contract_payment) {
		// 初始化返回类型
		Type type = AnalysisManager.GSON ? new TypeToken<List<Contract_payment>>() {
		}.getType()
				: Contract_payment.class;

		// 设置调用参数，调用远程服务
		return new RemoteService().setParams(SERVICE_NAME, "addPay",
				Contract_payment).call(manager, type, Operation.ADD);
	}

	/**
	 * 更新一个合同支付项
	 * 
	 * @param Contract_paymentJSON
	 * 
	 * @return
	 */
	public AsyncTaskPM360 updatePay(final DataManagerInterface manager,
			Contract_payment Contract_payment) {
		// 设置调用参数，调用远程服务
		return new RemoteService().setParams(SERVICE_NAME, "updatePay",
				Contract_payment).call(manager, Operation.MODIFY);
	}

	// ---------------------------【走审批流程，新增、修改后直接提交。状态为：审批中（FLOW_APPROVAL_STATUS[1][0]）】--------------------------------------------
	/**
	 * 新增一个合同付款项
	 * 
	 * @param Contract_paymentJSON
	 * @param flowApprovalJSON
	 * @return
	 */
	public AsyncTaskPM360 addPayForSubmit(final DataManagerInterface manager,
			Contract_payment Contract_payment, Flow_approval Flow_approval) {
		// 初始化返回类型
		Type type = AnalysisManager.GSON ? new TypeToken<List<Contract_payment>>() {
		}.getType()
				: Contract_payment.class;

		// 设置调用参数，调用远程服务
		return new RemoteService().setParams(SERVICE_NAME, "addPayForSubmit",
				Contract_payment, Flow_approval).call(manager, type,
				Operation.ADD);
	}

	/**
	 * 更新一个合同付款项
	 * 
	 * @param Contract_paymentJSON
	 * @param flowApprovalJSON
	 * @return
	 */
	public AsyncTaskPM360 updateForSubmit(final DataManagerInterface manager,
			Contract_payment Contract_payment, Flow_approval Flow_approval) {
		// 设置调用参数，调用远程服务
		return new RemoteService().setParams(SERVICE_NAME, "updateForSubmit",
				Contract_payment, Flow_approval)
				.call(manager, Operation.MODIFY);
	}

	// ---------------------------【走审批流程，直接提交。状态为：审批中（FLOW_APPROVAL_STATUS[1][0]）】--------------------------------------------
	/**
	 * 更新一个合同付款项
	 * 
	 * @param Contract_paymentJSON
	 * @param flowApprovalJSON
	 * @return
	 */
	public AsyncTaskPM360 submit(final DataManagerInterface manager,
			Contract_payment Contract_payment, Flow_approval Flow_approval) {
		// 设置调用参数，调用远程服务
		return new RemoteService().setParams(SERVICE_NAME, "submit",
				Contract_payment, Flow_approval)
				.call(manager, Operation.MODIFY);
	}

	/**
	 * 删除某个合同的某一个支付项
	 * 
	 * @param id
	 * 
	 * @return
	 */
	public AsyncTaskPM360 deletePay(final DataManagerInterface manager, int id) {
		// 设置调用参数，调用远程服务
		return new RemoteService().setParams(SERVICE_NAME, "deletePay", id)
				.call(manager, Operation.DELETE);
	}

	/**
	 * 获取某个合同下的文档(合同文档、支出项文档、清单项)
	 * 
	 * @param contract_id
	 * @param contract_attachments
	 * @return
	 */
	public void getContractFiles(final DataManagerInterface manager,
			int contract_id, String contract_attachments) {
		// 初始化返回类型
		Type type = AnalysisManager.GSON ? new TypeToken<List<Files>>() {
		}.getType() : Files.class;

		// 设置调用参数，调用远程服务
		new RemoteService().setParams(SERVICE_NAME, "getContractFiles",
				contract_id, contract_attachments).call(manager, type,
				Operation.QUERY);
	}

	/**
	 * 申请付款
	 * 
	 * @param Contract_paymentJSON
	 * @param flowApprovalJSON
	 * @return
	 */
	public AsyncTaskPM360 applyPay(final DataManagerInterface manager,
			Contract_payment Contract_payment, Flow_approval Flow_approval) {
		// 设置调用参数，调用远程服务
		return new RemoteService().setParams(SERVICE_NAME, "applyPay",
				Contract_payment, Flow_approval)
				.call(manager, Operation.MODIFY);
	}

	/**
	 * 审批
	 * 
	 * @param Contract_paymentJSON
	 * @param currentFlowApprovalJSON
	 * @param nextFlowApprovalJSON
	 * @return
	 */
	public AsyncTaskPM360 approval(final DataManagerInterface manager,
			Contract_payment Contract_payment,
			Flow_approval currentFlow_approval, Flow_approval nextFlow_approval) {
		// 设置调用参数，调用远程服务
		return new RemoteService().setParams(SERVICE_NAME, "approval",
				Contract_payment, currentFlow_approval, nextFlow_approval)
				.call(manager, Operation.MODIFY);
	}
}
