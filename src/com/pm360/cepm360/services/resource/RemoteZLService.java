package com.pm360.cepm360.services.resource;

import com.google.gson.reflect.TypeToken;
import com.pm360.cepm360.app.common.activity.DataManagerInterface;
import com.pm360.cepm360.common.json.AnalysisManager;
import com.pm360.cepm360.entity.P_ZL;
import com.pm360.cepm360.entity.P_ZL_DIR;
import com.pm360.cepm360.services.common.AsyncTaskPM360;
import com.pm360.cepm360.services.common.Operation;
import com.pm360.cepm360.services.common.RemoteService;

import java.lang.reflect.Type;
import java.util.List;

/**
 * 租赁维护
 * 
 * @author Andy
 * 
 */
public class RemoteZLService {
	// 框架服务类全限定名
	private static final String SERVICE_NAME = "com.pm360.cepm360.services.resource.ZLService";
	// 单例类变量
	private static RemoteZLService gService;

	/**
	 * 单例模式
	 * 
	 * @return
	 */
	public static synchronized RemoteZLService getInstance() {
		if (gService == null) {
			gService = new RemoteZLService();
		}
		return gService;
	}

	/**
	 * 销毁实例对象
	 */
	public static void destroyInstance() {
		gService = null;
	}

	private RemoteZLService() {

	}

	/**
	 * 获取租赁目录结构
	 * 
	 * @param tenant_id_
	 * @return ZL_DIR
	 */
	public AsyncTaskPM360 getZL_DIRList(final DataManagerInterface manager,
			int tenant_id) {
		// 初始化返回类型
		Type type = AnalysisManager.GSON ? new TypeToken<List<P_ZL_DIR>>() {
		}.getType() : P_ZL_DIR.class;
		// 设置调用参数，调用远程服务
		return new RemoteService().setParams(SERVICE_NAME, "getZL_DIRList",
				tenant_id).call(manager, type, Operation.QUERY);
	}

	/**
	 * 获取某个目录结构下的租赁信息
	 * 
	 * @param tenant_id_
	 * @return zl
	 */
	public AsyncTaskPM360 getZLList(final DataManagerInterface manager,
			int tenant_id) {
		// 初始化返回类型
		Type type = AnalysisManager.GSON ? new TypeToken<List<P_ZL>>() {
		}.getType() : P_ZL.class;
		// 设置调用参数，调用远程服务
		return new RemoteService().setParams(SERVICE_NAME, "getZLList",
				tenant_id).call(manager, type, Operation.QUERY);
	}

	/**
	 * 增加租赁目录
	 * 
	 * @param P_ZLDIRJSON
	 * @return
	 */
	public AsyncTaskPM360 addZL_DIR(final DataManagerInterface manager,
			P_ZL_DIR P_ZL_DIR) {
		// 设置返回类型
		Type type = AnalysisManager.GSON ? new TypeToken<List<P_ZL_DIR>>() {
		}.getType() : P_ZL_DIR.class;
		// 设置调用参数，调用远程服务
		return new RemoteService().setParams(SERVICE_NAME, "addZL_DIR",
				P_ZL_DIR).call(manager, type, Operation.ADD);
	}

	/**
	 * 增加租赁信息
	 * 
	 * @param P_ZLJSON
	 * @return
	 */
	public AsyncTaskPM360 addZL(final DataManagerInterface manager, P_ZL P_ZL) {
		// 设置返回类型
		Type type = AnalysisManager.GSON ? new TypeToken<List<P_ZL>>() {
		}.getType() : P_ZL.class;
		// 设置调用参数，调用远程服务
		return new RemoteService().setParams(SERVICE_NAME, "addZL", P_ZL).call(
				manager, type, Operation.ADD);
	}

	/**
	 * 更新租赁目录
	 * 
	 * @param P_ZLDIRJSON
	 * @return
	 */
	public AsyncTaskPM360 updateZL_DIR(final DataManagerInterface manager,
			P_ZL_DIR P_ZL_DIR) {
		// 设置调用参数，调用远程服务
		return new RemoteService().setParams(SERVICE_NAME, "updateZL_DIR",
				P_ZL_DIR).call(manager, Operation.MODIFY);
	}

	/**
	 * 更新租赁信息
	 * 
	 * @param P_ZLJSON
	 * @return
	 */
	public AsyncTaskPM360 updateZL(final DataManagerInterface manager, P_ZL P_ZL) {
		// 设置调用参数，调用远程服务
		return new RemoteService().setParams(SERVICE_NAME, "updateZL", P_ZL)
				.call(manager, Operation.MODIFY);
	}

	/**
	 * 删除租赁目录
	 * 
	 * @param zl_dir_id
	 * @return
	 */
	public AsyncTaskPM360 deleteZL_DIR(final DataManagerInterface manager,
			int zl_dir_id) {
		// 设置调用参数，调用远程服务
		return new RemoteService().setParams(SERVICE_NAME, "deleteZL_DIR",
				zl_dir_id).call(manager, Operation.DELETE);
	}

	/**
	 * 删除租赁信息
	 * 
	 * @param zl_id
	 * @return
	 */
	public AsyncTaskPM360 deleteZL(final DataManagerInterface manager, int zl_id) {
		// 设置调用参数，调用远程服务
		return new RemoteService().setParams(SERVICE_NAME, "deleteZL", zl_id)
				.call(manager, Operation.DELETE);
	}

}
