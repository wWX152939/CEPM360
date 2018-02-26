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
import com.pm360.cepm360.entity.P_CGYS;
import com.pm360.cepm360.services.purchase.RemoteBudgetService;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class PurchaseBudgetFragment extends Fragment{
	private static final String PURCHASE_MODIFY_PERMISSION = "6_1";
	private static final String PURCHASE_CHECK_PERMISSION = "6_2";

	public static final int PURCHASE_ADD_CODE = 100;
	public static final int PURCHASE_UPDATE_CODE = 101;
	public static final int PURCHASE_COMFIRM_CODE = 102;
	
	private View mRootView;
	private View mListLayout;

	private ListView mP_CGListView;

	private DataListAdapter<P_CGYS> mAdapter;


	// 列表头显示名称
	private String[] mListHeadNames;
	private int[] mDisplayItemIds;

	private P_CGYS mCurrentItem;

	private OptionsMenuView mOptionsMenuView;
	private OptionsMenuView mReadOnlyOptionsMenuView;

	protected long mAttachDismissTime;
	private Integer mLine;

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {

		super.onCreateView(inflater, container, savedInstanceState);
        if (!(PermissionCache.hasSysPermission(
                PURCHASE_MODIFY_PERMISSION) || PermissionCache
                .hasSysPermission(PURCHASE_CHECK_PERMISSION))) {
            mRootView = inflater.inflate(
                    R.layout.no_permissions_content_layout, container, false);
        } else {

			// 初始化基本布局变量
			initBasicLayout(inflater, container);
			
			// 初始化库存列表布局
			initManagerListView();
			// 加载数据
			loadData();

			mOptionsMenuView = createOptionsMenuView(new String[] {
					getResources().getString(R.string.purchase_details),
					getResources().getString(R.string.purchase_modify),
					getResources().getString(R.string.purchase_delete) });
			mReadOnlyOptionsMenuView = createOptionsMenuView(new String[] {
					getResources().getString(R.string.purchase_details)});

		}

		return mRootView;
	}

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
				}
				if (PermissionCache
						.hasSysPermission(PURCHASE_MODIFY_PERMISSION)) {
					mOptionsMenuView.dismiss();
				} else {
					mReadOnlyOptionsMenuView.dismiss();
				}			
			}
		});
		return optionsMenus;
	}

	// 详情
	private void detailTicket() {
		Intent intent1 = new Intent(getActivity(),
				PurchaseBudgetAddActivity.class);
		Bundle bundle = new Bundle();
		bundle.putSerializable(BaseTicketActivity.INFO_DATA_KEY, mAdapter.getItem(mLine));
		intent1.putExtras(bundle);
		startActivityForResult(intent1, 0);
	}

	// 修改
	private void modifyTicket() {
		if (PermissionCache
				.hasSysPermission(PURCHASE_MODIFY_PERMISSION)) {
			Intent intent1 = new Intent(getActivity(),
					PurchaseBudgetAddActivity.class);
			Bundle bundle = new Bundle();
			bundle.putSerializable(BaseTicketActivity.MODIFY_DATA_KEY, mAdapter.getItem(mLine));
			intent1.putExtras(bundle);
			startActivityForResult(intent1, PURCHASE_UPDATE_CODE);
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
					RemoteBudgetService.getInstance().deleteCGYS(mDataManager,
							mCurrentItem.getCgys_id());

				}
				
			});	
			// showProgressDialog(delEps);

		} else {
			BaseToast.show(getActivity(), BaseToast.NO_PERMISSION);
		}
	}

	@Override
	public void onActivityResult(int requestCode, int resultCode, Intent intent) {
		LogUtil.d("wzw resultcode " +requestCode +", intent= " +intent );
		if (intent != null && intent.getExtras() != null) {
			Bundle data = intent.getExtras();
			if (resultCode == Activity.RESULT_OK) {
				String result = data.getString(PurchaseDataCache.RESULT_KEY_CODE);
				
				if (result.equals(PurchaseDataCache.RESULT_ADD_CODE)) {

					LogUtil.d("wzw loadData");
					loadData();
				}
				else if (result.equals(PurchaseDataCache.RESULT_UPDATE_CODE)) {
					loadData();
				}
			}
			
		}
	}

    private void loadData() {
    	int projectId = ProjectCache.getCurrentProject() == null ? 0 : ProjectCache.getCurrentProject().getProject_id();
        RemoteBudgetService.getInstance().getCGYSList(mDataManager,
                UserCache.getCurrentUser().getTenant_id(), 0, projectId);
    }

	private void initBasicLayout(LayoutInflater inflater, ViewGroup container) {
		// 获取根布局
		mRootView = inflater.inflate(R.layout.purchase_budget_fragment,
				container, false);

	}
	
	@SuppressWarnings("rawtypes")
	DataListAdapter.ListAdapterInterface mListAdapterManager = new DataListAdapter.ListAdapterInterface() {

		@Override
		public int getLayoutId() {
			return R.layout.purchase_budget_title_list_item;
		}

		@Override
		public View getHeaderView() {
			return mListLayout;
		}

		@Override
		public void regesterListeners(ViewHolder viewHolder, final int position) {
			for (int i = 0; i < viewHolder.tvs.length; i++) {
				viewHolder.tvs[i].setOnClickListener(new View.OnClickListener() {
					@Override
					public void onClick(View view) {
						mAdapter.setSelected(position, true);
						
	                    long minus_time = System.currentTimeMillis()
	                            - mAttachDismissTime;

	                    if (minus_time < 300)
	                        return;

	                    mCurrentItem = mAdapter.getItem(position);

						if (PermissionCache
								.hasSysPermission(PURCHASE_MODIFY_PERMISSION)) {
							mOptionsMenuView.showAsDropDown(view, 0, (-view
		                            .getMeasuredHeight() - UtilTools.dp2pxH(
		                            view.getContext(), 40)));
						} else {
							mReadOnlyOptionsMenuView.showAsDropDown(view, 
									UtilTools.dp2pxW(view.getContext(), 40), (-view
		                            .getMeasuredHeight() - UtilTools.dp2pxH(
		                            view.getContext(), 40)));
						}
	                    

	                    mAdapter.setSelected(position, true);

	                    mLine = position;
					}
				});
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
		public List<?> findByCondition(Object... condition) {
			return null;
		}
		
		@Override
		public boolean isSameObject(Object t1, Object t2) {
			return ((P_CGYS)t1).getCgys_id() == ((P_CGYS)t2).getCgys_id();
		}

		@Override
		public void initListViewItem(View convertView, ViewHolder holder,
				DataListAdapter adapter, int position) {
			LogUtil.d("wzw initListViewItem");
			Map<String, String> listViewItem = beanToMap(adapter.getItem(position));
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
            LogUtil.d("wzw status:" + status.getCode());
			switch (status.getCode()) {
			case AnalysisManager.SUCCESS_DB_QUERY:
				if (list != null && list.size() > 0) {
					mAdapter.setShowDataList((List<P_CGYS>) list);
				}
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

	@SuppressLint("HandlerLeak") public Handler mToastHandler = new Handler() {
        public void handleMessage(Message msg) {
            Toast.makeText(getActivity(), (CharSequence) msg.obj,
                    Toast.LENGTH_SHORT).show();
        }
	};
	
	@SuppressWarnings("unchecked")
	public void initManagerListView() {
		mListLayout = mRootView.findViewById(R.id.purchase_manager_listhead);
		// 获取列表使用的相关资源
		mListHeadNames = getResources().getStringArray(
				R.array.purchase_budget_names);
		TypedArray typedArray = getResources().obtainTypedArray(
				R.array.purchase_budget_ids);
		if (mListHeadNames != null) {
			mDisplayItemIds = new int[typedArray.length()];
			for (int i = 0; i < mDisplayItemIds.length; i++) {
				mDisplayItemIds[i] = typedArray.getResourceId(i, 0);
				if (i < mListHeadNames.length) {
					// 配置列表头项相关字段
					TextView tv = (TextView) mListLayout
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
			floatingMenuView.addPopItem(getResources().getString(R.string.EPSMaintain_addeps), R.drawable.icn_add_budget);
			floatingMenuView
					.setPopOnItemClickListener(new AdapterView.OnItemClickListener() {
	
						@Override
						public void onItemClick(AdapterView<?> parent, View view,
								int position, long id) {
								Intent intent1 = new Intent(getActivity(),
										PurchaseBudgetAddActivity.class);
								startActivityForResult(intent1, PURCHASE_ADD_CODE);
								floatingMenuView.dismiss();
						}
	
					});
		}

		typedArray.recycle();

		mP_CGListView = (ListView) mRootView
				.findViewById(R.id.purchase_budget_listview);

		mAdapter = new DataListAdapter<P_CGYS>(getActivity().getBaseContext(), mListAdapterManager);
		mP_CGListView.setAdapter(mAdapter);

		// initial attr button function

	}


	@Override
	public void onResume() {
		super.onResume();
	}

	@Override
	public void onPause() {
		super.onPause();
	}

	@Override
	public void onHiddenChanged(boolean hidden) {
		super.onHiddenChanged(hidden);
		if (PermissionCache
				.hasSysPermission(PURCHASE_MODIFY_PERMISSION)
				|| PermissionCache
						.hasSysPermission(PURCHASE_CHECK_PERMISSION)) {
            if (!hidden) {
                loadData();
            }
		} else {
			BaseToast.show(getActivity(), BaseToast.NO_PERMISSION);
		}
	}

	@SuppressLint("SimpleDateFormat")
	private Map<String, String> beanToMap(Object bean) {
		Map<String, String> mapItem = new HashMap<String, String>();
		if (bean instanceof P_CGYS) {
			LogUtil.d("wzw P_CGYS");
			P_CGYS cgys = (P_CGYS)bean;
			int count = 0;

			mapItem.put(mListHeadNames[count++], cgys.getCgys_number());
			mapItem.put(mListHeadNames[count++], cgys.getCgys_name());
			mapItem.put(mListHeadNames[count++], cgys.getTask_name());
			mapItem.put(mListHeadNames[count++], Integer.toString(cgys.getWorkload()));
			mapItem.put(mListHeadNames[count++],
					UtilTools.formatMoney("¥", cgys.getTotal_money(), 2));
			mapItem.put(mListHeadNames[count++], Integer.toString(cgys.getPurchase_item()));
			mapItem.put(mListHeadNames[count++], UserCache.getUserMaps().get(Integer.toString(cgys.getCreator())));
			mapItem.put(mListHeadNames[count++], DateUtils.dateToString(DateUtils.FORMAT_SHORT, cgys.getCreate_date()));
			int status = cgys.getStatus() == 0 ? 0 : cgys.getStatus() - 1;
			mapItem.put(mListHeadNames[count++], GLOBAL.FLOW_APPROVAL_STATUS[status][1]);
		}
		return mapItem;
	}
}
