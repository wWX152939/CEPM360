package com.pm360.cepm360.common;

import android.annotation.SuppressLint;

public final class GLOBAL {
	// 数据库操作
	public static final String DB_ADD_SUCCESSFUL = "添加数据成功";
	public static final String DB_ADD_FAILED = "数据库异常，添加数据失败";
	public static final String DB_UPDATE_SUCCESSFUL = "更新数据成功";
	public static final String DB_UPDATE_FAILED = "数据库异常，更新数据失败";
	public static final String DB_DELETEE_SUCCESSFUL = "删除数据成功";
	public static final String DB_DELETE_FAILED = "数据库异常，删除数据失败";
	public static final String DB_QUERY_SUCCESSFUL = "查询数据成功";
	public static final String DB_QUERY_FAILED = "数据库异常，查询数据失败";

	public static final String RET_MESSAGE_KEY = "retmessage";
	public static final String RET_CODE_KEY = "retcode";

	public static final String RET_CODE_SUCCESSFUL = "SUCCESSFUL";
	public static final String RET_CODE_FAILED = "FAILED";
	public static final String PUBLISH_SUCCESSFUL = "发布成功";
	public static final String PUBLISH_FAILED = "发布失敗";
	public static final String INIT_VERSION = "初始版本";

	public static String CLASS_PARAM = "calss";
	public static String METHOD_PARAM = "method";
	public static String ARGS_PARAM = "args";

	public static String TASK_TYPE_WBS_KEY = "wbs";
	public static String TASK_TYPE_TASK_KEY = "task";
	public static String TASK_TYPE_MILE_KEY = "mile";
	public static String TASK_TYPE_VISA_KEY = "visa";

	public static String TASK_TYPE_WBS_VALUE = "WBS";
	public static String TASK_TYPE_TASK_VALUE = "任务作业";
	public static String TASK_TYPE_MILE_VALUE = "里程碑作业";
	public static String TASK_TYPE_VISA_VALUE = "签证";

	// 任务服务
	public static final String IS_PUBLISH = "yes";
	public static int LASTEST_CHANGE_ID = -1;
	public static boolean PUBLISH = false;
	@SuppressLint("SdCardPath")
	public static final String FILE_SAVE_PATH = "/sdcard/CEPM360";
	public static final String INCOME_CONTRACT_DOWNLOAD_FILE_DIR = FILE_SAVE_PATH
			+ "/contract/income/";
	public static final String OUTCOME_CONTRACT_DOWNLOAD_FILE_DIR = FILE_SAVE_PATH
			+ "/contract/outcome/";
	public static final String CONTRACT_CHANGE_DOWNLOAD_FILE_DIR = FILE_SAVE_PATH
			+ "/contract/change/";

	// 签证模板路径
	public static String QZMB_TEMPLET_DIR = "/qzmb/qzmb1.doc";
	
	// ciphertext
	public static byte[] CIPHERTEXT = new byte[]{'1', '2', '3', '4', '1', '2', '3', '4'};

	// SharedPreferentces key
	public static final String SHARED_PREFERENCES_KEY = "com.pm360.cepm360.prefs";

	// 本地异常
	public static final String HTTP_METHOD_NULL = "本地请求方法为空";
	// 接收内容为空
	public static final String RECV_CONTENT_NULL = "接收内容为空";
	// 400
	public static final String BAD_REQUEST = "请求错误，服务器不能理解请求内容";
	// 403
	public static final String SERVER_REFUSE = "服务器拒绝提供服务";
	// 404
	public static final String RESOURCE_NO_EXIST = "请求的资源不存在，请检查服务器地址是否正确";
	// 500
	public static final String SERVER_UNEXCEPTION_ERROR = "服务器发生不可预期的错误，请先检查调用方法";
	// 文件下载失败
	public static final String DOWNLOAD_SUCCES_STRING = "文件下载成功";
	// 文件下载失败
	public static final String DOWNLOAD_FAIL_STRING = "文件下载失败";
	// 文件已经存在
	public static final String FILE_EXIST = "文件已存在";
	// 登录成功
	public static final String LOGIN_SUCCESS = "登录成功";
	// 登录失败
	public static final String LOGIN_FAILED = "登录失败，登录名或密码错误";

