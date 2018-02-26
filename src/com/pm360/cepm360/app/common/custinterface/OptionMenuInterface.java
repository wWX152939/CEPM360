package com.pm360.cepm360.app.common.custinterface;

import com.pm360.cepm360.app.common.view.OptionsMenuView.SubMenuListener;

public interface OptionMenuInterface {
	/**
	 * 获取选项菜单名字资源ID
	 * @return
	 */
	int getOptionMenuNames();
	
	/**
	 * 获取选择菜单监听器
	 * @return
	 */
	SubMenuListener getOptionMenuClickListener();
}
