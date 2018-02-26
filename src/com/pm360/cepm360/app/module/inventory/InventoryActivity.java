package com.pm360.cepm360.app.module.inventory;

import android.app.ProgressDialog;
import android.os.Bundle;
import android.support.v4.app.Fragment;

import com.pm360.cepm360.R;
import com.pm360.cepm360.app.common.view.parent.BaseSlidingPaneActivity;
import com.pm360.cepm360.app.utils.UtilTools;

public class InventoryActivity extends BaseSlidingPaneActivity {

    private ProgressDialog mProgressDialog;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
		init(mFragmentManagerInterface, true);

    	super.onCreate(savedInstanceState);
    }
    
	private FragmentManagerInterface mFragmentManagerInterface = new FragmentManagerInterface() {

		@Override
		public Class<? extends Fragment>[] getManagerFragments() {
			@SuppressWarnings("unchecked")
			Class<? extends Fragment>[] fragmentClass = new Class[3];
			fragmentClass[0] = InventoryStoreHouseFragment.class; // 库存
			fragmentClass[1] = InventoryStockInFragment.class;    // 入库
			fragmentClass[2] = InventoryStockOutFragment.class;   // 出库
			return fragmentClass;
		}

		@Override
		public Class<?>[] getSearchObjectClasses() {
			return null;
		}

		@Override
		public String getHomeTitleName() {
			return "";
		}

		@Override
		public int getNavigationTitleNamesId() {
			return R.array.inventory_navigation_titiles;
		}

		@Override
		public int getNavigationIconsId() {
			return R.array.inventory_navigation_icons;
		}
		
	};    
    
    public void showProgressDialog(String text) {
        dismissProgressDialog();
        mProgressDialog = UtilTools.showProgressDialog(InventoryActivity.this, true, false);
    }

    public void dismissProgressDialog() {
        if (mProgressDialog != null && mProgressDialog.isShowing())
            mProgressDialog.dismiss();
    }

}