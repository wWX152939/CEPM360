
package com.pm360.cepm360.app.common.activity;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Intent;
import android.graphics.Color;
import android.net.Uri;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import com.pm360.cepm360.R;
import com.pm360.cepm360.app.cache.ObsCache;
import com.pm360.cepm360.app.cache.RoleCache;
import com.pm360.cepm360.app.cache.TenantCache;
import com.pm360.cepm360.app.cache.UserCache;
import com.pm360.cepm360.app.module.project.table.ButtonItemCell;
import com.pm360.cepm360.app.module.project.table.TableAdapter;
import com.pm360.cepm360.app.module.project.table.TableRow;
import com.pm360.cepm360.app.module.project.table.TextCell;
import com.pm360.cepm360.app.utils.UtilTools;
import com.pm360.cepm360.common.GLOBAL;
import com.pm360.cepm360.common.ResultStatus;
import com.pm360.cepm360.common.json.AnalysisManager;
import com.pm360.cepm360.entity.OBS;
import com.pm360.cepm360.entity.Project;
import com.pm360.cepm360.entity.Role;
import com.pm360.cepm360.entity.User;
import com.pm360.cepm360.services.system.RemoteOBSService;
import com.pm360.cepm360.services.system.RemoteRoleService;
import com.pm360.cepm360.services.system.RemoteUserService;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

/**
 * 
 * 用于用户选择（单选或多选） 
 * a.需要传入对话框标题
 * b.是否多选multi_select
 * c.某一个租户的cooperation_id
 * d.默认选中的列表user_list
 *
 */
public class OwnerSelectActivity extends Activity {

    private TableAdapter mAdapter;
    private String[] headNames;
    private int[] arrHeadWidths;
    private Intent mIntent;
    private String mTitle;
    private Project mProject;
    private boolean mMultiselect;
    private int mCooperationId;
    private ProgressDialog mProgressDialog;
    private ArrayList<Role> mRoleList = new ArrayList<Role>();
    private ArrayList<OBS> mOBSList = new ArrayList<OBS>();
    private ArrayList<User> mUserList = new ArrayList<User>();
    private ArrayList<String> mPreUserList = new ArrayList<String>();
    

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.project_radio_dialog);
        
        mIntent = getIntent();
        mTitle = mIntent.getStringExtra("title");        
        mProject = (Project) mIntent.getSerializableExtra("project");
        mMultiselect = mIntent.getBooleanExtra("multiselect", false);
        mCooperationId = mIntent.getIntExtra("cooperation_id", 0);
        String user_list = (String) mIntent.getStringExtra("user_list");

        if (user_list != null && !user_list.equals("")) {
            mPreUserList.addAll(Arrays.asList(user_list.split(",")));
        }
        View.OnClickListener onClickListener = new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                switch (v.getId()) {
                    case R.id.ok:
                        if (mMultiselect) {
                            ArrayList<User> userList = new ArrayList<User>();
                            ArrayList<TableRow> selectList = mAdapter.getMultiSelectList();
                            for (TableRow row : selectList) {
                                User user = findUserById(row.getRowId());
                                userList.add(user);
                            }
                            mIntent.putExtra("user_list", userList);
                        } else {
                            int user_id = (int) mAdapter.getSelectItemId();
                            User user = findUserById(user_id);
                            mIntent.putExtra("user", user);
                        }
                        setResult(RESULT_OK, mIntent);
                        break;
                    case R.id.clear:
                        setResult(RESULT_OK, mIntent);
                        break;
                    case R.id.close:
                        setResult(RESULT_CANCELED, mIntent);
                    case R.id.btn_close:
                        setResult(RESULT_CANCELED, mIntent);
                        break;
                }
                finish();
            }
        };
        TextView title = (TextView) findViewById(R.id.title);
        title.setText(mTitle);
        Button ok = (Button) findViewById(R.id.ok);
        Button clear = (Button) findViewById(R.id.clear);
        Button close = (Button) findViewById(R.id.close);
        ImageView btn_close = (ImageView) findViewById(R.id.btn_close);
        ok.setOnClickListener(onClickListener);
        clear.setOnClickListener(onClickListener);
        close.setOnClickListener(onClickListener);
        btn_close.setOnClickListener(onClickListener);
        if (mCooperationId > 0) {
            mAdapter = new TableAdapter(this, findViewById(R.id.list),
                    R.array.owner_select_table_names,
                    R.array.owner_select_table_widths, Color.BLACK, Color.BLACK, getResources()
                            .getColor(R.color.table_line), false, false,
                    mMultiselect);
        } else {
            mAdapter = new TableAdapter(this, findViewById(R.id.list),
                    R.array.contract_owner_table_names,
                    R.array.contract_owner_table_widths, Color.BLACK, Color.BLACK, getResources()
                            .getColor(R.color.table_line), false, false,
                    mMultiselect);
        }
        headNames = mAdapter.getHeadNames();
        arrHeadWidths = mAdapter.getArrHeadWidths();
        Role role = new Role();
        role.setTenant_id(UserCache.getTenantId());
        
        if (mCooperationId > 0) {
            // 获取基于某一个协作的干系人
            loadUserByCooperationId(mCooperationId);
        } else {
            // 获取当前公司的所有用户，或基于某个项目的用户
            loadRoleList(role);
        }
