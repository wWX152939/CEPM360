package com.pm360.cepm360.app.module.schedule;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.pm360.cepm360.R;
import com.pm360.cepm360.app.cache.ProjectCache;
import com.pm360.cepm360.app.common.custinterface.TaskRelevanceChildInterface;
import com.pm360.cepm360.app.module.combination.CombinationView;
import com.pm360.cepm360.app.module.common.plan.CommonDocumentFragment;
import com.pm360.cepm360.app.module.common.plan.CommonFeedbackFragment;
import com.pm360.cepm360.entity.ZH_group;
import com.pm360.cepm360.entity.ZH_group_feedback;
import com.pm360.cepm360.entity.ZH_group_task;

import java.util.ArrayList;

public class TaskFeedbackWithNodeFragment extends
	CommonFeedbackFragment<ZH_group_task, ZH_group_feedback> {
	private com.pm360.cepm360.app.module.combination.SubFeedbackFragment mFeedbackFragment = new com.pm360.cepm360.app.module.combination.SubFeedbackFragment();
	private CommonDocumentFragment<ZH_group_task> mDocumentFragment = new CommonDocumentFragment<ZH_group_task>(CommonDocumentFragment.TYPE_SCHEDULE_FEEDBACK_DOC);
	private com.pm360.cepm360.app.module.combination.WorkLogFragment mWorkLogFragment = new com.pm360.cepm360.app.module.combination.WorkLogFragment();
	private SafetyFragment<ZH_group_task> mSecureFragment = new SafetyFragment<ZH_group_task>();
	private QualityFragment<ZH_group_task> mQualityFragment = new QualityFragment<ZH_group_task>();

	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {
		return super.onCreateView(inflater, container, savedInstanceState);
	}
	
	@Override
	protected int getFlag() {
		return 1;
	}
	
	@Override
	protected boolean isFeedbackPermission() {
		return true;
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
		mMsgGroupData = (ZH_group) bundle.get(CombinationView.GROUP_KEY);

		mProject = ProjectCache.findProjectById(mMsgGroupData.getProject_id());
	}

	@Override
	protected ZH_group_feedback getUpdateFeedback() {
		return new ZH_group_feedback();
	}
	
	@Override
	protected boolean needSelectProject() {
		return false;
	}
	
	
	@Override
	public void onHiddenChanged(boolean hidden) {
		if (!hidden) {
			if (mActivity.gIsPublish) {
				mActivity.gIsPublish = false;
				serviceInterface.getListData();
			}
		}
		super.onHiddenChanged(hidden);
	}

}
