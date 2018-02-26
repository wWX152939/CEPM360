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
import com.pm360.cepm360.app.utils.UtilTools;
import com.pm360.cepm360.common.GLOBAL;
import com.pm360.cepm360.common.ResultStatus;
import com.pm360.cepm360.common.json.AnalysisManager;
import com.pm360.cepm360.common.util.DateUtils;
import com.pm360.cepm360.common.util.LogUtil;
import com.pm360.cepm360.entity.P_CGJHD;
import com.pm360.cepm360.entity.P_CGYS;
import com.pm360.cepm360.entity.P_CGYSD;
import com.pm360.cepm360.services.purchase.RemoteBudgetService;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class PurchaseBudgetDialog {
	private Activity mActivity;
	private BudgetDialogInterface mBudgetDialogInterface;
	public PurchaseBudgetDialog(Activity activity, BudgetDialogInterface budgetDialogInterface) {
		mActivity = activity;
		mBudgetDialogInterface = budgetDialogInterface;
	}
	
	public void show() {
		mDialogCurrentItem = null;
		mBudgetDialog.show(1000, 0);
		loadDialogListViewData();
	}

	private void loadDialogListViewData() {
		RemoteBudgetService.getInstance().getCGYSList(
				mDialogDataManager,
				UserCache.getCurrentUser()
						.getTenant_id(), 0, 0);
	}

	private BaseDialogStyle mBudgetDialog;
	private View mDialogView;

	public void initBudgetDialog() {
		mBudgetDialog = new BaseDialogStyle(mActivity);
		mDialogView = mBudgetDialog.init(R.layout.purchase_budget_fragment);
		initDialogListView();
		
		OnClickListener listener = new OnClickListener() {
			
			@Override
			public void onClick(View v) {
				if (mDialogCurrentItem != null) {
					RemoteBudgetService.getInstance().getCGYSD(mP_YSDDataManager, mDialogCurrentItem.getCgys_id());
				}
			}
		};
		mBudgetDialog.setButton(null, listener);
	}

	private View mDialogListLayout;
	private ListView mDialogListView;
	private DataListAdapter<P_CGYS> mDialogAdapter;
	private String[] mDialogListHeadNames;
	private int[] mDialogDisplayItemIds;
	@SuppressWarnings("unchecked")
	private void initDialogListView() {
		View frame = mDialogView.findViewById(R.id.purchase_budget_id);
		LinearLayout.LayoutParams params = (android.widget.LinearLayout.LayoutParams) frame.getLayoutParams();
		params.leftMargin = 0;
		params.rightMargin = 0;
		frame.setLayoutParams(params);
		// 列表头布局
		mDialogListLayout = mDialogView.findViewById(R.id.purchase_manager_listhead);
		// 获取列表使用的相关资源
		mDialogListHeadNames = mActivity.getResources().getStringArray(
				R.array.purchase_budget_names);
		TypedArray typedArray = mActivity.getResources().obtainTypedArray(
				R.array.purchase_budget_ids);
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
				.findViewById(R.id.purchase_budget_listview);

		mDialogAdapter = new DataListAdapter<P_CGYS>(mActivity.getBaseContext(),
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
					mDialogAdapter.setShowDataList((List<P_CGYS>) list);
				}
				break;

			default:
				break;
			}
		}

	};
	
	private DataManagerInterface mP_YSDDataManager = new DataManagerInterface() {

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
					List<P_CGYSD> listYSD = (List<P_CGYSD>) list;
					List<P_CGJHD> listJHD = new ArrayList<P_CGJHD>();
					for (int i = 0; i < listYSD.size(); i++) {
						P_CGJHD cgjhd = new P_CGJHD();
						cgjhd.setCgys_id(mDialogCurrentItem.getCgys_id());
						cgjhd.setIDU(GLOBAL.IDU_INSERT);
						cgjhd.setWz_id(listYSD.get(i).getWz_id());
						cgjhd.setWz_name(listYSD.get(i).getWz_name());
						cgjhd.setWz_type_2(listYSD.get(i).getWz_type_2());
						cgjhd.setWz_brand(listYSD.get(i).getWz_brand());
						cgjhd.setWz_spec(listYSD.get(i).getWz_brand());
						cgjhd.setWz_unit(listYSD.get(i).getWz_unit());
						cgjhd.setUnit_price(listYSD.get(i).getUnit_price());
						listJHD.add(cgjhd);
					}
					mBudgetDialogInterface.setData(listJHD);
					mBudgetDialog.dismiss();
					//mListAdapter.addShowDataList(listJHD);
				} else {
					Message msg = new Message();
					msg.obj = mActivity.getString(R.string.purchase_budget_is_null);
					mToastHandler.sendMessage(msg);
				}
				break;

			default:
				break;
			}
		}

	};
	
	private P_CGYS mDialogCurrentItem;
	@SuppressWarnings("rawtypes")
	private DataListAdapter.ListAdapterInterface mDialogListAdapterManager = new DataListAdapter.ListAdapterInterface() {
		@Override
		public int getLayoutId() {
			return R.layout.purchase_budget_title_list_item;
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
		if (bean instanceof P_CGYS) {
			P_CGYS cgys = (P_CGYS)bean;
			int count = 0;
			mapItem.put(mDialogListHeadNames[count++], cgys.getCgys_number());
			mapItem.put(mDialogListHeadNames[count++], cgys.getCgys_name());
			mapItem.put(mDialogListHeadNames[count++], cgys.getTask_name());
			mapItem.put(mDialogListHeadNames[count++], Integer.toString(cgys.getWorkload()));
			mapItem.put(mDialogListHeadNames[count++],
					UtilTools.formatMoney("¥", cgys.getTotal_money(), 2));
			mapItem.put(mDialogListHeadNames[count++], Integer.toString(cgys.getPurchase_item()));
			mapItem.put(mDialogListHeadNames[count++], UserCache.getUserMaps().get(Integer.toString(cgys.getCreator())));
			mapItem.put(mDialogListHeadNames[count++], DateUtils.dateToString(DateUtils.FORMAT_SHORT, cgys.getCreate_date()));
			int status = cgys.getStatus() == 0 ? 0 : cgys.getStatus() - 1;
			mapItem.put(mDialogListHeadNames[count++], GLOBAL.FLOW_APPROVAL_STATUS[status][1]);
			
		}
		return mapItem;
	}
	

    public interface BudgetDialogInterface {
    	void setData(List<P_CGJHD> cgjhdList);
    }

}
