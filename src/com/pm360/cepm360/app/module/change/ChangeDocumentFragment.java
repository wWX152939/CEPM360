
package com.pm360.cepm360.app.module.change;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.ActivityNotFoundException;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.Toast;

import com.pm360.cepm360.R;
import com.pm360.cepm360.app.cache.PermissionCache;
import com.pm360.cepm360.app.cache.ProjectCache;
import com.pm360.cepm360.app.cache.UserCache;
import com.pm360.cepm360.app.common.activity.ActionBarFragmentActivity.SearchListener;
import com.pm360.cepm360.app.common.activity.DataManagerInterface;
import com.pm360.cepm360.app.common.activity.ListSelectActivity;
import com.pm360.cepm360.app.common.fileexplorer.Util;
import com.pm360.cepm360.app.common.view.FloatingMenuView;
import com.pm360.cepm360.app.common.view.OptionsMenuView;
import com.pm360.cepm360.app.common.view.parent.BaseSearchView;
import com.pm360.cepm360.app.module.project.table.TableAdapter;
import com.pm360.cepm360.app.module.project.table.TextCell;
import com.pm360.cepm360.app.module.template.DocTemplateFragment;
import com.pm360.cepm360.app.utils.IntentBuilder;
import com.pm360.cepm360.app.utils.UtilTools;
import com.pm360.cepm360.common.GLOBAL;
import com.pm360.cepm360.common.ResultStatus;
import com.pm360.cepm360.common.json.AnalysisManager;
import com.pm360.cepm360.common.util.FileUtils;
import com.pm360.cepm360.entity.Files;
import com.pm360.cepm360.entity.P_GCQZ;
import com.pm360.cepm360.entity.Project;
import com.pm360.cepm360.entity.User;
import com.pm360.cepm360.services.change.RemoteChangeService;
import com.pm360.cepm360.services.common.RemoteCommonService;
import com.pm360.cepm360.services.document.RemoteDocumentsService;
import com.pm360.cepm360.services.system.RemoteUserService;

import org.apache.poi.hwpf.HWPFDocument;
import org.apache.poi.hwpf.usermodel.Paragraph;
import org.apache.poi.hwpf.usermodel.Range;
import org.apache.poi.hwpf.usermodel.Table;
import org.apache.poi.hwpf.usermodel.TableCell;
import org.apache.poi.hwpf.usermodel.TableIterator;
import org.apache.poi.hwpf.usermodel.TableRow;

import java.io.File;
import java.io.FileInputStream;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

/**
 * 添加一条签证单的流程：
 * 	1. 点击添加菜单，选择模块， 下载模板文档
 * 	2. 将模板文件重命名，已签证单号命名
 * 	3. 记录当前文档的修改时间，然后通过第三方软件打开文档，编辑，保存之后
 * 	4. 返回ChangeActivity时，onResume() 通过判断比较文档的修改时间来确认文档有没有被修改过
 * 	5. 如被修改过，判断getCode()是否为null, 如果为null则表示为添加， 如果不为null，表示为修改
 * 
 * 查看附件
 *  目前是先下载后附件后，才通过ChangeAttachmentActivity去显示这些附件图片。
 *  但是，如果把下载的动作移到ChangeAttachmentActivity去完成也是不错的做法。
 *
 */
public class ChangeDocumentFragment extends Fragment {
    private ChangeActivity mActivity;
    private View mRootView;
    private BaseSearchView mConditionView;
    private TableAdapter mTabAdapter;
    private FloatingMenuView mMenu;
    private OptionsMenuView mOptionsMenuView;
    private boolean mHasViewPermission, mHasEditPermission;

    private Project mProject;
    private P_GCQZ mCurrentChange;
    private Files mCurrentFiles;
    private File mEditFile;
    private String mCurrentCode;
    private Date mLastModified;
    private ProgressDialog mProgressDialog;
    private ArrayList<P_GCQZ> mList = new ArrayList<P_GCQZ>();
    private ArrayList<User> mUserList = new ArrayList<User>();
    private ArrayList<String> mAttachmentThumbs = new ArrayList<String>();
    private ArrayList<Files> mAttachmentFiles = new ArrayList<Files>();
    private int mAttachmentCount;
    private Files mTemplateFiles;

