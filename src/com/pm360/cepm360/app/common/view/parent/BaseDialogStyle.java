package com.pm360.cepm360.app.common.view.parent;

import android.app.Activity;
import android.app.AlertDialog;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.LinearLayout.LayoutParams;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.pm360.cepm360.R;
import com.pm360.cepm360.app.CepmApplication;
import com.pm360.cepm360.common.util.LogUtil;

public class BaseDialogStyle {

	/**-- Cache --*/
	private RelativeLayout.LayoutParams mParams;

	/**-- View --*/
	private Button mSaveButton;
	private ImageView mExitView;
	private Button mFirstButton;
	protected Activity mActivity;
	private AlertDialog mDialog;
	private View mBaseView;
	private LinearLayout mTitle;
	
	public View getRootView() {
		return mBaseView;
	}
	
	public BaseDialogStyle(Activity activity) {
		mActivity = activity;
	}
	
	public void setCanceledOnTouchOutside(boolean cancel) {
		mDialog.setCanceledOnTouchOutside(cancel);
	}
	
	
	/**
	 * if you want to increase width of dialog, invoke this function
	 * @param width 0 for defaults
	 * @param height
	 */
	public void show(int width, int height) {
		setParams(width, height);
		show();
		WindowManager windowManager = mActivity.getWindowManager();
		windowManager.getDefaultDisplay();
		WindowManager.LayoutParams lp = mDialog.getWindow().getAttributes();
		if (width != 0) {
			int wFix = ((CepmApplication)mActivity.getApplicationContext()).getDeviceInfo().getWidth();
	    	int pxW = (int) (wFix * width / 1280);
			lp.width = (int)(pxW); //设置宽度
		}
		if (height != 0) {
			int hFix = ((CepmApplication)mActivity.getApplicationContext()).getDeviceInfo().getHeight();
	    	int pxH = (int) (hFix * height / 736);
			lp.height = (int)(pxH);
		}
		mDialog.getWindow().setAttributes(lp);
	}
	

	public void setParams(int dpX, int dpY) {
		if (dpX != 0) {
			int wFix = ((CepmApplication)mActivity.getApplicationContext()).getDeviceInfo().getWidth();
	    	int pxW = (int) (wFix * dpX * 1.33125 / 1280);
			mParams.width = pxW;
		}
		if (dpY != 0) {
			int hFix = ((CepmApplication)mActivity.getApplicationContext()).getDeviceInfo().getHeight();
	    	int pxH = (int) (hFix * dpY * 1.33125 / 736);
			mParams.height = pxH;
		}
	}
	
	public void show() {
		LinearLayout parent = (LinearLayout) mBaseView
				.findViewById(R.id.list_view);
	
		if (mParams != null) {
			LogUtil.i("wzw w:" + mParams.width + " h:" + mParams.height);
			parent.setLayoutParams(mParams);
		}

		mDialog.show();
		
	}
	
	public void dismiss() {
		mDialog.dismiss();
	}
	
	public View init(int layoutId) {
		View view = LayoutInflater.from(mActivity).inflate(
				layoutId, null);
		return init(view);
	}
	
	public View init(View view) {
		return init(view, true);
	}

	public View init(int layoutId, boolean showButton) {
		View view = LayoutInflater.from(mActivity).inflate(
				layoutId, null);
		return init(view, showButton);
	}
	
	public void setTitleName(String name) {
		mTitle.setVisibility(View.VISIBLE);
		TextView tv = (TextView) mTitle.findViewById(R.id.edit_title);
		tv.setText(name);
	}
	
	public View init(View view, boolean showButton) {
		mBaseView = initLayout();
		AlertDialog.Builder addPopup = new AlertDialog.Builder(mActivity);
		addPopup.setView(mBaseView);
		addPopup.setCancelable(false);
		mDialog = addPopup.create();
		
		mTitle = (LinearLayout) mBaseView
		.findViewById(R.id.title);
		mTitle.setVisibility(View.GONE);
		
		mSaveButton = (Button) mBaseView
				.findViewById(R.id.save_Button);
		
		mExitView = (ImageView) mBaseView
				.findViewById(R.id.btn_close);
		
		mFirstButton = (Button) mBaseView
				.findViewById(R.id.first_Button);
		
		mExitView.setOnClickListener(new View.OnClickListener() {

			@Override
			public void onClick(View arg0) {
				mDialog.dismiss();
			}
		});

		ViewGroup parent = (ViewGroup) mBaseView
				.findViewById(R.id.list_view);
		parent.addView(view, LayoutParams.MATCH_PARENT, LayoutParams.MATCH_PARENT);
		
		if (!showButton) {
			mSaveButton.setVisibility(View.GONE);
		}

		mParams = (RelativeLayout.LayoutParams) parent.getLayoutParams();
		
		return view;
	}
	
	protected View initLayout() {
		return mActivity.getLayoutInflater().inflate(
				R.layout.base_dialog_listview, null);
	}
	
	public void setButton(String text, OnClickListener listener) {
		if (text != null && !text.equals(""))
			mSaveButton.setText(text);
		if (listener != null) {
			mSaveButton.setOnClickListener(listener);
		}
	}
	
	public void setFirstButtonVisible() {
		mFirstButton.setVisibility(View.VISIBLE);
	}
	
	public void setButton(int whichButton, String text, OnClickListener listener) {
		if (text != null && !text.equals("")) {
			switch (whichButton) {
			case 0:
				mFirstButton.setText(text);
				break;
			case 1:
				mSaveButton.setText(text);
				break;
			}
		}
		if (listener != null) {
			switch (whichButton) {
			case 0:
				mFirstButton.setOnClickListener(listener);
				break;
			case 1:
				mSaveButton.setOnClickListener(listener);
				break;
			}
		}
	}
	
	public void setButton(int whichButton, int stringId, OnClickListener listener) {
		String text = mActivity.getResources().getString(stringId);
		if (text != null && !text.equals("")) {
			switch (whichButton) {
			case 0:
				mFirstButton.setText(text);
				break;
			case 1:
				mSaveButton.setText(text);
				break;
			}
		}
		if (listener != null) {
			switch (whichButton) {
			case 0:
				mFirstButton.setOnClickListener(listener);
				break;
			case 1:
				mSaveButton.setOnClickListener(listener);
				break;
			}
		}
	}
	
	/**
	 * 
	 * @param whichButton 0 firstButton 1 saveButton 2 exitButton
	 * @return
	 */
	public Button getButton(int whichButton) {
		switch (whichButton) {
		case 0:
			return mFirstButton;
		case 1:
			return mSaveButton;
		}
		return null;
	}
	
	public View getExitView() {
		return mExitView;
	}

}
