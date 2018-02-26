package com.pm360.cepm360.app.module.schedule;

import java.util.ArrayList;

import android.graphics.Color;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.pm360.cepm360.R;
import com.pm360.cepm360.app.cache.ProjectCache;
import com.pm360.cepm360.app.common.activity.ListSelectActivity;
import com.pm360.cepm360.app.common.custinterface.TaskRelevanceChildInterface;
import com.pm360.cepm360.app.module.common.plan.CommonDocumentFragment;
import com.pm360.cepm360.app.module.common.plan.CommonMakeFragment;
import com.pm360.cepm360.entity.Project;
import com.pm360.cepm360.entity.Task;

public class PlanMakeFragment extends
		CommonMakeFragment<Task> {

	private SubCommonFragment mSubCommonFragment = new SubCommonFragment();
	private CommonDocumentFragment<Task> mReferenceFragment = new CommonDocumentFragment<Task>(2);

	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {
		return super.onCreateView(inflater, container, savedInstanceState);
	}
	
	@Override
	protected Class<Task> getPlanClass() {
		return Task.class;
	}
	
	@SuppressWarnings("rawtypes")
	@Override
	protected void initMsgData(Bundle bundle) {
		mStringArray = getResources().getStringArray(R.array.task_make_viewpager);
		mFragments = new ArrayList<TaskRelevanceChildInterface>();
		mFragments.add(mSubCommonFragment);
		mFragments.add(mReferenceFragment);
		
		mProject = (Project) bundle.get(ScheduleActivity.PROJECT_KEY);
		if (mProject == null) {
			mProject = (Project) bundle.get(ListSelectActivity.PROJECT_KEY);
		}
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
					}
				}
			}
		}
		
		super.onHiddenChanged(hidden);
	}
	

	@Override
	protected void doExtraEventWithViewPermission() {
		if (OperationMode.NORMAL == mCurrentMode) {
			super.doExtraEventWithViewPermission();
		} else {
			//mListHeader.setBackgroundResource(R.drawable.setting_list_title_background);
		    mListHeader.setBackgroundResource(R.color.content_listview_header_bg);
			mAttrButton = (ImageView)mListHeader.findViewById(R.id.attr_button);
			
			if (OperationMode.NORMAL != mCurrentMode) {
				mAttrButton.setVisibility(View.GONE);
			}
		}
	}
	
	@Override
	protected void doExtraEventWithEditPermission() {
		if (OperationMode.NORMAL == mCurrentMode) {
			super.doExtraEventWithEditPermission();
		}
	}
	
	@Override
	protected boolean needSelectProject() {
		if (OperationMode.NORMAL == mCurrentMode) {
			return super.needSelectProject();
		} else {
			return false;
		}
	}
	
	@Override
	protected void setHeaderTextColor(TextView tv) {
		if (OperationMode.NORMAL != mCurrentMode) {
			tv.setTextColor(Color.WHITE);
		}
	}
	
}
