package com.pm360.cepm360.app.module.invitebid;

import android.annotation.SuppressLint;

import java.util.HashMap;
import java.util.Map;

public class InviteBidDataCache {
	private static InviteBidDataCache mDataCache;
	@SuppressLint("UseSparseArrays") 
	private Map<Integer, Integer> mPlanIdCache = new HashMap<Integer, Integer>();
	
	public static synchronized InviteBidDataCache getInstance() {
		if (mDataCache == null) {
			mDataCache = new InviteBidDataCache();
		}
		return mDataCache;
	}
	
	public void setPlanIdCache(Map<Integer, Integer> planIdCache) {
		mPlanIdCache = planIdCache;
	}
	
	public Map<Integer, Integer> getPlanIdCache() {
		return mPlanIdCache;
	}
}
