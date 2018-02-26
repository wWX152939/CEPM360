package com.pm360.cepm360.app.common.view.parent.list;

import android.annotation.SuppressLint;
import android.app.AlertDialog;
import android.app.AlertDialog.Builder;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.DialogInterface.OnClickListener;
import android.os.Handler;
import android.os.Message;
import android.view.Gravity;
import android.view.View;
import android.widget.Toast;

import com.pm360.cepm360.R;
import com.pm360.cepm360.app.common.custinterface.OperationMode;

import java.io.Serializable;
import java.util.HashMap;
import java.util.Map;

/**
 * 
 * BaseListCommon 定义列表基本信息和操作
 * @author yuanlu
 *
 */
public abstract class BaseListCommon<T extends Serializable> {
	
	/** ----------------- 位置常量定义 ----------------- */
	public static final String SERIAL_NUMBER = "serial";
	
	public static final int BASE_POSITION = 0;
	public static final int SHOW_TOAST = BASE_POSITION;
	public static final int SHOW_PROGRESS_DIALOG = BASE_POSITION + 1;
	public static final int DISMISS_PROGRESS_DIALOG = BASE_POSITION + 2;
	
	public static final int DELAYTIME_FOR_SHOW = 500;
	
	
	/** ----------------- 上下文及视图定义 ----------------- */ 
	protected Context mContext;
	protected ProgressDialog mProgressDialog;	// 进度对话框
	
	// 当前操作模式
	protected OperationMode mCurrentMode;
	
	/** ----------------- 操作数据 --------------- */
	protected Class<T> mListItemClass;	// 保存类型对象
	protected T mCurrentItem;	// 当前数据
	
	/** ----------------- 标识定义 --------------- */
	// 数据是否加载成功
	public boolean mDataLoaded;
	
	// 权限管理者
	protected PermissionManager mPermissionManager;
	
	// 视图组合使用时要实现该接口
	protected LocationInterface mLocationImpl;
	
	/**
	 * 构造函数
	 * @param context
	 */
	public BaseListCommon(Context context) {
		mContext = context;
		mCurrentMode = OperationMode.NORMAL;
		mPermissionManager = new PermissionManager();
	}
	
	/**
	 * 获取当前列表项
	 * @return
	 */
	public T getCurrentItem() {
		return mCurrentItem;
	}
	
	/**
	 * 设置当前树节点
	 * @param t
	 */
	public void setCurrentItem(T t) {
		mCurrentItem = t;
	}
	
	/**
	 * 设置定位实现
	 * @param locationInterface
	 */
	public void setLocationInterface(LocationInterface locationInterface) {
		mLocationImpl = locationInterface;
	}
	
	/**
	 * 初始化上下文
	 */
	public void init() {
		
		// 当设置模式时即初始化一次权限
		mPermissionManager.setOperationMode(mCurrentMode);
		
		// 进度对话框初始化
		createProgressDialog();
	}
	
	/**
	 * 获取权限管理器
	 * @return
	 */
	public PermissionManager getPermissionManager() {
		return mPermissionManager;
	}
	
	/**
	 * 设置当前操作模式
	 * @param mode
	 */
	public void setOperationMode(OperationMode mode) {
		mCurrentMode = mode;
		mPermissionManager.setOperationMode(mCurrentMode);
	}
	
	/**
	 * 设置权限标示符和权限类型
	 * @param viewPermission
	 * @param editPermission
	 * @param type
	 */
	public void setPermission(String viewPermission, String editPermission, int type) {
		mPermissionManager.setPermission(viewPermission, editPermission, type);
	}
	
	/**
	 * 获取进度对话框
	 * @return
	 */
	public ProgressDialog getProgressDialog() {
		return mProgressDialog;
	}
	
	/** ----------------- 消息处理系统 ----------------- */ 
	/**
	 * 消息处理
	 */
	@SuppressLint("HandlerLeak") 
	private Handler mHandler = new Handler() {
		public void handleMessage(Message msg) {
			handleMsg(msg);
		}
	};
	
