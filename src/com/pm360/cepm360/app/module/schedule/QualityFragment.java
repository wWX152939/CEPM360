package com.pm360.cepm360.app.module.schedule;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.TextView;

import com.pm360.cepm360.R;
import com.pm360.cepm360.app.CepmApplication;
import com.pm360.cepm360.app.cache.UserCache;
import com.pm360.cepm360.app.common.adpater.DataListAdapter.ViewHolder;
import com.pm360.cepm360.app.common.custinterface.CommonListInterface;
import com.pm360.cepm360.app.common.custinterface.DialogAdapterInterface;
import com.pm360.cepm360.app.common.custinterface.FloatingMenuInterface;
import com.pm360.cepm360.app.common.custinterface.ServiceInterface;
import com.pm360.cepm360.app.common.view.parent.BaseDialog;
import com.pm360.cepm360.app.module.common.attachment.AttachmentActivity;
import com.pm360.cepm360.app.module.common.attachment.AttachmentFragment;
import com.pm360.cepm360.app.module.common.attachment.AttachmentReadOnlyActivity;
import com.pm360.cepm360.app.module.common.attachment.UploadAttachManager;
import com.pm360.cepm360.app.utils.UtilTools;
import com.pm360.cepm360.common.GLOBAL;
import com.pm360.cepm360.entity.Quality;
import com.pm360.cepm360.entity.TaskCell;
import com.pm360.cepm360.entity.ZH_group;
import com.pm360.cepm360.services.plan.RemoteQualityService;

import java.io.Serializable;
import java.util.HashMap;
import java.util.Map;

public class QualityFragment<P extends TaskCell> extends SendEmailFragment<Quality, P> {

	private RemoteQualityService mService;
	private boolean mIsCurrentUser = true;
	
	private ZH_group mMsgGroupData;

	private String[] mTitleString = new String[] {
			SERIAL_NUMBER,
			"name",
			"type",
			"content",
			"event_date",
			"attachment",
			"record"
	};
	
	public static final String[] mDialogString = new String[] {
			"name",
			"type",
			"content",
			"event_date",
	};
	
	FloatingMenuInterface mFloatingMenuInterface = new FloatingMenuInterface() {

		@Override
		public int[] getFloatingMenuImages() {
			return new int[] { R.drawable.icn_add };
		}

		@Override
		public String[] getFloatingMenuTips() {
			return new String[] { getActivity().getString(R.string.add) };
		}

		@Override
		public OnItemClickListener getFloatingMenuListener() {
			return new OnItemClickListener() {
				
				 @Override	
		         public void onItemClick(AdapterView<?> parent,
		         						View view,
		         						int position,
		         						long id) {
		             switch (position) {
		                 case 0:
		                 	 mIsAddOperation = mIsFloatMenuAdd = true;
		                 	 if (!doExtraAddFloatingMenuEvent()) {
		                 		String[] values = new String[mDialogLableNames.length];
		                 		values[3] = UtilTools.getCurrentTime("yyyy-MM-dd");
		                 		mDialog.show(values);
		                 	 }
		                     mFloatingMenu.dismiss();
		                     break;
		                 default:
		                     break;
		             }
		         }
			};
		}
		
	};
	
	@Override
	protected boolean doExtraInitLayout(View convertView, ViewHolder holder, int position) {
		if (super.doExtraInitLayout(convertView, holder, position)) {
			return true;
		}
		if (position == 6) {
			holder.tvs[position] = (TextView) convertView
					.findViewById(mListItemIds[position]);
			Drawable drawable = getResources().getDrawable(R.drawable.mailbox);
			
			//必须设置图片大小，否则不显示
            drawable.setBounds(0, 0, drawable.getMinimumWidth(), drawable.getMinimumHeight());
            holder.tvs[position].setCompoundDrawablePadding(UtilTools.dp2pxW(getActivity(), 4));
            holder.tvs[position].setCompoundDrawables(drawable, null, null, null);
			return true;
		}
		return false;
	}
	
	
	@Override
	public void handleParentEvent(P b){
		super.handleParentEvent(b);
		if (b != null && (UserCache.getCurrentUser().getUser_id() != mMsgGroupData.getQuality_user())) {
			mIsCurrentUser = false;
			if (mFloatingMenu != null) {
				mFloatingMenu.setVisibility(View.GONE);
			}
			
			if (mOptionsMenu != null) {
				mOptionsMenu.setVisibility(false);
			}
		} else {
			mIsCurrentUser = true;
			if (mFloatingMenu != null) {
				mFloatingMenu.setVisibility(View.VISIBLE);
			}
			
			if (mOptionsMenu != null) {
				mOptionsMenu.setVisibility(true);
			}
			
		}
	}
	
	/**
	 * 创建视图
	 */
	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {
		Bundle bundle = getArguments();
		Serializable data = null;
		if (bundle != null) {
			data = bundle.getSerializable("groupData");
		}
		if (data != null) {
			mMsgGroupData = (ZH_group) data;
		} else {
			mMsgGroupData = ((CombinationScheduleActivity)getActivity()).getGroupData();
		}
		// 初始化类型和接口
		init(Quality.class, mListInterface, mRequestInterface, mFloatingMenuInterface, null,
				mDialogInterface);

		setPermissionIdentity(GLOBAL.SYS_ACTION[5][0],
				GLOBAL.SYS_ACTION[4][0]);
		mService = RemoteQualityService.getInstance();
		mApplication = (CepmApplication) getActivity().getApplication();

		View view = super.onCreateView(inflater, container, savedInstanceState);
		if (!mIsCurrentUser) {
			if (mFloatingMenu != null) {
				mFloatingMenu.setVisibility(View.GONE);
			}
			
			if (mOptionsMenu != null) {
				mOptionsMenu.setVisibility(false);
			}
		}
		return view;
	}

