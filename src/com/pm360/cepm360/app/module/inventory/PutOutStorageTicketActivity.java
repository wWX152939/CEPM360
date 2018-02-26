package com.pm360.cepm360.app.module.inventory;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.Dialog;
import android.content.Intent;
import android.content.res.TypedArray;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.text.Html;
import android.util.TypedValue;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.View.OnTouchListener;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.LinearLayout.LayoutParams;
import android.widget.PopupWindow;
import android.widget.TextView;
import android.widget.Toast;

import com.pm360.cepm360.R;
import com.pm360.cepm360.app.cache.ProjectCache;
import com.pm360.cepm360.app.cache.UserCache;
import com.pm360.cepm360.app.common.activity.DataManagerInterface;
import com.pm360.cepm360.app.common.activity.ListSelectActivity;
import com.pm360.cepm360.app.common.activity.OwnerSelectActivity;
import com.pm360.cepm360.app.common.adpater.DataListAdapter;
import com.pm360.cepm360.app.common.adpater.DataListAdapter.ListAdapterInterface;
import com.pm360.cepm360.app.common.adpater.DataListAdapter.ViewHolder;
import com.pm360.cepm360.app.common.view.OptionsMenuView;
import com.pm360.cepm360.app.module.schedule.PlanMakeSelectActivity;
import com.pm360.cepm360.app.utils.UtilTools;
import com.pm360.cepm360.common.GLOBAL;
import com.pm360.cepm360.common.ResultStatus;
import com.pm360.cepm360.common.json.AnalysisManager;
import com.pm360.cepm360.entity.P_CK;
import com.pm360.cepm360.entity.P_CKD;
import com.pm360.cepm360.entity.P_KC;
import com.pm360.cepm360.entity.TaskCell;
import com.pm360.cepm360.entity.User;
import com.pm360.cepm360.entity.ZH_group;
import com.pm360.cepm360.services.common.RemoteCommonService;
import com.pm360.cepm360.services.group.RemoteGroupService;
import com.pm360.cepm360.services.storehouse.RemoteOutStoreService;
import com.pm360.cepm360.services.storehouse.RemoteStoreHouseService;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class PutOutStorageTicketActivity extends TicketActivity {

    private TextView mCKNumber, mTaskName, mStoreHouse, mUseName; 
    private Button mAddButton;
    private int mSelectProjectId;
    private List<ZH_group> mGroupList;
    
    private DataListAdapter<P_CKD> mStockOutListAdapter;
    private List<P_CKD> mShowDataList = new ArrayList<P_CKD>();
    private List<P_CKD> mUpdatingDataList = new ArrayList<P_CKD>();
    
    private View mStockOutListHeader;
    // 列表头显示名称
    private String[] mStockOutDisplayItemNames;
    private int[] mStockOutDisplayItemIds;
    
    private int mOperation;
    private P_CK mCurrentBean;
    private P_CKD mCurrentTicketItem;
    private int mCurrentTicketPosition;
    private OptionsMenuView mOptionsMenuView;
    private Dialog mModifyDialog;
    
    private static final int OPERATION_DETAIL = 10;
    private static final int OPERATION_ADD = 11;
    private static final int OPERATION_MODIFY = 12;
    private static final int UPDATE_SHOW_LIST = 20;
    private static final int TASK_REQUEST_CODE = 100;
    private static final int MATERIAL_REQUEST_CODE = 200;
    private static final int OWNER_REQUEST_CODE = 300;
    private static final int STOREHOUSE_REQUEST_CODE = 400;
    
    @SuppressLint("HandlerLeak")
    public Handler mHandler = new Handler() {
        public void handleMessage(Message msg) {
            switch (msg.what) {
            case UPDATE_SHOW_LIST:
                mStockOutListAdapter.setShowDataList(mShowDataList);
                mStockOutListAdapter.notifyDataSetChanged();
                break;
            }
        }
    };
    
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    
        Intent intent = getIntent();
        mOperation = (Integer) intent.getSerializableExtra("operation");
        mCurrentBean = (P_CK) intent.getSerializableExtra("data");
        
        setupHeaderView();
        setupListView();
        setupStatisticsView();
        setupButtons();
        
        if (mOperation != OPERATION_DETAIL) 
            createOptionsMenuView();
    }
    
    PopupWindow mPopupWindow;
    @SuppressLint("ResourceAsColor") private void showPopupWindow(View view, final List<ZH_group> items) {
    	if (mPopupWindow == null) {
    		// 一个自定义的布局，作为显示的内容
            View contentView = LayoutInflater.from(this).inflate(
                    R.layout.popup_window, null);
            LinearLayout parent = (LinearLayout) contentView.findViewById(R.id.popup_line);

            for (int i = 0; i < items.size(); i++) {
            	TextView tv = new TextView(this);
            	tv.setText(items.get(i).getNode_name());
            	tv.setTextColor(Color.WHITE);
            	tv.setGravity(Gravity.CENTER_VERTICAL);
            	tv.setPadding(2, 0, 0, 0);
            	final int groupId = items.get(i).getZh_group_id();
            	tv.setOnClickListener(new OnClickListener() {
    				
    				@Override
    				public void onClick(View v) {
                        Intent intent = new Intent(PutOutStorageTicketActivity.this,
                                PlanMakeSelectActivity.class);
                        intent.putExtra(PlanMakeSelectActivity.NODE_CODE, groupId);
                        startActivityForResult(intent, TASK_REQUEST_CODE);
                        if (mPopupWindow != null) {
                        	mPopupWindow.dismiss();
                        }
                        
    				}
    			});
            	parent.addView(tv, new LayoutParams(UtilTools.dp2pxW(this, 200), UtilTools.dp2pxH(this, 35)));
            	if (i != items.size() - 1) {
            		View line = new View(this);
            		line.setBackgroundColor(R.color.popup_divider);
                	parent.addView(line, new LayoutParams(LayoutParams.MATCH_PARENT, 1));
            	}
            }
            
            mPopupWindow = new PopupWindow(contentView,
                    LayoutParams.WRAP_CONTENT, LayoutParams.WRAP_CONTENT, true);

            mPopupWindow.setTouchable(true);

            mPopupWindow.setTouchInterceptor(new OnTouchListener() {

                @Override
                public boolean onTouch(View v, MotionEvent event) {

                    return false;
                    // 这里如果返回true的话，touch事件将被拦截
                    // 拦截后 PopupWindow的onTouchEvent不被调用，这样点击外部区域无法dismiss
                }
            });

            // 如果不设置PopupWindow的背景，无论是点击外部区域还是Back键都无法dismiss弹框
            // 我觉得这里是API的一个bug
            mPopupWindow.setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));

    	}
        
        // 设置好参数之后再show
        mPopupWindow.showAsDropDown(view);

    }
    
    // 初始化表头
    private void setupHeaderView() {
        String[] itemLabels = getResources().getStringArray(
                R.array.inventory_stock_out_ticket_labels);
        addItemLabel(itemLabels, 3); // 2: 每行2个   3:每行2个
        String user = UserCache.getUserMaps()
                .get(String.valueOf(mCurrentBean.getUse_person()));
        
        // 出库单号
        mCKNumber = (TextView) mItem1.findViewById(R.id.value);
        mCKNumber.setText(mCurrentBean.getCk_number());
        mCKNumber.setBackgroundResource(R.drawable.bg_edittext);     
        mTaskName = (TextView) mItem2.findViewById(R.id.value);
        mTaskName.setText(mCurrentBean.getTask_name());        
        mStoreHouse = (TextView) mItem3.findViewById(R.id.value);
        mStoreHouse.setText(mCurrentBean.getStorehouse());        
        mUseName = (TextView) mItem4.findViewById(R.id.value);
        mUseName.setText(user == null ? "" : user);
        
        mSelectProjectId = mCurrentBean.getProject_id();
        mCurrentBean.setProject_id(mCurrentBean.getProject_id());
        RemoteGroupService.getInstance().getGroup(new DataManagerInterface() {
			
			@SuppressWarnings("unchecked")
			@Override
			public void getDataOnResult(ResultStatus status, List<?> list) {
				if (status.getCode() == AnalysisManager.SUCCESS_DB_QUERY) {
					mGroupList = (List<ZH_group>) list;
				}
			}
		}, mSelectProjectId);
        
        if (mOperation != OPERATION_DETAIL) { // 修改 或 添加  
            createProgressDialog(true, true);
            if (mOperation == OPERATION_ADD) getCKNumber();

            // 所用部位  
            mTaskName.setOnClickListener(new View.OnClickListener() {               
                @Override
                public void onClick(View view) {

                	if (mGroupList == null || mGroupList.isEmpty()) {
                		Toast.makeText(PutOutStorageTicketActivity.this, R.string.pls_create_node, Toast.LENGTH_SHORT).show();
                	} else {
                		showPopupWindow(view, mGroupList);
                	}
                	
                }
            });
            
            // 库房   
            mStoreHouse.setOnClickListener(new View.OnClickListener() {               
                @Override
                public void onClick(View view) {
                    if (mSelectProjectId == 0) {
                        showToast(getResources().getString(R.string.select_project_first));
                        return;
                    }
                    Intent intent = new Intent(PutOutStorageTicketActivity.this,
                            StoreHouseSelectActivity.class);
                    intent.putExtra(StoreHouseSelectActivity.PROJECT_ID, mSelectProjectId);
                    startActivityForResult(intent, STOREHOUSE_REQUEST_CODE);
                }
            });
            
            // 领用人
            mUseName.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    Intent intent = new Intent(PutOutStorageTicketActivity.this, OwnerSelectActivity.class);
                    intent.putExtra("title", getString(R.string.please_select_user));
                    intent.putExtra("project", ProjectCache.getCurrentProject());
                    startActivityForResult(intent, OWNER_REQUEST_CODE);
                }
            });
           
            mAddButton = buildButton(getString(R.string.inventory_add_resource));
            LinearLayout.LayoutParams layoutParams = new LinearLayout.LayoutParams(
                    getResources().getDimensionPixelSize(R.dimen.ticket_button_width),
                    getResources().getDimensionPixelSize(R.dimen.ticket_button_height));
            mAddButton.setOnClickListener(new View.OnClickListener() {                
                @Override
                public void onClick(View view) {
                    Intent intent = new Intent(
                            PutOutStorageTicketActivity.this,
                            ListSelectActivity.class);
                    Bundle bundle = new Bundle();
                    List<P_KC> includedList = new ArrayList<P_KC>();
                    for (int i = 0; i < mStockOutListAdapter.getDataShowList().size(); i++) {
                        P_KC kc = new P_KC();
                        kc.setRk_id(mStockOutListAdapter.getDataShowList().get(i).getRk_id());
                        includedList.add(kc);
                    }
                    if (!includedList.isEmpty()) {
                        bundle.putSerializable(ListSelectActivity.FILTER_DATA_KEY, 
                                (Serializable) includedList);
                    }
                    bundle.putSerializable(ListSelectActivity.FRAGMENT_KEY, 
                            InventoryStoreHouseFragment.class);
                    bundle.putBoolean(ListSelectActivity.SELECT_MODE_KEY, true);
                    intent.putExtras(bundle);
                    startActivityForResult(intent, MATERIAL_REQUEST_CODE);
                }
            });
            mItem5.removeAllViews();
            mItem5.addView(mAddButton, layoutParams);
            mItem5.setVisibility(View.VISIBLE);
        }
    }
    
    // 初始化表单
    private void setupListView() {
        ViewGroup parent = (ViewGroup) findViewById(R.id.ticket_table_list_header);
        mStockOutListHeader = LayoutInflater.from(this)
                .inflate(R.layout.inventory_stock_out_ticket_list, parent, false);
        parent.addView(mStockOutListHeader);
        
        mStockOutDisplayItemNames = getResources().getStringArray(
                R.array.inventory_stock_out_ticket_names);
        TypedArray typedArray = getResources().obtainTypedArray(
                R.array.inventory_stock_out_ticket_ids);

        if (mStockOutDisplayItemNames != null) {
            mStockOutDisplayItemIds = new int[typedArray.length()];
            for (int i = 0; i < mStockOutDisplayItemIds.length; i++) {
                mStockOutDisplayItemIds[i] = typedArray.getResourceId(i, 0);
                // 配置列表头项相关字段
                String text = "<b>" + mStockOutDisplayItemNames[i] + "</b>";
                TextView tv = (TextView) findViewById(mStockOutDisplayItemIds[i]);
                tv.setTextSize(TypedValue.COMPLEX_UNIT_PX, getResources().getDimension(R.dimen.table_title_textsize));
                tv.setTextColor(Color.BLACK);
                tv.setText(Html.fromHtml(text));
            }
        }
        typedArray.recycle();
        
        mStockOutListAdapter = new DataListAdapter<P_CKD>(this, mStockOutListManager);
        mListView.setAdapter(mStockOutListAdapter); 
        
        if (mOperation != OPERATION_ADD ) {
            loadCKDData();
        }
    }
    
    // 设置 合计 view, 此处设为不可见
    private void setupStatisticsView() {
        mStatisticsLayout.setVisibility(View.GONE);
    }
    
    // 按钮设置
    private void setupButtons() {
        if (mOperation == OPERATION_DETAIL) {
            mButtonLayout.setVisibility(View.GONE);
            return;
        }
        ((Button) findViewById(R.id.btn_left)).setVisibility(View.GONE); 
        Button btn_save = (Button) findViewById(R.id.btn_right);
        btn_save.setVisibility(View.VISIBLE);
        btn_save.setText(R.string.save);
        btn_save.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (mOperation == OPERATION_DETAIL) {
                    finish();
                } 
                // 保存数据
                if (!checkInputComplete() || !checkCKDOutCount()) return;
                
                mCurrentBean.setTenant_id(UserCache.getCurrentUser().getTenant_id());
                mCurrentBean.setCk_id(mCurrentBean.getCk_id());
                mCurrentBean.setProject_id(mCurrentBean.getProject_id());
                mCurrentBean.setTask_id(mCurrentBean.getTask_id());
                mCurrentBean.setStorehouse(mStoreHouse.getText().toString());
                mCurrentBean.setUse_person(mCurrentBean.getUse_person());
                mCurrentBean.setStoreman(UserCache.getCurrentUser().getUser_id());
                if (mOperation == OPERATION_ADD) {
                    // 增加出库单
                    showProgressDialog("addCK...");
                    RemoteOutStoreService.getInstance().addCK(
                            dataManager, mCurrentBean,
                            mUpdatingDataList);
                } else if (mOperation == OPERATION_MODIFY) {
                    // 更新出库单
                    showProgressDialog("updateCK...");
                    RemoteOutStoreService.getInstance().updateCK(
                            dataManager, mCurrentBean,
                            mUpdatingDataList);
                }
            }            
        });            
    }
    
    // 获取出库单列表
    private void loadCKDData() {
        RemoteOutStoreService.getInstance().getCKD(dataManager, mCurrentBean.getCk_id());
    }
    
    DataManagerInterface dataManager = new DataManagerInterface() {
        @Override
        public void getDataOnResult(ResultStatus status, List<?> list) {
            dismissProgressDialog();
            switch (status.getCode()) {
            case AnalysisManager.SUCCESS_DB_QUERY:
                if (list != null && list.size() != 0) {
                    mShowDataList.clear();
                    mUpdatingDataList.clear();
                    for (Object object : list) {
                        if (object instanceof P_CKD) {
                            mShowDataList.add((P_CKD) object);
                            mUpdatingDataList.add((P_CKD) object);
                        }
                    }
                }
                mHandler.sendEmptyMessage(UPDATE_SHOW_LIST);
                break;
            case AnalysisManager.SUCCESS_DB_UPDATE:
            case AnalysisManager.SUCCESS_DB_ADD:
                Intent intent = new Intent();
                Bundle bundle = new Bundle();
                bundle.putSerializable("current_bean", (P_CK) list.get(0));
                intent.putExtras(bundle);
                setResult(Activity.RESULT_OK, intent);
                finish();
                break;
            }            
        }
    };
    
    // 修改表单
    private void modifyTicket(P_CKD bean, boolean needDelete) {
        if (needDelete) {            
            mUpdatingDataList.remove(bean);
            if (bean.getIDU() != 1) {
                bean.setIDU(GLOBAL.IDU_DELETE);
                mUpdatingDataList.add(bean);
            }
            mShowDataList.remove(bean);
            mHandler.sendEmptyMessage(UPDATE_SHOW_LIST);
        } else { // 修改
            if (bean.getIDU() == 0) // 原有数据修改
                bean.setIDU(GLOBAL.IDU_UPDATE);
            updateTicketList(bean);
        }
    }
    
    // 更新表单
    private void updateTicketList(P_CKD bean) {
        mShowDataList.remove(mCurrentTicketPosition);
        mShowDataList.add(mCurrentTicketPosition, bean);
        
        mUpdatingDataList.remove(mCurrentTicketPosition);
        mUpdatingDataList.add(mCurrentTicketPosition, bean);

        mHandler.sendEmptyMessage(UPDATE_SHOW_LIST);
    }
    
    private void showModifyDialog() {
        mModifyDialog = new Dialog(this, R.style.MyDialogStyle);
        mModifyDialog.setContentView(R.layout.inventory_dialog_layout);
                
        final EditText editText = (EditText) mModifyDialog.findViewById(R.id.dialog_edit_text);
        //editText.setText(String.valueOf(mCurrentTicketItem.getOut_quantity()));
        View.OnClickListener listener = new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                switch (v.getId()) {
                case R.id.save:
                    if (!editText.getText().toString().equals("")) {
                        mCurrentTicketItem.setOut_quantity(Double.parseDouble(editText.getText().toString()));
                        modifyTicket(mCurrentTicketItem, false);
                    }
                    break;
                case R.id.cancel:
                    break;
                }
                mModifyDialog.dismiss();
            }
        };
        ((TextView) mModifyDialog.findViewById(R.id.save)).setOnClickListener(listener);
        ((TextView) mModifyDialog.findViewById(R.id.cancel)).setOnClickListener(listener);
        final TextView storeAmount = (TextView) mModifyDialog.findViewById(R.id.tv_store_quantity);
        if (mCurrentTicketItem.getIDU() == GLOBAL.IDU_INSERT) {
            storeAmount.setText(getString(R.string.current_store_amount) 
                    + mCurrentTicketItem.getStore_quantity()
                    + " (" + GLOBAL.UNIT_TYPE[mCurrentTicketItem.getWz_unit()-1][1] + ")");
            mModifyDialog.show();
        } else {
            RemoteStoreHouseService.getInstance().getKCAmountByRKID(
                    new DataManagerInterface() {
                        @Override
                        public void getDataOnResult(ResultStatus status, List<?> list) {
                            if (status.getCode() == AnalysisManager.SUCCESS_DB_QUERY) {
                                if (list != null && list.size() != 0) {
                                    P_KC kc = (P_KC) list.get(0);
                                    storeAmount.setText(getString(R.string.current_store_amount)
                                            + kc.getQuantity() 
                                            + " (" + GLOBAL.UNIT_TYPE[mCurrentTicketItem.getWz_unit()-1][1] + ")" );
                                }
                            } else {
                                storeAmount.setText(getString(R.string.current_store_amount));
                            }
                            mModifyDialog.show();
                        }
                    },
                    UserCache.getCurrentUser().getTenant_id(),
                    mCurrentTicketItem.getRk_id());
        }
    }
    
    /**
     * 初始化文件操作的Popup对话框
     */
    @SuppressLint("InflateParams")
    private void createOptionsMenuView() {
        String[] subMenuNames = new String[] { getString(R.string.modify),
                getString(R.string.delete) };
        mOptionsMenuView = new OptionsMenuView(this, subMenuNames);
        mOptionsMenuView
                .setSubMenuListener(new OptionsMenuView.SubMenuListener() {
                    @Override
                    public void onSubMenuClick(View view) {
                        switch ((Integer) view.getTag()) {
                        case 0: // 修改
                            showModifyDialog();
                            break;
                        case 1: // 删除
                            modifyTicket(mCurrentTicketItem, true);
                            break;
                        }
                        mOptionsMenuView.dismiss();
                    }
                });
    }
    
    private ListAdapterInterface<P_CKD> mStockOutListManager = new DataListAdapter.ListAdapterInterface<P_CKD>() {

        @Override
        public int getLayoutId() {
            return R.layout.inventory_stock_out_ticket_list;
        }

        @Override
        public View getHeaderView() {
            return mStockOutListHeader;
        }

        @Override
        public void regesterListeners(ViewHolder viewHolder, final int position) {
            for (int i = 0; i < viewHolder.tvs.length; i++) {
                viewHolder.tvs[i].setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        mStockOutListAdapter.setSelected(position, true);
                        mCurrentTicketItem = mStockOutListAdapter.getItem(position);
                        mCurrentTicketPosition = position;
                        if (mOperation != OPERATION_DETAIL) {
                            if (view.getId() == R.id.amount) {
                                showModifyDialog();
                            } else {
                                mOptionsMenuView.showAsDropDown(view, 0, (-view
                                        .getMeasuredHeight() - UtilTools.dp2pxH(
                                                view.getContext(), 40)));
                            }
                        }
                    }
                });
            }            
        }

        @Override
        public void initListViewItem(View convertView, ViewHolder holder,
                DataListAdapter<P_CKD> adapter, int position) {
            Map<String, String> listViewItem = beanToMap(adapter.getItem(position),
                    position);
            if (mOperation != OPERATION_DETAIL) {
                Drawable drawable= getResources().getDrawable(R.drawable.icon_modify);
                drawable.setBounds(0, 0, 25, 25);
                holder.tvs[5].setCompoundDrawables(null, null, drawable, null);
            }
            
            for (int i = 0; i < mStockOutDisplayItemNames.length; i++) {
                holder.tvs[i].setText(listViewItem.get(mStockOutDisplayItemNames[i]));
            }
        }

        @Override
        public void initLayout(View convertView, ViewHolder holder) {
            holder.tvs = new TextView[mStockOutDisplayItemIds.length];
            for (int i = 0; i < mStockOutDisplayItemIds.length; i++) {
                holder.tvs[i] = (TextView) convertView
                        .findViewById(mStockOutDisplayItemIds[i]);                
            }
        }

        @Override
        public List<P_CKD> findByCondition(Object... condition) {
            // TODO Auto-generated method stub
            return null;
        }

        @Override
        public boolean isSameObject(P_CKD t1, P_CKD t2) {
            // TODO Auto-generated method stub
            return false;
        }
        
        private Map<String, String> beanToMap(Object data, int position) {
            Map<String, String> mapItem = new HashMap<String, String>();
            if (data instanceof P_CKD) {
                P_CKD bean = (P_CKD) data;
                mapItem.put(mStockOutDisplayItemNames[0], bean.getWz_name());
                if (bean.getWz_type_1() > 0)
                    mapItem.put(mStockOutDisplayItemNames[1], GLOBAL.WZ_TYPE[bean.getWz_type_1()-1][1]);
                mapItem.put(mStockOutDisplayItemNames[2], bean.getWz_brand());
                mapItem.put(mStockOutDisplayItemNames[3], bean.getWz_spec());
                mapItem.put(mStockOutDisplayItemNames[4], GLOBAL.UNIT_TYPE[bean.getWz_unit()-1][1]);
                mapItem.put(mStockOutDisplayItemNames[5], String.valueOf(bean.getOut_quantity()));
            }
            return mapItem;
        }        
    };
    
    // 调用添加资源的返回。
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (resultCode == 0 || data == null) return;
        
        if (requestCode == MATERIAL_REQUEST_CODE) { // 添加表单数据
            @SuppressWarnings("unchecked")
            ArrayList<P_KC> kcs = (ArrayList<P_KC>) data
                    .getSerializableExtra(ListSelectActivity.RESULT_KEY);
            for (P_KC kc : kcs) {
                P_CKD bean = new P_CKD();
                bean.setWz_id(kc.getWz_id());
                bean.setRk_id(kc.getRk_id());
                bean.setWz_name(kc.getName());
                bean.setWz_brand(kc.getBrand());
                bean.setWz_unit(kc.getUnit());
                bean.setWz_model_number(kc.getModel_number());
                bean.setWz_spec(kc.getSpec());
                bean.setWz_type_1(kc.getWz_type_1());
                bean.setWz_type_2(kc.getWz_type_2());
                bean.setStore_quantity(kc.getQuantity());
                bean.setIDU(GLOBAL.IDU_INSERT); // 插入
                
                mShowDataList.add(bean);
                mUpdatingDataList.add(bean);
            }
            mHandler.sendEmptyMessage(UPDATE_SHOW_LIST);
        }
        
        if (requestCode == TASK_REQUEST_CODE) { // 获取所用部位
        	TaskCell task = (TaskCell) data.getSerializableExtra(PlanMakeSelectActivity.TASK_CODE);
            if (task != null) {
                mCurrentBean.setTask_id(task.getTask_id());
                mCurrentBean.setTask_name(task.getName());
                mTaskName.setText(task.getName());
            }
        }
        
        if (requestCode == OWNER_REQUEST_CODE) {
            User user = (User) data.getSerializableExtra("user");
            if (user != null) {
                mUseName.setText(user.getName());
                mCurrentBean.setUse_person(user.getUser_id());
            }
        }
        
        if (requestCode == STOREHOUSE_REQUEST_CODE) {
            String storeHouse = data.getStringExtra(StoreHouseSelectActivity.RESULT_KEY);
            mStoreHouse.setText(storeHouse);
            mCurrentBean.setStorehouse(storeHouse);
        }
        
        super.onActivityResult(requestCode, resultCode, data);
    }
    
    private void getCKNumber() {
        RemoteCommonService.getInstance().getCodeByDate(new DataManagerInterface() {
            @Override
            public void getDataOnResult(ResultStatus status, List<?> list) {
                if (status.getCode() == AnalysisManager.SUCCESS_DB_QUERY) {
                    mCurrentBean.setCk_number(status.getMessage().toString());
                    mCKNumber.setText(status.getMessage().toString());
                }
            }            
        }, "CK");
    }
    
    private boolean checkInputComplete() {
         if (mTaskName.getText() == null || mTaskName.getText().toString().equals("")) {
            showToast(getString(R.string.please_select_task));
            return false;
        } else if (mUseName.getText() == null || mUseName.getText().toString().equals("")) {
            showToast(getString(R.string.please_select_user));
            return false;
        }
        return true;
    }
    
    private boolean checkCKDOutCount() {
        if (mUpdatingDataList.isEmpty()) {
            showToast(getString(R.string.please_insert_resources));
            return false;
        } else {
            for (P_CKD ckd : mUpdatingDataList) {
                if (ckd.getOut_quantity() == 0.0) {
                    showToast(getString(R.string.out_store_is_emtpy));
                    return false;
                }
            }
        }
        return true;
    }
    
    private void showToast(String message) {
        Toast.makeText(this, message, Toast.LENGTH_SHORT).show();
    }

}
