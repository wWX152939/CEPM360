package com.pm360.cepm360.services.lease;

import com.google.gson.reflect.TypeToken;
import com.pm360.cepm360.app.common.activity.DataManagerInterface;
import com.pm360.cepm360.common.json.AnalysisManager;
import com.pm360.cepm360.entity.P_ZLH;
import com.pm360.cepm360.entity.P_ZLHD;
import com.pm360.cepm360.entity.P_ZLRD;
import com.pm360.cepm360.services.common.AsyncTaskPM360;
import com.pm360.cepm360.services.common.Operation;
import com.pm360.cepm360.services.common.RemoteService;

import java.lang.reflect.Type;
import java.util.List;

/**
 * 远程租赁-还
 * 
 * @author Andy
 * 
 */
public class RemoteZLHService {

	// 框架服务类全限定名
	private static final String SERVICE_NAME = "com.pm360.cepm360.services.lease.ZLHService";
	// 单例类变量
	private static RemoteZLHService gService;

	/**
	 * 单例模式
	 * 
	 * @return
	 */
	public static synchronized RemoteZLHService getInstance() {
		if (gService == null) {
			gService = new RemoteZLHService();
		}
		return gService;
	}

	/**
	 * 销毁实例对象
	 */
	public static void destroyInstance() {
		gService = null;
	}

	private RemoteZLHService() {

	}

	/**
	 * 获取租还信息
	 * 
	 * @param tenant_id_
	 * @param task_id_
	 *            可以不赋值
	 * @param project_id_可以不赋值
	 * @return
	 */
	public AsyncTaskPM360 getZLHList(final DataManagerInterface manager,
			int tenant_id, int task_id_, int project_id_) {
		// 初始化返回类型
		Type type = AnalysisManager.GSON ? new TypeToken<List<P_ZLH>>() {
		}.getType() : P_ZLH.class;
		// 设置调用参数，调用远程服务
		return new RemoteService().setParams(SERVICE_NAME, "getZLHList",
				tenant_id, task_id_, project_id_).call(manager, type,
				Operation.QUERY);
	}

	/**
	 * 获取需要租还信息
	 * 
	 * @param tenant_id_
	 * @param project_id_
	 * @param name_
	 * @param lease_date
	 * @return
	 */
	public AsyncTaskPM360 getNeedZLHList(final DataManagerInterface manager,
			int tenant_id) {
		// 初始化返回类型
		Type type = AnalysisManager.GSON ? new TypeToken<List<P_ZLRD>>() {
		}.getType() : P_ZLRD.class;
		// 设置调用参数，调用远程服务
		return new RemoteService().setParams(SERVICE_NAME, "getNeedZLHList",
				tenant_id).call(manager, type,
				Operation.QUERY);
	}

	/**
	 * 查看租还单
	 * 
	 * @param zlh_id
	 * @return
	 */
	public AsyncTaskPM360 getZLHD(final DataManagerInterface manager, int zlh_id) {
		// 初始化返回类型
		Type type = AnalysisManager.GSON ? new TypeToken<List<P_ZLHD>>() {
		}.getType() : P_ZLHD.class;
		// 设置调用参数，调用远程服务
		return new RemoteService().setParams(SERVICE_NAME, "getZLHD", zlh_id)
				.call(manager, type, Operation.QUERY);
	}

	/**
	 * 新增租还单信息
	 * 
	 * @param P_ZLHJSON
	 * @param P_ZLHDJSONList
	 * @return
	 */
	public AsyncTaskPM360 addZLH(final DataManagerInterface manager,
			P_ZLH P_ZLH, List<P_ZLHD> P_ZLHDList) {
		// 设置调用参数，调用远程服务
		return new RemoteService().setParams(SERVICE_NAME, "addZLH", P_ZLH,
				P_ZLHDList).call(manager, Operation.ADD);
	}

	/**
	 * 更新还租信息
	 * 
	 * @param P_ZLHJSON
	 * @param P_ZLHDJSONList
	 * @return
	 */
	public AsyncTaskPM360 updateZLH(final DataManagerInterface manager,
			P_ZLH P_ZLH, List<P_ZLHD> P_ZLHDList) {
		// 设置调用参数，调用远程服务
		return new RemoteService().setParams(SERVICE_NAME, "updateZLH", P_ZLH,
				P_ZLHDList).call(manager, Operation.MODIFY);
	}

	/**
	 * 删除还租单
	 * 
	 * @param zlh_id
	 * @return
	 */
	public AsyncTaskPM360 deleteZLH(final DataManagerInterface manager,
			int zlh_id) {
		// 设置调用参数，调用远程服务
		return new RemoteService().setParams(SERVICE_NAME, "deleteZLH", zlh_id)
				.call(manager, Operation.DELETE);
	}

}
