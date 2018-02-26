package com.pm360.cepm360.app.module.cooperation;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Intent;
import android.content.res.TypedArray;
import android.graphics.Color;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.v4.app.Fragment;
import android.util.TypedValue;
import android.view.View;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.CompoundButton.OnCheckedChangeListener;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.pm360.cepm360.R;
import com.pm360.cepm360.app.cache.UserCache;
import com.pm360.cepm360.app.common.activity.DataManagerInterface;
import com.pm360.cepm360.app.common.activity.ListSelectActivity;
import com.pm360.cepm360.app.common.adpater.DataListAdapter;
import com.pm360.cepm360.app.common.adpater.DataListAdapter.ViewHolder;
import com.pm360.cepm360.app.common.view.parent.BaseDialogStyle;
import com.pm360.cepm360.app.module.settings.UserSelectFragment;
import com.pm360.cepm360.common.ResultStatus;
import com.pm360.cepm360.common.json.AnalysisManager;
import com.pm360.cepm360.entity.Cooperation;
import com.pm360.cepm360.entity.User;
import com.pm360.cepm360.services.cooperation.RemoteCooperationService;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Map;

@SuppressWarnings("rawtypes")
public class AddReceivePeopleDialog extends BaseDialogStyle {

	private Fragment mFragment;
	private View mDialogView;
	private ListView mDialogListView;
	private View mDialogListLayout;
	private String[] mDialogListHeadNames;
	private int[] mDialogDisplayItemIds;
	private DataListAdapter<User> mDialogAdapter;
	private Cooperation mFatherCurrentItem;
	private Button mImportButton;
	String[] mUserIdArr;
	private List<Integer> mCheckBoxCheckedBuffer = new ArrayList<Integer>(); 
	private List<User> mShowUserList;
	ResfrshInterface mResfrshInterface;
	private int mFunctionFlag = ACCEPT_CONTACT_MODE;
	public static int ACCEPT_CONTACT_MODE = 0;
	public static int LUNCH_CONTACT_MODE = 1;
	public static int ADDDIALOG = 400;
	
	@SuppressWarnings("unused")
	private int mHandlerFlag = DETAIL_MODE;
	public static int MODIFY_MODE = 0;
	public static int DETAIL_MODE = 1;			
	
	View.OnClickListener mListener = new View.OnClickListener() {
		
		@Override
		public void onClick(View arg0) {
			switch(arg0.getId()) {
			case R.id.import_button:
				Intent intent = new Intent(mActivity, ListSelectActivity.class);
				Bundle bundle = new Bundle();
				bundle.putSerializable(ListSelectActivity.FRAGMENT_KEY, UserSelectFragment.class);
				bundle.putBoolean(ListSelectActivity.SELECT_MODE_KEY, true);
				intent.putExtra(ListSelectActivity.SELECT_MODE_KEY, ListSelectActivity.MULTI_SELECT);
				bundle.putSerializable(ListSelectActivity.FILTER_DATA_KEY, (Serializable) mShowUserList);
				intent.putExtras(bundle);
                
                mFragment.startActivityForResult(intent, ADDDIALOG);
				break;
			case R.id.first_Button:
				//delete
				Collections.sort(mCheckBoxCheckedBuffer);
				
				for (int i = mCheckBoxCheckedBuffer.size(); i > 0; i--) {
					mShowUserList.remove((mCheckBoxCheckedBuffer.get(i-1)).intValue());
					View view = mDialogListView.getChildAt((mCheckBoxCheckedBuffer.get(i-1)).intValue());
					CheckBox cb = (CheckBox)view.findViewById(R.id.cooperation_receive_people_checkbox);
					cb.setChecked(false);					
				}
				mDialogAdapter.notifyDataSetChanged();
				mCheckBoxCheckedBuffer.clear();
				break;
			case R.id.save_Button:
				StringBuffer userIdStr = new StringBuffer();
				for (int i = 0; i < mShowUserList.size(); i++){
					userIdStr.append(mShowUserList.get(i).getUser_id()+"");
					if (i != mShowUserList.size()-1){
						userIdStr.append(",");
					}					
				}
				if (mFunctionFlag == ACCEPT_CONTACT_MODE) {
					mFatherCurrentItem.setAccept_contact_window(userIdStr.toString());
				} else if (mFunctionFlag == LUNCH_CONTACT_MODE) {
					mFatherCurrentItem.setLunch_contact_window(userIdStr.toString());
				}
				
				RemoteCooperationService.getInstance().updateCooperation(mAcceptPeopleManage, mFatherCurrentItem, 
						mFunctionFlag == ACCEPT_CONTACT_MODE ? 2+"" : 1+"");
				dismiss();
				mResfrshInterface.freshFunction();
				break;
				
			case R.id.btn_close:
				dismiss();
				break;
			default:break;
			}
		}
	};	
	
