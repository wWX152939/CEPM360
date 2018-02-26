package com.pm360.cepm360.app.module.cooperation;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

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
import com.pm360.cepm360.app.module.message.MessageUtil;
import com.pm360.cepm360.app.utils.UtilTools;
import com.pm360.cepm360.common.GLOBAL;
import com.pm360.cepm360.common.ResultStatus;
import com.pm360.cepm360.common.json.AnalysisManager;
import com.pm360.cepm360.common.util.MiscUtils;
import com.pm360.cepm360.entity.Cooperation;
import com.pm360.cepm360.entity.Message;
import com.pm360.cepm360.entity.Project;
import com.pm360.cepm360.entity.Tenant;
import com.pm360.cepm360.services.cooperation.RemoteCooperationService;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class CooperationReceiver extends BaseListFragment<Cooperation> {
	private RemoteCooperationService mService = RemoteCooperationService
			.getInstance();
	private Project mProject = new Project();
	private ArrayList<Tenant> mTenantLists = new ArrayList<Tenant>();
	private ArrayList<Cooperation> mlistcooperations = new ArrayList<Cooperation>();
	private static final int ADD_PROJECT_SELECT_REQUEST = 100;
	private static final int OWNER_REQUEST_CODE = 200;
	
	private boolean mEnableOption = true;
	private static final String COOPERATION_EDIT = "17_1";
	private static final String COOPERATION_VIEW = "17_2";

	/**
	 * 创建视图
	 */
	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {
		Message msg = (Message) getArguments().getSerializable(GLOBAL.MSG_OBJECT_KEY);
		if (msg != null) {
			mSpecifiedItem = new Cooperation();
			mSpecifiedItem.setCooperation_id(msg.getType_id());
		}
		forceEnableOption(mEnableOption);
		// 初始化类型和接口
		init(Cooperation.class, mListInterface, mRequestInterface, null,
				mOptionsMenuInterface, mDialogInterface);

		setPermissionIdentity(GLOBAL.SYS_ACTION[37][0],
				GLOBAL.SYS_ACTION[36][0]);

		mApplication = (CepmApplication) getActivity().getApplication();

		View view = super.onCreateView(inflater, container, savedInstanceState);

		if (mDialog != null && mHasEditPermission) {
			mFloatingMenu.setVisibility(View.GONE);
		}

		return view;

	}

	/**
	 * 服务返回结果处理
	 */
	private DataManagerInterface mDataManager = new DataManagerInterface() {

		@SuppressWarnings("unchecked")
		@Override
		public void getDataOnResult(ResultStatus status, List<?> list) {
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
				sendMessage(DISMISS_PROGRESS_DIALOG);

				if (mSpecifiedItem != null) {
					locationSpecifiedItem(mSpecifiedItem);
				}

				doExtraCooperationInit();

				break;

			case AnalysisManager.SUCCESS_DB_ADD:
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
			mService.getAcceptCooperationList(mDataManager, UserCache.getTenantId());
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
			return new String[] { "project_name", "launch_company",
					"company_type", "launch_date", "accept_date", "status",
					"accept_person" };
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
			return R.array.cooperation_received_names;
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

		}

		@Override
		public void deleteItem(Cooperation t) {

		}

		@Override
		public void updateItem(Cooperation t) {

		}
	};

	DialogAdapterInterface mDialogInterface = new DialogAdapterInterface() {

		@Override
		public String[] getUpdateFeilds() {
			return mListInterface.getDisplayFeilds();
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
			return null;
		}

		@SuppressLint("UseSparseArrays")
		@Override
		public Map<Integer, Integer> getDialogStyles() {
			return null;
		}

		@Override
		public Map<Integer, String[]> getSupplyData() {
			// TODO Auto-generated method stub
			return null;
		}

		@Override
		public void additionalInit(BaseDialog dialog) {

		}

		@Override
		public Map<String, Map<String, String>> getUpdateFieldsSwitchMap() {
			return null;
		}
	};

	private OptionMenuInterface mOptionsMenuInterface = new OptionMenuInterface() {

		@Override
		public int getOptionMenuNames() {
			return R.array.cooperation_reveiver_popup_attr;
		}

		@Override
		public SubMenuListener getOptionMenuClickListener() {
			SubMenuListener listener = new SubMenuListener() {

				@Override
				public void onSubMenuClick(View view) {
					mOptionsMenu.dismiss();
					switch ((Integer) view.getTag()) {
					case 0: // 接受
						Intent intent = new Intent();
						intent.setClass(getActivity(),
								ProjectSelectActivity.class);
						Bundle bundle = new Bundle();
						bundle.putInt("action",
								ProjectSelectActivity.ACTION_PICK);
						intent.putExtras(bundle);
						startActivityForResult(intent,
								CooperationReceiver.ADD_PROJECT_SELECT_REQUEST);	
						break;
					default:
						break;
					}
				}

			};
			return listener;
		}
	};

	public void onActivityResult(int requestCode, int resultCode, Intent data) {
		super.onActivityResult(requestCode, resultCode, data);
		if (resultCode != Activity.RESULT_OK) {
			return;
		}

		if (requestCode == OWNER_REQUEST_CODE) {
			Cooperation coopertion = (Cooperation) data
					.getSerializableExtra("bean");
			MiscUtils.clone(mCurrentItem, coopertion);
			mListAdapter.notifyDataSetChanged();
		}

		if (requestCode == ADD_PROJECT_SELECT_REQUEST) {
			mProject = (Project) data.getSerializableExtra("project");
			List<Cooperation> projectList = mListAdapter.getDataShowList();
			List<Integer> acceptProjectList = new ArrayList<Integer>();
			for (int i = 0; i < projectList.size(); i++) {
				if (projectList.get(i).getStatus() == 2 && projectList.get(i).getLaunch_company() == mCurrentItem.getLaunch_company()) {
					acceptProjectList.add(projectList.get(i).getAccept_project_id());
				}
			}

			for (int i = 0; i < acceptProjectList.size(); i++) {
				if ((mProject.getProject_id()+"").equals(acceptProjectList.get(i)+"")){
					UtilTools.showToast(getActivity(), R.string.cooperation_already_accepted);
					return ;
				}
			}
			if (mProject != null) {
				mCurrentItem.setAccept_person(UserCache
						.getCurrentUser().getUser_id());
				mCurrentItem.setAccept_project_id(mProject.getProject_id());
				final int msgId =  MessageUtil.getMessageId(getActivity(), 32, mCurrentItem.getCooperation_id());
				mCurrentItem.setMessage_id(msgId);
				mService.acceptCooperation(new DataManagerInterface() {
					@Override
					public void getDataOnResult(ResultStatus status,
							List<?> list) {
						
						if (status.getCode() != AnalysisManager.SUCCESS_DB_UPDATE
								&& status.getMessage() != null
								&& !status.getMessage().equals("")) {
							UtilTools.showToast(getActivity(),
									status.getMessage());
						}
						if (status.getCode() == AnalysisManager.SUCCESS_DB_UPDATE) {
							if (list != null && list.size() > 0) {
								MessageUtil.setMessageToProcessed(getActivity(), msgId);
								refreshData();
							}
						}
					}
				}, mCurrentItem);	
			}
		}

	}

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
                    intent.setClass(getActivity(),
                            StakeholderActivity.class);
                    bundle.putSerializable("bean", mCurrentItem);
					switch ((Integer) view.getTag()) {
					case 0:
						bundle.putInt("type", 0); // 本单位
						bundle.putInt("action", 1); // 接收 1
						break;
					case 1: // 联系单位					    
                        bundle.putInt("type", 1); // 联系单位
                        bundle.putInt("action", 1); // 接收 1
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

	private void refreshData() {
		mService.getAcceptCooperationList(mDataManager, UserCache
				.getCurrentUser().getTenant_id());
	}

	@Override
	protected void switchOptionMenu() {
		if (mDialog != null && PermissionCache.hasSysPermission(COOPERATION_EDIT)) {
			if (mCurrentItem.getStatus() == 1 && UserCache.getCurrentUser().getRole().equals("XMJL")) {
				setOptionMenuInterface(mOptionsMenuInterface);
			} else if (mCurrentItem.getStatus() == 2) {
				setOptionMenuInterface(mAcceptedMenuInterface);
			} else {
				mOptionsMenu = null;
			}
		} else if (!PermissionCache.hasSysPermission(COOPERATION_EDIT)
				&& PermissionCache.hasSysPermission(COOPERATION_VIEW)) {
			setOptionMenuInterface(mAcceptedMenuInterface);
		}
	}

}
