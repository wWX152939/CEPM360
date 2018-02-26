package com.pm360.cepm360.app.module.purchase;

import android.annotation.SuppressLint;
import android.app.ActionBar.LayoutParams;
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
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.pm360.cepm360.R;
import com.pm360.cepm360.app.cache.UserCache;
import com.pm360.cepm360.app.common.activity.ActionBarActivity;
import com.pm360.cepm360.app.common.activity.DataManagerInterface;
import com.pm360.cepm360.app.common.activity.ListSelectActivity;
import com.pm360.cepm360.app.common.activity.OwnerSelectActivity;
import com.pm360.cepm360.app.common.activity.ProjectSelectActivity;
import com.pm360.cepm360.app.common.adpater.DataListAdapter;
import com.pm360.cepm360.app.common.adpater.DataListAdapter.ViewHolder;
import com.pm360.cepm360.app.common.view.OptionsMenuView;
import com.pm360.cepm360.app.common.view.parent.BaseDialog;
import com.pm360.cepm360.app.common.view.parent.BaseToast;
import com.pm360.cepm360.app.common.view.parent.BaseWindow;
import com.pm360.cepm360.app.common.view.parent.FlowApprovalDialog;
import com.pm360.cepm360.app.module.purchase.PurchaseBudgetDialog.BudgetDialogInterface;
import com.pm360.cepm360.app.module.resource.ContactCompanyFragment;
import com.pm360.cepm360.app.module.resource.EquipmentFragment;
import com.pm360.cepm360.app.module.resource.MaterialFragment;
import com.pm360.cepm360.app.module.schedule.PlanMakeFragment;
import com.pm360.cepm360.app.utils.UtilTools;
import com.pm360.cepm360.common.GLOBAL;
import com.pm360.cepm360.common.ResultStatus;
import com.pm360.cepm360.common.json.AnalysisManager;
import com.pm360.cepm360.common.util.DateUtils;
import com.pm360.cepm360.common.util.LogUtil;
import com.pm360.cepm360.entity.Flow_approval;
import com.pm360.cepm360.entity.Flow_setting;
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
import com.pm360.cepm360.services.system.RemoteFlowSettingService;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;


public class PurchasePlanAddActivity2 extends ActionBarActivity {
	public final static String MSG_PURCHASE_PLAN_ACTION = "com.pm360.cepm360.action.purchase_plan";
	
	private FlowApprovalDialog mFlowApprovalDialog;

