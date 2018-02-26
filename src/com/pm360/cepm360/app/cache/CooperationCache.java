package com.pm360.cepm360.app.cache;

import android.annotation.SuppressLint;

import com.pm360.cepm360.app.common.activity.DataManagerInterface;
import com.pm360.cepm360.app.common.custinterface.CallBack;
import com.pm360.cepm360.common.ResultStatus;
import com.pm360.cepm360.common.json.AnalysisManager;
import com.pm360.cepm360.entity.Tenant;
import com.pm360.cepm360.entity.User;
import com.pm360.cepm360.services.common.RemoteCommonService;
import com.pm360.cepm360.services.system.RemoteUserService;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * 协作单位和协作单位联系人缓存，为了确保使用简单，本实现将扩展功能对外关闭
 * 这里需要注意两点：
 * 1. 所有获取缓存数据的方式都是通过回调用户接口返回，也就是说不会在调用
 * 	    获取数据方法的返回值得到数据
 * 2. 如果没有查询到数据，或者服务器请求失败都会返回null，用户不必区分这两者
 * 	   的区别，对于用户来说，只要请求数据只有两种情况：
 * 		（1）可以正常获取到
 * 		（2）已经尝试了所有可能，但还是获取不到，只能和后台调试
 * @author yuanlu
 */
@SuppressLint("UseSparseArrays") 
public class CooperationCache {
	
	// 第一层关系，项目ID和对应的协作单位列表的映射
	private static Map<Integer, List<Tenant>> mProjectCooperationTenantsMap;
	
	// 第二层关系，项目ID和协作单位ID的拼接字符串到协作单位的映射
	private static Map<String, Tenant> mCooperationIdTenantMap;
	
	// 第三层关系，用于保存协作ID到用户列表的映射
	private static Map<Integer, List<User>> mCooperationContactsMap;
	
	// 初始化映射表
	static {
		mProjectCooperationTenantsMap = new HashMap<Integer, List<Tenant>>();
		mCooperationIdTenantMap = new HashMap<String, Tenant>();
		mCooperationContactsMap = new HashMap<Integer, List<User>>();
	}
	
	/**
	 * 获取指定项目的协作单位列表
	 * @param projectId 指定项目ID
	 * @param callBack 回调接口
	 * @return 如果没有缓存，返回null
	 */
	public static void getTenants(int projectId, CallBack<Void, List<Tenant>> callBack) {
		getTenants(projectId, callBack, false);
	}
	
	/**
	 * 获取指定项目的协作单位列表，如果没有查到会加载数据通过callBack接口通知用户
	 * @param projectId	指定项目ID
	 * @param callBack 回调接口
	 * @param forceReload 是否强制重新加载数据， 该标志一般用于修改了本类相关缓存
	 * 			数据的强制重载
	 * @return 如果没有缓存，返回null
	 */
	public static void getTenants(final int projectId, 
					final CallBack<Void, List<Tenant>> callBack, boolean forceReload) {
		
		// 不强制重新加载数据，如果找到缓存的数据直接返回
		if (!forceReload) {
			if (doGetTenants(projectId, callBack, false)) {
				return;
			}
		}
		
		// 为找到缓存数据或者强制重新载入数据，加载并返回数据
		loadCooperationTanents(projectId, new CallBack<Void, Integer>() {
			
			@Override
			public Void callBack(Integer a) {
				
				/*
				 * 已经重新加载数据，即使没有查询到数据也不会再次载入数据
				 * 找不到数据原因有两个：
				 * 1. 加载失败
				 * 2. 数据库没有想要的数据
				 */
				switch (a) {
				
					// 服务器查询成功
					case AnalysisManager.SUCCESS_DB_QUERY:
						doGetTenants(projectId, callBack, true);
						break;
						
					// 服务器查询失败，直接调用用户回调接口
					case AnalysisManager.EXCEPTION_DB_QUERY:
						callBack.callBack(null);
						break;
						
					default:
						break;
				}
				
				return null;
			}
		});
	}
	
