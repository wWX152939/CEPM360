package com.pm360.cepm360.services.plan;

import com.google.gson.reflect.TypeToken;
import com.pm360.cepm360.app.common.activity.DataManagerInterface;
import com.pm360.cepm360.common.json.AnalysisManager;
import com.pm360.cepm360.entity.Changes;
import com.pm360.cepm360.entity.Feedback;
import com.pm360.cepm360.entity.Files;
import com.pm360.cepm360.entity.Project;
import com.pm360.cepm360.entity.Task;
import com.pm360.cepm360.entity.User;
import com.pm360.cepm360.services.common.AsyncTaskPM360;
import com.pm360.cepm360.services.common.Operation;
import com.pm360.cepm360.services.common.RemoteService;

import java.lang.reflect.Type;
import java.util.List;

public class RemoteTaskService {
	// 任务服务的类全限定名
	private static final String SERVICE_NAME = "com.pm360.cepm360.services.plan.TaskService";
	// 单例对象类变量
	private static RemoteTaskService gService;
	// 当前任务
	private Task mTask;
	// 反馈
	private Feedback mFeedback;

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
	public RemoteTaskService setTask(Task task) {
		mTask = task;
		return this;
	}

	/**
	 * 获取feedback
	 * 
	 * @param mFeedback
	 */
	public Feedback getFeedback() {
		return mFeedback;
	}

	/**
	 * 设置反馈
	 * 
	 * @param mFeedback
	 */
	public RemoteTaskService setFeedback(Feedback feedback) {
		mFeedback = feedback;
		return this;
	}

	/**
	 * 获取task
	 * 
	 * @param task
	 */
	public Task getTask() {
		return mTask;
	}

	/**
	 * 获取任务列表
	 * 
	 * @param manager
	 * @return
	 */
	public AsyncTaskPM360 getTaskList(final DataManagerInterface manager,
			Project project) {
		// 初始化返回类型
		Type type = AnalysisManager.GSON ? new TypeToken<List<Task>>() {
		}.getType() : Task.class;
		// 设置调用参数，调用远程服务
		return new RemoteService().setParams(SERVICE_NAME, "getTaskList",
				project.getProject_id()).call(manager, type, Operation.QUERY);
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
		Type type = AnalysisManager.GSON ? new TypeToken<List<Task>>() {
		}.getType() : Task.class;
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
			final DataManagerInterface manager, Project project) {
		// 初始化返回类型
		Type type = AnalysisManager.GSON ? new TypeToken<List<Task>>() {
		}.getType() : Task.class;
		// 设置调用参数，调用远程服务
		return new RemoteService().setParams(SERVICE_NAME,
				"getPublishTaskList", project.getProject_id()).call(manager,
				type, Operation.QUERY);
	}

	/**
	 * 添加一个任务
	 * 
	 * @param manager
	 * @param task
	 *            任务信息
	 * @return
	 */
	public AsyncTaskPM360 addTask(final DataManagerInterface manager, Task task) {

		// 初始化返回类型
		Type type = AnalysisManager.GSON ? new TypeToken<List<Task>>() {
		}.getType() : Task.class;

		// 设置调用参数，调用远程服务
		return new RemoteService().setParams(SERVICE_NAME, "addTask", task)
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
		return new RemoteService().setParams(SERVICE_NAME, "updateTask", mTask)
				.call(manager, Operation.MODIFY);
	}
	
	/**
	 * 修改任务组属性
	 * 
	 * @param manager
	 * @param taskAttribute
	 * @return
	 */
	public AsyncTaskPM360 updateTask(final DataManagerInterface manager, List<Task> taskList) {
		// 设置调用参数，调用远程服务
		return new RemoteService().setParams(SERVICE_NAME, "updateTaskList", taskList)
				.call(manager, Operation.MODIFY);
	}

	/**
	 * 发布计划
	 * 
	 * @param project_id
	 * @param project_name
	 * @return
	 */
	public AsyncTaskPM360 publish(final DataManagerInterface manager,
			Project project) {
		// 设置调用参数，调用远程服务
		return new RemoteService().setParams(SERVICE_NAME, "publish",
				project.getProject_id(), project.getName()).call(manager,
				Operation.PUBLISH);
	}

	/**
	 * 获取历史版本
	 * 
	 * @param manager
	 */
	public AsyncTaskPM360 getVersionList(final DataManagerInterface manager,
			Project project) {
		// 初始化返回类型
		Type type = AnalysisManager.GSON ? new TypeToken<List<Changes>>() {
		}.getType() : Changes.class;
		// 设置调用参数，调用远程服务
		return new RemoteService().setParams(SERVICE_NAME, "getVersion",
				project.getProject_id()).call(manager, type, Operation.QUERY);
	}

