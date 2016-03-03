package Utilities;
import java.io.File;

import NameChecker.TopLevelDecls;
import Parser.parser;
import Scanner.Scanner;
import Utilities.Visitor;
import AST.*;

public class SetImplicitImportTypes extends Visitor<AST> {

	private Name procName = null;
	
	public AST visitInvocation(Invocation in) {
		Name oldProcName = procName;
		procName = in.procedureName();
		super.visitInvocation(in);
		procName = oldProcName;
		return null;
	}
	
	
	public AST visitImplicitImport(ImplicitImport im) {
		System.out.println("VisitImplicitImport: " + im);
		
		if (im.typeName() == null)
			im.children[2] = procName;
		System.out.println("Can I has '" + im + "?");
		Object o = canIHas(im, im.packageName(), im.fileName(), im.typeName());
		if (o == null)
			System.out.println(" NO!");
		else
			System.out.println(" Yes: " + o);
		return null;
	}


	Object canIHas(ImplicitImport ii, Name pn, Name fn, Name tn) {
		System.out.println("<" + pn + "," + fn + "," + tn + ">");
		String fileName = "";
		boolean localFile = false;
		java_cup.runtime.Symbol r = null;
		if (pn == null) {
			fileName = "./" + fn.getname() + ".pj";
			localFile = true;
			System.out.println("  Implicit Import of non packaged file: " + fileName + " (to be attempted)");
		} else {
			fileName = Utilities.Settings.includeDir + Utilities.Settings.targetLanguage + "/" + pn.getname() + "/" + fn.getname() + ".pj";
			System.out.println("  Implicit Import of non packaged file: " + fileName + " (to be attempted)");
			if (!(new File(fileName).isFile())) {
				fileName = "./" + pn + ".pj";
				System.out.println("  Import of file '" + fileName + "' failed, trying local file '" + fileName + "'");	
				localFile = true;
			}
		}
		if (!(new File(fileName).isFile())) {
			Error.error("  Implicit import failed for file '" + fileName + "'");
		}
		try {
			System.out.println("  Starting implicit import of " + fileName);
			Scanner s1 = new Scanner( new java.io.FileReader(fileName) );
			parser p1 = new parser(s1);
			r = p1.parse();           
			
		} catch (java.io.FileNotFoundException e) {
			Error.error(ii,"File not found : " + fileName, true, 0000);
		} catch (Exception e) {
			Error.error(ii,"Something went wrong while trying to parse " + fileName, true, 0000);
		}
		SymbolTable s = new SymbolTable("Implicityly Imported library '" + fileName + "'");
		Compilation c = (Compilation)r.value;		
		c.visit(new TopLevelDecls<AST>(s)); 
		Object o = s.getShallow(tn.getname()); // grab the type name out of the new symbol table;
		if (o == null) {
			if (!localFile) {
				System.out.println("No type " + tn + " found in " + pn + "." + fn);
				// try local file
				fileName = "./" + pn.getname() + ".pj";
				if (!(new File(fileName).isFile())) {
					Error.error("  Implicit import failed for file '" + fileName + "'");
					return null;
				}
				try {
					System.out.println("  Starting implicit import of " + fileName);
					Scanner s1 = new Scanner( new java.io.FileReader(fileName) );
					parser p1 = new parser(s1);
					r = p1.parse();           	
				} catch (java.io.FileNotFoundException e) {
					Error.error(ii,"File not found : " + fileName, true, 0000);
				} catch (Exception e) {
					Error.error(ii,"Something went wrong while trying to parse " + fileName, true, 0000);
				}
				s = new SymbolTable("Implicityly Imported library '" + fileName + "'");
				c = (Compilation)r.value;		
				c.visit(new TopLevelDecls<AST>(s)); 
				o = s.getShallow(fn.getname()); // grab the type name out of the new symbol table;
				if (o == null)
					Error.error(ii,"Import of type '" + fn.getname() + "' failed, no such type found,");
				else
					// this was not originally an import of a local file, but <pn,fn,tn>
					// pn/fn.pj did not contain tn, but ./pn.pj contained fn, so
					// rewrite <pn,fn,tn> to <null,pn,fn>.tn 
					// TODO: Make sure this is turned into ImplicitImport + a RecordAccess.
					
					return (Object)o;
			} else			
				Error.error(ii,"Import of type '" + tn.getname() + "' failed, no such type found,");
		} else
			return (Object)o;
		return null;
	}
	
}
