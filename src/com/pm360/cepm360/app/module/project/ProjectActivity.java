package com.pm360.cepm360.app.module.project;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.Dialog;
import android.content.Intent;
import android.graphics.Color;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.text.Editable;
import android.text.Selection;
import android.text.TextWatcher;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.Spinner;
import android.widget.TextView;

import com.pm360.cepm360.R;
import com.pm360.cepm360.app.cache.ObsCache;
import com.pm360.cepm360.app.cache.ProjectCache;
import com.pm360.cepm360.app.cache.UserCache;
import com.pm360.cepm360.app.common.activity.DataManagerInterface;
import com.pm360.cepm360.app.common.activity.ListSelectActivity;
import com.pm360.cepm360.app.common.activity.OwnerSelectActivity;
import com.pm360.cepm360.app.common.custinterface.PageChangeListenerInterface;
import com.pm360.cepm360.app.common.view.DatePickText;
import com.pm360.cepm360.app.common.view.FloatingMenuView;
import com.pm360.cepm360.app.common.view.NumberSeekBar;
import com.pm360.cepm360.app.common.view.OptionsMenuView;
import com.pm360.cepm360.app.common.view.RightButtonEditText;
import com.pm360.cepm360.app.common.view.parent.BaseToast;
import com.pm360.cepm360.app.common.view.parent.BaseViewPager;
import com.pm360.cepm360.app.module.cooperation.StakeholderActivity;
import com.pm360.cepm360.app.module.project.table.ButtonItemCell;
import com.pm360.cepm360.app.module.project.table.ExpandCell;
import com.pm360.cepm360.app.module.project.table.TableAdapter;
import com.pm360.cepm360.app.module.project.table.TextCell;
import com.pm360.cepm360.app.utils.UtilTools;
import com.pm360.cepm360.common.GLOBAL;
import com.pm360.cepm360.common.ResultStatus;
import com.pm360.cepm360.common.json.AnalysisManager;
import com.pm360.cepm360.entity.Cooperation;
import com.pm360.cepm360.entity.OBS;
import com.pm360.cepm360.entity.P_LWDW;
import com.pm360.cepm360.entity.Project;
import com.pm360.cepm360.entity.Tenant;
import com.pm360.cepm360.entity.User;
import com.pm360.cepm360.services.common.RemoteCommonService;
import com.pm360.cepm360.services.project.RemoteProjectService;
import com.pm360.cepm360.services.system.RemoteOBSService;

/**
 * ProjectActivity 继承BaseProjectActivity
 * 
 * 主要管理项目的属性，如常用，项目人员，写作单位，仓库的属性设置
 * 施工方的项目属性会有仓库，其他没有。
 *
 */
public class ProjectActivity extends BaseProjectActivity {

    private static final int LWDW_DISTRIBUTION_REQUEST = 1;
    private static final int MEMBER_DISTRIBUTION_REQUEST = 2;
    private static final int OWNER_DISTRIBUTION_REQUEST = 3;
    
    private View[] mTabs;
    //private ImageView[] mTabFgViews;
    private RelativeLayout mContentTabHost;

    private TableAdapter mMemberAdapter;
    private TableAdapter mCooperationAdapter;
    private TableAdapter mStorehouseAdapter;
    
    private OptionsMenuView mStorehouseOptionsMenuView;
    
    private FloatingMenuView mMemberMenu;
    private FloatingMenuView mStorehouseMenu;

    private ArrayList<User> mMemberList = new ArrayList<User>();
    private ArrayList<Tenant> mCooperationList = new ArrayList<Tenant>();
    private AsyncTask<Object, Object, Object> mMemberAsyncTask;
    private AsyncTask<Object, Object, Object> mCooperationAsyncTask;
    
