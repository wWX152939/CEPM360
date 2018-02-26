package com.pm360.cepm360.app.common.view.parent;

import android.annotation.SuppressLint;
import android.os.Bundle;
import android.support.v4.widget.SlidingPaneLayout;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.view.animation.TranslateAnimation;
import android.widget.FrameLayout.LayoutParams;
import android.widget.ImageView;

import com.pm360.cepm360.R;
import com.pm360.cepm360.app.cache.ProjectCache;
import com.pm360.cepm360.app.common.activity.ActionBarFragmentActivity;
import com.pm360.cepm360.app.common.custinterface.ActionBarInterface;
import com.pm360.cepm360.app.common.custinterface.CommonListInterface;
import com.pm360.cepm360.app.common.custinterface.FloatingMenuInterface;
import com.pm360.cepm360.app.common.custinterface.OptionMenuInterface;
import com.pm360.cepm360.app.common.custinterface.ServiceInterface;
import com.pm360.cepm360.app.common.custinterface.SimpleDialogInterface;
import com.pm360.cepm360.app.common.custinterface.ViewPagerInterface;
import com.pm360.cepm360.entity.Expandable;
import com.pm360.cepm360.entity.Project;

import java.io.Serializable;

/**
 * 
 * 标题: BaseTreeListWithFrameLayoutFragment 
 * 描述: 在BaseTreeListFragment的基础上增加第二个界面，实现一个ViewPager，第一个界面和第二个界面可以交互
 * 作者： onekey
 * 日期： 2016年1月5日
 *
 * @param <T>
 */
