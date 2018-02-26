package com.pm360.cepm360.app.module.invitebid;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.Toast;

import com.pm360.cepm360.R;
import com.pm360.cepm360.app.CepmApplication;
import com.pm360.cepm360.app.common.activity.DataManagerInterface;
import com.pm360.cepm360.app.common.activity.ListSelectActivity;
import com.pm360.cepm360.app.common.custinterface.CommonListInterface;
import com.pm360.cepm360.app.common.custinterface.FloatingMenuInterface;
import com.pm360.cepm360.app.common.custinterface.OptionMenuInterface;
import com.pm360.cepm360.app.common.custinterface.ServiceInterface;
import com.pm360.cepm360.app.common.view.OptionsMenuView.SubMenuListener;
import com.pm360.cepm360.common.GLOBAL;
import com.pm360.cepm360.common.ResultStatus;
import com.pm360.cepm360.common.json.AnalysisManager;
import com.pm360.cepm360.entity.Mavin_lib;
import com.pm360.cepm360.entity.ZB_mavin;
import com.pm360.cepm360.services.invitebid.RemoteZBPlanService;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class MavinFragment extends BaseCommonFragment<ZB_mavin> {

	private RemoteZBPlanService mService;
	
	Map<String, String> mSexMap = new HashMap<String, String>();
	Map<String, String> mTypeMap = new HashMap<String, String>();

	private String[] mTitleString = new String[] {
			SERIAL_NUMBER,
			"name",
			"sex",
			"birthday",
			"work_start",
			"work_company",			
			"type",
			"tel",
			"title",	
			"major",
			"degree",
			"mark",
	};
	/**
	 * 创建视图
	 */
	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {
		// 初始化类型和接口
		init(ZB_mavin.class, mListInterface, mRequestInterface, floatingMenuInterface, mOptionMenuInterface,
				null);

		setPermissionIdentity(GLOBAL.SYS_ACTION[51][0],
				GLOBAL.SYS_ACTION[50][0]);
		mService = RemoteZBPlanService.getInstance();
		mApplication = (CepmApplication) getActivity().getApplication();
		
		// 为单位做映射
		for (int i = 0; i < GLOBAL.SEX_TYPE.length; i++) {
			mSexMap.put(GLOBAL.SEX_TYPE[i][1], GLOBAL.SEX_TYPE[i][0]);
			mSexMap.put(GLOBAL.SEX_TYPE[i][0], GLOBAL.SEX_TYPE[i][1]);
		}
		
		for (int i = 0; i < GLOBAL.MAVIN_TYPE.length; i++) {
			mTypeMap.put(GLOBAL.MAVIN_TYPE[i][1], GLOBAL.MAVIN_TYPE[i][0]);
			mTypeMap.put(GLOBAL.MAVIN_TYPE[i][0], GLOBAL.MAVIN_TYPE[i][1]);
		}		

		return super.onCreateView(inflater, container, savedInstanceState);
	}
	
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
						if (mParentBean == null) {
							Toast.makeText(getActivity(), R.string.pls_select_task, Toast.LENGTH_SHORT).show();
							break;
						}
	                    Intent intent = new Intent();
	                    intent.setClass(getActivity(), ListSelectActivity.class);
	                    intent.putExtra(ListSelectActivity.FRAGMENT_KEY, com.pm360.cepm360.app.module.resource.MavinFragment.class);
	                    intent.putExtra(ListSelectActivity.PARENT_BEAN_KEY,
	                    		mParentBean);
	                    getParentFragment().startActivityForResult(intent, MAVINLIB_MAVIN_REQUEST + getRatio());						
						break;
					default:
						break;
					}
				}

			};
			return mOnItemClickListener;
		}

	};	
	
	CommonListInterface<ZB_mavin> mListInterface = new CommonListInterface<ZB_mavin>(){

		@Override
		public Map<String, Map<String, String>> getDisplayFieldsSwitchMap() {
			Map<String, Map<String, String>> sMap = new HashMap<String, Map<String, String>>();
			sMap.put("sex", mSexMap);
			sMap.put("type", mTypeMap);
			return sMap;
		}

		@Override
		public int getListItemId(ZB_mavin t) {
			return t.getZb_mavin_id();
		}

		@Override
		public String[] getDisplayFeilds() {
			return mTitleString;
		}

		@Override
		public int getListHeaderLayoutId() {
			return R.layout.invitebid_mavin_list_item;
		}

		@Override
		public int getListLayoutId() {
			return R.layout.invitebid_mavin_list_item;
		}

		@Override
		public int getListHeaderNames() {
			return R.array.invitebid_mavin_list_names;
		}

		@Override
		public int getListHeaderIds() {
			return R.array.invitebid_mavin_list_ids;
		}

		@Override
		public int getListItemIds() {
			return R.array.invitebid_mavin_list_ids;
		}};	
		
	private DataManagerInterface mDataListManager = new DataManagerInterface() {

		@Override
		public void getDataOnResult(ResultStatus status, List<?> list) {
			if (status.getCode() == AnalysisManager.SUCCESS_DB_ADD) {
				mRequestInterface.getListData();
//				if (list != null && !list.isEmpty()) {
//					ZB_mavin mavin = (ZB_mavin) list.get(0);
//					mZBMavin.setZb_mavin_id(mavin.getZb_mavin_id());
//					MiscUtils.clone(mavin, mZBMavin);
//					mListAdapter.addDataToList(mavin);
//				}
			} else {
				Toast.makeText(getActivity(), status.getMessage(), Toast.LENGTH_SHORT).show();
			}
		}
	};
	
	ServiceInterface<ZB_mavin> mRequestInterface = new ServiceInterface<ZB_mavin>() {

		@Override
		public void getListData() {
			if (checkParentBeanForQuery()) {
				mService.getZBMavinList(getServiceManager(), mParentBean.getZb_plan_id());
			}
		}

		@Override
		public void addItem(ZB_mavin t) {
			mService.addZBMavin(mDataListManager, t);
		}

		@Override
		public void deleteItem(ZB_mavin t) {
			mService.deleteZBMavin(getServiceManager(), t.getZb_mavin_id());
		}

		@Override
		public void updateItem(ZB_mavin t) {
		}};		
		
	@Override
	protected int getDocumentType() {
		return 0;
	}

	@Override
	protected int getAttachPosition() {
		return -1;
	}
	
	@Override
	public void onHiddenChanged(boolean hidden) {
	    super.onHiddenChanged(hidden);
	    if (!hidden) {
	        mRequestInterface.getListData();
	    }
	}
	
	@Override
	protected boolean disableFloatingMenu() {
		return false;
	}
	
	private ZB_mavin mZBMavin = new ZB_mavin();
	public void onActivityResult(int requestCode, int resultCode, Intent data) {
		super.onActivityResult(requestCode, resultCode, data);
		if (resultCode != Activity.RESULT_OK) {
			return;
		}
		
		if (requestCode == MAVINLIB_MAVIN_REQUEST + getRatio()){
			Mavin_lib t = (Mavin_lib) data.getSerializableExtra(ListSelectActivity.RESULT_KEY);
			mZBMavin.setTenant_id(t.getTenant_id());
			mZBMavin.setZb_plan_id(mParentBean.getZb_plan_id());
			mZBMavin.setMavin_lib_id(t.getMavin_lib_id());
			mZBMavin.setName(t.getName());
			mZBMavin.setSex(t.getSex());
			mZBMavin.setBirthday(t.getBirthday());
			mZBMavin.setWork_start(t.getWork_start());
			mZBMavin.setWork_company(t.getWork_company());
			mZBMavin.setType(t.getType());
			mZBMavin.setTel(t.getTel());
			mZBMavin.setTitle(t.getTitle());
			mZBMavin.setMajor(t.getMajor());
			mZBMavin.setDegree(t.getDegree());
			mZBMavin.setMark(t.getMark());
			mZBMavin.setAttachment(t.getAttachment());
			
			mRequestInterface.addItem(mZBMavin);
		}
		
	}
}

