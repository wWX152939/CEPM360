
package com.pm360.cepm360.app.module.message;

import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.Service;
import android.content.ContentResolver;
import android.content.ContentValues;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.Binder;
import android.os.IBinder;

import com.pm360.cepm360.R;
import com.pm360.cepm360.app.cache.UserCache;
import com.pm360.cepm360.app.common.activity.DataManagerInterface;
import com.pm360.cepm360.app.common.custinterface.CallBack;
import com.pm360.cepm360.app.module.email.EmailActivity;
import com.pm360.cepm360.app.utils.UtilTools;
import com.pm360.cepm360.common.GLOBAL;
import com.pm360.cepm360.common.ResultStatus;
import com.pm360.cepm360.common.json.AnalysisManager;
import com.pm360.cepm360.common.util.DateUtils;
import com.pm360.cepm360.common.util.LogUtil;
import com.pm360.cepm360.entity.Message;
import com.pm360.cepm360.services.message.RemoteMessageService;

import java.util.ArrayList;
import java.util.List;
import java.util.Timer;
import java.util.TimerTask;

/**
 * MessageService
 * 
 * 	1. Service启动后，获取当前用户的所有消息：getAllMessage(), 
 * 	          设置Timer每隔5秒去检查未读消息getUnRreadMessage().
 * 	2. 将获取到消息写到本地数据库，并发送广播通知ACTION_MESSAGE 和 状态栏Notification
 *
 */
public class MessageService extends Service {

    private static final int TIME = 5;
    private static final int MESSAGE_NOTIFICATION = 1;
    public static final Uri MESSAGE_URI = Uri
            .parse("content://com.pm360.cepm360.provider/message");
    public static final String ACTION_MESSAGE = "com.pm360.cepm360.message";
    
    // 消息类型，如邮件：15， 来往函会处理这类的广播
    public static final String MESSAGE_TYPE = "message_type";
    // 这里的flag是为了第一次拉所有消息列表时，HomeActivity不处理此广播，而在拉未读时才处理。
    public static final String MESSAGE_FLAG = "message_flag";
    
    private final IBinder mBinder = new LocalBinder();
    private Timer mTimer;
    private MyTimerTask mTimerTask;
    private ContentResolver mResolver;
    private int[] mUnReadCount = new int[6];
    private ArrayList<Message> mMessageList = new ArrayList<Message>();
    private NotificationManager mManager;
    
    // 有时其他模块要查找某一条消息，但是MessageService还没有收到（每隔5秒检查），或者被别设备接收了。
    // 这里的做法是将要查询的消息暂存为mPendingMessage，重新来一次getAllMessage，进行匹配，
    // 并通过callBack将找到的消息返回给调用者
    private Message mPendingMessage = new Message();

    @Override
    public void onCreate() {
        super.onCreate();

        mManager = (NotificationManager)
                getSystemService(Context.NOTIFICATION_SERVICE);
        
        mResolver = getContentResolver();
        getAllMessage(null);

        mTimer = new Timer();
        mTimerTask = new MyTimerTask();
        mTimer.schedule(mTimerTask, 0, TIME * 1000);
    }

