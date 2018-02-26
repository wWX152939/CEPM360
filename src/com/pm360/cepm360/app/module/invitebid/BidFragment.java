package com.pm360.cepm360.app.module.invitebid;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.text.TextUtils.TruncateAt;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.FrameLayout.LayoutParams;
import android.widget.Toast;

import com.pm360.cepm360.R;
import com.pm360.cepm360.app.cache.ObsCache;
import com.pm360.cepm360.app.cache.ProjectCache;
import com.pm360.cepm360.app.cache.UserCache;
import com.pm360.cepm360.app.common.activity.ActionBarFragmentActivity;
import com.pm360.cepm360.app.common.activity.DataManagerInterface;
import com.pm360.cepm360.app.common.activity.OwnerSelectActivity;
import com.pm360.cepm360.app.common.adpater.DataTreeListAdapter;
import com.pm360.cepm360.app.common.adpater.DataTreeListAdapter.ViewHolder;
import com.pm360.cepm360.app.common.custinterface.CommonListInterface;
import com.pm360.cepm360.app.common.custinterface.DialogAdapterInterface;
import com.pm360.cepm360.app.common.custinterface.ServiceInterface;
import com.pm360.cepm360.app.common.custinterface.TaskRelevanceChildInterface;
import com.pm360.cepm360.app.common.custinterface.ViewPagerInterface;
import com.pm360.cepm360.app.common.view.TextProgressBar;
import com.pm360.cepm360.app.common.view.parent.BaseDialog;
import com.pm360.cepm360.app.common.view.parent.BaseTreeListWithFrameLayoutFragment;
import com.pm360.cepm360.app.common.view.parent.DoubleDatePickerDialog;
import com.pm360.cepm360.common.GLOBAL;
import com.pm360.cepm360.common.ResultStatus;
import com.pm360.cepm360.common.json.AnalysisManager;
import com.pm360.cepm360.common.util.DateUtils;
import com.pm360.cepm360.common.util.LogUtil;
import com.pm360.cepm360.entity.Project;
import com.pm360.cepm360.entity.User;
import com.pm360.cepm360.entity.ZB_flow;
import com.pm360.cepm360.services.invitebid.RemoteZBFlowService;

public class BidFragment extends BaseTreeListWithFrameLayoutFragment<ZB_flow, ActionBarFragmentActivity> {

	private final String NAME = "flow_type";
	private final String STATUS = "status";
	private final String PLAN_DURATION = "period";
	private final String START_TIME = "start_date";
	private final String END_TIME = "end_date";
	private final String PROGRESS = "progress";
	private final String OWNER = "owner";
	private final String DEPARTMENT = "dep";
	private final String ZBNAME = "name";

	private RemoteZBFlowService mService;

	// startActivityForResult flag
	public static final int OWNER_SELECT_REQUEST = 102;

	// 当前行
	protected int mLine;

	@SuppressWarnings("rawtypes")
	protected List<TaskRelevanceChildInterface> mFragments;
	protected String[] mStringArray;

	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {
		initMsgData();
		initMessageCenterData();
		initEnvironment();
		init(ZB_flow.class, true, listInterface, serviceInterface, null, null,
				dialogInterface, viewPagerInterface);
		View view = super.onCreateView(inflater, container, savedInstanceState);
		
		mBaseViewPager.setChildProject(mProject);

		return view;
	}

	private void initMessageCenterData() {

	}

	private void initEnvironment() {
		mService = RemoteZBFlowService.getInstance();
		setPermissionIdentity(GLOBAL.SYS_ACTION[51][0],
				GLOBAL.SYS_ACTION[50][0]);

	}

