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
import com.pm360.cepm360.common.GLOBAL;
import com.pm360.cepm360.common.ResultStatus;
import com.pm360.cepm360.common.json.AnalysisManager;
import com.pm360.cepm360.entity.Message;
import com.pm360.cepm360.entity.Project;
import com.pm360.cepm360.entity.Task;
import com.pm360.cepm360.services.common.RemoteCommonService;
import com.pm360.cepm360.services.plan.RemoteTaskService;

import java.util.ArrayList;
import java.util.List;

public class ScheduleActivity extends BasePlanActivity {
	
	public static final String PROJECT_KEY = "project_key";

	private ProgressDialog mProgressDialog;
	
	@Override
	protected List<String> getPermission() {
		List<String> list = new ArrayList<String>();
		list.add(GLOBAL.SYS_ACTION[3][0]);
		list.add(GLOBAL.SYS_ACTION[2][0]);
		list.add("2_4");
		list.add("2_3");
		return list;
	}
	
	/**
	 *  set task cache to null
	 */
	private void initDefaultData() {
		RemoteTaskService.getInstance().setTask(null);
	}
	
	/**
	 * the msg comes from message center
	 * @param flowFlag
	 * @param msg
	 */
	private void initMsgTaskData(int flowFlag, Message msg) {
		Task task = new Task();
		task.setTask_id(msg.getTask_id());
    	RemoteTaskService.getInstance().setTask(task);
    	mBundleData.putInt("taskId", task.getTask_id());
    	mBundleData.putInt("type", msg.getType());
    	loadProjectId(msg);
	}
	
	private void createProgressDialog() {
		mProgressDialog = new ProgressDialog(this);
        mProgressDialog.setIndeterminate(true);
        mProgressDialog.setCancelable(true);
	}
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		initDefaultData();

		createProgressDialog();
		
		init(mFragmentManagerInterface, mMessgeManagerInterface, true);
		super.onCreate(savedInstanceState);

	}
	
	private MessgeManagerInterface mMessgeManagerInterface = new MessgeManagerInterface() {

		@Override
		public boolean getIntentInfo() {
			boolean startNormalFlag = false;
			Intent intent = getIntent();
			String action = intent.getAction();
			mBundleData = new Bundle();
			final Message msg = (Message) intent
					.getSerializableExtra("message");
			
			if (action != null) {
				if (action.equals(GLOBAL.MSG_TASK_ACTION)
						|| action.equals(GLOBAL.MSG_FEEDBACK_ACTION)
						|| action.equals(GLOBAL.MSG_FEEDBACK_DOC_ACTION) ) {
					if (msg != null) {
						mProgressDialog.show();
						mProgressDialog.setContentView(R.layout.layout_progress);
						initMsgTaskData(1, msg);
					}
				} else {
					setActionBarTitle(ProjectCache.getCurrentProject().getName());
					mBundleData.putSerializable(PROJECT_KEY, ProjectCache.getCurrentProject());
					startNormalFlag = true;
				}
			} else {
				setActionBarTitle(ProjectCache.getCurrentProject().getName());
				mBundleData.putSerializable(PROJECT_KEY, ProjectCache.getCurrentProject());
				startNormalFlag = true;
			}
			
			return startNormalFlag;
		}
		
	};

	private FragmentManagerInterface mFragmentManagerInterface = new FragmentManagerInterface() {

		@Override
		public Class<? extends Fragment>[] getManagerFragments() {
			@SuppressWarnings("unchecked")
			Class<? extends Fragment>[] fragmentClass = new Class[2];
			fragmentClass[0] = PlanMakeFragment.class;
			fragmentClass[1] = TaskFeedbackFragment.class;
			return fragmentClass;
		}

		@Override
		public Class<?>[] getSearchObjectClasses() {
			return null;
		}

		@Override
		public String getHomeTitleName() {
			return getString(R.string.schedule_navigation_title);
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
	
	@SuppressLint("HandlerLeak") 
	public Handler mServerToastHandler = new Handler() {
		public void handleMessage(android.os.Message msg) {

			Toast.makeText(getBaseContext(), msg.getData().getString("key"),
					Toast.LENGTH_SHORT).show();

		}
	};

	private DataManagerInterface projectInterface = new DataManagerInterface() {

		@Override
		public void getDataOnResult(ResultStatus status, List<?> list) {
			mProgressDialog.dismiss();
			if (status.getCode() == AnalysisManager.SUCCESS_DB_QUERY) {
				if (list != null && list.size() != 0) {
					Project project = (Project) list.get(0);
					setActionBarTitle(project.getName());
			    	mBundleData.putSerializable(PROJECT_KEY, project);
			    	switchContent(1);
				} else {
					android.os.Message msg = new android.os.Message();
					Bundle bundle = new Bundle();
					bundle.putString("key",   getString(R.string.task_not_exist));
					msg.setData(bundle);
					mServerToastHandler.sendMessage(msg);
				}
				
			} else {
				android.os.Message msg = new android.os.Message();
				Bundle bundle = new Bundle();
				bundle.putString("key", status.getMessage());
				msg.setData(bundle);
				mServerToastHandler.sendMessage(msg);
			}
		}
	};

	public void loadProjectId(Message messages) {
		RemoteCommonService.getInstance().getProjectIDBYTaskID(
				projectInterface, messages);
	}

}
