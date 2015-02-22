package NameChecker;

import AST.*;
import Utilities.*;
import Utilities.Error;
import Scanner.*;
import Parser.*;
import java.io.File;
import java.util.ArrayList;
import java.io.FilenameFilter;
import java.util.ArrayList;

// Error message number range: [2100 - 2199]
// Used 2100 - 2104

public class TopLevelDecls<T extends AST> extends Visitor<T> {
	private SymbolTable symtab;

	public TopLevelDecls(SymbolTable symtab) {
		Log.log("======================================");
		Log.log("*   T O P   L E V E L   D E C L S    *");
		Log.log("======================================");
		this.symtab = symtab;
	}

	// TODO: imported files MUST name checked BECAUSE names in extends of protcols and record and constants and procedures can be undefined.

	// TODO: locally imported files should not be in packages .... what about 'this' file ? what about its package ... this must be sorted out


	public T visitCompilation(Compilation co) {
		//Log.log("Defining forward referencable names.");
		// 'symtab' is either passed in here from the driver (ProcessJ.java) or from
		// the visitImport() method in this file. Save it cause we need to put all
		// the types and constants for this compilation into it after we handle
		// the imports.
		SymbolTable myGlobals = symtab;

		// reset the symbol table to null so we get a fresh chain for these imports.
		symtab = null;
		co.imports().visit(this);

		// symtab now contains a chain through the parent link of all the imports for this compilation.
		// set the 'importParent' of this compilation's symbol table to point to the
		// import chain of symbol tables.
		myGlobals.setImportParent(symtab);
		// re-establish this compilation's table
		symtab = myGlobals;
		// now vist all the type declarations and the constants in this compilation
		co.typeDecls().visit(this);
		// hook the symbol table so it can be grabbed from who ever called us.
		SymbolTable.hook = symtab;
		Log.log("Done.");
		return null;
	}

	// ConstantDecl
	public T visitConstantDecl(ConstantDecl cd) {
		Log.log(cd.line + ": Visiting a ConstantDecl " + cd.var().name().getname());
		if (!symtab.put(cd.var().name().getname(), cd)) 
			Error.error(cd, "Type with name '" + cd.var().name().getname() + "' already declared in this scope.", false, 2100);
		return null;
	}    

	// Static class used for filtering files in imports (only the ones ending in the proper extension will be considered)
	static class PJfiles implements FilenameFilter {
		public boolean accept(File dir, String name) {
			String[] result = name.split("\\.");
			return result[result.length-1].equals(Utilities.Settings.importFileExtension);
		}
	}

	// Given a directory, makeFileList creates an array list of Strings representing
	// the absolute paths of all the files in the directory and its sub-directories
	// that satisfy the filter in the PJFiles class.	
	public static void makeFileList(ArrayList<String> list, String directory) {
		//System.out.println("Called with : " + directory);
		String entries[] = new File(directory).list(new PJfiles());
		//System.out.println("Number of files is '" + directory + "' = " + entries.length);
		for (String s : entries) {
			//System.out.println(directory + " / " + s);
			File f = new File(directory + "/" + s);
			if (f.isFile()) {
				//System.out.println("Adding " + s);
				list.add(directory + "/" + s);
			}
		}
		entries = new File(directory).list();
		for (String s : entries) {
			File f = new File(directory + "/" + s);
			if (f.isDirectory())
				makeFileList(list, directory + "/" + s);
		}
	}



	// TODO: remember source imports must be compiled too!

