package com.pm360.cepm360.app.module.common.plan;

import java.io.File;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.ActivityNotFoundException;
import android.content.Intent;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.ImageView;
import android.widget.Toast;

import com.pm360.cepm360.R;
import com.pm360.cepm360.app.CepmApplication;
import com.pm360.cepm360.app.cache.UserCache;
import com.pm360.cepm360.app.common.activity.DataManagerInterface;
import com.pm360.cepm360.app.common.activity.ListSelectActivity;
import com.pm360.cepm360.app.common.adpater.DataListAdapter.ViewHolder;
import com.pm360.cepm360.app.common.adpater.DataTreeListAdapter;
import com.pm360.cepm360.app.common.custinterface.CommonListInterface;
import com.pm360.cepm360.app.common.custinterface.DateListInterface;
import com.pm360.cepm360.app.common.custinterface.DialogAdapterInterface;
import com.pm360.cepm360.app.common.custinterface.DocumentServiceInterface;
import com.pm360.cepm360.app.common.custinterface.FloatingMenuInterface;
import com.pm360.cepm360.app.common.custinterface.OptionMenuInterface;
import com.pm360.cepm360.app.common.view.OptionsMenuView.SubMenuListener;
import com.pm360.cepm360.app.common.view.parent.BaseDialog;
import com.pm360.cepm360.app.common.view.parent.BaseListRelevanceFragment;
import com.pm360.cepm360.app.module.common.attachment.AttachmentActivity;
import com.pm360.cepm360.app.module.common.attachment.AttachmentReadOnlyActivity;
import com.pm360.cepm360.app.module.common.attachment.UploadAttachManager;
import com.pm360.cepm360.app.module.email.MailboxViewFragment;
import com.pm360.cepm360.app.module.schedule.SendEmailPresenter;
import com.pm360.cepm360.app.utils.FileIconHelper;
import com.pm360.cepm360.app.utils.IntentBuilder;
import com.pm360.cepm360.common.GLOBAL;
import com.pm360.cepm360.common.ResultStatus;
import com.pm360.cepm360.common.json.AnalysisManager;
import com.pm360.cepm360.common.util.DateUtils;
import com.pm360.cepm360.entity.Files;
import com.pm360.cepm360.entity.MailBox;
import com.pm360.cepm360.entity.Task;
import com.pm360.cepm360.entity.TaskCell;
import com.pm360.cepm360.entity.User;
import com.pm360.cepm360.entity.ZH_group_task;
import com.pm360.cepm360.services.common.RemoteCommonService;
import com.pm360.cepm360.services.document.RemoteDocumentsService;
import com.pm360.cepm360.services.plan.RemoteTaskService;

public class CommonDocumentFragment<B extends TaskCell> extends BaseListRelevanceFragment<Files, B> {

	public static final int TYPE_ZH_MAKE_DOC = 1;
	public static final int TYPE_SCHEDULE_MAKE_DOC = 2;
	public static final int TYPE_ZH_FEEDBACK_DOC = 3;
	public static final int TYPE_SCHEDULE_FEEDBACK_DOC = 4;
	
	private RemoteDocumentsService mService;
	private RemoteTaskService mTaskService;
	private com.pm360.cepm360.services.group.RemoteTaskService mGroupService;
	private boolean mIsCurrentUser = true;

	private final String TYPE = "file_type";
	private final String TITLE = "title";
	private final String CREATER = "author";
	private final String UP_TIME = "create_time";
	private final String MARK = "mark";
	private SendEmailPresenter<B> mSendEmailPresenter;
	
	private boolean mIsTaskPermission;
	
	/**-- flag --*/
	private final int START_ATTACH_ACTIVITY_CODE = 201;
	private int mType; // 1 组合参考文档 2 计划参考文档 3 组合形象成果 4现场图文

	public CommonDocumentFragment(int type) {
		mType = type;
	}
	
	public void setTaskPermission(boolean permission) {
		mIsTaskPermission = permission;
	}
	
