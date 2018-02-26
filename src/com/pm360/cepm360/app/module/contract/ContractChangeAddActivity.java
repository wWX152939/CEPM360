package com.pm360.cepm360.app.module.contract;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.FrameLayout;
import android.widget.FrameLayout.LayoutParams;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.pm360.cepm360.R;
import com.pm360.cepm360.app.cache.UserCache;
import com.pm360.cepm360.app.common.activity.DataManagerInterface;
import com.pm360.cepm360.app.common.activity.ListSelectActivity;
import com.pm360.cepm360.app.common.custinterface.ServiceInterface;
import com.pm360.cepm360.app.common.view.CustomTranslateAnimation;
import com.pm360.cepm360.app.common.view.parent.list.BaseListCommon;
import com.pm360.cepm360.app.module.contract.pager.ContractChangeApplyAttribute;
import com.pm360.cepm360.app.module.contract.pager.ContractChangeAttachment;
import com.pm360.cepm360.app.module.contract.pager.ContractChangeDetail;
import com.pm360.cepm360.app.module.document.DocumentUploadActivity;
import com.pm360.cepm360.app.module.email.ComposeActivity;
import com.pm360.cepm360.app.utils.UtilTools;
import com.pm360.cepm360.common.GLOBAL;
import com.pm360.cepm360.common.ResultStatus;
import com.pm360.cepm360.common.json.AnalysisManager;
import com.pm360.cepm360.entity.Contract;
import com.pm360.cepm360.entity.Contract_change;
import com.pm360.cepm360.entity.Contract_list;
import com.pm360.cepm360.entity.Files;
import com.pm360.cepm360.entity.Flow_approval;
import com.pm360.cepm360.entity.Flow_setting;
import com.pm360.cepm360.entity.MailBox;
import com.pm360.cepm360.entity.P_WBRGNR;
import com.pm360.cepm360.entity.P_WZ;
import com.pm360.cepm360.entity.Project;
import com.pm360.cepm360.entity.Tenant;
import com.pm360.cepm360.entity.User;
import com.pm360.cepm360.services.document.RemoteDocumentsService;
import com.pm360.cepm360.services.expenses.RemoteChangeContractService;
import com.pm360.cepm360.services.expenses.RemoteExpensesContractService;
import com.pm360.cepm360.services.expenses.RemoteRevenueContractService;

import java.io.File;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

public class ContractChangeAddActivity extends Activity {
	
	// 请求码
	public static final int COOPERATION_REQUEST_CODE = 7100;
	
	// 参数key
	public static final String CONTRACT_KEY = "contractKey";
	public static final String INCOME_CONTRACT_KEY = "incomeContractKey";
	
	// 增加合同视图索引
	public static final int INDEX_CONTRACT_CHANGE_BASIC_INFO = 0;
	public static final int INDEX_CONTRACT_CHANGE_DETAIL = 1;
	public static final int INDEX_CONTRACT_CHANGE_ATTACHMENT = 2;
	
	// 返回码定义
	public static final String RESULT_KEY = "result_key";
	public static final String DETAIL_FAILED_KEY = "detail_failed_key";
	public static final String PAYMENT_FAILED_KEY = "payment_failed_key";
	public static final String ATTACHMENT_FAILED_KEY = "attachment_failed_key";
	public static final String FLOW_SETTING_KEY = ContractChangeAttributeActivity.FLOW_SETTING_KEY;
	
	// 接收码
	public static final String PROJECT_KEY = "projectKey";
	
	// 合同视图数组
	List<View> mContractAddViews = new ArrayList<View>();
	
	// 标题控件
	private TextView mTitle;
	
	// 内容根布局
	private FrameLayout mContentLayout;
	
	// 按键根布局
	private LinearLayout mButtonRootLayout;
	private Button mPreviousButton;
	private Button mNextButton;
	private Button mSaveButton;
	private Button mCommitButton;
	private Button mCooperationButton;
	
	private ImageView mCloseImageView;
	
	// 添加合同的视图封装对象
	private ContractChangeApplyAttribute mContractChangeCommon;
	private ContractChangeDetail mDetailView;
	private ContractChangeAttachment mAttachmentView;
	
