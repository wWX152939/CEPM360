package com.pm360.cepm360.services.group;

import com.google.gson.reflect.TypeToken;
import com.pm360.cepm360.app.common.activity.DataManagerInterface;
import com.pm360.cepm360.common.json.AnalysisManager;
import com.pm360.cepm360.entity.Files;
import com.pm360.cepm360.entity.User;
import com.pm360.cepm360.entity.ZH_group;
import com.pm360.cepm360.entity.ZH_group_feedback;
import com.pm360.cepm360.entity.ZH_group_task;
import com.pm360.cepm360.services.common.AsyncTaskPM360;
import com.pm360.cepm360.services.common.Operation;
import com.pm360.cepm360.services.common.RemoteService;

import java.lang.reflect.Type;
import java.util.List;

public class RemoteTaskService {
	// 任务服务的类全限定名
	private static final String SERVICE_NAME = "com.pm360.cepm360.services.group.TaskService";
	// 单例对象类变量
	private static RemoteTaskService gService;
	// 当前任务
	private ZH_group_task mGroup_task;
	// 反馈
	private ZH_group_feedback mZH_group_feedback;

	/**
	 * 单例模式
	 * 
	 * @return
	 */
	public static synchronized RemoteTaskService getInstance() {
		if (gService == null) {
			gService = new RemoteTaskService();
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
	private RemoteTaskService() {

	}

	/**
	 * 设置任务
	 * 
	 * @param task
	 */
	public RemoteTaskService setTask(ZH_group_task task) {
		mGroup_task = task;
		return this;
	}

	/**
	 * 获取ZH_group_feedback
	 * 
	 * @param mZH_group_feedback
	 */
	public ZH_group_feedback getZH_group_feedback() {
		return mZH_group_feedback;
	}

	/**
	 * 设置反馈
	 * 
	 * @param mZH_group_feedback
	 */
	public RemoteTaskService setZH_group_feedback(
			ZH_group_feedback ZH_group_feedback) {
		mZH_group_feedback = ZH_group_feedback;
		return this;
	}

	/**
	 * 获取task
	 * 
	 * @param task
	 */
	public ZH_group_task getZH_group_task() {
		return mGroup_task;
	}

	/**
	 * 获取任务列表
	 * 
	 * @param manager
	 * @return
	 */
	public AsyncTaskPM360 getTaskList(final DataManagerInterface manager,
			int zh_group_id) {
		// 初始化返回类型
		Type type = AnalysisManager.GSON ? new TypeToken<List<ZH_group_task>>() {
		}.getType()
				: ZH_group_task.class;
		// 设置调用参数，调用远程服务
		return new RemoteService().setParams(SERVICE_NAME, "getTaskList",
				zh_group_id).call(manager, type, Operation.QUERY);
	}

	/**
	 * 获取某个task的详细信息
	 * 
	 * @param task_id
	 * @return
	 */
	public AsyncTaskPM360 getTaskDetailByTaskID(
			final DataManagerInterface manager, int task_id) {
		// 初始化返回类型
		Type type = AnalysisManager.GSON ? new TypeToken<List<ZH_group_task>>() {
		}.getType()
				: ZH_group_task.class;
		// 设置调用参数，调用远程服务
		return new RemoteService().setParams(SERVICE_NAME,
				"getTaskDetailByTaskID", task_id).call(manager, type,
				Operation.QUERY);
	}

	/**
	 * 获取发布后的任务列表
	 * 
	 * @param project_id
	 * @param user_id_
	 * @return
	 */
	public AsyncTaskPM360 getPublishTaskList(
			final DataManagerInterface manager, int zh_group_id) {
		// 初始化返回类型
		Type type = AnalysisManager.GSON ? new TypeToken<List<ZH_group_task>>() {
		}.getType()
				: ZH_group_task.class;
		// 设置调用参数，调用远程服务
		return new RemoteService().setParams(SERVICE_NAME,
				"getPublishTaskList", zh_group_id).call(manager, type,
				Operation.QUERY);
	}

	/**
	 * 添加一个任务
	 * 
	 * @param manager
	 * @param task
	 *            任务信息
	 * @return
	 */
	public AsyncTaskPM360 addTask(final DataManagerInterface manager,
			ZH_group_task task) {

		// 初始化返回类型
		Type type = AnalysisManager.GSON ? new TypeToken<List<ZH_group_task>>() {
		}.getType()
				: ZH_group_task.class;

		// 设置调用参数，调用远程服务
		return new RemoteService().setParams(SERVICE_NAME, "addTask", task)
				.call(manager, type, Operation.ADD);
	}
	
	/**
	 * 添加一个任务,并且修改父
	 * 
	 * @param manager
	 * @param task
	 *            任务信息
	 * @return
	 */
	public AsyncTaskPM360 addTaskAndModifyParent(final DataManagerInterface manager,
			ZH_group_task task, ZH_group_task taskParent) {

		// 初始化返回类型
		Type type = AnalysisManager.GSON ? new TypeToken<List<ZH_group_task>>() {
		}.getType()
				: ZH_group_task.class;

		// 设置调用参数，调用远程服务
		return new RemoteService().setParams(SERVICE_NAME, "addTaskAndModifyParent", task, taskParent)
				.call(manager, type, Operation.ADD);
	}

	/**
	 * 假删除
	 * 
	 * @param manager
	 * @param task
	 *            任务信息
	 * @return
	 */
	public AsyncTaskPM360 updateTaskForDelete(
			final DataManagerInterface manager, String taskString) {
		// 设置调用参数，调用远程服务
		return new RemoteService().setParams(SERVICE_NAME,
				"updateTaskForDelete", taskString).call(manager,
				Operation.MODIFY);
	}

	/**
	 * 删除一个任务
	 * 
	 * @param manager
	 * @param task
	 *            任务信息
	 * @return
	 */
	public AsyncTaskPM360 delTask(final DataManagerInterface manager,
			String taskString) {
		// 设置调用参数，调用远程服务
		return new RemoteService().setParams(SERVICE_NAME, "deleteTask",
				taskString).call(manager, Operation.DELETE);
	}

	/**
	 * 修改任务常用属性
	 * 
	 * @param manager
	 * @param taskAttribute
	 * @return
	 */
	public AsyncTaskPM360 updateTask(final DataManagerInterface manager) {
		// 设置调用参数，调用远程服务
		return new RemoteService().setParams(SERVICE_NAME, "updateTask",
				mGroup_task).call(manager, Operation.MODIFY);
	}

	/**
	 * 批量修改任务的类型
	 * 
	 * @param ZH_group_taskLISTJSON
	 * @return
	 */
	public AsyncTaskPM360 updateTaskType(final DataManagerInterface manager,
			List<ZH_group_task> ZH_group_taskLIST) {
		// 设置调用参数，调用远程服务
		return new RemoteService().setParams(SERVICE_NAME, "updateTaskType",
				ZH_group_taskLIST).call(manager, Operation.MODIFY);
	}

	/**
	 * 批量修改任务的责任人
	 * 
	 * @param ZH_group_taskLISTJSON
	 * @return
	 */
	public AsyncTaskPM360 updateTaskOwner(final DataManagerInterface manager,
			List<ZH_group_task> ZH_group_taskLIST) {
		// 设置调用参数，调用远程服务
		return new RemoteService().setParams(SERVICE_NAME, "updateTaskOwner",
				ZH_group_taskLIST).call(manager, Operation.MODIFY);
	}

	/**
	 * 
	 * 发布计划
	 * 需要填写project_id zh_group_id zh_group_node_name safety_user quality_user
	 * @param manager
	 * @param project_id
	 * @param zh_group_id
	 * @param zh_group_node_name
	 * @return
	 */
	public AsyncTaskPM360 publish(final DataManagerInterface manager,
			ZH_group group) {
		// 设置调用参数，调用远程服务
		return new RemoteService().setParams(SERVICE_NAME, "publish",
				group).call(manager,
				Operation.PUBLISH);
	}

	/**
	 * 获取任务的反馈列表
	 * 
	 * @param manager
	 * @param feedBack
	 */
	public AsyncTaskPM360 getFeedbackList(final DataManagerInterface manager,
			int zh_group_id) {
		// 初始化返回类型
		Type type = AnalysisManager.GSON ? new TypeToken<List<ZH_group_feedback>>() {
		}.getType()
				: ZH_group_feedback.class;
		// 设置调用参数，调用远程服务
		return new RemoteService().setParams(SERVICE_NAME, "getFeedbackList",
				zh_group_id).call(manager, type, Operation.QUERY);
	}

	/**
	 * 修改任务的反馈信息
	 * 
	 * @param manager
	 * @param feedBack
	 * @return
	 */
	public AsyncTaskPM360 addFeedback(final DataManagerInterface manager,
			User user) {
		// 初始化返回类型
		Type type = AnalysisManager.GSON ? new TypeToken<List<ZH_group_feedback>>() {
		}.getType()
				: ZH_group_feedback.class;

		// 设置调用参数，调用远程服务
		return new RemoteService().setParams(SERVICE_NAME, "updateFeedback",
				mZH_group_feedback, user.getTenant_id()).call(manager, type,
				Operation.ADD);
	}

	/**
	 * 修改任务的反馈信息
	 * 
	 * @param manager
	 * @param feedBack
	 * @return
	 */
	public AsyncTaskPM360 updateFeedback(final DataManagerInterface manager,
			User user) {
		// 设置调用参数，调用远程服务
		return new RemoteService().setParams(SERVICE_NAME, "updateFeedback",
				mZH_group_feedback, user.getTenant_id()).call(manager,
				Operation.MODIFY);
	}

	/**
	 * 获取形象成果
	 * 
	 * @param task_id
	 * @return
	 */
	public AsyncTaskPM360 getXingXiangFiles(final DataManagerInterface manager,
			int tenant_id) {
		// 初始化返回类型
		Type type = AnalysisManager.GSON ? new TypeToken<List<Files>>() {
		}.getType() : Files.class;
		// 设置调用参数，调用远程服务
		return new RemoteService().setParams(SERVICE_NAME, "getXingXiangFiles",
				mGroup_task.getTask_id(), tenant_id).call(manager, type,
				Operation.QUERY);
	}

	/**
	 * 获取参考文档
	 * 
	 * @param task_id
	 * @return
	 */
	public AsyncTaskPM360 getReferenceFiles(final DataManagerInterface manager,
			int tenant_id) {
		// 初始化返回类型
		Type type = AnalysisManager.GSON ? new TypeToken<List<Files>>() {
		}.getType() : Files.class;
		// 设置调用参数，调用远程服务
		return new RemoteService().setParams(SERVICE_NAME, "getReferenceFiles",
				mGroup_task.getTask_id(), tenant_id).call(manager, type,
				Operation.QUERY);
	}
}
