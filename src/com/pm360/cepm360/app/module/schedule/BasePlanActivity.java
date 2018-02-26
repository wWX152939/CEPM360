package com.pm360.cepm360.app.module.schedule;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;

import com.pm360.cepm360.app.common.activity.ActionBarFragmentActivity;
import com.pm360.cepm360.app.common.view.parent.BaseSlidingPaneActivity;

public class BasePlanActivity extends BaseSlidingPaneActivity {

	public boolean gIsFirstSelectProject = false;
	public boolean gIsPublish = false;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		gIsFirstSelectProject = true;
	}
	
	@Override
	public void onActivityResult(int requestCode, int resultCode, Intent data) {
		super.onActivityResult(requestCode, resultCode, data);
		if (resultCode != Activity.RESULT_OK) {
			return;
		}
		if (requestCode == ActionBarFragmentActivity.PROJECT_SELECT_RESULT) {
			gIsFirstSelectProject = false;
		}
	}
}
