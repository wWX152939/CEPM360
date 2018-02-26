package com.pm360.cepm360.app.module.purchase;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;

import com.pm360.cepm360.R;
import com.pm360.cepm360.app.cache.ProjectCache;
import com.pm360.cepm360.app.cache.UserCache;
import com.pm360.cepm360.app.common.activity.DataManagerInterface;
import com.pm360.cepm360.app.common.activity.ListSelectActivity;
import com.pm360.cepm360.app.common.custinterface.DialogAdapterInterface;
import com.pm360.cepm360.app.common.custinterface.EditableTicketListInterface;
import com.pm360.cepm360.app.common.custinterface.OptionMenuInterface;
import com.pm360.cepm360.app.common.custinterface.SimpleButtonInterface;
import com.pm360.cepm360.app.common.custinterface.SimpleWindowInterface;
import com.pm360.cepm360.app.common.custinterface.SpecialDialogAdapterInterface;
import com.pm360.cepm360.app.common.custinterface.TicketServiceInterface;
import com.pm360.cepm360.app.common.custinterface.WindowAdapterInterface;
import com.pm360.cepm360.app.common.view.parent.BaseDialog;
import com.pm360.cepm360.app.common.view.parent.BaseWindow;
import com.pm360.cepm360.app.module.resource.EquipmentFragment;
import com.pm360.cepm360.app.module.resource.MaterialFragment;
import com.pm360.cepm360.common.GLOBAL;
import com.pm360.cepm360.common.util.LogUtil;
import com.pm360.cepm360.entity.Flow_approval;
import com.pm360.cepm360.entity.P_CGYS;
import com.pm360.cepm360.entity.P_CGYSD;
import com.pm360.cepm360.entity.P_WZ;
import com.pm360.cepm360.services.purchase.RemoteBudgetService;

public class PurchaseBudgetAddActivity extends BaseTicketActivity<P_CGYS, P_CGYSD> {

	// the same as GLOBAL WZ_TYPE
	private final int MATERIAL_TYPE = 1;
	private final int EQUIPMENT_TYPE = 2;
	
	private int MATERIAL_SELECT_CODE = 200;
	private int EQUIPMENT_SELECT_CODE = 201;
	
	@Override
	protected TicketServiceInterface<P_CGYS, P_CGYSD> getTicketServiceInterface() {
		return new TicketServiceInterface<P_CGYS, P_CGYSD>() {

			@Override
			public void getDetailServerData(DataManagerInterface manager,
					int typeId) {
				RemoteBudgetService.getInstance().getCGYSD(manager, typeId);
			}

			@Override
			public void addServerData(DataManagerInterface manager,
					P_CGYS topData, List<P_CGYSD> detailData) {
				topData.setTenant_id(UserCache.getCurrentUser().getTenant_id());
				topData.setProject_id(ProjectCache.getCurrentProject().getProject_id());
				topData.setPurchase_item(detailData.size());
				RemoteBudgetService.getInstance().addCGYS(manager, topData, detailData);
			}

			@Override
			public void updateServerData(DataManagerInterface manager,
					P_CGYS topData, List<P_CGYSD> detailData) {
				topData.setPurchase_item(detailData.size());
				RemoteBudgetService.getInstance().updateCGYS(manager, topData, detailData);
			}

			@Override
			public void rebutApproval(DataManagerInterface manager,
					P_CGYS topData, Flow_approval currentFlow_approval,
					Flow_approval nextFlow_approvalow_approval) {
				// TODO Auto-generated method stub
				
			}

			@Override
			public void passApproval(DataManagerInterface manager,
					P_CGYS topData, List<P_CGYSD> detailData,
					Flow_approval currentFlow_approval,
					Flow_approval nextFlow_approvalow_approval) {
				// TODO Auto-generated method stub
				
			}

			@Override
			public void submitForAdd(DataManagerInterface manager,
					P_CGYS topData, List<P_CGYSD> detailData,
					Flow_approval Flow_approval, String flow_status) {
				RemoteBudgetService.getInstance().submitForAdd(manager, topData, detailData, Flow_approval, flow_status);
			}

			@Override
			public void submitForUpdate(DataManagerInterface manager,
					P_CGYS topData, List<P_CGYSD> detailData,
					Flow_approval Flow_approval, String flow_status) {
				RemoteBudgetService.getInstance().submitForUpdate(manager, topData, detailData, Flow_approval, flow_status);
				
			}

			@Override
			public void getDataByMessageId(DataManagerInterface manager,
					int messageId) {
//				RemoteBudgetService.getInstance().getCGJHByMsgId(manager,
//						messageId);
			}
		};
	}

