import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;

import AST.AST;
import AST.Compilation;
import CodeGeneratorC.CodeGeneratorC;
import CodeGeneratorJava.CodeGeneratorJava;
import Library.Library;
import Parser.parser;
import Scanner.Scanner;
import Utilities.Error;
import Utilities.Log;
import Utilities.Settings;
import Utilities.SymbolTable;

public class ProcessJc {
    public static void usage() {
        System.out.println("ProcessJ Version 1.0");
        System.out.println("usage: pjc [-I dir] [-pp language] [-t language] input");
        System.out.println("  -I dir\tSets the include directory (default is include)");
        System.out.println("  -pp\tDo not output code but produce a pretty print");
        System.out.println("     \tlanguage can be one of:");
        System.out.println("     \tlatex : produce latex includable output.");
        System.out.println("     \tprocessj : produce ProcessJ output.");
        System.out.println("  -t\tSets the target language.");
        System.out.println("     \tlanguage can be one of:");
        System.out.println("     \tc: c source is written, compiled and linked with the CCSP runtime.");
        System.out.println("     \tjvm: JVM class files are written.");
        System.out.println("     \tjs: JavaScript is written.");
        System.out.println("  -sts\tDumps the global symbole table structure.");
        System.out.println("  -help\tPrints this message.");
    }

    static boolean sts = false;

    /*	public static void writeTree(Compilation c) {
    try {
    FileOutputStream fileOut = new FileOutputStream("Tree.ser");
    ObjectOutputStream out = new ObjectOutputStream(fileOut);
    out.writeObject(c);
    out.close();
    fileOut.close();
    System.out.printf("Serialized data is saved in Tree.ser");
    } catch (IOException e) {
    e.printStackTrace();
    }
    }
    */

