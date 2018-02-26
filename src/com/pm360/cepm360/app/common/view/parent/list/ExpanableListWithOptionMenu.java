package com.pm360.cepm360.app.common.view.parent.list;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Context;
import android.content.DialogInterface;
import android.view.ActionMode;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.View.OnLongClickListener;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.FrameLayout.LayoutParams;

import com.pm360.cepm360.R;
import com.pm360.cepm360.app.common.adpater.DataExpanableListAdapter.ViewHolder;
import com.pm360.cepm360.app.common.custinterface.BaseWidgetInterface;
import com.pm360.cepm360.app.common.custinterface.CommonListInterface;
import com.pm360.cepm360.app.common.custinterface.DialogAdapterInterface;
import com.pm360.cepm360.app.common.custinterface.FloatingMenuInterface;
import com.pm360.cepm360.app.common.custinterface.OperationMode;
import com.pm360.cepm360.app.common.custinterface.OptionMenuInterface;
import com.pm360.cepm360.app.common.custinterface.ServiceInterface;
import com.pm360.cepm360.app.common.custinterface.SimpleDialogInterface;
import com.pm360.cepm360.app.common.view.FloatingMenuView;
import com.pm360.cepm360.app.common.view.OptionsMenuView;
import com.pm360.cepm360.app.common.view.OptionsMenuView.SubMenuListener;
import com.pm360.cepm360.app.common.view.parent.BaseDialog;
import com.pm360.cepm360.app.utils.UtilTools;
import com.pm360.cepm360.common.ResultStatus;
import com.pm360.cepm360.common.json.AnalysisManager;
import com.pm360.cepm360.common.util.DateUtils;
import com.pm360.cepm360.common.util.MiscUtils;

import java.io.Serializable;
import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

