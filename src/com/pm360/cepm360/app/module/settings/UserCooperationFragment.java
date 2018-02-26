package com.pm360.cepm360.app.module.settings;

import android.annotation.SuppressLint;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.pm360.cepm360.R;
import com.pm360.cepm360.app.cache.ObsCache;
import com.pm360.cepm360.app.cache.TenantCache;
import com.pm360.cepm360.app.cache.UserCache;
import com.pm360.cepm360.app.common.activity.DataManagerInterface;
import com.pm360.cepm360.app.common.activity.ListSelectActivity;
import com.pm360.cepm360.app.common.custinterface.CommonListInterface;
import com.pm360.cepm360.app.common.custinterface.OperationMode;
import com.pm360.cepm360.app.common.custinterface.SelectInterface;
import com.pm360.cepm360.app.common.custinterface.ServiceInterface;
import com.pm360.cepm360.app.common.custinterface.SimpleListInterface;
import com.pm360.cepm360.app.common.custinterface.SimpleServiceInterface;
import com.pm360.cepm360.app.common.view.parent.list.ListWithOptionMenu;
import com.pm360.cepm360.app.common.view.parent.list.PermissionManager;
import com.pm360.cepm360.app.common.view.parent.list.SimpleDirListOptionMenu;
import com.pm360.cepm360.app.module.email.ComposeActivity;
import com.pm360.cepm360.common.GLOBAL;
import com.pm360.cepm360.common.ResultStatus;
import com.pm360.cepm360.common.json.AnalysisManager;
import com.pm360.cepm360.entity.Cooperation;
import com.pm360.cepm360.entity.Project;
import com.pm360.cepm360.entity.Tenant;
import com.pm360.cepm360.entity.User;
import com.pm360.cepm360.services.common.RemoteCommonService;
import com.pm360.cepm360.services.system.RemoteUserService;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class UserCooperationFragment extends Fragment implements SelectInterface {
    
    private Project mProject;
    private SimpleDirListOptionMenu<Cooperation, User> mDirList;
    
    // 服务
    private RemoteCommonService mCooperationService = RemoteCommonService.getInstance();
    
    private List<Cooperation> mCooperations;
    
    // 协作单位联系人列表映射表
    @SuppressLint("UseSparseArrays") 
    Map<Integer, List<User>> mCooperationUsersMap = new HashMap<Integer, List<User>>();
    
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
        // 协作发起是否与项目有关
        mProject = (Project) getArguments().getSerializable(ComposeActivity.EMAIL_PROJECT_KEY);
        
        ListWithOptionMenu<User> listWithOptionMenu = new ListWithOptionMenu<User>(getActivity()) {

            @Override
            protected void loadData() {
            	loadListData();
            }
            
        };
        
        mDirList = new SimpleDirListOptionMenu<Cooperation, User>(getActivity(), 
                getActivity().getResources().getString(R.string.combination_cooperation), listWithOptionMenu ) {

                    @Override
                    public int getTreeListRelevanceId(User c) {
                        return c.getUser_id();
                    }

                    @Override
                    public int getDirNodeItemId(Cooperation t) {
                        return t.getCooperation_id();
                    }
            
        };
        
        // 初始化权限
        mDirList.setPermission(GLOBAL.SYS_ACTION[37][0], 
                                GLOBAL.SYS_ACTION[36][0], 
                                PermissionManager.PERMISSION_TYPE_SYS);

        listWithOptionMenu.setOperationMode(OperationMode.MULTI_SELECT);
        
        // 初始化列表
        mDirList.getList().init(User.class, 
                            listCommonListInterface,
                            listServiceInterface);
                
        // 初始化树
        mDirList.initTree(Cooperation.class,
                            dirListInterface, 
                            dirServiceInterface);
        
        @SuppressWarnings("unchecked")
        ArrayList<User> filterList = (ArrayList<User>) getArguments()
                .getSerializable(ListSelectActivity.FILTER_DATA_KEY);
        mDirList.setFilterDataList(filterList);
        
        mDirList.getTreeAdapter().setConvertViewBackground(false);
        mDirList.getList().enableInnerButton(false);
        
        // 返回树、列表根视图
        return mDirList.getRootView();
    }
    
    private void loadListData() {
    	final Cooperation cooperation = mDirList.getTree().getCurrentItem();
        if (cooperation != null) {
        	if (mCooperationUsersMap.containsKey(cooperation.getCooperation_id())) {
        		ResultStatus status = new ResultStatus(AnalysisManager.SUCCESS_DB_QUERY,
        				GLOBAL.DB_QUERY_SUCCESSFUL);
        		List<User> users = new ArrayList<>();
        		users.addAll(mCooperationUsersMap.get(cooperation.getCooperation_id()));
        		mDirList.getListServiceManager().getDataOnResult(status, users);
        	} else {
        		
        		// 获取该公司的所有协作项目的干系人
            	String contacts = cooperation.getAccept_contact_window();
            	if (contacts != null && !contacts.isEmpty()) {
                    RemoteUserService.getInstance().getUserDetailByUserIDs(new DataManagerInterface() {
						
						@SuppressWarnings("unchecked")
						@Override
						public void getDataOnResult(ResultStatus status, List<?> list) {
							List<User> users = new ArrayList<User>();
							users.addAll((List<User>) list);
							mDirList.getListServiceManager().getDataOnResult(status, list);
							if (status.getCode() == AnalysisManager.SUCCESS_DB_QUERY) {
								mCooperationUsersMap.put(cooperation
											.getCooperation_id(), users);
							}
						}
					}, contacts);
            	} else {
            		ResultStatus status = new ResultStatus(AnalysisManager.SUCCESS_DB_QUERY,
            				GLOBAL.DB_QUERY_SUCCESSFUL);
            		List<User> users = new ArrayList<User>();
            		mCooperationUsersMap.put(cooperation.getCooperation_id(), users);
            		mDirList.getListServiceManager().getDataOnResult(status, users);
            	}
        	}
        }
    }
    
    public List<?> getSelectedDataList() {
        return mDirList.getListAdapter().getSelectedDatas();
    }
    
    @SuppressLint("UseSparseArrays") 
    private List<Cooperation> formateCooperations(List<Cooperation> cooperations) {
    	Map<Integer, Cooperation> cooperationMap = new HashMap<Integer, Cooperation>();
    	for (int i = 0; i < cooperations.size(); i++) {
    		Cooperation temp = cooperations.get(i);
    		int cooperationTenantId = getCooperationCompany(temp);
    		String cooperationWindows = getCooperationWindows(temp);
    		
    		// 拼接相同的协作单位干系人
    		Cooperation cooperation = null;
    		if (!cooperationMap.containsKey(cooperationTenantId)) {
    			cooperation = new Cooperation();
    			cooperation.setAccept_contact_window(cooperationWindows);
    			cooperation.setCooperation_id(cooperationTenantId);
    			
    			// 加入协作单位ID到协作对象映射表
    			cooperationMap.put(cooperationTenantId, cooperation);
    		} else {
	    		cooperation = cooperationMap.get(cooperationTenantId);
	    		cooperation.setAccept_contact_window(uniqueIds(
	    				cooperation.getAccept_contact_window(), cooperationWindows));
    		}
    	}
    	
    	// 构造协作列表
    	List<Cooperation> cooperationList = new ArrayList<Cooperation>();
    	for (Map.Entry<Integer, Cooperation> entry : cooperationMap.entrySet()) {
    		cooperationList.add(entry.getValue());
    	}
    	
    	return cooperationList;
    }
    
    /**
     * 获取协作方的干系人
     * @param cooperation
     * @return
     */
    private String getCooperationWindows(Cooperation cooperation) {
    	if (cooperation.getLaunch_company() == UserCache.getTenantId()) {
    		return cooperation.getAccept_contact_window();
    	} else {
    		return cooperation.getLaunch_contact_window();
    	}
    }
    
    /**
     * 获取协作方公司ID
     * @param cooperation
     * @return
     */
    private int getCooperationCompany(Cooperation cooperation) {
    	if (cooperation.getLaunch_company() == UserCache.getTenantId()) {
    		return cooperation.getAccept_company();
    	} else {
    		return cooperation.getLaunch_company();
    	}
    }
    
    /**
     * 唯一化ID字符串
     * @param firstIds
     * @param secondIds
     * @return
     */
    private String uniqueIds(String firstIds, String secondIds) {
    	if (firstIds == null || firstIds.isEmpty()) {
    		return secondIds;
    	}
    	
    	if (secondIds == null || secondIds.isEmpty()) {
    		return firstIds;
    	}
    	
    	String[] firstArray = firstIds.split(",");
    	String[] secondeArray = secondIds.split(",");
    	
    	List<String> idList = new ArrayList<String>();
    	for (int i = 0; i < firstArray.length; i++) {
    		if (!idList.contains(firstArray[i])) {
    			idList.add(firstArray[i]);
    		}
    	}
    	
    	for (int i = 0; i < secondeArray.length; i++) {
    		if (!idList.contains(secondeArray[i])) {
    			idList.add(secondeArray[i]);
    		}
    	}
    	
    	StringBuilder sb = new StringBuilder();
    	for (int i = 0; i < idList.size(); i++) {
    		sb.append(idList.get(i) + ",");
    	}
    	
    	// 开始检查了参数，这里sb不会为空
    	return sb.substring(0, sb.length() - 1);
    }
        
    /**------------------------ 列表相关定义 ----------------------*/
    /**
     * 列表接口实现
     */
    CommonListInterface<User> listCommonListInterface = new CommonListInterface<User>() {

        @Override
        public Map<String, Map<String, String>> getDisplayFieldsSwitchMap() {
            Map<String, Map<String, String>> fieldSwitchMap = new HashMap<String, Map<String, String>>();

            fieldSwitchMap.put("obs_id", ObsCache.getObsIdMaps());
            fieldSwitchMap.put("tenant_id", TenantCache.getTenantIdMaps());

            return fieldSwitchMap;
        }

        @Override
        public int getListItemId(User t) {
            return t.getUser_id();
        }

        @Override
        public String[] getDisplayFeilds() {
            return new String[] {
                    "name",
                    "obs_id",
                    "tenant_id",
                    "login_name"
            };
        }

        @Override
        public int getListHeaderLayoutId() {
            return R.layout.user_cooperation_listitem_layout;
        }

        @Override
        public int getListLayoutId() {
            return getListHeaderLayoutId();
        }

        @Override
        public int getListHeaderNames() {
            return R.array.user_cooperation_list_header_names;
        }

        @Override
        public int getListHeaderIds() {
            return R.array.user_cooperation_list_header_ids;
        }

        @Override
        public int getListItemIds() {
            return getListHeaderIds();
        }
                                    
    };
    
    /**
     * 列表服务接口实现
     */
    ServiceInterface<User> listServiceInterface = new ServiceInterface<User>() {

        @Override
        public void getListData() {
            // TODO Auto-generated method stub
        }

        @Override
        public void addItem(User t) {
            // TODO Auto-generated method stub            
        }

        @Override
        public void deleteItem(User t) {
            // TODO Auto-generated method stub            
        }

        @Override
        public void updateItem(User t) {
            // TODO Auto-generated method stub            
        }
        
    };
    
    /**
     * 简单树视图接口实现
     */
    SimpleListInterface<Cooperation> dirListInterface = new SimpleListInterface<Cooperation>() {

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
    
    /**
     * 树服务接口
     */
    SimpleServiceInterface<Cooperation> dirServiceInterface 
    						= new SimpleServiceInterface<Cooperation>() {

        @Override
        public void getListData() {
            if (mProject != null && mProject.getProject_id() > 0) {
                // 获取基于某个项目的协作单位信息
                Tenant tenant = new Tenant();
                tenant.setTenant_id(mProject.getTenant_id());
                mCooperationService.getCooperationTenantListByProject2(new DataManagerInterface() {
					
					@Override
					public void getDataOnResult(ResultStatus status, List<?> list) {
						if (status.getCode() == AnalysisManager.SUCCESS_DB_QUERY) {
							if (!list.isEmpty()) {
								getTenantList(list);
							}
						} else {
							mDirList.getTreeServiceManager().getDataOnResult(status, 
									mCooperations);
                		}
					}
				}, tenant, mProject.getProject_id()); 
            } else {
                // 获取该公司合作过的所有公司信息
                Tenant tenant = new Tenant();
                tenant.setTenant_id(UserCache.getTenantId());
                mCooperationService.getAllCooperationList(new DataManagerInterface() {
					
					@Override
					public void getDataOnResult(ResultStatus status, List<?> list) {
						if (status.getCode() == AnalysisManager.SUCCESS_DB_QUERY) {
							if (!list.isEmpty()) {
								getTenantList(list);
							}
						} else {
							mDirList.getTreeServiceManager().getDataOnResult(status, 
									mCooperations);
                		}
					}
				}, tenant);
            }
        }
        
    };
    
    /**
     * 取得tenant名称
     * @param list
     */
    @SuppressWarnings("unchecked")
	private void getTenantList(List<?> list) {
    	mCooperations = formateCooperations((List<Cooperation>) list);
		StringBuilder sb = new StringBuilder();
		for (int i = 0; i < mCooperations.size(); i++) {
			sb.append(mCooperations.get(i).getCooperation_id() + ",");
		}
		
		RemoteCommonService.getInstance().getTenantDetailByTenantIDs(new DataManagerInterface() {
			
			@Override
			public void getDataOnResult(ResultStatus status, List<?> list) {
				if (status.getCode() == AnalysisManager.SUCCESS_DB_QUERY) {
					List<Tenant> tenants = (List<Tenant>) list;
					for (int i = 0; i < mCooperations.size(); i++) {
						for (int j = 0; j < tenants.size(); j++) {
							if (mCooperations.get(i).getCooperation_id() 
									== tenants.get(j).getTenant_id()) {
								mCooperations.get(i).setProject_name(tenants.get(j).getName());
								break;
							}
						}
					}
				}
				mDirList.getTreeServiceManager().getDataOnResult(status, mCooperations);
				if (!mCooperations.isEmpty()) {
					mDirList.setCurrentTreeNode(mDirList.getTreeAdapter().getItem(0));
					mDirList.getTreeAdapter().setSelected(0, true);
					loadListData();
				}
			}
		}, sb.substring(0, sb.length() - 1));
    }

    @Override
    public void setFilterData(List<?> filters) {
        mDirList.getList().setFilterData(filters);
    }

    @Override
    public void enableInnerButton(boolean enable) {
        //mDirList.getList().enableInnerButton(enable);    
    }
    
}
