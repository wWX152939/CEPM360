package com.pm360.cepm360.app.module.resource;

import android.annotation.SuppressLint;
import android.os.Bundle;
import android.support.v4.app.Fragment;

import com.pm360.cepm360.R;
import com.pm360.cepm360.app.common.view.parent.BaseSearchView;
import com.pm360.cepm360.app.common.view.parent.BaseSlidingPaneActivity;
import com.pm360.cepm360.common.GLOBAL;
import com.pm360.cepm360.entity.Experience;
import com.pm360.cepm360.entity.Mavin_lib;
import com.pm360.cepm360.entity.P_LWDW;
import com.pm360.cepm360.entity.P_LWDW_DIR;
import com.pm360.cepm360.entity.P_WBRGNR;
import com.pm360.cepm360.entity.P_WBRGNR_DIR;
import com.pm360.cepm360.entity.P_WZ;
import com.pm360.cepm360.entity.P_WZ_DIR;
import com.pm360.cepm360.entity.P_ZL;
import com.pm360.cepm360.entity.P_ZL_DIR;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@SuppressLint("UseSparseArrays")
public class ResourceActivity extends BaseSlidingPaneActivity {

	/*
	 * (non-Javadoc)
	 * 
	 * @see com.pm360.cepm360.ui.parent.BaseActivity#onCreate(android.os.Bundle)
	 */
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		init(mFragmentManagerInterface, mSearchInterface, false);
		super.onCreate(savedInstanceState);
	}

	FragmentManagerInterface mFragmentManagerInterface = new FragmentManagerInterface() {

		@Override
		public Class<?>[] getSearchObjectClasses() {
			return new Class[] { P_WZ.class, P_WZ.class, P_WBRGNR.class,
					P_ZL.class, P_LWDW.class, Mavin_lib.class, Experience.class };
		}

		@Override
		public int getNavigationTitleNamesId() {
			return R.array.resource_navigation_titles;
		}

		@Override
		public int getNavigationIconsId() {
			return R.array.resource_navigation_icons;
		}

		@SuppressWarnings("unchecked")
		@Override
		public Class<? extends Fragment>[] getManagerFragments() {
			return new Class[] { 
				MaterialFragment.class,
				EquipmentFragment.class, 
				LabourOutSourcingFragment.class,
				LeaseManagementFragment.class,
				ContactCompanyFragment.class, 
				MavinFragment.class,
				RiskControlFragment.class
			};
		}

		@Override
		public String getHomeTitleName() {
			return getString(R.string.resource_management);
		}
	};

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

			// 租赁管理
			specifiedFieldsMap = new HashMap<String, Map<String, String>>();
			typeMap = new HashMap<String, String>();
			typeStrings = GLOBAL.SB_TYPE;
			for (int i = 0; i < typeStrings.length; i++) {
				typeMap.put(typeStrings[i][1], typeStrings[i][0]);
				typeMap.put(typeStrings[i][0], typeStrings[i][1]);
			}
			specifiedFieldsMap.put("zl_type", typeMap);
			specifiedFieldsList.add(specifiedFieldsMap);

			// 往来单位
			specifiedFieldsList.add(null);

			// 专家管理
			specifiedFieldsMap = new HashMap<String, Map<String, String>>();
			typeMap = new HashMap<String, String>();
			typeStrings = GLOBAL.MAVIN_TYPE;
			for (int i = 0; i < typeStrings.length; i++) {
				typeMap.put(typeStrings[i][1], typeStrings[i][0]);
				typeMap.put(typeStrings[i][0], typeStrings[i][1]);
			}
			specifiedFieldsMap.put("type", typeMap);
			specifiedFieldsList.add(specifiedFieldsMap);
			
			// 风控库
			specifiedFieldsList.add(null);

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

			// 租赁管理
			styleDataMap = new HashMap<Integer, String[]>();
			String[] lTypes = new String[GLOBAL.SB_TYPE.length];
			for (int i = 0; i < GLOBAL.SB_TYPE.length; i++) {
				lTypes[i] = GLOBAL.SB_TYPE[i][1];
			}
			styleDataMap.put(0, lTypes);
			stylesDataList.add(styleDataMap);

			// 往来单位
			stylesDataList.add(null);
			
			// 专家管理
			styleDataMap = new HashMap<Integer, String[]>();
			String[] mavinStrings = new String[GLOBAL.MAVIN_TYPE.length];
			for (int i = 0; i < GLOBAL.MAVIN_TYPE.length; i++) {
				mavinStrings[i] = GLOBAL.MAVIN_TYPE[i][1];
			}
			styleDataMap.put(0, mavinStrings);
			stylesDataList.add(styleDataMap);
			
			// 风控库
			stylesDataList.add(null);

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

			// 租赁管理
			styleMap = new HashMap<Integer, Integer>();
			styleMap.put(0, BaseSearchView.spinnerLineStyle);
			stylesList.add(styleMap);

			// 往来单位
			stylesList.add(null);
			
			// 专家管理
			styleMap = new HashMap<Integer, Integer>();
			styleMap.put(0, BaseSearchView.spinnerLineStyle);
			stylesList.add(styleMap);
			
			// 风控库
			stylesList.add(null);
			
			return stylesList;
		}

		@Override
		public int[] getSearchLablesId() {
			return new int[] { 
				R.array.material_equipment_search_titles,
				R.array.material_equipment_search_titles,
				R.array.labour_search_titles, 
				R.array.lease_search_titles,
				R.array.contact_search_titles,
				R.array.mavin_search_titles,
				R.array.risk_control_search_titles
			};
		}

		@Override
		public String[][] getSearchFields() {
			return new String[][] { 
				{ "name", "wz_type_2", "brand", "spec" },
				{ "name", "wz_type_2", "brand", "spec" },
				{ "type", "work" }, 
				{ "zl_type", "name" }, 
				{ "name" },
				{"type", "name"},
				{"title", "content"},
			};
		}

		@Override
		public List<List<Integer>> getRelevanceList() {
			List<List<Integer>> relevanceList = new ArrayList<List<Integer>>();
			relevanceList.add(null);
			relevanceList.add(null);
			relevanceList.add(null);
			relevanceList.add(null);
			relevanceList.add(null);
			relevanceList.add(null);
			relevanceList.add(null); // 风控库
			return relevanceList;
		}

		@Override
		public void doSearch(Object searchCondition) {
			switch (mCurrentPosition) {
			case 0:
				MaterialFragment materialFragment = (MaterialFragment) mCurrentFragment;
				P_WZ_DIR wzDir = materialFragment.getCurrentTreeNode();
				localMaterialEquipmentSearch(true, wzDir,
						(P_WZ) searchCondition);
				break;
			case 1:
				EquipmentFragment equipmentFragment = (EquipmentFragment) mCurrentFragment;
				P_WZ_DIR ewzDir = equipmentFragment.getCurrentTreeNode();
				localMaterialEquipmentSearch(false, ewzDir,
						(P_WZ) searchCondition);
				break;
			case 2:
				LabourOutSourcingFragment labourOutSourcingFragment = (LabourOutSourcingFragment) mCurrentFragment;
				P_WBRGNR_DIR labourDir = labourOutSourcingFragment
						.getCurrentTreeNode();
				localLabourSearch(labourDir, (P_WBRGNR) searchCondition);
				break;
			case 3:
				LeaseManagementFragment leasemanagementfragment = (LeaseManagementFragment) mCurrentFragment;
				P_ZL_DIR leaseDir = leasemanagementfragment
						.getCurrentTreeNode();
				localLeaseSearch(leaseDir, (P_ZL) searchCondition);
				break;
			case 4:
				ContactCompanyFragment contactCompanyFragment = (ContactCompanyFragment) mCurrentFragment;
				P_LWDW_DIR contactDir = contactCompanyFragment
						.getCurrentTreeNode();
				localContactSearch(contactDir, (P_LWDW) searchCondition);
				break;
			case 5:
				localmavinSearch((Mavin_lib) searchCondition);
				break;
			case 6:
				localRiskControlSearch((Experience) searchCondition);
				break;
			default:
				break;
			}
		}
	};

	/**
	 * 查询当前目录列表下的指定项
	 * 
	 * @param isMaterial
	 * @param treeNode
	 * @param searchCondition
	 */
	private void localMaterialEquipmentSearch(boolean isMaterial,
			P_WZ_DIR treeNode, P_WZ searchCondition) {

		List<P_WZ> showList = null;
		if (isMaterial) {
			showList = ((MaterialFragment) mCurrentFragment)
					.getContentsFromTree(treeNode);
		} else {
			showList = ((EquipmentFragment) mCurrentFragment)
					.getContentsFromTree(treeNode);
		}
		List<P_WZ> filterList = new ArrayList<P_WZ>();
		for (P_WZ item : showList) {
			boolean selected = false;
			if (searchCondition.getName() != null
					&& !searchCondition.getName().equals("")) {
				if (searchCondition.getName().equals(item.getName())) {
					selected = true;
				} else {
					continue;
				}
			}
			if (searchCondition.getWz_type_2() != 0) {
				if (searchCondition.getWz_type_2() == item.getWz_type_2()) {
					selected = true;
				} else {
					continue;
				}
			}
			if (searchCondition.getBrand() != null
					&& !searchCondition.getBrand().equals("")) {
				if (searchCondition.getBrand().equals(item.getBrand())) {
					selected = true;
				} else {
					continue;
				}
			}
			if (searchCondition.getSpec() != null
					&& !searchCondition.getSpec().equals("")) {
				if (searchCondition.getSpec().equals(item.getSpec())) {
					selected = true;
				} else {
					continue;
				}
			}

			if (selected) {
				filterList.add(item);
			}
		}

		if (isMaterial) {
			((MaterialFragment) mCurrentFragment).setDataShowList(filterList);
		} else {
			((EquipmentFragment) mCurrentFragment).setDataShowList(filterList);
		}
	}

	/**
	 * 查询当前目录列表下的指定项
	 * 
	 * @param treeNode
	 * @param searchCondition
	 */
	private void localLabourSearch(P_WBRGNR_DIR treeNode,
			P_WBRGNR searchCondition) {

		List<P_WBRGNR> showList = ((LabourOutSourcingFragment) mCurrentFragment)
				.getContentsFromTree(treeNode);
		List<P_WBRGNR> filterList = new ArrayList<P_WBRGNR>();
		for (P_WBRGNR item : showList) {
			boolean selected = false;
			if (searchCondition.getWork() != null
					&& !searchCondition.getWork().equals("")) {
				if (searchCondition.getWork().equals(item.getWork())) {
					selected = true;
				} else {
					continue;
				}
			}
			if (searchCondition.getType() != 0) {
				if (searchCondition.getType() == item.getType()) {
					selected = true;
				} else {
					continue;
				}
			}

			if (selected) {
				filterList.add(item);
			}
		}

		((LabourOutSourcingFragment) mCurrentFragment)
				.setDataShowList(filterList);
	}

	/**
	 * 查询当前目录列表下的指定项
	 * 
	 * @param treeNode
	 * @param searchCondition
	 */
	private void localLeaseSearch(P_ZL_DIR treeNode, P_ZL searchCondition) {

		List<P_ZL> showList = ((LeaseManagementFragment) mCurrentFragment)
				.getContentsFromTree(treeNode);
		List<P_ZL> filterList = new ArrayList<P_ZL>();
		for (P_ZL item : showList) {
			boolean selected = false;
			if (searchCondition.getName() != null
					&& !searchCondition.getName().equals("")) {
				if (searchCondition.getName().equals(item.getName())) {
					selected = true;
				} else {
					continue;
				}
			}

			if (searchCondition.getZl_type() != 0) {
				if (searchCondition.getZl_type() == item.getZl_type()) {
					selected = true;
				} else {
					continue;
				}
			}

			if (selected) {
				filterList.add(item);
			}
		}

		((LeaseManagementFragment) mCurrentFragment)
				.setDataShowList(filterList);
	}

	/**
	 * 查询当前目录列表下的指定项
	 * 
	 * @param treeNode
	 * @param searchCondition
	 */
	private void localContactSearch(P_LWDW_DIR treeNode, P_LWDW searchCondition) {

		List<P_LWDW> showList = ((ContactCompanyFragment) mCurrentFragment)
				.getContentsFromTree(treeNode);
		List<P_LWDW> filterList = new ArrayList<P_LWDW>();
		for (P_LWDW item : showList) {
			boolean selected = false;
			if (searchCondition.getName() != null
					&& !searchCondition.getName().equals("")) {
				if (searchCondition.getName().equals(item.getName())) {
					selected = true;
				} else {
					continue;
				}
			}

			if (selected) {
				filterList.add(item);
			}
		}

		((ContactCompanyFragment) mCurrentFragment).setDataShowList(filterList);
	}
	
	private void localmavinSearch(Mavin_lib searchCondition) {

		List<Mavin_lib> showList = ((MavinFragment) mCurrentFragment).getListAdapter().getDataList();
		List<Mavin_lib> filterList = new ArrayList<Mavin_lib>();
		for (Mavin_lib item : showList) {
			boolean selected = false;
			if (searchCondition.getName() != null
					&& !searchCondition.getName().equals("")) {
				if (searchCondition.getName().equals(item.getName())) {
					selected = true;
				} else {
					continue;
				}
			}
			if (searchCondition.getType() != 0) {
				if (searchCondition.getType() == item.getType()) {
					selected = true;
				} else {
					continue;
				}
			}

			if (selected) {
				filterList.add(item);
			}
		}
 		((MavinFragment) mCurrentFragment).getListAdapter().setShowDataList(filterList);
	}
	
	private void localRiskControlSearch(Experience experience) {
		
	}
	
}
