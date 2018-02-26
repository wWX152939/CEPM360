package com.pm360.cepm360.app.module.combination;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.pm360.cepm360.app.cache.UserCache;
import com.pm360.cepm360.app.common.custinterface.ServiceInterface;
import com.pm360.cepm360.app.module.common.attachment.AttachmentFragment;
import com.pm360.cepm360.app.module.common.plan.CommonWorkLogFragment;
import com.pm360.cepm360.entity.ZH_group_task;
import com.pm360.cepm360.entity.ZH_group_worklog;
import com.pm360.cepm360.services.group.RemoteWorkLogService;

public class WorkLogFragment extends CommonWorkLogFragment<ZH_group_worklog, ZH_group_task> {

	private RemoteWorkLogService mService;
	/**
	 * 创建视图
	 */
	@Override
	public View onCreateView(final LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {
		mService = RemoteWorkLogService.getInstance();
		return super.onCreateView(inflater, container, savedInstanceState);
	}
	
	private ServiceInterface<ZH_group_worklog> mRequestInterface = new ServiceInterface<ZH_group_worklog>() {

		@Override
		public void getListData() {
			if (mParentBean != null) {
				mService.getWorkLogs(getServiceManager(), mParentBean.getTask_id());
			}
			
		}

		@Override
		public void addItem(ZH_group_worklog t) {
			if (mParentBean != null) {
				t.setTask_id(mParentBean.getTask_id());
				t.setZh_group_id(mParentBean.getZh_group_id());
				t.setCreater(UserCache.getCurrentUserId());
				mService.addWorkLog(getServiceManager(), t);
			}
		}

		@Override
		public void deleteItem(ZH_group_worklog t) {
			mService.deleteWorkLog(getServiceManager(), t.getZh_group_worklog_id());
		}

		@Override
		public void updateItem(ZH_group_worklog t) {
			mService.updateWorkLog(getServiceManager(), t);
		}

	};

	@Override
	protected int getDocumentType() {
		// TODO Auto-generated method stub
		return AttachmentFragment.ZH_WORKLOG_TYPE;
	}
	
	@Override
	protected int getType() {
		return 1;
	}

	@Override
	public ZH_group_worklog getWorkLogBean() {
		return new ZH_group_worklog();
	}

	@Override
	public ServiceInterface<ZH_group_worklog> getServiceInterface() {
		return mRequestInterface;
	}
	
}

