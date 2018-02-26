package com.pm360.cepm360.app.module.settings;

import android.annotation.SuppressLint;
import android.app.Dialog;
import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Color;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.v4.app.Fragment;
import android.util.TypedValue;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.CompoundButton.OnCheckedChangeListener;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.SimpleAdapter;
import android.widget.TextView;
import android.widget.Toast;

import com.pm360.cepm360.R;
import com.pm360.cepm360.app.CepmApplication;
import com.pm360.cepm360.app.cache.PermissionCache;
import com.pm360.cepm360.app.cache.UserCache;
import com.pm360.cepm360.app.common.activity.DataManagerInterface;
import com.pm360.cepm360.app.common.view.OptionsMenuView;
import com.pm360.cepm360.app.module.home.ModuleConfig;
import com.pm360.cepm360.common.GLOBAL;
import com.pm360.cepm360.common.ResultStatus;
import com.pm360.cepm360.common.json.AnalysisManager;
import com.pm360.cepm360.entity.Role;
import com.pm360.cepm360.services.common.RemoteCommonService;
import com.pm360.cepm360.services.system.RemoteRoleService;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.StringTokenizer;

public class RolePermissionFragment extends Fragment {

	private OptionsMenuView mOptionsMenuView;
	private SimpleAdapter mListAdapter;
	private ActionAdapter mActionadapter;
	private String[] mItemNames;
	private int[] mItemIds;
	private List<Role> mRoleLists = new ArrayList<Role>();
	private List<Action> mListActions = new ArrayList<Action>();
	private List<Map<String, String>> mListViewDatas = new ArrayList<Map<String, String>>();

	private Role mSelectedRole;
	private Map<String, String> mSelectedRoleMap;

	private Dialog mAddRolePermissionDialog;

	String actionsString = "";
	String roleCode = "";
	String roleName = "";
	String roleAction = "";

	private static final String ROLE_EDIT = "11_1";
	private static final String ROLE_VIEW = "11_2";

	@SuppressWarnings("unused")
	private static final String TAG = "RolePermisonFragment";

	@SuppressLint("HandlerLeak")
	Handler mHandler = new Handler() {
		@Override
		public void handleMessage(Message msg) {
			switch (msg.what) {
			case 0:
				mListViewDatas.clear();
				for (Role r : mRoleLists) {
					if (r instanceof Role) {
						Role role = r;
						Map<String, String> map = new HashMap<String, String>();
						map.put("code", role.getCode());
						map.put("name", role.getName());
						if(role.getAction()==null||role.getAction().isEmpty())
						{
							map.put("assigned", getString(R.string.no));
						}else {
							map.put("assigned", getString(R.string.yes));
						}
						mListViewDatas.add(map);
					}
				}
				mListAdapter.notifyDataSetChanged();
				break;
			case 1:
				for (Role r : mRoleLists) {
					if (r instanceof Role) {
						Role role = r;
						Map<String, String> map = new HashMap<String, String>();
						map.put("code", role.getCode());
						map.put("name", role.getName());
						if(role.getAction()==null||role.getAction().isEmpty())
						{
							map.put("assigned", getString(R.string.no));
						}else {
							map.put("assigned", getString(R.string.yes));
						}
						mListViewDatas.add(map);
					}
				}
				mListAdapter.notifyDataSetChanged();
				break;
			case 2:
				mListAdapter.notifyDataSetChanged();
				break;
			case 3:
				mListViewDatas.clear();
				mRoleLists.remove(mSelectedRole);
				mSelectedRole = null;
				mSelectedRoleMap = null;
				for (Role r : mRoleLists) {
					Role role = r;
					Map<String, String> map = new HashMap<String, String>();
					map.put("code", role.getCode());
					map.put("name", role.getName());
					if(role.getAction()==null||role.getAction().isEmpty())
					{
						map.put("assigned", getString(R.string.no));
					}else {
						map.put("assigned", getString(R.string.yes));
					}
					mListViewDatas.add(map);
				}
				mListAdapter.notifyDataSetChanged();
				break;
			default:
				break;
			}
		}
	};

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {
		if (!PermissionCache.hasSysPermission(ROLE_EDIT)
				&& !PermissionCache.hasSysPermission(ROLE_VIEW)) {
			return inflater.inflate(R.layout.no_permissions_content_layout,
					container, false);
		}

		View rootView = inflater.inflate(R.layout.role_permissions_fragment,
				container, false);
		initContentView(rootView);
		loadRoleList();

		getActions();
		if (PermissionCache.hasSysPermission(ROLE_EDIT)) {
		    createPopupWindow();
		}
		return rootView;
	}

