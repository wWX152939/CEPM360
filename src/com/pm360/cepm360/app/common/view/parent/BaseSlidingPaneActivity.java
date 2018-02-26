package com.pm360.cepm360.app.common.view.parent;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.content.res.TypedArray;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.view.View;
import android.view.WindowManager;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.ListView;

import com.pm360.cepm360.R;
import com.pm360.cepm360.app.cache.PermissionCache;
import com.pm360.cepm360.app.common.activity.ActionBarFragmentActivity;
import com.pm360.cepm360.app.common.activity.ListSelectActivity;
import com.pm360.cepm360.app.common.adpater.NavigationAdapter;
import com.pm360.cepm360.app.common.view.BaseSlidingPaneLayout;
import com.pm360.cepm360.common.GLOBAL;
import com.pm360.cepm360.common.util.DateUtils;
import com.pm360.cepm360.entity.Message;

import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@SuppressLint("UseSparseArrays") 
public abstract class BaseSlidingPaneActivity extends ActionBarFragmentActivity {
	
	/**-------------------------- 常量定义 ----------------------*/
	private static final int BASE_POSITION = 0;
	
	public static final String BUNDLE_DATA = "bundle_data";
	
	/**------------------------- 视图控件 ------------------------*/
	public BaseSlidingPaneLayout mSlidingPane;
	private ListView mNavigationListView;
	private NavigationAdapter mNavigationAdapter;
	
	// 模块名称
    private BaseSearchView mBaseSearchView;
	
	/**-------------------------- fragment变量管理 ------------------*/
	private FragmentManager mFragmentManager;
	// 保存Activity管理的fragment数组
	private Class<? extends Fragment>[] mTypeClass;
	private Class<?>[] mManagerObjectClasses;
	
	// 当前位置和标题、Fragment
	private String mCurrentTitle;
	protected int mCurrentPosition;
	protected Fragment mCurrentFragment;
	protected Bundle mBundleData;
	protected Message mMessage;
	
	/**------------------------- fragment搜索数据---------------------*/
	// 使能搜索功能
	private boolean[] mEnableSearches;
	private boolean mEnableMenu;
	
	// 搜索Lables的资源ID
	private int[] mSearchLablesId;
	
	// 各fragment的搜索lables
	private String[][] mSearchLables;
	
	// 搜索域
	private String[][] mSearchFields;
	
	// 列表图标和标题资源
	private String[] mItems;
	private int[] mIcons;
	
	/**--------------------------- 保存数据的集合变量定义 ------------------*/
	// 初始化搜索布局数据
	private List<Map<Integer, Integer>> mDialogStyleMapList;
	private List<Map<Integer, String[]>> mDialogStyleDataMapList;
	private List<List<Integer>> mRelevanceList;
	
	// 所有fragment搜索功能的域和Lable的映射
	private List<Map<String, String>> mFieldLableList;
	
	// 保存类型转换域的映射表
	private List<Map<String, Map<String, String>>> mSpecifiedFieldsMapList;

	// 记录没有权限的界面
	private List<Integer> mNoPermissionList = new ArrayList<Integer>();
	
	/** -------------------------- 实现的接口 --------------------------*/
	// 搜索功能接口实现
	SearchInterface mSearchImplement;
	
	// Activity管理Fragment的实现
	FragmentManagerInterface mFragmentManagerImplement;
	
	//消息进入管理类
	MessgeManagerInterface mMessgeManagerInterface;
	
	/**
	 * 初始化本地接口数据
	 * @param fragmentManagerInterface
	 * @param enableMenu
	 */
	protected void init(FragmentManagerInterface fragmentManagerInterface,
						boolean enableMenu) {
		mFragmentManagerImplement = fragmentManagerInterface;
		mEnableMenu = enableMenu;
	}
	
	/**
	 * 初始化本地接口数据
	 * @param fragmentManagerInterface
	 * @param messgeManagerInterface
	 * @param enableMenu
	 */
	protected void init(FragmentManagerInterface fragmentManagerInterface,
						MessgeManagerInterface messgeManagerInterface,
						boolean enableMenu) {
		init(fragmentManagerInterface, enableMenu);
		mMessgeManagerInterface = messgeManagerInterface;
	}
	
	/**
	 * 初始化本地接口数据
	 * @param fragmentManagerInterface
	 * @param searchInterface
	 * @param enableMenu
	 */
	protected void init(FragmentManagerInterface fragmentManagerInterface,
						SearchInterface searchInterface,
						boolean enableMenu) {
		init(fragmentManagerInterface, searchInterface, null, enableMenu);
	}
	
