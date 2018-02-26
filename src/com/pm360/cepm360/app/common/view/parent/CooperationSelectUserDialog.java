package com.pm360.cepm360.app.common.view.parent;

import java.util.HashMap;
import java.util.Map;

import android.app.Activity;

import com.pm360.cepm360.R;
import com.pm360.cepm360.app.cache.ObsCache;
import com.pm360.cepm360.app.cache.TenantCache;
import com.pm360.cepm360.app.cache.UserCache;
import com.pm360.cepm360.app.common.custinterface.CommonListInterface;
import com.pm360.cepm360.app.common.custinterface.ServiceInterface;
import com.pm360.cepm360.app.common.view.parent.list.SimpleList;
import com.pm360.cepm360.entity.Cooperation;
import com.pm360.cepm360.entity.User;
import com.pm360.cepm360.services.system.RemoteUserService;

public class CooperationSelectUserDialog extends BaseDialogStyle {

	private SimpleList<User> mSimpleList;
	private Cooperation mCooperation;
	public CooperationSelectUserDialog(Activity activity, Cooperation cooperation) {
		super(activity);
		mCooperation = cooperation;
		mSimpleList = new SimpleList<>(activity);
	}
	
	public User getCurrentItem() {
		return mSimpleList.getCurrentItem();
	}
	
	public void show() {
		mSimpleList.init(User.class, listCommonListInterface, listServiceInterface);
		init(mSimpleList.getRootView());
		setTitleName(mActivity.getString(R.string.owner));
		super.show();
	}
	
	/**------------------------ 列表相关定义 ----------------------*/
    /**
     * 列表接口实现
     */
    CommonListInterface<User> listCommonListInterface = new CommonListInterface<User>() {

        @Override
        public Map<String, Map<String, String>> getDisplayFieldsSwitchMap() {
            Map<String, Map<String, String>> fieldSwitchMap = new HashMap<String, Map<String, String>>();

            fieldSwitchMap.put("obs_id", ObsCache.getObsIdMaps());
            fieldSwitchMap.put("tenant_id", TenantCache.getTenantIdMaps());

            return fieldSwitchMap;
        }

        @Override
        public int getListItemId(User t) {
            return t.getUser_id();
        }

        @Override
        public String[] getDisplayFeilds() {
            return new String[] {
                    "name",
                    "obs_id",
                    "tenant_id",
                    "login_name"
            };
        }

        @Override
        public int getListHeaderLayoutId() {
            return R.layout.user_cooperation_listitem_layout;
        }

        @Override
        public int getListLayoutId() {
            return getListHeaderLayoutId();
        }

        @Override
        public int getListHeaderNames() {
            return R.array.user_cooperation_list_header_names;
        }

        @Override
        public int getListHeaderIds() {
            return R.array.user_cooperation_list_header_ids;
        }

        @Override
        public int getListItemIds() {
            return getListHeaderIds();
        }
                                    
    };
    
    /**
     * 列表服务接口实现
     */
    ServiceInterface<User> listServiceInterface = new ServiceInterface<User>() {

        @SuppressWarnings("static-access")
		@Override
        public void getListData() {
        	// 获取该公司的所有协作项目的干系人
        	String contacts = mCooperation.getAccept_contact_window();
        	if (UserCache.getCurrentUser().getTenant_id() == mCooperation.getAccept_company()) {
        		contacts = mCooperation.getLaunch_contact_window();
        	}
        	if (contacts != null && !contacts.isEmpty()) {
                RemoteUserService.getInstance().getUserDetailByUserIDs(mSimpleList.getServiceManager(), contacts);
        	} else {
        		mSimpleList.sendMessage(mSimpleList.DISMISS_PROGRESS_DIALOG);
        	}
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
}
