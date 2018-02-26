package com.pm360.cepm360.services.system;

import com.google.gson.reflect.TypeToken;
import com.pm360.cepm360.app.common.activity.DataManagerInterface;
import com.pm360.cepm360.common.json.AnalysisManager;
import com.pm360.cepm360.entity.Project;
import com.pm360.cepm360.entity.User;
import com.pm360.cepm360.services.common.Operation;
import com.pm360.cepm360.services.common.RemoteService;

import java.lang.reflect.Type;
import java.util.List;

public class RemoteUserService {
	// 框架服务类全限定名
	private static final String SERVICE_NAME = "com.pm360.cepm360.services.system.UserService";
	// 单例类变量
	private static RemoteUserService gService;

	/**
	 * 单例模式
	 * 
	 * @return
	 */
	public static synchronized RemoteUserService getInstance() {
		if (gService == null) {
			gService = new RemoteUserService();
		}
		return gService;
	}

	/**
	 * 销毁实例对象
	 */
	public static void destroyInstance() {
		gService = null;
	}

	private RemoteUserService() {

	}

	// --------------------------- 接口 ------------------------------------ //

	/**
	 * 获取该公司的所有用户信息
	 * 
	 * @param tenant_id
	 * 
	 * @return
	 */
	public void getTenantUsers(final DataManagerInterface manager, User user) {
		// 初始化返回类型
		Type type = AnalysisManager.GSON ? new TypeToken<List<User>>() {
		}.getType() : User.class;
		// 设置调用参数，调用远程服务
		new RemoteService().setParams(SERVICE_NAME, "getTenantUsers",
				user.getTenant_id()).call(manager, type, Operation.QUERY);
	}

	/**
	 * 根据user_ds查询User信息
	 * 
	 * @param user_ids
	 *            （user_id以逗号形式组合）
	 * 
	 * @return
	 */
	public void getUserDetailByUserIDs(final DataManagerInterface manager,
			String user_ids) {
		// 初始化返回类型
		Type type = AnalysisManager.GSON ? new TypeToken<List<User>>() {
		}.getType() : User.class;
		// 设置调用参数，调用远程服务
		new RemoteService().setParams(SERVICE_NAME, "getUserDetailByUserIDs",
				user_ids).call(manager, type, Operation.QUERY);
	}

	/**
	 * 获取某个项目的所有人员
	 * 
	 * @param manager
	 */
	public void getProjectUsers(final DataManagerInterface manager,
			Project project) {
		// 初始化返回类型
		Type type = AnalysisManager.GSON ? new TypeToken<List<User>>() {
		}.getType() : User.class;
		// 设置调用参数，调用远程服务
		new RemoteService().setParams(SERVICE_NAME, "getProjectUsers", project)
				.call(manager, type, Operation.QUERY);
	}

	/**
	 * 获取该贵公司的的协作单位人员列表信息
	 * 
	 * @param cooperation_id
	 * @param tenant_id
	 *            (当前公司，并非协作单位)
	 * @return
	 */
	public void getCooperationUsers(final DataManagerInterface manager,
			int cooperation_id, int tenant_id) {
		// 初始化返回类型
		Type type = AnalysisManager.GSON ? new TypeToken<List<User>>() {
		}.getType() : User.class;
		// 设置调用参数，调用远程服务
		new RemoteService().setParams(SERVICE_NAME, "getCooperationUsers",
				cooperation_id, tenant_id).call(manager, type, Operation.QUERY);
	}

	/**
	 * 获取未分配的用户信息
	 * 
	 * @param manager
	 * @param user
	 *            (1,2,3)
	 * @param tenant_id
	 */
	public void getUndistributedUserList(final DataManagerInterface manager,
			String user, int tenant_id) {
		// 初始化返回类型
		Type type = AnalysisManager.GSON ? new TypeToken<List<User>>() {
		}.getType() : User.class;
		// 设置调用参数，调用远程服务
		new RemoteService().setParams(SERVICE_NAME, "getUndistributedUserList",
				user, tenant_id).call(manager, type, Operation.QUERY);
	}

	/**
	 * 添加一个User
	 * 
	 * @param manager
	 * @param user
	 */
	public void addUser(final DataManagerInterface manager, User user) {
		// 初始化返回类型
		Type type = AnalysisManager.GSON ? new TypeToken<List<User>>() {
		}.getType() : User.class;
		// 设置调用参数，调用远程服务
		new RemoteService().setParams(SERVICE_NAME, "addUser", user).call(
				manager, type, Operation.ADD);
	}

	/**
	 * 删除一个User
	 * 
	 * @param manager
	 * @param user
	 */
	public void deleteUser(final DataManagerInterface manager, User user) {
		// 设置调用参数，调用远程服务
		new RemoteService().setParams(SERVICE_NAME, "deleteUser",
				user.getUser_id()).call(manager, Operation.DELETE);
	}

	/**
	 * 修改一个User
	 * 
	 * @param manager
	 * @param user
	 */
	public void updateUser(final DataManagerInterface manager, User user) {
		// 设置调用参数，调用远程服务
		new RemoteService().setParams(SERVICE_NAME, "updateUser", user).call(
				manager, Operation.MODIFY);
	}

}