	/**
	 * 缓存中查找指定项目的协作单位列表
	 * @param projectId	项目ID
	 * @param callBack	回调接口
	 * @param forceCallBack 是否强制回调用户接口
	 * @return 是否查找到数据：
	 * 			1. true 查询到数据或者强制回调，
	 * 			2. false 不强制回调并没有找到数据
	 */
	private static boolean doGetTenants(int projectId, 
					CallBack<Void, List<Tenant>> callBack, boolean forceCallBack) {
		List<Tenant> tenants = mProjectCooperationTenantsMap.get(projectId);
		
		// 回调用户接口
		return doCallBack(tenants, callBack, forceCallBack);
	}
	
	/**
	 * 添加指定项目的协作单位列表到映射表
	 * @param projectId	指定项目ID
	 * @param tenants	协作单位列表
	 */
	private static void addTenants(int projectId, List<Tenant> tenants) {
		
		// 建立项目ID到协作单位列表的映射
		if (!mProjectCooperationTenantsMap.containsKey(projectId)) {
			mProjectCooperationTenantsMap.put(projectId, tenants);
			
			// 建立协作ID到协作单位的映射
			for (int i = 0; i < tenants.size(); i++) {
				Tenant tenant = tenants.get(i);
				String key = projectId + "-" + tenant.getTenant_id();
				mCooperationIdTenantMap.put(key, tenant);
			}
		}
	}
	
	/**
	 * 获取项目ID和协作单位ID的协作单位信息
	 * @param projectId 指定项目ID
	 * @param tenantId	指定该项目下某个协作单位ID
	 * @return 如果没有缓存，返回null
	 */
	public static void getTenant(int projectId, int tenantId, 
					CallBack<Void, Tenant> callBack) {
		getTenant(projectId, tenantId, callBack, false);
	}
	
	/**
	 * 获取项目ID和协作单位ID的协作单位信息，如果没有查到会加载数据通过callBack接口通知用户
	 * @param projectId 指定项目ID
	 * @param tenantId	指定该项目下某个协作单位ID
	 * @param callBack 回调接口
	 * @param forceReload 是否强制重新载入数据
	 * @return 如果没有缓存，返回null
	 */
	public static void getTenant(final int projectId, final int tenantId, 
					final CallBack<Void, Tenant> callBack, boolean forceReload) {
		
		// 不强制重新加载数据，如果找到缓存的数据直接返回
		if (!forceReload) {
			if (doGetTenant(projectId, tenantId, callBack, false)) {
				return;
			}
		}
		
		// 为找到缓存数据或者强制重新载入数据，加载并返回数据
		loadCooperationTanents(projectId, new CallBack<Void, Integer>() {
			
			@Override
			public Void callBack(Integer a) {
				
				/*
				 * 已经重新加载数据，即使没有查询到数据也不会再次载入数据
				 * 找不到数据原因有两个：
				 * 1. 加载失败
				 * 2. 数据库没有想要的数据
				 */
				switch (a) {
				
					// 服务器查询成功
					case AnalysisManager.SUCCESS_DB_QUERY:
						doGetTenant(projectId, tenantId, callBack, true);
						break;
						
					// 服务器查询失败，直接调用用户回调接口
					case AnalysisManager.EXCEPTION_DB_QUERY:
						callBack.callBack(null);
						break;
						
					default:
						break;
				}
				
				return null;
			}
		});
	}
	
	/**
	 * 缓存中查找指定项目和协作单位ID的协作单位详情
	 * @param projectId	项目ID
	 * @param tenantId	协作单位ID
	 * @param callBack	用户回调接口
	 * @param forceCallBack	是否强制回调
	 * @return @return 是否查找到数据：
	 * 			1. true 查询到数据或者强制回调，
	 * 			2. false 不强制回调并没有找到数据
	 */
	public static boolean doGetTenant(int projectId, int tenantId, 
					CallBack<Void, Tenant> callBack, boolean forceCallBack) {
		Tenant tenant = mCooperationIdTenantMap.get(projectId + "-" + tenantId);
		
		// 回调用户接口
		return doCallBack(tenant, callBack, forceCallBack);
	}
	
