
package com.pm360.cepm360.app.module.settings;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.View;

import com.pm360.cepm360.R;
import com.pm360.cepm360.app.common.view.parent.BaseSlidingPaneActivity;

public class SettingsActivity extends BaseSlidingPaneActivity {
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
			return R.array.settings_navigation_titles;
		}

		@Override
		public int getNavigationIconsId() {
			return R.array.settings_navigation_icons;
		}

		@SuppressWarnings("unchecked")
		@Override
		public Class<? extends Fragment>[] getManagerFragments() {
			return new Class[] { EPSMaintainFragment.class, OBSFragment.class, 
					RolePermissionFragment1.class, UserManageFragment.class, 
	                ApprovalProcessFragment.class, MoreSettingsFragment.class };
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
