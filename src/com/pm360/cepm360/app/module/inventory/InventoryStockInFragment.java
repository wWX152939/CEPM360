package com.pm360.cepm360.app.module.inventory;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.pm360.cepm360.R;
import com.pm360.cepm360.app.cache.PermissionCache;
import com.pm360.cepm360.app.cache.UserCache;
import com.pm360.cepm360.app.common.adpater.DataListAdapter.ViewHolder;
import com.pm360.cepm360.app.common.custinterface.CommonListInterface;
import com.pm360.cepm360.app.common.custinterface.OptionMenuInterface;
import com.pm360.cepm360.app.common.custinterface.ServiceInterface;
import com.pm360.cepm360.app.common.custinterface.SimpleListInterface;
import com.pm360.cepm360.app.common.custinterface.SimpleServiceInterface;
import com.pm360.cepm360.app.common.view.OptionsMenuView.SubMenuListener;
import com.pm360.cepm360.app.common.view.parent.list.BaseListCommon;
import com.pm360.cepm360.app.common.view.parent.list.ListWithOptionMenu;
import com.pm360.cepm360.app.common.view.parent.list.PermissionManager;
import com.pm360.cepm360.app.common.view.parent.list.TreeTwoListOptionMenu;
import com.pm360.cepm360.common.GLOBAL;
import com.pm360.cepm360.common.util.MiscUtils;
import com.pm360.cepm360.entity.EPS;
import com.pm360.cepm360.entity.Expandable;
import com.pm360.cepm360.entity.P_RK;
import com.pm360.cepm360.entity.Project;
import com.pm360.cepm360.entity.TreeTwoBean;
import com.pm360.cepm360.services.project.RemoteProjectService;
import com.pm360.cepm360.services.storehouse.RemoteInStoreService;
import com.pm360.cepm360.services.system.RemoteEPSService;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * 入库 fragment
 * 
 * 1. 入库操作，可以重复入库
 * 2. 查看附件
 *
 */

public class InventoryStockInFragment extends Fragment {
    
    // ViewPager的树、列表对象
    private TreeTwoListOptionMenu<EPS, Project, P_RK> mTreeList;
    
    // 服务
    private RemoteEPSService mEpsService = RemoteEPSService.getInstance();
    private RemoteProjectService mProjectService = RemoteProjectService.getInstance();
    private RemoteInStoreService mListService 
                                = RemoteInStoreService.getInstance();
    
    // 基础资源Id、name映射
    Map<String, String> mUnitMap;
    Map<String, String> mUserMap;
    Map<String, String> mStatusMap;
    
    
    private static final int REQUEST_STOCK_IN_CODE = 200;
    
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
        // 为物资类型做映射
        mUnitMap = BaseListCommon.genIdNameMap(GLOBAL.UNIT_TYPE);
        mUserMap = UserCache.getUserMaps();
        mStatusMap = BaseListCommon.genIdNameMap(GLOBAL.RK_TYPE);
        
        ListWithOptionMenu<P_RK> listWithOptionMenu = new ListWithOptionMenu<P_RK>(getActivity()) {
            @Override
            protected void displayFieldRemap(Map<String, String> displayFieldMap,
                    P_RK p, int position) {
                super.displayFieldRemap(displayFieldMap, p, position);
                
                if (p.getWz_type_1() == 1) {
                    displayFieldMap.put("wz_type_2", GLOBAL.CL_TYPE[p.getWz_type_2()-1][1]);
                } else if (p.getWz_type_1() == 2) {
                    displayFieldMap.put("wz_type_2", GLOBAL.SB_TYPE[p.getWz_type_2()-1][1]);   
                }
                
                String attachmens = p.getAttachment();
                if (attachmens != null && !attachmens.equals("")) {
                    String[] aStrings = attachmens.split(",");
                    displayFieldMap.put("attachment", "" + aStrings.length);
                } else {
                    displayFieldMap.put("attachment", "0");
                }
            }
            
            @Override
            protected void initListViewItemMore(ViewHolder holder,
                    int position, Map<String, String> displayFieldMap) {
                super.initListViewItemMore(holder, position, displayFieldMap);
                
                if (!displayFieldMap.get("attachment").equals("0")) {
                    Drawable drawable = getResources().getDrawable(
                            R.drawable.email_attachment);            
                    drawable.setBounds(0, 0, 27, 27);
                    holder.tvs[13].setCompoundDrawables(null, null, drawable, null);
                } else {
                    holder.tvs[13].setCompoundDrawables(null, null, null, null);
                }              
            }            
        };
    
