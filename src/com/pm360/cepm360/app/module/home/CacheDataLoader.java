package com.pm360.cepm360.app.module.home;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.SharedPreferences;
import android.os.Handler;
import android.os.Message;

import com.pm360.cepm360.app.cache.EpsCache;
import com.pm360.cepm360.app.cache.ObsCache;
import com.pm360.cepm360.app.cache.ProjectCache;
import com.pm360.cepm360.app.cache.RoleCache;
import com.pm360.cepm360.app.cache.TenantCache;
import com.pm360.cepm360.app.cache.UserCache;
import com.pm360.cepm360.app.common.activity.DataManagerInterface;
import com.pm360.cepm360.app.utils.UtilTools;
import com.pm360.cepm360.common.GLOBAL;
import com.pm360.cepm360.common.ResultStatus;
import com.pm360.cepm360.common.json.AnalysisManager;
import com.pm360.cepm360.entity.EPS;
import com.pm360.cepm360.entity.OBS;
import com.pm360.cepm360.entity.Project;
import com.pm360.cepm360.entity.Role;
import com.pm360.cepm360.entity.Tenant;
import com.pm360.cepm360.entity.User;
import com.pm360.cepm360.services.common.RemoteCommonService;
import com.pm360.cepm360.services.project.RemoteProjectService;
import com.pm360.cepm360.services.system.RemoteEPSService;
import com.pm360.cepm360.services.system.RemoteOBSService;
import com.pm360.cepm360.services.system.RemoteRoleService;
import com.pm360.cepm360.services.system.RemoteUserService;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

/**
 * 缓存数据加载类
 * 
 * 1. 通过loadData()， 拉到数据后写到Cache中（UserCache等）
 * 2. 通过setOnDataLoadedListener可以监听数据加载完成的事件。
 *
 */
public class CacheDataLoader {
    
    private Context mContext;
    private ProgressDialog mProgressDialog;
    private OnDataLoadedListener mListener;
    
    private int mPreLoadData = 0;
    
    public static interface OnDataLoadedListener {
        public void onDataLoaded(int flag);
    };
    
    public void setOnDataLoadedListener(OnDataLoadedListener listener) {
        mListener = listener;
    }
    
    private Handler mHandler = new Handler(new Handler.Callback() {
        @Override
        public boolean handleMessage(Message msg) {
            checkDataLoad(mPreLoadData++);
            return false;
        }
    });
    
    public CacheDataLoader(Context context) {
        mContext = context;
    }
    
    public void loadData() {
        showProgressDialog("Getting preload data...");
        loadUserData();    // 加载用户列表
        loadProjectData(); // 加载项目列表
        loadEpsData();     // 加载EPS列表
        loadObsData();     // 加载OBS列表
        loadRoleData();    // 加载Role列表
        loadTenantData();  // 加载Tenant列表
    }
    
    private void checkDataLoad(int loaded) {
        if (mPreLoadData == 6) {
            dismissProgressDialog();
            mListener.onDataLoaded(0);
        }
    }
        
    private void loadUserData() {
        RemoteUserService.getInstance().getTenantUsers(new DataManagerInterface() {
            @SuppressWarnings("unchecked")
            @Override
            public void getDataOnResult(ResultStatus status, List<?> list) {
                mHandler.sendEmptyMessage(0);
                if (status.getCode() == AnalysisManager.SUCCESS_DB_QUERY) {
                    if (list != null && list.size() != 0) {
                        UserCache.setUserLists((ArrayList<User>) list);
                    }
                }
            }
        }, UserCache.getCurrentUser());
    }
    
