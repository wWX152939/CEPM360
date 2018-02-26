package com.pm360.cepm360.app.cache;

import android.annotation.SuppressLint;

import com.pm360.cepm360.app.common.activity.DataManagerInterface;
import com.pm360.cepm360.app.common.custinterface.CallBack;
import com.pm360.cepm360.common.ResultStatus;
import com.pm360.cepm360.common.json.AnalysisManager;
import com.pm360.cepm360.entity.OBS;
import com.pm360.cepm360.entity.Tenant;
import com.pm360.cepm360.services.common.RemoteCommonService;
import com.pm360.cepm360.services.system.RemoteOBSService;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * 
 * Obs缓存
 * @author yuanlu
 *
 */
@SuppressLint("UseSparseArrays") 
public class CacheObs {
	
	// 所有合作公司的大小
	private static int mTenantSize;

	// 公司和部门映射表
	private static Map<Integer, List<OBS>> mTenantObsMap;
	
	// 部门ID和部门映射表
	private static Map<String, OBS> mIdObsMap;
	
	// 部门ID和名称映射表
	private static Map<String, String> mObsIdNameMap;
	
	// 初始化空间
	static {
		mTenantObsMap = new HashMap<Integer, List<OBS>>();
		mIdObsMap = new HashMap<String, OBS>();
		mObsIdNameMap = new HashMap<String, String>();
	}
	
	/**
	 * 获取所有缓存的OBS对象列表
	 * @return
	 */
	public static List<OBS> getAllObs() {
		List<OBS> obsList = new ArrayList<OBS>();
		for (Map.Entry<Integer, List<OBS>> entry 
							: mTenantObsMap.entrySet()) {
			obsList.addAll(entry.getValue());
		}
		
		return obsList;
	}
	
	/**
	 * 获取指定公司部门信息
	 * @param tenantId	指定的公司ID
	 * @return
	 */
	public static List<OBS> getTenantObs(final int tenantId) {
		return mTenantObsMap.get(tenantId);
	}
	
	/**
	 * 获取指定公司部门信息
	 * @param tenantId	指定的公司ID
	 * @param callBack	不能为null，本方法不做null检查，
	 * 					因为要获取到数据，callBack是唯一返回的地方
	 */
	public static void getTenantObs(final int tenantId, 
						final CallBack<Void, List<OBS>> callBack) {
		getTenantObs(tenantId, callBack, false);
	}
	
	/**
	 * 获取公司部门列表
	 * @param tenantId 公司ID
	 * @param callBack 回调接口
	 * @param forceCallBack 是否强制回调用户接口
	 */
	public static void getTenantObs(final int tenantId,
			final CallBack<Void, List<OBS>> callBack, boolean forceReload) {

		// 不强制重新加载数据，如果找到缓存的数据直接返回
		if (!forceReload) {
			if (doGetTenantObs(tenantId, callBack, false)) {
				return;
			}
		}

		// 为找到缓存数据或者强制重新载入数据，加载并返回数据
		load(tenantId, new CallBack<Void, Integer>() {

			@Override
			public Void callBack(Integer a) {

				/*
				 * 已经重新加载数据，即使没有查询到数据也不会再次载入数据 找不到数据原因有两个：
				 *  1. 加载失败 2. 数据库没有想要的数据
				 */
				switch (a) {
					case AnalysisManager.SUCCESS_DB_QUERY: // 服务器查询成功
						doGetTenantObs(tenantId, callBack, true);
						break;
	
					// 服务器查询失败，直接调用用户回调接口
					case AnalysisManager.EXCEPTION_DB_QUERY:
						callBack.callBack(null);
						break;
				}
				return null;
			}
		}, forceReload);
	}

	/**
	 * 缓存中查找指定项目的协作单位列表
	 * 
	 * @param tenantId 公司ID
	 * @param callBack 回调接口
	 * @param forceCallBack 是否强制回调用户接口
	 * @return 是否查找到数据： 1. true 查询到数据或者强制回调， 2. false 不强制回调并没有找到数据
	 */
	private static boolean doGetTenantObs(int tenantId,
			CallBack<Void, List<OBS>> callBack, boolean forceCallBack) {
		List<OBS> tenants = mTenantObsMap.get(tenantId);

		// 回调用户接口
		return doCallBack(tenants, callBack, forceCallBack);
	}
	
	/**
	 * 获取指定公司部门信息
	 * @param obsId	指定的部门ID
	 * @return
	 */
	public static OBS getObs(final int obsId) {
		return mIdObsMap.get(obsId);
	}
	
