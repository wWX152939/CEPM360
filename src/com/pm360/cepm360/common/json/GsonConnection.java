
package com.pm360.cepm360.common.json;

import android.util.Log;

import com.google.gson.reflect.TypeToken;
import com.pm360.cepm360.app.common.activity.DataManagerInterface;
import com.pm360.cepm360.common.GLOBAL;
import com.pm360.cepm360.common.ResultStatus;
import com.pm360.cepm360.common.net.HttpClientTransmit;
import com.pm360.cepm360.entity.Files;
import com.pm360.cepm360.services.common.Operation;

import org.json.JSONException;

import java.io.File;
import java.lang.reflect.Type;
import java.util.List;
import java.util.Map;

/**
 * 通过Google的Gson解析服务器返回的json字符串
 * 
 * @author yuanlu
 */
public class GsonConnection extends AnalysisManager {
    private static final String TAG = "GsonConnection";
    private static final boolean DEBUG = true;

    /**
     * 构造函数
     */
    public GsonConnection() {
        super(HttpClientTransmit.getInstance());
    }

    /**
     * 解析网络返回的字符串
     */
    @Override
    public ResultStatus judgeException(Object object) {
        // 初步判断
        ResultStatus resultStatus = super.judgeException(object);
        // 初步判断无异常，解析服务器返回的字符串
        if (resultStatus.getCode() == SUCCESS) {
            if (object instanceof String) {
                String jsonStr = (String) object;
                // 判断返回字符串时的执行结果
                String code = (String) GsonUtils.getValueFromList(jsonStr, GLOBAL.RET_CODE_KEY);
                if (null != code) {
                    // 设置UI可能显示的信息
                    resultStatus.setMessage((String)
                            GsonUtils.getValueFromList(jsonStr, GLOBAL.RET_MESSAGE_KEY));
                    switch (code) {
                        case GLOBAL.RET_CODE_FAILED:
                            resultStatus.setCode(EXCEPTION);
                            break;
                        default:
                            break;
                    }
                }
            }
        }
        return resultStatus;
    }

    /**
     * 网络请求获取列表信息，并将其转换成对象列表
     * 
     * @param manager
     * @param parameters
     * @param cl 指定了对象类型
     * @return
     */
    @Override
    public AnalysisResult getData(Map<String, Object> parameters,
            Type type,
            Operation operation) {
        if (DEBUG)
            Log.d(TAG, "Enter getList()");
        // 网络请求
        Object object = mHttpTransmit.post(GsonUtils.toJson(parameters));

        List<?> list = null;
        // 异常判断
        ResultStatus resultStatus = judgeException(object);
        // 如果statusCode不为0，UI只需要statusCode
        if (resultStatus.getCode() == SUCCESS) {
            // 解析返回的list字符串
            if (type != null) {
                list = GsonUtils.jsonToList((String) object, type);
            }
            // 重新设置返回状态码
            switch (operation) {
                case LOGIN:
                    if (list.size() > 0) {
                        resultStatus.setCode(SUCCESS_LOGIN);
                        resultStatus.setMessage(GLOBAL.LOGIN_SUCCESS);
                    } else {
                        resultStatus.setCode(EXCEPTION_LOGIN);
                        resultStatus.setMessage(GLOBAL.LOGIN_FAILED);
                    }
                    break;
                case QUERY:
                    resultStatus.setCode(SUCCESS_DB_QUERY);
                    if (resultStatus.getMessage() == null 
                			|| resultStatus.getMessage().equals("")) {
                    	resultStatus.setMessage(GLOBAL.DB_QUERY_SUCCESSFUL);
                    }
                    break;
                case ADD:
                    resultStatus.setCode(SUCCESS_DB_ADD);
                    if (resultStatus.getMessage() == null 
                			|| resultStatus.getMessage().equals("")) {
                    	resultStatus.setMessage(GLOBAL.DB_ADD_SUCCESSFUL);
                    }
                    break;
                case MODIFY:
                    resultStatus.setCode(SUCCESS_DB_UPDATE);
                    // resultStatus.setMessage(GLOBAL.DB_UPDATE_SUCCESSFUL);
                    break;
                case DELETE:
                    resultStatus.setCode(SUCCESS_DB_DEL);
                    // resultStatus.setMessage(GLOBAL.DB_DELETEE_SUCCESSFUL);
                    break;
                case PUBLISH:
                    resultStatus.setCode(SUCCESS_PUBLISH);
                    // resultStatus.setMessage(GLOBAL.PUBLISH_SUCCESSFUL);
                    break;
                default:
                    break;
            }
        } else if (resultStatus.getCode() == EXCEPTION) {
            // 重新设置返回状态码
            switch (operation) {
                case LOGIN:
                    resultStatus.setCode(EXCEPTION_LOGIN);
                    break;
                case QUERY:
                    resultStatus.setCode(EXCEPTION_DB_QUERY);
                    // resultStatus.setMessage(GLOBAL.DB_QUERY_FAILED);
                    break;
                case ADD:
                    resultStatus.setCode(EXCEPTION_DB_ADD);
                    // resultStatus.setMessage(GLOBAL.DB_ADD_FAILED);
                    break;
                case MODIFY:
                    resultStatus.setCode(EXCEPTION_DB_UPDATE);
                    // resultStatus.setMessage(GLOBAL.DB_UPDATE_FAILED);
                    break;
                case DELETE:
                    resultStatus.setCode(EXCEPTION_DB_DELETE);
                    // resultStatus.setMessage(GLOBAL.DB_DELETE_FAILED);
                    break;
                case PUBLISH:
                    resultStatus.setCode(EXCEPTION_PUBLISH);
                    // resultStatus.setMessage(GLOBAL.PUBLISH_FAILED);
                    break;
                default:
                    break;
            }
        }
        // 打印状态码和list列表，可能为空
        if (DEBUG)
            Log.d(TAG, "resultStatus = " + resultStatus + "\nlist = " + list);
        // 回调UI界面接口填充数据

        AnalysisResult result = new AnalysisResult();
        result.setResultStatus(resultStatus);
        return result;
    }

