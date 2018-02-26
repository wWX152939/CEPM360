package com.pm360.cepm360.app.module.contract;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;

import com.pm360.cepm360.R;
import com.pm360.cepm360.app.cache.EpsCache;
import com.pm360.cepm360.app.cache.ProjectCache;
import com.pm360.cepm360.app.cache.UserCache;
import com.pm360.cepm360.app.common.activity.DataManagerInterface;
import com.pm360.cepm360.app.common.activity.ListSelectActivity;
import com.pm360.cepm360.app.common.custinterface.CommonListInterface;
import com.pm360.cepm360.app.common.custinterface.FloatingMenuInterface;
import com.pm360.cepm360.app.common.custinterface.OperationMode;
import com.pm360.cepm360.app.common.custinterface.OptionMenuInterface;
import com.pm360.cepm360.app.common.custinterface.ServiceInterface;
import com.pm360.cepm360.app.common.custinterface.SimpleListInterface;
import com.pm360.cepm360.app.common.custinterface.SimpleServiceInterface;
import com.pm360.cepm360.app.common.view.OptionsMenuView.SubMenuListener;
import com.pm360.cepm360.app.common.view.parent.list.BaseListCommon;
import com.pm360.cepm360.app.common.view.parent.list.ListWithOptionMenu;
import com.pm360.cepm360.app.common.view.parent.list.PermissionManager;
import com.pm360.cepm360.app.common.view.parent.list.TreeTwoListOptionMenu;
import com.pm360.cepm360.app.utils.UtilTools;
import com.pm360.cepm360.common.GLOBAL;
import com.pm360.cepm360.common.ResultStatus;
import com.pm360.cepm360.common.json.AnalysisManager;
import com.pm360.cepm360.common.util.LogUtil;
import com.pm360.cepm360.common.util.MiscUtils;
import com.pm360.cepm360.entity.Contract;
import com.pm360.cepm360.entity.Contract_change;
import com.pm360.cepm360.entity.EPS;
import com.pm360.cepm360.entity.Expandable;
import com.pm360.cepm360.entity.Project;
import com.pm360.cepm360.entity.TreeTwoBean;
import com.pm360.cepm360.services.expenses.RemoteRevenueContractService;
import com.pm360.cepm360.services.project.RemoteProjectService;
import com.pm360.cepm360.services.system.RemoteEPSService;

import java.io.Serializable;
import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * 收入合同
 * @author yuanlu
 *
 */
public class IncomeContractFragment extends Fragment {
	
	// ViewPager的树、列表对象
	private TreeTwoListOptionMenu<EPS, Project, Contract> mTreeList;
	
	// 服务
	private RemoteEPSService mEpsService = RemoteEPSService.getInstance();
	private RemoteProjectService mProjectService = RemoteProjectService.getInstance();
	private RemoteRevenueContractService mListService 
								= RemoteRevenueContractService.getInstance();
	
	// 合同列表
	private List<Contract> mContractList;
	private Map<String, Contract> mCodeIdToContractMap = new HashMap<String, Contract>();
	private boolean mDataLoaded;
	
	// 基础资源Id、name映射
	Map<String, String> mContractTypeMap = new HashMap<String, String>();
	
	// 广播接收器
	BroadcastReceiver mReceiver;
	
	// 权限管理
	PermissionManager mPermissionManager;
	
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
	
	@Override
	public void onStart() {
		super.onStart();
		
		// 注册广播接收器
		registerBroadCastReciever();
	}

	@Override
	public void onStop() {
		super.onStop();
		getActivity().unregisterReceiver(mReceiver);
	}