	/**
	 * 获取指定公司部门信息
	 * @param obsId	指定的部门ID
	 * @param callBack	不能为null，本方法不做null检查，
	 * 					因为要获取到数据，callBack是唯一返回的地方
	 */
	public static void getObs(final int obsId, 
						final CallBack<Void, OBS> callBack) {
		getObs(obsId, callBack, false);
	}
	
	/**
	 * 获取公司部门信息
	 * @param obsId 部门ID
	 * @param callBack 回调接口
	 * @param forceCallBack 是否强制回调用户接口
	 */
	public static void getObs(final int obsId,
			final CallBack<Void, OBS> callBack, boolean forceReload) {

		// 不强制重新加载数据，如果找到缓存的数据直接返回
		if (!forceReload) {
			if (doGetObs(obsId, callBack, false)) {
				return;
			}
		}

		// 为找到缓存数据或者强制重新载入数据，加载并返回数据
		load(new CallBack<Void, Integer>() {

			@Override
			public Void callBack(Integer a) {

				/*
				 * 已经重新加载数据，即使没有查询到数据也不会再次载入数据 找不到数据原因有两个：
				 *  1. 加载失败 2. 数据库没有想要的数据
				 */
				switch (a) {
					case AnalysisManager.SUCCESS_DB_QUERY: // 服务器查询成功
						doGetObs(obsId, callBack, true);
						break;
	
					// 服务器查询失败，直接调用用户回调接口
					case AnalysisManager.EXCEPTION_DB_QUERY:
						callBack.callBack(null);
						break;
				}
				return null;
			}
		}, forceReload);
	}

	/**
	 * 缓存中查找指定部门ID的部门信息
	 * 
	 * @param obsId 部门ID
	 * @param callBack 回调接口
	 * @param forceCallBack 是否强制回调用户接口
	 * @return 是否查找到数据： 1. true 查询到数据或者强制回调，
	 *                         2. false 不强制回调并没有找到数据
	 */
	private static boolean doGetObs(int obsId,
			CallBack<Void, OBS> callBack, boolean forceCallBack) {
		OBS obs = mIdObsMap.get(obsId + "");

		// 回调用户接口
		return doCallBack(obs, callBack, forceCallBack);
	}
	
	/**
	 * 获取指定公司部门信息
	 * @param obsId	指定的公司ID
	 */
	public static String getObsName(final int obsId) {
		return mObsIdNameMap.get(obsId);
	}
	
	/**
	 * 获取指定公司部门信息
	 * @param obsId	指定的公司ID
	 * @param callBack	不能为null，本方法不做null检查，
	 * 					因为要获取到数据，callBack是唯一返回的地方
	 */
	public static void getObsName(final int obsId, 
						final CallBack<Void, String> callBack) {
		getObsName(obsId, callBack, false);
	}
	
	/**
	 * 获取公司部门信息
	 * @param obsId 部门ID
	 * @param callBack 回调接口
	 * @param forceCallBack 是否强制回调用户接口
	 */
	public static void getObsName(final int obsId,
			final CallBack<Void, String> callBack, boolean forceReload) {

		// 不强制重新加载数据，如果找到缓存的数据直接返回
		if (!forceReload) {
			if (doGetObsName(obsId, callBack, false)) {
				return;
			}
		}

		// 为找到缓存数据或者强制重新载入数据，加载并返回数据
		load(new CallBack<Void, Integer>() {

			@Override
			public Void callBack(Integer a) {

				/*
				 * 已经重新加载数据，即使没有查询到数据也不会再次载入数据 找不到数据原因有两个：
				 *  1. 加载失败 2. 数据库没有想要的数据
				 */
				switch (a) {
					case AnalysisManager.SUCCESS_DB_QUERY: // 服务器查询成功
						doGetObsName(obsId, callBack, true);
						break;
	
					// 服务器查询失败，直接调用用户回调接口
					case AnalysisManager.EXCEPTION_DB_QUERY:
						callBack.callBack(null);
						break;
				}
				return null;
			}
		}, forceReload);
	}

	/**
	 * 缓存中查找指定部门ID的部门信息
	 * 
	 * @param obsId 部门ID
	 * @param callBack 回调接口
	 * @param forceCallBack 是否强制回调用户接口
	 * @return 是否查找到数据： 1. true 查询到数据或者强制回调，
	 *                         2. false 不强制回调并没有找到数据
	 */
	private static boolean doGetObsName(int obsId,
			CallBack<Void, String> callBack, boolean forceCallBack) {
		String obsName = mObsIdNameMap.get(obsId + "");

		// 回调用户接口
		return doCallBack(obsName, callBack, forceCallBack);
	}
	
