package com.pm360.cepm360.app.module.home;

import android.content.Context;
import android.os.Handler;
import android.os.Message;

import com.pm360.cepm360.R;
import com.pm360.cepm360.app.CepmApplication;
import com.pm360.cepm360.app.cache.UserCache;
import com.pm360.cepm360.app.common.activity.DataManagerInterface;
import com.pm360.cepm360.app.module.home.portal.PortailListItem;
import com.pm360.cepm360.common.GLOBAL;
import com.pm360.cepm360.common.ResultStatus;
import com.pm360.cepm360.common.json.AnalysisManager;
import com.pm360.cepm360.entity.Announcement;
import com.pm360.cepm360.entity.Files;
import com.pm360.cepm360.entity.Index_daiban;
import com.pm360.cepm360.entity.Index_feedback;
import com.pm360.cepm360.entity.Index_task;
import com.pm360.cepm360.entity.User;
import com.pm360.cepm360.services.document.RemoteDocumentsService;
import com.pm360.cepm360.services.query.RemoteIndexService;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

/**
 * 
 * 描述: 用于加载主页门户各个模块的数据
 * 
 * 	1. 通过loadAllData() 或  loadData(int index) 方法去加载数据
 * 	2. 设置setOnDataChangedListener监听数据加载完成后的响应
 * 	3. 通过getPortalList(int index)获取格式化好的数据
 *
 */
public class PortalDataLoader {
    
    private Context mContext;
    private User mUser;
    private OnDataChangedListener mListener;
    
    private ArrayList<Index_daiban> mTodoTaskLists = new ArrayList<Index_daiban>();
    private ArrayList<Index_task> mMyTaskLists = new ArrayList<Index_task>();
    private ArrayList<Files> mProgressLists = new ArrayList<Files>();
    private ArrayList<Index_feedback> mLastFeedbackLists = new ArrayList<Index_feedback>();
    private ArrayList<Announcement> mAnnouncementLists = new ArrayList<Announcement>();
    private ArrayList<Files> mLastDocumentLists = new ArrayList<Files>();
    
    private ArrayList<String> mFilePaths = new ArrayList<String>();
    
    private static final int FLAG_TODO_TASK_DATA = 0;     // 代办事项
    private static final int FLAG_MY_TASK_DATA = 1;       // 我的任务
    private static final int FLAG_PROGRESS_DATA = 2;      // 形象进展    [现场图片，形象成果]
    private static final int FLAG_LAST_FEEDBACK_DATA = 3; // 最新反馈
    private static final int FLAG_ANNOUNCEMENT_DATA = 4;  // 公司通告
    private static final int FLAG_LAST_DOC_DATA = 5;      // 最新文档
    
    private static final int MSG_FILE_DOWNLOADED = 100;
    
    private static final String PROGRESS_TEMPLATE = GLOBAL.FILE_SAVE_PATH + "/Portal/progress/";
    
    public static interface OnDataChangedListener {
        public void onDataChanged(int flag);
    };
    
    public void setOnDataChangedListener(OnDataChangedListener listener) {
        mListener = listener;
    }
    
    private Handler mHandler = new Handler(new Handler.Callback() {

        @Override
        public boolean handleMessage(Message msg) {
            if (msg.what == MSG_FILE_DOWNLOADED) {
                mFilePaths.clear();
                for (Files files : mProgressLists) {
                    String path = PROGRESS_TEMPLATE + files.getFile_name();
                    mFilePaths.add(path);
                }
                mListener.onDataChanged(FLAG_PROGRESS_DATA);
            } else if (msg.what == FLAG_PROGRESS_DATA) {                
                downloadFiles(mProgressLists);
            } else {
                mListener.onDataChanged(msg.what);
            }
            return false;
        }
    });
    
    
    public PortalDataLoader(Context context) {
        mContext = context;
        mUser = UserCache.getCurrentUser();
    }
    
    /**
     * 获取所有数据
     */
    public void loadAllData() {       
        loadTodoList();
        loadMyTaskList();
        loadProgressList();
        loadFeedbackList();
        loadPublicAnnouncement();
        loadLastDocument();
    }
    
    /**
     * 获取某一项数据
     */
    public void loadData(int index) {
        if (index == 0) {
            loadTodoList();
        } else if (index == 1) {
            loadMyTaskList();
        } else if (index == 2) {
            loadProgressList();
        } else if (index == 3) {
            loadFeedbackList();
        } else if (index == 4) {
            loadPublicAnnouncement();
        } else if (index == 5) {
            loadLastDocument();
        } else {
            loadAllData();
        }
    }
    
