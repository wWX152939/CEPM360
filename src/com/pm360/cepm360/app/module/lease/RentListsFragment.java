package com.pm360.cepm360.app.module.lease;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.res.TypedArray;
import android.graphics.Color;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.util.TypedValue;
import android.view.LayoutInflater;
import android.view.View;
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
import com.pm360.cepm360.app.module.purchase.BaseTicketActivity;
import com.pm360.cepm360.app.utils.UtilTools;
import com.pm360.cepm360.common.ResultStatus;
import com.pm360.cepm360.common.json.AnalysisManager;
import com.pm360.cepm360.common.util.DateUtils;
import com.pm360.cepm360.entity.P_ZLH;
import com.pm360.cepm360.entity.Project;
import com.pm360.cepm360.services.lease.RemoteZLHService;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class RentListsFragment extends Fragment implements
		DataListAdapter.ListAdapterInterface<P_ZLH> {
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
	private ListView mRentListView;
	private DataListAdapter<P_ZLH> mRentDataListAdapter;
	private List<P_ZLH> mRentDataList = new ArrayList<P_ZLH>();
	private P_ZLH mCurrentItem;
	private static final int DATA_CHANGED = 101;
	private static final int OPERATION_DETAIL = 10;
	private static final int OPERATION_ADD = 11;
	private static final int OPERATION_MODIFY = 12;

	private static final String RENT_VIEW = "5_2";
	private static final String RENT_EDIT = "5_1";

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {
		if (!PermissionCache.hasSysPermission(RENT_VIEW)
				&& !PermissionCache.hasSysPermission(RENT_EDIT)) {
			return inflater.inflate(R.layout.no_permissions_content_layout,
					container, false);
		}
		mRootView = inflater.inflate(R.layout.rent_lists_fragment, container,
				false);
		initRentLayout(mRootView);
		floatingMenuView.setVisibility(View.GONE);
		return mRootView;
	}

	private void initRentLayout(View mRootView) {
		FrameLayout frame = (FrameLayout) mRootView.findViewById(R.id.rent);
		frame.setVisibility(View.VISIBLE);
		floatingMenuView = (FloatingMenuView) mRootView
				.findViewById(R.id.rent_floating_menu);
		floatingMenuView.addPopItem(getString(R.string.float_menu_add),
				R.drawable.icn_add);
		floatingMenuView.setVisibility(View.GONE);
		mListHeaderView = mRootView.findViewById(R.id.listHeaderView);
		mListHeaderNames = getActivity().getResources().getStringArray(
				R.array.rent_header_names);
		TypedArray titleType = getActivity().getResources().obtainTypedArray(
				R.array.rent_header_ids);
		if (mListHeaderNames != null) {
			int itemLength = mListHeaderNames.length;
			mDisplayItemIds = new int[itemLength];
			for (int i = 0; i < itemLength; i++) {
				mDisplayItemIds[i] = titleType.getResourceId(i, 0);
				TextView tv = (TextView) mRootView
						.findViewById(mDisplayItemIds[i]);
				tv.setText(mListHeaderNames[i]);
				tv.setTextSize(TypedValue.COMPLEX_UNIT_PX, getActivity()
						.getResources().getDimension(R.dimen.table_title_textsize));
				//tv.setGravity(Gravity.CENTER);
				//tv.setTextColor(Color.WHITE);
			}
		}
		titleType.recycle();
		ListView listView = (ListView) mRootView.findViewById(R.id.list_view);
		mRentListView = (ListView) mRootView.findViewById(R.id.listView);
		mRentDataListAdapter = new DataListAdapter<P_ZLH>(getActivity(), this,
				mRentDataList);
		mListAdapter = new EpsAdapter(getActivity(), EpsCache
				.getEpsLists(), ProjectCache.getProjectLists(),
				new EpsAdapter.EpsInterface() {

					@Override
					public void initListItem(EpsAdapter.ExpandCell expandCell) {
						Log.v("ccc", "选中项目，开始请求组节点数据");
//						if (PermissionCache.hasSysPermission(LEASE_EDIT))						
						if(expandCell.isEPS()){
							floatingMenuView.setVisibility(View.GONE);
						}else {
							if (PermissionCache.hasSysPermission(RENT_EDIT))
							floatingMenuView.setVisibility(View.VISIBLE);
							mProjectId = expandCell.getProjectId();
							mSelectProject = ProjectCache.findProjectById(mProjectId);
							loadData(mSelectProject);
						}
					}
				});
		listView.setAdapter(mListAdapter);
		mRentListView.setAdapter(mRentDataListAdapter);
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
									RENT_EDIT)) {
								startRentManagerAddActivity(OPERATION_ADD,
										new P_ZLH());
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
		mRentDataListAdapter.notifyDataSetChanged();
		super.onActivityResult(requestCode, resultCode, intent);
	}

	@SuppressLint("HandlerLeak")
	public Handler mHandler = new Handler() {
		public void handleMessage(Message msg) {
			switch (msg.what) {
			case DATA_CHANGED:
				mRentDataListAdapter.notifyDataSetChanged();
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
	private void updateRent(P_ZLH bean) {
		for (int i = 0; i < mRentDataList.size(); i++) {
			if (bean.getZlh_id() == mRentDataList.get(i).getZlh_id()) {
				mRentDataList.remove(i);
				mRentDataList.add(i, bean);
				break;
			}
		}
	}

	private void loadData(Project p) {
		RemoteZLHService.getInstance().getZLHList(mManager,
				UserCache.getCurrentUser().getTenant_id(), 0,
				p.getProject_id());
	}

	private DataManagerInterface mManager = new DataManagerInterface() {

		public void getDataOnResult(ResultStatus status, List<?> list) {
			switch (status.getCode()) {
			case AnalysisManager.SUCCESS_DB_QUERY:
				if (list != null && list.size() > 0) {
					mRentDataList.clear();
					for (Object object : list) {
						if (object instanceof P_ZLH) {
							mRentDataList.add((P_ZLH) object);
						}
					}
					mHandler.sendEmptyMessage(DATA_CHANGED);
				} else {
					mRentDataList.clear();
					mHandler.sendEmptyMessage(DATA_CHANGED);
				}

				break;

			case AnalysisManager.SUCCESS_DB_DEL:
				// mRentDataListAdapter.deleteData(mCurrentItem);
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
		return R.layout.rent_list_header;
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
									RENT_VIEW)
									|| PermissionCache
											.hasSysPermission(RENT_EDIT)) {
								startRentManagerAddActivity(OPERATION_DETAIL,
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
									RENT_VIEW)
									|| PermissionCache
											.hasSysPermission(RENT_EDIT)) {
								startRentManagerAddActivity(OPERATION_DETAIL,
										mCurrentItem);
							} else {
								BaseToast.show(getActivity(),
										BaseToast.NO_PERMISSION);
							}
							break;
						case 1:
							if (PermissionCache.hasSysPermission(
									RENT_EDIT)) {
								startRentManagerAddActivity(OPERATION_MODIFY,
										mCurrentItem);
							} else {
								BaseToast.show(getActivity(),
										BaseToast.NO_PERMISSION);
							}
							break;
						case 2:
							if (PermissionCache.hasSysPermission(
									RENT_EDIT)) {
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

	private void startRentManagerAddActivity(int operation, P_ZLH currentBean) {				
		Intent intent = new Intent(getActivity(), RentListAddActivity.class);
		Bundle bundle = new Bundle();
		if (operation == OPERATION_MODIFY) {
			bundle.putSerializable(BaseTicketActivity.MODIFY_DATA_KEY, currentBean);
		} else if (operation == OPERATION_DETAIL) {
			bundle.putSerializable(BaseTicketActivity.INFO_DATA_KEY, currentBean);
		} else if (operation == OPERATION_ADD) {
			currentBean.setProject_id(mProjectId);
			bundle.putSerializable(BaseTicketActivity.ADD_DATA_KEY, currentBean);
		}
		intent.putExtras(bundle);
		startActivityForResult(intent, operation);		
	}

	private void deleteRent(P_ZLH bean) {
		if (PermissionCache.hasSysPermission(RENT_EDIT)) {
			RemoteZLHService.getInstance()
					.deleteZLH(mManager, bean.getZlh_id());
			loadData(mSelectProject);
		} else {
			BaseToast.show(getActivity(), BaseToast.NO_PERMISSION);
		}
	}

	@Override
	public void regesterListeners(ViewHolder viewHolder, final int position) {
		for (int i = 0; i < viewHolder.tvs.length; i++) {

			viewHolder.tvs[i].setOnClickListener(new View.OnClickListener() {

				@Override
				public void onClick(View view) {
					mRentDataListAdapter.setSelected(position, true);
					mCurrentItem = mRentDataListAdapter.getItem(position);
					mOptionsMenuView.showAsDropDown(
							view,
							0,
							(-view.getMeasuredHeight() - UtilTools.dp2pxH(
									view.getContext(), 40)));
				}
			});
		}

	}

	@Override
	public void initListViewItem(View convertView, ViewHolder holder,
			DataListAdapter<P_ZLH> adapter, int position) {

		Map<String, String> listViewItem = beanToMap(position,
				adapter.getItem(position));
		for (int i = 0; i < mListHeaderNames.length; i++) {
			holder.tvs[i].setText(listViewItem.get(mListHeaderNames[i]));
		}

		// 将选中的列表项高亮
		if (mRentDataListAdapter.getSelectedList().contains((Integer) position)) {
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
	public List<P_ZLH> findByCondition(Object... condition) {
		return null;
	}

	public void onHiddenChanged(boolean hidden) {
		super.onHiddenChanged(hidden);
		if (!hidden) {
			if (PermissionCache.hasSysPermission(RENT_VIEW)
					|| PermissionCache
							.hasSysPermission(RENT_EDIT)) {
				loadData(mSelectProject);
			} else {
				BaseToast.show(getActivity(), BaseToast.NO_PERMISSION);
			}
		}
	}

	public Map<String, String> beanToMap(int position, Object bean) {
		Map<String, String> mapItem = new HashMap<String, String>();
		if (bean instanceof P_ZLH) {
			P_ZLH zlh = (P_ZLH) bean;
			mapItem.put(mListHeaderNames[0], String.valueOf(position + 1));
			mapItem.put(mListHeaderNames[1],
					String.valueOf(zlh.getZlh_number()));
			mapItem.put(mListHeaderNames[2], String.valueOf(zlh.getItem()));
			String username = UserCache.getUserMaps()
					.get(String.valueOf(zlh.getOperator()));
			mapItem.put(mListHeaderNames[3], username == null ? "" : username);//
			mapItem.put(
					mListHeaderNames[4],
					DateUtils.dateToString(DateUtils.FORMAT_LONG,
							zlh.getCreate_date()));
			mapItem.put(mListHeaderNames[5], zlh.getMark());
		}

		return mapItem;
	}

	@Override
	public boolean isSameObject(P_ZLH t1, P_ZLH t2) {
		// TODO Auto-generated method stub
		return t1.getZlh_id() == t2.getZlh_id();
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
								deleteRent(mCurrentItem);
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
//			if (PermissionCache.hasSysPermission(RENT_VIEW)
//					|| PermissionCache.hasSysPermission(
//							RENT_EDIT)) {
//				loadData(mSelectProject);
//			}
//
//		} else if (mCurrentProject != null) {
//			Project project = ProjectCache.getCurrentProject();
//			if (project.getProject_id() != mCurrentProject.getProject_id()) {
//				((LeaseManagementActivity) getActivity())
//						.setActionBarTitle(project.getName());
//				mCurrentProject = project;
//				if (PermissionCache.hasSysPermission(RENT_VIEW)
//						|| PermissionCache.hasSysPermission(
//								RENT_EDIT)) {
//					loadData(mSelectProject);
//				}
//			}
//		}
	}
}