	// 文件夹目录类型
	public static final String PUBLIC_DOCUMENT = "公共文档";
	public static final String PROJECT_DOCUMENT = "项目文档";
	public static final String PRIVATE_DOCUMENT = "个人文档";
	public static final String PROJECT_REPORT = "项目报告";

	// 文档目录类型（0：公共文档 1：项目文档 2：个人文档 3：文件归档 ）
	public final static int DIR_TYPE_PUBLIC = 0;
	public final static int DIR_TYPE_PROJECT = 1;
	public final static int DIR_TYPE_PERSONAL = 2;
	public final static int DIR_TYPE_ARCHIVE = 3;

	// 任务状态 1：已发布 2：增加 3：修改4：删除
	public static final String PUBLISH_STATUS[][] = { { "1", "已发布" },
			{ "2", "新增,未发布" }, { "3", "修改,未发布" }, { "4", "删除,未发布" },
			{ "5", "未发布" } };

	// 除个人文档和项目文档外，其他的文档路径和文档类型ID保持一致
	// 目录关联路径代码
	public static final String RELEVANCE_PATH_CODE[][] = { { "1", "公共文档" },
			{ "2", "施工方案" }, { "3", "现场图文" }, { "4", "承包合同" }, { "5", "采购合同" },
			{ "6", "租赁合同" }, { "7", "签证文件" }, { "8", "签证附件" } };
	// 文档类型 对应files表中的dir_type
	public static final String FILE_TYPE[][] = { { "1", "公共文档" },
			{ "2", "参考文档" }, { "3", "现场图文" }, { "4", "收入合同" }, { "5", "支出合同" },
			{ "6", "回款项" }, { "7", "签证文件" }, { "8", "签证附件" }, { "9", "个人文档" },
			{ "10", "项目文档" }, { "11", "组合工作日志附件" }, { "12", "组合风险识别附件" },
			{ "13", "组合参考文档" }, { "14", "工作日志" }, { "15", "形象成果" },
			{ "16", "安全监督" }, { "17", "质量文明" }, { "18", "邮件附件" },
			{ "19", "招标工作日志附件" }, { "20", "邀标附件" }, { "21", "投标附件" },
			{ "22", "预审附件" }, { "23", "澄清答疑附件" }, { "24", "报价附件" },
			{ "25", "评分附件" }, { "26", "招标计划附件" }, { "27", "模板文件" },
			{ "28", "分包合同" }, { "29", "风控经验" }, { "30", "支付项" },
			{ "31", "收入合同清单项" }, { "32", "支出合同清单项" }, { "33", "合同变更" },
			{ "34", "合同变更清单项" }, { "35", "物资附件" }, { "36", "置顶" } };

	public static final String FILE_ARCHIVE[][] = { { "0", "未归档" },
			{ "1", "已归档" }, { "2", "临时归档" } };
	public static final String FILE_TOP[][] = { { "0", "未置顶" }, { "1", "已置顶" } };

	// 组合管理节点类型
	public static final String COMBINATION_NODE_TYPE[][] = { { "1", "工作进度" },
			{ "2", "工作日志" }, { "3", "风险识别" }, { "4", "形象成果" }, { "5", "安全监督" },
			{ "6", "质量文明" } };

	// 邮件、消息定义
	public static final String MSG_PUSH[][] = { { "0", "未推送" }, { "1", "已推送" } };
	public static final String MSG_READ[][] = { { "0", "未读" }, { "1", "已读" } };
	public static final String MSG_DEL[][] = { { "0", "未删除" }, { "1", "已删除" } };
	public static final String RECEIVE_TYPE[][] = { { "1", "to" },
			{ "2", "cc" } };
	public static final String MSG_SENT[][] = { { "0", "未发送" }, { "1", "已发送" } };

	// 消息事务处理标志（0：未处理 1：已处理）
	public static final String MSG_IS_PROCESS[][] = { { "0", "未处理" },
			{ "1", "已处理" } };

	// 企业类型
	public static final String ENTERPRISE_TYPE[][] = { { "1", "管理方" },
			{ "2", "建设单位" }, { "3", "施工单位" }, { "4", "设计单位" }, { "5", "监理单位" },
			{ "6", "投资单位" }, { "7", "政府部门" } };

