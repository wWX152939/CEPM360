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
import com.pm360.cepm360.entity.P_WBRGNR;
import com.pm360.cepm360.entity.P_WBRGNR_DIR;
import com.pm360.cepm360.services.resource.RemoteSubContentService;

import java.util.HashMap;
import java.util.Map;

public class LabourOutSourcingFragment extends BaseTreeContentFragment<P_WBRGNR_DIR, P_WBRGNR> {
	RemoteSubContentService mTreeService;
	RemoteSubContentService mContentService;
	
	Map<String, String> mTypeMap = new HashMap<String, String>();
	
	public Map<String, String> getTypeMap() {
		return mTypeMap;
	}
	
	// 实例化时初始化
	void prepareEnvironment() {
		initTreeList(P_WBRGNR_DIR.class, 
					 treeSimpleListInterface, 
					 treeServiceInterface, 
					 (OptionMenuInterface) null, 
					 treeSimpleDialogInterface);
		
		initContentList(P_WBRGNR.class, 
						contentCommonListInterface, 
						treeContentCombInterface, 
						contentServiceInterface, 
						null, 
						null, 
						contentDialogAdapterInterface);
		
		mTreeService = RemoteSubContentService.getInstance();
		mContentService = mTreeService;
		
		// 为人工类型做映射
		for (int i = 0; i < GLOBAL.RG_TYPE.length; i++) {
			mTypeMap.put(GLOBAL.RG_TYPE[i][1], GLOBAL.RG_TYPE[i][0]);
			mTypeMap.put(GLOBAL.RG_TYPE[i][0], GLOBAL.RG_TYPE[i][1]);
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
		mTreeListTitle.setText(R.string.labour_outsourcing_category);
		mTreeListTitle.setTextColor(getResources().getColor(R.color.content_listview_header_text_color));
		}
		
		return view;
	}
	
	SimpleListInterface<P_WBRGNR_DIR> treeSimpleListInterface = new SimpleListInterface<P_WBRGNR_DIR>() {

		@Override
		public int getListItemId(P_WBRGNR_DIR t) {
			return t.getId();
		}

		@Override
		public String[] getDisplayFeilds() {
			return new String[] {
					"name"
			};
		}
	};
	
	ServiceInterface<P_WBRGNR_DIR> treeServiceInterface = new ServiceInterface<P_WBRGNR_DIR>() {

		@Override
		public void getListData() {
			mTreeService.getWBRGNR_DIRList(mTreeManager, 
			        UserCache.getCurrentUser().getTenant_id());
		}

		@Override
		public void addItem(P_WBRGNR_DIR t) {
			if (t != null) {
				t.setP_wbrgnr_dir_id(mCurrentTree.getWbrgnr_dir_id());
			} else {
				t = new P_WBRGNR_DIR();
				t.setWbrgnr_dir_id(1);
				t.setP_wbrgnr_dir_id(0);
				t.setName(getResources().getString(R.string.all_labour_outsourcing));
			}
			t.setTenant_id(UserCache.getCurrentUser().getTenant_id());
			mTreeService.addWBRGNR_DIR(mTreeManager, t);
		}

		@Override
		public void deleteItem(P_WBRGNR_DIR t) {
			mTreeService.deleteWBRGNR_DIR(mTreeManager, t.getWbrgnr_dir_id());
		}

		@Override
		public void updateItem(P_WBRGNR_DIR t) {
			mTreeService.updateWBRGNR_DIR(mTreeManager, t);
		}
	};
	
	SimpleDialogInterface treeSimpleDialogInterface = new SimpleDialogInterface() {
		
		@Override
		public String[] getUpdateFeilds() {
			return treeSimpleListInterface.getDisplayFeilds();
		}
		
		@Override
		public int getDialogTitleId() {
			return R.string.add_modify_outsourcing_type;
		}
		
		@Override
		public int getDialogLableNames() {
			return R.array.outsourcing_type_maintain_pop;
		}

		@Override
		public Integer[] getImportantColumns(Map<String, String> saveDataMap) {
			return null;
		}
	};
	
