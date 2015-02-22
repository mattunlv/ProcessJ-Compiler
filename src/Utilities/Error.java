package Utilities;

import AST.*;

public class Error {
	public static String fileName = "";
	public static int errorCount = 0;
	public static String errors = "";
	
	public static void setFileName(String name) {
		fileName = name;
	}

	public static void error(AST e, String msg) {
		System.out.println(fileName + ":" + e.line + ": " + msg);
		System.exit(1);
	}   

	public static void error(String msg) {
		System.out.println(fileName + ": " + msg);
		System.exit(1);
	}

	public static void error(AST e, String msg, boolean terminate) {
		System.out.println(fileName + ":" + e.line + ": " + msg);
		if (terminate)
			System.exit(1);
	}   

	public static void error(AST e, String msg, boolean terminate, int errorno) {
		System.out.println(fileName + ":" + e.line + ": " + msg);
		System.out.println("Error number: " + errorno);
		if (terminate)
			System.exit(1);
		else {
			errorCount++;
			errors += "\n" + fileName + ":" + e.line + ": " + msg;
			errors += "\n" + "Error number: " + errorno;
		}
	}   
	
	public static Type addError(AST e, String msg, int errorno) {
		System.out.println(fileName + ":" + e.line + ": " + msg);
		System.out.println("Error number: " + errorno);

		errorCount++;
		errors += "\n" + fileName + ":" + e.line + ": " + msg;
		errors += "\n" + "Error number: " + errorno;
		return new ErrorType();
	}  
	
	
	public static void error(String msg, boolean terminate) {
		System.out.println(fileName + ": " + msg);
		if (terminate)
			System.exit(1);
	}
}