	/**
	 * 初始化本地接口数据
	 * @param fragmentManagerInterface
	 * @param searchInterface
	 * @param messgeManagerInterface
	 * @param enableMenu
	 */
	protected void init(FragmentManagerInterface fragmentManagerInterface,
						SearchInterface searchInterface,
						MessgeManagerInterface messgeManagerInterface,
						boolean enableMenu) {
		init(fragmentManagerInterface, messgeManagerInterface, enableMenu);
		mSearchImplement = searchInterface;
		mSearchLablesId = searchInterface.getSearchLablesId();
		mEnableSearches = new boolean[mSearchLablesId.length];
		for (int i = 0; 
			 i < mSearchLablesId.length; 
			 i++) {
			if (mSearchLablesId[i] != 0) {
				mEnableSearches[i] = true;
			}
		}
	}
	
	public View getSlidingPaneLayout() {
		return mSlidingPane;
	}
	
	protected boolean getBlackBackgroudColor() {
		return true;
	}
	
	/**
	 * 继承该方法，设置每一个子View的查看,编辑权限， 查看在前，编辑在后
	 * @return
	 */
	protected List<String> getPermission() {
		return null;
	}
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		
		// 权限控制
		List<String> permissionList = getPermission();
		if (permissionList != null) {
			for (int i = 0; i < permissionList.size(); i += 2) {
				if (!PermissionCache.hasSysPermission(permissionList.get(i))
						&& !PermissionCache.hasSysPermission(permissionList.get(i + 1))) {
					mNoPermissionList.add(i/2);
				}
			}
		}
		
