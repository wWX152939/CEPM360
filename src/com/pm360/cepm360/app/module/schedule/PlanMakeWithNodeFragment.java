package com.pm360.cepm360.app.module.schedule;

import java.util.ArrayList;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.pm360.cepm360.R;
import com.pm360.cepm360.app.cache.ProjectCache;
import com.pm360.cepm360.app.common.custinterface.TaskRelevanceChildInterface;
import com.pm360.cepm360.app.module.combination.CombinationView;
import com.pm360.cepm360.app.module.common.plan.CommonDocumentFragment;
import com.pm360.cepm360.app.module.common.plan.CommonMakeFragment;
import com.pm360.cepm360.entity.ZH_group;
import com.pm360.cepm360.entity.ZH_group_task;

public class PlanMakeWithNodeFragment extends
		CommonMakeFragment<ZH_group_task> {

	private com.pm360.cepm360.app.module.combination.SubCommonFragment mSubCommonFragment = new com.pm360.cepm360.app.module.combination.SubCommonFragment();
	private CommonDocumentFragment<ZH_group_task> mReferenceFragment = new CommonDocumentFragment<ZH_group_task>(1);

	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {
		return super.onCreateView(inflater, container, savedInstanceState);
	}
	
	@Override
	protected Class<ZH_group_task> getPlanClass() {
		return ZH_group_task.class;
	}
	
	@Override
	protected boolean isPlanMakePermission() {
		return true;
	}
	
	@SuppressWarnings("rawtypes")
	@Override
	protected void initMsgData(Bundle bundle) {

		mFragments = new ArrayList<TaskRelevanceChildInterface>();
		mSubCommonFragment.setTaskPermission(true);
		mReferenceFragment.setTaskPermission(true);
		
		Bundle data = getArguments();//获得从activity中传递过来的值
		mMsgGroupData = (ZH_group) data.get(CombinationView.GROUP_KEY);
		mProject = ProjectCache.findProjectById(mMsgGroupData.getProject_id());

		mStringArray = getResources()
				.getStringArray(R.array.task_make_viewpager);
		
		mFragments.add(mSubCommonFragment);
		mFragments.add(mReferenceFragment);

	}

	@Override
	protected boolean needSelectProject() {
		return false;
	}
	
}
