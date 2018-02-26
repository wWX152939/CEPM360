package com.pm360.cepm360.app.module.project;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.LinearLayout;
import android.widget.Spinner;
import android.widget.TabHost;
import android.widget.TabHost.TabSpec;

import com.pm360.cepm360.R;
import com.pm360.cepm360.app.CepmApplication;
import com.pm360.cepm360.app.cache.EpsCache;
import com.pm360.cepm360.app.cache.PermissionCache;
import com.pm360.cepm360.app.cache.ProjectCache;
import com.pm360.cepm360.app.cache.RoleCache;
import com.pm360.cepm360.app.cache.UserCache;
import com.pm360.cepm360.app.common.activity.ActionBarActivity;
import com.pm360.cepm360.app.common.activity.DataManagerInterface;
import com.pm360.cepm360.app.common.view.RightButtonEditText;
import com.pm360.cepm360.app.common.view.swipemenulistview.SwipeMenu;
import com.pm360.cepm360.app.common.view.swipemenulistview.SwipeMenuCreator;
import com.pm360.cepm360.app.common.view.swipemenulistview.SwipeMenuItem;
import com.pm360.cepm360.app.common.view.swipemenulistview.SwipeMenuListView;
import com.pm360.cepm360.app.module.project.table.ButtonExpandCell;
import com.pm360.cepm360.app.module.project.table.ButtonItemCell;
import com.pm360.cepm360.app.module.project.table.ExpandCell;
import com.pm360.cepm360.app.module.project.table.ProgressBarCell;
import com.pm360.cepm360.app.module.project.table.TableAdapter;
import com.pm360.cepm360.app.module.project.table.TextCell;
import com.pm360.cepm360.app.module.schedule.CombinationActivity;
import com.pm360.cepm360.app.utils.UtilTools;
import com.pm360.cepm360.common.GLOBAL;
import com.pm360.cepm360.common.ResultStatus;
import com.pm360.cepm360.common.json.AnalysisManager;
import com.pm360.cepm360.common.util.DateUtils;
import com.pm360.cepm360.entity.EPS;
import com.pm360.cepm360.entity.OBS;
import com.pm360.cepm360.entity.Project;
import com.pm360.cepm360.entity.Role;
import com.pm360.cepm360.services.project.RemoteProjectService;
import com.pm360.cepm360.services.system.RemoteEPSService;
import com.pm360.cepm360.services.system.RemoteRoleService;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

/**
 * BaseProjectActivity父类
 * 
 * TabHost包含了3个Table: Recent/EpsSort/Nosort 最近打开/按EPS分类/不分类
 * 界面初始化，数据加载，刷新等操作 
 * 
 * 主要使用tableAdapter，构造函数初始化头，列表需要通过add逐个添加
 *
 */
public class BaseProjectActivity extends ActionBarActivity {

    private static final int ADD_PROJECT_REQUEST = 0;
    private static final String PROJECT_EDIT_PERMISSION = "1_1";

    private LinearLayout mProjectListLayout;
    private Spinner mSpinner;
    private RightButtonEditText mSearchEditText;
    private TabHost mViewTabHost;

    protected TableAdapter mRecentAdapter, mEpsSortAdapter, mNosortAdapter;
    private TableAdapter mCurrentAdapter;

    private SharedPreferences mSharedPreferences;
    private ArrayList<String> mRecents = new ArrayList<String>();
    public ArrayList<OBS> mOBSList = new ArrayList<OBS>();

    public ArrayList<Project> mProjectList = new ArrayList<Project>();
    protected Project mCurrentProject;
    private ArrayList<Role> mRoleList = new ArrayList<Role>();

    private ProgressDialog mProgressDialog;
    public String[] mStages;
    private boolean mHasPermission;

