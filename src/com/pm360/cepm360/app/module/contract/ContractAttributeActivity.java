package com.pm360.cepm360.app.module.contract;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.KeyEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.pm360.cepm360.R;
import com.pm360.cepm360.app.common.activity.ActionBarFragmentActivity;
import com.pm360.cepm360.app.common.activity.DataManagerInterface;
import com.pm360.cepm360.app.common.activity.ListSelectActivity;
import com.pm360.cepm360.app.common.custinterface.CallBack;
import com.pm360.cepm360.app.common.custinterface.ViewPagersInterface;
import com.pm360.cepm360.app.common.view.parent.BaseViewPager;
import com.pm360.cepm360.app.common.view.parent.list.BaseListCommon;
import com.pm360.cepm360.app.module.common.attachment.AttachmentActivity;
import com.pm360.cepm360.app.module.contract.pager.ContractAttachment;
import com.pm360.cepm360.app.module.contract.pager.ContractCommon;
import com.pm360.cepm360.app.module.contract.pager.ContractDetail;
import com.pm360.cepm360.app.module.contract.pager.ContractLeaseCommon;
import com.pm360.cepm360.app.module.contract.pager.ContractPayment;
import com.pm360.cepm360.app.module.contract.pager.ContractProjectCommon;
import com.pm360.cepm360.app.module.contract.pager.ContractPurchaseCommon;
import com.pm360.cepm360.app.module.contract.pager.ContractType;
import com.pm360.cepm360.app.module.document.DocumentUploadActivity;
import com.pm360.cepm360.common.GLOBAL;
import com.pm360.cepm360.common.ResultStatus;
import com.pm360.cepm360.common.json.AnalysisManager;
import com.pm360.cepm360.entity.Contract;
import com.pm360.cepm360.entity.Files;
import com.pm360.cepm360.entity.P_LWDW;
import com.pm360.cepm360.entity.P_WBRGNR;
import com.pm360.cepm360.entity.P_WZ;
import com.pm360.cepm360.entity.P_ZL;
import com.pm360.cepm360.entity.Tenant;
import com.pm360.cepm360.entity.User;
import com.pm360.cepm360.services.expenses.RemoteExpensesContractService;
import com.pm360.cepm360.services.expenses.RemoteRevenueContractService;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

