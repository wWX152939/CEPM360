package com.pm360.cepm360.app.module.contract;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentFilter;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;

import com.pm360.cepm360.R;
import com.pm360.cepm360.app.cache.CooperationCache;
import com.pm360.cepm360.app.cache.EpsCache;
import com.pm360.cepm360.app.cache.ProjectCache;
import com.pm360.cepm360.app.cache.TenantCache;
import com.pm360.cepm360.app.cache.UserCache;
import com.pm360.cepm360.app.common.activity.DataManagerInterface;
import com.pm360.cepm360.app.common.activity.ListSelectActivity;
import com.pm360.cepm360.app.common.adpater.DataExpanableListAdapter.ViewHolder;
import com.pm360.cepm360.app.common.custinterface.CallBack;
import com.pm360.cepm360.app.common.custinterface.CommonListInterface;
import com.pm360.cepm360.app.common.custinterface.FloatingMenuInterface;
import com.pm360.cepm360.app.common.custinterface.OperationMode;
import com.pm360.cepm360.app.common.custinterface.OptionMenuInterface;
import com.pm360.cepm360.app.common.custinterface.ServiceInterface;
import com.pm360.cepm360.app.common.custinterface.SimpleListInterface;
import com.pm360.cepm360.app.common.custinterface.SimpleServiceInterface;
import com.pm360.cepm360.app.common.view.OptionsMenuView.SubMenuListener;
import com.pm360.cepm360.app.common.view.parent.FlowApprovalDialog;
import com.pm360.cepm360.app.common.view.parent.FlowApprovalDialog.FlowApprovalManager;
import com.pm360.cepm360.app.common.view.parent.list.BaseListCommon;
import com.pm360.cepm360.app.common.view.parent.list.ExpanableListWithOptionMenu;
import com.pm360.cepm360.app.common.view.parent.list.PermissionManager;
import com.pm360.cepm360.app.common.view.parent.list.TreeTwoExpanableListOptionMenu;
import com.pm360.cepm360.app.module.email.ComposeActivity;
import com.pm360.cepm360.app.module.email.MailboxViewFragment;
import com.pm360.cepm360.app.utils.UtilTools;
import com.pm360.cepm360.common.GLOBAL;
import com.pm360.cepm360.common.ResultStatus;
import com.pm360.cepm360.common.json.AnalysisManager;
import com.pm360.cepm360.common.util.MiscUtils;
import com.pm360.cepm360.entity.Contract_change;
import com.pm360.cepm360.entity.EPS;
import com.pm360.cepm360.entity.Expandable;
import com.pm360.cepm360.entity.Flow_approval;
import com.pm360.cepm360.entity.Flow_setting;
import com.pm360.cepm360.entity.MailBox;
import com.pm360.cepm360.entity.Message;
import com.pm360.cepm360.entity.Project;
import com.pm360.cepm360.entity.TreeTwoBean;
import com.pm360.cepm360.entity.User;
import com.pm360.cepm360.services.common.RemoteCommonService;
import com.pm360.cepm360.services.expenses.RemoteChangeContractService;
import com.pm360.cepm360.services.project.RemoteProjectService;
import com.pm360.cepm360.services.system.RemoteEPSService;
import com.pm360.cepm360.services.system.RemoteFlowSettingService;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * 合同变更申请
 * @author yuanlu
 *
 */
public class ContractChangeApplyFragment extends Fragment {
	
	public static final int STATUS_UNSPECIFIED = 0;
	public static final int STATUS_COOPERATION = 2;
	public static final int STATUS_UNSUBMIT = 3;
	public static final int STATUS_INNER_APPLY = 4;
	public static final int STATUS_INNER_PASS = 5;
	public static final int STATUS_INNER_REJECT = 6;
	
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
	
	// 权限管理
	PermissionManager mPermissionManager;
	
	// 流程申请
	private FlowApprovalDialog mFlowApprovalDialog;
	private Flow_setting mFlowSetting;
	private FlowApprovalManager mFlowApprovalManager;
	private Flow_approval mFlowApproval;
	
	// 广播接收器
	private BroadcastReceiver mBroadcastReceiver;
	
