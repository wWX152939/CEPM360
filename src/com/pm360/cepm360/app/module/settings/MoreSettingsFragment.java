package com.pm360.cepm360.app.module.settings;

//import java.io.File;

import android.annotation.SuppressLint;
import android.app.Dialog;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.pm360.cepm360.R;
import com.pm360.cepm360.app.CepmApplication;
import com.pm360.cepm360.app.cache.UserCache;
import com.pm360.cepm360.app.common.activity.ApplicationUpdateActivity;
import com.pm360.cepm360.app.common.activity.DataManagerInterface;
import com.pm360.cepm360.app.common.activity.LoginActivity;
import com.pm360.cepm360.app.common.view.MoreSettingsItemView;
import com.pm360.cepm360.app.common.view.parent.BaseDialog;
import com.pm360.cepm360.app.module.message.MessageService;
import com.pm360.cepm360.common.GLOBAL;
import com.pm360.cepm360.common.ResultStatus;
import com.pm360.cepm360.common.json.AnalysisManager;
import com.pm360.cepm360.entity.User_comment;
import com.pm360.cepm360.services.system.RemoteUserCommentService;

import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

//import android.os.Environment;
//import android.widget.CompoundButton;
//import android.widget.ToggleButton;
//import com.pm360.cepm360.common.util.FileUtils;

public class MoreSettingsFragment extends Fragment {
	
	private static final int SHOW_TOAST = 0;
	
    private SettingsActivity mActivity;
    private MoreSettingsItemView mFeedbackLayout;
//    private MoreSettingsItemView mOnlyWifiLayout;
//    private MoreSettingsItemView mClearCacheLayout;
    private MoreSettingsItemView mVersionCheckLayout;
    private MoreSettingsItemView mAboutAppLayout;
    
    private BaseDialog mFeedDialog;
    private TextView mExitButton;
    private Dialog mAboutCEPM360Dialog;

    private SharedPreferences mSharedPreferences;
    
    private Map<String, String> mFeedbackTypeMap;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
            Bundle savedInstanceState) {

        View rootView = inflater.inflate(R.layout.more_settings_fragment,
                container, false);

        mActivity = (SettingsActivity) getActivity();
        mSharedPreferences = mActivity.getSharedPreferences(
                GLOBAL.SHARED_PREFERENCES_KEY, Context.MODE_PRIVATE);
        
//        mOnlyWifiLayout = (MoreSettingsItemView) rootView
//                .findViewById(R.id.more_item_only_wifi_connect);
//        mClearCacheLayout = (MoreSettingsItemView) rootView
//                .findViewById(R.id.more_item_clear_cache);
        mVersionCheckLayout = (MoreSettingsItemView) rootView
                .findViewById(R.id.more_item_version_check);
        mAboutAppLayout = (MoreSettingsItemView) rootView
                .findViewById(R.id.more_item_about_cepm360);
        mFeedbackLayout = (MoreSettingsItemView) rootView
                .findViewById(R.id.more_item_feedback_and_suggestion);
        mExitButton = (TextView) rootView.findViewById(R.id.exit_app);
        mExitButton.setOnClickListener(mListener);

        //initClearCacheLayout();
        initVersionCheckLayout();
        initAboutAppLayout();
        initFeedbackLayout();

//        mOnlyWifiLayout.init(R.drawable.ic_navigation_more_settings,
//                "仅Wi-Fi联网", "", true, false, true);

//        ToggleButton toggle = (ToggleButton) mOnlyWifiLayout
//                .findViewById(R.id.more_item_feature_switch);
//        toggle.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
//
//            @Override
//            public void onCheckedChanged(CompoundButton button,
//                    boolean isChecked) {
//                if (isChecked) {
//                }
//            }
//        });

        return rootView;
    }
    
    protected Map<String, String> globalIdNameMap(String[][] globalStrings) {
		Map<String, String> globalMap = new HashMap<String, String>();
		for (String[] itemStrings : globalStrings) {
			globalMap.put(itemStrings[0], itemStrings[1]);
			globalMap.put(itemStrings[1], itemStrings[0]);
		}
		return globalMap;
	}

    View.OnClickListener mListener = new View.OnClickListener() {

        @Override
        public void onClick(View view) {
            switch (view.getId()) {
            case R.id.more_item_about_cepm360:
                mAboutCEPM360Dialog.show();
                break;
            case R.id.exit_app:
                exitApplication();
                break;
            }
        }
    };