    private TextView mStorehouseTitle;
    private EditText mStorehouse;
    private Button mStorehouseOK;
    private ImageView mStorehouseCancel;
    private Dialog mStorehouseDialog;
    private EditText mCommonNumber, mCommonName, mCommonLocation, 
    		mCommonMark, mMaterialCostControl,
            mOtherCostControl, mCommonWorkLoad;
    private NumberSeekBar mCommonProgress;
    private Spinner mCommonStage;
    private DatePickText mCommonStartDate, mCommonEndDate;
    private RightButtonEditText mCommonOwner;
    private Button mCommmonUpdate;
    private int mCurrentTab = 0;
    
    
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        
//        mEnterpriseType = ((CepmApplication) getApplicationContext())
//                .getEnterpriseType();
//        if (mEnterpriseType == 2) {
//            // 施工方
//            initConstructorTabs();            
//        } else {
//            // 其他企业类型
//            initCommonTabs();
//        }    
        initConstructorTabs();
        setAdpaterItemClick();
    }
    
    private void setAdpaterItemClick() {
        mRecentAdapter.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                int project_id = (int) mRecentAdapter.getItemId(position);
                Project project = findProjectById(project_id);
                if (mRecentAdapter.getSelectedPosition() != position) {
                    mRecentAdapter.setSelectedPosition(position);
                    initTabContent(project, mCurrentTab);
                    setFloatingMenuStatus(project);
                    mCurrentProject = project;
                    mRecentAdapter.setSelectedPosition(position);
                }
            }
        });
        mEpsSortAdapter.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                ExpandCell expandCell = (ExpandCell) mEpsSortAdapter.getItem(
                        position).getValueAt(0);
                if (!mEpsSortAdapter.setExpandList(position, !expandCell.isExpanded())) {
                    int project_id = (int) mEpsSortAdapter.getItemId(position);
                    Project project = findProjectById(project_id);
                    if (mEpsSortAdapter.getSelectedPosition() != position) {
                        mEpsSortAdapter.setSelectedPosition(position);
                        initTabContent(project, mCurrentTab);
                        setFloatingMenuStatus(project);
                        mCurrentProject = project;
                    }
                } else {
                    mCurrentProject = null;
                    resetTabContent();
                }
                mEpsSortAdapter.setSelectedPosition(position);
            }
        });
        mNosortAdapter.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                int project_id = (int) mNosortAdapter.getItemId(position);
                Project project = findProjectById(project_id);
                if (mNosortAdapter.getSelectedPosition() != position) {
                    mNosortAdapter.setSelectedPosition(position);
                    initTabContent(project, mCurrentTab);
                    setFloatingMenuStatus(project);
                    mCurrentProject = project;
                }
            }
        });
    }
    
    private PageChangeListenerInterface pageChangeListener = new PageChangeListenerInterface() {

        @Override
        public void onExtraPageScrolled() {            
        }

        @Override
        public void onExtraPageSelected(int position) {
            mCurrentTab = position;
            initTabContent(mCurrentProject, mCurrentTab);
        }

        @Override
        public void onExtraPageScrollStateChanged() {
        }
        
    };
    
    @SuppressLint("InflateParams") 
    private void initConstructorTabs() {
    	mContentTabHost = (RelativeLayout) findViewById(R.id.content_tabhost);
    	mTabs = new View[4];
    	mTabs[0] = LayoutInflater.from(this).inflate(
              R.layout.project_tab_common, null);
        mTabs[1] = LayoutInflater.from(this).inflate(
                R.layout.project_table4, null);
        mTabs[2] = LayoutInflater.from(this).inflate(
                R.layout.project_table4, null);
        mTabs[3] = LayoutInflater.from(this).inflate(
                R.layout.project_table4, null);
        initCommonTab(mTabs[0]); // 常用
        initMemberTab(mTabs[1]); // 项目成员
        initCooperationTab(mTabs[2]); // 协作单位
        initStorehouseTab(mTabs[3]); // 项目仓库
        initStorehouseOptionsMenuView();
        BaseViewPager viewPager = new BaseViewPager(this, mContentTabHost);
    	List<View> viewList = new ArrayList<View>();
    	viewList.add(mTabs[0]);
    	viewList.add(mTabs[1]);
    	viewList.add(mTabs[2]);
    	viewList.add(mTabs[3]);
    	viewPager.init(R.array.project_constructor_content_tab_labels, viewList, null);
    	viewPager.setOnPageChangeListenerInterface(pageChangeListener);
    }

    @SuppressLint("InflateParams") 
    private void initCommonTabs() {
    	mContentTabHost = (RelativeLayout) findViewById(R.id.content_tabhost);
    	mTabs = new View[3];
    	mTabs[0] = LayoutInflater.from(this).inflate(
              R.layout.project_tab_common, null);
        mTabs[1] = LayoutInflater.from(this).inflate(
                R.layout.project_table4, null);
        mTabs[2] = LayoutInflater.from(this).inflate(
                R.layout.project_table4, null);
        initCommonTab(mTabs[0]); // 常用
        initMemberTab(mTabs[1]); // 项目成员
        initCooperationTab(mTabs[2]); // 协作单位
        BaseViewPager viewPager = new BaseViewPager(this, mContentTabHost);
    	List<View> viewList = new ArrayList<View>();
    	viewList.add(mTabs[0]);
    	viewList.add(mTabs[1]);
    	viewList.add(mTabs[2]);
    	viewPager.init(R.array.project_management_content_tab_labels, viewList, null);
    	viewPager.setOnPageChangeListenerInterface(pageChangeListener);
    }
    
    private void updateProject(Project project, String text) {
        final Project updateProject = (Project) project.clone();
        if (text.endsWith(","))
            text = text.substring(0, text.length() - 1);
        //int currentTabId = getCurrentTabId(mCurrentTab);
        switch (mCurrentTab) {
            case 0:
                updateProject.setName(mCommonName.getText().toString());
                updateProject.setProject_number(mCommonNumber.getText().toString());
                updateProject.setLocation(mCommonLocation.getText().toString());
                updateProject.setActual_start_time(mCommonStartDate.getDate());
                updateProject.setActual_end_time(mCommonEndDate.getDate());
                updateProject.setProgress(mCommonProgress.getProgress());
                updateProject.setStatus(mCommonStage.getSelectedItemPosition());
                if (mMaterialCostControl.getText().toString().equals("")) {
                    updateProject.setControl_cost(0);
                } else {
                    updateProject.setControl_cost(Double.valueOf(
                            mMaterialCostControl.getText().toString()).doubleValue());
                }
                if (mOtherCostControl.getText().toString().equals("")) {
                    updateProject.setOther_control_cost(0);
                } else {
                    updateProject.setOther_control_cost(Double.valueOf(
                            mOtherCostControl.getText().toString()).doubleValue());
                }
                if (mCommonWorkLoad.getText().toString().equals("")) {
                    updateProject.setWork_load(0);
                } else {
                    updateProject.setWork_load(Integer.valueOf(
                            mCommonWorkLoad.getText().toString()).intValue());
                }
                updateProject.setMark(mCommonMark.getText().toString());
                break;
            case 3:
                updateProject.setStorehouse(text);
                break;
        }

        showLoadingProgress("Update a Project...");
        RemoteProjectService.getInstance().updateProject(
                new DataManagerInterface() {

                    @Override
                    public void getDataOnResult(ResultStatus status, List<?> list) {
                        dismissLoadingProgress();
                        BaseToast.showView(ProjectActivity.this, status.getMessage());
                        if (status.getCode() == AnalysisManager.SUCCESS_DB_UPDATE) {
                            for (Project project : mProjectList) {
                                if (project.getProject_id() == updateProject
                                        .getProject_id()) {
                                    mProjectList.set(mProjectList.indexOf(project),
                                            updateProject);
                                    ProjectCache.updateProject(updateProject);
                                }
                            }
                            //int currentTabId = getCurrentTabId(mCurrentTab);
                            switch (mCurrentTab) {
                                case 0:
                                    addProject(updateProject);
                                    mEpsSortAdapter.refresh();
                                    mNosortAdapter.refresh();
                                    mRecentAdapter.refresh();
                                    break;
                                case 3:
                                    refreshStorehouseList(updateProject);
                                    break;
                            }
                            mCurrentProject = updateProject;
                        }
                    }
                }, updateProject);
    }
    
    /*
     * 清除 常用 tab 数据
     */
    private void resetTabContent() {
        mCommonNumber.setText("");
        mCommonName.setText("");
        mCommonLocation.setText("");
        mCommonProgress.setProgress(0);
        mCommonMark.setText("");
        mMaterialCostControl.setText("");
        mOtherCostControl.setText("");
        mCommonWorkLoad.setText("");
        mCommonOwner.getEditText().setText("");
        mCommonStartDate.clear();
        mCommonEndDate.clear();
        mMemberAdapter.clear();
        mMemberAdapter.refresh();
        mMemberList.clear();
        if (mStorehouseAdapter != null) {
            mStorehouseAdapter.clear();
            mStorehouseAdapter.refresh();
        }
        mCommmonUpdate.setVisibility(View.GONE);
    }
    
    /*
     * 初始化常用Tab
     */
    private void initCommonTab(View tab) {
        mCommonNumber = (EditText) tab.findViewById(R.id.project_number);
        mCommonName = (EditText) tab.findViewById(R.id.project_name);
        mCommonLocation = (EditText) tab.findViewById(R.id.project_location);
        mCommonProgress = (NumberSeekBar) tab.findViewById(R.id.seek_bar);
        mCommonProgress.setMyPadding(0, 0, 4, 0);
        mCommonProgress.setTextColor(Color.WHITE);
        
        mCommonMark = (EditText) tab.findViewById(R.id.project_mark);
        mCommonStage = (Spinner) tab.findViewById(R.id.project_stage);
        mStages = getResources().getStringArray(R.array.project_stage_items);
        ArrayAdapter<String> adapter = new ArrayAdapter<String>(this,
                R.layout.spinner_item, mStages);
        adapter.setDropDownViewResource(R.layout.spinner_dropdown_item);
        mCommonStage.setAdapter(adapter);
        mCommonStartDate = (DatePickText) tab.findViewById(R.id.project_start_date);
        mCommonEndDate = (DatePickText) tab.findViewById(R.id.project_end_date);
        mCommonOwner = (RightButtonEditText) tab.findViewById(R.id.project_department);
        mCommonOwner.getEditText().setEnabled(false);        
        mCommonOwner.getRightButton().setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
            	if (mCurrentProject != null) {
	                Intent intent = new Intent();
	                intent.setClass(v.getContext(),
	                        OwnerSelectActivity.class);
	                intent.putExtra("title", getString(R.string.project_department));
	                intent.putExtra("project", ProjectCache.getCurrentProject());
	                startActivityForResult(intent, OWNER_DISTRIBUTION_REQUEST);
            	}
            }
        });

        mCommmonUpdate = (Button) tab.findViewById(R.id.project_common_update);
        mCommmonUpdate.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                if (mCurrentProject == null) {
                    UtilTools.showToast(v.getContext(), R.string.select_project_toast);
                } else {
                    updateProject(mCurrentProject, "");
                }
            }
        });
        
        mMaterialCostControl = (EditText) tab.findViewById(R.id.project_material_control);
        mOtherCostControl = (EditText) tab.findViewById(R.id.project_other_control);
        mCommonWorkLoad = (EditText) tab.findViewById(R.id.project_workload);
        mCommonWorkLoad.addTextChangedListener(new TextWatcher() {
			
			@Override
			public void onTextChanged(CharSequence s, int start, int before, int count) {
				if (count > 0) {
					double num = Double.parseDouble(s.toString());
					if (num > 2147483647) {
						s = Integer.toString(2147483647);
						mCommonWorkLoad.setText(s);
						Selection.setSelection(mCommonWorkLoad.getText(), s.length());
					}
				}
			}
			
			@Override
			public void beforeTextChanged(CharSequence s, int start, int count,
					int after) {
				// TODO Auto-generated method stub
				
			}
			
			@Override
			public void afterTextChanged(Editable s) {
				// TODO Auto-generated method stub
				
			}
		});

    }
    
    
    /*
     * 刷新 常用 tab
     */
    private void initTabContent(Project project, int currentTab) {
        if ((project != null && !project.equals(mCurrentProject)) || project == null) {
            resetTabContent();
        }
        //int currentTabId = getCurrentTabId(currentTab);
        if (currentTab == 0) {
            boolean enabled = checkPermission(project, false);
            mCommonNumber.setEnabled(enabled);
            mCommonName.setEnabled(enabled);
            mCommonLocation.setEnabled(enabled);
            mCommonProgress.setEnabled(enabled);
            mMaterialCostControl.setEnabled(enabled);
            mOtherCostControl.setEnabled(enabled);
            mCommonWorkLoad.setEnabled(enabled);
            mCommonMark.setEnabled(enabled);
            mCommonStage.setEnabled(enabled);
            mCommonStartDate.setEnabled(enabled);
            mCommonEndDate.setEnabled(enabled);
            mCommonOwner.setEnabled(enabled);
            mCommonOwner.getEditText().setEnabled(false);
            mCommmonUpdate.setEnabled(enabled);
        }

        if (currentTab == 1 && mOBSList.size() == 0) {
            if (ObsCache.isDataLoaded()) {
                mOBSList.clear();
                for(OBS obs : ObsCache.getObsLists()) {
                    mOBSList.add(obs);
                }
            } else {
                showLoadingProgress("Getting OBS list...");
                RemoteOBSService.getInstance().getOBSList(
                        new DataManagerInterface() {

                            @Override
                            public void getDataOnResult(ResultStatus status,
                                    List<?> list) {
                                dismissLoadingProgress();
                                if (status.getCode() != AnalysisManager.SUCCESS_DB_QUERY
                                        && status.getMessage() != null
                                        && !status.getMessage().equals("")) {
                                    UtilTools.showToast(ProjectActivity.this,
                                            status.getMessage());
                                }
                                if (status.getCode() == AnalysisManager.SUCCESS_DB_QUERY
                                        && list != null && list.size() > 0) {
                                    for (Object object : list) {
                                        if (object instanceof OBS) {
                                            OBS obs = (OBS) object;
                                            mOBSList.add(obs);
                                            ObsCache.addObs(obs);
                                        }
                                    }
                                }
                            }
                        }, UserCache.getCurrentUser());
            }
        }

        if (project != null) {
            project.setTab_flag(2);
            switch (currentTab) {
                case 0:
                    mCommonName.setText(project.getName());
                    mCommonNumber.setText(project.getProject_number());
                    if (project.getOwner() != 0) {
                        String owner = UserCache.getUserMaps()
                                .get(String.valueOf(project.getOwner())); 
                        mCommonOwner.getEditText().setText(owner == null ? "" : owner);
                    }
                    mCommonProgress.setProgress(project.getProgress());
                    mCommonLocation.setText(project.getLocation());
                    mCommonStartDate.setDate(project.getActual_start_time());
                    mCommonEndDate.setDate(project.getActual_end_time());
                    mCommonStage.setSelection(project.getStatus());
                    mCommonMark.setText(project.getMark());
                    mMaterialCostControl
                            .setText(project.getControl_cost() == 0.0 ? ""
                                    : project.getControl_cost() + "");
                    mOtherCostControl
                            .setText(project.getOther_control_cost() == 0.0 ? ""
                                    : project.getOther_control_cost() + "");
                    mCommonWorkLoad.setText(project.getWork_load() == 0 ? ""
                            : project.getWork_load() + "");
                    break;
                case 1:
                    if (!project.equals(mCurrentProject) || mMemberAdapter.getCount() == 0)
                        refreshMemberList(project);
                    break;
                case 2:
                    if (!project.equals(mCurrentProject) || mCooperationAdapter.getCount() == 0)
                        refreshCooperationList(project);
                    break;
                case 3:
                    if (!project.equals(mCurrentProject) || mStorehouseAdapter.getCount() == 0)
                        refreshStorehouseList(project);
                    break;
            }
        }
    }

    /*
     * 初始化项目人员Tab
     */
    private void initMemberTab(View tab) {
        mMemberAdapter = new TableAdapter(this, tab,
                R.array.project_member_table_names,
                R.array.project_member_table_widths, Color.BLACK, Color.BLACK, getResources()
                        .getColor(R.color.table_line), false, false, false);
        mMemberMenu = mMemberAdapter.getFloatingMenuView();
        mMemberMenu.addPopItem(getString(R.string.distribution),
                R.drawable.menu_icon_allocation);
        mMemberMenu.addPopItem(getString(R.string.modify),
                R.drawable.menu_icon_update);
        mMemberMenu.setPopOnItemClickListener(new
                AdapterView.OnItemClickListener() {
                    @Override
                    public void onItemClick(AdapterView<?> parent, View view,
                            int position, long id) {

                        if (!checkPermission(mCurrentProject, true)) {
                            mMemberMenu.dismiss();
                            return;
                        }
                        if (mCurrentProject == null) {
                            UtilTools.showToast(parent.getContext(),
                                    R.string.select_project_toast);
                            mMemberMenu.dismiss();
                            return;
                        }
                        Intent intent = new Intent();
                        intent.setClass(parent.getContext(),
                                ProjectCreaterActivity.class);
                        intent.putExtra("project", mCurrentProject);
                        intent.putExtra("user_list", mMemberList);
                        intent.putExtra("type", 2);
                        intent.putExtra("operation", position);
                        startActivityForResult(intent, MEMBER_DISTRIBUTION_REQUEST);
                        mMemberMenu.dismiss();
                    }
                });

    }
    
    /*
     * 初始化协作单位Tab
     */
    private void initCooperationTab(View tab) {
        mCooperationAdapter = new TableAdapter(this, tab,
                R.array.project_cooperation_table_names,
                R.array.project_cooperation_table_widths, Color.BLACK, Color.BLACK, getResources()
                        .getColor(R.color.table_line), false, false, false);                
    }

    /*
     * 初始化库房Tab （施工方）
     */
    private void initStorehouseTab(View tab) {
        mStorehouseAdapter = new TableAdapter(this, tab,
                R.array.project_storehouse_table_names,
                R.array.project_storehouse_table_widths, Color.BLACK, Color.BLACK, getResources()
                        .getColor(R.color.table_line), false, false, false);
        AdapterView.OnItemClickListener itemClickListener = new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                mStorehouseAdapter.setSelectedPosition(position);
                if (!checkPermission(mCurrentProject, false)) {
                    return;
                }
                int[] location = new int[2];
                view.getLocationOnScreen(location);
                mStorehouseOptionsMenuView.showAtLocation(view, Gravity.NO_GRAVITY,
                        UtilTools.dp2pxW(view.getContext(), 360),
                        location[1] - UtilTools.dp2pxH(view.getContext(), 28));
            }
        };
        mStorehouseAdapter.setOnItemClickListener(itemClickListener);
        mStorehouseMenu = mStorehouseAdapter.getFloatingMenuView();
        mStorehouseMenu.addPopItem(getString(R.string.add), R.drawable.menu_icon_add);
        mStorehouseMenu.setPopOnItemClickListener(new AdapterView.OnItemClickListener() {

            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {

                if (!checkPermission(mCurrentProject, true)) {
                    mStorehouseMenu.dismiss();
                    return;
                }
                if (mCurrentProject == null) {
                    UtilTools.showToast(parent.getContext(),
                            R.string.select_project_toast);
                    mStorehouseMenu.dismiss();
                    return;
                }
                switch (position) {
                    case 0:
                        mStorehouseTitle.setText(R.string.project_storehouse_add);
                        mStorehouse.setText("");
                        mStorehouseDialog.show();
                        break;
                }
                mStorehouseMenu.dismiss();
            }

        });
        mStorehouseDialog = new Dialog(this, R.style.MyDialogStyle);
        mStorehouseDialog.setContentView(R.layout.project_storehouse_dialog);
        mStorehouseDialog.setCanceledOnTouchOutside(false);
        mStorehouseTitle = (TextView) mStorehouseDialog.findViewById(R.id.edit_title);
        mStorehouse = (EditText) mStorehouseDialog.findViewById(R.id.storehouse);

        mStorehouseOK = (Button) mStorehouseDialog.findViewById(R.id.ok);
        mStorehouseCancel = (ImageView) mStorehouseDialog.findViewById(R.id.btn_close);

        View.OnClickListener listener = new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                switch (v.getId()) {
                    case R.id.ok:
                        String new_storehouse = mStorehouse.getText().toString();
                        if (new_storehouse.equals("")) {
                            return;
                        }
                        String storehouse = mCurrentProject.getStorehouse();
                        ArrayList<String> storehouse_list = new ArrayList<String>();
                        if (storehouse != null && !storehouse.equals("")) {
                            storehouse_list.addAll(Arrays.asList(storehouse.split(",")));
                        }
                        if (mStorehouseTitle.getText().equals(
                                getString(R.string.project_storehouse_add))) {
                            if (storehouse_list.contains(new_storehouse)) {
                                UtilTools.showToast(v.getContext(),
                                        R.string.project_storehouse_add_toast);
                                return;
                            }
                            storehouse_list.add(mStorehouse.getText().toString());
                        } else {
                            int position = mStorehouseAdapter.getSelectedPosition();
                            if (storehouse_list.contains(new_storehouse)
                                    && storehouse_list.indexOf(new_storehouse) != position) {
                                UtilTools.showToast(v.getContext(),
                                        R.string.project_storehouse_add_toast);
                                return;
                            }
                            storehouse_list.set(position, new_storehouse);
                        }
                        StringBuilder builder = new StringBuilder();
                        for (String s : storehouse_list) {
                            builder.append(s + ",");
                        }
                        updateProject(mCurrentProject, builder.toString());
                        mStorehouseDialog.dismiss();
                        break;
                    case R.id.btn_close:
                        mStorehouseDialog.dismiss();
                        break;
                }

            }
        };
        mStorehouseOK.setOnClickListener(listener);
        mStorehouseCancel.setOnClickListener(listener);
    }

    /*
     * 刷新项目人员列表
     */
    private void refreshMemberList(Project project) {
        if (mMemberAsyncTask != null && mMemberAsyncTask.getStatus() != AsyncTask.Status.FINISHED)
            mMemberAsyncTask.cancel(true);
        mMemberAdapter.clear();
        mMemberList.clear();
        mMemberAdapter.refresh();
        project.setTab_flag(2);
        showLoadingProgress("Getting member of Project list...");
        mMemberAsyncTask = RemoteProjectService.getInstance().getProjectUser(new
                DataManagerInterface() {
                    @Override
                    public void getDataOnResult(ResultStatus status, List<?>
                            list) {
                        dismissLoadingProgress();
                        if (status.getCode() !=
                                AnalysisManager.SUCCESS_DB_QUERY && status.getMessage() != null &&
                                !status.getMessage().equals("")) {
                            UtilTools.showToast(ProjectActivity.this, status.getMessage());
                        }
                        if (status.getCode() == AnalysisManager.SUCCESS_DB_QUERY && list !=
                                null && list.size() > 0) {
                            String[] headNames =
                                    mMemberAdapter.getHeadNames();
                            int[] arrHeadWidths =
                                    mMemberAdapter.getArrHeadWidths();
                            for (Object object : list) {
                                if (object instanceof User) {
                                    final User user = (User) object;
                                    List<TextCell> rowValues = new ArrayList<TextCell>();
                                    String user_role = user.getProle();
                                    String role_text = "";
                                    if (user_role != null && !user_role.equals("")) {
                                        String[] role = user.getProle().split(",");
                                        StringBuilder builder = new StringBuilder();
                                        for (String item : role) {
                                            builder.append(getRoleNameByCode(item)
                                                    + ",");
                                        }
                                        role_text = builder.toString();
                                        if (role_text.endsWith(","))
                                            role_text = role_text.substring(0,
                                                    role_text.length() - 1);
                                    }

                                    OBS obs = findOBSById(user.getObs_id());
                                    String[] values = new String[] {
                                            user.getName(), role_text,
                                            obs != null ? obs.getName() : "",
                                            user.getLogin_name()
                                    };
                                    for (int j = 0; j < arrHeadWidths.length; j++) {
                                        TextCell itemCell;
                                        if (j == 3) {
                                        	itemCell = new ButtonItemCell(values[j], headNames[j],
                                                    arrHeadWidths[j], R.drawable.mobile);
                                            itemCell.setOnClickListener(new View.OnClickListener() {
                                                    @Override
                                                     public void onClick(View v) {
                                                        Intent intent = new Intent(Intent.ACTION_DIAL);
                                                        intent.setData(Uri.parse("tel:" + user.getLogin_name()));
                                                        if (intent.resolveActivity(getPackageManager()) != null) {
                                                            startActivity(intent);
                                                        }
                                                     }
                                             });
                                        } else {
                                        	itemCell = new TextCell(values[j], headNames[j],
                                                    arrHeadWidths[j]);
                                        }
                                        rowValues.add(itemCell);
                                    }
                                    mMemberAdapter.addItem(user.getUser_id(), rowValues);
                                    mMemberList.add(user);
                                }
                            }
                            mMemberAdapter.refresh();
                        } else if (status.getCode() == AnalysisManager.EXCEPTION_RECV_NULL) {
                            mMemberAdapter.clear();
                            mMemberList.clear();
                            mMemberAdapter.refresh();
                        }
                    }
                }, project);
    }
    
    /*
     * 刷新写作单位列表
     */
    private void refreshCooperationList(Project project) {
        if (mCooperationAsyncTask != null && mCooperationAsyncTask.getStatus() != AsyncTask.Status.FINISHED)
            mCooperationAsyncTask.cancel(true);
        mCooperationAdapter.clear();
        mCooperationList.clear();
        mCooperationAdapter.refresh();
        
        showLoadingProgress("Getting cooperation of Project list...");
        
        Tenant tenant = new Tenant();
        tenant.setTenant_id(project.getTenant_id());
        mMemberAsyncTask = RemoteCommonService.getInstance().getCooperationTenantListByProject(new
                DataManagerInterface() {

                    @Override
                    public void getDataOnResult(ResultStatus status,
                            List<?> list) {
                        dismissLoadingProgress();
                        if (status.getCode() !=
                                AnalysisManager.SUCCESS_DB_QUERY && status.getMessage() != null &&
                                !status.getMessage().equals("")) {
                            UtilTools.showToast(ProjectActivity.this, status.getMessage());
                        }
                        if (status.getCode() == AnalysisManager.SUCCESS_DB_QUERY && list !=
                                null && list.size() > 0) {
                           
                            String[] headNames =
                                    mCooperationAdapter.getHeadNames();
                            int[] arrHeadWidths =
                                    mCooperationAdapter.getArrHeadWidths();
                            
                            for (Object object : list) {
                                final Tenant tenant = (Tenant) object;
                                List<TextCell> rowValues = new ArrayList<TextCell>();
                                
                                String[] values = new String[] {
                                        GLOBAL.ENTERPRISE_TYPE[tenant.getType() - 1][1],
                                        tenant.getName(),
                                        tenant.getKey_person(),
                                        tenant.getTel(), "",
                                        tenant.getAddress() };
                                for (int j = 0; j < arrHeadWidths.length; j++) {
                                    TextCell itemCell = new TextCell(values[j], headNames[j],
                                            arrHeadWidths[j]);
                                    if (j == 4) { // 干系人
                                        itemCell.setBackground(R.drawable.icon_stakeholder);
                                        itemCell.setOnClickListener(new View.OnClickListener() {
                                            
                                            @Override
                                            public void onClick(View v) {
                                                Intent intent = new Intent();
                                                Bundle bundle = new Bundle();
                                                intent.setClass(ProjectActivity.this,
                                                        StakeholderActivity.class);
                                                Cooperation currentItem = new Cooperation();
                                                currentItem.setCooperation_id(tenant.getCooperation_id());
                                                currentItem.setAccept_company(UserCache.getCurrentUser().getTenant_id());
                                                bundle.putSerializable("bean", currentItem);
                                                bundle.putInt("type", 1);   // 联系单位
                                                bundle.putInt("action", 1); // 接收 1
                                                intent.putExtras(bundle);
                                                startActivity(intent);
                                            }
                                        });
                                    }
                                    rowValues.add(itemCell);
                                }
                                mCooperationAdapter.addItem(tenant.getTenant_id(), rowValues);
                                mCooperationList.add(tenant);
                            }
                            mCooperationAdapter.refresh();
                        }
                    }            
        }, tenant, project.getProject_id());
    }

    /*
     * 刷新库房列表
     */
    private void refreshStorehouseList(Project project) {
        String project_storehouse = project.getStorehouse();
        mStorehouseAdapter.clear();
        if (project_storehouse == null || project_storehouse.equals("")) {
            mStorehouseAdapter.refresh();
            return;
        }
        String[] storehouses = project.getStorehouse().split(",");
        String[] headNames = mStorehouseAdapter.getHeadNames();
        int[] arrHeadWidths = mStorehouseAdapter.getArrHeadWidths();
        for (int i = 0; i < storehouses.length; i++) {
            List<TextCell> rowValues = new ArrayList<TextCell>();
            String[] values = new String[] {
                    (i + 1) + "", storehouses[i]
            };
            for (int j = 0; j < arrHeadWidths.length; j++) {
                TextCell itemCell = new TextCell(values[j], headNames[j],
                        arrHeadWidths[j]);
                if (j == 0)
                    itemCell.setGravity(Gravity.CENTER);
                rowValues.add(itemCell);
            }
            mStorehouseAdapter.addItem(i, rowValues);
        }
        mStorehouseAdapter.refresh();
    }
    
