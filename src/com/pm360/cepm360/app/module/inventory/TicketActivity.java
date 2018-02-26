package com.pm360.cepm360.app.module.inventory;

import android.graphics.Color;
import android.os.Bundle;
import android.util.TypedValue;
import android.view.Gravity;
import android.view.View;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.pm360.cepm360.R;
import com.pm360.cepm360.app.common.activity.ActionBarActivity;
import com.pm360.cepm360.app.utils.UtilTools;

public class TicketActivity extends ActionBarActivity {
    protected LinearLayout mStatisticsLayout;
    protected RelativeLayout mButtonLayout;
    protected ListView mListView;    
    protected LinearLayout mItem1, mItem2, mItem3, mItem4, mItem5, mItem6;
    
    protected TextView mStatistics;
    protected EditText mRemark;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
      getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,
      WindowManager.LayoutParams.FLAG_FULLSCREEN);
        
      setContentView(R.layout.base_ticket_activity);
      
      mListView = (ListView) findViewById(R.id.ticket_list);
      
      mItem1 = (LinearLayout) findViewById(R.id.item1);
      mItem2 = (LinearLayout) findViewById(R.id.item2);
      mItem3 = (LinearLayout) findViewById(R.id.item3);
      mItem4 = (LinearLayout) findViewById(R.id.item4);
      mItem5 = (LinearLayout) findViewById(R.id.item5);
      mItem6 = (LinearLayout) findViewById(R.id.item6);
      
      mStatisticsLayout = (LinearLayout) findViewById(R.id.ticket_statistics_layout);
      mStatistics = (TextView) findViewById(R.id.total_content);
      mRemark = (EditText) findViewById(R.id.remark_content_edit); 
      
      mButtonLayout = (RelativeLayout) findViewById(R.id.ticket_button_layout);
    }
    
    private View getItemByIndex(int index) {
        View view = null;
        if (index == 1) {
            view = mItem1;
        } else if (index == 2) {
            view = mItem2;
        } else if (index == 3) {
            view = mItem3;
        } else if (index == 4) {
            view = mItem4;
        } else if (index == 5) {
            view = mItem5;
        } else if (index == 6) {
            view = mItem6;
        }
        return view;
    }
    
    private void setLabel(View layout, String value) {
        layout.setVisibility(View.VISIBLE);
        ((TextView) layout.findViewById(R.id.label)).setText(value);
    }
    
    protected void addItemLabel(String[] titles, int coulmn) {
        if (coulmn == 3) {
            for (int i = 0; i < titles.length; i++) {
                setLabel(getItemByIndex(i+1), titles[i]);
            }
        } else {
            for (int i = 0; i < titles.length; i++) {
                setLabel(getItemByIndex(i+1), titles[i]);
                if (i == 1) break;
            }
            for (int i = 2; i < titles.length; i++) {
                setLabel(getItemByIndex(i+2), titles[i]);
            }
//            mItem3.setVisibility(View.GONE);
//            mItem6.setVisibility(View.GONE);
        }
        
    }
    
    protected TextView buildTextView(String title) {
        TextView textView = new TextView(this);
        textView.setText(title);
        textView.setTextColor(Color.BLACK);
        textView.setTextSize(TypedValue.COMPLEX_UNIT_PX, getResources().getDimensionPixelSize(R.dimen.sp18_s));
        textView.setPadding(0, 0, 10, 0);
        textView.setGravity(Gravity.CENTER_VERTICAL);
        textView.setWidth(UtilTools.dp2pxW(this, 184));
        textView.setBackgroundResource(R.drawable.dialog_spinner_bg);
        return textView;
    }
    
    protected Button buildButton(String title) {
        Button button = new Button(this);
        button.setText(title);
        button.setTextSize(TypedValue.COMPLEX_UNIT_PX, getResources().getDimensionPixelSize(R.dimen.sp18_s));
        button.setTextColor(Color.BLACK);
        button.setPadding(0, 0, 0, 0);
        button.setBackgroundResource(R.drawable.ticket_button_bg);
        return button;
    }
}
