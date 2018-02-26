package com.pm360.cepm360.app.module.lease;

import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;

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
import com.pm360.cepm360.app.module.purchase.BaseTicketActivity;
import com.pm360.cepm360.common.GLOBAL;
import com.pm360.cepm360.entity.P_ZLH;
import com.pm360.cepm360.entity.P_ZLHD;
import com.pm360.cepm360.entity.P_ZLRD;
import com.pm360.cepm360.entity.User;
import com.pm360.cepm360.services.lease.RemoteZLHService;

public class RentListAddActivity extends BaseTicketActivity<P_ZLH, P_ZLHD> 
{
	private static final int ADD_ZLHD_REQUEST = 222;
	private static final int OPERATOR_SELECT_CODE = 202;
	private int HZPERSON_SELECT_CODE = 205;
	
	@Override
	protected SimpleTicketServiceInterface<P_ZLH, P_ZLHD> getTicketServiceInterface() {
		return new SimpleTicketServiceInterface<P_ZLH, P_ZLHD>(){

			@Override
			public void getDetailServerData(DataManagerInterface manager,
					int typeId) {
				RemoteZLHService.getInstance().getZLHD(manager, typeId);
			}

			@Override
			public void addServerData(DataManagerInterface manager,
					P_ZLH topData, List<P_ZLHD> detailData) {
				topData.setTenant_id(UserCache.getCurrentUser().getTenant_id());
				topData.setItem(detailData.size());
				RemoteZLHService.getInstance().addZLH(manager, topData, detailData);
			}

			@Override
			public void updateServerData(DataManagerInterface manager,
					P_ZLH topData, List<P_ZLHD> detailData) {
				topData.setItem(detailData.size());
				RemoteZLHService.getInstance().updateZLH(manager, topData, detailData);
			}
		};
	}

	@Override
	protected TicketListInterface<P_ZLHD> getListInterface() {
		return new TicketListInterface<P_ZLHD>() {

			@Override
			public int getListHeaderLayoutId() {
				return R.layout.rent_add_list_item_title;
			}

			@Override
			public int getListLayoutId() {
				return getListHeaderLayoutId();
			}

			@Override
			public int getListHeaderNames() {
				return R.array.rent_header_title_names;
			}

			@Override
			public int getListHeaderIds() {
				return R.array.rent_header_title_ids;
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
				map.put("hz_person", UserCache.getUserMaps());
				
				map.put("zl_type", submap);
						
				return map;
			}

			@Override
			public int getListItemId(P_ZLHD t) {
				return t.getZlhd_id();
			}

			@Override
			public String[] getDisplayFeilds() {
				return new String[] { SERIAL_NUMBER, "zl_type", "zl_name", "zl_spec", "zlr_number", "number", "zl_company_name", "hz_person", "hz_date"};
			}

			@Override
			public int[] getEditTextNums() {
				return new int[] {5, 7, 8};
			}

			@Override
			public boolean isDataValid() {
				return true;
			}
		};
	}

	@Override
	protected SimpleDialogInterface getDialogInterface() {
		return new SpecialDialogAdapterInterface<P_ZLHD>(){

			@SuppressLint("UseSparseArrays") @Override
			public Map<Integer, Integer> getDialogStyles() {
				Map<Integer, Integer> buttons = new HashMap<Integer, Integer>();
				buttons.put(0, BaseDialog.decimalEditTextLineStyle);
				buttons.put(1, BaseDialog.userSelectLineStyle);
				buttons.put(2, BaseDialog.calendarLineStyle);				
				return buttons;
			}

			@Override
			public Map<Integer, String[]> getSupplyData() {
				return null;
			}

			@Override
			public void additionalInit(BaseDialog dialog) {
				OnClickListener userListener = new OnClickListener() {

					@Override
					public void onClick(View v) {
						Intent intent = new Intent();
						intent.setClass(v.getContext(), OwnerSelectActivity.class);
						intent.putExtra("title", getString(R.string.operator));
						startActivityForResult(intent, HZPERSON_SELECT_CODE);
					}
				};				
				mDialog.setEditTextStyle(1, 0, userListener, null);
			}

			@Override
			public Map<String, Map<String, String>> getUpdateFieldsSwitchMap() {
				return null;
			}

			@Override
			public int getDialogTitleId() {
				return R.string.modify_lease;
			}

			@Override
			public int getDialogLableNames() {
				return R.array.rent_add_dialog;
			}

			@Override
			public String[] getUpdateFeilds() {
				return new String[] { "number", "hz_person", "hz_date"};
			}

			@Override
			public void additionalSaveData(P_ZLHD t) {
				
			}

			@Override
			public Integer[] getImportantColumns(Map<String, String> saveDataMap) {
				return new Integer[]{0, 1, 2};
			}};
	}

