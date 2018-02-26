package com.pm360.cepm360.app.module.contract.pager;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.Dialog;
import android.content.Context;
import android.content.Intent;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.Gravity;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.Toast;

import com.pm360.cepm360.R;
import com.pm360.cepm360.app.cache.ContactCache;
import com.pm360.cepm360.app.cache.CooperationCache;
import com.pm360.cepm360.app.cache.UserCache;
import com.pm360.cepm360.app.common.activity.DataManagerInterface;
import com.pm360.cepm360.app.common.activity.ListSelectActivity;
import com.pm360.cepm360.app.common.activity.OwnerSelectActivity;
import com.pm360.cepm360.app.common.custinterface.CallBack;
import com.pm360.cepm360.app.common.custinterface.ViewPagersInterface;
import com.pm360.cepm360.app.common.view.parent.BaseWindow;
import com.pm360.cepm360.app.common.view.parent.BaseWindow.WindowInterface;
import com.pm360.cepm360.app.common.view.parent.list.BaseListCommon;
import com.pm360.cepm360.app.module.contract.CodeFactory;
import com.pm360.cepm360.app.module.contract.IncomeContractWithoutTreeFragment;
import com.pm360.cepm360.app.module.contract.OutcomeContractWithoutTreeFragment;
import com.pm360.cepm360.app.utils.UtilTools;
import com.pm360.cepm360.common.GLOBAL;
import com.pm360.cepm360.common.ResultStatus;
import com.pm360.cepm360.common.json.AnalysisManager;
import com.pm360.cepm360.common.util.DateUtils;
import com.pm360.cepm360.common.util.MiscUtils;
import com.pm360.cepm360.entity.Contract;
import com.pm360.cepm360.entity.Contract_change;
import com.pm360.cepm360.entity.Project;
import com.pm360.cepm360.entity.Tenant;
import com.pm360.cepm360.entity.User;
import com.pm360.cepm360.services.common.RemoteCommonService;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class ContractChangeApplyAttribute implements ViewPagersInterface<Contract_change> {
	
	// 请求码定义
	public static final int USER_SELECT_REQUEST_CODE = 5010;
	public static final int TENANT_SELECT_REQUEST_CODE = 5020;
	public static final int ATTACHMENT_SELECT_REQUEST_CODE = 5030;
	public static final int CONTRACT_SELECT_REQUEST_CODE = 5040;
	
	// 该类索引，标识该类的回调调用
	public static final int CONTRACT_CHANGE_COMMON_INDEX = 5100;
	
	private Context mContext;
	private BaseWindow mBaseWindow;
	private Dialog mPickDialog;
	
	private Contract mContract;
	private Project mProject;
	private Contract_change mContractChange;
	private boolean mIsAddMode;
	private boolean mIsModify;
	
	// 发送方和接收方联系人，发送方未必是本单位
	private User mSenderUser;
	private User mRecieverUser;

	private BaseListCommon<Serializable> mBaseListCommon;
	/**
	 * 构造函数，添加合同变更
	 * @param context
	 * @param project
	 */
	public ContractChangeApplyAttribute(Context context, 
					Project project, boolean isAddMode) {
		this(context, null, project, null, isAddMode, true);
	}
	
	/**
	 * 属性，修改
	 * @param context
	 * @param project
	 * @param contractChange
	 */
	public ContractChangeApplyAttribute(Context context, 
					Contract_change contractChange, boolean isModify) {
		this(context, null, null, contractChange, false, isModify);
	}
	
	/**
	 * 构造函数
	 * @param context
	 * @param contract
	 * @param project
	 */
	public ContractChangeApplyAttribute(Context context, 
								Contract contract, 
								Project project, 
								Contract_change contractChange, 
								boolean isAddMode,
								boolean isModify) {
		mContext = context;
		mContract = contract;
		mProject = project;
		mContractChange = contractChange;
		mIsAddMode = isAddMode;
		mIsModify = isModify;
		
		// 初始化选择对话框
		if (mProject != null) {
			initPickDialog();
		}
		
		if (mIsAddMode) {
			mBaseListCommon = new BaseListCommon<Serializable>(mContext) {

				@Override
				public Object getListAdapter() {
					return null;
				}
			};
			mBaseListCommon.init();
		}
		
		// 初始化常用信息
		initCommonLayout();
	}
	
	/**
	 * 设置选中的合同
	 * @param contract
	 */
	public void setContract(Contract contract) {
		mContract = contract;
		
		// 当选择合同后，初始化合同信息
		initContractInfo();
	}
	
	/**
	 * 初始化常用页
	 */
	@SuppressLint("UseSparseArrays") 
	private void initCommonLayout() {
		WindowInterface windowInterface = null;
		
		if (mIsAddMode) {
			windowInterface = new WindowInterface() {
				
				@Override
				public int getWidgetWidthMargin() {
					return UtilTools.dp2pxW(mContext, 20);
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
					return UtilTools.dp2pxW(mContext, 542);
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
		}
		
		mBaseWindow = new BaseWindow((Activity) mContext, windowInterface);
		
		// 单行布局
		List<Integer> onlineList = new ArrayList<Integer>();
		onlineList.add(12);
		mBaseWindow.setOneLineStyle(onlineList);
		
		Map<Integer, Integer> buttons = new HashMap<Integer, Integer>();
		
		buttons.put(0, BaseWindow.editTextClickLineStyle);
		buttons.put(1, BaseWindow.editTextClickLineStyle);
		buttons.put(2, BaseWindow.editTextReadOnlyLineStyle);
		buttons.put(4, BaseWindow.calendarLineStyle);
		buttons.put(5, BaseWindow.calendarLineStyle);
		buttons.put(6, BaseWindow.editTextReadOnlyLineStyle);
		buttons.put(7, BaseWindow.editTextReadOnlyLineStyle);
		buttons.put(8, BaseWindow.tenantReadOnlySelectLineStyle);
		buttons.put(9, BaseWindow.tenantReadOnlySelectLineStyle);
		if (mIsModify) {
			buttons.put(10, BaseWindow.userSelectLineStyle);
			buttons.put(11, BaseWindow.editTextReadOnlyLineStyle);
		} else {
			buttons.put(10, BaseWindow.editTextReadOnlyLineStyle);
			buttons.put(11, BaseWindow.editTextReadOnlyLineStyle);
		}
		
		mBaseWindow.init(R.array.contract_change_add_basic_info_lables, 
							buttons, null, false, 2);
		
		// 合同选择监听器
		OnClickListener contractClickListener = new OnClickListener() {
			
			@Override
			public void onClick(View v) {
				mPickDialog.show();
			}
		};
		
		// 设置合同选择监听
		if (mIsAddMode) {
			mBaseWindow.setEditTextStyle(0, 0, contractClickListener, mContext.getString(R.string.contract_edit_text_hint_1));
			mBaseWindow.setEditTextStyle(1, 0, contractClickListener, mContext.getString(R.string.contract_edit_text_hint_1));
			mBaseWindow.setEditTextStyle(2, 0, null, mContext.getString(R.string.contract_edit_text_hint_2));
			mBaseWindow.setEditTextStyle(6, 0, null, mContext.getString(R.string.contract_edit_text_hint_3));
			RemoteCommonService.getInstance().getCodeByDate(new DataManagerInterface() {
				
				@Override
				public void getDataOnResult(ResultStatus status, List<?> list) {
					if (status.getCode() == AnalysisManager.SUCCESS_DB_QUERY) {
						mBaseWindow.setEditTextContent(2, status.getMessage());
					} else {
	    				mBaseListCommon.sendMessage(BaseListCommon.SHOW_TOAST, status.getMessage());
					}
				}
			}, CodeFactory.getCodePrefix(CodeFactory.CONTRACT_CHANGE_CODE));
		}
		
		// 调整后工期自动结算
		mBaseWindow.getEditTextView(5).addTextChangedListener(new TextWatcher() {
			
			@Override
			public void onTextChanged(CharSequence s, int start, int before, int count) {
				int number = Integer.MIN_VALUE;
				if (mContractChange != null) {
					number = MiscUtils.calcDateSub(mContractChange.getYdhtwgrq(), 
							DateUtils.stringToDate(DateUtils.FORMAT_SHORT, s.toString()));
				}
				
				if (mContract != null) {
					number = MiscUtils.calcDateSub(mContract.getEnd_date(), 
							DateUtils.stringToDate(DateUtils.FORMAT_SHORT, s.toString()));
				}
				
				if (number != Integer.MIN_VALUE) {
					mBaseWindow.getEditTextView(7).setText(number + "");
				} else {
					mBaseWindow.getEditTextView(7).setText("");
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
		
		// 初始合同相关信息
		initContractInfo();
		
		// 用户选择监听
		mBaseWindow.setEditTextStyle(10, 0, new OnClickListener() {
			
			@Override
			public void onClick(View v) {
				if (!mBaseWindow.getEditTextView(0).getText().toString().isEmpty()) {
					Intent intent = new Intent(mContext, OwnerSelectActivity.class);
					((Activity) mContext).startActivityForResult(intent,
							USER_SELECT_REQUEST_CODE);
				} else {
					showToast(mContext.getResources().getString(R.string.select_contract_first));
				}
			}
		}, "");
		
		mBaseWindow.setEditTextStyle(11, R.drawable.user_select_icon, new OnClickListener() {
			
			@Override
			public void onClick(View v) {
				if (!mBaseWindow.getEditTextView(0).getText().toString().isEmpty()) {
					final Intent intent = new Intent(mContext, OwnerSelectActivity.class);
					
					// 从对方公司选择联系人
					if (mIsAddMode) {
						if (mContract != null) {
							int another = 0;
							if (UserCache.getTenantId() == mContract.getFirst_party()) {
								another = mContract.getSecond_party();
							} else {
								another = mContract.getFirst_party();
							}
							
							final int tenantId1 = another;
							CooperationCache.getTenant(mContract.getProject_id(), 
									tenantId1, new CallBack<Void, Tenant>() {
								@Override
								public Void callBack(Tenant a) {
									if (a != null) {
										intent.putExtra("cooperation_id", a.getCooperation_id());
										((Activity) mContext).startActivityForResult(intent, 
												USER_SELECT_REQUEST_CODE + 1);
									}
									return null;
								}
							});
						}
					} else {
						if (mContractChange != null) {
							final int tenantId1 = mContractChange.getReceiver();
							CooperationCache.getTenant(mContractChange.getProject_id(), 
									tenantId1, new CallBack<Void, Tenant>() {
								@Override
								public Void callBack(Tenant a) {
									if (a != null) {
										intent.putExtra("cooperation_id", a.getCooperation_id());
										((Activity) mContext).startActivityForResult(intent, 
												USER_SELECT_REQUEST_CODE + 1);
									}
									
									return null;
								}
							});
						}
					}
				} else {
					showToast(mContext.getResources().getString(R.string.select_contract_first));
				}
			}}, "");
		
		// 指定申请日期为当前日期，可以修改
		mBaseWindow.setEditTextContent(4, DateUtils.dateToString(DateUtils.FORMAT_SHORT,
				new Date()));
	}
	
	/**
	 * 显示Toast
	 * @param showText
	 */
	private void showToast(String showText) {
		Toast toast = Toast.makeText(mContext, showText, Toast.LENGTH_LONG);
		toast.setGravity(Gravity.CENTER, 0, 0);
		toast.show();
	}
	
	/**
	 * 选中合同后，初始化相关信息，合同编号，合同名称，发起方和接收方
	 */
	private void initContractInfo() {
		if (mContract != null) {
			
			// 设置合同信息
			mBaseWindow.setEditTextContent(0, mContract.getCode());
			mBaseWindow.setEditTextContent(1, mContract.getName());
			
			// 设置发起方和接收方
			mBaseWindow.setTenantContent(8, mContract.getTenant_id());
			if (mContract.getTenant_id() == mContract.getFirst_party()) {
				mBaseWindow.setTenantContent(9, mContract.getSecond_party());
			} else {
				mBaseWindow.setTenantContent(9, mContract.getFirst_party());
			}
			
			// 重新计算工期
			String endDate = mBaseWindow.getEditTextView(5).getText().toString();
			if (endDate.isEmpty()) {
				mBaseWindow.setEditTextContent(5, DateUtils
						.dateToString(DateUtils.FORMAT_SHORT, mContract.getEnd_date()));
			}
			if (!endDate.equals("")) {
				int number = MiscUtils.calcDateSub(mContract.getEnd_date(), 
							DateUtils.stringToDate(DateUtils.FORMAT_SHORT, endDate));
				
				if (number != Integer.MIN_VALUE) {
					mBaseWindow.getEditTextView(7).setText(number + "");
				} else {
					mBaseWindow.getEditTextView(7).setText("");
				}
			}
		}
	}
	
	/**
     * 初始化选择对话框
     */
    private void initPickDialog() {
        mPickDialog = new Dialog(mContext, R.style.MyDialogStyle);
        mPickDialog.setContentView(R.layout.change_attachment_pic_pick_dialog);
        
        // 选择收入合同
        Button incomeContractButton = (Button) mPickDialog.findViewById(R.id.take_picture);
        incomeContractButton.setText(mContext.getResources()
        		.getString(R.string.income_contract_select));
        
        // 选择支出合同
        Button outcomeContractButton = (Button) mPickDialog.findViewById(R.id.local_picture);
        outcomeContractButton.setText(mContext.getResources()
        		.getString(R.string.outcome_contract_select));
        
        // 注册监听
        incomeContractButton.setOnClickListener(mButtonClickListener);
        outcomeContractButton.setOnClickListener(mButtonClickListener);

        // 设置点击其他地方退出选择对话框
        mPickDialog.setCanceledOnTouchOutside(true);
    }
    
    /**
     * 按键监听
     */
    OnClickListener mButtonClickListener = new OnClickListener() {
		
		@Override
		public void onClick(View v) {
			mPickDialog.dismiss();
			
			// 请求选择合同
			Intent intent = new Intent(mContext, ListSelectActivity.class);
			
			// 设置工程项目
			if (mProject != null) {
				intent.putExtra(ListSelectActivity.PROJECT_KEY, mProject);
			}
			intent.putExtra(ListSelectActivity.SELECT_MODE_KEY, ListSelectActivity.SINGLE_SELECT);
			
			switch (v.getId()) {
				case R.id.take_picture:		// 收入合同
					intent.putExtra(ListSelectActivity.FRAGMENT_KEY, 
										IncomeContractWithoutTreeFragment.class);
					break;
				case R.id.local_picture:	// 支出合同，工程合同
					intent.putExtra(ListSelectActivity.FRAGMENT_KEY, 
							OutcomeContractWithoutTreeFragment.class);
					intent.putExtra(ListSelectActivity.PARENT_BEAN_KEY, 
							GLOBAL.CONTRACT_TYPE[0][0]);
					break;
				default:
					break;
			}
			((Activity) mContext).startActivityForResult(intent, CONTRACT_SELECT_REQUEST_CODE);
		}
	};

	/**
	 * 当父对象改变时切换显示内容
	 */
	@SuppressWarnings("unchecked")
	@Override
	public void handleParentEvent(final Contract_change b) {
		mContractChange = b;
		if (mContext instanceof CallBack) {
			((CallBack<Void, Integer>) mContext).callBack(CONTRACT_CHANGE_COMMON_INDEX);
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
			mBaseWindow.SetDefaultValue(null);
		}
	}
	
	/**
	 * 设置默认数据
	 * @param b
	 */
	private void setDefaultValue(Contract_change b) {
		String sender = null;
		if (mIsModify) {	// 可修改传递用户ID
			sender = b.getSender_contact() + "";
		} else {	// 不可修改传递用户名
			sender = (mSenderUser == null ? "" : mSenderUser.getName());
		}
		
		mBaseWindow.SetDefaultValue(new String[] {
				b.getContract_code(),
				b.getContract_name(),
				b.getCode(),
				b.getName(),
				DateUtils.dateToString(DateUtils.FORMAT_SHORT, b.getApply_date()),
				DateUtils.dateToString(DateUtils.FORMAT_SHORT, b.getAdjust_finish_date()),
				UtilTools.formatMoney("", b.getBqbgk(), 2),
				String.valueOf(b.getAdjust_period()),
				String.valueOf(b.getSender()),
				String.valueOf(b.getReceiver()),
				sender,
				mRecieverUser == null ? "" : mRecieverUser.getName(),
				b.getMark()
		});
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
	 * 设置接收方联系人
	 * @param reciever
	 */
	public void setRecieverUser(User reciever) {
		mRecieverUser = reciever;
		mBaseWindow.setEditTextContent(11, reciever.getName());
	}
	
	/**
	 * 设置发起方联系人
	 * @param sender
	 */
	public void setSenderUser(User sender) {
		mBaseWindow.setUserTextContent(10, sender.getUser_id());
	}
	
	/**
	 * 更新合同变更信息
	 * @return
	 */
	public Contract_change updateContractChange() {
		return updateContractChange(mContractChange);
	}
	

	/**
	 * 设置变更金额
	 * @param changeMoney
	 */
	public void setContractChangeMoney(double changeMoney) {
		if (changeMoney == 0) {
			mBaseWindow.setEditTextContent(6, "");
		} else {
			mBaseWindow.setEditTextContent(6, changeMoney + "");
		}
	}
	
	/**
	 * 更新合同变更信息
	 * @param contractChange
	 * @return
	 */
	public Contract_change updateContractChange(Contract_change contractChange) {
		Map<String, String> saveMap = mBaseWindow.SaveData();
		
		if (saveMap.get(mContext.getString(R.string.contract_name)) != null
				&& !saveMap.get(mContext.getString(R.string.contract_name)).isEmpty()
				&& saveMap.get(mContext.getString(R.string.contract_change_name)) != null
				&& !saveMap.get(mContext.getString(R.string.contract_change_name)).isEmpty()) {

			contractChange.setContract_code(saveMap.get(mContext.getString(R.string.contract_code)));
			contractChange.setContract_name(saveMap.get(mContext.getString(R.string.contract_name)));
			contractChange.setCode(saveMap.get(mContext.getString(R.string.contract_change_number)));
			contractChange.setName(saveMap.get(mContext.getString(R.string.contract_change_name)));
			if (mContract != null) {
				contractChange.setYdhtwgrq(mContract.getEnd_date());
			}
			contractChange.setApply_date(DateUtils.stringToDate(DateUtils.FORMAT_SHORT, 
						saveMap.get(mContext.getString(R.string.contract_apply_date))));
	
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
			if (mRecieverUser != null) {
				contractChange.setReceive_contact(mRecieverUser.getUser_id());
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
		if (mContract == null) {
			return null;
		}
		
		Contract_change contractChange = new Contract_change();
		
		// 设置默认信息
		contractChange.setProject_id(project.getProject_id());
		contractChange.setTenant_id(UserCache.getTenantId());
		contractChange.setFirst_party(mContract.getFirst_party());
		contractChange.setOwner(mContract.getOwner());
		contractChange.setYshtzj(mContract.getTotal());
		
		// 更新合同
		updateContractChange(contractChange);
		
		return contractChange;
	}
}
