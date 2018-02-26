package com.pm360.cepm360.app.module.lease;

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
import com.pm360.cepm360.app.CepmApplication;
import com.pm360.cepm360.app.cache.ProjectCache;
import com.pm360.cepm360.app.cache.UserCache;
import com.pm360.cepm360.app.common.activity.ActionBarFragmentActivity;
import com.pm360.cepm360.app.common.activity.DataManagerInterface;
import com.pm360.cepm360.app.common.activity.ListSelectActivity;
import com.pm360.cepm360.app.common.activity.OwnerSelectActivity;
import com.pm360.cepm360.app.common.adpater.DataListAdapter;
import com.pm360.cepm360.app.common.adpater.DataListAdapter.ViewHolder;
import com.pm360.cepm360.app.common.view.OptionsMenuView;
import com.pm360.cepm360.app.common.view.parent.BaseDialog;
import com.pm360.cepm360.app.common.view.parent.DoubleDatePickerDialog;
import com.pm360.cepm360.app.module.contract.LeaseContractWithoutTreeFragment;
import com.pm360.cepm360.app.module.inventory.StoreHouseSelectActivity;
import com.pm360.cepm360.app.module.resource.ContactCompanyFragment;
import com.pm360.cepm360.app.module.resource.LeaseManagementFragment;
import com.pm360.cepm360.app.utils.UtilTools;
import com.pm360.cepm360.common.GLOBAL;
import com.pm360.cepm360.common.ResultStatus;
import com.pm360.cepm360.common.json.AnalysisManager;
import com.pm360.cepm360.common.util.DateUtils;
import com.pm360.cepm360.entity.Contract;
import com.pm360.cepm360.entity.P_LWDW;
import com.pm360.cepm360.entity.P_ZL;
import com.pm360.cepm360.entity.P_ZLR;
import com.pm360.cepm360.entity.P_ZLRD;
import com.pm360.cepm360.entity.Project;
import com.pm360.cepm360.entity.User;
import com.pm360.cepm360.services.common.RemoteCommonService;
import com.pm360.cepm360.services.lease.RemoteZLRService;

