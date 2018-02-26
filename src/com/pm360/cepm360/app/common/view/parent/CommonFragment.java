package com.pm360.cepm360.app.common.view.parent;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.View;

import com.pm360.cepm360.app.CepmApplication;
import com.pm360.cepm360.app.cache.PermissionCache;
import com.pm360.cepm360.app.common.activity.ListSelectActivity;
import com.pm360.cepm360.app.common.adpater.BaseDragListAdapter;
import com.pm360.cepm360.common.GLOBAL;

public class CommonFragment extends Fragment implements BaseDragListAdapter.OngetBDLASlidePaneListener {
	// 模块权限
	protected boolean mHasViewPermission;
	protected boolean mHasEditPermission;
	
	// 应用对象，用于获取共享数据
	protected CepmApplication mApplication;

	/** ------------------ 权限 --------------------- */
	// 权限识别符
	private String mViewPermission;
	private String mEditPermission;
	
	/**
	 * 初始化权限
	 * @param viewPermission 传递空指针强制无此权限
	 * @param editPermission 传递空指针强制无此权限
	 * @param type 1 PERMISSION_TYPE_PROJECT 2 PERMISSION_TYPE_SYS
	 */
	protected void setPermissionIdentity(String viewPermission, String editPermission) {
		setPermissionIdentity(viewPermission, editPermission, null);
	}
	
	/**
	 * 初始化权限
	 * @param viewPermission 传递空指针强制无此权限
	 * @param editPermission 传递空指针强制无此权限
	 * @param type 1 PERMISSION_TYPE_PROJECT 2 PERMISSION_TYPE_SYS
	 * @param rolePermission 传入权限
	 */
	protected void setPermissionIdentity(String viewPermission, String editPermission, 
			String rolePermission) {
		mViewPermission = viewPermission;
		mEditPermission = editPermission;
	}
	
	/**
	 * 初始化权限
	 */
	protected void initPermission() {
		// 获取应用对象
		mApplication = (CepmApplication) getActivity().getApplication();
		Bundle bundle = getArguments();
		if (bundle != null) {
			if (bundle.containsKey(ListSelectActivity.SELECT_MODE_KEY)) {
				mHasViewPermission = true;
				mHasEditPermission = false;
				return;
			}
		}

		// 组合默认既有查看权限
    	if (mViewPermission != null && (mViewPermission.equals(GLOBAL.SYS_ACTION[35][0]) 
    			|| mViewPermission.equals(GLOBAL.SYS_ACTION[53][0])
    			|| mViewPermission.equals(GLOBAL.SYS_ACTION[5][0])
    			)) {
    		mHasViewPermission = true;
    	} else {
    		mHasViewPermission = PermissionCache
                    .hasSysPermission(mViewPermission);
    	}
		// 判定用户是否有查看和编辑权限

    	if (mEditPermission != null && (mEditPermission.equals(GLOBAL.SYS_ACTION[52][0]) 
    			|| mViewPermission.equals(GLOBAL.SYS_ACTION[4][0])
    			)) {
    		mHasEditPermission = true;
		} else {
			mHasEditPermission = PermissionCache
                    .hasSysPermission(mEditPermission);
		}
		
	}
    
    /**
     * 得到根Fragment
     * 
     * @return
     */
    public Fragment getRootFragment() {
        Fragment fragment = getParentFragment();
        while (fragment.getParentFragment() != null) {
            fragment = fragment.getParentFragment();
        }
        return fragment;
    }


	@Override
	public View getSlidePane() {
		if (BaseSlidingPaneActivity.class.isInstance(getActivity())) {
			return ((BaseSlidingPaneActivity) getActivity()).getSlidingPaneLayout();
		} 
		return null;
	}
}
