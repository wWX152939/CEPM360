package com.pm360.cepm360.app.module.contract;

import android.annotation.SuppressLint;
import android.os.Bundle;
import android.support.v4.app.Fragment;

import com.pm360.cepm360.R;
import com.pm360.cepm360.app.common.view.parent.BaseSearchView;
import com.pm360.cepm360.app.common.view.parent.BaseSlidingPaneActivity;
import com.pm360.cepm360.common.GLOBAL;
import com.pm360.cepm360.entity.Contract;
import com.pm360.cepm360.entity.Contract_change;
import com.pm360.cepm360.entity.Message;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * 合同管理
 * @author yuanlu
 *
 */
@SuppressLint("UseSparseArrays")
public class ContractActivity extends BaseSlidingPaneActivity {
	
	// 合同变更生效ACTION
	public static final String ACTION_CONTRACT_CHANGE_TAKE_EFFECT 
			= "com.pm360.cepm360.app.module.contract.ACTION.contract.change.take.effect";
	public static final String CONTRACT_CHANGE_TAKE_EFFECT = "contractChangeTakeEffect";
	
	// 顶层Activity请求码定义
	public static final int INCOME_CONTRACT_ADD_REQUEST_CODE = 10;
	public static final int INCOME_CONTRACT_UPDATE_REQUEST_CODE = 11;
	public static final int OUTCOME_CONTRACT_ADD_REQUEST_CODE = 20;
	public static final int OUTCOME_CONTRACT_UPDATE_REQUEST_CODE = 21;
	public static final int CHANGE_CONTRACT_ADD_REQUEST_CODE = 30;
	public static final int CHANGE_CONTRACT_UPDATE_REQUEST_CODE = 31;
	
	/**
	 * 合同操作类型
	 * @author yuanlu
	 *
	 */
	public static enum OperationType {
		INCOME_CONTRACT,
		OUTCOME_CONTRACT,
		CHANGE_CONTRACT
	}
	
	/**
	 * Activity创建入口函数，启动初始化流程
	 */
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		
		// 启动初始化流程
		init(mFragmentManagerInterface, mSearchInterface, false);
		
