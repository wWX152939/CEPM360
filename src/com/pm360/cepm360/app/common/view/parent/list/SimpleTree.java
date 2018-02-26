package com.pm360.cepm360.app.common.view.parent.list;

import android.content.Context;
import android.content.res.TypedArray;
import android.util.TypedValue;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;

import com.pm360.cepm360.R;
import com.pm360.cepm360.app.common.activity.DataManagerInterface;
import com.pm360.cepm360.app.common.activity.ListSelectActivity;
import com.pm360.cepm360.app.common.adpater.BaseDragListAdapter;
import com.pm360.cepm360.app.common.adpater.DataTreeListAdapter;
import com.pm360.cepm360.app.common.adpater.DataTreeListAdapter.TreeListAdapterInterface;
import com.pm360.cepm360.app.common.adpater.DataTreeListAdapter.ViewHolder;
import com.pm360.cepm360.app.common.custinterface.CommonListInterface;
import com.pm360.cepm360.app.common.custinterface.FieldRemap;
import com.pm360.cepm360.app.common.custinterface.OperationMode;
import com.pm360.cepm360.app.common.custinterface.SHCommonListInterface;
import com.pm360.cepm360.app.common.custinterface.SimpleServiceInterface;
import com.pm360.cepm360.app.common.view.parent.BaseSlidingPaneActivity;
import com.pm360.cepm360.common.ResultStatus;
import com.pm360.cepm360.common.json.AnalysisManager;
import com.pm360.cepm360.common.util.MiscUtils;
import com.pm360.cepm360.entity.Expandable;

import java.io.Serializable;
import java.util.List;
import java.util.Map;

/**
 * BaseTree 树列表类定义
 * @author yuanlu
 *
 * @param <T>
 */
public class SimpleTree<T extends Expandable & Serializable> extends BaseListCommon<T> {

	/**------------------ 视图控件及适配器 -----------------*/
	protected View mRootLayout;		// 根布局
	protected LayoutInflater mInflater;
	
	protected View mListHeader;		// 列表头视图
	protected ListView mListView;	// 列表视图
	protected DataTreeListAdapter<T> mListAdapter;	// 适配器adapter定义
	
	/**------------------ 保存资源数据 -------------*/
	// 通过xml文件提供内容表头控件ID数组
	protected int[] mListHeaderItemIds;
	protected int[] mListItemIds;
	
	// 通过xml文件提供内容表头字符串数组
	protected String[] mListHeaderNames;
	
	/**------------------ 暂存数据 -----------------*/
	protected Map<String, Map<String, String>> mDisplayFieldSwitchMap;
	protected String[] mDisplayFields;	// 显示域数组
	
	/** ------------------ 接口 ----------------- */
	protected CommonListInterface<T> mListImplement;
	protected SimpleServiceInterface<T> mServiceImplement;
	
	private FieldRemap<T> mFieldRemap;
	
	/**
	 * 构造函数
	 * @param context
	 */
	public SimpleTree(Context context) {
		super(context);
	}
	
	/**
	 * 只有现实功能的初始化
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
	 * 获取树适配器
	 * @return
	 */
	public DataTreeListAdapter<T> getListAdapter() {
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
	 * 设置域重映射接口
	 * @param fieldRemap
	 */
	public void setFieldRemap(FieldRemap<T> fieldRemap) {
		mFieldRemap = fieldRemap;
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
				mListAdapter.setDataList((List<T>) list);
				break;
			case AnalysisManager.EXCEPTION_DB_QUERY:
				sendMessage(SHOW_TOAST, status.getMessage());
				break;
			default:
				break;
		}
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
		
		// 生成根布局视图
		if (mLocationImpl == null) {
			mInflater = (LayoutInflater)
					mContext.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
			
			mRootLayout = mInflater.inflate(R.layout.base_list_fragment, null);
		} else {
			mRootLayout = mLocationImpl.getRootView()
					.findViewById(mLocationImpl.getLocationId());
		}
		
		// 初始化树列表视图
		initTreeView();
	}
	
