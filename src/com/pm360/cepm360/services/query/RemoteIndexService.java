package com.pm360.cepm360.services.query;

import com.google.gson.reflect.TypeToken;
import com.pm360.cepm360.app.common.activity.DataManagerInterface;
import com.pm360.cepm360.common.json.AnalysisManager;
import com.pm360.cepm360.entity.Announcement;
import com.pm360.cepm360.entity.Feedback;
import com.pm360.cepm360.entity.Files;
import com.pm360.cepm360.entity.Index_daiban;
import com.pm360.cepm360.entity.Index_document;
import com.pm360.cepm360.entity.Index_feedback;
import com.pm360.cepm360.entity.Index_task;
import com.pm360.cepm360.entity.ZH_group;
import com.pm360.cepm360.entity.ZH_group_feedback;
import com.pm360.cepm360.services.common.AsyncTaskPM360;
import com.pm360.cepm360.services.common.Operation;
import com.pm360.cepm360.services.common.RemoteService;

import java.lang.reflect.Type;
import java.util.List;

/**
 * 远程首页服务
 * 
 * @author Andy
 * 
 */
public class RemoteIndexService {
	// 框架服务类全限定名
	private static final String SERVICE_NAME = "com.pm360.cepm360.services.query.IndexService";
	// 单例类变量
	private static RemoteIndexService gService;

	/**
	 * 单例模式
	 * 
	 * @return
	 */
	public static synchronized RemoteIndexService getInstance() {
		if (gService == null) {
			gService = new RemoteIndexService();
		}
		return gService;
	}

	/**
	 * 销毁实例对象
	 */
	public static void destroyInstance() {
		gService = null;
	}

	private RemoteIndexService() {

	}

	/**
	 * 代办事项-限制5条(1,2,3,4,5,6,7,13,14,22)
	 * 
	 * @param tenant_id
	 * @param user_id
	 * @return
	 */
	public AsyncTaskPM360 getToDoListByLimit(
			final DataManagerInterface manager, int tenant_id, int user_id) {
		// 初始化返回类型
		Type type = AnalysisManager.GSON ? new TypeToken<List<Index_daiban>>() {
		}.getType() : Index_daiban.class;
		// 设置调用参数，调用远程服务
		return new RemoteService().setParams(SERVICE_NAME,
				"getToDoListByLimit", tenant_id, user_id).call(manager, type,
				Operation.QUERY);
	}

	/**
	 * 代办事项-更多(1,2,3,4,5,6,7,13,14,22)
	 * 
	 * @param tenant_id
	 * @param user_id
	 * @return
	 */
	public AsyncTaskPM360 getMoreToDoList(final DataManagerInterface manager,
			int tenant_id, int user_id) {
		// 初始化返回类型
		Type type = AnalysisManager.GSON ? new TypeToken<List<Index_daiban>>() {
		}.getType() : Index_daiban.class;
		// 设置调用参数，调用远程服务
		return new RemoteService().setParams(SERVICE_NAME, "getMoreToDoList",
				tenant_id, user_id).call(manager, type, Operation.QUERY);
	}

	/**
	 * 代办事项-带条件查询(1,2,3,4,5,6,7,13,14,22)
	 * 
	 * @param Index_daibanJSON
	 * @return
	 */
	public AsyncTaskPM360 getToDoListByCondition(
			final DataManagerInterface manager, Index_daiban Index_daiban) {
		// 初始化返回类型
		Type type = AnalysisManager.GSON ? new TypeToken<List<Index_daiban>>() {
		}.getType() : Index_daiban.class;
		// 设置调用参数，调用远程服务
		return new RemoteService().setParams(SERVICE_NAME,
				"getToDoListByCondition", Index_daiban).call(manager, type,
				Operation.QUERY);
	}

	/**
	 * 我的任务-统计
	 * 
	 * @param tenant_id
	 * @param user_id
	 * @param tenant_type
	 * @return
	 */
	public AsyncTaskPM360 getMyTaskList(final DataManagerInterface manager,
			int tenant_id, int user_id, String tenant_type) {
		// 初始化返回类型
		Type type = AnalysisManager.GSON ? new TypeToken<List<Index_task>>() {
		}.getType() : Index_task.class;
		// 设置调用参数，调用远程服务
		return new RemoteService().setParams(SERVICE_NAME, "getMyTaskList",
				tenant_id, user_id, tenant_type).call(manager, type,
				Operation.QUERY);
	}

//	/**
//	 * 我的任务-更多
//	 * 
//	 * @param tenant_id
//	 * @param user_id
//	 * @param tenant_type
//	 * @return
//	 */
//	public AsyncTaskPM360 getMoreMyTaskList(final DataManagerInterface manager,
//			int tenant_id, int user_id, String tenant_type) {
//		// 初始化返回类型
//		Type type = AnalysisManager.GSON ? new TypeToken<List<Index_task>>() {
//		}.getType() : Index_task.class;
//		// 设置调用参数，调用远程服务
//		return new RemoteService().setParams(SERVICE_NAME, "getMoreMyTaskList",
//				tenant_id, user_id, tenant_type).call(manager, type,
//				Operation.QUERY);
//	}

