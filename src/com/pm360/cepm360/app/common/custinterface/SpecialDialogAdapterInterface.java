package com.pm360.cepm360.app.common.custinterface;



public interface SpecialDialogAdapterInterface<T> extends DialogAdapterInterface {
	
	/**
	 * 额外的数据操作
	 */
	void additionalSaveData(T t);
}
