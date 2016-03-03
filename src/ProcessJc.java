	import Utilities.Error;
	import Utilities.Visitor;
	import Utilities.Settings;
	import Utilities.SymbolTable;
	import Printers.*;
	import Scanner.*;
	import Parser.*;
	import AST.*;
import CodeGeneratorC.*;
import java.io.*;
import CodeGeneratorJava.*;
	import Library.*;
	
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
					    Error.setPackageName(argv[i]);
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
				// CODE GENERATOR

				if (Settings.targetLanguage.equals("c"))
				    c.visit(new CodeGeneratorC<AST>());
				else if (Settings.targetLanguage.equals("JVM"))
				    c.visit(new CodeGeneratorJava<AST>());
				else {
				    System.out.println("Unknown target language selected");
				    System.exit(1);
				}
				System.out.println("============= S = U = C = C = E = S = S =================");
			}	
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
				} while (line != null) ;
				br.close();
			} catch (Exception e) {
				e.printStackTrace();
			}
		}
	
	}
	
	
