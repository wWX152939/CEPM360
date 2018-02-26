package com.pm360.cepm360.app.module.contract;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.pm360.cepm360.R;
import com.pm360.cepm360.app.cache.UserCache;
import com.pm360.cepm360.app.common.activity.ListSelectActivity;
import com.pm360.cepm360.app.common.custinterface.CommonListInterface;
import com.pm360.cepm360.app.common.custinterface.OperationMode;
import com.pm360.cepm360.app.common.custinterface.ServiceInterface;
import com.pm360.cepm360.app.common.view.parent.list.BaseListCommon;
import com.pm360.cepm360.app.common.view.parent.list.PermissionManager;
import com.pm360.cepm360.app.common.view.parent.list.SimpleList;
import com.pm360.cepm360.common.GLOBAL;
import com.pm360.cepm360.entity.Contract;
import com.pm360.cepm360.entity.Project;
import com.pm360.cepm360.services.expenses.RemoteExpensesContractService;

import java.util.HashMap;
import java.util.Map;

/**
 * 租凭合同
 *
 */
public class LeaseContractWithoutTreeFragment extends Fragment {
	
	// 简单合同列表
	private SimpleList<Contract> mSimpleList;
	
	// 服务
	private RemoteExpensesContractService mExpensesContractService 
									= RemoteExpensesContractService.getInstance();
	
	// 当前工程项目
	private Project mProject;
	
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
					"create_date" 	// 合同类型
			};
		}

		@Override
		public int getListHeaderLayoutId() {
			return R.layout.lease_contract_list_item_layout;
		}

		@Override
		public int getListLayoutId() {
			return R.layout.lease_contract_list_item_layout;
		}

		@Override
		public int getListHeaderNames() {
			return R.array.lease_contract_list_header_names;
		}

		@Override
		public int getListHeaderIds() {
			return R.array.lease_contract_list_header_ids;
		}

		@Override
		public int getListItemIds() {
			return R.array.lease_contract_list_header_ids;
		}
	};
	
	/**
	 * 列表服务接口实现
	 */
	ServiceInterface<Contract> listServiceInterface 
								= new ServiceInterface<Contract>() {

		@Override
		public void getListData() {
			mExpensesContractService.getLeaseContractList(mSimpleList.getServiceManager(), UserCache.getTenantId(), mProject.getProject_id());
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
}