public abstract class ExpanableListWithOptionMenu<T extends Serializable> 
									extends SimpleExpanableList<T> {
	
	/**------------------ 视图控件 -----------------*/
	protected OptionsMenuView mOptionsMenu;	// 选项菜单
	protected FloatingMenuView mFloatingMenu;	// 浮动菜单
	protected BaseDialog mDialog;	// 弹出对话框
	
	// 接口
	protected ExpanableListWithOptionMenuInterface mListWithOptionMenuInterface;
	
	/**------------------ 保存资源数据 -------------*/
	// 保存对话框标签名数组
	protected String[] mDialogLableNames;
	
	protected T mCurrentUpdateItem;	// 当前更新列表项
	protected T mSpecifiedItem;		// 指定列表项
	
	// 界面数据保存
	protected Map<String, String> mSaveData;
	private Map<String, Map<String, String>> mUpdateFieldSwitchMap;
	protected String[] mUpdateFields;
	
	private int[] mMenuImages;
	private String[] mMenuTips;
	private String[] mSubMenuNames;	// 选择菜单
	
	/**------------------ 删除操作数据 ------------*/
	protected List<T> mRemoveList = new ArrayList<T>();
	protected int mRemoveCount;
	protected int mRemoveFailedCount;
	
	// 模式定义，普通模式和批量操作模式
	protected ActionMode mActionMode;
	
	// 增加修改操作，是否是增加操作
	protected boolean mIsAddOperation;
	protected boolean mIsFloatMenuAdd;
	
	private boolean mEnableFloatingMenu;	// 是否启用浮动菜单
	private boolean mEnableOptionMenu;	// 使能选择菜单功能
	private boolean mForceEnableOptionMenu;
		
	// 正常模式下是否启用多选功能
	protected boolean mEnableNormalMultSelect;
	
	// 是否隐藏选项菜单
	protected boolean mHideOptionMenu;
	
	/** ----------------- 映射表定义 --------------*/
	// 更新字段映射表
	private Map<String, String> mFieldLableMap;
	
	/** ------------------ 接口 ----------------- */
	protected SimpleDialogInterface mDialogImplement;
	protected OptionMenuInterface mOptionMenuImplement;
	protected FloatingMenuInterface mFloatingMenuImplement;
	
	// 如需要对dialog做额外的界面操作，设置该接口
	protected BaseWidgetInterface mBaseWidgetInterface;
	
	/**
	 * 构造函数
	 * @param context
	 */
	public ExpanableListWithOptionMenu(Context context) {
		super(context);
	}

	/**
	 * 带选项菜单的初始化
	 * @param listItemClass
	 * @param listInterface
	 * @param serviceInterface
	 * @param simpleDialogInterface
	 * @param optionMenuInterface
	 */
	public void init(Class<T> listItemClass,
					CommonListInterface<T> listInterface,
					ServiceInterface<T> serviceInterface,
					SimpleDialogInterface simpleDialogInterface,
					OptionMenuInterface optionMenuInterface) {
		mDialogImplement = simpleDialogInterface;
		mOptionMenuImplement = optionMenuInterface;
		if (mPermissionManager.hasEditPermission() || mForceEnableOptionMenu) {
			mEnableOptionMenu = true;
		}
		
		super.init(listItemClass, listInterface, serviceInterface);
	}
	
	/**
	 * 设置树节点计数更新接口
	 * @param interface1
	 */
	public void setListWithOptionMenuInterface(ExpanableListWithOptionMenuInterface interface1) {
		mListWithOptionMenuInterface = interface1;
	}
	
	/**
	 * 带浮动菜单的初始化
	 * @param listItemClass
	 * @param listInterface
	 * @param serviceInterface
	 * @param simpleDialogInterface
	 * @param floatingMenuInterface
	 */
	public void init(Class<T> listItemClass,
						CommonListInterface<T> listInterface,
						ServiceInterface<T> serviceInterface,
						SimpleDialogInterface simpleDialogInterface,
						FloatingMenuInterface floatingMenuInterface) {
		mDialogImplement = simpleDialogInterface;
		mFloatingMenuImplement = floatingMenuInterface;
		if (mPermissionManager.hasEditPermission()) {
			mEnableFloatingMenu = true;
		}
		
		super.init(listItemClass, listInterface, serviceInterface);
	}
	
	/**
	 * 带选项菜单和浮动菜单的初始化
	 * @param listItemClass
	 * @param listInterface
	 * @param serviceInterface
	 * @param simpleDialogInterface
	 * @param optionMenuInterface
	 * @param floatingMenuInterface
	 */
	public void init(Class<T> listItemClass,
						CommonListInterface<T> listInterface,
						ServiceInterface<T> serviceInterface,
						SimpleDialogInterface simpleDialogInterface,
						OptionMenuInterface optionMenuInterface,
						FloatingMenuInterface floatingMenuInterface) {
		mDialogImplement = simpleDialogInterface;
		mOptionMenuImplement = optionMenuInterface;
		mFloatingMenuImplement = floatingMenuInterface;
		
		if (mPermissionManager.hasEditPermission() || mForceEnableOptionMenu) {
			mEnableOptionMenu = true;
		}
		
		if (mPermissionManager.hasEditPermission()) {
			mEnableFloatingMenu = true;
		}
		
		super.init(listItemClass, listInterface, serviceInterface);
	}
	
	/**
	 * 启用正常模式下多选功能
	 */
	public void enableNormalMultSelect(boolean enable) {
		mEnableNormalMultSelect = enable;
	}
	
	/**
	 * 设置适配器显示列表
	 * @param list
	 */
	public void setShowList(List<T> list) {
		mListAdapter.setDataList(formateData(list));
	}
	
	/**
	 * 获取适配器显示列表
	 * @return
	 */
	public List<T> getShowList() {
		return mListAdapter.getDataList();
	}
	
	/**
	 * 获取浮动菜单控件
	 * @return
	 */
	public FloatingMenuView getFloatingMenu() {
		return mFloatingMenu;
	}
	
	/**
	 * 获取选项菜单控件
	 * @return
	 */
	public OptionsMenuView getOptionsMenu() {
		return mOptionsMenu;
	}
	
	/**
	 * 设置选项菜单接口实现，重新初始化选项菜单
	 * @param interface1
	 */
	public void setOptionMenuInterface(OptionMenuInterface interface1) {
		mOptionMenuImplement = interface1;
		
		if ((mEnableOptionMenu || mForceEnableOptionMenu)
				&& mListItemClass != null) {
			initOptionsMenu();
		}
	}
	
	/**
	 * 设置浮动菜单接口实现，重新初始化浮动菜单
	 * @param interface1
	 */
	public void setFloatingMenuInterface(FloatingMenuInterface interface1) {
		mFloatingMenuImplement = interface1;
		
		if (mEnableFloatingMenu) {
			initFloatingMenu();
		}
	}
	
	/**
	 * 强制使能选项菜单
	 * @param enable
	 */
	public void setForceEnableOptionMenu(boolean enable) {
		mForceEnableOptionMenu = enable;
		if (enable) {
			if (mListItemClass != null) {	// 已经初始化
				
				// 初始化选项菜单
				initOptionsMenu();
			}
		} else {
			if (!mPermissionManager.hasEditPermission()) {
				mOptionsMenu = null;
			}
		}
	}
	
	/**
	 * 设置指定选中项
	 * @param specifiedItem
	 */
	public void setSpecifiedItem(T specifiedItem) {
		mSpecifiedItem = specifiedItem;
	}
	
	/**
	 * 返回定位对象
	 * @return
	 */
	public T getSpecifiedItem() {
		return mSpecifiedItem;
	}

	@SuppressWarnings("unchecked")
	@Override
	protected void handleDataOnResult(ResultStatus status, List<?> list) {
		
		// 结束加载进度对话框
		sendMessage(DISMISS_PROGRESS_DIALOG);
		
		switch (status.getCode()) {
			case AnalysisManager.SUCCESS_DB_QUERY:
				if (OperationMode.MULTI_SELECT == mCurrentMode 
						&& mFilterList != null 
						&& mFilterList.size() > 0) {
						filterSelectedDatas((List<T>) list);
				}
				
				mListAdapter.setDataList(formateData((List<T>) list));
				
				locationSpecifiedItem();
				
				if (mHeaderCheckBox != null && mHeaderCheckBox.isChecked()) {
					mHeaderCheckBox.setChecked(false);
				}
				break;
				
			case AnalysisManager.SUCCESS_DB_ADD:
				if (list != null && !list.isEmpty()) {
					T t = (T) list.get(0);
					mListAdapter.addData(getParentName(t), t);
					
					if (mListWithOptionMenuInterface != null) {
						mListWithOptionMenuInterface.updateTreeCount(1);
					}
				}
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
				if (mCurrentUpdateItem != null) {
					MiscUtils.clone(mCurrentItem, mCurrentUpdateItem);
				}
				mListAdapter.notifyDataSetChanged();
				break;
		}
		
		if (status.getCode() != AnalysisManager.SUCCESS_DB_QUERY
				&& mRemoveCount == 0) {
			sendMessage(SHOW_TOAST, status.getMessage());
		}
	}
	
	/**
	 * 定位到指定列表项
	 */
	protected void locationSpecifiedItem() {
		if (mSpecifiedItem != null && !mListAdapter.getShowList().isEmpty()) {
			int position = mListAdapter.getItem(mSpecifiedItem);
			mCurrentItem = mListAdapter.getItem(position);
			
			mListAdapter.setSelected(position, true);
			mSpecifiedItem = null;
		}
	}
	
	/**
	 * 全部删除成功
	 */
	protected void allDeleteSuccessful() {
		mListAdapter.clearSelection();
		mListAdapter.deleteData(mRemoveList);
		
		// 将当前项置为空
		mCurrentItem = null;
		
		// 更新树节点计数
		if (mListWithOptionMenuInterface != null) {
			mListWithOptionMenuInterface.updateTreeCount(-mRemoveCount);
		}
	}
	
	/**
	 * 部分删除成功
	 * @param status
	 */
	protected void partDeleteSucessful(ResultStatus status) {
		status.setMessage(mContext.getResources()
				.getString(R.string.delete_part_failed));
		mListAdapter.clearAll();
		mServiceImplement.getListData();
	}
	
	/**
	 * 清理删除操作使用的数据
	 */
	protected void deleteDataClear() {
		mRemoveList.clear();
		mRemoveCount = 0;
		mRemoveFailedCount = 0;
	}

	/**
	 * 多选模式中，过滤掉已选择的内容
	 * @param list
	 */
	protected void filterSelectedDatas(List<T> list) {
		Iterator<T> iterator = list.iterator();
		while (iterator.hasNext()) {
			T t = iterator.next();
			for (T filter : mFilterList) {
				if (mListImplement.getListItemId(filter) 
								== mListImplement.getListItemId(t)) {
					iterator.remove();
					break;
				}
			}
		}
	}
	
	@Override
	protected void initFeildsAndMap() {
		super.initFeildsAndMap();
		
		// 初始化更新域
		if (mDialogImplement != null) {
			mUpdateFields = mDialogImplement.getUpdateFeilds();
			
			if (DialogAdapterInterface.class.isInstance(mDialogImplement)) {
				mUpdateFieldSwitchMap = ((DialogAdapterInterface) mDialogImplement).getUpdateFieldsSwitchMap();
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
	}
	
	@Override
	protected void initListView() {
		super.initListView();

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
	 * 设置浮动菜单的开始位置
	 * @param hasSlidePanel
	 */
	public void setFloatingMenuLocation(boolean hasSlidePanel) {
		if (hasSlidePanel) {
			LayoutParams params = (LayoutParams) mFloatingMenu.getLayoutParams();
	        params.rightMargin = UtilTools.dp2pxW(mContext, 116);
		} else {
			LayoutParams params = (LayoutParams) mFloatingMenu.getLayoutParams();
	        params.rightMargin = UtilTools.dp2pxW(mContext, 16);
		}
	}
	
	/**
	 * 设置浮动菜单图片
	 * @param menuImages
	 */
	public void setFloatingMenuImages(int[] menuImages) {
		if (menuImages != null) {
			mMenuImages = menuImages;
		}
	}
	
	/**
	 * 设置浮动菜单提示
	 * @param menuTips
	 */
	public void setFloatingMenuTips(String[] menuTips) {
		if (menuTips != null) {
			mMenuTips = menuTips;
		}
	}
	
	/**
	 * 浮动菜单监听器
	 */
	protected OnItemClickListener mItemClickListener = new OnItemClickListener() {
		
		 @Override
         public void onItemClick(AdapterView<?> parent,
         						View view,
         						int position,
         						long id) {
             floatingMenuItemClick(parent, view, position, id);
         }
	};
	
	/**
	 * 浮动菜单项单击监听事件
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
	public void setItemClickListener(OnItemClickListener listener) {
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
				.getStringArray(R.array.list_options_menu);
		
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
	
	/**
	 * 选项菜单监听器
	 */
	private SubMenuListener mSubMenuListener = new SubMenuListener() {
		
		@Override
		public void onSubMenuClick(View view) {
			mOptionsMenu.dismiss();
			switch ((Integer) view.getTag()) {
				case 0:		// 修改
					mIsAddOperation = false;
					showUpdateDialog(true);
					break;
				case 1:
					commonConfirmDelete();
					break;
			}
		}
	};
	
	/**
	 * 设置选项菜单监听器
	 * @param listener
	 */
	public void setOptionSubMenutListener(SubMenuListener listener) {
		if (listener != null) {
			mSubMenuListener = listener;
		}
		
		mOptionsMenu.setSubMenuListener(mSubMenuListener);
	}
	
	/**
	 * 删除确认提示
	 */
	public void commonConfirmDelete() {
		// 确定按钮监听
		android.content.DialogInterface.OnClickListener clickListener 
						= new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
            	// 首先隐藏对话框
                dialog.dismiss();
                
                mRemoveList.addAll(mListAdapter.getSelectedDatas());
                
                // 开始循环删除材料，普通模式和批量删除模式一样处理
                for (int i = 0; i < mRemoveList.size(); i++) {
                	T listItem = mRemoveList.get(i);
                	((ServiceInterface<T>) mServiceImplement).deleteItem(listItem);
                }
                
                // 如果mActionMode不为空，说明是批量模式，要结束批量模式
                if (mActionMode != null) {
                    mActionMode.finish();
                    mActionMode = null;
                }
            }
		};
		
		showAlertDialog(mContext, mContext.getString(R.string.confirm_delete), 
				clickListener);
	}
	
	/**
	 * 获取对话的值，为对话框显示做准备
	 * @return
	 */
	protected String[] getDefaultDialogValue() {

		Map<String, String> listItemMap = MiscUtils.beanToMap(mCurrentItem);
		
		// 构建默认值数组
		String[] defaultValues = new String[mUpdateFields.length];
		for (int i = 0; i < mUpdateFields.length; i++) {
			if (mUpdateFieldSwitchMap != null &&
					(mUpdateFieldSwitchMap.get(mUpdateFields[i]) != null)) {
				defaultValues[i] = mUpdateFieldSwitchMap.get(mUpdateFields[i])
										.get(listItemMap.get(mUpdateFields[i]));
			} else {
				defaultValues[i] = listItemMap.get(mUpdateFields[i]);
			}
		}
		
		return defaultValues;
	}
	
	/**
	 * 修改一条
	 */
	protected void showUpdateDialog(boolean isEdit) {
		
		// 设置是否可以修改
		mDialog.switchModifyDialog(isEdit);
		
		// 显示内容对话框
		mDialog.show(getDefaultDialogValue());
	}
	
	/** 
	 * 初始化内容弹出对话框
	 */
	protected void initDialog() {
		
		// 对话框标题
		if (mDialogImplement != null) {
			String title = mContext.getResources()
					.getString(mDialogImplement.getDialogTitleId());
			mDialog = new BaseDialog((Activity) mContext, title, mBaseWidgetInterface);
	
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
					if (dialogSaveButtonEvent()) {
						mDialog.dismiss();
						
						if (mFloatingMenu != null) {
							mFloatingMenu.dismiss();
						}

					}
				}        	
	        });
		}
	}
	
	/**
	 * 重载该方法实现dialog保存处理
	 * @param saveData
	 * @return
	 */
	protected boolean dialogSaveButtonEvent() {
		// 保存界面输入的数据
		mSaveData = mDialog.SaveData();
		Integer[] columns = mDialogImplement.getImportantColumns(mSaveData);
		
		if (columns != null) {
			for (Integer column : columns) {
				if (mSaveData.get(mDialogLableNames[column]).equals("")) {
					sendMessage(SHOW_TOAST, mContext.
							getResources().getString(R.string.pls_input_all_info));
						return false;
				}
			}
		}
		
		if (mIsAddOperation) {	// 添加
			// 获取界面保存的数据			
			((ServiceInterface<T>) mServiceImplement).addItem(addFieldCopy());
		} else {	// 修改
			updateFieldCopy();
			((ServiceInterface<T>) mServiceImplement).updateItem(mCurrentUpdateItem);
		}
		return true;
	}
	
	/**
	 * 内容添加拷贝
	 * @return
	 */
	@SuppressLint("NewApi") 
	protected T addFieldCopy() {
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
	protected void updateFieldCopy() {
		
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
						&& (mUpdateFieldSwitchMap.get(fieldName) != null)) {
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
	
	/**
	 * 用于返回内容适配器中已经选择的列表项
	 * @return
	 */
	public List<T> getSelectedDataList() {
		return mListAdapter.getSelectedDatas();
	}
	
	/**
     * 用于列表的多选 或批量操作
     */
    protected ActionMode.Callback mCallback = new ActionMode.Callback() {
    	// 进入批量操作前的准备工作
        @Override
        public boolean onPrepareActionMode(ActionMode mode, Menu menu) {
            return false;
        }

        // 退出批量操作前的清理工作
        @Override
        public void onDestroyActionMode(ActionMode mode) {
            mListAdapter.setSelected(-1, false);
            mActionMode = null;
            mode = null;
        }

        // 进入批量操作初始化工作
        @Override
        public boolean onCreateActionMode(ActionMode mode, Menu menu) {
            MenuInflater inflater = mode.getMenuInflater();
            inflater.inflate(R.menu.operation_menu, menu);
            return true;
        }

        // 批量操作处理
        @Override
        public boolean onActionItemClicked(ActionMode mode, MenuItem item) {
            switch (item.getItemId()) {
	            case R.id.action_delete:	// 删除操作
	                commonConfirmDelete();
	                break;
	                
	            case R.id.action_select_all:	// 权限操作
	                mListAdapter.setSelectedAll();
	                updateActionModeTitle(  mActionMode, 
    						mContext, 
    						mListAdapter.getSelectedList().size());
	                break;
            }
            return false;
        }
    };

    /**
     * 当增加或减少选择项时，更新ActionMode标题
     * @param mode
     * @param context
     * @param selectedNum
     */
    protected void updateActionModeTitle(  ActionMode mode, 
    									Context context,
    									int selectedNum) {
        if (mode != null) {
            mode.setTitle(context.getString(R.string.multi_select_title, selectedNum));
        }
    }
    
	@Override
	protected void clickRegister(final ViewHolder viewHolder, final int position) {
		for (int i = 0; i < mListItemIds.length; i++ ) {
			
			// 单击监听
			viewHolder.tvs[i].setOnClickListener(new OnClickListener() {
				
				@Override
				public void onClick(View view) {
					clickHandler(viewHolder, view, position);
				}
			});
			
			// 多选模式下的处理
			multiModeHandle(viewHolder, position);
			
			// 正常模式下，注册长按监听器
			if (OperationMode.NORMAL == mCurrentMode) {
				
				// 注册长按监听
                viewHolder.tvs[i].setOnLongClickListener(new OnLongClickListener() {
					
					@Override
					public boolean onLongClick(View view) {
						longClickHandler(view, position);
						return true;
					}
				});
			}
		}
	}
	
	/**
	 * 单击注册
	 * @param viewHolder
	 * @param view
	 * @param position
	 */
	protected void clickHandler(final ViewHolder viewHolder, 
										final View view, final int position) {
		// ActionMode批量操作模式
		if (mActionMode != null) {
			mListAdapter.setPickSelected(position);
			updateActionModeTitle(mActionMode, mContext,
                    			  mListAdapter.getSelectedList().size());
		// 非多选模式，弹出选项菜单，但如果是单选模式也不会弹出
		} else if (OperationMode.MULTI_SELECT != mCurrentMode) {
			mCurrentItem = mListAdapter.getItem(position);
			mListAdapter.setSelected(position, true);
				
			// 切换选项菜单
			switchOptionMenu(view);
		} else { // 多选择模式
			if (viewHolder.cbs[0].isChecked()) {
				viewHolder.cbs[0].setChecked(false);
				if (mHeaderCheckBox.isChecked()) {
					mHeaderCheckBox.setChecked(false);
				}
				
				T t = mListAdapter.getItem(position);
				mSelectedDataList.remove(t);
			} else {
				viewHolder.cbs[0].setChecked(true);
				mCurrentItem = mListAdapter.getItem(position);
				
				mSelectedDataList.add(mCurrentItem);
			}
			mListAdapter.setPickSelected(position);
		}
	}
	
	/**
	 * 多选模式的处理
	 * @param viewHolder
	 * @param position
	 */
	protected void multiModeHandle(ViewHolder viewHolder, final int position) {
		
		// 多选模式，启动多选功能（注册复选框的单击监听器）
		if (OperationMode.MULTI_SELECT == mCurrentMode) {
			if (mListAdapter.isContainPosition(position)) {
				viewHolder.cbs[0].setChecked(true);
			} else {
				viewHolder.cbs[0].setChecked(false);
			}
			
			viewHolder.cbs[0].setOnClickListener(new View.OnClickListener() {
				
				@Override
				public void onClick(View view) {
					if (((CheckBox) view).isChecked()) {
						mCurrentItem = mListAdapter.getItem(position);
						
						mSelectedDataList.add(mCurrentItem);
					} else {
						if (mHeaderCheckBox.isChecked()) {
							mHeaderCheckBox.setChecked(false);
						}
						
						T t = mListAdapter.getItem(position);
						mSelectedDataList.remove(t);
					}
					mListAdapter.setPickSelected(position);
				}
			});
		}
	}
	
	/**
	 * 长按监听
	 * @param view
	 * @param position
	 */
	protected void longClickHandler(View view, final int position) {
		if (mPermissionManager.hasEditPermission()
				&& mCurrentMode == OperationMode.NORMAL
				&& mEnableNormalMultSelect) {

			// 长按进入ActionMode，此时ActionMode应该是NULL
			mActionMode = ((Activity) mContext).startActionMode(mCallback);
			mListAdapter.setPickSelected(position);
			updateActionModeTitle(mActionMode, mContext, 
						mListAdapter.getSelectedList().size());
		} else {
			mCurrentItem = mListAdapter.getItem(position);
			mListAdapter.setSelected(position, true);
		}
	}
	
	/**
	 * 切换选项菜单，注意重写时最后调用父函数
	 * @param view 监听控件
	 */
	public void switchOptionMenu(View view) {
		
		// 如果是选择模式和无编辑权限下，mContentOptionsMenu不被初始化，为空
		if (mOptionsMenu != null && !mHideOptionMenu) {
			
			mOptionsMenu.showAsDropDown(view, 0, (-view
					.getMeasuredHeight() - UtilTools.dp2pxH(view.getContext(), 46)));
		}
	}
	
	/**
	 * 该类的接口，使用ListWithOptionMenu和Tree的双层结构时实现该接口
	 * @author yuanlu
	 *
	 */
	public interface ExpanableListWithOptionMenuInterface {
		
		/**
		 * 更新树
		 */
		public void updateTreeCount(int count);
	}
	
	/**
	 * 获取T的父名称
	 * @param t
	 * @return
	 */
	public abstract String getParentName(T t);
}
