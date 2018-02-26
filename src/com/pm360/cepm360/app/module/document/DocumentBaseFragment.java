package com.pm360.cepm360.app.module.document;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.ActivityNotFoundException;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.res.TypedArray;
import android.graphics.Color;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.v4.app.Fragment;
import android.text.Spannable;
import android.text.SpannableStringBuilder;
import android.text.style.ForegroundColorSpan;
import android.util.TypedValue;
import android.view.ActionMode;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
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
import com.pm360.cepm360.app.common.adpater.BaseDragListAdapter;
import com.pm360.cepm360.app.common.adpater.DataListAdapter;
import com.pm360.cepm360.app.common.adpater.DataListAdapter.ListAdapterInterface;
import com.pm360.cepm360.app.common.adpater.DataListAdapter.ViewHolder;
import com.pm360.cepm360.app.common.adpater.DataTreeListAdapter;
import com.pm360.cepm360.app.common.adpater.DataTreeListAdapter.TreeListAdapterInterface;
import com.pm360.cepm360.app.common.custinterface.CommonListInterface;
import com.pm360.cepm360.app.common.view.FloatingMenuView;
import com.pm360.cepm360.app.common.view.OptionsMenuView;
import com.pm360.cepm360.app.common.view.ServerDragLinearLayout;
import com.pm360.cepm360.app.utils.FileIconHelper;
import com.pm360.cepm360.app.utils.IntentBuilder;
import com.pm360.cepm360.app.utils.UtilTools;
import com.pm360.cepm360.common.GLOBAL;
import com.pm360.cepm360.common.ResultStatus;
import com.pm360.cepm360.common.json.AnalysisManager;
import com.pm360.cepm360.common.util.DateUtils;
import com.pm360.cepm360.common.util.MiscUtils;
import com.pm360.cepm360.entity.Document;
import com.pm360.cepm360.entity.Expandable;
import com.pm360.cepm360.entity.Files;
import com.pm360.cepm360.entity.Project;
import com.pm360.cepm360.entity.User;
import com.pm360.cepm360.services.document.RemoteDocumentsService;
import com.pm360.cepm360.services.system.RemoteUserService;

import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * 
 *  文档管理中4个fragment都基于这个DocumentBaseFragment
 *  DocumentBaseFragment 处理了 所有的文档相关操作
 *
 * @param <T>
 */
