package com.pm360.cepm360.app.module.email;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.pm360.cepm360.R;
import com.pm360.cepm360.app.cache.ContactCache;
import com.pm360.cepm360.app.cache.UserCache;
import com.pm360.cepm360.app.common.activity.DataManagerInterface;
import com.pm360.cepm360.app.common.activity.ListSelectActivity;
import com.pm360.cepm360.app.common.custinterface.CallBack;
import com.pm360.cepm360.app.common.custinterface.ListNoHeaderInterface;
import com.pm360.cepm360.common.GLOBAL;
import com.pm360.cepm360.common.ResultStatus;
import com.pm360.cepm360.common.json.AnalysisManager;
import com.pm360.cepm360.entity.MailBox;
import com.pm360.cepm360.entity.Message;
import com.pm360.cepm360.services.mail.RemoteMailService;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class MailboxViewFragment extends EmailFragment<MailBox> {
	
	// 服务
	RemoteMailService mService = RemoteMailService.getInstance();
	
	// 设置显示数据列表
	private List<MailBox> mShowDataList;
	
	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {
		
		mEnableInnerButton = true;
		mViewMode = true;
		
		init(MailBox.class, mListInterface, mServiceInterface, null, null, EMAIL_INBOX);

		// 父类初始化
		View view = super.onCreateView(inflater, container, savedInstanceState);
		
		return view;
	}
	
	/**
	 * 获取参数信息
	 */
	@SuppressWarnings("unchecked")
	@Override
	protected void getArgumentsInfo() {
		super.getArgumentsInfo();
		
		Bundle bundle = getArguments();
		if (bundle.containsKey("specified_item")) {
			mSpecifiedItem = (MailBox) bundle.getSerializable("specified_item");
		}
		
		if (bundle.containsKey(ListSelectActivity.SHOW_DATA)) {
			mShowDataList = (List<MailBox>) bundle
					.getSerializable(ListSelectActivity.SHOW_DATA);
		}
	}
	
	ListNoHeaderInterface<MailBox> mListInterface = new ListNoHeaderInterface<MailBox>() {

		@Override
		public Map<String, Map<String, String>> getDisplayFieldsSwitchMap() {
			Map<String, Map<String, String>> switchMap 
					= new HashMap<String, Map<String, String>>();
			switchMap.put("sender", ContactCache.getContactIdNameMap());
			
			return switchMap;
		}

		@Override
		public int getListItemId(MailBox t) {
			return t.getMail_box_id();
		}

		@Override
		public String[] getDisplayFeilds() {
			return new String[] {
					"read",
					"sender",
					"mail_time",
					"title"
			};
		}

		@Override
		public int getListLayoutId() {
			return R.layout.email_list_layout;
		}

		@Override
		public int getListItemIds() {
			return R.array.email_listitem_ids;
		}
	};
	
	MailServiceInterface mServiceInterface = new MailServiceInterface() {

		@Override
		public void getListData() {
			if (mShowDataList != null && !mShowDataList.isEmpty()) {
				sortByDate(mShowDataList);
				
				// 加载联系人详情
				ContactCache.load(MailBoxUtils.getAllContacts(mShowDataList),
						new CallBack<Void, Integer>() {
					
					@Override
					public Void callBack(Integer a) {

						sendEmptyMessageDelayed(DISMISS_PROGRESS_DIALOG);
						mListAdapter.setDataList(mShowDataList);
						mListAdapter.setShowDataList(mShowDataList);
						
						if (mShowDataList.size() > 0) {
							handleClick(0);
						}
						
						if (mCurrentBox == EMAIL_INBOX) {
							refreshUnreadCount();
						}
						return null;
					}
				});
			} else {
				sendEmptyMessageDelayed(DISMISS_PROGRESS_DIALOG);
			}
		}

		@Override
		public void addItem(MailBox t) {
			
		}

		@Override
		public void deleteItem(MailBox t) {
			
		}

		@Override
		public void updateItem(MailBox t) {
			
		}

		@SuppressWarnings({ "rawtypes", "unchecked" })
		@Override
		public void read(final MailBox mailBox) {
			CallBack<Void, Message> callBack = new CallBack<Void, Message>() {

				@Override
				public Void callBack(Message a) {
					final Message message = a;
					mService.markRead(new DataManagerInterface() {
						
						@Override
						public void getDataOnResult(ResultStatus status, List<?> list) {
							switch (status.getCode()) {
								case AnalysisManager.SUCCESS_DB_UPDATE:
									if (getActivity() instanceof EmailActivity) {
										((EmailActivity) getActivity()).readLocalMessage(message
												.getMessage_id());
									} else if (getActivity() instanceof ListSelectActivity) {
										((ListSelectActivity) getActivity()).readLocalMessage(message
												.getMessage_id());
									}
									break;
								case AnalysisManager.EXCEPTION_DB_UPDATE:
									MailBoxUtils.setEmailRead(mCurrentListItem, UserCache
											.getCurrentUser(), GLOBAL.MSG_READ[0][0]);
									break;
								default:
									break;
							}
						
							getServiceHandler().getDataOnResult(status, list);
							refreshUnreadCount();
						}
					}, mailBox.getMail_box_id(), mailBox.getIs_read(), message.getMessage_id());
					return null;
				}
			};
			
			if (getActivity() instanceof EmailActivity) {
				((EmailActivity) getActivity()).getMessage(
						mCurrentListItem.getMail_box_id(), 
						Integer.parseInt(GLOBAL.MSG_TYPE_KEY[14][0]),
						callBack);
			} else if (getActivity() instanceof ListSelectActivity) {
				((ListSelectActivity) getActivity()).getMessage(
						mCurrentListItem.getMail_box_id(), 
						Integer.parseInt(GLOBAL.MSG_TYPE_KEY[14][0]),
						callBack);
			}
		}
	};

	@Override
	public void setListTitle(boolean isSearch) {
		super.setListTitle(isSearch);
		
		if (!isSearch) {
			mListTitle.setText(R.string.email_come_go_list);
		}
	}
}
