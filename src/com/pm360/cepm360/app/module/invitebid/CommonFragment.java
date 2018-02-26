package com.pm360.cepm360.app.module.invitebid;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.pm360.cepm360.R;
import com.pm360.cepm360.app.common.view.parent.BaseListRelevanceFragment;
import com.pm360.cepm360.common.GLOBAL;
import com.pm360.cepm360.entity.ZB_flow;

public class CommonFragment extends BaseListRelevanceFragment<ZB_flow, ZB_flow> {

	public final static String CURRENT_BEAN_KEY = "current_bean";
	private FragmentManager mFragmentManager;
	private String mCurrentTitle;
	
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {
		mFragmentManager = getChildFragmentManager();
		setPermissionIdentity(null, null);
		return super.onCreateView(inflater, container, savedInstanceState);
	} 

	protected void showNoViewPermission(  LayoutInflater inflater, 
										ViewGroup container) {
		mRootLayout = inflater.inflate( R.layout.bid_common_fragment_layout,
				container,
				false);
	}
	
	@SuppressWarnings({ "unchecked", "rawtypes" })
	public void switchContent(int position) {

		String title = getItem(position);
		if (title.equals(mCurrentTitle)) {
			BaseListRelevanceFragment showFragment = (BaseListRelevanceFragment) mFragmentManager.findFragmentByTag(title);
			showFragment.setCurrentParentBean(mParentBean);
			showFragment.handleParentEvent(mParentBean);
			return;
		}

		FragmentTransaction fragmentTransaction = mFragmentManager
				.beginTransaction();

		fragmentTransaction.setCustomAnimations(R.anim.slide_in_left,
				R.anim.slide_out_left, R.anim.slide_in_right,
				R.anim.slide_out_right);

		BaseListRelevanceFragment showFragment = (BaseListRelevanceFragment) mFragmentManager.findFragmentByTag(title);
		if (showFragment == null) {
			showFragment = (BaseListRelevanceFragment) BaseListRelevanceFragment.instantiate(getActivity(),
					getFragmentClass(position));
		}
		if (mCurrentTitle != null) {
			Fragment hideFragment = mFragmentManager
					.findFragmentByTag(mCurrentTitle);
			if (hideFragment != null)
				fragmentTransaction.hide(hideFragment);
		}

		if (showFragment.isAdded()) {
			fragmentTransaction.show(showFragment);
			showFragment.setCurrentParentBean(mParentBean);
			showFragment.handleParentEvent(mParentBean);
		} else {
			Bundle bundle = new Bundle();
			bundle.putSerializable(CURRENT_BEAN_KEY, mParentBean);
			showFragment.setArguments(bundle);
			fragmentTransaction.add(R.id.content_frame, showFragment, title);
		}
		fragmentTransaction.commitAllowingStateLoss();
		mCurrentTitle = title;

	}
	
	private String getItem(int position) {
		return GLOBAL.BID_FLOW_TYPE[position][0];
	}

	private String getFragmentClass(int position) {
		String fragmentClass = MavinFragment.class.getCanonicalName();
		switch (position) {
		case 0:
			fragmentClass = MavinFragment.class.getCanonicalName();
			break;
		case 1:
			fragmentClass = InviteFragment.class.getCanonicalName();
			break;
		case 2:
			fragmentClass = BidCompanyFragment.class.getCanonicalName();
			break;
		case 3:
			fragmentClass = PretrialFragment.class.getCanonicalName();
			break;
		case 4:
			fragmentClass = AnswerFragment.class.getCanonicalName();
			break;
		case 5:
			fragmentClass = QuoteFragment.class.getCanonicalName();
			break;
		case 6:
			fragmentClass = ScoreFragment.class.getCanonicalName();
			break;
		case 7:
			fragmentClass = BidWinningNoticeFragment.class.getCanonicalName();
			break;
		case 8:
			fragmentClass = BidWinningNoticeFragment.class.getCanonicalName();
			break;
		}
		return fragmentClass;
	}
	
	@Override
	public void handleParentEvent(ZB_flow b) {
		if (mParentBean == null)
			return;

		switchContent(mParentBean.getFlow_type() - 1);
		
	}

}

