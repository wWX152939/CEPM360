package com.pm360.cepm360.services.storehouse;

import com.google.gson.reflect.TypeToken;
import com.pm360.cepm360.app.common.activity.DataManagerInterface;
import com.pm360.cepm360.common.json.AnalysisManager;
import com.pm360.cepm360.entity.P_CK;
import com.pm360.cepm360.entity.P_CKD;
import com.pm360.cepm360.services.common.AsyncTaskPM360;
import com.pm360.cepm360.services.common.Operation;
import com.pm360.cepm360.services.common.RemoteService;

import java.lang.reflect.Type;
import java.util.List;

/**
 * 出库服务
 * 
 * @author Andy
 * 
 */
public class RemoteOutStoreService {
	// 框架服务类全限定名
	private static final String SERVICE_NAME = "com.pm360.cepm360.services.storehouse.OutStoreService";
	// 单例类变量
	private static RemoteOutStoreService gService;

	/**
	 * 单例模式
	 * 
	 * @return
	 */
	public static synchronized RemoteOutStoreService getInstance() {
		if (gService == null) {
			gService = new RemoteOutStoreService();
		}
		return gService;
	}

	/**
	 * 销毁实例对象
	 */
	public static void destroyInstance() {
		gService = null;
	}

	private RemoteOutStoreService() {

	}

	/**
	 * 获取出库列表
	 * 
	 * @param tenant_id_
	 * @param task_id_
	 * @param project_id_
	 * @return CK
	 */
	public AsyncTaskPM360 getOutStoreList(final DataManagerInterface manager,
			int tenant_id, int task_id, int project_id) {
		// 初始化返回类型
		Type type = AnalysisManager.GSON ? new TypeToken<List<P_CK>>() {
		}.getType() : P_CK.class;

		// 设置调用参数，调用远程服务
		return new RemoteService().setParams(SERVICE_NAME, "getOutStoreList",
				tenant_id, task_id, project_id).call(manager, type,
				Operation.QUERY);
	}

	/**
	 * 查看出库单
	 * 
	 * @param ck_id
	 * @return
	 */
	public AsyncTaskPM360 getCKD(final DataManagerInterface manager, int ck_id) {
		// 初始化返回类型
		Type type = AnalysisManager.GSON ? new TypeToken<List<P_CKD>>() {
		}.getType() : P_CKD.class;
		// 设置调用参数，调用远程服务
		return new RemoteService().setParams(SERVICE_NAME, "getCKD", ck_id)
				.call(manager, type, Operation.QUERY);
	}

	/**
	 * 增加出库
	 * 
	 * @param P_CKJSON
	 * @param P_CKDJSONList
	 * @return
	 */
	public AsyncTaskPM360 addCK(final DataManagerInterface manager, P_CK P_CK,
			List<P_CKD> ckdList) {
		// 初始化返回类型
		Type type = AnalysisManager.GSON ? new TypeToken<List<P_CK>>() {
		}.getType() : P_CK.class;
		// 设置调用参数，调用远程服务
		return new RemoteService().setParams(SERVICE_NAME, "addCK", P_CK,
				ckdList).call(manager, type, Operation.ADD);
	}

	/**
	 * 更新出库
	 * 
	 * @param P_CKJSON
	 * @param P_CKDJSONList
	 * @return
	 */
	public AsyncTaskPM360 updateCK(final DataManagerInterface manager,
			P_CK P_CK, List<P_CKD> ckdList) {
		// 初始化返回类型
		Type type = AnalysisManager.GSON ? new TypeToken<List<P_CK>>() {
		}.getType() : P_CK.class;
		// 设置调用参数，调用远程服务
		return new RemoteService().setParams(SERVICE_NAME, "updateCK", P_CK,
				ckdList).call(manager, type, Operation.MODIFY);
	}

	/**
	 * 删除出库
	 * 
	 * @param ck_id
	 * @return
	 */
	public AsyncTaskPM360 deleteCK(final DataManagerInterface manager, int ck_id) {
		// 设置调用参数，调用远程服务
		return new RemoteService().setParams(SERVICE_NAME, "deleteCK", ck_id)
				.call(manager, Operation.DELETE);
	}
}
