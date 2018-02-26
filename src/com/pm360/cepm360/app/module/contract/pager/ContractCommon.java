package com.pm360.cepm360.app.module.contract.pager;

import android.content.Context;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.LinearLayout.LayoutParams;

import com.pm360.cepm360.R;
import com.pm360.cepm360.app.common.custinterface.CallBack;
import com.pm360.cepm360.app.common.custinterface.ViewPagersInterface;
import com.pm360.cepm360.app.common.view.CustomTranslateAnimation;
import com.pm360.cepm360.app.common.view.parent.BaseWindow;
import com.pm360.cepm360.common.GLOBAL;
import com.pm360.cepm360.entity.Contract;
import com.pm360.cepm360.entity.P_LWDW;
import com.pm360.cepm360.entity.Project;

public class ContractCommon implements CallBack<Void, ContractType>, ViewPagersInterface<Contract> {
	
	// 根视图， 用以管理两个子视图
	private ViewGroup mRootView;
	private Context mContext;
	
	// 当前合同类型
	private ContractType mContractType;
	private boolean mIsIncomeContract; // 是否是收入合同
	
	// 添加模式
	private boolean mIsAddMode;
	private boolean mIsModify;
	
	// 该类索引，标识该类的回调调用
	public static final int CONTRACT_COMMON_INDEX = 1100;
	
	// 管理对象
	private ContractProjectCommon mProjectCommon;
	private ContractPurchaseCommon mPurchaseCommon;
	private ContractLeaseCommon mLeaseCommon;
	
	private Project mProject;
	
	/**
	 * 构造函数，添加合同时使用
	 * @param context
	 * @param isIncomeContract
	 */
	public ContractCommon(Context context, boolean isIncomeContract, Project project) {
		mContext = context;
		mIsIncomeContract = isIncomeContract;
		mProject = project;
		mContractType = ContractType.PROJECT_CONTRACT;
		mIsAddMode = true;
		mIsModify = true;
		
		// 启动初始化流程
		init();
	}
	
	/**
	 * 构造函数，属性或修改时使用
	 * @param context
	 * @param isIncomeContract
	 * @param isPurchaseContract
	 */
	public ContractCommon(Context context, 
						boolean isIncomeContract, 
						ContractType contractType,
						boolean isModify) {
		mContext = context;
		mIsIncomeContract = isIncomeContract;
		mContractType = contractType;
		mIsAddMode = false;
		mIsModify = isModify;
		
		// 启动初始化流程
		init();
	}
	
	/**
	 * 获取当前合同类型
	 * @return
	 */
	public ContractType getContractType() {
		return mContractType;
	}
	
	/**
	 * 获取子视图
	 * @return
	 */
	public View getView() {
		if (mContractType == ContractType.PROJECT_CONTRACT) {
			return mProjectCommon.getRootView();
		} else if (mContractType == ContractType.PURCHASE_CONTRACT) {
			return mPurchaseCommon.getRootView();
		} else {
			return mLeaseCommon.getRootView();
		}
	}
	
	/**
	 * 获取根视图
	 * @return
	 */
	public View getRootView() {
		return mRootView;
	}
	
	/**
	 * 获取BaseWindow对象
	 * @return
	 */
	public BaseWindow getBaseWindow() {
		if (mContractType == ContractType.PURCHASE_CONTRACT) {
			return mPurchaseCommon.getBaseWindow();
		} else if (mContractType == ContractType.PROJECT_CONTRACT) {
			return mProjectCommon.getBaseWindow();
		} else {
			return mLeaseCommon.getBaseWindow();
		}
	}
	
	/**
	 * 获取合同对象
	 * @param project
	 * @return
	 */
	public Contract getContract(Project project) {
		if (mContractType == ContractType.PURCHASE_CONTRACT) {
			return mPurchaseCommon.getContract(project);
		} else if (mContractType == ContractType.PROJECT_CONTRACT) {
			return mProjectCommon.getContract(project);
		} else {
			return mLeaseCommon.getContract(project);
		}
	}
	
	/**
	 * 初始流程
	 */
	private void init() {
		
		// 初始化布局
		initLayout();
		
		// 初始、添加化工程合同
		mProjectCommon = new ContractProjectCommon(mContext, 
								this, mIsIncomeContract, mProject, mIsAddMode, mIsModify);
		mRootView.addView(mProjectCommon.getRootView());
		
		// 初始、添加化采购、租赁合同
		if (!mIsIncomeContract) {
			mPurchaseCommon = new ContractPurchaseCommon(mContext, this, mIsAddMode);
			mRootView.addView(mPurchaseCommon.getRootView());
			
			mLeaseCommon = new ContractLeaseCommon(mContext, this, mIsAddMode);
			mRootView.addView(mLeaseCommon.getRootView());
			
			// 初始化选择视图
			if (mContractType == ContractType.PURCHASE_CONTRACT) {
				mProjectCommon.getRootView().setVisibility(View.GONE);
				mLeaseCommon.getRootView().setVisibility(View.GONE);
				mPurchaseCommon.getRootView().setVisibility(View.VISIBLE);
			} else if (mContractType == ContractType.PROJECT_CONTRACT) {
				mProjectCommon.getRootView().setVisibility(View.VISIBLE);
				mPurchaseCommon.getRootView().setVisibility(View.GONE);
				mLeaseCommon.getRootView().setVisibility(View.GONE);
			} else {
				mProjectCommon.getRootView().setVisibility(View.GONE);
				mPurchaseCommon.getRootView().setVisibility(View.GONE);
				mLeaseCommon.getRootView().setVisibility(View.VISIBLE);
			}
		}
	}
	
