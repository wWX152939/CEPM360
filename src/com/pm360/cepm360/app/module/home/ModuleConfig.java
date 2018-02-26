/*
 * Copyright (c) 2015 PM360, Inc.
 * All Rights Reserved.
 * PM360 Proprietary and Confidential.
 */
package com.pm360.cepm360.app.module.home;

import com.pm360.cepm360.R;
import com.pm360.cepm360.app.module.announcement.AnnouncementActivity;
import com.pm360.cepm360.app.module.attendance.LocationActivity;
import com.pm360.cepm360.app.module.change.ChangeActivity;
import com.pm360.cepm360.app.module.combination.CombinationView;
import com.pm360.cepm360.app.module.contract.ContractActivity;
import com.pm360.cepm360.app.module.cooperation.CooperationActivity;
import com.pm360.cepm360.app.module.cost.CostActivity;
import com.pm360.cepm360.app.module.document.DocumentActivity;
import com.pm360.cepm360.app.module.email.EmailActivity;
import com.pm360.cepm360.app.module.fund.FundActivity;
import com.pm360.cepm360.app.module.inventory.InventoryActivity;
import com.pm360.cepm360.app.module.investment.InvestmentActivity;
import com.pm360.cepm360.app.module.invitebid.BidActivity;
import com.pm360.cepm360.app.module.lease.LeaseManagementActivity;
import com.pm360.cepm360.app.module.project.ProjectActivity;
import com.pm360.cepm360.app.module.purchase.PurchaseActivity;
import com.pm360.cepm360.app.module.resource.ResourceActivity;
import com.pm360.cepm360.app.module.schedule.CombinationActivity;
import com.pm360.cepm360.app.module.settings.SettingsActivity;
import com.pm360.cepm360.app.module.subcontract.SubcontractManagementActivity;
import com.pm360.cepm360.app.module.template.TemplateActivity;

/**
 * 
 * 主界面配置信息
 * 
 * 1。根据不同的企业类型，配置相应的模块，模块显示顺序就是数组的顺序
 * 2.[设置]模块中的[角色管理]也根据此配置信息来设置相应的角色权限
 * 
 * 作者： Qiuwei Chen
 * 日期： 2016-1-14
 *
 */
public class ModuleConfig {
    
    /**
     * 获取当前企业类型的模块 
     * 
     * @param type 企业类型
     * @return 模块序列
     */
    public static int[] getHomeModules(int type) {
        int[][] allHomePages = new int[][] { 
                { //***************** 0: 管理方主页  *****************/
                    1,  // 项目管理
                    //16, // 组合管理
                    //2,  // 计划管理
                    13, // 合同管理
                    17, // 协作管理
                    3,  // 文档管理                    
                    24, // 投标管理 
                    23, // 投资管理  
                    8,  // 物资管理
                    14,  // 考勤管理
//                    5,  // 租赁管理
                    18, // 邮件管理
                    9,  // 资源管理
                    10, // 模块管理
                    11, // 系统设置
                    22, // 监控预警
                    25  // 公告管理
                },
                { /* 1: 建设单位主页   */
                    1,  // 项目管理
                    2,  // 计划管理
                    22, // 监控预警
                    17, // 协作管理 
                    3,  // 文档管理
                    13, // 合同管理
                    8,  // 物资管理
                    14,  // 考勤管理
//                    5,  // 租赁管理
                    10, // 模块管理
                    18, // 邮件管理
                    9,  // 资源管理
                    11, // 系统设置
                    25  // 公告管理
                },
                { /* 2: 施工单位主页   */
                    1,  // 项目管理
                    2,  // 计划管理
                    22, // 监控预警
                    17, // 协作管理 
                    3,  // 文档管理
                    13, // 合同管理
                    8,  // 物资管理
                    14,  // 考勤管理
//                    5,  // 租赁管理
                    10, // 模块管理
                    18, // 邮件管理
                    9,  // 资源管理
                    11, // 系统设置
                    25  // 公告管理
                },
                { /* 3: 设计单位主页   */
                    1,  // 项目管理
                    2,  // 计划管理
                    22, // 监控预警
                    17, // 协作管理 
                    3,  // 文档管理
                    13, // 合同管理
                    8,  // 物资管理
                    14,  // 考勤管理
//                    5,  // 租赁管理
                    10, // 模块管理
                    18, // 邮件管理
                    9,  // 资源管理
                    11, // 系统设置
                    25  // 公告管理
                },
                { /* 4: 监理单位主页   */
                    1,  // 项目管理
                    2,  // 计划管理
                    22, // 监控预警
                    17, // 协作管理 
                    3,  // 文档管理
                    13, // 合同管理
                    8,  // 物资管理
                    14,  // 考勤管理
//                    5,  // 租赁管理
                    10, // 模块管理
                    18, // 邮件管理
                    9,  // 资源管理
                    11, // 系统设置
                    25  // 公告管理
                },
                { /* 5: 投资方主页   */
                    1,  // 项目管理
                    2,  // 计划管理
                    22, // 监控预警
                    17, // 协作管理 
                    3,  // 文档管理
                    13, // 合同管理
                    8,  // 物资管理
                    14,  // 考勤管理
//                    5,  // 租赁管理
                    10, // 模块管理
                    18, // 邮件管理
                    9,  // 资源管理
                    11, // 系统设置
                    25  // 公告管理
                },
                { /* 6: 政府部门主页   */
                    1,  // 项目管理
                    2,  // 计划管理
                    22, // 监控预警
                    17, // 协作管理 
                    3,  // 文档管理
                    13, // 合同管理
                    8,  // 物资管理
                    14,  // 考勤管理
//                    5,  // 租赁管理
                    10, // 模块管理
                    18, // 邮件管理
                    9,  // 资源管理
                    11, // 系统设置
                    25  // 公告管理
                },
        };
        int[] moduleIds = allHomePages[type];        
        return moduleIds;
    }
    
