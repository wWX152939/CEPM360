package com.pm360.cepm360.app.module.template;

import android.os.Bundle;
import android.support.v4.app.Fragment;

import com.pm360.cepm360.R;
import com.pm360.cepm360.app.common.view.parent.BaseSlidingPaneActivity;

public class TemplateActivity extends BaseSlidingPaneActivity {

    FragmentManagerInterface fragmentManagerInterface = new FragmentManagerInterface() {
		
		@Override
		public Class<?>[] getSearchObjectClasses() {
			return null;
		}
		
		@Override
		public int getNavigationTitleNamesId() {
			return R.array.template_navigation_titles;
		}
		
		@Override
		public int getNavigationIconsId() {
			return R.array.template_navigation_ids;
		}
		
		@SuppressWarnings("unchecked")
		@Override
		public Class<? extends Fragment>[] getManagerFragments() {
			return new Class[] { PlanTemplateFragment.class, DocTemplateFragment.class };
		}
		
		@Override
		public String getHomeTitleName() {
			return "";
		}
	};

    @Override
    protected void onCreate(Bundle savedInstanceState) {
    	init(fragmentManagerInterface, true);
        super.onCreate(savedInstanceState);
    }

}
