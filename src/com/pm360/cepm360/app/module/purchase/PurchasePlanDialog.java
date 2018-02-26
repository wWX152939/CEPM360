package com.pm360.cepm360.app.module.purchase;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.res.TypedArray;
import android.graphics.Color;
import android.os.Handler;
import android.os.Message;
import android.util.TypedValue;
import android.view.Gravity;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.pm360.cepm360.R;
import com.pm360.cepm360.app.cache.UserCache;
import com.pm360.cepm360.app.common.activity.DataManagerInterface;
import com.pm360.cepm360.app.common.adpater.DataListAdapter;
import com.pm360.cepm360.app.common.adpater.DataListAdapter.ViewHolder;
import com.pm360.cepm360.app.common.view.parent.BaseDialogStyle;
import com.pm360.cepm360.common.GLOBAL;
import com.pm360.cepm360.common.ResultStatus;
import com.pm360.cepm360.common.json.AnalysisManager;
import com.pm360.cepm360.common.util.DateUtils;
import com.pm360.cepm360.common.util.LogUtil;
import com.pm360.cepm360.entity.P_CG;
import com.pm360.cepm360.entity.P_CGD;
import com.pm360.cepm360.entity.P_CGJH;
import com.pm360.cepm360.entity.P_CGJHD;
import com.pm360.cepm360.services.purchase.RemotePlanService;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class PurchasePlanDialog {
	private Activity mActivity;
	private PlanDialogInterface mPlanDialogInterface;
	public PurchasePlanDialog(Activity activity, PlanDialogInterface planDialogInterface) {
		mActivity = activity;
		mPlanDialogInterface = planDialogInterface;
	}
	
	public void show() {
		mDialogCurrentItem = null;
		mPlanDialog.show(1000, 0);
		loadDialogListViewData();
	}
	
	private List<P_CG> mCGList;
	public void show(List<P_CG> cgList) {
		mCGList = cgList;
		mDialogCurrentItem = null;
		mPlanDialog.show(1000, 0);
		loadDialogListViewData();
	}

	private void loadDialogListViewData() {
		RemotePlanService.getInstance().getCGJHList(
				mDialogDataManager,
				UserCache.getCurrentUser()
						.getTenant_id(), 0, 0);
	}

	private BaseDialogStyle mPlanDialog;
	private View mDialogView;

	public void initPlanDialog() {
		mPlanDialog = new BaseDialogStyle(mActivity);
		mDialogView = mPlanDialog.init(R.layout.purchase_plan_fragment);
		
		initDialogListView();
		
		OnClickListener listener = (new OnClickListener() {
			
			@Override
			public void onClick(View v) {
				if (mDialogCurrentItem != null) {
					RemotePlanService.getInstance().getCGJHD(mP_JHDDataManager, mDialogCurrentItem.getCgjh_id());
				}
			}
		});
		mPlanDialog.setButton(null, listener);
	}

	private View mDialogListLayout;
	private ListView mDialogListView;
	private DataListAdapter<P_CGJH> mDialogAdapter;
	private String[] mDialogListHeadNames;
	private int[] mDialogDisplayItemIds;
	@SuppressWarnings("unchecked")
	private void initDialogListView() {
		View frame = mDialogView.findViewById(R.id.purchase_plan_id);
		LinearLayout.LayoutParams params = (android.widget.LinearLayout.LayoutParams) frame.getLayoutParams();
		params.leftMargin = 0;
		params.rightMargin = 0;
		frame.setLayoutParams(params);
		// 列表头布局
		mDialogListLayout = mDialogView.findViewById(R.id.purchase_plan_listhead);
		// 获取列表使用的相关资源
		mDialogListHeadNames = mActivity.getResources().getStringArray(
				R.array.purchase_plan_names);
		TypedArray typedArray = mActivity.getResources().obtainTypedArray(
				R.array.purchase_plan_ids);
		if (mDialogListHeadNames != null) {
			mDialogDisplayItemIds = new int[typedArray.length()];
			for (int i = 0; i < mDialogDisplayItemIds.length; i++) {
				mDialogDisplayItemIds[i] = typedArray.getResourceId(i, 0);
				if (i < mDialogListHeadNames.length) {
					// 配置列表头项相关字段
					TextView tv = (TextView) mDialogListLayout
							.findViewById(mDialogDisplayItemIds[i]);
					tv.setTextSize(TypedValue.COMPLEX_UNIT_PX, mActivity.getResources().getDimensionPixelSize(R.dimen.sp17_s));
					tv.setTextColor(Color.WHITE);
					tv.setText(mDialogListHeadNames[i]);
					tv.setGravity(Gravity.CENTER);
					tv.setPadding(0, 0, 0, 0);
				}
			}
		}

		typedArray.recycle();

		mDialogListView = (ListView) mDialogView
				.findViewById(R.id.purchase_plan_listview);

		mDialogAdapter = new DataListAdapter<P_CGJH>(mActivity.getBaseContext(),
				mDialogListAdapterManager);
		mDialogListView.setAdapter(mDialogAdapter);
		mDialogListView.setBackgroundColor(Color.WHITE);
	}
	
	@SuppressLint("HandlerLeak") private Handler mToastHandler = new Handler() {
		public void handleMessage(Message msg) {
			Toast.makeText(mActivity,
					(CharSequence) msg.obj, Toast.LENGTH_SHORT).show();
		}
	};
	
	private void filterCGList(List<P_CGJH> list) {
		for (int i = 0; i < mCGList.size(); i++) {
			for (int j = 0; j < list.size(); j++) {
				if (mCGList.get(i).getCgjh_id() == list.get(j).getCgjh_id()) {	
					list.remove(j);
					break;
				}
			}
			
		}
	}
	
	private DataManagerInterface mDialogDataManager = new DataManagerInterface() {

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
					filterCGList((List<P_CGJH>)list);
					mDialogAdapter.setShowDataList((List<P_CGJH>) list);
				}
				break;

			default:
				break;
			}
		}

	};
	
	private DataManagerInterface mP_JHDDataManager = new DataManagerInterface() {

		@SuppressWarnings("unchecked")
		@Override
		public void getDataOnResult(ResultStatus status, List<?> list) {
			if (status.getCode() != AnalysisManager.SUCCESS_DB_QUERY) {
				Message msg = new Message();
				msg.obj = status.getMessage();
				mToastHandler.sendMessage(msg);
			}

			switch (status.getCode()) {
			case AnalysisManager.SUCCESS_DB_QUERY:
				if (list != null && list.size() != 0) {
					List<P_CGJHD> listJHD = (List<P_CGJHD>) list;
					List<P_CGD> cgdList = new ArrayList<P_CGD>();
					for (int i = 0; i < listJHD.size(); i++) {
						P_CGD cgd = new P_CGD();
						cgd.setWz_id(listJHD.get(i).getWz_id());
						cgd.setCgjh_id(listJHD.get(i).getCgjh_id());
						cgd.setCgjhd_id(listJHD.get(i).getCgjhd_id());
						cgd.setWz_name(listJHD.get(i).getWz_name()); 
						cgd.setWz_type_1(listJHD.get(i).getWz_type_1());
						cgd.setWz_type_2(listJHD.get(i).getWz_type_2());
						cgd.setWz_brand(listJHD.get(i).getWz_brand());
						cgd.setWz_spec(listJHD.get(i).getWz_brand());
						cgd.setWz_unit(listJHD.get(i).getWz_unit());
						cgd.setCg_quantity(listJHD.get(i).getQuantity());
						cgd.setProject_id(mDialogCurrentItem.getProject_id());
						cgd.setTenant_id(mDialogCurrentItem.getTenant_id());
						//cgd.set
						cgd.setLwdw_name(listJHD.get(i).getLwdw_name());
						cgd.setUnit_price(listJHD.get(i).getUnit_price());
						cgd.setMoney(listJHD.get(i).getMoney());
						cgdList.add(cgd);
					}
					mPlanDialogInterface.setData(cgdList, mDialogCurrentItem);
					mPlanDialog.dismiss();
					//mListAdapter.addShowDataList(listJHD);
				} else {
					Message msg = new Message();
					msg.obj = mActivity.getString(R.string.purchase_plan_is_null);
					mToastHandler.sendMessage(msg);
				}
				break;

			default:
				break;
			}
		}

	};
	
	private P_CGJH mDialogCurrentItem;
	@SuppressWarnings("rawtypes")
	private DataListAdapter.ListAdapterInterface mDialogListAdapterManager = new DataListAdapter.ListAdapterInterface() {
		@Override
		public int getLayoutId() {
			return R.layout.purchase_plan_title_list_item;
		}

		@Override
		public View getHeaderView() {
			return mDialogListLayout;
		}

		@Override
		public void regesterListeners(ViewHolder viewHolder, final int position) {
			for (int i = 0; i < viewHolder.tvs.length; i++) {
				viewHolder.tvs[i].setOnClickListener(new View.OnClickListener() {
					@Override
					public void onClick(View view) {
						mDialogAdapter.setSelected(position, true);
						
	                    mDialogCurrentItem = mDialogAdapter.getItem(position);

					}
				});
			}
		}

		@Override
		public void initLayout(View convertView, ViewHolder holder) {
			holder.tvs = new TextView[mDialogDisplayItemIds.length];
			for (int i = 0; i < mDialogDisplayItemIds.length; i++) {
				holder.tvs[i] = (TextView) convertView
						.findViewById(mDialogDisplayItemIds[i]);
			}
		}

		@Override
		public List<?> findByCondition(Object... condition) {
			return null;
		}
		
		@Override
		public boolean isSameObject(Object t1, Object t2) {
			return false;
		}

		@Override
		public void initListViewItem(View convertView, ViewHolder holder,
				DataListAdapter adapter, int position) {
			Map<String, String> listViewItem = dialogBeanToMap(adapter.getItem(position));
			for (int i = 0; i < mDialogListHeadNames.length; i++) {
				holder.tvs[i].setText(listViewItem.get(mDialogListHeadNames[i]));
				holder.tvs[i].setTextColor(Color.BLACK);
			}

			// 将选中的列表项高亮
			if (mDialogAdapter.getSelectedList().contains((Integer) position)) {
				convertView.setBackgroundResource(R.color.touch_high_light);
			} else {
				convertView.setBackgroundColor(Color.TRANSPARENT);
			}
			
		}
	};
	
	private Map<String, String> dialogBeanToMap(Object bean) {
		Map<String, String> mapItem = new HashMap<String, String>();
		if (bean instanceof P_CGJH) {
			P_CGJH cgjh = (P_CGJH)bean;
			int count = 0;

			mapItem.put(mDialogListHeadNames[count++], cgjh.getCgjh_number());
			mapItem.put(mDialogListHeadNames[count++], cgjh.getCgjh_name());
			mapItem.put(mDialogListHeadNames[count++], cgjh.getProject_name());
			mapItem.put(mDialogListHeadNames[count++],
					Double.toString(cgjh.getTotal_money()));
			mapItem.put(mDialogListHeadNames[count++],
					UserCache.getUserMaps().get(Integer.toString(cgjh.getPlan_person())));
			mapItem.put(mDialogListHeadNames[count++],
					UserCache.getUserMaps().get(Integer.toString(cgjh.getExecute_person())));
			mapItem.put(
					mDialogListHeadNames[count++],
					DateUtils.dateToString(DateUtils.FORMAT_SHORT,
							cgjh.getReport_date()));
			mapItem.put(
					mDialogListHeadNames[count++],
					DateUtils.dateToString(DateUtils.FORMAT_SHORT,
							cgjh.getPlan_date()));
			int status = cgjh.getStatus();
			if (status == 0) {
				status = 1;
			}
			mapItem.put(mDialogListHeadNames[count++], 
					GLOBAL.FLOW_APPROVAL_STATUS[status - 1][1]);
			}
		return mapItem;
	}
	

    public interface PlanDialogInterface {
    	void setData(List<P_CGD> cgdList, P_CGJH cgjh);
    }

}
