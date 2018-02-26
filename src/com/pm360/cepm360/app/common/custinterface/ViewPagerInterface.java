package com.pm360.cepm360.app.common.custinterface;

import com.pm360.cepm360.entity.Expandable;

import java.io.Serializable;
import java.util.List;

public interface ViewPagerInterface<T extends Serializable, B extends Expandable> {
	/**
	 * 传入 array id
	 * @return
	 */
	public String[] getViewPagerTitle();
	@SuppressWarnings("rawtypes")
	public List<TaskRelevanceChildInterface> getFragment();
	
}
