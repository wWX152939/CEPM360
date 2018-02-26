package com.pm360.cepm360.services;

import com.google.gson.reflect.TypeToken;
import com.pm360.cepm360.app.common.activity.DataManagerInterface;
import com.pm360.cepm360.common.json.AnalysisManager;
import com.pm360.cepm360.entity.User;
import com.pm360.cepm360.services.common.AsyncTaskPM360;
import com.pm360.cepm360.services.common.Operation;
import com.pm360.cepm360.services.common.RemoteService;

import java.lang.reflect.Type;
import java.util.List;

/**
 * 登录服务
 * 
 * @author yuanlu
 * 
 */
public class RemoteLoginService {
	// 框架服务类全限定名
	private static final String SERVICE_NAME = "com.pm360.cepm360.services.LoginService";
	// 单例类变量
	private static RemoteLoginService gService;

	/**
	 * 单例模式
	 * 
	 * @return
	 */
	public static synchronized RemoteLoginService getInstance() {
		if (gService == null) {
			gService = new RemoteLoginService();
		}
		return gService;
	}

	/**
	 * 销毁实例对象
	 */
	public static void destroyInstance() {
		gService = null;
	}

	private RemoteLoginService() {

	}

	/**
	 * 用户登录
	 * 
	 * @param login_name
	 *            登录名
	 * @param pwd
	 *            登录密码
	 * @param type
	 *            企业类型
	 * @return 验证用户名和密码是否正确
	 */
	public AsyncTaskPM360 requestLogin(final DataManagerInterface manager,
			String name, String passwd, int type) {
		// 获取返回类型
		Type treType = AnalysisManager.GSON ? new TypeToken<List<User>>() {
		}.getType() : User.class;
		// 设置参数，调用远程服务
		return new RemoteService().setParams(SERVICE_NAME, "login", name,
				passwd, type).call(manager, treType, Operation.LOGIN);
	}
	
	/**
	 * 用户登录
	 * 
	 * @param name
	 *            登录名
	 * @param passwd
	 *            登录密码
	 * @return 验证用户名和密码是否正确
	 */
	public void requestLogin(final DataManagerInterface manager,
			String name, String passwd) {
		// 获取返回类型
		Type treType = User.class;
		// 设置参数，调用远程服务
		new RemoteService().setParams(SERVICE_NAME, "login2", name,
				passwd).call(manager, treType, Operation.LOGIN);
	}
}
