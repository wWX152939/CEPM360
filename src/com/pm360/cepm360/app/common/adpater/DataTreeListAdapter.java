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
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.Spinner;
import android.widget.TextView;

import com.pm360.cepm360.R;
import com.pm360.cepm360.app.common.custinterface.TreeExpandInterface;
import com.pm360.cepm360.app.common.view.CHScrollView;
import com.pm360.cepm360.app.common.view.DatePickText;
import com.pm360.cepm360.app.common.view.TextProgressBar;
import com.pm360.cepm360.entity.Expandable;
import com.pm360.cepm360.entity.ExpandableSort;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;


@SuppressLint("HandlerLeak") 
public class DataTreeListAdapter<T extends Expandable> extends BaseDragListAdapter<T> implements TreeExpandInterface {
    private Context mContext;
    private List<CHScrollView> mHScrollViews = new ArrayList<CHScrollView>();
    private TreeListAdapterInterface mManager;
    
    private List<T> mShowList = new ArrayList<T>();
    private List<T> mDataList = new ArrayList<T>();
    private List<List<T>> mLevelList = new ArrayList<List<T>>();
    private List<Integer> mSelectedPosition = new ArrayList<Integer>();
    
    // 1: plan 2 normal only tree 3 tree content
    public static final int BACKGROUND_TYPE_PLAN = 1;
    public static final int BACKGROUND_TYPE_NORMAL = 2;
    public static final int BACKGROUND_TYPE_TREE_CONTENT = 3;
    private int mBackgroundType;
    private boolean mIsScroll = true;
    
    // 多选模式
    private boolean mIsMultiSelectMode;
    private boolean mIsMultiModifyMode;
    
    // 多选模式的缓存 展开后到不展开状态，会丢失数据，此时做缓存 主要用于删除模式
    private List<T> mSelectBeanList = new ArrayList<T>();
    public void setEnableMultiSelectMode(boolean isMultiSelectMode) {
    	mIsMultiSelectMode = isMultiSelectMode;
    }
    
