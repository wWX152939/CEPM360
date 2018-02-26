
package com.pm360.cepm360.app.module.announcement;

import android.os.Bundle;
import android.support.v4.app.FragmentTransaction;

import com.pm360.cepm360.R;
import com.pm360.cepm360.app.common.activity.ActionBarFragmentActivity;
import com.pm360.cepm360.app.module.announcement.AnnouncementFragment;

public class AnnouncementActivity extends ActionBarFragmentActivity {

    private android.support.v4.app.FragmentManager mFragmentManager;
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.announcement_activity);
        mFragmentManager = getSupportFragmentManager();
        FragmentTransaction fragmentTransaction = mFragmentManager
                .beginTransaction();
        
        AnnouncementFragment announcementFragment = new AnnouncementFragment();
        fragmentTransaction.add(R.id.fragment_layout, announcementFragment);
        fragmentTransaction.show(announcementFragment);
        fragmentTransaction.commitAllowingStateLoss();        
	}
}
