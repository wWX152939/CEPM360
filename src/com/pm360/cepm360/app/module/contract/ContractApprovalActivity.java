package com.pm360.cepm360.app.module.contract;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;

import com.pm360.cepm360.R;
import com.pm360.cepm360.app.common.activity.DataManagerInterface;
import com.pm360.cepm360.app.common.view.parent.list.BaseListCommon;
import com.pm360.cepm360.app.module.contract.pager.ContractPayment;
import com.pm360.cepm360.common.GLOBAL;
import com.pm360.cepm360.common.ResultStatus;
import com.pm360.cepm360.common.json.AnalysisManager;
import com.pm360.cepm360.entity.Contract_payment;
import com.pm360.cepm360.entity.Flow_approval;
import com.pm360.cepm360.entity.Message;
import com.pm360.cepm360.services.expenses.RemoteExpensesContractService;

import java.util.List;

public class ContractApprovalActivity extends Activity {
	
	private ContractPayment mContractPayment;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		
		// 初始化合同支付组件
		initContractPayment();
				
		// 初始化参数
		initArgumentsData();
	}
	
	/**
	 * 初始化支出合同付款组件
	 */
	private void initContractPayment() {
		mContractPayment = new ContractPayment(this, false, false, false);
		mContractPayment.setAllDialogButton(false, false, false);
		mContractPayment.setCloseViewListener(new OnClickListener() {
			
			@Override
			public void onClick(View v) {
				finish();
			}
		});
		setContentView(mContractPayment.getDialogRootView());
	}
	
	/**
	 * 初始化参数
	 */
	private void initArgumentsData() {
		DataManagerInterface dataManagerInterface = new DataManagerInterface() {
			
			@Override
			public void getDataOnResult(ResultStatus status, List<?> list) {
				if (status.getCode() == AnalysisManager.SUCCESS_DB_QUERY) {
					if (!list.isEmpty()) {
						
						// 获取支付详情
						RemoteExpensesContractService.getInstance()
									.getPayDetail(new DataManagerInterface() {
							
							@Override
							public void getDataOnResult(ResultStatus status, List<?> list) {
								mContractPayment.sendMessage(BaseListCommon.DISMISS_PROGRESS_DIALOG);
								if (status.getCode() == AnalysisManager.SUCCESS_DB_QUERY) {
									if (!list.isEmpty()) {
										Contract_payment contractPayment = (Contract_payment) list.get(0);
										mContractPayment.setCurrentItem(contractPayment);
										mContractPayment.switchMenuAndButton();
										mContractPayment.updateDialogValues(false);
									} else {
										mContractPayment.sendMessage(BaseListCommon.SHOW_TOAST, 
												getString(R.string.cannot_query_specified_record));
									}
								} else {
									mContractPayment.sendMessage(BaseListCommon.SHOW_TOAST, 
											status.getMessage());
								}
							}
						}, ((Flow_approval) list.get(0)).getType_id());
					} else {
						mContractPayment.sendMessage(BaseListCommon.DISMISS_PROGRESS_DIALOG);
						mContractPayment.sendMessage(BaseListCommon.SHOW_TOAST, 
								getString(R.string.cannot_query_specified_record));
					}
				} else {
					mContractPayment.sendMessage(BaseListCommon.DISMISS_PROGRESS_DIALOG);
					mContractPayment.sendMessage(BaseListCommon.SHOW_TOAST, 
							status.getMessage());
				}
			}
		};
		
		Message message = (Message) getIntent()
				.getSerializableExtra(GLOBAL.MSG_OBJECT_KEY);
		mContractPayment.sendEmptyMessageDelayed(BaseListCommon.SHOW_PROGRESS_DIALOG);
		RemoteExpensesContractService.getInstance()
					.getPayIDByFlowApprovalID(dataManagerInterface, message.getType_id());
	}
	
	@Override
	protected void onActivityResult(int requestCode, int resultCode, Intent data) {
		super.onActivityResult(requestCode, resultCode, data);
		if (resultCode == Activity.RESULT_OK) {
			mContractPayment.OnActivityResult(requestCode, resultCode, data);
		}
	}
}