public abstract class BaseTreeListWithFrameLayoutFragment<T extends Expandable & Serializable, B extends ActionBarFragmentActivity> 
	extends BaseTreeListFragment<T> {
	
	// 判断是否切入到第二个界面
	protected boolean mSwitchToAttrView = false;
	protected ImageView mAttrButton;
	protected View mSwitchButton;
	
	// 继承并设置project
	protected Project mProject;
	protected B mActivity;
	protected BaseViewPager mBaseViewPager;
	
	@SuppressWarnings("rawtypes")
	private ViewPagerInterface mViewPagerInterface;
	@SuppressWarnings("rawtypes")
	private ServiceInterface mServiceInterface;
	private View mSecondView;
	private ViewGroup mContainer;
	
	protected abstract boolean enableProjectMenu();
	
	protected abstract boolean needSelectProject();
	
	@SuppressWarnings("rawtypes")
	protected void init(Class<T> listItemClass,
			boolean isDeleteParentNode,
			CommonListInterface<T> listInterface,
			ServiceInterface<T> serviceInterface,
			FloatingMenuInterface floatingMenuInterface,
			OptionMenuInterface optionMenuInterface,
			SimpleDialogInterface dialogInterface,
			ViewPagerInterface viewPagerInterface) {
		mViewPagerInterface = viewPagerInterface;
		mServiceInterface = serviceInterface;
		super.init(listItemClass, isDeleteParentNode, listInterface, serviceInterface, floatingMenuInterface, optionMenuInterface, dialogInterface);
	}
	
	@SuppressWarnings("rawtypes")
	protected void init(Class<T> listItemClass,
			boolean isDeleteParentNode,
			CommonListInterface<T> listInterface,
			ServiceInterface<T> serviceInterface,
			FloatingMenuInterface floatingMenuInterface,
			OptionMenuInterface optionMenuInterface,
			SimpleDialogInterface dialogInterface,
			ViewPagerInterface viewPagerInterface,
			ActionBarInterface actionBarInterface) {
		mViewPagerInterface = viewPagerInterface;
		mServiceInterface = serviceInterface;
		super.init(listItemClass, isDeleteParentNode, listInterface, serviceInterface, floatingMenuInterface, optionMenuInterface, dialogInterface, actionBarInterface);
	}
	
	@SuppressWarnings("unchecked")
	public View onCreateView( LayoutInflater inflater,
			  ViewGroup container,
			  Bundle savedInstanceState) {
		mContainer = container;

		View view = super.onCreateView(inflater, container, savedInstanceState);
		if (OperationMode.NORMAL == mCurrentMode) {
			mActivity = (B) getActivity();
			mActivity.setActionBarTitle(mProject == null ? "" : mProject.getName());
		}
		return view;
	}
	
	protected void switchFloatingButton() {
		if (mSwitchToAttrView) {
			if (mFloatingMenu != null) {
				mFloatingMenu.setVisibility(View.GONE);
			}
		} else {
			if (mFloatingMenu != null) {
				mFloatingMenu.setVisibility(View.VISIBLE);
			}
		}
		
	}
	
	private void initAnimationSlide() {
		final TranslateAnimation showAction = new TranslateAnimation(Animation.RELATIVE_TO_SELF, 1.0f,
				Animation.RELATIVE_TO_SELF, 0.0f, Animation.RELATIVE_TO_SELF,
				0.0f, Animation.RELATIVE_TO_SELF, 0.0f);
		showAction.setDuration(500);
		final TranslateAnimation hiddenAction = new TranslateAnimation(Animation.RELATIVE_TO_SELF,
				0.0f, Animation.RELATIVE_TO_SELF, 1.0f,
				Animation.RELATIVE_TO_SELF, 0.0f, Animation.RELATIVE_TO_SELF,
				0.0f);
		hiddenAction.setDuration(500);

		mAttrButton = (ImageView)mListHeader.findViewById(R.id.attr_button);
		mAttrButton.setVisibility(View.VISIBLE);

		mSwitchButton = mListHeader.findViewById(R.id.switch_button);
		
		mAttrButton.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View arg0) {
				if (!hasListItemData()) {
					return;
				}
				mSwitchToAttrView = (mSwitchToAttrView == false) ? true : false;
				switchFloatingButton();
				switchToAttrView(mSwitchToAttrView);
				if (mSwitchToAttrView) {
					mSecondView.startAnimation(showAction);
				} else {
					mSecondView.startAnimation(hiddenAction);
				}

			}
		});
	}
	
	protected abstract boolean hasListItemData();
	
	protected void switchToAttrView(boolean switchStatus) {
		if (mListAdapter != null) {
			mListAdapter.setSpecialIsDrag(!switchStatus);
		}
		
		if (switchStatus) {
			enableProjectMenuView(false);
			// 此处是为了丝滑的弹出attr界面
			mSecondView.setVisibility(View.VISIBLE);
			mAttrButton.setImageResource(R.drawable.arrow_double_right);
			if (mSwitchButton != null) {
				mSwitchButton.setVisibility(View.GONE);
			}
		} else {
			enableProjectMenuView(true);
			mSecondView.setVisibility(View.GONE);
			mAttrButton.setImageResource(R.drawable.arrow_double_left);
			if (mSwitchButton != null) {
				mSwitchButton.setVisibility(View.VISIBLE);
			}
		}
		
	}
	
	private void enableProjectMenuView(boolean status) {
		if (enableProjectMenu()) {
			mActivity.enableMenuView(status);
		}
	}
	
    /**
	 * 单击处理
	 * @param position
	 */
	@Override
	public void handleClickWithTextView(int position, View view) {
		super.handleClickWithTextView(position, view);
		handleChildEvent();
	}
	
	protected void handleChildEvent() {
		if (OperationMode.NORMAL == mCurrentMode) {
			mBaseViewPager.setCurrentParentBean(mCurrentItem);
		}
	}
	
	@SuppressLint("ResourceAsColor") 
	private void addLineView() {
		ViewGroup parent = (ViewGroup) 
    			mRootLayout.findViewById(R.id.content_list_layout);

        View view = new View(getActivity());
        android.widget.LinearLayout.LayoutParams params = new android.widget.LinearLayout.LayoutParams(LayoutParams.MATCH_PARENT, 1);
        view.setBackgroundColor(R.color.task_child_list_line);
		parent.addView(view, 1, params);
	}
	
	@SuppressWarnings("unchecked")
	@Override
	protected void doExtraEventWithViewPermission() {
//		addLineView();
		
		mSecondView = mRootLayout.findViewById(R.id.baseViewPager);
		View leftTitleView = mListHeader.findViewById(R.id.left_title_container);
		LayoutParams params = (LayoutParams) mSecondView.getLayoutParams();
		params.leftMargin = leftTitleView.getLayoutParams().width;
		mSecondView.setLayoutParams(params);
		
		if (mViewPagerInterface != null && mViewPagerInterface.getViewPagerTitle() != null
				&& mViewPagerInterface.getFragment() != null) {
			mBaseViewPager = new BaseViewPager(getActivity(), mSecondView);
			mBaseViewPager.initFragments(mViewPagerInterface.getViewPagerTitle(), mViewPagerInterface.getFragment());
			
			setSlidingPane();
			initAnimationSlide();
		}
	}
	
	protected void setTextViewContent(int which, String content) {
		mBaseViewPager.setTextViewContent(which, content);
	}
	
	/**
	 * 考虑到可能没有slidingPane的情况
	 */
	protected void setSlidingPane() {
		mBaseViewPager.setSlidingPane((SlidingPaneLayout)mContainer.getParent());
	}
	
	@Override
	protected void doExtraSetWhiteBackground() {
		mIsBlackBackGroud = false;
	}
	
	@Override
	public void onResume() {
		super.onResume();
		if ((mHasEditPermission || mHasViewPermission) && needSelectProject()) {
			handleProjectDataEvent();
		}
	}
	
	public void handleProjectDataEvent() {
		Project project = ProjectCache.getCurrentProject();
		
		if (mProject != null) {
			if (mProject.getProject_id() != project.getProject_id()) {
				mActivity.setActionBarTitle(project.getName());
				mProject = project;
				mBaseViewPager.setChildProject(project);
				mCurrentItem = null;
				mListAdapter.clearAll();
				mServiceInterface.getListData();
				handleChildEvent();
			}
		} else {
			
			// 消息进入，消息数据删除导致project查询不到
			mActivity.setActionBarTitle(project.getName());
			mProject = project;
			mServiceInterface.getListData();
			handleChildEvent();
		}
	}
	
}