	/**
	 * 在Fragment被创建是调用
	 */
	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {
		
		// 父类初始化
		super.onCreateView(inflater, container, savedInstanceState);
		
		mBroadcastReceiver = new BroadcastReceiver() {
			
			@Override
			public void onReceive(Context context, Intent intent) {
				if (intent.getAction().equals(ContractChangeAttributeActivity
									.ACTION_UPDATE_CONTRACT_CHANGE)) {
					Contract_change contractChange = (Contract_change) intent
							.getSerializableExtra(ContractChangeAttributeActivity.CONTRACT_CHANGE_KEY);
					
					// 更新合同
					List<Contract_change> contracts = mTreeList.getListAdapter().getDataList();
					for (int i = 0; i < contracts.size(); i++) {
						if (contractChange.getId() == contracts.get(i).getId()) {
							MiscUtils.clone(contracts.get(i), contractChange);
							mTreeList.getListAdapter().notifyDataSetChanged();
							break;
						}
					}
				}
			}
		};
		
		IntentFilter intentFilter = new IntentFilter(ContractChangeAttributeActivity
									.ACTION_UPDATE_CONTRACT_CHANGE);
		getActivity().registerReceiver(mBroadcastReceiver, intentFilter);
		
		// 初始化实现部分数据
		return prepareEnvironment();
	}
	
