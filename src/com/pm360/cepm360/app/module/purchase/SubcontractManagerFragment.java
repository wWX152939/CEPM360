package com.pm360.cepm360.app.module.purchase;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.res.TypedArray;
import android.graphics.Color;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.v4.app.Fragment;
import android.util.TypedValue;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.FrameLayout;
import android.widget.ListView;
import android.widget.TextView;

import com.pm360.cepm360.R;
import com.pm360.cepm360.app.cache.PermissionCache;
import com.pm360.cepm360.app.cache.ProjectCache;
import com.pm360.cepm360.app.cache.UserCache;
import com.pm360.cepm360.app.common.activity.DataManagerInterface;
import com.pm360.cepm360.app.common.adpater.DataListAdapter;
import com.pm360.cepm360.app.common.adpater.DataListAdapter.ViewHolder;
import com.pm360.cepm360.app.common.view.FloatingMenuView;
import com.pm360.cepm360.app.common.view.OptionsMenuView;
import com.pm360.cepm360.app.common.view.parent.BaseDialog;
import com.pm360.cepm360.app.common.view.parent.BaseToast;
import com.pm360.cepm360.app.utils.UtilTools;
import com.pm360.cepm360.common.GLOBAL;
import com.pm360.cepm360.common.ResultStatus;
import com.pm360.cepm360.common.json.AnalysisManager;
import com.pm360.cepm360.common.util.DateUtils;
import com.pm360.cepm360.common.util.LogUtil;
import com.pm360.cepm360.entity.Contract;
import com.pm360.cepm360.entity.Flow_setting;
import com.pm360.cepm360.entity.P_WBRG;
import com.pm360.cepm360.entity.Project;
import com.pm360.cepm360.entity.User;
import com.pm360.cepm360.services.expenses.RemoteExpensesContractService;
import com.pm360.cepm360.services.subcontract.RemoteSubCcontentService;
import com.pm360.cepm360.services.system.RemoteFlowSettingService;
import com.pm360.cepm360.services.system.RemoteUserService;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

//import com.pm360.cepm360.app.module.contract.ContractInfoActivity;