	private BaseListCommon<Serializable> mBaseListCommon;
	
	// 添加合同各视图的保存数据
	private Contract mContract;
	private Contract_change mContractChange;
	private List<Contract_list> mDetailList = new ArrayList<Contract_list>();
	private List<Files> mAttachmentList = new ArrayList<Files>();
	private List<Files> mAttachmentUploaded = new ArrayList<Files>();
	
	// 各个视图添加修改数据弹出对话框中的选中对象
	private Serializable mSelectItem;
	
	// 当前视图的索引
	private int mCurrentIndex;
	private int mOldIndex;
	private Project mProject;
	
	// 合同清单项，合同支付项，合同附件ID
	private int mDetailCountId = 1;
	private int mAttachmentCountId = 1;
	
	// 合同清单项，合同支付项，合同附件添加情况
	private int mDetailAddCount;
	private int mAttachmentAddCount;
	
	private int mDetailAddFailedCount;
	private int mAttachmentAddFailedCount;
	
	// 是否提交或协作
	private boolean mIsSubmit;
	private boolean mIsCooperation;
	
	private Flow_setting mFlowSetting;
	
	// 服务
	RemoteRevenueContractService mRevenueContractService 
							= RemoteRevenueContractService.getInstance();
	RemoteExpensesContractService mExpensesContractService 
							= RemoteExpensesContractService.getInstance();
	RemoteChangeContractService mChangeContractService 
							= RemoteChangeContractService.getInstance();
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		
		// 设置Activity布局
		setContentView(R.layout.add_contract_change_activity);
		
		// 获取传递过来的参数
		getArgument();
		
		// 初始化标题
		initLayout();
		
		// 初始化内容视图
		initContentView();
		
