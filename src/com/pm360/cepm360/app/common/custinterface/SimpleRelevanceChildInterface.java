package com.pm360.cepm360.app.common.custinterface;



/**
 * 父子关系界面子需要实现的接口
 * 标题: SimpleRelevanceChildInterface 
 * 描述: 
 * 作者： onekey
 * 日期： 2016年4月15日
 *
 */
public interface SimpleRelevanceChildInterface<B> {
	
	/**
	 * 设置相关界面处理父的响应时间，如加载数据。
	 * @param parentBean 父对象
	 */
	void handleParentEvent(B b);

}