public class ContractAttributeActivity extends ActionBarFragmentActivity 
					implements CallBack<Void, Integer> {

	// 请求码定义
	public static final int USER_SELECT_REQUEST_CODE = 1000;
	public static final int TENANT_SELECT_REQUEST_CODE = 1010;
	
	public static final String CONTRACT_KEY = "contractKey";
	public static final String CONTRACT_ID_KEY = "contractIdKey";
	public static final String INCOME_CONTRACT_KEY = "incomeContractKey";
	public static final String IS_MODIFY_KEY = "isModifyKey";
	public static final String TAB_INDEX_KEY = "tabIndexKey";
	
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
	private ContractCommon mCommon;
	private ContractDetail mDetail;
	private ContractPayment mPayment;
	private ContractAttachment mAttachment;
	
	// ViewPager管理器
	private BaseViewPager mBaseViewPager;
	private LinearLayout mButtonBar;
	private Button mSaveButton;
	
	// Activity标题
	private TextView mTitle;
	
	// 收入或者支出合同
	private boolean mIsIncomeContract;
	private boolean mIsModify;	// 是否可以修改
	private boolean mCommonHasModify;	// 是否已经修改
	
	// 当前合同
	private Contract mContract;
	private int mTabIndex;
	
	private BaseListCommon<Serializable> mBaseListCommon;

	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		
		initBaseListCommon();
		
		// 初始化基本布局
		initBasicLayout();
				
		// 获取传入的参数
		getArgument();
		
	}
	
	/**
	 * 初始化BaseListCommon
	 */
	private void initBaseListCommon() {
		mBaseListCommon = new BaseListCommon<Serializable>(this) {
			
			@Override
			public Object getListAdapter() {
				return null;
			}
		};
		mBaseListCommon.init();
	}
	
	/**
	 * 初始化流程
	 */
	private void initMore() {
		
		// 创建页视图列表
		buildPagerViews();
		
		// 初始化ViewPager管理器
		initBaseViewPager();
	}
	
	/**
	 * 获取传入的参数
	 */
	private void getArgument() {
		Intent intent = getIntent();
		mContract = (Contract) intent.getSerializableExtra(CONTRACT_KEY);
		mIsIncomeContract = intent.getBooleanExtra(INCOME_CONTRACT_KEY, true);
		mIsModify = intent.getBooleanExtra(IS_MODIFY_KEY, false);
		mTabIndex = intent.getIntExtra(TAB_INDEX_KEY, 0);
		int contractId = intent.getIntExtra(CONTRACT_ID_KEY, 0);
		if (contractId != 0) {
			mBaseListCommon.sendEmptyMessageDelayed(BaseListCommon.SHOW_PROGRESS_DIALOG);
			RemoteExpensesContractService.getInstance()
						.getExpensesContractDetail(new DataManagerInterface() {
				
				@Override
				public void getDataOnResult(ResultStatus status, List<?> list) {
					mBaseListCommon.sendMessage(BaseListCommon.DISMISS_PROGRESS_DIALOG);
					if (status.getCode() == AnalysisManager.SUCCESS_DB_QUERY) {
						mContract = (Contract) list.get(0);
						
						initMore();
					}
				}
			}, contractId);
		} else {
			initMore();
		}
	}
	
	/**
	 * 初始化基本布局
	 */
	private void initBasicLayout() {

		// 设置Activity的显示视图
		setContentView(R.layout.contract_attribute_layout);
		
		// 标题初始化
		mTitle = (TextView) findViewById(R.id.title);
		mTitle.setText(getResources().getString(R.string.contract_attribute_title));
		
		// 按钮栏初始化
		mButtonBar = (LinearLayout) findViewById(R.id.button_bar);
		mButtonBar.findViewById(R.id.btn_left).setVisibility(View.GONE);
		mButtonBar.findViewById(R.id.btn_right).setVisibility(View.GONE);
		mSaveButton = (Button) mButtonBar.findViewById(R.id.btn_middle);
		mSaveButton.setText(R.string.save);
		
		if (!mIsModify) {
			mButtonBar.setVisibility(View.GONE);
		}
		
		// 当返回时，如果合同更新，返回更新合同对象
		getBackView().setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View v) {
				doFinish();
			}
		});
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
		
		// 构建支付项视图
		buildPaymentView();
		
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
		if (mIsIncomeContract) {
			mBaseViewPager.init(R.array.income_contract_view_pager_titles, null, mPagerViews);
		} else {
			mBaseViewPager.init(R.array.outcome_contract_view_pager_titles, null, mPagerViews);
		}
		mBaseViewPager.setCurrentParentBean(mContract);
		
		// 调到指定页面
		mBaseViewPager.onButtonClick(mTabIndex);
	}
	
	/**
	 * 构建常用视图
	 */
	private void buildCommon() {
		ContractType contractType = ContractType.PROJECT_CONTRACT;
		String type = String.valueOf(mContract.getType());
		if (type.equals(GLOBAL.CONTRACT_TYPE[1][0])) {
			contractType = ContractType.PURCHASE_CONTRACT;
		} else if (type.equals(GLOBAL.CONTRACT_TYPE[2][0])) {
			contractType = ContractType.LEASE_CONTRACT;
		}
		
		mCommon = new ContractCommon(this, mIsIncomeContract, 
						contractType, mIsModify);
		mCommon.getBaseWindow().switchModifyWindow(mIsModify);
		mPagerViews.add(mCommon);
		
		final DataManagerInterface dataManagerInterface = new DataManagerInterface() {
			
			@Override
			public void getDataOnResult(ResultStatus status, List<?> list) {
				mDetail.sendMessage(BaseListCommon.DISMISS_PROGRESS_DIALOG);
				
				switch (status.getCode()) {
					case AnalysisManager.SUCCESS_DB_UPDATE:
						mCommonHasModify = true;
						mCommon.handleParentEvent(mContract);
						break;
					default:
						break;
				}
				
				mDetail.sendMessage(BaseListCommon.SHOW_TOAST, status.getMessage());
			}
		};
		
		// 注册保存按钮监听
		mSaveButton.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View v) {
				
				// 发送延时显示进度对话框消息
				mDetail.sendEmptyMessageDelayed(BaseListCommon.SHOW_PROGRESS_DIALOG);
				
				if (mIsIncomeContract) {
					RemoteRevenueContractService.getInstance().updateRevenueContract(
							dataManagerInterface, mCommon.updateContract());
				} else {
					RemoteExpensesContractService.getInstance().updateExpensesContract(
							dataManagerInterface, mCommon.updateContract());
				}
			}
		});
	}
	
	/**
	 * 构建清单视图
	 */
	private void buildDetailListView() {
		mDetail = new ContractDetail(this, mIsIncomeContract, mIsModify, false);
		mPagerViews.add(mDetail);
	}
	
	/**
	 * 构建支付项视图
	 */
	private void buildPaymentView() {
		mPayment = new ContractPayment(this, mIsIncomeContract, mIsModify, false);
		mPagerViews.add(mPayment);
	}
	
	/**
	 * 构建附件视图
	 */
	private void buildAttachmentView() {
		mAttachment = new ContractAttachment(this, mIsIncomeContract, 
				mIsModify, false, false);
		mPagerViews.add(mAttachment);
	}
	
	@SuppressWarnings("unchecked")
	@Override
	protected void onActivityResult(int requestCode, int resultCode, Intent data) {
		super.onActivityResult(requestCode, resultCode, data);
		
		if (resultCode == Activity.RESULT_OK) {
			switch (requestCode) {
				case ContractDetail.MATERIAL_SELECT_REQUEST_CODE:
					P_WZ material = (P_WZ) data
								.getSerializableExtra(ListSelectActivity.RESULT_KEY);
					if (material != null) {
						mDetail.setAddModifySource(material);
					}
					break;
				case ContractDetail.LEASE_SELECT_REQUEST_CODE:
					P_ZL zl = (P_ZL) data
								.getSerializableExtra(ListSelectActivity.RESULT_KEY);
					if (zl != null) {
						mDetail.setAddModifySource(zl);
					}
					break;
				case ContractDetail.EQUIPMENT_SELECT_REQUEST_CODE:
					P_WZ equiment = (P_WZ) data
							.getSerializableExtra(ListSelectActivity.RESULT_KEY);
					if (equiment != null) {
						mDetail.setAddModifySource(equiment);
					}
					break;
				case ContractDetail.LABOUR_SELECT_REQUEST_CODE:
					P_WBRGNR labour = (P_WBRGNR) data
							.getSerializableExtra(ListSelectActivity.RESULT_KEY);
					if (labour != null) {
						mDetail.setAddModifySource(labour);
					}
					break;
				case ContractAttachment.FILE_SELECT_REQEUST_CODE:
					List<Files> files = (List<Files>) data.getSerializableExtra(
							DocumentUploadActivity.UPLOAD_FILE_LIST_KEY);
					mAttachment.addFileListToAdapter(files);
					break;
				case ContractProjectCommon.USER_SELECT_REQUEST_CODE:
					User user = (User) data.getSerializableExtra("user");
					if (user != null) {
						mCommon.getBaseWindow().setUserTextContent(8, user.getUser_id());
					}
					break;
				case ContractProjectCommon.TENANT_SELECT_REQUEST_CODE:
					Tenant tenant = (Tenant) data.getSerializableExtra("tenant");
					if (tenant != null) {
						mCommon.getBaseWindow().setTenantContent(12, tenant.getTenant_id());
					}
					break;
				case ContractProjectCommon.TENANT_SELECT_REQUEST_CODE + 1:
					Tenant tenant1 = (Tenant) data.getSerializableExtra("tenant");
					if (tenant1 != null) {
						mCommon.getBaseWindow().setTenantContent(13, tenant1.getTenant_id());
					}
					break;
				case ContractPurchaseCommon.USER_SELECT_REQUEST_CODE:
					User user1 = (User) data.getSerializableExtra("user");
					if (user1 != null) {
						mCommon.getBaseWindow().
									setUserTextContent(8, user1.getUser_id());
					}
					break;
				case ContractPurchaseCommon.CONTACT_COMPANY_SELECT_REQUEST_CODE:
					P_LWDW contactCompany = (P_LWDW) data
									.getSerializableExtra(ListSelectActivity.RESULT_KEY);
					if (contactCompany != null) {
						mCommon.setPurchaseContactCompany(contactCompany);
					}
					break;
				case ContractLeaseCommon.CONTACT_COMPANY_SELECT_REQUEST_CODE:
					P_LWDW contactCompany1 = (P_LWDW) data
									.getSerializableExtra(ListSelectActivity.RESULT_KEY);
					if (contactCompany1 != null) {
						mCommon.setLeaseContactCompany(contactCompany1);
					}
					break;
				case ContractDetail.FILE_SELECT_REQUEST_CODE:
					List<Files> fileList = (List<Files>) data
							.getSerializableExtra(AttachmentActivity.RESULT_ATTACH_LIST);
					if (fileList != null) {
						mDetail.setFileList(fileList);
					}
					break;
				case ContractPayment.FILE_SELECT_REQUEST_CODE:
					List<Files> fileList2 = (List<Files>) data
							.getSerializableExtra(AttachmentActivity.RESULT_ATTACH_LIST);
					if (fileList2 != null) {
						mPayment.setFileList(fileList2);
					}
					break;	
				default:
					mDetail.OnActivityResult(requestCode, resultCode, data);
					mPayment.OnActivityResult(requestCode, resultCode, data);
					break;
			}
		}
	}

	@Override
	public Void callBack(Integer a) {
		switch (a) {
			case ContractCommon.CONTRACT_COMMON_INDEX:
				setButtonBarVisibility(mIsModify);
				break;
			case ContractDetail.CONTRACT_DETIAL_INDEX:
				// fall through
			case ContractPayment.CONTRACT_PAYMENT_INDEX:
				// fall through
			case ContractAttachment.CONTRACT_ATTACHMENT_INDEX:
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
				|| mPayment.hasModify()) {
			Intent intent = new Intent();
			intent.putExtra(CONTRACT_KEY, mContract);
			setResult(Activity.RESULT_OK, intent);
		}
		
		finish();
	}
}