	// visitImport will read and parse an import statement. The chain of symbol tables will be left
	// in the 'symtab' field. The parentage of multiple files imported in the same import is also through
	// the parent link.
	public T visitImport(Import im) {
		Log.log(im.line + "Visiting an import");

		// an import is fist tried in the local director
		// then in the include directory - unless it is of the form 'f' then it must be local.

		// make the path for this import
		String path ="";
		if (im.path() != null) {
			int i = 0;
			for (Name n : im.path()) {
				path = path + n.getname();
				if (i < im.path().size()-1) 
					path = path + "/";
				i++;
			}	
		}

		System.out.println("Package path is : " + path);
		System.out.println("Package file name is : " + im.file().getname());

		// try local first
		String fileName = new File("").getAbsolutePath() + "/" + path;

		// fileList will hold a list of files found in wildcard imports (.*)
		ArrayList<String> fileList = new ArrayList<String>();

		if (im.importAll()) { // a .* import
			// is it a local directory?
			if ((new File(fileName).isDirectory())) {
				// yes, so add it's content to the fileList
				makeFileList(fileList, fileName);
			} else {	
				// was not a local directory, but see if it is a library directory
				fileName = new File(Utilities.Settings.includeDir).getAbsolutePath() + "/" + 
						Utilities.Settings.targetLanguage + "/" + path;
				//System.out.println("Not a local so try a library: " + fileName);
				if (new File(fileName).isDirectory()) {
					// yes it was, so add it's content to the fileList
					makeFileList(fileList, fileName);
				} else {
					// Oh no, the directory wasn't found at all!
					String packageName = path.replaceAll("/", ".");
					packageName = packageName.substring(0, packageName.length()-1);	
					Error.error(im,"package '" + packageName + "' does not exist.", false, 0);
				}
			}
			//System.out.println("About to import");
			//for (String s : fileList)
			//	System.out.println("  > " + s);
		} else { // not a .* import
			fileName = fileName + "/" + im.file().getname() + ".pj";

			// is it a local file 
			if (new File(fileName).isFile()) {	
				// yes, so add it to the fileList
				fileList.add(fileName);
			} else {
				// no, so look in the library
				fileName = new File(Utilities.Settings.includeDir).getAbsolutePath() + "/" + Utilities.Settings.targetLanguage + "/" + 
						path + (path.equals("")?"":"/") + im.file().getname() + ".pj";
				System.out.println("Not a local so try a library: " + fileName);
				// but only if it isn't for the form 'import f' cause they can only be local!
				if (!path.equals("") && new File(fileName).isFile()) {
					fileList.add(fileName);
				} else {
					// nope, nothing found!
					if (path.equals("")) {
						Error.error(im, "File '" + im.file().getname() + "' not found.", false, 0000); 	
					} else {
						String packageName = path.replaceAll("/", ".");
						packageName = packageName.substring(0, packageName.length()-1);
						Error.error(im, "File '" + im.file().getname() + "' not found in package '" + path + "'.", false, 0000); 
					} 
				}
			}	
		}

		// fileList now contains the list of all the files that this import caused to be imported
		for (String fn : fileList) {
			// scan, parse and build tree.
			Compilation c = importFile(im, fn);
			// add it to the list of compilations for this import
			im.addCompilation(c);
			// create a symboltable for it
			SymbolTable importSymtab = new SymbolTable("Import: " + fn);
			// declare types and constants for handle it's imports		
			c.visit(new TopLevelDecls<AST>(importSymtab));	
			// insert into the symtab chain along the parent link
			if (symtab == null)
				symtab = importSymtab;
			else {
				importSymtab.setParent(symtab);
				symtab = importSymtab;
			}
		}		
		// done!
		return null;
	}

	// ProcTypeDecl
	public T visitProcTypeDecl(ProcTypeDecl pd) {
		Log.log(pd.line + ": Visiting a ProcTypeDecl " + pd.name().getname());
		// Procedures can be overloaded, so an entry in the symbol table for a procedure is
		// another symbol table which is indexed by signature.
		if (Modifier.hasModifierSet(pd.modifiers(), Modifier.MOBILE))
			if (!pd.returnType().isVoidType())
				Error.error(pd, "Mobile procedure '" + pd.name().getname() + "' must have void return type.");

		// mobile procedure may NOT be overloaded.
		// if a symbol table contains a mobile the field isMobileProcedure is true.

		Object s = symtab.getShallow(pd.name().getname());
		//System.out.println(symtab.getname() + "lookup? " + s);
		if (s == null) { // this is the first time we see a procedure by this name in this scope 
			SymbolTable st = new SymbolTable();
			if (Modifier.hasModifierSet(pd.modifiers(), Modifier.MOBILE))
				st.isMobileProcedure = true;
			st.put(pd.signature(), pd);
			symtab.put(pd.name().getname(), st);
		} else {
			if (s instanceof SymbolTable) {
				SymbolTable st = (SymbolTable)s;
				if (Modifier.hasModifierSet(pd.modifiers(), Modifier.MOBILE)) {
					if (st.isMobileProcedure)
						Error.error(pd,"Only one instance of mobile procedure '" + pd.name().getname() + "' may exist.");
					else
						Error.error(pd,"Non-mobile proecdure '" + pd.name().getname() + "' already exists.");
				} else   			
					st.put(pd.signature(), pd);
			} else
				Error.error(pd,"Non-procedure type with name '" + pd.getname() + "' already declared in this scope", false, 2101);
		}
		return null;		
	}

	// RecordTypeDecl
	public T visitRecordTypeDecl(RecordTypeDecl rd) {
		Log.log(rd.line + ": Visiting a RecordTypeDecl " + rd.name().getname());
		if (!symtab.put(rd.name().getname(), rd))
			Error.error(rd, "Type with name '" + rd.name().getname() + "' already declared in this scope.", false, 2102);
		return null;
	}

	// ProtocolTypeDecl
	public T visitProtocolTypeDecl(ProtocolTypeDecl pd) {
		Log.log(pd.line + ": Visiting a ProtocolTypeDecl " + pd.name().getname());
		if (!symtab.put(pd.name().getname(), pd))
			Error.error(pd, "Type with name '" + pd.name().getname() + "' already declared in this scope.", false, 2103);
		return null;
	}

	// imports (by scanning, parsing and tree building one file!
	public static Compilation importFile(Import im, String fileName) {
		try {
			System.out.println("---=== Starting import of " + fileName);
			Scanner s1 = new Scanner( new java.io.FileReader(fileName) );
			parser p1 = new parser(s1);
			java_cup.runtime.Symbol r = p1.parse();           
			return (Compilation)r.value;
		} catch (java.io.FileNotFoundException e) {
			Error.error(im, "File not found : " + fileName, true, 2104);
		} catch (Exception e) {
			Error.error(im, "Something went wrong while trying to parse " + fileName, true, 2105);
		}
		return null;
	}

}