    /**
     * 获取某一项中的某一行数据
     * 
     * @param index
     * @param position
     * @return
     */
    public Object getPortalItem(int index, int position) {
        if (index == 0) {
            return mTodoTaskLists.get(position);
        } else if (index == 1) {
            return mMyTaskLists.get(position);
        } else if (index == 2) {
            return null;
        } else if (index == 3) {
            return mLastFeedbackLists.get(position);
        } else if (index == 4) {
            return mAnnouncementLists.get(position);
        } else if (index == 5) {
            return mLastDocumentLists.get(position);
        } else {
            return null;
        }
    }
    
    /**
     * 形象进展 ：获取图片下载后的存放路径列表
     * 
     * @return
     */
    public ArrayList<String> getFilePaths() {
        return mFilePaths;
    }
    
    /**
     * 数据加载完成后，通过此方法来获取数据，数据经过组装填充到PortailListItem对象中
     * 
     * @param index 小模块的唯一编号
     * @return PortailListItem类型的列表
     */
    public ArrayList<PortailListItem> getPortalList(int index) {
        ArrayList<PortailListItem> portalDataLists = new ArrayList<PortailListItem>();
        if (index == FLAG_TODO_TASK_DATA) {
            for (int i = 0; i < mTodoTaskLists.size(); i++) {
                if (mTodoTaskLists.get(i).getIs_process() == 0) {
                    PortailListItem listItem = new PortailListItem();
                	listItem.setMessage_id(mTodoTaskLists.get(i).getMessage_id());
                    listItem.setTitle(mTodoTaskLists.get(i).getTitle());
                    listItem.setType(mTodoTaskLists.get(i).getMessage_type_key());
                    listItem.setType_id(mTodoTaskLists.get(i).getType_id());
                    listItem.setDate(mTodoTaskLists.get(i).getTime());
                    portalDataLists.add(listItem);
                }
                
            }
        } else if (index == FLAG_MY_TASK_DATA) {
            for (int i = 0; i < mMyTaskLists.size(); i++) {
                PortailListItem listItem = new PortailListItem();
                if (mMyTaskLists.get(i).getProgress() == 0) {
                    listItem.setTitle(GLOBAL.TASK_STATUS_TYPE[0][1]); // 未开始
                } else if (mMyTaskLists.get(i).getProgress() == 1) {
                    listItem.setTitle(GLOBAL.TASK_STATUS_TYPE[1][1]); // 进行中
                } else if (mMyTaskLists.get(i).getProgress() == 2) {
                    listItem.setTitle(mContext.getString(R.string.finished)); //GLOBAL.TASK_STATUS_TYPE[2][1]
                    listItem.setType(1);
                }                    
                listItem.setTag(String.valueOf(mMyTaskLists.get(i).getNumber()));
                portalDataLists.add(0, listItem);
            }

        } else if (index == FLAG_LAST_FEEDBACK_DATA) {
            for (int i = 0; i < mLastFeedbackLists.size(); i++) {
                PortailListItem listItem = new PortailListItem();
                listItem.setTitle(mLastFeedbackLists.get(i).getTitle());
                listItem.setType(mLastFeedbackLists.get(i).getMessage_type_key());
                listItem.setDate(mLastFeedbackLists.get(i).getTime());
                portalDataLists.add(listItem);
            }
        } else if (index == FLAG_ANNOUNCEMENT_DATA) {
            for (int i = 0; i < mAnnouncementLists.size(); i++) {
                PortailListItem listItem = new PortailListItem();
                listItem.setTitle(mAnnouncementLists.get(i).getTitle());
                listItem.setDate(mAnnouncementLists.get(i).getPublish_time());
                portalDataLists.add(listItem);
            }
        } else if (index == FLAG_LAST_DOC_DATA) {
            for (int i = 0; i < mLastDocumentLists.size(); i++) {
                PortailListItem listItem = new PortailListItem();
                listItem.setTitle(mLastDocumentLists.get(i).getTitle());
                listItem.setDate(mLastDocumentLists.get(i).getCreate_time());
                portalDataLists.add(listItem);
            }            
        }
        
        return portalDataLists;
    }
    
    public void loadTodoList() {        
        RemoteIndexService.getInstance().getToDoListByLimit(new DataManagerInterface() {

            @SuppressWarnings("unchecked")
            @Override
            public void getDataOnResult(ResultStatus status, List<?> list) {
                mTodoTaskLists.clear();
                if (status.getCode() == AnalysisManager.SUCCESS_DB_QUERY) {
                    if (list != null && list.size() > 0) {
                        mTodoTaskLists.addAll((ArrayList<Index_daiban>) list);
                    }
                }
                
                Message msg = Message.obtain();
                msg.what = FLAG_TODO_TASK_DATA;
                mHandler.sendEmptyMessage(FLAG_TODO_TASK_DATA);
            }
            
        }, mUser.getTenant_id(), mUser.getUser_id());
    }
    
