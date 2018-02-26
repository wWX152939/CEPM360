package com.pm360.cepm360.app.module.invitebid;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.pm360.cepm360.app.common.view.parent.BaseListRelevanceFragment;
import com.pm360.cepm360.entity.ZB_flow;
import com.pm360.cepm360.entity.ZB_worklog;

public class BidWinningNoticeFragment extends BaseListRelevanceFragment<ZB_worklog, ZB_flow> {

	/**
	 * 创建视图
	 */
	@Override
	public View onCreateView(final LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {
		setPermissionIdentity(null, null);
		return super.onCreateView(inflater, container, savedInstanceState);
	}
	
}

