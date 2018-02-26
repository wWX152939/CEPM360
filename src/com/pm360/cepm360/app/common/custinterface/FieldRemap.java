package com.pm360.cepm360.app.common.custinterface;

import java.util.Map;

public interface FieldRemap<T> {

	/**
	 * 将域映射表重新做映射
	 * @param displayFieldMap
	 * @param t
	 * @param position
	 */
	public void fieldRemap(Map<String, String> fieldMap, T t, int position);
}
