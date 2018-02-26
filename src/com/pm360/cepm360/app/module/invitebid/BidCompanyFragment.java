package com.pm360.cepm360.app.module.invitebid;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.Dialog;
import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;

import com.pm360.cepm360.R;
import com.pm360.cepm360.app.CepmApplication;
import com.pm360.cepm360.app.common.activity.ListSelectActivity;
import com.pm360.cepm360.app.common.custinterface.CommonListInterface;
import com.pm360.cepm360.app.common.custinterface.DialogAdapterInterface;
import com.pm360.cepm360.app.common.custinterface.ServiceInterface;
import com.pm360.cepm360.app.common.view.parent.BaseDialog;
import com.pm360.cepm360.app.module.resource.ContactCompanyFragment;
import com.pm360.cepm360.common.GLOBAL;
import com.pm360.cepm360.common.util.MiscUtils;
import com.pm360.cepm360.entity.P_LWDW;
import com.pm360.cepm360.entity.ZB_bid_company;
import com.pm360.cepm360.entity.ZB_invite;
import com.pm360.cepm360.services.invitebid.RemoteZBProcessService;

import java.util.HashMap;
import java.util.Map;

/**
 * 投标单位
 * 
 * 标题: BidCompanyFragment 
 * 描述: 
 * 作者： Administrator
 * 日期： 2016-1-21
 *
 */
public class BidCompanyFragment extends BaseCommonFragment<ZB_bid_company> {
    private RemoteZBProcessService mService;
    private Dialog mPickDialog;
    private Map<String, String> mYesMap;
    
    private String[] mTableHeader = new String[] {
            SERIAL_NUMBER,
            "company_name",
            "key_person",
            "tel",
            "is_invite",
            "is_qualification",
            "aptitude_desc",
            "attachment",
    };
    
    private String[] mDialogLabel = new String[] {
            "company_name",
            "key_person",
            "tel",
            "is_invite",
            "is_qualification",
            "aptitude_desc",
    };
    
