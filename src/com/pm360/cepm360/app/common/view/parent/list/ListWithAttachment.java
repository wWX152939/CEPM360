package com.pm360.cepm360.app.common.view.parent.list;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.graphics.drawable.Drawable;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.AdapterView;

import com.pm360.cepm360.R;
import com.pm360.cepm360.app.common.adpater.DataListAdapter.ViewHolder;
import com.pm360.cepm360.app.common.custinterface.CommonListInterface;
import com.pm360.cepm360.app.common.custinterface.OperationMode;
import com.pm360.cepm360.app.common.custinterface.ServiceInterface;
import com.pm360.cepm360.app.common.custinterface.SimpleServiceInterface;
import com.pm360.cepm360.app.common.view.parent.BaseDialog;
import com.pm360.cepm360.app.module.common.attachment.AttachmentActivity;
import com.pm360.cepm360.app.module.common.attachment.AttachmentReadOnlyActivity;
import com.pm360.cepm360.app.module.document.DocumentUploadView;
import com.pm360.cepm360.app.module.document.DocumentUploadView.GetAttachmentInterface;
import com.pm360.cepm360.app.module.document.DocumentUploadView.SeleteFileInterface;
import com.pm360.cepm360.app.module.document.DocumentUploadView.UploadInterface;
import com.pm360.cepm360.app.module.document.DocumentUploadView.WindowInterface;
import com.pm360.cepm360.app.module.email.MailBoxUtils;
import com.pm360.cepm360.app.utils.UtilTools;
import com.pm360.cepm360.entity.Files;
import com.pm360.cepm360.entity.User;
import com.pm360.cepm360.services.document.RemoteDocumentsService;

import java.io.Serializable;
import java.util.List;
import java.util.Map;

