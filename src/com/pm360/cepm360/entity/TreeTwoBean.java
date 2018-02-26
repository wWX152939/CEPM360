package com.pm360.cepm360.entity;

import java.io.Serializable;

public class TreeTwoBean extends Expandable implements Serializable {
	private static final long serialVersionUID = 5126930294796470476L;
	
	private int virtualId;
	private int realId;
	private String name;
	private int parentId;
	private boolean isParent;
	private int tenantId;
	private int level;
	private boolean hasChild;
	private boolean expanded;

	/**
	 * @return the virtualId
	 */
	public int getVirtualId() {
		return virtualId;
	}

	/**
	 * @param virtualId the virtualId to set
	 */
	public void setVirtualId(int virtualId) {
		this.virtualId = virtualId;
	}

	/**
	 * @return the realId
	 */
	@Override
	public int getRealId() {
		return realId;
	}

	/**
	 * @param realId the realId to set
	 */
	public void setRealId(int realId) {
		this.realId = realId;
	}

	/**
	 * @return the Name
	 */
	public String getName() {
		return name;
	}

	/**
	 * @param Name the Name to set
	 */
	public void setName(String name) {
		this.name = name;
	}

	/**
	 * @return the parentId
	 */
	public int getParentId() {
		return parentId;
	}

	/**
	 * @param parentId the parentId to set
	 */
	public void setParentId(int parentId) {
		this.parentId = parentId;
	}

	/**
	 * @return the isParent
	 */
	public boolean isParent() {
		return isParent;
	}

	/**
	 * @param isParent the isParent to set
	 */
	public void setParent(boolean isParent) {
		this.isParent = isParent;
	}

	/**
	 * @return the tenantId
	 */
	public int getTenantId() {
		return tenantId;
	}

	/**
	 * @param tenantId the tenantId to set
	 */
	public void setTenantId(int tenantId) {
		this.tenantId = tenantId;
	}

	/**
	 * @return the level
	 */
	public int getLevel() {
		return level;
	}

	/**
	 * @param level the level to set
	 */
	public void setLevel(int level) {
		this.level = level;
	}

	/**
	 * @return the hasChild
	 */
	public boolean isHasChild() {
		return hasChild;
	}

	/**
	 * @param hasChild the hasChild to set
	 */
	public void setHasChild(boolean hasChild) {
		this.hasChild = hasChild;
	}

	/**
	 * @return the mExpanded
	 */
	public boolean isExpanded() {
		return expanded;
	}

	/**
	 * @param mExpanded the mExpanded to set
	 */
	public void setExpanded(boolean expanded) {
		this.expanded = expanded;
	}

	/* (non-Javadoc)
	 * @see java.lang.Object#toString()
	 */
	@Override
	public String toString() {
		return "TreeTwoBean [virtualId=" + virtualId + ", realId=" + realId
				+ ", name=" + name + ", parentId=" + parentId
				+ ", isParent=" + isParent + ", tenantId=" + tenantId + ", level="
				+ level + ", hasChild=" + hasChild + ", expanded=" + expanded
				+ "]";
	}

	@Override
	public int getParents_id() {
		return parentId;
	}

	@Override
	public void setParentsId(int parent_id) {
		this.parentId = parent_id;
	}

	@Override
	public int getId() {
		return virtualId;
	}
}
