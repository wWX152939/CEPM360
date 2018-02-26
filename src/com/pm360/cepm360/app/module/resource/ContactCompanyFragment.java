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
import com.pm360.cepm360.entity.P_LWDW;
import com.pm360.cepm360.entity.P_LWDW_DIR;
import com.pm360.cepm360.services.resource.RemoteLWCompanyService;

import java.util.HashMap;
import java.util.Map;

public class ContactCompanyFragment extends BaseTreeContentFragment<P_LWDW_DIR, P_LWDW> {
	RemoteLWCompanyService mTreeService;
	RemoteLWCompanyService mContentService;
	
	Map<String, String> mTypeMap = new HashMap<String, String>();
	
	public Map<String, String> getTypeMap() {
		return mTypeMap;
	}
	
	// 实例化时初始化
	void prepareEnvironment() {
		initTreeList(P_LWDW_DIR.class, 
					 treeSimpleListInterface, 
					 treeServiceInterface, 
					 (OptionMenuInterface) null, 
					 treeSimpleDialogInterface);
		
		initContentList(P_LWDW.class, 
						contentCommonListInterface, 
						treeContentCombInterface, 
						contentServiceInterface, 
						null, 
						null, 
						contentDialogAdapterInterface);
		
		mTreeService = RemoteLWCompanyService.getInstance();
		mContentService = mTreeService;
		
		// 为往来单位类型做映射
		for (int i = 0; i < GLOBAL.LWDW_TYPE.length; i++) {
			mTypeMap.put(GLOBAL.LWDW_TYPE[i][1], GLOBAL.LWDW_TYPE[i][0]);
			mTypeMap.put(GLOBAL.LWDW_TYPE[i][0], GLOBAL.LWDW_TYPE[i][1]);
		}
	}
	
	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {
		// 初始化权限
		setPermissionIdentity(GLOBAL.SYS_ACTION[21][0],
				GLOBAL.SYS_ACTION[20][0]);
				
		// 先初始化实现部分数据
		prepareEnvironment();
		
		// 父类初始化
		View view = super.onCreateView(inflater, container, savedInstanceState);
		
		// 设置树视图域标题
		if (mTreeListTitle != null) {
		mTreeListTitle.setText(R.string.contact_company_category);
		mTreeListTitle.setTextColor(getResources().getColor(R.color.content_listview_header_text_color));
		}
		
		return view;
	}
	
	SimpleListInterface<P_LWDW_DIR> treeSimpleListInterface = new SimpleListInterface<P_LWDW_DIR>() {

		@Override
		public int getListItemId(P_LWDW_DIR t) {
			return t.getId();
		}

		@Override
		public String[] getDisplayFeilds() {
			return new String[] {
					"name"
			};
		}
	};

	ServiceInterface<P_LWDW_DIR> treeServiceInterface = new ServiceInterface<P_LWDW_DIR>() {

		@Override
		public void getListData() {
			mTreeService.getLWDW_DIRList(mTreeManager, 
			        UserCache.getCurrentUser().getTenant_id());
		}

		@Override
		public void addItem(P_LWDW_DIR t) {
			if (t != null) {
				t.setP_lwdw_dir_id(mCurrentTree.getLwdw_dir_id());
			} else {
				t = new P_LWDW_DIR();
				t.setLwdw_dir_id(1);
				t.setP_lwdw_dir_id(0);
				t.setName(getResources().getString(R.string.all_contact_company));
			}
			t.setTenant_id(UserCache.getCurrentUser().getTenant_id());
			mTreeService.addLWDW_DIR(mTreeManager, t);	
		}

		@Override
		public void deleteItem(P_LWDW_DIR t) {
			mTreeService.deleteLWDW_DIR(mTreeManager, t.getLwdw_dir_id());
		}

		@Override
		public void updateItem(P_LWDW_DIR t) {
			mTreeService.updateLWDW_DIR(mTreeManager, t);
		}
	};
	
