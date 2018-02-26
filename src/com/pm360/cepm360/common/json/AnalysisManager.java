
package com.pm360.cepm360.common.json;

import android.util.Log;

import com.pm360.cepm360.app.common.activity.DataManagerInterface;
import com.pm360.cepm360.common.GLOBAL;
import com.pm360.cepm360.common.ResultStatus;
import com.pm360.cepm360.common.net.HttpClientTransmit;
import com.pm360.cepm360.services.common.Operation;

import org.json.JSONException;

import java.io.File;
import java.lang.reflect.Type;
import java.util.List;
import java.util.Map;

/**
 * 用于管理多个json解析工具
 * 
 * @author yuanlu
 */
public abstract class AnalysisManager {
    // 打印使用的TAG
    private static final String TAG = "AnalysisManager";
    // 是否使用Gson解析数据
    public static final boolean GSON = false;

    /* -------------------------操作成功----------------------------- */

    // 数据库添加数据异常
    public static final int SUCCESS = 0;
    // 无异常
    public static final int SUCCESS_DB_QUERY = 2;
    // 数据库添加成功
    public static final int SUCCESS_DB_ADD = 3;
    // 数据库删除成功
    public static final int SUCCESS_DB_DEL = 4;
    // 数据库更新成功
    public static final int SUCCESS_DB_UPDATE = 5;
    // 发布成功
    public static final int SUCCESS_PUBLISH = 6;
    // 文件下载成功
    public static final int SUCCESS_DOWNLOAD = 7;
    // 文件已存在
    public static final int SUCCESS_FILE_EXIST = 8;
    // 登录成功
    public static final int SUCCESS_LOGIN = 9;
    // 文件上传成功
    public static final int SUCCESS_UPLOAD = 10;

    /* -------------------------EXCEPTION------------------------ */

    // 数据库添加数据异常
    public static final int EXCEPTION = -1;
    // 数据库添加数据异常
    public static final int EXCEPTION_DB_QUERY = -2;
    // 数据库添加数据异常
    public static final int EXCEPTION_DB_ADD = -3;
    // 数据库删除数据异常
    public static final int EXCEPTION_DB_DELETE = -4;
    // 数据库更新数据异常
    public static final int EXCEPTION_DB_UPDATE = -5;
    // 发布异常
    public static final int EXCEPTION_PUBLISH = -6;
    // 文件下载成功
    public static final int EXCEPTION_DOWNLOAD = -7;
    // 方法空异常
    public static final int EXCEPTION_HTTP_METHOD = -8;
    // 登录失败
    public static final int EXCEPTION_LOGIN = -9;
    // 文件上传成功
    public static final int EXCEPTION_UPLOAD = 10;
    // 从服务器返回内容为空
    public static final int EXCEPTION_RECV_NULL = -11;

    // 请求错误，服务器不能理解
    public static final int EXCEPTION_BAD_REQUEST = 400;
    // 服务器拒绝提供服务
    public static final int EXCEPTION_SERVER_REFUSE = 403;
    // 请求的资源不存在
    public static final int EXCEPTION_RESOURCE_NO_EXIST = 404;
    // 服务器发送不可预期的错
    public static final int EXCEPTION_SERVER_UNEXCEPTION_ERROR = 500;

    // http客户端
    HttpClientTransmit mHttpTransmit;

    public AnalysisManager(HttpClientTransmit httpTransmit) {
        this.mHttpTransmit = httpTransmit;
    }

    /**
     * 服务器返回异常处理
     * 
     * @param jsonString 服务器返回实体内容字符串
     * @return
     * @throws JSONException
     */
    public ResultStatus judgeException(Object object) {
        ResultStatus resultStatus = new ResultStatus(SUCCESS, null);
        // 几乎不会出现该错误
        if (object == null) {
            Log.e(TAG, GLOBAL.HTTP_METHOD_NULL);
            resultStatus.setCode(EXCEPTION_HTTP_METHOD);
            resultStatus.setMessage(GLOBAL.HTTP_METHOD_NULL);
            return resultStatus;
        }
        // object是返回的网络状态码
        if (object instanceof Integer) {
            int statusCode = (Integer) object;
            // 将异常码保存到ResultStatus
            resultStatus.setCode(statusCode);
            String serverIp = HttpClientTransmit.getServerIp();
            switch (statusCode) {
                case EXCEPTION_BAD_REQUEST:
                    resultStatus.setMessage(GLOBAL.BAD_REQUEST + "当前请求服务IP："
                            + serverIp);
                    break;
                case EXCEPTION_SERVER_REFUSE:
                    resultStatus.setMessage(GLOBAL.SERVER_REFUSE + "当前请求服务IP："
                            + serverIp);
                    break;
                case EXCEPTION_RESOURCE_NO_EXIST:
                    resultStatus.setMessage(GLOBAL.RESOURCE_NO_EXIST + "当前请求服务IP："
                            + serverIp);
                    break;
                case EXCEPTION_SERVER_UNEXCEPTION_ERROR:
                    resultStatus.setMessage(GLOBAL.SERVER_UNEXCEPTION_ERROR + "当前请求服务IP："
                            + serverIp);
                    break;
                case SUCCESS_DOWNLOAD:
                    resultStatus.setMessage(GLOBAL.DOWNLOAD_SUCCES_STRING);
                    break;
                case EXCEPTION_DOWNLOAD:
                    resultStatus.setMessage(GLOBAL.DOWNLOAD_FAIL_STRING);
                    break;
                case SUCCESS_FILE_EXIST:
                    resultStatus.setMessage(GLOBAL.FILE_EXIST);
                    break;
                default:
                    break;
            }
            return resultStatus;
        }
        /*
         * 网络正常情况下，服务器返回的字符串， 这里可能是正常的数据，也可能是服务器处理请求异常的记录
         */
        if (object instanceof String) {
            String str = (String) object;
            // 返回内容为空""
            if (str.equals("")) {
                Log.e(TAG, GLOBAL.RECV_CONTENT_NULL);
                resultStatus.setCode(EXCEPTION_RECV_NULL);
                resultStatus.setMessage(GLOBAL.RECV_CONTENT_NULL);
            }
        }
        return resultStatus;
    }

    /**
     * 指定类型的解析方式获取列表，可以保证转换出来的类型是正确的
     * 
     * @param manager
     * @param params
     * @param type
     * @return
     */
    public abstract AnalysisResult getData(Map<String, Object> params,
            								Type type,
            								Operation operation);

    /**
     * 上传表单和文件
     * 
     * @param manager
     * @param params
     * @param file
     * @return
     */
    public abstract boolean uploadFile(DataManagerInterface manager,
							            Map<String, Object> params,
							            File file,
							            boolean withProgress);

    /**
     * 下载文件
     * 
     * @param parameters
     * @param fileName
     * @return
     * @throws JSONException
     */
    public abstract AnalysisResult downloadFile(Map<String, Object> parameters, 
    											String fileName,
    											boolean withProgress);

    /**
     * 解析结果类
     * @author yuanlu
     *
     */
    public class AnalysisResult {
    	
        private ResultStatus resultStatus;
        private List<?> list;
        
        public ResultStatus getResultStatus() {
            return resultStatus;
        }
        
        public void setResultStatus(ResultStatus resultStatus) {
            this.resultStatus = resultStatus;
        }
        
        public List<?> getList() {
            return list;
        }
        
        public void setList(List<?> list) {
            this.list = list;
        }
    }
}
