package com.pm360.cepm360.app.module.common.plan;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.view.Gravity;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.WindowManager;
import android.webkit.JavascriptInterface;
import android.webkit.WebView;
import android.widget.Toast;

import com.pm360.cepm360.R;
import com.pm360.cepm360.app.cache.UserCache;
import com.pm360.cepm360.app.common.view.parent.BaseDialogStyle;
import com.pm360.cepm360.common.GLOBAL;
import com.pm360.cepm360.common.util.DateUtils;
import com.pm360.cepm360.entity.TaskCell;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.List;

public class GanttDialog<T extends TaskCell> extends BaseDialogStyle {
	private WebView mWebView;
	private View mParentView;
	private List<T> mTaskList;
	
	public GanttDialog(Activity activity) {
		super(activity);
		mParentView = init(R.layout.gantt_layout);
		setTitleName(activity.getString(R.string.gantt));
		initWebView();
		getButton(1).setVisibility(View.GONE);
		setButton(activity.getString(R.string.close), new OnClickListener() {

			@Override
			public void onClick(View v) {
				dismiss();
			}
			
		});
	}
	
	public GanttDialog(Activity activity, List<T> taskList) {
		super(activity);
		mParentView = init(R.layout.gantt_layout);
		setTitleName(activity.getString(R.string.gantt));
		initWebView();
		getButton(1).setVisibility(View.GONE);
		setButton(activity.getString(R.string.close), new OnClickListener() {

			@Override
			public void onClick(View v) {
				dismiss();
			}
			
		});
		mTaskList = taskList;
		try {

//			pID, pName,  pStart,      pEnd,        pColor,   pLink,          pMile, pRes,  pComp, pGroup, pParent, pOpen
			// 构造一个json对象
			mJsonArray = new JSONArray();
			for (T t : mTaskList) {
				JSONObject obj1 = new JSONObject();
				obj1.put("pID",	t.getId());
				obj1.put("pName", t.getName());
				obj1.put("pStart", DateUtils.dateToString("MM/dd/yyyy", t.getStart_time()));
				obj1.put("pEnd", DateUtils.dateToString("MM/dd/yyyy", t.getEnd_time()));
				obj1.put("pColor", "00ff00");
				obj1.put("pLink", "");
				obj1.put("pMile", t.getType().equals(GLOBAL.TASK_TYPE_MILE_KEY) ? 1 : 0);
				obj1.put("pRes", UserCache.getNameById(t.getOwner()));
				obj1.put("pComp", t.getProgress());
				obj1.put("pGroup", t.isHas_child() ? 1 : 0);
				obj1.put("pParent",	t.getParents_id());
				obj1.put("pOpen", 1);
				mJsonArray.put(obj1);
			}
			
		} catch (JSONException e) {
			e.printStackTrace();
		}
        loadGanttJs();
	}
	
	@SuppressLint("SetJavaScriptEnabled")
	private void initWebView() {
		mWebView = (WebView)mParentView.findViewById(R.id.webview);
        
		//重要：让 webview 支持 javascript  
		mWebView.getSettings().setJavaScriptEnabled(true);
		mWebView.getSettings().setSupportZoom(true); 
		mWebView.getSettings().setBuiltInZoomControls(true);
		mWebView.getSettings().setDisplayZoomControls(false);
		mWebView.setLayerType(View.LAYER_TYPE_SOFTWARE, null);
		mActivity.getWindow().setFlags(WindowManager.LayoutParams.FLAG_HARDWARE_ACCELERATED, WindowManager.LayoutParams.FLAG_HARDWARE_ACCELERATED);
	}
	
	private void loadGanttJs() {
    	GanttJavaScript ganttJavaScript = new GanttJavaScript();
    	mWebView.addJavascriptInterface(ganttJavaScript, "gtjavascript");
    	mWebView.loadUrl("file:///android_asset/gantt_chart.html");
    }
	
	private JSONArray mJsonArray;
	public void show(List<T> taskList) {
		super.show(1260, 760);
	}
	
	public void showGanttDialog() {
		super.show(1260, 760);
	}
	
	public class GanttJavaScript {
		
		/*
		 * 传入数据到js中
		 */
		@JavascriptInterface
		public String generateData() {
			return mJsonArray.toString();
		}
		
		@JavascriptInterface
		public void showToast(String str) {
			Toast toast = Toast.makeText(mActivity, str.replace("-#", "\n"), Toast.LENGTH_SHORT);
			toast.setGravity(Gravity.CENTER, 0, 0);
			toast.show();
		}

	}
	
}
