package com.pm360.cepm360.app.module.inventory;

import android.annotation.SuppressLint;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.Toast;

import com.pm360.cepm360.R;
import com.pm360.cepm360.app.cache.PermissionCache;
import com.pm360.cepm360.app.cache.UserCache;
import com.pm360.cepm360.app.common.activity.DataManagerInterface;
import com.pm360.cepm360.app.common.custinterface.CommonListInterface;
import com.pm360.cepm360.app.common.custinterface.FloatingMenuInterface;
import com.pm360.cepm360.app.common.custinterface.OptionMenuInterface;
import com.pm360.cepm360.app.common.custinterface.ServiceInterface;
import com.pm360.cepm360.app.common.custinterface.SimpleListInterface;
import com.pm360.cepm360.app.common.custinterface.SimpleServiceInterface;
import com.pm360.cepm360.app.common.view.OptionsMenuView.SubMenuListener;
import com.pm360.cepm360.app.common.view.parent.list.PermissionManager;
import com.pm360.cepm360.app.common.view.parent.list.TreeTwoListOptionMenu;
import com.pm360.cepm360.common.GLOBAL;
import com.pm360.cepm360.common.ResultStatus;
import com.pm360.cepm360.common.json.AnalysisManager;
import com.pm360.cepm360.entity.EPS;
import com.pm360.cepm360.entity.Expandable;
import com.pm360.cepm360.entity.P_CK;
import com.pm360.cepm360.entity.Project;
import com.pm360.cepm360.entity.TreeTwoBean;
import com.pm360.cepm360.services.project.RemoteProjectService;
import com.pm360.cepm360.services.storehouse.RemoteOutStoreService;
import com.pm360.cepm360.services.system.RemoteEPSService;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class InventoryStockOutFragment extends Fragment {

    // 的树、列表对象
    private TreeTwoListOptionMenu<EPS, Project, P_CK> mTreeList;
    
    // 服务
    private RemoteEPSService mEpsService = RemoteEPSService.getInstance();
    private RemoteProjectService mProjectService = RemoteProjectService.getInstance();
    private RemoteOutStoreService mListService 
                                = RemoteOutStoreService.getInstance();
    
    // 基础资源Id、name映射
    Map<String, String> mUserMap;
    
    private static final int OPERATION_DETAIL = 10;
    private static final int OPERATION_ADD = 11;
    private static final int OPERATION_MODIFY = 12;
    
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
    
    /**
     * 实例化时初始化
     */
    private View prepareEnvironment() {
        // 为合同类型做映射
        mUserMap = UserCache.getUserMaps();   
    
        // 必须先创建对象，然后设置权限（权限管理对象在本对象创建时被创建）
        mTreeList = new TreeTwoListOptionMenu<EPS, Project,
                            P_CK>(getActivity(), getActivity().getResources()
                                                .getString(R.string.project_directory)) {

                @Override
                public int getTreeListRelevanceId(P_CK p) {
                    return p.getProject_id();
                }

                @SuppressLint("UseSparseArrays") 
                @Override
                public List<TreeTwoBean> genTreeTwoBeanList(
                        List<EPS> parentList, List<Project> childList) {
                    return getTreeTwoBeanList(parentList, childList);
                }
                
                @Override
    			protected <E extends Expandable & Serializable> boolean getIsBoldText(E t) {
    				if (((com.pm360.cepm360.entity.TreeTwoBean) t).isParent()) {
    					return true;
    				} else {
    					return false;
    				}
    			}
        };
        // 初始化权限
        mTreeList.setPermission(GLOBAL.SYS_ACTION[19][0], 
                                GLOBAL.SYS_ACTION[18][0], 
                                PermissionManager.PERMISSION_TYPE_SYS);       
        // 初始化列表
        mTreeList.initList(P_CK.class, listCommonListInterface,
                listServiceInterface, null, optionMenuInterface,
                floatingMenuInterface);

        // 初始化树
        mTreeList.initTree( TreeTwoBean.class,
                            treeListInterface, 
                            treeServiceInterface);
        
        // 返回树、列表根视图
        return mTreeList.getRootView();
    }
    
    /**
     * 构造树节点对象列表，由于构造过程存在差异，这里单独做算法实现
     * @param parentList
     * @param childList
     * @return
     */
    @SuppressLint("UseSparseArrays") 
    private List<TreeTwoBean> getTreeTwoBeanList(
            List<EPS> parentList, List<Project> childList) {
        
        List<TreeTwoBean> trees = new ArrayList<TreeTwoBean>();
        Map<Integer, Integer> maps = new HashMap<Integer, Integer>();
        
        int id = 1;
        
        // 构造EPS对应的EpsProjectTree对象列表
        for (int i = 0; i < parentList.size(); i++) {
            EPS eps = parentList.get(i);
            TreeTwoBean node = new TreeTwoBean();
            
            node.setVirtualId(id++);
            node.setParent(true);
            node.setRealId(eps.getEps_id());
            node.setName(eps.getName());
            node.setTenantId(eps.getTenant_id());
            node.setParentId(eps.getParents_id());
            
            // 加入映射表
            maps.put(node.getRealId(), node.getVirtualId());
            
            trees.add(node);
        }

        // 构造Project对应的EpsProjectTree对象列表
        for (int i = 0; i < childList.size(); i++) {
            Project project = childList.get(i);
            TreeTwoBean node = new TreeTwoBean();
            
            node.setVirtualId(id++);
            node.setParent(false);
            node.setRealId(project.getProject_id());
            node.setName(project.getName());
            node.setTenantId(project.getTenant_id());
            node.setParentId(project.getEps_id());
            
            trees.add(node);
        }
        
        // 从新设置父节点ID
        for (int i = 0; i < trees.size(); i++) {
            TreeTwoBean node = trees.get(i);
            int parentId = node.getParentId();
            if (parentId == 0) {
                continue;
            }
            
            node.setParentId(maps.get(parentId));
        }
        
        return trees;
    }
    
    /**------------------------ 列表相关定义 ----------------------*/
    /**
     * 列表接口实现
     */
    CommonListInterface<P_CK> listCommonListInterface 
                                = new CommonListInterface<P_CK>() {

        @Override
        public Map<String, Map<String, String>> getDisplayFieldsSwitchMap() {
            Map<String, Map<String, String>> fieldSwitchMap 
                            = new HashMap<String, Map<String, String>>();
            fieldSwitchMap.put("storeman", mUserMap);
            fieldSwitchMap.put("use_person", mUserMap);

            return fieldSwitchMap;
        }

        @Override
        public int getListItemId(P_CK p) {
            return p.getCk_id();
        }

        @Override
        public String[] getDisplayFeilds() {
            return new String[] {
                    "ck_number",     // 出库单号
                    "storehouse",    // 库房
                    "storeman",      // 库管员
                    "use_person",    // 领用人
                    "out_date",      // 出库时间
            };
        }

        @Override
        public int getListHeaderLayoutId() {
            return R.layout.inventory_stock_out_listitem;
        }

        @Override
        public int getListLayoutId() {
            return R.layout.inventory_stock_out_listitem;
        }

        @Override
        public int getListHeaderNames() {
            return R.array.inventory_putout_storage_items;
        }

        @Override
        public int getListHeaderIds() {
            return R.array.inventory_putout_storage_ids;
        }

        @Override
        public int getListItemIds() {
            return R.array.inventory_putout_storage_ids;
        }
    };
    
    /**
     * 列表服务接口实现
     */
    ServiceInterface<P_CK> listServiceInterface 
                                = new ServiceInterface<P_CK>() {

        @Override
        public void getListData() {
            mListService.getOutStoreList(mTreeList.getListServiceManager(),
                    UserCache.getCurrentUser().getTenant_id(), 0,
                    0);
        }

        @Override
        public void addItem(P_CK t) {
            // TODO Auto-generated method stub
            
        }

        @Override
        public void deleteItem(P_CK t) {
            // TODO Auto-generated method stub
            
        }

        @Override
        public void updateItem(P_CK t) {
            // TODO Auto-generated method stub
            
        }

    };
    
    /**
     * 选项菜单接口实现
     */
    private OptionMenuInterface optionMenuInterface = new OptionMenuInterface() {

        @Override
        public int getOptionMenuNames() {
            return R.array.inventory_putout_options_menu;
        }

        @Override
        public SubMenuListener getOptionMenuClickListener() {
            return new SubMenuListener() {

                @Override
                public void onSubMenuClick(View view) {
                    mTreeList.getList().getOptionsMenu().dismiss();
                    
                    
                    switch ((Integer) view.getTag()) {
                    case 0: // 查看详细
                        startTicketActivity(OPERATION_DETAIL, mTreeList.getList().getCurrentItem());
                        break;
                    case 1: // 修改
                        if (!PermissionCache.hasSysPermission(GLOBAL.SYS_ACTION[19][0])) {
                            showToast(getResources().getString(R.string.no_edit_permission));
                            return;
                        }
                        startTicketActivity(OPERATION_MODIFY, mTreeList.getList().getCurrentItem());
                        break;
                    case 2:
                        doDeleteTicket(mTreeList.getList().getCurrentItem());
                        break;
                    }
                }
            };
        }
    };
    

    
    /**
     * 浮动菜单接口实现
     */
    private FloatingMenuInterface floatingMenuInterface = new FloatingMenuInterface() {
        
        @Override
        public String[] getFloatingMenuTips() {
            return new String[] {
                    getActivity().getResources().getString(R.string.add)
            };
        }
        
        @Override
        public OnItemClickListener getFloatingMenuListener() {
            return new OnItemClickListener() {

                @Override
                public void onItemClick(AdapterView<?> parent, View view,
                        int position, long id) {
                    switch (position) {
                        case 0:
                            if (!PermissionCache.hasSysPermission(GLOBAL.SYS_ACTION[19][0])) {
                                showToast(getResources().getString(R.string.no_edit_permission));
                                return;
                            }
                            P_CK pCk = new P_CK();
                            pCk.setProject_id(mTreeList.getTree().getCurrentItem().getRealId());
                            pCk.setProject_name(mTreeList.getTree().getCurrentItem().getName());
                            startTicketActivity(OPERATION_ADD, pCk);
                            break;
                        default:
                            break;
                    }                   
                }
            };
        }
        
        @Override
        public int[] getFloatingMenuImages() {
            return new int[] { 
                    R.drawable.icn_add 
            };
        }
    };
        
    /**
     * 简单树视图接口实现
     */
    SimpleListInterface<TreeTwoBean> treeListInterface 
                                = new SimpleListInterface<TreeTwoBean>() {

        @Override
        public int getListItemId(TreeTwoBean t) {
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
    SimpleServiceInterface<TreeTwoBean> treeServiceInterface 
                            = new SimpleServiceInterface<TreeTwoBean>() {

        @Override
        public void getListData() {
            
            // 获取EPS列表
            mEpsService.getEPSList(mTreeList.getParentManager(), 
                                    UserCache.getCurrentUser());
            
            // 获取项目列表
            mProjectService.getProjectList(mTreeList.getChildManager(),
                                    UserCache.getCurrentUser());
        }
    };
    
    /**
     * 异步线程回调接口
     */
    DataManagerInterface dataManager = new DataManagerInterface() {

        @Override
        public void getDataOnResult(ResultStatus status, List<?> list) {
//            mActivity.dismissProgressDialog();
//            if (list != null && list.size() != 0
//                    && status.getCode() == AnalysisManager.SUCCESS_DB_QUERY) {
//                mStockOutDataList.clear();
//                for (Object object : list) {
//                    if (object instanceof P_CK) {
//                        mStockOutDataList.add(0, (P_CK) object);
//                    }
//                }
//                mHandler.sendEmptyMessage(DATA_CHANGED);
//            }
//
//            if ((status.getCode() == AnalysisManager.SUCCESS_DB_UPDATE)) {
//                loadData();
//            }
            
            if ((status.getCode() == AnalysisManager.SUCCESS_DB_DEL)) {
                mTreeList.getListAdapter().deleteData(mTreeList.getList().getCurrentItem());
                mTreeList.getListAdapter().notifyDataSetChanged();
                //mStockOutDataList.remove(mStockOutCurrentItem);
                //mHandler.sendEmptyMessage(DATA_CHANGED);
            }
        }
    };
    
    /**
     * 删除一条数据
     * 
     * @param bean
     */
    private void doDeleteTicket(final P_CK bean) {
        if (!PermissionCache.hasSysPermission(GLOBAL.SYS_ACTION[19][0])) {
            showToast(getResources().getString(R.string.no_edit_permission));
            return;
        }
        
        new AlertDialog.Builder(getActivity())
                .setTitle(getResources().getString(R.string.remind))
                .setMessage(getResources().getString(R.string.confirm_delete))
                .setPositiveButton(getResources().getString(R.string.confirm),
                        new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        mListService.deleteCK(dataManager, bean.getCk_id());
                        dialog.dismiss();
                    }
                })
                .setNegativeButton(getResources().getString(R.string.cancel),
                        new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.dismiss();
                    }
                }).show();
    }
    
    private void startTicketActivity(int operation, P_CK currentBean) {        
        Intent intent = new Intent(getActivity(),
                PutOutStorageTicketActivity.class);
        Bundle bundle = new Bundle();
        bundle.putSerializable("operation", operation);
        bundle.putSerializable("data", currentBean);
        intent.putExtras(bundle);
        startActivityForResult(intent, operation);
    }
    
    private void showToast(String message) {
        Toast.makeText(getActivity(), message, Toast.LENGTH_SHORT).show();
    }
    
    /**
     * activity返回的处理
     */
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (resultCode == 0) return;
        
        P_CK ck = (P_CK) data.getSerializableExtra("current_bean");
        
        switch (requestCode) {
        case OPERATION_ADD:
            mTreeList.getTreeAdapter().updateTreeNodesCount(1);
            mTreeList.getListAdapter().addDataToList(ck);
            break;
        case OPERATION_MODIFY:
            mTreeList.getListAdapter().updateData(ck);
            break;
        }
        super.onActivityResult(requestCode, resultCode, data);
    }
}
