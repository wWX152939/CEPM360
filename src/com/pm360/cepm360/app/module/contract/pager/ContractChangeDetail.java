package com.pm360.cepm360.app.module.contract.pager;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.Dialog;
import android.content.Context;
import android.content.Intent;
import android.graphics.drawable.Drawable;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ProgressBar;

import com.pm360.cepm360.R;
import com.pm360.cepm360.app.cache.UserCache;
import com.pm360.cepm360.app.common.activity.DataManagerInterface;
import com.pm360.cepm360.app.common.activity.ListSelectActivity;
import com.pm360.cepm360.app.common.adpater.DataListAdapter.ViewHolder;
import com.pm360.cepm360.app.common.custinterface.CallBack;
import com.pm360.cepm360.app.common.custinterface.CommonListInterface;
import com.pm360.cepm360.app.common.custinterface.DialogAdapterInterface;
import com.pm360.cepm360.app.common.custinterface.ServiceInterface;
import com.pm360.cepm360.app.common.custinterface.ViewPagersInterface;
import com.pm360.cepm360.app.common.view.parent.BaseDialog;
import com.pm360.cepm360.app.common.view.parent.BaseWindow;
import com.pm360.cepm360.app.common.view.parent.list.ListWithOptionMenu;
import com.pm360.cepm360.app.common.view.parent.list.PermissionManager;
import com.pm360.cepm360.app.module.common.attachment.AttachmentActivity;
import com.pm360.cepm360.app.module.common.attachment.AttachmentReadOnlyActivity;
import com.pm360.cepm360.app.module.document.DocumentUploadView;
import com.pm360.cepm360.app.module.document.DocumentUploadView.GetAttachmentInterface;
import com.pm360.cepm360.app.module.document.DocumentUploadView.ProgressBarWindowInterface;
import com.pm360.cepm360.app.module.document.DocumentUploadView.SeleteFileInterface;
import com.pm360.cepm360.app.module.document.DocumentUploadView.UploadInterface;
import com.pm360.cepm360.app.module.email.MailBoxUtils;
import com.pm360.cepm360.app.module.resource.EquipmentFragment;
import com.pm360.cepm360.app.module.resource.LabourOutSourcingFragment;
import com.pm360.cepm360.app.module.resource.MaterialFragment;
import com.pm360.cepm360.app.utils.UtilTools;
import com.pm360.cepm360.common.GLOBAL;
import com.pm360.cepm360.common.ResultStatus;
import com.pm360.cepm360.common.json.AnalysisManager;
import com.pm360.cepm360.entity.Contract_change;
import com.pm360.cepm360.entity.Contract_list;
import com.pm360.cepm360.entity.Files;
import com.pm360.cepm360.entity.P_WBRGNR;
import com.pm360.cepm360.entity.Project;
import com.pm360.cepm360.entity.User;
import com.pm360.cepm360.services.document.RemoteDocumentsService;
import com.pm360.cepm360.services.expenses.RemoteChangeContractService;

import java.io.Serializable;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * 合同变更清单项列表实现类
 * @author yuanlu
 *
 */
