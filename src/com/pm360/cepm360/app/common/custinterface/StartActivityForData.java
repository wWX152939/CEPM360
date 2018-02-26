package com.pm360.cepm360.app.common.custinterface;

import android.content.Intent;

public interface StartActivityForData {

	/**
	 * 启动一个Activity，并获取返回数据
	 * @param intent
	 * @param requestCode
	 */
	public void startActivityForData(Intent intent, int requestCode);
}
