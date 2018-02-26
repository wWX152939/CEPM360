package com.pm360.cepm360.app.module.document;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.pm360.cepm360.R;
import com.pm360.cepm360.app.cache.PermissionCache;
import com.pm360.cepm360.app.cache.ProjectCache;
import com.pm360.cepm360.app.cache.UserCache;
import com.pm360.cepm360.app.common.activity.DataManagerInterface;
import com.pm360.cepm360.app.common.activity.ProjectSelectActivity;
import com.pm360.cepm360.app.common.adpater.DataTreeListAdapter;
import com.pm360.cepm360.app.common.adpater.DataTreeListAdapter.TreeListAdapterInterface;
import com.pm360.cepm360.app.common.adpater.DataTreeListAdapter.ViewHolder;
import com.pm360.cepm360.common.GLOBAL;
import com.pm360.cepm360.common.ResultStatus;
import com.pm360.cepm360.common.json.AnalysisManager;
import com.pm360.cepm360.entity.Document;
import com.pm360.cepm360.entity.Project;
import com.pm360.cepm360.services.document.RemoteDocumentsService;

import java.util.ArrayList;
import java.util.List;

/**
 * 目录选择窗口
 * 
 * 如传入的参数包含project_id， 加载项目文档目录，没有传入，则不加载。
 * 
 * 返回目录的id串(20,23,50)
 * 返回路径串{/公共文档/施工文档, /项目文档/会议纪要/}
 *
 */
public class DirectorySelectActivity extends Activity {
	private final int PROJECT_SELECT_RESULT = 0x101;
	
    private Button mConfirmButton;
    private Button mSelectButton;
    private ImageView mCancelButton;
    private TextView mTipText;
    private TextView mTempPaths;
    private TextView mTitle;
    private LinearLayout mTempLayout;
    
    private Document mCurrentDirectory;
    private ListView mTreeView;
    private DataTreeListAdapter<Document> mTreeViewAdapter;
    private List<Document> mProjectList;
    
    private int mProjectId;
    private String mTempDirIds;
    private ArrayList<String> mTempPathLists = new ArrayList<String>();
    private static final int MAX_SELECT_COUNT = 5;
    
    // 传入的operation 操作： 选择目录 或归档
    public static final String OPERATION = "operation";
    // 传入的Project Id Key
    public static final String PROJECT_ID_KEY = "project_id";
    // 传入的已经选择的ids key
    public static final String TEMP_DIRS_KEY = "temp_dir_ids";
    // 返回选择的目录Id列表 Key
    public static final String DIR_SELECTED_ID_KEY = "selected_ids";
    // 返回选择的目录path列表 Key
    public static final String DIR_SELECTED_PATH_KEY = "selected_paths";
    // 返回projectID
    public static final String RET_PROJECT_KEY = "ret_project";
    