public abstract class ListWithAttachment<T extends Serializable>
										extends ListWithOptionMenu<T> {

	// 注意请求码的唯一性
	public static final int FILE_SELECT_REQUEST_CODE = 10000;
	
	// 文档上传View
	private DocumentUploadView mDocumentUploadView;
	
	// 要添加的清单附件
	private String mAttachments;
	private List<Files> mFilesList;
	
	// 附件服务接口
	protected ServiceInterface<T> mAttchmentServiceInterface;
	
	/**
	 * 设置当前清单项的附件
	 * @param files
	 */
	public void setFileList(List<Files> files) {
		mFilesList = files;
		
		setAttachments(mCurrentItem, MailBoxUtils.buildAttachmentIds(mFilesList));
		((ServiceInterface<T>) mServiceImplement).updateItem(mCurrentItem);
	}
	
	/**
	 * 构造函数必须有
	 * @param context
	 */
	public ListWithAttachment(Context context) {
		super(context);
		
		/**
		 * 注意该服务接口的实现只为附件添加信息
		 */
		mServiceImplement = new ServiceInterface<T>() {

			@Override
			public void getListData() {
				mAttchmentServiceInterface.getListData();
			}

			@Override
			public void addItem(final T t) {
				
				// 设置附件字段
				setAttachments(t, mAttachments);
				
				mAttchmentServiceInterface.addItem(t);
			}

			@Override
			public void deleteItem(T t) {
				mAttchmentServiceInterface.deleteItem(t);
			}

			@Override
			public void updateItem(T t) {
				mAttchmentServiceInterface.updateItem(t);
			}
		};
	}
	
	/**
	 * 简单初始化
	 * @param context
	 * @param listItemClass
	 * @param listInterface
	 * @param serviceInterface
	 */
	@Override
	public void init(Class<T> listItemClass,
						CommonListInterface<T> listInterface,
						SimpleServiceInterface<T> serviceInterface) {
		super.init();
		mListItemClass = listItemClass;
		mListImplement = listInterface;
		mAttchmentServiceInterface = (ServiceInterface<T>) serviceInterface;
		
		// 初始化显示域并映射
		initFeildsAndMap();
		
		// 初始化视图
		initLayout();
		
		// 加载数据
		loadData();
	}
	
	@Override
	protected void floatingMenuItemClick(AdapterView<?> parent, View view,
			int position, long id) {
		switch (position) {
	        case 0:
	        	mIsAddOperation = mIsFloatMenuAdd = true;
	        	mDocumentUploadView.setDefaultData(true);
	        	mDialog.show(null);
	            mFloatingMenu.dismiss();
	            break;
	        default:
	            break;
		}
	}
	
	/**
	 * 上传文件列表
	 * @param newT
	 * @param dataT
	 * @param typeId
	 */
	public void uploadFilesList(T newT, T dataT, int typeId) {
		int id = mListImplement.getListItemId(dataT);
		setId(newT, id);
		
		// 循环上传文件
		for (int i = 0; i < mFilesList.size(); i++) {
			mFilesList.get(i).setType_id(typeId);
			RemoteDocumentsService.getInstance()
					.updateFile(null, mFilesList.get(i));
		}
	}

	/**
	 * 对话框中添加附件视图控件，一定要在对话框接口的additionalInit中调用
	 * @param dialog
	 */
	protected void addAttachmentView(final BaseDialog dialog) {
		UploadInterface uploadInterface = new UploadInterface() {
			
			@Override
			public List<User> getUserList() {
				return null;
			}
			
			@Override
			public Files getPreFile() {
				Files files = new Files();
				files.setDir_type(getFileType());
				
				if (!mIsAddOperation) {
					files.setType_id(mListImplement.getListItemId(mCurrentItem));
				}
				
				files.setProject_id(getProjectId());
				return files;
			}
			
			@Override
			public boolean getIsUpload() {
				return true;
			}
			
			@Override
			public void dismiss(List<Files> fileList) {
				
				// 添加模式下会使用
				mFilesList = fileList;
				
				mAttachments = MailBoxUtils.buildAttachmentIds(fileList);
				if (!mIsAddOperation) {
					setAttachments(mCurrentItem, mAttachments);
				}
				
				// 如果是添加，在调用服务添加接口前调用
				if (dialogSaveButtonEvent()) {
					mDialog.dismiss();
				}
				
				if (mFloatingMenu != null) {
					mFloatingMenu.dismiss();
				}
			}
		};
		
		GetAttachmentInterface getAttachmentInterface = new GetAttachmentInterface() {
											
			@Override
			public String getAttachment() {
				return mCurrentItem == null ? null : getAttachments(mCurrentItem);
			}
		};
		
		WindowInterface windowInterface = new WindowInterface() {
			
			@Override
			public boolean noTitleAndButtonWindow() {
				return true;
			}
		};
		
		mDocumentUploadView = new DocumentUploadView((Activity) mContext, 
				uploadInterface, getAttachmentInterface, windowInterface);
		
		mDocumentUploadView.initializeWindow();
		mDocumentUploadView.setSelectFileInterface(new SeleteFileInterface() {
			
			@Override
			public int getStartActivityCodeNum() {
				return FILE_SELECT_REQUEST_CODE;
			}
		});
		
		// 添加到对话框
		dialog.addView(mDocumentUploadView.getView());
		
		// 重写对话框保存按钮监听
		mDialogSaveListener = new OnClickListener() {
			
			@Override
			public void onClick(View v) {
				Map<String, String> saveMap = dialog.SaveData();
				Integer[] columns = mDialogImplement.getImportantColumns(saveMap);
				
				if (columns != null) {
					for (Integer column : columns) {
						if (saveMap.get(mDialogLableNames[column]).equals("")) {
							sendMessage(SHOW_TOAST, 
									mContext.getString(R.string.pls_input_all_info));
								return;
						}
					}
				}
				
				mDocumentUploadView.uploadButtonEvent(false);
			}
		};
	}
	
	@Override
	protected void showUpdateDialog(boolean isEdit) {
		mDocumentUploadView.setDefaultData(false);
		super.showUpdateDialog(isEdit);
	}
	
	@Override
	protected void initListViewItemMore(ViewHolder holder, int position,
			Map<String, String> displayFieldMap) {
		super.initListViewItemMore(holder, position, displayFieldMap);
		
		int attachmentIndex = mDisplayFields.length - 1;
		String attachmentString = displayFieldMap.get(mDisplayFields[attachmentIndex]);
		
		if (attachmentString != null && !attachmentString.isEmpty()) {
			String[] attachments = attachmentString.split(",");
			holder.tvs[attachmentIndex].setText(attachments.length + "");
			
			Drawable drawable = mContext.getResources().getDrawable(R.drawable.email_attachment);
			
			//必须设置图片大小，否则不显示
			drawable.setBounds(0, 0, drawable.getMinimumWidth(),
									 drawable.getMinimumHeight());
			
			holder.tvs[attachmentIndex].setCompoundDrawablePadding(UtilTools.dp2pxH(mContext, 8));
			holder.tvs[attachmentIndex].setCompoundDrawables(drawable, null, null, null);
		} else {
			holder.tvs[attachmentIndex].setCompoundDrawables(null, null, null, null);
		}
	}
	
	@Override
	protected void clickRegister(ViewHolder viewHolder, final int position) {
		super.clickRegister(viewHolder, position);
		
		viewHolder.tvs[viewHolder.tvs.length-1].setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View view) {
				mCurrentItem = mListAdapter.getItem(position);
				mListAdapter.setSelected(position, true);
				
				Intent intent = null;
				if (mCurrentMode == OperationMode.NORMAL) {
					intent = new Intent(mContext, AttachmentActivity.class);
				} else {
					intent = new Intent(mContext, AttachmentReadOnlyActivity.class);
				}
				
				// 设置文件类型和文件列表
				intent.putExtra(AttachmentActivity.KEY_CURRENT_BEAN_ATTACHMENT, 
							getAttachments(mCurrentItem));
				
				intent.putExtra(AttachmentActivity.KEY_TYPE, getFileType());
				intent.putExtra(AttachmentActivity.KEY_IS_UPLOAD_STATUS, true);
				
				if (mCurrentMode == OperationMode.NORMAL) {
					((Activity) mContext).startActivityForResult(intent, FILE_SELECT_REQUEST_CODE);
				} else {
					((Activity) mContext).startActivity(intent);
				}
			}
		});
	}
	
	/**
	 * 转发返回数据
	 * @param requestCode
	 * @param resultCode
	 * @param data
	 */
	public void OnActivityResult(int requestCode, int resultCode, Intent data) {
		mDocumentUploadView.onActivityResult(requestCode, resultCode, data);
	}
	
	/**
	 * 获取附件类型
	 * @return
	 */
	public abstract int getFileType();
	
	/**
	 * 设置ID
	 * @param t
	 * @param id
	 */
	public abstract void setId(T t, int id);
	
	/**
	 * 获取附件字符串
	 * @return
	 */
	public abstract String getAttachments(T t);
	
	/**
	 * 设置指定对象的附件字段
	 * @param t
	 * @param attachments
	 * @return
	 */
	public abstract String setAttachments(T t, String attachments);
	
	/**
	 * 获取项目ID
	 * @return
	 */
	public abstract int getProjectId();
	
	/**
	 * 水印，如果需要水印功能实现该接口
	 * @author yuanlu
	 *
	 */
	public interface WaterMarker<T> {
		
		/**
		 * 获取水印映射表
		 * @param t	对于列表而言，唯一区别就是当前列表项，因此要作为参数传递
		 * @return
		 */
		public Map<Integer, String> getWaterMarkerMap(T t);
	}
}
