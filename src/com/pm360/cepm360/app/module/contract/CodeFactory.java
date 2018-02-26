package com.pm360.cepm360.app.module.contract;

import android.annotation.SuppressLint;

import com.pm360.cepm360.common.util.DateUtils;

import java.util.Date;
import java.util.HashMap;
import java.util.Map;

/**
 * 编码工厂
 * @author yuanlu
 *
 */
@SuppressLint("UseSparseArrays") 
public class CodeFactory {
	
	// 合同变更
	public static final int CONTRACT_CHANGE_CODE = 0;
	
	// 回款清单
	public static final int RETURN_MONEY_CODE = 1;
	
	// 支付清单
	public static final int PAYMENT_CODE = 2;
	
	// 映射表
	private static Map<Integer, String> mCodeMap;
	
	// 初始化编码映射表
	static {
		mCodeMap = new HashMap<Integer, String>();
		
		mCodeMap.put(CONTRACT_CHANGE_CODE, "HTBG");
		mCodeMap.put(RETURN_MONEY_CODE, "HK");
		mCodeMap.put(PAYMENT_CODE, "ZF");
	}
	
	/**
	 * 获取编码前缀
	 * @param code
	 * @return
	 */
	public static String getCodePrefix(int prefixCode) {
		return mCodeMap.get(prefixCode);
	}
	
	/**
	 * 编码生成函数
	 * @param codeType
	 * @return
	 */
	public static String produce(int codeType) {
		StringBuilder codeBuilder = new StringBuilder();
		
		// 编号前缀
		codeBuilder.append(mCodeMap.get(codeType));
		
		// 编号后缀-日期
		codeBuilder.append(DateUtils.dateToString(DateUtils.FORMAT_FULL_2, new Date()));
		
		return codeBuilder.toString();
	}
}
