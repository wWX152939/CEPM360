package com.pm360.cepm360.app.module.resource;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout.LayoutParams;

import com.pm360.cepm360.R;
import com.pm360.cepm360.app.cache.UserCache;
import com.pm360.cepm360.app.common.custinterface.CommonListInterface;
import com.pm360.cepm360.app.common.custinterface.ServiceInterface;
import com.pm360.cepm360.app.common.custinterface.SimpleDialogInterface;
import com.pm360.cepm360.app.module.common.attachment.AttachmentFragment;
import com.pm360.cepm360.common.GLOBAL;
import com.pm360.cepm360.entity.Expandable;
import com.pm360.cepm360.entity.Experience;
import com.pm360.cepm360.services.system.RemoteExperienceService;

import java.util.Map;

public class RiskControlFragment extends AttachmentFragment<Experience, Expandable> {
	
	private RemoteExperienceService mService;
	
	/**
	 * 创建视图
	 */
	@Override
	public View onCreateView( LayoutInflater inflater,
							  ViewGroup container,
							  Bundle savedInstanceState) {
		// 初始化类型和接口
		init( Experience.class, 
			  mListInterface, 
			  mRequestInterface,
			  null,
			  null,
			  mDialogInterface);
		
		setPermissionIdentity(GLOBAL.SYS_ACTION[21][0], GLOBAL.SYS_ACTION[20][0]);
		mService = RemoteExperienceService.getInstance();
		
		return super.onCreateView(inflater, container, savedInstanceState);
	}
	
	/**
	 * 列表接口，该接口实现的内容比较杂，主要用来提供资源
	 */
	CommonListInterface<Experience> mListInterface 
						= new CommonListInterface<Experience>() {
		@Override
		public int getListItemId(Experience t) {
			return t.getId();
		}

		@Override
		public String[] getDisplayFeilds() {
			return new String[] {
					SERIAL_NUMBER,
					"title",
					"content",
					"step",
					"attachment"
			};
		}

		@Override
		public int getListHeaderLayoutId() {
			return R.layout.risk_control_listitem;
		}
		
		@Override
		public int getListLayoutId() {
			return getListHeaderLayoutId();
		}

		@Override
		public int getListHeaderNames() {
			return R.array.risk_control_list_header_names;
		}

		@Override
		public int getListHeaderIds() {
			return R.array.risk_control_list_header_ids;
		}
		
		@Override
		public Map<String, Map<String, String>> getDisplayFieldsSwitchMap() {
			return null;
		}

		@Override
		public int getListItemIds() {
			return getListHeaderIds();
		}
	};
	
	/**
	 * 服务请求接口实现
	 */
	ServiceInterface<Experience> mRequestInterface 
								= new ServiceInterface<Experience>() {

		@Override
		public void getListData() {
			mService.getExperienceList(getServiceManager(), 
			        UserCache.getCurrentUser().getTenant_id());
		}

		@Override
		public void addItem(Experience t) {
			t.setTenant_id(UserCache.getCurrentUser().getTenant_id());
			mService.addExperience(getServiceManager(), t);
		}

		@Override
		public void deleteItem(Experience t) {
			mService.deleteExperience(getServiceManager(), t.getId());
		}

		@Override
		public void updateItem(Experience t) {
			mService.updateExperience(getServiceManager(), t);
		}
	};
	
	/**
	 * 对话框风格，数据，映射及额外初始化接口实现
	 */
	SimpleDialogInterface mDialogInterface = new SimpleDialogInterface() {
		@Override
		public int getDialogTitleId() {
			return R.string.risk_control_dialog_title;
		}

		@Override
		public int getDialogLableNames() {
			return R.array.risk_control_dialog_lable_names;
		}
		
		@Override
		public String[] getUpdateFeilds() {
			return new String[] {
					"title",
					"content",
					"step"
			};
		}

		@Override
		public Integer[] getImportantColumns(Map<String, String> saveDataMap) {
			return null;
		}
	};

	@Override
	protected int getDocumentType() {
		return Integer.parseInt(GLOBAL.FILE_TYPE[28][0]);
	}

	@Override
	protected int getAttachPosition() {
		return 4;
	}
	
	@Override
	protected void initExtraEvent() {
		if (mFloatingMenu != null) {
			LayoutParams params = (LayoutParams) mFloatingMenu.getLayoutParams();
			params.rightMargin = getActivity().getResources().getDimensionPixelOffset(R.dimen.dp116_w);
			mFloatingMenu.setLayoutParams(params);
		}
	}

}
