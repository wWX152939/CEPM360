package com.pm360.cepm360.app.common.activity;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.v4.view.ViewPager;
import android.support.v4.view.ViewPager.OnPageChangeListener;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.pm360.cepm360.R;
import com.pm360.cepm360.app.cache.EpsCache;
import com.pm360.cepm360.app.cache.ProjectCache;
import com.pm360.cepm360.app.common.adpater.EpsAdapter;
import com.pm360.cepm360.app.common.adpater.SimplePagerAdapter;
import com.pm360.cepm360.common.GLOBAL;
import com.pm360.cepm360.entity.EPS;
import com.pm360.cepm360.entity.Project;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

/**
 * 
 * 主要提供2个作用：
 * a. 选择打开某一个项目，设置选择的项目到cache，全局使用
 * b。  选择作用，返回一个project对象，未设置cache，传入 action=100
 *
 */

@SuppressLint("InflateParams")
public class ProjectSelectActivity extends Activity {

    private LinearLayout mButtonLayout;
    private TextView mEmptyText;
    private TextView mTitle;
    private Button mConfirmButton;
    private ImageView mCancelButton;
	private ListView mAllProjectListView;
	private ListView mEPSProjectListView;
	private MyProjectListAdapter mProjectAdapter;
    private EpsAdapter mEPSProjectAdapter;
    private ArrayList<Project> mProjectLists = new ArrayList<Project>();
    private ArrayList<EPS> mEpsLists = new ArrayList<EPS>();

	private Project mCurrentProject = null;
//	private ProgressDialog mProgressDialog;

	private ViewPager mTabPager;
	private ArrayList<View> mPagerViews = new ArrayList<View>();
	
	private int mAction = 0;
	public static final int ACTION_PICK = 100;

	private static final int TOAST_SHOW = 0;
    private static final int UPDATE_PROJECT_PERMISSION = 1;