    /**
     * 下载文件
     * 
     * @param parameters
     * @param fileName
     * @return
     * @throws JSONException
     */
    public AnalysisResult downloadFile(Map<String, Object> parameters,
            							String fileName,
            							boolean withProgress) {
    	Object object = null;
    	if (withProgress) {
    		object = mHttpTransmit.postDownloadFileWithProgress(parameters, fileName);
    	} else {
    		object = mHttpTransmit.postDownloadFile(parameters, fileName);
    	}
        return downloadFileCommon(object);
    }

    /**
     * 文件下载的公共操作
     * 
     * @param manager
     * @param object
     * @return
     */
    public AnalysisResult downloadFileCommon(Object object) {
        // 判断是否有异常，如果出现异常表示执行失败，否则执行成功
        ResultStatus resultStatus = judgeException(object);
        // 打印状态码
        if (DEBUG)
            Log.d(TAG, "resultStatus = " + resultStatus);
        // UI回调接口
        AnalysisResult result = new AnalysisResult();
        result.setResultStatus(resultStatus);
        return result;
    }
    
    /**
     * 上传文件
     * 
     * @param manager
     * @param file
     * @return
     */
    @SuppressWarnings("unchecked")
    public boolean uploadFile(DataManagerInterface manager, Map<String, Object> params, File file, boolean withProgress) {
        if (DEBUG) {
            Log.d(TAG, "[\"method\":\"uploadFile\", params = " + file.getAbsolutePath() + "]");
        }
        
        List<Files> list = null;
        Object object = null;
        
        // 获取tenant_id
        int tenant_id = (int) params.get("p0");
        
        // 上传文件
        if (!withProgress) {
            object = mHttpTransmit.postUploadFile(tenant_id, file);
        } else {
            object = mHttpTransmit.postUploadFileWithProgress(tenant_id, file);
        }
        
        ResultStatus resultStatus = judgeException(object);
        // 上传文件成功
        if (resultStatus.getCode() == SUCCESS) {
        	resultStatus.setCode(AnalysisManager.SUCCESS_UPLOAD);
            // 获取Files列表信息
        	list = (List<Files>) GsonUtils.jsonToList((String) object,
                    new TypeToken<List<Files>>() {
                    }.getType());
            if (DEBUG) Log.d(TAG, "list = " + list); 
        } else {
        	resultStatus.setCode(AnalysisManager.EXCEPTION_UPLOAD);
        }
        
        // 调用回调告知UI
        if (manager != null) {
            manager.getDataOnResult(resultStatus, list);
        }
        return true;
    }
}