//        if (mMultiselect) {
            clear.setVisibility(View.GONE);
//        }
    }

    /**
     * 获取当前公司的Role 列表
     * @param role
     */
    private void loadRoleList(Role role) {
        if (RoleCache.isDataLoaded()) {
            mRoleList.clear();
            for (Role r : RoleCache.getRoleLists()) {
                mRoleList.add(r);
            }
            loadOBSList();
        } else {
            showProgressDialog("Getting role list...");
            RemoteRoleService.getInstance().getRoleList(new DataManagerInterface() {
    
                @Override
                public void getDataOnResult(ResultStatus status, List<?> list) {
                    dismissProgressDialog();
                    if (status.getCode() != AnalysisManager.SUCCESS_DB_QUERY
                            && status.getMessage() != null
                            && !status.getMessage().equals("")) {
                        UtilTools.showToast(OwnerSelectActivity.this, status.getMessage());
                    }
                    if ((status.getCode() == AnalysisManager.SUCCESS_DB_QUERY)
                            && list != null
                            && list.size() > 0) {
                        for (Object object : list) {
                            if (object instanceof Role) {
                                mRoleList.add((Role) object);
                            }
                        }
                        loadOBSList();
                    }
                }
            }, role);
        }
    }

    /**
     * 获取当前公司的OBS 列表
     */
    private void loadOBSList() {
        dismissProgressDialog();
        if (ObsCache.isDataLoaded()) {
            mOBSList.clear();
            for (OBS obs : ObsCache.getObsLists()) {
                mOBSList.add(obs);
            }
            loadUserList();
        } else {
            showProgressDialog("loading OBSList...");
            RemoteOBSService.getInstance().getOBSList(new DataManagerInterface() {
    
                @Override
                public void getDataOnResult(ResultStatus status, List<?> list) {
                    dismissProgressDialog();
                    if (status.getCode() != AnalysisManager.SUCCESS_DB_QUERY
                            && status.getMessage() != null
                            && !status.getMessage().equals("")) {
                        UtilTools.showToast(OwnerSelectActivity.this, status.getMessage());
                    }
                    if (status.getCode() == AnalysisManager.SUCCESS_DB_QUERY
                            && list != null
                            && list.size() > 0) {
                        for (Object object : list) {
                            if (object instanceof OBS) {
                                mOBSList.add((OBS) object);
                            }
                        }
                    }
                    loadUserList();
                }
            }, UserCache.getCurrentUser());
        }

    }

    /**
     * 获取当前公司的用户，或基于某一项目的用户
     */
    private void loadUserList() {
        dismissProgressDialog();
        DataManagerInterface userManager = new DataManagerInterface() {

            @Override
            public void getDataOnResult(ResultStatus status, List<?> list) {
                dismissProgressDialog();
                if (status.getCode() != AnalysisManager.SUCCESS_DB_QUERY
                        && status.getMessage() != null
                        && !status.getMessage().equals("")) {
                    UtilTools.showToast(OwnerSelectActivity.this, status.getMessage());
                }
                if (status.getCode() == AnalysisManager.SUCCESS_DB_QUERY
                        && list != null && list.size() > 0) {
                    for (Object object : list) {
                        User user = (User) object;
                        // 去掉管理员
                        if (!user.getRole().equals(GLOBAL.ROLE_TYPE[12][0])) {
                            addUser(user);
                        }
                    }
                    mAdapter.refresh();
                }
            }
        };
        
        if (mProject != null) {
            // 基于某一个项目获取项目的用户
            showProgressDialog("loading UserList...");
            RemoteUserService.getInstance().getProjectUsers(userManager, mProject);
        } else {
            // 获取当前公司的用户
            if (UserCache.isDataLoaded()) {
                for (User user : UserCache.getUserLists()) {
                    if (!user.getRole().equals(GLOBAL.ROLE_TYPE[12][0])) {
                        addUser(user);
                    }
                }
                mAdapter.refresh();
            } else {
                showProgressDialog("loading UserList...");
                RemoteUserService.getInstance().getTenantUsers(userManager,
                        UserCache.getCurrentUser());
            }
        }
    }
    
    /**
     * 根据cooperationId获取相应的干系人
     * 
     * @param cooperationId
     */
    private void loadUserByCooperationId(int cooperationId) {
        showProgressDialog("loading UserList...");
        RemoteUserService.getInstance().getCooperationUsers(new DataManagerInterface() {

            @Override
            public void getDataOnResult(ResultStatus status, List<?> list) {
                dismissProgressDialog();
                if (status.getCode() == AnalysisManager.SUCCESS_DB_QUERY
                        && list != null && list.size() > 0) {
                    for (Object object : list) {
                        addUser((User) object);
                    }
                    mAdapter.refresh();
                }
            }            
        }, cooperationId, UserCache.getTenantId());
    }

    /**
     * 添加到界面显示
     * 
     * @param user
     */
    private void addUser(final User user) {
        mUserList.add(user);
        List<TextCell> rowValues = new ArrayList<TextCell>();
        String user_role = user.getRole();
        String role_text = "";
        if (user_role != null && !user_role.equals("")) {
            String[] role = user_role.split(",");
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
        String[] values;
        if (mCooperationId > 0) {
            values = new String[] {
                    user.getName(),
                    //obs != null ? obs.getName() : "",
                    user.getObs_name(),
                    TenantCache.getTenantIdMaps().get(String.valueOf(user.getTenant_id())),        
                    user.getLogin_name()
            };
        } else {
            values = new String[] {
                    user.getName(), role_text,
                    obs != null ? obs.getName() : "",
                    user.getLogin_name()
            };
        }
        for (int j = 0; j < arrHeadWidths.length; j++) {
        	TextCell itemCell;
            if (j == 3) {
                itemCell = new ButtonItemCell(values[j], headNames[j], arrHeadWidths[j], R.drawable.mobile);
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
            	itemCell = new TextCell(values[j], headNames[j], arrHeadWidths[j]);
            }
            rowValues.add(itemCell);
        }
        mAdapter.addItem(user.getUser_id(), rowValues, false,
                mPreUserList.contains(user.getUser_id() + ""));
    }

    private User findUserById(int user_id) {
        User res = null;
        for (User user : mUserList) {
            if (user.getUser_id() == user_id) {
                res = user;
                break;
            }
        }
        return res;
    }

    private String getRoleNameByCode(String code) {
        String res = "";
        for (Role role : mRoleList) {
            if (code.endsWith(role.getCode())) {
                res = role.getName();
                break;
            }
        }
        return res;
    }

    private OBS findOBSById(int obs_id) {
        OBS res = null;
        for (OBS obs : mOBSList) {
            if (obs.getObs_id() == obs_id) {
                res = obs;
                break;
            }
        }
        return res;
    }

    private void showProgressDialog(String text) {
        dismissProgressDialog();
        mProgressDialog = UtilTools.showProgressDialog(OwnerSelectActivity.this, true, false);
    }

    private void dismissProgressDialog() {
        if (mProgressDialog != null && mProgressDialog.isShowing())
            mProgressDialog.dismiss();
    }

}
