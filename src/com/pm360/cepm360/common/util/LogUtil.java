package com.pm360.cepm360.common.util;

import android.util.Log;

public class LogUtil {
	private static StackTraceElement[] currentThread;
	private static String tagName;
	private static String msgT;
	private static String msgC;
	private static String callTraceStack;
	private static int curLogLevel = Log.VERBOSE;

	public static int getCurLogLevel() {
		return curLogLevel;
	}

	public static void setCurLogLevel(int curLogLevel) {
		LogUtil.curLogLevel = curLogLevel;
	}

	/**
	 * format log, get ClassName, line number and method for tag
	 * @param msg
	 * @param isPrintStack
	 */
	private synchronized static void initTrace(String msg, int... isPrintStack) {
		int isPrintStackOne = isPrintStack.length > 0 ? isPrintStack[0] : 10;
		currentThread = Thread.currentThread().getStackTrace();
		int curentIndex = 4;
		String className = currentThread[curentIndex].getFileName();
		int endIndex = className.lastIndexOf(".");
		tagName = endIndex < 0 ? className : className.substring(0, endIndex);
		msgT = className + ":"
				+ currentThread[curentIndex].getLineNumber() + ": ";
		msgC = msg;
		if (isPrintStackOne > 0) {
			StringBuilder sb = new StringBuilder();
			sb.append("callTraceStack:[");
			for (int i = curentIndex; i < curentIndex + isPrintStackOne
					&& i < currentThread.length; i++) {
				sb.append(currentThread[i].getFileName() + ":"
						+ currentThread[i].getLineNumber() + ":"
						+ currentThread[i].getMethodName() + "()" + "<--");
			}
			sb.append("]");
			callTraceStack = sb.toString();
			msgC += callTraceStack;
		}
	}

	/**
	 * error level
	 * @param msg
	 * @param print stack if printStack is true
	 */
	public static void e(String msg, boolean printStack) {
		e(msg, printStack ? 105 : 0);
	}

	/**
	 * warning level
	 * @param msg
	 * @param print stack if printStack is true
	 */
	public static void w(String msg, boolean printStackNum) {
		w(msg, printStackNum ? 105 : 0);
	}

	/**
	 * debug level
	 * @param msg
	 * @param print stack if printStack is true
	 */
	public static void d(String msg, boolean printStackNum) {
		d(msg, printStackNum ? 105 : 0);
	}

	/**
	 * verbose level
	 * @param msg
	 * @param print stack if printStack is true
	 */
	public static void v(String msg, boolean printStackNum) {
		v(msg, printStackNum ? 105 : 0);
	}

	/**
	 * info level
	 * @param msg
	 * @param print stack if printStack is true
	 */
	public static void i(String msg, boolean printStackNum) {
		i(msg, printStackNum ? 105 : 0);
	}

	/**
	 * error level
	 * @param msg
	 * @param printStackNum: print the stack's log (depend on printStackNum)
	 */
	public static void e(String msg, int... printStackNum) {
		if (curLogLevel <= Log.ERROR) {
			initTrace(msg, printStackNum.length > 0 ? printStackNum[0] : 0);
			Log.e(tagName, msgT + msgC);
		}
	}

	/**
	 * warning level
	 * @param msg
	 * @param printStackNum: print the number of stack's log
	 */
	public static void w(String msg, int... printStackNum) {
		if (curLogLevel <= Log.WARN) {
			initTrace(msg, printStackNum.length > 0 ? printStackNum[0] : 0);
			Log.w(tagName, msgT + msgC);
		}
	}

	/**
	 * debug level
	 * @param msg
	 * @param printStackNum: print the stack's log (depend on printStackNum)
	 */
	public static void d(String msg, int... printStackNum) {
		if (curLogLevel <= Log.DEBUG) {
			initTrace(msg, printStackNum.length > 0 ? printStackNum[0] : 0);
			Log.d(tagName, msgT + msgC);
		}
	}

	/**
	 * verbose level
	 * @param msg
	 * @param printStackNum: print the stack's log (depend on printStackNum)
	 */
	public static void v(String msg, int... printStackNum) {
		if (curLogLevel <= Log.VERBOSE) {
			initTrace(msg, printStackNum.length > 0 ? printStackNum[0] : 0);
			Log.v(tagName, msgT + msgC);
		}
	}

	/**
	 * info level
	 * @param msg
	 * @param printStackNum: print the stack's log (depend on printStackNum)
	 */
	public static void i(String msg, int... printStackNum) {
		if (curLogLevel <= Log.INFO) {
			initTrace(msg, printStackNum.length > 0 ? printStackNum[0] : 0);
			Log.i(tagName, msgT + msgC);
		}
	}
	
	public static void v(String msg) {
		if (curLogLevel <= Log.VERBOSE) {
			initTrace(msg, 0);
			Log.v(tagName, msgT + msgC);
		}
	}
	public static void i(String msg) {
		if (curLogLevel <= Log.INFO) {
			initTrace(msg, 0);
			Log.i(tagName, msgT + msgC);
		}
	}
	public static void d(String msg) {
		if (curLogLevel <= Log.DEBUG) {
			initTrace(msg, 0);
			Log.d(tagName, msgT + msgC);
		}
	}
	public static void w(String msg) {
		if (curLogLevel <= Log.WARN) {
			initTrace(msg, 0);
			Log.w(tagName, msgT + msgC);
		}
	}
	public static void e(String msg) {
		if (curLogLevel <= Log.ERROR) {
			initTrace(msg, 0);
			Log.e(tagName, msgT + msgC);
		}
	}
}