package com.pm360.cepm360.services.purchase;

import com.google.gson.reflect.TypeToken;
import com.pm360.cepm360.app.common.activity.DataManagerInterface;
import com.pm360.cepm360.common.json.AnalysisManager;
import com.pm360.cepm360.entity.Flow_approval;
import com.pm360.cepm360.entity.P_CGYS;
import com.pm360.cepm360.entity.P_CGYSD;
import com.pm360.cepm360.services.common.AsyncTaskPM360;
import com.pm360.cepm360.services.common.Operation;
import com.pm360.cepm360.services.common.RemoteService;

import java.lang.reflect.Type;
import java.util.List;

/**
 * 采购预算服务
 * 
 * @author Andy
 * 
 */
public class RemoteBudgetService {
	// 框架服务类全限定名
	private static final String SERVICE_NAME = "com.pm360.cepm360.services.purchase.BudgetService";
	// 单例类变量
	private static RemoteBudgetService gService;

	/**
	 * 单例模式
	 * 
	 * @return
	 */
	public static synchronized RemoteBudgetService getInstance() {
		if (gService == null) {
			gService = new RemoteBudgetService();
		}
		return gService;
	}

	/**
	 * 销毁实例对象
	 */
	public static void destroyInstance() {
		gService = null;
	}

	private RemoteBudgetService() {

	}

	/**
	 * 预算列表
	 * 
	 * @param tenant_id_
	 * @param task_id_
	 * @param project_id_
	 * @return CGYS
	 */
	public AsyncTaskPM360 getCGYSList(final DataManagerInterface manager,
			int tenant_id, int task_id, int project_id) {
		// 初始化返回类型
		Type type = AnalysisManager.GSON ? new TypeToken<List<P_CGYS>>() {
		}.getType() : P_CGYS.class;
		// 设置调用参数，调用远程服务
		return new RemoteService().setParams(SERVICE_NAME, "getCGYSList",
				tenant_id, task_id, project_id).call(manager, type,
				Operation.QUERY);
	}

	/**
	 * 查看采购预算单
	 * 
	 * @param cgys_id
	 * @return
	 */
	public AsyncTaskPM360 getCGYSD(final DataManagerInterface manager,
			int cgys_id) {
		// 初始化返回类型
		Type type = AnalysisManager.GSON ? new TypeToken<List<P_CGYSD>>() {
		}.getType() : P_CGYSD.class;
		// 设置调用参数，调用远程服务
		return new RemoteService().setParams(SERVICE_NAME, "getCGYSD", cgys_id)
				.call(manager, type, Operation.QUERY);
	}

	// ---------------------------只保存或者修改--------------------------------------------
	/**
	 * 增加采购预算
	 * 
	 * @param P_CGYSJSON
	 * @param P_CGYSDJSONList
	 * @return
	 */
	public AsyncTaskPM360 addCGYS(final DataManagerInterface manager,
			P_CGYS P_CGYS, List<P_CGYSD> cgysdList) {
		// 设置调用参数，调用远程服务
		return new RemoteService().setParams(SERVICE_NAME, "addCGYS", P_CGYS,
				cgysdList).call(manager, Operation.ADD);
	}

	/**
	 * 更新采购预算
	 * 
	 * @param P_CGYSJSON
	 * @param P_CGYSDJSONList
	 * @return
	 */
	public AsyncTaskPM360 updateCGYS(final DataManagerInterface manager,
			P_CGYS P_CGYS, List<P_CGYSD> cgysdList) {
		// 设置调用参数，调用远程服务
		return new RemoteService().setParams(SERVICE_NAME, "updateCGYS",
				P_CGYS, cgysdList).call(manager, Operation.MODIFY);
	}

	// -----------------------------------------------------------提交-----------------------------------------------------------
	/**
	 * 新增直接提交（带审批和不带审批）
	 * 
	 * @param P_CGYSJSON
	 * @param P_CGYSDJSONList
	 * @param flowApprovalJSON
	 * @param flow_status
	 *            (GLOBAL.FLOW_STATUS)
	 * @return
	 */
	public AsyncTaskPM360 submitForAdd(final DataManagerInterface manager,
			P_CGYS P_CGYS, List<P_CGYSD> cgysdList,
			Flow_approval Flow_approval, String flow_status) {
		// 设置调用参数，调用远程服务
		return new RemoteService().setParams(SERVICE_NAME, "submitForAdd",
				P_CGYS, cgysdList, Flow_approval, flow_status).call(manager,
				Operation.ADD);
	}

	/**
	 * 修改后提交（带审批和不带审批）
	 * 
	 * @param P_CGYSJSON
	 * @param P_CGYSDJSONList
	 * @param flowApprovalJSON
	 * @param flow_status
	 *            (GLOBAL.FLOW_STATUS)
	 * @return
	 */
	public AsyncTaskPM360 submitForUpdate(final DataManagerInterface manager,
			P_CGYS P_CGYS, List<P_CGYSD> cgysdList,
			Flow_approval Flow_approval, String flow_status) {
		// 设置调用参数，调用远程服务
		return new RemoteService().setParams(SERVICE_NAME, "submitForUpdate",
				P_CGYS, cgysdList, Flow_approval, flow_status).call(manager,
				Operation.MODIFY);
	}

	/**
	 * 删除采购预算
	 * 
	 * @param cgys_id
	 * @return
	 */
	public AsyncTaskPM360 deleteCGYS(final DataManagerInterface manager,
			int cgys_id) {
		// 设置调用参数，调用远程服务
		return new RemoteService().setParams(SERVICE_NAME, "deleteCGYS",
				cgys_id).call(manager, Operation.DELETE);
	}
}
