package com.pm360.cepm360.app.module.contract.pager;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.view.View;
import android.view.View.OnClickListener;

import com.pm360.cepm360.R;
import com.pm360.cepm360.app.common.activity.DataManagerInterface;
import com.pm360.cepm360.app.common.activity.ListSelectActivity;
import com.pm360.cepm360.app.common.activity.OwnerSelectActivity;
import com.pm360.cepm360.app.common.custinterface.CallBack;
import com.pm360.cepm360.app.common.custinterface.ViewPagersInterface;
import com.pm360.cepm360.app.common.view.parent.BaseWidgetCore.WidgetInterface;
import com.pm360.cepm360.app.common.view.parent.BaseWindow;
import com.pm360.cepm360.app.common.view.parent.BaseWindow.WindowInterface;
import com.pm360.cepm360.app.common.view.parent.list.BaseListCommon;
import com.pm360.cepm360.app.module.resource.ContactCompanyFragment;
import com.pm360.cepm360.app.utils.UtilTools;
import com.pm360.cepm360.common.GLOBAL;
import com.pm360.cepm360.common.ResultStatus;
import com.pm360.cepm360.common.json.AnalysisManager;
import com.pm360.cepm360.entity.Contract;
import com.pm360.cepm360.entity.P_LWDW;
import com.pm360.cepm360.entity.Project;
import com.pm360.cepm360.services.common.RemoteCommonService;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class ContractPurchaseCommon implements ViewPagersInterface<Contract> {
	
	// 请求码定义
	public static final int USER_SELECT_REQUEST_CODE = 2000;
	public static final int CONTACT_COMPANY_SELECT_REQUEST_CODE = 2010;
	
	private Context mContext;
	private BaseWindow mBaseWindow;
	private Contract mContract;
	private P_LWDW mContactCompany;
	
	// 合同类型映射表
	private Map<String, String> mContractTypeMap;
	
	// 回调接口
	private CallBack<Void, ContractType> mCallBack;
	
	// 是否是监听模式
	private boolean mIsAddMode;
	
	// 货币类型映射
	private Map<String, String> mCoinTypeMap = BaseListCommon.genIdNameMap(GLOBAL.COIN_TYPE);
	private BaseListCommon<Serializable> mBaseListCommon;
	
	/**
	 * 构造函数
	 * @param context
	 * @param callBack
	 * @param isAddMode
	 */
	public ContractPurchaseCommon(Context context, 
							CallBack<Void, ContractType> callBack, boolean isAddMode) {
		mContext = context;
		mCallBack = callBack;
		mIsAddMode = isAddMode;
		
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
		mBaseListCommon = new BaseListCommon<Serializable>(mContext) {

			@Override
			public Object getListAdapter() {
				return null;
			}
		};
		mBaseListCommon.init();
		
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
		
		mBaseWindow = new BaseWindow((Activity) mContext, windowInterface);
		
		// 单行布局
		List<Integer> onlineList = new ArrayList<Integer>();
		onlineList.add(9);
		onlineList.add(10);
		mBaseWindow.setOneLineStyle(onlineList);
		
		Map<Integer, Integer> buttons = new HashMap<Integer, Integer>();
		buttons.put(0, BaseWindow.editTextReadOnlyLineStyle);
		buttons.put(1, BaseWindow.editTextClickLineStyle);
		buttons.put(3, BaseWindow.editTextReadOnlyLineStyle);
		buttons.put(4, BaseWindow.decimalEditTextLineStyle);
		buttons.put(5, BaseWindow.editTextReadOnlyLineStyle);
		buttons.put(6, BaseWindow.spinnerLineStyle);
		buttons.put(7, BaseWindow.editTextReadOnlyLineStyle);
		buttons.put(8, BaseWindow.userSelectLineStyle);
		
		// 提供数据
		Map<Integer, String[]> map = new HashMap<Integer, String[]>();
		if (mIsAddMode) {
			buttons.put(10, BaseWindow.radioLineStyle);
			map.put(10, BaseListCommon.getGlobalNames(GLOBAL.CONTRACT_TYPE));
		} else {
			buttons.put(10, BaseWindow.editTextReadOnlyLineStyle);
		}
		
		map.put(6, BaseListCommon.getGlobalNames(GLOBAL.COIN_TYPE));
		
		// 将第10个已到第一行
		Map<Integer, Integer> map2 = new HashMap<Integer, Integer>();
		map2.put(10, 0);
		mBaseWindow.setOneLineMap(map2);
		
		// 初始化窗口
		mBaseWindow.init(R.array.contract_purchase_basic_info_lables, 
							buttons, map, false, 2);
		
		if (mIsAddMode) {
			mBaseWindow.setEditTextStyle(0, 0, null, mContext.getString(R.string.contract_edit_text_hint_2));
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
		
		// 选择来往单位
		mBaseWindow.setEditTextStyle(1, 0, new OnClickListener() {
			
			@Override
			public void onClick(View v) {
				Intent intent = new Intent(mContext, ListSelectActivity.class);
				intent.putExtra(ListSelectActivity.SELECT_MODE_KEY, 
						ListSelectActivity.SINGLE_SELECT);
				intent.putExtra(ListSelectActivity.FRAGMENT_KEY, ContactCompanyFragment.class);
				((Activity) mContext).startActivityForResult(intent, 
						CONTACT_COMPANY_SELECT_REQUEST_CODE);
			}
		}, "");
				
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
		
		// 获取采购合同编号
		if (mIsAddMode) {
			mBaseListCommon.sendEmptyMessageDelayed(BaseListCommon.SHOW_PROGRESS_DIALOG);
			RemoteCommonService.getInstance().getCodeByDate(new DataManagerInterface() {
				
				@Override
				public void getDataOnResult(ResultStatus status, List<?> list) {
					mBaseListCommon.sendMessage(BaseListCommon.DISMISS_PROGRESS_DIALOG);
					if (status.getCode() == AnalysisManager.SUCCESS_DB_QUERY) {
						mBaseWindow.setEditTextContent(0, status.getMessage());
					} else {
						mBaseListCommon.sendMessage(BaseListCommon.SHOW_TOAST, 
								status.getMessage());
					}
				}
			}, "CGHT");
		}
	}

	/**
	 * 当父对象改变时切换显示内容
	 */
	@Override
	public void handleParentEvent(Contract b) {
		mContract = b;
		mCallBack.callBack(ContractType.PURCHASE_CONTRACT);
		
		if (b != null) {
			mBaseWindow.SetDefaultValue(new String[] {
					b.getCode(),
					b.getSecond_party_name(),
					b.getName(),
					b.getSecond_party_address(),
					UtilTools.formatMoney("", b.getTotal(), 2),
					b.getSecond_party_keyperson(),
					mCoinTypeMap.get(String.valueOf(b.getCoin())),
					b.getSecond_party_keyperson_tel(),
					b.getOwner() + "",
					b.getMark(),
					mContractTypeMap.get(b.getType() + "")
					
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
		Map<String, String> saveMap = mBaseWindow.SaveData();
		
		// 合同类型
		contract.setType(Integer.parseInt(GLOBAL.CONTRACT_TYPE[1][0]));
		contract.setCode(mContext.getString(R.string.contract_code));
				
		if (!saveMap.get(mContext.getString(R.string.supplier)).isEmpty() 
						&& mContactCompany != null) {
			contract.setSecond_party(mContactCompany.getLwdw_id());
			contract.setSecond_party_name(mContactCompany.getName());
		}
		
		contract.setName(saveMap.get(mContext.getString(R.string.contract_name)));
		contract.setSecond_party_address(mContext.getString(R.string.company_address));
		
		if (!saveMap.get(mContext.getString(R.string.contract_total)).isEmpty()) {
			contract.setTotal(UtilTools.backFormatMoney("", saveMap.get(mContext.getString(R.string.contract_total))));
		}
		
		if (!saveMap.get(mContext.getString(R.string.contacts)).isEmpty()) {
			contract.setSecond_party_keyperson(saveMap.get(mContext.getString(R.string.contacts)));
		}
		
		if (!saveMap.get(mContext.getString(R.string.contract_currency)).isEmpty()) {
			contract.setCoin(Integer.parseInt(coinMap.get(saveMap.get(mContext.getString(R.string.contract_currency)))));
		}
		
		contract.setSecond_party_keyperson_tel(saveMap.get(mContext.getString(R.string.contact_number)));
		
		if (!saveMap.get(mContext.getString(R.string.owner)).isEmpty()) {
			contract.setOwner(Integer.parseInt(saveMap.get(mContext.getString(R.string.owner))));
		}
		
		contract.setMark(saveMap.get(mContext.getString(R.string.note)));
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
	
	/**
	 * 当选中往来单位时，设置相关显示信息
	 * @param contactCompany
	 */
	public void setContactCompany(P_LWDW contactCompany) {
		mContactCompany = contactCompany;
		
		mBaseWindow.setEditTextContent(1, contactCompany.getName());
		mBaseWindow.setEditTextContent(3, contactCompany.getAddress());
		mBaseWindow.setEditTextContent(5, contactCompany.getKey_person());
		mBaseWindow.setEditTextContent(7, contactCompany.getTel());
	}
}
