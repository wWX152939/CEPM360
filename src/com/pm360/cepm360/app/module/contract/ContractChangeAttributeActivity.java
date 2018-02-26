package com.pm360.cepm360.app.module.contract;

import android.app.Activity;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.view.KeyEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.pm360.cepm360.R;
import com.pm360.cepm360.app.cache.CooperationCache;
import com.pm360.cepm360.app.cache.UserCache;
import com.pm360.cepm360.app.common.activity.ActionBarFragmentActivity;
import com.pm360.cepm360.app.common.activity.DataManagerInterface;
import com.pm360.cepm360.app.common.activity.ListSelectActivity;
import com.pm360.cepm360.app.common.custinterface.CallBack;
import com.pm360.cepm360.app.common.custinterface.ViewPagersInterface;
import com.pm360.cepm360.app.common.view.parent.BaseViewPager;
import com.pm360.cepm360.app.common.view.parent.FlowApprovalDialog;
import com.pm360.cepm360.app.common.view.parent.FlowApprovalDialog.FlowApprovalManager;
import com.pm360.cepm360.app.common.view.parent.list.BaseListCommon;
import com.pm360.cepm360.app.module.common.attachment.AttachmentActivity;
import com.pm360.cepm360.app.module.contract.pager.ContractChangeApplyAttribute;
import com.pm360.cepm360.app.module.contract.pager.ContractChangeAttachment;
import com.pm360.cepm360.app.module.contract.pager.ContractChangeAttribute;
import com.pm360.cepm360.app.module.contract.pager.ContractChangeDetail;
import com.pm360.cepm360.app.module.document.DocumentUploadActivity;
import com.pm360.cepm360.app.module.email.ComposeActivity;
import com.pm360.cepm360.common.GLOBAL;
import com.pm360.cepm360.common.ResultStatus;
import com.pm360.cepm360.common.json.AnalysisManager;
import com.pm360.cepm360.entity.Contract_change;
import com.pm360.cepm360.entity.Files;
import com.pm360.cepm360.entity.Flow_approval;
import com.pm360.cepm360.entity.Flow_setting;
import com.pm360.cepm360.entity.MailBox;
import com.pm360.cepm360.entity.Message;
import com.pm360.cepm360.entity.P_WBRGNR;
import com.pm360.cepm360.entity.P_WZ;
import com.pm360.cepm360.entity.User;
import com.pm360.cepm360.services.expenses.RemoteChangeContractService;
import com.pm360.cepm360.services.system.RemoteFlowSettingService;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

