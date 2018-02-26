package com.pm360.cepm360.app.utils;

import java.text.DecimalFormat;
import java.text.NumberFormat;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.Dialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ListAdapter;
import android.widget.ListView;
import android.widget.Toast;

import com.pm360.cepm360.R;
import com.pm360.cepm360.app.CepmApplication;
import com.pm360.cepm360.app.module.message.MessageService;

public class UtilTools {

	/**
	 * 
	 * @param context
	 * @param dp
	 *            直接填写数字
	 * @return
	 */
	public static int dp2pxW(Context context, float dp) {
		int width = ((CepmApplication) context.getApplicationContext())
				.getDeviceInfo().getWidth();
		int pxW = (int) (dp * 1.33125 * width / 1280);
		return pxW;
	}

	/**
	 * 
	 * @param context
	 * @param dp
	 *            直接填写数字
	 * @return
	 */
	public static int dp2pxH(Context context, float dp) {
		int height = ((CepmApplication) context.getApplicationContext())
				.getDeviceInfo().getHeight();
		int pxH = (int) (dp * 1.33125 * height / 736);
		return pxH;
	}

	public static int sp2px(Context context, float sp) {
		int width = ((CepmApplication) context.getApplicationContext())
				.getDeviceInfo().getWidth();
		int height = ((CepmApplication) context.getApplicationContext())
				.getDeviceInfo().getHeight();
		int px = (int) (sp * 1.33125
				* Math.sqrt(width * width + height * height) / 1476.5);
		return px;
	}

	public static void setListViewHeightBasedOnChildren(ListView listView) {
		ListAdapter listAdapter = listView.getAdapter();
		if (listAdapter == null) {
			return;
		}
		int totalHeight = 0;
		int count = listAdapter.getCount();
		for (int i = 0; i < count; i++) {
			View listItem = listAdapter.getView(i, null, listView);
			listItem.measure(0, 0);
			totalHeight += (listItem.getMeasuredHeight() + listView
					.getDividerHeight());
		}

		ViewGroup.LayoutParams params = listView.getLayoutParams();
		params.height = totalHeight;
		// listView.setLayoutParams(params);
	}

	public static String getPercent(double px, double ptotal) {
		String res = "";
		double fen = px / ptotal;
		DecimalFormat df1 = new DecimalFormat("##.00%");
		res = df1.format(fen);
		return res;
	}

	public static ProgressDialog showProgressDialog(Context contetxt,
			boolean indeterminate, boolean cancelable) {
		ProgressDialog dialog = new ProgressDialog(contetxt);
		dialog.setIndeterminate(indeterminate);
		dialog.setCancelable(cancelable);
		dialog.show();
		dialog.setContentView(R.layout.layout_progress);
		return dialog;
	}
	
	/**
     * 全局广播通知，首页更新数据
     * 
     * @param messageType
     */
    public static void sendBroadcast(Context context, int messageType) {
        Intent intent = new Intent(MessageService.ACTION_MESSAGE);
        intent.putExtra(MessageService.MESSAGE_FLAG, 1);            // 更新数据
        intent.putExtra(MessageService.MESSAGE_TYPE, messageType);  // 消息类型
        context.sendBroadcast(intent);
    }

	public static void showToast(Context context, String text) {
		Toast.makeText(context, text, Toast.LENGTH_SHORT).show();
	}

	public static void showToast(Context context, int textId) {
		Toast.makeText(context, textId, Toast.LENGTH_SHORT).show();
	}

	/**
	 * 
	 * @param coinSymbol
	 *            符号 如"¥"
	 * @param num
	 *            money
	 * @param len
	 *            小数点
	 * @return
	 */
	public static String formatMoney(String coinSymbol, double num, int len) {
		NumberFormat formater = null;
		if (len == 0) {
			formater = new DecimalFormat("###,###");
		} else {
			StringBuffer buff = new StringBuffer();
			buff.append("###,###.");
			for (int i = 0; i < len; i++) {
				buff.append("#");
			}
			formater = new DecimalFormat(buff.toString());
		}
		String result = formater.format(num);
		if (result.indexOf(".") == -1) {
			result = coinSymbol + result + ".00";
		} else if (result.indexOf(".") == result.length() - 2) {
			result = coinSymbol + result + "0";
		} else {
			result = coinSymbol + result;
		}
		return result;
	}

