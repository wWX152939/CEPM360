package com.pm360.cepm360.app.module.schedule;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.view.animation.TranslateAnimation;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.LinearLayout;

import com.pm360.cepm360.R;
import com.pm360.cepm360.app.cache.ObsCache;
import com.pm360.cepm360.app.cache.ProjectCache;
import com.pm360.cepm360.app.cache.TenantCache;
import com.pm360.cepm360.app.cache.UserCache;
import com.pm360.cepm360.app.common.activity.DataManagerInterface;
import com.pm360.cepm360.app.common.custinterface.CommonListInterface;
import com.pm360.cepm360.app.common.custinterface.FloatingMenuInterface;
import com.pm360.cepm360.app.common.custinterface.OptionMenuInterface;
import com.pm360.cepm360.app.common.custinterface.ServiceInterface;
import com.pm360.cepm360.app.common.custinterface.SimpleListInterface;
import com.pm360.cepm360.app.common.custinterface.SimplePageChangeListenerInterface;
import com.pm360.cepm360.app.common.custinterface.SimpleServiceInterface;
import com.pm360.cepm360.app.common.view.OptionsMenuView.SubMenuListener;
import com.pm360.cepm360.app.common.view.parent.BaseViewPager;
import com.pm360.cepm360.app.common.view.parent.CooperationSelectUserDialog;
import com.pm360.cepm360.app.common.view.parent.list.BaseListCommon;
import com.pm360.cepm360.app.common.view.parent.list.ListWithOptionMenu;
import com.pm360.cepm360.app.common.view.parent.list.PermissionManager;
import com.pm360.cepm360.app.common.view.parent.list.SimpleDirListOptionMenu;
import com.pm360.cepm360.app.module.combination.SubFeedbackFragment;
import com.pm360.cepm360.app.module.combination.WorkLogFragment;
import com.pm360.cepm360.app.module.common.plan.CommonDocumentFragment;
import com.pm360.cepm360.common.GLOBAL;
import com.pm360.cepm360.common.ResultStatus;
import com.pm360.cepm360.common.json.AnalysisManager;
import com.pm360.cepm360.common.util.LogUtil;
import com.pm360.cepm360.entity.Cooperation;
import com.pm360.cepm360.entity.FeedbackCell;
import com.pm360.cepm360.entity.Project;
import com.pm360.cepm360.entity.ShareTask;
import com.pm360.cepm360.entity.Tenant;
import com.pm360.cepm360.entity.ZH_group;
import com.pm360.cepm360.entity.ZH_group_feedback;
import com.pm360.cepm360.entity.ZH_group_task;
import com.pm360.cepm360.services.common.RemoteCommonService;
import com.pm360.cepm360.services.group.RemoteShareTaskService;


public class TaskSharedWithNodeFragment extends Fragment {
	
	// 返回码
	public static final int RET_MULTI_SELECT_TASK = 201;
	public static final int RET_SELECT_TASK = 202;
	
	// 数据库数据
	private final String[] mTitleString = new String[] { "name", "plan_duration",
			"start_time", "end_time", "status", "actual_duration", "actual_start_time",
			"actual_end_time", "progress", "owner", "department", "type", "out_status"};
	private final String[] mTitleString2 = new String[] { "name", "plan_duration",
			"start_time", "end_time", "status", "actual_duration", "actual_start_time",
			"actual_end_time", "progress", "owner", "type" };
	
	private CombinationScheduleActivity mActivity;	
	private BaseViewPager mBaseViewPager;
	private BaseViewPager mBaseViewPager2;
	private View mFirstView;
	private View mSecondView;
	
    private SimpleDirListOptionMenu<Cooperation, ShareTask> mDirList1;
    private SimpleDirListOptionMenu<Cooperation, ShareTask> mDirList2;
    private final int TYPE_SHARED = 1;
    private final int TYPE_MOUNT = 2;
    
    private RemoteCommonService mTreeService = RemoteCommonService.getInstance();
    private RemoteShareTaskService mContentService = RemoteShareTaskService.getInstance();
    
