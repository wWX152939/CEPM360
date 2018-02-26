package com.pm360.cepm360.app.common.view.parent;

import android.annotation.SuppressLint;
import android.app.ActionBar.LayoutParams;
import android.app.Activity;
import android.graphics.Color;
import android.graphics.drawable.Drawable;
import android.text.Spannable;
import android.text.SpannableStringBuilder;
import android.text.style.ForegroundColorSpan;
import android.util.TypedValue;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.View.OnTouchListener;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.MultiAutoCompleteTextView;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.RadioGroup.OnCheckedChangeListener;
import android.widget.Spinner;
import android.widget.TextView;

import com.pm360.cepm360.R;
import com.pm360.cepm360.app.cache.ObsCache;
import com.pm360.cepm360.app.cache.TenantCache;
import com.pm360.cepm360.app.cache.UserCache;
import com.pm360.cepm360.app.common.adpater.SemicolonTokenizer;
import com.pm360.cepm360.app.common.custinterface.BaseWidgetInterface;
import com.pm360.cepm360.app.common.view.NumberSeekBar;
import com.pm360.cepm360.app.module.common.plan.SelectOBSPathDialog;
import com.pm360.cepm360.app.module.common.plan.SelectOBSPathDialog.SubListListener;
import com.pm360.cepm360.common.util.DateUtils;
import com.pm360.cepm360.common.util.TwoNumber;
import com.pm360.cepm360.entity.OBS;
import com.pm360.cepm360.entity.Tenant;
import com.pm360.cepm360.entity.User;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

/*
 * popupWindow基类，布局文件在各自xml定义
 */
public abstract class BaseWidgetCore {

	// normal editText
	public static final Integer editTextReadOnlyLineStyle = 1;
	public static final Integer editTextClickLineStyle = 2;
	public static final Integer numberEditTextLineStyle = 3;
	public static final Integer decimalEditTextLineStyle = 4;
	public static final Integer remarkEditTextLineStyle = 5;
	public static final Integer autoCompleteTextViewStyle = 6;
	public static final Integer multiAutoCompleteTextViewStyle = 7;
	public static final Integer editTextNoBottomLineStyle = 8;
	
	// obs
	public static final Integer OBSSelectLineStyle = 18;
	public static final Integer OBSReadOnlyLineStyle = 19;
	
	// user
	public static final Integer userSelectLineStyle = 20;
	public static final Integer userReadOnlySelectLineStyle = 21;
	
	// tenant
	public static final Integer tenantSelectLineStyle = 22;
	public static final Integer tenantReadOnlySelectLineStyle = 23;
	
	// misc EditText
	public static final Integer localFileLineStyle = 34;

	// common click editText
	public static final Integer radioLineStyle = 101;
	public static final Integer checkboxLineStyle = 102;
	public static final Integer calendarLineStyle = 103;
	public static final Integer spinnerLineStyle = 104;
	public static final Integer numberSeekBarLineStyle = 105;

	// linearlayout id, if one line has three textView, then baseLineId + 1 is for fourth textView
	public final int baseLineId = 1000;

	// 用于区别反馈和编制
	public boolean readOnlyFlag = false;

	// the ids below belong to linearlayout
	public final int baseTextViewId = 1100;
	// baseEditTextId is for every line editText View
	public final int baseEditTextId = 1200;
	// baseLineStyleId is for special widget, like spinner radio button
	public final int baseLineStyleId = 1300;
	public final int radioButtonId = 2000;
	public final int checkBoxId = 3000;
	public final int localFileLineTag = 4000;
	public final int OBSLineTag = 4002;

	public static final int CAMERA_REQUEST_CODE = 1;
	public static final int IMAGE_REQUEST_CODE = 2;
	public static final int FILE_REQUEST_CODE = 4;
	

	protected View mBaseView;
	protected String[] mTextViewNames;
	protected Activity mActivity;

	protected Map<Integer, Integer> mButtons;
	protected Map<Integer, String[]> mWidgetContent;

	//default one line has one textView
	protected int mSplitLine = 1;
	protected boolean isWindowBg = false;

	/**-- 缓存 --*/
	@SuppressLint("UseSparseArrays") 
	protected Map<Integer, User> mUserCache = new HashMap<Integer, User>();
	@SuppressLint("UseSparseArrays") 
	protected Map<Integer, Tenant> mTenantCache = new HashMap<Integer, Tenant>();
	@SuppressLint("UseSparseArrays") 
	private Map<Integer, OBS> mOBSCache = new HashMap<Integer, OBS>();
	protected List<WidgetCache> widgetCacheData = new ArrayList<WidgetCache>();
	
	@SuppressLint("UseSparseArrays") 
	private Map<Integer, Drawable> mDrawableCache = new HashMap<Integer, Drawable>();
	
	private Map<Integer, String> mInitialDate;
	private Map<String, String> mSaveData;
	private int[] mKeys;
	private int mOBSId;
	
	/**-- 接口 --*/
	protected BaseWidgetInterface mBaseWidgetInterface;
	private WidgetInterface mWidgetInterface;
	
	protected void setLineVisible(int line, boolean visible) {
		LinearLayout l = (LinearLayout) mBaseView.findViewById(baseLineId + line);
		if (visible) {
			l.setVisibility(View.VISIBLE);
		} else {
			l.setVisibility(View.GONE);
		}
	}
	
	protected void setEditTextContent(int line, String content) {
		EditText et = (EditText) mBaseView
				.findViewById(baseEditTextId + line);
		
		et.setText(content);
	}
	
	protected void setReadOnlyLine(int line, boolean readOnlyMode, boolean showKeyBoard) {
		EditText et = (EditText) mBaseView
				.findViewById(baseEditTextId + line);
		
		if (readOnlyMode) {
			et.clearFocus();
			et.setFocusableInTouchMode(false);
			et.setFocusable(false);
			et.setLongClickable(false);
			et.setClickable(false);
			if (et.getCompoundDrawables() != null) {
				if (et.getCompoundDrawables()[2] != null) {
					mDrawableCache.put(line, et.getCompoundDrawables()[2]);
				}
			}
			et.setCompoundDrawables(null, null, null, null);
		} else {
			if (showKeyBoard) {
				et.requestFocus();
				et.setFocusableInTouchMode(true);
				et.setFocusable(true);
			}
			
			et.setLongClickable(false);
			et.setClickable(true);
			if (mDrawableCache.get(line) != null) {
				mDrawableCache.get(line).setBounds(0, 0, 30, 30);
				et.setCompoundDrawables(null, null, mDrawableCache.get(line), null);
			}
		}
	}
	
