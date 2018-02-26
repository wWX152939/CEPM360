package com.pm360.cepm360.app.common.adpater;
import android.view.View;
import android.widget.AbsListView;
import android.widget.AbsListView.OnScrollListener;
import android.widget.ListView;

public class ListScrollSyncListener implements OnScrollListener {

		private ListView mListView;
		private int mFirstVisibleItem;
		private int mTop;

		public ListScrollSyncListener(ListView listView) {
			mListView = listView;
		}

		@Override
		public void onScroll(AbsListView view, int firstVisibleItem,
				int visibleItemCount, int totalItemCount) {

			View subView = view.getChildAt(0);
			if (subView != null) {
				final int top = subView.getTop();
//				if (DEBUG)
//					Log.i("dog2", "view:" + view + "otherview:" + mListView
//							+ "firstV:" + firstVisibleItem + "top:" + top);
				if (mFirstVisibleItem != firstVisibleItem || mTop != top) {
					mListView.setSelectionFromTop(firstVisibleItem, top);
					mFirstVisibleItem = firstVisibleItem;
					mTop = top;
				}

			}
		}

		@Override
		public void onScrollStateChanged(AbsListView view, int scrollState) {
			// TODO Auto-generated method stub
		}

	}