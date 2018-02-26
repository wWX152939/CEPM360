package com.pm360.cepm360.app.module.purchase;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.Dialog;
import android.app.ProgressDialog;
import android.content.Intent;
import android.content.res.TypedArray;
import android.graphics.Color;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.v4.app.Fragment;
import android.util.TypedValue;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.view.Window;
import android.widget.Button;
import android.widget.EditText;
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
import com.pm360.cepm360.app.common.view.OptionsMenuView;
import com.pm360.cepm360.app.common.view.parent.BaseToast;
import com.pm360.cepm360.app.utils.UtilTools;
import com.pm360.cepm360.common.GLOBAL;
import com.pm360.cepm360.common.ResultStatus;
import com.pm360.cepm360.common.json.AnalysisManager;
import com.pm360.cepm360.common.util.DateUtils;
import com.pm360.cepm360.common.util.LogUtil;
import com.pm360.cepm360.entity.Contract;
import com.pm360.cepm360.entity.P_CG;
import com.pm360.cepm360.entity.Project;
import com.pm360.cepm360.entity.User;
import com.pm360.cepm360.services.expenses.RemoteExpensesContractService;
import com.pm360.cepm360.services.purchase.RemotePurchaseService;
import com.pm360.cepm360.services.system.RemoteUserService;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

//import com.pm360.cepm360.app.module.contract.ContractInfoActivity;

public class PurchaseExecutiveFragment extends Fragment implements DataListAdapter.OngetBDLASlidePaneListener {

	private static final String PURCHASE_MODIFY_PERMISSION = "6_1";
	private static final String PURCHASE_CHECK_PERMISSION = "6_2";
	public static final String PURCHASE_MSG_VALUE = "purchase_msg_value";

	private View mRootView;
	private int mCgId = 0;

	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {
		super.onCreateView(inflater, container, savedInstanceState);
		Bundle bundle = getArguments();
		if (bundle != null && bundle.getInt(PURCHASE_MSG_VALUE) != 0) {
			mCgId = bundle.getInt(PURCHASE_MSG_VALUE);
		}
		if (!(PermissionCache
				.hasSysPermission(PURCHASE_MODIFY_PERMISSION) || PermissionCache.hasSysPermission(PURCHASE_CHECK_PERMISSION))) {
			mRootView = inflater.inflate(
					R.layout.no_permissions_content_layout, container, false);
		} else {
			mRootView = inflater.inflate(R.layout.purchase_executive_fragment,
					container, false);
			initListView();
			mOptionsMenuView = createOptionsMenuView(new String[] {
					getResources().getString(R.string.purchase_details),
					getResources().getString(R.string.purchase_modify)});
			mReadOnlyOptionsMenuView = createOptionsMenuView(new String[] {
					getResources().getString(R.string.purchase_details)});
			loadData();
		}
		return mRootView;

	}

	private View mHeaderList;
	private String[] mListHeadNames;
	private int[] mDisplayItemIds;
	private DataListAdapter<P_CG> mAdapter;
	private ListView mListView;

	@SuppressWarnings("unchecked")
	private void initListView() {
		mHeaderList = mRootView.findViewById(R.id.purchase_executive_listhead);
		mListHeadNames = getResources().getStringArray(
				R.array.purchase_executive_names);
		TypedArray typedArray = getResources().obtainTypedArray(
				R.array.purchase_executive_ids);
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
			/*
			final FloatingMenuView floatingMenuView = (FloatingMenuView) mRootView
					.findViewById(R.id.floating_menu);
			floatingMenuView.addPopItem("增加", R.drawable.icn_add_plan);
			floatingMenuView
					.setPopOnItemClickListener(new AdapterView.OnItemClickListener() {
	
						@Override
						public void onItemClick(AdapterView<?> parent, View view,
								int position, long id) {
								Intent intent1 = new Intent(getActivity(),
										PurchaseExecutiveAddActivity.class);
								Bundle bundle = new Bundle();
								bundle.putSerializable("purchase_add", (Serializable) mAdapter.getDataShowList());
								intent1.putExtras(bundle);
								startActivityForResult(intent1, 101);
								floatingMenuView.dismiss();
							}
	
					});
					*/
		}

