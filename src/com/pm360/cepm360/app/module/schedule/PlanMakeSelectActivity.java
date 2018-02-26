package com.pm360.cepm360.app.module.schedule;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Intent;
import android.content.res.TypedArray;
import android.graphics.Color;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.text.TextPaint;
import android.util.TypedValue;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.pm360.cepm360.R;
import com.pm360.cepm360.app.common.activity.DataManagerInterface;
import com.pm360.cepm360.app.common.adpater.DataTreeListAdapter;
import com.pm360.cepm360.app.common.adpater.DataTreeListAdapter.ViewHolder;
import com.pm360.cepm360.app.utils.TaskUtil;
import com.pm360.cepm360.common.ResultStatus;
import com.pm360.cepm360.common.json.AnalysisManager;
import com.pm360.cepm360.common.util.LogUtil;
import com.pm360.cepm360.entity.ZH_group_task;

import java.io.Serializable;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class PlanMakeSelectActivity extends Activity {
	public static final String NODE_CODE = "zh_node_code";
	public static final String TASK_CODE = "task_code";
	public static final String MULTI_MODE_CODE = "MULTI_MODE_CODE";
	
	private int mZHGroupId;
	private boolean mIsMultiMode;
	
	
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		Intent intent = getIntent();
		mZHGroupId = intent.getIntExtra(NODE_CODE, 0);
		mIsMultiMode = intent.getBooleanExtra(MULTI_MODE_CODE, false);
		
		setContentView(R.layout.plan_make_select_activity);
		initPlanMakeWindow();
		initButtons();
		loadData();
	}
	
	private View mCurrentView;
	private String[] mDisplayItems;
	private int[] mItemIds;
	private ListView mListView;
	private View mHeaderView;
	private DataTreeListAdapter<ZH_group_task> mAdapter;
	@SuppressLint("ResourceAsColor") private void initPlanMakeWindow() {
		ViewGroup parent = (ViewGroup)findViewById(R.id.list_view);
		mCurrentView = LayoutInflater.from(this).inflate(
				R.layout.task_select_activity, parent, true);
		mDisplayItems = getResources().getStringArray(R.array.plan_make_names);

		mHeaderView = (View) mCurrentView
				.findViewById(R.id.task_select_listhead);
		mHeaderView.setBackgroundColor(getResources().getColor(R.color.task_home_list_title));
		mListView = (ListView) mCurrentView
				.findViewById(R.id.task_select_listview);

		TypedArray typedArray = getResources().obtainTypedArray(
				R.array.plan_make_ids);
		if (mDisplayItems != null) {
			int itemLength = mDisplayItems.length;
			mItemIds = new int[itemLength];
			for (int i = 0; i < itemLength; i++) {

				mItemIds[i] = typedArray.getResourceId(i, 0);
				TextView tv = (TextView) mHeaderView.findViewById(mItemIds[i]);

				tv.setTextColor(Color.BLACK);
				tv.setText(mDisplayItems[i]);
			}
		}
		typedArray.recycle();
		
		mAdapter = new DataTreeListAdapter<ZH_group_task>(getBaseContext(), null, true, 
				mListAdapterManager, 0, DataTreeListAdapter.BACKGROUND_TYPE_PLAN);

		mAdapter.addCHScrollView(null, mHeaderView);
		mListView.setAdapter(mAdapter);
	}
	
	private void initButtons() {
		Button save = (Button) findViewById(R.id.save_Button);
		Button cancel = (Button) findViewById(R.id.exit_Button);
		ImageView mClose  = (ImageView)findViewById(R.id.btn_close);
		save.setText(R.string.confirm);
		save.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View v) {
				Intent intent = new Intent();
		        Bundle bundle = new Bundle();
				if (mIsMultiMode) {
					bundle.putSerializable(TASK_CODE, (Serializable) mAdapter.getSelectedData());
				} else {
			        bundle.putSerializable(TASK_CODE, mCurrentItem);
				}
		        intent.putExtras(bundle);
		        setResult(Activity.RESULT_OK, intent);
				finish();
			}
		});
		cancel.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View v) {
				finish();
			}
		});
		mClose.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				finish();
			}
		});
		
	}
	
	private void loadData() {
		com.pm360.cepm360.services.group.RemoteTaskService.getInstance().getPublishTaskList(mDataManager, mZHGroupId);
	}
	
	@SuppressLint("HandlerLeak") private Handler mToastHandler = new Handler() {
		public void handleMessage(Message msg) {
			Toast.makeText(PlanMakeSelectActivity.this,
					(CharSequence) msg.obj, Toast.LENGTH_SHORT).show();
		}
	};
	
	private DataManagerInterface mDataManager = new DataManagerInterface() {

		@SuppressWarnings("unchecked")
		@Override
		public void getDataOnResult(ResultStatus status, List<?> list) {
			if (status.getCode() != AnalysisManager.SUCCESS_DB_QUERY) {
				Message msg = new Message();
				msg.obj = status.getMessage();
				mToastHandler.sendMessage(msg);
			}
			
			LogUtil.d("wzw status:" + status + "list:" + list);
			switch (status.getCode()) {
			case AnalysisManager.SUCCESS_DB_QUERY:
				if (list != null && list.size() != 0) {
					mAdapter.setDataList((List<ZH_group_task>) list);
				}
				break;

			default:
				break;
			}
		}

	};
	
	private ZH_group_task mCurrentItem;
	private DataTreeListAdapter.TreeListAdapterInterface mListAdapterManager = new DataTreeListAdapter.TreeListAdapterInterface() {

		@Override
		public int getLayoutId() {
			return R.layout.task_make_list_item2;
		}

		@Override
		public void initListViewItem(ViewHolder holder, int position) {
			Map<String, String> listViewItem = beanToMap(mAdapter.getItem(position));
			for (int i = 0; i < mDisplayItems.length; i++) {
				holder.tvs[i].setText(listViewItem.get(mDisplayItems[i]));
				holder.tvs[i].setTextColor(Color.BLACK);
			}

			if (mAdapter.getItem(position).isHas_child() && !mAdapter.getItem(position).isExpanded()) {
                holder.ivs[0].setImageResource(R.drawable.item_collapse);
            } else if (mAdapter.getItem(position).isHas_child() && mAdapter.getItem(position).isExpanded()) {
                holder.ivs[0].setImageResource(R.drawable.item_expand);
            } else if (!mAdapter.getItem(position).isHas_child()){
                holder.ivs[0].setImageResource(R.drawable.item_collapse);
            }
			
		}

		@Override
		public void regesterListeners(ViewHolder viewHolder, final int position) {
			viewHolder.tvs[0].setOnClickListener(new View.OnClickListener() {
				@Override
				public void onClick(View view) {
					mAdapter.updateListView(position);
				}
			});
			for (int i = 1; i < viewHolder.tvs.length; i++) {
				viewHolder.tvs[i].setOnClickListener(new View.OnClickListener() {
					@Override
					public void onClick(View view) {
						if (mIsMultiMode) {
							mAdapter.setPickSelectedInUpdateMode(position);
						} else {
							mAdapter.setSelected(position, true);
		                    mCurrentItem = mAdapter.getItem(position);
						}
					}
				});
			}
		}

		@Override
		public void initLayout(View convertView, ViewHolder holder) {
			holder.tvs = new TextView[mItemIds.length];
			for (int i = 0; i < mItemIds.length; i++) {
				holder.tvs[i] = (TextView) convertView
						.findViewById(mItemIds[i]);
			}
			holder.ivs = new ImageView[1];
            holder.ivs[0] = (ImageView) convertView.findViewById(R.id.icon);
		}

		@Override
		public void calculateContentItemCount() {
			
		}
		
	};
	
	@SuppressLint("UseSparseArrays") private Map<String, String> beanToMap(Object bean) {
		Map<String, String> mapItem = new HashMap<String, String>();
		if (bean instanceof ZH_group_task) {
			ZH_group_task task = (ZH_group_task)bean;
			Map<Integer, String> mOBSMap2 = new HashMap<Integer, String>();
			Map<Integer, String> mUserMap2 = new HashMap<Integer, String>();
			mapItem = TaskUtil.taskToMap(mOBSMap2, mUserMap2, mDisplayItems, task);
		}
		return mapItem;
	}
	
}
