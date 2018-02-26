package com.pm360.cepm360.app.module.inventory;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import com.pm360.cepm360.R;
import com.pm360.cepm360.app.cache.UserCache;
import com.pm360.cepm360.app.common.activity.DataManagerInterface;
import com.pm360.cepm360.app.common.activity.ListSelectActivity;
import com.pm360.cepm360.app.common.custinterface.CommonListInterface;
import com.pm360.cepm360.app.common.custinterface.OperationMode;
import com.pm360.cepm360.app.common.custinterface.ServiceInterface;
import com.pm360.cepm360.app.common.custinterface.SimpleListInterface;
import com.pm360.cepm360.app.common.custinterface.SimpleServiceInterface;
import com.pm360.cepm360.app.common.view.parent.list.BaseListCommon;
import com.pm360.cepm360.app.common.view.parent.list.ListWithOptionMenu;
import com.pm360.cepm360.app.common.view.parent.list.PermissionManager;
import com.pm360.cepm360.app.common.view.parent.list.TreeSimpleListOptionMenu;
import com.pm360.cepm360.common.GLOBAL;
import com.pm360.cepm360.common.ResultStatus;
import com.pm360.cepm360.common.json.AnalysisManager;
import com.pm360.cepm360.entity.P_KC;
import com.pm360.cepm360.entity.P_WZ_DIR;
import com.pm360.cepm360.services.resource.RemoteGoodsService;
import com.pm360.cepm360.services.storehouse.RemoteStoreHouseService;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class InventoryStoreHouseFragment extends Fragment {
    
    private TreeSimpleListOptionMenu<P_WZ_DIR, P_KC> mTreeList;
    
    private RemoteGoodsService mTreeService = RemoteGoodsService.getInstance();
    private RemoteStoreHouseService mContentService = RemoteStoreHouseService.getInstance();
    
    // 基础资源Id、name映射
    Map<String, String> mUnitMap;

    // Tree 需要拉2次数据
    private int mDataFlag;
    
    /**
     * 在Fragment被创建是调用
     */
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
            Bundle savedInstanceState) {
        
        // 父类初始化
        super.onCreateView(inflater, container, savedInstanceState);
        
        // 初始化实现部分数据
        return prepareEnvironment();
    }
    
    @Override
    public void onHiddenChanged(boolean hidden) {
        super.onHiddenChanged(hidden);
        
        if (!hidden) {
            mTreeList.getListAdapter().clearAll();
            listServiceInterface.getListData();
            
            mTreeList.getTreeAdapter().notifyDataSetChanged();
        }
    }
    
    /**
     * 实例化时初始化
     */
    private View prepareEnvironment() {
        
        mUnitMap = BaseListCommon.genIdNameMap(GLOBAL.UNIT_TYPE);
        
        ListWithOptionMenu<P_KC> listWithOptionMenu = new ListWithOptionMenu<P_KC>(getActivity()) {
            @Override
            protected void displayFieldRemap(Map<String, String> displayFieldMap,
                    P_KC p, int position) {
                super.displayFieldRemap(displayFieldMap, p, position);
                
                if (p.getWz_type_1() == 1) {
                    displayFieldMap.put("wz_type_2", GLOBAL.CL_TYPE[p.getWz_type_2()-1][1]);
                } else if (p.getWz_type_1() == 2) {
                    displayFieldMap.put("wz_type_2", GLOBAL.SB_TYPE[p.getWz_type_2()-1][1]);   
                }
            }
        };
        
        // 树、列表对象
        mTreeList = new TreeSimpleListOptionMenu<P_WZ_DIR, P_KC>(getActivity(), 
                getActivity().getResources().getString(R.string.inventory_category),
                listWithOptionMenu) {

                    @Override
                    public int getTreeListRelevanceId(P_KC c) {
                        return c.getWz_dir_id();
                    }            
        };
        
        // 初始化权限
        mTreeList.setPermission(GLOBAL.SYS_ACTION[19][0], 
                                GLOBAL.SYS_ACTION[18][0], 
                                PermissionManager.PERMISSION_TYPE_SYS);
        
        handleArguments();
        
        // 初始化列表
        mTreeList.getList().init( P_KC.class, 
                            listCommonListInterface,
                            listServiceInterface);
        
      // 初始化树
      mTreeList.initTree( P_WZ_DIR.class,
                          treeListInterface, 
                          treeServiceInterface);              
        // 返回树、列表根视图
        return mTreeList.getRootView();
    }
    
    private void handleArguments() {
        Bundle bundle = getArguments();
        if (bundle != null) {
            if (bundle.containsKey(ListSelectActivity.SELECT_MODE_KEY)) {
                OperationMode mode = OperationMode.NORMAL;
                if (bundle.getBoolean(ListSelectActivity.SELECT_MODE_KEY)) {
                    mode = OperationMode.MULTI_SELECT;
                } else {
                    mode = OperationMode.SINGLE_SELECT;
                }
                
                // 设置操作模式
                mTreeList.getList().setOperationMode(mode);
            }
        }
    }
    
    /**------------------------ 列表相关定义 ----------------------*/
    /**
     * 列表接口实现
     */
    CommonListInterface<P_KC> listCommonListInterface = new CommonListInterface<P_KC>() {

        @Override
        public Map<String, Map<String, String>> getDisplayFieldsSwitchMap() {
            Map<String, Map<String, String>> fieldSwitchMap = new HashMap<String, Map<String, String>>();
            fieldSwitchMap.put("unit", mUnitMap);

            return fieldSwitchMap;
        }

        @Override
        public int getListItemId(P_KC t) {
            return t.getRk_id();
        }

        @Override
        public String[] getDisplayFeilds() {
            return new String[] {
                    "serial",       // 序号
                    "name",         // 名称
                    "wz_type_2",    // 类型
                    "spec",         // 规格
                    "unit",         // 单位
                    "quantity"      // 库存量
            };
        }

        @Override
        public int getListHeaderLayoutId() {
            return R.layout.inventory_manager_listitem;
        }

        @Override
        public int getListLayoutId() {
            return R.layout.inventory_manager_listitem;
        }

        @Override
        public int getListHeaderNames() {
            return R.array.inventory_content_header_names;
        }

        @Override
        public int getListHeaderIds() {
            return R.array.inventory_content_header_ids;
        }

        @Override
        public int getListItemIds() {
            return getListHeaderIds();
        }
        
    };
    
    /**
     * 列表服务接口实现
     */
    ServiceInterface<P_KC> listServiceInterface = new ServiceInterface<P_KC>() {

        @Override
        public void getListData() {
            mContentService.getKCList(mTreeList.getListServiceManager(), 
                    UserCache.getCurrentUser().getTenant_id(), 1);            
        }

        @Override
        public void addItem(P_KC t) {
        }

        @Override
        public void deleteItem(P_KC t) {
        }

        @Override
        public void updateItem(P_KC t) {            
        }        
    };

    /**
     * 简单树视图接口实现
     */
    SimpleListInterface<P_WZ_DIR> treeListInterface 
                                = new SimpleListInterface<P_WZ_DIR>() {

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
    
    /**
     * 树服务接口
     */
    SimpleServiceInterface<P_WZ_DIR> treeServiceInterface 
                            = new SimpleServiceInterface<P_WZ_DIR>() {

        @Override
        public void getListData() {
            mDataFlag = 2;
            mTreeService.getWZ_DIRList(mTreeManager,
                    UserCache.getCurrentUser().getTenant_id(), GLOBAL.WZ_TYPE[0][0]);
            mTreeService.getWZ_DIRList(mTreeManager,
                    UserCache.getCurrentUser().getTenant_id(), GLOBAL.WZ_TYPE[1][0]);
        }
    };
    
    DataManagerInterface mTreeManager = new DataManagerInterface() {

        @SuppressWarnings("unchecked")
        @Override
        public void getDataOnResult(ResultStatus status, List<?> list) {
            if (status.getCode() != AnalysisManager.SUCCESS_DB_QUERY) {
                Toast.makeText(getActivity(), status.getMessage(),
                        Toast.LENGTH_SHORT).show();
            }
            
            if (status.getCode() == AnalysisManager.SUCCESS_DB_QUERY &&
                    list !=null && list.size() > 0) {
                mTreeList.getTreeAdapter().addDataList((List<P_WZ_DIR>) list);
            }
            
            mDataFlag--;
            if (mDataFlag == 0) {
                // 结束加载进度对话框
                mTreeList.getTree().sendMessage(BaseListCommon.DISMISS_PROGRESS_DIALOG);
            }
        }        
    };

}
