/*
 * Copyright (C) 2007 The Android Open Source Project
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.pm360.cepm360.app.common.view.parent;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.DialogInterface.OnClickListener;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.DatePicker;
import android.widget.DatePicker.OnDateChangedListener;
import android.widget.EditText;

import com.pm360.cepm360.R;
import com.pm360.cepm360.common.util.DateUtils;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;

/**
 * A simple dialog containing an {@link android.widget.DatePicker}.
 *
 * <p>
 * See the <a href="{@docRoot}guide/topics/ui/controls/pickers.html">Pickers</a>
 * guide.
 * </p>
 */
public class DoubleDatePickerDialog extends AlertDialog implements OnClickListener, OnDateChangedListener {

	private static final String START_YEAR = "start_year";
	private static final String END_YEAR = "end_year";
	private static final String START_MONTH = "start_month";
	private static final String END_MONTH = "end_month";
	private static final String START_DAY = "start_day";
	private static final String END_DAY = "end_day";

	private final DatePicker mDatePicker_start;
	private final DatePicker mDatePicker_end;
	private final OnDateSetListener mCallBack;

	/**
	 * The callback used to indicate the user is done filling in the date.
	 */
	public interface OnDateSetListener {

		/**
		 * @param view
		 *            The view associated with this listener.
		 * @param year
		 *            The year that was set.
		 * @param monthOfYear
		 *            The month that was set (0-11) for compatibility with
		 *            {@link java.util.Calendar}.
		 * @param dayOfMonth
		 *            The day of the month that was set.
		 */
		void onDateSet(DatePicker startDatePicker, int startYear, int startMonthOfYear, int startDayOfMonth,
				DatePicker endDatePicker, int endYear, int endMonthOfYear, int endDayOfMonth);
	}

	public DoubleDatePickerDialog(Context context, OnDateSetListener callBack, EditText startEt, EditText endEt, EditText durationEt) {
		this(context, THEME_HOLO_LIGHT, callBack, startEt, endEt, durationEt);
	} 

	/**
	 * @param context
	 *            The context the dialog is to run in.
	 * @param theme
	 *            the theme to apply to this dialog
	 * @param callBack
	 *            How the parent is notified that the date is set.
	 * @param year
	 *            The initial year of the dialog.
	 * @param monthOfYear
	 *            The initial month of the dialog.
	 * @param dayOfMonth
	 *            The initial day of the dialog.
	 */
	private EditText mStartEt = null;
	private EditText mEndEt = null;
	private EditText mDurationEt = null;
	protected DoubleDatePickerDialog(Context context, int theme, OnDateSetListener callBack, EditText startEt, EditText endEt, EditText durationEt) {
		super(context, theme);
		mStartDate = new CEPMDate();
		mEndDate = new CEPMDate();
		mStartEt = startEt;
		mEndEt = endEt;
		mDurationEt = durationEt;
		mStartDateCache = new Date();
		mEndDateCache = new Date();
		Calendar cal = Calendar.getInstance();
		int startYear = cal.get(Calendar.YEAR);
		int startMonth = cal.get(Calendar.MONTH);
		int startDay = cal.get(Calendar.DAY_OF_MONTH);
		int endYear = startYear;
		int endMonth = startMonth;
		int endDay = startDay;
		setStartDate(startYear, startMonth, startDay);
		setEndDate(endYear, endMonth, endDay);

		mCallBack = callBack;

		Context themeContext = getContext();
		setButton(BUTTON_POSITIVE, getContext().getString(R.string.confirm), this);
		
		setButton(BUTTON_NEGATIVE, getContext().getString(R.string.cancel), this);
		// setButton(BUTTON_POSITIVE,
		// themeContext.getText(android.R.string.date_time_done), this);
		setIcon(0);

		LayoutInflater inflater = (LayoutInflater) themeContext.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
		View view = inflater.inflate(R.layout.date_picker_dialog, null);
		setView(view);
		mDatePicker_start = (DatePicker) view.findViewById(R.id.datePickerStart);
		mDatePicker_end = (DatePicker) view.findViewById(R.id.datePickerEnd);
		mDatePicker_start.init(startYear, startMonth, startDay, this);
		mDatePicker_end.init(endYear, endMonth, endDay, this);
		((ViewGroup)((ViewGroup)mDatePicker_start.getChildAt(0)).getChildAt(0)).getChildAt(2).setVisibility(View.GONE);
		((ViewGroup)((ViewGroup)mDatePicker_end.getChildAt(0)).getChildAt(0)).getChildAt(2).setVisibility(View.GONE);

	}