    public void loadMyTaskList() {        
        int type = ((CepmApplication) mContext.getApplicationContext()).getEnterpriseType();
        RemoteIndexService.getInstance().getMyTaskList(new DataManagerInterface() {

            @SuppressWarnings("unchecked")
            @Override
            public void getDataOnResult(ResultStatus status, List<?> list) {
                mMyTaskLists.clear();
                if (status.getCode() == AnalysisManager.SUCCESS_DB_QUERY) {
                    if (list != null && list.size() > 0) {
                        mMyTaskLists.addAll((ArrayList<Index_task>) list);
                    }
                }

                mHandler.sendEmptyMessage(FLAG_MY_TASK_DATA);
            }
            
        }, mUser.getTenant_id(), mUser.getUser_id(), String.valueOf(type + 1));
    }
    
    public List<Index_task> getMyTaskList() {
    	return mMyTaskLists;
    }
    
    public void loadProgressList() {
        RemoteIndexService.getInstance().getProjectPICListByLimit(new DataManagerInterface() {

            @SuppressWarnings("unchecked")
            @Override
            public void getDataOnResult(ResultStatus status, List<?> list) {
                mFilePaths.clear();
                mProgressLists.clear();
                if (status.getCode() == AnalysisManager.SUCCESS_DB_QUERY) {
                    if (list != null && list.size() > 0) {
                        mProgressLists.addAll((ArrayList<Files>) list);
                    }
                }
                mHandler.sendEmptyMessage(FLAG_PROGRESS_DATA);
            }
            
        }, mUser.getTenant_id());
    }

    public void loadFeedbackList() {        
        RemoteIndexService.getInstance().getFeedbackListByLimit(new DataManagerInterface() {

            @SuppressWarnings("unchecked")
            @Override
            public void getDataOnResult(ResultStatus status, List<?> list) {
                mLastFeedbackLists.clear();
                if (status.getCode() == AnalysisManager.SUCCESS_DB_QUERY) {
                    if (list != null && list.size() > 0) {
                        mLastFeedbackLists.addAll((ArrayList<Index_feedback>) list);
                    }
                }
                mHandler.sendEmptyMessage(FLAG_LAST_FEEDBACK_DATA);
            }
            
        }, mUser.getTenant_id(), mUser.getUser_id());
    }
    
    public void loadPublicAnnouncement() {        
        RemoteIndexService.getInstance().getAnnouncementListByLimit(new DataManagerInterface() {

            @SuppressWarnings("unchecked")
            @Override
            public void getDataOnResult(ResultStatus status, List<?> list) {
                mAnnouncementLists.clear();
                if (status.getCode() == AnalysisManager.SUCCESS_DB_QUERY) {
                    if (list != null && list.size() > 0) {
                        mAnnouncementLists.addAll((ArrayList<Announcement>) list);
                    }
                }
                mHandler.sendEmptyMessage(FLAG_ANNOUNCEMENT_DATA);
            }
            
        }, mUser.getTenant_id());
    }
    
    public void loadLastDocument() {        
        RemoteIndexService.getInstance().getDocumentListByLimit(new DataManagerInterface() {

            @SuppressWarnings("unchecked")
            @Override
            public void getDataOnResult(ResultStatus status, List<?> list) {
                mLastDocumentLists.clear();
                if (status.getCode() == AnalysisManager.SUCCESS_DB_QUERY) {
                    if (list != null && list.size() > 0) {
                        mLastDocumentLists.addAll((ArrayList<Files>) list);
                    }
                }
                mHandler.sendEmptyMessage(FLAG_LAST_DOC_DATA);
            }
            
        }, mUser.getTenant_id(), mUser.getUser_id());
    }
    
    /**
     * 下载进展图片，默认服务器返回 4 张图片
     * 
     * @param progressLists
     */
    public void downloadFiles(ArrayList<Files> progressLists) {
        mFilePaths.clear();
        for (Files files : progressLists) {
            //final int progressSize = progressLists.size();
            final String filePath = PROGRESS_TEMPLATE + files.getFile_name();
            File file = new File(filePath);
            if (file.exists()) {
                // 不用下载
                mFilePaths.add(filePath);
                //if (mFilePaths.size() == progressSize) {
                    Message msg = Message.obtain();
                    msg.what = MSG_FILE_DOWNLOADED;
                    msg.obj = filePath;                        
                    mHandler.sendMessage(msg); 
                //}
            } else {
                files.setTenant_id(UserCache.getCurrentUser().getTenant_id());
                RemoteDocumentsService.getInstance().downloadFile(new DataManagerInterface() {
                    @Override
                    public void getDataOnResult(ResultStatus status, List<?> list) {
                        if (status.getCode() == AnalysisManager.SUCCESS_DOWNLOAD) {
                            mFilePaths.add(0, filePath);
                           // if (mFilePaths.size() == progressSize) {
                                Message msg = Message.obtain();
                                msg.what = MSG_FILE_DOWNLOADED;
                                msg.obj = filePath;
                                mHandler.sendMessage(msg);
                            //}
                        }
                    }
                }, null, files, file);
            }            
        }
    }
    
}
