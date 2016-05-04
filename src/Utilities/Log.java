package Utilities;

import AST.*;

public class Log {
    public static void log(String s) {
        //System.out.println(Error.fileName + ":" + s);
        System.out.println(s);
    }

    public static void log(AST a, String s) {
	//System.out.println(Error.fileName + ":" + a.line + " " + s);
	System.out.println(Error.fileName + ":" + a.line + " " + s);
    }

    public static void logHeader(String s) {
        System.out.println(s);
    }

}
