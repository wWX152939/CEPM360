package com.pm360.cepm360.app.module.schedule;

import android.content.Intent;
import android.os.Bundle;

import com.pm360.cepm360.app.cache.ProjectCache;
import com.pm360.cepm360.app.common.activity.ActionBarFragmentActivity;
import com.pm360.cepm360.app.module.combination.CombinationView;

public class CombinationActivity extends ActionBarFragmentActivity {

	private CombinationView mCombinationView;
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		mCombinationView = new CombinationView(this, CombinationView.TYPE_PLAN);
		setContentView(mCombinationView.getContentView());
		enableMenuView(false);
		mCombinationView.setProject(ProjectCache.getCurrentProject());
	}

	@Override
	public void onBackPressed() {
		if (mCombinationView.doBackPressed()) {
			super.onBackPressed();
		}
	}
	
	@Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
		mCombinationView.doActivityResult(requestCode, resultCode, data);
		super.onActivityResult(requestCode, resultCode, data);
	}

}