	@SuppressLint("Recycle")
	private void initContentView(View rootView) {
		ListView listView = (ListView) rootView.findViewById(R.id.listView);
		View roleHeaderView = rootView.findViewById(R.id.rolelistHeaderView);
		TypedArray typedArray = getResources().obtainTypedArray(
				R.array.role_ids);

		mItemNames = getResources().getStringArray(R.array.role_names);
		if (mItemNames != null) {
			int itemLength = mItemNames.length;
			mItemIds = new int[itemLength];
			for (int i = 0; i < itemLength; i++) {
				mItemIds[i] = typedArray.getResourceId(i, 0);
				TextView tv = (TextView) roleHeaderView
						.findViewById(mItemIds[i]);
				tv.setTextSize(TypedValue.COMPLEX_UNIT_PX, getActivity()
						.getResources().getDimension(R.dimen.table_title_textsize));
				//tv.setGravity(Gravity.CENTER);
				tv.setTextColor(Color.BLACK);
				tv.setText(mItemNames[i]);
			}

		}

		mListAdapter = new RoleAdapter(getActivity(), mListViewDatas,
				R.layout.role_permissions_list_item, new String[] { "code",
						"name" ,"assigned"}, new int[] { R.id.role_code,
						R.id.role_permission_name,R.id.role_permission_assigned });
		listView.setAdapter(mListAdapter);
	}

	private void loadRoleList() {
		Role role = new Role();
		role.setTenant_id(UserCache.getCurrentUser()
				.getTenant_id());
		RemoteRoleService.getInstance().getRoleList(new DataManagerInterface() {

			@SuppressWarnings("unchecked")
			@Override
			public void getDataOnResult(ResultStatus status, List<?> list) {
				if (list != null
						&& status.getCode() == AnalysisManager.SUCCESS_DB_QUERY) {
					mRoleLists = (List<Role>) list;
					Message message = Message.obtain();
					message.what = 0;
					mHandler.sendMessage(message);
				}
			}
		}, role);
	}

	private void createPopupWindow() {
		String[] subMenuNames = getResources().getStringArray(
				R.array.role_options_menu);
		mOptionsMenuView = new OptionsMenuView(getActivity(), subMenuNames);
		mOptionsMenuView
				.setSubMenuListener(new OptionsMenuView.SubMenuListener() {
					@Override
					public void onSubMenuClick(View view) {
						switch ((Integer) view.getTag()) {
						case 0:
							if (PermissionCache.hasSysPermission(
									ROLE_EDIT)) {
								initAddRolePermissionsDialog(mSelectedRole);
							} else {
								Toast.makeText(getActivity(),
										getString(R.string.no_role_permission),
										Toast.LENGTH_SHORT).show();
							}
							break;
						}
						mOptionsMenuView.dismiss();
					}
				});
	}

