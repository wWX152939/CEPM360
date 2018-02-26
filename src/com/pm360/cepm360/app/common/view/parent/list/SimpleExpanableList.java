package com.pm360.cepm360.app.common.view.parent.list;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Color;
import android.text.TextUtils.TruncateAt;
import android.util.TypedValue;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.ExpandableListView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.pm360.cepm360.R;
import com.pm360.cepm360.app.common.activity.DataManagerInterface;
import com.pm360.cepm360.app.common.activity.ListSelectActivity;
import com.pm360.cepm360.app.common.adpater.DataExpanableListAdapter;
import com.pm360.cepm360.app.common.adpater.DataExpanableListAdapter.ChildListAdapterInterface;
import com.pm360.cepm360.app.common.adpater.DataExpanableListAdapter.ParentListAdapterInterface;
import com.pm360.cepm360.app.common.adpater.DataExpanableListAdapter.ViewHolder;
import com.pm360.cepm360.app.common.custinterface.CommonListInterface;
import com.pm360.cepm360.app.common.custinterface.FieldRemap;
import com.pm360.cepm360.app.common.custinterface.OperationMode;
import com.pm360.cepm360.app.common.custinterface.SelectInterface;
import com.pm360.cepm360.app.common.custinterface.SimpleServiceInterface;
import com.pm360.cepm360.common.ResultStatus;
import com.pm360.cepm360.common.json.AnalysisManager;
import com.pm360.cepm360.common.util.MiscUtils;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public abstract class SimpleExpanableList<T extends Serializable> extends BaseListCommon<T>
							implements SelectInterface {
	
	/**------------------ 视图控件 -----------------*/
	protected View mRootLayout;		// 根布局
	protected LayoutInflater mInflater;
	protected LinearLayout mListLayout;
	protected View mButtonBar;
	
	protected View mListHeader;		// 列表头视图
	private ExpandableListView mListView;		// 列表视图
	protected DataExpanableListAdapter<T> mListAdapter;	// 适配器adapter定义
	
	protected ProgressDialog mProgressDialog;	// 进度对话框
	protected CheckBox mHeaderCheckBox;	// 列表头的复选框
	
	/**------------------ 保存资源数据 -------------*/
	protected int[] mListHeaderItemIds;		// 列表头控件ID数组
	private String[] mListItemNames;		// 列表头项名称
	protected int[] mListItemIds;			// 列表项控件ID列表
	
	private Map<String, Map<String, String>> mDisplayFieldSwitchMap;
	
	protected String[] mDisplayFields;	// 内容显示的域
	
	/** ----------------- 映射表定义 --------------*/
	protected List<T> mFilterList;	// 过滤列表项
	protected List<T> mSelectedDataList = new ArrayList<T>();	// 多选模式选中的项
	
	/** ------------------- 标识 -------------------- */
	protected boolean mDataLoaded;	// 数据是否加载成功
	private boolean mEnableInnerButton = true;
	private boolean mEnableListHeader = true;// 是否禁用列表头
	
	/** ------------------ 接口 ----------------- */
	protected CommonListInterface<T> mListImplement;
	protected SimpleServiceInterface<T> mServiceImplement;
	
	// 域重映射接口
	private FieldRemap<T> mFieldRemap;
	
	/** --------------------------------- 方法定义 ------------------------ */
	
	/**
	 * 构造函数
	 * @param context
	 */
	public SimpleExpanableList(Context context) {
		super(context);
	}
	
	/**
	 * 获取列表适配器
	 * @return
	 */
	public DataExpanableListAdapter<T> getListAdapter() {
		return mListAdapter;
	}
	/**
	 * 返回服务请求处理接口实现
	 * @return
	 */
	public DataManagerInterface getServiceManager() {
		return mManager;
	}
	
	/**
	 * 获取根布局
	 * @return
	 */
	public View getRootView() {
		return mRootLayout;
	}
	
	/**
	 * 服务返回结果处理
	 */
	private DataManagerInterface mManager = new DataManagerInterface() {

		@Override
		public void getDataOnResult(ResultStatus status, List<?> list) {
			handleDataOnResult(status, list);
		}
	};
	
	/**
	 * 数据加载成功处理函数
	 * @param status
	 * @param list
	 */
	@SuppressWarnings("unchecked")
	protected void handleDataOnResult(ResultStatus status, List<?> list) {
		
		// 结束加载进度对话框
		sendMessage(DISMISS_PROGRESS_DIALOG);
		
		switch (status.getCode()) {
			case AnalysisManager.SUCCESS_DB_QUERY:
				mListAdapter.setDataList(formateData((List<T>) list));
				break;
			case AnalysisManager.EXCEPTION_DB_QUERY:
				sendMessage(SHOW_TOAST, status.getMessage());
				break;
			default:
				break;
		}
	}

	/**
	 * 简单初始化
	 * @param context
	 * @param listItemClass
	 * @param listInterface
	 * @param serviceInterface
	 */
	public void init(Class<T> listItemClass,
						CommonListInterface<T> listInterface,
						SimpleServiceInterface<T> serviceInterface) {
		super.init();
		mListItemClass = listItemClass;
		mListImplement = listInterface;
		mServiceImplement = serviceInterface;
		
		// 初始化显示域并映射
		initFeildsAndMap();
		
		// 初始化视图
		initLayout();
		
		// 加载数据
		loadData();
	}
	
	/**
	 * 初始化域并建立域映射表
	 */
	protected void initFeildsAndMap() {
		
		// 初始化显示域
		mDisplayFields = mListImplement.getDisplayFeilds();
		
		// 建立域映射
		mDisplayFieldSwitchMap = mListImplement.getDisplayFieldsSwitchMap();
		
		TypedArray typedArray = mContext.getResources()
					.obtainTypedArray(mListImplement.getListItemIds());
		mListItemIds = new int[typedArray.length()];
		for (int i = 0; i < mListItemIds.length; i++) {
			mListItemIds[i] = typedArray.getResourceId(i, 0);
		}
		
		typedArray.recycle();
	}
	
	/**
	 * 初始化视图
	 */
	private void initLayout() {
		mInflater = (LayoutInflater)mContext
				.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
		
		// 生成视图根布局视图
		if (mLocationImpl == null) {
			mRootLayout = mInflater.inflate(R.layout.expanable_list_layout, null);
		} else {
			mRootLayout = mLocationImpl.getRootView()
						.findViewById(mLocationImpl.getLocationId());
		}
		
		// 设置内容列表背景色
		mListLayout = (LinearLayout) mRootLayout.findViewById(R.id.content_list_layout);
		
		// 初始化树列表视图
		initListView();
	}
	
	/**
	 * 初始化内容视图
	 */
	protected void initListView() {
		// 初始化内容列表头
		initListHeader();
		
		// 为内容列表设置适配器
		setListAdapter();
		
		if (OperationMode.NORMAL != mCurrentMode
							&& mEnableInnerButton) {
			initActionButton();
		}
	}
	
	/**
	 * 初始化内容列表头
	 */
	private void initListHeader() {
		
		// 库存列表头布局，这里采用软件绑定布局方法
	    ViewGroup parent = (ViewGroup) 
	    			mRootLayout.findViewById(R.id.content_header_layout);
		
		// 如果禁用列表头，设置为不可见
		if (!mEnableListHeader) {
			parent.setVisibility(View.GONE);
			return;
		}
		
		mListHeader = mInflater.inflate(mListImplement
							.getListHeaderLayoutId(), parent, false);
		
		if (OperationMode.MULTI_SELECT == mCurrentMode) {
		    mHeaderCheckBox = (CheckBox) mListHeader.findViewById(R.id.mult_select);
		    mHeaderCheckBox.setVisibility(View.VISIBLE);
		    mHeaderCheckBox.setOnClickListener(new View.OnClickListener() {
				
				@Override
				public void onClick(View view) {
					if (((CheckBox) view).isChecked()) {
						mSelectedDataList.removeAll(mListAdapter.getSelectedDatas());
						mListAdapter.setSelectedAll();
						mSelectedDataList.addAll(mListAdapter.getSelectedDatas());
					} else {
						mSelectedDataList.removeAll(mListAdapter.getSelectedDatas());
						mListAdapter.clearSelection();
						mListAdapter.notifyDataSetChanged();
					}
				}
			});
			mListHeader.findViewById(R.id.select_right_view).setVisibility(View.VISIBLE);
		}
		
		// 获取列表使用的相关资源
		mListItemNames = mContext.getResources()
						.getStringArray(mListImplement.getListHeaderNames());
		TypedArray typedArray = mContext.getResources()
						.obtainTypedArray(mListImplement.getListHeaderIds());

		if (mListItemNames != null) {
			mListHeaderItemIds = new int[typedArray.length()];
			for (int i = 0; i < mListHeaderItemIds.length; i++) {
				mListHeaderItemIds[i] = typedArray.getResourceId(i, 0);
				if (i < mListItemNames.length) {
					// 配置列表头项相关字段
					TextView tv = (TextView) 
							mListHeader.findViewById(mListHeaderItemIds[i]);
					tv.setTextSize(TypedValue.COMPLEX_UNIT_PX, mContext.getResources().getDimension(R.dimen.table_title_textsize));
					tv.setText(mListItemNames[i]);
				}
			}
		}
		
//		// 隐藏滑动指示线
//		mListHeader.findViewById(R.id.slide_flage_line).setVisibility(View.GONE);
		
		typedArray.recycle();
		parent.addView(mListHeader);
	}
	
	/**
	 * 为内容列表设置适配器   
	 */
	private void setListAdapter() {
		
		// 获取内容列表视图控件
		mListView = (ExpandableListView) mRootLayout.findViewById(R.id.content_listview);
		
		// 初始化适配器并与内容列表视图控件绑定
		mListAdapter = new DataExpanableListAdapter<T>(mContext, 
							mParentListAdapterInterface, mListAdapterImplement, mListView);
		mListView.setAdapter(mListAdapter);
	}
	
	/**
	 * 父列表适配器接口实现
	 */
	private ParentListAdapterInterface mParentListAdapterInterface 
											= new ParentListAdapterInterface() {

		@Override
		public int getParentLayoutId() {
			return R.layout.expanable_parent_list_layout;
		}

		@Override
		public void regesterParentListeners(ViewHolder viewHolder, int position) {
			
		}

		@Override
		public void initParentListViewItem(View convertView, ViewHolder holder,
				DataExpanableListAdapter<?> adapter, int position) {
			String name = adapter.getGroup(position);
			holder.tvs[0].setText(name);
		}

		@Override
		public void initParentLayout(View convertView, ViewHolder holder) {
			// 分配引用数组内存
			holder.tvs = new TextView[1];
			holder.tvs[0] = (TextView) convertView.findViewById(R.id.expanable_parent);
		}
	};
	
	/**
	 * 实现列表适配器接口
	 */
	private ChildListAdapterInterface<T> mListAdapterImplement 
										= new ChildListAdapterInterface<T>() {
		@Override
		public int getChildLayoutId() {
			return mListImplement.getListLayoutId();
		}

		@Override
		public View getChildHeaderView() {
			return mListHeader;
		}

		@Override
		public void regesterChildListeners(final ViewHolder viewHolder, final int position) {
			clickRegister(viewHolder, position);
		}

		@Override
		public void initChildListViewItem(View convertView, ViewHolder holder,
										DataExpanableListAdapter<T> adapter, int position) {
			// 获取当前位置对象，然后转换为映射
			T listItem = adapter.getItem(position);
			
			if (OperationMode.MULTI_SELECT == mCurrentMode
					&& mSelectedDataList.contains(listItem)) {
				if (!mListAdapter.isContainPosition(position)) {
					holder.cbs[0].setChecked(true);
					mListAdapter.setPickSelected(position);
				}
			}
			
			Map<String, String> displayFieldMap = MiscUtils.beanToMap(listItem);
			
			// 重新映射域
			displayFieldRemap(displayFieldMap, listItem, position);
			
			// 更多操作
			initListViewItemMore(holder, position, displayFieldMap);
		}

		@Override
		public void initChildLayout(View convertView, ViewHolder holder) {
			
			// 分配引用数组内存
			holder.tvs = new TextView[mListItemIds.length];
			holder.cbs = new CheckBox[1];
			
			// 为引用数组成员分配视图对象内存
			initLayoutMore(convertView, holder);
			
			if (OperationMode.MULTI_SELECT == mCurrentMode) {
				// 显示复选框和分界线
				holder.cbs[0] = (CheckBox) convertView.findViewById(R.id.mult_select);
				holder.cbs[0].setVisibility(View.VISIBLE);
				convertView.findViewById(R.id.select_right_view).setVisibility(View.VISIBLE);
			}
		}

		@Override
		public List<T> findByCondition(Object... condition) {
			return null;
		}

		@Override
		public boolean isSameObject(T t1, T t2) {
			return mListImplement.getListItemId(t1) == 
							mListImplement.getListItemId(t2);
		}
	};
	
	/**
	 * 初始化列表项视图
	 * @param holder
	 * @param displayFieldMap
	 */
	protected void initListViewItemMore(ViewHolder holder, int position,
							Map<String, String> displayFieldMap) {
		for (int i = 0; i < mDisplayFields.length; i++) {
			if ((displayFieldMap.get(mDisplayFields[i]) != null) 
					&& !(displayFieldMap.get(mDisplayFields[i])).equals("0")) {
				holder.tvs[i].setText(displayFieldMap.get(mDisplayFields[i]));
				if (OperationMode.NORMAL != mCurrentMode) {
					holder.tvs[i].setTextColor(Color.BLACK);
				}
				holder.tvs[i].setEllipsize(TruncateAt.MARQUEE);
			} else {
				holder.tvs[i].setText("");
			}
		}
	}
	
	/**
	 * 
	 * @param convertView
	 * @param holder
	 */
	protected void initLayoutMore(View convertView, ViewHolder holder) {
		for (int i = 0; i < mListItemIds.length; i++) {
			holder.tvs[i] = (TextView) convertView.findViewById(mListItemIds[i]);
		}
	}
	
	/**
	 * 控件监听注册
	 * @param viewHolder
	 * @param position
	 */
	protected void clickRegister(final ViewHolder viewHolder, final int position) {
		for (int i = 0; i < mListItemIds.length; i++ ) {
			viewHolder.tvs[i].setOnClickListener(new OnClickListener() {
				
				@Override
				public void onClick(View view) {
					// 非多选模式，弹出选项菜单，但如果是单选模式也不会弹出
					if (OperationMode.MULTI_SELECT != mCurrentMode) {
						mCurrentItem = mListAdapter.getItem(position);
						mListAdapter.setSelected(position, true);
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
			});
			
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
	}

	/**
	 * 显示域重映射，如果要做额外映射请实现mFieldRemap对应接口
	 * @param displayFieldMap
	 * @param t
	 * @param position
	 */
	protected void displayFieldRemap(Map<String, String> displayFieldMap,
										T t, int position) {
		// 重映射序号
		displayFieldMap.put(SERIAL_NUMBER, position + 1 + "");
		
		// 重映射状态
		for (int i = 1; i < mDisplayFields.length; i++) {
			if (mDisplayFieldSwitchMap != null
					&& (mDisplayFieldSwitchMap.get(mDisplayFields[i]) != null)) {
				String value = displayFieldMap.get(mDisplayFields[i]);
				String result = mDisplayFieldSwitchMap.get(mDisplayFields[i]).get(value);
				
				displayFieldMap.put(mDisplayFields[i], result);
			}
		}
		
		if (mFieldRemap != null) {
			mFieldRemap.fieldRemap(displayFieldMap, t, position);
		}
	}

	/**
	 * 加载数据
	 */
	protected void loadData() {
		
		// 延时显示进度对话框
		sendEmptyMessageDelayed(SHOW_PROGRESS_DIALOG, DELAYTIME_FOR_SHOW);
		
		// 加载资源类型数据
		mServiceImplement.getListData();
	}

	/**
	 * 设置过滤数据
	 */
	@SuppressWarnings("unchecked")
	@Override
	public void setFilterData(List<?> filters) {
		mFilterList = (List<T>) filters;
		loadData();
	}

	/**
	 * 选择模式下使用
	 */
	@Override
	public void enableInnerButton(boolean enable) {
		mEnableInnerButton = enable;
		
		// 如果已经初始化，将其设置为不可见
		if (!enable) {
			if (mButtonBar != null) {
				mButtonBar.setVisibility(View.GONE);
			}
		} else {
			
			/*
			 *  如果要使能按钮，但当前按钮为NULL并且已经完成初始化，
			 *  则初始化按钮控件
			 */
			if (mButtonBar == null && mRootLayout != null) {
				initActionButton();
			}
		}
	}
	
	/**
	 * 用于返回内容适配器中已经选择的列表项
	 * @return
	 */
	public List<T> getSelectedDataList() {
		return mListAdapter.getSelectedDatas();
	}
	
	/**
	 * 设置域重映射接口
	 * @param fieldRemap
	 */
	public void setFieldRemap(FieldRemap<T> fieldRemap) {
		mFieldRemap = fieldRemap;
	}
	
	/**
	 * 确认/取消 按钮
	 */
	private void initActionButton() {
		mButtonBar = (View) mRootLayout.findViewById(R.id.button_bar);
		mButtonBar.setVisibility(View.VISIBLE);
	    
        Button ok = (Button) mRootLayout.findViewById(R.id.btn_ok);
        ok.setOnClickListener(new View.OnClickListener() {

            @SuppressWarnings({ "rawtypes", "unchecked" })
			@Override
            public void onClick(View view) {
                Serializable result = null;
                List<T> data = null;
                if (mCurrentMode == OperationMode.SINGLE_SELECT) {
                	data = getSelectedDataList();
                    if (data.size() == 1) {
                        result = data.get(BASE_POSITION);
                    }
                } else {
                	data = (List<T>) mSelectedDataList;
                    if (data.size() > 0) {
                        result = (Serializable) data;
                    }
                }
                ((ListSelectActivity) mContext).setSeletctedData(result);
            }
        });

        Button cancel = (Button) mRootLayout.findViewById(R.id.btn_cancel);
        cancel.setOnClickListener(new View.OnClickListener() {

            @SuppressWarnings({ "rawtypes", "unchecked" })
			@Override
            public void onClick(View view) {
            	((ListSelectActivity) mContext).setSeletctedData(null);
            }
        });
    }
	
	/**
	 * 格式化数据，生成父子映射
	 * @param list
	 * @return
	 */
	public abstract Map<String, List<T>> formateData(List<T> list);
}