	// 企业人员角色
	public static final String ROLE_TYPE[][] = { { "GG", "高管" },
			{ "XMJL", "项目经理" }, { "ZG", "总工" }, { "JSY", "技术员" },
			{ "ZiLY", "资料员" }, { "ZLY", "质量员" }, { "CLY", "测量员" },
			{ "SGY", "施工员" }, { "CGY", "采购员" }, { "CW", "财务" }, { "KG", "库管" },
			{ "RS", "人事" }, { "GLY", "管理员" }, };

	// 招标流程节点
	public static final String BID_FLOW_TYPE[][] = { { "1", "招标计划" },
			{ "2", "邀标" }, { "3", "投标" }, { "4", "资格预审" }, { "5", "澄清答疑" },
			{ "6", "投标报价" }, { "7", "评标打分" }, { "8", "中标通知" }, { "9", "中标谈判" } };

	// 招标类型
	public static final String INVATE_BID_TYPE[][] = { { "1", "公开招标" },
			{ "2", "邀请招标" }, { "3", "竞争性谈判" }, { "4", "单一来源采购" }, { "5", "询价" } };

	// 招标模式
	public static final String INVATE_BID_MODE[][] = { { "1", "集中采购模式" },
			{ "2", "挂网模式" }, { "3", "药房托管模式" }, { "4", "打包模式" } };

	// 预审状态
	public static final String ZB_PRETRAIL_STATUS[][] = { { "1", "通过" },
			{ "2", "不通过" } };

	// 流程启用状态
	public static final String FLOW_STATUS[][] = { { "1", "启用" }, { "2", "关闭" } };
	// 流程类型
	public static final String FLOW_TYPE[][] = { { "1", "签证-审批" },
			{ "2", "合同付款审批" }, { "3", "合同付款会签" }, { "4", "合同变更审批" },
			{ "5", "合同变更会签" } };

	// 流程审批状态
	public static final String FLOW_APPROVAL_STATUS[][] = { { "1", "未提交" },
			{ "2", "审批中" }, { "3", "通过，待执行" }, { "4", "驳回" }, { "5", "已执行" } };

	// 合同类型
	public static final String CONTRACT_TYPE[][] = { { "1", "工程合同" },
			{ "2", "物资采购合同" }, { "3", "租赁合同" } };
	// 合同甲乙
	public static final String CONTRACT_JWYI[][] = { { "1", "甲方" },
			{ "2", "乙方" } };
	// 合同变更状态
	public static final String CONTRACT_CHANGE_STATUS[][] = { { "1", "生效" },
			{ "2", "协作处理中" }, { "3", "未提交" }, { "4", "内部审批中" },
			{ "5", "内部通过" }, { "6", "内部驳回" } };

	// 单位类型
	public static final String[][] UNIT_TYPE = { { "1", "m" }, { "2", "㎡" },
			{ "3", "㎥" }, { "4", "㎞" }, { "5", "t" }, { "6", "㎏" },
			{ "7", "米" }, { "8", "平方米" }, { "9", "立方米" }, { "10", "台" },
			{ "11", "块" }, { "12", "根" }, { "13", "工日" }, { "14", "组" },
			{ "15", "件" }, { "16", "个" }, { "17", "台班" }, { "18", "元" },
			{ "19", "套" }, { "20", "付" }, { "21", "片" }, { "22", "座" },
			{ "23", "口" }, { "24", "部" }, { "25", "段" }, { "26", "串" },
			{ "27", "束" }, { "28", "次" } };
	// 货币类型
	public static final String[][] COIN_TYPE = { { "1", "人民币" }, { "2", "港币" },
			{ "3", "美元" }, { "4", "英镑" } };
	// 物资分类
	public static final String[][] SAFETY_TYPE = { { "1", "安全检查" },
			{ "2", "安全事故" }, { "3", "安全措施" } };
	// 物资分类
	public static final String[][] QUALITY_TYPE = { { "1", "质量验收" },
			{ "2", "质量检查" }, { "3", "质量事故" } };

	// 物资分类
	public static final String[][] WZ_TYPE = { { "1", "材料" }, { "2", "设备" } };

	// 工种
	public static final String[][] CL_TYPE = { { "1", "土建" }, { "2", "木工" },
			{ "3", "安装" }, { "4", "电工" }, { "5", "消耗" }, { "6", "装饰" },
			{ "7", "办公用品" }, { "8", "其他" } };