	@Override
	public void onDestroy() {
		super.onDestroy();
		getActivity().unregisterReceiver(mBroadcastReceiver);
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
			
			@Override
			public void switchOptionMenu(View view) {
				int status = mCurrentItem.getStatus();
				
				// 未提交
				switch (status) {
					case STATUS_UNSPECIFIED:
					case STATUS_UNSUBMIT: // 未提交
						if (!mTreeList.getCurrentTreeNode().isHas_child()) {
							if (mPermissionManager.hasEditPermission()) {
								setOptionMenuInterface(noSubmitOptionMenuInterface);
							} else {
								setOptionMenuInterface(noSubmitNoEditOptionMenuInterface);
							}
						} else {
							if (mPermissionManager.hasEditPermission()) {
								setOptionMenuInterface(rejectOptionMenuInterface);
							} else {
								setOptionMenuInterface(noSubmitNoEditOptionMenuInterface);
							}
						}
						break;
					case STATUS_INNER_APPLY:	// 已经提交
						setOptionMenuInterface(hasSubmitMenuInterface);
						break;
					case STATUS_INNER_REJECT:	// 内部审批拒绝
						if (mPermissionManager.hasEditPermission()) {
							setOptionMenuInterface(rejectOptionMenuInterface);
						} else {
							setOptionMenuInterface(hasSubmitMenuInterface);
						}
						break;
					case STATUS_INNER_PASS:		// 内部审批通过
						if (mPermissionManager.hasEditPermission()) {
							setOptionMenuInterface(hasApprovalOptionMenuInterface);
						} else {
							setOptionMenuInterface(hasSubmitMenuInterface);
						}
						break;
					case STATUS_COOPERATION: // 已经发起协作
						if (mPermissionManager.hasEditPermission()) {
							setOptionMenuInterface(hasCooperationOptionMenuInterface);
						} else {
							setOptionMenuInterface(hasSubmitMenuInterface);
						}
						break;
					default:
						break;
				}
				
				super.switchOptionMenu(view);
			}
			
			@Override
			protected void loadData() {
				super.loadData();
				
				// 查询合同变更审批详情
				RemoteFlowSettingService.getInstance().getFlowDetail(
						new DataManagerInterface() {
							
							@Override
							public void getDataOnResult(ResultStatus status, List<?> list) {
								if (status.getCode() == AnalysisManager.SUCCESS_DB_QUERY) {
									if (!list.isEmpty()) {
										mFlowSetting = (Flow_setting) list.get(0);
										
										// 初始化审批流程对话框
										prepareFlowApproval();
									}
								} else {
									sendMessage(SHOW_TOAST, status.getMessage());
								}
							}
						}, UserCache.getTenantId(), GLOBAL.FLOW_TYPE[3][0]);
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
				protected void switchClickShow() {
					super.switchClickShow();
					
					// 这里切换选项菜单
					if (mSimpleTreeForDir.getCurrentItem().isHas_child()) {
						mListWithOptionMenu.setOptionMenuInterface(noSubmitOptionMenuInterface2);
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
		
		// 初始化权限
		mTreeList.setPermission(GLOBAL.SYS_ACTION[29][0], 
								GLOBAL.SYS_ACTION[28][0], 
								PermissionManager.PERMISSION_TYPE_SYS);
		
		// 获取权限管理器
		mPermissionManager = mTreeList.getList().getPermissionManager();
		
		// 处理参数
		handleArguments();
		
		mTreeList.getList().setForceEnableOptionMenu(true);
		
		// 初始化列表
		mTreeList.initList( Contract_change.class, 
							listCommonListInterface,
							listServiceInterface, 
							null,
							noSubmitOptionMenuInterface2, 
							floatingMenuInterface);
				
		// 初始化树
		mTreeList.initTree( TreeTwoBean.class,
							treeListInterface, 
							treeServiceInterface);
		
		// 初始化审批回调接口
		initFlowApprovalManager();
		
		// 返回树、列表根视图
		return mTreeList.getRootView();
	}
	
	/**
	 * 处理参数
	 */
	private void handleArguments() {
		Bundle bundle = getArguments();
		if (bundle.containsKey(GLOBAL.MSG_OBJECT_KEY)) {
			Message message = (Message) bundle.getSerializable(GLOBAL.MSG_OBJECT_KEY);
			mTreeList.getList().sendEmptyMessageDelayed(BaseListCommon.SHOW_PROGRESS_DIALOG);
			mListService.getChangeContractIDByFlowApprovalID(new DataManagerInterface() {
				
				@Override
				public void getDataOnResult(ResultStatus status, List<?> list) {
					if (status.getCode() == AnalysisManager.SUCCESS_DB_QUERY) {
						if (!list.isEmpty()) {
							
							// 再次请求服务器
							mListService.getChangeContractDetail(new DataManagerInterface() {
								
								@Override
								public void getDataOnResult(ResultStatus status, List<?> list) {
									mTreeList.getList().sendMessage(BaseListCommon.DISMISS_PROGRESS_DIALOG);
									if (status.getCode() == AnalysisManager.SUCCESS_DB_QUERY) {
										if (!list.isEmpty()) {
											mTreeList.getList().setSpecifiedItem((Contract_change) list.get(0));
										}
									}
								}
							}, ((Flow_approval) list.get(0)).getType_id(), UserCache.getTenantId());
						}  else {
							mTreeList.getList().sendMessage(BaseListCommon.DISMISS_PROGRESS_DIALOG);
							mTreeList.getList().sendMessage(BaseListCommon.SHOW_TOAST, 
									getString(R.string.cannot_query_specified_record));
						}
					}  else {
						mTreeList.getList().sendMessage(BaseListCommon.DISMISS_PROGRESS_DIALOG);
						mTreeList.getList().sendMessage(BaseListCommon.SHOW_TOAST, 
								status.getMessage());
					}
				}
			}, message.getType_id());
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
				mTreeList.getCurrentListItem().setStatus(Integer
						.parseInt(GLOBAL.CONTRACT_CHANGE_STATUS[5][0]));
				listServiceInterface.updateItem(mTreeList.getCurrentListItem());
			}
			
			@Override
			public void passFlowData2Server() {
				mTreeList.getCurrentListItem().setStatus(Integer
						.parseInt(GLOBAL.CONTRACT_CHANGE_STATUS[4][0]));
				listServiceInterface.updateItem(mTreeList.getCurrentListItem());
			}
		};
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
			return fieldSwitchMap;
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
					"sender",	// 发起方
					"receiver",		// 接收方
					"apply_date",		// 申请日期
					"bqbgk",		// 本期变更款
					"status",		// 状态
					"contact_record" 		// 往来记录
			};
		}

		@Override
		public int getListHeaderLayoutId() {
			return R.layout.contract_change_apply_list_item_layout;
		}

		@Override
		public int getListLayoutId() {
			return R.layout.contract_change_apply_list_item_layout;
		}

		@Override
		public int getListHeaderNames() {
			return R.array.contract_change_apply_list_header_names;
		}

		@Override
		public int getListHeaderIds() {
			return R.array.contract_change_apply_list_header_ids;
		}

		@Override
		public int getListItemIds() {
			return R.array.contract_change_apply_list_header_ids;
		}
	};
	
