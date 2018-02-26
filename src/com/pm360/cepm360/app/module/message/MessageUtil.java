
package com.pm360.cepm360.app.module.message;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;


public class MessageUtil {

	public static void setMessageToProcessed(Context context, int msgId) {

        ContentValues cv = new ContentValues();
        cv.put("is_process", 1);
        context.getContentResolver().update(MessageService.MESSAGE_URI, cv, "message_id=?", new String[] {
        		msgId + ""
        });
	}
	
	public static int getMessageId(Context context, int msgType, int typeId) {
		Cursor cursor = context.getContentResolver().query(MessageService.MESSAGE_URI, null,
                "type=? AND type_id=?", new String[] {
				msgType + "", typeId + ""
                }, "_id desc");
		int messageId = 0;
		while (cursor.moveToNext()) {
			messageId = cursor.getInt(cursor.getColumnIndex("message_id"));
		}
		cursor.close();
		return messageId;
	}
}