	private Handler mHandler = new Handler(new Handler.Callback() {
		@Override
		public boolean handleMessage(Message msg) {
			switch (msg.what) {
			case TOAST_SHOW:
				Toast.makeText(ProjectSelectActivity.this,
				        (CharSequence) msg.obj, Toast.LENGTH_SHORT).show();
				break;
            case UPDATE_PROJECT_PERMISSION:
//                dismissProgressDialog();
//                PermissionCache.setProjectPermissions((String[]) msg.obj);
//                Intent intent = new Intent();
//                setResult(Activity.RESULT_OK, intent);
//                finish();
                break;
			}			
			return false;
		}
	});

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.project_select_activity);
		
        Intent intent = getIntent();
        mAction = intent.getIntExtra("action", 0);
        
        mButtonLayout = (LinearLayout) findViewById(R.id.button_layout);
        mButtonLayout.setBackgroundResource(R.color.tree_listview_bg);
		mEmptyText = (TextView) findViewById(R.id.empty_text);
		mTitle = (TextView) findViewById(R.id.edit_title);
		mTitle.setText(R.string.select_opening_project);
        mConfirmButton = (Button) findViewById(R.id.btn_confirm);
        if (mAction == ACTION_PICK) {
            mConfirmButton.setText(R.string.confirm);
            mTitle.setText(R.string.project_list_title);
        } else {
            mConfirmButton.setText(R.string.open);
        }
        
        mCancelButton = (ImageView) findViewById(R.id.btn_close);
		mConfirmButton.setOnClickListener(mClickListener);
        mCancelButton.setOnClickListener(mClickListener);

		if (ProjectCache.getCurrentProject() != null) {
			mCurrentProject = ProjectCache.getCurrentProject();
		}

		initEPSProjectListView();
		initAllProjectListView();

		mTabPager = (ViewPager) findViewById(R.id.tabpager);
        mTabPager.setAdapter(new SimplePagerAdapter(mPagerViews));
		mTabPager.setOnPageChangeListener(new OnPageChangeListener() {
			@Override
			public void onPageScrollStateChanged(int state) {
			}

			@Override
			public void onPageScrolled(int position, float positionOffset,
			        int positionOffsetPixels) {
			}

			@Override
			public void onPageSelected(int position) {
			    updateResource(position);
			}
		});

        // 获取项目列表、EPS列表		
		if (ProjectCache.isDataLoaded()) {
		    mProjectLists.clear();
		    for(Project project : ProjectCache.getProjectLists()) {
		        mProjectLists.add(project);
		    }
		    
            mEmptyText.setVisibility(mProjectLists.size() == 0 ? View.VISIBLE
                    : View.GONE);
            mProjectAdapter.notifyDataSetChanged();
            if (mCurrentProject != null) {
                mProjectAdapter.setSelected(mCurrentProject);
            }
		} else {
		    // loadProjectList();
		}
		
        if (EpsCache.isDataLoaded()) {           
            mEpsLists.clear();
            for(EPS eps : EpsCache.getEpsLists()) {
                mEpsLists.add(eps);
            }
            mEPSProjectAdapter.updateTreeList(mEpsLists);
            mEPSProjectAdapter.notifyDataSetChanged();
        }
	}
	
	/**
	 * 设置ViewPager切换时，界面的清除和按钮的背景
	 * @param position
	 */
    public void updateResource(int position) {
        mEPSProjectAdapter.clearSelectedProject();
        mProjectAdapter.clearSelectedProject();
        if (position == 1 && mCurrentProject != null) {           
            mProjectAdapter.setCurrentProject(mCurrentProject);            
        }
        
        if (position == 1) {
            mButtonLayout.setBackgroundColor(Color.WHITE);
        } else {
            mButtonLayout.setBackgroundResource(R.color.tree_listview_bg);
        }        
    }

	View.OnClickListener mClickListener = new View.OnClickListener() {
		public void onClick(View view) {
			switch (view.getId()) {
			case R.id.btn_confirm:
			    if (mAction == ACTION_PICK) {
			        setPickResult();
			    } else {
			        setProjectAndPermissions();
			    }
				break;
            case R.id.btn_close:
                finish();
				break;
			}
		}
	};

    /**
     *  初始化 所有项目（不分类） 列表界面
     */
	private void initAllProjectListView() {
		View view = LayoutInflater.from(this).inflate(
		        R.layout.project_select_list_view, null);

		mAllProjectListView = (ListView) view.findViewById(R.id.listView1);
		mProjectAdapter = new MyProjectListAdapter(this, mProjectLists);
		mAllProjectListView.setAdapter(mProjectAdapter);
		mPagerViews.add(view);
	}

	/**
	 * 初始化带有EPS的列表界面
	 */
	private void initEPSProjectListView() {
		View view = LayoutInflater.from(this).inflate(
		        R.layout.project_select_list_view, null);
        mEPSProjectListView = (ListView) view.findViewById(R.id.listView1);
        mEPSProjectListView.setBackgroundResource(R.color.tree_listview_bg);
        mEPSProjectAdapter = new EpsAdapter(this, mEpsLists, mProjectLists);
        mEPSProjectListView.setAdapter(mEPSProjectAdapter);
        
        mPagerViews.add(view);
	}
	
	/**
	 * 获取页面上选择的内容
	 * @return
	 */
	private Project getSelectedProject() {
	    Project project = null;
        if (mProjectAdapter.getSelectedProjects().size() > 0) {
            project = mProjectAdapter.getSelectedProjects().get(0);
        } else if (mEPSProjectAdapter.getSelectedProject().size() > 0) {
            project = mEPSProjectAdapter.getSelectedProject().get(0);
        }
	    return project;
	}
	
	/**
	 * 设置Pick的结果，返回结果，并关闭对话框
	 */
	private void setPickResult() {
        if (getSelectedProject() == null) {
            Message msg = Message.obtain();
            msg.what = TOAST_SHOW;
            msg.obj = getResources().getString(R.string.select_first_project_toast);
            mHandler.sendMessage(msg);
            return;
        }
        Intent intent = new Intent();
        intent.putExtra("project", getSelectedProject());
        setResult(Activity.RESULT_OK, intent);
        finish();
	}

	/**
	 * 设置选中的project到recent,cache,
	 * 并重新拉project-permission和设置到ProjectCache
	 */
	private void setProjectAndPermissions() {
        Project project = getSelectedProject();
        if (project == null) {
            Message msg = Message.obtain();
            msg.what = TOAST_SHOW;
            msg.obj = getResources().getString(R.string.select_first_project_toast);
            mHandler.sendMessage(msg);
            return;
        } else {
            if (project == mCurrentProject) {
                Intent intent = new Intent();
                setResult(Activity.RESULT_OK, intent);
                finish();
            }
            
            ArrayList<String> recents = new ArrayList<String>();
            SharedPreferences sharedPreferences = getSharedPreferences(
                    GLOBAL.SHARED_PREFERENCES_KEY, Context.MODE_PRIVATE);
            String recentArray = sharedPreferences.getString("recent", "");            
            if (!recentArray.equals(""))
                recents.addAll(Arrays.asList(recentArray.split(",")));

            int project_id = project.getProject_id();
            if (recents.contains(project_id + "")) {
                recents.remove(project_id + "");
            }
            recents.add(0, project_id + "");

            SharedPreferences.Editor editor = sharedPreferences.edit();
            StringBuilder recentsBuilder = new StringBuilder();
            for (int i = 0; i < recents.size() && i < 10; i++) {
                recentsBuilder.append(recents.get(i) + ",");
            }

            String s = recentsBuilder.substring(0, recentsBuilder.length() - 1);
            editor.putString("recent", s);
            editor.commit();
                        
            ProjectCache.setCurrentProject(project);
        }
        
        Intent intent = new Intent();
        setResult(Activity.RESULT_OK, intent);
        finish();
        
//        Project currentProject = ProjectCache.getCurrentProject();
//        showProgressDialog("Getting Project Permission...");
//        RemoteCommonService.getInstance().getPermissionByProject(
//                new DataManagerInterface() {
//
//                    @Override
//                    public void getDataOnResult(ResultStatus status, List<?> list) {                        
//                        Message msg = Message.obtain();
//                        msg.what = UPDATE_PROJECT_PERMISSION;
//                        if (status.getCode() == AnalysisManager.SUCCESS_DB_QUERY
//                                && list != null && list.size() > 0) {                            
//                            StringBuilder builder = new StringBuilder();
//                            for (Object object : list) {
//                                if (object instanceof Role) {
//                                    Role role = (Role) object;
//                                    String action = role.getAction();
//                                    if (action != null) {
//                                        builder.append(action + ",");
//                                    }
//                                }
//                            }
//                            String role_text = builder.toString();
//                            if (role_text.endsWith(",")) {
//                                role_text = role_text.substring(0, role_text.length() - 1);
//                            }
//                            msg.obj = role_text.split(",");
//                        }
//                        mHandler.sendMessage(msg);
//                    }
//                }, currentProject.getProject_id(), 
//                UserCache.getCurrentUser());
        
	}

