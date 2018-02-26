package com.pm360.cepm360.services.resource;

import com.google.gson.reflect.TypeToken;
import com.pm360.cepm360.app.common.activity.DataManagerInterface;
import com.pm360.cepm360.common.json.AnalysisManager;
import com.pm360.cepm360.entity.P_WBRGNR;
import com.pm360.cepm360.entity.P_WBRGNR_DIR;
import com.pm360.cepm360.services.common.AsyncTaskPM360;
import com.pm360.cepm360.services.common.Operation;
import com.pm360.cepm360.services.common.RemoteService;

import java.lang.reflect.Type;
import java.util.List;

/**
 * 外包内容资源服务
 * 
 * @author Andy
 * 
 */
public class RemoteSubContentService {
	// 框架服务类全限定名
	private static final String SERVICE_NAME = "com.pm360.cepm360.services.resource.SubContentService";
	// 单例类变量
	private static RemoteSubContentService gService;

	/**
	 * 单例模式
	 * 
	 * @return
	 */
	public static synchronized RemoteSubContentService getInstance() {
		if (gService == null) {
			gService = new RemoteSubContentService();
		}
		return gService;
	}

	/**
	 * 销毁实例对象
	 */
	public static void destroyInstance() {
		gService = null;
	}

	private RemoteSubContentService() {

	}

	/**
	 * 获取外包内容目录
	 * 
	 * @param tenant_id_
	 * @return
	 */
	public AsyncTaskPM360 getWBRGNR_DIRList(final DataManagerInterface manager,
			int tenant_id) {
		// 初始化返回类型
		Type type = AnalysisManager.GSON ? new TypeToken<List<P_WBRGNR_DIR>>() {
		}.getType() : P_WBRGNR_DIR.class;
		// 设置调用参数，调用远程服务
		return new RemoteService().setParams(SERVICE_NAME, "getWBRGNR_DIRList",
				tenant_id).call(manager, type, Operation.QUERY);
	}

	/**
	 * 获取某个目录结构下的外包内容
	 * 
	 * @param tenant_id_
	 * @return P_WBRGNR
	 */
	public AsyncTaskPM360 getWBRGNRList(final DataManagerInterface manager,
			int tenant_id) {
		// 初始化返回类型
		Type type = AnalysisManager.GSON ? new TypeToken<List<P_WBRGNR>>() {
		}.getType() : P_WBRGNR.class;
		// 设置调用参数，调用远程服务
		return new RemoteService().setParams(SERVICE_NAME, "getWBRGNRList",
				tenant_id).call(manager, type, Operation.QUERY);
	}

	/**
	 * 增加分包内容目录
	 * 
	 * @param P_WBRGNR_DIRJSON
	 * @return
	 */
	public AsyncTaskPM360 addWBRGNR_DIR(final DataManagerInterface manager,
			P_WBRGNR_DIR P_WBRGNR_DIR) {
		// 设置返回类型
		Type type = AnalysisManager.GSON ? new TypeToken<List<P_WBRGNR_DIR>>() {
		}.getType() : P_WBRGNR_DIR.class;
		// 设置调用参数，调用远程服务
		return new RemoteService().setParams(SERVICE_NAME, "addWBRGNR_DIR",
				P_WBRGNR_DIR).call(manager, type,Operation.ADD);
	}

	/**
	 * 增加分包内容信息
	 * 
	 * @param P_WBRGNRJSON
	 * @return
	 */
	public AsyncTaskPM360 addWBRGNR(final DataManagerInterface manager,
			P_WBRGNR P_WBRGNR) {
		// 设置返回类型
		Type type = AnalysisManager.GSON ? new TypeToken<List<P_WBRGNR>>() {
		}.getType() : P_WBRGNR.class;
		// 设置调用参数，调用远程服务
		return new RemoteService().setParams(SERVICE_NAME, "addWBRGNR",
				P_WBRGNR).call(manager,type, Operation.ADD);
	}

	/**
	 * 更新分包内容目录
	 * 
	 * @param P_WBRGNR_DIRJSON
	 * @return
	 */
	public AsyncTaskPM360 updateWBRGNR_DIR(final DataManagerInterface manager,
			P_WBRGNR_DIR P_WBRGNR_DIR) {
		// 设置调用参数，调用远程服务
		return new RemoteService().setParams(SERVICE_NAME, "updateWBRGNR_DIR",
				P_WBRGNR_DIR).call(manager, Operation.MODIFY);
	}

	/**
	 * 更新分包内容信息
	 * 
	 * @param P_WBRGNRJSON
	 * @return
	 */
	public AsyncTaskPM360 updateWBRGNR(final DataManagerInterface manager,
			P_WBRGNR P_WBRGNR) {
		// 设置调用参数，调用远程服务
		return new RemoteService().setParams(SERVICE_NAME, "updateWBRGNR",
				P_WBRGNR).call(manager, Operation.MODIFY);
	}

	/**
	 * 删除分包内容目录
	 * 
	 * @param wbrgnr_dir_id
	 * @return
	 */
	public AsyncTaskPM360 deleteWBRGNR_DIR(final DataManagerInterface manager,
			int wbrgnr_dir_id) {
		// 设置调用参数，调用远程服务
		return new RemoteService().setParams(SERVICE_NAME, "deleteWBRGNR_DIR",
				wbrgnr_dir_id).call(manager, Operation.DELETE);
	}

	/**
	 * 删除分包内容信息
	 * 
	 * @param wbrgnr_id
	 * @return
	 */
	public AsyncTaskPM360 deleteWBRGNR(final DataManagerInterface manager,
			int wbrgnr_id) {
		// 设置调用参数，调用远程服务
		return new RemoteService().setParams(SERVICE_NAME, "deleteWBRGNR",
				wbrgnr_id).call(manager, Operation.DELETE);
	}

}