    /**
     * 创建视图
     */
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
            Bundle savedInstanceState) {
    	initEnvironment();
        // 初始化类型和接口
        init(ZB_bid_company.class, mListInterface, mRequestInterface, null, null,
                mDialogInterface);

        setPermissionIdentity(GLOBAL.SYS_ACTION[51][0],
                GLOBAL.SYS_ACTION[50][0]);
        mService = RemoteZBProcessService.getInstance();
        mApplication = (CepmApplication) getActivity().getApplication();
        initPickDialog();

        return super.onCreateView(inflater, container, savedInstanceState);
    }
    
    private void initEnvironment() {
    	mYesMap = new HashMap<String, String>();
    	mYesMap.put("0", getString(R.string.no));
    	mYesMap.put("1", getString(R.string.yes));
    	mYesMap.put(getString(R.string.yes), "1");
    	mYesMap.put(getString(R.string.no), "0");
    }
    
    CommonListInterface<ZB_bid_company> mListInterface = new CommonListInterface<ZB_bid_company>() {

        @Override
        public Map<String, Map<String, String>> getDisplayFieldsSwitchMap() {
        	Map<String, Map<String, String>> map = new HashMap<String, Map<String, String>>();
        	map.put("is_invite", mYesMap);
        	map.put("is_qualification", mYesMap);
            return map;
        }

        @Override
        public int getListItemId(ZB_bid_company t) {
            return t.getZb_bid_company_id();
        }

        @Override
        public String[] getDisplayFeilds() {
            return mTableHeader;
        }

        @Override
        public int getListHeaderLayoutId() {
            return R.layout.invitebid_bid_company_list_item;
        }

        @Override
        public int getListLayoutId() {
            return R.layout.invitebid_bid_company_list_item;
        }

        @Override
        public int getListHeaderNames() {
            return R.array.invitebid_bid_company_list_names;
        }

        @Override
        public int getListHeaderIds() {
            return R.array.invitebid_bid_company_list_ids;
        }

        @Override
        public int getListItemIds() {
            return R.array.invitebid_bid_company_list_ids;
        }
        
    };
    
    ServiceInterface<ZB_bid_company> mRequestInterface = new ServiceInterface<ZB_bid_company>() {

        @Override
        public void getListData() {
            if (checkParentBeanForQuery()) {
                mService.getZBBidList(getServiceManager(), mParentBean.getZb_plan_id());
            }
        }

        @Override
        public void addItem(ZB_bid_company t) {
            if (mParentBean != null) {
                t.setZb_plan_id(mParentBean.getZb_plan_id());
                mService.addZBBid(getServiceManager(), t);
            }
        }

        @Override
        public void deleteItem(ZB_bid_company t) {
            mService.deleteZBBid(getServiceManager(), t.getZb_bid_company_id());            
        }

        @Override
        public void updateItem(ZB_bid_company t) {
            mService.updateZBBid(getServiceManager(), t);            
        }        
    };
    
    DialogAdapterInterface mDialogInterface = new DialogAdapterInterface() {

        @Override
        public int getDialogTitleId() {
            return R.string.invitebid_bid_company_add_dialog;
        }

        @Override
        public int getDialogLableNames() {
            return R.array.invitebid_bid_company_dialog;
        }

        @Override
        public String[] getUpdateFeilds() {
            return mDialogLabel;
        }

        @Override
        public Integer[] getImportantColumns(Map<String, String> saveDataMap) {
        	return null;
        }

        @SuppressLint("UseSparseArrays") @Override
        public Map<Integer, Integer> getDialogStyles() {
            Map<Integer, Integer> buttons = new HashMap<Integer, Integer>();
            buttons.put(0, BaseDialog.editTextClickLineStyle);
            buttons.put(1, BaseDialog.editTextReadOnlyLineStyle);
            buttons.put(2, BaseDialog.editTextReadOnlyLineStyle);
            buttons.put(3, BaseDialog.editTextReadOnlyLineStyle);
            buttons.put(4, BaseDialog.checkboxLineStyle);
            buttons.put(5, BaseDialog.remarkEditTextLineStyle);
            return buttons;
        }

        @SuppressLint("UseSparseArrays") @Override
        public Map<Integer, String[]> getSupplyData() {
        	Map<Integer, String[]> map = new HashMap<Integer, String[]>();
        	map.put(4, new String[] {getString(R.string.yes)});
        	
            return map;
        }

        @Override
        public void additionalInit(BaseDialog dialog) {
            View.OnClickListener companyListener = new View.OnClickListener() {

                @Override
                public void onClick(View view) {
                    mPickDialog.show();
                }                
            };
            mDialog.setEditTextStyle(0, 0, companyListener, null);
        }

        @Override
        public Map<String, Map<String, String>> getUpdateFieldsSwitchMap() {
            return mListInterface.getDisplayFieldsSwitchMap();
        }
        
    };
    
    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {     
        if (resultCode == Activity.RESULT_OK) {
            if (requestCode == BID_INVITE_COMPANY_REQUEST + getRatio()) {
            	mIsLWDWSelected = true;
                ZB_invite company = (ZB_invite) data.getSerializableExtra(ListSelectActivity.RESULT_KEY); // TODO key
                // 将项目的名称，联系人，电话 更新到Dialog上
                mCompanyId = company.getCompany_id();
                mInviteId = company.getZb_invite_id();
                mDialog.setEditTextContent(0, company.getCompany_name());
                mDialog.setEditTextContent(1, company.getKey_person());
                mDialog.setEditTextContent(2, company.getTel());
                mDialog.setEditTextContent(3, getString(R.string.yes));
            }
            
            if (requestCode == BID_LWDW_COMPANY_REQUEST + getRatio()) {
            	mIsLWDWSelected = true;
                P_LWDW company = (P_LWDW) data
                        .getSerializableExtra(ListSelectActivity.RESULT_KEY);
                // 将项目的名称，联系人，电话 更新到Dialog上
                mCompanyId = company.getLwdw_id();
                mDialog.setEditTextContent(0, company.getName());
                mDialog.setEditTextContent(1, company.getKey_person());
                mDialog.setEditTextContent(2, company.getTel());
                mDialog.setEditTextContent(3, getString(R.string.no));
            }
        }
        super.onActivityResult(requestCode, resultCode, data);
    }
    
    private void initPickDialog() {
        View.OnClickListener listener = new View.OnClickListener() {

            @Override
            public void onClick(View view) {
                if (view.getId() == R.id.take_picture) {
                    // 从邀标单位中选择
                    Intent intent = new Intent();
                    intent.setClass(view.getContext(), ListSelectActivity.class);
                    intent.putExtra(ListSelectActivity.FRAGMENT_KEY, InviteFragment.class);
                    intent.putExtra(ListSelectActivity.PARENT_BEAN_KEY, mParentBean);
                    getRootFragment().startActivityForResult(intent, BID_INVITE_COMPANY_REQUEST + getRatio());
                } else {
                    // 从来往单位中选择
                    Intent intent = new Intent();
                    intent.setClass(view.getContext(), ListSelectActivity.class);
                    intent.putExtra(ListSelectActivity.FRAGMENT_KEY, ContactCompanyFragment.class);
                    intent.putExtra(ListSelectActivity.SELECT_MODE_KEY,
                            ListSelectActivity.SINGLE_SELECT);
                    intent.putExtra("title", getString(R.string.invitebid_bid_company));
                    getRootFragment().startActivityForResult(intent, BID_LWDW_COMPANY_REQUEST + getRatio());
                }
                mPickDialog.dismiss();
            }            
        };
        
        mPickDialog = new Dialog(getActivity(), R.style.MyDialogStyle);
        mPickDialog.setContentView(R.layout.change_attachment_pic_pick_dialog);
        
        Button inviteCompany = (Button) mPickDialog.findViewById(R.id.take_picture);
        Button lwdwCompany = (Button) mPickDialog.findViewById(R.id.local_picture);
        inviteCompany.setText(getString(R.string.invitebid_select_from_invite));
        lwdwCompany.setText(getString(R.string.invitebid_select_from_lwdw));
        inviteCompany.setOnClickListener(listener);
        lwdwCompany.setOnClickListener(listener);

        mPickDialog.setCanceledOnTouchOutside(true);        
    }
    
	@Override
	protected int getDocumentType() {
		return ZB_BID_TYPE;
	}

	@Override
	protected int getAttachPosition() {
		return 7;
	}
	
    @Override
	protected boolean disableFloatingMenu() {
		return false;
	}
    
    @Override
	protected void handleServerData(String attachment) {
    	if (mIsAddOperation) {	// 添加
			ZB_bid_company company = new ZB_bid_company();
			if (mSaveData.get(mDialogLableNames[3]).endsWith(getString(R.string.yes))) {
				if (mIsLWDWSelected) {
					company.setZb_invite_id(mInviteId);
					company.setCompany_id(mCompanyId);
					company.setIs_invite(1);
				}
				
			} else {
				if (mIsLWDWSelected) {
					company.setCompany_id(mCompanyId);
					company.setIs_invite(0);	
				}
			}
			if (mSaveData.get(mDialogLableNames[4]).endsWith(getString(R.string.yes))) {
				company.setIs_qualification(1);
			} else {
				company.setIs_qualification(0);
			}
			company.setKey_person(mSaveData.get(mDialogLableNames[1]));
			company.setTel(mSaveData.get(mDialogLableNames[2]));
			company.setAptitude_desc(mSaveData.get(mDialogLableNames[5]));
			if (attachment != null) {
				company.setAttachment(attachment);
			}
			
			mRequestInterface.addItem(company);
			
		} else {	// 修改
			mCurrentUpdateItem = MiscUtils.clone(mCurrentItem);
			if (mSaveData.get(mDialogLableNames[3]).endsWith(getString(R.string.yes))) {
				if (mIsLWDWSelected) {
					mCurrentUpdateItem.setZb_invite_id(mInviteId);
					mCurrentUpdateItem.setCompany_id(mCompanyId);
					mCurrentUpdateItem.setIs_invite(1);
				}
			} else {
				if (mIsLWDWSelected) {
					mCurrentUpdateItem.setCompany_id(mCompanyId);
					mCurrentUpdateItem.setIs_invite(0);
				}
			}
			if (mSaveData.get(mDialogLableNames[4]).endsWith(getString(R.string.yes))) {
				mCurrentUpdateItem.setIs_qualification(1);
			} else {
				mCurrentUpdateItem.setIs_qualification(0);
			}
			mCurrentUpdateItem.setKey_person(mSaveData.get(mDialogLableNames[1]));
			mCurrentUpdateItem.setTel(mSaveData.get(mDialogLableNames[2]));
			mCurrentUpdateItem.setAptitude_desc(mSaveData.get(mDialogLableNames[5]));
			if (attachment != null) {
				mCurrentUpdateItem.setAttachment(attachment);
			}
			mRequestInterface.updateItem(mCurrentUpdateItem);
		}
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
    
    private boolean mIsLWDWSelected;
    private int mCompanyId;
    private int mInviteId;
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
	
}

