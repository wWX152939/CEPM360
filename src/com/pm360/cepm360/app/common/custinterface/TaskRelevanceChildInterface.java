package com.pm360.cepm360.app.common.custinterface;

import com.pm360.cepm360.app.common.adpater.DataTreeListAdapter;
import com.pm360.cepm360.entity.Expandable;
import com.pm360.cepm360.entity.Project;

import java.io.Serializable;
import java.util.List;


/**
 * 父子关系界面子需要实现的接口
 * 标题: RelevanceChildInterface 
 * 描述: 
 * 作者： onekey
 * 日期： 2016年4月15日
 *
 */
public interface TaskRelevanceChildInterface<T extends Serializable, B extends Expandable> extends RelevanceChildInterface<B> {
	
	void setParentList(DataTreeListAdapter<B> listAdapter);

	void setCurrentList(List<T> currentList);
	
	void setCurrentProject(Project prject);
}
