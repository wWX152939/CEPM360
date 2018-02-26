package com.pm360.cepm360.app.module.contract;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.FrameLayout;
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
import com.pm360.cepm360.app.module.contract.pager.ContractAttachment;
import com.pm360.cepm360.app.module.contract.pager.ContractCommon;
import com.pm360.cepm360.app.module.contract.pager.ContractDetail;
import com.pm360.cepm360.app.module.contract.pager.ContractLeaseCommon;
import com.pm360.cepm360.app.module.contract.pager.ContractPayment;
import com.pm360.cepm360.app.module.contract.pager.ContractProjectCommon;
import com.pm360.cepm360.app.module.contract.pager.ContractPurchaseCommon;
import com.pm360.cepm360.app.module.document.DocumentUploadActivity;
import com.pm360.cepm360.common.GLOBAL;
import com.pm360.cepm360.common.ResultStatus;
import com.pm360.cepm360.common.json.AnalysisManager;
import com.pm360.cepm360.entity.Contract;
import com.pm360.cepm360.entity.Contract_list;
import com.pm360.cepm360.entity.Contract_payment;
import com.pm360.cepm360.entity.Files;
import com.pm360.cepm360.entity.P_LWDW;
import com.pm360.cepm360.entity.P_WBRGNR;
import com.pm360.cepm360.entity.P_WZ;
import com.pm360.cepm360.entity.P_ZL;
import com.pm360.cepm360.entity.Project;
import com.pm360.cepm360.entity.Tenant;
import com.pm360.cepm360.entity.User;
import com.pm360.cepm360.services.document.RemoteDocumentsService;
import com.pm360.cepm360.services.expenses.RemoteExpensesContractService;
import com.pm360.cepm360.services.expenses.RemoteRevenueContractService;

import java.io.File;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

public class ContractAddActivity extends Activity {
	
	// 参数key
	public static final String CONTRACT_KEY = "contractKey";
	public static final String INCOME_CONTRACT_KEY = "incomeContractKey";
	
	// 增加合同视图索引
	public static final int INDEX_CONTRACT_BASIC_INFO = 0;
	public static final int INDEX_CONTRACT_DETAIL = 1;
	public static final int INDEX_CONTRACT_PAYMENT = 2;
	public static final int INDEX_CONTRACT_ATTACHMENT = 3;
	
	// 返回码定义
	public static final String RESULT_KEY = "result_key";
	public static final String DETAIL_FAILED_KEY = "detail_failed_key";
	public static final String PAYMENT_FAILED_KEY = "payment_failed_key";
	public static final String ATTACHMENT_FAILED_KEY = "attachment_failed_key";
	
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
	private Button mCompleteButton;
	
	private ImageView mCloseImageView;
	
	// 添加合同的视图封装对象
	private ContractCommon mContractCommon;
	private ContractDetail mDetailView;
	private ContractPayment mPaymentView;
	private ContractAttachment mAttachmentView;
	
	private BaseListCommon<Serializable> mBaseListCommon;
	
	// 添加合同各视图的保存数据
	private Contract mContract;
	private List<Contract_list> mDetailList = new ArrayList<Contract_list>();
	private List<Contract_payment> mPaymentList = new ArrayList<Contract_payment>();
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
	private int mPaymentCountId = 1;
	private int mAttachmentCountId = 1;
	
	// 合同清单项，合同支付项，合同附件添加情况
	private int mDetailAddCount;
	private int mPaymentAddCount;
	private int mAttachmentAddCount;
	
	private int mDetailAddFailedCount;
	private int mPaymentAddFailedCount;
	private int mAttachmentAddFailedCount;
	
	// 是否是收入合同
	private boolean mIsIncomeContract;
	
