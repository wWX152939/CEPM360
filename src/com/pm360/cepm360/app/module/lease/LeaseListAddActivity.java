package com.pm360.cepm360.app.module.lease;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import android.annotation.SuppressLint;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.EditText;
import com.pm360.cepm360.R;
import com.pm360.cepm360.app.cache.UserCache;
import com.pm360.cepm360.app.common.activity.DataManagerInterface;
import com.pm360.cepm360.app.common.activity.ListSelectActivity;
import com.pm360.cepm360.app.common.activity.OwnerSelectActivity;
import com.pm360.cepm360.app.common.custinterface.OptionMenuInterface;
import com.pm360.cepm360.app.common.custinterface.SimpleButtonInterface;
import com.pm360.cepm360.app.common.custinterface.SimpleDialogInterface;
import com.pm360.cepm360.app.common.custinterface.SimpleTicketServiceInterface;
import com.pm360.cepm360.app.common.custinterface.SimpleWindowInterface;
import com.pm360.cepm360.app.common.custinterface.SpecialDialogAdapterInterface;
import com.pm360.cepm360.app.common.custinterface.TicketListInterface;
import com.pm360.cepm360.app.common.custinterface.WindowAdapterInterface;
import com.pm360.cepm360.app.common.view.parent.BaseDialog;
import com.pm360.cepm360.app.common.view.parent.BaseWindow;
import com.pm360.cepm360.app.common.view.parent.DoubleDatePickerDialog;
import com.pm360.cepm360.app.module.contract.LeaseContractWithoutTreeFragment;
import com.pm360.cepm360.app.module.inventory.StoreHouseSelectActivity;
import com.pm360.cepm360.app.module.purchase.BaseTicketActivity;
import com.pm360.cepm360.app.module.resource.ContactCompanyFragment;
import com.pm360.cepm360.app.module.resource.LeaseManagementFragment;
import com.pm360.cepm360.common.GLOBAL;
import com.pm360.cepm360.entity.Contract;
import com.pm360.cepm360.entity.P_LWDW;
import com.pm360.cepm360.entity.P_ZL;
import com.pm360.cepm360.entity.P_ZLR;
import com.pm360.cepm360.entity.P_ZLRD;
import com.pm360.cepm360.entity.Project;
import com.pm360.cepm360.entity.User;
import com.pm360.cepm360.services.lease.RemoteZLRService;

public class LeaseListAddActivity extends BaseTicketActivity<P_ZLR, P_ZLRD> 
{
	private static final int ADD_ZLRD_REQUEST = 222;
	private static final int CONTRACT_SELECT_CODE = 200;
	private static final int COMPANY_SELECT_CODE = 201;
	private static final int OPERATOR_SELECT_CODE = 202;
	private static final int STOREHOUSE_REQUEST_CODE = 203;
	private static final int KEEPER_SELECT_CODE = 204;
	
	@Override
	protected SimpleTicketServiceInterface<P_ZLR, P_ZLRD> getTicketServiceInterface() {
		return new SimpleTicketServiceInterface<P_ZLR, P_ZLRD>(){

			@Override
			public void getDetailServerData(DataManagerInterface manager,
					int typeId) {
				RemoteZLRService.getInstance().getZLRD(manager, typeId);
			}

			@Override
			public void addServerData(DataManagerInterface manager,
					P_ZLR topData, List<P_ZLRD> detailData) {
				topData.setTenant_id(UserCache.getCurrentUser().getTenant_id());
				topData.setItem(detailData.size());
				RemoteZLRService.getInstance().addZLR(manager, topData, detailData);
			}

			@Override
			public void updateServerData(DataManagerInterface manager,
					P_ZLR topData, List<P_ZLRD> detailData) {
				topData.setItem(detailData.size());
				RemoteZLRService.getInstance().updateZLR(manager, topData, detailData);
			}
		};
	}