import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class LeaseListAddActivity1 extends ActionBarFragmentActivity implements
		DataListAdapter.ListAdapterInterface<P_ZLRD> {
	public final static String MSG_SUBCONTRACT_MANAGEMENT_ACTION = "com.pm360.cepm360.action.lease_manager";
	// 列表头显示名称
	private View mListHeaderView;
	private View mFootingRemark;
	private TextView mFootingTextView;
	private String[] mListHeaderNames;
	private int[] mDisplayItemIds;
	private P_ZLRD mCurrentItem;
	private EditText exe_contractET;
	private Button btn_add;
	private Button btn_save;
	private EditText mRemarkContent;
	private OptionsMenuView mOptionsMenuView;
	private TextView mLeaseNumberTextView;
	private TextView mTotalTextView;

	private TextView mLeaseProjectTextView;
	private TextView mContractTextView;
	private TextView mLeaseCompanyTextView;
	private TextView mOperatorTextView;
	private TextView mStorehouseTextView;
	private TextView mWarehouseKeeperTextView;

	private ListView mZLRDListView;
	private List<P_ZLRD> mShowZLRDDataList = new ArrayList<P_ZLRD>();
	private List<P_ZLRD> mUpdatingDataList = new ArrayList<P_ZLRD>();
	private int mOperation;
	private P_ZLR mCurrentZLRBean;
	private static final int OPERATION_DETAIL = 10;
	private static final int OPERATION_ADD = 11;
	private static final int OPERATION_MODIFY = 12;
	private static final int DATA_CHANGED = 100;
	private DataListAdapter<P_ZLRD> mListAdapter;
	private Project mSelectProject;
	private P_LWDW mCurrentProvider = new P_LWDW();
	@SuppressWarnings("unused")
	private CepmApplication mApp;

	private BaseDialog mListViewDialog;
	private String[] mListDialogNames;

	private static final int ADD_ZLRD_REQUEST = 222;
	private static final int SELECT_SUBCONTRACT = 333;
	private int PROJECT_SELECT_CODE = 200;
	private int OPERATOR_SELECT_CODE = 204;
	private int PROVIDER_SELECT_CODE = 205;
	private int KEEPER_SELECT_CODE = 207;
	private static final int STOREHOUSE_REQUEST_CODE = 500;

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
		setContentView(R.layout.lease_add);

		initWindow();
	}

	private void initWindow() {
//		mSelectProject = ProjectCache.getCurrentProject();
//		if (mSelectProject != null) {
//			setActionBarTitle(mSelectProject.getName());
//		}
		mFootingRemark = findViewById(R.id.lease_manager_add_bottom);
		mFootingTextView = (TextView) mFootingRemark
				.findViewById(R.id.total_content);
		mLeaseNumberTextView = (TextView) findViewById(R.id.lease_number);
		mLeaseNumberTextView.setBackground(getResources().getDrawable(
				R.drawable.bg_edittext));
		mLeaseProjectTextView = (TextView) findViewById(R.id.lease_project);
		mLeaseProjectTextView.setBackground(getResources().getDrawable(
				R.drawable.bg_edittext));
		mContractTextView = (TextView) findViewById(R.id.lease_contract);
		mLeaseCompanyTextView = (TextView) findViewById(R.id.lease_company);
		mOperatorTextView = (TextView) findViewById(R.id.creater);
		mStorehouseTextView = (TextView) findViewById(R.id.storehouse);
		mWarehouseKeeperTextView = (TextView) findViewById(R.id.warehouse_keeper);

		mTotalTextView = (TextView) this.findViewById(R.id.total_content);
		mRemarkContent = (EditText) findViewById(R.id.remark_content_edit);
		mZLRDListView = (ListView) this.findViewById(R.id.listView);
		mListHeaderView = this.findViewById(R.id.listHeaderView);
		mListHeaderNames = getApplicationContext().getResources()
				.getStringArray(R.array.lease_header_title_names);

		TypedArray titleType = getApplicationContext().getResources()
				.obtainTypedArray(R.array.lease_header_title_ids);

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

		mListAdapter = new DataListAdapter<P_ZLRD>(this, this,
				mShowZLRDDataList);
		if (mOperation == OPERATION_DETAIL || mOperation == OPERATION_MODIFY) { // 查看详细&修改
			mLeaseNumberTextView.setText(mCurrentZLRBean.getZlr_number());
			mLeaseProjectTextView
					.setText(ProjectCache
							.findProjectById(mCurrentZLRBean.getProject_id())
							.getName());
			mLeaseCompanyTextView.setText(mCurrentZLRBean.getZl_company_name());
			mOperatorTextView.setText(UserCache
					.findUserById(mCurrentZLRBean.getOperator()).getName());
			mStorehouseTextView.setText(mCurrentZLRBean.getStorehouse());
			mWarehouseKeeperTextView.setText(UserCache
					.findUserById(mCurrentZLRBean.getStoreman()).getName());

			mTotalTextView.setText(UtilTools.formatMoney("¥",
					mCurrentZLRBean.getTotal(), 2));
			mContractTextView.setText(mCurrentZLRBean.getZlht_name());
			mRemarkContent.setText(mCurrentZLRBean.getMark());
			loaderZLRDData(mCurrentZLRBean);
			//mSelectProject = new Project();
			// mSelectProject.setName(mCurrentZLRBean.getProject_name());
			//mSelectProject.setProject_id(mCurrentZLRBean.getProject_id());
		} else {// 添加
			mShowZLRDDataList.clear();
			mUpdatingDataList.clear();
			mCurrentZLRBean.setProject_id(mSelectProject.getProject_id());
			mCurrentZLRBean.setProject_name(mSelectProject.getName());
			mLeaseProjectTextView.setText(mSelectProject.getName());
			RemoteCommonService.getInstance().getCodeByDate(ZLRCodeManager,
					"ZLR");
		}

		setupButtons();
		initListViewDialog();
		if (mOperation != OPERATION_DETAIL) {
			initOptionsMenuView();
		}else {
			mLeaseProjectTextView.setBackground(getResources().getDrawable(
					R.drawable.bg_edittext));
			mLeaseProjectTextView.setGravity(Gravity.CENTER_VERTICAL);
			mLeaseProjectTextView.setTextSize(TypedValue.COMPLEX_UNIT_PX, getResources().getDimensionPixelSize(R.dimen.sp16_s));
			int pxw = UtilTools.dp2pxW(getBaseContext(), 8);
			int pxh = UtilTools.dp2pxH(getBaseContext(), 8);
			mLeaseProjectTextView.setPadding(pxw, pxh, pxw, pxh);
			
			mContractTextView.setBackground(getResources().getDrawable(
					R.drawable.bg_edittext));
			mContractTextView.setGravity(Gravity.CENTER_VERTICAL);
			mContractTextView.setTextSize(TypedValue.COMPLEX_UNIT_PX, getResources().getDimensionPixelSize(R.dimen.sp16_s));
			mContractTextView.setPadding(pxw, pxh, pxw, pxh);
			
			mLeaseCompanyTextView.setBackground(getResources().getDrawable(
					R.drawable.bg_edittext));
			mLeaseCompanyTextView.setGravity(Gravity.CENTER_VERTICAL);
			mLeaseCompanyTextView.setTextSize(TypedValue.COMPLEX_UNIT_PX, getResources().getDimensionPixelSize(R.dimen.sp16_s));
			mLeaseCompanyTextView.setPadding(pxw, pxh, pxw, pxh);
			
			mOperatorTextView.setBackground(getResources().getDrawable(
					R.drawable.bg_edittext));
			mOperatorTextView.setGravity(Gravity.CENTER_VERTICAL);
			mOperatorTextView.setTextSize(TypedValue.COMPLEX_UNIT_PX, getResources().getDimensionPixelSize(R.dimen.sp16_s));
			mOperatorTextView.setPadding(pxw, pxh, pxw, pxh);
			
			mStorehouseTextView.setBackground(getResources().getDrawable(
					R.drawable.bg_edittext));
			mStorehouseTextView.setGravity(Gravity.CENTER_VERTICAL);
			mStorehouseTextView.setTextSize(TypedValue.COMPLEX_UNIT_PX, getResources().getDimensionPixelSize(R.dimen.sp16_s));
			mStorehouseTextView.setPadding(pxw, pxh, pxw, pxh);
			
			mWarehouseKeeperTextView.setBackground(getResources().getDrawable(
					R.drawable.bg_edittext));
			mWarehouseKeeperTextView.setGravity(Gravity.CENTER_VERTICAL);
			mWarehouseKeeperTextView.setTextSize(TypedValue.COMPLEX_UNIT_PX, getResources().getDimensionPixelSize(R.dimen.sp16_s));
			mWarehouseKeeperTextView.setPadding(pxw, pxh, pxw, pxh);
		}

		mZLRDListView.setAdapter(mListAdapter);

	}

	private void msgHandlerProgress() {
		Intent intent = getIntent();
		mOperation = (Integer) intent.getSerializableExtra("operation");
		mCurrentZLRBean = (P_ZLR) intent.getSerializableExtra("data");
		mSelectProject = (Project) intent.getSerializableExtra("project");
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
							modifyZLRD();
							break;
						case 1: // 删除
							commonConfirmDeleteZLRD();
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
			mLeaseNumberTextView.setText(code);
			if (mOperation == OPERATION_ADD) {
				mCurrentZLRBean.setTenant_id(UserCache
						.getCurrentUser().getTenant_id());
				mCurrentZLRBean.setProject_id(mSelectProject.getProject_id());
				mCurrentZLRBean.setProject_name(mSelectProject.getName());
				mCurrentZLRBean.setZlr_number(code);
			}
		}
	};

	private DataManagerInterface ZLRCodeManager = new DataManagerInterface() {
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

	private void loaderZLRDData(P_ZLR zlr) {
		RemoteZLRService.getInstance().getZLRD(mManager, zlr.getZlr_id());
	}

	private DataManagerInterface mManager = new DataManagerInterface() {

		public void getDataOnResult(ResultStatus status, List<?> list) {
			switch (status.getCode()) {
			case AnalysisManager.SUCCESS_DB_QUERY:
				if (list != null && list.size() > 0) {
					mShowZLRDDataList.clear();
					mUpdatingDataList.clear();
					for (Object object : list) {
						if (object instanceof P_ZLRD) {
							mShowZLRDDataList.add((P_ZLRD) object);
							mUpdatingDataList.add((P_ZLRD) object);
						}
					}
					mHandler.sendEmptyMessage(DATA_CHANGED);
				}
			default:
				break;
			}
		}
	};

	private void AddZLRD() {
		Intent intent = new Intent(this, ListSelectActivity.class);
		Bundle bundle = new Bundle();
		bundle.putSerializable(ListSelectActivity.FRAGMENT_KEY,
				LeaseManagementFragment.class);
		bundle.putBoolean(ListSelectActivity.SELECT_MODE_KEY,
				ListSelectActivity.MULTI_SELECT);
		ArrayList<P_ZL> list = new ArrayList<P_ZL>();
		for (P_ZLRD rgd : mShowZLRDDataList) {
			P_ZL nr = new P_ZL();
			nr.setZl_id(rgd.getZl_id());
			list.add(nr);
		}
		bundle.putSerializable(ListSelectActivity.FILTER_DATA_KEY, list);
		intent.putExtras(bundle);
		startActivityForResult(intent, ADD_ZLRD_REQUEST);
	}

	private void modifyZLRD() {
		String[] editTexts = new String[4];
		if (mCurrentItem.getNumber() != 0) {
			editTexts[0] = Double.toString(mCurrentItem.getNumber());
		}
		if (mCurrentItem.getRent() != 0) {
			editTexts[1] = Double.toString(mCurrentItem.getRent());
		}
		editTexts[2] = DateUtils.dateToString(DateUtils.FORMAT_SHORT,
				mCurrentItem.getLease_date());
		editTexts[3] = DateUtils.dateToString(DateUtils.FORMAT_SHORT,
				mCurrentItem.getEnd_date());
		mListViewDialog.show(editTexts);
	}

	private void commonConfirmDeleteZLRD() {
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
								deleteZLRD();
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
	private void deleteZLRD() {
		mCurrentItem.setIDU(GLOBAL.IDU_DELETE);
		if (mCurrentItem.getZlrd_id() == 0)
			mUpdatingDataList.remove(mCurrentItem);
		mShowZLRDDataList.remove(mCurrentItem);
		mListAdapter.notifyDataSetChanged();
		double total_money = 0.0;
		for (P_ZLRD zlrd : mShowZLRDDataList) {
			total_money += zlrd.getSum();
		}
		mTotalTextView.setText(UtilTools.formatMoney("¥", total_money, 2));
	}

	/**
	 * 数据更新
	 * 
	 * @param bean
	 */
	@SuppressLint("HandlerLeak")
	private void updateZLRDList(P_ZLRD bean) {
		if (bean.getZlrd_id() != 0) {
			bean.setIDU(GLOBAL.IDU_UPDATE);
		}

		for (int i = 0; i < mUpdatingDataList.size(); i++) {
			if (mUpdatingDataList.get(i).getZlrd_id() == bean.getZlrd_id()
					&& mUpdatingDataList.get(i).getZl_type() == bean
							.getZl_type()) {
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
		return R.layout.lease_add_list_item_title;
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
			if ((i == 4 || i == 6 || i == 7 || i == 8)) {
				viewHolder.tvs[i]
						.setOnClickListener(new View.OnClickListener() {
							@Override
							public void onClick(View view) {
								mListAdapter.setSelected(position, true);
								mCurrentItem = mListAdapter.getItem(position);
								modifyZLRD();
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
			DataListAdapter<P_ZLRD> adapter, int position) {
		Map<String, String> listViewItem = beanToMap(position,
				adapter.getItem(position));
		for (int i = 0; i < mListHeaderNames.length; i++) {
			if ((mOperation != OPERATION_DETAIL)
					&& (i == 4 || i == 6 || i == 7 || i == 8)) {
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
            	convertView.setBackgroundResource(R.color.content_listview_single_line_bg);
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
//			if (i == 6 || i == 9 || i == 10) {
//				holder.tvs[i].setGravity(Gravity.CENTER | Gravity.LEFT);
//				holder.tvs[i].setPadding(5, 0, 0, 0);
//			} else {
//				holder.tvs[i].setGravity(Gravity.CENTER | Gravity.LEFT);
//				holder.tvs[i].setPadding(10, 0, 0, 0);
//			}
		}
	}

	public List<P_ZLRD> findByConditioonCreaten(Object... condition) {
		return null;
	}

	@Override
	public boolean isSameObject(P_ZLRD t1, P_ZLRD t2) {
		return false;
	}

	@Override
	public List<P_ZLRD> findByCondition(Object... condition) {
		return null;
	}

	public Map<String, String> beanToMap(int position, Object bean) {
		Map<String, String> mapItem = new HashMap<String, String>();
		if (bean instanceof P_ZLRD) {
			P_ZLRD zlrd = (P_ZLRD) bean;

			mapItem.put(mListHeaderNames[0], String.valueOf(position + 1));
			mapItem.put(mListHeaderNames[1],
					GLOBAL.SB_TYPE[Integer.valueOf(zlrd.getZl_type()) - 1][1]);//
			mapItem.put(mListHeaderNames[2], zlrd.getZl_name());//
			mapItem.put(mListHeaderNames[3], zlrd.getZl_spec());//
			mapItem.put(mListHeaderNames[4], String.valueOf(zlrd.getNumber()));// 数量
			mapItem.put(mListHeaderNames[5],
					GLOBAL.UNIT_TYPE[Integer.valueOf(zlrd.getZl_unit()) - 1][1]);//
			mapItem.put(mListHeaderNames[6],
					UtilTools.formatMoney("¥", zlrd.getRent(), 2));//
			mapItem.put(
					mListHeaderNames[7],
					DateUtils.dateToString(DateUtils.FORMAT_SHORT,
							zlrd.getLease_date()));//
			mapItem.put(
					mListHeaderNames[8],
					DateUtils.dateToString(DateUtils.FORMAT_SHORT,
							zlrd.getEnd_date()));
			mapItem.put(mListHeaderNames[9],
					UtilTools.formatMoney("¥", zlrd.getSum(), 2));// 金额

		}
		return mapItem;
	}

	// 按钮设置
	private void setupButtons() {
		exe_contractET = (EditText) findViewById(R.id.lease_contract);
		btn_add = (Button) findViewById(R.id.lease_addlist);
		btn_save = (Button) findViewById(R.id.lease_add_save);
		exe_contractET.setFocusableInTouchMode(false);
		exe_contractET.clearFocus();
		mRemarkContent = (EditText) findViewById(R.id.remark_content_edit);

		if (mOperation == OPERATION_DETAIL) {
			mRemarkContent.setEnabled(false);
			btn_add.setVisibility(View.GONE);
			btn_save.setVisibility(View.GONE);
		} else {
//			mLeaseProjectTextView.setOnClickListener(new OnClickListener() {
//
//				@Override
//				public void onClick(View v) {
//					// TODO Auto-generated method stub
//					Intent intent = new Intent(LeaseListAddActivity.this,
//							ProjectSelectActivity.class);
//					Bundle bundle = new Bundle();
//					bundle.putInt("action", ProjectSelectActivity.ACTION_PICK);
//					intent.putExtras(bundle);
//					startActivityForResult(intent, PROJECT_SELECT_CODE);
//				}
//			});

			exe_contractET.setOnClickListener(new OnClickListener() {

				@Override
				public void onClick(View v) {
					Intent intent = new Intent(v.getContext(), ListSelectActivity.class);					
					intent.putExtra(ListSelectActivity.PROJECT_KEY, mSelectProject);
					intent.putExtra(ListSelectActivity.SELECT_MODE_KEY,ListSelectActivity.SINGLE_SELECT);
					intent.putExtra(ListSelectActivity.FRAGMENT_KEY,
							LeaseContractWithoutTreeFragment.class);
					startActivityForResult(intent, SELECT_SUBCONTRACT);
				}
			});

			mLeaseCompanyTextView.setOnClickListener(new OnClickListener() {

				@Override
				public void onClick(View v) {
					// TODO Auto-generated method stub
					Intent data = new Intent(LeaseListAddActivity1.this,
							ListSelectActivity.class);
					Bundle bundle = new Bundle();
					bundle.putSerializable(ListSelectActivity.FRAGMENT_KEY,
							ContactCompanyFragment.class);
					data.putExtras(bundle);
					startActivityForResult(data, PROVIDER_SELECT_CODE);
				}
			});

			mOperatorTextView.setOnClickListener(new OnClickListener() {

				@Override
				public void onClick(View v) {
					// TODO Auto-generated method stub
					Intent intent = new Intent();
					intent.setClass(v.getContext(), OwnerSelectActivity.class);
					intent.putExtra("title", getString(R.string.operator));
					startActivityForResult(intent, OPERATOR_SELECT_CODE);
				}
			});

			mStorehouseTextView.setOnClickListener(new OnClickListener() {

				@Override
				public void onClick(View v) {
					// TODO Auto-generated method stub
					if (mSelectProject.getProject_id() == 0) {
						return;
					}
					Intent intent = new Intent(LeaseListAddActivity1.this,
							StoreHouseSelectActivity.class);
					intent.putExtra(StoreHouseSelectActivity.PROJECT_ID,
							mSelectProject.getProject_id());
					startActivityForResult(intent, STOREHOUSE_REQUEST_CODE);
				}
			});

			mWarehouseKeeperTextView.setOnClickListener(new OnClickListener() {

				@Override
				public void onClick(View v) {
					// TODO Auto-generated method stub
					Intent intent = new Intent();
					intent.setClass(v.getContext(), OwnerSelectActivity.class);
					intent.putExtra("title", getString(R.string.storekeeper));
					startActivityForResult(intent, KEEPER_SELECT_CODE);
				}
			});

			btn_add.setOnClickListener(new OnClickListener() {

				@Override
				public void onClick(View v) {
					AddZLRD();
				}
			});

			btn_save.setOnClickListener(new View.OnClickListener() {

				@Override
				public void onClick(View view) {
					if (mOperation == OPERATION_ADD) {
						if (!checkDataList(mUpdatingDataList))
							return;
						// 增加租赁入单
						double total_money = 0.0;
						for (P_ZLRD zrld : mShowZLRDDataList) {
							total_money += zrld.getSum();
						}
						mCurrentZLRBean.setMark(mRemarkContent.getText()
								.toString());
						mCurrentZLRBean.setTotal(total_money);
						mCurrentZLRBean.setItem(mUpdatingDataList.size());
						RemoteZLRService.getInstance().addZLR(
								new DataManagerInterface() {

									@Override
									public void getDataOnResult(
											ResultStatus status, List<?> list) {
									}
								}, mCurrentZLRBean, mUpdatingDataList);
						Intent mIntent = new Intent();
						// mIntent.putExtras(bundle);
						setResult(RESULT_OK, mIntent);
						finish();

					} else if (mOperation == OPERATION_MODIFY) {
						// 更新租赁入单
						double total_money = 0.0;
						for (P_ZLRD zrld : mShowZLRDDataList) {
							total_money += zrld.getSum();
						}
						mCurrentZLRBean.setMark(mRemarkContent.getText()
								.toString());
						mCurrentZLRBean.setTotal(total_money);
						mCurrentZLRBean.setItem(mShowZLRDDataList.size());
						RemoteZLRService.getInstance().updateZLR(
								new DataManagerInterface() {

									public void getDataOnResult(
											ResultStatus status, List<?> list) {
									}
								}, mCurrentZLRBean, mUpdatingDataList);

						if (mCurrentZLRBean.getZlr_id() != 0
								&& mShowZLRDDataList.size() == 0) {
							RemoteZLRService.getInstance().deleteZLR(
									new DataManagerInterface() {

										@Override
										public void getDataOnResult(
												ResultStatus status,
												List<?> list) {
										}
									}, mCurrentZLRBean.getZlr_id());
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
						if (mCurrentZLRBean.getZlr_id() != 0) {
							bean.setZlr_id(mCurrentZLRBean.getZlr_id());
						}
						bean.setTenant_id(UserCache
								.getCurrentUser().getTenant_id());
						bean.setZl_id(zl.getZl_id());
						bean.setZl_type(zl.getZl_type());
						bean.setZl_name(zl.getName());
						bean.setZl_spec(zl.getSpec());
						bean.setZl_unit(zl.getUnit());
						bean.setZlr_number(mCurrentZLRBean.getZlr_number());
						bean.setIDU(GLOBAL.IDU_INSERT); // 插入
						mShowZLRDDataList.add(bean);
						mUpdatingDataList.add(bean);
					}
					mListAdapter.notifyDataSetChanged();
				}
			}
		} else if (requestCode == SELECT_SUBCONTRACT) {
			Contract contract = (Contract) data
					.getSerializableExtra(ListSelectActivity.RESULT_KEY);
			if (contract != null) {
				exe_contractET.setText(contract.getName());
				mCurrentZLRBean.setContract_id(contract.getContract_id());
				mCurrentZLRBean.setZlht_name(contract.getName());
			}
		} else if (requestCode == OPERATOR_SELECT_CODE) {
			User user = (User) data.getSerializableExtra("user");
			if (user != null) {
				mOperatorTextView.setText(user.getName());
				mCurrentZLRBean.setOperator(user.getUser_id());
			}
		} else if (requestCode == KEEPER_SELECT_CODE) {
			User user = (User) data.getSerializableExtra("user");
			if (user != null) {
				mWarehouseKeeperTextView.setText(user.getName());
				mCurrentZLRBean.setStoreman(user.getUser_id());
			}
		} else if (requestCode == PROJECT_SELECT_CODE) {
			mSelectProject = (Project) data.getSerializableExtra("project");
			mLeaseProjectTextView.setText(mSelectProject.getName());
			mCurrentZLRBean.setProject_id(mSelectProject.getProject_id());
			mCurrentZLRBean.setProject_name(mSelectProject.getName());
		} else if (requestCode == PROVIDER_SELECT_CODE) {
			mCurrentProvider = (P_LWDW) data
					.getSerializableExtra(ListSelectActivity.RESULT_KEY);
			mLeaseCompanyTextView.setText(mCurrentProvider.getName());
			mCurrentZLRBean.setZl_company(mCurrentProvider.getLwdw_id());
			mCurrentZLRBean.setZl_company_name(mCurrentProvider.getName());
		} else if (requestCode == STOREHOUSE_REQUEST_CODE) {
			String storeHouse = data
					.getStringExtra(StoreHouseSelectActivity.RESULT_KEY);
			mStorehouseTextView.setText(storeHouse);
			mCurrentZLRBean.setStorehouse(storeHouse);
		}

		super.onActivityResult(requestCode, resultCode, data);
	}

	@SuppressLint("UseSparseArrays")
	private void initListViewDialog() {
		mListViewDialog = new BaseDialog(this,
				getString(R.string.modify_lease_list));
		Map<Integer, Integer> buttons = new HashMap<Integer, Integer>();
		buttons.put(0, BaseDialog.decimalEditTextLineStyle);
		buttons.put(1, BaseDialog.decimalEditTextLineStyle);
		buttons.put(2, BaseDialog.calendarLineStyle);
		buttons.put(3, BaseDialog.calendarLineStyle);

		mListViewDialog.init(R.array.lease_add_dialog, buttons, null);
		final EditText startEt = (EditText) mListViewDialog.getEditTextView(2);
		final EditText endEt = (EditText) mListViewDialog.getEditTextView(3);

		final DoubleDatePickerDialog doubleDatePickerDialog = new DoubleDatePickerDialog(
				this, null, startEt, endEt, null);

		OnClickListener listener = new OnClickListener() {

			@Override
			public void onClick(View v) {
				doubleDatePickerDialog.show(mCurrentItem.getLease_date(),
						mCurrentItem.getEnd_date());
			}
		};
		mListViewDialog.setEditTextStyle(2, 0, listener, null);
		mListViewDialog.setEditTextStyle(3, 0, listener, null);

		mListDialogNames = getResources().getStringArray(
				R.array.lease_add_dialog);

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

				Date lease_date = DateUtils.stringToDate(
						DateUtils.FORMAT_SHORT,
						items.get(mListDialogNames[count++]));
				Date end_date = DateUtils.stringToDate(DateUtils.FORMAT_SHORT,
						items.get(mListDialogNames[count++]));
				double money = quantity * unitPrice * DateUtils.calculateDuration(lease_date, end_date);

				mCurrentItem.setNumber(quantity);
				mCurrentItem.setRemainder_number(quantity);
				mCurrentItem.setRent(unitPrice);
				mCurrentItem.setSum(money);
				mCurrentItem.setLease_date(lease_date);
				mCurrentItem.setEnd_date(end_date);
				if (mCurrentItem.getZlrd_id() != 0)
					mCurrentItem.setIDU(GLOBAL.IDU_UPDATE);
				updateZLRDList(mCurrentItem);
				mListAdapter.updateData(mCurrentItem);
				mListViewDialog.dismiss();
				showFootingMoney();
			}
		});

	}

	private void showFootingMoney() {
		List<P_ZLRD> showList = mListAdapter.getDataShowList();
		double footing = 0;
		for (P_ZLRD item : showList) {
			footing += item.getSum();
		}
		mFootingTextView.setText(UtilTools.formatMoney("¥", footing, 2));
	}

	private boolean checkDataList(List<P_ZLRD> lists) {
		for (P_ZLRD zlrd : lists) {
			if (zlrd.getNumber() > 0 && zlrd.getRent() > 0
					&& zlrd.getEnd_date() != null
					&& zlrd.getLease_date() != null
					&& mCurrentZLRBean.getProject_id() > 0
					//&& mCurrentZLRBean.getContract_id() > 0
					&& mCurrentZLRBean.getStoreman() > 0
					&& mCurrentZLRBean.getOperator() > 0
					&& mCurrentZLRBean.getStorehouse() != null
					&& !mCurrentZLRBean.getStorehouse().equals("")
					&& mCurrentZLRBean.getZl_company() > 0)
				return true;
		}
		Toast.makeText(this, getString(R.string.incomplete_information),
				Toast.LENGTH_SHORT).show();
		return false;
	}

	@SuppressLint("HandlerLeak")
	private Handler mToastHandler = new Handler() {
		public void handleMessage(Message msg) {
			Toast.makeText(LeaseListAddActivity1.this, (CharSequence) msg.obj,
					Toast.LENGTH_SHORT).show();
		}
	};

}
