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
import com.pm360.cepm360.app.cache.UserCache;
import com.pm360.cepm360.app.common.activity.ActionBarFragmentActivity;
import com.pm360.cepm360.app.common.activity.DataManagerInterface;
import com.pm360.cepm360.app.common.activity.ListSelectActivity;
import com.pm360.cepm360.app.common.activity.OwnerSelectActivity;
import com.pm360.cepm360.app.common.adpater.DataListAdapter;
import com.pm360.cepm360.app.common.adpater.DataListAdapter.ViewHolder;
import com.pm360.cepm360.app.common.view.OptionsMenuView;
import com.pm360.cepm360.app.common.view.parent.BaseDialog;
import com.pm360.cepm360.app.utils.UtilTools;
import com.pm360.cepm360.common.GLOBAL;
import com.pm360.cepm360.common.ResultStatus;
import com.pm360.cepm360.common.json.AnalysisManager;
import com.pm360.cepm360.common.util.DateUtils;
import com.pm360.cepm360.entity.P_ZLH;
import com.pm360.cepm360.entity.P_ZLHD;
import com.pm360.cepm360.entity.P_ZLRD;
import com.pm360.cepm360.entity.Project;
import com.pm360.cepm360.entity.User;
import com.pm360.cepm360.services.common.RemoteCommonService;
import com.pm360.cepm360.services.lease.RemoteZLHService;