	/**
	 * 创建视图
	 */
	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {
		// 初始化类型和接口
		init(Files.class, mListInterface, mRequestInterface, floatingMenuInterface, mOptionMenuInterface,
				mDialogInterface);
		initEnvironment();
		switch (mType) {
		case 1:
			if (mIsTaskPermission) {
				setPermissionIdentity(GLOBAL.SYS_ACTION[3][0],
						GLOBAL.SYS_ACTION[2][0]);
			} else {
				setPermissionIdentity(GLOBAL.SYS_ACTION[35][0],
						GLOBAL.SYS_ACTION[34][0]);
			}
			
			break;
		case 2:
			setPermissionIdentity(GLOBAL.SYS_ACTION[3][0],
					GLOBAL.SYS_ACTION[2][0]);
			break;
		case 3:
			setPermissionIdentity(GLOBAL.SYS_ACTION[53][0],
					GLOBAL.SYS_ACTION[52][0]);
			break;
		case 4:
			setPermissionIdentity(GLOBAL.SYS_ACTION[5][0],
					GLOBAL.SYS_ACTION[4][0]);
			break;
		}
		
		if (mType == 3 || mType == 4) {
			mSendEmailPresenter = new SendEmailPresenter<B>(getActivity(), mCurrentProject);	
		}
		
		View view = super.onCreateView(inflater, container, savedInstanceState);
		if (mType == 3 || mType == 4) {
			if (!mIsCurrentUser) {
				if (mFloatingMenu != null) {
					mFloatingMenu.setVisibility(View.GONE);
				}
				
				if (mOptionsMenu != null) {
					mOptionsMenu.setVisibility(false);
				}
			}
		}
		
		return view;
	}
	
	@Override
	public void onResume() {
		super.onResume();
	}
	
	@Override
	public void onPause() {
		super.onPause();
	}
	
	private void initEnvironment() {
		mService = RemoteDocumentsService.getInstance();
		mTaskService = RemoteTaskService.getInstance();
		mGroupService = com.pm360.cepm360.services.group.RemoteTaskService.getInstance(); 
		mApplication = (CepmApplication) getActivity().getApplication();
	}
	
	FloatingMenuInterface floatingMenuInterface = new FloatingMenuInterface() {

		@Override
		public int[] getFloatingMenuImages() {
			int[] draw = new int[1];
			draw[0] = R.drawable.menu_icon_add;
			return draw;
		}

		@Override
		public String[] getFloatingMenuTips() {
			return new String[] { getActivity().getResources().getString(
					R.string.add)};
		}

		@Override
		public OnItemClickListener getFloatingMenuListener() {
			return mOnItemClickListener;
		}

	};
	
	OnItemClickListener mOnItemClickListener = new AdapterView.OnItemClickListener() {

		@Override
		public void onItemClick(AdapterView<?> parent, View view,
				int position, long id) {
			switch (position) {
			case 0:
				mFloatingMenu.dismiss();
				if (mParentBean == null) {
					Toast.makeText(getActivity(), R.string.pls_select_task, Toast.LENGTH_SHORT).show();
					return;
				}
				
				Intent intent = new Intent();
				if (!mIsCurrentUser && (mType == 3 || mType == 4)) {
					intent.setClass(getActivity(), AttachmentReadOnlyActivity.class);
				} else {
					intent.setClass(getActivity(), AttachmentActivity.class);
				}
				
				intent.putExtra(AttachmentActivity.KEY_PROJECT, mCurrentProject);
				intent.putExtra(AttachmentActivity.KEY_TASK_ID, mParentBean == null ? null : mParentBean.getTask_id());
				intent.putExtra(AttachmentActivity.KEY_IS_ADD_STATUS, true);
				intent.putExtra(AttachmentActivity.KEY_TASK_NAME, mParentBean == null ? null : mParentBean.getName());
				int type = 0;
				switch (mType) {
				case 1:
					type = Integer.parseInt(GLOBAL.FILE_TYPE[12][0]);
					break;
				case 2:
					type = Integer.parseInt(GLOBAL.FILE_TYPE[1][0]);
					break;
				case 3:
					type = Integer.parseInt(GLOBAL.FILE_TYPE[14][0]);
					break;
				case 4:
					type = Integer.parseInt(GLOBAL.FILE_TYPE[2][0]);
					break;
				}
				intent.putExtra(AttachmentActivity.KEY_TYPE, type);
				ArrayList<User> userList;
				if (mType == 3 || mType == 4) {
					userList = (ArrayList<User>) UploadAttachManager.getUserList(mParentBean, mParentListAdapter);
				} else {
					userList = new ArrayList<>();
				}
				
				intent.putExtra(AttachmentActivity.KEY_USER_LIST, userList);
				startActivityForResult(intent, START_ATTACH_ACTIVITY_CODE);
				break;
			}
		}
	};
	
