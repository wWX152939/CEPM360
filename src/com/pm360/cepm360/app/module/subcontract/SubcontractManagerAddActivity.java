package com.pm360.cepm360.app.module.subcontract;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;
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
import android.view.View;
import android.view.View.OnClickListener;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.pm360.cepm360.R;
import com.pm360.cepm360.app.CepmApplication;
import com.pm360.cepm360.app.cache.ProjectCache;
import com.pm360.cepm360.app.cache.UserCache;
import com.pm360.cepm360.app.common.activity.ActionBarFragmentActivity;
import com.pm360.cepm360.app.common.activity.DataManagerInterface;
import com.pm360.cepm360.app.common.activity.ListSelectActivity;
import com.pm360.cepm360.app.common.adpater.DataListAdapter;
import com.pm360.cepm360.app.common.adpater.DataListAdapter.ViewHolder;
import com.pm360.cepm360.app.common.view.OptionsMenuView;
import com.pm360.cepm360.app.common.view.parent.BaseDialog;
import com.pm360.cepm360.app.common.view.parent.DoubleDatePickerDialog;
import com.pm360.cepm360.app.common.view.parent.FlowApprovalDialog;
import com.pm360.cepm360.app.module.resource.ContactCompanyFragment;
import com.pm360.cepm360.app.module.resource.LabourOutSourcingFragment;
import com.pm360.cepm360.app.module.schedule.PlanMakeSelectActivity;
import com.pm360.cepm360.app.utils.UtilTools;
import com.pm360.cepm360.common.GLOBAL;
import com.pm360.cepm360.common.ResultStatus;
import com.pm360.cepm360.common.json.AnalysisManager;
import com.pm360.cepm360.common.util.DateUtils;
import com.pm360.cepm360.common.util.LogUtil;
import com.pm360.cepm360.entity.Contract;
import com.pm360.cepm360.entity.Flow_approval;
import com.pm360.cepm360.entity.Flow_setting;
import com.pm360.cepm360.entity.P_LWDW;
import com.pm360.cepm360.entity.P_WBRG;
import com.pm360.cepm360.entity.P_WBRGD;
import com.pm360.cepm360.entity.P_WBRGNR;
import com.pm360.cepm360.entity.Project;
import com.pm360.cepm360.entity.Task;
import com.pm360.cepm360.services.common.RemoteCommonService;
import com.pm360.cepm360.services.subcontract.RemoteSubCcontentService;
import com.pm360.cepm360.services.system.RemoteFlowSettingService;

