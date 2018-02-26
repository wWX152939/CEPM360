package com.pm360.cepm360.app.module.resource;

import android.annotation.SuppressLint;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.pm360.cepm360.R;
import com.pm360.cepm360.app.cache.UserCache;
import com.pm360.cepm360.app.common.custinterface.CommonListInterface;
import com.pm360.cepm360.app.common.custinterface.DialogAdapterInterface;
import com.pm360.cepm360.app.common.custinterface.OptionMenuInterface;
import com.pm360.cepm360.app.common.custinterface.ServiceInterface;
import com.pm360.cepm360.app.common.custinterface.SimpleDialogInterface;
import com.pm360.cepm360.app.common.custinterface.SimpleListInterface;
import com.pm360.cepm360.app.common.custinterface.TreeContentCombInterface;
import com.pm360.cepm360.app.common.view.parent.BaseDialog;
import com.pm360.cepm360.app.common.view.parent.BaseTreeContentFragment;
import com.pm360.cepm360.common.GLOBAL;
import com.pm360.cepm360.entity.P_ZL;
import com.pm360.cepm360.entity.P_ZL_DIR;
import com.pm360.cepm360.services.resource.RemoteZLService;

import java.util.HashMap;
import java.util.Map;

public class LeaseManagementFragment extends BaseTreeContentFragment<P_ZL_DIR, P_ZL> {
	RemoteZLService mTreeService;
	RemoteZLService mContentService;
	
	Map<String, String> mTypeMap = new HashMap<String, String>();
	Map<String, String> mUnitTypeMap = new HashMap<String, String>();
	
	public Map<String, String> getTypeMap() {
		return mTypeMap;
	}
	
	// 实例化时初始化
	void prepareEnvironment() {
		initTreeList(P_ZL_DIR.class, 
					 treeSimpleListInterface, 
					 treeServiceInterface, 
					 (OptionMenuInterface) null, 
					 treeSimpleDialogInterface);
		
		initContentList(P_ZL.class, 
						contentCommonListInterface, 
						treeContentCombInterface, 
						contentServiceInterface, 
						null, 
						null, 
						contentDialogAdapterInterface);
		
		mTreeService = RemoteZLService.getInstance();
		mContentService = mTreeService;
		
		// 为租赁类型做映射
		for (int i = 0; i < GLOBAL.SB_TYPE.length; i++) {
			mTypeMap.put(GLOBAL.SB_TYPE[i][1], GLOBAL.SB_TYPE[i][0]);
			mTypeMap.put(GLOBAL.SB_TYPE[i][0], GLOBAL.SB_TYPE[i][1]);
		}
		
		// 为单位做映射
		for (int i = 0; i < GLOBAL.UNIT_TYPE.length; i++) {
			mUnitTypeMap.put(GLOBAL.UNIT_TYPE[i][1], GLOBAL.UNIT_TYPE[i][0]);
			mUnitTypeMap.put(GLOBAL.UNIT_TYPE[i][0], GLOBAL.UNIT_TYPE[i][1]);
		}
	}
	
	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {
		// 初始化权限
		setPermissionIdentity(GLOBAL.SYS_ACTION[21][0],
				GLOBAL.SYS_ACTION[20][0]);
				
		// 初始化实现部分数据
		prepareEnvironment();
		
		// 父类初始化
		View view = super.onCreateView(inflater, container, savedInstanceState);
		
		// 设置树视图域标题
		if (mTreeListTitle != null) {
		mTreeListTitle.setText(R.string.lease_company_category);
		mTreeListTitle.setTextColor(getResources().getColor(R.color.content_listview_header_text_color));
		}
		
		return view;
	}
	
	SimpleListInterface<P_ZL_DIR> treeSimpleListInterface = new SimpleListInterface<P_ZL_DIR>() {

		@Override
		public int getListItemId(P_ZL_DIR t) {
			return t.getZl_dir_id();
		}

		@Override
		public String[] getDisplayFeilds() {
			return new String[] {
					"name"
			};
		}
	};
	
	ServiceInterface<P_ZL_DIR> treeServiceInterface = new ServiceInterface<P_ZL_DIR>() {

		@Override
		public void getListData() {
			mTreeService.getZL_DIRList(mTreeManager, 
			        UserCache.getCurrentUser().getTenant_id());
		}

		@Override
		public void addItem(P_ZL_DIR t) {
			if (t != null) {
				Log.i ("sss2", "mCurrentTree " + mCurrentTree.getZl_dir_id());
				t.setP_zl_dir_id(mCurrentTree.getZl_dir_id());
				Log.i ("sss2", "name " + t);

			} else {
				t = new P_ZL_DIR();
				t.setZl_dir_id(1);
				t.setP_zl_dir_id(0);
				t.setName(getResources().getString(R.string.all_contact_company));
			}
			t.setTenant_id(UserCache.getCurrentUser().getTenant_id());
			Log.i ("sss2", "P_ZL_DIR " + t);
			mTreeService.addZL_DIR(mTreeManager, t);
		}

		@Override
		public void deleteItem(P_ZL_DIR t) {
			mTreeService.deleteZL_DIR(mTreeManager, t.getZl_dir_id());
		}

		@Override
		public void updateItem(P_ZL_DIR t) {
			mTreeService.updateZL_DIR(mTreeManager, t);
		}
	};
	
