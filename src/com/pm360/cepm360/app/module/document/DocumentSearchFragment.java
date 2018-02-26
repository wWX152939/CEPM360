package com.pm360.cepm360.app.module.document;

import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.ActivityNotFoundException;
import android.content.DialogInterface;
import android.content.res.TypedArray;
import android.graphics.Color;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.v4.app.Fragment;
import android.text.TextUtils.TruncateAt;
import android.util.TypedValue;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.pm360.cepm360.R;
import com.pm360.cepm360.app.cache.PermissionCache;
import com.pm360.cepm360.app.cache.UserCache;
import com.pm360.cepm360.app.common.activity.DataManagerInterface;
import com.pm360.cepm360.app.common.adpater.DataListAdapter;
import com.pm360.cepm360.app.common.adpater.DataListAdapter.ListAdapterInterface;
import com.pm360.cepm360.app.common.adpater.DataListAdapter.ViewHolder;
import com.pm360.cepm360.app.common.view.OptionsMenuView;
import com.pm360.cepm360.app.utils.FileIconHelper;
import com.pm360.cepm360.app.utils.IntentBuilder;
import com.pm360.cepm360.app.utils.UtilTools;
import com.pm360.cepm360.common.GLOBAL;
import com.pm360.cepm360.common.ResultStatus;
import com.pm360.cepm360.common.json.AnalysisManager;
import com.pm360.cepm360.common.util.DateUtils;
import com.pm360.cepm360.common.util.FileUtils;
import com.pm360.cepm360.entity.Files;
import com.pm360.cepm360.entity.SearchFiles;
import com.pm360.cepm360.services.document.RemoteDocumentsService;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * 文档搜索 Fragment
 *
 */
public class DocumentSearchFragment extends Fragment {
	private View mRootView;
	private View mHeaderView;

	private DocumentActivity mActivity;
	private ListView mFileResultListView;

	private int[] mDisplayItemIds;
	private String[] mDisplayItemNames;

	private DataListAdapter<Files> mFileResultListAdapter;
//	private List<Project> mProjectDataLists = new ArrayList<Project>();

	private SearchFiles mSearchFiles;
	private Files mCurrentFileItem;
	private OptionsMenuView mOptionsMenuView;
	private ProgressDialog mProgressDialog;

	private static final int TOAST_SHOW = 0;
//	private static final int GOT_PROJECT_PERMISSION = 1;

	private static final String PROJECT_DOC_EDIT = "3_1";
	private static final String PROJECT_DOC_VIEW = "3_2";
	private static final String PUBLIC_DOC_EDIT = "3_3";
	// private static final String PUBLIC_DOC_VIEW = "3_4";
	private static final String DOWNLOAD_DIR = GLOBAL.FILE_SAVE_PATH;

	private Handler mHandler = new Handler(new Handler.Callback() {
		@Override
		public boolean handleMessage(Message msg) {
			switch (msg.what) {
			case TOAST_SHOW:
				if (mActivity != null) {
					Toast.makeText(mActivity, (CharSequence) msg.obj,
					        Toast.LENGTH_SHORT).show();
				}
				break;
//			case GOT_PROJECT_PERMISSION:
//				setProjectPermissions((String[]) msg.obj);
//				break;
			}
			return false;
		}
	});

//	private void setProjectPermissions(String[] permissions) {
//		if (hasProjectPermission(permissions, PROJECT_DOC_EDIT)) {
//			showDeleteTips();
//		} else {
//			showToast(getString(R.string.no_edit_permission));
//			return;
//		}
//	}

	public boolean hasProjectPermission(String[] actions, String permission) {
		if (actions == null)
			return false;
		for (String s : actions) {
			if (s.equals(permission))
				return true;
		}
		return false;
	}

	public void showToast(String message) {
		Message msg = Message.obtain();
		msg.what = TOAST_SHOW;
		msg.obj = message;
		mHandler.sendMessage(msg);
	}
	
    @Override
    public void onHiddenChanged(boolean hidden) {
        super.onHiddenChanged(hidden);
        if (!hidden) {
        	searchFile();
        }
    }
    
