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
import android.widget.DatePicker;
import android.widget.DatePicker.OnDateChangedListener;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.pm360.cepm360.R;

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
public class CEPMDatePickerDialog extends AlertDialog implements OnClickListener, OnDateChangedListener {

	private static final String YEAR = "year";
	private static final String MONTH = "month";
	private static final String DAY = "day";

	private final DatePicker mDatePicker;
	private final OnDateSetListener mCallBack;
	private View mRootView;

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
		void onDateSet(DatePicker datePicker, int startYear, int startMonthOfYear, int startDayOfMonth);
	}

	public CEPMDatePickerDialog(Context context, EditText et, String title) {
		this(context, THEME_HOLO_LIGHT, null, et, title);
	}
	
	public CEPMDatePickerDialog(Context context, OnDateSetListener callBack, String title) {
		this(context, THEME_HOLO_LIGHT, callBack, null, title);
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
	protected CEPMDatePickerDialog(Context context, int theme, OnDateSetListener callBack, EditText et, String title) {
		super(context, theme);
		mStartDate = new CEPMDate();
		mStartEt = et;
		Calendar cal = Calendar.getInstance();
		int startYear = cal.get(Calendar.YEAR);
		int startMonth = cal.get(Calendar.MONTH);
		int startDay = cal.get(Calendar.DAY_OF_MONTH);

		mCallBack = callBack;

		Context themeContext = getContext();
		setButton(BUTTON_POSITIVE, getContext().getString(R.string.confirm), this);
		
		setButton(BUTTON_NEGATIVE, getContext().getString(R.string.cancel), this);
		// setButton(BUTTON_POSITIVE,
		// themeContext.getText(android.R.string.date_time_done), this);
		setIcon(0);

		LayoutInflater inflater = (LayoutInflater) themeContext.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
		mRootView = inflater.inflate(R.layout.date_picker_dialog, null);
		initView(title);
		setView(mRootView);
		mDatePicker = (DatePicker) mRootView.findViewById(R.id.datePickerStart);
		mDatePicker.init(startYear, startMonth, startDay, this);
	}
	
	private void initView(String title) {
		ImageView iv = (ImageView) mRootView.findViewById(R.id.iv);
		iv.setVisibility(View.GONE);
		LinearLayout line = (LinearLayout) mRootView.findViewById(R.id.lineEnd);
		line.setVisibility(View.GONE);
		TextView tv = (TextView) mRootView.findViewById(R.id.tvStart);
		tv.setText(title);
	}
	
	public void setTitle(String title) {
		TextView tv = (TextView) mRootView.findViewById(R.id.tvStart);
		tv.setText(title);
	}

	public void onClick(DialogInterface dialog, int which) {
		if (which == BUTTON_POSITIVE)
			tryNotifyDateSet();
	}

	@Override
	public void onDateChanged(DatePicker view, int year, int month, int day) {
		if (view.getId() == R.id.datePickerStart) {
			mDatePicker.init(year, month, day, this);
		}
			
	}

	public DatePicker getDatePickerStart() {
		return mDatePicker;
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
		mDatePicker.init(year, monthOfYear, dayOfMonth, this);
//		mDatePicker.updateDate(year, monthOfYear, dayOfMonth);
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

	private void tryNotifyDateSet() {
		if (mCallBack != null) {
			mDatePicker.clearFocus();
			mCallBack.onDateSet(mDatePicker, mDatePicker.getYear(), mDatePicker.getMonth(),
					mDatePicker.getDayOfMonth());
		} else {
			mStartDate.setYear(mDatePicker.getYear());
			mStartDate.setMonth(mDatePicker.getMonth());
			mStartDate.setDay(mDatePicker.getDayOfMonth());
			String startDate = String.format("%d-%d-%d", mDatePicker.getYear(),
					mDatePicker.getMonth() + 1, mDatePicker.getDayOfMonth());
			mStartEt.setText(startDate);
		}
	}
	
	public void setDateLimit(Date startDate, Date endDate) {
		if (startDate != null) {
			mDatePicker.setMinDate(startDate.getTime());
		}
		if (endDate != null) {
			mDatePicker.setMaxDate(endDate.getTime());
		}
	}
	
	@Override
	public void show() {
		show(null);
	}
	
	public void show(Date startDate) {
		super.show();
		if (startDate != null) {
			Calendar cal = Calendar.getInstance();
			cal.setTime(startDate);
			mStartDate.setYear(cal.get(Calendar.YEAR));
			mStartDate.setMonth(cal.get(Calendar.MONTH));
			mStartDate.setDay(cal.get(Calendar.DAY_OF_MONTH));
			
		} else {
			Calendar cal = Calendar.getInstance();
			mStartDate.setYear(cal.get(Calendar.YEAR));
			mStartDate.setMonth(cal.get(Calendar.MONTH));
			mStartDate.setDay(cal.get(Calendar.DAY_OF_MONTH));
		}
		updateStartDate(mStartDate.getYear(),
				mStartDate.getMonth(), mStartDate.getDay());
	}

	@Override
	protected void onStop() {
		// tryNotifyDateSet();
		super.onStop();
	}

	@Override
	public Bundle onSaveInstanceState() {
		Bundle state = super.onSaveInstanceState();
		state.putInt(YEAR, mDatePicker.getYear());
		state.putInt(MONTH, mDatePicker.getMonth());
		state.putInt(DAY, mDatePicker.getDayOfMonth());
		return state;
	}

	@Override
	public void onRestoreInstanceState(Bundle savedInstanceState) {
		super.onRestoreInstanceState(savedInstanceState);
		int start_year = savedInstanceState.getInt(YEAR);
		int start_month = savedInstanceState.getInt(MONTH);
		int start_day = savedInstanceState.getInt(DAY);
		mDatePicker.init(start_year, start_month, start_day, this);
	}
}
