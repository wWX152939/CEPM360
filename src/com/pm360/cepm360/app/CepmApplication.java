
package com.pm360.cepm360.app;

import java.io.PrintWriter;
import java.io.StringWriter;

import android.app.Application;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.location.Location;
import android.location.LocationManager;
import android.text.TextUtils;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.WindowManager;

import com.pm360.cepm360.app.cache.EpsCache;
import com.pm360.cepm360.app.cache.PermissionCache;
import com.pm360.cepm360.app.cache.ProjectCache;
import com.pm360.cepm360.app.cache.RoleCache;
import com.pm360.cepm360.app.cache.UserCache;
import com.pm360.cepm360.app.common.activity.LoginActivity;
import com.pm360.cepm360.common.util.DateUtils;
import com.pm360.cepm360.common.util.FileUtils;
import com.pm360.cepm360.entity.DeviceInfo;

public class CepmApplication extends Application implements Thread.UncaughtExceptionHandler {
    
    private int mEnterpriseType;
    private DeviceInfo mDeviceInfo;
    private boolean mScreenLockState;
    
    @Override
    public void onCreate() {
        super.onCreate();
        // 设置Thread Exception Handler;
        Thread.setDefaultUncaughtExceptionHandler(this);
    }
    
	private String getHandSetInfo(){ 
		@SuppressWarnings("deprecation")
		String handSetInfo= 
		"手机型号:" + android.os.Build.MODEL + 
		",SDK版本:" + android.os.Build.VERSION.SDK + 
		",系统版本:" + android.os.Build.VERSION.RELEASE+ 
		",软件版本:"+ getAppVersionName(this); 
		return handSetInfo; 
	}
	
	//获取当前版本号 
	private String getAppVersionName(Context context) { 
		String versionName = ""; 
		try { 
			PackageManager packageManager = context.getPackageManager(); 
			PackageInfo packageInfo = packageManager.getPackageInfo("cn.testgethandsetinfo", 0); 
			versionName = packageInfo.versionName; 
			if (TextUtils.isEmpty(versionName)) { 
				return ""; 
			} 
			} catch (Exception e) { 
			e.printStackTrace(); 
		} 
		return versionName; 
	} 

    @Override
    public void uncaughtException(Thread thread, Throwable ex) {
        try {
            StringWriter sw = new StringWriter();
            PrintWriter pw = new PrintWriter(sw);
            ex.printStackTrace(pw);
    		String str = DateUtils.getCurrentDate(DateUtils.FORMAT_FULL) + "\r\n";
    		str += getHandSetInfo() + "\r\n";

    		LocationManager locationManager = (LocationManager)getSystemService(Context.LOCATION_SERVICE);
    		Location location = null;
    		if (locationManager.isProviderEnabled(LocationManager.GPS_PROVIDER)) {  
                location = locationManager.getLastKnownLocation(LocationManager.GPS_PROVIDER);  
            }
    		if (location != null) {
    			str += "latitude:" + location.getLatitude() + " longitude:" + location.getLongitude() + "\r\n";
    		}
    		String path = getExternalFilesDir(null).getPath();
            FileUtils.write(path + "/cepm360_exception.txt", "\r\n" + str + "\r\n" + sw.toString() + "\r\n");
            Log.e("CEPM360", "\r\n" + sw.toString() + "\r\n");
        } catch (Exception e2) {
        }

        Intent intent = new Intent(this, LoginActivity.class);
        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP
                | Intent.FLAG_ACTIVITY_CLEAR_TASK
                | Intent.FLAG_ACTIVITY_NEW_TASK);
        startActivity(intent);
        System.exit(0);
    }
    
    public int getEnterpriseType() {
        return mEnterpriseType;
    }
    
    public void setEnterpriseType(int type) {
        mEnterpriseType = type;
    }
    
    public DeviceInfo getDeviceInfo() {
        return mDeviceInfo;
    }

    @SuppressWarnings("deprecation")
    public void setDeviceInfo() {
        DisplayMetrics metrics = new DisplayMetrics();
        WindowManager wm = (WindowManager) this
                .getSystemService(Context.WINDOW_SERVICE);
        wm.getDefaultDisplay().getMetrics(metrics); 
        int densityDpi = metrics.densityDpi;
        int width = wm.getDefaultDisplay().getWidth();
        int height = wm.getDefaultDisplay().getHeight();
        DeviceInfo devInfo = new DeviceInfo();
        devInfo.setDensityDpi(densityDpi);
        devInfo.setHeight(height);
        devInfo.setWidth(width);
        mDeviceInfo = devInfo;
    }
        
    public boolean getScreenLockState() {
        return mScreenLockState;
    }
    
    public void setScreenLockState(boolean locked) {
        mScreenLockState = locked;
    }

    public void clear() {
        UserCache.clear();
        ProjectCache.clear();
        PermissionCache.clear();
        EpsCache.clear();
        RoleCache.clear();
    }
}