import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class SubcontractManagerAddActivity extends ActionBarFragmentActivity
		implements DataListAdapter.ListAdapterInterface<P_WBRGD> {
	// 列表头显示名称
	private View mListHeaderView;
	private View mFootingRemark;
	private TextView mFootingTextView;
	private String[] mListHeaderNames;
	private int[] mDisplayItemIds;
	private P_WBRGD mCurrentItem;
	private EditText exe_contractET;
	private Button btn_add;
	private Button btn_save;
	private EditText mRemarkContent;
	private OptionsMenuView mOptionsMenuView;
	private TextView mArtificialNumberTextView;
	private TextView mContractTextView;
	private TextView totalTextView;
	private ListView mWBRGDListView;
	private List<P_WBRGD> mShowWBRGDDataList = new ArrayList<P_WBRGD>();
	private List<P_WBRGD> mUpdatingDataList = new ArrayList<P_WBRGD>();
	private int mOperation;
	private P_WBRG mCurrentWBRGBean;
	private static final int OPERATION_DETAIL = 10;
	private static final int OPERATION_ADD = 11;
	private static final int OPERATION_MODIFY = 12;
	private static final int DATA_CHANGED = 100;
	private DataListAdapter<P_WBRGD> mListAdapter;
	private Project mProject;
	@SuppressWarnings("unused")
	private CepmApplication mApp;

	public static String PASS_APPROVAL = "approval_status";
	public static String PASS_APPROVAL_SETTING = "approval_setting";
	private com.pm360.cepm360.entity.Message mMessage = null;
	private Flow_setting mFlowSetting = new Flow_setting();
	private boolean hasFlowApproval;
	
    private BaseDialog mListViewDialog;
    private String[] mListDialogNames;
    private EditText mSelectLWDWEditText;
    private EditText mSelectTaskEditText;
    private Flow_approval mCurrentApproval;
    private Flow_approval mNextApproval;
    private DataManagerInterface mFlowApprovalDataInterface;
    
    private P_LWDW lwdw = null;
    private Task task = null;
    
    private static final int SELECT_LWDW_REQUEST = 111;
    private static final int ADD_WBRGD_REQUEST = 222;
    private static final int SELECT_SUBCONTRACT = 333;
    private static final int TASK_SELECT_REQUEST = 444;
    
    private final int SUBMIT_BUTTON_STYLE = 1;
    private final int APPROVAL_BUTTON_STYLE = 2;
    private final int REJECT_BUTTON_STYLE = 3;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		mApp = (CepmApplication) getApplication();
		enableMenuView(false);

		/* 全屏显示 */
		getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,
				WindowManager.LayoutParams.FLAG_FULLSCREEN);
		msgHandlerProgress();
		/* 载入布局文件 */
		setContentView(R.layout.subcontract_manager_add);

		if (mMessage != null) {
			return;
		}

		initWindow();
	}

	private void initWindow() {
		mProject = ProjectCache.getCurrentProject();
		if (mProject != null) {
			setActionBarTitle(mProject.getName());
		}
		mFootingRemark = findViewById(R.id.subcontract_manager_add_bottom);
		mFootingTextView = (TextView) mFootingRemark
				.findViewById(R.id.total_content);
		mArtificialNumberTextView = (TextView) this
				.findViewById(R.id.artificial_number_edit);
		mArtificialNumberTextView.setBackground(getResources().getDrawable(
				R.drawable.bg_edittext));
		mContractTextView = (TextView) this
				.findViewById(R.id.executive_contract);
		totalTextView = (TextView) this.findViewById(R.id.total_content);
		mRemarkContent = (EditText) findViewById(R.id.remark_content_edit);
		mWBRGDListView = (ListView) this.findViewById(R.id.listView);
		mListHeaderView = this.findViewById(R.id.listHeaderView);
		mListHeaderNames = getApplicationContext().getResources()
				.getStringArray(R.array.subcontract_header_title_names);

		TypedArray titleType = getApplicationContext().getResources()
				.obtainTypedArray(R.array.subcontract_header_title_ids);
		if (mListHeaderNames != null) {
			int itemLength = mListHeaderNames.length;
			mDisplayItemIds = new int[itemLength];
			for (int i = 0; i < itemLength; i++) {
				mDisplayItemIds[i] = titleType.getResourceId(i, 0);
				String text = "<b>" + mListHeaderNames[i] + "</b>";
				TextView tv = (TextView) findViewById(mDisplayItemIds[i]);
				tv.setText(Html.fromHtml(text));
				tv.setTextSize(TypedValue.COMPLEX_UNIT_PX, getResources()
						.getDimension(R.dimen.table_title_textsize));
			}
		}
		titleType.recycle();

		mListAdapter = new DataListAdapter<P_WBRGD>(this, this,
				mShowWBRGDDataList);
		if (mOperation == OPERATION_DETAIL || mOperation == OPERATION_MODIFY) { // 查看详细&修改
			mArtificialNumberTextView
					.setText(mCurrentWBRGBean.getWbrg_number());
			totalTextView.setText(UtilTools.formatMoney("¥",
					mCurrentWBRGBean.getTotal_money(), 2));
			mContractTextView.setText(mCurrentWBRGBean.getWbht_name());
			mRemarkContent.setText(mCurrentWBRGBean.getMark());
			loaderWBRGDData(mCurrentWBRGBean);
			mProject = new Project();
			// mProject.setName(mCurrentWBRGBean.getProject_name());
			mProject.setProject_id(mCurrentWBRGBean.getProject_id());
		} else {// 添加
			mShowWBRGDDataList.clear();
			mUpdatingDataList.clear();
			RemoteCommonService.getInstance().getCodeByDate(WBRGCodeManager,
					"WBRG");
		}

		setupButtons();
		initListViewDialog();
		if (mOperation != OPERATION_DETAIL) {
			initOptionsMenuView();
		} else {
			mContractTextView.setBackground(getResources().getDrawable(
					R.drawable.bg_edittext));
			mContractTextView.setGravity(Gravity.CENTER_VERTICAL);
			mContractTextView.setTextSize(TypedValue.COMPLEX_UNIT_PX, getResources().getDimension(R.dimen.table_title_textsize));
			int pxw = UtilTools.dp2pxW(getBaseContext(), 8);
			int pxh = UtilTools.dp2pxH(getBaseContext(), 8);
			mContractTextView.setPadding(pxw, pxh, pxw, pxh);
		}

		mWBRGDListView.setAdapter(mListAdapter);

	}

	private void msgHandlerProgress() {
		Intent intent = getIntent();
		String action = intent.getAction();
		if (action != null && action.equals(GLOBAL.MSG_SUBCONTRACT_MANAGEMENT_ACTION)) {
			mMessage = (com.pm360.cepm360.entity.Message) intent
					.getSerializableExtra("message");

			RemoteSubCcontentService.getInstance().getWBRGByMsgId(mWBRGManager,
					mMessage.getMessage_id());

            RemoteFlowSettingService.getInstance().getFlowDetail(mFlowManager,
                    UserCache.getCurrentUser().getTenant_id(),
                    GLOBAL.FLOW_TYPE[4][0]);

		} else {
			mOperation = (Integer) intent.getSerializableExtra("operation");
			mCurrentWBRGBean = (P_WBRG) intent.getSerializableExtra("data");
			hasFlowApproval = (Boolean) intent
					.getSerializableExtra(PASS_APPROVAL);
			mFlowSetting = (Flow_setting) intent
					.getSerializableExtra(PASS_APPROVAL_SETTING);
		}
	}

	private void initOptionsMenuView() {
		String[] subMenuNames = getResources().getStringArray(
				R.array.subcontract_wbrgd_options_menu);
		mOptionsMenuView = new OptionsMenuView(this, subMenuNames);
		mOptionsMenuView
				.setSubMenuListener(new OptionsMenuView.SubMenuListener() {
					@Override
					public void onSubMenuClick(View view) {
						switch ((Integer) view.getTag()) {
						case 0: // 修改
							modifyWBRGD();
							break;
						case 1: // 删除
							commonConfirmDeleteWBRGD();
							break;
						}
						mOptionsMenuView.dismiss();
					}
				});
	}

	@SuppressLint("HandlerLeak") 
	public Handler mAddDataHandle = new Handler() {
		public void handleMessage(Message msg) {
			String code = msg.getData().getString("key");
			mArtificialNumberTextView.setText(code);
			if (mOperation == OPERATION_ADD) {
				mCurrentWBRGBean
						.setTenant_id(UserCache.getCurrentUser().getTenant_id());
				mCurrentWBRGBean
						.setProject_id(ProjectCache.getCurrentProject().getProject_id());
				mCurrentWBRGBean.setWbrg_number(code);
			}
		}
	};

	private DataManagerInterface WBRGCodeManager = new DataManagerInterface() {
		public void getDataOnResult(ResultStatus status, List<?> list) {
			if (status.getCode() == AnalysisManager.SUCCESS_DB_QUERY) {
				Message msg = new Message();
				Bundle bundle = new Bundle();
				bundle.putString("key", status.getMessage());
				msg.setData(bundle);
				mAddDataHandle.sendMessage(msg);
			}
		}

	};

	private void loaderWBRGDData(P_WBRG wbrg) {
		RemoteSubCcontentService.getInstance().getWBRGD(mManager,
				wbrg.getWbrg_id());
	}

	private DataManagerInterface mFlowManager = new DataManagerInterface() {

		@Override
		public void getDataOnResult(ResultStatus status, List<?> list) {

			if (status.getCode() == AnalysisManager.SUCCESS_DB_QUERY) {

				if (list != null && list.size() != 0) {
					@SuppressWarnings("unchecked")
					List<Flow_setting> flowList = ((List<Flow_setting>) list);
					if (flowList.get(0).getFlow_type()
							.equals(GLOBAL.FLOW_TYPE[4][0])) {
						if (flowList.get(0).getStatus() == Integer
								.parseInt(GLOBAL.FLOW_STATUS[0][0])) {
							hasFlowApproval = true;

							mFlowSetting = flowList.get(0);
                            if (mCurrentWBRGBean.getStatus() == Integer
                                    .parseInt(GLOBAL.FLOW_APPROVAL_STATUS[3][0])
                                    && (UserCache
                                            .getCurrentUser().getUser_id() == mCurrentWBRGBean
                                            .getCreater())) {
                                mOperation = OPERATION_MODIFY;
							} else {
								mOperation = OPERATION_DETAIL;
							}
							initWindow();
						} else if (flowList.get(0).getStatus() == Integer
								.parseInt(GLOBAL.FLOW_STATUS[1][0])) {
							hasFlowApproval = false;
							mFlowSetting = flowList.get(0);
							mOperation = OPERATION_DETAIL;
							initWindow();
						}
					}

				}
			}
		}
	};
	
	private DataManagerInterface mManager = new DataManagerInterface() {

		public void getDataOnResult(ResultStatus status, List<?> list) {
			switch (status.getCode()) {
			case AnalysisManager.SUCCESS_DB_QUERY:
				if (list != null && list.size() > 0) {
					mShowWBRGDDataList.clear();
					mUpdatingDataList.clear();
					for (Object object : list) {
						if (object instanceof P_WBRGD) {
							mShowWBRGDDataList.add((P_WBRGD) object);
							mUpdatingDataList.add((P_WBRGD) object);
						}
					}
					mHandler.sendEmptyMessage(DATA_CHANGED);
				}
			default:
				break;
			}
		}
	};

	private void AddWBRGD() {
		Intent intent = new Intent(this, ListSelectActivity.class);
		Bundle bundle = new Bundle();
		bundle.putSerializable(ListSelectActivity.FRAGMENT_KEY,
				LabourOutSourcingFragment.class);
		bundle.putBoolean(ListSelectActivity.SELECT_MODE_KEY,
				ListSelectActivity.MULTI_SELECT);
		ArrayList<P_WBRGNR> list = new ArrayList<P_WBRGNR>();
		for (P_WBRGD rgd : mShowWBRGDDataList) {
			P_WBRGNR nr = new P_WBRGNR();
			nr.setWbrgnr_id(rgd.getWbrgnr_id());
			list.add(nr);
		}
		bundle.putSerializable(ListSelectActivity.FILTER_DATA_KEY, list);
		intent.putExtras(bundle);
		startActivityForResult(intent, ADD_WBRGD_REQUEST);
	}
	
	private void SelectLWDW() {
		Intent intent = new Intent(this, ListSelectActivity.class);
		Bundle bundle = new Bundle();
		bundle.putSerializable(ListSelectActivity.FRAGMENT_KEY,
				ContactCompanyFragment.class);
		bundle.putBoolean(ListSelectActivity.SELECT_MODE_KEY,
				ListSelectActivity.SINGLE_SELECT);
		intent.putExtras(bundle);
		startActivityForResult(intent, SELECT_LWDW_REQUEST);
	}

	private void SelectTask() {
		Intent intent = new Intent(this, PlanMakeSelectActivity.class);
//		intent.putExtra(PlanMakeSelectActivity.PROJECT_CODE,
//		        ProjectCache.getCurrentProject());
		startActivityForResult(intent, TASK_SELECT_REQUEST);
	}

	private void modifyWBRGD() {
		lwdw = null;
		task = null;
		String[] editTexts = new String[6];
		if (mCurrentItem.getQuantity() != 0) {
			editTexts[0] = Double.toString(mCurrentItem.getQuantity());
		}
		if (mCurrentItem.getUnit_price() != 0) {
			editTexts[1] = Double.toString(mCurrentItem.getUnit_price());
		}
		editTexts[2] = mCurrentItem.getDw_name();
		editTexts[3] = mCurrentItem.getTask_name();
		editTexts[4] = DateUtils.dateToString(DateUtils.FORMAT_SHORT,
				mCurrentItem.getIndate());
		editTexts[5] = DateUtils.dateToString(DateUtils.FORMAT_SHORT,
				mCurrentItem.getOutdate());
		mListViewDialog.show(editTexts);
	}

	private void commonConfirmSubmit() {
		// 创建确认删除对话框，并显示
		new AlertDialog.Builder(this)
				// 设置对话框主体内容
				.setMessage(getString(R.string.confirm_submit))
				// 设置对话框标题
				.setTitle(getResources().getString(R.string.remind))
				// 为对话框按钮注册监听
				.setPositiveButton(getResources().getString(R.string.confirm),
						new DialogInterface.OnClickListener() {
							@Override
							public void onClick(DialogInterface dialog,
									int which) {
								// 首先隐藏对话框
								dialog.dismiss();
								SubmitFlowDataToServer();
							}
						})
				.setNegativeButton(getResources().getString(R.string.cancel),
						new DialogInterface.OnClickListener() {
							@Override
							public void onClick(DialogInterface dialog,
									int which) {
								dialog.dismiss();
							}
						}).show();
	}
	
	private void commonConfirmDeleteWBRGD() {
		// 创建确认删除对话框，并显示
		new AlertDialog.Builder(this)
				// 设置对话框主体内容
				.setMessage(
						getResources().getString(
								R.string.confirm_delete))
				// 设置对话框标题
				.setTitle(getResources().getString(R.string.remind))
				// 为对话框按钮注册监听
				.setPositiveButton(getResources().getString(R.string.confirm),
						new DialogInterface.OnClickListener() {
							@Override
							public void onClick(DialogInterface dialog,
									int which) {
								// 首先隐藏对话框
								dialog.dismiss();
								deleteWBRGD();
							}
						})
				.setNegativeButton(getResources().getString(R.string.cancel),
						new DialogInterface.OnClickListener() {
							@Override
							public void onClick(DialogInterface dialog,
									int which) {
								dialog.dismiss();
							}
						}).show();
	}

	// 删除
	private void deleteWBRGD() {
		mCurrentItem.setIDU(GLOBAL.IDU_DELETE);
		if (mCurrentItem.getWbrgd_id() == 0)
			mUpdatingDataList.remove(mCurrentItem);
		mShowWBRGDDataList.remove(mCurrentItem);
		mListAdapter.notifyDataSetChanged();
		double total_money = 0.0;
		for (P_WBRGD wbrgd : mShowWBRGDDataList) {
			total_money += wbrgd.getMoney();
		}
		totalTextView.setText(UtilTools.formatMoney("¥", total_money, 2));
	}

	/**
	 * 数据更新
	 * 
	 * @param bean
	 */
	@SuppressLint("HandlerLeak")
	private void updateWBRGDList(P_WBRGD bean) {
		if (bean.getWbrgd_id() != 0) {
			bean.setIDU(GLOBAL.IDU_UPDATE);
		}

		for (int i = 0; i < mUpdatingDataList.size(); i++) {
			if (mUpdatingDataList.get(i).getWbrgd_id() == bean.getWbrgd_id()
					&& mUpdatingDataList.get(i).getWbrgnr_id() == bean
							.getWbrgnr_id()) {
				mUpdatingDataList.set(i, bean);
				return;
			}
		}
		mUpdatingDataList.add(bean);
	}

	@SuppressLint("HandlerLeak") 
	public Handler mHandler = new Handler() {
		public void handleMessage(Message msg) {
			switch (msg.what) {
			case DATA_CHANGED:
				mListAdapter.notifyDataSetChanged();
				showFootingMoney();
				break;
			}
		}
	};

	@Override
	public int getLayoutId() {
		return R.layout.subcontract_add_list_item_title;
	}

	@Override
	public View getHeaderView() {
		return mListHeaderView;
	}

	@Override
	public void regesterListeners(ViewHolder viewHolder, final int position) {
		if (mOperation == OPERATION_DETAIL) {
			return;
		}
		for (int i = 0; i < viewHolder.tvs.length; i++) {
			if ((i == 4 || i == 5 || i == 7 || i == 8 || i == 9 || i == 10)) {
				viewHolder.tvs[i]
						.setOnClickListener(new View.OnClickListener() {
							@Override
							public void onClick(View view) {
								mListAdapter.setSelected(position, true);
								mCurrentItem = mListAdapter.getItem(position);
								modifyWBRGD();
							}
						});
			} else {
				viewHolder.tvs[i]
						.setOnClickListener(new View.OnClickListener() {
							@Override
							public void onClick(View view) {
								mListAdapter.setSelected(position, true);
								mCurrentItem = mListAdapter.getItem(position);
								mOptionsMenuView.showAsDropDown(view, 0, (-view
										.getMeasuredHeight() - UtilTools.dp2pxH(
										view.getContext(), 40)));
							}
						});
			}
		}
	}

	@Override
	public void initListViewItem(View convertView, ViewHolder holder,
			DataListAdapter<P_WBRGD> adapter, int position) {
		Map<String, String> listViewItem = beanToMap(position,
				adapter.getItem(position));
		for (int i = 0; i < mListHeaderNames.length; i++) {
			if ((mOperation != OPERATION_DETAIL)
					&& (i == 4 || i == 5 || i == 7 || i == 8 || i == 9 || i == 10)) {
				Drawable drawable = getResources().getDrawable(
						R.drawable.icon_modify);
				// 这一步必须要做,否则不会显示.
				drawable.setBounds(0, 0, 25, 25);
				holder.tvs[i].setCompoundDrawables(null, null, drawable, null);
				holder.tvs[i].setTextColor(Color.RED);
			} else {
				holder.tvs[i].setTextColor(Color.BLACK);
			}
			holder.tvs[i].setText(listViewItem.get(mListHeaderNames[i]));

			holder.tvs[i]
					.setTextSize(TypedValue.COMPLEX_UNIT_PX, getResources()
							.getDimension(R.dimen.table_content_textsize));
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
			if (i == 0 || i == 4 || i == 5) {
				holder.tvs[i].setGravity(Gravity.CENTER);
				holder.tvs[i].setPadding(0, 0, 0, 0);
			} else if (i == 6 || i == 9 || i == 10) {
				holder.tvs[i].setGravity(Gravity.CENTER | Gravity.RIGHT);
				holder.tvs[i].setPadding(0, 0, 10, 0);
			} else {
				holder.tvs[i].setGravity(Gravity.CENTER | Gravity.LEFT);
				holder.tvs[i].setPadding(10, 0, 0, 0);
			}
		}
	}

	public List<P_WBRGD> findByConditioonCreaten(Object... condition) {
		return null;
	}

	@Override
	public boolean isSameObject(P_WBRGD t1, P_WBRGD t2) {
		return false;
	}

	@Override
	public List<P_WBRGD> findByCondition(Object... condition) {
		return null;
	}

	public Map<String, String> beanToMap(int position, Object bean) {
		Map<String, String> mapItem = new HashMap<String, String>();
		if (bean instanceof P_WBRGD) {
			P_WBRGD wbrg = (P_WBRGD) bean;

			mapItem.put(mListHeaderNames[0], String.valueOf(position + 1));
			mapItem.put(mListHeaderNames[1],
					GLOBAL.RG_TYPE[Integer.valueOf(wbrg.getRg_type()) - 1][1]);// 工种
			mapItem.put(mListHeaderNames[2], wbrg.getRg_work());// 工作内容
			mapItem.put(mListHeaderNames[3], String.valueOf(getString(R.string.company)));// 单位
			mapItem.put(mListHeaderNames[4], String.valueOf(wbrg.getQuantity()));// 数量
			mapItem.put(mListHeaderNames[5],
					UtilTools.formatMoney("¥", wbrg.getUnit_price(), 2));// 单价
			mapItem.put(mListHeaderNames[6],
					UtilTools.formatMoney("¥", wbrg.getMoney(), 2));// 金额
			mapItem.put(mListHeaderNames[7], wbrg.getDw_name());// 来往单位
			mapItem.put(mListHeaderNames[8], wbrg.getTask_name());// 所用部位
			mapItem.put(
					mListHeaderNames[9],
					DateUtils.dateToString(DateUtils.FORMAT_SHORT,
							wbrg.getIndate()));// 入场时间
			mapItem.put(
					mListHeaderNames[10],
					DateUtils.dateToString(DateUtils.FORMAT_SHORT,
							wbrg.getOutdate()));// 撤出时间

		}
		return mapItem;
	}

	// 按钮设置
	private void setupButtons() {
		exe_contractET = (EditText) findViewById(R.id.executive_contract);
		btn_add = (Button) findViewById(R.id.subcontract_addlist);
		Button submitButton = (Button) findViewById(R.id.submit);
		Button approvalButton = (Button) findViewById(R.id.approval);
		btn_save = (Button) findViewById(R.id.subcontract_add_save);
		exe_contractET.setFocusableInTouchMode(false);
		exe_contractET.clearFocus();
		mRemarkContent = (EditText) findViewById(R.id.remark_content_edit);

		exe_contractET.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				Intent intent = new Intent(v.getContext(), null);

				intent.putExtra("title", getString(R.string.perform_subcontract));
				intent.putExtra("project", mProject);
				intent.putExtra("type", 2);

				startActivityForResult(intent, SELECT_SUBCONTRACT);
			}
		});

		btn_add.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				AddWBRGD();
			}
		});

		if (mOperation == OPERATION_DETAIL) {// 详情
			exe_contractET.setEnabled(false);
			mRemarkContent.setEnabled(false);
			if (!hasFlowApproval
					|| (hasFlowApproval && (mCurrentWBRGBean.getStatus() == Integer
							.parseInt(GLOBAL.FLOW_APPROVAL_STATUS[0][0]) || mCurrentWBRGBean
							.getStatus() == 0))) {// 没有审批或未提交
				{
					btn_save.setVisibility(View.GONE);
					return;
				}
			}
		} else {
			btn_add.setVisibility(View.VISIBLE);
		}

		OnClickListener saveListener = (new OnClickListener() {

			@Override
			public void onClick(View view) {
				if (mOperation == OPERATION_ADD) {
					if (!checkDataList(mUpdatingDataList))
						return;
					// 增加外包人工单
					double total_money = 0.0;
					for (P_WBRGD wbrgd : mShowWBRGDDataList) {
						total_money += wbrgd.getMoney();
					}
					mCurrentWBRGBean
							.setCreater(UserCache.getCurrentUser().getUser_id());
					mCurrentWBRGBean.setMark(mRemarkContent.getText()
							.toString());
					mCurrentWBRGBean.setTotal_money(total_money);

					RemoteSubCcontentService.getInstance().addWBRG(
							new DataManagerInterface() {

								@Override
								public void getDataOnResult(
										ResultStatus status, List<?> list) {
								}
							}, mCurrentWBRGBean, mUpdatingDataList);
					Intent mIntent = new Intent();
					// mIntent.putExtras(bundle);
					setResult(RESULT_OK, mIntent);
					finish();

				} else if (mOperation == OPERATION_MODIFY) {
					// 更新外包人工单
					double total_money = 0.0;
					for (P_WBRGD wbrgd : mShowWBRGDDataList) {
						total_money += wbrgd.getMoney();
					}
					mCurrentWBRGBean.setMark(mRemarkContent.getText()
							.toString());
					mCurrentWBRGBean.setTotal_money(total_money);

					RemoteSubCcontentService.getInstance().updateWBRG(
							new DataManagerInterface() {

								@Override
								public void getDataOnResult(
										ResultStatus status, List<?> list) {
								}
							}, mCurrentWBRGBean, mUpdatingDataList);

					if (mCurrentWBRGBean.getWbrg_id() != 0
							&& mShowWBRGDDataList.size() == 0) {
						RemoteSubCcontentService.getInstance().deleteWBRG(
								new DataManagerInterface() {

									@Override
									public void getDataOnResult(
											ResultStatus status, List<?> list) {
									}
								}, mCurrentWBRGBean.getWbrg_id());
					}
					Intent mIntent = new Intent();
					setResult(RESULT_OK, mIntent);
					finish();
				}
			}
		});
		
		OnClickListener approvalListener = new OnClickListener() {
			public void onClick(View v) {
				Flow_approval flowApproval = new Flow_approval();
				flowApproval.setFlow_type(GLOBAL.FLOW_TYPE[4][0]);
				flowApproval.setType_id(mCurrentWBRGBean.getWbrg_id());

				FlowApprovalDialog dialog = new FlowApprovalDialog(
						SubcontractManagerAddActivity.this, flowApproval,
						mFlowSetting, flowApprovalManager);

				if (mCurrentWBRGBean.getStatus() == Integer
						.parseInt(GLOBAL.FLOW_APPROVAL_STATUS[1][0])) {
					dialog.show(true);
				} else {
					dialog.show(false);
				}
			}
		};

		if (hasFlowApproval) {// 审批流程开启
			// sync GLOBAL.FLOW_APPROVAL_STATUS
			if (mCurrentWBRGBean.getStatus() == 0) {
				mCurrentWBRGBean.setStatus(1);
				// mCurrentWBRGBean.setCreate_person(3);
			}

			if (!isSaveButtonStyle()) {
				btn_save.setOnClickListener(saveListener);
				submitButton.setVisibility(View.VISIBLE);
				submitButton.setOnClickListener(new OnClickListener() {

					@Override
					public void onClick(View v) {
						// mCurrentWBRGBean.setStatus(2);
						commonConfirmSubmit();
					}

				});
				if (submitButtonStyle() == REJECT_BUTTON_STYLE
						&& (mOperation == OPERATION_MODIFY)) {
					btn_add.setVisibility(View.GONE);
					approvalButton.setVisibility(View.VISIBLE);
					approvalButton.setOnClickListener(approvalListener);
				}
			} else {// 只有审批按钮
				if (UserCache.getCurrentUser()
						.getUser_id() != mCurrentWBRGBean.getCreater())
					btn_add.setVisibility(View.GONE);
				btn_save.setText(R.string.approve);
				btn_save.setOnClickListener(approvalListener);
			}
		} else {// 审批流程关闭
			if (mOperation == OPERATION_DETAIL) {
				if (!hasFlowApproval
						|| (hasFlowApproval && mCurrentWBRGBean.getStatus() == Integer
								.parseInt(GLOBAL.FLOW_APPROVAL_STATUS[0][0]))) {
					{
						btn_save.setVisibility(View.GONE);
					}
				}
			}
			btn_save.setOnClickListener(saveListener);
			submitButton.setVisibility(View.VISIBLE);
			submitButton.setOnClickListener(new OnClickListener() {

				@Override
				public void onClick(View v) {
					commonConfirmSubmit();
				}
			});
		}
	}

	private void PassWBRGFlowDataToServer2(String codeNumber) {
		P_WBRG wbrg = new P_WBRG();
		P_WBRG mWBRG = mCurrentWBRGBean;

		wbrg.setWbrg_id(mWBRG.getWbrg_id());
		wbrg.setWbht_id(mWBRG.getWbht_id());
		wbrg.setWbht_name(mWBRG.getWbht_name());
		wbrg.setStatus(mWBRG.getStatus());
		wbrg.setCreater(mWBRG.getCreater());
		wbrg.setProject_id(mWBRG.getProject_id());
		wbrg.setMark(mWBRG.getMark());
		wbrg.setTenant_id(mWBRG.getTenant_id());
		wbrg.setWbrg_number(codeNumber);
		wbrg.setTotal_money(mWBRG.getTotal_money());
		wbrg.setTenant_id(mWBRG.getTenant_id());
		List<P_WBRGD> mWBRGDList = mUpdatingDataList;
		List<P_WBRGD> listWBRGD = (List<P_WBRGD>) mWBRGDList;
		List<P_WBRGD> wbrgdList = new ArrayList<P_WBRGD>();
		for (int i = 0; i < listWBRGD.size(); i++) {
			P_WBRGD wbrgd = new P_WBRGD();

			wbrgd.setWbrgnr_id(mWBRGDList.get(i).getWbrgnr_id());
			wbrgd.setRg_type(mWBRGDList.get(i).getRg_type());
			wbrgd.setRg_work(mWBRGDList.get(i).getRg_work());
			wbrgd.setIDU(mWBRGDList.get(i).getIDU());
			wbrgd.setQuantity(mWBRGDList.get(i).getQuantity());
			wbrgd.setUnit_price(mWBRGDList.get(i).getUnit_price());
			wbrgd.setMoney(mWBRGDList.get(i).getMoney());
			wbrgd.setLwdw_id(mWBRGDList.get(i).getLwdw_id());
			wbrgd.setTask_id(mWBRGDList.get(i).getTask_id());
			wbrgd.setDw_name(mWBRGDList.get(i).getDw_name());
			wbrgd.setTask_name(mWBRGDList.get(i).getTask_name());
			wbrgd.setIndate(mWBRGDList.get(i).getIndate());
			wbrgd.setOutdate(mWBRGDList.get(i).getOutdate());

			wbrgdList.add(wbrgd);
		}
		RemoteSubCcontentService.getInstance().passApproval(
				mFlowApprovalDataInterface, wbrg, mCurrentApproval,
				mNextApproval);

	}

	private DataManagerInterface mWBRGCodeManager = new DataManagerInterface() {

		@Override
		public void getDataOnResult(ResultStatus status, List<?> list) {

			if (status.getCode() == AnalysisManager.SUCCESS_DB_QUERY) {

				PassWBRGFlowDataToServer2(status.getMessage());
			}
		}

	};

	@SuppressWarnings("unused")
	private void PassFlowDataToServer1() {
		RemoteCommonService.getInstance().getCodeByDate(mWBRGCodeManager,
				"WBRG");
	}

	FlowApprovalDialog.FlowApprovalManager flowApprovalManager;
