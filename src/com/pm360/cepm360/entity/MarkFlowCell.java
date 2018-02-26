package com.pm360.cepm360.entity;


/**
 * FlowCell
 * 
 * @author OneKey
 * 
 */
public interface MarkFlowCell extends FlowCell {

	public void setMark(String mark);
	
    public String getMark();
    
    public void setTotal_money(double money);
    
    public double getTotal_money();
}
