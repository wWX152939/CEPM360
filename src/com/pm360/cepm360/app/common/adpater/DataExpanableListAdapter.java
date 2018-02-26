package com.pm360.cepm360.app.common.adpater;

import android.annotation.SuppressLint;
import android.content.Context;
import android.graphics.Color;
import android.os.Handler;
import android.os.Message;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AutoCompleteTextView;
import android.widget.BaseExpandableListAdapter;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.ExpandableListView;
import android.widget.ExpandableListView.OnGroupClickListener;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.Spinner;
import android.widget.TextView;

import com.pm360.cepm360.R;
import com.pm360.cepm360.app.common.view.CHScrollView;
import com.pm360.cepm360.app.common.view.DatePickText;
import com.pm360.cepm360.common.util.LogUtil;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

@SuppressLint("HandlerLeak") public class DataExpanableListAdapter<C> extends BaseExpandableListAdapter implements
	CHScrollView.OnScrollChagnedListener{
    private Context mContext;
    
    // 重要的接口，用于adapter向adapterview主体类传递数据
    private ChildListAdapterInterface<C> mChildManager;
    private ParentListAdapterInterface mParentManager;
    
    // 保存所有的父子对象
    private List<String> mParentList = new ArrayList<String>();
    private Map<String, List<C>> mChildMap = new HashMap<String, List<C>>();
    private List<Object> mShowList = new ArrayList<Object>();
    private Map<String, List<C>> mAllMap = new HashMap<String, List<C>>();
    
    // 保存选中的列表项索引
    private List<Integer> mSelectedPositionList = new ArrayList<Integer>();
    
    // 带有左右滑动的列表
    private List<CHScrollView> mHScrollViews = new ArrayList<CHScrollView>();
    
    // 是否可滑动
    private boolean mIsScroll = true;
    
    // expandableListView
    private ExpandableListView mExpandableListView;

    /**
     * 构造函数
     * 
     * @param context
     * @param manager
     */
    public DataExpanableListAdapter(Context context, ParentListAdapterInterface parentManager, ChildListAdapterInterface<C> childManager,
    		ExpandableListView expandableListView) {
        this(context, true, parentManager, childManager, expandableListView);
    }
    
    /**
     * 构造函数
     * 
     * @param context
     * @param manager
     */
    public DataExpanableListAdapter(Context context, boolean isScroll, 
    		ParentListAdapterInterface parentManager, ChildListAdapterInterface<C> childManager, ExpandableListView expandableListView) {
        mContext = context;
        mParentManager = parentManager;
        mChildManager = childManager;
        mIsScroll = isScroll;
        mExpandableListView = expandableListView;
        mExpandableListView.setOnGroupClickListener(new OnGroupClickListener() {
			
			@Override
			public boolean onGroupClick(ExpandableListView parent, View v,
					int groupPosition, long id) {
				return true;
			}
		});
        
        if (mIsScroll = isScroll) {
        	addCHScrollView(null, mChildManager.getChildHeaderView());
        }
    }

    /**
     * 子线程通知最线程刷新界面的处理函数
     */
    public Handler mHander = new Handler() {
        public void handleMessage(Message msg) {
            notifyDataSetChanged();
        }
    };

    /**
     * 清除数据列表所有数据
     */
    public void clearAll() {
        mParentList.clear();
        mChildMap.clear();
        mShowList.clear();
        mAllMap.clear();
        
        clearSelection();
        mHander.sendEmptyMessage(0);
    }
    
    /**
     * 选中所有文件
     */
    public void setSelectedAll() {
        mSelectedPositionList.clear();
        for (int i = 0; i < mShowList.size(); i++) {
        	if (!String.class.isInstance(mShowList.get(i))) {
        		mSelectedPositionList.add(i);
        	}
        }
        notifyDataSetChanged();
    }

    /**
     * 设置数据列表
     * 
     * @param List
     */
    public void setShowList(Map<String, List<C>> cList) {
    	mParentList.clear();
        mChildMap.clear();
        mShowList.clear();
        Map<String, List<C>> showList = new HashMap<String, List<C>>();
        Iterator<String> it = cList.keySet().iterator();
        while(it.hasNext()){
        	String key = it.next();
        	if (mAllMap.containsKey(key)) {
        		showList.put(key, mAllMap.get(key));
        	}
        }
        
        clearSelection();
        addDataList(showList);
        
    }
    
    /**
     * 设置数据列表
     * 
     * @param List
     */
    public void setDataList(Map<String, List<C>> cList) {
    	mParentList.clear();
        mChildMap.clear();
        mShowList.clear();
        mAllMap.clear();
        clearSelection();

        mAllMap.putAll(cList);
    }
    
    /**
     * 添加数据列表，不带清除的添加
     * 
     * @param List
     */
    public void addDataList(Map<String, List<C>> cList) {
    	Object[] keyArray = cList.keySet().toArray();   
	    Arrays.sort(keyArray);
        if (cList != null) {
        	for (int i = 0; i < keyArray.length; i++) {
        		mParentList.add((String) keyArray[i]);
        	}
        	for (String key : mParentList) {
        		mChildMap.put(key, cList.get(key));
        		mAllMap.put(key, cList.get(key));
        	}
        	for (int i = 0; i < mChildMap.size(); i++) {
        		mShowList.add(mParentList.get(i));
        		for (int j = 0; j < mChildMap.get(mParentList.get(i)).size(); j++) {
                	mShowList.add(mChildMap.get(mParentList.get(i)).get(j));
        		}
        	}
            mHander.sendEmptyMessage(0);

            for (int i = 0; i < getGroupCount(); i++) {
            	mExpandableListView.expandGroup(i);
        	}
        }
    }

    /**
     * 添加数据到数据列表
     * @param c
     */
    public void addData(String parentName, C c) {
    	LogUtil.i("wzw parentName:" + parentName);
    	List<C> cList = mChildMap.get(parentName);
    	if (cList == null || cList.isEmpty()) {
    		cList = new ArrayList<C>();
    		cList.add(c);
    		mChildMap.put(parentName, cList);
    		mAllMap.put(parentName, cList);
    		mParentList.add(parentName);
    		mExpandableListView.expandGroup(mParentList.size() - 1);
    		mShowList.add(parentName);
    		mShowList.add(c);
    	} else {
    		cList.add(c);
        	mChildMap.put(parentName, cList);
    		mAllMap.put(parentName, cList);
    		
    		int i = 0;
        	for (i = 0; i < mParentList.size(); i++) {
        		if (mParentList.get(i).equals(parentName)) {
        			break;
        		}
        	}
        	LogUtil.i("wzw i:" + i + "mParentList:" + Arrays.asList(mParentList));
        	
        	int position = calculateParentPosition(i);
        	
        	// 插入到当前父节点最后
        	mShowList.add(position - 1, c);
    	}
    	
        mHander.sendEmptyMessage(0);
    }

    /**
     * 删除指定的数据对象
     * @param c
     */
    public void deleteData(C c) {
        LogUtil.i("wzw c:" + c);
    	int position = -1;
    	int groupId = -1;
        for (int i = 0; i < mChildMap.size(); i++) {
        	for (int j = 0; j < mChildMap.get(mParentList.get(i)).size(); j++) {
                LogUtil.i("wzw i:" + i + " j:" + j + " map:" + mChildMap.get(mParentList.get(i)).get(j));
        		if (mChildManager.isSameObject(mChildMap.get(mParentList.get(i)).get(j), c)) {
                    LogUtil.i("wzw i: remove");
        			position = calculateCurPosition(i, j);
        			groupId = i;
        			mChildMap.get(mParentList.get(i)).remove(j);
                    break;
                }
        	}
            if (position != -1) {
            	break;
            }
        }
        
        LogUtil.i("wzw position:" + position);
        if (position != -1) {
        	mShowList.remove(position);
        }
        
        if (groupId != -1) {
        	if (mChildMap.get(mParentList.get(groupId)).isEmpty()) {
        		mShowList.remove(position - 1);
        		mParentList.remove(groupId);
        		mChildMap.remove(groupId);
        		mAllMap.remove(groupId);
        	}
        }

        mHander.sendEmptyMessage(0);
    }

    /**
     * 删除列表中的数据对象
     * 
     * @param list
     */
    public void deleteData(List<C> list) {
        for (int i = 0; i < list.size(); i++) {
            C c = list.get(i);
            deleteData(c);
        }
        
        mHander.sendEmptyMessage(0);
    }

    /**
     * 更新数据对象
     * 
     * @param P
     */
    public void updateData(C c) {
    	int position = 0;
        for (int i = 0; i < mChildMap.size(); i++) {
        	for (int j = 0; j < mChildMap.get(mParentList.get(i)).size(); j++) {
        		if (mChildManager.isSameObject(mChildMap.get(mParentList.get(i)).get(j), c)) {
        			position = calculateCurPosition(i, j);
        			mChildMap.get(mParentList.get(i)).set(j, c);
                    break;
                }
        	}
            if (position != 0) {
            	break;
            }
        }

        mShowList.set(position, c);
        mHander.sendEmptyMessage(0);
    }

    /**
     * 列表左右滑动的相应函数
     */
    @Override
    public void onScrollChanged(CHScrollView view, int l, int P, int oldl, int oldt) {
        for (CHScrollView scrollView : mHScrollViews) {
            if (view != scrollView)
                scrollView.smoothScrollTo(l, P);
        }
    }

    /**
     * 为所有的列表项添加滑动
     * @param listView
     * @param view
     */
    public void addCHScrollView(ListView listView, View view) {
        final CHScrollView chScrollView = 
        		(CHScrollView) view.findViewWithTag(CHScrollView.TAG);
          
        if (chScrollView == null)
            return;
        
        chScrollView.setOnScrollChagnedListener(this);

        if (!mHScrollViews.isEmpty()) {
            final int scrollX = mHScrollViews.get(0).getScrollX();
            // 这里完全可以直接调用chScrollView.scrollTo(scrollX, 0);
            listView.post(new Runnable() {
                @Override
                public void run() {
                	// 触发onScrollChanged被调用，注意这里实际上还是在UI线程里面
                    chScrollView.scrollTo(scrollX, 0);
                }
            });
        }
        mHScrollViews.add(chScrollView);     
    }
    
    /**
     * 单选
     * 
     * @param position
     * @param isSelected
     */
    public void setSelected(int position, boolean isSelected) {
        mSelectedPositionList.clear();
        if (isSelected) {
            mSelectedPositionList.add((Integer) position);
        }
        notifyDataSetChanged();
    }

    /**
     * 多选
     * 
     * @param position
     */
    public void setPickSelected(int position) {
        boolean isSelected = false;
        for (int i = 0; i < mSelectedPositionList.size(); i++) {
            if (position == mSelectedPositionList.get(i)) {
                mSelectedPositionList.remove(i);
                isSelected = true;
            }
        }
        if (!isSelected) {
            mSelectedPositionList.add((Integer) position);
        }
        notifyDataSetChanged();
    }
    
    /**
     * 判断选择列表中是否包含指定位置
     * @param position
     * @return
     */
    public boolean isContainPosition(int position) {
    	for (Integer temp : mSelectedPositionList) {
    		if (position == temp) {
    			return true;
    		}
    	}
    	return false;
    }

    /**
     * 获取已选择位置列表
     * 
     * @return
     */
    public final List<Integer> getSelectedList() {
        return mSelectedPositionList;
    }

    /**
     * 获取已选择的文件列表
     * 
     * @return
     */
    public final List<C> getSelectedDatas() {
        List<C> selectedDatas = new ArrayList<C>();
        for (int i = 0; i < mSelectedPositionList.size(); i++) {
            selectedDatas.add(getItem(mSelectedPositionList.get(i)));
        }
        return selectedDatas;
    }
    
    /**
     * 根据传入的位置（父节点计算在内）返回子对象
     * @param position
     * @return
     */
	public C getItem(int position) {
    	for (int i = 0; i < mChildMap.size(); i++) {
        	for (int j = 0; j < mChildMap.get(mParentList.get(i)).size(); j++) {
        		if (position == calculateCurPosition(i, j)) {
        			return mChildMap.get(mParentList.get(i)).get(j);
                }
        	}
        }
        return null;
    }
    
    /**
     * 根据输入对象返回对应的位置
     * @param c
     * @return
     */
    @SuppressWarnings("unchecked")
	public int getItem(C c) {
    	int position = 0;
    	for (int i = 0; i < mShowList.size(); i++) {
    		if (!String.class.isInstance(mShowList.get(i))) {
    			if (mChildManager.isSameObject(c, (C)mShowList.get(i))) {
    				position = i;
    				break;
    			}
    		}
    	}
    	return position;
    }
    
    /**
     * 获取当前显示的list
     * @return
     */
    public List<C> getShowList() {
    	List<C> cList = new ArrayList<C>();
    	for (int i = 0; i < mChildMap.size(); i++) {
    		cList.addAll(mChildMap.get(mParentList.get(i)));
    	}
    	return cList;
    }
    
    /**
     * 获取当前显示的list
     * @return
     */
    public List<C> getDataList() {
    	List<C> cList = new ArrayList<C>();
		for (Entry<String, List<C>> entry : mAllMap.entrySet()) {
			cList.addAll(entry.getValue());
		}
    	return cList;
    }

    /**
     * 清除所有选择项
     */
    public void clearSelection() {
        mSelectedPositionList.clear();
    }

    public static class ViewHolder {
    	public View root;
        public TextView[] tvs;
        public ImageView[] ivs;
        public EditText[] ets;
        public Button[] bs;
        public CheckBox[] cbs;
        public AutoCompleteTextView[] actvs;
        public DatePickText[] dpts; 
        public Spinner sp;
    }

	@Override
	public int getGroupCount() {
		return mParentList.size();
	}

	@Override
	public int getChildrenCount(int groupPosition) {
		return mChildMap.get(mParentList.get(groupPosition)).size();
	}

	@Override
	public String getGroup(int groupPosition) {
		return mParentList.get(groupPosition);
	}

	@Override
	public C getChild(int groupPosition, int childPosition) {
		return mChildMap.get(mParentList.get(groupPosition)).get(childPosition);
	}

	@Override
	public long getGroupId(int groupPosition) {
		return groupPosition;
	}

	@Override
	public long getChildId(int groupPosition, int childPosition) {
		return childPosition;
	}

	@Override
	public boolean hasStableIds() {
		return true;
	}

	@Override
	public View getGroupView(int groupPosition, boolean isExpanded,
			View convertView, ViewGroup parent) {
		// 用来设置Tag，以便循环利用已经隐藏的列表项视图对象
        ViewHolder holder;

        if (convertView != null) {
        	// 循环利用隐藏的列表项
            holder = (ViewHolder) convertView.getTag();
        } else {
            holder = new ViewHolder();
            convertView = LayoutInflater.from(mContext)
            				.inflate(mParentManager.getParentLayoutId(), parent, false);
            
            mParentManager.initParentLayout(convertView, holder);
            convertView.setTag(holder);
        }

        // 初始化列表项视图显示内容
        mParentManager.initParentListViewItem(convertView, holder, this, groupPosition);
        
        // AdapterView设置列表项的监听
        mParentManager.regesterParentListeners(holder, groupPosition);
        
        convertView.setBackgroundColor(Color.WHITE);
        
        return convertView;
	}

	@Override
	public View getChildView(int groupPosition, int childPosition,
			boolean isLastChild, View convertView, ViewGroup parent) {
		// 用来设置Tag，以便循环利用已经隐藏的列表项视图对象
        ViewHolder holder;

        if (convertView != null) {
        	// 循环利用隐藏的列表项
            holder = (ViewHolder) convertView.getTag();
        } else {
            holder = new ViewHolder();
            convertView = LayoutInflater.from(mContext)
            				.inflate(mChildManager.getChildLayoutId(), parent, false);
            if (mIsScroll) {
            	addCHScrollView((ListView) parent, convertView);
            }
            
            mChildManager.initChildLayout(convertView, holder);
            convertView.setTag(holder);
        }

        int position = calculateCurPosition(groupPosition, childPosition);
        // 初始化列表项视图显示内容
        mChildManager.initChildListViewItem(convertView, holder, this, position);
        
        // AdapterView设置列表项的监听
        mChildManager.regesterChildListeners(holder, position);
        
        if (mSelectedPositionList.contains((Integer) position)) {
            convertView.setBackgroundResource(R.color.listview_selected_bg); 
        } else {
        	if (childPosition % 2 != 0) {
            	convertView.setBackgroundResource(R.color.content_listview_single_line_bg);
            } else {
            	convertView.setBackgroundColor(Color.WHITE);
            }
        }
     
        return convertView;
	}

	@Override
	public boolean isChildSelectable(int groupPosition, int childPosition) {
		return true;
	}
	
	private int calculateCurPosition(int groupPosition, int childPosition) {
		int retPosition = childPosition + 1;
		for (int i = groupPosition - 1; i >= 0; i--) {
			if (mExpandableListView.isGroupExpanded(i)) {
				retPosition += mChildMap.get(mParentList.get(i)).size();
			}
			retPosition += 1;
		}
		return retPosition;
		
	}
	
	private int calculateParentPosition(int groupPosition) {
		int retPosition = 0;
		for (int i = groupPosition; i >= 0; i--) {
			if (mExpandableListView.isGroupExpanded(i)) {
				retPosition += mChildMap.get(mParentList.get(i)).size();
			}
			retPosition += 1;
		}
		LogUtil.i("wzw retPosition:" + retPosition + " groupPosition:" + groupPosition);
		return retPosition;
		
	}

	@Override
	public boolean getIsDrag() {
		return false;
	}
	

    /**
     * 适配器父接口
     * 
     * @author wangzhiwei
     * 
     */
    public interface ParentListAdapterInterface {
    	
    	// 获取列表布局资源ID
        int getParentLayoutId();

        // 注册组件的监听器
        void regesterParentListeners(ViewHolder viewHolder, final int position);
        
        // 初始化列表项视图
        void initParentListViewItem(View convertView, 
        						ViewHolder holder,
        						DataExpanableListAdapter<?> adapter,
        						int groupPosition);
        
        // 布局初始化
        void initParentLayout(View convertView, ViewHolder holder);
        
    }

    /**
     * 适配器子接口
     * 
     * @author wangzhiwei
     * 
     */
    public interface ChildListAdapterInterface<C> {
    	
    	// 获取列表布局资源ID
        int getChildLayoutId();

        // 获取列表头，用于添加到左右滑动组件中
        View getChildHeaderView();

        // 注册组件的监听器
        void regesterChildListeners(ViewHolder viewHolder, final int position);
        
        // 初始化列表项视图
        void initChildListViewItem(View convertView, 
        						ViewHolder holder,
        						DataExpanableListAdapter<C> adapter,
        						int position);
        
        // 布局初始化
        void initChildLayout(View convertView, ViewHolder holder);
        
        // 查询接口
        List<C> findByCondition(Object... condition);
        
        // 判断两个对象是否是同一个对象，实际上要根据对象里面的ID来比较，而不是真正意义上的完全相同的对象
        boolean isSameObject(C c1, C c2);
    }
}
