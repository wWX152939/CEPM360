package com.pm360.cepm360.app.module.contract.pager;

import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.drawable.Drawable;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.AdapterView;
import android.widget.LinearLayout.LayoutParams;
import android.widget.ProgressBar;

import com.pm360.cepm360.R;
import com.pm360.cepm360.app.cache.UserCache;
import com.pm360.cepm360.app.common.activity.DataManagerInterface;
import com.pm360.cepm360.app.common.adpater.DataListAdapter.ViewHolder;
import com.pm360.cepm360.app.common.custinterface.BaseWidgetInterface;
import com.pm360.cepm360.app.common.custinterface.CallBack;
import com.pm360.cepm360.app.common.custinterface.CommonListInterface;
import com.pm360.cepm360.app.common.custinterface.DialogAdapterInterface;
import com.pm360.cepm360.app.common.custinterface.OptionMenuInterface;
import com.pm360.cepm360.app.common.custinterface.ServiceInterface;
import com.pm360.cepm360.app.common.custinterface.ViewPagersInterface;
import com.pm360.cepm360.app.common.view.OptionsMenuView.SubMenuListener;
import com.pm360.cepm360.app.common.view.parent.BaseDialog;
import com.pm360.cepm360.app.common.view.parent.BaseWindow;
import com.pm360.cepm360.app.common.view.parent.FlowApprovalDialog;
import com.pm360.cepm360.app.common.view.parent.FlowApprovalDialog.FlowApprovalManager;
import com.pm360.cepm360.app.common.view.parent.list.BaseListCommon;
import com.pm360.cepm360.app.common.view.parent.list.ListWithOptionMenu;
import com.pm360.cepm360.app.common.view.parent.list.PermissionManager;
import com.pm360.cepm360.app.module.common.attachment.AttachmentActivity;
import com.pm360.cepm360.app.module.common.attachment.AttachmentReadOnlyActivity;
import com.pm360.cepm360.app.module.contract.CodeFactory;
import com.pm360.cepm360.app.module.document.DocumentUploadView;
import com.pm360.cepm360.app.module.document.DocumentUploadView.GetAttachmentInterface;
import com.pm360.cepm360.app.module.document.DocumentUploadView.ProgressBarWindowInterface;
import com.pm360.cepm360.app.module.document.DocumentUploadView.SeleteFileInterface;
import com.pm360.cepm360.app.module.document.DocumentUploadView.UploadInterface;
import com.pm360.cepm360.app.module.email.MailBoxUtils;
import com.pm360.cepm360.app.utils.UtilTools;
import com.pm360.cepm360.common.GLOBAL;
import com.pm360.cepm360.common.ResultStatus;
import com.pm360.cepm360.common.json.AnalysisManager;
import com.pm360.cepm360.common.util.MiscUtils;
import com.pm360.cepm360.common.util.TwoNumber;
import com.pm360.cepm360.entity.Contract;
import com.pm360.cepm360.entity.Contract_payment;
import com.pm360.cepm360.entity.Files;
import com.pm360.cepm360.entity.Flow_approval;
import com.pm360.cepm360.entity.Flow_setting;
import com.pm360.cepm360.entity.Project;
import com.pm360.cepm360.entity.User;
import com.pm360.cepm360.services.common.RemoteCommonService;
import com.pm360.cepm360.services.expenses.RemoteExpensesContractService;
import com.pm360.cepm360.services.expenses.RemoteRevenueContractService;
import com.pm360.cepm360.services.system.RemoteFlowSettingService;

