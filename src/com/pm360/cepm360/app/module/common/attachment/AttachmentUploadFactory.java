package com.pm360.cepm360.app.module.common.attachment;

import com.pm360.cepm360.app.common.activity.DataManagerInterface;
import com.pm360.cepm360.entity.AttachCell;
import com.pm360.cepm360.entity.Quality;
import com.pm360.cepm360.entity.Safety;
import com.pm360.cepm360.entity.WorkLog;
import com.pm360.cepm360.entity.ZB_answer;
import com.pm360.cepm360.entity.ZB_bid_company;
import com.pm360.cepm360.entity.ZB_invite;
import com.pm360.cepm360.entity.ZB_plan;
import com.pm360.cepm360.entity.ZB_pretrial;
import com.pm360.cepm360.entity.ZB_quote;
import com.pm360.cepm360.entity.ZB_worklog;
import com.pm360.cepm360.entity.ZH_group_risk;
import com.pm360.cepm360.entity.ZH_group_worklog;
import com.pm360.cepm360.services.group.RemoteRiskService;
import com.pm360.cepm360.services.group.RemoteWorkLogService;
import com.pm360.cepm360.services.invitebid.RemoteZBPlanService;
import com.pm360.cepm360.services.invitebid.RemoteZBProcessService;
import com.pm360.cepm360.services.plan.RemoteQualityService;
import com.pm360.cepm360.services.plan.RemoteSafetyService;



public class AttachmentUploadFactory<T extends AttachCell> {
	private boolean isFindStatus;
	private void handleEvent(DataManagerInterface manager, T t, int type) {
		switch (type) {
		case 11:
			// 组合工作日志附件
			RemoteWorkLogService.getInstance().updateWorkLog(manager, (ZH_group_worklog) t);
			break;
		case 12:
			// 组合风险识别附件
			RemoteRiskService.getInstance().updateRisk(manager, (ZH_group_risk) t);
			break;
		case 14:
			// 工作日志
			com.pm360.cepm360.services.plan.RemoteWorkLogService.getInstance().updateWorkLog(manager, (WorkLog) t);
			break;
		case 16:
			// 安全监督
			RemoteSafetyService.getInstance().updateSafety(manager, (Safety) t);
			break;
		case 17:
			// 质量文明
			RemoteQualityService.getInstance().updateQuality(manager, (Quality) t);
			break;
		case 19:
			// 招标工作日志附件
			com.pm360.cepm360.services.invitebid.RemoteWorkLogService.getInstance().updateWorkLog(manager, (ZB_worklog) t);
			break;
		case 20:
			// 邀标附件
			RemoteZBProcessService.getInstance().updateZBInvite(manager, (ZB_invite) t);
			break;
		case 21:
			// 投标附件
			RemoteZBProcessService.getInstance().updateZBBid(manager, (ZB_bid_company) t);
			break;
		case 22:
			// 预审附件
			RemoteZBProcessService.getInstance().zbPretrial(manager, (ZB_pretrial) t);
			break;
		case 23:
			// 澄清答疑附件
			RemoteZBProcessService.getInstance().zbAnswer(manager, (ZB_answer) t);
			break;
		case 24:
			// 报价附件
			RemoteZBProcessService.getInstance().zbQuote(manager, (ZB_quote) t);
			break;
		case 25:
			// 评分附件
			RemoteZBProcessService.getInstance().zbQuote(manager, (ZB_quote) t);
			break;
		case 26:
			// 招标计划附件
			RemoteZBPlanService.getInstance().updateZBPlan(manager, (ZB_plan) t);
			break;
		case 29:
			// 风控经验
//			RemoteRiskKPIService.getInstance().
			break;
		default:
			isFindStatus = false;
			break;
		}
	}
	
	public AttachmentUploadInterface<T> createAttachment(final int type) {
		AttachmentUploadInterface<T> attachmentInterface = null;
		isFindStatus = true;
		attachmentInterface = new AttachmentUploadInterface<T>() {

			@Override
			public void updateParentBean(DataManagerInterface manager, T t) {
				handleEvent(manager, t, type);
			}
		};
		return attachmentInterface;
	}
	
	public boolean getFindStatus() {
		return isFindStatus;
	}


}
