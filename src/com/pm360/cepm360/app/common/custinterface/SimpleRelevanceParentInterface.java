package com.pm360.cepm360.app.common.custinterface;



/**
 * 父子关系界面父需要实现的接口
 * 标题: SimpleRelevanceParentInterface 
 * 描述: 
 * 作者： onekey
 * 日期： 2016年4月15日
 *
 */
public interface SimpleRelevanceParentInterface<T> {
	
	/**
	 * 设置相关父界面的当前对象
	 * @param t
	 */
	void setCurrentParentBean(T t);
	
}
