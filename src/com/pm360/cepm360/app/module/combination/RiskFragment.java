package com.pm360.cepm360.app.module.combination;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;

import com.pm360.cepm360.R;
import com.pm360.cepm360.app.CepmApplication;
import com.pm360.cepm360.app.cache.ObsCache;
import com.pm360.cepm360.app.cache.UserCache;
import com.pm360.cepm360.app.common.activity.ListSelectActivity;
import com.pm360.cepm360.app.common.activity.OwnerSelectActivity;
import com.pm360.cepm360.app.common.custinterface.CommonListInterface;
import com.pm360.cepm360.app.common.custinterface.DialogAdapterInterface;
import com.pm360.cepm360.app.common.custinterface.ServiceInterface;
import com.pm360.cepm360.app.common.view.parent.BaseDialog;
import com.pm360.cepm360.app.module.common.attachment.AttachmentActivity;
import com.pm360.cepm360.app.module.common.attachment.AttachmentFragment;
import com.pm360.cepm360.app.module.common.attachment.AttachmentReadOnlyActivity;
import com.pm360.cepm360.app.module.schedule.SendEmailFragment;
import com.pm360.cepm360.common.GLOBAL;
import com.pm360.cepm360.common.util.LogUtil;
import com.pm360.cepm360.entity.User;
import com.pm360.cepm360.entity.ZH_group_risk;
import com.pm360.cepm360.entity.ZH_group_task;
import com.pm360.cepm360.entity.Zh_fkzb;
import com.pm360.cepm360.services.group.RemoteRiskService;

import java.util.HashMap;
import java.util.Map;

public class RiskFragment extends SendEmailFragment<ZH_group_risk, ZH_group_task> {

	private RemoteRiskService mService;
	private boolean mIsCurrentUser = true;

	private String[] mTitleString = new String[] {
			SERIAL_NUMBER,
			"risk_number",
			"risk_name",
			"problem_description",
			"fix_result",
			"fix_company",
			"check_user",
			"check_date",
			"attachment",
	};
	
	public static final String[] mDialogString = new String[] {
			"risk_number",
			"risk_name",
			"problem_description",
			"fix_result",
			"fix_company",
			"check_user",
			"check_date",
	};
	
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
	
