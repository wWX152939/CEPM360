package com.pm360.cepm360.app.module.home.portal;

import android.app.Activity;
import android.app.DatePickerDialog;
import android.app.Dialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.content.res.TypedArray;
import android.graphics.Color;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.v4.app.Fragment;
import android.text.Spannable;
import android.text.SpannableStringBuilder;
import android.text.style.ForegroundColorSpan;
import android.util.TypedValue;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.view.animation.TranslateAnimation;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.pm360.cepm360.R;
import com.pm360.cepm360.app.CepmApplication;
import com.pm360.cepm360.app.cache.ProjectCache;
import com.pm360.cepm360.app.cache.UserCache;
import com.pm360.cepm360.app.common.activity.DataManagerInterface;
import com.pm360.cepm360.app.common.activity.ProjectSelectActivity;
import com.pm360.cepm360.app.common.adpater.DataListAdapter;
import com.pm360.cepm360.app.common.adpater.DataListAdapter.ListAdapterInterface;
import com.pm360.cepm360.app.common.adpater.DataListAdapter.ViewHolder;
import com.pm360.cepm360.app.common.custinterface.SimplePageChangeListenerInterface;
import com.pm360.cepm360.app.common.view.parent.BaseViewPager;
import com.pm360.cepm360.app.module.combination.RiskFragment;
import com.pm360.cepm360.app.module.combination.SubFeedbackFragment;
import com.pm360.cepm360.app.module.combination.WorkLogFragment;
import com.pm360.cepm360.app.module.common.plan.CommonDocumentFragment;
import com.pm360.cepm360.app.module.schedule.QualityFragment;
import com.pm360.cepm360.app.module.schedule.SafetyFragment;
import com.pm360.cepm360.app.utils.UtilTools;
import com.pm360.cepm360.common.GLOBAL;
import com.pm360.cepm360.common.ResultStatus;
import com.pm360.cepm360.common.json.AnalysisManager;
import com.pm360.cepm360.common.util.DateUtils;
import com.pm360.cepm360.entity.Feedback;
import com.pm360.cepm360.entity.FeedbackCell;
import com.pm360.cepm360.entity.Index_task;
import com.pm360.cepm360.entity.Project;
import com.pm360.cepm360.entity.Task;
import com.pm360.cepm360.entity.TaskCell;
import com.pm360.cepm360.entity.ZH_group;
import com.pm360.cepm360.entity.ZH_group_feedback;
import com.pm360.cepm360.entity.ZH_group_task;
import com.pm360.cepm360.services.plan.RemoteTaskService;
import com.pm360.cepm360.services.query.RemoteIndexService;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Locale;
import java.util.Map;

public class MyTaskFragment extends Fragment {
    
	public static final String TASK_POSITION_KEY = "task_position_key";
	public static final String TASK_INFO_KEY = "task_info_key";
    private static final int PROJECT_REQUEST_CODE = 100;
	
	/**--  View布局  --**/
    private View mRootView;
    private View mHeaderView;
    private View mSecondView;
    private TextView mProjectText;
    private ImageView mQueryMenu;
    private ListView mLeftListView;
    private ListView mListView;
    
    private ProgressDialog mProgressDialog;
    private PortalMoreListActivity mActivity;
    private Dialog mQueryDialog;
	private BaseViewPager mBaseViewPager;
    
    /**--  Data数据  --**/
	private FeedbackCell mCurrentFeedback;
	private Index_task mCurrentIndexTask;
    private DataListAdapter<Index_task> mAdapter;
    private String[] mMenuArray;
    private List<Index_task> mShowList = new ArrayList<Index_task>();
    private List<List<Index_task>> mDataList;
    private int mCurrentPosition = 0;
    private Calendar mCalendar = Calendar
            .getInstance(Locale.SIMPLIFIED_CHINESE);
    
    public final int MSG_ZH_TYPE = Integer.parseInt(GLOBAL.MSG_TYPE_KEY[15][0]);
    public final int MSG_TASK_TYPE = Integer.parseInt(GLOBAL.MSG_TYPE_KEY[7][0]);
    
