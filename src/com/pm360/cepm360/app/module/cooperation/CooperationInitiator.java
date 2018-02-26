package com.pm360.cepm360.app.module.cooperation;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.FrameLayout.LayoutParams;

import com.pm360.cepm360.R;
import com.pm360.cepm360.app.CepmApplication;
import com.pm360.cepm360.app.cache.PermissionCache;
import com.pm360.cepm360.app.cache.TenantCache;
import com.pm360.cepm360.app.cache.UserCache;
import com.pm360.cepm360.app.common.activity.DataManagerInterface;
import com.pm360.cepm360.app.common.activity.ProjectSelectActivity;
import com.pm360.cepm360.app.common.custinterface.CommonListInterface;
import com.pm360.cepm360.app.common.custinterface.DialogAdapterInterface;
import com.pm360.cepm360.app.common.custinterface.OptionMenuInterface;
import com.pm360.cepm360.app.common.custinterface.ServiceInterface;
import com.pm360.cepm360.app.common.view.OptionsMenuView.SubMenuListener;
import com.pm360.cepm360.app.common.view.parent.BaseDialog;
import com.pm360.cepm360.app.common.view.parent.BaseListFragment;
import com.pm360.cepm360.common.GLOBAL;
import com.pm360.cepm360.common.ResultStatus;
import com.pm360.cepm360.common.json.AnalysisManager;
import com.pm360.cepm360.common.util.MiscUtils;
import com.pm360.cepm360.entity.Cooperation;
import com.pm360.cepm360.entity.Project;
import com.pm360.cepm360.entity.Tenant;
import com.pm360.cepm360.entity.User;
import com.pm360.cepm360.services.cooperation.RemoteCooperationService;
import com.pm360.cepm360.services.system.RemoteUserService;

public class CooperationInitiator extends BaseListFragment<Cooperation> {
	private RemoteCooperationService mService = RemoteCooperationService
			.getInstance();;
	private Project mProject = new Project();
	private Tenant mTenant = new Tenant();
	private ArrayList<Tenant> mTenantLists = new ArrayList<Tenant>();
	private ArrayList<Cooperation> mlistcooperations = new ArrayList<Cooperation>();
	
	private static final int ADD_PROJECT_SELECT_REQUEST = 100;
	private static final int ADD_COMPANY_SELECT_REQUSET = 101;
	private static final int OWNER_REQUEST_CODE = 102;
	protected static final int TenantList = 100;
	protected static final int AllCooperationTenantList = 200;
	protected static final int UnCooperationTenantListByProject = 300;
	protected static final int CooperationTenantListByProject = 400;
	
	private boolean mEnableOption = true;
	private static final String COOPERATION_EDIT = "17_1";
	private static final String COOPERATION_VIEW = "17_2";
	private Button mbutton;
	
	/**
	 * 创建视图
	 */
	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {
		forceEnableOption(mEnableOption);
		
		// 初始化类型和接口
		init(Cooperation.class, mListInterface, mRequestInterface, null,
				mOptionsMenuInterface, mDialogInterface);

		setPermissionIdentity(GLOBAL.SYS_ACTION[37][0],
				GLOBAL.SYS_ACTION[36][0]);

		mApplication = (CepmApplication) getActivity().getApplication();
		

		View view = super.onCreateView(inflater, container, savedInstanceState);

		if (mDialog != null) {
			doExtraAddFloatingMenuEvent();
		}
		
		return view;

	}

	/**
	 * 服务返回结果处理
	 */
	private DataManagerInterface mGetPojectLeaderManager = new DataManagerInterface() {

		@Override
		public void getDataOnResult(ResultStatus status, List<?> list) {
			
			if (status.getCode() == AnalysisManager.SUCCESS_DB_QUERY) {
				@SuppressWarnings("unchecked")
				List<User> userList = (List<User>) list;
				String user = "";
				
				for (User u : userList) {
					if (u.getRole().contains("XMJL")) {
						user += u.getUser_id() + ",";
					}
				}
				if (user.isEmpty()) {
					mProgressDialog.dismiss();
					sendMessage(SHOW_TOAST, getString(R.string.no_project_leader_exist));
					return;
				}
				// 去掉最后一个逗号
				user = user.substring(0, user.length() - 1);
				mCurrentUpdateItem.setNotice_person(user);
				mCurrentUpdateItem.setStatus(Integer.parseInt(GLOBAL.COORPERATION_STATUS[0][0]));
				if (mIsAddOperation) {
					mService.LaunchCooperation(mDataManager, mCurrentUpdateItem);
				} else {
					mService.updateLaunchCooperation(mDataManager, mCurrentUpdateItem);
				}
				
				// refreshData();
				mDialog.dismiss();
			} else {
				mProgressDialog.dismiss();
				sendMessage(SHOW_TOAST, status.getMessage());
			}
		}
	};

