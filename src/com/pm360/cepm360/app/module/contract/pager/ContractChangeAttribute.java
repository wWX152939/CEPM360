package com.pm360.cepm360.app.module.contract.pager;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Context;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.pm360.cepm360.R;
import com.pm360.cepm360.app.cache.ContactCache;
import com.pm360.cepm360.app.cache.UserCache;
import com.pm360.cepm360.app.common.custinterface.CallBack;
import com.pm360.cepm360.app.common.custinterface.ViewPagersInterface;
import com.pm360.cepm360.app.common.view.parent.BaseWindow;
import com.pm360.cepm360.app.common.view.parent.BaseWindow.WindowInterface;
import com.pm360.cepm360.app.utils.UtilTools;
import com.pm360.cepm360.common.json.AnalysisManager;
import com.pm360.cepm360.common.util.DateUtils;
import com.pm360.cepm360.common.util.MiscUtils;
import com.pm360.cepm360.entity.Contract_change;
import com.pm360.cepm360.entity.Project;
import com.pm360.cepm360.entity.User;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class ContractChangeAttribute implements ViewPagersInterface<Contract_change> {
	
	// 请求码定义
	public static final int USER_SELECT_REQUEST_CODE = 5000;
	public static final int TENANT_SELECT_REQUEST_CODE = 5001;
	public static final int ATTACHMENT_SELECT_REQUEST_CODE = 5002;
	public static final int CONTRACT_SELECT_REQUEST_CODE = 5003;
	
	// 该类索引，标识该类的回调调用
	public static final int CONTRACT_CHANGE_ATTRIBUTE_INDEX = 5200;
	
	// 根布局
	private View mRootView;
	private ViewGroup mSummaryLayout;
	private ViewGroup mChangeLayout;
	
	private Context mContext;
	private BaseWindow mSummaryBaseWindow;
	private BaseWindow mChangeBaseWindow;
	
	private List<Contract_change> mChanges;
	private Contract_change mContractChange;

	// 接收方联系人
	private User mSenderUser;
	private User mRecieverUser;
	
	/**
	 * 构造函数
	 * @param context
	 * @param changes
	 */
	public ContractChangeAttribute(Context context, 
			Contract_change contractChange, List<Contract_change> changes) {
		mContext = context;
		mContractChange = contractChange;
		mChanges = changes;
		
		// 初始化布局
		initLayout();
		
		// 初始化常用信息
		initCommonLayout();
	}
	
	/**
	 * 初始化布局
	 */
	private void initLayout() {
		mRootView = LayoutInflater.from(mContext).inflate(R.layout.contract_change_attribute, null);
		mSummaryLayout = (ViewGroup) mRootView.findViewById(R.id.summary_layout);
		mChangeLayout = (ViewGroup) mRootView.findViewById(R.id.contract_change_layout);
	}
	
	/**
	 * 初始化常用页
	 */
	@SuppressLint("UseSparseArrays") 
	private void initCommonLayout() {
		WindowInterface windowInterface = new WindowInterface() {
			
			@Override
			public int getWidgetWidthMargin() {
				return UtilTools.dp2pxW(mContext, 80);
			}
			
			@Override
			public int getWidgetHeightMargin() {
				return 30;
			}
			
			@Override
			public int getTextSize() {
				return UtilTools.sp2px(mContext, 16);
			}
			
			@Override
			public int getOneLineEditTextWidth() {
				return UtilTools.dp2pxW(mContext, 795);
			}

			@Override
			public int getMultiLineEditTextWidth() {
				return UtilTools.dp2pxW(mContext, 300);
			}
		};
		
		mSummaryBaseWindow = new BaseWindow((Activity) mContext, windowInterface);
		Map<Integer, Integer> buttons = new HashMap<Integer, Integer>();
		
		// 汇总信息
		buttons.put(0, BaseWindow.editTextReadOnlyLineStyle);
		buttons.put(1, BaseWindow.editTextReadOnlyLineStyle);
		buttons.put(2, BaseWindow.userReadOnlySelectLineStyle);
		buttons.put(3, BaseWindow.tenantReadOnlySelectLineStyle);
		buttons.put(4, BaseWindow.editTextReadOnlyLineStyle);
		buttons.put(5, BaseWindow.editTextReadOnlyLineStyle);
		buttons.put(6, BaseWindow.editTextReadOnlyLineStyle);
		buttons.put(7, BaseWindow.editTextReadOnlyLineStyle);
		buttons.put(8, BaseWindow.editTextReadOnlyLineStyle);
		buttons.put(9, BaseWindow.editTextReadOnlyLineStyle);
		
		// 初始化窗口
		if (mContractChange != null) {
			if (UserCache.getCurrentUser().getTenant_id() 
									== mContractChange.getFirst_party()) {	// 甲方单位
				mSummaryBaseWindow.init(R.array.contract_summary2_lables, 
								buttons, null, false, 2);
			} else {
				mSummaryBaseWindow.init(R.array.contract_summary_lables, 
								buttons, null, false, 2);
			}
		}
		
		// 添加到汇总行布局
		mSummaryLayout.addView(mSummaryBaseWindow.getPopupView());
		
		mChangeBaseWindow = new BaseWindow((Activity) mContext, windowInterface);
		Map<Integer, Integer> buttons2 = new HashMap<Integer, Integer>();
		
		// 变更信息
		buttons2.put(0, BaseWindow.editTextReadOnlyLineStyle);
		buttons2.put(2, BaseWindow.calendarLineStyle);
		buttons2.put(3, BaseWindow.calendarLineStyle);
		buttons2.put(4, BaseWindow.editTextReadOnlyLineStyle);
		buttons2.put(5, BaseWindow.editTextReadOnlyLineStyle);	// 调整工期
		buttons2.put(6, BaseWindow.tenantReadOnlySelectLineStyle);
		buttons2.put(7, BaseWindow.tenantReadOnlySelectLineStyle);
		buttons2.put(8, BaseWindow.editTextReadOnlyLineStyle);
		buttons2.put(9, BaseWindow.editTextReadOnlyLineStyle);
		
		// 单行布局
		List<Integer> onlineList = new ArrayList<Integer>();
		onlineList.add(10);
		mChangeBaseWindow.setOneLineStyle(onlineList);
		
		// 初始化窗口
		mChangeBaseWindow.init(R.array.contract_change_attribute_lables, 
						buttons2, null, false, 2);
		
		// 添加到变更信息行布局中
		mChangeLayout.addView(mChangeBaseWindow.getPopupView());
		
		// 调整后工期自动结算
		mChangeBaseWindow.getEditTextView(3).addTextChangedListener(new TextWatcher() {
			
			@Override
			public void onTextChanged(CharSequence s, int start, int before, int count) {
				int number = MiscUtils.calcDateSub(mContractChange.getYdhtwgrq(), 
						DateUtils.stringToDate(DateUtils.FORMAT_SHORT, s.toString()));
				
				if (number != Integer.MIN_VALUE) {
					mChangeBaseWindow.getEditTextView(13).setText(number + "");
				} else {
					mChangeBaseWindow.getEditTextView(13).setText("");
				}
			}
			
			@Override
			public void beforeTextChanged(CharSequence s, int start, int count,
					int after) {
				
			}
			
			@Override
			public void afterTextChanged(Editable s) {
				
			}
		});
	}
	
	/**
	 * 当父对象改变时切换显示内容
	 */
	@SuppressWarnings("unchecked")
	@Override
	public void handleParentEvent(final Contract_change b) {
		mContractChange = b;
		if (mContext instanceof CallBack) {
			((CallBack<Void, Integer>) mContext).callBack(CONTRACT_CHANGE_ATTRIBUTE_INDEX);
		}
		
		if (b != null) {
			if (mRecieverUser == null 
					|| mRecieverUser.getUser_id() != b.getReceive_contact() 
					|| mSenderUser == null
					|| mSenderUser.getUser_id() != b.getSender_contact()) {
				String ids = b.getReceive_contact() + "," + b.getSender_contact();
				ContactCache.load(ids, new CallBack<Void, Integer>() {

					@Override
					public Void callBack(Integer a) {
						if (a == AnalysisManager.SUCCESS_DB_QUERY) {
							mRecieverUser = ContactCache.getContact(b.getReceive_contact());
							mSenderUser = ContactCache.getContact(b.getSender_contact());
						}
						setDefaultValue(b);
						return null;
					}
				});
			} else {
				setDefaultValue(b);
			}
		} else {
			mSummaryBaseWindow.SetDefaultValue(null);
			mChangeBaseWindow.SetDefaultValue(null);
		}
	}
	
	/**
	 * 设置属性页面内容
	 * @param b
	 */
	private void setDefaultValue(Contract_change b) {
		double totleChangeMoney = 0;
		for (int i = 0; i < mChanges.size(); i++) {
			totleChangeMoney += mChanges.get(i).getBqbgk();
		}
		
		mSummaryBaseWindow.SetDefaultValue(new String[] {
				b.getContract_code(),
				b.getContract_name(),
				b.getOwner() + "",
				b.getReceiver() + "",		// 甲乙方单位
				UtilTools.formatMoney("", b.getYshtzj(), 2),
				UtilTools.formatMoney("", b.getBqbgk(), 2),
				UtilTools.formatMoney("", totleChangeMoney, 2),		// 已批累计变更
				DateUtils.dateToString(DateUtils.FORMAT_SHORT, b.getYdhtwgrq()),
				UtilTools.formatMoney("", totleChangeMoney + b.getYshtzj(), 2),
				DateUtils.dateToString(DateUtils.FORMAT_SHORT, b.getAdjust_finish_date())
		});
		
		mChangeBaseWindow.SetDefaultValue(new String[] {
				b.getCode(),
				b.getName(),
				DateUtils.dateToString(DateUtils.FORMAT_SHORT, b.getApply_date()),
				DateUtils.dateToString(DateUtils.FORMAT_SHORT, b.getAdjust_finish_date()),
				DateUtils.dateToString(DateUtils.FORMAT_SHORT, b.getPass_date()),
				String.valueOf(b.getAdjust_period()),
				String.valueOf(b.getSender()),
				String.valueOf(b.getReceiver()),
				mSenderUser == null ? "" : mSenderUser.getName(),
				mRecieverUser == null ? "" : mRecieverUser.getName(),
				b.getMark()
		});
	}
	
	/**
	 * 获取页面数据
	 * @return
	 */
	public Map<String, String> saveData() {
		return mChangeBaseWindow.SaveData();
	}

	@Override
	public View getRootView() {
		return mRootView;
	}
	
	/**
	 * 获取BaseWindow对象
	 * @return
	 */
	public BaseWindow getBaseWindow() {
		return mChangeBaseWindow;
	}
	
	/**
	 * 更新合同变更信息
	 * @return
	 */
	public Contract_change updateContractChange() {
		return updateContractChange(mContractChange);
	}
	
	/**
	 * 更新合同变更信息
	 * @param contractChange
	 * @return
	 */
	public Contract_change updateContractChange(Contract_change contractChange) {
		Map<String, String> saveMap = mChangeBaseWindow.SaveData();
		
		if (saveMap.get(mContext.getString(R.string.contract_change_name)) != null
				&& !saveMap.get(mContext.getString(R.string.contract_change_name)).isEmpty()) {
			contractChange.setCode(saveMap.get(mContext.getString(R.string.contract_change_number)));
			contractChange.setName(saveMap.get(mContext.getString(R.string.contract_change_name)));
			contractChange.setApply_date(DateUtils.stringToDate(
					DateUtils.FORMAT_SHORT, saveMap.get(mContext.getString(R.string.contract_apply_date))));
	
			if (saveMap.get(mContext.getString(R.string.contract_change_duration)) != null 
					&& !saveMap.get(mContext.getString(R.string.contract_change_duration)).isEmpty()) {
				contractChange.setAdjust_period(Integer.parseInt(saveMap.get(mContext.getString(R.string.contract_change_duration))));
			}
			contractChange.setPass_date(DateUtils.stringToDate(DateUtils.FORMAT_SHORT, 
					saveMap.get(mContext.getString(R.string.contract_availability_date))));
			contractChange.setAdjust_finish_date(DateUtils.stringToDate(DateUtils.FORMAT_SHORT, 
					saveMap.get(mContext.getString(R.string.contract_change_finish_time))));
			
			// 设置填写信息
			if (saveMap.get(mContext.getString(R.string.contract_change_lunch)) != null 
					&& !saveMap.get(mContext.getString(R.string.contract_change_lunch)).isEmpty()) {
				contractChange.setSender(Integer.parseInt(saveMap.get(mContext.getString(R.string.contract_change_lunch))));
			}
			if (saveMap.get(mContext.getString(R.string.contract_change_accept)) != null 
					&& !saveMap.get(mContext.getString(R.string.contract_change_accept)).isEmpty()) {
				contractChange.setReceiver(Integer.parseInt(saveMap.get(mContext.getString(R.string.contract_change_accept))));
			}
			if (saveMap.get(mContext.getString(R.string.cooperation_lunch_contact_window)) != null 
					&& !saveMap.get(mContext.getString(R.string.cooperation_lunch_contact_window)).isEmpty()) {
				contractChange.setSender_contact(Integer.parseInt(saveMap.get(mContext.getString(R.string.cooperation_lunch_contact_window))));
			}
			if (saveMap.get(mContext.getString(R.string.cooperation_accept_contact_window)) != null 
					&& !saveMap.get(mContext.getString(R.string.cooperation_accept_contact_window)).isEmpty()) {
				contractChange.setReceive_contact(Integer.parseInt(saveMap.get(mContext.getString(R.string.cooperation_accept_contact_window))));
			}
			contractChange.setMark(saveMap.get(mContext.getString(R.string.note)));
		}
		
		return contractChange;
	}
	
	/**
	 * 获取新合同变更
	 * @param project
	 * @return
	 */
	public Contract_change getContractChange(Project project) {
		Contract_change contractChange = new Contract_change();
		
		// 设置默认信息
		contractChange.setProject_id(project.getProject_id());
		
		// 更新合同
		updateContractChange(contractChange);
		
		return contractChange;
	}
}
