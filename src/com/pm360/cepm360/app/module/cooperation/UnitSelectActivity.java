package com.pm360.cepm360.app.module.cooperation;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.res.TypedArray;
import android.graphics.Color;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.util.TypedValue;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.inputmethod.InputMethodManager;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;

import com.pm360.cepm360.R;
import com.pm360.cepm360.app.cache.UserCache;
import com.pm360.cepm360.app.common.activity.DataManagerInterface;
import com.pm360.cepm360.app.common.adpater.DataListAdapter;
import com.pm360.cepm360.app.common.adpater.DataListAdapter.ViewHolder;
import com.pm360.cepm360.common.GLOBAL;
import com.pm360.cepm360.common.ResultStatus;
import com.pm360.cepm360.common.json.AnalysisManager;
import com.pm360.cepm360.entity.Tenant;
import com.pm360.cepm360.services.common.RemoteCommonService;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@SuppressWarnings("rawtypes")
public class UnitSelectActivity extends Activity implements
		DataListAdapter.ListAdapterInterface {
	public static final String ACTION_KEY = "action";
	public static final String TENANT_KEY = "tenant";
	public static final String PROJECT_KEY = "projectid";
	public static final String TITLE_KEY = "title";

	private Intent mIntent;
	private LinearLayout mSearchLinearView;
	private View mListLayout;
	private ListView mTenantView;
	private DataListAdapter<Tenant> mAdapter;
	// 列表头显示名称
	private String[] mListHeadNames;
	private int[] mDisplayItemIds;

	private int Action;
	private int ProjectId;
	private String mTitle;
	private Tenant mCurrentItem;
	protected long mAttachDismissTime;
	protected static Tenant mTenant = new Tenant();
	/** 标志位定义：
     *  TenantList：	获取系统所有租户信息 ：
     *  AllCooperationTenantList：	获取该公司合作过的所有公司信息
     *  UnCooperationTenantListByProject：	获取基于某个项目未合作的过的公司信息
     *  CooperationTenantListByProject：		获取基于某个项目的协作单位信息
     */
	public static final int TenantList = 100;
	public static final int AllCooperationTenantList = 200;
	public static final int UnCooperationTenantListByProject = 300;
	public static final int CooperationTenantListByProject = 400;

	private EditText mSearchKeyWord;
	private ImageView mClearKeyWord;
	private ImageView mSearchButton;

	@SuppressWarnings("unchecked")
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);

		mIntent = getIntent();
		mTenant.setTenant_id(mIntent.getIntExtra(TENANT_KEY, 0));
		Action = mIntent.getIntExtra(ACTION_KEY, 0);
		ProjectId = mIntent.getIntExtra(PROJECT_KEY, 0);
		mTitle = mIntent.getStringExtra(TITLE_KEY);

		View.OnClickListener onClickListener = new View.OnClickListener() {

			@Override
			public void onClick(View v) {
				switch (v.getId()) {
				case R.id.ok:
					Log.v("sss2", "aaaaa");
					mIntent.putExtra("tenant", mCurrentItem);
					setResult(RESULT_OK, mIntent);
					break;
				case R.id.btn_close:
					Log.v("sss2", "ssss");
					setResult(RESULT_CANCELED, mIntent);
				default:
					break;
				}
				finish();
			}
		};

		setContentView(R.layout.unit_select_activity_fragment);

		// 库存列表头布局
		mListLayout = (LinearLayout) findViewById(R.id.unit_select_activity_listhead);
		// 获取列表使用的相关资源
		mListHeadNames = getResources().getStringArray(
				R.array.unit_select_activity_names);
		TypedArray typedArray = getResources().obtainTypedArray(
				R.array.unit_select_activity_ids);
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
							getResources().getDimensionPixelSize(
									R.dimen.table_title_textsize));
					tv.setTextColor(getResources().getColor(
							R.color.content_listview_header_text_color));
					tv.setText(mListHeadNames[i]);
				}
			}
		}

		TextView title = (TextView) findViewById(R.id.edit_title);
		title.setText(mTitle);

		Button ok = (Button) findViewById(R.id.ok);
		ImageView close = (ImageView) findViewById(R.id.btn_close);

		ok.setOnClickListener(onClickListener);
		close.setOnClickListener(onClickListener);

		typedArray.recycle();

		// 库存列表
		mTenantView = (ListView) findViewById(R.id.unit_select_activity_listview);
		mTenantView.setOnItemClickListener(new OnItemClickListener() {

			@Override
			public void onItemClick(AdapterView<?> adapterView, View view,
					int position, long id) {
				mAdapter.setSelected(position, true);
				mTenantView.setSelection(position);
			}
		});
		mAdapter = new DataListAdapter<Tenant>(getBaseContext(), this);
		mTenantView.setAdapter(mAdapter);

		initSearchLayout();

