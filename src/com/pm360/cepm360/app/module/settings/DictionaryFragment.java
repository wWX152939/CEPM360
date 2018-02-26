/**
 * 
 */
package com.pm360.cepm360.app.module.settings;

import android.annotation.SuppressLint;
import android.app.ActionBar.LayoutParams;
import android.content.Context;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemSelectedListener;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.PopupWindow;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.RadioGroup.OnCheckedChangeListener;
import android.widget.SimpleAdapter;
import android.widget.Spinner;
import android.widget.Toast;

import com.pm360.cepm360.R;
import com.pm360.cepm360.app.common.activity.DataManagerInterface;
import com.pm360.cepm360.app.common.view.FloatingMenuView;
import com.pm360.cepm360.app.common.view.parent.BaseDialog;
import com.pm360.cepm360.common.ResultStatus;
import com.pm360.cepm360.common.json.AnalysisManager;
import com.pm360.cepm360.entity.Dictionary;
import com.pm360.cepm360.services.system.RemoteDictionaryService;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * @author ouyanhua
 * 
 */
public class DictionaryFragment extends Fragment {
	private String TAG = "DictionaryFragment";
	private Context mContext = null;
	private View view;
	private ListView listView;
	private Spinner mSpinner;
	private BaseDialog mAddDictionaryDialog;
	private PopupWindow popupWindow;
	private RadioGroup radioGroup;
	private SimpleAdapter listAdapter;
	private List<Map<String, String>> listViewData = new ArrayList<Map<String, String>>();
	// private BuildFloatingMenu mBuildFloatingMenu;
	private List<Dictionary> listType = new ArrayList<Dictionary>();
	private List<Dictionary> listDict = new ArrayList<Dictionary>();
	int[] buttonResources = new int[] { R.drawable.dictionary,
	/* R.drawable.ic_action_video_light */};
	private String type = "d";
	EditText codeEditText;
	EditText nameEditText;

	ArrayList<String> mSpinnerList = new ArrayList<String>();
	
	@SuppressLint("HandlerLeak") 
	Handler mHandler = new Handler() {
		@Override
		public void handleMessage(Message msg) {

			switch (msg.what) {
			case 0:
				mSpinnerAdapter.notifyDataSetChanged();
				break;
			case 1:
				listAdapter.notifyDataSetChanged();
				break;
			case 3:
				getlistdata();
				break;
			default:
				break;
			}
		}

	};

	// private ArrayAdapter<String> adapter;
	/**
	 * 
	 */
	public DictionaryFragment() {
		// Auto-generated constructor stub
	}

	int dictionary_typeID;
	Dictionary dictionary1 = null;