	CommonListInterface<ZB_flow> listInterface = new CommonListInterface<ZB_flow>() {

		@Override
		public Map<String, Map<String, String>> getDisplayFieldsSwitchMap() {
			Map<String, Map<String, String>> map = new HashMap<String, Map<String, String>>();
			Map<String, String> nameMap = new HashMap<String, String>();
			for (int i = 0; i < GLOBAL.BID_FLOW_TYPE.length; i++) {
				nameMap.put(GLOBAL.BID_FLOW_TYPE[i][0],
						GLOBAL.BID_FLOW_TYPE[i][1]);
			}
			map.put(NAME, nameMap);
			map.put(DEPARTMENT, ObsCache.getObsIdMaps());
			map.put(OWNER, UserCache.getUserMaps());
			Map<String, String> statusMap = new HashMap<String, String>();
			statusMap.put(GLOBAL.TASK_STATUS_TYPE[0][0], GLOBAL.TASK_STATUS_TYPE[0][1]);
			statusMap.put(GLOBAL.TASK_STATUS_TYPE[1][0], GLOBAL.TASK_STATUS_TYPE[1][1]);
			statusMap.put(GLOBAL.TASK_STATUS_TYPE[2][0], GLOBAL.TASK_STATUS_TYPE[2][1]);
			map.put(STATUS, statusMap);
			return map;
		}

		@Override
		public int getListItemId(ZB_flow t) {
			return t.getId();
		}

		@Override
		public String[] getDisplayFeilds() {
			return new String[] { NAME, STATUS, PROGRESS, START_TIME, END_TIME,
					PLAN_DURATION, OWNER, DEPARTMENT };
		}

		@Override
		public int getListHeaderLayoutId() {
			return R.layout.invitebid_plan_title_list_item;
		}

		@Override
		public int getListLayoutId() {
			return R.layout.invitebid_plan_list_item;
		}

		@Override
		public int getListHeaderNames() {
			return R.array.invitebid_add_flow;
		}

		@Override
		public int getListHeaderIds() {
			return R.array.invitebid_flow_ids;
		}

		@Override
		public int getListItemIds() {
			return R.array.invitebid_flow_list_ids;
		}
	};

	protected ServiceInterface<ZB_flow> serviceInterface = new ServiceInterface<ZB_flow>() {

		@Override
		public void getListData() {
			mService.getZBFlow(getServiceManager(), mProject.getTenant_id(),
					mProject.getProject_id());
		}

		@Override
		public void addItem(ZB_flow t) {
			t.setTenent_id(mProject.getTenant_id());
			t.setProject_id(mProject.getProject_id());
			t.setFlow_type(Integer.parseInt(GLOBAL.BID_FLOW_TYPE[0][0]));
			
			t.setPeriod(DateUtils.calculateDuration(t.getStart_date(), t.getEnd_date()));

			mService.addZBFlow(mMakeManager, t);

		}

		@Override
		public void deleteItem(ZB_flow t) {
			// mService.
		}

		@Override
		public void updateItem(ZB_flow t) {
			// TODO check date
			// int duration = (int) ((t.getEnd_date().getTime() -
			// t.getStart_date().getTime()) / (24 * 3600 * 1000));
			//
			// t.setPeriod(duration);

			mService.updateZBFlow(getServiceManager(), t);
		}

	};

	DialogAdapterInterface dialogInterface = new DialogAdapterInterface() {

		@Override
		public int getDialogTitleId() {
			return R.string.invitebid_add_dialog;
		}

		@Override
		public int getDialogLableNames() {
			return R.array.invitebid_add_flow_dialog;
		}

		@Override
		public String[] getUpdateFeilds() {
			return new String[] { ZBNAME, START_TIME, END_TIME, OWNER,
					DEPARTMENT };
		}

		@Override
		public Integer[] getImportantColumns(Map<String, String> saveDataMap) {
			return new Integer[]{0, 1, 3};
		}

		@SuppressLint("UseSparseArrays")
		@Override
		public Map<Integer, Integer> getDialogStyles() {

			Map<Integer, Integer> buttons = new HashMap<Integer, Integer>();
			buttons.put(1, BaseDialog.calendarLineStyle);
			buttons.put(2, BaseDialog.calendarLineStyle);
			buttons.put(3, BaseDialog.userSelectLineStyle);
			buttons.put(4, BaseDialog.OBSReadOnlyLineStyle);

			return buttons;
		}

		@SuppressLint("UseSparseArrays")
		@Override
		public Map<Integer, String[]> getSupplyData() {
			return null;
		}

		@Override
		public void additionalInit(BaseDialog dialog) {

			final EditText startEt = (EditText) mDialog.getEditTextView(1);
			final EditText endEt = (EditText) mDialog.getEditTextView(2);
			final DoubleDatePickerDialog doubleDatePickerDialog = new DoubleDatePickerDialog(
					getActivity(), null, startEt, endEt, null);
			OnClickListener dateListener = new OnClickListener() {

				@Override
				public void onClick(View v) {
					doubleDatePickerDialog.show(null, null);
					
//					else {
//						if (mCurrentItem.getParents_id() != 0
//								&& mCurrentItem.getStart_date() == null) {
//							ZB_flow taskParent = null;
//							for (int i = mLine - 1; i >= 0; i--) {
//
//								if (mListAdapter.getShowList().get(i)
//										.getLevel() < mCurrentItem.getLevel()) {
//									taskParent = mListAdapter.getShowList()
//											.get(i);
//									break;
//								}
//							}
//							doubleDatePickerDialog.show(
//									taskParent.getStart_date(),
//									taskParent.getEnd_date());
//						} else {
//							doubleDatePickerDialog.show(
//									mCurrentItem.getStart_date(),
//									mCurrentItem.getEnd_date());
//						}
//					}
				}
			};

			OnClickListener ownerListener = new OnClickListener() {

				@Override
				public void onClick(View v) {
					Intent intent = new Intent();
					intent.setClass(v.getContext(), OwnerSelectActivity.class);
					intent.putExtra("title", getString(R.string.owner));
					Project project = new Project();
					{
						project.setProject_id(mProject.getProject_id());
						project.setTenant_id(mProject.getTenant_id());
					}

					intent.putExtra("project", project);
					startActivityForResult(intent, OWNER_SELECT_REQUEST);
				}
			};

			mDialog.setEditTextStyle(1, 0, dateListener, null);
			mDialog.setEditTextStyle(2, 0, dateListener, null);
			mDialog.setEditTextStyle(3, 0, ownerListener, null);

		}

		@Override
		public Map<String, Map<String, String>> getUpdateFieldsSwitchMap() {

			return null;
		}

	};