public class DocumentBaseFragment<T extends Expandable> extends Fragment
		implements BaseDragListAdapter.OngetBDLASlidePaneListener {
	private View mRootView;
	private TextView mDirTitle;
	private DocumentActivity mActivity;
    
    private DataTreeListAdapter<Document> mTreeViewAdapter;
    private DataListAdapter<Files> mFileListAdapter;
    private List<User> mUserList = new ArrayList<>();
    private List<Files> mFilesList = new ArrayList<Files>();
    private List<Files> mTempFilesList = new ArrayList<Files>();
    private List<Files> mDeleteFilesList = new ArrayList<Files>();
    private List<Document> mChildrenDirectoryList = new ArrayList<Document>();

    private View mContentListHeader;
    private ListView mTreeView;
    private ListView mFileListView;
    private String[] mDisplayItemNames;
    private int[] mItemIds;
    private CommonListInterface<Files> mContentListImplement;

    protected OptionsMenuView mOptionsMenuView;
    protected OptionsMenuView mTreeOptionsMenuView;
    private FloatingMenuView mFloatingMenu;
    protected String[] mDialogNames;

    private Project mCurrentProject;
    private Document mCurrentDirectory;
    private Files mCurrentFileItem;   

    private DataOperationHelper mDataHelper;
    
    private int mSelectDirRetRes;
    private int mProjectIdRet = 0;
    
    // 删除旗标
    private int mRemoveFlag = 1;
    public enum operation {
        ADD_ROOT, ADD_CHILD, MODIFY, RELEVANCE  
    }
    private operation mFlag;
    
    // handle 消息类型
    private static final int SHOW_FLOATING_MENU = 1;
    private static final int DISMISS_FLOATING_MENU = 2;
    private static final int CHANGE_ACTIONBAR_TITLE = 3;
    
    // activity 请求码
    private static final int DIR_SELECT_REQUEST = 100;
    private static final int PROJECT_REQUEST_CODE = 200;
    private static final int DOCUMENT_UPLOAD_CODE = 300;
    
    // 权限
    private static final String PROJECT_DOC_EDIT = GLOBAL.SYS_ACTION[6][0];
    private static final String PROJECT_DOC_VIEW = GLOBAL.SYS_ACTION[7][0];
    private static final String PUBLIC_DOC_EDIT = GLOBAL.SYS_ACTION[8][0];
    private static final String PUBLIC_DOC_VIEW = GLOBAL.SYS_ACTION[9][0];
    private static final String SYS_DOC_ARCHIVE = GLOBAL.SYS_ACTION[54][0];
    
    // 标记位，判断是不是第一进入文档管理，在项目文档页面，决定是否已选择过项目，是否弹出项目选择窗口
    private boolean isFirstStart;
    
    // 当前文档类型，（个人/项目/公共/归档）
    protected int mCurrentType;
    
    // Action Mode
    private Mode mCurrentMode;
    private static ActionMode mActionMode;
    public enum Mode {
        Normal, View, Pick
    };
    
    private Handler mHandler = new Handler(new Handler.Callback() {
        public boolean handleMessage(Message msg) {
            switch (msg.what) {
            case SHOW_FLOATING_MENU:
                if (mCurrentMode.equals(Mode.Normal) && hasEditPermission()) {
                    mFloatingMenu.setVisibility(View.VISIBLE);
                }
                break;
            case DISMISS_FLOATING_MENU:
                mFloatingMenu.setVisibility(View.GONE);
                break;
            case CHANGE_ACTIONBAR_TITLE:
                mActivity.setActionBarTitle(msg.obj.toString());
                break;
            default:
                break;
            }
            return false;
        }
    });
    
    /**
     * 初始化内容·列表数据
     * @param commonListInterface
     */
    protected void initContentList(CommonListInterface<Files> commonListInterface) {
        mContentListImplement = commonListInterface;
    }
    
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
            Bundle savedInstanceState) {        
        super.onCreateView(inflater, container, savedInstanceState);
        
        mActivity = (DocumentActivity) getActivity();
        mDataHelper = new DataOperationHelper(mActivity);
        mDataHelper.setOnDataChangedListener(new DataOperationHelper.OnDataChangedListener() {
            
            @Override
            public void onFilesDataLoaded(List<Files> fileLists) {
                mFilesList.addAll(fileLists);
                mFileListAdapter.setDataList(fileLists);
                if (mCurrentType == GLOBAL.DIR_TYPE_ARCHIVE) { // 文件归档
                    mFileListAdapter.setShowDataList(fileLists);
                } else {
                    mFileListAdapter.setShowDataList(loadFilesByDirectory(mCurrentDirectory));
                }
            }
            
            @Override
            public void onDirectoryDataLoaded(List<Document> directoryLists) {
                if (directoryLists != null && directoryLists.size() == 0) {
                    addDefaultDirectory(mCurrentType);
                }
                mTreeViewAdapter.setDataList(directoryLists);                
            }
        });

        if (hasPermission()) {
            mRootView = inflater.inflate(R.layout.document_management_layout,
                    container, false);
            
            mDirTitle = (TextView) mRootView.findViewById(R.id.dir_category);
            if (mCurrentType == GLOBAL.DIR_TYPE_ARCHIVE) {
                mDirTitle.setText(getString(R.string.project_directory));
            } else {
                mDirTitle.setText(getString(R.string.document_directory));
            }
            
            initTreeView(mRootView);
            initContentView(mRootView);
            createTreePopup();
            createFloatingMenu(mRootView);
            setMode(Mode.Normal);
        } else {
            mRootView = inflater.inflate(R.layout.no_permissions_content_layout,
                    container, false);
        }
        
        if (mCurrentType == GLOBAL.DIR_TYPE_PROJECT
                && ProjectCache.getCurrentProject() == null) {
            isFirstStart = true;
            startProjectSelectActivity();
        }
        
        return mRootView;
    }
    
    /**
     * 当前可能没有选择过项目，所以，当弹出项目选择界面后，返回回来时，
     * 需要重新拉数据，设置项目名称等。
     */
    @Override
    public void onResume() {
        super.onResume();

        if (mCurrentProject == null && mCurrentDirectory == null) {
            if (ProjectCache.getCurrentProject() != null) {
                mCurrentProject = ProjectCache.getCurrentProject();
                if (mCurrentType == GLOBAL.DIR_TYPE_PROJECT) {
                    mActivity.setActionBarTitle(mCurrentProject.getName());
                } else {
                    mActivity.setActionBarTitle("");
                }
            }
            if (hasPermission()) {
                loadData();
            }
            
        } else if (mCurrentProject != null && mCurrentType == GLOBAL.DIR_TYPE_PROJECT) {
            Project project = ProjectCache.getCurrentProject();
            if (project.getProject_id() != mCurrentProject.getProject_id()) {
                mActivity.setActionBarTitle(project.getName());
                mCurrentProject = project;
                mCurrentDirectory = null;
                mFileListAdapter.clearAll();
                mTreeViewAdapter.clearAll();
                mTreeViewAdapter.getLevelList().clear();
                if (hasPermission()) {
                    loadData();
                }
            }
        }
    }
    
    /**
     * 在fragment切换中，需要重新加载数据
     * 比如，在 文件归档 中完成了归档一个文件到公共文档目录，
     * 然后切换到公共文档时，需要重新加载数据才能看得到。
     */
    @Override
    public void onHiddenChanged(boolean hidden) {
        super.onHiddenChanged(hidden);
        cancelActionMode();
        
        if (!hidden && hasPermission()) {
            mTreeViewAdapter.clearAll();
            mFileListAdapter.clearAll();
            loadData();
        }
        if (mFloatingMenu != null) {
            mFloatingMenu.setVisibility(View.INVISIBLE);
        }
        
        if (!hidden) {
            if (mFileListAdapter != null) {
                mFileListAdapter.setSPListener();
            }
        } 
        
        if (mCurrentProject != null && mCurrentType == GLOBAL.DIR_TYPE_PROJECT) {
            mActivity.setActionBarTitle(mCurrentProject.getName());
        } else {
            mActivity.setActionBarTitle("");
        }
    }
    
    /**
     * 根据当前类型，加载相应的数据
     */
    private void loadData() {
        mDataHelper.loadData(mCurrentType);
    }
    
    /**
     * 注册目录树适配器
     * 
     * @param view
     */
    private void initTreeView(View view) {
        View headerView = (LinearLayout) mRootView
                .findViewById(R.id.tree_layout);
        mTreeViewAdapter = new DataTreeListAdapter<Document>(mActivity, true,
                treeAdapterInterface, R.array.document_tree_list_ids,
                DataTreeListAdapter.BACKGROUND_TYPE_TREE_CONTENT, headerView);
        mTreeViewAdapter.setOngetBDLASlidePaneListener(this);
        mTreeViewAdapter.setTreeMode();
		final ServerDragLinearLayout serverDragLayout = (ServerDragLinearLayout)mRootView.findViewById(R.id.file_browse_frame);
		mTreeViewAdapter.setOnDragStatusListener(new BaseDragListAdapter.OnDragStatusListener() {

			@Override
			public boolean getDragStatus() {
				return serverDragLayout.getDragStatus();
			}

			@Override
			public void setDragStatus(boolean newStatus) {
				serverDragLayout.setDragStatus(newStatus);
			}}); 
		serverDragLayout.addAdapter(mTreeViewAdapter);
        mTreeView = (ListView) view.findViewById(R.id.file_dir_list);
        mTreeView.setAdapter(mTreeViewAdapter);
    }
    
    /**
     * 目录树列表适配器回调接口
     */
    private TreeListAdapterInterface treeAdapterInterface = new TreeListAdapterInterface() {

        @Override
        public int getLayoutId() {
            return R.layout.treeview_list_item;
        }

        @Override
        public void initListViewItem(
                com.pm360.cepm360.app.common.adpater.DataTreeListAdapter.ViewHolder holder,
                int position) {
            if(mTreeViewAdapter.getItem(position).getFile_count() > 0) {
            	
            	String str = mTreeViewAdapter.getItem(position).getName()
                        + " (" + mTreeViewAdapter.getItem(position).getFile_count() + ")";
    			
    			int fstart=str.indexOf(" (" + mTreeViewAdapter.getItem(position).getFile_count() + ")");
    	        int fend=fstart+String.valueOf(mTreeViewAdapter.getItem(position).getFile_count()).length()+3;
    	        SpannableStringBuilder style=new SpannableStringBuilder(str);
    	        style.setSpan(new ForegroundColorSpan(Color.BLUE),fstart,fend,Spannable.SPAN_EXCLUSIVE_INCLUSIVE);
    	        holder.tvs[0].setText(style);
            } else {
                holder.tvs[0].setText(mTreeViewAdapter.getItem(position).getName());
            }
            
            if (mTreeViewAdapter.getItem(position).isHas_child() && !mTreeViewAdapter.getItem(position).isExpanded()) {
                holder.ivs[0].setImageResource(R.drawable.item_collapse);
            } else if (mTreeViewAdapter.getItem(position).isHas_child() && mTreeViewAdapter.getItem(position).isExpanded()) {
                holder.ivs[0].setImageResource(R.drawable.item_expand);
            } else if (!mTreeViewAdapter.getItem(position).isHas_child()){
                holder.ivs[0].setImageResource(R.drawable.item_collapse);
            }
            
            if (mTreeViewAdapter.getItem(position).getRelevance_path_code() == null 
                    || mTreeViewAdapter.getItem(position).getRelevance_path_code().equals("")) {
                holder.ivs[1].setImageResource(R.drawable.folder2);                
            } else {
                holder.ivs[1].setImageResource(R.drawable.folder_link);
            }
            holder.ivs[1].setVisibility(View.VISIBLE);
            
        }

        @Override
        public void regesterListeners(
                com.pm360.cepm360.app.common.adpater.DataTreeListAdapter.ViewHolder holder,
                final int position) {
            View.OnClickListener clickListener = new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    mTreeViewAdapter.updateListView(position);
                    mTreeViewAdapter.setSelected(position, true);
                    
                    mCurrentDirectory = mTreeViewAdapter.getItem(position);
                    mFileListAdapter.setShowDataList(loadFilesByDirectory(mCurrentDirectory));
                    
                    cancelActionMode();
                    if (mCurrentDirectory.isHas_child()) {
                        // 选择模式下，mFloatingMenu不被初始化，为null
                        if (mFloatingMenu != null) {
                            mFloatingMenu.setVisibility(View.INVISIBLE);
                        }                                               
                    } else {
                        if (mCurrentMode.equals(Mode.Normal) && hasEditPermission()) {
                            mFloatingMenu.setVisibility(View.VISIBLE);
                        }
                    }
                }                
            };
            
            holder.ivs[0].setOnClickListener(clickListener);
            holder.ivs[1].setOnClickListener(clickListener);
            holder.tvs[0].setOnClickListener(clickListener);
            
            View.OnLongClickListener longClickListener = new View.OnLongClickListener() {                
                @Override
                public boolean onLongClick(View view) {
                    if (mCurrentType != GLOBAL.DIR_TYPE_ARCHIVE) {
                        mTreeOptionsMenuView.showAsDropDown(
                                view,
                                -80,
                                (-view.getMeasuredHeight() - UtilTools.dp2pxH(
                                        view.getContext(), 44)));
                        mCurrentDirectory = mTreeViewAdapter.getItem(position);
                    }
                    return false;
                }
            };

            // 注册长按监听器
            holder.ivs[0].setOnLongClickListener(longClickListener);
            holder.ivs[1].setOnLongClickListener(longClickListener);
            holder.tvs[0].setOnLongClickListener(longClickListener);
            
        }

        @Override
        public void initLayout(
                View convertView,
                com.pm360.cepm360.app.common.adpater.DataTreeListAdapter.ViewHolder holder) {
            holder.tvs = new TextView[1];
            holder.tvs[0] = (TextView) convertView.findViewById(R.id.text);
            
            holder.ivs = new ImageView[2];
            holder.ivs[0] = (ImageView) convertView.findViewById(R.id.icon);
            holder.ivs[1] = (ImageView) convertView.findViewById(R.id.type);
            
        }

        /**
         * 计算树列表的计数
         */
        @Override
        public void calculateContentItemCount() {
            List<Document> treeList = mTreeViewAdapter.getDataList();
            List<Files> filesList = mFileListAdapter.getDataList();
            if (filesList != null && filesList.size() > 0) {
                if (mCurrentType == GLOBAL.DIR_TYPE_ARCHIVE) {
                    // 遍历文件
                    for (int i = 0; i < filesList.size(); i++) {
                        // 遍历目录
                        for (int j = 0; j < treeList.size(); j++) {
                            // 如果文件的目录就是该目录
                            if (filesList.get(i).getProject_id() == treeList.get(j).getProject_id()) {
                                treeList.get(j).setFile_count(
                                        treeList.get(j).getFile_count() + 1);
                                // 更新父目录的计数
                                if (treeList.get(j).getParents_id() > 0) {
                                    Utils.setParentDirectoryCount(treeList, treeList.get(j), 1);
                                }
                                break;
                            }
                        }
                    }
                } else {
                    // 遍历文件
                    for (int i = 0; i < filesList.size(); i++) {
                        // 遍历目录
                        for (int j = 0; j < treeList.size(); j++) {
                            // 如果文件的目录就是该目录
                            String[] dirStrings = filesList.get(i).getDirectory_id().split(",");
                            for (int k = 0; k < dirStrings.length; k++) {
                                if (!dirStrings[k].equals("") && Integer.parseInt(dirStrings[k]) == treeList.get(j).getDocument_id()) {
                                    treeList.get(j).setFile_count(
                                            treeList.get(j).getFile_count() + 1);
                                    // 更新父目录的计数
                                    if (treeList.get(j).getParents_id() > 0) {
                                        Utils.setParentDirectoryCount(treeList, treeList.get(j), 1);
                                    }
                                    // 找到就跳出循环
                                    break;
                                }
                            }
                        }
                    }
                }
            }
        }        
    };
    
    /**
     * 查找当前目录下的所有文件
     * 
     * @param directory
     * @return
     */
    private List<Files> loadFilesByDirectory(Document directory) {
        List<Files> fileList = mFileListAdapter.getDataList();
        List<Files> tmpList = new ArrayList<Files>();
        if (directory != null) {
            if (mCurrentType == GLOBAL.DIR_TYPE_ARCHIVE) {
                int projectId = directory.getProject_id();
                for (Files f : fileList) {
                    if (f.getProject_id() == projectId) {
                        tmpList.add(f);
                    }
                }
            } else {            
                int directoryId = directory.getDocument_id();
                for (Files f : fileList) {
                    String[] dirStrings = f.getDirectory_id().split(",");
                    for (int i = 0; i < dirStrings.length; i++) {
                        String id = dirStrings[i];
                        if (!id.equals("") && Integer.parseInt(id) == directoryId) {
                            tmpList.add(f);
                            break;
                        }
                    }
                }
            }
        }
        return tmpList;
    }
    
    /**
     * 注册文档列表适配器
     * 
     * @param view
     */
    private void initContentView(View view) {
        // 获取列表使用的相关资源
        mDisplayItemNames = getResources()
                        .getStringArray(mContentListImplement.getListHeaderNames());
        TypedArray typedArray = getResources()
                        .obtainTypedArray(mContentListImplement.getListHeaderIds());
        
        ViewGroup parent = (ViewGroup) view.findViewById(R.id.content_header_layout);
        mContentListHeader = LayoutInflater.from(getActivity())
                .inflate(mContentListImplement.getListHeaderLayoutId(), parent, false);
        ((TextView) mContentListHeader.findViewById(R.id.file_type)).setVisibility(View.VISIBLE);
        mContentListHeader.setBackgroundResource(R.color.content_listview_header_bg);
        parent.addView(mContentListHeader);

        if (mDisplayItemNames != null) {
            int itemLength = mDisplayItemNames.length;
            mItemIds = new int[itemLength];
            for (int i = 0; i < itemLength; i++) {
                mItemIds[i] = typedArray.getResourceId(i, 0);
                TextView tv = (TextView) mContentListHeader.findViewById(mItemIds[i]);
                tv.setTextSize(
                        TypedValue.COMPLEX_UNIT_PX,
                        getActivity().getResources().getDimensionPixelSize(
                                R.dimen.table_title_textsize));
                tv.setTextColor(Color.BLACK);
                tv.setText(mDisplayItemNames[i]);
            }
        }
        typedArray.recycle();
        
        mFileListView = (ListView) mRootView.findViewById(R.id.file_list_view);
        mFileListAdapter = new DataListAdapter<Files>(mActivity,
                mContentListInterface, mContentListImplement.getListHeaderIds());
        mFileListAdapter.setOngetBDLASlidePaneListener(this);
		final ServerDragLinearLayout serverDragLayout = (ServerDragLinearLayout)mRootView.findViewById(R.id.file_browse_frame);
		mFileListAdapter.setOnDragStatusListener(new BaseDragListAdapter.OnDragStatusListener() {

			@Override
			public boolean getDragStatus() {
				return serverDragLayout.getDragStatus();
			}

			@Override
			public void setDragStatus(boolean newStatus) {
				serverDragLayout.setDragStatus(newStatus);
			}});
		serverDragLayout.addAdapter(mFileListAdapter);
        mFileListView.setAdapter(mFileListAdapter);
    }
    
    private ListAdapterInterface<Files> mContentListInterface = new ListAdapterInterface<Files>() {

        @Override
        public int getLayoutId() {
            return mContentListImplement.getListLayoutId();
        }

        @Override
        public View getHeaderView() {
            return mContentListHeader;
        }

        @Override
        public void regesterListeners(ViewHolder viewHolder, final int position) {
            for (int i = 0; i < viewHolder.tvs.length; i++) {
                viewHolder.tvs[i].setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        mCurrentFileItem = mFileListAdapter.getItem(position);
                        if (mCurrentMode == Mode.Pick) {                            
                            mFileListAdapter.setPickSelected(position);
                            updateActionModeTitle(mActionMode, mActivity,
                                    mFileListAdapter.getSelectedList().size());
                            return;
                        }
                        mFileListAdapter.setSelected(position, true);
                        
                        // 根据不同类型，不同的权限，设置选项菜单的弹出内容
                        // 0:查看，1:删除，2:属性，3:归档，4:置顶，5:移动，6:复制
                        if (mCurrentType == GLOBAL.DIR_TYPE_ARCHIVE) {
                            if (mCurrentFileItem.getDir_type() != Integer.parseInt(GLOBAL.FILE_TYPE[2][0])
                                    && mCurrentFileItem.getDir_type() != Integer.parseInt(GLOBAL.FILE_TYPE[14][0])) {
                                // 不是 <现场图片> 也不是 <形象成果>, 隐藏 <置顶> 菜单
                                mOptionsMenuView.setVisibileMenu(4, false);
                            } else {
                                mOptionsMenuView.setVisibileMenu(4, true);
                            }
                        } else if (mCurrentType != GLOBAL.DIR_TYPE_PERSONAL){
                            if (mCurrentFileItem.getDir_type() != Integer.parseInt(GLOBAL.FILE_TYPE[2][0])
                                    && mCurrentFileItem.getDir_type() != Integer.parseInt(GLOBAL.FILE_TYPE[14][0])) {
                                mOptionsMenuView.setVisibileMenu(4, false);
                            } else {
                                mOptionsMenuView.setVisibileMenu(4, true);
                            }
                        }
                        
                        mOptionsMenuView.showAsDropDown(view, 0, (-view
                                .getMeasuredHeight() - UtilTools.dp2pxH(
                                view.getContext(), 42)));
                    }
                });
            }
            for (int i = 0; i < viewHolder.tvs.length; i++) {
                viewHolder.tvs[i].setOnLongClickListener(new View.OnLongClickListener() {
                    @Override
                    public boolean onLongClick(View view) {
                        if (mActionMode != null) {
                            mActionMode = null;
                        }
                        mActionMode = getActivity().startActionMode(mCallback);
                        mCurrentFileItem = mFileListAdapter.getItem(position);
                        mFileListAdapter.setPickSelected(position);
                        updateActionModeTitle(mActionMode, mActivity,
                                mFileListAdapter.getSelectedList().size());
                        return true;
                    }
                });
            }
        }

        @Override
        public void initListViewItem(View convertView, ViewHolder holder,
                DataListAdapter<Files> adapter, int position) {
            holder.ivs[0].setImageResource(FileIconHelper.getIcon((Files) adapter.getItem(position)));
            
            Map<String, String> item = FilesToMap((Files) adapter.getItem(position), mCurrentType);
            for (int i = 0; i < mDisplayItemNames.length; i++) {
                holder.tvs[i].setText(item.get(mDisplayItemNames[i]));
                holder.tvs[i].setTextColor(Color.BLACK);
            }            
        }

        @Override
        public void initLayout(View convertView, ViewHolder holder) {
            holder.tvs = new TextView[mItemIds.length];
            for (int i = 0; i < mItemIds.length; i++) {
                holder.tvs[i] = (TextView) convertView.findViewById(mItemIds[i]);
            }
            
            holder.ivs = new ImageView[1];
            holder.ivs[0] = (ImageView) convertView.findViewById(R.id.file_image);
            holder.ivs[0].setVisibility(View.VISIBLE);
        }

        @Override
        public List<Files> findByCondition(Object... condition) {
            // TODO Auto-generated method stub
            return null;
        }

        @Override
        public boolean isSameObject(Files t1, Files t2) {
            if (t1.getFile_id() == t2.getFile_id()) {
                return true;
            } else {
                return false;
            }
        }
        
    };
    
    /**
     * 文件bean转换为map
     */
    private Map<String, String> FilesToMap(Files bean, int type) {
        Map<String, String> mapItem = new HashMap<String, String>();
        if (type == GLOBAL.DIR_TYPE_ARCHIVE) { // 文件归档
            mapItem.put(mDisplayItemNames[0], bean.getTitle());
            String projectName = ProjectCache
                    .getProjectIdMaps().get(String.valueOf(bean.getProject_id()));
            mapItem.put(mDisplayItemNames[1], projectName == null ? "" : projectName); // 所属项目
            if (bean.getDir_type() > 0) {
                mapItem.put(mDisplayItemNames[2], String
                    .valueOf(GLOBAL.FILE_TYPE[bean.getDir_type() - 1][1])); // 来源
            }
            mapItem.put(mDisplayItemNames[3], UserCache
                    .getUserMaps().get(String.valueOf(bean.getAuthor()))); // 创建人
            mapItem.put(
                    mDisplayItemNames[4],
                    DateUtils.dateToString(DateUtils.FORMAT_LONG1,
                            bean.getCreate_time())); // 创建时间
            if (bean.getDir_type() == Integer.valueOf(GLOBAL.FILE_TYPE[2][0])
                    || bean.getDir_type() == Integer.valueOf(GLOBAL.FILE_TYPE[14][0])) {
                mapItem.put(mDisplayItemNames[5],
                        String.valueOf(GLOBAL.FILE_ARCHIVE[bean.getArchive()][1])
                                + "-"
                                + String.valueOf(GLOBAL.FILE_TOP[bean.getTop()][1])); // 是否归档, 是否置顶
            } else {
                mapItem.put(mDisplayItemNames[5], String
                        .valueOf(GLOBAL.FILE_ARCHIVE[bean.getArchive()][1])); // 是否归档
            }
        } else {
            mapItem.put(mDisplayItemNames[0], bean.getTitle());
            String userName = UserCache
                    .getUserMaps().get(String.valueOf(bean.getAuthor()));
            String dirType = "";
            if (bean.getDir_type() != 0) {
            	dirType = String.valueOf(GLOBAL.FILE_TYPE[bean.getDir_type() - 1][1]);
            }
            String projectName = ProjectCache
                    .getProjectIdMaps().get(String.valueOf(bean.getProject_id()));
            if (bean.getDir_type() == 9) { // 所属项目                   
                mapItem.put(mDisplayItemNames[1], getString(R.string.personal_upload));
            } else if (projectName != null) {
                mapItem.put(mDisplayItemNames[1], projectName + "-" + getString(R.string.archive));
            } else if (bean.getDir_type() > 0) {
                mapItem.put(mDisplayItemNames[1], dirType + "-" + getString(R.string.archive));
            }
            mapItem.put(mDisplayItemNames[2], userName == null ? "" : userName);
            mapItem.put(mDisplayItemNames[3],
                    DateUtils.dateToString(DateUtils.FORMAT_LONG1,
                            bean.getCreate_time()));
        }
        return mapItem;
    }
    
    /**
     * 创建文档的浮动菜单
     */
    private void createFloatingMenu(View view) {
        int[] menuResources = new int[] { R.drawable.icn_add };
        String[] menuTips = new String[] { getResources().getString(R.string.add) };

        mFloatingMenu = (FloatingMenuView) view
                .findViewById(R.id.floating_menu);
        for (int i = 0; i < menuTips.length; i++) {
            mFloatingMenu.addPopItem(menuTips[i], menuResources[i]);
        }
        mFloatingMenu
                .setPopOnItemClickListener(new AdapterView.OnItemClickListener() {
                    @Override
                    public void onItemClick(AdapterView<?> parent, View view,
                            int position, long id) {
                        switch (position) {
                        case 0:
                            mFloatingMenu.dismiss();
                            uploadNewFiles();
                            break;
                        default:
                            break;
                        }

                    }
                });
        mFloatingMenu.setVisibility(View.INVISIBLE);
    }
    
    /**
     * 添加文件前的准备，填充好Files对象，绑定给DocumentUploadActivity
     */
    private void uploadNewFiles() {
        if (mCurrentDirectory.getLevel() == 0) {
            Toast.makeText(mActivity, getString(R.string.add_directory_first),
                    Toast.LENGTH_SHORT).show();
            return;
        }

        Files file = new Files();
        file.setCode("");
        file.setDirectory_id(mCurrentDirectory.getDocument_id()+"");
        file.setPath(getDirectoryRelativePath());
        file.setStatus(getString(R.string.new_create));
        file.setVersion("1.0");
        file.setAuthor(UserCache.getCurrentUser().getUser_id());
        file.setCreate_time(new Date());
        if (mCurrentType == GLOBAL.DIR_TYPE_PERSONAL) {
            file.setDir_type(Integer.valueOf(GLOBAL.FILE_TYPE[8][0]));
        } else if (mCurrentType == GLOBAL.DIR_TYPE_PROJECT) {
            file.setDir_type(Integer.valueOf(GLOBAL.FILE_TYPE[9][0]));
        } else if (mCurrentType == GLOBAL.DIR_TYPE_PUBLIC) {
            file.setDir_type(Integer.valueOf(GLOBAL.FILE_TYPE[0][0]));
        }
        
        if (PermissionCache.hasSysPermission(SYS_DOC_ARCHIVE)) {
            file.setArchive(Integer.parseInt(GLOBAL.FILE_ARCHIVE[1][0]));
        } else {
            file.setArchive(Integer.parseInt(GLOBAL.FILE_ARCHIVE[2][0]));
        }

        Intent intent = new Intent(Intent.ACTION_MAIN);
        Bundle bundle = new Bundle();
        bundle.putSerializable(DocumentUploadActivity.UPLOAD_FILE_KEY, file);
        bundle.putBoolean(DocumentUploadActivity.NEED_SEND_MSG_KEY, true);
        intent.putExtras(bundle);
        intent.setClass(mActivity, DocumentUploadActivity.class);
        startActivityForResult(intent, DOCUMENT_UPLOAD_CODE);
    }
    
    /**
     * 获取目录路径 
     * 
     * @return 如 /公共文档/整合管理/../../
     */
    private String getDirectoryRelativePath() {
        String path = mCurrentDirectory.getName() + "/";
        int level = mCurrentDirectory.getLevel() - 1;
        int mSelectedPosition = mTreeViewAdapter.getSelected().get(0);
        for (int i = mSelectedPosition - 1; i >= 0 ; i--) {
            if (level == mTreeViewAdapter.getShowList().get(i).getLevel()) {
                path = mTreeViewAdapter.getShowList().get(i).getName() + "/" + path;
                level--;
            }
        }
        return "/" + path;
    }
    
    /**
     * 导航栏第一个是个人文档，第三个为公共文档
     * @param index
     */
    protected void setCurrentType(int index) {       
        if (index == 0) {
            mCurrentType = GLOBAL.DIR_TYPE_PERSONAL;
        } else if (index == 2) {
            mCurrentType = GLOBAL.DIR_TYPE_PUBLIC;
        } else {
            mCurrentType = index;
        }
    }
    
    public void setMode(Mode m) {
        mCurrentMode = m;
    }
    
    private boolean hasPermission() {
        if (mCurrentType == GLOBAL.DIR_TYPE_PERSONAL
                || mCurrentType == GLOBAL.DIR_TYPE_ARCHIVE) {
            return true;
        }
        
        if (mCurrentType == GLOBAL.DIR_TYPE_PROJECT) {
            if (PermissionCache.hasSysPermission(PROJECT_DOC_VIEW)
                || PermissionCache.hasSysPermission(PROJECT_DOC_EDIT)) {
                return true;
            }
        }
        if (mCurrentType == GLOBAL.DIR_TYPE_PUBLIC) {
            if (PermissionCache.hasSysPermission(PUBLIC_DOC_VIEW)
                    || PermissionCache.hasSysPermission(PUBLIC_DOC_EDIT)) {
                return true;
            }
        }
        return false;
    }
    
    private boolean hasEditPermission() {
        if (mCurrentType == GLOBAL.DIR_TYPE_PERSONAL)
            return true;
        if (mCurrentType == GLOBAL.DIR_TYPE_PROJECT) {
            if (PermissionCache.hasSysPermission(PROJECT_DOC_EDIT)) {
                return true;
            }
        }
        if (mCurrentType == GLOBAL.DIR_TYPE_PUBLIC) {
            if (PermissionCache.hasSysPermission(PUBLIC_DOC_EDIT)) {
                return true;
            }
        }
        return false;
    }
    
    private void updateActionModeTitle(ActionMode mode, Context context,
            int selectedNum) {
        if (mode != null) {
            mode.setTitle(context.getString(R.string.multi_select_title,
                    selectedNum));
        }
    }
    
    /**
     * ActionMode callback 用于列表的多选 或批量操作
     */
    private ActionMode.Callback mCallback = new ActionMode.Callback() {
        @Override
        public boolean onPrepareActionMode(ActionMode mode, Menu menu) {
            setMode(Mode.Pick);
            return false;
        }

        @Override
        public void onDestroyActionMode(ActionMode mode) {
            mFileListAdapter.clearSelection();
            setMode(Mode.Normal);
            mFileListAdapter.setSelected(-1, false);
            mode = null;
        }

        @Override
        public boolean onCreateActionMode(ActionMode mode, Menu menu) {
            MenuInflater inflater = mode.getMenuInflater();
            if (mCurrentType == GLOBAL.DIR_TYPE_ARCHIVE) {
                if (PermissionCache.hasSysPermission(SYS_DOC_ARCHIVE)) {
                    inflater.inflate(R.menu.document_archive_operation_menu, menu);
                } else {
                    inflater.inflate(R.menu.document_top_operation_menu, menu);
                }
            } else {
                inflater.inflate(R.menu.operation_menu, menu);
            }            
            setMode(Mode.Pick);
            return true;
        }

        @Override
        public boolean onActionItemClicked(ActionMode mode, MenuItem item) {
            switch (item.getItemId()) {
            case R.id.action_delete:
                showDeleteTips();
                break;
            case R.id.action_archive:                
                if (mFileListAdapter.getSelectedDatas().size() > 0) {
                    mTempFilesList.clear();
                    mTempFilesList.addAll(mFileListAdapter.getSelectedDatas());
                    startDirectorySelectActivity(R.string.archive);
                }
                break;
            case R.id.action_to_top:
                for (Files files : mFileListAdapter.getSelectedDatas()) {
                    setToTop(files);
                }
                cancelActionMode();
                break;
            case R.id.action_select_all:
                mFileListAdapter.setSelectedAll();
                break;
            }
            updateActionModeTitle(mActionMode, mActivity, mFileListAdapter
                    .getSelectedList().size());
            return false;
        }
    };
    
    /**
     * 取消 ActionMode
     */
    public void cancelActionMode() {
        if (mActionMode != null) {
            mActionMode.finish();
            mActionMode = null;
        }
        setMode(Mode.Normal);
    }
    
    /**
     * 显示删除确认对话框
     */
    public void showDeleteTips() {
        if (mCurrentType == GLOBAL.DIR_TYPE_PROJECT
                && !PermissionCache.hasSysPermission(PROJECT_DOC_EDIT)) {
            showToast(getResources().getString(R.string.no_edit_permission));
            return;
        }

        if (mCurrentType == GLOBAL.DIR_TYPE_PUBLIC
                && !PermissionCache.hasSysPermission(PUBLIC_DOC_EDIT)) {
            showToast(getResources().getString(R.string.no_edit_permission));
            return;
        }

        new AlertDialog.Builder(mActivity)
                .setTitle(getResources().getString(R.string.remind))
                .setMessage(getResources().getString(R.string.confirm_delete))
                .setPositiveButton(getResources().getString(R.string.confirm),
                        new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.dismiss();
                        mTempFilesList.clear();
                        mDeleteFilesList.clear();
                        if (mCurrentMode == Mode.Normal) {
                            mTempFilesList.add(mCurrentFileItem);
                            if (mCurrentFileItem.getDirectory_id().split(",").length == 1) {
                                mDeleteFilesList.add(mCurrentFileItem);
                            }
                            deleteFileObject(mCurrentFileItem);
                        } else {
                            mTempFilesList.addAll(mFileListAdapter.getSelectedDatas());
                            for (Files f : mTempFilesList) {
                                if (mCurrentFileItem.getDirectory_id().split(",").length == 1) {
                                    mDeleteFilesList.add(f);
                                }
                                deleteFileObject(f);
                            }
                            cancelActionMode();
                        }
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
    
    // 删除对象
    private void deleteFileObject(Files files) {
        files.setTenant_id(UserCache.getCurrentUser().getTenant_id());
        RemoteDocumentsService.getInstance().deleteFile(fileManager, files, mCurrentDirectory.getDocument_id());
    }
    
    public void showToast(String message) {
        if (mActivity != null) {
            Toast.makeText(mActivity, message, Toast.LENGTH_SHORT).show();
        }
    }
    
    /**
     * 文档回调接口
     */
    DataManagerInterface fileManager = new DataManagerInterface() {
        @Override
        public void getDataOnResult(ResultStatus status, List<?> list) {
            switch (status.getCode()) {
            case AnalysisManager.EXCEPTION_DB_ADD:
                mHandler.sendEmptyMessage(SHOW_FLOATING_MENU);
                break;
            case AnalysisManager.SUCCESS_DB_DEL:
                if (mRemoveFlag == mTempFilesList.size()) {
                    updateTreeListCount(-mRemoveFlag);
                    mTempFilesList.removeAll(mDeleteFilesList);
                    updateContentList(mTempFilesList);
                    mFileListAdapter.deleteData(mDeleteFilesList);
                    mTempFilesList.clear();
                    mDeleteFilesList.clear();
                    mRemoveFlag = 1;
                } else {
                    mRemoveFlag++;
                }
                break;
            case AnalysisManager.EXCEPTION_DB_DELETE:
                mTempFilesList.remove(mRemoveFlag - 1);
                break;
            case AnalysisManager.SUCCESS_DB_UPDATE:
                mFileListAdapter.updateData(mCurrentFileItem);
                break;
            default:
                break;
            }
            // 显示接口反馈的信息
            if (status.getCode() != AnalysisManager.SUCCESS_DB_QUERY) {
                showToast(status.getMessage());
            }
        }
    };
    
    /**
     * 目录树回调接口
     */
    DataManagerInterface directoryManager = new DataManagerInterface() {
        @Override
        public void getDataOnResult(ResultStatus status, List<?> list) {
            //dismissProgressDialog();
            switch (status.getCode()) {
            case AnalysisManager.SUCCESS_DB_ADD:
                mTreeViewAdapter.addTreeNode((Document) list.get(0));
                mHandler.sendEmptyMessage(DISMISS_FLOATING_MENU);
                break;
            case AnalysisManager.SUCCESS_DB_DEL:
                mTreeViewAdapter.notifyDataChange();
                break;
            case AnalysisManager.SUCCESS_DB_UPDATE:
                updateDirectoryList(mTreeViewAdapter.getDataList(), mCurrentDirectory);
                updateDirectoryList(mTreeViewAdapter.getShowList(), mCurrentDirectory);
                mTreeViewAdapter.notifyDataChange();
                break;
            default:
                break;
            }
            
            if (status.getCode() != AnalysisManager.SUCCESS_DB_QUERY) {
                showToast(status.getMessage());
            }
        }
    };
    
    /* 如果当前用户个人文档目录为空，默认为用户添加一个<个人文档>目录  */
    private void addDefaultDirectory(int type) {
        Document directory = new Document();
        if (type == GLOBAL.DIR_TYPE_PUBLIC) {
            directory.setName(getResources().getString(R.string.public_document));            
        } else if (type == GLOBAL.DIR_TYPE_PROJECT) {
            directory.setProject_id(ProjectCache.getCurrentProject().getProject_id());
            directory.setName(getResources().getString(R.string.project_document));
        } else if (type == GLOBAL.DIR_TYPE_PERSONAL) {
            directory.setName(getResources().getString(R.string.private_document));
        }
        directory.setDirectory_type(type);
        directory.setUser_id(UserCache.getCurrentUser().getUser_id());
        directory.setTenant_id(UserCache.getCurrentUser().getTenant_id());
        directory.setParents_id(0);
        directory.setLevel(0);
        RemoteDocumentsService.getInstance().addDirectory(directoryManager, directory);
    }
    
    private List<Document> updateDirectoryList(List<Document> datalist, Document directory) {
        for (int i =0; i < datalist.size(); i++) {
            if (datalist.get(i).getDocument_id() == directory.getDocument_id()) {
                datalist.set(i, directory);
                break;
            }
        }
        return datalist;
    }
    
    private void updateTreeListCount(int count) {
        int lastCount = mCurrentDirectory.getFile_count();
        int level = mCurrentDirectory.getLevel() - 1;
        mCurrentDirectory.setFile_count(lastCount + count);
        int mSelectedPosition = mTreeViewAdapter.getSelected().get(0);
        for (int i = mSelectedPosition - 1; i >= 0; i--) {
            lastCount = mTreeViewAdapter.getShowList().get(i).getFile_count();
            if (level == mTreeViewAdapter.getShowList().get(i).getLevel()) {
                mTreeViewAdapter.getShowList().get(i).setFile_count(lastCount + count);
                if (level == 0) {
                    break;
                }
                level--;
            }       
        }
        mTreeViewAdapter.notifyDataChange();
    }
    
    private void updateContentList(List<Files> list) {
        if (list.size() == 0) return;
        for (Files files : list) {
            String dirs = null;
            StringBuilder builder = new StringBuilder();
            String[] dirStrings = files.getDirectory_id().split(",");
            for (int i = 0; i < dirStrings.length; i++) {
                if (!dirStrings[i].equals(String.valueOf(mCurrentDirectory.getDocument_id()))){
                    builder.append(dirStrings[i] + ",");
                }             
            }
            dirs = builder.substring(0, builder.length() - 1);
            if (dirs != null && dirs.length() > 0) {                
                files.setDirectory_id(dirs);
                mFileListAdapter.updateData(files);
                mFileListAdapter.setShowDataList(loadFilesByDirectory(mCurrentDirectory));
            }
        }        
    }
    
    public void openFile() {
        if (mCurrentFileItem == null) return;
        String path = "";
        if (mCurrentType == GLOBAL.DIR_TYPE_ARCHIVE) {
            path = GLOBAL.FILE_SAVE_PATH + "/unarchive_file/"
                    + mCurrentFileItem.getFile_name();
        } else {
            path = GLOBAL.FILE_SAVE_PATH
                    + Utils.calculateDirPath(mTreeViewAdapter.getShowList(),
                            mCurrentDirectory)
                    + mCurrentFileItem.getFile_name();
        }
        try {
            IntentBuilder.viewFile(mActivity, mCurrentFileItem, path);
        } catch (ActivityNotFoundException e) {
            showToast(getString(R.string.application_no_found));
        }
    }
    
    /**
     * 查看文档属性
     */
    public void showDocumentDetails() {
        DocumentPropertiesDialog dialog = new DocumentPropertiesDialog(mActivity, true);
        dialog.setFileItem(mCurrentFileItem);
        dialog.initLayout();
        dialog.show();
    }
    
    public void startDirectorySelectActivity() {
    	startDirectorySelectActivity(R.string.archive);
    }
    
    /**
     * 打开目录选择界面
     */
    public void startDirectorySelectActivity(int selectDirRetRes) {
    	mSelectDirRetRes = selectDirRetRes;
        Intent intent = new Intent(Intent.ACTION_MAIN);
        intent.setClass(mActivity,
                DirectorySelectActivity.class);
        if (mCurrentType == GLOBAL.DIR_TYPE_PROJECT) {
        	intent.putExtra(DirectorySelectActivity.PROJECT_ID_KEY, mCurrentFileItem.getProject_id());
        }
        
        if (mCurrentFileItem.getArchive() == Integer.parseInt(GLOBAL.FILE_ARCHIVE[2][0])) {
            intent.putExtra(DirectorySelectActivity.TEMP_DIRS_KEY, mCurrentFileItem.getDirectory_id());
        }
        startActivityForResult(intent, DIR_SELECT_REQUEST);
    }
    
    /**
     * 打开项目选择界面
     */
    public synchronized void startProjectSelectActivity() {
        if (isFirstStart) {
            isFirstStart = false;
            Intent intent = new Intent(Intent.ACTION_MAIN);
            intent.setClass(mActivity, ProjectSelectActivity.class);
            startActivityForResult(intent, PROJECT_REQUEST_CODE);
        }
    }
    
    /**
     * 设置为 置顶
     */
    public void setToTop() {
        setToTop(mCurrentFileItem);
    }
    
    private void setToTop(final Files files) {
        mUserList.clear();
        mUserList.addAll(UserCache.getUserLists());
        if (files.getDir_type() == Integer.parseInt(GLOBAL.FILE_TYPE[2][0])
                || files.getDir_type() == Integer.parseInt(GLOBAL.FILE_TYPE[14][0])) {
            RemoteDocumentsService.getInstance().topFile(
                    new DataManagerInterface() {

                        @Override
                        public void getDataOnResult(ResultStatus status,
                                List<?> list) {
                            if (status.getCode() == AnalysisManager.SUCCESS_DB_UPDATE) {
                                files.setTop(1);
                                mFileListAdapter.notifyDataSetChanged();
                                showToast(getString(R.string.had_to_top));
                            }
                        }
                    }, files, mUserList);
        }
    }
    
    private boolean mNeedLoadData;
    @SuppressWarnings("unchecked")
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (resultCode == Activity.RESULT_OK) {
            if (requestCode == DIR_SELECT_REQUEST) {
                mUserList.clear();
                String dirs = data.getStringExtra(DirectorySelectActivity.DIR_SELECTED_ID_KEY);
                ArrayList<String> pathLists = (ArrayList<String>) data
                        .getStringArrayListExtra(DirectorySelectActivity.DIR_SELECTED_PATH_KEY);
                // 归档会发送消息， 如果归档到公共文档目录，则给全公司人员发送，如果只归到项目文档目录，则只给所在项目人员发
                boolean hasPersonDir = false;
                boolean hasPublicDir = false;
                boolean hasProjectDir = false;
                mNeedLoadData = false;
                if (pathLists != null && pathLists.size() > 0) {
                    for (String path : pathLists) {
                        if (path.startsWith("/" + GLOBAL.PUBLIC_DOCUMENT)) {
                            hasPublicDir = true;
                        } else if (path.startsWith("/" + GLOBAL.PROJECT_DOCUMENT)) {
                            hasProjectDir = true;
                        } else if (path.startsWith("/" + GLOBAL.PRIVATE_DOCUMENT)) {
                        	hasPersonDir = true;
                        }
                    }
                    if (mCurrentType == GLOBAL.DIR_TYPE_ARCHIVE) {
                    	
                    }
                    switch (mCurrentType) {
                    case GLOBAL.DIR_TYPE_PERSONAL:
                    	if (hasPersonDir) {
                    		mNeedLoadData = true;
                    	}
                    	break;
                    case GLOBAL.DIR_TYPE_PROJECT:
                    	if (hasProjectDir) {
                    		mNeedLoadData = true;
                    	}
                    	break;
                    case GLOBAL.DIR_TYPE_PUBLIC:
                    	if (hasPublicDir) {
                    		mNeedLoadData = true;
                    	}
                    	break;
                    }
                    
                    if (hasProjectDir) {
                    	mProjectIdRet = data.getIntExtra(DirectorySelectActivity.RET_PROJECT_KEY, 0);
                    }
                    
                    if (hasPublicDir) {
                        // 包含了公共文档目录
                        mUserList.addAll(UserCache.getUserLists());
                        prepareArchiving(dirs, mUserList); 
                    } else if (hasProjectDir && !hasPublicDir) {
                        // 只有项目文档目录
                        loadProjectUserAndArchiving(dirs);
                    } else {
                        // 不包含公共文档和项目文档目录，如个人文档，mUserList为空，不会发送消息
                        prepareArchiving(dirs, mUserList); 
                    }
                }
            }
            
            if (requestCode == DOCUMENT_UPLOAD_CODE) {
                //Files file = (Files) data.getSerializableExtra("file");
                ArrayList<Files> dirList = (ArrayList<Files>) data.getSerializableExtra(DocumentUploadActivity.UPLOAD_FILE_LIST_KEY);
                updateTreeListCount(dirList.size());
                for (Files files : dirList) {
                    mFileListAdapter.getDataList().add(0, files);
                    mFileListAdapter.getDataShowList().add(0, files);
                    mFileListAdapter.notifyDataSetChanged();
                }               
            }            
        }
        super.onActivityResult(requestCode, resultCode, data);
    }
    
    /**
     * 获取相应项目的人员之后，再归档， 因为归档需要用到人员列表，便于发消息
     * @param dirs
     */
    private void loadProjectUserAndArchiving(final String dirs) {
        // 项目文档, 获取项目成员
        RemoteUserService.getInstance().getProjectUsers(new DataManagerInterface() {

            @SuppressWarnings("unchecked")
            @Override
            public void getDataOnResult(ResultStatus status,
                    List<?> list) {
                if (status.getCode() == AnalysisManager.SUCCESS_DB_QUERY
                        && list != null && list.size() > 0) {
                    mUserList.addAll((List<User>) list);                     
                }
                prepareArchiving(dirs, mUserList);
            }
        }, ProjectCache.getCurrentProject());
    }
    
    public static boolean contain(String[] arr, String targetValue) {
        for (String s : arr) {
            if (s.equals(targetValue))
                return true;
        }
        return false;
    }
    
    private void prepareArchiving(String dirs, List<User> userlist) {
        if (mCurrentMode == Mode.Pick) {
            // 多选模式归档，也就是同时归档多的文件
            for (Files files : mTempFilesList) {
                files.setDirectory_id(dirs);
                if (mProjectIdRet != 0) {
        			files.setProject_id(mProjectIdRet);
        		}
                doArchiving(files, userlist);
            }
            cancelActionMode();
        } else {
            // 单个文件归档
        	if (mSelectDirRetRes == R.string.copy) {
        		Files files = MiscUtils.clone(mCurrentFileItem);
        		dirs += "," + files.getDirectory_id();
        		files.setDirectory_id(dirs);
        		if (mProjectIdRet != 0) {
        			files.setProject_id(mProjectIdRet);
        		}
        		
        		doArchiving(files, userlist);
        	} else {
        		mCurrentFileItem.setDirectory_id(dirs);
        		doArchiving(mCurrentFileItem, userlist);
        	}
        }
    }
    
    /**
     * 归档
     * 
     * @param files
     * @param userlist， 列表为空则不发消息
     */
    private void doArchiving(final Files files, List<User> userlist) {
        files.setArchive(Integer.parseInt(GLOBAL.FILE_ARCHIVE[1][0]));
        RemoteDocumentsService.getInstance().archiveFile(new DataManagerInterface() {

            @Override
            public void getDataOnResult(ResultStatus status, List<?> list) {
                String msg = "";
                if (status.getCode() == AnalysisManager.SUCCESS_DB_UPDATE) {
                    msg = getString(mSelectDirRetRes) + getString(R.string.succ);
                    if (R.string.copy != mSelectDirRetRes) {
                    	mFileListAdapter.deleteData(files);
                    }
                    if (mNeedLoadData) {
                    	loadData();
                    }
                    
                } else {
                    msg = getString(mSelectDirRetRes) + getString(R.string.failed);
                }
                Toast.makeText(mActivity, msg, Toast.LENGTH_SHORT).show();
            }
            
        }, files, userlist);
    }
    
    /**
     * 初始化目录树的PopupWindow
     */
    private void createTreePopup() {
        String[] subMenuNames1 = new String[] {
                getString(R.string.add),
                getString(R.string.modify),
                getString(R.string.delete),
        };
        mTreeOptionsMenuView = new OptionsMenuView(mActivity, subMenuNames1);
        mTreeOptionsMenuView
                .setSubMenuListener(new OptionsMenuView.SubMenuListener() {
                    @Override
                    public void onSubMenuClick(View view) {
                        if (RemoteDocumentsService.getInstance().equals(GLOBAL.PROJECT_DOCUMENT)
                                && !PermissionCache.hasSysPermission(PROJECT_DOC_EDIT)) {
                            showToast(getResources().getString(R.string.no_edit_permission));
                            mTreeOptionsMenuView.dismiss();
                            return;
                        }
                        if (mCurrentType == GLOBAL.DIR_TYPE_PUBLIC
                                && !PermissionCache.hasSysPermission(PUBLIC_DOC_EDIT)) {
                            showToast(getResources().getString(R.string.no_edit_permission));
                            mTreeOptionsMenuView.dismiss();
                            return;
                        }

                        switch ((Integer) view.getTag()) {
                        case 0: // 添加
                            if (mCurrentDirectory.getFile_count() != 0
                                    && !mCurrentDirectory.isHas_child()) {
                                showToast(getResources().getString(R.string.file_exist_cannot_create));
                            } else {
                                mFlag = operation.ADD_CHILD;
                                createAddModifyDialog(getResources().getString(R.string.add_directory_title),
                                        DirectoryModifyDialog.Operation.ADD);                            
                            }                            
                            break;
                        case 1: // 修改                            
                            mFlag = operation.MODIFY;
                            createAddModifyDialog(getResources().getString(R.string.modify_directory_title), 
                                    DirectoryModifyDialog.Operation.MODIFY);
                            break;
                        case 2: // 删除
                            if (mCurrentDirectory.getLevel() == 0) { // 根目录不能删除
                                showToast(getResources().getString(R.string.cannot_delete_root_dir));
                                return;
                            }
                            
                            if (mCurrentDirectory.getFile_count() == 0) {
                                mChildrenDirectoryList.clear();
                                mChildrenDirectoryList.add(mCurrentDirectory);                                
                                if (mCurrentDirectory.isHas_child()) {
                                    findoutAllChildrens(mCurrentDirectory);
                                }
                                
                                List<Document> childrens = getChildrenDirectoryList();
                                for (int i = 0; i < childrens.size(); i++) {
                                    RemoteDocumentsService.getInstance().deleteDirectory(
                                            directoryManager, childrens.get(i));
                                }
                                
                                mTreeViewAdapter.getShowList().remove(mCurrentDirectory);
                                mTreeViewAdapter.getDataList().removeAll(mChildrenDirectoryList);
                            } else {
                                showToast(getResources().getString(R.string.delete_file_first));
                            }
                            break;
                        }
                        mTreeOptionsMenuView.dismiss();
                    }
                });
    }
    
    /**
     * 创建目录对话框
     */
    protected void updateDirectory(String dirName) {
        Document directory = new Document();
        if (mCurrentType == GLOBAL.DIR_TYPE_PERSONAL) {
            directory.setUser_id(UserCache.getCurrentUser().getUser_id());                    
        } else if (mCurrentType == GLOBAL.DIR_TYPE_PROJECT) {
            directory.setProject_id(mCurrentDirectory.getProject_id());
        }
        directory.setTenant_id(UserCache.getCurrentUser().getTenant_id());

        switch (mFlag) {
        case ADD_ROOT:
            directory.setName(dirName);
            directory.setParents_id(0);
            directory.setLevel(0);
            RemoteDocumentsService.getInstance().addDirectory(directoryManager, directory);
            break;
        case ADD_CHILD:
            directory.setName(dirName);
            directory.setParents_id(mCurrentDirectory.getDocument_id());
            directory.setDirectory_type(mCurrentDirectory.getDirectory_type());
            RemoteDocumentsService.getInstance().addDirectory(directoryManager, directory);
            break;
        case MODIFY:
            mCurrentDirectory.setName(dirName);
            RemoteDocumentsService.getInstance().updateDirectory(directoryManager, mCurrentDirectory);
            break;
        default:
            break;
        }
    }
    
    private void createAddModifyDialog(String title, DirectoryModifyDialog.Operation operation) {
        final DirectoryModifyDialog dialog = new DirectoryModifyDialog(
                mActivity, title, operation, mCurrentDirectory);
        View.OnClickListener listener = new View.OnClickListener() {            
            @Override
            public void onClick(View view) {
                switch (view.getId()) {
                case R.id.btn_save:
                    String dirName = dialog.getDirName();
                    updateDirectory(dirName);
                    break;
                }
                dialog.dismiss();
            }
        };
        dialog.getSaveButton().setOnClickListener(listener);
        dialog.getCloseButton().setOnClickListener(listener);
        dialog.show();
    }
    
    private void findoutAllChildrens(Document directory) {
        for (int i = 0; i < mTreeViewAdapter.getDataList().size(); i++) {
            if (directory.getDocument_id() == mTreeViewAdapter.getDataList().get(i).getParents_id()) {
                mChildrenDirectoryList.add(mTreeViewAdapter.getDataList().get(i));
                if (mTreeViewAdapter.getDataList().get(i).isHas_child()) {
                    findoutAllChildrens(mTreeViewAdapter.getDataList().get(i));
                } else {
                    break;
                }                
            }
        }
    }
    
    private List<Document> getChildrenDirectoryList() {
        return mChildrenDirectoryList;
    }

    @Override
    public View getSlidePane() {
        return mActivity.getSlidingPaneLayout();
    }
}
