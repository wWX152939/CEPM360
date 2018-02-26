package com.pm360.cepm360.app.module.purchase;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Intent;
import android.content.res.TypedArray;
import android.graphics.Color;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.text.Html;
import android.util.TypedValue;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.pm360.cepm360.R;
import com.pm360.cepm360.app.cache.UserCache;
import com.pm360.cepm360.app.common.activity.DataManagerInterface;
import com.pm360.cepm360.app.common.activity.OwnerSelectActivity;
import com.pm360.cepm360.app.common.adpater.DataListAdapter;
import com.pm360.cepm360.app.common.adpater.DataListAdapter.ViewHolder;
import com.pm360.cepm360.app.common.view.OptionsMenuView;
import com.pm360.cepm360.app.common.view.parent.BaseDialog;
import com.pm360.cepm360.app.common.view.parent.BaseToast;
import com.pm360.cepm360.app.common.view.parent.BaseWindow;
import com.pm360.cepm360.app.module.inventory.StoreHouseSelectActivity;
import com.pm360.cepm360.app.module.purchase.PurchasePlanDialog.PlanDialogInterface;
import com.pm360.cepm360.app.utils.UtilTools;
import com.pm360.cepm360.common.GLOBAL;
import com.pm360.cepm360.common.ResultStatus;
import com.pm360.cepm360.common.json.AnalysisManager;
import com.pm360.cepm360.common.util.DateUtils;
import com.pm360.cepm360.common.util.LogUtil;
import com.pm360.cepm360.entity.Contract;
import com.pm360.cepm360.entity.P_CG;
import com.pm360.cepm360.entity.P_CGD;
import com.pm360.cepm360.entity.P_CGJH;
import com.pm360.cepm360.entity.Project;
import com.pm360.cepm360.entity.User;
import com.pm360.cepm360.services.common.RemoteCommonService;
import com.pm360.cepm360.services.purchase.RemotePurchaseService;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

//import com.pm360.cepm360.app.module.contract.ContractSelectActivity;

public class PurchaseExecutiveAddActivity extends TicketActivity2 {
	private int mCurrentStatus;
	private final int MODIFY_STATUS = 1;
	private final int INFO_STATUS = 2;
	private final int ADD_STATUS = 3;
	private P_CG mMsgData;
	private boolean isModifyStatusOnly = true;
	private boolean needShowToast = true;
	
	private int OWNER_SELECT_CODE = 200;
	private int INVENTORY_SELECT_CODE = 201;
	private int SELECT_CONTRACT = 202;

	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		msgHandlerProgress();
		initViewTopWindow();
		initPlanListView();
		initButtons();

		View[] views = new View[2];
		views[0] = mViewTop;
		views[1] = mViewExecutiveList;
		initWindows(views, false);

