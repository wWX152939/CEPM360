package com.pm360.cepm360.app.module.combination;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;

import com.pm360.cepm360.R;
import com.pm360.cepm360.app.common.activity.DataManagerInterface;
import com.pm360.cepm360.app.common.activity.ListSelectActivity;
import com.pm360.cepm360.app.common.custinterface.CommonListInterface;
import com.pm360.cepm360.app.common.custinterface.FloatingMenuInterface;
import com.pm360.cepm360.app.common.custinterface.OptionMenuInterface;
import com.pm360.cepm360.app.common.custinterface.ServiceInterface;
import com.pm360.cepm360.app.common.view.OptionsMenuView.SubMenuListener;
import com.pm360.cepm360.app.module.common.attachment.AttachmentReadOnlyFragment;
import com.pm360.cepm360.common.GLOBAL;
import com.pm360.cepm360.common.ResultStatus;
import com.pm360.cepm360.common.json.AnalysisManager;
import com.pm360.cepm360.entity.Experience;
import com.pm360.cepm360.entity.TaskCell;
import com.pm360.cepm360.entity.ZH_group_task;
import com.pm360.cepm360.entity.Zh_fkzb;
import com.pm360.cepm360.services.group.RemoteRiskKPIService;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public class RiskControlFragment extends AttachmentReadOnlyFragment<Zh_fkzb, TaskCell> {
	private final int RISK_CONTROL_SELECT_CODE = 1;
	
	private RemoteRiskKPIService mService;
	
	/**
	 * 创建视图
	 */
	@Override
	public View onCreateView( LayoutInflater inflater,
							  ViewGroup container,
							  Bundle savedInstanceState) {
		// 初始化类型和接口
		init( Zh_fkzb.class, 
			  mListInterface, 
			  mRequestInterface,
			  floatingMenuInterface,
			  mOptionMenuInterface,
			  null);
		initMsgData();
		setPermissionIdentity(GLOBAL.SYS_ACTION[35][0], GLOBAL.SYS_ACTION[34][0]);
		mService = RemoteRiskKPIService.getInstance();
		
		return super.onCreateView(inflater, container, savedInstanceState);
	}
	
	private void initMsgData() {
		Bundle data = getArguments();
    	if (data != null && data.get(ListSelectActivity.PARENT_BEAN_KEY) != null) {
    		mParentBean = (ZH_group_task) data.get(ListSelectActivity.PARENT_BEAN_KEY);
    	}
	}
	
	/**
	 * 列表接口，该接口实现的内容比较杂，主要用来提供资源
	 */
	CommonListInterface<Zh_fkzb> mListInterface 
						= new CommonListInterface<Zh_fkzb>() {
		@Override
		public int getListItemId(Zh_fkzb t) {
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
	ServiceInterface<Zh_fkzb> mRequestInterface 
								= new ServiceInterface<Zh_fkzb>() {

		@Override
		public void getListData() {
			if (checkParentBeanForQuery()) {
				mService.getRiskKPIList(getServiceManager(), mParentBean.getId());
			}
		}

		@Override
		public void addItem(Zh_fkzb t) {
			
		}

		@Override
		public void deleteItem(Zh_fkzb t) {
			mService.deleteRiskKPI(getServiceManager(), t.getId());
		}

		@Override
		public void updateItem(Zh_fkzb t) {
			
		}
	};
	
	OptionMenuInterface mOptionMenuInterface = new OptionMenuInterface() {

		@Override
		public int getOptionMenuNames() {
			return R.array.delete;
		}

		@Override
		public SubMenuListener getOptionMenuClickListener() {
			return new SubMenuListener() {
				
				@Override
				public void onSubMenuClick(View view) {
					mOptionsMenu.dismiss();
					switch ((Integer) view.getTag()) {
						case 0:		// 删除
							commonConfirmDelete();
							break;
					}
				}
			};
		}
	};	
	
	FloatingMenuInterface floatingMenuInterface = new FloatingMenuInterface() {

		@Override
		public int[] getFloatingMenuImages() {
			return new int[] { R.drawable.icn_add };
		}

		@Override
		public String[] getFloatingMenuTips() {
			return new String[] { getActivity().getResources().getString(
					R.string.add) };
		}

		@Override
		public OnItemClickListener getFloatingMenuListener() {
			OnItemClickListener mOnItemClickListener = new OnItemClickListener() {

				@Override
				public void onItemClick(AdapterView<?> arg0, View arg1,
						int arg2, long arg3) {
					switch (arg2) {
					case 0:
						mFloatingMenu.dismiss();
	                    Intent intent = new Intent();
	                    intent.setClass(getActivity(), ListSelectActivity.class);
	                    intent.putExtra(ListSelectActivity.FRAGMENT_KEY, com.pm360.cepm360.app.module.resource.RiskControlFragment.class);
	                    intent.putExtra(ListSelectActivity.SELECT_MODE_KEY, ListSelectActivity.MULTI_SELECT);
	                    if (mListAdapter.getDataShowList() != null && !mListAdapter.getDataShowList().isEmpty()) {
	                    	intent.putExtra(ListSelectActivity.FILTER_DATA_KEY, (Serializable)fkzb2Experience(mListAdapter.getDataShowList()));
	                    }
	                    
	                    startActivityForResult(intent, RISK_CONTROL_SELECT_CODE);						
						break;
					default:
						break;
					}
				}

			};
			return mOnItemClickListener;
		}

	};
	
	private List<Experience> fkzb2Experience(List<Zh_fkzb> tList) {
		List<Experience> zHfkzbList = new ArrayList<Experience>();
		for (Zh_fkzb t : tList) {
			zHfkzbList.add(fkzb2Experience(t));
		}
		return zHfkzbList;
	}
	
	private Experience fkzb2Experience(Zh_fkzb t) {
		Experience zHfkzb = new Experience();
		zHfkzb.setTenant_id(t.getTenant_id());
		zHfkzb.setContent(t.getContent());
		zHfkzb.setId(t.getExperience_id());
		zHfkzb.setStep(t.getStep());
		zHfkzb.setTitle(t.getTitle());
		zHfkzb.setAttachment(t.getAttachment());
		return zHfkzb;
	}
	
	private List<Zh_fkzb> Experience2fkzb(List<Experience> tList) {
		List<Zh_fkzb> zHfkzbList = new ArrayList<Zh_fkzb>();
		for (Experience t : tList) {
			zHfkzbList.add(Experience2fkzb(t));
		}
		return zHfkzbList;
	}
	
	private Zh_fkzb Experience2fkzb(Experience t) {
		Zh_fkzb zHfkzb = new Zh_fkzb();
		zHfkzb.setTenant_id(t.getTenant_id());
		zHfkzb.setContent(t.getContent());
		zHfkzb.setExperience_id(t.getId());
		zHfkzb.setStep(t.getStep());
		zHfkzb.setTitle(t.getTitle());
		zHfkzb.setTask_id(mParentBean.getId());
		zHfkzb.setAttachment(t.getAttachment());
		return zHfkzb;
	}

	@Override
	protected int getDocumentType() {
		return Integer.parseInt(GLOBAL.FILE_TYPE[28][0]);
	}

	@Override
	protected int getAttachPosition() {
		return 4;
	}

	public void onActivityResult(int requestCode, int resultCode, Intent data) {
		super.onActivityResult(requestCode, resultCode, data);
		if (resultCode != Activity.RESULT_OK)
			return;
		if (requestCode == RISK_CONTROL_SELECT_CODE) {
			@SuppressWarnings("unchecked")
			List<Experience> tList = (List<Experience>) data.getSerializableExtra(ListSelectActivity.RESULT_KEY);
			
			sendMessage(SHOW_PROGRESS_DIALOG);
			mService.addRiskKPI(mRiskControlManager, Experience2fkzb(tList));
		}
	}
	

	private DataManagerInterface mRiskControlManager = new DataManagerInterface() {

		@Override
		public void getDataOnResult(ResultStatus status, List<?> list) {
			sendMessage(DISMISS_PROGRESS_DIALOG);
			sendMessage(SHOW_TOAST, status.getMessage());
			if (status.getCode() == AnalysisManager.SUCCESS_DB_ADD) {
				mRequestInterface.getListData();
			}
		}
	};
	
	@Override
	protected void initExtraEvent() {
		if (mCurrentMode != OperationMode.NORMAL) {
			
		} else {
			super.initExtraEvent();
		}
	}
	
}