	/**
	 * 设置radio被选中
	 * @param line
	 * @param postion
	 */
	protected void setRadioButtonPosition(int line, String postion) {
		int baseRadioId = radioButtonId + line * 10;
		String[] radioContent = mWidgetContent.get(line);
		if (radioContent == null) {
			return;
		}
		
		for (int radioI = 0; radioI < radioContent.length; radioI++) {
			RadioButton rb = (RadioButton) mBaseView
					.findViewById(baseRadioId + radioI);

			if (rb.getText().toString().equals(postion)) {
				rb.setChecked(true);
			}
		}
	}
	
	protected void setWidgetInterface(WidgetInterface widgetInterface) {
		mWidgetInterface = widgetInterface;
	}
	
	protected void setTenantContent(int line, int tenantId) {
		EditText et = (EditText) mBaseView
				.findViewById(baseEditTextId + line);
		mTenantCache.put(line, TenantCache.findTenantById(tenantId));

		if (mTenantCache.get(line) != null) {
			et.setText(mTenantCache.get(line).getName());
		}
	}
	
	protected void setUserTextContent(int line, int id) {
		EditText et = (EditText) mBaseView
				.findViewById(baseEditTextId + line);
		mUserCache.put(line, UserCache.findUserById(id));
		
		if (mUserCache.get(line) != null) {
			et.setText(mUserCache.get(line).getName());
		}
	}
	
	protected void setOBSTextContent(int line, int id) {
		EditText et = (EditText) mBaseView
				.findViewById(baseEditTextId + line);
		mOBSCache.put(line, ObsCache.findObsById(id));
		
		if (mOBSCache.get(line) != null) {
			et.setText(mOBSCache.get(line).getName());
		}
	}
	
	protected EditText getEditTextView(int line) {
		return (EditText) mBaseView
				.findViewById(baseEditTextId + line);
	}
	
	private boolean checkEmptyString(String str) {

		if (str == null || ("").equals(str)) {
			return true;
		} else {
			return false;
		}
	}
	
	/**
	 * 
	 * @param editTexts can be null
	 */
	protected void SetDefaultValue(String[] editTexts) {
		boolean isAddStatus = false;
		if (null == editTexts) {
			isAddStatus = true;
			editTexts = new String[mTextViewNames.length];
		}
		boolean isNormalEditText;
		int firstEditTextId = 0;
		List<Integer> editTextIdList = new ArrayList<Integer>();
		boolean findFirstEditText = false;
		for (int i = 0; i < mTextViewNames.length; i++) {
			isNormalEditText = false;
			if (mButtons != null && mButtons.size() != 0) {
				for (int j = 0; j < mButtons.size(); j++) {
					if (i == mKeys[j]) {
						isNormalEditText = true;
						Integer lineStyle = mButtons.get(mKeys[j]);

						switch (lineStyle) {
						case 1:
							// editTextReadOnlyLineStyle
						case 8:
							// editTextNoBottomLineStyle
						case 2:
							// editTextClickLineStyle
						case 34:
							// localFile
							break;
						case 3:
						case 4:
						case 5:
						case 6:
						case 7:
							// edit
							editTextIdList.add(baseEditTextId + i);
							if (findFirstEditText == false) {
								firstEditTextId = baseEditTextId + i;
								findFirstEditText = true;
							}
							break;
						case 18:
						case 19:
							// obs
							if (!checkEmptyString(editTexts[i])) {
								mOBSCache.put(i, ObsCache.findObsById(Integer.parseInt(editTexts[i])));
								if (mOBSCache.get(i) != null) {
									editTexts[i] = mOBSCache.get(i).getName();
								} else {
									editTexts[i] = "";
								}
							}
							break;
						case 20:
						case 21:
							// user
							// show(null) 时为null
							if (!checkEmptyString(editTexts[i])) {
								mUserCache.put(i, UserCache.findUserById(Integer.parseInt(editTexts[i])));
								if (mUserCache.get(i) != null) {
									editTexts[i] = mUserCache.get(i).getName();
								} else {
									editTexts[i] = "";
								}
							}
							break;
						case 22:
						case 23:
							// tenant
							if (!checkEmptyString(editTexts[i])) {
								mTenantCache.put(i, TenantCache.findTenantById(Integer.parseInt(editTexts[i])));
								if (mTenantCache.get(i) != null) {
									editTexts[i] = mTenantCache.get(i).getName();
								} else {
									editTexts[i] = "";
								}
							}
							break;

						case 101:
							// radioLineStyle
							final String[] radioContent = mWidgetContent
								.get(mKeys[j]);
							final int baseRadioId = radioButtonId + i * 10;
							for (int radioI = 0; radioI < radioContent.length; radioI++) {
								RadioButton rb = (RadioButton) mBaseView
										.findViewById(baseRadioId + radioI);
		
								if (rb.getText().toString()
										.equals(editTexts[i])) {
									rb.setChecked(true);
								}
							}
							break;
						case 102:
							// checkboxLineStyle
							final int baseCheckBoxId = checkBoxId + i * 10;
							final String[] checkBoxContent = mWidgetContent
									.get(mKeys[j]);
							String[] checkBoxText = null;
							if (editTexts[i] != null)
								checkBoxText = editTexts[i].split("#");
							for (int checkBoxI = 0; checkBoxI < checkBoxContent.length; checkBoxI++) {

								CheckBox cb = (CheckBox) mBaseView
										.findViewById(baseCheckBoxId
												+ checkBoxI);
								cb.setChecked(false);
								if (checkBoxText != null
										&& cb.getText()
												.toString()
												.equals(checkBoxText[checkBoxI]))
									cb.setChecked(true);
							}
							break;
						case 103:
							// calendarLineStyle
							mInitialDate.put(i, editTexts[i]);
							break;
						case 104:
							// spinnerLineStyle
							Spinner sp = (Spinner) mBaseView
								.findViewById(baseLineStyleId + i);
							String[] spinnerContent = mWidgetContent
									.get(mKeys[j]);
							int spinner_i = 0;
							
							for (spinner_i = 0; spinner_i < spinnerContent.length; spinner_i++) {
								if (spinnerContent[spinner_i]
										.equals(editTexts[i])) {
									sp.setSelection(spinner_i);
									break;
								}
							}
							if (spinner_i == spinnerContent.length) {
								sp.setSelection(0);
							}
							break;
						case 105:
							// numberSeekBarLineStyle
							final NumberSeekBar seekBar = (NumberSeekBar) mBaseView
									.findViewById(baseLineStyleId + i);
							if (editTexts[i] != null && !editTexts[i].isEmpty()) {
								seekBar.setProgress(Integer.parseInt(editTexts[i]));
							}
							break;
							
						}
						
						if (lineStyle != numberSeekBarLineStyle
								&& lineStyle != spinnerLineStyle
								&& lineStyle != checkboxLineStyle
								&& lineStyle != radioLineStyle) {
							EditText et = (EditText) mBaseView
									.findViewById(baseEditTextId + i);
							if (!checkEmptyString(editTexts[i])) {
								et.setText(editTexts[i]);
							} else {
								et.setText("");
							}
						}
					}
				}
			}
			if (isNormalEditText == false) {
				EditText et = (EditText) mBaseView
						.findViewById(baseEditTextId + i);
				editTextIdList.add(baseEditTextId + i);
				if (findFirstEditText == false) {
					firstEditTextId = baseEditTextId + i;
					findFirstEditText = true;
				}
				if (editTexts[i] == null || editTexts[i].equals("0")) {
					et.setText(null);
				} else {
					et.setText(editTexts[i]);
				}

			}

		}
		if (findFirstEditText && !readOnlyFlag) {
			final EditText et = (EditText) mBaseView.findViewById(firstEditTextId);
			et.setSelection(et.getText().length());
			
			et.setFocusable(true);
			et.setFocusableInTouchMode(true);
			et.requestFocus();
			et.requestFocusFromTouch();

			setInputMode(isAddStatus, editTextIdList);
		}
	}
	
