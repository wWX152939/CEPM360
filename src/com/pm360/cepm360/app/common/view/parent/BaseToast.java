package com.pm360.cepm360.app.common.view.parent;

import android.content.Context;
import android.widget.Toast;

import com.pm360.cepm360.R;

public class BaseToast {
	
	public final static int NULL_MSG = -1;
	public final static int ALREADY_DOWNLOAD = 11110;
	public final static int NO_PROJECT_EXIST = 11111;
	public final static int NO_TASK_EXIST = 11112;
	public final static int NO_COMPANY_EXIST = 11113;
	public final static int NO_PERMISSION = 11114;
	public static void show(Context context, int state) {
		if (state == NULL_MSG)
			Toast.makeText(context,
				     R.string.pls_input_all_info, Toast.LENGTH_SHORT).show();
		else if (state == ALREADY_DOWNLOAD) {
			Toast.makeText(context,
				     R.string.document_downloaded, Toast.LENGTH_SHORT).show();
		} else if (state == NO_PROJECT_EXIST) {
			Toast.makeText(context,
				     R.string.pls_select_project, Toast.LENGTH_SHORT).show();
		} else if (state == NO_TASK_EXIST) {
			Toast.makeText(context,
					R.string.pls_select_task, Toast.LENGTH_SHORT).show();
		} else if (state == NO_COMPANY_EXIST) {
			Toast.makeText(context,
					R.string.pls_select_obs, Toast.LENGTH_SHORT).show();
		} else if (state == NO_PERMISSION) {
			Toast.makeText(context,
				     R.string.no_edit_permission, Toast.LENGTH_SHORT).show();
		}
	}
	
	public static void showDialog(Context context, String text) {
		Toast.makeText(context, text, Toast.LENGTH_SHORT).show();
	}

	public static void showDialog(Context context, int textId) {
		Toast.makeText(context, textId, Toast.LENGTH_SHORT).show();
	}
	
	public static void showView(Context context, String text) {
		Toast.makeText(context, text, Toast.LENGTH_SHORT).show();
	}

	public static void showView(Context context, int textId) {
		Toast.makeText(context, textId, Toast.LENGTH_SHORT).show();
	}
}

