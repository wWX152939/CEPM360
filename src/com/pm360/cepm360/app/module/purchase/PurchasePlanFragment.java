package com.pm360.cepm360.app.module.purchase;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Intent;
import android.content.res.TypedArray;
import android.graphics.Color;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.v4.app.Fragment;
import android.util.TypedValue;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.pm360.cepm360.R;
import com.pm360.cepm360.app.cache.PermissionCache;
import com.pm360.cepm360.app.cache.ProjectCache;
import com.pm360.cepm360.app.cache.UserCache;
import com.pm360.cepm360.app.common.activity.DataManagerInterface;
import com.pm360.cepm360.app.common.adpater.DataListAdapter;
import com.pm360.cepm360.app.common.adpater.DataListAdapter.ViewHolder;
import com.pm360.cepm360.app.common.view.FloatingMenuView;
import com.pm360.cepm360.app.common.view.OptionsMenuView;
import com.pm360.cepm360.app.common.view.parent.BaseToast;
import com.pm360.cepm360.app.utils.UtilTools;
import com.pm360.cepm360.common.GLOBAL;
import com.pm360.cepm360.common.ResultStatus;
import com.pm360.cepm360.common.json.AnalysisManager;
import com.pm360.cepm360.common.util.DateUtils;
import com.pm360.cepm360.common.util.LogUtil;
import com.pm360.cepm360.entity.P_CGJH;
import com.pm360.cepm360.services.purchase.RemotePlanService;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class PurchasePlanFragment extends Fragment implements DataListAdapter.OngetBDLASlidePaneListener{

	private static final String PURCHASE_MODIFY_PERMISSION = "6_1";
	private static final String PURCHASE_CHECK_PERMISSION = "6_2";

	private View mRootView;

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {
		super.onCreateView(inflater, container, savedInstanceState);
		if (!(PermissionCache
				.hasSysPermission(PURCHASE_MODIFY_PERMISSION) || PermissionCache.hasSysPermission(PURCHASE_CHECK_PERMISSION))) {
			mRootView = inflater.inflate(
					R.layout.no_permissions_content_layout, container, false);
		} else {
			mRootView = inflater.inflate(R.layout.purchase_plan_fragment,
					container, false);
			initListView();
			mOptionsMenuView = createOptionsMenuView(new String[] {
					getResources().getString(R.string.purchase_details),
					getResources().getString(R.string.purchase_modify),
					getResources().getString(R.string.purchase_delete)});
			mReadOnlyWithApprovalOptionsMenuView = createOptionsMenuView2(new String[] {
					getResources().getString(R.string.purchase_details)});
			mReadOnlyNoApprovalOptionsMenuView = createOptionsMenuView(new String[] {
					getResources().getString(R.string.purchase_details)});

			loadData();
		}
		return mRootView;

	}
	
	private View mHeaderList;
	private String[] mListHeadNames;
	private int[] mDisplayItemIds;
	private DataListAdapter<P_CGJH> mAdapter;
	private ListView mListView;

	@SuppressWarnings("unchecked")
	private void initListView() {
		mHeaderList = mRootView.findViewById(R.id.purchase_plan_listhead);
		mListHeadNames = getResources().getStringArray(
				R.array.purchase_plan_names);
		TypedArray typedArray = getResources().obtainTypedArray(
				R.array.purchase_plan_ids);
		if (mListHeadNames != null) {
			mDisplayItemIds = new int[typedArray.length()];
			for (int i = 0; i < mDisplayItemIds.length; i++) {
				mDisplayItemIds[i] = typedArray.getResourceId(i, 0);
				if (i < mListHeadNames.length) {
					// 配置列表头项相关字段
					TextView tv = (TextView) mHeaderList
							.findViewById(mDisplayItemIds[i]);

					tv.setTextSize(TypedValue.COMPLEX_UNIT_PX, getActivity().getResources().getDimension(R.dimen.table_title_textsize));
					tv.setTextColor(Color.WHITE);
					tv.setText(mListHeadNames[i]);
					tv.setGravity(Gravity.CENTER);
					tv.setPadding(0, 0, 0, 0);
				}
			}
		}

		if (PermissionCache
				.hasSysPermission(PURCHASE_MODIFY_PERMISSION)) {
			final FloatingMenuView floatingMenuView = (FloatingMenuView) mRootView
					.findViewById(R.id.floating_menu);
			floatingMenuView.addPopItem(getResources().getString(R.string.EPSMaintain_addeps), R.drawable.icn_add_plan);
			floatingMenuView
					.setPopOnItemClickListener(new AdapterView.OnItemClickListener() {
	
						@Override
						public void onItemClick(AdapterView<?> parent, View view,
								int position, long id) {
								Intent intent1 = new Intent(getActivity(),
										PurchasePlanAddActivity.class);
								startActivity(intent1, 101, ADD_TYPE);
								floatingMenuView.dismiss();
	
						}
	
					});
		}

		typedArray.recycle();
		mAdapter = new DataListAdapter<P_CGJH>(getActivity(),
				mListAdapterManager, R.array.purchase_plan_ids);
		mAdapter.setOngetBDLASlidePaneListener(this);
		mListView = (ListView) mRootView
				.findViewById(R.id.purchase_plan_listview);
		mListView.setAdapter(mAdapter);
	}

	private void loadData() {
		int projectId = ProjectCache.getCurrentProject() == null ? 0 : ProjectCache.getCurrentProject().getProject_id();
		RemotePlanService.getInstance().getCGJHList(
				mDataManager,
				UserCache.getCurrentUser()
						.getTenant_id(), 0, projectId);
	}

	@SuppressLint("HandlerLeak") public Handler mToastHandler = new Handler() {
		public void handleMessage(Message msg) {
			Toast.makeText(getActivity(), (CharSequence) msg.obj,
					Toast.LENGTH_SHORT).show();
		}
	};

	private DataManagerInterface mDataManager = new DataManagerInterface() {

		@SuppressWarnings("unchecked")
		@Override
		public void getDataOnResult(ResultStatus status, List<?> list) {

			if (status.getCode() != AnalysisManager.SUCCESS_DB_QUERY) {
				Message msg = new Message();
				msg.obj = status.getMessage();
				mToastHandler.sendMessage(msg);
			}
			LogUtil.d("status.getCode:" + status.getCode() + "list:" + list);
			switch (status.getCode()) {
			case AnalysisManager.SUCCESS_DB_QUERY:
				if (list != null && list.size() > 0)
					mAdapter.setShowDataList((List<P_CGJH>) list);
				break;

			case AnalysisManager.SUCCESS_DB_DEL:
				mAdapter.deleteData(mCurrentItem);
				break;

			case AnalysisManager.SUCCESS_DB_UPDATE:
				break;

			default:
				break;
			}
		}
	};

	private Map<String, String> beanToMap(Object bean) {
		Map<String, String> mapItem = new HashMap<String, String>();
		if (bean instanceof P_CGJH) {
			P_CGJH cgjh = (P_CGJH) bean;
			int count = 0;

			mapItem.put(mListHeadNames[count++], cgjh.getCgjh_number());
			mapItem.put(mListHeadNames[count++], cgjh.getCgjh_name());
			mapItem.put(mListHeadNames[count++],
					cgjh.getProject_name());
			
			mapItem.put(mListHeadNames[count++],
					UtilTools.formatMoney("¥", cgjh.getTotal_money(), 2));
			mapItem.put(mListHeadNames[count++],
					UserCache.getUserMaps().get(Integer.toString(cgjh.getPlan_person())));
			mapItem.put(mListHeadNames[count++],
					UserCache.getUserMaps().get(Integer.toString(cgjh.getExecute_person())));
			mapItem.put(
					mListHeadNames[count++],
					DateUtils.dateToString(DateUtils.FORMAT_SHORT,
							cgjh.getReport_date()));
			mapItem.put(
					mListHeadNames[count++],
					DateUtils.dateToString(DateUtils.FORMAT_SHORT,
							cgjh.getPlan_date()));
			int status = cgjh.getStatus();
			if (status == 0) {
				status = 1;
			}
			mapItem.put(mListHeadNames[count++], 
					GLOBAL.FLOW_APPROVAL_STATUS[status - 1][1]);
		}
		return mapItem;
	}

	private long mAttachDismissTime;

	private OptionsMenuView createOptionsMenuView(String[] subMenuNames) {
		OptionsMenuView optionsMenus = new OptionsMenuView(getActivity(),
				subMenuNames);
		optionsMenus.setSubMenuListener(new OptionsMenuView.SubMenuListener() {
			@Override
			public void onSubMenuClick(View view) {
				mAttachDismissTime = System.currentTimeMillis();
				switch ((Integer) view.getTag()) {
				case 0:
					detailTicket();
					break;
				case 1:
					modifyTicket();
					break;
				case 2:
					deleteTicket();
					break;
				case 3:
					submitTicket();
					break;
				}
				switchPopupMenu(view, false);
			}
		});
		return optionsMenus;
	}
	
	private OptionsMenuView createOptionsMenuView2(String[] subMenuNames) {
		OptionsMenuView optionsMenus = new OptionsMenuView(getActivity(),
				subMenuNames);
		optionsMenus.setSubMenuListener(new OptionsMenuView.SubMenuListener() {
			@Override
			public void onSubMenuClick(View view) {
				mAttachDismissTime = System.currentTimeMillis();
				switch ((Integer) view.getTag()) {
				case 0:
					detailTicket();
					break;
				case 1:
					approvalTicket();
					break;
				}
				switchPopupMenu(view, false);
			}
		});
		return optionsMenus;
	}
	
	private void submitTicket() {
		
	}
	
	private void approvalTicket() {
		
	}

	// 详情
	private void detailTicket() {
		Intent intent1 = new Intent(getActivity(),
				PurchasePlanAddActivity.class);
		startActivity(intent1, 0, INFO_TYPE);
	}

	// 修改
	private void modifyTicket() {
		if (PermissionCache
				.hasSysPermission(PURCHASE_MODIFY_PERMISSION)) {
			Intent intent1 = new Intent(getActivity(),
					PurchasePlanAddActivity.class);
			startActivity(intent1, 0, MODIFY_TYPE);
		} else {
			BaseToast.show(getActivity(), BaseToast.NO_PERMISSION);
		}
	}

	// 删除
	private void deleteTicket() {
		if (PermissionCache
				.hasSysPermission(PURCHASE_MODIFY_PERMISSION)) {
			UtilTools.deleteConfirm(getActivity(), new UtilTools.DeleteConfirmInterface() {

				@Override
				public void deleteConfirmCallback() {
					RemotePlanService.getInstance().deleteCGJH(mDataManager,
							mCurrentItem.getCgjh_id());

				}
				
			});
			// showProgressDialog(delEps);
			

		} else {
			BaseToast.show(getActivity(), BaseToast.NO_PERMISSION);
		}
	}
	
	private void switchPopupMenu(View view, boolean isShowStatus) {
		if (isShowStatus) {
			if (PermissionCache
					.hasSysPermission(PURCHASE_MODIFY_PERMISSION)) {
				if ((mCurrentItem.getStatus() == 0 
						|| mCurrentItem.getStatus() == Integer.parseInt(GLOBAL.FLOW_APPROVAL_STATUS[0][0])
						|| mCurrentItem.getStatus() == Integer.parseInt(GLOBAL.FLOW_APPROVAL_STATUS[3][0]))
						&& mCurrentItem.getPlan_person() == UserCache.getCurrentUser().getUser_id()) {
					mOptionsMenuView.showAsDropDown(view, 0, (-view
							.getMeasuredHeight() - UtilTools.dp2pxH(
							view.getContext(), 40)));
				} else {
					mReadOnlyWithApprovalOptionsMenuView.showAsDropDown(view, 
							UtilTools.dp2pxH(view.getContext(), 40), (-view
		                    .getMeasuredHeight() - UtilTools.dp2pxH(
		                    view.getContext(), 40)));
				}
				
			} else {
				mReadOnlyWithApprovalOptionsMenuView.showAsDropDown(view, 
						UtilTools.dp2pxH(view.getContext(), 40), (-view
	                    .getMeasuredHeight() - UtilTools.dp2pxH(
	                    view.getContext(), 40)));
				
			}
		} else {
			if (mOptionsMenuView.isShowing()) {
				mOptionsMenuView.dismiss();
			} else if (mReadOnlyWithApprovalOptionsMenuView.isShowing()) {
				mReadOnlyWithApprovalOptionsMenuView.dismiss();
			} else if (mReadOnlyNoApprovalOptionsMenuView.isShowing()) {
				mReadOnlyNoApprovalOptionsMenuView.dismiss();
			}
		}
		
	}

	private P_CGJH mCurrentItem;
	private OptionsMenuView mOptionsMenuView;
	private OptionsMenuView mReadOnlyNoApprovalOptionsMenuView;
	private OptionsMenuView mReadOnlyWithApprovalOptionsMenuView;
	private int mLine;
	@SuppressWarnings("rawtypes")
	DataListAdapter.ListAdapterInterface mListAdapterManager = new DataListAdapter.ListAdapterInterface() {
		@Override
		public int getLayoutId() {
			return R.layout.purchase_plan_title_list_item;
		}

		@Override
		public View getHeaderView() {
			return mHeaderList;
		}

		@Override
		public void regesterListeners(ViewHolder viewHolder, final int position) {
			for (int i = 0; i < viewHolder.tvs.length; i++) {
				viewHolder.tvs[i]
						.setOnClickListener(new View.OnClickListener() {
							@Override
							public void onClick(View view) {
								mAdapter.setSelected(position, true);

								long minus_time = System.currentTimeMillis()
										- mAttachDismissTime;

								if (minus_time < 300)
									return;

								mCurrentItem = mAdapter.getItem(position);
								switchPopupMenu(view, true);

								mAdapter.setSelected(position, true);

								mLine = position;
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
				holder.tvs[i].setText(listViewItem.get(mListHeadNames[i]));
			}

			// 将选中的列表项高亮
			if (mAdapter.getSelectedList().contains((Integer) position)) {
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
			return ((P_CGJH)t1).getCgjh_id() == ((P_CGJH)t2).getCgjh_id();
		}
	};
	
	private final int ADD_TYPE = 0;
	private final int INFO_TYPE = 1;
	private final int MODIFY_TYPE = 2;
	private void startActivity(Intent intent, int i, int type) {
		Bundle bundle = new Bundle();
		switch(type) {
		case ADD_TYPE:
			break;
		case INFO_TYPE:
			bundle.putSerializable(PurchasePlanAddActivity.INFO_DATA_KEY, mAdapter.getItem(mLine));
			break;
		case MODIFY_TYPE:
			bundle.putSerializable(PurchasePlanAddActivity.MODIFY_DATA_KEY, mAdapter.getItem(mLine));
			break;
		}

		intent.putExtras(bundle);
		startActivityForResult(intent, i);
	}
	
	public void onActivityResult(int requestCode, int resultCode, Intent intent) {
		LogUtil.d("wzw resultcode " +requestCode +", intent= " +intent );
		if (intent != null && intent.getExtras() != null) {
			Bundle data = intent.getExtras();
			if (resultCode == Activity.RESULT_OK) {
				String result = data.getString(PurchaseDataCache.RESULT_KEY_CODE);
				
				if (result.equals(PurchaseDataCache.RESULT_ADD_CODE)) {
					loadData();
				} else if (result.equals(PurchaseDataCache.RESULT_UPDATE_CODE)) {
					loadData();
				} else if (result.equals(PurchaseDataCache.RESULT_APPROVAL_CODE)) {
					loadData();
				}
			}
			
		}
	}

	@Override
	public View getSlidePane() {
		return ((PurchaseActivity) getActivity()).getSlidingPaneLayout();
	}


}
