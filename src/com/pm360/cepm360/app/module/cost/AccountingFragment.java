
package com.pm360.cepm360.app.module.cost;

import android.annotation.SuppressLint;
import android.app.ProgressDialog;
import android.content.Context;
import android.graphics.Color;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.v4.app.Fragment;
import android.util.SparseArray;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.webkit.JavascriptInterface;
import android.webkit.WebSettings.LayoutAlgorithm;
import android.webkit.WebView;
import android.widget.AdapterView;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TabHost;
import android.widget.TabHost.TabSpec;
import android.widget.TextView;
import android.widget.Toast;

import com.pm360.cepm360.R;
import com.pm360.cepm360.app.cache.PermissionCache;
import com.pm360.cepm360.app.cache.ProjectCache;
import com.pm360.cepm360.app.cache.UserCache;
import com.pm360.cepm360.app.common.activity.DataManagerInterface;
import com.pm360.cepm360.app.module.project.table.ListCell;
import com.pm360.cepm360.app.module.project.table.MoneyEditCell;
import com.pm360.cepm360.app.module.project.table.TableAdapter;
import com.pm360.cepm360.app.module.project.table.TableRow;
import com.pm360.cepm360.app.module.project.table.TextCell;
import com.pm360.cepm360.app.utils.UtilTools;
import com.pm360.cepm360.common.ResultStatus;
import com.pm360.cepm360.common.json.AnalysisManager;
import com.pm360.cepm360.entity.P_final_cost;
import com.pm360.cepm360.entity.Project;
import com.pm360.cepm360.entity.User;
import com.pm360.cepm360.services.expenses.RemoteFinalCostService;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;
import java.util.Locale;
import java.util.Random;

public class AccountingFragment extends Fragment {

    //private static final String COST_EDIT_PERMISSIONS = "7_1";
    private static final String COST_VIEW_PERMISSIONS = "7_2";

    private View mRootView, mCheckPermissionView;
    private TableAdapter mTabAdapter;
    private String[] mHeadNames;
    private int[] mArrHeadWidths;
    private Project mProject;
    private boolean mHasViewPermission;//, mHasEditPermission;
    private SparseArray<ArrayList<P_final_cost>> mCostArray = new SparseArray<ArrayList<P_final_cost>>();
    private ProgressDialog mProgressDialog;
    private TabHost mTabHost;
    private View[] mTabs = new View[4];

