package com.pm360.cepm360.app.module.document;

import java.util.ArrayList;
import java.util.List;

import android.app.ProgressDialog;
import android.content.Context;
import android.widget.Toast;

import com.pm360.cepm360.R;
import com.pm360.cepm360.app.cache.EpsCache;
import com.pm360.cepm360.app.cache.ProjectCache;
import com.pm360.cepm360.app.cache.UserCache;
import com.pm360.cepm360.app.common.activity.DataManagerInterface;
import com.pm360.cepm360.app.utils.UtilTools;
import com.pm360.cepm360.common.GLOBAL;
import com.pm360.cepm360.common.ResultStatus;
import com.pm360.cepm360.common.json.AnalysisManager;
import com.pm360.cepm360.entity.Document;
import com.pm360.cepm360.entity.EPS;
import com.pm360.cepm360.entity.Files;
import com.pm360.cepm360.entity.Project;
import com.pm360.cepm360.services.document.RemoteDocumentsService;

/**
 * DataOperationHelper 文档数据获取类
 * loadData(int type) 获取数据， getFileList/getDirectoryList(文件/目录)
 * formatTreeList() 格式化目录， EPS和Project组合一个新的对象
 * 获取到数据之后通过onFilesDataLoaded、onDirectoryDataLoaded 将数据返回给调用者
 *
 */
public class DataOperationHelper {
    
    private Context mContext;
    private ProgressDialog mProgressDialog;
    private OnDataChangedListener mListener;
    
    public static interface OnDataChangedListener {
        public void onFilesDataLoaded(List<Files> fileLists);
        public void onDirectoryDataLoaded(List<Document> directoryLists);
    };
    
    public void setOnDataChangedListener(OnDataChangedListener listener) {
        mListener = listener;
    }
    
    public DataOperationHelper(Context context) {
        mContext = context;
    }
    
    /**
     * @param item
     *            文档类型 根据不同的文档类型，请求相应的数据， 更新界面 公共文档 、最新文档、项目文档、项目分类查询
     *            等界面都是用这个框架显示。
     */
    public void loadData(int type) {
        Document directory = new Document();
        Project project = ProjectCache.getCurrentProject();
        switch (type) {
        case 0: //公共文档
            directory.setDirectory_type(GLOBAL.DIR_TYPE_PUBLIC);
            directory.setTenant_id(UserCache.getCurrentUser().getTenant_id());
            connectingService(directory);
            break;
        case 1: //项目文档
            directory.setDirectory_type(GLOBAL.DIR_TYPE_PROJECT);
            directory.setTenant_id(UserCache.getCurrentUser().getTenant_id());
            if (ProjectCache.getCurrentProject() != null) {
                directory.setProject_id(project.getProject_id());
                connectingService(directory);
            }
            break;
        case 2: //个人文档
            directory.setDirectory_type(GLOBAL.DIR_TYPE_PERSONAL);
            directory.setUser_id(UserCache.getCurrentUser().getUser_id());
            directory.setTenant_id(UserCache.getCurrentUser().getTenant_id());
            connectingService(directory);
            break;
        case 3:
            showProgressDialog("Loading data...");
            RemoteDocumentsService.getInstance().getUnArchiveFiles(fileManager,
                    UserCache.getCurrentUser().getTenant_id(),
                    0);
            break;
        default:
            break;
        }
    }
    
    private void formatTreeList() {
        List<Document> treeList = new ArrayList<Document>();
        ArrayList<EPS> epsList = new ArrayList<EPS>();
        ArrayList<Project> projectlist = new ArrayList<Project>();
        epsList.addAll(EpsCache.getEpsLists());
        projectlist.addAll(ProjectCache.getProjectLists());
        
        for (int i = 0; i < epsList.size(); i++) {
            Document cell = new Document();
            cell.setExpanded(false);
            cell.setHas_child(epsList.get(i).isHas_child());
            cell.setLevel(epsList.get(i).getLevel());
            cell.setName(epsList.get(i).getName());
            //cell.setEPS(true);
            //cell.setEPSId(epsList.get(i).getEps_id());
            cell.setProject_id(-1);
            cell.setDocument_id(epsList.get(i).getEps_id());
            cell.setParents_id(epsList.get(i).getParents_id());
            treeList.add(cell);
        }

        for (int i = 0; i < projectlist.size(); i++) {
            Document cell = new Document();
            cell.setExpanded(false);
            cell.setHas_child(false);
            //cell.setEPS(false);
            //cell.setEPSId(-1);
            cell.setDirectory_type(-1); // epsId
            cell.setProject_id(projectlist.get(i).getProject_id());
            cell.setName(projectlist.get(i).getName());
            cell.setParents_id(projectlist.get(i).getEps_id());
            cell.setLevel(-1);
            cell.setTenant_id(projectlist.get(i).getTenant_id());
            treeList.add(cell);

            int epsid = projectlist.get(i).getEps_id();
            for (int j = 0; j < treeList.size(); j++) {
                if (epsid == treeList.get(i).getDirectory_type()) {
                    treeList.get(i).setHas_child(true);
                    break;
                }
            }
        }
        
        Document cell = new Document();
        cell.setExpanded(false);
        cell.setHas_child(false);
        cell.setProject_id(0);
        cell.setName(mContext.getString(R.string.other));
        cell.setParents_id(0);
        cell.setLevel(0);
        treeList.add(cell);
        
        mListener.onDirectoryDataLoaded(treeList);
    }
    
    private DataManagerInterface fileManager = new DataManagerInterface() {

        @SuppressWarnings("unchecked")
        @Override
        public void getDataOnResult(ResultStatus status, List<?> list) {
            dismissProgressDialog();
            if (status.getCode() == AnalysisManager.SUCCESS_DB_QUERY) {
                mListener.onFilesDataLoaded((List<Files>) list);
                formatTreeList();
            } else {
                Toast.makeText(mContext, status.getMessage(), Toast.LENGTH_SHORT).show();
            }            
        }        
    };
    
    /**
     * @param directory
     *            调用远程接口请求数据
     */
    private void connectingService(Document directory) {
        showProgressDialog("Loading data...");
        RemoteDocumentsService.getInstance().getFileList(fileManager, directory);        
        RemoteDocumentsService.getInstance().getDirectoryList(new DataManagerInterface() {

            @SuppressWarnings("unchecked")
            @Override
            public void getDataOnResult(ResultStatus status, List<?> list) {
                dismissProgressDialog();
                if (status.getCode() == AnalysisManager.SUCCESS_DB_QUERY) {
                    mListener.onDirectoryDataLoaded((List<Document>) list);
                } else {
                    Toast.makeText(mContext, status.getMessage(), Toast.LENGTH_SHORT).show();
                }                
            }
            
        }, directory);
    }

    private void showProgressDialog(String text) {
        dismissProgressDialog();
        mProgressDialog = UtilTools.showProgressDialog(mContext, true, false);
    }

    private void dismissProgressDialog() {
        if (mProgressDialog != null && mProgressDialog.isShowing())
            mProgressDialog.dismiss();
    }
    
}
