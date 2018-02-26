package com.pm360.cepm360.app.common.view.parent.list;

import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Color;
import android.text.Spannable;
import android.text.SpannableStringBuilder;
import android.text.TextUtils.TruncateAt;
import android.text.style.ForegroundColorSpan;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.ImageView;
import android.widget.LinearLayout.LayoutParams;
import android.widget.ListView;
import android.widget.TextView;

import com.pm360.cepm360.R;
import com.pm360.cepm360.app.common.activity.DataManagerInterface;
import com.pm360.cepm360.app.common.adpater.DataTreeListAdapter;
import com.pm360.cepm360.app.common.adpater.DataTreeListAdapter.TreeListAdapterInterface;
import com.pm360.cepm360.app.common.adpater.DataTreeListAdapter.ViewHolder;
import com.pm360.cepm360.app.common.custinterface.SimpleListInterface;
import com.pm360.cepm360.app.common.custinterface.SimpleServiceInterface;
import com.pm360.cepm360.common.ResultStatus;
import com.pm360.cepm360.common.json.AnalysisManager;
import com.pm360.cepm360.common.util.MiscUtils;
import com.pm360.cepm360.entity.Expandable;

import java.io.Serializable;
import java.util.List;
import java.util.Map;

public class SimpleTreeForDir<T extends Expandable & Serializable> 
										extends BaseListCommon<T> {
	/**------------------ 视图控件及适配器 -----------------*/
	protected View mRootLayout;		// 本视图的根布局
	protected LayoutInflater mInflater;
	
	protected View mListHeader;		// 列表头视图
	protected ListView mListView;	// 列表视图
	
	private TextView mTreeTitle;
	private ImageView mTreeExpand;
	
	protected DataTreeListAdapter<T> mListAdapter;	// 适配器adapter定义
	
	/**------------------ 保存资源数据 -------------*/
	protected int[] mListItemIds;
	protected String[] mDisplayFields;	// 显示域数组
	private String mTitle;
	
	// 定位对象
	protected T mSpecifiedItem;
	
	/** ------------------ 接口 ----------------- */
	protected SimpleListInterface<T> mListImplement;
	protected SimpleServiceInterface<T> mServiceImplement;
	
	private SimpleTreeForDirInterface mSimpleTreeForDirImpl;
	
	/**
	 * 构造函数
	 */
	public SimpleTreeForDir(Context context, String title, 
						SimpleTreeForDirInterface treeForDirInterface) {
		super(context);
		mTitle = title;
		mSimpleTreeForDirImpl = treeForDirInterface;
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
	 * 获取树适配器
	 * @return
	 */
	@Override
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
	 * 服务返回结果处理
	 */
	private DataManagerInterface mManager = new DataManagerInterface() {

		@Override
		public void getDataOnResult(ResultStatus status, List<?> list) {
			handleDataOnResult(status, list);
		}
	};
	
	/**
	 * 设置指定选中项
	 * @param specifiedItem
	 */
	public void setSpecifiedItem(T specifiedItem) {
		mSpecifiedItem = specifiedItem;
	}
	
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
				if (mSpecifiedItem != null) {
					// TODO
					mSpecifiedItem = null;
				}
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
	}
	
	/**
	 * 配置树视图基本信息
	 */
	protected void initTreeHeader() {
		
		// 获取树列表分类标题控件，标题的名称在实现类中设置
		mTreeTitle = (TextView) mRootLayout.findViewById(R.id.dir_title);
		mTreeTitle.setText(mTitle);
		
		// 获取扩展树列表控件
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
	 * 初始化树列表
	 */
	protected void initTreeList() {
		
		// 获取树列表项的控件ID并初始化
		TypedArray typedArray = mContext.getResources()
							.obtainTypedArray(R.array.tree_item_ids);
		mListItemIds = new int[typedArray.length()];
		for (int i = 0; i < mListItemIds.length; i++) {
			mListItemIds[i] = typedArray.getResourceId(i, 0);
		}
		
		typedArray.recycle();
		
		// 获取树视图对象
		mListView = (ListView) mRootLayout.findViewById(R.id.tree_listview);
		
		// 创建树视图适配器，并将其绑定到树视图列表上
		mListAdapter = new DataTreeListAdapter<T>(mContext, true, mListAdapterImplement);
		mListView.setAdapter(mListAdapter);
	}
	
	/**
	 * 实现列表适配器接口
	 */
	private TreeListAdapterInterface mListAdapterImplement 
										= new TreeListAdapterInterface() {

		@Override
		public int getLayoutId() {
			return R.layout.treeview_list_item;
		}

		@Override
		public void initListViewItem(ViewHolder viewHolder, int position) {
			
			// 获取position的数据项，并转化为map
			T treeNode = mListAdapter.getItem(position);
			
			// 建立域名称和域值的映射
			Map<String, String> treeNodeMap = MiscUtils.beanToMap(treeNode);
			
			// 如果有子类型，显示为文件夹图标，否则显示文件图标
			if (treeNode.isHas_child()) {
				viewHolder.ivs[1].setImageResource(R.drawable.folder2);
			} else {
				viewHolder.ivs[1].setImageResource(R.drawable.file_icon_default);
			}
			
			if (treeNode.getCount() == 0) {
				viewHolder.tvs[0].setText(treeNodeMap.get(mDisplayFields[0]));
				if (mSimpleTreeForDirImpl.isBoldText(treeNode)) {
					viewHolder.tvs[0].getPaint().setFakeBoldText(true);	
				}
			} else {
				String text = treeNodeMap.get(mDisplayFields[0]) 
						+ " (" + treeNode.getCount() + ")";
				
				int startIndex = text.indexOf("(");
				SpannableStringBuilder builder = new SpannableStringBuilder(text);
				ForegroundColorSpan blueSpan = new ForegroundColorSpan(Color.BLUE);
				builder.setSpan(blueSpan, startIndex - 1, 
						text.length(), Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
				viewHolder.tvs[0].setText(builder);
				if (mSimpleTreeForDirImpl.isBoldText(treeNode)) {
					viewHolder.tvs[0].getPaint().setFakeBoldText(true);	
				}
			}
			
			viewHolder.tvs[0].setEllipsize(TruncateAt.MARQUEE);
		}

		@Override
		public void regesterListeners(ViewHolder viewHolder, final int position) {
			clickListener(viewHolder, position);
		}

		@Override
		public void initLayout(View convertView, ViewHolder holder) {
			holder.ivs = new ImageView[2];
			holder.tvs = new TextView[1];
			
			int index = 0;
			// 初始化ImageView
			for (int i = 0; i < holder.ivs.length; i++) {
				holder.ivs[i] = (ImageView) convertView
								.findViewById(mListItemIds[index++]);
			}
			// 初始化TextView
			for (int i = 0; i < holder.tvs.length; i++) {
				holder.tvs[i] = (TextView) convertView
								.findViewById(mListItemIds[index++]);
			}			
		}

		@Override
		public void calculateContentItemCount() {
			mSimpleTreeForDirImpl.calcListItemCount();
		}
	};
	
	/**
	 * 长按处理
	 * @param view
	 * @param position
	 */
	private boolean handleLongClick(View view, int position) {
		mSimpleTreeForDirImpl.handleClick(position, view);
		return true;
	}
	
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
					mSimpleTreeForDirImpl.handleClick(position, view);
				}
			});
			
			viewHolder.ivs[i].setOnLongClickListener(new View.OnLongClickListener() {
				
				@Override
				public boolean onLongClick(View view) {
					return handleLongClick(view, position);
				}
			});
		}
		
		viewHolder.tvs[0].setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View view) {
				mSimpleTreeForDirImpl.handleClick(position, view);
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
	 * @param view
	 */
	protected void handleClick(int position, View view) {
		mCurrentItem = mListAdapter.getItem(position);
		mListAdapter.setSelected(position, true);
		
		if (view != null) {
			mListAdapter.updateListView(position);
		}
	}
	
	// 加载数据
	public void loadData() {
		
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
	public interface SimpleTreeForDirInterface {
		
		/**
		 * 单击处理
		 */
		public void handleClick(int position, View view);
		
		/**
		 * 计算树节点下的右侧列表项数量
		 */
		public void calcListItemCount();
		
		/**
		 * 列表是否需要加粗显示
		 * @return
		 */
		public <E extends Expandable & Serializable> boolean isBoldText(E t);
	}
}
