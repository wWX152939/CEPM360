package com.pm360.cepm360.app.module.settings;

import android.annotation.SuppressLint;
import android.app.AlertDialog;
import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.res.TypedArray;
import android.graphics.Color;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.v4.app.Fragment;
import android.text.InputType;
import android.util.Log;
import android.util.TypedValue;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.CompoundButton.OnCheckedChangeListener;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.SimpleAdapter;
import android.widget.TextView;
import android.widget.Toast;

import com.pm360.cepm360.R;
import com.pm360.cepm360.app.cache.ObsCache;
import com.pm360.cepm360.app.cache.PermissionCache;
import com.pm360.cepm360.app.cache.RoleCache;
import com.pm360.cepm360.app.cache.UserCache;
import com.pm360.cepm360.app.common.activity.DataManagerInterface;
import com.pm360.cepm360.app.common.activity.ListSelectActivity;
import com.pm360.cepm360.app.common.view.FloatingMenuView;
import com.pm360.cepm360.app.common.view.OptionsMenuView;
import com.pm360.cepm360.app.common.view.parent.BaseDialog;
import com.pm360.cepm360.app.utils.UtilTools;
import com.pm360.cepm360.common.ResultStatus;
import com.pm360.cepm360.common.json.AnalysisManager;
import com.pm360.cepm360.common.util.DateUtils;
import com.pm360.cepm360.entity.OBS;
import com.pm360.cepm360.entity.Role;
import com.pm360.cepm360.entity.User;
import com.pm360.cepm360.services.common.RemoteCommonService;
import com.pm360.cepm360.services.system.RemoteUserService;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.StringTokenizer;

public class UserManageFragment extends Fragment {
	@SuppressWarnings("unused")
	private static final String TAG = "UserRightsFragment";

	private String[] mDisplayItems;
	private View mHeaderView;
	private int[] mItemIds;
	private BaseDialog mAddUserDialog;
	private BaseDialog mInfoDialog;
	private BaseDialog mInfoModifyDialog;
	private Dialog mRolePermissionsDialog;
	private OptionsMenuView mOptionsMenuView;
	private OptionsMenuView mOptionsMenuView2;
	private int mSelectedIndex;
	private UserAdapter mAdapter;
	private List<Integer> mSelectedPosition = new ArrayList<Integer>();
	private ArrayList<User> mUserList = new ArrayList<User>();
	private List<Map<String, Object>> listems = new ArrayList<Map<String, Object>>();
	private User userselect;
	private RoleAdapter adapter;
	private static final String USER_EDIT = "11_1";
	private static final String USER_VIEW = "11_2";
	private static final int MODIFY_OBS_SELECT_REQUEST = 100;
	private static final int ADD_OBS_SELECT_REQUEST = 101;

	@SuppressLint("HandlerLeak")
	Handler mHandler = new Handler() {
		@Override
		public void handleMessage(Message msg) {

			switch (msg.what) {
			case 0:
				listems.clear();
				for (User user : mUserList) {
					Map<String, Object> map = getFeildMap(user);
					listems.add(map);
				}
				break;
			case 1:
				break;
			case 2:
				User user2 = (User) msg.obj;
				mAdapter.notifyDataSetChanged();
				UserCache.removeUser(user2);
				break;
			default:
				break;
			}
			mAdapter.notifyDataSetChanged();
		}

	};

	private Map<String, Object> getFeildMap(User user) {
		Map<String, Object> map = new HashMap<String, Object>();
		map.put("USER_NAME", user.getLogin_name());
		map.put("FULL_NAME", user.getName());

		String user_role = user.getRole();
		String role_text = "";
		if (user_role != null && !user_role.equals("")) {
			String[] role = user.getRole().split(",");
			StringBuilder builder = new StringBuilder();
			for (String code : role) {
				builder.append(RoleCache.getRoleCodeMaps()
						.get(code)
						+ ",");

			}
			role_text = builder.toString();
			if (role_text.endsWith(","))
				role_text = role_text.substring(0, role_text.length() - 1);
		}
		map.put("ROLE_NAME", role_text);
		OBS obs = ObsCache.findObsById(user.getObs_id());
		map.put("OBS_NAME", obs!=null ?obs.getName():"");
		map.put("MOBILE", user.getMobile());
		return map;
	}

	@SuppressLint("HandlerLeak")
	Handler mShowHandler = new Handler() {
		@Override
		public void handleMessage(Message msg) {
			Toast toast = Toast.makeText(getActivity(), (CharSequence) msg.obj,
					Toast.LENGTH_LONG);
			toast.show();
		}
	};