//	FlowApprovalDialog.FlowApprovalManager flowApprovalManager = new FlowApprovalDialog.FlowApprovalManager() {
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
//			P_WBRG wbrg = new P_WBRG();
//			wbrg.setWbrg_id(mCurrentWBRGBean.getWbrg_id());
//			RemoteSubCcontentService.getInstance().rebutApproval(
//					flowApprovalManagerInterface, wbrg, currentApproval,
//					nextApproval);
//		}
//
//	};

	private int submitButtonStyle() {
		if (mCurrentWBRGBean.getStatus() == Integer
				.parseInt(GLOBAL.FLOW_APPROVAL_STATUS[0][0])) {
			return SUBMIT_BUTTON_STYLE;
        } else if ((mCurrentWBRGBean.getStatus() == Integer
                .parseInt(GLOBAL.FLOW_APPROVAL_STATUS[3][0]) && UserCache
                .getCurrentUserId() == mCurrentWBRGBean
                .getCreater())) {
            return REJECT_BUTTON_STYLE;
		} else {
			return APPROVAL_BUTTON_STYLE;
		}
	}

	// save button can't be live alone, otherwise it is approval button
	private boolean isSaveButtonStyle() {
		if (mMessage != null && mMessage.getIs_process() == 1) {
			return true;
		}
		if (submitButtonStyle() == SUBMIT_BUTTON_STYLE
				|| (submitButtonStyle() == REJECT_BUTTON_STYLE && mOperation == OPERATION_MODIFY)) {
			return false;
		} else {
			// actually it is approval button
			return true;
		}
	}

	// 调用添加资源的返回。
	public void onActivityResult(int requestCode, int resultCode, Intent data) {
		if (resultCode == 0 || data == null)
			return;
		if (requestCode == SELECT_LWDW_REQUEST) {
			if (resultCode == RESULT_CANCELED) {
				// setTitle("Canceled...");
			} else if (resultCode == RESULT_OK) {
				// Bundle bundle = data.getExtras();
				lwdw = (P_LWDW) data
						.getSerializableExtra(ListSelectActivity.RESULT_KEY);
				if (lwdw != null) {
					// mCurrentItem.setDw_name(lwdw.getName());
					// mCurrentItem.setLwdw_id(lwdw.getLwdw_id());
					mSelectLWDWEditText.setText(lwdw.getName());
					// updateWBRGDList(mCurrentItem);
					// mListAdapter.updateData(mCurrentItem);
				}
			}
		} else if (requestCode == ADD_WBRGD_REQUEST) {
			if (resultCode == RESULT_CANCELED) {
				// setTitle("Canceled...");
			} else if (resultCode == RESULT_OK) {
				@SuppressWarnings("unchecked")
				List<P_WBRGNR> wbngnrs = (List<P_WBRGNR>) data
						.getSerializableExtra(ListSelectActivity.RESULT_KEY);
				if (wbngnrs != null && !wbngnrs.isEmpty()) {
					for (P_WBRGNR wbngnr : wbngnrs) {
						P_WBRGD bean = new P_WBRGD();
						if (mCurrentWBRGBean.getWbrg_id() != 0) {
							bean.setWbrg_id(mCurrentWBRGBean.getWbrg_id());
						}
						bean.setWbrgnr_id(wbngnr.getWbrgnr_id());
						bean.setRg_type(wbngnr.getType());
						bean.setRg_work(wbngnr.getWork());
						bean.setIDU(GLOBAL.IDU_INSERT); // 插入
						mShowWBRGDDataList.add(bean);
						mUpdatingDataList.add(bean);
					}
					mListAdapter.notifyDataSetChanged();
				}
			}
		} else if (requestCode == TASK_SELECT_REQUEST) {
			if (resultCode == RESULT_OK) {
				task = (Task) data
						.getSerializableExtra(PlanMakeSelectActivity.TASK_CODE);
				if (task != null) {
					// mCurrentItem.setTask_id(task.getTask_id());
					// mCurrentItem.setTask_name(task.getName());
					// if (mCurrentItem.getWbrgd_id() != 0)
					// mCurrentItem.setIDU(GLOBAL.IDU_UPDATE);
					mSelectTaskEditText.setText(task.getName());
					// updateWBRGDList(mCurrentItem);
					// mListAdapter.updateData(mCurrentItem);
				}
			}
		} else if (requestCode == SELECT_SUBCONTRACT) {
			Contract contract = (Contract) data
					.getSerializableExtra("contract");
			if (contract != null) {
				exe_contractET.setText(contract.getName());
				mCurrentWBRGBean.setWbht_id(contract.getContract_id());
				mCurrentWBRGBean.setWbht_name(contract.getName());
			}
		}

		super.onActivityResult(requestCode, resultCode, data);
	}



	@SuppressLint("UseSparseArrays")
	private void initListViewDialog() {
		mListViewDialog = new BaseDialog(this, getString(R.string.modify_manpower));
		Map<Integer, Integer> buttons = new HashMap<Integer, Integer>();
		buttons.put(0, BaseDialog.decimalEditTextLineStyle);
		buttons.put(1, BaseDialog.decimalEditTextLineStyle);
		buttons.put(2, BaseDialog.editTextClickLineStyle);
		buttons.put(3, BaseDialog.editTextClickLineStyle);
		buttons.put(4, BaseDialog.calendarLineStyle);
		buttons.put(5, BaseDialog.calendarLineStyle);

		mListViewDialog.init(R.array.subcontract_add_dialog, buttons, null);
		final EditText startEt = (EditText) mListViewDialog.getEditTextView(4);
		final EditText endEt = (EditText) mListViewDialog.getEditTextView(5);

		final DoubleDatePickerDialog doubleDatePickerDialog = new DoubleDatePickerDialog(
				this, null, startEt, endEt, null);

		OnClickListener listener = new OnClickListener() {

			@Override
			public void onClick(View v) {
				doubleDatePickerDialog.show(mCurrentItem.getIndate(),
						mCurrentItem.getOutdate());
			}
		};
		mListViewDialog.setEditTextStyle(4, 0, listener, null);
		mListViewDialog.setEditTextStyle(5, 0, listener, null);

		mListDialogNames = getResources().getStringArray(
				R.array.subcontract_add_dialog);
		mSelectLWDWEditText = (EditText) mListViewDialog.getPopupView()
				.findViewById(mListViewDialog.baseEditTextId + 2);
		mSelectLWDWEditText.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				SelectLWDW();
			}
		});
		mSelectTaskEditText = (EditText) mListViewDialog.getPopupView()
				.findViewById(mListViewDialog.baseEditTextId + 3);
		mSelectTaskEditText.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				SelectTask();
			}
		});

		Button saveImageView = (Button) mListViewDialog.getPopupView()
				.findViewById(R.id.save_Button);
		saveImageView.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				Map<String, String> items = mListViewDialog.SaveData();
				for (Map.Entry<String, String> entry : items.entrySet()) {
					if (entry.getValue().equals("")) {
						Toast.makeText(getApplicationContext(),
								R.string.pls_input_all_info, Toast.LENGTH_LONG)
								.show();
						return;
					}
				}

				int count = 0;
				double quantity = Double.parseDouble(items
						.get(mListDialogNames[count++]));
				double unitPrice = Double.parseDouble(items
						.get(mListDialogNames[count++]));
				String dw = items.get(mListDialogNames[count++]);
				String task_name = items.get(mListDialogNames[count++]);
				Date indate = DateUtils.stringToDate(DateUtils.FORMAT_SHORT,
						items.get(mListDialogNames[count++]));
				Date outdate = DateUtils.stringToDate(DateUtils.FORMAT_SHORT,
						items.get(mListDialogNames[count++]));
				double money = quantity * unitPrice;

				mCurrentItem.setQuantity(quantity);
				mCurrentItem.setUnit_price(unitPrice);
				mCurrentItem.setMoney(money);
				if (lwdw != null)
					mCurrentItem.setLwdw_id(lwdw.getLwdw_id());
				if (task != null)
					mCurrentItem.setTask_id(task.getTask_id());
				mCurrentItem.setDw_name(dw);
				mCurrentItem.setTask_name(task_name);
				mCurrentItem.setIndate(indate);
				mCurrentItem.setOutdate(outdate);
				if (mCurrentItem.getWbrgd_id() != 0)
					mCurrentItem.setIDU(GLOBAL.IDU_UPDATE);
				updateWBRGDList(mCurrentItem);
				mListAdapter.updateData(mCurrentItem);
				mListViewDialog.dismiss();
				showFootingMoney();
			}
		});

	}

	private void showFootingMoney() {
		List<P_WBRGD> showList = mListAdapter.getDataShowList();
		double footing = 0;
		for (P_WBRGD item : showList) {
			footing += item.getMoney();
		}
		mFootingTextView.setText(UtilTools.formatMoney("¥", footing, 2));
	}

	private boolean checkDataList(List<P_WBRGD> lists) {
		for (P_WBRGD wbrgd : lists) {
			if (wbrgd.getQuantity() > 0 && wbrgd.getUnit_price() > 0
					&& wbrgd.getIndate() != null && wbrgd.getLwdw_id() > 0
					&& wbrgd.getTask_name() != null
					&& !wbrgd.getTask_name().equals(""))
				return true;
		}
		Toast.makeText(this, getString(R.string.enter_unit_quantity), Toast.LENGTH_SHORT)
				.show();
		return false;
	}

	private DataManagerInterface mWBRGManager = new DataManagerInterface() {

		@Override
		public void getDataOnResult(ResultStatus status, List<?> list) {

			if (status.getCode() == AnalysisManager.SUCCESS_DB_QUERY) {
				if (list != null && !list.isEmpty()) {
					mCurrentWBRGBean = (P_WBRG) list.get(0);
					mProject = new Project();
					// mProject.setName(mCurrentWBRGBean.getProject_name());
					mProject.setProject_id(mCurrentWBRGBean.getProject_id());

					RemoteFlowSettingService.getInstance().getFlowDetail(
							mFlowManager,
							UserCache.getCurrentUser().getTenant_id(),
							GLOBAL.FLOW_TYPE[4][0]);

				}
			}
		}

	};

	@SuppressLint("HandlerLeak")
	private Handler mToastHandler = new Handler() {
		public void handleMessage(Message msg) {
			Toast.makeText(SubcontractManagerAddActivity.this,
					(CharSequence) msg.obj, Toast.LENGTH_SHORT).show();
		}
	};

	private DataManagerInterface mSubmitManager = new DataManagerInterface() {

		@Override
		public void getDataOnResult(ResultStatus status, List<?> list) {
			// dismissProgressDialog();
			if (status.getCode() != AnalysisManager.SUCCESS_DB_QUERY) {
				Message msg = new Message();
				msg.obj = status.getMessage();
				mToastHandler.sendMessage(msg);
			}
			LogUtil.i("wzw status" + status.getCode());
			if (status.getCode() == AnalysisManager.SUCCESS_DB_ADD
					|| status.getCode() == AnalysisManager.SUCCESS_DB_UPDATE) {
				Intent intent = new Intent();
				setResult(Activity.RESULT_OK, intent);
				finish();
			}
		}

	};

	private void SubmitFlowDataToServer() {

		double total_money = 0.0;
		for (P_WBRGD wbrgd : mShowWBRGDDataList) {
			total_money += wbrgd.getMoney();
		}
		mCurrentWBRGBean.setMark(mRemarkContent.getText().toString());
		mCurrentWBRGBean.setTotal_money(total_money);
		if (hasFlowApproval) {
			mCurrentWBRGBean.setStatus(Integer
					.parseInt(GLOBAL.FLOW_APPROVAL_STATUS[1][0]));
			Flow_approval flowApproval = new Flow_approval();
			flowApproval.setType_id(mCurrentWBRGBean.getWbrg_id());
			flowApproval.setFlow_type(GLOBAL.FLOW_TYPE[4][0]);
			flowApproval
					.setSubmiter(UserCache.getCurrentUser().getUser_id());
			flowApproval
					.setTenant_id(UserCache.getCurrentUser().getTenant_id());
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
			if (mOperation == OPERATION_ADD) {
				mCurrentWBRGBean
						.setCreater(UserCache.getCurrentUser().getUser_id());
				RemoteSubCcontentService.getInstance()
						.submitWithApprovalForAdd(mSubmitManager,
								mCurrentWBRGBean, mUpdatingDataList,
								flowApproval);
			} else if (mOperation == OPERATION_MODIFY) {
				RemoteSubCcontentService.getInstance()
						.submitWithApprovalForUpdate(mSubmitManager,
								mCurrentWBRGBean, mUpdatingDataList,
								flowApproval);
			}
		} else {
			mCurrentWBRGBean.setStatus(Integer
					.parseInt(GLOBAL.FLOW_APPROVAL_STATUS[2][0]));
			if (mOperation == OPERATION_ADD) {
				mCurrentWBRGBean
						.setCreater(UserCache.getCurrentUser().getUser_id());
				RemoteSubCcontentService.getInstance().submitNoApprovalForAdd(
						mSubmitManager, mCurrentWBRGBean, mUpdatingDataList);
			} else if (mOperation == OPERATION_MODIFY) {
				// RemoteSubCcontentService.getInstance().submitNoApprovalForUpdate(mSubmitManager,
				// mCurrentWBRGBean, mUpdatingDataList);
				RemoteSubCcontentService.getInstance()
						.submitNoApprovalForUpdate(mSubmitManager,
								mCurrentWBRGBean);
			}
		}

	}
}