    private void getAllMessage(final CallBack<Void, Message> callBack) {        
        mResolver.delete(MESSAGE_URI, null, null);
        resetUnread();
        
        if (UserCache.getCurrentUser() != null) {
            RemoteMessageService.getInstance().getALLMessage(new DataManagerInterface() {
    
                @SuppressWarnings("unchecked")
                @Override
                public void getDataOnResult(ResultStatus status, List<?> list) {
                    if (status.getCode() != AnalysisManager.SUCCESS_DB_QUERY
                            & status.getMessage() != null && !status.getMessage().equals("")) {
                        UtilTools.showToast(MessageService.this, status.getMessage());
                    }
                    if ((status.getCode() == AnalysisManager.SUCCESS_DB_QUERY)
                            && list != null
                            && list.size() > 0) {
                        mMessageList.clear();
                        mMessageList.addAll((List<Message>) list);
                        messageInsert((List<Message>) list);
                        Message lastMsg = null;                    
                        for (Object object : list) {
                            if (object instanceof Message) {
                                Message msg = (Message) object;
                                //messageInsert(msg);
                                if (msg.getIs_read() == Integer.valueOf(GLOBAL.MSG_READ[0][0])
                                        && msg.getType() > 0) {
                                    int tabType = getTabType(msg.getType());
                                    int type = msg.getType();
                                    int typeId = msg.getType_id();                             
                                    if (tabType != -1) {
                                        mUnReadCount[getTabType(msg.getType())]++;
                                        Intent intent = new Intent(ACTION_MESSAGE);
                                        intent.putExtra(MESSAGE_FLAG, 0);
                                        sendBroadcast(intent);
                                        lastMsg = msg;
                                        
                                        if (type == mPendingMessage.getType() 
                                                && typeId == mPendingMessage.getType_id()) {
                                            mPendingMessage = msg;
                                        }
                                    }                                
                                }
                            }
                        }
                        if (lastMsg != null && lastMsg.getType() > 0) {
                            notification(lastMsg);
                        }
                        if (callBack != null) {
                            callBack.callBack(mPendingMessage);
                        }
                    }
                }
            }, UserCache.getCurrentUser());
        }
    }

    @Override
    public IBinder onBind(Intent intent) {
        return mBinder;
    }

    /**
     * 发送广播通知系统，在系统通知栏显示
     * 
     * @param msg
     */
    private void notification(Message msg) {
        Intent msg_intent = new Intent(Intent.ACTION_MAIN);
        msg_intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        if (msg.getType() == Integer.valueOf(GLOBAL.MSG_TYPE_KEY[14][0])) {
            // 邮件
            msg_intent.setClass(this, EmailActivity.class);
        } else {
            msg_intent.setClass(this, MessageActivity.class);
            msg_intent.putExtra("tabIndex", getTabType(msg.getType()));
        }
        PendingIntent pendingIntent = PendingIntent.getActivity(getApplicationContext(), 0,
                msg_intent,
                PendingIntent.FLAG_UPDATE_CURRENT);
        Notification notification = new Notification.Builder(this)
                .setAutoCancel(true)
                .setContentTitle(GLOBAL.MSG_TYPE_KEY[msg.getType()-1][1])
                .setContentText(msg.getTitle())
                .setContentIntent(pendingIntent)
                .setSmallIcon(R.drawable.ic_launcher)
                .setWhen(System.currentTimeMillis())
                .setDefaults(Notification.DEFAULT_ALL)
                .build();
        mManager.notify(MESSAGE_NOTIFICATION, notification);
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        mTimer.cancel();
        mManager.cancel(MESSAGE_NOTIFICATION);
    }
    
    private void messageInsert(final List<Message> messageList) {
        Thread t = new Thread(new Runnable() {
            @Override
            public void run() {
                for (Message msg : messageList) {
                    if (msg.getType() > 0) {
                        messageInsert(msg);
                    }
                }
            }
        });
        t.start();
    }

    /**
     * 将消息插入到本地数据库
     * 
     * @param messageList
     */
    private Uri messageInsert(Message msg) {
        //mMessageList.add(msg);
        ContentValues contentValues = new ContentValues();
        contentValues.put("message_id", msg.getMessage_id());
        contentValues.put("type_id", msg.getType_id());
        contentValues.put("title", msg.getTitle());
        contentValues.put("type", msg.getType());
        contentValues.put("user_id", msg.getUser_id());
        contentValues.put("time", DateUtils.dateToString(DateUtils.FORMAT_LONG, msg.getTime()));
        contentValues.put("is_read", msg.getIs_read());
        contentValues.put("is_push", msg.getIs_push());
        contentValues.put("is_process", msg.getIs_process());
        contentValues.put("tab_type", getTabType(msg.getType()));
        Uri insertUri = mResolver.insert(MESSAGE_URI, contentValues);
        return insertUri;
    }

    private void resetUnread() {
        for (int i = 0; i < mUnReadCount.length; i++) {
            mUnReadCount[i] = 0;
        }
    }
    