    public void searchFile() {
    	mSearchFiles = mActivity.getBuilder().getSearchFiles();
        startSearch();
    }

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
	        Bundle savedInstanceState) {
		super.onCreateView(inflater, container, savedInstanceState);

		mActivity = (DocumentActivity) getActivity();
		if (PermissionCache.hasSysPermission(PROJECT_DOC_VIEW)
		        || PermissionCache.hasSysPermission(PROJECT_DOC_EDIT)) {
			mRootView = inflater.inflate(R.layout.document_search_layout,
			        container, false);

			initSearchResultView(mRootView);
			initOptionsMenuView();
			
			searchFile();
		} else {
			mRootView = inflater.inflate(
			        R.layout.no_permissions_content_layout, container, false);
		}
		return mRootView;
	}

	// 初始化搜索结果界面
	private void initSearchResultView(View view) {
		mDisplayItemNames = getResources().getStringArray(
		        R.array.document_result_table_names);
		TypedArray typedArray = getResources().obtainTypedArray(
		        R.array.document_result_table_ids);

		mHeaderView = view.findViewById(R.id.listHeaderView);
		mHeaderView.setBackgroundColor(Color.parseColor("#E4E9EC"));
		((TextView) mHeaderView.findViewById(R.id.file_type))
		        .setVisibility(View.VISIBLE);
		if (mDisplayItemNames != null) {
			int itemLength = mDisplayItemNames.length;
			mDisplayItemIds = new int[itemLength];
			// 配置目录项
			for (int i = 0; i < itemLength; i++) {
				mDisplayItemIds[i] = typedArray.getResourceId(i, 0);
				TextView tv = (TextView) mHeaderView
				        .findViewById(mDisplayItemIds[i]);
                tv.setGravity(Gravity.CENTER);
                tv.setTextSize(TypedValue.COMPLEX_UNIT_PX,
                        getActivity().getResources().getDimensionPixelSize(
                                R.dimen.table_title_textsize));
                tv.setText(mDisplayItemNames[i]);
			}
		}
		typedArray.recycle();
		
		// 注册文件结果视图适配器
		mFileResultListView = (ListView) mRootView
		        .findViewById(R.id.file_list_item);
		mFileResultListAdapter = new DataListAdapter<Files>(mActivity,
		        adapterInterface);
		mFileResultListView.setAdapter(mFileResultListAdapter);
	}

	private void openFile() {
		String path = DOWNLOAD_DIR + mCurrentFileItem.getPath();
		mCurrentFileItem.setTenant_id(UserCache.getCurrentUser().getTenant_id());
		try {
			IntentBuilder.viewFile(mActivity, mCurrentFileItem, path);
		} catch (ActivityNotFoundException e) {
		}
	}

