package com.pm360.cepm360.services.group;

import com.google.gson.reflect.TypeToken;
import com.pm360.cepm360.app.common.activity.DataManagerInterface;
import com.pm360.cepm360.common.json.AnalysisManager;
import com.pm360.cepm360.entity.ZH_count_info;
import com.pm360.cepm360.entity.ZH_group;
import com.pm360.cepm360.services.common.AsyncTaskPM360;
import com.pm360.cepm360.services.common.Operation;
import com.pm360.cepm360.services.common.RemoteService;

import java.lang.reflect.Type;
import java.util.List;

public class RemoteGroupService {
	// 框架服务类全限定名
	private static final String SERVICE_NAME = "com.pm360.cepm360.services.group.GroupService";
	// 单例类变量
	private static RemoteGroupService gService;

	/**
	 * 单例模式
	 * 
	 * @return
	 */
	public static synchronized RemoteGroupService getInstance() {
		if (gService == null) {
			gService = new RemoteGroupService();
		}
		return gService;
	}

	/**
	 * 销毁实例对象
	 */
	public static void destroyInstance() {
		gService = null;
	}

	private RemoteGroupService() {

	}

	/**
	 * 获取某个项目下的组合
	 * 
	 * @param project_id
	 *            项目ID
	 * @return 某个项目下的组合
	 */
	public AsyncTaskPM360 getGroup(final DataManagerInterface manager,
			int project_id) {
		// 初始化返回类型
		Type type = AnalysisManager.GSON ? new TypeToken<List<ZH_group>>() {
		}.getType() : ZH_group.class;
		// 设置调用参数，调用远程服务
		return new RemoteService().setParams(SERVICE_NAME, "getGroup",
				project_id).call(manager, type, Operation.QUERY);
	}

	/**
	 * 获取某个项目下组合节点的统计信息
	 * 
	 * @param zh_group_id
	 *            以逗号分开
	 * @return
	 */
	public AsyncTaskPM360 getCountGroupInfo(final DataManagerInterface manager,
			String zh_group_id) {
		// 初始化返回类型
		Type type = AnalysisManager.GSON ? new TypeToken<List<ZH_count_info>>() {
		}.getType()
				: ZH_count_info.class;
		// 设置调用参数，调用远程服务
		return new RemoteService().setParams(SERVICE_NAME, "getCountGroupInfo",
				zh_group_id).call(manager, type, Operation.QUERY);
	}

	/**
	 * 增加组合节点
	 * 
	 * @param ZH_groupListJSON
	 * @return
	 */
	public AsyncTaskPM360 addGroupNode(final DataManagerInterface manager,
			List<ZH_group> ZH_groupList) {
		// 设置返回类型
		Type type = AnalysisManager.GSON ? new TypeToken<List<ZH_group>>() {
		}.getType() : ZH_group.class;
		// 设置调用参数，调用远程服务
		return new RemoteService().setParams(SERVICE_NAME, "addGroupNode",
				ZH_groupList).call(manager, type, Operation.ADD);
	}

	/**
	 * 删除组合节点
	 * 
	 * @param zh_group_id
	 * @return
	 */
	public AsyncTaskPM360 deleteGroupNode(final DataManagerInterface manager,
			int zh_group_id) {
		// 设置调用参数，调用远程服务
		return new RemoteService().setParams(SERVICE_NAME, "deleteGroupNode",
				zh_group_id).call(manager, Operation.DELETE);
	}

	/**
	 * 更新组合节点
	 * 
	 * @param ZH_groupListJSON
	 * @return
	 */
	public AsyncTaskPM360 updateGroupNode(final DataManagerInterface manager,
			List<ZH_group> ZH_groupList) {
		// 设置调用参数，调用远程服务
		return new RemoteService().setParams(SERVICE_NAME, "updateGroupNode",
				ZH_groupList).call(manager, Operation.MODIFY);
	}
}