    private void loadProjectData() {
        RemoteProjectService.getInstance().getProjectList(new DataManagerInterface() {
            @SuppressWarnings("unchecked")
            @Override
            public void getDataOnResult(ResultStatus status, List<?> list) {
                mHandler.sendEmptyMessage(0);
                if (status.getCode() == AnalysisManager.SUCCESS_DB_QUERY) {
                    if (list != null && list.size() != 0) {
                        ProjectCache.setProjectLists((ArrayList<Project>) list);
                        
                        // 根据最新打开过的项目，设置为当前项目
                        SharedPreferences pref = mContext.getSharedPreferences(
                                GLOBAL.SHARED_PREFERENCES_KEY, Context.MODE_PRIVATE);
                        String recentArray = pref.getString("recent", "");
                        if (!recentArray.equals("")) {
                            List<String> recentlist = Arrays.asList(recentArray.split(","));
                            int projectId = Integer.parseInt(recentlist.get(0));
                            for (Object object : list) {
                                Project p = (Project) object;
                                if (p.getProject_id() == projectId) {
                                    ProjectCache.setCurrentProject(p);
                                }
                            }
                        }
                    }
                }
            }            
        }, UserCache.getCurrentUser());
    }
    
    private void loadEpsData() {
        RemoteEPSService.getInstance().getEPSList(new DataManagerInterface() {

            @SuppressWarnings("unchecked")
            @Override
            public void getDataOnResult(ResultStatus status, List<?> list) {
                mHandler.sendEmptyMessage(0);
                if (status.getCode() == AnalysisManager.SUCCESS_DB_QUERY) {
                    if (list != null && list.size() > 0) {
                        EpsCache.setEpsLists((ArrayList<EPS>) list);
                    }
                }
            }
            
        }, UserCache.getCurrentUser());
    }
    
    private void loadObsData() {
        RemoteOBSService.getInstance().getOBSList(new DataManagerInterface() {

            @SuppressWarnings("unchecked")
            @Override
            public void getDataOnResult(ResultStatus status, List<?> list) {
                mHandler.sendEmptyMessage(0);
                if (status.getCode() != AnalysisManager.SUCCESS_DB_QUERY
                        && status.getMessage() != null
                        && !status.getMessage().equals("")) {
                    UtilTools.showToast(mContext,
                            status.getMessage());
                }
                if (status.getCode() == AnalysisManager.SUCCESS_DB_QUERY
                        && list != null && list.size() > 0) {
                    ObsCache.setObsLists((ArrayList<OBS>) list);
                }
            }
        }, UserCache.getCurrentUser());
    }
    
    private void loadRoleData() {
        Role role = new Role();
        role.setTenant_id(UserCache.getCurrentUser().getTenant_id());
        RemoteRoleService.getInstance().getRoleList(new DataManagerInterface() {

            @SuppressWarnings("unchecked")
            @Override
            public void getDataOnResult(ResultStatus status, List<?> list) {
                mHandler.sendEmptyMessage(0);
                if (status.getCode() != AnalysisManager.SUCCESS_DB_QUERY
                        && status.getMessage() != null
                        && !status.getMessage().equals("")) {
                    UtilTools.showToast(mContext, status.getMessage());
                }
                if ((status.getCode() == AnalysisManager.SUCCESS_DB_QUERY)
                        && list != null
                        && list.size() > 0) {
                    RoleCache.setRoleLists((ArrayList<Role>) list);
                }
            }
        }, role);
    }
    
    private void loadTenantData() {
        RemoteCommonService.getInstance().getTenantList(new DataManagerInterface() {

            @SuppressWarnings("unchecked")
            @Override
            public void getDataOnResult(ResultStatus status, List<?> list) {
                mHandler.sendEmptyMessage(0);
                if (status.getCode() != AnalysisManager.SUCCESS_DB_QUERY
                        && status.getMessage() != null
                        && !status.getMessage().equals("")) {
                    UtilTools.showToast(mContext, status.getMessage());
                }
                if ((status.getCode() == AnalysisManager.SUCCESS_DB_QUERY)
                        && list != null
                        && list.size() > 0) {
                    TenantCache.setTenantLists((ArrayList<Tenant>) list);
                }
            }
        }, new Tenant());
    }
    
    public void showProgressDialog(String text) {
        dismissProgressDialog();
        mProgressDialog = UtilTools.showProgressDialog(mContext, true, false);
    }

    public void dismissProgressDialog() {
        if (mProgressDialog != null && mProgressDialog.isShowing())
            mProgressDialog.dismiss();
    }

}