	/**
	 * 服务返回结果处理
	 */
	private DataManagerInterface mDataManager = new DataManagerInterface() {

		@SuppressWarnings("unchecked")
		@Override
		public void getDataOnResult(ResultStatus status, List<?> list) {
			Log.i("wzw", "code:" + status.getCode());
			switch (status.getCode()) {
			case AnalysisManager.SUCCESS_DB_QUERY:
				if (OperationMode.MULTI_SELECT == mCurrentMode
						&& mFilterList != null && mFilterList.size() > 0) {
					filterSelectedDatas((List<Cooperation>) list);
				}
				mListAdapter.setDataList((List<Cooperation>) list);
				mListAdapter.setShowDataList((List<Cooperation>) list);

				// 结束加载进度对话框
				mDataLoaded = true;

				if (mSpecifiedItem != null) {
					locationSpecifiedItem(mSpecifiedItem);
				}

				doExtraCooperationInit();

				mListAdapter.notifyDataSetChanged();

				break;

			case AnalysisManager.SUCCESS_DB_ADD:
				Log.i("wzw", "111list" + list);
				if (list != null && !list.isEmpty()) {
					Cooperation cooperation = (Cooperation) list.get(0);
					for (Tenant tenant : mTenantLists) {
						if (tenant.getTenant_id() == cooperation
								.getAccept_company()) {
							cooperation.setCompany_type(tenant.getType());
						}
					}

					mListAdapter.addDataToList(cooperation);
				}
				break;

			case AnalysisManager.SUCCESS_DB_DEL:
				mRemoveCount++;
				if (mRemoveCount == mRemoveList.size()) {
					if (mRemoveFailedCount == 0) { // 删除都成功
						allDeleteSuccessful();
					} else { // 删除部分失败
						partDeleteSucessful(status);
					}
					deleteDataClear();
				}
				break;

			case AnalysisManager.EXCEPTION_DB_DELETE:
				mRemoveCount++;
				mRemoveFailedCount++;
				if (mRemoveCount == mRemoveList.size()) {
					if (mRemoveCount == mRemoveFailedCount) { // 全部失败
						status.setMessage(getResources().getString(
								R.string.delete_all_failed));
					} else { // 部分失败
						partDeleteSucessful(status);
					}
					deleteDataClear();
				}
				break;

			case AnalysisManager.SUCCESS_DB_UPDATE:
				MiscUtils.clone(mCurrentItem, mCurrentUpdateItem);
				mListAdapter.notifyDataSetChanged();
				break;
			}

			sendMessage(DISMISS_PROGRESS_DIALOG);
			
			if (status.getCode() != AnalysisManager.SUCCESS_DB_QUERY
					&& mRemoveCount == 0) {
				sendMessage(SHOW_TOAST, status.getMessage());
			}
		}

	};

	private void loadTenantData() {
		mTenantLists.clear();
		if (TenantCache.getTenantLists() != null
				&& TenantCache.getTenantLists().size() > 0) {
			mTenantLists.addAll(TenantCache.getTenantLists());
			mService.getLaunchCooperationList(mDataManager, UserCache.getTenantId());
		}
	}

	protected void doExtraCooperationInit() {
		mlistcooperations.clear();
		mlistcooperations.addAll(mListAdapter.getDataShowList());
		for (Cooperation cooperation : mlistcooperations) {
			for (Tenant tenant : mTenantLists) {
				if (tenant.getTenant_id() == cooperation.getAccept_company()) {
					cooperation.setCompany_type(tenant.getType());
					continue;
				}
			}
		}
	}