	/**
	 * 我的任务-带条件查询
	 * 
	 * @param Index_taskJSON
	 * @return
	 */
	public AsyncTaskPM360 getMyTaskListByCondition(
			final DataManagerInterface manager, Index_task Index_task) {
		// 初始化返回类型
		Type type = AnalysisManager.GSON ? new TypeToken<List<Index_task>>() {
		}.getType() : Index_task.class;
		// 设置调用参数，调用远程服务
		return new RemoteService().setParams(SERVICE_NAME,
				"getMyTaskListByCondition", Index_task).call(manager, type,
				Operation.QUERY);
	}

	/**
	 * 最新反馈-限制5条(9,10,17,18,19,20,21)
	 * 
	 * @param tenant_id
	 * @param user_id
	 * @return
	 */
	public AsyncTaskPM360 getFeedbackListByLimit(
			final DataManagerInterface manager, int tenant_id, int user_id) {
		// 初始化返回类型
		Type type = AnalysisManager.GSON ? new TypeToken<List<Index_feedback>>() {
		}.getType()
				: Index_feedback.class;
		// 设置调用参数，调用远程服务
		return new RemoteService().setParams(SERVICE_NAME,
				"getFeedbackListByLimit", tenant_id, user_id).call(manager,
				type, Operation.QUERY);
	}

	/**
	 * 最新反馈-更多(9,10,17,18,19,20,21)
	 * 
	 * @param tenant_id
	 * @param user_id
	 * @return
	 */
	public AsyncTaskPM360 getMoreFeedbackList(
			final DataManagerInterface manager, int tenant_id, int user_id) {
		// 初始化返回类型
		Type type = AnalysisManager.GSON ? new TypeToken<List<Index_feedback>>() {
		}.getType()
				: Index_feedback.class;
		// 设置调用参数，调用远程服务
		return new RemoteService().setParams(SERVICE_NAME,
				"getMoreFeedbackList", tenant_id, user_id).call(manager, type,
				Operation.QUERY);
	}

	/**
	 * 最新反馈-带条件查询(9,10,17,18,19,20,21)
	 * 
	 * @param Index_feedbackJSON
	 * @return
	 */
	public AsyncTaskPM360 getFeedbackListByCondition(
			final DataManagerInterface manager, Index_feedback Index_feedback) {
		// 初始化返回类型
		Type type = AnalysisManager.GSON ? new TypeToken<List<Index_feedback>>() {
		}.getType()
				: Index_feedback.class;
		// 设置调用参数，调用远程服务
		return new RemoteService().setParams(SERVICE_NAME,
				"getFeedbackListByCondition", Index_feedback).call(manager,
				type, Operation.QUERY);
	}

	/**
	 * 最新文档-限制5条(FILE_TYPE 1,10,3,15,16,17,18)
	 * 
	 * @param tenant_id
	 * @param user_id
	 * @return
	 */
	public AsyncTaskPM360 getDocumentListByLimit(
			final DataManagerInterface manager, int tenant_id, int user_id) {
		// 初始化返回类型
		Type type = AnalysisManager.GSON ? new TypeToken<List<Files>>() {
		}.getType() : Files.class;
		// 设置调用参数，调用远程服务
		return new RemoteService().setParams(SERVICE_NAME,
				"getDocumentListByLimit", tenant_id, user_id).call(manager,
				type, Operation.QUERY);
	}

	/**
	 * 最新文档-更多(FILE_TYPE 1,10,3,15,16,17,18)
	 * 
	 * @param tenant_id
	 * @param user_id
	 * @return
	 */
	public AsyncTaskPM360 getMoreDocumentList(
			final DataManagerInterface manager, int tenant_id, int user_id) {
		// 初始化返回类型
		Type type = AnalysisManager.GSON ? new TypeToken<List<Files>>() {
		}.getType() : Files.class;
		// 设置调用参数，调用远程服务
		return new RemoteService().setParams(SERVICE_NAME,
				"getMoreDocumentList", tenant_id, user_id).call(manager, type,
				Operation.QUERY);
	}