    private Handler mHandler = new Handler(new Handler.Callback() {
        public boolean handleMessage(Message msg) {
            updateTempDirLayout();
            return false;
        }
    });
    
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.document_directory_select_activity);
        
        mConfirmButton = (Button) findViewById(R.id.btn_left);
        mCancelButton = (ImageView) findViewById(R.id.imgbtn_cancel);
        mSelectButton = (Button) findViewById(R.id.btn_right);
        
        mConfirmButton.setOnClickListener(mClickListener);
        mCancelButton.setOnClickListener(mClickListener);
        
        mTipText = (TextView) findViewById(R.id.tip_text);
        mTempPaths = (TextView) findViewById(R.id.temp_paths);
        mTitle = (TextView) findViewById(R.id.title);
        
        mTempLayout = (LinearLayout) findViewById(R.id.temp_path_layout);
        
        mTempDirIds = getIntent().getStringExtra(TEMP_DIRS_KEY);
        // 如果没有传入临时目录，则设置为0
        if (mTempDirIds == null || mTempDirIds.equals("")) {
            mTempDirIds = "0";
        }
        
        initContentView();
        loadData();
    }
    
    private void updateTempDirLayout() {
        if (mTempDirIds != null && !mTempDirIds.equals("") && !mTempDirIds.equals("0")) {
            mTempPathLists.clear();
            mTempLayout.setVisibility(View.VISIBLE);
            String[] dirIds = mTempDirIds.split(",");
            for (int i = 0; i < dirIds.length; i++) {
                String path = getDirectoryRelativePath(Integer.parseInt(dirIds[i]), "");
                if (path != null && !path.equals("")) {
                    mTempPathLists.add(path);
                }
            }
            
            if (mTempPathLists.size() > 0) {
                String paths = mTempPathLists.get(0);
                for (int i = 1; i < mTempPathLists.size(); i++) {
                    paths = mTempPathLists.get(i) + "\n" + paths;
                }
                mTempPaths.setText(paths);
            }
        }
    }
    
    // return 如 /公共文档/整合管理/../../
    private String getDirectoryRelativePath(int dirId, String paths) {
        String path = paths;
        List<Document> dataList = mTreeViewAdapter.getDataList();
        for (Document document : dataList) {
            if (dirId == document.getDocument_id()) {
                path = document.getName() + "/" + path;
                int pdirId = document.getParents_id();
                if (pdirId == 0) {
                    path = "/" + path;
                    break;
                } else {
                    path = getDirectoryRelativePath(pdirId, path);
                }
            }
        }
        if (!path.equals("")) {
            return path;
        } else {
            return "";
        }
    }
    
    View.OnClickListener mClickListener = new View.OnClickListener() {
        public void onClick(View view) {
            switch (view.getId()) {
            case R.id.btn_left:
                setResult();
                break;
            case R.id.imgbtn_cancel:
                finish();
                break;
            }
        }
    };
    
    private List<Integer> getSelectedList() {
        List<Integer> selected = new ArrayList<Integer>();
        for (Document document : mTreeViewAdapter.getDataList()) {
            if (document.isSelected()) {
                selected.add(document.getDocument_id());
            }
        }
        return selected;
    }
    
    private void setResult() {
        List<Integer> selectedList = getSelectedList();
        if ((mTempDirIds == null || mTempDirIds.equals(""))
                && selectedList.size() == 0) {
            Toast.makeText(this, getString(R.string.select_directory_first),
                    Toast.LENGTH_SHORT).show();
            return;
        }
        String ids = "";
        if (selectedList.size() == 0) {
            // 没有选择任何目录，则默认使用临时归档的目录
            if (mTempDirIds != null && !mTempDirIds.equals("")) {
                ids = mTempDirIds;
            } else {
                Toast.makeText(this, getString(R.string.select_directory_first),
                        Toast.LENGTH_SHORT).show();
                return;
            }
        } else {
            // 选择了目录，则使用新的目录，如有临时目录包含‘0’， 则保留‘0’
            mTempPathLists.clear();
            StringBuilder idsBuilder = new StringBuilder();
            for (Integer id : selectedList) {
                idsBuilder.append(id + ",");
                
                String path = getDirectoryRelativePath(id, "");
                if (path != null && !path.equals("")) {
                    mTempPathLists.add(path);
                }
            }
//            if (mTempDirIds != null && !mTempDirIds.equals("")) {
//                String[] dirIds = mTempDirIds.split(",");
//                for (int i = 0; i < dirIds.length; i++) {
//                    if (dirIds[i].equals("0")) {
//                        idsBuilder.append("0"  + ",");
//                        break;
//                    }
//                }
//            }
            ids = idsBuilder.substring(0, idsBuilder.length() - 1);
        }
        
        Intent intent = new Intent();
        intent.putExtra(DIR_SELECTED_ID_KEY, ids);
        intent.putStringArrayListExtra(DIR_SELECTED_PATH_KEY, mTempPathLists);
        intent.putExtra(RET_PROJECT_KEY, mProjectId);
        
        setResult(Activity.RESULT_OK, intent);
        finish();
    }
    
    private void initContentView() {
        mTreeViewAdapter = new DataTreeListAdapter<Document>(this, false, directoryListener);
        mTreeView = (ListView) findViewById(R.id.tree_listview);
        mTreeView.setAdapter(mTreeViewAdapter);
    }
    
    TreeListAdapterInterface directoryListener = new TreeListAdapterInterface() {

        @Override
        public int getLayoutId() {
            return R.layout.document_directory_select_list_item;
        }

        @Override
        public void initListViewItem(ViewHolder holder, int position) {
            Document docs = mTreeViewAdapter.getItem(position);
            if (docs.isHas_child() && !docs.isExpanded()) {
                holder.ivs[0].setImageResource(R.drawable.item_collapse);                
            } else if (docs.isHas_child() && docs.isExpanded()) {
                holder.ivs[0].setImageResource(R.drawable.item_expand);
            } else if (!docs.isHas_child()){
                holder.ivs[0].setImageResource(R.drawable.item_collapse);                
            }
            
            if (docs.isHas_child() || docs.getParents_id() == 0) {
                holder.cbs[0].setVisibility(View.INVISIBLE);
            } else {
                holder.cbs[0].setVisibility(View.VISIBLE);  
            }

            holder.cbs[0].setChecked(docs.isSelected());
            holder.ivs[1].setImageResource(R.drawable.folder2);
            holder.tvs[0].setText(docs.getName());
        }

        @Override
        public void regesterListeners(final ViewHolder holder, final int position) {
            View.OnClickListener clickListener = new View.OnClickListener() {

                @Override
                public void onClick(View view) {
                    mTreeViewAdapter.updateListView(position);                                       
                    mCurrentDirectory = mTreeViewAdapter.getItem(position);
                    
                    boolean hasChild = mCurrentDirectory.isHas_child();
                    int parentId = mCurrentDirectory.getParents_id();
                    if (!hasChild && parentId > 0) {
                        //mTreeViewAdapter.setPickSelected(position);
                        mTreeViewAdapter.getItem(position).setSelected(!mCurrentDirectory.isSelected());
                        
                        if (mTreeViewAdapter.getSelected().size() > MAX_SELECT_COUNT) {
                            //mTreeViewAdapter.setPickSelected(position);
                            mTreeViewAdapter.getItem(position).setSelected(!mCurrentDirectory.isSelected());
                            mTipText.setVisibility(View.VISIBLE);
                        } else{
                            mTipText.setVisibility(View.INVISIBLE);
                        }                       
                        mTreeViewAdapter.notifyDataSetChanged();
                    }
                }                
            };
            holder.ivs[0].setOnClickListener(clickListener);
            holder.ivs[1].setOnClickListener(clickListener);
            holder.tvs[0].setOnClickListener(clickListener);
            
            holder.cbs[0].setOnClickListener(new Button.OnClickListener() {
                @Override
                public void onClick(View view) {
                    //mTreeViewAdapter.setPickSelected(position);
                    Document doc = mTreeViewAdapter.getItem(position);
                    mTreeViewAdapter.getItem(position).setSelected(!doc.isSelected());
                    mTreeViewAdapter.notifyDataSetChanged();
                }
            });
        }

        @Override
        public void initLayout(View convertView, ViewHolder holder) {
            holder.tvs = new TextView[1];
            holder.tvs[0] = (TextView) convertView.findViewById(R.id.text);
            
            holder.ivs = new ImageView[2];
            holder.ivs[0] = (ImageView) convertView.findViewById(R.id.icon);
            holder.ivs[1] = (ImageView) convertView.findViewById(R.id.type);
            
            holder.cbs = new CheckBox[1];
            holder.cbs[0] = (CheckBox) convertView.findViewById(R.id.checkbox);
        }

        @Override
        public void calculateContentItemCount() {
            // TODO Auto-generated method stub
        }        
    };
    
    private void setTitleText(String projectName) {
    	String title = getString(R.string.select_archive_directory) + "(" + getString(R.string.current_project) + ":" + projectName + ")";
    	mTitle.setText(title);
    }
    
    private void loadData() {
        // 个人文档
        Document directory1 = new Document();
        directory1.setDirectory_type(GLOBAL.DIR_TYPE_PERSONAL);
        directory1.setUser_id(UserCache.getCurrentUser().getUser_id());
        directory1.setTenant_id(UserCache.getCurrentUser().getTenant_id());
        RemoteDocumentsService.getInstance().getDirectoryList(directoryManager, directory1);

        // 没有归档权限
        if (!PermissionCache.hasSysPermission(GLOBAL.SYS_ACTION[54][0])) {
        	return;
        }
        
        // 项目文档
        int projectId = getIntent().getIntExtra(PROJECT_ID_KEY, 0);
        if (projectId != 0) {
        	setTitleText(ProjectCache.getProjectIdMaps().get(Integer.toString(projectId)));
            Document directory2 = new Document();
            directory2.setDirectory_type(GLOBAL.DIR_TYPE_PROJECT);
            directory2.setTenant_id(UserCache.getCurrentUser().getTenant_id());
            directory2.setProject_id(projectId);
            RemoteDocumentsService.getInstance().getDirectoryList(directoryManager, directory2);
        } else {
        	mSelectButton.setVisibility(View.VISIBLE);
        	mSelectButton.setOnClickListener(new OnClickListener() {
				
				@Override
				public void onClick(View v) {
					Intent intent = new Intent();
					intent.setClass(DirectorySelectActivity.this, ProjectSelectActivity.class);
					intent.putExtra("action", ProjectSelectActivity.ACTION_PICK);
					startActivityForResult(intent, PROJECT_SELECT_RESULT);
				}
			});
        	
        }
        
        // 公共文档     
        Document directory3 = new Document();
        directory3.setDirectory_type(GLOBAL.DIR_TYPE_PUBLIC);
        directory3.setTenant_id(UserCache.getCurrentUser().getTenant_id());
        RemoteDocumentsService.getInstance().getDirectoryList(directoryManager, directory3);
    }
    
    /**
     * 目录树回调接口
     */
    DataManagerInterface directoryManager = new DataManagerInterface() {
        @SuppressWarnings("unchecked")
        @Override
        public void getDataOnResult(ResultStatus status, List<?> list) {
            //dismissProgressDialog();
            switch (status.getCode()) {
            case AnalysisManager.SUCCESS_DB_QUERY:
                mTreeViewAdapter.addDataList((List<Document>) list);
                mHandler.sendEmptyMessage(0);
                break;
            default:
                break;
            }
            
            if (status.getCode() != AnalysisManager.SUCCESS_DB_QUERY) {
                //showToast(SHOW_TOAST, status.getMessage());
            }
        }
    };
    
    DataManagerInterface mProjectDirectoryManager = new DataManagerInterface() {
        @SuppressWarnings("unchecked")
        @Override
        public void getDataOnResult(ResultStatus status, List<?> list) {
            //dismissProgressDialog();
            switch (status.getCode()) {
            case AnalysisManager.SUCCESS_DB_QUERY:
            	if (mProjectList != null) {
            		mTreeViewAdapter.deleteMultiTreeNode(mProjectList);
            	}
            	
            	mProjectList = (List<Document>) list;
                mTreeViewAdapter.addDataList(mProjectList);
                mHandler.sendEmptyMessage(0);
                break;
            default:
                break;
            }
            
            if (status.getCode() != AnalysisManager.SUCCESS_DB_QUERY) {
                //showToast(SHOW_TOAST, status.getMessage());
            }
        }
    };
    
    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
    	super.onActivityResult(requestCode, resultCode, data);
    	if (requestCode == PROJECT_SELECT_RESULT && resultCode == Activity.RESULT_OK) {
    		Project project = (Project) data.getSerializableExtra("project");
    		if (project != null) {
    			mProjectId = project.getProject_id();
    			mSelectButton.setText(R.string.switch_project);
    			setTitleText(project.getName());
    			Document directory2 = new Document();
                directory2.setDirectory_type(GLOBAL.DIR_TYPE_PROJECT);
                directory2.setTenant_id(UserCache.getCurrentUser().getTenant_id());
                directory2.setProject_id(project.getProject_id());
                RemoteDocumentsService.getInstance().getDirectoryList(mProjectDirectoryManager, directory2);
    		}
    	}  
    }
}