	/**
	 * 初始化树视图
	 */
	protected void initTreeView() {
		
		// 初始化树视图列表头
		initTreeHeader();
		
		// 为树列表设置适配器
		initTreeList();
		
		// 选择模式
		if (OperationMode.NORMAL != mCurrentMode) {
			initActionButton();
		}
	}
	
	/**
	 * 初始化树列表头
	 */
	protected void initTreeHeader() {
		
		// 列表头布局，这里采用软件绑定布局方法
	    ViewGroup parent = (ViewGroup) 
	    			mRootLayout.findViewById(R.id.content_header_layout);
		mListHeader = mInflater.inflate(mListImplement.getListHeaderLayoutId(), 
										parent, false);
		
		// 获取列表使用的相关资源
		mListHeaderNames = mContext.getResources()
						.getStringArray(mListImplement.getListHeaderNames());
		TypedArray typedArray = mContext.getResources()
						.obtainTypedArray(mListImplement.getListHeaderIds());

		if (mListHeaderNames != null) {
			mListHeaderItemIds = new int[typedArray.length()];
			for (int i = 0; i < mListHeaderItemIds.length; i++) {
				mListHeaderItemIds[i] = typedArray.getResourceId(i, 0);
				if (i < mListHeaderNames.length) {
					
					// 配置列表头项相关字段
					TextView tv = (TextView) 
							mListHeader.findViewById(mListHeaderItemIds[i]);
					tv.setTextSize(TypedValue.COMPLEX_UNIT_PX, mContext
							.getResources().getDimension(R.dimen.table_title_textsize));
					tv.setText(mListHeaderNames[i]);
					tv.setGravity(Gravity.CENTER);
					tv.setPadding(0, 0, 0, 0);
				}
			}
		}
		
		typedArray.recycle();
		parent.addView(mListHeader);
	}
	
	/**
	 * 为内容列表设置适配器   
	 */
	protected void initTreeList() {
		
		// 设置内容列表背景色
		View contentLayout = mRootLayout.findViewById(R.id.content_list_layout);
		
		if (mCurrentMode != OperationMode.NORMAL) {
			contentLayout.setBackgroundResource(R.drawable.corners_white_bg);
		}
		
		// 获取内容列表视图控件
		mListView = (ListView) mRootLayout.findViewById(R.id.content_listview);
		
		// 初始化适配器并与内容列表视图控件绑定
		int id = SHCommonListInterface.class.isInstance(mListImplement)
					? ((SHCommonListInterface<T>) mListImplement).getListItemDoScrollIds() 
					: mListImplement.getListHeaderIds();
					
		mListAdapter = new DataTreeListAdapter<T>(mContext,
								mListAdapterImplement, false, id);
		mListAdapter.setOngetBDLASlidePaneListener(new BaseDragListAdapter
				.OngetBDLASlidePaneListener() {

			@Override
			public View getSlidePane() {
				View view = null;
				if (BaseSlidingPaneActivity.class.isInstance(mContext)) {
					view = ((BaseSlidingPaneActivity) mContext)
											.getSlidingPaneLayout();
				}
				return view;
			}
		});
		
		mListAdapter.addCHScrollView(null, mListHeader);
		mListView.setAdapter(mListAdapter);
	}
	