	private void showToast(String msgString) {
		Message msg = Message.obtain();
		msg.obj = msgString;
		mShowHandler.sendMessage(msg);
	}

	@SuppressLint("Recycle")
	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {
		if (!PermissionCache.hasSysPermission(USER_EDIT)
				&& !PermissionCache.hasSysPermission(USER_VIEW)) {
			return inflater.inflate(R.layout.no_permissions_content_layout,
					container, false);
		}
		super.onCreateView(inflater, container, savedInstanceState);

		View rootView = inflater.inflate(
				R.layout.settings_user_rights_fragment, container, false);
		mDisplayItems = getResources()
				.getStringArray(R.array.user_rights_title);
		ListView mListView = (ListView) rootView.findViewById(R.id.listView);
		mHeaderView = rootView.findViewById(R.id.listHeaderView);
		TypedArray typedArray = getResources().obtainTypedArray(
				R.array.user_rights_title_ids);

		if (mDisplayItems != null) {
			int itemLength = mDisplayItems.length;
			mItemIds = new int[itemLength];
			for (int i = 0; i < itemLength; i++) {
				mItemIds[i] = typedArray.getResourceId(i, 0);
				TextView tv = (TextView) mHeaderView.findViewById(mItemIds[i]);
				tv.setTextSize(
						TypedValue.COMPLEX_UNIT_PX,
						getActivity().getResources().getDimension(
								R.dimen.table_title_textsize));
				//tv.setGravity(Gravity.CENTER);
				tv.setTextColor(Color.BLACK);
				tv.setText(mDisplayItems[i]);
			}
		}

		mAdapter = new UserAdapter(this.getActivity().getBaseContext(),
				listems, R.layout.user_rights_list_item, new String[] {
						"USER_NAME", "FULL_NAME", "ROLE_NAME", "OBS_NAME" ,"MOBILE" },
				new int[] {R.id.user_name, R.id.full_name, R.id.role_name,
						R.id.obs_name ,R.id.mobile });
		mListView.setAdapter(mAdapter);

		loadUserData();

		adapter = new RoleAdapter(getActivity(), RoleCache.getRoleLists());
		mAddUserDialog = new BaseDialog(getActivity(),
				getString(R.string.user_add));
		mInfoDialog = new BaseDialog(getActivity(),
				getString(R.string.user_info));
		mInfoModifyDialog = new BaseDialog(getActivity(),
				getString(R.string.role_modify));
		initUserCommonWindow();
		initOptionsMenuView();

		final FloatingMenuView floatingMenuView = (FloatingMenuView) rootView
				.findViewById(R.id.floating_menu);
		floatingMenuView.addPopItem(getString(R.string.float_menu_add),
				R.drawable.users);
		floatingMenuView
				.setPopOnItemClickListener(new AdapterView.OnItemClickListener() {

					@Override
					public void onItemClick(AdapterView<?> parent, View view,
							int position, long id) {
						switch (position) {
						case 0:
							if (PermissionCache.hasSysPermission(
									USER_EDIT)) {
								userselect = new User();
								mAddUserDialog.show(null);
							} else {
								Toast.makeText(
										getActivity(),
										getString(R.string.no_add_user_pemission),
										Toast.LENGTH_SHORT).show();
							}
							floatingMenuView.dismiss();
							break;
						default:
							break;
						}
					}
				});
		if (!PermissionCache.hasSysPermission(USER_EDIT)) {
			floatingMenuView.setVisibility(View.GONE);
		}

		return rootView;
	}

	public void setSelected(int position, boolean isSeleced) {
		if (isSeleced) {
			mSelectedPosition.clear();
			mSelectedPosition.add((Integer) position);
		} else {
			mSelectedPosition.remove((Integer) position);
		}
		mAdapter.notifyDataSetChanged();
	}

