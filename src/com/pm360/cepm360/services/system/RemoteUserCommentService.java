package com.pm360.cepm360.services.system;

import com.pm360.cepm360.app.common.activity.DataManagerInterface;
import com.pm360.cepm360.entity.User_comment;
import com.pm360.cepm360.services.common.AsyncTaskPM360;
import com.pm360.cepm360.services.common.Operation;
import com.pm360.cepm360.services.common.RemoteService;

/**
 * 远程用户意见收集
 * 
 * @author Andy
 * 
 */
public class RemoteUserCommentService {
	// 框架服务类全限定名
	private static final String SERVICE_NAME = "com.pm360.cepm360.services.system.UserCommentService";
	// 单例类变量
	private static RemoteUserCommentService gService;

	/**
	 * 单例模式
	 * 
	 * @return
	 */
	public static synchronized RemoteUserCommentService getInstance() {
		if (gService == null) {
			gService = new RemoteUserCommentService();
		}
		return gService;
	}

	/**
	 * 销毁实例对象
	 */
	public static void destroyInstance() {
		gService = null;
	}

	private RemoteUserCommentService() {

	}

	// --------------------------- 接口 ------------------------------------ //

	// /**
	// * 获取角色组织结构列表
	// *
	// * @param manager
	// */
	// public AsyncTaskPM360 getRoleList(final DataManagerInterface manager,
	// Role role) {
	// // 初始化返回类型
	// Type type = AnalysisManager.GSON ? new TypeToken<List<Role>>() {
	// }.getType() : Role.class;
	// // 设置调用参数，调用远程服务
	// return new RemoteService().setParams(SERVICE_NAME, "getRole", role)
	// .call(manager, type, Operation.QUERY);
	// }

	/**
	 * 用户意见反馈
	 * 
	 * @param User_commentJSON
	 * @return
	 */
	public AsyncTaskPM360 addUserComments(final DataManagerInterface manager,
			User_comment User_comment) {
		
		// 设置调用参数，调用远程服务
		return new RemoteService().setParams(SERVICE_NAME, "addUserComments",
				User_comment).call(manager, Operation.ADD);
	}

}
