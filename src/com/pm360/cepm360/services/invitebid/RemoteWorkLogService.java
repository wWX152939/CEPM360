package com.pm360.cepm360.services.invitebid;

import com.google.gson.reflect.TypeToken;
import com.pm360.cepm360.app.common.activity.DataManagerInterface;
import com.pm360.cepm360.common.json.AnalysisManager;
import com.pm360.cepm360.entity.Files;
import com.pm360.cepm360.entity.ZB_worklog;
import com.pm360.cepm360.services.common.AsyncTaskPM360;
import com.pm360.cepm360.services.common.Operation;
import com.pm360.cepm360.services.common.RemoteService;

import java.lang.reflect.Type;
import java.util.List;

/**
 * 招标-工作日志
 * 
 * @author Andy
 * 
 */
public class RemoteWorkLogService {
	// 框架服务类全限定名
	private static final String SERVICE_NAME = "com.pm360.cepm360.services.invitebid.WorkLogService";
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
     * @param zb_plan_id
     * @param zb_flow_id
     * @return
     */
	public AsyncTaskPM360 getWorkLogs(final DataManagerInterface manager,
			int zb_plan_id,int zb_flow_id) {
		// 初始化返回类型
		Type type = AnalysisManager.GSON ? new TypeToken<List<ZB_worklog>>() {
		}.getType() : ZB_worklog.class;
		// 设置调用参数，调用远程服务
		return new RemoteService().setParams(SERVICE_NAME, "getWorkLogs",
				zb_plan_id,zb_flow_id).call(manager, type, Operation.QUERY);
	}

	/**
	 * 增加日志信息
	 * 
	 * @param ZB_worklogJSOn
	 * @return
	 */
	public AsyncTaskPM360 addWorkLog(final DataManagerInterface manager,
			ZB_worklog ZB_worklog) {
		// 设置返回类型
		Type type = AnalysisManager.GSON ? new TypeToken<List<ZB_worklog>>() {
		}.getType() : ZB_worklog.class;
		// 设置调用参数，调用远程服务
		return new RemoteService().setParams(SERVICE_NAME, "addWorkLog",
				ZB_worklog).call(manager, type, Operation.ADD);
	}

	/**
	 * 删除日志
	 * 
	 * @param zb_worklog_id
	 * @return
	 */
	public AsyncTaskPM360 deleteWorkLog(final DataManagerInterface manager,
			int zb_worklog_id) {
		// 设置调用参数，调用远程服务
		return new RemoteService().setParams(SERVICE_NAME, "deleteWorkLog",
				zb_worklog_id).call(manager, Operation.DELETE);
	}

	/**
	 * 更新日志信息
	 * 
	 * @param ZB_worklog
	 * @return
	 */
	public AsyncTaskPM360 updateWorkLog(final DataManagerInterface manager,
			ZB_worklog ZB_worklog) {
		// 设置调用参数，调用远程服务
		return new RemoteService().setParams(SERVICE_NAME, "updateWorkLog",
				ZB_worklog).call(manager, Operation.MODIFY);
	}

	/**
	 * 获取工作日志下的附件
	 * 
	 * @param zh_group_worklog_id
	 * @return
	 */
	public AsyncTaskPM360 getZBGZRZFiles(final DataManagerInterface manager,
			int zb_worklog_id) {
		// 初始化返回类型
		Type type = AnalysisManager.GSON ? new TypeToken<List<Files>>() {
		}.getType() : Files.class;

		// 设置调用参数，调用远程服务
		return new RemoteService().setParams(SERVICE_NAME, "getZBGZRZFiles",
				zb_worklog_id).call(manager, type, Operation.QUERY);
	}
}
