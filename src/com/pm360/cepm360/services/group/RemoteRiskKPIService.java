package com.pm360.cepm360.services.group;

import com.google.gson.reflect.TypeToken;
import com.pm360.cepm360.app.common.activity.DataManagerInterface;
import com.pm360.cepm360.common.json.AnalysisManager;
import com.pm360.cepm360.entity.Zh_fkzb;
import com.pm360.cepm360.services.common.AsyncTaskPM360;
import com.pm360.cepm360.services.common.Operation;
import com.pm360.cepm360.services.common.RemoteService;

import java.lang.reflect.Type;
import java.util.List;

/**
 * 组合-风控指标远程服务
 * 
 * @author Andy
 * 
 */
public class RemoteRiskKPIService {
	// 框架服务类全限定名
	private static final String SERVICE_NAME = "com.pm360.cepm360.services.group.RiskKPIService";
	// 单例类变量
	private static RemoteRiskKPIService gService;

	/**
	 * 单例模式
	 * 
	 * @return
	 */
	public static synchronized RemoteRiskKPIService getInstance() {
		if (gService == null) {
			gService = new RemoteRiskKPIService();
		}
		return gService;
	}

	/**
	 * 销毁实例对象
	 */
	public static void destroyInstance() {
		gService = null;
	}

	private RemoteRiskKPIService() {

	}

	/**
	 * 获取风控指标
	 * 
	 * @param task_id
	 * @return
	 */
	public AsyncTaskPM360 getRiskKPIList(final DataManagerInterface manager,
			int task_id) {
		// 初始化返回类型
		Type type = AnalysisManager.GSON ? new TypeToken<List<Zh_fkzb>>() {
		}.getType()
				: Zh_fkzb.class;
		// 设置调用参数，调用远程服务
		return new RemoteService().setParams(SERVICE_NAME, "getRiskKPIList",
				task_id).call(manager, type, Operation.QUERY);
	}

	/**
	 * 增加风控指标
	 * 
	 * @param Zh_fkzbJSONList
	 * @return
	 */
	public AsyncTaskPM360 addRiskKPI(final DataManagerInterface manager,
			List<Zh_fkzb> Zh_fkzbList) {
		// 设置返回类型
		Type type = AnalysisManager.GSON ? new TypeToken<List<Zh_fkzb>>() {
		}.getType() : Zh_fkzb.class;
		// 设置调用参数，调用远程服务
		return new RemoteService().setParams(SERVICE_NAME, "addRiskKPI",
				Zh_fkzbList).call(manager, type, Operation.ADD);
	}

	/**
	 * 删除风控指标
	 * 
	 * @param id
	 * @return
	 */
	public AsyncTaskPM360 deleteRiskKPI(final DataManagerInterface manager,
			int id) {
		// 设置调用参数，调用远程服务
		return new RemoteService().setParams(SERVICE_NAME, "deleteRiskKPI", id)
				.call(manager, Operation.DELETE);
	}

}
