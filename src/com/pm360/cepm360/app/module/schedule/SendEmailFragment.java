package com.pm360.cepm360.app.module.schedule;

import android.content.Intent;
import android.view.View;
import android.view.View.OnClickListener;

import com.pm360.cepm360.R;
import com.pm360.cepm360.app.common.activity.DataManagerInterface;
import com.pm360.cepm360.app.common.activity.ListSelectActivity;
import com.pm360.cepm360.app.common.adpater.DataListAdapter.ViewHolder;
import com.pm360.cepm360.app.common.adpater.DataTreeListAdapter;
import com.pm360.cepm360.app.common.custinterface.CommonListInterface;
import com.pm360.cepm360.app.common.custinterface.FloatingMenuInterface;
import com.pm360.cepm360.app.common.custinterface.OptionMenuInterface;
import com.pm360.cepm360.app.common.custinterface.ServiceInterface;
import com.pm360.cepm360.app.common.custinterface.SimpleDialogInterface;
import com.pm360.cepm360.app.common.view.OptionsMenuView.SubMenuListener;
import com.pm360.cepm360.app.module.common.attachment.AttachmentFragment;
import com.pm360.cepm360.app.module.email.MailboxViewFragment;
import com.pm360.cepm360.common.GLOBAL;
import com.pm360.cepm360.common.ResultStatus;
import com.pm360.cepm360.entity.AttachCell;
import com.pm360.cepm360.entity.MailBox;
import com.pm360.cepm360.entity.TaskCell;
import com.pm360.cepm360.services.common.RemoteCommonService;

import java.util.ArrayList;
import java.util.List;

public abstract class SendEmailFragment<T extends AttachCell, B extends TaskCell> extends AttachmentFragment<T, B> {
	private SendEmailPresenter<B> mSendEmailPresenter;
	
	protected DataTreeListAdapter<B> mParentListAdapter;

	private CommonListInterface<T> mListInterface;
	
	protected abstract int getEmailTitleType();
	
	protected String getEmailContent() {
		String content = "";
		String[] values = getDefaultDialogValue();
		for (int i = 0; i < mDialogLableNames.length; i++) {
			String value = values[i];
			if (values[i] == null || values[i].isEmpty()) {
				value = "";
			}
			content += mDialogLableNames[i] + "：" + value + "\n"; 
		}
		return content;
	}
	

	@Override
	protected boolean doExtraRegListener(ViewHolder holder, final int position,
			int i) {
		if (super.doExtraRegListener(holder, position, i)) {
			return true;
		} else {
			String[] displays = mListInterface.getDisplayFeilds();
			
			if (displays[i].equals("record")) {
				holder.tvs[i].setOnClickListener(new OnClickListener() {

					@Override
					public void onClick(View v) {
						mCurrentItem = mListAdapter.getItem(position);
						mListAdapter.setSelected(position, true);
						String mailType = "0";
						if (getEmailTitleType() == Integer.parseInt(GLOBAL.FILE_TYPE[15][0])) {
							mailType = GLOBAL.MAIL_TABLE_TYPE[2][0];
						} else if (getEmailTitleType() == Integer.parseInt(GLOBAL.FILE_TYPE[16][0])) {
							mailType = GLOBAL.MAIL_TABLE_TYPE[3][0];
						}
						RemoteCommonService.getInstance().getMailByType(new DataManagerInterface() {
							
							@SuppressWarnings("unchecked")
							@Override
							public void getDataOnResult(ResultStatus status, List<?> list) {
								Intent intent = new Intent(getActivity(), 
										ListSelectActivity.class);
								intent.putExtra(ListSelectActivity.FRAGMENT_KEY, 
										MailboxViewFragment.class);
								intent.putExtra(ListSelectActivity.SHOW_DATA, 
										(ArrayList<MailBox>) list);
								intent.putExtra(ListSelectActivity.SELECT_MODE_KEY, false);
								startActivity(intent);
							}
						}, mailType, mCurrentItem.getId());
					}
				});
				return true;
			} else {
				return false;
			}
		}
	}
	
	@Override
	protected void init(Class<T> listItemClass,
			CommonListInterface<T> listInterface,
			ServiceInterface<T> serviceInterface,
			FloatingMenuInterface floatingMenuInterface,
			OptionMenuInterface optionMenuInterface,
			SimpleDialogInterface dialogInterface) {
		mSendEmailPresenter = new SendEmailPresenter<B>(getActivity(), mCurrentProject);
		mListInterface = listInterface;
		super.init(listItemClass, listInterface, serviceInterface,
				floatingMenuInterface, mOptionMenuInterface, dialogInterface);
	}
	
	private OptionMenuInterface mOptionMenuInterface = new OptionMenuInterface() {

		@Override
		public int getOptionMenuNames() {
			return R.array.plan_email_option_menu;
		}

		@Override
		public SubMenuListener getOptionMenuClickListener() {
			SubMenuListener listener = new SubMenuListener() {
				
				@Override
				public void onSubMenuClick(View view) {
					mOptionsMenu.dismiss();
					switch ((Integer) view.getTag()) {
						case 0:		// 修改
							mIsAddOperation = false;
							showUpdateDialog(true);
							break;
						case 1:
							commonConfirmDelete();
							break;
						case 2:
							writeEmail();
							break;
					}
				}
			};
			return listener;
		}
		
	};
	
	protected void writeEmail() {
		mSendEmailPresenter.loadFiles(mCurrentItem, mParentBean, mParentListAdapter, getDefaultDialogValue(),
				mDialogLableNames, getEmailTitleType(), mCurrentItem.getAttachment());
	}

	@Override
	public void setParentList(DataTreeListAdapter<B> listAdapter) {
		mParentListAdapter = listAdapter;
	}

}

