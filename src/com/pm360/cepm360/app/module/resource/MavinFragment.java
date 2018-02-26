package com.pm360.cepm360.app.module.resource;


import android.annotation.SuppressLint;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout.LayoutParams;

import com.pm360.cepm360.R;
import com.pm360.cepm360.app.CepmApplication;
import com.pm360.cepm360.app.cache.UserCache;
import com.pm360.cepm360.app.common.adpater.DataListAdapter;
import com.pm360.cepm360.app.common.custinterface.CommonListInterface;
import com.pm360.cepm360.app.common.custinterface.DialogAdapterInterface;
import com.pm360.cepm360.app.common.custinterface.OptionMenuInterface;
import com.pm360.cepm360.app.common.custinterface.ServiceInterface;
import com.pm360.cepm360.app.common.view.parent.BaseDialog;
import com.pm360.cepm360.app.common.view.parent.BaseListFragment;
import com.pm360.cepm360.common.GLOBAL;
import com.pm360.cepm360.entity.Mavin_lib;
import com.pm360.cepm360.services.resource.RemoteMavinLibService;

import java.util.HashMap;
import java.util.Map;

public class MavinFragment extends BaseListFragment<Mavin_lib> {
	protected static final String SERIAL_NUMBER = "serial";
	private RemoteMavinLibService mService = RemoteMavinLibService
			.getInstance();

	Map<String, String> mTypeMap = new HashMap<String, String>();
	Map<String, String> mSexMap = new HashMap<String, String>();

	/**
	 * 创建视图
	 */
	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {
		// 初始化类型和接口
		init(Mavin_lib.class, mListInterface, mRequestInterface, null,
				(OptionMenuInterface) null, mDialogInterface);

		setPermissionIdentity(GLOBAL.SYS_ACTION[21][0],
				GLOBAL.SYS_ACTION[20][0]);

		mApplication = (CepmApplication) getActivity().getApplication();
		
		for (int i = 0; i < GLOBAL.MAVIN_TYPE.length; i++) {
			mTypeMap.put(GLOBAL.MAVIN_TYPE[i][1], GLOBAL.MAVIN_TYPE[i][0]);
			mTypeMap.put(GLOBAL.MAVIN_TYPE[i][0], GLOBAL.MAVIN_TYPE[i][1]);
		}
		
		// 为单位做映射
		for (int i = 0; i < GLOBAL.SEX_TYPE.length; i++) {
			mSexMap.put(GLOBAL.SEX_TYPE[i][1], GLOBAL.SEX_TYPE[i][0]);
			mSexMap.put(GLOBAL.SEX_TYPE[i][0], GLOBAL.SEX_TYPE[i][1]);
		}

		View view = super.onCreateView(inflater, container, savedInstanceState);

		doExtraFloatingMenu();
		
		return view;

	}


	CommonListInterface<Mavin_lib> mListInterface = new CommonListInterface<Mavin_lib>() {

		@Override
		public Map<String, Map<String, String>> getDisplayFieldsSwitchMap() {
			Map<String, Map<String, String>> fieldSwitchMap = new HashMap<String, Map<String, String>>();
			fieldSwitchMap.put(mDisplayFields[2], mSexMap);
			fieldSwitchMap.put(mDisplayFields[6], mTypeMap);
			return fieldSwitchMap;
		}

		@Override
		public int getListItemId(Mavin_lib t) {
			return t.getMavin_lib_id();
		}

		@Override
		public String[] getDisplayFeilds() {
			return new String[] {
			SERIAL_NUMBER,
			"name",			// 姓名
			"sex", 	// 性别
			"birthday",			// 生日
			"work_start", 		// 工作时间
			"work_company",			// 单位
			"type", 	// 类型
			"tel", 
			"title", 
			"major",
			"degree",
			"mark"
			};
	}

		@Override
		public int getListHeaderLayoutId() {
			return R.layout.mavin_lib_listitem_layout;
		}

		@Override
		public int getListLayoutId() {
			return R.layout.mavin_lib_listitem_layout;
		}

		@Override
		public int getListHeaderNames() {
			return R.array.mavin_lib_header_names;
		}

		@Override
		public int getListHeaderIds() {
			return R.array.mavin_lib__header_ids;
		}

		@Override
		public int getListItemIds() {
			return R.array.mavin_lib__header_ids;
		}
	};

