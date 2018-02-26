package com.pm360.cepm360.app.module.settings;

import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.text.TextUtils.TruncateAt;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.Toast;

import com.pm360.cepm360.R;
import com.pm360.cepm360.app.cache.UserCache;
import com.pm360.cepm360.app.common.activity.OwnerSelectActivity;
import com.pm360.cepm360.app.common.adpater.DataListAdapter.ViewHolder;
import com.pm360.cepm360.app.common.custinterface.CommonListInterface;
import com.pm360.cepm360.app.common.custinterface.DialogAdapterInterface;
import com.pm360.cepm360.app.common.custinterface.OptionMenuInterface;
import com.pm360.cepm360.app.common.custinterface.ServiceInterface;
import com.pm360.cepm360.app.common.view.OptionsMenuView.SubMenuListener;
import com.pm360.cepm360.app.common.view.parent.BaseDialog;
import com.pm360.cepm360.app.common.view.parent.BaseListFragment;
import com.pm360.cepm360.app.common.view.parent.BaseWidgetCore.WidgetInterface;
import com.pm360.cepm360.common.GLOBAL;
import com.pm360.cepm360.common.util.MiscUtils;
import com.pm360.cepm360.entity.Flow_setting;
import com.pm360.cepm360.entity.User;
import com.pm360.cepm360.services.system.RemoteFlowSettingService;

public class ApprovalProcessFragment extends BaseListFragment<Flow_setting> {
	
	private RemoteFlowSettingService mService;
	
	/**
	 * 创建视图
	 */
	@Override
	public View onCreateView( LayoutInflater inflater,
							  ViewGroup container,
							  Bundle savedInstanceState) {
		// 初始化类型和接口
		init( Flow_setting.class, 
			  mListInterface, 
			  mRequestInterface,
			  mOptionMenuInterface,
			  mDialogInterface);
		
		setPermissionIdentity(GLOBAL.SYS_ACTION[25][0], GLOBAL.SYS_ACTION[24][0]);
		mService = RemoteFlowSettingService.getInstance();
		
		return super.onCreateView(inflater, container, savedInstanceState);
	}
	
	/**
	 * 列表接口，该接口实现的内容比较杂，主要用来提供资源
	 */
	CommonListInterface<Flow_setting> mListInterface 
						= new CommonListInterface<Flow_setting>() {
		@Override
		public int getListItemId(Flow_setting t) {
			return t.getFlow_id();
		}

		@Override
		public String[] getDisplayFeilds() {
			return new String[] {
					SERIAL_NUMBER,
					"flow_name",
					"status"
			};
		}

		@Override
		public int getListHeaderLayoutId() {
			return R.layout.approval_process_listitem_layout;
		}
		
		@Override
		public int getListLayoutId() {
			return getListHeaderLayoutId();
		}

		@Override
		public int getListHeaderNames() {
			return R.array.approval_process_list_header_names;
		}

		@Override
		public int getListHeaderIds() {
			return R.array.approval_process_list_header_ids;
		}
		
		@Override
		public Map<String, Map<String, String>> getDisplayFieldsSwitchMap() {
			Map<String, Map<String, String>> fieldSwitchMap
							= new HashMap<String, Map<String, String>>();
			
			fieldSwitchMap.put(mDisplayFields[2], globalIdNameMap(GLOBAL.FLOW_STATUS));
			
			return fieldSwitchMap;
		}

		@Override
		public int getListItemIds() {
			return getListHeaderIds();
		}
	};
	
	OptionMenuInterface mOptionMenuInterface = new OptionMenuInterface() {

		@Override
		public int getOptionMenuNames() {
			return R.array.approval_process_option_menu_names;
		}

		@Override
		public SubMenuListener getOptionMenuClickListener() {
			return new SubMenuListener() {
				
				@Override
				public void onSubMenuClick(View view) {
					mOptionsMenu.dismiss();
					switch ((Integer) view.getTag()) {
						case 0:		// 关闭
							mCurrentUpdateItem = MiscUtils.clone(mCurrentItem);
							mCurrentUpdateItem.setStatus(Integer.parseInt(GLOBAL.FLOW_STATUS[1][0]));
							mService.SetFlow(getServiceManager(), mCurrentUpdateItem);
							break;
						case 1:		// 修改
							mIsAddOperation = false;
							showUpdateDialog(true);
							break;
					}
				}
			};
		}
	};
	