	SimpleDialogInterface treeSimpleDialogInterface = new SimpleDialogInterface() {
		
		@Override
		public String[] getUpdateFeilds() {
			return treeSimpleListInterface.getDisplayFeilds();
		}
		
		@Override
		public int getDialogTitleId() {
			return R.string.add_modify_lease_type;
		}
		
		@Override
		public int getDialogLableNames() {
			return R.array.lease_maintain_pop;
		}

		@Override
		public Integer[] getImportantColumns(Map<String, String> saveDataMap) {
			return null;
		}
	};
	
	CommonListInterface<P_ZL> contentCommonListInterface = new CommonListInterface<P_ZL>() {

		@Override
		public Map<String, Map<String, String>> getDisplayFieldsSwitchMap() {
			Map<String, Map<String, String>> fieldSwitchMap 
							= new HashMap<String, Map<String, String>>();
			fieldSwitchMap.put(mContentUpdateFields[1], mTypeMap);
			fieldSwitchMap.put(mContentDisplayFields[4], mUnitTypeMap);
			return fieldSwitchMap;
		}

		@Override
		public int getListItemId(P_ZL t) {
			return t.getZl_id();
		}

		@Override
		public String[] getDisplayFeilds() {
			return new String[] {
					SERIAL_NUMBER, 	// 序号
					"name",			// 名称
					"zl_type",		// 类型
					"spec",			// 规格
					"unit"	// 单位
			};
		}

		@Override
		public int getListHeaderLayoutId() {
			return R.layout.lease_listitem_layout;
		}

		@Override
		public int getListLayoutId() {
			return R.layout.lease_listitem_layout;
		}

		@Override
		public int getListHeaderNames() {
			return R.array.lease_content_header_names;
		}

		@Override
		public int getListHeaderIds() {
			return R.array.lease_content_header_ids;
		}

		@Override
		public int getListItemIds() {
			return getListHeaderIds();
		}
	};
	
	ServiceInterface<P_ZL> contentServiceInterface = new ServiceInterface<P_ZL>() {

		@Override
		public void getListData() {
			mContentService.getZLList(mContentManager, 
			        UserCache.getCurrentUser().getTenant_id());
		}

		@Override
		public void addItem(P_ZL t) {
			t.setZl_dir_id(mCurrentTree.getZl_dir_id());
			t.setTenant_id(UserCache.getCurrentUser().getTenant_id());
			mContentService.addZL(mContentManager, t);
		}

		@Override
		public void deleteItem(P_ZL t) {
			mContentService.deleteZL(mContentManager, t.getZl_id());
		}

		@Override
		public void updateItem(P_ZL t) {
			mContentService.updateZL(mContentManager, t);
		}
	};
	
	DialogAdapterInterface contentDialogAdapterInterface = new DialogAdapterInterface() {
		
		@Override
		public String[] getUpdateFeilds() {
			return new String[] {
					"name",			// 名称
					"zl_type",		// 类型
					"spec",			// 规格
					"unit"	// 单位
			};
		}
		
		@Override
		public int getDialogTitleId() {
			return R.string.add_modify_lease_type;
		}
		
		@Override
		public int getDialogLableNames() {
			return R.array.add_lease_maintain_pop;
		}
		
		@Override
		public Integer[] getImportantColumns(Map<String, String> saveDataMap) {
			return null;
		}
		
		@Override
		public Map<String, Map<String, String>> getUpdateFieldsSwitchMap() {
//			Map<String, Map<String, String>> fieldSwitchMap 
//							= new HashMap<String, Map<String, String>>();
//			fieldSwitchMap.put(mContentUpdateFields[1], mTypeMap);
			return contentCommonListInterface.getDisplayFieldsSwitchMap();
		}
		
		@SuppressLint("UseSparseArrays") @Override
		public Map<Integer, String[]> getSupplyData() {
			Map<Integer, String[]> dataMap = new HashMap<Integer, String[]>();
			
			String[] typeStrings = new String[GLOBAL.SB_TYPE.length];
			for (int i = 0; i < GLOBAL.SB_TYPE.length; i++) {
				typeStrings[i] = GLOBAL.SB_TYPE[i][1];
			}
			dataMap.put(BASE_POSITION + 1, typeStrings);
			typeStrings = new String[GLOBAL.UNIT_TYPE.length];
			for (int i = 0; i < GLOBAL.UNIT_TYPE.length; i++) {
				typeStrings[i] = GLOBAL.UNIT_TYPE[i][1];
			}
			dataMap.put(BASE_POSITION + 3, typeStrings);
			return dataMap;
		}
		
		@SuppressLint("UseSparseArrays") @Override
		public Map<Integer, Integer> getDialogStyles() {
			Map<Integer, Integer> styleMap = new HashMap<Integer, Integer>();
			styleMap.put(BASE_POSITION + 1, BaseDialog.spinnerLineStyle);
			styleMap.put(BASE_POSITION + 3, BaseDialog.spinnerLineStyle);
			return styleMap;
		}
		
		@Override
		public void additionalInit(BaseDialog dialog) {
			
		}
	};
	
	TreeContentCombInterface<P_ZL> treeContentCombInterface = new TreeContentCombInterface<P_ZL>() {

		@Override
		public int getTreeContentCombId(P_ZL t) {
			return t.getZl_dir_id();
		}
	};

}