	/**
	 * 获取指定项目ID和协作单位ID的联系人列表
	 * @param projectId 项目ID
	 * @param tenantId 协作单位ID
	 * @param callBack 用户回调接口
	 * @return 项目和协作单位对应的用户（相关联系人），如果没有
	 * 			缓存该组数据，返回null
	 */
	public static void getContacts(int projectId, int tenantId, 
			CallBack<Void, List<User>> callBack) {
		getContacts(projectId, tenantId, callBack, false);
	}
	
	/**
	 * 获取指定项目ID和协作单位ID的联系人列表，如果没有查到会加载数据通过callBack接口通知用户
	 * @param projectId 项目ID
	 * @param tenantId 协作单位ID
	 * @param callBack 回调接口
	 * @param forceReload 是否强制重新载入数据
	 * @return 项目和协作单位对应的用户（相关联系人），如果没有
	 * 			缓存该组数据，返回null
	 */
	public static void getContacts(final int projectId, final int tenantId, 
					final CallBack<Void, List<User>> callBack, boolean forceReload) {
		
		// 不强制重新加载数据，如果找到缓存的数据直接返回
		if (!forceReload) {
			if (doGetContacts(projectId, tenantId, callBack, false)) {
				return;
			}
		}
		
		// 为找到缓存数据或者强制重新载入数据，加载并返回数据
		loadCooperationTanantContacts(projectId, tenantId, new CallBack<Void, Integer>() {
			
			@Override
			public Void callBack(Integer a) {
				
				/*
				 * 已经重新加载数据，即使没有查询到数据也不会再次载入数据
				 * 找不到数据原因有两个：
				 * 1. 加载失败
				 * 2. 数据库没有想要的数据
				 */
				switch (a) {
				
					// 服务器查询成功
					case AnalysisManager.SUCCESS_DB_QUERY:
						doGetContacts(projectId, tenantId, callBack, true);
						break;
						
					// 服务器查询失败，直接调用用户回调接口
					case AnalysisManager.EXCEPTION_DB_QUERY:
						callBack.callBack(null);
						break;
						
					default:
						break;
				}
				
				return null;
			}
		});
	}
	
	/**
	 * 缓存中查找指定项目和协作单位联系人列表
	 * @param projectId 项目ID
	 * @param tenantId	协作单位ID
	 * @param callBack	用户回调接口
	 * @param forceCallBack	是否强制回调
	 * @return 是否查找到数据：
	 * 			1. true 查询到数据或者强制回调，
	 * 			2. false 不强制回调并没有找到数据
	 */
	private static boolean doGetContacts(int projectId, int tenantId, 
							final CallBack<Void, List<User>> callBack, 
							final boolean forceCallBack) {
		Tenant tenant = mCooperationIdTenantMap.get(projectId + "-" + tenantId);
		List<User> contacts = null;
		if (tenant != null) {
			contacts = mCooperationContactsMap.get(tenant.getCooperation_id());
		}
		
		// 回调用户接口
		return doCallBack(contacts, callBack, forceCallBack);
	}
	
	/**
	 * 添加协作单位和联系人信息
	 * @param cooperationId 协作单位
	 * @param users	协作单位联系人列表
	 */
	private static void addContacts(int cooperationId, List<User> users) {
		
		// 建立协作ID到联系人列表的映射
		if (!mCooperationContactsMap.containsKey(cooperationId)) {
			mCooperationContactsMap.put(cooperationId, users);
		}
	}
	
	/**
	 * 获取指定条件的联系人
	 * @param projectId 项目ID
	 * @param tenantId	协作单位ID
	 * @param userId	用户ID
	 * @param callBack	用户回调接口
	 * @return	如果返回null，说明没有缓存该数据
	 */
	public static void getContact(int projectId, int tenantId, int userId,
			CallBack<Void, User> callBack) {
		getContact(projectId, tenantId, userId, callBack, false);
	}
	
