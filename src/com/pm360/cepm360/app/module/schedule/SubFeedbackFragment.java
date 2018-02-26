package com.pm360.cepm360.app.module.schedule;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.pm360.cepm360.app.module.common.plan.CommonSubFeedbackFragment;
import com.pm360.cepm360.entity.Feedback;
import com.pm360.cepm360.entity.Task;

public class SubFeedbackFragment extends CommonSubFeedbackFragment<Feedback, Task> {

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {
		return super.onCreateView(inflater, container, savedInstanceState);
	}
	
	@Override
	protected Feedback getCommonBean() {
		return new Feedback();
	}

	@Override
	protected int getType() {
		return 2;
	}

}