    private DataManagerInterface mProjectManager = new DataManagerInterface() {

        @Override
        public void getDataOnResult(ResultStatus status, List<?> list) {
            dismissLoadingProgress();
            if (status.getCode() != AnalysisManager.SUCCESS_DB_QUERY && status.getMessage() != null
                    && !status.getMessage().equals("")) {
                UtilTools.showToast(BaseProjectActivity.this, status.getMessage());
            }
            if ((status.getCode() == AnalysisManager.SUCCESS_DB_QUERY)
                    && list != null
                    && list.size() > 0) {
                for (Object object : list) {
                    if (object instanceof Project) {
                        addProject((Project) object);
                    }
                }
                if (mRecentAdapter.getCount() == 0) {
                    mSpinner.setSelection(1, true);
                }
                mEpsSortAdapter.refresh();
                mNosortAdapter.refresh();
                mRecentAdapter.refresh();
            }
        }
    };
    
    private DataManagerInterface mRoleManager = new DataManagerInterface() {

        @Override
        public void getDataOnResult(ResultStatus status, List<?> list) {
            dismissLoadingProgress();
            if (status.getCode() != AnalysisManager.SUCCESS_DB_QUERY
                    && status.getMessage() != null
                    && !status.getMessage().equals("")) {
                UtilTools.showToast(BaseProjectActivity.this, status.getMessage());
            }
            if ((status.getCode() == AnalysisManager.SUCCESS_DB_QUERY)
                    && list != null
                    && list.size() > 0) {
                for (Object object : list) {
                    if (object instanceof Role) {
                        mRoleList.add((Role) object);
                    }
                }
            }
            mEpsSortAdapter.refresh();
            mNosortAdapter.refresh();
            mRecentAdapter.refresh();
        }        
    };

    private DataManagerInterface mEpsManager = new DataManagerInterface() {

        @SuppressWarnings("unchecked")
        @Override
        public void getDataOnResult(ResultStatus status, List<?> list) {
            dismissLoadingProgress();
            if (status.getCode() != AnalysisManager.SUCCESS_DB_QUERY && status.getMessage() != null
                    && !status.getMessage().equals("")) {
                UtilTools.showToast(BaseProjectActivity.this, status.getMessage());
            }
            if (status.getCode() == AnalysisManager.SUCCESS_DB_QUERY && list != null
                    && list.size() > 0) {
                fillEpsSortAdapter((ArrayList<EPS>) list);
                mEpsSortAdapter.refresh();
                mNosortAdapter.refresh();
                mRecentAdapter.refresh();
            }
        }
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getMenuView().setVisibility(View.GONE);
        setContentView(R.layout.project_activity);
        mSharedPreferences = getSharedPreferences(
                GLOBAL.SHARED_PREFERENCES_KEY, Context.MODE_PRIVATE);
        String recentArray = mSharedPreferences.getString("recent", "");
        if (!recentArray.equals("")) {
            mRecents.addAll(Arrays.asList(recentArray.split(",")));
        }
        
        mStages = getResources().getStringArray(R.array.project_stage_items);
        mHasPermission = PermissionCache
                .hasSysPermission(PROJECT_EDIT_PERMISSION);
        
        initProjectList();
        
        if (EpsCache.isDataLoaded()) {
            fillEpsSortAdapter(EpsCache.getEpsLists());
            mEpsSortAdapter.refresh();
            mNosortAdapter.refresh();
            mRecentAdapter.refresh();
        } else {
            showLoadingProgress("Getting Esp list...");
            RemoteEPSService.getInstance().getEPSList(mEpsManager,
                    UserCache.getCurrentUser());
        }
        
        if (RoleCache.isDataLoaded()) {
            mRoleList.clear();
            for(Role role : RoleCache.getRoleLists()) {
                mRoleList.add(role);
            }
        } else {
            Role role = new Role();
            role.setTenant_id(UserCache.getCurrentUser().getTenant_id());
            showLoadingProgress("Getting role list...");
            RemoteRoleService.getInstance().getRoleList(mRoleManager, role);                    
        }
        
        if (ProjectCache.isDataLoaded()) {
            for(Project project : ProjectCache.getProjectLists()) {
                addProject(project);
            }
            if (mRecentAdapter.getCount() == 0) {
                mSpinner.setSelection(1, true);
            }
            mEpsSortAdapter.refresh();
            mNosortAdapter.refresh();
            mRecentAdapter.refresh();
        } else {
            showLoadingProgress("Getting Project list...");
            RemoteProjectService.getInstance().getProjectList(mProjectManager,
                    UserCache.getCurrentUser());
        }
    }

