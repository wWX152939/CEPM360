package com.pm360.cepm360.app.module.announcement;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.Window;
import android.webkit.WebSettings.LayoutAlgorithm;
import android.webkit.WebView;
import android.widget.ImageView;
import android.widget.TextView;

import com.pm360.cepm360.R;
import com.pm360.cepm360.app.common.activity.DataManagerInterface;
import com.pm360.cepm360.common.GLOBAL;
import com.pm360.cepm360.common.ResultStatus;
import com.pm360.cepm360.entity.Announcement;
import com.pm360.cepm360.services.system.RemoteAnnouncementService;

import java.util.List;

@SuppressLint("SetJavaScriptEnabled")
public class AnnouncementShowActivity extends Activity {
	private com.pm360.cepm360.entity.Message mMessage = null;
	private WebView webView;
	private TextView titleTextView;
	private ImageView closeBtn;
	private Announcement mCurrentAnnouncement = new Announcement();

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
		requestWindowFeature(Window.FEATURE_NO_TITLE);  
		setContentView(R.layout.announcement_show);
		
		titleTextView = (TextView) findViewById(R.id.tv_title);
		closeBtn = (ImageView)findViewById(R.id.btn_close);
		closeBtn.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				finish();
			}
		});
		webView = (WebView) findViewById(R.id.webView);
		webView.getSettings().setJavaScriptEnabled(true);
		// 设置可以支持缩放
		webView.getSettings().setSupportZoom(true);
		// 设置出现缩放工具
		webView.getSettings().setBuiltInZoomControls(true);
		// 清除浏览器缓存
		webView.clearCache(true);
		webView.getSettings().setLayoutAlgorithm(LayoutAlgorithm.SINGLE_COLUMN);		
		msgHandlerProgress();
	}

	private void msgHandlerProgress() {
		Intent intent = getIntent();		
		String action = intent.getAction();
		if (action != null && action.equals(GLOBAL.MSG_ANNOUNCEMENT)) {
            Announcement bean = (Announcement) getIntent().getSerializableExtra("announcement");
            if (bean != null) {
                mCurrentAnnouncement = bean;
                titleTextView.setText(mCurrentAnnouncement.getTitle());
                webView.loadData(mCurrentAnnouncement.getContent(),
                        "text/html; charset=UTF-8", null);
            }
		    
			mMessage = (com.pm360.cepm360.entity.Message) intent.getSerializableExtra("message");
			if (mMessage != null) {
    			RemoteAnnouncementService.getInstance().viewAnnouncement(new DataManagerInterface() {
    				
    				@Override
    				public void getDataOnResult(ResultStatus status, List<?> list) {
    					// TODO Auto-generated method stub
    					if (list != null && !list.isEmpty()) {
    						mCurrentAnnouncement = (Announcement) list.get(0);
    						if (mCurrentAnnouncement != null) {
    							titleTextView.setText(mCurrentAnnouncement.getTitle());
    							webView.loadData(mCurrentAnnouncement.getContent(), "text/html; charset=UTF-8", null);
    						}
    					}
    				}
    			}, mMessage.getType_id());
			}
		}
	}
}
