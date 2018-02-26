package com.pm360.cepm360.services.group;

import java.lang.reflect.Type;
import java.util.List;

import com.google.gson.reflect.TypeToken;
import com.pm360.cepm360.app.common.activity.DataManagerInterface;
import com.pm360.cepm360.common.json.AnalysisManager;
import com.pm360.cepm360.entity.ShareTask;
import com.pm360.cepm360.services.common.AsyncTaskPM360;
import com.pm360.cepm360.services.common.Operation;
import com.pm360.cepm360.services.common.RemoteService;

/**
 * 远程任务共享服务
 * 
 * @author Andy
 * 
 */
public class RemoteShareTaskService {
	// 框架服务类全限定名
	private static final String SERVICE_NAME = "com.pm360.cepm360.services.group.ShareTaskService";
	// 单例类变量
	private static RemoteShareTaskService gService;

	/**
	 * 单例模式
	 * 
	 * @return
	 */
	public static synchronized RemoteShareTaskService getInstance() {
		if (gService == null) {
			gService = new RemoteShareTaskService();
		}
		return gService;
	}

	/**
	 * 销毁实例对象
	 */
	public static void destroyInstance() {
		gService = null;
	}

	private RemoteShareTaskService() {

	}

	/**
	 * 获取共享任务列表
	 * 
	 * @param zh_group_id
	 *            (共享出去的给zh_group_id赋值，共享进来的给in_zh_group_id赋值)
	 * @param tenant_id
	 * @param flag
	 *            共享出去：0 共享进来：1
	 * @return
	 */
	public AsyncTaskPM360 getShareTaskList(final DataManagerInterface manager,
			int zh_group_id, int tenant_id, int flag) {
		// 初始化返回类型
		Type type = AnalysisManager.GSON ? new TypeToken<List<ShareTask>>() {
		}.getType() : ShareTask.class;
		// 设置调用参数，调用远程服务
		return new RemoteService().setParams(SERVICE_NAME, "getShareTaskList",
				zh_group_id, tenant_id, flag).call(manager, type,
				Operation.QUERY);
	}

	/**
	 * 增加共享任务（只保存）
	 * 
	 * @param ShareTaskListJSON
	 * @return
	 */
	public AsyncTaskPM360 addShareTask(final DataManagerInterface manager,
			List<ShareTask> ShareTaskList) {
		// 设置调用参数，调用远程服务
		return new RemoteService().setParams(SERVICE_NAME, "addShareTask",
				ShareTaskList).call(manager, Operation.ADD);
	}

	/**
	 * 删除未共享的任务
	 * 
	 * @param ids
	 *            以逗号分开id，例如1,2,3
	 * @return
	 */
	public AsyncTaskPM360 deleteUNShareTask(final DataManagerInterface manager,
			String ids) {
		// 设置调用参数，调用远程服务
		return new RemoteService().setParams(SERVICE_NAME, "deleteUNShareTask",
				ids).call(manager, Operation.DELETE);
	}

	/**
	 * 删除已共享的任务
	 * 
	 * @param ids
	 *            以逗号分开id，例如1,2,3
	 * @return
	 */
	public AsyncTaskPM360 deleteShareTask(final DataManagerInterface manager,
			String ids) {
		// 设置调用参数，调用远程服务
		return new RemoteService().setParams(SERVICE_NAME, "deleteShareTask",
				ids).call(manager, Operation.MODIFY);
	}

	/**
	 * 共享任务+发消息
	 * 
	 * @param ShareTaskJSON
	 *            (zh_group_id,accept_person,out_project_name,in_company,in_project_id 必填)
	 * @return
	 */
	public AsyncTaskPM360 shareTask(final DataManagerInterface manager,
			ShareTask ShareTask) { 
		// 设置调用参数，调用远程服务
		return new RemoteService().setParams(SERVICE_NAME, "shareTask",
				ShareTask).call(manager, Operation.MODIFY);
	}
	
	/**
	 * 挂载共享任务
	 * 
	 * @param mount_task_id
	 * @param zh_group_id
	 * @param in_project_id
	 * @param in_zh_group_id
	 * @return
	 */
	public AsyncTaskPM360 mountShareTask(final DataManagerInterface manager,
			int mount_task_id, int zh_group_id, int in_project_id,
			int in_zh_group_id) {
		// 设置调用参数，调用远程服务
		return new RemoteService().setParams(SERVICE_NAME, "mountShareTask",
				mount_task_id, zh_group_id, in_project_id, in_zh_group_id)
				.call(manager, Operation.MODIFY);
	}

	/**
	 * 挂载共享任务
	 * 
	 * @param mount_task_id
	 * @param zh_group_id
	 * @param in_project_id
	 * @param in_zh_group_id
	 * @return
	 */
	public AsyncTaskPM360 mountShareTask(final DataManagerInterface manager,
			int mount_task_id, int zh_group_id, int in_project_id,
			int in_zh_group_id, int message_id) {
		// 设置调用参数，调用远程服务
		return new RemoteService().setParams(SERVICE_NAME, "mountShareTask",
				mount_task_id, zh_group_id, in_project_id, in_zh_group_id, message_id)
				.call(manager, Operation.MODIFY);
	}
}
