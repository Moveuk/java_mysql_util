package com.ldu.mysqlutil;

import java.io.PrintStream;
import java.io.PrintWriter;

public class MysqlUtilException extends RuntimeException {
	// 런타임 익셉션은 throw를 안던져도되므로 상속해서 사용함.
	// 오류 발생시 이쪽으로 던지는 이유는 오류 발생시 스레드를 바로 죽임.
	private Exception originException;

	public MysqlUtilException(Exception e) {
		this.originException = e;
	}

	@Override
	public String getMessage() {
		return originException.getMessage();
	}

	@Override
	public String getLocalizedMessage() {
		return originException.getLocalizedMessage();
	}

	@Override
	public synchronized Throwable getCause() {
		return originException.getCause();
	}

	@Override
	public StackTraceElement[] getStackTrace() {
		return originException.getStackTrace();
	}

	@Override
	public String toString() {
		return originException.toString();
	}

	@Override
	public void printStackTrace() {
		originException.printStackTrace();
	}

	@Override
	public void printStackTrace(PrintStream s) {
		originException.printStackTrace(s);
	}

	@Override
	public void printStackTrace(PrintWriter s) {
		originException.printStackTrace(s);
	}
}
