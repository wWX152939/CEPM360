package com.pm360.cepm360.app.module.contract;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.pm360.cepm360.R;
import com.pm360.cepm360.app.cache.UserCache;
import com.pm360.cepm360.app.common.activity.DataManagerInterface;
import com.pm360.cepm360.app.common.activity.ListSelectActivity;
import com.pm360.cepm360.app.common.custinterface.CommonListInterface;
import com.pm360.cepm360.app.common.custinterface.OperationMode;
import com.pm360.cepm360.app.common.custinterface.ServiceInterface;
import com.pm360.cepm360.app.common.view.parent.list.BaseListCommon;
import com.pm360.cepm360.app.common.view.parent.list.PermissionManager;
import com.pm360.cepm360.app.common.view.parent.list.SimpleList;
import com.pm360.cepm360.common.GLOBAL;
import com.pm360.cepm360.common.ResultStatus;
import com.pm360.cepm360.common.json.AnalysisManager;
import com.pm360.cepm360.entity.Contract;
import com.pm360.cepm360.entity.Project;
import com.pm360.cepm360.services.expenses.RemoteRevenueContractService;

import java.text.DecimalFormat;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * 收入合同
 * @author yuanlu
 *
 */
public class IncomeContractWithoutTreeFragment extends Fragment {
	
	// 简单合同列表
	private SimpleList<Contract> mSimpleList;
	
	// 服务
	private RemoteRevenueContractService mRevenueContractService 
							= RemoteRevenueContractService.getInstance();
	
	// 当前工程项目
	private Project mProject;
	
	// 合同列表
	private List<Contract> mContractList;
	private Map<String, Contract> mCodeIdToContractMap = new HashMap<String, Contract>();
	private boolean mDataLoaded;
	
	// 基础资源Id、name映射
	Map<String, String> mContractTypeMap = new HashMap<String, String>();
	
	/**
	 * 在Fragment被创建是调用
	 */
	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {
		
		// 父类初始化
		super.onCreateView(inflater, container, savedInstanceState);
		
		// 初始化实现部分数据
		return prepareEnvironment();
	}
	
	/**
	 * 实例化时初始化
	 */
	private View prepareEnvironment() {
		
		// 为合同类型做映射
		mContractTypeMap = BaseListCommon.genIdNameMap(GLOBAL.CONTRACT_TYPE);
		
		// 生成列表对象
		mSimpleList = new SimpleList<Contract>(getActivity()) {
			
			@Override
			protected void displayFieldRemap(Map<String, String> displayFieldMap,
						Contract t, int position) {
				super.displayFieldRemap(displayFieldMap, t, position);
				
				double adjustTotle = t.getTotal() + t.getTotal_change();
				displayFieldMap.put("adjust_total", "" + adjustTotle);
				displayFieldMap.put("remain_money", "" + (adjustTotle - t.getPaid()));
				
				double percent = 0;
				if (adjustTotle != 0) {
					percent = ((double) (t.getPaid() * 100 / adjustTotle));
				}
				DecimalFormat format = new DecimalFormat("##.##");
				displayFieldMap.put("percent", format.format(percent) + "%");
			}
		};
		
		// 处理参数
		handleArguments();
		
		// 初始化权限
		mSimpleList.setPermission(GLOBAL.SYS_ACTION[29][0], 
								GLOBAL.SYS_ACTION[28][0], 
								PermissionManager.PERMISSION_TYPE_SYS);
		// 初始化列表
		mSimpleList.init( Contract.class, 
							listCommonListInterface,
							listServiceInterface);
		
		// 返回树、列表根视图
		return mSimpleList.getRootView();
	}
	
	/**
	 * 处理参数
	 */
	public void handleArguments() {
		Bundle bundle = getArguments();
		if (bundle != null) {
			if (bundle.containsKey(ListSelectActivity.SELECT_MODE_KEY)) {
				OperationMode mode = OperationMode.NORMAL;
				if (bundle.getBoolean(ListSelectActivity.SELECT_MODE_KEY)) {
					mode = OperationMode.MULTI_SELECT;
				} else {
					mode = OperationMode.SINGLE_SELECT;
				}
				
				// 设置操作模式
				mSimpleList.setOperationMode(mode);
			}
			
			if (bundle.containsKey(ListSelectActivity.PROJECT_KEY)) {
				mProject = (Project) bundle.getSerializable(ListSelectActivity.PROJECT_KEY);
			}
		}
	}
	
	/**------------------------ 列表相关定义 ----------------------*/
	/**
	 * 列表接口实现
	 */
	CommonListInterface<Contract> listCommonListInterface 
								= new CommonListInterface<Contract>() {

		@Override
		public Map<String, Map<String, String>> getDisplayFieldsSwitchMap() {
			Map<String, Map<String, String>> fieldSwitchMap 
							= new HashMap<String, Map<String, String>>();
			fieldSwitchMap.put("type", mContractTypeMap);
			return fieldSwitchMap;
		}

		@Override
		public int getListItemId(Contract t) {
			return t.getContract_id();
		}

		@Override
		public String[] getDisplayFeilds() {
			return new String[] {
					"code",		// 合同编号
					"name",		// 合同名称
					"total",	// 原合同总价
					"total_change",		// 累计变更
					"adjust_total",		// 调整后合同总价
					"paid",		// 已回款
					"remain_money",		// 剩余额
					"percent",		// 回款百分比
					"type" 		// 合同类型
			};
		}

		@Override
		public int getListHeaderLayoutId() {
			return R.layout.contract_list_item_layout;
		}

		@Override
		public int getListLayoutId() {
			return R.layout.contract_list_item_layout;
		}

		@Override
		public int getListHeaderNames() {
			return R.array.contract_income_list_header_names;
		}

		@Override
		public int getListHeaderIds() {
			return R.array.contract_list_header_ids;
		}

		@Override
		public int getListItemIds() {
			return R.array.contract_list_header_ids;
		}
	};
	