	/**
	 * 列表服务接口实现
	 */
	ServiceInterface<Contract_change> listServiceInterface 
								= new ServiceInterface<Contract_change>() {

		@Override
		public void getListData() {
			mListService.getChangeContractList2(new DataManagerInterface() {
				
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
			t.setProject_id(mTreeList.getCurrentTreeNode().getRealId());
			mListService.addContractChange(mTreeList.getListServiceManager(), t);
		}

		@Override
		public void deleteItem(Contract_change t) {
			mListService.deleteChangeContract(mTreeList.getListServiceManager(), t.getId());
		}

		@Override
		public void updateItem(Contract_change t) {
			mListService.updateChangeContract(mTreeList.getListServiceManager(), t);
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
	 * 选项菜单接口实现，新增变更尚未提交过
	 */
	private OptionMenuInterface noSubmitOptionMenuInterface = new OptionMenuInterface() {
		
		@Override
		public int getOptionMenuNames() {
			return R.array.contract_change_option_menu_names;
		}
		
		@Override
		public SubMenuListener getOptionMenuClickListener() {
			return new SubMenuListener() {
				
				@Override
				public void onSubMenuClick(View view) {
					mTreeList.getList().getOptionsMenu().dismiss();
					
					switch ((int) view.getTag()) {
						case 0:		// 详情
						case 1:		// 清单
						case 2:		// 附件
							startAttributeActivity((int) view.getTag());
							break;
						case 3:		// 删除
							mTreeList.getList().commonConfirmDelete();
							break;
						case 4:		// 提交
							submit();
							break;
						default:
							break;
					}
					
				}
			};
		}
	};
	
	/**
	 * 选项菜单接口实现，新增变更尚未提交过，但没有编辑权限
	 */
	private OptionMenuInterface noSubmitNoEditOptionMenuInterface = new OptionMenuInterface() {
		
		@Override
		public int getOptionMenuNames() {
			return R.array.contract_change_base_option_menu_names;
		}
		
		@Override
		public SubMenuListener getOptionMenuClickListener() {
			return new SubMenuListener() {
				
				@Override
				public void onSubMenuClick(View view) {
					mTreeList.getList().getOptionsMenu().dismiss();
					
					switch ((int) view.getTag()) {
						case 0:		// 详情
						case 1:		// 清单
						case 2:		// 附件
							startAttributeActivity((int) view.getTag());
							break;
						default:
							break;
					}
					
				}
			};
		}
	};
	
	/**
	 * 选项菜单接口实现，已经提交过，但内部审批被驳回后尚未提交
	 */
	private OptionMenuInterface rejectOptionMenuInterface = new OptionMenuInterface() {
		
		@Override
		public int getOptionMenuNames() {
			return R.array.contract_change_no_delete_option_menu_names;
		}
		
		@Override
		public SubMenuListener getOptionMenuClickListener() {
			return new SubMenuListener() {
				
				@Override
				public void onSubMenuClick(View view) {
					mTreeList.getList().getOptionsMenu().dismiss();
					
					switch ((int) view.getTag()) {
						case 0:		// 详情
						case 1:		// 清单
						case 2:		// 附件
							startAttributeActivity((int) view.getTag());
							break;
						case 3:		// 提交
							submit();
							break;
						default:
							break;
					}
					
				}
			};
		}
	};
	
	/*
	 * 提交变更
	 */
	private void submit() {
		BaseListCommon.showAlertDialog(getActivity(), 
				getString(R.string.confirm_submit), 
				new DialogInterface.OnClickListener() {
			
			@Override
			public void onClick(DialogInterface dialog, int which) {
				if (mFlowSetting.getStatus() == Integer.parseInt(GLOBAL.FLOW_STATUS[0][0])) {
					mTreeList.getCurrentListItem().setStatus(Integer
							.parseInt(GLOBAL.CONTRACT_CHANGE_STATUS[3][0]));
					Flow_approval flowApproval = new Flow_approval();
					flowApproval.setCurrent_level(mFlowSetting.getLevel1());
					flowApproval.setFlow_type(GLOBAL.FLOW_TYPE[3][0]);
					flowApproval.setNext_level(mFlowSetting.getLevel2());
					flowApproval.setStatus(Integer.parseInt(GLOBAL.FLOW_APPROVAL_STATUS[1][0]));
					flowApproval.setSubmit_time(new Date());
					flowApproval.setSubmiter(UserCache.getCurrentUserId());
					flowApproval.setTenant_id(UserCache.getTenantId());
					flowApproval.setType_id(mTreeList.getCurrentListItem().getId());
					mListService.updateChangeContractForSubmit(mTreeList.getListServiceManager(), 
									mTreeList.getCurrentListItem(), flowApproval);
				} else {
					mTreeList.getCurrentListItem().setStatus(Integer
							.parseInt(GLOBAL.CONTRACT_CHANGE_STATUS[4][0]));
					mListService.updateChangeContract(mTreeList.getListServiceManager(), 
									mTreeList.getCurrentListItem());
				}
			}
		});
	}
	
	/**
	 * 选项菜单接口实现，本次变更已经生效
	 */
	private OptionMenuInterface noSubmitOptionMenuInterface2 = new OptionMenuInterface() {
		
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
	
	/**
	 * 选项菜单接口实现，内部已经审批通过
	 */
	private OptionMenuInterface hasApprovalOptionMenuInterface = new OptionMenuInterface() {
		
		@Override
		public int getOptionMenuNames() {
			return R.array.contract_change_option_menu2_names;
		}
		
		@Override
		public SubMenuListener getOptionMenuClickListener() {
			return new SubMenuListener() {
				
				@Override
				public void onSubMenuClick(View view) {
					mTreeList.getList().getOptionsMenu().dismiss();
					switch ((int) view.getTag()) {
						case 0:	// 详情
						case 1:	// 清单
						case 2: // 附件
							startAttributeActivity((int) view.getTag());
							break;
						case 3:	// 生效
							takeEffect();
							break;
						case 4:	// 协作
							startCooperation();
							break;
						default:
							break;
					}
				}
			};
		}
	};
	
	/**
	 * 选项菜单接口实现，已经发起协作
	 */
	private OptionMenuInterface hasCooperationOptionMenuInterface = new OptionMenuInterface() {
		
		@Override
		public int getOptionMenuNames() {
			return R.array.contract_change_has_cooperation_option_menu_names;
		}
		
		@Override
		public SubMenuListener getOptionMenuClickListener() {
			return new SubMenuListener() {
				
				@Override
				public void onSubMenuClick(View view) {
					mTreeList.getList().getOptionsMenu().dismiss();
					switch ((int) view.getTag()) {
						case 0:	// 详情
						case 1:	// 清单
						case 2: // 附件
							startAttributeActivity((int) view.getTag());
							break;
						case 3:	// 生效
							takeEffect();
							break;
						default:
							break;
					}
				}
			};
		}
	};
	
	/**
	 * 选项菜单接口实现，已提交
	 */
	private OptionMenuInterface hasSubmitMenuInterface = new OptionMenuInterface() {
		
		@Override
		public int getOptionMenuNames() {
			return R.array.contract_change_has_submit_option_menu_names;
		}
		
		@Override
		public SubMenuListener getOptionMenuClickListener() {
			return new SubMenuListener() {
				
				@Override
				public void onSubMenuClick(View view) {
					mTreeList.getList().getOptionsMenu().dismiss();
					switch ((int) view.getTag()) {
						case 0:	// 详情
						case 1:	// 清单
						case 2: // 附件
							startAttributeActivity((int) view.getTag());
							break;
						case 3:	// 审批
							approval();
							break;
						default:
							break;
					}
				}
			};
		}
	};
	
	
	
	/*
	 * 启动变更属性Activity
	 */
	private void startAttributeActivity(int index) {
		Intent intent = new Intent(getActivity(), ContractChangeAttributeActivity.class);
		intent.putExtra(ContractChangeAttributeActivity.CONTRACT_CHANGE_KEY,
							mTreeList.getCurrentListItem());
		intent.putExtra(ContractChangeAttributeActivity.IS_CHANGE_APPLY_KEY, true);
		intent.putExtra(ContractChangeAttributeActivity.TAB_INDEX_KEY, index);
		
		if (mPermissionManager.hasEditPermission()) {
			intent.putExtra(ContractChangeAttributeActivity.IS_MODIFY_KEY, true);
		}
		intent.putExtra(ContractChangeAttributeActivity.FLOW_SETTING_KEY, mFlowSetting);
		
		if (mPermissionManager.hasEditPermission()) {
			startActivityForResult(intent, ContractActivity.CHANGE_CONTRACT_UPDATE_REQUEST_CODE);
		} else {
			startActivity(intent);
		}
	}
	
	/*
	 * 审批
	 */
	private void approval() {
		if (mFlowApprovalDialog != null) {
			mFlowApproval.setType_id(mTreeList.getCurrentListItem().getId());
			mFlowApprovalDialog.setFlowApproval(mFlowApproval);
			mFlowApprovalDialog.show(mTreeList.getCurrentListItem().getStatus() 
					== Integer.parseInt(GLOBAL.CONTRACT_CHANGE_STATUS[3][0])); // 内部审批中
		}
	}
	
	/**
	 * 准备流程审批
	 */
	private void prepareFlowApproval() {
		if (mFlowApproval == null) {
			mFlowApproval = new Flow_approval();
		}
		mFlowApproval.setFlow_type(GLOBAL.FLOW_TYPE[3][0]);
		
		if (mFlowApprovalDialog == null) {
			mFlowApprovalDialog = new FlowApprovalDialog(getActivity(),
					mFlowApproval, mFlowSetting, mFlowApprovalManager);
		}
	}
	
	/*
	 * 生效变更
	 */
	private void takeEffect() {
		BaseListCommon.showAlertDialog(getActivity(), 
				getString(R.string.confirm_take_effect), 
				new DialogInterface.OnClickListener() {
			
			@Override
			public void onClick(DialogInterface dialog, int which) {
				mTreeList.getCurrentListItem().setStatus(Integer
						.parseInt(GLOBAL.CONTRACT_CHANGE_STATUS[0][0]));
				mTreeList.getCurrentListItem().setPass_date(new Date());
				mListService.updateChangeContract(new DataManagerInterface() {
					
					@Override
					public void getDataOnResult(ResultStatus status, List<?> list) {
						switch (status.getCode()) {
							case AnalysisManager.SUCCESS_DB_UPDATE:
								
								// 通知合同变更已生效一个变更
								Intent intent = new Intent(ContractActivity.ACTION_CONTRACT_CHANGE_TAKE_EFFECT);
								intent.putExtra(ContractActivity.CONTRACT_CHANGE_TAKE_EFFECT, 
										mTreeList.getCurrentListItem());
								getActivity().sendBroadcast(intent);
								
								mTreeList.getListAdapter().deleteData(mTreeList.getCurrentListItem());
								mTreeList.getTreeAdapter().updateTreeNodesCount(mTreeList
										.getCurrentListItem().getProject_id(), -1);
								break;
							case AnalysisManager.EXCEPTION_DB_UPDATE:
								mTreeList.getList().sendMessage(BaseListCommon.SHOW_TOAST, 
										status.getMessage());
								break;
							default:
								break;
						}
					}
				}, mTreeList.getCurrentListItem());
			}
		});
	}
	
	/*
	 * 发起协作
	 */
	private void startCooperation() {
		final Contract_change contractChange = mTreeList.getCurrentListItem();
		final Intent intent2 = new Intent(getActivity(), 
						ComposeActivity.class);
   	 	intent2.putExtra(ComposeActivity.EMAIL_OPERATION_KEY, 
   	 					ComposeActivity.OPERATION_NEW);
   	 	intent2.putExtra(ComposeActivity.EMAIL_CONTRACT_CHANGE_KEY, 
   	 					contractChange);
   	 	intent2.putExtra(ComposeActivity.EMAIL_PROJECT_KEY, 
   	 					contractChange.getProject_id());
   	 	CooperationCache.getContact(contractChange.getProject_id(), 
	 				contractChange.getReceiver(), 
	 				contractChange.getReceive_contact(),
	 				new CallBack<Void, User>() {
					
					@Override
					public Void callBack(User a) {
						if (a != null) {
							ArrayList<User> users = new ArrayList<User>();
							users.add(a);
							users.add(UserCache.getUserById(contractChange.getSender_contact() + ""));
							intent2.putExtra(ComposeActivity.EMAIL_WRITE_TO_KEY, users);
					   	 	startActivityForResult(intent2, 
					   	 			ContractChangeAddActivity.COOPERATION_REQUEST_CODE);
						} else {
							mTreeList.getList().sendMessage(BaseListCommon.SHOW_TOAST, 
									getString(R.string.contract_confirm_selected_cooperation_window));
						}
						return null;
					}
				});
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
							Intent intent = new Intent(getActivity(), ContractChangeAddActivity.class);
							
							if (mTreeList.getTree().getCurrentItem() != null) {
								TreeTwoBean tree = mTreeList.getTree().getCurrentItem();
								Project project = ProjectCache
														.findProjectById(tree.getRealId());
								intent.putExtra(ContractChangeAddActivity.PROJECT_KEY, project);
								intent.putExtra(ContractChangeAddActivity.INCOME_CONTRACT_KEY, true);
								intent.putExtra(ContractChangeAddActivity.FLOW_SETTING_KEY, mFlowSetting);
								
								// 启动AddContractActivity
								startActivityForResult(intent, 
										ContractActivity.CHANGE_CONTRACT_ADD_REQUEST_CODE);
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
				case ContractActivity.CHANGE_CONTRACT_ADD_REQUEST_CODE:
					if (data != null) {
						Contract_change Contract_change = (Contract_change) data
									.getSerializableExtra(ContractChangeAddActivity.RESULT_KEY);
						
						int detailFail = data.getIntExtra(ContractChangeAddActivity.DETAIL_FAILED_KEY, 0);
						int attachmentFial = data.getIntExtra(ContractChangeAddActivity.ATTACHMENT_FAILED_KEY, 0);
						
						String parentName = mTreeList.getList().getParentName(Contract_change);
						mTreeList.getList().getListAdapter().addData(parentName, Contract_change);
						mTreeList.getList().sendMessage(BaseListCommon.SHOW_TOAST, 
									getToastString(detailFail, 0, attachmentFial));
						mTreeList.getTreeAdapter().updateTreeNodesCount(1);
					}
					break;
				case ContractActivity.CHANGE_CONTRACT_UPDATE_REQUEST_CODE:
					if (data != null) {
						Contract_change contractChange = (Contract_change) data
								.getSerializableExtra(ContractChangeAttributeActivity.CONTRACT_CHANGE_KEY);
						
						// 更新合同
						List<Contract_change> contracts = mTreeList.getListAdapter().getDataList();
						for (int i = 0; i < contracts.size(); i++) {
							if (contractChange.getId() == contracts.get(i).getId()) {
								MiscUtils.clone(contracts.get(i), contractChange);
								mTreeList.getListAdapter().notifyDataSetChanged();
								break;
							}
						}
					}
					break;
				case ContractChangeAddActivity.COOPERATION_REQUEST_CODE:
					
					// 获取协作产生的邮件对象，用以保存到合同变更中
					MailBox mailBox = (MailBox) data
								.getSerializableExtra(ComposeActivity.MAILBOX_KEY);
					
					if (mailBox != null) {
						mTreeList.getList().sendMessage(BaseListCommon.SHOW_TOAST, 
								getActivity().getString(R.string.email_send_success));
						mTreeList.getCurrentListItem().setStatus(Integer
								.parseInt(GLOBAL.CONTRACT_CHANGE_STATUS[1][0]));
						mListService.updateChangeContract(mTreeList.getListServiceManager(),
								mTreeList.getCurrentListItem());
						
					} else {
						mTreeList.getList().sendMessage(BaseListCommon.SHOW_TOAST, 
								getActivity().getString(R.string.email_send_failed));
					}
					break;
				case FlowApprovalDialog.REQUEST_COUNTER:
					if (mFlowApprovalDialog != null) {
						mFlowApprovalDialog.handleUserSelectResult(requestCode, resultCode, data);
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
			sb.append(R.string.contract_change_add_success);
		}
		
		return sb.toString();
	}
	
	
}
