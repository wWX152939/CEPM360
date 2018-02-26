package com.pm360.cepm360.services.common;

import com.google.gson.reflect.TypeToken;
import com.pm360.cepm360.app.common.activity.DataManagerInterface;
import com.pm360.cepm360.common.json.AnalysisManager;
import com.pm360.cepm360.entity.APPProperties;
import com.pm360.cepm360.entity.Cooperation;
import com.pm360.cepm360.entity.Document;
import com.pm360.cepm360.entity.Files;
import com.pm360.cepm360.entity.MailBox;
import com.pm360.cepm360.entity.Message;
import com.pm360.cepm360.entity.Project;
import com.pm360.cepm360.entity.Prole;
import com.pm360.cepm360.entity.Role;
import com.pm360.cepm360.entity.Templet_WBS;
import com.pm360.cepm360.entity.Tenant;
import com.pm360.cepm360.entity.User;
import com.pm360.cepm360.entity.ZH_group;

import java.lang.reflect.Type;
import java.util.List;

public class RemoteCommonService {
	// 任务服务的类全限定名
	private static final String SERVICE_NAME = "com.pm360.cepm360.services.common.CommonService";
	// 单例对象类变量
	private static RemoteCommonService gService;

	/**
	 * 单例模式
	 * 
	 * @return
	 */
	public static synchronized RemoteCommonService getInstance() {
		if (gService == null) {
			gService = new RemoteCommonService();
		}
		return gService;
	}

	/**
	 * 销毁实例对象
	 */
	public static void destroyInstance() {
		gService = null;
	}

	/**
	 * 禁止外部通过构造函数实例化对象
	 */
	private RemoteCommonService() {

	}

	/**
	 * 获取系统所有租户信息
	 * 
	 * @param TenantJSON
	 *            租户名字模糊查询
	 * @return
	 */
	public AsyncTaskPM360 getTenantList(final DataManagerInterface manager,
			Tenant tenant) {
		// 初始化返回类型
		Type type = AnalysisManager.GSON ? new TypeToken<List<Tenant>>() {
		}.getType() : Tenant.class;
		// 设置调用参数，调用远程服务
		return new RemoteService().setParams(SERVICE_NAME, "getTenantList",
				tenant).call(manager, type, Operation.QUERY);
	}

	/**
	 * 根据租户ID查询租户详细信息
	 * 
	 * @param tenant_ids
	 * 
	 * @return
	 */
	public AsyncTaskPM360 getTenantDetailByTenantIDs(
			final DataManagerInterface manager, String tenant_ids) {
		// 初始化返回类型
		Type type = AnalysisManager.GSON ? new TypeToken<List<Tenant>>() {
		}.getType() : Tenant.class;
		// 设置调用参数，调用远程服务
		return new RemoteService().setParams(SERVICE_NAME,
				"getTenantDetailByTenantIDs", tenant_ids).call(manager, type,
				Operation.QUERY);
	}

	/**
	 * 获取基于某个项目未合作的过的公司信息
	 * 
	 * @param TenantJSON
	 *            租户名字模糊查询
	 * @param project_id
	 * @return
	 */
	public AsyncTaskPM360 getUNCooperationTenantListByProject(
			final DataManagerInterface manager, Tenant tenant, int project_id) {
		// 初始化返回类型
		Type type = AnalysisManager.GSON ? new TypeToken<List<Tenant>>() {
		}.getType() : Tenant.class;
		// 设置调用参数，调用远程服务
		return new RemoteService().setParams(SERVICE_NAME,
				"getUNCooperationTenantListByProject", tenant, project_id)
				.call(manager, type, Operation.QUERY);
	}

	/**
	 * 获取该公司合作过的所有公司信息
	 * 
	 * @param TenantJSON
	 *            租户名字模糊查询
	 * @return
	 */
	public AsyncTaskPM360 getAllCooperationTenantList(
			final DataManagerInterface manager, Tenant tenant) {
		// 初始化返回类型
		Type type = AnalysisManager.GSON ? new TypeToken<List<Tenant>>() {
		}.getType() : Tenant.class;
		// 设置调用参数，调用远程服务
		return new RemoteService().setParams(SERVICE_NAME,
				"getAllCooperationTenantList", tenant).call(manager, type,
				Operation.QUERY);
	}