	DialogAdapterInterface mDialogInterface = new DialogAdapterInterface() {

		@Override
		public int getDialogTitleId() {
			return R.string.update_document_file;
		}

		@Override
		public int getDialogLableNames() {
			return R.array.task_update_document_dialog_names;
		}

		@Override
		public String[] getUpdateFeilds() {
			return new String[] {TITLE, MARK};
		}

		@Override
		public Integer[] getImportantColumns(Map<String, String> saveDataMap) {
			return new Integer[] {0, 1};
		}

		@SuppressLint("UseSparseArrays") @Override
		public Map<Integer, Integer> getDialogStyles() {
			Map<Integer, Integer> buttons = new HashMap<Integer, Integer>();
			buttons.put(1, BaseDialog.remarkEditTextLineStyle);
			return buttons;
		}

		@Override
		public Map<Integer, String[]> getSupplyData() {
			return null;
		}

		@Override
		public void additionalInit(BaseDialog dialog) {
			
		}

		@Override
		public Map<String, Map<String, String>> getUpdateFieldsSwitchMap() {
			return null;
		}
		
	};
	
	OptionMenuInterface mOptionMenuInterface = new OptionMenuInterface() {

		@Override
		public int getOptionMenuNames() {
			if (mType == 1 || mType == 2) {
				return R.array.task_make_document_popup_attr;
			} else {
				return R.array.plan_file_email_option_menu;
			}
		}

		@Override
		public SubMenuListener getOptionMenuClickListener() {
			SubMenuListener listener = new SubMenuListener() {

				@Override
				public void onSubMenuClick(View view) {
					mOptionsMenu.dismiss();
					switch ((Integer) view.getTag()) {
						case 0:		// 查看 path本地路径 file_path 服务器路径
							String path = mCurrentItem.getPath();
							try {
								IntentBuilder.viewFile(getActivity(), mCurrentItem, path);
							} catch (ActivityNotFoundException e) {
								Toast.makeText(getActivity(),
										R.string.document_cannot_open,
										Toast.LENGTH_SHORT).show();
							}
							
							break;
						case 1: // 修改
							String[] editText = new String[2];
							editText[0] = mCurrentItem.getTitle();
							editText[1] = mCurrentItem.getMark(); 
							mDialog.show(editText);
							break;
						case 2:
							commonConfirmDelete();
							break;
						case 3:
							int type = 0;
							if (mType == 3) {
								type = Integer.parseInt(GLOBAL.FILE_TYPE[14][0]);
							} else if (mType == 4) {
								type = Integer.parseInt(GLOBAL.FILE_TYPE[2][0]);
							}
							mSendEmailPresenter.loadFiles(mCurrentItem, mParentBean, mParentListAdapter, getDefaultDialogValue(), mDialogLableNames, type);
							break;
					}
				}
				
			};
			return listener;
		}
		
	};

