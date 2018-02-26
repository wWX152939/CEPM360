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
import com.pm360.cepm360.app.cache.EpsCache;
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
import com.pm360.cepm360.entity.EPS;
import com.pm360.cepm360.services.system.RemoteEPSService;

import java.util.List;
import java.util.Map;

/**
 * @author yuanlu
 * 
 */
public class EPSMaintainFragment extends BaseTreeListFragment<EPS> {
	private RemoteEPSService mService;
	private EPS mEps;

	/**
	 * 创建视图
	 */
	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {
		// 初始化类型和接口
		init(EPS.class, false, mListInterface, mRequestInterface, null, mOptionMenuInterface,
				mDialogInterface);

		setPermissionIdentity(GLOBAL.SYS_ACTION[25][0],
				GLOBAL.SYS_ACTION[24][0]);
		mService = RemoteEPSService.getInstance();
		mApplication = (CepmApplication) getActivity().getApplication();

		return super.onCreateView(inflater, container, savedInstanceState);
	}

	CommonListInterface<EPS> mListInterface = new CommonListInterface<EPS>() {

		@Override
		public Map<String, Map<String, String>> getDisplayFieldsSwitchMap() {
			return null;
		}

		@Override
		public int getListItemId(EPS t) {
			return t.getEps_id();
		}

		@Override
		public String[] getDisplayFeilds() {
			return new String[] { "code", "name" };
		}

		@Override
		public int getListHeaderLayoutId() {
			return R.layout.eps_maintain_list_item;
		}

		@Override
		public int getListLayoutId() {
			return R.layout.eps_maintain_list_item;
		}

		@Override
		public int getListHeaderNames() {
			return R.array.eps_names;
		}

		@Override
		public int getListHeaderIds() {
			return R.array.eps_ids;
		}

		@Override
		public int getListItemIds() {
			return R.array.eps_listitem_ids;
		}
	};

	@Override
	protected void doExtraGetServerData(ResultStatus status, List<?> list) {
		switch (status.getCode()) {

		// TODO
		case AnalysisManager.SUCCESS_DB_ADD:
			if (list != null && list.size() != 0) {
				for (Object object : list) {
					if (object instanceof EPS) {
						EpsCache.addEps((EPS) object);
					}
				}
			}
			break;

		case AnalysisManager.SUCCESS_DB_DEL:
			EpsCache.removeEps(mEps);
			break;

		case AnalysisManager.SUCCESS_DB_UPDATE:
			EpsCache.updateEps(mEps);
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

	ServiceInterface<EPS> mRequestInterface = new ServiceInterface<EPS>() {

		@Override
		public void getListData() {
			mService.getEPSList(getServiceManager(), UserCache
					.getCurrentUser());
		}

		@Override
		public void addItem(EPS t) {
			t.setTenant_id(UserCache.getCurrentUser()
					.getTenant_id());
			mService.addEPS(getServiceManager(), t);
			mEps = t;
		}

		@Override
		public void deleteItem(EPS t) {
			mService.deleteEPS(mDeleteInterface, t);
			mEps = t;
		}

		@Override
		public void updateItem(EPS t) {
			t.setTenant_id(UserCache.getCurrentUser()
					.getTenant_id());
			mService.updateEPS(getServiceManager(), t);
			mEps = t;
		}
	};
	
	DataManagerInterface mDeleteInterface = new DataManagerInterface() {
		
		@Override
		public void getDataOnResult(ResultStatus status, List<?> list) {
			Toast.makeText(getActivity(), status.getMessage(), Toast.LENGTH_SHORT).show();
			if (status.getCode() == AnalysisManager.SUCCESS_DB_DEL) {
				mListAdapter.deleteTreeNode();
				EpsCache.removeEps(mEps);
			}
		}
	};
	
	OptionMenuInterface mOptionMenuInterface = new OptionMenuInterface() {

		@Override
		public int getOptionMenuNames() {
			return R.array.eps_make_attr_popup;
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
			return R.string.EPSMaintain_add_modify;
		}

		@Override
		public int getDialogLableNames() {
			return R.array.eps_maintain_pop;
		}

		@Override
		public Integer[] getImportantColumns(Map<String, String> saveDataMap) {
			return new Integer[] {0, 1};
		}
	};
}