		if (mCurrentStatus != ADD_STATUS) {
			loadData();
		}
		mOptionsMenuView = createOptionsMenuView(new String[] {
				getResources().getString(R.string.purchase_arrival),
				getResources().getString(R.string.purchase_empty) });
		mOutWZMenuView = createOptionsMenuView(new String[] {
				getResources().getString(R.string.purchase_arrival)});

	}

	private List<P_CG> mCGList;
	@SuppressWarnings("unchecked")
	private void msgHandlerProgress() {
		Intent intent = getIntent();
		if (intent.getSerializableExtra("purchase_modify") != null) {
			mCurrentStatus = MODIFY_STATUS;
			mMsgData = (P_CG) intent.getSerializableExtra("purchase_modify");
		} else if (intent.getSerializableExtra("purchase_info") != null) {
			mCurrentStatus = INFO_STATUS;
			mMsgData = (P_CG) intent.getSerializableExtra("purchase_info");
		} else if (intent.getSerializableExtra("purchase_add") != null) {
			mMsgData = new P_CG();
			mCGList = (List<P_CG>)intent.getSerializableExtra("purchase_add");
			mCurrentStatus = ADD_STATUS;
		}
	}

	private BaseWindow mTopTitleView;
	private View mViewTop;
	private EditText mPlanSelectEditText;
	private EditText mSelectContract;
	private EditText mInventorySelectEditText;
	private EditText mInventoryPeopleEditText;
	private PurchasePlanDialog mPurchasePlanDialog;
	private String[] mTopDialogNames;

	@SuppressLint("UseSparseArrays") private void initViewTopWindow() {
		mTopTitleView = new BaseWindow(this);

		Map<Integer, Integer> buttons = new HashMap<Integer, Integer>();

		Map<Integer, String[]> widgetContent = new HashMap<Integer, String[]>();

		if (mCurrentStatus == INFO_STATUS) {
			for (int i = 0; i < 5; i++) {
				buttons.put(i, BaseDialog.editTextReadOnlyLineStyle);
			}
			mTopTitleView.init(R.array.purchase_executive_add_title,
					buttons, widgetContent, false, 3);
			mTopTitleView.getPopupView().findViewById(mTopTitleView.baseEditTextId).setBackground(null);
		} else {
			if (mCurrentStatus == ADD_STATUS) {
				buttons.put(1, BaseDialog.editTextClickLineStyle);
			} else if (mCurrentStatus == MODIFY_STATUS){
				buttons.put(1, BaseDialog.editTextReadOnlyLineStyle);
			}
			buttons.put(0, BaseDialog.editTextReadOnlyLineStyle);
			buttons.put(2, BaseDialog.editTextClickLineStyle);
			buttons.put(3, BaseDialog.editTextClickLineStyle);
			buttons.put(4, BaseDialog.editTextClickLineStyle);
			mTopTitleView.init(R.array.purchase_executive_add_title,
					buttons, widgetContent, false, 3);
			
			if (mCurrentStatus == ADD_STATUS) {
				mPurchasePlanDialog = new PurchasePlanDialog(this, mPlanDialogInterface);
				mPurchasePlanDialog.initPlanDialog();
				mPlanSelectEditText = (EditText) mTopTitleView.getPopupView().findViewById(mTopTitleView.baseEditTextId + 1);
				mPlanSelectEditText.setOnClickListener(new OnClickListener() {
					
					@Override
					public void onClick(View v) {
						mPurchasePlanDialog.show(mCGList);
					}
				});
			}

			mTopTitleView.getPopupView().findViewById(mTopTitleView.baseEditTextId).setBackground(null);
			mSelectContract = (EditText) mTopTitleView.getPopupView().findViewById(mTopTitleView.baseEditTextId + 2);
			mSelectContract.setOnClickListener(new OnClickListener() {
				
				@Override
				public void onClick(View v) {
					if (mCurrentCGJH == null && mMsgData.getProject_id() == 0) {
						Toast.makeText(PurchaseExecutiveAddActivity.this, R.string.pls_select_purchase_plan, Toast.LENGTH_SHORT).show();
						return;
					}
					Intent intent = new Intent(v.getContext(), /*ContractSelectActivity.class*/ null);

					Project project = new Project();
					if (mCurrentCGJH != null) {
						project.setProject_id(mCurrentCGJH.getProject_id());
						project.setTenant_id(mCurrentCGJH.getTenant_id());
					} else {
						project.setProject_id(mMsgData.getProject_id());
						project.setTenant_id(mMsgData.getTenant_id());
					}

					intent.putExtra("title", getString(R.string.contract_select));
					intent.putExtra("project", project);
					intent.putExtra("type", 3);

					startActivityForResult(intent, SELECT_CONTRACT);
				}
			});
			
			mInventorySelectEditText = (EditText) mTopTitleView.getPopupView().findViewById(mTopTitleView.baseEditTextId + 3);
			mInventorySelectEditText.setOnClickListener(new OnClickListener() {
				
				@Override
				public void onClick(View v) {
					
					if (mCurrentCGJH == null && mMsgData.getProject_id() == 0) {
						Toast.makeText(PurchaseExecutiveAddActivity.this, R.string.pls_select_purchase_plan, Toast.LENGTH_SHORT).show();
						return;
					}
					Intent intent = new Intent(v.getContext(), StoreHouseSelectActivity.class);

					int projectID = 0;
					if (mCurrentCGJH != null) {
						projectID = mCurrentCGJH.getProject_id();
					} else {
						projectID = mMsgData.getProject_id();
					}

					intent.putExtra(StoreHouseSelectActivity.PROJECT_ID, projectID);

					startActivityForResult(intent, INVENTORY_SELECT_CODE);
				}
			});
			
			mInventoryPeopleEditText = (EditText) mTopTitleView.getPopupView().findViewById(mTopTitleView.baseEditTextId + 4);
			mInventoryPeopleEditText.setOnClickListener(new OnClickListener() {
				
				@Override
				public void onClick(View v) {
					if (mCurrentCGJH == null && mMsgData.getProject_id() == 0) {
						Toast.makeText(PurchaseExecutiveAddActivity.this, R.string.pls_select_purchase_plan, Toast.LENGTH_SHORT).show();
						return;
					}
					Intent intent = new Intent();
	                intent.setClass(v.getContext(), OwnerSelectActivity.class);
	                intent.putExtra("title", getString(R.string.owner));
	                Project project = new Project();
	                if (mCurrentCGJH != null) {
	                	project.setProject_id(mCurrentCGJH.getProject_id());
		                project.setTenant_id(mCurrentCGJH.getTenant_id());
	                } else {
	                	project.setProject_id(mMsgData.getProject_id());
		                project.setTenant_id(mMsgData.getTenant_id());
	                }
	                intent.putExtra("project", project);
	                startActivityForResult(intent, OWNER_SELECT_CODE);
				}
			});
		}
		if (mCurrentStatus != ADD_STATUS) {
			int widgetCount = 5;
			String[] editTexts = new String[widgetCount];
			editTexts[--widgetCount] = UserCache.getUserMaps().get(Integer.toString(mMsgData.getStoreman()));
			editTexts[--widgetCount] = mMsgData.getStorehouse();
			editTexts[--widgetCount] = mMsgData.getCght_name();
			editTexts[--widgetCount] = mMsgData.getCgjh_name();
			editTexts[--widgetCount] = mMsgData.getCg_number();

			mTopTitleView.SetDefaultValue(editTexts);
		} else {
			RemoteCommonService.getInstance().getCodeByDate(mCodeManager, "CG");
		}

		mTopDialogNames = getResources().getStringArray(
				R.array.purchase_executive_add_title);
		mViewTop = mTopTitleView.getPopupView();
	}

	private DataManagerInterface mCodeManager = new DataManagerInterface() {

		@Override
		public void getDataOnResult(ResultStatus status, List<?> list) {

			if (status.getCode() == AnalysisManager.SUCCESS_DB_QUERY) {
				String[] editTexts = new String[5];
				editTexts[0] = status.getMessage();
				mTopTitleView.SetDefaultValue(editTexts);
			}
		}

	};

	private View mViewExecutiveList;
	private View mHeaderView;
	private String[] mListHeadNames;
	private int[] mDisplayItemIds;
	private ListView mExecutiveAddListView;
	private DataListAdapter<P_CGD> mListAdapter;

	@SuppressWarnings("unchecked")
	private void initPlanListView() {
		LayoutInflater inflater = LayoutInflater.from(this);
		mViewExecutiveList = inflater.inflate(R.layout.purchase_executive_add,
				null);
		/* 采购列表头布局 */
		mHeaderView = mViewExecutiveList.findViewById(R.id.listHeaderView);
		/* 获取列表使用的相关资源 */
		TypedArray typedArray = getResources().obtainTypedArray(
				R.array.purchase_executive_add_item_ids);
		mListHeadNames = getResources().getStringArray(
				R.array.purchase_executive_add_item_names);
		mExecutiveAddListView = (ListView) mViewExecutiveList
				.findViewById(R.id.listView);

		if (mListHeadNames != null) {
			mDisplayItemIds = new int[typedArray.length()];
			for (int i = 0; i < mDisplayItemIds.length; i++) {
				mDisplayItemIds[i] = typedArray.getResourceId(i, 0);
				TextView tv = (TextView) mHeaderView
						.findViewById(mDisplayItemIds[i]);
				String text = "<b>" + mListHeadNames[i] + "</b>";
				tv.setText(Html.fromHtml(text));
				tv.setTextSize(TypedValue.COMPLEX_UNIT_PX, getResources().getDimension(R.dimen.ticket_table_text_size));
				tv.setGravity(Gravity.CENTER);
				tv.setPadding(0, 0, 0, 0);
			}
		}
		typedArray.recycle();

		/* ListView适配器初始化 */
		mListAdapter = new DataListAdapter<P_CGD>(this, mListAdapterManager);
		mExecutiveAddListView.setAdapter(mListAdapter);

	}

	private long mDismissTime;

	private OptionsMenuView createOptionsMenuView(String[] subMenuNames) {
		OptionsMenuView optionsMenus = new OptionsMenuView(this, subMenuNames);
		optionsMenus.setSubMenuListener(new OptionsMenuView.SubMenuListener() {
			@Override
			public void onSubMenuClick(View view) {
				mDismissTime = System.currentTimeMillis();
				switch ((Integer) view.getTag()) {
				case 0:
					WZArrival();
					break;
				case 1:
					WZEmpty();
					break;
				}
				mOptionsMenuView.dismiss();
				mOutWZMenuView.dismiss();
			}
		});
		return optionsMenus;
	}

	private void WZArrival() {
		if (mCurrentItem.getCgd_status() == 1 || mCurrentItem.getCgd_status() == 3) {
			mCurrentItem.setCgd_status(2);
			mCurrentItem.setStorehouse(mMsgData.getStorehouse());
			mCurrentItem.setStoreman(mMsgData.getStoreman());
			LogUtil.d("wzw mCurrentItem:" + mCurrentItem);
			int i;
			for (i = 0; i < mListAdapter.getDataShowList().size(); i++) {
				if (mListAdapter.getDataShowList().get(i).getCgd_status() != 2) {
					break;
				}
			}
			if (i == mListAdapter.getDataShowList().size()) {
				mMsgData.setCg_status(2);
				needShowToast = false;
				RemotePurchaseService.getInstance().updateCG(mDataManager,
						mMsgData, mListAdapter.getDataShowList());
			}
			RemotePurchaseService.getInstance().updateCGStatus(mDataManager, mCurrentItem);
		}
	}

	private void WZEmpty() {
		if (mCurrentItem.getCgd_status() == 1) {
			mCurrentItem.setCgd_status(3);
			RemotePurchaseService.getInstance().updateCGStatus(mDataManager, mCurrentItem);
		}
	}

	/**
	 * bottom button init
	 */
	private void initButtons() {
		Button saveBottomButton = (Button) findViewById(R.id.save);
		Button add = (Button) findViewById(R.id.add);
		if (mCurrentStatus == INFO_STATUS) {
			saveBottomButton.setVisibility(View.GONE);
			add.setVisibility(View.GONE);
		} else {
			add.setVisibility(View.GONE);
		}
		saveBottomButton.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				Map<String, String> items = mTopTitleView.SaveData();
				if (items.get(mTopDialogNames[0]).equals("") || items.get(mTopDialogNames[1]).equals("")
						|| items.get(mTopDialogNames[2]).equals("")
						|| items.get(mTopDialogNames[3]).equals("")
						|| items.get(mTopDialogNames[4]).equals("")) {
					BaseToast.show(PurchaseExecutiveAddActivity.this, BaseToast.NULL_MSG);
					return;
				}
				if (requestServerDataFlag) {
					LogUtil.i("wzw request ServerData");
					return;
				} else {
					requestServerDataFlag = true;
				}
				if (mCurrentCGJH != null) {
					mMsgData.setCgjh_id(mCurrentCGJH.getCgjh_id());
					mMsgData.setCgjh_name(mCurrentCGJH.getCgjh_name());
					mMsgData.setProject_id(mCurrentCGJH.getProject_id());
				}
				mMsgData.setCght_name(items.get(mTopDialogNames[2]));  
				mMsgData.setCg_number(items.get(mTopDialogNames[0]));
				mMsgData.setStorehouse(items.get(mTopDialogNames[3]));
				mMsgData.setStoreman(UserCache.getUserListsMap().get((items.get(mTopDialogNames[4]))));
				mMsgData.setTenant_id(UserCache.getCurrentUser().getTenant_id());
				double money = 0;
				for (int i = 0; i < mListAdapter.getDataShowList().size(); i++) {
					money += mListAdapter.getDataShowList().get(i).getMoney();
				}
				mMsgData.setCg_money(money);
				String date = items.get(mTopDialogNames[0]).substring(2, 10);
				mMsgData.setCreate_date(DateUtils.stringToDate("yyyyMMdd", date));
				
				if (mCurrentStatus == ADD_STATUS) {
//					RemotePurchaseService.getInstance().addCG(mDataManager,
//							mMsgData, mListAdapter.getDataShowList());
				} else if (mCurrentStatus == MODIFY_STATUS) {
			        if (calculatePurchaseStatus()) {
			        	mMsgData.setCg_status(2);
			        }
			        isModifyStatusOnly = false;
					RemotePurchaseService.getInstance().updateCG(mDataManager,
							mMsgData, mListAdapter.getDataShowList());
				}
			}
		});
	}

	private void loadData() {
		RemotePurchaseService.getInstance().getCGD(mDataManager,
				mMsgData.getCg_id());
	}
	
	private P_CGJH mCurrentCGJH;
	private PlanDialogInterface mPlanDialogInterface = new PlanDialogInterface() {

		@Override
		public void setData(List<P_CGD> cgdList, P_CGJH cgjh) {
			mListAdapter.setShowDataList(cgdList);
			mCurrentCGJH = cgjh;
			mPlanSelectEditText.setText(mCurrentCGJH.getCgjh_name());
			mInventorySelectEditText.setText("");
			mInventoryPeopleEditText.setText("");
		}
		
	};

	@SuppressLint("HandlerLeak") private Handler mToastHandler = new Handler() {
		public void handleMessage(Message msg) {
			Toast.makeText(PurchaseExecutiveAddActivity.this,
					(CharSequence) msg.obj, Toast.LENGTH_SHORT).show();
		}
	};
	
	private boolean calculatePurchaseStatus() {
		int i = 0;
		for (i = 0; i < mListAdapter.getDataShowList().size(); i++) {
			if (2 != mListAdapter.getDataShowList().get(i).getCgd_status()) {
				break;
			}
		}
		if (i == mListAdapter.getDataShowList().size()) {
			return true;
		}
		return false;
	}

	private boolean requestServerDataFlag = false;
	private DataManagerInterface mDataManager = new DataManagerInterface() {

		@SuppressWarnings("unchecked")
		@Override
		public void getDataOnResult(ResultStatus status, List<?> list) {
			if (status.getCode() != AnalysisManager.SUCCESS_DB_QUERY) {
				if (status.getCode() == AnalysisManager.SUCCESS_DB_UPDATE) {
					if (needShowToast) {
						Message msg = new Message();
						msg.obj = status.getMessage();
						mToastHandler.sendMessage(msg);
					} else {
						needShowToast = true;
					}
				} else {
					Message msg = new Message();
					msg.obj = status.getMessage();
					mToastHandler.sendMessage(msg);
				}
			}

			Intent intent;
			Bundle bundle;
			switch (status.getCode()) {
			case AnalysisManager.SUCCESS_DB_QUERY:
				if (list != null && list.size() != 0) {
					mListAdapter.setShowDataList((List<P_CGD>) list);
				}
				break;

			case AnalysisManager.SUCCESS_DB_DEL:
				break;

			case AnalysisManager.SUCCESS_DB_UPDATE:
				
		        if (isModifyStatusOnly) {
		        	loadData();
		        } else {
		        	intent = new Intent();
			        bundle = new Bundle();
			        bundle.putSerializable(PurchaseDataCache.RESULT_KEY_CODE, PurchaseDataCache.RESULT_UPDATE_CODE);
			        intent.putExtras(bundle);
			        setResult(Activity.RESULT_OK, intent);
		        	finish();
		        }
				
				break;
			case AnalysisManager.SUCCESS_DB_ADD:
				intent = new Intent();
		        bundle = new Bundle();
		        bundle.putSerializable(PurchaseDataCache.RESULT_KEY_CODE, PurchaseDataCache.RESULT_ADD_CODE);
		        intent.putExtras(bundle);
		        setResult(Activity.RESULT_OK, intent);
				finish();
				break;
			case AnalysisManager.EXCEPTION_DB_ADD:
			case AnalysisManager.EXCEPTION_DB_UPDATE:
				requestServerDataFlag = false;
				break;
			default:
				break;
			}
		}

	};

	private P_CGD mCurrentItem;
	private OptionsMenuView mOptionsMenuView;
	private OptionsMenuView mOutWZMenuView;
	@SuppressWarnings("rawtypes")
	DataListAdapter.ListAdapterInterface mListAdapterManager = new DataListAdapter.ListAdapterInterface() {

		@Override
		public int getLayoutId() {
			return R.layout.purchase_executive_add_list_item;
		}

		@Override
		public View getHeaderView() {
			return mHeaderView;
		}

		@Override
		public void regesterListeners(ViewHolder holder, final int position) {
			for (int i = 0; i < holder.tvs.length; i++) {
				// holder.tvs[i].setClickable(false);

				holder.tvs[i].setOnClickListener(new View.OnClickListener() {
					@Override
					public void onClick(View view) {
						long minus_time = System.currentTimeMillis()
								- mDismissTime;

						if (minus_time < 300)
							return;
						mCurrentItem = mListAdapter.getItem(position);
						if (mCurrentStatus != INFO_STATUS) {
							if (mCurrentItem.getCgd_status() == 1) {
								if (mMsgData.getCght_id() != 0 && mMsgData.getStoreman() != 0) {
									mOptionsMenuView.showAsDropDown(
											view,
											0,
											-view.getMeasuredHeight()
													- getResources()
															.getDimensionPixelSize(
																	R.dimen.popup_window_height));
								}
								
							} else if (mCurrentItem.getCgd_status() == 3) {
								mOutWZMenuView.showAsDropDown(
										view,
										0,
										-view.getMeasuredHeight()
												- getResources()
														.getDimensionPixelSize(
																R.dimen.popup_window_height));	
							}
							
						}

						mListAdapter.setSelected(position, true);
					}
				});

			}
		}

		@Override
		public void initListViewItem(View convertView, ViewHolder holder,
				DataListAdapter adapter, int position) {
			Map<String, String> listViewItem = beanToMap(adapter
					.getItem(position));
			for (int i = 0; i < mListHeadNames.length; i++) {
			    holder.tvs[i].setTextSize(TypedValue.COMPLEX_UNIT_PX, getResources().getDimension(R.dimen.ticket_table_text_size));
				holder.tvs[i].setText(listViewItem.get(mListHeadNames[i]));
			}

			// 将选中的列表项高亮
			if (mListAdapter.getSelectedList().contains((Integer) position)) {
				convertView.setBackgroundResource(R.color.touch_high_light);
			} else {
				convertView.setBackgroundColor(Color.TRANSPARENT);
			}
		}

		@Override
		public void initLayout(View convertView, ViewHolder holder) {
			holder.tvs = new TextView[mDisplayItemIds.length];
			for (int i = 0; i < mDisplayItemIds.length; i++) {
				holder.tvs[i] = (TextView) convertView
						.findViewById(mDisplayItemIds[i]);
			}
		}

		@Override
		public List findByCondition(Object... condition) {
			return null;
		}

		@Override
		public boolean isSameObject(Object t1, Object t2) {
			return false;
		}

	};

	private Map<String, String> beanToMap(Object bean) {
		Map<String, String> mapItem = new HashMap<String, String>();
		if (bean instanceof P_CGD) {
			P_CGD cgjhd = (P_CGD) bean;
			int count = 0;

			mapItem.put(mListHeadNames[count++], cgjhd.getWz_name());
			int critical = cgjhd.getWz_type_2() - 1;
			if (critical > 7) {
				critical = 7;
			} else if (critical < 0) {
				critical = 0;
			}
			if (cgjhd.getWz_type_1() == 1) {
				mapItem.put(mListHeadNames[count++],
						GLOBAL.CL_TYPE[critical][1]);
			} else {
				mapItem.put(mListHeadNames[count++],
						GLOBAL.SB_TYPE[critical][1]);
			}
			// mapItem.put(mListHeadNames[count++],
			// Integer.toString(cgjhd.get));//种类
			mapItem.put(mListHeadNames[count++], cgjhd.getWz_brand());
			mapItem.put(mListHeadNames[count++], cgjhd.getWz_spec());
			critical = cgjhd.getWz_unit() - 1;
			if (critical > 28) {
				critical = 28;
			} else if (critical < 0) {
				critical = 0;
			}
			mapItem.put(mListHeadNames[count++], GLOBAL.UNIT_TYPE[critical][1]);

			mapItem.put(mListHeadNames[count++],
					Double.toString(cgjhd.getCg_quantity()));
			
			mapItem.put(mListHeadNames[count++],
					UtilTools.formatMoney("¥", cgjhd.getUnit_price(), 2));//单价
			mapItem.put(mListHeadNames[count++],
					UtilTools.formatMoney("¥", cgjhd.getMoney(), 2));//金额
			mapItem.put(mListHeadNames[count++], cgjhd.getLwdw_name());
			critical = cgjhd.getCgd_status() - 1;
			if (critical > 2) {
				critical = 2;
			} else if (critical < 0) {
				critical = 0;
			}
			mapItem.put(mListHeadNames[count++],
					GLOBAL.CG_TYPE[critical][1]);
			mapItem.put(
					mListHeadNames[count++],
					DateUtils.dateToString(DateUtils.FORMAT_SHORT,
							cgjhd.getArrival_date()));
		}
		return mapItem;
	}
	
	public void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (resultCode == 0 || data == null) return;
        
        if (requestCode == OWNER_SELECT_CODE) {
			User user = (User) data.getSerializableExtra("user");
			if (user != null) {
				mInventoryPeopleEditText.setText(user.getName());
				for (int i = 0; i < mListAdapter.getDataShowList().size(); i++) {
	        		mListAdapter.getDataShowList().get(i).setStoreman(user.getUser_id());
	        	}
			}
        } else if (requestCode == INVENTORY_SELECT_CODE) {
        	String inventory = data.getStringExtra(StoreHouseSelectActivity.RESULT_KEY);
        	mInventorySelectEditText.setText(inventory);
        	for (int i = 0; i < mListAdapter.getDataShowList().size(); i++) {
        		mListAdapter.getDataShowList().get(i).setStorehouse(inventory);
        	}
        } else if (requestCode == SELECT_CONTRACT) {
        	Contract contract = (Contract) data.getSerializableExtra("contract");
        	if (contract != null) {
        		mSelectContract.setText(contract.getName());
        		mMsgData.setCght_id(contract.getContract_id());
        		mMsgData.setCght_name(contract.getName());
        	}
        	
        }
        
        super.onActivityResult(requestCode, resultCode, data);
    }
}