import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class RentListAddActivity2 extends ActionBarFragmentActivity implements
		DataListAdapter.ListAdapterInterface<P_ZLHD> {
	public final static String MSG_SUBCONTRACT_MANAGEMENT_ACTION = "com.pm360.cepm360.action.rent_manager";
	// 列表头显示名称
	private View mListHeaderView;
	@SuppressWarnings("unused")
	private View mFootingRemark;
	private String[] mListHeaderNames;
	private int[] mDisplayItemIds;
	private P_ZLHD mCurrentItem;
	private Button btn_add;
	private Button btn_save;
	private EditText mRemarkContent;
	private OptionsMenuView mOptionsMenuView;
	private TextView mRentNumberTextView;
	private TextView mOperatorTextView;
	private ListView mZLHDListView;
	private List<P_ZLHD> mShowZLHDDataList = new ArrayList<P_ZLHD>();
	private List<P_ZLHD> mUpdatingDataList = new ArrayList<P_ZLHD>();
	private int mOperation;
	private P_ZLH mCurrentZLHBean;
	private static final int OPERATION_DETAIL = 10;
	private static final int OPERATION_ADD = 11;
	private static final int OPERATION_MODIFY = 12;
	private static final int DATA_CHANGED = 100;
	private DataListAdapter<P_ZLHD> mListAdapter;
	private Project mProject;
	@SuppressWarnings("unused")
	private CepmApplication mApp;

	private BaseDialog mListViewDialog;
	private String[] mListDialogNames;
	private EditText mHZPersonEditText;

	private static final int ADD_ZLHD_REQUEST = 222;
	private int OPERATOR_SELECT_CODE = 204;
	private int HZPERSON_SELECT_CODE = 205;

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
		setContentView(R.layout.rent_add);

		initWindow();
	}

	private void initWindow() {
//		mProject = ProjectCache.getCurrentProject();
//		if (mProject != null) {
//			setActionBarTitle(mProject.getName());
//		}
		mFootingRemark = findViewById(R.id.rent_manager_add_bottom);

		mRentNumberTextView = (TextView) this.findViewById(R.id.rent_number);
		mRentNumberTextView.setBackground(getResources().getDrawable(
				R.drawable.bg_edittext));
		mOperatorTextView = (TextView) this.findViewById(R.id.operator);
		mRemarkContent = (EditText) findViewById(R.id.remark_content_edit);
		mZLHDListView = (ListView) this.findViewById(R.id.listView);
		mListHeaderView = this.findViewById(R.id.listHeaderView);
		mListHeaderNames = getApplicationContext().getResources()
				.getStringArray(R.array.rent_header_title_names);
		btn_add = (Button) findViewById(R.id.rent_addlist);
		btn_save = (Button) findViewById(R.id.rent_add_save);
		TypedArray titleType = getApplicationContext().getResources()
				.obtainTypedArray(R.array.rent_header_title_ids);

		if (mListHeaderNames != null) {
			int itemLength = mListHeaderNames.length;
			mDisplayItemIds = new int[itemLength];
			for (int i = 0; i < itemLength; i++) {
				mDisplayItemIds[i] = titleType.getResourceId(i, 0);
				String text = "<b>" + mListHeaderNames[i] + "</b>";
				TextView tv = (TextView) findViewById(mDisplayItemIds[i]);
				tv.setText(Html.fromHtml(text));
				tv.setTextSize(TypedValue.COMPLEX_UNIT_PX, getResources()
						.getDimension(R.dimen.ticket_title_text_size));
			}
		}
		titleType.recycle();

		mListAdapter = new DataListAdapter<P_ZLHD>(this, this,
				mShowZLHDDataList);
		if (mOperation == OPERATION_DETAIL || mOperation == OPERATION_MODIFY) { // 查看详细&修改
			mRentNumberTextView.setText(mCurrentZLHBean.getZlh_number());
			mOperatorTextView.setText(UserCache
					.findUserById(mCurrentZLHBean.getOperator()).getName());
			mRemarkContent.setText(mCurrentZLHBean.getMark());
			loaderZLHDData(mCurrentZLHBean);
		//	mProject = new Project();
			// mProject.setName(mCurrentZLHBean.getProject_name());
		//	mProject.setProject_id(mCurrentZLHBean.getProject_id());
		} else {// 添加
			mShowZLHDDataList.clear();
			mUpdatingDataList.clear();
			RemoteCommonService.getInstance().getCodeByDate(ZLHCodeManager,
					"ZLH");
		}

		setupButtons();
		initListViewDialog();
		if (mOperation != OPERATION_DETAIL) {
			initOptionsMenuView();
		}else {
			mOperatorTextView.setBackground(getResources().getDrawable(
					R.drawable.bg_edittext));
			mOperatorTextView.setGravity(Gravity.CENTER_VERTICAL);
			mOperatorTextView.setTextSize(TypedValue.COMPLEX_UNIT_PX, getResources().getDimensionPixelSize(R.dimen.sp16_s));
			int pxw = UtilTools.dp2pxW(getBaseContext(), 8);
			int pxh = UtilTools.dp2pxH(getBaseContext(), 8);
			mOperatorTextView.setPadding(pxw, pxh, pxw, pxh);
		}

		mZLHDListView.setAdapter(mListAdapter);

	}

	private void msgHandlerProgress() {
		Intent intent = getIntent();
		mOperation = (Integer) intent.getSerializableExtra("operation");
		mCurrentZLHBean = (P_ZLH) intent.getSerializableExtra("data");
		mProject = (Project) intent.getSerializableExtra("project");
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
							modifyZLHD();
							break;
						case 1: // 删除
							commonConfirmDeleteZLHD();
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
			mRentNumberTextView.setText(code);
			if (mOperation == OPERATION_ADD) {
				mCurrentZLHBean.setTenant_id(UserCache
						.getCurrentUser().getTenant_id());
				mCurrentZLHBean.setProject_id(mProject.getProject_id());
				mCurrentZLHBean.setZlh_number(code);
			}
		}
	};

	private DataManagerInterface ZLHCodeManager = new DataManagerInterface() {
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

	private void loaderZLHDData(P_ZLH zlh) {
		RemoteZLHService.getInstance().getZLHD(mManager, zlh.getZlh_id());
	}

	private DataManagerInterface mManager = new DataManagerInterface() {

		public void getDataOnResult(ResultStatus status, List<?> list) {
			switch (status.getCode()) {
			case AnalysisManager.SUCCESS_DB_QUERY:
				if (list != null && list.size() > 0) {
					mShowZLHDDataList.clear();
					mUpdatingDataList.clear();
					for (Object object : list) {
						if (object instanceof P_ZLHD) {
							mShowZLHDDataList.add((P_ZLHD) object);
							mUpdatingDataList.add((P_ZLHD) object);
						}
					}
					mHandler.sendEmptyMessage(DATA_CHANGED);
				}
			default:
				break;
			}
		}
	};

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

	private void modifyZLHD() {
		String[] editTexts = new String[3];
		if (mCurrentItem.getNumber() != 0) {
			editTexts[0] = Double.toString(mCurrentItem.getNumber());
		}
		mCurrentItem.setPrimeval_number(mCurrentItem.getNumber());
		String username = UserCache.getUserMaps()
				.get(String.valueOf(mCurrentItem.getHz_person()));
		editTexts[1] = username == null ? "" : username;
		editTexts[2] = DateUtils.dateToString(DateUtils.FORMAT_SHORT,
				mCurrentItem.getHz_date());
		mListViewDialog.show(editTexts);
	}

	private void commonConfirmDeleteZLHD() {
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
								deleteZLHD();
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
	private void deleteZLHD() {
		mCurrentItem.setIDU(GLOBAL.IDU_DELETE);
		if (mCurrentItem.getZlhd_id() == 0)
			mUpdatingDataList.remove(mCurrentItem);
		mShowZLHDDataList.remove(mCurrentItem);
		mListAdapter.notifyDataSetChanged();
	}

	/**
	 * 数据更新
	 * 
	 * @param bean
	 */
	@SuppressLint("HandlerLeak")
	private void updateZLHDList(P_ZLHD bean) {
		if (bean.getZlhd_id() != 0) {
			bean.setIDU(GLOBAL.IDU_UPDATE);
		}

		for (int i = 0; i < mUpdatingDataList.size(); i++) {
			if (mUpdatingDataList.get(i).getZlhd_id() == bean.getZlhd_id()) {
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
				break;
			}
		}
	};

	@Override
	public int getLayoutId() {
		return R.layout.rent_add_list_item_title;
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
			if ((i == 5 || i == 7 || i == 8)) {
				viewHolder.tvs[i]
						.setOnClickListener(new View.OnClickListener() {
							@Override
							public void onClick(View view) {
								mListAdapter.setSelected(position, true);
								mCurrentItem = mListAdapter.getItem(position);
								modifyZLHD();
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
			DataListAdapter<P_ZLHD> adapter, int position) {
		Map<String, String> listViewItem = beanToMap(position,
				adapter.getItem(position));
		for (int i = 0; i < mListHeaderNames.length; i++) {
			if ((mOperation != OPERATION_DETAIL)
					&& (i == 5 || i == 7 || i == 8)) {
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
			if (i == 6 || i == 9 || i == 10) {
				holder.tvs[i].setGravity(Gravity.CENTER | Gravity.LEFT);
				holder.tvs[i].setPadding(5, 0, 0, 0);
			} else {
				holder.tvs[i].setGravity(Gravity.CENTER | Gravity.LEFT);
				holder.tvs[i].setPadding(10, 0, 0, 0);
			}
		}
	}

	public List<P_ZLHD> findByConditioonCreaten(Object... condition) {
		return null;
	}

	@Override
	public boolean isSameObject(P_ZLHD t1, P_ZLHD t2) {
		return false;
	}

	@Override
	public List<P_ZLHD> findByCondition(Object... condition) {
		return null;
	}

	public Map<String, String> beanToMap(int position, Object bean) {
		Map<String, String> mapItem = new HashMap<String, String>();
		if (bean instanceof P_ZLHD) {
			P_ZLHD zlhd = (P_ZLHD) bean;

			mapItem.put(mListHeaderNames[0], String.valueOf(position + 1));
			mapItem.put(mListHeaderNames[1],
					GLOBAL.SB_TYPE[Integer.valueOf(zlhd.getZl_type()) - 1][1]);// 类型
			mapItem.put(mListHeaderNames[2], zlhd.getZl_name());// 名称
			mapItem.put(mListHeaderNames[3], zlhd.getZl_spec());// 规格
			mapItem.put(mListHeaderNames[4], zlhd.getZlr_number());// 租入单号
			mapItem.put(mListHeaderNames[5], String.valueOf(zlhd.getNumber()));// 数量
			mapItem.put(mListHeaderNames[6], zlhd.getZl_company_name());// 租赁单位
			String username = UserCache.getUserMaps()
					.get(String.valueOf(zlhd.getHz_person()));
			mapItem.put(mListHeaderNames[7], username == null ? "" : username);//
			mapItem.put(
					mListHeaderNames[8],
					DateUtils.dateToString(DateUtils.FORMAT_SHORT,
							zlhd.getHz_date()));//

		}
		return mapItem;
	}

	// 按钮设置
	private void setupButtons() {
		if (mOperation == OPERATION_DETAIL) {
			mRemarkContent.setEnabled(false);
			btn_add.setVisibility(View.GONE);
			btn_save.setVisibility(View.GONE);
		} else {
			mOperatorTextView.setOnClickListener(new OnClickListener() {

				@Override
				public void onClick(View v) {
					// TODO Auto-generated method stub
					Intent intent = new Intent();
					intent.setClass(v.getContext(), OwnerSelectActivity.class);
					intent.putExtra("title", getString(R.string.execute_person));
					startActivityForResult(intent, OPERATOR_SELECT_CODE);
				}
			});

			btn_add.setOnClickListener(new OnClickListener() {

				@Override
				public void onClick(View v) {
					AddZLHD();
				}
			});

			btn_save.setOnClickListener(new View.OnClickListener() {

				@Override
				public void onClick(View view) {
					if (mOperation == OPERATION_ADD) {
						if (!checkDataList(mUpdatingDataList))
							return;
						// 增加还租单
						mCurrentZLHBean.setItem(mShowZLHDDataList.size());
						mCurrentZLHBean.setMark(mRemarkContent.getText()
								.toString());

						RemoteZLHService.getInstance().addZLH(
								new DataManagerInterface() {

									@Override
									public void getDataOnResult(
											ResultStatus status, List<?> list) {
									}
								}, mCurrentZLHBean, mUpdatingDataList);
						Intent mIntent = new Intent();
						setResult(RESULT_OK, mIntent);
						finish();

					} else if (mOperation == OPERATION_MODIFY) {
						// 更新还租单
						mCurrentZLHBean.setItem(mShowZLHDDataList.size());
						mCurrentZLHBean.setMark(mRemarkContent.getText()
								.toString());

						RemoteZLHService.getInstance().updateZLH(
								new DataManagerInterface() {

									@Override
									public void getDataOnResult(
											ResultStatus status, List<?> list) {
									}
								}, mCurrentZLHBean, mUpdatingDataList);

						if (mCurrentZLHBean.getZlh_id() != 0
								&& mShowZLHDDataList.size() == 0) {
							RemoteZLHService.getInstance().deleteZLH(
									new DataManagerInterface() {

										@Override
										public void getDataOnResult(
												ResultStatus status,
												List<?> list) {
										}
									}, mCurrentZLHBean.getZlh_id());
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
						mShowZLHDDataList.add(bean);
						mUpdatingDataList.add(bean);
					}
					mListAdapter.notifyDataSetChanged();
				}
			}
		} else if (requestCode == OPERATOR_SELECT_CODE) {
			User user = (User) data.getSerializableExtra("user");
			if (user != null) {
				mOperatorTextView.setText(user.getName());
				mCurrentZLHBean.setOperator(user.getUser_id());
			}
		} else if (requestCode == HZPERSON_SELECT_CODE) {
			User user = (User) data.getSerializableExtra("user");
			if (user != null) {
				mHZPersonEditText.setText(user.getName());
				mCurrentItem.setHz_person(user.getUser_id());
			}
		}

		super.onActivityResult(requestCode, resultCode, data);
	}

	@SuppressLint("UseSparseArrays")
	private void initListViewDialog() {
		mListViewDialog = new BaseDialog(this, getString(R.string.modify_lease));
		Map<Integer, Integer> buttons = new HashMap<Integer, Integer>();
		buttons.put(0, BaseDialog.decimalEditTextLineStyle);
		buttons.put(1, BaseDialog.editTextClickLineStyle);
		buttons.put(2, BaseDialog.calendarLineStyle);

		mListViewDialog.init(R.array.rent_add_dialog, buttons, null);

		mListDialogNames = getResources().getStringArray(
				R.array.rent_add_dialog);

		mHZPersonEditText = (EditText) mListViewDialog.getPopupView()
				.findViewById(mListViewDialog.baseEditTextId + 1);
		mHZPersonEditText.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				Intent intent = new Intent();
				intent.setClass(v.getContext(), OwnerSelectActivity.class);
				intent.putExtra("title", getString(R.string.operator));
				startActivityForResult(intent, HZPERSON_SELECT_CODE);
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
				mCurrentItem.setHz_person(UserCache
						.getUserListsMap()
						.get(items.get(mListDialogNames[count++])));
				Date hz_date = DateUtils.stringToDate(DateUtils.FORMAT_SHORT,
						items.get(mListDialogNames[count++]));

				mCurrentItem.setNumber(quantity);
				mCurrentItem.setHz_date(hz_date);

				if (mCurrentItem.getZlhd_id() != 0)
					mCurrentItem.setIDU(GLOBAL.IDU_UPDATE);
				updateZLHDList(mCurrentItem);
				mListAdapter.updateData(mCurrentItem);
				mListViewDialog.dismiss();
			}
		});

	}

	private boolean checkDataList(List<P_ZLHD> lists) {
		for (P_ZLHD zlhd : lists) {
			if (zlhd.getNumber() > 0 && zlhd.getHz_person() > 0
					&& zlhd.getHz_date() != null && zlhd.getZl_type() > 0
					&& zlhd.getZlr_number() != null
					&& !zlhd.getZlr_number().equals("")
					&& mCurrentZLHBean.getOperator() > 0)
				return true;
		}
		Toast.makeText(this, getResources().getString(R.string.information_incomplete), Toast.LENGTH_SHORT).show();
		return false;
	}

	@SuppressLint("HandlerLeak")
	private Handler mToastHandler = new Handler() {
		public void handleMessage(Message msg) {
			Toast.makeText(RentListAddActivity2.this, (CharSequence) msg.obj,
					Toast.LENGTH_SHORT).show();
		}
	};

}
