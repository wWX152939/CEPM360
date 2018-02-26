package com.pm360.cepm360.app.module.lease;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.pm360.cepm360.R;
import com.pm360.cepm360.app.cache.UserCache;
import com.pm360.cepm360.app.common.custinterface.CommonListInterface;
import com.pm360.cepm360.app.common.custinterface.DialogAdapterInterface;
import com.pm360.cepm360.app.common.custinterface.OptionMenuInterface;
import com.pm360.cepm360.app.common.custinterface.ServiceInterface;
import com.pm360.cepm360.app.common.view.OptionsMenuView.SubMenuListener;
import com.pm360.cepm360.app.common.view.parent.BaseDialog;
import com.pm360.cepm360.app.common.view.parent.BaseListFragment;
import com.pm360.cepm360.common.GLOBAL;
import com.pm360.cepm360.entity.P_ZLRD;
import com.pm360.cepm360.services.lease.RemoteZLHService;

import java.util.HashMap;
import java.util.Map;

public class SelectZLRDFragment extends BaseListFragment<P_ZLRD> {

	private RemoteZLHService mService;

	/**
	 * 创建视图
	 */
	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {
		// 初始化类型和接口
		init(P_ZLRD.class, mListInterface, mRequestInterface,
				mOptionMenuInterface, mDialogInterface);

		setPermissionIdentity(GLOBAL.SYS_ACTION[13][0],
				GLOBAL.SYS_ACTION[12][0]);
		mService = RemoteZLHService.getInstance();
		enableInnerButton(true);
		return super.onCreateView(inflater, container, savedInstanceState);
	}

	/**
	 * 列表接口，该接口实现的内容比较杂，主要用来提供资源
	 */
	CommonListInterface<P_ZLRD> mListInterface = new CommonListInterface<P_ZLRD>() {
		@Override
		public int getListItemId(P_ZLRD t) {
			return t.getZlrd_id();
		}

		@Override
		public String[] getDisplayFeilds() {
			return new String[] { SERIAL_NUMBER, "zlr_number", "zl_type",
					"zl_name", "zl_spec", "remainder_number", "rent",
					"operator", "lease_date", "end_date" };
		}

		@Override
		public int getListHeaderLayoutId() {
			return R.layout.select_zlrd_header;
		}

		@Override
		public int getListLayoutId() {
			return getListHeaderLayoutId();
		}

		@Override
		public int getListHeaderNames() {
			return R.array.select_lease_header_title_names;
		}

		@Override
		public int getListHeaderIds() {
			return R.array.select_lease_header_title_ids;
		}

		@Override
		public Map<String, Map<String, String>> getDisplayFieldsSwitchMap() {
			Map<String, Map<String, String>> fieldSwitchMap = new HashMap<String, Map<String, String>>();
			fieldSwitchMap.put(mDisplayFields[2],
					globalIdNameMap(GLOBAL.SB_TYPE));
			fieldSwitchMap.put(mDisplayFields[7], UserCache
					.getUserMaps());
			return fieldSwitchMap;
		}

		@Override
		public int getListItemIds() {
			return getListHeaderIds();
		}
	};

	/**
	 * 服务请求接口实现
	 */
	ServiceInterface<P_ZLRD> mRequestInterface = new ServiceInterface<P_ZLRD>() {

		@Override
		public void getListData() {
			mService.getNeedZLHList(getServiceManager(), UserCache.getTenantId());
		}

		@Override
		public void addItem(P_ZLRD t) {

		}

		@Override
		public void deleteItem(P_ZLRD t) {

		}

		@Override
		public void updateItem(P_ZLRD t) {
			// mService.SetFlow(getServiceManager(), t);
		}
	};

	OptionMenuInterface mOptionMenuInterface = new OptionMenuInterface() {

		@Override
		public int getOptionMenuNames() {
			// TODO Auto-generated method stub
			return 0;
		}

		@Override
		public SubMenuListener getOptionMenuClickListener() {
			// TODO Auto-generated method stub
			return null;
		}
	};
	/**
	 * 对话框风格，数据，映射及额外初始化接口实现
	 */
	DialogAdapterInterface mDialogInterface = new DialogAdapterInterface() {

		@Override
		public Integer[] getImportantColumns(Map<String, String> saveDataMap) {
			return null;
		}

		@Override
		public String[] getUpdateFeilds() {
			// TODO Auto-generated method stub
			return null;
		}

		@Override
		public int getDialogTitleId() {
			// TODO Auto-generated method stub
			return 0;
		}

		@Override
		public int getDialogLableNames() {
			// TODO Auto-generated method stub
			return 0;
		}

		@Override
		public Map<String, Map<String, String>> getUpdateFieldsSwitchMap() {
			// TODO Auto-generated method stub
			return null;
		}

		@Override
		public Map<Integer, String[]> getSupplyData() {
			// TODO Auto-generated method stub
			return null;
		}

		@Override
		public Map<Integer, Integer> getDialogStyles() {
			// TODO Auto-generated method stub
			return null;
		}

		@Override
		public void additionalInit(BaseDialog dialog) {
			// TODO Auto-generated method stub

		}
	};
	//
	// @Override
	// public void onActivityResult(int requestCode, int resultCode, Intent
	// data) {
	// if (resultCode == Activity.RESULT_OK) {
	// User user = (User) data.getSerializableExtra("user");
	// if (user != null) {
	// mDialog.setEditTextContent(requestCode, user.getName());
	// } else {
	// mDialog.setEditTextContent(requestCode, "");
	// }
	// }
	//
	// super.onActivityResult(requestCode, resultCode, data);
	// }
}
