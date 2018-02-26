package com.pm360.cepm360.app.module.invitebid;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;

import com.pm360.cepm360.R;
import com.pm360.cepm360.app.CepmApplication;
import com.pm360.cepm360.app.common.activity.ListSelectActivity;
import com.pm360.cepm360.app.common.custinterface.CommonListInterface;
import com.pm360.cepm360.app.common.custinterface.DialogAdapterInterface;
import com.pm360.cepm360.app.common.custinterface.ServiceInterface;
import com.pm360.cepm360.app.common.view.parent.BaseDialog;
import com.pm360.cepm360.common.GLOBAL;
import com.pm360.cepm360.entity.ZB_mavin;
import com.pm360.cepm360.entity.ZB_score;
import com.pm360.cepm360.services.invitebid.RemoteZBProcessService;

import java.util.HashMap;
import java.util.Map;

/**
 * 评分
 * @author onekey
 *
 */
public class ScoreFragment extends BaseCommonFragment<ZB_score> {

	private RemoteZBProcessService mService;

	private String[] mTitleString = new String[] {
			SERIAL_NUMBER,
			"company_name",
			"review_export_name",
			"tec_score",
			"bus_score",
			"mark",
			"attachment",			
	};
	
	private String[] mDialogString = new String[] {
			"company_name",
			"review_export_name",
			"tec_score",
			"bus_score",
			"mark",		
	};
	/**
	 * 创建视图
	 */
	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {
		// 初始化类型和接口
		init(ZB_score.class, mListInterface, mRequestInterface, null, mUpdateOptionMenuImplement,
				mDialogInterface);

		setPermissionIdentity(GLOBAL.SYS_ACTION[51][0],
				GLOBAL.SYS_ACTION[50][0]);
		mService = RemoteZBProcessService.getInstance();
		mApplication = (CepmApplication) getActivity().getApplication();

		return super.onCreateView(inflater, container, savedInstanceState);
	}
	
	CommonListInterface<ZB_score> mListInterface = new CommonListInterface<ZB_score>(){

		@Override
		public Map<String, Map<String, String>> getDisplayFieldsSwitchMap() {
			return mDialogInterface.getUpdateFieldsSwitchMap();
		}

		@Override
		public int getListItemId(ZB_score t) {
			// TODO Auto-generated method stub
			return t.getZb_score_id();
		}

		@Override
		public String[] getDisplayFeilds() {
			// TODO Auto-generated method stub
			return mTitleString;
		}

		@Override
		public int getListHeaderLayoutId() {
			// TODO Auto-generated method stub
			return R.layout.invitebid_score_list_item;
		}

		@Override
		public int getListLayoutId() {
			// TODO Auto-generated method stub
			return R.layout.invitebid_score_list_item;
		}

		@Override
		public int getListHeaderNames() {
			// TODO Auto-generated method stub
			return R.array.invitebid_score_list_names;
		}

		@Override
		public int getListHeaderIds() {
			// TODO Auto-generated method stub
			return R.array.invitebid_score_list_ids;
		}

		@Override
		public int getListItemIds() {
			// TODO Auto-generated method stub
			return R.array.invitebid_score_list_ids;
		}};	
		
	ServiceInterface<ZB_score> mRequestInterface = new ServiceInterface<ZB_score>() {

		@Override
		public void getListData() {
			if (checkParentBeanForQuery()) {
				mService.getZBScore(getServiceManager(), mParentBean.getZb_plan_id());
			}
		}

		@Override
		public void addItem(ZB_score t) {
		}

		@Override
		public void deleteItem(ZB_score t) {
		}

		@Override
		public void updateItem(ZB_score t) {
			if (mIsMavinSelected) {
				t.setReview_export(mMavinId);
			}
			mService.zbScore(getServiceManager(), t);
			
		}};		
		
	DialogAdapterInterface mDialogInterface = new DialogAdapterInterface() {

		@Override
		public int getDialogTitleId() {
			return R.string.invitebid_score_dialog_title;
		}

		@Override
		public int getDialogLableNames() {
			return R.array.invitebid_score_dialog;
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
			buttons.put(0, BaseDialog.editTextReadOnlyLineStyle);
			buttons.put(1, BaseDialog.editTextClickLineStyle);
			buttons.put(4, BaseDialog.remarkEditTextLineStyle);
			return buttons;
		}

		@SuppressLint("UseSparseArrays") @Override
		public Map<Integer, String[]> getSupplyData() {
			return null;
		}

		@Override
		public void additionalInit(BaseDialog dialog) {
			OnClickListener ownerListener1 = new OnClickListener() {

				@Override
				public void onClick(View v) {
                    Intent intent = new Intent();
                    intent.setClass(getActivity(), ListSelectActivity.class);
                    intent.putExtra(ListSelectActivity.FRAGMENT_KEY, MavinFragment.class);
                    intent.putExtra(ListSelectActivity.PARENT_BEAN_KEY,
                    		mParentBean);
                    getParentFragment().startActivityForResult(intent, SCORE_MAVIN_REQUEST + getRatio());
				}
			};
			mDialog.setEditTextStyle(1, 0, ownerListener1, null);			
		}

		@Override
		public Map<String, Map<String, String>> getUpdateFieldsSwitchMap() {
			return null;
		}};		

	@Override
	protected int getDocumentType() {
		return Integer.parseInt(GLOBAL.FILE_TYPE[24][0]);
	}

	@Override
	protected int getAttachPosition() {
		return 6;
	}
	
	@Override
	protected boolean disableFloatingMenu() {
		return true;
	}
	
	public void onActivityResult(int requestCode, int resultCode, Intent data) {
		super.onActivityResult(requestCode, resultCode, data);
		if (resultCode != Activity.RESULT_OK) {
			return;
		}
		if (requestCode == SCORE_MAVIN_REQUEST + getRatio()){
			mIsMavinSelected = true;
			ZB_mavin t = (ZB_mavin) data.getSerializableExtra(ListSelectActivity.RESULT_KEY);
			mMavinId = t.getZb_mavin_id();
			mDialog.setEditTextContent(1, t.getName());
		}
	}	
	
	private boolean mIsMavinSelected;
    private int mMavinId;
	@Override
	protected boolean doExtraAddFloatingMenuEvent() {
		mIsMavinSelected = false;
		return super.doExtraAddFloatingMenuEvent();
	}
	
	@Override
	protected void showUpdateDialog(boolean isEdit) {
		mIsMavinSelected = false;
		super.showUpdateDialog(isEdit);
	}
	
	@Override
	protected boolean dialogSaveButtonEvent() {
		mSaveData = mDialog.SaveData();
		if (mSaveData.get(mDialogLableNames[1]).isEmpty()
				|| mSaveData.get(mDialogLableNames[2]).isEmpty()
				|| mSaveData.get(mDialogLableNames[3]).isEmpty()) {
			sendMessage(SHOW_TOAST, 
					getResources().getString(R.string.pls_input_all_info));
			return false;
		} else {
			return super.dialogSaveButtonEvent();
		}
	}
}

