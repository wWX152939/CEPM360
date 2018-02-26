package com.pm360.cepm360.app.module.combination;

import android.annotation.SuppressLint;
import android.app.ActionBar.LayoutParams;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.graphics.Typeface;
import android.graphics.drawable.AnimationDrawable;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.text.Spannable;
import android.text.SpannableStringBuilder;
import android.text.style.StyleSpan;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.animation.AccelerateDecelerateInterpolator;
import android.view.animation.Animation;
import android.view.animation.Animation.AnimationListener;
import android.view.animation.TranslateAnimation;
import android.widget.AdapterView;
import android.widget.HorizontalScrollView;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.pm360.cepm360.R;
import com.pm360.cepm360.app.cache.EpsCache;
import com.pm360.cepm360.app.cache.PermissionCache;
import com.pm360.cepm360.app.cache.ProjectCache;
import com.pm360.cepm360.app.cache.UserCache;
import com.pm360.cepm360.app.common.activity.DataManagerInterface;
import com.pm360.cepm360.app.common.adpater.EpsAdapter;
import com.pm360.cepm360.app.common.adpater.EpsAdapter.ExpandCell;
import com.pm360.cepm360.app.common.view.FloatingMenuView;
import com.pm360.cepm360.app.module.home.HomeActivity;
import com.pm360.cepm360.app.module.schedule.CombinationScheduleActivity;
import com.pm360.cepm360.app.utils.UtilTools;
import com.pm360.cepm360.common.GLOBAL;
import com.pm360.cepm360.common.ResultStatus;
import com.pm360.cepm360.common.json.AnalysisManager;
import com.pm360.cepm360.common.util.DateUtils;
import com.pm360.cepm360.core.TreePresenter;
import com.pm360.cepm360.entity.Project;
import com.pm360.cepm360.entity.ZH_count_info;
import com.pm360.cepm360.entity.ZH_group;
import com.pm360.cepm360.services.group.RemoteGroupService;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

@SuppressLint("InflateParams")
public class CombinationView implements HomeActivity.OnFreshEpsInterface{
	
	public static final int TYPE_ZH = 1;
	public static final int TYPE_PLAN = 2;
	
	public static final String GROUP_KEY = "project_key";
	public static final int REQUEST_NODE_ADD = 100;
	public static final int REQUEST_NODE_MODIFY = 101;
	public static final int REFRESH_WINDOW = 102;
	private static final int TAG_BASE = 10000;
	private String COMBINATION_MODIFY_PERMISSION = GLOBAL.SYS_ACTION[34][0];
	private EpsAdapter mListAdapter;
	private RemoteGroupService mGroupService;
	private EpsAdapter.ExpandCell mExpandCell;
	private List<ZH_group> mListGroup;
	private List<ZH_count_info> mCountListGroup = new ArrayList<ZH_count_info>();
	private ZH_group mUpdateGroup;
	private boolean mIsReceiveGroupInfo = false;
	
	private int mType = TYPE_ZH;
	
	private RelativeLayout mEmptyTextViewFather;
	private TextView mEmptyTextView;
	private ImageView mClickMeImageView;
	private RelativeLayout mHorizontalScrollViewFather;
	private LinearLayout mScrollViewLinearLayout;
	@SuppressWarnings("unused")
	private HorizontalScrollView mHorizontalScrollView;
	private FloatingMenuView mFloatingMenuView;
	private boolean mAnimalFlag = false;
	private View mContentView;
	private Context mContext;
	
    @SuppressLint("HandlerLeak") 
    public Handler mViewAnimalHandler = new Handler() {
        public void handleMessage(Message msg) {
            switch (msg.what) {
            case 0:
            	int size = mScrollViewLinearLayout.getChildCount();
            	for (int index = 0; index < size; index++) {
            		TextView itemContent = (TextView)mScrollViewLinearLayout.
            				getChildAt(index).findViewById(R.id.item_content);            		
            		itemContent.setVisibility(View.VISIBLE);
            	}
            	mAnimalFlag = false;
                break;
            }
        }
    };
    
