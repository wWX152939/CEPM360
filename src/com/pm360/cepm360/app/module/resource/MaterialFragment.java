package com.pm360.cepm360.app.module.resource;

import android.annotation.SuppressLint;
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
import com.pm360.cepm360.app.common.custinterface.SimpleDialogInterface;
import com.pm360.cepm360.app.common.custinterface.SimpleListInterface;
import com.pm360.cepm360.app.common.custinterface.TreeContentCombInterface;
import com.pm360.cepm360.app.common.view.parent.BaseDialog;
import com.pm360.cepm360.app.common.view.parent.BaseTreeContentFragment;
import com.pm360.cepm360.common.GLOBAL;
import com.pm360.cepm360.entity.P_WZ;
import com.pm360.cepm360.entity.P_WZ_DIR;
import com.pm360.cepm360.services.resource.RemoteGoodsService;

import java.util.HashMap;
import java.util.Map;

public class MaterialFragment extends BaseTreeContentFragment<P_WZ_DIR, P_WZ> {
	// 树和内容远程服务
	private RemoteGoodsService mTreeService;
	private RemoteGoodsService mContentService;
	
	Map<String, String> mUnitTypeMap = new HashMap<String, String>();
	Map<String, String> mTypeMap = new HashMap<String, String>();
	
	public Map<String, String> getTypeMap() {
		return mTypeMap;
	}
	
	// 实例化时初始化
	void prepareEnvironment() {
		// 调用该方法初始化父类数据
		initTreeList(P_WZ_DIR.class, 
					 treeListInterface, 
					 treeServiceInterface, 
					 (OptionMenuInterface) null, 
					 treeDialogInterface);
		
		initContentList(P_WZ.class, 
						contentCommonListInterface, 
						treeContentCombInterface,
						contentServiceInterface, 
						null, 
						null, 
						contentDialogAdapterInterface);
		
		// 服务类对象初始化
		mTreeService = RemoteGoodsService.getInstance();
		mContentService = mTreeService;
		
		String[][] typeStrings = GLOBAL.CL_TYPE;
		
		// 为物资类型（材料或设备）做映射
		for (int i = 0; i < typeStrings.length; i++) {
			mTypeMap.put(typeStrings[i][1], typeStrings[i][0]);
			mTypeMap.put(typeStrings[i][0], typeStrings[i][1]);
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
		    mTreeListTitle.setText(R.string.material_category);
		    mTreeListTitle.setTextColor(getResources().getColor(R.color.content_listview_header_text_color));
		}
		return view;
	}

	SimpleListInterface<P_WZ_DIR> treeListInterface = new SimpleListInterface<P_WZ_DIR>() {

		@Override
		public int getListItemId(P_WZ_DIR t) {
			return t.getId();
		}

		@Override
		public String[] getDisplayFeilds() {
			return new String[] {
					"name"
			};
		}
	};
	
	ServiceInterface<P_WZ_DIR> treeServiceInterface = new ServiceInterface<P_WZ_DIR>() {

		@Override
		public void getListData() {
			mTreeService.getWZ_DIRList( mTreeManager, 
			        UserCache.getCurrentUser().getTenant_id(), 
								GLOBAL.WZ_TYPE[0][0]);
		}

		@Override
		public void addItem(P_WZ_DIR t) {
			if (t != null) {
				t.setP_wz_dir_id(mCurrentTree.getWz_dir_id());
				t.setWz_type_1(Integer.parseInt(GLOBAL.WBS_TYPE[0][0]));
			} else {
				t = new P_WZ_DIR();
				t.setP_wz_dir_id(0);
				t.setWz_dir_id(1);
				t.setName(getResources().getString(R.string.all_material));
				t.setWz_type_1(Integer.parseInt(GLOBAL.WBS_TYPE[0][0]));
			}
			t.setTenant_id(UserCache.getCurrentUser().getTenant_id());
			mTreeService.addWZ_DIR(mTreeManager, t);
		}

		@Override
		public void deleteItem(P_WZ_DIR t) {
			mTreeService.deleteWZ_DIR(mTreeManager, t.getWz_dir_id());
		}

		@Override
		public void updateItem(P_WZ_DIR t) {
			mTreeService.updateWZ_DIR(mTreeManager, t);
		}
	};
	