//    private void initClearCacheLayout() {
//        String cacheSize = getCacheSizeText();
//        mClearCacheLayout.init(R.drawable.ic_navigation_more_settings, "清空缓存",
//                cacheSize, false, true, false);
//        mClearCacheLayout.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View view) {
//                String cachePath = Environment.getExternalStorageDirectory()
//                        + "/CEPM360/";
//                FileUtils.deleteDirectory(cachePath);
//                mClearCacheLayout.setFeatureNote(getCacheSizeText());
//            }
//        });
//    }

    private void initVersionCheckLayout() {
        final int currentVersion = mSharedPreferences.getInt(
                "current_app_version", 0);
        final int newVersion = mSharedPreferences.getInt("new_app_version", 0);
        final String currentAppName = mSharedPreferences.getString(
                "current_app_name", "");
        final String newAppName = mSharedPreferences.getString("new_app_name",
                "");
        final String newAppDescription = mSharedPreferences.getString(
                "new_app_description", "");

        mVersionCheckLayout.init(R.drawable.ic_navigation_more_settings,
                getString(R.string.check_for_update), getString(R.string.current_version) + currentAppName, false, true, true);
        mVersionCheckLayout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (newVersion > currentVersion) {
                    Intent intent = new Intent();
                    intent.putExtra("name", newAppName);
                    intent.putExtra("description", newAppDescription);
                    intent.setClass(mActivity, ApplicationUpdateActivity.class);
                    startActivity(intent);
                } else {
                    showToast(getString(R.string.latest_version));
                }
            }
        });
    }

    private void initAboutAppLayout() {
        mAboutAppLayout.init(R.drawable.ic_navigation_more_settings,
                getString(R.string.about_cepm360), "", false, true, true);
        mAboutAppLayout.setOnClickListener(mListener);

        View dialogLayout = LayoutInflater.from(mActivity).inflate(
                R.layout.about_cepm360_layout, null);
        mAboutCEPM360Dialog = new Dialog(mActivity, R.style.MyDialogStyle);
        mAboutCEPM360Dialog.setContentView(dialogLayout);
        mAboutCEPM360Dialog.setCanceledOnTouchOutside(true);
        mAboutCEPM360Dialog.setCancelable(true);
    }
    
    /**
	 * 消息处理
	 */
	@SuppressLint("HandlerLeak") 
	private Handler mHandler = new Handler() {
		public void handleMessage(Message msg) {
			switch (msg.what) {
				case SHOW_TOAST:
	                Toast.makeText( getActivity(), 
	                				(CharSequence) msg.obj,
	                				Toast.LENGTH_SHORT).show();
					break;
			}
		}
	};
	
	/**
	 * 发送消息，本地处理
	 * @param what
	 * @param object
	 */
	protected void sendMessage(int what, Object object) {
		if (object != null) {
			// 创建初始化Message
			Message msg = Message.obtain();
			msg.what = what;
			msg.obj = object;
			
			// 发送消息到mHandler
			mHandler.sendMessage(msg);
		} else {
			mHandler.sendEmptyMessage(what);
		}
	}
	
	/**
	 * 发送消息，本地处理
	 * @param what
	 */
	protected void sendMessage(int what) {
		sendMessage(what, null);
	}
    
    private void initFeedbackLayout() {
    	
    	// 初始化反馈类型映射表
    	mFeedbackTypeMap = globalIdNameMap(GLOBAL.COMMENT_TYPE);
    	
    	mFeedbackLayout.init(R.drawable.ic_navigation_more_settings, 
    			getResources().getString(R.string.setting_feedback_and_suggestion),
                "", false, true, false);
    	
    	initFeedBackDialog();
    	
    	mFeedbackLayout.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View v) {
				mFeedDialog.SetDefaultValue(null);
				mFeedDialog.show();
			}
		});
    }
    
    @SuppressLint("UseSparseArrays") 
    private void initFeedBackDialog() {
    	mFeedDialog = new BaseDialog(getActivity(), 
    			getResources().getString(R.string.setting_feedback_and_suggestion));
    	
    	final String[] lableNames = getResources()
    			.getStringArray(R.array.feedback_dialog_lable_names);
    	
    	Map<Integer, Integer> styleMap = new HashMap<Integer, Integer>();
		styleMap.put(0, BaseDialog.spinnerLineStyle);
		styleMap.put(1, BaseDialog.remarkEditTextLineStyle);
    	
		Map<Integer, String[]> dataMap = new HashMap<Integer, String[]>();
		String[] feedbackType = new String[]{
				GLOBAL.COMMENT_TYPE[0][1],
				GLOBAL.COMMENT_TYPE[1][1],
				GLOBAL.COMMENT_TYPE[2][1]
		};
		dataMap.put(0, feedbackType);
    	
    	mFeedDialog.init(R.array.feedback_dialog_lable_names, styleMap, dataMap);
    	
    	Button saveImageView = (Button) mFeedDialog.getPopupView()
				.findViewById(R.id.save_Button);
    	saveImageView.setText(getResources().getString(R.string.button_feedback));
    	saveImageView.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				User_comment comment = new User_comment();
				
				// 保存界面输入的数据
				Map<String, String> saveData = mFeedDialog.SaveData();
				comment.setFeedback_time(new Date());
				comment.setUser_id(UserCache.getCurrentUser().getUser_id());
				comment.setTenant_id(UserCache.getCurrentUser().getTenant_id());
				comment.setType(Integer.parseInt(mFeedbackTypeMap.get(saveData.get(lableNames[0]))));
				comment.setComments(saveData.get(lableNames[1]));
				
				RemoteUserCommentService.getInstance().addUserComments(new DataManagerInterface() {
					
					@SuppressLint("ShowToast") 
					@Override
					public void getDataOnResult(ResultStatus status, List<?> list) {
						if (status.getCode() == AnalysisManager.SUCCESS_DB_ADD) {
							mFeedDialog.dismiss();
							sendMessage(SHOW_TOAST, status.getMessage());
						}
					}
				}, comment);
			}
    	});
    }

    private void exitApplication() {
        Intent msg_intent = new Intent(mActivity, MessageService.class);
        mActivity.stopService(msg_intent);
        ((CepmApplication) mActivity.getApplicationContext()).clear();
        Intent intent = new Intent(Intent.ACTION_MAIN);
        intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP
                | Intent.FLAG_ACTIVITY_CLEAR_TASK
                | Intent.FLAG_ACTIVITY_NEW_TASK);
        intent.setClass(mActivity, LoginActivity.class);
        startActivity(intent);
    }

    private void showToast(String msg) {
        Toast.makeText(mActivity, msg, Toast.LENGTH_SHORT).show();
    }

//    private String getCacheSizeText() {
//        final File cachePath = new File(
//                Environment.getExternalStorageDirectory() + "/CEPM360/");
//        if (!cachePath.exists()) {
//            return "0 B";
//        }
//
//        long size = FileUtils.sizeOfDirectory(cachePath);
//        String cacheSize = size + " B";
//        if (size >= 1024) {
//            size /= 1024;
//            cacheSize = size + " KB";
//        }
//        if (size >= 1024) {
//            size /= 1024;
//            cacheSize = size + " MB";
//        }
//        if (size >= 1024) {
//            size /= 1024;
//            cacheSize = size + " GB";
//        }
//        return cacheSize;
//    }
}