	@Override
	protected EditableTicketListInterface<P_CGYSD> getListInterface() {
		return new EditableTicketListInterface<P_CGYSD>() {

			@Override
			public int getListHeaderLayoutId() {
				return R.layout.purchase_budget_add_list_item;
			}

			@Override
			public int getListLayoutId() {
				return getListHeaderLayoutId();
			}

			@Override
			public int getListHeaderNames() {
				return R.array.purchase_budget_add_item_names;
			}

			@Override
			public int getListHeaderIds() {
				return R.array.purchase_budget_add_item_ids;
			}

			@Override
			public int getListItemIds() {
				return getListHeaderIds();
			}

			@Override
			public Map<String, Map<String, String>> getDisplayFieldsSwitchMap() {
				Map<String, Map<String, String>> map = new HashMap<String, Map<String, String>>();
				Map<String, String> submap1 = new HashMap<String, String>();
				for (int i = 0; i < GLOBAL.UNIT_TYPE.length; i++) {
					submap1.put(GLOBAL.UNIT_TYPE[i][0], GLOBAL.UNIT_TYPE[i][1]);
				}
				map.put("wz_unit", submap1);
				return map;
			}

			@Override
			public int getListItemId(P_CGYSD t) {
				return t.getCgysd_id();
			}

			@Override
			public String[] getDisplayFeilds() {
				return new String[] { "wz_name", "wz_type_2", "wz_brand", "wz_spec", "wz_unit", "quantity", "unit_price", "money" };
			}

			@Override
			public int[] getEditTextNums() {
				return new int[] {5, 6};
			}

			@Override
			public boolean isDataValid() {
				return true;
			}

			@Override
			public void modifyFieldMap(Map<String, String> displayFieldMap,
					P_CGYSD t, String field) {
				if (field.equals("wz_type_2")) {
					if (t.getWz_type_1() == Integer.parseInt(GLOBAL.WZ_TYPE[0][0])) {
						int type2 = t.getWz_type_2() == 0 ? 0 : t.getWz_type_2() - 1;
						displayFieldMap.put(field, GLOBAL.CL_TYPE[type2][1]);
					} else if (t.getWz_type_1() == Integer.parseInt(GLOBAL.WZ_TYPE[1][0])) {
						int type2 = t.getWz_type_2() == 0 ? 0 : t.getWz_type_2() - 1;
						displayFieldMap.put(field, GLOBAL.SB_TYPE[type2][1]);
					}
				}
			}
		};
	}

	@Override
	protected DialogAdapterInterface getDialogInterface() {
		return new SpecialDialogAdapterInterface<P_CGYSD>() {
			
			@Override
			public Integer[] getImportantColumns(Map<String, String> saveDataMap) {
				return new Integer[] {0, 1};
			}
			
			@Override
			public String[] getUpdateFeilds() {
				return new String[] { "quantity", "unit_price" };
			}
			
			@Override
			public int getDialogTitleId() {
				return R.string.purchase_budget_modify;
			}
			
			@Override
			public int getDialogLableNames() {
				return R.array.purchase_budget_add_dialog;
			}
			
			@Override
			public Map<String, Map<String, String>> getUpdateFieldsSwitchMap() {
				return null;
			}
			
			@Override
			public Map<Integer, String[]> getSupplyData() {
				return null;
			}
			
			@SuppressLint("UseSparseArrays") @Override
			public Map<Integer, Integer> getDialogStyles() {
				Map<Integer, Integer> buttons = new HashMap<Integer, Integer>();
				buttons.put(0, BaseDialog.decimalEditTextLineStyle);
				buttons.put(1, BaseDialog.decimalEditTextLineStyle);
				return buttons;
			}
			
			@Override
			public void additionalInit(BaseDialog dialog) {
				
			}
			
			@Override
			public void additionalSaveData(P_CGYSD t) {
				double money = t.getQuantity() * t.getUnit_price();
				t.setMoney(money);
			}
		};
	}

