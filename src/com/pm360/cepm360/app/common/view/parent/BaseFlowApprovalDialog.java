package com.pm360.cepm360.app.common.view.parent;

import android.app.Activity;
import android.view.View;

import com.pm360.cepm360.R;

public class BaseFlowApprovalDialog extends BaseDialogStyle {

	public BaseFlowApprovalDialog(Activity activity) {
		super(activity);
	}
	
	@Override
	protected View initLayout() {
		return mActivity.getLayoutInflater().inflate(
				R.layout.base_flowapproval_dialog_listview, null);
	}
	
}
