package Utilities;
import java.io.File;

import AST.*;

public class Error {
	public static String fileName = "";
	public static int errorCount = 0;
	public static String errors = "";
    public static String packageName ="";


    public static void setPackageName(String packageName) {
	// First strip the .pj part
	String str = packageName.replaceAll("\\.pj$", "");
	// Now remove the absolute path
	String absPath = new File("").getAbsolutePath() + "/";
	str = str.replaceAll(absPath,"");
	// replace all / with .
	str = str.replaceAll("/","\\.");
	Error.packageName = str;
    }

    

	
	public static void setFileName(String name) {
	    // remove all double //:
	    String str = name.replaceAll("//","/");
	    // Now remove the absolute path
	    String absPath = new File("").getAbsolutePath() + "/";
	    str = str.replaceAll(absPath,"");
	    fileName = str;
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