    String colorData[] = {
            "#4572a7", "#aa4643", "#89a54e", "#80699b", "#3d96ae"
    };

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        if (mRootView == null) {
            mRootView = inflater.inflate(R.layout.cost_accounting_fragment, container,
                    false);
            mCheckPermissionView = mRootView.findViewById(R.id.check_permission);
            initTabs();
        }
        ViewGroup parent = (ViewGroup) mRootView.getParent();
        if (parent != null) {
            parent.removeView(mRootView);
        }
        return mRootView;
    }

    private void initTabs() {
        mTabHost = (TabHost) mRootView.findViewById(R.id.tabhost);
        mTabHost.setup();
        mTabs[0] = mTabHost.findViewById(R.id.tab1);
        mTabs[1] = mTabHost.findViewById(R.id.tab2);
        //mTabs[2] = mTabHost.findViewById(R.id.tab3);
        //mTabs[3] = mTabHost.findViewById(R.id.tab4);
        initDetailTab();
        initAnalysisChart();
        mTabHost.setOnTabChangedListener(new TabHost.OnTabChangeListener() {

            @Override
            public void onTabChanged(String tabId) {
                int currentTab = mTabHost.getCurrentTab();
                initTabContent(currentTab);
            }

        });
        int[] tabIcons = new int[] {
                R.drawable.cost_accounting_detail,
                R.drawable.cost_accounting_chart,
                //R.drawable.cost_accounting_detail,
                //R.drawable.cost_accounting_detail
        };
        String[] tabLabels = getResources().getStringArray(R.array.accounting_detail_tablabels);
        int[] tabIds = new int[] {
                R.id.tab1, R.id.tab2 //, R.id.tab3, R.id.tab4
        };
        for (int i = 0; i < tabLabels.length; i++) {
            TabSpec spec = mTabHost.newTabSpec("" + i);
            spec.setIndicator(createIndicatorView(mTabHost, tabIcons[i], tabLabels[i]));
            spec.setContent(tabIds[i]);
            mTabHost.addTab(spec);
        }
    }

    private void initDetailTab() {
        mTabAdapter = new TableAdapter(mTabs[0].getContext(), mTabs[0],
                R.array.accounting_table_names,
                R.array.accounting_table_widths, Color.BLACK, Color.BLACK, getResources().getColor(
                        R.color.table_line), false, false, false);
        mHeadNames = mTabAdapter.getHeadNames();
        mArrHeadWidths = mTabAdapter.getArrHeadWidths();
        ListView left_list = (ListView) mTabs[0]
                .findViewById(R.id.left_container_listview);
        ListView right_list = (ListView) mTabs[0]
                .findViewById(R.id.right_container_listview);
        AdapterView.OnItemClickListener itemClickListener = new AdapterView.OnItemClickListener() {

            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
            }
        };
        left_list.setOnItemClickListener(itemClickListener);
        right_list.setOnItemClickListener(itemClickListener);
    }

    @SuppressLint("SetJavaScriptEnabled")
    private void initAnalysisChart() {
        WebView mWebView = (WebView) mTabs[1];

        mWebView.getSettings().setJavaScriptEnabled(true);
        mWebView.getSettings().setUseWideViewPort(true);
        mWebView.getSettings().setSupportZoom(true);
        mWebView.getSettings().setBuiltInZoomControls(true);
        mWebView.getSettings().setLoadWithOverviewMode(true);
        mWebView.getSettings().setAppCacheEnabled(false);
        mWebView.getSettings().setLayoutAlgorithm(LayoutAlgorithm.SINGLE_COLUMN);
        mWebView.setLayerType(View.LAYER_TYPE_SOFTWARE, null);
        mWebView.addJavascriptInterface(new DemoJavaScriptInterface(), "cepm");
    }

    public String getRandColorCode() {
        String r, g, b;
        Random random = new Random();
        r = Integer.toHexString(random.nextInt(256)).toUpperCase(Locale.US);
        g = Integer.toHexString(random.nextInt(256)).toUpperCase(Locale.US);
        b = Integer.toHexString(random.nextInt(256)).toUpperCase(Locale.US);

        r = r.length() == 1 ? "0" + r : r;
        g = g.length() == 1 ? "0" + g : g;
        b = b.length() == 1 ? "0" + b : b;

        return "#" + r + g + b;
    }

    final class DemoJavaScriptInterface {

        String title;
        String subtitle;

        DemoJavaScriptInterface() {
        }

        /**
         * This is not called on the UI thread. Post a runnable to invoke
         * loadUrl on the UI thread.
         */
        @JavascriptInterface
        public void clickOnAndroid(String str) {
            Toast.makeText(getActivity().getBaseContext(), str, Toast.LENGTH_SHORT).show();

        }

        @JavascriptInterface
        public String jsontohtml() {
            double sum = 0;
            JSONArray array = new JSONArray();
            ArrayList<TableRow> list = mTabAdapter.getList();
            int size = list.size();
            JSONObject[] maps = new JSONObject[size];

            try {
                for (int i = 0; i < size; i++) {
                    TableRow row = list.get(i);
                    String name = row.getValueAt(0).getCellValue();
                    ListCell.ListItem[] listItem = ((ListCell) row.getValueAt(2)).getCellValues();
                    String total = listItem[listItem.length - 1].getItemCell().getCellValue();
                    double totalNum = UtilTools.backFormatMoney("", total);
                    sum += totalNum;
                    maps[i] = new JSONObject();
                    maps[i].put("name", name);
                    maps[i].put("value", totalNum);
                    maps[i].put("color", i < colorData.length ? colorData[i] : getRandColorCode());
                }

                for (JSONObject json : maps) {
                    double value = json.getDouble("value");
                    if (value != 0) {
                        json.put("value", UtilTools.getPercent(value, sum));
                        array.put(json);
                    }
                }
            } catch (JSONException e) {
                e.printStackTrace();
            }

            return array.toString();
        }
    }

    private void initTabContent(int currentTab) {
        switch (currentTab) {
            case 1:
                WebView mWebView = (WebView) mTabs[1];
                mWebView.requestFocus();
                mWebView.loadUrl("file:///android_asset/zhuzhuang_chart.html");
                break;
        }
    }

    @Override
    public void onResume() {
        super.onResume();
        Project project = ProjectCache.getCurrentProject();
        if (!project.equals(mProject)) {
            mProject = project;
            mHandler.sendEmptyMessageDelayed(0, 300);
        }
        mHasViewPermission = PermissionCache
                .hasSysPermission(COST_VIEW_PERMISSIONS);
        //mHasEditPermission = ((CepmApplication) getActivity().getApplicationContext())
        //        .hasProjectPermission(COST_EDIT_PERMISSIONS);
        mCheckPermissionView.setVisibility(mHasViewPermission ? View.GONE : View.VISIBLE);
    }

    private void loadDate() {
        User user = UserCache.getCurrentUser();
        showProgressDialog("Getting P_final_cost list...");
        RemoteFinalCostService.getInstance().getCostList(new DataManagerInterface() {

            @Override
            public void getDataOnResult(ResultStatus status, List<?> list) {
                dismissProgressDialog();
                if (status.getCode() != AnalysisManager.SUCCESS_DB_QUERY
                        && status.getMessage() != null &&
                        !status.getMessage().equals("")) {
                    UtilTools.showToast(getActivity(), status.getMessage());
                }
                if ((status.getCode() == AnalysisManager.SUCCESS_DB_QUERY)
                        && list != null
                        && list.size() > 0) {
                    mCostArray.clear();
                    for (Object object : list) {
                        if (object instanceof P_final_cost) {
                            addP_final_cost((P_final_cost) object);
                        }
                    }
                    refreshList();
                }

            }
        }, user, mProject);

    }

    Handler mHandler = new Handler(new Handler.Callback() {
        @Override
        public boolean handleMessage(Message msg) {
            loadDate();
            return false;
        }
    });

    private void refreshList() {
        mTabAdapter.clear();
        for (int i = 0; i < mCostArray.size(); i++) {
            int type_id_1 = mCostArray.keyAt(i);
            ArrayList<P_final_cost> cost_list = mCostArray.get(type_id_1);
            int size = cost_list.size();
            ListCell.ListItem[] type_2_list = new ListCell.ListItem[size];
            ListCell.ListItem[] money_list = new ListCell.ListItem[size];
            ListCell.ListItem[] mark_list = new ListCell.ListItem[size];
            for (int j = 0; j < size; j++) {
                P_final_cost cost = cost_list.get(j);
                int type_id_2 = cost.getType_id_2();
                TextCell type_2_cell = new TextCell(cost.getType_2(), mHeadNames[1],
                        mArrHeadWidths[1]);
                type_2_cell.setFakeBoldText(type_id_2 == -1);
                type_2_list[j] = new ListCell.ListItem(type_id_2, type_2_cell);
                MoneyEditCell money_cell = new MoneyEditCell("", cost.getMoney(),
                        mHeadNames[2], mArrHeadWidths[2]);
                money_cell.setGravity(Gravity.RIGHT);
                //money_cell.setEnabled(mHasEditPermission && cost.getFlag() == 0);
                money_cell.setEnabled(mHasViewPermission && cost.getFlag() == 0);                
                money_cell.setFakeBoldText(type_id_2 == -1);
                money_list[j] = new ListCell.ListItem(type_id_2, money_cell);
                TextCell mark_cell = new TextCell(cost.getMark(), mHeadNames[3], mArrHeadWidths[3]);
                mark_list[j] = new ListCell.ListItem(type_id_2, mark_cell);
            }

            List<TextCell> rowValues = new ArrayList<TextCell>();
            TextCell itemCell = new TextCell(cost_list.get(0).getType_1(), mHeadNames[0],
                    mArrHeadWidths[0]);
            ListCell type2ListCell = new ListCell(type_2_list, mHeadNames[1], mArrHeadWidths[1]);
            ListCell moneyListCell = new ListCell(money_list, mHeadNames[2], mArrHeadWidths[2]);
            ListCell markListCell = new ListCell(mark_list, mHeadNames[3], mArrHeadWidths[3]);
            rowValues.add(itemCell);
            rowValues.add(type2ListCell);
            rowValues.add(moneyListCell);
            rowValues.add(markListCell);
            mTabAdapter.addItem(cost_list.get(0).getType_id_1(), rowValues,
                    UtilTools.dp2pxH(getActivity(), size * 37 - 1));
        }
        mTabAdapter.refresh();
    }

    private void addP_final_cost(P_final_cost cost) {
        int type_id_1 = cost.getType_id_1();
        ArrayList<P_final_cost> cost_list = mCostArray.get(cost.getType_id_1());
        if (cost_list == null) {
            cost_list = new ArrayList<P_final_cost>();
            mCostArray.put(cost.getType_id_1(), cost_list);
            P_final_cost total = new P_final_cost();
            total.setType_id_1(type_id_1);
            total.setType_1(cost.getType_1());
            total.setType_id_2(-1);
            total.setType_2(getActivity().getString(R.string.accounting_total));
            total.setMoney(0);
            total.setMark("");
            total.setFlag(1);
            cost_list.add(total);
        }

        if (cost.getType_id_2() == -1) {
            cost_list.set(cost_list.size() - 1, cost);
        } else {
            cost_list.add(cost_list.size() - 1, cost);
            P_final_cost total = cost_list.get(cost_list.size() - 1);
            total.setMoney(total.getMoney() + cost.getMoney());
        }
    }

    private View createIndicatorView(TabHost tabHost, int iconResId, String title) {
        LayoutInflater inflater = (LayoutInflater) tabHost.getContext()
                .getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        View tabIndicator = inflater.inflate(R.layout.accounting_tab_indicator_layout,
                tabHost.getTabWidget(), false);
        ImageView iconView = (ImageView) tabIndicator.findViewById(R.id.icon);
        iconView.setImageResource(iconResId);
        TextView titleView = (TextView) tabIndicator
                .findViewById(R.id.title);
        titleView.setText(title);
        return tabIndicator;
    }

    private void showProgressDialog(String text) {
        dismissProgressDialog();
        mProgressDialog = UtilTools.showProgressDialog(getActivity(), true, false);
    }

    private void dismissProgressDialog() {
        if (mProgressDialog != null && mProgressDialog.isShowing())
            mProgressDialog.dismiss();
    }
}
