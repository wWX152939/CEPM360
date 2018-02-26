package com.pm360.cepm360.app.common.view.parent;

import android.app.ActionBar;
import android.app.Activity;
import android.app.AlertDialog;
import android.app.AlertDialog.Builder;
import android.app.Dialog;
import android.app.Service;
import android.content.BroadcastReceiver;
import android.content.ComponentName;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.ServiceConnection;
import android.os.IBinder;
import android.util.TypedValue;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.WindowManager;
import android.widget.AdapterView;
import android.widget.BaseAdapter;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;

import com.pm360.cepm360.R;
import com.pm360.cepm360.app.CepmApplication;
import com.pm360.cepm360.app.cache.ProjectCache;
import com.pm360.cepm360.app.common.activity.LoginActivity;
import com.pm360.cepm360.app.common.activity.ProjectSelectActivity;
import com.pm360.cepm360.app.common.view.BadgeView;
import com.pm360.cepm360.app.module.email.EmailActivity;
import com.pm360.cepm360.app.module.message.MessageActivity;
import com.pm360.cepm360.app.module.message.MessageService;
import com.pm360.cepm360.app.utils.UtilTools;
import com.pm360.cepm360.entity.Project;

import java.util.ArrayList;

public class BaseActionBar {
	
	private Activity mActivity;
	private ActionBar mActionBar;
    private TextView mTitleView;
	private ImageButton mMessageView;
	private ImageView mRefreshView;
	private ImageView mMenu;
	private AlertDialog mCheckDialog;
	
    private Dialog mMessageDialog;
    private MessageMenuAdapter mAdapter;
    private BadgeView mBadgeView;

    private MessageService mService;
    private boolean mBound = false;	
    
    private Intent mMsgIntent;
    private MsgBroadcastReceiver mReceiver;
    private IntentFilter mIntentFilter;
	
	public BaseActionBar(Activity activity) {
		mActivity = activity;
	}
	
	public ImageView getRefreshView() {
		return mRefreshView;
	}
	
	public MessageService getService() {
		return mService;
	}
	
	public void myOnStart() {
        mActivity.bindService(mMsgIntent, mConnection, Service.BIND_AUTO_CREATE);
        mActivity.registerReceiver(mReceiver, mIntentFilter);		
	}
	
	public void myOnStop() {
    	mActivity.unregisterReceiver(mReceiver);
        if (mBound) {
        	mActivity.unbindService(mConnection);
            mBound = false;
        }
    }	
	
	private void showCheckDialog() {
		AlertDialog.Builder builder = new Builder(mActivity);
		builder.setMessage(mActivity.getString(R.string.exit_msg));
		builder.setTitle(mActivity.getString(R.string.logoff));
		
		builder.setPositiveButton(mActivity.getString(R.string.confirm), new DialogInterface.OnClickListener() {
			
			@Override
			public void onClick(DialogInterface arg0, int arg1) {
				Intent msg_intent = new Intent(mActivity, MessageService.class);
				mActivity.stopService(msg_intent);
				((CepmApplication) mActivity.getApplicationContext()).clear();
                Intent intent = new Intent(Intent.ACTION_MAIN);
                intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP |Intent.FLAG_ACTIVITY_CLEAR_TASK
                        | Intent.FLAG_ACTIVITY_NEW_TASK);
            	intent.setClass(mActivity, LoginActivity.class);
            	mActivity.startActivity(intent);
			}
		});
		
