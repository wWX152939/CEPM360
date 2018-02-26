package com.pm360.cepm360.app.common.custinterface;

import com.pm360.cepm360.app.common.adpater.DataTreeListAdapter;
import com.pm360.cepm360.entity.Expandable;
import com.pm360.cepm360.entity.Project;


/**
 * 父子关系界面父需要实现的接口
 * 标题: SimpleRelevanceParentInterface 
 * 描述: 
 * 作者： onekey
 * 日期： 2016年4月15日
 *
 */
public interface RelevanceParentInterface<T extends Expandable> extends SimpleRelevanceParentInterface<T> {
	
	/**
	 * 
	 * @param listAdapter
	 */
	void setParentList(DataTreeListAdapter<T> listAdapter);
	
	/**
	 * 
	 * @param project
	 */
	void setChildProject(Project project);
}
