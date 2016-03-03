package CodeGeneratorC;

import AST.*;
import Utilities.*;
import Utilities.Error;

public class CodeGeneratorC <T extends AST> extends Visitor<T> {
    public static SymbolTable symtab = new SymbolTable();

    public CodeGeneratorC() {
    	Log.log("======================================");
    	Log.log("* C O D E   G E N E R A T O R  ( C )  *");
    	Log.log("======================================");
    }
    String tab = "";
    // AltCase
    // AltStat
    // ArrayAccessExpr
    // ArrayLiteral
    // ArrayType
    // Assignment
    public T visitAssignment(Assignment as) {
	String s = as.left().visit(this) + " " + as.opString() + " " + as.right().visit(this);
	System.out.println(tab + s);
	return null;
    }
    // BinaryExpr
    // Block
    // BreakStat
    // CastExpr
    // ChannelType
    // ChannelEndExpr
    // ChannelEndType
    // ChannelReadExpr
    // ChannelWriteStat
    // ClaimStat
    // Compilation
    public T visitCompilation(Compilation c) {
	// to visit all the children: c.visitChildren(this); or super.visitCompilation(c);
	
	


	return null;
    }



    // ConstantDecl
    // ContinueStat
    // DoStat
    // ExprStat
    // ExternType
    // ForStat
    // Guard
    // IfStat
    // Import
    // Invocation
    // LocalDecl
    // Modifier
    // Name
    // NamedType
    // NameExpr
    // NewArray
    // NewMobile
    // ParamDecl
    // ParBlock
    // Pragma
    // PrimitiveLiteral
    // PrimitiveType
    // ProcTypeDecl
    // ProtocolLiteral
    // ProtocolCase
    // ProtocolTypeDecl
    // RecordAccess
    // RecordLiteral
    // RecordMember
    // RecordTypeDecl
    // ReturnStat
    // Sequence
    // SkipStat
    // StopStat
    // SuspendStat
    // SwitchGroup
    // SwitchLabel
    // SwitchStat
    // SyncStat
    // Ternary
    // TimeoutStat
    // UnaryPostExpr
    // UnaryPreExpr
    // Var
    // WhileStat
    public T visitWhileStat(WhileStat ws) {
	super.visitWhileStat(ws);
	return null;
    }
}