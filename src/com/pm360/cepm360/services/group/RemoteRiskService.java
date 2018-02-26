package com.pm360.cepm360.services.group;

import com.google.gson.reflect.TypeToken;
import com.pm360.cepm360.app.common.activity.DataManagerInterface;
import com.pm360.cepm360.common.json.AnalysisManager;
import com.pm360.cepm360.entity.Files;
import com.pm360.cepm360.entity.ZH_group_risk;
import com.pm360.cepm360.services.common.AsyncTaskPM360;
import com.pm360.cepm360.services.common.Operation;
import com.pm360.cepm360.services.common.RemoteService;

import java.lang.reflect.Type;
import java.util.List;

public class RemoteRiskService {
	// 框架服务类全限定名
	private static final String SERVICE_NAME = "com.pm360.cepm360.services.group.RiskService";
	// 单例类变量
	private static RemoteRiskService gService;

	/**
	 * 单例模式
	 * 
	 * @return
	 */
	public static synchronized RemoteRiskService getInstance() {
		if (gService == null) {
			gService = new RemoteRiskService();
		}
		return gService;
	}

	/**
	 * 销毁实例对象
	 */
	public static void destroyInstance() {
		gService = null;
	}

	private RemoteRiskService() {

	}

	/**
	 * 获取风险项列表
	 * 
	 * @param task_id
	 * @return
	 */
	public AsyncTaskPM360 getRiskList(final DataManagerInterface manager,
			int task_id) {
		// 初始化返回类型
		Type type = AnalysisManager.GSON ? new TypeToken<List<ZH_group_risk>>() {
		}.getType()
				: ZH_group_risk.class;
		// 设置调用参数，调用远程服务
		return new RemoteService().setParams(SERVICE_NAME, "getRiskList",
				task_id).call(manager, type, Operation.QUERY);
	}

	/**
	 * 增加风险检查项
	 * 
	 * @param ZH_group_riskJSON
	 * @return
	 */
	public AsyncTaskPM360 addRisk(final DataManagerInterface manager,
			ZH_group_risk ZH_group_risk) {
		// 设置返回类型
		Type type = AnalysisManager.GSON ? new TypeToken<List<ZH_group_risk>>() {
		}.getType()
				: ZH_group_risk.class;
		// 设置调用参数，调用远程服务
		return new RemoteService().setParams(SERVICE_NAME, "addRisk",
				ZH_group_risk).call(manager, type, Operation.ADD);
	}

	/**
	 * 删除风险记录
	 * 
	 * @param zh_group_risk_id
	 * @return
	 */
	public AsyncTaskPM360 deleteRisk(final DataManagerInterface manager,
			int zh_group_risk_id) {
		// 设置调用参数，调用远程服务
		return new RemoteService().setParams(SERVICE_NAME, "deleteRisk",
				zh_group_risk_id).call(manager, Operation.DELETE);
	}

	/**
	 * 更新风险信息
	 * 
	 * @param ZH_group_riskJSON
	 * @return
	 */
	public AsyncTaskPM360 updateRisk(final DataManagerInterface manager,
			ZH_group_risk ZH_group_risk) {
		// 设置调用参数，调用远程服务
		return new RemoteService().setParams(SERVICE_NAME, "updateRisk",
				ZH_group_risk).call(manager, Operation.MODIFY);
	}

	/**
	 * 获取风险识别下的附件
	 * 
	 * @param zh_group_risk_id
	 * @return
	 */
	public AsyncTaskPM360 getRiskFiles(final DataManagerInterface manager,
			int zh_group_risk_id) {
		// 初始化返回类型
		Type type = AnalysisManager.GSON ? new TypeToken<List<Files>>() {
		}.getType() : Files.class;

		// 设置调用参数，调用远程服务
		return new RemoteService().setParams(SERVICE_NAME, "getRiskFiles",
				zh_group_risk_id).call(manager, type, Operation.QUERY);
	}
}