	private int mCurrentStatus;
	private final int MODIFY_STATUS = 1;
	private final int INFO_STATUS = 2;
	private final int ADD_STATUS = 3;
	private P_CGJH mMsgJHData;
	private int PROJECT_SELECT_CODE = 200;
	private int TASK_SELECT_CODE = 201;
	private int MATERIAL_SELECT_CODE = 202;
	private int EQUIPMENT_SELECT_CODE = 203;
	private int OWNER_SELECT_CODE = 204;
	private int PROVIDER_SELECT_CODE = 205;
	private int FLOW_APPROVAL_SELECT_CODE = 206;

	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		createProgressDialog(true, true);
		getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,
				WindowManager.LayoutParams.FLAG_FULLSCREEN);
		getMenuView().setVisibility(View.GONE);

		setContentView(R.layout.purchase_plan_ticket_activity);
		msgHandlerProgress();
		if (mMessage != null) {
			return;
		}
		initWindow();
	}

	private void initWindow() {
		switchBottomButtons();
		initViewTopWindow();
		initListViewDialog();
		initPlanListView();
		View[] views = new View[2];
		views[0] = mViewTop;
		views[1] = mViewPlanList;
		initWindows(views, true);

		if (mCurrentStatus != ADD_STATUS) {
			loadData();
		}

		mOptionsMenuView = createOptionsMenuView(new String[] {
				getResources().getString(R.string.purchase_modify),
				getResources().getString(R.string.purchase_delete) });
	}

	private boolean hasFlowApproval;

	private void switchBottomButtons() {
		Button saveBottomButton = (Button) findViewById(R.id.save);
		Button approvalButton = (Button) findViewById(R.id.approval);
		Button submitButton = (Button) findViewById(R.id.submit);
		LogUtil.i("wzw flow:" + hasFlowApproval);

		if (mCurrentStatus == INFO_STATUS) {
			if (!hasFlowApproval
					|| (hasFlowApproval && mMsgJHData.getStatus() == Integer
							.parseInt(GLOBAL.FLOW_APPROVAL_STATUS[0][0]))) {
				{
					saveBottomButton.setVisibility(View.GONE);
					return;
				}
			}
		}

		OnClickListener saveListener = (new OnClickListener() {

			@Override
			public void onClick(View v) {
				if (!checkAndSaveData()) {
					return;
				}
				// must judge at last
				if (requestServerDataFlag) {
					LogUtil.i("wzw request ServerData");
					return;
				} else {
					requestServerDataFlag = true;
				}

				PassCGJHDataToServer();
			}
		});

		OnClickListener approvalListener = new OnClickListener() {

			@Override
			public void onClick(View v) {
				Flow_approval flowApproval = new Flow_approval();
				flowApproval.setFlow_type(GLOBAL.FLOW_TYPE[3][0]);
				flowApproval.setType_id(mMsgJHData.getCgjh_id());
				if (mFlowApprovalDialog == null) {
					mFlowApprovalDialog = new FlowApprovalDialog(PurchasePlanAddActivity2.this, 
							flowApproval, mFlowSetting, mFlowApprovalManager);
				}
				
				if (mMsgJHData.getStatus() == Integer
						.parseInt(GLOBAL.FLOW_APPROVAL_STATUS[1][0])) {
					mFlowApprovalDialog.show(true);
				} else {
					mFlowApprovalDialog.show(false);
				}
			}
		};

		// first we should check whether open approval flow, and if it is open,
		// we invoke isSubmitButtonStyle function to judge if we need add two
		// buttons submit and save,
		// or add one button approval
		if (hasFlowApproval) {
			// sync GLOBAL.FLOW_APPROVAL_STATUS
			if (mMsgJHData.getStatus() == 0) {
				mMsgJHData.setStatus(1);
			}
			if (isSaveButtonStyle()) {

				saveBottomButton.setOnClickListener(saveListener);
				submitButton.setVisibility(View.VISIBLE);
				submitButton.setOnClickListener(new OnClickListener() {

					@Override
					public void onClick(View v) {

						if (!checkAndSaveData()) {
							return;
						}
						UtilTools.deleteConfirm(PurchasePlanAddActivity2.this, new UtilTools.DeleteConfirmInterface() {

							@Override
							public void deleteConfirmCallback() {
								handleMessage2Server();
								showProgressDialog();
								SubmitFlowDataToServer();

							}
							
						}, getResources().getString(
								R.string.need_upload_change), getResources().getString(R.string.remind));	
					}

				});
				if (submitButtonStyle() == REJECT_BUTTON_STYLE
						&& mCurrentStatus == MODIFY_STATUS) {
					approvalButton.setVisibility(View.VISIBLE);
					approvalButton.setOnClickListener(approvalListener);
				}
			} else {
				saveBottomButton.setText(R.string.approve);
				saveBottomButton.setOnClickListener(approvalListener);
			}
		} else {
			saveBottomButton.setOnClickListener(saveListener);
			submitButton.setVisibility(View.VISIBLE);
			submitButton.setOnClickListener(new OnClickListener() {

				@Override
				public void onClick(View v) {

					if (!checkAndSaveData()) {
						return;
					}
					handleMessage2Server();
					showProgressDialog();
					SubmitFlowDataToServer();
				}

			});
		}

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
			mTotalTextView = (TextView) itemView
					.findViewById(R.id.total_content);
			mMarkTextView = (TextView) itemView
					.findViewById(R.id.remark_content_edit);
		}
		if (mCurrentStatus == INFO_STATUS) {
			mMarkTextView.setFocusableInTouchMode(false);
			mMarkTextView.clearFocus();
		}
		return parentLayout;
	}

	private DataManagerInterface mFlowManager = new DataManagerInterface() {

		@Override
		public void getDataOnResult(ResultStatus status, List<?> list) {
			if (status.getCode() != AnalysisManager.SUCCESS_DB_QUERY) {
				Message msg = new Message();
				msg.obj = status.getMessage();
				mToastHandler.sendMessage(msg);
			}

			if (status.getCode() == AnalysisManager.SUCCESS_DB_QUERY) {

				if (list != null && list.size() != 0) {
					@SuppressWarnings("unchecked")
					List<Flow_setting> flowList = ((List<Flow_setting>) list);
					if (flowList.get(0).getFlow_type()
							.equals(GLOBAL.FLOW_TYPE[3][0])) {
						if (flowList.get(0).getStatus() == Integer
								.parseInt(GLOBAL.FLOW_STATUS[0][0])) {
							hasFlowApproval = true;

							mFlowSetting = flowList.get(0);
							if (mMsgJHData.getStatus() == Integer
									.parseInt(GLOBAL.FLOW_APPROVAL_STATUS[3][0])) {
								mCurrentStatus = MODIFY_STATUS;
							} else {
								mCurrentStatus = INFO_STATUS;
							}

							initWindow();
						} else if (flowList.get(0).getStatus() == Integer
								.parseInt(GLOBAL.FLOW_STATUS[1][0])) {
							hasFlowApproval = false;
							mFlowSetting = flowList.get(0);
							mCurrentStatus = INFO_STATUS;
							initWindow();
						}
					}

				}
			}
		}
	};

	private DataManagerInterface mCGJHManager = new DataManagerInterface() {

		@Override
		public void getDataOnResult(ResultStatus status, List<?> list) {

			if (status.getCode() == AnalysisManager.SUCCESS_DB_QUERY) {
				if (list != null && !list.isEmpty()) {
					mMsgJHData = (P_CGJH) list.get(0);

					mSelectProject = new Project();
					mSelectProject.setName(mMsgJHData.getProject_name());
					mSelectProject.setProject_id(mMsgJHData.getProject_id());

					RemoteFlowSettingService.getInstance().getFlowDetail(
							mFlowManager,
							UserCache.getCurrentUser().getTenant_id(),
							GLOBAL.FLOW_TYPE[3][0]);

				}
			}
		}

	};

	public static String PASS_MODIFY = "purchase_modify";
	public static String PASS_INFO = "purchase_info";
	public static String PASS_APPROVAL = "approval_status";
	public static String PASS_APPROVAL_SETTING = "approval_setting";
	private com.pm360.cepm360.entity.Message mMessage = null;
	private Flow_setting mFlowSetting = new Flow_setting();

	/**
	 * we should invoke msgHandlerProgress first and get hasFlowApproval flag
	 */
	private void msgHandlerProgress() {
		Intent intent = getIntent();

		String action = intent.getAction();
		if (action != null && action.equals(MSG_PURCHASE_PLAN_ACTION)) {
			mMessage = (com.pm360.cepm360.entity.Message) intent
					.getSerializableExtra("message");
			RemotePlanService.getInstance().getCGJHByMsgId(mCGJHManager,
					mMessage.getMessage_id());
			return;
		}
		hasFlowApproval = (Boolean) intent.getSerializableExtra(PASS_APPROVAL);
		mFlowSetting = (Flow_setting) intent
				.getSerializableExtra(PASS_APPROVAL_SETTING);
		if (intent.getSerializableExtra("purchase_modify") != null) {
			mCurrentStatus = MODIFY_STATUS;
			mMsgJHData = (P_CGJH) intent
					.getSerializableExtra("purchase_modify");
			mSelectProject = new Project();
			mSelectProject.setName(mMsgJHData.getProject_name());
			mSelectProject.setProject_id(mMsgJHData.getProject_id());
		} else if (intent.getSerializableExtra("purchase_info") != null) {
			mCurrentStatus = INFO_STATUS;
			mMsgJHData = (P_CGJH) intent.getSerializableExtra("purchase_info");
			mSelectProject = new Project();
			mSelectProject.setName(mMsgJHData.getProject_name());
			mSelectProject.setProject_id(mMsgJHData.getProject_id());
		} else {
			mMsgJHData = new P_CGJH();
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
			btn.setText(R.string.select_purchase_budget);
			btn.setOnClickListener(new OnClickListener() {

				@Override
				public void onClick(View v) {
					mPurchaseBudgetDialog.show();
				}
			});
			break;
		case 1:
			btn.setText(R.string.select_material);
			btn.setOnClickListener(new OnClickListener() {

				@Override
				public void onClick(View v) {
					Intent data;
					Bundle bundle;
					data = new Intent(PurchasePlanAddActivity2.this,
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
			});
			break;
		case 2:
			btn.setText(R.string.select_equipment);
			btn.setOnClickListener(new OnClickListener() {

				@Override
				public void onClick(View v) {
					Intent data;
					Bundle bundle;
					data = new Intent(PurchasePlanAddActivity2.this,
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
			});
			break;
		}

		return btn;
	}

	private void initViewTopButtons() {
		LinearLayout parentLine = (LinearLayout) mTopTitleView.getPopupView()
				.findViewById(R.id.parent_line);
		LinearLayout line = new LinearLayout(this);
		LinearLayout.LayoutParams layoutParams = new LinearLayout.LayoutParams(
				LayoutParams.MATCH_PARENT, LayoutParams.MATCH_PARENT);
		parentLine.addView(line, layoutParams);

		mPurchaseBudgetDialog = new PurchaseBudgetDialog(this,
				mBudgetDialogInterface);
		mPurchaseBudgetDialog.initBudgetDialog();

		LinearLayout.LayoutParams layoutParams1 = new LinearLayout.LayoutParams(
				getResources().getDimensionPixelSize(
						R.dimen.ticket_button_width), getResources()
						.getDimensionPixelSize(R.dimen.ticket_button_height));
		layoutParams1.setMargins(0, 10, 10, 10);
		for (int i = 0; i < 3; i++) {
			Button btn = initViewTopButton(i);
			line.addView(btn, layoutParams1);
		}
	}

	private BaseWindow mTopTitleView;
	private View mViewTop;
	private String[] mTopDialogNames;
	private EditText mProjectSelectEditText;
	private EditText mOwnerSelectEditText;

	@SuppressLint("UseSparseArrays")
	private void initViewTopWindow() {
		mTopTitleView = new BaseWindow(this);

		Map<Integer, Integer> buttons = new HashMap<Integer, Integer>();
		if (mCurrentStatus != INFO_STATUS) {
			buttons.put(0, BaseDialog.editTextReadOnlyLineStyle);
			buttons.put(2, BaseDialog.editTextClickLineStyle);
			buttons.put(3, BaseDialog.calendarLineStyle);
			buttons.put(4, BaseDialog.calendarLineStyle);
			if (mCurrentStatus == ADD_STATUS) {
				buttons.put(5, BaseDialog.editTextClickLineStyle);
			} else if (mCurrentStatus == MODIFY_STATUS) {
				buttons.put(5, BaseDialog.editTextReadOnlyLineStyle);
			}

			mTopTitleView.init(R.array.purchase_plan_add_title, buttons, null,
					false, 3);
			initViewTopButtons();

			mTopTitleView.getPopupView()
					.findViewById(mTopTitleView.baseEditTextId)
					.setBackground(null);
			mOwnerSelectEditText = (EditText) mTopTitleView.getPopupView()
					.findViewById(mTopTitleView.baseEditTextId + 2);
			mOwnerSelectEditText.setOnClickListener(new OnClickListener() {

				@Override
				public void onClick(View v) {
					Intent intent = new Intent();
					intent.setClass(v.getContext(), OwnerSelectActivity.class);
					intent.putExtra("title", getString(R.string.execute_person));
					startActivityForResult(intent, OWNER_SELECT_CODE);
				}
			});
			if (mCurrentStatus == ADD_STATUS) {
				mProjectSelectEditText = (EditText) mTopTitleView
						.getPopupView().findViewById(
								mTopTitleView.baseEditTextId + 5);
				mProjectSelectEditText
						.setOnClickListener(new OnClickListener() {

							@Override
							public void onClick(View v) {
								Intent intent = new Intent(
										PurchasePlanAddActivity2.this,
										ProjectSelectActivity.class);
								Bundle bundle = new Bundle();
								bundle.putInt("action", ProjectSelectActivity.ACTION_PICK);
								intent.putExtras(bundle);
								startActivityForResult(intent,
										PROJECT_SELECT_CODE);
							}
						});
			}

			mTopDialogNames = getResources().getStringArray(
					R.array.purchase_plan_add_title);
		} else {
			for (int i = 0; i < 6; i++) {
				buttons.put(i, BaseDialog.editTextReadOnlyLineStyle);
			}
			mTopTitleView.init(R.array.purchase_plan_add_title, buttons, null,
					false, 3);
			mTopTitleView.getPopupView()
					.findViewById(mTopTitleView.baseEditTextId)
					.setBackground(null);
		}

		if (mCurrentStatus != ADD_STATUS) {
			int widgetCount = 7;
			String[] editTexts = new String[widgetCount];
			editTexts[5] = mMsgJHData.getProject_name();
			editTexts[4] = DateUtils.dateToString(DateUtils.FORMAT_SHORT,
					mMsgJHData.getPlan_date());
			editTexts[3] = DateUtils.dateToString(DateUtils.FORMAT_SHORT,
					mMsgJHData.getReport_date());

            editTexts[2] = UserCache.getUserMaps()
                    .get(String.valueOf(mMsgJHData.getExecute_person()))
                    + "";
            editTexts[1] = mMsgJHData.getCgjh_name();
            editTexts[0] = mMsgJHData.getCgjh_number();

			mTopTitleView.SetDefaultValue(editTexts);
		} else {
			RemoteCommonService.getInstance().getCodeByDate(mCodeManager,
					"CGJH");
		}

		mViewTop = mTopTitleView.getPopupView();
	}

	private DataManagerInterface mCodeManager = new DataManagerInterface() {

		@Override
		public void getDataOnResult(ResultStatus status, List<?> list) {

			if (status.getCode() == AnalysisManager.SUCCESS_DB_QUERY) {
				String[] editTexts = new String[7];
				editTexts[0] = status.getMessage();
				mTopTitleView.SetDefaultValue(editTexts);
			}
		}

	};

	private View mViewPlanList;
	private View mHeaderView;
	private String[] mListHeadNames;
	private int[] mDisplayItemIds;
	private ListView planAddListView;
	private DataListAdapter<P_CGJHD> mListAdapter;

	@SuppressWarnings("unchecked")
	private void initPlanListView() {
		LayoutInflater inflater = LayoutInflater.from(this);
		mViewPlanList = inflater.inflate(R.layout.purchase_plan_add, null);
		/* 采购列表头布局 */
		mHeaderView = mViewPlanList.findViewById(R.id.listHeaderView);
		/* 获取列表使用的相关资源 */
		TypedArray typedArray = getResources().obtainTypedArray(
				R.array.purchase_plan_add_item_ids);
		mListHeadNames = getResources().getStringArray(
				R.array.purchase_plan_add_item_names);
		planAddListView = (ListView) mViewPlanList.findViewById(R.id.listView);

		if (mListHeadNames != null) {
			mDisplayItemIds = new int[typedArray.length()];
			for (int i = 0; i < mDisplayItemIds.length; i++) {
				mDisplayItemIds[i] = typedArray.getResourceId(i, 0);
				TextView tv = (TextView) mHeaderView
						.findViewById(mDisplayItemIds[i]);
				String text = "<b>" + mListHeadNames[i] + "</b>";
				tv.setText(Html.fromHtml(text));
				tv.setTextSize(TypedValue.COMPLEX_UNIT_PX, getResources()
						.getDimension(R.dimen.ticket_table_text_size));
				tv.setGravity(Gravity.CENTER);
				tv.setPadding(0, 0, 0, 0);
			}
		}
		typedArray.recycle();

		/* ListView适配器初始化 */
		mListAdapter = new DataListAdapter<P_CGJHD>(this, mListAdapterManager,R.array.purchase_plan_add_item_ids);
		planAddListView.setAdapter(mListAdapter);

	}

	private BaseDialog mListViewDialog;
	private String[] mListDialogNames;
	private EditText mSelectTaskEditText;
	private EditText mSelectProviderEditText;

	@SuppressLint("UseSparseArrays")
	private void initListViewDialog() {
		mListViewDialog = new BaseDialog(this, R.string.purchase_plan_modify);

		if (mCurrentStatus != INFO_STATUS) {
			Map<Integer, Integer> buttons = new HashMap<Integer, Integer>();
			buttons.put(0, BaseDialog.decimalEditTextLineStyle);
			buttons.put(1, BaseDialog.decimalEditTextLineStyle);
			buttons.put(2, BaseDialog.editTextClickLineStyle);
			buttons.put(3, BaseDialog.calendarLineStyle);
			buttons.put(4, BaseDialog.editTextClickLineStyle);
			mListViewDialog.init(R.array.purchase_plan_add_dialog, buttons,
					null);
			mSelectProviderEditText = (EditText) mListViewDialog.getPopupView()
					.findViewById(mListViewDialog.baseEditTextId + 2);
			mSelectProviderEditText.setOnClickListener(new OnClickListener() {

				@Override
				public void onClick(View v) {
					Intent data = new Intent(PurchasePlanAddActivity2.this,
							ListSelectActivity.class);
					Bundle bundle = new Bundle();
					bundle.putSerializable(ListSelectActivity.FRAGMENT_KEY,
							ContactCompanyFragment.class);
					data.putExtras(bundle);
					startActivityForResult(data, PROVIDER_SELECT_CODE);
				}
			});
			mSelectTaskEditText = (EditText) mListViewDialog.getPopupView()
					.findViewById(mListViewDialog.baseEditTextId + 4);
			mSelectTaskEditText.setOnClickListener(new OnClickListener() {

				@Override
				public void onClick(View v) {
					Intent intent = new Intent(PurchasePlanAddActivity2.this,
							ListSelectActivity.class);
					intent.putExtra(ListSelectActivity.PROJECT_KEY,
							mSelectProject);
					intent.putExtra(ListSelectActivity.FRAGMENT_KEY, PlanMakeFragment.class);
					startActivityForResult(intent, TASK_SELECT_CODE);
				}
			});

			mListDialogNames = getResources().getStringArray(
					R.array.purchase_plan_add_dialog);
			Button saveImageView = (Button) mListViewDialog.getPopupView()
					.findViewById(R.id.save_Button);
			saveImageView.setOnClickListener(new OnClickListener() {

				@Override
				public void onClick(View v) {
					Map<String, String> items = mListViewDialog.SaveData();
					int count = 0;
					double quantity = Double.parseDouble(items
							.get(mListDialogNames[count++]));
					double unitPrice = Double.parseDouble(items
							.get(mListDialogNames[count++]));
					String provider = items.get(mListDialogNames[count++]);
					Date intime = DateUtils.stringToDate(
							DateUtils.FORMAT_SHORT,
							items.get(mListDialogNames[count++]));
					double money = quantity * unitPrice;
					mCurrentItem.setQuantity(quantity);
					mCurrentItem.setUnit_price(unitPrice);
					mCurrentItem.setLwdw_name(provider);
					mCurrentItem.setMoney(money);
					mCurrentItem.setIndate(intime);
					if (mCurrentProvider != null) {
						mCurrentItem.setLwdw_id(mCurrentProvider.getLwdw_id());
					}

					if (mCurrentTask != null) {
						mCurrentItem.setTask_name(mCurrentTask.getName());
						mCurrentItem.setTask_id(mCurrentTask.getTask_id());
					}
					mCurrentTask = null;
					setTotalTextView();

					if (mCurrentItem.getIDU() == 0) {
						mCurrentItem.setIDU(GLOBAL.IDU_UPDATE);
					}

					mListAdapter.updateData(mLine, mCurrentItem);
					mListViewDialog.dismiss();
				}
			});
		}
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
		String[] editTexts = new String[5];
		if (mCurrentItem.getQuantity() == 0) {

		} else {
			editTexts[0] = Double.toString(mCurrentItem.getQuantity());
		}

		if (mCurrentItem.getUnit_price() == 0) {

		} else {
			editTexts[1] = Double.toString(mCurrentItem.getUnit_price());
		}
		editTexts[2] = mCurrentItem.getLwdw_name();
		editTexts[3] = DateUtils.dateToString(DateUtils.FORMAT_SHORT,
				mCurrentItem.getIndate());
		editTexts[4] = mCurrentItem.getTask_name();
		mListViewDialog.show(editTexts);
	}

	private List<P_CGJHD> mRemoveList = new ArrayList<P_CGJHD>();

	// 删除
	private void deleteTicket() {
		UtilTools.deleteConfirm(this, new UtilTools.DeleteConfirmInterface() {

			@Override
			public void deleteConfirmCallback() {
				if (mCurrentItem.getIDU() != GLOBAL.IDU_INSERT) {
					mCurrentItem.setIDU(GLOBAL.IDU_DELETE);
					mRemoveList.add(mCurrentItem);
				}
				mListAdapter.deleteData(mLine);
				
			}
			
		});
	}

	private boolean checkAndSaveData() {
		boolean ret = false;
		do {
			Map<String, String> items = mTopTitleView.SaveData();
			LogUtil.i("wzw items" + items);
			if (items.get(mTopDialogNames[1]).equals("")
					|| items.get(mTopDialogNames[2]).equals("")
					|| items.get(mTopDialogNames[3]).equals("")
					|| items.get(mTopDialogNames[0]).equals("")
					|| items.get(mTopDialogNames[4]).equals("")
					|| mSelectProject == null) {
				BaseToast
						.show(PurchasePlanAddActivity2.this, BaseToast.NULL_MSG);
				break;
			}

			if (mListAdapter.getDataShowList().size() == 0) {
				Toast.makeText(PurchasePlanAddActivity2.this,
						R.string.list_view_is_null, Toast.LENGTH_SHORT).show();
				break;
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
				break;
			}

			mMsgJHData.setCgjh_number(items.get(mTopDialogNames[0]));
			mMsgJHData.setCgjh_name(items.get(mTopDialogNames[1]));
            mMsgJHData.setExecute_person(UserCache
                    .getUserListsMap().get(items.get(mTopDialogNames[2])));
            mMsgJHData.setReport_date(DateUtils.stringToDate(
					DateUtils.FORMAT_SHORT, items.get(mTopDialogNames[3])));
			mMsgJHData.setPlan_date(DateUtils.stringToDate(
					DateUtils.FORMAT_SHORT, items.get(mTopDialogNames[4])));
			mMsgJHData.setProject_id(mSelectProject.getProject_id());
			mMsgJHData.setProject_name(mSelectProject.getName());
			mMsgJHData.setTenant_id(UserCache.getCurrentUser().getTenant_id());
			mMsgJHData.setMark(mMarkTextView.getText().toString());

			double money = 0;
			for (i = 0; i < mListAdapter.getDataShowList().size(); i++) {
				money += mListAdapter.getDataShowList().get(i).getMoney();
			}
			mMsgJHData.setTotal_money(money);
			ret = true;
		} while (false);
		return ret;
	}

	private void PassCGJHDataToServer() {
		// set list item data
		List<P_CGJHD> listJH = new ArrayList<P_CGJHD>();
		listJH.addAll(mListAdapter.getDataShowList());
		listJH.addAll(mRemoveList);

		if (mCurrentStatus == ADD_STATUS) {
			mMsgJHData
					.setPlan_person(UserCache.getCurrentUser().getUser_id());
			RemotePlanService.getInstance().addCGJH(mDataManager, mMsgJHData,
					listJH);
		} else if (mCurrentStatus == MODIFY_STATUS) {
			RemotePlanService.getInstance().updateCGJH(mDataManager,
					mMsgJHData, listJH);
		}
	}
	
	private void SubmitFlowDataToServer() {
		List<P_CGJHD> listJH = new ArrayList<P_CGJHD>();
		listJH.addAll(mListAdapter.getDataShowList());
		listJH.addAll(mRemoveList);

		if (hasFlowApproval) {
			mMsgJHData.setStatus(Integer.parseInt(GLOBAL.FLOW_APPROVAL_STATUS[1][0]));
			Flow_approval flowApproval = new Flow_approval();
			flowApproval.setType_id(mMsgJHData.getCgjh_id());
			flowApproval.setFlow_type(GLOBAL.FLOW_TYPE[3][0]);
			flowApproval.setSubmiter(UserCache.getCurrentUser().getUser_id());
			flowApproval.setTenant_id(UserCache.getCurrentUser().getTenant_id());
			flowApproval.setStatus(Integer
					.parseInt(GLOBAL.FLOW_APPROVAL_STATUS[1][0]));
			flowApproval.setCurrent_level(mFlowSetting.getLevel1());
			LogUtil.i("wzw userList" + mFlowSetting);
			if (mFlowSetting.getLevel2() != 0) {
				flowApproval.setNext_level(mFlowSetting.getLevel2());
			} else {
				flowApproval
						.setNext_level(UserCache.getCurrentUser().getUser_id());
			}
			if (mCurrentStatus == ADD_STATUS) {
				mMsgJHData.setPlan_person(UserCache.getCurrentUser().getUser_id());
				RemotePlanService.getInstance().submitWithApprovalForAdd(mSubmitManager, mMsgJHData, listJH, flowApproval);
			} else if (mCurrentStatus == MODIFY_STATUS) {
				RemotePlanService.getInstance().submitWithApprovalForUpdate(mSubmitManager, mMsgJHData, listJH, flowApproval);
			}
		} else {
			mMsgJHData.setStatus(Integer.parseInt(GLOBAL.FLOW_APPROVAL_STATUS[2][0]));
			if (mCurrentStatus == ADD_STATUS) {
				mMsgJHData.setPlan_person(UserCache.getCurrentUser().getUser_id());
				RemotePlanService.getInstance().submitNoApprovalForAdd(mSubmitManager, mMsgJHData, listJH);
			} else if (mCurrentStatus == MODIFY_STATUS) {
				RemotePlanService.getInstance().submitNoApprovalForUpdate(mSubmitManager, mMsgJHData, listJH);
			}
		}

	}

	private PurchaseBudgetDialog mPurchaseBudgetDialog;

	/**
	 * if the jh status is 1, or it is 4 and the plan person login, we need add
	 * two buttons, submit and save else, we just add one button approval
	 * 
	 * @return
	 */
	private final int SUBMIT_BUTTON_STYLE = 1;
	private final int APPROVAL_BUTTON_STYLE = 2;
	private final int REJECT_BUTTON_STYLE = 3;

	private int submitButtonStyle() {
		if (mMsgJHData.getStatus() == Integer
				.parseInt(GLOBAL.FLOW_APPROVAL_STATUS[0][0])) {
			return SUBMIT_BUTTON_STYLE;
        } else if ((mMsgJHData.getStatus() == Integer
                .parseInt(GLOBAL.FLOW_APPROVAL_STATUS[3][0]) && UserCache
                .getCurrentUserId() == mMsgJHData
                .getPlan_person())) {
            return REJECT_BUTTON_STYLE;
        } else {
            return APPROVAL_BUTTON_STYLE;
        }
	}

	// save button can't be live alone, otherwise it is approval button
	private boolean isSaveButtonStyle() {
		if (mMessage != null && mMessage.getIs_process() == 1) {
			return false;
		}
		if (submitButtonStyle() == SUBMIT_BUTTON_STYLE
				|| (submitButtonStyle() == REJECT_BUTTON_STYLE && mCurrentStatus == MODIFY_STATUS)) {
			return true;
		} else {
			// actually it is approval button
			return false;
		}
	}

	private BudgetDialogInterface mBudgetDialogInterface = new BudgetDialogInterface() {

		@Override
		public void setData(List<P_CGJHD> cgjhdList) {
			mListAdapter.setShowDataList(cgjhdList);
			setTotalTextView();
		}

	};

	private void loadData() {
		RemotePlanService.getInstance().getCGJHD(mDataManager,
				mMsgJHData.getCgjh_id());
	}

	@SuppressLint("HandlerLeak")
	private Handler mToastHandler = new Handler() {
		public void handleMessage(Message msg) {
			Toast.makeText(PurchasePlanAddActivity2.this,
					(CharSequence) msg.obj, Toast.LENGTH_SHORT).show();
		}
	};

	private DataManagerInterface mSubmitManager = new DataManagerInterface() {

		@Override
		public void getDataOnResult(ResultStatus status, List<?> list) {
			dismissProgressDialog();
			if (status.getCode() != AnalysisManager.SUCCESS_DB_QUERY) {
				Message msg = new Message();
				msg.obj = status.getMessage();
				mToastHandler.sendMessage(msg);
			}
			LogUtil.i("wzw status" + status.getCode());
			if (status.getCode() == AnalysisManager.SUCCESS_DB_ADD
					|| status.getCode() == AnalysisManager.SUCCESS_DB_UPDATE) {
				Intent intent = new Intent();
				Bundle bundle = new Bundle();
				bundle.putSerializable(PurchaseDataCache.RESULT_KEY_CODE,
						PurchaseDataCache.RESULT_APPROVAL_CODE);
				intent.putExtras(bundle);
				setResult(Activity.RESULT_OK, intent);
				finish();
			}
		}
		
	};

	private boolean requestServerDataFlag = false;
	private DataManagerInterface mDataManager = new DataManagerInterface() {

		@SuppressWarnings("unchecked")
		@Override
		public void getDataOnResult(ResultStatus status, List<?> list) {
			if (status.getCode() != AnalysisManager.SUCCESS_DB_QUERY) {
				if (status.getCode() == AnalysisManager.SUCCESS_DB_ADD) {
					Message msg = new Message();
					msg.obj = getString(R.string.add_purchase_plan_succ);
					mToastHandler.sendMessage(msg);
				} else {
					if (retStatus != FLOW_APPROVAL_SELECT_CODE) {
						Message msg = new Message();
						msg.obj = status.getMessage();
						mToastHandler.sendMessage(msg);
					}
				}
			}
			LogUtil.i("wzw status" + status + " list:" + list);
			Intent intent;
			Bundle bundle;
			switch (status.getCode()) {
			case AnalysisManager.SUCCESS_DB_QUERY:
				if (list != null && list.size() != 0) {
					mListAdapter.setShowDataList((List<P_CGJHD>) list);
					setBottomData();
				}
				break;

			case AnalysisManager.SUCCESS_DB_DEL:
				break;

			case AnalysisManager.SUCCESS_DB_UPDATE:

				intent = new Intent();
				bundle = new Bundle();
				bundle.putSerializable(PurchaseDataCache.RESULT_KEY_CODE,
						PurchaseDataCache.RESULT_UPDATE_CODE);
				intent.putExtras(bundle);
				setResult(Activity.RESULT_OK, intent);
				finish();
				break;
			case AnalysisManager.SUCCESS_DB_ADD:

				intent = new Intent();
				bundle = new Bundle();
				bundle.putSerializable(PurchaseDataCache.RESULT_KEY_CODE,
						PurchaseDataCache.RESULT_ADD_CODE);
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

	private P_CGJHD mCurrentItem;
	private OptionsMenuView mOptionsMenuView;
	private int mLine;
	@SuppressWarnings("rawtypes")
	DataListAdapter.ListAdapterInterface mListAdapterManager = new DataListAdapter.ListAdapterInterface() {

		@Override
		public int getLayoutId() {
			return R.layout.purchase_plan_add_list_item;
		}

		@Override
		public View getHeaderView() {
			return mHeaderView;
		}

		@Override
		public void regesterListeners(ViewHolder holder, final int position) {
			for (int i = 0; i < holder.tvs.length; i++) {
				// holder.tvs[i].setClickable(false);

				if ((mCurrentStatus != INFO_STATUS)
						&& (i == 5 || i == 6 || i == 8 || i == 9 || i == 10)) {
					holder.tvs[i]
							.setOnClickListener(new View.OnClickListener() {
								@Override
								public void onClick(View view) {
									mCurrentItem = mListAdapter
											.getItem(position);
									mLine = position;
									modifyTicket();
								}
							});
				} else {
					holder.tvs[i]
							.setOnClickListener(new View.OnClickListener() {
								@Override
								public void onClick(View view) {
									long minus_time = System
											.currentTimeMillis() - mDismissTime;

									if (minus_time < 300)
										return;
									if (mCurrentStatus != INFO_STATUS) {
										mOptionsMenuView.showAsDropDown(
												view,
												0,
												-view.getMeasuredHeight()
														- getResources()
																.getDimensionPixelSize(
																		R.dimen.popup_window_height));
									}

									mListAdapter.setSelected(position, true);
									mCurrentItem = mListAdapter
											.getItem(position);

									mLine = position;
								}
							});
				}

			}
		}

		@Override
		public void initListViewItem(View convertView, ViewHolder holder,
				DataListAdapter adapter, int position) {
			Map<String, String> listViewItem = beanToMap(adapter
					.getItem(position));
			for (int i = 0; i < mListHeadNames.length; i++) {
				if (mCurrentStatus != INFO_STATUS) {
					if (i == 5 || i == 8 || i == 9 || i == 10 || i == 6) {
						Drawable drawable = getResources().getDrawable(
								R.drawable.icon_modify);
						// 这一步必须要做,否则不会显示.
						drawable.setBounds(0, 0, 25, 25);
						holder.tvs[i].setCompoundDrawables(null, null,
								drawable, null);
						holder.tvs[i].setTextColor(Color.RED);
					}
					if (i == 6) {
						holder.tvs[i].setPadding(0, 0, 0, 0);
					}
				}
				holder.tvs[i].setTextSize(
						TypedValue.COMPLEX_UNIT_PX,
						getResources().getDimension(
								R.dimen.ticket_table_text_size));
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
			return ((P_CGJHD) t1).getCgjhd_id() == ((P_CGJHD) t2).getCgjhd_id();
		}

	};

	private Map<String, String> beanToMap(Object bean) {
		Map<String, String> mapItem = new HashMap<String, String>();
		if (bean instanceof P_CGJHD) {
			P_CGJHD cgjhd = (P_CGJHD) bean;
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
					Double.toString(cgjhd.getQuantity()));
			mapItem.put(mListHeadNames[count++],
					UtilTools.formatMoney("¥", cgjhd.getUnit_price(), 2));
			mapItem.put(mListHeadNames[count++],
					UtilTools.formatMoney("¥", cgjhd.getMoney(), 2));
			mapItem.put(mListHeadNames[count++], cgjhd.getLwdw_name());
			mapItem.put(
					mListHeadNames[count++],
					DateUtils.dateToString(DateUtils.FORMAT_SHORT,
							cgjhd.getIndate()));
			mapItem.put(mListHeadNames[count++], cgjhd.getTask_name());
		}
		return mapItem;
	}

	private void setTotalTextView() {
		Double allMoney = 0.0;
		for (int i = 0; i < mListAdapter.getDataShowList().size(); i++) {
			allMoney += mListAdapter.getDataShowList().get(i).getMoney();
		}
		mTotalTextView.setText(UtilTools.formatMoney("¥", allMoney, 2));
	}

	private void setBottomData() {
		mTotalTextView.setText(UtilTools.formatMoney("¥",
				mMsgJHData.getTotal_money(), 2));
		mMarkTextView.setText(mMsgJHData.getMark());
	}

	private void handleMessage2Server() {
		if (mMessage != null) {

		}
	}
	
	@SuppressWarnings("unused")
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
		cg.setCgjh_id(mMsgJHData.getCgjh_id());
		cg.setCgjh_name(mMsgJHData.getCgjh_name());
		cg.setExecute_person(mMsgJHData.getExecute_person());
		cg.setProject_id(mMsgJHData.getProject_id());
		cg.setCg_money(mMsgJHData.getTotal_money());
		cg.setCg_number(codeNumber);
		cg.setTenant_id(mMsgJHData.getTenant_id());
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
			cgd.setProject_id(mMsgJHData.getProject_id());
			cgd.setTenant_id(mMsgJHData.getTenant_id());
			//cgd.set
			cgd.setLwdw_name(listJHD.get(i).getLwdw_name());
			cgd.setUnit_price(listJHD.get(i).getUnit_price());
			cgd.setMoney(listJHD.get(i).getMoney());
			cgdList.add(cgd);
		}
		RemotePlanService.getInstance().passApproval(mFlowApprovalDataInterface, cg, cgdList, mCurrentApproval, mNextApproval);
	
	}
	
	private Flow_approval mCurrentApproval;
	private Flow_approval mNextApproval;
	private DataManagerInterface mFlowApprovalDataInterface;
	FlowApprovalDialog.FlowApprovalManager mFlowApprovalManager;
//	FlowApprovalDialog.FlowApprovalManager mFlowApprovalManager = new FlowApprovalDialog.FlowApprovalManager() {
//
//		@Override
//		public void passFlowData2Server(
//				DataManagerInterface flowApprovalDataInterface,
//				Flow_approval currentApproval, Flow_approval nextApproval) {
//			mCurrentApproval = currentApproval;
//			mNextApproval = nextApproval;
//			mFlowApprovalDataInterface = flowApprovalDataInterface;
//			PassFlowDataToServer1();
//		}
//
//		@Override
//		public void rebutFlowData2Server(
//				DataManagerInterface flowApprovalManagerInterface,
//				Flow_approval currentApproval, Flow_approval nextApproval) {
//			P_CG cg = new P_CG();
//			cg.setCgjh_id(mMsgJHData.getCgjh_id());
//			RemotePlanService.getInstance().rebutApproval(flowApprovalManagerInterface, cg, currentApproval, nextApproval);
//		}
//
//		
//	};

	private Project mSelectProject;
	private Task mCurrentTask;
	private P_LWDW mCurrentProvider;
	// the same as GLOBAL WZ_TYPE
	private final int MATERIAL_TYPE = 1;
	private final int EQUIPMENT_TYPE = 2;
	private int retStatus = 0;

	@SuppressWarnings("unchecked")
	public void onActivityResult(int requestCode, int resultCode, Intent data) {
		LogUtil.d("wzw requestCode" + requestCode + "select:" + data);
		if (resultCode == 0 || data == null)
			return;

		if (requestCode == PROJECT_SELECT_CODE) {
			mSelectProject = (Project) data.getSerializableExtra("project");
			mProjectSelectEditText.setText(mSelectProject.getName());
		} else if (requestCode == TASK_SELECT_CODE) {
			mCurrentTask = (Task) data
					.getSerializableExtra(ListSelectActivity.RESULT_KEY);
			if (mCurrentTask != null) {
				mCurrentItem.setTask_id(mCurrentTask.getTask_id());
				mSelectTaskEditText.setText(mCurrentTask.getName());
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
				mOwnerSelectEditText.setText(user.getName());
			}
		} else if (requestCode == PROVIDER_SELECT_CODE) {
			mCurrentProvider = (P_LWDW) data
					.getSerializableExtra(ListSelectActivity.RESULT_KEY);
			mSelectProviderEditText.setText(mCurrentProvider.getName());
		} else if (requestCode == FlowApprovalDialog.REQUEST_COUNTER) {
			if (mFlowApprovalDialog != null) {
				mFlowApprovalDialog.handleUserSelectResult(requestCode, resultCode, data);
			}
		}

		super.onActivityResult(requestCode, resultCode, data);
	}

	@Override
	public boolean onKeyDown(int keyCode, KeyEvent event) {
		return super.onKeyDown(keyCode, event);
	}
}