	CommonListInterface<Files> mListInterface = new DateListInterface<Files>() {

		@Override
		public Map<String, Map<String, String>> getDisplayFieldsSwitchMap() {
			Map<String, Map<String, String>> switchMap = new HashMap<String, Map<String, String>>();
			Map<String, String> map = UserCache.getUserMaps();
			switchMap.put(CREATER, map);
			return switchMap;
		}

		@Override
		public int getListItemId(Files t) {
			return t.getFile_id();
		}

		@Override
		public String[] getDisplayFeilds() {
			if (mType == 1 || mType == 2) {
				return new String[] { SERIAL_NUMBER, TYPE, TITLE, CREATER,
						UP_TIME, MARK};
			} else {
				return new String[] { SERIAL_NUMBER, TYPE, TITLE, CREATER,
						UP_TIME, MARK, "record"};
			}
			
		}

		@Override
		public int getListHeaderLayoutId() {
			return R.layout.task_make_document_list_title;
		}

		@Override
		public int getListLayoutId() {
			return R.layout.task_make_document_list_item;
		}

		@Override
		public int getListHeaderNames() {
			if (mType == 1 || mType == 2) {
				return R.array.task_document_names_title;
			} else {
				return R.array.document_names_title_with_record;
			}
		}

		@Override
		public int getListHeaderIds() {
			if (mType == 1 || mType == 2) {
				return R.array.task_document_ids_title;
			} else {
				return R.array.document_ids_title_with_record;
			}
		}

		@Override
		public int getListItemIds() {
			if (mType == 1 || mType == 2) {
				return R.array.task_document_ids_list;
			} else {
				return R.array.document_ids_list_with_record;
			}
		}

		@Override
		public String getDateFormat() {
			return DateUtils.FORMAT_LONG1;
		}

	};
	DocumentServiceInterface<Files, File> mRequestInterface = new DocumentServiceInterface<Files, File>() {

		@Override
		public void getListData() {
			if (checkParentBeanForQuery()) {

				if (mType == 1) {
					mGroupService.setTask((ZH_group_task) mParentBean);
					mGroupService.getReferenceFiles(getServiceManager(),
							mCurrentProject.getTenant_id());
				} else if (mType == 2) {
					mTaskService.setTask((Task) mParentBean);
					mTaskService.getReferenceFiles(getServiceManager(),
							mCurrentProject.getTenant_id());
				} else if (mType == 3) {
					mGroupService.setTask((ZH_group_task) mParentBean);
					mGroupService.getXingXiangFiles(getServiceManager(),
							mCurrentProject.getTenant_id());
				} else if (mType == 4) {
					Task task = new Task();
					task.setTask_id(mParentBean.getTask_id());
					mTaskService.setTask(task);
					mTaskService.getXianChangFiles(getServiceManager(), 
							mCurrentProject.getTenant_id());
				}
			}
		}

		@Override
		public void addItem(Files t) {
//			ProgressDialog progressDialog = new ProgressDialog(getActivity());
//			progressDialog.setProgressStyle(ProgressDialog.STYLE_HORIZONTAL);
//			progressDialog.setMessage(getActivity()
//					.getString(R.string.document_uploading));
//			progressDialog.setCancelable(true);
//			mService.uploadFile(getServiceManager(), progressDialog, t,
//					mApplication.getProject().getTenant_id(), new File(""),
//					PlanDataCache.getInstance().getCompanyUserList());
		}

		@Override
		public void deleteItem(Files t) {
			mService.deleteFile(getServiceManager(), t, 0);
		}

		@Override
		public void updateItem(Files t) {
			RemoteDocumentsService.getInstance().updateFile(getServiceManager(), t, new ArrayList<User>());
		}

		@Override
		public void uploadDocument(Files files, File file) {

		}

	};
	
	
	@Override
	protected void doExtraGetServerData(ResultStatus status, List<?> list) {
		if (status.getCode() == AnalysisManager.SUCCESS_DB_ADD) {
			sendMessage(DISMISS_PROGRESS_DIALOG);
		}
	}
	
	@Override
	protected boolean doExtraInitListViewItem(int line, int position, ViewHolder holder) {
		if (position == 1) {
			holder.ivs[0].setImageResource(FileIconHelper.getIcon((Files)mListAdapter.getItem(line)));
			return true;
		} else {
			return false;
		}
	}
	
