package com.pm360.cepm360.app.module.combination;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.pm360.cepm360.app.module.common.plan.CommonSubFeedbackFragment;
import com.pm360.cepm360.entity.Task;
import com.pm360.cepm360.entity.ZH_group_feedback;

public class SubFeedbackFragment extends CommonSubFeedbackFragment<ZH_group_feedback, Task> {

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {
		return super.onCreateView(inflater, container, savedInstanceState);
	}
	
	@Override
	protected ZH_group_feedback getCommonBean() {
		return new ZH_group_feedback();
	}

	@Override
	protected int getType() {
		return 1;
	}

}

