package com.pm360.cepm360.app.common.view.parent;

import android.app.Activity;
import android.graphics.Color;
import android.util.TypedValue;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.LinearLayout.LayoutParams;
import android.widget.TextView;

import com.pm360.cepm360.R;
import com.pm360.cepm360.app.common.custinterface.BaseWidgetInterface;
import com.pm360.cepm360.app.utils.UtilTools;

import java.util.List;
import java.util.Map;

public class BaseWindow extends BaseWidget {
	private Button mSaveButton;

	private List<Integer> mLineList;
	private Map<Integer, Integer> mOneLineMap;
	private int mLastNoOneLineNum;
	private boolean mIsFirstEnter = true;
	private int mCurrentLine;
	
	/**-- 接口 --*/
	private WindowInterface mWindowInterface;
	
	public BaseWindow(Activity activity) {
		super(activity);
	}
	
	public BaseWindow(Activity activity, BaseWidgetInterface baseWidgetInterface) {
		super(activity, baseWidgetInterface);
	}
	
	/**
	 * 设置行间距和组件之间的间距
	 * @param activity
	 * @param windowInterface
	 */
	public BaseWindow(Activity activity, WindowInterface windowInterface) {
		super(activity);
		mWindowInterface = windowInterface;
	}
	
	public BaseWindow(Activity activity, WindowInterface windowInterface, BaseWidgetInterface baseWidgetInterface) {
		super(activity, baseWidgetInterface);
		mWindowInterface = windowInterface;
	} 
	
	/**
	 * 设置button按钮监听
	 * @param listener
	 */
	public void setSaveButtonClickListener(OnClickListener listener) {
		mSaveButton.setVisibility(View.VISIBLE);
		mSaveButton.setOnClickListener(listener);
	}
	
	/**
	 * 设置button按钮显示字体和监听
	 * @param name
	 * @param listener
	 */
	public void setSaveButtonStyle(String name, OnClickListener listener) {
		setSaveButtonClickListener(listener);
		mSaveButton.setText(name);
	}

	/**
	 * init interface for windows
	 * 
	 * @param arrayId
	 * @param buttons
	 * @param widgetContent
	 * @param flag true:readOnlyMode 
	 */
	public void init(int arrayId, Map<Integer, Integer> buttons,
			Map<Integer, String[]> widgetContent, boolean flag) {
		super.init(arrayId, buttons, widgetContent);
		readOnlyFlag = flag;
		isWindowBg = false;
		mBaseView = mActivity.getLayoutInflater().inflate(R.layout.base_window,
				null);
		mSaveButton = (Button) mBaseView.findViewById(R.id.save_Button);
		createLinearAdapter(buttons, widgetContent);

	}

	/**
	 * 
	 * @param arrayId
	 * @param buttons
	 * @param widgetContent
	 * @param flag
	 * @param splitLine
	 *            for ticket window
	 */
	public void init(int arrayId, Map<Integer, Integer> buttons,
			Map<Integer, String[]> widgetContent, boolean flag, int splitLine) {
		super.init(arrayId, buttons, widgetContent);
		readOnlyFlag = flag;
		isWindowBg = false;
		mSplitLine = splitLine;
		mBaseView = mActivity.getLayoutInflater().inflate(
				R.layout.base_window_form, null);
		createLinearAdapter(buttons, widgetContent);

	}
	
	public void init(int arrayId, Map<Integer, Integer> buttons,
			Map<Integer, String[]> widgetContent, int splitLine) {
		super.init(arrayId, buttons, widgetContent);
		readOnlyFlag = false;
		isWindowBg = false;
		mSplitLine = splitLine;
		mBaseView = mActivity.getLayoutInflater().inflate(
				R.layout.base_window, null);
		createLinearAdapter(buttons, widgetContent);

	}
	
	/**
	 * 针对window界面，每行可以为两列，可以为一列
	 * @param lineList
	 */
	public void setOneLineStyle(List<Integer> lineList) {
		mLineList = lineList;
	}
	
	/**
	 * 设置单独一行的Map
	 * @param oneLineMap key为行号，value为需要插入第几行后面，例：如果需要插入第一行，填0
	 */
	public void setOneLineMap(Map<Integer, Integer> oneLineMap) {
		mOneLineMap = oneLineMap;
	}

	@Override
	protected void setInputMode(boolean isAddStatus, List<Integer> editTextIdList) {
		//do nothing
	}
	
	@Override
	protected LinearLayout createNewLine(int i) {
		LinearLayout line = null;
		mCurrentLine = i;
		if (mLineList == null) {
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
		} else {
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
		}
		
		return line;
	}

	@Override
	protected void setLineStyle(LinearLayout parentLine, LinearLayout line1,
			LayoutParams layoutParams1, int i) {
		if (mLineList == null) {
			if (mSplitLine == 1) {
				parentLine.addView(line1, layoutParams1);
				View view1 = new View(mActivity);
				// 在计划编制常用界面没有分隔线，其余都有分隔线
				// 在任务反馈和任务查询的常用界面没有编辑功能，其余都有编辑功能
				LayoutParams viewParams1 = new LayoutParams(800, readOnlyFlag ? 1
						: 0);
				view1.setBackgroundColor(mActivity.getResources().getColor(
						R.color.popup_divider));
				parentLine.addView(view1, viewParams1);
			} else if ((mSplitLine == 2) && ((i % mSplitLine) == 0)) {
				parentLine.addView(line1, layoutParams1);
			} else if ((mSplitLine == 3) && ((i % mSplitLine) == 0)) {
				parentLine.addView(line1, layoutParams1);
			}
		} else {
			// 只针对mSplitLine为2，可以单独设置一行的情况
			if (mLineList.contains(i)) {
				if (mOneLineMap != null && mOneLineMap.containsKey(i)) {
					parentLine.addView(line1, mOneLineMap.get(i), layoutParams1);
				} else {
					parentLine.addView(line1, layoutParams1);
				}
			} else if (i % mSplitLine == 0) {
				parentLine.addView(line1, layoutParams1);
			}
		}
		
	}

