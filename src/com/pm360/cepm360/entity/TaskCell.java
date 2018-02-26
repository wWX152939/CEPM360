package com.pm360.cepm360.entity;

import java.io.Serializable;
import java.util.Date;

@SuppressWarnings("serial")
public abstract class TaskCell extends ExpandableSort implements Serializable {

    private int level;
    private boolean has_Child;
    private boolean expanded;
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

    /*
     * (non-Javadoc)
     * 
     * @see java.lang.Object#toString()
     */
    @Override
    public String toString() {
	return "Expandable [level=" + level + ", has_Child=" + has_Child + ", expanded=" + expanded + ", count=" + count + "]";
    }

    public abstract int getId();

    public abstract int getParents_id();
    
    public abstract void setParentsId(int parent_id);
    
    //Task 的逻辑处理
    public abstract Date getActual_start_time();
    
    public abstract void setActual_start_time(Date actual_start_time);

    public abstract Date getActual_end_time();
    
    public abstract void setActual_end_time(Date actual_end_time);
    
    public abstract Date getStart_time();
    
    public abstract Date getEnd_time();
    
    public abstract void setStart_time(Date start_time);

    public abstract void setEnd_time(Date end_time);
    
    public abstract int getStatus();

    public abstract void setStatus(int status);
    
    public abstract int getProgress();

    public abstract void setProgress(int progress);

    public abstract String getPlan_duration();

	public abstract void setPlan_duration(String plan_duration);
	
	public abstract String getActual_duration();
	
	public abstract void setActual_duration(String actual_duration);
	
    public abstract void setCc_user(String cc_user);
    
    public abstract void setMark(String mark);

    public abstract int getTask_id();
    
    public abstract int getTenant_id();
    
    public abstract int getProject_id();
    
    public abstract int getZh_group_id();
    
    public abstract void setZh_group_id(int ground_id);
    
    // For first create, setTask_id to 0
    public abstract void setTask_id(int id);
    
    public abstract String getChange_id();
    
    public abstract String getName();
    
    public abstract String getCc_user();
    
    public abstract String getMark();
    
    public abstract int getCreater();
    
    public abstract int getOwner();
    
    public abstract void setName(String name);
    
    public abstract void setType(String type);
    
    public abstract void setDepartment(int department);
    
    public abstract int getDepartment();
    
    public abstract String getType();
    
    public abstract void setTenant_id(int tenant_id);
    
    public abstract void setParents_id(int parent_id);
    
    public abstract void setOwner(int owner);
    
    public abstract void setPublish(int publish);
    
    public abstract int getPublish();
    
    public abstract void setProject_id(int project_id);

}