//		Tenant tenant = new Tenant();
//		tenant.setTenant_id(UserCache.getCurrentUser()
//				.getTenant_id());
//		Log.v("sha2", "ProjectId2" + ProjectId);
		loadData(mTenant);

	}

	private void initSearchLayout() {

		// 妫�绱㈢嚎鎬у竷灞�
		mSearchLinearView = (LinearLayout) findViewById(R.id.search_layout);
		mSearchLinearView.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View view) {
				InputMethodManager imm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
				imm.hideSoftInputFromWindow(mSearchKeyWord.getWindowToken(), 0);
			}
		});

		mSearchKeyWord = (EditText) findViewById(R.id.key_work);

		mSearchKeyWord.addTextChangedListener(new TextWatcher() {

			@Override
			public void beforeTextChanged(CharSequence s, int start, int count,
					int after) {
				if (s.length() > 0) {
					mClearKeyWord.setVisibility(View.VISIBLE);
				}
			}

			@Override
			public void onTextChanged(CharSequence s, int start, int before,
					int count) {

			}

			@Override
			public void afterTextChanged(Editable s) {
				if (s.toString().length() > 0) {
					mClearKeyWord.setVisibility(View.VISIBLE);
					doCooperationSearch();
				} else {
					mClearKeyWord.setVisibility(View.INVISIBLE);
					Tenant tenant = new Tenant();
					tenant.setTenant_id(UserCache
							.getCurrentUser().getTenant_id());
					loadData(tenant);
				}
			}
		});
		mSearchKeyWord
				.setOnFocusChangeListener(new View.OnFocusChangeListener() {

					@Override
					public void onFocusChange(View view, boolean hasFocus) {
						if (hasFocus) {
							if (mSearchKeyWord.getText().toString().length() > 0) {
								mClearKeyWord.setVisibility(View.VISIBLE);
							}
						}
					}
				});

		mClearKeyWord = (ImageView) findViewById(R.id.clear_input);
		mClearKeyWord.setOnClickListener(new View.OnClickListener() {

			@Override
			public void onClick(View view) {

				mSearchKeyWord.setText("");
				Tenant tenant = new Tenant();
				tenant.setTenant_id(UserCache.getCurrentUser()
						.getTenant_id());
				loadData(tenant);
			}
		});

		mSearchButton = (ImageView) findViewById(R.id.search_icon);
		mSearchButton.setOnClickListener(new View.OnClickListener() {

			@Override
			public void onClick(View arg0) {
				Tenant tenant = new Tenant();
				tenant.setName(mSearchKeyWord.getText().toString());
				loadData(tenant);
			}
		});
	}

	private void doCooperationSearch() {
		mAdapter.showSearchResult(1, mSearchKeyWord.getText().toString());
	}

	@Override
	public List<?> findByCondition(Object... condition) {

		List<Tenant> tenants = mAdapter.getDataShowList();
		List<Tenant> tempList = new ArrayList<Tenant>();

		for (int i = 0; i < tenants.size(); i++) {
			// Log.v("sss2", "111Name" +tenants.get(i).getName() + "11i" + i);
			if (tenants.get(i).getName() != null) {
				if (tenants.get(i).getName().contains((String) condition[1])) {
					tempList.add(tenants.get(i));
				}
			} else
				tempList.add(tenants.get(i));
		}

		return tempList;
	}

	private void loadData(Tenant tenant) {
//		RemoteCommonService.getInstance().getCooperationTenantList(mManager, tenant, ProjectId);
		Log.e("sha2", "action= " +Action);
		Log.e("sha2", "ProjectId= " +ProjectId);
		Log.e("sha2", "tenant= " +tenant);
		switch (Action) {
		case TenantList:
			RemoteCommonService.getInstance().getTenantList(mManager, tenant);
			break;
			
		case AllCooperationTenantList:
			RemoteCommonService.getInstance().getAllCooperationTenantList(mManager, tenant);
			break;
			
		case UnCooperationTenantListByProject:
			RemoteCommonService.getInstance().getUNCooperationTenantListByProject(mManager, tenant, ProjectId);
			break;
			
		case CooperationTenantListByProject:
			RemoteCommonService.getInstance().getCooperationTenantListByProject(mManager, tenant, ProjectId);
			break;
			
		default:
			break;
		}
	}

	private DataManagerInterface mManager = new DataManagerInterface() {

		@SuppressWarnings("unchecked")
		@Override
		public void getDataOnResult(ResultStatus status, List<?> list) {
			switch (status.getCode()) {
			case AnalysisManager.SUCCESS_DB_QUERY:
				if (list != null && list.size() > 0)
					mAdapter.setDataList((List<Tenant>) list);
				mAdapter.setShowDataList((List<Tenant>) list);
				break;

			default:
				break;
			}

		}
	};

	@Override
	public int getLayoutId() {
		return R.layout.unit_select_activity_list_item;
	}

	@Override
	public View getHeaderView() {
		return mListLayout;
	}

	@Override
	public void regesterListeners(ViewHolder viewHolder, final int position) {
		for (int i = 0; i < viewHolder.tvs.length; i++) {
			viewHolder.tvs[i].setOnClickListener(new View.OnClickListener() {

				@Override
				public void onClick(View view) {
					mAdapter.setSelected(position, true);
					mCurrentItem = mAdapter.getItem(position);
				}
			});
		}
	}

	@Override
	public void initListViewItem(View convertView, ViewHolder holder,
			DataListAdapter adapter, final int position) {

		Map<String, String> listViewItem = beanToMap(adapter.getItem(position));
		for (int i = 0; i < mListHeadNames.length; i++) {
			if (i == 0) {
				if (Integer.parseInt(listViewItem.get(mListHeadNames[0])) != 0) {
					holder.tvs[i]
							.setText(GLOBAL.ENTERPRISE_TYPE[Integer
									.parseInt(listViewItem
											.get(mListHeadNames[i])) - 1][1]);
				}
			}
			// else if (i == 2) {
			// String userName = UserCache.getUserMaps()
			// .get(listViewItem.get(mListHeadNames[i]));
			// holder.tvs[i].setText(userName == null ? "" : userName);
			// }
			else {
				holder.tvs[i].setText(listViewItem.get(mListHeadNames[i]));
			}

			holder.tvs[i].setOnClickListener(new View.OnClickListener() {

				@Override
				public void onClick(View view) {
					// TODO Auto-generated method stub

					long minus_time = System.currentTimeMillis()
							- mAttachDismissTime;

					if (minus_time < 300)
						return;

					mCurrentItem = mAdapter.getItem(position);

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

		mapItem.put(mListHeadNames[0], ((Tenant) bean).getType() + "");
		mapItem.put(mListHeadNames[1], ((Tenant) bean).getName());
		mapItem.put(mListHeadNames[2], ((Tenant) bean).getKey_person());
		mapItem.put(mListHeadNames[3], ((Tenant) bean).getTel());
		mapItem.put(mListHeadNames[4], ((Tenant) bean).getAddress());

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
	public boolean isSameObject(Object t1, Object t2) {
		// TODO Auto-generated method stub
		return false;
	}

}