	// 设备种类
	public static final String[][] SB_TYPE = { { "1", "机械" }, { "2", "架杆" },
			{ "3", "钢模板" }, { "4", "小型工具" }, { "5", "施工用电" }, { "6", "仪器仪表" },
			{ "7", "日常办公用品" }, { "8", "其他" } };

	// 人工种类
	public static final String[][] RG_TYPE = { { "1", "安装" }, { "2", "土建" },
			{ "3", "装饰" }, { "4", "环保" }, { "5", "其他" } };

	// 来往单位种类
	public static final String[][] LWDW_TYPE = { { "1", "甲方" }, { "2", "监理" },
			{ "3", "银行" }, { "4", "外包人工商" }, { "5", "材料供应商" },
			{ "6", "设备租赁商" }, { "7", "其他" } };

	// WBS模板种类
	public static final String[][] WBS_TYPE = { { "1", "安装" }, { "2", "土建" },
			{ "3", "装饰" }, { "4", "环保" }, { "5", "其他" } };

	// 文档模板种类
	public static final String[][] DOC_TYPE = { { "1", "安装" }, { "2", "土建" },
			{ "3", "装饰" }, { "4", "环保" }, { "5", "其他" } };
	// 采购状态
	public static final String[][] CG_TYPE = { { "1", "采购中" }, { "2", "到货" },
			{ "3", "缺货" } };
	// 入库状态
	public static final String[][] RK_TYPE = { { "1", "未入库" }, { "2", "已入库" },
			{ "3", "采购中" } };

	// 协作状态
	public static final String[][] COORPERATION_STATUS = { { "1", "未接受" },
			{ "2", "已接受" }, { "3", "未发起" } };

	// 任务共享状态
	public static final String[][] SHARE_TASK_STATUS = { { "1", "新增,未共享" },
			{ "2", "已共享" }, { "3", "未挂载" }, { "4", "已挂载" }, { "5", "删除,未共享" }, };

	// 性别
	public static final String[][] SEX_TYPE = { { "1", "男" }, { "2", "女" } };

	public static final String[][] MAVIN_TYPE = { { "1", "装饰设计工程" },
			{ "2", "消防工程" }, { "3", "土木工程" }, { "4", "土建工程" }, { "5", "土建" },
			{ "6", "通信工程" }, { "7", "市政工程" }, { "8", "其他" }, { "9", "给排水" },
			{ "10", "高级土木工程师" } };

	// 首页类型
	public static final String INDEX_TYPE[][] = { { "1", "代办事项" },
			{ "2", "我的任务" }, { "3", "形象进展" }, { "4", "最新反馈" }, { "5", "新闻公告" },
			{ "6", "最新文档" } };

	// 我的任务：(8)(16)
	// 我的待办：(1,2,3,4,5,6,7,13,14,22,23,24,32,33)
	// 最新反馈：(9,10,17,18,19,20,21)
	// 最新文档：(11,25,26,27,28,29,30)
	// 往来函：(15)
	// 公告：(12)
	// 注意：如果有新增的消息类型的时候，请同步添加R.array.message_types，并在MessageService做相应的归类
	public static final String MSG_TYPE_KEY[][] = { { "1", "签证-审批" },
			{ "2", "合同付款审批" }, { "3", "合同付款会签" }, { "4", "合同变更审批" },
			{ "5", "合同变更会签" }, { "6", "员工借款-审批" }, { "7", "外包人工单-审批 " },
			{ "8", "计划发布" }, { "9", "进度反馈" }, { "10", "现场图片" },
			{ "11", "公共文档" }, { "12", "公司公告" }, { "13", "采购执行" },
			{ "14", "承包合同付款-审批" }, { "15", "来往函" }, { "16", "任务发布" },
			{ "17", "任务反馈" }, { "18", "安全监督" }, { "19", "质量文明" },
			{ "20", "风险识别" }, { "21", "形象成果" }, { "22", "采购计划-会签" },
			{ "23", "分包合同付款-会签" }, { "24", "采购合同付款-会签" }, { "25", "项目文档" },
			{ "26", "邮件附件" }, { "27", "现场图片" }, { "28", "形象成果" },
			{ "29", "安全监督" }, { "30", "质量文明" }, { "31", "置顶" },
			{ "32", "协作邀请" }, { "33", "任务共享" } };