	@SuppressWarnings("unused")
	private void createFloatingMenu() {
		int[] buttonResources = new int[] { R.drawable.dictionary, };

	}

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {
		// Auto-generated method stub
		mContext = container.getContext();
		view = inflater.inflate(R.layout.dictionary_fragment, container, false);
		mSpinner = (Spinner) view.findViewById(R.id.dictionary_spinner);
		listView = (ListView) view.findViewById(R.id.dictionary_listview);
		initialSpinner();
		mAddDictionaryDialog = new BaseDialog(getActivity(), getString(R.string.date_dictionary));
		initDictionaryCommonWindow();
		mSpinner.setOnItemSelectedListener(new OnItemSelectedListener() {

			@Override
			public void onItemSelected(AdapterView<?> parent, View view,
					int position, long id) {
				// Auto-generated method stub
				String string = parent.getItemAtPosition(position).toString();
				Dictionary dictionary = null;

				for (Dictionary dict : listType) {
					if (dict.getName().equals(string)) {
						dictionary = dict;
						dictionary1 = dict;
						dictionary_typeID = dictionary.getDictionary_id();
						break;
					}
				}

				if (dictionary != null) {
					RemoteDictionaryService.getInstance().getDictionary(
							new DataManagerInterface() {
								@SuppressWarnings({ "unchecked", "rawtypes" })
								@Override
								public void getDataOnResult(
										ResultStatus status, List list) {
									listViewData.clear();
									if (list != null
											&& status.getCode() == AnalysisManager.SUCCESS_DB_QUERY) {
										// Auto-generated method stub
										listDict = (List<Dictionary>) list;

										for (Dictionary d : listDict) {
											if (d instanceof Dictionary) {
												Dictionary dictionary = d;
												Map<String, String> map = new HashMap<String, String>();
												map.put("code",
														dictionary.getCode());
												map.put("dname",
														dictionary.getName());

												listViewData.add(map);
											}
										}
									}
									Log.i(TAG, "listViewData = " + listViewData);
									Message message = Message.obtain();
									message.what = 1;
									mHandler.sendMessage(message);

								}
							}, dictionary);
				}

				/*
				 * Toast.makeText(getActivity(), "" + string,
				 * Toast.LENGTH_SHORT) .show();
				 */
			}

			@Override
			public void onNothingSelected(AdapterView<?> parent) {
				// Auto-generated method stub

			}
		});
		// listViewData = myDataSource.getMaps();
		listAdapter = new SimpleAdapter(getActivity(), listViewData,
				R.layout.simple_adapter, new String[] { "code", "dname", },
				new int[] { R.id.code, R.id.dname, });
		listView.setAdapter(listAdapter);

		/*
		 * mBuildFloatingMenu = new BuildFloatingMenu(getActivity(),
		 * buttonResources);
		 * 
		 * mBuildFloatingMenu .setSubActionButtonListener(new
		 * BuildFloatingMenu.SubActionButtonListener() {
		 * 
		 * @Override public void onSubActionButtonClick(View view) { switch
		 * (view.getId()) { case 0: mAddDictionaryDialog.show(null); //
		 * showPopupWindow(view); break; case 1:
		 * mAddDictionaryDialog.show(null); // showPopupWindow(view); break;
		 * case 2: // showPopupWindow(view); break; default: break; } } });
		 */
		createFloatingMenu();
		final FloatingMenuView floatingMenuView = (FloatingMenuView) view
				.findViewById(R.id.floating_menu);
		floatingMenuView.addPopItem(getString(R.string.EPSMaintain_addeps), R.drawable.dictionary);
		floatingMenuView
				.setPopOnItemClickListener(new AdapterView.OnItemClickListener() {

					@Override
					public void onItemClick(AdapterView<?> parent, View view,
							int position, long id) {
						switch (position) {
						case 0:
							mAddDictionaryDialog.show(null);
							// showPopupWindow(view);
							floatingMenuView.dismiss();
							break;
						case 1:
							mAddDictionaryDialog.show(null);
							// showPopupWindow(view);
							floatingMenuView.dismiss();
							break;
						case 2:
							// showPopupWindow(view);
							break;
						default:
							break;
						}

					}
				});
		return view;
		// return super.onCreateView(inflater, container, savedInstanceState);
	}