	/**
	 * 最新文档-带条件查询(FILE_TYPE 1,10,3,15,16,17,18)
	 * 
	 * @param Index_documentJSON
	 * @return
	 */
	public AsyncTaskPM360 getDocumentListByCondition(
			final DataManagerInterface manager, Index_document Index_document) {
		// 初始化返回类型
		Type type = AnalysisManager.GSON ? new TypeToken<List<Files>>() {
		}.getType() : Files.class;
		// 设置调用参数，调用远程服务
		return new RemoteService().setParams(SERVICE_NAME,
				"getDocumentListByCondition", Index_document).call(manager,
				type, Operation.QUERY);
	}

	/**
	 * 获取形象进展图片(FILE_TYPE 3,15)
	 * 
	 * @param tenant_id
	 * @return
	 */
	public AsyncTaskPM360 getProjectPICListByLimit(
			final DataManagerInterface manager, int tenant_id) {
		// 初始化返回类型
		Type type = AnalysisManager.GSON ? new TypeToken<List<Files>>() {
		}.getType() : Files.class;
		// 设置调用参数，调用远程服务
		return new RemoteService().setParams(SERVICE_NAME,
				"getProjectPICListByLimit", tenant_id).call(manager, type,
				Operation.QUERY);
	}

	/**
	 * 最新公告-限制5条
	 * 
	 * @param tenant_id
	 * @return
	 */
	public AsyncTaskPM360 getAnnouncementListByLimit(
			final DataManagerInterface manager, int tenant_id) {
		// 初始化返回类型
		Type type = AnalysisManager.GSON ? new TypeToken<List<Announcement>>() {
		}.getType() : Announcement.class;
		// 设置调用参数，调用远程服务
		return new RemoteService().setParams(SERVICE_NAME,
				"getAnnouncementListByLimit", tenant_id).call(manager, type,
				Operation.QUERY);
	}

	/**
	 * 最新公告-更多
	 * 
	 * @param tenant_id
	 * @return
	 */
	public AsyncTaskPM360 getMoreAnnouncementList(
			final DataManagerInterface manager, int tenant_id) {
		// 初始化返回类型
		Type type = AnalysisManager.GSON ? new TypeToken<List<Announcement>>() {
		}.getType() : Announcement.class;
		// 设置调用参数，调用远程服务
		return new RemoteService().setParams(SERVICE_NAME,
				"getMoreAnnouncementList", tenant_id).call(manager, type,
				Operation.QUERY);
	}

	/**
	 * 最新公告-带条件查询
	 * 
	 * @param AnnouncementJSON
	 * @return
	 */
	public AsyncTaskPM360 getAnnouncementListByCondition(
			final DataManagerInterface manager, Announcement Announcement) {
		// 初始化返回类型
		Type type = AnalysisManager.GSON ? new TypeToken<List<Announcement>>() {
		}.getType() : Announcement.class;
		// 设置调用参数，调用远程服务
		return new RemoteService().setParams(SERVICE_NAME,
				"getAnnouncementListByCondition", Announcement).call(manager,
				type, Operation.QUERY);
	}

	/**
	 * 根据任务ID查询反馈详细信息（非组合）
	 * 
	 * @param task_id
	 * @return
	 */
	public AsyncTaskPM360 getFeedBackByTaskID(
			final DataManagerInterface manager, int task_id) {
		// 初始化返回类型
		Type type = AnalysisManager.GSON ? new TypeToken<List<Feedback>>() {
		}.getType() : Feedback.class;
		// 设置调用参数，调用远程服务
		return new RemoteService().setParams(SERVICE_NAME,
				"getFeedBackByTaskID", task_id).call(manager, type,
				Operation.QUERY);
	}

	/**
	 * 根据任务ID查询反馈详细信息（组合）
	 * 
	 * @param task_id
	 * @return
	 */
	public AsyncTaskPM360 getFeedBackByTaskIDForZH(
			final DataManagerInterface manager, int task_id) {
		// 初始化返回类型
		Type type = AnalysisManager.GSON ? new TypeToken<List<ZH_group_feedback>>() {
		}.getType()
				: ZH_group_feedback.class;
		// 设置调用参数，调用远程服务
		return new RemoteService().setParams(SERVICE_NAME,
				"getFeedBackByTaskIDForZH", task_id).call(manager, type,
				Operation.QUERY);
	}

	/**
	 * 根据组合ID,查询该组合的节点TAB
	 * 
	 * @param zh_group_id
	 * @return
	 */
	public AsyncTaskPM360 getNodeModule(final DataManagerInterface manager,
			int zh_group_id) {
		// 初始化返回类型
		Type type = AnalysisManager.GSON ? new TypeToken<List<ZH_group>>() {
		}.getType() : ZH_group.class;
		// 设置调用参数，调用远程服务
		return new RemoteService().setParams(SERVICE_NAME, "getNodeModule",
				zh_group_id).call(manager, type, Operation.QUERY);
	}
}