	/**
	 * 注册广播接收器
	 */
	public void registerBroadCastReciever() {
		IntentFilter intentFilter 
				= new IntentFilter(ContractActivity.ACTION_CONTRACT_CHANGE_TAKE_EFFECT);
		
		// 实现广播接收器
		mReceiver = new BroadcastReceiver() {
			
			@Override
			public void onReceive(Context context, Intent intent) {
				if (intent.getAction().equals(ContractActivity
								.ACTION_CONTRACT_CHANGE_TAKE_EFFECT)) {
					Contract_change contractChange = (Contract_change) intent
							.getSerializableExtra(ContractActivity.CONTRACT_CHANGE_TAKE_EFFECT);
					List<Contract> contracts = mTreeList.getListAdapter().getDataList();
					int size = mTreeList.getListAdapter().getDataList().size();
					for (int i = 0; i < size; i++) {
						Contract contract = contracts.get(i);
						
						// 查找指定合同更新累计变更字段
						if (contractChange.getContract_code().equals(contract.getCode())
								&& contractChange.getContract_name().equals(contract.getName())) {
							contract.setTotal_change(contract.getTotal_change() + contractChange.getBqbgk());
							mTreeList.getListAdapter().notifyDataSetChanged();
							break;
						}
					}
				}
			}
		};
		
		getActivity().registerReceiver(mReceiver, intentFilter);
	}
	
