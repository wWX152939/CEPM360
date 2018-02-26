package com.pm360.cepm360.services.investment;

import com.google.gson.reflect.TypeToken;
import com.pm360.cepm360.app.common.activity.DataManagerInterface;
import com.pm360.cepm360.common.json.AnalysisManager;
import com.pm360.cepm360.entity.InvestmentEstimate;
import com.pm360.cepm360.entity.InvestmentEstimateD;
import com.pm360.cepm360.services.common.AsyncTaskPM360;
import com.pm360.cepm360.services.common.Operation;
import com.pm360.cepm360.services.common.RemoteService;

import java.lang.reflect.Type;
import java.util.List;

/**
 * 远程投资估算服务
 * 
 * @author Andy
 * 
 */
public class RemoteInvestmentEstimateService {

	// 框架服务类全限定名
	private static final String SERVICE_NAME = "com.pm360.cepm360.services.investment.InvestmentEstimateService";
	// 单例类变量
	private static RemoteInvestmentEstimateService gService;

	/**
	 * 单例模式
	 * 
	 * @return
	 */
	public static synchronized RemoteInvestmentEstimateService getInstance() {
		if (gService == null) {
			gService = new RemoteInvestmentEstimateService();
		}
		return gService;
	}

	/**
	 * 销毁实例对象
	 */
	public static void destroyInstance() {
		gService = null;
	}

	private RemoteInvestmentEstimateService() {

	}

	/**
	 * 获取投资估算列表
	 * 
	 * @param tenant_id_
	 * @param task_id_
	 * @param project_id_
	 * @return
	 */
	public AsyncTaskPM360 getInvestmentEstimateList(
			final DataManagerInterface manager, int tenant_id, int task_id_,
			int project_id_) {
		// 初始化返回类型
		Type type = AnalysisManager.GSON ? new TypeToken<List<InvestmentEstimate>>() {
		}.getType()
				: InvestmentEstimate.class;
		// 设置调用参数，调用远程服务
		return new RemoteService().setParams(SERVICE_NAME,
				"getInvestmentEstimateList", tenant_id, task_id_, project_id_)
				.call(manager, type, Operation.QUERY);
	}

	/**
	 * 查看投资估算清单
	 * 
	 * @param investment_estimate_id
	 * @return
	 */
	public AsyncTaskPM360 getInvestmentEstimate(
			final DataManagerInterface manager, int investment_estimate_id) {
		// 初始化返回类型
		Type type = AnalysisManager.GSON ? new TypeToken<List<InvestmentEstimateD>>() {
		}.getType()
				: InvestmentEstimateD.class;
		// 设置调用参数，调用远程服务
		return new RemoteService().setParams(SERVICE_NAME,
				"getInvestmentEstimate", investment_estimate_id).call(manager,
				type, Operation.QUERY);
	}

	/**
	 * 新增投资预算
	 * 
	 * @param InvestmentEstimateJSON
	 * @param InvestmentEstimateDJSONList
	 * @return
	 */
	public AsyncTaskPM360 addInvestmentEstimate(
			final DataManagerInterface manager,
			InvestmentEstimate InvestmentEstimate,
			List<InvestmentEstimateD> InvestmentEstimateDList) {
		// 设置调用参数，调用远程服务
		return new RemoteService().setParams(SERVICE_NAME,
				"addInvestmentEstimate", InvestmentEstimate,
				InvestmentEstimateDList).call(manager, Operation.ADD);
	}

	/**
	 * 更新投资估算
	 * 
	 * @param InvestmentEstimateJSON
	 * @param InvestmentEstimateDJSONList
	 * @return
	 */
	public AsyncTaskPM360 updateInvestmentEstimate(
			final DataManagerInterface manager,
			InvestmentEstimate InvestmentEstimate,
			List<InvestmentEstimateD> InvestmentEstimateDList) {
		// 设置调用参数，调用远程服务
		return new RemoteService().setParams(SERVICE_NAME,
				"updateInvestmentEstimate", InvestmentEstimate,
				InvestmentEstimateDList).call(manager, Operation.MODIFY);
	}

	/**
	 * 删除投资估算
	 * 
	 * @param investment_estimate_id
	 * @return
	 */
	public AsyncTaskPM360 deleteInvestmentEstimate(
			final DataManagerInterface manager, int investment_estimate_id) {
		// 设置调用参数，调用远程服务
		return new RemoteService().setParams(SERVICE_NAME,
				"deleteInvestmentEstimate", investment_estimate_id).call(
				manager, Operation.DELETE);
	}

}
