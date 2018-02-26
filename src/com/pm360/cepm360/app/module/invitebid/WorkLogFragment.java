package com.pm360.cepm360.app.module.invitebid;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout.LayoutParams;

import com.pm360.cepm360.R;
import com.pm360.cepm360.app.cache.ProjectCache;
import com.pm360.cepm360.app.common.custinterface.DocumentServiceInterface;
import com.pm360.cepm360.app.common.custinterface.ServiceInterface;
import com.pm360.cepm360.app.module.common.attachment.AttachmentFragment;
import com.pm360.cepm360.app.module.common.plan.CommonWorkLogFragment;
import com.pm360.cepm360.entity.ZB_flow;
import com.pm360.cepm360.entity.ZB_worklog;
import com.pm360.cepm360.services.invitebid.RemoteWorkLogService;

import java.io.File;

public class WorkLogFragment extends CommonWorkLogFragment<ZB_worklog, ZB_flow> {

	private RemoteWorkLogService mService;
	/**
	 * 创建视图
	 */
	@Override
	public View onCreateView(final LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {
		initMsgData();
		mService = RemoteWorkLogService.getInstance();
		return super.onCreateView(inflater, container, savedInstanceState);
	}
	
	private void initMsgData() {
		setCurrentProject(ProjectCache.getCurrentProject());
    }
	
	private DocumentServiceInterface<ZB_worklog, File> mRequestInterface = new DocumentServiceInterface<ZB_worklog, File>() {

		@Override
		public void getListData() {
			if (mParentBean != null) {
				mService.getWorkLogs(getServiceManager(), mParentBean.getZb_plan_id(), mParentBean.getId());
			}
			
		}

		@Override
		public void addItem(ZB_worklog t) {
			if (mParentBean != null) {
				t.setTask_id(mParentBean.getZb_plan_id());
				t.setZb_flow_id(mParentBean.getZb_flow_id());
				mService.addWorkLog(getServiceManager(), t);
			}
		}

		@Override
		public void deleteItem(ZB_worklog t) {
			mService.deleteWorkLog(getServiceManager(), t.getId());
		}

		@Override
		public void updateItem(ZB_worklog t) {
			mService.updateWorkLog(getServiceManager(), t);
		}

		@Override
		public void uploadDocument(ZB_worklog t, File b) {
			// TODO Auto-generated method stub
			
		}
	};

	@Override
	protected int getDocumentType() {
		return AttachmentFragment.ZB_WORKLOG_TYPE;
	}
	
	@Override
	protected int getType() {
		return 3;
	}

	@Override
	public ServiceInterface<ZB_worklog> getServiceInterface() {
		return mRequestInterface;
	}

	@Override
	public ZB_worklog getWorkLogBean() {
		return new ZB_worklog();
	}
	
	@Override
	protected void initExtraEvent() {
		super.initExtraEvent();
		if (mFloatingMenu != null) {
			LayoutParams params = (LayoutParams) mFloatingMenu.getLayoutParams();
			params.rightMargin = getActivity().getResources().getDimensionPixelOffset(R.dimen.dp16_w);
			mFloatingMenu.setLayoutParams(params);
		}
	}
	
	@Override
	public void handleParentEvent(ZB_flow b) {
		super.handleParentEvent(b);
		if (mParentBean != null) {
			mZBFlowNumber = InviteBidDataCache.getInstance().getPlanIdCache().get(mParentBean.getZb_plan_id());
		}
	}
}

