package com.pm360.cepm360.entity;

import java.io.Serializable;
import java.util.Date;

public class Templet_WBS_dir extends ExpandableSort implements Serializable {

    /**
     * 
     */
    private static final long serialVersionUID = 2228796797875014292L;
    private int templet_wbs_dir_id;
    private int templet_wbs_id;
    private String name;
    private int p_templet_wbs_dir_id;

    private Date start_time;
    private Date end_time;
    private String duration;
    private int sort;

    public int getTemplet_wbs_dir_id() {
	return templet_wbs_dir_id;
    }

    public void setTemplet_wbs_dir_id(int templet_wbs_dir_id) {
	this.templet_wbs_dir_id = templet_wbs_dir_id;
    }

    public int getTemplet_wbs_id() {
	return templet_wbs_id;
    }

    public void setTemplet_wbs_id(int templet_wbs_id) {
	this.templet_wbs_id = templet_wbs_id;
    }

    public String getName() {
	return name;
    }

    public void setName(String name) {
	this.name = name;
    }

    public int getP_templet_wbs_dir_id() {
	return p_templet_wbs_dir_id;
    }

    public void setP_templet_wbs_dir_id(int p_templet_wbs_dir_id) {
	this.p_templet_wbs_dir_id = p_templet_wbs_dir_id;
    }

    public int getSort() {
        return sort;
    }

    public void setSort(int sort) {
        this.sort = sort;
    }

    @Override
    public String toString() {
	return "Templet_WBS_dir [templet_wbs_dir_id=" + templet_wbs_dir_id + ", templet_wbs_id=" + templet_wbs_id + ", name=" + name
	        + ", p_templet_wbs_dir_id=" + p_templet_wbs_dir_id + ", start_time=" + start_time + ", end_time=" + end_time + ", duration="
	        + duration + ", sort=" + sort + "]";
    }

    @Override
    public int getId() {
	return templet_wbs_dir_id;
    }

    public void setId(int id) {
	templet_wbs_dir_id = id;
    }

    @Override
    public int getParents_id() {
	return p_templet_wbs_dir_id;
    }

    public void setParents_id(int parents_id) {
	this.p_templet_wbs_dir_id = parents_id;
    }

    @Override
    public void setParentsId(int parent_id) {
	p_templet_wbs_dir_id = parent_id;
    }

    public Date getStart_time() {
	return start_time;
    }

    public void setStart_time(Date start_time) {
	this.start_time = start_time;
    }

    public Date getEnd_time() {
	return end_time;
    }

    public void setEnd_time(Date end_time) {
	this.end_time = end_time;
    }

    public String getDuration() {
	return duration;
    }

    public void setDuration(String duration) {
	this.duration = duration;
    }

}