    public class MyThread implements Runnable {
        @Override
        public void run() {
            try {
                Thread.sleep(200);// 线程暂停10秒，单位毫秒
                Message message = new Message();
                message.what = 0;
                mViewAnimalHandler.sendMessage(message);// 发送消息
                
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
    }       
	
	public CombinationView(Context context) {
	    mContext = context;
	    
		mContentView = LayoutInflater.from(mContext).inflate(
		        R.layout.combination_activity, null);
		if (HomeActivity.class.isInstance(mContext)) {
			((HomeActivity) mContext).setOnFreshEpsInterface(this);
		} else {
			initLeftView();
		}
 		
		mGroupService = RemoteGroupService.getInstance();
		initView();
		
		initFloatingMenu();

		mEmptyTextViewFather.bringToFront();
	}

	public CombinationView(Context context, int type) {
		this(context);
		mType = type;
		if (mType == TYPE_PLAN) {
			COMBINATION_MODIFY_PERMISSION = GLOBAL.SYS_ACTION[2][0];
		}

	}
	
	public View getContentView(){
		return mContentView;
	}
	
	private void initView() {
		mHorizontalScrollViewFather = (RelativeLayout)mContentView.findViewById(R.id.horizontalScrollView_father);
		mScrollViewLinearLayout = (LinearLayout)mContentView.findViewById(R.id.scrollviewlinearlayout);
		mHorizontalScrollView = (HorizontalScrollView)mContentView.findViewById(R.id.horizontalScrollView1);
		
		mEmptyTextViewFather = (RelativeLayout)mContentView.findViewById(R.id.empty_tv_father);
		mEmptyTextView = (TextView)mContentView.findViewById(R.id.empty_tv);
		mClickMeImageView = (ImageView)mContentView.findViewById(R.id.clickme);
	}

	private void loadData() {
		Log.v("ccc","loadData");
		if (ProjectCache.getCurrentProject() != null) {
			Project project = ProjectCache.getCurrentProject();
			mExpandCell = new EpsAdapter.ExpandCell();
			mExpandCell.setEPS(false);
			mExpandCell.setEPSId(project.getEps_id());
			mExpandCell.setProjectId(project.getProject_id());
			mExpandCell.setTenantId(project.getTenant_id());
			
			// 为了第一次不显示创建界面
			mExpandCell.setHasChild(true);
			mGroupService.getGroup(mGroupManager, project.getProject_id());
		} else {
			
		}
	}
	
	public EpsAdapter.ExpandCell getExpandCell() {
		return mExpandCell;
	}
	
	private void initFloatingMenu() {
		mFloatingMenuView = (FloatingMenuView) mContentView.findViewById(R.id.floating_menu);
		mFloatingMenuView.addPopItem(mContext.getString(R.string.add), R.drawable.icn_add);
		mFloatingMenuView.addPopItem(mContext.getString(R.string.delete), R.drawable.menu_icon_delete);
		mFloatingMenuView.addPopItem(mContext.getString(R.string.modify), R.drawable.menu_icon_update);
		mFloatingMenuView
				.setPopOnItemClickListener(new AdapterView.OnItemClickListener() {

					@Override
					public void onItemClick(AdapterView<?> parent, View view,
							int position, long id) {
						switch (position) {
							case 0:/*Add*/
								mItemMode = ITEM_MODE_NORMAL;
								updateAllItemView(mScrollViewLinearLayout);
								
				                Intent intent = new Intent();  
				                intent.setClass(mContext, NodeCreatorActivity.class);
				                intent.putExtra(NodeCreatorActivity.NODE_OPERATION_KEY, NodeCreatorActivity.NODE_ADD); 
				                intent.putExtra(NodeCreatorActivity.NODE_TYPE, mType);
				                Bundle bundle = new Bundle();
				        		Project project = new Project(); 
				        		project.setProject_id(mExpandCell.getProjectId());
				        		project.setTenant_id(mExpandCell.getTenantId());
				                bundle.putSerializable("project", (Serializable) project);
				                intent.putExtras(bundle);
				                ((Activity) mContext).startActivityForResult(intent, REQUEST_NODE_ADD);
								break;
							case 1:/*Delete*/
								mItemMode = ITEM_MODE_DELETE;
								updateAllItemView(mScrollViewLinearLayout);
								break;
							case 2:/*Modify*/
								mItemMode = ITEM_MODE_EDIT;
								updateAllItemView(mScrollViewLinearLayout);
								break;						
							
						}
							mFloatingMenuView.dismiss();
					}

				});
		mFloatingMenuView.setVisibility(View.GONE);
	}
	

	private DataManagerInterface mGroupInfoManager = new DataManagerInterface() {

		@SuppressWarnings("unchecked")
		@Override
		public void getDataOnResult(ResultStatus status, List<?> list) {
			
			if (status.getCode() == AnalysisManager.SUCCESS_DB_QUERY) {
				mCountListGroup = (List<ZH_count_info>) list;
				Log.v("ccc","组信息查询成功");
				dataPreProcess();
				mIsReceiveGroupInfo = true;
				updateGroupInfo();
			}
		}
		
	};
	
	private DataManagerInterface mGroupManager = new DataManagerInterface() {
		
		@SuppressWarnings("unchecked")
		@Override
		public void getDataOnResult(ResultStatus status, List<?> list) {
			switch(status.getCode()) {
			case AnalysisManager.SUCCESS_DB_QUERY:
				Log.v("ccc","组节点查询成功");
				if (list == null || list.isEmpty()) {
					Log.v("ccc","组节点为空 绘制空节点界面");
					if (!mExpandCell.hasChild()) {
						initEmptyWindow();
					}
				} else {
					Log.v("ccc","组节点查询成功,接着查询 组信息");
					mListGroup = (List<ZH_group>) list;

					Log.v("chenchen","mListGroup="+mListGroup);					
					String groupIds = "";
					for (ZH_group group : mListGroup) {
						groupIds += group.getZh_group_id() + ",";
					}
					
					mCountListGroup.clear();
					notifyDataSetChanged();
					
					RemoteGroupService.getInstance().getCountGroupInfo(mGroupInfoManager, 
							groupIds.substring(0, groupIds.length() - 1));
				}
				break;
			case AnalysisManager.SUCCESS_DB_ADD:
				Log.v("ccc","组节点添加成功");
				mGroupService.getGroup(mGroupManager, mExpandCell.getProjectId());
				break;
			case AnalysisManager.SUCCESS_DB_UPDATE:
				Log.v("ccc","组节点查询更新成功");
				notifyDataSetChanged();
				updateGroupInfo();
				break;
			case AnalysisManager.SUCCESS_DB_DEL:
				Log.v("ccc","组节点移除成功");
				mListGroup.remove(mDeletePostion);
				mCountListGroup.remove(mDeletePostion);
				notifyDataSetChanged();
				updateGroupInfo();
				break;
			}
		}
	};
	


	private void initLeftView() {
		ListView listView = (ListView) mContentView.findViewById(R.id.list_view);
		mListAdapter = new EpsAdapter(mContext, EpsCache.getEpsLists(),
				ProjectCache.getProjectLists(), new EpsAdapter.EpsInterface() {

					@Override
					public void initListItem(EpsAdapter.ExpandCell expandCell) {
						if (expandCell.isEPS()) {
							mFloatingMenuView.setVisibility(View.GONE);
							mClickMeImageView.setVisibility(View.GONE);
							mEmptyTextView.setVisibility(View.VISIBLE);
							mEmptyTextView.setText(R.string.combination_empty);
							mEmptyTextView.setBackground(null);
							mEmptyTextView.setOnClickListener(null);
							mEmptyTextViewFather.bringToFront();
						} else {
							if (PermissionCache.hasSysPermission(COMBINATION_MODIFY_PERMISSION)){
								mFloatingMenuView.setVisibility(View.VISIBLE);
							}
							mItemMode = ITEM_MODE_NORMAL;
							mExpandCell = expandCell;
							mIsReceiveGroupInfo = false;
							mGroupService.getGroup(mGroupManager, expandCell.getProjectId());
						}					
					}
				});
		
		listView.setAdapter(mListAdapter);
	}
	
	@Override
	public void onFreshEps() {
		initLeftView();
	}
	
	@Override
	public void setProject(Project project) {
		if (project != null) {
			ExpandCell cell = new ExpandCell();
			cell = TreePresenter.initMsgInfo(mListAdapter.getDataList(), -project.getProject_id());
			TreePresenter.updateListView(TreePresenter.initLevelTask(mListAdapter.getDataList(), cell), mListAdapter);
			if (PermissionCache.hasSysPermission(COMBINATION_MODIFY_PERMISSION)){
				mFloatingMenuView.setVisibility(View.VISIBLE);
			}
			mItemMode = ITEM_MODE_NORMAL;
			mExpandCell = cell;
			mIsReceiveGroupInfo = false;
			mGroupService.getGroup(mGroupManager, cell.getProjectId());
		}
	}
	
	private void initEmptyWindow() {
		Log.v("ccc","初始化空信息界面");
		mScrollViewLinearLayout.removeAllViews();
		mEmptyTextViewFather.bringToFront();
		mFloatingMenuView.bringToFront();
		mEmptyTextView.setVisibility(View.VISIBLE);
		if (PermissionCache.hasSysPermission(COMBINATION_MODIFY_PERMISSION)) {
			if (mType == TYPE_PLAN) {
				mEmptyTextView.setText(R.string.combination_empty_node_plan);
			} else {
				mEmptyTextView.setText(R.string.combination_empty_node);
			}
			mEmptyTextView.setBackgroundResource(R.drawable.clickme_bg_selector);
			mClickMeImageView.setVisibility(View.VISIBLE);	
			AnimationDrawable animationDrawable;
	        animationDrawable = (AnimationDrawable) mClickMeImageView.getDrawable();  
	        animationDrawable.start();
	        
	        TranslateAnimation textViewAnim = new TranslateAnimation(0, 0, -50, 0);
	        textViewAnim.setDuration(800);
	        textViewAnim.setInterpolator(new AccelerateDecelerateInterpolator()); 
	        TranslateAnimation imageViewAnim = new TranslateAnimation(0, 0, 50, 0);
	        imageViewAnim.setDuration(500);
	        imageViewAnim.setInterpolator(new AccelerateDecelerateInterpolator());
	        
	        mEmptyTextView.startAnimation(textViewAnim);  
	        mClickMeImageView.startAnimation(imageViewAnim);
	        mEmptyTextView.setOnClickListener(new OnClickListener() {
				
				@Override
	            public void onClick(View v) {
	                Intent intent = new Intent();
	                intent.setClass(mContext, NodeCreatorActivity.class);
	                intent.putExtra("operation", NodeCreatorActivity.NODE_ADD);
					intent.putExtra(NodeCreatorActivity.NODE_TYPE, mType);				                
					Bundle bundle = new Bundle();
	        		Project project = new Project(); 
	        		project.setProject_id(mExpandCell.getProjectId());
	        		project.setTenant_id(mExpandCell.getTenantId());
	                bundle.putSerializable("project", (Serializable) project);
	                intent.putExtras(bundle);
	                ((Activity) mContext).startActivityForResult(intent, REQUEST_NODE_ADD);
	            }
			});
		} else {
			mEmptyTextView.setText(R.string.combination_empty_is_node);
		}
		
		mItemMode = ITEM_MODE_NORMAL;
	}
	
	private void notifyDataSetChanged() {
		Log.v("ccc","数据改变，开始更新本地数据列表");
		mClickMeImageView.setVisibility(View.GONE);
		
		mHorizontalScrollViewFather.bringToFront();
		mFloatingMenuView.bringToFront();
		dataSourceList.clear();
		
		for (int i = 0; i < mListGroup.size(); i++) {
			HashMap<String, Object> itemHashMap = new HashMap<String, Object>();
			itemHashMap.put("gridview_item_Text",mListGroup.get(i).getNode_name());	
			dataSourceList.add(itemHashMap);
		}
		
		/*界面刷新*/
		mEmptyTextView.setText("");
		mEmptyTextView.setVisibility(View.GONE);
		updateDisplay();
	}
	
	private List<HashMap<String, Object>> dataSourceList = new ArrayList<HashMap<String, Object>>();
	private int mDeletePostion;
	private void updateDisplay() {
		Log.v("ccc","更新显示整个节点展示界面");
		/*负责初始化整个界面*/
		if (dataSourceList.size() == 0) {
			/*此种模式说明数据全部清空，不需要显示*/
			initEmptyWindow();
		} else {
			mScrollViewLinearLayout.removeAllViews();
			
			int size = dataSourceList.size();
			for (int index = 0; index < size; index++) {
				View viewItem;
				viewItem = createItemView(
					dataSourceList.get(index).get("gridview_item_Text").toString(), index);						
				viewItem.setTag(index);
				TextView itemTitle = (TextView)viewItem.findViewById(R.id.item_title);
				itemTitle.setTag(TAG_BASE+index);

				final TranslateAnimation animation;
				animation = new TranslateAnimation(0, 0, -500, 0);
				animation.setDuration(500);//设置动画持续时间 
				animation.setRepeatCount(0);//设置重复次数 
				viewItem.setAnimation(animation);
				animation.start();
				animation.setAnimationListener(new AnimationListener() {
					
					@Override
					public void onAnimationStart(Animation arg0) {
					}
					
					@Override
					public void onAnimationRepeat(Animation arg0) {
					}
					
					@Override
					public void onAnimationEnd(Animation arg0) {
						if (dataSourceList.size() == mScrollViewLinearLayout.getChildCount()) {
							if (mAnimalFlag == false) {
								Log.v("ccc","onAnimationEnd ="+dataSourceList.size());
								new Thread(new MyThread()).start();
								mAnimalFlag = true;
							}
						}	 
					}
				});
				mScrollViewLinearLayout.addView(viewItem);
			}
		}		
	}
	
	private View createItemView(String titleStr, int index) {
		Log.v("ccc","创建第"+index+"个节点View 只有头");
		View viewItem;
		
		viewItem = LayoutInflater.from(mContext).inflate(R.layout.grid_item_card, null);
		@SuppressWarnings("deprecation")
		LinearLayout.LayoutParams lp = new LinearLayout.LayoutParams(UtilTools.dp2pxH(mContext, 180),LayoutParams.FILL_PARENT);
		viewItem.setLayoutParams(lp);
		TextView titleTextView = (TextView)viewItem.findViewById(R.id.item_title);
		titleTextView.setText(dataSourceList.get(index).get("gridview_item_Text").toString());
		titleTextView.setBackgroundResource(R.drawable.unititem_titlebg_nostart);
			
		return setItemListener(setItemViewMode(viewItem, mItemMode));
	}
	
	private void updateGroupInfo() {
		Log.v("ccc","开始绘制View身子的数据");
		if (mIsReceiveGroupInfo == true) {
			for (int index = 0; index < mCountListGroup.size(); index++) {
				View viewItem = mScrollViewLinearLayout.findViewWithTag(index);
				if (viewItem != null) {
					TextView titleTextView = (TextView)viewItem.findViewById(R.id.item_title);
					TextView titleRate = (TextView)viewItem.findViewById(R.id.item_title_rate);
					TextView itemContent = (TextView)viewItem.findViewById(R.id.item_content);
	//				Log.v("ccc","绘制第"+index+"个View的身子");
					int finish_rate = mCountListGroup.get(index).getFinish_rate();
	
					if (finish_rate == 0) {
						/*未开始*/
						titleTextView.setBackgroundResource(R.drawable.unititem_titlebg_nostart_selector);
					} else if (finish_rate >= 100) {
						/*结束*/
						titleTextView.setBackgroundResource(R.drawable.unititem_titlebg_end_selector);
					} else {
						/*未完成*/
						titleTextView.setBackgroundResource(R.drawable.unititem_titlebg_starting_selector);
					}
					
	                String userName = UserCache.getUserMaps()
	                        .get(String.valueOf(mCountListGroup.get(index).getOwner()));
									
					ImageView progressImageView = (ImageView)viewItem.findViewById(R.id.item_progressbar);
					progressImageView.setVisibility(View.GONE);
					
					if (mType == TYPE_ZH) {
						titleTextView.setText(dataSourceList.get(index).get("gridview_item_Text").toString());
						titleRate.setText(mCountListGroup.get(index).getFinish_rate()+"%");
			
						String str = mContext.getResources().getString(R.string.owner)+"："+ (userName == null ? "" : userName) +"\n"+
								mContext.getResources().getString(R.string.combination_period) + mCountListGroup.get(index).getPeriod()+"\n"+
								mContext.getResources().getString(R.string.combination_start) + DateUtils.dateToString(DateUtils.FORMAT_SHORT, mCountListGroup.get(index).getStart_date())+"\n"+
								mContext.getResources().getString(R.string.combination_end) + DateUtils.dateToString(DateUtils.FORMAT_SHORT, mCountListGroup.get(index).getEnd_date())+"\n"+
								mContext.getResources().getString(R.string.combination_total_task) + mCountListGroup.get(index).getTotal_task_count()+mContext.getResources().getString(R.string.combination_piece)+"\n"+
								mContext.getResources().getString(R.string.combination_finished) + "：" + mCountListGroup.get(index).getFinished_count()+mContext.getResources().getString(R.string.combination_piece)+"\n"+
								mContext.getResources().getString(R.string.combination_starting) + "：" + mCountListGroup.get(index).getIn_progress_count()+mContext.getResources().getString(R.string.combination_piece)+"\n"+
								mContext.getResources().getString(R.string.combination_nostart) + "：" + mCountListGroup.get(index).getUndoing_count()+mContext.getResources().getString(R.string.combination_piece);
						SpannableStringBuilder style = new SpannableStringBuilder(str);
						int start = 0;
						int len = (mContext.getResources().getString(R.string.owner)+"：").length();
						style.setSpan(new StyleSpan(Typeface.BOLD), start, len, Spannable.SPAN_EXCLUSIVE_INCLUSIVE);
						start = len + ((userName == null ? "" : userName) +"\n").length();
						len = mContext.getResources().getString(R.string.combination_period).length();
						style.setSpan(new StyleSpan(Typeface.BOLD), start, start+len, Spannable.SPAN_EXCLUSIVE_INCLUSIVE);
						start = start + len + (mCountListGroup.get(index).getPeriod()+"\n").length();
						len = mContext.getResources().getString(R.string.combination_start).length();
						style.setSpan(new StyleSpan(Typeface.BOLD), start, start+len, Spannable.SPAN_EXCLUSIVE_INCLUSIVE);
						start = start + len + (DateUtils.dateToString(DateUtils.FORMAT_SHORT, mCountListGroup.get(index).getStart_date())+"\n").length();
						len = mContext.getResources().getString(R.string.combination_end).length();
						style.setSpan(new StyleSpan(Typeface.BOLD), start, start+len, Spannable.SPAN_EXCLUSIVE_INCLUSIVE);
						start = start + len + (DateUtils.dateToString(DateUtils.FORMAT_SHORT, mCountListGroup.get(index).getEnd_date())+"\n").length();
						len = mContext.getResources().getString(R.string.combination_total_task).length();
						style.setSpan(new StyleSpan(Typeface.BOLD), start, start+len, Spannable.SPAN_EXCLUSIVE_INCLUSIVE);
						start = start + len +(mCountListGroup.get(index).getTotal_task_count()+mContext.getResources().getString(R.string.combination_piece)+"\n").length();
						len = (mContext.getResources().getString(R.string.combination_finished)+"：").length();
						style.setSpan(new StyleSpan(Typeface.BOLD), start, start+len, Spannable.SPAN_EXCLUSIVE_INCLUSIVE);
						start = start + len +(mCountListGroup.get(index).getFinished_count()+mContext.getResources().getString(R.string.combination_piece)+"\n").length();
						len = (mContext.getResources().getString(R.string.combination_starting)+"：").length();
						style.setSpan(new StyleSpan(Typeface.BOLD), start, start+len, Spannable.SPAN_EXCLUSIVE_INCLUSIVE);
						start = start + len +(mCountListGroup.get(index).getIn_progress_count()+mContext.getResources().getString(R.string.combination_piece)+"\n").length();
						len = (mContext.getResources().getString(R.string.combination_nostart)+"：").length();
						style.setSpan(new StyleSpan(Typeface.BOLD), start, start+len, Spannable.SPAN_EXCLUSIVE_INCLUSIVE);						
						itemContent.setText(style);						
					} else {
		                int rate = mCountListGroup.get(index).getFinish_rate();
						titleTextView.setText(dataSourceList.get(index).get("gridview_item_Text").toString());
						titleRate.setText(rate == 0 ? mContext.getResources().getString(R.string.combination_nostart) : 
							(rate < 100 ? mContext.getResources().getString(R.string.combination_starting) : 
								mContext.getResources().getString(R.string.combination_finished)));
						
		                String safetyuserName = UserCache.getNameById(mListGroup.get(index).getSafety_user());
		                String qualityuserName = UserCache.getNameById(mListGroup.get(index).getQuality_user());
						String str = mContext.getResources().getString(R.string.owner)+"："+ (userName == null ? "" : userName) +"\n"+
								mContext.getResources().getString(R.string.combination_safety) + (safetyuserName == null ? "" : safetyuserName) +"\n"+
								mContext.getResources().getString(R.string.combination_quality) + (qualityuserName == null ? "" : qualityuserName) +"\n\n"+
								mContext.getResources().getString(R.string.combination_period) + mCountListGroup.get(index).getPeriod()+"\n"+
								mContext.getResources().getString(R.string.combination_start)+DateUtils.dateToString(DateUtils.FORMAT_SHORT, mCountListGroup.get(index).getStart_date())+"\n"+
								mContext.getResources().getString(R.string.combination_end)+DateUtils.dateToString(DateUtils.FORMAT_SHORT, mCountListGroup.get(index).getEnd_date());
						SpannableStringBuilder style = new SpannableStringBuilder(str);
						int start = 0;
						int len = (mContext.getResources().getString(R.string.owner)+"：").length();
						style.setSpan(new StyleSpan(Typeface.BOLD), start, len, Spannable.SPAN_EXCLUSIVE_INCLUSIVE);
						start = len + ((userName == null ? "" : userName) +"\n").length();
						len = (mContext.getResources().getString(R.string.combination_safety)).length();
						style.setSpan(new StyleSpan(Typeface.BOLD), start, start+len, Spannable.SPAN_EXCLUSIVE_INCLUSIVE);
						start = start + len + ((safetyuserName == null ? "" : safetyuserName) +"\n").length();
						len = (mContext.getResources().getString(R.string.combination_quality)).length();
						style.setSpan(new StyleSpan(Typeface.BOLD), start, start+len, Spannable.SPAN_EXCLUSIVE_INCLUSIVE);
						start = start + len + ((qualityuserName == null ? "" : qualityuserName) +"\n\n").length();
						len = (mContext.getResources().getString(R.string.combination_period)).length();
						style.setSpan(new StyleSpan(Typeface.BOLD), start, start+len, Spannable.SPAN_EXCLUSIVE_INCLUSIVE);
						start = start + len + (mCountListGroup.get(index).getPeriod()+"\n").length();
						len = (mContext.getResources().getString(R.string.combination_start)).length();
						style.setSpan(new StyleSpan(Typeface.BOLD), start, start+len, Spannable.SPAN_EXCLUSIVE_INCLUSIVE);
						start = start + len +(DateUtils.dateToString(DateUtils.FORMAT_SHORT, mCountListGroup.get(index).getStart_date())+"\n").length();
						len = (mContext.getResources().getString(R.string.combination_end)).length();
						style.setSpan(new StyleSpan(Typeface.BOLD), start, start+len, Spannable.SPAN_EXCLUSIVE_INCLUSIVE);
						itemContent.setText(style);
					}
				}
			}
		}
	}	
	
	private void dataPreProcess() {
		Log.v("ccc","组信息预处理");
		for (int i = 0; i < mCountListGroup.size(); i++) {
			if (mCountListGroup.get(i) != null) {
				/*计算任务完成率*/
				if (mCountListGroup.get(i).getTotal_task_count() != 0) {
					mCountListGroup.get(i).setFinish_rate(
							mCountListGroup.get(i).getIn_progress_workload() / 
							(mCountListGroup.get(i).getTotal_task_count()));
				} else {
					mCountListGroup.get(i).setFinish_rate(0);
				}
				/*计算任务未开始数量*/
				mCountListGroup.get(i).setUndoing_count(
						mCountListGroup.get(i).getTotal_task_count() - 
						mCountListGroup.get(i).getIn_progress_count() - 
						mCountListGroup.get(i).getFinished_count());
			}
		}
	}
	
	private View setItemListener(View viewItem){
		if (viewItem != null) {
			TextView itemTitle = (TextView)viewItem.findViewById(R.id.item_title);
			itemTitle.setOnClickListener(new OnClickListener() {
				
				@Override
				public void onClick(final View arg0) {
//					Log.v("ccc","arg0="+arg0.getId());
					if (mItemMode == ITEM_MODE_NORMAL) {
						Intent intent = null;
						if (mType == TYPE_ZH) {
							intent = new Intent(mContext, CommonActivity.class);
						} else {
							intent = new Intent(mContext, CombinationScheduleActivity.class);
						}
						intent.putExtra(GROUP_KEY, mListGroup.get((int)arg0.getTag()-TAG_BASE));
						
						((Activity) mContext).startActivityForResult(intent, REFRESH_WINDOW);
					} else if (mItemMode == ITEM_MODE_EDIT) {
						Intent intent = new Intent(mContext, NodeCreatorActivity.class);
						intent.putExtra(NodeCreatorActivity.NODE_OPERATION_KEY, NodeCreatorActivity.NODE_MODIFY);
						intent.putExtra(NodeCreatorActivity.NODE_DATA_KEY, mListGroup.get((int)arg0.getTag()-TAG_BASE));
						intent.putExtra(NodeCreatorActivity.NODE_TYPE, mType);
		                Bundle bundle = new Bundle();
		        		Project project = new Project(); 
		        		project.setProject_id(mExpandCell.getProjectId());
		        		project.setTenant_id(mExpandCell.getTenantId());
		                bundle.putSerializable("project", (Serializable) project);
		                intent.putExtras(bundle);						
						((Activity) mContext).startActivityForResult(intent, REQUEST_NODE_MODIFY);
						
					} else if (mItemMode == ITEM_MODE_DELETE) {

				        UtilTools.deleteConfirm(((Activity) mContext), new UtilTools.DeleteConfirmInterface() {
				            
				            @Override
				            public void deleteConfirmCallback() {
				            	mDeletePostion = (int)arg0.getTag()-TAG_BASE;
				            	mGroupService.deleteGroupNode(mGroupManager, 
				            			mListGroup.get(mDeletePostion).getZh_group_id());
				            }
				        });
					}
				}					
			});
			
			OnClickListener listener = new OnClickListener() {
				
				@Override
				public void onClick(View arg0) {
					mItemMode = ITEM_MODE_NORMAL;
					updateAllItemView(mScrollViewLinearLayout);					
				}
			};	
			viewItem.setOnClickListener(listener);
			mHorizontalScrollViewFather.setOnClickListener(listener);
		}
		
		return viewItem;
	}

	private int mItemMode = ITEM_MODE_NORMAL;
	private static int ITEM_MODE_NORMAL = 0; 
	private static int ITEM_MODE_EDIT = 1; 
	private static int ITEM_MODE_DELETE = 2;
	private View setItemViewMode(View viewItem, int viewMode){
		if (viewItem != null) {
			ImageView itemModeIconView = (ImageView) viewItem.findViewById(R.id.item_mode_icon);
			
			if(viewMode == ITEM_MODE_NORMAL) {
				Log.v("chen","setItemViewMode 1");
				/*正常模式*/
				itemModeIconView.setVisibility(View.GONE);
			} else if (viewMode == ITEM_MODE_EDIT) {
				Log.v("chen","setItemViewMode 2");
				/*编辑模式*/
				itemModeIconView.setImageResource(R.drawable.edit_icon);
				itemModeIconView.setVisibility(View.VISIBLE);
			} else if (viewMode == ITEM_MODE_DELETE) {
				/*删除模式*/Log.v("chen","setItemViewMode 3");
				itemModeIconView.setImageResource(R.drawable.delete_icon);
				itemModeIconView.setVisibility(View.VISIBLE);
			}
		}		
		
		return viewItem;
	}	
	
	private void updateAllItemView(LinearLayout linearLayoutView) {
		Log.v("ccc","更新所以节点View的显示模式");
		int size = linearLayoutView.getChildCount();
		if (linearLayoutView.getChildCount() > 0) {
			for (int i = 0; i < size; i++) {
				setItemViewMode(linearLayoutView.getChildAt(i), mItemMode);
			}
		}	
	}
	
	public void doActivityResult(int requestCode, int resultCode, Intent data) {
		switch (requestCode) {
		      case REQUEST_NODE_ADD:
		    	  if (data != null) {
			      	@SuppressWarnings("unchecked")
					List<ZH_group> listGroup = (List<ZH_group>)data.getSerializableExtra("group_list");
			      	for (ZH_group group : listGroup) {
			      		group.setProject_id(mExpandCell.getProjectId());
			      		group.setTenant_id(mExpandCell.getTenantId());
			      	}
			      	mGroupService.addGroupNode(mGroupManager, listGroup);
		    	  }
		          break;
		      case REQUEST_NODE_MODIFY:
		    	  if (data != null) {
					mUpdateGroup = (ZH_group)data.getSerializableExtra("group");
					for (int i = 0 ; i < mListGroup.size(); i++) {
						if (mListGroup.get(i).getZh_group_id() == mUpdateGroup.getZh_group_id()) {
							mListGroup.get(i).setNode_name(mUpdateGroup.getNode_name());
							if (mType == TYPE_PLAN) {
								mListGroup.get(i).setQuality_user(mUpdateGroup.getQuality_user());
								mListGroup.get(i).setSafety_user(mUpdateGroup.getSafety_user());
							} else {
								mListGroup.get(i).setNode_module(mUpdateGroup.getNode_module());
							}
						}
					}
					mGroupService.updateGroupNode(mGroupManager, mListGroup);
		    	  }
		          break;
        	case REFRESH_WINDOW:
				mIsReceiveGroupInfo = false;
				if (mExpandCell != null) {
					mGroupService.getGroup(mGroupManager, mExpandCell.getProjectId());
				} else {
					loadData();
				}
				
	        	break;		          
	    }		
	}
	
//    @SuppressWarnings("unchecked")
//	@Override
//    public void onActivityResult(int requestCode, int resultCode, Intent data) {
//        super.onActivityResult(requestCode, resultCode, data);
//        if (resultCode == Activity.RESULT_OK) {
//            switch (requestCode) {
//                case REQUEST_NODE_ADD:
//                	List<ZH_group> listGroup = (List<ZH_group>)data.getSerializableExtra("group_list");
//                	for (ZH_group group : listGroup) {
//                		group.setProject_id(mExpandCell.getProjectId());
//                		group.setTenant_id(mExpandCell.getTenantId());
//                	}
//                	mGroupService.addGroupNode(mGroupManager, listGroup);               	
//                    break;
//                case REQUEST_NODE_MODIFY:
//                	mUpdateGroup = (ZH_group)data.getSerializableExtra("group");
//                	for (int i = 0 ; i < mListGroup.size(); i++) {
//                		if (mListGroup.get(i).getZh_group_id() == mUpdateGroup.getZh_group_id()) {
//                			mListGroup.get(i).setNode_name(mUpdateGroup.getNode_name());
//                			mListGroup.get(i).setNode_module(mUpdateGroup.getNode_module());
//                		}
//                	}
//                	mGroupService.updateGroupNode(mGroupManager, mListGroup);
//                    break;
//            }
//        }
//        
//        if (resultCode == Activity.RESULT_CANCELED) {
//        	switch (requestCode) {
//        	case REFRESH_WINDOW:
//				mIsReceiveGroupInfo = false;
//				if (mExpandCell != null) {
//					mGroupService.getGroup(mGroupManager, mExpandCell.getProjectId());
//				} else {
//					loadData();
//				}
//				
//            	break;
//        	}
//        }
//    }
	
	public boolean doBackPressed() {
		if (mItemMode != ITEM_MODE_NORMAL) {
			mItemMode = ITEM_MODE_NORMAL;
			updateAllItemView(mScrollViewLinearLayout);
			return false;
		} else {
			return true;
		}
	}
}
