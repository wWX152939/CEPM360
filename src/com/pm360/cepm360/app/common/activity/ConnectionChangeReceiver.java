
package com.pm360.cepm360.app.common.activity;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;

import com.pm360.cepm360.app.cache.UserCache;
import com.pm360.cepm360.app.module.message.MessageService;
import com.pm360.cepm360.entity.User;

/**
 * 网络连接广播监听处理
 * 
 * 有网络时， 开启消息服务； 无网络时，停止消息服务；
 * 当然这里也可以检测如果是移动数据时，提示用户是否继续使用移动网络，等等
 *
 */
public class ConnectionChangeReceiver extends BroadcastReceiver {

    @Override
    public void onReceive(Context context, Intent intent) {
        User user = UserCache.getCurrentUser();
        if (user == null)
            return;
        ConnectivityManager connectivityManager = (ConnectivityManager) context
                .getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo mobNetInfo = connectivityManager
                .getNetworkInfo(ConnectivityManager.TYPE_MOBILE);
        NetworkInfo wifiNetInfo = connectivityManager.getNetworkInfo(ConnectivityManager.TYPE_WIFI);
        Intent msg_intent = new Intent(context, MessageService.class);
        if (!mobNetInfo.isConnected() && !wifiNetInfo.isConnected()) {
            context.stopService(msg_intent);
        } else {
            context.startService(msg_intent);
//            Project project = ProjectCache.getCurrentProject();
//            if (project != null) {
//                getPermissionByProject(project, user);
//            }
        }
    }
    
//    private void getPermissionByProject(Project project, User user) {
//        RemoteCommonService.getInstance().getPermissionByProject(
//                new DataManagerInterface() {
//
//                    @Override
//                    public void getDataOnResult(ResultStatus status,
//                            List<?> list) {
//                        if (status.getCode() == AnalysisManager.SUCCESS_DB_QUERY
//                                && list != null && list.size() > 0) {
//                            StringBuilder builder = new StringBuilder();
//                            for (Object object : list) {
//                                if (object instanceof Role) {
//                                    Role role = (Role) object;
//                                    String action = role.getAction();
//                                    if (action != null) {
//                                        builder.append(action + ",");
//                                    }
//                                }
//                            }
//                            String role_text = builder.toString();
//                            if (role_text.endsWith(",")) {
//                                role_text = role_text.substring(0,
//                                        role_text.length() - 1);
//                            }
//                            PermissionCache.setProjectPermissions(role_text.split(","));
//                        }
//                    }
//                }, project.getProject_id(), user);
//    }

}