	SimpleDialogInterface treeDialogInterface = new SimpleDialogInterface() {
		
		@Override
		public String[] getUpdateFeilds() {
			return new String[] {
					"name"
			};
		}
		
		@Override
		public int getDialogTitleId() {
			return R.string.add_modify_material_type;
		}
		
		@Override
		public int getDialogLableNames() {
			return R.array.material_type_maintain_pop;
		}

		@Override
		public Integer[] getImportantColumns(Map<String, String> saveDataMap) {
			return null;
		}
	};
	
	CommonListInterface<P_WZ> contentCommonListInterface = new CommonListInterface<P_WZ>() {
		
		@Override
		public int getListItemId(P_WZ t) {
			return t.getWz_id();
		}
		
		@Override
		public String[] getDisplayFeilds() {
			return new String[] {
					SERIAL_NUMBER, 	// 序号
					"name",			// 名称
					"wz_type_2", 	// 物资类型
					"spec",			// 规格
					"brand", 		// 品牌
					"unit",			// 单位
					"model_number" 	// 型号
			};
		}
		
		@Override
		public Map<String, Map<String, String>> getDisplayFieldsSwitchMap() {
			Map<String, Map<String, String>> fieldSwitchMap 
							= new HashMap<String, Map<String, String>>();
			fieldSwitchMap.put(mContentDisplayFields[2], mTypeMap);
			fieldSwitchMap.put(mContentDisplayFields[5], mUnitTypeMap);
			
			return fieldSwitchMap;
		}
		
		@Override
		public int getListLayoutId() {
			return R.layout.material_listitem_layout;
		}
		
		@Override
		public int getListHeaderNames() {
			return R.array.material_content_header_names;
		}
		
		@Override
		public int getListHeaderLayoutId() {
			return R.layout.material_listitem_layout;
		}
		
		@Override
		public int getListHeaderIds() {
			return R.array.material_content_header_ids;
		}

		@Override
		public int getListItemIds() {
			return getListHeaderIds();
		}
	};
	
	ServiceInterface<P_WZ> contentServiceInterface = new ServiceInterface<P_WZ>() {
		
		@Override
		public void updateItem(P_WZ t) {
			mContentService.updateWZ(mContentManager, t);
		}
		
		@Override
		public void getListData() {
			mContentService.getWZList(  mContentManager, 
			        UserCache.getCurrentUser().getTenant_id(), 
						GLOBAL.WZ_TYPE[0][0]);
		}
		
		@Override
		public void deleteItem(P_WZ t) {
			mContentService.deleteWZ(mContentManager, t.getWz_id());
		}
		
		@Override
		public void addItem(P_WZ t) {
			t.setTenant_id(UserCache.getCurrentUser().getTenant_id());
			t.setWz_dir_id(mCurrentTree.getId());
			mContentService.addWZ(mContentManager, t);
		}
	};
	
	DialogAdapterInterface contentDialogAdapterInterface = new DialogAdapterInterface() {
		
		@Override
		public String[] getUpdateFeilds() {
			return new String[] {
					"name",	// 名称
					"wz_type_2", // 物资类型
					"spec",	// 规格
					"unit",	// 单位
					"brand",// 品牌
					"model_number"	// 型号
			};
		}
		
		@Override
		public int getDialogTitleId() {
			return R.string.add_modify_material;
		}
		
		@Override
		public int getDialogLableNames() {
			return R.array.material_maintain_pop;
		}
		
		@Override
		public Integer[] getImportantColumns(Map<String, String> saveDataMap) {
			return null;
		}
		
		@Override
		public Map<String, Map<String, String>> getUpdateFieldsSwitchMap() {
			return contentCommonListInterface.getDisplayFieldsSwitchMap();
		}
		
		@SuppressLint("UseSparseArrays") @Override
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
	
	TreeContentCombInterface<P_WZ> treeContentCombInterface = new TreeContentCombInterface<P_WZ>() {

		@Override
		public int getTreeContentCombId(P_WZ t) {
			return t.getWz_dir_id();
		}
	};
}