		// 初始化Activity
		initActivity();
	}
	
	/**
	 * 初始化Activity
	 */
	private void initActivity() {
		// 读取intent信息
		boolean normalStart = true;
		int normalStartIndex = 0;
		if (mMessgeManagerInterface != null) {
			normalStart = mMessgeManagerInterface.getIntentInfo();
		} else {
			normalStartIndex = getIntentInfo();
		}
		
		// 设置视图布局
		setContentView(R.layout.inventory_sliding_layout);
		
		// 全屏模式
		getWindow().setFlags( WindowManager.LayoutParams.FLAG_FULLSCREEN,
							  WindowManager.LayoutParams.FLAG_FULLSCREEN);
		// 是否开启菜单
		enableMenuView(mEnableMenu);
		
		// 获取Fragment管理器
		mFragmentManager = getSupportFragmentManager();
		
		// 初始化slidepane相关操作
		initScheduleWindow();
		
		// 建立标签和域映射
		buildSearchFieldLableMap();
		
		if (normalStart) {
			// 正常启动，默认启动第一个fragment
			switchContent(BASE_POSITION + normalStartIndex);
		}
	}
	
	/**
	 * 读取intent信息
	 * @return 位置index
	 */
	protected int getIntentInfo() {
		Intent intent = getIntent();
		mBundleData = intent.getBundleExtra(BUNDLE_DATA);
		return 0;
	}
	
	/**
	 * 初始化SlidePane相关
	 */
	@SuppressWarnings("unchecked")
	private void initScheduleWindow() {
		mTypeClass = mFragmentManagerImplement.getManagerFragments();
        mManagerObjectClasses = mFragmentManagerImplement.getSearchObjectClasses();
        
		// 获取滑板控件，默认打开
		mSlidingPane = (BaseSlidingPaneLayout) findViewById(R.id.sliding_pane);
		mSlidingPane.openPane();

        // 获取导航项的标题和图标资源
        mItems = getResources().getStringArray(
        				mFragmentManagerImplement.getNavigationTitleNamesId());
        TypedArray typedArray = getResources().obtainTypedArray(
						mFragmentManagerImplement.getNavigationIconsId());
        mIcons = new int[typedArray.length()];
        for (int i = 0; i < typedArray.length(); i++) {
            mIcons[i] = typedArray.getResourceId(i, 0);
        }
        typedArray.recycle();
        
        // 设置导航列表
        mNavigationListView = (ListView) findViewById(R.id.navigation_listView);
        
        int size = mItems.length - mNoPermissionList.size();
        String[] items;
    	int[] icons;
    	Class<? extends Fragment>[] typeClass;
    	items = new String[size];
    	icons = new int[size * 2];
    	typeClass = new Class[size];
    	for (int i = 0, j = 0; i < mItems.length; i++) {
    		if (!mNoPermissionList.contains(i)) {
    			items[j] = mItems[i];
    			j++;
    		}
    	}
    	for (int i = 0, j = 0; i < mItems.length; i++) {
    		if (!mNoPermissionList.contains(i)) {
    			icons[j] = mIcons[i];
    			icons[j + size] = mIcons[i + mItems.length];
    			j++;
    		}
    	}
    	for (int i = 0, j = 0; i < mItems.length; i++) {
    		if (!mNoPermissionList.contains(i)) {
    			typeClass[j] = mTypeClass[i];
    			j++;
    		}
    	}
    	mItems = items;
    	mIcons = icons;
    	mTypeClass = typeClass;
        mNavigationAdapter = new NavigationAdapter(this, mIcons, mItems);
        mNavigationListView.setAdapter(mNavigationAdapter);
        mNavigationListView.setOnItemClickListener(new OnItemClickListener() 
        		{
		            @Override
		            public void onItemClick(AdapterView<?> parent, 
		            						View view,
		                    				int position, 
		                    				long id) {
		            	onClickSwitchContent(position);
	            }
        });
	}
	
	/**
	 * 建立标签和域映射
	 */
	private void buildSearchFieldLableMap() {
		// 获取搜索标签和域
		if (mSearchImplement != null) {
			
			// 搜索框的域和标签映射列表
			mFieldLableList = new ArrayList<Map<String,String>>();
		
			mSearchFields = mSearchImplement.getSearchFields();
			mSearchLables = new String[mEnableSearches.length][];
			for (int i = 0; i < mEnableSearches.length; i++) {
				if (mEnableSearches[i] && mSearchLablesId[i] != 0) {
					mSearchLables[i] = getResources().getStringArray(mSearchLablesId[i]);
				}
			}
			
			// 建立域和标签映射
			for (int i = 0; i < mEnableSearches.length; i++) {
				if (mEnableSearches[i] 
						&& mSearchFields[i] != null
						&& mSearchLables[i] != null) {
					Map<String, String> fieldToLablesMap = new HashMap<String, String>();
					for (int j = 0; j < mSearchFields[i].length; j++) {
						fieldToLablesMap.put(mSearchFields[i][j], mSearchLables[i][j]);
					}
					mFieldLableList.add(fieldToLablesMap);
				}
			}
			
			mSpecifiedFieldsMapList = mSearchImplement.getSpecifiedFieldsList();
        	mDialogStyleMapList = mSearchImplement.getSearchStyles();
    		mDialogStyleDataMapList = mSearchImplement.getSearchSupplyData();
    		mRelevanceList = mSearchImplement.getRelevanceList();
		}

	}
	
	/**
	 * 切换content，对于权限模块，做转换
	 * @param position
	 */
	public void switchContent(int position) {
		if (!mNoPermissionList.isEmpty()) {
			int count = 0;
			for (int i = 0; i < mNoPermissionList.size(); i++) {
				if (mNoPermissionList.get(i) < position) {
					count++;
				} else {
					break;
				}
			}
			position -= count;
		}
		onClickSwitchContent(position);
	}

	/**
	 * 切换Fragment
	 * @param position
	 */
	private void onClickSwitchContent(int position) {
        String title = mNavigationAdapter.getItem(position);
		if (title.equals(mCurrentTitle)) {
			return;
		}
        mNavigationAdapter.setSelected(position);
		
		FragmentTransaction fragmentTransaction = mFragmentManager
				.beginTransaction();

		fragmentTransaction.setCustomAnimations(R.anim.slide_in_left,
				R.anim.slide_out_left, R.anim.slide_in_right,
				R.anim.slide_out_right);
		
		if (mBundleData == null) {
			mBundleData = new Bundle();
		}

		mBundleData.putBoolean(ListSelectActivity.IS_BLACK_BACKGROUD_COLOR, getBlackBackgroudColor());
		mBundleData.putString("tag", title);
		if (mMessage != null) {
			mBundleData.putSerializable(GLOBAL.MSG_OBJECT_KEY, mMessage);
		}

		Fragment showFragment = mFragmentManager.findFragmentByTag(title);
		if (showFragment == null) {
			if (position == -1) { // 这里是只为文档管理-文档搜索而做
				Class<? extends Fragment>[] typeClass = mFragmentManagerImplement
						.getManagerFragments();
				position = typeClass.length - 1;
				showFragment = Fragment.instantiate(this,
						typeClass[position].getCanonicalName(), mBundleData);
			} else {
				showFragment = Fragment.instantiate(this,
						mTypeClass[position].getCanonicalName(), mBundleData);
			}
		}
		if (mCurrentTitle != null) {
			Fragment hideFragment = mFragmentManager
					.findFragmentByTag(mCurrentTitle);
			if (hideFragment != null)
				fragmentTransaction.hide(hideFragment);
		}

		if (showFragment.isAdded()) {
			fragmentTransaction.show(showFragment);
		} else {
			fragmentTransaction.add(R.id.content_frame, showFragment, title);
		}
		fragmentTransaction.commitAllowingStateLoss();
		mCurrentTitle = title;
		mCurrentFragment = showFragment;
		mCurrentPosition = position;
		
		if (mEnableSearches != null) {
			
			// 初始化查询视图
			if (mEnableSearches[position]) {
				enableSearchView(true);
				initSearchConditionView();
			} else {
				enableSearchView(false);
			}
		}
	}
	
	/**
	 * 初始化查询条件视图
	 */
	private void initSearchConditionView() {
		// 删除老的视图
		if (mBaseSearchView != null) {
			removeSearchConditionView(mBaseSearchView.getView());
		}
		
		mBaseSearchView = new BaseSearchView(this);
		mBaseSearchView.init(mSearchLablesId[mCurrentPosition], 
							 mDialogStyleMapList.get(mCurrentPosition), 
							 mDialogStyleDataMapList.get(mCurrentPosition), 
							 mRelevanceList.get(mCurrentPosition));
		addSearchConditionView(mBaseSearchView.getView());
		
		// 设置检索和清空操作
		setSearchListener(new SearchListener() {
			
			@Override
			public void doSearch() {
				startSearch();
			}
			
			@Override
			public void doReset() {
				resetSearch();
			}
		});
		
	}
	
	/**
	 * 开始检索
	 * @param keyWord
	 */
	@SuppressLint("NewApi") 
	private void startSearch() {
		Map<String, String> saveDataMap = mBaseSearchView.SaveData();
		Class<?> typeClass = mManagerObjectClasses[mCurrentPosition];
		
		Object target = null;
		try {
			target = typeClass.newInstance();
		} catch (InstantiationException | IllegalAccessException e1) {
			e1.printStackTrace();
		}
		Map<String, String> lableMap = mFieldLableList.get(mCurrentPosition);
		
		Field[] fs = typeClass.getDeclaredFields();
		for (Field field : fs) {
			field.setAccessible(true);
			if (lableMap.containsKey(field.getName())) {
				Class<?> type = field.getType();
				String value = saveDataMap.get(lableMap.get(field.getName()));
				Map<String, Map<String, String>> specifiedFieldsMap 
										= mSpecifiedFieldsMapList.get(mCurrentPosition);
				if (specifiedFieldsMap != null
						&& specifiedFieldsMap.containsKey(field.getName())
						&& specifiedFieldsMap.get(field.getName()).containsKey(value)) {
					value = specifiedFieldsMap.get(field.getName()).get(value);
				}
				
				try {
					if (type.equals(String.class)) {
						if (!value.equals("")) {
							field.set(target, value);
						}
					} else if (type.equals(Date.class)) {
						if (!value.equals("")) {
							field.set(target, DateUtils.stringToDate(DateUtils.FORMAT_LONG, value));
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
		
		// 开始请求检索服务
		mSearchImplement.doSearch(target);
	}

	/**
	 * 清空检索条件
	 */
	private void resetSearch() {
		mBaseSearchView.SetDefaultValue(null);
	}
	
	public static interface MessgeManagerInterface {
		/**
		 * 读取intent信息
		 * @return true 正常模式， false 消息模式
		 */
		boolean getIntentInfo();
	}
	
	public static interface FragmentManagerInterface {
		/**
		 * 获取管理的fragment
		 * @return
		 */
		Class<? extends Fragment>[] getManagerFragments();
		
		/**
		 * 获取搜索对象的类型
		 * @return
		 */
		Class<?>[] getSearchObjectClasses();
		
		/**
		 * 获取Home标题名
		 * @return
		 */
		String getHomeTitleName();
		
		/**
		 * 获取导航列表标题名资源ID
		 * @return
		 */
		int getNavigationTitleNamesId();
		
		/**
		 * 获取导航列表图标资源ID
		 * @return
		 */
		int getNavigationIconsId();
	}
	
	public static interface SearchInterface {
		/**
		 * 获取所有fragment中搜索布局Lables的资源数组ID
		 * @return
		 */
		int[] getSearchLablesId();
		
		/**
		 * 获取所有fragment中搜索布局Lables对应的搜索域
		 * @return
		 */
		String[][] getSearchFields();
		
		/**
		 * 获取对话框中每项的风格
		 * @return
		 */
		List<Map<Integer, Integer>> getSearchStyles();
		
		/**
		 * 获取特定风格的可选数据
		 * @return
		 */
		List<Map<Integer, String[]>> getSearchSupplyData();
		
		/**
		 * 获取关联行号
		 * @return
		 */
		List<List<Integer>> getRelevanceList();
		
		/**
		 * 获取特殊域的类型转换映射表
		 * @return
		 */
		List<Map<String, Map<String, String>>> getSpecifiedFieldsList();
		
		/**
		 * 查询操作
		 * @param searchCondition
		 */
		void doSearch(Object searchCondition);
	}
}
