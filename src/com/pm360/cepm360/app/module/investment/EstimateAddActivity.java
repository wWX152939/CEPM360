package com.pm360.cepm360.app.module.investment;

import android.annotation.SuppressLint;
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
import com.pm360.cepm360.app.cache.ProjectCache;
import com.pm360.cepm360.app.cache.UserCache;
import com.pm360.cepm360.app.common.activity.ActionBarFragmentActivity;
import com.pm360.cepm360.app.common.activity.DataManagerInterface;
import com.pm360.cepm360.app.common.adpater.DataListAdapter;
import com.pm360.cepm360.app.common.adpater.DataListAdapter.ViewHolder;
import com.pm360.cepm360.app.common.view.OptionsMenuView;
import com.pm360.cepm360.app.common.view.parent.BaseDialog;
import com.pm360.cepm360.app.utils.UtilTools;
import com.pm360.cepm360.common.GLOBAL;
import com.pm360.cepm360.common.ResultStatus;
import com.pm360.cepm360.common.json.AnalysisManager;
import com.pm360.cepm360.entity.InvestmentEstimate;
import com.pm360.cepm360.entity.InvestmentEstimateD;
import com.pm360.cepm360.entity.User;
import com.pm360.cepm360.services.common.RemoteCommonService;
import com.pm360.cepm360.services.investment.RemoteInvestmentEstimateService;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class EstimateAddActivity extends ActionBarFragmentActivity implements
		DataListAdapter.ListAdapterInterface<InvestmentEstimateD> {
	public final static String MSG_SUBCONTRACT_MANAGEMENT_ACTION = "com.pm360.cepm360.action.estimate_manager";
	// 列表头显示名称
	private View mListHeaderView;
	private View mFootingRemark;
	private TextView mFootingTextView;
	private String[] mListHeaderNames;
	private int[] mDisplayItemIds;
	private InvestmentEstimateD mCurrentItem;
	private Button btn_add;
	private Button btn_save;
	private EditText mRemarkContent;
	private OptionsMenuView mOptionsMenuView;
	private TextView mEstimateNumberTextView;
	private TextView mTotalTextView;

	private TextView mEstimateProjectTextView;
	private EditText mEstimateNameEditText;
	private EditText mEstimateYearEditText;
	private TextView mCreateTextView;

	private ListView mInvestmentEstimateDListView;
	private List<InvestmentEstimateD> mShowInvestmentEstimateDDataList = new ArrayList<InvestmentEstimateD>();
	private List<InvestmentEstimateD> mUpdatingDataList = new ArrayList<InvestmentEstimateD>();
	private int mOperation;
	private InvestmentEstimate mInvestmentEstimate;
	private static final int OPERATION_DETAIL = 10;
	private static final int OPERATION_ADD = 11;
	private static final int OPERATION_MODIFY = 12;
	private static final int DATA_CHANGED = 100;
	private DataListAdapter<InvestmentEstimateD> mListAdapter;
//	private Project mSelectProject;

	private BaseDialog mListViewDialog;
	private String[] mListDialogNames;

	private int OPERATOR_SELECT_CODE = 204;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		enableMenuView(false);

		/* 全屏显示 */
		getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,
				WindowManager.LayoutParams.FLAG_FULLSCREEN);
		msgHandlerProgress();
		/* 载入布局文件 */
		setContentView(R.layout.estimate_add);
		initWindow();
	}

	private void initWindow() {
//		mSelectProject = ProjectCache.findProjectById(
//				mInvestmentEstimate.getProject_id());
//		if (mSelectProject != null) {
//			setActionBarTitle(mSelectProject.getName());
//		}
		mFootingRemark = findViewById(R.id.investment_estimate_add_bottom);
		mFootingTextView = (TextView) mFootingRemark
				.findViewById(R.id.total_content);
		mEstimateNumberTextView = (TextView) findViewById(R.id.estimate_number);
		mEstimateNumberTextView.setBackground(getResources().getDrawable(
				R.drawable.bg_edittext));
		mEstimateNameEditText = (EditText) findViewById(R.id.estimate_name);
		mEstimateYearEditText = (EditText) findViewById(R.id.estimate_year);
		mEstimateProjectTextView = (TextView) findViewById(R.id.estimate_project);
		mEstimateProjectTextView.setBackground(getResources().getDrawable(
				R.drawable.bg_edittext));
		mCreateTextView = (TextView) findViewById(R.id.creater);

		mTotalTextView = (TextView) this.findViewById(R.id.total_content);
		mRemarkContent = (EditText) findViewById(R.id.remark_content_edit);
		mInvestmentEstimateDListView = (ListView) this
				.findViewById(R.id.listView);
		mListHeaderView = this.findViewById(R.id.listHeaderView);
		mListHeaderNames = getApplicationContext().getResources()
				.getStringArray(R.array.estimate_header_title_names);

		TypedArray titleType = getApplicationContext().getResources()
				.obtainTypedArray(R.array.estimate_header_title_ids);

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

		mListAdapter = new DataListAdapter<InvestmentEstimateD>(this, this,
				mShowInvestmentEstimateDDataList);
		if (mOperation == OPERATION_DETAIL || mOperation == OPERATION_MODIFY) { // 查看详细&修改
			mEstimateNumberTextView.setText(mInvestmentEstimate
					.getEstimate_number());
			mEstimateProjectTextView.setText(ProjectCache
					.findProjectById(mInvestmentEstimate.getProject_id())
					.getName());
			mEstimateNameEditText.setText(mInvestmentEstimate
					.getEstimate_name());
			mEstimateYearEditText.setText(mInvestmentEstimate
					.getEstimate_period());
			mCreateTextView.setText(UserCache
					.findUserById(mInvestmentEstimate.getCreater()).getName());
			mTotalTextView.setText(UtilTools.formatMoney("¥",
					mInvestmentEstimate.getEstimate_money(), 2));
			mRemarkContent.setText(mInvestmentEstimate.getMark());
			loaderInvestmentEstimateDData(mInvestmentEstimate);
		} else {// 添加
			mCreateTextView.setText(UserCache.getCurrentUser()
					.getName());
			mInvestmentEstimate.setCreater((UserCache
					.getCurrentUser().getUser_id()));
			mShowInvestmentEstimateDDataList.clear();
			mUpdatingDataList.clear();
			mEstimateProjectTextView.setText(ProjectCache
					.findProjectById(mInvestmentEstimate.getProject_id())
					.getName());
			RemoteCommonService.getInstance().getCodeByDate(
					InvestmentEstimateCodeManager, "TZGS");
		}

		setupButtons();
		initListViewDialog();
		int pxw = UtilTools.dp2pxW(getBaseContext(), 8);
		int pxh = UtilTools.dp2pxH(getBaseContext(), 8);
		mCreateTextView.setBackground(getResources().getDrawable(
				R.drawable.bg_edittext));
		mCreateTextView.setGravity(Gravity.CENTER_VERTICAL);
		mCreateTextView.setTextSize(TypedValue.COMPLEX_UNIT_PX, getResources()
				.getDimension(R.dimen.table_title_textsize));
		mCreateTextView.setPadding(pxw, pxh, pxw, pxh);
		if (mOperation != OPERATION_DETAIL) {
			initOptionsMenuView();
		} else {
			mEstimateNameEditText.setBackground(getResources().getDrawable(
					R.drawable.bg_edittext));
			mEstimateNameEditText.setGravity(Gravity.CENTER_VERTICAL);
			mEstimateNameEditText.setTextSize(TypedValue.COMPLEX_UNIT_PX,
					getResources().getDimension(R.dimen.table_title_textsize));
			mEstimateNameEditText.setPadding(pxw, pxh, pxw, pxh);

			mEstimateProjectTextView.setBackground(getResources().getDrawable(
					R.drawable.bg_edittext));
			mEstimateProjectTextView.setGravity(Gravity.CENTER_VERTICAL);
			mEstimateProjectTextView.setTextSize(TypedValue.COMPLEX_UNIT_PX,
					getResources().getDimension(R.dimen.table_title_textsize));
			mEstimateProjectTextView.setPadding(pxw, pxh, pxw, pxh);

			mEstimateYearEditText.setBackground(getResources().getDrawable(
					R.drawable.bg_edittext));
			mEstimateYearEditText.setGravity(Gravity.CENTER_VERTICAL);
			mEstimateYearEditText.setTextSize(TypedValue.COMPLEX_UNIT_PX,
					getResources().getDimension(R.dimen.table_title_textsize));
			mEstimateYearEditText.setPadding(pxw, pxh, pxw, pxh);

		}

		mInvestmentEstimateDListView.setAdapter(mListAdapter);

	}

	private void msgHandlerProgress() {
		Intent intent = getIntent();
		mOperation = (Integer) intent.getSerializableExtra("operation");
		mInvestmentEstimate = (InvestmentEstimate) intent
				.getSerializableExtra("data");
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
							modifyInvestmentEstimateD();
							break;
						case 1: // 删除
							commonConfirmDeleteInvestmentEstimateD();
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
			mEstimateNumberTextView.setText(code);
			if (mOperation == OPERATION_ADD) {
				mInvestmentEstimate.setTenant_id(UserCache
						.getCurrentUser().getTenant_id());
				// mInvestmentEstimate.setProject_id(ProjectCache
				// .getCurrentProject().getProject_id());
				mInvestmentEstimate.setEstimate_number(code);
			}
		}
	};

	private DataManagerInterface InvestmentEstimateCodeManager = new DataManagerInterface() {
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

	private void loaderInvestmentEstimateDData(
			InvestmentEstimate InvestmentEstimate) {
		RemoteInvestmentEstimateService.getInstance().getInvestmentEstimate(
				mManager, InvestmentEstimate.getInvestment_estimate_id());
	}

	private DataManagerInterface mManager = new DataManagerInterface() {

		public void getDataOnResult(ResultStatus status, List<?> list) {
			switch (status.getCode()) {
			case AnalysisManager.SUCCESS_DB_QUERY:
				if (list != null && list.size() > 0) {
					mShowInvestmentEstimateDDataList.clear();
					mUpdatingDataList.clear();
					for (Object object : list) {
						if (object instanceof InvestmentEstimateD) {
							mShowInvestmentEstimateDDataList
									.add((InvestmentEstimateD) object);
							mUpdatingDataList.add((InvestmentEstimateD) object);
						}
					}
					mHandler.sendEmptyMessage(DATA_CHANGED);
				}
			default:
				break;
			}
		}
	};

	private void AddInvestmentEstimateD() {
		mListViewDialogMode(0);
		mListViewDialog.show(null);
	}

	private void modifyInvestmentEstimateD() {
		String[] editTexts = new String[7];
		editTexts[0] = mCurrentItem.getCost_name();
		editTexts[1] = String.valueOf(mCurrentItem.getConstruction_cost());
		editTexts[2] = String.valueOf(mCurrentItem.getInstall_cost());
		editTexts[3] = String.valueOf(mCurrentItem.getOther_cost());
		editTexts[4] = mCurrentItem.getUnit();
		editTexts[5] = String.valueOf(mCurrentItem.getAmount());
		editTexts[6] = mCurrentItem.getMark();
		mListViewDialogMode(1);
		mListViewDialog.show(editTexts);
	}

	private void commonConfirmDeleteInvestmentEstimateD() {
		// 创建确认删除对话框，并显示
		new AlertDialog.Builder(this)
				// 设置对话框主体内容
				.setMessage(getResources().getString(R.string.confirm_delete))
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
								deleteInvestmentEstimateD();
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
	private void deleteInvestmentEstimateD() {
		mCurrentItem.setIDU(GLOBAL.IDU_DELETE);
		if (mCurrentItem.getInvestment_estimate_d_id() == 0)
			mUpdatingDataList.remove(mCurrentItem);
		mShowInvestmentEstimateDDataList.remove(mCurrentItem);
		mListAdapter.notifyDataSetChanged();
		double total_money = 0.0;
		for (InvestmentEstimateD tzgsd : mShowInvestmentEstimateDDataList) {
			total_money += tzgsd.getSum();
		}
		mTotalTextView.setText(UtilTools.formatMoney("¥", total_money, 2));
	}

	/**
	 * 数据更新
	 * 
	 * @param bean
	 */
	@SuppressLint("HandlerLeak")
	private void updateInvestmentEstimateDList(InvestmentEstimateD bean) {
		if (bean.getInvestment_estimate_d_id() != 0) {
			bean.setIDU(GLOBAL.IDU_UPDATE);
		}

		for (int i = 0; i < mUpdatingDataList.size(); i++) {
			if (mUpdatingDataList.get(i).getInvestment_estimate_d_id() == bean
					.getInvestment_estimate_d_id()) {
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
		return R.layout.investment_estimate_add_list_item_title;
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
			if (i != 0 && i != 5) {
				viewHolder.tvs[i]
						.setOnClickListener(new View.OnClickListener() {
							@Override
							public void onClick(View view) {
								mListAdapter.setSelected(position, true);
								mCurrentItem = mListAdapter.getItem(position);
								modifyInvestmentEstimateD();
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
										.getMeasuredHeight() - UtilTools
										.dp2pxH(view.getContext(), 40)));
							}
						});
			}
		}
	}

	@Override
	public void initListViewItem(View convertView, ViewHolder holder,
			DataListAdapter<InvestmentEstimateD> adapter, int position) {
		Map<String, String> listViewItem = beanToMap(position,
				adapter.getItem(position));
		for (int i = 0; i < mListHeaderNames.length; i++) {
			if ((mOperation != OPERATION_DETAIL) && (i != 0 && i != 5 && i != 8)) {
				Drawable drawable = getResources().getDrawable(
						R.drawable.icon_modify);
				// 这一步必须要做,否则不会显示.
				drawable.setBounds(0, 0, 25, 25);
				holder.tvs[i].setCompoundDrawables(drawable, null, null, null);
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
			convertView.setBackgroundResource(R.color.listview_selected_bg);
		} else {
			if (position % 2 == 1) {
				convertView
						.setBackgroundResource(R.color.content_listview_single_line_bg);
			} else {
				convertView.setBackgroundColor(Color.TRANSPARENT);
			}
		}
	}

	@Override
	public void initLayout(View convertView, ViewHolder holder) {
		holder.tvs = new TextView[mDisplayItemIds.length];
		for (int i = 0; i < mDisplayItemIds.length; i++) {
			holder.tvs[i] = (TextView) convertView
					.findViewById(mDisplayItemIds[i]);
			if (i == 2 || i == 3 || i == 4 || i == 5 || i == 6 || i == 7) {
				holder.tvs[i].setGravity(Gravity.CENTER | Gravity.LEFT);
				holder.tvs[i].setPadding(5, 0, 0, 0);
			} else {
				holder.tvs[i].setGravity(Gravity.CENTER | Gravity.LEFT);
				holder.tvs[i].setPadding(10, 0, 0, 0);
			}
		}
	}

	public List<InvestmentEstimateD> findByConditioonCreaten(
			Object... condition) {
		return null;
	}

	@Override
	public boolean isSameObject(InvestmentEstimateD t1, InvestmentEstimateD t2) {
		return false;
	}

	@Override
	public List<InvestmentEstimateD> findByCondition(Object... condition) {
		return null;
	}

	public Map<String, String> beanToMap(int position, Object bean) {
		Map<String, String> mapItem = new HashMap<String, String>();
		if (bean instanceof InvestmentEstimateD) {
			InvestmentEstimateD tzgsd = (InvestmentEstimateD) bean;

			mapItem.put(mListHeaderNames[0], String.valueOf(position + 1));
			mapItem.put(mListHeaderNames[1], tzgsd.getCost_name());//
			mapItem.put(mListHeaderNames[2],
					UtilTools.formatMoney("¥", tzgsd.getConstruction_cost(), 2));//
			mapItem.put(mListHeaderNames[3],
					UtilTools.formatMoney("¥", tzgsd.getInstall_cost(), 2));//
			mapItem.put(mListHeaderNames[4],
					UtilTools.formatMoney("¥", tzgsd.getOther_cost(), 2));// 数量
			mapItem.put(mListHeaderNames[5],
					UtilTools.formatMoney("¥", tzgsd.getSum(), 2));//
			mapItem.put(mListHeaderNames[6], tzgsd.getUnit());//
			mapItem.put(mListHeaderNames[7], String.valueOf(tzgsd.getAmount()));
			mapItem.put(mListHeaderNames[8], tzgsd.getMark());// 金额

		}
		return mapItem;
	}

	// 按钮设置
	private void setupButtons() {
		btn_add = (Button) findViewById(R.id.estimate_addlist);
		btn_save = (Button) findViewById(R.id.investment_estimate_add_save);
		mRemarkContent = (EditText) findViewById(R.id.remark_content_edit);

		if (mOperation == OPERATION_DETAIL) {
			mRemarkContent.setEnabled(false);
			mEstimateNameEditText.setEnabled(false);
			mEstimateYearEditText.setEnabled(false);
			btn_add.setVisibility(View.GONE);
			btn_save.setVisibility(View.GONE);
		} else {
			// mCreateTextView.setOnClickListener(new OnClickListener() {
			//
			// @Override
			// public void onClick(View v) {
			// // TODO Auto-generated method stub
			// Intent intent = new Intent();
			// intent.setClass(v.getContext(), OwnerSelectActivity.class);
			// intent.putExtra("title", getString(R.string.operator));
			// startActivityForResult(intent, OPERATOR_SELECT_CODE);
			// }
			// });

			btn_add.setOnClickListener(new OnClickListener() {

				@Override
				public void onClick(View v) {
					AddInvestmentEstimateD();
				}
			});

			btn_save.setOnClickListener(new OnClickListener() {

				@Override
				public void onClick(View view) {
					if (mOperation == OPERATION_ADD) {
						// 增加租赁入单
						double total_money = 0.0;
						for (InvestmentEstimateD zrld : mShowInvestmentEstimateDDataList) {
							total_money += zrld.getSum();
						}
						mInvestmentEstimate
								.setEstimate_name(mEstimateNameEditText
										.getText().toString());
						mInvestmentEstimate
								.setEstimate_period(mEstimateYearEditText
										.getText().toString());
						mInvestmentEstimate.setMark(mRemarkContent.getText()
								.toString());
						mInvestmentEstimate.setEstimate_money(total_money);
						mInvestmentEstimate.setItem(mUpdatingDataList.size());
						if (!checkDataList(mUpdatingDataList))
							return;
						RemoteInvestmentEstimateService.getInstance()
								.addInvestmentEstimate(
										new DataManagerInterface() {

											@Override
											public void getDataOnResult(
													ResultStatus status,
													List<?> list) {
											}
										}, mInvestmentEstimate,
										mUpdatingDataList);
						Intent mIntent = new Intent();
						setResult(RESULT_OK, mIntent);
						finish();

					} else if (mOperation == OPERATION_MODIFY) {
						// 更新租赁入单
						double total_money = 0.0;
						for (InvestmentEstimateD zrld : mShowInvestmentEstimateDDataList) {
							total_money += zrld.getSum();
						}
						mInvestmentEstimate
								.setEstimate_name(mEstimateNameEditText
										.getText().toString());
						mInvestmentEstimate
								.setEstimate_period(mEstimateYearEditText
										.getText().toString());
						mInvestmentEstimate.setMark(mRemarkContent.getText()
								.toString());
						mInvestmentEstimate.setEstimate_money(total_money);
						mInvestmentEstimate
								.setItem(mShowInvestmentEstimateDDataList
										.size());
						if (!checkDataList(mUpdatingDataList))
							return;
						RemoteInvestmentEstimateService.getInstance()
								.updateInvestmentEstimate(
										new DataManagerInterface() {

											public void getDataOnResult(
													ResultStatus status,
													List<?> list) {
											}
										}, mInvestmentEstimate,
										mUpdatingDataList);

						if (mInvestmentEstimate.getInvestment_estimate_id() != 0
								&& mShowInvestmentEstimateDDataList.size() == 0) {
							RemoteInvestmentEstimateService
									.getInstance()
									.deleteInvestmentEstimate(
											new DataManagerInterface() {

												@Override
												public void getDataOnResult(
														ResultStatus status,
														List<?> list) {
												}
											},
											mInvestmentEstimate
													.getInvestment_estimate_id());
						}
						Intent mIntent = new Intent();
						setResult(RESULT_OK, mIntent);
						finish();
					}
				}
			});
		}
	}

	// 调用添加资源的返回。
	public void onActivityResult(int requestCode, int resultCode, Intent data) {
		if (resultCode == 0 || data == null)
			return;
		if (requestCode == OPERATOR_SELECT_CODE) {
			User user = (User) data.getSerializableExtra("user");
			if (user != null) {
				mCreateTextView.setText(user.getName());
				mInvestmentEstimate.setCreater(user.getUser_id());
			}
		}
		super.onActivityResult(requestCode, resultCode, data);

	}

	@SuppressLint("UseSparseArrays") 
	private void initListViewDialog() {
		mListViewDialog = new BaseDialog(this,
				getString(R.string.modify_estimate_list));
		Map<Integer, Integer> buttons = new HashMap<Integer, Integer>();
		Map<Integer, String[]> widgetContent = new HashMap<Integer, String[]>();

		buttons.put(1, BaseDialog.decimalEditTextLineStyle);
		buttons.put(2, BaseDialog.decimalEditTextLineStyle);
		buttons.put(3, BaseDialog.decimalEditTextLineStyle);
		buttons.put(4, BaseDialog.spinnerLineStyle);
		buttons.put(5, BaseDialog.decimalEditTextLineStyle);

		String[] unit = new String[GLOBAL.UNIT_TYPE.length];
		for (int i = 0; i < GLOBAL.UNIT_TYPE.length; i++) {
			unit[i] = GLOBAL.UNIT_TYPE[i][1];
		}
		widgetContent.put(4, unit);

		mListViewDialog.init(R.array.estimate_add_dialog, buttons,
				widgetContent);

		mListDialogNames = getResources().getStringArray(
				R.array.estimate_add_dialog);
	}

	private void mListViewDialogMode(int mode) {
		Button saveImageView = (Button) mListViewDialog.getPopupView()
				.findViewById(R.id.save_Button);
		if (mode == 0) {
			saveImageView.setOnClickListener(new OnClickListener() {

				@Override
				public void onClick(View v) {
					Map<String, String> items = mListViewDialog.SaveData();
					for (Map.Entry<String, String> entry : items.entrySet()) {
						if (entry.getValue().equals("")) {
							Toast.makeText(getApplicationContext(),
									R.string.pls_input_all_info,
									Toast.LENGTH_LONG).show();
							return;
						}
					}
					InvestmentEstimateD bean = new InvestmentEstimateD();
					int count = 0;
					bean.setCost_name(items.get(mListDialogNames[count++]));// 费用名称

					double constructionCost = Double.parseDouble(items
							.get(mListDialogNames[count++]));// 建筑费用
					bean.setConstruction_cost(constructionCost);

					double installCost = Double.parseDouble(items
							.get(mListDialogNames[count++]));// 安装工程费
					bean.setInstall_cost(installCost);

					double otherCost = Double.parseDouble(items
							.get(mListDialogNames[count++]));// 其它费用
					bean.setOther_cost(otherCost);

					double sumCost = constructionCost + installCost + otherCost;
					bean.setSum(sumCost);

					bean.setUnit(items.get(mListDialogNames[count++]));// 单位
					bean.setAmount(Double.parseDouble(items
							.get(mListDialogNames[count++])));// 数量
					bean.setMark(items.get(mListDialogNames[count++]));// 备注
					bean.setInvestment_estimate_id(mInvestmentEstimate
							.getInvestment_estimate_id());
					bean.setIDU(GLOBAL.IDU_INSERT); // 插入
					mShowInvestmentEstimateDDataList.add(bean);
					mUpdatingDataList.add(bean);
					mListAdapter.notifyDataSetChanged();
					mListViewDialog.dismiss();
					showFootingMoney();
				}
			});

		} else if (mode == 1) {
			saveImageView.setOnClickListener(new OnClickListener() {

				@Override
				public void onClick(View v) {
					Map<String, String> items = mListViewDialog.SaveData();
					for (Map.Entry<String, String> entry : items.entrySet()) {
						if (entry.getValue().equals("")) {
							Toast.makeText(getApplicationContext(),
									R.string.pls_input_all_info,
									Toast.LENGTH_LONG).show();
							return;
						}
					}

					int count = 0;
					mCurrentItem.setCost_name(items
							.get(mListDialogNames[count++]));

					double constructionCost = Double.parseDouble(items
							.get(mListDialogNames[count++]));
					mCurrentItem.setConstruction_cost(constructionCost);

					double installCost = Double.parseDouble(items
							.get(mListDialogNames[count++]));
					mCurrentItem.setInstall_cost(installCost);

					double otherCost = Double.parseDouble(items
							.get(mListDialogNames[count++]));
					mCurrentItem.setOther_cost(otherCost);

					double sumCost = constructionCost + installCost + otherCost;
					mCurrentItem.setSum(sumCost);
					mCurrentItem.setUnit(items.get(mListDialogNames[count++]));
					mCurrentItem.setAmount(Double.parseDouble(items
							.get(mListDialogNames[count++])));
					mCurrentItem.setMark(items.get(mListDialogNames[count++]));
					if (mCurrentItem.getInvestment_estimate_d_id() != 0)
						mCurrentItem.setIDU(GLOBAL.IDU_UPDATE);
					updateInvestmentEstimateDList(mCurrentItem);
					mListAdapter.updateData(mCurrentItem);
					mListViewDialog.dismiss();
					showFootingMoney();
				}
			});
		}
	}

	private void showFootingMoney() {
		List<InvestmentEstimateD> showList = mListAdapter.getDataShowList();
		double footing = 0;
		for (InvestmentEstimateD item : showList) {
			footing += item.getSum();
		}
		mFootingTextView.setText(UtilTools.formatMoney("¥", footing, 2));
		// mSumTextView.setText(UtilTools.formatMoney("¥", footing, 2));
	}

	private boolean checkDataList(List<InvestmentEstimateD> lists) {
		for (InvestmentEstimateD tzgsd : lists) {
			if (tzgsd.getCost_name() != null && !tzgsd.getCost_name().isEmpty()
					&& tzgsd.getConstruction_cost() > 0
					&& tzgsd.getInstall_cost() > 0 && tzgsd.getOther_cost() > 0
					&& tzgsd.getSum() > 0 && tzgsd.getAmount() > 0
					&& mInvestmentEstimate.getEstimate_number() != null
					&& !mInvestmentEstimate.getEstimate_number().isEmpty()
					&& mInvestmentEstimate.getEstimate_name() != null
					&& !mInvestmentEstimate.getEstimate_name().isEmpty()
					&& mInvestmentEstimate.getEstimate_period() != null
					&& !mInvestmentEstimate.getEstimate_period().isEmpty()
					&& mInvestmentEstimate.getCreater() > 0)
				return true;
		}
		Toast.makeText(this, getString(R.string.incomplete_information),
				Toast.LENGTH_SHORT).show();
		return false;
	}

	@SuppressLint("HandlerLeak")
	private Handler mToastHandler = new Handler() {
		public void handleMessage(Message msg) {
			Toast.makeText(EstimateAddActivity.this, (CharSequence) msg.obj,
					Toast.LENGTH_SHORT).show();
		}
	};
}