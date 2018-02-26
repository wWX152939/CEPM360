package com.pm360.cepm360.app.module.schedule;

import com.pm360.cepm360.entity.User;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;


public class PlanDataCache {
	private static PlanDataCache mUserDataCache;
	private String[] mUserNames;
	private int[] mUserIds;
	private Map<String, Integer> mUserMap = new HashMap<String, Integer>();
	private Map<Integer, String> mUserMap2 = new HashMap<Integer, String>();
	
	private String[] mCompanyUserNames;
	private int[] mCompanyUserIds;
	private Map<String, Integer> mCompanyUserMap = new HashMap<String, Integer>();
	private Map<Integer, String> mCompanyUserMap2 = new HashMap<Integer, String>();

	private Map<String, Integer> mOBSMap = new HashMap<String, Integer>();
	private Map<Integer, String> mOBSMap2 = new HashMap<Integer, String>();
	private ArrayList<User> mProjectUserList = new ArrayList<User>();
	private ArrayList<User> mCompanyUserList = new ArrayList<User>();
	
	public String[] getCompanyUserNames() {
		return mCompanyUserNames;
	}

	public void setCompanyUserNames(String[] mCompanyUserNames) {
		this.mCompanyUserNames = mCompanyUserNames;
	}

	public int[] getCompanyUserIds() {
		return mCompanyUserIds;
	}

	public void setCompanyUserIds(int[] mCompanyUserIds) {
		this.mCompanyUserIds = mCompanyUserIds;
	}

	public Map<String, Integer> getCompanyUserMap() {
		return mCompanyUserMap;
	}

	public void setCompanyUserMap(Map<String, Integer> mCompanyUserMap) {
		this.mCompanyUserMap = mCompanyUserMap;
	}

	public Map<Integer, String> getCompanyUserMap2() {
		return mCompanyUserMap2;
	}

	public void setCompanyUserMap2(Map<Integer, String> mCompanyUserMap2) {
		this.mCompanyUserMap2 = mCompanyUserMap2;
	}
	
	public static synchronized PlanDataCache getInstance() {
	       if (mUserDataCache == null) {
	    	   mUserDataCache = new PlanDataCache();
	       }
	       return mUserDataCache;
	}
	
	public Map<String, Integer> getUserMap() {
		return mUserMap;
	}

	public void setUserMap(Map<String, Integer> mUserMap) {
		this.mUserMap = mUserMap;
	}

	public Map<String, Integer> getOBSMap() {
		return mOBSMap;
	}

	public void setOBSMap(Map<String, Integer> mOBSMap) {
		this.mOBSMap = mOBSMap;
	}

	public Map<Integer, String> getOBSMap2() {
		return mOBSMap2;
	}

	public void setOBSMap2(Map<Integer, String> mOBSMap2) {
		this.mOBSMap2 = mOBSMap2;
	}

	public Map<Integer, String> getUserMap2() {
		return mUserMap2;
	}

	public void setUserMap2(Map<Integer, String> mUserMap2) {
		this.mUserMap2 = mUserMap2;
	}

	public String[] getUserNames() {
		return mUserNames;
	}
	public void setUserNames(String[] mUserLoginNames) {
		this.mUserNames = mUserLoginNames;
	}
	public int[] getUserIds() {
		return mUserIds;
	}
	public void setUserIds(int[] mUserIds) {
		this.mUserIds = mUserIds;
	}
	
	public ArrayList<User> getProjectUserList() {
		return mProjectUserList;
	}
	public void setProjectUserList(ArrayList<User> userList) {
		mProjectUserList = userList;
	}
	public ArrayList<User> getCompanyUserList() {
		return mCompanyUserList;
	}
	public void setCompanyUserList(ArrayList<User> companyList) {
		mCompanyUserList = companyList;
	}
	
}
