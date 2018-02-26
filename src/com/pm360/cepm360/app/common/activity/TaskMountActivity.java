package com.pm360.cepm360.app.common.activity;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.PopupWindow;
import android.widget.TextView;
import android.widget.Toast;

import com.pm360.cepm360.R;
import com.pm360.cepm360.app.cache.ProjectCache;
import com.pm360.cepm360.app.common.view.parent.BaseDialog;
import com.pm360.cepm360.app.common.view.parent.BaseWindow;
import com.pm360.cepm360.app.common.view.parent.TaskSharedDialog;
import com.pm360.cepm360.app.module.schedule.PlanMakeSelectActivity;
import com.pm360.cepm360.common.GLOBAL;
import com.pm360.cepm360.common.ResultStatus;
import com.pm360.cepm360.common.json.AnalysisManager;
import com.pm360.cepm360.common.util.LogUtil;
import com.pm360.cepm360.entity.Message;
import com.pm360.cepm360.entity.Project;
import com.pm360.cepm360.entity.ZH_group;
import com.pm360.cepm360.entity.ZH_group_task;
import com.pm360.cepm360.services.group.RemoteGroupService;
import com.pm360.cepm360.services.group.RemoteShareTaskService;

/**
 * 任务挂载界面
 */

@SuppressLint("InflateParams")
public class TaskMountActivity extends Activity implements OnClickListener{
	public final int CODE_SELECT_TASK = 202;
	
	public final String CODE_GROUP_ID = "code_group_id";
	
	// Data
	private Project mSelectProject;
	private ZH_group mSelectGroup;
	private ZH_group_task mSelectTask;
	private Message mMessage;
	
	// View
	private BaseDialog mBaseWindow;
	
