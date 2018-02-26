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
import com.pm360.cepm360.app.common.view.parent.list.BaseListCommon;
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
import com.pm360.cepm360.app.module.resource.LeaseManagementFragment;
import com.pm360.cepm360.app.module.resource.MaterialFragment;
import com.pm360.cepm360.app.utils.UtilTools;
import com.pm360.cepm360.common.GLOBAL;
import com.pm360.cepm360.common.ResultStatus;
import com.pm360.cepm360.common.json.AnalysisManager;
import com.pm360.cepm360.entity.Contract;
import com.pm360.cepm360.entity.Contract_list;
import com.pm360.cepm360.entity.Files;
import com.pm360.cepm360.entity.P_WBRGNR;
import com.pm360.cepm360.entity.P_WZ;
import com.pm360.cepm360.entity.P_ZL;
import com.pm360.cepm360.entity.Project;
import com.pm360.cepm360.entity.User;
import com.pm360.cepm360.services.document.RemoteDocumentsService;
import com.pm360.cepm360.services.expenses.RemoteExpensesContractService;
import com.pm360.cepm360.services.expenses.RemoteRevenueContractService;

import java.io.Serializable;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * 合同清单项列表实现类
 * @author yuanlu
 *
 */
public class ContractDetail extends ListWithOptionMenu<Contract_list> 
									implements ViewPagersInterface<Contract> {
										
	public static final int MATERIAL_SELECT_REQUEST_CODE = 3000;
	public static final int EQUIPMENT_SELECT_REQUEST_CODE = 3001;
	public static final int LABOUR_SELECT_REQUEST_CODE = 3002;
	public static final int FILE_SELECT_REQUEST_CODE = 3003;
	public static final int LEASE_SELECT_REQUEST_CODE = 3004;
										
	// 服务，包括收入合同、清单项和支付项
	private RemoteRevenueContractService mRevenueContractService 
							= RemoteRevenueContractService.getInstance();
	private RemoteExpensesContractService mExpensesContractService
							= RemoteExpensesContractService.getInstance();
	
	// 当前页展示的合同节点
	private Contract mContract;
	private ContractType mContractType;
	private Project mProject;
	
	private Dialog mPickDialog;
	
	// 收入或者支出合同
	private boolean mIsIncomeContract;
	private boolean mIsAddMode;
	private boolean mIsModify;
	
	// 添加修改的资源
	private Serializable mAddModifySource;
	
	// 单位映射
	private Map<String, String> mUnitMap;
	
	// 文档上传View
	private DocumentUploadView mDocumentUploadView;
	
	// 要添加的清单附件
	private String mCurrentDetailAttachments;
	private List<Files> mFilesList;
	
	// 该类索引，标识该类的回调调用
	public static final int CONTRACT_DETIAL_INDEX = 2100;
	
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
	 * 构造函数，启动初始化流程
	 * @param context
	 * @param isIncomeContract
	 * @param isModify
	 * @param hasSlidePanel
	 */
	public ContractDetail(Context context, boolean isIncomeContract, 
						boolean isModify, boolean hasSlidePanel) {
		super(context);
		mIsAddMode = false;
		commonInit(isIncomeContract, isModify, serviceInterface, hasSlidePanel);
	}
	
	/**
	 * 构造函数
	 * @param context
	 * @param isIncomeContract
	 * @param project
	 * @param serviceInterface
	 * @param hasSlidePanel
	 */
	public ContractDetail(Context context, 
				boolean isIncomeContract,
				Project project,
				ServiceInterface<Contract_list> serviceInterface,
				boolean hasSlidePanel) {
		super(context);
		mProject = project;
		mIsAddMode = true;
		commonInit(isIncomeContract, true, serviceInterface, hasSlidePanel);
	}
	
	/**
	 * 设置当前选择的合同类型
	 * @param type
	 */
	public void setContractType(ContractType type) {
		mContractType = type;
		
		if (mContractType == ContractType.PROJECT_CONTRACT) {
			
			// 隐藏列表的特征和单位
			mListHeader.findViewById(mListHeaderItemIds[1]).setVisibility(View.GONE);
			mListHeader.findViewById(mListHeaderItemIds[2]).setVisibility(View.GONE);
			
			// 隐藏对话框的特征和单位
			hideDialogLineForLabour();
		} else {
			showDialogLine();
		}
	}
	
	/**
	 * 初始化方法，不能使用this()方法实现
	 * @param isIncomeContract
	 * @param isModify
	 * @param serviceInterface
	 * @param hasSlidePanel
	 */
	private void commonInit(boolean isIncomeContract,
			boolean isModify,
			ServiceInterface<Contract_list> serviceInterface,
			boolean hasSlidePanel) {
		mIsIncomeContract = isIncomeContract;
		mIsModify = isModify;
		
		// 初始化单位映射表
		mUnitMap = genIdNameMap(GLOBAL.UNIT_TYPE);
		
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
			
			if (mIsIncomeContract) {
				hideDialogLineForLabour();
			}
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
			Map<String, Map<String, String>> dispayFieldsMap 
									= new HashMap<String, Map<String,String>>();
			dispayFieldsMap.put("qd_unit", BaseListCommon.genIdNameMap(GLOBAL.UNIT_TYPE));
			return dispayFieldsMap;
		}

		@Override
		public int getListItemId(Contract_list t) {
			return t.getId();
		}

		@Override
		public String[] getDisplayFeilds() {
			return new String[] {
					"qd_name", // 清单项名称
					"qd_spec",	// 项目特征
					"qd_unit",	// 单位
					"quantity",	// 数量
					"unit_price",	// 单价
					//"reference_price",	// 参考价格, 未定义
					"total",	// 总额
					"attachments",	// 附件
					"contract_change_code" // 合同变更编号
			};
		}

		@Override
		public int getListHeaderLayoutId() {
			return R.layout.contract_detail_list_layout;
		}

		@Override
		public int getListLayoutId() {
			return getListHeaderLayoutId();
		}

		@Override
		public int getListHeaderNames() {
			return R.array.contract_detail_item_header_names;
		}

		@Override
		public int getListHeaderIds() {
			return R.array.contract_detail_item_header_ids;
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
			if (mIsIncomeContract) {
				mRevenueContractService.getRevenueList(getServiceManager(), 
						mContract.getContract_id(), mContract.getCode());
			} else {
				mExpensesContractService.getExpensesList(getServiceManager(), 
						mContract.getContract_id(), mContract.getCode(), getContractType());
			}
		}

		@Override
		public void addItem(final Contract_list t) {
			t.setContract_id(mContract.getContract_id());
			t.setContract_code(mContract.getCode());
			t.setTenant_id(UserCache.getCurrentUser().getTenant_id());
			t.setProject_id(mContract.getProject_id());
			t.setAttachments(mCurrentDetailAttachments);
			
			// 选择清单时，重新设置清单域
			setDetailField(t);
			
			// 添加清单项后，更新附件的类型ID字段
			DataManagerInterface dataInterface = new DataManagerInterface() {
				
				@Override
				public void getDataOnResult(ResultStatus status, List<?> list) {
					getServiceManager().getDataOnResult(status, list);
					
					switch (status.getCode()) {
						case AnalysisManager.SUCCESS_DB_ADD:
							t.setId(((Contract_list) list.get(0)).getId());
							int id = ((Contract_list) list.get(0)).getId();
							for (int i = 0; i < mFilesList.size(); i++) {
								mFilesList.get(i).setType_id(id);
								RemoteDocumentsService.getInstance().updateFile(null, mFilesList.get(i));
							}
							break;
						default:
							break;
					}
				}
			};
			
			if (mIsIncomeContract) {
				mRevenueContractService.addRevenueList(dataInterface, t);
			} else {
				mExpensesContractService.addExpensesList(dataInterface,
						t, getContractType());
			}
		}

		@Override
		public void deleteItem(Contract_list t) {
			if (mIsIncomeContract) {
				mRevenueContractService.deleteRevenueList(getServiceManager(), t.getId());
			} else {
				mExpensesContractService.deleteExpensesList(getServiceManager(),
						t, getContractType());
			}
		}

		@Override
		public void updateItem(Contract_list t) {
			setDetailField(t);
			
			if (mIsIncomeContract) {
				mRevenueContractService.updateRevenueList(getServiceManager(), t);
			} else {
				mExpensesContractService.updateExpensesList(getServiceManager(), 
						t, getContractType());
			}
		}
	};
	
	/**
	 * 设置清单域
	 * @param t
	 */
	private void setDetailField(Contract_list t) {
		if (mAddModifySource != null) {
			if (mAddModifySource instanceof P_WZ) {
				P_WZ material = (P_WZ) mAddModifySource;
				
				t.setQd_id(material.getWz_id()); 
				t.setQd_name(material.getName());
				t.setQd_spec(material.getSpec());
				t.setQd_unit(material.getUnit());
			} else if (mAddModifySource instanceof P_WBRGNR) {
				P_WBRGNR labour =(P_WBRGNR) mAddModifySource;
				
				t.setQd_id(labour.getWbrgnr_id()); 
				t.setQd_name(labour.getWork());
			} else if (mAddModifySource instanceof P_ZL) {
				P_ZL zl = (P_ZL) mAddModifySource;
				
				t.setQd_id(zl.getZl_id()); 
				t.setQd_name(zl.getName());
				t.setQd_spec(zl.getSpec());
				t.setQd_unit(zl.getUnit());
			}
		}
	}
	
	/**
	 * 获取合同类型
	 * @return
	 */
	private String getContractType() {
		String contractType = null;
		if (mContractType != null) {
			if (mContractType == ContractType.PROJECT_CONTRACT) {
				contractType = GLOBAL.CONTRACT_TYPE[0][0];
			} else if (mContractType == ContractType.PURCHASE_CONTRACT) {
				contractType = GLOBAL.CONTRACT_TYPE[1][0];
			} else {
				contractType = GLOBAL.CONTRACT_TYPE[2][0];
			}
		}
		
		if (mContract != null) {
			contractType = mContract.getType() + "";
		}
		
		return contractType;
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
			return R.array.contract_detail_dialog_lables_names;
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
			dialogStyleMap.put(1, BaseWindow.editTextReadOnlyLineStyle);
			dialogStyleMap.put(2, BaseWindow.editTextReadOnlyLineStyle);
			dialogStyleMap.put(3, BaseWindow.decimalEditTextLineStyle);
			dialogStyleMap.put(4, BaseWindow.decimalEditTextLineStyle);
			dialogStyleMap.put(5, BaseWindow.editTextReadOnlyLineStyle);
			
			return dialogStyleMap;
		}
		
		@Override
		public void additionalInit(final BaseDialog dialog) {
			dialog.setEditTextStyle(0, R.drawable.search_icon, new OnClickListener() {
				
				@Override
				public void onClick(View v) {
					if (getContractType().equals(GLOBAL.CONTRACT_TYPE[2][0])) {
						Intent intent = new Intent(mContext, ListSelectActivity.class);
						intent.putExtra(ListSelectActivity.FRAGMENT_KEY, LeaseManagementFragment.class);
						intent.putExtra(ListSelectActivity.SELECT_MODE_KEY, 
								ListSelectActivity.SINGLE_SELECT);
						((Activity) mContext).startActivityForResult(intent, 
								LEASE_SELECT_REQUEST_CODE);
						
					// 工程合同
					} else if (getContractType().equals(GLOBAL.CONTRACT_TYPE[0][0])) {
						Intent intent = new Intent(mContext, ListSelectActivity.class);
						intent.putExtra(ListSelectActivity.FRAGMENT_KEY, LabourOutSourcingFragment.class);
						intent.putExtra(ListSelectActivity.SELECT_MODE_KEY, 
								ListSelectActivity.SINGLE_SELECT);
						((Activity) mContext).startActivityForResult(intent, 
								LABOUR_SELECT_REQUEST_CODE);
					} else { 	// 采购合同
						mPickDialog.show();
					}
				}
			}, mContext.getString(R.string.contract_select_dialog_title));
			
			final EditText amountEditText = dialog.getEditTextView(3);
			final EditText unitPriceEditText = dialog.getEditTextView(4);
			amountEditText.addTextChangedListener(new TextWatcher() {
				
				@Override
				public void onTextChanged(CharSequence s, int start, int before, int count) {
					String unitPrice = unitPriceEditText.getText().toString();
					String amount = amountEditText.getText().toString();
					
					if (!unitPrice.isEmpty() && !amount.isEmpty()) {
						double totleMoney = Double.parseDouble(amount) 
												* UtilTools.backFormatMoney("", unitPrice);
						dialog.setEditTextContent(5, UtilTools.formatMoney("", totleMoney, 2));
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
						dialog.setEditTextContent(5, UtilTools.formatMoney("", totleMoney, 2));
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
				if (mIsIncomeContract) {
					files.setDir_type(Integer.parseInt(GLOBAL.FILE_TYPE[30][0]));
				} else {
					files.setDir_type(Integer.parseInt(GLOBAL.FILE_TYPE[31][0]));
				}
				
				if (!mIsAddOperation) {
					files.setType_id(mCurrentItem.getId());
				}
				
				if (mProject != null) {
					files.setProject_id(mProject.getProject_id());
				} else if (mContract != null) {
					files.setProject_id(mContract.getProject_id());
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
				return CONTRACT_DETIAL_INDEX;
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
		values[4] = UtilTools.formatMoney("", Double.parseDouble(values[4]), 2);
		values[5] = UtilTools.formatMoney("", Double.parseDouble(values[5]), 2);
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
        beforeLabourView.setVisibility(View.GONE);
        labourButton.setVisibility(View.GONE);
        
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
	public void handleParentEvent(Contract b) {
		if (mContext instanceof CallBack) {
			((CallBack<Void, Integer>) mContext).callBack(CONTRACT_DETIAL_INDEX);
		}
		
		mContract = b;
		super.loadData();
		
		if (mIsIncomeContract || getContractType()
					.equals(GLOBAL.CONTRACT_TYPE[0][0])) {
			mContractType = ContractType.PROJECT_CONTRACT;

			// 隐藏列表的特征和单位
			mListHeader.findViewById(mListHeaderItemIds[1]).setVisibility(
					View.GONE);
			mListHeader.findViewById(mListHeaderItemIds[2]).setVisibility(
					View.GONE);

			if (mIsModify) {
				hideDialogLineForLabour();
			}
		} else {
			if (mIsModify) {
				showDialogLine();
			}
		}
	}
	
	/**
	 * 为人工外包隐藏对话框1、2两行
	 */
	private void hideDialogLineForLabour() {
		mDialog.setLineVisible(1, false);
		mDialog.setLineVisible(2, false);
	}
	
	/**
	 * 为物资和设备显示对话框1、2两行
	 */
	private void showDialogLine() {
		mDialog.setLineVisible(1, true);
		mDialog.setLineVisible(2, true);
	}
	
	/**
	 * 设置添加修改的资源
	 * @param resource
	 */
	public void setAddModifySource(Serializable resource) {
		mAddModifySource = resource;
		if (resource instanceof P_WZ) {
			P_WZ material = (P_WZ) mAddModifySource;
			
			// 设置显示控件
			mDialog.setLineVisible(1, true);
			mDialog.setLineVisible(2, true);
			
			// 设置内容
			mDialog.setEditTextContent(0, material.getName());
			mDialog.setEditTextContent(1, material.getSpec());
			if (material.getUnit() != 0) {
				mDialog.setEditTextContent(2, "" + mUnitMap.get("" + material.getUnit()));
			}
		} else if (resource instanceof P_WBRGNR) {
			P_WBRGNR labour = (P_WBRGNR) mAddModifySource;
			
			// 设置内容和隐藏下面两行
			mDialog.setEditTextContent(0, labour.getWork());
			mDialog.setEditTextContent(1, "");
			mDialog.setEditTextContent(2, "");
			mDialog.setLineVisible(1, false);
			mDialog.setLineVisible(2, false);
		} else if (resource instanceof P_ZL) {
			P_ZL zl = (P_ZL) mAddModifySource;
			
			// 设置显示空间
			mDialog.setLineVisible(1, true);
			mDialog.setLineVisible(2, true);
			
			// 设置内容
			mDialog.setEditTextContent(0, zl.getName());
			mDialog.setEditTextContent(1, zl.getSpec());
			if (zl.getUnit() != 0) {
				mDialog.setEditTextContent(2, "" + mUnitMap.get("" + zl.getUnit()));
			}
		}
	}
	
	@Override
	protected void initListViewItemMore(ViewHolder holder, int position,
			Map<String, String> displayFieldMap) {
		super.initListViewItemMore(holder, position, displayFieldMap);
		
		// 工程合同隐藏清单的规格和单位列
		if (mContractType == ContractType.PROJECT_CONTRACT) {
			holder.tvs[1].setVisibility(View.GONE);
			holder.tvs[2].setVisibility(View.GONE);
		}
		
		int attachmentIndex = mDisplayFields.length - 2;
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
			viewHolder.tvs[viewHolder.tvs.length-2].setOnClickListener(new OnClickListener() {
				
				@Override
				public void onClick(View view) {
					mCurrentItem = mListAdapter.getItem(position);
					mListAdapter.setSelected(position, true);
					
					Intent intent = null;
					if (mIsModify) {
						intent = new Intent(mContext, AttachmentActivity.class);
						
						// 设置项目
						Project project = new Project();
						project.setProject_id(mContract.getProject_id());
						intent.putExtra(AttachmentActivity.KEY_PROJECT, project);
					} else {
						intent = new Intent(mContext, AttachmentReadOnlyActivity.class);
					}
					
					// 设置文件类型和文件列表
					intent.putExtra(AttachmentActivity.KEY_CURRENT_BEAN_ATTACHMENT, 
								mCurrentItem.getAttachments());
					
					if (mIsIncomeContract) {
						intent.putExtra(AttachmentActivity.KEY_TYPE, 
								Integer.parseInt(GLOBAL.FILE_TYPE[30][0]));
					} else {
						intent.putExtra(AttachmentActivity.KEY_TYPE, 
								Integer.parseInt(GLOBAL.FILE_TYPE[31][0]));
					}
					
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
	
	@Override
	public void switchOptionMenu(View view) {
		String changeCode = mCurrentItem.getContract_change_code();
		if (changeCode != null && !changeCode.isEmpty()) {
			mHideOptionMenu = true;
		} else {
			mHideOptionMenu = false;
		}
		
		super.switchOptionMenu(view);
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
}