	CommonListInterface<P_WBRGNR> contentCommonListInterface = new CommonListInterface<P_WBRGNR>() {

		@Override
		public Map<String, Map<String, String>> getDisplayFieldsSwitchMap() {
			Map<String, Map<String, String>> fieldSwitchMap 
							= new HashMap<String, Map<String, String>>();
			fieldSwitchMap.put(mContentUpdateFields[0], mTypeMap);
			return fieldSwitchMap;
		}

		@Override
		public int getListItemId(P_WBRGNR t) {
			return t.getWbrgnr_id();
		}

		@Override
		public String[] getDisplayFeilds() {
			return new String[] {
					SERIAL_NUMBER, 	// 序号
					"type", // 类型
					"work"	// 分包内容
			};
		}

		@Override
		public int getListHeaderLayoutId() {
			return R.layout.outsourcing_content_listitem_layout;
		}

		@Override
		public int getListLayoutId() {
			return R.layout.outsourcing_content_listitem_layout;
		}

		@Override
		public int getListHeaderNames() {
			return R.array.outsourcing_content_header_names;
		}

		@Override
		public int getListHeaderIds() {
			return R.array.outsourcing_content_header_ids;
		}

		@Override
		public int getListItemIds() {
			return getListHeaderIds();
		}
	};
	
	ServiceInterface<P_WBRGNR> contentServiceInterface = new ServiceInterface<P_WBRGNR>() {

		@Override
		public void getListData() {
			mContentService.getWBRGNRList(mContentManager, 
			        UserCache.getCurrentUser().getTenant_id());
		}

		@Override
		public void addItem(P_WBRGNR t) {
			t.setWbrgnr_dir_id(mCurrentTree.getWbrgnr_dir_id());
			mContentService.addWBRGNR(mContentManager, t);
		}

		@Override
		public void deleteItem(P_WBRGNR t) {
			mContentService.deleteWBRGNR(mContentManager, t.getWbrgnr_id());
		}

		@Override
		public void updateItem(P_WBRGNR t) {
			mContentService.updateWBRGNR(mContentManager, t);
		}
	};
	
	DialogAdapterInterface contentDialogAdapterInterface = new DialogAdapterInterface() {
		
		@Override
		public String[] getUpdateFeilds() {
			return new String[] {
					"type", // 类型
					"work"	// 分包内容
			};
		}
		
		@Override
		public int getDialogTitleId() {
			return R.string.add_modify_outsourcing;
		}
		
		@Override
		public int getDialogLableNames() {
			return R.array.outsourcing_maintain_pop;
		}
		
		@Override
		public Integer[] getImportantColumns(Map<String, String> saveDataMap) {
			return null;
		}
		
		@Override
		public Map<String, Map<String, String>> getUpdateFieldsSwitchMap() {
			Map<String, Map<String, String>> fieldSwitchMap 
							= new HashMap<String, Map<String, String>>();
			fieldSwitchMap.put(mContentUpdateFields[0], mTypeMap);
			return fieldSwitchMap;
		}
		
		@SuppressLint("UseSparseArrays") @Override
		public Map<Integer, String[]> getSupplyData() {
			Map<Integer, String[]> dataMap = new HashMap<Integer, String[]>();
			
			String[] typeStrings = new String[GLOBAL.RG_TYPE.length];
			for (int i = 0; i < GLOBAL.RG_TYPE.length; i++) {
				typeStrings[i] = GLOBAL.RG_TYPE[i][1];
			}
			dataMap.put(BASE_POSITION, typeStrings);
			return dataMap;
		}
		
		@SuppressLint("UseSparseArrays") @Override
		public Map<Integer, Integer> getDialogStyles() {
			Map<Integer, Integer> styleMap = new HashMap<Integer, Integer>();
			styleMap.put(BASE_POSITION, BaseDialog.spinnerLineStyle);
			return styleMap;
		}
		
		@Override
		public void additionalInit(BaseDialog dialog) {
			
		}
	};
	
	TreeContentCombInterface<P_WBRGNR> treeContentCombInterface = new TreeContentCombInterface<P_WBRGNR>() {

		@Override
		public int getTreeContentCombId(P_WBRGNR t) {
			return t.getWbrgnr_dir_id();
		}
	};

}
