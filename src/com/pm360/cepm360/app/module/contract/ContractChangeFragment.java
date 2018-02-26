package com.pm360.cepm360.app.module.contract;

import android.annotation.SuppressLint;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;

import com.pm360.cepm360.R;
import com.pm360.cepm360.app.cache.EpsCache;
import com.pm360.cepm360.app.cache.ProjectCache;
import com.pm360.cepm360.app.cache.TenantCache;
import com.pm360.cepm360.app.cache.UserCache;
import com.pm360.cepm360.app.common.activity.DataManagerInterface;
import com.pm360.cepm360.app.common.activity.ListSelectActivity;
import com.pm360.cepm360.app.common.adpater.DataExpanableListAdapter.ViewHolder;
import com.pm360.cepm360.app.common.custinterface.CommonListInterface;
import com.pm360.cepm360.app.common.custinterface.OperationMode;
import com.pm360.cepm360.app.common.custinterface.OptionMenuInterface;
import com.pm360.cepm360.app.common.custinterface.ServiceInterface;
import com.pm360.cepm360.app.common.custinterface.SimpleListInterface;
import com.pm360.cepm360.app.common.custinterface.SimpleServiceInterface;
import com.pm360.cepm360.app.common.view.OptionsMenuView.SubMenuListener;
import com.pm360.cepm360.app.common.view.parent.list.BaseListCommon;
import com.pm360.cepm360.app.common.view.parent.list.ExpanableListWithOptionMenu;
import com.pm360.cepm360.app.common.view.parent.list.PermissionManager;
import com.pm360.cepm360.app.common.view.parent.list.TreeTwoExpanableListOptionMenu;
import com.pm360.cepm360.app.module.email.MailboxViewFragment;
import com.pm360.cepm360.app.utils.UtilTools;
import com.pm360.cepm360.common.GLOBAL;
import com.pm360.cepm360.common.ResultStatus;
import com.pm360.cepm360.common.json.AnalysisManager;
import com.pm360.cepm360.entity.Contract_change;
import com.pm360.cepm360.entity.EPS;
import com.pm360.cepm360.entity.Expandable;
import com.pm360.cepm360.entity.MailBox;
import com.pm360.cepm360.entity.Project;
import com.pm360.cepm360.entity.TreeTwoBean;
import com.pm360.cepm360.services.common.RemoteCommonService;
import com.pm360.cepm360.services.expenses.RemoteChangeContractService;
import com.pm360.cepm360.services.project.RemoteProjectService;
import com.pm360.cepm360.services.system.RemoteEPSService;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * 合同变更申请
 * @author yuanlu
 *
 */
public class ContractChangeFragment extends Fragment {
	
	// ViewPager的树、列表对象
	private TreeTwoExpanableListOptionMenu<EPS, Project, Contract_change> mTreeList;
	
	// 合同变更列表
	private List<Contract_change> mContractChangeList;
	private Map<String, Contract_change> mIdToContractChangeMap 
								= new HashMap<String, Contract_change>();
									
	// 服务
	private RemoteEPSService mEpsService = RemoteEPSService.getInstance();
	private RemoteProjectService mProjectService = RemoteProjectService.getInstance();
	private RemoteChangeContractService mListService 
								= RemoteChangeContractService.getInstance();
	
	// 基础资源Id、name映射
	Map<String, String> mStatusMap;
	
	// 广播接收器
	BroadcastReceiver mReceiver;
	
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
					mTreeList.getListAdapter().addData(mTreeList
							.getList().getParentName(contractChange), contractChange);
					
					// 通知树列表更新计数
					mTreeList.getTreeAdapter().updateTreeNodesCount(contractChange.getProject_id(), 1);
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
		mStatusMap = BaseListCommon.genIdNameMap(GLOBAL.CONTRACT_CHANGE_STATUS);
		
