
package com.pm360.cepm360.app.module.purchase;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.res.TypedArray;
import android.graphics.Color;
import android.text.TextUtils.TruncateAt;
import android.util.TypedValue;
import android.view.Gravity;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.WindowManager;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.CompoundButton.OnCheckedChangeListener;
import android.widget.ListView;
import android.widget.TextView;

import com.pm360.cepm360.R;
import com.pm360.cepm360.app.common.activity.DataManagerInterface;
import com.pm360.cepm360.app.common.adpater.DataListAdapter;
import com.pm360.cepm360.app.common.adpater.DataListAdapter.ViewHolder;
import com.pm360.cepm360.app.utils.UtilTools;
import com.pm360.cepm360.common.ResultStatus;
import com.pm360.cepm360.common.json.AnalysisManager;
import com.pm360.cepm360.common.util.MiscUtils;
import com.pm360.cepm360.entity.P_WZ;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

/*
 * popupWindow基类，布局文件在各自xml定义
 */
public class PurchaseListDialog implements DataListAdapter.ListAdapterInterface<P_WZ> {
	
	private View mBaseDialog;
	private View mListLayout;
	private Activity mActivity;
	private AlertDialog mDialog;
	private ListView mPurchaseBillListView;
	private DataListAdapter<P_WZ> mAdapter;
	private int[] mDisplayItemIds;
	
	@SuppressWarnings("unused")
	private P_WZ mCurrentItem;
	private List<P_WZ> mAlreadyCheckPspList;
	private List<P_WZ> mDisplaysPspList = new ArrayList<P_WZ>();
	private List<P_WZ> mAllBillPspList;
	
	// 列表头显示名称
	private String[] mListHeadNames;
	// 列表显示字段
	private String[] mDisplayItemNames = {
		"stuff_name",
		"stuff_spec",
		"stuff_type",
		"stuff_unit",
		"price",
		"seller_name",
	};	


	public PurchaseListDialog(Activity activity) {
		mActivity = activity;
	}

	public void setAlreadyCgs(List<P_WZ> psp) {
		mAlreadyCheckPspList = psp;
	}
	
	public void show() {
		boolean findFlag = true;		
		/*先清空再添加*/
		mDisplaysPspList.clear();		
		for (int i = 0; i < mAllBillPspList.size(); i++){
			findFlag = true;
			for (int ii = 0; ii < mAlreadyCheckPspList.size(); ii++){			
				/*stuff_id不同就添到采购Dialog列表*/
//				if (mAllBillPspList.get(i).getStuff_id() == mAlreadyCheckPspList.get(ii).getStuff_id()){
//					findFlag = false;
//				}/
			}
			if (findFlag == true){
				mDisplaysPspList.add(mAllBillPspList.get(i));
			}
		}
		
		CheckBox checkBox = (CheckBox) mListLayout.findViewById(R.id.purchase_selecte);
		checkBox.setChecked(false);
		
		mAdapter.setDataList(mDisplaysPspList);
		mAdapter.clearSelection();
		mDialog.show();
		WindowManager.LayoutParams params = 
				mDialog.getWindow().getAttributes();
				params.width = 1000;
				params.height = 600;
				params.gravity = Gravity.CENTER;
				mDialog.getWindow().setAttributes(params);
	}
	
	public void dismiss() {
		mDialog.dismiss();
	}
		
	public View getPopupView() {
		return mBaseDialog;
	}
	
	public List<P_WZ> SaveData() {
		return mAdapter.getSelectedDatas();
	}	
	
	
	/*商品信息转采购信息*/
//	private P_WZ pcgTopsp(P_CG pcg){
//		P_WZ psp= new P_WZ();
//		psp.setStuff_id(pcg.getStuff_id());
//		psp.setStuff_name(pcg.getStuff_name());
//		psp.setStuff_spec(pcg.getStuff_spec());
//		psp.setStuff_type(pcg.getStuff_type());
//		psp.setStuff_unit(pcg.getStuff_unit());
//		psp.setSeller_name(pcg.getSeller_name());
//		return psp;
//	}	
	
	public void loadData() {
		/*load data*/
//		RemoteStuffService.getInstance().getStuffList(stuffListManager, 
//				((CepmApplication)mActivity.getApplicationContext()).getUser().getTenant_id());
	}	
	