    public int[] getUnreadCount() {
        return mUnReadCount;
    }

    public Message getMessageById(int message_id) {
        Message res = null;
        for (Message message : mMessageList) {
            if (message.getMessage_id() == message_id) {
                res = message;
                break;
            }
        }
        return res;
    }
    
    /**
     * 通过type_id, type查询message对象，通过callback返回message对象。
     * 
     * @param type_id
     * @param type
     * @param callBack
     */
    public void getMessageByType(int type_id, int type, CallBack<Void, Message> callBack) {
        Message res = null;
        for (Message message : mMessageList) {
            if (message.getType_id() == type_id && message.getType() == type) {
                res = message;
                break;
            }
        }
        
        if (res != null) {
            callBack.callBack(res);
        } else {
            mPendingMessage.setType_id(type_id);
            mPendingMessage.setType(type);
            getAllMessage(callBack);
        }
        
    }
    
    /**
     * 根据type_id & type 查找消息对象
     * @param type_id
     * @param type
     * @return
     */
    private Message getMessageByType(int type_id, int type) {
        Message res = null;
        for (Message message : mMessageList) {
            if (message.getType_id() == type_id && message.getType() == type) {
                res = message;
                break;
            }
        }
        return res;
    }
    
    /**
     * 设置消息为已读
     * 
     * @param type_id 邮件id
     * @param type 邮件类型
     */
    public void readMessage(int type_id, int type) {
        Message res = getMessageByType(type_id, type);   
        LogUtil.e("res = " + res);
        if (res != null) {
            readMessage(res.getMessage_id());
        } else {
            mPendingMessage.setType_id(type_id);
            mPendingMessage.setType(type);
            getAllMessage(null);
        }
    }

    /**
     * 设置某一条消息为已读
     * @param message_id
     */
    public void readMessage(int message_id) {
        Message msg = getMessageById(message_id);
        LogUtil.e("msg = " + msg);
        if (msg != null && msg.getIs_read() == Integer.valueOf(GLOBAL.MSG_READ[0][0])) {
            RemoteMessageService.getInstance().readMessage(new DataManagerInterface() {
                @Override
                public void getDataOnResult(ResultStatus status, List<?> list) {
                    if (status.getMessage() != null && !status.getMessage().equals("")) {
                        UtilTools.showToast(MessageService.this, status.getMessage());
                    }
                }
            }, msg);
            readLocalMessage(msg.getMessage_id());
        }
    }
    
    /**
     * 设置本地消息为已读
     * 
     * @param message_id
     */
    public void readLocalMessage(int message_id) {
        Message msg = getMessageById(message_id);
        if (msg != null) {
	        msg.setIs_read(Integer.valueOf(GLOBAL.MSG_READ[1][0]));
	        ContentValues cv = new ContentValues();
	        cv.put("is_read", Integer.valueOf(GLOBAL.MSG_READ[1][0]));
	        mResolver.update(MESSAGE_URI, cv, "message_id=?", new String[] {
	                msg.getMessage_id() + ""
	        });
	        if (getTabType(msg.getType()) != -1) {
	        	mUnReadCount[getTabType(msg.getType())]--;
	        }
	        Intent intent = new Intent(ACTION_MESSAGE);
            intent.putExtra(MESSAGE_FLAG, 1);
            intent.putExtra(MESSAGE_TYPE, msg.getType());
	        sendBroadcast(intent);
        }
    }
    
    /**
     * 设置某一条消息为已处理
     * 
     * @param message_id
     */
    public void processMessage(int message_id) {
        Message msg = getMessageById(message_id);
        if (msg.getIs_process() == Integer.valueOf(GLOBAL.MSG_IS_PROCESS[0][0])) {
            msg.setIs_process(Integer.valueOf(GLOBAL.MSG_IS_PROCESS[1][0]));
            ContentValues cv = new ContentValues();
            cv.put("is_process", Integer.valueOf(GLOBAL.MSG_IS_PROCESS[1][0]));
            mResolver.update(MESSAGE_URI, cv, "message_id=?", new String[] {
                    msg.getMessage_id() + ""
            });
        }
    }
    
