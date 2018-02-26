package com.pm360.cepm360.app.common.custinterface;

import android.view.View;
import android.widget.LinearLayout;

import com.pm360.cepm360.common.util.TwoNumber;

public interface BaseWidgetInterface {
	
	TwoNumber<View, LinearLayout.LayoutParams> createExtraLayout();
	
	Integer[] getImportantColumns();
}
