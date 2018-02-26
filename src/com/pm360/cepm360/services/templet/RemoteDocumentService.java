package com.pm360.cepm360.services.templet;

import com.google.gson.reflect.TypeToken;
import com.pm360.cepm360.app.common.activity.DataManagerInterface;
import com.pm360.cepm360.common.json.AnalysisManager;
import com.pm360.cepm360.entity.Files;
import com.pm360.cepm360.entity.Templet_document;
import com.pm360.cepm360.entity.Templet_document_dir;
import com.pm360.cepm360.services.common.AsyncTaskPM360;
import com.pm360.cepm360.services.common.Operation;
import com.pm360.cepm360.services.common.RemoteService;

import java.lang.reflect.Type;
import java.util.List;

/**
 * 文档模板
 * 
 * @author Andy
 * 
 */
public class RemoteDocumentService {

	// 框架服务类全限定名
	private static final String SERVICE_NAME = "com.pm360.cepm360.services.templet.DocumentService";
	// 单例类变量
	private static RemoteDocumentService gService;

	/**
	 * 单例模式
	 * 
	 * @return
	 */
	public static synchronized RemoteDocumentService getInstance() {
		if (gService == null) {
			gService = new RemoteDocumentService();
		}
		return gService;
	}

	/**
	 * 销毁实例对象
	 */
	public static void destroyInstance() {
		gService = null;
	}

	private RemoteDocumentService() {

	}

	// /**
	// * 获取文档模板列表
	// *
	// * @param tenant_id_
	// * @param type_
	// * @return
	// */
	// public AsyncTaskPM360 getDocTmpList(final DataManagerInterface manager,
	// int tenant_id, int type_) {
	// // 初始化返回类型
	// Type type = AnalysisManager.GSON ? new
	// TypeToken<List<Templet_document>>() {
	// }.getType()
	// : Templet_document.class;
	// // 设置调用参数，调用远程服务
	// return new RemoteService().setParams(SERVICE_NAME, "getDocTmpList",
	// tenant_id, type_).call(manager, type, Operation.QUERY);
	// }

	/**
	 * 获取文档模板列表
	 * 
	 * @param tenant_id
	 * @return
	 */
	public AsyncTaskPM360 getDocTmpList(final DataManagerInterface manager,
			int tenant_id) {
		// 初始化返回类型
		Type type = AnalysisManager.GSON ? new TypeToken<List<Files>>() {
		}.getType() : Files.class;
		// 设置调用参数，调用远程服务
		return new RemoteService().setParams(SERVICE_NAME, "getDocTmpList",
				tenant_id).call(manager, type, Operation.QUERY);
	}

	/**
	 * 查看文档模板
	 * 
	 * @param templet_document_id
	 * @return
	 */
	public AsyncTaskPM360 getDocTmp(final DataManagerInterface manager,
			int templet_document_id) {
		// 初始化返回类型
		Type type = AnalysisManager.GSON ? new TypeToken<List<Templet_document_dir>>() {
		}.getType()
				: Templet_document_dir.class;
		// 设置调用参数，调用远程服务
		return new RemoteService().setParams(SERVICE_NAME, "getDocTmp",
				templet_document_id).call(manager, type, Operation.QUERY);
	}

	/**
	 * 增加文档模板信息
	 * 
	 * @param Templet_documentJSON
	 * @return
	 */
	public AsyncTaskPM360 addDocTmp(final DataManagerInterface manager,
			Templet_document Templet_document) {
		// 设置返回类型
		Type type = AnalysisManager.GSON ? new TypeToken<List<Templet_document>>() {
		}.getType()
				: Templet_document.class;
		// 设置调用参数，调用远程服务
		return new RemoteService().setParams(SERVICE_NAME, "addDocTmp",
				Templet_document).call(manager, type, Operation.ADD);
	}

	/**
	 * 修改文档模板信息
	 * 
	 * @param Templet_documentJSON
	 * @return
	 */
	public AsyncTaskPM360 updateDocTmp(final DataManagerInterface manager,
			Templet_document Templet_document) {
		// 设置调用参数，调用远程服务
		return new RemoteService().setParams(SERVICE_NAME, "updateDocTmp",
				Templet_document).call(manager, Operation.MODIFY);
	}

	/**
	 * 删除文档模板信息
	 * 
	 * @param templet_document_id
	 * @return
	 */
	public AsyncTaskPM360 deleteDocTmp(final DataManagerInterface manager,
			int templet_document_id) {
		// 设置调用参数，调用远程服务
		return new RemoteService().setParams(SERVICE_NAME, "deleteDocTmp",
				templet_document_id).call(manager, Operation.DELETE);
	}

	/**
	 * 增加目录
	 * 
	 * @param Templet_document_dirJSON
	 * @return
	 */
	public AsyncTaskPM360 adddir(final DataManagerInterface manager,
			Templet_document_dir Templet_document_dir) {
		// 设置返回类型
		Type type = AnalysisManager.GSON ? new TypeToken<List<Templet_document_dir>>() {
		}.getType()
				: Templet_document_dir.class;
		// 设置调用参数，调用远程服务
		return new RemoteService().setParams(SERVICE_NAME, "adddir",
				Templet_document_dir).call(manager, type, Operation.ADD);
	}

	/**
	 * 修改目录
	 * 
	 * @param Templet_document_dirJSON
	 * @return
	 */
	public AsyncTaskPM360 updateDir(final DataManagerInterface manager,
			Templet_document_dir Templet_document_dir) {
		// 设置调用参数，调用远程服务
		return new RemoteService().setParams(SERVICE_NAME, "updateDir",
				Templet_document_dir).call(manager, Operation.MODIFY);
	}

	/**
	 * 删除目录
	 * 
	 * @param templet_document_dir_id
	 * @return
	 */
	public AsyncTaskPM360 deleteDir(final DataManagerInterface manager,
			int templet_document_dir_id) {
		// 设置调用参数，调用远程服务
		return new RemoteService().setParams(SERVICE_NAME, "deleteDir",
				templet_document_dir_id).call(manager, Operation.DELETE);
	}
}