	CommonListInterface<Quality> mListInterface = new CommonListInterface<Quality>() {

		@Override
		public Map<String, Map<String, String>> getDisplayFieldsSwitchMap() {
			Map<String, Map<String, String>> map = new HashMap<String, Map<String, String>>();
			Map<String, String> subMap2 = new HashMap<String, String>();
			for (int i = 0; i < GLOBAL.QUALITY_TYPE.length; i++) {
				subMap2.put(GLOBAL.QUALITY_TYPE[i][0], GLOBAL.QUALITY_TYPE[i][1]);
				subMap2.put(GLOBAL.QUALITY_TYPE[i][1], GLOBAL.QUALITY_TYPE[i][0]);
			}
			
			map.put("type", subMap2);
			return map;
		}

		@Override
		public int getListItemId(Quality t) {
			return t.getId();
		}

		@Override
		public String[] getDisplayFeilds() {
			return mTitleString;
		}

		@Override
		public int getListHeaderLayoutId() {
			return R.layout.safety_list_item;
		}

		@Override
		public int getListLayoutId() {
			return R.layout.safety_list_item;
		}

		@Override
		public int getListHeaderNames() {
			return R.array.safety_list_names;
		}

		@Override
		public int getListHeaderIds() {
			return R.array.safety_list_ids;
		}

		@Override
		public int getListItemIds() {
			return R.array.safety_list_ids;
		}

	};
	
	ServiceInterface<Quality> mRequestInterface = new ServiceInterface<Quality>() {

		@Override
		public void getListData() {
			if (checkParentBeanForQuery()) {
				mService.getQualityList(getServiceManager(), mParentBean.getTask_id());
			}
		}

		@Override
		public void addItem(Quality t) {
			if (checkParentBeanForQuery()) {
				t.setTenant_id(mParentBean.getTenant_id());
				if (mParentBean.getProject_id() == 0) {
					t.setZh_group_id(mParentBean.getZh_group_id());
				} else {
					t.setProject_id(mParentBean.getProject_id());
				}
				
				t.setTask_id(mParentBean.getTask_id());
				t.setMsg_user(UploadAttachManager.getUserString(mParentBean, mParentListAdapter));
				mService.addQuality(getServiceManager(), t);
			}
		}

		@Override
		public void deleteItem(Quality t) {
			mService.deleteQuality(getServiceManager(), t.getId());
		}

		@Override
		public void updateItem(Quality t) {
			mService.updateQuality(getServiceManager(), t);
		}

	};
	DialogAdapterInterface mDialogInterface = new DialogAdapterInterface() {

		@Override
		public int getDialogTitleId() {
			return R.string.plan_add_safety;
		}

		@Override
		public int getDialogLableNames() {
			return R.array.safety_dialog_names;
		}

		@Override
		public String[] getUpdateFeilds() {
			return mDialogString;
		}

		@Override
		public Integer[] getImportantColumns(Map<String, String> saveDataMap) {
			return null;
		}

		@SuppressLint("UseSparseArrays") @Override
		public Map<Integer, Integer> getDialogStyles() {
			Map<Integer, Integer> buttons = new HashMap<Integer, Integer>();
			buttons.put(1, BaseDialog.spinnerLineStyle);
			buttons.put(2, BaseDialog.remarkEditTextLineStyle);
			buttons.put(3, BaseDialog.calendarLineStyle);
			return buttons;
		}

		@SuppressLint("UseSparseArrays") @Override
		public Map<Integer, String[]> getSupplyData() {
			Map<Integer, String[]> map = new HashMap<Integer, String[]>();
			String[] array = new String[GLOBAL.QUALITY_TYPE.length];
			for (int i = 0; i < GLOBAL.QUALITY_TYPE.length; i++) {
				array[i] = GLOBAL.QUALITY_TYPE[i][1];
			}
			map.put(1, array);
			return map;
		}

		@Override
		public void additionalInit(BaseDialog dialog) {
			
		}

		@Override
		public Map<String, Map<String, String>> getUpdateFieldsSwitchMap() {
			return mListInterface.getDisplayFieldsSwitchMap();
		}

	};

	@Override
	protected int getDocumentType() {
		return AttachmentFragment.JH_QUALITY_TYPE;
	}
	
	@Override
	protected int getEmailTitleType() {
		return getDocumentType();
	}
	
	@Override
	protected int getAttachPosition() {
		return 5;
	}

	@Override
	protected boolean dialogSaveButtonEvent() {
		mSaveData = mDialog.SaveData();
		if (mSaveData.get(mDialogLableNames[0]).isEmpty()
				|| mSaveData.get(mDialogLableNames[3]).isEmpty()) {
			sendMessage(SHOW_TOAST, 
					getResources().getString(R.string.pls_input_all_info));
			return false;
		} else {
			return super.dialogSaveButtonEvent();
		}
	}
	
	@Override
	public boolean isChildHandleFloatingMenuOnly() {
		return true;
	}
	
	@Override
	protected Intent setAttachmentActivity() {
		Intent intent = new Intent();
		if (mIsCurrentUser) {
			intent.setClass(getActivity(), AttachmentActivity.class);
		} else {
			intent.setClass(getActivity(), AttachmentReadOnlyActivity.class);
		}
		return intent;
	}
}