	ServiceInterface<Mavin_lib> mRequestInterface = new ServiceInterface<Mavin_lib>() {

		@Override
		public void getListData() {
			 mService.getMavinLibList(getServiceManager(), UserCache.getTenantId());
		}

		@Override
		public void addItem(Mavin_lib t) {
			t.setTenant_id(UserCache.getCurrentUser()
					.getTenant_id());
			mService.addMavin(getServiceManager(), t);
		}

		@Override
		public void deleteItem(Mavin_lib t) {
			mService.deleteMavin(getServiceManager(), t.getMavin_lib_id());
		}

		@Override
		public void updateItem(Mavin_lib t) {
			mService.updateMavin(getServiceManager(), t);
		}
	};

	DialogAdapterInterface mDialogInterface = new DialogAdapterInterface() {

		@Override
		public String[] getUpdateFeilds() {
			return new String[] {
			"name",			// 姓名
			"sex", 	// 性别
			"birthday",			// 生日
			"work_start", 		// 工作时间
			"work_company",			// 单位
			"type", 	// 类型
			"tel", 
			"title", 
			"major",
			"degree",
			"mark"
			};
	}

		@Override
		public int getDialogTitleId() {
			return R.string.add_mavin_management;
		}

		@Override
		public int getDialogLableNames() {
			return R.array.mavin_maintain_pop;
		}

		@Override
		public Integer[] getImportantColumns(Map<String, String> saveDataMap) {
			return new Integer[] {0, 1, 2, 3, 4, 5, 6, 7, 8, 9, 10};
		}

		@SuppressLint("UseSparseArrays")
		@Override
		public Map<Integer, Integer> getDialogStyles() {
			Map<Integer, Integer> styleMap = new HashMap<Integer, Integer>();
			styleMap.put(BASE_POSITION + 1, BaseDialog.spinnerLineStyle);
			styleMap.put(BASE_POSITION + 2, BaseDialog.calendarLineStyle);
			styleMap.put(BASE_POSITION + 3, BaseDialog.calendarLineStyle);
			styleMap.put(BASE_POSITION + 5, BaseDialog.spinnerLineStyle);
			styleMap.put(BASE_POSITION + 6, BaseDialog.numberEditTextLineStyle);
			styleMap.put(BASE_POSITION + 10, BaseDialog.remarkEditTextLineStyle);
			return styleMap;
		}

		@SuppressLint("UseSparseArrays")
		@Override
		public Map<Integer, String[]> getSupplyData() {
			Map<Integer, String[]> dataMap = new HashMap<Integer, String[]>();
			
			String[] typeStrings = new String[GLOBAL.SEX_TYPE.length];
			for (int i = 0; i < GLOBAL.SEX_TYPE.length; i++) {
				typeStrings[i] = GLOBAL.SEX_TYPE[i][1];
			}
			dataMap.put(BASE_POSITION + 1, typeStrings);
			
			typeStrings = new String[GLOBAL.MAVIN_TYPE.length];
			for (int i = 0; i < GLOBAL.MAVIN_TYPE.length; i++) {
				typeStrings[i] = GLOBAL.MAVIN_TYPE[i][1];
			}
			dataMap.put(BASE_POSITION + 5, typeStrings);
			
			return dataMap;
		}

		@Override
		public Map<String, Map<String, String>> getUpdateFieldsSwitchMap() {
			return mListInterface.getDisplayFieldsSwitchMap();
		}

		@Override
		public void additionalInit(BaseDialog dialog) {
			// TODO Auto-generated method stub
			
		}
	};
	
	protected void doExtraFloatingMenu() {
		if (mFloatingMenu != null) {
			LayoutParams params = (LayoutParams) mFloatingMenu.getLayoutParams();
			params.rightMargin = getActivity().getResources().getDimensionPixelOffset(R.dimen.dp116_w);
			mFloatingMenu.setLayoutParams(params);
		}
	}

	public DataListAdapter<Mavin_lib> getListAdapter() {
		
		return mListAdapter;
	}
}
