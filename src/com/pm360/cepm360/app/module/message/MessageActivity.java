
package com.pm360.cepm360.app.module.message;

import android.app.Service;
import android.content.BroadcastReceiver;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.ServiceConnection;
import android.database.Cursor;
import android.database.CursorWrapper;
import android.graphics.Color;
import android.os.Bundle;
import android.os.IBinder;
import android.util.TypedValue;
import android.view.LayoutInflater;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.TranslateAnimation;
import android.widget.AdapterView;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.RelativeLayout;
import android.widget.TabHost;
import android.widget.TabHost.TabSpec;
import android.widget.TextView;

import com.pm360.cepm360.R;
import com.pm360.cepm360.app.common.activity.ActionBarActivity;
import com.pm360.cepm360.app.common.view.BadgeView;
import com.pm360.cepm360.app.common.view.parent.BaseViewPager;
import com.pm360.cepm360.app.module.home.portal.IntentHelper;
import com.pm360.cepm360.app.utils.UtilTools;
import com.pm360.cepm360.entity.Message;

import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.List;

/**
 * MessageActivity 消息显示页面
 * 
 * 列表使用了MessageCursorAdapter 从本地数据库中读出数据
 * MessageCursorAdapter 中使用到 R.array.message_types，
 * 这里要注意了，message_types应该与GLOBAL.MSG_TYPE_KEY保持一致
 *
 */
public class MessageActivity extends ActionBarActivity {

    private TranslateAnimation mTopInAction, mTopOutAction, mDownInAction, mDownOutAction;
    private TabHost mTabHost;
    private int[] mTabIcon;
    private String[] mTabLabels;
    private View[] mTabs = new View[5];
    private int mCurrentTab = 0;
    private RelativeLayout mTab2_TabHost;
    private View[] mTab2_Tabs = new View[2];
    private MessageCursorAdapter mAdapter;
    private ArrayList<MessageCursorAdapter> mAdapterList = new ArrayList<MessageCursorAdapter>();

    private MessageService mService;
    private boolean mBound = false;
    private Intent mMsgIntent;
    private MsgBroadcastReceiver mReceiver;
    private IntentFilter mIntentFilter;
    private BadgeView[] mTabBadgeViews = new BadgeView[5];
    private int mUnReadCount[];
    @SuppressWarnings("serial")
	private List<View> mIndicatorView = new ArrayList<View>() {};
    
    private LayoutInflater mInflater;

    private AdapterView.OnItemClickListener mItemClickListener = new AdapterView.OnItemClickListener() {

        @Override
        public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
            CursorWrapper wrapper = (CursorWrapper) parent
                    .getItemAtPosition(position);
            int columnIndex = wrapper.getColumnIndex("message_id");
            int msg_id = wrapper.getInt(columnIndex);
            mService.readMessage(msg_id);
            startActivity(mService.getMessageById(msg_id));
            // finish();
        }
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getMenuView().setVisibility(View.GONE);
        getMessageView().setVisibility(View.GONE);
        setContentView(R.layout.message_activity);
        initAnimation();
        initTabs();
        