	/**
	 * 回调用户接口
	 * @param t	要返回的数据
	 * @param callBack	回调接口
	 * @param forceCallBack	是否强制回调
	 * @return
	 */
	private static <T> boolean doCallBack(T t, 
					CallBack<?, T> callBack, boolean forceCallBack) {
		
		// 强制回调，不管是否查询到缓存数据都会回调用户接口
		if (forceCallBack) {
			callBack.callBack(t);
			return true;
			
		// 不强制回调，只有查询到数据时才会回调用户接口
		} else {
			if (t != null) {
				callBack.callBack(t);
				return true;
			} else {
				return false;
			}
		}
	}
	
	/**
	 * 添加OBS列表
	 * @param tenantId
	 * @param obsList
	 */
	private static void addObsList(int tenantId, List<OBS> obsList) {
		mTenantObsMap.put(tenantId, obsList);
		
		for (int i = 0; i < obsList.size(); i++) {
			OBS obs = obsList.get(i);
			
			// 添加到ID到对象映射
			mIdObsMap.put(obs.getTenant_id() + "", obs);
			
			// 添加到ID名称映射
			mObsIdNameMap.put(obs.getObs_id() + "", obs.getName());
		}
	}
	
	/**
	 * 加载本公司和所有协作公司的OBS列表，注意该方法的使用场景，见下面函数注释
	 * @param forceReload
	 */
	public static void load(boolean forceReload) {
		load(null, forceReload);
	}
	
	/**
	 * 加载本公司和所有协作公司的OBS列表
	 * @param callBack 可以为null, 如果为null一般用于启动软件后，先加载数据以备后用
	 * 					，如果调用该方法后要等待完成加载直接使用，不能为null
	 * @param forceReload 是否强制重载
	 */
	public static void load(final CallBack<Void, Integer> callBack,
			final boolean forceReload) {
		
		// 强制重载数据，首先清空数据
		if (forceReload) {
			clear();
		}
		
		load(UserCache.getTenantId(), new CallBack<Void, Integer>() {

			@Override
			public Void callBack(Integer a) {
				loadCooperationObs(callBack, forceReload);
				return null;
			}
		}, forceReload);
	}
	
	/**
	 * 加载所有协作公司的OBS
	 * @param callBack
	 */
	private static void loadCooperationObs(final CallBack<Void, Integer> callBack, 
			final boolean forceReload) {
		Tenant tenant = new Tenant();
		tenant.setTenant_id(UserCache.getTenantId());
		RemoteCommonService.getInstance()
					.getAllCooperationTenantList(new DataManagerInterface() {
			
			@Override
			public void getDataOnResult(ResultStatus status, List<?> list) {
				switch (status.getCode()) {
					case AnalysisManager.SUCCESS_DB_QUERY:
						mTenantSize = list.size();
						if (!list.isEmpty()) {
							for (int i = 0; i < list.size(); i++) {
								load(((Tenant) list.get(i)).getTenant_id(), 
													new CallBack<Void, Integer>() {

									@Override
									public Void callBack(Integer a) {
										if (--mTenantSize == 0) {
											if (callBack != null) {
												callBack.callBack(a);
											}
										}
										return null;
									}
								}, forceReload);
							}
						}
						break;
					default:
						break;
				}
			}
		}, tenant);
	}
	

	/**
	 * 加载数据，即使已经加载也会再次加载，用户可能就是要重新加载数据，
	 * 因此是否强制加载在调用函数中处理
	 * @param tenantId
	 * @param callBack
	 * @param forceReload
	 */
	public static void load(final int tenantId, 
			final CallBack<Void, Integer> callBack, boolean forceReload) {
		if (forceReload || !mTenantObsMap.containsKey(tenantId)) {
			RemoteOBSService.getInstance().getOBSList(new DataManagerInterface() {
					
				@SuppressWarnings("unchecked")
				@Override
				public void getDataOnResult(ResultStatus status, List<?> list) {
					switch (status.getCode()) {
						case AnalysisManager.SUCCESS_DB_QUERY:
							if (!list.isEmpty()) {
								addObsList(tenantId, (List<OBS>) list);
							}
							break;
						default:
							break;
					}
					
					callBack.callBack(status.getCode());
				}
			}, tenantId);
		}
	}
	
	/**
	 * 清空数据，恢复原始状态
	 */
	private static void clear() {
		mTenantObsMap.clear();
		mIdObsMap.clear();
		mObsIdNameMap.clear();
		mTenantSize = 0;
	}
}
