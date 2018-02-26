
package com.pm360.cepm360.app.common.activity;

import java.util.ArrayList;

import android.app.ActionBar;
import android.app.Activity;
import android.app.AlertDialog;
import android.app.Dialog;
import android.app.ProgressDialog;
import android.app.Service;
import android.content.BroadcastReceiver;
import android.content.ComponentName;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.ServiceConnection;
import android.net.Uri;
import android.os.Bundle;
import android.os.IBinder;
import android.util.TypedValue;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.WindowManager;
import android.view.inputmethod.InputMethodManager;
import android.widget.AdapterView;
import android.widget.BaseAdapter;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;

import com.pm360.cepm360.R;
import com.pm360.cepm360.app.CepmApplication;
import com.pm360.cepm360.app.common.view.BadgeView;
import com.pm360.cepm360.app.common.view.parent.ShareContent;
import com.pm360.cepm360.app.module.email.ComposeActivity;
import com.pm360.cepm360.app.module.email.EmailActivity;
import com.pm360.cepm360.app.module.message.MessageActivity;
import com.pm360.cepm360.app.module.message.MessageService;
import com.pm360.cepm360.app.utils.UtilTools;

public class ActionBarActivity extends Activity {

    private TextView mTitleView;
    private TextView mUserTextView;
    private TextView mTenantTextView;
    private ImageButton mSendEmail;
    private ImageButton mShared;
    private ImageButton mCallPhone;
    private ImageButton mBackView;
    private ImageButton mMessageView;
    private ImageButton mMenuView;
    private ImageButton mLogoffView;
    
    private View mMessagePopupView;
    private Dialog mMessageDialog;
    private MessageMenuAdapter mAdapter;
    private BadgeView mBadgeView;

