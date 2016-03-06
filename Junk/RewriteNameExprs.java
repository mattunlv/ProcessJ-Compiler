package Utilities;

import NameChecker.TopLevelDecls;
import Parser.parser;
import Parser.sym;
import Scanner.Scanner;
import AST.*;

import java.util.ArrayList;
import java.io.File;


// rewriteToRecordAccess:
// 
// X.Y.Z.W.U
//
// X.Y.Z.W.U RecordAccess
//
// rewzriteToImplicitImport:
//
// X.Y.Z.W.U
//
// (X,Y) Implicit Import and Z.W.U RecordAccess
// or
// (null,X) Implicit Import and Y.Z.W.U RecordAccess
//


public class RewriteNameExprs {

	





	private Expression makeRecordAccess(Name n) {
		if (n.isSimple()) 
			return new NameExpr(n);
		else
			return new RecordAccess(makeRecordAccess(n.qualifier()), new Name(n.getSimpleName()));
	}

	private Expression makeImplicitImport(Name n) {
		/*
		 * n must be qualified.
		 * if n = x.y => (null, x) - y
		 * if n = x.y.z => (x, y) - z OR (null, x).y.z  (try the first first and then the second)
		 * if n = x.y.z.w. => (x,y).z - w OR (null, x).y.z.w
		 */
		System.out.println("Rewrite NameExprs: " + n + " " + n.getSize());
		if (n.getSize() == 1) { // should only happen for invocations!
			return new ImplicitImport(null, new Name(n.getSimpleName()), null);
		} else if (n.getSize() == 2) { // can only be x.y => (null, x, y)
			return new ImplicitImport(null, n.qualifier(), new Name(n.getSimpleName()));
		} else if (n.getSize() == 3) { // can only be x.y.z => (x,y,z) - if that doesn't work try (null,x,y).z
			return new ImplicitImport(n.qualifier().qualifier(), new Name(n.qualifier().getSimpleName()), new Name(n.getSimpleName()));
		} else {
			// x.y.z.w.u..... 
			return new RecordAccess(makeImplicitImport(n.qualifier()), new Name(n.getSimpleName()));
		}
	}


	public void go(AST a) {
		if (a instanceof Sequence) {
			Sequence<AST> s = (Sequence<AST>)a;
			for (int i=0; i<s.children.size(); i++) {
				if (s.children.get(i) instanceof NameExpr) {
					NameExpr ne = (NameExpr)s.children.get(i);
					if (ne.rewriteToImplicitImport) {
						System.out.println("RewriteNameExprs: Transforming to ImplicitImport");
						s.children.add(i,makeImplicitImport(ne.name()));
					} else if (ne.rewriteToRecordAccess) {
						System.out.println("RewriteNameExprs: Transforming to RecordAccess");
						s.children.add(i,makeRecordAccess(ne.name()));
					}
				} else
					go((AST)s.children.get(i));
			}
		} else {
			for (int i=0; i<a.nchildren; i++) {
				if (a.children[i] != null) {
					if (a.children[i] instanceof NameExpr) {  // if a.children[i] id a Name Expression
						NameExpr ne = (NameExpr)a.children[i];
						if (ne.rewriteToImplicitImport) {
							System.out.println("RewriteNameExprs: Transforming to> ImplicitImport " + ne.name());
							a.children[i] = makeImplicitImport(ne.name());
						} else if (ne.rewriteToRecordAccess) {
							System.out.println("RewriteNameExprs: Transforming to RecordAccess");
							a.children[i] = makeRecordAccess(ne.name());
						}
					} else 
						go(a.children[i]);
				}
			}
		}
	}
}