    /**
     * 删除本地数据库中某一条消息
     * 
     * @param type_id 
     * @param type  消息类型
     */
    public void deleteLocalMessage(int type_id, int type) {
        Message msg = getMessageByType(type_id, type);
        if (msg != null) {
            mResolver.delete(MESSAGE_URI, "message_id=?",
                    new String[] { msg.getMessage_id() + "" });
            mMessageList.remove(msg);
            
            // 如果当前消息处于未读状态，则更新UI的显示
            if (msg.getIs_read() == 0) {
            	if (getTabType(msg.getType()) != -1) {
            		mUnReadCount[getTabType(msg.getType())]--;
            	}
                Intent intent = new Intent(ACTION_MESSAGE);
                intent.putExtra(MESSAGE_FLAG, 1);
                intent.putExtra(MESSAGE_TYPE, msg.getType());
                sendBroadcast(intent);
            }
        }
    }

    // 我的任务：(8)(16)
    // 我的待办：(1,2,3,4,5,6,7,13,14,22,23,24)
    // 最新反馈：(9,10,17,18,19,20,21)
    // 最新文档：(11,25,26,27,28,29,30)
    // 往来函：(15)
    // 公告：(12)
    // 置顶：(31) 不在message列表中显示，在此不做归类
    private int getTabType(int type) {
        int res = -1;
        switch (type) {
            case 8:
            case 16:
                // 我的任务
                res = 0;
                break;
            case 1:
            case 2:
            case 3:
            case 4:
            case 5:
            case 6:
            case 7:
            case 13:
            case 14:
            case 22:
            case 23:
            case 24:
            case 32:
            case 33:
                // 我的代办
                res = 1;
                break;
            case 9:
            case 10:
            case 17:
            case 18:
            case 19:
            case 20:
            case 21:
                // 最新反馈
                res = 2;
                break;
            case 11:
            case 25:
            case 26:
            case 27:
            case 28:
            case 29:
            case 30:
                // 最新文档
                res = 3;
                break;
            case 15:
                // 来往函
                res = 4;
                break;
            case 12:
            case 0: //如果为0，是错误，说明服务端不应该发送这个消息，这里只是防止App崩溃而已。
                // 公告
                res = 5;
                break;
        }
        return res;
    }

    public class LocalBinder extends Binder {

        public MessageService getService() {
            return MessageService.this;
        }
    }

    class MyTimerTask extends TimerTask {

        @Override
        public void run() {
            if (UserCache.getCurrentUser() == null) {
                MessageService.this.stopSelf();
                return;
            }
            
            // 每个5秒轮询服务器，获取未读消息
            RemoteMessageService.getInstance().getUnRreadMessage(new DataManagerInterface() {

                @SuppressWarnings("unchecked")
                @Override
                public void getDataOnResult(ResultStatus status, List<?> list) {

                    if (status.getCode() != AnalysisManager.SUCCESS_DB_QUERY
                            && status.getMessage() != null &&
                            !status.getMessage().equals("")) {
                        UtilTools.showToast(MessageService.this, status.getMessage());
                    }
                    if ((status.getCode() == AnalysisManager.SUCCESS_DB_QUERY)
                            && list != null
                            && list.size() > 0) {
                        mMessageList.addAll((List<Message>) list);
                        messageInsert((List<Message>) list);
                        notifyUI((List<Message>) list);
                    }
                }
            }, UserCache.getCurrentUser());
        }
    }
    
    private void notifyUI(List<Message> list) {
        for (Message msg : list) {
            if (msg.getType() > 0) {
            	if (getTabType(msg.getType()) != -1) {            
            		notification(msg);
            		mUnReadCount[getTabType(msg.getType())]++;
            	}
                Intent intent = new Intent(ACTION_MESSAGE);
                intent.putExtra(MESSAGE_FLAG, 1);
                intent.putExtra(MESSAGE_TYPE, msg.getType());
                sendBroadcast(intent);
            }
        }
    }
}
