package com.pm360.cepm360.app.module.document;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.pm360.cepm360.R;
import com.pm360.cepm360.app.cache.PermissionCache;
import com.pm360.cepm360.app.common.custinterface.CommonListInterface;
import com.pm360.cepm360.app.common.view.OptionsMenuView;
import com.pm360.cepm360.common.GLOBAL;
import com.pm360.cepm360.entity.Document;
import com.pm360.cepm360.entity.Files;

import java.util.Map;

/**
 * 
 *  DocumentTreeFragment 继承于DocumentBaseFragment， 主要的目的是：
 *  用于区别 个人文档、项目文档，公共文档，文件归档 4个fragment中，
 *  1. 列表表头的不同，
 *  2. 列表中选项菜单的不同，以及响应处理的不同。
 *
 */
public class DocumentTreeFragment extends DocumentBaseFragment<Document> {
    
    private static final String PROJECT_DOC_EDIT = GLOBAL.SYS_ACTION[6][0];
    //private static final String PROJECT_DOC_VIEW = GLOBAL.SYS_ACTION[7][0];
    private static final String PUBLIC_DOC_EDIT = GLOBAL.SYS_ACTION[8][0];
    //private static final String PUBLIC_DOC_VIEW = GLOBAL.SYS_ACTION[9][0];
    private static final String SYS_DOC_ARCHIVE = GLOBAL.SYS_ACTION[54][0];
    
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
            Bundle savedInstanceState) {

        // 在初始化列表之前先获取当前是什么类型，个人文档还是公共文档。。。
        String tag = getArguments().getString("tag");
        setCurrentType(getTypeByTag(tag));
        // 初始化实现部分数据
        initContentList(contentListInterface);
        createOptionsMenuView();
        
        // 父类初始化
        View view = super.onCreateView(inflater, container, savedInstanceState);

        return view;
    }
    
    private int getTypeByTag(String tag) {
        if (tag.equals(getString(R.string.private_document))) {
            return 0;
        } else if (tag.equals(getString(R.string.project_document))) {
            return 1;
        } else if (tag.equals(getString(R.string.public_document))) {
            return 2;
        } else if (tag.equals(getString(R.string.archive_document))) {
            return 3;
        } else {
            return 0;
        }
    }
    
    /**
     * 根据不同的类型，创建列表行的选项菜单，以及设置其响应事件
     */
    private void createOptionsMenuView() {
        String[] subMenuNames;

        subMenuNames = new String[] {
              getResources().getString(R.string.view),
              getResources().getString(R.string.delete),
              getResources().getString(R.string.more_attributes),
              getResources().getString(R.string.archive),
              getResources().getString(R.string.set_to_top),
              getResources().getString(R.string.move),
              getResources().getString(R.string.copy), };
        mOptionsMenuView = new OptionsMenuView(getActivity(), subMenuNames);
//      0:查看，1:删除，2:属性，3:归档，4:置顶，5:移动，6:复制
        if (mCurrentType == GLOBAL.DIR_TYPE_ARCHIVE) { // 文件归档
        	if (PermissionCache.hasSysPermission(SYS_DOC_ARCHIVE)) {
                // 有归档权限
        		mOptionsMenuView.setVisibileMenu(new int[] {1, 2, 5, 6}, false);
        	} else {
        		// 无归档权限
        		mOptionsMenuView.setVisibileMenu(new int[] {1, 2, 3, 5, 6}, false);
        	}
        } else {
        	if (mCurrentType == GLOBAL.DIR_TYPE_PERSONAL) { // 个人文档
        		mOptionsMenuView.setVisibileMenu(new int[] {3, 4}, false);
        	} else {
        		if (hasEditPermission()) {
                    // 有编辑权限
            		mOptionsMenuView.setVisibileMenu(new int[] {3}, false);
        		} else {
        			// 无编辑权限
            		mOptionsMenuView.setVisibileMenu(new int[] {1, 3}, false);
        		}
        	}
        }
        
        mOptionsMenuView
                .setSubMenuListener(new OptionsMenuView.SubMenuListener() {
                    @Override
                    public void onSubMenuClick(View view) {
                        switch ((Integer) view.getTag()) {
                        case 0:
                        	// 查看
                            openFile();
                            break;
                        case 1:
                        	// 删除
                            showDeleteTips();
                            break;
                        case 2:
                        	// 属性
                        	showDocumentDetails();
                            break;
                        case 3:
                        	// 归档
                        	startDirectorySelectActivity(R.string.archive);
                        	break;
                        case 4:
                        	// 置顶
                            setToTop();
                            break;
                        case 5:
                        	// 移动
                        	startDirectorySelectActivity(R.string.move);
                        	break;
                        case 6:
                        	// 复制
                        	startDirectorySelectActivity(R.string.copy);
                        	break;
                        }
                        mOptionsMenuView.dismiss();
                    }
                });
    }
    
    /**
     * 根据当前的类型初始化表格
     */
    CommonListInterface<Files> contentListInterface = new CommonListInterface<Files>() {

        @Override
        public Map<String, Map<String, String>> getDisplayFieldsSwitchMap() {
            // TODO Auto-generated method stub
            return null;
        }

        @Override
        public int getListItemId(Files t) {
            return t.getFile_id();
        }

        @Override
        public String[] getDisplayFeilds() {
            return null;
        }

        @Override
        public int getListHeaderLayoutId() {
            if (mCurrentType == GLOBAL.DIR_TYPE_ARCHIVE) { // 归档
                return R.layout.document_archive_manage_list_item;
            } else {
                return R.layout.document_personal_list_item;
            }
        }

        @Override
        public int getListLayoutId() {
            if (mCurrentType == GLOBAL.DIR_TYPE_ARCHIVE) { // 归档
                return R.layout.document_archive_manage_list_item;
            } else {
                return R.layout.document_personal_list_item;
            }
        }

        @Override
        public int getListHeaderNames() {
            if (mCurrentType == GLOBAL.DIR_TYPE_ARCHIVE) { // 归档
                return R.array.document_archive_manage_table_names;
            } else {
                return R.array.document_personal_item_names;
            }
        }

        @Override
        public int getListHeaderIds() {
            if (mCurrentType == GLOBAL.DIR_TYPE_ARCHIVE) { // 归档
                return R.array.document_archive_manage_table_ids;
            } else {
                return R.array.document_personal_item_ids;
            }
        }

        @Override
        public int getListItemIds() {
            return getListHeaderIds();
        }
        
    };
    
    private boolean hasEditPermission() {
        if (PermissionCache.hasSysPermission(PUBLIC_DOC_EDIT)
                || PermissionCache.hasSysPermission(PROJECT_DOC_EDIT)) {
            return true;
        } else {
            return false;
        }
    }
    
}