    private void initProjectList() {
        mProjectListLayout = (LinearLayout) findViewById(R.id.project_list_layout);
        mSearchEditText = (RightButtonEditText) mProjectListLayout.findViewById(R.id.search_edit);
        mSearchEditText.getRightButton().setImageResource(R.drawable.search_icon);
        mSearchEditText.getRightButton().setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                String search_text = mSearchEditText.getEditText().getText().toString();
                mCurrentAdapter.setSearchText(search_text, new int[] { 0 });
                mCurrentAdapter.refresh();
            }
        });
        mViewTabHost = (TabHost) mProjectListLayout
                .findViewById(R.id.view_tabhost);
        mViewTabHost.setup();
        String[] view_items = getResources().getStringArray(R.array.project_view_spinner_items);
        ArrayAdapter<String> adapter = new ArrayAdapter<String>(this,
                R.layout.spinner_item, view_items);

        adapter.setDropDownViewResource(R.layout.spinner_dropdown_item);
        mSpinner = (Spinner) mProjectListLayout.findViewById(R.id.project_view_spinner);
        mSpinner.setAdapter(adapter);
        int[] viewIds = new int[] {
                R.id.recent_list, R.id.eps_sort_list, R.id.nosort_list
        };
        for (int i = 0; i < viewIds.length; i++) {
            TabSpec spec = mViewTabHost.newTabSpec("" + i);
            spec.setIndicator("" + i);
            spec.setContent(viewIds[i]);
            mViewTabHost.addTab(spec);
        }

        mSpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {

            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {

                mViewTabHost.setCurrentTab(position);
                int visibility = mCurrentAdapter.isVisibleRightScrollView() ? View.VISIBLE
                        : View.GONE;
                switch (position) {
                    case 0:
                        mSearchEditText.setVisibility(View.GONE);
                        mRecentAdapter.setVisibleRightScrollView(visibility);
                        mCurrentAdapter = mRecentAdapter;
                        break;
                    case 1:
                        mSearchEditText.setVisibility(View.GONE);
                        mCurrentAdapter = mEpsSortAdapter;
                        break;
                    case 2:
                        mSearchEditText.setVisibility(View.GONE);
                        mCurrentAdapter = mNosortAdapter;
                        break;
                }
                mCurrentAdapter.setVisibleRightScrollView(visibility);
                mEpsSortAdapter.refresh();
                mNosortAdapter.refresh();
                mRecentAdapter.refresh();
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }

        });

        View recentList = mProjectListLayout.findViewById(R.id.recent_list);
        View epsSortList = mProjectListLayout.findViewById(R.id.eps_sort_list);
        View nosortList = mProjectListLayout.findViewById(R.id.nosort_list);

        mRecentAdapter = new TableAdapter(this, recentList,
                R.array.project_table_names,
                R.array.project_table_widths, Color.BLACK, Color.BLACK, getResources().getColor(
                        R.color.table_line), false, true, false);
        mEpsSortAdapter = new TableAdapter(this, epsSortList,
                R.array.project_table_names,
                R.array.project_table_widths, Color.BLACK, Color.BLACK, getResources().getColor(
                        R.color.table_line), true, true, false);
        mNosortAdapter = new TableAdapter(this, nosortList,
                R.array.project_table_names,
                R.array.project_table_widths, Color.BLACK, Color.BLACK, getResources().getColor(
                        R.color.table_line), false, true, false);
        mCurrentAdapter = mRecentAdapter;
        SwipeMenuCreator menuCreator = new SwipeMenuCreator() {

            @Override
            public void create(SwipeMenu menu) {
                switch (menu.getViewType()) {
                    case 0:
//                        SwipeMenuItem item01 = SwipeMenuItem.createSwipeMenuItem(menu.getContext(),
//                                new ColorDrawable(Color.rgb(0x94, 0x97, 0x98)),
//                                UtilTools.dp2pxW(menu.getContext(), 60f),
//                                R.drawable.ic_action_add);
                        SwipeMenuItem item02 = SwipeMenuItem.createSwipeMenuItem(menu.getContext(),
                                new ColorDrawable(Color.rgb(0xF9, 0x3F, 0x25)),
                                UtilTools.dp2pxW(menu.getContext(), 60f),
                                R.drawable.ic_action_remove);
//                        menu.addMenuItem(item01);
                        menu.addMenuItem(item02);
                        break;
                    case 1:
                        SwipeMenuItem item11 = SwipeMenuItem.createSwipeMenuItem(menu.getContext(),
                                new ColorDrawable(Color.rgb(0x94, 0x97, 0x98)),
                                UtilTools.dp2pxW(menu.getContext(), 60f),
                                R.drawable.ic_action_add);
                        menu.addMenuItem(item11);
                        break;
                }
            }

        };
        mEpsSortAdapter.setLeftMenuCreator(menuCreator);
        mEpsSortAdapter
                .setLeftOnMenuItemClickListener(new SwipeMenuListView.OnMenuItemClickListener() {

                    @Override
                    public void onMenuItemClick(int position, SwipeMenu menu, int index) {
                        Project project = findProjectById((int) mEpsSortAdapter.getItemId(position));
                        mEpsSortAdapter.setSelectedPosition(position);
                        switch (menu.getViewType()) {
                            case 0: // 删除项目
                                if (checkPermission(project, true)) {
                                    removeProject(position);
                                }
//                                switch (index) {
//                                    case 0:
//                                        if (mHasPermission) {
//                                            ExpandCell expandCell = (ExpandCell) mEpsSortAdapter
//                                                    .getItem(position).getValueAt(0);
//                                            showProjectAddActivity(expandCell.getParentId());
//                                        } else {
//                                            UtilTools.showToast(BaseProjectActivity.this,
//                                                    R.string.no_edit_permission);
//                                        }
//                                        break;
//                                    case 0:
//                                        if (checkPermission(project, true)) {
//                                            removeProject(position);
//                                        }
//                                        break;
//                                }
                                break;
                            case 1: // 创建项目
                                if (mHasPermission) {
                                    ExpandCell expandCell = (ExpandCell) mEpsSortAdapter.getItem(
                                            position).getValueAt(0);
                                    showProjectAddActivity(expandCell.getExpandId());
                                } else {
                                    UtilTools.showToast(BaseProjectActivity.this,
                                            R.string.no_edit_permission);
                                }
                                break;

                        }

                    }
                });
    }

    /**
     * 添加项目到Adapter显示， 或更新某一个项目
     * 
     * @param project
     */
    public void addProject(final Project project) {
        String[] values = new String[] {
                project.getName(),
                project.getProject_number(),
                mStages[project.getStatus()],
                project.getProgress() + "%",
                DateUtils.dateToString(DateUtils.FORMAT_SHORT,
                        project.getActual_start_time()),
                DateUtils.dateToString(DateUtils.FORMAT_SHORT,
                        project.getActual_end_time())
        };
        String[] headNames = mEpsSortAdapter.getHeadNames();
        int[] arrHeadWidths = mEpsSortAdapter.getArrHeadWidths();
        List<TextCell> epsSortRowValues = new ArrayList<TextCell>();
        List<TextCell> noSortRowValues = new ArrayList<TextCell>();
        View.OnClickListener listener = new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                openProject(project);
            }
        };
        /**
         * eps 添加project 的btn，响应事件只针对img响应
         */
        ButtonExpandCell buttonExpandCell = new ButtonExpandCell(values[0],
                headNames[0],
                arrHeadWidths[0],
                project.getProject_id(), project.getEps_id(), false, R.drawable.project_icon);
        buttonExpandCell.setOnClickListener(listener);
        epsSortRowValues.add(buttonExpandCell);