	@Override
	protected SimpleWindowInterface getTopWindowInterface() {
		return new WindowAdapterInterface() {
			
			@Override
			public Integer[] getImportantColumns(Map<String, String> saveDataMap) {
				return new Integer[] {0, 1, 2};
			}
			
			@Override
			public int getWindowLableNames() {
				return R.array.purchase_budget_add_title;
			}
			
			@Override
			public String[] getUpdateFeilds() {
				return new String[] {"cgys_number", "cgys_name", "workload", "task_name"};
			}

			@SuppressLint("UseSparseArrays") @Override
			public Map<Integer, Integer> getWindowStyles() {
				Map<Integer, Integer> buttons = new HashMap<Integer, Integer>();
				buttons.put(0, BaseDialog.editTextReadOnlyLineStyle);
				buttons.put(2, BaseDialog.numberEditTextLineStyle);
				return buttons;
			}

			@Override
			public Map<Integer, String[]> getSupplyData() {
				// TODO Auto-generated method stub
				return null;
			}

			@Override
			public void additionalInit(BaseWindow window) {
				// TODO Auto-generated method stub
				
			}

			@Override
			public Map<String, Map<String, String>> getUpdateFieldsSwitchMap() {
				// TODO Auto-generated method stub
				return null;
			}
		};
	}

	@Override
	protected OptionMenuInterface getOptionMenuInterface() {
		return null;
	}

	@Override
	protected SimpleButtonInterface getTopButtonInterface() {
		return new SimpleButtonInterface() {
			
			@Override
			public String[] getNames() {
				return new String[] { getString(R.string.select_material), getString(R.string.select_equipment) };
			}
			
			@Override
			public OnClickListener[] getListeners() {
				return new OnClickListener[] {
						new OnClickListener() {
							
							@Override
							public void onClick(View v) {
								Intent data;
								Bundle bundle;
								data = new Intent(PurchaseBudgetAddActivity.this, ListSelectActivity.class);
								bundle = new Bundle();
								bundle.putSerializable(ListSelectActivity.FRAGMENT_KEY, MaterialFragment.class);
								bundle.putBoolean(ListSelectActivity.SELECT_MODE_KEY, true);
								List<P_WZ> materialList = new ArrayList<P_WZ>();
								for (int i = 0; i < mListAdapter.getDataShowList().size(); i++) {
									if (mListAdapter.getDataShowList().get(i).getWz_type_1() == MATERIAL_TYPE) {
										P_WZ wz = new P_WZ();
										wz.setWz_id(mListAdapter.getDataShowList().get(i).getWz_id());
										materialList.add(wz);
									}
								}
								if (!materialList.isEmpty()) {
									bundle.putSerializable(ListSelectActivity.FILTER_DATA_KEY, (Serializable) materialList);
								}
								data.putExtras(bundle);
								startActivityForResult(data, MATERIAL_SELECT_CODE);
							}
						},
						new OnClickListener() {
							
							@Override
							public void onClick(View v) {
								Intent data;
								Bundle bundle;
								data = new Intent(PurchaseBudgetAddActivity.this, ListSelectActivity.class);
								bundle = new Bundle();
								bundle.putSerializable(ListSelectActivity.FRAGMENT_KEY, EquipmentFragment.class);
								bundle.putBoolean(ListSelectActivity.SELECT_MODE_KEY, true);
								List<P_WZ> equipmentList = new ArrayList<P_WZ>();
								for (int i = 0; i < mListAdapter.getDataShowList().size(); i++) {
									if (mListAdapter.getDataShowList().get(i).getWz_type_1() == EQUIPMENT_TYPE) {
										P_WZ wz = new P_WZ();
										wz.setWz_id(mListAdapter.getDataShowList().get(i).getWz_id());
										equipmentList.add(wz);
									}
								}
								if (!equipmentList.isEmpty()) {
									bundle.putSerializable(ListSelectActivity.FILTER_DATA_KEY, (Serializable) equipmentList);
								}
								data.putExtras(bundle);
								startActivityForResult(data, EQUIPMENT_SELECT_CODE);
							}
						}
				};
			}
		};
	}

