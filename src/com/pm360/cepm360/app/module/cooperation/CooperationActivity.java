package com.pm360.cepm360.app.module.cooperation;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.View;

import com.pm360.cepm360.R;
import com.pm360.cepm360.app.common.view.parent.BaseSlidingPaneActivity;
import com.pm360.cepm360.common.GLOBAL;
import com.pm360.cepm360.entity.Message;

@SuppressLint("UseSparseArrays")
public class CooperationActivity extends BaseSlidingPaneActivity {
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		init(mFragmentManagerInterface, false);
		super.onCreate(savedInstanceState);
	}

	/**
	 * 读取intent信息
	 * @return 位置index
	 */
	protected int getIntentInfo() {
		int ret = 0;
		Intent intent = getIntent();
		String action = intent.getAction();
		mMessage = (Message) intent
				.getSerializableExtra(GLOBAL.MSG_OBJECT_KEY);
		
		if (action != null) {
			if (action.equals(GLOBAL.MSG_COOPERATION_ACTION)) {
				ret = 1;
			}
		}
		return ret;
	}
	
	FragmentManagerInterface mFragmentManagerInterface = new FragmentManagerInterface() {

		@Override
		public Class<?>[] getSearchObjectClasses() {
			return null;
		}

		@Override
		public int getNavigationTitleNamesId() {
			return R.array.cooperation_navigation_titles;
		}

		@Override
		public int getNavigationIconsId() {
			return R.array.cooperation_navigation_icons;
		}

		@SuppressWarnings("unchecked")
		@Override
		public Class<? extends Fragment>[] getManagerFragments() {
			return new Class[] { CooperationInitiator.class,
					CooperationReceiver.class };
		}

		@Override
		public String getHomeTitleName() {
			return getString(R.string.cooperation_management);
		}
	};

	public View getSettingsActivitySlidingPaneLayout() {
		return mSlidingPane;
	}

}