	@Override
	protected void setTextViewStyle(TextView textView1) {
		if (mWindowInterface != null) {
			textView1.setTextSize(TypedValue.COMPLEX_UNIT_PX, mWindowInterface.getTextSize());
		} else {
			if (mSplitLine == 1) {
				textView1.setTextSize(TypedValue.COMPLEX_UNIT_PX, getDimen(16));
			} else {
				textView1.setTextSize(TypedValue.COMPLEX_UNIT_PX, getDimen(18));
			}
		}
	}

	@Override
	protected void setEditTextStyle(EditText editText) {
		if (mWindowInterface != null) {
			editText.setTextSize(TypedValue.COMPLEX_UNIT_PX, mWindowInterface.getTextSize());
		} else {
			if (mSplitLine == 1) {
				editText.setTextSize(TypedValue.COMPLEX_UNIT_PX, getDimen(16));
			} else {
				editText.setTextSize(TypedValue.COMPLEX_UNIT_PX, getDimen(18));
			}
		}
		
		int pxW = mActivity.getResources().getDimensionPixelSize(R.dimen.dp8_w);
		editText.setPadding(pxW, 0, pxW, 0);
	}
	
	private LinearLayout.LayoutParams getOneLineEtParams() {
		int witdh = mActivity.getResources().getDimensionPixelSize(R.dimen.popup_window_et_width);
		if (mWindowInterface != null) {
			witdh = mWindowInterface.getOneLineEditTextWidth();
		}
		LinearLayout.LayoutParams etParams = new LinearLayout.LayoutParams(witdh,
				mActivity.getResources().getDimensionPixelSize(
						R.dimen.popup_window_et_height));

		int pxW = UtilTools.dp2pxW(mActivity.getBaseContext(), 8);
		int pxH = UtilTools.dp2pxH(mActivity.getBaseContext(), 8);
		etParams.setMargins(pxW, pxH, pxW, pxH);
		return etParams;
	}
	
	@Override
	protected android.widget.LinearLayout.LayoutParams getTextViewParams(int i) {
		LinearLayout.LayoutParams tvParams = super.getTextViewParams(i);
		if (mWindowInterface != null) {
			if (i % mSplitLine != 0 && (mLineList == null || !mLineList.contains(i))) {
				int pxW = mWindowInterface.getWidgetWidthMargin();
				tvParams.leftMargin = pxW;
			}

		}
		return tvParams;
	}
	
	private LinearLayout.LayoutParams getMultiLineEtParams(int i) {
		int witdh = mActivity.getResources().getDimensionPixelSize(R.dimen.popup_window_et_width) / 2;
		if (mWindowInterface != null) {
			witdh = mWindowInterface.getMultiLineEditTextWidth();
		}
		LinearLayout.LayoutParams etParams = new LinearLayout.LayoutParams(witdh,
				LayoutParams.MATCH_PARENT);
		int pxW = UtilTools.dp2pxW(mActivity.getBaseContext(), 8);
		int pxH = UtilTools.dp2pxH(mActivity.getBaseContext(), 8);
		
		etParams.setMargins(pxW, pxH, pxW, pxH);
		return etParams;
	}

	@Override
	protected LinearLayout.LayoutParams getEditTextParams(int i) {
		LinearLayout.LayoutParams etParams = null;
		if (mLineList == null) {
			if (mSplitLine == 1) {
				etParams = getOneLineEtParams();
			} else if (mSplitLine == 2 || mSplitLine == 3) {
				etParams = getMultiLineEtParams(i);
			}
		} else {
			// 只针对mSplitLine为2，可以单独设置一行的情况
			if (mLineList.contains(mCurrentLine)) {
				etParams = getOneLineEtParams();
			} else {
				etParams = getMultiLineEtParams(i);
			}
		}
		

		return etParams;
	}

	@Override
	protected LayoutParams getSeekBarParams() {
		LinearLayout.LayoutParams etParams = null;
		if (mSplitLine == 1) {
			etParams = new LinearLayout.LayoutParams(mActivity.getResources()
					.getDimensionPixelSize(R.dimen.popup_window_et_width),
					LinearLayout.LayoutParams.WRAP_CONTENT);

		} else if (mSplitLine == 2 || mSplitLine == 3) {
			etParams = new LinearLayout.LayoutParams((mActivity.getResources()
					.getDimensionPixelSize(R.dimen.popup_window_et_width) / 2),
					LinearLayout.LayoutParams.WRAP_CONTENT);

		}

		return etParams;
	}
	
	public void switchModifyWindow(boolean status) {
		super.switchModifyDialog(status);
	}
	
	protected LinearLayout.LayoutParams getMarkEditTextParams(EditText editText) {
		editText.setTextColor(Color.BLACK);
		return super.getMarkEditTextParams(editText);
	}
	
	public interface WindowInterface {
		/**
		 * 获取行间距
		 * @return
		 */
		int getWidgetHeightMargin();
		
		/**
		 * 获取两组控件之间的间距
		 * @return
		 */
		int getWidgetWidthMargin();
		
		/**
		 * 获取界面字体大小
		 * @return
		 */
		int getTextSize();
		
		/**
		 * 获取单列下EditText的宽度
		 * @return
		 */
		int getOneLineEditTextWidth();
		
		/**
		 * 获取多列下EditText的宽度
		 * @return
		 */
		int getMultiLineEditTextWidth();
	}
}
