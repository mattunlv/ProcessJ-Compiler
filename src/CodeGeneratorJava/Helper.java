package CodeGeneratorJava;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStreamWriter;
import java.io.Writer;

import AST.AltCase;
import AST.ExprStat;
import AST.ProcTypeDecl;
import AST.SkipStat;
import AST.Statement;
import AST.TimeoutStat;
import AST.Type;
import Utilities.Log;
import Utilities.Settings;

public class Helper {

    static String getQualifiedPkg(ProcTypeDecl pd, String originalFilename) {
        String myPkg = pd.myPackage;

        if (myPkg.contains(originalFilename)) {
            //invocation from the same file
            return originalFilename;
        } else {
            //invocation from imported file
            String includeDirPath = Settings.includeDir + File.separator + Settings.targetLanguage + File.separator;
            includeDirPath = includeDirPath.replaceAll(File.separator, "\\."); // replace all '/' with '.'
            myPkg = myPkg.replaceAll(includeDirPath, "");
            return myPkg;
        }
    }

    static String convertToFieldName(String name, boolean formals, int cnt) {
        String fieldName = "";
        if (name != null && !name.isEmpty()) {
            if (formals) {
                fieldName = "_pd$" + name;
            } else {
                fieldName = "_ld" + cnt + "$" + name;
            }
        }
        return fieldName;
    }

    /**
     * Check if given AltCase is a timeout.
     * 
     * @param altCase
     *            : AltCase to check.
     * @return was this AltCase a timer?
     */
    static boolean caseIsTimeout(AltCase altCase) {
        Statement stmt = altCase.guard().guard();
        return (stmt instanceof TimeoutStat);
    }

    /**
     * Check if given AltCase is a Skip.
     * 
     * @param altCase
     *            : AltCase to check.
     * @return was this AltCase a Skip?
     */
    static boolean caseIsSkip(AltCase altCase) {
        Statement stmt = altCase.guard().guard();
        return (stmt instanceof SkipStat);
    }

    /**
     * Check if given AltCase is a Skip.
     * 
     * @param altCase
     *            : AltCase to check.
     * @return was this AltCase a Skip?
     */
    static boolean caseIsChannel(AltCase altCase) {
        Statement stmt = altCase.guard().guard();
        return (stmt instanceof ExprStat);
    }

    static boolean isYieldingProc(ProcTypeDecl pd) {
        if (pd == null) {
            return false;
        }
        return pd.annotations().isDefined("yield") && Boolean.valueOf(pd.annotations().get("yield"));
    }

    /**
     * Returns the wrapper class name of primitive data types.
     */
    static String getWrapperType(Type t) {
        String typeStr = "";

        if (t.isIntegerType()) {
            typeStr = Integer.class.getSimpleName();
        } else if (t.isLongType()) {
            typeStr = Long.class.getSimpleName();
        } else if (t.isFloatType()) {
            typeStr = Float.class.getSimpleName();
        } else if (t.isDoubleType()) {
            typeStr = Double.class.getSimpleName();
        } else if (t.isByteType()) {
            typeStr = Byte.class.getSimpleName();
        } else if (t.isBooleanType()) {
            typeStr = Boolean.class.getSimpleName();
        } else if (t.isCharType()) {
            typeStr = Character.class.getSimpleName();
        } else if (t.isShortType()) {
            typeStr = Short.class.getSimpleName();
        }

        return typeStr;
    }

    /**
     * Given a string it will write to the file as the final output of the compiler.
     */
    static void writeToFile(String finalOutput, String filename, String workdir) {
        Writer writer = null;

        try {
            String home = System.getProperty("user.home");
            File dir = new File(home + File.separator + workdir);
            if (!dir.exists()) {
                dir.mkdir();
            }

            Log.log(dir.getAbsolutePath() + filename);

            String javafile = dir.getAbsolutePath() + File.separator + filename + ".java";

            Log.log("Writing Java File to: " + javafile);

            FileOutputStream fos = new FileOutputStream(javafile);
            writer = new BufferedWriter(new OutputStreamWriter(fos, "utf-8"));
            writer.write(finalOutput);
        } catch (IOException ex) {
            Log.log("IOException: Could not write to file for some reason :/");
        } finally {
            try {
                writer.close();
            } catch (Exception ex) {
                Log.log("Could not close file handle!");
            }
        }
    }

}
