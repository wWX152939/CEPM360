package com.pm360.cepm360.app.module.contract.pager;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.View.OnLongClickListener;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.ImageView;
import android.widget.TextView;

import com.pm360.cepm360.R;
import com.pm360.cepm360.app.cache.UserCache;
import com.pm360.cepm360.app.common.activity.DataManagerInterface;
import com.pm360.cepm360.app.common.adpater.DataListAdapter.ViewHolder;
import com.pm360.cepm360.app.common.custinterface.CallBack;
import com.pm360.cepm360.app.common.custinterface.CommonListInterface;
import com.pm360.cepm360.app.common.custinterface.DialogAdapterInterface;
import com.pm360.cepm360.app.common.custinterface.OperationMode;
import com.pm360.cepm360.app.common.custinterface.OptionMenuInterface;
import com.pm360.cepm360.app.common.custinterface.ServiceInterface;
import com.pm360.cepm360.app.common.custinterface.ViewPagersInterface;
import com.pm360.cepm360.app.common.view.OptionsMenuView.SubMenuListener;
import com.pm360.cepm360.app.common.view.parent.BaseDialog;
import com.pm360.cepm360.app.common.view.parent.list.ListWithOptionMenu;
import com.pm360.cepm360.app.common.view.parent.list.PermissionManager;
import com.pm360.cepm360.app.module.document.DocumentUploadActivity;
import com.pm360.cepm360.app.utils.FileIconHelper;
import com.pm360.cepm360.app.utils.IntentBuilder;
import com.pm360.cepm360.common.GLOBAL;
import com.pm360.cepm360.common.ResultStatus;
import com.pm360.cepm360.common.json.AnalysisManager;
import com.pm360.cepm360.common.util.DateUtils;
import com.pm360.cepm360.common.util.MiscUtils;
import com.pm360.cepm360.entity.Contract_change;
import com.pm360.cepm360.entity.Files;
import com.pm360.cepm360.entity.User;
import com.pm360.cepm360.services.document.RemoteDocumentsService;
import com.pm360.cepm360.services.expenses.RemoteChangeContractService;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class ContractChangeAttachment extends ListWithOptionMenu<Files> 
									implements ViewPagersInterface<Contract_change> {
	
	// 文件选择请求码
	public static final int FILE_SELECT_REQEUST_CODE = 7000;
	
	// 该类索引，标识该类的回调调用
	public static final int CONTRACT_CHANGE_COMMON_INDEX = 7100;
	
	// 服务
	private RemoteChangeContractService mChangeContractService 
						= RemoteChangeContractService.getInstance();
	private RemoteDocumentsService mDocumentsService
						= RemoteDocumentsService.getInstance();
	
	// 当前合同对象
	private Contract_change mContractChange;
	
	// 收入或支出合同
	private boolean mIsIncomeContract;
	private boolean mHasModify;
	private boolean mIsAddMode;
	
	// 用于添加附件操作
	private int mAddCount;
	
	
	/**
	 * 构造函数，初始化列表
	 * @param context
	 * @param isModify
	 * @param isAddMode
	 * @param hasSildePanel
	 */
	public ContractChangeAttachment(Context context,
			boolean isModify, boolean isAddMode, boolean hasSildePanel) {
		super(context);
		mIsAddMode = isAddMode;
		commonInit(isModify, serviceInterface, hasSildePanel);
	}
	
	/**
	 * 构造函数，初始化列表
	 * @param context
	 * @param serviceInterface
	 * @param hasSildePanel
	 */
	public ContractChangeAttachment(Context context,
			ServiceInterface<Files> serviceInterface, 
			boolean hasSildePanel) {
		super(context);
		mIsAddMode = true;
		commonInit(true, serviceInterface, hasSildePanel);
	}
	
	/**
	 * 初始化方法，不能使用this()方法
	 * @param isModify
	 * @param serviceInterface
	 * @param hasSildePanel
	 */
	private void commonInit(boolean isModify, 
					ServiceInterface<Files> serviceInterface, 
					boolean hasSildePanel) {
		
		// 启动初始化流程
		if (isModify) {
			setPermission(GLOBAL.SYS_ACTION[29][0], GLOBAL.SYS_ACTION[28][0], 
					PermissionManager.PERMISSION_TYPE_SYS);
			
			init(Files.class, listInterface, serviceInterface, 
						dialogAdapterInterface, optionMenuInterface, null);
			
			// 重新设置浮动菜单监听器
			initFloatingMenuItemListener();
			
			// 设置浮动菜单位置
			setFloatingMenuLocation(hasSildePanel);
			
			mFloatingMenu.setOnClickListener(new OnClickListener() {
				
				@Override
				public void onClick(View v) {
					mFloatingMenu.dismiss();
					
					Intent intent = new Intent(mContext, DocumentUploadActivity.class);
					intent.putExtra(DocumentUploadActivity.NEED_UPLOAD_KEY, !mIsAddMode);
					
					Files files = new Files();
					if (mContractChange != null) {
						files.setType_id(mContractChange.getId());
					}
					files.setDir_type(Integer.parseInt(GLOBAL.FILE_TYPE[32][0]));
					intent.putExtra(DocumentUploadActivity.UPLOAD_FILE_KEY, files);
					((Activity) mContext).startActivityForResult(intent, 
							FILE_SELECT_REQEUST_CODE);
				}
			});
		} else {
			init(Files.class, listInterface, serviceInterface,
					dialogAdapterInterface, optionMenuReadOnlyInterface);
		}
	}
	
	/**
	 * 选项菜单定义
	 */
	OptionMenuInterface optionMenuReadOnlyInterface = new OptionMenuInterface() {
		
		@Override
		public int getOptionMenuNames() {
			return R.array.attachment_option_menu_readonly;
		}
		
		@Override
		public SubMenuListener getOptionMenuClickListener() {
			return new SubMenuListener() {
				
				@Override
				public void onSubMenuClick(View view) {
					mOptionsMenu.dismiss();
					
					switch ((Integer) view.getTag()) {
						case 0:	// 属性
							mIsAddOperation = false;
							showUpdateDialog(false);
							break;
						case 1:	// 查看
							IntentBuilder.view(mContext, getCurrentItem(), 
									GLOBAL.CONTRACT_CHANGE_DOWNLOAD_FILE_DIR);
							break;
					}
				}
			};
		}
	};
	
	/**
	 * 选项菜单定义
	 */
	OptionMenuInterface optionMenuInterface = new OptionMenuInterface() {
		
		@Override
		public int getOptionMenuNames() {
			return R.array.attachment_option_menu_names;
		}
		
		@Override
		public SubMenuListener getOptionMenuClickListener() {
			return new SubMenuListener() {
				
				@Override
				public void onSubMenuClick(View view) {
					mOptionsMenu.dismiss();
					
					switch ((Integer) view.getTag()) {
						case 0:	// 属性
							mIsAddOperation = false;
							showUpdateDialog(true);
							break;
						case 1:		// 查看
							IntentBuilder.view(mContext, getCurrentItem(), 
									GLOBAL.CONTRACT_CHANGE_DOWNLOAD_FILE_DIR);
							break;
						case 2:
							commonConfirmDelete();
							break;
					}
				}
			};
		}
	};
	
	/**
	 * 设置浮动菜单监听器
	 */
	private void initFloatingMenuItemListener() {
		
		/**
		 * 浮动菜单的相应处理
		 */
		OnItemClickListener itemClickListener = new OnItemClickListener() {

			@Override
			public void onItemClick(AdapterView<?> parent, View view,
					int position, long id) {
				switch (position) {
					case 0:
						mFloatingMenu.dismiss();
						
						Intent intent = new Intent(mContext, DocumentUploadActivity.class);
						intent.putExtra(DocumentUploadActivity.NEED_UPLOAD_KEY, !mIsAddMode);
						
						Files files = new Files();
						files.setType_id(mContractChange.getId());
						files.setDir_type(Integer.parseInt(GLOBAL.FILE_TYPE[32][0]));
						intent.putExtra(DocumentUploadActivity.UPLOAD_FILE_KEY, files);
						((Activity) mContext).startActivityForResult(intent, 
								FILE_SELECT_REQEUST_CODE);
						break;
				default:
					break;
				}
			}
		};
		
		setItemClickListener(itemClickListener);
	}
	
	/**
	 * 列表接口
	 */
	private CommonListInterface<Files> listInterface 
								= new CommonListInterface<Files>() {

		@Override
		public Map<String, Map<String, String>> getDisplayFieldsSwitchMap() {
			Map<String, Map<String, String>> dispayFieldsMap 
							= new HashMap<String, Map<String,String>>();
			
			
			dispayFieldsMap.put("author", UserCache.getUserMaps());
			return dispayFieldsMap;
		}

		@Override
		public int getListItemId(Files t) {
			return t.getId();
		}

		@Override
		public String[] getDisplayFeilds() {
			return new String[] {
					"serial", 
					"title",
					"author",
					"create_time",
					"mark"
			};
		}

		@Override
		public int getListHeaderLayoutId() {
			return R.layout.attachment_list_header_layout;
		}

		@Override
		public int getListLayoutId() {
			return R.layout.attachment_list_layout;
		}

		@Override
		public int getListHeaderNames() {
			return R.array.task_document_names_title;
		}

		@Override
		public int getListHeaderIds() {
			return R.array.task_document_ids_title;
		}

		@Override
		public int getListItemIds() {
			return R.array.task_document_ids_list;
		}
	};
	
	/**
	 * 服务接口实现
	 */
	protected ServiceInterface<Files> serviceInterface 
							= new ServiceInterface<Files>() {

		@Override
		public void getListData() {
			String attachment = "null";
			if (mContractChange.getAttachments() != null 
						&& !mContractChange.getAttachments().isEmpty()) {
				attachment = mContractChange.getAttachments();
			}
			
			mChangeContractService.getChangeContractFiles(getServiceManager(), 
							mContractChange.getId(), attachment);
		}

		@Override
		public void addItem(Files t) {
			t.setTenant_id(UserCache.getCurrentUser().getTenant_id());
			t.setType_id(mContractChange.getId());
			t.setDir_type(Integer.parseInt(GLOBAL.FILE_TYPE[32][0]));
			
			mDocumentsService.addFile(getServiceManager(), t, new ArrayList<User>(), 
					UserCache.getCurrentUser().getUser_id());
		}

		@Override
		public void deleteItem(Files t) {
			mDocumentsService.deleteFile(getServiceManager(), t, 0);
		}

		@Override
		public void updateItem(Files t) {
			mDocumentsService.updateFile(getServiceManager(), t);
		}
	};
	
	/**
	 * 选项菜单对话框接口实现
	 */
	DialogAdapterInterface dialogAdapterInterface = new DialogAdapterInterface() {
		
		@Override
		public Integer[] getImportantColumns(Map<String, String> saveDataMap) {
			return null;
		}
		
		@Override
		public String[] getUpdateFeilds() {
			return new String[] {
					"code",	// 编号
					"title",	// 标题
					"author",	// 创建人
					"create_time",	// 上传时间
					"modified_time",	// 修改时间
					"mark"	// 备注
			};
		}
		
		@Override
		public int getDialogTitleId() {
			return R.string.document_properties_title;
		}
		
		@Override
		public int getDialogLableNames() {
			return R.array.attachment_properties_names;
		}
		
		@Override
		public Map<String, Map<String, String>> getUpdateFieldsSwitchMap() {
			return null;
		}
		
		@Override
		public Map<Integer, String[]> getSupplyData() {
			return null;
		}
		
		@SuppressLint("UseSparseArrays") 
		@Override
		public Map<Integer, Integer> getDialogStyles() {
			Map<Integer, Integer> stylesMap = new HashMap<Integer, Integer>();
			stylesMap.put(0, BaseDialog.editTextReadOnlyLineStyle);
			stylesMap.put(2, BaseDialog.userSelectLineStyle);
			stylesMap.put(3, BaseDialog.editTextReadOnlyLineStyle);
			stylesMap.put(4, BaseDialog.editTextReadOnlyLineStyle);
			return stylesMap;
		}
		
		@Override
		public void additionalInit(BaseDialog dialog) {
			
		}
	};
	
	/**
	 * 浮动菜单子菜单项点击处理方法
	 */
	@Override
	protected void floatingMenuItemClick(AdapterView<?> parent, View view,
										int position, long id) {
		switch (position) {
			case 0:
				mFloatingMenu.dismiss();
				
				Intent intent = new Intent(mContext, DocumentUploadActivity.class);
				Files files = new Files();
				
				// 设置文档所属类型
				if (mIsIncomeContract) {
					files.setDir_type(Integer.parseInt(GLOBAL.FILE_TYPE[3][0]));
				} else {
					files.setDir_type(Integer.parseInt(GLOBAL.FILE_TYPE[4][0]));
				}
				
				intent.putExtra("file", files);
				intent.putExtra("is_upload", false);
				
				((Activity) mContext).startActivityForResult(intent, 
										FILE_SELECT_REQEUST_CODE);
				break;
			default:
				break;
		}
	}

	/**
	 * 如果作为ViewPager一个页布局视图，该方法用来切换父节点时更新页面
	 */
	@SuppressWarnings("unchecked")
	@Override
	public void handleParentEvent(Contract_change b) {
		if (mContext instanceof CallBack) {
			((CallBack<Void, Integer>) mContext).callBack(CONTRACT_CHANGE_COMMON_INDEX);
		}
		
		mContractChange = b;
		super.loadData();
	}
	
	/**
	 * 上传文件列表
	 * @param fileList
	 */
	public void uploadFileList(List<Files> fileList) {
		mAddCount = fileList.size();
		
		for (int i = 0; i < fileList.size(); i++) {
			serviceInterface.addItem(fileList.get(i));
		}
	}
	
	/**
	 * 添加文件列表到适配器
	 * @param fileList
	 */
	public void addFileListToAdapter(List<Files> fileList) {
		mAddCount = fileList.size();
		
		for (int i = 0; i < fileList.size(); i++) {
			ResultStatus status = new ResultStatus(AnalysisManager.SUCCESS_DB_ADD, 
					GLOBAL.DB_ADD_SUCCESSFUL);
			List<Files> list = new ArrayList<Files>();
			list.add(fileList.get(i));
			handleDataOnResult(status, list);
		}
	}
	
	/**
	 * 设置合同变更
	 * @param contractChange
	 */
	public void setContractChange(Contract_change contractChange) {
		mContractChange = contractChange;
	}
	
	/**
	 * 这里重载该函数，不做任何加载操作，因为当前页不是要在初始化后直接
	 * 加载数据，而是显示的时候通过handleParentEvent加载
	 */
	@Override
	protected void loadData() {
		
	}
	
	@Override
	protected String[] getDefaultDialogValue() {
		Map<String, String> listItemMap = MiscUtils
				.beanToMap(mCurrentItem, DateUtils.FORMAT_LONG1);
		
		// 构建默认值数组
		String[] defaultValues = new String[mUpdateFields.length];
		for (int i = 0; i < mUpdateFields.length; i++) {
			if (mUpdateFieldSwitchMap != null &&
					(mUpdateFieldSwitchMap.get(mUpdateFields[i]) != null)) {
				defaultValues[i] = mUpdateFieldSwitchMap.get(mUpdateFields[i])
										.get(listItemMap.get(mUpdateFields[i]));
			} else {
				defaultValues[i] = listItemMap.get(mUpdateFields[i]);
			}
		}
		
		return defaultValues;
	}

	@Override
	protected void clickRegister(final ViewHolder viewHolder, final int position) {
		for (int i = 0; i < mListItemIds.length; i++ ) {
			View view = null;
			
			if (i == 0) {
				view = viewHolder.tvs[0];
			} else if (i == 1) {
				view = viewHolder.ivs[0];
			} else {
				view = viewHolder.tvs[i-1];
			}
			
			// 单击监听
			view.setOnClickListener(new OnClickListener() {
				
				@Override
				public void onClick(View view) {
					clickHandler(viewHolder, view, position);
				}
			});
			
			// 多选模式下的处理
			multiModeHandle(viewHolder, position);
			
			// 正常模式下，注册长按监听器
			if (OperationMode.NORMAL == mCurrentMode) {
				
				// 注册长按监听
                view.setOnLongClickListener(new OnLongClickListener() {
					
					@Override
					public boolean onLongClick(View view) {
						longClickHandler(view, position);
						return true;
					}
				});
			}
		}
	}

	@Override
	protected void initLayoutMore(View convertView, ViewHolder holder) {
		for (int i = 0; i < mListItemIds.length; i++) {
			if (i == 0) {
				holder.tvs[i] = (TextView) 
						convertView.findViewById(mListItemIds[i]);
			} else if (i == 1) {
				holder.ivs = new ImageView[1];
				holder.ivs[0] = (ImageView) 
						convertView.findViewById(mListItemIds[i]);
			} else {
				holder.tvs[i-1] = (TextView) 
						convertView.findViewById(mListItemIds[i]);
			}
		}
	}
	
	@Override
	protected void displayFieldRemap(Map<String, String> displayFieldMap,
			Files t, int position) {
		super.displayFieldRemap(displayFieldMap, t, position);
		displayFieldMap.put("create_time", DateUtils
				.dateToString(DateUtils.FORMAT_LONG1, t.getCreate_time()));
	}

	@Override
	protected void initListViewItemMore(ViewHolder holder, int position,
					Map<String, String> displayFieldMap) {
		super.initListViewItemMore(holder, position, displayFieldMap);
		holder.ivs[0].setImageResource(FileIconHelper.getFileIcon(mListAdapter
				.getItem(position).getFile_type()));
	}
	
	@Override
	public void handleDataOnResult(ResultStatus status, List<?> list) {
		super.handleDataOnResult(status, list);
		
		switch (status.getCode()) {
			case AnalysisManager.SUCCESS_DB_ADD:
				Files files = (Files) list.get(0);
				StringBuilder attachments = new StringBuilder();
				String attachmentString = mContractChange.getAttachments();
				
				if (attachmentString == null || attachmentString.isEmpty()) {
					attachments.append(files.getFile_id());
				} else {
					attachments.append(attachmentString);
					attachments.append(",");
					attachments.append(files.getFile_id());
				}
				mContractChange.setAttachments(attachments.toString());
				
				if (--mAddCount == 0) {
					mHasModify = true;
					mChangeContractService.updateChangeContract(contractUpdateInterface, mContractChange);
				}
				break;
			case AnalysisManager.SUCCESS_DB_DEL:
				String attachment = mContractChange.getAttachments();
				String[] attachmentParts = attachment.split(",");
				StringBuilder sb = new StringBuilder();
				for (int i = 0; i < attachmentParts.length; i++) {
					if (attachmentParts[i].equals(mCurrentItem.getFile_id() + "")) {
						attachmentParts[i] = null;
					}
					
					if (attachmentParts[i] != null) {
						sb.append(attachmentParts[i] + ",");
					}
				}
				
				// 不为空设置
				if (!sb.toString().isEmpty()) {
					String string = sb.subSequence(0, sb.length() - 1).toString();
					mContractChange.setAttachments(string);
				} else {
					mContractChange.setAttachments(sb.toString());
				}
				
				mHasModify = true;
				break;
			default:
				break;
		}
	}
	
	/**
	 * 合同更新回调接口
	 */
	DataManagerInterface contractUpdateInterface = new DataManagerInterface() {
		
		@Override
		public void getDataOnResult(ResultStatus status, List<?> list) {
			sendMessage(DISMISS_PROGRESS_DIALOG);
			sendMessage(SHOW_TOAST, status.getMessage());
		}
	};
	
	/**
	 * 附件是否有修改
	 * @return
	 */
	public boolean hasModify() {
		return mHasModify;
	}
}