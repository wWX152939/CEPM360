/**
 * 
 */
package com.pm360.cepm360.app.module.settings;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.pm360.cepm360.R;
import com.pm360.cepm360.app.cache.UserCache;
import com.pm360.cepm360.app.common.custinterface.CommonListInterface;
import com.pm360.cepm360.app.common.custinterface.OptionMenuInterface;
import com.pm360.cepm360.app.common.custinterface.ServiceInterface;
import com.pm360.cepm360.app.common.custinterface.SimpleListInterface;
import com.pm360.cepm360.app.common.custinterface.TreeContentCombInterface;
import com.pm360.cepm360.app.common.view.parent.BaseTreeContentFragment;
import com.pm360.cepm360.common.GLOBAL;
import com.pm360.cepm360.entity.OBS;
import com.pm360.cepm360.entity.User;
import com.pm360.cepm360.services.system.RemoteOBSService;
import com.pm360.cepm360.services.system.RemoteUserService;

import java.util.HashMap;
import java.util.Map;

/**
 * @author wangzhiwei
 * 
 */
public class UserSelectFragment extends BaseTreeContentFragment<OBS, User> {
	public final static String NO_CORNER_KEY = "no_corner_key";
	public final static boolean CLOSE_CORNER = true;
	@SuppressWarnings("unused")
	private boolean mCornerType;
	
	// 树和内容远程服务
	private RemoteOBSService mTreeService;
	private RemoteUserService mContentService;
		
	// 实例化时初始化
	void prepareEnvironment() {
		// 调用该方法初始化父类数据
		initTreeList(OBS.class, 
					 treeListInterface, 
					 treeServiceInterface, 
					 (OptionMenuInterface) null, 
					 null);
		
		initContentList(User.class, 
						contentCommonListInterface, 
						treeContentCombInterface,
						contentServiceInterface, 
						null, 
						null, 
						null);
		
		enableMixMode();
		
		// 服务类对象初始化
		mTreeService = RemoteOBSService.getInstance();
		mContentService = RemoteUserService.getInstance();
	
	}
	
	private void initMsgData() {
		Bundle bundle = getArguments();
		if (bundle.getBoolean(NO_CORNER_KEY)) {
			mCornerType = CLOSE_CORNER;
		}
	}
		
	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {
		initMsgData();
		// 初始化权限
		setPermissionIdentity(GLOBAL.SYS_ACTION[21][0],
				GLOBAL.SYS_ACTION[20][0]);
				
		// 初始化实现部分数据
		prepareEnvironment();
		
		// 父类初始化
		View view = super.onCreateView(inflater, container, savedInstanceState);
		
		// 设置树视图域标题
		if (mTreeListTitle != null) {
		    mTreeListTitle.setText(R.string.obs_classification);
		}
		return view;
	}
	
	SimpleListInterface<OBS> treeListInterface = new SimpleListInterface<OBS>() {

		@Override
		public int getListItemId(OBS t) {
			return t.getId();
		}

		@Override
		public String[] getDisplayFeilds() {
			return new String[] {
					"name"
			};
		}
	};
	
	ServiceInterface<OBS> treeServiceInterface = new ServiceInterface<OBS>() {

		@Override
		public void getListData() {
			mTreeService.getOBSList(getTreeServiceHandler(), UserCache.getCurrentUser());
		}

		@Override
		public void addItem(OBS t) {
			
		}

		@Override
		public void deleteItem(OBS t) {
			
		}

		@Override
		public void updateItem(OBS t) {
			
		}
		
	};
	
	CommonListInterface<User> contentCommonListInterface = new CommonListInterface<User>() {
		
		@Override
		public int getListItemId(User t) {
			return t.getUser_id();
		}
		
		@Override
		public String[] getDisplayFeilds() {
			return new String[] {
					SERIAL_NUMBER,	
					"name", 
					"role", 		
					"login_name",		
			};
		}
		
		@Override
		public Map<String, Map<String, String>> getDisplayFieldsSwitchMap() {
			Map<String, Map<String, String>> map = new HashMap<String, Map<String, String>>();
			Map<String, String> subMap = new HashMap<String, String>();
			for (int i = 0; i < GLOBAL.ROLE_TYPE.length; i++) {
				subMap.put(GLOBAL.ROLE_TYPE[i][0], GLOBAL.ROLE_TYPE[i][1]);
			}
			map.put("role", subMap);
			return map;
		}
		
		@Override
		public int getListLayoutId() {
			return R.layout.user_select_listitem_layout;
		}
		
		@Override
		public int getListHeaderNames() {
			return R.array.user_select_list_name;
		}
		
		@Override
		public int getListHeaderLayoutId() {
			return R.layout.user_select_listitem_layout;
		}
		
		@Override
		public int getListHeaderIds() {
			return R.array.user_select_header_ids;
		}

		@Override
		public int getListItemIds() {
			return getListHeaderIds();
		}
	};
	
	ServiceInterface<User> contentServiceInterface = new ServiceInterface<User>() {
		
		@Override
		public void updateItem(User t) {
			
		}
		
		@Override
		public void getListData() {
			mContentService.getTenantUsers(getContentServiceHandler(), UserCache.getCurrentUser());
		}
		
		@Override
		public void deleteItem(User t) {
			
		}
		
		@Override
		public void addItem(User t) {

		}
	};
	
	TreeContentCombInterface<User> treeContentCombInterface = new TreeContentCombInterface<User>() {

		@Override
		public int getTreeContentCombId(User t) {
			return t.getObs_id();
		}
	};
}