//    private void showProgressDialog(String text) {
//        dismissProgressDialog();
//        mProgressDialog = UtilTools.showProgressDialog(ProjectSelectActivity.this, true, false);
//    }
//
//    private void dismissProgressDialog() {
//        if (mProgressDialog != null && mProgressDialog.isShowing())
//            mProgressDialog.dismiss();
//    }

    // ------------------------- ListAdapter 不分类 --------------------------- //
	class MyProjectListAdapter extends BaseAdapter {
		private Context mContext;
		private Project mSelectedItem;
		private List<Project> mDataList;
		private List<Project> mSelecedProjects = new ArrayList<Project>();
		private int mSelectedPosition = -1;

		public MyProjectListAdapter(Context context, List<Project> data) {
			mContext = context;
			mDataList = data;
		}
		
		

		@Override
		public View getView(int position, View convertView, ViewGroup parent) {
			TextView textView;
			if (convertView != null) {
				textView = (TextView) convertView
				        .findViewById(R.id.project_name);
			} else {
				convertView = LayoutInflater.from(mContext).inflate(
				        R.layout.project_list_simple_item, parent, false);
				textView = (TextView) convertView
				        .findViewById(R.id.project_name);

			}

			regesterListeners(textView, position);
			textView.setText(getItem(position).getName());
			if (mSelectedPosition == position) {
			    convertView.setBackgroundResource(R.color.listview_selected_bg); 
			} else {
	            if (position % 2 == 1) {
	                convertView.setBackgroundResource(R.color.content_listview_single_line_bg);
	            } else {
	                convertView.setBackgroundColor(Color.WHITE);
	            }
			}

			return convertView;
		}

		@Override
		public int getCount() {
			return mDataList.size();
		}

		@Override
		public Project getItem(int position) {
			return mDataList.get(position);
		}

		@Override
		public long getItemId(int position) {
			return position;
		}

		public void regesterListeners(TextView textView, final int position) {
			textView.setOnClickListener(new View.OnClickListener() {
				@Override
				public void onClick(View view) {
					setSelected(position);
					notifyDataSetChanged();
					mSelecedProjects.clear();
					mSelecedProjects.add(getItem(position));
				}
			});
		}
		
		public void setCurrentProject(Project currentProject) {
            mSelectedItem = currentProject;
            mSelecedProjects.clear();
            mSelecedProjects.add(currentProject);
		}

        public List<Project> getSelectedProjects() {
			return mSelecedProjects;
		}

        public void clearSelectedProject() {
            mSelecedProjects.clear();
            setSelected(-1);
        }

		public Project getSelecedItem() {
			return mSelectedItem;
		}

		public void setSelected(int position) {
			mSelectedPosition = position;
			notifyDataSetChanged();
		}

		public void setSelected(Project project) {
			for (int i = 0; i < mDataList.size(); i++) {
				if (project.getProject_id() == mDataList.get(i).getProject_id()) {
					mSelectedPosition = i;
					break;
				}
			}
			notifyDataSetChanged();
		}
	}
}