	/**
	 * 初始化布局
	 */
	public void initLayout() {
		
		// 初始化根视图
		mRootView = new LinearLayout(mContext);
		LayoutParams params = new LayoutParams(LayoutParams.MATCH_PARENT,
				LayoutParams.MATCH_PARENT);
		mRootView.setLayoutParams(params);
		mRootView.setBackgroundResource(R.color.white);
	}

	/**
	 * 回调更新视图
	 */
	@Override
	public Void callBack(ContractType type) {
		if (type != mContractType) {
			switch (type) {
				case PROJECT_CONTRACT:
					if (mContractType == ContractType.PURCHASE_CONTRACT) {
						CustomTranslateAnimation.showLeft(mProjectCommon.getRootView(), 
								mPurchaseCommon.getRootView());
						mProjectCommon.getBaseWindow()
									.setRadioButtonPosition(14, GLOBAL.CONTRACT_TYPE[0][1]);
					} else {
						CustomTranslateAnimation.showLeft(mProjectCommon.getRootView(), 
								mLeaseCommon.getRootView());
						mProjectCommon.getBaseWindow()
									.setRadioButtonPosition(14, GLOBAL.CONTRACT_TYPE[0][1]);
					}
					break;
				case PURCHASE_CONTRACT:
					if (mContractType == ContractType.PROJECT_CONTRACT) {
						CustomTranslateAnimation.showRight(mPurchaseCommon.getRootView(),
								mProjectCommon.getRootView());
						mPurchaseCommon.getBaseWindow()
									.setRadioButtonPosition(10, GLOBAL.CONTRACT_TYPE[1][1]);
					} else {
						CustomTranslateAnimation.showLeft(mPurchaseCommon.getRootView(),
								mLeaseCommon.getRootView());
						mPurchaseCommon.getBaseWindow()
									.setRadioButtonPosition(10, GLOBAL.CONTRACT_TYPE[1][1]);
					}
					break;
				case LEASE_CONTRACT:
					if (mContractType == ContractType.PURCHASE_CONTRACT) {
						CustomTranslateAnimation.showRight(mLeaseCommon.getRootView(),
								mPurchaseCommon.getRootView());
						mLeaseCommon.getBaseWindow()
									.setRadioButtonPosition(10, GLOBAL.CONTRACT_TYPE[2][1]);
					} else {
						CustomTranslateAnimation.showRight(mLeaseCommon.getRootView(),
								mProjectCommon.getRootView());
						mLeaseCommon.getBaseWindow()
									.setRadioButtonPosition(10, GLOBAL.CONTRACT_TYPE[2][1]);
					}
					break;
				default:
					break;
			}
			
			mContractType = type;
		}
		return null;
	}

	/**
	 * 当根视图被添加到BaseViewPager，会被回调
	 */
	@SuppressWarnings("unchecked")
	@Override
	public void handleParentEvent(Contract b) {
		if (mContext instanceof CallBack) {
			((CallBack<Void, Integer>) mContext).callBack(CONTRACT_COMMON_INDEX);
		}
		
		if (mContractType == ContractType.PURCHASE_CONTRACT) {
			mPurchaseCommon.handleParentEvent(b);
		} else if (mContractType == ContractType.PROJECT_CONTRACT) {
			mProjectCommon.handleParentEvent(b);
		} else {
			mLeaseCommon.handleParentEvent(b);
		}
	}
	
	/**
	 * 更新指定的合同对象
	 * @param contract
	 */
	public Contract updateContract(Contract contract) {
		if (mContractType == ContractType.PURCHASE_CONTRACT) {
			return mPurchaseCommon.updateContract(contract);
		} else if (mContractType == ContractType.PROJECT_CONTRACT) {
			return mProjectCommon.updateContract(contract);
		} else {
			return mLeaseCommon.updateContract(contract);
		}
	}
	
	/**
	 * 更新当前合同对象
	 */
	public Contract updateContract() {
		if (mContractType == ContractType.PURCHASE_CONTRACT) {
			return mPurchaseCommon.updateContract();
		} else if (mContractType == ContractType.PROJECT_CONTRACT) {
			return mProjectCommon.updateContract();
		} else {
			return mLeaseCommon.updateContract();
		}
	}
	
	/**
	 * 当选中往来单位，更新信息
	 * @param contactCompany
	 */
	public void setPurchaseContactCompany(P_LWDW contactCompany) {
		mPurchaseCommon.setContactCompany(contactCompany);
	}
	
	/**
	 * 当选中往来单位，更新信息
	 * @param contactCompany
	 */
	public void setLeaseContactCompany(P_LWDW contactCompany) {
		mLeaseCommon.setContactCompany(contactCompany);
	}
}
