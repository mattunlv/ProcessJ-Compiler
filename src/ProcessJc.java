import Utilities.Error;
import Utilities.Visitor;
import Utilities.Settings;
import Utilities.SymbolTable;
import Utilities.Log;
import Printers.*;
import Scanner.*;
import Parser.*;
import AST.*;
import CodeGeneratorC.*;
import java.io.*;
import CodeGeneratorJava.*;
import Library.*;
import java.util.Hashtable;
import AllocateStackSize.*;
import java.util.Set;
import java.lang.Process;

public class ProcessJc {
  //========================================================================================
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
  //========================================================================================
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
  //========================================================================================
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
        if ( argv[i].equals("-")) {
          s = new Scanner( System.in );
        } else if (argv[i].equals("-I")) {
          if (argv[i+1].charAt(argv[i+1].length()-1) == '/')
            argv[i+1] = argv[i+1].substring(0, argv[i+1].length()-1);
          Settings.includeDir = argv[i+1];
          i++;
          continue;
        } else if (argv[i].equals("-t")) {
          if (argv[i+1].equals("c") || argv[i+1].equals("JVM") || argv[i+1].equals("js")) {
            Settings.targetLanguage = argv[i+1];
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
          System.out.println("Setting scanner");
          Error.setFileName(argv[i]);
          s = new Scanner( new java.io.FileReader(argv[i]) );
        }
        p = new parser(s);
      } catch (java.io.FileNotFoundException e) {
        System.out.println("File not found : \""+argv[i]+"\"");
        System.exit(1);
      } catch (ArrayIndexOutOfBoundsException e) {
        usage();
      }

      try {
        java_cup.runtime.Symbol r = ((parser)p).parse();
        root = (AST)r.value;
      } catch (java.io.IOException e) { e.printStackTrace(); System.exit(1);
      } catch (Exception e) { e.printStackTrace(); System.exit(1); }

      // cast the result from the parse to a Compilation - this is the root of the tree
      Compilation c = (Compilation)root;

      // SYNTAX TREE PRINTER
      c.visit(new ParseTreePrinter());

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

      if (sts) // dump the symbol table structure
        globalTypeTable.printStructure("");

      // Resolve types from imported packages.
      c.visit(new NameChecker.ResolvePackedTypes());

      ////////////////////////////////////////////////////////////////////////////////
      // NAME CHECKER
      c.visit(new NameChecker.NameChecker<AST>(globalTypeTable));
      if (Error.errorCount != 0) {
        System.out.println("---------- Error Report ----------");
        System.out.println(Error.errorCount + " errors in symbol resolution - fix these before type checking.");
        System.out.println(Error.errors);
        System.exit(1);
      }

      // Re-construct Array Types correctly
      root.visit(new NameChecker.ArrayTypeConstructor());

      ////////////////////////////////////////////////////////////////////////////////
      // TYPE CHECKER
      c.visit(new TypeChecker.TypeChecker(globalTypeTable));

      ////////////////////////////////////////////////////////////////////////////////
      // CODE GENERATOR

