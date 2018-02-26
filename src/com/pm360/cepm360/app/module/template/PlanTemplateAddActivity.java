package com.pm360.cepm360.app.module.template;

import android.annotation.SuppressLint;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.util.TypedValue;
import android.view.ActionMode;
import android.view.Gravity;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.View.OnLongClickListener;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.pm360.cepm360.R;
import com.pm360.cepm360.app.common.activity.ActionBarFragmentActivity;
import com.pm360.cepm360.app.common.activity.DataManagerInterface;
import com.pm360.cepm360.app.common.adpater.DataTreeListAdapter;
import com.pm360.cepm360.app.common.view.OptionsMenuView;
import com.pm360.cepm360.app.common.view.parent.BaseDialog;
import com.pm360.cepm360.app.common.view.parent.DoubleDatePickerDialog;
import com.pm360.cepm360.app.utils.UtilTools;
import com.pm360.cepm360.common.ResultStatus;
import com.pm360.cepm360.common.json.AnalysisManager;
import com.pm360.cepm360.common.util.DateUtils;
import com.pm360.cepm360.common.util.MiscUtils;
import com.pm360.cepm360.core.TreePresenter;
import com.pm360.cepm360.entity.Templet_WBS;
import com.pm360.cepm360.entity.Templet_WBS_dir;
import com.pm360.cepm360.services.templet.RemoteWBSService;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class PlanTemplateAddActivity extends ActionBarFragmentActivity {
	
	private com.pm360.cepm360.app.common.view.DragView<Templet_WBS_dir> mListView;
	private String[] mDisplayItems;
	private int[] mDisplayIds;
	private DataTreeListAdapter<Templet_WBS_dir> mListAdapter;
	private Templet_WBS_dir mWBSBean;
	private Templet_WBS mTemplet_WBS;
	private Integer mLine;
	private BaseDialog mAddWbsDialog;
	private OptionsMenuView mPopupAttr = null;
	private ArrayList<Templet_WBS_dir> mWBSList = new ArrayList<Templet_WBS_dir>();
	private ProgressDialog mProgressDialog;
	private operation operationflag;
	private Button mAddTemplateButton;
	
	// 数据处理类
	private TreePresenter<Templet_WBS_dir> mTreePresenter;
	// 模式定义，普通模式和批量操作模式
	private ActionMode mActionMode;
	
	private static final int DETAIL_OPERATION = 1;
	private static final int MODIFY_OPERATION = 2;
	private int runFlag;
	
	public enum operation {
		ADD_ROOT, ADD_CHILD, INSERT, MODIFY
	}
	
	private DataTreeListAdapter.TreeListAdapterInterface mListInterface = new DataTreeListAdapter.TreeListAdapterInterface() {
		
		@Override
		public void calculateContentItemCount() {	
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
			
			Map<String, String> map = MiscUtils.beanToMap(mListAdapter.getItem(position));
	    	for (int i = 0; i < viewHolder.tvs.length; i++) {
		    	viewHolder.tvs[i].setText(map.get(mDisplayItems[i]));
	    		viewHolder.tvs[i].setTextColor(Color.BLACK);
		        viewHolder.tvs[i].setTextSize(TypedValue.COMPLEX_UNIT_PX, getResources().getDimensionPixelSize(R.dimen.table_content_textsize));
	    	}
	    	           
	    }
		
		@Override
		public void regesterListeners(
		        com.pm360.cepm360.app.common.adpater.DataTreeListAdapter.ViewHolder viewHolder,
				final int position) {
			
			viewHolder.ivs[0].setOnClickListener(new View.OnClickListener() {
	            @Override
	            public void onClick(View view) {
	            	leftClickEvent(position);
	            }
	        });
			
			viewHolder.tvs[0].setOnClickListener(new View.OnClickListener() {
	            @Override
	            public void onClick(View view) {
	            	leftClickEvent(position);
	            }
	        });

			for (int i = 1; i < viewHolder.tvs.length; i++) {
		        viewHolder.tvs[i].setOnClickListener(new View.OnClickListener() {
		            @Override
		            public void onClick(View view) {
		            	if (mActionMode != null) {
							mListAdapter.setPickSelected(position);
		                	mDeleteList = mTreePresenter.calculateMutilTaskOfChildren(mListAdapter, mListAdapter.getSelected());
							updateActionModeTitle(mActionMode, getBaseContext(), mDeleteList.size());
						} else {
							if (runFlag == MODIFY_OPERATION) {
			        			if (mPopupAttr != null) {
			        				if (mListAdapter.getItem(position).getParents_id() == 0) {
			        					mPopupAttr.setVisibileMenu(1, false);
			        				} else {
			        					mPopupAttr.setVisibileMenu(1, true);
			        				}
					                mPopupAttr.showAsDropDown(
					                        view,
					                        0,
					                        (-view.getMeasuredHeight() - UtilTools.dp2pxH(
					                                view.getContext(), 35)));
			        			}
			        		}
			                mListAdapter.setSelected(position, true);
			                mWBSBean = mListAdapter.getItem(position);
			                mLine = position;
						}
		            }
		        });
		        viewHolder.tvs[i].setOnLongClickListener(new OnLongClickListener() {
					
					@Override
					public boolean onLongClick(View v) {
                    	// 长按进入ActionMode，此时ActionMode应该是NULL
						if (runFlag == MODIFY_OPERATION) {
							if (mActionMode == null) {
								mActionMode = startActionMode(mCallback);
		                        mListAdapter.clearSelectionAll();
		                        updateActionModeTitle(mActionMode, getBaseContext(),
		                                				mListAdapter.getSelected().size());
							}
						}
                    	
                        return true;
					}
				});
			}
	    }
		
		@Override
		public void initLayout(View convertView,
		        com.pm360.cepm360.app.common.adpater.DataTreeListAdapter.ViewHolder holder) {
	        holder.tvs = new TextView[4];
	        for (int i = 0; i < holder.tvs.length; i++) {
	        	holder.tvs[i] = (TextView) convertView.findViewById(mDisplayIds[i]);
	        }
	        holder.ivs = new ImageView[1];
	        holder.ivs[0] = (ImageView) convertView.findViewById(R.id.icon);
	    }
	};
	
	private void leftClickEvent(int position) {
		mListAdapter.updateListView(position);
		if (mActionMode == null) {
	        mListAdapter.setSelected(position, true);	
		}
	}
	
	public ActionMode startActionMode(ActionMode.Callback callback) {
		mListAdapter.setEnableMultiSelectMode(true);
		return super.startActionMode(callback);
    }
	
	private void initDefaultData() {
		mDisplayItems = new String[] {
			"name", "duration", "start_time", "end_time"	
		};
		
		mDisplayIds = new int[] {
			R.id.plan_wbs_name, R.id.plan_duration,
			R.id.start_time, R.id.end_time
		};
		mTreePresenter = new TreePresenter<Templet_WBS_dir>(this);
	}
	
	/**
     * 用于列表的多选 或批量操作
     */
    private ActionMode.Callback mCallback = new ActionMode.Callback() {
    	// 进入批量操作前的准备工作
        @Override
        public boolean onPrepareActionMode(ActionMode mode, Menu menu) {
            return false;
        }

        // 退出批量操作前的清理工作
        @Override
        public void onDestroyActionMode(ActionMode mode) {
            mListAdapter.setSelected(-1, false);
    		mListAdapter.setEnableMultiSelectMode(false);
    		mListAdapter.clearSelectionAll();
            mActionMode = null;
            mode = null;
        }

        // 进入批量操作初始化工作
        @Override
        public boolean onCreateActionMode(ActionMode mode, Menu menu) {
            MenuInflater inflater = mode.getMenuInflater();
            inflater.inflate(R.menu.operation_menu, menu);
            return true;
        }

        // 批量操作处理
        @Override
        public boolean onActionItemClicked(ActionMode mode, MenuItem item) {
            switch (item.getItemId()) {
	            case R.id.action_delete:	// 删除操作
	            	if (mListAdapter.getSelected().isEmpty()) {
	            		Toast.makeText(getBaseContext(), R.string.pls_select_task, Toast.LENGTH_SHORT).show();
	            		return false;
	            	}
	            	deleteTicket();
	                break;
	                
	            case R.id.action_select_all:	// 权限操作
	                mListAdapter.setSelectedAll();
	                updateActionModeTitle(  mActionMode, 
    						getBaseContext(), 
    						mListAdapter.getDataList().size());
	                break;
            }
            return false;
        }
    };
	
	/**
     * 当增加或减少选择项时，更新ActionMode标题
     * @param mode
     * @param context
     * @param selectedNum
     */
    private void updateActionModeTitle(  ActionMode mode, 
    									Context context,
    									int selectedNum) {
        if (mode != null) {
            mode.setTitle(context.getString(R.string.multi_select_title, selectedNum));
        }
    }
	
	private void initListHeader() {
		LinearLayout line = (LinearLayout) findViewById(R.id.list_header);
		line.findViewById(R.id.icon).setVisibility(View.GONE);
		String[] names = getResources().getStringArray(R.array.plan_template_list_names);
		for (int i = 0; i < mDisplayIds.length; i++) {
			TextView tv = (TextView) 
					line.findViewById(mDisplayIds[i]);
			tv.setTextSize(TypedValue.COMPLEX_UNIT_PX, getResources().getDimension(R.dimen.table_title_textsize));
			tv.setText(names[i]);
			tv.setGravity(Gravity.CENTER);
		}
	}
	
	@SuppressWarnings({ "unchecked", "rawtypes" })
	@SuppressLint("ResourceAsColor")
	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		initDefaultData();
		
		Intent intent = getIntent();
		if (intent.getSerializableExtra("plan_template_info") != null) {
			mTemplet_WBS = (Templet_WBS) intent
					.getSerializableExtra("plan_template_info");
			runFlag = DETAIL_OPERATION;
		} else if (intent.getSerializableExtra("plan_template_modify") != null) {
			mTemplet_WBS = (Templet_WBS) intent
					.getSerializableExtra("plan_template_modify");
			runFlag = MODIFY_OPERATION;
		}
		/* 全屏显示 */
		getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,
				WindowManager.LayoutParams.FLAG_FULLSCREEN);

		/* 载入布局文件 */
		setContentView(R.layout.plan_template_add);
		initListHeader();
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
			public DataTreeListAdapter<Templet_WBS_dir> getListAdapter() {
				return mListAdapter; 
			}
		});
        mListAdapter = new DataTreeListAdapter<Templet_WBS_dir>(this, mWBSList, false, mListInterface);
		mListView.setAdapter(mListAdapter);

		loadData();
		if (runFlag == MODIFY_OPERATION) {
			initPopupAttr();
		}		
		
		mAddWbsDialog = new BaseDialog(this, getResources().getString(
				R.string.plan_template_dialog_title));
		
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
	
	private Templet_WBS_dir mTempWBSDir;
	
	@SuppressLint("UseSparseArrays") 
	private void initWbsCommonWindow() {
		Map<Integer, Integer> buttons = new HashMap<Integer, Integer>();
		buttons.put(1, BaseDialog.calendarLineStyle);
		buttons.put(2, BaseDialog.calendarLineStyle);

		mAddWbsDialog.init(R.array.wbstemplate_maintain_pop, buttons, null);
		final EditText startEt = (EditText) mAddWbsDialog.getEditTextView(1);
		final EditText endEt = (EditText) mAddWbsDialog.getEditTextView(2);
		final DoubleDatePickerDialog doubleDatePickerDialog = new DoubleDatePickerDialog(
				this, null, startEt, endEt, null);
		OnClickListener dateListener = new OnClickListener() {

			@Override
			public void onClick(View v) {
				
				if (operationflag == operation.ADD_CHILD) {
					Templet_WBS_dir taskParent = null;
					for (int i = mLine - 1; i >= 0; i--) {

						if (mListAdapter.getShowList().get(i)
								.getLevel() < mWBSBean.getLevel()) {
							taskParent = mListAdapter.getShowList()
									.get(i);
							break;
						}
					}
					
					if (taskParent == null) {
						taskParent = mWBSBean;
					}
					doubleDatePickerDialog.show(
							taskParent.getStart_time(),
							taskParent.getStart_time());
				} else if (operationflag == operation.MODIFY) { 
					doubleDatePickerDialog.show(
							mWBSBean.getStart_time(),
							mWBSBean.getEnd_time());
				} else {
					doubleDatePickerDialog.show(null, null);
				}
				
			}
		};
		startEt.setOnClickListener(dateListener);
		endEt.setOnClickListener(dateListener);
		
		Button saveImageView = (Button) mAddWbsDialog.getPopupView()
				.findViewById(R.id.save_Button);

		saveImageView.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View arg0) {
				String[] names = getResources().getStringArray(
						R.array.wbstemplate_maintain_pop);

				Map<String, String> saveData = mAddWbsDialog.SaveData();
				if (saveData.get(names[0]).isEmpty()) {
					Toast.makeText(PlanTemplateAddActivity.this, R.string.pls_input_all_info, Toast.LENGTH_SHORT).show();
					return;
				}
				
				if (operationflag == operation.MODIFY) {
					mTempWBSDir = MiscUtils.clone(mWBSBean);
				} else {
					mTempWBSDir = new Templet_WBS_dir();
				}
				mTempWBSDir.setName(saveData.get(names[0]));
				mTempWBSDir.setStart_time(DateUtils.stringToDate(DateUtils.FORMAT_SHORT, saveData.get(names[1])));
				mTempWBSDir.setEnd_time(DateUtils.stringToDate(DateUtils.FORMAT_SHORT, saveData.get(names[2])));
				if (mTempWBSDir.getStart_time() != null && !mTempWBSDir.getStart_time().equals("")
						&& mTempWBSDir.getEnd_time() != null && !mTempWBSDir.getEnd_time().equals("")) {
					String duration = mTreePresenter.calculateDuration(mTempWBSDir.getStart_time(), mTempWBSDir.getEnd_time());
					mTempWBSDir.setDuration(duration);
				}
				
				switch (operationflag) {
				case ADD_ROOT:
					mTempWBSDir.setParents_id(0);
					mTempWBSDir.setLevel(0);
					mTempWBSDir.setTemplet_wbs_id(mTemplet_WBS.getTemplet_wbs_id());
					showProgressDialog("add_root planTemplateAdd...");
					RemoteWBSService.getInstance().adddir(mDataManagerInterface, mTempWBSDir);
					break;
				case ADD_CHILD:
					mTempWBSDir.setParents_id(mWBSBean.getTemplet_wbs_dir_id());
					mTempWBSDir.setLevel(mWBSBean.getLevel() + 1);	
					mTempWBSDir.setTemplet_wbs_id(mTemplet_WBS.getTemplet_wbs_id());
					mTempWBSDir.setSort(TreePresenter.getAddSortNum(mListAdapter, mLine));
					showProgressDialog("add_child planTemplateAdd...");
					RemoteWBSService.getInstance().adddir(mDataManagerInterface, mTempWBSDir);
					break;
				case MODIFY:
					showProgressDialog("modify planTemplateAdd...");
					RemoteWBSService.getInstance().updateDir(mDataManagerInterface, mTempWBSDir);
					break;
				case INSERT:
					mTempWBSDir.setParents_id(mWBSBean.getP_templet_wbs_dir_id());
					mTempWBSDir.setLevel(mWBSBean.getLevel());	
					mTempWBSDir.setTemplet_wbs_id(mTemplet_WBS.getTemplet_wbs_id());
					mTempWBSDir.setSort(TreePresenter.getInsertSortNum(mListAdapter, mLine));
					showProgressDialog("insert_child planTemplateAdd...");
					RemoteWBSService.getInstance().adddir(mDataManagerInterface, mTempWBSDir);
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
				getResources().getString(R.string.add),
				getResources().getString(R.string.insert),
				getResources().getString(R.string.modify),
				getResources().getString(R.string.delete)};
		mPopupAttr = new OptionsMenuView(this, subMenuNames);
		mPopupAttr.setSubMenuListener(new OptionsMenuView.SubMenuListener() {
			@Override
			public void onSubMenuClick(View view) {
				System.currentTimeMillis();
				switch ((Integer) view.getTag()) {
				case 0:
					mAddWbsDialog.setEditTextStyle(1, 0, null, getResources().getString(R.string.parent_start_time)
							+ DateUtils.dateToString(DateUtils.FORMAT_SHORT, mWBSBean.getStart_time()));
					mAddWbsDialog.setEditTextStyle(2, 0, null, getResources().getString(R.string.parent_end_time) 
							+ DateUtils.dateToString(DateUtils.FORMAT_SHORT, mWBSBean.getEnd_time()));
					addTicket();
					break;
				case 1:
					insertTicket();
					break;
				case 2:
					mAddWbsDialog.setEditTextStyle(1, 0, null, "");
					mAddWbsDialog.setEditTextStyle(2, 0, null, "");
					modifyTicket();
					break;
				case 3:
					deleteTicket();
					break;
				}
				mPopupAttr.dismiss();
			}
		});
	}
	
	private List<Templet_WBS_dir> mDeleteList = new ArrayList<Templet_WBS_dir>();
	protected void deleteTicket() {
		UtilTools.deleteConfirm(this, new UtilTools.DeleteConfirmInterface() {
			
			@Override
			public void deleteConfirmCallback() {
				showProgressDialog("delete planTemplateAdd...");
				
				// 如果mActionMode不为空，说明是批量删除模式
                if (mActionMode != null) {
    				String taskId = "";
    				for (Templet_WBS_dir dir : mDeleteList) {
    					taskId += dir.getId()  + ",";
    				}
    				RemoteWBSService.getInstance().deleteDir(mDataManagerInterface, taskId.substring(0, taskId.length() - 1));
                    return;
                }
                
				
				String taskId = mWBSBean.getId() + "";
				if (mWBSBean.isHas_child()) {
					ArrayList<Integer> deleteList = mTreePresenter
							.calculateTaskOfChildren(
									(ArrayList<Templet_WBS_dir>) mListAdapter.getDataList(),
									mWBSBean);
					for (int i = 0; i < deleteList.size(); i++) {
						taskId += "," + mListAdapter.getDataList().get(deleteList.get(i))
								.getId();
					}
					RemoteWBSService.getInstance().deleteDir(mDataManagerInterface, taskId);
					
				} else {
					RemoteWBSService.getInstance().deleteDir(mDataManagerInterface, taskId);
				}
			}
		});	
			
	}
	protected void modifyTicket() {		
		operationflag = operation.MODIFY;
		String[] defaultValues = new String[3];
		defaultValues[0] = mWBSBean.getName();
		defaultValues[1] = DateUtils.dateToString(DateUtils.FORMAT_SHORT, mWBSBean.getStart_time());
		defaultValues[2] = DateUtils.dateToString(DateUtils.FORMAT_SHORT, mWBSBean.getEnd_time());

		mAddWbsDialog.show(defaultValues);
	}
	protected void addTicket() {		
		operationflag = operation.ADD_CHILD;
		mAddWbsDialog.show(null);
	}
	protected void insertTicket() {		
		operationflag = operation.INSERT;
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
		showProgressDialog("loading planTemplateAdd...");
		RemoteWBSService.getInstance().getWBSTmp(mDataManagerInterface, mTemplet_WBS.getTemplet_wbs_id());	
	}
	
	@SuppressWarnings({ "rawtypes", "unchecked" })
	private DataManagerInterface mDataManagerInterface = new DataManagerInterface() {
		public void getDataOnResult(ResultStatus status, List list) {
			dismissProgressDialog();
			if (status.getCode() != AnalysisManager.SUCCESS_DB_QUERY) {
				Message msg = new Message();
				Bundle bundle = new Bundle();
				bundle.putString("123", status.getMessage());
				msg.setData(bundle);
				mToastHandler.sendMessage(msg);
			}
			if (status.getCode() == AnalysisManager.SUCCESS_DB_UPDATE) {
				if (mWBSBean == null){
					return;
				}
				MiscUtils.clone(mWBSBean, mTempWBSDir);
				mListAdapter.notifyDataSetChanged();

			} else if (status.getCode() == AnalysisManager.SUCCESS_DB_ADD) {
				if (list == null || list.size() == 0)
					return;
				mWBSBean = (Templet_WBS_dir) list.get(0);
				if (operationflag == operation.ADD_CHILD) {
					TreePresenter.addToTreeList(mListAdapter, mWBSBean, mLine);
				} else if (operationflag == operation.INSERT) {
					TreePresenter.insertToTreeList(mListAdapter, mWBSBean, mLine);
				} else if (operationflag == operation.ADD_ROOT) {
					TreePresenter.addToTreeList(mListAdapter, mWBSBean, 0);
					mAddTemplateButton.setVisibility(View.GONE);
				}

			} else if (status.getCode() == AnalysisManager.SUCCESS_DB_DEL) {
				if (mActionMode != null) {
					mListAdapter.deleteMultiTreeNode(mDeleteList);
					updateActionModeTitle(mActionMode, getBaseContext(), 0);
					
				} else {
					mTreePresenter.deleteTaskNodeList((ArrayList<Templet_WBS_dir>) mListAdapter.getShowList(), mWBSBean, mLine);
					mTreePresenter.deleteTaskList((ArrayList<Templet_WBS_dir>) mListAdapter.getDataList(), mWBSBean);
					mListAdapter.notifyDataSetChanged();
				}

	    		if (mListAdapter.getShowList().isEmpty()) {
	    			mAddTemplateButton.setVisibility(View.VISIBLE);	
	    		}	
	            mWBSBean = null;
			} else if (status.getCode() == AnalysisManager.SUCCESS_DB_QUERY) {

				if (list != null && list.size() > 0) {
					mWBSList = initWbsList((ArrayList<Templet_WBS_dir>) list);
	                mListAdapter.updateDataList(mWBSList);
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
	};
	
	private ArrayList<Templet_WBS_dir> initWbsList(ArrayList<Templet_WBS_dir> list) 
	{
		ArrayList<Templet_WBS_dir> wbslist = list;

		for (int i = 0; i < wbslist.size(); i++) {
			int parent = wbslist.get(i).getParents_id();
			for (int j = 0; j < i; j++) {
				if (parent == wbslist.get(j).getTemplet_wbs_dir_id()) {
					int level = wbslist.get(j).getLevel() + 1;
					wbslist.get(i).setLevel(level);
					wbslist.get(j).setHas_child(true);
				}
			}
		}
		return wbslist;
	}

	@SuppressLint("HandlerLeak") 
	public Handler mToastHandler = new Handler() {
		public void handleMessage(Message msg) {

			Toast.makeText(getBaseContext(), msg.getData().getString("123"),
					Toast.LENGTH_SHORT).show();

		}
	};


}
