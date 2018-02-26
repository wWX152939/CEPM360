package com.pm360.cepm360.app.common.view.parent.list;

import com.pm360.cepm360.app.cache.PermissionCache;
import com.pm360.cepm360.app.common.custinterface.OperationMode;

public class PermissionManager {

	/** ------------------ 权限 --------------------- */
	// 权限类型
	public static final int PERMISSION_TYPE_SYS = 2;
	
	private int mType;	// 权限类型
	private OperationMode mOperationMode = OperationMode.NORMAL;
		
	// 模块的权限识别符
	private String mViewPermission;
	private String mEditPermission;
	
	// 是否有模块查看和编辑权限
	private boolean mHasViewPermission;
	private boolean mHasEditPermission;
	
	/**
	 * 初始化模块权限和权限类型
	 * @param viewPermission
	 * @param editPermission 
	 * @param type 1 PERMISSION_TYPE_PROJECT 2 PERMISSION_TYPE_SYS
	 */
	public void setPermission(String viewPermission, 
								String editPermission, 
								int type) {
		mViewPermission = viewPermission;
		mEditPermission = editPermission;
		mType = type;
		
		// 计算权限
		initPermission();
	}
	
	/**
	 * 设置操作模式，出发权限系统重新定权
	 * @param mode
	 */
	public void setOperationMode(OperationMode mode) {
		if (mOperationMode != mode) {
			mOperationMode = mode;
			
			// 计算权限
			initPermission();
		}
	}
	
	/**
	 * 是否有查看权限
	 * @return
	 */
	public boolean hasViewPermission() {
		return mHasViewPermission;
	}
	
	/**
	 * 是否有编辑权限
	 * @return
	 */
	public boolean hasEditPermission() {
		return mHasEditPermission;
	}
	
	/**
	 * 初始化权限
	 */
	public void initPermission() {
		if (mOperationMode != OperationMode.NORMAL) {
			mHasViewPermission = true;
			mHasEditPermission = false;
		} else if (mType == PERMISSION_TYPE_SYS) {
			
			// 判定用户是否有查看和编辑权限
	        if (mViewPermission != null) {
	            mHasViewPermission = PermissionCache
	                    .hasSysPermission(mViewPermission);
	        }

	        if (mEditPermission != null) {
	            mHasEditPermission = PermissionCache
	                    .hasSysPermission(mEditPermission);
	        }
		}
	}
}
