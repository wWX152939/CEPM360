package com.pm360.cepm360.app.module.combination;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.pm360.cepm360.app.module.common.plan.CommonSubCommonFragment;
import com.pm360.cepm360.entity.Task;

public class SubCommonFragment extends CommonSubCommonFragment<Task, Task> {

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {
		return super.onCreateView(inflater, container, savedInstanceState);
	}

	@Override
	protected int getType() {
		return 1;
	}

}