        mMsgIntent = new Intent(this, MessageService.class);
        mIntentFilter = new IntentFilter();
        mIntentFilter.addAction(MessageService.ACTION_MESSAGE);
        mReceiver = new MsgBroadcastReceiver();
    }

    @Override
    protected void onNewIntent(Intent intent) {
        super.onNewIntent(intent);
        setIntent(intent);
    }

    @Override
    protected void onStart() {
        super.onStart();
        bindService(mMsgIntent, mConnection, Service.BIND_AUTO_CREATE);
        registerReceiver(mReceiver, mIntentFilter);
        
        int tabIndex = getIntent().getIntExtra("tabIndex", -1);        
        if (tabIndex == -1) {            
            mTabHost.setCurrentTab(mCurrentTab);
        } else {
            mTabHost.setCurrentTab(tabIndex);
            mCurrentTab = tabIndex;
        }
        // 清除绑定的数据，确保绑定的数据只用一次
        getIntent().removeExtra("tabIndex");
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

    @Override
    protected void onDestroy() {
        super.onDestroy();
        for (MessageCursorAdapter adapter : mAdapterList) {
            adapter.unregister();
        }
    }

    private ServiceConnection mConnection = new ServiceConnection() {

        @Override
        public void onServiceConnected(ComponentName name, IBinder service) {
            MessageService.LocalBinder binder = (MessageService.LocalBinder) service;
            mService = binder.getService();
            mBound = true;
            mUnReadCount = mService.getUnreadCount();
            for (int i = 0; i < mTabBadgeViews.length; i++) {               
                if (i == 4) { // 公告   （由于来往函不在MessageActivity显示）
                    mTabBadgeViews[i].setText(mUnReadCount[i+1] + "");
                    if (mUnReadCount[i+1] == 0) {
                        mTabBadgeViews[i].hide();
                    } else {
                        mTabBadgeViews[i].show();
                    }
                } else {
                    mTabBadgeViews[i].setText(mUnReadCount[i] + "");
                    if (mUnReadCount[i] == 0) {
                        mTabBadgeViews[i].hide();
                    } else {
                        mTabBadgeViews[i].show();
                    }
                }
            }
        }

        @Override
        public void onServiceDisconnected(ComponentName name) {
            mBound = false;
        }

    };

    private void initTabs() {
        mTabHost = (TabHost) findViewById(R.id.tabhost);
        mTabHost.setup();
        mTabs[0] = mTabHost.findViewById(R.id.tab1);
        mTabs[1] = mTabHost.findViewById(R.id.tab2);
        mTabs[2] = mTabHost.findViewById(R.id.tab3);
        mTabs[3] = mTabHost.findViewById(R.id.tab4);
        mTabs[4] = mTabHost.findViewById(R.id.tab5);

        mTabHost.setOnTabChangedListener(new TabHost.OnTabChangeListener() {

            @Override
            public void onTabChanged(String tabId) {
                int currentTab = mTabHost.getCurrentTab();
                if (currentTab > mCurrentTab) {
                    mTabs[mCurrentTab].startAnimation(mTopOutAction);
                    mTabs[currentTab].startAnimation(mDownInAction);
                } else {
                    mTabs[mCurrentTab].startAnimation(mDownOutAction);
                    mTabs[currentTab].startAnimation(mTopInAction);
                }
                mCurrentTab = currentTab;
                updateNavigation(currentTab);
            }
        });

        try {
            Field current = mTabHost.getClass().getDeclaredField("mCurrentTab");
            current.setAccessible(true);
            current.setInt(mTabHost, 0);
        } catch (Exception e) {
            e.printStackTrace();
        }
        mTabLabels = getResources().getStringArray(R.array.message_activity_tablabels);
        mTabIcon = new int[] {
                R.drawable.ic_navigation_message_mytask_normal, R.drawable.ic_navigation_message_mytodo_normal,
                R.drawable.ic_navigation_message_newfeedback_normal, R.drawable.ic_navigation_message_newdocument_normal,
                R.drawable.ic_navigation_announcement,
                R.drawable.ic_navigation_message_mytask_selected, R.drawable.ic_navigation_message_mytodo_selected,
                R.drawable.ic_navigation_message_newfeedback_selected, R.drawable.ic_navigation_message_newdocument_selected,
                R.drawable.ic_navigation_announcement_select,
        };
        int[] tabIds = new int[] {
                R.id.tab1, R.id.tab2, R.id.tab3, R.id.tab4, R.id.tab5
        };
        for (int i = 0; i < mTabLabels.length; i++) {
            TabSpec spec = mTabHost.newTabSpec("" + i);
            View indicator = createIndicatorView(this, mTabHost,
                    mTabIcon[i], mTabLabels[i], i);
            spec.setIndicator(indicator);
            spec.setContent(tabIds[i]);
            mTabHost.addTab(spec);
            mIndicatorView.add(indicator);

            mTabBadgeViews[i] = new BadgeView(this, indicator);
            mTabBadgeViews[i].setBadgePosition(BadgeView.POSITION_TOP_RIGHT);
            mTabBadgeViews[i].setBadgeMargin(UtilTools.dp2pxW(this, 20), UtilTools.dp2pxH(this, 25));
            mTabBadgeViews[i].setTextSize(TypedValue.COMPLEX_UNIT_PX, getResources().getDimensionPixelSize(R.dimen.sp12_s));
            if (mUnReadCount != null) {
                if (i == 4) { // 公告   （由于来往函不在MessageActivity显示）
                    mTabBadgeViews[i].setText(mUnReadCount[i+1] + "");
                    if (mUnReadCount[i+1] == 0) {
                        mTabBadgeViews[i].hide();
                    } else {
                        mTabBadgeViews[i].show();
                    }
                } else {
                    mTabBadgeViews[i].setText(mUnReadCount[i] + "");
                    if (mUnReadCount[i] == 0) {
                        mTabBadgeViews[i].hide();
                    } else {
                        mTabBadgeViews[i].show();
                    }
                }
            }
        }

        try {
            Field current = mTabHost.getClass().getDeclaredField("mCurrentTab");
            current.setAccessible(true);
            current.set(mTabHost, -1);
        } catch (Exception e) {
            e.printStackTrace();
        }
        for (int i = 0; i < mTabs.length; i++) {
            initTab(i, 0);
        }
    }

    private void initTab(final int tabIndex, int is_process) {
        switch (tabIndex) {
            case 0:
            case 2:
            case 3:
                initMessageListView(mTabs[tabIndex], tabIndex, is_process);
                break;
            case 4: // 公告 对应数据库中tab_type:5 (往来函不在MessageActivity显示)
                initMessageListView(mTabs[tabIndex], tabIndex+1, is_process);
                break;
            case 1: // 我的待办
                mTab2_TabHost = (RelativeLayout) mTabs[tabIndex];
            	mTab2_Tabs[0] = mInflater.inflate(R.layout.message_table2, mTab2_TabHost, false);
            	mTab2_Tabs[1] = mInflater.inflate(R.layout.message_table2, mTab2_TabHost, false);
            	BaseViewPager viewPager = new BaseViewPager(this, mTab2_TabHost);
            	List<View> viewList = new ArrayList<View>();
            	viewList.add(mTab2_Tabs[0]);
            	viewList.add(mTab2_Tabs[1]);
            	viewPager.init(R.array.message_tab2_tablabels, viewList, null);
                initMessageListView(mTab2_Tabs[0], tabIndex, 0);
                initMessageListView(mTab2_Tabs[1], tabIndex, 1);
                break;
        }
    }

    private void initMessageListView(View msgTable, int tab_type, int is_process) {
        ListView listView = (ListView) msgTable.findViewById(R.id.msg_listview);
        Cursor cursor = getContentResolver().query(MessageService.MESSAGE_URI, null,
                "tab_type=? AND is_process=?", new String[] {
                        tab_type + "", is_process + ""
                }, "_id desc");
        listView.setOnItemClickListener(mItemClickListener);
        mAdapter = new MessageCursorAdapter(this, cursor);
        mAdapter.register();
        listView.setAdapter(mAdapter);
    }

    private View createIndicatorView(Context context, TabHost tabHost,
            int icon, String title, int index) {
    	mInflater = (LayoutInflater) context
                .getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        View tabIndicator = mInflater.inflate(R.layout.navigation_item,
                tabHost.getTabWidget(), false);
        ImageView iconView = (ImageView) tabIndicator
                .findViewById(R.id.icon);
        TextView titleView = (TextView) tabIndicator
                .findViewById(R.id.title);
        titleView.setText(title);
        iconView.setImageResource(icon);
        
        return tabIndicator;
    }
    
    private void updateNavigation(int currentTab) {
        for (int i = 0; i < mIndicatorView.size(); i++) {
            ImageView iconView = (ImageView) mIndicatorView.get(i)
                    .findViewById(R.id.icon);
            TextView titleView = (TextView) mIndicatorView.get(i)
                    .findViewById(R.id.title);
            titleView.setTextColor(i == currentTab ? getResources().getColor(R.color.system_action_bar_bg) : Color.BLACK);
            iconView.setImageResource(i == currentTab ? mTabIcon[mTabLabels.length + currentTab] : mTabIcon[i]);
        }
    }

    /**
     * See IntentHelper.startActivity(context, message) function
     * 
     * @param msg
     */
    private void startActivity(Message msg) {
        
        switch (msg.getType()) {
        case 11:
        case 25:
        case 26:
        case 27:
        case 28:
        case 29:
        case 30:
            // 最新文档 (11,25,26,27,28,29,30) 直接打开
            IntentHelper.downloadAndOpen(MessageActivity.this, msg.getType_id());
            break;
        default:
            // 其他跳转到activity
            IntentHelper.startActivity(MessageActivity.this, msg);
            break;
        }
        
    }

    private class MsgBroadcastReceiver extends BroadcastReceiver {

        @Override
        public void onReceive(Context context, Intent intent) {
            if (mBound) {
                mUnReadCount = mService.getUnreadCount();
                for (int i = 0; i < mUnReadCount.length; i++) {               
                    if (i == 4 || i == 5) { // 公告   （由于来往函不在MessageActivity显示）
                        if (i== 5) continue; // i == 4 表示为公告，但对于的是mUnReadCount[5]
                        mTabBadgeViews[i].setText(mUnReadCount[i+1] + "");
                        if (mUnReadCount[i+1] == 0) {
                            mTabBadgeViews[i].hide();
                        } else {
                            mTabBadgeViews[i].show();
                        }
                    } else {
                        mTabBadgeViews[i].setText(mUnReadCount[i] + "");
                        if (mUnReadCount[i] == 0) {
                            mTabBadgeViews[i].hide();
                        } else {
                            mTabBadgeViews[i].show();
                        }
                    }
                }
            }
        }
    }

    private void initAnimation() {
        mTopInAction = new TranslateAnimation(Animation.RELATIVE_TO_SELF, 0.0f,
                Animation.RELATIVE_TO_SELF, 0.0f, Animation.RELATIVE_TO_SELF,
                -1.0f, Animation.RELATIVE_TO_SELF, 0.0f);
        mTopInAction.setDuration(500);
        mTopOutAction = new TranslateAnimation(Animation.RELATIVE_TO_SELF,
                0.0f, Animation.RELATIVE_TO_SELF, 0.0f,
                Animation.RELATIVE_TO_SELF, 0.0f, Animation.RELATIVE_TO_SELF,
                -1.0f);
        mTopOutAction.setDuration(500);
        mDownInAction = new TranslateAnimation(Animation.RELATIVE_TO_SELF, 0.0f,
                Animation.RELATIVE_TO_SELF, 0.0f, Animation.RELATIVE_TO_SELF,
                1.0f, Animation.RELATIVE_TO_SELF, 0.0f);
        mDownInAction.setDuration(500);
        mDownOutAction = new TranslateAnimation(Animation.RELATIVE_TO_SELF,
                0.0f, Animation.RELATIVE_TO_SELF, 0.0f,
                Animation.RELATIVE_TO_SELF, 0.0f, Animation.RELATIVE_TO_SELF,
                1.0f);
        mDownOutAction.setDuration(500);
    }
}