	/**
	 * 实现列表适配器接口
	 */
	private TreeListAdapterInterface mListAdapterImplement 
										= new TreeListAdapterInterface() {

		@Override
		public int getLayoutId() {
			return mListImplement.getListLayoutId();
		}

		@Override
		public void initListViewItem(ViewHolder viewHolder,
									 int position) {
			// 获取position的数据项，并转化为map
			T treeNode = mListAdapter.getItem(position);
			// 建立域名称和域值的映射
			Map<String, String> treeNodeMap = MiscUtils.beanToMap(treeNode);
			
			displayFieldRemap(treeNodeMap, treeNode, position);
			
			for (int i = 0; i < mDisplayFields.length; i++) {
				setListItemTextContent(viewHolder, treeNodeMap, i, position);
			}
		}

		@Override
		public void regesterListeners( final ViewHolder viewHolder, final int position) {
			clickListener(viewHolder, position);			
		}

		@Override
		public void initLayout( View convertView, ViewHolder holder) {
			
			// 分配引用数组内存
			holder.ivs = new ImageView[1];
			for (int i = 0; i < holder.ivs.length; i++) {
				holder.ivs[i] = (ImageView) convertView.findViewById(mListItemIds[i]);
			}
			
			// 为引用数组成员分配视图对象内存
			holder.tvs = new TextView[mListItemIds.length-holder.ivs.length];
			for (int i = 0; i < holder.tvs.length; i++) {
				holder.tvs[i] = (TextView) 
						convertView.findViewById(mListItemIds[i + holder.ivs.length]);
			}
		}

		@Override
		public void calculateContentItemCount() {
			
		}
	};
	
	/**
	 * 单击、长按监听处理，可被子类重写
	 * @param viewHolder
	 * @param position
	 */
	protected void clickListener(final ViewHolder viewHolder, final int position) {
		for (int i = 0; i < viewHolder.ivs.length; i++) {
			viewHolder.ivs[i].setOnClickListener(new OnClickListener() {
				
				@Override
				public void onClick(View view) {
					handleClick(position);
				}
			});
		}
		
		viewHolder.tvs[0].setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View view) {
				handleClick(position);
			}
		});
	}
	
	/**
	 * 显示域重映射
	 * @param displayFieldMap
	 * @param t
	 * @param position
	 */
	protected void displayFieldRemap(Map<String, String> displayFieldMap,
										T t, int position) {
		if (mFieldRemap == null) {
			
			// 重映射状态
			for (int i = 0; i < mDisplayFields.length; i++) {
				if (mDisplayFieldSwitchMap != null 
						&& mDisplayFieldSwitchMap.containsKey(mDisplayFields[i])) {
					String value = displayFieldMap.get(mDisplayFields[i]);
					String result = mDisplayFieldSwitchMap.get(mDisplayFields[i]).get(value);
					displayFieldMap.put(mDisplayFields[i], result);
				}
			}
		} else {
			mFieldRemap.fieldRemap(displayFieldMap, t, position);
		}
	}
	
	/**
	 * 设置列表项内容
	 * @param viewHolder
	 * @param treeNodeMap
	 * @param i
	 * @param position
	 */
	protected void setListItemTextContent(ViewHolder viewHolder, 
					Map<String, String> treeNodeMap, int i, int position) {
		if ((treeNodeMap.get(mDisplayFields[i]) != null) 
				&& !(treeNodeMap.get(mDisplayFields[i])).equals("0")) {
			viewHolder.tvs[i].setText(treeNodeMap.get(mDisplayFields[i]));
		} else {
			viewHolder.tvs[i].setText("");
		}
	}
	
	/**
	 * 单击处理
	 * @param position
	 */
	protected void handleClick(int position) {
		mCurrentItem = mListAdapter.getItem(position);
		mListAdapter.setSelected(position, true);
		mListAdapter.updateListView(position);
	}
	
	/**
	 * 确认/取消 按钮
	 */
	private void initActionButton() {
	    View buttonLayout = (View) mRootLayout.findViewById(R.id.button_bar);
	    buttonLayout.setVisibility(View.VISIBLE);
	    
        Button ok = (Button) mRootLayout.findViewById(R.id.btn_ok);
        ok.setOnClickListener(new View.OnClickListener() {

            @SuppressWarnings({ "rawtypes", "unchecked" })
			@Override
            public void onClick(View view) {
            	Serializable result = null;
                if (mCurrentMode == OperationMode.SINGLE_SELECT) {
                	result = mCurrentItem;
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
	
	// 加载数据
	public void loadData() {
		
		// 延时显示进度对话框
		sendEmptyMessageDelayed(SHOW_PROGRESS_DIALOG, DELAYTIME_FOR_SHOW);
		
		// 加载列表数据
		mServiceImplement.getListData();
	}
}
