package com.pm360.cepm360.app.module.schedule;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.pm360.cepm360.app.common.custinterface.DocumentServiceInterface;
import com.pm360.cepm360.app.module.common.attachment.AttachmentFragment;
import com.pm360.cepm360.app.module.common.plan.CommonWorkLogFragment;
import com.pm360.cepm360.entity.Task;
import com.pm360.cepm360.entity.WorkLog;
import com.pm360.cepm360.services.plan.RemoteWorkLogService;

import java.io.File;

public class WorkLogFragment extends CommonWorkLogFragment<WorkLog, Task> {

	private RemoteWorkLogService mService;
	/**
	 * 创建视图
	 */
	@Override
	public View onCreateView(final LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {
		mService = RemoteWorkLogService.getInstance();
		View view = super.onCreateView(inflater, container, savedInstanceState);
		
		return view;
	}
	
	private DocumentServiceInterface<WorkLog, File> mRequestInterface = new DocumentServiceInterface<WorkLog, File>() {

		@Override
		public void getListData() {
			if (checkParentBeanForQuery()) {
				mService.getWorkLogs(getServiceManager(), mParentBean.getId());
			}
			
		}

		@Override
		public void addItem(WorkLog t) {
			if (mParentBean != null) {
				t.setTask_id(mParentBean.getTask_id());
				t.setProject_id(t.getProject_id());
				mService.addWorkLog(getServiceManager(), t);
			}
		}

		@Override
		public void deleteItem(WorkLog t) {
			mService.deleteWorkLog(getServiceManager(), t.getId());
		}

		@Override
		public void updateItem(WorkLog t) {
			mService.updateWorkLog(getServiceManager(), t);
		}

		@Override
		public void uploadDocument(WorkLog t, File b) {
			// TODO Auto-generated method stub
			
		}

	};

	@Override
	protected int getDocumentType() {
		return AttachmentFragment.JH_WORKLOG_TYPE;
	}
	
	@Override
	protected int getType() {
		return 2;
	}

	@Override
	public DocumentServiceInterface<WorkLog, File> getServiceInterface() {
		return mRequestInterface;
	}

	@Override
	public WorkLog getWorkLogBean() {
		return new WorkLog();
	}
	
}

