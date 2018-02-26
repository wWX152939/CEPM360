package com.pm360.cepm360.app.module.common.plan;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.AlertDialog;
import android.content.res.TypedArray;
import android.os.Handler;
import android.os.Message;
import android.text.TextPaint;
import android.util.TypedValue;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.pm360.cepm360.R;
import com.pm360.cepm360.app.cache.UserCache;
import com.pm360.cepm360.app.common.activity.DataManagerInterface;
import com.pm360.cepm360.app.common.adpater.DataTreeListAdapter;
import com.pm360.cepm360.app.common.adpater.DataTreeListAdapter.ViewHolder;
import com.pm360.cepm360.common.ResultStatus;
import com.pm360.cepm360.common.json.AnalysisManager;
import com.pm360.cepm360.entity.OBS;
import com.pm360.cepm360.services.system.RemoteOBSService;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class SelectOBSPathDialog implements DataManagerInterface, 
        DataTreeListAdapter.TreeListAdapterInterface {
	
	private List<OBS> mTreeList = new ArrayList<OBS>();
	private DataTreeListAdapter<OBS> mTreeViewAdapter;
	private ListView mListView;
	private Activity mActivity;
	private View mSelectDialog;
	private AlertDialog mDialog;
	
	private SubListListener mListener;
	
	private String[] mDisplayItems;
	private int[] mItemIds;
	
	public static interface SubListListener {
		public void onSubTreeClick(String OBSName);
	}
	
	public void setSubListListener(SubListListener listener) {
		mListener = listener;
	}
	
	public SelectOBSPathDialog(Activity activity) {
		mActivity = activity;
		mSelectDialog = mActivity.getLayoutInflater().inflate(
				R.layout.obs_select_dialog, null);
		AlertDialog.Builder addPopup = new AlertDialog.Builder(mActivity);
		addPopup.setView(mSelectDialog);
		addPopup.setCancelable(false);
		mDialog = addPopup.create();
		mDialog.show();
		showButton();
	}
	
	public void initTreeView() {
		mTreeViewAdapter = new DataTreeListAdapter<OBS>(mActivity, mTreeList, false, this);
        mListView = (ListView) mSelectDialog.findViewById(R.id.file_dir_list);
        mListView.setAdapter(mTreeViewAdapter);
        
		mDisplayItems = mActivity.getResources().getStringArray(R.array.select_obs_names);

		TypedArray typedArray = mActivity.getResources()
				.obtainTypedArray(R.array.select_obs_ids);
		if (mDisplayItems != null) {
			int itemLength = mDisplayItems.length;
			mItemIds = new int[itemLength];
			for (int i = 0; i < itemLength; i++) {
				mItemIds[i] = typedArray.getResourceId(i, 0);
			}

		}
		typedArray.recycle();
		loadData();
    }
	
	public void loadData() {
		RemoteOBSService.getInstance().getOBSList(this, UserCache.getCurrentUser());
	}
	
	public void showButton() {

        
		Button save = (Button)mSelectDialog.findViewById(R.id.save_Button);
		save.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View arg0) {
	            mListener.onSubTreeClick(mTreeViewAdapter.getItem(mLine).getObs_id() + "#" + mTreeViewAdapter.getItem(mLine).getName());
	            mDialog.dismiss();
			}
		});
		Button cancel = (Button)mSelectDialog.findViewById(R.id.exit_Button);
		cancel.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View arg0) {
				mDialog.dismiss();
			}
		});	
	}

	public Map<String, String> OBSToMap(OBS obs) {
		Map<String, String> mapItem = new HashMap<String, String>();
		if (obs instanceof OBS) {

			mapItem.put(mDisplayItems[0], obs.getCode());
			mapItem.put(mDisplayItems[1], obs.getName());

		}
		return mapItem;
	}
	
    @SuppressLint("HandlerLeak") public Handler mServerToastHandler = new Handler() {
        public void handleMessage(Message msg) {
            if (mActivity != null) {
                Toast.makeText(mActivity, (CharSequence) msg.obj,
                        Toast.LENGTH_SHORT).show();
            }
        }
    };

	@SuppressWarnings("unchecked")
	@Override
	public void getDataOnResult(ResultStatus status, List<?> list) {
		if (status.getCode() != AnalysisManager.SUCCESS_DB_QUERY) {
			Message msg = new Message();
			msg.obj = status.getMessage();
			mServerToastHandler.sendMessage(msg);
		}
		if (status.getCode() == AnalysisManager.SUCCESS_DB_QUERY) {

			if (list != null && list.size() != 0) {
				mTreeList = initOBSList((ArrayList<OBS>) list);
				mTreeViewAdapter.updateDataList(mTreeList);
			} else {
				Message msg = new Message();
				msg.obj = mActivity.getString(R.string.obs_is_null);
				mServerToastHandler.sendMessage(msg);
			}

		}
	}
	
	private ArrayList<OBS> initOBSList(ArrayList<OBS> list) {
		ArrayList<OBS> obslist = list;

		for (int i = 0; i < obslist.size(); i++) {
			int parent = obslist.get(i).getParents_id();
			for (int j = 0; j < i; j++) {
				if (parent == obslist.get(j).getObs_id()) {
					int level = obslist.get(j).getLevel() + 1;
					obslist.get(i).setLevel(level);
					obslist.get(j).setHas_child(true);
				}
			}
		}
		return obslist;
	}

    @Override
    public int getLayoutId() {
        return R.layout.obs_select_list_item;
    }
    
    @Override
    public void initLayout(View convertView, ViewHolder holder) {
        holder.tvs = new TextView[mItemIds.length];
        for (int i = 0; i < mItemIds.length; i++) {
            holder.tvs[i] = (TextView) convertView.findViewById(mItemIds[i]);
        }
        
        holder.ivs = new ImageView[1];
        holder.ivs[0] = (ImageView) convertView.findViewById(R.id.obs_icon);
    }

    @Override
    public void initListViewItem(ViewHolder holder, int position) {
        Map<String, String> item = OBSToMap(mTreeViewAdapter.getItem(position));        
        for (int i = 0; i < mDisplayItems.length; i++) {
            holder.tvs[i].setText(item.get(mDisplayItems[i]));
            holder.tvs[i].setTextColor(mActivity.getResources().getColor(R.color.task_dialog_text));
            TextPaint tp = holder.tvs[i].getPaint();
            if (mTreeViewAdapter.getItem(position).isHas_child()) {
                holder.tvs[i].setTextSize(TypedValue.COMPLEX_UNIT_PX, mActivity.getResources().getDimension(R.dimen.sp16_s));
                tp.setFakeBoldText(true);
            } else {
                holder.tvs[i].setTextSize(TypedValue.COMPLEX_UNIT_PX, mActivity.getResources().getDimension(R.dimen.sp14_s));
                tp.setFakeBoldText(false);
            }           
        }
        
        if (mTreeViewAdapter.getItem(position).isHas_child()
                && !mTreeViewAdapter.getItem(position).isExpanded()) {
            holder.ivs[0].setImageResource(R.drawable.item_collapse);
        } else if (mTreeViewAdapter.getItem(position).isHas_child()
                && mTreeViewAdapter.getItem(position).isExpanded()) {
            holder.ivs[0].setImageResource(R.drawable.item_expand);
        } else if (!mTreeViewAdapter.getItem(position).isHas_child()) {
            holder.ivs[0].setImageResource(R.drawable.item_collapse);
        }
    }

    private int mLine;
    @Override
    public void regesterListeners(ViewHolder holder, final int position) {
        for (int i = 0; i < holder.tvs.length; i++) {
            holder.tvs[i].setClickable(false);
            OnClickListener listener = (new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                	mLine = position;
                    mTreeViewAdapter.setSelected(position, true);
                    mTreeViewAdapter.updateListView(position);
                                       
                }
            });
            holder.tvs[i].setOnClickListener(listener);
            holder.ivs[0].setOnClickListener(listener);
        }
    }

	@Override
	public void calculateContentItemCount() {
		
	}
}