//	private void checkPermission(String projectName) {
//		Project project = null;
//		for (int i = 0; i < mProjectDataLists.size(); i++) {
//			if (projectName.equals(mProjectDataLists.get(i).getName())) {
//				project = mProjectDataLists.get(i);
//				break;
//			}
//		}
//
//		if (project != null) {
//			RemoteCommonService.getInstance().getPermissionByProject(
//			        new DataManagerInterface() {
//				        @Override
//				        public void getDataOnResult(ResultStatus status,
//				                List<?> list) {
//					        Message msg = Message.obtain();
//					        msg.what = GOT_PROJECT_PERMISSION;
//					        if (status.getCode() == AnalysisManager.SUCCESS_DB_QUERY
//					                && list != null && list.size() > 0) {
//						        StringBuilder builder = new StringBuilder();
//						        for (Object object : list) {
//							        if (object instanceof Role) {
//								        Role role = (Role) object;
//								        String action = role.getAction();
//								        if (action != null) {
//									        builder.append(action + ",");
//								        }
//							        }
//						        }
//						        String role_text = builder.toString();
//						        if (role_text.endsWith(",")) {
//							        role_text = role_text.substring(0,
//							                role_text.length() - 1);
//						        }
//						        msg.obj = role_text.split(",");
//					        }
//					        mHandler.sendMessage(msg);
//				        }
//			        }, project.getProject_id(), UserCache.getCurrentUser());
//		} else {
//			showToast(getResources().getString(R.string.no_edit_permission));
//		}
//	}

	@SuppressWarnings("unused")
    private void deleteFile() {
		if (mCurrentFileItem.getDir_type() == GLOBAL.DIR_TYPE_PERSONAL) {
			showDeleteTips();
		} else if (mCurrentFileItem.getDir_type() == GLOBAL.DIR_TYPE_PUBLIC) {
			if (PermissionCache.hasSysPermission(PUBLIC_DOC_EDIT)) {
				showDeleteTips();
			} else {
				showToast(getResources().getString(R.string.no_edit_permission));
			}
		} else if (mCurrentFileItem.getDir_type() == GLOBAL.DIR_TYPE_PROJECT) {
            if (PermissionCache.hasSysPermission(PROJECT_DOC_EDIT)) {
                showToast(getResources().getString(
                        R.string.no_edit_permission));
                showDeleteTips();
            } else {
                showToast(getResources().getString(R.string.no_edit_permission));
            }           
		}
		    
//		    Project currentProject = ProjectCache.getCurrentProject();
//		    String projectName = mCurrentFileItem.getProject_name();
//			if (currentProject != null && projectName != null
//			        && projectName.equals(currentProject.getName())) {
//				if (!PermissionCache.hasSysPermission(PROJECT_DOC_EDIT)) {
//					showToast(getResources().getString(
//					        R.string.no_edit_permission));
//					return;
//				}
//				showDeleteTips();
//			} else {
//				if (PermissionCache.hasSysPermission(PROJECT_DOC_EDIT)) {
//					checkPermission(mCurrentFileItem.getProject_name());
//				}
//			}
//		}
	}

	private void showDeleteTips() {
		new AlertDialog.Builder(mActivity)
		        .setTitle(getResources().getString(R.string.remind))
		        .setMessage(
		                getResources().getString(
		                        R.string.confirm_delete))
		        .setPositiveButton(getResources().getString(R.string.confirm),
		                new DialogInterface.OnClickListener() {
			                @Override
			                public void onClick(DialogInterface dialog,
			                        int which) {
				                dialog.dismiss();
				                mCurrentFileItem.setTenant_id(UserCache.getCurrentUser()
				                        .getTenant_id());
				                RemoteDocumentsService.getInstance()
				                        .deleteFile(mFilesListManager, mCurrentFileItem, 0);
			                }
		                })
		        .setNegativeButton(getResources().getString(R.string.cancel),
		                new DialogInterface.OnClickListener() {
			                @Override
			                public void onClick(DialogInterface dialog,
			                        int which) {
				                dialog.dismiss();
			                }
		                }).show();
	}

	private void showDocumentDetails() {
		DocumentPropertiesDialog dialog = new DocumentPropertiesDialog(
		        mActivity, false);
		dialog.setFileItem(mCurrentFileItem);
		dialog.initLayout();
		dialog.show();
	}

	private void initOptionsMenuView() {
        String[] subMenuNames;
//        if (PermissionCache.hasSysPermission(PUBLIC_DOC_EDIT)) {
//            subMenuNames = new String[] {
//                    getResources().getString(R.string.view),
//                    getResources().getString(R.string.delete),
//                    getResources().getString(R.string.more_attributes) };
//        } else {
            subMenuNames = new String[] {
                    getResources().getString(R.string.view),
                    getResources().getString(R.string.more_attributes) };
//        }
		mOptionsMenuView = new OptionsMenuView(mActivity, subMenuNames);
		mOptionsMenuView
		        .setSubMenuListener(new OptionsMenuView.SubMenuListener() {
			        @Override
			        public void onSubMenuClick(View view) {
				        switch ((Integer) view.getTag()) {
						case 0: // 查看
							openFile();
							break;
						case 1: // 删除
//							deleteFile();
//							break;
//						case 2: // 更多属性
							showDocumentDetails();
							break;
						}
						mOptionsMenuView.dismiss();
					}
		        });
	}
	
    private void startSearch() {
        mFileResultListAdapter.clearAll();
        showProgressDialog("Searching Files...");
        RemoteDocumentsService.getInstance().getSearchedFiles(
                mFilesListManager, mSearchFiles);
    }

	private DataManagerInterface mFilesListManager = new DataManagerInterface() {
		@Override
		public void getDataOnResult(ResultStatus status, List<?> list) {
			dismissProgressDialog();
			if ((status.getCode() == AnalysisManager.SUCCESS_DB_QUERY)
			        && list != null && list.size() != 0) {
				for (Object object : list) {
					if (object instanceof Files) {
						mFileResultListAdapter.addDataToList((Files) object);
					}
				}
			}

			if (status.getCode() == AnalysisManager.SUCCESS_DB_DEL) {
				mFileResultListAdapter.deleteData(mCurrentFileItem);
			}

			if (status.getCode() != AnalysisManager.SUCCESS_DB_QUERY) {
				showToast(status.getMessage());
			}
		}
	};

	private void showProgressDialog(String text) {
		dismissProgressDialog();
		mProgressDialog = UtilTools.showProgressDialog(getActivity(), true,
		        false);
	}

	private void dismissProgressDialog() {
		if (mProgressDialog != null && mProgressDialog.isShowing())
			mProgressDialog.dismiss();
	}

	ListAdapterInterface<Files> adapterInterface = new ListAdapterInterface<Files>() {
		@Override
		public int getLayoutId() {
			return R.layout.document_search_result_list_item;
		}

		@Override
		public View getHeaderView() {
			return mHeaderView;
		}

		private Map<String, String> documentToMap(Files bean) {
			Map<String, String> mapItem = new HashMap<String, String>();
			if (bean instanceof Files) {
				mapItem.put(mDisplayItemNames[0], bean.getTitle());
				mapItem.put(mDisplayItemNames[1], bean.getProject_name()); // 所属项目
				mapItem.put(mDisplayItemNames[2],
				        FileUtils.getDirctoryPath(bean.getPath())); // 所在目录
				mapItem.put(
				        mDisplayItemNames[3],
				        DateUtils.dateToString(DateUtils.FORMAT_LONG1,
				                bean.getCreate_time())); // 创建时间
			}
			return mapItem;
		}

		@Override
		public void regesterListeners(ViewHolder viewHolder, final int position) {
			for (int i = 0; i < viewHolder.tvs.length; i++) {
				viewHolder.tvs[i]
				        .setOnClickListener(new View.OnClickListener() {
					        @Override
					        public void onClick(View view) {
						        mFileResultListAdapter.setSelected(position,
						                true);
						        mCurrentFileItem = mFileResultListAdapter
						                .getItem(position);

						        mOptionsMenuView.showAsDropDown(view, 0, (-view
						                .getMeasuredHeight() - UtilTools.dp2pxH(
						                view.getContext(), 40)));
					        }
				        });
			}
		}

		@Override
		public void initListViewItem(View convertView, ViewHolder holder,
		        DataListAdapter<Files> adapter, int position) {
			holder.ivs[0].setImageResource(FileIconHelper
			        .getIcon((Files) adapter.getItem(position)));

			Map<String, String> item = documentToMap((Files) adapter
			        .getItem(position));
			for (int i = 0; i < mDisplayItemNames.length; i++) {
				holder.tvs[i].setText(item.get(mDisplayItemNames[i]));
				holder.tvs[i].setEllipsize(TruncateAt.MARQUEE);
			}
		}

		@Override
		public void initLayout(View convertView,
		        com.pm360.cepm360.app.common.adpater.DataListAdapter.ViewHolder holder) {
			holder.tvs = new TextView[mDisplayItemIds.length];
			for (int i = 0; i < mDisplayItemIds.length; i++) {
				holder.tvs[i] = (TextView) convertView
				        .findViewById(mDisplayItemIds[i]);
			}

			holder.ivs = new ImageView[1];
			holder.ivs[0] = (ImageView) convertView
			        .findViewById(R.id.file_image);
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
}