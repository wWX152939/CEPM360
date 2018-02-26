package com.pm360.cepm360.services.system;

import com.google.gson.reflect.TypeToken;
import com.pm360.cepm360.app.common.activity.DataManagerInterface;
import com.pm360.cepm360.common.json.AnalysisManager;
import com.pm360.cepm360.entity.Announcement;
import com.pm360.cepm360.entity.User;
import com.pm360.cepm360.services.common.AsyncTaskPM360;
import com.pm360.cepm360.services.common.Operation;
import com.pm360.cepm360.services.common.RemoteService;

import java.lang.reflect.Type;
import java.util.List;

/**
 * 远程公告服务
 * 
 * @author Andy
 * 
 */
public class RemoteAnnouncementService {
	// 框架服务类全限定名
	private static final String SERVICE_NAME = "com.pm360.cepm360.services.system.AnnouncementService";
	// 单例类变量
	private static RemoteAnnouncementService gService;

	/**
	 * 单例模式
	 * 
	 * @return
	 */
	public static synchronized RemoteAnnouncementService getInstance() {
		if (gService == null) {
			gService = new RemoteAnnouncementService();
		}
		return gService;
	}

	/**
	 * 销毁实例对象
	 */
	public static void destroyInstance() {
		gService = null;
	}

	private RemoteAnnouncementService() {

	}

	/**
	 * 获取公告列表
	 * 
	 * @param tenant_id
	 * 
	 * @return
	 */
	public AsyncTaskPM360 getAnnouncementList(
			final DataManagerInterface manager, int tenant_id) {
		// 初始化返回类型
		Type type = AnalysisManager.GSON ? new TypeToken<List<Announcement>>() {
		}.getType() : Announcement.class;
		// 设置调用参数，调用远程服务
		return new RemoteService().setParams(SERVICE_NAME,
				"getAnnouncementList", tenant_id).call(manager, type,
				Operation.QUERY);
	}

	/**
	 * 查看公告
	 * 
	 * @param announcement_id
	 * 
	 * @return
	 */
	public AsyncTaskPM360 viewAnnouncement(final DataManagerInterface manager,
			int announcement_id) {
		// 初始化返回类型
		Type type = AnalysisManager.GSON ? new TypeToken<List<Announcement>>() {
		}.getType() : Announcement.class;
		// 设置调用参数，调用远程服务
		return new RemoteService().setParams(SERVICE_NAME, "viewAnnouncement",
				announcement_id).call(manager, type, Operation.QUERY);
	}

	/**
	 * 新增公告内容或者直接发布
	 * 
	 * @param announcementJSON
	 * @param listUserJSON
	 * @return
	 */
	public AsyncTaskPM360 addAnnouncement(final DataManagerInterface manager,
			Announcement Announcement, List<User> userList) {
		// 初始化返回类型
		Type type = AnalysisManager.GSON ? new TypeToken<List<Announcement>>() {
		}.getType() : Announcement.class;
		// 设置调用参数，调用远程服务
		return new RemoteService().setParams(SERVICE_NAME, "addAnnouncement",
				Announcement, userList).call(manager, type, Operation.ADD);
	}

	/**
	 * 更新未发布的公告
	 * 
	 * @param announcementJSON
	 * @param listUserJSON
	 * @return
	 */
	public AsyncTaskPM360 updateAnnouncement(
			final DataManagerInterface manager, Announcement Announcement,
			List<User> userList) {
		// 初始化返回类型
		Type type = AnalysisManager.GSON ? new TypeToken<List<Announcement>>() {
		}.getType() : Announcement.class;
		// 设置调用参数，调用远程服务
		return new RemoteService().setParams(SERVICE_NAME,
				"updateAnnouncement", Announcement, userList).call(manager,
				type, Operation.MODIFY);
	}

	/**
	 * 删除某个公告
	 * 
	 * @param announcement_id
	 * @return
	 */
	public AsyncTaskPM360 deleteAnnouncement(
			final DataManagerInterface manager, int announcement_id) {
		// 设置调用参数，调用远程服务
		return new RemoteService().setParams(SERVICE_NAME,
				"deleteAnnouncement", announcement_id).call(manager,
				Operation.DELETE);
	}

}