public class SubcontractManagerFragment extends Fragment implements
		DataListAdapter.ListAdapterInterface<P_WBRG> {
	private View mRootView;
	// 列表头显示名称
	private View mListHeaderView;
	private String[] mListHeaderNames;
	private int[] mDisplayItemIds;
	private Project mCurrentProject;
	private BaseDialog mAddSubcontractDialog;
	private OptionsMenuView mOptionsMenuView;
	private OptionsMenuView mReadOnlyOptionsMenuView;
	private ListView mSubcontractListView;
	private DataListAdapter<P_WBRG> mSubcontractDataListAdapter;
	private List<P_WBRG> mSubcontractDataList = new ArrayList<P_WBRG>();
	private P_WBRG mCurrentItem;
	private static final int DATA_CHANGED = 101;
	private static final int OPERATION_DETAIL = 10;
	private static final int OPERATION_ADD = 11;
	private static final int OPERATION_MODIFY = 12;

	private static final String SUBCONTRACT_VIEW = "6_2";
	private static final String SUBCONTRACT_EDIT = "6_1";

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
            Bundle savedInstanceState) {
        if (!PermissionCache.hasSysPermission(SUBCONTRACT_VIEW)
                && !PermissionCache.hasSysPermission(
                        SUBCONTRACT_EDIT)) {
            return inflater.inflate(R.layout.no_permissions_content_layout,
                    container, false);
        }
        mRootView = inflater.inflate(R.layout.subcontract_fragment, container,
                false);
        initSubcontractLayout(mRootView);
        return mRootView;
    }

	private void initSubcontractLayout(View mRootView) {
		FrameLayout frame = (FrameLayout) mRootView
				.findViewById(R.id.subcontract);
		frame.setVisibility(View.VISIBLE);
		mListHeaderView = mRootView.findViewById(R.id.listHeaderView);
		mListHeaderNames = getActivity().getResources().getStringArray(
				R.array.subcontract_header_names);
		TypedArray titleType = getActivity().getResources().obtainTypedArray(
				R.array.subcontract_header_ids);
		if (mListHeaderNames != null) {
			int itemLength = mListHeaderNames.length;
			mDisplayItemIds = new int[itemLength];
			for (int i = 0; i < itemLength; i++) {
				mDisplayItemIds[i] = titleType.getResourceId(i, 0);
				TextView tv = (TextView) mRootView
						.findViewById(mDisplayItemIds[i]);
				tv.setText(mListHeaderNames[i]);
				tv.setTextSize(TypedValue.COMPLEX_UNIT_PX, getActivity().getResources().getDimensionPixelSize(R.dimen.table_title_textsize));
//				tv.setGravity(Gravity.CENTER);
//				tv.setTextColor(Color.WHITE);
			}
		}
		titleType.recycle();

		mSubcontractListView = (ListView) mRootView.findViewById(R.id.listView);
		mSubcontractDataListAdapter = new DataListAdapter<P_WBRG>(
				getActivity(), this, mSubcontractDataList);
		mSubcontractListView.setAdapter(mSubcontractDataListAdapter);
		loadData();
		initSubcontractCommonWindow();
		initOptionsMenuView();
		final FloatingMenuView floatingMenuView = (FloatingMenuView) mRootView
				.findViewById(R.id.subcontract_floating_menu);
		floatingMenuView.addPopItem(getString(R.string.float_menu_add),
				R.drawable.subcontract_add);
		floatingMenuView
				.setPopOnItemClickListener(new AdapterView.OnItemClickListener() {

					@Override
					public void onItemClick(AdapterView<?> parent, View view,
							int position, long id) {
						switch (position) {
						case 0:
							if (PermissionCache
									.hasSysPermission(SUBCONTRACT_EDIT)) {
								startSubcontractManagerAddActivity(
										OPERATION_ADD, new P_WBRG());
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
		loadFlowApprovalData();
	}

	private boolean hasFlowApproval = false;
	private Flow_setting mFlowSetting = new Flow_setting();
	private DataManagerInterface mFlowManager = new DataManagerInterface() {

		@Override
		public void getDataOnResult(ResultStatus status, List<?> list) {
			// if (status.getCode() != AnalysisManager.SUCCESS_DB_QUERY) {
			// Message msg = new Message();
			// msg.obj = status.getMessage();
			// mToastHandler.sendMessage(msg);
			// }

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
						}
					}

				}
			}
		}
	};

    private void loadFlowApprovalData() {
        RemoteFlowSettingService.getInstance().getFlowDetail(mFlowManager,
                UserCache.getCurrentUser().getTenant_id(),
                GLOBAL.FLOW_TYPE[4][0]);
    }

	@Override
	public void onActivityResult(int requestCode, int resultCode, Intent intent) {
		if (resultCode == 0 || intent == null)
			return;

		switch (requestCode) {
		case OPERATION_ADD:
		case OPERATION_MODIFY:
		case Activity.RESULT_OK:
			loadData();
			break;
		}
		mSubcontractDataListAdapter.notifyDataSetChanged();
		super.onActivityResult(requestCode, resultCode, intent);
	}

	@SuppressLint("HandlerLeak") 
	public Handler mHandler = new Handler() {
		public void handleMessage(Message msg) {
			switch (msg.what) {
			case DATA_CHANGED:
				mSubcontractDataListAdapter.notifyDataSetChanged();
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
	private void updateSubcontract(P_WBRG bean) {
		for (int i = 0; i < mSubcontractDataList.size(); i++) {
			if (bean.getWbrg_id() == mSubcontractDataList.get(i).getWbrg_id()) {
				mSubcontractDataList.remove(i);
				mSubcontractDataList.add(i, bean);
				break;
			}
		}
	}

	private void initSubcontractCommonWindow() {
		mAddSubcontractDialog = new BaseDialog(getActivity(),
				getString(R.string.seller_add_modify));
		mAddSubcontractDialog.init(R.array.add_subcontract, null, null);

		Button saveImageView = (Button) mAddSubcontractDialog.getPopupView()
				.findViewById(R.id.save_Button);
		saveImageView.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
			}
		});
	}

    private void loadData() {
        Project project = ProjectCache.getCurrentProject();
        if (project != null) {
            RemoteSubCcontentService.getInstance().getWBRGList(mManager,
                    UserCache.getCurrentUser().getTenant_id(), 0,
                    project.getProject_id());
        }
    }

	private DataManagerInterface mManager = new DataManagerInterface() {

		public void getDataOnResult(ResultStatus status, List<?> list) {
			switch (status.getCode()) {
			case AnalysisManager.SUCCESS_DB_QUERY:
				if (list != null && list.size() > 0) {
					mSubcontractDataList.clear();
					for (Object object : list) {
						if (object instanceof P_WBRG) {
							mSubcontractDataList.add((P_WBRG) object);
						}
					}
					mHandler.sendEmptyMessage(DATA_CHANGED);
				} else {
					mSubcontractDataList.clear();
					mHandler.sendEmptyMessage(DATA_CHANGED);
				}

				break;

			case AnalysisManager.SUCCESS_DB_DEL:
				// mSubcontractDataListAdapter.deleteData(mCurrentItem);
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
		return R.layout.subcontract_list_header;
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
							if (PermissionCache
									.hasSysPermission(SUBCONTRACT_VIEW)
									|| PermissionCache
											.hasSysPermission(SUBCONTRACT_EDIT)) {
								startSubcontractManagerAddActivity(
										OPERATION_DETAIL, mCurrentItem);
							} else {
								BaseToast.show(getActivity(),
										BaseToast.NO_PERMISSION);
							}
							break;
						}
						mReadOnlyOptionsMenuView.dismiss();
					}
				});
		mOptionsMenuView = new OptionsMenuView(getActivity(), subMenuNames);
		mOptionsMenuView
				.setSubMenuListener(new OptionsMenuView.SubMenuListener() {
					@Override
					public void onSubMenuClick(View view) {
						switch ((Integer) view.getTag()) {
						case 0:
							if (PermissionCache
									.hasSysPermission(SUBCONTRACT_VIEW)
									|| PermissionCache
											.hasSysPermission(SUBCONTRACT_EDIT)) {
								startSubcontractManagerAddActivity(
										OPERATION_DETAIL, mCurrentItem);
							} else {
								BaseToast.show(getActivity(),
										BaseToast.NO_PERMISSION);
							}
							break;
						case 1:
							if (PermissionCache
									.hasSysPermission(SUBCONTRACT_EDIT)) {
								startSubcontractManagerAddActivity(
										OPERATION_MODIFY, mCurrentItem);
							} else {
								BaseToast.show(getActivity(),
										BaseToast.NO_PERMISSION);
							}
							break;
						case 2:
							if (PermissionCache
									.hasSysPermission(SUBCONTRACT_EDIT)) {
								// deleteSubcontract(mCurrentItem);
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

	private void startSubcontractManagerAddActivity(int operation,
			P_WBRG currentBean) {
		Intent intent = new Intent(getActivity(),
				SubcontractManagerAddActivity.class);
		Bundle bundle = new Bundle();
		bundle.putSerializable("operation", operation);
		bundle.putSerializable("data", currentBean);
		bundle.putSerializable(SubcontractManagerAddActivity.PASS_APPROVAL,
				hasFlowApproval);
		bundle.putSerializable(
				SubcontractManagerAddActivity.PASS_APPROVAL_SETTING,
				mFlowSetting);
		intent.putExtras(bundle);
		startActivityForResult(intent, operation);
	}

	private void deleteSubcontract(P_WBRG bean) {
		// if (((CepmApplication) (getActivity().getApplication()))
		// .hasSysPermission(SUBCONTRACT_EDIT)) {
		RemoteSubCcontentService.getInstance().deleteWBRG(mManager,
				bean.getWbrg_id());
		loadData();
		// } else {
		// BaseToast.show(getActivity(), BaseToast.NO_PERMISSION);
		// }
	}

	private Project mProject = new Project();
	private ProgressDialog mProgressDialog;
	private List<User> mUserList = new ArrayList<User>();

	private void loadUserData() {
		showProgressDialog("loading UserList...");
		mProject.setProject_id(mCurrentItem.getProject_id());
		mProject.setTenant_id(mCurrentItem.getTenant_id());
		RemoteUserService.getInstance().getProjectUsers(
				new DataManagerInterface() {

					@Override
					public void getDataOnResult(ResultStatus status,
							List<?> list) {
						dismissProgressDialog();
						if (status.getCode() != AnalysisManager.SUCCESS_DB_QUERY
								&& status.getMessage() != null
								&& !status.getMessage().equals("")) {
							UtilTools.showToast(getActivity(),
									status.getMessage());
						}
						if (status.getCode() == AnalysisManager.SUCCESS_DB_QUERY
								&& list != null && list.size() > 0) {
							for (Object object : list) {
								if (object instanceof User) {
									User user = (User) object;
									mUserList.add(user);
								}
							}
							loadContractData();
						}
					}
				}, mProject);
	}

	private void loadContractData() {
		showProgressDialog("loading ContractList...");
		RemoteExpensesContractService.getInstance().getExpensesContractList(
				new DataManagerInterface() {

					@Override
					public void getDataOnResult(ResultStatus status,
							List<?> list) {
						dismissProgressDialog();
						if (status.getCode() != AnalysisManager.SUCCESS_DB_QUERY
								&& status.getMessage() != null
								&& !status.getMessage().equals("")) {
							UtilTools.showToast(getActivity(),
									status.getMessage());
						}
						if ((status.getCode() == AnalysisManager.SUCCESS_DB_QUERY)
								&& list != null && list.size() > 0) {
							for (Object object : list) {
								if (object instanceof Contract) {
									LogUtil.i("wzw cght_id:"
											+ mCurrentItem.getWbht_id());
									if (mCurrentItem.getWbht_id() == ((Contract) object)
											.getContract_id()) {
										Contract contract = (Contract) object;
										openContract(contract, 0);
										break;
									}
								}
							}

						}
					}
				}, UserCache.getCurrentUser().getTenant_id(), mProject.getProject_id());
	}

	private void openContract(Contract contract, int option) {
		Intent intent = new Intent();
		intent.setClass(getActivity(), /*ContractInfoActivity.class*/null);
		intent.putExtra("contract", contract);
		intent.putExtra("owner", findUserById(contract.getOwner()));
		intent.putExtra("option", option);
		startActivityForResult(intent, 1);
	}

	private User findUserById(int user_id) {
		User res = null;
		for (User user : mUserList) {
			if (user.getUser_id() == user_id) {
				res = user;
				break;
			}
		}
		return res;
	}

	private void showProgressDialog(String text) {
		dismissProgressDialog();
		mProgressDialog = UtilTools.showProgressDialog(getActivity(), true,
				true);
	}

	private void dismissProgressDialog() {
		if (mProgressDialog != null && mProgressDialog.isShowing())
			mProgressDialog.dismiss();
	}

	@Override
	public void regesterListeners(ViewHolder viewHolder, final int position) {
		for (int i = 0; i < viewHolder.tvs.length; i++) {
			if (i == 2) {
				String htName = mSubcontractDataListAdapter.getItem(position)
						.getWbht_name();
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
							mCurrentItem = mSubcontractDataListAdapter
									.getItem(position);
							loadUserData();
						}
					});
				}
			} else {
				viewHolder.tvs[i]
						.setOnClickListener(new View.OnClickListener() {

							@Override
							public void onClick(View view) {
								mSubcontractDataListAdapter.setSelected(
										position, true);
								mCurrentItem = mSubcontractDataListAdapter
										.getItem(position);
								if (UserCache.getCurrentUser()
										.getUser_id() == mCurrentItem
										.getCreater()
										&& (mCurrentItem.getStatus() == Integer
												.parseInt(GLOBAL.FLOW_APPROVAL_STATUS[0][0])
										|| mCurrentItem.getStatus() == 0
										|| mCurrentItem.getStatus() == Integer
												.parseInt(GLOBAL.FLOW_APPROVAL_STATUS[3][0]))) {
									mOptionsMenuView.showAsDropDown(
											view,
											0,
											(-view.getMeasuredHeight() - UtilTools
													.dp2pxH(view.getContext(),
															40)));
								} else {
									mReadOnlyOptionsMenuView.showAsDropDown(
											view,
											0,
											(-view.getMeasuredHeight() - UtilTools
													.dp2pxH(view.getContext(),
															40)));
								}

							}
						});
			}

		}
	}

	@Override
	public void initListViewItem(View convertView, ViewHolder holder,
			DataListAdapter<P_WBRG> adapter, int position) {

		Map<String, String> listViewItem = beanToMap(position,
				adapter.getItem(position));
		for (int i = 0; i < mListHeaderNames.length; i++) {
			holder.tvs[i].setText(listViewItem.get(mListHeaderNames[i]));
		}

		// 将选中的列表项高亮
		if (mSubcontractDataListAdapter.getSelectedList().contains(
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
	public List<P_WBRG> findByCondition(Object... condition) {
		return null;
	}

	public void onHiddenChanged(boolean hidden) {
		super.onHiddenChanged(hidden);
		if (!hidden) {
			if (PermissionCache
					.hasSysPermission(SUBCONTRACT_VIEW)
					|| PermissionCache
							.hasSysPermission(SUBCONTRACT_EDIT)) {
				loadData();
			} else {
				BaseToast.show(getActivity(), BaseToast.NO_PERMISSION);
			}
		}
	}

	public Map<String, String> beanToMap(int position, Object bean) {
		Map<String, String> mapItem = new HashMap<String, String>();
		if (bean instanceof P_WBRG) {
			P_WBRG wbrg = (P_WBRG) bean;
			mapItem.put(mListHeaderNames[0], String.valueOf(position + 1));
			mapItem.put(mListHeaderNames[1], wbrg.getWbrg_number());
			mapItem.put(mListHeaderNames[2], wbrg.getWbht_name());
			mapItem.put(mListHeaderNames[3],
					UtilTools.formatMoney("¥", wbrg.getTotal_money(), 2));
            mapItem.put(mListHeaderNames[4], UserCache
                    .getUserMaps().get(String.valueOf(wbrg.getCreater())));		
			mapItem.put(
					mListHeaderNames[5],
					DateUtils.dateToString(DateUtils.FORMAT_LONG,
							wbrg.getCreate_date()));

			int status = wbrg.getStatus();
			if (status == 0) {
				status = 1;
			}
			mapItem.put(mListHeaderNames[6],
					GLOBAL.FLOW_APPROVAL_STATUS[status - 1][1]);
		}
		return mapItem;
	}

	@Override
	public boolean isSameObject(P_WBRG t1, P_WBRG t2) {
		// TODO Auto-generated method stub
		return t1.getWbrg_id() == t2.getWbrg_id();
	}

	public void onResume() {
		super.onResume();

		if (mCurrentProject == null) {
			if (ProjectCache.getCurrentProject() != null) {
				mCurrentProject = ProjectCache.getCurrentProject();
				((PurchaseActivity) getActivity())
						.setActionBarTitle(mCurrentProject.getName());
			}
			if (PermissionCache
					.hasSysPermission(SUBCONTRACT_VIEW)
					|| PermissionCache
							.hasSysPermission(SUBCONTRACT_EDIT)) {
				loadData();
			}

		} else if (mCurrentProject != null) {
			Project project = ProjectCache.getCurrentProject();
			if (project.getProject_id() != mCurrentProject.getProject_id()) {
				((PurchaseActivity) getActivity())
						.setActionBarTitle(project.getName());
				mCurrentProject = project;
				if (PermissionCache
						.hasSysPermission(SUBCONTRACT_VIEW)
						|| PermissionCache
								.hasSysPermission(SUBCONTRACT_EDIT)) {
					loadData();
				}
			}
		}
	}

	private void commonConfirmDelete() {
		// 创建确认删除对话框，并显示
		new AlertDialog.Builder(getActivity())
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
								deleteSubcontract(mCurrentItem);
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
}