	public void init() {		
		
		mBaseDialog = mActivity.getLayoutInflater().inflate(
				R.layout.purchase_select_list_dialog, null);
		
		AlertDialog.Builder addPopup = new AlertDialog.Builder(mActivity);
		addPopup.setView(mBaseDialog);
		addPopup.setCancelable(false);
		mDialog = addPopup.create();
		
		Button exitTextView = (Button)mBaseDialog.findViewById(R.id.exit_Button);
		exitTextView.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View arg0) {
				mDialog.dismiss();
			}
		});		
		
		initPurchaseListView();
		
		loadData();	
	}
	
	/**
	 * 初始化库存列表布局
	 */
	private void initPurchaseListView() {
		// 库存列表头布局
		mListLayout = mBaseDialog.findViewById(R.id.purchase_listhead);
		
		// 获取列表使用的相关资源
		mListHeadNames = mActivity.getResources().getStringArray(R.array.purchase_dialog_product_list);
		TypedArray typedArray = mActivity.getResources().obtainTypedArray(
											R.array.purchase_dialog_product_list_ids);

		if (mListHeadNames != null) {
			mDisplayItemIds = new int[typedArray.length()];
			for (int i = 0; i < mDisplayItemIds.length; i++) {
				mDisplayItemIds[i] = typedArray.getResourceId(i, 0);
				if (i < mListHeadNames.length) {
					// 配置列表头项相关字段
					TextView tv = (TextView) mListLayout
									.findViewById(mDisplayItemIds[i]);
					tv.setTextSize(TypedValue.COMPLEX_UNIT_PX, mActivity.getResources().getDimensionPixelSize(R.dimen.sp17_s));
					tv.setText(mListHeadNames[i]);
				}
			}
		}
		typedArray.recycle();
		
		CheckBox checkBox = (CheckBox) mListLayout.findViewById(R.id.purchase_selecte);
		checkBox.setOnCheckedChangeListener(new OnCheckedChangeListener() {
			
			@Override
			public void onCheckedChanged(CompoundButton button, boolean isChecked) {
				if (isChecked) {
					mAdapter.setSelectedAll();
				} else {
					mAdapter.clearSelection();
					mAdapter.notifyDataSetChanged();
				}
			}
		});

		
		// 库存列表
		mPurchaseBillListView = (ListView) mBaseDialog
							.findViewById(R.id.purchase_listview);		
		mPurchaseBillListView.setOnItemClickListener(new OnItemClickListener() {

			@Override
			public void onItemClick(AdapterView<?> adapterView, View view, int position,
					long id) {
				mAdapter.setSelected(position, true);
				mPurchaseBillListView.setSelection(position);
			}
		});
		mAdapter = new DataListAdapter<P_WZ>(mActivity, this);
		mPurchaseBillListView.setAdapter(mAdapter);		
	}	
	
	@SuppressWarnings("unused")
	private DataManagerInterface stuffListManager = new DataManagerInterface() {
		
		@SuppressWarnings("unchecked")
		@Override
		public void getDataOnResult(ResultStatus status, List<?> list) {
			switch (status.getCode()) {
				case AnalysisManager.SUCCESS_DB_QUERY:
					mAllBillPspList = (List<P_WZ>) list;
//					mAdapter.setDataList((List<P_WZ>) list);
					break;
				default:
					break;
			}
		}
	};


	@Override
	public int getLayoutId() {
		return R.layout.purchase_add_select_list_item;
	}

	@Override
	public View getHeaderView() {
		return mListLayout;
	}

	/**
	 * 设置组件的监听器
	 */
	@Override
	public void regesterListeners(final ViewHolder viewHolder, final int position) {
		// 为EditText设置监听
		for (int i = 0; i < viewHolder.tvs.length; i++) {
			viewHolder.tvs[i].setOnClickListener(new View.OnClickListener() {
				
				@Override
				public void onClick(View view) {
					mAdapter.setPickSelected(position);
					viewHolder.cbs[0].setChecked(mAdapter.getSelectedList().contains(position));
					mCurrentItem = mAdapter.getItem(position);
				}
			});
		}
		
		viewHolder.cbs[0].setOnClickListener(new Button.OnClickListener() {
			
			@Override
			public void onClick(View view) {
				mAdapter.setPickSelected(position);
			}
		});
	}

	/**
	 * 初始化控件内容
	 */
	@Override
	public void initListViewItem(View convertView, 
									ViewHolder holder,
									DataListAdapter<P_WZ> adapter, 
									int position) {
        P_WZ inventory = (P_WZ) adapter.getItem(position);     
        Map<String, String> listViewItem = MiscUtils.beanToMap(inventory);
    	for (int i = 0; i < mDisplayItemNames.length; i++) {  
    		if (i == 2) {
    			Integer.parseInt(listViewItem.get(mDisplayItemNames[i]));
//    			if (type != 0) {
//    				holder.tvs[i].setText(GLOBAL.STUFF_TYPE_VALUE[type - 1]);
//    			}
    		} else if (mDisplayItemNames[i].equals("price")) {
    			holder.tvs[i].setText(UtilTools.formatMoney("￥", Double.parseDouble(listViewItem.get(mDisplayItemNames[i])), 2));
    		} else {
    			holder.tvs[i].setText(listViewItem.get(mDisplayItemNames[i]));
    		}
    		
    		holder.tvs[i].setTextColor(Color.BLACK);
            holder.tvs[i].setEllipsize(TruncateAt.MARQUEE);
        }
    	
    	// 将选中的列表项高亮
        if (mAdapter.getSelectedList().contains((Integer) position)) {
            convertView.setBackgroundResource(R.color.touch_high_light);
            if (!holder.cbs[0].isChecked()) {
            	holder.cbs[0].setChecked(true);
            }
        } else {
            convertView.setBackgroundColor(Color.TRANSPARENT);
            if (holder.cbs[0].isChecked()) {
            	holder.cbs[0].setChecked(false);
            }
        }
	}

	@Override
	public void initLayout(View convertView, ViewHolder holder) {
		holder.tvs = new TextView[mDisplayItemIds.length];
        for (int i = 0; i < mDisplayItemIds.length; i++) {
            holder.tvs[i] = (TextView) 
            		convertView.findViewById(mDisplayItemIds[i]);
        }
        
        holder.cbs = new CheckBox[1];
        holder.cbs[0] = (CheckBox) convertView.findViewById(R.id.purchase_selecte);		
	}

	@Override
	public List<P_WZ> findByCondition(Object... condition) {
		return null;
	}

	@Override
	public boolean isSameObject(P_WZ t1, P_WZ t2) {
		return false;
	}

}