	@Override
	protected TicketListInterface<P_ZLRD> getListInterface() {
		return new TicketListInterface<P_ZLRD>() {

			@Override
			public int getListHeaderLayoutId() {
				return R.layout.lease_add_list_item_title;
			}

			@Override
			public int getListLayoutId() {
				return getListHeaderLayoutId();
			}

			@Override
			public int getListHeaderNames() {
				return R.array.lease_header_title_names;
			}

			@Override
			public int getListHeaderIds() {
				return R.array.lease_header_title_ids;
			}

			@Override
			public int getListItemIds() {
				return getListHeaderIds();
			}

			@Override
			public Map<String, Map<String, String>> getDisplayFieldsSwitchMap() {
				Map<String, Map<String, String>> map = new HashMap<String, Map<String, String>>();
				Map<String, String> submap = new HashMap<String, String>();
				for (int i = 0; i < GLOBAL.SB_TYPE.length; i++) {
					submap.put(GLOBAL.SB_TYPE[i][0], GLOBAL.SB_TYPE[i][1]);
				}
				map.put("zl_type", submap);
				
				Map<String, String> submap1 = new HashMap<String, String>();
				for (int i = 0; i < GLOBAL.UNIT_TYPE.length; i++) {
					submap1.put(GLOBAL.UNIT_TYPE[i][0], GLOBAL.UNIT_TYPE[i][1]);
				}
				map.put("zl_unit", submap1);				
				return map;
			}

			@Override
			public int getListItemId(P_ZLRD t) {
				return t.getZlrd_id();
			}

			@Override
			public String[] getDisplayFeilds() {
				return new String[] { SERIAL_NUMBER, "zl_type", "zl_name", "zl_spec", "number", "zl_unit", "rent", "lease_date", "end_date", "sum"};
			}

			@Override
			public int[] getEditTextNums() {
				return new int[] {4, 6, 7, 8};
			}

			@Override
			public boolean isDataValid() {
				return true;
			}
		};
	}

	@Override
	protected SimpleDialogInterface getDialogInterface() {
		return new SpecialDialogAdapterInterface<P_ZLRD>(){

			@SuppressLint("UseSparseArrays") @Override
			public Map<Integer, Integer> getDialogStyles() {
				Map<Integer, Integer> buttons = new HashMap<Integer, Integer>();
				buttons.put(0, BaseDialog.decimalEditTextLineStyle);
				buttons.put(1, BaseDialog.decimalEditTextLineStyle);
				buttons.put(2, BaseDialog.calendarLineStyle);
				buttons.put(3, BaseDialog.calendarLineStyle);				
				return buttons;
			}

			@Override
			public Map<Integer, String[]> getSupplyData() {
				return null;
			}

			@Override
			public void additionalInit(BaseDialog dialog) {
				final EditText startEt = (EditText) mDialog.getEditTextView(2);
				final EditText endEt = (EditText) mDialog.getEditTextView(3);
				final DoubleDatePickerDialog doubleDatePickerDialog = new DoubleDatePickerDialog(
						LeaseListAddActivity.this, null, startEt, endEt, null);
				OnClickListener dateListener = new OnClickListener() {

					@Override
					public void onClick(View v) {
						doubleDatePickerDialog.show(
								mCurrentItem.getLease_date(), mCurrentItem.getEnd_date());
					}
				};

				mDialog.setEditTextStyle(2, 0, dateListener, null);
				mDialog.setEditTextStyle(3, 0, dateListener, null);
			}

			@Override
			public Map<String, Map<String, String>> getUpdateFieldsSwitchMap() {
				return null;
			}

			@Override
			public int getDialogTitleId() {
				return R.string.modify_lease_list;
			}

			@Override
			public int getDialogLableNames() {
				return R.array.lease_add_dialog;
			}

			@Override
			public String[] getUpdateFeilds() {
				return new String[] { "number", "rent", "lease_date", "end_date"};
			}

			@Override
			public Integer[] getImportantColumns(Map<String, String> saveDataMap) {
				return new Integer[] {0, 1, 2, 3};
			}

			@Override
			public void additionalSaveData(P_ZLRD t) {
				double money = t.getNumber() * t.getRent();
				t.setSum(money);
				t.setRemainder_number(t.getNumber());
			}};
	}

