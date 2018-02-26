package com.pm360.cepm360.app.module.invitebid;

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
import com.pm360.cepm360.entity.ZB_pretrial;
import com.pm360.cepm360.services.invitebid.RemoteZBProcessService;

import java.util.HashMap;
import java.util.Map;

/**
 * 预审
 * 
 * 标题: PretrialFragment 
 * 描述: 
 * 作者： Administrator
 * 日期： 2016-1-21
 *
 */
public class PretrialFragment extends BaseCommonFragment<ZB_pretrial> {
    private RemoteZBProcessService mService;
    private Map<String, String> mPassMap;
    
    private String[] mTableHeader = new String[] {
            SERIAL_NUMBER,
            "company_name",
            "review_export_name",
            "auditing_status",
            "auditing_content",
            "attachment"
    };
    
    private String[] mDialogLabel = new String[] {
            "company_name",
            "review_export_name",
            "auditing_status",
            "auditing_content"
    };

	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {
		initEnvironment();
        // 初始化类型和接口
        init(ZB_pretrial.class, mListInterface, mRequestInterface, null, mUpdateOptionMenuImplement,
                mDialogInterface);

        setPermissionIdentity(GLOBAL.SYS_ACTION[51][0],
                GLOBAL.SYS_ACTION[50][0]);
        //mService = RemoteRiskService.getInstance();
        mApplication = (CepmApplication) getActivity().getApplication();
        mService = RemoteZBProcessService.getInstance();
        
        return super.onCreateView(inflater, container, savedInstanceState);
	}
	
	private void initEnvironment() {
    	mPassMap = new HashMap<String, String>();
    	mPassMap.put("1", getString(R.string.pass));
    	mPassMap.put(getString(R.string.pass), "1");
    }
	
	CommonListInterface<ZB_pretrial> mListInterface = new CommonListInterface<ZB_pretrial>() {

        @Override
        public Map<String, Map<String, String>> getDisplayFieldsSwitchMap() {
        	Map<String, Map<String, String>> map = new HashMap<String, Map<String, String>>();
        	map.put("auditing_status", mPassMap);
            return map;
        }

        @Override
        public int getListItemId(ZB_pretrial t) {
            return t.getZb_pretrial_id();
        }

        @Override
        public String[] getDisplayFeilds() {
            return mTableHeader;
        }

        @Override
        public int getListHeaderLayoutId() {
            return R.layout.invitebid_pretrial_list_item;
        }

        @Override
        public int getListLayoutId() {
            return R.layout.invitebid_pretrial_list_item;
        }

        @Override
        public int getListHeaderNames() {
            return R.array.invitebid_pretrial_list_names;
        }

        @Override
        public int getListHeaderIds() {
            return R.array.invitebid_pretrial_list_ids;
        }

        @Override
        public int getListItemIds() {
            return R.array.invitebid_pretrial_list_ids;
        }
	    
	};
	
	ServiceInterface<ZB_pretrial> mRequestInterface = new ServiceInterface<ZB_pretrial>() {

        @Override
        public void getListData() {
            if (checkParentBeanForQuery()) {
                mService.getZBPretrialList(getServiceManager(), mParentBean.getZb_plan_id());
            }
        }

        @Override
        public void addItem(ZB_pretrial t) {
        }

        @Override
        public void deleteItem(ZB_pretrial t) {
        }

        @Override
        public void updateItem(ZB_pretrial t) {
        	if (mIsMavinSelected) {
        		t.setReview_export(mMavinId);
        	}
            mService.zbPretrial(getServiceManager(), t);
        }
	    
	};
	
	DialogAdapterInterface mDialogInterface = new DialogAdapterInterface() {

        @Override
        public int getDialogTitleId() {
            return R.string.invitebid_pretrial_add_dialog;
        }

        @Override
        public int getDialogLableNames() {
            return R.array.invitebid_pretrial_dialog;
        }

        @Override
        public String[] getUpdateFeilds() {
            return mDialogLabel;
        }

        @Override
        public Integer[] getImportantColumns(Map<String, String> saveDataMap) {
        	return null;
        }

        @Override
        public Map<Integer, Integer> getDialogStyles() {
          Map<Integer, Integer> buttons = new HashMap<Integer, Integer>();
          buttons.put(0, BaseDialog.editTextReadOnlyLineStyle);
          buttons.put(1, BaseDialog.editTextClickLineStyle);
          buttons.put(2, BaseDialog.checkboxLineStyle);
          buttons.put(3, BaseDialog.remarkEditTextLineStyle);
          return buttons;
        }

        @Override
        public Map<Integer, String[]> getSupplyData() {
        	Map<Integer, String[]> map = new HashMap<Integer, String[]>();
        	map.put(2, new String[] {getString(R.string.pass)});
            return map;
        }

        @Override
        public void additionalInit(BaseDialog dialog) {
			
			OnClickListener mavinListener = new OnClickListener() {
				
				@Override
				public void onClick(View v) {
					Intent intent = new Intent();
                    intent.setClass(getActivity(), ListSelectActivity.class);
                    intent.putExtra(ListSelectActivity.FRAGMENT_KEY, MavinFragment.class);
                    intent.putExtra(ListSelectActivity.PARENT_BEAN_KEY,
                    		mParentBean);
                    getParentFragment().startActivityForResult(intent, PRETRIAL_MAVIN_REQUEST + getRatio());
				}
			};
			
            dialog.setEditTextStyle(1, 0, mavinListener, "");
        }

        @Override
        public Map<String, Map<String, String>> getUpdateFieldsSwitchMap() {
            return mListInterface.getDisplayFieldsSwitchMap();
        }
	    
	};
	
    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
    	if (resultCode != Activity.RESULT_OK) {
			return;
		}
		if (requestCode == PRETRIAL_MAVIN_REQUEST + getRatio()){
			mIsMavinSelected = true;
			ZB_mavin t = (ZB_mavin) data.getSerializableExtra(ListSelectActivity.RESULT_KEY);
			mMavinId = t.getZb_mavin_id();
			mDialog.setEditTextContent(1, t.getName());
		}

        super.onActivityResult(requestCode, resultCode, data);
    }

    @Override
    protected int getDocumentType() {
        return Integer.parseInt(GLOBAL.FILE_TYPE[21][0]);
    }

    @Override
    protected int getAttachPosition() {
        return 5;
    }
    
    @Override
	protected boolean disableFloatingMenu() {
		return true;
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
		if (mSaveData.get(mDialogLableNames[1]).isEmpty()) {
			sendMessage(SHOW_TOAST, 
					getResources().getString(R.string.pls_input_all_info));
			return false;
		} else {
			return super.dialogSaveButtonEvent();
		}
	}

}