public class ContractChangeDetail extends ListWithOptionMenu<Contract_list> 
									implements ViewPagersInterface<Contract_change> {
										
	public static final int MATERIAL_SELECT_REQUEST_CODE = 2000;
	public static final int EQUIPMENT_SELECT_REQUEST_CODE = 2001;
	public static final int LABOUR_SELECT_REQUEST_CODE = 2002;
	public static final int FILE_SELECT_REQUEST_CODE = 2003;
										
	// 服务
	private RemoteChangeContractService mChangeContractService 
								= RemoteChangeContractService.getInstance();
	
	// 当前页展示的合同变更节点
	private Contract_change mContractChange;
	private Project mProject;
	
	private Dialog mPickDialog;
	
	// 添加修改的资源
	private Serializable mAddModifySource;
	private boolean mIsAddMode;
	private boolean mHasModify;
	
	// 文档上传View
	private DocumentUploadView mDocumentUploadView;
	private boolean mIsModify;
	
	// 要添加的清单附件
	private String mCurrentDetailAttachments;
	private List<Files> mFilesList;
	
	// 该类索引，标识该类的回调调用
	public static final int CONTRACT_CHANGE_DETIAL_INDEX = 6100;
	
	/**
	 * 设置当前清单项的附件
	 * @param files
	 */
	public void setFileList(List<Files> files) {
		mFilesList = files;
		
		mCurrentItem.setAttachments(MailBoxUtils.buildAttachmentIds(mFilesList));
		serviceInterface.updateItem(mCurrentItem);
	}
	
	/**
	 * 返回是否修改标识
	 * @return
	 */
	public boolean hasModify() {
		return mHasModify;
	}
	
	/**
	 * 构造函数，启动初始化流程
	 * @param context
	 * @param contractChange
	 * @param isModify
	 * @param hasSlidePanel
	 */
	public ContractChangeDetail(Context context, 
								Contract_change contractChange,
								boolean isModify,
								boolean hasSlidePanel) {
		super(context);
		mIsAddMode = false;
		commonInit(isModify, null, contractChange, serviceInterface, hasSlidePanel);
	}
	
	/**
	 * 构造函数
	 * @param context
	 * @param project
	 * @param serviceInterface
	 * @param hasSlidePanel
	 */
	public ContractChangeDetail(Context context,
				Project project,
				ServiceInterface<Contract_list> serviceInterface,
				boolean hasSlidePanel) {
		super(context);
		mIsAddMode = true;
		commonInit(true, project, null, serviceInterface, hasSlidePanel);
	}
	
	/**
	 * 初始化方法，不能使用this()方法实现
	 * @param isModify
	 * @param project
	 * @param contractChange
	 * @param serviceInterface
	 * @param hasSlidePanel
	 */
	private void commonInit(boolean isModify,
					Project project,
					Contract_change contractChange,
					ServiceInterface<Contract_list> serviceInterface,
					boolean hasSlidePanel) {
		mIsModify = isModify;
		mProject = project;
		mContractChange = contractChange;
		
		// 启动初始化流程
		if (isModify) {
			setPermission(GLOBAL.SYS_ACTION[29][0], GLOBAL.SYS_ACTION[28][0], 
					PermissionManager.PERMISSION_TYPE_SYS);
			
			init(Contract_list.class, listInterface, serviceInterface, 
							dialogAdapterInterface, null, null);
			
			// 设置浮动菜单位置
			setFloatingMenuLocation(hasSlidePanel);
			
			mFloatingMenu.setOnClickListener(new OnClickListener() {
				
				@Override
				public void onClick(View v) {
					mIsAddOperation = mIsFloatMenuAdd = true;
					if (!mIsAddMode) {
						mDocumentUploadView.setDefaultData(true);
					}
		         	mDialog.show(null);
		         	mFloatingMenu.dismiss();
				}
			});
		} else {
			init(Contract_list.class, listInterface, serviceInterface);
		}

		// 初始化选择对话框
		initPickDialog();
	}
	
	/**
	 * 合同清单项列表接口实现
	 */
	private CommonListInterface<Contract_list> listInterface 
								= new CommonListInterface<Contract_list>() {

		@Override
		public Map<String, Map<String, String>> getDisplayFieldsSwitchMap() {
			return null;
		}

		@Override
		public int getListItemId(Contract_list t) {
			return t.getId();
		}

		@Override
		public String[] getDisplayFeilds() {
			return new String[] {
					"qd_name", // 清单项名称
					"quantity",	// 数量
					"unit_price",	// 单价
					"total",	// 总额
					"attachments"	// 附件
			};
		}

		@Override
		public int getListHeaderLayoutId() {
			return R.layout.contract_change_detail_list_layout;
		}

		@Override
		public int getListLayoutId() {
			return getListHeaderLayoutId();
		}

		@Override
		public int getListHeaderNames() {
			return R.array.contract_change_detail_item_header_names;
		}

		@Override
		public int getListHeaderIds() {
			return R.array.contract_change_detail_item_header_ids;
		}

		@Override
		public int getListItemIds() {
			return getListHeaderIds();
		}
	};
	
	/**
	 * 服务接口实现
	 */
	private ServiceInterface<Contract_list> serviceInterface 
							= new ServiceInterface<Contract_list>() {

		@Override
		public void getListData() {
			mChangeContractService.getChangeList(getServiceManager(), mContractChange.getId());
		}

		@Override
		public void addItem(final Contract_list t) {
			t.setContract_code(mContractChange.getContract_code());
			t.setContract_change_code(mContractChange.getCode());
			t.setContract_change_id(mContractChange.getId());
			t.setTenant_id(UserCache.getCurrentUser().getTenant_id());
			t.setProject_id(mContractChange.getProject_id());
			t.setAttachments(mCurrentDetailAttachments);
			
			setDetailField(t);
			
			// 添加清单项后，更新附件的类型ID字段
			DataManagerInterface dataInterface = new DataManagerInterface() {
				
				@Override
				public void getDataOnResult(ResultStatus status, List<?> list) {
					switch (status.getCode()) {
						case AnalysisManager.SUCCESS_DB_ADD:
							mHasModify = true;
							mContractChange.setBqbgk(mContractChange.getBqbgk() + t.getTotal());
							int id = ((Contract_list) list.get(0)).getId();
							for (int i = 0; i < mFilesList.size(); i++) {
								mFilesList.get(i).setType_id(id);
								RemoteDocumentsService.getInstance().updateFile(null, mFilesList.get(i));
							}
							
							((Contract_list) list.get(0)).setQd_name(t.getQd_name());
							getServiceManager().getDataOnResult(status, list);
							break;
						default:
							break;
					}
				}
			};
			
			mChangeContractService.addChangeList(dataInterface, t);
		}

		@Override
		public void deleteItem(final Contract_list t) {
			mChangeContractService.deleteChangeList(new DataManagerInterface() {
				
				@Override
				public void getDataOnResult(ResultStatus status, List<?> list) {
					getServiceManager().getDataOnResult(status, list);
					
					switch (status.getCode()) {
						case AnalysisManager.SUCCESS_DB_DEL:
							mHasModify = true;
							mContractChange.setBqbgk(mContractChange.getBqbgk()
									- t.getTotal());
							break;
						default:
							break;
					}
				}
			}, t.getId());
		}

		@Override
		public void updateItem(final Contract_list t) {
			setDetailField(t);
			
			mChangeContractService.updateChangeList(new DataManagerInterface() {
				
				@Override
				public void getDataOnResult(ResultStatus status, List<?> list) {
					switch (status.getCode()) {
						case AnalysisManager.SUCCESS_DB_UPDATE:
							mHasModify = true;
							mContractChange.setBqbgk(mContractChange.getBqbgk()
									- mCurrentItem.getTotal() + t.getTotal());
							break;
						default:
							break;
					}
					
					getServiceManager().getDataOnResult(status, list);
				}
			}, t);
		}
	};
	
	/**
	 * 设置清单域
	 * @param t
	 */
	private void setDetailField(Contract_list t) {
		if (mAddModifySource != null) {
			P_WBRGNR labour =(P_WBRGNR) mAddModifySource;
			
			t.setQd_id(labour.getWbrgnr_id()); 
			t.setQd_name(labour.getWork());
		}
	}

	/**
	 * 对话框接口实现
	 */
	DialogAdapterInterface dialogAdapterInterface = new DialogAdapterInterface() {
		
		@Override
		public Integer[] getImportantColumns(Map<String, String> saveDataMap) {
			return null;
		}
		
		@Override
		public String[] getUpdateFeilds() {
			return listInterface.getDisplayFeilds();
		}
		
		@Override
		public int getDialogTitleId() {
			return R.string.contract_detail_add_modify;
		}
		
		@Override
		public int getDialogLableNames() {
			return R.array.contract_change_detail_dialog_lable_names;
		}
		
		@Override
		public Map<String, Map<String, String>> getUpdateFieldsSwitchMap() {
			return listInterface.getDisplayFieldsSwitchMap();
		}
		
		@SuppressLint("UseSparseArrays") 
		@Override
		public Map<Integer, String[]> getSupplyData() {
			return null;
		}
		
		@SuppressLint("UseSparseArrays") 
		@Override
		public Map<Integer, Integer> getDialogStyles() {
			Map<Integer, Integer> dialogStyleMap = new HashMap<Integer, Integer>();
			
			dialogStyleMap.put(0, BaseWindow.editTextClickLineStyle);
			dialogStyleMap.put(1, BaseWindow.decimalEditTextLineStyle);
			dialogStyleMap.put(2, BaseWindow.decimalEditTextLineStyle);
			dialogStyleMap.put(3, BaseWindow.editTextReadOnlyLineStyle);
			
			return dialogStyleMap;
		}
		
		@Override
		public void additionalInit(final BaseDialog dialog) {
			dialog.setEditTextStyle(0, R.drawable.search_icon, new OnClickListener() {
				
				@Override
				public void onClick(View v) {
					//mPickDialog.show();
					Intent intent2 = new Intent(mContext, ListSelectActivity.class);
					intent2.putExtra(ListSelectActivity.FRAGMENT_KEY, LabourOutSourcingFragment.class);
					intent2.putExtra(ListSelectActivity.SELECT_MODE_KEY, 
							ListSelectActivity.SINGLE_SELECT);
					((Activity) mContext).startActivityForResult(intent2, 
							LABOUR_SELECT_REQUEST_CODE);
				}
			}, mContext.getString(R.string.contract_select_dialog_title));
			
			final EditText amountEditText = dialog.getEditTextView(1);
			final EditText unitPriceEditText = dialog.getEditTextView(2);
			amountEditText.addTextChangedListener(new TextWatcher() {
				
				@Override
				public void onTextChanged(CharSequence s, int start, int before, int count) {
					String unitPrice = unitPriceEditText.getText().toString();
					String amount = amountEditText.getText().toString();
					
					if (!unitPrice.isEmpty() && !amount.isEmpty()) {
						double totleMoney = Double.parseDouble(amount) 
												* UtilTools.backFormatMoney("", unitPrice);
						dialog.setEditTextContent(3, UtilTools.formatMoney("", totleMoney, 2));
					}
				}
				
				@Override
				public void beforeTextChanged(CharSequence s, int start, int count,
						int after) {
					
				}
				
				@Override
				public void afterTextChanged(Editable s) {
					
				}
			});
			
			unitPriceEditText.addTextChangedListener(new TextWatcher() {
				
				@Override
				public void onTextChanged(CharSequence s, int start, int before, int count) {
					String unitPrice = unitPriceEditText.getText().toString();
					String amount = amountEditText.getText().toString();
					
					if (!unitPrice.isEmpty() && !amount.isEmpty()) {
						double totleMoney = Double.parseDouble(amount) 
												* UtilTools.backFormatMoney("", unitPrice);
						dialog.setEditTextContent(3, UtilTools.formatMoney("", totleMoney, 2));
					}
				}
				
				@Override
				public void beforeTextChanged(CharSequence s, int start, int count,
						int after) {
					
				}
				
				@Override
				public void afterTextChanged(Editable s) {
					
				}
			});
			
			if (!mIsAddMode) {
				
				// 添加附件视图控件
				addAttachmentView(dialog);
			}
		}
	};
	
	/**
	 * 对话框中添加附件视图控件
	 * @param dialog
	 */
	private void addAttachmentView(final BaseDialog dialog) {
		UploadInterface uploadInterface = new UploadInterface() {
			
			@Override
			public List<User> getUserList() {
				return null;
			}
			
			@Override
			public Files getPreFile() {
				Files files = new Files();
				files.setDir_type(Integer.parseInt(GLOBAL.FILE_TYPE[33][0]));
				
				if (!mIsAddOperation) {
					files.setType_id(mCurrentItem.getId());
				}
				
				if (mProject != null) {
					files.setProject_id(mProject.getProject_id());
				} else if (mContractChange != null) {
					files.setProject_id(mContractChange.getProject_id());
				} else {
					files.setProject_id(mCurrentItem.getProject_id());
				}
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
				
				mCurrentDetailAttachments = MailBoxUtils.buildAttachmentIds(fileList);
				if (!mIsAddOperation) {
					mCurrentItem.setAttachments(mCurrentDetailAttachments);
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
				return mCurrentItem == null ? null : mCurrentItem.getAttachments();
			}
		};
		
		ProgressBarWindowInterface windowInterface = new ProgressBarWindowInterface() {
			
			@Override
			public boolean noTitleAndButtonWindow() {
				return true;
			}
			
			@Override
			public ProgressBar getProgressBar() {
				return mDialog.insertProgressBar();
			}
		};
		
		mDocumentUploadView = new DocumentUploadView((Activity) mContext, 
				uploadInterface, getAttachmentInterface, windowInterface);
		
		mDocumentUploadView.initializeWindow();
		mDocumentUploadView.setSelectFileInterface(new SeleteFileInterface() {
			
			@Override
			public int getStartActivityCodeNum() {
				return CONTRACT_CHANGE_DETIAL_INDEX;
			}
		});
		
		// 添加到对话框
		dialog.addView(mDocumentUploadView.getView());
		
		// 重写对话框保存按钮监听
		mDialogSaveListener = new OnClickListener() {
			
			@Override
			public void onClick(View v) {
				Map<String, String> saveMap = dialog.SaveData();
				if (!saveMap.get(mContext.getString(R.string.contract_list_item_name)).isEmpty()) {
					
					// 附件处理完毕后，添加和更新实际操作在上面的dismiss方法中
					mDocumentUploadView.uploadButtonEvent(false);
				} else {
					sendMessage(SHOW_TOAST, mContext.getString(R.string.contract_select_details));
				}
			}
		};
	}
	
	@Override
	protected boolean handleSaveData() {
		if (!super.handleSaveData()) {
			return false;
		}
		
		mSaveData.put(mContext.getString(R.string.price), UtilTools.backFormatMoney("", mSaveData.get(mContext.getString(R.string.price))) + "");
		mSaveData.put(mContext.getString(R.string.contract_total_amount), UtilTools.backFormatMoney("", mSaveData.get(mContext.getString(R.string.contract_total_amount))) + "");
		
		return true;
	}
	
	@Override
	protected String[] getDefaultDialogValue() {
		String[] values = super.getDefaultDialogValue();
		values[2] = UtilTools.formatMoney("", Double.parseDouble(values[2]), 2);
		values[3] = UtilTools.formatMoney("", Double.parseDouble(values[3]), 2);
		return values;
	}
	
	@Override
	protected void showUpdateDialog(boolean isEdit) {
		super.showUpdateDialog(isEdit);
		
		if (!mIsAddMode) {
			mDocumentUploadView.setDefaultData(false);
		}
	}
	
	/**
     * 初始化选择对话框
     */
    private void initPickDialog() {
        mPickDialog = new Dialog(mContext, R.style.MyDialogStyle);
        mPickDialog.setContentView(R.layout.change_attachment_pic_pick_dialog);
        
        // 选择材料
        Button materialButton = (Button) mPickDialog.findViewById(R.id.take_picture);
        materialButton.setText(mContext.getResources().getString(R.string.material_select));
        
        // 选择设备
        Button equipmentButton = (Button) mPickDialog.findViewById(R.id.local_picture);
        equipmentButton.setText(mContext.getResources().getString(R.string.equipment_select));
        
        // 选择人工
        View beforeLabourView = mPickDialog.findViewById(R.id.before_edit_template);
        Button labourButton = (Button) mPickDialog.findViewById(R.id.edit_template);
        labourButton.setText(mContext.getResources()
        			.getString(R.string.laboure_outsource_select));
        beforeLabourView.setVisibility(View.VISIBLE);
        labourButton.setVisibility(View.VISIBLE);
        
        // 注册监听
        materialButton.setOnClickListener(mButtonClickListener);
        equipmentButton.setOnClickListener(mButtonClickListener);
        labourButton.setOnClickListener(mButtonClickListener);

        // 设置点击其他地方退出选择对话框
        mPickDialog.setCanceledOnTouchOutside(true);
    }
    
    /**
     * 按键监听
     */
    OnClickListener mButtonClickListener = new OnClickListener() {
		
		@Override
		public void onClick(View v) {
			mPickDialog.dismiss();
			
			switch (v.getId()) {
				case R.id.take_picture:		// 选择物资
					Intent intent = new Intent(mContext, ListSelectActivity.class);
					intent.putExtra(ListSelectActivity.FRAGMENT_KEY, MaterialFragment.class);
					intent.putExtra(ListSelectActivity.SELECT_MODE_KEY, 
							ListSelectActivity.SINGLE_SELECT);
					((Activity) mContext).startActivityForResult(intent, 
							MATERIAL_SELECT_REQUEST_CODE);
					break;
				case R.id.local_picture:	// 选择设备
					Intent intent1 = new Intent(mContext, ListSelectActivity.class);
					intent1.putExtra(ListSelectActivity.FRAGMENT_KEY, EquipmentFragment.class);
					intent1.putExtra(ListSelectActivity.SELECT_MODE_KEY, 
							ListSelectActivity.SINGLE_SELECT);
					((Activity) mContext).startActivityForResult(intent1, 
							EQUIPMENT_SELECT_REQUEST_CODE);
					break;
				case R.id.edit_template:	// 选择人工外包
					Intent intent2 = new Intent(mContext, ListSelectActivity.class);
					intent2.putExtra(ListSelectActivity.FRAGMENT_KEY, LabourOutSourcingFragment.class);
					intent2.putExtra(ListSelectActivity.SELECT_MODE_KEY, 
							ListSelectActivity.SINGLE_SELECT);
					((Activity) mContext).startActivityForResult(intent2, 
							LABOUR_SELECT_REQUEST_CODE);
					break;
				default:
					break;
			}
		}
	};
	
	/**
	 * 这里重载该函数，不做任何加载操作，因为当前页不是要在初始化后直接
	 * 加载数据，而是显示的时候通过handleParentEvent加载
	 */
	@Override
	protected void loadData() {
		
	}

	/**
	 * 被ViewPager或者其他组合类调用，用于更新页面
	 */
	@SuppressWarnings("unchecked")
	@Override
	public void handleParentEvent(Contract_change b) {
		if (mContext instanceof CallBack) {
			((CallBack<Void, Integer>) mContext).callBack(CONTRACT_CHANGE_DETIAL_INDEX);
		}
		
		mContractChange = b;
		super.loadData();
	}
	
	/**
	 * 设置添加修改的资源
	 * @param resource
	 */
	public void setAddModifySource(Serializable resource) {
		mAddModifySource = resource;
		if (resource instanceof P_WBRGNR) {
			P_WBRGNR labour = (P_WBRGNR) mAddModifySource;
			
			// 设置内容和隐藏下面两行
			mDialog.setEditTextContent(0, labour.getWork());
		}
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
	protected void displayFieldRemap(Map<String, String> displayFieldMap,
			Contract_list t, int position) {
		super.displayFieldRemap(displayFieldMap, t, position);
		displayFieldMap.put("unit_price", UtilTools.formatMoney("", t.getUnit_price(), 2));
		displayFieldMap.put("total", UtilTools.formatMoney("", t.getTotal(), 2));
	}
	
	@Override
	protected void clickRegister(ViewHolder viewHolder, final int position) {
		super.clickRegister(viewHolder, position);
		
		if (!mIsAddMode) {
			viewHolder.tvs[viewHolder.tvs.length-1].setOnClickListener(new OnClickListener() {
				
				@Override
				public void onClick(View view) {
					mCurrentItem = mListAdapter.getItem(position);
					mListAdapter.setSelected(position, true);
					
					Intent intent = null;
					if (mIsModify) {
						intent = new Intent(mContext, AttachmentActivity.class);
						
						// 设置项目
						Project project = new Project();
						project.setProject_id(mContractChange.getProject_id());
						intent.putExtra(AttachmentActivity.KEY_PROJECT, project);
					} else {
						intent = new Intent(mContext, AttachmentReadOnlyActivity.class);
					}
					
					// 设置文件类型和文件列表
					intent.putExtra(AttachmentActivity.KEY_CURRENT_BEAN_ATTACHMENT, 
								mCurrentItem.getAttachments());
					
					intent.putExtra(AttachmentActivity.KEY_TYPE, 
							Integer.parseInt(GLOBAL.FILE_TYPE[32][0]));
					
					intent.putExtra(AttachmentActivity.KEY_IS_UPLOAD_STATUS, true);
					
					if (mIsModify) {
						((Activity) mContext).startActivityForResult(intent, 
										FILE_SELECT_REQUEST_CODE);
					} else {
						((Activity) mContext).startActivity(intent);
					}
				}
			});
		}
	}
	
	/**
	 * 转发返回数据
	 * @param requestCode
	 * @param resultCode
	 * @param data
	 */
	public void OnActivityResult(int requestCode, int resultCode, Intent data) {
		if (mDocumentUploadView != null) {
			mDocumentUploadView.onActivityResult(requestCode, resultCode, data);
		}
	}
}
