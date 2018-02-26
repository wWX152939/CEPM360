package com.pm360.cepm360.services.resource;

import com.google.gson.reflect.TypeToken;
import com.pm360.cepm360.app.common.activity.DataManagerInterface;
import com.pm360.cepm360.common.json.AnalysisManager;
import com.pm360.cepm360.entity.Mavin_lib;
import com.pm360.cepm360.services.common.AsyncTaskPM360;
import com.pm360.cepm360.services.common.Operation;
import com.pm360.cepm360.services.common.RemoteService;

import java.lang.reflect.Type;
import java.util.List;

/**
 * 远程专家库服务
 * 
 * @author Andy
 * 
 */
public class RemoteMavinLibService {
	// 框架服务类全限定名
	private static final String SERVICE_NAME = "com.pm360.cepm360.services.resource.MavinLibService";
	// 单例类变量
	private static RemoteMavinLibService gService;

	/**
	 * 单例模式
	 * 
	 * @return
	 */
	public static synchronized RemoteMavinLibService getInstance() {
		if (gService == null) {
			gService = new RemoteMavinLibService();
		}
		return gService;
	}

	/**
	 * 销毁实例对象
	 */
	public static void destroyInstance() {
		gService = null;
	}

	private RemoteMavinLibService() {

	}

	/**
	 * 获取专家列表
	 * 
	 * @param tenant_id
	 * @return
	 */
	public AsyncTaskPM360 getMavinLibList(final DataManagerInterface manager,
			int tenant_id) {
		// 初始化返回类型
		Type type = AnalysisManager.GSON ? new TypeToken<List<Mavin_lib>>() {
		}.getType() : Mavin_lib.class;
		// 设置调用参数，调用远程服务
		return new RemoteService().setParams(SERVICE_NAME, "getMavinLibList",
				tenant_id).call(manager, type, Operation.QUERY);
	}

	/**
	 * 增加专家信息
	 * 
	 * @param Mavin_libJSON
	 * @return
	 */
	public AsyncTaskPM360 addMavin(final DataManagerInterface manager,
			Mavin_lib Mavin_lib) {
		// 设置返回类型
		Type type = AnalysisManager.GSON ? new TypeToken<List<Mavin_lib>>() {
		}.getType() : Mavin_lib.class;
		// 设置调用参数，调用远程服务
		return new RemoteService().setParams(SERVICE_NAME, "addMavin",
				Mavin_lib).call(manager, type, Operation.ADD);
	}

	/**
	 * 删除专家
	 * 
	 * @param mavin_lib_id
	 * @return
	 */
	public AsyncTaskPM360 deleteMavin(final DataManagerInterface manager,
			int mavin_lib_id) {
		// 设置调用参数，调用远程服务
		return new RemoteService().setParams(SERVICE_NAME, "deleteMavin",
				mavin_lib_id).call(manager, Operation.DELETE);
	}

	/**
	 * 更新专家信息
	 * 
	 * @param Mavin_libJSON
	 * @return
	 */
	public AsyncTaskPM360 updateMavin(final DataManagerInterface manager,
			Mavin_lib Mavin_lib) {
		// 设置调用参数，调用远程服务
		return new RemoteService().setParams(SERVICE_NAME, "updateMavin",
				Mavin_lib).call(manager, Operation.MODIFY);
	}
}