	@Override
	protected String getFlowType() {
		return GLOBAL.FLOW_TYPE[4][0];
	}
	
	@SuppressWarnings("unchecked")
	public void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (resultCode == 0 || data == null) return;
        
        if (requestCode == MATERIAL_SELECT_CODE) {
        	LogUtil.d("wzw select:" + data);
			List<P_WZ> wz = (List<P_WZ>) data.getSerializableExtra(ListSelectActivity.RESULT_KEY);
        	LogUtil.d("wzw " + wz);
        	List<P_CGYSD> cgysdList = new ArrayList<P_CGYSD>();
        	for (int i = 0; i < wz.size(); i++) {
        		P_CGYSD cgysd = new P_CGYSD();
        		cgysd.setIDU(GLOBAL.IDU_INSERT);
        		cgysd.setWz_name(wz.get(i).getName());
        		cgysd.setWz_type_1(MATERIAL_TYPE);
        		cgysd.setWz_id(wz.get(i).getWz_id());
        		cgysd.setWz_type_2(wz.get(i).getWz_type_2());
        		cgysd.setWz_spec(wz.get(i).getSpec());
        		cgysd.setWz_brand(wz.get(i).getBrand());
        		cgysd.setWz_unit(wz.get(i).getUnit());
        		cgysd.setWz_model_number(wz.get(i).getModel_number());
        		cgysdList.add(cgysd);
        	}
        	mListAdapter.addShowDataList(cgysdList);
        } else if (requestCode == EQUIPMENT_SELECT_CODE) {
        	List<P_WZ> wz = (List<P_WZ>) data.getSerializableExtra(ListSelectActivity.RESULT_KEY);
        	LogUtil.d("wzw " + wz);
        	List<P_CGYSD> cgysdList = new ArrayList<P_CGYSD>();
        	for (int i = 0; i < wz.size(); i++) {
        		P_CGYSD cgysd = new P_CGYSD();
        		cgysd.setWz_name(wz.get(i).getName());
        		cgysd.setIDU(GLOBAL.IDU_INSERT);
        		cgysd.setWz_type_1(EQUIPMENT_TYPE);
        		cgysd.setWz_id(wz.get(i).getWz_id());
        		cgysd.setWz_type_2(wz.get(i).getWz_type_2());
        		cgysd.setWz_spec(wz.get(i).getSpec());
        		cgysd.setWz_brand(wz.get(i).getBrand());
        		cgysd.setWz_unit(wz.get(i).getUnit());
        		cgysd.setWz_model_number(wz.get(i).getModel_number());
        		cgysdList.add(cgysd);
        	}
        	mListAdapter.addShowDataList(cgysdList);
        }
        
        super.onActivityResult(requestCode, resultCode, data);
    }

	@Override
	protected String getActivityAction() {
		return null;
	}

	@Override
	protected String getNumCode() {
		return "CGYS";
	}

	@Override
	protected Class<P_CGYS> getTopClass() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	protected Class<P_CGYSD> getListClass() {
		// TODO Auto-generated method stub
		return null;
	}
	
}
