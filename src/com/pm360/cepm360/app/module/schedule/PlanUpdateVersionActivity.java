package com.pm360.cepm360.app.module.schedule;

import android.annotation.SuppressLint;
import android.app.ProgressDialog;
import android.content.Intent;
import android.content.res.TypedArray;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.text.TextPaint;
import android.util.Log;
import android.util.TypedValue;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.WindowManager;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.ListView;
import android.widget.PopupWindow.OnDismissListener;
import android.widget.TextView;
import android.widget.Toast;

import com.pm360.cepm360.R;
import com.pm360.cepm360.app.cache.PermissionCache;
import com.pm360.cepm360.app.cache.ProjectCache;
import com.pm360.cepm360.app.cache.UserCache;
import com.pm360.cepm360.app.common.activity.ActionBarFragmentActivity;
import com.pm360.cepm360.app.common.activity.DataManagerInterface;
import com.pm360.cepm360.app.common.adpater.BaseBeanListAdapter;
import com.pm360.cepm360.app.common.adpater.BaseBeanListAdapter.BaseBeanListAdapterManager;
import com.pm360.cepm360.app.common.view.FloatingMenuView;
import com.pm360.cepm360.app.common.view.parent.BaseActionBar;
import com.pm360.cepm360.app.common.view.parent.BaseDialog;
import com.pm360.cepm360.app.common.view.parent.BasePopupWindow;
import com.pm360.cepm360.app.common.view.parent.BaseToast;
import com.pm360.cepm360.common.GLOBAL;
import com.pm360.cepm360.common.ResultStatus;
import com.pm360.cepm360.common.json.AnalysisManager;
import com.pm360.cepm360.common.util.DateUtils;
import com.pm360.cepm360.entity.Changes;
import com.pm360.cepm360.entity.Project;
import com.pm360.cepm360.services.plan.RemoteChangeService;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@SuppressLint("HandlerLeak") public class PlanUpdateVersionActivity extends ActionBarFragmentActivity implements
		BaseBeanListAdapterManager<Changes>,
		DataManagerInterface {

//	private String[] mItemNames;
	
	private ProgressDialog mProgressDialog;

	private BasePopupWindow mAttrPopup = null;
	private BasePopupWindow mPublishAttrPopup = null;
	private BasePopupWindow mVersionAttrPopup = null;
	
	private Integer mLine;

	private BaseDialog mAddVersionDialog;

	private Changes mChangesInfoBean;

	private List<Changes> mChangesList = new ArrayList<Changes>();

	// 0 add 1 modify
	private int mSaveImageFlag = 0;

	private String[] mDisplayItems;

	private BaseBeanListAdapter<Changes> mListAdapter;

	private ListView taskUpdateVersionListView;

	private int[] mItemIds;

	protected long mAttachDismissTime;
	
	private boolean mAddConditionFlag = false;
	
	private BaseActionBar mBaseActionBar;
	
	private final int updateVersionFlag = 0x1;
	
	private int mServerDataFlag = 0;
	
	private Project mCurrentProject;
	
	@SuppressLint("HandlerLeak") public Handler mToastHandler = new Handler(){
    	public void handleMessage(Message msg) {
    		BaseToast.show(getBaseContext(), msg.what);
        }
    };
    
    public Handler mServerToastHandler = new Handler() {
    	public void handleMessage(Message msg) {
    		Toast.makeText(getBaseContext(), msg.getData().getString("123"),
					Toast.LENGTH_SHORT).show();
        }
    };
    
    public Handler mHandler = new Handler(){
    	public void handleMessage(Message msg) {
    		Toast.makeText(PlanUpdateVersionActivity.this,
    				R.string.change_check_1, Toast.LENGTH_SHORT).show();
        }
    };
    
	@SuppressLint("HandlerLeak") private Handler mProgressHandler = new Handler() {
		@Override
		public void handleMessage(Message msg) {
			Log.i("dog2", "mServerDataFlag:" + mServerDataFlag + " msg:" + msg.what);
			if (msg.what == 0) {
				if (mProgressDialog.isShowing())
					mProgressDialog.dismiss();
			} else if (msg.what == 1) {
				if ((mServerDataFlag & updateVersionFlag) != updateVersionFlag) {
					mProgressDialog.show();
			        mProgressDialog.setContentView(R.layout.layout_progress);
				}
			}			

		}
	};


	private void initVersionDialog() {
		mAddVersionDialog = new BaseDialog(this, R.string.change_add_dialog);

		mAddVersionDialog.init(R.array.plan_change_names, null, null);

		Button saveImageView = (Button) mAddVersionDialog.getPopupView()
				.findViewById(R.id.save_Button);
		saveImageView.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View arg0) {
				String[] names = getResources().getStringArray(
						R.array.plan_change_names);

				Map<String, String> saveData = mAddVersionDialog.SaveData();
				
				if (saveData.get(names[0]).equals("") 
						|| saveData.get(names[1]).equals("")) {
					mToastHandler.sendEmptyMessage(BaseToast.NULL_MSG);
					return;
				}

				if (mSaveImageFlag == 0) {
					if (!mAddConditionFlag) {
						mHandler.sendEmptyMessage(0);
						return;
					}
					showProgressDialog(updateVersionFlag);
					Changes changes = new Changes();
					changes.setProject_id(ProjectCache.getCurrentProject().getProject_id());
					changes.setTitle(saveData.get(names[0]));
					changes.setContent(saveData.get(names[1]));

					changes.setCreater(UserCache.getCurrentUser()
							.getUser_id());
					RemoteChangeService.getInstance().addChange(
							PlanUpdateVersionActivity.this, changes);
				} else if (mSaveImageFlag == 1) {
					showProgressDialog(updateVersionFlag);
					mChangesInfoBean.setTitle(saveData.get(names[0]));
					mChangesInfoBean.setContent(saveData.get(names[1]));

					mChangesInfoBean.setCreater(UserCache.getCurrentUser()
							.getUser_id());
					RemoteChangeService.getInstance().setChange(mChangesInfoBean);
					RemoteChangeService.getInstance().updateChange(
							PlanUpdateVersionActivity.this);
				}

				mAddVersionDialog.dismiss();
			}
		});

	}

	private void createPopupAttrWindow() {
		mAttrPopup = new BasePopupWindow(this);
		mAttrPopup.init(R.array.task_update_version);
		mAttrPopup.getPopupWindow().setOnDismissListener(
				new OnDismissListener() {

					@Override
					public void onDismiss() {
//						mListAdapter.setSelected(mLine, false);
						mAttachDismissTime = System.currentTimeMillis();
					}

				});

		TextView[] tvs = mAttrPopup.getTextViews();
		TextView tv_info = tvs[0];
		TextView tv_modify = tvs[1];

		tv_info.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View arg0) {
				mAttrPopup.getPopupWindow().dismiss();
				if (!PermissionCache.hasSysPermission("2_1")) {
					BaseToast.show(getBaseContext(), BaseToast.NO_PERMISSION);
					return;
				}
				Intent intent = getIntent();
				intent.putExtra("newChange", "newChange");
				PlanUpdateVersionActivity.this.setResult(101, intent);
				finish();
			}
		});

		tv_modify.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View arg0) {
				mAttrPopup.getPopupWindow().dismiss();
				if (!PermissionCache.hasSysPermission("2_1")) {
					BaseToast.show(getBaseContext(), BaseToast.NO_PERMISSION);
					return;
				}
				mSaveImageFlag = 1;

				String[] names = getResources().getStringArray(
						R.array.plan_change_names);

				String[] values = new String[names.length];

				values[0] = mChangesInfoBean.getTitle();
				values[1] = mChangesInfoBean.getContent();

				mAddVersionDialog.show(values);
			}
		});


	}

	private void createPublishPopupAttrWindow() {
		mPublishAttrPopup = new BasePopupWindow(this);
		mPublishAttrPopup.init(R.array.task_publish_update_version);
		mPublishAttrPopup.getPopupWindow().setOnDismissListener(
				new OnDismissListener() {

					@Override
					public void onDismiss() {
//						mListAdapter.setSelected(mLine, false);
						mAttachDismissTime = System.currentTimeMillis();
					}

				});

		TextView[] tvs = mPublishAttrPopup.getTextViews();
		TextView tv_del = tvs[0];
		TextView tv_add_version = tvs[1];

		tv_add_version.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View arg0) {
				mPublishAttrPopup.getPopupWindow().dismiss();
				if (!PermissionCache.hasSysPermission("2_1")) {
					BaseToast.show(getBaseContext(), BaseToast.NO_PERMISSION);
					return;
				}

				showProgressDialog(updateVersionFlag);
				RemoteChangeService.getInstance().generateVersion(
						PlanUpdateVersionActivity.this);
			}
		});
		tv_del.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View arg0) {
				mPublishAttrPopup.getPopupWindow().dismiss();
				if (!PermissionCache.hasSysPermission("2_1")) {
					BaseToast.show(getBaseContext(), BaseToast.NO_PERMISSION);
					return;
				}

				showProgressDialog(updateVersionFlag);
				RemoteChangeService.getInstance().deleteChange(
						PlanUpdateVersionActivity.this);
			}
		});

	}
	
	private void createVersionPopupAttrWindow() {
		mVersionAttrPopup = new BasePopupWindow(this);
		mVersionAttrPopup.init(R.array.task_version_update_version);
		mVersionAttrPopup.getPopupWindow().setOnDismissListener(
				new OnDismissListener() {

					@Override
					public void onDismiss() {
//						mListAdapter.setSelected(mLine, false);
						mAttachDismissTime = System.currentTimeMillis();
					}

				});

		TextView[] tvs = mVersionAttrPopup.getTextViews();
		TextView tv_del = tvs[0];

		tv_del.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View arg0) {
				mVersionAttrPopup.getPopupWindow().dismiss();
				if (!PermissionCache.hasSysPermission("2_1")) {
					BaseToast.show(getBaseContext(), BaseToast.NO_PERMISSION);
					return;
				}

				showProgressDialog(updateVersionFlag);
				RemoteChangeService.getInstance().deleteChange(
						PlanUpdateVersionActivity.this);
			}
		});

	}

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		mChangesInfoBean = new Changes();
		getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,
				WindowManager.LayoutParams.FLAG_FULLSCREEN);

		setContentView(R.layout.task_update_fragment);

		TypedArray typedArray = getResources().obtainTypedArray(
				R.array.task_update_item_ids);
		mDisplayItems = getResources().getStringArray(
				R.array.task_update_header_names);
		taskUpdateVersionListView = (ListView) findViewById(R.id.listView);

		if (mDisplayItems != null) {
			int itemLength = mDisplayItems.length;
			mItemIds = new int[itemLength];
			for (int i = 0; i < itemLength; i++) {
				mItemIds[i] = typedArray.getResourceId(i, 0);
				TextView tv = (TextView) getHeaderView().findViewById(
						mItemIds[i]);
				tv.setTextSize(TypedValue.COMPLEX_UNIT_PX, getResources().getDimensionPixelSize(R.dimen.sp18_s));
				tv.setText(mDisplayItems[i]);
				TextPaint tp = tv.getPaint();
    			tp.setFakeBoldText(true);

			}
		}
		typedArray.recycle();

		mListAdapter = new BaseBeanListAdapter<>(this, mChangesList, this);
		taskUpdateVersionListView.setAdapter(mListAdapter);
		
		mBaseActionBar = new BaseActionBar(this);
		mBaseActionBar.initActionBar();		

		initVersionDialog();
		
		createProgressDialog();

		loadData();
		
		createPopupAttrWindow();
		createPublishPopupAttrWindow();
		createVersionPopupAttrWindow();
		

		final FloatingMenuView floatingMenuView = (FloatingMenuView)findViewById(R.id.floating_menu);
		floatingMenuView.addPopItem(getString(R.string.add), R.drawable.icn_add);
		floatingMenuView
				.setPopOnItemClickListener(new AdapterView.OnItemClickListener() {

					@Override
					public void onItemClick(AdapterView<?> parent, View view,
							int position, long id) {
						floatingMenuView.dismiss();
						if (!PermissionCache.hasSysPermission("2_1")) {
							BaseToast.show(getBaseContext(), BaseToast.NO_PERMISSION);
							return;
						}
						List<Changes> changesList = mListAdapter.getTreedNode();
						if (changesList.isEmpty()) {
							Toast.makeText(getBaseContext(), R.string.pls_publish_first_version, Toast.LENGTH_SHORT).show();
							return;
						}
						mSaveImageFlag = 0;
						mAddVersionDialog.show(null);
					}

				});	

	}
	
	private void loadData() {

		RemoteChangeService.getInstance().getChangeList(this, ProjectCache.getCurrentProject());
		
	}
	
	private void showProgressDialog(final int flag) {
		mServerDataFlag = 0;
		Thread thread = new Thread(new Runnable() {
			public void run() {

				try {
					Thread.sleep(500);
					if ((mServerDataFlag & flag) != flag) {

						mProgressHandler.sendEmptyMessage(1);
					}
				} catch (InterruptedException e) {
					e.printStackTrace();
				}
			}
		});
		thread.start();	
		
	}
	
	private void dismissProgressDialog() {
		mProgressHandler.sendEmptyMessage(0);
		
	}
	
	private void createProgressDialog() {
		mProgressDialog = new ProgressDialog(this);
        mProgressDialog.setIndeterminate(true);
        mProgressDialog.setCancelable(true);
        showProgressDialog(updateVersionFlag);
	}	

	@Override
	public void onResume() {
		super.onResume();
		if (mCurrentProject == null) {
			mCurrentProject = ProjectCache.getCurrentProject();
		} else {
			Project project = ProjectCache.getCurrentProject();
			if (mCurrentProject.getProject_id() != project.getProject_id()) {
				mBaseActionBar.setTitle(project.getName());
				mCurrentProject = project;
				loadData();
			}
		}

	}

	@Override
	public void onPause() {

		super.onPause();
	}

	@Override
	public int getListItemLayoutId() {
		return R.layout.task_update_list_item;
	}

	@Override
	public int[] getItemIds() {
		return mItemIds;
	}

	@Override
	public View getHeaderView() {
		return (View) findViewById(R.id.listHeaderView);
	}

	@Override
	public void initListItem(TextView[] tvs, final int position) {
		for (int i = 0; i < tvs.length; i++) {
			tvs[i].setClickable(false);

			tvs[i].setOnClickListener(new View.OnClickListener() {

				@Override
				public void onClick(View view) {
					long minus_time = System.currentTimeMillis()
							- mAttachDismissTime;

					if (minus_time < 300)
						return;
					mChangesInfoBean = mListAdapter.getItem(position);
					RemoteChangeService.getInstance().setChange(mChangesInfoBean);

					 if (mChangesInfoBean.getVersion_number() != null)
					 {
						 mVersionAttrPopup.getPopupWindow().showAsDropDown(view, 0,
								 -view.getMeasuredHeight()
								 -
								 getResources().getDimensionPixelSize(R.dimen.popup_window_height));
					 }
					
					 else if (mChangesInfoBean.getStatus().equals(GLOBAL.IS_PUBLISH)){
						 mPublishAttrPopup.getPopupWindow().showAsDropDown(view, 0,
								 -view.getMeasuredHeight()
								 -
								 getResources().getDimensionPixelSize(R.dimen.popup_window_height));
					 } else {
						 mAttrPopup.getPopupWindow().showAsDropDown(
									view,
									0,
									-view.getMeasuredHeight()
											- getResources().getDimensionPixelSize(
													R.dimen.popup_window_height));
					 }

					mListAdapter.setSelected(position, true);

					mLine = position;

				}
			});
		}
	}

