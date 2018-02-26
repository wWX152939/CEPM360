package com.pm360.cepm360.app.cache;

import com.pm360.cepm360.entity.Tenant;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

public class TenantCache extends CepmCache {

    private static Tenant mCurrentTenant;
    private static ArrayList<Tenant> mTenantLists = new ArrayList<Tenant>();
    private static Map<String, String> mTenantIdMaps = new HashMap<String, String>();
    
    /**
     * 获取当前打开的tenant
     * 
     * @return current tenant
     */
    public static Tenant getCurrentTenant() {
        return mCurrentTenant;
    }

    /**
     * 设置打开了的tenant到缓存
     * 
     * @param tenant
     */
    public static void setCurrentTenant(Tenant tenant) {
        mCurrentTenant = tenant;
    }
    
    /**
     * 获取所有tenant列表
     * 
     * @return
     */
    public static ArrayList<Tenant> getTenantLists() {
        return mTenantLists;
    }
    
    /**
     * 设置所有tenant到缓存
     * 
     * @param tenantLists
     */
    public static void setTenantLists(ArrayList<Tenant> tenantLists) {
        mTenantLists.clear();
        mTenantIdMaps.clear();
        
        for (int i = 0; i < tenantLists.size(); i++) {
            Tenant tenant = tenantLists.get(i);
             int id = tenant.getTenant_id();
             String name = tenant.getName();
             mTenantIdMaps.put(String.valueOf(id), name);
             mTenantIdMaps.put(name, String.valueOf(id));
             mTenantLists.add(tenant);
        }
        dumpCache("TenantCache", mTenantLists);
    }
    
    /**
     * 
     * @param tenantId
     * @return
     */
    public static Tenant findTenantById(int tenantId) {
        Tenant tenant = null;
        if (mTenantLists != null) {
            for (Tenant t : mTenantLists) {
                if (t.getTenant_id() == tenantId) {
                    tenant = t;
                    break;
                }
            }
        }
        return tenant;
    }
    
    public static String getTenantName(String tenantId) {
    	return mTenantIdMaps.get(tenantId);
    }
    
    public static Map<String, String> getTenantIdMaps() {
        return mTenantIdMaps;
    }
    
    /**
     * 清除缓存数据
     */
    public static void clear() {
        mCurrentTenant = null;
        mTenantIdMaps.clear();
        mTenantLists.clear();  
    }
    
    /**
     * 是不是当前的tenant
     * @param tenant
     * @return
     */
    public static boolean isCurrentTenant(Tenant tenant) {
        if (mCurrentTenant != null
                && mCurrentTenant.getTenant_id() == tenant.getTenant_id()) {
            return true;
        }
        return false;
    }
}
