package com.pm360.cepm360.app.common.view.parent.list;

import android.app.Activity;
import android.content.Context;
import android.support.v4.app.FragmentActivity;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.CheckBox;
import android.widget.FrameLayout.LayoutParams;

import com.pm360.cepm360.R;
import com.pm360.cepm360.app.common.adpater.DataListAdapter.ViewHolder;
import com.pm360.cepm360.app.common.custinterface.ListViewPagerInterface;
import com.pm360.cepm360.app.common.custinterface.OperationMode;
import com.pm360.cepm360.app.common.view.CustomTranslateAnimation;
import com.pm360.cepm360.app.common.view.parent.BaseViewPager;

import java.io.Serializable;

public abstract class ListWithViewPager<T extends Serializable> 
									extends ListWithOptionMenu<T> 
									implements ListViewPagerInterface {

	/**------------------ 视图控件 -----------------*/
	private BaseViewPager mBaseViewPager;
	private ViewGroup mViewPagerLayout;
	
	// 是否已经触发ViewPager显示
	private boolean mHasDisplayViewPager;
	
	
	/**------------------ 方法定义 -----------------*/
	
	/**
	 * 构造函数
	 * @param context
	 */
	public ListWithViewPager(Context context) {
		super(context);
	}
	
	/**
	 * 在SimpleList中被调用
	 */
	@Override
	protected void initListView() {
		super.initListView();
		
		// 初始化ViewPager
		initViewPager();
	}
	
	/**
	 * 初始化ViewPager
	 */
	private void initViewPager() {
		mViewPagerLayout = (ViewGroup) mRootLayout.findViewById(R.id.baseViewPager);
		mBaseViewPager = new BaseViewPager((FragmentActivity) mContext, mViewPagerLayout);
		
		// 设置ViewPager的偏移
		View fixedView = mListHeader.findViewById(R.id.fix_id);
		LayoutParams params = (LayoutParams) mViewPagerLayout.getLayoutParams();
		params.leftMargin = fixedView.getLayoutParams().width;
		
		mBaseViewPager.init(getTitleResourceId(), 
					getFloatingMenuViews(), getViewPagers());
		mBaseViewPager.setSlidingPane(getSlidingPaneLayout());
		
		// 设置触发ViewPager的监听
		View openView = mListHeader.findViewById(R.id.open_eye);
		openView.setVisibility(View.VISIBLE);
		openView.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View v) {
				if (mHasDisplayViewPager) {
					CustomTranslateAnimation.fadeRight(mViewPagerLayout);
					mFloatingMenu.setVisibility(View.VISIBLE);
					mHasDisplayViewPager = false;
				} else {
					CustomTranslateAnimation.showRight(mViewPagerLayout);
					mFloatingMenu.setVisibility(View.INVISIBLE);
					mHasDisplayViewPager = true;
					mBaseViewPager.setCurrentParentBean(mCurrentItem);
				}
			}
		});
	}
	
	/**
	 * 单击处理函数
	 */
	@Override
	protected void clickRegister(final ViewHolder viewHolder, final int position) {
		for (int i = 0; i < mListItemIds.length; i++ ) {
			viewHolder.tvs[i].setOnClickListener(new OnClickListener() {
				
				@Override
				public void onClick(View view) {
					// ActionMode批量操作模式
					if (mActionMode != null) {
						mListAdapter.setPickSelected(position);
						updateActionModeTitle(mActionMode, mContext,
                                			  mListAdapter.getSelectedList().size());
					// 非多选模式，弹出选项菜单，但如果是单选模式也不会弹出
					} else if (OperationMode.MULTI_SELECT != mCurrentMode) {
						mCurrentItem = mListAdapter.getItem(position);
						mListAdapter.setSelected(position, true);
						
						// TODO
						if (mHasDisplayViewPager) {
							mBaseViewPager.setCurrentParentBean(mCurrentItem);
						}
							
						// 切换选项菜单
						switchOptionMenu(view);
					} else { // 多选择模式
						if (viewHolder.cbs[0].isChecked()) {
							viewHolder.cbs[0].setChecked(false);
							if (mHeaderCheckBox.isChecked()) {
								mHeaderCheckBox.setChecked(false);
							}
							
							T t = mListAdapter.getItem(position);
							mSelectedDataList.remove(t);
						} else {
							viewHolder.cbs[0].setChecked(true);
							mCurrentItem = mListAdapter.getItem(position);
							
							mSelectedDataList.add(mCurrentItem);
						}
						mListAdapter.setPickSelected(position);
					}
				}
			});
			
			// 多选模式，启动多选功能（注册复选框的单击监听器）
			if (OperationMode.MULTI_SELECT == mCurrentMode) {
				if (mListAdapter.isContainPosition(position)) {
					viewHolder.cbs[0].setChecked(true);
				} else {
					viewHolder.cbs[0].setChecked(false);
				}
				
				viewHolder.cbs[0].setOnClickListener(new View.OnClickListener() {
					
					@Override
					public void onClick(View view) {
						if (((CheckBox) view).isChecked()) {
							mCurrentItem = mListAdapter.getItem(position);
							
							mSelectedDataList.add(mCurrentItem);
						} else {
							if (mHeaderCheckBox.isChecked()) {
								mHeaderCheckBox.setChecked(false);
							}
							
							T t = mListAdapter.getItem(position);
							mSelectedDataList.remove(t);
						}
						mListAdapter.setPickSelected(position);
					}
				});
			}
			
			// 正常模式下，注册长按监听器
			if (OperationMode.NORMAL == mCurrentMode) {
				// 注册长按监听
                viewHolder.tvs[i].setOnLongClickListener(new View.OnLongClickListener() {
                    @Override
                    public boolean onLongClick(View view) {
                    	if (mPermissionManager.hasEditPermission()
                    					&& mEnableNormalMultSelect
                    					&& !mHasDisplayViewPager) {
                    		
	                    	// 长按进入ActionMode，此时ActionMode应该是NULL
	                        mActionMode = ((Activity) mContext).startActionMode(mCallback);
	                        mListAdapter.setPickSelected(position);
	                        updateActionModeTitle(mActionMode, mContext,
	                                				mListAdapter.getSelectedList().size());
                    	} else {
                    		mCurrentItem = mListAdapter.getItem(position);
                    		mListAdapter.setSelected(position, true);
                    	}
                        return true;
                    }
                });
			}
		}
	}
}
