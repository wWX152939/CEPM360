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
import com.pm360.cepm360.entity.ZB_answer;
import com.pm360.cepm360.services.invitebid.RemoteZBProcessService;

import java.util.HashMap;
import java.util.Map;

/**
 * 答疑
 * @author onekey
 *
 */
public class AnswerFragment extends BaseCommonFragment<ZB_answer> {

	private RemoteZBProcessService mService;

	private String[] mTitleString = new String[] {
			SERIAL_NUMBER,
			"company_name",
			"answer_time",
			"answer_address",
			"partake_person",
			"answer_content",
			"attachment",
	};
	
	private String[] mDialogString = new String[] {
			"company_name",
			"answer_time",
			"answer_address",
			"partake_person",
			"answer_content",
			"other",
	};
	/**
	 * 创建视图
	 */
	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {
		// 初始化类型和接口
		init(ZB_answer.class, mListInterface, mRequestInterface, null, mUpdateOptionMenuImplement,
				mDialogInterface);

		setPermissionIdentity(GLOBAL.SYS_ACTION[51][0],
				GLOBAL.SYS_ACTION[50][0]);
		mService = RemoteZBProcessService.getInstance();
		mApplication = (CepmApplication) getActivity().getApplication();

		return super.onCreateView(inflater, container, savedInstanceState);
	}
	
	CommonListInterface<ZB_answer> mListInterface = new CommonListInterface<ZB_answer>(){

		@Override
		public Map<String, Map<String, String>> getDisplayFieldsSwitchMap() {
			return null;
		}

		@Override
		public int getListItemId(ZB_answer t) {
			return t.getZb_answer_id();
		}

		@Override
		public String[] getDisplayFeilds() {
			return mTitleString;
		}

		@Override
		public int getListHeaderLayoutId() {
			return R.layout.invitebid_answer_list_item;
		}

		@Override
		public int getListLayoutId() {
			return R.layout.invitebid_answer_list_item;
		}

		@Override
		public int getListHeaderNames() {
			return R.array.invitebid_answer_list_names;
		}

		@Override
		public int getListHeaderIds() {
			return R.array.invitebid_answer_list_ids;
		}

		@Override
		public int getListItemIds() {
			return R.array.invitebid_answer_list_ids;
		}};	
		
	ServiceInterface<ZB_answer> mRequestInterface = new ServiceInterface<ZB_answer>() {

		@Override
		public void getListData() {
			 if (checkParentBeanForQuery()) {
				 mService.getZBAnswerList(getServiceManager(), mParentBean.getZb_plan_id());
			 }
		}

		@Override
		public void addItem(ZB_answer t) {
		}

		@Override
		public void deleteItem(ZB_answer t) {
		}

		@Override
		public void updateItem(ZB_answer t) {
			mService.zbAnswer(getServiceManager(), t);
		}};		
		
	DialogAdapterInterface mDialogInterface = new DialogAdapterInterface() {

		@Override
		public int getDialogTitleId() {
			return R.string.invitebid_answer_dialog_title;
		}

		@Override
		public int getDialogLableNames() {
			return R.array.invitebid_answer_dialog;
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
			buttons.put(1, BaseDialog.calendarLineStyle);
			buttons.put(4, BaseDialog.remarkEditTextLineStyle);
			buttons.put(5, BaseDialog.remarkEditTextLineStyle);
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
			return mListInterface.getDisplayFieldsSwitchMap();
		}};		

	@Override
	protected int getDocumentType() {
		return Integer.parseInt(GLOBAL.FILE_TYPE[22][0]);
	}

	@Override
	protected int getAttachPosition() {
		return 6;
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
				|| mSaveData.get(mDialogLableNames[3]).isEmpty()) {
			sendMessage(SHOW_TOAST, 
					getResources().getString(R.string.pls_input_all_info));
			return false;
		} else {
			return super.dialogSaveButtonEvent();
		}
	}

}

