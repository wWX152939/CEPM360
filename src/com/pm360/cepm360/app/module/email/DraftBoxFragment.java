package com.pm360.cepm360.app.module.email;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.pm360.cepm360.R;
import com.pm360.cepm360.app.cache.ContactCache;
import com.pm360.cepm360.app.cache.UserCache;
import com.pm360.cepm360.app.common.activity.ListSelectActivity;
import com.pm360.cepm360.app.common.custinterface.CallBack;
import com.pm360.cepm360.app.common.custinterface.ListNoHeaderInterface;
import com.pm360.cepm360.common.GLOBAL;
import com.pm360.cepm360.entity.MailBox;
import com.pm360.cepm360.entity.Message;
import com.pm360.cepm360.services.mail.RemoteMailService;

import java.util.HashMap;
import java.util.Map;

public class DraftBoxFragment extends EmailFragment<MailBox> {
	
	RemoteMailService mService = RemoteMailService.getInstance();
	
	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {
		
		// 使能多选功能
		enableMultSelect();
		
		init(MailBox.class, mListInterface, mServiceInterface, null, null, EMAIL_DRAFTBOX);
		
		// 父类初始化
		View view = super.onCreateView(inflater, container, savedInstanceState);
		
		return view;
	}
	
	ListNoHeaderInterface<MailBox> mListInterface = new ListNoHeaderInterface<MailBox>() {

		@Override
		public Map<String, Map<String, String>> getDisplayFieldsSwitchMap() {
			Map<String, Map<String, String>> switchMap 
					= new HashMap<String, Map<String, String>>();
			switchMap.put("receiver", ContactCache.getContactIdNameMap());
			
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
					"receiver",
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
			mService.getDraftbox(getServiceHandler(),
					UserCache.getCurrentUser().getUser_id(),
					UserCache.getCurrentUser().getTenant_id());
		}

		@Override
		public void addItem(MailBox t) {
			
		}

		@Override
		public void deleteItem(MailBox t) {
			mService.deleteDraftMail(getServiceHandler(), t.getMail_box_id());
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
					Message message = a;
					mService.markRead(getServiceHandler(),
							mailBox.getMail_box_id(), 
							mailBox.getIs_read(), 
							message.getMessage_id());
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
