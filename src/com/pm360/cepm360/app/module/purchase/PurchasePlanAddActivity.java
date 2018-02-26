package com.pm360.cepm360.app.module.purchase;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Toast;

import com.pm360.cepm360.R;
import com.pm360.cepm360.app.cache.UserCache;
import com.pm360.cepm360.app.common.activity.DataManagerInterface;
import com.pm360.cepm360.app.common.activity.ListSelectActivity;
import com.pm360.cepm360.app.common.activity.OwnerSelectActivity;
import com.pm360.cepm360.app.common.activity.ProjectSelectActivity;
import com.pm360.cepm360.app.common.custinterface.EditableTicketListInterface;
import com.pm360.cepm360.app.common.custinterface.OptionMenuInterface;
import com.pm360.cepm360.app.common.custinterface.SimpleWindowInterface;
import com.pm360.cepm360.app.common.custinterface.SpecialButtonInterface;
import com.pm360.cepm360.app.common.custinterface.SpecialDialogAdapterInterface;
import com.pm360.cepm360.app.common.custinterface.TicketServiceInterface;
import com.pm360.cepm360.app.common.custinterface.WindowAdapterInterface;
import com.pm360.cepm360.app.common.view.parent.BaseDialog;
import com.pm360.cepm360.app.common.view.parent.BaseWindow;
import com.pm360.cepm360.app.common.view.parent.FlowApprovalDialog;
import com.pm360.cepm360.app.module.purchase.PurchaseBudgetDialog.BudgetDialogInterface;
import com.pm360.cepm360.app.module.resource.ContactCompanyFragment;
import com.pm360.cepm360.app.module.resource.EquipmentFragment;
import com.pm360.cepm360.app.module.resource.MaterialFragment;
import com.pm360.cepm360.app.module.schedule.PlanMakeFragment;
import com.pm360.cepm360.common.GLOBAL;
import com.pm360.cepm360.common.ResultStatus;
import com.pm360.cepm360.common.json.AnalysisManager;
import com.pm360.cepm360.common.util.LogUtil;
import com.pm360.cepm360.entity.Flow_approval;
import com.pm360.cepm360.entity.P_CG;
import com.pm360.cepm360.entity.P_CGD;
import com.pm360.cepm360.entity.P_CGJH;
import com.pm360.cepm360.entity.P_CGJHD;
import com.pm360.cepm360.entity.P_LWDW;
import com.pm360.cepm360.entity.P_WZ;
import com.pm360.cepm360.entity.Project;
import com.pm360.cepm360.entity.Task;
import com.pm360.cepm360.entity.User;
import com.pm360.cepm360.services.common.RemoteCommonService;
import com.pm360.cepm360.services.purchase.RemotePlanService;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;


public class PurchasePlanAddActivity extends BaseTicketActivity<P_CGJH, P_CGJHD> {
	
	private DataManagerInterface mFlowApprovalManager;
	private Flow_approval mCurrentFlowApproval;
	private Flow_approval mNextFlowApproval;
	
	private int PROJECT_SELECT_CODE = 200;
	private int TASK_SELECT_CODE = 201;
	private int MATERIAL_SELECT_CODE = 202;
	private int EQUIPMENT_SELECT_CODE = 203;
	private int OWNER_SELECT_CODE = 204;
	private int PROVIDER_SELECT_CODE = 205;
	
	private void PassFlowDataToServer1() {
		RemoteCommonService.getInstance().getCodeByDate(mCGCodeManager, "CG");
	}
	
	private DataManagerInterface mCGCodeManager = new DataManagerInterface() {

		@Override
		public void getDataOnResult(ResultStatus status, List<?> list) {

			if (status.getCode() == AnalysisManager.SUCCESS_DB_QUERY) {
				
				PassFlowDataToServer2(status.getMessage());
			}
		}

	};
	