	@Override
	protected boolean doExtraInitLayout(View convertView, ViewHolder holder, int position) {
		if (position == 1) {
			holder.ivs = new ImageView[1];
			holder.ivs[0] = (ImageView)convertView.findViewById(R.id.file_image);
			return true;
		} else {
			return false;
		}
		
	}
	
	@Override
	protected boolean doExtraRegListener(ViewHolder holder, final int position, int i) {
		if (i == 1) {
			return true;
		} else {
			String[] displays = mListInterface.getDisplayFeilds();
			if (displays[i].equals("record")) {
				Drawable drawable = getActivity().getResources().getDrawable(
						R.drawable.mailbox);
				drawable.setBounds(0, 0, 30, 30);
				holder.tvs[i].setCompoundDrawables(drawable, null, null, null);
				holder.tvs[i].setOnClickListener(new OnClickListener() {

					@Override
					public void onClick(View v) {
						mCurrentItem = mListAdapter.getItem(position);
						mListAdapter.setSelected(position, true);
						String mailType = "0";
						if (mType == TYPE_ZH_FEEDBACK_DOC) {
							mailType = GLOBAL.MAIL_TABLE_TYPE[1][0];
						} else if (mType == TYPE_SCHEDULE_FEEDBACK_DOC) {
							mailType = GLOBAL.MAIL_TABLE_TYPE[1][0];
						}
						RemoteCommonService.getInstance().getMailByType(new DataManagerInterface() {
							
							@SuppressWarnings("unchecked")
							@Override
							public void getDataOnResult(ResultStatus status, List<?> list) {
								Intent intent = new Intent(getActivity(), 
										ListSelectActivity.class);
								intent.putExtra(ListSelectActivity.FRAGMENT_KEY, 
										MailboxViewFragment.class);
								intent.putExtra(ListSelectActivity.SHOW_DATA, 
										(ArrayList<MailBox>) list);
								intent.putExtra(ListSelectActivity.SELECT_MODE_KEY, false);
								startActivity(intent);
							}
						}, mailType, mCurrentItem.getTask_id());
					}
				});
				return true;
			} else {
				return false;
			}
		}
	}
	
	@Override
	public void handleParentEvent(B b) {
		// 门户界面，由于界面初始化project为null，需要在此处重新设置
		if (mSendEmailPresenter != null) {
			mSendEmailPresenter.setCurrentProject(mCurrentProject);
		}
		mRequestInterface.getListData();
		if (b != null && (mType == 3 || mType == 4)) {
			if (UserCache.getCurrentUser().getUser_id() != b.getOwner()) {
				mIsCurrentUser = false;
				if (mFloatingMenu != null) {
					mFloatingMenu.setVisibility(View.GONE);
				}
				
				if (mOptionsMenu != null) {
					mOptionsMenu.setVisibileMenu(new int[] {1, 2, 3}, false);
				}
			} else {
				mIsCurrentUser = true;
				if (mFloatingMenu != null) {
					mFloatingMenu.setVisibility(View.VISIBLE);
				}
				
				if (mOptionsMenu != null) {
					mOptionsMenu.setVisibileMenu(new int[] {1, 2, 3}, true);
				}
				
			}
		}
	}

	private DataTreeListAdapter<B> mParentListAdapter;
	@Override
	public void setParentList(DataTreeListAdapter<B> listAdapter) {
		mParentListAdapter = listAdapter;
	}
	
	@Override
	public void onActivityResult(int requestCode, int resultCode, Intent data) {
		super.onActivityResult(requestCode, resultCode, data);
		if (resultCode != Activity.RESULT_OK)
			return;
		if (requestCode == START_ATTACH_ACTIVITY_CODE) {
			mRequestInterface.getListData();
		}
		
	}
	
	@Override
	public boolean isChildHandleFloatingMenuOnly() {
		if (mType == 3 || mType == 4) {
			return true;
		} else {
			return super.isChildHandleFloatingMenuOnly();
		}
		
	}
}

