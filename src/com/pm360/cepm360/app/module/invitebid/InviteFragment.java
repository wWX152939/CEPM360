package com.pm360.cepm360.app.module.invitebid;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.pm360.cepm360.R;
import com.pm360.cepm360.app.CepmApplication;
import com.pm360.cepm360.app.common.activity.ListSelectActivity;
import com.pm360.cepm360.app.common.custinterface.CommonListInterface;
import com.pm360.cepm360.app.common.custinterface.DialogAdapterInterface;
import com.pm360.cepm360.app.common.custinterface.ServiceInterface;
import com.pm360.cepm360.app.common.view.parent.BaseDialog;
import com.pm360.cepm360.app.module.resource.ContactCompanyFragment;
import com.pm360.cepm360.common.GLOBAL;
import com.pm360.cepm360.common.util.LogUtil;
import com.pm360.cepm360.entity.P_LWDW;
import com.pm360.cepm360.entity.ZB_invite;
import com.pm360.cepm360.services.invitebid.RemoteZBProcessService;

import java.util.HashMap;
import java.util.Map;

/**
 * 邀标单位
 * 
 * 标题: InviteFragment 
 * 描述: 
 * 作者： Administrator
 * 日期： 2016-1-21
 *
 */
public class InviteFragment extends BaseCommonFragment<ZB_invite> {
    private RemoteZBProcessService mService;
    
    private String[] mTableHeader = new String[] {
            SERIAL_NUMBER,
            "company_name",
            "key_person",
            "tel",
            "aptitude_desc",
            "attachment",
    };
    
    private String[] mDialogLabel = new String[] {
            "company_name",
            "key_person",
            "tel",
            "aptitude_desc",
    };

    /**
     * 创建视图
     */
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
            Bundle savedInstanceState) {
        // 初始化类型和接口
        init(ZB_invite.class, mListInterface, mRequestInterface, null, null,
                mDialogInterface);

        setPermissionIdentity(GLOBAL.SYS_ACTION[51][0],
                GLOBAL.SYS_ACTION[50][0]);
        mService = RemoteZBProcessService.getInstance();
        mApplication = (CepmApplication) getActivity().getApplication();

        return super.onCreateView(inflater, container, savedInstanceState);
    }
    
    CommonListInterface<ZB_invite> mListInterface = new CommonListInterface<ZB_invite>() {

        @Override
        public Map<String, Map<String, String>> getDisplayFieldsSwitchMap() {
            return null;
        }

        @Override
        public int getListItemId(ZB_invite t) {
            return t.getZb_invite_id();
        }

        @Override
        public String[] getDisplayFeilds() {
            return mTableHeader;
        }

        @Override
        public int getListHeaderLayoutId() {
            return R.layout.invitebid_invite_list_item;
        }

        @Override
        public int getListLayoutId() {
            return R.layout.invitebid_invite_list_item;
        }

        @Override
        public int getListHeaderNames() {
            return R.array.invitebid_invite_list_names;
        }

        @Override
        public int getListHeaderIds() {
            return R.array.invitebid_invite_list_ids;
        }

        @Override
        public int getListItemIds() {
            return R.array.invitebid_invite_list_ids;
        }
        
    };
    
    ServiceInterface<ZB_invite> mRequestInterface = new ServiceInterface<ZB_invite>() {

        @Override
        public void getListData() {
            if (checkParentBeanForQuery()) {
                mService.getZBInviteList(getServiceManager(), mCurrentProject.getProject_id(), mParentBean.getZb_plan_id());
            }
        }

        @Override
        public void addItem(ZB_invite t) {
            if (mParentBean != null) {
            	if (mIsLWDWSelected) {
            		t.setCompany_id(mLWcompany.getLwdw_id());
            	}
                t.setZb_plan_id(mParentBean.getZb_plan_id());
                mService.addZBInvite(getServiceManager(), t);
            }
        }

        @Override
        public void deleteItem(ZB_invite t) {
            mService.deleteZBInvite(getServiceManager(), t.getZb_invite_id());            
        }

        @Override
        public void updateItem(ZB_invite t) {
        	if (mIsLWDWSelected) {
        		t.setCompany_id(mLWcompany.getLwdw_id());
        	}
            mService.updateZBInvite(getServiceManager(), t);
            
        }        
    };
    
    DialogAdapterInterface mDialogInterface = new DialogAdapterInterface() {

        @Override
        public int getDialogTitleId() {
            return R.string.invitebid_invite_add_dialog;
        }

        @Override
        public int getDialogLableNames() {
            return R.array.invitebid_invite_dialog;
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
            buttons.put(0, BaseDialog.editTextClickLineStyle);
            buttons.put(1, BaseDialog.editTextReadOnlyLineStyle);
            buttons.put(2, BaseDialog.editTextReadOnlyLineStyle);
            buttons.put(3, BaseDialog.remarkEditTextLineStyle);
            return buttons;
        }

        @Override
        public Map<Integer, String[]> getSupplyData() {
            // TODO Auto-generated method stub
            return null;
        }

        @Override
        public void additionalInit(BaseDialog dialog) {
            View.OnClickListener companyListener = new View.OnClickListener() {

                @Override
                public void onClick(View v) {
                    Intent intent = new Intent();
                    intent.setClass(getActivity(), ListSelectActivity.class);
                    intent.putExtra(ListSelectActivity.FRAGMENT_KEY, ContactCompanyFragment.class);
                    intent.putExtra(ListSelectActivity.SELECT_MODE_KEY,
                            ListSelectActivity.SINGLE_SELECT);
                    intent.putExtra("title", getString(R.string.invitebid_invite_company));
                    getParentFragment().startActivityForResult(intent, INVITE_LWDW_COMPANY_REQUEST + getRatio());
                }
            };
            mDialog.setEditTextStyle(0, 0, companyListener, null);    
        }

        @Override
        public Map<String, Map<String, String>> getUpdateFieldsSwitchMap() {
            return null;
        }
        
    };
    
    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {  
    	LogUtil.i("wzw requestCode:" + requestCode + " resultCode:"
				+ resultCode + " mCurrentItem" + mCurrentItem);
        if (resultCode == Activity.RESULT_OK
                && requestCode == INVITE_LWDW_COMPANY_REQUEST + getRatio()) {
        	mIsLWDWSelected = true;
        	mLWcompany = (P_LWDW) data
                    .getSerializableExtra(ListSelectActivity.RESULT_KEY);
            // 将项目的名称，联系人，电话 更新到Dialog上
            mDialog.setEditTextContent(0, mLWcompany.getName());
            mDialog.setEditTextContent(1, mLWcompany.getKey_person());
            mDialog.setEditTextContent(2, mLWcompany.getTel());
        }
        super.onActivityResult(requestCode, resultCode, data);
    }
    
	@Override
	protected int getDocumentType() {
		return Integer.parseInt(GLOBAL.FILE_TYPE[19][0]);
	}

	@Override
	protected int getAttachPosition() {
		return 5;
	}
	
	@Override
	protected boolean disableFloatingMenu() {
		return false;
	}

    
    private boolean mIsLWDWSelected;
    private P_LWDW mLWcompany;
	@Override
	protected boolean doExtraAddFloatingMenuEvent() {
		mIsLWDWSelected = false;
		return super.doExtraAddFloatingMenuEvent();
	}
	
	@Override
	protected void showUpdateDialog(boolean isEdit) {
		mIsLWDWSelected = false;
		super.showUpdateDialog(isEdit);
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

}