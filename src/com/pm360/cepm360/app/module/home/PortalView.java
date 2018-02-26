package com.pm360.cepm360.app.module.home;

import android.annotation.SuppressLint;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.media.ThumbnailUtils;
import android.os.Handler;
import android.os.Vibrator;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.AdapterView;
import android.widget.GridView;
import android.widget.Toast;

import com.pm360.cepm360.R;
import com.pm360.cepm360.app.module.home.HomeActivity;
import com.pm360.cepm360.app.module.home.PortalDataLoader;
import com.pm360.cepm360.app.module.home.portal.IntentHelper;
import com.pm360.cepm360.app.module.home.portal.MyTaskFragment;
import com.pm360.cepm360.app.module.home.portal.PortailListItem;
import com.pm360.cepm360.app.module.home.portal.PortalGridAdpater;
import com.pm360.cepm360.app.module.home.portal.PortalModelBean;
import com.pm360.cepm360.app.module.home.portal.PortalModelEditorActivity;
import com.pm360.cepm360.app.module.home.portal.PortalMoreListActivity;
import com.pm360.cepm360.app.module.message.MessageService;
import com.pm360.cepm360.common.GLOBAL;
import com.pm360.cepm360.common.util.DateUtils;
import com.pm360.cepm360.entity.Announcement;
import com.pm360.cepm360.entity.Files;
import com.pm360.cepm360.entity.Index_task;
import com.pm360.cepm360.entity.Message;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * 
 * 标题: PortalView 
 * 描述: 主页门户界面
 * 
 * 	1.根据mPortalModelSequences创建各个小界面（ PortalModelView ）
 *	  PortalModelView编号：
 *	       代办事项：0      我的任务：1    形象进展：2      最新反馈：3      公司通告：4      最新文档：5
 *
 *	2.PortalDataLoader加载数据
 *	       注册setOnDataChangedListener
 *	       获取数据，填充数据
 *
 *	3.注册广播监听ACTION_MESSAGE消息通知
 *	       目的是为了重新拉数据，刷新页面
 *
 *	4.PortalModelEditorActivity 负责编辑小界面的排序
 *	       将顺序写到SharedPreferences中， PortalView 重新创建小界面
 *
 */

@SuppressLint({ "InflateParams", "UseSparseArrays" }) 
public class PortalView {
    
    private Context mContext;
    private View mContentView;
    private Vibrator mVibrator;
    
    private MsgBroadcastReceiver mReceiver;
    private IntentFilter mIntentFilter;

    private GridView mPortalGridView;
    private PortalGridAdpater mPortalGridAdpater;
    
    private String[] mModelTitles;    
    private String mPortalModelSequences;
    private PortalDataLoader mHelper;
    
    private List<List<Index_task>> mTaskBigList = new ArrayList<List<Index_task>>();
    private ArrayList<PortalModelBean> mModelLists = new ArrayList<PortalModelBean>();
    private Map<Integer, ArrayList<PortailListItem>> mDatalListMap = new HashMap<Integer, ArrayList<PortailListItem>>();
    
    public static final int UPDATE_PORTAL_VIEW = 1;
    
    
    public Handler mHandler = new Handler(new Handler.Callback() {
        @Override
        public boolean handleMessage(android.os.Message msg) {
            switch (msg.what) {
            case UPDATE_PORTAL_VIEW:
                mPortalGridAdpater.notifyDataSetChanged();
                break;
            default:
                break;
            }
            
            return false;
        }
    });
    
    public PortalView(Context context, String Sequences) {
        mContext = context; 
        mPortalModelSequences = Sequences;
        initView();
    }
    
    public View getView() {
        return mContentView;
    }
    
    public void registerReceiver() {
        // 这里注册一个广播监听，当收到ACTION_MESSAGE时，重新拉数据，更新UI
        // 比如，文档管理设置了一张图片为 “置顶”， 则会发出ACTION_MESSAGE广播
        mIntentFilter = new IntentFilter();
        mIntentFilter.addAction(MessageService.ACTION_MESSAGE);
        mReceiver = new MsgBroadcastReceiver();
        
        mContext.registerReceiver(mReceiver, mIntentFilter);
    }
    
    public void unregisterReceiver() {
        mContext.unregisterReceiver(mReceiver);
    }
    
