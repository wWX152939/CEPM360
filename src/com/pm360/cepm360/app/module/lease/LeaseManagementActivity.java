package com.pm360.cepm360.app.module.lease;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.View;

import com.pm360.cepm360.R;
import com.pm360.cepm360.app.common.view.parent.BaseSlidingPaneActivity;

public class LeaseManagementActivity extends BaseSlidingPaneActivity {
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		init(mFragmentManagerInterface, false);
		super.onCreate(savedInstanceState);
	}

	FragmentManagerInterface mFragmentManagerInterface = new FragmentManagerInterface() {

		@Override
		public Class<?>[] getSearchObjectClasses() {
			return null;
		}

		@Override
		public int getNavigationTitleNamesId() {
			return R.array.lease_navigation_titles;
		}

		@Override
		public int getNavigationIconsId() {
			return R.array.lease_navigation_icon;
		}

		@SuppressWarnings("unchecked")
		@Override
		public Class<? extends Fragment>[] getManagerFragments() {
			return new Class[] { LeaseListsFragment.class,
				RentListsFragment.class };
		}

		@Override
		public String getHomeTitleName() {
			return getString(R.string.setting_title);
		}
	};

	public View getSettingsActivitySlidingPaneLayout() {
		return mSlidingPane;
	}
}