		builder.setNegativeButton(mActivity.getString(R.string.cancel), new DialogInterface.OnClickListener() {
			
			@Override
			public void onClick(DialogInterface arg0, int arg1) {
				mCheckDialog.dismiss();
			}
		});
		mCheckDialog = builder.create();
		mCheckDialog.show();

	}
	
	public void initActionBar() {
        mActionBar = mActivity.getActionBar();
        mActionBar.setDisplayShowTitleEnabled(false);
        mActionBar.setDisplayShowHomeEnabled(false);
        mActionBar.setDisplayOptions(ActionBar.DISPLAY_SHOW_CUSTOM,
                ActionBar.DISPLAY_SHOW_CUSTOM);
        mActionBar.setCustomView(R.layout.actionbar_layout);
        ImageView backView = (ImageView) mActionBar.getCustomView().findViewById(R.id.back);
        mMenu = (ImageView) mActionBar.getCustomView().findViewById(R.id.menu);
        ImageView logoffView = (ImageView) mActionBar.getCustomView().findViewById(R.id.logoff);
        mRefreshView = (ImageView) mActionBar.getCustomView().findViewById(R.id.refresh);
        mMessageView = (ImageButton) mActionBar.getCustomView().findViewById(R.id.message);   
        
        mTitleView = (TextView) mActionBar.getCustomView().findViewById(
                R.id.actionbar_title);
        Project project = ProjectCache.getCurrentProject();
        if (project != null) {
            String names = "" + project.getName();
            mTitleView.setText(names);
        }
  
        View.OnClickListener listener = new View.OnClickListener() {

            @Override
            public void onClick(View view) {
                switch (view.getId()) {
                case R.id.back:
                    mActivity.onBackPressed();
                    break;
                case R.id.menu:
                    Intent intent = new Intent(Intent.ACTION_MAIN);
                    intent.setClass(mActivity, ProjectSelectActivity.class);
                    mActivity.startActivity(intent);
                    break;
                case R.id.logoff:
                    showCheckDialog();
                    break;
                case R.id.message:
                    showMessageDialog();
                    break;
                }
            }
        };
        backView.setOnClickListener(listener);
        mMenu.setOnClickListener(listener); 
        logoffView.setOnClickListener(listener);
        mMessageView.setOnClickListener(listener);
        
        mBadgeView = new BadgeView(mActivity, mMessageView);
        mBadgeView.setBadgePosition(BadgeView.POSITION_TOP_RIGHT);
        mBadgeView.setTextSize(TypedValue.COMPLEX_UNIT_PX, mActivity.getResources().getDimension(R.dimen.sp10_s));        
        
        mAdapter = new MessageMenuAdapter();
        View popupView = LayoutInflater.from(mActivity).inflate(R.layout.message_menu_popup_layout, null);        
        ListView listView = (ListView) popupView.findViewById(R.id.menu_list);        
        listView.setAdapter(mAdapter);
        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view,
                    int position, long id) {
                Intent intent = new Intent(Intent.ACTION_MAIN);
                if (position == 4) {
                    intent.setClass(view.getContext(), EmailActivity.class);
                } else {
                    intent.putExtra("tabIndex", position == 5 ? position - 1
                            : position);
                    intent.setClass(view.getContext(), MessageActivity.class);
                }
                mActivity.startActivity(intent);
                mMessageDialog.dismiss();
            }
        });

        mMessageDialog = new Dialog(mActivity, R.style.MyDialogStyle);
        mMessageDialog.setContentView(popupView);
        mMessageDialog.setCanceledOnTouchOutside(true);    
        
        mMsgIntent = new Intent(mActivity, MessageService.class);
        mIntentFilter = new IntentFilter();
        mIntentFilter.addAction(MessageService.ACTION_MESSAGE);
        mReceiver = new MsgBroadcastReceiver();
    }
	
    private void showMessageDialog() {
        int marginRight = UtilTools.dp2pxW(mActivity, 12);
        if (mMenu.getVisibility() == View.VISIBLE) {
            marginRight = UtilTools.dp2pxW(mActivity, 67);
        }
        
        Window window = mMessageDialog.getWindow();        
        int marginTop = UtilTools.dp2pxH(mActivity, 50);
        WindowManager.LayoutParams wmlp = window.getAttributes();
        wmlp.gravity = Gravity.RIGHT | Gravity.TOP;
        wmlp.x = marginRight;
        wmlp.y = marginTop;
        window.setAttributes(wmlp);
        window.setWindowAnimations(R.style.GlobalPopWindowAnimation);
        
        mMessageDialog.show();
    }
	
	public void disableMenu() {
		mMenu.setVisibility(View.GONE);
	}
	
    public void setTitle(String title) {
        mTitleView.setText(title);
    }

    private ServiceConnection mConnection = new ServiceConnection() {

        @Override
        public void onServiceConnected(ComponentName name, IBinder service) {
            MessageService.LocalBinder binder = (MessageService.LocalBinder) service;
            mService = binder.getService();
            mBound = true;
            int[] unReadCount = mService.getUnreadCount();
            int sum = 0;
            for (int i = 0; i < unReadCount.length; i++) {
                sum += unReadCount[i];
            }
            mBadgeView.setText(sum + "");
            if (sum == 0) {
                mBadgeView.hide();
            } else {
                mBadgeView.show();
            }
            mAdapter.setUnReadCount(unReadCount);
            mAdapter.notifyDataSetChanged();
        }

        @Override
        public void onServiceDisconnected(ComponentName name) {
            mBound = false;
        }

    };	

    public class MessageMenuAdapter extends BaseAdapter {

        private ArrayList<Item> items = new ArrayList<Item>();
        private int mUnReadCount[];

        public MessageMenuAdapter() {
            String[] tabLabels = mActivity.getResources().getStringArray(R.array.message_tablabels);
            int[] tabIcon = new int[] {
                    R.drawable.message_icon_1, R.drawable.message_icon_2,
                    R.drawable.message_icon_3, R.drawable.message_icon_6,
                    R.drawable.message_icon_4, R.drawable.message_icon_7
            };
            for (int i = 0; i < tabLabels.length; i++) {
                addItem(tabLabels[i], tabIcon[i]);
            }
        }

        public void setUnReadCount(int[] unReadCount) {
            mUnReadCount = unReadCount;
        }

        @Override
        public int getCount() {
            return items.size();
        }

        @Override
        public Object getItem(int position) {
            return position;
        }

        @Override
        public long getItemId(int position) {
            return position;
        }

        public void addItem(String text, int resId) {
            Item item = new Item();
            item.text = text;
            item.resId = resId;
            items.add(item);
        }

        @Override
        public View getView(int position, View convertView, ViewGroup parent) {
            ViewHolder holder;
            if (convertView == null) {
                convertView = LayoutInflater.from(parent.getContext()).inflate(
                        R.layout.actionbar_dropdown_item, parent, false);
                holder = new ViewHolder();
                holder.icon = (ImageView) convertView.findViewById(R.id.icon);
                holder.title = (TextView) convertView.findViewById(R.id.title);
                holder.bicon = (ImageView) convertView.findViewById(R.id.badge);
                holder.badge = new BadgeView(parent.getContext(), holder.bicon);
                holder.badge.setBadgePosition(BadgeView.POSITION_TOP_RIGHT);
                holder.badge.setBadgeMargin(0);
                holder.badge.setTextSize(TypedValue.COMPLEX_UNIT_PX, mActivity.getResources().getDimension(R.dimen.sp10_s));
                convertView.setTag(holder);
            }
            else {
                holder = (ViewHolder) convertView.getTag();
            }
            holder.icon.setImageResource(items.get(position).resId);
            holder.title.setText(items.get(position).text);
            if (mUnReadCount != null) {
                holder.badge.setText(mUnReadCount[position] + "");
                if (mUnReadCount[position] == 0) {
                    holder.badge.hide();
                } else {
                    holder.badge.show();
                }
            }
            return convertView;
        }

        private class ViewHolder {
            TextView title;
            ImageView icon;
            ImageView bicon;
            BadgeView badge;
        }

        private class Item {
            String text;
            int resId;
        }
    }
    
    private class MsgBroadcastReceiver extends BroadcastReceiver {

        @Override
        public void onReceive(Context context, Intent intent) {
            if (mBound) {
                int[] unReadCount = mService.getUnreadCount();
                int sum = 0;
                for (int i = 0; i < unReadCount.length; i++) {
                    sum += unReadCount[i];
                }
                mBadgeView.setText(sum + "");
                if (sum == 0) {
                    mBadgeView.hide();
                } else {
                    mBadgeView.show();
                }
                mAdapter.setUnReadCount(unReadCount);
                mAdapter.notifyDataSetChanged();
            }
        }
    }	
    
}

