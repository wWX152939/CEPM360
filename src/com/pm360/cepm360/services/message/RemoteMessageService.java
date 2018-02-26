package com.pm360.cepm360.services.message;

import com.google.gson.reflect.TypeToken;
import com.pm360.cepm360.app.common.activity.DataManagerInterface;
import com.pm360.cepm360.common.json.AnalysisManager;
import com.pm360.cepm360.entity.Message;
import com.pm360.cepm360.entity.User;
import com.pm360.cepm360.services.common.AsyncTaskPM360;
import com.pm360.cepm360.services.common.Operation;
import com.pm360.cepm360.services.common.RemoteService;

import java.lang.reflect.Type;
import java.util.List;

public class RemoteMessageService {
	// 框架服务类全限定名
	private static final String SERVICE_NAME = "com.pm360.cepm360.services.message.MessageService";
	// 单例类变量
	private static RemoteMessageService gService;
    // 消息
    private Message mMessage;
	
	/**
	 * 单例模式
	 * @return
	 */
    public static synchronized RemoteMessageService getInstance() {
        if (gService == null) {
            gService = new RemoteMessageService();
        }
        return gService;
    }
    
    /**
     * 销毁实例对象
     */
    public static void destroyInstance() {
    	gService = null;
    }
    
    private RemoteMessageService() {
    	
    }
    
    /**
     * 设置变更
     * @param message
     */
    public RemoteMessageService setMessage(Message message) {
    	mMessage = message;
    	return this;
    }
    
    /**
     * 获取当前变更
     * @return
     */
    public Message getMessage() {
    	return mMessage;
    }
    
	/**
	 * 获取未读消息
	 * 
	 * @param manager
	 * @param user
	 */
    public AsyncTaskPM360 getUnRreadMessage(final DataManagerInterface manager, User user) {     	
    	// 初始化返回类型
    	Type type = AnalysisManager.GSON 
				? new TypeToken<List<Message>>() {}.getType()
						: Message.class;
				
    	// 设置调用参数，调用远程服务	
    	return new RemoteService()
			.setParams( SERVICE_NAME,
						"getUnRreadMessage",
						user
						).call(manager, type, Operation.QUERY);
    }
    
    /**
	 * 获取该用户所有消息
	 * 
	 * @param manager
	 * @param user
	 */
    public AsyncTaskPM360 getALLMessage(final DataManagerInterface manager, User user) {     	
    	// 初始化返回类型
    	Type type = AnalysisManager.GSON 
				? new TypeToken<List<Message>>() {}.getType()
						: Message.class;
				
    	// 设置调用参数，调用远程服务	
    	return new RemoteService()
			.setParams( SERVICE_NAME,
						"getALLMessage",
						user
						).call(manager, type, Operation.QUERY);
    }
    
    /**
     * 读取消息
     * 
     * @param manager
     * @param user_id
     * @param message_id
     */
    public AsyncTaskPM360 readMessage(final DataManagerInterface manager, Message message) {				
    	// 设置调用参数，调用远程服务	
    	return new RemoteService()
			.setParams( SERVICE_NAME,
						"readMessage",
						message.getMessage_id()
						).call(manager, Operation.QUERY);
    }
    
}
