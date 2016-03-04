package ConstantPropagation;

import Utilities.Visitor;
import AST.*;
import Utilities.Error;
import Utilities.Log;
import java.lang.Boolean;

public class ConstantPropagation extends Visitor<Object> {
    
    public Object visitPrimitiveLiteral(PrimitiveLiteral pl) {
	System.out.println("PrimitiveLiteral: (Constant=true) = " + pl.getText());
	return null;
    }

    public Object visitBinaryExpr(BinaryExpr be) {
	System.out.print("BinaryExpr.left: (Constant=" + be.left().isConstant() + ") = ");
	if (be.left().isConstant())
	    System.out.println(be.left().constantValue());
	else
	    System.out.println("N/A");
	System.out.print("BinaryExpr.right: (Constant=" + be.right().isConstant() + ") = ");
	if (be.right().isConstant())
	    System.out.println(be.right().constantValue());
	else
	    System.out.println("N/A");
	System.out.print("BinaryExpr: (Constant=" + (be.left().isConstant() && be.right().isConstant()) + ") = ");

	if (be.left().isConstant() && be.right().isConstant()) 
	    System.out.println(be.constantValue());
	else
	    System.out.println("N/A");
	return null;
    }

}