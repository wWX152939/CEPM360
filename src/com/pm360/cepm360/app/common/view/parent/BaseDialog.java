package com.pm360.cepm360.app.common.view.parent;

import java.util.Map;

import android.annotation.SuppressLint;
import android.app.ActionBar.LayoutParams;
import android.app.Activity;
import android.app.AlertDialog;
import android.util.TypedValue;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.pm360.cepm360.R;
import com.pm360.cepm360.app.common.custinterface.BaseWidgetInterface;
import com.pm360.cepm360.app.utils.UtilTools;

/**
 * 
 * @author onekey
 * interface SetDefaultValue SaveData setEditTextStyle setEditTextContent setEditTextHint
 */
public class BaseDialog extends BaseWidget {

	private LinearLayout mRootView;
	private AlertDialog mDialog;
	private Button mFirstButton;
	private Button mSecondButton;
	private Button mSaveButton;
	private ImageView mCloseView;
	private String mPopupTitleName;
	private ProgressBar mProgressBar;
	
	public BaseDialog(Activity activity) {
		super(activity);
	}
	
	public BaseDialog(Activity activity, String name) {
		super(activity);
		mPopupTitleName = name;
	}
	
	public BaseDialog(Activity activity, int id) {
		super(activity);
		mPopupTitleName = mActivity.getString(id);
	}

	public BaseDialog(Activity activity, String name, BaseWidgetInterface baseWidgetInterface) {
		super(activity, baseWidgetInterface);
		mPopupTitleName = name;
	}
	
	public void show(String[] editTexts) {
		SetDefaultValue(editTexts);
		mDialog.show();
	}
	
	public void show() {
		mDialog.show();
	}

	public void dismiss() {
		mDialog.dismiss();
	}
	
	/**
	 * 设置行是否显示
	 */
	public void setLineVisible(int line, boolean visible) {
		super.setLineVisible(line, visible);
	}
	
	public void setSaveButtonListener(OnClickListener listener) {
		Button saveButton = (Button) mBaseView
				.findViewById(R.id.save_Button);
		saveButton.setOnClickListener(listener);
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
	
	/**
	 * 在布局中插入自定义布局
	 * @param index 默认情况，0 title 1:content 2 bottom button
	 * @param view
	 * @param params
	 */
	public void insertLayout(int index, View view, android.widget.LinearLayout.LayoutParams params) {
		mRootView.addView(view, index, params);
	}
	
	public ProgressBar insertProgressBar() {
		if (mProgressBar == null) {
			mProgressBar = (ProgressBar) mActivity.getLayoutInflater().inflate(
					R.layout.progress_bar, null);
			android.widget.LinearLayout.LayoutParams params = new android.widget.LinearLayout.LayoutParams(
					android.widget.LinearLayout.LayoutParams.MATCH_PARENT, android.widget.LinearLayout.LayoutParams.WRAP_CONTENT);
			params.setMarginStart(UtilTools.dp2pxW(mActivity, 24));
			params.setMarginEnd(UtilTools.dp2pxW(mActivity, 24));
			insertLayout(2, mProgressBar, params);
		}
		
		return mProgressBar;
	}
	
	@SuppressLint("UseSparseArrays")
	public void init(int arrayId, Map<Integer, Integer> buttons,
			Map<Integer, String[]> widgetContent) {
		String[] lableNames = mActivity.getResources().getStringArray(arrayId);
		init(lableNames, buttons, widgetContent);
	}
	
	@SuppressLint("UseSparseArrays")
	public void init(String[] lableNames, Map<Integer, Integer> buttons,
			Map<Integer, String[]> widgetContent) {
		super.init(lableNames, buttons, widgetContent);
		
		isWindowBg = false;

		mBaseView = mActivity.getLayoutInflater().inflate(
				R.layout.base_dialog, null);
		mRootView = (LinearLayout) mBaseView.findViewById(R.id.root_view);

		AlertDialog.Builder addPopup = new AlertDialog.Builder(mActivity);
		addPopup.setView(mBaseView);
		addPopup.setCancelable(false);
		mDialog = addPopup.create();

		createLinearAdapter(buttons, widgetContent);
		LinearLayout line = (LinearLayout) mBaseView.findViewById(R.id.title);
		if (mPopupTitleName != null) {
			TextView tv = (TextView) line.findViewById(R.id.edit_title);
			tv.setText(mPopupTitleName);
			mCloseView = (ImageView) line.findViewById(R.id.btn_close);
			mCloseView.setOnClickListener(new OnClickListener() {
				
				@Override
				public void onClick(View v) {
					mDialog.dismiss();
				}
			});
		}

		mSaveButton = (Button) mBaseView
				.findViewById(R.id.save_Button);
		mFirstButton = (Button) mBaseView
				.findViewById(R.id.first_Button);
		mSecondButton = (Button) mBaseView
				.findViewById(R.id.second_Button);
	}
	
	/**
	 * 设置关闭控件的监听者
	 * @param listener 关闭监听者
	 */
	public void setCloseViewListener(OnClickListener listener) {
		mCloseView.setOnClickListener(listener);
	}
	
	public void setFirstButton(String name, OnClickListener listener) {
		mFirstButton.setVisibility(View.VISIBLE);
		mFirstButton.setText(name);
		mFirstButton.setOnClickListener(listener);
	}
	
	/**
	 * 设置Button的名字和监听
	 * @param whichButton
	 * @param name
	 * @param listener
	 */
	public void setWhichButton(int whichButton, String name, OnClickListener listener) {
		Button btn = getWhichButton(whichButton);
		btn.setText(name);
		btn.setOnClickListener(listener);
	}
	
	/**
	 * 设置Button是否显示
	 * @param whichButton
	 * @param visibility
	 */
	public void setButtonVisibility(int whichButton, boolean visibility) {
		Button btn = getWhichButton(whichButton);
		if (visibility) {
			btn.setVisibility(View.VISIBLE);
		} else {
			btn.setVisibility(View.GONE);
		}
	}
	
	public Button getFirstButton() {
		return mFirstButton;
	}
	
	/**
	 * 获取Button，position 0，返回mFirstButton；1，返回mSecondButton, 2返回mSaveButton
	 * @param position
	 * @return
	 */
	public Button getWhichButton(int position) {
		if (position == 0) {
			return mFirstButton;
		} if (position == 1) {
			return mSecondButton;
		} else {
			return mSaveButton;
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
