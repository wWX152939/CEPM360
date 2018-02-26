
package com.pm360.cepm360.common.json;

import java.io.File;
import java.lang.reflect.Type;
import java.util.List;
import java.util.Map;

import org.json.JSONException;

import android.util.Log;

import com.pm360.cepm360.app.common.activity.DataManagerInterface;
import com.pm360.cepm360.common.GLOBAL;
import com.pm360.cepm360.common.ResultStatus;
import com.pm360.cepm360.common.net.HttpClientTransmit;
import com.pm360.cepm360.entity.Files;
import com.pm360.cepm360.services.common.Operation;

public class JsonConnection extends AnalysisManager {
    // 调试有关
    private static final String TAG = "JsonConnection";
    private static final boolean DEBUG = true;

    public JsonConnection() {
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
                if (JsonUtils.hasKey(GLOBAL.RET_CODE_KEY, jsonStr)) {
                    String code = JsonUtils.getString(GLOBAL.RET_CODE_KEY, jsonStr);
                    // 设置UI可能会显示的信息
                    resultStatus.setMessage(JsonUtils.getString(GLOBAL.RET_MESSAGE_KEY, jsonStr));
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
     * 根据对象类型返回对象列表
     * 
     * @param parameters
     * @param c 对象类型
     * @return
     */
    public AnalysisResult getData(String parameters,
            Type type,
            Operation operation) {
        if (type != null && DEBUG)
            Log.d(TAG, "Enter getList(String) type = "
                    			+ ((Class<?>) type).getName());
        
        Object object = mHttpTransmit.post(parameters);
        List<?> list = null;
        // 异常处理
        ResultStatus resultStatus = judgeException(object);
        // 如果statusCode不为0，UI只需要statusCode
        if (resultStatus.getCode() == SUCCESS) {
            // 解析返回的list字符串
            if (type != null) {
                list = JsonUtils.getList((Class<?>) type, (String) object);
            }
            // 重新设置返回状态码
            switch (operation) {
                case LOGIN:
                    if (list != null && list.size() > 0) {
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
                    break;
                case DELETE:
                    resultStatus.setCode(SUCCESS_DB_DEL);
                    break;
                case PUBLISH:
                    resultStatus.setCode(SUCCESS_PUBLISH);
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
                    break;
                case ADD:
                    resultStatus.setCode(EXCEPTION_DB_ADD);
                    break;
                case MODIFY:
                    resultStatus.setCode(EXCEPTION_DB_UPDATE);
                    break;
                case DELETE:
                    resultStatus.setCode(EXCEPTION_DB_DELETE);
                    break;
                case PUBLISH:
                    resultStatus.setCode(EXCEPTION_PUBLISH);
                    break;
                default:
                    break;
            }
        }
        // 打印状态码和list列表，可能为空
        if (DEBUG) {
            Log.d(TAG, "resultStatus = " + resultStatus + "\nlist = ");
            if (list != null) {
            	for (int i = 0; i < list.size(); i++) {
            		Log.d(TAG, "[" + i + "] " + list.get(i));
            	}
            }
        }
        // 回调UI界面接口填充数据
        AnalysisResult result = new AnalysisResult();
        result.setResultStatus(resultStatus);
        result.setList(list);
        return result;
    }

    /**
     * 根据对象类型返回对象列表
     * 
     * @param parameters
     * @param c 对象类型
     * @return
     */
    @Override
    public AnalysisResult getData(Map<String, Object> parameters,
            Type type,
            Operation operation) {
        return getData(JsonUtils.mapToJson(parameters), type, operation);
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
    	Files files = (Files) parameters.get("p0");
    	Object object = null;
    	if (files.getAppUpdateFlag() == 1) {	// APK下载
    		object = mHttpTransmit.download(files.getFile_path(), fileName);
    	} else {
    		object = mHttpTransmit.downloadFile(files.getFile_path(), fileName);
    	}
    	
        // 判断是否有异常，如果出现异常表示执行失败，否则执行成功
        ResultStatus resultStatus = judgeException(object);
        
        // 打印状态码
        if (DEBUG) {
            Log.d(TAG, "resultStatus = " + resultStatus);
        }
        
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
    public boolean uploadFile(DataManagerInterface manager, 
    							Map<String, Object> params, 
    							File file, 
    							boolean withProgress) {
        if (DEBUG) {
            Log.d(TAG, "[\"method\":\"uploadFile\", params = " 
            						+ file.getAbsolutePath() + "]");
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
            list = (List<Files>) JsonUtils.getList(Files.class, (String) object);
            if (DEBUG) Log.d(TAG, "list = " + list); 
        } else {
        	resultStatus.setCode(AnalysisManager.EXCEPTION_UPLOAD);
        }
        // 回调UI
        if (manager != null) {
            manager.getDataOnResult(resultStatus, list);
        }
        return true;
    }
}
