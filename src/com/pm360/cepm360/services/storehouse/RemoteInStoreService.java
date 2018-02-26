package com.pm360.cepm360.services.storehouse;

import com.google.gson.reflect.TypeToken;
import com.pm360.cepm360.app.common.activity.DataManagerInterface;
import com.pm360.cepm360.common.json.AnalysisManager;
import com.pm360.cepm360.entity.Files;
import com.pm360.cepm360.entity.P_RK;
import com.pm360.cepm360.services.common.AsyncTaskPM360;
import com.pm360.cepm360.services.common.Operation;
import com.pm360.cepm360.services.common.RemoteService;

import java.lang.reflect.Type;
import java.util.List;

/**
 * 入库服务
 * 
 * @author Andy
 * 
 */
public class RemoteInStoreService {
	// 框架服务类全限定名
	private static final String SERVICE_NAME = "com.pm360.cepm360.services.storehouse.InStoreService";
	// 单例类变量
	private static RemoteInStoreService gService;

	/**
	 * 单例模式
	 * 
	 * @return
	 */
	public static synchronized RemoteInStoreService getInstance() {
		if (gService == null) {
			gService = new RemoteInStoreService();
		}
		return gService;
	}

	/**
	 * 销毁实例对象
	 */
	public static void destroyInstance() {
		gService = null;
	}

	private RemoteInStoreService() {

	}

	/**
	 * 获取入库列表
	 * 
	 * @param tenant_id_
	 * @param task_id_
	 * @param project_id_
	 * @return
	 */
	public AsyncTaskPM360 getInStoreList(final DataManagerInterface manager,
			int tenant_id, int task_id, int project_id) {
		// 初始化返回类型
		Type type = AnalysisManager.GSON ? new TypeToken<List<P_RK>>() {
		}.getType() : P_RK.class;

		// 设置调用参数，调用远程服务
		return new RemoteService().setParams(SERVICE_NAME, "getInStoreList",
				tenant_id, task_id, project_id).call(manager, type,
				Operation.QUERY);
	}

	/**
	 * 清点物资
	 * 
	 * @param rk_id
	 * @return
	 */
	public AsyncTaskPM360 makeInventory(final DataManagerInterface manager,
			int rk_id) {
		// 设置调用参数，调用远程服务
		return new RemoteService().setParams(SERVICE_NAME, "makeInventory",
				rk_id).call(manager, Operation.MODIFY);
	}

	/**
	 * 入库
	 * 
	 * @param P_RKJSON
	 * @return
	 */
	public AsyncTaskPM360 checkIn(final DataManagerInterface manager, P_RK P_RK) {
		// 设置调用参数，调用远程服务
		return new RemoteService().setParams(SERVICE_NAME, "checkIn", P_RK)
				.call(manager, Operation.MODIFY);
	}

	/**
	 * 获取某个入库信息下的附件
	 * 
	 * @param rk_attachments
	 * @return
	 */
	public void getRKFiles(final DataManagerInterface manager,
			String rk_attachments) {
		// 初始化返回类型
		Type type = AnalysisManager.GSON ? new TypeToken<List<Files>>() {
		}.getType() : Files.class;

		// 设置调用参数，调用远程服务
		new RemoteService().setParams(SERVICE_NAME, "getRKFiles",
				rk_attachments).call(manager, type, Operation.QUERY);
	}
}