	@Override
	protected boolean doExtraAddFloatingMenuEvent() {
		mCurrentUpdateItem = new Cooperation();
		mbutton = mDialog.getFirstButton();

		mbutton.setVisibility(View.VISIBLE);
		mbutton.setText(R.string.cooperation_save_publish);
		mbutton.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View arg0) {
				Map<String, String> saveData = mDialog.SaveData();
				for (Map.Entry<String, String> entry : saveData.entrySet()) {
					if (entry.getValue().equals("")) {
						sendMessage(SHOW_TOAST,
								getString(R.string.pls_input_all_info));
						return;
					}
				}
				mProgressDialog.show();
				mProgressDialog.setContentView(R.layout.layout_progress);
				//TODO
				User user = new User();
				user.setTenant_id(mCurrentUpdateItem.getAccept_company() == 0 ? mCurrentItem.getAccept_company() : mCurrentUpdateItem.getAccept_company());
				RemoteUserService.getInstance().getTenantUsers(mGetPojectLeaderManager, user);

			}
		});
		return false;
	}

	CommonListInterface<Cooperation> mListInterface = new CommonListInterface<Cooperation>() {

		@Override
		public Map<String, Map<String, String>> getDisplayFieldsSwitchMap() {
			Map<String, Map<String, String>> fieldSwitchMap = new HashMap<String, Map<String, String>>();
			fieldSwitchMap
					.put(mDisplayFields[1], TenantCache.getTenantIdMaps());
			fieldSwitchMap.put(mDisplayFields[2],
					globalIdNameMap(GLOBAL.ENTERPRISE_TYPE));
			fieldSwitchMap.put(mDisplayFields[5],
					globalIdNameMap(GLOBAL.COORPERATION_STATUS));
			fieldSwitchMap.put(mDisplayFields[6], UserCache
					.getUserMaps());
			return fieldSwitchMap;
		}

		@Override
		public int getListItemId(Cooperation t) {
			return t.getCooperation_id();
		}

		@Override
		public String[] getDisplayFeilds() {
			return new String[] { "project_name", "accept_company",
					"company_type", "accept_date", "launch_date", "status",
					"launch_person" };
		}

		@Override
		public int getListHeaderLayoutId() {
			return R.layout.cooperation_initiator_list_item;
		}

		@Override
		public int getListLayoutId() {
			return R.layout.cooperation_initiator_list_item;
		}

		@Override
		public int getListHeaderNames() {
			return R.array.cooperation_initiator_names;
		}

		@Override
		public int getListHeaderIds() {
			return R.array.cooperation_initiator_ids;
		}

		@Override
		public int getListItemIds() {
			return R.array.cooperation_initiator_listitem_ids;
		}
	};

	ServiceInterface<Cooperation> mRequestInterface = new ServiceInterface<Cooperation>() {

		@Override
		public void getListData() {
			loadTenantData();
		}

		@Override
		public void addItem(Cooperation t) {
			Log.i("sss2", "112mProject" + mProject);
			Log.i("sss2", "113mTenant" + mTenant);
			t.setLaunch_project_id(mProject.getProject_id());
			t.setProject_name(mProject.getName());
			t.setLaunch_person(UserCache.getCurrentUser()
					.getUser_id());
			t.setTenant_id(UserCache.getCurrentUser()
					.getTenant_id());
			t.setLaunch_company(UserCache.getCurrentUser()
					.getTenant_id());
			t.setAccept_company(mTenant.getTenant_id());

			mService.AddCooperation(mDataManager, t);
		}

		@Override
		public void deleteItem(Cooperation t) {
			mService.deleteCooperation(mDataManager, t.getCooperation_id());
		}

		@Override
		public void updateItem(Cooperation t) {
			mService.updateLaunchCooperation(mDataManager, t);
		}
	};

	DialogAdapterInterface mDialogInterface = new DialogAdapterInterface() {

		@Override
		public String[] getUpdateFeilds() {
			return new String[] { "project_name", "accept_company",
					"company_type" };
		}

		@Override
		public int getDialogTitleId() {
			return R.string.cooperation_add_modify;
		}

		@Override
		public int getDialogLableNames() {
			return R.array.cooperation_maintain_pop;
		}

		@Override
		public Integer[] getImportantColumns(Map<String, String> saveDataMap) {
			return new Integer[] {0, 1, 2};
		}

		@SuppressLint("UseSparseArrays")
		@Override
		public Map<Integer, Integer> getDialogStyles() {
			Map<Integer, Integer> buttonStyle = new HashMap<Integer, Integer>();
			buttonStyle.put(0, BaseDialog.editTextReadOnlyLineStyle);
			buttonStyle.put(1, BaseDialog.editTextReadOnlyLineStyle);
			buttonStyle.put(2, BaseDialog.editTextReadOnlyLineStyle);
			return buttonStyle;
		}

		@Override
		public Map<Integer, String[]> getSupplyData() {
			return null;
		}

		@Override
		public void additionalInit(BaseDialog dialog) {
			OnClickListener projectlistener = new OnClickListener() {

				@Override
				public void onClick(View v) {
					Intent intent = new Intent();
					intent.setClass(getActivity(), ProjectSelectActivity.class);
					Bundle bundle = new Bundle();
					bundle.putInt("action", ProjectSelectActivity.ACTION_PICK);
					intent.putExtras(bundle);
					startActivityForResult(intent,
							CooperationInitiator.ADD_PROJECT_SELECT_REQUEST);
				}
			};

			OnClickListener unitlistener = new OnClickListener() {

				@Override
				public void onClick(View v) {
					if (mProject.getName() != null) {
						Intent intent = new Intent();
						intent.setClass(getActivity(), UnitSelectActivity.class);
						Bundle bundle = new Bundle();
						bundle.putString(UnitSelectActivity.TITLE_KEY,
								getString(R.string.combination_cooperation)); // 窗口名字
	                    bundle.putInt(UnitSelectActivity.PROJECT_KEY, mProject.getProject_id()); // 项目ID
	                    /** 标志位定义：
	                     *  TenantList：	获取系统所有租户信息 ：
	                     *  AllCooperationTenantList：	获取该公司合作过的所有公司信息
	                     *  UnCooperationTenantListByProject：	获取基于某个项目未合作的过的公司信息
	                     *  CooperationTenantListByProject：		获取基于某个项目的协作单位信息
	                     */
	                    bundle.putInt(UnitSelectActivity.ACTION_KEY, UnitSelectActivity.UnCooperationTenantListByProject); // action 
	                    bundle.putInt(UnitSelectActivity.TENANT_KEY, UserCache.getCurrentUser().getTenant_id());
	                    intent.putExtras(bundle);
						startActivityForResult(intent,
								CooperationInitiator.ADD_COMPANY_SELECT_REQUSET);
					} else {
						sendMessage(SHOW_TOAST,
								getString(R.string.pls_input_cooperation_project));
					}
					
				}
			};

			dialog.setEditTextStyle(0, R.drawable.floating_menu_update_version,
					projectlistener, null);
			dialog.setEditTextStyle(1, R.drawable.floating_menu_update_version,
					unitlistener, null);
		}

		@Override
		public Map<String, Map<String, String>> getUpdateFieldsSwitchMap() {

			Map<String, Map<String, String>> fieldSwitchMap = new HashMap<String, Map<String, String>>();
			fieldSwitchMap.put(mUpdateFields[1], TenantCache.getTenantIdMaps());
			fieldSwitchMap.put(mUpdateFields[2],
					globalIdNameMap(GLOBAL.ENTERPRISE_TYPE));

			return fieldSwitchMap;
		}
	};



	private OptionMenuInterface mOptionsMenuInterface = new OptionMenuInterface() {

		@Override
		public int getOptionMenuNames() {
			return R.array.cooperation_popup_attr;
		}

		@Override
		public SubMenuListener getOptionMenuClickListener() {
			SubMenuListener listener = new SubMenuListener() {

				@Override
				public void onSubMenuClick(View view) {
					mOptionsMenu.dismiss();
					switch ((Integer) view.getTag()) {
					case 0: // 发起
						mIsAddOperation = false;
						lunchTicket();
						break;
					case 1: // 修改
						mIsAddOperation = false;
						showUpdateDialog(true);
						break;
					case 2: // 删除
						commonConfirmDelete();
						break;
					default:
						break;
					}
				}

			};
			return listener;
		}
	};

	private void lunchTicket() {
		User user = new User();
		MiscUtils.clone(mCurrentUpdateItem, mCurrentItem);
		user.setTenant_id(mCurrentUpdateItem.getAccept_company());
		RemoteUserService.getInstance().getTenantUsers(mGetPojectLeaderManager, user);
	}

