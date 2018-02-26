package com.pm360.cepm360.app.common.view.parent;

import android.app.Activity;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout.LayoutParams;

import com.pm360.cepm360.R;
import com.pm360.cepm360.app.common.adpater.DataTreeListAdapter;
import com.pm360.cepm360.app.common.custinterface.CommonListInterface;
import com.pm360.cepm360.app.common.custinterface.FloatingMenuInterface;
import com.pm360.cepm360.app.common.custinterface.OptionMenuInterface;
import com.pm360.cepm360.app.common.custinterface.ServiceInterface;
import com.pm360.cepm360.app.common.custinterface.SimpleDialogInterface;
import com.pm360.cepm360.app.common.custinterface.TaskRelevanceChildInterface;
import com.pm360.cepm360.app.common.view.FloatingMenuView;
import com.pm360.cepm360.app.common.view.ScheduleSlidingPaneLayout;
import com.pm360.cepm360.entity.Expandable;
import com.pm360.cepm360.entity.Project;

import java.io.Serializable;
import java.util.List;

public class BaseListRelevanceFragment<T extends Serializable, B extends Expandable> extends
		BaseListFragment<T> implements TaskRelevanceChildInterface<T, B> {
	protected B mParentBean;
	protected Project mCurrentProject;

	@Override
	protected void init(Class<T> listItemClass,
			CommonListInterface<T> listInterface,
			ServiceInterface<T> serviceInterface,
			FloatingMenuInterface floatingMenuInterface,
			OptionMenuInterface optionMenuInterface,
			SimpleDialogInterface dialogInterface) {
		super.init(listItemClass, listInterface, serviceInterface,
				floatingMenuInterface, optionMenuInterface, dialogInterface);
	}
	
	protected boolean checkParentBeanForQuery() {
		if (mParentBean == null) {
			if (mListAdapter != null)
				mListAdapter.clearAll();
			return false;
		} else if (!mHasViewPermission && !mHasEditPermission){
			return false;
		} else {
			return true;
		}
	}
	
	/**
	 * 对于组合管理，需要设置当前的project，而不是取cache中的project
	 * @param project
	 */
	public void setCurrentProject(Project project) {
		mCurrentProject = project;
	}
	
	/**
	 * 针对需要parentList的子Fragment，重载该方法
	 * @param bList
	 */
	public void setParentList(DataTreeListAdapter<B> listAdapter) {
		
	}
	
	/**
	 * 针对反馈，需要传递反馈列表
	 * @param listAdapter
	 */
	@Override
	public void setCurrentList(List<T> list) {
		
	}
	
	/**
	 * 重载实现处理父的响应事件
	 */
	public void handleParentEvent(B b) {
		
	}
	
	public void setCurrentParentBean(B b) {
		mParentBean = b;
	}
	
	@Override
	protected boolean doExtraAddFloatingMenuEvent() {
		if (mParentBean == null) {
			BaseToast.show(getActivity(), BaseToast.NO_TASK_EXIST);
			return true;
		}
		return false;
	}
	
	protected void initExtraEvent() {
		if (mFloatingMenu != null) {
			Activity activity = getActivity();
			if (BaseSlidingPaneActivity.class.isInstance(activity)) {
				boolean isOpen = ((ScheduleSlidingPaneLayout) ((BaseSlidingPaneActivity) getActivity()).getSlidingPaneLayout()).getPanelStatus();
				if (!isOpen) {
					LayoutParams params = (LayoutParams) mFloatingMenu.getLayoutParams();
					params.rightMargin = getActivity().getResources().getDimensionPixelOffset(R.dimen.dp16_w);
					mFloatingMenu.setLayoutParams(params);
				}
			} else {
				LayoutParams params = (LayoutParams) mFloatingMenu.getLayoutParams();
				params.rightMargin = getActivity().getResources().getDimensionPixelOffset(R.dimen.dp16_w);
				mFloatingMenu.setLayoutParams(params);
			}
		}
		
		if (mListHeader != null) {
			mListHeader.setBackgroundResource(R.color.content_listview_header_bg);
		}
	    
		ViewGroup layout = (ViewGroup) 
    			mRootLayout.findViewById(R.id.content_list_layout);

		if (layout != null) {
			LayoutParams params = (LayoutParams) layout.getLayoutParams();
		    params.setMargins(0, 0, 0, 0);
		    layout.setLayoutParams(params);
		}
	    
	    ViewGroup headerLayout = (ViewGroup) 
    			mRootLayout.findViewById(R.id.content_header_layout);
	    if (headerLayout != null) {
	    	android.widget.LinearLayout.LayoutParams header_params = (android.widget.LinearLayout.LayoutParams) headerLayout.getLayoutParams();
		    header_params.height = getResources().getDimensionPixelSize(R.dimen.schedule_table_height);
		    headerLayout.setLayoutParams(header_params);
	    }
	}
	
	public FloatingMenuView getFloatingMenu() {
		return mFloatingMenu;
	}

	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {
		return super.onCreateView(inflater, container, savedInstanceState);
	}

	@Override
	public boolean isChildHandleFloatingMenuOnly() {
		return false;
	}

}
