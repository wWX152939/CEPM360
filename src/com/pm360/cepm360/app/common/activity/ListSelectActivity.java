package com.pm360.cepm360.app.common.activity;

import android.app.Activity;
import android.app.Service;
import android.content.ComponentName;
import android.content.Intent;
import android.content.ServiceConnection;
import android.os.Bundle;
import android.os.IBinder;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentActivity;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;

import com.pm360.cepm360.R;
import com.pm360.cepm360.app.common.custinterface.CallBack;
import com.pm360.cepm360.app.module.message.MessageService;
import com.pm360.cepm360.app.module.message.MessageService.LocalBinder;
import com.pm360.cepm360.entity.Message;
import com.pm360.cepm360.entity.Project;

import java.io.Serializable;
import java.util.ArrayList;

public class ListSelectActivity<T extends Serializable> extends FragmentActivity {
	
	// 启动一个fragment
	public static final String FRAGMENT_KEY = "fragment_class";
	public static final String SHOW_DATA = "showData";
	public static final String IS_BLACK_BACKGROUD_COLOR = "background_color";
	
	// 模式选择Key
	public static final String SELECT_MODE_KEY = "select_mode_key";
	public static final String PARENT_BEAN_KEY = "parent_bean_key";
	public static final boolean MULTI_SELECT = true;
	public static final boolean SINGLE_SELECT = false;
	
	// 数据过滤Key
	public static final String FILTER_DATA_KEY = "filter_data_key";
	// 数据默认显示不能点击Key
	public static final String DEFAULT_DATA_KEY = "default_data_key";
	// 传入选择的project
	public static final String PROJECT_KEY = "select_project";
	// 返回数据Key
	public static final String RESULT_KEY = "result_key";
	
	// Message服务
	private MessageService mService;
	private boolean mBound = false;
	private Intent mMsgIntent;
	
	private FragmentManager mFragmentManager;
	private Fragment mShowFragment;
	
	private Class<?> mTargetFragmentClass;
	
	private T mParentBean;
	
	private Project mProject;
	
	private boolean mIsMultiSelectMode;
	
	private ArrayList<? extends Serializable> mFilterList;
	private ArrayList<? extends Serializable> mDefaultList;
	private ArrayList<? extends Serializable> mShowDataList;
	
	private int type;
	private int typeId;
	private CallBack<Void, Message> callBack;
	
	@SuppressWarnings("unchecked")
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		mMsgIntent = new Intent(this, MessageService.class);
		
		// 获取fragment索引
		Intent intent = getIntent();
		mTargetFragmentClass = (Class<?>) 
				intent.getSerializableExtra(FRAGMENT_KEY);
		mIsMultiSelectMode = intent
				.getBooleanExtra(SELECT_MODE_KEY, false);
		
		mParentBean = (T) intent
				.getSerializableExtra(PARENT_BEAN_KEY);
		
		mProject = (Project) intent
				.getSerializableExtra(PROJECT_KEY);
		
		if (mIsMultiSelectMode) {
			mFilterList = (ArrayList<? extends Serializable>) 
							intent.getSerializableExtra(FILTER_DATA_KEY);
			mDefaultList = (ArrayList<? extends Serializable>) 
							intent.getSerializableExtra(DEFAULT_DATA_KEY);
		}
		
		mShowDataList = (ArrayList<? extends Serializable>) 
							intent.getSerializableExtra(SHOW_DATA);
		
		if (mTargetFragmentClass != null) {
			dialogActivityInit();
		}
	}
	
	/**
	 * 初始化一个对话框风格的Activity
	 */
	private void dialogActivityInit() {
		
		setContentView(R.layout.framelayout_layout);
		
		mFragmentManager = getSupportFragmentManager();
        
        selectContent(mTargetFragmentClass);
	}
	
	/**
	 * 切换fragment
	 * @param targetFragmentClass
	 */
	public void selectContent(Class<?> targetFragmentClass) {
		
		FragmentTransaction fragmentTransaction 
					= mFragmentManager.beginTransaction();
		
		// 材料和设备为同一个fragment，为了区分
		Bundle args = new Bundle();
		args.putBoolean(SELECT_MODE_KEY, mIsMultiSelectMode);
		args.putBoolean(IS_BLACK_BACKGROUD_COLOR, false);

		if (mParentBean != null) {
			args.putSerializable(PARENT_BEAN_KEY, mParentBean);
		}
		
		if (mProject != null) {
			args.putSerializable(PROJECT_KEY, mProject);
		}
		
		// 多选模式下的过滤
		if (mFilterList != null
				&& mIsMultiSelectMode) {
			args.putSerializable(FILTER_DATA_KEY, mFilterList);
		}
		
		// 查看模式下提供的数据
		if (mShowDataList != null) {
			args.putSerializable(SHOW_DATA, mShowDataList);
		}
		
		if (mDefaultList != null
				&& mIsMultiSelectMode) {
			args.putSerializable(DEFAULT_DATA_KEY, mDefaultList);
		}
		
		mShowFragment = Fragment.instantiate(this, 
							targetFragmentClass.getCanonicalName(), args);

		if (mShowFragment.isAdded()) {
			fragmentTransaction.show(mShowFragment);
		} else {
			fragmentTransaction.add(R.id.content_fragment, mShowFragment);
		}
		
		fragmentTransaction.commitAllowingStateLoss();
	}
	
	/**
	 * 返回选择的列表项，也可能是列表
	 * @param data 应该是实现了Serializable
	 */
	@SuppressWarnings("hiding")
	public <T extends Serializable> void setSeletctedData(T data) {
		Intent intent = new Intent();
		
		if (data != null) {
			// 传回选中的列表数据
			Bundle resultBundle = new Bundle();
			resultBundle.putSerializable(RESULT_KEY, data);
			intent.putExtras(resultBundle);
			setResult(Activity.RESULT_OK, intent);
		} else {
			setResult(Activity.RESULT_CANCELED, intent);
		}
		
		// 结束当前Activity
		finish();
	}
	
	@Override
	protected void onStart() {
		super.onStart();
		bindService(mMsgIntent, mConnection, Service.BIND_AUTO_CREATE);
	}
	
	@Override
	protected void onStop() {
		super.onStop();
		
		if (mBound) {
			unbindService(mConnection);
			mBound = false;
		}
	}
	
	/**
	 * 服务连接器
	 */
	private ServiceConnection mConnection = new ServiceConnection() {

		@Override
		public void onServiceConnected(ComponentName name, IBinder service) {
			LocalBinder binder = (LocalBinder) service;
			mService = binder.getService();
			mBound = true;
			
			if (callBack != null) {
				getMessage(typeId, type, callBack);
			}
		}

		@Override
		public void onServiceDisconnected(ComponentName name) {
			mBound = false;
		}
	};
	
	/**
     * 设置消息为已读
     * 
     * @param type_id 邮件id
     * @param type 邮件类型
     */
    public void readMessage(int type_id, int type) {
    	if (mService != null) {
			mService.readMessage(type_id, type);
		}
    }
    
    /**
     * 获取消息
     * @param type_id
     * @param type
     * @param callBack
     */
	public void getMessage(int type_id, int type,
			CallBack<Void, Message> callBack) {
		if (mService != null) {
			mService.getMessageByType(type_id, type, callBack);
		} else {
			this.typeId = type_id;
			this.type = type;
			this.callBack = callBack;
		}
	}
    
    /**
     * 读本地消息
     * @param messageId
     */
    public void readLocalMessage(int messageId) {
		mService.readLocalMessage(messageId);
    }
}
