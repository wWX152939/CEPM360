package com.pm360.cepm360.services.resource;

import com.google.gson.reflect.TypeToken;
import com.pm360.cepm360.app.common.activity.DataManagerInterface;
import com.pm360.cepm360.common.json.AnalysisManager;
import com.pm360.cepm360.entity.P_WZ;
import com.pm360.cepm360.entity.P_WZ_DIR;
import com.pm360.cepm360.services.common.AsyncTaskPM360;
import com.pm360.cepm360.services.common.Operation;
import com.pm360.cepm360.services.common.RemoteService;

import java.lang.reflect.Type;
import java.util.List;

/**
 * 物资资源 （材料和设备）服务
 * 
 * @author Andy
 * 
 */
public class RemoteGoodsService {
	// 框架服务类全限定名
	private static final String SERVICE_NAME = "com.pm360.cepm360.services.resource.GoodsService";
	// 单例类变量
	private static RemoteGoodsService gService;

	/**
	 * 单例模式
	 * 
	 * @return
	 */
	public static synchronized RemoteGoodsService getInstance() {
		if (gService == null) {
			gService = new RemoteGoodsService();
		}
		return gService;
	}

	/**
	 * 销毁实例对象
	 */
	public static void destroyInstance() {
		gService = null;
	}

	private RemoteGoodsService() {

	}

	/**
	 * 获取物资目录结构
	 * 
	 * @param tenant_id_
	 * @param wz_type
	 * @return WZ_DIR
	 */
	public AsyncTaskPM360 getWZ_DIRList(final DataManagerInterface manager,
			int tenant_id, String wz_type) {
		// 初始化返回类型
		Type type = AnalysisManager.GSON ? new TypeToken<List<P_WZ_DIR>>() {
		}.getType() : P_WZ_DIR.class;
		// 设置调用参数，调用远程服务
		return new RemoteService().setParams(SERVICE_NAME, "getWZ_DIRList",
				tenant_id, wz_type).call(manager, type, Operation.QUERY);
	}

	/**
	 * 获取某个目录结构下的物资信息
	 * 
	 * @param tenant_id_
	 * @param wz_type_
	 * @return WZ
	 */
	public AsyncTaskPM360 getWZList(final DataManagerInterface manager,
			int tenant_id, String wz_type) {
		// 初始化返回类型
		Type type = AnalysisManager.GSON ? new TypeToken<List<P_WZ>>() {
		}.getType() : P_WZ.class;
		// 设置调用参数，调用远程服务
		return new RemoteService().setParams(SERVICE_NAME, "getWZList",
				tenant_id, wz_type).call(manager, type, Operation.QUERY);
	}

	/**
	 * 增加物资目录
	 * 
	 * @param P_WZDIRJSON
	 * @return
	 */
	public AsyncTaskPM360 addWZ_DIR(final DataManagerInterface manager,
			P_WZ_DIR P_WZ_DIR) {
		// 设置返回类型
		Type type = AnalysisManager.GSON ? new TypeToken<List<P_WZ_DIR>>() {
		}.getType() : P_WZ_DIR.class;
		// 设置调用参数，调用远程服务
		return new RemoteService().setParams(SERVICE_NAME, "addWZ_DIR",
				P_WZ_DIR).call(manager,type, Operation.ADD);
	}

	/**
	 * 增加物资信息
	 * 
	 * @param P_WZJSON
	 * @return
	 */
	public AsyncTaskPM360 addWZ(final DataManagerInterface manager, P_WZ P_WZ) {
		// 设置返回类型
		Type type = AnalysisManager.GSON ? new TypeToken<List<P_WZ>>() {
		}.getType() : P_WZ.class;
		// 设置调用参数，调用远程服务
		return new RemoteService().setParams(SERVICE_NAME, "addWZ", P_WZ).call(
				manager, type,Operation.ADD);
	}

	/**
	 * 更新物资目录
	 * 
	 * @param P_WZDIRJSON
	 * @return
	 */
	public AsyncTaskPM360 updateWZ_DIR(final DataManagerInterface manager,
			P_WZ_DIR P_WZ_DIR) {
		// 设置调用参数，调用远程服务
		return new RemoteService().setParams(SERVICE_NAME, "updateWZ_DIR",
				P_WZ_DIR).call(manager, Operation.MODIFY);
	}

	/**
	 * 更新物资信息
	 * 
	 * @param P_WZJSON
	 * @return
	 */
	public AsyncTaskPM360 updateWZ(final DataManagerInterface manager, P_WZ P_WZ) {
		// 设置调用参数，调用远程服务
		return new RemoteService().setParams(SERVICE_NAME, "updateWZ", P_WZ)
				.call(manager, Operation.MODIFY);
	}

	/**
	 * 删除物资目录
	 * 
	 * @param wz_dir_id
	 * @return
	 */
	public AsyncTaskPM360 deleteWZ_DIR(final DataManagerInterface manager,
			int wz_dir_id) {
		// 设置调用参数，调用远程服务
		return new RemoteService().setParams(SERVICE_NAME, "deleteWZ_DIR",
				wz_dir_id).call(manager, Operation.DELETE);
	}

	/**
	 * 删除物资信息
	 * 
	 * @param wz_id
	 * @return
	 */
	public AsyncTaskPM360 deleteWZ(final DataManagerInterface manager, int wz_id) {
		// 设置调用参数，调用远程服务
		return new RemoteService().setParams(SERVICE_NAME, "deleteWZ", wz_id)
				.call(manager, Operation.DELETE);
	}

}