	OptionMenuInterface mOptionMenuOpen = new OptionMenuInterface() {

		@Override
		public int getOptionMenuNames() {
			return R.array.approval_process_option_open;
		}

		@Override
		public SubMenuListener getOptionMenuClickListener() {
			return new SubMenuListener() {
				
				@Override
				public void onSubMenuClick(View view) {
					mOptionsMenu.dismiss();
					switch ((Integer) view.getTag()) {
						case 0:		//打开
							mIsAddOperation = false;
							showUpdateDialog(true);
							mDialog.setRadioButtonPosition(0, GLOBAL.FLOW_STATUS[0][1]);
							break;
					}
				}
			};
		}
	};
	
	
	/**
	 * 服务请求接口实现
	 */
	ServiceInterface<Flow_setting> mRequestInterface 
								= new ServiceInterface<Flow_setting>() {

		@Override
		public void getListData() {
			mService.getFlowSettingList(getServiceManager(), 
			        UserCache.getCurrentUser().getTenant_id());
		}

		@Override
		public void addItem(Flow_setting t) {
			
		}

		@Override
		public void deleteItem(Flow_setting t) {
			
		}

		@Override
		public void updateItem(Flow_setting t) {
			mService.SetFlow(getServiceManager(), t);
		}
	};
	
	/**
	 * 监听器
	 * @param line
	 * @return
	 */
	private OnClickListener getClickListener(final int line) {
		return new OnClickListener() {
			@Override
			public void onClick(View view) {
				Intent intent = new Intent();
				intent.setClass(getActivity(), OwnerSelectActivity.class);
				intent.putExtra("title", getResources()
						.getString(R.string.approval_process_select_person));
				startActivityForResult(intent, line);
			}
		};
	}
	
	/**
	 * 对话框风格，数据，映射及额外初始化接口实现
	 */
	DialogAdapterInterface mDialogInterface = new DialogAdapterInterface() {
		@Override
		public int getDialogTitleId() {
			return R.string.approval_process_dialog_title;
		}

		@Override
		public int getDialogLableNames() {
			return R.array.approval_process_dialog_lable_names;
		}
		
		@Override
		public String[] getUpdateFeilds() {
			return new String[] {
					"status",
					"flow_name",
					"level1",
					"level2",
					"level3",
					"level4",
					"level5"
			};
		}

		@SuppressLint("UseSparseArrays") 
		@Override
		public Map<Integer, Integer> getDialogStyles() {
			Map<Integer, Integer> styleMap = new HashMap<Integer, Integer>();
			styleMap.put(BASE_POSITION, BaseDialog.radioLineStyle);
			
			for (int i = 2; i < 7; i++) {
				styleMap.put(BASE_POSITION + i, BaseDialog.userSelectLineStyle);
			}
			return styleMap;
		}

		@SuppressLint("UseSparseArrays")
		@Override
		public Map<Integer, String[]> getSupplyData() {
			Map<Integer, String[]> dataMap = new HashMap<Integer, String[]>();
			dataMap.put(BASE_POSITION, getResources()
						.getStringArray(R.array.approval_process_status));
			return dataMap;
		}
		
		@SuppressLint("UseSparseArrays") 
		@Override
		public Map<String, Map<String, String>> getUpdateFieldsSwitchMap() {
			Map<String, Map<String, String>> fieldSwitchMap
								= new HashMap<String, Map<String, String>>();

			fieldSwitchMap.put(mUpdateFields[0], globalIdNameMap(GLOBAL.FLOW_STATUS));
//			Map<String, String> userIdNameMap = new HashMap<String, String>();
//			Map<Integer, String> userMap = new HashMap<Integer, String>();
//			ArrayList<User> userLists = UserCache.getUserLists();
//			for (User user : userLists) {
//			    userMap.put(user.getUser_id(), user.getName());
//			}
//			for (Map.Entry<Integer, String> entry : userMap.entrySet()) {
//				userIdNameMap.put(String.valueOf(entry.getKey()), entry.getValue());
//				userIdNameMap.put(entry.getValue(), String.valueOf(entry.getKey()));
//			}
//			
//			for (int i = 2; i < 7; i++) {
//				fieldSwitchMap.put(mUpdateFields[i], userIdNameMap);
//			}
			return fieldSwitchMap;
		}

		@Override
		public void additionalInit(final BaseDialog dialog) {
			for (int i = 2; i < 7; i++) {
				OnClickListener listener = getClickListener(i);				
				dialog.setEditTextStyle(BASE_POSITION + i, 0, listener, null);
			}
			
			dialog.setWidgetInterface(new WidgetInterface() {
				
				@Override
				public void setRadioButtonChangeListener(int line, int position) {
					if (line == 0) {
						switchStatus(dialog, position);
					}
				}
			});
		}

		@Override
		public Integer[] getImportantColumns(Map<String, String> saveDataMap) {
			if (saveDataMap == null || saveDataMap.isEmpty()) {
				return new Integer[] {0, 1};
			}
			
			Integer[] importantColumns = new Integer[]{-1};
			List<String> mustFillInList = Arrays.asList(new String[] {
					mDialogLableNames[0],
					mDialogLableNames[1]
			});
			
			// 启用时检查审批人
			if (saveDataMap.get(mustFillInList.get(0)).equals(getString(R.string.enable))) {
				mustFillInList = Arrays.asList(new String[] {
						mDialogLableNames[2],
						mDialogLableNames[3],
						mDialogLableNames[4],
						mDialogLableNames[5],
						mDialogLableNames[6]
				});
				
				// 启用后必须填写审批人
				if (saveDataMap.get(mustFillInList.get(0)).isEmpty()) {
					return new Integer[] {0, 1, 2};
				}
				
				for (int i = mustFillInList.size() - 1; i >= 0; i--) {
					if (!saveDataMap.get(mustFillInList.get(i)).equals("")) {
						for (int j = i - 1; j >= 0; j--) {
							if (saveDataMap.get(mustFillInList.get(j)).equals("")) {
								Toast.makeText(getActivity(), R.string.pls_care_order, Toast.LENGTH_SHORT).show();
								return importantColumns;
							}
						}
						break;
					}
				}
			}
			
			return null;
		}
	};
	