public class ContractPayment extends ListWithOptionMenu<Contract_payment> 
									implements ViewPagersInterface<Contract> {
										
	public static final int FILE_SELECT_REQUEST_CODE = 4000;
	
	// 服务，包括支出合同、清单项和支付项
	private RemoteExpensesContractService mExpensesContractService 
							= RemoteExpensesContractService.getInstance();
	private RemoteRevenueContractService mRevenueContractService
							= RemoteRevenueContractService.getInstance();
	
	// 添加修改回款项对话框
	private BaseDialog mBaseDialog;
	
	// 当前页展示的合同节点
	private Contract mContract;
	private Project mProject;
	
	// 收入、支出合同
	private boolean mIsIncomeContract;
	private boolean mIsAddMode;
	private boolean mIsModify;
	private boolean mHasModify;
	
	// 是否是触发保存
	private boolean mIsSave;
	
	// 要添加的清单附件
	private String mCurrentDetailAttachments;
	private List<Files> mFilesList;
	
	// 文档上传View
	private DocumentUploadView mDocumentUploadView;
	
	// 流程申请
	private FlowApprovalDialog mFlowApprovalDialog;
	private Flow_setting mFlowSetting;
	private FlowApprovalManager mFlowApprovalManager;
	private Flow_approval mFlowApproval;
	
	// 该类索引，标识该类的回调调用
	public static final int CONTRACT_PAYMENT_INDEX = 3100;
	
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
	 * 是否修改
	 * @return
	 */
	public boolean hasModify() {
		return mHasModify;
	}
	
	/**
	 * 构造函数，启动初始化流程
	 * @param context
	 * @param isIncomeContract
	 * @param isModify
	 * @param hasSlidePanel
	 */
	public ContractPayment(Context context, boolean isIncomeContract, 
			boolean isModify, boolean hasSlidePanel) {
		super(context);
		mIsAddMode = false;
		commonInit(isIncomeContract, isModify, serviceInterface, hasSlidePanel);
	}
	
	/**
	 * 构造函数，启动初始化流程
	 * @param context
	 * @param isIncomeContract
	 * @param project
	 * @param serviceInterface
	 * @param hasSlidePanel
	 */
	public ContractPayment(Context context, 
			boolean isIncomeContract,
			Project project,
			ServiceInterface<Contract_payment> serviceInterface, 
			boolean hasSlidePanel) {
		super(context);
		mProject = project;
		mIsAddMode = true;
		commonInit(isIncomeContract, true, serviceInterface, hasSlidePanel);
	}
	
	/**
	 * 初始化方法，不能使用this()方法
	 * @param isIncomeContract
	 * @param isModify
	 * @param serviceInterface
	 * @param hasSlidePanel
	 */
	private void commonInit(boolean isIncomeContract, 
							boolean isModify,
							final ServiceInterface<Contract_payment> serviceInterface, 
							boolean hasSlidePanel) {
		mIsIncomeContract = isIncomeContract;
		mIsModify = isModify;
		
		// 创建对话框接口实现
		if (!mIsAddMode) {
			mBaseWidgetInterface = new BaseWidgetInterface() {
				
				@Override
				public TwoNumber<View, LayoutParams> createExtraLayout() {
						
					// 添加附件视图控件
					initAttachmentView();
					return new TwoNumber<View, LayoutParams>(mDocumentUploadView
							.getView(), null);
				}

				@Override
				public Integer[] getImportantColumns() {
					// TODO Auto-generated method stub
					return null;
				}
			};
		}
		
		setPermission(GLOBAL.SYS_ACTION[29][0], GLOBAL.SYS_ACTION[28][0], 
				PermissionManager.PERMISSION_TYPE_SYS);
		
		// 启动初始化流程
		if (isModify) {
			init(Contract_payment.class, listInterface, serviceInterface, 
					dialogAdapterInterface, normalMenuInterface, null);
			
			// 不显示状态或延期控件
			if (mIsIncomeContract) {
				mListHeader.findViewById(mListHeaderItemIds[mListHeaderItemIds.length-2])
							.setVisibility(View.GONE);
			}
			
			// 设置浮动菜单位置
			setFloatingMenuLocation(hasSlidePanel);
			
			mFloatingMenu.setOnClickListener(new OnClickListener() {
				
				@Override
				public void onClick(View v) {
					mIsAddOperation = mIsFloatMenuAdd = true;
					
					if (!mIsAddMode) {
						mDocumentUploadView.setDefaultData(true);
					}
					
					mDialog.switchModifyDialog(true);
					
					// 设置对话框按钮显示
					if (!mIsAddMode && !mIsIncomeContract) {
						if (mFlowSetting != null && mFlowSetting.getStatus() == 1) {
							setAllDialogButton(true, true, false);
						} else {
							setAllDialogButton(true, false, false);
						}
					}
					
		         	mDialog.show(null);
		         	mFloatingMenu.dismiss();
		         	
		         	// 生成回款编号
		    		int prefixCode = CodeFactory.RETURN_MONEY_CODE;
		    		if (!mIsIncomeContract) {
		    			prefixCode = CodeFactory.PAYMENT_CODE;
		    			
		    			// 状态默认为“未提交”
		    			mBaseDialog.setEditTextContent(6, GLOBAL.FLOW_APPROVAL_STATUS[0][1]);
		    		}
		    		RemoteCommonService.getInstance().getCodeByDate(new DataManagerInterface() {
		    			
		    			@Override
		    			public void getDataOnResult(ResultStatus status, List<?> list) {
		    				if (status.getCode() == AnalysisManager.SUCCESS_DB_QUERY) {
		    					mBaseDialog.setEditTextContent(0, status.getMessage());
		    				} else {
		    					sendMessage(BaseListCommon.SHOW_TOAST, status.getMessage());
		    				}
		    			}
		    		}, CodeFactory.getCodePrefix(prefixCode));
				}
			});
			
			if (mIsIncomeContract) {
				mDialog.setLineVisible(6, false);
			}
		} else {
			if (mIsIncomeContract) {
				init(Contract_payment.class, listInterface, serviceInterface);
			} else {
				setForceEnableOptionMenu(true);
				init(Contract_payment.class, listInterface, serviceInterface, 
						dialogAdapterInterface, normalMenuInterface);
			}
		}
		
		// 审批通过和驳回的回调函数
		mFlowApprovalManager = new FlowApprovalManager() {
			
			@Override
			public void rebutFlowData2Server() {
				mIsSave = true;
				mCurrentItem.setStatus(Integer.parseInt(GLOBAL.FLOW_APPROVAL_STATUS[3][0]));
				serviceInterface.updateItem(mCurrentItem);
			}
			
			@Override
			public void passFlowData2Server() {
				mIsSave = true;
				mCurrentItem.setStatus(Integer.parseInt(GLOBAL.FLOW_APPROVAL_STATUS[2][0]));
				mExpensesContractService.updatePay(new DataManagerInterface() {
		
					@Override
					public void getDataOnResult(ResultStatus status, List<?> list) {
						switch (status.getCode()) {
							case AnalysisManager.SUCCESS_DB_UPDATE:
								if (mContract != null) {
									mHasModify = true;
									mContract.setPaid(mContract.getPaid()
													+ mCurrentItem.getActual_pay());
								}
								mBaseDialog.SetDefaultValue(getDefaultDialogValue());
								break;
							default:
								break;
						}
						
						// 后调用该方法，更新当前节点
						getServiceManager().getDataOnResult(status, list);
					}
				}, mCurrentItem);
			}
		};
	}
	
	/**
	 * 未提交的选项菜单
	 */
	OptionMenuInterface unSubmitMenuInterface = new OptionMenuInterface() {
		
		@Override
		public int getOptionMenuNames() {
			return R.array.contract_payment_option_menu_names; // 详情、修改、删除、提交
		}
		
		@Override
		public SubMenuListener getOptionMenuClickListener() {
			return new SubMenuListener() {
				
				@Override
				public void onSubMenuClick(View view) {
					handleOptionMenuClick(view);
				}
			};
		}
	};
	
	/**
	 * 只显示详情的选项菜单
	 */
	OptionMenuInterface detailMenuInterface = new OptionMenuInterface() {
		
		@Override
		public int getOptionMenuNames() {
			return R.array.pass_option_menu_names; // 详情
		}
		
		@Override
		public SubMenuListener getOptionMenuClickListener() {
			return new SubMenuListener() {
				
				@Override
				public void onSubMenuClick(View view) {
					handleOptionMenuClick(view);
				}
			};
		}
	};
	
	/**
	 * 驳回状态下的选项菜单
	 */
	OptionMenuInterface rejectMenuInterface = new OptionMenuInterface() {
		
		@Override
		public int getOptionMenuNames() {
			return R.array.attachment_reject_option_menu_names; // 属性、修改、删除，提交
		}
		
		@Override
		public SubMenuListener getOptionMenuClickListener() {
			return new SubMenuListener() {
				
				@Override
				public void onSubMenuClick(View view) {
					mOptionsMenu.dismiss();
					switch ((Integer) view.getTag()) {
						case 0:		// 详情
							showDetailDialog(false);
							break;
						case 1:		// 修改
							showDetailDialog(true);
							break;
						case 2:		// 提交
							submit();
							break;
					}
				}
			};
		}
	};
	
	/**
	 * 驳回状态下的选项菜单
	 */
	OptionMenuInterface normalMenuInterface = new OptionMenuInterface() {
		
		@Override
		public int getOptionMenuNames() {
			return R.array.contract_option_menu_names; // 详情、修改、删除
		}
		
		@Override
		public SubMenuListener getOptionMenuClickListener() {
			return new SubMenuListener() {
				
				@Override
				public void onSubMenuClick(View view) {
					handleOptionMenuClick(view);
				}
			};
		}
	};
	
	/**
	 * 审批中的选项菜单
	 */
	OptionMenuInterface approvalMenuInterface = new OptionMenuInterface() {
		
		@Override
		public int getOptionMenuNames() {
			return R.array.approval_option_menu_names;	// 属性、审批
		}
		
		@Override
		public SubMenuListener getOptionMenuClickListener() {
			return new SubMenuListener() {
				
				@Override
				public void onSubMenuClick(View view) {
					mOptionsMenu.dismiss();
					switch ((Integer) view.getTag()) {
						case 0:		// 详情
							showDetailDialog(false);
							break;
						case 1:		// 审批
							approval();
							break;
					}
				}
			};
		}
	};
	
	/**
	 * 审批中的选项菜单
	 */
	OptionMenuInterface passMenuInterface = new OptionMenuInterface() {
		
		@Override
		public int getOptionMenuNames() {
			return R.array.pass_option_menu_names;	// 属性
		}
		
		@Override
		public SubMenuListener getOptionMenuClickListener() {
			return new SubMenuListener() {
				
				@Override
				public void onSubMenuClick(View view) {
					handleOptionMenuClick(view);
				}
			};
		}
	};
	
	/**
	 * 处理选项菜单点击操作
	 * @param view
	 */
	private void handleOptionMenuClick(View view) {
		mOptionsMenu.dismiss();
		switch ((Integer) view.getTag()) {
			case 0:		// 详情
				showDetailDialog(false);
				break;
			case 1:		// 修改
				showDetailDialog(true);
				break;
			case 2:		// 删除
				commonConfirmDelete();
				break;
			case 3:		// 提交
				submit();
				break;
		}
	}
	
	/**
	 * 显示详情对话框
	 * @param isEdit 决定是否可以修改
	 */
	public void showDetailDialog(boolean isEdit) {
		mIsAddOperation = false;
		showUpdateDialog(isEdit);
	}
	
	/**
	 * 
	 */
	private void submit() {
		showAlertDialog(mContext, mContext.getResources().getString(R.string.submit), 
			new DialogInterface.OnClickListener() {
					
				@Override
				public void onClick(DialogInterface dialog, int which) {
					mCurrentItem.setStatus(Integer.parseInt(GLOBAL.FLOW_APPROVAL_STATUS[1][0]));
					Flow_approval flowApproval = new Flow_approval();
					flowApproval.setCurrent_level(mFlowSetting.getLevel1());
					flowApproval.setFlow_type(GLOBAL.FLOW_TYPE[1][0]);
					flowApproval.setNext_level(mFlowSetting.getLevel2());
					flowApproval.setStatus(Integer.parseInt(GLOBAL.FLOW_APPROVAL_STATUS[1][0]));
					flowApproval.setSubmit_time(new Date());
					flowApproval.setSubmiter(UserCache.getCurrentUserId());
					flowApproval.setTenant_id(UserCache.getTenantId());
					flowApproval.setType_id(mCurrentItem.getId());
					mExpensesContractService.updateForSubmit(getServiceManager(), 
							mCurrentItem, flowApproval);
				}
			});
	}
	
	/**
	 * 审批按钮相应函数
	 */
	private void approval() {
		if (mFlowApprovalDialog != null) {
			mFlowApprovalDialog.show(mCurrentItem.getStatus() 
					== Integer.parseInt(GLOBAL.FLOW_APPROVAL_STATUS[1][0]));
		}
	}
	
	/**
	 * 支付项列表接口实现								
	 */
	private CommonListInterface<Contract_payment> listInterface 
								= new CommonListInterface<Contract_payment>() {

		@Override
		public Map<String, Map<String, String>> getDisplayFieldsSwitchMap() {
			Map<String, Map<String, String>> dispayFieldsMap 
						= new HashMap<String, Map<String,String>>();
			dispayFieldsMap.put("status", BaseListCommon
						.genIdNameMap(GLOBAL.FLOW_APPROVAL_STATUS));
			return dispayFieldsMap;
		}

		@Override
		public int getListItemId(Contract_payment t) {
			return t.getId();
		}

		@Override
		public String[] getDisplayFeilds() {
			return new String[] {
				"code",	// 回款编号
				"name",	// 回款名称
				"payable",	// 应回款额
				"expect_date",	// 预计回款日期
				"actual_pay",	// 实际回款额
				"actual_date",	// 实际回款日期
				"status",	// 状态
				"attachments"	// 附件
			};
		}

		@Override
		public int getListHeaderLayoutId() {
			return R.layout.contract_payment_list_layout;
		}

		@Override
		public int getListLayoutId() {
			return getListHeaderLayoutId();
		}

		@Override
		public int getListHeaderNames() {
			if (!mIsIncomeContract) {
				return R.array.contract_payment_item_header_names;
			} else {
				return R.array.contract_return_money_item_header_names;
			}
		}

		@Override
		public int getListHeaderIds() {
			return R.array.contract_payment_item_ids;
		}

		@Override
		public int getListItemIds() {
			return getListHeaderIds();
		}
	};
	
	/**
	 * 服务接口实现
	 */
	private ServiceInterface<Contract_payment> serviceInterface 
							= new ServiceInterface<Contract_payment>() {

		@Override
		public void getListData() {
			if (!mIsIncomeContract) {
				mExpensesContractService.getPayList(getServiceManager(), mContract.getContract_id());
			} else {
				mRevenueContractService.getCashbackList(getServiceManager(),
						mContract.getContract_id());
			}
		}

		@Override
		public void addItem(final Contract_payment t) {
			t.setContract_id(mContract.getContract_id());
			t.setAttachments(mCurrentDetailAttachments);
			
			DataManagerInterface dataManagerInterface = new DataManagerInterface() {
				
				@Override
				public void getDataOnResult(ResultStatus status, List<?> list) {
					getServiceManager().getDataOnResult(status, list);
					
					switch (status.getCode()) {
						case AnalysisManager.SUCCESS_DB_ADD:
							mHasModify = true;
							mContract.setPaid(mContract.getPaid() + t.getActual_pay());
							break;
						default:
							break;
					}
				}
			};
			
			if (!mIsIncomeContract) {
				if (mFlowSetting == null || mFlowSetting.getStatus() 
						== Integer.parseInt(GLOBAL.FLOW_STATUS[1][0])) {
					t.setStatus(Integer.parseInt(GLOBAL.FLOW_APPROVAL_STATUS[2][0]));
					mExpensesContractService.addPay(dataManagerInterface, t);
				} else if (mIsSave) {
					t.setStatus(Integer.parseInt(GLOBAL.FLOW_APPROVAL_STATUS[0][0]));
					mExpensesContractService.addPay(getServiceManager(), t);
				} else {
					t.setStatus(Integer.parseInt(GLOBAL.FLOW_APPROVAL_STATUS[1][0]));
					mExpensesContractService.addPayForSubmit(getServiceManager(), 
							t, getSubmitFlowApproval(true));
				}
			} else {
				mRevenueContractService.addCashback(dataManagerInterface, t);
			}
		}

		@Override
		public void deleteItem(final Contract_payment t) {
			DataManagerInterface dataManagerInterface = new DataManagerInterface() {
				
				@Override
				public void getDataOnResult(ResultStatus status, List<?> list) {
					getServiceManager().getDataOnResult(status, list);
					
					switch (status.getCode()) {
						case AnalysisManager.SUCCESS_DB_DEL:
							mHasModify = true;
							mContract.setPaid(mContract.getPaid() - t.getActual_pay());
							break;
						default:
							break;
					}
				}
			};
			
			if (!mIsIncomeContract) {
				mExpensesContractService.deletePay(getServiceManager(), t.getId());
			} else {
				mRevenueContractService.deleteCashback(dataManagerInterface, t.getId());
			}
		}

		@Override
		public void updateItem(final Contract_payment t) {
			if (!mIsIncomeContract) {
				if (mIsSave) {
					mExpensesContractService.updatePay(getServiceManager(), t);
				} else {
					t.setStatus(Integer.parseInt(GLOBAL.FLOW_APPROVAL_STATUS[1][0]));
					mExpensesContractService.updateForSubmit(getServiceManager(),
							t, getSubmitFlowApproval(false));
				}
			} else {
				mRevenueContractService.updateCashback(new DataManagerInterface() {
					
					@Override
					public void getDataOnResult(ResultStatus status, List<?> list) {
						switch (status.getCode()) {
							case AnalysisManager.SUCCESS_DB_UPDATE:
								mHasModify = true;
								mContract.setPaid(mContract.getPaid() 
										- mCurrentItem.getActual_pay() + t.getActual_pay());
								break;
							default:
								break;
						}
						
						// 后调用该方法，更新当前节点
						getServiceManager().getDataOnResult(status, list);
					}
				}, t);
			}
		}
	};
	
	/**
	 * 获取提交时生成的Flow_approval对象
	 * @param isAdd 添加还是修改界面的提交
	 * @return
	 */
	private Flow_approval getSubmitFlowApproval(boolean isAdd) {
		Flow_approval flowApproval = new Flow_approval();
		flowApproval.setCurrent_level(mFlowSetting.getLevel1());
		flowApproval.setFlow_type(GLOBAL.FLOW_TYPE[1][0]);
		flowApproval.setNext_level(mFlowSetting.getLevel2());
		flowApproval.setStatus(Integer.parseInt(GLOBAL.FLOW_APPROVAL_STATUS[1][0]));
		flowApproval.setSubmit_time(new Date());
		flowApproval.setSubmiter(UserCache.getCurrentUserId());
		flowApproval.setTenant_id(UserCache.getTenantId());
		
		if (!isAdd) {
			flowApproval.setType_id(mCurrentItem.getId());
		}
		
		return flowApproval;
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
			if (mIsIncomeContract) {
				return R.string.contract_return_money_add_modify;
			} else {
				return R.string.contract_payment_add_modify;
			}
		}
		
		@Override
		public int getDialogLableNames() {
			if (mIsIncomeContract) {
				return R.array.contract_return_money_dialog_lables_names;
			} else {
				return R.array.contract_payment_dialog_lables_names;
			}
		}
		
		@Override
		public Map<String, Map<String, String>> getUpdateFieldsSwitchMap() {
			return listInterface.getDisplayFieldsSwitchMap();
		}
		
		@Override
		public Map<Integer, String[]> getSupplyData() {
			return null;
		}
		
		@SuppressLint("UseSparseArrays") 
		@Override
		public Map<Integer, Integer> getDialogStyles() {
			Map<Integer, Integer> dialogStyleMap = new HashMap<Integer, Integer>();
			
			dialogStyleMap.put(0, BaseWindow.editTextReadOnlyLineStyle);
			dialogStyleMap.put(2, BaseWindow.decimalEditTextLineStyle);
			dialogStyleMap.put(3, BaseWindow.calendarLineStyle);
			dialogStyleMap.put(4, BaseWindow.decimalEditTextLineStyle);
			dialogStyleMap.put(5, BaseWindow.calendarLineStyle);
			dialogStyleMap.put(6, BaseWindow.editTextReadOnlyLineStyle);
			
			return dialogStyleMap;
		}
		
		@Override
		public void additionalInit(final BaseDialog dialog) {
			mBaseDialog = dialog;
			
			// 重写对话框保存按钮监听
			dialog.setButtonVisibility(2, true);
			mDialogSaveListener = new OnClickListener() {
				
				@Override
				public void onClick(View v) {
					mIsSave = true;
					Map<String, String> saveMap = dialog.SaveData();
					if (mIsIncomeContract) {
						if (!saveMap.get(mContext.getString(R.string.received_payment_name)).isEmpty()) {
							if (mIsAddMode) {
								
								// 如果是添加，在调用服务添加接口前调用
								if (dialogSaveButtonEvent()) {
									mDialog.dismiss();
								}
								
								if (mFloatingMenu != null) {
									mFloatingMenu.dismiss();
								}
							} else {
								mDocumentUploadView.uploadButtonEvent(false);
							}
						} else {
							sendMessage(SHOW_TOAST, mContext.getString(R.string.input_received_payment_name));
						}
					} else {
						if (!saveMap.get(mContext.getString(R.string.payment_name)).isEmpty()) {
							if (mIsAddMode) {
								
								// 如果是添加，在调用服务添加接口前调用
								if (dialogSaveButtonEvent()) {
									mDialog.dismiss();
								}
								
								if (mFloatingMenu != null) {
									mFloatingMenu.dismiss();
								}
							} else {
								mDocumentUploadView.uploadButtonEvent(false);
							}
						} else {
							sendMessage(SHOW_TOAST, mContext.getString(R.string.contract_input_payment_name));
						}
					}
				}
			};
		}
	};
	
	/**
	 * 对话框中添加附件视图控件
	 * @param dialog
	 */
	private void initAttachmentView() {
		UploadInterface uploadInterface = new UploadInterface() {
			
			@Override
			public List<User> getUserList() {
				return null;
			}
			
			@Override
			public Files getPreFile() {
				Files files = new Files();
				if (mIsIncomeContract) {
					files.setDir_type(Integer.parseInt(GLOBAL.FILE_TYPE[5][0]));
				} else {
					files.setDir_type(Integer.parseInt(GLOBAL.FILE_TYPE[29][0]));
				}
				
				if (!mIsAddOperation) {
					files.setType_id(mCurrentItem.getId());
				}
				
				if (mProject != null) {
					files.setProject_id(mProject.getProject_id());
				} else if (mContract != null) {
					files.setProject_id(mContract.getProject_id());
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
				return CONTRACT_PAYMENT_INDEX;
			}
		});
	}
	
	@Override
	protected boolean handleSaveData() {
		if (!super.handleSaveData()) {
			return false;
		}
		
		if (mIsIncomeContract) {
			mSaveData.put(mContext.getString(R.string.should_received_payment_amount), UtilTools.backFormatMoney("", 
					mSaveData.get(mContext.getString(R.string.should_received_payment_amount))) + "");
			mSaveData.put(mContext.getString(R.string.actual_received_payment_amount), UtilTools.backFormatMoney("", 
					mSaveData.get(mContext.getString(R.string.actual_received_payment_amount))) + "");
		} else {
			mSaveData.put(mContext.getString(R.string.payment_o_payment), UtilTools.backFormatMoney("", 
					mSaveData.get(mContext.getString(R.string.payment_o_payment))) + "");
			mSaveData.put(mContext.getString(R.string.payment_a_payment), UtilTools.backFormatMoney("", 
					mSaveData.get(mContext.getString(R.string.payment_a_payment))) + "");
		}
		
		return true;
	}
	
	@Override
	protected String[] getDefaultDialogValue() {
		String[] defaultValues = super.getDefaultDialogValue();
		defaultValues[2] = UtilTools.formatMoney("",
						Double.parseDouble(defaultValues[2]), 2);
		defaultValues[4] = UtilTools.formatMoney("", 
						Double.parseDouble(defaultValues[4]), 2);
		return defaultValues;
	}

	@Override
	protected void showUpdateDialog(boolean isEdit) {		
		super.showUpdateDialog(isEdit);
		
		if (!mIsAddMode) {
			
			// 切换对话框的编辑模式
			mDocumentUploadView.switchReadOnlyMode(!isEdit);
			
			mDocumentUploadView.setDefaultData(false);
		}
		
		// 支出合同，详情
		if (!mIsIncomeContract) {
			if (!isEdit) {
				if (mCurrentItem.getStatus() != 1 && mCurrentItem.getStatus() != 0) {
					mDialog.setButtonVisibility(2, true);
				}
				if (mCurrentItem.getStatus() != Integer.parseInt(GLOBAL.FLOW_APPROVAL_STATUS[2][0])) {
					mDialog.setButtonVisibility(1, false);
				}
				mDialog.setButtonVisibility(0, false);
			} else {
				if (mCurrentItem.getStatus() 
						!= Integer.parseInt(GLOBAL.FLOW_APPROVAL_STATUS[3][0])) {
					mDialog.setButtonVisibility(2, false);
				}
			}
		} else {
			mDialog.setLineVisible(6, false);
		}
	}
	
	/**
	 * 更新对话框内容
	 * @param isEdit
	 */
	public void updateDialogValues(boolean isEdit) {		
		
		// 设置是否可以修改
		mDialog.switchModifyDialog(isEdit);
		mDialog.SetDefaultValue(getDefaultDialogValue());
		
		if (!mIsAddMode) {
			
			// 切换对话框的编辑模式
			mDocumentUploadView.switchReadOnlyMode(!isEdit);
			mDocumentUploadView.setDefaultData(false);
		}

		// 支出合同，详情
		if (!mIsIncomeContract) {
			if (!isEdit) {
				if (mCurrentItem.getStatus() != 1 && mCurrentItem.getStatus() != 0) {
					mDialog.setButtonVisibility(2, true);
				}
				
				if (mCurrentItem.getStatus() != Integer.parseInt(GLOBAL.FLOW_APPROVAL_STATUS[2][0])) {
					mDialog.setButtonVisibility(1, false);
				}
				mDialog.setButtonVisibility(0, false);
			} else {
				if (mCurrentItem.getStatus() 
						!= Integer.parseInt(GLOBAL.FLOW_APPROVAL_STATUS[3][0])) {
					mDialog.setButtonVisibility(2, false);
				}
			}
		} 
	}
	
	@Override
	protected void floatingMenuItemClick(AdapterView<?> parent, View view,
			int position, long id) {
		super.floatingMenuItemClick(parent, view, position, id);
		
		// 生成回款编号
		int prefixCode = CodeFactory.RETURN_MONEY_CODE;
		if (!mIsIncomeContract) {
			prefixCode = CodeFactory.PAYMENT_CODE;
			
			// 状态默认为“未提交”
			mBaseDialog.setEditTextContent(6, GLOBAL.FLOW_APPROVAL_STATUS[0][1]);
		}
		RemoteCommonService.getInstance().getCodeByDate(new DataManagerInterface() {
			
			@Override
			public void getDataOnResult(ResultStatus status, List<?> list) {
				if (status.getCode() == AnalysisManager.SUCCESS_DB_QUERY) {
					mBaseDialog.setEditTextContent(0, status.getMessage());
				} else {
					sendMessage(BaseListCommon.SHOW_TOAST, status.getMessage());
				}
			}
		}, CodeFactory.getCodePrefix(prefixCode));
	}
	
	@Override
	protected void displayFieldRemap(Map<String, String> displayFieldMap,
			Contract_payment t, int position) {
		super.displayFieldRemap(displayFieldMap, t, position);
		
		// 计算日期差
		Date realDate = t.getActual_date();
		Date planDate = t.getExpect_date();
		int number = MiscUtils.calcDateSub(planDate, realDate);
		
		if (number != Integer.MIN_VALUE) {
			displayFieldMap.put("delay_date", number + " 天");
		}
		displayFieldMap.put("payable", UtilTools.formatMoney("", t.getPayable(), 2));
		displayFieldMap.put("actual_pay", UtilTools.formatMoney("", t.getActual_pay(), 2));
	}
	
	@Override
	protected void initListViewItemMore(ViewHolder holder, int position,
			Map<String, String> displayFieldMap) {
		super.initListViewItemMore(holder, position, displayFieldMap);
		
		if (mIsIncomeContract) {
			holder.tvs[mDisplayFields.length - 2].setVisibility(View.GONE);
		}
		
		// 设置附件
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

	/**
	 * 当父节点改变，调用该函数刷新界面
	 */
	@SuppressWarnings("unchecked")
	@Override
	public void handleParentEvent(Contract b) {
		if (mContext instanceof CallBack) {
			((CallBack<Void, Integer>) mContext).callBack(CONTRACT_PAYMENT_INDEX);
		}
		
		mContract = b;
		super.loadData();
	}
	
	/**
	 * 设置对话框的关闭控件监听者
	 * @param listener
	 */
	public void setCloseViewListener(OnClickListener listener) {
		mBaseDialog.setCloseViewListener(listener);
	}
	
	/**
	 * 这里重载该函数，不做任何加载操作，因为当前页不是要在初始化后直接
	 * 加载数据，而是显示的时候通过handleParentEvent加载
	 */
	@Override
	protected void loadData() {
		if (!mIsIncomeContract) {
			RemoteFlowSettingService.getInstance().getFlowDetail(
					new DataManagerInterface() {
						
						@Override
						public void getDataOnResult(ResultStatus status, List<?> list) {
							if (status.getCode() == AnalysisManager.SUCCESS_DB_QUERY) {
								if (!list.isEmpty()) {
									mFlowSetting = (Flow_setting) list.get(0);
									
									// 初始化审批流程对话框
									prepareFlowApproval();
								}
							} else {
								sendMessage(SHOW_TOAST, status.getMessage());
							}
						}
					}, UserCache.getTenantId(), GLOBAL.FLOW_TYPE[1][0]);
		}
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
					if (mIsModify && (mIsIncomeContract || mCurrentItem.getStatus() 
								== Integer.parseInt(GLOBAL.FLOW_APPROVAL_STATUS[0][0]))) {
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
					
					if (mIsModify && (mIsIncomeContract || mCurrentItem.getStatus() 
							== Integer.parseInt(GLOBAL.FLOW_APPROVAL_STATUS[0][0]))) {
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
		switchMenuAndButton();
		super.switchOptionMenu(view);
	}
	
	/**
	 * 切换菜单和按钮显示
	 */
	public void switchMenuAndButton() {
		if (!mIsAddMode && !mIsIncomeContract) {
			mFlowApproval.setType_id(mCurrentItem.getId());
			mFlowApprovalDialog.setFlowApproval(mFlowApproval);

			if (mCurrentItem.getStatus() == 0) { // 未知
				setOptionMenuInterface(normalMenuInterface);
				setAllDialogButton(true, true, false);
			} else if (mCurrentItem.getStatus() == Integer
					.parseInt(GLOBAL.FLOW_APPROVAL_STATUS[0][0])) { // 未提交
				if (mIsModify) {
					setOptionMenuInterface(unSubmitMenuInterface);
					setAllDialogButton(true, true, false);
				} else {
					setOptionMenuInterface(detailMenuInterface);
					setAllDialogButton(false, false, false);
				}
			} else if (mCurrentItem.getStatus() == Integer
					.parseInt(GLOBAL.FLOW_APPROVAL_STATUS[3][0])) { // 驳回
				if (mIsModify) {
					setOptionMenuInterface(rejectMenuInterface);
					setAllDialogButton(true, true, true);
				} else {
					setOptionMenuInterface(approvalMenuInterface);
					setAllDialogButton(false, false, true);
				}
			} else if (mCurrentItem.getStatus() == Integer
					.parseInt(GLOBAL.FLOW_APPROVAL_STATUS[1][0])) { // 审批中
				setOptionMenuInterface(approvalMenuInterface);
				setAllDialogButton(false, false, true);
			} else if (mCurrentItem.getStatus() == Integer
					.parseInt(GLOBAL.FLOW_APPROVAL_STATUS[2][0])) { // 通过
				setOptionMenuInterface(passMenuInterface);
				setPassDialogButton();
			} else { // 已执行
				setOptionMenuInterface(passMenuInterface);
				setAllDialogButton(false, false, true);
			}
		}
	}
	
	/**
	 * 获取对话框跟视图
	 * @return
	 */
	public View getDialogRootView() {
		return mDialog.getPopupView();
	}
	
	public void setPassDialogButton() {
		if (mBaseDialog != null) { 
			mBaseDialog.setButtonVisibility(1, true);
			mBaseDialog.setButtonVisibility(2, true);
			mBaseDialog.setWhichButton(1, mContext.getResources()
					.getString(R.string.approve), new OnClickListener() {
				
				@Override
				public void onClick(View v) {
					approval();
				}
			});
			mBaseDialog.setWhichButton(2, mContext.getResources()
							.getString(R.string.execute), new OnClickListener() {
								
								@Override
								public void onClick(View v) {
									mCurrentUpdateItem = MiscUtils.clone(mCurrentItem);
									mCurrentUpdateItem.setStatus(Integer.parseInt(GLOBAL.FLOW_APPROVAL_STATUS[4][0]));
									mExpensesContractService.updatePay(mPayManager, mCurrentUpdateItem);
								}
							});
		}
	}
	
	/**
	 * 服务返回结果处理
	 */
	private DataManagerInterface mPayManager = new DataManagerInterface() {

		@Override
		public void getDataOnResult(ResultStatus status, List<?> list) {
			handleDataOnResult(status, list);
			if (mBaseDialog != null && status.getCode() == AnalysisManager.SUCCESS_DB_UPDATE) {
				mBaseDialog.setEditTextContent(6, GLOBAL.FLOW_APPROVAL_STATUS[4][1]);
				mBaseDialog.setButtonVisibility(2, false);
			}
		}
	};
	
	/**
	 * 显示对话框三个按钮，并设置监听处理器
	 */
	public void setAllDialogButton(boolean showSave, 
					boolean showSubmit, boolean showApproval) {
		if (mBaseDialog != null) { 
			mBaseDialog.setButtonVisibility(0, showSave);
			mBaseDialog.setButtonVisibility(1, showSubmit);
			mBaseDialog.setButtonVisibility(2, showApproval);
			mBaseDialog.setWhichButton(0, mContext.getResources()
							.getString(R.string.save), mDialogSaveListener);
			mBaseDialog.setWhichButton(1, mContext.getResources()
							.getString(R.string.submit), new OnClickListener() {
								
								@Override
								public void onClick(View v) {
									mIsSave = false;
									Map<String, String> saveMap = mDialog.SaveData();
									if (!saveMap.get(mContext.getString(R.string.payment_name)).isEmpty()) {
										mDocumentUploadView.uploadButtonEvent(false);
									} else {
										sendMessage(SHOW_TOAST, mContext.getString(R.string.contract_input_payment_name));
									}
								}
							});
			mBaseDialog.setWhichButton(2, mContext.getResources()
							.getString(R.string.approve), new OnClickListener() {
								
								@Override
								public void onClick(View v) {
									approval();
								}
							});
		}
	}
	
	/**
	 * 准备流程审批
	 */
	private void prepareFlowApproval() {
		if (mFlowApproval == null) {
			mFlowApproval = new Flow_approval();
		}
		mFlowApproval.setFlow_type(GLOBAL.FLOW_TYPE[1][0]);
		
		if (mFlowApprovalDialog == null) {
			mFlowApprovalDialog = new FlowApprovalDialog((Activity) mContext,
					mFlowApproval, mFlowSetting, mFlowApprovalManager);
		}
	}

	/**
	 * 转发返回数据
	 * @param requestCode
	 * @param resultCode
	 * @param data
	 */
	public void OnActivityResult(int requestCode, int resultCode, Intent data) {
		if (requestCode == FlowApprovalDialog.REQUEST_COUNTER) {
			if (mFlowApprovalDialog != null) {
				mFlowApprovalDialog.handleUserSelectResult(requestCode, resultCode, data);
			}
		} else {
			mDocumentUploadView.onActivityResult(requestCode, resultCode, data);
		}
	}
}