	@SuppressLint("UseSparseArrays")
	protected void initUserCommonWindow() {
		Map<Integer, Integer> buttons = new HashMap<Integer, Integer>();
		buttons.put(3, BaseDialog.OBSReadOnlyLineStyle);
		buttons.put(4, BaseDialog.editTextReadOnlyLineStyle);
		mAddUserDialog.init(R.array.user_rights_add_user, buttons, null);
		mAddUserDialog.setEditTextStyle(0, 0, null,
				getString(R.string.login_name_hint));
		OnClickListener obsListener = new OnClickListener() {

			@Override
			public void onClick(View v) {
				Intent intent = new Intent();
				intent.setClass(getActivity(), ListSelectActivity.class);
				intent.putExtra(ListSelectActivity.FRAGMENT_KEY,
						OBSFragment.class);
				startActivityForResult(intent,
						UserManageFragment.ADD_OBS_SELECT_REQUEST);
			}
		};
		mAddUserDialog.setEditTextStyle(3, R.drawable.dialog_obs, obsListener,
				null);
		EditText editText = (EditText) mAddUserDialog.getPopupView()
				.findViewById(mAddUserDialog.baseEditTextId + 0);
		editText.setInputType(InputType.TYPE_CLASS_TEXT
				| InputType.TYPE_CLASS_NUMBER);

		OnClickListener roleListener = new OnClickListener() {

			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				assignRolePermision();
			}

		};
		mAddUserDialog.setEditTextStyle(4, R.drawable.ic_role, roleListener,
				null);

