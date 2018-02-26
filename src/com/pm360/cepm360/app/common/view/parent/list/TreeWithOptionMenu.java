package com.pm360.cepm360.app.common.view.parent.list;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.Button;

import com.pm360.cepm360.R;
import com.pm360.cepm360.app.common.custinterface.CommonListInterface;
import com.pm360.cepm360.app.common.custinterface.DialogAdapterInterface;
import com.pm360.cepm360.app.common.custinterface.FloatingMenuInterface;
import com.pm360.cepm360.app.common.custinterface.OperationMode;
import com.pm360.cepm360.app.common.custinterface.OptionMenuInterface;
import com.pm360.cepm360.app.common.custinterface.ServiceInterface;
import com.pm360.cepm360.app.common.custinterface.SimpleDialogInterface;
import com.pm360.cepm360.app.common.custinterface.SimpleServiceInterface;
import com.pm360.cepm360.app.common.view.FloatingMenuView;
import com.pm360.cepm360.app.common.view.OptionsMenuView;
import com.pm360.cepm360.app.common.view.OptionsMenuView.SubMenuListener;
import com.pm360.cepm360.app.common.view.parent.BaseDialog;
import com.pm360.cepm360.common.ResultStatus;
import com.pm360.cepm360.common.json.AnalysisManager;
import com.pm360.cepm360.common.util.DateUtils;
import com.pm360.cepm360.common.util.MiscUtils;
import com.pm360.cepm360.entity.Expandable;

