package com.pm360.cepm360.app.module.project;

import android.app.ProgressDialog;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.LinearLayout.LayoutParams;
import android.widget.ListView;
import android.widget.TextView;

import com.pm360.cepm360.R;
import com.pm360.cepm360.app.cache.ObsCache;
import com.pm360.cepm360.app.cache.ProjectCache;
import com.pm360.cepm360.app.cache.RoleCache;
import com.pm360.cepm360.app.cache.UserCache;
import com.pm360.cepm360.app.common.activity.DataManagerInterface;
import com.pm360.cepm360.app.common.adpater.DataListAdapter;
import com.pm360.cepm360.app.common.adpater.DataListAdapter.ListAdapterInterface;
import com.pm360.cepm360.app.common.adpater.DataTreeListAdapter;
import com.pm360.cepm360.app.common.adpater.DataTreeListAdapter.TreeListAdapterInterface;
import com.pm360.cepm360.app.common.adpater.DataTreeListAdapter.ViewHolder;
import com.pm360.cepm360.app.utils.UtilTools;
import com.pm360.cepm360.common.GLOBAL;
import com.pm360.cepm360.common.ResultStatus;
import com.pm360.cepm360.common.json.AnalysisManager;
import com.pm360.cepm360.entity.OBS;
import com.pm360.cepm360.entity.Project;
import com.pm360.cepm360.entity.Prole;
import com.pm360.cepm360.entity.Role;
import com.pm360.cepm360.entity.User;
import com.pm360.cepm360.services.project.RemoteProjectService;
import com.pm360.cepm360.services.system.RemoteUserService;

import java.util.ArrayList;
import java.util.List;

/**
 * 修改或分配项目人员
 *
 */
public class UserDistributionFragment extends Fragment {
    
    private Button mButtonLeft;
    private Button mButtonMiddle;
    private ProjectCreaterActivity mActivity;
    private ProgressDialog mProgressDialog;
    private Project mProject;
    private int mType;
    
    // 0: 分配用户， 1: 修改用户，2: 添加项目
    private int mOperation;
    private ArrayList<Role> mRoleList = new ArrayList<Role>();
    private ArrayList<User> mUserList = new ArrayList<User>();
    private ArrayList<OBS> mObsList = new ArrayList<OBS>();
    private ArrayList<User> mSelectedUserList = new ArrayList<User>();

    private ListView mUserListView;
    private DataListAdapter<User> mUserListAdapter;
    
    private CheckBox mHeaderCheckBox;
    private View mUserListHeaderView;
    private ListView mTreeView;
    private DataTreeListAdapter<OBS> mTreeViewAdapter;
    private LinearLayout mTreeViewLayout;
    private ImageView mExpandImageView;
    private boolean mTreeViewExpanded = false;
    private OBS mCurrentObs = new OBS();
    
    private Handler mHandler = new Handler(new Handler.Callback() {
        @Override
        public boolean handleMessage(Message msg) {
            if (msg.what == 1) { // 点击了左边的OBS, 加载显示相应的用户
                List<User> userList = getUserListByIds(mCurrentObs.getUser_ids());
                mUserListAdapter.setShowDataList(userList);
            }

            mUserListAdapter.notifyDataSetChanged();
            return false;
        }
    });
    
    private DataManagerInterface mProjectAddManager = new DataManagerInterface() {

        @Override
        public void getDataOnResult(ResultStatus status, List<?> list) {            
            if (status.getMessage() != null && !status.getMessage().equals("")) {
                if (status.getCode() == AnalysisManager.SUCCESS_DB_ADD) {
                    UtilTools.showToast(mActivity, R.string.project_add_sucess);
                } else {
                    dismissProgressDialog();
                    UtilTools.showToast(mActivity, status.getMessage());
                }
            }
            if (status.getCode() == AnalysisManager.SUCCESS_DB_ADD && list != null
                    && list.size() > 0) {
                for (Object object : list) {
                    if (object instanceof Project) {
                        ProjectCache.addProject((Project) object);
                        addProle((Project) object);
                    }
                }
            } else {
                dismissProgressDialog();
            }
        }
    };
    
