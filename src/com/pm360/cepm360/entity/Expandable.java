package com.pm360.cepm360.entity;

public abstract class Expandable {

    private int level;
    private boolean has_Child;
    private boolean expanded;
    private boolean selected; // 是否选中
    private int count;

    /**
     * @return the count
     */
    public int getCount() {
	return count;
    }

    /**
     * @param count
     *            the count to set
     */
    public void setCount(int count) {
	this.count = count;
    }

    public int getLevel() {
	return level;
    }

    public void setLevel(int level) {
	this.level = level;
    }

    public boolean isHas_child() {
	return has_Child;
    }

    public void setHas_child(boolean hasChild) {
	this.has_Child = hasChild;
    }

    public boolean isExpanded() {
	return expanded;
    }

    public void setExpanded(boolean expanded) {
	this.expanded = expanded;
    }


    public abstract int getId();

    public abstract int getParents_id();
    
    public abstract void setParentsId(int parent_id);
    
    /**
     * 获取实际ID，用于多个对象连接为一个对象时使用
     * @return
     */
    public int getRealId() {
    	return 0;
    }

    public boolean isSelected() {
        return selected;
    }

    public void setSelected(boolean selected) {
        this.selected = selected;
    }

    @Override
    public String toString() {
        return "Expandable [level=" + level + ", has_Child=" + has_Child
                + ", expanded=" + expanded + ", selected=" + selected
                + ", count=" + count + "]";
    }
    
    
}