	/**
	 * 消息处理函数，必要时可重载该函数
	 * @param message
	 */
	protected void handleMsg(Message message) {
		switch (message.what) {
			case SHOW_TOAST:
				Toast toast = Toast.makeText( mContext, (CharSequence) message.obj,
            				Toast.LENGTH_SHORT);
				toast.setGravity(Gravity.CENTER, 0, 0);
				toast.show();
				break;
			case SHOW_PROGRESS_DIALOG:
				if (!mDataLoaded && !mProgressDialog.isShowing()) {
					mProgressDialog.show();
					mProgressDialog.setContentView(R.layout.layout_progress);
				}
				break;
			case DISMISS_PROGRESS_DIALOG:
				mDataLoaded = true;
				if (mProgressDialog.isShowing()) {
					mProgressDialog.dismiss();
					mDataLoaded = false;
				}
				break;
			default:
				break;
		}
	}
	
	/**
	 * 发送消息，本地处理
	 * @param what
	 * @param object
	 */
	public void sendMessage(int what, Object object) {
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
	 * 发送消息ID，本地处理
	 * @param what
	 */
	public void sendMessage(int what) {
		sendMessage(what, null);
	}
	
	/**
	 * 发送延时消息
	 * @param what
	 * @param delay
	 */
	public void sendEmptyMessageDelayed(int what, long delay) {
		mHandler.sendEmptyMessageDelayed(what, delay);
	}
	
	/**
	 * 发送延时消息
	 * @param what
	 */
	public void sendEmptyMessageDelayed(int what) {
		mHandler.sendEmptyMessageDelayed(what, DELAYTIME_FOR_SHOW);
	}
	
	/**
	 * 发送TOAST消息，本地处理
	 * @param what
	 */
	public void sendMessage(String msgString) {
		sendMessage(SHOW_TOAST, msgString);
	}
	
	
	/** ----------------- ID和NAME映射方法 ----------------- */ 
	/**
	 * 建立ID和Name映射，用户快速查询和显示映射。
	 * 注意：考虑到正常使用场景一般来说ID是唯一的，但
	 * 是NAME可能不唯一，因此可以保证ID到NAME的查询正
	 * 确，但反之不能保证正确性。
	 * @param sourceStrings
	 * @return
	 */
	public static Map<String, String> genIdNameMap(String[][] sourceStrings) {
		Map<String, String> map = new HashMap<String, String>();
		
		// 这里使用了双向映射
		for (String[] itemStrings : sourceStrings) {
			map.put(itemStrings[0], itemStrings[1]);
			map.put(itemStrings[1], itemStrings[0]);
		}
		
		return map;
	}
	
	/**
	 * 获取类型名字数组
	 * @param source
	 * @return
	 */
	public static String[] getGlobalNames(String[][] source) {
		String[][] typeArray = source;
		
		String[] typeStrings = new String[typeArray.length];
		for (int i = 0; i < typeArray.length; i++) {
			typeStrings[i] = typeArray[i][1];
		}
		
		return typeStrings;
	}
	
	/**
	 * 创建并显示警告对话框
	 * @param context
	 * @param message
	 * @param clickListener
	 */
	public static void showAlertDialog(Context context, 
						String message, OnClickListener clickListener) {
		
		// 创建确认对话框
		Builder dialogbBuilder = new AlertDialog.Builder(context);
		
		// 设置对话框主体内容
		dialogbBuilder.setMessage(message);
        
		// 设置对话框标题
		dialogbBuilder.setTitle(context.getResources().getString(R.string.remind));
        
		// 为对话框按钮注册监听
		dialogbBuilder.setPositiveButton(context.getResources()
        		.getString(R.string.confirm), clickListener);
        
		dialogbBuilder.setNegativeButton(context.getResources()
				.getString(R.string.cancel), new DialogInterface.OnClickListener() {
	            @Override
	            public void onClick(DialogInterface dialog, int which) {
	                dialog.dismiss();
	        }
        }).show();
	}
	
	/**
	 * 初始化进度对话框
	 */
	private void createProgressDialog() {
		mProgressDialog = new ProgressDialog(mContext);
		
        mProgressDialog.setIndeterminate(true);
        mProgressDialog.setCancelable(true);
	}
	
	/**
	 * 获取列表适配器
	 * @return
	 */
	abstract public Object getListAdapter();
	
	/**
	 * 当该列表实现类被用于和其他视图组合时使用，比如：
	 * 当与树视图实现类组合使用时，需要通过该接口提供
	 * 当前视图类的位置
	 * @author yuanlu
	 *
	 */
	public interface LocationInterface {
		
		/**
		 * 获取根布局视图
		 * @return
		 */
		public View getRootView();
		
		/**
		 * 获取本视图资源定位ID
		 * @return
		 */
		public int getLocationId();
	}
}