	@Override
	protected SimpleWindowInterface getTopWindowInterface() {
		return new WindowAdapterInterface(){

			@Override
			public int getWindowLableNames() {
				return R.array.lease_top_names;
			}

			@Override
			public String[] getUpdateFeilds() {
				return new String[] {"zlr_number", "project_name", "zlht_name", "zl_company_name", "operator", "storehouse", "storeman"};
			}

			@Override
			public Integer[] getImportantColumns(Map<String, String> saveDataMap) {
				return new Integer[]{2, 3, 4, 5, 6};
			}

			@SuppressLint("UseSparseArrays") @Override
			public Map<Integer, Integer> getWindowStyles() {
				Map<Integer, Integer> buttons = new HashMap<Integer, Integer>();
				buttons.put(0, BaseDialog.editTextReadOnlyLineStyle);
				buttons.put(1, BaseDialog.editTextReadOnlyLineStyle);
				buttons.put(2, BaseDialog.editTextClickLineStyle);
				buttons.put(3, BaseDialog.editTextClickLineStyle);
				buttons.put(4, BaseDialog.userSelectLineStyle);
				buttons.put(5, BaseDialog.editTextClickLineStyle);
				buttons.put(6, BaseDialog.userSelectLineStyle);
				return buttons;
			}

			@Override
			public Map<Integer, String[]> getSupplyData() {
				return null;
			}

			@Override
			public void additionalInit(BaseWindow window) {
				OnClickListener contractSelectListener = (new OnClickListener() {

					@Override
					public void onClick(View v) {
						Intent intent = new Intent(v.getContext(), ListSelectActivity.class);
						Project project = new Project();
						project.setProject_id(mMsgData.getProject_id());
						intent.putExtra(ListSelectActivity.PROJECT_KEY, project);
						intent.putExtra(ListSelectActivity.SELECT_MODE_KEY,ListSelectActivity.SINGLE_SELECT);
						intent.putExtra(ListSelectActivity.FRAGMENT_KEY,
								LeaseContractWithoutTreeFragment.class);
						startActivityForResult(intent, CONTRACT_SELECT_CODE);
					}
				});
				mTopWindow.setEditTextStyle(2, 0, contractSelectListener, null);
				
				OnClickListener leaseCompanySelectListener = (new OnClickListener() {

					@Override
					public void onClick(View v) {
						Intent data = new Intent(LeaseListAddActivity.this,
								ListSelectActivity.class);
						Bundle bundle = new Bundle();
						bundle.putSerializable(ListSelectActivity.FRAGMENT_KEY,
								ContactCompanyFragment.class);
						data.putExtras(bundle);
						startActivityForResult(data, COMPANY_SELECT_CODE);
					}
				});
				mTopWindow.setEditTextStyle(3, 0, leaseCompanySelectListener, null);	
				
				OnClickListener operatorSelectListener = (new OnClickListener() {

					@Override
					public void onClick(View v) {
						Intent intent = new Intent();
						intent.setClass(v.getContext(), OwnerSelectActivity.class);
						intent.putExtra("title", getString(R.string.operator));
						startActivityForResult(intent, OPERATOR_SELECT_CODE);
					}
				});
				mTopWindow.setEditTextStyle(4, 0, operatorSelectListener, null);				

				OnClickListener storeHouseSelectListener = (new OnClickListener() {

					@Override
					public void onClick(View v) {
						if (mMsgData.getProject_id() == 0) {
							return;
						}
						Intent intent = new Intent(LeaseListAddActivity.this,
								StoreHouseSelectActivity.class);
						intent.putExtra(StoreHouseSelectActivity.PROJECT_ID,
								mMsgData.getProject_id());
						startActivityForResult(intent, STOREHOUSE_REQUEST_CODE);
					}
				});
				mTopWindow.setEditTextStyle(5, 0, storeHouseSelectListener, null);
				
				OnClickListener mWarehouseKeeperSelectListener = (new OnClickListener() {

					@Override
					public void onClick(View v) {
						Intent intent = new Intent();
						intent.setClass(v.getContext(), OwnerSelectActivity.class);
						intent.putExtra("title", getString(R.string.storekeeper));
						startActivityForResult(intent, KEEPER_SELECT_CODE);
					}
				});
				mTopWindow.setEditTextStyle(6, 0, mWarehouseKeeperSelectListener, null);
				if (mCurrentStatus == ADD_STATUS) {
					mTopWindow.setEditTextContent(1, mMsgData.getProject_name());
				}
			}

			@Override
			public Map<String, Map<String, String>> getUpdateFieldsSwitchMap() {
				return null;
			}};
	}
	