//	private void refreshData() {
//		mService.getLaunchCooperationList(mDataManager, UserCache
//				.getCurrentUser().getTenant_id());
//	}


	private OptionMenuInterface mlaunchedMenuInterface = new OptionMenuInterface() {

		@Override
		public int getOptionMenuNames() {
			return R.array.cooperation_lunched_popup_attr;
		}

		@Override
		public SubMenuListener getOptionMenuClickListener() {
			SubMenuListener listener = new SubMenuListener() {

				@Override
				public void onSubMenuClick(View view) {
					mOptionsMenu.dismiss();
					switch ((Integer) view.getTag()) {
					case 0: // 删除
						commonConfirmDelete();
						break;
                    case 1:
                        Intent intent = new Intent();
                        Bundle bundle = new Bundle();
                        intent.setClass(getActivity(),
                                StakeholderActivity.class);
                        bundle.putSerializable("bean", mCurrentItem);
                        bundle.putInt("type", 0); // 本单位
                        bundle.putInt("action", 0); // 发起 0
                        intent.putExtras(bundle);
                        startActivityForResult(intent, OWNER_REQUEST_CODE);
                        break;
                    case 2:
                        Intent intent2 = new Intent();
                        Bundle bundle2 = new Bundle();
                        intent2.setClass(getActivity(),
                                StakeholderActivity.class);
                        bundle2.putSerializable("bean", mCurrentItem);
                        bundle2.putInt("type", 1); // 本单位
                        bundle2.putInt("action", 0); // 发起 0
                        intent2.putExtras(bundle2);
                        startActivityForResult(intent2, OWNER_REQUEST_CODE);
                        break;
                    default:
                        break;
                    }
				}

			};
			return listener;
		}
	};
	
	private OptionMenuInterface mAcceptedMenuInterface = new OptionMenuInterface() {

		@Override
		public int getOptionMenuNames() {
			return R.array.cooperation_accepted_popup_attr;
		}

		@Override
		public SubMenuListener getOptionMenuClickListener() {
			SubMenuListener listener = new SubMenuListener() {

				@Override
				public void onSubMenuClick(View view) {
					mOptionsMenu.dismiss();
					
                    Intent intent = new Intent();
                    Bundle bundle = new Bundle();
                    intent.setClass(getActivity(), StakeholderActivity.class);
                    bundle.putSerializable("bean", mCurrentItem);
					switch ((Integer) view.getTag()) {
					case 0: 
	                    bundle.putInt("type", 0); // 本单位
	                    bundle.putInt("action", 0); // 发起 0
						break;
					case 1:
	                    bundle.putInt("type", 1); // 联系单位
	                    bundle.putInt("action", 0); // 发起0
						break;
					default:
						break;
					}
                    intent.putExtras(bundle);
                    startActivityForResult(intent, OWNER_REQUEST_CODE);
				}

			};
			return listener;
		}
	};

	@Override
	protected void switchOptionMenu() {
		if (mDialog != null && PermissionCache.hasSysPermission(COOPERATION_EDIT)) {
			switch (mCurrentItem.getStatus()) {
			case 1:
				mbutton.setVisibility(View.GONE);
				setOptionMenuInterface(mlaunchedMenuInterface);
				break;
			case 2:
				mbutton.setVisibility(View.GONE);
				setOptionMenuInterface(mAcceptedMenuInterface);
				break;
			case 3:
				mbutton.setVisibility(View.GONE);
				setOptionMenuInterface(mOptionsMenuInterface);
				break;
			default:
				break;
			}
		} else if (!PermissionCache.hasSysPermission(COOPERATION_EDIT)
				&& PermissionCache.hasSysPermission(COOPERATION_VIEW)) {
			mbutton.setVisibility(View.GONE);
			setOptionMenuInterface(mAcceptedMenuInterface);
		}
	}
	
	public void onActivityResult(int requestCode, int resultCode, Intent data) {
		if (resultCode == 0) {
			return;
		}
		if (requestCode == ADD_PROJECT_SELECT_REQUEST) {
			mProject = (Project) data.getSerializableExtra("project");

			if (mProject != null) {
				mDialog.setEditTextContent(0, mProject.getName());
				mCurrentUpdateItem.setLaunch_project_id(mProject.getProject_id());
				mCurrentUpdateItem.setProject_name(mProject.getName());
				mCurrentUpdateItem.setLaunch_person(UserCache
						.getCurrentUser().getUser_id());
			}

		} 
        if (requestCode == ADD_COMPANY_SELECT_REQUSET) {
			mTenant = (Tenant) data.getSerializableExtra("tenant");

			if (mTenant != null) {
				mDialog.setEditTextContent(1, mTenant.getName());
				if (mTenant.getType() != 0) {
					mDialog.setEditTextContent(2,
							GLOBAL.ENTERPRISE_TYPE[mTenant.getType() - 1][1]);
				}
				mCurrentUpdateItem.setLaunch_company(UserCache
						.getCurrentUser().getTenant_id());
				mCurrentUpdateItem.setTenant_id(UserCache.getCurrentUser()
						.getTenant_id());
				mCurrentUpdateItem.setAccept_company(mTenant.getTenant_id());
			}
		} 
        
        if (requestCode == OWNER_REQUEST_CODE) {
        	Cooperation coopertion = (Cooperation) data.getSerializableExtra("bean");
        	MiscUtils.clone(mCurrentItem, coopertion);
			mListAdapter.notifyDataSetChanged();
        }
	}
	
	@Override
	protected void initExtraEvent() {
		if (mFloatingMenu != null) {
			LayoutParams params = (LayoutParams) mFloatingMenu.getLayoutParams();
			params.rightMargin = getActivity().getResources().getDimensionPixelOffset(R.dimen.floatingmenu_width);
			mFloatingMenu.setLayoutParams(params);
		}
	}
}