        // 必须先创建对象，然后设置权限（权限管理对象在本对象创建时被创建）
        mTreeList = new TreeTwoListOptionMenu<EPS, Project, P_RK>(
                getActivity(), getActivity().getResources().getString(
                        R.string.project_directory), listWithOptionMenu) {

            @Override
            public int getTreeListRelevanceId(P_RK p) {
                return p.getProject_id();
            }

            @SuppressLint("UseSparseArrays")
            @Override
            public List<TreeTwoBean> genTreeTwoBeanList(List<EPS> parentList,
                    List<Project> childList) {
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
        mTreeList.getList().setForceEnableOptionMenu(true);
        // 初始化列表
        mTreeList.initList( P_RK.class, 
                            listCommonListInterface,
                            listServiceInterface, 
                            null,
                            optionMenuInterface);
                
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
    CommonListInterface<P_RK> listCommonListInterface 
                                = new CommonListInterface<P_RK>() {

        @Override
        public Map<String, Map<String, String>> getDisplayFieldsSwitchMap() {
            Map<String, Map<String, String>> fieldSwitchMap 
                            = new HashMap<String, Map<String, String>>();
            fieldSwitchMap.put("wz_unit", mUnitMap);
            fieldSwitchMap.put("operator", mUserMap);
            fieldSwitchMap.put("status", mStatusMap);

            return fieldSwitchMap;
        }

        @Override
        public int getListItemId(P_RK p) {
            return p.getRk_id();
        }

        @Override
        public String[] getDisplayFeilds() {
            return new String[] {
                    "wz_name",     // 资源名称
                    "wz_type_2",   // 资源类型 *
                    "wz_brand",    // 品牌
                    "wz_spec",     // 规格
                    "wz_unit",     // 单位 *
                    "ys_quantity", // 预算数量
                    "cg_quantity", // 采购数量
                    "in_quantity", // 入库数量
                    "storehouse",  // 库房
                    "contract_name", // 合同
                    "operator",    // 经办人                    
                    "in_date",     // 入库时间
                    "status",      // 状态 *
                    "attachment",  // 附件
            };
        }

        @Override
        public int getListHeaderLayoutId() {
            return R.layout.inventory_stock_in_listitem;
        }

        @Override
        public int getListLayoutId() {
            return R.layout.inventory_stock_in_listitem;
        }

        @Override
        public int getListHeaderNames() {
            return R.array.inventory_pas_names;
        }

        @Override
        public int getListHeaderIds() {
            return R.array.inventory_pas_ids;
        }

        @Override
        public int getListItemIds() {
            return R.array.inventory_pas_ids;
        }
    };
    
    /**
     * 列表服务接口实现
     */
    ServiceInterface<P_RK> listServiceInterface 
                                = new ServiceInterface<P_RK>() {

        @Override
        public void getListData() {
            mListService.getInStoreList(mTreeList.getListServiceManager(), 
                    UserCache.getCurrentUser().getTenant_id(), 0, 0);
        }

        @Override
        public void addItem(P_RK t) {
            // TODO Auto-generated method stub
            
        }

        @Override
        public void deleteItem(P_RK t) {
            // TODO Auto-generated method stub
            
        }

        @Override
        public void updateItem(P_RK t) {
            // TODO Auto-generated method stub
            
        }

    };
    
    /**
     * 选项菜单接口实现
     */
    private OptionMenuInterface optionMenuInterface = new OptionMenuInterface() {

        @Override
        public int getOptionMenuNames() {
            if (mTreeList.getList().getPermissionManager().hasEditPermission()) {
                return R.array.inventory_stockin_option_menu_names;
            } else {
                return R.array.inventory_stockin_option_menu_names_2;
            }            
        }

        @Override
        public SubMenuListener getOptionMenuClickListener() {
            return new SubMenuListener() {

                @Override
                public void onSubMenuClick(View view) {
                    mTreeList.getList().getOptionsMenu().dismiss();
                    Intent intent = new Intent(Intent.ACTION_MAIN);
                    Bundle bundle = new Bundle();
                    bundle.putSerializable("bean", mTreeList.getList().getCurrentItem());
                    if ((int) view.getTag() == 1 ||
                            !PermissionCache.hasSysPermission(GLOBAL.SYS_ACTION[18][0])) { // 附件
                        bundle.putBoolean(InventoryStockInActivity.VIEW_ATTACHMENT, true);
                    }
                    intent.putExtras(bundle);
                    intent.setClass(getActivity(), InventoryStockInActivity.class);
                    startActivityForResult(intent, REQUEST_STOCK_IN_CODE);
                }
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
     * activity返回的处理
     */
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (resultCode == 0) return;
        if (requestCode == REQUEST_STOCK_IN_CODE) {
            MiscUtils.clone(mTreeList.getList().getCurrentItem(), data.getSerializableExtra("bean"));
            mTreeList.getListAdapter().notifyDataSetChanged();
        }
        super.onActivityResult(requestCode, resultCode, data);
    }

}