	// 调用添加资源的返回。
	public void onActivityResult(int requestCode, int resultCode, Intent data) {
		if (resultCode == 0 || data == null)
			return;
		if (requestCode == ADD_ZLRD_REQUEST) {
			if (resultCode == RESULT_CANCELED) {
				// setTitle("Canceled...");
			} else if (resultCode == RESULT_OK) {
				@SuppressWarnings("unchecked")
				List<P_ZL> zls = (List<P_ZL>) data
						.getSerializableExtra(ListSelectActivity.RESULT_KEY);
				if (zls != null && !zls.isEmpty()) {
					for (P_ZL zl : zls) {
						P_ZLRD bean = new P_ZLRD();
						if (mMsgData.getZlr_id() != 0) {
							bean.setZlr_id(mMsgData.getZlr_id());
						}
						bean.setTenant_id(UserCache
								.getCurrentUser().getTenant_id());
						bean.setZl_id(zl.getZl_id());
						bean.setZl_type(zl.getZl_type());
						bean.setZl_name(zl.getName());
						bean.setZl_spec(zl.getSpec());
						bean.setZl_unit(zl.getUnit());
						bean.setZlr_number(mMsgData.getZlr_number());
						bean.setIDU(GLOBAL.IDU_INSERT); // 插入
						mListAdapter.getDataShowList().add(bean);
					}
					mListAdapter.notifyDataSetChanged();
				}
			}
		} else if (requestCode == CONTRACT_SELECT_CODE) {
			Contract contract = (Contract) data
					.getSerializableExtra(ListSelectActivity.RESULT_KEY);
			if (contract != null) {
				mTopWindow.setEditTextContent(2, contract.getName());				
				mMsgData.setContract_id(contract.getContract_id());
				mMsgData.setZlht_name(contract.getName());
			}
		} else if (requestCode == OPERATOR_SELECT_CODE) {
			User user = (User) data.getSerializableExtra("user");
			if (user != null) {
				mTopWindow.setUserTextContent(4, user.getUser_id());				
				mMsgData.setOperator(user.getUser_id());
			}
		} else if (requestCode == KEEPER_SELECT_CODE) {
			User user = (User) data.getSerializableExtra("user");
			if (user != null) {
				mTopWindow.setUserTextContent(6, user.getUser_id());
				mMsgData.setStoreman(user.getUser_id());
			}
		} else if (requestCode == COMPANY_SELECT_CODE) {
			P_LWDW currentProvider = (P_LWDW) data
					.getSerializableExtra(ListSelectActivity.RESULT_KEY);
			mTopWindow.setEditTextContent(3, currentProvider.getName());
			mMsgData.setZl_company(currentProvider.getLwdw_id());
			mMsgData.setZl_company_name(currentProvider.getName());
		} else if (requestCode == STOREHOUSE_REQUEST_CODE) {
			String storeHouse = data
					.getStringExtra(StoreHouseSelectActivity.RESULT_KEY);
			if (storeHouse != null && storeHouse.length() != 0) {
				mTopWindow.setEditTextContent(5, storeHouse);
				mMsgData.setStorehouse(storeHouse);
			}
		}

		super.onActivityResult(requestCode, resultCode, data);
	}

	@Override
	protected OptionMenuInterface getOptionMenuInterface() {
		return null;
	}
	
	private void AddZLRD() {
		Intent intent = new Intent(this, ListSelectActivity.class);
		Bundle bundle = new Bundle();
		bundle.putSerializable(ListSelectActivity.FRAGMENT_KEY,
				LeaseManagementFragment.class);
		bundle.putBoolean(ListSelectActivity.SELECT_MODE_KEY,
				ListSelectActivity.MULTI_SELECT);
		ArrayList<P_ZL> list = new ArrayList<P_ZL>();
		for (P_ZLRD rgd : mListAdapter.getDataShowList()) {
			P_ZL nr = new P_ZL();
			nr.setZl_id(rgd.getZl_id());
			list.add(nr);
		}
		bundle.putSerializable(ListSelectActivity.FILTER_DATA_KEY, list);
		intent.putExtras(bundle);
		startActivityForResult(intent, ADD_ZLRD_REQUEST);
	}	

	@Override
	protected SimpleButtonInterface getTopButtonInterface() {
		return new SimpleButtonInterface(){

			@Override
			public String[] getNames() {
				return new String[] { getString(R.string.select_lease_equipent) };
			}

			@Override
			public OnClickListener[] getListeners() {
				return new OnClickListener[] {
						new OnClickListener() {
							
							@Override
							public void onClick(View v) {
								AddZLRD();
							}
						}};
			}};
	}

	@Override
	protected String getFlowType() {
		return null;
	}

	@Override
	protected String getActivityAction() {
		return null;
	}

	@Override
	protected String getNumCode() {
		return "ZLR";
	}

	@Override
	protected Class<P_ZLR> getTopClass() {
		return  P_ZLR.class;
	}

	@Override
	protected Class<P_ZLRD> getListClass() {
		return  P_ZLRD.class;
	}
}