	public void onClick(DialogInterface dialog, int which) {
		if (which == BUTTON_POSITIVE)
			tryNotifyDateSet();
	}
	
	private boolean isBigTime(int y1, int m1, int d1, long time) {
		int m = m1 + 1;
		String date_str = y1 + "-" + m + "-" + d1;
		long timestamp = 0;
		try {  
            SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");  
            timestamp = (sdf.parse(date_str).getTime());  
        } catch (Exception e) {  
            e.printStackTrace();  
        }
		if (timestamp > time) {
			return true;
		} else {
			return false;
		}
	}

	@Override
	public void onDateChanged(DatePicker view, int year, int month, int day) {
		if (view.getId() == R.id.datePickerStart) {
			mDatePicker_start.init(year, month, day, this);
			setStartDate(year, month, day);
			if (isBigTime(year, month, day, mEndDateCache.getTime())) {
				mDatePicker_end.init(year, month, day, this);
				setEndDate(year, month, day);
			}
		}
			
		if (view.getId() == R.id.datePickerEnd) {
			mDatePicker_end.init(year, month, day, this);
			setEndDate(year, month, day);
			if (!isBigTime(year, month, day, mStartDateCache.getTime())) {
				mDatePicker_start.init(year, month, day, this);
				setStartDate(year, month, day);
			}
		}
	}

	public DatePicker getDatePickerStart() {
		return mDatePicker_start;
	}

	public DatePicker getDatePickerEnd() {
		return mDatePicker_end;
	}

	/**
	 * Sets the start date.
	 *
	 * @param year
	 *            The date year.
	 * @param monthOfYear
	 *            The date month.
	 * @param dayOfMonth
	 *            The date day of month.
	 */
	public void updateStartDate(int year, int monthOfYear, int dayOfMonth) {
		mDatePicker_start.init(year, monthOfYear, dayOfMonth, this);
//		mDatePicker_start.updateDate(year, monthOfYear, dayOfMonth);
	}

	/**
	 * Sets the end date.
	 *
	 * @param year
	 *            The date year.
	 * @param monthOfYear
	 *            The date month.
	 * @param dayOfMonth
	 *            The date day of month.
	 */
	public void updateEndDate(int year, int monthOfYear, int dayOfMonth) {
		mDatePicker_end.init(year, monthOfYear, dayOfMonth, this);
//		mDatePicker_end.updateDate(year, monthOfYear, dayOfMonth);
	}
	
	private class CEPMDate {
		int year;
		int month;
		int day;

		public int getYear() {
			return year;
		}

		public void setYear(int year) {
			this.year = year;
		}

		public int getMonth() {
			return month;
		}

		public void setMonth(int month) {
			this.month = month;
		}

		public int getDay() {
			return day;
		}

		public void setDay(int day) {
			this.day = day;
		}

		@Override
		public String toString() {
			return "CEPMDate [year=" + year + ", month=" + month + ", day="
					+ day + "]";
		}
	}

	private CEPMDate mStartDate;
	private CEPMDate mEndDate;

	private void tryNotifyDateSet() {
		if (mCallBack != null) {
			mDatePicker_start.clearFocus();
			mDatePicker_end.clearFocus();
			mCallBack.onDateSet(mDatePicker_start, mDatePicker_start.getYear(), mDatePicker_start.getMonth(),
					mDatePicker_start.getDayOfMonth(), mDatePicker_end, mDatePicker_end.getYear(),
					mDatePicker_end.getMonth(), mDatePicker_end.getDayOfMonth());
		} 
		{
			mStartDate.setYear(mDatePicker_start.getYear());
			mStartDate.setMonth(mDatePicker_start.getMonth());
			mStartDate.setDay(mDatePicker_start.getDayOfMonth());
			mEndDate.setYear(mDatePicker_end.getYear());
			mEndDate.setMonth(mDatePicker_end.getMonth());
			mEndDate.setDay(mDatePicker_end.getDayOfMonth())  ;
			String startDate = String.format("%d-%d-%d", mDatePicker_start.getYear(),
					mDatePicker_start.getMonth() + 1, mDatePicker_start.getDayOfMonth());
			String endDate = String.format("%d-%d-%d", mDatePicker_end.getYear(), mDatePicker_end.getMonth() + 1, mDatePicker_end.getDayOfMonth());
			mStartEt.setText(startDate);
			mEndEt.setText(endDate);
			Date startTime = DateUtils.stringToDate(
					DateUtils.FORMAT_SHORT, mStartEt.getText()
							.toString());
			Date endTime = DateUtils.stringToDate(
					DateUtils.FORMAT_SHORT, mEndEt.getText()
							.toString());
			String duration = DateUtils.calculateDuration(startTime, endTime)
					+ getContext().getString(R.string.plan_day);
			if (mDurationEt != null)
				mDurationEt.setText(duration);
		}
	}
	
