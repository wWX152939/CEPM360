package com.pm360.cepm360.app.module.home.portal;

import android.app.Activity;
import android.content.ActivityNotFoundException;
import android.content.Context;
import android.content.Intent;
import android.widget.Toast;

import com.pm360.cepm360.R;
import com.pm360.cepm360.app.CepmApplication;
import com.pm360.cepm360.app.cache.UserCache;
import com.pm360.cepm360.app.common.activity.DataManagerInterface;
import com.pm360.cepm360.app.module.common.attachment.AttachmentViewActivity;
import com.pm360.cepm360.app.utils.IntentBuilder;
import com.pm360.cepm360.common.GLOBAL;
import com.pm360.cepm360.common.ResultStatus;
import com.pm360.cepm360.common.json.AnalysisManager;
import com.pm360.cepm360.entity.Announcement;
import com.pm360.cepm360.entity.Files;
import com.pm360.cepm360.entity.Index_daiban;
import com.pm360.cepm360.entity.Index_feedback;
import com.pm360.cepm360.entity.Index_task;
import com.pm360.cepm360.entity.Message;
import com.pm360.cepm360.services.document.RemoteDocumentsService;

import java.util.List;

public class IntentHelper {
    
    private static final String DOCUMENT_TEMPLATE = GLOBAL.FILE_SAVE_PATH + "/Default/download/";
    
    public static void openFile(Context context, Files files) {
        String localPath =  DOCUMENT_TEMPLATE + files.getFile_name();
        openFile(context, files, localPath);
    }
    
    public static void openFile(Context context, Files files, String localPath) {
        try {
            IntentBuilder.viewFile(context, files, localPath);
        } catch (ActivityNotFoundException e) {
            Toast.makeText(context, context.getResources().getString(
                            R.string.application_no_found), Toast.LENGTH_SHORT)
                    .show();
        }
    }
    
    /**
     * 根据文件Id，先下载文件对象，再打开
     * 
     * @param context
     * @param fileId
     */
    public static void downloadAndOpen(final Context context, int fileId) {
        RemoteDocumentsService.getInstance().getFilesByID(
                new DataManagerInterface() {

                    @Override
                    public void getDataOnResult(ResultStatus status,
                            List<?> list) {
                        if (status.getCode() == AnalysisManager.SUCCESS_DB_QUERY) {
                            if (list != null && list.size() > 0) {
                                Files files = (Files) list.get(0);
                                IntentHelper.openFile(context, files);
                            } else {
                                Toast.makeText(
                                        context,
                                        context.getResources().getString(
                                                R.string.download_failed),
                                        Toast.LENGTH_SHORT).show();
                            }
                        }
                    }

                }, fileId,
                UserCache.getCurrentUser().getTenant_id());
    }
    
    /**
     * 消息界面调用，activity跳转
     * @param context
     * @param msg 消息对象
     */
    public static void startActivity(Context context, Message msg) {
        Intent intent = new Intent();
        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        if (msg.getType() == 9 || msg.getType() == 10 || msg.getType() == 17
                || msg.getType() == 18 || msg.getType() == 19
                || msg.getType() == 20 || msg.getType() == 21) {
            // 最新反馈：(9,10,17,18,19,20,21)
            intent.setClass(context, AttachmentViewActivity.class);
        } else {
            intent.setAction(getAction(context, msg.getType()));
        }
        intent.putExtra(GLOBAL.MSG_OBJECT_KEY, msg);
        context.startActivity(intent);
    }
        
    /**
     * 首页-我的任务
     * 
     * @param context
     * @param index (1)
     * @param position (0, 1, 2)
     */
    public static void startActivity(Context context, int index, int position) {
        Intent intent = new Intent(Intent.ACTION_MAIN);
        intent.putExtra("index", index);
        intent.putExtra(MyTaskFragment.TASK_POSITION_KEY, position);
        intent.setClass(context, PortalMoreListActivity.class);
        context.startActivity(intent);
    }
    
