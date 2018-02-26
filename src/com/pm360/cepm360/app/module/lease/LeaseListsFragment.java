package com.pm360.cepm360.app.module.lease;

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
import android.support.v4.app.Fragment;
import android.util.Log;
import android.util.TypedValue;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.FrameLayout;
import android.widget.ListView;
import android.widget.TextView;

import com.pm360.cepm360.R;
import com.pm360.cepm360.app.cache.EpsCache;
import com.pm360.cepm360.app.cache.PermissionCache;
import com.pm360.cepm360.app.cache.ProjectCache;
import com.pm360.cepm360.app.cache.UserCache;
import com.pm360.cepm360.app.common.activity.DataManagerInterface;
import com.pm360.cepm360.app.common.adpater.DataListAdapter;
import com.pm360.cepm360.app.common.adpater.DataListAdapter.ViewHolder;
import com.pm360.cepm360.app.common.adpater.EpsAdapter;
import com.pm360.cepm360.app.common.view.FloatingMenuView;
import com.pm360.cepm360.app.common.view.OptionsMenuView;
import com.pm360.cepm360.app.common.view.parent.BaseToast;
import com.pm360.cepm360.app.module.contract.ContractAttributeActivity;
import com.pm360.cepm360.app.module.purchase.BaseTicketActivity;
import com.pm360.cepm360.app.utils.UtilTools;
import com.pm360.cepm360.common.ResultStatus;
import com.pm360.cepm360.common.json.AnalysisManager;
import com.pm360.cepm360.common.util.DateUtils;
import com.pm360.cepm360.entity.P_ZLR;
import com.pm360.cepm360.entity.Project;
import com.pm360.cepm360.entity.User;
import com.pm360.cepm360.services.lease.RemoteZLRService;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class LeaseListsFragment extends Fragment implements
		DataListAdapter.ListAdapterInterface<P_ZLR> {
	private View mRootView;
	private EpsAdapter mListAdapter;
	private int mProjectId;
	private Project mSelectProject = new Project();
	private FloatingMenuView floatingMenuView;
	// 列表头显示名称
	private View mListHeaderView;
	private String[] mListHeaderNames;
	private int[] mDisplayItemIds;
	private OptionsMenuView mOptionsMenuView;
	private OptionsMenuView mReadOnlyOptionsMenuView;
	private ListView mLeaseListView;
	private DataListAdapter<P_ZLR> mLeaseDataListAdapter;
	private List<P_ZLR> mLeaseDataList = new ArrayList<P_ZLR>();
	private P_ZLR mCurrentItem;
	private static final int DATA_CHANGED = 101;
	private static final int OPERATION_DETAIL = 10;
	private static final int OPERATION_ADD = 11;
	private static final int OPERATION_MODIFY = 12;

	private static final String LEASE_VIEW = "5_2";
	private static final String LEASE_EDIT = "5_1";

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {
		if (!PermissionCache.hasSysPermission(LEASE_VIEW)
				&& !PermissionCache.hasSysPermission(LEASE_EDIT)) {
			return inflater.inflate(R.layout.no_permissions_content_layout,
					container, false);
		}
		mRootView = inflater.inflate(R.layout.lease_lists_fragment, container,
				false);
		initLeaseLayout(mRootView);
		floatingMenuView.setVisibility(View.GONE);
		return mRootView;
	}
	
	private void initLeaseLayout(View mRootView) {
		FrameLayout frame = (FrameLayout) mRootView.findViewById(R.id.lease);
		frame.setVisibility(View.VISIBLE);
		floatingMenuView = (FloatingMenuView) mRootView
				.findViewById(R.id.lease_floating_menu);
		floatingMenuView.addPopItem(getString(R.string.float_menu_add),
				R.drawable.icn_add);
		floatingMenuView.setVisibility(View.GONE);
		mListHeaderView = mRootView.findViewById(R.id.listHeaderView);
		mListHeaderNames = getActivity().getResources().getStringArray(
				R.array.lease_header_names);
		TypedArray titleType = getActivity().getResources().obtainTypedArray(
				R.array.lease_header_ids);
		if (mListHeaderNames != null) {
			int itemLength = mListHeaderNames.length;
			mDisplayItemIds = new int[itemLength];
			for (int i = 0; i < itemLength; i++) {
				mDisplayItemIds[i] = titleType.getResourceId(i, 0);
				TextView tv = (TextView) mRootView
						.findViewById(mDisplayItemIds[i]);
				tv.setText(mListHeaderNames[i]);
				tv.setTextSize(TypedValue.COMPLEX_UNIT_PX, getActivity()
						.getResources().getDimensionPixelSize(R.dimen.sp16_s));
				//tv.setGravity(Gravity.CENTER);
				//tv.setTextColor(Color.WHITE);
			}
		}
		titleType.recycle();
		ListView listView = (ListView) mRootView.findViewById(R.id.list_view);
		mLeaseListView = (ListView) mRootView.findViewById(R.id.listView);
		mLeaseDataListAdapter = new DataListAdapter<P_ZLR>(getActivity(), this,
				mLeaseDataList);
		mListAdapter = new EpsAdapter(getActivity(), EpsCache
				.getEpsLists(), ProjectCache.getProjectLists(),
				new EpsAdapter.EpsInterface() {

					@Override
					public void initListItem(EpsAdapter.ExpandCell expandCell) {
						Log.v("ccc", "选中项目，开始请求组节点数据");						
						if(expandCell.isEPS()){
							floatingMenuView.setVisibility(View.GONE);
						}else {
							if (PermissionCache.hasSysPermission(LEASE_EDIT))
							floatingMenuView.setVisibility(View.VISIBLE);
							mProjectId = expandCell.getProjectId();
							mSelectProject = ProjectCache.findProjectById(mProjectId);
							loadData(mSelectProject);
						}
					}
				});
		listView.setAdapter(mListAdapter);
		mLeaseListView.setAdapter(mLeaseDataListAdapter);
		//loadData(mSelectProject);
		initOptionsMenuView();
		floatingMenuView
				.setPopOnItemClickListener(new AdapterView.OnItemClickListener() {

					@Override
					public void onItemClick(AdapterView<?> parent, View view,
							int position, long id) {
						switch (position) {
						case 0:
							if (PermissionCache.hasSysPermission(
									LEASE_EDIT)) {
								startLeaseManagerAddActivity(OPERATION_ADD,
										new P_ZLR());
							} else {
								BaseToast.show(getActivity(),
										BaseToast.NO_PERMISSION);
							}
							floatingMenuView.dismiss();
							break;
						default:
							break;
						}
					}
				});
	}

	@Override
	public void onActivityResult(int requestCode, int resultCode, Intent intent) {
		if (resultCode == 0 || intent == null)
			return;
		switch (requestCode) {
		case OPERATION_ADD:
			loadData(mSelectProject);
			break;
		case OPERATION_MODIFY:
			loadData(mSelectProject);
			break;
		case Activity.RESULT_OK:
			loadData(mSelectProject);
			break;
		}
		mLeaseDataListAdapter.notifyDataSetChanged();
		super.onActivityResult(requestCode, resultCode, intent);
	}

	@SuppressLint("HandlerLeak")
	public Handler mHandler = new Handler() {
		public void handleMessage(Message msg) {
			switch (msg.what) {
			case DATA_CHANGED:
				mLeaseDataListAdapter.notifyDataSetChanged();
				break;
			}
		}
	};

	/**
	 * 数据更新
	 * 
	 * @param bean
	 */
	@SuppressWarnings("unused")
	private void updateLease(P_ZLR bean) {
		for (int i = 0; i < mLeaseDataList.size(); i++) {
			if (bean.getZlr_id() == mLeaseDataList.get(i).getZlr_id()) {
				mLeaseDataList.remove(i);
				mLeaseDataList.add(i, bean);
				break;
			}
		}
	}

	private void loadData(Project p) {
		RemoteZLRService.getInstance().getZLRList(mManager,
				UserCache.getCurrentUser().getTenant_id(), 0,
				p.getProject_id());
	}

	private DataManagerInterface mManager = new DataManagerInterface() {

		public void getDataOnResult(ResultStatus status, List<?> list) {
			switch (status.getCode()) {
			case AnalysisManager.SUCCESS_DB_QUERY:
				if (list != null && list.size() > 0) {
					mLeaseDataList.clear();
					for (Object object : list) {
						if (object instanceof P_ZLR) {
							mLeaseDataList.add((P_ZLR) object);
						}
					}
					mHandler.sendEmptyMessage(DATA_CHANGED);
				} else {
					mLeaseDataList.clear();
					mHandler.sendEmptyMessage(DATA_CHANGED);
				}

				break;

			case AnalysisManager.SUCCESS_DB_DEL:
				// mLeaseDataListAdapter.deleteData(mCurrentItem);
				// loadData(mSelectProject);
				break;

			case AnalysisManager.SUCCESS_DB_UPDATE:
				loadData(mSelectProject);
				break;
			default:
				break;
			}
		}
	};

	@Override
	public int getLayoutId() {
		return R.layout.lease_list_header;
	}

	@Override
	public View getHeaderView() {
		return mListHeaderView;
	}

	private void initOptionsMenuView() {
		String[] subMenuNames = getResources().getStringArray(
				R.array.subcontract_options_menu);
		String[] readOnlyMenuNames = getResources().getStringArray(
				R.array.subcontract_readonly_options_menu);
		mReadOnlyOptionsMenuView = new OptionsMenuView(getActivity(),
				readOnlyMenuNames);
		mReadOnlyOptionsMenuView
				.setSubMenuListener(new OptionsMenuView.SubMenuListener() {

					@Override
					public void onSubMenuClick(View view) {
						switch ((Integer) view.getTag()) {
						case 0:
							if (PermissionCache.hasSysPermission(
									LEASE_VIEW)
									|| PermissionCache
											.hasSysPermission(LEASE_EDIT)) {
								startLeaseManagerAddActivity(OPERATION_DETAIL,
										mCurrentItem);
							} else {
								BaseToast.show(getActivity(),
										BaseToast.NO_PERMISSION);
							}
							break;
						}
					}
				});
		mOptionsMenuView = new OptionsMenuView(getActivity(), subMenuNames);
		mOptionsMenuView
				.setSubMenuListener(new OptionsMenuView.SubMenuListener() {
					@Override
					public void onSubMenuClick(View view) {
						switch ((Integer) view.getTag()) {
						case 0:
							if (PermissionCache.hasSysPermission(
									LEASE_VIEW)
									|| PermissionCache
											.hasSysPermission(LEASE_EDIT)) {
								startLeaseManagerAddActivity(OPERATION_DETAIL,
										mCurrentItem);
							} else {
								BaseToast.show(getActivity(),
										BaseToast.NO_PERMISSION);
							}
							break;
						case 1:
							if (PermissionCache.hasSysPermission(
									LEASE_EDIT)) {
								startLeaseManagerAddActivity(OPERATION_MODIFY,
										mCurrentItem);
							} else {
								BaseToast.show(getActivity(),
										BaseToast.NO_PERMISSION);
							}
							break;
						case 2:
							if (PermissionCache.hasSysPermission(
									LEASE_EDIT)) {
								commonConfirmDelete();
							} else {
								BaseToast.show(getActivity(),
										BaseToast.NO_PERMISSION);
							}
							break;
						}
						mOptionsMenuView.dismiss();
					}
				});
	}

	private void startLeaseManagerAddActivity(int operation, P_ZLR currentBean) {
		Intent intent = new Intent(getActivity(), LeaseListAddActivity.class);
		Bundle bundle = new Bundle();
		if (operation == OPERATION_MODIFY) {
			bundle.putSerializable(BaseTicketActivity.MODIFY_DATA_KEY, currentBean);
		} else if (operation == OPERATION_DETAIL) {
			bundle.putSerializable(BaseTicketActivity.INFO_DATA_KEY, currentBean);
		} else if (operation == OPERATION_ADD) {
			currentBean.setProject_id(mProjectId);
			currentBean.setProject_name(ProjectCache.findProjectById(mProjectId).getName());
			Log.v("cccc","currentBean"+currentBean);
			bundle.putSerializable(BaseTicketActivity.ADD_DATA_KEY, currentBean);
		}
		intent.putExtras(bundle);
		startActivityForResult(intent, operation);
	}

	private void deleteLease(P_ZLR bean) {
		if (PermissionCache.hasSysPermission(LEASE_EDIT)) {
			RemoteZLRService.getInstance()
					.deleteZLR(mManager, bean.getZlr_id());
			loadData(mSelectProject);
		} else {
			BaseToast.show(getActivity(), BaseToast.NO_PERMISSION);
		}
	}

	private void openContract(int contractID) {
		Intent intent = new Intent();
		intent.setClass(getActivity(), ContractAttributeActivity.class);
		intent.putExtra(ContractAttributeActivity.CONTRACT_ID_KEY, contractID);
		intent.putExtra(ContractAttributeActivity.INCOME_CONTRACT_KEY, false);
		intent.putExtra(ContractAttributeActivity.IS_MODIFY_KEY, false);
		startActivity(intent);
	}

	@Override
	public void regesterListeners(ViewHolder viewHolder, final int position) {
		for (int i = 0; i < viewHolder.tvs.length; i++) {
			if (i == 3) {
				String htName = mLeaseDataListAdapter.getItem(position)
						.getZlht_name();
				if (htName != null && !htName.equals("")) {
					Drawable drawable = getResources().getDrawable(
							R.drawable.operation_button_info_white);
					// x, y, width, height
					drawable.setBounds(0, 0, 27, 27);
					viewHolder.tvs[i].setPadding(10, 0, 5, 0);
					viewHolder.tvs[i].setCompoundDrawables(drawable, null,
							null, null);
					viewHolder.tvs[i].setOnClickListener(new OnClickListener() {

						@Override
						public void onClick(View v) {
							mCurrentItem = mLeaseDataListAdapter
									.getItem(position);
							//loadUserData();
							//loadContractData();
							openContract(mCurrentItem.getContract_id());
						}
					});
				}
			} else {
				viewHolder.tvs[i]
						.setOnClickListener(new View.OnClickListener() {

							@Override
							public void onClick(View view) {
								mLeaseDataListAdapter.setSelected(position,
										true);
								mCurrentItem = mLeaseDataListAdapter
										.getItem(position);
								// if (UserCache.getCurrentUser()
								// .getUser_id() == mCurrentItem.getOperator()
								// && (mCurrentItem.getStatus() == Integer
								// .parseInt(GLOBAL.FLOW_APPROVAL_STATUS[0][0])
								// || mCurrentItem.getStatus() == 0
								// || mCurrentItem.getStatus() == Integer
								// .parseInt(GLOBAL.FLOW_APPROVAL_STATUS[3][0])))
								// {
								mOptionsMenuView.showAsDropDown(view, 0, (-view
										.getMeasuredHeight() - UtilTools
										.dp2pxH(view.getContext(), 40)));
								// } else {
								// mReadOnlyOptionsMenuView.showAsDropDown(
								// view,
								// 0,
								// (-view.getMeasuredHeight() - UtilTools
								// .dp2pxH(view.getContext(),
								// 40)));
								// }

							}
						});
			}

		}
	}

	@Override
	public void initListViewItem(View convertView, ViewHolder holder,
			DataListAdapter<P_ZLR> adapter, int position) {

		Map<String, String> listViewItem = beanToMap(position,
				adapter.getItem(position));
		for (int i = 0; i < mListHeaderNames.length; i++) {
			holder.tvs[i].setText(listViewItem.get(mListHeaderNames[i]));
		}

		// 将选中的列表项高亮		
		if (mLeaseDataListAdapter.getSelectedList().contains((Integer) position)) {
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
		}
	}

	@Override
	public List<P_ZLR> findByCondition(Object... condition) {
		return null;
	}

	public void onHiddenChanged(boolean hidden) {
		super.onHiddenChanged(hidden);
		if (!hidden) {
			if (PermissionCache.hasSysPermission(LEASE_VIEW)
					|| PermissionCache.hasSysPermission(
							LEASE_EDIT)) {
				loadData(mSelectProject);
			} else {
				BaseToast.show(getActivity(), BaseToast.NO_PERMISSION);
			}
		}
	}

	public Map<String, String> beanToMap(int position, Object bean) {
		Map<String, String> mapItem = new HashMap<String, String>();
		if (bean instanceof P_ZLR) {
			P_ZLR zlr = (P_ZLR) bean;
			mapItem.put(mListHeaderNames[0], String.valueOf(position + 1));
			mapItem.put(mListHeaderNames[1],
					String.valueOf(zlr.getZlr_number()));
			mapItem.put(mListHeaderNames[2], zlr.getProject_name());
			mapItem.put(mListHeaderNames[3], zlr.getZlht_name());
			mapItem.put(mListHeaderNames[4],
					UtilTools.formatMoney("¥", zlr.getTotal(), 2));
			mapItem.put(mListHeaderNames[5], zlr.getZl_company_name());
			mapItem.put(mListHeaderNames[6], String.valueOf(zlr.getItem()));
			User operauser = UserCache.findUserById(zlr.getOperator());
			if (operauser != null) {
				mapItem.put(mListHeaderNames[7], operauser.getName());
			}
			mapItem.put(mListHeaderNames[8],DateUtils.dateToString(DateUtils.FORMAT_LONG,zlr.getCreate_date()));
			mapItem.put(mListHeaderNames[9], zlr.getStorehouse());
			User storeuser = UserCache.findUserById(zlr.getStoreman());
			if (storeuser != null) {
				mapItem.put(mListHeaderNames[10], storeuser.getName());
			}

		}

		return mapItem;
	}

	@Override
	public boolean isSameObject(P_ZLR t1, P_ZLR t2) {
		// TODO Auto-generated method stub
		return t1.getZlr_id() == t2.getZlr_id();
	}

	private void commonConfirmDelete() {
		// 创建确认删除对话框，并显示
		new AlertDialog.Builder(getActivity())
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
								deleteLease(mCurrentItem);
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

	public void onResume() {
		super.onResume();

//		if (mCurrentProject == null) {
//			if (ProjectCache.getCurrentProject() != null) {
//				mCurrentProject = ProjectCache
//						.getCurrentProject();
//				((LeaseManagementActivity) getActivity())
//						.setActionBarTitle(mCurrentProject.getName());
//			}
//			if (PermissionCache.hasSysPermission(LEASE_VIEW)
//					|| PermissionCache.hasSysPermission(
//							LEASE_EDIT)) {
//				loadData(mSelectProject);
//			}
//
//		} else if (mCurrentProject != null) {
//			Project project = ProjectCache.getCurrentProject();
//			if (project.getProject_id() != mCurrentProject.getProject_id()) {
//				((LeaseManagementActivity) getActivity())
//						.setActionBarTitle(project.getName());
//				mCurrentProject = project;
//				if (PermissionCache.hasSysPermission(LEASE_VIEW)
//						|| PermissionCache.hasSysPermission(
//								LEASE_EDIT)) {
//					loadData(mSelectProject);
//				}
//			}
//		}
	}
}