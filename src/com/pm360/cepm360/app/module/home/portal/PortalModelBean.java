package com.pm360.cepm360.app.module.home.portal;

import android.graphics.Bitmap;

import java.util.ArrayList;

public class PortalModelBean {
    
    private int index;
    private boolean isChecked;
    private String title;
    private ArrayList<PortailListItem> dataList;
    private ArrayList<Bitmap> bitmaps;
    
    private int nostart_task_number;
    private int starting_task_number;
    private int end_task_number;
    
    public int getIndex() {
        return index;
    }
    public void setIndex(int index) {
        this.index = index;
    }
    public String getTitle() {
        return title;
    }
    public void setTitle(String title) {
        this.title = title;
    }
    public ArrayList<PortailListItem> getDataList() {
        return dataList;
    }
    public void setDataList(ArrayList<PortailListItem> dataList) {
        this.dataList = dataList;
    }
    public boolean isChecked() {
        return isChecked;
    }
    public void setChecked(boolean isChecked) {
        this.isChecked = isChecked;
    }
    public ArrayList<Bitmap> getBitmaps() {
        return bitmaps;
    }
    public void setBitmaps(ArrayList<Bitmap> bitmaps) {
        this.bitmaps = bitmaps;
    }
	public int getNostart_task_number() {
		return nostart_task_number;
	}
	public void setNostart_task_number(int nostart_task_number) {
		this.nostart_task_number = nostart_task_number;
	}
	public int getStarting_task_number() {
		return starting_task_number;
	}
	public void setStarting_task_number(int starting_task_number) {
		this.starting_task_number = starting_task_number;
	}
	public int getEnd_task_number() {
		return end_task_number;
	}
	public void setEnd_task_number(int end_task_number) {
		this.end_task_number = end_task_number;
	}
	@Override
	public String toString() {
		return "PortalModelBean [index=" + index + ", isChecked=" + isChecked
				+ ", title=" + title + ", dataList=" + dataList + ", bitmaps="
				+ bitmaps + ", nostart_task_number=" + nostart_task_number
				+ ", starting_task_number=" + starting_task_number
				+ ", end_task_number=" + end_task_number + "]";
	}

}
