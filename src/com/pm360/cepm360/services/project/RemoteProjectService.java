package com.pm360.cepm360.services.project;

import com.google.gson.reflect.TypeToken;
import com.pm360.cepm360.app.common.activity.DataManagerInterface;
import com.pm360.cepm360.common.json.AnalysisManager;
import com.pm360.cepm360.entity.Project;
import com.pm360.cepm360.entity.Prole;
import com.pm360.cepm360.entity.Templet_document;
import com.pm360.cepm360.entity.User;
import com.pm360.cepm360.services.common.AsyncTaskPM360;
import com.pm360.cepm360.services.common.Operation;
import com.pm360.cepm360.services.common.RemoteService;

import java.lang.reflect.Type;
import java.util.List;

public class RemoteProjectService {
	// 框架服务类全限定名
	private static final String SERVICE_NAME = "com.pm360.cepm360.services.project.ProjectService";
	// 单例类变量
	private static RemoteProjectService gService;

	/**
	 * 单例模式
	 * 
	 * @return
	 */
	public static synchronized RemoteProjectService getInstance() {
		if (gService == null) {
			gService = new RemoteProjectService();
		}
		return gService;
	}

	/**
	 * 销毁实例对象
	 */
	public static void destroyInstance() {
		gService = null;
	}

	private RemoteProjectService() {

	}

	/**
	 * 获取项目列表
	 * 
	 * @param manager
	 */
	public AsyncTaskPM360 getProjectList(final DataManagerInterface manager,
			User user) {
		// 初始化返回类型
		Type type = AnalysisManager.GSON ? new TypeToken<List<Project>>() {
		}.getType() : Project.class;
		// 设置调用参数，调用远程服务
		return new RemoteService().setParams(SERVICE_NAME, "getProjectList",
				user).call(manager, type, Operation.QUERY);
	}

	/**
	 * 获取该项目的库房
	 * 
	 * @param project_id
	 * @return 库房是中文反回，以逗号分隔，表示多个库房。例如（库房1,库房2），对象是project-storehouse
	 */
	public AsyncTaskPM360 getStorehouse(final DataManagerInterface manager,
			int project_id) {
		// 初始化返回类型
		Type type = AnalysisManager.GSON ? new TypeToken<List<Project>>() {
		}.getType() : Project.class;
		// 设置调用参数，调用远程服务
		return new RemoteService().setParams(SERVICE_NAME, "getStorehouse",
				project_id).call(manager, type, Operation.QUERY);
	}

	/**
	 * 获取指定ID的项目信息
	 * 
	 * @param manager
	 * @param project_id
	 */
	public AsyncTaskPM360 getProject(final DataManagerInterface manager,
			Project project) {
		// 初始化返回类型
		Type type = AnalysisManager.GSON ? new TypeToken<List<Project>>() {
		}.getType() : Project.class;
		// 设置调用参数，调用远程服务
		return new RemoteService().setParams(SERVICE_NAME, "getProject",
				project).call(manager, type, Operation.QUERY);
	}

	/**
	 * 添加一个项目
	 * 
	 * @param manager
	 * @param project
	 */
	public AsyncTaskPM360 addProject(final DataManagerInterface manager,
			Project project) {
		// 设置返回类型
		Type type = AnalysisManager.GSON ? new TypeToken<List<Project>>() {
		}.getType() : Project.class;
		// 设置调用参数，调用远程服务
		return new RemoteService().setParams(SERVICE_NAME, "addProject",
				project).call(manager, type, Operation.ADD);
	}

	/**
	 * 删除一个项目
	 * 
	 * @param manager
	 * @param project
	 */
	public AsyncTaskPM360 deleteProject(final DataManagerInterface manager,
			Project project) {
		// 设置调用参数，调用远程服务
		return new RemoteService().setParams(SERVICE_NAME, "deleteProject",
				project).call(manager, Operation.DELETE);
	}

	/**
	 * 更新一个项目
	 * 
	 * @param manager
	 * @param project
	 */
	public AsyncTaskPM360 updateProject(final DataManagerInterface manager,
			Project project) {
		// 设置调用参数，调用远程服务
		return new RemoteService().setParams(SERVICE_NAME, "updateProject",
				project).call(manager, Operation.MODIFY);
	}

//	/**
//	 * 获取参建单位列表
//	 * 
//	 * @param manager
//	 * @param project
//	 */
//	public AsyncTaskPM360 getConstruction_unit(
//			final DataManagerInterface manager, Project project) {
//		// 初始化返回类型
//		Type type = AnalysisManager.GSON ? new TypeToken<List<P_LWDW>>() {
//		}.getType() : P_LWDW.class;
//		// 设置调用参数，调用远程服务
//		return new RemoteService().setParams(SERVICE_NAME,
//				"getConstruction_unit", project).call(manager, type,
//				Operation.QUERY);
//	}


	/**
	 * 获取文档模板
	 * 
	 * @param manager
	 * @param directory
	 */
	public AsyncTaskPM360 getDocumentTemplet(
			final DataManagerInterface manager, User user) {
		// 初始化返回类型
		Type type = AnalysisManager.GSON ? new TypeToken<List<Templet_document>>() {
		}.getType()
				: Templet_document.class;
		// 设置调用参数，调用远程服务
		return new RemoteService().setParams(SERVICE_NAME,
				"getDocumentTemplet", user.getTenant_id()).call(manager, type,
				Operation.QUERY);
	}

	/**
	 * 获取该项目的项目人员用户以及用户信息
	 * 
	 * @param manager
	 * @param project
	 *            （通过tab flag来判断获取哪个信息 2:项目人员 3：用户）
	 */
	public AsyncTaskPM360 getProjectUser(final DataManagerInterface manager,
			Project project) {
		// 初始化返回类型
		Type type = AnalysisManager.GSON ? new TypeToken<List<User>>() {
		}.getType() : User.class;
		// 设置调用参数，调用远程服务
		return new RemoteService().setParams(SERVICE_NAME, "getProjectUser",
				project).call(manager, type, Operation.QUERY);
	}

	/**
	 * 为该项目分配用户和项目人员
	 * 
	 * @param manager
	 * @param List
	 *            <Prole>
	 */
	public AsyncTaskPM360 distributeProjectUser(
			final DataManagerInterface manager, List<Prole> proleList) {
		// 设置调用参数，调用远程服务
		return new RemoteService().setParams(SERVICE_NAME,
				"distributeProjectUser", proleList).call(manager,
				Operation.MODIFY);
	}
}