	ListView getDialogListView(){
		return  mDialogListView;
	}
	
	List<User> getShowList(){
		return  mShowUserList;
	}	

	public AddReceivePeopleDialog(Activity activity, Fragment fragment, ResfrshInterface freshFunction) {
		super(activity);
		mFragment = fragment;
		mResfrshInterface = freshFunction;
		initAddReceivePeopleDialog();
		mImportButton.setOnClickListener(mListener);
		getButton(0).setOnClickListener(mListener);
		getButton(1).setOnClickListener(mListener);
		
	}
	
	public interface ResfrshInterface {
		public void freshFunction();
	}

	private void initAddReceivePeopleDialog() {
		mDialogView = init(R.layout.cooperation_receive_people_dialog);
		initDialogListView();		
	}
	
	protected View initLayout() {
		View view = mActivity.getLayoutInflater().inflate(
				R.layout.cooperation_base_dialog_listview, null);
		mImportButton = (Button) view.findViewById(R.id.import_button);
		return view;
	}
	
	public void show(Cooperation currentItem, int functionFlag) {
		mFatherCurrentItem = currentItem;
		mFunctionFlag = functionFlag;
		mShowUserList = mDialogAdapter.getDataShowList();
		CheckBox allSelectCb = (CheckBox)mDialogListLayout.findViewById(R.id.cooperation_receive_people_checkbox);
		allSelectCb.setChecked(false);
		mShowUserList.clear();
		mCheckBoxCheckedBuffer.clear();
		if (mFunctionFlag == ACCEPT_CONTACT_MODE) {
			if (mFatherCurrentItem.getAccept_contact_window() != null ){
				mUserIdArr= mFatherCurrentItem.getAccept_contact_window().split(",");
		
				Map userMaps = UserCache.getUserMaps();
				
				for (int i = 0; i < mUserIdArr.length; i++) {
					if (userMaps.get(mUserIdArr[i]) != null) {
						mShowUserList.add(UserCache.findUserById(Integer.parseInt(mUserIdArr[i])));
					}
				}			
			}
		} else if (mFunctionFlag == LUNCH_CONTACT_MODE){
			if (mFatherCurrentItem.getLaunch_contact_window() != null ) {
				mUserIdArr= mFatherCurrentItem.getLaunch_contact_window().split(",");
		
				Map userMaps = UserCache.getUserMaps();
				
				for (int i = 0; i < mUserIdArr.length; i++) {
					if (userMaps.get(mUserIdArr[i]) != null) {
						mShowUserList.add(UserCache.findUserById(Integer.parseInt(mUserIdArr[i])));
					}
				}			
			}			
		}
		
		super.show();
	}
	
	@SuppressLint("HandlerLeak")
	private Handler mToastHandler = new Handler() {
		public void handleMessage(Message msg) {
			Toast.makeText(mActivity,
					(CharSequence) msg.obj, Toast.LENGTH_SHORT).show();
		}
	};	

	@SuppressWarnings("unchecked")
	private void initDialogListView() {
		
		mDialogView.findViewById(R.id.btn_close).setOnClickListener(mListener);
		mDialogListLayout = mDialogView.findViewById(R.id.cooperation_receive_people_listhead);
		// 获取列表使用的相关资源
		mDialogListHeadNames = mActivity.getResources().getStringArray(
				R.array.cooperation_receive_people_list_name);
		TypedArray typedArray = mActivity.getResources().obtainTypedArray(
				R.array.cooperation_receive_people_list_ids);
		if (mDialogListHeadNames != null) {
			mDialogDisplayItemIds = new int[typedArray.length()];
			for (int i = 0; i < mDialogDisplayItemIds.length; i++) {
				mDialogDisplayItemIds[i] = typedArray.getResourceId(i, 0);
				if (i < mDialogListHeadNames.length) {
					// 配置列表头项相关字段
					TextView tv = (TextView) mDialogListLayout
							.findViewById(mDialogDisplayItemIds[i]);
					tv.setTextSize(TypedValue.COMPLEX_UNIT_PX, mActivity.getResources().getDimensionPixelSize(R.dimen.table_title_textsize));
					tv.setTextColor(mActivity.getResources().getColor(R.color.content_listview_header_text_color));
					tv.setText(mDialogListHeadNames[i]);
					tv.setPadding(mActivity.getResources().getDimensionPixelSize(R.dimen.table_padding_left), 0, 0, 0);
				}
			}
		}	
		
		TextView titleTv = (TextView) mDialogView.findViewById(R.id.edit_title);
		titleTv.setText(R.string.lunch_contact_window);
		
		CheckBox allSelectCb = (CheckBox)mDialogListLayout.findViewById(R.id.cooperation_receive_people_checkbox);
		allSelectCb.setOnCheckedChangeListener(new OnCheckedChangeListener() {
			
			@SuppressLint("UseValueOf") @Override
			public void onCheckedChanged(CompoundButton arg0, boolean arg1) {
				if (arg1) {
					//All Select
					for (int i = 0; i < mDialogListView.getChildCount(); i++) {
						View view = mDialogListView.getChildAt(i);
						CheckBox cb = (CheckBox)view.findViewById(R.id.cooperation_receive_people_checkbox);
						cb.setChecked(true);					
					}					
					
				} else {
					//All cancel
					for (int i = 0; i < mDialogListView.getChildCount(); i++) {
						View view = mDialogListView.getChildAt(i);
						CheckBox cb = (CheckBox)view.findViewById(R.id.cooperation_receive_people_checkbox);
						cb.setChecked(false);					
					}					
					
				}
				mDialogAdapter.notifyDataSetChanged();
			}
		});
		
		typedArray.recycle();

		mDialogListView = (ListView) mDialogView
				.findViewById(R.id.cooperation_receive_people_listview);

		mDialogAdapter = new DataListAdapter<User>(mActivity.getBaseContext(),
				mDialogListAdapterManager);
		mDialogListView.setAdapter(mDialogAdapter);
		mDialogListView.setBackgroundColor(Color.WHITE);		
		
	}
	
