
package com.pm360.cepm360.services.expenses;

import com.google.gson.reflect.TypeToken;
import com.pm360.cepm360.app.common.activity.DataManagerInterface;
import com.pm360.cepm360.common.json.AnalysisManager;
import com.pm360.cepm360.entity.P_final_cost;
import com.pm360.cepm360.entity.Project;
import com.pm360.cepm360.entity.User;
import com.pm360.cepm360.services.common.AsyncTaskPM360;
import com.pm360.cepm360.services.common.Operation;
import com.pm360.cepm360.services.common.RemoteService;

import java.lang.reflect.Type;
import java.util.List;

/**
 * 统计成本服务
 * 
 * @author Andy
 */
public class RemoteFinalCostService {
    // 任务服务的类全限定名
    private static final String SERVICE_NAME = "com.pm360.cepm360.services.expenses.FinalCostService";
    // 单例对象类变量
    private static RemoteFinalCostService gService;

    /**
     * 单例模式
     * 
     * @return
     */
    public static synchronized RemoteFinalCostService getInstance() {
        if (gService == null) {
            gService = new RemoteFinalCostService();
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
    private RemoteFinalCostService() {

    }

    /**
     * 计算成本费用
     * 
     * @param tenant_id_
     * @param project_id_
     * @return P_final_cost
     */
    public AsyncTaskPM360 getCostList(final DataManagerInterface manager,
            User user, Project project) {
        // 初始化返回类型
        Type type = AnalysisManager.GSON ? new TypeToken<List<P_final_cost>>() {
        }.getType() : P_final_cost.class;

        // 设置调用参数，调用远程服务
        return new RemoteService().setParams(SERVICE_NAME, "getCostList",
                user.getTenant_id(), project.getProject_id()).call(manager, type, Operation.QUERY);
    }
}
