package com.pm360.cepm360.app.common.view.parent;

import android.annotation.SuppressLint;
import android.app.ActionBar.LayoutParams;
import android.app.Activity;
import android.graphics.Color;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentActivity;
import android.support.v4.view.ViewPager;
import android.support.v4.view.ViewPager.OnPageChangeListener;
import android.support.v4.widget.SlidingPaneLayout;
import android.util.TypedValue;
import android.view.Gravity;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.pm360.cepm360.R;
import com.pm360.cepm360.app.common.activity.NoScrollViewPager;
import com.pm360.cepm360.app.common.adpater.BaseFragmentViewPagerAdapter;
import com.pm360.cepm360.app.common.adpater.DataTreeListAdapter;
import com.pm360.cepm360.app.common.adpater.FragmentClassViewPagerAdapter;
import com.pm360.cepm360.app.common.adpater.ViewPagerAdapter;
import com.pm360.cepm360.app.common.custinterface.PageChangeListenerInterface;
import com.pm360.cepm360.app.common.custinterface.RelevanceChildInterface;
import com.pm360.cepm360.app.common.custinterface.RelevanceParentInterface;
import com.pm360.cepm360.app.common.custinterface.SelectInterface;
import com.pm360.cepm360.app.common.custinterface.SimplePageChangeListenerInterface;
import com.pm360.cepm360.app.common.custinterface.SimpleRelevanceChildInterface;
import com.pm360.cepm360.app.common.custinterface.TaskRelevanceChildInterface;
import com.pm360.cepm360.app.common.custinterface.ViewPagersInterface;
import com.pm360.cepm360.app.common.view.FloatingMenuView;
import com.pm360.cepm360.entity.Project;

import java.util.ArrayList;
import java.util.List;

/*
 * ViewPager基类
 */
@SuppressWarnings("rawtypes")
@SuppressLint("ResourceAsColor") 
public class BaseViewPager implements SelectInterface, RelevanceParentInterface, OnPageChangeListener {
	/*-- 标识 --*/
	public final static int RELEVANCE_TYPE = 1;
	private static final int SHOW_FLOATMENU = 100;
    private static final int DISMISS_FLOATMENU = 101;
	private final int BASE_TITLE_ID = 0x200;
	
	/*-- View --*/
	private SlidingPaneLayout mSlidingPaneLayout = null;
	protected ImageView RightImageViewTitle;
	protected TextView[] tvs;
	private NoScrollViewPager mViewPager;
	private LinearLayout mLinearLayout;

	private FloatingMenuView[] mMenus;
	
	private View mView;
	private List<View> mChildViewList;
	@SuppressWarnings("unused")
	private List<ViewPagersInterface<?>> mChildPagersList;
	
	/*-- 缓存 --*/
	protected String[] mViewPagerNames;
	private Object mParentBean;
	private int mCurrentPage;
	
	/*-- 接口 --*/
	private PageChangeListenerInterface mPageChangeListenerInterface;
	private FragmentViewPagerAdapterInterface mFragmentViewPagerAdapterInterface;
	private List<SimpleRelevanceChildInterface> mRelevanceChildInterfaceList;
	
	/*-- Android相关 --*/
	private Activity mActivity;
	private BaseFragmentViewPagerAdapter<? extends Fragment> mPagerAdapter;
	
