package com.pm360.cepm360.services.document;

import com.google.gson.reflect.TypeToken;
import com.pm360.cepm360.app.common.activity.DataManagerInterface;
import com.pm360.cepm360.common.ResultStatus;
import com.pm360.cepm360.common.json.AnalysisManager;
import com.pm360.cepm360.entity.Document;
import com.pm360.cepm360.entity.Files;
import com.pm360.cepm360.entity.SearchFiles;
import com.pm360.cepm360.entity.User;
import com.pm360.cepm360.services.common.AsyncTaskPM360;
import com.pm360.cepm360.services.common.Operation;
import com.pm360.cepm360.services.common.RemoteService;

import java.io.File;
import java.lang.reflect.Type;
import java.util.List;

public class RemoteDocumentsService {
	// 任务服务的类全限定名
	private static final String SERVICE_NAME = "com.pm360.cepm360.services.documents.DocumentsService";
	// 单例对象类变量
	private static RemoteDocumentsService gService;

	/**
	 * 单例模式
	 * 
	 * @return
	 */
	public static synchronized RemoteDocumentsService getInstance() {
		if (gService == null) {
			gService = new RemoteDocumentsService();
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
	private RemoteDocumentsService() {

	}

	/*
	 * --------------------------------- 接口
	 * ---------------------------------------
	 */

	/**
	 * 获取任务报告的目录列表
	 * 
	 * @param manager
	 */
	public AsyncTaskPM360 getDirectoryList(final DataManagerInterface manager,
			Document directory) {
		// 初始化返回类型
		Type type = AnalysisManager.GSON ? new TypeToken<List<Document>>() {
		}.getType() : Document.class;
		// 设置调用参数，调用远程服务
		return new RemoteService().setParams(SERVICE_NAME, "getDirectory",
				directory).call(manager, type, Operation.QUERY);
	}

	/**
	 * 获取任务报告的文件列表
	 * 
	 * @param manager
	 */
	public AsyncTaskPM360 getFileList(final DataManagerInterface manager,
			Document directory) {
		// 初始化返回类型
		Type type = AnalysisManager.GSON ? new TypeToken<List<Files>>() {
		}.getType() : Files.class;
		// 设置调用参数，调用远程服务
		return new RemoteService().setParams(SERVICE_NAME, "getFiles",
				directory).call(manager, type, Operation.QUERY);
	}

	/**
	 * 修改文件属性
	 * 
	 * @param manager
	 * @param document
	 */
	public AsyncTaskPM360 updateFile(final DataManagerInterface manager,
			Files document) {
		// 设置调用参数，调用远程服务
		return new RemoteService().setParams(SERVICE_NAME, "updateFile",
				document).call(manager, Operation.MODIFY);
	}

	/**
	 * 删除某个文件
	 * 
	 * @param fileJSON
	 *            (tenan_id,file_name必须传值)
	 * @param currentlyDirID
	 *            (当前文件所在的目录ID)
	 * @return
	 */
	public AsyncTaskPM360 deleteFile(final DataManagerInterface manager,
			Files document, int currentlyDirID) {
		// 设置调用参数，调用远程服务
		return new RemoteService().setParams(SERVICE_NAME, "deleteFiles",
				document, currentlyDirID).call(manager, Operation.DELETE);
	}

	// /**
	// * 删除磁盘文件
	// *
	// * @param tenan_id 公司文件夹根目录
	// * @param file_name
	// * @return
	// */
	// public AsyncTaskPM360 deleteDiskFiles(final DataManagerInterface manager,
	// int tenan_id,String file_name) {
	// // 设置调用参数，调用远程服务
	// return new RemoteService().setParams(SERVICE_NAME, "deleteDiskFiles",
	// tenan_id,file_name).call(manager, Operation.DELETE);
	// }

	/**
	 * 新增文件表单
	 * 
	 * @param filesJSON
	 * @param listUserJSON
	 * @param user_id
	 *            (如果是高级权限user_id传递0，非高级权限，则需要传递当前user_id)
	 * @return
	 */
	public AsyncTaskPM360 addFile(final DataManagerInterface manager,
			Files document, List<User> users, int user_id) {
		// 初始化返回类型
		Type type = AnalysisManager.GSON ? new TypeToken<List<Files>>() {
		}.getType() : Files.class;
		// 设置调用参数，调用远程服务
		return new RemoteService().setParams(SERVICE_NAME, "addFile", document,
				users, user_id).call(manager, type, Operation.ADD);
	}

	/**
	 * 更新文件
	 * 
	 * @param filesJSON
	 * @param listUserJSON
	 * @return
	 */
	public AsyncTaskPM360 updateFile(final DataManagerInterface manager,
			Files document, List<User> users) {
		// 初始化返回类型
		Type type = AnalysisManager.GSON ? new TypeToken<List<Files>>() {
		}.getType() : Files.class;
		// 设置调用参数，调用远程服务
		return new RemoteService().setParams(SERVICE_NAME, "updateFile",
				document, users).call(manager, type, Operation.MODIFY);
	}

	/**
	 * 下载文件
	 * 
	 * @param manager
	 * @param document
	 */
	public AsyncTaskPM360 downloadFile(final DataManagerInterface manager,
			Files document, File file) {
		// 设置调用参数，调用远程服务
		return new RemoteService().setParams(null, null, document).call(
				manager, Operation.DOWNLOAD, file);
	}

	/**
	 * 带进度条的下载文件
	 * 
	 * @param manager
	 * @param document
	 */
	public AsyncTaskPM360 downloadFile(final DataManagerInterface manager,
			Object progress, Files document, File file) {
		// 设置调用参数，调用远程服务
		return new RemoteService().setParams(null, null, document).call(
				manager, Operation.DOWNLOAD, file, progress);
	}

	/**
	 * 上传文件
	 * 
	 * @param manager
	 * @param document
	 * @param file
	 * @param users
	 */
	public AsyncTaskPM360 uploadFile(final DataManagerInterface manager,
			Object progress, int tenant_id, File file) {
		// 设置调用参数，调用远程服务
		return new RemoteService().setParams(null, null, tenant_id).call(
				manager, Operation.UPLOAD, file, progress);
	}

	/**
	 * 上传文件，文件和表单数据一起传输
	 * 
	 * @param manager
	 * @param progress
	 * @param tenant_id
	 * @param document
	 * @param file
	 * @param users
	 * @return
	 */
	public AsyncTaskPM360 uploadFile(final DataManagerInterface manager,
			Object progress, final Files document, int tenant_id, File file,
			final List<User> users, final int user_id) {
		return uploadFile(new DataManagerInterface() {

			@Override
			public void getDataOnResult(ResultStatus status, List<?> list) {
				if (status.getCode() == AnalysisManager.SUCCESS_UPLOAD) {
					Files result = (Files) list.get(0);

					// 将UI传递的Files对象和服务器返回的Files对象组合起来
					document.setFile_name(result.getFile_name());
					document.setFile_path(result.getFile_path());
					document.setFile_size(result.getFile_size());
					document.setPath(new File(document.getPath()).getParent()
							+ "/" + result.getFile_name());

					addFile(manager, document, users, user_id);
				} else {
					ResultStatus resultStatus = new ResultStatus(
							AnalysisManager.EXCEPTION_DB_ADD,
							status.getMessage());
					manager.getDataOnResult(resultStatus, list);
				}
			}
		}, progress, tenant_id, file);
	}

	/**
	 * 文件发布
	 * 
	 * @param manager
	 * @param document
	 */
	public AsyncTaskPM360 publish(final DataManagerInterface manager,
			Files document) {
		// 设置调用参数，调用远程服务
		return new RemoteService().setParams(SERVICE_NAME, "publish",
				document.getFile_id()).call(manager, Operation.PUBLISH);
	}

	/**
	 * 更新目录
	 * 
	 * @param directoryJSON
	 * 
	 * @return
	 */
	public AsyncTaskPM360 updateDirectory(final DataManagerInterface manager,
			Document directory) {
		// 设置调用参数，调用远程服务
		return new RemoteService().setParams(SERVICE_NAME, "updateDirectory",
				directory).call(manager, Operation.MODIFY);
	}

	/**
	 * 关联文档路径
	 * 
	 * @param directoryJSON
	 * 
	 * @return
	 */
	public AsyncTaskPM360 relatedDocumentPath(
			final DataManagerInterface manager, Document directory) {
		// 设置调用参数，调用远程服务
		return new RemoteService().setParams(SERVICE_NAME,
				"relatedDocumentPath", directory).call(manager,
				Operation.MODIFY);
	}

	/**
	 * 删除文件
	 * 
	 * @param manager
	 * @param document
	 */
	public AsyncTaskPM360 deleteDirectory(final DataManagerInterface manager,
			Document directory) {
		// 设置调用参数，调用远程服务
		return new RemoteService().setParams(SERVICE_NAME, "deleteDirectory",
				directory.getDocument_id()).call(manager, Operation.DELETE);
	}

	/**
	 * 添加文件表单
	 * 
	 * @param manager
	 * @param document
	 */
	public AsyncTaskPM360 addDirectory(final DataManagerInterface manager,
			Document directory) {
		// 初始化返回类型
		Type type = AnalysisManager.GSON ? new TypeToken<List<Document>>() {
		}.getType() : Document.class;
		// 设置调用参数，调用远程服务
		return new RemoteService().setParams(SERVICE_NAME, "addDirectory",
				directory).call(manager, type, Operation.ADD);
	}

	/**
	 * 获取符合指定查询条件的文件列表
	 * 
	 * @param manager
	 * @param searchFile
	 */
	public AsyncTaskPM360 getSearchedFiles(final DataManagerInterface manager,
			SearchFiles searchFile) {
		// 初始化返回类型
		Type type = AnalysisManager.GSON ? new TypeToken<List<Files>>() {
		}.getType() : Files.class;
		// 设置调用参数，调用远程服务
		return new RemoteService().setParams(SERVICE_NAME, "searchFiles",
				searchFile).call(manager, type, Operation.QUERY);
	}

	/**
	 * 获取未归档或者临时归档的文件
	 * 
	 * @param tenant_id
	 * @param user_id
	 *            (如果是高级权限user_id传递0，非高级权限，则需要传递当前user_id)
	 * @return
	 */
	public AsyncTaskPM360 getUnArchiveFiles(final DataManagerInterface manager,
			int tenant_id, int user_id) {
		// 初始化返回类型
		Type type = AnalysisManager.GSON ? new TypeToken<List<Files>>() {
		}.getType() : Files.class;
		// 设置调用参数，调用远程服务
		return new RemoteService().setParams(SERVICE_NAME, "getUnArchiveFiles",
				tenant_id, user_id).call(manager, type, Operation.QUERY);
	}

	/**
	 * 归档
	 * 
	 * @param filesJSON
	 * @param listUserJSON
	 * 
	 * @return
	 */
	public AsyncTaskPM360 archiveFile(final DataManagerInterface manager,
			Files document, List<User> users) {
		// 设置调用参数，调用远程服务
		return new RemoteService().setParams(SERVICE_NAME, "archiveFile",
				document, users).call(manager, Operation.MODIFY);
	}

	/**
	 * 置顶(针对现场图片和形象成果 FILE_TYPE：3或者15)
	 * 
	 * @param filesJSON
	 * @param listUserJSON
	 * @return
	 */
	public AsyncTaskPM360 topFile(final DataManagerInterface manager,
			Files document, List<User> users) {
		// 设置调用参数，调用远程服务
		return new RemoteService().setParams(SERVICE_NAME, "topFile", document,
				users).call(manager, Operation.MODIFY);
	}

	/**
	 * 查看文件
	 * 
	 * @param file_id
	 * @param tenant_id
	 * @return
	 */
	public AsyncTaskPM360 getFilesByID(final DataManagerInterface manager,
			int file_id, int tenant_id) {
		// 初始化返回类型
		Type type = AnalysisManager.GSON ? new TypeToken<List<Files>>() {
		}.getType() : Files.class;
		// 设置调用参数，调用远程服务
		return new RemoteService().setParams(SERVICE_NAME, "getFilesByID",
				file_id, tenant_id).call(manager, type, Operation.QUERY);
	}
}