    @SuppressWarnings("unchecked")
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
            Bundle savedInstanceState) {
        
        View view = inflater.inflate(
                R.layout.user_distribute_fragment, container,
                false);
        mActivity = (ProjectCreaterActivity) getActivity();
        mOperation = getArguments().getInt("operation");
        mType = getArguments().getInt("type");
        mProject = (Project) getArguments().getSerializable("project");
        ArrayList<User> userlist = (ArrayList<User>) getArguments().getSerializable("user_list");
        if (userlist != null && userlist.size() > 0) {
            if (mOperation == 0) { // 分配
                mUserList.addAll(userlist);
            } else if (mOperation == 1){ // 修改， 过滤掉领导和创建者
                for (User user : userlist) {
                    mUserList.add(user);
//                    if (!isCompanyLeader(user) && !isCreater(user)) {
//                        mUserList.add(user);
//                    }
                }
            }
        }
        
        initContentView(view);
        initObsTreeView(view);
        setupButtonBar(view);
        loadUserData();
        
        return view;
    }
    
    View.OnClickListener clickListener = new View.OnClickListener() {
        
        @Override
        public void onClick(View view) {
            switch (view.getId()) {
            case R.id.button_previous:
                mActivity.switchContent(0);
                break;
            case R.id.button_finish:
                if (mOperation == 0) {
                    addProle(mProject);
                } else if (mOperation == 1) {
                    updateProle();
                } else if (mOperation == 2) {
                    addProject();
                }
                break;
            default:
                break;
            }            
        }
    };
    
    private void setupButtonBar(View view) {
        mButtonLeft = (Button) view.findViewById(R.id.button_previous);
        mButtonMiddle = (Button) view.findViewById(R.id.button_finish);
        
        if (mOperation == 2) {
            mButtonLeft.setVisibility(View.VISIBLE);
        } else {
            mButtonLeft.setVisibility(View.GONE);
        }
        
        mButtonLeft.setOnClickListener(clickListener);
        mButtonMiddle.setOnClickListener(clickListener);
    }
    
    private void loadObsData() {
        mObsList.clear();
        for(OBS obs : ObsCache.getObsLists()) {
            obs.setCount(0);
            mObsList.add(obs);            
        }
        mTreeViewAdapter.addDataList(mObsList);
        
        if (RoleCache.getRoleLists() != null) {
            mRoleList.clear();
            for(Role role : RoleCache.getRoleLists()) {
                mRoleList.add(role);
            }            
        }
    }
    
    private void loadUserData() {
        if (mOperation == 0 || mOperation == 2) {
            StringBuilder userBuilder = new StringBuilder();
            int size = mUserList.size();
            for (int i = 0; i < size; i++) {
                userBuilder.append(mUserList.get(i).getUser_id() + ",");
            }
            String user = userBuilder.toString();
            if (user.equals("")) {
                user = "0";
            } else if (user.endsWith(",")) {
                user = user.substring(0, user.length() - 1);
            }

            showProgressDialog("Getting user list...");
            mUserList.clear();
            RemoteUserService.getInstance().getUndistributedUserList(
                    new DataManagerInterface() {

                        @Override
                        public void getDataOnResult(ResultStatus status, List<?> list) {
                            dismissProgressDialog();
                            if (status.getCode() != AnalysisManager.SUCCESS_DB_QUERY
                                    && status.getMessage() != null
                                    && !status.getMessage().equals("")) {
                                UtilTools.showToast(mActivity,
                                        status.getMessage());
                            }
                            if ((status.getCode() == AnalysisManager.SUCCESS_DB_QUERY)
                                    && list != null
                                    && list.size() > 0) {
                                for (int i = list.size() - 1; i >= 0; i--) {
                                    if (list.get(i) instanceof User) {
                                        User user = (User) list.get(i);
                                        user.setProle(user.getRole());
                                        mUserList.add(user);
                                    }
                                }
                                mUserListAdapter.setShowDataList(mUserList);
                                mHandler.sendEmptyMessage(0);
                                loadObsData();
                            }
                        }
                    }, user, UserCache.getCurrentUser().getTenant_id());
        } else { // 修改用户
            mSelectedUserList.addAll(mUserList);
            mUserListAdapter.setShowDataList(mUserList);
            mUserListAdapter.setSelectedAll();
            mHandler.sendEmptyMessage(0);           
            loadObsData();
        }
    }

    private void addProject() {
        showProgressDialog("Add new project...");
        RemoteProjectService.getInstance().addProject(mProjectAddManager,
                mActivity.getProject());
    }
    
    private void addProle(final Project project) {
        ArrayList<Prole> proleList = new ArrayList<Prole>();
        User user = UserCache.getCurrentUser();
        boolean included = false;
        for (User u : mSelectedUserList) {
            Prole prole = new Prole();
            prole.setTenant_id(UserCache.getCurrentUser().getTenant_id());
            prole.setProject_id(project.getProject_id());
            prole.setUser_id(u.getUser_id());
            prole.setRole(u.getProle());
            prole.setType(2);
            prole.setIDU(GLOBAL.IDU_INSERT);
            proleList.add(prole);
            if (user.getUser_id() == u.getUser_id()) {
                included = true;
            }
        }

        if (!included && mOperation == 2 && !isCompanyLeader(user)) {
            Prole prole = new Prole();
            prole.setTenant_id(UserCache.getCurrentUser().getTenant_id());
            prole.setProject_id(project.getProject_id());
            prole.setUser_id(user.getUser_id());
            prole.setRole(user.getRole());
            prole.setType(2);
            prole.setIDU(GLOBAL.IDU_INSERT);
            proleList.add(prole);
        }

        if (proleList.size() == 0) {
            dismissProgressDialog();
            mActivity.setResult(project);
        } else {
            //showProgressDialog("Distribut user list...");
            RemoteProjectService.getInstance().distributeProjectUser(
                    new DataManagerInterface() {
    
                        @Override
                        public void getDataOnResult(ResultStatus status, List<?> list) {
                            dismissProgressDialog();
                            if (status.getCode()
                                        != AnalysisManager.SUCCESS_DB_UPDATE
                                    && status.getMessage() !=
                                    null && !status.getMessage().equals("")) {
                                UtilTools.showToast(mActivity, status.getMessage());
                            }
                            //无论人员是否分配成功，都关闭对话框，因为项目已经创建成功。
                            if (status.getCode() == AnalysisManager.SUCCESS_DB_UPDATE) {
                                ProjectCache.updateProject(project);
                            }
                            mActivity.setResult(project);
                        }
                    }, proleList);
        }
    }
    
    private void updateProle() {
        ArrayList<Prole> proleList = new ArrayList<Prole>();
        for (int i = 0; i < mUserList.size(); i++) {
            User user = mUserList.get(i);
            Prole prole = new Prole();
            prole.setTenant_id(UserCache.getCurrentUser().getTenant_id());
            prole.setProject_id(mProject.getProject_id());
            prole.setUser_id(user.getUser_id());
            prole.setRole(user.getProle());
            prole.setType(mType);
            prole.setIDU(isChecked(user) ? GLOBAL.IDU_UPDATE : GLOBAL.IDU_DELETE);
            proleList.add(prole);
        }
        showProgressDialog("Distribut user list...");
        RemoteProjectService.getInstance().distributeProjectUser(
                new DataManagerInterface() {

                    @Override
                    public void getDataOnResult(ResultStatus status, List<?> list) {
                        dismissProgressDialog();
                        if (status.getCode()
                                    != AnalysisManager.SUCCESS_DB_UPDATE
                                && status.getMessage() !=
                                null && !status.getMessage().equals("")) {
                            UtilTools.showToast(mActivity, status.getMessage());
                        }
                        if (status.getCode() == AnalysisManager.SUCCESS_DB_UPDATE) {
                            mActivity.setResult(mProject);
                        }
                    }
                }, proleList);
    }
    
    private void initContentView(View view) {
        mHeaderCheckBox = (CheckBox) view.findViewById(R.id.cb_header);
        mHeaderCheckBox.setOnClickListener(new View.OnClickListener() {            
            @Override
            public void onClick(View view) {
                mSelectedUserList.removeAll(mUserListAdapter.getSelectedDatas());
                if (((CheckBox) view).isChecked()) {
                    mUserListAdapter.setSelectedAll();
                    mSelectedUserList.addAll(mUserListAdapter.getSelectedDatas());
                } else {
                    mUserListAdapter.clearSelection();
                    mUserListAdapter.notifyDataSetChanged();
                }
            }
        });
        mUserListHeaderView = view.findViewById(R.id.user_list_header);
        mUserListView = (ListView) view.findViewById(R.id.user_list_item);
        mUserListAdapter = new DataListAdapter<User>(mActivity, userAdpaterManager);        
        mUserListView.setAdapter(mUserListAdapter);
    }
    
    ListAdapterInterface<User> userAdpaterManager = new ListAdapterInterface<User>() {

        @Override
        public int getLayoutId() {
            return R.layout.project_user_list_item;
        }

        @Override
        public View getHeaderView() {
            return mUserListHeaderView;
        }

        @Override
        public void regesterListeners(
                final com.pm360.cepm360.app.common.adpater.DataListAdapter.ViewHolder viewHolder,
                final int position) {
            viewHolder.cbs[0].setOnClickListener(new Button.OnClickListener() {
                @Override
                public void onClick(View view) {
                    User user = mUserListAdapter.getItem(position);
                    if (((CheckBox) view).isChecked()) {
                        mSelectedUserList.add(user);
                    } else {
                        if (mHeaderCheckBox.isChecked()) {
                            mHeaderCheckBox.setChecked(false);
                        }
                        mSelectedUserList.remove(user);
                    }
                    mUserListAdapter.setPickSelected(position);
                }
            });
            
          for (int i = 0; i < viewHolder.tvs.length; i++) {
              viewHolder.tvs[i].setOnClickListener(new View.OnClickListener() {
                
                @Override
                public void onClick(View view) {
                    User user = mUserListAdapter.getItem(position);
                    if (viewHolder.cbs[0].isChecked()) {
                        viewHolder.cbs[0].setChecked(false);
                        mSelectedUserList.remove(user);
                    } else {
                        viewHolder.cbs[0].setChecked(true);
                        mSelectedUserList.add(user);
                    }                    
                    mUserListAdapter.setPickSelected(position);                    
                }
            });
          }
        

//            for (int i = 1; i < viewHolder.cbs.length; i++) {
//                viewHolder.cbs[i]
//                        .setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
//
//                            @Override
//                            public void onCheckedChanged(CompoundButton button,
//                                    boolean isChecked) {
//                                String prole = updateProle(
//                                        mUserListAdapter.getItem(position),
//                                        button.getText().toString(), isChecked);
//                                mUserListAdapter.getItem(position).setProle(prole);
//                            }
//                        });
//            }
        }
        
//        private String updateProle(User user, String roleName, boolean isChecked) {
//            if (TextUtils.isEmpty(roleName)) {
//                return user.getProle();
//            }
//            
//            String code = RoleCache.getRoleNameMaps().get(roleName);
//            String[] prole = user.getProle().split(",");           
//            if (isChecked) {
//                if (contain(prole, code)) {
//                    return user.getProle();
//                } else {
//                    if (user.getProle() != null && !user.getProle().equals("")) {
//                        return user.getProle() + "," + code;
//                    } else {
//                        return code;
//                    }
//                }
//            }
//            
//            StringBuilder proleBuilder = new StringBuilder();             
//            for (String p : prole) {
//                if (p.equals(code)) {
//                    continue;
//                }
//                proleBuilder.append(p + ",");
//            }
//            if (proleBuilder.length() > 1) {
//                return proleBuilder.substring(0, proleBuilder.length() - 1);
//            } else {
//                return "";
//            }
//        }

        @Override
        public void initListViewItem(
                View convertView,
                com.pm360.cepm360.app.common.adpater.DataListAdapter.ViewHolder holder,
                DataListAdapter<User> adapter, final int position) {
            
            holder.tvs[0].setText(adapter.getItem(position).getName());

            // 将选中的列表项高亮            
            if (mUserListAdapter.getSelectedList().contains((Integer) position)) {
                holder.cbs[0].setChecked(true);
            } else {
                holder.cbs[0].setChecked(false);
                mHeaderCheckBox.setChecked(false);
            }
            
            if (mSelectedUserList.contains(mUserListAdapter.getItem(position))) {
                if (!mUserListAdapter.isContainPosition(position)) {
                    holder.cbs[0].setChecked(true);
                    mUserListAdapter.setPickSelected(position);
                }               
            }
            
            if (mRoleList.size() > 0) {
                String[] prole = adapter.getItem(position).getProle().split(",");
                StringBuilder proleBuilder = new StringBuilder();
                for (int i = 0; i < prole.length; i++) {
                    String name = RoleCache.getRoleCodeMaps().get(prole[i]);
                    proleBuilder.append(name + ",");
                }
                if (proleBuilder.length() > 0) {
                    String roles = proleBuilder.substring(0, proleBuilder.length() - 1);
                    holder.tvs[1].setText(roles);
                }
                //String[] role = new String[mRoleList.size()];
                //for (int i = 0; i < mRoleList.size(); i++) {
                    //role[i] = mRoleList.get(i).getCode();
                    //holder.cbs[i+1].setText(mRoleList.get(i).getName());
                    //holder.cbs[i+1].setChecked(contain(prole, role[i]));
                //}
                
            }
        }

        @Override
        public void initLayout(
                View convertView,
                com.pm360.cepm360.app.common.adpater.DataListAdapter.ViewHolder holder) {
            
            holder.tvs = new TextView[2];
            holder.tvs[0] = (TextView) convertView.findViewById(R.id.user_name);
            holder.tvs[1] = (TextView) convertView.findViewById(R.id.user_role);
            
            holder.cbs = new CheckBox[1];
            holder.cbs[0] = (CheckBox) convertView.findViewById(R.id.checkbox);
//            holder.cbs[1] = (CheckBox) convertView.findViewById(R.id.checkbox1);
//            holder.cbs[2] = (CheckBox) convertView.findViewById(R.id.checkbox2);
//            holder.cbs[3] = (CheckBox) convertView.findViewById(R.id.checkbox3);
//            holder.cbs[4] = (CheckBox) convertView.findViewById(R.id.checkbox4);
//            holder.cbs[5] = (CheckBox) convertView.findViewById(R.id.checkbox5);
//            holder.cbs[6] = (CheckBox) convertView.findViewById(R.id.checkbox6);
//            holder.cbs[7] = (CheckBox) convertView.findViewById(R.id.checkbox7);
//            holder.cbs[8] = (CheckBox) convertView.findViewById(R.id.checkbox8);
//            holder.cbs[9] = (CheckBox) convertView.findViewById(R.id.checkbox9);
//            holder.cbs[10] = (CheckBox) convertView.findViewById(R.id.checkbox10);
//            holder.cbs[11] = (CheckBox) convertView.findViewById(R.id.checkbox11);
//            holder.cbs[12] = (CheckBox) convertView.findViewById(R.id.checkbox12);
//            holder.cbs[13] = (CheckBox) convertView.findViewById(R.id.checkbox13);
        }

        @Override
        public List<User> findByCondition(Object... condition) {
            return null;
        }

        @Override
        public boolean isSameObject(User t1, User t2) {
            if (t1.getUser_id() == t2.getUser_id()) {
                return true;
            }
            return false;
        }
        
    };
    
    private void initObsTreeView(View view) {
        mTreeViewLayout = (LinearLayout) view.findViewById(R.id.tree_layout);
        mTreeViewAdapter = new DataTreeListAdapter<OBS>(mActivity, false,
                obsAdapterInterface, 0,
                DataTreeListAdapter.BACKGROUND_TYPE_TREE_CONTENT, null);
        mTreeView = (ListView) view.findViewById(R.id.tree_listview);
        mTreeView.setAdapter(mTreeViewAdapter);
        
        mExpandImageView = (ImageView) view.findViewById(R.id.expand_icon);
        mExpandImageView.setOnClickListener(new View.OnClickListener() {
            
            @Override
            public void onClick(View view) {
                LayoutParams params = (LayoutParams) mTreeViewLayout.getLayoutParams();
                if (mTreeViewExpanded) {
                    params.weight = 3;
                    mTreeViewExpanded = false;
                } else {
                    params.weight = 2;
                    mTreeViewExpanded = true;
                }
                mTreeViewLayout.setLayoutParams(params);
                mExpandImageView
                .setImageResource(mTreeViewExpanded ? R.drawable.arrow_double_left_white
                        : R.drawable.arrow_double_right_white);
                mTreeViewAdapter.notifyDataChange();
            }
        });
    }
    
    TreeListAdapterInterface obsAdapterInterface = new TreeListAdapterInterface() {

        @Override
        public int getLayoutId() {
            return R.layout.project_obs_treeview_listitem;
        }

        @Override
        public void initListViewItem(ViewHolder holder, int position) {
            if(mTreeViewAdapter.getItem(position).getCount() > 0) {
                holder.tvs[0].setText(mTreeViewAdapter.getItem(position).getName()
                        + " (" + mTreeViewAdapter.getItem(position).getCount() + ")");
            } else {
                holder.tvs[0].setText(mTreeViewAdapter.getItem(position).getName());
            }
            
        }

        @Override
        public void regesterListeners(ViewHolder holder, final int position) {
            View.OnClickListener clickListener = new View.OnClickListener() {

                @Override
                public void onClick(View view) {
                    mTreeViewAdapter.updateListView(position);
                    mTreeViewAdapter.setSelected(position, true);
                    
                    OBS obs = mTreeViewAdapter.getItem(position);
                    if (mCurrentObs.getObs_id() != obs.getObs_id()) {
                        mCurrentObs = obs;
                        mHandler.sendEmptyMessage(1); 
                    }
                }
            };
            
            holder.ivs[0].setOnClickListener(clickListener);
            holder.tvs[0].setOnClickListener(clickListener);
            
        }

        @Override
        public void initLayout(View convertView, ViewHolder holder) {
            holder.ivs = new ImageView[1];
            holder.tvs = new TextView[1];
            holder.ivs[0] = (ImageView) convertView.findViewById(R.id.icon);
            holder.tvs[0] = (TextView) convertView.findViewById(R.id.text);
        }

        @Override
        public void calculateContentItemCount() {
            List<OBS> obsList = mTreeViewAdapter.getDataList();
            List<User> userList = new ArrayList<User>(); 
            for (User user : mUserList) {
                userList.add(user);
            }

            if (userList != null && userList.size() > 0) {
                // 遍历文件
                for (int i = 0; i < userList.size(); i++) {
                    // 遍历目录
                    for (int j = 0; j < obsList.size(); j++) {
                        // 如果文件的目录就是该目录
                        if (userList.get(i).getObs_id() == obsList.get(j).getObs_id()) {
                            obsList.get(j).setCount(obsList.get(j).getCount() + 1);
                            updateUserIds(obsList.get(j), userList.get(i).getUser_id());
                            // 更新父目录的计数
                            if (obsList.get(j).getParents_id() > 0) {
                                setParentObsCount(obsList, obsList.get(j), 1, userList.get(i).getUser_id());
                            }
                            // 找到就跳出循环
                            break;
                        }
                    }
                }
            }
        }
        
        private void setParentObsCount(List<OBS> obsList,
                OBS obs, int count, int userId) {
            for (int i = 0; i < obsList.size(); i++) {
                if (obs.getParents_id() == obsList.get(i).getObs_id()) {
                    int obsCount = obsList.get(i).getCount();
                    obsList.get(i).setCount(obsCount + count);
                    updateUserIds(obsList.get(i), userId);
                    // 递归设置目录计算
                    if (obsList.get(i).getParents_id() > 0) {
                        setParentObsCount(obsList,
                                obsList.get(i), count, userId);
                    }
                    // 找到就跳出循环
                    break;
                }
            }
        }        
    };
    
    private ArrayList<User> getUserListByIds(String userIds) {
        ArrayList<User> userList = new ArrayList<User>();
        String[] ids = userIds.split(",");
        for (String id : ids) {
            if (id.equals("")) {
                continue;
            }
            User user = findUserById(Integer.valueOf(id));
            if (user != null) {
                userList.add(user);
            }
        }
        return userList;
    }
    
    private void updateUserIds(OBS obs, int userId) {
        String userids = obs.getUser_ids();
        String[] ids = userids.split(",");
        if (!contain(ids, String.valueOf(userId))) {
            obs.setUser_ids(userids + userId + ",");
        }
    }
    
    public static boolean contain(String[] arr, String targetValue) {
        for (String s : arr) {
            if (s.equals(targetValue))
                return true;
        }
        return false;
    }
    
    private boolean isChecked(User user) {
        List<User> list = mUserListAdapter.getSelectedDatas();
        for (User u : list) {
            if (u.getUser_id() == user.getUser_id()) {
                return true;
            }
        }
        return false;
    }
    
    private boolean isCompanyLeader(User user) {
        if (user.getRole().equals(GLOBAL.ROLE_TYPE[0][0]) // 高管 或 总工
                || user.getRole().equals(GLOBAL.ROLE_TYPE[2][0])) { 
            return true;
        }
        return false;
    }
/*    
    private boolean isCreater(User user) {
        if (mProject.getCreater() == user.getUser_id()) {
            return true;
        }
        return false;
    }
*/    
    public User findUserById(int userId) {
        User res = null;
        for (User user : mUserList) {
            if (user.getUser_id() == userId) {
                res = user;
                break;
            }
        }
        return res;
    }
    
    private void showProgressDialog(String text) {
        dismissProgressDialog();
        mProgressDialog = UtilTools.showProgressDialog(mActivity, true, false);
    }

    private void dismissProgressDialog() {
        if (mProgressDialog != null && mProgressDialog.isShowing())
            mProgressDialog.dismiss();
    }
}
