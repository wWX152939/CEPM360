package com.pm360.cepm360.app.module.purchase;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.WindowManager;
import android.widget.LinearLayout;

import com.pm360.cepm360.R;
import com.pm360.cepm360.app.common.activity.ActionBarActivity;
import com.pm360.cepm360.app.utils.UtilTools;

public class TicketActivity2 extends ActionBarActivity {

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,
				WindowManager.LayoutParams.FLAG_FULLSCREEN);

		setContentView(R.layout.base_ticket_activity2);
		getMenuView().setVisibility(View.GONE);

	}

	public View initWindows(View[] views, boolean totalFlag) {
		LinearLayout parentLayout = (LinearLayout) findViewById(R.id.parent_id);
		int px = UtilTools.dp2pxH(getBaseContext(), 8);
		views[0].setPadding(0, px, 0, px);
		parentLayout.addView(views[0]);
		parentLayout.addView(views[1]);
		if (totalFlag == true) {
			View itemView = LayoutInflater.from(parentLayout.getContext())
					.inflate(R.layout.base_ticket_activity2_bottom_item,
							parentLayout, false);
			parentLayout.addView(itemView);
		}
		return parentLayout;
	}

}