    private void initView() {
        mContentView = LayoutInflater.from(mContext).inflate(
                R.layout.activity_portal_gridview, null);
        
        mModelLists.clear();        
        mModelTitles = mContext.getResources().getStringArray(
                R.array.home_portal_model_titles);       
        
        for (int i = 0; i < GLOBAL.INDEX_TYPE.length; i++) {
            mDatalListMap.put(i, new ArrayList<PortailListItem>());
        }
        
        String[] strings = mPortalModelSequences.split(",");
        for (int i = 0; i < strings.length; i++) {    
            PortalModelBean bean = new PortalModelBean();
            ArrayList<Bitmap> bitmaps = new ArrayList<Bitmap>();
            bean.setIndex(Integer.valueOf(strings[i]));
            bean.setTitle(mModelTitles[Integer.valueOf(strings[i])]);
            bean.setBitmaps(bitmaps);
            bean.setDataList(mDatalListMap.get(Integer.valueOf(strings[i])));
            mModelLists.add(bean);
        }
        
        mVibrator = (Vibrator) mContext.getSystemService(Context.VIBRATOR_SERVICE);
        mPortalGridView = (GridView) mContentView.findViewById(R.id.portal_grid_view);
        mPortalGridAdpater = new PortalGridAdpater(mContext, R.layout.portal_model_view_layout, mModelLists);
        mPortalGridAdpater.setOnListItemClickListener(listItemClickListener);
        mPortalGridView.setAdapter(mPortalGridAdpater);
        mPortalGridView.setOnItemLongClickListener(new AdapterView.OnItemLongClickListener() {
        	// 长按弹出编辑窗口
            @Override
            public boolean onItemLongClick(AdapterView<?> parent, View view, int position,
                    long id) {             
                mVibrator.vibrate(100);
                Intent intent = new Intent(Intent.ACTION_MAIN);
                intent.putExtra("sequence", mPortalModelSequences);
                intent.setClass(mContext, PortalModelEditorActivity.class);
                ((HomeActivity) mContext).startActivityForResult(intent, HomeActivity.MODEL_EDIT_REQUEST_CODE);
                return false;
            }
            
        });
        
        mHelper = new PortalDataLoader(mContext);
        mHelper.loadAllData();
        mHelper.setOnDataChangedListener(new PortalDataLoader.OnDataChangedListener() {
            
            @Override
            public void onDataChanged(int flag) {
                // update UI
                if (flag == 1) { // 我的任务
                    
                    List<Index_task> list0 = new ArrayList<Index_task>();
                    List<Index_task> list1 = new ArrayList<Index_task>();
                    List<Index_task> list2 = new ArrayList<Index_task>();
                    List<Index_task> list3 = new ArrayList<Index_task>();
                    List<Index_task> list4 = new ArrayList<Index_task>();
                    List<Index_task> list5 = new ArrayList<Index_task>();
                    
                    
                    Date endDateTemp;
                    List<Index_task> indexAllTask = (List<Index_task>)mHelper.getMyTaskList();
                    for(int i = 0; i < indexAllTask.size(); i++) {
                    	Index_task taskTemp = indexAllTask.get(i); 
                        endDateTemp = taskTemp.getEnd_time();
	                    if (taskTemp.getSystem_time() != null) {
	                    	
	                        if (taskTemp.getProgress() == 0 || taskTemp.getActual_start_time() == null) {
	                        	list0.add(taskTemp);
	                        }
	                        if (taskTemp.getProgress() != 0) {
	                        	list1.add(taskTemp);
	                        }
	                        if (taskTemp.getEnd_time().getTime() + 86400000 < taskTemp.getSystem_time().getTime() ) {
	                        	list2.add(taskTemp);
	                        }                        
	                        if (DateUtils.isSameDate(endDateTemp, taskTemp.getSystem_time())) {
	                        	list3.add(taskTemp);
	                        }
	                        if (DateUtils.isSameWeek(endDateTemp, taskTemp.getSystem_time())) {
	                        	list4.add(taskTemp);
	                        }
	                        if (DateUtils.isSameMonth(endDateTemp, taskTemp.getSystem_time())) {
	                        	list5.add(taskTemp);
	                        }
                        } else {
                        	Toast.makeText(mContext, R.string.combination_no_systemdate, Toast.LENGTH_SHORT).show();
                        }
                    }
                    
                    mTaskBigList.clear();
                    mTaskBigList.add(list0);
                    mTaskBigList.add(list1);
                    mTaskBigList.add(list2);
                    mTaskBigList.add(list3);
                    mTaskBigList.add(list4);
                    mTaskBigList.add(list5);
                            
                    List<Integer> mListTaskNum = new ArrayList<Integer>();
                    mListTaskNum.add(list0.size());
                    mListTaskNum.add(list1.size());
                    mListTaskNum.add(list2.size());                    
                    mListTaskNum.add(list3.size());
                    mListTaskNum.add(list4.size());
                    mListTaskNum.add(list5.size());
                    
                    mPortalGridAdpater.updateMyTask(mListTaskNum);
                } else if (flag == 2) {  // 形象进展                  
                    int index = 0;
                    for (int i = 0; i < mModelLists.size(); i++) {
                        if (mModelLists.get(i).getIndex() == flag) { // 2
                            index = i;
                            break;
                        }
                    }
                    
                    mModelLists.get(index).getBitmaps().clear();
                    for (int i = 0; i < mHelper.getFilePaths().size() && i < 2; i++) {                        
                        String p = mHelper.getFilePaths().get(i);
                        createScaledBitmap(p);
                    }
                } else {
                    mDatalListMap.get(flag).clear();
                    mDatalListMap.get(flag).addAll(mHelper.getPortalList(flag));
                    mHandler.sendEmptyMessage(UPDATE_PORTAL_VIEW);   
                }
            }
        });
    }
    
