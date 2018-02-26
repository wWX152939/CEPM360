package com.pm360.cepm360.services.resource;

import com.google.gson.reflect.TypeToken;
import com.pm360.cepm360.app.common.activity.DataManagerInterface;
import com.pm360.cepm360.common.json.AnalysisManager;
import com.pm360.cepm360.entity.P_LWDW;
import com.pm360.cepm360.entity.P_LWDW_DIR;
import com.pm360.cepm360.services.common.AsyncTaskPM360;
import com.pm360.cepm360.services.common.Operation;
import com.pm360.cepm360.services.common.RemoteService;

import java.lang.reflect.Type;
import java.util.List;

/**
 * 往来单位服务
 * 
 * @author Andy
 * 
 */
public class RemoteLWCompanyService {

	// 框架服务类全限定名
	private static final String SERVICE_NAME = "com.pm360.cepm360.services.resource.LWCompanyService";
	// 单例类变量
	private static RemoteLWCompanyService gService;

	/**
	 * 单例模式
	 * 
	 * @return
	 */
	public static synchronized RemoteLWCompanyService getInstance() {
		if (gService == null) {
			gService = new RemoteLWCompanyService();
		}
		return gService;
	}

	/**
	 * 销毁实例对象
	 */
	public static void destroyInstance() {
		gService = null;
	}

	private RemoteLWCompanyService() {

	}

	/**
	 * 获取往来单位目录结构
	 * 
	 * @param tenant_id_
	 * @return LWDW_DIR
	 */
	public AsyncTaskPM360 getLWDW_DIRList(final DataManagerInterface manager,
			int tenant_id) {
		// 初始化返回类型
		Type type = AnalysisManager.GSON ? new TypeToken<List<P_LWDW_DIR>>() {
		}.getType() : P_LWDW_DIR.class;
		// 设置调用参数，调用远程服务
		return new RemoteService().setParams(SERVICE_NAME, "getLWDW_DIRList",
				tenant_id).call(manager, type, Operation.QUERY);
	}

	/**
	 * 获取某个目录结构下的单位信息
	 * 
	 * @param tenant_id_
	 * @return LWDW
	 */
	public AsyncTaskPM360 getLWDWList(final DataManagerInterface manager,
			int tenant_id_) {
		// 初始化返回类型
		Type type = AnalysisManager.GSON ? new TypeToken<List<P_LWDW>>() {
		}.getType() : P_LWDW.class;
		// 设置调用参数，调用远程服务
		return new RemoteService().setParams(SERVICE_NAME, "getLWDWList",
				tenant_id_).call(manager, type, Operation.QUERY);
	}

	/**
	 * 查询单位详细信息
	 * 
	 * @param lwdw_id
	 * @return LWDW
	 */
	public AsyncTaskPM360 getLWDWDetail(final DataManagerInterface manager,
			int lwdw_id) {
		// 初始化返回类型
		Type type = AnalysisManager.GSON ? new TypeToken<List<P_LWDW>>() {
		}.getType() : P_LWDW.class;
		// 设置调用参数，调用远程服务
		return new RemoteService().setParams(SERVICE_NAME, "getLWDWDetail",
				lwdw_id).call(manager, type, Operation.QUERY);
	}

	/**
	 * 增加单位目录
	 * 
	 * @param P_LWDWDIRJSON
	 * @return
	 */
	public AsyncTaskPM360 addLWDW_DIR(final DataManagerInterface manager,
			P_LWDW_DIR P_LWDW_DIR) {
		// 设置返回类型
		Type type = AnalysisManager.GSON ? new TypeToken<List<P_LWDW_DIR>>() {
		}.getType() : P_LWDW_DIR.class;
		// 设置调用参数，调用远程服务
		return new RemoteService().setParams(SERVICE_NAME, "addLWDW_DIR",
				P_LWDW_DIR).call(manager, type,Operation.ADD);
	}

	/**
	 * 增加单位信息
	 * 
	 * @param P_LWDWJSON
	 * @return
	 */
	public AsyncTaskPM360 addLWDW(final DataManagerInterface manager,
			P_LWDW P_LWDW) {
		// 设置返回类型
		Type type = AnalysisManager.GSON ? new TypeToken<List<P_LWDW>>() {
		}.getType() : P_LWDW.class;
		// 设置调用参数，调用远程服务
		return new RemoteService().setParams(SERVICE_NAME, "addLWDW", P_LWDW)
				.call(manager,type, Operation.ADD);
	}

	/**
	 * 更新单位目录
	 * 
	 * @param P_LWDWDIRJSON
	 * @return
	 */
	public AsyncTaskPM360 updateLWDW_DIR(final DataManagerInterface manager,
			P_LWDW_DIR P_LWDW_DIR) {
		// 设置调用参数，调用远程服务
		return new RemoteService().setParams(SERVICE_NAME, "updateLWDW_DIR",
				P_LWDW_DIR).call(manager, Operation.MODIFY);
	}

	/**
	 * 更新单位信息
	 * 
	 * @param P_LWDWJSON
	 * @return
	 */
	public AsyncTaskPM360 updateLWDW(final DataManagerInterface manager,
			P_LWDW P_LWDW) {
		// 设置调用参数，调用远程服务
		return new RemoteService()
				.setParams(SERVICE_NAME, "updateLWDW", P_LWDW).call(manager,
						Operation.MODIFY);
	}

	/**
	 * 删除单位目录
	 * 
	 * @param lwdw_dir_id
	 * @return
	 */
	public AsyncTaskPM360 deleteLWDW_DIR(final DataManagerInterface manager,
			int lwdw_dir_id) {
		// 设置调用参数，调用远程服务
		return new RemoteService().setParams(SERVICE_NAME, "deleteLWDW_DIR",
				lwdw_dir_id).call(manager, Operation.DELETE);
	}

	/**
	 * 删除单位信息
	 * 
	 * @param lwdw_id
	 * @return
	 */
	public AsyncTaskPM360 deleteLWDW(final DataManagerInterface manager,
			int lwdw_id) {
		// 设置调用参数，调用远程服务
		return new RemoteService().setParams(SERVICE_NAME, "deleteLWDW",
				lwdw_id).call(manager, Operation.DELETE);
	}

}