	/**
	 * 获取该公司合作过的所有协作信息
	 * 
	 * @param TenantJSON
	 *            租户名字模糊查询
	 * @return
	 */
	public AsyncTaskPM360 getAllCooperationList(
			final DataManagerInterface manager, Tenant tenant) {
		// 初始化返回类型
		Type type = AnalysisManager.GSON ? new TypeToken<List<Cooperation>>() {
		}.getType() : Cooperation.class;
		// 设置调用参数，调用远程服务
		return new RemoteService().setParams(SERVICE_NAME,
				"getAllCooperationList", tenant).call(manager, type,
				Operation.QUERY);
	}

	/**
	 * 获取基于某个项目的协作单位信息
	 * 
	 * @param TenantJSON
	 *            租户名字模糊查询
	 * @param project_id
	 * @return
	 */
	public AsyncTaskPM360 getCooperationTenantListByProject(
			final DataManagerInterface manager, Tenant tenant, int project_id) {
		// 初始化返回类型
		Type type = AnalysisManager.GSON ? new TypeToken<List<Tenant>>() {
		}.getType() : Tenant.class;
		// 设置调用参数，调用远程服务
		return new RemoteService().setParams(SERVICE_NAME,
				"getCooperationTenantListByProject", tenant, project_id).call(
				manager, type, Operation.QUERY);
	}

	/**
	 * 获取基于某个项目的协作单位信息
	 * 
	 * @param TenantJSON
	 *            租户名字模糊查询
	 * @param project_id
	 * @return
	 */
	public AsyncTaskPM360 getCooperationTenantListByProject2(
			final DataManagerInterface manager, Tenant tenant, int project_id) {
		// 初始化返回类型
		Type type = AnalysisManager.GSON ? new TypeToken<List<Cooperation>>() {
		}.getType() : Cooperation.class;
		// 设置调用参数，调用远程服务
		return new RemoteService().setParams(SERVICE_NAME,
				"getCooperationTenantListByProject2", tenant, project_id).call(
				manager, type, Operation.QUERY);
	}

	/**
	 * 获取WBS模板
	 * 
	 * @param manager
	 * @param wbs
	 */
	public AsyncTaskPM360 getWBSTemplet(final DataManagerInterface manager,
			User user) {
		// 初始化返回类型
		Type type = AnalysisManager.GSON ? new TypeToken<List<Templet_WBS>>() {
		}.getType() : Templet_WBS.class;
		// 设置调用参数，调用远程服务
		return new RemoteService().setParams(SERVICE_NAME, "getWBSTemplet",
				user.getTenant_id()).call(manager, type, Operation.QUERY);
	}

	/**
	 * 导入WBS模板
	 * 
	 * @param templet_wbs_id
	 * @param type_id
	 *            (project_id OR zh_group_id)
	 * @param tenant_id
	 * @param enterprise_type对应GLOBAL里的企业类型
	 * @return
	 */
	public AsyncTaskPM360 importWBS(final DataManagerInterface manager,
			int templet_wbs_id, int type_id, int tenant_id,
			String enterprise_type) {
		// 设置调用参数，调用远程服务
		return new RemoteService().setParams(SERVICE_NAME, "importWBS",
				templet_wbs_id, type_id, tenant_id, enterprise_type).call(
				manager, Operation.ADD);
	}

	/**
	 * 通过任务ID获取项目对象
	 * 
	 * @param manager
	 * @param message
	 */
	public AsyncTaskPM360 getProjectIDBYTaskID(
			final DataManagerInterface manager, Message message) {
		// 初始化返回类型
		Type type = AnalysisManager.GSON ? new TypeToken<List<Project>>() {
		}.getType() : Project.class;
		// 设置调用参数，调用远程服务
		return new RemoteService().setParams(SERVICE_NAME,
				"getProjectIDBYTaskID", message.getTask_id()).call(manager,
				type, Operation.QUERY);
	}

	/**
	 * 根据task_id查询group_id
	 * 
	 * @param task_id
	 * @return
	 */
	public AsyncTaskPM360 getGroupIDBYTaskID(
			final DataManagerInterface manager, Message message) {
		// 初始化返回类型
		Type type = AnalysisManager.GSON ? new TypeToken<List<ZH_group>>() {
		}.getType() : ZH_group.class;
		// 设置调用参数，调用远程服务
		return new RemoteService().setParams(SERVICE_NAME,
				"getGroupIDBYTaskID", message.getTask_id()).call(manager, type,
				Operation.QUERY);
	}

