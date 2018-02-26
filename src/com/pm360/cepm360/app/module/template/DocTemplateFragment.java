package com.pm360.cepm360.app.module.template;

import android.app.Activity;
import android.app.Dialog;
import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import com.pm360.cepm360.R;
import com.pm360.cepm360.app.cache.UserCache;
import com.pm360.cepm360.app.common.custinterface.CommonListInterface;
import com.pm360.cepm360.app.common.custinterface.OptionMenuInterface;
import com.pm360.cepm360.app.common.custinterface.ServiceInterface;
import com.pm360.cepm360.app.common.fileexplorer.FileExplorerActivity;
import com.pm360.cepm360.app.common.view.OptionsMenuView;
import com.pm360.cepm360.app.common.view.OptionsMenuView.SubMenuListener;
import com.pm360.cepm360.app.common.view.parent.BaseListFragment;
import com.pm360.cepm360.app.module.common.attachment.UploadAttachManager;
import com.pm360.cepm360.app.utils.IntentBuilder;
import com.pm360.cepm360.common.GLOBAL;
import com.pm360.cepm360.common.util.FileUtils;
import com.pm360.cepm360.entity.Files;
import com.pm360.cepm360.entity.User;
import com.pm360.cepm360.services.document.RemoteDocumentsService;
import com.pm360.cepm360.services.templet.RemoteDocumentService;

import java.io.File;
import java.util.ArrayList;
import java.util.Map;

public class DocTemplateFragment extends BaseListFragment<Files> {
	
	// 本地文件请求码
	private static final int FILE_REQUEST_CODE = 100;
	
	// 远程服务
	private RemoteDocumentService mTemplateService;
	private RemoteDocumentsService mDocumentsService;
	
	// 当前要上传的文件
	private File mCurrentFile;
	
	// 对话框视图控件
	private Dialog mSelectFileDialog;
	private TextView mSelectedFileTextView;
	
	/**
	 * 创建视图
	 */
	@Override
	public View onCreateView( LayoutInflater inflater,
							  ViewGroup container,
							  Bundle savedInstanceState) {
		// 初始化类型和接口
		init( Files.class, 
			  mListInterface, 
			  mRequestInterface,
			  null,
			  mOptionMenuInterface,
			  null);
		
		setPermissionIdentity(GLOBAL.SYS_ACTION[23][0],
				GLOBAL.SYS_ACTION[22][0]);
		
		mDocumentsService = RemoteDocumentsService.getInstance();
		mTemplateService = RemoteDocumentService.getInstance();
		
		View view = super.onCreateView(inflater, container, savedInstanceState);
		
		if (!mHasEditPermission && mHasViewPermission) {
			mOptionsMenu = new OptionsMenuView(getActivity(), new String[] {getString(R.string.view)});
			mOptionsMenu.setSubMenuListener(new SubMenuListener() {
				
				@Override
				public void onSubMenuClick(View view) {
					mOptionsMenu.dismiss();
					switch ((Integer) view.getTag()) {
						case 0:		// 查看
							IntentBuilder.viewFile(getActivity(), mCurrentItem,
									FileUtils.SDPATH + mCurrentItem.getFile_name());
							break;
					}
				}
			});
		}
		if (mFloatingMenu != null) {
			setItemClickListener(mItemListener);
		}
		
		return view;
	}
	
	/**
	 * 列表接口，该接口实现的内容比较杂，主要用来提供资源
	 */
	CommonListInterface<Files> mListInterface 
						= new CommonListInterface<Files>() {
		@Override
		public int getListItemId(Files t) {
			return t.getFile_id();
		}

		@Override
		public String[] getDisplayFeilds() {
			return new String[] {
					SERIAL_NUMBER,
					"file_name"
			};
		}

		@Override
		public int getListHeaderLayoutId() {
			return R.layout.doc_template_listitem_layout;
		}
		
		@Override
		public int getListLayoutId() {
			return getListHeaderLayoutId();
		}

		@Override
		public int getListHeaderNames() {
			return R.array.doc_template_list_header_names;
		}

		@Override
		public int getListHeaderIds() {
			return R.array.doc_template_list_header_ids;
		}
		
		@Override
		public Map<String, Map<String, String>> getDisplayFieldsSwitchMap() {
			return null;
		}

		@Override
		public int getListItemIds() {
			return getListHeaderIds();
		}
	};
	
	OptionMenuInterface mOptionMenuInterface = new OptionMenuInterface() {

		@Override
		public int getOptionMenuNames() {
			return R.array.doc_template_option_menu_names;
		}

		@Override
		public SubMenuListener getOptionMenuClickListener() {
			return new SubMenuListener() {
				
				@Override
				public void onSubMenuClick(View view) {
					mOptionsMenu.dismiss();
					switch ((Integer) view.getTag()) {
						case 0:		// 查看
							IntentBuilder.viewFile(getActivity(), mCurrentItem,
									FileUtils.SDPATH + mCurrentItem.getFile_name());
							break;
						case 1:		// 删除
							commonConfirmDelete();
							break;
					}
				}
			};
		}
	};
	