      if (Settings.targetLanguage.equals("c")){
        //There is a bit of things to do here separate into function below.
        generateCodeC(c);
      }
      else if (Settings.targetLanguage.equals("JVM"))
        c.visit(new CodeGeneratorJava<AST>());
      else {
        System.out.println("Unknown target language selected");
        System.exit(1);
      }
      System.out.println("============= S = U = C = C = E = S = S =================");
    }
  }
  //========================================================================================
  public static void displayFile(String name) {
    try {
      BufferedReader br = new BufferedReader(new FileReader(name));
      StringBuilder sb = new StringBuilder();
      String line;
      do {
        line = br.readLine();
        if (line != null)
          System.out.println(line);
      } while (line != null) ;
      br.close();
    } catch (Exception e) {
      e.printStackTrace();
    }
  }
  //========================================================================================
  /**
   * Given our Compilations, i.e. our abstract syntax tree we will generate the code for
   * C. We have a few extra steps for we have to generate code, then run gcc with
   * -fstack-usage flag to get the size required by each function frame. This information
   * is needed since CCSP requires the stack size necessary. This procedure was taken from
   * Fred Barnes from his nocc compiler. See  Guppy: Process-Oriented Programming on
   * Embedded Devices. Frederick R.M. BARNES, School of Computing, University of Kent, UK.
   * For information on the procedure.
   */
  private static void generateCodeC(Compilation c){
    //Return value of ./makeSu call.
    int returnValue = -1;

    //Generate code with incorrect stack sizes.
    c.visit(new CodeGeneratorC<Object>());

    //Now we compile the code with gcc using the -fstack-usage flag to create a su file
    //that we can read in.
    try{
      Log.log("Creating .su file for stack sizes...");
      Process p = Runtime.getRuntime().exec("./makeSu");
      p.waitFor();
      returnValue = p.exitValue();
    }
    catch (Exception e){
      Error.error("Failed to run command \"./makeSu\" to compile with gcc.");
    }

    //Check return value. If it's non zero compilation failed for some reason...
    if(returnValue != 0)
      Error.error("\n\nError: Failed to create .su file, this is caused by:\n" +
		  "  1) Failure to compile the malformed generated C code.\n" +
		  "  2) gcc could not find a library needed for compilation.\n" +
		  "Manually run ./makeSu to see actual error.\n" +
		  "If this is case 1 please report the error.");

    //Read in .su file into our hash table.
    Log.log("\nReading in .su file:");
    Hashtable<String, Integer> suTable = readSuFile("codeGenerated.su");
    printTable(suTable);

    //Compute the stack sizes for all functions.
    Hashtable<String, Integer> sizePerFunction = new Hashtable();
    c.visit( new AllocateStackSize(sizePerFunction, suTable) );

    //Print final sizes for the user to see:
    Log.log("Total Size for functions:");
    Log.log("(Remember only the max function call is picked at the top level.)");
    printTable(sizePerFunction);

    //Call CodeGeneratorC, in the previous pass the correct stacksizes where set.
    c.visit(new CodeGeneratorC<Object>(sizePerFunction));

    return;
  }
  //========================================================================================
  /**
   * given the name of a .su file created by gcc it will read the file and return a hash
   * table with the entries in our hashtable. Maybe place this function as a static public
   * under the AllocateStackSize/ folder?? TODO.
   * @param fileName: name of *.su file to read.
   * @param suTable: HashTable of values to read.
   */
  public static Hashtable<String, Integer> readSuFile(String fileName){
    Hashtable<String, Integer> suTable = new Hashtable();

    try (BufferedReader br = new BufferedReader(new FileReader(fileName))){
      String currentLine;

      while( (currentLine = br.readLine()) != null ){
        String[] columns = currentLine.split("\\t");
        String extendedName = columns[0];
        Integer size = Integer.parseInt(columns[1]);

        //extendedName contains something that looks like this:
        //ccsp_cif.h:136:20:ChanInit
        //We only want the name, i.e. the last part.
        String[] extendedSplit = extendedName.split(":");
        int k = extendedSplit.length;
        String name = extendedSplit[k - 1];

        suTable.put(name, size);
      }

    }
    catch (IOException e){
      Error.error("Failed to read in .su file!");
    }

    return suTable;
  }
  //========================================================================================
  /**
   * Pretty prints a hash table with fancy formatting.
   * @param table: HashTable of values to print.
   * @return void.
   */
  public static void printTable(Hashtable<String, Integer> table){
    String dashLine = "---------------------------------------------------------";
    Log.log(dashLine);
    Log.log(String.format("|%-25s\t|\t%15s\t|", "functionName", "Size"));
    Log.log(dashLine);
    Set<String> myKeys = table.keySet();

    for(String name: myKeys){
      int size = table.get(name);
      String msg = String.format("|%-25s\t|\t%15d\t|", name, size);
      Log.log(msg);
    }
    Log.log(dashLine);

    return;
  }
  //========================================================================================
}
