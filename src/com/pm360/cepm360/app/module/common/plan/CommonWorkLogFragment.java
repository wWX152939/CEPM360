package com.pm360.cepm360.app.module.common.plan;

import java.util.HashMap;
import java.util.Map;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.pm360.cepm360.R;
import com.pm360.cepm360.app.cache.UserCache;
import com.pm360.cepm360.app.common.adpater.DataListAdapter.ViewHolder;
import com.pm360.cepm360.app.common.custinterface.CommonListInterface;
import com.pm360.cepm360.app.common.custinterface.DialogAdapterInterface;
import com.pm360.cepm360.app.common.custinterface.ServiceInterface;
import com.pm360.cepm360.app.common.view.parent.BaseDialog;
import com.pm360.cepm360.app.module.common.attachment.AttachmentActivity;
import com.pm360.cepm360.app.module.common.attachment.AttachmentFragment;
import com.pm360.cepm360.app.module.common.attachment.AttachmentReadOnlyActivity;
import com.pm360.cepm360.common.GLOBAL;
import com.pm360.cepm360.entity.Expandable;
import com.pm360.cepm360.entity.WorkLog;
import com.pm360.cepm360.entity.WorkLogCell;
import com.pm360.cepm360.entity.ZB_worklog;
import com.pm360.cepm360.entity.ZH_group_worklog;

public abstract class CommonWorkLogFragment<T extends WorkLogCell, B extends Expandable> extends AttachmentFragment<T, B> {
	
	private String[] mTitleString = new String[] { SERIAL_NUMBER, "content", "creater", "date", "attachment"};
	private String[] mDialogString = new String[] {"content"};
	private Class<T> mClassItem;
	private int mType; // 1 ZH_group_worklog 2 WorkLog 3 ZB_worklog
	private boolean mIsCurrentUser = true;
	
	public abstract ServiceInterface<T> getServiceInterface();
	
	public abstract T getWorkLogBean();
	
	protected abstract int getType();
	
	@Override
	protected Intent setAttachmentActivity() {
		Intent intent = new Intent();
		
		if (!mIsCurrentUser && (mType == 1 || mType == 2)) {
			intent.setClass(getActivity(), AttachmentReadOnlyActivity.class);
		} else {
			intent.setClass(getActivity(), AttachmentActivity.class);
		}
		
		return intent;
	}
	
	@Override
	public void handleParentEvent(B b) {
		super.handleParentEvent(b);
//		if (b != null && (mType == 1 || mType == 2)) {
//			if (UserCache.getCurrentUser().getUser_id() != ((TaskCell)b).getOwner()) {
//				mIsCurrentUser = false;
//				if (mFloatingMenu != null) {
//					mFloatingMenu.setVisibility(View.GONE);
//				}
//				
//				if (mOptionsMenu != null) {
//					mOptionsMenu.setVisibility(false);
//				}
//			} else {
//				mIsCurrentUser = true;
//				if (mFloatingMenu != null) {
//					mFloatingMenu.setVisibility(View.VISIBLE);
//				}
//				
//				if (mOptionsMenu != null) {
//					mOptionsMenu.setVisibility(true);
//				}
//				
//			}
//		}
		
	}
	
	
	protected boolean doExtraInitListViewItem(int line, int position, ViewHolder holder) {
		if (position == 1) {
			if (mListAdapter.getItem(line).getContent() != null) {
				holder.tvs[position].setText(mListAdapter.getItem(line).getContent());
			} else {
				holder.tvs[position].setText("");
			}
			return true;
		}
		return false;
	}

