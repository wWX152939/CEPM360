package com.pm360.cepm360.app.module.common.plan;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.Dialog;
import android.content.res.TypedArray;
import android.graphics.Color;
import android.util.TypedValue;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.LinearLayout;
import android.widget.LinearLayout.LayoutParams;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.pm360.cepm360.R;
import com.pm360.cepm360.app.cache.UserCache;
import com.pm360.cepm360.app.common.activity.DataManagerInterface;
import com.pm360.cepm360.app.common.adpater.DataListAdapter;
import com.pm360.cepm360.app.common.adpater.DataListAdapter.ListAdapterInterface;
import com.pm360.cepm360.app.common.adpater.DataListAdapter.ViewHolder;
import com.pm360.cepm360.app.common.view.parent.BaseDialogStyle;
import com.pm360.cepm360.app.utils.UtilTools;
import com.pm360.cepm360.common.GLOBAL;
import com.pm360.cepm360.common.ResultStatus;
import com.pm360.cepm360.common.json.AnalysisManager;
import com.pm360.cepm360.entity.Templet_WBS;
import com.pm360.cepm360.services.common.RemoteCommonService;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class TemplateDialog extends BaseDialogStyle {
	private View mRootView;
	private View mListLayout;
	
	// 列表头显示名称
	private String[] mListHeadNames;
	private int[] mDisplayItemIds;
	private ListView mTemplet_WBSListView;
	private DataListAdapter<Templet_WBS> mAdapter;
	private Templet_WBS mCurrentItem;
	private TemplateDialogInterface mTemplateDialogInterface;
	private Dialog mProgressDialog;
	
	public TemplateDialog(Activity activity, final int type, final int id, TemplateDialogInterface templateDialogInterface) {
		super(activity);
		mTemplateDialogInterface = templateDialogInterface;
		mRootView = init(R.layout.plan_template_fragment);
		setTitleName(mActivity.getString(R.string.new_add));
		setFirstButtonVisible();
		setButton(0, mActivity.getString(R.string.import_data), new OnClickListener() {
			
			@Override
			public void onClick(View v) {
				if (mCurrentItem == null) {
					Toast.makeText(mActivity, R.string.template_select, Toast.LENGTH_SHORT).show();
					return;
				}
				String enterpriseType = "";
				if (type == 1) {
					enterpriseType = GLOBAL.ENTERPRISE_TYPE[0][0];
				} else {
					enterpriseType = GLOBAL.ENTERPRISE_TYPE[2][0];
				}
				mProgressDialog = UtilTools.showProgressDialog(mActivity, true, false);
				RemoteCommonService.getInstance().importWBS(mManager, mCurrentItem.getTemplet_wbs_id(), id,
						mCurrentItem.getTenant_id(), enterpriseType);
			}
		});
		setButton(1, mActivity.getString(R.string.new_add), new OnClickListener() {
			
			@Override
			public void onClick(View v) {
				dismiss();
				mTemplateDialogInterface.createTask();
			}
		});
		initManagerListView();
		show();
	}
	
	@Override
	public void show() {
		super.show();
		loadData();
	}
	
	private DataManagerInterface mManager = new DataManagerInterface() {

		@SuppressWarnings("unchecked")
		@Override
		public void getDataOnResult(ResultStatus status, List<?> list) {
            if (status.getCode() != AnalysisManager.SUCCESS_DB_QUERY) {
            	Toast.makeText(mActivity, status.getMessage(), Toast.LENGTH_SHORT).show();
            }
            if (mProgressDialog != null && mProgressDialog.isShowing()) {
            	mProgressDialog.dismiss();
            }
			
			switch (status.getCode()) {
			case AnalysisManager.SUCCESS_DB_QUERY:
				if (list != null && list.size() > 0)
					mAdapter.setShowDataList((List<Templet_WBS>) list);
				break;
			case AnalysisManager.SUCCESS_DB_ADD:
				dismiss();
				mTemplateDialogInterface.addTemplateSucc();
				break;
			default:
				break;
			}
		}
	};
	
	private void loadData() {
		RemoteCommonService.getInstance().getWBSTemplet(mManager, UserCache.getCurrentUser());
	}
	
	private void initManagerListView() {
		View view = mRootView.findViewById(R.id.root_view);
		LinearLayout.LayoutParams params = (LayoutParams) view.getLayoutParams();
		params.setMargins(0, 0, 0, 0);
		view.setLayoutParams(params);
		// 库存列表头布局
		mListLayout = mRootView.findViewById(R.id.plan_template_listhead);
		// 获取列表使用的相关资源
		mListHeadNames = mActivity.getResources().getStringArray(
				R.array.plantemplate_names);
		TypedArray typedArray = mActivity.getResources().obtainTypedArray(
				R.array.plantemplate_ids);

		if (mListHeadNames != null) {
			mDisplayItemIds = new int[typedArray.length()];
			for (int i = 0; i < mDisplayItemIds.length; i++) {
				mDisplayItemIds[i] = typedArray.getResourceId(i, 0);
				if (i < mListHeadNames.length) {
					// 配置列表头项相关字段
					TextView tv = (TextView) mListLayout
							.findViewById(mDisplayItemIds[i]);

					tv.setTextSize(TypedValue.COMPLEX_UNIT_PX, mActivity.getResources().getDimensionPixelSize(R.dimen.sp17_s));
					tv.setTextColor(Color.BLACK);
					tv.setText(mListHeadNames[i]);
				}
			}
		}
		
		typedArray.recycle();

		// 库存列表
		mTemplet_WBSListView = (ListView) mRootView
				.findViewById(R.id.plan_template_listview);
		mTemplet_WBSListView.setBackgroundColor(Color.WHITE);
		mTemplet_WBSListView.setOnItemClickListener(new OnItemClickListener() {

			@Override
			public void onItemClick(AdapterView<?> adapterView, View view,
					int position, long id) {
				mAdapter.setSelected(position, true);
				mTemplet_WBSListView.setSelection(position);
			}
		});
		mAdapter = new DataListAdapter<Templet_WBS>(mActivity, mDataListAdapter);
		mTemplet_WBSListView.setAdapter(mAdapter);
	}
	
	ListAdapterInterface<Templet_WBS> mDataListAdapter = new DataListAdapter.ListAdapterInterface<Templet_WBS>() {
		@Override
		public int getLayoutId() {
			return R.layout.plan_template_list_item;
		}

		@Override
		public View getHeaderView() {
			return mListLayout;
		}

		@Override
		public void regesterListeners(ViewHolder viewHolder, final int position) {
			
		}

		@Override
		public void initListViewItem(View convertView, ViewHolder holder,
				DataListAdapter<Templet_WBS> adapter, final int position) {

			Map<String, String> listViewItem = beanToMap(adapter.getItem(position));
			for (int i = 0; i < mListHeadNames.length; i++) {
				if (i == 0){
					holder.tvs[0].setText(position+1+"");
				}else if (i == 2){
					holder.tvs[i].setText(GLOBAL.WBS_TYPE[Integer.parseInt(listViewItem.get(mListHeadNames[i]))-1][1]);
				} else {
					holder.tvs[i].setText(listViewItem.get(mListHeadNames[i]));
				}
				holder.tvs[i].setTextColor(Color.BLACK);
				holder.tvs[i].setClickable(false);
				holder.tvs[i].setOnClickListener(new View.OnClickListener() {

					@Override
					public void onClick(View view) {

						mCurrentItem = mAdapter.getItem(position);
							
						mAdapter.setSelected(position, true);
					}
				});
			}

			// 将选中的列表项高亮
			if (mAdapter.getSelectedList().contains((Integer) position)) {
				convertView.setBackgroundResource(R.color.touch_high_light);
			} else {
				convertView.setBackgroundColor(Color.TRANSPARENT);
			}
		}
		
		
		@SuppressLint("SimpleDateFormat")
		private Map<String, String> beanToMap(Templet_WBS bean) {
			Map<String, String> mapItem = new HashMap<String, String>();

			mapItem.put(mListHeadNames[0], ((Templet_WBS) bean).getTemplet_wbs_id()+ "");
			mapItem.put(mListHeadNames[1], ((Templet_WBS) bean).getName());
			mapItem.put(mListHeadNames[2], ((Templet_WBS) bean).getType()+ "");

			return mapItem;
		}

		@Override
		public void initLayout(View convertView, ViewHolder holder) {
			holder.tvs = new TextView[mDisplayItemIds.length];
			for (int i = 0; i < mDisplayItemIds.length; i++) {
				holder.tvs[i] = (TextView) convertView
						.findViewById(mDisplayItemIds[i]);
			}
		}

		@SuppressWarnings({ "unchecked", "rawtypes" })
		@Override
		public List findByCondition(Object... condition) {
			return null;
		}

		@Override
		public boolean isSameObject(Templet_WBS t1, Templet_WBS t2) {
			return false;
		}
	};
	
	public static interface TemplateDialogInterface {
		public void addTemplateSucc();
		public void createTask();
	}
	
}