    PortalGridAdpater.OnListItemClickListener listItemClickListener = new PortalGridAdpater.OnListItemClickListener() {
        
        @Override
        public void onMoreButtonClick(int viewTag, View view) {
            Intent intent = new Intent(Intent.ACTION_MAIN);
            intent.putExtra("index", viewTag);

            if (viewTag == 1) { //我的任务
                switch (view.getId()) 
                {
                    case R.id.nostart_task_layout:
                        intent.putExtra(MyTaskFragment.TASK_POSITION_KEY, 0);
                        break;
                    case R.id.starting_task_layout:
                        intent.putExtra(MyTaskFragment.TASK_POSITION_KEY, 1);
                        break;
                    case R.id.delay_task_layout:
                        intent.putExtra(MyTaskFragment.TASK_POSITION_KEY, 2);
                        break;
                    case R.id.today_expire_layout:
                        intent.putExtra(MyTaskFragment.TASK_POSITION_KEY, 3);
                        break;
                    case R.id.week_expire_layout:
                        intent.putExtra(MyTaskFragment.TASK_POSITION_KEY, 4);
                        break;
                    case R.id.month_expire_layout:
                        intent.putExtra(MyTaskFragment.TASK_POSITION_KEY, 5);
                        break;                  
                    default:break;
                }           
            } 
            
            if (viewTag == 2) { // 形象进展
                intent.putStringArrayListExtra("paths", mHelper.getFilePaths());
            }
            intent.setClass(view.getContext(), PortalMoreListActivity.class);
            intent.putExtra(MyTaskFragment.TASK_INFO_KEY, (Serializable) mTaskBigList);            
            mContext.startActivity(intent);
            
        }
        
        @Override
        public void onListViewItemClick(int viewTag, AdapterView<?> parent,
                View view, int position, long id) {
            Message msg = new Message();
            msg.setMessage_id(mHelper.getPortalList(viewTag).get(position).getMessage_id());
            msg.setType(mHelper.getPortalList(viewTag).get(position).getType());
            msg.setType_id(mHelper.getPortalList(viewTag).get(position).getType_id());
            msg.setTime(mHelper.getPortalList(viewTag).get(position).getDate());
            
            if (viewTag == 1) { // 我的任务
                IntentHelper.startActivity(view.getContext(), viewTag, position);
            } else if (viewTag == 4) { // 公司公告
                IntentHelper.startActivity(mContext, viewTag, (Announcement) mHelper.getPortalItem(viewTag, position));
            } else if (viewTag == 5) { // 最新文档
                IntentHelper.openFile(mContext, (Files) mHelper.getPortalItem(viewTag, position));
            } else {
                IntentHelper.startActivity(mContext, viewTag, mHelper.getPortalItem(viewTag, position));
            }  
            
        }
        
        @Override
        public void onImageClick(int viewTag, View view) {
            Intent intent = new Intent(Intent.ACTION_MAIN);
            intent.putExtra("index", viewTag);
            intent.putStringArrayListExtra("paths", mHelper.getFilePaths());
            intent.setClass(view.getContext(), PortalMoreListActivity.class);
            mContext.startActivity(intent);            
        }
        
        @Override
        public void onItemLongClick(View view) {
            // TODO Auto-generated method stub            
        }
        
        @Override
        public void onCheckBoxClick(int viewTag, boolean isChecked) {
            // TODO Auto-generated method stub            
        }

    };
    
