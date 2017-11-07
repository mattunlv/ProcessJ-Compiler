package Utilities;

import AST.AST;

public class Log {
    public static boolean doLog = false;

    public static void startLogging() {
        doLog = true;
    }

    public static void stopLogging() {
        doLog = false;
    }

    public static void log(String s) {
        if (doLog)
            System.out.println(Error.fileName + ":" + s);
    }

    public static void log(AST a, String s) {
        if (doLog)
            System.out.println(Error.fileName + ":" + a.line + " " + s);
    }

    public static void logHeader(String s) {
        if (doLog)
            System.out.println(s);
    }

    public static void logNoNewline(String s) {
        if (doLog)
            System.out.print(Error.fileName + ":" + s);
    }

    public static void logNoNewline(AST a, String s) {
        if (doLog)
            System.out.print(Error.fileName + ":" + a.line + " " + s);
    }

}