	SimpleDialogInterface treeSimpleDialogInterface = new SimpleDialogInterface() {
		
		@Override
		public String[] getUpdateFeilds() {
			return treeSimpleListInterface.getDisplayFeilds();
		}
		
		@Override
		public int getDialogTitleId() {
			return R.string.add_modify_contact_company_type;
		}
		
		@Override
		public int getDialogLableNames() {
			return R.array.contact_company_type_maintain_pop;
		}

		@Override
		public Integer[] getImportantColumns(Map<String, String> saveDataMap) {
			return null;
		}
	};
	
	CommonListInterface<P_LWDW> contentCommonListInterface = new CommonListInterface<P_LWDW>() {

		@Override
		public Map<String, Map<String, String>> getDisplayFieldsSwitchMap() {
			return null;
		}

		@Override
		public int getListItemId(P_LWDW t) {
			return t.getLwdw_id();
		}

		@Override
		public String[] getDisplayFeilds() {
			return new String[] {
					SERIAL_NUMBER, 	// 序号
					"name",			// 名称
					"address",		// 地址
					"tel",			// 电话
					"key_person"	// 联系人
			};
		}

		@Override
		public int getListHeaderLayoutId() {
			return R.layout.contact_company_listitem_layout;
		}

		@Override
		public int getListLayoutId() {
			return R.layout.contact_company_listitem_layout;
		}

		@Override
		public int getListHeaderNames() {
			return R.array.contact_company_content_header_names;
		}

		@Override
		public int getListHeaderIds() {
			return R.array.contact_company_content_header_ids;
		}

		@Override
		public int getListItemIds() {
			return getListHeaderIds();
		}
	};
	
	ServiceInterface<P_LWDW> contentServiceInterface = new ServiceInterface<P_LWDW>() {

		@Override
		public void getListData() {
			mContentService.getLWDWList(mContentManager, 
			        UserCache.getCurrentUser().getTenant_id());
		}

		@Override
		public void addItem(P_LWDW t) {
			t.setLwdw_dir_id(mCurrentTree.getLwdw_dir_id());
			mContentService.addLWDW(mContentManager, t);
		}

		@Override
		public void deleteItem(P_LWDW t) {
			mContentService.deleteLWDW(mContentManager, t.getLwdw_id());
		}

		@Override
		public void updateItem(P_LWDW t) {
			mContentService.updateLWDW(mContentManager, t);
		}
	};

	DialogAdapterInterface contentDialogAdapterInterface = new DialogAdapterInterface() {
		
		@Override
		public String[] getUpdateFeilds() {
			return new String[] {
					"name",			// 名称
					"address",		// 地址
					"tel",			// 电话
					"key_person"	// 联系人
			};
		}
		
		@Override
		public int getDialogTitleId() {
			return R.string.add_modify_contact_company;
		}
		
		@Override
		public int getDialogLableNames() {
			return R.array.contact_company_maintain_pop;
		}
		
		@Override
		public Integer[] getImportantColumns(Map<String, String> saveDataMap) {
			return null;
		}
		
		@Override
		public Map<String, Map<String, String>> getUpdateFieldsSwitchMap() {
			return null;
		}
		
		@Override
		public Map<Integer, String[]> getSupplyData() {
			return null;
		}
		
		@SuppressLint("UseSparseArrays") @Override
		public Map<Integer, Integer> getDialogStyles() {
			Map<Integer, Integer> styleMap = new HashMap<Integer, Integer>();
			styleMap.put(BASE_POSITION + 2, BaseDialog.numberEditTextLineStyle);
			return styleMap;
		}
		
		@Override
		public void additionalInit(BaseDialog dialog) {
			
		}
	};
	
	TreeContentCombInterface<P_LWDW> treeContentCombInterface = new TreeContentCombInterface<P_LWDW>() {

		@Override
		public int getTreeContentCombId(P_LWDW t) {
			return t.getLwdw_dir_id();
		}
	};
}
