package com.pm360.cepm360.services.lease;

import com.google.gson.reflect.TypeToken;
import com.pm360.cepm360.app.common.activity.DataManagerInterface;
import com.pm360.cepm360.common.json.AnalysisManager;
import com.pm360.cepm360.entity.P_ZLR;
import com.pm360.cepm360.entity.P_ZLRD;
import com.pm360.cepm360.services.common.AsyncTaskPM360;
import com.pm360.cepm360.services.common.Operation;
import com.pm360.cepm360.services.common.RemoteService;

import java.lang.reflect.Type;
import java.util.List;

/**
 * 远程租赁-租入服务
 * 
 * @author Andy
 * 
 */
public class RemoteZLRService {

	// 框架服务类全限定名
	private static final String SERVICE_NAME = "com.pm360.cepm360.services.lease.ZLRService";
	// 单例类变量
	private static RemoteZLRService gService;

	/**
	 * 单例模式
	 * 
	 * @return
	 */
	public static synchronized RemoteZLRService getInstance() {
		if (gService == null) {
			gService = new RemoteZLRService();
		}
		return gService;
	}

	/**
	 * 销毁实例对象
	 */
	public static void destroyInstance() {
		gService = null;
	}

	private RemoteZLRService() {

	}

	/**
	 * 获取租赁信息
	 * 
	 * @param tenant_id_
	 * @param task_id_
	 *            可以不赋值
	 * @param project_id_可以不赋值
	 * @return
	 */
	public AsyncTaskPM360 getZLRList(final DataManagerInterface manager,
			int tenant_id, int task_id_, int project_id_) {
		// 初始化返回类型
		Type type = AnalysisManager.GSON ? new TypeToken<List<P_ZLR>>() {
		}.getType() : P_ZLR.class;
		// 设置调用参数，调用远程服务
		return new RemoteService().setParams(SERVICE_NAME, "getZLRList",
				tenant_id, task_id_, project_id_).call(manager, type,
				Operation.QUERY);
	}

	/**
	 * 查看租赁单
	 * 
	 * @param zlr_id
	 * @return
	 */
	public AsyncTaskPM360 getZLRD(final DataManagerInterface manager, int zlr_id) {
		// 初始化返回类型
		Type type = AnalysisManager.GSON ? new TypeToken<List<P_ZLRD>>() {
		}.getType() : P_ZLRD.class;
		// 设置调用参数，调用远程服务
		return new RemoteService().setParams(SERVICE_NAME, "getZLRD", zlr_id)
				.call(manager, type, Operation.QUERY);
	}

	/**
	 * 新增租赁单信息
	 * 
	 * @param P_ZLRJSON
	 * @param P_ZLRDJSONList
	 * @return
	 */
	public AsyncTaskPM360 addZLR(final DataManagerInterface manager,
			P_ZLR P_ZLR, List<P_ZLRD> P_ZLRDList) {
		// 设置调用参数，调用远程服务
		return new RemoteService().setParams(SERVICE_NAME, "addZLR", P_ZLR,
				P_ZLRDList).call(manager, Operation.ADD);
	}

	/**
	 * 更新租赁单信息
	 * 
	 * @param P_ZLRJSON
	 * @param P_ZLRDJSONList
	 * @return
	 */
	public AsyncTaskPM360 updateZLR(final DataManagerInterface manager,
			P_ZLR P_ZLR, List<P_ZLRD> P_ZLRDList) {
		// 设置调用参数，调用远程服务
		return new RemoteService().setParams(SERVICE_NAME, "updateZLR", P_ZLR,
				P_ZLRDList).call(manager, Operation.MODIFY);
	}

	/**
	 * 删除租赁单
	 * 
	 * @param zlr_id
	 * @return
	 */
	public AsyncTaskPM360 deleteZLR(final DataManagerInterface manager,
			int zlr_id) {
		// 设置调用参数，调用远程服务
		return new RemoteService().setParams(SERVICE_NAME, "deleteZLR", zlr_id)
				.call(manager, Operation.DELETE);
	}

}
