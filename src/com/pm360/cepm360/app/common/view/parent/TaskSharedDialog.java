package com.pm360.cepm360.app.common.view.parent;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import android.app.Activity;

import com.pm360.cepm360.R;
import com.pm360.cepm360.app.cache.ObsCache;
import com.pm360.cepm360.app.cache.UserCache;
import com.pm360.cepm360.app.common.activity.DataManagerInterface;
import com.pm360.cepm360.app.common.custinterface.CommonListInterface;
import com.pm360.cepm360.app.common.custinterface.ServiceInterface;
import com.pm360.cepm360.app.common.view.parent.list.SimpleList;
import com.pm360.cepm360.common.GLOBAL;
import com.pm360.cepm360.common.ResultStatus;
import com.pm360.cepm360.entity.Message;
import com.pm360.cepm360.entity.ShareTask;
import com.pm360.cepm360.entity.Tenant;
import com.pm360.cepm360.entity.User;
import com.pm360.cepm360.services.common.RemoteCommonService;
import com.pm360.cepm360.services.group.RemoteShareTaskService;
import com.pm360.cepm360.services.system.RemoteUserService;

public class TaskSharedDialog extends BaseDialogStyle {
	
	private final String NAME = "name";
	private final String PLAN_DURATION = "plan_duration";
	private final String START_TIME = "start_time";
	private final String END_TIME = "end_time";
	private final String OWNER = "owner";
	private final String DEPARTMENT = "department";
	private final String TYPE = "type";
	private final String STATUS = "publish";
	
	private Message mMessage;
	private SimpleList<ShareTask> mSimpleList;
	public TaskSharedDialog(Activity activity, Message message) {
		super(activity);
		mMessage = message;
		mSimpleList = new SimpleList<>(activity);
	}
	
	public void show(int width) {
		mSimpleList.init(ShareTask.class, listCommonListInterface, listServiceInterface);
		init(mSimpleList.getRootView());
		setTitleName(mActivity.getString(R.string.share) + mActivity.getString(R.string.task));
		super.show(width, 0);
	}
	
	/**------------------------ 列表相关定义 ----------------------*/
    /**
     * 列表接口实现
     */
    CommonListInterface<ShareTask> listCommonListInterface = new CommonListInterface<ShareTask>() {

    	@Override
		public Map<String, Map<String, String>> getDisplayFieldsSwitchMap() {
			Map<String, Map<String, String>> map = new HashMap<String, Map<String, String>>();
			Map<String, String> subMap = new HashMap<String, String>();
			subMap.put(GLOBAL.TASK_TYPE_MILE_KEY, GLOBAL.TASK_TYPE_MILE_VALUE);
			subMap.put(GLOBAL.TASK_TYPE_TASK_KEY, GLOBAL.TASK_TYPE_TASK_VALUE);
			subMap.put(GLOBAL.TASK_TYPE_WBS_KEY, GLOBAL.TASK_TYPE_WBS_VALUE);
			map.put(TYPE, subMap);
			map.put(DEPARTMENT, ObsCache.getObsIdMaps());
			map.put(OWNER, UserCache.getUserMaps());
			
			Map<String, String> subMap2 = new HashMap<String, String>();
			for (int i = 0; i < GLOBAL.PUBLISH_STATUS.length; i++) {
				subMap2.put(GLOBAL.PUBLISH_STATUS[i][0], GLOBAL.PUBLISH_STATUS[i][1]);
			}
			map.put(STATUS, subMap2);
			return map;
		}

		@Override
		public int getListItemId(ShareTask t) {
			return t.getTask_id();
		}

		@Override
		public String[] getDisplayFeilds() {
			return new String[] { NAME, PLAN_DURATION, START_TIME, END_TIME,
					OWNER, DEPARTMENT, TYPE, STATUS };
		}

		@Override
		public int getListHeaderLayoutId() {
			return R.layout.task_make_title_list_item2;
		}

		@Override
		public int getListLayoutId() {
			return R.layout.task_make_list_item2;
		}

		@Override
		public int getListHeaderNames() {
			return R.array.plan_make_names;
		}

		@Override
		public int getListHeaderIds() {
			return R.array.plan_make_ids;
		}

		@Override
		public int getListItemIds() {
			return R.array.plan_make_ids;
		}
                                    
    };
    
    /**
     * 列表服务接口实现
     */
    ServiceInterface<ShareTask> listServiceInterface = new ServiceInterface<ShareTask>() {

		@Override
        public void getListData() {
        	RemoteShareTaskService.getInstance().getShareTaskList(new DataManagerInterface() {
				
				@Override
				public void getDataOnResult(ResultStatus status, List<?> list) {
					@SuppressWarnings("unchecked")
					List<ShareTask> taskList = (List<ShareTask>) list;
					final List<ShareTask> showList = new ArrayList<ShareTask>();
					if (taskList != null && !taskList.isEmpty()) {
						for (ShareTask task : taskList) {
							if (task.getIn_company() == UserCache.getCurrentUser().getTenant_id()) {
								task.setDepartment(task.getOut_company());
								showList.add(task);
							}
						}
						String company = "";
						String users = "";
						for (ShareTask task : showList) {
							company = task.getOut_company() + ",";
							users = task.getOwner() + ",";
						}
						if (company.endsWith(",")) {
							company = company.substring(0, company.length() - 1);
						}
						if (users.endsWith(",")) {
							users = users.substring(0, users.length() - 1);
						}
						final String tmpUsers = users;
						RemoteCommonService.getInstance().getTenantDetailByTenantIDs(new DataManagerInterface() {
							
							@Override
							public void getDataOnResult(ResultStatus status, List<?> list) {
								if (list != null && !list.isEmpty()) {
									for (Object tenant : list) {
										Tenant t = (Tenant) tenant;
										ObsCache.getObsIdMaps().put("" + t.getTenant_id(), t.getName());
									}
								}
								
								RemoteUserService.getInstance().getUserDetailByUserIDs(new DataManagerInterface() {
									
									@Override
									public void getDataOnResult(ResultStatus status, List<?> list) {
										if (list != null && !list.isEmpty()) {
											for (Object user : list) {
												User t = (User) user;
												UserCache.getUserMaps().put("" + t.getUser_id(), t.getName());
											}
										}
										mSimpleList.handleDataOnResult(status, showList);
									}
								}, tmpUsers);
							}
						}, company);
					}
					
				}
			}, mMessage.getType_id(), mMessage.getTask_id(), 0);
        }

        @Override
        public void addItem(ShareTask t) {
            // TODO Auto-generated method stub            
        }

        @Override
        public void deleteItem(ShareTask t) {
            // TODO Auto-generated method stub            
        }

        @Override
        public void updateItem(ShareTask t) {
            // TODO Auto-generated method stub            
        }
        
    };	
}