	/**
	 * 修改模式，默认不弹出输入框，增加模式弹出输入框
	 * @param isAddStatus
	 * @param editTextIdList
	 */
	protected void setInputMode(boolean isAddStatus, List<Integer> editTextIdList) {
		if (!isAddStatus) {
			for (int id : editTextIdList) {
				final EditText editText = (EditText) mBaseView.findViewById(id);
				editText.setFocusableInTouchMode(false);
				editText.setOnTouchListener(new OnTouchListener(){
					@Override
					public boolean onTouch(View v, MotionEvent event) {
						editText.setFocusableInTouchMode(true);
						return false;
					}  
				});
			}
		} else {
			for (int id : editTextIdList) {
				EditText editText = (EditText) mBaseView.findViewById(id);
				editText.setFocusableInTouchMode(true);
			}
		}
	}

	/**
	 * saveData interface
	 * @return
	 */
	protected Map<String, String> SaveData() {
		mSaveData = new HashMap<String, String>();
		boolean editTextFlag;
		for (int i = 0; i < mTextViewNames.length; i++) {

			editTextFlag = false;
			if (mButtons != null && mButtons.size() != 0) {
				for (int j = 0; j < mButtons.size(); j++) {
					if (i == mKeys[j]) {
						editTextFlag = true;
						Integer lineStyle = mButtons.get(mKeys[j]);
						String retString = "";
						switch (lineStyle) {
						case 1:
							// editTextReadOnlyLineStyle
						case 8:
							// editTextNoBottomLineStyle
						case 2:
							// editTextClickLineStyle
						case 34:
							// localFile
						case 3:
						case 4:
						case 5:
						case 6:
						case 7:
							// edit
							TextView tv = (TextView) mBaseView
								.findViewById(baseEditTextId + i);
							retString = tv.getText().toString();
							break;
						case 18:
						case 19:
							// obs
							if (mOBSId == 0) {
								if (mOBSCache.get(i) != null) {
									retString = Integer.toString(mOBSCache.get(i).getId());
								}
							} else {
								retString = Integer.toString(mOBSId);
							}
							break;
						case 20:
						case 21:
							// user
							EditText et = (EditText) mBaseView
									.findViewById(baseEditTextId + i);
							if (!et.getText().toString().isEmpty()) {
								// setDefaultData and setEditContent will set userCache
								if (mUserCache.get(i) != null) {
									retString = Integer.toString(mUserCache.get(i).getUser_id());
								}	
							}
							break;
						case 22:
						case 23:
							// tenant
							et = (EditText) mBaseView
									.findViewById(baseEditTextId + i);
							if (!et.getText().toString().isEmpty()) {
								// setDefaultData and setEditContent will set userCache
								if (mTenantCache.get(i) != null) {
									retString = Integer.toString(mTenantCache.get(i).getTenant_id());
								}	
							}
							break;
						case 101:
							// radioLineStyle
							final String[] radioContent = mWidgetContent
								.get(mKeys[j]);
							final int baseRadioId = radioButtonId + i * 10;
							for (int radioI = 0; radioI < radioContent.length; radioI++) {
								RadioButton rb = (RadioButton) mBaseView
										.findViewById(baseRadioId + radioI);
								if (rb.isChecked()) {
									retString = rb.getText().toString();
									break;
								}
							}
							break;
						case 102:
							// checkboxLineStyle
							final int baseCheckBoxId = checkBoxId + i * 10;
							final String[] checkBoxContent = mWidgetContent
									.get(mKeys[j]);
							String checkBoxText = "";

							for (int checkBoxI = 0; checkBoxI < checkBoxContent.length; checkBoxI++) {

								CheckBox cb = (CheckBox) mBaseView
										.findViewById(baseCheckBoxId
												+ checkBoxI);

								if (cb.isChecked()) {
									checkBoxText += cb.getText().toString();
								} else {
									checkBoxText += "";
								}
								if (checkBoxI != (checkBoxContent.length - 1))
									checkBoxText += "#";
							}
							retString = checkBoxText;
							break;
						case 103:
							// calendarLineStyle
							tv = (TextView) mBaseView
								.findViewById(baseEditTextId + i);
							retString = tv.getText().toString();
							break;
						case 104:
							// spinnerLineStyle
							Spinner sp = (Spinner) mBaseView
									.findViewById(baseLineStyleId + i);
							if (sp.getSelectedItem() != null) {
								retString = sp.getSelectedItem().toString();
							}
							break;
						case 105:
							// numberSeekBarLineStyle
							final NumberSeekBar seekBar = (NumberSeekBar) mBaseView
									.findViewById(baseLineStyleId + i);
							retString = Integer.toString(seekBar.getProgress());
							break;
						}
						
						if (!checkEmptyString(retString)) {
							mSaveData.put(mTextViewNames[i], retString);
						} else {
							mSaveData.put(mTextViewNames[i], "");
						}
						break;
						
					}
				}
			}
			if (editTextFlag == false) {
				EditText et = (EditText) mBaseView
						.findViewById(baseEditTextId + i);
				mSaveData.put(mTextViewNames[i], et.getText().toString());
			}

		}

		return mSaveData;
	}