    /**
     * 裁剪一张160*160的bitmap，显示在主页门户上
     * @param path
     */
    private void createScaledBitmap(final String path) {
        Bitmap thumbnail = null;
        try {
            BitmapFactory.Options options = new BitmapFactory.Options();
            options.inSampleSize = 10;
            Bitmap bitmap = BitmapFactory.decodeFile(path, options);
            thumbnail = ThumbnailUtils.extractThumbnail(bitmap, 160, 160);
            bitmap.recycle();
        } catch (Exception e) {
            e.printStackTrace();
        }

        if (thumbnail != null) {
            for (int i = 0; i < mModelLists.size(); i++) {
                if (mModelLists.get(i).getIndex() == 2) {
                    mModelLists.get(i).getBitmaps().add(thumbnail);
                    mHandler.sendEmptyMessage(UPDATE_PORTAL_VIEW);
                    break;
                }
            }
        }
    }
    
    /**
     * 
     * 收到【未读消息】广播，重新拉数据，更新门户UI 
     */
    private class MsgBroadcastReceiver extends BroadcastReceiver {

        @Override
        public void onReceive(Context context, Intent intent) {
            int msgFlag = intent.getIntExtra(MessageService.MESSAGE_FLAG, 0);
            if (msgFlag == 0) {
                // 拉所有消息时，不处理。 因为第一次进入系统时，刚拉完过。
                return;
            }
            
            int msgType = intent.getIntExtra(MessageService.MESSAGE_TYPE, 1);
            switch (msgType) {
                case 1:
                case 2:
                case 3:
                case 4:
                case 5:
                case 6:
                case 7:
                case 13:
                case 14:
                case 22:
                case 23:
                case 24:
                case 32: // 我的待办：(1,2,3,4,5,6,7,13,14,22,23,24,32)
                    mHelper.loadData(0);
                    break;
                case 8:
                case 16: // 我的任务：(8,16)
                    mHelper.loadData(1);
                    break;
                case 9:
                case 10:
                case 17:
                case 18:
                case 19:
                case 20:
                case 21: // 最新反馈：(9,10,17,18,19,20,21)
                    mHelper.loadData(1);
                    mHelper.loadData(3);
                    break;
                case 12: // 公告：(12)
                    mHelper.loadData(4);
                    break;
                case 11:
                case 25: 
                case 27:
                case 28:// 最新文档：(11,25,26,27,28,29,30)
                    mHelper.loadData(5);
                    break;
                case 31: // 置顶 (31)
                    mHelper.loadData(2);
                    break;
                default:
                    break;
            }
        }
    }
    
    public void doActivityResult(int requestCode, int resultCode, Intent data) {
        if (requestCode == HomeActivity.MODEL_EDIT_REQUEST_CODE) {
            SharedPreferences prfs = mContext.getSharedPreferences(
                    GLOBAL.SHARED_PREFERENCES_KEY, Context.MODE_PRIVATE);
            String sequence = prfs.getString("portal_model_sequence", "");
            // 默认选择一个
            if (sequence.equals("")) {
                sequence = "0";
            }
            
            if (!mPortalModelSequences.equals(sequence)) {
                mModelLists.clear();
                mPortalModelSequences = sequence;
                String[] strings = mPortalModelSequences.split(",");
                for (int i = 0; i < strings.length; i++) {                    
                    PortalModelBean bean = new PortalModelBean();
                    ArrayList<Bitmap> bitmaps = new ArrayList<Bitmap>();
                    bean.setIndex(Integer.valueOf(strings[i]));
                    bean.setTitle(mModelTitles[Integer.valueOf(strings[i])]);
                    bean.setBitmaps(bitmaps);
                    bean.setDataList(mDatalListMap.get(Integer.valueOf(strings[i])));
                    mModelLists.add(bean);
                }
                mPortalGridAdpater.notifyDataSetChanged();
                mHelper.loadAllData();
            } else {
                return;
            } 
        }
    }
}