	/**
	 * 获取指定条件的联系人
	 * @param projectId 项目ID
	 * @param tenantId	协作单位ID
	 * @param userId	用户ID
	 * @param callBack	回调接口
	 * @param forceReload 是否强制重载数据
	 * @return	如果返回null，说明没有缓存该数据
	 */
	public static void getContact(final int projectId, 
								final int tenantId, 
								final int userId,
								final CallBack<Void, User> callBack, 
								boolean forceReload) {
		
		// 不强制重新加载数据，如果找到缓存的数据直接返回
		if (!forceReload) {
			if (doGetContact(projectId, tenantId, userId, callBack, false)) {
				return;
			}
		}
		
		// 为找到缓存数据或者强制重新载入数据，加载并返回数据
		loadCooperationTanantContacts(projectId, tenantId, new CallBack<Void, Integer>() {
			
			@Override
			public Void callBack(Integer a) {
				
				/*
				 * 已经重新加载数据，即使没有查询到数据也不会再次载入数据
				 * 找不到数据原因有两个：
				 * 1. 加载失败
				 * 2. 数据库没有想要的数据
				 */
				switch (a) {
				
					// 服务器查询成功
					case AnalysisManager.SUCCESS_DB_QUERY:
						doGetContact(projectId, tenantId, userId, callBack, true);
						break;
						
					// 服务器查询失败，直接调用用户回调接口
					case AnalysisManager.EXCEPTION_DB_QUERY:
						callBack.callBack(null);
						break;
						
					default:
						break;
				}
				
				return null;
			}
		});
	}
	
	/**
	 * 缓存中查找指定项目和协作单位的指定联系人
	 * @param projectId 项目ID
	 * @param tenantId	协作单位ID
	 * @param userId	联系人ID
	 * @param callBack	用户回调接口
	 * @param forceCallBack 是否强制回调
	 * @return 是否查找到数据：
	 * 			1. true 查询到数据或者强制回调，
	 * 			2. false 不强制回调并没有找到数据
	 */
	private static boolean doGetContact(final int projectId, 
								final int tenantId, 
								int userId,
								final CallBack<Void, User> callBack, 
								boolean forceCallBack) {
		
		Tenant tenant = mCooperationIdTenantMap.get(projectId + "-" + tenantId);
		User user = null;
		if (tenant != null) {
			List<User> contacts = mCooperationContactsMap.get(tenant.getCooperation_id());
			user = getContact(userId, contacts);
		}
		
		// 回调用户接口
		return doCallBack(user, callBack, forceCallBack);
	}
	
	/**
	 * 加载协作单位列表，建议使用该接口，这样可以避免了解本缓存的实现细节，否则数据加载成功后
	 * 需要对自己添加到本地缓存中
	 * @param projectId	指定的项目ID
	 * @param callBack	用户本地回调，通知用户数据加载完毕，也可能加载失败，
	 * 					用户要根据返回状态决定下一步
	 */
	public static void loadCooperationTanents(int projectId, CallBack<Void, Integer> callBack) {
		loadCooperationTanents(projectId, null, callBack);
	}
	
	/**
	 * 加载协作单位列表
	 * @param projectId	指定的项目ID
	 * @param manager	如果采用本地回调方式通知数据加载成功manager可以为空，
	 * 					但此时callBack不为空
	 * @param callBack	用户本地回调，通知用户数据加载完毕，也可能加载失败，
	 * 					用户要根据返回状态决定下一步
	 */
	private static void loadCooperationTanents(final int projectId, 
						DataManagerInterface manager, 
						final CallBack<Void, Integer> callBack) {
		Tenant tenant = new Tenant();
		tenant.setTenant_id(UserCache.getTenantId());
		
		// 默认使用用户提供的数据管理接口实现方法
		DataManagerInterface localManager = manager;
		if (manager == null) {
			localManager = new DataManagerInterface() {
				
				@SuppressWarnings("unchecked")
				@Override
				public void getDataOnResult(ResultStatus status, List<?> list) {
					switch (status.getCode()) {
						case AnalysisManager.SUCCESS_DB_QUERY:
							
							// 添加到本地缓存
							addTenants(projectId, (List<Tenant>) list);
							break;
						default:
							break;
					}
					
					/*
					 * manager为空，那么callBack最好不要为空，否则需要轮询是否已经
					 * 加载成功，然后读取数据，这里使用回调来提醒用户数据加载成功，
					 * 注意这里传递过去的是状态码
					 */
					if (callBack != null) {
						callBack.callBack(status.getCode());
					}
				}
			};
		}
		
		// 请求服务器，获取项目相关的协作单位列表
		RemoteCommonService.getInstance()
				.getCooperationTenantListByProject(localManager, tenant, projectId);
	}
	