    public static void main(String argv[]) {
        AST root = null;

        if (argv.length == 0) {
            System.out.println("ProcessJ Compiler version 1.00");
            usage();
            System.exit(1);
        }

        int debugLevel = 0;
        for (int i = 0; i < argv.length; i++) {
            Scanner s = null;
            parser p = null;
            try {
                if (argv[i].equals("-")) {
                    s = new Scanner(System.in);
                } else if (argv[i].equals("-I")) {
                    if (argv[i + 1].charAt(argv[i + 1].length() - 1) == '/')
                        argv[i + 1] = argv[i + 1].substring(0, argv[i + 1].length() - 1);
                    Settings.includeDir = argv[i + 1];
                    i++;
                    continue;
                } else if (argv[i].equals("-t")) {
                    if (argv[i + 1].equals("c") || argv[i + 1].equals("JVM") || argv[i + 1].equals("js")) {
                        Settings.targetLanguage = argv[i + 1];
                        i++;
                        continue;
                    } else {
                        System.out.println("Unknown target option for -t");
                        System.exit(1);
                    }
                } else if (argv[i].equals("-help")) {
                    usage();
                    System.exit(1);
                    continue;
                } else if (argv[i].equals("-sts")) {
                    sts = true;
                    continue;
                } else {
                    //System.out.println("Setting scanner");
                    Error.setFileName(argv[i]);
                    Error.setPackageName(argv[i]);
                    s = new Scanner(new java.io.FileReader(argv[i]));
                }
                p = new parser(s);
            } catch (java.io.FileNotFoundException e) {
                System.out.println("File not found : \"" + argv[i] + "\"");
                System.exit(1);
            } catch (ArrayIndexOutOfBoundsException e) {
                usage();
            }

            try {
                java_cup.runtime.Symbol r = ((parser) p).parse();
                root = (AST) r.value;
            } catch (java.io.IOException e) {
                e.printStackTrace();
                System.exit(1);
            } catch (Exception e) {
                e.printStackTrace();
                System.exit(1);
            }

            Log.stopLogging();

            // cast the result from the parse to a Compilation - this is the root of the tree
            Compilation c = (Compilation) root;

            // SYNTAX TREE PRINTER
            //c.visit(new ParseTreePrinter());

            // Decode pragmas - these are used for generating stubs from libraries. 
            // No regular program would have them.
            Library.decodePragmas(c);
            Library.generateLibraries(c);

            // This table will hold all the top level types
            SymbolTable globalTypeTable = new SymbolTable("Main file: " + Error.fileName);

            ////////////////////////////////////////////////////////////////////////////////
            // TOP LEVEL DECLARATIONS
            c.visit(new NameChecker.TopLevelDecls<AST>(globalTypeTable));
            globalTypeTable = SymbolTable.hook;

            // Resolve types from imported packages.
            c.visit(new NameChecker.ResolvePackedTypes());

            if (sts) // dump the symbol table structure
                globalTypeTable.printStructure("");
            ////////////////////////////////////////////////////////////////////////////////
            // NAME CHECKER
            c.visit(new NameChecker.NameChecker<AST>(globalTypeTable));
            if (Error.errorCount != 0) {
                System.out.println("---------- Error Report ----------");
                System.out.println(Error.errorCount + " errors in symbol resolution - fix these before type checking.");
                System.out.println(Error.errors);
                System.out.println("** COMPILATION FAILED **");
                System.exit(1);
            }

            // Re-construct Array Types correctly
            root.visit(new NameChecker.ArrayTypeConstructor());

            ////////////////////////////////////////////////////////////////////////////////
            // TYPE CHECKER
            c.visit(new TypeChecker.TypeChecker(globalTypeTable));

            if (Error.errorCount != 0) {
                System.out.println("---------- Error Report ----------");
                System.out.println(Error.errorCount + " errors in type checking - fix these before code generation.");
                System.out.println(Error.errors);
                System.exit(1);
            }

            ////////////////////////////////////////////////////////////////////////////////
            // OTHER SEMANTIC CHECKS

            c.visit(new Reachability.Reachability());
            c.visit(new ParallelUsageCheck.ParallelUsageCheck());

            ////////////////////////////////////////////////////////////////////////////////
            // CODE GENERATOR

            if (Settings.targetLanguage.equals("c"))
                c.visit(new CodeGeneratorC<AST>());
            else if (Settings.targetLanguage.equals("JVM"))
                generateCodeJava(c, argv[i], globalTypeTable);
            else {
                System.out.println("Unknown target language selected");
                System.exit(1);
            }
            System.out.println("** COMPILATION SUCCEEDED **");
        }
    }

    private static void generateCodeJava(Compilation c, String filename, SymbolTable topLevelDecls) {
        CodeGeneratorJava<Object> generator = new CodeGeneratorJava<Object>(topLevelDecls);
        /*
         * Extracting the filename without the path and extension (.pj).
         */
        String[] tokens = filename.split(File.separator);
        String n = tokens[tokens.length - 1];
        String name = n.substring(0, n.lastIndexOf("."));

        generator.setSourceFilename(name);
        generator.setWorkingDirectory(getWorkDirConfig());

        c.visit(generator);

        return;

    }

    public static String getWorkDirConfig() {
        String home = System.getProperty("user.home");
        String configPath = home + "/.pjconfig";
        File in = new File(configPath);
        String dir = null;

        try {
            BufferedReader br = new BufferedReader(new FileReader(in));

            String line;
            while ((line = br.readLine()) != null) {
                String[] tokens = line.split("=");
                if (tokens.length != 2)
                    continue;

                for (int i = 0; i < tokens.length; i++) {
                    if ("workingdir".equals(tokens[0])) {
                        dir = tokens[1];
                        break;
                    }
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return dir;
    }

    public static void displayFile(String name) {
        try {
            BufferedReader br = new BufferedReader(new FileReader(name));
            StringBuilder sb = new StringBuilder();
            String line;
            do {
                line = br.readLine();
                if (line != null)
                    System.out.println(line);
            } while (line != null);
            br.close();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

}
