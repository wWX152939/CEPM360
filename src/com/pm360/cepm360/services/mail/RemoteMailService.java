package com.pm360.cepm360.services.mail;

import com.google.gson.reflect.TypeToken;
import com.pm360.cepm360.app.common.activity.DataManagerInterface;
import com.pm360.cepm360.common.json.AnalysisManager;
import com.pm360.cepm360.entity.MailBox;
import com.pm360.cepm360.services.common.AsyncTaskPM360;
import com.pm360.cepm360.services.common.Operation;
import com.pm360.cepm360.services.common.RemoteService;

import java.lang.reflect.Type;
import java.util.List;

/**
 * 远程邮件服务
 * 
 * @author Andy
 * 
 */
public class RemoteMailService {
	// 框架服务类全限定名
	private static final String SERVICE_NAME = "com.pm360.cepm360.services.mail.MailService";
	// 单例类变量
	private static RemoteMailService gService;

	/**
	 * 单例模式
	 * 
	 * @return
	 */
	public static synchronized RemoteMailService getInstance() {
		if (gService == null) {
			gService = new RemoteMailService();
		}
		return gService;
	}

	/**
	 * 销毁实例对象
	 */
	public static void destroyInstance() {
		gService = null;
	}

	private RemoteMailService() {

	}

	/**
	 * 获取个人收件箱
	 * 
	 * @param user_id
	 * @param tenant_id
	 * @return
	 */
	public AsyncTaskPM360 getInbox(final DataManagerInterface manager,
			int user_id, int tenant_id) {
		// 初始化返回类型
		Type type = AnalysisManager.GSON ? new TypeToken<List<MailBox>>() {
		}.getType() : MailBox.class;
		// 设置调用参数，调用远程服务
		return new RemoteService().setParams(SERVICE_NAME, "getInbox", user_id,
				tenant_id).call(manager, type, Operation.QUERY);
	}

	/**
	 * 获取个人发件箱
	 * 
	 * @param user_id
	 * @param tenant_id
	 * @return
	 */
	public AsyncTaskPM360 getOutbox(final DataManagerInterface manager,
			int user_id, int tenant_id) {
		// 初始化返回类型
		Type type = AnalysisManager.GSON ? new TypeToken<List<MailBox>>() {
		}.getType() : MailBox.class;
		// 设置调用参数，调用远程服务
		return new RemoteService().setParams(SERVICE_NAME, "getOutbox",
				user_id, tenant_id).call(manager, type, Operation.QUERY);
	}

	/**
	 * 获取个人草稿箱
	 * 
	 * @param user_id
	 * @param tenant_id
	 * @return
	 */
	public AsyncTaskPM360 getDraftbox(final DataManagerInterface manager,
			int user_id, int tenant_id) {
		// 初始化返回类型
		Type type = AnalysisManager.GSON ? new TypeToken<List<MailBox>>() {
		}.getType() : MailBox.class;
		// 设置调用参数，调用远程服务
		return new RemoteService().setParams(SERVICE_NAME, "getDraftbox",
				user_id, tenant_id).call(manager, type, Operation.QUERY);
	}

	/**
	 * 发送邮件(非草稿箱的 发送、回复、全回)
	 * 
	 * @param MailBoxJSON
	 * @return
	 */
	public AsyncTaskPM360 sendMail(final DataManagerInterface manager,
			MailBox MailBox) {
		// 设置调用参数，调用远程服务
		return new RemoteService().setParams(SERVICE_NAME, "sendMail", MailBox)
				.call(manager, Operation.ADD);
	}

	/**
	 * 保存-发送邮件(非草稿箱的 发送、回复、全回)
	 * 
	 * @param MailBoxJSON
	 * @return
	 */
	public AsyncTaskPM360 saveSendMail(final DataManagerInterface manager,
			MailBox MailBox) {
		// 设置调用参数，调用远程服务
		return new RemoteService().setParams(SERVICE_NAME, "saveSendMail", MailBox)
				.call(manager, Operation.ADD);
	}

	/**
	 * 标记已读状态
	 * 
	 * @param mail_box_id
	 * @param is_read
	 * @param message_id
	 * @return
	 */
	public AsyncTaskPM360 markRead(final DataManagerInterface manager,
			int mail_box_id, String is_read, int message_id) {
		// 设置调用参数，调用远程服务
		return new RemoteService().setParams(SERVICE_NAME, "markRead",
				mail_box_id, is_read, message_id).call(manager,
				Operation.MODIFY);
	}

	/**
	 * 发送邮件(草稿箱的 发送、回复、全回)
	 * 
	 * @param MailBoxJSON
	 * @return
	 */
	public AsyncTaskPM360 sendMailByDraft(final DataManagerInterface manager,
			MailBox MailBox) {
		// 设置调用参数，调用远程服务
		return new RemoteService().setParams(SERVICE_NAME, "sendMailByDraft",
				MailBox).call(manager, Operation.MODIFY);
	}

	/**
	 * 修改——发送邮件(草稿箱的 发送、回复、全回)
	 * 
	 * @param MailBoxJSON
	 * @return
	 */
	public AsyncTaskPM360 updateSendMailByDraft(
			final DataManagerInterface manager, MailBox MailBox) {
		// 设置调用参数，调用远程服务
		return new RemoteService().setParams(SERVICE_NAME, "updateSendMailByDraft",
				MailBox).call(manager, Operation.MODIFY);
	}

	/**
	 * 
	 * 删除收件箱邮件
	 * 
	 * @param mail_box_id
	 * @param del_in
	 * @param user_id
	 * @return
	 */
	public AsyncTaskPM360 deleteInboxMail(final DataManagerInterface manager,
			int mail_box_id, String del_in, int user_id) {
		// 设置调用参数，调用远程服务
		return new RemoteService().setParams(SERVICE_NAME, "deleteInboxMail",
				mail_box_id, del_in, user_id).call(manager, Operation.DELETE);
	}

	/**
	 * 删除草稿箱邮件
	 * 
	 * @param mail_box_id
	 * @return
	 */
	public AsyncTaskPM360 deleteDraftMail(final DataManagerInterface manager,
			int mail_box_id) {
		// 设置调用参数，调用远程服务
		return new RemoteService().setParams(SERVICE_NAME, "deleteDraftMail",
				mail_box_id).call(manager, Operation.DELETE);
	}

	/**
	 * 删除发件箱邮件
	 * 
	 * @param mail_box_id
	 * @param del_out
	 * @return
	 */
	public AsyncTaskPM360 deleteOutboxMail(final DataManagerInterface manager,
			int mail_box_id, String del_out) {
		// 设置调用参数，调用远程服务
		return new RemoteService().setParams(SERVICE_NAME, "deleteOutboxMail",
				mail_box_id, del_out).call(manager, Operation.DELETE);
	}
}
