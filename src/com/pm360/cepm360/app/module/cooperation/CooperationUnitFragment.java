package com.pm360.cepm360.app.module.cooperation;

import android.annotation.SuppressLint;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.pm360.cepm360.R;
import com.pm360.cepm360.app.common.custinterface.CommonListInterface;
import com.pm360.cepm360.app.common.custinterface.DialogAdapterInterface;
import com.pm360.cepm360.app.common.custinterface.OptionMenuInterface;
import com.pm360.cepm360.app.common.custinterface.ServiceInterface;
import com.pm360.cepm360.app.common.view.OptionsMenuView.SubMenuListener;
import com.pm360.cepm360.app.common.view.parent.BaseDialog;
import com.pm360.cepm360.app.common.view.parent.BaseListFragment;
import com.pm360.cepm360.common.GLOBAL;
import com.pm360.cepm360.entity.User;
import com.pm360.cepm360.services.system.RemoteUserService;

import java.util.HashMap;
import java.util.Map;

public class CooperationUnitFragment extends BaseListFragment<User> {
	
	/**
	 * 服务接口
	 */
	@SuppressWarnings("unused")
	private RemoteUserService mService = RemoteUserService.getInstance();
	
	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {
		
		/**
		 * 一定要再调用父对象onCreateView前调用初始化
		 */
		init(User.class, 
				mListInterface, 
				mServiceInterface, 
				null, 					// 启用默认的浮动菜单功能
				mOptionMenuInterface, 
				mDialogAdapterInterface);
		
		setPermissionIdentity(GLOBAL.SYS_ACTION[24][0],
				GLOBAL.SYS_ACTION[23][0]);
		return super.onCreateView(inflater, container, savedInstanceState);
	}

	/**
	 * 列表相关接口
	 */
	CommonListInterface<User> mListInterface = new CommonListInterface<User>() {

		@Override
		public Map<String, Map<String, String>> getDisplayFieldsSwitchMap() {
			Map<String, Map<String, String>> fieldSwitchMap 
								= new HashMap<String, Map<String, String>>();
			fieldSwitchMap.put(mDisplayFields[2], null);
			fieldSwitchMap.put(mDisplayFields[6], null);
			
			return fieldSwitchMap;
		}

		@Override
		public int getListItemId(User t) {
			return t.getUser_id();
		}

		@Override
		public String[] getDisplayFeilds() {
			return new String[] {
					SERIAL_NUMBER,
					"",				// 公司名称
					"",				// 公司类型
					"",				// 联系人
					"",				// 电话
					"",				// 地址
					""				// 子系统权限
			};
		}

		@Override
		public int getListHeaderLayoutId() {
			return R.layout.cooperation_list_layout;
		}

		@Override
		public int getListLayoutId() {
			return R.layout.cooperation_list_layout;
		}

		@Override
		public int getListHeaderNames() {
			return R.array.cooperation_list_title_names;
		}

		@Override
		public int getListHeaderIds() {
			return R.array.cooperation_list_item_ids;
		}

		@Override
		public int getListItemIds() {
			return R.array.cooperation_list_item_ids;
		}
	};
	
	/**
	 * 服务相关接口
	 */
	ServiceInterface<User> mServiceInterface = new ServiceInterface<User>() {

		@Override
		public void getListData() {
			// TODO Auto-generated method stub
			
		}

		@Override
		public void addItem(User t) {
			// TODO Auto-generated method stub
			
		}

		@Override
		public void deleteItem(User t) {
			// TODO Auto-generated method stub
			
		}

		@Override
		public void updateItem(User t) {
			// TODO Auto-generated method stub
			
		}
	};
	
	/**
	 * 选项菜单接口
	 */
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
	 * 对话框适配接口
	 */
	DialogAdapterInterface mDialogAdapterInterface = new DialogAdapterInterface() {
		
		@Override
		public Integer[] getImportantColumns(Map<String, String> saveDataMap) {
			return null;
		}
		
		@Override
		public String[] getUpdateFeilds() {
			return new String[] {
					"",				// 公司名称
					"",				// 公司类型
					"",				// 联系人
					"",				// 电话
					"",				// 地址
					""				// 子系统权限
			};
		}
		
		@Override
		public int getDialogTitleId() {
			return R.string.cooperation_dialog_title;
		}
		
		@Override
		public int getDialogLableNames() {
			return R.array.cooperation_dialog_lables;
		}
		
		@Override
		public Map<String, Map<String, String>> getUpdateFieldsSwitchMap() {
			return mListInterface.getDisplayFieldsSwitchMap();
		}
		
		@SuppressLint("UseSparseArrays") 
		@Override
		public Map<Integer, String[]> getSupplyData() {
			Map<Integer, String[]> dataMap = new HashMap<Integer, String[]>();
			String[][] typeArray = GLOBAL.CL_TYPE;
			
			String[] typeStrings = new String[typeArray.length];
			for (int i = 0; i < typeArray.length; i++) {
				typeStrings[i] = typeArray[i][1];
			}
			dataMap.put(BASE_POSITION + 1, typeStrings);
			
			typeStrings = new String[GLOBAL.UNIT_TYPE.length];
			for (int i = 0; i < GLOBAL.UNIT_TYPE.length; i++) {
				typeStrings[i] = GLOBAL.UNIT_TYPE[i][1];
			}
			dataMap.put(BASE_POSITION + 3, typeStrings);
			
			return dataMap;
		}
		
		@SuppressLint("UseSparseArrays") 
		@Override
		public Map<Integer, Integer> getDialogStyles() {
			Map<Integer, Integer> styleMap = new HashMap<Integer, Integer>();
			
			styleMap.put(2, BaseDialog.spinnerLineStyle);
			styleMap.put(6, BaseDialog.radioLineStyle);
			
			return styleMap;
		}
		
		@Override
		public void additionalInit(BaseDialog dialog) {
			
		}
	};
	
}