	protected void showDialogWindow(final Activity activity,
			final EditText editText, int i) {
		final Date date = DateUtils.stringToDate(DateUtils.FORMAT_SHORT, mInitialDate.get(i));
		final CEPMDatePickerDialog dialog = new CEPMDatePickerDialog(mActivity, editText, mActivity.getString(R.string.select_time));
		editText.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				dialog.show(date);
			}
			
		});
	}

	private void showOBSPathhDialog(View arg0) {
		SelectOBSPathDialog selectOBS = new SelectOBSPathDialog(mActivity);
		selectOBS.initTreeView();
		selectOBS.setSubListListener(new SubListListener() {

			@Override
			public void onSubTreeClick(String OBSObject) {
				EditText OBSFilePath = (EditText) mBaseView
						.findViewWithTag(OBSLineTag);
				String[] obs = OBSObject.split("#");
				mOBSId = Integer.parseInt(obs[0]);
				OBSFilePath.setText(obs[1]);
			}
		});
	}
	
	@SuppressWarnings("unused")
	private void setLayoutParams(LinearLayout.LayoutParams layoutParams,
			int flag) {
		if (spinnerLineStyle == flag) {
			layoutParams.leftMargin = mActivity.getResources()
					.getDimensionPixelSize(R.dimen.popup_window_et_margin_left);
		} else if (calendarLineStyle == flag) {
			layoutParams.leftMargin = mActivity.getResources()
					.getDimensionPixelSize(
							R.dimen.popup_window_image_margin_left);
		} else if ((radioLineStyle == flag) || (checkboxLineStyle == flag)) {
			layoutParams.leftMargin = mActivity.getResources()
					.getDimensionPixelSize(R.dimen.popup_window_et_margin_left);
			layoutParams.gravity = Gravity.CENTER_HORIZONTAL;
		}

	}

	/**-- backgroundStyle --*/
	protected final int spinnerETBgStyle = 1;
	protected final int calendarETBgStyle = 2;
	protected final int OBSETBgStyle = 3;
	protected final int userETBgStyle = 4;
	protected final int tenantETBgStyle = 5;
	protected final int noLineBgStyle = 6;

	/**-- style --*/
	protected final int EtNormalStyle = 0;
	protected final int EtReadOnlyStyle = 1;
	protected final int EtNumStyle = 2;
	protected final int EtDecimalStyle = 3;
	/**
	 * 
	 * @param style 
	 * @param line1
	 * @param i
	 * @param backgroundStyle 
	 * @return
	 */
	protected EditText createEditText(int style, LinearLayout parentLine, int i, int backgroundStyle) {
		EditText editText = null;
		
		if (style == EtReadOnlyStyle) {
			// readOnly mode
			editText = (EditText) LayoutInflater.from(mActivity).inflate(
					R.layout.base_dialog_edit_text, parentLine, false);
			editText.clearFocus();
			editText.setFocusableInTouchMode(false);
			editText.setFocusable(false);
			editText.setLongClickable(false);
		} else {
			switch (style) {
			case EtNumStyle:
				// num
				editText = (EditText) LayoutInflater.from(mActivity).inflate(
						R.layout.base_dialog_number_edit_text, parentLine, false);
				break;
			case EtNormalStyle:
				// normal
				editText = (EditText) LayoutInflater.from(mActivity).inflate(
						R.layout.base_dialog_edit_text, parentLine, false);
				break;
			case EtDecimalStyle:
				// decimal
				editText = (EditText) LayoutInflater.from(mActivity).inflate(
						R.layout.base_dialog_decimal_edit_text, parentLine, false);
				break;
			}
			
			editText.setHint(mActivity.getString(R.string.pls_input)
						+ mTextViewNames[i]);
//			editText.setFocusableInTouchMode(true);
//			editText.requestFocus();
		}
		
		// set background action must before set padding
		if (0 != backgroundStyle) {
			Drawable drawable = null;
			switch (backgroundStyle) {
			case spinnerETBgStyle:
				drawable = mActivity.getResources()
						.getDrawable(
								R.drawable.dialog_spinner_bg);
				break;
			case calendarETBgStyle:
				if (isWindowBg) {
					drawable = mActivity.getResources()
							.getDrawable(
									R.drawable.calendar_windows);
				} else {
					drawable = mActivity
							.getResources()
							.getDrawable(R.drawable.dialog_calendar);
				}
				break;
			case OBSETBgStyle:
				if (isWindowBg) {
					drawable = mActivity.getResources()
							.getDrawable(R.drawable.windows_obs);
				} else {
					drawable = mActivity.getResources()
							.getDrawable(R.drawable.dialog_obs);
				}
				
				break;
			case userETBgStyle:
				drawable = mActivity.getResources()
					.getDrawable(R.drawable.icon_user_select);
				break;
			case tenantETBgStyle:
				drawable = mActivity.getResources()
					.getDrawable(R.drawable.icon_tenant_select);
				break;
			}
			if (spinnerETBgStyle == backgroundStyle || noLineBgStyle == backgroundStyle) {
				editText.setBackground(drawable);
			} else {
				// 这一步必须要做,否则不会显示.
				drawable.setBounds(0, 0, 30, 30);
				editText.setCompoundDrawables(null, null,
						drawable, null);
			}
		}  
		
		setEditTextStyle(editText);
		editText.setGravity(Gravity.CENTER_VERTICAL);
		editText.setId(baseEditTextId + i);

		if (isWindowBg) {
			editText.setTextColor(mActivity.getResources().getColor(
					R.color.task_window_text));
		} else {
			editText.setTextColor(mActivity.getResources().getColor(
					R.color.task_dialog_text));
		}

		return editText;
	}
	
	private EditText createAndAddEditText(int editTextType, int backgroundType, int i, LinearLayout.LayoutParams etParams,
			LinearLayout line) {
		EditText editText = createEditText(editTextType,
				line, i, backgroundType);
		etParams = getEditTextParams(i);
		
		line.addView(editText, etParams);
		return editText;
	}
	
	@SuppressWarnings("deprecation")
	@SuppressLint("UseSparseArrays")
	private void initSubWidget(Map<Integer, Integer> buttons, Map<Integer, String[]> widgetContent
			, int i, LinearLayout line1, TextView textView) {
		mInitialDate = new HashMap<Integer, String>();
		boolean editTextFlag = false;
		if (buttons != null && buttons.size() != 0) {
			for (int j = 0; j < buttons.size(); j++) {
				if (i == mKeys[j]) {
					editTextFlag = true;
					Integer lineStyle = buttons.get(mKeys[j]);
					EditText editText = null;
					LinearLayout.LayoutParams etParams = null;
					switch (lineStyle) {
					case 1:
					case 19: // obsReadOnly
					case 21: // userReadOnly
					case 23: // tenant
						// editTextReadOnlyLineStyle
						createAndAddEditText(EtReadOnlyStyle, 0, i, etParams, line1);
						break;
					case 8:
						// editTextNoBottomLineStyle
						createAndAddEditText(EtReadOnlyStyle, noLineBgStyle, i, etParams, line1);
						break;
					case 2:
						// editTextClickLineStyle
						createAndAddEditText(EtReadOnlyStyle, spinnerETBgStyle, i, etParams, line1);
						break;
					case 20:
						// user
						createAndAddEditText(EtReadOnlyStyle, userETBgStyle, i, etParams, line1);
						break;
					case 22: 
						// tenant
						createAndAddEditText(EtReadOnlyStyle, tenantETBgStyle, i, etParams, line1);
						break;
					case 3:
						// numberEditTextLineStyle
						createAndAddEditText(EtNumStyle, 0, i, etParams, line1);
						break;
					case 4:
						// decimalEditTextLineStyle
						createAndAddEditText(EtDecimalStyle, 0, i, etParams, line1);
						break;
					case 5:
						// remarkEditTextLineStyle
						editText = createEditText(EtNormalStyle,
								line1, i, 0);
						editText.setGravity(Gravity.TOP);
						editText.setBackgroundDrawable(mActivity.getResources().getDrawable(R.drawable.combination_edit_text_bg));

						etParams = getMarkEditTextParams(editText);

						textView.setGravity(Gravity.TOP);
						LinearLayout.LayoutParams tvParams = (android.widget.LinearLayout.LayoutParams) textView.getLayoutParams();
						int pxH = mActivity.getResources().getDimensionPixelSize(R.dimen.dp8_h);
						tvParams.topMargin = pxH;
						textView.setLayoutParams(tvParams);
						
						line1.addView(editText, etParams);
						// edit
						break;
					case 18:
						// obs
						editText = createAndAddEditText(EtReadOnlyStyle, OBSETBgStyle, i, etParams, line1);

						editText.setTag(OBSLineTag);

						editText.setOnClickListener(new OnClickListener() {

							@Override
							public void onClick(View arg0) {
								showOBSPathhDialog(arg0);
							}
						});

						break;
					case 103:
						// calendarLineStyle
						// 创建editTextView对象
						editText = createAndAddEditText(EtReadOnlyStyle, calendarETBgStyle, i, etParams, line1);

						showDialogWindow(mActivity, editText, i);
						
						break;
					case 34:
						// localFile
						editText = createEditText(EtReadOnlyStyle,
								line1, i, spinnerETBgStyle);

						editText.setTag(localFileLineTag);
						etParams = new LinearLayout.LayoutParams(
								mActivity
										.getResources()
										.getDimensionPixelSize(
												R.dimen.popup_window_localfile_et_width),
								LayoutParams.WRAP_CONTENT);
						int pxW = mActivity.getResources().getDimensionPixelSize(R.dimen.dp8_w);
						pxH = mActivity.getResources().getDimensionPixelSize(R.dimen.dp8_h);
						etParams.setMargins(pxW, pxH, pxW, pxH);

						line1.addView(editText, etParams);

						final Button bt = (Button) LayoutInflater.from(
								mActivity).inflate(
								R.layout.base_dialog_button, line1, false);
						bt.setId(baseLineStyleId + i);
						bt.setText(R.string.pls_select);

						line1.addView(bt);
						break;

					case 6:
						// autoCompleteTextViewStyle
						if (widgetContent.containsKey(mKeys[j])) {
							final String[] autoCompleteContent = widgetContent
									.get(mKeys[j]);
							ArrayAdapter<String> aa = new ArrayAdapter<String>(
									mActivity,
									R.layout.autocompletetextview_type,
									autoCompleteContent);
							final AutoCompleteTextView actv = (AutoCompleteTextView) LayoutInflater
									.from(mActivity)
									.inflate(
											R.layout.base_dialog_autocomplete_tv,
											line1, false);
							LinearLayout.LayoutParams actvParams1 = getEditTextParams(i);
							actv.setAdapter(aa);
							actv.setTextSize(TypedValue.COMPLEX_UNIT_PX, getDimen(16));
							if (isWindowBg) {
								actv.setTextColor(Color.WHITE);
							} else {
								actv.setTextColor(Color.BLACK);
							}
							actv.setBackground(mActivity.getResources()
									.getDrawable(R.drawable.bg_edittext));
							actv.setHint(mActivity
									.getString(R.string.pls_input)
									+ mTextViewNames[i]);
							actv.setId(baseEditTextId + i);
							actv.setThreshold(1);
							actv.setOnClickListener(new OnClickListener() {

								@Override
								public void onClick(View v) {
									actv.showDropDown();
								}
							});

							line1.addView(actv, actvParams1);
						}
						break;
					case 7:
						// multiAutoCompleteTextViewStyle 
						if (widgetContent.containsKey(mKeys[j])) {
							final String[] autoCompleteContent = widgetContent
									.get(mKeys[j]);

							ArrayAdapter<String> aa = new ArrayAdapter<String>(
									mActivity,
									R.layout.autocompletetextview_type,
									autoCompleteContent);
							MultiAutoCompleteTextView mtactv = (MultiAutoCompleteTextView) LayoutInflater
									.from(mActivity)
									.inflate(
											R.layout.base_dialog_multiautocomplete_tv,
											line1, false);
							LinearLayout.LayoutParams actvParams1 = getEditTextParams(i);
							mtactv.setAdapter(aa);
							mtactv.setTextSize(TypedValue.COMPLEX_UNIT_PX, getDimen(16));
							if (isWindowBg) {
								mtactv.setTextColor(Color.WHITE);
							} else {
								mtactv.setTextColor(Color.BLACK);
							}
							mtactv.setBackground(mActivity.getResources()
									.getDrawable(R.drawable.bg_edittext));
							mtactv.setHint(mActivity
									.getString(R.string.pls_input)
									+ mTextViewNames[i]);
							mtactv.setId(baseEditTextId + i);
							mtactv.setThreshold(1);
							mtactv.setTokenizer(new SemicolonTokenizer(','));

							line1.addView(mtactv, actvParams1);
						}
						break;
					case 101:
						// radioLineStyle
						if (widgetContent.containsKey(mKeys[j])) {
							final String[] radioContent = widgetContent
									.get(mKeys[j]);
							final int baseRadioId = radioButtonId + i * 10;
							RadioGroup rg = new RadioGroup(mActivity);
							rg.setId(baseLineStyleId + i);
							LinearLayout.LayoutParams rgParams1 = new LinearLayout.LayoutParams(
									LayoutParams.WRAP_CONTENT,
									LayoutParams.WRAP_CONTENT);

							// setLayoutParams(rgParams1, radioLineStyle);
							rg.setOrientation(LinearLayout.HORIZONTAL);
							rg.setGravity(Gravity.CENTER_VERTICAL);
//							rg.setBackground(mActivity.getResources()
//									.getDrawable(R.drawable.bg_edittext));
							pxW = mActivity.getResources().getDimensionPixelSize(R.dimen.dp8_w);
							pxH = mActivity.getResources().getDimensionPixelSize(R.dimen.dp8_h);
							rgParams1.setMargins(pxW, pxH, pxW, pxH);

							for (int radio_i = 0; radio_i < radioContent.length; radio_i++) {
								RadioButton rb = new RadioButton(mActivity);
								rb.setText(radioContent[radio_i]);
								rb.setTextSize(TypedValue.COMPLEX_UNIT_PX, mActivity.getResources().getDimensionPixelSize(R.dimen.sp16_s));
								rb.setId(baseRadioId + radio_i);
								if (isWindowBg) {
									rb.setTextColor(Color.WHITE);
								} else {
									rb.setTextColor(Color.BLACK);
								}
								// rb.setBackgroundResource(R.drawable.radiobutton);
								rb.setButtonDrawable(R.drawable.radiobutton);
								rb.setChecked(false);
								rb.setPadding(pxW, pxH, pxW, pxH);
								rg.addView(rb);
							}
							final int line = i;
							rg.setOnCheckedChangeListener(new OnCheckedChangeListener() {
								
								@Override
								public void onCheckedChanged(RadioGroup group, int checkedId) {
									
									if (mWidgetInterface != null) {
										mWidgetInterface.setRadioButtonChangeListener(line, checkedId - baseRadioId);
									}
								}
							});

							line1.addView(rg, rgParams1);
						}
						break;
					case 102:
						// checkboxLineStyle
						if (widgetContent.containsKey(mKeys[j])) {
							final String[] checkboxContent = widgetContent
									.get(mKeys[j]);
							final int baseCheckBoxId = checkBoxId + i * 10;

							LinearLayout.LayoutParams cbParams1 = new LinearLayout.LayoutParams(
									LayoutParams.WRAP_CONTENT,
									LayoutParams.WRAP_CONTENT);
							pxW = mActivity.getResources().getDimensionPixelSize(R.dimen.dp8_w);
							pxH = mActivity.getResources().getDimensionPixelSize(R.dimen.dp8_h);
							int pxWR = getCheckBoxMarginRightParams(pxW);
							cbParams1.setMargins(pxW, pxH, pxWR, pxH);

							for (int cb_i = 0; cb_i < checkboxContent.length; cb_i++) {
								final CheckBox cb = new CheckBox(mActivity);
								cb.setText(checkboxContent[cb_i]);
								cb.setId(baseCheckBoxId + cb_i);
								cb.setGravity(Gravity.CENTER);
								cb.setButtonDrawable(R.drawable.checkbox_bg);
								if (isWindowBg) {
									cb.setTextColor(Color.WHITE);
								} else {
									cb.setTextColor(Color.BLACK);
								}
								cb.setPadding(mActivity.getResources().getDimensionPixelSize(R.dimen.dp5_w),
										mActivity.getResources().getDimensionPixelSize(R.dimen.dp5_h),
										mActivity.getResources().getDimensionPixelSize(R.dimen.dp20_w), 
										mActivity.getResources().getDimensionPixelSize(R.dimen.dp5_h));
								line1.addView(cb, cbParams1);
							}

						}

						break;
					case 104:
						// spinnerLineStyle
						if (widgetContent.containsKey(mKeys[j])) {
							final String[] spinnerContent = widgetContent
									.get(mKeys[j]);

							LinearLayout.LayoutParams spParams1 = getEditTextParams(i);
							// setLayoutParams(spParams1, spinnerLineStyle);

							final Spinner sp = new Spinner(mActivity);
							sp.setId(baseLineStyleId + i);
							sp.setGravity(Gravity.CENTER);
							// 创建ArrayAdapter对象
							ArrayAdapter<String> adapter;
							if (isWindowBg) {
								adapter = new ArrayAdapter<String>(
										mActivity, R.layout.spinner_item,
										spinnerContent);
							} else {
								adapter = new ArrayAdapter<String>(
										mActivity,
										R.layout.spinner_item_dialog,
										spinnerContent);
							}
							// 为Spinner设置Adapter
							adapter.setDropDownViewResource(R.layout.spinner_dropdown_item);
							sp.setAdapter(adapter);

							sp.setBackground(mActivity.getResources()
									.getDrawable(
											R.drawable.dialog_spinner_bg));

							pxW = mActivity.getResources().getDimensionPixelSize(R.dimen.dp8_w);
							pxH = mActivity.getResources().getDimensionPixelSize(R.dimen.dp8_h);
							sp.setPadding(0, pxH, pxW, pxH);

							line1.addView(sp, spParams1);

						}
						break;
					case 105:
						// numberSeekBarLineStyle
						final NumberSeekBar numberSeekBar = (NumberSeekBar) LayoutInflater.from(mActivity).inflate(
								R.layout.base_dialog_seek_bar, line1, false);
						numberSeekBar.setId(baseLineStyleId + i);
						LinearLayout.LayoutParams params = getSeekBarParams();
						line1.addView(numberSeekBar, params);
						break;
						
					}
					
				}
			}
		}
		if (false == editTextFlag) {
			// 创建editTextView对象
			final EditText editText;

			if (readOnlyFlag == false) {
				editText = createEditText(EtNormalStyle, line1, i, 0);
			} else {
				editText = createEditText(EtReadOnlyStyle, line1, i, 0);
				editText.setBackground(null);
			}

			LinearLayout.LayoutParams etParams = getEditTextParams(i);
			line1.addView(editText, etParams);
		}
	}

	/**
	 * buttons 和 widgetContent可能会改变，需要每次传入
	 * @param buttons
	 * @param widgetContent
	 */
	@SuppressLint("ResourceAsColor")
	protected void createLinearAdapter(
			Map<Integer, Integer> buttons, Map<Integer, String[]> widgetContent) {

		LinearLayout parentLine = (LinearLayout) mBaseView
				.findViewById(R.id.parent_line);
		parentLine.removeAllViews();

		if (buttons != null && buttons.size() != 0) {
			mKeys = new int[buttons.size()];

			Iterator<Integer> iterator = buttons.keySet().iterator();
			int iterator_i = 0;
			while (iterator.hasNext()) {
				mKeys[iterator_i] = (Integer) iterator.next();
				iterator_i++;
			}

		}
		
		LinearLayout line1 = null;
		String pre = "";
		if (mBaseWidgetInterface != null && mBaseWidgetInterface.getImportantColumns() != null) {
			pre = "   ";
		}
		for (int i = 0; i < mTextViewNames.length; i++) {
			LinearLayout tmpLine = createNewLine(i);
			if (tmpLine != null) {
				line1 = tmpLine;
			}			
			
			LinearLayout.LayoutParams layoutParams1 = new LinearLayout.LayoutParams(
					LayoutParams.MATCH_PARENT, LayoutParams.MATCH_PARENT);
			line1.setOrientation(LinearLayout.HORIZONTAL);
			
			
			// 创建TextView对象
			TextView textView1 = new TextView(mActivity);
			LinearLayout.LayoutParams tvParams1;
			setTextViewStyle(textView1);

			tvParams1 = getTextViewParams(i);
			textView1.setGravity(Gravity.CENTER_VERTICAL);
			
			if (mBaseWidgetInterface != null && mBaseWidgetInterface.getImportantColumns() != null
					&& Arrays.asList(mBaseWidgetInterface.getImportantColumns()).contains(i)) {
				SpannableStringBuilder style = new SpannableStringBuilder("* " + mTextViewNames[i]);
		        style.setSpan(new ForegroundColorSpan(Color.RED), 0, 1, Spannable.SPAN_EXCLUSIVE_INCLUSIVE); 
				textView1.setText(style);
			} else {
				textView1.setText(pre + mTextViewNames[i]);
			}
			
			textView1.setId(baseTextViewId + i); 
			
			if (isWindowBg) {
				textView1.setTextColor(Color.WHITE);
			} else {
				textView1.setTextColor(Color.BLACK);
			}

			// textView1.setGravity(Gravity.CENTER_VERTICAL | Gravity.RIGHT);
			line1.addView(textView1, tvParams1);

			initSubWidget(buttons, widgetContent, i, line1, textView1);
			
			setLineStyle(parentLine, line1, layoutParams1, i);
			
		}
		
		if (mBaseWidgetInterface != null && mBaseWidgetInterface.createExtraLayout() != null) {
			TwoNumber<View, LinearLayout.LayoutParams> twoNumber = mBaseWidgetInterface.createExtraLayout();
			if (twoNumber.b == null) {
				parentLine.addView(twoNumber.a);
			} else {
				parentLine.addView(twoNumber.a, twoNumber.b);
			}
			
		}
	}
	
	class WidgetCache {
		public int getLine() {
			return mLine;
		}
		public void setLine(int line) {
			this.mLine = line;
		}
		public int getDrawableId() {
			return mDrawableId;
		}
		public void setDrawableId(int drawableId) {
			this.mDrawableId = drawableId;
		}
		public OnClickListener getListener() {
			return mListener;
		}
		public void setListener(OnClickListener listener) {
			this.mListener = listener;
		}
		public String getHintContent() {
			return mHintContent;
		}
		public void setHintContent(String hintContent) {
			this.mHintContent = hintContent;
		}
		int mLine;
		int mDrawableId;
		OnClickListener mListener;
		String mHintContent;
	}
	
	/**
	 * 设置editText背景
	 * @param line
	 * @param backgrondType 0：空，1：普通模式，下划线显示 2：spinner模式显示
	 */
	protected void setEditTextBackground(int line, int backgroundType) {
		EditText et = (EditText) mBaseView
				.findViewById(baseEditTextId + line);
		switch (backgroundType) {
		case 0:
			et.setBackground(null);
			break;
		case 1:
			et.setBackground(mActivity.getResources()
					.getDrawable(R.drawable.bg_edittext));
			break;
		case 2:
			et.setBackground(mActivity.getResources()
					.getDrawable(R.drawable.dialog_spinner_bg));
			break;
		}
	}
	
	
	
	/**
	 * 
	 * @param line
	 * @param drawableId default 0
	 * @param listener default null
	 * @param hintContent if no, set it to null
	 * @param saveFlag see switchModifyDialog
	 */
	protected void setEditTextStyle(int line, int drawableId, OnClickListener listener, String hintContent, boolean saveFlag) {
		EditText et = (EditText) mBaseView
				.findViewById(baseEditTextId + line);
		boolean userSaveFlag = false;
		WidgetCache widgetCache = new WidgetCache();
		widgetCache.setLine(line);
		if (drawableId != 0) {
			userSaveFlag = true;
			widgetCache.setDrawableId(drawableId);
			Drawable drawable = mActivity
					.getResources()
					.getDrawable(drawableId);
		
			drawable.setBounds(0, 0, 30, 30);
			et.setCompoundDrawables(null, null,
					drawable, null);
		}
		
		if (listener != null) {
			userSaveFlag = true;
			et.setFocusableInTouchMode(false);
			et.clearFocus();
			widgetCache.setListener(listener);
			et.setOnClickListener(listener);
		}
		if (hintContent != null) {
			userSaveFlag = true;
			et.setHint(hintContent);
			widgetCache.setHintContent(hintContent);
		}
		if (saveFlag && userSaveFlag) {
			widgetCacheData.add(widgetCache);
		}
	}
	
	protected LinearLayout createNewLine(int i) {
		LinearLayout line = null;
		if (mSplitLine == 1) {
			line = new LinearLayout(mActivity);
			line.setId(baseLineId + i);
		} else if ((mSplitLine == 2) && ((i % mSplitLine) == 0)) {
			line = new LinearLayout(mActivity);
			line.setId(baseLineId + i/2);
		} else if ((mSplitLine == 3) && ((i % mSplitLine) == 0)) {
			line = new LinearLayout(mActivity);
			line.setId(baseLineId + i/3);
		}
		return line;
	}
	
	protected android.widget.LinearLayout.LayoutParams getTextViewParams(int i) {
		LinearLayout.LayoutParams tvParams1 = new LinearLayout.LayoutParams(
				mActivity.getResources().getDimensionPixelSize(
						R.dimen.popup_window_tv_width),
				LayoutParams.MATCH_PARENT);
		tvParams1.leftMargin = mActivity.getResources()
				.getDimensionPixelSize(R.dimen.popup_window_tv_margin_left);
		tvParams1.gravity = Gravity.CENTER_VERTICAL | Gravity.RIGHT;
		return tvParams1;
	}
		
	protected int getDimen(int value) {
		int sp = 0;
		switch (value) {
		case 16:
			sp = mActivity.getResources().getDimensionPixelSize(R.dimen.sp16_s);
			break;
		case 18:
			sp = mActivity.getResources().getDimensionPixelSize(R.dimen.sp18_s);
			break;
		}
		return sp;
	}
	
	protected void init(int arrayId, Map<Integer, Integer> buttons,
			Map<Integer, String[]> widgetContent) {
		String[] dialogLableNames = mActivity.getResources().getStringArray(arrayId);
		init(dialogLableNames, buttons, widgetContent);
	}

	/**
	 * 初始化缓存
	 * @param inputLableNames
	 * @param buttons
	 * @param widgetContent
	 */
	@SuppressLint("UseSparseArrays") 
	protected void init(String[] inputLableNames, Map<Integer, Integer> buttons,
			Map<Integer, String[]> widgetContent) {
		
		// mDialogButtons mWidgetContent 做缓存
		mTextViewNames = inputLableNames;
		if (buttons != null) {
			mDialogButtons = new HashMap<Integer, Integer>();
			mDialogButtons.putAll(buttons);
		}
		mWidgetContent = widgetContent;
		mButtons = buttons;
	}
	
	private Map<Integer, Integer> mDialogButtons;
	private boolean mSwitchStatus = true;
	/**
	 * 
	 * @param status true modify dialog, false: info dialog
	 */
	@SuppressLint("UseSparseArrays")
	public void switchModifyDialog(boolean status) {
		if (mSwitchStatus == status) {
			return;
		}
		mSwitchStatus = status;
		if (status) {
			// 修改
			if (mDialogButtons != null) { // 默认有buttons类型，存入缓存
				mButtons.clear();
				mButtons.putAll(mDialogButtons);
				createLinearAdapter(mButtons, mWidgetContent);
			} else {
				mButtons = null;
				createLinearAdapter(null, null);
			}
			initCacheEditTextStyle();
			setModifyButtons();
		} else {
			
			// 查看
			if (mDialogButtons != null) {
				mButtons.clear();
				for (int i = 0; i < mTextViewNames.length; i++) {
					int style = editTextReadOnlyLineStyle;
					if (mDialogButtons.get(i) == null) {
						mButtons.put(i, style);
					} else {
						switch (mDialogButtons.get(i)) {
						case 8:
							style = editTextNoBottomLineStyle;
							break;
						case 18:
						case 19:
							// OBSReadOnlyLineStyle
							style = OBSReadOnlyLineStyle;
							break;
						case 20:
						case 21:
							// userReadOnlySelectLineStyle
							style = userReadOnlySelectLineStyle;
							break;
						case 22:
						case 23:
							// tenantReadOnlySelectLineStyle
							style = tenantReadOnlySelectLineStyle;
							break;
						default:
							style = editTextReadOnlyLineStyle;
							break;
						}
						
						mButtons.put(i, style);
					}
				}
				createLinearAdapter(mButtons, null);
			} else {
				mButtons = new HashMap<>();
				for (int i = 0; i < mTextViewNames.length; i++) {
					mButtons.put(i, editTextReadOnlyLineStyle);
				}
				createLinearAdapter(mButtons, null);
			}
			
			setReadOnlyButtons();
		}
	}
	
	private void initCacheEditTextStyle() {
		for (int i = 0; i < widgetCacheData.size(); i++) {
			setEditTextStyle(widgetCacheData.get(i).getLine(), widgetCacheData.get(i).getDrawableId(),
					widgetCacheData.get(i).getListener(), widgetCacheData.get(i).getHintContent(), false);
		}
	}
	
	protected void setReadOnlyButtons() {
		
	};
	
	protected void setModifyButtons() {
		
	}
	
	protected LinearLayout.LayoutParams getMarkEditTextParams(EditText editText) {
		LinearLayout.LayoutParams etParams = new LinearLayout.LayoutParams(
				mActivity
				.getResources()
				.getDimensionPixelSize(
						R.dimen.popup_window_et_width),
						mActivity
						.getResources()
						.getDimensionPixelSize(
								R.dimen.dp120_h));

		int pxW = mActivity.getResources().getDimensionPixelSize(R.dimen.dp8_w);
		int pxH = mActivity.getResources().getDimensionPixelSize(R.dimen.dp8_h);
		etParams.setMargins(pxW, pxH, pxW, pxH);;
		int pxWR = mActivity.getResources().getDimensionPixelSize(R.dimen.dp18_w);
		etParams.setMargins(pxW, pxH, pxWR, pxH);
		return etParams;
	}
	
	protected int getCheckBoxMarginRightParams(int pxW) {
		return pxW;
	}
	
	protected abstract void setLineStyle(LinearLayout parentLine, LinearLayout line1, android.widget.LinearLayout.LayoutParams layoutParams1, int i);
	protected abstract void setTextViewStyle(TextView textView1);
	protected abstract android.widget.LinearLayout.LayoutParams getEditTextParams(int position);
	protected abstract void setEditTextStyle(EditText et);
	protected abstract android.widget.LinearLayout.LayoutParams getSeekBarParams();
	
	public interface WidgetInterface {
		void setRadioButtonChangeListener(int line, int position);
	}

}
