package com.pm360.cepm360.app.common.view.parent;

import android.app.Activity;
import android.util.TypedValue;
import android.view.Gravity;
import android.view.View;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.LinearLayout.LayoutParams;
import android.widget.TextView;

import com.pm360.cepm360.R;
import com.pm360.cepm360.app.utils.UtilTools;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public class BaseSearchView extends BaseWidget {
	private List<Integer> mRelevance;
	private final int baseSearchViewId = 18000;
	private List<Integer> mWidgetIds = new ArrayList<Integer>();
	public static final String mConnectorSymbol = "##";
	
	
	public BaseSearchView(Activity activity) {
		super(activity);
	}
	
	/**
	 * 
	 * @param arrayId
	 * @param buttons
	 * @param widgetContent
	 * @param flag
	 * @param splitLine for ticket window
	 */
	public void init(int arrayId, Map<Integer, Integer> buttons,
			Map<Integer, String[]> widgetContent, List<Integer> relevance) {
		super.init(arrayId, buttons, widgetContent);
		mRelevance = relevance;
		readOnlyFlag = false;
		isWindowBg = true;
		mSplitLine = 2;
		mBaseView = mActivity.getLayoutInflater().inflate(
				R.layout.base_search_view, null);
		mWidgetIds.clear();
		createLinearAdapter(buttons, widgetContent);

	}

	@Override
	public void setLineStyle(LinearLayout parentLine, LinearLayout line1,
			LayoutParams layoutParams1, int i) {
		if ((i % mSplitLine) == 0) {
			parentLine.addView(line1, layoutParams1);
			if (mRelevance != null && mRelevance.contains(i)) {
				setRelevanceLayout(line1, i);
			}
		}
	}
	
	private void setRelevanceLayout(LinearLayout line, int i) {
		TextView tv = new TextView(mActivity);
		tv.setText("一");
		line.addView(tv);
		EditText et = null;
		LinearLayout.LayoutParams etParams = getEditTextParams(i);
		switch(mButtons.get(i)) {
			//numberEditTextLineStyle
		case 11:
			et = createEditText(0,
					line, i, 0);
			break;
			//decimalEditTextLineStyle
		case 13:
			et = createEditText(3,
					line, i, 0);
			break;
			//calendarLineStyle
		case 1:
			EditText editText = (EditText) line.findViewById(baseEditTextId + i);
			showDialogWindow(mActivity, editText, i);
			et = createEditText(2,
					line, i, calendarETBgStyle);

			showDialogWindow(mActivity, et, i);
			break;
		}
		if (et != null) {
			et.setId(baseSearchViewId + i);
			mWidgetIds.add(baseSearchViewId + i);
			line.addView(et, etParams);
		}
			
	}

	@Override
	public void setTextViewStyle(TextView textView1) {
		textView1.setTextSize(TypedValue.COMPLEX_UNIT_PX, mActivity.getResources().getDimension(R.dimen.sp16_s));
	}
	

	public android.widget.LinearLayout.LayoutParams getTextViewParams() {
		int px = UtilTools.dp2pxW(
				mActivity.getBaseContext(), 75);
		LinearLayout.LayoutParams tvParams1 = new LinearLayout.LayoutParams(px,
				LayoutParams.MATCH_PARENT);

		tvParams1.gravity = Gravity.BOTTOM | Gravity.RIGHT;
		return tvParams1;
	}

	@Override
	public void setEditTextStyle(EditText editText) {
		editText.setTextSize(TypedValue.COMPLEX_UNIT_PX, mActivity.getResources().getDimension(R.dimen.sp14_s));
		int px = UtilTools.dp2pxW(
				mActivity.getBaseContext(), 8);
		editText.setPadding(px, 0, px, 0);
		editText.setHint("");
	}
	
	public void SetDefaultValue(String[] editTexts) {
		super.SetDefaultValue(editTexts);
		for (int i = 0; i < mWidgetIds.size(); i++) {
			EditText et = (EditText)mBaseView.findViewById(mWidgetIds.get(i));
			et.setText("");
		}
	}
	
	/**
	 * 带有"-"行的返回 data1 + mConnectorSymbol + data2
	 */
	public Map<String, String> SaveData() {
		Map<String, String> saveData = super.SaveData();
		int number;
		for (int i = 0; i < mWidgetIds.size(); i++) {
			number = mWidgetIds.get(i) - baseSearchViewId;
			EditText et = (EditText)mBaseView.findViewById(mWidgetIds.get(i));
			String data1 = saveData.get(number);
			String data2 = et.getText().toString();
			String data = data1 + mConnectorSymbol + data2;
			saveData.put(mTextViewNames[number], data);
		}
		return saveData;
	}

	@Override
	public LinearLayout.LayoutParams getEditTextParams(int i) {
		LinearLayout.LayoutParams etParams1 = null;

		int px = UtilTools.dp2pxW(
				mActivity.getBaseContext(), 150);

		int pxH = UtilTools.dp2pxH(
				mActivity.getBaseContext(), 35);
		etParams1 = new LinearLayout.LayoutParams(px, pxH);
		px = UtilTools.dp2pxW(
				mActivity.getBaseContext(), 8);
		pxH = UtilTools.dp2pxH(
				mActivity.getBaseContext(), 8);
		etParams1.setMargins(px, pxH, px, pxH);
		
		return etParams1;
	}
	
	@Override
	public android.widget.LinearLayout.LayoutParams getSeekBarParams() {
		int px = UtilTools.dp2pxW(
				mActivity.getBaseContext(), 150);

		LinearLayout.LayoutParams etParams = new LinearLayout.LayoutParams(px, LinearLayout.LayoutParams.WRAP_CONTENT);

		return etParams;
	}

	public LinearLayout createNewLine(int i) {
		LinearLayout line = null;
		if ((i % mSplitLine) == 0) {
			line = new LinearLayout(mActivity);
		} else if (mRelevance != null && mRelevance.contains(i)) {
			line = new LinearLayout(mActivity);
		}
		return line;
	}
	
	public View getView() {
		return getPopupView();
	}
}
