package com.pm360.cepm360.app.common.view.parent;

import android.annotation.SuppressLint;
import android.app.ActionBar.LayoutParams;
import android.app.Activity;
import android.util.TypedValue;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.pm360.cepm360.R;
import com.pm360.cepm360.app.common.custinterface.BaseWidgetInterface;

import java.util.Map;

/**
 * 
 * @author onekey
 * interface SetDefaultValue SaveData setEditTextStyle setEditTextContent setEditTextHint
 */
public class InformationView extends BaseWidget {

	private Button mFirstButton;
	private Button mSaveButton;
	private ImageView mCancelView;
	private String mPopupTitleName;
	
	public InformationView(Activity activity) {
		super(activity);
	}
	
	public InformationView(Activity activity, String name) {
		super(activity);
		mPopupTitleName = name;
	}
	
	public InformationView(Activity activity, int id) {
		super(activity);
		mPopupTitleName = mActivity.getString(id);
	}

	public InformationView(Activity activity, String name, BaseWidgetInterface baseWidgetInterface) {
		super(activity, baseWidgetInterface);
		mPopupTitleName = name;
	}
	
	public void SetDefaultValue(String[] editTexts) {
		super.SetDefaultValue(editTexts);
	}
	
	@SuppressLint("UseSparseArrays")
	public void init(String[] inputNames, Map<Integer, Integer> buttons,
			Map<Integer, String[]> widgetContent) {
		super.init(inputNames, buttons, widgetContent);
		
		isWindowBg = false;

		mBaseView = mActivity.getLayoutInflater().inflate(
				R.layout.base_dialog, null);

		createLinearAdapter(buttons, widgetContent);
		
		TextView tv = (TextView) mBaseView.findViewById(R.id.edit_title);
		if (mPopupTitleName != null) {
			tv.setText(mPopupTitleName);
		}

		mCancelView = (ImageView) mBaseView
				.findViewById(R.id.btn_close);
		
		mSaveButton = (Button) mBaseView
				.findViewById(R.id.save_Button);
		mFirstButton = (Button) mBaseView
				.findViewById(R.id.first_Button);
	}
	
	public void setFirstButton(String name, OnClickListener listener) {
		mFirstButton.setVisibility(View.VISIBLE);
		mFirstButton.setText(name);
		mFirstButton.setOnClickListener(listener);
	}
	
	public View getExitView() {
		return mCancelView;
	}

	/**
	 * interface for dialog
	 */
	@Override
	public void setReadOnlyButtons() {
		Button saveTextView = (Button) mBaseView
				.findViewById(R.id.save_Button);
		saveTextView.setVisibility(View.GONE);
	}
	
	public Button getFirstButton() {
		return mFirstButton;
	}
	
	public Button getWhichButton(int position) {
		if (position == 0) {
			return mFirstButton;
		} else if (position == 1) {
			return mSaveButton;
		} else {
			return null;
		}
	}

	
	@Override
	protected void setModifyButtons() {
		Button saveTextView = (Button) mBaseView
				.findViewById(R.id.save_Button);
		saveTextView.setVisibility(View.VISIBLE);
	}

	@Override
	protected void setLineStyle(LinearLayout parentLine, LinearLayout line1, android.widget.LinearLayout.LayoutParams layoutParams1, int i) {
		parentLine.addView(line1, layoutParams1);
		View view1 = new View(mActivity);
		// 在计划编制常用界面没有分隔线，其余都有分隔线
		// 在任务反馈和任务查询的常用界面没有编辑功能，其余都有编辑功能
		LayoutParams viewParams1 = new LayoutParams(800, readOnlyFlag ? 1
				: 0);
		view1.setBackgroundColor(mActivity.getResources().getColor(
				R.color.popup_divider));
		parentLine.addView(view1, viewParams1);
	}

	@Override
	protected void setTextViewStyle(TextView textView1) {
		textView1.setTextSize(TypedValue.COMPLEX_UNIT_PX, getDimen(16));
	}

	@Override
	protected void setEditTextStyle(EditText editText) {
		editText.setTextSize(TypedValue.COMPLEX_UNIT_PX, getDimen(16));
		int pxW = mActivity.getResources().getDimensionPixelSize(R.dimen.dp8_w);
		int pxH = mActivity.getResources().getDimensionPixelSize(R.dimen.dp8_h);

		editText.setPadding(pxW, pxH, pxW, pxH);
	}

	@Override
	protected LinearLayout.LayoutParams getEditTextParams(int i) {
		LinearLayout.LayoutParams etParams1 = null;
		etParams1 = new LinearLayout.LayoutParams(
				mActivity
						.getResources()
						.getDimensionPixelSize(
								R.dimen.popup_window_et_width),
								mActivity
								.getResources()
								.getDimensionPixelSize(
										R.dimen.popup_window_et_height));

		int pxW = mActivity.getResources().getDimensionPixelSize(R.dimen.dp8_w);
		int pxH = mActivity.getResources().getDimensionPixelSize(R.dimen.dp8_h);
		etParams1.setMargins(pxW, pxH, pxW, pxH);
		return etParams1;
	}
	
	@Override
	protected android.widget.LinearLayout.LayoutParams getSeekBarParams() {
		return new LinearLayout.LayoutParams(mActivity.getResources()
				.getDimensionPixelSize(R.dimen.popup_window_et_width),
				LinearLayout.LayoutParams.WRAP_CONTENT);
	}
	
	public void addView(View view) {
		LinearLayout parentLine = (LinearLayout) mBaseView
				.findViewById(R.id.parent_line);
		if (view != null) {
			parentLine.addView(view);
		}
	}
	
	public void removeView(View view) {
		LinearLayout parentLine = (LinearLayout) mBaseView
				.findViewById(R.id.parent_line);
		if (view != null) {
			parentLine.removeView(view);
		}
	}
}