		// 首先定位第一个视图
		switchView();
	}
	
	/**
	 * 获取参数
	 * @return
	 */
	private void getArgument() {
		Intent intent = getIntent();
		mProject = (Project) intent.getSerializableExtra(PROJECT_KEY);
		mFlowSetting = (Flow_setting) intent.getSerializableExtra(FLOW_SETTING_KEY);
	}
	
	/**
	 * 初始化Activity标题
	 */
	@SuppressLint("CutPasteId") 
	private void initLayout() {
		mBaseListCommon = new BaseListCommon<Serializable>(this) {

			@Override
			public Object getListAdapter() {
				return null;
			}
		};
		mBaseListCommon.init();
		
		// 标题初始化
		mTitle = (TextView) findViewById(R.id.title);
		mTitle.setText(getResources().getString(R.string.contract_change_add_title));
		
		// 初始化内容布局
		mContentLayout = (FrameLayout) findViewById(R.id.content_frame);
		
		// 初始化按键根布局
		mButtonRootLayout = (LinearLayout) findViewById(R.id.button_bar);
		mButtonRootLayout.setVisibility(View.VISIBLE);
		
		// 上一步
		mPreviousButton = (Button) mButtonRootLayout.findViewById(R.id.btn1);
		mPreviousButton.setVisibility(View.GONE);
		mPreviousButton.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View v) {
				
				// 先保存数据
				saveData();
				
				mOldIndex = mCurrentIndex;
				--mCurrentIndex;
				
				// 切换视图
				switchView();
			}
		});
		
		// 下一步
		mNextButton = (Button) mButtonRootLayout.findViewById(R.id.btn2);
		mNextButton.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View v) {
					
				// 先保存数据
				saveData();
				
				if (mContractChange != null) {
					mOldIndex = mCurrentIndex;
					++mCurrentIndex;
					
					// 切换视图
					switchView();
				} else {
					mBaseListCommon.sendMessage(BaseListCommon.SHOW_TOAST, getString(R.string.select_contract_first));
				}
			}
		});
		
		// 保存
		mSaveButton = (Button) mButtonRootLayout.findViewById(R.id.btn3);
		mSaveButton.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View v) {
				startCreate();
			}
		});
		
		// 提交
		mCommitButton = (Button) mButtonRootLayout.findViewById(R.id.btn4);
		mCommitButton.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View v) {
				mIsSubmit = true;
				startCreate();
			}
		});
		
		// 协作
		mCooperationButton = (Button) mButtonRootLayout.findViewById(R.id.btn5);
		if (mFlowSetting.getStatus() == Integer.parseInt(GLOBAL.FLOW_STATUS[0][0])) {
			mCooperationButton.setVisibility(View.GONE);
		} else {
			mCooperationButton.setOnClickListener(new OnClickListener() {
				
				@Override
				public void onClick(View v) {
					mIsCooperation = true;
					startCreate();
				}
			});
		}
		
		// 暂时关闭添加模式下的协作功能
		mCooperationButton.setVisibility(View.GONE);
		
		mCloseImageView = (ImageView) findViewById(R.id.btn_close);
		mCloseImageView.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View v) {
				setResult(Activity.RESULT_OK);
				finish();
			}
		});
		
		mCurrentIndex = INDEX_CONTRACT_CHANGE_BASIC_INFO;
	}
	
	/**
	 * 开始创建过程
	 */
	private void startCreate() {
		
		// 先保存数据
		saveData();
		
		if (checkValid()) {
			
			// 创建合同变更
			createContractChange();
		} else {
			mBaseListCommon.sendMessage(BaseListCommon.SHOW_TOAST, 
					getString(R.string.info_fill_no_completion));
		}
	}
	
	/**
	 * 判断合同变更信息有效性
	 * @return
	 */
	private boolean checkValid() {
		boolean valid = true;
		
		if (mContractChange != null) {
			String contractChangeName = mContractChange.getName();
			if (contractChangeName == null 
					|| contractChangeName.isEmpty()) {
				valid = false;
			}
		} else {
			valid = false;
		}
		
		return valid;
	}
	
	/**
	 * 发起协作
	 */
	private void startCooperation() {
    	Intent intent = new Intent(this, ComposeActivity.class);
   	 	intent.putExtra(ComposeActivity.EMAIL_OPERATION_KEY, ComposeActivity.OPERATION_NEW);
   	 	intent.putExtra(ComposeActivity.EMAIL_CONTRACT_CHANGE_KEY, mContractChange);
   	 	startActivityForResult(intent, COOPERATION_REQUEST_CODE);
    }
	
	/**
	 * 返回数据
	 * @param contract
	 * @param detail
	 * @param payment
	 * @param attachment
	 */
	private void dataBack(boolean isCancle) {
		
		// 退出进度对话框
		mBaseListCommon.sendMessage(BaseListCommon.DISMISS_PROGRESS_DIALOG);
		
		Intent intent = new Intent();
		if (!isCancle) {
			
			// 传回选中的列表数据
			intent.putExtra(RESULT_KEY, mContractChange);
			intent.putExtra(DETAIL_FAILED_KEY, mDetailAddFailedCount);
			intent.putExtra(ATTACHMENT_FAILED_KEY, mAttachmentAddFailedCount);
			
			setResult(Activity.RESULT_OK, intent);
		} else {
			setResult(Activity.RESULT_CANCELED, intent);
		}
		
		finish();
	}
	
	/**
	 * 保存个页面数据数据
	 */
	public void saveData() {
		switch (mCurrentIndex) {
			case INDEX_CONTRACT_CHANGE_BASIC_INFO:
				saveCommonData();
				break;
			default:
				break;
		}
	}
	
	/**
	 * 保存合同基本信息
	 */
	public void saveCommonData() {
		mContractChange = mContractChangeCommon.getContractChange(mProject);
		if (mContractChange != null) {
			mContractChange.setBqbgk(getChangeTotalMoney());
		}
	}
	
	/**
	 * 创建合同变更
	 */
	private void createContractChange() {
		
		// 显示进度
		mBaseListCommon.sendMessage(BaseListCommon.SHOW_PROGRESS_DIALOG);
		
		// 初始化添加数量
		mDetailAddCount = mDetailList.size();
		mAttachmentAddCount = mAttachmentList.size();
		
		// 实现添加合同回调接口
		DataManagerInterface managerInterface = new DataManagerInterface() {
			
			@Override
			public void getDataOnResult(ResultStatus status, List<?> list) {
				switch (status.getCode()) {
					case AnalysisManager.SUCCESS_DB_ADD:
						Contract_change contractChange = (Contract_change) list.get(0);
						mContractChange.setYdhtwgrq(mContract.getEnd_date());
						mContractChange.setId(contractChange.getId());
						mAttachmentView.setContractChange(mContractChange);
						
						// 检查是否完成
						if (mDetailAddCount == 0
								&& mAttachmentAddCount == 0) {
							prepareFinish();
						} else {
						
							// 创建清单项
							createDetailList();
							
							// 创建附件列表
							createAttachmentList();
						}
						break;
					case AnalysisManager.EXCEPTION_DB_ADD:
						mBaseListCommon.sendMessage(BaseListCommon.SHOW_TOAST,
								status.getMessage());
						mBaseListCommon.sendMessage(BaseListCommon.DISMISS_PROGRESS_DIALOG);
						break;
					default:
						break;
				} 
			}
		};
				
		if (mFlowSetting.getStatus() 
				== Integer.parseInt(GLOBAL.FLOW_STATUS[0][0])) { // 启动审批
			if (mIsSubmit) {
				
				// 提交或协作按钮指示合同变更首先进入“内部审批中”状态
				mContractChange.setStatus(Integer
						.parseInt(GLOBAL.CONTRACT_CHANGE_STATUS[3][0]));
				mChangeContractService.addContractChangeForSubmit(managerInterface, 
						mContractChange, getSubmitFlowApproval());
			} else {
			
				// 首先将合同变更设置为“未提交”状态
				mContractChange.setStatus(Integer
						.parseInt(GLOBAL.CONTRACT_CHANGE_STATUS[2][0]));
				mChangeContractService.addContractChange(managerInterface, mContractChange);
			}
		} else {
			
			if (mIsSubmit || mIsCooperation) {
				
				// 如果没有启动审批，提交或发起协作将直接置为审批通过
				mContractChange.setStatus(Integer
						.parseInt(GLOBAL.CONTRACT_CHANGE_STATUS[4][0]));
			} else {
				
				// 保存将合同变更设置为“未提交”状态
				mContractChange.setStatus(Integer
						.parseInt(GLOBAL.CONTRACT_CHANGE_STATUS[2][0]));
			}
			mChangeContractService.addContractChange(managerInterface, mContractChange);
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
		
		return flowApproval;
	}
	
	/**
	 * 创建清单项
	 */
	private void createDetailList() {
		
		// 回调接口实现
		DataManagerInterface managerInterface = new DataManagerInterface() {
			
			@Override
			public void getDataOnResult(ResultStatus status, List<?> list) {
				mDetailAddCount--;
				
				if (status.getCode() == AnalysisManager.EXCEPTION_DB_ADD) {
					mDetailAddFailedCount++;
				}
				
				if (mDetailAddCount == 0
						&& mAttachmentAddCount == 0) {
					prepareFinish();
				}
			}
		};
		
		// 循环添加清单项
		for (int i = 0; i < mDetailList.size(); i++) {
			mDetailList.get(i).setContract_change_code(mContractChange.getCode());
			mDetailList.get(i).setContract_change_id(mContractChange.getId());
			
			mChangeContractService.addChangeList(managerInterface, mDetailList.get(i));
		}
	}
	
	/**
	 * 循环添加附件列表
	 */
	private void createAttachmentList() {
			
		// 循环添加清单项
		for (int i = 0; i < mAttachmentList.size(); i++) {
			mAttachmentList.get(i).setDir_type(Integer.parseInt(GLOBAL.FILE_TYPE[32][0]));
			mAttachmentList.get(i).setTenant_id(UserCache.getTenantId());
			mAttachmentList.get(i).setProject_id(mContract.getProject_id());
			mAttachmentList.get(i).setType_id(mContract.getContract_id());
			
			RemoteDocumentsService.getInstance().uploadFile(new DataManagerInterface() {
				
				@Override
				public void getDataOnResult(ResultStatus status, List<?> list) {
					mAttachmentAddCount--;
					switch (status.getCode()) {
						case AnalysisManager.SUCCESS_DB_ADD:
							mAttachmentUploaded.add((Files) list.get(0));
							break;
						case AnalysisManager.EXCEPTION_DB_ADD:
							mAttachmentAddFailedCount++;
							break;
						default:
							break;
					}
					
					if (mDetailAddCount == 0
							&& mAttachmentAddCount == 0) {
						prepareFinish();
					}
				}
			}, null, mAttachmentList.get(i), UserCache.getTenantId(), 
					new File(mAttachmentList.get(i).getPath()), new ArrayList<User>(), 
					UserCache.getCurrentUserId());
		}
	}
	
	/**
	 * 完成前的准备
	 */
	private void prepareFinish() {
		
		// 更新合同
		updateContract();
	}
	
	/**
	 * 更新合同，数据返回后进入协作或直接返回数据并退出
	 */
	private void updateContract() {
		
		// 如果存在附件
		if (mAttachmentUploaded.size() > 0) {
			StringBuilder sb = new StringBuilder();
			
			for (int i = 0; i < mAttachmentUploaded.size(); i++) {
				sb.append(mAttachmentUploaded.get(i).getFile_id());
				sb.append(",");
			}
			
			mContractChange.setAttachments(sb.substring(0, sb.length() - 1));
			mChangeContractService.updateChangeContract(new DataManagerInterface() {
				
				@Override
				public void getDataOnResult(ResultStatus status, List<?> list) {
					switch (status.getCode()) {
						case AnalysisManager.SUCCESS_DB_UPDATE:
							if (mIsCooperation) {
								
								// 发起协作
								startCooperation();
							} else {
								
								// 返回数据
								dataBack(false);
							}
							break;
						case AnalysisManager.EXCEPTION_DB_UPDATE:
							mAttachmentView.sendMessage(BaseListCommon.SHOW_TOAST,
										status.getMessage());
							
							// 返回数据
							dataBack(false);
							break;
						default:
							break;
					}
				}
			}, mContractChange);
		} else if (mIsCooperation) {
			
			// 发起协作
			startCooperation();
		} else {
			
			// 返回数据
			dataBack(false);
		}
	}
	
	/**
	 * 切换视图，禁止上面的视图
	 */
	private void switchView() {
		switch (mCurrentIndex) {
			case INDEX_CONTRACT_CHANGE_BASIC_INFO:
				mPreviousButton.setVisibility(View.GONE);
				mTitle.setText(getResources().getString(R.string.contract_change_add_title));
				mContractChangeCommon.setContractChangeMoney(getChangeTotalMoney());
				break;
			case INDEX_CONTRACT_CHANGE_DETAIL:
				mPreviousButton.setVisibility(View.VISIBLE);
				mNextButton.setVisibility(View.VISIBLE);
				mTitle.setText(getResources().getString(R.string.contract_add_detail_item));
				break;
			case INDEX_CONTRACT_CHANGE_ATTACHMENT:
				mNextButton.setVisibility(View.GONE);
				mTitle.setText(getResources().getString(R.string.contract_add_attachment));
				break;
			default:
				break;
		}
		
		// 不显示其上面的View
		disableOverViews();
	}
	
	/**
	 * 获取合同变更总价
	 * @return
	 */
	private double getChangeTotalMoney() {
		List<Contract_list> contractList = mDetailView.getShowList();
		double totalChangeMoney = 0;
		for (int i = 0; i < contractList.size(); i++) {
			totalChangeMoney += contractList.get(i).getTotal();
		}
		return totalChangeMoney;
	}
	
	/**
	 * 不显示上面的视图
	 * @param index
	 */
	private void disableOverViews() {
		
		// 启动Activity的第一个界面不需要滑动
		if (mOldIndex == mCurrentIndex) {
			mContractAddViews.get(mCurrentIndex).setVisibility(View.VISIBLE);
			
			for (int i = 1; i < mContractAddViews.size(); i++) {
				mContractAddViews.get(i).setVisibility(View.GONE);
			}
		} else if (mCurrentIndex > mOldIndex) {	// 下一步
			CustomTranslateAnimation.showRight(mContractAddViews.get(mCurrentIndex));
			CustomTranslateAnimation.fadeLeft(mContractAddViews.get(mOldIndex));
		} else {	// 上一步
			CustomTranslateAnimation.showLeft(mContractAddViews.get(mCurrentIndex));
			CustomTranslateAnimation.fadeRight(mContractAddViews.get(mOldIndex));
		}
	}
	
	/**
	 * 初始化内容视图
	 */
	private void initContentView() {
		
		// 初始基本信息视图
		initCommon();
		
		// 合同变更清单列表
		initDetailList();
		
		// 合同附件
		initAttachment();
	}
	
	/**
	 * 初始化合同变更基本信息
	 */
	@SuppressLint("UseSparseArrays") 
	private void initCommon() {
		mContractChangeCommon = new ContractChangeApplyAttribute(this, mProject, true);
		
		// 加入父控件布局器中
		mContentLayout.addView(mContractChangeCommon.getRootView());
		
		// 设置顶距离
		LayoutParams params = (LayoutParams) 
				mContractChangeCommon.getRootView().getLayoutParams();
		params.topMargin = UtilTools.dp2pxH(this, 20);
		
		// 加入添加合同视图数组
		mContractAddViews.add(mContractChangeCommon.getRootView());
	}
	
	/**
	 * 合同变更清单
	 */
	private void initDetailList() {
		mDetailView = new ContractChangeDetail(this, mProject, detailServiceInterface, false);
		
		// 加入父控件布局器中
		mContentLayout.addView(mDetailView.getRootView());
		
		// 加入添加合同视图数组
		mContractAddViews.add(mDetailView.getRootView());
	}
	
	/**
	 * 合同清单项服务接口
	 */
	ServiceInterface<Contract_list> detailServiceInterface 
							= new ServiceInterface<Contract_list>() {

		@Override
		public void getListData() {
			mBaseListCommon.sendMessage(BaseListCommon.DISMISS_PROGRESS_DIALOG);
		}

		@Override
		public void addItem(Contract_list t) {
			t.setId(mDetailCountId++);
			t.setContract_code(mContractChange.getContract_code());
			t.setContract_change_code(mContractChange.getCode());
			t.setContract_change_id(mContractChange.getId());
			t.setTenant_id(UserCache.getCurrentUser().getTenant_id());
			t.setProject_id(mContractChange.getProject_id());
			mContractChange.setBqbgk(mContractChange.getBqbgk() + t.getTotal());
			
			if (mSelectItem instanceof P_WZ) {
				P_WZ material = (P_WZ) mSelectItem;
				
				t.setQd_id(material.getWz_id()); 
				t.setQd_name(material.getName());
				t.setQd_spec(material.getSpec());
				t.setQd_unit(material.getUnit());
			} else if (mSelectItem instanceof P_WBRGNR) {
				P_WBRGNR labour =(P_WBRGNR) mSelectItem;
				
				t.setQd_id(labour.getWbrgnr_id()); 
				t.setQd_name(labour.getWork());
			}
			mDetailList.add(t);
			
			// 构造返回数据
			ResultStatus status = new ResultStatus(AnalysisManager.SUCCESS_DB_ADD, getString(R.string.add_success));
			List<Contract_list> list = new ArrayList<Contract_list>();
			list.add(t);
			
			mDetailView.getServiceManager().getDataOnResult(status, list);
		}

		@Override
		public void deleteItem(Contract_list t) {
			for (int i = 0; i < mDetailList.size(); i++) {
				if (mDetailList.get(i).getId() == t.getId()) {
					mDetailList.remove(i);
					break;
				}
			}
			
			mContractChange.setBqbgk(mContractChange.getBqbgk() - t.getTotal());
			
			ResultStatus status = new ResultStatus(AnalysisManager.SUCCESS_DB_DEL, getString(R.string.delete_success));
			mDetailView.getServiceManager().getDataOnResult(status, null);
		}

		@Override
		public void updateItem(Contract_list t) {
			for (int i = 0; i < mDetailList.size(); i++) {
				if (mDetailList.get(i).getId() == t.getId()) {
					mContractChange.setBqbgk(mContractChange.getBqbgk() 
							- mDetailList.get(i).getTotal() + t.getTotal());
					mDetailList.set(i, t);
					break;
				}
			}
			
			ResultStatus status = new ResultStatus(AnalysisManager.SUCCESS_DB_UPDATE, getString(R.string.update_success));
			mDetailView.getServiceManager().getDataOnResult(status, null);
		}
	};
	
	/**
	 * 合同附件
	 */
	private void initAttachment() {
		mAttachmentView = new ContractChangeAttachment(this, true, true, false);
		
		// 加入父控件布局器中
		mContentLayout.addView(mAttachmentView.getRootView());
		
		// 加入添加合同视图数组
		mContractAddViews.add(mAttachmentView.getRootView());
	}
	
	@Override
	protected void onActivityResult(int requestCode, int resultCode, Intent data) {
		super.onActivityResult(requestCode, resultCode, data);
		
		if (resultCode == Activity.RESULT_OK) {
			switch (requestCode) {
				case ContractChangeApplyAttribute.USER_SELECT_REQUEST_CODE:
					User user = (User) data.getSerializableExtra("user");
					if (user != null) {
						mContractChangeCommon.setSenderUser(user);
					}
					break;
				case ContractChangeApplyAttribute.USER_SELECT_REQUEST_CODE + 1:
					User user1 = (User) data.getSerializableExtra("user");
					if (user1 != null) {
						mContractChangeCommon.setRecieverUser(user1);
					}
					break;
				case ContractChangeApplyAttribute.TENANT_SELECT_REQUEST_CODE:
					Tenant tenant1 = (Tenant) data.getSerializableExtra("tenant");
					if (tenant1 != null) {
						mContractChangeCommon.getBaseWindow()
									.setTenantContent(8, tenant1.getTenant_id());
					}
					break;
				case ContractChangeApplyAttribute.TENANT_SELECT_REQUEST_CODE + 1:
					Tenant tenant2 = (Tenant) data.getSerializableExtra("tenant");
					if (tenant2 != null) {
						mContractChangeCommon.getBaseWindow()
									.setTenantContent(9, tenant2.getTenant_id());
					}
					break;
				case ContractChangeAttachment.FILE_SELECT_REQEUST_CODE:
					@SuppressWarnings("unchecked")
					List<Files> files = (List<Files>) data.getSerializableExtra(
							DocumentUploadActivity.UPLOAD_FILE_LIST_KEY);
					if (files != null) {
						for (int i = 0; i < files.size(); i++) {
							files.get(i).setFile_id(mAttachmentCountId++);
							mAttachmentList.add(files.get(i));
						}
					}
					
					mAttachmentView.getListAdapter().setDataList(mAttachmentList);
					mAttachmentView.getListAdapter().setShowDataList(mAttachmentList);
					break;
				case ContractChangeApplyAttribute.CONTRACT_SELECT_REQUEST_CODE:
					Contract contract = (Contract) data
								.getSerializableExtra(ListSelectActivity.RESULT_KEY);
					mContract = contract;
					mContractChangeCommon.setContract(contract);
					break;
				case ContractChangeDetail.MATERIAL_SELECT_REQUEST_CODE:
					P_WZ material = (P_WZ) data
								.getSerializableExtra(ListSelectActivity.RESULT_KEY);
					mSelectItem = material;
					mDetailView.setAddModifySource(material);
					break;
				case ContractChangeDetail.EQUIPMENT_SELECT_REQUEST_CODE:
					P_WZ equiment = (P_WZ) data
							.getSerializableExtra(ListSelectActivity.RESULT_KEY);
					mSelectItem = equiment;
					mDetailView.setAddModifySource(equiment);
					break;
				case ContractChangeDetail.LABOUR_SELECT_REQUEST_CODE:
					P_WBRGNR labour = (P_WBRGNR) data
							.getSerializableExtra(ListSelectActivity.RESULT_KEY);
					mSelectItem = labour;
					mDetailView.setAddModifySource(labour);
					break;
				case COOPERATION_REQUEST_CODE:
					
					// 获取协作产生的邮件对象，用以保存到合同变更中
					MailBox mailBox = (MailBox) data
								.getSerializableExtra(ComposeActivity.MAILBOX_KEY);
					
					// 如果协作成功，更新合同变更状态
					if (mailBox != null) {
						mContractChange.setStatus(Integer
									.parseInt(GLOBAL.CONTRACT_CHANGE_STATUS[1][0]));
						mChangeContractService.updateChangeContract(null, mContractChange);
					}
					
					// 返回数据
					dataBack(false);
					break;
				default:
					break;
			}
		}
	}
}
