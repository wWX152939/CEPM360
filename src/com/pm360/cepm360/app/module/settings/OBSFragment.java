/**
 * 
 */
package com.pm360.cepm360.app.module.settings;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import com.pm360.cepm360.R;
import com.pm360.cepm360.app.CepmApplication;
import com.pm360.cepm360.app.cache.ObsCache;
import com.pm360.cepm360.app.cache.UserCache;
import com.pm360.cepm360.app.common.activity.DataManagerInterface;
import com.pm360.cepm360.app.common.custinterface.CommonListInterface;
import com.pm360.cepm360.app.common.custinterface.OptionMenuInterface;
import com.pm360.cepm360.app.common.custinterface.ServiceInterface;
import com.pm360.cepm360.app.common.custinterface.SimpleDialogInterface;
import com.pm360.cepm360.app.common.view.OptionsMenuView.SubMenuListener;
import com.pm360.cepm360.app.common.view.parent.BaseTreeListFragment;
import com.pm360.cepm360.app.utils.UtilTools;
import com.pm360.cepm360.app.utils.UtilTools.DeleteConfirmInterface;
import com.pm360.cepm360.common.GLOBAL;
import com.pm360.cepm360.common.ResultStatus;
import com.pm360.cepm360.common.json.AnalysisManager;
import com.pm360.cepm360.entity.OBS;
import com.pm360.cepm360.services.system.RemoteOBSService;

import java.util.List;
import java.util.Map;

/**
 * @author yuanlu
 * 
 */
public class OBSFragment extends BaseTreeListFragment<OBS> {

	private RemoteOBSService mService;
	private OBS mObs;

	/**
	 * 创建视图
	 */
	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {
		// 初始化类型和接口
		init(OBS.class, false, mListInterface, mRequestInterface, null, mOptionMenuInterface,
				mDialogInterface);

		setPermissionIdentity(GLOBAL.SYS_ACTION[25][0],
				GLOBAL.SYS_ACTION[24][0]);
		mService = RemoteOBSService.getInstance();
		mApplication = (CepmApplication) getActivity().getApplication();

		return super.onCreateView(inflater, container, savedInstanceState);
	}

	CommonListInterface<OBS> mListInterface = new CommonListInterface<OBS>() {

		@Override
		public Map<String, Map<String, String>> getDisplayFieldsSwitchMap() {
			return null;
		}

		@Override
		public int getListItemId(OBS t) {
			return t.getObs_id();
		}

		@Override
		public String[] getDisplayFeilds() {
			return new String[] { "code", "name", "tel" };
		}

		@Override
		public int getListHeaderLayoutId() {
			return R.layout.obs_list_item_head;
		}

		@Override
		public int getListLayoutId() {
			return R.layout.obs_list_item;
		}

		@Override
		public int getListHeaderNames() {
			return R.array.obs_names;
		}

		@Override
		public int getListHeaderIds() {
			return R.array.obs_ids;
		}

		@Override
		public int getListItemIds() {
			return R.array.obs_listitem_ids;
		}
	};

	@Override
	protected void doExtraGetServerData(ResultStatus status, List<?> list) {
		switch (status.getCode()) {

		// TODO
		case AnalysisManager.SUCCESS_DB_ADD:
			if (list != null && list.size() != 0) {
				for (Object object : list) {
					if (object instanceof OBS) {
						ObsCache.addObs((OBS) object);
					}
				}
			}
			break;

		case AnalysisManager.SUCCESS_DB_DEL:
			ObsCache.removeObs(mObs);
			break;

		case AnalysisManager.SUCCESS_DB_UPDATE:
			ObsCache.updateObs(mObs);
			break;
		}
		
		if (mCurrentMode == OperationMode.NORMAL && mFloatingMenu != null) {
			if (mListAdapter.getShowList().size() != 0) {
				mFloatingMenu.setVisibility(View.GONE);
			} else {
				mFloatingMenu.setVisibility(View.VISIBLE);
			}		
		}
	}

	ServiceInterface<OBS> mRequestInterface = new ServiceInterface<OBS>() {

		@Override
		public void getListData() {
			mService.getOBSList(getServiceManager(), UserCache
					.getCurrentUser());
		}

		@Override
		public void addItem(OBS t) {
			t.setTenant_id(UserCache.getCurrentUser()
					.getTenant_id());
			mService.addOBS(getServiceManager(), t);
			mObs = t;
		}

		@Override
		public void deleteItem(OBS t) {
			mService.deleteOBS(mDeleteInterface, t);
			mObs = t;
		}

		@Override
		public void updateItem(OBS t) {
			t.setTenant_id(UserCache.getCurrentUser()
					.getTenant_id());
			mService.updateOBS(getServiceManager(), t);
			mObs = t;
		}
	};
	
	DataManagerInterface mDeleteInterface = new DataManagerInterface() {
		
		@Override
		public void getDataOnResult(ResultStatus status, List<?> list) {
			Toast.makeText(getActivity(), status.getMessage(), Toast.LENGTH_SHORT).show();
			if (status.getCode() == AnalysisManager.SUCCESS_DB_DEL) {
				mListAdapter.deleteTreeNode();
				ObsCache.removeObs(mObs);
			}
		}
	};
	
	OptionMenuInterface mOptionMenuInterface = new OptionMenuInterface() {

		@Override
		public int getOptionMenuNames() {
			return R.array.obs_make_attr_popup;
		}

		@Override
		public SubMenuListener getOptionMenuClickListener() {
			return new SubMenuListener() {
				
				@Override
				public void onSubMenuClick(View view) {
					mOptionsMenu.dismiss();
					switch ((Integer) view.getTag()) {
						case 0:		// 添加
							mIsAddOperation = true;
							mIsInsertOperation = false;
							mIsFloatMenuAdd = false;
							optionMenuAddFunction();
							break;
						case 1:		// 修改
							mIsAddOperation = false;
							mIsInsertOperation = false;
							showUpdateDialog(true);
							break;
						case 2:
							if (!mIsDeleteParentNode && mCurrentItem.isHas_child()) {
								sendMessage(SHOW_TOAST, 
										getActivity().getResources().getString(R.string.EPSMaintain_no_delete));
							} else {
								UtilTools.deleteConfirm(getActivity(), new DeleteConfirmInterface() {
									
									@Override
									public void deleteConfirmCallback() {
										mRequestInterface.deleteItem(mCurrentItem);
									}
								});
							}
							break;
					}
				}
			};
		}
	};	

	SimpleDialogInterface mDialogInterface = new SimpleDialogInterface() {

		@Override
		public String[] getUpdateFeilds() {
			return mListInterface.getDisplayFeilds();
		}

		@Override
		public int getDialogTitleId() {
			return R.string.OBSMaintain_add_modify;
		}

		@Override
		public int getDialogLableNames() {
			return R.array.obs_names;
		}

		@Override
		public Integer[] getImportantColumns(Map<String, String> saveDataMap) {
			return new Integer[] {0, 1};
		}
	};
}
