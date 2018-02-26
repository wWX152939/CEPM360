package com.pm360.cepm360.app.module.template;

import android.annotation.SuppressLint;
import android.app.ProgressDialog;
import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.text.TextPaint;
import android.util.Log;
import android.util.TypedValue;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.pm360.cepm360.R;
import com.pm360.cepm360.app.common.activity.ActionBarFragmentActivity;
import com.pm360.cepm360.app.common.activity.DataManagerInterface;
import com.pm360.cepm360.app.common.adpater.DataTreeListAdapter;
import com.pm360.cepm360.app.common.view.OptionsMenuView;
import com.pm360.cepm360.app.common.view.parent.BaseDialog;
import com.pm360.cepm360.app.utils.UtilTools;
import com.pm360.cepm360.common.ResultStatus;
import com.pm360.cepm360.common.json.AnalysisManager;
import com.pm360.cepm360.entity.Templet_WBS_dir;
import com.pm360.cepm360.entity.Templet_document;
import com.pm360.cepm360.entity.Templet_document_dir;
import com.pm360.cepm360.services.templet.RemoteDocumentService;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class DocumentTemplateAddActivity extends ActionBarFragmentActivity
		implements DataManagerInterface,
		DataTreeListAdapter.TreeListAdapterInterface {
	private com.pm360.cepm360.app.common.view.DragView<Templet_WBS_dir> mListView;
	private String[] mDisplayItems;
	private DataTreeListAdapter<Templet_document_dir> mListAdapter;
	private Templet_document_dir mDOCBean;
	private Templet_document mTemplet_Document;
	private Integer mLine;
	private BaseDialog mAddWbsDialog;
	private OptionsMenuView mPopupAttr = null;
	private ArrayList<Templet_document_dir> mDOCList = new ArrayList<Templet_document_dir>();
	private ProgressDialog mProgressDialog;
	List<Templet_document_dir> treelist;
	protected String[] mDialogNames;
	private operation operationflag;
	private Button mAddTemplateButton;
	
	private static final int DETAIL_OPERATION = 1;
	private static final int MODIFY_OPERATION = 2;
	private int runFlag;	
	
	public enum operation {
		ADD_ROOT, ADD_CHILD, MODIFY
	}
	
	@Override
	public int getLayoutId() {
        return R.layout.plan_template_add_list_item;
	}
	@Override
	public void initListViewItem(
	        com.pm360.cepm360.app.common.adpater.DataTreeListAdapter.ViewHolder viewHolder,
			int position) {
		int level = mListAdapter.getItem(position).getLevel();
		viewHolder.ivs[0].setPadding(20 * (level + 1), viewHolder.ivs[0]
                .getPaddingTop(), 20, viewHolder.ivs[0].getPaddingBottom());
		
    	viewHolder.tvs[0].setText(mListAdapter.getItem(position).getName());
    	viewHolder.tvs[0].setTextColor(Color.BLACK);
        TextPaint tp = viewHolder.tvs[0].getPaint();
        if (mListAdapter.getItem(position).isHas_child()) {
        	viewHolder.tvs[0].setTextSize(TypedValue.COMPLEX_UNIT_PX, getResources().getDimensionPixelSize(R.dimen.sp16_s));
            tp.setFakeBoldText(true);
        } else {
        	viewHolder.tvs[0].setTextSize(TypedValue.COMPLEX_UNIT_PX, getResources().getDimensionPixelSize(R.dimen.sp14_s));
            tp.setFakeBoldText(false);
        }           
    }
	
    public Map<String, String> PlanToMap(Templet_document_dir Templet_document_dir) {
        Map<String, String> mapItem = new HashMap<String, String>();
        if (Templet_document_dir instanceof Templet_document_dir) {
            mapItem.put(mDisplayItems[0], Templet_document_dir.getName());
        }
        return mapItem;
    }
	
	@Override
	public void regesterListeners(
	        com.pm360.cepm360.app.common.adpater.DataTreeListAdapter.ViewHolder viewHolder,
			final int position) {
		
		viewHolder.ivs[0].setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                mListAdapter.updateListView(position);
                mListAdapter.setSelected(position, true);
            }
        });

        viewHolder.tvs[0].setClickable(false);
        viewHolder.tvs[0].setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
        		if (runFlag == MODIFY_OPERATION) {  
        			if (mPopupAttr != null) {
		                mPopupAttr.showAsDropDown(
		                        view,
		                        0,
		                        (-view.getMeasuredHeight() - UtilTools.dp2pxH(
		                                view.getContext(), 35)));
        			}
        		}
                mListAdapter.setSelected(position, true);
                mDOCBean = mListAdapter.getItem(position);
                mLine = position;
            }
        });
    }
	@Override
	public void initLayout(View convertView,
	        com.pm360.cepm360.app.common.adpater.DataTreeListAdapter.ViewHolder holder) {
        holder.tvs = new TextView[1];
        holder.tvs[0] = (TextView) convertView.findViewById(R.id.plan_wbs_name);
        holder.ivs = new ImageView[1];
        holder.ivs[0] = (ImageView) convertView.findViewById(R.id.icon);
    }
	
	@SuppressWarnings({ "unchecked", "rawtypes" })
	@SuppressLint("ResourceAsColor")
	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		
		Intent intent = getIntent();
		if (intent.getSerializableExtra("templet_document_detail") != null) {
			mTemplet_Document = (Templet_document) intent
					.getSerializableExtra("templet_document_detail");
			runFlag = DETAIL_OPERATION;
		} else if (intent.getSerializableExtra("templet_document_modify") != null) {	
			mTemplet_Document = (Templet_document) intent
					.getSerializableExtra("templet_document_modify");
			runFlag = MODIFY_OPERATION;
		}
		
		/* 全屏显示 */
		getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,
				WindowManager.LayoutParams.FLAG_FULLSCREEN);

		/* 载入布局文件 */
		setContentView(R.layout.plan_template_add);

		mListView = (com.pm360.cepm360.app.common.view.DragView) findViewById(R.id.listView);
		mListView.setDragViewInterface(new com.pm360.cepm360.app.common.view.DragView.DragViewInterface<Templet_WBS_dir>() {

			@Override
			public int getViewId() {
				return R.id.plan_wbs_name;
			}

			@Override
			public int getSegmentationWidth() {
				return 800;
			}

			@Override
			public void sortPeerId(Templet_WBS_dir lastDragItem, Templet_WBS_dir dragItem) {
				// TODO Auto-generated method stub

				int tmpId = lastDragItem.getId();
				lastDragItem.setId(dragItem.getId());
				dragItem.setId(tmpId);
				
			}

			@Override
			public DataTreeListAdapter<Templet_document_dir> getListAdapter() {
				return mListAdapter; 
			}
		});

        mListAdapter = new DataTreeListAdapter<Templet_document_dir>(this, mDOCList, false, this);
		mListView.setAdapter(mListAdapter);

		loadData();
		if (runFlag == MODIFY_OPERATION) {
			initPopupAttr();
		}	
		mAddWbsDialog = new BaseDialog(this, getResources().getString(
				R.string.document_template_dialog_title));
		
		initWbsCommonWindow();	
		ButtonInit();	
	}
	
	private void ButtonInit() {
		mAddTemplateButton = (Button)findViewById(R.id.template_add_addlist);
		mAddTemplateButton.setOnClickListener(new View.OnClickListener() {
			
			@Override
			public void onClick(View arg0) {
				operationflag = operation.ADD_ROOT;
				mAddWbsDialog.show(null);		
			}
		});
		
	}
	@SuppressLint("UseSparseArrays") 
	private void initWbsCommonWindow() {
		Map<Integer, Integer> buttons = new HashMap<Integer, Integer>();
		Map<Integer, String[]> widgetContent = new HashMap<Integer, String[]>();

		mAddWbsDialog.init(R.array.wbstemplate_maintain_pop, buttons, widgetContent);
		mDialogNames = this.getResources().getStringArray(
				R.array.eps_names);

		Button saveImageView = (Button) mAddWbsDialog.getPopupView()
				.findViewById(R.id.save_Button);

		saveImageView.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View arg0) {
				
				String[] names = getResources().getStringArray(
						R.array.wbstemplate_maintain_pop);

				Map<String, String> saveData = mAddWbsDialog.SaveData();
				Templet_document_dir wbs = new Templet_document_dir();

				switch (operationflag) {
				case ADD_ROOT:
					wbs.setName(saveData.get(names[0]));
					wbs.setParents_id(0);
					wbs.setLevel(0);
					wbs.setTemplet_document_id(mTemplet_Document.getTemplet_document_id());
					showProgressDialog("add_root documentTemplateAdd...");
					RemoteDocumentService.getInstance().adddir(DocumentTemplateAddActivity.this, wbs);
					break;
				case ADD_CHILD:
					wbs.setName(saveData.get(names[0]));
					wbs.setParents_id(mDOCBean.getTemplet_document_dir_id());
					wbs.setLevel(mDOCBean.getLevel() + 1);	
					wbs.setTemplet_document_id(mTemplet_Document.getTemplet_document_id());
					showProgressDialog("add_child documentTemplateAdd...");
					RemoteDocumentService.getInstance().adddir(DocumentTemplateAddActivity.this, wbs);
					break;
				case MODIFY:
					mDOCBean.setName(saveData.get(names[0]));
					showProgressDialog("modify documentTemplateAdd...");
					RemoteDocumentService.getInstance().updateDir(DocumentTemplateAddActivity.this, mDOCBean);
					break;
				default:
					break;
				}

				mAddWbsDialog.dismiss();
			}
		});
	}
	
	private void initPopupAttr() {
		String[] subMenuNames = new String[] {
				getResources().getString(R.string.EPSMaintain_addeps),
				getResources().getString(R.string.EPSMaintain_modify),
				getResources().getString(R.string.EPSMaintain_delete) };
		mPopupAttr = new OptionsMenuView(this, subMenuNames);
		mPopupAttr.setSubMenuListener(new OptionsMenuView.SubMenuListener() {
			@Override
			public void onSubMenuClick(View view) {
				switch ((Integer) view.getTag()) {
				case 0:
					addTicket();
					break;
				case 1:
					modifyTicket();
					break;
				case 2:
					deleteTicket();
					break;
				}
				mPopupAttr.dismiss();
			}
		});
	}
	
	protected void deleteTicket() {	
		if (mDOCBean.isHas_child()) {
			Toast.makeText(
					this,
					getResources()
							.getString(R.string.EPSMaintain_no_delete),
					Toast.LENGTH_SHORT).show();
		} else {
			UtilTools.deleteConfirm(this, new UtilTools.DeleteConfirmInterface() {
				
				@Override
				public void deleteConfirmCallback() {
					showProgressDialog("delete documentTemplateAdd...");
					RemoteDocumentService.getInstance().deleteDir(DocumentTemplateAddActivity.this,
							mDOCBean.getTemplet_document_dir_id());
				}
			});			
		}
		
		if (mDOCBean.getP_templet_document_dir_id() == 0){
			mAddTemplateButton.setVisibility(View.VISIBLE);	
		}
	}
	protected void modifyTicket() {	
		operationflag = operation.MODIFY;
		String[] defaultValues = new String[mDialogNames.length];
		defaultValues[0] = mDOCBean.getName();

		mAddWbsDialog.show(defaultValues);
	}
	protected void addTicket() {		
		operationflag = operation.ADD_CHILD;
		mAddWbsDialog.show(null);
	}
	
    private void showProgressDialog(String text) {
        if (mProgressDialog != null && mProgressDialog.isShowing())
            mProgressDialog.dismiss();
        mProgressDialog = UtilTools.showProgressDialog(this, true, true);
    }

    private void dismissProgressDialog() {
        if (mProgressDialog != null && mProgressDialog.isShowing())
            mProgressDialog.dismiss();
    }	
	
	private void loadData() {
		showProgressDialog("loading documentTemplateAdd...");
		RemoteDocumentService.getInstance().getDocTmp(this, mTemplet_Document.getTemplet_document_id());	
	}
	@Override
	public void calculateContentItemCount() {
		
	}
	@Override
	@SuppressWarnings({ "rawtypes", "unchecked" })
	public void getDataOnResult(ResultStatus status,  List list) {
		dismissProgressDialog();
		if (status.getCode() != AnalysisManager.SUCCESS_DB_QUERY) {
			Message msg = new Message();
			msg.obj = status.getMessage();
			mToastHandler.sendMessage(msg);
		}
		if (status.getCode() == AnalysisManager.SUCCESS_DB_UPDATE) {	
			if (mDOCBean == null){
				return;
			}
			List<Templet_document_dir> docNode = mListAdapter.getShowList();
			List<Templet_document_dir> doclist = mListAdapter.getDataList();
			mListAdapter.updateDataList(
					updateDocNodeList((ArrayList<Templet_document_dir>) docNode),
					updateDoclist((ArrayList<Templet_document_dir>) doclist));
			
		} else if (status.getCode() == AnalysisManager.SUCCESS_DB_ADD) {
			if (list == null || list.size() == 0)
				return;
			
			List<Templet_document_dir> docNode = mListAdapter.getShowList();
			List<Templet_document_dir> doclist = mListAdapter.getDataList();
			if ((mDOCBean == null) || (operationflag == operation.ADD_ROOT)) {
				docNode.add((Templet_document_dir) list.get(0));
				doclist.add((Templet_document_dir) list.get(0));
				mListAdapter.updateDataList(docNode, doclist);
				mAddTemplateButton.setVisibility(View.GONE);
			} else {
				if (!mDOCBean.isExpanded()) {
					mListAdapter.updateListView(mLine);
				}
				Templet_document_dir wbs = Change2Wbs(list);
				mListAdapter.updateDataList(
						insertDocNodeList((ArrayList<Templet_document_dir>) docNode, wbs),
						insertdoclist((ArrayList<Templet_document_dir>) doclist, wbs));
			}

		} else if (status.getCode() == AnalysisManager.SUCCESS_DB_DEL) {
			List<Templet_document_dir> docNode = mListAdapter.getShowList();
			List<Templet_document_dir> doclist = mListAdapter.getDataList();
            mListAdapter.clearSelection(mLine);
            mListAdapter.updateDataList(deleteDoclist((ArrayList<Templet_document_dir>) docNode),
                    deleteDoclist((ArrayList<Templet_document_dir>) doclist));
            mDOCBean = null;

		} else if (status.getCode() == AnalysisManager.SUCCESS_DB_QUERY) {

			if (list != null && list.size() > 0) {
				mDOCList = initDocList((ArrayList<Templet_document_dir>) list);
                mListAdapter.updateDataList(mDOCList);
                mAddTemplateButton.setVisibility(View.GONE);
			} else {
				if (runFlag == MODIFY_OPERATION) {
					mAddTemplateButton.setVisibility(View.VISIBLE);
				} else {
					mAddTemplateButton.setVisibility(View.GONE);
				}
			}
		}
	}
	
	private ArrayList<Templet_document_dir> initDocList(ArrayList<Templet_document_dir> list) 
	{
		ArrayList<Templet_document_dir> doclist = list;

		for (int i = 0; i < doclist.size(); i++) {
			int parent = doclist.get(i).getParents_id();
			for (int j = 0; j < i; j++) {
				if (parent == doclist.get(j).getTemplet_document_dir_id()) {
					int level = doclist.get(j).getLevel() + 1;
					doclist.get(i).setLevel(level);
					doclist.get(j).setHas_child(true);
				}
			}
		}
		return doclist;
	}
	private ArrayList<Templet_document_dir> deleteDoclist(ArrayList<Templet_document_dir> list) {
		ArrayList<Templet_document_dir> doclist = list;

		boolean equalLevel = false;
		for (int i = mLine + 1; i < list.size(); i++) {
			if (doclist.get(i).getLevel() == mDOCBean.getLevel()) {
				equalLevel = true;
				break;
			}
		}
		if (mLine != 0) {
			for (int i = mLine - 1; i >= 0; i--) {
				if (doclist.get(i).getLevel() == mDOCBean.getLevel()) {
					equalLevel = true;
					break;
				}
				if (doclist.get(i).getLevel() < mDOCBean.getLevel()) {
					break;
				}
			}
		}

		if (!equalLevel) {
			for (int i = mLine; i >= 0; i--) {
				if (doclist.get(i).getTemplet_document_dir_id() == mDOCBean.getParents_id()) {
					doclist.get(i).setExpanded(false);
					doclist.get(i).setHas_child(false);
					break;
				}
			}
		}

		for (int i = 0; i < list.size(); i++) {
			if (doclist.get(i).getTemplet_document_dir_id() == mDOCBean.getTemplet_document_dir_id()) {
				doclist.remove(i);
				break;
			}
		}
		return doclist;
	}
	private ArrayList<Templet_document_dir> insertdoclist(ArrayList<Templet_document_dir> list, Templet_document_dir wbs) {
		ArrayList<Templet_document_dir> doclist = list;
		if (list.isEmpty()) {
			doclist.add(wbs);
			return doclist;
		}

		doclist.add(doclist.size(), wbs);

		return doclist;
	}
	private ArrayList<Templet_document_dir> insertDocNodeList(ArrayList<Templet_document_dir> list, Templet_document_dir wbs) 
	{
		ArrayList<Templet_document_dir> doclist = list;
		Log.i("dog2", "list " + list);
		boolean first = true;
		if (list.isEmpty() || (wbs.getParents_id() == 0)) {
			doclist.add(wbs);
			return doclist;
		}

		for (int i = mLine; i < doclist.size(); i++) {
			Log.i("dog2", "count:" + i + "size:" + doclist.size());

			if (first) {
				first = false;
			} else {
				if (doclist.get(i).getLevel() <= mDOCBean.getLevel()) {
					Log.i("dog2",
							"i:" + i + " position-id:" + mDOCBean.getLevel()
									+ " pid:" + doclist.get(i).getLevel());
					if (!mDOCBean.isHas_child()) {
						mDOCBean.setExpanded(true);
						mDOCBean.setHas_child(true);
					}

					doclist.add(i, wbs);
					break;
				}
			}

			Log.i("dog2", "enter3");

			if (i == (doclist.size() - 1)) {
				Log.i("dog2", "last one i:" + i);
				if (!mDOCBean.isHas_child()) {
					mDOCBean.setExpanded(true);
					mDOCBean.setHas_child(true);
				}

				doclist.add(i + 1, wbs);
				break;
			}

		}

		return doclist;
	}
	private Templet_document_dir Change2Wbs(List<Templet_document_dir> list) {
		Templet_document_dir wbs = (Templet_document_dir) list.get(0);
		if (wbs.getP_templet_document_dir_id() == 0) {
			wbs.setLevel(0);
			wbs.setHas_child(false);
		} else {
			wbs.setLevel(mDOCBean.getLevel() + 1);
			wbs.setHas_child(false);
		}

		return wbs;
	}
	private ArrayList<Templet_document_dir> updateDoclist(ArrayList<Templet_document_dir> list) 
	{
		ArrayList<Templet_document_dir> doclist = list;
		doclist.set(mLine, mDOCBean);
		return doclist;
	}
	private ArrayList<Templet_document_dir> updateDocNodeList(ArrayList<Templet_document_dir> list) 
	{
		ArrayList<Templet_document_dir> doclist = list;
		for (int i = 0; i < list.size(); i++) {
			if (list.get(i).getTemplet_document_dir_id() == mDOCBean.getTemplet_document_dir_id()) {
				doclist.set(i, mDOCBean);
			}
		}

		return doclist;
	}

	@SuppressLint("HandlerLeak") 
	public Handler mToastHandler = new Handler() {
		public void handleMessage(Message msg) {
			Toast.makeText(getBaseContext(), (CharSequence) msg.obj,
					Toast.LENGTH_SHORT).show();
		}
	};

}