		super.onCreate(savedInstanceState);
	}
	
	@Override
	protected int getIntentInfo() {
		super.getIntentInfo();
		
		// 消息设置
		Message message = (Message) getIntent()
				.getSerializableExtra(GLOBAL.MSG_OBJECT_KEY);
		if (message != null) {
			mMessage = message;
		}
		
		// 导航索引
		String action = getIntent().getAction();
		if (action.equals(GLOBAL.MSG_CONTRACT_CHANGE_ACTION)) {
			return 3;
		}
		
		return 0;
	}

	/**
	 * Fragment管理接口实现
	 */
	FragmentManagerInterface mFragmentManagerInterface = new FragmentManagerInterface() {

		@Override
		public Class<?>[] getSearchObjectClasses() {
			return new Class[] { 
					Contract.class, 
					Contract.class, 
					Contract_change.class,
					Contract_change.class
			};
		}

		@Override
		public int getNavigationTitleNamesId() {
			return R.array.contract_navigation_titiles;
		}

		@Override
		public int getNavigationIconsId() {
			return R.array.contract_navigation_icons;
		}

		@SuppressWarnings("unchecked")
		@Override
		public Class<? extends Fragment>[] getManagerFragments() {
			return new Class[] { 
				IncomeContractFragment.class,
				OutcomeContractFragment.class, 
				ContractChangeFragment.class,
				ContractChangeApplyFragment.class
			};
		}

		@Override
		public String getHomeTitleName() {
			return getString(R.string.contract_manager);
		}
	};

	/**
	 * 搜索接口实现
	 */
	SearchInterface mSearchInterface = new SearchInterface() {

		@Override
		public List<Map<String, Map<String, String>>> getSpecifiedFieldsList() {
			List<Map<String, Map<String, String>>> specifiedFieldsList = new ArrayList<Map<String, Map<String, String>>>();

			// 材料
			Map<String, Map<String, String>> specifiedFieldsMap = new HashMap<String, Map<String, String>>();
			String[][] typeStrings = GLOBAL.CL_TYPE;
			Map<String, String> typeMap = new HashMap<String, String>();
			// 为物资类型（材料或设备）做映射
			for (int i = 0; i < typeStrings.length; i++) {
				typeMap.put(typeStrings[i][1], typeStrings[i][0]);
				typeMap.put(typeStrings[i][0], typeStrings[i][1]);
			}
			specifiedFieldsMap.put("wz_type_2", typeMap);
			specifiedFieldsList.add(specifiedFieldsMap);

			// 设备
			specifiedFieldsMap = new HashMap<String, Map<String, String>>();
			typeMap = new HashMap<String, String>();
			typeStrings = GLOBAL.SB_TYPE;
			for (int i = 0; i < typeStrings.length; i++) {
				typeMap.put(typeStrings[i][1], typeStrings[i][0]);
				typeMap.put(typeStrings[i][0], typeStrings[i][1]);
			}
			specifiedFieldsMap.put("wz_type_2", typeMap);
			specifiedFieldsList.add(specifiedFieldsMap);

			// 人工外包
			specifiedFieldsMap = new HashMap<String, Map<String, String>>();
			typeMap = new HashMap<String, String>();
			typeStrings = GLOBAL.RG_TYPE;
			for (int i = 0; i < typeStrings.length; i++) {
				typeMap.put(typeStrings[i][1], typeStrings[i][0]);
				typeMap.put(typeStrings[i][0], typeStrings[i][1]);
			}
			specifiedFieldsMap.put("type", typeMap);
			specifiedFieldsList.add(specifiedFieldsMap);
			
			// 人工外包
			specifiedFieldsMap = new HashMap<String, Map<String, String>>();
			typeMap = new HashMap<String, String>();
			typeStrings = GLOBAL.RG_TYPE;
			for (int i = 0; i < typeStrings.length; i++) {
				typeMap.put(typeStrings[i][1], typeStrings[i][0]);
				typeMap.put(typeStrings[i][0], typeStrings[i][1]);
			}
			specifiedFieldsMap.put("type", typeMap);
			specifiedFieldsList.add(specifiedFieldsMap);

			return specifiedFieldsList;
		}

		@Override
		public List<Map<Integer, String[]>> getSearchSupplyData() {
			List<Map<Integer, String[]>> stylesDataList = new ArrayList<Map<Integer, String[]>>();

			// 材料
			Map<Integer, String[]> styleDataMap = new HashMap<Integer, String[]>();
			String[] types = new String[GLOBAL.CL_TYPE.length];
			for (int i = 0; i < GLOBAL.CL_TYPE.length; i++) {
				types[i] = GLOBAL.CL_TYPE[i][1];
			}
			styleDataMap.put(1, types);
			stylesDataList.add(styleDataMap);

			// 设备
			styleDataMap = new HashMap<Integer, String[]>();
			String[] eTypes = new String[GLOBAL.SB_TYPE.length];
			for (int i = 0; i < GLOBAL.SB_TYPE.length; i++) {
				eTypes[i] = GLOBAL.SB_TYPE[i][1];
			}
			styleDataMap.put(1, eTypes);
			stylesDataList.add(styleDataMap);

			// 人工外包
			styleDataMap = new HashMap<Integer, String[]>();
			String[] typeStrings = new String[GLOBAL.RG_TYPE.length];
			for (int i = 0; i < GLOBAL.RG_TYPE.length; i++) {
				typeStrings[i] = GLOBAL.RG_TYPE[i][1];
			}
			styleDataMap.put(0, typeStrings);
			stylesDataList.add(styleDataMap);
			
			// 人工外包
			styleDataMap = new HashMap<Integer, String[]>();
			String[] typeStrings1 = new String[GLOBAL.RG_TYPE.length];
			for (int i = 0; i < GLOBAL.RG_TYPE.length; i++) {
				typeStrings1[i] = GLOBAL.RG_TYPE[i][1];
			}
			styleDataMap.put(0, typeStrings1);
			stylesDataList.add(styleDataMap);

			return stylesDataList;
		}

		@Override
		public List<Map<Integer, Integer>> getSearchStyles() {
			List<Map<Integer, Integer>> stylesList = new ArrayList<Map<Integer, Integer>>();
			// 材料
			Map<Integer, Integer> styleMap = new HashMap<Integer, Integer>();
			styleMap.put(1, BaseSearchView.spinnerLineStyle);
			stylesList.add(styleMap);

			// 设备，和材料相同
			stylesList.add(styleMap);

			// 外包人工
			styleMap = new HashMap<Integer, Integer>();
			styleMap.put(0, BaseSearchView.spinnerLineStyle);
			stylesList.add(styleMap);
			
			// 外包人工
			styleMap = new HashMap<Integer, Integer>();
			styleMap.put(0, BaseSearchView.spinnerLineStyle);
			stylesList.add(styleMap);
			
			return stylesList;
		}

		@Override
		public int[] getSearchLablesId() {
			return new int[] { 
				R.array.material_equipment_search_titles,
				R.array.material_equipment_search_titles,
				R.array.labour_search_titles,
				R.array.labour_search_titles
			};
		}

		@Override
		public String[][] getSearchFields() {
			return new String[][] { 
				{ "name", "wz_type_2", "brand", "spec" },
				{ "name", "wz_type_2", "brand", "spec" },
				{ "type", "work" },
				{ "type", "work" }
			};
		}

		@Override
		public List<List<Integer>> getRelevanceList() {
			List<List<Integer>> relevanceList = new ArrayList<List<Integer>>();
			relevanceList.add(null);
			relevanceList.add(null);
			relevanceList.add(null);
			relevanceList.add(null);
			return relevanceList;
		}

		@Override
		public void doSearch(Object searchCondition) {
			switch (mCurrentPosition) {
				case 0:
					break;
				case 1:
					break;
				case 2:
					break;
				default:
					break;
			}
		}
	};
}