		Button saveImageView = (Button) mAddUserDialog.getPopupView()
				.findViewById(R.id.save_Button);
		saveImageView.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View arg0) {
				String[] names = getActivity().getResources().getStringArray(
						R.array.user_rights_add_user);

				Map<String, String> saveData = mAddUserDialog.SaveData();
				for (Map.Entry<String, String> entry : saveData.entrySet()) {
					if (entry.getValue().equals("")) {
						showToast(getString(R.string.pls_input_all_info));
						return;
					}
				}

				userselect.setLogin_name(saveData.get(names[0]));
				userselect.setPwd(saveData.get(names[1]));
				userselect.setName(saveData.get(names[2]));
				userselect.setTenant_id(UserCache
						.getCurrentUser().getTenant_id());
				if (!saveData.get(names[3]).isEmpty()) {
					userselect.setObs_id(Integer.parseInt(saveData.get(names[3])));
				}
				
				userselect.setMobile(saveData.get(names[0]));
				mAdapter.notifyDataSetChanged();

				if (UtilTools.isMobileNO(saveData.get(names[0]))) {

					RemoteUserService.getInstance().addUser(
							new DataManagerInterface() {

								@Override
								public void getDataOnResult(
										ResultStatus status, List<?> list) {
									if (status.getCode() == AnalysisManager.SUCCESS_DB_ADD
											&& list != null && list.size() > 0) {
										RemoteUserService
												.getInstance()
												.getTenantUsers(
														new DataManagerInterface() {

															@SuppressWarnings("unchecked")
															@Override
															public void getDataOnResult(
																	ResultStatus status,
																	List<?> list) {
																if (status
																		.getCode() == AnalysisManager.SUCCESS_DB_QUERY
																		&& list != null) {
																	mUserList
																			.clear();
																	mUserList
																			.addAll((ArrayList<User>) list);
																}
																UserCache.setUserLists(
																				mUserList);

																Message message = Message
																		.obtain();
																message.what = 0;
																mHandler.sendMessage(message);
															}
														},
														UserCache.getCurrentUser());
									}
								}
							}, userselect);
					mAddUserDialog.dismiss();
				}

				else {
					showToast(getString(R.string.mobile_input_all_info));
				}

			}
		});
	}

	@SuppressLint("UseSparseArrays")
	protected void initInfoCommonWindow() {
		Map<Integer, Integer> buttons = new HashMap<Integer, Integer>();

		buttons.put(3, BaseDialog.radioLineStyle);
		buttons.put(4, BaseDialog.calendarLineStyle);
		buttons.put(8, BaseDialog.editTextReadOnlyLineStyle);
		buttons.put(11, BaseDialog.OBSReadOnlyLineStyle);

		Map<Integer, String[]> widgetContent = new HashMap<Integer, String[]>();

		String[] radioContent = getResources().getStringArray(R.array.user_sex);

		widgetContent.put(3, radioContent);

		mInfoModifyDialog.init(R.array.user_info, buttons, widgetContent);

		for (int i = 0; i < 24; i++) {
			EditText editText = (EditText) mInfoModifyDialog.getPopupView()
					.findViewById(mInfoModifyDialog.baseEditTextId + i);
			if (editText != null) {

				switch (i) {
				case 0: // login_name
					editText.setInputType(InputType.TYPE_CLASS_TEXT
							| InputType.TYPE_CLASS_NUMBER);
				case 5: // office_tel
				case 7: // phone
				case 8: // mobile
					editText.setInputType(InputType.TYPE_CLASS_PHONE);
					break;
				case 9:
					editText.setInputType(InputType.TYPE_CLASS_TEXT
							| InputType.TYPE_TEXT_VARIATION_WEB_EMAIL_ADDRESS);
					break;
				default:
					break;
				}
			}
		}
		OnClickListener obsListener = new OnClickListener() {

			@Override
			public void onClick(View v) {
				Intent intent = new Intent();
				intent.setClass(getActivity(), ListSelectActivity.class);
				intent.putExtra(ListSelectActivity.FRAGMENT_KEY,
						OBSFragment.class);
				startActivityForResult(intent,
						UserManageFragment.MODIFY_OBS_SELECT_REQUEST);
			}
		};
		mInfoModifyDialog.setEditTextStyle(11, R.drawable.dialog_obs, obsListener,
				null);
		final Button saveImageView = (Button) mInfoModifyDialog.getPopupView()
				.findViewById(R.id.save_Button);

		saveImageView.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View arg0) {
				String[] names = getActivity().getResources().getStringArray(
						R.array.user_info);

				Map<String, String> saveData = mInfoModifyDialog.SaveData();
				Log.e("CEMP360", saveData.toString());
				User user = mUserList.get(mSelectedIndex);

				user.setLogin_name(saveData.get(names[0]));
				user.setPwd(saveData.get(names[1]));
				user.setName(saveData.get(names[2]));
				user.setSex(saveData.get(names[3]));
				user.setBirth(DateUtils.stringToDate(DateUtils.FORMAT_SHORT,
						saveData.get(names[4])));
				user.setOffice_phone(saveData.get(names[5]));
				user.setAddress(saveData.get(names[6]));
				user.setTel(saveData.get(names[7]));
				user.setMobile(saveData.get(names[0]));
				user.setEmail(saveData.get(names[9]));
				user.setEmail_pwd(saveData.get(names[10]));
				//user.setObs_id(Integer.parseInt(saveData.get(names[11])));
				if (!saveData.get(names[11]).isEmpty()) {
					user.setObs_id(Integer.parseInt(saveData.get(names[11])));
				}
								
				if (UtilTools.isMobileNO(saveData.get(names[0]))||user.getName().equals(getString(R.string.adminstrator))) {
					RemoteUserService.getInstance().updateUser(
							new DataManagerInterface() {

								@Override
								public void getDataOnResult(
										ResultStatus status, List<?> list) {
									// if (status.getCode() ==
									// AnalysisManager.SUCCESS_DB_UPDATE
									// && list != null)
									// mUserList = (ArrayList<User>) list;
								}
							}, user);

					// mAdapter.notifyDataSetChanged();
					RemoteUserService.getInstance().getTenantUsers(
							new DataManagerInterface() {

								@SuppressWarnings("unchecked")
								@Override
								public void getDataOnResult(
										ResultStatus status, List<?> list) {
									if (status.getCode() == AnalysisManager.SUCCESS_DB_QUERY
											&& list != null) {
										mUserList = (ArrayList<User>) list;
										Message message = Message.obtain();
										message.what = 0;
										mHandler.sendMessage(message);
									}
								}
							}, UserCache.getCurrentUser());
					mInfoModifyDialog.dismiss();
				} else {
					showToast(getString(R.string.mobile_input_all_info));
				}

			}
		});
	}

	@SuppressLint("UseSparseArrays")
	protected void initInfoCommonDialog() {
		Map<Integer, Integer> buttons = new HashMap<Integer, Integer>();

		for (int i = 0; i < 12; i++) {
			buttons.put(i, BaseDialog.editTextReadOnlyLineStyle);
		}

		mInfoDialog.init(R.array.user_info, buttons, null);
		mInfoDialog.setReadOnlyButtons();
	}

	private class RoleAdapter extends BaseAdapter {
		private ArrayList<Role> mArrayList;
		@SuppressWarnings("unused")
		private Context mContext;
		private LayoutInflater mInflater;

		/**
		 * CheckBox 是否选择的存储集合,key 是 position , value 是该position是否选中
		 */
		@SuppressLint("UseSparseArrays")
		private Map<String, Boolean> isCheckMap = new HashMap<String, Boolean>();

		public RoleAdapter(Context context, ArrayList<Role> list) {
			this.mContext = context;
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
				isCheckMap.put(mArrayList.get(i).getCode(), bool);
			}

		}

		/**
		 * 增加一项的时候
		 */
		// public void add(Role role) {
		// this.mArrayList.add(role);
		// // 让所有项目都为不选择
		// configCheckMap(false);
		// }

		// 移除一个项目的时候
		// public void remove(int position) {
		// this.mArrayList.remove(position);
		// }

		public Map<String, Boolean> getCheckMap() {
			return this.isCheckMap;
		}

		@Override
		public int getCount() {
			return mArrayList.size();
		}

		@Override
		public Object getItem(int position) {
			return mArrayList.get(position);
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
				convertView = mInflater.inflate(R.layout.user_role_item,
						parent, false);
				holder = new ViewHolder();
				holder.textView = (TextView) convertView
						.findViewById(R.id.role_item);
				holder.checkBox = (CheckBox) convertView
						.findViewById(R.id.role_checkBox);
				convertView.setTag(holder);
			} else {
				holder = (ViewHolder) convertView.getTag();
			}
			holder.textView.setText(mArrayList.get(position).getName());
			/*
			 * 设置单选按钮的选中
			 */
			holder.checkBox
					.setOnCheckedChangeListener(new OnCheckedChangeListener() {
						@Override
						public void onCheckedChanged(CompoundButton buttonView,
								boolean isChecked) {

							/*
							 * 将选择项加载到map里面寄存
							 */
							isCheckMap.put(mArrayList.get(position).getCode(),
									isChecked);
						}
					});

			if (isCheckMap.get(mArrayList.get(position).getCode()) == null) {
				isCheckMap.put(mArrayList.get(position).getCode(), false);
			}

			holder.checkBox.setChecked(isCheckMap.get(mArrayList.get(position)
					.getCode()));
			return convertView;

		}

		public class ViewHolder {
			public CheckBox checkBox;
			public TextView textView;
		}

	}

	protected void initRolePermissionsDialog(User selectUser) {
		mRolePermissionsDialog = new Dialog(getActivity(),
				R.style.MyDialogStyle);
		mRolePermissionsDialog.setContentView(R.layout.role_permissions_dialog);
		mRolePermissionsDialog.setCanceledOnTouchOutside(true);
		mRolePermissionsDialog.show();
		final ListView listView = (ListView) mRolePermissionsDialog
				.findViewById(R.id.role_permision_listView);
		final CheckBox select_box0 = (CheckBox) mRolePermissionsDialog
				.findViewById(R.id.select_checkBox);
		ImageView mClose  = (ImageView) mRolePermissionsDialog.findViewById(R.id.btn_close);
		listView.setAdapter(adapter);
		if (selectUser != null && selectUser.getRole() != null) {
			StringTokenizer st = new StringTokenizer(selectUser.getRole(), ",");
			adapter.configCheckMap(false);
			while (st.hasMoreTokens()) {
				String code = st.nextToken();
				adapter.getCheckMap().put(code, true);
			}
			adapter.notifyDataSetChanged();
		} else if (selectUser.getRole() == null) {
			adapter.configCheckMap(false);
			adapter.notifyDataSetChanged();
		}
		mClose.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				mRolePermissionsDialog.dismiss();
			}
		});
		listView.setOnItemClickListener(new OnItemClickListener() {

			@Override
			public void onItemClick(AdapterView<?> parent, View itemLayout,
					int position, long id) {
				if (itemLayout.getTag() instanceof RoleAdapter.ViewHolder) {

					RoleAdapter.ViewHolder holder = (RoleAdapter.ViewHolder) itemLayout
							.getTag();
					// 会自动触发CheckBox的checked事件
					holder.checkBox.toggle();

				}
			}
		});

		select_box0.setOnCheckedChangeListener(new OnCheckedChangeListener() {

			@Override
			public void onCheckedChanged(CompoundButton buttonView,
					boolean isChecked) {

				if (isChecked == true) {
					// 所有项目全部选中
					adapter.configCheckMap(true);
					adapter.notifyDataSetChanged();
				} else {
					// 所有项目全部不选中
					adapter.configCheckMap(false);
					adapter.notifyDataSetChanged();
				}
			}
		});

		Button OKButton = (Button) mRolePermissionsDialog
				.findViewById(R.id.role_permission_OK);
		Button exitButton = (Button) mRolePermissionsDialog
				.findViewById(R.id.role_exit);

		exitButton.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				mRolePermissionsDialog.dismiss();
			}
		});

		OKButton.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				// Auto-generated method stub
				StringBuilder sb = new StringBuilder();
				for (int i = 0; i < RoleCache.getRoleLists().size(); i++) {
					if (adapter.getCheckMap().get(RoleCache.getRoleLists().get(i).getCode()) == true) {
						sb.append(RoleCache.getRoleLists().get(i).getCode() + ",");
					}
				}

				String rolesString = "";
				if (sb.length() > 0) {
					rolesString = sb.substring(0, sb.length() - 1);
				}
				userselect.setRole(rolesString);
				if (userselect.getUser_id() != 0) {
					RemoteUserService.getInstance().updateUser(
							new DataManagerInterface() {

								@Override
								public void getDataOnResult(
										ResultStatus status, List<?> list) {
								}
							}, userselect);

					RemoteUserService.getInstance().getTenantUsers(
							new DataManagerInterface() {

								@SuppressWarnings("unchecked")
								@Override
								public void getDataOnResult(
										ResultStatus status, List<?> list) {
									if (status.getCode() == AnalysisManager.SUCCESS_DB_QUERY
											&& list != null) {
										mUserList = (ArrayList<User>) list;
										Message message = Message.obtain();
										message.what = 0;
										mHandler.sendMessage(message);
									}
								}
							}, UserCache.getCurrentUser());
					if (userselect.getUser_id() == UserCache
							.getCurrentUser().getUser_id()) {
						setSysPermissions();
					}
				} else {
					String user_role = userselect.getRole();
					String role_text = "";
					if (user_role != null && !user_role.equals("")) {
						String[] role = userselect.getRole().split(",");
						StringBuilder builder = new StringBuilder();
						for (String code : role) {
							builder.append(RoleCache
									.getRoleCodeMaps().get(code)
									+ ",");

						}
						role_text = builder.toString();
						if (role_text.endsWith(","))
							role_text = role_text.substring(0,
									role_text.length() - 1);
					}
					// roleEditText.setText(role_text);
					mAddUserDialog.setEditTextContent(4, role_text);
				}
				mRolePermissionsDialog.dismiss();

			}
		});
	}

	private void initOptionsMenuView() {
		String[] subMenuNames = getResources().getStringArray(
				R.array.user_options_menu);
		String[] subMenu2Names = getResources().getStringArray(
				R.array.user_options_menu2);
		if (!PermissionCache.hasSysPermission(USER_EDIT)) {
			subMenuNames = new String[] { subMenuNames[0] };
		}
		mOptionsMenuView = new OptionsMenuView(getActivity(), subMenuNames);
		mOptionsMenuView2 = new OptionsMenuView(getActivity(), subMenu2Names);
		mOptionsMenuView
				.setSubMenuListener(new OptionsMenuView.SubMenuListener() {
					@Override
					public void onSubMenuClick(View view) {
						switch ((Integer) view.getTag()) {
						case 0:
							showUserInfo();
							break;
						case 1:
							modifyUserInfo();
							break;
						case 2:
							assignRolePermision();
							break;
						case 3:
							commonConfirmDeleteUser();
							break;
						}
						mOptionsMenuView.dismiss();
					}
				});
		mOptionsMenuView2
		.setSubMenuListener(new OptionsMenuView.SubMenuListener() {
			@Override
			public void onSubMenuClick(View view) {
				switch ((Integer) view.getTag()) {
				case 0:
					showUserInfo();
					break;
				case 1:
					modifyUserInfo();
					break;
				}
				mOptionsMenuView2.dismiss();
			}
		});
	}

	private void commonConfirmDeleteUser() {
		// 创建确认删除对话框，并显示
		new AlertDialog.Builder(getActivity())
				// 设置对话框主体内容
				.setMessage(getResources().getString(R.string.confirm_delete))
				// 设置对话框标题
				.setTitle(getResources().getString(R.string.remind))
				// 为对话框按钮注册监听
				.setPositiveButton(getResources().getString(R.string.confirm),
						new DialogInterface.OnClickListener() {
							@Override
							public void onClick(DialogInterface dialog,
									int which) {
								// 首先隐藏对话框
								dialog.dismiss();
								deleteUser();
							}
						})
				.setNegativeButton(getResources().getString(R.string.cancel),
						new DialogInterface.OnClickListener() {
							@Override
							public void onClick(DialogInterface dialog,
									int which) {
								dialog.dismiss();
							}
						}).show();
	}

	private void modifyUserInfo() {

		String[] str = new String[24];
		User user = mUserList.get(mSelectedIndex);
		initInfoCommonWindow();
		str[0] = user.getLogin_name();
		// str[1] = user.getPwd();
		str[2] = user.getName();
		str[3] = user.getSex();
		str[4] = DateUtils
				.dateToString(DateUtils.FORMAT_SHORT, user.getBirth());
		str[5] = user.getOffice_phone();
		str[6] = user.getAddress();
		str[7] = user.getTel();
		str[8] = user.getMobile();
		str[9] = user.getEmail();
		// str[10] = user.getEmail_pwd();
		OBS obs = ObsCache.findObsById(user.getObs_id());
		if (obs != null)
			str[11] = Integer.toString(obs.getId());
		
		Button saveImageView = (Button) mInfoModifyDialog.getPopupView()
				.findViewById(R.id.save_Button);

		if (PermissionCache.hasSysPermission(USER_EDIT)) {// 管理员权限
			str[1] = user.getPwd();
			str[10] = user.getEmail_pwd();
			mInfoModifyDialog.show(str);
		} else if (PermissionCache.hasSysPermission(USER_VIEW)) {// 用户查看权限
			if (UserCache.getCurrentUser().getUser_id() == user
					.getUser_id()) {
				str[1] = user.getPwd();
				str[10] = user.getEmail_pwd();
			} else {
				str[1] = user.getPwd();
				str[10] = user.getEmail_pwd();
				EditText pwdEditText = (EditText) mInfoModifyDialog
						.getPopupView().findViewById(
								mInfoModifyDialog.baseEditTextId + 1);
				EditText emailPWDEditText = (EditText) mInfoModifyDialog
						.getPopupView().findViewById(
								mInfoModifyDialog.baseEditTextId + 10);
				pwdEditText.setInputType(InputType.TYPE_CLASS_TEXT
						| InputType.TYPE_TEXT_VARIATION_PASSWORD);
				emailPWDEditText.setInputType(InputType.TYPE_CLASS_TEXT
						| InputType.TYPE_TEXT_VARIATION_PASSWORD);
			}
			saveImageView.setEnabled(false);
			saveImageView.setBackgroundResource(R.drawable.button_disabled);
			mInfoModifyDialog.show(str);
		} else {// 无权限
			Toast.makeText(getActivity(),
					getString(R.string.no_user_permission), Toast.LENGTH_SHORT)
					.show();
		}

	}

	private void showUserInfo() {

		String[] str = new String[24];
		User user = mUserList.get(mSelectedIndex);
		initInfoCommonDialog();
		str[0] = user.getLogin_name();
		// str[1] = user.getPwd();
		str[2] = user.getName();
		str[3] = user.getSex();
		str[4] = DateUtils
				.dateToString(DateUtils.FORMAT_SHORT, user.getBirth());
		str[5] = user.getOffice_phone();
		str[6] = user.getAddress();
		str[7] = user.getTel();
		str[8] = user.getMobile();
		str[9] = user.getEmail();
		OBS obs = ObsCache.findObsById(user.getObs_id());
		if (obs != null)
			str[11] = obs.getName();

		if (PermissionCache.hasSysPermission(USER_EDIT)) {// 管理员权限
			str[1] = user.getPwd();
			str[10] = user.getEmail_pwd();
			mInfoDialog.show(str);
		} else if (PermissionCache.hasSysPermission(USER_VIEW)) {// 用户查看权限
			if (UserCache.getCurrentUser().getUser_id() == user
					.getUser_id()) {
				str[1] = user.getPwd();
				str[10] = user.getEmail_pwd();
			} else {
				str[1] = user.getPwd();
				str[10] = user.getEmail_pwd();
			}
			mInfoDialog.show(str);
		} else {// 无权限
			Toast.makeText(getActivity(),
					getString(R.string.no_user_permission), Toast.LENGTH_SHORT)
					.show();
		}

	}

	private void assignRolePermision() {
		if (PermissionCache.hasSysPermission(USER_EDIT)) {
			initRolePermissionsDialog(userselect);
		} else {
			Toast.makeText(getActivity(),
					getString(R.string.no_user_permission), Toast.LENGTH_SHORT)
					.show();
		}
	}

	private void deleteUser() {
		if (PermissionCache.hasSysPermission(USER_EDIT)) {
			final User user = mUserList.get(mSelectedIndex);
			RemoteUserService.getInstance().deleteUser(
					new DataManagerInterface() {
						@Override
						public void getDataOnResult(ResultStatus status,
								List<?> list) {
							if (status.getCode() == AnalysisManager.SUCCESS_DB_DEL) {
								listems.remove(mSelectedIndex);
								Message message = Message.obtain();
								message.what = 2;
								message.obj = user;
								mHandler.sendMessage(message);
							}
						}
					}, user);
		} else {
			Toast.makeText(getActivity(),
					getString(R.string.no_user_permission), Toast.LENGTH_SHORT)
					.show();
		}
	}
	private void dialPhoneNumber(String phoneNumber) {
	    Intent intent = new Intent(Intent.ACTION_DIAL);
	    intent.setData(Uri.parse("tel:" + phoneNumber));
	    if (intent.resolveActivity(getActivity().getPackageManager()) != null) {
	        startActivity(intent);
	    }
	}
	
	class UserAdapter extends SimpleAdapter {

		public UserAdapter(Context context,
				List<? extends Map<String, ?>> data, int resource,
				String[] from, int[] to) {
			super(context, data, resource, from, to);
		}

		@Override
		public View getView(final int position, View convertView,
				ViewGroup parent) {
			final int currentposition = position;
			View view = super.getView(position, convertView, parent);
			TextView serialTextView = (TextView) view.findViewById(R.id.serial_number);
			final TextView moblieTextView = (TextView) view.findViewById(R.id.mobile);
			ImageButton phoneImageButton = (ImageButton) view.findViewById(R.id.phone);
			serialTextView.setText(String.valueOf(position + 1));
			if(moblieTextView.getText()!=null && !moblieTextView.getText().equals("")){
				phoneImageButton.setVisibility(View.VISIBLE);
				phoneImageButton.setOnClickListener(new OnClickListener() {
					
					@Override
					public void onClick(View v) {
						dialPhoneNumber(moblieTextView.getText().toString());
					}
				});
			} else {
				phoneImageButton.setVisibility(View.GONE);
			}
			view.setOnClickListener(new View.OnClickListener() {

				@Override
				public void onClick(View view) {

					setSelected(currentposition, true);
					userselect = mUserList.get(position);
					// selectMap = listems.get(position);
					mSelectedIndex = position;
					if(userselect.getName().equals(getString(R.string.adminstrator))){
						mOptionsMenuView2.showAsDropDown(
								view,
								view.getMeasuredWidth() / 2,
								(-view.getMeasuredHeight() - UtilTools.dp2pxH(
										view.getContext(), 40)));	
					}else {
						mOptionsMenuView.showAsDropDown(
							view,
							view.getMeasuredWidth() / 2,
							(-view.getMeasuredHeight() - UtilTools.dp2pxH(
									view.getContext(), 40)));
					}
				}
			});

			if (mSelectedPosition.contains((Integer) position)) {
				view.setBackgroundResource(R.color.listview_selected_bg);
	        } else {
	            if (position % 2 == 1) {
	            	view.setBackgroundResource(R.color.content_listview_single_line_bg);
	            } else {
	            	view.setBackgroundColor(Color.TRANSPARENT);
	            }
	        }
			return view;
		}
	}

	private void loadUserData() {
		mUserList.clear();
		if (UserCache.isDataLoaded()) {
			for (User user : UserCache.getUserLists()) {
				mUserList.add(user);
			}
			Message message = Message.obtain();
			message.what = 0;
			mHandler.sendMessage(message);
		} else {
			RemoteUserService.getInstance().getTenantUsers(
					new DataManagerInterface() {

						@SuppressWarnings("unchecked")
						@Override
						public void getDataOnResult(ResultStatus status,
								List<?> list) {
							if (status.getCode() == AnalysisManager.SUCCESS_DB_QUERY
									&& list != null && list.size() > 0) {
								mUserList.addAll((ArrayList<User>) list);
								Message message = Message.obtain();
								message.what = 0;
								mHandler.sendMessage(message);
							}
						}
					}, UserCache.getCurrentUser());
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
						}
					}
				}, UserCache.getCurrentUser());
	}

	public void onActivityResult(int requestCode, int resultCode, Intent data) {
		if (resultCode == 0) {
			return;
		}
		if (requestCode == MODIFY_OBS_SELECT_REQUEST) {
			OBS obs = (OBS) data
					.getSerializableExtra(ListSelectActivity.RESULT_KEY);
			mInfoModifyDialog.setOBSTextContent(11, obs.getId());
		} else if (requestCode == ADD_OBS_SELECT_REQUEST) {
			OBS obs = (OBS) data
					.getSerializableExtra(ListSelectActivity.RESULT_KEY);
			mAddUserDialog.setOBSTextContent(3, obs.getId());
		}
	}
}
