
package com.pm360.cepm360.app.common.view;

import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Color;
import android.graphics.drawable.Drawable;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;

import com.pm360.cepm360.R;
import com.pm360.cepm360.app.common.view.parent.CEPMDatePickerDialog;
import com.pm360.cepm360.common.util.DateUtils;

import java.util.Calendar;
import java.util.Date;
import java.util.Locale;

public class DatePickText extends LinearLayout {
    private EditText mEditText;
    private ImageView mButton;
    private Calendar mCalendar = Calendar
            .getInstance(Locale.SIMPLIFIED_CHINESE);
    private CEPMDatePickerDialog mDatePickerDialog;

    public DatePickText(Context context) {
        super(context);
    }

    public DatePickText(Context context, AttributeSet attrs) {
        super(context, attrs);
        TypedArray a = context.obtainStyledAttributes(attrs, R.styleable.CepmView);
        int textColor = a.getColor(R.styleable.CepmView_textColor, Color.WHITE);
        Drawable buttonIcon = a.getDrawable(R.styleable.CepmView_buttonIcon);
        a.recycle();
        LayoutInflater.from(context).inflate(R.layout.date_pick_text, this);
        mEditText = (EditText) findViewById(R.id.date_text);
        mEditText.setTextColor(textColor);
        mEditText.setOnClickListener(listener);
        mButton = (ImageView) findViewById(R.id.date_btn);
        if (buttonIcon != null)
            mButton.setImageDrawable(buttonIcon);
//        mDatePickerDialog = new DatePickerDialog(context,
//                new DatePickerDialog.OnDateSetListener() {
//
//                    @Override
//                    public void onDateSet(DatePicker view, int year,
//                            int monthOfYear, int dayOfMonth) {
//                        mCalendar.set(Calendar.YEAR, year);
//                        mCalendar.set(Calendar.MONTH, monthOfYear);
//                        mCalendar.set(Calendar.DAY_OF_MONTH, dayOfMonth);
//                        setDate(mCalendar.getTime());
//                    }
//                }, mCalendar.get(Calendar.YEAR), mCalendar.get(Calendar.MONTH),
//                mCalendar.get(Calendar.DAY_OF_MONTH));
        mButton.setOnClickListener(listener);
        // setDate(mCalendar.getTime());
        
        CEPMDatePickerDialog.OnDateSetListener dateSetCallBack = new CEPMDatePickerDialog.OnDateSetListener() {

            @Override
            public void onDateSet(DatePicker view, int year, int monthOfYear,
                    int dayOfMonth) {
                mCalendar.set(Calendar.YEAR, year);
                mCalendar.set(Calendar.MONTH, monthOfYear);
                mCalendar.set(Calendar.DAY_OF_MONTH, dayOfMonth);
                setDate(mCalendar.getTime());
            }
        };
        mDatePickerDialog = new CEPMDatePickerDialog(context,
                dateSetCallBack, getResources().getString(R.string.set_time));
    }
    
    View.OnClickListener listener = new View.OnClickListener() {
        
        @Override
        public void onClick(View view) {
            mDatePickerDialog.show();            
        }
    };

    public void setDate(Date date) {
        if (date == null) {
            mEditText.setText("");
            return;
        }
        mCalendar.setTime(date);
//        mDatePickerDialog.getDatePicker().updateDate(mCalendar.get(Calendar.YEAR),
//                mCalendar.get(Calendar.MONTH),
//                mCalendar.get(Calendar.DAY_OF_MONTH));
        String text = DateUtils.dateToString(DateUtils.FORMAT_SHORT, date);
        mEditText.setText(text);
    }

    public Date getDate() {
        if (mEditText.getText().toString().equals(""))
            return null;
        return mCalendar.getTime();
    }

    public void clear() {
        mEditText.setText("");
    }

    public Date getDateSet() {
        if (mEditText.getText().toString().equals("")) {
            return null;
        } else {
            return mCalendar.getTime();
        }
    }

    @Override
    public void setEnabled(boolean enabled) {
        super.setEnabled(enabled);
        mEditText.setEnabled(enabled);
        mButton.setVisibility(enabled ? View.VISIBLE : View.GONE);
    }

    public EditText getEditText() {
        return mEditText;
    }
}