	/**
	 * 创建视图
	 */
	@Override
	public View onCreateView(final LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {
		initEnvironment();
		// 初始化类型和接口
		init(mClassItem, mListInterface, getServiceInterface(), null, null,
				mDialogInterface);

		if (mType == 1) {
			setPermissionIdentity(GLOBAL.SYS_ACTION[53][0],
					GLOBAL.SYS_ACTION[52][0]);
		} else if (mType == 2) {
			setPermissionIdentity(GLOBAL.SYS_ACTION[5][0],
					GLOBAL.SYS_ACTION[4][0]);
		} else if (mType == 3) {
			setPermissionIdentity(GLOBAL.SYS_ACTION[51][0],
					GLOBAL.SYS_ACTION[50][0]);
		}
		View view = super.onCreateView(inflater, container, savedInstanceState);
//		if (!mIsCurrentUser) {
//			if (mFloatingMenu != null) {
//				mFloatingMenu.setVisibility(View.GONE);
//			}
//			
//			if (mOptionsMenu != null) {
//				mOptionsMenu.setVisibility(false);
//			}
//		}
		return view;
	}
	
	@SuppressWarnings("unchecked")
	private void initEnvironment() {
		mType = getType();
		if (mType == 1) {
			mClassItem = (Class<T>) ZH_group_worklog.class;
		} else if (mType == 2) {
			mClassItem = (Class<T>) WorkLog.class;
		} else if (mType == 3) {
			mClassItem = (Class<T>) ZB_worklog.class;
		}
	}
	

	CommonListInterface<T> mListInterface = new CommonListInterface<T>() {

		@Override
		public Map<String, Map<String, String>> getDisplayFieldsSwitchMap() {
			Map<String, Map<String, String>> map = new HashMap<String, Map<String, String>>();
			map.put("creater", UserCache.getUserMaps());
			return map;
		}

		@Override
		public int getListItemId(T t) {
			return t.getId();
		}

		@Override
		public String[] getDisplayFeilds() {
			return mTitleString;
		}

		@Override
		public int getListHeaderLayoutId() {
			return R.layout.combination_document_list_item;
		}

		@Override
		public int getListLayoutId() {
			return R.layout.combination_document_list_item;
		}

		@Override
		public int getListHeaderNames() {
			return R.array.combination_document_names;
		}

		@Override
		public int getListHeaderIds() {
			return R.array.combination_document_title_ids;
		}

		@Override
		public int getListItemIds() {
			return R.array.combination_document_list_ids;
		}

	};


	DialogAdapterInterface mDialogInterface = new DialogAdapterInterface() {

		@Override
		public int getDialogTitleId() {
			return R.string.combination_add_worklog;
		}

		@Override
		public int getDialogLableNames() {
			return R.array.combination_dialog_document_names;
		}

		@Override
		public String[] getUpdateFeilds() {
			return mDialogString;
		}

		public Integer[] getImportantColumns(Map<String, String> saveDataMap) {
			return null;
		}

		@SuppressLint("UseSparseArrays") @Override
		public Map<Integer, Integer> getDialogStyles() {
			Map<Integer, Integer> buttons = new HashMap<Integer, Integer>();
			buttons.put(0, BaseDialog.remarkEditTextLineStyle);
			return buttons;
		}

		@Override
		public Map<Integer, String[]> getSupplyData() {
			return null;
		}

		@Override
		public void additionalInit(final BaseDialog dialog) {

		}

		@Override
		public Map<String, Map<String, String>> getUpdateFieldsSwitchMap() {
			// TODO Auto-generated method stub
			return null;
		}

	};
	
	@Override
	protected int getAttachPosition() {
		return 4;
	}
	
	@Override
	protected boolean dialogSaveButtonEvent() {
		mSaveData = mDialog.SaveData();
		if (mSaveData.get(mDialogLableNames[0]).isEmpty()) {
			sendMessage(SHOW_TOAST, 
					getResources().getString(R.string.pls_input_all_info));
			return false;
		} else {
			return super.dialogSaveButtonEvent();
		}
	}
	
//	@Override
//	public boolean isChildHandleFloatingMenuOnly() {
//		if (mType == 1 || mType == 2) {
//			return true;
//		} else {
//			return isChildHandleFloatingMenuOnly();
//		}
//		
//	}
}

