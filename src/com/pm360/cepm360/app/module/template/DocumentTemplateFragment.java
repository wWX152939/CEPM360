package com.pm360.cepm360.app.module.template;

import android.annotation.SuppressLint;
import android.app.ProgressDialog;
import android.content.Intent;
import android.content.res.TypedArray;
import android.graphics.Color;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.util.TypedValue;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.Button;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.pm360.cepm360.R;
import com.pm360.cepm360.app.cache.PermissionCache;
import com.pm360.cepm360.app.cache.UserCache;
import com.pm360.cepm360.app.common.activity.DataManagerInterface;
import com.pm360.cepm360.app.common.adpater.DataListAdapter;
import com.pm360.cepm360.app.common.adpater.DataListAdapter.ViewHolder;
import com.pm360.cepm360.app.common.view.FloatingMenuView;
import com.pm360.cepm360.app.common.view.OptionsMenuView;
import com.pm360.cepm360.app.common.view.parent.BaseDialog;
import com.pm360.cepm360.app.common.view.parent.BaseToast;
import com.pm360.cepm360.app.utils.UtilTools;
import com.pm360.cepm360.common.GLOBAL;
import com.pm360.cepm360.common.ResultStatus;
import com.pm360.cepm360.common.json.AnalysisManager;
import com.pm360.cepm360.entity.Templet_document;
import com.pm360.cepm360.services.templet.RemoteDocumentService;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@SuppressWarnings("rawtypes")
public class DocumentTemplateFragment extends Fragment implements
        DataListAdapter.ListAdapterInterface {
    private static final String TEMPLATE_MODIFY_PERMISSION = "10_1";
    private static final String TEMPLATE_CHECK_PERMISSION = "10_2";
    public static final int PURCHASE_ADD_FLAG = 100;
    public static final int PURCHASE_UPDATA_FLAG = 101;
    public static final int PURCHASE_COMFIRM_FLAG = 102;
    private ProgressDialog mProgressDialog;
    private View mRootView;
    private View mListLayout;

    private ListView mTemplet_DocumentListView;

    private DataListAdapter<Templet_document> mAdapter;

    // 列表头显示名称
    private String[] mListHeadNames;
    private int[] mDisplayItemIds;
    private BaseDialog mAddDocumentDirDialog;
    protected String[] mDialogNames;
    private Templet_document mCurrentItem;
    private OptionsMenuView mOptionsMenuView;

    protected long mAttachDismissTime;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
            Bundle savedInstanceState) {

        super.onCreateView(inflater, container, savedInstanceState);
        if (!(PermissionCache.hasSysPermission(
                TEMPLATE_MODIFY_PERMISSION) || PermissionCache
                .hasSysPermission(TEMPLATE_CHECK_PERMISSION))) {
            mRootView = inflater.inflate(
                    R.layout.no_permissions_content_layout, container, false);
        } else {
            // 初始化基本布局变量
            initBasicLayout(inflater, container);

            // 初始化库存列表布局
            initManagerListView();

            // 加载数据
            loadData();

            mAddDocumentDirDialog = new BaseDialog(getActivity(),
                    getResources().getString(
                            R.string.document_template_dialog_title));
            // 初始化dialog
            initDocumentCommonWindow();

            if (PermissionCache.hasSysPermission(
                    TEMPLATE_MODIFY_PERMISSION)) {
                mOptionsMenuView = createOptionsMenuView(new String[] {
                        getResources().getString(R.string.purchase_details),
                        getResources().getString(R.string.purchase_modify),
                        getResources().getString(R.string.purchase_delete) });
            } else {
                mOptionsMenuView = createOptionsMenuView(new String[] { getResources()
                        .getString(R.string.purchase_details) });
            }
        }

        return mRootView;
    }

    @SuppressLint("UseSparseArrays")
    protected void initDocumentCommonWindow() {
        Map<Integer, Integer> buttons = new HashMap<Integer, Integer>();
        buttons.put(1, BaseDialog.spinnerLineStyle);
        Map<Integer, String[]> widgetContent = new HashMap<Integer, String[]>();
        widgetContent.put(
                1,
                getResources().getStringArray(
                        R.array.plan_template_spinner_type));
        mAddDocumentDirDialog.init(R.array.document_template_dialog, buttons,
                widgetContent);

        Button saveImageView = (Button) mAddDocumentDirDialog.getPopupView()
                .findViewById(R.id.save_Button);

        saveImageView.setOnClickListener(new OnClickListener() {

            @Override
            public void onClick(View arg0) {
                String[] names = getActivity().getResources().getStringArray(
                        R.array.document_template_dialog);
                String[] typenames = getActivity().getResources()
                        .getStringArray(R.array.plan_template_spinner_type);

                Map<String, String> saveData = mAddDocumentDirDialog.SaveData();
                String temptypeString = saveData.get(names[1]);
                Templet_document templet_Document = new Templet_document();
                for (int i = 0; i < typenames.length; i++) {
                    if (temptypeString.equals(typenames[i])) {
                        templet_Document.setType(i + 1);
                    }
                }
                templet_Document.setName(saveData.get(names[0]));
                templet_Document.setTenant_id(UserCache
                        .getCurrentUser().getTenant_id());

                RemoteDocumentService.getInstance().addDocTmp(mManager,
                        templet_Document);
                mAddDocumentDirDialog.dismiss();
            }
        });
    }

    private void showProgressDialog(String text) {
        if (mProgressDialog != null && mProgressDialog.isShowing())
            mProgressDialog.dismiss();
        mProgressDialog = UtilTools.showProgressDialog(getActivity(), true,
                true);
    }

    private void dismissProgressDialog() {
        if (mProgressDialog != null && mProgressDialog.isShowing())
            mProgressDialog.dismiss();
    }

    private void loadData() {
        showProgressDialog("loading documentTemplateList...");
        // RemoteDocumentService.getInstance().getDocTmpList(mManager,
        // UserCache.getCurrentUser().getTenant_id(), 0);
    }

    private OptionsMenuView createOptionsMenuView(String[] subMenuNames) {
        OptionsMenuView optionsMenus = new OptionsMenuView(getActivity(),
                subMenuNames);
        optionsMenus.setSubMenuListener(new OptionsMenuView.SubMenuListener() {
            @Override
            public void onSubMenuClick(View view) {
                mAttachDismissTime = System.currentTimeMillis();
                switch ((Integer) view.getTag()) {
                case 0:
                    detailTicket();
                    break;
                case 1:
                    modifyTicket();
                    break;
                case 2:
                    deleteTicket();
                    break;
                }
                mOptionsMenuView.dismiss();
            }
        });
        return optionsMenus;
    }

    protected void deleteTicket() {
        UtilTools.deleteConfirm(getActivity(),
                new UtilTools.DeleteConfirmInterface() {

                    @Override
                    public void deleteConfirmCallback() {
                        showProgressDialog("delete documenTemplate...");
                        RemoteDocumentService.getInstance()
                                .deleteDocTmp(mManager,
                                        mCurrentItem.getTemplet_document_id());
                        loadData();
                    }
                });
    }

    protected void modifyTicket() {

        Intent intent1 = new Intent(getActivity(),
                DocumentTemplateAddActivity.class);
        Bundle bundle = new Bundle();
        bundle.putSerializable("templet_document_modify", mCurrentItem);
        intent1.putExtras(bundle);
        startActivityForResult(intent1, 0);
    }

    protected void detailTicket() {

        Intent intent1 = new Intent(getActivity(),
                DocumentTemplateAddActivity.class);
        Bundle bundle = new Bundle();
        bundle.putSerializable("templet_document_detail", mCurrentItem);
        intent1.putExtras(bundle);
        startActivityForResult(intent1, 0);
    }

    private DataManagerInterface mManager = new DataManagerInterface() {

        @SuppressWarnings("unchecked")
        @Override
        public void getDataOnResult(ResultStatus status, List<?> list) {
            dismissProgressDialog();
            if (status.getCode() != AnalysisManager.SUCCESS_DB_QUERY) {
                Message msg = new Message();
                msg.obj = status.getMessage();
                mToastHandler.sendMessage(msg);
            }
            switch (status.getCode()) {
            case AnalysisManager.SUCCESS_DB_QUERY:
                if (list != null && list.size() > 0)
                    mAdapter.setShowDataList((List<Templet_document>) list);
                break;

            case AnalysisManager.SUCCESS_DB_DEL:
                mAdapter.deleteData(mCurrentItem);
                break;

            case AnalysisManager.SUCCESS_DB_UPDATE:
                loadData();
                break;

            case AnalysisManager.SUCCESS_DB_ADD:
                loadData();
                break;

            default:
                break;
            }
        }
    };

    @SuppressLint("HandlerLeak")
    public Handler mToastHandler = new Handler() {
        public void handleMessage(Message msg) {
            Toast.makeText(getActivity(), (CharSequence) msg.obj,
                    Toast.LENGTH_SHORT).show();
        }
    };

    @SuppressWarnings("unchecked")
    private void initManagerListView() {
        // 库存列表头布局
        mListLayout = mRootView.findViewById(R.id.plan_template_listhead);
        // 获取列表使用的相关资源
        mListHeadNames = getResources().getStringArray(
                R.array.document_template_names);
        TypedArray typedArray = getResources().obtainTypedArray(
                R.array.plantemplate_ids);
        Log.i("sha", "list " + mListLayout);
        if (mListHeadNames != null) {
            mDisplayItemIds = new int[typedArray.length()];
            for (int i = 0; i < mDisplayItemIds.length; i++) {
                mDisplayItemIds[i] = typedArray.getResourceId(i, 0);
                if (i < mListHeadNames.length) {
                    // 配置列表头项相关字段
                    TextView tv = (TextView) mListLayout
                            .findViewById(mDisplayItemIds[i]);

                    tv.setTextSize(
                            TypedValue.COMPLEX_UNIT_PX,
                            getActivity().getResources().getDimensionPixelSize(
                                    R.dimen.sp17_s));
                    tv.setTextColor(Color.BLACK);
                    tv.setText(mListHeadNames[i]);
                }
            }
        }
        if (PermissionCache.hasSysPermission(
                TEMPLATE_MODIFY_PERMISSION)) {
            final FloatingMenuView floatingMenuView = (FloatingMenuView) mRootView
                    .findViewById(R.id.floating_menu);
            floatingMenuView.addPopItem(getResources().getString(R.string.add),
                    R.drawable.icn_add);
            floatingMenuView
                    .setPopOnItemClickListener(new AdapterView.OnItemClickListener() {

                        @Override
                        public void onItemClick(AdapterView<?> parent,
                                View view, int position, long id) {
                            if (PermissionCache.hasSysPermission(
                                    TEMPLATE_MODIFY_PERMISSION)) {
                                mAddDocumentDirDialog.show(null);
                                floatingMenuView.dismiss();
                            } else {
                                BaseToast.show(getActivity(),
                                        BaseToast.NO_PERMISSION);
                            }
                        }

                    });
        }

        typedArray.recycle();

        // 库存列表
        mTemplet_DocumentListView = (ListView) mRootView
                .findViewById(R.id.plan_template_listview);
        mTemplet_DocumentListView
                .setOnItemClickListener(new OnItemClickListener() {

                    @Override
                    public void onItemClick(AdapterView<?> adapterView,
                            View view, int position, long id) {
                        mAdapter.setSelected(position, true);
                        mTemplet_DocumentListView.setSelection(position);
                    }
                });
        mAdapter = new DataListAdapter<Templet_document>(getActivity(), this);
        mTemplet_DocumentListView.setAdapter(mAdapter);

    }

    private void initBasicLayout(LayoutInflater inflater, ViewGroup container) {
        // 获取跟布局
        mRootView = inflater.inflate(R.layout.plan_template_fragment,
                container, false);

    }

    @Override
    public int getLayoutId() {
        return R.layout.plan_template_list_item;
    }

    @Override
    public View getHeaderView() {
        return mListLayout;
    }

    @Override
    public void regesterListeners(ViewHolder viewHolder, final int position) {
    }

    @Override
    public void initListViewItem(View convertView, ViewHolder holder,
            DataListAdapter adapter, final int position) {

        Map<String, String> listViewItem = beanToMap(adapter.getItem(position));
        Log.v("chen", "initListViewItem =" + listViewItem);
        for (int i = 0; i < mListHeadNames.length; i++) {
            if (i == 0) {
                holder.tvs[0].setText(position + 1 + "");
            } else if (i == 2) {
                holder.tvs[i].setText(GLOBAL.DOC_TYPE[Integer
                        .parseInt(listViewItem.get(mListHeadNames[i])) - 1][1]);
            } else {
                holder.tvs[i].setText(listViewItem.get(mListHeadNames[i]));
            }
            holder.tvs[i].setClickable(false);
            holder.tvs[i].setOnClickListener(new View.OnClickListener() {

                @Override
                public void onClick(View view) {
                    long minus_time = System.currentTimeMillis()
                            - mAttachDismissTime;

                    if (minus_time < 300)
                        return;

                    mCurrentItem = mAdapter.getItem(position);
                    mOptionsMenuView.showAsDropDown(
                            view,
                            0,
                            (-view.getMeasuredHeight() - UtilTools.dp2pxH(
                                    view.getContext(), 40)));

                    mAdapter.setSelected(position, true);
                }
            });
        }

        // 将选中的列表项高亮
        if (mAdapter.getSelectedList().contains((Integer) position)) {
            convertView.setBackgroundResource(R.color.touch_high_light);
        } else {
            convertView.setBackgroundColor(Color.TRANSPARENT);
        }
    }

    @SuppressLint("SimpleDateFormat")
    private Map<String, String> beanToMap(Object bean) {
        Map<String, String> mapItem = new HashMap<String, String>();

        mapItem.put(mListHeadNames[0],
                ((Templet_document) bean).getTemplet_document_id() + "");
        mapItem.put(mListHeadNames[1], ((Templet_document) bean).getName());
        mapItem.put(mListHeadNames[2], ((Templet_document) bean).getType() + "");

        return mapItem;
    }

    @Override
    public void initLayout(View convertView, ViewHolder holder) {
        holder.tvs = new TextView[mDisplayItemIds.length];
        for (int i = 0; i < mDisplayItemIds.length; i++) {
            holder.tvs[i] = (TextView) convertView
                    .findViewById(mDisplayItemIds[i]);
        }
    }

    @Override
    public List<Templet_document> findByCondition(Object... condition) {
        return null;
    }

    @Override
    public boolean isSameObject(Object t1, Object t2) {
        return false;
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        loadData();
    }
}
