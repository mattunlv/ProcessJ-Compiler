package NameChecker;
import java.io.File;

import Parser.parser;
import Scanner.Scanner;
import Utilities.Visitor;
import AST.*;
import Utilities.Error;
import Utilities.SymbolTable;
import java.util.Hashtable;


public class ResolvePackedTypes extends Visitor<AST> {

	public static Hashtable<String, Compilation> alreadyImportedFiles = new Hashtable<String,Compilation>();
	
	
	
	
	public static Compilation importFile(Name na, String fileName) {
		Compilation c = alreadyImportedFiles.get(fileName);
		if (c != null) {
			System.out.println("Import of '" + fileName + "' already done before!");
			return c;
		} 
		try {
			System.out.println("---=== Starting import of " + fileName);
			Scanner s1 = new Scanner( new java.io.FileReader(fileName) );
			parser p1 = new parser(s1);
			java_cup.runtime.Symbol r = p1.parse();           
			alreadyImportedFiles.put(fileName,(Compilation)r.value);
			return (Compilation)r.value;
		} catch (java.io.FileNotFoundException e) {
			Error.error(na, "File not found : " + fileName, true, 2104);
		} catch (Exception e) {
			Error.error(na, "Something went wrong while trying to parse " + fileName, true, 2105);
		}
		return null;
	}
	
	public void resolveTypeOrConstant(Name na) {
		Sequence<Name> pa = na.packageAccess();
		String fileName = "";

		//Utilities.Settings.includeDir).getAbsolutePath() + "/" + Utilities.Settings.targetLanguage + "/" + 
		//path + (path.equals("")?"":"/") + im.file().getname() + ".pj";

		if (pa.size() > 0) {
			if (pa.size() == 1) {
				// Try local file first (well  this can't be a library file!
				fileName = new File("").getAbsolutePath() + "/" + pa.child(0).getname() + ".pj";
				if (new File(fileName).isFile()) {
					System.out.println("Success: ready to resolve '" + fileName + "'.");
					Compilation comp = importFile(pa.child(0), fileName);
					// Now top level visit it
					SymbolTable st = new SymbolTable();
					comp.visit(new TopLevelDecls<AST>(st));
					st = SymbolTable.hook;
					// TODO: this should do a proper find if its a symb ol table that comes back
					// but we probably need Type checking for that !
					// so for now - SymbolTable implements TopLevelDecl as well!
					TopLevelDecl td = (TopLevelDecl)st.getShallow(na.simplename());
					if (td == null) {
						;// don't error out now - the NameChecker will do that!
						//Error.error(na,"Constant or Type '" + na + "' not declared.", false, 0000);
					} else {
						na.c = comp;
						na.resolvedPackageAccess = td;
					}
				} else {
					// It must be a local - it cannot be a package cause no package name
					// was given
					; // don't error out now - the NameChecker will do that!
					// Error.error(pa, "Cannot resolve file '" + pa.child(0).getname() + "' for type or constant import.");
				}
			} else {
				System.out.println("not a local file name!");
				String path ="";
				int i = 0;
				for (Name n : pa) {
					path = path + n.getname();
					System.out.println(i + " == " + n.getname());
					if (i<pa.size()-1)
						path = path + "/";		
					i++;
				}	
				

				// try local first
				fileName = new File("").getAbsolutePath() + "/" + path + ".pj";

				System.out.println("Package file name is : " + fileName);

				// is it a local file 
				if (new File(fileName).isFile()) {	
					// yes, so add it to the fileList
					System.out.println("Success: ready to resolve '" + fileName + "'.");
					Compilation comp = importFile(na, fileName);
					// Now top level visit it
					SymbolTable st = new SymbolTable();
					comp.visit(new TopLevelDecls<AST>(st));
					st = SymbolTable.hook;
					// TODO: this should do a proper find if its a symb ol table that comes back
					// but we probably need Type checking for that !
					// so for now - SymbolTable implements TopLevelDecl as well!
					TopLevelDecl td = (TopLevelDecl)st.getShallow(na.simplename());
					if (td == null) {
						; // don't error out now - the NameChecker will do that!
						// Error.error(na,"Constant or Type '" + na + "' not declared.", false, 0000);
					} else {
						na.c = comp;
						na.resolvedPackageAccess = td;
					}
				} else {
					// no - now try a library!
					fileName = new File(Utilities.Settings.includeDir).getAbsolutePath() + "/" + 
							       Utilities.Settings.targetLanguage + "/" + 
							       path +".pj";
					System.out.println("try library now: package file name is : " + fileName);
					if (new File(fileName).isFile()) {	
						System.out.println("but then again - try library: ready to resolve '" + fileName + "'.");
						// yes, so add it to the fileList
						Compilation comp = importFile(na, fileName);
						// Now top level visit it
						SymbolTable st = new SymbolTable();
						comp.visit(new TopLevelDecls<AST>(st));
						st = SymbolTable.hook;
						// TODO: this should do a proper find if its a symb ol table that comes back
						// but we probably need Type checking for that !
						// so for now - SymbolTable implements TopLevelDecl as well!
						TopLevelDecl td = (TopLevelDecl)st.getShallow(na.simplename());
						if (td == null) {
							; // don't error out now - the NameChecker will do that!
							//	Error.error(na,"Constant or Type '" + na + "' not declared.", false, 0000);
						} else {
							na.c = comp;
							na.resolvedPackageAccess = td;
						}				
					} else {
						; // don't error out now - the NameChecker will do that!
						// Error.error(pa, "Cannot resolve file '" + na + "' for type or constant import.");
					}
				}
			}
		}	
	}	

	public AST visitName(Name na) {
		if (na.packageAccess().size() > 0) {
			System.out.println("--> " + na);
			resolveTypeOrConstant(na);
		}
		return null;
	}


	public AST visitNamedType(NamedType nt) {
		Sequence packages = nt.name().packageAccess();
		if (packages.size() > 0) {
			System.out.println("> " + nt.name());
			resolveTypeOrConstant(nt.name());
		}

		return null;
	}
}