    // 列表头显示名称
    private String[] mHeaderNames;
    private int[] mDisplayItemIds;
    
    private int mTaskStatus = -1;
    private List<Integer> mNumList;
    private int mProjectId;
    
    private Handler mHandler = new Handler(new Handler.Callback() {
        @Override
        public boolean handleMessage(Message msg) {
            mAdapter.notifyDataSetChanged();
            return false;
        }
    });
    
    @SuppressWarnings("unchecked")
	@Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
            Bundle savedInstanceState) {
        super.onCreateView(inflater, container, savedInstanceState);
        
        mActivity = (PortalMoreListActivity) getActivity();
        mRootView = inflater.inflate(
                R.layout.portal_mytask_fragment_layout, container, false);
              
        mTaskStatus = getArguments().getInt(TASK_POSITION_KEY);
        mDataList = (List<List<Index_task>>) getArguments().getSerializable(TASK_INFO_KEY);
        mNumList = new ArrayList<>();
        for (List<Index_task> taskList : mDataList) {
        	mNumList.add(taskList.size());
        }
        
        initLeftMenuView(mRootView);
        initContentView(mRootView);
        initQueryMenu(mRootView);
        
        
        initSecondWindow();
        formatDataList(mTaskStatus);
        return mRootView;
    }
    
    /**
     * 初始化属性界面
     */
    private void initSecondWindow() {
    	mSecondView = mRootView.findViewById(R.id.baseViewPager);
    	View view = mRootView.findViewById(R.id.base_title_left);
    	view.setVisibility(View.VISIBLE);
    	
		final TranslateAnimation hiddenAction = new TranslateAnimation(Animation.RELATIVE_TO_SELF,
				0.0f, Animation.RELATIVE_TO_SELF, 1.0f,
				Animation.RELATIVE_TO_SELF, 0.0f, Animation.RELATIVE_TO_SELF,
				0.0f);
		hiddenAction.setDuration(500);
    	view.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View v) {
				mSecondView.setVisibility(View.GONE);
				mSecondView.startAnimation(hiddenAction);
			}
		});
    	
    	mBaseViewPager = new BaseViewPager(getActivity(), mSecondView);
    }
    
    private void formatLeftListColor(int position) {
    	for (int i = 0; i < mMenuArray.length; i++) {
    		if (i == position) {
    			if (mLeftListView.getChildAt(i) != null) {
    				mLeftListView.getChildAt(i).setBackgroundResource(R.color.listview_selected_bg);
    			}
    			
    		} else {
    			if (mLeftListView.getChildAt(i) != null) {
    				mLeftListView.getChildAt(i).setBackgroundColor(Color.TRANSPARENT);
    			}
    			
    		}
    	}
    }
    
    private void setCurrentShowList(int position) {
    	mShowList.clear();
    	if (mDataList != null) {
    		if (position == -1) {
    			for (int i = 0; i < 6; i++) {
    				for (int j = 0; j < mDataList.get(i).size(); j++) {
						int k = 0;
						for (k = 0; k < mShowList.size(); k++) {
    						if (mShowList.get(k).getTask_id() == mDataList.get(i).get(j).getTask_id()) {
    							break;
    						}
    					}
						if (k == mShowList.size()) {
							mShowList.add(mDataList.get(i).get(j));
						}
    				}
    			}
    			
    		} else {
    			mShowList.addAll(mDataList.get(position));
    		}
    	}
    }
    
    private void formatDataList(int position) {
    	mCurrentPosition = position;
    	formatLeftListColor(position);
    	setCurrentShowList(position);
    	mHandler.sendEmptyMessage(0);
    }
    
    private void doQuery(Index_task bean) {
//        int type = ((CepmApplication) mActivity.getApplicationContext()).getEnterpriseType();
//        User user = UserCache.getCurrentUser();
//        bean.setTenant_id(user.getTenant_id());
//        bean.setUser_id(user.getUser_id());
//        bean.setTenant_type(String.valueOf(type + 1));
//        mActivity.showProgressDialog("MyTask doQuery...");
    	
    	List<Index_task> tempList = new ArrayList<Index_task>();
    	setCurrentShowList(mCurrentPosition);
		tempList.addAll(mShowList);
		
		// 名称条件筛选
    	if (!bean.getName().isEmpty()) {
    		Iterator<Index_task> iterator = tempList.iterator();
            while (iterator.hasNext()) {
            	Index_task task = iterator.next();
            	if (!bean.getName().equals(task.getName())) {
            		iterator.remove();
        		}
            }
    	}
    	
    	// 项目添加筛选
    	if (bean.getProject_id() != 0) {
    		Iterator<Index_task> iterator = tempList.iterator();
            while (iterator.hasNext()) {
            	Index_task task = iterator.next();
            	if (bean.getProject_id() != task.getProject_id()) {
            		iterator.remove();
        		}
            }
    	}
    	
    	// 状态筛选
    	if (bean.getProgress() != 0) {
    		if (bean.getProgress() == 1) {
    			// 未开始
    			Iterator<Index_task> iterator = tempList.iterator();
                while (iterator.hasNext()) {
                	Index_task task = iterator.next();
            		if (0 != task.getProgress()) {
                		iterator.remove();
            		}
                }
    		} else if (bean.getProgress() == 2) {
    			// 进行中
    			Iterator<Index_task> iterator = tempList.iterator();
                while (iterator.hasNext()) {
                	Index_task task = iterator.next();
            		if (0 == task.getProgress() || 100 == task.getProgress()) {
                		iterator.remove();
            		}
                }
    		} else if (bean.getProgress() == 3) {
    			// 完成
    			Iterator<Index_task> iterator = tempList.iterator();
                while (iterator.hasNext()) {
                	Index_task task = iterator.next();
            		if (100 != task.getProgress()) {
                		iterator.remove();
            		}
                }
    		}
    	}
    	
    	// 时间筛选
    	if (bean.getStart_time() != null || bean.getEnd_time() != null) {
    		if (bean.getStart_time() != null) {
    			Iterator<Index_task> iterator = tempList.iterator();
                while (iterator.hasNext()) {
                	Index_task task = iterator.next();
            		if (bean.getStart_time().getTime() - task.getStart_time().getTime() > 0) {
                		iterator.remove();
            		}
                }
    		}
    		
    		if (bean.getEnd_time() != null) {
    			Iterator<Index_task> iterator = tempList.iterator();
                while (iterator.hasNext()) {
                	Index_task task = iterator.next();
    				if (bean.getEnd_time().getTime() - task.getEnd_time().getTime() < 0) {
                		iterator.remove();
            		}
                }
    		}
    	}
    	
    	mShowList.clear();
    	mShowList.addAll(tempList);
        mHandler.sendEmptyMessage(0);
    }
    
    private void showDatePickerDialog(final TextView textView) {
        new DatePickerDialog(mActivity,
                new DatePickerDialog.OnDateSetListener() {

                    @Override
                    public void onDateSet(DatePicker view, int year,
                            int monthOfYear, int dayOfMonth) {
                        mCalendar.set(Calendar.YEAR, year);
                        mCalendar.set(Calendar.MONTH, monthOfYear);
                        mCalendar.set(Calendar.DAY_OF_MONTH, dayOfMonth);
                        textView.setText(DateUtils.dateToString(DateUtils.FORMAT_SHORT, mCalendar.getTime()));
                    }
                }, mCalendar.get(Calendar.YEAR), mCalendar.get(Calendar.MONTH),
                mCalendar.get(Calendar.DAY_OF_MONTH)).show();
    }
    
    private void initQueryMenu(View view) {
        mQueryDialog = new Dialog(mActivity, R.style.MyDialogStyle);
        mQueryDialog.setContentView(R.layout.portal_more_mytask_query_dialog);
        mQueryDialog.setCanceledOnTouchOutside(true);
        
        final EditText condition1 = (EditText) mQueryDialog.findViewById(R.id.condition1);
        mProjectText = (TextView) mQueryDialog.findViewById(R.id.condition2);
        final Spinner condition3 = (Spinner) mQueryDialog.findViewById(R.id.condition3);
        final TextView condition4 = (TextView) mQueryDialog.findViewById(R.id.condition4);
        final TextView condition5 = (TextView) mQueryDialog.findViewById(R.id.condition5);
        
        mProjectText.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View view) {
                Intent intent = new Intent(mActivity,
                        ProjectSelectActivity.class);
                Bundle bundle = new Bundle();
                bundle.putInt("action", ProjectSelectActivity.ACTION_PICK);
                intent.putExtras(bundle);
                startActivityForResult(intent, PROJECT_REQUEST_CODE);
            }            
        });
        
        String[] condition3Items = new String[] { getString(R.string.all), GLOBAL.TASK_STATUS_TYPE[0][1],
                GLOBAL.TASK_STATUS_TYPE[1][1],  GLOBAL.TASK_STATUS_TYPE[2][1]};
        ArrayAdapter<String> condition3Adapter = new ArrayAdapter<String>(
                mActivity, R.layout.spinner_item, condition3Items);
        condition3Adapter.setDropDownViewResource(R.layout.spinner_dropdown_item);
        condition3.setAdapter(condition3Adapter);
        
        condition4.setOnClickListener(new View.OnClickListener() {
            
            @Override
            public void onClick(View view) {
                showDatePickerDialog((TextView) view);
            }
        });
        condition5.setOnClickListener(new View.OnClickListener() {
            
            @Override
            public void onClick(View view) {
                showDatePickerDialog((TextView) view);
            }
        });
        
        Button queryButton = (Button) mQueryDialog.findViewById(R.id.button_query);
        Button cancelButton = (Button) mQueryDialog.findViewById(R.id.button_clear);
        View.OnClickListener listener = new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                switch (v.getId()) {
                case R.id.button_clear:
                    condition1.setText("");
                    condition3.setSelection(0);
                    condition4.setText("");
                    condition5.setText("");
                    mProjectText.setText("");
                    mProjectId = 0;
                    break;
                case R.id.button_query:
                    Index_task bean = new Index_task();
                    bean.setName(condition1.getText().toString());
                    bean.setProject_id(mProjectId);
                    bean.setProgress(condition3.getSelectedItemPosition());
                    bean.setStart_time(DateUtils.stringToDate(DateUtils.FORMAT_SHORT, condition4.getText().toString()));
                    bean.setEnd_time(DateUtils.stringToDate(DateUtils.FORMAT_SHORT, condition5.getText().toString()));
                    doQuery(bean);
                    mQueryDialog.dismiss();
                    break;
                }
            }
        };
        queryButton.setOnClickListener(listener);
        cancelButton.setOnClickListener(listener);
        
        mQueryMenu = (ImageView) view.findViewById(R.id.floating_query_menu);
        mQueryMenu.setOnClickListener(new View.OnClickListener() {
            
            @Override
            public void onClick(View view) {
                mQueryDialog.show();           
            }
        });
    }
    
    @SuppressWarnings("rawtypes")
	private class LeftArrayAdapter extends ArrayAdapter {

		@SuppressWarnings("unchecked")
		public LeftArrayAdapter(Context context, int resource, Object[] objects) {
			super(context, resource, objects);
		}
		
		@Override
	    public View getView(int position, View convertView, ViewGroup parent) {
			
			View view = super.getView(position, convertView, parent);

			if (convertView != null && (position == mMenuArray.length - 1)) {
				formatLeftListColor(mTaskStatus);
			}
			if (mLeftListView.getChildAt(position) != null) {
				String str = ((TextView) mLeftListView.getChildAt(position)).getText() + "";
				int fstart = str.indexOf('(');
		        SpannableStringBuilder style = new SpannableStringBuilder(str);
		        if (mNumList.get(position) > 0) {
		        	style.setSpan(new ForegroundColorSpan(Color.BLUE), fstart, str.length(), Spannable.SPAN_EXCLUSIVE_INCLUSIVE); 
		        } else {
		        	style.setSpan(new ForegroundColorSpan(Color.BLACK), fstart, str.length(), Spannable.SPAN_EXCLUSIVE_INCLUSIVE);	
		        }
		        ((TextView) mLeftListView.getChildAt(position)).setText(style);
			}
			
			return view;
		}
    	
    };
    
    /**
     * 初始化左侧菜单列表
     */
    private void initLeftMenuView(View view) {
    	mLeftListView = (ListView) view.findViewById(R.id.menu_list_view);
    	mMenuArray = getResources().getStringArray(R.array.portal_my_task_menu);
    	if (mNumList != null) {
    		for (int i = 0; i < mMenuArray.length; i++) {
    			mMenuArray[i] += " (" + mNumList.get(i) + ")";
    		}
    	}
    	mLeftListView.setAdapter(new LeftArrayAdapter(getActivity(), R.layout.text_view, mMenuArray));
    	mLeftListView.setOnItemClickListener(new OnItemClickListener() {

			@Override
			public void onItemClick(AdapterView<?> parent, View view,
					int position, long id) {
				formatDataList(position);
			}
		});
    }
    
    /**
     * 初始化内容列表
     * @param view
     */
    private void initContentView(View view) {
        ViewGroup viewGroup = (ViewGroup) view.findViewById(R.id.header_layout);
        mHeaderView = LayoutInflater.from(mActivity)
                .inflate(R.layout.portal_more_mytask_listitem, viewGroup, false);
        viewGroup.addView(mHeaderView);
        
        mHeaderNames = getResources().getStringArray(
                R.array.portal_my_task_names);
        TypedArray typedArray = getResources().obtainTypedArray(
                R.array.table_column_ids_5);

        if (mHeaderNames != null) {
            mDisplayItemIds = new int[typedArray.length()];
            for (int i = 0; i < mDisplayItemIds.length; i++) {
                mDisplayItemIds[i] = typedArray.getResourceId(i, 0);
                // 配置列表头项相关字段
                TextView tv = (TextView) view
                        .findViewById(mDisplayItemIds[i]);
                tv.setTextSize(TypedValue.COMPLEX_UNIT_PX, mActivity
                        .getResources().getDimensionPixelSize(R.dimen.table_title_textsize));
                tv.setText(mHeaderNames[i]);
                tv.setTextColor(getActivity().getResources().getColor(R.color.dialog_list_title_text_color));
            }
        }
        typedArray.recycle();

        mListView = (ListView) mRootView.findViewById(R.id.listview);
        mAdapter = new DataListAdapter<Index_task>(mActivity, adapterManager, mShowList);
        mListView.setAdapter(mAdapter);
    }
    
    private void handleTaskEvent() {

    	final TranslateAnimation showAction = new TranslateAnimation(Animation.RELATIVE_TO_SELF, 1.0f,
				Animation.RELATIVE_TO_SELF, 0.0f, Animation.RELATIVE_TO_SELF,
				0.0f, Animation.RELATIVE_TO_SELF, 0.0f);
		showAction.setDuration(500);
		mSecondView.setVisibility(View.VISIBLE);
		mSecondView.setAnimation(showAction);
		int enterprise = ((CepmApplication)getActivity().getApplication()).getEnterpriseType();
    	if (enterprise == 0) {
    		// zuhe
    		mProgressDialog = UtilTools.showProgressDialog(getActivity(), false, false);
    		RemoteIndexService.getInstance().getNodeModule(mNodeManager, mCurrentIndexTask.getZh_group_id());
    	} else {
    		// jihua

    		mProgressDialog = UtilTools.showProgressDialog(getActivity(), false, false);
    		RemoteIndexService.getInstance().getNodeModule(mPlanManager, mCurrentIndexTask.getZh_group_id());
    		
    	}
    	
    }
    
    private void setFeedbackData() {
    	Project project = new Project();
    	project.setProject_id(mCurrentIndexTask.getProject_id());
    	project.setTenant_id(mCurrentIndexTask.getTenant_id());
    	project.setName(ProjectCache.getProjectIdMaps().get(Integer.toString(mCurrentIndexTask.getProject_id())));
    	mBaseViewPager.setChildProject(project);
    	
    	List<FeedbackCell> feedbackList = new ArrayList<>();
		//有反馈信息，加入
		feedbackList.add(mCurrentFeedback);
    	
    	mBaseViewPager.setCurrentList(feedbackList);
    	
    	TaskCell task = null;
    	if (mCurrentIndexTask.getMessage_type_key() == MSG_TASK_TYPE) {
    		task = new Task();
    	} else if (mCurrentIndexTask.getMessage_type_key() == MSG_ZH_TYPE) {
    		task = new ZH_group_task();
    	}
    	
    	if (task == null) {
    		return;
    	}
    	
    	task.setTask_id(mCurrentIndexTask.getTask_id());
    	task.setProject_id(mCurrentIndexTask.getProject_id());
    	task.setTenant_id(mCurrentIndexTask.getTenant_id());
    	
    	// 门户模式下设置parent_id为父责任人
    	task.setParents_id(mCurrentFeedback.getParents_user());
    	task.setStart_time(mCurrentIndexTask.getStart_time());
    	task.setEnd_time(mCurrentIndexTask.getEnd_time());
//    	task.setOwner(mCurrentIndexTask.getUser_id());
    	task.setOwner(UserCache.getCurrentUserId());
    	task.setName(mCurrentIndexTask.getName());
    	
		task.setActual_start_time(mCurrentFeedback.getActual_start_time());
    	task.setActual_end_time(mCurrentFeedback.getActual_end_time());
    	task.setProgress(mCurrentFeedback.getProgress());
    	task.setMark(mCurrentFeedback.getMark());
    	task.setCc_user(mCurrentFeedback.getCc_user());
    	task.setStatus(mCurrentFeedback.getStatus());
    	task.setZh_group_id(mMsgGroupData.getZh_group_id());
    	
    	mBaseViewPager.setCurrentParentBean(task);
    	if (mProgressDialog != null) {
    		mProgressDialog.dismiss();
    	}
    	
    }
    
    private ZH_group mMsgGroupData;
    private DataManagerInterface mPlanManager = new DataManagerInterface() {
		
		@Override
		public void getDataOnResult(ResultStatus status, List<?> list) {
			mProgressDialog.dismiss();
			if (status.getCode() == AnalysisManager.SUCCESS_DB_QUERY) {
				if (list != null && !list.isEmpty()) {
					if (mCurrentIndexTask.getMessage_type_key() == MSG_ZH_TYPE) {
			    	
						mMsgGroupData = (ZH_group) list.get(0);
			    		final List<Fragment> fragmentList = new ArrayList<Fragment>();
			    		SubFeedbackFragment feedbackFragment = new SubFeedbackFragment();
			    		feedbackFragment.setIndexMode(true);
			        	fragmentList.add(feedbackFragment);
			        	fragmentList.add(new CommonDocumentFragment<ZH_group_task>(CommonDocumentFragment.TYPE_SCHEDULE_FEEDBACK_DOC));
			        	fragmentList.add(new WorkLogFragment());
			        	fragmentList.add(new SafetyFragment<ZH_group_task>());
			        	fragmentList.add(new QualityFragment<ZH_group_task>());
			        	
			    		mBaseViewPager.initFragments(getResources().getStringArray(R.array.task_feedback_tab_names), fragmentList, new SimplePageChangeListenerInterface() {
							
							@Override
							public Bundle[] getBundleData() {
								Bundle[] bundleArray = new Bundle[fragmentList.size()];
								Bundle bundle = new Bundle();
								bundle.putString("permission", "2_3, 2_4");
								bundle.putSerializable("groupData", mMsgGroupData);
								for (int i = 0; i < fragmentList.size(); i++) {
									bundleArray[i] = bundle;
								}
								
								return bundleArray;
							}
						} );
			    		mProgressDialog = UtilTools.showProgressDialog(getActivity(), false, false);
			        	RemoteIndexService.getInstance().getFeedBackByTaskIDForZH(mFeedbackManager, mCurrentIndexTask.getTask_id());
			    	}
				}
			}
		}
	};
    
    private DataManagerInterface mNodeManager = new DataManagerInterface() {
		
		@Override
		public void getDataOnResult(ResultStatus status, List<?> list) {
			mProgressDialog.dismiss();
			if (status.getCode() == AnalysisManager.SUCCESS_DB_QUERY) {
				if (list != null && !list.isEmpty()) {
					if (mCurrentIndexTask.getMessage_type_key() == MSG_ZH_TYPE) {
			    		List<String> msgPageData = new ArrayList<String>();
			    		List<Fragment> fragments = new ArrayList<Fragment>();
			    	
			    		mMsgGroupData = (ZH_group) list.get(0);

			    		String[] msgArray = mMsgGroupData.getNode_module().split(",");
			    		for (int i = 0; i< msgArray.length; i++) {
			    			if (msgArray[i] != null && !msgArray[i].isEmpty()) {
			    				msgPageData.add(msgArray[i]);
			    			}
			    		}

			    		for (String name : msgPageData) {
			    			if (name.equals(GLOBAL.COMBINATION_NODE_TYPE[0][0])) {
			    				com.pm360.cepm360.app.module.combination.SubFeedbackFragment feedbackFragment = 
			    						new com.pm360.cepm360.app.module.combination.SubFeedbackFragment();
			    				feedbackFragment.setIndexMode(true);
			    				fragments.add(feedbackFragment);
			    			} else if (name.equals(GLOBAL.COMBINATION_NODE_TYPE[1][0])) {
			    				fragments.add(new com.pm360.cepm360.app.module.combination.WorkLogFragment());
			    			} else if (name.equals(GLOBAL.COMBINATION_NODE_TYPE[2][0])) {
			    				fragments.add(new RiskFragment());
			    			} else if (name.equals(GLOBAL.COMBINATION_NODE_TYPE[3][0])) {
			    				fragments.add(new CommonDocumentFragment<ZH_group_task>(CommonDocumentFragment.TYPE_ZH_FEEDBACK_DOC));
			    			}
			    		}
			    		
			    		String[] stringArray = new String[msgPageData.size()];
			    		for (int i = 0; i < stringArray.length; i++) {
			    			stringArray[i] = GLOBAL.COMBINATION_NODE_TYPE[Integer.parseInt(msgPageData.get(i)) - 1][1];
			    		}
			        	
			    		mBaseViewPager.initFragments(stringArray, fragments);

			    		RemoteIndexService.getInstance().getFeedBackByTaskIDForZH(mFeedbackManager, mCurrentIndexTask.getTask_id());
			    	}
				}
			}
				
		}
	};
    
    private DataManagerInterface mFeedbackManager = new DataManagerInterface() {
		
		@Override
		public void getDataOnResult(ResultStatus status, List<?> list) {
			if (status.getCode() == AnalysisManager.SUCCESS_DB_QUERY) {
				if (list != null && !list.isEmpty()) {
					mCurrentFeedback = (FeedbackCell) list.get(0);
					if (mCurrentFeedback.getParents_user() == 0 && mCurrentIndexTask.getParents_id() != 0) {
						// 父责任人不存在，需要获取父责任人
						if (mCurrentIndexTask.getMessage_type_key() == MSG_TASK_TYPE) {
							RemoteTaskService.getInstance().getTaskDetailByTaskID(mTaskManager, mCurrentIndexTask.getParents_id());
						} else if (mCurrentIndexTask.getMessage_type_key() == MSG_ZH_TYPE) {
							// 父责任人不存在，需要获取父责任人
							com.pm360.cepm360.services.group.RemoteTaskService.getInstance().getTaskDetailByTaskID(mTaskManager, mCurrentIndexTask.getParents_id());
						}
						
					} else {
						setFeedbackData();
					}
				} else {
					if (mCurrentIndexTask.getParents_id() != 0) {
						if (mCurrentIndexTask.getMessage_type_key() == MSG_TASK_TYPE) {
							mCurrentFeedback = new Feedback();
							// 父责任人存在，需要获取父责任人
							RemoteTaskService.getInstance().getTaskDetailByTaskID(mTaskManager, mCurrentIndexTask.getParents_id());
						} else if (mCurrentIndexTask.getMessage_type_key() == MSG_ZH_TYPE) {
							mCurrentFeedback = new ZH_group_feedback();
							// 父责任人存在，需要获取父责任人
							com.pm360.cepm360.services.group.RemoteTaskService.getInstance().getTaskDetailByTaskID(mTaskManager, mCurrentIndexTask.getParents_id());
						}
					} else {
						if (mCurrentIndexTask.getMessage_type_key() == MSG_TASK_TYPE) {
							mCurrentFeedback = new Feedback();
						} else if (mCurrentIndexTask.getMessage_type_key() == MSG_ZH_TYPE) {
							mCurrentFeedback = new ZH_group_feedback();
						}
						setFeedbackData();
					}
				}
			} else {
				if (mProgressDialog != null) {
					mProgressDialog.dismiss();
				}
				Toast.makeText(getActivity(), status.getMessage(), Toast.LENGTH_SHORT).show();
			}
		}
	};
	
	private DataManagerInterface mTaskManager = new DataManagerInterface() {
		
		@Override
		public void getDataOnResult(ResultStatus status, List<?> list) {
			if (status.getCode() == AnalysisManager.SUCCESS_DB_QUERY) {
				if (list != null && !list.isEmpty()) {
					TaskCell task = (TaskCell) list.get(0);
					mCurrentFeedback.setParents_user(task.getOwner());
					setFeedbackData();
				}
			}
		}
	};
    
    private ListAdapterInterface<Index_task> adapterManager = new DataListAdapter.ListAdapterInterface<Index_task>() {

        @Override
        public int getLayoutId() {
            return R.layout.portal_more_mytask_listitem;
        }

        @Override
        public View getHeaderView() {
            return mHeaderView;
        }

        @Override
        public void regesterListeners(ViewHolder viewHolder, final int position) {
            for (int i = 0; i < viewHolder.tvs.length; i++) {
                viewHolder.tvs[i].setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        mAdapter.setSelected(position, true);
                        // 跳转至计划界面
                        //IntentHelper.startActivity(getActivity(), 1, mShowList.get(position));
                        mCurrentIndexTask = mShowList.get(position);
                        handleTaskEvent();
                    }
                });
            }
        }

        @Override
        public void initListViewItem(View convertView, ViewHolder holder,
                DataListAdapter<Index_task> adapter, int position) {
            Map<String, String> listViewItem = beanToMap(adapter
                    .getItem(position), position);
            for (int i = 0; i < mHeaderNames.length; i++) {
                holder.tvs[i].setText(listViewItem.get(mHeaderNames[i]));
            }
        }

        @Override
        public void initLayout(View convertView, ViewHolder holder) {
            holder.tvs = new TextView[mDisplayItemIds.length];
            for (int i = 0; i < mDisplayItemIds.length; i++) {                
                holder.tvs[i] = (TextView) convertView
                        .findViewById(mDisplayItemIds[i]);
            }
        }
        
        private Map<String, String> beanToMap(Index_task data, int position) {
            Map<String, String> mapItem = new HashMap<String, String>();  
            mapItem.put(mHeaderNames[0], String.valueOf(position + 1));
            mapItem.put(mHeaderNames[1], ProjectCache.getProjectIdMaps().get(String.valueOf(data.getProject_id())));
            mapItem.put(mHeaderNames[2], data.getName());
            mapItem.put(mHeaderNames[3], DateUtils.dateToString(DateUtils.FORMAT_SHORT, data.getStart_time()) + " ~ "
                    + DateUtils.dateToString(DateUtils.FORMAT_SHORT, data.getEnd_time()));
            int res = 0;
            if (data.getProgress() > 0 && data.getProgress() < 100) {                
                res = 1;
            } else if (data.getProgress() == 100) {
                res = 2;
            }
            mapItem.put(mHeaderNames[4], GLOBAL.TASK_STATUS_TYPE[res][1]);
            return mapItem;
        }

        @Override
        public List<Index_task> findByCondition(Object... condition) {
            return null;
        }

        @Override
        public boolean isSameObject(Index_task t1, Index_task t2) {
            return false;
        }
        
    };
    
    
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (resultCode != Activity.RESULT_OK || data == null) return;
        if (requestCode == PROJECT_REQUEST_CODE) {
            Project project = (Project) data.getSerializableExtra("project");
            if (project != null) {
                mProjectText.setText(project.getName());
                mProjectId = project.getProject_id();
            }
        }
    }
}