	@SuppressWarnings("rawtypes")
	ViewPagerInterface viewPagerInterface = new ViewPagerInterface() {

		@Override
		public String[] getViewPagerTitle() {
			return mStringArray;
		}

		@Override
		public List<TaskRelevanceChildInterface> getFragment() {
			return mFragments;
		}

	};

	@Override
	public void onActivityResult(int requestCode, int resultCode, Intent data) {
		super.onActivityResult(requestCode, resultCode, data);
		if (resultCode == 0)
			return;
		if (requestCode == OWNER_SELECT_REQUEST) {
			User user = (User) data.getSerializableExtra("user");
			mDialog.setUserTextContent(3, user.getUser_id());
			mDialog.setOBSTextContent(4, user.getObs_id());
		}
	}
	
	public void displayFieldRemap(Map<String, String> displayFieldMap,
			ZB_flow t, int position) {
		super.displayFieldRemap(displayFieldMap, t, position);
	}
	
	@Override
	protected void setListItemTextContent(ViewHolder viewHolder, Map<String, String> treeNodeMap, int i, int position) {
		mLine = position;
		if (i == 0 && mListAdapter.getItem(position).getFlow_type() == 1) {
			String name = "";
			if (mListAdapter.getItem(position).getName() != null) {
				name = mListAdapter.getItem(position).getName();
			}
			viewHolder.tvs[i].setText("【" + name + "】" + getString(R.string.invitation_for_bids));
			viewHolder.tvs[i].setEllipsize(TruncateAt.MARQUEE);
			if (!mIsBlackBackGroud) {
				viewHolder.tvs[i].setTextColor(Color.BLACK);
			}
		} else if (i == 2) {
			viewHolder.txpbs[0].setProgress(mListAdapter.getItem(position).getProgress());
		} else if (i > 2) {
			if ((treeNodeMap.get(mDisplayFields[i]) != null) && !(treeNodeMap.get(mDisplayFields[i])).equals("0")) {
				if (i == 5) {
					viewHolder.tvs[i - 1].setText(treeNodeMap.get(mDisplayFields[i]) + getString(R.string.plan_day));
					
				} else {
					viewHolder.tvs[i - 1].setText(treeNodeMap.get(mDisplayFields[i]));

				}
				viewHolder.tvs[i - 1].setEllipsize(TruncateAt.MARQUEE);
				if (!mIsBlackBackGroud) {
					viewHolder.tvs[i - 1].setTextColor(Color.BLACK);
				}
			} else {
				viewHolder.tvs[i - 1].setText("");
			}
		} else {
			super.setListItemTextContent(viewHolder, treeNodeMap, i, position);
		}
	}

	protected void doExtraInitLayout(View convertView, ViewHolder viewHolder) {
		viewHolder.txpbs = new TextProgressBar[1];
		viewHolder.txpbs[0] = (TextProgressBar) convertView.findViewById(R.id.textProgressBar);
	}

	protected void doExtraRegListener(ViewHolder viewHolder, int position) {

	}

	protected void setChildCurrentBeanList(
			DataTreeListAdapter<ZB_flow> listAdapter) {
		mBaseViewPager.setParentList(listAdapter);
	}

