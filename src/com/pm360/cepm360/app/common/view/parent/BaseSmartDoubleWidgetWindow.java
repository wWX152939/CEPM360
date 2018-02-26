package com.pm360.cepm360.app.common.view.parent;

import android.app.Activity;
import android.graphics.Color;
import android.util.TypedValue;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.LinearLayout.LayoutParams;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.pm360.cepm360.R;
import com.pm360.cepm360.app.common.custinterface.BaseWidgetInterface;
import com.pm360.cepm360.app.utils.UtilTools;

import java.util.List;
import java.util.Map;

public class BaseSmartDoubleWidgetWindow extends BaseWidget {
	public BaseSmartDoubleWidgetWindow(Activity activity) {
		super(activity);
	}
	
	public BaseSmartDoubleWidgetWindow(Activity activity, BaseWidgetInterface baseWidgetInterface) {
		super(activity, baseWidgetInterface);
	}
	
	/**
	 * 
	 * @param arrayId
	 * @param buttons
	 * @param widgetContent
	 * @param lineList 告知从第几行开始需要单独成行显示
	 */
	public void init(int arrayId, Map<Integer, Integer> buttons,
			Map<Integer, String[]> widgetContent, List<Integer> lineList) {
		super.init(arrayId, buttons, widgetContent);
		mLineList = lineList;
		readOnlyFlag = false;
		isWindowBg = false;
		mSplitLine = 2;
		mBaseView = mActivity.getLayoutInflater().inflate(
				R.layout.base_window, null);
		initSaveButton();
		createLinearAdapter(buttons, widgetContent);

	}

	private void initSaveButton() {
		Button bt = (Button) mBaseView.findViewById(R.id.save_Button);
		RelativeLayout.LayoutParams params = (android.widget.RelativeLayout.LayoutParams) bt.getLayoutParams();
		params.setMarginStart(UtilTools.dp2pxW(mActivity, 672));
		bt.setLayoutParams(params);
	}
	
	private List<Integer> mLineList;
	private int mLastNoOneLineNum;
	private boolean mIsFirstEnter = true;
	private int mCurrentLine;
	
	@Override
	protected LinearLayout createNewLine(int i) {
		LinearLayout line = null;
		mCurrentLine = i;
		
		// 只针对mSplitLine为2，可以单独设置一行的情况
		if (mLineList.contains(i)) {
			if (mIsFirstEnter) {
				if (i % mSplitLine == 0) {
					mLastNoOneLineNum = i / 2;	
				} else {
					mLastNoOneLineNum = (i + 1) / 2;
				}
				mIsFirstEnter = false;
			} else {
				mLastNoOneLineNum++;
			}
			
			line = new LinearLayout(mActivity);
			line.setId(mLastNoOneLineNum);
		} else if (i % mSplitLine == 0) {
			line = new LinearLayout(mActivity);
			line.setId(baseLineId + i/2);
		}	
		
		return line;
	}

	@Override
	protected void setLineStyle(LinearLayout parentLine, LinearLayout line1,
		LayoutParams layoutParams1, int i) {
		
		// 只针对mSplitLine为2，可以单独设置一行的情况
		if (mLineList.contains(i)) {
			parentLine.addView(line1, layoutParams1);
		} else if (i % mSplitLine == 0) {
			parentLine.addView(line1, layoutParams1);
		}
	}

	@Override
	protected void setTextViewStyle(TextView textView) {
		textView.setTextSize(TypedValue.COMPLEX_UNIT_PX, getDimen(16));
	}

	@Override
	protected void setEditTextStyle(EditText editText) {
		editText.setTextSize(TypedValue.COMPLEX_UNIT_PX, getDimen(16));
		int pxW = mActivity.getResources().getDimensionPixelSize(R.dimen.dp8_w);
		editText.setPadding(pxW, 0, pxW, 0);
	}
	
	private LinearLayout.LayoutParams getOneLineEtParams() {
		LinearLayout.LayoutParams etParams = new LinearLayout.LayoutParams(mActivity.getResources()
				.getDimensionPixelSize(R.dimen.dp420_w),
				mActivity.getResources().getDimensionPixelSize(
						R.dimen.popup_window_et_height));

		int pxW = UtilTools.dp2pxW(mActivity.getBaseContext(), 8);
		int pxH = UtilTools.dp2pxH(mActivity.getBaseContext(), 8);
		etParams.setMargins(pxW, pxH, pxW, pxH);
		
		return etParams;
	}
	
	private LinearLayout.LayoutParams getMultiLineEtParams() {
		LinearLayout.LayoutParams etParams = new LinearLayout.LayoutParams((mActivity.getResources()
				.getDimensionPixelSize(R.dimen.popup_window_et_width) / 2),
				LayoutParams.MATCH_PARENT);

		int pxWL = UtilTools.dp2pxW(mActivity.getBaseContext(), 8);
		int pxWR = UtilTools.dp2pxW(mActivity.getBaseContext(), 40);
		int pxH = UtilTools.dp2pxH(mActivity.getBaseContext(), 8);
		etParams.setMargins(pxWL, pxH, pxWR, pxH);
		return etParams;
	}
	
	protected int getCheckBoxMarginRightParams(int pxW) {
		//TODO
		return UtilTools.dp2pxH(mActivity, 152);
	}

	@Override
	protected LinearLayout.LayoutParams getEditTextParams(int i) {
		LinearLayout.LayoutParams etParams = null;

		// 只针对mSplitLine为2，可以单独设置一行的情况
		if (mLineList.contains(mCurrentLine)) {
			etParams = getOneLineEtParams();
		} else {
			etParams = getMultiLineEtParams();
		}
		
		return etParams;
	}

	@Override
	protected LayoutParams getSeekBarParams() {
		LinearLayout.LayoutParams etParams = null;

		etParams = new LinearLayout.LayoutParams((mActivity.getResources()
				.getDimensionPixelSize(R.dimen.popup_window_et_width) / 2),
				LinearLayout.LayoutParams.WRAP_CONTENT);

		return etParams;
	}
	
	@Override
	protected LinearLayout.LayoutParams getMarkEditTextParams(EditText editText) {
		editText.setTextColor(Color.BLACK);
		LinearLayout.LayoutParams etParams = new LinearLayout.LayoutParams(
				mActivity
				.getResources()
				.getDimensionPixelSize(
						R.dimen.dp540_w),
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
	
	public void switchModifyWindow(boolean status) {
		super.switchModifyDialog(status);
	}
}