//        ExpandCell expandCell = new ExpandCell(values[0],
//              headNames[0],
//              arrHeadWidths[0],
//              project.getProject_id(), project.getEps_id(), false);
//        epsSortRowValues.add(expandCell);
        
        /**
         * nosort 添加project 的btn，响应事件只针对img响应
         */
        ButtonItemCell buttonItemCell = new ButtonItemCell(values[0], headNames[0],
                arrHeadWidths[0], R.drawable.project_icon);        
        buttonItemCell.setOnClickListener(listener);
        noSortRowValues.add(buttonItemCell);
//        TextCell itemCell = new TextCell(values[0], headNames[0], arrHeadWidths[0]);
//        noSortRowValues.add(itemCell);
        for (int j = 1; j < arrHeadWidths.length; j++) {
            TextCell itemCellTemp;
            if (j == 3) {
                itemCellTemp = new ProgressBarCell(project.getProgress(), headNames[j],
                        arrHeadWidths[j]);
            } else {
                itemCellTemp = new TextCell(values[j], headNames[j],
                        arrHeadWidths[j]);
//                switch (j) {
//                    case 2:
//                    case 4:
//                    case 5:
//                        itemCellTemp.setGravity(Gravity.CENTER);
//                }
            }
            epsSortRowValues.add(itemCellTemp);
            noSortRowValues.add(itemCellTemp);
        }

        int project_id = project.getProject_id();
        if (findProjectById(project_id) == null) {
            mEpsSortAdapter.addItem(project_id, epsSortRowValues);
            mNosortAdapter.addItem(project_id, noSortRowValues);
            if (mRecents.contains(project_id + "")) {
                mRecentAdapter.addItem(project_id, noSortRowValues);
            }
            mProjectList.add(project);
        } else {
            mEpsSortAdapter.setItem(project_id, epsSortRowValues);
            mNosortAdapter.setItem(project_id, noSortRowValues);
            if (mRecents.contains(project_id + "")) {
                mRecentAdapter.setItem(project_id, noSortRowValues);
            }
            for (Project project2 : mProjectList) {
                if (project2.getProject_id() == project_id) {
                    mProjectList.set(mProjectList.indexOf(project2), project);
                }
            }
            mCurrentProject = project;
        }
    }

    public Project findProjectById(int project_id) {
        Project res = null;
        for (Project project : mProjectList) {
            if (project.getProject_id() == project_id) {
                res = project;
                break;
            }
        }
        return res;
    }

    private void openProject(final Project project) {
        //PermissionCache.setProjectPermissions(null);
    	/**
    	 * 打开project，并将最近打开的项目写入到xml中，如果打开的项目在最近打开的列表中，将该项目提升至第一位
    	 */
        mRecents.clear();
        int size = mRecentAdapter.getCount();
        for (int i = 0; i < size; i++) {
            int project_id = (int) mRecentAdapter.getItemId(i);
            mRecents.add(project_id + "");
        }
        int project_id = project.getProject_id();
        if (mRecents.contains(project_id + "")) {
            mRecents.remove(project_id + "");
        }
        mRecents.add(0, project_id + "");

        SharedPreferences.Editor editor = mSharedPreferences.edit();
        StringBuilder recentsBuilder = new StringBuilder();
        for (int i = 0; i < mRecents.size() && i < 10; i++) {
            recentsBuilder.append(mRecents.get(i) + ",");
        }
        String s = recentsBuilder.substring(0, recentsBuilder.length() - 1);
        editor.putString("recent", s);
        editor.commit();

        ProjectCache.setCurrentProject(project);
        
        int enterpriseType = ((CepmApplication) getApplicationContext())
                .getEnterpriseType();
        if (enterpriseType == 0) { // 管理方
            // 回到主页的组合管理界面
            Intent intent = new Intent();
            intent.putExtra("project", project);
            setResult(Activity.RESULT_OK, intent);
            finish();
        } else {
            // 跳转到计划管理模块
            Intent intent = new Intent(Intent.ACTION_MAIN);
            intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
            intent.setClass(BaseProjectActivity.this, CombinationActivity.class);
            startActivity(intent);
            finish();
        }

//        showLoadingProgress("Getting permission of Project list...");
//        RemoteCommonService.getInstance().getPermissionByProject(
//                new DataManagerInterface() {
//
//                    @Override
//                    public void getDataOnResult(ResultStatus status,
//                            List<?> list) {
//                        dismissLoadingProgress();
//                        if (status.getCode() != AnalysisManager.SUCCESS_DB_QUERY
//                                && status.getMessage() != null
//                                && !status.getMessage().equals("")) {
//                            UtilTools.showToast(BaseProjectActivity.this, status.getMessage());
//                        }
//                        if (status.getCode() == AnalysisManager.SUCCESS_DB_QUERY
//                                && list != null
//                                && list.size() > 0) {
//                            StringBuilder builder = new StringBuilder();
//                            for (Object object : list) {
//                                if (object instanceof Role) {
//                                    Role role = (Role) object;
//                                    String action = role
//                                            .getAction();
//                                    if (action != null) {
//                                        builder.append(action + ",");
//                                    }
//                                }
//                            }
//                            String role_text = builder.toString();
//                            if (role_text.endsWith(",")) {
//                                role_text = role_text.substring(0,
//                                        role_text.length() - 1);
//                            }
//                            String[] permissions = role_text
//                                    .split(",");
//                            PermissionCache
//                                    .setProjectPermissions(
//                                    permissions);
//                        }
//                        
//                        int enterpriseType = ((CepmApplication) getApplicationContext())
//                                .getEnterpriseType();
//                        if (enterpriseType == 0) { // 管理方
//                            // 回到主页的组合管理界面                            
//                            Intent intent = new Intent();
//                            intent.putExtra("project", project);
//                            setResult(Activity.RESULT_OK, intent);
//                            finish();
//                        } else {
//                            // 跳转到计划管理模块
//                            Intent intent = new Intent(Intent.ACTION_MAIN);
//                            intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
//                            intent.setClass(BaseProjectActivity.this,  ScheduleActivity.class);
//                            startActivity(intent);
//                            finish();
//                        }
//                    }
//                }, project.getProject_id(), UserCache.getCurrentUser());
    }

    private void removeProject(final int position) {
        final Project project = new Project();
        project.setProject_id(mEpsSortAdapter.getItem(position)
                .getRowId());
        UtilTools.deleteConfirm(this, new UtilTools.DeleteConfirmInterface() {
            
            @Override
            public void deleteConfirmCallback() {
                showLoadingProgress("Getting Project list...");
                RemoteProjectService.getInstance().deleteProject(
                        new DataManagerInterface() {

                            @Override
                            public void getDataOnResult(ResultStatus status,
                                    List<?> list) {
                                dismissLoadingProgress();
                                if (status.getCode() != AnalysisManager.SUCCESS_DB_DEL
                                        && status.getMessage() != null && !status.getMessage().equals("")) {
                                    UtilTools.showToast(BaseProjectActivity.this, status.getMessage());
                                }

                                if (status.getCode() == AnalysisManager.SUCCESS_DB_DEL) {
                                    int project_id = mEpsSortAdapter.removeSelectItem(position);
                                    mNosortAdapter.removeItemById(project_id);
                                    mRecentAdapter.removeItemById(project_id);
                                    mProjectList.remove(findProjectById(project_id));
                                    mEpsSortAdapter.refresh();
                                    mNosortAdapter.refresh();
                                    mRecentAdapter.refresh();
                                    ProjectCache.removeProject(project);
                                }
                            }
                        }, project);                
            }
        });
    }
    
    /**
     * 填写eps行数据
     * @param epsList
     */
    private void fillEpsSortAdapter(ArrayList<EPS> epsList) {
        String[] headNames = mEpsSortAdapter.getHeadNames();
        int[] arrHeadWidths = mEpsSortAdapter.getArrHeadWidths();
        for (Object object : epsList) {
            if (object instanceof EPS) {
                EPS eps = (EPS) object;
                List<TextCell> rowValues = new ArrayList<TextCell>();
                ExpandCell expandCell = new ExpandCell(eps.getName(), headNames[0],
                        arrHeadWidths[0], eps.getEps_id(), eps.getParents_id(), true);
                rowValues.add(expandCell);
                //TextCell itemCell = new TextCell(eps.getCode(), headNames[1],
                //        arrHeadWidths[1]);
                //rowValues.add(itemCell);
                for (int j = 1; j < arrHeadWidths.length; j++) {
                    TextCell itemCellTemp = new TextCell("", headNames[j],
                            arrHeadWidths[j]);
                    rowValues.add(itemCellTemp);
                }
                mEpsSortAdapter.addItem(eps.getEps_id(), rowValues, true, false);
            }
        }
    }
    
    public OBS findOBSById(int obs_id) {
        OBS res = null;
        for (OBS obs : mOBSList) {
            if (obs.getObs_id() == obs_id) {
                res = obs;
                break;
            }
        }
        return res;
    }

    public String getRoleNameById(int role_id) {
        String res = "";
        for (Role role : mRoleList) {
            if (role.getRole_id() == role_id) {
                res = role.getName();
                break;
            }
        }
        return res;
    }
    
    public String getRoleNameByCode(String code) {
        String res = "";
        for (Role role : mRoleList) {
            if (code.equals(role.getCode())) {
                res = role.getName();
                break;
            }
        }
        return res;
    }
    
    public void showLoadingProgress(String text) {
        dismissLoadingProgress();
        mProgressDialog = UtilTools.showProgressDialog(BaseProjectActivity.this, true, false);
    }

    public void dismissLoadingProgress() {
        if (mProgressDialog != null && mProgressDialog.isShowing())
            mProgressDialog.dismiss();
    }

    private void showProjectAddActivity(int epsId) {
        Intent intent = new Intent();
        intent.setClass(this, ProjectCreaterActivity.class);
        intent.putExtra("eps_id", epsId);
        intent.putExtra("operation", 2);
        startActivityForResult(intent, ADD_PROJECT_REQUEST);
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (resultCode == Activity.RESULT_OK) {
            switch (requestCode) {
                case ADD_PROJECT_REQUEST:
                    Project project = (Project) data.getSerializableExtra("project");
                    addProject(project);
                    int selectedPosition = mEpsSortAdapter.getSelectedPosition();
                    if (selectedPosition != -1) {
                        ExpandCell expandCell = (ExpandCell) mEpsSortAdapter.getItem(
                                selectedPosition)
                                .getValueAt(0);
                        if (expandCell.isFolder() && !expandCell.isExpanded()) {
                            mEpsSortAdapter.setExpandList(selectedPosition, true);
                        }
                    }
                    mEpsSortAdapter.refresh();
                    mNosortAdapter.refresh();
                    mRecentAdapter.refresh();
                    
                    break;
            }
        }
    }

    public boolean checkPermission(Project project, boolean isShow) {
        if (project == null) {
            if (isShow)
                UtilTools.showToast(this, R.string.select_first_project_toast);
            return false;
        }
//        if (project.getCreater() == UserCache.getCurrentUser()
//                .getUser_id()) {
//            return true;
//        }
        if (PermissionCache.hasSysPermission(GLOBAL.SYS_ACTION[0][0])) {
            return true;
        }
        
        if (isShow)
            UtilTools.showToast(this, R.string.no_edit_permission);
        return false;
    }

}