	private void PassFlowDataToServer2(String codeNumber) {
		P_CG cg = new P_CG();
		cg.setCgjh_id(mMsgData.getCgjh_id());
		cg.setCgjh_name(mMsgData.getCgjh_name());
		cg.setExecute_person(mMsgData.getExecute_person());
		cg.setProject_id(mMsgData.getProject_id());
		cg.setCg_money(mMsgData.getTotal_money());
		cg.setCg_number(codeNumber);
		cg.setTenant_id(mMsgData.getTenant_id());
		List<P_CGJHD> listJHD = (List<P_CGJHD>) mListAdapter.getDataShowList();
		List<P_CGD> cgdList = new ArrayList<P_CGD>();
		for (int i = 0; i < listJHD.size(); i++) {
			P_CGD cgd = new P_CGD();
			cgd.setWz_id(listJHD.get(i).getWz_id());
			cgd.setCgjh_id(listJHD.get(i).getCgjh_id());
			cgd.setCgjhd_id(listJHD.get(i).getCgjhd_id());
			cgd.setWz_name(listJHD.get(i).getWz_name()); 
			cgd.setWz_type_1(listJHD.get(i).getWz_type_1());
			cgd.setWz_type_2(listJHD.get(i).getWz_type_2());
			cgd.setWz_brand(listJHD.get(i).getWz_brand());
			cgd.setWz_spec(listJHD.get(i).getWz_brand());
			cgd.setWz_unit(listJHD.get(i).getWz_unit());
			cgd.setCg_quantity(listJHD.get(i).getQuantity());
			cgd.setProject_id(mMsgData.getProject_id());
			cgd.setTenant_id(mMsgData.getTenant_id());
			//cgd.set
			cgd.setLwdw_name(listJHD.get(i).getLwdw_name());
			cgd.setUnit_price(listJHD.get(i).getUnit_price());
			cgd.setMoney(listJHD.get(i).getMoney());
			cgdList.add(cgd);
		}
		RemotePlanService.getInstance().passApproval(mFlowApprovalManager, cg, cgdList, mCurrentFlowApproval, mNextFlowApproval);
	
	}
	
	private void initAdd(P_CGJH topData) {
		topData.setProject_id(mSelectProject.getProject_id());
		topData.setProject_name(mSelectProject.getName());
		topData.setTenant_id(UserCache.getCurrentUser().getTenant_id());
	}
	
	@Override
	protected TicketServiceInterface<P_CGJH, P_CGJHD> getTicketServiceInterface() {
		return new TicketServiceInterface<P_CGJH, P_CGJHD>() {

			@Override
			public void getDataByMessageId(DataManagerInterface manager,
					int messageId) {
				RemotePlanService.getInstance().getCGJHByMsgId(manager, messageId);
			}

			@Override
			public void getDetailServerData(DataManagerInterface manager,
					int typeId) {
				RemotePlanService.getInstance().getCGJHD(manager, typeId);
			}

			@Override
			public void addServerData(DataManagerInterface manager,
					P_CGJH topData, List<P_CGJHD> detailData) {
				initAdd(topData);
				RemotePlanService.getInstance().addCGJH(manager, topData, detailData);
			}

			@Override
			public void updateServerData(DataManagerInterface manager,
					P_CGJH topData, List<P_CGJHD> detailData) {
				RemotePlanService.getInstance().updateCGJH(manager, topData, detailData);
			}

			@Override
			public void rebutApproval(DataManagerInterface manager,
					P_CGJH topData, Flow_approval currentFlow_approval,
					Flow_approval nextFlow_approval) {
				P_CG cg = new P_CG();
				cg.setCgjh_id(topData.getCgjh_id());
				RemotePlanService.getInstance().rebutApproval(manager, cg, currentFlow_approval, nextFlow_approval);
			}

			@Override
			public void passApproval(DataManagerInterface manager,
					P_CGJH topData, List<P_CGJHD> detailData,
					Flow_approval currentFlow_approval,
					Flow_approval nextFlow_approval) {
					mFlowApprovalManager = manager;
					mCurrentFlowApproval = currentFlow_approval;
					mNextFlowApproval = nextFlow_approval;
					PassFlowDataToServer1();
				}

			@Override
			public void submitForAdd(DataManagerInterface manager,
					P_CGJH topData, List<P_CGJHD> detailData,
					Flow_approval Flow_approval, String flow_status) {
				initAdd(topData);
				if (flow_status.equals(GLOBAL.FLOW_STATUS[0][0])) {
					RemotePlanService.getInstance().submitWithApprovalForAdd(manager, topData, detailData, Flow_approval);
				} else {
					RemotePlanService.getInstance().submitNoApprovalForAdd(manager, topData, detailData);
				}
			}

			@Override
			public void submitForUpdate(DataManagerInterface manager,
					P_CGJH topData, List<P_CGJHD> detailData,
					Flow_approval Flow_approval, String flow_status) {
				if (flow_status.equals(GLOBAL.FLOW_STATUS[0][0])) {
					RemotePlanService.getInstance().submitWithApprovalForUpdate(manager, topData, detailData, Flow_approval);
				} else {
					RemotePlanService.getInstance().submitNoApprovalForUpdate(manager, topData, detailData);
				}
			}
		};
	}