	/**
	 * 切换状态
	 * @param dialog
	 * @param position
	 */
	private void switchStatus(BaseDialog dialog, int position) {
		if (position == 0) {
			for (int i = 2; i < 7; i++) {
				dialog.setReadOnlyLine(i, false);
			}
		} else {
			for (int i = 2; i < 7; i++) {
				dialog.setReadOnlyLine(i, true);
			}
		}
	}
	
	@Override
	protected void switchOptionMenu() {
		super.switchOptionMenu();
		
		if (mDialog != null) {
			switch (mCurrentItem.getStatus()) {
				case 1:
					setOptionMenuInterface(mOptionMenuInterface);
					break;
				case 2:
					setOptionMenuInterface(mOptionMenuOpen);
					break;
				default:
					break;
			}
			
			switchStatus(mDialog, mCurrentItem.getStatus() - 1);
		}
	}

	
	@Override
	protected boolean doExtraInitListViewItem(int line, int position, ViewHolder holder) {
		if (position == 2) {
			Flow_setting listItem = mListAdapter.getItem(line);
			Map<String, String> displayFieldMap = MiscUtils.beanToMap(listItem);
			
			displayFieldRemap(displayFieldMap, listItem, position);
			if ((displayFieldMap.get(mDisplayFields[2]) != null) && !(displayFieldMap.get(mDisplayFields[2])).equals("0")) {
				holder.tvs[2].setText(displayFieldMap.get(mDisplayFields[2]));
				holder.tvs[2].setEllipsize(TruncateAt.MARQUEE);
			} else {
				holder.tvs[2].setText("");
			}	
			
			if (holder.tvs[2].getText() != null) {
				
				if (holder.tvs[2].getText() == getString(R.string.close)) {
					holder.tvs[2].setTextColor(Color.RED);
				} else {
					holder.tvs[2].setTextColor(Color.BLACK);
				}
			}

			return true;
		} else {
			return false;
		}
	}


	@Override
	public void onActivityResult(int requestCode, int resultCode, Intent data) {
		if (resultCode == Activity.RESULT_OK) {
			User user = (User) data.getSerializableExtra("user");
			if (user != null) {
				mDialog.setUserTextContent(requestCode, user.getUser_id());
			} else {
				mDialog.setUserTextContent(requestCode, 0);
			}
		}
		
		super.onActivityResult(requestCode, resultCode, data);
	}
}