	/**
	 * 列表服务接口实现
	 */
	ServiceInterface<Contract> listServiceInterface 
								= new ServiceInterface<Contract>() {

		@Override
		public void getListData() {
			mRevenueContractService.getRevenueContractList(new DataManagerInterface() {
				
				@SuppressWarnings("unchecked")
				@Override
				public void getDataOnResult(ResultStatus status, List<?> list) {
					switch (status.getCode()) {
						case AnalysisManager.SUCCESS_DB_QUERY:
							mContractList = (List<Contract>) list;
							
							if (!mContractList.isEmpty()) {
								
								// 构建编码和ID到合同对象的映射
								buildCodeIdToContractMap();
								
								// 获取合同累计变更
								getContractTotalChanges();
								
								// 获取合同累计支付
								getContractTotalPaid();
							} else {
								mSimpleList.sendMessage(BaseListCommon.DISMISS_PROGRESS_DIALOG, 
										status.getMessage());
							}
							break;
						case AnalysisManager.EXCEPTION_DB_QUERY:
							mSimpleList.sendMessage(BaseListCommon.DISMISS_PROGRESS_DIALOG, 
									status.getMessage());
							break;
						default:
							break;
					}
				}
			}, UserCache.getTenantId(), mProject.getProject_id());
		}

		@Override
		public void addItem(Contract t) {
			
		}

		@Override
		public void deleteItem(Contract t) {
			
		}

		@Override
		public void updateItem(Contract t) {
			
		}
	};
	
	/**
	 * 构建编码和ID到合同对象的映射表
	 */
	private void buildCodeIdToContractMap() {
		for (int i = 0; i < mContractList.size(); i++) {
			Contract contract = mContractList.get(i);
			mCodeIdToContractMap.put(contract.getContract_id() + "", contract);
			mCodeIdToContractMap.put(contract.getCode(), contract);
		}
	}
	
	/**
	 * 获取合同的累计变更
	 */
	private void getContractTotalChanges() {
		StringBuilder sb = new StringBuilder();
		for (int i = 0; i < mContractList.size(); i++) {
			sb.append("'" + mContractList.get(i).getCode() + "',");
		}
		
		String codes = sb.subSequence(0, sb.length() - 1).toString();
		mRevenueContractService.getRevenueContractByToatlChanges(new DataManagerInterface() {
			
			@Override
			public void getDataOnResult(ResultStatus status, List<?> list) {
				switch (status.getCode()) {
					case AnalysisManager.SUCCESS_DB_QUERY:
						@SuppressWarnings("unchecked")
						List<Contract> contracts = (List<Contract>) list;
						for (int i = 0; i < contracts.size(); i++) {
							Contract source = contracts.get(i);
							Contract targetContract = mCodeIdToContractMap.get(source.getCode());
							targetContract.setTotal_change(source.getTotal_change());
						}
						// fall through
					case AnalysisManager.EXCEPTION_DB_QUERY:
						handleData();
						break;
					default:
						break;
				}
			}
		}, codes);
	}
	
	/**
	 * 获取累计支付
	 */
	private void getContractTotalPaid() {
		StringBuilder sb = new StringBuilder();
		for (int i = 0; i < mContractList.size(); i++) {
			sb.append(mContractList.get(i).getContract_id() + ",");
		}
		
		String ids = sb.subSequence(0, sb.length() - 1).toString();
		mRevenueContractService.getRevenueContractByToatlPaid(new DataManagerInterface() {
			
			@Override
			public void getDataOnResult(ResultStatus status, List<?> list) {
				switch (status.getCode()) {
					case AnalysisManager.SUCCESS_DB_QUERY:
						@SuppressWarnings("unchecked")
						List<Contract> contracts = (List<Contract>) list;
						for (int i = 0; i < contracts.size(); i++) {
							Contract source = contracts.get(i);
							Contract targetContract = mCodeIdToContractMap
									.get(source.getContract_id() + "");
							targetContract.setPaid(source.getPaid());
						}
					case AnalysisManager.EXCEPTION_DB_QUERY:
						handleData();
						break;
					default:
						break;
				}
			}
		}, ids);
	}
	
	/**
	 * 数据处理
	 */
	private void handleData() {
		if (mDataLoaded) {
			mSimpleList.sendMessage(BaseListCommon.DISMISS_PROGRESS_DIALOG);
			mSimpleList.getListAdapter().setDataList(mContractList);
			mSimpleList.getListAdapter().setShowDataList(mContractList);
			
			// 复位
			mDataLoaded = false;
		} else {
			mDataLoaded = true;
		}
	}
}