    /**
     * 所有模块的图标
     * 
     * @return
     */
    public static int[] getAllIcons() {
        int[] allIcons = new int[] {
                R.drawable.project_manage_icon,
                R.drawable.plan_manage_icon,
                R.drawable.document_manage_icon,
                R.drawable.subcontract_manage_icon,
                R.drawable.lease_manage_icon,
                R.drawable.template_manage_icon,     //TBD purchase_manage_icon
                R.drawable.cost_manage_icon,
                R.drawable.inventory_manage_icon,
                R.drawable.res_manage_icon,
                R.drawable.template_manage_icon,
                R.drawable.settings_icon,
                R.drawable.change_manage_icon,
                R.drawable.contract_manage_icon,
                R.drawable.check_in_manage_icon,
                R.drawable.fund_manage_icon,
                R.drawable.template_manage_icon,     //TBD combination_manage_icon
                R.drawable.cooperation_manage_icon,
                R.drawable.email_manage_icon, 
                R.drawable.email_manage_icon,        // 安全管理
                R.drawable.quality_manage_icon,      // 质量管理
                R.drawable.completion_manage_icon,   // 竣工管理
                R.drawable.warning_monitor_icon,     // 预警监控
                R.drawable.template_manage_icon,     // 投资管理 TBD investment_manage_icon
                R.drawable.template_manage_icon,     // 投标管理 TBD bidding_manage_icon
                R.drawable.announcement_manage_icon, // 公告管理

        };
        return allIcons;
    }
    
    /**
     * 所有模块的主Activity
     * 
     * @return
     */
    public static Class<?>[] getAllClazzs() {
        Class<?>[] allClazz = new Class<?>[] { 
                ProjectActivity.class,               // 1. 项目信息
                CombinationActivity.class,           // 2. 计划管理
                DocumentActivity.class,              // 3. 文档管理
                SubcontractManagementActivity.class, // 4. 分包管理
                LeaseManagementActivity.class,       // 5. 租赁管理  
                PurchaseActivity.class,              // 6. 采购管理
                CostActivity.class,                  // 7. 成本利润
                InventoryActivity.class,             // 8. 物资管理
                ResourceActivity.class,              // 9. 基础资源
                TemplateActivity.class,              // 10. 模版管理
                SettingsActivity.class,              // 11. 系统设置
                ChangeActivity.class,                // 12. 签证管理
                ContractActivity.class,              // 13. 合同管理
                LocationActivity.class,              // 14. 考勤管理
                FundActivity.class,                  // 15. 资金管理
                CombinationView.class,               // 16. 组合管理
                CooperationActivity.class,           // 17. 协作管理
                EmailActivity.class,                 // 18. 邮件管理
                null,                                // 19. 安全管理  TBD
                null,                                // 20. 质量管理  TBD
                null,                                // 21. 竣工管理  TBD
                null,                                // 22. 预警监控  TBD
                InvestmentActivity.class,            // 23. 投资管理  
                BidActivity.class,                   // 24. 投标管理  TBD
                AnnouncementActivity.class           // 25. 公告管理
        };        
        return allClazz;
    }
    
    /**
     * 获取模块默认背景色
     * 当前有5中颜色，每个模块配置什么颜色，需要根据具体的显示顺序做调整
     * 基本规则：
     * -- 显示相邻的两个模块最好不是相同颜色
     * -- 文档管理为黄色
     * 
     * @return
     */
    public static int[] getDefaultBackground() {
        int[] backgroundColors = new int[] {
                R.drawable.home_gridview_green_bg,   // 1. 项目信息
                R.drawable.home_gridview_cyan_bg,    // 2. 计划管理
                R.drawable.home_gridview_yellow_bg,  // 3. 文档管理  -- 黄色
                R.drawable.home_gridview_blue_bg,
                R.drawable.home_gridview_purple_bg,
                R.drawable.home_gridview_yellow_bg,
                R.drawable.home_gridview_green_bg,
                R.drawable.home_gridview_blue_bg,
                R.drawable.home_gridview_yellow_bg,
                R.drawable.home_gridview_yellow_bg,
                R.drawable.home_gridview_purple_bg,
                R.drawable.home_gridview_green_bg,
                R.drawable.home_gridview_cyan_bg,
                R.drawable.home_gridview_blue_bg,
                R.drawable.home_gridview_purple_bg,
                R.drawable.home_gridview_yellow_bg,
                R.drawable.home_gridview_blue_bg,
                R.drawable.home_gridview_cyan_bg,
                R.drawable.home_gridview_blue_bg,
                R.drawable.home_gridview_purple_bg,
                R.drawable.home_gridview_yellow_bg,
                R.drawable.home_gridview_green_bg,
                R.drawable.home_gridview_cyan_bg,
                R.drawable.home_gridview_purple_bg,
                R.drawable.home_gridview_cyan_bg,
        };
        return backgroundColors;
    }
    
    private ModuleConfig() {
    }
}
