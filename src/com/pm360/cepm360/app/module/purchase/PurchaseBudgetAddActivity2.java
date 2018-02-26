package com.pm360.cepm360.app.module.purchase;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Intent;
import android.content.res.TypedArray;
import android.graphics.Color;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.text.Html;
import android.util.TypedValue;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.pm360.cepm360.R;
import com.pm360.cepm360.app.cache.ProjectCache;
import com.pm360.cepm360.app.cache.UserCache;
import com.pm360.cepm360.app.common.activity.ActionBarActivity;
import com.pm360.cepm360.app.common.activity.DataManagerInterface;
import com.pm360.cepm360.app.common.activity.ListSelectActivity;
import com.pm360.cepm360.app.common.adpater.DataListAdapter;
import com.pm360.cepm360.app.common.adpater.DataListAdapter.ViewHolder;
import com.pm360.cepm360.app.common.view.OptionsMenuView;
import com.pm360.cepm360.app.common.view.parent.BaseDialog;
import com.pm360.cepm360.app.common.view.parent.BaseToast;
import com.pm360.cepm360.app.common.view.parent.BaseWindow;
import com.pm360.cepm360.app.module.resource.EquipmentFragment;
import com.pm360.cepm360.app.module.resource.MaterialFragment;
import com.pm360.cepm360.app.utils.UtilTools;
import com.pm360.cepm360.common.GLOBAL;
import com.pm360.cepm360.common.ResultStatus;
import com.pm360.cepm360.common.json.AnalysisManager;
import com.pm360.cepm360.common.util.LogUtil;
import com.pm360.cepm360.entity.P_CGYS;
import com.pm360.cepm360.entity.P_CGYSD;
import com.pm360.cepm360.entity.P_WZ;
import com.pm360.cepm360.services.common.RemoteCommonService;
import com.pm360.cepm360.services.purchase.RemoteBudgetService;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class PurchaseBudgetAddActivity2 extends ActionBarActivity {
	private int mCurrentStatus;
	private final int MODIFY_STATUS = 1;
	private final int INFO_STATUS = 2;
	private final int ADD_STATUS = 3;
	private P_CGYS mMsgYSData;
	private int MATERIAL_SELECT_CODE = 200;
	private int EQUIPMENT_SELECT_CODE = 201;
	
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);;
		getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,
				WindowManager.LayoutParams.FLAG_FULLSCREEN);
		setContentView(R.layout.purchase_plan_ticket_activity);
		getMenuView().setVisibility(View.GONE);
		
		msgHandlerProgress();
		initViewTopWindow();
		// we need init dialog before listView
		initListViewDialog();
		initPlanListView();
		initButtons();
		View[] views = new View[2];
		views[0] = mViewTop;
		views[1] = mViewBudgetList;
		initWindows(views, true);
		
		if (mCurrentStatus != ADD_STATUS) {
			loadData();
		}
		
		mOptionsMenuView = createOptionsMenuView(new String[] {
				getResources().getString(R.string.purchase_modify),
				getResources().getString(R.string.purchase_delete) });
	}
	
	private TextView mTotalTextView;
	private TextView mMarkTextView;
	private View initWindows(View[] views, boolean totalFlag) {
		LinearLayout parentLayout = (LinearLayout) findViewById(R.id.parent_id);

		int px = UtilTools.dp2pxH(getBaseContext(), 8);
		views[0].setPadding(0, px, 0, px);
		parentLayout.addView(views[0]);
		parentLayout.addView(views[1]);
		if (totalFlag == true) {
			View itemView = LayoutInflater.from(parentLayout.getContext())
					.inflate(R.layout.base_ticket_activity2_bottom_item,
							parentLayout, false);
			parentLayout.addView(itemView);
			mTotalTextView = (TextView) itemView.findViewById(R.id.total_content);
			mMarkTextView = (TextView) itemView.findViewById(R.id.remark_content_edit);
		}
		
		if (mCurrentStatus == INFO_STATUS) {
			mMarkTextView.setFocusableInTouchMode(false);
			mMarkTextView.clearFocus();
		}
		return parentLayout;
	}

	
	/**
	 * we should handle msg first
	 */
	private void msgHandlerProgress() {
		Intent intent = getIntent();
		if (intent.getSerializableExtra("purchase_modify") != null) {
			mCurrentStatus = MODIFY_STATUS;
			mMsgYSData = (P_CGYS) intent
					.getSerializableExtra("purchase_modify");
		} else if (intent.getSerializableExtra("purchase_info") != null) {
			mCurrentStatus = INFO_STATUS;
			mMsgYSData = (P_CGYS) intent
					.getSerializableExtra("purchase_info");
		} else {
			mMsgYSData = new P_CGYS();
			mCurrentStatus = ADD_STATUS;
		}
	}
	
	private Button initViewTopButton(int i) {
		Button btn = new Button(this);
		btn.setPadding(0, 0, 0, 0);
		btn.setBackgroundResource(R.drawable.ticket_button_bg);
		btn.setTextColor(Color.BLACK);
		switch (i) {
		case 0:
			btn.setText(R.string.select_material);
			btn.setOnClickListener(new OnClickListener() {
				
				@Override
				public void onClick(View v) {
					Intent data;
					Bundle bundle;
					data = new Intent(PurchaseBudgetAddActivity2.this, ListSelectActivity.class);
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
			});
			
			break;
		case 1:
			btn.setText(R.string.select_equipment);
			btn.setOnClickListener(new OnClickListener() {
				
				@Override
				public void onClick(View v) {
					Intent data;
					Bundle bundle;
					data = new Intent(PurchaseBudgetAddActivity2.this, ListSelectActivity.class);
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
			});
			
			break;
		}

		return btn;
	}
	
	private void initViewTopButtons() {
		LinearLayout line = (LinearLayout) mTopTitleView.getPopupView().findViewById(mTopTitleView.baseLineId + 1);
		
		LinearLayout.LayoutParams layoutParams1 = new LinearLayout.LayoutParams(
				getResources().getDimensionPixelSize(R.dimen.ticket_button_width), getResources().getDimensionPixelSize(R.dimen.ticket_button_height));
		layoutParams1.setMargins(0, 10, 10, 10);
		for (int i = 0; i < 2; i++) {
			Button btn = initViewTopButton(i);
			line.addView(btn, layoutParams1);
		}
	}
	
	private BaseWindow mTopTitleView;
	private View mViewTop;
	private String[] mTopDialogNames;
	/**
	 * top view init
	 */
	@SuppressLint("UseSparseArrays") private void initViewTopWindow() {
		mTopTitleView = new BaseWindow(this);

		Map<Integer, Integer> buttons = new HashMap<Integer, Integer>();

		if (mCurrentStatus == INFO_STATUS) {
			for (int i = 0; i < 4; i++) {
				buttons.put(i, BaseDialog.editTextReadOnlyLineStyle);
			}
			mTopTitleView.init(R.array.purchase_budget_add_title, buttons,
					null, false, 3);
			mTopTitleView.getPopupView().findViewById(mTopTitleView.baseEditTextId).setBackground(null);
		} else {
			buttons.put(0, BaseDialog.editTextReadOnlyLineStyle);
			buttons.put(2, BaseDialog.numberEditTextLineStyle);
			mTopTitleView.init(R.array.purchase_budget_add_title, buttons,
					null, false, 3);

			mTopTitleView.getPopupView().findViewById(mTopTitleView.baseEditTextId).setBackground(null);
			/* select_material and select_equipment buttons */
			initViewTopButtons();
			
			mTopDialogNames = getResources().getStringArray(
					R.array.purchase_budget_add_title);
		}
		
		if (mCurrentStatus != ADD_STATUS) {
			String[] editTexts = new String[4];
			editTexts[0] = mMsgYSData.getCgys_number();
			editTexts[1] = mMsgYSData.getCgys_name();
			editTexts[2] = Integer.toString(mMsgYSData.getWorkload());
			editTexts[3] = mMsgYSData.getTask_name();
			
			mTopTitleView.SetDefaultValue(editTexts);
		} else {
			RemoteCommonService.getInstance().getCodeByDate(mCodeManager, "CGYS");
		}
		
		mViewTop = mTopTitleView.getPopupView();
	}
	
	private DataManagerInterface mCodeManager = new DataManagerInterface() {

		@Override
		public void getDataOnResult(ResultStatus status, List<?> list) {

			if (status.getCode() == AnalysisManager.SUCCESS_DB_QUERY) {
				String[] editTexts = new String[4];
				editTexts[0] = status.getMessage();
				mTopTitleView.SetDefaultValue(editTexts);
			}
		}
		
	};
	
	private View mViewBudgetList;
	private View mHeaderView;
	private String[] mListHeadNames;
	private int[] mDisplayItemIds;
	private ListView planAddListView;
	private DataListAdapter<P_CGYSD> mListAdapter;
	private List<P_CGYSD> mRemoveList = new ArrayList<P_CGYSD>();
	/**
	 * list view init
	 */
	@SuppressWarnings("unchecked")
	private void initPlanListView() {
		LayoutInflater inflater = LayoutInflater.from(this);
		mViewBudgetList = inflater.inflate(R.layout.purchase_budget_add, null);
		/* 采购列表头布局 */
		mHeaderView = mViewBudgetList.findViewById(R.id.listHeaderView);
		/* 获取列表使用的相关资源 */
		TypedArray typedArray = getResources().obtainTypedArray(
				R.array.purchase_budget_add_item_ids);
		mListHeadNames = getResources().getStringArray(
				R.array.purchase_budget_add_item_names);
		planAddListView = (ListView) mViewBudgetList.findViewById(R.id.listView);

		if (mListHeadNames != null) {
			mDisplayItemIds = new int[typedArray.length()];
			for (int i = 0; i < mDisplayItemIds.length; i++) {
				mDisplayItemIds[i] = typedArray.getResourceId(i, 0);
				TextView tv = (TextView) mHeaderView.findViewById(mDisplayItemIds[i]);
				String text = "<b>" + mListHeadNames[i] + "</b>";
				tv.setText(Html.fromHtml(text));
				tv.setTextSize(TypedValue.COMPLEX_UNIT_PX, getResources().getDimension(R.dimen.ticket_table_text_size));
				tv.setGravity(Gravity.CENTER);
				tv.setPadding(0, 0, 0, 0);
			}
		}
		typedArray.recycle();

		/* ListView适配器初始化 */
		mListAdapter = new DataListAdapter<P_CGYSD>(this, mListAdapterManager);
		planAddListView.setAdapter(mListAdapter);

	}
	
	private BaseDialog mListViewDialog;
	private String[] mListDialogNames;
	/**
	 * modify quantity radio and unitPrice for current P_CGYSD
	 */
	@SuppressLint("UseSparseArrays") private void initListViewDialog() {
		mListViewDialog = new BaseDialog(this, R.string.purchase_budget_modify);

		if (mCurrentStatus != INFO_STATUS) {
			Map<Integer, Integer> buttons = new HashMap<Integer, Integer>();
			buttons.put(0, BaseDialog.decimalEditTextLineStyle);
			buttons.put(1, BaseDialog.decimalEditTextLineStyle);
			mListViewDialog.init(R.array.purchase_budget_add_dialog, buttons,
					null);
			mListDialogNames = getResources().getStringArray(R.array.purchase_budget_add_dialog);
			Button saveImageView = (Button) mListViewDialog.getPopupView()
					.findViewById(R.id.save_Button);
			saveImageView.setOnClickListener(new OnClickListener() {
				
				@Override
				public void onClick(View v) {
					Map<String, String> items = mListViewDialog.SaveData();
					int count = 0;
					if (items.get(mListDialogNames[0]).equals("") || items.get(mListDialogNames[1]).equals("")) {
						BaseToast.show(getBaseContext(), BaseToast.NULL_MSG);
						return;
					}
					double quantity = Double.parseDouble(items.get(mListDialogNames[count++]));
					double unitPrice = Double.parseDouble(items.get(mListDialogNames[count++]));
					double money = quantity * unitPrice;
					mCurrentItem.setQuantity(quantity);
					mCurrentItem.setUnit_price(unitPrice);
					mCurrentItem.setMoney(money);
					
					if (mCurrentItem.getIDU() == 0) {
						mCurrentItem.setIDU(GLOBAL.IDU_UPDATE);
					}
					setTotalTextView();
					LogUtil.d("wzw mLine:" + mLine);
					mListAdapter.updateData(mLine, mCurrentItem);
					mListViewDialog.dismiss();
				}
			});
		}
	}
	
	private long mDismissTime;
	private OptionsMenuView createOptionsMenuView(String[] subMenuNames) {
		OptionsMenuView optionsMenus = new OptionsMenuView(this,
				subMenuNames);
		optionsMenus.setSubMenuListener(new OptionsMenuView.SubMenuListener() {
			@Override
			public void onSubMenuClick(View view) {
				mDismissTime = System.currentTimeMillis();
				switch ((Integer) view.getTag()) {
				case 0:
					modifyTicket();
					break;
				case 1:
					deleteTicket();
					break;
				}
				mOptionsMenuView.dismiss();
			}
		});
		return optionsMenus;
	}
	
	// 修改
	private void modifyTicket() {
		String[] editTexts = new String[2];
		if (mCurrentItem.getQuantity() == 0) {
			
		} else {
			editTexts[0] = Double.toString(mCurrentItem.getQuantity());	
		}
		
		if (mCurrentItem.getUnit_price() == 0) {
			
		} else {
			editTexts[1] = Double.toString(mCurrentItem.getUnit_price());	
		}
		mListViewDialog.show(editTexts);
	}

	// 删除
	private void deleteTicket() {
		UtilTools.deleteConfirm(this, new UtilTools.DeleteConfirmInterface() {

			@Override
			public void deleteConfirmCallback() {
				if (mCurrentItem.getIDU() != GLOBAL.IDU_INSERT) {
					mCurrentItem.setIDU(GLOBAL.IDU_DELETE);
					mRemoveList.add(mCurrentItem);
					mListAdapter.deleteData(mLine);
				}
				
			}
			
		});
		
	}
	
	/**
	 * bottom button init
	 */
	private void initButtons() {
		Button saveBottomButton = (Button) findViewById(R.id.save);
		if (mCurrentStatus == INFO_STATUS) {
			saveBottomButton.setVisibility(View.GONE);
		}
		saveBottomButton.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View v) {
				// set top item data
				Map<String, String> items = mTopTitleView.SaveData();
				if (items.get(mTopDialogNames[0]).equals("") || items.get(mTopDialogNames[1]).equals("") || items.get(mTopDialogNames[2]).equals("")) {
					BaseToast.show(getBaseContext(), BaseToast.NULL_MSG);
					return;
				}
				
				if (mListAdapter.getDataShowList().size() == 0) {
					Toast.makeText(PurchaseBudgetAddActivity2.this, R.string.list_view_is_null, Toast.LENGTH_SHORT).show();
					return;
				}
				if (requestServerDataFlag) {
					LogUtil.i("wzw request ServerData");
					return;
				} else {
					requestServerDataFlag = true;
				}
				mMsgYSData.setCgys_number(items.get(mTopDialogNames[0]));
				mMsgYSData.setCgys_name(items.get(mTopDialogNames[1]));
				mMsgYSData.setWorkload(Integer.parseInt(items.get(mTopDialogNames[2])));
				mMsgYSData.setPurchase_item(mListAdapter.getDataShowList().size());
				mMsgYSData.setTask_name(items.get(mTopDialogNames[3]));
				mMsgYSData.setTenant_id(UserCache.getCurrentUser().getTenant_id());
				mMsgYSData.setProject_id(ProjectCache.getCurrentProject().getProject_id());
				
				double money = 0;
				for (int i = 0; i < mListAdapter.getDataShowList().size(); i++) {
					money += mListAdapter.getDataShowList().get(i).getMoney();
				}
				mMsgYSData.setTotal_money(money);
				
				// set list item data
				List<P_CGYSD> listYS = new ArrayList<P_CGYSD>();
				listYS.addAll(mListAdapter.getDataShowList());
				listYS.addAll(mRemoveList);
				if (mCurrentStatus == ADD_STATUS) {
					mMsgYSData.setCreator(UserCache.getCurrentUser().getUser_id());
					RemoteBudgetService.getInstance().addCGYS(mDataManager, mMsgYSData, listYS);
				} else if (mCurrentStatus == MODIFY_STATUS) {
					RemoteBudgetService.getInstance().updateCGYS(mDataManager, mMsgYSData, listYS);
				}
			}
		});

	}
	
	private void loadData() {
		RemoteBudgetService.getInstance().getCGYSD(mDataManager, mMsgYSData.getCgys_id());
	}
	
	@SuppressLint("HandlerLeak") private Handler mToastHandler = new Handler() {
        public void handleMessage(Message msg) {
            Toast.makeText(PurchaseBudgetAddActivity2.this, (CharSequence) msg.obj,
                    Toast.LENGTH_SHORT).show();
        }
	};

	private boolean requestServerDataFlag = false;
	private DataManagerInterface mDataManager = new DataManagerInterface() {

		@SuppressWarnings("unchecked")
		@Override
		public void getDataOnResult(ResultStatus status, List<?> list) {
			if (status.getCode() != AnalysisManager.SUCCESS_DB_QUERY) {
                Message msg = new Message();
                msg.obj = status.getMessage();
                mToastHandler.sendMessage(msg);
            }

			Intent intent;
			Bundle bundle;
			switch (status.getCode()) {
			case AnalysisManager.SUCCESS_DB_QUERY:
				if (list != null && list.size() != 0) {
					mListAdapter.setShowDataList((List<P_CGYSD>) list);
					setTotalTextView();
				}
				break;

			case AnalysisManager.SUCCESS_DB_UPDATE:
				intent = new Intent();
		        bundle = new Bundle();
		        bundle.putSerializable(PurchaseDataCache.RESULT_KEY_CODE, PurchaseDataCache.RESULT_UPDATE_CODE);
		        intent.putExtras(bundle);
		        setResult(Activity.RESULT_OK, intent);
				finish();
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

	private P_CGYSD mCurrentItem;
	private OptionsMenuView mOptionsMenuView;
	private int mLine;
	@SuppressWarnings("rawtypes")
	DataListAdapter.ListAdapterInterface mListAdapterManager = new DataListAdapter.ListAdapterInterface() {

		@Override
		public int getLayoutId() {
			return R.layout.purchase_budget_add_list_item;
		}

		@Override
		public View getHeaderView() {
			return mHeaderView;
		}

		@Override
		public void regesterListeners(ViewHolder holder, final int position) {
			for (int i = 0; i < holder.tvs.length; i++) {
				//holder.tvs[i].setClickable(false);

				if ((mCurrentStatus != INFO_STATUS) && (i == 5 || i == 6 || i == 8)) {
					holder.tvs[i].setOnClickListener(new View.OnClickListener() {
						@Override
						public void onClick(View view) {
							mCurrentItem = mListAdapter.getItem(position);
							mLine = position;
							modifyTicket();
						}
					});
				} else {
					holder.tvs[i].setOnClickListener(new View.OnClickListener() {
						@Override
						public void onClick(View view) {
							long minus_time = System.currentTimeMillis()
									- mDismissTime;

							if (minus_time < 300)
								return;
							if (mCurrentStatus != INFO_STATUS) {
								mOptionsMenuView
								.showAsDropDown(
										view,
										0,
										-view.getMeasuredHeight()
												- getResources()
														.getDimensionPixelSize(
																R.dimen.popup_window_height));
							}
							
							mListAdapter.setSelected(position, true);
							mCurrentItem = mListAdapter.getItem(position);

							mLine = position;
						}
					});
				}
				
			}
		}

		@Override
		public void initListViewItem(View convertView, ViewHolder holder,
				DataListAdapter adapter, int position) {
			Map<String, String> listViewItem = beanToMap(adapter.getItem(position));
			for (int i = 0; i < mListHeadNames.length; i++) {
				if (mCurrentStatus != INFO_STATUS) {
					if (i == 5 || i == 6 || i == 8) {
						Drawable drawable= getResources().getDrawable(R.drawable.icon_modify);
				        // 这一步必须要做,否则不会显示.
				        drawable.setBounds(0, 0, 25, 25);
				        holder.tvs[i].setCompoundDrawables(null,null,drawable,null);
				        holder.tvs[i].setTextColor(Color.RED);
					}
					if (i == 8) {
				        holder.tvs[i].setPadding(0, 0, 0, 0);
					}
				}
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
			return ((P_CGYSD)t1).getCgysd_id() == ((P_CGYSD)t2).getCgysd_id();
		}
		
	};
	
	private Map<String, String> beanToMap(Object bean) {
		Map<String, String> mapItem = new HashMap<String, String>();
		if (bean instanceof P_CGYSD) {
			P_CGYSD cgysd = (P_CGYSD)bean;
			int count = 0;

			mapItem.put(mListHeadNames[count++], cgysd.getWz_name());
			int critical = cgysd.getWz_type_2() - 1;
			if (critical > 7) {
				critical = 7;
			} else if (critical < 0) {
				critical = 0;
			}
			
			if (cgysd.getWz_type_1() == 1) {
				mapItem.put(mListHeadNames[count++],
						GLOBAL.CL_TYPE[critical][1]);
			} else {
				mapItem.put(mListHeadNames[count++],
						GLOBAL.SB_TYPE[critical][1]);
			}
			mapItem.put(mListHeadNames[count++], cgysd.getWz_brand());
			mapItem.put(mListHeadNames[count++], cgysd.getWz_spec());
			
			critical = cgysd.getWz_unit() - 1;
			if (critical > 28) {
				critical = 28;
			} else if (critical < 0) {
				critical = 0;
			}
			mapItem.put(mListHeadNames[count++], GLOBAL.UNIT_TYPE[critical][1]);
			
			mapItem.put(mListHeadNames[count++], Double.toString(cgysd.getQuantity()));
			
			mapItem.put(mListHeadNames[count++], UtilTools.formatMoney("¥", cgysd.getUnit_price(), 2));
			mapItem.put(mListHeadNames[count++], UtilTools.formatMoney("¥", cgysd.getMoney(), 2));
		}
		return mapItem;
	}
	
	private void setTotalTextView() {
		Double allMoney = 0.0;
		for (int i = 0; i < mListAdapter.getDataShowList().size(); i++) {
			allMoney += mListAdapter.getDataShowList().get(i).getMoney();
			LogUtil.d("wzw allMoney " + allMoney);
		}
		mTotalTextView.setText(UtilTools.formatMoney("¥", allMoney, 2));
	}

	// the same as GLOBAL WZ_TYPE
	private final int MATERIAL_TYPE = 1;
	private final int EQUIPMENT_TYPE = 2;
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
//        	LogUtil.d("wzw list" + wz);
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
}