		typedArray.recycle();
		mAdapter = new DataListAdapter<P_CG>(getActivity(), mListAdapterManager, R.array.purchase_executive_ids);
		mAdapter.setOngetBDLASlidePaneListener(this);
		mListView = (ListView) mRootView
				.findViewById(R.id.purchase_executive_listview);
		mListView.setAdapter(mAdapter);
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
				PurchaseExecutiveAddActivity.class);
		Bundle bundle = new Bundle();
		bundle.putSerializable("purchase_info", mAdapter.getItem(mLine));
		intent1.putExtras(bundle);
		startActivityForResult(intent1, 0);
	}

	// 修改
	private void modifyTicket() {
		if (PermissionCache
				.hasSysPermission(PURCHASE_MODIFY_PERMISSION)) {
			Intent intent1 = new Intent(getActivity(),
					PurchaseExecutiveAddActivity.class);
			Bundle bundle = new Bundle();
			bundle.putSerializable("purchase_modify", mAdapter.getItem(mLine));
			intent1.putExtras(bundle);
			startActivityForResult(intent1, 0);
		} else {
			BaseToast.show(getActivity(), BaseToast.NO_PERMISSION);
		}
	}

	// 删除
	private void deleteTicket() {
		if (PermissionCache
				.hasSysPermission(PURCHASE_MODIFY_PERMISSION)) {
//			RemotePurchaseService.getInstance().deleteCG(mDataManager, mCurrentItem.getCg_id());
		} else {
			BaseToast.show(getActivity(), BaseToast.NO_PERMISSION);
		}
	}
	
	@SuppressWarnings("unused")
	private void payTicket() {
		if (PermissionCache
				.hasSysPermission(PURCHASE_MODIFY_PERMISSION)) {

			saveFunction();

		} else {
			BaseToast.show(getActivity(), BaseToast.NO_PERMISSION);
		}
	}

    private void loadData() {
		int projectId = ProjectCache.getCurrentProject() == null ? 0 : ProjectCache.getCurrentProject().getProject_id();
        RemotePurchaseService.getInstance().getCGList(mDataManager,
                UserCache.getCurrentUser().getTenant_id(), 0, projectId);
    }

	@SuppressLint("HandlerLeak") 
	public Handler mToastHandler = new Handler() {
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

			switch (status.getCode()) {
			case AnalysisManager.SUCCESS_DB_QUERY:
				if (list != null && list.size() > 0) {
					mAdapter.setShowDataList((List<P_CG>) list);
					if (mCgId != 0) {
						for (int i = 0; i < mAdapter.getDataShowList().size(); i++) {
							if (mCgId == mAdapter.getDataShowList().get(i).getCg_id()) {
								mAdapter.setSelected(i, true);
								break;
							}
						}
					}
				}	
				break;

			case AnalysisManager.SUCCESS_DB_DEL:
				mAdapter.deleteData(mCurrentItem);
				break;

			case AnalysisManager.SUCCESS_DB_UPDATE:
				mAdapter.updateData(mCurrentItem);
				break;

			default:
				break;
			}
		}
	};

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
                    public void getDataOnResult(ResultStatus status, List<?> list) {
                        dismissProgressDialog();
                        if (status.getCode() != AnalysisManager.SUCCESS_DB_QUERY
                                && status.getMessage() != null
                                && !status.getMessage().equals("")) {
                            UtilTools.showToast(getActivity(), status.getMessage());
                        }
                        if (status.getCode() == AnalysisManager.SUCCESS_DB_QUERY
                                && list != null
                                && list.size() > 0) {
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
        RemoteExpensesContractService.getInstance().getExpensesContractList(new DataManagerInterface() {

            @Override
            public void getDataOnResult(ResultStatus status, List<?> list) {
                dismissProgressDialog();
                if (status.getCode() != AnalysisManager.SUCCESS_DB_QUERY
                        && status.getMessage() != null &&
                        !status.getMessage().equals("")) {
                    UtilTools.showToast(getActivity(), status.getMessage());
                }
                if ((status.getCode() == AnalysisManager.SUCCESS_DB_QUERY)
                        && list != null
                        && list.size() > 0) {
                    for (Object object : list) {
                        if (object instanceof Contract) {
                        	LogUtil.i("wzw cght_id:" + mCurrentItem.getCght_id());
                        	if (mCurrentItem.getCght_id() == ((Contract)object).getContract_id()) {
                        		Contract contract = (Contract)object;
                        		openContract(contract, 0);
                        		break;
                        	}
                        }
                    }
                    
                }
            }
        }, mProject.getProject_id(), 3);
    }
	
	private void openContract(Contract contract, int option) {
        Intent intent = new Intent();
        //intent.setClass(getActivity(), ContractInfoActivity.class);
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
        mProgressDialog = UtilTools.showProgressDialog(getActivity(), true, true);
    }

    private void dismissProgressDialog() {
        if (mProgressDialog != null && mProgressDialog.isShowing())
            mProgressDialog.dismiss();
    }

	private P_CG mCurrentItem;
	private OptionsMenuView mOptionsMenuView;
	private OptionsMenuView mReadOnlyOptionsMenuView;
	private int mLine;
	@SuppressWarnings("rawtypes")
	private DataListAdapter.ListAdapterInterface mListAdapterManager = new DataListAdapter.ListAdapterInterface() {
		@Override
		public int getLayoutId() {
			return R.layout.purchase_executive_title_list_item;
		}

		@Override
		public View getHeaderView() {
			return mHeaderList;
		}

		@Override
		public void regesterListeners(ViewHolder viewHolder, final int position) {
			for (int i = 0; i < viewHolder.tvs.length; i++) {
				if (i == 2) {
					String htName = mAdapter.getItem(position).getCght_name();
					if (htName != null && !htName.equals("")) {
						Drawable drawable= getResources().getDrawable(R.drawable.operation_button_info_white);
				        // x, y, width, height
				        drawable.setBounds(0, 0, 27, 27);
				        viewHolder.tvs[i].setCompoundDrawables(null,null,drawable,null);
				        viewHolder.tvs[i].setPadding(10, 0, 5, 0);
				        viewHolder.tvs[i].setOnClickListener(new OnClickListener() {
							
							@Override
							public void onClick(View v) {
								mCurrentItem = mAdapter.getItem(position);
								mLine = position;
								loadUserData();
							}
						});
					}
					
				} else {
					viewHolder.tvs[i]
							.setOnClickListener(new View.OnClickListener() {
								@Override
								public void onClick(View view) {

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
			return ((P_CG) t1).getCg_id() == ((P_CG) t2).getCg_id();
		}

	};

	private Map<String, String> beanToMap(Object bean) {
		Map<String, String> mapItem = new HashMap<String, String>();
		if (bean instanceof P_CG) {
			P_CG cg = (P_CG) bean;
			int count = 0;

			mapItem.put(mListHeadNames[count++], cg.getCg_number());
			mapItem.put(mListHeadNames[count++], cg.getCgjh_name());
			mapItem.put(mListHeadNames[count++], cg.getCght_name());
			mapItem.put(mListHeadNames[count++],
					UtilTools.formatMoney("¥", cg.getCg_money(), 2));
			mapItem.put(mListHeadNames[count++],
					GLOBAL.CG_TYPE[cg.getCg_status() - 1][1]);
			mapItem.put(mListHeadNames[count++], DateUtils.dateToString(DateUtils.FORMAT_SHORT, cg.getCreate_date()));
		}
		return mapItem;
	}

	public void saveFunction() {
		final Dialog dialog = new Dialog(getActivity());
		dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
		dialog.setContentView(R.layout.dialog_layout);

		Window dialogWindow = dialog.getWindow();
		dialogWindow.setGravity(Gravity.CENTER);
		dialog.show();

		TextView titleTextView = (TextView) dialog.findViewById(R.id.tv_title);
		final EditText dialog_et = (EditText) dialog
				.findViewById(R.id.dialog_edit_text);
		titleTextView.setText(R.string.input_pay);

		Drawable drawable = getActivity().getResources().getDrawable(
				R.drawable.icn_rmb);
		// 这一步必须要做,否则不会显示.
		drawable.setBounds(0, 0, 25, 25);
		dialog_et.setCompoundDrawables(null, null, drawable, null);

		OnClickListener listener = new OnClickListener() {

			@Override
			public void onClick(View arg0) {
				switch (arg0.getId()) {
				case R.id.cancel:
					dialog.cancel();
					break;
				case R.id.save:
					if (dialog_et.getText() == null) {
						BaseToast.show(getActivity(), BaseToast.NULL_MSG);
						break;
					}
//					RemotePurchaseService.getInstance().payment(mDataManager,
//							mCurrentItem);
					dialog.dismiss();
					break;
				}
			}
		};

		Button saveButton = (Button) dialog.findViewById(R.id.save);
		Button cacelButton = (Button) dialog.findViewById(R.id.cancel);
		saveButton.setOnClickListener(listener);
		cacelButton.setOnClickListener(listener);
	}
	
	@Override
	public void onActivityResult(int requestCode, int resultCode, Intent intent) {
		LogUtil.d("wzw resultcode " + requestCode + ", intent= " + intent);
		if (intent != null && intent.getExtras() != null) {
			Bundle data = intent.getExtras();
			if (resultCode == Activity.RESULT_OK) {

				String result = data
						.getString(PurchaseDataCache.RESULT_KEY_CODE);

				if (result.equals(PurchaseDataCache.RESULT_ADD_CODE)) {
					loadData();
				} else if (result.equals(PurchaseDataCache.RESULT_UPDATE_CODE)) {
					loadData();
				}
			}

		}
	}
	
	@Override
	public void onHiddenChanged(boolean hidden) {
		LogUtil.i("onHiddenChanged hidden" + hidden);
		if (!hidden) {
			loadData();
		}

		super.onHiddenChanged(hidden);
	}
	
	@Override
	public View getSlidePane() {
		return ((PurchaseActivity) getActivity()).getSlidingPaneLayout();
	}	
}