public class ContractChangeAttributeActivity extends ActionBarFragmentActivity 
						implements CallBack<Void, Integer> {

	// 请求码定义
	public static final int USER_SELECT_REQUEST_CODE = 1000;
	public static final int TENANT_SELECT_REQUEST_CODE = 1010;
	public static final int COOPERATION_REQUEST_CODE = 1020;
	
	public static final String ACTION_UPDATE_CONTRACT_CHANGE 
				= "com.pm360.cepm360.app.module.contract.Action.update.contractchange";
	
	public static final String CONTRACT_CHANGE_KEY = "contractChangeKey";
	public static final String EMAIL_INTER_KEY = "emailEnterKey";
	public static final String IS_MODIFY_KEY = "isModifyKey";
	public static final String IS_CHANGE_APPLY_KEY = "isChangeApplyKey";
	public static final String CHANGE_LIST_KEY = "changeListKey";
	public static final String TAB_INDEX_KEY = "tabIndexKey";
	public static final String FLOW_SETTING_KEY = "flowSettingKey";
	
	// 接收码
	public static final String PROJECT_KEY = "projectKey";
	
	// 增加合同视图索引
	public static final int INDEX_CONTRACT_BASIC_INFO = 0;
	public static final int INDEX_CONTRACT_DETAIL = 1;
	public static final int INDEX_CONTRACT_PAYMENT = 2;
	public static final int INDEX_CONTRACT_ATTACHMENT = 3;
	
	// ViewPager页面实现对象列表
	private List<ViewPagersInterface<?>> mPagerViews 
						= new ArrayList<ViewPagersInterface<?>>();
	
	// 实际对象保存
	private ContractChangeApplyAttribute mCommon;
	private ContractChangeAttribute mAttribute;
	private ContractChangeDetail mDetail;
	private ContractChangeAttachment mAttachment;
	
	// ViewPager管理器
	private BaseViewPager mBaseViewPager;
	private LinearLayout mButtonBar;
	private Button mSaveButton;
	private Button mSubmitButton;
	private Button mApprovalCooperationButton;
	
	// Activity标题
	private TextView mTitle;
	
	private boolean mIsModify;	// 是否可以修改
	private boolean mForceUnmodify; // 强制不能修改
	private boolean mCommonHasModify;	// 是否已经修改
	private boolean mIsChangeApply;		// 变更申请
	private boolean mIsMsgOpen;
	private boolean mIsEmailOpen;
	
	// 当前合同变更
	private Contract_change mContractChange;
	private List<Contract_change> mChanges;

	private int mTabIndex;
	
	// 流程申请
	private FlowApprovalDialog mFlowApprovalDialog;
	private Flow_setting mFlowSetting;
	private FlowApprovalManager mFlowApprovalManager;
	private Flow_approval mFlowApproval;
	
	@SuppressWarnings("rawtypes")
	private BaseListCommon mBaseListCommon;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		
		// 初始化基本布局
		initBasicLayout();
				
		// 获取传入的参数
		getArgument();
	}
	
	/**
	 * 初始化剩下内容
	 */
	private void initRemain() {
		
		if (mIsChangeApply) {

			// 初始化审批回调接口
			initFlowApprovalManager();
			
			// 初始化审批流程对话框
			prepareFlowApproval();
		}
		
		// 初始化按钮
		initButton();
		
		// 创建页视图列表
		buildPagerViews();
		
		// 初始化ViewPager管理器
		initBaseViewPager();
	}
	
	/**
	 * 获取传入的参数
	 */
	@SuppressWarnings({ "unchecked" })
	private void getArgument() {
		Intent intent = getIntent();
		if (intent.getAction() == null) {
			mContractChange = (Contract_change) intent.getSerializableExtra(CONTRACT_CHANGE_KEY);
			mIsModify = intent.getBooleanExtra(IS_MODIFY_KEY, false);
			mIsChangeApply = intent.getBooleanExtra(IS_CHANGE_APPLY_KEY, false);
			mChanges = (List<Contract_change>) intent.getSerializableExtra(CHANGE_LIST_KEY);
			mTabIndex = intent.getIntExtra(TAB_INDEX_KEY, 0);
			mIsEmailOpen = intent.getBooleanExtra(EMAIL_INTER_KEY, false);
			mFlowSetting = (Flow_setting) intent.getSerializableExtra(FLOW_SETTING_KEY);
			if (mIsEmailOpen) {
				mBaseListCommon.sendEmptyMessageDelayed(BaseListCommon.SHOW_PROGRESS_DIALOG);
				RemoteFlowSettingService.getInstance().getFlowDetail(
						new DataManagerInterface() {
							
							@Override
							public void getDataOnResult(ResultStatus status, List<?> list) {
								if (status.getCode() == AnalysisManager.SUCCESS_DB_QUERY) {
									if (!list.isEmpty()) {
										mFlowSetting = (Flow_setting) list.get(0);
										RemoteChangeContractService.getInstance().getChangeContractMoney(
												changeMoneyManagerInterface, mContractChange.getId() + "");
									} else {
										mBaseListCommon.sendMessage(BaseListCommon.DISMISS_PROGRESS_DIALOG);
										mBaseListCommon.sendMessage(BaseListCommon.SHOW_TOAST, 
												getString(R.string.cannot_query_specified_record));
									}
								} else {
									mBaseListCommon.sendMessage(BaseListCommon.DISMISS_PROGRESS_DIALOG);
									mBaseListCommon.sendMessage(BaseListCommon.SHOW_TOAST, 
											status.getMessage());
								}
							}
						}, UserCache.getTenantId(), GLOBAL.FLOW_TYPE[3][0]);
			} else {
				initRemain();
			}
		} else {
			mIsModify = true;
			mIsChangeApply = true;
			mIsMsgOpen = true;
			
			// 查询合同变更审批详情
			mBaseListCommon.sendMessage(BaseListCommon.SHOW_PROGRESS_DIALOG);
			
			RemoteFlowSettingService.getInstance().getFlowDetail(
					new DataManagerInterface() {
						
						@Override
						public void getDataOnResult(ResultStatus status, List<?> list) {
							if (status.getCode() == AnalysisManager.SUCCESS_DB_QUERY) {
								if (!list.isEmpty()) {
									mFlowSetting = (Flow_setting) list.get(0);
								}
							}
						}
					}, UserCache.getTenantId(), GLOBAL.FLOW_TYPE[3][0]);
			
			// 获取合同变更详情
			Message message = (Message) intent.getSerializableExtra(GLOBAL.MSG_OBJECT_KEY);
			RemoteChangeContractService.getInstance()
						.getChangeContractIDByFlowApprovalID(new DataManagerInterface() {
				
				@Override
				public void getDataOnResult(ResultStatus status, List<?> list) {
					if (status.getCode() == AnalysisManager.SUCCESS_DB_QUERY) {
						if (!list.isEmpty()) {
							
							// 再次请求服务器
							RemoteChangeContractService.getInstance()
									.getChangeContractDetail(new DataManagerInterface() {
								
								@Override
								public void getDataOnResult(ResultStatus status, List<?> list) {
									if (status.getCode() == AnalysisManager.SUCCESS_DB_QUERY) {
										
										if (!list.isEmpty()) {
											mContractChange = (Contract_change) list.get(0);
											RemoteChangeContractService.getInstance().getChangeContractMoney(
														changeMoneyManagerInterface, mContractChange.getId() + "");
										} else {
											mBaseListCommon.sendMessage(BaseListCommon.DISMISS_PROGRESS_DIALOG);
											mBaseListCommon.sendMessage(BaseListCommon.SHOW_TOAST, 
													getString(R.string.cannot_query_specified_record));
										}
									} else {
										mBaseListCommon.sendMessage(BaseListCommon.DISMISS_PROGRESS_DIALOG);
										mBaseListCommon.sendMessage(BaseListCommon.SHOW_TOAST, 
												status.getMessage());
									}
								}
							}, ((Flow_approval) list.get(0)).getType_id(), UserCache.getTenantId());
						}  else {
							mBaseListCommon.sendMessage(BaseListCommon.DISMISS_PROGRESS_DIALOG);
							mBaseListCommon.sendMessage(BaseListCommon.SHOW_TOAST, 
									getString(R.string.cannot_query_specified_record));
						}
					}  else {
						mBaseListCommon.sendMessage(BaseListCommon.DISMISS_PROGRESS_DIALOG);
						mBaseListCommon.sendMessage(BaseListCommon.SHOW_TOAST, 
								status.getMessage());
					}
				}
			}, message.getType_id());
		}
	}
	
	/*
	 * 合同变更金额处理
	 */
	private DataManagerInterface changeMoneyManagerInterface = new DataManagerInterface() {
		
		@Override
		public void getDataOnResult(ResultStatus status, List<?> list) {
			mBaseListCommon.sendMessage(BaseListCommon.DISMISS_PROGRESS_DIALOG);
			if (status.getCode() == AnalysisManager.SUCCESS_DB_QUERY) {
				if (!list.isEmpty()) {
					mContractChange.setBqbgk(((Contract_change) list.get(0)).getBqbgk()); 
				}
				
				// 初始化
				initRemain();
			}
		}
	};
	
	/**
	 * 初始化基本布局
	 */
	@SuppressWarnings("rawtypes")
	private void initBasicLayout() {

		// 设置Activity的显示视图
		setContentView(R.layout.contract_attribute_layout);
		
		// 标题初始化
		mTitle = (TextView) findViewById(R.id.title);
		mTitle.setText(getResources().getString(R.string.contract_change_attribute_title));
		
		// 按钮栏初始化
		mButtonBar = (LinearLayout) findViewById(R.id.button_bar);
		mSaveButton = (Button) mButtonBar.findViewById(R.id.btn_left);
		mSubmitButton = (Button) mButtonBar.findViewById(R.id.btn_middle);
		mApprovalCooperationButton = (Button) mButtonBar.findViewById(R.id.btn_right);
		
		mSaveButton.setText(getString(R.string.save));
		mSubmitButton.setText(getString(R.string.submit));
		
		// 消息跳过来首先隐藏按钮
		if (getIntent().getAction() != null) {
			mButtonBar.setVisibility(View.GONE);
		}
		
		// 当返回时，如果合同变更更新，返回更新合同变更对象
		getBackView().setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View v) {
				doFinish();
			}
		});
		
		mBaseListCommon = new BaseListCommon(this) {

			@Override
			public Object getListAdapter() {
				return null;
			}
		};
		mBaseListCommon.init();
	}
	
	/**
	 * 初始化按钮
	 */
	private void initButton() {
		if (!mIsModify) {
			if (mFlowSetting != null
					&& mFlowSetting.getStatus() == 1	// 审批开关开启
					&& mContractChange != null
					&& mContractChange.getStatus() 		// 不是未提交
						!= Integer.parseInt(GLOBAL.CONTRACT_CHANGE_STATUS[2][0])) { 
				mButtonBar.setVisibility(View.VISIBLE);
				showApprovalOrCooperation(true);
			} else {
				mButtonBar.setVisibility(View.GONE);
			}
		} else {
			mButtonBar.setVisibility(View.VISIBLE);
			
			// 切换按钮
			switchButton();
		}
	}
	
	/*
	 * 切换button控件显示
	 */
	private void switchButton() {
		switch (mContractChange.getStatus()) {
			case 2:	// 协作中
				mSaveButton.setVisibility(View.GONE);
				mSubmitButton.setVisibility(View.GONE);
				showApprovalOrCooperation(true);
				break;
			case 4:	// 内部审批中
				mSaveButton.setVisibility(View.GONE);
				mSubmitButton.setVisibility(View.GONE);
				showApprovalOrCooperation(true);
				
				// 强制不能修改
				mForceUnmodify = true;
				break;
			case 3:	// 未提交
				mSaveButton.setVisibility(View.VISIBLE);
				switchSubmitAndApproval(true);
				mApprovalCooperationButton.setVisibility(View.GONE);
				break;
			case 5:	// 内部通过
				mSaveButton.setVisibility(View.GONE);
				switchSubmitAndApproval(false);
				showApprovalOrCooperation(false);
				
				// 强制不能修改
				mForceUnmodify = true;
				break;
			case 6:	// 内部驳回
				mSaveButton.setVisibility(View.VISIBLE);
				switchSubmitAndApproval(true);
				showApprovalOrCooperation(true);
				break;
			default:
				break;
		}
	}
	
	/*
	 * 将提交按钮切换为审批按钮
	 * @param isSubmit 是否是提交按钮
	 */
	private void switchSubmitAndApproval(boolean isSubmit) {
		mSubmitButton.setVisibility(View.VISIBLE);
		
		if (isSubmit) {
			mSubmitButton.setText(getString(R.string.submit));
			mSubmitButton.setOnClickListener(new OnClickListener() {
				
				@Override
				public void onClick(View v) {
					submit();
				}
			});
		} else {
			mSubmitButton.setText(getString(R.string.approve));
			mSubmitButton.setOnClickListener(new OnClickListener() {
				
				@Override
				public void onClick(View v) {
					approval();
				}
			});
		}
	}
	
	/*
	 * 显示审批或协作按钮
	 */
	private void showApprovalOrCooperation(boolean isApproval) {
		mApprovalCooperationButton.setVisibility(View.VISIBLE);
		
		if (isApproval) {
			mApprovalCooperationButton.setText(getString(R.string.approve));
			mApprovalCooperationButton.setOnClickListener(new OnClickListener() {
				
				@Override
				public void onClick(View v) {
					approval();
				}
			});
		} else {
			mApprovalCooperationButton.setText(getString(R.string.cooperation));
			mApprovalCooperationButton.setOnClickListener(new OnClickListener() {
				
				@Override
				public void onClick(View v) {
					startCooperation();
				}
			});
		}
	}
	
	/**
	 * 审批按钮相应函数
	 */
	private void approval() {
		if (mFlowApprovalDialog != null) {
			mFlowApprovalDialog.show(mContractChange.getStatus() 
					== Integer.parseInt(GLOBAL.CONTRACT_CHANGE_STATUS[3][0]));
		}
	}
	
	/*
	 * 提交变更
	 */
	private void submit() {
		BaseListCommon.showAlertDialog(this, 
				getString(R.string.confirm_submit), 
				new DialogInterface.OnClickListener() {
			
			@Override
			public void onClick(DialogInterface dialog, int which) {
				
				// 发送延时显示进度对话框消息
				mDetail.sendEmptyMessageDelayed(BaseListCommon.SHOW_PROGRESS_DIALOG);
				
				if (mFlowSetting.getStatus() == Integer.parseInt(GLOBAL.FLOW_STATUS[0][0])) {
					RemoteChangeContractService.getInstance().updateChangeContractForSubmit(dataManagerInterface, 
							mContractChange, getSubmitFlowApproval());
				} else {
					mContractChange.setStatus(Integer
							.parseInt(GLOBAL.CONTRACT_CHANGE_STATUS[4][0]));
					RemoteChangeContractService.getInstance()
							.updateChangeContract(dataManagerInterface, mContractChange);
				}
			}
		});
	}
	
	/**
	 * 发起协作
	 */
	private void startCooperation() {
    	final Intent intent = new Intent(this, ComposeActivity.class);
   	 	intent.putExtra(ComposeActivity.EMAIL_OPERATION_KEY, ComposeActivity.OPERATION_NEW);
   	 	intent.putExtra(ComposeActivity.EMAIL_CONTRACT_CHANGE_KEY, mContractChange);
	   	intent.putExtra(ComposeActivity.EMAIL_PROJECT_KEY, mContractChange.getProject_id());
		CooperationCache.getContact(mContractChange.getProject_id(), 
				mContractChange.getReceiver(), 
				mContractChange.getReceive_contact(),
				new CallBack<Void, User>() {
				
				@Override
				public Void callBack(User a) {
					ArrayList<User> users = new ArrayList<User>();
					users.add(a);
					users.add(UserCache.getUserById(mContractChange.getSender_contact() + ""));
					intent.putExtra(ComposeActivity.EMAIL_WRITE_TO_KEY, users);
			   	 	startActivityForResult(intent, COOPERATION_REQUEST_CODE);
					return null;
				}
			});
    }
	
	/**
	 * 准备流程审批
	 */
	private void prepareFlowApproval() {
		if (mFlowApproval == null) {
			mFlowApproval = new Flow_approval();
		}
		mFlowApproval.setFlow_type(GLOBAL.FLOW_TYPE[3][0]);
		mFlowApproval.setType_id(mContractChange.getId());
		
		if (mFlowApprovalDialog == null) {
			mFlowApprovalDialog = new FlowApprovalDialog(this,
					mFlowApproval, mFlowSetting, mFlowApprovalManager);
		}
	}
	
	/*
	 * 初始化回调接口
	 */
	private void initFlowApprovalManager() {
		
		// 审批通过和驳回的回调函数
		mFlowApprovalManager = new FlowApprovalManager() {
			
			@Override
			public void rebutFlowData2Server() {
				mContractChange.setStatus(Integer.parseInt(GLOBAL.CONTRACT_CHANGE_STATUS[5][0]));
				updateItem();
			}
			
			@Override
			public void passFlowData2Server() {
				mContractChange.setStatus(Integer.parseInt(GLOBAL.CONTRACT_CHANGE_STATUS[4][0]));
				updateItem();
			}
		};
	}
	
	/**
	 * 审批通过或拒绝时，更新对象
	 */
	private void updateItem() {
		
		// 发送延时显示进度对话框消息
		mDetail.sendEmptyMessageDelayed(BaseListCommon.SHOW_PROGRESS_DIALOG);
		
		RemoteChangeContractService.getInstance().updateChangeContract(
				new DataManagerInterface() {
					
					@Override
					public void getDataOnResult(ResultStatus status, List<?> list) {
						mDetail.sendMessage(BaseListCommon.DISMISS_PROGRESS_DIALOG);
						
						switch (status.getCode()) {
							case AnalysisManager.SUCCESS_DB_UPDATE:
								mCommonHasModify = true;
								switchButton();
								break;
							case AnalysisManager.EXCEPTION_DB_UPDATE:
								mContractChange.setStatus(Integer
											.parseInt(GLOBAL.CONTRACT_CHANGE_STATUS[3][0]));
								break;
							default:
								break;
						}
						
						mDetail.sendMessage(BaseListCommon.SHOW_TOAST, status.getMessage());
					}
				}, mContractChange);
	}
	
	/**
	 * 设置按钮栏是否可见
	 * @param visible
	 */
	public void setButtonBarVisibility(boolean visible) {
		if (visible) {
			mButtonBar.setVisibility(View.VISIBLE);
		} else {
			mButtonBar.setVisibility(View.GONE);
		}
	}
	
	/**
	 * 创建页视图列表
	 */
	private void buildPagerViews() {
		
		// 构建常用视图
		buildCommon();
		
		// 构建清单视图
		buildDetailListView();
		
		// 构建附件视图
		buildAttachmentView();
	}
	
	/**
	 * 初始化ViewPager管理器
	 */
	private void initBaseViewPager() {
		View pagerView = findViewById(R.id.baseViewPager);
		mBaseViewPager = new BaseViewPager(this, pagerView);
		
		// 初始化ViewPager
		mBaseViewPager.init(R.array.change_contract_view_pager_titles, null, mPagerViews);
		mBaseViewPager.setCurrentParentBean(mContractChange);
		
		// 跳到指定页面
		mBaseViewPager.onButtonClick(mTabIndex);
	}
	
	/*
	 * 保存、提交
	 */
	DataManagerInterface dataManagerInterface = new DataManagerInterface() {
		
		@Override
		public void getDataOnResult(ResultStatus status, List<?> list) {
			mDetail.sendMessage(BaseListCommon.DISMISS_PROGRESS_DIALOG);
			
			switch (status.getCode()) {
				case AnalysisManager.SUCCESS_DB_UPDATE:
					mCommonHasModify = true;
					mCommon.handleParentEvent(mContractChange);
					switchButton();
					break;
				default:
					break;
			}
			
			mDetail.sendMessage(BaseListCommon.SHOW_TOAST, status.getMessage());
		}
	};
	
	/**
	 * 构建常用视图
	 */
	private void buildCommon() {
		if (mIsChangeApply) {
			mCommon = new ContractChangeApplyAttribute(this, mContractChange, mIsModify && !mForceUnmodify);
			mCommon.getBaseWindow().switchModifyWindow(mIsModify && !mForceUnmodify);
			mPagerViews.add(mCommon);
			
			// 注册保存按钮监听
			mSaveButton.setOnClickListener(new OnClickListener() {
				
				@Override
				public void onClick(View v) {
					handleCommonSave();
				}
			});
		} else {
			mAttribute = new ContractChangeAttribute(this, mContractChange, mChanges);
			mAttribute.getBaseWindow().switchModifyWindow(false);
			mPagerViews.add(mAttribute);
		}
	}
	
	/**
	 * 获取提交时生成的Flow_approval对象
	 * @return
	 */
	private Flow_approval getSubmitFlowApproval() {
		Flow_approval flowApproval = new Flow_approval();
		flowApproval.setCurrent_level(mFlowSetting.getLevel1());
		flowApproval.setFlow_type(GLOBAL.FLOW_TYPE[3][0]);
		flowApproval.setNext_level(mFlowSetting.getLevel2());
		flowApproval.setStatus(Integer.parseInt(GLOBAL.FLOW_APPROVAL_STATUS[1][0]));
		flowApproval.setSubmit_time(new Date());
		flowApproval.setSubmiter(UserCache.getCurrentUserId());
		flowApproval.setTenant_id(UserCache.getTenantId());
		flowApproval.setType_id(mContractChange.getId());
		
		return flowApproval;
	}
	
	/*
	 * 处理common保存按钮
	 */
	private void handleCommonSave() {
		
		// 发送延时显示进度对话框消息
		mDetail.sendEmptyMessageDelayed(BaseListCommon.SHOW_PROGRESS_DIALOG);
		
		RemoteChangeContractService.getInstance().updateChangeContract(
				dataManagerInterface, mCommon.updateContractChange());
	}
	
	/**
	 * 构建清单视图
	 */
	private void buildDetailListView() {
		mDetail = new ContractChangeDetail(this, mContractChange, mIsModify && !mForceUnmodify, false);
		mPagerViews.add(mDetail);
	}
	
	/**
	 * 构建附件视图
	 */
	private void buildAttachmentView() {
		mAttachment = new ContractChangeAttachment(this, mIsModify && !mForceUnmodify, false, false);
		mPagerViews.add(mAttachment);
	}
	
	@SuppressWarnings("unchecked")
	@Override
	protected void onActivityResult(int requestCode, int resultCode, Intent data) {
		super.onActivityResult(requestCode, resultCode, data);
		
		if (resultCode == Activity.RESULT_OK) {
			switch (requestCode) {
				case ContractChangeDetail.MATERIAL_SELECT_REQUEST_CODE:
					P_WZ material = (P_WZ) data
								.getSerializableExtra(ListSelectActivity.RESULT_KEY);
					mDetail.setAddModifySource(material);
					break;
				case ContractChangeDetail.EQUIPMENT_SELECT_REQUEST_CODE:
					P_WZ equiment = (P_WZ) data
							.getSerializableExtra(ListSelectActivity.RESULT_KEY);
					mDetail.setAddModifySource(equiment);
					break;
				case ContractChangeDetail.LABOUR_SELECT_REQUEST_CODE:
					P_WBRGNR labour = (P_WBRGNR) data
							.getSerializableExtra(ListSelectActivity.RESULT_KEY);
					mDetail.setAddModifySource(labour);
					break;
				case ContractChangeAttachment.FILE_SELECT_REQEUST_CODE:
					List<Files> files = (List<Files>) data.getSerializableExtra(
							DocumentUploadActivity.UPLOAD_FILE_LIST_KEY);
					mAttachment.addFileListToAdapter(files);
					break;
				case ContractChangeApplyAttribute.USER_SELECT_REQUEST_CODE:
					User user = (User) data.getSerializableExtra("user");
					if (user != null) {
						mCommon.getBaseWindow().
									setUserTextContent(10, user.getUser_id());
					}
					break;
				case ContractChangeApplyAttribute.USER_SELECT_REQUEST_CODE + 1:
					User user1 = (User) data.getSerializableExtra("user");
					if (user1 != null) {
						mCommon.setRecieverUser(user1);
					}
					break;
				case ContractChangeDetail.FILE_SELECT_REQUEST_CODE:
					List<Files> fileList = (List<Files>) data
							.getSerializableExtra(AttachmentActivity.RESULT_ATTACH_LIST);
					if (fileList != null) {
						mDetail.setFileList(fileList);
					}
					break;
				case FlowApprovalDialog.REQUEST_COUNTER:
					if (mFlowApprovalDialog != null) {
						mFlowApprovalDialog.handleUserSelectResult(requestCode, resultCode, data);
					}
					break;
				case COOPERATION_REQUEST_CODE:
					
					// 获取协作产生的邮件对象，用以保存到合同变更中
					MailBox mailBox = (MailBox) data
								.getSerializableExtra(ComposeActivity.MAILBOX_KEY);
					
					if (mailBox != null) {
						mBaseListCommon.sendMessage(BaseListCommon.SHOW_TOAST, 
								getString(R.string.email_send_success));
						mContractChange.setStatus(Integer
								.parseInt(GLOBAL.CONTRACT_CHANGE_STATUS[1][0]));
						RemoteChangeContractService.getInstance()
								.updateChangeContract(dataManagerInterface, mContractChange);
						
					} else {
						mBaseListCommon.sendMessage(BaseListCommon.SHOW_TOAST, 
								getString(R.string.email_send_failed));
					}
				default:
					mDetail.OnActivityResult(requestCode, resultCode, data);
					break;
			}
		}
	}

	@Override
	public Void callBack(Integer a) {
		switch (a) {
			case ContractChangeApplyAttribute.CONTRACT_CHANGE_COMMON_INDEX:
				setButtonBarVisibility(mIsModify);
				break;
			case ContractChangeDetail.CONTRACT_CHANGE_DETIAL_INDEX:
				// fall through
			case ContractChangeAttachment.CONTRACT_CHANGE_COMMON_INDEX:
				setButtonBarVisibility(false);
				break;
			default:
				break;
		}
		
		return null;
	}
	
	@Override
	public boolean onKeyDown(int keyCode, KeyEvent event) {
		if (keyCode == KeyEvent.KEYCODE_BACK) {
			doFinish();
		}
		
		return false;
	}
	
	/**
	 * 退出Activity
	 */
	private void doFinish() {
		if (mCommonHasModify 
				|| mAttachment.hasModify()
				|| mDetail.hasModify()) {
			Intent intent = new Intent();
			intent.putExtra(CONTRACT_CHANGE_KEY, mContractChange);
			setResult(Activity.RESULT_OK, intent);
			
			if (mIsMsgOpen) {
				intent.setAction(ACTION_UPDATE_CONTRACT_CHANGE);
				sendBroadcast(intent);
			}
		}
		
		finish();
	}
}
