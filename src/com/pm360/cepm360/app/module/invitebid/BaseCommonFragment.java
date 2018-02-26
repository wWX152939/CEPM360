package com.pm360.cepm360.app.module.invitebid;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout.LayoutParams;

import com.pm360.cepm360.R;
import com.pm360.cepm360.app.cache.ProjectCache;
import com.pm360.cepm360.app.common.activity.ListSelectActivity;
import com.pm360.cepm360.app.common.custinterface.OptionMenuInterface;
import com.pm360.cepm360.app.common.view.OptionsMenuView.SubMenuListener;
import com.pm360.cepm360.app.module.common.attachment.AttachmentFragment;
import com.pm360.cepm360.common.ResultStatus;
import com.pm360.cepm360.common.json.AnalysisManager;
import com.pm360.cepm360.entity.AttachCell;
import com.pm360.cepm360.entity.ZB_flow;

import java.util.List;

public abstract class BaseCommonFragment<T extends AttachCell> extends AttachmentFragment<T, ZB_flow> {
    private static final int BASE_REQUEST_CODE = 100;
    /* 邀标  来往单位请求码   */
    protected static final int INVITE_LWDW_COMPANY_REQUEST = BASE_REQUEST_CODE + 1;
    
    /* 投标 来往单位请求码   */
    protected static final int BID_INVITE_COMPANY_REQUEST = BASE_REQUEST_CODE + 2;
    
    /* 投标  来往单位请求码   */
    protected static final int BID_LWDW_COMPANY_REQUEST = BASE_REQUEST_CODE + 3;  
    
    /* 评分  专家请求码   */
    protected static final int SCORE_MAVIN_REQUEST = BASE_REQUEST_CODE + 4;  
    
    /* 预审 专家请求码   */
    protected static final int PRETRIAL_MAVIN_REQUEST = BASE_REQUEST_CODE + 5;  

    /* 专家库 专家请求码   */
    protected static final int MAVINLIB_MAVIN_REQUEST = BASE_REQUEST_CODE + 6;  
    
    public static final int RADIO = 10;
	
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {
		initMsgData();
		setCurrentProject(ProjectCache.getCurrentProject());
		return super.onCreateView(inflater, container, savedInstanceState);
	}
	
    private void initMsgData() {
    	Bundle data = getArguments();
    	if (data.get(ListSelectActivity.PARENT_BEAN_KEY) != null) {
    		mParentBean = (ZB_flow) data.get(ListSelectActivity.PARENT_BEAN_KEY);
    	}
    	if (data.get(CommonFragment.CURRENT_BEAN_KEY) != null) {
    		mParentBean = (ZB_flow) data.get(CommonFragment.CURRENT_BEAN_KEY);
    	}
		
    }
	
	@Override
	protected void initExtraEvent() {
		super.initExtraEvent();
		if (disableFloatingMenu()) {
			getFloatingMenu().setVisibility(View.GONE);
		} else {
			if (mFloatingMenu != null) {
				LayoutParams params = (LayoutParams) mFloatingMenu.getLayoutParams();
				params.rightMargin = getActivity().getResources().getDimensionPixelOffset(R.dimen.dp16_w);
				mFloatingMenu.setLayoutParams(params);
			}
		}
	}
	
	@Override
	public void handleParentEvent(ZB_flow b) {
		super.handleParentEvent(b);
		if (mParentBean != null) {
			mZBFlowNumber = InviteBidDataCache.getInstance().getPlanIdCache().get(mParentBean.getZb_plan_id());
		}
	}
	
	protected int getRatio() {
		return mZBFlowNumber * RADIO;
	}
	
	protected abstract boolean disableFloatingMenu();
	
	protected OptionMenuInterface mUpdateOptionMenuImplement = new OptionMenuInterface() {

		@Override
		public int getOptionMenuNames() {
			return R.array.update;
		}

		@Override
		public SubMenuListener getOptionMenuClickListener() {
			SubMenuListener listener = new SubMenuListener() {

				@Override
				public void onSubMenuClick(View view) {
					mOptionsMenu.dismiss();
					switch ((Integer) view.getTag()) {
						case 0:		// 修改
							mIsAddOperation = false;
							showUpdateDialog(true);
							break;
					}
				}
				
			};
			return listener;
		}
		
	};
	
	@Override
	protected void doExtraGetServerData(ResultStatus status, List<?> list) {
		super.doExtraGetServerData(status, list);
		switch (status.getCode()) {
		case AnalysisManager.SUCCESS_DB_ADD:
			handleParentEvent(mParentBean);
			break;
		case AnalysisManager.SUCCESS_DB_UPDATE:
			handleParentEvent(mParentBean);
			break;
		}
	}
}