	private DataManagerInterface mMakeManager = new DataManagerInterface() {

		@SuppressLint("UseSparseArrays")
		@Override
		public void getDataOnResult(ResultStatus status, List<?> list) {
			LogUtil.i("wzw status:" + status.getCode());
			if (status.getCode() != AnalysisManager.SUCCESS_DB_QUERY) {
				Toast.makeText(mActivity, status.getMessage(),
						Toast.LENGTH_SHORT).show();
			}
			if (status.getCode() == AnalysisManager.SUCCESS_DB_DEL) {

				mListAdapter.notifyDataSetChanged();
			} else if (status.getCode() == AnalysisManager.SUCCESS_DB_ADD) {
				serviceInterface.getListData();
			}
		}
	};

	@Override
	protected void loadData() {
		serviceInterface.getListData();
	}

	@SuppressWarnings("unchecked")
	@Override
	protected void doExtraGetServerData(ResultStatus status, List<?> list) {
		if (status.getCode() == AnalysisManager.SUCCESS_DB_QUERY) {
			if (list == null || list.isEmpty()) {

			} else {
				// TODO 处理消息
				for (int i = 0; i < mListAdapter.getShowList().size(); i++) {
					InviteBidDataCache.getInstance().getPlanIdCache().put(
							mListAdapter.getShowList().get(i).getZb_plan_id(), i);
				}
				
				// 提供list给子Fragment处理
				setChildCurrentBeanList(mListAdapter);
			}
		} else if (status.getCode() == AnalysisManager.SUCCESS_DB_UPDATE) {

			// 修改成功后，更新viewPager常用界面
			handleChildEvent();
		} else if (status.getCode() == AnalysisManager.SUCCESS_DB_ADD) {
			InviteBidDataCache.getInstance().getPlanIdCache().put(((List<ZB_flow>)list).get(0).getZb_plan_id(), 
					InviteBidDataCache.getInstance().getPlanIdCache().size());
		}
	}

	@Override
	public void handleClickWithShowOptionMenu(int position, View view) {
		mCurrentItem = mListAdapter.getItem(position);
		mListAdapter.setSelected(position, true);

		// 点击更新viewPager常用界面
		handleChildEvent();
	}

	@Override
	protected void deleteCurrentAndChildItem() {
		serviceInterface.deleteItem(mCurrentItem);
	}

	private SubCommonFragment mSubCommonFragment = new SubCommonFragment();
	private SecondTableFragment mSecondTableFragment = new SecondTableFragment();
	private CommonFragment mCommonFragment = new CommonFragment();

	@SuppressWarnings("rawtypes")
	private void initMsgData() {
		mProject = ProjectCache.getCurrentProject();
		mStringArray = getResources().getStringArray(R.array.bid_viewpager);
		mFragments = new ArrayList<TaskRelevanceChildInterface>();
		mFragments.add(mSubCommonFragment);
		mFragments.add(mSecondTableFragment);
		mFragments.add(mCommonFragment);
	}

	@Override
	public void handleClickWithTextView(int position, View view) {
		super.handleClickWithTextView(position, view);
		String FLOW_TYPE[][] = { { "1",   getResources().getString(R.string.invitebid_invite_plan) }, {"2", getResources().getString(R.string.work_log)}};
		int type = mListAdapter.getItem(position).getFlow_type();

		if (type == 1) {
			setTextViewContent(2,
					getString(R.string.expert_lib));
		} else {
			setTextViewContent(2,
					GLOBAL.BID_FLOW_TYPE[mListAdapter.getItem(position)
							.getFlow_type() - 1][1]);
		}
		
		if (type == 1) {
			setTextViewContent(1, FLOW_TYPE[0][1]);
		} else {
			setTextViewContent(1, FLOW_TYPE[1][1]);
		}

	}

	@Override
	protected void setSlidingPane() {

	}

	@Override
	protected boolean enableProjectMenu() {
		return true;
	}
	
	@Override
	protected boolean needSelectProject() {
		return true;
	}
	
	@Override
	protected boolean hasListItemData() {
		if (mListAdapter.getShowList() != null && !mListAdapter.getShowList().isEmpty()) {
			return true;
		} else {
			Toast.makeText(mActivity, R.string.pls_add_task, Toast.LENGTH_SHORT).show();
			return false;
		}
	}
	
	@Override
	protected void doExtraEventWithEditPermission() {
		LayoutParams params = (LayoutParams) mFloatingMenu.getLayoutParams();
		params.rightMargin = getActivity().getResources().getDimensionPixelOffset(R.dimen.dp16_w);
		mFloatingMenu.setLayoutParams(params);
	}
}