	private DataManagerInterface mAcceptPeopleManage = new DataManagerInterface(){

		@Override
		public void getDataOnResult(ResultStatus status, List<?> list) {
			if (status.getCode() != AnalysisManager.SUCCESS_DB_QUERY) {
				Message msg = new Message();
				msg.obj = status.getMessage();
				mToastHandler.sendMessage(msg);
			}
		}};	
	
	private DataListAdapter.ListAdapterInterface mDialogListAdapterManager = new DataListAdapter.ListAdapterInterface() {
		@Override
		public int getLayoutId() {
			return R.layout.cooperation_receive_people_list_item;
		}

		@Override
		public View getHeaderView() {
			return mDialogListLayout;
		}

		@Override
		public void regesterListeners(final ViewHolder viewHolder, final int position) {
			for (int i = 0; i < viewHolder.tvs.length; i++) {
				viewHolder.tvs[i].setOnClickListener(new View.OnClickListener() {
					@Override
					public void onClick(View view) {
						viewHolder.cbs[0].performClick();
					}
				});
			}
			
			viewHolder.cbs[0].setOnCheckedChangeListener(new OnCheckedChangeListener() {
				
				@SuppressLint("UseValueOf") @Override
				public void onCheckedChanged(CompoundButton arg0, boolean arg1) {
					if (arg1) {
						mDialogAdapter.setPickSelected(position);
						mCheckBoxCheckedBuffer.add(new Integer(position));
					} else {
						mDialogAdapter.setPickSelected(position);
						mCheckBoxCheckedBuffer.remove(new Integer(position));
					}	
				}
			});
		}

		@Override
		public void initListViewItem(View convertView, ViewHolder holder,
				DataListAdapter adapter, int position) {
			
				boolean findFlag = false;
				for (int i = 0; i < mCheckBoxCheckedBuffer.size(); i++){
					if(mCheckBoxCheckedBuffer.get(i).intValue() == position) {
						findFlag = true;
					}
				}
				
				holder.cbs[0].setChecked(findFlag == false ? false: true);
				
				holder.tvs[0].setText(mShowUserList.get(position).getName());
				holder.tvs[0].setTextColor(Color.BLACK);
				holder.tvs[1].setText(mShowUserList.get(position).getObs_name());
				holder.tvs[1].setTextColor(Color.BLACK);
				holder.tvs[2].setText(mShowUserList.get(position).getTel());
				holder.tvs[2].setTextColor(Color.BLACK);
		}

		@Override
		public void initLayout(View convertView, ViewHolder holder) {
			holder.cbs = new CheckBox[1];
			holder.cbs[0] = (CheckBox)convertView.findViewById(R.id.cooperation_receive_people_checkbox);
			holder.tvs = new TextView[mDialogDisplayItemIds.length];
			for (int i = 0; i < mDialogDisplayItemIds.length; i++) {
				holder.tvs[i] = (TextView) convertView
						.findViewById(mDialogDisplayItemIds[i]);
			}		
		}

		@Override
		public List findByCondition(Object... condition) {
			return null;
		}

		@Override
		public boolean isSameObject(Object t1, Object t2) {
			return false;
		}	
	};
}
