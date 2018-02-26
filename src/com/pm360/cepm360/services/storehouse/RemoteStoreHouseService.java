package com.pm360.cepm360.services.storehouse;

import com.google.gson.reflect.TypeToken;
import com.pm360.cepm360.app.common.activity.DataManagerInterface;
import com.pm360.cepm360.common.json.AnalysisManager;
import com.pm360.cepm360.entity.P_KC;
import com.pm360.cepm360.services.common.AsyncTaskPM360;
import com.pm360.cepm360.services.common.Operation;
import com.pm360.cepm360.services.common.RemoteService;

import java.lang.reflect.Type;
import java.util.List;

/**
 * 库存信息服务
 * 
 * @author Andy
 * 
 */
public class RemoteStoreHouseService {
	// 框架服务类全限定名
	private static final String SERVICE_NAME = "com.pm360.cepm360.services.storehouse.StoreHouseService";
	// 单例类变量
	private static RemoteStoreHouseService gService;

	/**
	 * 单例模式
	 * 
	 * @return
	 */
	public static synchronized RemoteStoreHouseService getInstance() {
		if (gService == null) {
			gService = new RemoteStoreHouseService();
		}
		return gService;
	}

	/**
	 * 销毁实例对象
	 */
	public static void destroyInstance() {
		gService = null;
	}

	private RemoteStoreHouseService() {

	}

	/**
	 * 获取库存信息列表
	 * 
	 * @param tenant_id_
	 * @param wz_type_
	 * @return P_KC
	 */
	public AsyncTaskPM360 getKCList(final DataManagerInterface manager,
			int tenant_id, int wz_type) {
		// 初始化返回类型
		Type type = AnalysisManager.GSON ? new TypeToken<List<P_KC>>() {
		}.getType() : P_KC.class;

		// 设置调用参数，调用远程服务
		return new RemoteService().setParams(SERVICE_NAME, "getKCList",
				tenant_id, wz_type).call(manager, type, Operation.QUERY);
	}

	/**
	 * 根据入库的材料查询该材料的库存数量
	 * 
	 * @param tenant_id_
	 * @param rk_id_
	 * @return
	 */
	public AsyncTaskPM360 getKCAmountByRKID(final DataManagerInterface manager,
			int tenant_id, int rk_id_) {
		// 初始化返回类型
		Type type = AnalysisManager.GSON ? new TypeToken<List<P_KC>>() {
		}.getType() : P_KC.class;

		// 设置调用参数，调用远程服务
		return new RemoteService().setParams(SERVICE_NAME, "getKCAmountByRKID",
				tenant_id, rk_id_).call(manager, type, Operation.QUERY);
	}
}
