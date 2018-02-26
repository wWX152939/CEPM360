package com.pm360.cepm360.services.templet;

import com.google.gson.reflect.TypeToken;
import com.pm360.cepm360.app.common.activity.DataManagerInterface;
import com.pm360.cepm360.common.json.AnalysisManager;
import com.pm360.cepm360.entity.Templet_WBS;
import com.pm360.cepm360.entity.Templet_WBS_dir;
import com.pm360.cepm360.services.common.AsyncTaskPM360;
import com.pm360.cepm360.services.common.Operation;
import com.pm360.cepm360.services.common.RemoteService;

import java.lang.reflect.Type;
import java.util.List;

/**
 * WBS服务
 * 
 * @author Andy
 * 
 */
public class RemoteWBSService {

	// 框架服务类全限定名
	private static final String SERVICE_NAME = "com.pm360.cepm360.services.templet.WBSService";
	// 单例类变量
	private static RemoteWBSService gService;

	/**
	 * 单例模式
	 * 
	 * @return
	 */
	public static synchronized RemoteWBSService getInstance() {
		if (gService == null) {
			gService = new RemoteWBSService();
		}
		return gService;
	}

	/**
	 * 销毁实例对象
	 */
	public static void destroyInstance() {
		gService = null;
	}

	private RemoteWBSService() {

	}

	/**
	 * 获取文档模板列表
	 * 
	 * @param tenant_id_
	 * @param type_
	 * @return
	 */
	public AsyncTaskPM360 getWBSTmpList(final DataManagerInterface manager,
			int tenant_id, int type_) {
		// 初始化返回类型
		Type type = AnalysisManager.GSON ? new TypeToken<List<Templet_WBS>>() {
		}.getType() : Templet_WBS.class;
		// 设置调用参数，调用远程服务
		return new RemoteService().setParams(SERVICE_NAME, "getWBSTmpList",
				tenant_id, type_).call(manager, type, Operation.QUERY);
	}

	/**
	 * 查看文档模板
	 * 
	 * @param templet_wbs_id
	 * @return
	 */
	public AsyncTaskPM360 getWBSTmp(final DataManagerInterface manager,
			int templet_wbs_id) {
		// 初始化返回类型
		Type type = AnalysisManager.GSON ? new TypeToken<List<Templet_WBS_dir>>() {
		}.getType()
				: Templet_WBS_dir.class;
		// 设置调用参数，调用远程服务
		return new RemoteService().setParams(SERVICE_NAME, "getWBSTmp",
				templet_wbs_id).call(manager, type, Operation.QUERY);
	}

	/**
	 * 增加WBS模板信息
	 * 
	 * @param Templet_wbsJSON
	 * @return
	 */
	public AsyncTaskPM360 addWBSTmp(final DataManagerInterface manager,
			Templet_WBS Templet_WBS) {
		// 设置返回类型
		Type type = AnalysisManager.GSON ? new TypeToken<List<Templet_WBS>>() {
		}.getType() : Templet_WBS.class;
		// 设置调用参数，调用远程服务
		return new RemoteService().setParams(SERVICE_NAME, "addWBSTmp",
				Templet_WBS).call(manager, type, Operation.ADD);
	}

	/**
	 * 
	 * 导入WBS模板信息
	 * 
	 * @param Templet_wbsJSON
	 * @param file_name
	 *            上传文件的名字
	 * @return
	 */
	public AsyncTaskPM360 importWBSTemplet(final DataManagerInterface manager,
			Templet_WBS Templet_WBS, String file_name) {
		// 设置返回类型
		Type type = AnalysisManager.GSON ? new TypeToken<List<Templet_WBS>>() {
		}.getType() : Templet_WBS.class;
		// 设置调用参数，调用远程服务
		return new RemoteService().setParams(SERVICE_NAME, "importWBSTemplet",
				Templet_WBS, file_name).call(manager, type, Operation.ADD);
	}

	/**
	 * 修改文档模板信息
	 * 
	 * @param Templet_documentJSON
	 * @return
	 */
	public AsyncTaskPM360 updateWBSTmp(final DataManagerInterface manager,
			Templet_WBS Templet_WBS) {
		// 设置调用参数，调用远程服务
		return new RemoteService().setParams(SERVICE_NAME, "updateWBSTmp",
				Templet_WBS).call(manager, Operation.MODIFY);
	}

	/**
	 * 删除文档模板信息
	 * 
	 * @param templet_wbs_id
	 * @return
	 */
	public AsyncTaskPM360 deleteWBSTmp(final DataManagerInterface manager,
			int templet_wbs_id) {
		// 设置调用参数，调用远程服务
		return new RemoteService().setParams(SERVICE_NAME, "deleteWBSTmp",
				templet_wbs_id).call(manager, Operation.DELETE);
	}

	/**
	 * 增加目录
	 * 
	 * @param Templet_document_dirJSON
	 * @return
	 */
	public AsyncTaskPM360 adddir(final DataManagerInterface manager,
			Templet_WBS_dir Templet_WBS_dir) {
		// 设置返回类型
		Type type = AnalysisManager.GSON ? new TypeToken<List<Templet_WBS_dir>>() {
		}.getType()
				: Templet_WBS_dir.class;
		// 设置调用参数，调用远程服务
		return new RemoteService().setParams(SERVICE_NAME, "adddir",
				Templet_WBS_dir).call(manager, type, Operation.ADD);
	}

	/**
	 * 修改目录
	 * 
	 * @param Templet_document_dirJSON
	 * @return
	 */
	public AsyncTaskPM360 updateDir(final DataManagerInterface manager,
			Templet_WBS_dir Templet_WBS_dir) {
		// 设置调用参数，调用远程服务
		return new RemoteService().setParams(SERVICE_NAME, "updateDir",
				Templet_WBS_dir).call(manager, Operation.MODIFY);
	}

	/**
	 * 删除目录
	 * 
	 * @param templet_wbs_dir_id
	 *            (以逗号分隔)
	 * @return
	 */
	public AsyncTaskPM360 deleteDir(final DataManagerInterface manager,
			String templet_wbs_dir_id) {
		// 设置调用参数，调用远程服务
		return new RemoteService().setParams(SERVICE_NAME, "deleteDir",
				templet_wbs_dir_id).call(manager, Operation.DELETE);
	}

}
