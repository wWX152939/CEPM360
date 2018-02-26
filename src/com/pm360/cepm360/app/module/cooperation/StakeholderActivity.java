package com.pm360.cepm360.app.module.cooperation;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Intent;
import android.graphics.Color;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.view.Gravity;
import android.view.View;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.pm360.cepm360.R;
import com.pm360.cepm360.app.cache.ObsCache;
import com.pm360.cepm360.app.cache.PermissionCache;
import com.pm360.cepm360.app.cache.RoleCache;
import com.pm360.cepm360.app.cache.TenantCache;
import com.pm360.cepm360.app.cache.UserCache;
import com.pm360.cepm360.app.common.activity.DataManagerInterface;
import com.pm360.cepm360.app.common.activity.ListSelectActivity;
import com.pm360.cepm360.app.common.view.OptionsMenuView;
import com.pm360.cepm360.app.module.project.table.ButtonItemCell;
import com.pm360.cepm360.app.module.project.table.TableAdapter;
import com.pm360.cepm360.app.module.project.table.TableRow;
import com.pm360.cepm360.app.module.project.table.TextCell;
import com.pm360.cepm360.app.module.settings.UserSelectFragment;
import com.pm360.cepm360.app.utils.UtilTools;
import com.pm360.cepm360.common.ResultStatus;
import com.pm360.cepm360.common.json.AnalysisManager;
import com.pm360.cepm360.entity.Cooperation;
import com.pm360.cepm360.entity.OBS;
import com.pm360.cepm360.entity.Role;
import com.pm360.cepm360.entity.User;
import com.pm360.cepm360.services.cooperation.RemoteCooperationService;
import com.pm360.cepm360.services.system.RemoteOBSService;
import com.pm360.cepm360.services.system.RemoteRoleService;
import com.pm360.cepm360.services.system.RemoteUserService;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;


public class StakeholderActivity extends Activity {
    private Button mLeftButton;
    private Button mMiddleButton;
    private Button mRightButton;
    private TableAdapter mAdapter;
    private String[] headNames;
    private int[] arrHeadWidths;
    private Intent mIntent;
    private String mTitle;
//    private Project mProject;
    private OptionsMenuView mOptionsMenuView;
    private boolean mMultiselect;
    private ProgressDialog mProgressDialog;
    private ArrayList<Role> mRoleList = new ArrayList<Role>();
    private ArrayList<OBS> mOBSList = new ArrayList<OBS>();
    private ArrayList<User> mUserList = new ArrayList<User>();
    
	private static final String COOPERATION_EDIT = "17_1";
	private static final String COOPERATION_VIEW = "17_2";    
    private int mPosition;
    private int mDelete;
    private int mType;
    private int mAction;
    private Cooperation mCurrentItem;
    
    private static final int IMPORT_USER_CODE = 100;
    private static final int COOPERATION_UPDATE_SUCCESS = 200;
    private static final int COOPERATION_DELETE_SUCCESS = 300;
    
    @SuppressLint("HandlerLeak") 
    private Handler mHandler = new Handler() {
        public void handleMessage(Message msg) {
            Toast.makeText(StakeholderActivity.this,
                    (CharSequence) msg.obj, Toast.LENGTH_SHORT).show();
            switch (msg.what) {
            case COOPERATION_UPDATE_SUCCESS:
                // 更新成功
            	mIntent.putExtra("bean", mCurrentItem);
                setResult(RESULT_OK, mIntent);
                break;
            case COOPERATION_DELETE_SUCCESS:
                // 删除成功
            	mIntent.putExtra("bean", mCurrentItem);
                setResult(RESULT_OK, mIntent);
                mAdapter.removeSelectItem(mPosition);
                mAdapter.refresh();
                break;    
            default:
                break;
            }
        }
    };

    
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.cooperation_stakeholder_layout);
        mIntent = getIntent();
        
        mType = mIntent.getIntExtra("type", 0);  // 0:本单位干系人页面   / 1:联系单位干系人页面
        mAction = mIntent.getIntExtra("action", 0); // 0:协作发起  / 1:协作接收 
