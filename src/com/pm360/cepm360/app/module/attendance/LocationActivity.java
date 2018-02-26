package com.pm360.cepm360.app.module.attendance;

import android.annotation.SuppressLint;
import android.content.res.TypedArray;
import android.graphics.Color;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.util.Log;
import android.util.TypedValue;
import android.view.Gravity;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.RadioGroup.OnCheckedChangeListener;
import android.widget.TextView;
import android.widget.Toast;

import com.baidu.location.BDLocation;
import com.baidu.location.BDLocationListener;
import com.baidu.location.LocationClient;
import com.baidu.location.LocationClientOption;
import com.baidu.mapapi.SDKInitializer;
import com.baidu.mapapi.map.BaiduMap;
import com.baidu.mapapi.map.BitmapDescriptor;
import com.baidu.mapapi.map.MapStatus;
import com.baidu.mapapi.map.MapStatusUpdate;
import com.baidu.mapapi.map.MapStatusUpdateFactory;
import com.baidu.mapapi.map.MapView;
import com.baidu.mapapi.map.MyLocationConfiguration;
import com.baidu.mapapi.map.MyLocationConfiguration.LocationMode;
import com.baidu.mapapi.map.MyLocationData;
import com.baidu.mapapi.model.LatLng;
import com.pm360.cepm360.R;
import com.pm360.cepm360.app.cache.UserCache;
import com.pm360.cepm360.app.common.activity.ActionBarActivity;
import com.pm360.cepm360.app.common.adpater.DataListAdapter;
import com.pm360.cepm360.app.common.adpater.DataListAdapter.ViewHolder;
import com.pm360.cepm360.app.common.view.parent.BaseDialogStyle;
import com.pm360.cepm360.common.util.DateUtils;
import com.pm360.cepm360.entity.P_CGYS;