    /**
     * 首页-其他跳转
     * 
     * @param context
     * @param index
     * @param object
     */
    public static void startActivity(Context context, int index, Object object) {
        if (index == 0) { // 代办事项  (1,2,3,4,5,6,7,13,14,22,23,24,32)
            Index_daiban bean = (Index_daiban) object;
            Message msg = new Message();
            msg.setMessage_id(bean.getMessage_id());
            msg.setType(bean.getMessage_type_key());
            msg.setType_id(bean.getType_id());
            msg.setTime(bean.getTime());
            
            Intent intent = new Intent();
            intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
            
            intent.setAction(getAction(context, bean.getMessage_type_key()));
            intent.putExtra("message", msg);
            context.startActivity(intent); 
        }
        
        if (index == 1) { // 我的任务 (8)(16)
            Index_task bean = (Index_task) object;
            Message msg = new Message();
            msg.setTask_id(bean.getTask_id());
            msg.setType(bean.getMessage_type_key());
            Intent intent = new Intent();
            intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
            intent.setAction(getAction(context, bean.getMessage_type_key()));
            intent.putExtra("message", msg);
            context.startActivity(intent);
        }
               
        if (index == 3) { // 最新反馈 (9,10,17,18,19,20,21)
            Intent intent = new Intent();
            intent.putExtra(AttachmentViewActivity.KEY_INDEX_FEEDBACK, (Index_feedback) object);
            intent.setClass(context, AttachmentViewActivity.class);
            context.startActivity(intent);
        }
        
        if (index == 4) { // 公司公告 (12)
            Announcement bean = (Announcement) object;           
            Intent intent = new Intent();
            intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
            intent.setAction(GLOBAL.MSG_ANNOUNCEMENT);
            intent.putExtra("announcement", bean);
            context.startActivity(intent);
        }
        
    }
    
    // 我的任务：(8,16)
    // 我的待办：(1,2,3,4,5,6,7,13,14,22,23,24)
    // 最新反馈：(9,10,17,18,19,20,21)
    // 最新文档：(11,25,26,27,28,29,30)
    // 往来函：(15)
    // 公告：(12)
    public static String getAction(Context context, int msgType) {
    	int enterprise = ((CepmApplication)((Activity) context).getApplication()).getEnterpriseType();
        String action = "";
        switch (msgType) {
        case 2:
        case 3:
        	action = GLOBAL.MSG_CONTRACT_PAYMENT_ACTION;
        	break;        
        case 4:
        case 5:
            action = GLOBAL.MSG_CONTRACT_CHANGE_ACTION;
            break;
        case 7:
            action = GLOBAL.MSG_SUBCONTRACT_MANAGEMENT_ACTION;
            break;
        case 8:
            action = GLOBAL.MSG_TASK_ACTION;
            break;
//        case 10:
//            action = GLOBAL.MSG_FEEDBACK_DOC_ACTION;
//            break;
        case 11: // 公共文档
        case 25: // 项目文档
        case 26: // 邮件附件附件
        case 27: // 现场图片附件
        case 28: // 形象成果附件
        case 29: // 安全监督附件
        case 30: // 质量文明附件
            action = GLOBAL.MSG_DOCUMENT_ACTION;
            break;
        case 12: // 公告
        	action = GLOBAL.MSG_ANNOUNCEMENT;
            break;
        case 13:
            action = GLOBAL.MSG_EXECUTIVE_ACTION;
            break;
        case 14:
            action = GLOBAL.MSG_SUBCONTRACT_MANAGEMENT_ACTION;
            break;
        case 16:
        	if (enterprise == 0) {
        		action = GLOBAL.MSG_ZHTASK_ACTION;
        	} else {
        		action = GLOBAL.MSG_TASK_ACTION;
        	}
            break;
//        case 17:
//        	if (enterprise == 0) {
//        		action = GLOBAL.MSG_ZHFEEDBACK_ACTION;
//        	} else {
//        		action = GLOBAL.MSG_FEEDBACK_ACTION;
//        	}
//            break;
//        case 9:  // 进度反馈
//        case 18: // 安全监督
//        case 19: // 质量文明
//        case 20: // 风险识别
//        case 21: // 形象成果
//            action = GLOBAL.MSG_FEEDBACK_ACTION;
//            break;
        case 22: // 采购计划-会签
            action = GLOBAL.MSG_PURCHASE_PLAN_ACTION;
            break;
        case 23: // 分包合同付款-会签
        case 24: // 采购合同付款-会签
            break;
        case 32: // 协作
        	action = GLOBAL.MSG_COOPERATION_ACTION;
        	break;
        case 33: // 任务共享
        	action = GLOBAL.MSG_TASK_SHARED_ACTION;
        	break;
        default:
            break;
        }
                
        return action;
    }
}
