package com.pm360.cepm360.app.module.contract.pager;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.LinearLayout.LayoutParams;

import com.pm360.cepm360.R;
import com.pm360.cepm360.app.cache.UserCache;
import com.pm360.cepm360.app.common.activity.OwnerSelectActivity;
import com.pm360.cepm360.app.common.custinterface.BaseWidgetInterface;
import com.pm360.cepm360.app.common.custinterface.CallBack;
import com.pm360.cepm360.app.common.custinterface.ViewPagersInterface;
import com.pm360.cepm360.app.common.view.parent.BaseWidgetCore.WidgetInterface;
import com.pm360.cepm360.app.common.view.parent.BaseWindow;
import com.pm360.cepm360.app.common.view.parent.BaseWindow.WindowInterface;
import com.pm360.cepm360.app.common.view.parent.DoubleDatePickerDialog;
import com.pm360.cepm360.app.common.view.parent.list.BaseListCommon;
import com.pm360.cepm360.app.module.cooperation.UnitSelectActivity;
import com.pm360.cepm360.app.utils.UtilTools;
import com.pm360.cepm360.common.GLOBAL;
import com.pm360.cepm360.common.util.DateUtils;
import com.pm360.cepm360.common.util.MiscUtils;
import com.pm360.cepm360.common.util.TwoNumber;
import com.pm360.cepm360.entity.Contract;
import com.pm360.cepm360.entity.Project;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class ContractProjectCommon implements ViewPagersInterface<Contract> {
	
	// 请求码定义
	public static final int USER_SELECT_REQUEST_CODE = 1000;
	public static final int TENANT_SELECT_REQUEST_CODE = 1010;
	public static final int ATTACHMENT_SELECT_REQUEST_CODE = 1020;
	
	private Context mContext;
	private BaseWindow mBaseWindow;
	private Contract mContract;
	private Project mProject;
	
	// 合同类型映射表
	private Map<String, String> mContractTypeMap;
	
	// 是否是收入合同
	private boolean mIsIncomeContract;
	private boolean mIsAddMode;	// 添加模式
	private boolean mIsModify;
	
	// 回调接口
	private CallBack<Void, ContractType> mCallBack;
	
	// 货币映射
	private Map<String, String> mCoinTypeMap = BaseListCommon.genIdNameMap(GLOBAL.COIN_TYPE);
	
	/**
	 * 构造函数
	 * @param context
	 * @param callBack
	 * @param isIncomeContract
	 * @param isAddMode
	 * @param isModify
	 */
	public ContractProjectCommon(Context context, 
					CallBack<Void, ContractType> callBack,
					boolean isIncomeContract,
					Project project,
					boolean isAddMode,
					boolean isModify) {
		mContext = context;
		mCallBack = callBack;
		mProject = project;
		mIsIncomeContract = isIncomeContract;
		mIsAddMode = isAddMode;
		mIsModify = isModify;
		
		// 初始化合同类型映射表
		mContractTypeMap = BaseListCommon.genIdNameMap(GLOBAL.CONTRACT_TYPE);
		
		// 初始化常用信息
		initCommonInfo();
	}
	
	/**
	 * 初始化常用页
	 */
	@SuppressLint("UseSparseArrays") 
	private void initCommonInfo() {
		WindowInterface windowInterface = null;
		
		if (mIsAddMode) {
			windowInterface = new WindowInterface() {
				
				@Override
				public int getWidgetWidthMargin() {
					return UtilTools.dp2pxW(mContext, 20);
				}
				
				@Override
				public int getWidgetHeightMargin() {
					return 0;
				}
				
				@Override
				public int getTextSize() {
					return UtilTools.sp2px(mContext, 16);
				}
				
				@Override
				public int getOneLineEditTextWidth() {
					return UtilTools.dp2pxW(mContext, 543);
				}
	
				@Override
				public int getMultiLineEditTextWidth() {
					return UtilTools.dp2pxW(mContext, 205);
				}
			};
		} else {
			windowInterface = new WindowInterface() {
				
				@Override
				public int getWidgetWidthMargin() {
					return UtilTools.dp2pxW(mContext, 80);
				}
				
				@Override
				public int getWidgetHeightMargin() {
					return 0;
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
		}
		
		mBaseWindow = new BaseWindow((Activity) mContext, windowInterface, new BaseWidgetInterface() {
			
			@Override
			public Integer[] getImportantColumns() {
				return new Integer[]{1, 2, 5, 7, 9, 12};
			}
			
			@Override
			public TwoNumber<View, LayoutParams> createExtraLayout() {
				return null;
			}
		});
		
		// 单行布局
		List<Integer> onlineList = new ArrayList<Integer>();
		onlineList.add(14);
		onlineList.add(15);
		onlineList.add(16);
		mBaseWindow.setOneLineStyle(onlineList);
		
		Map<Integer, Integer> buttons = new HashMap<Integer, Integer>();
		buttons.put(0, BaseWindow.editTextReadOnlyLineStyle);
		buttons.put(1, BaseWindow.calendarLineStyle);
		buttons.put(4, BaseWindow.decimalEditTextLineStyle);
		buttons.put(5, BaseWindow.calendarLineStyle);
		buttons.put(6, BaseWindow.spinnerLineStyle);
		buttons.put(7, BaseWindow.calendarLineStyle);
		buttons.put(8, BaseWindow.userSelectLineStyle);
		buttons.put(9, BaseWindow.calendarLineStyle);
		buttons.put(11, BaseWindow.editTextReadOnlyLineStyle);
		
		// 提供数据
		Map<Integer, String[]> map = new HashMap<Integer, String[]>();
		
		// 不同模式和合同类型的特殊处理
		if (mIsAddMode) {
			if (mIsIncomeContract) {
				buttons.put(12, BaseWindow.tenantSelectLineStyle);
				buttons.put(13, BaseWindow.tenantReadOnlySelectLineStyle);
				
				// 收入合同，单选按钮就一个
				map.put(14, new String[] { GLOBAL.CONTRACT_TYPE[0][1] });
			} else {
				buttons.put(12, BaseWindow.tenantReadOnlySelectLineStyle);
				buttons.put(13, BaseWindow.tenantSelectLineStyle);
				
				// 支出合同，单选按钮有三个
				map.put(14, BaseListCommon.getGlobalNames(GLOBAL.CONTRACT_TYPE));
			}
			
			buttons.put(14, BaseWindow.radioLineStyle);
		} else {
			if (mIsModify) {
				if (mIsIncomeContract) {
					buttons.put(12, BaseWindow.tenantSelectLineStyle);
					buttons.put(13, BaseWindow.tenantReadOnlySelectLineStyle);
				} else {
					buttons.put(12, BaseWindow.tenantReadOnlySelectLineStyle);
					buttons.put(13, BaseWindow.tenantSelectLineStyle);
				}
			} else {
				buttons.put(12, BaseWindow.tenantReadOnlySelectLineStyle);
				buttons.put(13, BaseWindow.tenantReadOnlySelectLineStyle);
			}
			buttons.put(14, BaseWindow.editTextReadOnlyLineStyle);
		}
		map.put(6, BaseListCommon.getGlobalNames(GLOBAL.COIN_TYPE));
		
		// 将第14个移到第一行
		Map<Integer, Integer> map2 = new HashMap<Integer, Integer>();
		map2.put(14, 0);
		mBaseWindow.setOneLineMap(map2);
		
		// 初始化BaseWindow
		mBaseWindow.init(R.array.contract_project_basic_info_lables, 
							buttons, map, false, 2);
		
		final DoubleDatePickerDialog doubleDatePickerDialog = new DoubleDatePickerDialog(
				mContext, null, mBaseWindow.getEditTextView(7), mBaseWindow.getEditTextView(9), null);
		OnClickListener dateListener = new OnClickListener() {

			@Override
			public void onClick(View v) {
					doubleDatePickerDialog.show();
			}
		};

		mBaseWindow.setEditTextStyle(7, 0, dateListener, null);
		mBaseWindow.setEditTextStyle(9, 0, dateListener, null);
		
		mBaseWindow.setEditTextStyle(0, 0, null, 
				mContext.getString(R.string.system_auto_generate));
		
		// 初始化合同类型为“工程合同”
		mBaseWindow.setRadioButtonPosition(14, GLOBAL.CONTRACT_TYPE[0][1]);
		
		// 如果是添加模式，注册监听
		if (mIsAddMode) {
			if (!mIsIncomeContract) {
				mBaseWindow.setWidgetInterface(new WidgetInterface() {
	
					@Override
					public void setRadioButtonChangeListener(int line, int position) {
						switch (position) {
							case 0:
								mCallBack.callBack(ContractType.PROJECT_CONTRACT);
								break;
							case 1:
								mCallBack.callBack(ContractType.PURCHASE_CONTRACT);
								break;
							case 2:
								mCallBack.callBack(ContractType.LEASE_CONTRACT);
								break;
							default:
								break;
						}
					}
				});
			}
		}
		
		// 工期自动结算
		mBaseWindow.getEditTextView(7).addTextChangedListener(new TextWatcher() {
			
			@Override
			public void onTextChanged(CharSequence s, int start, int before, int count) {
				int number = MiscUtils.calcDateSub(s.toString(), 
						mBaseWindow.getEditTextView(9).getText().toString());
				if (number != Integer.MIN_VALUE) {
					mBaseWindow.getEditTextView(11).setText("" + number);
				} else {
					mBaseWindow.getEditTextView(11).setText("");
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
		
		// 工期自动结算
		mBaseWindow.getEditTextView(9).addTextChangedListener(new TextWatcher() {
			
			@Override
			public void onTextChanged(CharSequence s, int start, int before, int count) {
				int number = MiscUtils.calcDateSub(mBaseWindow.getEditTextView(7)
						.getText().toString(), s.toString());
				if (number != Integer.MIN_VALUE) {
					mBaseWindow.getEditTextView(11).setText("" + number);
				} else {
					mBaseWindow.getEditTextView(11).setText("");
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
		
		// 用户选择监听
		mBaseWindow.setEditTextStyle(8, 0, new OnClickListener() {
			
			@Override
			public void onClick(View v) {
				Intent intent = new Intent(mContext, 
								OwnerSelectActivity.class);
				((Activity) mContext).startActivityForResult(intent, 
								USER_SELECT_REQUEST_CODE);
			}
		}, "");
		
		// 承包方、发包方选择监听
		if (!mIsIncomeContract) {
			mBaseWindow.setTenantContent(12, UserCache.getTenantId());
			
			if (mIsAddMode || mIsModify) {
				mBaseWindow.setEditTextStyle(13, 0, new OnClickListener() {
					
					@Override
					public void onClick(View v) {
						Intent intent = new Intent(mContext, UnitSelectActivity.class);
						intent.putExtra(UnitSelectActivity.TITLE_KEY, mContext
								.getResources().getString(R.string.second_party));
						intent.putExtra(UnitSelectActivity.ACTION_KEY, 
								UnitSelectActivity.CooperationTenantListByProject);
						if (mIsAddMode) {
							intent.putExtra(UnitSelectActivity.PROJECT_KEY, mProject.getProject_id());
						} else {
							intent.putExtra(UnitSelectActivity.PROJECT_KEY, mContract.getProject_id());
						}
						intent.putExtra(UnitSelectActivity.TENANT_KEY, 
								UserCache.getCurrentUser().getTenant_id());
						((Activity) mContext).startActivityForResult(intent, 
												TENANT_SELECT_REQUEST_CODE + 1);
					}
				}, "");
			}
		} else {
			if (mIsAddMode || mIsModify) {
				mBaseWindow.setEditTextStyle(12, 0, new OnClickListener() {
					
					@Override
					public void onClick(View v) {
						Intent intent = new Intent(mContext, UnitSelectActivity.class);
						intent.putExtra(UnitSelectActivity.TITLE_KEY, mContext
										.getResources().getString(R.string.first_party));
						intent.putExtra(UnitSelectActivity.ACTION_KEY, UnitSelectActivity.CooperationTenantListByProject);
						if (mIsAddMode) {
							intent.putExtra(UnitSelectActivity.PROJECT_KEY, mProject.getProject_id());
						} else {
							intent.putExtra(UnitSelectActivity.PROJECT_KEY, mContract.getProject_id());
						}
						intent.putExtra(UnitSelectActivity.TENANT_KEY, 
								UserCache.getCurrentUser().getTenant_id());
						((Activity) mContext).startActivityForResult(intent, 
										TENANT_SELECT_REQUEST_CODE);
					}
				}, "");
			}
			
			mBaseWindow.setTenantContent(13, UserCache.getTenantId());
		}
	}

	/**
	 * 当父对象改变时切换显示内容
	 */
	@Override
	public void handleParentEvent(Contract b) {
		mContract = b;
		mCallBack.callBack(ContractType.PROJECT_CONTRACT);
		
		if (b != null) {
			mBaseWindow.SetDefaultValue(new String[] {
					b.getCode(),
					DateUtils.dateToString(DateUtils.FORMAT_SHORT, b.getSign_date()),
					b.getName(),
					b.getSign_adress(),
					UtilTools.formatMoney("", b.getTotal(), 2),
					DateUtils.dateToString(DateUtils.FORMAT_SHORT, b.getAvailability_date()),
					mCoinTypeMap.get(String.valueOf(b.getCoin())),
					DateUtils.dateToString(DateUtils.FORMAT_SHORT, b.getStart_date()),
					String.valueOf(b.getOwner()),
					DateUtils.dateToString(DateUtils.FORMAT_SHORT, b.getEnd_date()),
					b.getP_adress(),
					b.getDuration() + "",
					b.getFirst_party() + "",
					b.getSecond_party() + "",
					mContractTypeMap.get(b.getType() + ""),
					b.getP_content(),
					b.getQuality_standard()
					
			});
		} else {
			mBaseWindow.SetDefaultValue(null);
		}
	}
	
	/**
	 * 获取页面数据
	 * @return
	 */
	public Map<String, String> saveData() {
		return mBaseWindow.SaveData();
	}

	@Override
	public View getRootView() {
		return mBaseWindow.getPopupView();
	}
	
	/**
	 * 获取BaseWindow对象
	 * @return
	 */
	public BaseWindow getBaseWindow() {
		return mBaseWindow;
	}
	
	/**
	 * 更新合同信息
	 * @return
	 */
	public Contract updateContract() {
		return updateContract(mContract);
	}
	
	/**
	 * 更新合同信息
	 * @param contract
	 * @return
	 */
	public Contract updateContract(Contract contract) {
		Map<String, String> coinMap = BaseListCommon.genIdNameMap(GLOBAL.COIN_TYPE);
		Map<String, String> typeMap = BaseListCommon.genIdNameMap(GLOBAL.CONTRACT_TYPE);
		Map<String, String> saveMap = mBaseWindow.SaveData();
		
		// 设置填写信息
		if (saveMap.get(mContext.getString(R.string.contract_type)) != null 
				&& !saveMap.get(mContext.getString(R.string.contract_type)).isEmpty()) {
			contract.setType(Integer.parseInt(typeMap.get(saveMap.get(mContext.getString(R.string.contract_type)))));
		}
		contract.setName(saveMap.get(mContext.getString(R.string.contract_name)));
		contract.setSign_date(DateUtils.stringToDate(DateUtils.FORMAT_SHORT, 
				saveMap.get(mContext.getString(R.string.contract_sign_date))));
		contract.setSign_adress(saveMap.get(mContext.getString(R.string.contract_sign_adress)));
		
		if (!saveMap.get(mContext.getString(R.string.contract_total)).isEmpty()) {
			contract.setTotal(UtilTools.backFormatMoney("", saveMap.get(mContext.getString(R.string.contract_total))));
		}
		
		contract.setAvailability_date(DateUtils.stringToDate(DateUtils.FORMAT_SHORT, 
				saveMap.get(mContext.getString(R.string.contract_availability_date))));
		
		if (!saveMap.get(mContext.getString(R.string.contract_currency)).isEmpty()) {
			contract.setCoin(Integer.parseInt(coinMap.get(saveMap.get(mContext.getString(R.string.contract_currency)))));
		}
		contract.setStart_date(DateUtils.stringToDate(DateUtils.FORMAT_SHORT, 
				saveMap.get(mContext.getString(R.string.contract_start_date))));
		
		if (!saveMap.get(mContext.getString(R.string.owner)).isEmpty()) {
			contract.setOwner(Integer.parseInt(saveMap.get(mContext.getString(R.string.owner))));
		}
		
		contract.setEnd_date(DateUtils.stringToDate(DateUtils.FORMAT_SHORT, 
				saveMap.get(mContext.getString(R.string.contract_end_date))));
		contract.setP_adress(saveMap.get(mContext.getString(R.string.contract_p_adress)));
		
		if (!saveMap.get(mContext.getString(R.string.contract_duration)).isEmpty()) {
			contract.setDuration(Integer.parseInt(saveMap.get(mContext.getString(R.string.contract_duration))));
		}
		
		if (!saveMap.get(mContext.getString(R.string.contract_first_party)).isEmpty()) {
			contract.setFirst_party(Integer.parseInt(saveMap.get(mContext.getString(R.string.contract_first_party))));
		}
		
		if (!saveMap.get(mContext.getString(R.string.contract_second_party)).isEmpty()) {
			contract.setSecond_party(Integer.parseInt(saveMap.get(mContext.getString(R.string.contract_second_party))));
		}
		contract.setP_content(saveMap.get(mContext.getString(R.string.contract_p_content)));
		contract.setQuality_standard(saveMap.get(mContext.getString(R.string.contract_quality_standard)));
		
		return contract;
	}
	
	/**
	 * 获取新合同
	 * @param project
	 * @return
	 */
	public Contract getContract(Project project) {
		Contract contract = new Contract();
		
		// 设置默认信息
		contract.setProject_id(project.getProject_id());
		contract.setTenant_id(project.getTenant_id());
		
		// 更新合同
		updateContract(contract);
		
		return contract;
	}
}