	// 服务
	RemoteRevenueContractService mRevenueContractService 
							= RemoteRevenueContractService.getInstance();
	RemoteExpensesContractService mExpensesContractService 
							= RemoteExpensesContractService.getInstance();
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		
		// 设置Activity布局
		setContentView(R.layout.add_contract_activity);
		
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
		mIsIncomeContract = intent.getBooleanExtra(INCOME_CONTRACT_KEY, true);
	}
	
	/**
	 * 初始化Activity标题
	 */
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
		mTitle.setText(getResources().getString(R.string.contract_add_title));
		
		// 初始化内容布局
		mContentLayout = (FrameLayout) findViewById(R.id.content_frame);
		
		// 初始化按键根布局
		mButtonRootLayout = (LinearLayout) findViewById(R.id.button_bar);
		mButtonRootLayout.setVisibility(View.VISIBLE);
		
		mPreviousButton = (Button) mButtonRootLayout.findViewById(R.id.btn_left);
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
		
		mNextButton = (Button) mButtonRootLayout.findViewById(R.id.btn_middle);
		mNextButton.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View v) {
				if (mCurrentIndex == INDEX_CONTRACT_ATTACHMENT) {
					finish();
				} else {
					
					// 先保存数据
					saveData();
					mOldIndex = mCurrentIndex;
					++mCurrentIndex;
					
					// 切换视图
					switchView();
				}
				
			}
		});
		
		mCompleteButton = (Button) mButtonRootLayout.findViewById(R.id.btn_right);
		mCompleteButton.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View v) {
				
				// 先保存数据
				saveData();
				
				if (checkValid()) {
					
					// 创建合同
					createContract();
				} else {
					mBaseListCommon.sendMessage(BaseListCommon.SHOW_TOAST,
							"请填写完整合同信息！");
				}
			}
		});
		
		mCloseImageView = (ImageView) findViewById(R.id.btn_close);
		mCloseImageView.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View v) {
				setResult(Activity.RESULT_OK);
				finish();
			}
		});
		
		mCurrentIndex = INDEX_CONTRACT_BASIC_INFO;
	}
	
	/**
	 * 检查合同信息完整性
	 * @return
	 */
	private boolean checkValid() {
		if (mContract.getName().isEmpty()) {
			return false;
		}
		
		if (mContract.getType() == Integer.parseInt(GLOBAL.CONTRACT_TYPE[0][0])) {
			if (mContract.getFirst_party() == 0 
					|| mContract.getSecond_party() == 0) {
				return false;
			}
		} else if (mContract.getSecond_party() == 0) {
			return false;
		}
		
		return true;
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
			Bundle resultBundle = new Bundle();
			resultBundle.putSerializable(RESULT_KEY, mContract);
			resultBundle.putInt(DETAIL_FAILED_KEY, mDetailAddFailedCount);
			resultBundle.putInt(PAYMENT_FAILED_KEY, mPaymentAddFailedCount);
			resultBundle.putInt(ATTACHMENT_FAILED_KEY, mAttachmentAddFailedCount);
			intent.putExtras(resultBundle);
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
			case INDEX_CONTRACT_BASIC_INFO:
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
		mContract = mContractCommon.getContract(mProject);
	}
	
	/**
	 * 创建合同
	 */
	private void createContract() {
			
		// 显示进度
		mBaseListCommon.sendMessage(BaseListCommon.SHOW_PROGRESS_DIALOG);
		
		// 初始化添加数量
		mDetailAddCount = mDetailList.size();
		mPaymentAddCount = mPaymentList.size();
		mAttachmentAddCount = mAttachmentList.size();
		
		// 实现添加合同回调接口
		DataManagerInterface managerInterface = new DataManagerInterface() {
			
			@Override
			public void getDataOnResult(ResultStatus status, List<?> list) {
				switch (status.getCode()) {
					case AnalysisManager.SUCCESS_DB_ADD:
						Contract contract = (Contract) list.get(0);
						mContract.setContract_id(contract.getContract_id());
						mContract.setCode(contract.getCode());
						mAttachmentView.setContract(mContract);
						
						// 检查是否完成
						if (mDetailAddCount == 0
								&& mPaymentAddCount == 0
								&& mAttachmentAddCount == 0) {
							prepareFinish();
						} else {
						
							// 创建清单项
							createDetailList();
							
							// 创建支付项列表
							createPaymentList();
							
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
		
		// 先创建合同
		if (mIsIncomeContract) {
			mRevenueContractService.addRevenueContract(managerInterface, mContract);
		} else {
			mExpensesContractService.addExpensesContract(managerInterface, mContract);
		}
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
						&& mPaymentAddCount == 0
						&& mAttachmentAddCount == 0) {
					prepareFinish();
				}
			}
		};
		
		// 循环添加清单项
		for (int i = 0; i < mDetailList.size(); i++) {
			mDetailList.get(i).setContract_id(mContract.getContract_id());
			mDetailList.get(i).setContract_code(mContract.getCode());
			
			if (mIsIncomeContract) {
				mRevenueContractService.addRevenueList(managerInterface, mDetailList.get(i));
			} else {
				mExpensesContractService.addExpensesList(managerInterface, 
						mDetailList.get(i), mContract.getType() + "");
			}
		}
	}
	
	/**
	 * 循环添加支付项列表
	 */
	private void createPaymentList() {
		
		// 服务回调接口实现
		DataManagerInterface managerInterface = new DataManagerInterface() {
			
			@Override
			public void getDataOnResult(ResultStatus status, List<?> list) {
				mPaymentAddCount--;
				
				switch (status.getCode()) {
					case AnalysisManager.SUCCESS_DB_ADD:
						mContract.setPaid(mContract.getPaid() 
								+ ((Contract_payment) list.get(0)).getActual_pay());
						break;
					case AnalysisManager.EXCEPTION_DB_ADD:
						mPaymentAddFailedCount++;
						break;
					default:
						break;
				}
				
				if (mDetailAddCount == 0 
						&& mPaymentAddCount == 0
						&& mAttachmentAddCount == 0) {
					prepareFinish();
				}
			}
		};
		
		// 循环添加清单项
		for (int i = 0; i < mPaymentList.size(); i++) {
			mPaymentList.get(i).setContract_id(mContract.getContract_id());
			
			if (mIsIncomeContract) {
				mRevenueContractService.addCashback(managerInterface, mPaymentList.get(i));
			} else {
				mExpensesContractService.addPay(managerInterface, mPaymentList.get(i));
			}
		}
	}
	
	/**
	 * 循环添加支付项列表
	 */
	private void createAttachmentList() {
		User user = UserCache.getCurrentUser();
			
		// 循环添加清单项
		for (int i = 0; i < mAttachmentList.size(); i++) {
			if (mIsIncomeContract) {
				mAttachmentList.get(i).setDir_type(Integer.parseInt(GLOBAL.FILE_TYPE[3][0]));
			} else {
				mAttachmentList.get(i).setDir_type(Integer.parseInt(GLOBAL.FILE_TYPE[4][0]));
			}
			mAttachmentList.get(i).setTenant_id(UserCache.getTenantId());
			mAttachmentList.get(i).setProject_id(mContract.getProject_id());
			mAttachmentList.get(i).setType_id(mContract.getContract_id());
			
			RemoteDocumentsService.getInstance()
								.uploadFile(new DataManagerInterface() {
				
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
							&& mPaymentAddCount == 0
							&& mAttachmentAddCount == 0) {
						prepareFinish();
					}
				}
			}, null, mAttachmentList.get(i), 
				user.getTenant_id(), new File(mAttachmentList.get(i).getPath()), 
				new ArrayList<User>(), user.getUser_id());
		}
	}
	
	/**
	 * 完成前的准备
	 */
	private void prepareFinish() {
		
		// 更新合同
		updateContract();
		
		// 返回数据
		dataBack(false);
	}
	
	/**
	 * 更新合同
	 */
	private void updateContract() {
		
		// 如果存在附件
		if (mAttachmentUploaded.size() > 0) {
			StringBuilder sb = new StringBuilder();
			
			for (int i = 0; i < mAttachmentUploaded.size(); i++) {
				sb.append(mAttachmentUploaded.get(i).getFile_id());
				sb.append(",");
			}
			
			mContract.setAttachments(sb.substring(0, sb.length() - 1));
		}
		
		if (mIsIncomeContract) {
			mRevenueContractService.updateRevenueContract(new DataManagerInterface() {
				
				@Override
				public void getDataOnResult(ResultStatus status, List<?> list) {
					
				}
			}, mContract);
		} else {
			mExpensesContractService.updateExpensesContract(new DataManagerInterface() {
				
				@Override
				public void getDataOnResult(ResultStatus status, List<?> list) {
					
				}
			}, mContract);
		}
	}
	
	/**
	 * 切换视图，禁止上面的视图
	 */
	private void switchView() {
		switch (mCurrentIndex) {
			case INDEX_CONTRACT_BASIC_INFO:
				mPreviousButton.setVisibility(View.GONE);
				mTitle.setText(getResources().getString(R.string.contract_add_title));
				break;
			case INDEX_CONTRACT_DETAIL:
				mPreviousButton.setVisibility(View.VISIBLE);
				mTitle.setText(getResources().getString(R.string.contract_add_detail_item));
				break;
			case INDEX_CONTRACT_PAYMENT:
				mNextButton.setText(getResources().getString(R.string.next));
				mNextButton.setVisibility(View.VISIBLE);
				
				if (mIsIncomeContract) {
					mTitle.setText(getResources()
							.getString(R.string.contract_add_return_money_item));
				} else {
					mTitle.setText(getResources()
							.getString(R.string.contract_add_payment_item));
				}
				break;
			case INDEX_CONTRACT_ATTACHMENT:
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
		
		if (mCurrentIndex == INDEX_CONTRACT_DETAIL) {
			mDetailView.setContractType(mContractCommon.getContractType());
		}
	}
	
	/**
	 * 初始化内容视图
	 */
	private void initContentView() {
		
		// 初始基本信息视图
		initCommon();
		
		// 合同清单
		initDetailList();
		
		// 合同支付项
		initPayment();
		
		// 合同附件
		initAttachment();
	}
	
	/**
	 * 初始化合同基本信息
	 */
	@SuppressLint("UseSparseArrays") 
	private void initCommon() {
		mContractCommon = new ContractCommon(this, mIsIncomeContract, mProject);
		
		// 加入父控件布局器中
		mContentLayout.addView(mContractCommon.getRootView());
		
		// 加入添加合同视图数组
		mContractAddViews.add(mContractCommon.getRootView());
	}
	
	/**
	 * 合同清单
	 */
	private void initDetailList() {
		mDetailView = new ContractDetail(this, mIsIncomeContract, mProject,
							detailServiceInterface, false);
		
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
			
		}

		@Override
		public void addItem(Contract_list t) {
			t.setId(mDetailCountId++);
			t.setTenant_id(UserCache.getCurrentUser().getTenant_id());
			t.setProject_id(mContract.getProject_id());
			
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
			} else if (mSelectItem instanceof P_ZL) {
				P_ZL zl = (P_ZL) mSelectItem;
				
				t.setQd_id(zl.getZl_id()); 
				t.setQd_name(zl.getName());
				t.setQd_spec(zl.getSpec());
				t.setQd_unit(zl.getUnit());
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
			
			ResultStatus status = new ResultStatus(AnalysisManager.SUCCESS_DB_DEL, getString(R.string.delete_success));
			mDetailView.getServiceManager().getDataOnResult(status, null);
		}

		@Override
		public void updateItem(Contract_list t) {
			for (int i = 0; i < mDetailList.size(); i++) {
				if (mDetailList.get(i).getId() == t.getId()) {
					mDetailList.set(i, t);
					break;
				}
			}
			
			ResultStatus status = new ResultStatus(AnalysisManager.SUCCESS_DB_UPDATE, getString(R.string.update_success));
			mDetailView.getServiceManager().getDataOnResult(status, null);
		}
	};
	
	
	/**
	 * 合同支付项
	 */
	private void initPayment() {
		mPaymentView = new ContractPayment(this, mIsIncomeContract,
					mProject, paymentServiceInterface, false);
		
		// 加入父控件布局器中
		mContentLayout.addView(mPaymentView.getRootView());
		
		// 加入添加合同视图数组
		mContractAddViews.add(mPaymentView.getRootView());
	}
	
	/**
	 * 支付项服务接口实现
	 */
	ServiceInterface<Contract_payment> paymentServiceInterface 
							= new ServiceInterface<Contract_payment>() {

		@Override
		public void getListData() {
			
		}

		@Override
		public void addItem(Contract_payment t) {
			t.setId(mPaymentCountId++);
			mPaymentList.add(t);
			
			// 构造返回数据
			ResultStatus status = new ResultStatus(AnalysisManager.SUCCESS_DB_ADD, getString(R.string.add_success));
			List<Contract_payment> list = new ArrayList<Contract_payment>();
			list.add(t);
			
			mPaymentView.getServiceManager().getDataOnResult(status, list);
		}

		@Override
		public void deleteItem(Contract_payment t) {
			for (int i = 0; i < mPaymentList.size(); i++) {
				if (mPaymentList.get(i).getId() == t.getId()) {
					mPaymentList.remove(i);
					break;
				}
			}
			mContract.setPaid(mContract.getPaid() - t.getActual_pay());
			
			ResultStatus status = new ResultStatus(AnalysisManager.SUCCESS_DB_DEL, getString(R.string.delete_success));
			mPaymentView.getServiceManager().getDataOnResult(status, null);
		}

		@Override
		public void updateItem(Contract_payment t) {
			for (int i = 0; i < mPaymentList.size(); i++) {
				if (mPaymentList.get(i).getId() == t.getId()) {
					mContract.setPaid(mContract.getPaid() 
							- mPaymentList.get(i).getActual_pay() + t.getActual_pay());
					mPaymentList.set(i, t);
					break;
				}
			}
			
			ResultStatus status = new ResultStatus(AnalysisManager.SUCCESS_DB_UPDATE, getString(R.string.update_success));
			mPaymentView.getServiceManager().getDataOnResult(status, null);
		}
	};
	
	/**
	 * 合同附件
	 */
	private void initAttachment() {
		mAttachmentView = new ContractAttachment(this, mIsIncomeContract, true, true, false);
		
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
				case ContractProjectCommon.USER_SELECT_REQUEST_CODE:
					User user = (User) data.getSerializableExtra("user");
					if (user != null) {
						mContractCommon.getBaseWindow().
									setUserTextContent(8, user.getUser_id());
					}
					break;
				case ContractProjectCommon.TENANT_SELECT_REQUEST_CODE:
					Tenant tenant1 = (Tenant) data.getSerializableExtra("tenant");
					if (tenant1 != null) { 
						mContractCommon.getBaseWindow()
									.setTenantContent(12, tenant1.getTenant_id());
					}
					break;
				case ContractProjectCommon.TENANT_SELECT_REQUEST_CODE + 1:
					Tenant tenant2 = (Tenant) data.getSerializableExtra("tenant");
					if (tenant2 != null) {
						mContractCommon.getBaseWindow()
									.setTenantContent(13, tenant2.getTenant_id());
					}
					break;
				case ContractPurchaseCommon.USER_SELECT_REQUEST_CODE:
					User user1 = (User) data.getSerializableExtra("user");
					if (user1 != null) {
						mContractCommon.getBaseWindow().
									setUserTextContent(8, user1.getUser_id());
					}
					break;
				case ContractPurchaseCommon.CONTACT_COMPANY_SELECT_REQUEST_CODE:
					P_LWDW contactCompany = (P_LWDW) data
									.getSerializableExtra(ListSelectActivity.RESULT_KEY);
					if (contactCompany != null) {
						mContractCommon.setPurchaseContactCompany(contactCompany);
					}
					break;
				case ContractLeaseCommon.CONTACT_COMPANY_SELECT_REQUEST_CODE:
					P_LWDW contactCompany1 = (P_LWDW) data
									.getSerializableExtra(ListSelectActivity.RESULT_KEY);
					if (contactCompany1 != null) {
						mContractCommon.setLeaseContactCompany(contactCompany1);
					}
					break;
				case ContractAttachment.FILE_SELECT_REQEUST_CODE:
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
				case ContractDetail.MATERIAL_SELECT_REQUEST_CODE:
					P_WZ material = (P_WZ) data
								.getSerializableExtra(ListSelectActivity.RESULT_KEY);
					mSelectItem = material;
					mDetailView.setAddModifySource(material);
					break;
				case ContractDetail.LEASE_SELECT_REQUEST_CODE:
					P_ZL zl = (P_ZL) data
								.getSerializableExtra(ListSelectActivity.RESULT_KEY);
					mSelectItem = zl;
					mDetailView.setAddModifySource(zl);
					break;
				case ContractDetail.EQUIPMENT_SELECT_REQUEST_CODE:
					P_WZ equiment = (P_WZ) data
							.getSerializableExtra(ListSelectActivity.RESULT_KEY);
					mSelectItem = equiment;
					mDetailView.setAddModifySource(equiment);
					break;
				case ContractDetail.LABOUR_SELECT_REQUEST_CODE:
					P_WBRGNR labour = (P_WBRGNR) data
							.getSerializableExtra(ListSelectActivity.RESULT_KEY);
					mSelectItem = labour;
					mDetailView.setAddModifySource(labour);
					break;
				default:
					mDetailView.OnActivityResult(requestCode, resultCode, data);
					mPaymentView.OnActivityResult(requestCode, resultCode, data);
					break;
			}
		}
	}
}