	/**
	 * 实例化时初始化
	 */
	private View prepareEnvironment() {
		
		// 为合同类型做映射
		mContractTypeMap = BaseListCommon.genIdNameMap(GLOBAL.CONTRACT_TYPE);
		
		// 右侧列表实现
		ListWithOptionMenu<Contract> listWithOptionMenu 
									= new ListWithOptionMenu<Contract>(getActivity()) {
			
			@Override
			protected void displayFieldRemap(Map<String, String> displayFieldMap,
						Contract t, int position) {
				super.displayFieldRemap(displayFieldMap, t, position);
				
				double adjustTotle = t.getTotal() + t.getTotal_change();
				displayFieldMap.put("total", UtilTools.formatMoney("", t.getTotal(), 2));
				displayFieldMap.put("total_change", UtilTools
						.formatMoney("", t.getTotal_change(), 2));
				displayFieldMap.put("adjust_total", UtilTools
						.formatMoney("", adjustTotle, 2));
				displayFieldMap.put("paid", UtilTools.formatMoney("", t.getPaid(), 2));
				displayFieldMap.put("remain_money", UtilTools
						.formatMoney("", adjustTotle - t.getPaid(), 2));
				
				double percent = 0;
				if (adjustTotle != 0) {
					percent = ((double) (t.getPaid() * 100 / adjustTotle));
				}
				DecimalFormat format = new DecimalFormat("##.##");
				displayFieldMap.put("percent", format.format(percent) + "%");
			}
			
			@SuppressWarnings("unchecked")
			@Override
			public void handleDataOnResult(ResultStatus status, List<?> list) {
				super.handleDataOnResult(status, list);
				
				switch (status.getCode()) {
					case AnalysisManager.SUCCESS_DB_QUERY:
						mListAdapter.setShowDataList((List<Contract>) list);
						break;
					default:
						break;
				}
			}
			
			@Override
			protected void longClickHandler(View view, int position) {
				if (mTreeList.getCurrentTreeNode().isHas_child()) {
					
					// 正常模式并且非子树节点时，使能多选模式
					mTreeList.getList().enableNormalMultSelect(false);
				} else {
					
					// 正常模式并且子树节点时，使能多选模式
					mTreeList.getList().enableNormalMultSelect(true);
				}
				
				super.longClickHandler(view, position);
			}
		};
	
		// 必须先创建对象，然后设置权限（权限管理对象在本对象创建时被创建）
		mTreeList = new TreeTwoListOptionMenu<EPS, Project, Contract>(getActivity(), 
							getActivity().getResources().getString(R.string.project_directory),
							listWithOptionMenu) {

			@Override
			public int getTreeListRelevanceId(Contract c) {
				return c.getProject_id();
			}

			@SuppressLint("UseSparseArrays") 
			@Override
			public List<TreeTwoBean> genTreeTwoBeanList(
					List<EPS> parentList, List<Project> childList) {
				return getTreeTwoBeanList(parentList, childList);
			}
			
			@Override
			protected void switchClickShow() {
				super.switchClickShow();
				
				// 这里切换选项菜单
				if (mSimpleTreeForDir.getCurrentItem().isHas_child()) {
					mListWithOptionMenu.setOptionMenuInterface(optionMenuInterface2);
				} else {
					if (mPermissionManager.hasEditPermission()) {
						mListWithOptionMenu.setOptionMenuInterface(optionMenuInterface);
					}
				}
			}
			
			@Override
			protected <E extends Expandable & Serializable> boolean getIsBoldText(E t) {
				if (((com.pm360.cepm360.entity.TreeTwoBean) t).isParent()) {
					return true;
				} else {
					return false;
				}
			}
		};
		
		// 处理参数
		handleArguments();
		
		// 初始化权限
		mTreeList.setPermission(GLOBAL.SYS_ACTION[29][0], 
								GLOBAL.SYS_ACTION[28][0], 
								PermissionManager.PERMISSION_TYPE_SYS);
		
		// 获取权限管理器
		mPermissionManager = mTreeList.getList().getPermissionManager();
		
		// 强制使能选项菜单
		mTreeList.getList().setForceEnableOptionMenu(true);
		
		// 初始化列表
		mTreeList.initList( Contract.class, 
							listCommonListInterface,
							listServiceInterface, 
							null,
							optionMenuInterface2, 
							floatingMenuInterface);
				
		// 初始化树
		mTreeList.initTree( TreeTwoBean.class,
							treeListInterface, 
							treeServiceInterface);
		
		// 返回树、列表根视图
		return mTreeList.getRootView();
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
				mTreeList.setOperationMode(mode);
			}
		}
	}
	
	/**
	 * 构造树节点对象列表，由于构造过程存在差异，这里单独做算法实现
	 * @param parentList
	 * @param childList
	 * @return
	 */
	@SuppressLint("UseSparseArrays") 
	private List<TreeTwoBean> getTreeTwoBeanList(
			List<EPS> parentList, List<Project> childList) {
		
		List<TreeTwoBean> trees = new ArrayList<TreeTwoBean>();
		Map<Integer, Integer> maps = new HashMap<Integer, Integer>();
		
		int id = 1;
		
		// 构造EPS对应的EpsProjectTree对象列表
		for (int i = 0; i < parentList.size(); i++) {
			EPS eps = parentList.get(i);
			TreeTwoBean node = new TreeTwoBean();
			
			node.setVirtualId(id++);
			node.setParent(true);
			node.setRealId(eps.getEps_id());
			node.setName(eps.getName());
			node.setTenantId(eps.getTenant_id());
			node.setParentId(eps.getParents_id());
			
			// 加入映射表
			maps.put(node.getRealId(), node.getVirtualId());
			
			trees.add(node);
		}

		// 构造Project对应的EpsProjectTree对象列表
		for (int i = 0; i < childList.size(); i++) {
			Project project = childList.get(i);
			TreeTwoBean node = new TreeTwoBean();
			
			node.setVirtualId(id++);
			node.setParent(false);
			node.setRealId(project.getProject_id());
			node.setName(project.getName());
			node.setTenantId(project.getTenant_id());
			node.setParentId(project.getEps_id());
			
			trees.add(node);
		}
		
		// 从新设置父节点ID
		for (int i = 0; i < trees.size(); i++) {
			TreeTwoBean node = trees.get(i);
			int parentId = node.getParentId();
			if (parentId == 0) {
				continue;
			}
			
			node.setParentId(maps.get(parentId));
		}
		
		return trees;
	}
	
	/**
	 * 简单树视图接口实现
	 */
	SimpleListInterface<TreeTwoBean> treeListInterface 
								= new SimpleListInterface<TreeTwoBean>() {

		@Override
		public int getListItemId(TreeTwoBean t) {
			return t.getId();
		}

		@Override
		public String[] getDisplayFeilds() {
			return new String[] {
					"name"
			};
		}
	};
	
	/**
	 * 树服务接口
	 */
	SimpleServiceInterface<TreeTwoBean> treeServiceInterface 
							= new SimpleServiceInterface<TreeTwoBean>() {

		@Override
		public void getListData() {
			
		}
	};
	
	
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
			mListService.getRevenueContractList(new DataManagerInterface() {
				
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
								mTreeList.getList().sendMessage(BaseListCommon.DISMISS_PROGRESS_DIALOG, 
										status.getMessage());
								
								// 加载树列表数据
								getTreeDataList();
							}
							break;
						case AnalysisManager.EXCEPTION_DB_QUERY:
							mTreeList.getList().sendMessage(BaseListCommon.DISMISS_PROGRESS_DIALOG, 
									status.getMessage());
							break;
						default:
							break;
					}
				}
			}, UserCache.getCurrentUser().getTenant_id(), 0);
		}

		@Override
		public void addItem(Contract t) {
			t.setProject_id(mTreeList.getCurrentTreeNode().getRealId());
			t.setTenant_id(mTreeList.getCurrentTreeNode().getTenantId());
			mListService.addRevenueContract(mTreeList.getListServiceManager(), t);
		}

		@Override
		public void deleteItem(Contract t) {
			mListService.deleteRevenueContract(mTreeList.getListServiceManager(),
												t.getContract_id());
		}

		@Override
		public void updateItem(Contract t) {
			mListService.updateRevenueContract(mTreeList.getListServiceManager(), t);
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
		mListService.getRevenueContractByToatlChanges(new DataManagerInterface() {
			
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
		mListService.getRevenueContractByToatlPaid(new DataManagerInterface() {
			
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
						
						// fall through
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
			mTreeList.getList().sendMessage(BaseListCommon.DISMISS_PROGRESS_DIALOG);
			mTreeList.getListAdapter().setDataList(mContractList);
			mTreeList.getListAdapter().setShowDataList(mContractList);
			
			// 复位
			mDataLoaded = false;
			
			// 加载树数据列表
			getTreeDataList();
		} else {
			mDataLoaded = true;
		}
	}
	
	/**
	 * 获取树数据列表
	 */
	private void getTreeDataList() {
		ResultStatus status = new ResultStatus(AnalysisManager.SUCCESS_DB_QUERY, null);
		
		// 加载EPS列表数据
		if (EpsCache.isDataLoaded()) {
			mTreeList.getParentManager().getDataOnResult(status, EpsCache.getEpsLists());
		} else {
			mEpsService.getEPSList(mTreeList.getParentManager(), 
									UserCache.getCurrentUser());
		}
		
		// 获取项目列表
		if (ProjectCache.isDataLoaded()) {
			mTreeList.getChildManager().getDataOnResult(status, ProjectCache.getProjectLists());
		} else {
			mProjectService.getProjectList(mTreeList.getChildManager(),
									UserCache.getCurrentUser());
		}
	}
	
	/**
	 * 选项菜单接口实现
	 */
	private OptionMenuInterface optionMenuInterface = new OptionMenuInterface() {
		
		@Override
		public int getOptionMenuNames() {
			return R.array.contract_new_option_menu_names;
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
	 * 选项菜单接口实现
	 */
	private OptionMenuInterface optionMenuInterface2 = new OptionMenuInterface() {
		
		@Override
		public int getOptionMenuNames() {
			return R.array.contract_new_option_menu2_names;
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
	 * 选项菜单单击处理
	 * @param view
	 */
	private void handleOptionMenuClick(View view) {
		mTreeList.getList().getOptionsMenu().dismiss();
		
		switch ((int) view.getTag()) {
			case 0:
			case 1:
			case 2:
			case 3:
				Intent intent = new Intent(getActivity(), ContractAttributeActivity.class);
				intent.putExtra(ContractAttributeActivity.CONTRACT_KEY,
										mTreeList.getList().getCurrentItem());
				intent.putExtra(ContractAttributeActivity.INCOME_CONTRACT_KEY, true);
				intent.putExtra(ContractAttributeActivity.IS_MODIFY_KEY, 
										mPermissionManager.hasEditPermission());
				intent.putExtra(ContractAttributeActivity.TAB_INDEX_KEY, (int) view.getTag());
				if (mPermissionManager.hasEditPermission()) {
					startActivityForResult(intent, 
							ContractActivity.INCOME_CONTRACT_UPDATE_REQUEST_CODE);
				} else {
					startActivity(intent);
				}
				break;
			case 4:	// 删除
				mTreeList.getList().commonConfirmDelete();
				break;
			default:
				break;
		}
	}
	
	/**
	 * 浮动菜单接口实现
	 */
	private FloatingMenuInterface floatingMenuInterface = new FloatingMenuInterface() {
		
		@Override
		public String[] getFloatingMenuTips() {
			return new String[] {
					getActivity().getResources().getString(R.string.add)
			};
		}
		
		@Override
		public OnItemClickListener getFloatingMenuListener() {
			return new OnItemClickListener() {

				@Override
				public void onItemClick(AdapterView<?> parent, View view,
						int position, long id) {
					switch (position) {
						case 0:
							mTreeList.getList().getFloatingMenu().dismiss();
							
							// 启动添加合同Activity
							Intent intent = new Intent(getActivity(), ContractAddActivity.class);
							
							if (mTreeList.getTree().getCurrentItem() != null) {
								TreeTwoBean tree = mTreeList.getTree().getCurrentItem();
								Project project = ProjectCache
														.findProjectById(tree.getRealId());
								intent.putExtra(ContractAddActivity.PROJECT_KEY, project);
								intent.putExtra(ContractAddActivity.INCOME_CONTRACT_KEY, true);
								
								// 启动AddContractActivity
								startActivityForResult(intent, 
										ContractActivity.INCOME_CONTRACT_ADD_REQUEST_CODE);
							} else {
								mTreeList.getList().sendMessage(BaseListCommon.SHOW_TOAST,
										getString(R.string.please_select_project));
							}
							break;
						default:
							break;
					}
					
				}
			};
		}
		
		@Override
		public int[] getFloatingMenuImages() {
			return new int[] { 
					R.drawable.icn_add 
			};
		}
	};
	
	/**
	 * 获取当前树节点
	 * @return
	 */
	public TreeTwoBean getCurrentTreeNode() {
		return mTreeList.getCurrentTreeNode();
	}
	
	@Override
	public void onActivityResult(int requestCode, int resultCode, Intent data) {
		super.onActivityResult(requestCode, resultCode, data);
		
		if (resultCode == Activity.RESULT_OK) {
			switch (requestCode) {
				case ContractActivity.INCOME_CONTRACT_ADD_REQUEST_CODE:
					if (data != null) {
						Contract contract = (Contract) data
									.getSerializableExtra(ContractAddActivity.RESULT_KEY);
						int detailFail = data.getIntExtra(ContractAddActivity.DETAIL_FAILED_KEY, 0);
						int paymentFail = data.getIntExtra(ContractAddActivity.PAYMENT_FAILED_KEY, 0);
						int attachmentFial = data.getIntExtra(ContractAddActivity.ATTACHMENT_FAILED_KEY, 0);
						
						mTreeList.getList().getListAdapter().addDataToList(contract);
						mTreeList.getList().sendMessage(BaseListCommon.SHOW_TOAST, 
									getToastString(detailFail, paymentFail, attachmentFial));
						mTreeList.getTreeAdapter().updateTreeNodesCount(1);
					}
					break;
				case ContractActivity.INCOME_CONTRACT_UPDATE_REQUEST_CODE:
					if (data != null) {
						Contract contract = (Contract) data
								.getSerializableExtra(ContractAttributeActivity.CONTRACT_KEY);
						
						LogUtil.e("contract = " + contract);
						
						// 更新合同
						List<Contract> contracts = mTreeList.getListAdapter().getDataShowList();
						for (int i = 0; i < contracts.size(); i++) {
							if (contract.getContract_id() == contracts.get(i).getContract_id()) {
								MiscUtils.clone(contracts.get(i), contract);
								mTreeList.getListAdapter().notifyDataSetChanged();
								break;
							}
						}
					}
					break;
				default:
					break;
			}
		}
	}
	
	/**
	 * 通过参数获取Toast显示字符串
	 * @param detailFailCount
	 * @param paymentFailCount
	 * @param attachmentFailCount
	 * @return
	 */
	private String getToastString(int detailFailCount, 
					int paymentFailCount, int attachmentFailCount) {
		StringBuilder sb = new StringBuilder();
		
		if (detailFailCount != 0 
				|| paymentFailCount != 0
				|| attachmentFailCount != 0) {
			if (detailFailCount != 0) {
				sb.append(" " + R.string.contract_details_fail + " ");
			}
			
			if (paymentFailCount != 0) {
				sb.append(" " + R.string.contract_payment_fail + " ");
			}
			
			if (attachmentFailCount != 0) {
				sb.append(" " + R.string.contract_attachment_fail + " ");
			}
		} else {
			sb.append(R.string.contract_add_success);
		}
		
		return sb.toString();
	}
}