	/**
	 * 加载协作单位的联系人，建议使用该接口，这样可以避免了解本缓存的实现细节，否则数据加载成功后
	 * 需要对自己添加到本地缓存中
	 * @param projectId 项目ID
	 * @param tenantId	指定协作单位Id
	 * @param callBack 	用户本地回调，通知用户数据加载完毕，也可能加载失败，
	 * 					用户要根据返回状态决定下一步
	 */
	public static void loadCooperationTanantContacts(int projectId, int tenantId, 
								CallBack<Void, Integer> callBack) {
		loadCooperationTanantContacts(projectId, tenantId, null, callBack);
	}
	
	/**
	 * 加载协作单位的联系人
	 * @param projectId 指定项目ID
	 * @param tenantId	指定协作单位Id
	 * @param manager 	如果采用本地回调方式通知数据加载成功manager可以为空，
	 * 					但此时callBack不为空
	 * @param callBack 	用户本地回调，通知用户数据加载完毕，也可能加载失败，
	 * 					用户要根据返回状态决定下一步
	 */
	private static void loadCooperationTanantContacts(final int projectId, 
						final int tenantId, 
						final DataManagerInterface manager, 
						final CallBack<Void, Integer> callBack) {
		final Tenant tenant = mCooperationIdTenantMap.get(projectId + "-" + tenantId);
		
		// 如果拿到缓存的协作单位信息，直接获取对应的联系人
		if (tenant != null) {
			loadCooperationUsers(tenant, manager, callBack);
			
		// 如果没有拿到缓存的协作单位信息，先获取项目对应的协作单位列表
		} else {
			loadCooperationTanents(projectId, new CallBack<Void, Integer>() {
				
				@Override
				public Void callBack(Integer a) {
					
					// 如果查询成功，进行下一步
					if (a == AnalysisManager.SUCCESS_DB_QUERY) {
						final Tenant tenant = mCooperationIdTenantMap
											.get(projectId + "-" + tenantId);
						if (tenant != null) {
							loadCooperationUsers(tenant, manager, callBack);
						} else {
							callBack.callBack(a);
						}
					}
					return null;
				}
			});
		}
	}
	
	/**
	 * 加载协作单位联系人
	 * @param tenant
	 * @param manager
	 * @param callBack
	 */
	private static void loadCooperationUsers(final Tenant tenant, 
				DataManagerInterface manager, final CallBack<Void, Integer> callBack) {
		
		// 默认使用用户提供的数据管理接口实现方法
		DataManagerInterface localManager = manager;
		localManager = new DataManagerInterface() {
			
			@SuppressWarnings("unchecked")
			@Override
			public void getDataOnResult(ResultStatus status, List<?> list) {
				switch (status.getCode()) {
					case AnalysisManager.SUCCESS_DB_QUERY:
						
						// 添加到本地缓存
						addContacts(tenant.getCooperation_id(), (List<User>) list);
						break;
					default:
						break;
				}
				
				/*
				 * manager为空，那么callBack最好不要为空，否则需要轮询是否已经
				 * 加载成功，然后读取数据，这里使用回调来提醒用户数据加载成功，
				 * 注意这里传递过去的是状态码
				 */
				if (callBack != null) {
					callBack.callBack(status.getCode());
				}
			}
		};
				
		RemoteUserService.getInstance().getCooperationUsers(localManager, 
							tenant.getCooperation_id(), UserCache.getTenantId());
	}
	
	/**
	 * 获取列表中指定用户ID的联系人
	 * @param userId	用户ID
	 * @param users		用户列表
	 * @return	返回查找到的联系人信息
	 */
	private static User getContact(int userId, List<User> users) {
		if (users != null) {
			for (int i = 0; i < users.size(); i++) {
				if (users.get(i).getUser_id() == userId) {
					return users.get(i);
				}
			}
		}
		
		return null;
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
}
