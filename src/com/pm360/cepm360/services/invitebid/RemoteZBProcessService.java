package com.pm360.cepm360.services.invitebid;

import com.google.gson.reflect.TypeToken;
import com.pm360.cepm360.app.common.activity.DataManagerInterface;
import com.pm360.cepm360.common.json.AnalysisManager;
import com.pm360.cepm360.entity.ZB_answer;
import com.pm360.cepm360.entity.ZB_bid_company;
import com.pm360.cepm360.entity.ZB_invite;
import com.pm360.cepm360.entity.ZB_pretrial;
import com.pm360.cepm360.entity.ZB_quote;
import com.pm360.cepm360.entity.ZB_score;
import com.pm360.cepm360.services.common.AsyncTaskPM360;
import com.pm360.cepm360.services.common.Operation;
import com.pm360.cepm360.services.common.RemoteService;

import java.lang.reflect.Type;
import java.util.List;

/**
 * 远程招标过程服务
 * 
 * @author Andy
 * 
 */
public class RemoteZBProcessService {

	// 任务服务的类全限定名
	private static final String SERVICE_NAME = "com.pm360.cepm360.services.invitebid.ZBProcessService";
	// 单例对象类变量
	private static RemoteZBProcessService gService;

	/**
	 * 单例模式
	 * 
	 * @return
	 */
	public static synchronized RemoteZBProcessService getInstance() {
		if (gService == null) {
			gService = new RemoteZBProcessService();
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
	private RemoteZBProcessService() {

	}

	/**
	 * 获取邀标单位列表
	 * 
	 * @param project_id
	 * @param zb_plan_id
	 * @return
	 */
	public AsyncTaskPM360 getZBInviteList(final DataManagerInterface manager,
			int project_id, int zb_plan_id) {
		// 初始化返回类型
		Type type = AnalysisManager.GSON ? new TypeToken<List<ZB_invite>>() {
		}.getType() : ZB_invite.class;

		// 设置调用参数，调用远程服务
		return new RemoteService().setParams(SERVICE_NAME, "getZBInviteList",
				project_id, zb_plan_id).call(manager, type, Operation.QUERY);
	}

	/**
	 * 获取投标单位列表
	 * 
	 * @param zb_plan_id
	 * @return
	 */
	public AsyncTaskPM360 getZBBidList(final DataManagerInterface manager,
			int zb_plan_id) {
		// 初始化返回类型
		Type type = AnalysisManager.GSON ? new TypeToken<List<ZB_bid_company>>() {
		}.getType()
				: ZB_bid_company.class;

		// 设置调用参数，调用远程服务
		return new RemoteService().setParams(SERVICE_NAME, "getZBBidList",
				zb_plan_id).call(manager, type, Operation.QUERY);
	}

	/**
	 * 获取预审列表
	 * 
	 * @param zb_plan_id
	 * @return
	 */
	public AsyncTaskPM360 getZBPretrialList(final DataManagerInterface manager,
			int zb_plan_id) {
		// 初始化返回类型
		Type type = AnalysisManager.GSON ? new TypeToken<List<ZB_pretrial>>() {
		}.getType() : ZB_pretrial.class;

		// 设置调用参数，调用远程服务
		return new RemoteService().setParams(SERVICE_NAME, "getZBPretrialList",
				zb_plan_id).call(manager, type, Operation.QUERY);
	}

	/**
	 * 获取澄清答疑列表
	 * 
	 * @param zb_plan_id
	 * @return
	 */
	public AsyncTaskPM360 getZBAnswerList(final DataManagerInterface manager,
			int zb_plan_id) {
		// 初始化返回类型
		Type type = AnalysisManager.GSON ? new TypeToken<List<ZB_answer>>() {
		}.getType() : ZB_answer.class;

		// 设置调用参数，调用远程服务
		return new RemoteService().setParams(SERVICE_NAME, "getZBAnswerList",
				zb_plan_id).call(manager, type, Operation.QUERY);
	}

	/**
	 * 获取报价列表
	 * 
	 * @param zb_plan_id
	 * @return
	 */
	public AsyncTaskPM360 getZBQuote(final DataManagerInterface manager,
			int zb_plan_id) {
		// 初始化返回类型
		Type type = AnalysisManager.GSON ? new TypeToken<List<ZB_quote>>() {
		}.getType() : ZB_quote.class;

		// 设置调用参数，调用远程服务
		return new RemoteService().setParams(SERVICE_NAME, "getZBQuote",
				zb_plan_id).call(manager, type, Operation.QUERY);
	}

	/**
	 * 获取评分列表
	 * 
	 * @param zb_plan_id
	 * @return
	 */
	public AsyncTaskPM360 getZBScore(final DataManagerInterface manager,
			int zb_plan_id) {
		// 初始化返回类型
		Type type = AnalysisManager.GSON ? new TypeToken<List<ZB_score>>() {
		}.getType() : ZB_score.class;

		// 设置调用参数，调用远程服务
		return new RemoteService().setParams(SERVICE_NAME, "getZBScore",
				zb_plan_id).call(manager, type, Operation.QUERY);
	}

	/**
	 * 增加邀标单位
	 * 
	 * @param ZB_inviteJSON
	 * @return
	 */
	public AsyncTaskPM360 addZBInvite(final DataManagerInterface manager,
			ZB_invite ZB_invite) {
		// 初始化返回类型
		Type type = AnalysisManager.GSON ? new TypeToken<List<ZB_invite>>() {
		}.getType() : ZB_invite.class;
		// 设置调用参数，调用远程服务
		return new RemoteService().setParams(SERVICE_NAME, "addZBInvite",
				ZB_invite).call(manager, type, Operation.ADD);
	}

	/**
	 * 增加投标单位
	 * 
	 * @param ZB_bid_companyJSON
	 * @return
	 */
	public AsyncTaskPM360 addZBBid(final DataManagerInterface manager,
			ZB_bid_company ZB_bid_company) {
		// 初始化返回类型
		Type type = AnalysisManager.GSON ? new TypeToken<List<ZB_bid_company>>() {
		}.getType()
				: ZB_bid_company.class;
		// 设置调用参数，调用远程服务
		return new RemoteService().setParams(SERVICE_NAME, "addZBBid",
				ZB_bid_company).call(manager, type, Operation.ADD);
	}

	/**
	 * 修改邀标单位
	 * 
	 * @param ZB_inviteJSON
	 * @return
	 */
	public AsyncTaskPM360 updateZBInvite(final DataManagerInterface manager,
			ZB_invite ZB_invite) {
		// 设置调用参数，调用远程服务
		return new RemoteService().setParams(SERVICE_NAME, "updateZBInvite",
				ZB_invite).call(manager, Operation.MODIFY);
	}

	/**
	 * 修改投标单位
	 * 
	 * @param ZB_bid_companyJSON
	 * @return
	 */
	public AsyncTaskPM360 updateZBBid(final DataManagerInterface manager,
			ZB_bid_company ZB_bid_company) {
		// 设置调用参数，调用远程服务
		return new RemoteService().setParams(SERVICE_NAME, "updateZBBid",
				ZB_bid_company).call(manager, Operation.MODIFY);
	}

	/**
	 * 预审
	 * 
	 * @param ZB_pretrialJSON
	 * @return
	 */
	public AsyncTaskPM360 zbPretrial(final DataManagerInterface manager,
			ZB_pretrial ZB_pretrial) {
		// 设置调用参数，调用远程服务
		return new RemoteService().setParams(SERVICE_NAME, "zbPretrial",
				ZB_pretrial).call(manager, Operation.MODIFY);
	}

	/**
	 * 澄清答疑
	 * 
	 * @param ZB_answerJSON
	 * @return
	 */
	public AsyncTaskPM360 zbAnswer(final DataManagerInterface manager,
			ZB_answer ZB_answer) {
		// 设置调用参数，调用远程服务
		return new RemoteService().setParams(SERVICE_NAME, "zbAnswer",
				ZB_answer).call(manager, Operation.MODIFY);
	}

	/**
	 * 报价
	 * 
	 * @param ZB_quoteJSON
	 * @return
	 */
	public AsyncTaskPM360 zbQuote(final DataManagerInterface manager,
			ZB_quote ZB_quote) {
		// 设置调用参数，调用远程服务
		return new RemoteService().setParams(SERVICE_NAME, "zbQuote", ZB_quote)
				.call(manager, Operation.MODIFY);
	}

	/**
	 * 评分
	 * 
	 * @param ZB_scoreJSON
	 * @return
	 */
	public AsyncTaskPM360 zbScore(final DataManagerInterface manager,
			ZB_score ZB_score) {
		// 设置调用参数，调用远程服务
		return new RemoteService().setParams(SERVICE_NAME, "zbScore", ZB_score)
				.call(manager, Operation.MODIFY);
	}

	/**
	 * 删除邀标单位
	 * 
	 * @param zb_invite_id
	 * @return
	 */
	public AsyncTaskPM360 deleteZBInvite(final DataManagerInterface manager,
			int zb_invite_id) {
		// 设置调用参数，调用远程服务
		return new RemoteService().setParams(SERVICE_NAME, "deleteZBInvite",
				zb_invite_id).call(manager, Operation.DELETE);
	}

	/**
	 * 删除投标单位
	 * 
	 * @param zb_bid_company_id
	 * @return
	 */
	public AsyncTaskPM360 deleteZBBid(final DataManagerInterface manager,
			int zb_bid_company_id) {
		// 设置调用参数，调用远程服务
		return new RemoteService().setParams(SERVICE_NAME, "deleteZBBid",
				zb_bid_company_id).call(manager, Operation.DELETE);
	}
}