	/**
	 * 服务请求接口实现
	 */
	ServiceInterface<Files> mRequestInterface 
								= new ServiceInterface<Files>() {

		@Override
		public void getListData() {
			mTemplateService.getDocTmpList(getServiceManager(), 
			        UserCache.getCurrentUser().getTenant_id());
		}

		@Override
		public void addItem(Files t) {
		    t.setDirectory_id("0");
			mDocumentsService.uploadFile(getServiceManager(),
					mProgressDialog, t,
					UserCache.getCurrentUser().getTenant_id(),
					mCurrentFile, new ArrayList<User>(),
					UserCache.getCurrentUser().getUser_id());
			
		}

		@Override
		public void deleteItem(Files t) {
			mDocumentsService.deleteFile(getServiceManager(), t, 0);
		}

		@Override
		public void updateItem(Files t) {
			
		}
	};
	
	/**
	 * 浮动菜单单击监听
	 */
	private OnItemClickListener mItemListener = new OnItemClickListener() {
		
		 @Override
        public void onItemClick(AdapterView<?> parent,
        						View view,
        						int position,
        						long id) {
            switch (position) {
                case 0:
                	mIsAddOperation = mIsFloatMenuAdd = true;
                	
                	initSelectFileDialog();
                	mSelectFileDialog.show();
                	
                    mFloatingMenu.dismiss();
                    break;
                default:
                    break;
            }
        }
	};
	
	/**
	 * 初始化文件选择对话框
	 */
	private void initSelectFileDialog() {
		mSelectFileDialog = new Dialog(getActivity(), R.style.MyDialogStyle);
		mSelectFileDialog.setContentView(R.layout.upload_file_layout);
		
		TextView dialogTitle = (TextView)
						mSelectFileDialog.findViewById(R.id.dialog_title);
		dialogTitle.setText(getActivity()
						.getString(R.string.doc_template_dialog_title));
		
		mSelectedFileTextView = (TextView)
						mSelectFileDialog.findViewById(R.id.select_file_textview);
		ImageView mClose  = (ImageView) mSelectFileDialog.findViewById(R.id.btn_close);
		Button selectButton = (Button)
				mSelectFileDialog.findViewById(R.id.file_select_button);
		selectButton.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View v) {
				pickLocalFile();
			}
		});
		
		Button leftbButton = (Button) mSelectFileDialog.findViewById(R.id.btn_left);
		Button rightButton = (Button) mSelectFileDialog.findViewById(R.id.btn_right);
		leftbButton.setText(getActivity().getString(R.string.upload));
		rightButton.setText(getActivity().getString(R.string.cancel));
		
		leftbButton.setOnClickListener(mButtonListener);
		rightButton.setOnClickListener(mButtonListener);
		mClose.setOnClickListener(mButtonListener);
	}
	
	/**
     * 从文件管理器选择本地文件
     */
    private void pickLocalFile() {
        Intent intent = new Intent(Intent.ACTION_MAIN);
        intent.setClass(getActivity(), FileExplorerActivity.class);
        startActivityForResult(intent, FILE_REQUEST_CODE);
    }
    
	/**
	 * 按键监听
	 */
	private OnClickListener mButtonListener = new OnClickListener() {
		
		@Override
		public void onClick(View v) {
			switch (v.getId()) {
				case R.id.btn_left:
					UploadAttachManager uploadAttachManager = new 
							UploadAttachManager(getActivity(), getServiceManager(), null);
					uploadAttachManager.uploadDocument(mCurrentFile.getAbsolutePath(),
							Integer.parseInt(GLOBAL.FILE_TYPE[26][0]), 0, 0);
					mSelectFileDialog.dismiss();
					break;
				case R.id.btn_right:
					mSelectFileDialog.dismiss();
					break;
				case R.id.btn_close:
					mSelectFileDialog.dismiss();
					break;
				default:
					break;
			}
		}
	};

	@Override
	public void onActivityResult(int requestCode, int resultCode, Intent data) {
		if (resultCode == Activity.RESULT_OK) {
			if (requestCode == FILE_REQUEST_CODE) {
				Bundle bundle = data.getExtras();
	            String path = bundle.getString(FileExplorerActivity.FILE_PATH_KEY);
	            mCurrentFile = new File(path);
	            mSelectedFileTextView.setText(mCurrentFile.getAbsolutePath());
			}
		}
		
		super.onActivityResult(requestCode, resultCode, data);
	}
}
