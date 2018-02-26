package com.pm360.cepm360.app.module.schedule;

import android.annotation.SuppressLint;
import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.support.v4.app.Fragment;
import android.widget.Toast;

import com.pm360.cepm360.R;
import com.pm360.cepm360.app.cache.ProjectCache;
import com.pm360.cepm360.app.common.activity.DataManagerInterface;
import com.pm360.cepm360.app.module.combination.CombinationView;
import com.pm360.cepm360.app.utils.UtilTools;
import com.pm360.cepm360.common.GLOBAL;
import com.pm360.cepm360.common.ResultStatus;
import com.pm360.cepm360.common.json.AnalysisManager;
import com.pm360.cepm360.entity.Message;
import com.pm360.cepm360.entity.ZH_group;
import com.pm360.cepm360.entity.ZH_group_task;
import com.pm360.cepm360.services.common.RemoteCommonService;
import com.pm360.cepm360.services.group.RemoteTaskService;

import java.util.List;

public class CombinationScheduleActivity extends BasePlanActivity {

	private ProgressDialog mProgressDialog;
	private ZH_group_task mSelectTask;
	private Message mMessage;
	
	private ZH_group mMsgGroupData;
	public void onCreate(Bundle savedInstanceState) {
		init(mFragmentManagerInterface, mMessgeManagerInterface, false);
		super.onCreate(savedInstanceState);
	}
	
	/**
	 * 针对质量，安全等需要获取该数据
	 * @return
	 */
	public ZH_group getGroupData() {
		return mMsgGroupData;
	}
	
	@Override
	public void setActionBarTitle(String title) {
//		title = "[" + title + "] ";
		if (mMsgGroupData != null) {
			title += "  --  " + mMsgGroupData.getNode_name();
		}
		
		super.setActionBarTitle(title);
	}
	
	private MessgeManagerInterface mMessgeManagerInterface = new MessgeManagerInterface() {

		@Override
		public boolean getIntentInfo() {
			Intent intent = getIntent();
            mBundleData = new Bundle();
	        if (intent.getSerializableExtra(CombinationView.GROUP_KEY) != null) {
	            mMsgGroupData = (ZH_group) intent
	                    .getSerializableExtra(CombinationView.GROUP_KEY);
	            mBundleData.putSerializable(CombinationView.GROUP_KEY, mMsgGroupData);
	            setActionBarTitle(ProjectCache.getProjectIdMaps()
	                    .get(String.valueOf(mMsgGroupData.getProject_id())));
	        }
	        
			String action = intent.getAction();

			if (action != null) {
				if (action.equals(GLOBAL.MSG_TASK_ACTION)
						|| action.equals(GLOBAL.MSG_FEEDBACK_ACTION)) {
					mMessage = (Message) intent
							.getSerializableExtra("message");
					if (mMessage != null) {
			        	mBundleData.putInt("type", mMessage.getType());
				        
						initMsgTaskData(mMessage);
						mProgressDialog = UtilTools.showProgressDialog(CombinationScheduleActivity.this, false, false);
						return false;
					}
				}
			}
			
			return true;
		}
		
	};
	
	private FragmentManagerInterface mFragmentManagerInterface = new FragmentManagerInterface() {

		@Override
		public Class<? extends Fragment>[] getManagerFragments() {
			@SuppressWarnings("unchecked")
			Class<? extends Fragment>[] fragmentClass = new Class[2];
			fragmentClass[0] = PlanMakeWithNodeFragment.class;
			fragmentClass[1] = TaskFeedbackWithNodeFragment.class;
//			fragmentClass[2] = TaskSharedWithNodeFragment.class;
			return fragmentClass;
		}

		@Override
		public Class<?>[] getSearchObjectClasses() {
			return null;
		}

		@Override
		public String getHomeTitleName() {
			return mMsgGroupData == null ? "" : mMsgGroupData.getNode_name();
		}

		@Override
		public int getNavigationTitleNamesId() {
			return R.array.schedule_navigation_names;
		}

		@Override
		public int getNavigationIconsId() {
			return R.array.schedule_navigation_ids;
		}
		
	};
	
	private void loadProjectId(Message messages) {
		RemoteCommonService.getInstance().getGroupIDBYTaskID(mZhInterface, messages);
	}
	
	private void initMsgTaskData(Message msg) {
		mSelectTask = new ZH_group_task();
    	mSelectTask.setTask_id(msg.getType_id());
        mBundleData.putSerializable("taskId", mSelectTask.getTask_id());
        
    	RemoteTaskService.getInstance().setTask(mSelectTask);
    	loadProjectId(msg);
	}
	
	@SuppressLint("HandlerLeak") 
	public Handler mServerToastHandler = new Handler() {
		public void handleMessage(android.os.Message msg) {

			Toast.makeText(getBaseContext(), msg.getData().getString("key"),
					Toast.LENGTH_SHORT).show();

		}
	};
	
	private DataManagerInterface mZhInterface = new DataManagerInterface() {

		@Override
		public void getDataOnResult(ResultStatus status, List<?> list) {
			if (status.getCode() == AnalysisManager.SUCCESS_DB_QUERY) {
				if (list != null && list.size() != 0) {
					mMsgGroupData = (ZH_group)list.get(0);
					setActionBarTitle(ProjectCache.getProjectIdMaps()
		                    .get(String.valueOf(mMsgGroupData.getProject_id())));
					mBundleData.putSerializable(CombinationView.GROUP_KEY, mMsgGroupData);
					
					if (mMessage == null) {
						switchContent(0);
					} else {
						switchContent(1);
					}
				}
			} else {
					android.os.Message msg = new android.os.Message();
					Bundle bundle = new Bundle();
					bundle.putString("key", status.getMessage());
					msg.setData(bundle);
					mServerToastHandler.sendMessage(msg);
			}
			mProgressDialog.dismiss();
		}
	};

}
