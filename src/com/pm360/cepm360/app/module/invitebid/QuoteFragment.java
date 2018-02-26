package com.pm360.cepm360.app.module.invitebid;

import android.annotation.SuppressLint;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.pm360.cepm360.R;
import com.pm360.cepm360.app.CepmApplication;
import com.pm360.cepm360.app.common.custinterface.CommonListInterface;
import com.pm360.cepm360.app.common.custinterface.DialogAdapterInterface;
import com.pm360.cepm360.app.common.custinterface.ServiceInterface;
import com.pm360.cepm360.app.common.view.parent.BaseDialog;
import com.pm360.cepm360.common.GLOBAL;
import com.pm360.cepm360.entity.ZB_quote;
import com.pm360.cepm360.services.invitebid.RemoteZBProcessService;

import java.util.HashMap;
import java.util.Map;

/**
 * 报价
 * @author onekey
 *
 */
public class QuoteFragment extends BaseCommonFragment<ZB_quote> {

	private RemoteZBProcessService mService;

	private String[] mTitleString = new String[] {
			SERIAL_NUMBER,
			"company_name",
			"bid_dep",
			"bid_person",
			"bid_date",
			"quote_date",
			"bid_price",
			"project_period",
			"mark",
			"attachment",			
	};
	
	private String[] mDialogString = new String[] {
			"company_name",
			"bid_dep",
			"bid_person",
			"quote_date",
			"bid_date",
			"bid_price",
			"project_period",
			"tel",
			"address",
			"unit",
			"mark",			
	};
	/**
	 * 创建视图
	 */
	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {
		// 初始化类型和接口
		init(ZB_quote.class, mListInterface, mRequestInterface, null, mUpdateOptionMenuImplement,
				mDialogInterface);

		setPermissionIdentity(GLOBAL.SYS_ACTION[51][0],
				GLOBAL.SYS_ACTION[50][0]);
		mService = RemoteZBProcessService.getInstance();
		mApplication = (CepmApplication) getActivity().getApplication();

		return super.onCreateView(inflater, container, savedInstanceState);
	}
	
	CommonListInterface<ZB_quote> mListInterface = new CommonListInterface<ZB_quote>(){

		@Override
		public Map<String, Map<String, String>> getDisplayFieldsSwitchMap() {
			return mDialogInterface.getUpdateFieldsSwitchMap();
		}

		@Override
		public int getListItemId(ZB_quote t) {
			return t.getZb_quote_id();
		}

		@Override
		public String[] getDisplayFeilds() {
			return mTitleString;
		}

		@Override
		public int getListHeaderLayoutId() {
			return R.layout.invitebid_quote_list_item;
		}

		@Override
		public int getListLayoutId() {
			return R.layout.invitebid_quote_list_item;
		}

		@Override
		public int getListHeaderNames() {
			return R.array.invitebid_quote_list_names;
		}

		@Override
		public int getListHeaderIds() {
			return R.array.invitebid_quote_list_ids;
		}

		@Override
		public int getListItemIds() {
			return R.array.invitebid_quote_list_ids;
		}};	
		
	ServiceInterface<ZB_quote> mRequestInterface = new ServiceInterface<ZB_quote>() {

		@Override
		public void getListData() {
			if (checkParentBeanForQuery()) {			
				mService.getZBQuote(getServiceManager(), mParentBean.getZb_plan_id());
			}
		}

		@Override
		public void addItem(ZB_quote t) {
		}

		@Override
		public void deleteItem(ZB_quote t) {
		}

		@Override
		public void updateItem(ZB_quote t) {
			mService.zbQuote(getServiceManager(), t);
		}};		
		
	DialogAdapterInterface mDialogInterface = new DialogAdapterInterface() {

		@Override
		public int getDialogTitleId() {
			return R.string.invitebid_quote_dialog_title;
		}

		@Override
		public int getDialogLableNames() {
			return R.array.invitebid_quote_dialog;
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
			buttons.put(3, BaseDialog.calendarLineStyle);
			buttons.put(4, BaseDialog.calendarLineStyle);
			buttons.put(10, BaseDialog.remarkEditTextLineStyle);
			return buttons;
		}

		@SuppressLint("UseSparseArrays") @Override
		public Map<Integer, String[]> getSupplyData() {
			return null;
		}

		@Override
		public void additionalInit(BaseDialog dialog) {			
		}

		@Override
		public Map<String, Map<String, String>> getUpdateFieldsSwitchMap() {
			return null;
		}};		

	@Override
	protected int getDocumentType() {
		return Integer.parseInt(GLOBAL.FILE_TYPE[23][0]);
	}

	@Override
	protected int getAttachPosition() {
		return 9;
	}
	
	@Override
	protected boolean disableFloatingMenu() {
		return true;
	}
	
	@Override
	protected boolean dialogSaveButtonEvent() {
		mSaveData = mDialog.SaveData();
		if (mSaveData.get(mDialogLableNames[1]).isEmpty()
				|| mSaveData.get(mDialogLableNames[2]).isEmpty()
				|| mSaveData.get(mDialogLableNames[3]).isEmpty()
				|| mSaveData.get(mDialogLableNames[4]).isEmpty()
				|| mSaveData.get(mDialogLableNames[5]).isEmpty()) {
			sendMessage(SHOW_TOAST, 
					getResources().getString(R.string.pls_input_all_info));
			return false;
		} else {
			return super.dialogSaveButtonEvent();
		}
	}
}

