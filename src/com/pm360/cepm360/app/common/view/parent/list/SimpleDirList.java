package com.pm360.cepm360.app.common.view.parent.list;

import android.content.Context;
import android.content.res.TypedArray;
import android.text.TextUtils.TruncateAt;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.ImageView;
import android.widget.LinearLayout.LayoutParams;
import android.widget.ListView;
import android.widget.TextView;

import com.pm360.cepm360.R;
import com.pm360.cepm360.app.common.activity.DataManagerInterface;
import com.pm360.cepm360.app.common.adpater.DataListAdapter;
import com.pm360.cepm360.app.common.adpater.DataListAdapter.ListAdapterInterface;
import com.pm360.cepm360.app.common.adpater.DataListAdapter.ViewHolder;
import com.pm360.cepm360.app.common.custinterface.SimpleListInterface;
import com.pm360.cepm360.app.common.custinterface.SimpleServiceInterface;
import com.pm360.cepm360.app.utils.UtilTools;
import com.pm360.cepm360.common.ResultStatus;
import com.pm360.cepm360.common.json.AnalysisManager;
import com.pm360.cepm360.common.util.MiscUtils;

import java.io.Serializable;
import java.util.List;
import java.util.Map;

public class SimpleDirList<T extends Serializable> 
										extends BaseListCommon<T> {
	/**------------------ 视图控件及适配器 -----------------*/
	protected View mRootLayout;		// 本视图的根布局
	protected LayoutInflater mInflater;
	
	protected View mListHeader;		// 列表头视图
	protected ListView mListView;	// 列表视图
	
	private TextView mTreeTitle;
	private ImageView mTreeExpand;
	
	// 适配器adapter定义
	protected DataListAdapter<T> mListAdapter;	
	
	/**------------------ 保存资源数据 -------------*/
	protected int[] mListItemIds;
	protected String[] mDisplayFields;	// 显示域数组
	private String mTitle;
	
	/** ------------------ 接口 ----------------- */
	protected SimpleListInterface<T> mListImplement;
	protected SimpleServiceInterface<T> mServiceImplement;
	
	private SimpleDirInterface<T> mSimpleDirImpl;
	
	/**
	 * 构造函数
	 */
	public SimpleDirList(Context context, String title, 
						SimpleDirInterface<T> dirInterface) {
		super(context);
		mTitle = title;
		mSimpleDirImpl = dirInterface;
	}

	/**
	 * 只有现实功能的初始化
	 * @param context
	 * @param listItemClass
	 * @param listInterface
	 * @param serviceInterface
	 */
	public void init(Class<T> listItemClass,
					SimpleListInterface<T> listInterface,
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
	 * 获取目录适配器
	 * @return
	 */
	@Override
	public DataListAdapter<T> getListAdapter() {
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
				mListAdapter.setShowDataList((List<T>) list);
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
	}
	
	/**
	 * 初始化视图
	 */
	private void initLayout() {
		
		// 生成根布局视图
		if (mLocationImpl == null) {
			mInflater = (LayoutInflater)
					mContext.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
			
			mRootLayout = mInflater.inflate(R.layout.tree_for_dir_layout, null);
		} else {
			mRootLayout = mLocationImpl.getRootView()
					.findViewById(mLocationImpl.getLocationId());
		}
		
		// 初始化目录列表视图
		initDirView();
	}
	
	/**
	 * 初始化目录视图
	 */
	protected void initDirView() {
		
		// 初始化目录视图列表头
		initDirHeader();
		
		// 为目录列表设置适配器
		initDirList();
	}
	
	/**
	 * 配置目录视图基本信息
	 */
	protected void initDirHeader() {
		
		// 获取目录列表分类标题控件，标题的名称在实现类中设置
		mTreeTitle = (TextView) mRootLayout.findViewById(R.id.dir_title);
		mTreeTitle.setText(mTitle);
		
		// 获取扩展目录列表控件
		mTreeExpand = (ImageView) mRootLayout.findViewById(R.id.expand_icon);
		mTreeExpand.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View view) {
				LayoutParams params = (LayoutParams) mRootLayout.getLayoutParams();
				if (params.weight == 3) { // 未展开
					params.weight = 2;
					mTreeExpand.setImageResource(R.drawable.arrow_double_left);
				} else { // 已展开
					params.weight = 3;
					mTreeExpand.setImageResource(R.drawable.arrow_double_right);
				}
				mRootLayout.setLayoutParams(params);
			}
		});
	}

	/**
	 * 初始化目录列表
	 */
	protected void initDirList() {
		
		// 获取目录列表项的控件ID并初始化
		TypedArray typedArray = mContext.getResources()
							.obtainTypedArray(R.array.dir_item_ids);
		mListItemIds = new int[typedArray.length()];
		for (int i = 0; i < mListItemIds.length; i++) {
			mListItemIds[i] = typedArray.getResourceId(i, 0);
		}
		
		typedArray.recycle();
		
		// 获取目录视图对象
		mListView = (ListView) mRootLayout.findViewById(R.id.tree_listview);
		
		// 创建目录视图适配器，并将其绑定到目录视图列表上
		mListAdapter = new DataListAdapter<T>(mContext, true, mListAdapterImplement);
		mListView.setAdapter(mListAdapter);
	}
	
	/**
	 * 列表适配器接口实现
	 */
	private ListAdapterInterface<T> mListAdapterImplement = new ListAdapterInterface<T>() {

		@Override
		public int getLayoutId() {
			return R.layout.dir_list_item;
		}

		@Override
		public View getHeaderView() {
			return null;
		}

		@Override
		public void regesterListeners(ViewHolder viewHolder, int position) {
			clickListener(viewHolder, position);
			
		}

		@Override
		public void initListViewItem(View convertView, ViewHolder holder,
				DataListAdapter<T> adapter, int position) {

			// 获取position的数据项，并转化为map
			T treeNode = mListAdapter.getItem(position);
			
			// 建立域名称和域值的映射
			Map<String, String> treeNodeMap = MiscUtils.beanToMap(treeNode);
			
			// 去掉了计数显示
			holder.tvs[0].setPadding(UtilTools.dp2pxW(mContext, 6), 0, 0, 0);
			holder.tvs[0].setText(treeNodeMap.get(mDisplayFields[0]));
			holder.tvs[0].setEllipsize(TruncateAt.MARQUEE);
		}

		@Override
		public void initLayout(View convertView, ViewHolder holder) {
			holder.tvs = new TextView[1];
			
			// 初始化TextView
			holder.tvs[0] = (TextView) convertView
								.findViewById(mListItemIds[0]);
		}

		@Override
		public List<T> findByCondition(Object... condition) {
			return null;
		}

		@Override
		public boolean isSameObject(T t1, T t2) {
			return mSimpleDirImpl.getListItemId(t1) 
							== mSimpleDirImpl.getListItemId(t2);
		}
	};
	
	/**
	 * 长按处理
	 * @param view
	 * @param position
	 */
	private boolean handleLongClick(View view, int position) {
		mSimpleDirImpl.handleClick(position, view);
		return true;
	}
	
	/**
	 * 单击、长按监听处理，可被子类重写
	 * @param viewHolder
	 * @param position
	 */
	protected void clickListener(final ViewHolder viewHolder, final int position) {
		viewHolder.tvs[0].setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View view) {
				mSimpleDirImpl.handleClick(position, view);
			}
		});
		
		viewHolder.tvs[0].setOnLongClickListener(new View.OnLongClickListener() {
			
			@Override
			public boolean onLongClick(View view) {
				return handleLongClick(view, position);
			}
		});
	}
	
	/**
	 * 单击处理
	 * @param position
	 */
	protected void handleClick(int position) {
		mCurrentItem = mListAdapter.getItem(position);
		mListAdapter.setSelected(position, true);
	}
	
	// 加载数据
	protected void loadData() {
		
		// 延时显示进度对话框
		sendEmptyMessageDelayed(SHOW_PROGRESS_DIALOG, DELAYTIME_FOR_SHOW);
		
		// 加载列表数据
		mServiceImplement.getListData();
	}
	
	/**
	 * 本类使用的接口
	 * @author yuanlu
	 *
	 */
	public interface SimpleDirInterface<T> {
		
		/**
		 * 单击处理
		 */
		public void handleClick(int position, View view);
		
		/**
		 * 获取列表项的ID，用于比较两个列表项对象是否为同一个
		 * @return
		 */
		public int getListItemId(T t);
	}
}
