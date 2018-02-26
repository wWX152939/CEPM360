package com.pm360.cepm360.app.module.combination;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.pm360.cepm360.R;
import com.pm360.cepm360.app.cache.ProjectCache;
import com.pm360.cepm360.app.common.custinterface.TaskRelevanceChildInterface;
import com.pm360.cepm360.app.module.common.plan.CommonMakeFragment;
import com.pm360.cepm360.entity.Project;
import com.pm360.cepm360.entity.ZH_group;
import com.pm360.cepm360.entity.ZH_group_task;

import java.util.ArrayList;

public class CombinationMakeFragment extends
		CommonMakeFragment<ZH_group_task> {

	private SubCommonFragment mSubCommonFragment = new SubCommonFragment();
	private RiskControlFragment mRiskControlFragment = new RiskControlFragment();

	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {
		return super.onCreateView(inflater, container, savedInstanceState);
	}
	
	@Override
	protected Class<ZH_group_task> getPlanClass() {
		return ZH_group_task.class;
	}
	
	@SuppressWarnings("rawtypes")
	@Override
	protected void initMsgData(Bundle bundle) {

		mFragments = new ArrayList<TaskRelevanceChildInterface>();
		
		Bundle data = getArguments();//获得从activity中传递过来的值
		mMsgGroupData = (ZH_group) data.get(CombinationView.GROUP_KEY);
		mProject = new Project();
		mProject = ProjectCache.findProjectById(mMsgGroupData.getProject_id());

		mStringArray = getResources()
				.getStringArray(R.array.zh_task_make_viewpager);
		
		mFragments.add(mSubCommonFragment);
		mFragments.add(mRiskControlFragment);

	}

	@Override
	protected boolean needSelectProject() {
		return false;
	}
}
