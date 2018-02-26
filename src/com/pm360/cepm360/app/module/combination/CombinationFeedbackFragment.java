package com.pm360.cepm360.app.module.combination;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.pm360.cepm360.app.cache.ProjectCache;
import com.pm360.cepm360.app.common.custinterface.TaskRelevanceChildInterface;
import com.pm360.cepm360.app.module.common.plan.CommonDocumentFragment;
import com.pm360.cepm360.app.module.common.plan.CommonFeedbackFragment;
import com.pm360.cepm360.common.GLOBAL;
import com.pm360.cepm360.entity.ZH_group;
import com.pm360.cepm360.entity.ZH_group_feedback;
import com.pm360.cepm360.entity.ZH_group_task;

import java.util.ArrayList;
import java.util.List;

public class CombinationFeedbackFragment extends
		CommonFeedbackFragment<ZH_group_task, ZH_group_feedback> {

	private List<String> mMsgPageData;
	private SubFeedbackFragment mSubFeedbackFragment = new SubFeedbackFragment();
	private WorkLogFragment mWorkLogFragment = new WorkLogFragment();
	private RiskFragment mRiskFragment = new RiskFragment();
	private CommonDocumentFragment<ZH_group_task> mDocumentFragment = new CommonDocumentFragment<ZH_group_task>(CommonDocumentFragment.TYPE_ZH_FEEDBACK_DOC);

	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {
		return super.onCreateView(inflater, container, savedInstanceState);
	}
	
	@Override
	protected int getFlag() {
		return 1;
	}
	
	@SuppressWarnings("rawtypes")
	@Override
	protected void initMsgData(Bundle bundle) {

		mMsgPageData = new ArrayList<String>();
		mFragments = new ArrayList<TaskRelevanceChildInterface>();
	
		mMsgGroupData = (ZH_group) bundle.get(CombinationView.GROUP_KEY);

		mProject = ProjectCache.findProjectById(mMsgGroupData.getProject_id());
		
		String[] msgArray = mMsgGroupData.getNode_module().split(",");
		for (int i = 0; i< msgArray.length; i++) {
			if (msgArray[i] != null && !msgArray[i].isEmpty()) {
				mMsgPageData.add(msgArray[i]);
			}
		}

		for (String name : mMsgPageData) {
			if (name.equals(GLOBAL.COMBINATION_NODE_TYPE[0][0])) {
				mFragments.add(mSubFeedbackFragment);
			} else if (name.equals(GLOBAL.COMBINATION_NODE_TYPE[1][0])) {
				mFragments.add(mWorkLogFragment);
			} else if (name.equals(GLOBAL.COMBINATION_NODE_TYPE[2][0])) {
				mFragments.add(mRiskFragment);
			} else if (name.equals(GLOBAL.COMBINATION_NODE_TYPE[3][0])) {
				mFragments.add(mDocumentFragment);
			}
		}
		
		mStringArray = new String[mMsgPageData.size()];
		for (int i = 0; i < mStringArray.length; i++) {
			mStringArray[i] = GLOBAL.COMBINATION_NODE_TYPE[Integer.parseInt(mMsgPageData.get(i)) - 1][1];
		}
		
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
