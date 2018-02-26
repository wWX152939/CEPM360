package com.pm360.cepm360.app.common.custinterface;

import com.pm360.cepm360.entity.Expandable;

import java.util.List;

public interface TreeExpandInterface {
	/**
	 * 选中/取消一行
	 * @param position
	 * @param isSelected
	 */
    public void setSelected(int position, boolean isSelected);
    
    /**
     * 刷新一行（通常是刷新父）
     * @param position
     */
    public void updateListView(int position);
    
    /**
     * 获取显示列表
     * @return
     */
	public <T extends Expandable> List<T> getShowList();
    
}
