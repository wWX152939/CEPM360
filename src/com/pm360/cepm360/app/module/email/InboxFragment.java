package com.pm360.cepm360.app.module.email;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
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
import com.pm360.cepm360.app.module.message.MessageService;
import com.pm360.cepm360.common.GLOBAL;
import com.pm360.cepm360.common.ResultStatus;
import com.pm360.cepm360.common.json.AnalysisManager;
import com.pm360.cepm360.entity.MailBox;
import com.pm360.cepm360.entity.Message;
import com.pm360.cepm360.services.mail.RemoteMailService;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class InboxFragment extends EmailFragment<MailBox> {
	RemoteMailService mService = RemoteMailService.getInstance();
	BroadcastReceiver mReceiver;
	
	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {
		mReceiver = new BroadcastReceiver() {

			@Override
			public void onReceive(Context context, Intent intent) {
				if (intent.getAction().equals(MessageService.ACTION_MESSAGE)) {
					
					// 重新加载数据
					if (intent.getIntExtra(MessageService.MESSAGE_TYPE, 0) 
							== Integer.parseInt(GLOBAL.MSG_TYPE_KEY[14][0])) {
						
						// 将当前选中列表项作为重新加载数据后的定位项
						mSpecifiedItem = mCurrentListItem;
						mServiceImplement.getListData();
					}
				}
			}
		};
		
		IntentFilter intentFilter = new IntentFilter(MessageService.ACTION_MESSAGE);
		getActivity().registerReceiver(mReceiver, intentFilter);
		
		// 使能多选功能
		enableMultSelect();
		
		init(MailBox.class, mListInterface, mServiceInterface, null, null, EMAIL_INBOX);
		
		// 父类初始化
		View view = super.onCreateView(inflater, container, savedInstanceState);
		
		return view;
	}
	
	
	@Override
	public void onDestroy() {
		getActivity().unregisterReceiver(mReceiver);
		super.onDestroy();
	}

	/**
	 * 获取参数信息
	 */
	@Override
	protected void getArgumentsInfo() {
		super.getArgumentsInfo();
		
		Bundle bundle = getArguments();
		if (bundle.containsKey("specified_item")) {
			mSpecifiedItem = (MailBox) bundle.getSerializable("specified_item");
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
			mService.getInbox(getServiceHandler(),
					UserCache.getCurrentUser().getUser_id(),
					UserCache.getCurrentUser().getTenant_id());
		}

		@Override
		public void addItem(MailBox t) {
			
		}

		@Override
		public void deleteItem(final MailBox t) {
			t.setDel_in(MailBoxUtils.deleteLeaderPart(t.getDel_in()));
			mService.deleteInboxMail(new DataManagerInterface() {
				
				@Override
				public void getDataOnResult(ResultStatus status, List<?> list) {
					if (status.getCode() == AnalysisManager.SUCCESS_DB_DEL
							&& getActivity() instanceof EmailActivity) {
						((EmailActivity) getActivity()).deleteLocalMessage(t.getMail_box_id(), 
								Integer.parseInt(GLOBAL.MSG_TYPE_KEY[14][0]));
					}
					
					getServiceHandler().getDataOnResult(status, list);
					refreshUnreadCount();
				}
			}, t.getMail_box_id(), t.getDel_in(), UserCache.getCurrentUserId());
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
}
