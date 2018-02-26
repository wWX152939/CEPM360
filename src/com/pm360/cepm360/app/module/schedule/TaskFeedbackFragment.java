package com.pm360.cepm360.app.module.schedule;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.pm360.cepm360.R;
import com.pm360.cepm360.app.cache.ProjectCache;
import com.pm360.cepm360.app.common.custinterface.TaskRelevanceChildInterface;
import com.pm360.cepm360.app.module.common.plan.CommonDocumentFragment;
import com.pm360.cepm360.app.module.common.plan.CommonFeedbackFragment;
import com.pm360.cepm360.entity.Feedback;
import com.pm360.cepm360.entity.Project;
import com.pm360.cepm360.entity.Task;

import java.util.ArrayList;

public class TaskFeedbackFragment extends
	CommonFeedbackFragment<Task, Feedback> {
	private SubFeedbackFragment mFeedbackFragment = new SubFeedbackFragment();
	private CommonDocumentFragment<Task> mDocumentFragment = new CommonDocumentFragment<Task>(4);
	private WorkLogFragment mWorkLogFragment = new WorkLogFragment();
	private SafetyFragment<Task> mSecureFragment = new SafetyFragment<Task>();
	private QualityFragment<Task> mQualityFragment = new QualityFragment<Task>();

	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {
		return super.onCreateView(inflater, container, savedInstanceState);
	}
	
	@Override
	protected int getFlag() {
		return 2;
	}
	
	@SuppressWarnings("rawtypes")
	@Override
	protected void initMsgData(Bundle bundle) {
		mStringArray = getResources().getStringArray(R.array.task_feedback_tab_names);
		mFragments = new ArrayList<TaskRelevanceChildInterface>();
		mFragments.add(mFeedbackFragment);
		mFragments.add(mDocumentFragment);
		mFragments.add(mWorkLogFragment);
		mFragments.add(mSecureFragment);
		mFragments.add(mQualityFragment);
		mProject = (Project) bundle.get(ScheduleActivity.PROJECT_KEY);
	}

	@Override
	protected Feedback getUpdateFeedback() {
		return new Feedback();
	}
	
	@Override
	public void onHiddenChanged(boolean hidden) {
		if (mHasViewPermission || mHasEditPermission) {
			if (!hidden) {
				if (!mActivity.gIsFirstSelectProject) {
					Project project = ProjectCache.getCurrentProject();
					if (mProject.getProject_id() != project.getProject_id()) {
						mActivity.setActionBarTitle(project.getName());
						
						mProject = project;
						mCurrentItem = null;
						mListAdapter.clearAll();
						serviceInterface.getListData();
						handleChildEvent();
					} else {
						if (mActivity.gIsPublish) {
							mActivity.gIsPublish = false;
							serviceInterface.getListData();
						}
					}
				} else {
					if (mActivity.gIsPublish) {
						mActivity.gIsPublish = false;
						serviceInterface.getListData();
					}
				}
			}
		}
		super.onHiddenChanged(hidden);
	}

}
