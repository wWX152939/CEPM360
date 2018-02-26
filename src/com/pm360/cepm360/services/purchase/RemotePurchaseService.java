package com.pm360.cepm360.services.purchase;

import com.google.gson.reflect.TypeToken;
import com.pm360.cepm360.app.common.activity.DataManagerInterface;
import com.pm360.cepm360.common.json.AnalysisManager;
import com.pm360.cepm360.entity.P_CG;
import com.pm360.cepm360.entity.P_CGD;
import com.pm360.cepm360.services.common.AsyncTaskPM360;
import com.pm360.cepm360.services.common.Operation;
import com.pm360.cepm360.services.common.RemoteService;

import java.lang.reflect.Type;
import java.util.List;

/**
 * 材料信息服务
 * 
 * @author Andy
 * 
 */
public class RemotePurchaseService {
	// 框架服务类全限定名
	private static final String SERVICE_NAME = "com.pm360.cepm360.services.purchase.PurchaseService";
	// 单例类变量
	private static RemotePurchaseService gService;

	/**
	 * 单例模式
	 * 
	 * @return
	 */
	public static synchronized RemotePurchaseService getInstance() {
		if (gService == null) {
			gService = new RemotePurchaseService();
		}
		return gService;
	}

	/**
	 * 销毁实例对象
	 */
	public static void destroyInstance() {
		gService = null;
	}

	private RemotePurchaseService() {

	}

	/**
	 * 采购列表
	 * 
	 * @param tenant_id_
	 * @param task_id_
	 * @param project_id_
	 * @return CG
	 */
	public AsyncTaskPM360 getCGList(final DataManagerInterface manager,
			int tenant_id, int task_id, int project_id) {
		// 初始化返回类型
		Type type = AnalysisManager.GSON ? new TypeToken<List<P_CG>>() {
		}.getType() : P_CG.class;
		// 设置调用参数，调用远程服务
		return new RemoteService().setParams(SERVICE_NAME, "getCGList",
				tenant_id, task_id, project_id).call(manager, type,
				Operation.QUERY);
	}

	/**
	 * 查看采购
	 * 
	 * @param cg_id
	 * @return
	 */
	public AsyncTaskPM360 getCGD(final DataManagerInterface manager, int cg_id) {
		// 初始化返回类型
		Type type = AnalysisManager.GSON ? new TypeToken<List<P_CGD>>() {
		}.getType() : P_CGD.class;
		// 设置调用参数，调用远程服务
		return new RemoteService().setParams(SERVICE_NAME, "getCGD", cg_id)
				.call(manager, type, Operation.QUERY);
	}

	
	// /**
	// * 采购付款
	// *
	// * @param P_CGJSON
	// * @return
	// */
	// public AsyncTaskPM360 payment(final DataManagerInterface manager, P_CG
	// P_CG) {
	// // 设置调用参数，调用远程服务
	// return new RemoteService().setParams(SERVICE_NAME, "payment", P_CG)
	// .call(manager, Operation.MODIFY);
	// }

	/**
	 * 更新采购
	 * 
	 * @param P_CGJSON
	 * @param P_CGDJSONList
	 * @return
	 */
	public AsyncTaskPM360 updateCG(final DataManagerInterface manager,
			P_CG P_CG, List<P_CGD> cgdList) {
		// 设置调用参数，调用远程服务
		return new RemoteService().setParams(SERVICE_NAME, "updateCG", P_CG,
				cgdList).call(manager, Operation.MODIFY);
	}

	// /**
	// * 删除采购
	// *
	// * @param cg_id
	// * @return
	// */
	// public AsyncTaskPM360 deleteCG(final DataManagerInterface manager, int
	// cg_id) {
	// // 设置调用参数，调用远程服务
	// return new RemoteService().setParams(SERVICE_NAME, "deleteCG", cg_id)
	// .call(manager, Operation.DELETE);
	// }

	/**
	 * 更新采购到货的状态
	 * 
	 * @param P_CGDJSON
	 * @return
	 */
	public AsyncTaskPM360 updateCGStatus(final DataManagerInterface manager,
			P_CGD P_CGD) {
		// 设置调用参数，调用远程服务
		return new RemoteService().setParams(SERVICE_NAME, "updateCGStatus",
				P_CGD).call(manager, Operation.MODIFY);
	}

}