	// 消息对象key
	public static final String MSG_OBJECT_KEY = "message";
	// 支付合同的支付项审批消息intent action (msg type: 2/3)
	public static final String MSG_CONTRACT_PAYMENT_ACTION = "com.pm360.cepm360.app.module.contract.payment.action";
	// 合同变更审批消息intent action (msg type: 4/5)
	public final static String MSG_CONTRACT_CHANGE_ACTION = "com.pm360.cepm360.app.module.contract.ACTION.contract.change";
	// 外包人工单-审批 (msg type: 7/14)
	public static final String MSG_SUBCONTRACT_MANAGEMENT_ACTION = "com.pm360.cepm360.action.subcontract_manager";
	// 计划发布 (msg type: 8)
	public static final String MSG_TASK_ACTION = "com.pm360.cepm360.action.msg_task";
	// 计划反馈 (msg type: 9/18/19/20/21)
	public static final String MSG_FEEDBACK_ACTION = "com.pm360.cepm360.action.msg_feedback";
	// 现场图片(msg type: 10)
	public static final String MSG_FEEDBACK_DOC_ACTION = "com.pm360.cepm360.action.msg_feedback_doc";
	// 最新文档(msg type: 11/25/26/27/28/29/30)
	public static final String MSG_DOCUMENT_ACTION = "com.pm360.cepm360.action.msg_document";
	// 公告 (msg type: 12)
	public static final String MSG_ANNOUNCEMENT = "com.pm360.cepm360.action.announcement";
	// 采购执行 (msg type: 13)
	public static final String MSG_EXECUTIVE_ACTION = "com.pm360.cepm360.action.msg_executive";
	// 组合-任务发布 (msg type: 16)
	public static final String MSG_ZHTASK_ACTION = "com.pm360.cepm360.action.msg_zh_task";
	// 组合任务反馈 (msg type: 17)
	public static final String MSG_ZHFEEDBACK_ACTION = "com.pm360.cepm360.action.msg_zh_feedback";
	// 采购计划-会签 (msg type: 22)
	public final static String MSG_PURCHASE_PLAN_ACTION = "com.pm360.cepm360.action.purchase_plan";
	// 协作 (msg type: 32)
	public final static String MSG_COOPERATION_ACTION = "com.pm360.cepm360.action.cooperation";
	// 任务共享 (msg type: 33)
	public final static String MSG_TASK_SHARED_ACTION = "com.pm360.cepm360.action.task_shared";

	// 邮件附件表格类型
	public static final String MAIL_TABLE_TYPE[][] = { { "1", "合同变更表格" },
			{ "2", "现场图文" }, { "3", "安全监督" }, { "4", "质量文明" } };

	// 前置任务
	public static final int LOGIC_TYPE_0 = 0;
	// 后置任务
	public static final int LOGIC_TYPE_1 = 1;

	public static final int LOGIC_F_TYPE_0 = 0;
	public static final String LOGIC_F_TYPE_0_VALUE = "完成-开始（FS）";

	public static final int LOGIC_F_TYPE_1 = 1;
	public static final String LOGIC_F_TYPE_1_VALUE = "开始-开始（SS）";

	public static final int LOGIC_F_TYPE_2 = 2;
	public static final String LOGIC_F_TYPE_2_VALUE = "完成-完成（FF）";

	public static final int LOGIC_F_TYPE_3 = 3;
	public static final String LOGIC_F_TYPE_3_VALUE = "开始-完成（SF）";

	public static final int FEEDBACK_STATUS_0 = 0;
	public static final String FEEDBACK_STATUS_0_VALUE = "未开始";

	public static final int FEEDBACK_STATUS_1 = 1;
	public static final String FEEDBACK_STATUS_1_VALUE = "进行中";

	public static final int FEEDBACK_STATUS_2 = 2;
	public static final String FEEDBACK_STATUS_2_VALUE = "完成";

	public static final int IDU_INSERT = 1;// 新增操作
	public static final int IDU_DELETE = 2;// 删除操作
	public static final int IDU_UPDATE = 3;// 更新操作

	// 任务完成状态
	public static final String[][] TASK_STATUS_TYPE = { { "0", "未开始" },
			{ "1", "进行中" }, { "2", "完成" } };