		// 右侧列表实现
		ExpanableListWithOptionMenu<Contract_change> listWithOptionMenu 
									= new ExpanableListWithOptionMenu<Contract_change>(getActivity()) {
			
			@Override
			protected void displayFieldRemap(Map<String, String> displayFieldMap,
						Contract_change t, int position) {
				super.displayFieldRemap(displayFieldMap, t, position);
				
				// 往来记录文本
				displayFieldMap.put("contact_record", "");
				displayFieldMap.put("bqbgk", UtilTools.formatMoney("", t.getBqbgk(), 2));
			}

			@Override
			public String getParentName(Contract_change t) {
				return t.getContract_code() + "-"+ t.getContract_name();
			}

			@Override
			public Map<String, List<Contract_change>> formateData(
					List<Contract_change> list) {
				Map<String, List<Contract_change>> changeMap 
								= new HashMap<String, List<Contract_change>>();
				
				for (int i = 0; i < list.size(); i++) {
					String parentName = getParentName(list.get(i));
					if (changeMap.containsKey(parentName)) {
						List<Contract_change> changes = changeMap.get(parentName);
						changes.add(list.get(i));
					} else {
						List<Contract_change> changes = new ArrayList<Contract_change>();
						changes.add(list.get(i));
						changeMap.put(parentName, changes);
					}
				}
				return changeMap;
			}
			
			@Override
			protected void clickRegister(ViewHolder viewHolder, final int position) {
				super.clickRegister(viewHolder, position);
				
				if (mCurrentMode == OperationMode.NORMAL) {
					viewHolder.tvs[viewHolder.tvs.length-1].setOnClickListener(new OnClickListener() {

						@Override
						public void onClick(View v) {
							mCurrentItem = mListAdapter.getItem(position);
							mListAdapter.setSelected(position, true);
							RemoteCommonService.getInstance().getMailByType(new DataManagerInterface() {
								
								@SuppressWarnings("unchecked")
								@Override
								public void getDataOnResult(ResultStatus status, List<?> list) {
									Intent intent = new Intent(mContext, 
											ListSelectActivity.class);
									intent.putExtra(ListSelectActivity.FRAGMENT_KEY, 
											MailboxViewFragment.class);
									intent.putExtra(ListSelectActivity.SHOW_DATA, 
											(ArrayList<MailBox>) list);
									intent.putExtra(ListSelectActivity.SELECT_MODE_KEY, false);
									startActivity(intent);
								}
							}, GLOBAL.MAIL_TABLE_TYPE[0][0], mCurrentItem.getId());
						}
					});
				}
			}
			
			@Override
			protected void initListViewItemMore(ViewHolder holder,
					int position, Map<String, String> displayFieldMap) {
				super.initListViewItemMore(holder, position, displayFieldMap);
				
				Drawable drawable = getResources().getDrawable(R.drawable.mailbox);
				
				//必须设置图片大小，否则不显示
	            drawable.setBounds(0, 0, drawable.getMinimumWidth(), drawable.getMinimumHeight());
	            int index = holder.tvs.length - 1;
	            holder.tvs[index].setCompoundDrawablePadding(UtilTools.dp2pxW(mContext, 4));
	            holder.tvs[index].setCompoundDrawables(drawable, null, null, null);
				holder.tvs[index].setText(displayFieldMap.get("contact_record"));
			}
		};
	
		// 必须先创建对象，然后设置权限（权限管理对象在本对象创建时被创建）
		mTreeList = new TreeTwoExpanableListOptionMenu<EPS, Project, Contract_change>(getActivity(), 
							getActivity().getResources().getString(R.string.project_directory),
							listWithOptionMenu) {

				@Override
				public int getTreeListRelevanceId(Contract_change c) {
					return c.getProject_id();
				}

				@SuppressLint("UseSparseArrays") 
				@Override
				public List<TreeTwoBean> genTreeTwoBeanList(
						List<EPS> parentList, List<Project> childList) {
					return getTreeTwoBeanList(parentList, childList);
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
		
		// 初始化权限
		mTreeList.setPermission(GLOBAL.SYS_ACTION[28][0], 
								GLOBAL.SYS_ACTION[29][0], 
								PermissionManager.PERMISSION_TYPE_SYS);
		
		mTreeList.getList().setForceEnableOptionMenu(true);
		
		// 初始化列表
		mTreeList.initList( Contract_change.class, 
							listCommonListInterface,
							listServiceInterface, 
							null,
							optionMenuInterface);
				
		// 初始化树
		mTreeList.initTree( TreeTwoBean.class,
							treeListInterface, 
							treeServiceInterface);
		
		// 返回树、列表根视图
		return mTreeList.getRootView();
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
			
//			// 获取EPS列表
//			mEpsService.getEPSList(mTreeList.getParentManager(), 
//									UserCache.getCurrentUser());
//			
//			// 获取项目列表
//			mProjectService.getProjectList(mTreeList.getChildManager(),
//									UserCache.getCurrentUser());
		}
	};
	
	
	/**------------------------ 列表相关定义 ----------------------*/
	/**
	 * 列表接口实现
	 */
	CommonListInterface<Contract_change> listCommonListInterface 
								= new CommonListInterface<Contract_change>() {

		@Override
		public Map<String, Map<String, String>> getDisplayFieldsSwitchMap() {
			Map<String, Map<String, String>> fieldSwitchMap 
							= new HashMap<String, Map<String, String>>();
			fieldSwitchMap.put("status", mStatusMap);
			fieldSwitchMap.put("sender", TenantCache.getTenantIdMaps());
			fieldSwitchMap.put("receiver", TenantCache.getTenantIdMaps());
			return null;
		}

		@Override
		public int getListItemId(Contract_change t) {
			return t.getId();
		}

		@Override
		public String[] getDisplayFeilds() {
			return new String[] {
					"code",		// 变更编号
					"name",		// 变更名称
					"apply_date",	// 申请日期
					"pass_date",		// 生效日期
					"bqbgk",	// 本期变更款
					"contact_record" 		// 往来记录
			};
		}

		@Override
		public int getListHeaderLayoutId() {
			return R.layout.contract_change_list_item_layout;
		}

		@Override
		public int getListLayoutId() {
			return R.layout.contract_change_list_item_layout;
		}

		@Override
		public int getListHeaderNames() {
			return R.array.contract_change_list_header_names;
		}

		@Override
		public int getListHeaderIds() {
			return R.array.contract_change_list_header_ids;
		}

		@Override
		public int getListItemIds() {
			return R.array.contract_change_list_header_ids;
		}
	};
	
