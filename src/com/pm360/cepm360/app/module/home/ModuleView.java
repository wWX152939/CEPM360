package com.pm360.cepm360.app.module.home;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.ActivityNotFoundException;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.BaseAdapter;
import android.widget.GridView;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.pm360.cepm360.R;
import com.pm360.cepm360.app.cache.PermissionCache;
import com.pm360.cepm360.app.cache.ProjectCache;
import com.pm360.cepm360.app.common.activity.ProjectSelectActivity;
import com.pm360.cepm360.app.module.contract.ContractActivity;
import com.pm360.cepm360.app.module.cooperation.CooperationActivity;
import com.pm360.cepm360.app.module.lease.LeaseManagementActivity;
import com.pm360.cepm360.app.module.project.ProjectActivity;
import com.pm360.cepm360.app.module.schedule.CombinationActivity;
import com.pm360.cepm360.app.module.schedule.ScheduleActivity;
import com.pm360.cepm360.app.utils.UtilTools;
import com.pm360.cepm360.common.GLOBAL;

import java.util.ArrayList;
import java.util.List;

/**
 * 
 * 描述: 主页功能模块页
 * 
 * 	1. 根据企业类型enterpriseType获取该企业类型的功能模块数组
 * 	2. 根据当前用户的系统权限过滤可以显示的功能模块
 *
 */
public class ModuleView {
    
    private Context mContext;
    private View mContentView;
    private int mEnterpriseType;
    
    private GridView mGridView;
    private GridAdapter mGridAdapter;
    
    private String[] mAllTitles;
    private int[] mAllPermissions;
    private int[] mAllIcons;
    private int[] mAllColors;
    private Class<?>[] mAllClazz;
    
    public ModuleView(Context context, int enterpriseType) {
        mContext = context; 
        mEnterpriseType = enterpriseType;
        initView();
    }
    
    public View getView() {
        return mContentView;
    }
    
    @SuppressLint("InflateParams") 
    private void initView() {
        mContentView = LayoutInflater.from(mContext).inflate(
                R.layout.activity_home_gridview, null);
        
        // 以下数组必须保持一致的顺序
        mAllTitles = mContext.getResources().getStringArray(
                R.array.home_titles);
        mAllPermissions = mContext.getResources().getIntArray(
                R.array.home_module_ids);
        mAllIcons = ModuleConfig.getAllIcons();
        mAllColors = ModuleConfig.getDefaultBackground();
        mAllClazz = ModuleConfig.getAllClazzs();

        setupModules(mContentView);
    }
    
