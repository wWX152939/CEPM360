package com.pm360.cepm360.app.module.invitebid;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentTransaction;

import com.pm360.cepm360.R;
import com.pm360.cepm360.app.common.activity.MutilFragmentActivity;

public class BidActivity extends MutilFragmentActivity {
	
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.bid_common_fragment_layout);
		instantFragment();
	}


	private void instantFragment() {

		FragmentTransaction fragmentTransaction = getSupportFragmentManager()
				.beginTransaction();

		Fragment showFragment = Fragment.instantiate(this,
					 BidFragment.class.getCanonicalName());

		fragmentTransaction.add(R.id.content_frame, showFragment, "");
		
		fragmentTransaction.commitAllowingStateLoss();

	}

}
