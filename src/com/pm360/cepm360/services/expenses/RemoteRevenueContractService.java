package com.pm360.cepm360.services.expenses;

import com.google.gson.reflect.TypeToken;
import com.pm360.cepm360.app.common.activity.DataManagerInterface;
import com.pm360.cepm360.common.json.AnalysisManager;
import com.pm360.cepm360.entity.Contract;
import com.pm360.cepm360.entity.Contract_list;
import com.pm360.cepm360.entity.Contract_payment;
import com.pm360.cepm360.entity.Files;
import com.pm360.cepm360.services.common.AsyncTaskPM360;
import com.pm360.cepm360.services.common.Operation;
import com.pm360.cepm360.services.common.RemoteService;

import java.lang.reflect.Type;
import java.util.List;

/**
 * 
 * 远程 收入合同服务
 * 
 * @author Andy
 * 
 */
public class RemoteRevenueContractService {
	// 任务服务的类全限定名
	private static final String SERVICE_NAME = "com.pm360.cepm360.services.expenses.RevenueContractService";
	// 单例对象类变量
	private static RemoteRevenueContractService gService;

	/**
	 * 单例模式
	 * 
	 * @return
	 */
	public static synchronized RemoteRevenueContractService getInstance() {
		if (gService == null) {
			gService = new RemoteRevenueContractService();
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
	private RemoteRevenueContractService() {

	}

	/**
	 * 获取某个项目的收入合同列表(工程、采购)
	 * 
	 * @param tenant_id
	 * @param project_id
	 * @return
	 */
	public AsyncTaskPM360 getRevenueContractList(
			final DataManagerInterface manager, int tenant_id, int project_id) {
		// 初始化返回类型
		Type type = AnalysisManager.GSON ? new TypeToken<List<Contract>>() {
		}.getType() : Contract.class;

		// 设置调用参数，调用远程服务
		return new RemoteService().setParams(SERVICE_NAME,
				"getRevenueContractList", tenant_id, project_id).call(manager,
				type, Operation.QUERY);
	}

	/**
	 * 获取某个项目的收入合同累计变更(工程、采购)
	 * 
	 * @param contract_code
	 *            （多个合同code逗号形式分开）
	 * @return
	 */
	public AsyncTaskPM360 getRevenueContractByToatlChanges(
			final DataManagerInterface manager, String contract_code) {
		// 初始化返回类型
		Type type = AnalysisManager.GSON ? new TypeToken<List<Contract>>() {
		}.getType() : Contract.class;

		// 设置调用参数，调用远程服务
		return new RemoteService().setParams(SERVICE_NAME,
				"getRevenueContractByToatlChanges", contract_code).call(
				manager, type, Operation.QUERY);
	}

	/**
	 * 获取某个项目的收入合同累计回款(工程、采购)
	 * 
	 * @param contract_id
	 *            （多个合同contract_id逗号形式分开）
	 * @return
	 */
	public AsyncTaskPM360 getRevenueContractByToatlPaid(
			final DataManagerInterface manager, String contract_id) {
		// 初始化返回类型
		Type type = AnalysisManager.GSON ? new TypeToken<List<Contract>>() {
		}.getType() : Contract.class;

		// 设置调用参数，调用远程服务
		return new RemoteService().setParams(SERVICE_NAME,
				"getRevenueContractByToatlPaid", contract_id).call(manager,
				type, Operation.QUERY);
	}

	/**
	 * 获取某个合同得详细信息
	 * 
	 * @param contract_id
	 * @return
	 */
	public AsyncTaskPM360 getRevenueContractDetail(
			final DataManagerInterface manager, int contract_id) {
		// 初始化返回类型
		Type type = AnalysisManager.GSON ? new TypeToken<List<Contract>>() {
		}.getType() : Contract.class;

		// 设置调用参数，调用远程服务
		return new RemoteService().setParams(SERVICE_NAME,
				"getRevenueContractDetail", contract_id).call(manager, type,
				Operation.QUERY);
	}

	/**
	 * 增加合同
	 * 
	 * @param contractJSON
	 * 
	 * @return
	 */
	public AsyncTaskPM360 addRevenueContract(
			final DataManagerInterface manager, Contract contract) {
		// 初始化返回类型
		Type type = AnalysisManager.GSON ? new TypeToken<List<Contract>>() {
		}.getType() : Contract.class;

		// 设置调用参数，调用远程服务
		return new RemoteService().setParams(SERVICE_NAME,
				"addRevenueContract", contract).call(manager, type,
				Operation.ADD);
	}

	/**
	 * 更新合同
	 * 
	 * @param contractJSON
	 * 
	 * @return
	 */
	public AsyncTaskPM360 updateRevenueContract(
			final DataManagerInterface manager, Contract contract) {
		// 设置调用参数，调用远程服务
		return new RemoteService().setParams(SERVICE_NAME,
				"updateRevenueContract", contract).call(manager,
				Operation.MODIFY);
	}

	/**
	 * 删除某个合同
	 * 
	 * @param contract_id
	 * @return
	 */
	public AsyncTaskPM360 deleteRevenueContract(
			final DataManagerInterface manager, int contract_id) {
		// 设置调用参数，调用远程服务
		return new RemoteService().setParams(SERVICE_NAME,
				"deleteRevenueContract", contract_id).call(manager,
				Operation.DELETE);
	}

	/**
	 * 获取某个合同清单项列表
	 * 
	 * @param contract_id
	 * @param contract_code
	 * @return
	 */
	public AsyncTaskPM360 getRevenueList(final DataManagerInterface manager,
			int contract_id, String contract_code) {
		// 初始化返回类型
		Type type = AnalysisManager.GSON ? new TypeToken<List<Contract_list>>() {
		}.getType()
				: Contract_list.class;

		// 设置调用参数，调用远程服务
		return new RemoteService().setParams(SERVICE_NAME, "getRevenueList",
				contract_id, contract_code)
				.call(manager, type, Operation.QUERY);
	}

	/**
	 * 新增清单项
	 * 
	 * @param Contract_listJSON
	 * 
	 * @return
	 */
	public AsyncTaskPM360 addRevenueList(final DataManagerInterface manager,
			Contract_list Contract_list) {
		// 初始化返回类型
		Type type = AnalysisManager.GSON ? new TypeToken<List<Contract_list>>() {
		}.getType()
				: Contract_list.class;

		// 设置调用参数，调用远程服务
		return new RemoteService().setParams(SERVICE_NAME, "addRevenueList",
				Contract_list).call(manager, type, Operation.ADD);
	}

	/**
	 * 更新一个清单项
	 * 
	 * @param Contract_listJSON
	 * 
	 * @return
	 */
	public AsyncTaskPM360 updateRevenueList(final DataManagerInterface manager,
			Contract_list Contract_list) {
		// 设置调用参数，调用远程服务
		return new RemoteService().setParams(SERVICE_NAME, "updateRevenueList",
				Contract_list).call(manager, Operation.MODIFY);
	}

	/**
	 * 删除某个合同的某一个清单项
	 * 
	 * @param id
	 * 
	 * @return
	 */
	public AsyncTaskPM360 deleteRevenueList(final DataManagerInterface manager,
			int id) {
		// 设置调用参数，调用远程服务
		return new RemoteService().setParams(SERVICE_NAME, "deleteRevenueList",
				id).call(manager, Operation.DELETE);
	}

	/**
	 * 获取某个合同回款项列表
	 * 
	 * @param contract_id
	 * 
	 * @return
	 */
	public AsyncTaskPM360 getCashbackList(final DataManagerInterface manager,
			int contract_id) {
		// 初始化返回类型
		Type type = AnalysisManager.GSON ? new TypeToken<List<Contract_payment>>() {
		}.getType()
				: Contract_payment.class;

		// 设置调用参数，调用远程服务
		return new RemoteService().setParams(SERVICE_NAME, "getCashbackList",
				contract_id).call(manager, type, Operation.QUERY);
	}

	/**
	 * 新增一个合同回款项
	 * 
	 * @param Contract_paymentJSON
	 * 
	 * @return
	 */
	public AsyncTaskPM360 addCashback(final DataManagerInterface manager,
			Contract_payment Contract_payment) {
		// 初始化返回类型
		Type type = AnalysisManager.GSON ? new TypeToken<List<Contract_payment>>() {
		}.getType()
				: Contract_payment.class;

		// 设置调用参数，调用远程服务
		return new RemoteService().setParams(SERVICE_NAME, "addCashback",
				Contract_payment).call(manager, type, Operation.ADD);
	}

	/**
	 * 更新一个合同回款项
	 * 
	 * @param Contract_paymentJSON
	 * 
	 * @return
	 */
	public AsyncTaskPM360 updateCashback(final DataManagerInterface manager,
			Contract_payment Contract_payment) {
		// 设置调用参数，调用远程服务
		return new RemoteService().setParams(SERVICE_NAME, "updateCashback",
				Contract_payment).call(manager, Operation.MODIFY);
	}

	/**
	 * 删除某个合同的某一个回款项
	 * 
	 * @param id
	 * 
	 * @return
	 */
	public AsyncTaskPM360 deleteCashback(final DataManagerInterface manager,
			int id) {
		// 设置调用参数，调用远程服务
		return new RemoteService()
				.setParams(SERVICE_NAME, "deleteCashback", id).call(manager,
						Operation.DELETE);
	}

	/**
	 * 获取某个合同下的文档(合同文档、回款项文档、清单项)
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
}