	/**
	 * 版本检查
	 * 
	 * @param manager
	 * @param aPPPropertiesiles
	 */
	public AsyncTaskPM360 checkVersion(final DataManagerInterface manager,
			APPProperties aPPPropertiesiles) {
		// 初始化返回类型
		Type type = AnalysisManager.GSON ? new TypeToken<List<APPProperties>>() {
		}.getType()
				: APPProperties.class;

		// 设置调用参数，调用远程服务
		return new RemoteService().setParams(SERVICE_NAME, "checkVersion",
				aPPPropertiesiles).call(manager, type, Operation.QUERY);
	}

	/**
	 * 根据服务器时间，自动生成编码
	 * 
	 * @param prefix
	 *            例如"CG"
	 * @return
	 */
	public AsyncTaskPM360 getCodeByDate(final DataManagerInterface manager,
			String prefix) {
		// 设置调用参数，调用远程服务
		return new RemoteService().setParams(SERVICE_NAME, "getCodeByDate",
				prefix).call(manager, Operation.QUERY);
	}

	/**
	 * 通过文件ID获取目录
	 * 
	 * @param manager
	 * @param message
	 */
	public AsyncTaskPM360 getDirectoryBYFileID(
			final DataManagerInterface manager, Message message) {
		// 初始化返回类型
		Type type = AnalysisManager.GSON ? new TypeToken<List<Document>>() {
		}.getType() : Document.class;

		// 设置调用参数，调用远程服务
		return new RemoteService().setParams(SERVICE_NAME,
				"getDocumentIDBYFileID", message.getType_id()).call(manager,
				type, Operation.QUERY);
	}

	/**
	 * 根据选择的项目获取系统权限
	 * 
	 * @param manager
	 * @param prole
	 *            该用户所选择的项目拥有的权限（参数需要project_id，user_id，tenant_id）
	 * @return role list对象，该对象里的action是各个模块的ID集合，根据这些操作的ID对控制页面显示和操作
	 */
	public AsyncTaskPM360 getPermissionByProject(
			final DataManagerInterface manager, int projectId, User user) {
		Prole prole = new Prole();
		prole.setProject_id(projectId);
		prole.setUser_id(user.getUser_id());
		prole.setTenant_id(user.getTenant_id());

		// 初始化返回类型
		Type type = AnalysisManager.GSON ? new TypeToken<List<Role>>() {
		}.getType() : Role.class;

		// 设置调用参数，调用远程服务
		return new RemoteService().setParams(SERVICE_NAME,
				"getPermissionByProject", prole).call(manager, type,
				Operation.QUERY);
	}

	/**
	 * 获取系统权限
	 * 
	 * @param manager
	 * @return role list对象，该对象里的action是各个模块的ID集合，根据这些操作的ID对控制页面显示和操作
	 */
	public AsyncTaskPM360 getSYSPermission(final DataManagerInterface manager,
			User user) {

		// 初始化返回类型
		Type type = AnalysisManager.GSON ? new TypeToken<List<Role>>() {
		}.getType() : Role.class;

		// 设置调用参数，调用远程服务
		return new RemoteService().setParams(SERVICE_NAME, "getSYSPermission",
				user.getUser_id(), user.getTenant_id()).call(manager, type,
				Operation.QUERY);
	}

	/**
	 * 查看附件(所有列表的附件)
	 * 
	 * @param file_id
	 * @return
	 */
	public AsyncTaskPM360 getAttachments(final DataManagerInterface manager,
			String file_id) {
		// 初始化返回类型
		Type type = AnalysisManager.GSON ? new TypeToken<List<Files>>() {
		}.getType() : Files.class;
		// 设置调用参数，调用远程服务
		return new RemoteService().setParams(SERVICE_NAME, "getAttachments",
				file_id).call(manager, type, Operation.QUERY);
	}

	/**
	 * 根据邮件类型查询邮件历史记录
	 * 
	 * @param mail_table_type
	 *            (对应GLOBAL里的MAIL_TABLE_TYPE)
	 * @param mail_table_id
	 *            相应记录的ID，例如现场图文：文件ID，合同变更：变更ID
	 * @return
	 */
	public AsyncTaskPM360 getMailByType(final DataManagerInterface manager,
			String mail_table_type, int id) {
		// 初始化返回类型
		Type type = AnalysisManager.GSON ? new TypeToken<List<MailBox>>() {
		}.getType() : MailBox.class;
		// 设置调用参数，调用远程服务
		return new RemoteService().setParams(SERVICE_NAME, "getMailByType",
				mail_table_type, id).call(manager, type, Operation.QUERY);
	}
}