	@Override
	protected EditableTicketListInterface<P_CGJHD> getListInterface() {
		return new EditableTicketListInterface<P_CGJHD>() {

			@Override
			public int getListHeaderLayoutId() {
				return R.layout.purchase_plan_add_list_item;
			}

			@Override
			public int getListLayoutId() {
				return R.layout.purchase_plan_add_list_item;
			}

			@Override
			public int getListHeaderNames() {
				return R.array.purchase_plan_add_item_names;
			}

			@Override
			public int getListHeaderIds() {
				return R.array.purchase_plan_add_item_ids;
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
			public int getListItemId(P_CGJHD t) {
				return t.getCgjhd_id();
			}

			@Override
			public String[] getDisplayFeilds() {
				return new String[] {"wz_name", "wz_type_2", "wz_brand", "wz_spec", "wz_unit", "quantity",
						"unit_price", "money", "lwdw_name", "indate", "task_name"};
			}

			@Override
			public int[] getEditTextNums() {
				return new int[] {5, 6, 8, 9, 10};
			}

			@Override
			public boolean isDataValid() {
				if (mListAdapter.getDataShowList().size() == 0) {
					Toast.makeText(PurchasePlanAddActivity.this,
							R.string.list_view_is_null, Toast.LENGTH_SHORT).show();
					return false;
				}
				
				int i;
				for (i = 0; i < mListAdapter.getDataShowList().size(); i++) {
					if (mListAdapter.getDataShowList().get(i).getLwdw_name() == null
							|| mListAdapter.getDataShowList().get(i).getLwdw_name()
									.equals("")
							|| mListAdapter.getDataShowList().get(i).getTask_id() == 0) {
						LogUtil.d("wzw i:"
								+ i
								+ "ll:"
								+ mListAdapter.getDataShowList().get(i)
										.getLwdw_name()
								+ "11:"
								+ mListAdapter.getDataShowList().get(i)
										.getTask_id());
						break;
					}
				}
				LogUtil.d("wzw i:" + i);
				if (i != mListAdapter.getDataShowList().size()) {
					Toast.makeText(getBaseContext(), R.string.list_view_is_error,
							Toast.LENGTH_SHORT).show();
					return false;
				} else {
					return true;
				}
			}

			@Override
			public void modifyFieldMap(Map<String, String> displayFieldMap,
					P_CGJHD t, String field) {
				if (field.equals("wz_type_2")) {
					int type2 = t.getWz_type_2() == 0 ? 0 : t.getWz_type_2() - 1;
					if (t.getWz_type_1() == Integer.parseInt(GLOBAL.WZ_TYPE[0][0])) {
						displayFieldMap.put(field, GLOBAL.CL_TYPE[type2][1]);
					} else if (t.getWz_type_1() == Integer.parseInt(GLOBAL.WZ_TYPE[1][0])) {
						displayFieldMap.put(field, GLOBAL.SB_TYPE[type2][1]);
					}
				}
			}
		};
	}

	@Override
	protected SpecialDialogAdapterInterface<P_CGJHD> getDialogInterface() {
		return new SpecialDialogAdapterInterface<P_CGJHD>() {
			
			@Override
			public Integer[] getImportantColumns(Map<String, String> saveDataMap) {
				return null;
			}
			
			@Override
			public String[] getUpdateFeilds() {
				return new String[] {"quantity", "unit_price", "lwdw_name", "indate", "task_name"};
			}
			
			@Override
			public int getDialogTitleId() {
				return R.string.purchase_plan_modify;
			}
			
			@Override
			public int getDialogLableNames() {
				return R.array.purchase_plan_add_dialog;
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
				buttons.put(2, BaseDialog.editTextClickLineStyle);
				buttons.put(3, BaseDialog.calendarLineStyle);
				buttons.put(4, BaseDialog.editTextClickLineStyle);
				return buttons;
			}
			
			@Override
			public void additionalInit(BaseDialog dialog) {
				OnClickListener providerSelectListener = (new OnClickListener() {

					@Override
					public void onClick(View v) {
						Intent data = new Intent(PurchasePlanAddActivity.this,
								ListSelectActivity.class);
						Bundle bundle = new Bundle();
						bundle.putSerializable(ListSelectActivity.FRAGMENT_KEY,
								ContactCompanyFragment.class);
						data.putExtras(bundle);
						startActivityForResult(data, PROVIDER_SELECT_CODE);
					}
				});
				
				OnClickListener taskSelectListener = (new OnClickListener() {

					@Override
					public void onClick(View v) {
						Intent intent = new Intent(PurchasePlanAddActivity.this,
								ListSelectActivity.class);
						intent.putExtra(ListSelectActivity.PROJECT_KEY,
								mSelectProject);
						intent.putExtra(ListSelectActivity.FRAGMENT_KEY, PlanMakeFragment.class);
						startActivityForResult(intent, TASK_SELECT_CODE);
					}
				});
				dialog.setEditTextStyle(2, 0, providerSelectListener, null);
				dialog.setEditTextStyle(4, 0, taskSelectListener, null);
			}

			@Override
			public void additionalSaveData(P_CGJHD t) {
				double money = t.getQuantity() * t.getUnit_price();
				t.setMoney(money);
				if (mCurrentProvider != null) {
					t.setLwdw_id(mCurrentProvider.getLwdw_id());
				}
			}
		};
	}

	@Override
	protected SimpleWindowInterface getTopWindowInterface() {
		return new WindowAdapterInterface() {
			
			@Override
			public Integer[] getImportantColumns(Map<String, String> saveDataMap) {
				return new Integer[] {0, 1, 2, 3, 4, 5};
			}
			
			@Override
			public int getWindowLableNames() {
				return R.array.purchase_plan_add_title;
			}
			
			@Override
			public String[] getUpdateFeilds() {
				return new String[] {"cgjh_number", "cgjh_name", "execute_person", "report_date", "plan_date", "project_name"};
			}
			
			@SuppressLint("UseSparseArrays") @Override
			public Map<Integer, Integer> getWindowStyles() {
				Map<Integer, Integer> buttons = new HashMap<Integer, Integer>();
				buttons.put(0, BaseDialog.editTextReadOnlyLineStyle);
				buttons.put(2, BaseDialog.editTextClickLineStyle);
				buttons.put(3, BaseDialog.calendarLineStyle);
				buttons.put(4, BaseDialog.calendarLineStyle);
				if (mCurrentStatus == ADD_STATUS) {
					buttons.put(5, BaseDialog.editTextClickLineStyle);
				} else if (mCurrentStatus == MODIFY_STATUS) {
					buttons.put(5, BaseDialog.editTextReadOnlyLineStyle);
				}
				return buttons;
			}
			
			@SuppressWarnings("rawtypes")
			@Override
			public Map<String, Map<String, String>> getUpdateFieldsSwitchMap() {
				Map<String, Map<String, String>> map = new HashMap<String, Map<String, String>>();
				Map<String, String> submap1 = new HashMap<String, String>();
				submap1.putAll(UserCache.getUserMaps());
				Iterator iter = UserCache.getUserListsMap().entrySet().iterator();
				while (iter.hasNext()) {
					Map.Entry entry = (Map.Entry) iter.next();
					String key = (String) entry.getKey();
					Integer val = (Integer) entry.getValue();
					submap1.put(key, Integer.toString(val));
				}
				map.put("execute_person", submap1);
				return map;
			}
			
			@Override
			public Map<Integer, String[]> getSupplyData() {
				return null;
			}
			
			@Override
			public void additionalInit(BaseWindow window) {
				OnClickListener ownerSelectListener = (new OnClickListener() {

					@Override
					public void onClick(View v) {
						Intent intent = new Intent();
						intent.setClass(v.getContext(), OwnerSelectActivity.class);
						intent.putExtra("title", getString(R.string.execute_person));
						startActivityForResult(intent, OWNER_SELECT_CODE);
					}
				});
				mTopWindow.setEditTextStyle(2, 0, ownerSelectListener, null);
				if (mCurrentStatus == ADD_STATUS) {
					OnClickListener projectSelectListener = (new OnClickListener() {

								@Override
								public void onClick(View v) {
									Intent intent = new Intent(
											PurchasePlanAddActivity.this,
											ProjectSelectActivity.class);
									Bundle bundle = new Bundle();
									bundle.putInt("action", ProjectSelectActivity.ACTION_PICK);
									intent.putExtras(bundle);
									startActivityForResult(intent,
											PROJECT_SELECT_CODE);
								}
							});
					mTopWindow.setEditTextStyle(5, 0, projectSelectListener, null);
				}
			}
		};
	}

	@Override
	protected OptionMenuInterface getOptionMenuInterface() {
		return null;
	}

	@Override
	protected SpecialButtonInterface getTopButtonInterface() {
		return new SpecialButtonInterface() {
			
			@Override
			public String[] getNames() {
				return new String[] { getString(R.string.select_purchase_budget),
						getString(R.string.select_material), getString(R.string.select_equipment) };
			}
			
			@Override
			public OnClickListener[] getListeners() {
				return new OnClickListener[] {
						new OnClickListener() {

							@Override
							public void onClick(View v) {
								mPurchaseBudgetDialog.show();
							}
						},
						new OnClickListener() {

							@Override
							public void onClick(View v) {
								Intent data;
								Bundle bundle;
								data = new Intent(PurchasePlanAddActivity.this,
										ListSelectActivity.class);
								bundle = new Bundle();
								bundle.putSerializable(ListSelectActivity.FRAGMENT_KEY,
										MaterialFragment.class);
								bundle.putBoolean(ListSelectActivity.SELECT_MODE_KEY,
										true);
								List<P_WZ> materialList = new ArrayList<P_WZ>();
								for (int i = 0; i < mListAdapter.getDataShowList().size(); i++) {
									LogUtil.i("wzw type:"
											+ mListAdapter.getDataShowList().get(i)
													.getWz_type_1());
									if (mListAdapter.getDataShowList().get(i)
											.getWz_type_1() == MATERIAL_TYPE) {
										P_WZ wz = new P_WZ();
										wz.setWz_id(mListAdapter.getDataShowList().get(i)
												.getWz_id());
										materialList.add(wz);
									}
								}
								if (!materialList.isEmpty()) {
									bundle.putSerializable(
											ListSelectActivity.FILTER_DATA_KEY,
											(Serializable) materialList);
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
								data = new Intent(PurchasePlanAddActivity.this,
										ListSelectActivity.class);
								bundle = new Bundle();
								bundle.putSerializable(ListSelectActivity.FRAGMENT_KEY,
										EquipmentFragment.class);
								bundle.putBoolean(ListSelectActivity.SELECT_MODE_KEY,
										true);
								List<P_WZ> equipmentList = new ArrayList<P_WZ>();
								for (int i = 0; i < mListAdapter.getDataShowList().size(); i++) {
									if (mListAdapter.getDataShowList().get(i)
											.getWz_type_1() == EQUIPMENT_TYPE) {
										P_WZ wz = new P_WZ();
										wz.setWz_id(mListAdapter.getDataShowList().get(i)
												.getWz_id());
										equipmentList.add(wz);
									}
								}
								if (!equipmentList.isEmpty()) {
									bundle.putSerializable(
											ListSelectActivity.FILTER_DATA_KEY,
											(Serializable) equipmentList);
								}
								data.putExtras(bundle);
								startActivityForResult(data, EQUIPMENT_SELECT_CODE);
							}
						}
				};
			}

			@Override
			public void init() {
				mPurchaseBudgetDialog = new PurchaseBudgetDialog(PurchasePlanAddActivity.this, mBudgetDialogInterface);
				mPurchaseBudgetDialog.initBudgetDialog();
			}
		};
	}
	
	private PurchaseBudgetDialog mPurchaseBudgetDialog;
	private BudgetDialogInterface mBudgetDialogInterface = new BudgetDialogInterface() {

		@Override
		public void setData(List<P_CGJHD> cgjhdList) {
			mListAdapter.setShowDataList(cgjhdList);
			setTotalTextView();
		}

	};

	@Override
	protected String getFlowType() {
		return GLOBAL.FLOW_TYPE[3][0];
	}
	
	private Project mSelectProject;
	private Task mCurrentTask;
	private P_LWDW mCurrentProvider;
	// the same as GLOBAL WZ_TYPE
	private final int MATERIAL_TYPE = 1;
	private final int EQUIPMENT_TYPE = 2;

	@SuppressWarnings("unchecked")
	public void onActivityResult(int requestCode, int resultCode, Intent data) {
		LogUtil.d("wzw requestCode" + requestCode + "select:" + data);
		if (resultCode == 0 || data == null)
			return;

		if (requestCode == PROJECT_SELECT_CODE) {
			mSelectProject = (Project) data.getSerializableExtra("project");
			mTopWindow.setEditTextContent(5, mSelectProject.getName());
		} else if (requestCode == TASK_SELECT_CODE) {
			mCurrentTask = (Task) data
					.getSerializableExtra(ListSelectActivity.RESULT_KEY);
			if (mCurrentTask != null) {
				mCurrentItem.setTask_id(mCurrentTask.getTask_id());
				mDialog.setEditTextContent(4, mCurrentTask.getName());
			}
			LogUtil.d("wzw task:" + mCurrentTask);
		} else if (requestCode == MATERIAL_SELECT_CODE) {
			List<P_WZ> wz = (List<P_WZ>) data
					.getSerializableExtra(ListSelectActivity.RESULT_KEY);
			LogUtil.d("wzw " + wz);
			List<P_CGJHD> cgjhdList = new ArrayList<P_CGJHD>();
			for (int i = 0; i < wz.size(); i++) {
				P_CGJHD cgjhd = new P_CGJHD();
				cgjhd.setWz_name(wz.get(i).getName());
				cgjhd.setIDU(GLOBAL.IDU_INSERT);
				cgjhd.setWz_id(wz.get(i).getWz_id());
				cgjhd.setWz_type_1(MATERIAL_TYPE);
				cgjhd.setWz_type_2(wz.get(i).getWz_type_2());
				cgjhd.setWz_spec(wz.get(i).getSpec());
				cgjhd.setWz_brand(wz.get(i).getBrand());
				cgjhd.setWz_unit(wz.get(i).getUnit());
				cgjhd.setWz_model_number(wz.get(i).getModel_number());
				cgjhdList.add(cgjhd);
			}
			mListAdapter.addShowDataList(cgjhdList);
		} else if (requestCode == EQUIPMENT_SELECT_CODE) {
			List<P_WZ> wz = (List<P_WZ>) data
					.getSerializableExtra(ListSelectActivity.RESULT_KEY);
			LogUtil.d("wzw " + wz);
			List<P_CGJHD> cgjhdList = new ArrayList<P_CGJHD>();
			for (int i = 0; i < wz.size(); i++) {
				P_CGJHD cgjhd = new P_CGJHD();
				cgjhd.setWz_name(wz.get(i).getName());
				cgjhd.setIDU(GLOBAL.IDU_INSERT);
				cgjhd.setWz_id(wz.get(i).getWz_id());
				cgjhd.setWz_type_1(EQUIPMENT_TYPE);
				cgjhd.setWz_type_2(wz.get(i).getWz_type_2());
				cgjhd.setWz_spec(wz.get(i).getSpec());
				cgjhd.setWz_brand(wz.get(i).getBrand());
				cgjhd.setWz_unit(wz.get(i).getUnit());
				cgjhd.setWz_model_number(wz.get(i).getModel_number());
				cgjhdList.add(cgjhd);
			}
			mListAdapter.addShowDataList(cgjhdList);
		} else if (requestCode == OWNER_SELECT_CODE) {
			User user = (User) data.getSerializableExtra("user");
			if (user != null) {
				mTopWindow.setEditTextContent(2, user.getName());
			}
		} else if (requestCode == PROVIDER_SELECT_CODE) {
			mCurrentProvider = (P_LWDW) data
					.getSerializableExtra(ListSelectActivity.RESULT_KEY);
			mDialog.setEditTextContent(2, mCurrentProvider.getName());
		} else if (requestCode == FlowApprovalDialog.REQUEST_COUNTER) {
			if (mFlowApprovalDialog != null) {
				mFlowApprovalDialog.handleUserSelectResult(requestCode, resultCode, data);
			}
		}

		super.onActivityResult(requestCode, resultCode, data);
	}

	@Override
	protected String getActivityAction() {
		return GLOBAL.MSG_PURCHASE_PLAN_ACTION;
	}

	@Override
	protected String getNumCode() {
		return "CGJH";
	}

	@Override
	protected Class<P_CGJH> getTopClass() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	protected Class<P_CGJHD> getListClass() {
		// TODO Auto-generated method stub
		return null;
	}
	
}
