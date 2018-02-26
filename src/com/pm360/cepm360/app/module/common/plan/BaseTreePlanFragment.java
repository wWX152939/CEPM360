package com.pm360.cepm360.app.module.common.plan;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.pm360.cepm360.app.common.view.parent.BaseTreeListWithFrameLayoutFragment;
import com.pm360.cepm360.app.module.schedule.BasePlanActivity;
import com.pm360.cepm360.entity.Expandable;

import java.io.Serializable;

/**
 * 
 * 标题: BaseTreePlanFragment 
 * 描述: 计划和组合使用
 * 作者： onekey
 * 日期： 2016年1月5日
 *
 * @param <T>
 */
public abstract class BaseTreePlanFragment<T extends Expandable & Serializable> extends BaseTreeListWithFrameLayoutFragment<T, BasePlanActivity> {
	
	@Override
	public View onCreateView( LayoutInflater inflater,
			  ViewGroup container,
			  Bundle savedInstanceState) {
		return super.onCreateView(inflater, container, savedInstanceState);
	}
	
	@Override
	protected boolean needSelectProject() {
		return !mActivity.gIsFirstSelectProject;
	}
	
}