	public BaseViewPager(Activity activity, View view) {
		mActivity = activity;
		mView = view;
		mView.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View v) {
				// 拦截第一个View的监听
			}
		});
	}
	
	public BaseViewPager(FragmentActivity activity, View view) {
		mActivity = activity;
		mView = view;
		mView.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View v) {
				// 拦截第一个View的监听
			}
		});
	}
	
	/**
	 * 选择界面
	 * @param currentPage
	 */
	public void onButtonClick(int currentPage) {
		if (mViewPager != null) {
			mViewPager.setCurrentItem(currentPage);
		}
	}

	@SuppressLint("HandlerLeak")
	private Handler mFloatingMenuHandler = new Handler() {
    	public void handleMessage(Message msg) {
    		int itemId = (int) msg.obj;
    		switch (msg.what) {
				case SHOW_FLOATMENU:
					mMenus[itemId].setVisibility(View.VISIBLE);
		            ViewGroup group = (ViewGroup) mMenus[itemId].getParent();
		            float offset = group.getMeasuredWidth()
		                    - mActivity.getResources().getDimensionPixelSize(R.dimen.dp16_w) //margin right 16dp
		                    - mMenus[itemId].getMeasuredWidth();

		            if (mSlidingPaneLayout != null) {
		                if (mSlidingPaneLayout.isOpen()) {
		                    // navigation width is 100dp
		                    offset = offset - mActivity.getResources().getDimensionPixelSize(R.dimen.system_left_navigation_width);
		                }
		            }
		            mMenus[itemId].setX(offset);
					break;
				case DISMISS_FLOATMENU:
					mMenus[itemId].setVisibility(View.GONE);
					break;
				default:
					break;
			}	    		
        }
    };
	
	
	/**
	 * for FragmentViewPagerAdapter
	 * @param slidingPaneLayout
	 */
	public void setSlidingPane(SlidingPaneLayout slidingPaneLayout) {
		mSlidingPaneLayout = slidingPaneLayout;
	}
	
	public ViewPager initFragments(int arrayId, Class<? extends Fragment>[] fragments, SimplePageChangeListenerInterface pageInterface) {
		mViewPagerNames = mActivity.getResources().getStringArray(arrayId);
		return initFragments(mViewPagerNames, fragments, pageInterface);
	}
	
	@SuppressWarnings({ "unchecked" })
	public ViewPager initFragments(String[] stringArray, Class<? extends Fragment>[] fragments, SimplePageChangeListenerInterface pageInterface) {
		setBundleData(pageInterface.getBundleData());

		mPagerAdapter = new FragmentClassViewPagerAdapter((FragmentActivity) mActivity,
				mBundle, fragments, stringArray);
		commonInitFragments(stringArray);

		return mViewPager;
	
	}

	public ViewPager initFragments(int arrayId, final List<Fragment> fragments) {
		mViewPagerNames = mActivity.getResources().getStringArray(arrayId);
		return initFragments(mViewPagerNames, fragments);
	}
	
	/**
	 * 最终会调用该地方
	 * @param stringArray
	 * @param fragments
	 * @return
	 */
	@SuppressWarnings({ "unchecked" })
	public ViewPager initFragments(String[] stringArray, final List<Fragment> fragments) {

		if (RelevanceChildInterface.class.isInstance(fragments.get(0))) {
			mMenus = new FloatingMenuView[stringArray.length];
		}
		mPagerAdapter = 
				new BaseFragmentViewPagerAdapter((FragmentActivity) mActivity, mBundle, 
						 fragments, mMenus);
		commonInitFragments(stringArray);
		return mViewPager;
	
	}
	
	public <T extends Fragment> ViewPager initFragments(int arrayId, final List<Fragment> fragments, SimplePageChangeListenerInterface pageInterface) {
		mViewPagerNames = mActivity.getResources().getStringArray(arrayId);
		return initFragments(mViewPagerNames, fragments, pageInterface);
	}
	
	public <T extends Fragment> ViewPager initFragments(String[] stringArray, final List<Fragment> fragments, SimplePageChangeListenerInterface pageInterface) {
		setBundleData(pageInterface.getBundleData());
		return initFragments(stringArray, fragments);
	
	}
	
	public void setTextViewContent(int which, String content) {
		TextView tv = (TextView) mLinearLayout.findViewById(BASE_TITLE_ID + which);
		tv.setText("    " + content + "    ");
	}
	
	public TextView getTextView(int which) {
		return (TextView) mLinearLayout.findViewById(BASE_TITLE_ID + which);
	}
	
	public Fragment getCurrentFragment() {
		return mPagerAdapter.getCurrentFragment();
	};
	public List<? extends Fragment> getListFragment() {
		return mPagerAdapter.getListFragment();
	};
	
	/**
	 * 实现SimpleRelevanceChildInterface接口
	 * @param arrayId
	 * @param views
	 * @param menus
	 * @return
	 */
	public ViewPager init(int arrayId, final List<View> views, FloatingMenuView[] menus) {
		mViewPagerNames = mActivity.getResources().getStringArray(arrayId);
		
		mMenus = menus;
		mChildViewList = views;

		commonInit(mViewPagerNames);
		if (SimpleRelevanceChildInterface.class.isInstance(views.get(0))) {
			mRelevanceChildInterfaceList = new ArrayList<SimpleRelevanceChildInterface>();
			for (int i = 0; i < mViewPagerNames.length; i++) {
				mRelevanceChildInterfaceList.add((SimpleRelevanceChildInterface) views.get(i));
			}
		}
		
		mViewPager.setOnPageChangeListener(this);
		mViewPager.setAdapter(new ViewPagerAdapter(mChildViewList));
		return mViewPager;
	
	}
	
	public ViewPager init(int arrayId, FloatingMenuView[] menus, final List<ViewPagersInterface<?>> views) {
		mViewPagerNames = mActivity.getResources().getStringArray(arrayId);
		
		mMenus = menus;
		mChildPagersList = views;
		mChildViewList = new ArrayList<View>();
		for (int i = 0; i < views.size(); i++) {
			mChildViewList.add(views.get(i).getRootView());
		}

		commonInit(mViewPagerNames);
		if (SimpleRelevanceChildInterface.class.isInstance(views.get(0))) {
			mRelevanceChildInterfaceList = new ArrayList<SimpleRelevanceChildInterface>();
			for (int i = 0; i < mViewPagerNames.length; i++) {
				mRelevanceChildInterfaceList.add((SimpleRelevanceChildInterface) views.get(i));
			}
		}
		
		mViewPager.setOnPageChangeListener(this);
		mViewPager.setAdapter(new ViewPagerAdapter(mChildViewList));
		return mViewPager;
	}
	
	protected void commonInitFragments(String[] stringArray) {
		commonInit(stringArray);
		if (SimpleRelevanceChildInterface.class.isInstance(mPagerAdapter.getListFragment().get(0))) {
			mRelevanceChildInterfaceList = new ArrayList<SimpleRelevanceChildInterface>();
			for (int i = 0; i < stringArray.length; i++) {
				mRelevanceChildInterfaceList.add((SimpleRelevanceChildInterface) mPagerAdapter.getListFragment().get(i));
			}
		}
		
        mViewPager.setOnPageChangeListener(this);
        mPageChangeListenerInterface = mPagerAdapter;
        mFragmentViewPagerAdapterInterface = mPagerAdapter;
		mViewPager.setAdapter(mPagerAdapter);
	}
	
	private void commonInit(String[] stringArray) {
		RightImageViewTitle = (ImageView)mView.findViewById(R.id.base_title_right);
		mViewPagerNames = stringArray;
		mLinearLayout = (LinearLayout)mView.findViewById(R.id.title_menu);
		mLinearLayout.removeAllViews();
		mViewPager = (NoScrollViewPager)mView.findViewById(R.id.tabpager);
		tvs = new TextView[mViewPagerNames.length];
		
		for (int i = 0; i < mViewPagerNames.length; i++) {
			final int currentItem = i;
			tvs[i] = new TextView(mActivity);

	        LinearLayout.LayoutParams tvParams = new LinearLayout.LayoutParams(  
	        		LayoutParams.WRAP_CONTENT, LayoutParams.MATCH_PARENT);
	        tvs[i].setId(BASE_TITLE_ID + i);
	        tvs[i].setText("    " + mViewPagerNames[i] + "    ");
	        tvs[i].setTextSize(TypedValue.COMPLEX_UNIT_PX, mActivity.getResources().getDimension(R.dimen.sp20_s));
			tvs[i].setTextColor(Color.BLACK);
	        tvs[i].setClickable(true);

	        tvs[i].setOnClickListener(new OnClickListener() {
				
				@Override
				public void onClick(View arg0) {
					mViewPager.setCurrentItem(currentItem);
				}
			});
	        mLinearLayout.addView(tvs[i], tvParams);
	
		}

		updateResource(0);
	}
	
	private Bundle[] mBundle;
	private void setBundleData(Bundle[] bundle) {
		mBundle = bundle;
	}
	
	/**
	 * 点击时响应
	 * @param arg0
	 */
	private void updateWindowAttr(int arg0) {
		updateResource(arg0);
    	updateFloatButton(arg0);
    }
	
	/**
	 * 滑动时响应
	 * @param arg0
	 */
	@SuppressWarnings("unused")
	private void updateWindowAttr(View arg0) {
		for (int i = 0; i < tvs.length; i++) {
			if ((tvs[i].equals(arg0))) {
				updateResource(i);
		    	updateFloatButton(i);
				break;
			}
		}
    }
	
	/**
	 * 处理顶部按钮效果
	 * @param arg0
	 */
	protected void updateResource(int arg0) {
		RightImageViewTitle.setBackgroundResource(R.drawable.base_title_right_bg);
		for (int i = 0; i < mViewPagerNames.length; i++) {
			if (i == 0) {
				tvs[i].setBackgroundResource(R.drawable.base_title_normal_bg);
			} else {
				tvs[i].setBackgroundResource(R.drawable.base_title_left_normal_bg); // left_normal_bg
			}
			
			if (i == arg0) {
				tvs[i].setTextColor(Color.WHITE);
			} else {
				tvs[i].setTextColor(Color.BLACK);
			}
			tvs[i].setGravity(Gravity.CENTER);
			
		}

		// 对选中做单独处理
		if (arg0 == 0) {
			tvs[arg0].setBackgroundResource(R.drawable.base_title_click_bg);
		} else {
			tvs[arg0].setBackgroundResource(R.drawable.base_title_click_content_bg);
		}
		
		if (arg0 != (mViewPagerNames.length - 1)) {
			tvs[arg0 + 1].setBackgroundResource(R.drawable.base_title_left_click_bg); // left_click_bg
		} else {
			RightImageViewTitle.setBackgroundResource(R.drawable.base_title_right_click_bg);
		}
		
    }
	
	/**
	 * 处理右下侧滑动按钮切换效果
	 * @param arg0
	 */
	private void updateFloatButton(int arg0) {
		if (mMenus == null) {
			return;
		}
		
		for (int i = 0; i < mMenus.length; i++) {
			if (arg0 != i) {
				if (mMenus[i] != null) {
					mMenus[i].setVisibility(View.GONE);
				}	
			}
		}
		if (mMenus[arg0] != null) {
			if (mRelevanceChildInterfaceList != null && RelevanceChildInterface.class.isInstance(mRelevanceChildInterfaceList.get(arg0))) {
				if (!((RelevanceChildInterface) mRelevanceChildInterfaceList.get(arg0)).isChildHandleFloatingMenuOnly()) {
					mMenus[arg0].setVisibility(View.VISIBLE);
					Message msg = Message.obtain();
					msg.what = SHOW_FLOATMENU;
					msg.obj = arg0;
					mFloatingMenuHandler.sendMessageDelayed(msg, 100);
				}
			} else {
				mMenus[arg0].setVisibility(View.VISIBLE);
				Message msg = Message.obtain();
				msg.what = SHOW_FLOATMENU;
				msg.obj = arg0;
				mFloatingMenuHandler.sendMessageDelayed(msg, 100);
			}
			
		}
    }

	@Override
	public List<?> getSelectedDataList() {
		Fragment fragment = getCurrentFragment();
		if (fragment instanceof SelectInterface) {
			return ((SelectInterface) fragment).getSelectedDataList();
		}
		return null;
	}

	@Override
	public void setFilterData(List<?> filters) {
		@SuppressWarnings("unchecked")
		List<Fragment> fragments = (List<Fragment>) getListFragment();
		for (Fragment fragment : fragments) {
			if (fragment instanceof SelectInterface) {
				((SelectInterface) fragment).setFilterData(filters);
			}
		}
	}

	@Override
	public void enableInnerButton(boolean enable) {
		@SuppressWarnings("unchecked")
		List<Fragment> fragments = (List<Fragment>) getListFragment();
		for (Fragment fragment : fragments) {
			if (fragment instanceof SelectInterface) {
				((SelectInterface) fragment).enableInnerButton(enable);
			}
		}
	}

	@SuppressWarnings("unchecked")
	@Override
	public void setCurrentParentBean(Object t) {
		mParentBean = t;
		if (mRelevanceChildInterfaceList != null && RelevanceChildInterface.class.isInstance(mRelevanceChildInterfaceList.get(0))) {
			for (SimpleRelevanceChildInterface childInterface : mRelevanceChildInterfaceList) {
				((RelevanceChildInterface) childInterface).setCurrentParentBean(mParentBean);
			}
		}
		
		// fragment view pager
		if (mFragmentViewPagerAdapterInterface != null) {
			if (mFragmentViewPagerAdapterInterface.isCurrentPageCanUsed()) {
				mRelevanceChildInterfaceList.get(mCurrentPage).handleParentEvent(mParentBean);
			}
		} else {
			// view pager
			mRelevanceChildInterfaceList.get(mCurrentPage).handleParentEvent(mParentBean);
		}
		
	}
	
	/**
	 * setParentList setCurrentList setChildProject 必须是TaskRelevanceChildInterface接口调用
	 */
	@SuppressWarnings("unchecked")
	public void setParentList(DataTreeListAdapter listAdapter) {
		for (int i = 0; i < mRelevanceChildInterfaceList.size(); i++) {
			TaskRelevanceChildInterface childInterface = (TaskRelevanceChildInterface) mRelevanceChildInterfaceList.get(i);
			childInterface.setParentList(listAdapter);
		}
	}
	
	@SuppressWarnings("unchecked")
	public void setCurrentList(List list) {
		for (int i = 0; i < mRelevanceChildInterfaceList.size(); i++) {
			TaskRelevanceChildInterface childInterface = (TaskRelevanceChildInterface) mRelevanceChildInterfaceList.get(i);
			childInterface.setCurrentList(list);
		}
	}
	
	public void setChildProject(Project project) {
		for (int i = 0; i < mRelevanceChildInterfaceList.size(); i++) {
			TaskRelevanceChildInterface childInterface = (TaskRelevanceChildInterface) mRelevanceChildInterfaceList.get(i);
			childInterface.setCurrentProject(project);
		}
	}
	
	/**
	 * 设置监听接口
	 * @param pageChangeListenerInterface
	 */
	public void setOnPageChangeListenerInterface(PageChangeListenerInterface pageChangeListenerInterface) {
		mPageChangeListenerInterface = pageChangeListenerInterface;
	}

	@Override
	public void onPageScrollStateChanged(int arg0) {
		
	}

	@Override
	public void onPageScrolled(int arg0, float arg1, int arg2) {
		
	}

	@SuppressWarnings("unchecked")
	@Override
	public void onPageSelected(int arg0) {
		mCurrentPage = arg0;
		updateWindowAttr(arg0);
		if (mPageChangeListenerInterface != null) {
			mPageChangeListenerInterface.onExtraPageSelected(arg0);
		}
		
		if (mRelevanceChildInterfaceList != null) {
			mRelevanceChildInterfaceList.get(mCurrentPage).handleParentEvent(mParentBean);
		}
	}
	
	public interface FragmentViewPagerAdapterInterface {
		boolean isCurrentPageCanUsed();
	}
}
