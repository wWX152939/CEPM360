package com.pm360.cepm360.services.group;

import com.google.gson.reflect.TypeToken;
import com.pm360.cepm360.app.common.activity.DataManagerInterface;
import com.pm360.cepm360.common.json.AnalysisManager;
import com.pm360.cepm360.entity.Files;
import com.pm360.cepm360.entity.ZH_group_worklog;
import com.pm360.cepm360.services.common.AsyncTaskPM360;
import com.pm360.cepm360.services.common.Operation;
import com.pm360.cepm360.services.common.RemoteService;

import java.lang.reflect.Type;
import java.util.List;

/**
 * 组合-工作日志
 * 
 * @author Andy
 * 
 */
public class RemoteWorkLogService {
	// 框架服务类全限定名
	private static final String SERVICE_NAME = "com.pm360.cepm360.services.group.WorkLogService";
	// 单例类变量
	private static RemoteWorkLogService gService;

	/**
	 * 单例模式
	 * 
	 * @return
	 */
	public static synchronized RemoteWorkLogService getInstance() {
		if (gService == null) {
			gService = new RemoteWorkLogService();
		}
		return gService;
	}

	/**
	 * 销毁实例对象
	 */
	public static void destroyInstance() {
		gService = null;
	}

	private RemoteWorkLogService() {

	}

	/**
	 * 获取工作日志
	 * 
	 * @param task_id
	 * @return
	 */
	public AsyncTaskPM360 getWorkLogs(final DataManagerInterface manager,
			int task_id) {
		// 初始化返回类型
		Type type = AnalysisManager.GSON ? new TypeToken<List<ZH_group_worklog>>() {
		}.getType()
				: ZH_group_worklog.class;
		// 设置调用参数，调用远程服务
		return new RemoteService().setParams(SERVICE_NAME, "getWorkLogs",
				task_id).call(manager, type, Operation.QUERY);
	}

	/**
	 * 增加日志信息
	 * 
	 * @param ZH_group_worklogJSON
	 * @return
	 */
	public AsyncTaskPM360 addWorkLog(final DataManagerInterface manager,
			ZH_group_worklog ZH_group_worklog) {
		// 设置返回类型
		Type type = AnalysisManager.GSON ? new TypeToken<List<ZH_group_worklog>>() {
		}.getType()
				: ZH_group_worklog.class;
		// 设置调用参数，调用远程服务
		return new RemoteService().setParams(SERVICE_NAME, "addWorkLog",
				ZH_group_worklog).call(manager, type, Operation.ADD);
	}

	/**
	 * 删除日志
	 * 
	 * @param zh_group_worklog_id
	 * @return
	 */
	public AsyncTaskPM360 deleteWorkLog(final DataManagerInterface manager,
			int zh_group_worklog_id) {
		// 设置调用参数，调用远程服务
		return new RemoteService().setParams(SERVICE_NAME, "deleteWorkLog",
				zh_group_worklog_id).call(manager, Operation.DELETE);
	}

	/**
	 * 更新日志信息
	 * 
	 * @param ZH_group_worklogJSON
	 * @return
	 */
	public AsyncTaskPM360 updateWorkLog(final DataManagerInterface manager,
			ZH_group_worklog ZH_group_worklog) {
		// 设置调用参数，调用远程服务
		return new RemoteService().setParams(SERVICE_NAME, "updateWorkLog",
				ZH_group_worklog).call(manager, Operation.MODIFY);
	}

	/**
	 * 获取组合工作日志下的附件
	 * 
	 * @param zh_group_worklog_id
	 * @return
	 */
	public AsyncTaskPM360 getZHGZRZFiles(final DataManagerInterface manager,
			int zh_group_worklog_id) {
		// 初始化返回类型
		Type type = AnalysisManager.GSON ? new TypeToken<List<Files>>() {
		}.getType() : Files.class;

		// 设置调用参数，调用远程服务
		return new RemoteService().setParams(SERVICE_NAME, "getZHGZRZFiles",
				zh_group_worklog_id).call(manager, type, Operation.QUERY);
	}
}