	/**
	 * 列表服务接口实现
	 */
	ServiceInterface<Contract_change> listServiceInterface 
								= new ServiceInterface<Contract_change>() {

		@Override
		public void getListData() {
			mListService.getChangeContractList(new DataManagerInterface() {
				
				@SuppressWarnings("unchecked")
				@Override
				public void getDataOnResult(ResultStatus status, List<?> list) {
					switch (status.getCode()) {
						case AnalysisManager.SUCCESS_DB_QUERY:
							mContractChangeList = (List<Contract_change>) list;
							
							if (!mContractChangeList.isEmpty()) {
								
								// 构建编码和ID到合同变更对象的映射
								buildIdToContractChangeMap();
								
								// 获取合同变更金额
								getContractChangeMoney();
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
			}, UserCache.getTenantId(), 0);
		}

		@Override
		public void addItem(Contract_change t) {
			
		}

		@Override
		public void deleteItem(Contract_change t) {
			
		}

		@Override
		public void updateItem(Contract_change t) {
			
		}
	};
	
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
	 * 构建编码和ID到合同变更对象的映射表
	 */
	private void buildIdToContractChangeMap() {
		for (int i = 0; i < mContractChangeList.size(); i++) {
			Contract_change contractChange = mContractChangeList.get(i);
			mIdToContractChangeMap.put(contractChange.getId() + "", contractChange);
		}
	}
	
	/**
	 * 获取变更金额
	 */
	private void getContractChangeMoney() {
		StringBuilder sb = new StringBuilder();
		for (int i = 0; i < mContractChangeList.size(); i++) {
			sb.append(mContractChangeList.get(i).getId() + ",");
		}
		
		String ids = sb.subSequence(0, sb.length() - 1).toString();
		mListService.getChangeContractMoney(new DataManagerInterface() {
			
			@Override
			public void getDataOnResult(ResultStatus status, List<?> list) {
				
				switch (status.getCode()) {
					case AnalysisManager.SUCCESS_DB_QUERY:
						@SuppressWarnings("unchecked")
						List<Contract_change> contractChanges = (List<Contract_change>) list;
						for (int i = 0; i < contractChanges.size(); i++) {
							Contract_change source = contractChanges.get(i);
							Contract_change target = mIdToContractChangeMap.get(source.getId() + "");
							if (target != null) {
								target.setBqbgk(source.getBqbgk());
							}
						}
						
						// fall through
					case AnalysisManager.EXCEPTION_DB_QUERY:
						mTreeList.getListServiceManager().getDataOnResult(status, mContractChangeList);
						
						// 加载树列表数据
						getTreeDataList();
						break;
					default:
						break;
				}
			}
		}, ids);
	}
	
	/**
	 * 选项菜单接口实现
	 */
	private OptionMenuInterface optionMenuInterface = new OptionMenuInterface() {
		
		@Override
		public int getOptionMenuNames() {
			return R.array.contract_option_menu2_names;
		}
		
		@Override
		public SubMenuListener getOptionMenuClickListener() {
			return new SubMenuListener() {
				
				@Override
				public void onSubMenuClick(View view) {
					mTreeList.getList().getOptionsMenu().dismiss();
					
					switch ((int) view.getTag()) {
						case 0:
						case 1:
						case 2:
							startAttributeActivity((int) view.getTag());
							break;
						default:
							break;
					}
					
				}
			};
		}
	};
	
	/*
	 * 启动属性Activity
	 */
	private void startAttributeActivity(int index) {
		Intent intent = new Intent(getActivity(), ContractChangeAttributeActivity.class);
		intent.putExtra(ContractChangeAttributeActivity.CONTRACT_CHANGE_KEY,
										mTreeList.getList().getCurrentItem());
		intent.putExtra(ContractChangeAttributeActivity.IS_MODIFY_KEY, false);
		intent.putExtra(ContractChangeAttributeActivity.IS_CHANGE_APPLY_KEY, false);
		intent.putExtra(ContractChangeAttributeActivity.TAB_INDEX_KEY, index);
		
		ArrayList<Contract_change> changes = new ArrayList<Contract_change>();
		String targetCode = mTreeList.getCurrentListItem().getContract_code();
		List<Contract_change> dataChanges = mTreeList.getListAdapter().getDataList();
		for (int i = 0; i < dataChanges.size(); i++) {
			if (dataChanges.get(i).getContract_code().equals(targetCode)) {
				changes.add(dataChanges.get(i));
			}
		}
		intent.putExtra(ContractChangeAttributeActivity.CHANGE_LIST_KEY, changes);
		startActivity(intent);
	}
	
	/**
	 * 获取当前树节点
	 * @return
	 */
	public TreeTwoBean getCurrentTreeNode() {
		return mTreeList.getCurrentTreeNode();
	}
}