	/**
	 * 获取历史版本的task列表
	 * 
	 * @param manager
	 */
	public AsyncTaskPM360 getVersionTaskList(
			final DataManagerInterface manager, Changes change) {
		// 初始化返回类型
		Type type = AnalysisManager.GSON ? new TypeToken<List<Task>>() {
		}.getType() : Task.class;
		// 设置调用参数，调用远程服务
		return new RemoteService().setParams(SERVICE_NAME,
				"getVersionTaskList", change.getChange_id()).call(manager,
				type, Operation.QUERY);
	}

	// /**
	// * 获取某个任务下实际消耗资源信息
	// *
	// * @param task_id_
	// * @param project_id_
	// * @return
	// */
	// public AsyncTaskPM360 getActualConsume(final DataManagerInterface
	// manager) {
	// // 初始化返回类型
	// Type type = AnalysisManager.GSON
	// ? new TypeToken<List<Consume>>() {}.getType()
	// : Consume.class;
	//
	// // 设置调用参数，调用远程服务
	// return new RemoteService()
	// .setParams( SERVICE_NAME,
	// "getActualConsume",
	// mTask.getTask_id(),mTask.getProject_id())
	// .call(manager, type, Operation.QUERY);
	// }

	// /**
	// * 获取某个任务下的计划资源信息
	// *
	// * @param task_id_
	// * @param project_id_
	// * @return
	// */
	// public AsyncTaskPM360 getPlanConsume(final DataManagerInterface manager)
	// {
	// // 初始化返回类型
	// Type type = AnalysisManager.GSON
	// ? new TypeToken<List<Consume>>() {}.getType()
	// : Consume.class;
	//
	// // 设置调用参数，调用远程服务
	// return new RemoteService()
	// .setParams( SERVICE_NAME,
	// "getPlanConsume",
	// mTask.getTask_id(),mTask.getProject_id())
	// .call(manager, type, Operation.QUERY);
	// }

	// /**
	// * 添加任务资源
	// * @param manager
	// * @param consume
	// */
	// public AsyncTaskPM360 addConsume(final DataManagerInterface
	// manager,List<Consume> consumelist) {
	// // 设置调用参数，调用远程服务
	// return new RemoteService()
	// .setParams( SERVICE_NAME,
	// "addConsume",
	// consumelist)
	// .call(manager, Operation.ADD);
	// }

	// /**
	// * 删除任务资源
	// * @param manager
	// * @param consume
	// */
	// public AsyncTaskPM360 deleteConsume(final DataManagerInterface manager) {
	// // 设置调用参数，调用远程服务
	// return new RemoteService()
	// .setParams( SERVICE_NAME,
	// "deleteConsume",
	// mConsume.getConsume_id())
	// .call(manager, Operation.DELETE);
	// }

	// /**
	// * 更新任务资源
	// * @param manager
	// * @param consume
	// */
	// public AsyncTaskPM360 updateConsume(final DataManagerInterface manager) {
	// // 设置调用参数，调用远程服务
	// return new RemoteService()
	// .setParams( SERVICE_NAME,
	// "updateConsume",
	// mConsume)
	// .call(manager, Operation.MODIFY);
	// }

	/**
	 * 获取任务的反馈列表
	 * 
	 * @param manager
	 * @param feedBack
	 */
	public AsyncTaskPM360 getFeedbackList(final DataManagerInterface manager,
			Project project) {
		// 初始化返回类型
		Type type = AnalysisManager.GSON ? new TypeToken<List<Task>>() {
		}.getType() : Feedback.class;
		// 设置调用参数，调用远程服务
		return new RemoteService().setParams(SERVICE_NAME, "getFeedbackList",
				project.getProject_id()).call(manager, type, Operation.QUERY);
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
		Type type = AnalysisManager.GSON ? new TypeToken<List<Feedback>>() {
		}.getType() : Feedback.class;

		// 设置调用参数，调用远程服务
		return new RemoteService().setParams(SERVICE_NAME, "updateFeedback",
				mFeedback, user.getTenant_id()).call(manager, type,
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
				mFeedback, user.getTenant_id()).call(manager, Operation.MODIFY);
	}

	/**
	 * 获取现场图文
	 * 
	 * @param manager
	 * @param tenant_id
	 */
	public AsyncTaskPM360 getXianChangFiles(final DataManagerInterface manager,
			int tenant_id) {
		// 初始化返回类型
		Type type = AnalysisManager.GSON ? new TypeToken<List<Files>>() {
		}.getType() : Files.class;
		// 设置调用参数，调用远程服务
		return new RemoteService().setParams(SERVICE_NAME, "getXianChangFiles",
				mTask.getTask_id(), tenant_id).call(manager, type,
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
				mTask.getTask_id(), tenant_id).call(manager, type,
				Operation.QUERY);
	}
}