    @SuppressLint("UseSparseArrays") @Override
    public void onCreate(Bundle savedInstanceState) {
    	super.onCreate(savedInstanceState);
    	setContentView(R.layout.base_activity);
    	mMessage = (Message) getIntent().getSerializableExtra(GLOBAL.MSG_OBJECT_KEY);
    	if (mMessage == null) {
    		mMessage = new Message();
    	}
    	
    	final TaskSharedDialog dialog = new TaskSharedDialog(this, mMessage);
    	dialog.show(1000);
    	ImageView iv = (ImageView) dialog.getRootView().findViewById(R.id.btn_close);
    	iv.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View v) {
				dialog.dismiss();
				finish();
			}
		});
    	dialog.setButton(getString(R.string.mount), new OnClickListener() {
			
			@Override
			public void onClick(View v) {
				dialog.dismiss();
				initSecondDialog();
			}
		});
    	
    }
    
    @SuppressLint("UseSparseArrays") private void initSecondDialog() {
    	mBaseWindow = new BaseDialog(TaskMountActivity.this);
    	Map<Integer, Integer> buttons = new HashMap<Integer, Integer>();
    	buttons.put(0, BaseWindow.editTextNoBottomLineStyle);
    	buttons.put(1, BaseWindow.editTextClickLineStyle);
    	buttons.put(2, BaseWindow.editTextClickLineStyle);
		
    	mBaseWindow.init(R.array.zuhe_info, buttons, null);
    	mSelectProject = new Project();
    	mSelectProject.setProject_id(mMessage.getProject_id());
    	mBaseWindow.setEditTextContent(0, ProjectCache.getProjectIdMaps().get(mMessage.getProject_id() + ""));
    	mBaseWindow.setEditTextStyle(1, 0, this, null);
    	mBaseWindow.setEditTextStyle(2, 0, this, null);
    	initFrameWindow();
    	mBaseWindow.show();
    }
    
    private void initFrameWindow() {
    	View parent = mBaseWindow.getPopupView();
    	TextView tv = (TextView) parent.findViewById(R.id.edit_title);
    	tv.setText(getString(R.string.mount));
    	ImageView iv = (ImageView) parent.findViewById(R.id.btn_close);
    	iv.setOnClickListener(this);
    	Button btn = (Button) parent.findViewById(R.id.save_Button);
    	btn.setOnClickListener(this);
    }

	@Override
	public void onClick(View v) {
		Intent intent = new Intent();
		switch(v.getId()) {
		case R.id.btn_close:
			mBaseWindow.dismiss();
			finish();
			break;
		case R.id.save_Button:
			if (mSelectProject == null) {
				Toast.makeText(this, R.string.pls_select_project, Toast.LENGTH_SHORT).show();
				break;
			}
			
			if (mSelectGroup == null) {
				Toast.makeText(this, R.string.pls_select_node, Toast.LENGTH_SHORT).show();
				break;
			}
			if (mSelectTask == null) {
				Toast.makeText(this, R.string.pls_select_task, Toast.LENGTH_SHORT).show();
				break;
			}
			// type_id is zh_id
			RemoteShareTaskService.getInstance().mountShareTask(new DataManagerInterface() {
				
				@Override
				public void getDataOnResult(ResultStatus status, List<?> list) {
					Toast.makeText(TaskMountActivity.this, status.getMessage(), Toast.LENGTH_SHORT).show();
					if (status.getCode() == AnalysisManager.SUCCESS_DB_UPDATE) {
						finish();
					}
				}
			}, mSelectTask.getTask_id(),
			mMessage.getType_id(), mSelectProject.getProject_id(), mSelectGroup.getZh_group_id(), mMessage.getMessage_id());
			break;
		case 1201:
			if (mSelectProject != null) {
				RemoteGroupService.getInstance().getGroup(new DataManagerInterface() {
					
					@Override
					public void getDataOnResult(ResultStatus status, List<?> list) {
						if (list == null || list.isEmpty()) {
							Toast.makeText(TaskMountActivity.this, R.string.combination_empty_is_node, Toast.LENGTH_SHORT).show();
							return;
						}
						final List<ZH_group> groupList = (List<ZH_group>) list;
						List<String> stringList = new ArrayList<String>();
						for (ZH_group group : groupList) {
							stringList.add(group.getNode_name());
						}
						ArrayAdapter<String> adapter = new ArrayAdapter<String>(TaskMountActivity.this, R.layout.text_view, stringList);
						int witdh = getResources().getDimensionPixelSize(R.dimen.popup_window_et_width);
						int height = groupList.size() * getResources().getDimensionPixelSize(R.dimen.popup_window_et_height);
						View popupView = getLayoutInflater().inflate(R.layout.popup_listview, null,
								false);
						final PopupWindow pop = new PopupWindow(popupView, witdh, height, true);
						ListView lv = (ListView)popupView.findViewById(R.id.lv);
						lv.setAdapter(adapter);
						pop.showAsDropDown(mBaseWindow.getEditTextView(1));

						lv.setOnItemClickListener(new OnItemClickListener() {

							@Override
							public void onItemClick(AdapterView<?> arg0, View arg1,
									int position, long arg3) {
								mSelectGroup = groupList.get(position);
								mBaseWindow.setEditTextContent(1, mSelectGroup.getNode_name());
								pop.dismiss();
								LogUtil.i("wzw mSelectGroup:" + mSelectGroup + " position:" + position);
							}
						});
					}
				}, mSelectProject.getProject_id());
				
			} else {
				Toast.makeText(this, R.string.pls_select_project, Toast.LENGTH_SHORT).show();
			}
			// select node
			break;
		case 1202:
			if (mSelectProject == null) {
				Toast.makeText(this, R.string.pls_select_project, Toast.LENGTH_SHORT).show();
				break;
			}
			
			if (mSelectGroup == null) {
				Toast.makeText(this, R.string.pls_select_node, Toast.LENGTH_SHORT).show();
				break;
			}
			
			intent.setClass(this, PlanMakeSelectActivity.class);
            intent.putExtra(PlanMakeSelectActivity.NODE_CODE, mSelectGroup.getZh_group_id());
			startActivityForResult(intent, CODE_SELECT_TASK);
			// select task
			break;
		}
	}
	
	public void onActivityResult(int requestCode, int resultCode, Intent data) {
		if (resultCode == 0) {
			return;
		}
		if (requestCode == CODE_SELECT_TASK) {
			mSelectTask = (ZH_group_task) data.getSerializableExtra(PlanMakeSelectActivity.TASK_CODE);
			
			if (mSelectTask != null) {
				mBaseWindow.setEditTextContent(2, mSelectTask.getName());
			}
		}
	}
}