	/**
     * 在Fragment被创建是调用
     */
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
            Bundle savedInstanceState) {
    	mActivity = (CombinationScheduleActivity) getActivity();
        // 父类初始化
        super.onCreateView(inflater, container, savedInstanceState);
        
        // 初始化实现部分数据
        View view = inflater.inflate(R.layout.base_activity, null);
        LinearLayout parent = (LinearLayout) view.findViewById(R.id.parent_id);
        
        mFirstView = inflater.inflate(R.layout.base_view_pager, null);
        mSecondView = inflater.inflate(R.layout.base_view_pager, null);
        mBaseViewPager = new BaseViewPager(getActivity(), mFirstView);
        List<View> viewList = new ArrayList<View>();
        View subView1 = prepareEnvironment1();
        View subView2 = prepareEnvironment2();
        viewList.add(subView1);
        viewList.add(subView2);
        mBaseViewPager.init(R.array.task_shared_titles, viewList, null);
        
        parent.addView(mFirstView);
        mSecondView.setVisibility(View.GONE);
        
        parent.addView(mSecondView);
        
        return parent;
    }
    
    /**
     * 实例化时初始化
     */
    private View prepareEnvironment1() {
        
        ListWithOptionMenu<ShareTask> listWithOptionMenu = new ListWithOptionMenu<ShareTask>(getActivity()) {
            @Override
            protected void displayFieldRemap(Map<String, String> displayFieldMap,
            		ShareTask p, int position) {
                super.displayFieldRemap(displayFieldMap, p, position);
            }
        };
        
        // 树、列表对象
        mDirList1 = new SimpleDirListOptionMenu<Cooperation, ShareTask>(getActivity(), 
                getActivity().getResources().getString(R.string.combination_cooperation),
                listWithOptionMenu) {

                    @Override
                    public int getTreeListRelevanceId(ShareTask c) {
                        return c.getIn_company();
                    }

					@Override
					public int getDirNodeItemId(Cooperation t) {
						return t.getCooperation_id();
					}         
					
					@Override
					public Map<Cooperation, List<ShareTask>> getDirListMap(Map<Cooperation, List<ShareTask>> listMap) {
						if (listMap.isEmpty()) {
							if (!mDirList1.getTreeAdapter().getDataList().isEmpty()
									&& !mDirList1.getListShowList().isEmpty()) {
								for (Cooperation cooperation : mDirList1.getTreeAdapter().getDataList()) {
									List<ShareTask> listTask = new ArrayList<ShareTask>();
									for (ShareTask task : mDirList1.getListShowList()) {
										// TODO
										if (task.getIn_company() == cooperation.getTenant_id()) {
											listTask.add(task);
										}
									}
									listMap.put(cooperation, listTask);
								}
							}
						}
						return listMap;
					} 
        };
        
        // 初始化权限
        mDirList1.setPermission(GLOBAL.SYS_ACTION[9][0], 
                                GLOBAL.SYS_ACTION[9][0], 
                                PermissionManager.PERMISSION_TYPE_SYS);
        
        handleArguments();
        
        // 初始化列表
        mDirList1.getList().init( ShareTask.class, 
                            listCommonListInterface,
                            listServiceInterface,
                            null, optionMenuInterface, floatingMenuInterface);
        
        // 初始化树
        mDirList1.initTree( Cooperation.class,
	                      treeListInterface, 
	                      treeServiceInterface);              
        // 返回树、列表根视图
        return mDirList1.getRootView();
    }
    
    private Map<String, Map<String, String>> getSwitchMap() {
    	Map<String, Map<String, String>> displayFieldMap = new HashMap<String, Map<String, String>>();
    	displayFieldMap.put("owner", UserCache.getUserMaps());
		displayFieldMap.put("department", ObsCache.getObsIdMaps());
		
		Map<String, String> statusMap = new HashMap<String, String>();
		for (int i = 0; i < GLOBAL.TASK_STATUS_TYPE.length; i++) {
			statusMap.put(GLOBAL.TASK_STATUS_TYPE[i][0], GLOBAL.TASK_STATUS_TYPE[i][1]);
		}
		displayFieldMap.put("status", statusMap);
		
		Map<String, String> typeMap = new HashMap<String, String>();
		typeMap.put(GLOBAL.TASK_TYPE_MILE_KEY, GLOBAL.TASK_TYPE_MILE_VALUE);
		typeMap.put(GLOBAL.TASK_TYPE_TASK_KEY, GLOBAL.TASK_TYPE_TASK_VALUE);
		typeMap.put(GLOBAL.TASK_TYPE_WBS_KEY, GLOBAL.TASK_TYPE_WBS_VALUE);
		displayFieldMap.put("type", typeMap);

		Map<String, String> progressMap = new HashMap<String, String>();
		for (int i = 1; i <= 100; i++) {
			progressMap.put(i + "", i + "%");
		}
		displayFieldMap.put("progress", progressMap);
		
		Map<String, String> sharedStatusMap = new HashMap<String, String>();
		for (int i = 0; i < GLOBAL.SHARE_TASK_STATUS.length; i++) {
			sharedStatusMap.put(GLOBAL.SHARE_TASK_STATUS[i][0], GLOBAL.SHARE_TASK_STATUS[i][1]);
		}
		displayFieldMap.put("out_status", sharedStatusMap);
        return displayFieldMap;
    }
    
    /**------------------------ 列表相关定义 ----------------------*/
    /**
     * 列表接口实现
     */
    CommonListInterface<ShareTask> listCommonListInterface = new CommonListInterface<ShareTask>() {

        @Override
        public Map<String, Map<String, String>> getDisplayFieldsSwitchMap() {
            return getSwitchMap();
        }

        @Override
        public int getListItemId(ShareTask t) {
            return t.getTask_id();
        }

        @Override
        public String[] getDisplayFeilds() {
            return mTitleString;
        }

        @Override
        public int getListHeaderLayoutId() {
            return R.layout.task_shared_list_item;
        }

        @Override
        public int getListLayoutId() {
            return R.layout.task_shared_list_item;
        }

        @Override
        public int getListHeaderNames() {
            return R.array.task_shared_names;
        }

        @Override
        public int getListHeaderIds() {
            return R.array.task_shared_ids;
        }

        @Override
        public int getListItemIds() {
            return R.array.task_shared_ids;
        }
        
    };
    
    /**
     * 列表接口实现
     */
    CommonListInterface<ShareTask> listCommonListInterface2 = new CommonListInterface<ShareTask>() {

        @Override
        public Map<String, Map<String, String>> getDisplayFieldsSwitchMap() {
            return getSwitchMap();
        }

        @Override
        public int getListItemId(ShareTask t) {
            return t.getTask_id();
        }

        @Override
        public String[] getDisplayFeilds() {
            return mTitleString2;
        }

        @Override
        public int getListHeaderLayoutId() {
            return R.layout.task_mount_list_item;
        }

        @Override
        public int getListLayoutId() {
            return R.layout.task_mount_list_item;
        }

        @Override
        public int getListHeaderNames() {
            return R.array.task_mount_names;
        }

        @Override
        public int getListHeaderIds() {
            return R.array.task_mount_ids;
        }

        @Override
        public int getListItemIds() {
            return R.array.task_mount_ids;
        }
        
    };
    
    /**
     * 列表服务接口实现
     */
    ServiceInterface<ShareTask> listServiceInterface = new ServiceInterface<ShareTask>() {

        @Override
        public void getListData() {
        	ZH_group group = mActivity.getGroupData();
        	mContentService.getShareTaskList(mDirList1.getListServiceManager(), group.getZh_group_id(), group.getTenant_id(), 0);
        }

        @Override
        public void addItem(ShareTask t) {
        }

        @Override
        public void deleteItem(final ShareTask t) {
        	LogUtil.i("wzw id:" + t.getId());
        	if (t.getOut_status() == Integer.parseInt(GLOBAL.SHARE_TASK_STATUS[1][0])) {
        		mContentService.deleteShareTask(new DataManagerInterface() {
					
					@Override
					public void getDataOnResult(ResultStatus status, List<?> list) {
						t.setOut_status(Integer.parseInt(GLOBAL.SHARE_TASK_STATUS[4][0]));
						mDirList1.getList().getListAdapter().notifyDataSetChanged();
						mDirList1.getList().sendMessage(BaseListCommon.SHOW_TOAST, status.getMessage());
					}
				}, t.getId() + "");
        	} else if (t.getOut_status() == Integer.parseInt(GLOBAL.SHARE_TASK_STATUS[0][0])) {
        		mContentService.deleteUNShareTask(mDirList1.getListServiceManager(), t.getId() + "");
        	}
        	
        }

        @Override
        public void updateItem(ShareTask t) {            
        }        
    };
    
    /**
     * 列表服务接口实现
     */
    ServiceInterface<ShareTask> listServiceInterface2 = new ServiceInterface<ShareTask>() {

        @Override
        public void getListData() {
        	ZH_group group = mActivity.getGroupData();
        	mContentService.getShareTaskList(mDirList2.getListServiceManager(), group.getZh_group_id(), group.getTenant_id(), 1);
        }

        @Override
        public void addItem(ShareTask t) {
        }

        @Override
        public void deleteItem(ShareTask t) {
        }

        @Override
        public void updateItem(ShareTask t) {            
        }        
    };
    
    /**
	 * 浮动菜单接口实现
	 */
	private FloatingMenuInterface floatingMenuInterface = new FloatingMenuInterface() {
		
		@Override
		public String[] getFloatingMenuTips() {
			return new String[] {
					getActivity().getResources().getString(R.string.add),
					getActivity().getResources().getString(R.string.publish)
			};
		}
		
		@Override
		public OnItemClickListener getFloatingMenuListener() {
			return new OnItemClickListener() {

				@Override
				public void onItemClick(AdapterView<?> parent, View view,
						int position, long id) {
					mDirList1.getList().getFloatingMenu().dismiss();
					if (mDirList1.getCurrentTreeNode() == null) {
						mDirList1.getList().sendMessage(BaseListCommon.SHOW_TOAST,
								getString(R.string.pls_select_obs));
						return;
					}
					switch (position) {
						case 0:
							
							Intent intent = new Intent(getActivity(), PlanMakeSelectActivity.class);
							if (mDirList1.getTree().getCurrentItem() != null) {
								intent.putExtra(PlanMakeSelectActivity.NODE_CODE, mActivity.getGroupData().getZh_group_id());
								intent.putExtra(PlanMakeSelectActivity.MULTI_MODE_CODE, true);
								startActivityForResult(intent, 
										RET_MULTI_SELECT_TASK);
							}
							break;
						case 1:
							
							boolean shareFlag = false;
							// 为空 或者都是已共享
							for (ShareTask task : mDirList1.getListAdapter().getDataShowList()) {
								if (task.getOut_status() != Integer.parseInt(GLOBAL.SHARE_TASK_STATUS[1][0])) {
									shareFlag = true;
									break;
								}
							}
							if (!shareFlag) {
								mDirList1.getList().sendMessage(BaseListCommon.SHOW_TOAST,
										getString(R.string.task_is_empty));
								break;
							}
							// 判断有删除未共享或者已共享的在此为第二次共享不需要发消息
							boolean secondPublish = false;
							for (ShareTask task : mDirList1.getListAdapter().getDataShowList()) {
								if (task.getOut_status() == Integer.parseInt(GLOBAL.SHARE_TASK_STATUS[4][0])
										|| task.getOut_status() == Integer.parseInt(GLOBAL.SHARE_TASK_STATUS[1][0])) {
									secondPublish = true;
									break;
								}
							}
							if (secondPublish) {
								ShareTask sTask = mDirList1.getListAdapter().getDataShowList().get(0);
								sTask.setOut_status(Integer.parseInt(GLOBAL.SHARE_TASK_STATUS[1][0]));
								sTask.setOut_project_name(ProjectCache.getProjectIdMaps().get(sTask.getOut_project_id() + ""));
								mContentService.shareTask(new DataManagerInterface() {
									
									@Override
									public void getDataOnResult(ResultStatus status, List<?> list) {
										mDirList1.getList().sendMessage(BaseListCommon.SHOW_TOAST, status.getMessage());
										listServiceInterface.getListData();
									}
								}, sTask);
							} else {
								final CooperationSelectUserDialog userDialog = new CooperationSelectUserDialog(getActivity(), mDirList1.getCurrentTreeNode());
								userDialog.show();
								userDialog.setButton(getString(R.string.confirm), new OnClickListener() {
									
									@Override
									public void onClick(View v) {
										ShareTask sTask = mDirList1.getListAdapter().getDataShowList().get(0);
										if (userDialog.getCurrentItem() == null) {
											mDirList1.getList().sendMessage(BaseListCommon.SHOW_TOAST,
													getString(R.string.pls_select) + getString(R.string.owner));
											return;
										}
										sTask.setAccept_person(userDialog.getCurrentItem().getUser_id());
										sTask.setOut_project_name(ProjectCache.getProjectIdMaps().get(sTask.getOut_project_id() + ""));
										mContentService.shareTask(new DataManagerInterface() {
											
											@Override
											public void getDataOnResult(ResultStatus status, List<?> list) {
												mDirList1.getList().sendMessage(BaseListCommon.SHOW_TOAST, status.getMessage());
												if (status.getCode() == AnalysisManager.SUCCESS_DB_UPDATE) {
													userDialog.dismiss();
												}
												listServiceInterface.getListData();
											}
										}, sTask);						
									}
								});
								
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
					R.drawable.icn_add,
					R.drawable.icn_publish
			};
		}
	};
	
	/**
	 * 浮动菜单接口实现
	 */
	private FloatingMenuInterface floatingMenuInterface2 = new FloatingMenuInterface() {
		
		@Override
		public String[] getFloatingMenuTips() {
			return new String[] {
					getActivity().getResources().getString(R.string.mount)
			};
		}
		
		@Override
		public OnItemClickListener getFloatingMenuListener() {
			return new OnItemClickListener() {

				@Override
				public void onItemClick(AdapterView<?> parent, View view,
						int position, long id) {
					mDirList2.getList().getFloatingMenu().dismiss();
					switch (position) {
						case 0:	// 挂载
							if (mDirList2.getCurrentTreeNode() == null) {
								mDirList2.getList().sendMessage(BaseListCommon.SHOW_TOAST, getString(R.string.pls_select_obs));
								return;
							}
							if (mDirList2.getListAdapter().getDataShowList() == null || 
									mDirList2.getListAdapter().getDataShowList().isEmpty()) {
								mDirList2.getList().sendMessage(BaseListCommon.SHOW_TOAST,
										getString(R.string.task_is_empty));
								return;
							}
							Intent intent = new Intent(getActivity(), PlanMakeSelectActivity.class);
							intent.putExtra(PlanMakeSelectActivity.NODE_CODE, mActivity.getGroupData().getZh_group_id());
							startActivityForResult(intent, 
									RET_SELECT_TASK);
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
					R.drawable.icon_gantt
			};
		}
	};
	
	private void optionMenuInfo(final int type) {
		mBaseViewPager2 = new BaseViewPager(getActivity(), mSecondView);
        final List<Fragment> fragmentList = new ArrayList<Fragment>();
		SubFeedbackFragment feedbackFragment = new SubFeedbackFragment();
		feedbackFragment.setIndexMode(true);
    	fragmentList.add(feedbackFragment);
    	fragmentList.add(new CommonDocumentFragment<ZH_group_task>(CommonDocumentFragment.TYPE_SCHEDULE_FEEDBACK_DOC));
    	fragmentList.add(new WorkLogFragment());
    	fragmentList.add(new SafetyFragment<ZH_group_task>());
    	fragmentList.add(new QualityFragment<ZH_group_task>());
    	
    	mBaseViewPager2.initFragments(getResources().getStringArray(R.array.task_feedback_tab_names), fragmentList, new SimplePageChangeListenerInterface() {
			
			@Override
			public Bundle[] getBundleData() {
				Bundle[] bundleArray = new Bundle[fragmentList.size()];
				Bundle bundle = new Bundle();
				bundle.putString("permission", "0_0, 2_4");
				ZH_group group = new ZH_group();
				if (type == TYPE_SHARED) {
					group.setZh_group_id(mDirList1.getCurrentListItem().getZh_group_id());
				} else {
					group.setZh_group_id(mDirList2.getCurrentListItem().getZh_group_id());
				}
				
				bundle.putSerializable("groupData", group);
				for (int i = 0; i < fragmentList.size(); i++) {
					bundleArray[i] = bundle;
				}
				
				return bundleArray;
			}
		} );

		ShareTask task;
		Project project = new Project();
		if (type == TYPE_SHARED) {
			task = mDirList1.getCurrentListItem();
		} else {
			task = mDirList2.getCurrentListItem();
		}
		project.setProject_id(task.getOut_project_id());
		project.setName(task.getOut_project_name());
		project.setTenant_id(task.getOut_company());
		mBaseViewPager2.setCurrentParentBean(switchToGroupTask(task));
    	mBaseViewPager2.setChildProject(project);
    	
    	List<FeedbackCell> feedbackList = new ArrayList<>();
		//有反馈信息，加入
    	ZH_group_feedback feedback = new ZH_group_feedback();
    	
		feedbackList.add(feedback);
    	
    	mBaseViewPager2.setCurrentList(feedbackList);
    	
    	
    	final TranslateAnimation showAction = new TranslateAnimation(Animation.RELATIVE_TO_SELF, 1.0f,
				Animation.RELATIVE_TO_SELF, 0.0f, Animation.RELATIVE_TO_SELF,
				0.0f, Animation.RELATIVE_TO_SELF, 0.0f);
		showAction.setDuration(500);
		mSecondView.setVisibility(View.VISIBLE);
		mSecondView.startAnimation(showAction);
		View view = mSecondView.findViewById(R.id.base_title_left);
    	view.setVisibility(View.VISIBLE);
    	mFirstView.setVisibility(View.GONE);
    	
		final TranslateAnimation hiddenAction = new TranslateAnimation(Animation.RELATIVE_TO_SELF,
				0.0f, Animation.RELATIVE_TO_SELF, 1.0f,
				Animation.RELATIVE_TO_SELF, 0.0f, Animation.RELATIVE_TO_SELF,
				0.0f);
		hiddenAction.setDuration(500);
		view.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View v) {
				mSecondView.setVisibility(View.GONE);
				mSecondView.startAnimation(hiddenAction);
				mFirstView.setVisibility(View.VISIBLE);
			}
		});
	}
	
	/**
	 * 选项菜单单击处理
	 * @param view
	 */
	private void handleOptionMenuClick(View view) {
		mDirList1.getList().getOptionsMenu().dismiss();
		
		switch ((int) view.getTag()) {
			case 0: // 详情 
				optionMenuInfo(TYPE_SHARED);
				break;
			case 1:	// 删除
				mDirList1.getList().commonConfirmDelete();
				break;
			default:
				break;
		}
	}
	
	/**
	 * 选项菜单单击处理
	 * @param view
	 */
	private void handleOptionMenuClick2(View view) {
		mDirList2.getList().getOptionsMenu().dismiss();
		
		switch ((int) view.getTag()) {
			case 0: // 详情 
				optionMenuInfo(TYPE_MOUNT);
				break;
			
			default:
				break;
		}
	}
	
	/**
	 * 选项菜单接口实现
	 */
	private OptionMenuInterface optionMenuInterface = new OptionMenuInterface() {
		
		@Override
		public int getOptionMenuNames() {
			return R.array.announcement_pub_option_menu_names;
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
	
	/*
	**
	 * 选项菜单接口实现
	 */
	private OptionMenuInterface optionMenuInterface2 = new OptionMenuInterface() {
		
		@Override
		public int getOptionMenuNames() {
			return R.array.purchase_manager_detail;
		}
		
		@Override
		public SubMenuListener getOptionMenuClickListener() {
			return new SubMenuListener() {
				
				@Override
				public void onSubMenuClick(View view) {
					handleOptionMenuClick2(view);
				}
			};
		}
	};

    /**
     * 简单树视图接口实现
     */
    SimpleListInterface<Cooperation> treeListInterface 
                                = new SimpleListInterface<Cooperation>() {

        @Override
        public int getListItemId(Cooperation t) {
            return t.getCooperation_id();
        }

        @Override
        public String[] getDisplayFeilds() {
            return new String[] {
                    "project_name"
            };
        }
    }; 
    
    private void handleServerData(final int type) {
    	Tenant tenant = new Tenant();
        tenant.setTenant_id(UserCache.getTenantId());
    	mTreeService.getAllCooperationList(new DataManagerInterface() {
			
			@Override
			public void getDataOnResult(ResultStatus status, List<?> list) {
				if (list != null && !list.isEmpty()) {
					@SuppressWarnings("unchecked")
					List<Cooperation> coopList = (List<Cooperation>) list;
					List<Cooperation> tmpList = new ArrayList<Cooperation>();
					ZH_group group = mActivity.getGroupData();
					for (Cooperation coop : coopList) {
						if (coop.getAccept_project_id() == group.getProject_id()
								|| coop.getLaunch_project_id() == group.getProject_id()) {
							int tenantId = coop.getAccept_company();
							if (tenantId == UserCache.getTenantId()) {
								tenantId = coop.getLaunch_company();
							}
							coop.setTenant_id(tenantId);
							coop.setProject_name(TenantCache.getTenantName("" + tenantId));
							tmpList.add(coop);
						}
					}
					LogUtil.i("wzw coopList:" + coopList);
					if (type == TYPE_SHARED) {
						mDirList1.getTreeServiceManager().getDataOnResult(status, tmpList);
					} else {
						mDirList2.getTreeServiceManager().getDataOnResult(status, tmpList);
					}
				}
			}
		}, tenant);
    }
    
    /**
     * 树服务接口
     */
    SimpleServiceInterface<Cooperation> treeServiceInterface 
                            = new SimpleServiceInterface<Cooperation>() {

        @Override
        public void getListData() {
        	handleServerData(TYPE_SHARED);
        }
    };
    
    /**
     * 树服务接口
     */
    SimpleServiceInterface<Cooperation> treeServiceInterface2 
                            = new SimpleServiceInterface<Cooperation>() {

        @Override
        public void getListData() {
        	handleServerData(TYPE_MOUNT);
        }
    };
    
    private void handleArguments() {
    
    }
    
    /**
     * 实例化时初始化
     */
    private View prepareEnvironment2() {
        
        ListWithOptionMenu<ShareTask> listWithOptionMenu = new ListWithOptionMenu<ShareTask>(getActivity()) {
            @Override
            protected void displayFieldRemap(Map<String, String> displayFieldMap,
            		ShareTask p, int position) {
                super.displayFieldRemap(displayFieldMap, p, position);
            }
        };
        
        // 树、列表对象
        mDirList2 = new SimpleDirListOptionMenu<Cooperation, ShareTask>(getActivity(), 
                getActivity().getResources().getString(R.string.combination_cooperation),
                listWithOptionMenu) {

                    @Override
                    public int getTreeListRelevanceId(ShareTask c) {
                        return c.getOut_company();
                    }

					@Override
					public int getDirNodeItemId(Cooperation t) {
						return t.getCooperation_id();
					}
					
					@Override
					public Map<Cooperation, List<ShareTask>> getDirListMap(Map<Cooperation, List<ShareTask>> listMap) {
						if (listMap.isEmpty()) {
							if (!mDirList2.getTreeAdapter().getDataList().isEmpty()
									&& !mDirList2.getListShowList().isEmpty()) {
								for (Cooperation cooperation : mDirList2.getTreeAdapter().getDataList()) {
									List<ShareTask> listTask = new ArrayList<ShareTask>();
									for (ShareTask task : mDirList2.getListShowList()) {
										// TODO
										if (task.getOut_company() == cooperation.getTenant_id()) {
											listTask.add(task);
										}
									}
									listMap.put(cooperation, listTask);
								}
							}
						}
						return listMap;
					} 
        };
        
        // 初始化权限
        mDirList2.setPermission(GLOBAL.SYS_ACTION[9][0],  
                                GLOBAL.SYS_ACTION[9][0], 
                                PermissionManager.PERMISSION_TYPE_SYS);
        
        handleArguments();
        
        // 初始化列表
        mDirList2.getList().init( ShareTask.class, 
                            listCommonListInterface2,
                            listServiceInterface2,
                            null, optionMenuInterface2, floatingMenuInterface2);
        
        // 初始化树
        mDirList2.initTree( Cooperation.class,
	                      treeListInterface, 
	                      treeServiceInterface2);              
        // 返回树、列表根视图
        return mDirList2.getRootView();
    }
    
    private ShareTask switchToShareTask(ZH_group_task task, ZH_group group) {
    	ShareTask shareTask = new ShareTask();
		shareTask.setTask_id(task.getTask_id());

		shareTask.setZh_group_id(task.getZh_group_id());
		shareTask.setCode(task.getCode());
		shareTask.setName(task.getName());
		shareTask.setStart_time(task.getStart_time());
		shareTask.setEnd_time(task.getEnd_time());
		shareTask.setActual_start_time(task.getActual_start_time());
		shareTask.setActual_end_time(task.getActual_end_time());
		shareTask.setOwner(task.getOwner());
		shareTask.setType(task.getType());
		shareTask.setStatus(task.getStatus());
		shareTask.setProgress(task.getProgress());
		shareTask.setPlan_duration(task.getPlan_duration());
		shareTask.setActual_duration(task.getActual_duration());
		shareTask.setCc_user(task.getCc_user());
		shareTask.setDepartment(task.getDepartment());
		shareTask.setParents_id(task.getParents_id());
		shareTask.setCreater(task.getCreater());
		shareTask.setCreate_time(task.getCreate_time());
		shareTask.setPublish(task.getPublish());
		shareTask.setMark(task.getMark());
		shareTask.setFeedback_creater(task.getFeedback_creater());
		shareTask.setWbs_id(task.getTemplet_wbs_dir_id());
		shareTask.setHas_child(task.isHas_child());
		shareTask.setLevel(task.getLevel());
		shareTask.setExpanded(task.isExpanded());
		shareTask.setChange_id(task.getChange_id());
		shareTask.setPk(task.getPk());
		shareTask.setTenant_id(task.getTenant_id());
		shareTask.setSort(task.getSort());
		shareTask.setProject_id(task.getProject_id());
		int projectId = mDirList1.getCurrentTreeNode().getLaunch_project_id();
		if (projectId == group.getProject_id()) {
			projectId = mDirList1.getCurrentTreeNode().getAccept_project_id();
		}
		shareTask.setIn_project_id(projectId);
		shareTask.setIn_project_name(ProjectCache.getProjectIdMaps().get("" + projectId));
		shareTask.setOut_project_id(group.getProject_id());
		shareTask.setOut_project_name(ProjectCache.getProjectIdMaps().get("" + group.getProject_id()));
		int tenantId = mDirList1.getTree().getCurrentItem().getTenant_id();
		shareTask.setIn_company(tenantId);
		shareTask.setIn_company_name(TenantCache.getTenantName("" + tenantId));
		shareTask.setOut_company(task.getTenant_id());
		shareTask.setOut_company_name(TenantCache.getTenantName("" + task.getTenant_id()));
		shareTask.setOut_status(Integer.parseInt(GLOBAL.SHARE_TASK_STATUS[0][0]));
		return shareTask;
    }
    
    public static ZH_group_task switchToGroupTask(ShareTask task) {
    	ZH_group_task shareTask = new ShareTask();
		shareTask.setTask_id(task.getTask_id());

		shareTask.setZh_group_id(task.getZh_group_id());
		shareTask.setCode(task.getCode());
		shareTask.setName(task.getName());
		shareTask.setStart_time(task.getStart_time());
		shareTask.setEnd_time(task.getEnd_time());
		shareTask.setActual_start_time(task.getActual_start_time());
		shareTask.setActual_end_time(task.getActual_end_time());
		shareTask.setOwner(task.getOwner());
		shareTask.setType(task.getType());
		shareTask.setStatus(task.getStatus());
		shareTask.setProgress(task.getProgress());
		shareTask.setPlan_duration(task.getPlan_duration());
		shareTask.setActual_duration(task.getActual_duration());
		shareTask.setCc_user(task.getCc_user());
		shareTask.setDepartment(task.getDepartment());
		shareTask.setParents_id(task.getParents_id());
		shareTask.setCreater(task.getCreater());
		shareTask.setCreate_time(task.getCreate_time());
		shareTask.setPublish(task.getPublish());
		shareTask.setMark(task.getMark());
		shareTask.setFeedback_creater(task.getFeedback_creater());
		shareTask.setTemplet_wbs_dir_id(task.getTemplet_wbs_dir_id());
		shareTask.setHas_child(task.isHas_child());
		shareTask.setLevel(task.getLevel());
		shareTask.setExpanded(task.isExpanded());
		shareTask.setChange_id(task.getChange_id());
		shareTask.setPk(task.getPk());
		shareTask.setTenant_id(task.getTenant_id());
		shareTask.setSort(task.getSort());
		shareTask.setProject_id(task.getProject_id());
		return shareTask;
    }

	List<ShareTask> mShareList = new ArrayList<ShareTask>();
    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
    	super.onActivityResult(requestCode, resultCode, data);
    	if (resultCode != Activity.RESULT_OK) {
    		return;
    	}
    	if (requestCode == RET_SELECT_TASK) {
    		ZH_group_task task = (ZH_group_task) data.getSerializableExtra(PlanMakeSelectActivity.TASK_CODE);
    		if (task == null) {
				return;
			}
    		mContentService.mountShareTask(new DataManagerInterface() {
				
				@Override
				public void getDataOnResult(ResultStatus status, List<?> list) {
					mDirList2.getList().sendMessage(BaseListCommon.SHOW_TOAST, status.getMessage());
				}
			}, task.getTask_id(), mDirList2.getListAdapter().getDataShowList().get(0).getZh_group_id(), task.getProject_id(), task.getZh_group_id());
    		
    	} else if (requestCode == RET_MULTI_SELECT_TASK) {
    		@SuppressWarnings("unchecked")
			List<ZH_group_task> list = (List<ZH_group_task>) data.getSerializableExtra(PlanMakeSelectActivity.TASK_CODE);

			LogUtil.i("wzw list:" + list);
			mShareList.clear();
			if (list == null) {
				return;
			}
			ZH_group group = mActivity.getGroupData();
			LogUtil.i("wzw group:" + group);
			List<ShareTask> showList = mDirList1.getListAdapter().getDataShowList();
    		for (ZH_group_task task : list) {
    			boolean breakFlag = false;  
    			for (ShareTask showTask : showList) {
    				if (showTask.getTask_id() == task.getTask_id()) {
    					breakFlag = true;
    					break;
    				}
    			}
    			if (breakFlag) {
    				continue;
    			}
    			mShareList.add(switchToShareTask(task, group));
    		}
    		if (mShareList.isEmpty()) {
				mDirList1.getList().sendMessage(BaseListCommon.SHOW_TOAST, getString(R.string.pls_select_no_shared_task));
    			return;
    		}
    		mContentService.addShareTask(new DataManagerInterface() {
				
				@Override
				public void getDataOnResult(ResultStatus status, List<?> list) {
					LogUtil.i("wzw code:" + status.getCode());
					mDirList1.getList().sendMessage(BaseListCommon.SHOW_TOAST, status.getMessage());
					if (status.getCode() == AnalysisManager.SUCCESS_DB_ADD) {
						mDirList1.getListAdapter().addShowDataList(mShareList);
						mDirList1.getListAdapter().addDataList(mShareList);
					}
				}
			}, mShareList);
    	}
    }
}