import java.io.Serializable;
import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class TreeWithOptionMenu<T extends Expandable & Serializable> 
								extends SimpleTree<T> {

	/**------------------ 视图控件 -----------------*/
	protected OptionsMenuView mOptionsMenu;		// 选项文菜单
	protected FloatingMenuView mFloatingMenu;	// 浮动菜单
	protected BaseDialog mDialog;	// 弹出对话框
	
	/**------------------ 保存资源数据 -------------*/
	// 保存对话框标签名数组
	protected String[] mDialogLableNames;
	
	/**------------------ 暂存数据 -----------------*/
	protected T mCurrentUpdateItem;		// 当前更新列表项
	private T mSpecifiedItem;	// 指定列表项
	private Map<String, String> mSaveData;	// 界面数据保存
	
	/**------------------ 继承者提供数据 ----------*/
	protected String[] mUpdateFields;	// 更新域
	
	private int[] mMenuImages;	// 浮动菜单图标
	private String[] mMenuTips;	// 浮动菜单名称
	private String[] mSubMenuNames;	// 选项菜单名称
	
	/**------------------ 删除操作数据 ------------*/
	private List<T> mRemoveList = new ArrayList<T>();
	private int mRemoveCount;
	private int mRemoveFailedCount;
	
	/** ----------------- 映射表定义 --------------*/
	private Map<String, Map<String, String>> mUpdateFieldSwitchMap;
	private Map<String, String> mFieldLableMap;	// 更新字段映射表
	
	/** ------------------- 标识 -------------------- */
	// 数据是否加载成功
	protected boolean mDataLoaded;
	protected boolean mIsBlackBackGroud;
	private boolean mIsDeleteParentNode;
	
	// 增加修改操作，是否是增加操作
	protected boolean mIsAddOperation;
	protected boolean mIsFloatMenuAdd;
	
	private boolean mEnableFloatingMenu;	// 是否启用浮动菜单
	private boolean mEnableOptionMenu;	// 使能选择菜单功能
	
	/** ------------------ 接口 ----------------- */
	private SimpleDialogInterface mDialogImplement;
	private OptionMenuInterface mOptionMenuImplement;
	private FloatingMenuInterface mFloatingMenuImplement;
	
	
	/** --------------------------------- 方法定义 ------------------------ */
	
	/**
	 * 构造函数
	 * @param context
	 */
	public TreeWithOptionMenu(Context context) {
		super(context);
	}
	
	/**
	 * 带选项菜单
	 * @param listItemClass
	 * @param listInterface
	 * @param serviceInterface
	 * @param dialogInterface
	 * @param optionMenuInterface
	 */
	protected void init(Class<T> listItemClass,
					CommonListInterface<T> listInterface,
					SimpleServiceInterface<T> serviceInterface,
					SimpleDialogInterface dialogInterface,
					OptionMenuInterface optionMenuInterface) {
		super.init(listItemClass, listInterface, serviceInterface);
		mDialogImplement = dialogInterface;
		mOptionMenuImplement = optionMenuInterface;
		mEnableOptionMenu = true;
	}
	
	/**
	 * 带浮动菜单
	 * @param context
	 * @param listItemClass
	 * @param listInterface
	 * @param serviceInterface
	 * @param dialogInterface
	 * @param floatingMenuInterface
	 */
	protected void init(Class<T> listItemClass,
			CommonListInterface<T> listInterface,
			SimpleServiceInterface<T> serviceInterface,
			SimpleDialogInterface dialogInterface,
			FloatingMenuInterface floatingMenuInterface) {
		super.init(listItemClass, listInterface, serviceInterface);
		mDialogImplement = dialogInterface;
		mFloatingMenuImplement = floatingMenuInterface;
		mEnableFloatingMenu = true;
	}
	
	/**
	 * 带选项菜单和浮动菜单
	 * @param listItemClass
	 * @param listInterface
	 * @param serviceInterface
	 * @param dialogInterface
	 * @param optionMenuInterface
	 * @param floatingMenuInterface
	 */
	protected void init(Class<T> listItemClass,
			CommonListInterface<T> listInterface,
			SimpleServiceInterface<T> serviceInterface,
			SimpleDialogInterface dialogInterface,
			OptionMenuInterface optionMenuInterface,
			FloatingMenuInterface floatingMenuInterface) {
		super.init(listItemClass, listInterface, serviceInterface);
		mDialogImplement = dialogInterface;
		mOptionMenuImplement = optionMenuInterface;
		mFloatingMenuImplement = floatingMenuInterface;
		mEnableOptionMenu = true;
		mEnableFloatingMenu = true;
	}

	/**
	 * 数据加载成功处理函数
	 */
	@SuppressWarnings("unchecked")
	@Override
	protected void handleDataOnResult(ResultStatus status, List<?> list) {
		switch (status.getCode()) {
			case AnalysisManager.SUCCESS_DB_QUERY:
				mListAdapter.setDataList((List<T>) list);
				
				// 结束加载进度对话框
				mDataLoaded = true;
				sendMessage(DISMISS_PROGRESS_DIALOG);
				
				if (mSpecifiedItem != null) {
					locationSpecifidItem(mSpecifiedItem);
				}
				break;
				
			case AnalysisManager.SUCCESS_DB_ADD:
				mListAdapter.addTreeNode((T) list.get(0));
				break;
	
			case AnalysisManager.SUCCESS_DB_DEL:
				mRemoveCount++;
				if (mRemoveCount == mRemoveList.size()) {
					if (mRemoveFailedCount == 0) {	// 删除都成功
						allDeleteSuccessful();
					} else {	// 删除部分失败
						partDeleteSucessful(status);
					}
					deleteDataClear();
				}
				break;
				
			case AnalysisManager.EXCEPTION_DB_DELETE:
				mRemoveCount++;
				mRemoveFailedCount++;
				if (mRemoveCount == mRemoveList.size()) {
					if (mRemoveCount == mRemoveFailedCount) {	// 全部失败
						status.setMessage(mContext.getResources()
									.getString(R.string.delete_all_failed));
					} else {	// 部分失败
						partDeleteSucessful(status);
					}
					deleteDataClear();
				}
				break;
				
			case AnalysisManager.SUCCESS_DB_UPDATE:
				MiscUtils.clone(mCurrentItem, mCurrentUpdateItem);
				mListAdapter.notifyDataSetChanged();
				break;
			default:
					break;
		}
		
		if (status.getCode() != AnalysisManager.SUCCESS_DB_QUERY
				&& mRemoveCount == 0) {
			sendMessage(SHOW_TOAST, status.getMessage());
		}
	}
	
	/**
	 * 全部删除成功
	 */
	private void allDeleteSuccessful() {
		mListAdapter.getSelected().remove(BASE_POSITION);
		// 将当前项置为空
		mCurrentItem = null;
	}
	
	/**
	 * 部分删除成功
	 * @param status
	 */
	private void partDeleteSucessful(ResultStatus status) {
		status.setMessage(mContext.getResources()
				.getString(R.string.delete_part_failed));
		mListAdapter.clearAll();
		mServiceImplement.getListData();
	}
	
	/**
	 * 清理删除操作使用的数据
	 */
	private void deleteDataClear() {
		mRemoveList.clear();
		mRemoveCount = 0;
		mRemoveFailedCount = 0;
	}
	
	/**
	 * 展开到指定列表项
	 * @param t
	 */
	private void locationSpecifidItem(T t) {
		// 获取显示列表
		List<List<T>> levelList = mListAdapter.getLevelList();
		List<T> lineList = new ArrayList<T>();
		
		T temp = t;
		// 在lineList中逆序保存
		lineList.add(temp);
		for (int i = t.getLevel() - 1; i >= 0; i--) {
			List<T> list = levelList.get(i);
			for (int j = 0; j < list.size(); j++) {
				if (list.get(j).getId() == temp.getParents_id()) {
					temp = list.get(j);
					lineList.add(temp);
					break;
				}
			}
		}
		
		// 递归展开第n项
		int position = -1;
		for (int i = 0; i <= t.getLevel(); i++) {
			List<T> showList = mListAdapter.getShowList();
			T guard = lineList.get(lineList.size()-i-1);
			for (int j = position + 1; j < showList.size(); j++) {
				position++;
				if (guard.getId() == showList.get(j).getId()) {
					break;
				}
			}
			
			// 最后一个不展开
			if (i != t.getLevel()) {
				mListAdapter.updateListView(position);
			}
		}
		
		handleClick(position);
	}
	
	/**
	 * 初始化域并建立域映射表
	 */
	@Override
	protected void initFeildsAndMap() {
		super.initFeildsAndMap();
		
		// 初始化更新域
		mUpdateFields = mDialogImplement.getUpdateFeilds();
		
		// 初始化域切换映射
		if (DialogAdapterInterface.class.isInstance(mDialogImplement)) {
			mUpdateFieldSwitchMap = ((DialogAdapterInterface) 
							mDialogImplement).getUpdateFieldsSwitchMap();
		}
		
		// 设置域名和标签映射，用于反映射提取保存的界面值
		if (OperationMode.NORMAL == mCurrentMode) {
			if (mDialogImplement.getDialogLableNames() != 0) {
				mDialogLableNames = mContext.getResources()
							.getStringArray(mDialogImplement.getDialogLableNames());
				
				// 初始化更新字段映射表
				if (mUpdateFields != null) {
					mFieldLableMap = new HashMap<String, String>();
					for (int i = 0; i < mDialogLableNames.length; i++) {
						mFieldLableMap.put(mUpdateFields[i], mDialogLableNames[i]);
					}
				}
			}
		}
	}

	/**
	 * 初始化舒适图
	 */
	@Override
	protected void initTreeView() {
		super.initTreeView();
		
		if (OperationMode.NORMAL == mCurrentMode) {
			if (mEnableFloatingMenu) {
				initFloatingMenu();
			}
			
			if (mEnableOptionMenu) {
				initOptionsMenu();
			}
			
			if (mEnableFloatingMenu || mEnableOptionMenu) {
				initDialog();
			}
		}
	}

	/**
	 * 初始化浮动菜单，可定制重写该方法
	 */
	protected void initFloatingMenu() {
		mMenuImages = new int[] { R.drawable.icn_add };
		mMenuTips = new String[] { 
	    		mContext.getResources().getString(R.string.add) 
	    };
		// 提取资源信息
		if (mFloatingMenuImplement != null) {
			setFloatingMenuImages(mFloatingMenuImplement.getFloatingMenuImages());
			setFloatingMenuTips(mFloatingMenuImplement.getFloatingMenuTips());
		}
        mFloatingMenu = (FloatingMenuView) 
        		mRootLayout.findViewById(R.id.floating_menu);
        
        // 初始化浮动菜单项
        for (int i = 0; i < mMenuTips.length; i++) {
            mFloatingMenu.addPopItem(mMenuTips[i], mMenuImages[i]);
        }
        
        // 为菜单项添加监听
        if (mFloatingMenuImplement != null) {
        	setItemClickListener(mFloatingMenuImplement.getFloatingMenuListener());
        } else {
        	setItemClickListener(mItemClickListener);
        }
	}
	
	/**
	 * 设置浮动菜单图片
	 * @param menuImages
	 */
	private void setFloatingMenuImages(int[] menuImages) {
		if (menuImages != null) {
			mMenuImages = menuImages;
		}
	}
	
	/**
	 * 设置浮动菜单提示
	 * @param menuTips
	 */
	private void setFloatingMenuTips(String[] menuTips) {
		if (menuTips != null) {
			mMenuTips = menuTips;
		}
	}
	
	/**
	 * 浮动菜单监听器
	 */
	private OnItemClickListener mItemClickListener = new OnItemClickListener() {
		
		 @Override
         public void onItemClick(AdapterView<?> parent,
         						View view, int position, long id) {
			 floatingMenuItemClick(parent, view, position, id);
         }
	};
	
	/**
	 * 附带菜单项单击监听处理函数
	 * @param parent
	 * @param view
	 * @param position
	 * @param id
	 */
	protected void floatingMenuItemClick(AdapterView<?> parent,
         						View view, int position, long id) {
		switch (position) {
	        case 0:
	        	 mIsAddOperation = mIsFloatMenuAdd = true;
	            mDialog.show(null);
	            mFloatingMenu.dismiss();
	            break;
	        default:
	            break;
		}
	}
	
	/**
	 * 设置浮动菜单的监听器
	 * @param listener
	 */
	private void setItemClickListener(OnItemClickListener listener) {
		if (listener != null) {
			mItemClickListener = listener;
		}
		
		mFloatingMenu.setPopOnItemClickListener(mItemClickListener);
	}
	
	/**
	 * 初始化内容弹出菜单，可定制重写该方法
	 */
	protected void initOptionsMenu() {
		mSubMenuNames = mContext.getResources()
				.getStringArray(R.array.directory_list_options_menu);
		
		// 查看，修改
		if (mOptionMenuImplement != null
				&& mOptionMenuImplement.getOptionMenuNames() != 0) {
			mSubMenuNames = mContext.getResources()
					.getStringArray(mOptionMenuImplement.getOptionMenuNames());
		}
        mOptionsMenu = new OptionsMenuView(mContext, mSubMenuNames);
        
        if (mOptionMenuImplement != null) {
        	setOptionSubMenutListener(mOptionMenuImplement.getOptionMenuClickListener());
        } else {
        	setOptionSubMenutListener(mSubMenuListener);
        }
	}
	
	protected void optionMenuAddFunction() {
		mDialog.show(null);
	}
	
	/**
	 * 选项菜单监听器
	 */
	private SubMenuListener mSubMenuListener = new SubMenuListener() {
		
		@Override
		public void onSubMenuClick(View view) {
			mOptionsMenu.dismiss();
			switch ((Integer) view.getTag()) {
				case 0:		// 添加
					mIsAddOperation = true;
					mIsFloatMenuAdd = false;
					optionMenuAddFunction();
					break;
				case 1:		// 修改
					mIsAddOperation = false;
					mDialog.show(updateDialog(true));
					break;
				case 2:
					if (!mIsDeleteParentNode && mCurrentItem.isHas_child()) {
						sendMessage(SHOW_TOAST, mContext.getResources()
										.getString(R.string.EPSMaintain_no_delete));
					} else {
						commonConfirmDelete();
					}
					break;
			}
		}
	};
	
	/**
	 * 设置选项菜单监听器
	 * @param listener
	 */
	protected void setOptionSubMenutListener(SubMenuListener listener) {
		if (listener != null) {
			mSubMenuListener = listener;
		}
		
		mOptionsMenu.setSubMenuListener(mSubMenuListener);
	}
	
	/**
	 * 删除确认提示
	 */
	protected void commonConfirmDelete() {
		// 创建确认删除对话框，并显示
		new AlertDialog.Builder(mContext)
			// 设置对话框主体内容
	        .setMessage(mContext.getResources().getString(R.string.confirm_delete))
	        // 设置对话框标题
	        .setTitle(mContext.getResources().getString(R.string.remind))
	        // 为对话框按钮注册监听
	        .setPositiveButton(mContext.getResources().getString(R.string.confirm),
	        	new DialogInterface.OnClickListener() {
		            @Override
		            public void onClick(DialogInterface dialog, int which) {
		            	
		            	// 首先隐藏对话框
		                dialog.dismiss();
		                
		                delete();
		            }
	        })
	        .setNegativeButton(mContext.getResources().getString(R.string.cancel),
                new DialogInterface.OnClickListener() {
		            @Override
		            public void onClick(DialogInterface dialog, int which) {
		                dialog.dismiss();
		        }
	        }).show();
	}
	
	/**
	 * 递归删除树节点
	 */
	protected void delete() {
		mRemoveList.addAll(mListAdapter.deleteTreeNode());
        
        // 开始循环删除，普通模式和批量删除一样处理
        for (int i = 0; i < mRemoveList.size(); i++) {
        	T listItem = mRemoveList.get(i);
        	((ServiceInterface<T>) mServiceImplement).deleteItem(listItem);
        }
	}
	
	/**
	 * 修改一条
	 */
	protected String[] updateDialog(boolean isEdit) {
		
		// 设置是否可以修改
		mDialog.switchModifyDialog(isEdit);
		
		Map<String, String> listItemMap = MiscUtils.beanToMap(mCurrentItem);
		
		// 构建默认值数组
		String[] defaultValues = new String[mUpdateFields.length];
		for (int i = 0; i < mUpdateFields.length; i++) {
			if (mUpdateFieldSwitchMap != null &&
					mUpdateFieldSwitchMap.containsKey(mUpdateFields[i])) {
				defaultValues[i] = mUpdateFieldSwitchMap.get(mUpdateFields[i])
										.get(listItemMap.get(mUpdateFields[i]));
			} else {
				defaultValues[i] = listItemMap.get(mUpdateFields[i]);
			}


			if (("0").equals(defaultValues[i])) {
				defaultValues[i] = "";
			}
		}
		
		return defaultValues;
	}
	
	/** 
	 * 初始化内容弹出对话框
	 */
	private void initDialog() {
		// 对话框标题
		String title = mContext.getResources().getString(mDialogImplement.getDialogTitleId());
		mDialog = new BaseDialog((Activity) mContext, title);

		// 对话框显示内容初始化
		if (DialogAdapterInterface.class.isInstance(mDialogImplement)) {
			mDialog.init(mDialogImplement.getDialogLableNames(), 
									((DialogAdapterInterface) mDialogImplement).getDialogStyles(), 
									((DialogAdapterInterface) mDialogImplement).getSupplyData());
			((DialogAdapterInterface) mDialogImplement).additionalInit(mDialog);
		} else {
			mDialog.init(mDialogImplement.getDialogLableNames(), null, null);
		}
		
        Button saveImageView = (Button) mDialog.getPopupView()
                							.findViewById(R.id.save_Button);
        saveImageView.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View view) {
				// 保存界面输入的数据
				mSaveData = mDialog.SaveData();
				Integer[] columns = mDialogImplement.getImportantColumns(mSaveData);
				
				if (columns != null) {
					for (Integer column : columns) {
						if (mSaveData.get(mDialogLableNames[column]).equals("")) {
							sendMessage(SHOW_TOAST, mContext.
									getResources().getString(R.string.pls_input_all_info));
								return;
						}
					}
				}
				
				if (mIsAddOperation) {	// 添加
					// 获取界面保存的数据
					T addItem = addFieldCopy();
					if (!mIsFloatMenuAdd) {
						addItem.setParentsId(mCurrentItem.getId());
					}
					((ServiceInterface<T>) mServiceImplement).addItem(addItem);
				} else {	// 修改
					updateFieldCopy();
					((ServiceInterface<T>) mServiceImplement)
										.updateItem(mCurrentUpdateItem);
				}
				mDialog.dismiss();
			}        	
        });
	}
	
	/**
	 * 内容添加拷贝
	 * @return
	 */
	@SuppressLint("NewApi") 
	private T addFieldCopy() {
		// 内容列表项添加域拷贝
		T listItem = null;
		try {
			listItem = mListItemClass.newInstance();
		} catch (java.lang.InstantiationException 
						| IllegalAccessException e) {
			e.printStackTrace();
		}
		
		Field[] fs = mListItemClass.getDeclaredFields();
		fieldCopy(fs, listItem, mFieldLableMap);
		return listItem;
	}
	
	/**
	 * 更新域拷贝
	 * @param saveData
	 */
	private void updateFieldCopy() {
		// 内容列表项更新域拷贝
		mCurrentUpdateItem = MiscUtils.clone(mCurrentItem);
		Field[] fs = mListItemClass.getDeclaredFields();
		fieldCopy(fs, mCurrentUpdateItem, mFieldLableMap);
	}
	
	/**
	 * 将UI添加或修改域拷贝到对应的对象中
	 * @param fs
	 * @param target
	 * @param map
	 * @param saveData
	 */
	private boolean fieldCopy(  Field[] fs, 
								T target, 
								Map<String, String> map) {
		if (mSaveData == null) {
			return false;
		}
		
		// 遍历类域
		for (int i = 0; i < fs.length; i++) {
			Field field = fs[i];
			field.setAccessible(true);
			// 如果映射表中包含了该域的名称
			if (map.containsKey(field.getName())) {
				Class<?> type = field.getType();
				String fieldName = field.getName();
				String value = mSaveData.get(map.get(fieldName));
				if (mUpdateFieldSwitchMap != null 
						&& mUpdateFieldSwitchMap.containsKey(fieldName)) {
					// 如果需要域转换，但用户没有输入有效数据，直接跳过
					if (!value.equals("")) {
						value = mUpdateFieldSwitchMap.get(fieldName).get(value);
					}
				}
				
				try {
					if (type.equals(String.class)) {
						field.set(target, value);
					} else if (type.equals(Date.class)) {
						if (!value.equals("")) {
							field.set(target, DateUtils.stringToDate(DateUtils.FORMAT_SHORT, value));
						} else {
							field.set(target, null);
						}
					} else {
						// 清除
						if (value.equals("")) {
							value = "0";
						}
						
						if (type.equals(int.class)) {
							field.setInt(target, Integer.parseInt(value));
						} else if (type.equals(double.class)) {
							field.setDouble(target, Double.parseDouble(value));
						} else if (type.equals((long.class))) {
							field.setLong(target, Long.parseLong(value));
						}
					}
				} catch (IllegalAccessException 
							| IllegalArgumentException e) {
					e.printStackTrace();
				}
			}
		}
		// 重置保存数据
		mSaveData = null;
		
		return true;		
	}
}