	public static double backFormatMoney(String coinSymbol, String numString) {
		if (numString.equals(""))
			return 0;
		String resString = numString.replaceAll(coinSymbol, "").replaceAll(",",
				"");
		return Double.parseDouble(resString);
	}

	public static String doubleToMoneyString(double value) {
		DecimalFormat format = new DecimalFormat("#.00");
		return format.format(value);
	}

	public interface DeleteConfirmInterface {
		public void deleteConfirmCallback();
	}

	public static void deleteConfirm(Activity activity,
			final DeleteConfirmInterface deleteConfirmInterface) {
		deleteConfirm(activity, deleteConfirmInterface, activity.getResources()
				.getString(R.string.confirm_delete), activity.getResources()
				.getString(R.string.remind));
	}

	public static void deleteConfirm(Activity activity,
			final DeleteConfirmInterface deleteConfirmInterface,
			String message, String title) {
		// 创建确认删除对话框，并显示
		new AlertDialog.Builder(activity)
				// 设置对话框主体内容
				.setMessage(message)
				// 设置对话框标题
				.setTitle(title)
				// 为对话框按钮注册监听
				.setPositiveButton(
						activity.getResources().getString(R.string.confirm),
						new DialogInterface.OnClickListener() {
							@Override
							public void onClick(DialogInterface dialog,
									int which) {
								// 首先隐藏对话框
								deleteConfirmInterface.deleteConfirmCallback();
								dialog.dismiss();
							}
						})
				.setNegativeButton(
						activity.getResources().getString(R.string.cancel),
						new DialogInterface.OnClickListener() {
							@Override
							public void onClick(DialogInterface dialog,
									int which) {
								dialog.dismiss();
							}
						}).show();
	}
	
	/**
     * 初始化选择附件对话框
     */
	public static Dialog initTwoPickDialog(Context context, List<OnClickListener> listenerList, List<String> textList) {
        Dialog pickDialog = new Dialog(context, R.style.MyDialogStyle);
        pickDialog.setContentView(R.layout.two_pick_dialog);
        
        Button firstButton = (Button) pickDialog.findViewById(R.id.first);
        Button secondButton = (Button) pickDialog.findViewById(R.id.second);        
        
        firstButton.setOnClickListener(listenerList.get(0));
        secondButton.setOnClickListener(listenerList.get(1));
        
        firstButton.setText(textList.get(0));
        secondButton.setText(textList.get(1));

        pickDialog.setCanceledOnTouchOutside(true);
        return pickDialog;
    }

	public static boolean isMobileNO(String mobiles) {

//		Pattern p = Pattern
//				.compile("^((13[0-9])|(15[^4,\\D])|(18[0,5-9]))\\d{8}$");
		Pattern p = Pattern
		.compile("^((13[0-9])|(15[0-9])|(18[0-9]))\\d{8}$");
		Matcher m = p.matcher(mobiles);
		return m.matches();

	}
	
	public static int calcStringByte(String str) {
	    int count = 0;
	    char[] chars = str.toCharArray(); 
	    for(int i = 0; i < chars.length; i++) {
			byte[] bytes = ("" + chars[i]).getBytes();
			if (bytes.length >= 2){
				count += 2;
			} else {
				count++;
			}
	    } 
	    return count; 
	}
	
	public static String getCurrentTime(String format) {
		SimpleDateFormat df = new SimpleDateFormat(format);//设置日期格式
		return df.format(new Date());
	}
	
	public static String getCurrentTime() {
		SimpleDateFormat df = new SimpleDateFormat("yyyy-MM-dd-HH-mm-ss");//设置日期格式
		return df.format(new Date());
	}
}
