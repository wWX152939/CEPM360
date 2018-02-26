package com.pm360.cepm360.app.common.custinterface;

/**
 * 
 * 标题: DocumentServiceInterface 
 * 描述: 
 * 作者： onekey
 * 日期： 2016年1月16日
 *
 * @param <T>
 * @param <B> File
 */
public interface DocumentServiceInterface<T, B> extends ServiceInterface<T>{
	/**
	 *  上传文档
	 */
	void uploadDocument(T t, B b);
	
}
