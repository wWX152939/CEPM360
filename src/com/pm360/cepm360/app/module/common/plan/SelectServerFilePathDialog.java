package com.pm360.cepm360.app.module.common.plan;

import android.app.Activity;
import android.app.AlertDialog;
import android.graphics.Color;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;

import com.pm360.cepm360.R;
import com.pm360.cepm360.app.cache.ProjectCache;
import com.pm360.cepm360.app.cache.UserCache;
import com.pm360.cepm360.app.common.activity.DataManagerInterface;
import com.pm360.cepm360.app.common.adpater.DataTreeListAdapter;
import com.pm360.cepm360.app.common.adpater.DataTreeListAdapter.ViewHolder;
import com.pm360.cepm360.common.GLOBAL;
import com.pm360.cepm360.common.ResultStatus;
import com.pm360.cepm360.common.json.AnalysisManager;
import com.pm360.cepm360.entity.Document;
import com.pm360.cepm360.entity.Project;
import com.pm360.cepm360.services.document.RemoteDocumentsService;

import java.util.ArrayList;
import java.util.List;

public class SelectServerFilePathDialog implements DataManagerInterface, 
        DataTreeListAdapter.TreeListAdapterInterface {
	
	private List<Document> mDocumentList = new ArrayList<Document>();
	private DataTreeListAdapter<Document> mTreeViewAdapter;
	private ListView mTreeView;
	private Document mDocumentBean;
	private Activity mActivity;
	private View mSelectDialog;
	private AlertDialog mDialog;
	private int directoryCount = 0;
	private int[] directoryParentId;
	
	private SubTreeListener mListener;
	
	public static interface SubTreeListener {
		public void onSubTreeClick(Document bean, String path);
	}
	
	public SelectServerFilePathDialog(Activity activity, SubTreeListener listener) {
		mActivity = activity;
		mSelectDialog = mActivity.getLayoutInflater().inflate(
				R.layout.server_file_select_dialog, null);
		AlertDialog.Builder addPopup = new AlertDialog.Builder(mActivity);
		addPopup.setView(mSelectDialog);
		addPopup.setCancelable(false);
		mDialog = addPopup.create();
		mDialog.show();
		showButton();

		initTreeView();
		loadData();

		mListener = listener;
	}
	
	public void initTreeView() {
        //mTreeViewAdapter = new DocumentTreeAdapter(mActivity, mDocumentList, this);
        mTreeViewAdapter = new DataTreeListAdapter<Document>(mActivity, mDocumentList, false, this);
        mTreeView = (ListView) mSelectDialog.findViewById(R.id.file_dir_list);
        mTreeView.setAdapter(mTreeViewAdapter);
    }
	
	public void loadData() {
		
//		Document directory = new Document();
//		directory.setDirectory_type(GLOBAL.DIR_TYPE_PUBLIC);
//		directory.setTenant_id(cepm.getUser().getTenant_id());
//		RemoteDocumentsService.getInstance().getDirectoryList(this, directory);
		Document directory1 = new Document();
		directory1.setDirectory_type(GLOBAL.DIR_TYPE_PROJECT);
		Project project = ProjectCache.getCurrentProject();
		directory1.setProject_id(project.getProject_id());
		directory1.setTenant_id(UserCache.getCurrentUser().getTenant_id());
		RemoteDocumentsService.getInstance().getDirectoryList(this, directory1);
		
	}
	
	public void showButton() {
//		ImageView confirm = (ImageView)mSelectDialog.findViewById(R.id.confirm);
//		confirm.setId(mConfirmId);
//		confirm.setImageDrawable(mActivity.getResources().getDrawable(R.drawable.floating_menu_publish));
//		confirm.setOnClickListener(new OnClickListener() {
//			
//			@Override
//			public void onClick(View arg0) {
//				if (mDocumentBean == null)
//					return;
//				if (!mDocumentBean.isHasChild()) {
//					mDialog.dismiss();
//				}	
//			}
//		});
		
		ImageView cancel = (ImageView)mSelectDialog.findViewById(R.id.exit_imageview);
		cancel.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View arg0) {
				mDialog.dismiss();
			}
		});	
	}
	
	private String CalculateFilePath(Document bean) {
		String path = bean.getName();

		int childId = bean.getParents_id();
		
		if (directoryCount == 1) {
			while (childId != directoryParentId[0]){

				for (int i = 0; i < mDocumentList.size(); i++) {
					if (childId == mDocumentList.get(i).getDocument_id()) {
						path += "#" + mDocumentList.get(i).getName();
						childId = mDocumentList.get(i).getParents_id();
						break;
					}
				}
			}
		} else if (directoryCount == 2){
			while (childId != directoryParentId[0] || childId != directoryParentId[1]){

				for (int i = 0; i < mDocumentList.size(); i++) {
					if (childId == mDocumentList.get(i).getDocument_id()) {
						path += "#" + mDocumentList.get(i).getName();
						childId = mDocumentList.get(i).getParents_id();
						break;
					}
				}
			}
		}
		
		String[] paths = path.split("#");
		path = "";
		for (int path_i = paths.length; path_i > 0; path_i--) {
			path += "/" + paths[path_i-1];
		}
		path += "/";
		Log.i("dog1", "path:========" + path + "  ==" + paths);
		return path;
	}
	
	private void formatDocumentList(List<Document> directoryList) {
	    mDocumentList.addAll(directoryList);

    	for (int i = 0; i < mDocumentList.size(); i++) {
    		int parentId = mDocumentList.get(i).getParents_id();
    		for (int j = 0; j < i; j++) {
    			if(parentId == mDocumentList.get(j).getDocument_id()) {
    				int level = mDocumentList.get(j).getLevel() + 1;
    				mDocumentList.get(i).setLevel(level);
    				mDocumentList.get(j).setHas_child(true);
    			}
    		}
    		    		
    	}
    }
	
	@SuppressWarnings("unchecked")
	@Override
	public void getDataOnResult(ResultStatus status, List<?> list) {
		if (status.getCode() == AnalysisManager.SUCCESS_DB_QUERY) {
			if (list != null && list.size() > 0) {
				if (list.get(0) instanceof Document) {
					directoryCount++;
					formatDocumentList((List<Document>) list);
				}
			}
			if (directoryCount >= 1) {
				mTreeViewAdapter.updateDataList(mDocumentList);
				List<Document> initDocumentList = mTreeViewAdapter.getShowList();
				if (directoryCount == 1) {
					directoryParentId = new int[1];
					directoryParentId[0] = initDocumentList.get(0).getParents_id();
				} else if (directoryCount == 2){
					directoryParentId = new int[2];
					directoryParentId[0] = initDocumentList.get(0).getParents_id();
					directoryParentId[1] = initDocumentList.get(1).getParents_id();
				}
			}
		}
		
	}

    @Override
    public int getLayoutId() {
        return R.layout.treeview_list_item;
    }

    @Override
    public void initLayout(View convertView, ViewHolder holder) {
        holder.tvs = new TextView[1];
        holder.ivs = new ImageView[2];
        holder.tvs[0] = (TextView) convertView.findViewById(R.id.text);
        holder.ivs[0] = (ImageView) convertView.findViewById(R.id.icon);
        holder.ivs[1] = (ImageView) convertView.findViewById(R.id.type);       
    }
    
    @Override
    public void initListViewItem(ViewHolder holder, int position) {
        holder.tvs[0].setTextColor(Color.BLACK);
        if(mTreeViewAdapter.getItem(position).getFile_count() > 0) {
            holder.tvs[0].setText(mTreeViewAdapter.getShowList().get(position).getName()
                    + " (" + mTreeViewAdapter.getItem(position).getFile_count() + ")");
        } else {
            holder.tvs[0].setText(mTreeViewAdapter.getShowList().get(position).getName());
        }
        holder.ivs[1].setImageResource(R.drawable.folder2);
        holder.ivs[1].setVisibility(View.VISIBLE);
        
        if (mTreeViewAdapter.getItem(position).isHas_child() && !mTreeViewAdapter.getItem(position).isExpanded()) {
            holder.ivs[0].setImageResource(R.drawable.item_collapse);
        } else if (mTreeViewAdapter.getItem(position).isHas_child() && mTreeViewAdapter.getItem(position).isExpanded()) {
            holder.ivs[0].setImageResource(R.drawable.item_expand);
        } else if (!mTreeViewAdapter.getItem(position).isHas_child()){
            holder.ivs[0].setImageResource(R.drawable.item_collapse);
        }
        

    }

    @Override
    public void regesterListeners(ViewHolder holder, final int position) {
        OnClickListener listener = new OnClickListener() {
            @Override
            public void onClick(View view) {
                mTreeViewAdapter.updateListView(position);
                mTreeViewAdapter.setSelected(position, true);
                
                mDocumentBean = mTreeViewAdapter.getItem(position);
                if (mDocumentBean == null)
                    return;
                if (!mDocumentBean.isHas_child()) {
                    String path = CalculateFilePath(mDocumentBean);
                    mListener.onSubTreeClick(mDocumentBean, path);
                    mDialog.dismiss();
                }
            }
        };
        holder.ivs[0].setOnClickListener(listener);
        holder.tvs[0].setOnClickListener(listener);
    }

	@Override
	public void calculateContentItemCount() {
		
	}

}
