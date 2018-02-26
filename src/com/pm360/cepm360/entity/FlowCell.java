package com.pm360.cepm360.entity;

import java.io.Serializable;

/**
 * FlowCell
 * 
 * @author OneKey
 * 
 */
public interface FlowCell extends Serializable {

    public int getId();

    public void setId(int id);

    public int getStatus();

    public void setStatus(int status);

    public int getPlan_person();
    
    public void setPlan_person(int plan_person);
}