	public void setDateLimit(Date startDate, Date endDate) {
		mDatePicker_start.setMinDate(startDate.getTime());
		mDatePicker_start.setMaxDate(endDate.getTime());
		mDatePicker_end.setMinDate(startDate.getTime());
		mDatePicker_end.setMaxDate(endDate.getTime());
	}
	
	public void clearDateLimit() {
		long maxDate = Long.parseLong("4133865600000");
		mDatePicker_start.setMinDate(0);
		mDatePicker_start.setMaxDate(maxDate);
		mDatePicker_end.setMinDate(0);
		mDatePicker_end.setMaxDate(maxDate);
	}
	
	@SuppressWarnings("deprecation")
	public void show(Date startDate, Date endDate) {
		super.show();
		if (startDate != null) {
			mStartDate.setYear(startDate.getYear() + 1900);
			mStartDate.setMonth(startDate.getMonth());
			mStartDate.setDay(startDate.getDate());
			
		} else {
			Calendar cal = Calendar.getInstance();
			mStartDate.setYear(cal.get(Calendar.YEAR));
			mStartDate.setMonth(cal.get(Calendar.MONTH));
			mStartDate.setDay(cal.get(Calendar.DAY_OF_MONTH));
		}
		updateStartDate(mStartDate.getYear(),
				mStartDate.getMonth(), mStartDate.getDay());
		
		if (endDate != null) {
			mEndDate.setYear(endDate.getYear() + 1900);
			mEndDate.setMonth(endDate.getMonth());
			mEndDate.setDay(endDate.getDate());
		} else {
			Calendar cal = Calendar.getInstance();
			mEndDate.setYear(cal.get(Calendar.YEAR));
			mEndDate.setMonth(cal.get(Calendar.MONTH));
			mEndDate.setDay(cal.get(Calendar.DAY_OF_MONTH));
		}

		updateEndDate(mEndDate.getYear(),
					mEndDate.getMonth(), mEndDate.getDay());
	}

	@Override
	protected void onStop() {
		// tryNotifyDateSet();
		super.onStop();
	}

	@Override
	public Bundle onSaveInstanceState() {
		Bundle state = super.onSaveInstanceState();
		state.putInt(START_YEAR, mDatePicker_start.getYear());
		state.putInt(START_MONTH, mDatePicker_start.getMonth());
		state.putInt(START_DAY, mDatePicker_start.getDayOfMonth());
		state.putInt(END_YEAR, mDatePicker_end.getYear());
		state.putInt(END_MONTH, mDatePicker_end.getMonth());
		state.putInt(END_DAY, mDatePicker_end.getDayOfMonth());
		return state;
	}

	@Override
	public void onRestoreInstanceState(Bundle savedInstanceState) {
		super.onRestoreInstanceState(savedInstanceState);
		int start_year = savedInstanceState.getInt(START_YEAR);
		int start_month = savedInstanceState.getInt(START_MONTH);
		int start_day = savedInstanceState.getInt(START_DAY);
		mDatePicker_start.init(start_year, start_month, start_day, this);

		int end_year = savedInstanceState.getInt(END_YEAR);
		int end_month = savedInstanceState.getInt(END_MONTH);
		int end_day = savedInstanceState.getInt(END_DAY);
		mDatePicker_end.init(end_year, end_month, end_day, this);

	}
	
	private Date mStartDateCache, mEndDateCache;
	private void setStartDate(int year, int month, int day) {
		Calendar cal = Calendar.getInstance();
		cal.set(Calendar.YEAR, year);
		cal.set(Calendar.MONTH, month);
		cal.set(Calendar.DAY_OF_MONTH, day);
		mStartDateCache.setTime(cal.getTimeInMillis());
	}
	
	private void setEndDate(int year, int month, int day) {
		Calendar cal = Calendar.getInstance();
		cal.set(Calendar.YEAR, year);
		cal.set(Calendar.MONTH, month);
		cal.set(Calendar.DAY_OF_MONTH, day);
		mEndDateCache.setTime(cal.getTimeInMillis());
	}
}