import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class LocationActivity extends ActionBarActivity {

	// 定位相关
	LocationClient mLocClient;
	public MyLocationListenner myListener = new MyLocationListenner();
	private LocationMode mCurrentMode;
	BitmapDescriptor mCurrentMarker;

	MapView mMapView;
	BaiduMap mBaiduMap;
	TextView mLocateInfoTextView;
	TextView mLongitudeTextView;
	TextView mLatitudeTextView;
	TextView mGobackTimeTextView;
	BaseDialogStyle mLocateInfoDialog;
	BaseDialogStyle mRegistrationInfoDialog;
	View mRegistrationInfoDialogView;
	private View mDialogListLayout;
	private ListView mDialogListView;
	private DataListAdapter<P_CGYS> mDialogAdapter;
	private String[] mDialogListHeadNames;
	private int[] mDialogDisplayItemIds;
	// UI相关
	OnCheckedChangeListener radioButtonListener;
	Button requestLocButton, mapTypeButton;
	boolean isFirstLoc = true;// 是否首次定位

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		SDKInitializer.initialize(getApplicationContext());
		setContentView(R.layout.location);
		requestLocButton = (Button) findViewById(R.id.mapmode);
		mapTypeButton = (Button) findViewById(R.id.map_type);
		mapTypeButton.setOnClickListener(new View.OnClickListener() {
			
			@Override
			public void onClick(View arg0) {
				// 若为卫星地图,则切换成普通地图,否则切换成卫星地图
				if(mBaiduMap.getMapType() == BaiduMap.MAP_TYPE_SATELLITE) {
					mBaiduMap.setMapType(BaiduMap.MAP_TYPE_NORMAL);
					mapTypeButton.setText(getResources().getString(R.string.normal_map));
				} else {
					mBaiduMap.setMapType(BaiduMap.MAP_TYPE_SATELLITE);
					mapTypeButton.setText(getResources().getString(R.string.satellite_map));
				}
			}
		});
		mCurrentMode = LocationMode.NORMAL;
		requestLocButton.setText("普通");
		OnClickListener btnClickListener = new OnClickListener() {
			public void onClick(View v) {
				switch (mCurrentMode) {
				case NORMAL:
					requestLocButton.setText("跟随");
					mCurrentMode = LocationMode.FOLLOWING;
					mBaiduMap
							.setMyLocationConfigeration(new MyLocationConfiguration(
									mCurrentMode, true, mCurrentMarker));
					break;
				case COMPASS:
					requestLocButton.setText("普通");
					mCurrentMode = LocationMode.NORMAL;
					mBaiduMap
							.setMyLocationConfigeration(new MyLocationConfiguration(
									mCurrentMode, true, mCurrentMarker));
					break;
				case FOLLOWING:
					requestLocButton.setText("罗盘");
					mCurrentMode = LocationMode.COMPASS;
					mBaiduMap
							.setMyLocationConfigeration(new MyLocationConfiguration(
									mCurrentMode, true, mCurrentMarker));
					break;
				}
			}
		};
		requestLocButton.setOnClickListener(btnClickListener);
		initLocationApi();
		
		mLocateInfoDialog = new BaseDialogStyle(this);		
		View locateInfoDialogView = mLocateInfoDialog.init(R.layout.locateinfo_dialog);
		mLocateInfoDialog.setParams(0, getResources().getDimensionPixelSize(R.dimen.dp190_h));
		mLocateInfoTextView = (TextView)locateInfoDialogView.findViewById(R.id.locateinfo_addr);
		mLocateInfoDialog.setButton(getString(R.string.confirm), listener);
			
		initRegistrationDialog();
		
		Button mylocationButton = (Button)findViewById(R.id.mylocation);
		Button startWorkButton = (Button)findViewById(R.id.startwork);
		Button goBackHomeButton = (Button)findViewById(R.id.gobackhome);
		Button registrationInfoButton = (Button)findViewById(R.id.registration);
		mylocationButton.setOnClickListener(listener);
		startWorkButton.setOnClickListener(listener);
		goBackHomeButton.setOnClickListener(listener);
		registrationInfoButton.setOnClickListener(listener);
		
		mLongitudeTextView = (TextView)locateInfoDialogView.findViewById(R.id.longitude);
		mLatitudeTextView = (TextView)locateInfoDialogView.findViewById(R.id.latitude);
		mGobackTimeTextView = (TextView)locateInfoDialogView.findViewById(R.id.gobacktime);
	}
	
	public void initRegistrationDialog() {
		mRegistrationInfoDialog = new BaseDialogStyle(this);
		mRegistrationInfoDialogView = mRegistrationInfoDialog.init(R.layout.registration_info_dialog);
		mRegistrationInfoDialog.setTitleName(getString(R.string.registration_log));
		mRegistrationInfoDialog.setButton(getString(R.string.confirm), listener);			
		initDialogListView();
	}	
	
	@SuppressWarnings("unchecked")
	private void initDialogListView() {
		View frame = mRegistrationInfoDialogView.findViewById(R.id.registration_info_frame_id);
		LinearLayout.LayoutParams params = (android.widget.LinearLayout.LayoutParams) frame.getLayoutParams();
		params.leftMargin = 0;
		params.rightMargin = 0;
		frame.setLayoutParams(params);
		// 列表头布局
		mDialogListLayout = mRegistrationInfoDialogView.findViewById(R.id.registration_info_listhead);
		// 获取列表使用的相关资源
		mDialogListHeadNames = getResources().getStringArray(
				R.array.registration_info_dialog_title_names);
		TypedArray typedArray = getResources().obtainTypedArray(
				R.array.registration_info_dialog_list_header_ids);
		if (mDialogListHeadNames != null) {
			mDialogDisplayItemIds = new int[typedArray.length()];
			for (int i = 0; i < mDialogDisplayItemIds.length; i++) {
				mDialogDisplayItemIds[i] = typedArray.getResourceId(i, 0);
				if (i < mDialogListHeadNames.length) {
					// 配置列表头项相关字段
					TextView tv = (TextView) mDialogListLayout
							.findViewById(mDialogDisplayItemIds[i]);
					tv.setTextSize(TypedValue.COMPLEX_UNIT_PX, getResources().getDimension(R.dimen.sp17_s));
					tv.setText(mDialogListHeadNames[i]);
					tv.setGravity(Gravity.CENTER);
					tv.setPadding(0, 0, 0, 0);
				}
			}
		}

		typedArray.recycle();

		//配置Dialog列表的适配器和背景
		mDialogListView = (ListView) mRegistrationInfoDialogView
				.findViewById(R.id.registration_listview);
		mDialogAdapter = new DataListAdapter<P_CGYS>(getBaseContext(),
				mDataAdapterInterface);
		mDialogListView.setAdapter(mDialogAdapter);
		mDialogListView.setBackgroundColor(Color.WHITE);
		
		List<P_CGYS> p_CGYSs = new ArrayList<P_CGYS>();
		P_CGYS p1 = new P_CGYS();
		P_CGYS p2 = new P_CGYS();
		p_CGYSs.add(p1);
		p_CGYSs.add(p2);
		mDialogAdapter.setShowDataList(p_CGYSs);
	}	
	
	@SuppressWarnings("rawtypes")
	private DataListAdapter.ListAdapterInterface mDataAdapterInterface = new DataListAdapter.ListAdapterInterface() {

		@Override
		public int getLayoutId() {
			// TODO Auto-generated method stub
			return R.layout.registration_info_title_list_item;
		}

		@Override
		public View getHeaderView() {
			// TODO Auto-generated method stub
			return mDialogListLayout;
		}

		@Override
		public void regesterListeners(ViewHolder viewHolder, final int position) {
			// TODO Auto-generated method stub
			for (int i = 0; i < viewHolder.tvs.length; i++) {
				viewHolder.tvs[i].setOnClickListener(new View.OnClickListener() {
					@Override
					public void onClick(View view) {
						mDialogAdapter.setSelected(position, true);
	                    mDialogAdapter.getItem(position);
					}
				});
			}			
		}

		@Override
		public void initListViewItem(View convertView, ViewHolder holder,
				DataListAdapter adapter, int position) {
			Map<String, String> listViewItem = dialogBeanToMap(adapter.getItem(position));
			for (int i = 0; i < mDialogListHeadNames.length; i++) {
				holder.tvs[i].setText(listViewItem.get(mDialogListHeadNames[i]));
				holder.tvs[i].setTextColor(Color.BLACK);
			}		

			// 将选中的列表项高亮
			if (mDialogAdapter.getSelectedList().contains((Integer) position)) {
				convertView.setBackgroundResource(R.color.touch_high_light);
			} else {
				convertView.setBackgroundColor(Color.TRANSPARENT);
			}			
		}

		@Override
		public void initLayout(View convertView, ViewHolder holder) {
			holder.tvs = new TextView[mDialogDisplayItemIds.length];
			for (int i = 0; i < mDialogDisplayItemIds.length; i++) {
				holder.tvs[i] = (TextView) convertView
						.findViewById(mDialogDisplayItemIds[i]);
			}			
		}

		@Override
		public List findByCondition(Object... condition) {
			return null;
		}

		@Override
		public boolean isSameObject(Object t1, Object t2) {
			return false;
		}
	};
	
	@SuppressLint("HandlerLeak") 
	public Handler mToastHandler = new Handler() {
		public void handleMessage(Message msg) {
			Toast.makeText(getBaseContext(), (CharSequence) msg.obj,
					Toast.LENGTH_SHORT).show();
		}
	};			
	
	private Map<String, String> dialogBeanToMap(Object bean) {
		Map<String, String> mapItem = new HashMap<String, String>();
		if (bean instanceof P_CGYS) {
			//P_CGYS cgys = (P_CGYS)bean;
			int count = 0;

			mapItem.put(mDialogListHeadNames[count++], "1");
			mapItem.put(mDialogListHeadNames[count++], UserCache.getCurrentUser().getName());
			mapItem.put(mDialogListHeadNames[count++], DateUtils.dateToString(DateUtils.FORMAT_LONG, new Date()));
			mapItem.put(mDialogListHeadNames[count++], "上班");
			mapItem.put(mDialogListHeadNames[count++], "中国江苏省南京市中山北路350号(附近)");
		}
		return mapItem;
	}	
	
	void initLocationApi() {
		// 地图初始化
		mMapView = (MapView) findViewById(R.id.bmapView);
		mBaiduMap = mMapView.getMap();
		// 开启定位图层
		mBaiduMap.setMyLocationEnabled(true);
		// 定位初始化
		mLocClient = new LocationClient(this);
		mLocClient.registerLocationListener(myListener);
		LocationClientOption option = new LocationClientOption();
		option.setOpenGps(true);// 打开gps
		option.setCoorType("bd09ll"); // 设置坐标类型
		option.setIsNeedAddress(true);
		mLocClient.setLocOption(option);
		mLocClient.start();		
	}
	
	OnClickListener listener = new View.OnClickListener() {
		
		@Override
		public void onClick(View arg0) {
			//接口调用
			switch (arg0.getId()) {
				case R.id.save_Button:
					Log.v("chenchen","confirm");
					Toast.makeText(getBaseContext(), "press confirm", Toast.LENGTH_SHORT).show();
					mLocateInfoDialog.dismiss();
					break;
				case R.id.startwork:
				case R.id.gobackhome:
					mGobackTimeTextView.setText(DateUtils.dateToString(DateUtils.FORMAT_LONG, new Date()));
					mLocateInfoDialog.show();
					break;
				case R.id.mylocation:
					LocationClientOption option = new LocationClientOption();
					option.setOpenGps(true);// 打开gps
					option.setCoorType("bd09ll"); // 设置坐标类型
					option.setIsNeedAddress(true);
					mLocClient.setLocOption(option);
					mLocClient.start();					
					break;
				case R.id.registration:
					mRegistrationInfoDialog.show(1000, 0);;
					break;
				default:break;
			}
		}
	};

	/**
	 * 定位SDK监听函数
	 */
	public class MyLocationListenner implements BDLocationListener {

		@Override
		public void onReceiveLocation(BDLocation location) {
			// map view 销毁后不在处理新接收的位置
			if (location == null || mMapView == null)
				return;
			MyLocationData locData = new MyLocationData.Builder()
					.accuracy(location.getRadius())
					// 此处设置开发者获取到的方向信息，顺时针0-360
					.direction(100).latitude(location.getLatitude())
					.longitude(location.getLongitude()).build();
			mBaiduMap.setMyLocationData(locData);
			mBaiduMap.setMapStatus(MapStatusUpdateFactory.newMapStatus(new MapStatus.Builder().zoom(18.5f).build()));
			Log.v("chenchen","location:"+location.getAddrStr());
			mLongitudeTextView.setText(Double.toString(location.getLongitude()));
			mLatitudeTextView.setText(Double.toString(location.getLatitude()));
			mLocateInfoTextView.setText(location.getAddrStr()+"(附近)");
			LatLng ll = new LatLng(location.getLatitude(),
					location.getLongitude());
			MapStatusUpdate u = MapStatusUpdateFactory.newLatLng(ll);
			mBaiduMap.animateMapStatus(u);
		}

		public void onReceivePoi(BDLocation poiLocation) {
		}
	}

	@Override
	protected void onPause() {
		mMapView.onPause();
		super.onPause();
	}

	@Override
	protected void onResume() {
		mMapView.onResume();
		super.onResume();
	}

	@Override
	protected void onDestroy() {
		// 退出时销毁定位
		mLocClient.stop();
		// 关闭定位图层
		mBaiduMap.setMyLocationEnabled(false);
		mMapView.onDestroy();
		mMapView = null;
		super.onDestroy();
	}

}