	protected void initAddRolePermissionsDialog(final Role roleSelect) {
		mAddRolePermissionDialog = new Dialog(getActivity(),
				R.style.MyDialogStyle);
//		mAddRolePermissionDialog.setCancelable(false);
		mAddRolePermissionDialog.setContentView(R.layout.role_add_dialog);
		mAddRolePermissionDialog.setCanceledOnTouchOutside(true);
		final ListView listView = (ListView) mAddRolePermissionDialog
				.findViewById(R.id.role_permision_listView);
		ImageView mClose  = (ImageView) mAddRolePermissionDialog.findViewById(R.id.btn_close);
		final CheckBox select_box0 = (CheckBox) mAddRolePermissionDialog
				.findViewById(R.id.modulesHeaderView).findViewById(
						R.id.role_checkBox);

		final EditText roleCodeEditText = (EditText) mAddRolePermissionDialog
				.findViewById(R.id.role_code);
		final EditText roleNameEditText = (EditText) mAddRolePermissionDialog
				.findViewById(R.id.role_name);
		roleCodeEditText.setEnabled(false);
		roleNameEditText.setEnabled(false);
		Button saveButton = (Button) mAddRolePermissionDialog
				.findViewById(R.id.role_save);
		Button exitButton = (Button) mAddRolePermissionDialog
				.findViewById(R.id.role_exit);

		mActionadapter = new ActionAdapter(getActivity(), mListActions);
		listView.setAdapter(mActionadapter);
		mAddRolePermissionDialog.show();
		if (roleSelect != null) {
			roleCodeEditText.setText(roleSelect.getCode());
			roleNameEditText.setText(roleSelect.getName());
			StringTokenizer st = new StringTokenizer(roleAction, ",");
			// Log.i(TAG, "StringTokenizer action" + st.toString());
			while (st.hasMoreTokens()) {
				String string = st.nextToken();
				// Log.i(TAG, "Role action" + string);
				mActionadapter.getCheckMap().put(string, true);
			}
			mActionadapter.notifyDataSetChanged();
			// Log.i(TAG, "Role select" + mActionadapter.getCheckMap());
		}

		select_box0.setOnCheckedChangeListener(new OnCheckedChangeListener() {

			@Override
			public void onCheckedChanged(CompoundButton buttonView,
					boolean isChecked) {
				if (select_box0.isChecked()) {
					mActionadapter.configCheckMap(true);
					mActionadapter.notifyDataSetChanged();
				} else {
					mActionadapter.configCheckMap(false);
					mActionadapter.notifyDataSetChanged();
				}
			}
		});
		
		mClose.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				mAddRolePermissionDialog.dismiss();
			}
		});
		exitButton.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				mAddRolePermissionDialog.dismiss();
			}
		});

		saveButton.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				if (roleSelect == null) {
					String roleCode = roleCodeEditText.getText().toString();
					String roleName = roleNameEditText.getText().toString();
					if (roleName != null && !roleName.equals("")
							&& roleCode != null && !roleCode.equals("")) {
						Role role = new Role();
						role.setCode(roleCode);
						role.setName(roleName);
						role.setTenant_id(UserCache
								.getCurrentUser().getTenant_id());

						StringBuilder sb = new StringBuilder();

						for (int i = 0; i < mListActions.size(); i++) {
							if (mActionadapter.getCheckMap().get(
									mListActions.get(i).number) == true) {
								sb.append(mListActions.get(i).number + ",");
							}
						}
						if (sb.length() > 0) {
							actionsString = sb.substring(0, sb.length() - 1);
						} else {
							actionsString = "";
						}
						role.setAction(actionsString);
						RemoteRoleService.getInstance().addRole(
								new DataManagerInterface() {

									@SuppressWarnings("unchecked")
									@Override
									public void getDataOnResult(
											ResultStatus status, List<?> list) {
										if (list != null
												&& status.getCode() == AnalysisManager.SUCCESS_DB_ADD) {

											mRoleLists.add(((List<Role>) list)
													.get(0));
											// Log.i(TAG, "addRole-return>>"
											// + mRoleLists.toString());
											Message message = Message.obtain();
											message.what = 0;
											mHandler.sendMessage(message);
										}
									}
								}, role);
						mAddRolePermissionDialog.dismiss();
					} else {
						Toast toast = Toast.makeText(getActivity(),
								getString(R.string.role_null_enter_again),
								Toast.LENGTH_SHORT);
						toast.setGravity(Gravity.CENTER, 0, 0);
						toast.show();
					}
				} else {

					final String roleCode = roleCodeEditText.getText()
							.toString();
					final String roleName = roleNameEditText.getText()
							.toString();
					if (roleName != null && !roleName.equals("")
							&& roleCode != null && !roleCode.equals("")) {

						roleSelect.setCode(roleCode);
						roleSelect.setName(roleName);

						StringBuilder sb = new StringBuilder();

						for (int i = 0; i < mListActions.size(); i++) {
							if (mActionadapter.getCheckMap().get(
									mListActions.get(i).number) == true) {
								sb.append(mListActions.get(i).number + ",");
							}
						}
						if (sb.length() > 0) {
							actionsString = sb.substring(0, sb.length() - 1);
						} else {
							actionsString = "";
						}
						roleSelect.setAction(actionsString);
						RemoteRoleService.getInstance().updateRole(
								new DataManagerInterface() {

									@Override
									public void getDataOnResult(
											ResultStatus status, List<?> list) {
										if (status.getCode() == AnalysisManager.SUCCESS_DB_UPDATE) {
											mSelectedRoleMap.put("number",
													String.valueOf(roleSelect
															.getRole_id()));
											mSelectedRoleMap.put("code",
													roleCode);
											mSelectedRoleMap.put("name",
													roleName);

											Message message = Message.obtain();
											message.what = 0;
											mHandler.sendMessage(message);
										}
									}
								}, roleSelect);
						mAddRolePermissionDialog.dismiss();
					}
				}
				setSysPermissions();
			}
		});
	}

	class Action {
		private String number;
		private String name;
	}

	private void getActions() {
		mListActions.clear();
		int enterpriseType = ((CepmApplication) getActivity()
				.getApplicationContext()).getEnterpriseType();
		int[] homeModules = ModuleConfig.getHomeModules(enterpriseType);
		for (int module : homeModules) {
			for (int i = 0; i < GLOBAL.SYS_ACTION.length; i++) {
				if (GLOBAL.SYS_ACTION[i][0].startsWith((module) + "_")) {
					Action action = new Action();
					action.number = GLOBAL.SYS_ACTION[i][0];
					action.name = GLOBAL.SYS_ACTION[i][1];
					mListActions.add(action);
					continue;
				}
			}
		}
		
		// 管理方 - 组合管理模块 权限
		if (enterpriseType == 0) {
		    // 组合编辑
            Action action = new Action();
            action.number = GLOBAL.SYS_ACTION[34][0];
            action.name = GLOBAL.SYS_ACTION[34][1];
            mListActions.add(action);
            
            // 组合反馈编辑
            Action action1 = new Action();
            action1.number = GLOBAL.SYS_ACTION[52][0];
            action1.name = GLOBAL.SYS_ACTION[52][1];
            mListActions.add(action1);
		}
	}

	private class ActionAdapter extends BaseAdapter {
		private List<Action> mArrayList;
		private LayoutInflater mInflater;

		/**
		 * CheckBox 是否选择的存储集合,key 是 position , value 是该position是否选中
		 */
		private Map<String, Boolean> isCheckMap = new HashMap<String, Boolean>();

		public ActionAdapter(Context context, List<Action> list) {
			mInflater = LayoutInflater.from(context);
			mArrayList = list;
			// 初始化,默认都没有选中
			configCheckMap(false);
		}

		/**
		 * 首先,默认情况下,所有项目都是没有选中的.这里进行初始化
		 */
		public void configCheckMap(boolean bool) {

			for (int i = 0; i < mArrayList.size(); i++) {
				isCheckMap.put(mArrayList.get(i).number, bool);
			}

		}

		@SuppressWarnings("unused")
		public void configRoleCheckMap(Map<String, Boolean> map) {
			isCheckMap = map;
		}

		public Map<String, Boolean> getCheckMap() {
			return this.isCheckMap;
		}

		@SuppressWarnings("unused")
		public void setCheckMap(Map<String, Boolean> map) {
			this.isCheckMap = map;
		}

		@Override
		public int getCount() {
			return (mArrayList.size() + 3) / 4;
		}

		@Override
		public Object getItem(int position) {
			return mArrayList.get(4 * position);
		}

		@Override
		public long getItemId(int position) {
			return position;
		}

		@Override
		public View getView(final int position, View convertView,
				ViewGroup parent) {
			ViewHolder holder = null;
			if (convertView == null) {
				convertView = mInflater.inflate(R.layout.role_item, parent,
						false);
				holder = new ViewHolder();
				holder.textView.add((TextView) convertView
						.findViewById(R.id.role_item0));
				holder.checkBox.add((CheckBox) convertView
						.findViewById(R.id.role_checkBox0));
				holder.textView.add((TextView) convertView
						.findViewById(R.id.role_item1));
				holder.checkBox.add((CheckBox) convertView
						.findViewById(R.id.role_checkBox1));
				holder.textView.add((TextView) convertView
						.findViewById(R.id.role_item2));
				holder.checkBox.add((CheckBox) convertView
						.findViewById(R.id.role_checkBox2));
				holder.textView.add((TextView) convertView
						.findViewById(R.id.role_item3));
				holder.checkBox.add((CheckBox) convertView
						.findViewById(R.id.role_checkBox3));
				convertView.setTag(holder);
			} else {
				holder = (ViewHolder) convertView.getTag();
			}
			for (int i = 0; i <= 3; i++) {
				final int m = i;
				if (4 * position + i > mArrayList.size() - 1) {
					holder.checkBox.get(i).setVisibility(View.INVISIBLE);
					holder.textView.get(i).setText("");
				} else {
					holder.checkBox.get(i).setVisibility(View.VISIBLE);
					holder.textView.get(i).setText(
							mArrayList.get(4 * position + i).name);
					if (isCheckMap.get(mArrayList.get(4 * position + i).number) == null) {
						isCheckMap.put(mArrayList.get(4 * position + i).number,
								false);
					}
					holder.checkBox.get(i).setOnCheckedChangeListener(
							new OnCheckedChangeListener() {
								@Override
								public void onCheckedChanged(
										CompoundButton buttonView,
										boolean isChecked) {

									/*
									 * 将选择项加载到map里面寄存
									 */
									isCheckMap.put(
											mArrayList.get(4 * position + m).number,
											isChecked);
									notifyDataSetChanged();
								}
							});
					holder.textView.get(i).setOnClickListener(
							new OnClickListener() {

								@Override
								public void onClick(View v) {
									boolean isChecked = isCheckMap
											.get(mArrayList.get(4 * position
													+ m).number);
									isCheckMap.put(
											mArrayList.get(4 * position + m).number,
											!isChecked);
									notifyDataSetChanged();
								}
							});
					holder.checkBox.get(i)
							.setChecked(
									isCheckMap.get(mArrayList.get(4 * position
											+ i).number));
				}
			}

			return convertView;

		}

		public class ViewHolder {
			public List<CheckBox> checkBox = new ArrayList<CheckBox>();
			public List<TextView> textView = new ArrayList<TextView>();
		}

	}

	class RoleAdapter extends SimpleAdapter {
		private int mSelectedPosition = -1;

		public RoleAdapter(Context context,
				List<? extends Map<String, String>> data, int resource,
				String[] from, int[] to) {
			super(context, data, resource, from, to);
		}

		@Override
		public View getView(final int position, View convertView,
				ViewGroup parent) {
			View viewItem = super.getView(position, convertView, parent);
			TextView tv = (TextView) viewItem.findViewById(R.id.role_permission_assigned);
			TextView serialTextView = (TextView) viewItem.findViewById(R.id.serial_number);
			serialTextView.setText(String.valueOf(position + 1));
			if(mRoleLists.get(position).getAction()==null||mRoleLists.get(position).getAction().isEmpty())
			{
				tv.setTextColor(Color.RED);
			}else {
				tv.setTextColor(Color.BLACK);
			}
			
			viewItem.setOnClickListener(new View.OnClickListener() {

				@Override
				public void onClick(View view) {
					roleCode = mRoleLists.get(position).getCode();
					roleName = mRoleLists.get(position).getName();
					if (mRoleLists.get(position).getAction() != null) {
						roleAction = mRoleLists.get(position).getAction();
					} else {
						roleAction = "";
					}
					mSelectedRole = mRoleLists.get(position);
					mSelectedRoleMap = mListViewDatas.get(position);

					setSelected(position);
					if (PermissionCache.hasSysPermission(ROLE_EDIT)) {
    					mOptionsMenuView.showAsDropDown(view,
    							view.getMeasuredWidth() / 3,
    							-view.getMeasuredHeight() - 35);
					}
				}

			});

	        if (mSelectedPosition == position) {
	        	viewItem.setBackgroundResource(R.color.listview_selected_bg);
	        } else {
	            if (position % 2 == 1) {
	            	viewItem.setBackgroundResource(R.color.content_listview_single_line_bg);
	            } else {
	            	viewItem.setBackgroundColor(Color.TRANSPARENT);
	            }
	        }

			return viewItem;
		}

		private void setSelected(int position) {
			mSelectedPosition = position;
			mListAdapter.notifyDataSetChanged();
		}
	}

	void setSysPermissions() {
		RemoteCommonService.getInstance().getSYSPermission(
				new DataManagerInterface() {

					@Override
					public void getDataOnResult(ResultStatus status,
							List<?> list) {

						if (status.getCode() != AnalysisManager.SUCCESS_DB_QUERY
								&& status.getMessage() != null
								&& !status.getMessage().equals("")) {
						}
						if (status.getCode() == AnalysisManager.SUCCESS_DB_QUERY
								&& list != null && list.size() > 0) {
							StringBuilder builder = new StringBuilder();
							for (Object object : list) {
								if (object instanceof Role) {
									Role role = (Role) object;
									String action = role.getAction();
									if (action != null) {
										builder.append(action + ",");
									}
								}
							}
							String role_text = builder.toString();
							if (role_text.endsWith(",")) {
								role_text = role_text.substring(0,
										role_text.length() - 1);
							}
							String[] permissions = role_text.split(",");
							if (permissions != null) {
								PermissionCache
										.setSysPermissions(permissions);
							}
							
							//updateProjectPermission();
						}
					}
				}, UserCache.getCurrentUser());
	}
	
//    private void updateProjectPermission() {
//        Project currentProject = ProjectCache.getCurrentProject();
//        if (currentProject != null) {
//            RemoteCommonService.getInstance().getPermissionByProject(
//                    new DataManagerInterface() {
//
//                        @Override
//                        public void getDataOnResult(ResultStatus status,
//                                List<?> list) {
//                            if (status.getCode() == AnalysisManager.SUCCESS_DB_QUERY
//                                    && list != null && list.size() > 0) {
//                                StringBuilder builder = new StringBuilder();
//                                for (Object object : list) {
//                                    if (object instanceof Role) {
//                                        Role role = (Role) object;
//                                        String action = role.getAction();
//                                        if (action != null) {
//                                            builder.append(action + ",");
//                                        }
//                                    }
//                                }
//                                String role_text = builder.toString();
//                                if (role_text.endsWith(",")) {
//                                    role_text = role_text.substring(0,
//                                            role_text.length() - 1);
//                                }
//                                PermissionCache
//                                        .setProjectPermissions(
//                                                role_text.split(","));
//                            }
//                        }
//                    }, currentProject.getProject_id(), UserCache.getCurrentUser());
//        }
//    }
}