    private MessageService mService;
    private boolean mBound = false;
    private Intent mMsgIntent;
    private MsgBroadcastReceiver mReceiver;
    private IntentFilter mIntentFilter;
    private ProgressDialog mProgressDialog;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        initActionBar();
        mMsgIntent = new Intent(this, MessageService.class);
        mIntentFilter = new IntentFilter();
        mIntentFilter.addAction(MessageService.ACTION_MESSAGE);
        mReceiver = new MsgBroadcastReceiver();
    }

    @Override
    protected void onStart() {
        super.onStart();
        bindService(mMsgIntent, mConnection, Service.BIND_AUTO_CREATE);
        registerReceiver(mReceiver, mIntentFilter);
    }

    @Override
    protected void onStop() {
        super.onStop();
        unregisterReceiver(mReceiver);
        if (mBound) {
            unbindService(mConnection);
            mBound = false;
        }
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
                if (mMessageView.getVisibility() == View.VISIBLE)
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

    private void initActionBar() {
        ActionBar actionBar = getActionBar();
        actionBar.setDisplayShowTitleEnabled(false);
        actionBar.setDisplayShowHomeEnabled(false);
        actionBar.setDisplayOptions(ActionBar.DISPLAY_SHOW_CUSTOM,
                ActionBar.DISPLAY_SHOW_CUSTOM);
        actionBar.setCustomView(R.layout.activity_actionbar);
        mCallPhone = (ImageButton) actionBar.getCustomView().findViewById(R.id.call_phone);
        mSendEmail = (ImageButton) actionBar.getCustomView().findViewById(R.id.send_email);
        mShared = (ImageButton) actionBar.getCustomView().findViewById(R.id.shared);
        mTitleView = (TextView) actionBar.getCustomView().findViewById(R.id.actionbar_title);
        mUserTextView = (TextView) actionBar.getCustomView().findViewById(R.id.tv1);
        mTenantTextView = (TextView) actionBar.getCustomView().findViewById(R.id.tv2);
        
        mBackView = (ImageButton) actionBar.getCustomView().findViewById(R.id.back);
        mMessageView = (ImageButton) actionBar.getCustomView()
                .findViewById(R.id.message);
        mMenuView = (ImageButton) actionBar.getCustomView().findViewById(R.id.menu);
        mLogoffView = (ImageButton) actionBar.getCustomView().findViewById(R.id.logoff);
        View.OnClickListener listener = new View.OnClickListener() {

            @Override
            public void onClick(View view) {
                switch (view.getId()) {
					case R.id.call_phone:
						dialPhoneNumber("");
            			break;
	                case R.id.shared:
	            		shared(view);
	            		break;
                	case R.id.send_email:
                		sendEmail();
                		break;
                    case R.id.back:
                        onBackPressed();
                        break;
                    case R.id.message:
                        showMessageDialog();
                        break;
                    case R.id.menu:
                        startActivity(ProjectSelectActivity.class);
                        break;
                    case R.id.logoff:
                        showTips();
                        break;
                }
            }
        };
        mCallPhone.setOnClickListener(listener);
        mShared.setOnClickListener(listener);
        mSendEmail.setOnClickListener(listener);
        mBackView.setOnClickListener(listener);
        mMessageView.setOnClickListener(listener);
        mMenuView.setOnClickListener(listener);
        mLogoffView.setOnClickListener(listener);
        mBadgeView = new BadgeView(this, mMessageView);
        mBadgeView.setBadgePosition(BadgeView.POSITION_TOP_RIGHT);
        mBadgeView.setBadgeMargin(15, 15);
        mBadgeView.setTextSize(TypedValue.COMPLEX_UNIT_PX, getResources()
                .getDimension(R.dimen.sp10_s));

        mAdapter = new MessageMenuAdapter();
        mMessagePopupView = LayoutInflater.from(this).inflate(R.layout.message_menu_popup_layout, null);        
        ListView listView = (ListView) mMessagePopupView.findViewById(R.id.menu_list);        
        listView.setAdapter(mAdapter);
        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view,
                    int position, long id) {
                Intent intent = new Intent(Intent.ACTION_MAIN);
                if (position == 4) { // 来往函，点击直接进入来往函模块
                    intent.setClass(view.getContext(), EmailActivity.class);
                } else {
                    intent.putExtra("tabIndex", position == 5 ? position - 1
                            : position);
                    intent.setClass(view.getContext(), MessageActivity.class);
                }
                startActivity(intent);
                mMessageDialog.dismiss();
            }
        });

        mMessageDialog = new Dialog(this, R.style.MyDialogStyle);
        mMessageDialog.setContentView(mMessagePopupView);
        mMessageDialog.setCanceledOnTouchOutside(true);
    }
    
    /**
     * 消息弹出对话框
     */
    private void showMessageDialog() {
        int marginRight = UtilTools.dp2pxW(this, 12);
        if (mMenuView.getVisibility() == View.VISIBLE) {
            marginRight = UtilTools.dp2pxW(this, 67);
        }
        
        Window window = mMessageDialog.getWindow();        
        int marginTop = UtilTools.dp2pxH(this, 50);
        WindowManager.LayoutParams wmlp = window.getAttributes();
        wmlp.gravity = Gravity.RIGHT | Gravity.TOP;
        wmlp.x = marginRight;
        wmlp.y = marginTop;
        window.setAttributes(wmlp);
        window.setWindowAnimations(R.style.GlobalPopWindowAnimation);
        
        mMessageDialog.show();
    }

    public void startActivity(Class<?> cls) {
        if (cls == null) {
            UtilTools.showToast(this, R.string.no_fun);
            return;
        }
        Intent intent = new Intent(Intent.ACTION_MAIN);
        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        intent.setClass(this, cls);
        startActivity(intent);
    }

    public void startActivityForResult(Class<?> cls, int requestCode) {
        Intent intent = new Intent(Intent.ACTION_MAIN);
        intent.setClass(this, cls);
        startActivityForResult(intent, requestCode);
    }
    
    private void shared(View view) {
    	ShareContent shareContent = new ShareContent(this);
    	shareContent.shareShotScreen(view);
    }
    
    private void sendEmail() {
    	Intent intent = new Intent(this, ComposeActivity.class);
   	 	Bundle bundle = new Bundle();
   	 	bundle.putInt(ComposeActivity.EMAIL_OPERATION_KEY, 
   	 			ComposeActivity.OPERATION_NEW);
   	 	intent.putExtras(bundle);
   	 	startActivity(intent);
    }

    private void dialPhoneNumber(String phoneNumber) {
	    Intent intent = new Intent(Intent.ACTION_DIAL);
	    intent.setData(Uri.parse("tel:" + phoneNumber));
	    if (intent.resolveActivity(getPackageManager()) != null) {
	        startActivity(intent);
	    }
	}
    
    public void showTips() {

        AlertDialog alertDialog = new AlertDialog.Builder(this).setTitle(R.string.remind)
                .setMessage(R.string.exit_msg)
                .setPositiveButton(R.string.confirm, new DialogInterface.OnClickListener() {

                    public void onClick(DialogInterface dialog, int which) {
                        Intent msg_intent = new Intent(ActionBarActivity.this, MessageService.class);
                        stopService(msg_intent);
                        ((CepmApplication) getApplicationContext()).clear();
                        Intent intent = new Intent(Intent.ACTION_MAIN);
                        intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP
                                | Intent.FLAG_ACTIVITY_CLEAR_TASK
                                | Intent.FLAG_ACTIVITY_NEW_TASK);
                        intent.setClass(ActionBarActivity.this, LoginActivity.class);
                        startActivity(intent);
                    }

                }).setNegativeButton(R.string.cancel,

                        new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int which) {
                                return;
                            }
                        }).create();
        alertDialog.show();
    }
    
    protected void enableSendMail(boolean enable) {
    	mSendEmail.setVisibility(enable ? View.VISIBLE : View.GONE);
    }

    public void setTitle(String title) {
        mTitleView.setText(title);
    }
    
    public void setUserName(String userName) {
    	mUserTextView.setText(userName);
    }
    
    public void setTenantName(String obsName) {
    	mTenantTextView.setText(obsName);
    }

    public ImageButton getBackView() {
        return mBackView;
    }

    public ImageButton getMessageView() {
        return mMessageView;
    }

    public ImageButton getMenuView() {
        return mMenuView;
    }

    public ImageButton getLogoffView() {
        return mLogoffView;
    }

    @Override
    public boolean dispatchTouchEvent(MotionEvent ev) {
        if (ev.getAction() == MotionEvent.ACTION_DOWN) {
            View v = getCurrentFocus();
            if (isShouldHideInput(v, ev)) {

                InputMethodManager imm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
                if (imm != null) {
                    imm.hideSoftInputFromWindow(v.getWindowToken(), 0);
                }
            }
            return super.dispatchTouchEvent(ev);
        }
        if (getWindow().superDispatchTouchEvent(ev)) {
            return true;
        }
        return onTouchEvent(ev);
    }

    public boolean isShouldHideInput(View v, MotionEvent event) {
        if (v != null && (v instanceof EditText)) {
            int[] leftTop = {
                    0, 0
            };

            v.getLocationInWindow(leftTop);
            int left = leftTop[0];
            int top = leftTop[1];
            int bottom = top + v.getHeight();
            int right = left + v.getWidth();
            if (event.getX() > left && event.getX() < right
                    && event.getY() > top && event.getY() < bottom) {
                return false;
            } else {
                return true;
            }
        }
        return false;
    }

    public class MessageMenuAdapter extends BaseAdapter {

        private ArrayList<Item> items = new ArrayList<Item>();
        private int mUnReadCount[];

        public MessageMenuAdapter() {
            String[] tabLabels = getResources().getStringArray(R.array.message_tablabels);
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
                holder.badge.setTextSize(TypedValue.COMPLEX_UNIT_PX, getResources().getDimension(R.dimen.sp10_s)); 
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
                    if (mMessageView.getVisibility() == View.VISIBLE)
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
                    if (mMessageView.getVisibility() == View.VISIBLE)
                        mBadgeView.show();
                }
                mAdapter.setUnReadCount(unReadCount);
                mAdapter.notifyDataSetChanged();
            }
        }
    }
    
    protected void createProgressDialog(boolean indeterminate,
            boolean cancelable) {
        mProgressDialog = new ProgressDialog(ActionBarActivity.this);
        mProgressDialog.setIndeterminate(indeterminate);
        mProgressDialog.setCancelable(cancelable);
    }
    
    protected void showProgressDialog() {
    	mProgressDialog.show();
        mProgressDialog.setContentView(R.layout.layout_progress);
    }
    
    protected void showProgressDialog(String text) {
    	dismissProgressDialog();
    	mProgressDialog.show();
        mProgressDialog.setContentView(R.layout.layout_progress);
    }
    
    protected void dismissProgressDialog() {
        if (mProgressDialog != null && mProgressDialog.isShowing())
        	mProgressDialog.dismiss();
    }

}