	@Override
	public void handleParentEvent(ZH_group_task b){
		super.handleParentEvent(b);
		if (b != null && (UserCache.getCurrentUser().getUser_id() != b.getOwner())) {
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
		// 初始化类型和接口
		init(ZH_group_risk.class, mListInterface, mRequestInterface, null, null,
				mDialogInterface);

		setPermissionIdentity(GLOBAL.SYS_ACTION[53][0],
				GLOBAL.SYS_ACTION[52][0]);
		mService = RemoteRiskService.getInstance();
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

	CommonListInterface<ZH_group_risk> mListInterface = new CommonListInterface<ZH_group_risk>() {

		@Override
		public Map<String, Map<String, String>> getDisplayFieldsSwitchMap() {
			Map<String, Map<String, String>> map = new HashMap<String, Map<String, String>>();
			map.put("check_user", UserCache.getUserMaps());
			map.put("fix_company", ObsCache.getObsIdMaps());
			return map;
		}

		@Override
		public int getListItemId(ZH_group_risk t) {
			return t.getZh_group_risk_id();
		}

		@Override
		public String[] getDisplayFeilds() {
			return mTitleString;
		}

		@Override
		public int getListHeaderLayoutId() {
			return R.layout.combination_risk_list_item;
		}

		@Override
		public int getListLayoutId() {
			return R.layout.combination_risk_list_item;
		}

		@Override
		public int getListHeaderNames() {
			return R.array.combination_risk_list_names;
		}

		@Override
		public int getListHeaderIds() {
			return R.array.combination_risk_list_ids;
		}

		@Override
		public int getListItemIds() {
			return R.array.combination_risk_list_ids;
		}

	};
	
	ServiceInterface<ZH_group_risk> mRequestInterface = new ServiceInterface<ZH_group_risk>() {

		@Override
		public void getListData() {
			if (checkParentBeanForQuery()) {
				mService.getRiskList(getServiceManager(), mParentBean.getTask_id());
			}
		}

		@Override
		public void addItem(ZH_group_risk t) {
			if (mParentBean != null) {
				t.setTask_id(mParentBean.getTask_id());
				t.setZh_group_id(mParentBean.getZh_group_id());
				mService.addRisk(getServiceManager(), t);
			}
		}

		@Override
		public void deleteItem(ZH_group_risk t) {
			mService.deleteRisk(getServiceManager(), t.getZh_group_risk_id());
		}

		@Override
		public void updateItem(ZH_group_risk t) {
			mService.updateRisk(getServiceManager(), t);
		}

	};
	DialogAdapterInterface mDialogInterface = new DialogAdapterInterface() {

		@Override
		public int getDialogTitleId() {
			return R.string.combination_risk_add_dialog;
		}

		@Override
		public int getDialogLableNames() {
			return R.array.combination_risk_dialog;
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
			buttons.put(0, BaseDialog.editTextClickLineStyle);
			buttons.put(2, BaseDialog.remarkEditTextLineStyle);
			buttons.put(3, BaseDialog.remarkEditTextLineStyle);
			buttons.put(4, BaseDialog.OBSSelectLineStyle);
			buttons.put(5, BaseDialog.userSelectLineStyle);
			buttons.put(6, BaseDialog.calendarLineStyle);
			return buttons;
		}

		@Override
		public Map<Integer, String[]> getSupplyData() {
			// TODO Auto-generated method stub
			return null;
		}

		@Override
		public void additionalInit(BaseDialog dialog) {
			
			OnClickListener ownerListener = new OnClickListener() {

				@Override
				public void onClick(View v) {
					Intent intent = new Intent();
					intent.setClass(v.getContext(), OwnerSelectActivity.class);
					intent.putExtra("title", getString(R.string.owner));
					intent.putExtra("project", mCurrentProject);
					startActivityForResult(intent, OWNER_SELECT_REQUEST);
				}
			};
			mDialog.setEditTextStyle(5, 0, ownerListener, null);
			
			OnClickListener riskControlListener = new OnClickListener() {
				
				@Override
				public void onClick(View v) {
					Intent intent = new Intent();
                    intent.setClass(getActivity(), ListSelectActivity.class);
                    intent.putExtra(ListSelectActivity.FRAGMENT_KEY, RiskControlFragment.class);
                    intent.putExtra(ListSelectActivity.PARENT_BEAN_KEY, mParentBean);
                    startActivityForResult(intent, RISK_CONTROL_SELECT_CODE);
				}
			};
			mDialog.setEditTextStyle(0, 0, riskControlListener, null);
			
		}

		@Override
		public Map<String, Map<String, String>> getUpdateFieldsSwitchMap() {
			return null;
		}

	};
	
	private final int OWNER_SELECT_REQUEST = 222;
	private final int RISK_CONTROL_SELECT_CODE = 221;
	@Override
	public void onActivityResult(int requestCode, int resultCode, Intent data) {
		super.onActivityResult(requestCode, resultCode, data);
		if (resultCode == 0)
			return;
		if (requestCode == OWNER_SELECT_REQUEST) {
			User owner = (User) data.getSerializableExtra("user");
			mDialog.setUserTextContent(5, owner.getUser_id());
		} else if (requestCode == RISK_CONTROL_SELECT_CODE) {
			Zh_fkzb fkzb = (Zh_fkzb) data.getSerializableExtra(ListSelectActivity.RESULT_KEY);
			mDialog.setEditTextContent(0, fkzb.getTitle());
		}
	}

	@Override
	protected int getDocumentType() {
		return AttachmentFragment.ZH_RISK_TYPE;
	}
	
	@Override
	protected int getAttachPosition() {
		return 8;
	}

	@Override
	protected int getEmailTitleType() {
		return getDocumentType();
	}
	
	@Override
	protected boolean dialogSaveButtonEvent() {
		mSaveData = mDialog.SaveData();
		LogUtil.i("wzw saveData:" + mSaveData);
		if (mSaveData.get(mDialogLableNames[0]).isEmpty()
				|| mSaveData.get(mDialogLableNames[1]).isEmpty()
				|| mSaveData.get(mDialogLableNames[2]).isEmpty()
				|| mSaveData.get(mDialogLableNames[3]).isEmpty()
				|| mSaveData.get(mDialogLableNames[4]).isEmpty()
				|| mSaveData.get(mDialogLableNames[5]).isEmpty()
				|| mSaveData.get(mDialogLableNames[6]).isEmpty()) {
			sendMessage(SHOW_TOAST, 
					getResources().getString(R.string.pls_input_all_info));
			return false;
		} else {
			return super.dialogSaveButtonEvent();
		}
	}

	private boolean mEmailDialogStatus;
	@Override
	protected String[] getDefaultDialogValue() {
		if (mEmailDialogStatus) { 
			mEmailDialogStatus = false;
			String[] values = super.getDefaultDialogValue();
			values[4] = ObsCache.getObsIdMaps().get(values[4]);
			values[5] = UserCache.getUserMaps().get(values[5]);
			return values;
		} else {
			return super.getDefaultDialogValue();
		}
	}
	@Override
	protected void writeEmail() {
		mEmailDialogStatus = true;
		super.writeEmail();
	}
	
	@Override
	public boolean isChildHandleFloatingMenuOnly() {
		return true;
	}
}