    private Handler mHandler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            notifyDataSetChanged();
        }
    };
    
    public DataTreeListAdapter(Context context, TreeListAdapterInterface manager, boolean backgroundType) {
        this(context, null, true, manager, 0, backgroundType);
    }
    
    public DataTreeListAdapter(Context context, TreeListAdapterInterface manager, int isBlackBackGroud) {
        this(context, null, true, manager, 0, isBlackBackGroud);
    }
    
    public DataTreeListAdapter(Context context, TreeListAdapterInterface manager, boolean isBlackBackGroud, int resourceId) {
        this(context, null, true, manager, resourceId, isBlackBackGroud);
    }
    
    public DataTreeListAdapter(Context context, boolean scrollable, TreeListAdapterInterface manager) {
    	this(context, null ,scrollable, manager, 0, BACKGROUND_TYPE_TREE_CONTENT);
    }
    
    public DataTreeListAdapter(Context context, List<T> dataList, boolean scrollable, TreeListAdapterInterface manager) {
        this(context, dataList, scrollable, manager, 0, false);
    }
    
    public DataTreeListAdapter(Context context, boolean scrollable, TreeListAdapterInterface manager, int resourceId,  View headerView) {
        this(context, null, scrollable, manager, resourceId, false);
        if (mIsScroll) {
            addCHScrollView(null, headerView);
        }
    }
    
    public DataTreeListAdapter(Context context, boolean scrollable, TreeListAdapterInterface manager, int resourceId, int backgroundType, View headerView) {
        this(context, null, scrollable, manager, resourceId, false);
        mBackgroundType = backgroundType;
        if (mIsScroll) {
            addCHScrollView(null, headerView);
        }
    }
    
    public DataTreeListAdapter(Context context, List<T> dataList, boolean scrollable, TreeListAdapterInterface manager, int resourceId, boolean isBlackBackGround) {
        super(context, resourceId);
    	mContext = context;
        mManager = manager;
        mIsScroll = scrollable;
        if (isBlackBackGround) {
        	mBackgroundType = BACKGROUND_TYPE_NORMAL;
        } else {
        	mBackgroundType = BACKGROUND_TYPE_PLAN;
        }
    }
    
    public DataTreeListAdapter(Context context, List<T> dataList, boolean scrollable, TreeListAdapterInterface manager, int resourceId, int backgroundType) {
        super(context, resourceId);
    	mContext = context;
        mManager = manager;
        mIsScroll = scrollable;
        mBackgroundType = backgroundType;
        if (dataList != null) {
        	setDataList(dataList);
        }
    }
    
    public void setMultiMode(boolean isModifyMode) {
    	mIsMultiModifyMode = isModifyMode;
    }

    @SuppressWarnings("unchecked")
	public List<T> getShowList() {
        return mShowList;
    }
    
    public void setShowList(List<T> nodeList) {
        mShowList = nodeList;
    }
    
    public List<T> getDataList() {
        return mDataList;
    }
    
    public void setDataList(List<T> datalist) {
        mDataList = datalist;
        mShowList = filterRootNode(mDataList);
        formatDirectoryList(mDataList);
        mHandler.sendEmptyMessage(0);
    }
    
    public void updateDataList(List<T> datalist) {      
        mShowList = filterRootNode(datalist);
        mDataList = datalist;
        mHandler.sendEmptyMessage(0);
    }
    
    public void addDataList(List<T> datalist) {
    	clearCount();
        mDataList.addAll(datalist);
        mShowList = filterRootNode(mDataList);
        formatDirectoryList(datalist);
        mHandler.sendEmptyMessage(0);
    }
    
    /**
     * 清除计数
     */
    private void clearCount() {
    	// 清除计数
    	for (T t : mDataList) {
    		t.setCount(0);
    	}
    }
    
    /**
     * 更新树形列表，树形列表不做收缩或展开
     * 通常在 添加，删除 时使用
     * 
     * @param showList 当前需求显示的列表
     * @param datalist 所有数据列表
     */
    public void updateDataList(List<T> showList, List<T> datalist) {     
        mShowList = showList;
        mDataList = datalist;
        mHandler.sendEmptyMessage(0);
    }
    
    private List<T> filterRootNode(List<T> list) {
        List<T> node = new ArrayList<T>();
         for(int i = 0; i < list.size(); i++) {
             if(list.get(i).getLevel() == 0 && list.get(i).getParents_id() == 0) {
                 node.add(list.get(i));
             } else if (list.get(i).getParents_id() == 0)
                 node.add(list.get(i));
         }
         return node;
     }
    
    /**
     * 清除数据列表所有数据
     */
    public void clearAll() {
        mShowList.clear();
        mDataList.clear();
        mSelectedPosition.clear();
        mHandler.sendEmptyMessage(0);
    }
    
    @Override
    public int getCount() {
        return mShowList.size();
    }

    @Override
    public T getItem(int position) {
        return mShowList.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        // 用来设置Tag，以便循环利用已经隐藏的列表项视图对象
        ViewHolder holder;

        if (convertView != null) {
            // 循环利用隐藏的列表项
            holder = (ViewHolder) convertView.getTag();
        } else {
            holder = new ViewHolder();
            convertView = LayoutInflater.from(mContext)
                            .inflate(mManager.getLayoutId(), parent, false);
            if (mIsScroll) {
                addCHScrollView((ListView) parent, convertView);
            }
            
            mManager.initLayout(convertView, holder);
            convertView.setTag(holder);
        }
           
        initListViewItem(holder, position);
        mManager.initListViewItem(holder, position);
        
        // AdapterView设置列表项的监听
        mManager.regesterListeners(holder, position);
        
        if (mSelectedPosition.contains((Integer) position) || getItem(position).isSelected()) {
            convertView.setBackgroundResource(R.color.listview_selected_bg); 
        } else {
        	if (mBackgroundType == BACKGROUND_TYPE_NORMAL) {
        		// normal
        		if (position % 2 == 1) {
                    convertView.setBackgroundResource(R.color.content_listview_single_line_bg);
                } else {
                    convertView.setBackgroundColor(Color.WHITE);
                }
        	} else if (mBackgroundType == BACKGROUND_TYPE_PLAN) {
        		// plan
        		convertView.setBackgroundColor(Color.WHITE);
        	} else {
        		// tree content
        		convertView.setBackgroundResource(R.color.tree_listview_bg);
        	}
        }
        
        return convertView;
    }
    
    private void initListViewItem(ViewHolder holder, final int position) {
        int level = getItem(position).getLevel();
        holder.ivs[0].setPadding(15 * (level + 1), holder.ivs[0]
            .getPaddingTop(), 0, holder.ivs[0].getPaddingBottom());
//        holder.ivs[0].setLayoutParams(
//				new LinearLayout.LayoutParams(15 * (level + 1), holder.ivs[0].getLayoutParams().height));
        
        if (getItem(position).isHas_child() && !getItem(position).isExpanded()) {
            holder.ivs[0].setImageResource(R.drawable.item_collapse);
            holder.ivs[0].setVisibility(View.VISIBLE);
        } else if (getItem(position).isHas_child() && getItem(position).isExpanded()) {
            holder.ivs[0].setImageResource(R.drawable.item_expand);
            holder.ivs[0].setVisibility(View.VISIBLE);
        } else if (!getItem(position).isHas_child()){
            holder.ivs[0].setImageResource(R.drawable.item_collapse);
            holder.ivs[0].setVisibility(View.INVISIBLE);
        }
    }
    
    public void updateListView(int position) {
        if (!getItem(position).isHas_child()) {
            return;
        }

        if (getItem(position).isExpanded()) {
            getItem(position).setExpanded(false);
            ArrayList<T> temp = new ArrayList<T>();
            ArrayList<T> lightTemp = new ArrayList<T>();
            ArrayList<Integer> lineList = new ArrayList<Integer>();
            for (int i = position + 1; i < mShowList.size(); i++) {
                if (getItem(position).getLevel() >= mShowList.get(i).getLevel()) {
                    break;
                }

                if (mIsMultiSelectMode && mSelectedPosition.contains(i)) {
                	lineList.add((Integer)i);
                	lightTemp.add(mShowList.get(i));
                }
                
                temp.add(mShowList.get(i));
            }
            
            if (mIsMultiSelectMode) {
            	// 展开到未展开状态
            	// 1.移除子高亮
            	mSelectedPosition.removeAll(lineList);
            	// 2.设置转换当前项后面的高亮值
            	for (int i = position + 1 + temp.size(); i < mShowList.size(); i++) {
            		if (mSelectedPosition.contains(i)) {
            			mSelectedPosition.remove((Integer)i);
            			mSelectedPosition.add(i - temp.size());
            		}
            	}
            	
                // 设置缓存
                mSelectBeanList.addAll(lightTemp);
            }
            
            mShowList.removeAll(temp);
        } else {
            getItem(position).setExpanded(true);

        	ArrayList<T> temp = new ArrayList<T>();
            // 插入模式， 需要进行排序，然后再显示
            if (ExpandableSort.class.isInstance(mShowList.get(position))) {
                for (T t : mDataList) {
                    if (t.getParents_id() == getItem(position).getId()) {
                        t.setLevel(getItem(position).getLevel() + 1);
                        t.setExpanded(false);
                        temp.add(t);
                    }
                }
                Collections.sort(temp, new ListSortComparator());
                mShowList.addAll(position + 1, temp);
                
            } else {
            	int j = 1;
                for (T t : mDataList) {
                    if (t.getParents_id() == getItem(position).getId()) {
                        t.setLevel(getItem(position).getLevel() + 1);
                        t.setExpanded(false);
                        mShowList.add(position + j, t);

                        if (mIsMultiSelectMode) {
                        	temp.add(t);
                        }
                        j++;
                    }
                }
            }
            
            if (mIsMultiSelectMode) {
            	// 未展开到展开状态
	            // 1.转换当前选中后面的高亮
	            for (int i = position + 1; i < mShowList.size() - temp.size(); i++) {
	            	if (mSelectedPosition.contains(i)) {
	            		mSelectedPosition.remove((Integer)i);
	            		mSelectedPosition.add(i + temp.size());
	            	}
	            }
	
	            // 2.添加子高亮 首先判断父高亮 （在多选删除模式）
	        	if (mSelectedPosition.contains(position) && !mIsMultiModifyMode) {
	        		for (int i = position + 1; i < position + 1 + temp.size(); i++) {
		            	mSelectedPosition.add(i);
		            }
	        	} else {
	        		// 3.如果父没有高亮，查看缓存
		        	for (int i = 0; i < mSelectBeanList.size();) {
		        		int j = position + 1;
		        		for (; j < position + 1 + temp.size(); j++) {
		        			if (mShowList.get(j).getId() == mSelectBeanList.get(i).getId()) {
		        				mSelectedPosition.add(j);
		        				// 已经恢复显示，去掉缓存内容
		        				mSelectBeanList.remove(i);
		        				break;
		        			}
		        		}
		        		if (j == (position + 1 + temp.size())) {
		        			i++;
		        		}
		        	}
	        	}

            }
            
        }
        
        notifyDataSetChanged();
    }
    
    public class ListSortComparator implements Comparator<T> {

		@Override
		public int compare(T lhs, T rhs) {
			return ((ExpandableSort) lhs).getSort() - ((ExpandableSort) rhs).getSort();
		}
    	
    }
    
    public List<Integer> getSelected() {
        return mSelectedPosition;
    }
    
    /**
     * 获取多选模式下所有选中数据
     * @return
     */
    public List<T> getSelectedData() {
    	List<T> list = new ArrayList<T>();
    	list.addAll(mSelectBeanList);
    	for (Integer i : mSelectedPosition) {
    		list.add(getItem(i));
    	}
    	return list;
    }
    
    public void setSelected(int position, boolean isSelected) {
        if (isSelected) {
            mSelectedPosition.clear();
            mSelectedPosition.add((Integer) position);
        } else {
            mSelectedPosition.remove((Integer) position);
        }
        mHandler.sendEmptyMessage(0);
    }
    
    public void setPickSelectedInUpdateMode(int position) {
    	boolean isSelected = false;
        for (int i = 0; i < mSelectedPosition.size(); i++) {
            if (position == mSelectedPosition.get(i)) {
            	mSelectedPosition.remove(i);
            	isSelected = true;
            	// 清楚缓存
            	for (T t : mSelectBeanList) {
					if (t.getId() == mShowList.get(position).getId()) {
						mSelectBeanList.remove(t);
						break;
					}
				}
            }
        }
        if (!isSelected) {
        	mSelectedPosition.add((Integer) position);
        }

        notifyDataSetChanged();
    }
    
    /**
     * 多选 (删除模式下的多选)
     * 
     * @param position
     */
    public boolean setPickSelected(int position) {
        boolean isSelected = false;
        for (int i = 0; i < mSelectedPosition.size(); i++) {
            if (position == mSelectedPosition.get(i)) {
            	mSelectedPosition.remove(i);
            	isSelected = true;
            	// 清楚缓存
            	for (T t : mSelectBeanList) {
					if (t.getId() == mShowList.get(position).getId()) {
						mSelectBeanList.remove(t);
						break;
					}
				}
            }
        }
        if (!isSelected) {
        	mSelectedPosition.add((Integer) position);
        }
        
        // 多选模式
        if (mIsMultiSelectMode) {
        	if (mShowList.get(position).isHas_child() && mShowList.get(position).isExpanded()) {
                ArrayList<Integer> lineList = new ArrayList<Integer>();
                for (int i = position + 1; i < mShowList.size(); i++) {
                    if (getItem(position).getLevel() >= mShowList.get(i).getLevel()) {
                        break;
                    }
                    lineList.add((Integer)i);
                }
        		if (!isSelected) {
        			// 子也要全部选中 排除之前选中
        			for (int i = 0; i < lineList.size(); i++) {
            			if (!mSelectedPosition.contains(lineList.get(i))) {
            				mSelectedPosition.add(lineList.get(i));
            			}
        			}
        		} else {
        			// 子全部取消
        			mSelectedPosition.removeAll(lineList);
        			// 清空高亮缓存
        			for (int i = 0; i < lineList.size(); i++) {
        				for (T t : mSelectBeanList) {
        					if (t.getId() == mShowList.get((int)lineList.get(i)).getId()) {
        						mSelectBeanList.remove(t);
        						break;
        					}
        				}
        			}
        		}
        		
        	}

        	// 子全部取消， 所有的父都需要取消
    		if (isSelected) {
    			T currentBean = mShowList.get(position);
    			for (int i = position - 1; i >= 0; i--) {
					if (mShowList.get(i).getId() == currentBean.getParents_id()) {
						currentBean = mShowList.get(i);
						if (mSelectedPosition.contains((Integer)i)) {
    						mSelectedPosition.remove((Integer)i);
    						for (T t : mSelectBeanList) {
    							if (t.getId() == mShowList.get(i).getId()) {
    								mSelectBeanList.remove(t);
    								break;
    							}
    						}
						}
					}
				}
    		}
        }
        
        notifyDataSetChanged();
        return isSelected;
    }
    
    public void setSelectedAll() {
    	mSelectedPosition.clear();
        for (int i = 0; i < getCount(); i++) {
        	mSelectedPosition.add(i);
        }
        notifyDataSetChanged();
    }
    
    public void clearSelectionAll() {
    	mSelectedPosition.clear();
    	mSelectBeanList.clear();
        notifyDataSetChanged();
    }
    
    public void clearSelection(int position) {
        mSelectedPosition.remove((Integer) position);
    }
    
    /**
     * 获取已选择的文件列表
     * 
     * @return
     */
    public final List<T> getSelectedDatas() {
        List<T> selectedDatas = new ArrayList<T>();
        for (int i = 0; i < mSelectedPosition.size(); i++) {
            selectedDatas.add(getItem(mSelectedPosition.get(i)));
        }
        return selectedDatas;
    }

    @Override
    public void onScrollChanged(CHScrollView view, int l, int t, int oldl,
            int oldt) {
        for (CHScrollView scrollView : mHScrollViews) {
            if (view != scrollView)
                scrollView.smoothScrollTo(l, t);
        }
    }
    
    /**
     * 为所有的列表项添加滑动
     * @param listView
     * @param view
     */
    public void addCHScrollView(ListView listView, View view) {
    	super.addCHScrollView(listView, view);
        final CHScrollView chScrollView = 
                (CHScrollView) view.findViewWithTag(CHScrollView.TAG);
        
        if (chScrollView == null)
            return;
        
        chScrollView.setOnScrollChagnedListener(this);
        if (!mHScrollViews.isEmpty()) {
            final int scrollX = mHScrollViews
                        .get(mHScrollViews.size() - 1).getScrollX();
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
    
    public void notifyDataChange() {
    	mHandler.sendEmptyMessage(0);
    }
    
    /**
     * 更新树节点计数
     * @param count
     */
    public void updateTreeNodesCount(int count) {
    	updateTreeNodesCount(mShowList.get(mSelectedPosition.get(0)).getId(), count);
    }
    
    /**
     * 更新树节点计数
     * @param nodeId 从指定的树节点更新计数
     * @param count
     */
    public void updateTreeNodesCount(int nodeId, int count) {
    	
    	// 当前树节点
    	T current = mShowList.get(mSelectedPosition.get(0));
    	if (current.getId() == nodeId) {
    		
    		// 更新当前节点的计数
            current.setCount(current.getCount() + count);
            int level = current.getLevel() - 1;
            for (int i = mSelectedPosition.get(0) - 1; i >= 0; i--) {
            	T node = mShowList.get(i);
                if (level == node.getLevel()) {
                	node.setCount(node.getCount() + count);
                    if (level == 0) {
                        break;
                    }
                    level--;
                }       
            }
    	} else {
    		
    		// 首先查询指定ID的对象
    		for (int i = 0; i < mDataList.size(); i++) {
    			if (mDataList.get(i).getRealId() == nodeId) {
    				current = mDataList.get(i);
    				
    				// 更新当前节点的计数
    	            current.setCount(current.getCount() + count);
    				break;
    			}
    		}
    		
    		// 依次增加父节点计数
    		T temp = current;
    		for (int i = current.getLevel() - 1; i >= 0; i--) {
    			List<T> list = mLevelList.get(i);
    			T levelT = null;
    			for (int j = 0; j < list.size(); j++) {
    				levelT = list.get(j);
    				if (levelT.getId() == temp.getParents_id()) {
    					levelT.setCount(levelT.getCount() + count);
    				}
    			}
    			
    			temp = levelT;
    		}
    	}
    	
        mHandler.sendEmptyMessage(0);
    }
    
    
    
    /**
     * 文件列表适配器接口，需要在fragment中实现
     * 
     * @author yuanlu
     * 
     */
    public interface TreeListAdapterInterface {
        // 获取列表布局资源ID
        int getLayoutId();

        // 初始化列表数据, 填充数据
        void initListViewItem(ViewHolder viewHolder, int position);
        
        // 注册组件的监听器(除了 tree item)
        void regesterListeners(ViewHolder viewHolder, final int position);
        
        // 布局初始化
        void initLayout(View convertView, ViewHolder holder);
        
        // 计算树节点下内容列表项的数量
        void calculateContentItemCount();

    }
    
    public static class ViewHolder {
        public TextView[] tvs;
        public View paddView;
        public ImageView[] ivs;
        public EditText[] ets;
        public Button[] bs;
        public CheckBox[] cbs;
        public View[] views;
        public AutoCompleteTextView[] actvs;
        public DatePickText[] dpts; 
        public Spinner sp;
        public TextProgressBar[] txpbs;
    }
    
    /**
     * 添加一个目录
     * @param directory
     */
    public void addTreeNode(T t) {
        mDataList.add(t);  
        addToTreeList(t);
    }
    
    /**
     * 添加一个目录到目录树中
     * @param directory
     */
    private void addToTreeList(T t) {
        if (t.getParents_id() == 0) {
            mShowList.add(t);
        } else {
            int i, level = mShowList.get(mSelectedPosition.get(0)).getLevel();
            t.setLevel(level + 1);
            // 模拟点击当前节点
            // 如果当前节点有孩子但没有展开，添加目录时需要先展开在添加
            if (mShowList.get(mSelectedPosition.get(0)).isHas_child() 
                    && !mShowList.get(mSelectedPosition.get(0)).isExpanded()) {
                // 模拟点击当前节点
                updateListView(mSelectedPosition.get(0));
            } else {
                mShowList.get(mSelectedPosition.get(0)).setHas_child(true);
                mShowList.get(mSelectedPosition.get(0)).setExpanded(true);
                for (i = mSelectedPosition.get(0) + 1; i < mShowList.size(); i++) {
                    if (mShowList.get(i).getLevel() <= level) {
                        break;
                    }
                }
                mShowList.add(i, t);
            }
        }
        mHandler.sendEmptyMessage(0);
    }
    
    /**
     * 多选模式下的删除
     */
    public void deleteMultiTreeNode(List<T> deleteList) {
    	for (int i = 0; i < mShowList.size();) {
    		if (deleteList.contains(mShowList.get(i))) {
    			if (i != 0) {
        			updateParentTreeNode(i);
    			}
    			mShowList.remove(i);
    		} else {
    			i++;
    		}
    	}
    	
    	mDataList.removeAll(deleteList);

    	clearSelectionAll();
    }
    
    /**
     * 删除一个目录节点
     */
    public List<T> deleteTreeNode() {
        List<T> childList = recursionRemoveList(mSelectedPosition.get(0));
        removeTreeNode(mSelectedPosition.get(0));
        return childList;
    }
    
    /**
     * 删除选中
     */
    private void removeTreeNode(int selectPosition) {
    	updateParentTreeNode(selectPosition);
        recursionRemoveTreeNode(selectPosition);
        mHandler.sendEmptyMessage(0);
    }
    
    /**
     * 当删除一个目录节点时，首先要更新父目录的状态
     */
    private void updateParentTreeNode(int selectPosition) {
        // 如果不是根节点，删除该节点要处理父节点的hasChild状态
        if (mShowList.get(selectPosition).getParents_id() != 0) {
            int level = mShowList.get(selectPosition).getLevel();    
            int count = 0;
            if (mShowList.get(selectPosition -1).getLevel() >= level) {
                count++;
            } else {
	            for (int i = selectPosition + 1; i < mShowList.size(); i++) {
	                if (mShowList.get(i).getLevel() < level) {
	                    break;
	                } else if (mShowList.get(i).getLevel() == level) {
	                    count++;
	                    break;
	                }
	            }
            }
            
            // 该节点没有兄弟节点，父节点的hasChild为false
            if (count == 0) {
                mShowList.get(selectPosition -1).setHas_child(false);
                // 处理目录链表中的父节点状态，（非目录树）
                int parentId = mShowList.get(selectPosition).getParents_id();
                for (int i = 0; i < mDataList.size(); i++) {
                    if (mDataList.get(i).getId() == parentId) {
                        mDataList.get(i).setHas_child(false);
                    }
                }
            }
        }
    }
    
    /**
     * 递归删除节点
     */
    public void recursionRemoveTreeNode(int selectPosition) {
        int level = mShowList.get(selectPosition).getLevel();
        mShowList.remove(selectPosition);
        for (int i = selectPosition; i < mShowList.size();) {
            if (mShowList.get(i).getLevel() <= level) {
                break;
            }
            mShowList.remove(i);
        }
    }
    
    /**
     * 递归删除某个节点和子节点
     */
    private List<T> recursionRemoveList(int selectPosition) {
        int direcotryId = mShowList.get(selectPosition).getId();
        List<T> childList = new ArrayList<T>();
        int i = 0;
        // 先删除选中的节点
        for (; i < mDataList.size(); i++) {
            if (mDataList.get(i).getId() == direcotryId) {
            	childList.add(mDataList.get(i));
                mDataList.remove(i);
                break;
            }
        }
        // 依次删除没有父节点的节点，直到链表大小不变，除去parent等于0的节点
        for (int j = i; j < mDataList.size();) {
            int parentId = mDataList.get(j).getParents_id();
            boolean found = false;
            for (int k = 0; k < mDataList.size(); k++) {
                if (mDataList.get(k).getId() == parentId) {
                    // 找到父节点，跳出循环
                    found = true;
                    break;
                }
            }
            // 未找到父节点，删除该节点
            if (!found) {
            	childList.add(mDataList.get(j));
                mDataList.remove(j);
                continue;
            }
            j++;
        }
        
        return childList;
    }

    /**
     * 格式化树列表
     */
    private void formatDirectoryList(List<T> listData) {    	
        for (int i = 0; i < listData.size(); i++) {
            int parentId = listData.get(i).getParents_id();
            // 顶层目录直接跳过
            if (parentId == 0) {
                if (mLevelList.size() == 0) {
                    List<T> rootList = new ArrayList<T>();
                    rootList.add(listData.get(i));
                    mLevelList.add(rootList);
                } else {
                	mLevelList.get(0).add(listData.get(i));
                }
                continue;
            }
            
            for (int j = 0; j < i; j++) {
                if(parentId == listData.get(j).getId()) {
                    int level = listData.get(j).getLevel() + 1;
                    listData.get(i).setLevel(level);
                    if (mLevelList.size() <= level) {
                    	List<T> levelList = null;
                    	while (mLevelList.size() <= level) {
                    		levelList = new ArrayList<T>();
                    		mLevelList.add(levelList);
                    	}
                    	levelList.add(listData.get(i));
                    } else {
                    	mLevelList.get(level).add(listData.get(i));
                    }
                    listData.get(j).setHas_child(true);
                    break;
                }
            }
        }

        mManager.calculateContentItemCount();
    }
    
    public List<List<T>> getLevelList() {
        return mLevelList;
    }
    
    /**
     * 计算树节点包含的内容列表项个数的递归函数
     * 
     * @param treeList
     * @param t
     * @param count
     */
    public void setParentTreeNodeCount(List<T> treeList, T t, int count) {
        for (int i = 0; i < treeList.size(); i++) {
            if (t.getParents_id() == treeList.get(i).getId()) {
                int contentCount = treeList.get(i).getCount();
                treeList.get(i).setCount(contentCount + count);
                
                // 递归设置目录计算
                if (treeList.get(i).getParents_id() > 0) {
                	setParentTreeNodeCount(treeList, treeList.get(i), count);
                }
                // 找到就跳出循环
                break;
            }
        }
    }
    
    /*
     * 定位到某一个目录
     */   
    public T findTreeNode(int treeId) {
        T target = null;
        for (int i = 0; i < mDataList.size(); i++) {
            if (treeId == mDataList.get(i).getId()) {
                target = mDataList.get(i);
                break;
            } 
        }
        
        return target;
    }
    
    /*
     * 定位到某一个目录, 并展开其父节点
     */
    public T locateAllDirectoryById(int treeId) {
        List<T> targetList = new ArrayList<T>();
        List<T> parentList = new ArrayList<T>();
        
        T target = findTreeNode(treeId);
        if (target != null) {
	        mShowList.clear();
	        if (target.getLevel() == 0) {	// 目标节点即为顶层节点
	            targetList = mLevelList.get(0);
	            mShowList.addAll(targetList);
	        } else {
	            parentList.add(target);
	            locateParentDirectory(parentList, target.getParents_id(), target.getLevel()-1);
	            targetList.addAll(mLevelList.get(0)); // 首先加入顶层目录
	            for (int i = parentList.size() - 2; i >= 0; i--) {
	            	
	                // 找出它的兄弟
	            	List<T> templist = mLevelList.get(parentList.get(i).getLevel());
	                int position = itemIndex(parentList.get(i+1), targetList);
	                for (int k = 0, j = position + 1; k < templist.size(); k++) {
	                    if (parentList.get(i+1).getId() 
	                    			== templist.get(k).getParents_id()) {
	                        targetList.add(j, templist.get(k));
	                        j++;
	                    }
	                }
	            }
	            
	            setShowList(targetList);
	        }
	        
	        notifyDataChange();
	        setSelected(itemIndex(target, targetList), true);
        }
        
        return target;
    }
    
    /**
     * 递归向上查找父节点
     * @param parentId
     * @param level
     */
    private void locateParentDirectory(List<T> parentList, int parentId, int level) {
        T parent = null;
        List<T> templist = mLevelList.get(level);
        for (int i = 0; i < templist.size(); i++) {
            if (parentId == templist.get(i).getId()) {
                parent = templist.get(i);               
                parentList.add(parent);
                templist.get(i).setExpanded(true);
                break;
            }
        }
        
        /**
         * 找到父节点，并且父节点的父节点ID不为0，继续查找
         */
        if (parent != null && parent.getParents_id() != 0) {
            locateParentDirectory(parentList, parent.getParents_id(), level - 1);
        }   
    }
    
    /*
     * 定位指定对象在目标列表中的索引
     */
    private int itemIndex(T t, List<T> list) {
        int position = -1;
        for (int i = 0; i < list.size(); i++) {
            if (t == list.get(i)) {
                position = i;
                break;
            }
        }
        
        return position;
    }
}
