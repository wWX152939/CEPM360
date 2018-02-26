package com.pm360.cepm360.app.module.common.plan;

import android.app.Activity;
import android.content.ActivityNotFoundException;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.WindowManager;
import android.view.inputmethod.InputMethodManager;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.pm360.cepm360.R;
import com.pm360.cepm360.app.common.view.parent.BaseDialogStyle;
import com.pm360.cepm360.common.GLOBAL;
import com.pm360.cepm360.common.util.DateUtils;
import com.pm360.cepm360.common.util.ExcelUtils;
import com.pm360.cepm360.common.util.LogUtil;
import com.pm360.cepm360.entity.TaskCell;

import java.io.File;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class ExcelExportDialog extends BaseDialogStyle {
	
	private Activity mActivity;
	private View mDialogView;
	private EditText mFileNameEditView;
	private TextView mExportPathTextView;
	private List<Map<String, String>> mMapList;
	private String[] mListHeaderNames;
	
	public ExcelExportDialog(Activity activity) {
		super(activity);
		activity.getWindow().setSoftInputMode( WindowManager.LayoutParams.SOFT_INPUT_STATE_HIDDEN);
		mActivity= activity;
		mDialogView = init(R.layout.excel_export_dialog);
		initTitle();
		mFileNameEditView = (EditText)mDialogView.findViewById(R.id.export_filename_edit);
		mExportPathTextView = (TextView)mDialogView.findViewById(R.id.export_filepath_edit);
		setButton(activity.getResources().getString(R.string.export), mListener);
	}
	
	private void initTitle() {
		TextView title = (TextView) mDialogView.findViewById(R.id.edit_title);
		title.setText(R.string.export_excel);
		ImageView iv = (ImageView) mDialogView.findViewById(R.id.btn_close);
		iv.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View v) {
				dismiss();
			}
		});
	}
	
	android.view.View.OnClickListener mListener = new View.OnClickListener(){

		@Override
		public void onClick(View arg0) {
			switch (arg0.getId()) {
			case R.id.save_Button:
				if (ExcelUtils.writeExcel(mExportPathTextView.getText().toString(), 
						mFileNameEditView.getText().toString()+".xls", mMapList, mListHeaderNames)) {
					try{
				        Intent intent = new Intent("android.intent.action.VIEW");     
				        intent.addCategory("android.intent.category.DEFAULT");     
				        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);     
				        Uri uri = Uri.fromFile(new File(mExportPathTextView.getText().toString()+mFileNameEditView.getText().toString()+".xls"));     
				        intent.setDataAndType(uri, "application/vnd.ms-excel"); 
				        mActivity.startActivity(intent);
						Toast.makeText(mActivity, R.string.export_starting,
								Toast.LENGTH_SHORT).show();
			        } catch (ActivityNotFoundException e) {
			            Toast.makeText(mActivity, R.string.document_cannot_open, Toast.LENGTH_SHORT).show();
			        }
			        dismiss();
				} else {
					Toast.makeText(mActivity, R.string.export_fail,
							Toast.LENGTH_SHORT).show();					
				}
				break;
			default:
				break;
			}
		}};

	public void show(String filePath, String fileName, List<Map<String, String>> mapList, String[] listHeaderNames) {
		super.show();
		setParams(0, 300);
		mFileNameEditView.setText(fileName);
		mExportPathTextView.setText(filePath);
		LinearLayout linear = (LinearLayout)mDialogView.findViewById(R.id.focus);
		linear.requestFocus();
		mMapList = mapList;
		mListHeaderNames = listHeaderNames;
	}

	public <T extends TaskCell> Map<String, String> excelPlanMakeToMap(Map<String, String> OBSMap,
			Map<String, String> userMap, String[] displayItems, T task) {
		Map<String, String> mapItem = new HashMap<String, String>();
		int count = 0;
		mapItem.put(displayItems[count++], task.getName());
		mapItem.put(displayItems[count++], task.getPlan_duration());
		if (task.getStart_time() != null) {
			mapItem.put(
					displayItems[count],
					DateUtils.dateToString(DateUtils.FORMAT_SHORT,
							task.getStart_time()));
		}
		count++;
		if (task.getEnd_time() != null) {
			mapItem.put(
					displayItems[count],
					DateUtils.dateToString(DateUtils.FORMAT_SHORT,
							task.getEnd_time()));
		}
		count++;

		mapItem.put(displayItems[count++],
				userMap.get(Integer.toString(task.getOwner())));
		mapItem.put(displayItems[count++],
				OBSMap.get(Integer.toString(task.getDepartment())));

		if (task.getType() != null) {
			if (task.getType().equals(GLOBAL.TASK_TYPE_MILE_KEY)) {
				mapItem.put(displayItems[count], GLOBAL.TASK_TYPE_MILE_VALUE);
			} else if (task.getType().equals(GLOBAL.TASK_TYPE_TASK_KEY)) {
				mapItem.put(displayItems[count], GLOBAL.TASK_TYPE_TASK_VALUE);
			} else if (task.getType().equals(GLOBAL.TASK_TYPE_WBS_KEY)) {
				mapItem.put(displayItems[count], GLOBAL.TASK_TYPE_WBS_VALUE);
			}
		}
		return mapItem;
	}	
	
	public <T extends TaskCell> Map<String, String> excelFeedbackToMap(Map<String, String> OBSMap,
			Map<String, String> userMap, String[] displayItems, T task) {
		Map<String, String> mapItem = new HashMap<String, String>();
		int count = 0;
		mapItem.put(displayItems[count++], task.getName());
		mapItem.put(displayItems[count++], task.getPlan_duration());
		if (task.getStart_time() != null) {
			mapItem.put(
					displayItems[count],
					DateUtils.dateToString(DateUtils.FORMAT_SHORT,
							task.getStart_time()));
		}
		count++;
		if (task.getEnd_time() != null) {
			mapItem.put(
					displayItems[count],
					DateUtils.dateToString(DateUtils.FORMAT_SHORT,
							task.getEnd_time()));
		}
		count++;
		if (task.getStatus() == GLOBAL.FEEDBACK_STATUS_0) {
			mapItem.put(displayItems[count], GLOBAL.FEEDBACK_STATUS_0_VALUE);
		} else if (task.getStatus() == GLOBAL.FEEDBACK_STATUS_1) {
			mapItem.put(displayItems[count], GLOBAL.FEEDBACK_STATUS_1_VALUE);
		} else if (task.getStatus() == GLOBAL.FEEDBACK_STATUS_2) {
			mapItem.put(displayItems[count], GLOBAL.FEEDBACK_STATUS_2_VALUE);
		}
		count++;
		mapItem.put(displayItems[count++], task.getActual_duration());
		if (task.getActual_start_time() != null) {
			mapItem.put(
					displayItems[count],
					DateUtils.dateToString(DateUtils.FORMAT_SHORT,
							task.getActual_start_time()));
		}
		count++;
		if (task.getActual_end_time() != null) {
			mapItem.put(
					displayItems[count],
					DateUtils.dateToString(DateUtils.FORMAT_SHORT,
							task.getActual_end_time()));
			String actualDuration = ((task.getActual_end_time().getTime() - task
					.getActual_start_time().getTime()) / (24 * 3600 * 1000) + "天");
			mapItem.put(displayItems[count - 2], actualDuration);
		}
		count++;

		if (task.getProgress() != 0) {
			mapItem.put(displayItems[count++], task.getProgress() + "%");
		} else {
			mapItem.put(displayItems[count++], "");
		}

		mapItem.put(displayItems[count++],
				userMap.get(Integer.toString(task.getOwner())));
		mapItem.put(displayItems[count++],
				OBSMap.get(Integer.toString(task.getDepartment())));

		if (task.getType() != null) {
			if (task.getType().equals(GLOBAL.TASK_TYPE_MILE_KEY)) {
				mapItem.put(displayItems[count], GLOBAL.TASK_TYPE_MILE_VALUE);
			} else if (task.getType().equals(GLOBAL.TASK_TYPE_TASK_KEY)) {
				mapItem.put(displayItems[count], GLOBAL.TASK_TYPE_TASK_VALUE);
			} else if (task.getType().equals(GLOBAL.TASK_TYPE_WBS_KEY)) {
				mapItem.put(displayItems[count], GLOBAL.TASK_TYPE_WBS_VALUE);
			}
		}
		LogUtil.i("mapItem:" + mapItem);
		return mapItem;
	}
	
	@Override
	public void dismiss() {
    	View editView = mDialogView.findViewById(R.id.export_filename_edit);
    	InputMethodManager imm = (InputMethodManager)mActivity.getSystemService(Context.INPUT_METHOD_SERVICE);
    	imm.hideSoftInputFromWindow(editView.getWindowToken(), 0);
		super.dismiss();
	}
}