	// 系统模块
	public static final String[][] SYS_MODULE = { { "1", "工程管理" },
			{ "2", "计划管理" }, { "3", "文档管理" }, { "4", "分包管理" }, { "5", "租赁管理" },
			{ "6", "采购管理" }, { "7", "成本利润" }, { "8", "物资管理" }, { "9", "资源管理" },
			{ "10", "模板管理" }, { "11", "系统设置" }, { "12", "签证管理" },
			{ "13", "合同管理" }, { "14", "考勤管理" }, { "15", "资金管理" },
			{ "16", "组合管理" }, { "17", "协作管理" }, { "18", "邮件管理" },
			{ "19", "安全管理" }, { "20", "质量管理" }, { "21", "竣工管理" },
			{ "22", "预警监控" }, { "23", "投资管理" }, { "24", "招标管理" },
			{ "25", "公告管理" } };

	// 权限操作名
	// _0 表示不在系统设置中显示 或 暂不使用
	public static final String[][] SYS_ACTION = {
			// 项目管理 (0,1)
			{ "1_1", "项目编辑" },
			{ "1_2", "项目查看" },

			// 计划 (2,3,4,5)
			{ "2_1", "计划编辑" }, { "2_2", "计划查看" }, { "2_0", "反馈编辑" },
			{ "2_0", "反馈查看" },

			// 文档管理 (6,7,8,9)
			{ "3_1", "项目文档编辑" }, { "3_2", "项目文档查看" }, { "3_3", "公共文档编辑" },
			{ "3_0", "公共文档查看" },

			// 分包管理 (10,11)
			{ "4_0", "分包编辑" }, { "4_0", "分包查看" },

			// 租赁管理 (12,13)
			{ "5_1", "租赁编辑" }, { "5_2", "租赁查看" },

			// 采购模块 (14,15)
			{ "6_0", "采购编辑" }, { "6_0", "采购查看" },

			// 成本利润 (16,17)
			{ "7_0", "成本利润编辑" }, { "7_0", "成本利润查看" },

			// 物资管理 (18,19)
			{ "8_1", "物资编辑" }, { "8_2", "物资查看" },

			// 资源管理 (20,21)
			{ "9_1", "基础信息编辑" }, { "9_2", "基础信息查看" },

			// 模板管理 (22,23)
			{ "10_1", "模板编辑" }, { "10_2", "模板查看" },

			// 系统设置 (24,25)
			{ "11_1", "系统设置" }, { "11_2", "设置查看" },

			// 签证管理 (26,27)
			{ "12_0", "签证编辑" }, { "12_0", "签证查看" },

			// 合同管理 (28,29)
			{ "13_1", "合同编辑" }, { "13_2", "合同查看" },

			// 考勤管理 (30,31)
			{ "14_1", "考勤编辑" }, { "14_2", "考勤查看" },

			// 资金管理 (32,33)
			{ "15_1", "资金编辑" }, { "15_2", "资金查看" },

			// 组合管理 (34,35)
			{ "16_1", "组合编辑" }, { "16_0", "组合查看" },

			// 协作管理 (36,37)
			{ "17_1", "协作编辑" }, { "17_2", "协作查看" },

			// 邮件管理 (38,39)
			{ "18_0", "邮件编辑" }, { "18_0", "邮件查看" },

			// 安全管理 (40,41)
			{ "19_0", "安全编辑" }, { "19_0", "安全查看" },

			// 质量管理 (42,43)
			{ "20_0", "质量编辑" }, { "20_0", "质量查看" },

			// 竣工管理 (44,45)
			{ "21_0", "竣工编辑" }, { "21_0", "竣工查看" },

			// 视频监控 (46,47)
			{ "22_1", "监控设置" }, { "22_2", "查看监控" },

			// 投资管理 (48,49)
			{ "23_1", "投资编辑" }, { "23_2", "投资查看" },

			// 投标管理 (50,51)
			{ "24_1", "招标编辑" }, { "24_2", "招标查看" },

			// 组合反馈 (52,53)
			{ "16_0", "组合反馈编辑" }, { "16_0", "组合反馈查看" },

			// 文件归档 (54, 55)
			{ "3_5", "文档归档编辑" }, { "3_6", "文档归档查看" },

			// 公告管理 (56,57)
			{ "25_1", "公告编辑" }, { "25_2", "公告查看" }, };

	public static final String[][] COMMENT_TYPE = { { "1", "操作不便" },
			{ "2", "功能缺陷" }, { "3", "其它" } };
}
