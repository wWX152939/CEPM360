package com.pm360.cepm360.services.change;

import com.google.gson.reflect.TypeToken;
import com.pm360.cepm360.app.common.activity.DataManagerInterface;
import com.pm360.cepm360.common.json.AnalysisManager;
import com.pm360.cepm360.entity.Files;
import com.pm360.cepm360.entity.P_GCQZ;
import com.pm360.cepm360.services.common.AsyncTaskPM360;
import com.pm360.cepm360.services.common.Operation;
import com.pm360.cepm360.services.common.RemoteService;

import java.lang.reflect.Type;
import java.util.List;

/**
 * 
 * 签证服务
 * 
 * @author Andy
 * 
 */
public class RemoteChangeService {
	// 任务服务的类全限定名
	private static final String SERVICE_NAME = "com.pm360.cepm360.services.change.ChangeService";
	// 单例对象类变量
	private static RemoteChangeService gService;

	/**
	 * 单例模式
	 * 
	 * @return
	 */
	public static synchronized RemoteChangeService getInstance() {
		if (gService == null) {
			gService = new RemoteChangeService();
		}
		return gService;
	}

	/**
	 * 销毁实例对象
	 */
	public static void destroyInstance() {
		gService = null;
	}

	/**
	 * 禁止外部通过构造函数实例化对象
	 */
	private RemoteChangeService() {

	}

	/**
	 * 获取工程签证列表
	 * 
	 * @param tenant_id_
	 * @param project_id_
	 * @return
	 */
	public AsyncTaskPM360 getGCQZList(final DataManagerInterface manager,
			int tenant_id, int project_id) {
		// 初始化返回类型
		Type type = AnalysisManager.GSON ? new TypeToken<List<P_GCQZ>>() {
		}.getType() : P_GCQZ.class;

		// 设置调用参数，调用远程服务
		return new RemoteService().setParams(SERVICE_NAME, "getGCQZList",
				tenant_id, project_id).call(manager, type, Operation.QUERY);
	}

	/**
	 * 增加一个签证
	 * 
	 * @param P_GCQZJSON
	 * @return
	 */
	public AsyncTaskPM360 addGCQZ(final DataManagerInterface manager,
			P_GCQZ P_GCQZ) {
		// 初始化返回类型
		Type type = AnalysisManager.GSON ? new TypeToken<List<P_GCQZ>>() {
		}.getType() : P_GCQZ.class;
		// 设置调用参数，调用远程服务
		return new RemoteService().setParams(SERVICE_NAME, "addGCQZ", P_GCQZ)
				.call(manager, type, Operation.ADD);
	}

	/**
	 * 更新一个签证
	 * 
	 * @param P_GCQZJSON
	 * @return
	 */
	public AsyncTaskPM360 updateGCQZ(final DataManagerInterface manager,
			P_GCQZ P_GCQZ) {
		// 初始化返回类型
		Type type = AnalysisManager.GSON ? new TypeToken<List<P_GCQZ>>() {
		}.getType() : P_GCQZ.class;
		// 设置调用参数，调用远程服务
		return new RemoteService()
				.setParams(SERVICE_NAME, "updateGCQZ", P_GCQZ).call(manager,
						type, Operation.MODIFY);
	}

	/**
	 * 删除某个签证
	 * 
	 * @param gcqz_id
	 * @return
	 */
	public AsyncTaskPM360 deleteGCQZ(final DataManagerInterface manager,
			int gcqz_id) {
		// 设置调用参数，调用远程服务
		return new RemoteService().setParams(SERVICE_NAME, "deleteGCQZ",
				gcqz_id).call(manager, Operation.DELETE);
	}

	/**
	 * 获取某个签证下的签证单或者附件
	 * 
	 * @param gcqz_id
	 * @param dir_type
	 *            （DIR_TYPE_QZWD OR DIR_TYPE_QZWD_ATTACHMENT）
	 * @return
	 */
	public AsyncTaskPM360 getGCBGFiles(final DataManagerInterface manager,
			int gcqz_id, int dir_type) {
		// 初始化返回类型
		Type type = AnalysisManager.GSON ? new TypeToken<List<Files>>() {
		}.getType() : Files.class;

		// 设置调用参数，调用远程服务
		return new RemoteService().setParams(SERVICE_NAME, "getGCBGFiles",
				gcqz_id, dir_type).call(manager, type, Operation.QUERY);
	}
}
