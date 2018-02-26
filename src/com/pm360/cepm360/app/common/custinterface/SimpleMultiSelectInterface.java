package com.pm360.cepm360.app.common.custinterface;



/**
 * 多选模式接口
 * 标题: SimpleMultiSelectInterface 
 * 描述: 
 * 作者： onekey
 * 日期： 2016年4月15日
 *
 */
public interface SimpleMultiSelectInterface {
	
	/**
	 * 实现该接口，在进入退出多选模式时响应操作
	 * @param enable
	 */
	void setEnableMultiSelectMode(boolean enable);
	
	/**
	 * 多选模式下获取没有选择数据的删除提示符ID
	 * @return
	 */
	int getNoSelectDeleteToastStringId();
}