    /**
     * 过滤该用户拥有查看权限的模块
     */
    public void filterModuleBySysPermission() {
        ArrayList<Integer> visibleModules = new ArrayList<Integer>();
        int[] allModules = ModuleConfig.getHomeModules(mEnterpriseType);
        for (int i = 0; i < allModules.length; i++) {
            if (PermissionCache.hasSysModulePermission(allModules[i])) {
                visibleModules.add(allModules[i]);
            }
        }
        
        if (visibleModules.size() > 0) {
            int[] modules = new int[visibleModules.size()];
            for (int i = 0; i < visibleModules.size(); i++) {
                modules[i] = visibleModules.get(i);
            }
            int[] permissions = new int[modules.length];
            int[] icons = new int[modules.length];
            int[] colors = new int[modules.length];
            String[] titles = new String[modules.length];
            Class<?>[] clazz = new Class<?>[modules.length];
            
            for (int i = 0; i < modules.length; i++) {
                titles[i] = mAllTitles[modules[i]-1];
                permissions[i] = mAllPermissions[modules[i]-1];
                icons[i] = mAllIcons[modules[i]-1];
                colors[i] = mAllColors[modules[i]-1];
                clazz[i] = mAllClazz[modules[i]-1];
            }
            
            mGridAdapter.refreshModuleView(titles, permissions, icons, colors, clazz);
            
            if (modules.length > 15) {
                // 模块超过15个， 显示需要4行，为了不让gridview滚动，缩小垂直行间距。
                mGridView.setVerticalSpacing(UtilTools.dp2pxH(mContext, 30));
            }
        }
        
        // 过滤完成，设置标记为false, 这样下次HomeActivity onStart() 不会再调用。
        SharedPreferences prefs = mContext.getSharedPreferences(
                GLOBAL.SHARED_PREFERENCES_KEY, Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = prefs.edit();
        editor.putBoolean("sys_permission_changed", false);
        editor.commit();
    }
    
    /**
     * 加载模块到主页显示
     * @param modules
     */
    public void setupModules(View parentView) {        
        mGridView = (GridView) parentView.findViewById(R.id.grid_view);
        mGridAdapter = new GridAdapter(mContext);
        mGridView.setAdapter(mGridAdapter);
        mGridView.setOnItemClickListener(new AdapterView.OnItemClickListener() {

            @Override
            public void onItemClick(AdapterView<?> parent, View view,
                    int position, long id) {
                int module = mGridAdapter.getItem(position).module;
                if (mGridAdapter.getItem(position).extapp) {
                    // 打开【视频监控】App
                    startExtApplication("yingshi");
                    return;
                }
                if (mGridAdapter.getItem(position).directly) {
                    Class<?> clazz = mGridAdapter.getItem(position).clazz;
                    if (mEnterpriseType == 0 && clazz == ProjectActivity.class) { // 管理方
                        // 使用startActivityForResult的目的是为了让 【项目信息】模块中点击打开时，
                        // 能够跳回首页并定位到【组合管理】的页面，并定位到所打开的项目
                        startActivityForResult(clazz,
                                HomeActivity.PROJECT_MODULE_REQUEST_CODE);
                    } else if (mEnterpriseType == 0 && clazz == CombinationActivity.class) {
                        clazz = ScheduleActivity.class;
                        startActivity(clazz);
                    } else {
                    	if (ProjectCache.getProjectLists().isEmpty() &&
                    			(clazz == CombinationActivity.class
                    			|| clazz == ContractActivity.class
                    			|| clazz == CooperationActivity.class
                    			|| clazz == LeaseManagementActivity.class)) {
                    		UtilTools.showToast(parent.getContext(),
                                    R.string.pls_create_project);
                    	} else {
                    		startActivity(clazz);
                    	}
                    }
                } else {
                    // 不能直接打开模块，需要检查项目权限
                    if (ProjectCache.getCurrentProject() == null) {
                        // 先去选择项目打开
                        startActivityForResult(ProjectSelectActivity.class,
                                position);
                    } else {
                        if (PermissionCache
                                .hasSysModulePermission(module)) {
                            // 有项目权限
                            startActivity(mGridAdapter.getItem(position).clazz);
                        } else {
                            UtilTools.showToast(parent.getContext(),
                                    R.string.no_edit_permission);
                        }
                    }
                }
            }
        });
        // 过滤该用户拥有查看权限的模块
        filterModuleBySysPermission();
    }
    
    /**
     * onActivityResult called by HomeActivity
     * 
     * @param requestCode
     * @param resultCode
     * @param data
     */
    public void doActivityResult(int requestCode, int resultCode, Intent data) {
        if (resultCode == Activity.RESULT_OK) {
            int module = mGridAdapter.getItem(requestCode).module;
            if (PermissionCache
                    .hasSysModulePermission(module)) {
                startActivity(mGridAdapter.getItem(requestCode).clazz);
            } else {
                UtilTools.showToast(mContext, R.string.no_edit_permission);
            }
        }
    }
    
    /**
     * 功能模块页面GridAdapter
     */
    private class GridAdapter extends BaseAdapter {
        private List<Item> mList = new ArrayList<Item>();
        
        public void refreshModuleView(String[] titles,
                int[] permissions, int[] icons, int[] colors, Class<?>[] clazz) {
            mList.clear();
            for (int i = 0; i < titles.length; i++) {
                boolean directly = true;
                boolean extapp = false;
                switch (permissions[i]) {
                case 4:  // 分包管理
                case 6:  // 采购管理
                case 7:  // 成本管理
                case 12: // 签证管理 
                case 24: // 投标管理 
                    // 以上这些模块不能直接打开，需要判断时是否选择了项目
                    directly = false;
                    break;
                case 22: // 监控预警
                    extapp = true;
                    break;
                default:
                    directly = true;
                }
                mList.add(new Item(titles[i], icons[i], colors[i], permissions[i],
                        clazz[i], directly, extapp));
            }
            notifyDataSetChanged();
        }
        
        public GridAdapter(Context context) {
            
        }

        @Override
        public int getCount() {
            return mList.size();
        }

        @Override
        public Item getItem(int position) {
            return mList.get(position);
        }

        @Override
        public long getItemId(int position) {
            return position;
        }

        @Override
        public View getView(int position, View convertView, ViewGroup parent) {
            ViewHolder holder;
            if (convertView == null) {
                convertView = LayoutInflater.from(parent.getContext()).inflate(
                        R.layout.home_item, parent, false);
                holder = new ViewHolder();
                holder.icon = (ImageView) convertView.findViewById(R.id.icon);
                holder.title = (TextView) convertView.findViewById(R.id.title);
                holder.title.setSelected(true);
                convertView.setTag(holder);
            }
            else {
                holder = (ViewHolder) convertView.getTag();
            }
            holder.icon.setImageResource(mList.get(position).icon);
            holder.icon.setBackgroundResource(mList.get(position).color);
            holder.title.setText(mList.get(position).title);
            return convertView;
        }

        private class Item {

            String title;
            int icon;
            int color;
            int module;
            Class<?> clazz;
            boolean directly;  // 是否需要先选择项目，还是直接打开
            boolean extapp;    // 启动外部应用程序

            public Item(String title, int icon, int color, int module,
                    Class<?> clazz, boolean directly, boolean extapp) {
                super();
                this.title = title;
                this.icon = icon;
                this.color = color;
                this.module = module;
                this.clazz = clazz;
                this.directly = directly;
                this.extapp = extapp;
            }
        }

        private class ViewHolder {
            TextView title;
            ImageView icon;
        }
    }
    
    public void startActivity(Class<?> cls) {
        if (cls == null) {
            UtilTools.showToast(mContext, R.string.no_fun);
            return;
        }
        Intent intent = new Intent(Intent.ACTION_MAIN);
        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        intent.setClass(mContext, cls);
        mContext.startActivity(intent);
    }

    public void startActivityForResult(Class<?> cls, int requestCode) {
        Intent intent = new Intent(Intent.ACTION_MAIN);
        intent.setClass(mContext, cls);
        ((HomeActivity) mContext).startActivityForResult(intent, requestCode);
    }
    
    /**
     * 调用第三方App
     * 监控预警 - 莹石云视频
     * 
     * @param packageName
     */
    private void startExtApplication(String packageName) {
        try {
            ComponentName componentName = new ComponentName("com.videogo",
                    "com.videogo.login.LoadingActivity");
            Intent intent = new Intent();
            intent.setComponent(componentName);
            intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
            mContext.startActivity(intent);
        } catch (ActivityNotFoundException e) {
            Toast.makeText(mContext, mContext.getString(R.string.monitor_app_uninstall), Toast.LENGTH_SHORT).show();
        }
    }

}
