package com.pm360.cepm360.app.module.template;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Intent;
import android.content.res.TypedArray;
import android.graphics.Color;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.util.TypedValue;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.Button;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.pm360.cepm360.R;
import com.pm360.cepm360.app.cache.PermissionCache;
import com.pm360.cepm360.app.cache.UserCache;
import com.pm360.cepm360.app.common.activity.DataManagerInterface;
import com.pm360.cepm360.app.common.adpater.DataListAdapter;
import com.pm360.cepm360.app.common.adpater.DataListAdapter.ViewHolder;
import com.pm360.cepm360.app.common.fileexplorer.FileExplorerActivity;
import com.pm360.cepm360.app.common.view.FloatingMenuView;
import com.pm360.cepm360.app.common.view.OptionsMenuView;
import com.pm360.cepm360.app.common.view.parent.BaseDialog;
import com.pm360.cepm360.app.module.common.attachment.UploadAttachManager;
import com.pm360.cepm360.app.utils.UtilTools;
import com.pm360.cepm360.common.GLOBAL;
import com.pm360.cepm360.common.ResultStatus;
import com.pm360.cepm360.common.json.AnalysisManager;
import com.pm360.cepm360.common.util.FileUtils;
import com.pm360.cepm360.common.util.MiscUtils;
import com.pm360.cepm360.entity.Files;
import com.pm360.cepm360.entity.Templet_WBS;
import com.pm360.cepm360.services.templet.RemoteWBSService;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@SuppressWarnings("rawtypes")
public class PlanTemplateFragment extends Fragment implements
		DataListAdapter.ListAdapterInterface<Templet_WBS> {
	private static final int START_TICKET_CODE = 1;
	private static final int FILE_REQUEST_CODE = 2;
	private static final String TEMPLATE_MODIFY_PERMISSION = "10_1";
	private static final String TEMPLATE_CHECK_PERMISSION = "10_2";
	public static final int PURCHASE_ADD_FLAG = 100;
	public static final int PURCHASE_UPDATA_FLAG = 101;
	public static final int PURCHASE_COMFIRM_FLAG = 102;
	private ProgressDialog mProgressDialog;
	private View mRootView;
	private View mListLayout;
	private Button mSaveButton;

	private ListView mTemplet_WBSListView;

	private DataListAdapter<Templet_WBS> mAdapter;
	
	private boolean mIsImportStatus = true;
	private boolean mIsModifyStatus = false;
	
	// 列表头显示名称
	private String[] mListHeadNames;
	private int[] mDisplayItemIds;
	private BaseDialog mAddWbsDialog;
	protected String[] mDialogNames;
	private Templet_WBS mCurrentItem;
	private OptionsMenuView mOptionsMenuView;
	protected long mAttachDismissTime;
	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {

		super.onCreateView(inflater, container, savedInstanceState);
		if (!(PermissionCache
				.hasSysPermission(TEMPLATE_MODIFY_PERMISSION) || PermissionCache.hasSysPermission(TEMPLATE_CHECK_PERMISSION))) {
			mRootView = inflater.inflate(
					R.layout.no_permissions_content_layout, container, false);
		} else {
			// 初始化基本布局变量
			initBasicLayout(inflater, container);
			
			// 初始化库存列表布局
			initManagerListView();
			
			// 加载数据
			loadData();
			
			mAddWbsDialog = new BaseDialog(getActivity(), getResources().getString(
					R.string.plan_template_dialog_title));			
			//初始化dialog
			initWbsCommonWindow();

			if (PermissionCache
					.hasSysPermission(TEMPLATE_MODIFY_PERMISSION)) {
				mOptionsMenuView = createOptionsMenuView(new String[] {
						getResources().getString(R.string.purchase_details),
						getResources().getString(R.string.purchase_modify),
						getResources().getString(R.string.purchase_delete)});
			} else {
				mOptionsMenuView = createOptionsMenuView(new String[] {
						getResources().getString(R.string.purchase_details)});				
			}
		}

		return mRootView;
	}
	
	private void switchImportDialog(boolean importStatus) {
		if (importStatus) {
			if (mIsImportStatus) {
				return;
			}
			mIsImportStatus = true;
			mAddWbsDialog.getPopupView().findViewById(mAddWbsDialog.baseLineId + 2).setVisibility(View.VISIBLE);
			mAddWbsDialog.getFirstButton().setVisibility(View.VISIBLE);
			mSaveButton.setText(R.string.import_data);
		} else {
			if (!mIsImportStatus) {
				return;
			}
			mIsImportStatus = false;
			mAddWbsDialog.getPopupView().findViewById(mAddWbsDialog.baseLineId + 2).setVisibility(View.GONE);
			mAddWbsDialog.getFirstButton().setVisibility(View.GONE);
			mSaveButton.setText(R.string.save);
		}
	}
	
	@SuppressLint("UseSparseArrays") 
	protected void initWbsCommonWindow() {
		Map<Integer, Integer> buttons = new HashMap<Integer, Integer>();
		buttons.put(0, BaseDialog.spinnerLineStyle);
		buttons.put(2, BaseDialog.editTextClickLineStyle);
		Map<Integer, String[]> widgetContent = new HashMap<Integer, String[]>();
		widgetContent.put(0, getResources().getStringArray(R.array.plan_template_spinner_type));
		mAddWbsDialog.init(R.array.plan_template_dialog, buttons, widgetContent);
		OnClickListener listener = new OnClickListener() {
			
			@Override
			public void onClick(View v) {

				Intent intent = new Intent();
				intent.setClass(getActivity(), FileExplorerActivity.class);

		        startActivityForResult(intent, FILE_REQUEST_CODE);
			}
		};
		
		mAddWbsDialog.setEditTextStyle(2, 0, listener, "");

		mSaveButton = (Button) mAddWbsDialog.getPopupView()
				.findViewById(R.id.save_Button);
		mSaveButton.setText(R.string.import_data);
		
		OnClickListener addListener = new OnClickListener() {
			
			@Override
			public void onClick(View v) {
				switchImportDialog(false);
			}
		};
		mAddWbsDialog.setFirstButton(getResources().getString(R.string.new_add), addListener);

		mSaveButton.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View arg0) {
				String[] names = getActivity().getResources().getStringArray(
						R.array.plan_template_dialog);
				String[] typenames = getActivity().getResources().getStringArray(
						R.array.plan_template_spinner_type);				
				
				Map<String, String> saveData = mAddWbsDialog.SaveData();
				if (mIsModifyStatus) {
					if (saveData.get(names[1]).isEmpty()) {
						Toast.makeText(getActivity(), R.string.pls_input_all_info, Toast.LENGTH_SHORT).show();
						return;
					}
					String temptypeString = saveData.get(names[0]);
					mDialogTemplet_WBS = new Templet_WBS();
					for (int i = 0; i < typenames.length; i++){
						if (temptypeString.equals(typenames[i])){
							mDialogTemplet_WBS.setType(i+1);
						}
					}
					mDialogTemplet_WBS.setName(saveData.get(names[1]));
					mDialogTemplet_WBS.setTenant_id(UserCache.getCurrentUser().getTenant_id());
					mDialogTemplet_WBS.setTemplet_wbs_id(mCurrentItem.getTemplet_wbs_id());
					showProgressDialog("updateWBSTmp");
					RemoteWBSService.getInstance().updateWBSTmp(mManager, mDialogTemplet_WBS);
				} else {
					if (mIsImportStatus) {
						if (saveData.get(names[1]).isEmpty()
								&& saveData.get(names[2]).isEmpty()) {
							Toast.makeText(getActivity(), R.string.pls_input_all_info, Toast.LENGTH_SHORT).show();
							return;
						}
						
						if (!saveData.get(names[2]).contains(".mpp")) {
							Toast.makeText(getActivity(), R.string.pls_select_valid_file, Toast.LENGTH_SHORT).show();
							return;
						}
						String temptypeString = saveData.get(names[0]);
						mDialogTemplet_WBS = new Templet_WBS();
						for (int i = 0; i < typenames.length; i++){
							if (temptypeString.equals(typenames[i])){
								mDialogTemplet_WBS.setType(i+1);
							}
						}
						mDialogTemplet_WBS.setName(saveData.get(names[1]));
						mDialogTemplet_WBS.setTenant_id(UserCache.getCurrentUser().getTenant_id());
						UploadAttachManager uploadAttachManager = new UploadAttachManager(getActivity(), mDocumentManager, null);
						showProgressDialog("importWBS");
						uploadAttachManager.uploadDocument(saveData.get(names[2]), 0, 0, 0);
					} else {
						if (saveData.get(names[1]).isEmpty()) {
							Toast.makeText(getActivity(), R.string.pls_input_all_info, Toast.LENGTH_SHORT).show();
							return;
						}
						String temptypeString = saveData.get(names[0]);
						mDialogTemplet_WBS = new Templet_WBS();
						for (int i = 0; i < typenames.length; i++){
							if (temptypeString.equals(typenames[i])){
								mDialogTemplet_WBS.setType(i+1);
							}
						}
						mDialogTemplet_WBS.setName(saveData.get(names[1]));
						mDialogTemplet_WBS.setTenant_id(UserCache.getCurrentUser().getTenant_id());
						showProgressDialog("addWBSTmp");
						RemoteWBSService.getInstance().addWBSTmp(mManager, mDialogTemplet_WBS);
					}
				}
			}
		});
	}
	
	private Templet_WBS mDialogTemplet_WBS;
	private DataManagerInterface mDocumentManager = new DataManagerInterface() {
		
		@Override
		public void getDataOnResult(ResultStatus status, List<?> list) {
			if (status.getCode() == AnalysisManager.SUCCESS_DB_ADD) {
				Files file = (Files)list.get(0);
				RemoteWBSService.getInstance().importWBSTemplet(mManager, mDialogTemplet_WBS, file.getFile_name());
			} else {
				Toast.makeText(getActivity(), status.getMessage(), Toast.LENGTH_SHORT).show();
			}
		}
	};
	
    private void showProgressDialog(String text) {
        if (mProgressDialog != null && mProgressDialog.isShowing())
            mProgressDialog.dismiss();
        mProgressDialog = UtilTools.showProgressDialog(getActivity(), true, true);
    }

    private void dismissProgressDialog() {
        if (mProgressDialog != null && mProgressDialog.isShowing())
            mProgressDialog.dismiss();
    }	

    private void loadData() {
        showProgressDialog("loading planTemplateList...");
        RemoteWBSService.getInstance().getWBSTmpList(mManager,
                UserCache.getCurrentUser().getTenant_id(), 0);
    }

	private OptionsMenuView createOptionsMenuView(String[] subMenuNames) {
		OptionsMenuView optionsMenus = new OptionsMenuView(getActivity(),
				subMenuNames);
		optionsMenus.setSubMenuListener(new OptionsMenuView.SubMenuListener() {
			@Override
			public void onSubMenuClick(View view) {
				mAttachDismissTime = System.currentTimeMillis();
				switch ((Integer) view.getTag()) {
				case 0:
					mIsModifyStatus = false;
					detailTicket();
					break;						
				case 1:
					mIsModifyStatus = true;
					modifyTicket();
					break;					
				case 2:
					deleteTicket();
					break;
				}
				mOptionsMenuView.dismiss();
			}
		});
		return optionsMenus;
	}
	
	protected void deleteTicket() {
		UtilTools.deleteConfirm(getActivity(), new UtilTools.DeleteConfirmInterface() {
			
			@Override
			public void deleteConfirmCallback() {
				showProgressDialog("delete planTemplate...");
				RemoteWBSService.getInstance().deleteWBSTmp(mManager,
						mCurrentItem.getTemplet_wbs_id());
			}
		});
	}	

	protected void modifyTicket() {
		switchImportDialog(false);
		String[] values = new String[3];
		int type = mCurrentItem.getType();
		if (type == 0) {
			type = 1;
		}
		values[0] = GLOBAL.WBS_TYPE[type - 1][1];
		values[1] = mCurrentItem.getName();
		values[2] = "";
		mAddWbsDialog.show(values);
	}
	
	protected void detailTicket() {	
		
		Intent intent1 = new Intent(getActivity(),
				PlanTemplateAddActivity.class);
		Bundle bundle = new Bundle();
		if (PermissionCache.hasSysPermission(TEMPLATE_MODIFY_PERMISSION)) {
			bundle.putSerializable("plan_template_modify", mCurrentItem);
		} else {
			bundle.putSerializable("plan_template_info", mCurrentItem);
		}
		
		intent1.putExtras(bundle);
		startActivityForResult(intent1, START_TICKET_CODE);			
	}	

	private DataManagerInterface mManager = new DataManagerInterface() {

		@SuppressWarnings("unchecked")
		@Override
		public void getDataOnResult(ResultStatus status, List<?> list) {
			dismissProgressDialog();
            if (status.getCode() != AnalysisManager.SUCCESS_DB_QUERY) {
                Message msg = new Message();
                msg.obj = status.getMessage();
                mToastHandler.sendMessage(msg);
            }
            Log.v("chen","planTemp ="+list + " code:" + status.getCode());
			switch (status.getCode()) {
			case AnalysisManager.SUCCESS_DB_QUERY:
				if (list != null && list.size() > 0)
					mAdapter.setShowDataList((List<Templet_WBS>) list);
				Log.v("chen","mAdapter ="+mAdapter.getDataList());
				break;

			case AnalysisManager.SUCCESS_DB_DEL:
	            Log.v("chen","mCurrentItem:" + mCurrentItem);
				mAdapter.deleteData(mCurrentItem);
				break;

			case AnalysisManager.SUCCESS_DB_UPDATE:
				MiscUtils.clone(mCurrentItem, mDialogTemplet_WBS);
				mAdapter.notifyDataSetChanged();
				mAddWbsDialog.dismiss();
				break;
				
			case AnalysisManager.SUCCESS_DB_ADD:
				mAddWbsDialog.dismiss();
//				loadData();
				mCurrentItem = (Templet_WBS) list.get(0);
				detailTicket();
				break;				

			default:
				break;
			}
		}
	};
	
	@SuppressLint("HandlerLeak") 
	public Handler mToastHandler = new Handler() {
        public void handleMessage(Message msg) {
            Toast.makeText(getActivity(), (CharSequence) msg.obj,
                    Toast.LENGTH_SHORT).show();
        }
	};

	private void initManagerListView() {
		// 库存列表头布局
		mListLayout = mRootView.findViewById(R.id.plan_template_listhead);
		// 获取列表使用的相关资源
		mListHeadNames = getResources().getStringArray(
				R.array.plantemplate_names);
		TypedArray typedArray = getResources().obtainTypedArray(
				R.array.plantemplate_ids);

		if (mListHeadNames != null) {
			mDisplayItemIds = new int[typedArray.length()];
			for (int i = 0; i < mDisplayItemIds.length; i++) {
				mDisplayItemIds[i] = typedArray.getResourceId(i, 0);
				if (i < mListHeadNames.length) {
					// 配置列表头项相关字段
					TextView tv = (TextView) mListLayout
							.findViewById(mDisplayItemIds[i]);
					tv.setTextSize(TypedValue.COMPLEX_UNIT_PX, getActivity().getResources().getDimensionPixelSize(R.dimen.table_title_textsize));
					tv.setTextColor(Color.BLACK);
					tv.setText(mListHeadNames[i]);
				}
			}
		}
		
		typedArray.recycle();
		
		if (PermissionCache
				.hasSysPermission(TEMPLATE_MODIFY_PERMISSION)) {		
			final FloatingMenuView floatingMenuView = (FloatingMenuView) mRootView
					.findViewById(R.id.floating_menu);
			floatingMenuView.addPopItem(getResources().getString(R.string.add), R.drawable.icn_add);
			floatingMenuView
					.setPopOnItemClickListener(new AdapterView.OnItemClickListener() {
	
						@Override
						public void onItemClick(AdapterView<?> parent, View view,
								int position, long id) {
								switchImportDialog(true);
								mIsModifyStatus = false;
								mAddWbsDialog.show(null);
								floatingMenuView.dismiss();						
						}
	
					});
		}

		// 库存列表
		mTemplet_WBSListView = (ListView) mRootView
				.findViewById(R.id.plan_template_listview);
		mTemplet_WBSListView.setOnItemClickListener(new OnItemClickListener() {

			@Override
			public void onItemClick(AdapterView<?> adapterView, View view,
					int position, long id) {
				mAdapter.setSelected(position, true);
				mTemplet_WBSListView.setSelection(position);
			}
		});
		mAdapter = new DataListAdapter<Templet_WBS>(getActivity(), this);
		mTemplet_WBSListView.setAdapter(mAdapter);
	}
	
	private void initBasicLayout(LayoutInflater inflater, ViewGroup container) {
		// 获取跟布局
		mRootView = inflater.inflate(R.layout.plan_template_fragment,
				container, false);

	}

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
			DataListAdapter adapter, final int position) {

		Map<String, String> listViewItem = beanToMap(adapter.getItem(position));
		Log.v("chen","initListViewItem ="+listViewItem);
		for (int i = 0; i < mListHeadNames.length; i++) {
			if (i == 0){
				holder.tvs[0].setText(position+1+"");
			}else if (i == 2){
				holder.tvs[i].setText(GLOBAL.WBS_TYPE[Integer.parseInt(listViewItem.get(mListHeadNames[i]))-1][1]);
			} else {
				holder.tvs[i].setText(listViewItem.get(mListHeadNames[i]));
			}
			holder.tvs[i].setClickable(false);
			holder.tvs[i].setOnClickListener(new View.OnClickListener() {

				@Override
				public void onClick(View view) {
					long minus_time = System.currentTimeMillis()
							- mAttachDismissTime;

					if (minus_time < 300)
						return;

					mCurrentItem = mAdapter.getItem(position);
					mOptionsMenuView.showAsDropDown(view, 0,
								(-view.getMeasuredHeight() - UtilTools.dp2pxH(view.getContext(), 40)));
						
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
	private Map<String, String> beanToMap(Object bean) {
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

	@SuppressWarnings("unchecked")
	@Override
	public List findByCondition(Object... condition) {
		return null;
	}

	@Override
	public boolean isSameObject(Templet_WBS t1, Templet_WBS t2) {
		return t1.getTemplet_wbs_id() == t2.getTemplet_wbs_id();
	}

	@Override
	public void onActivityResult(int requestCode, int resultCode, Intent data) {
		super.onActivityResult(requestCode, resultCode, data);

		if (requestCode == START_TICKET_CODE) {
			loadData();
		} else if (resultCode == Activity.RESULT_OK && requestCode == FILE_REQUEST_CODE) {
			Bundle bundle = data.getExtras();
			if (mAddWbsDialog.getEditTextView(1).getText().toString().equals("")) {
				String fileName = FileUtils.getFileName(bundle
						.getString("FILE_PATH"));
				mAddWbsDialog.setEditTextContent(1, fileName);
			}
			mAddWbsDialog.setEditTextContent(2, bundle.getString("FILE_PATH"));
		}
	}
}
