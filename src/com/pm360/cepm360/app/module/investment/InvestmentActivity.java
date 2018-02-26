package com.pm360.cepm360.app.module.investment;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.res.TypedArray;
import android.graphics.Color;
import android.os.Bundle;
import android.util.Log;
import android.util.TypedValue;
import android.view.View;
import android.view.WindowManager;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.TextView;

import com.pm360.cepm360.R;
import com.pm360.cepm360.app.cache.EpsCache;
import com.pm360.cepm360.app.cache.PermissionCache;
import com.pm360.cepm360.app.cache.ProjectCache;
import com.pm360.cepm360.app.cache.UserCache;
import com.pm360.cepm360.app.common.activity.ActionBarFragmentActivity;
import com.pm360.cepm360.app.common.activity.DataManagerInterface;
import com.pm360.cepm360.app.common.adpater.DataListAdapter;
import com.pm360.cepm360.app.common.adpater.DataListAdapter.ViewHolder;
import com.pm360.cepm360.app.common.adpater.EpsAdapter;
import com.pm360.cepm360.app.common.view.FloatingMenuView;
import com.pm360.cepm360.app.common.view.OptionsMenuView;
import com.pm360.cepm360.app.common.view.parent.BaseToast;
import com.pm360.cepm360.app.utils.UtilTools;
import com.pm360.cepm360.common.ResultStatus;
import com.pm360.cepm360.common.json.AnalysisManager;
import com.pm360.cepm360.common.util.DateUtils;
import com.pm360.cepm360.common.util.LogUtil;
import com.pm360.cepm360.entity.InvestmentEstimate;
import com.pm360.cepm360.entity.Project;
import com.pm360.cepm360.services.investment.RemoteInvestmentEstimateService;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class InvestmentActivity extends ActionBarFragmentActivity implements
		DataListAdapter.ListAdapterInterface<InvestmentEstimate> {
//	private SlidingPaneLayout mSlidingPane;
	private FloatingMenuView mFloatingMenuView;
	private EpsAdapter mListAdapter;

	// 列表头显示名称
	private View mListHeaderView;
	private String[] mListHeaderNames;
	private int[] mDisplayItemIds;
	private ListView mEstimateListView;
	private DataListAdapter<InvestmentEstimate> mEstimateDataListAdapter;
	private List<InvestmentEstimate> mEstimateDataList = new ArrayList<InvestmentEstimate>();
	private InvestmentEstimate mCurrentItem;
	private OptionsMenuView mOptionsMenuView;
	private OptionsMenuView mReadOnlyOptionsMenuView;
	private int mProjectId;

	private static final int OPERATION_DETAIL = 10;
	private static final int OPERATION_ADD = 11;
	private static final int OPERATION_MODIFY = 12;
	private static final String ESTIMATE_VIEW = "23_2";
	private static final String ESTIMATE_EDIT = "23_1";

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
		if (!PermissionCache.hasSysPermission(ESTIMATE_VIEW)
				&& !PermissionCache.hasSysPermission(
						ESTIMATE_EDIT)) {
			setContentView(R.layout.no_permissions_content_layout);
			return;
		}
		getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,
				WindowManager.LayoutParams.FLAG_FULLSCREEN);

		setContentView(R.layout.investment_activity);

		initView();
		initFloatingMenu();
		initLeftView();
		initOptionsMenuView();		
	}

	private void initView() {
		//mSlidingPane = (SlidingPaneLayout) findViewById(R.id.sliding_pane);
		mListHeaderView = findViewById(R.id.listHeaderView);
		mListHeaderNames = getResources().getStringArray(
				R.array.estimate_header_names);
		TypedArray titleType = getResources().obtainTypedArray(
				R.array.estimate_header_ids);
		if (mListHeaderNames != null) {
			int itemLength = mListHeaderNames.length;
			mDisplayItemIds = new int[itemLength];
			for (int i = 0; i < itemLength; i++) {
				mDisplayItemIds[i] = titleType.getResourceId(i, 0);
				TextView tv = (TextView) findViewById(mDisplayItemIds[i]);
				tv.setText(mListHeaderNames[i]);
				tv.setTextSize(TypedValue.COMPLEX_UNIT_PX, getResources()
						.getDimension(R.dimen.table_title_textsize));
				// tv.setGravity(Gravity.CENTER);
				// tv.setTextColor(Color.BLACK);
			}
		}
		titleType.recycle();
		//mSlidingPane.openPane();

		mEstimateListView = (ListView) findViewById(R.id.listView);
		mEstimateDataListAdapter = new DataListAdapter<InvestmentEstimate>(
				this, this, mEstimateDataList);
		mEstimateListView.setAdapter(mEstimateDataListAdapter);
	}

	private void initLeftView() {
		ListView listView = (ListView) findViewById(R.id.list_view);
		LogUtil.i("wzw list:" + EpsCache.getEpsLists());
		LogUtil.i("wzw list:" + ProjectCache.getProjectLists());
		mListAdapter = new EpsAdapter(this, EpsCache
				.getEpsLists(), ProjectCache.getProjectLists(),
				new EpsAdapter.EpsInterface() {

					@SuppressWarnings("unused")
					@Override
					public void initListItem(EpsAdapter.ExpandCell expandCell) {
						Log.v("ccc", "选中项目，开始请求组节点数据");
						if (PermissionCache.hasSysPermission(
								ESTIMATE_EDIT))
							if (expandCell.isEPS()) {
								mFloatingMenuView.setVisibility(View.GONE);
							} else {
								mFloatingMenuView.setVisibility(View.VISIBLE);
								mProjectId = expandCell.getProjectId();
								Project mSelectProject = ProjectCache.findProjectById(
												mProjectId);
//								if (mSelectProject != null) {
//									setActionBarTitle(mSelectProject.getName());
//								}
								RemoteInvestmentEstimateService.getInstance()
										.getInvestmentEstimateList(
												mManager,
												UserCache
														.getCurrentUser()
														.getTenant_id(), 0,
												mProjectId);
							}
					}
				});

		listView.setAdapter(mListAdapter);
	}

	private void initFloatingMenu() {
		mFloatingMenuView = (FloatingMenuView) findViewById(R.id.floating_menu);
		mFloatingMenuView.addPopItem(getString(R.string.EPSMaintain_addeps), R.drawable.icn_add);
		// mFloatingMenuView.addPopItem("删除", R.drawable.menu_icon_delete);
		// mFloatingMenuView.addPopItem("修改", R.drawable.menu_icon_update);
		mFloatingMenuView
				.setPopOnItemClickListener(new AdapterView.OnItemClickListener() {

					@Override
					public void onItemClick(AdapterView<?> parent, View view,
							int position, long id) {
						switch (position) {
						case 0:/* Add */
							InvestmentEstimate addInvestmentEstimate = new InvestmentEstimate();
							addInvestmentEstimate.setProject_id(mProjectId);
							startEstimateAddActivity(OPERATION_ADD,
									addInvestmentEstimate);
							break;
						// case 1:/* Delete */
						//
						// break;
						// case 2:/* Modify */
						//
						// break;

						}
						mFloatingMenuView.dismiss();
					}

				});
		mFloatingMenuView.setVisibility(View.GONE);
	}

	private void initOptionsMenuView() {
		String[] subMenuNames = getResources().getStringArray(
				R.array.subcontract_options_menu);
		String[] readOnlyMenuNames = getResources().getStringArray(
				R.array.subcontract_readonly_options_menu);
		mReadOnlyOptionsMenuView = new OptionsMenuView(this, readOnlyMenuNames);
		mReadOnlyOptionsMenuView
				.setSubMenuListener(new OptionsMenuView.SubMenuListener() {

					@Override
					public void onSubMenuClick(View view) {
						switch ((Integer) view.getTag()) {
						case 0:
							if (PermissionCache.hasSysPermission(
									ESTIMATE_VIEW)) {
								startEstimateAddActivity(OPERATION_DETAIL,
										mCurrentItem);

							} else {
								BaseToast.show(getApplicationContext(),
										BaseToast.NO_PERMISSION);
							}
							break;
						}
						mReadOnlyOptionsMenuView.dismiss();
					}
				});
		mOptionsMenuView = new OptionsMenuView(this, subMenuNames);
		mOptionsMenuView
				.setSubMenuListener(new OptionsMenuView.SubMenuListener() {
					@Override
					public void onSubMenuClick(View view) {
						switch ((Integer) view.getTag()) {
						case 0:
							if (PermissionCache.hasSysPermission(
									ESTIMATE_VIEW)
									|| PermissionCache
											.hasSysPermission(ESTIMATE_EDIT)) {
								startEstimateAddActivity(OPERATION_DETAIL,
										mCurrentItem);
							} else {
								BaseToast.show(InvestmentActivity.this,
										BaseToast.NO_PERMISSION);
								startEstimateAddActivity(OPERATION_MODIFY,
										mCurrentItem);
							}
							break;
						case 1:
							if (PermissionCache.hasSysPermission(
									ESTIMATE_EDIT)) {
								startEstimateAddActivity(OPERATION_MODIFY,
										mCurrentItem);
							} else {
								BaseToast.show(InvestmentActivity.this,
										BaseToast.NO_PERMISSION);
							}
							break;
						case 2:
							if (PermissionCache.hasSysPermission(
									ESTIMATE_EDIT)) {
								commonConfirmDelete();
							} else {
								BaseToast.show(InvestmentActivity.this,
										BaseToast.NO_PERMISSION);
							}
							break;
						}
						mOptionsMenuView.dismiss();
					}
				});
	}

	private void loadData() {
		RemoteInvestmentEstimateService
				.getInstance()
				.getInvestmentEstimateList(
						mManager,
						UserCache.getCurrentUser().getTenant_id(),
						0, mProjectId);
	}

	private DataManagerInterface mManager = new DataManagerInterface() {

		public void getDataOnResult(ResultStatus status, List<?> list) {
			switch (status.getCode()) {
			case AnalysisManager.SUCCESS_DB_QUERY:
				if (list != null && list.size() > 0) {
					mEstimateDataList.clear();
					for (Object object : list) {
						if (object instanceof InvestmentEstimate) {
							mEstimateDataList.add((InvestmentEstimate) object);
						}
					}
					mEstimateDataListAdapter.notifyDataSetChanged();
				} else {
					mEstimateDataList.clear();
					mEstimateDataListAdapter.notifyDataSetChanged();
				}

				break;

			case AnalysisManager.SUCCESS_DB_DEL:
				// mEstimateDataListAdapter.deleteData(mCurrentItem);
				// loadData();
				break;

			case AnalysisManager.SUCCESS_DB_UPDATE:
				loadData();
				break;
			default:
				break;
			}
		}
	};

	@Override
	public int getLayoutId() {
		// TODO Auto-generated method stub
		return R.layout.investment_estimate_list_head;
	}

	@Override
	public View getHeaderView() {
		// TODO Auto-generated method stub
		return mListHeaderView;
	}

	@Override
	public void regesterListeners(ViewHolder viewHolder, final int position) {

		for (int i = 0; i < viewHolder.tvs.length; i++) {
			viewHolder.tvs[i].setOnClickListener(new View.OnClickListener() {

				@Override
				public void onClick(View view) {
					mEstimateDataListAdapter.setSelected(position, true);
					mCurrentItem = mEstimateDataListAdapter.getItem(position);
					if (PermissionCache.hasSysPermission(
							ESTIMATE_EDIT)) {

						mOptionsMenuView.showAsDropDown(view, 0, (-view
								.getMeasuredHeight() - UtilTools.dp2pxH(
								view.getContext(), 40)));
					} else if (PermissionCache.hasSysPermission(
							ESTIMATE_VIEW)) {
						mReadOnlyOptionsMenuView.showAsDropDown(view, 0, (-view
								.getMeasuredHeight() - UtilTools.dp2pxH(
								view.getContext(), 40)));

					}

				}
			});
		}

	}

	@Override
	public void initListViewItem(View convertView, ViewHolder holder,
			DataListAdapter<InvestmentEstimate> adapter, int position) {

		Map<String, String> listViewItem = beanToMap(position,
				adapter.getItem(position));
		for (int i = 0; i < mListHeaderNames.length; i++) {
			holder.tvs[i].setText(listViewItem.get(mListHeaderNames[i]));
		}

		// 将选中的列表项高亮
		if (mEstimateDataListAdapter.getSelectedList().contains(
				(Integer) position)) {
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
	public List<InvestmentEstimate> findByCondition(Object... condition) {
		return null;
	}

	@Override
	public boolean isSameObject(InvestmentEstimate t1, InvestmentEstimate t2) {
		// TODO Auto-generated method stub
		return t1.getInvestment_estimate_id() == t2.getInvestment_estimate_id();
	}

	public Map<String, String> beanToMap(int position, Object bean) {
		Map<String, String> mapItem = new HashMap<String, String>();
		if (bean instanceof InvestmentEstimate) {
			InvestmentEstimate tzgs = (InvestmentEstimate) bean;
			mapItem.put(mListHeaderNames[0], String.valueOf(position + 1));
			mapItem.put(mListHeaderNames[1],
					String.valueOf(tzgs.getEstimate_number()));
			mapItem.put(mListHeaderNames[2], tzgs.getEstimate_name());
			mapItem.put(mListHeaderNames[3], tzgs.getEstimate_period());
			mapItem.put(mListHeaderNames[4],
					UtilTools.formatMoney("¥", tzgs.getEstimate_money(), 2));
			mapItem.put(mListHeaderNames[5], String.valueOf(tzgs.getItem()));
			mapItem.put(mListHeaderNames[6], tzgs.getMark());
			mapItem.put(mListHeaderNames[7], String.valueOf(tzgs.getStatus()));
			mapItem.put(mListHeaderNames[8], UserCache
					.findUserById(tzgs.getCreater()).getName());
			mapItem.put(
					mListHeaderNames[9],
					DateUtils.dateToString(DateUtils.FORMAT_LONG,
							tzgs.getCreate_time()));
		}

		return mapItem;
	}

	private void deleteEstimate(InvestmentEstimate bean) {
		if (PermissionCache.hasSysPermission(ESTIMATE_EDIT)) {
			RemoteInvestmentEstimateService.getInstance()
					.deleteInvestmentEstimate(mManager,
							bean.getInvestment_estimate_id());
			loadData();
		} else {
			BaseToast.show(this, BaseToast.NO_PERMISSION);
		}
	}

	private void commonConfirmDelete() {
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
								deleteEstimate(mCurrentItem);
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

	private void startEstimateAddActivity(int operation,
			InvestmentEstimate currentBean) {
		Intent intent = new Intent(this, EstimateAddActivity.class);
		Bundle bundle = new Bundle();
		bundle.putSerializable("operation", operation);
		bundle.putSerializable("data", currentBean);
		intent.putExtras(bundle);
		startActivityForResult(intent, operation);
	}

	public void onActivityResult(int requestCode, int resultCode, Intent intent) {
		if (resultCode == 0 || intent == null)
			return;
		switch (requestCode) {
		case OPERATION_ADD:
			loadData();
			break;
		case OPERATION_MODIFY:
			loadData();
			break;
		case Activity.RESULT_OK:
			loadData();
			break;
		}
		mEstimateDataListAdapter.notifyDataSetChanged();
		super.onActivityResult(requestCode, resultCode, intent);
	}
}