//    private View createIndicatorView(TabHost tabHost, String title, int index) {
//        LayoutInflater inflater = (LayoutInflater) tabHost.getContext()
//                .getSystemService(Context.LAYOUT_INFLATER_SERVICE);
//        View tabIndicator = inflater.inflate(R.layout.tab_indicator_layout,
//                tabHost.getTabWidget(), false);
//        ImageView bgView = (ImageView) tabIndicator
//                .findViewById(R.id.bg);
//        if (index == mTabFgViews.length - 1) {
//            bgView.setVisibility(View.GONE);
//        } else {
//            bgView.setImageResource(R.drawable.tab_indicator_bg);
//        }
//        mTabFgViews[index] = (ImageView) tabIndicator
//                .findViewById(R.id.fg);
//        mTabFgViews[index].setImageResource(R.drawable.tab_indicator_fg);
//        mTabFgViews[index].setImageLevel(index == 0 ? 0 : index == 1 ? 1 : 2);
//        TextView titleView = (TextView) tabIndicator
//                .findViewById(R.id.title);
//        titleView.setText(title);
//        return tabIndicator;
//    }
    
    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (resultCode == Activity.RESULT_OK) {
            switch (requestCode) {
                case LWDW_DISTRIBUTION_REQUEST:
                    StringBuilder builder = new StringBuilder(
                            mCurrentProject.getConstruction_unit());
                    if (!builder.toString().equals(""))
                        builder.append(",");
                    @SuppressWarnings("unchecked")
                    ArrayList<P_LWDW> list = (ArrayList<P_LWDW>) data
                            .getSerializableExtra(ListSelectActivity.RESULT_KEY);
                    for (P_LWDW lwdw : list) {
                        builder.append("(" + lwdw.getLwdw_id() + ")" + ",");
                    }
                    updateProject(mCurrentProject, builder.toString());
                case MEMBER_DISTRIBUTION_REQUEST:
                    refreshMemberList(mCurrentProject);
                    break;
                case OWNER_DISTRIBUTION_REQUEST:
                    User user = (User) data.getSerializableExtra("user");
                    if (user != null) {
                        mCommonOwner.getEditText().setText(user.getName());
                        mCurrentProject.setOwner(user.getUser_id());
                    }
                    break;
            }
        }
    }
    
    private void setFloatingMenuStatus(Project project) {
        boolean permission = checkPermission(project, false);
        mMemberMenu.setVisibility(permission ? View.VISIBLE : View.GONE);
        mCommmonUpdate.setVisibility(permission ? View.VISIBLE : View.GONE);
//        if (mEnterpriseType == 2) {            
            mStorehouseMenu.setVisibility(permission ? View.VISIBLE : View.GONE);
//        }
    }
    
    private void initStorehouseOptionsMenuView() {
        String[] subMenuNames = new String[] {
                getString(R.string.modify), getString(R.string.delete)
        };
        mStorehouseOptionsMenuView = new OptionsMenuView(this, subMenuNames);
        mStorehouseOptionsMenuView
                .setSubMenuListener(new OptionsMenuView.SubMenuListener() {
                    @Override
                    public void onSubMenuClick(View view) {
                        switch ((Integer) view.getTag()) {

                            case 0:
                                mStorehouseTitle.setText(R.string.project_storehouse_modify);
                                mStorehouse.setText(mStorehouseAdapter.getSelectItem()
                                        .getValueAt(1)
                                        .getCellValue());
                                mStorehouseDialog.show();
                                break;
                            case 1:
                                UtilTools.deleteConfirm(ProjectActivity.this, 
                                        new UtilTools.DeleteConfirmInterface() {
                                    @Override
                                    public void deleteConfirmCallback() {
                                        String storehouse = mCurrentProject.getStorehouse();
                                        ArrayList<String> storehouse_list = new ArrayList<String>();
                                        if (!storehouse.equals("")) {
                                            storehouse_list.addAll(Arrays.asList(storehouse.split(",")));
                                        }
                                        storehouse_list.remove(mStorehouseAdapter.getSelectedPosition());
                                        StringBuilder builder = new StringBuilder();
                                        for (String s : storehouse_list) {
                                            builder.append(s + ",");
                                        }
                                        updateProject(mCurrentProject, builder.toString());
                                    }
                                });
                                break;
                        }
                        mStorehouseOptionsMenuView.dismiss();
                    }
                });
    }
}