    private static final String CHANGE_EDIT_PERMISSIONS = "12_1";
    private static final String CHANGE_VIEW_PERMISSIONS = "12_2";
    //private static final String CHANGE_TEMPLATE = GLOBAL.FILE_SAVE_PATH + "/qzmb1.doc";
    private static final String CHANGE_DIR = GLOBAL.FILE_SAVE_PATH + "/ChangeDocument/";
    private static final String CAMERA_DOWNLOAD_DIR = CHANGE_DIR + "download/";
    private static final String CAMERA_UPLOAD_DIR = CHANGE_DIR + "upload/";
    
    private static final int ATTACHMENT_REQUEST_CODE = 100;
    private static final int SELECT_TEMPLATE_CODE = 200;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
            Bundle savedInstanceState) {
        if (mRootView == null) {
            mActivity = (ChangeActivity) getActivity();
            mRootView = inflater.inflate(
                    R.layout.change_document_fragment, container, false);

            View tableView = mRootView.findViewById(R.id.change_document_list);
            mTabAdapter = new TableAdapter(container.getContext(), tableView,
                    R.array.change_table_names,
                    R.array.change_table_widths, Color.WHITE, Color.WHITE, getResources().getColor(
                            R.color.table_line), false, false, false);
            mMenu = mTabAdapter.getFloatingMenuView();
            mMenu.addPopItem(getString(R.string.add), R.drawable.menu_icon_add);
            mMenu.setPopOnItemClickListener(new AdapterView.OnItemClickListener() {
                @Override
                public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                    switch (position) {
                        case 0:
                            //addNewChange();
                            
                            Intent intent = new Intent(mActivity, ListSelectActivity.class);
                            Bundle bundle = new Bundle();
                            bundle.putSerializable(ListSelectActivity.FRAGMENT_KEY,
                                    DocTemplateFragment.class);
                            bundle.putBoolean(ListSelectActivity.SELECT_MODE_KEY, 
                                    ListSelectActivity.SINGLE_SELECT);
                            intent.putExtras(bundle);
                            startActivityForResult(intent, SELECT_TEMPLATE_CODE);
                            break;
                    }
                    mMenu.dismiss();
                }
            });

            ListView left_list = (ListView) tableView
                    .findViewById(R.id.left_container_listview);
            ListView right_list = (ListView) tableView
                    .findViewById(R.id.right_container_listview);
            AdapterView.OnItemClickListener itemClickListener = new AdapterView.OnItemClickListener() {

                @Override
                public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                    mCurrentChange = findChangeById((int) mTabAdapter.getItemId(position));
                    mTabAdapter.setSelectedPosition(position);
                    int[] location = new int[2];
                    view.getLocationOnScreen(location);
                    mOptionsMenuView.showAtLocation(view, Gravity.NO_GRAVITY,
                            UtilTools.dp2pxW(view.getContext(), 480),
                            location[1] - UtilTools.dp2pxH(view.getContext(), 28));
                }
            };
            left_list.setOnItemClickListener(itemClickListener);
            right_list.setOnItemClickListener(itemClickListener);
            
            mActivity.enableSearchView(true);
            initSearchConditionView(mActivity.getSearchConditionLayout());
        }

        ViewGroup parent = (ViewGroup) mRootView.getParent();
        if (parent != null) {
            parent.removeView(mRootView);
        }

        return mRootView;
    }
    
    private void initSearchConditionView(LinearLayout conditionLayout) {
        mConditionView = new BaseSearchView(mActivity);
        if (mConditionView != null) {
            conditionLayout.removeView(mConditionView.getView());
        }
        mConditionView.init(R.array.change_table_names, null, null, null);
        conditionLayout.addView(mConditionView.getView());
        
        mActivity.setSearchListener(new SearchListener() {
            @Override
            public void doSearch() {
                mConditionView.SaveData();
            }

            @Override
            public void doReset() {
                mConditionView.SetDefaultValue(null);
            }
        });
    }

    @Override
    public void onResume() {
        super.onResume();
        
        Project project = ProjectCache.getCurrentProject();
        if (!project.equals(mProject)) {
            mProject = project;
            loadUserData();
            loadChangeData();
        }

        mHasViewPermission = PermissionCache
                .hasSysPermission(CHANGE_VIEW_PERMISSIONS);
        mHasEditPermission = PermissionCache
                .hasSysPermission(CHANGE_EDIT_PERMISSIONS);
        mMenu.setVisibility(mHasEditPermission ? View.VISIBLE : View.INVISIBLE);
        initOptionsMenuView();
        
        if (mCurrentChange != null && checkModified(mEditFile)) {
            String path = mEditFile.getPath();
            String name = readWord(path, 0, 1);
            mCurrentChange.setName(name);

            if (mCurrentChange.getCode() == null) {
                mCurrentChange.setCode(mCurrentCode);
                addChange(mCurrentChange);
            } else {
                showUpdateDocumentDialog();
            }
        }
    }
    
    @Override
    public void onStop() {
        super.onStop();
        mActivity.getSearchDialog().dismiss();
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        FileUtils.deleteDirectory(CAMERA_DOWNLOAD_DIR);
    }

    // 签证对象 P_GCQZ 查询，添加，更新，删除
    DataManagerInterface dataManager = new DataManagerInterface() {
        @Override
        public void getDataOnResult(ResultStatus status, List<?> list) {
            dismissProgressDialog();
            if (status.getCode() != AnalysisManager.SUCCESS_DB_QUERY
                    && status.getMessage() != null &&
                    !status.getMessage().equals("")) {
                UtilTools.showToast(getActivity(), status.getMessage());
            }
            if (status.getCode() == AnalysisManager.SUCCESS_DB_QUERY) {
                if (list != null && list.size() > 0) {
                    for (Object object : list) {
                        if (object instanceof P_GCQZ) {
                            addChangeToList((P_GCQZ) object, false);
                        }
                    }
                }
                mTabAdapter.refresh();
            }

            if ((status.getCode() == AnalysisManager.SUCCESS_DB_ADD)
                    && list != null && list.size() > 0) {
                mCurrentChange.setGcqz_id(((P_GCQZ) list.get(0)).getGcqz_id());
                addChangeToList(mCurrentChange, false);
                uploadChangeDocument(mCurrentChange, null, mEditFile, false);
            }

            if (status.getCode() == AnalysisManager.SUCCESS_DB_UPDATE) {
                addChangeToList(mCurrentChange, true);
                uploadChangeDocument(mCurrentChange, mCurrentFiles, mEditFile, false);
            }

            if (status.getCode() == AnalysisManager.SUCCESS_DB_DEL) {
                mTabAdapter.removeItemById(mCurrentChange.getGcqz_id());
                mTabAdapter.refresh();
                deleteChangeDocument(mCurrentChange);
            }
        }
    };

    // 文件对象Files 上传、下载、更新、删除
    DataManagerInterface filesManager = new DataManagerInterface() {
        @Override
        public void getDataOnResult(ResultStatus status, List<?> list) {
            dismissProgressDialog();
            if ((status.getCode() == AnalysisManager.SUCCESS_DB_ADD)
                    && list != null && list.size() > 0) {
                removeLocalDocument(mCurrentChange);
            }

            if (status.getCode() == AnalysisManager.SUCCESS_DOWNLOAD) {
                addNewChange();
            }
            if ((status.getCode() == AnalysisManager.SUCCESS_UPLOAD)
                    && list != null && list.size() > 0) {

            }
            if (status.getCode() == AnalysisManager.SUCCESS_DB_DEL) {
                removeLocalDocument(mCurrentChange);
            }
        }
    };

    // 文件 上传、删除
    DataManagerInterface fileManager = new DataManagerInterface() {
        @Override
        public void getDataOnResult(ResultStatus status, List<?> list) {
            dismissProgressDialog();
            if ((status.getCode() == AnalysisManager.SUCCESS_UPLOAD)
                    && list != null && list.size() > 0) {
                // 文件上传成功后，
                // 删除服务器上的原文件
                // 更新Files对象
                //deleteChangeDocument(mCurrentChange);

                mCurrentFiles.setFile_name(((Files) list.get(0)).getFile_name());
                mCurrentFiles.setFile_path(((Files) list.get(0)).getFile_path());
                mCurrentFiles.setFile_size(((Files) list.get(0)).getFile_size());
                updateFilesObject(mCurrentFiles);
                // 删除本地文件
                removeLocalDocument(mCurrentChange);
            }
        }
    };

    private void loadChangeData() {
        mTabAdapter.clear();
        mList.clear();
        mTabAdapter.refresh();
        showProgressDialog("loading ChangeList...");
        RemoteChangeService.getInstance().getGCQZList(dataManager,
                UserCache.getCurrentUser().getTenant_id(),
                ProjectCache.getCurrentProject().getProject_id());
    }

    private void loadUserData() {
        showProgressDialog("loading UserList...");
        RemoteUserService.getInstance().getProjectUsers(
                new DataManagerInterface() {

                    @Override
                    public void getDataOnResult(ResultStatus status, List<?> list) {
                        dismissProgressDialog();
                        if (status.getCode() != AnalysisManager.SUCCESS_DB_QUERY
                                && status.getMessage() != null
                                && !status.getMessage().equals("")) {
                            UtilTools.showToast(getActivity(), status.getMessage());
                        }
                        if (status.getCode() == AnalysisManager.SUCCESS_DB_QUERY
                                && list != null
                                && list.size() > 0) {
                            for (Object object : list) {
                                if (object instanceof User) {
                                    User user = (User) object;
                                    mUserList.add(user);
                                }
                            }
                        }
                    }
                }, mProject);
    }

    // 添加、更新显示列表
    private void addChangeToList(P_GCQZ changeBean, boolean update) {
        int change_id = changeBean.getGcqz_id();
        if (change_id == 0)
            return;

        String[] headNames = mTabAdapter.getHeadNames();
        int[] arrHeadWidths = mTabAdapter.getArrHeadWidths();
        List<TextCell> rowValues = new ArrayList<TextCell>();
        String[] values = new String[] {
                changeBean.getCode(),
                changeBean.getName(),
        };

        for (int i = 0; i < arrHeadWidths.length; i++) {
            TextCell itemCell = new TextCell(values[i], headNames[i], arrHeadWidths[i]);
            rowValues.add(itemCell);
        }

        if (update) {
            mTabAdapter.setItem(change_id, rowValues);
            for (P_GCQZ c : mList) {
                if (changeBean.getGcqz_id() == c.getGcqz_id()) {
                    mList.set(mList.indexOf(c), changeBean);
                    break;
                }
            }
        } else {
            mTabAdapter.addItem(change_id, rowValues);
            mList.add(changeBean);
        }
        mTabAdapter.refresh();
    }

    // 添加签证对象
    private void addChange(P_GCQZ change) {
        RemoteChangeService.getInstance().addGCQZ(dataManager, change);
    }

    // 更新签证对象
    private void updateChange(P_GCQZ change) {
        RemoteChangeService.getInstance().updateGCQZ(dataManager, change);
    }

    // 删除签证对象
    private void deleteChange(final P_GCQZ change) {
        RemoteChangeService.getInstance().getGCBGFiles(new DataManagerInterface() {
            @Override
            public void getDataOnResult(ResultStatus status, List<?> list) {
                if ((status.getCode() == AnalysisManager.SUCCESS_DB_QUERY)
                        && list != null && list.size() > 0) {
                    
                    mCurrentFiles = ((Files) list.get(0));
                }
                RemoteChangeService.getInstance().deleteGCQZ(dataManager, change.getGcqz_id());
            }
        }, change.getGcqz_id(), Integer.valueOf(GLOBAL.FILE_TYPE[6][0]));        
    }

    // 添加签证对象
    private void addNewChange() {
        RemoteCommonService.getInstance().getCodeByDate(new DataManagerInterface() {
            @Override
            public void getDataOnResult(ResultStatus status, List<?> list) {
                if (status.getCode() == AnalysisManager.SUCCESS_DB_QUERY) {
                    mCurrentCode = status.getMessage().toString();
                    mCurrentChange = new P_GCQZ();
                    mCurrentChange.setProject_id(mProject.getProject_id());
                    mCurrentChange.setTenant_id(UserCache.getCurrentUser().getTenant_id());

                    String dest = CHANGE_DIR + mCurrentCode + ".doc"; // 默认只支持word文档
                    String templatePath = GLOBAL.FILE_SAVE_PATH + "/" +  mTemplateFiles.getFile_name();
                    File template = new File(templatePath);
                    if (!template.exists()) {
                        RemoteDocumentsService.getInstance().downloadFile(filesManager, mTemplateFiles, template);
                    } else {
                        Util.copyFile(templatePath, CHANGE_DIR);
                        File temp = new File(CHANGE_DIR +  mTemplateFiles.getFile_name());
                        temp.renameTo(new File(dest));

                        File f = new File(dest);
                        if (f.exists()) {
                            try {
                                mEditFile = f;
                                mLastModified = new Date(f.lastModified());
                                IntentBuilder.viewFile(getActivity(), f.getPath());
                            } catch (ActivityNotFoundException e) {
                                showToast(getResources().getString(R.string.application_no_found));
                            }
                        }
                    }
                }
            }            
        }, "QZ");
    }

    // 更新文件对象
    private void updateFilesObject(Files files) {
        RemoteDocumentsService.getInstance().updateFile(filesManager, files,
                new ArrayList<User>());
    }

    // 删除文件对象
    private void deleteFilesObject(Files files) {
        if (files != null) {
            files.setTenant_id(UserCache.getCurrentUser().getTenant_id());
            RemoteDocumentsService.getInstance().deleteFile(filesManager, files, 0);
        }
    }
    
    // 同时上传对象和文件
    private void uploadFile(Files files, File file) {
        files.setDirectory_id("0");
        RemoteDocumentsService.getInstance().uploadFile(filesManager, 
                null,
                files,
                UserCache.getCurrentUser().getTenant_id(),
                file, new ArrayList<User>(),
                UserCache.getCurrentUser().getUser_id());
    }

    // 打开文件
    private void openChangeDocument(P_GCQZ change) {
        RemoteChangeService.getInstance().getGCBGFiles(new DataManagerInterface() {

            @Override
            public void getDataOnResult(ResultStatus status, List<?> list) {
                if ((status.getCode() == AnalysisManager.SUCCESS_DB_QUERY)
                        && list != null && list.size() > 0) {
                    mCurrentFiles = ((Files) list.get(0));
                    String filePath = mCurrentFiles.getFile_path();
                    String fileName = getFileName(filePath);
                    mEditFile = new File(CHANGE_DIR + fileName);
                    
                    try {
                        IntentBuilder.viewFile(getActivity(), mCurrentFiles, mEditFile.getPath());
                    } catch (ActivityNotFoundException e) {
                        showToast(getResources().getString(R.string.application_no_found));
                    }
                    
                    mLastModified = new Date(mEditFile.lastModified());
                }
            }
        }, change.getGcqz_id(), Integer.valueOf(GLOBAL.FILE_TYPE[6][0]));
    }

    // 上传文件
    private void uploadChangeDocument(P_GCQZ change, Files files, File file, boolean isAttachment) {
        File newFile;
        if (!isAttachment) {
            newFile = new File(CHANGE_DIR + change.getCode() + ".doc");
            file.renameTo(newFile);
        } else {
            newFile = file;
        }

        if (files == null) {
            files = new Files();
            if (isAttachment) {
                files.setDir_type(Integer.valueOf(GLOBAL.FILE_TYPE[7][0]));
            } else {
                files.setDir_type(Integer.valueOf(GLOBAL.FILE_TYPE[6][0]));
            }
            files.setType_id(change.getGcqz_id());
            files.setTitle(getFileName(newFile.getPath()));
            files.setAuthor(UserCache.getCurrentUser().getUser_id());
            //findRelevancePath(files, newFile);
            files.setPath(CHANGE_DIR + getFileName(file.getPath()));
            uploadFile(files, newFile);
        } else {
            // 只是更新文件
            RemoteDocumentsService.getInstance().uploadFile(fileManager, null,
                    UserCache.getCurrentUser().getTenant_id(), newFile);
        }
    }

    // 删除签证相关文件
    private void deleteChangeDocument(P_GCQZ change) {
        // 删除附件图片 和 删除签证文档 .doc
        RemoteChangeService.getInstance().getGCBGFiles(new DataManagerInterface() {
            @Override
            public void getDataOnResult(ResultStatus status, List<?> list) {
                mAttachmentFiles.clear();
                if (status.getCode() == AnalysisManager.SUCCESS_DB_QUERY) {
                    if (list != null && list.size() > 0) {
                        for (Object object : list) {
                            if (object instanceof Files) {
                                Files files = (Files) object;
                                files.setTenant_id(UserCache.getCurrentUser().getTenant_id());
                                mAttachmentFiles.add(files); 
                            }
                        }
                        for (Files attachment : mAttachmentFiles) {
                            deleteFilesObject(attachment);
                        }
                    }                    
                }
            }            
        }, change.getGcqz_id(), Integer.valueOf(GLOBAL.FILE_TYPE[7][0]));

        deleteFilesObject(mCurrentFiles);
    }

    // 删除本地文件
    private void removeLocalDocument(P_GCQZ change) {
        FileUtils.deleteFile(CHANGE_DIR + change.getCode() + ".doc");
        if (mCurrentFiles != null) {
            FileUtils.deleteFile(CHANGE_DIR + mCurrentFiles.getFile_name());
        }
        FileUtils.deleteDirectory(CAMERA_UPLOAD_DIR);
    }

    private void initOptionsMenuView() {
        String[] subMenuNames = new String[] {
                getString(R.string.open),
                getString(R.string.delete),
                getString(R.string.attachment)
        };
        if (mHasViewPermission && !mHasEditPermission) {
            subMenuNames = new String[] { getString(R.string.attachment) };
        }
        mOptionsMenuView = new OptionsMenuView(getActivity(), subMenuNames);
        mOptionsMenuView
                .setSubMenuListener(new OptionsMenuView.SubMenuListener() {
                    @Override
                    public void onSubMenuClick(View view) {
                        switch ((Integer) view.getTag()) {
                            case 0:
                                if (mHasViewPermission && !mHasEditPermission) {
                                    openAttachment(mCurrentChange);
                                } else {
                                    openChangeDocument(mCurrentChange);
                                }
                                break;
                            case 1:
                                showDeleteTips(mCurrentChange);
                                break;
                            case 2:
                                openAttachment(mCurrentChange);
                                break;
                        }
                        mOptionsMenuView.dismiss();
                    }
                });
    }
    
    /**
     * 删除一条数据
     * 
     * @param bean
     */
    private void showDeleteTips(final P_GCQZ change) {
        if (!mHasEditPermission) {
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
                        deleteChange(change);
                        dialog.dismiss();
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
    
    private void openAttachment(P_GCQZ change) {
        File f = new File(CAMERA_DOWNLOAD_DIR);
        if (!f.exists()) f.mkdirs();
        showProgressDialog("Loading Attachments...");
        RemoteChangeService.getInstance().getGCBGFiles(new DataManagerInterface() {

            @Override
            public void getDataOnResult(ResultStatus status, List<?> list) {
                mAttachmentThumbs.clear();
                mAttachmentFiles.clear();
                if ((status.getCode() == AnalysisManager.SUCCESS_DB_QUERY)
                        && list != null && list.size() > 0) {
                    mAttachmentCount = list.size();                   
                    for (Object object : list) {
                        if (object instanceof Files) {
                            Files files = (Files) object;
                            String filePath = CAMERA_DOWNLOAD_DIR + files.getFile_name();
                            mAttachmentFiles.add(files);                            
                            mAttachmentThumbs.add(filePath);
                            File file = new File(filePath);
                            if (file.exists()) {
                                mAttachmentCount--;
                                if (mAttachmentCount == 0) {
                                    startAttachmentActivity(mAttachmentThumbs);
                                }
                            } else {
                                files.setTenant_id(UserCache.getCurrentUser().getTenant_id());
                                RemoteDocumentsService.getInstance().downloadFile(new DataManagerInterface() {
                                    @Override
                                    public void getDataOnResult(ResultStatus status, List<?> list) {
                                        if (status.getCode() != AnalysisManager.SUCCESS_DOWNLOAD
                                                && status.getMessage() != null &&
                                                !status.getMessage().equals("")) {
                                            UtilTools.showToast(getActivity(), status.getMessage());
                                        }
                                        mAttachmentCount--;
                                        if (mAttachmentCount == 0) {
                                            startAttachmentActivity(mAttachmentThumbs);
                                        }                           
                                    }
                                }, null, files, file);
                            }
                        }
                    }
                } else {
                    startAttachmentActivity(mAttachmentThumbs);
                }
            }
        }, change.getGcqz_id(), Integer.valueOf(GLOBAL.FILE_TYPE[7][0]));
    }
    
    private void startAttachmentActivity(ArrayList<String> thumbs) {
        dismissProgressDialog();
        Intent intent = new Intent(getActivity(), ChangeAttachmentActivity.class);
        Bundle bundle = new Bundle();
        bundle.putSerializable("thumbs", thumbs);
        if (mHasViewPermission && !mHasEditPermission) {
            bundle.putBoolean("viewonly", true);
        }
        intent.putExtras(bundle);
        startActivityForResult(intent, ATTACHMENT_REQUEST_CODE);
    }
    
    @SuppressWarnings("unchecked")
    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (resultCode != Activity.RESULT_OK) return;

        if (requestCode == ATTACHMENT_REQUEST_CODE) {
            ArrayList<String> newThumbs = new  ArrayList<String>();
            newThumbs = (ArrayList<String>) data.getSerializableExtra("thumbs");
            checkAttachment(mAttachmentThumbs, newThumbs);
        }
        
        if (requestCode == SELECT_TEMPLATE_CODE) {
            Files files = (Files) data.getSerializableExtra(ListSelectActivity.RESULT_KEY);
            //mIsTemplateAttachmentAdd = true;
            //mCurrentFile = new File(TEMPLATE_DIR + files.getFile_name());
            //mChangeTemplate = CHANGE_TEMPLATE +  files.getFile_name();
            mTemplateFiles = files;
            //IntentBuilder.viewFile(mActivity, files, templatePath);
            addNewChange();
        }
        super.onActivityResult(requestCode, resultCode, data);
    }
    
    private void checkAttachment(ArrayList<String> oldList, ArrayList<String> newList) {
        ArrayList<String> sameList = new  ArrayList<String>();
        for (int i = 0; i < newList.size(); i++) {
            for (int j = 0; j < oldList.size(); j++) {
                if (newList.get(i).equals(oldList.get(j))) {
                    sameList.add(newList.get(i));
                }
            }
        }
        if (sameList.size() > 0) {
            oldList.removeAll(sameList);
            newList.removeAll(sameList);
        }
        for (int i = 0; i < oldList.size(); i++) {            
            for (int j = 0; j < mAttachmentFiles.size(); j++) {
                if (getFileName(oldList.get(i)).equals(mAttachmentFiles.get(j).getFile_name())) {
                    showProgressDialog("updating Attachments...");
                    deleteFilesObject(mAttachmentFiles.get(j));
                    continue;
                }
            }
            FileUtils.deleteFile(oldList.get(i));
        }
        for (int i = 0; i < newList.size(); i++) {
            showProgressDialog("updating Attachments...");
            File file = new File(newList.get(i));
            uploadChangeDocument(mCurrentChange, null, file, true);
        }                
    }

    private boolean checkModified(File file) {
        if (file != null && file.exists()
                && mLastModified.before(new Date(file.lastModified()))) {
            return true;
        }
        return false;
    }

    private void showToast(String msg) {
        if (getActivity() != null) {
            Toast.makeText(getActivity(), msg, Toast.LENGTH_SHORT).show();
        }
    }

    private void showProgressDialog(String text) {
        dismissProgressDialog();
        mProgressDialog = UtilTools.showProgressDialog(getActivity(), true, false);
    }

    private void dismissProgressDialog() {
        if (mProgressDialog != null && mProgressDialog.isShowing())
            mProgressDialog.dismiss();
    }

    private String getFileName(String path) {
        String name = "";
        String[] paths = path.split("/");
        if (paths.length > 1) {
            name = paths[paths.length - 1];
        }
        return name;
    }

    private P_GCQZ findChangeById(int change_id) {
        P_GCQZ res = null;
        for (P_GCQZ change : mList) {
            if (change.getGcqz_id() == change_id) {
                res = change;
                break;
            }
        }
        return res;
    }

    private String readWord(String path, int row, int col) {
        String res = "";
        try {
            FileInputStream in = new FileInputStream(path);
            HWPFDocument hwpf = new HWPFDocument(in);
            Range range = hwpf.getRange();
            TableIterator it = new TableIterator(range);
            while (it.hasNext()) {
                Table tb = (Table) it.next();
                TableRow tr = tb.getRow(row);
                TableCell td = tr.getCell(col);
                for (int k = 0; k < td.numParagraphs(); k++) {
                    Paragraph para = td.getParagraph(k);
                    String s = para.text();
                    res = s.replaceAll((char) 7 + "", "");
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return res;
    }

    /**
     * 显示是否上传文件对话框
     */
    private void showUpdateDocumentDialog() {
        new AlertDialog.Builder(getActivity())
                .setTitle(getResources().getString(R.string.remind))
                .setMessage(getResources().getString(R.string.need_upload_change))
                .setPositiveButton(getResources().getString(R.string.submit),
                        new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                updateChange(mCurrentChange);
                                dialog.dismiss();
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
/*    
    private void findRelevancePath(final Files files, final File file) {        
        final Document directory = new Document();     
        directory.setDirectory_type(GLOBAL.DIR_TYPE_PROJECT);
        directory.setTenant_id(UserCache.getCurrentUser().getTenant_id());
        directory.setProject_id(ProjectCache.getCurrentProject().getProject_id());
        RemoteDocumentsService.getInstance().getDirectoryList(new DataManagerInterface() {
            @Override
            public void getDataOnResult(ResultStatus status, List<?> list) {
                int directoryId = -1;
                String directoryPath = "";
                if (status.getCode() == AnalysisManager.SUCCESS_DB_QUERY) {
                    if (list != null && list.size() > 0) {
                        @SuppressWarnings("unchecked")
						List<Document> dList = (List<Document>) list;
                        for (Document d : dList) {
                            if (d.getRelevance_path_code() == null 
                                    || d.getRelevance_path_code().equals("")) {
                                continue;
                            }
                            if (d.getRelevance_path_code()
                                    .equals(GLOBAL.FILE_TYPE[6][0])) {
                                directoryId = d.getDocument_id();
                                directoryPath = Utils.calculateDirPath(dList, d);
                                break;
                            }
                        }
                        if (directoryId >= 0) {
                            files.setDirectory_id(directoryId);
                            files.setPath(directoryPath + getFileName(file.getPath()));
                            uploadFile(files, file);
                        } else {
                            // 创建level=2 的一个目录
                            addDirectory(directory, files, file);
                        }
                    } else {
                        // 创建一个跟目录
                        addDirectory(directory, files, file);
                    }
                }                
            }            
        }, directory);        
    }
    
    private void addDirectory(final Document directory, final Files files, final File file) {
        directory.setName(getString(R.string.change_folder));
        directory.setParents_id(0);
        directory.setLevel(0);
        directory.setRelevance_path_code(GLOBAL.FILE_TYPE[6][0]);
        RemoteDocumentsService.getInstance().addDirectory(new DataManagerInterface() {
            @Override
            public void getDataOnResult(ResultStatus status, List<?> list) {
                if (status.getCode() == AnalysisManager.SUCCESS_DB_ADD) {
                    Document d = (Document) list.get(0);
                    directory.setDocument_id(d.getDocument_id());
                    RemoteDocumentsService.getInstance().relatedDocumentPath(new DataManagerInterface() {
                        @Override
                        public void getDataOnResult(ResultStatus status, List<?> list) {
                        }
                    }, directory);
                    files.setDirectory_id(d.getDocument_id());
                    files.setPath("/" + getString(R.string.change_folder) + "/" + getFileName(file.getPath()));
                    uploadFile(files, file);
                }
            }                            
        }, directory);
    }
*/
}
