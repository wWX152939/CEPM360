package com.pm360.cepm360.app.common.view.parent;

import android.app.Activity;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.pm360.cepm360.app.common.custinterface.BaseWidgetInterface;

import java.util.Map;

/*
 * popupWindow基类，布局文件在各自xml定义
 */
public abstract class BaseWidget extends BaseWidgetCore {

	public BaseWidget(Activity activity) {
		mActivity = activity;
	}
	
	public BaseWidget(Activity activity, BaseWidgetInterface baseWidgetInterface) {
		mActivity = activity;
		mBaseWidgetInterface = baseWidgetInterface;
	}
	
	/**
	 * get baseDialog interface
	 * @return
	 */
	public View getPopupView() {
		return mBaseView;
	}
	
	/**
	 * 设置指定行是否为只读模式
	 */
	public void setReadOnlyLine(int line, boolean readOnlyMode) {
		super.setReadOnlyLine(line, readOnlyMode, false);
	}
	
	/**
	 * 设置指定行是否为只读模式
	 */
	public void setReadOnlyLine(int line, boolean readOnlyMode, boolean showKeyBoard) {
		super.setReadOnlyLine(line, readOnlyMode, showKeyBoard);
	}
	
	/**
	 * 设置editText背景
	 * @param line
	 * @param backgrondType 0：空，1：普通模式，下划线显示 2：spinner模式显示
	 */
	public void setEditTextBackground(int line, int backgroundType) {
		super.setEditTextBackground(line, backgroundType);
	}
	
	/**
	 * 
	 * @param line
	 * @param drawableId default 0
	 * @param listener default null
	 * @param hintContent if no, set it to null
	 */
	public void setEditTextStyle(int line, int drawableId, OnClickListener listener, String hintContent) {
		setEditTextStyle(line, drawableId, listener, hintContent, true);
	}
	
	public void setEditTextContent(int line, String content) {
		super.setEditTextContent(line, content);
	}
	
	/**
	 * 设置widgetInterface
	 */
	public void setWidgetInterface(WidgetInterface widgetInterface) {
		super.setWidgetInterface(widgetInterface);
	}
	
	/**
	 * 设置radioButton显示位置
	 * @param line 行号
	 * @param content radio的汉字 例 男，女
	 */
	public void setRadioButtonPosition(int line, String content) {
		super.setRadioButtonPosition(line, content);
	}
	
	public void setUserTextContent(int line, int id) {
		super.setUserTextContent(line, id);
	}
	
	public void setTenantContent(int line, int tenantId) {
		super.setTenantContent(line, tenantId);
	}
	
	public void setOBSTextContent(int line, int id) {
		super.setOBSTextContent(line, id);
	}
	
	public EditText getEditTextView(int line) {
		return super.getEditTextView(line);
	}

	/**
	 * 
	 * @param editTexts can be null
	 */
	public void SetDefaultValue(String[] editTexts) {
		super.SetDefaultValue(editTexts);
	}

	/**
	 * saveData interface
	 * @return
	 */
	public Map<String, String> SaveData() {
		return super.SaveData();
	}
	
	protected abstract void setLineStyle(LinearLayout parentLine, LinearLayout line1, android.widget.LinearLayout.LayoutParams layoutParams1, int i);
	protected abstract void setTextViewStyle(TextView textView1);
	protected abstract void setEditTextStyle(EditText et);
	protected abstract android.widget.LinearLayout.LayoutParams getEditTextParams(int i);
	protected abstract android.widget.LinearLayout.LayoutParams getSeekBarParams();

}