	@Override
	protected SimpleWindowInterface getTopWindowInterface() {
		return new WindowAdapterInterface(){

			@Override
			public int getWindowLableNames() {
				return R.array.rent_top_names;
			}

			@Override
			public String[] getUpdateFeilds() {
				return new String[] {"zlh_number", "operator"};
			}

			@Override
			public Integer[] getImportantColumns(Map<String, String> saveDataMap) {
				return new Integer[]{1};
			};

			@SuppressLint("UseSparseArrays") @Override
			public Map<Integer, Integer> getWindowStyles() {
				Map<Integer, Integer> buttons = new HashMap<Integer, Integer>();
				buttons.put(0, BaseDialog.editTextReadOnlyLineStyle);
				buttons.put(1, BaseDialog.userReadOnlySelectLineStyle);
				return buttons;
			}

			@Override
			public Map<Integer, String[]> getSupplyData() {
				return null;
			}

			@Override
			public void additionalInit(BaseWindow window) {				
//				OnClickListener operatorSelectListener = (new OnClickListener() {
//
//					@Override
//					public void onClick(View v) {
//						Intent intent = new Intent();
//						intent.setClass(v.getContext(), OwnerSelectActivity.class);
//						intent.putExtra("title", getString(R.string.operator));
//						startActivityForResult(intent, OPERATOR_SELECT_CODE);
//					}
//				});
//				mTopWindow.setEditTextStyle(1, 0, operatorSelectListener, null);				
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
		if (requestCode == ADD_ZLHD_REQUEST) {
			if (resultCode == RESULT_CANCELED) {
				// setTitle("Canceled...");
			} else if (resultCode == RESULT_OK) {
				@SuppressWarnings("unchecked")
				List<P_ZLRD> zlrds = (List<P_ZLRD>) data
						.getSerializableExtra(ListSelectActivity.RESULT_KEY); 
				if (zlrds != null && !zlrds.isEmpty()) {
					for (P_ZLRD zlrd : zlrds) {
						P_ZLHD bean = new P_ZLHD();
						bean.setTenant_id(UserCache
								.getCurrentUser().getTenant_id());
						bean.setZl_type(zlrd.getZl_type());
						bean.setZl_id(zlrd.getZl_id());
						bean.setZlr_id(zlrd.getZlr_id());
						bean.setZlrd_id(zlrd.getZlrd_id());
						bean.setZlh_id(mMsgData.getZlh_id());
						bean.setZl_name(zlrd.getZl_name());
						bean.setZl_spec(zlrd.getZl_spec());
						bean.setZl_company(zlrd.getZl_company());
						bean.setZl_company_name(zlrd.getZl_company_name());
						bean.setZlr_number(zlrd.getZlr_number());
						bean.setZlr_id(zlrd.getZlr_id());
						bean.setNumber(zlrd.getRemainder_number());
						bean.setHz_person(UserCache
								.getCurrentUser().getUser_id());
						bean.setHz_date(new Date(System.currentTimeMillis()));
						bean.setIDU(GLOBAL.IDU_INSERT); // 插入
						mListAdapter.getDataShowList().add(bean);
					}
					mListAdapter.notifyDataSetChanged();
				}
			}
		} else if (requestCode == OPERATOR_SELECT_CODE) {
			User user = (User) data.getSerializableExtra("user");
			if (user != null) {
				mTopWindow.setUserTextContent(1, user.getUser_id());				
				mMsgData.setOperator(user.getUser_id());
			}
		} else if (requestCode == HZPERSON_SELECT_CODE) {
			User user = (User) data.getSerializableExtra("user");
			if (user != null) {
				mDialog.setUserTextContent(1, user.getUser_id());
			}
		}
		super.onActivityResult(requestCode, resultCode, data);
	}

	@Override
	protected OptionMenuInterface getOptionMenuInterface() {
		return null;
	}
	
	private void AddZLHD() {
		Intent intent = new Intent(this, ListSelectActivity.class);
		Bundle bundle = new Bundle();
		bundle.putSerializable(ListSelectActivity.FRAGMENT_KEY,
				SelectZLRDFragment.class);
		bundle.putBoolean(ListSelectActivity.SELECT_MODE_KEY,
				ListSelectActivity.MULTI_SELECT);
		bundle.putSerializable(ListSelectActivity.FILTER_DATA_KEY, null);
		intent.putExtras(bundle);
		startActivityForResult(intent, ADD_ZLHD_REQUEST);
	}

	@Override
	protected SimpleButtonInterface getTopButtonInterface() {
		return new SimpleButtonInterface(){

			@Override
			public String[] getNames() {
				return new String[] { getString(R.string.lease_order) };
			}

			@Override
			public OnClickListener[] getListeners() {
				return new OnClickListener[] {
						new OnClickListener() {
							
							@Override
							public void onClick(View v) {
								AddZLHD();
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
		return "ZLH";
	}

	@Override
	protected Class<P_ZLH> getTopClass() {
		return  P_ZLH.class;
	}

	@Override
	protected Class<P_ZLHD> getListClass() {
		return  P_ZLHD.class;
	}
}