//	private Map<String, String> versionToMap(Object data) {
//		Map<String, String> map = new HashMap<String, String>();
//		if (data instanceof Changes) {
//			Changes changes = (Changes) data;
//
//			Map<String, String> saveData = mAddVersionDialog.SaveData();
//
//			map.put(mItemNames[0], Integer.toString(changes.getChange_id()));
//			map.put(mItemNames[1], changes.getTitle());
//			map.put(mItemNames[2],
//					DateUtils.dateToString(DateUtils.FORMAT_SHORT,
//							changes.getCreate_time()));
//			map.put(mItemNames[3], changes.getContent());
//			map.put(mItemNames[4], Integer.toString(changes.getCreater()));
//		}
//		return map;
//	}
//
//	private List<Map<String, String>> versionToMapList(List datas) {
//		List<Map<String, String>> list = new ArrayList<Map<String, String>>();
//		for (Object data : datas) {
//			Map<String, String> map = versionToMap(data);
//			if (map != null)
//				list.add(map);
//		}
//		return list;
//	}

	@SuppressWarnings("unchecked")
	@Override
	public void getDataOnResult(ResultStatus status, List<?> list) {
		Log.i("dog1", "PlanUpdateVersion:" + status.getCode());
		if (status.getCode() != AnalysisManager.SUCCESS_DB_QUERY) {
			Message msg = new Message();
			Bundle bundle = new Bundle();
			bundle.putString("123", status.getMessage());
			msg.setData(bundle);
			mServerToastHandler.sendMessage(msg);
		}
		
		if (status.getCode() == AnalysisManager.SUCCESS_DB_UPDATE
				|| status.getCode() == AnalysisManager.SUCCESS_DB_ADD
				|| status.getCode() == AnalysisManager.SUCCESS_DB_DEL) {
			
			if (status.getCode() == AnalysisManager.SUCCESS_DB_UPDATE) {
				showProgressDialog(updateVersionFlag);
				RemoteChangeService.getInstance().getChangeList(this, ProjectCache.getCurrentProject());

			} else if (status.getCode() == AnalysisManager.SUCCESS_DB_ADD){
				List<Changes> changesList = mListAdapter.getTreedNode();
				if (list != null && list.size() != 0) {
					if (list.get(0) instanceof Changes) {
						changesList.add((Changes)list.get(0));
						mListAdapter.refreshList();
					}
				}
				
			} else if (status.getCode() == AnalysisManager.SUCCESS_DB_DEL){
				List<Changes> changesList = mListAdapter.getTreedNode();
				for (int i = 0; i < changesList.size(); i++) {
					if (mChangesInfoBean.getChange_id() == changesList.get(i).getChange_id()) {
						changesList.remove(i);
						mListAdapter.refreshList(mLine);
						break;
					}
				}
			}
		} else if (status.getCode() == AnalysisManager.SUCCESS_DB_QUERY) {
			if (list != null && list.size() != 0) {
				mListAdapter.updateTreeList((List<Changes>) list);
				List<Changes> changeList = (List<Changes>) list;
				if (changeList.get(changeList.size() - 1).getStatus().equals(GLOBAL.IS_PUBLISH)) {
					mAddConditionFlag = true;
				} else {
					mAddConditionFlag = false;
				}
			} else {
				List<Changes> newList = new ArrayList<Changes>();
				mListAdapter.updateTreeList((List<Changes>) newList);
			}
		}
		mServerDataFlag |= updateVersionFlag;
		dismissProgressDialog();

	}

	@Override
	public String[] getDisplayItems() {
		return mDisplayItems;
	}

	private Map<String, String> changesToMap(String[] displayItems,
			Object bean) {

		Map<String, String> mapItem = new HashMap<String, String>();
		if (bean instanceof Changes) {
			Changes changes = (Changes) bean;
			int count = 0;
			mapItem.put(displayItems[count++], changes.getCode());
			mapItem.put(displayItems[count++], changes.getTitle());
			if (changes.getCreate_time() != null) {
				mapItem.put(
						displayItems[count++],
						DateUtils.dateToString(DateUtils.FORMAT_SHORT,
								changes.getCreate_time()));
			}


			if (changes.getStatus() != null && changes.getStatus().equals(GLOBAL.IS_PUBLISH)) {
				mapItem.put(displayItems[count], getString(R.string.change_already_publish));
			} else {
				mapItem.put(displayItems[count], getString(R.string.change_no_publish));
			}
			if (changes.getVersion_number() != null) {
				mapItem.put(displayItems[count], getString(R.string.change_already_has_version));
			}
			count++;
			mapItem.put(displayItems[count], changes.getContent());
		}
		return mapItem;
	}

	@Override
	public Map<String, String> beanToMap(Changes bean) {
		return changesToMap(mDisplayItems, bean);
	}
	
	@Override
    protected void onStart() {
        super.onStart();
        mBaseActionBar.myOnStart();
    }
    
    @Override
    protected void onStop() {
        super.onStop();
        mBaseActionBar.myOnStop();
    }  
}