	@SuppressLint("UseSparseArrays") 
	protected void initDictionaryCommonWindow() {
		Map<Integer, Integer> buttons = new HashMap<Integer, Integer>();
		buttons.put(0, BaseDialog.radioLineStyle);

		Map<Integer, String[]> widgetContent = new HashMap<Integer, String[]>();

		final String[] radioContent = new String[2];
		radioContent[0] = getString(R.string.type_dictionary);
		radioContent[1] = getString(R.string.dictionary);
		widgetContent.put(0, radioContent);

		mAddDictionaryDialog.init(R.array.dict_add, buttons, widgetContent);

		Button saveImageView = (Button) mAddDictionaryDialog.getPopupView()
				.findViewById(R.id.save_Button);
		saveImageView.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View arg0) {
				String[] names = getActivity().getResources().getStringArray(
						R.array.dict_add);

				Map<String, String> saveData = mAddDictionaryDialog.SaveData();
				if (!saveData.get(names[1]).equals("")
						&& !saveData.get(names[2]).equals("")) {
					if (saveData.get(names[0]) == radioContent[0]) {
						Dictionary dictionary = new Dictionary();
						dictionary.setCode(saveData.get(names[1]));
						dictionary.setName(saveData.get(names[2]));
						dictionary.setType("m");
						dictionary.setModule("m");
						RemoteDictionaryService.getInstance().addDictionary(
								new DataManagerInterface() {

									@SuppressWarnings("rawtypes")
									@Override
									public void getDataOnResult(
											ResultStatus status, List list) {
										if (status.getCode() == AnalysisManager.SUCCESS_DB_ADD)
											getData();
									}
								}, dictionary);
					} else if (saveData.get(names[0]) == radioContent[1]) {
						Dictionary dictionary = new Dictionary();
						dictionary.setCode(saveData.get(names[1]));
						dictionary.setName(saveData.get(names[2]));
						dictionary.setType("d");
						dictionary.setModule(Integer
								.toString(dictionary_typeID));
						RemoteDictionaryService.getInstance().addDictionary(
								new DataManagerInterface() {

									@SuppressWarnings("rawtypes")
									@Override
									public void getDataOnResult(
											ResultStatus status, List list) {
										if (status.getCode() == AnalysisManager.SUCCESS_DB_ADD) {
											Message message = Message.obtain();
											message.what = 3;
											mHandler.sendMessage(message);
										}
									}
								}, dictionary);
					}
					mAddDictionaryDialog.dismiss();
				} else {
					Toast toast = Toast.makeText(getActivity(),
							getString(R.string.dictionary_is_null), Toast.LENGTH_SHORT);
					toast.setGravity(Gravity.CENTER, 0, 0);
					toast.show();

				}
			}
		});
	}

	/*
	 * @Override public void onHiddenChanged(boolean hidden) { 
	 * Auto-generated method stub if (hidden) { if (mBuildFloatingMenu != null)
	 * mBuildFloatingMenu.detachMenu(); } else { if (mBuildFloatingMenu != null)
	 * mBuildFloatingMenu.attachMenu(getActivity()); }
	 * super.onHiddenChanged(hidden); }
	 */
	private void getData() {
		RemoteDictionaryService.getInstance().getModule(
				new DataManagerInterface() {
					@SuppressWarnings({ "unchecked", "rawtypes" })
					@Override
					public void getDataOnResult(ResultStatus status, List list) {
						if (list != null
								&& status.getCode() == AnalysisManager.SUCCESS_DB_QUERY) {
							mSpinnerList.clear();
							listType = (List<Dictionary>) list;
							for (Object o : list) {
								if (o instanceof Dictionary) {
									Dictionary dictionary = (Dictionary) o;
									mSpinnerList.add(dictionary.getName());
									Message message = Message.obtain();
									message.what = 0;
									mHandler.sendMessage(message);
								}
							}
						}

					}
				});
	}

	private void getlistdata() {
		if (dictionary1 != null) {
			RemoteDictionaryService.getInstance().getDictionary(
					new DataManagerInterface() {
						@SuppressWarnings({ "unchecked", "rawtypes" })
						@Override
						public void getDataOnResult(ResultStatus status,
								List list) {
							Log.i(TAG,
									" getDictionary state = "
											+ status.getCode());
							listViewData.clear();
							if (list != null
									&& status.getCode() == AnalysisManager.SUCCESS_DB_QUERY) {
								listDict = (List<Dictionary>) list;

								for (Dictionary d : listDict) {
									if (d instanceof Dictionary) {
										Dictionary dictionary = d;
										Map<String, String> map = new HashMap<String, String>();
										map.put("code", dictionary.getCode());
										map.put("dname", dictionary.getName());

										listViewData.add(map);
									}
								}
							}
							Log.i(TAG, "listViewData = " + listViewData);
							Message message = Message.obtain();
							message.what = 1;
							mHandler.sendMessage(message);

						}
					}, dictionary1);
		}
	}

	ArrayAdapter<String> mSpinnerAdapter;

	private void initialSpinner() {
		// 建立Adapter并且绑定数据源
		mSpinnerAdapter = new ArrayAdapter<String>(getActivity(),
				R.layout.spinner_item, mSpinnerList);
		// 绑定 Adapter到控件
		mSpinnerAdapter
				.setDropDownViewResource(R.layout.spinner_dropdown_item);
		mSpinner.setAdapter(mSpinnerAdapter);
		getData();
	}

	@SuppressWarnings("unused")
	private void showPopupWindow(View view) {

		// 一个自定义的布局，作为显示的内容
		View contentView = LayoutInflater.from(mContext).inflate(
				R.layout.dictionary_popwin, null);

		popupWindow = new PopupWindow(contentView, LayoutParams.WRAP_CONTENT,
				LayoutParams.WRAP_CONTENT, true);

		radioGroup = (RadioGroup) popupWindow.getContentView().findViewById(
				R.id.selectRadio);

		Button button = (Button) popupWindow.getContentView().findViewById(
				R.id.confirm_button);
		codeEditText = (EditText) popupWindow.getContentView().findViewById(
				R.id.edit_code);
		nameEditText = (EditText) popupWindow.getContentView().findViewById(
				R.id.edit_name);
		popupWindow.setBackgroundDrawable(getResources().getDrawable(
				R.drawable.page_background));

		// 设置好参数之后再show
		// popupWindow.showAsDropDown(view);
		// int[] location = new int[2];
		// view.getLocationOnScreen(location);

		popupWindow.showAtLocation(view, Gravity.NO_GRAVITY, 400, 500);

		radioGroup.setOnCheckedChangeListener(new OnCheckedChangeListener() {

			@Override
			public void onCheckedChanged(RadioGroup group, int checkedId) {

				for (int i = 0; i < radioGroup.getChildCount(); i++) {
					RadioButton radiobutton = (RadioButton) radioGroup
							.getChildAt(i);
					if (radiobutton.isChecked() && i == 1) {
						type = "d";
						/*
						 * Toast.makeText(getActivity(), "" +
						 * radiobutton.getText() + "type:" + type, 1) .show();
						 */

					} else {
						type = "m";
						/*
						 * Toast.makeText(getActivity(), "" +
						 * radiobutton.getText() + "type:" + type, 1) .show();
						 */
					}
				}
			}
		});

		button.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				String code = codeEditText.getText().toString();
				String name = nameEditText.getText().toString();

				if (type.equals("m")) {
					Dictionary dictionary = new Dictionary();
					dictionary.setCode(code);
					dictionary.setName(name);
					dictionary.setType(type);
					dictionary.setModule(type);
					RemoteDictionaryService.getInstance().addDictionary(
							new DataManagerInterface() {

								@SuppressWarnings("rawtypes")
								@Override
								public void getDataOnResult(
										ResultStatus status, List list) {
									if (status.getCode() == AnalysisManager.SUCCESS_DB_ADD)
										getData();
								}
							}, dictionary);
				} else if (type.equals("d")) {
					Dictionary dictionary = new Dictionary();
					dictionary.setCode(code);
					dictionary.setName(name);
					dictionary.setType(type);
					dictionary.setModule(Integer.toString(dictionary_typeID));
					RemoteDictionaryService.getInstance().addDictionary(
							new DataManagerInterface() {

								@SuppressWarnings("rawtypes")
								@Override
								public void getDataOnResult(
										ResultStatus status, List list) {
									if (status.getCode() == AnalysisManager.SUCCESS_DB_ADD) {
										Message message = Message.obtain();
										message.what = 3;
										mHandler.sendMessage(message);
									}
								}
							}, dictionary);
				}
				popupWindow.dismiss();
				type = "d";
			}
		});
	}

}