//        mProject = (Project) mIntent.getSerializableExtra("project");
        mCurrentItem = (Cooperation) mIntent.getSerializableExtra("bean");
               
        if (mType == 0) { // 本单位干系人页面
            mMultiselect = false;
        } else {  // 联系单位干系人页面
            mMultiselect = false;
        }
                

        
        View.OnClickListener onClickListener = new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                switch (v.getId()) {
                    case R.id.btn_import: // 导入
                        importUser();
                        
                        break;
                    case R.id.btn_clear: // 删除
                        ArrayList<TableRow> selectList = mAdapter.getMultiSelectList();
                        for (TableRow row : selectList) {
                            mAdapter.removeItemById(row.getRowId());
                            mAdapter.refresh();
                            mUserList.remove(findUserById(row.getRowId()));
                        }
                        break;
                    case R.id.btn_save:   // 保存
                        break;
                    case R.id.btn_close: // 关闭
                        setResult(RESULT_OK, mIntent);
                        finish();
                        break;
                }
            }
        };
        if (mType == 0) { // 本单位
            mTitle = getString(R.string.lunch_contact_window);
        } else { // 联系单位
            mTitle = getString(R.string.accept_contact_window);
        }
        TextView title = (TextView) findViewById(R.id.title);
        title.setText(mTitle);
        ImageView btn_close = (ImageView) findViewById(R.id.btn_close);
        btn_close.setOnClickListener(onClickListener);
        
        mLeftButton = (Button) findViewById(R.id.btn_import);
        mMiddleButton = (Button) findViewById(R.id.btn_clear);
        mRightButton = (Button) findViewById(R.id.btn_save);
        mLeftButton.setText(getString(R.string.import_data));
        mMiddleButton.setText(getString(R.string.delete));
        

        mMiddleButton.setVisibility(View.GONE);
        mRightButton.setText(getString(R.string.save));
        mRightButton.setVisibility(View.GONE);
        
        mLeftButton.setOnClickListener(onClickListener);
        mMiddleButton.setOnClickListener(onClickListener);
        mRightButton.setOnClickListener(onClickListener);
        
        
        initOptionsMenuView();

		
        if (mType == 1) {
            mAdapter = new TableAdapter(this, findViewById(R.id.list),
                    R.array.owner_select_table_names,
                    R.array.owner_select_table_widths, Color.BLACK, Color.BLACK, getResources()
                            .getColor(R.color.table_line), false, false,
                    mMultiselect);
        } else {
            mAdapter = new TableAdapter(this, findViewById(R.id.list),
                    R.array.contract_owner_table_names,
                    R.array.stakeholder_table_widths, Color.BLACK, Color.BLACK, getResources()
                            .getColor(R.color.table_line), false, false,
                    mMultiselect);
            AdapterView.OnItemClickListener itemClickListener = new AdapterView.OnItemClickListener() {
                @Override
                public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                	mAdapter.setSelectedPosition(position);
                    int[] location = new int[2];
                    view.getLocationOnScreen(location);
                    
            		if (PermissionCache.hasSysPermission(COOPERATION_EDIT)) {
            			mOptionsMenuView.showAtLocation(view, Gravity.NO_GRAVITY,
                                UtilTools.dp2pxW(view.getContext(), 240),
                                location[1] - UtilTools.dp2pxH(view.getContext(), 110));
            		}
                }
            };
            mAdapter.setOnItemClickListener(itemClickListener);
        }
        
        headNames = mAdapter.getHeadNames();
        arrHeadWidths = mAdapter.getArrHeadWidths();
        
        Role role = new Role();
        role.setTenant_id(UserCache.getCurrentUser().getTenant_id());        
        loadRoleList(role);
        
        if (mType == 0) { //本单位
            String[] userIds;
            if (mAction == 0) {
            	String lcw = mCurrentItem.getLaunch_contact_window();
                if (lcw != null && !lcw.equals("")){                	
                    userIds = mCurrentItem.getLaunch_contact_window().split(",");
                    formatUser(userIds);                
                }
            } else {
            	String acw = mCurrentItem.getAccept_contact_window();
                if (acw != null && !acw.equals("")){
                    userIds = mCurrentItem.getAccept_contact_window().split(",");
                    formatUser(userIds);
                }
            }
            for (User user : mUserList) {
                addUser(user);
            }
        } else { // 联系单位
            loadCooperationUser();
            mLeftButton.setVisibility(View.GONE);
        }
        
		if (!PermissionCache.hasSysPermission(COOPERATION_EDIT)
				&& PermissionCache.hasSysPermission(COOPERATION_VIEW)) {			
			mLeftButton.setVisibility(View.GONE);
		}        

    }
    
    private void initOptionsMenuView() {
		String[] subMenuNames = getResources().getStringArray(
				R.array.invitebid_mavin_optionmenu_name);
		
		mOptionsMenuView = new OptionsMenuView(this, subMenuNames);
		mOptionsMenuView
				.setSubMenuListener(new OptionsMenuView.SubMenuListener() {
					@Override
					public void onSubMenuClick(View view) {
						switch ((Integer) view.getTag()) {
						case 0:
							mDelete = 1; //删除人员
							mPosition = mAdapter.getSelectedPosition();
					    	mUserList.remove(mPosition);
	                        
	                        updateCooperation(mUserList);
							break;
						default:
							break;
						}
						mOptionsMenuView.dismiss();
					}
				});
	}

	private void loadCooperationUser() {
        int tenantId;
        if (mAction == 0) { // 发起
            tenantId = mCurrentItem.getLaunch_company();
        } else { // 接收
            tenantId = mCurrentItem.getAccept_company();
        }

        showProgressDialog("Getting role list...");
        RemoteUserService.getInstance().getCooperationUsers(new DataManagerInterface() {

            @SuppressWarnings("unchecked")
            @Override
            
            public void getDataOnResult(ResultStatus status, List<?> list) {
                dismissProgressDialog();
                if (status.getCode() != AnalysisManager.SUCCESS_DB_QUERY
                        && status.getMessage() != null
                        && !status.getMessage().equals("")) {
                    UtilTools.showToast(StakeholderActivity.this,
                            status.getMessage());
                }
                if (status.getCode() == AnalysisManager.SUCCESS_DB_QUERY && 
                        list != null && list.size() > 0) {
                    addContactUser((List<User>) list);                    
                }
            }
            
        }, mCurrentItem.getCooperation_id(), tenantId);
    }
    
    // 过滤联系单位干系人
    private void addContactUser(List<User> allTenantUser) {
        for (int i = 0; i < allTenantUser.size(); i++) {
            addUser(allTenantUser.get(i));
            mUserList.add(allTenantUser.get(i));
        }        
        mAdapter.refresh();
    }
    
    private void formatUser(String[] userIds) {    	
        for (int i = 0; i < userIds.length; i++) {
            mUserList.add(UserCache.findUserById(Integer.parseInt(userIds[i])));
        }
    }
    
    private void importUser() {
        ArrayList<User> userList = new ArrayList<User>();
        ArrayList<TableRow> selectList = mAdapter.getList();
        for (TableRow row : selectList) {
            User user = findUserById(row.getRowId());
            userList.add(user);
        }
                
        Intent intent = new Intent(this, ListSelectActivity.class);
        Bundle bundle = new Bundle();
        bundle.putSerializable(ListSelectActivity.FRAGMENT_KEY, UserSelectFragment.class);
        bundle.putBoolean(ListSelectActivity.SELECT_MODE_KEY, true);
        intent.putExtra(ListSelectActivity.SELECT_MODE_KEY, ListSelectActivity.MULTI_SELECT);
        bundle.putSerializable(ListSelectActivity.FILTER_DATA_KEY, (Serializable) userList);
        intent.putExtras(bundle);
        
        startActivityForResult(intent, IMPORT_USER_CODE);
    }

    private void updateCooperation(ArrayList<User> userList) {
        StringBuffer userIdStr = new StringBuffer();
        for (int i = 0; i < userList.size(); i++){
            userIdStr.append(userList.get(i).getUser_id() + "");
            if (i != userList.size() - 1){
            	userIdStr.append(",");
            }                   
        }
        if (mAction == 0) { // 协作发起
            mCurrentItem.setLunch_contact_window(userIdStr.toString());
        } else { // 协作接收            
            mCurrentItem.setAccept_contact_window(userIdStr.toString());
        }
        showProgressDialog("Getting Cooperation...");
        RemoteCooperationService.getInstance().updateCooperation(new DataManagerInterface() {

            @Override
            public void getDataOnResult(ResultStatus status, List<?> list) {
            	dismissProgressDialog();
                if (status.getCode() == AnalysisManager.SUCCESS_DB_UPDATE && mDelete == 0 ) {
                    Message msg = new Message();
                    msg.what = COOPERATION_UPDATE_SUCCESS;
                    msg.obj = status.getMessage();
                    mHandler.sendMessage(msg);
                }
                
                if (status.getCode() == AnalysisManager.SUCCESS_DB_UPDATE && mDelete == 1 ) {
                    Message msg = new Message();
                    msg.what = COOPERATION_DELETE_SUCCESS;
                    msg.obj = status.getMessage();
                    mHandler.sendMessage(msg);
                }
                
                mDelete = 0; //默认为增加人员
            }
            
        }, mCurrentItem, mAction == 0 ? "1" : "2");
    }
    
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
                        UtilTools.showToast(StakeholderActivity.this, status.getMessage());
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

    private void loadOBSList() {
        dismissProgressDialog();
        if (ObsCache.isDataLoaded()) {
            mOBSList.clear();
            for (OBS obs : ObsCache.getObsLists()) {
                mOBSList.add(obs);
            }
            //loadUserList(mProject);
        } else {
            showProgressDialog("loading OBSList...");
            RemoteOBSService.getInstance().getOBSList(new DataManagerInterface() {
    
                @Override
                public void getDataOnResult(ResultStatus status, List<?> list) {
                    dismissProgressDialog();
                    if (status.getCode() != AnalysisManager.SUCCESS_DB_QUERY
                            && status.getMessage() != null
                            && !status.getMessage().equals("")) {
                        UtilTools.showToast(StakeholderActivity.this, status.getMessage());
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
                    //loadUserList(mProject);
                }
            }, UserCache.getCurrentUser());
        }

    }
/**
    private void loadUserList(Project project) {
        dismissProgressDialog();
        DataManagerInterface userManager = new DataManagerInterface() {

            @Override
            public void getDataOnResult(ResultStatus status, List<?> list) {
                dismissProgressDialog();
                if (status.getCode() != AnalysisManager.SUCCESS_DB_QUERY
                        && status.getMessage() != null
                        && !status.getMessage().equals("")) {
                    UtilTools.showToast(StakeholderActivity.this, status.getMessage());
                }
                if (status.getCode() == AnalysisManager.SUCCESS_DB_QUERY
                        && list != null && list.size() > 0) {
                    for (Object object : list) {
                        if (object instanceof User) {
                            addUser((User) object);
                        }
                    }
                    mAdapter.refresh();
                }
            }
        };
        
        if (project != null) {
            showProgressDialog("loading UserList...");
            RemoteUserService.getInstance().getProjectUsers(userManager, project);
        } else {
            if (UserCache.isDataLoaded()) {
                for (User user : UserCache.getUserLists()) {
                    addUser(user);
                }
                mAdapter.refresh();
            } else {
                showProgressDialog("loading UserList...");
                RemoteUserService.getInstance().getUserList(userManager,
                        UserCache.getCurrentUser());
            }
        }
    }
**/
    private void addUser(final User user) {
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
        if (mType == 0) {
            values = new String[] {
            		user.getName(), 
            		role_text,
                    obs != null ? obs.getName() : "",
                    user.getLogin_name()
            };
        } else {
            values = new String[] {
                    
                    user.getName(),
                    //obs != null ? obs.getName() : "",
                    user.getObs_name(),
                    TenantCache.getTenantIdMaps().get(String.valueOf(user.getTenant_id())),        
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
        mAdapter.addItem(user.getUser_id(), rowValues, false, false);
        
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
        mProgressDialog = UtilTools.showProgressDialog(StakeholderActivity.this, true, false);
    }

    private void dismissProgressDialog() {
        if (mProgressDialog != null && mProgressDialog.isShowing())
            mProgressDialog.dismiss();
    }
    
    @SuppressWarnings("unchecked")
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (resultCode != Activity.RESULT_OK) {
            return;
        }
        if (requestCode == IMPORT_USER_CODE) {
            List<User> userList = (List<User>) data
                    .getSerializableExtra(ListSelectActivity.RESULT_KEY);
            for (User user : userList) {
                mUserList.add(user);
                addUser(user);
            }
            mAdapter.refresh();
        }
        updateCooperation(mUserList);
        
    }

}
