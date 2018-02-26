package com.pm360.cepm360.app.common.custinterface;

/**
 * 通用回调接口
 * @author yuanlu
 *
 */
public interface CallBack<R, A> {

	/**
	 * 通用回调函数, 适用于所有单一接口的回调场景
	 * @param a
	 */
	public R callBack(A a);
}
