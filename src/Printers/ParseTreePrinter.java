package Printers;

import Utilities.Visitor;
import AST.*;

public class ParseTreePrinter extends Visitor<AST> {
	public int indent=0;

	private String indent(int line) {
		String s = "" + line + ": ";
		int l = 4-s.length();
		for (int i=0; i<indent+l; i++) {
			s = s + " ";
		}
		return s;
	}

	public ParseTreePrinter() {
	}

	// AltCase
	public AST visitAltCase(AltCase ac) {        
		System.out.println(indent(ac.line) + "AltCase:");
		indent += 2;
		super.visitAltCase(ac);
		indent -= 2;
		return null;
	}
	// AltStat
	public AST visitAltStat(AltStat as) {        
		System.out.println(indent(as.line) + "AltStat:");
		indent += 2;
		super.visitAltStat(as);
		indent -= 2;
		return null;
	}
	// ArrayAccessExpr
	public AST visitArrayAccessExpr(ArrayAccessExpr ae) {
		System.out.println(indent(ae.line) + "ArrayAccessExpr:");
		indent += 2;
		super.visitArrayAccessExpr(ae);
		indent -=2;
		return null;
	}
	// ArrayLiteral
	public AST visitArrayLiteral(ArrayLiteral al) {
		System.out.println(indent(al.line) + "ArrayLiteral:");
		indent += 2;
		super.visitArrayLiteral(al);
		indent -= 2;
		return null;
	}
	// ArrayType
	public AST visitArrayType(ArrayType at) {
		System.out.print(indent(at.line) + "ArrayType:");
		for (int i=0;i<at.getDepth();i++)
			System.out.print("[]");
		System.out.println();
		indent += 2;
		super.visitArrayType(at);
		indent -= 2;
		return null;
	}
	// Assignment
	public AST visitAssignment(Assignment as) {
		System.out.println(indent(as.line) + "Assignment:");
		indent += 2;
		super.visitAssignment(as);
		System.out.println(indent(as.line) + "Operator: " + as.opString());
		indent -= 2;
		return null;
	}
	// BinaryExpr
	public AST visitBinaryExpr(BinaryExpr be) {
		System.out.println(indent(be.line) + "BinaryExpr:");
		indent += 2;
		super.visitBinaryExpr(be);
		System.out.println(indent(be.line) + "Operator: " + be.opString());
		indent -= 2;
		return null;
	} 
	// Block
	public AST visitBlock(Block bl) {
		System.out.println(indent(bl.line) + "Block:");
		indent += 2;
		super.visitBlock(bl);
		indent -= 2;
		return null;
	}
	// BreakStat
	/** BREAK STATEMENT */
	public AST visitBreakStat(BreakStat bs) {
		System.out.println(indent(bs.line) + "BreakStat");
		indent += 2;
		super.visitBreakStat(bs);
		indent -= 2;
		return null;
	}
	// CastExpr
	public AST visitCastExpr(CastExpr ce) {
		System.out.println(indent(ce.line) + "CastExpr:");
		indent += 2;
		super.visitCastExpr(ce);
		indent -= 2;
		return null;
	}
	// ChannelType
	public AST visitChannelType(ChannelType ct) {        
		System.out.println(indent(ct.line) + "ChannelType:");
		indent += 2;
		super.visitChannelType(ct);
		indent -= 2;
		return null;
	}
	// ChannelEndExpr
	public AST visitChannelEndExpr(ChannelEndExpr ce) {        
		System.out.println(indent(ce.line) + "ChannelEndExpr:");
		indent += 2;
		super.visitChannelEndExpr(ce);
		indent -= 2;
		return null;
	}
	// ChannelEndType
	public AST visitChannelEndType(ChannelEndType ce) {        
		System.out.println(indent(ce.line) + "ChannelEndType:");
		indent += 2;
		super.visitChannelEndType(ce);
		indent -= 2;
		return null;
	}
	// ChannelReadExpr
	public AST visitChannelReadExpr(ChannelReadExpr cr) {        
		System.out.println(indent(cr.line) + "ChannelReadExpr:");
		indent += 2;
		super.visitChannelReadExpr(cr);
		indent -= 2;
		return null;
	}
	// ChannelWriteStat
	public AST visitChannelWriteStat(ChannelWriteStat cw) {        
		System.out.println(indent(cw.line) + "ChannelWriteStat:");
		indent += 2;
		super.visitChannelWriteStat(cw);
		indent -= 2;
		return null;
	}
	// ClaimStat
	public AST visitClaimStat(ClaimStat cs) {        
		System.out.println(indent(cs.line) + "ClaimStat:");
		indent += 2;
		super.visitClaimStat(cs);
		indent -= 2;
		return null;
	}
	// Compilation
	public AST visitCompilation(Compilation co) {        
		System.out.println(indent(co.line) + "Compilation:");
		indent += 2;
		super.visitCompilation(co);
		indent -= 2;
		return null;
	}
	// ConstantDecl
	public AST visitConstantDecl(ConstantDecl cd) {
		System.out.println(indent(cd.line) + "ConstantDecl:");
		indent += 2;
		super.visitConstantDecl(cd);
		indent -= 2;
		return null;
	}
	// ContinueStat
	public AST visitContinueStat(ContinueStat cs) {
		System.out.println(indent(cs.line) + "Continue Statement");
		indent += 2;
		super.visitContinueStat(cs);
		indent -= 2;
		return null;
	} 
	// DoStat
	public AST visitDoStat(DoStat ds) {
		System.out.println(indent(ds.line) + "DoStat:");
		indent += 2;
		super.visitDoStat(ds);
		indent -= 2;
		return null;
	}
	// ExprStat
	public AST visitExprStat(ExprStat es) {
		System.out.println(indent(es.line) + "ExprStat:");
		indent += 2;
		super.visitExprStat(es);
		indent -= 2;
		return null;
	}
	// ExternType
	public AST visitExternType(ExternType et) {
		System.out.println(indent(et.line) + "ExternType:");
		indent += 2;
		super.visitExternType(et);
		indent -= 2;
		return null;
	}
	// ForStat
	public AST visitForStat(ForStat fs) {
		System.out.println(indent(fs.line) + "ForStat:");
		indent += 2;
		super.visitForStat(fs);
		indent -= 2;
		return null;
	}
	// Guard
	public AST visitGuard(Guard g) {        
		System.out.println(indent(g.line) + "Guard:");
		indent += 2;
		super.visitGuard(g);
		indent -= 2;
		return null;
	}
	// IfStat
	public AST visitIfStat(IfStat is) {
		System.out.println(indent(is.line) + "IfStat:");
		indent += 2;
		super.visitIfStat(is);
		indent -= 2;
		return null;
	}
	// ImplicitImport
	public AST visitImplicitImport(ImplicitImport im) {
		System.out.println(indent(im.line) + "ImplicitImport:");
		indent += 2;
		super.visitImplicitImport(im);
		indent -= 2;
		return null;
	}
	// Import
	public AST visitImport(Import im) {
		System.out.println(indent(im.line) + "Import:");
		indent += 2;
		super.visitImport(im);
		indent -= 2;
		return null;
	}
	// Invocation
	public AST visitInvocation(Invocation in) {
		System.out.println(indent(in.line) + "Invocation:");
		indent += 2;
		super.visitInvocation(in);
		indent -= 2;
		return null;
	}	
	// LocalDecl
	public AST visitLocalDecl(LocalDecl ld) {
		System.out.println(indent(ld.line) + "LocalDecl:");
		indent += 2;
		super.visitLocalDecl(ld);
		indent -= 2;
		return null;
	}      
	// Modifier
	public AST visitModifier(Modifier mo) {
		System.out.println(indent(mo.line) + "Modifier (" + mo + ")");
		return null;
	}
	// Name
	public AST visitName(Name na) {
		System.out.println(indent(na.line) + "Name = " + na.getname());
		return null;
	}
	// NamedType
	public AST visitNamedType(NamedType nt) {
		System.out.println(indent(nt.line)+ "NamedType:");
		indent += 2;
		super.visitNamedType(nt);
		indent -= 2;
		return null;
	}
	// NameExpr
	public AST visitNameExpr(NameExpr ne) {
		System.out.println(indent(ne.line) + "NameExpr:");
		indent += 2;
		super.visitNameExpr(ne);
		indent -= 2;
		return null;
	}	
	// NewArray
	public AST visitNewArray(NewArray ne) {
		System.out.println(indent(ne.line) + "New Array");
		indent += 2;
		super.visitNewArray(ne);
		indent -= 2;
		return null;
	}
	// NewMobile
	public AST visitNMewMobile(NewMobile nm) {        
		System.out.println(indent(nm.line) + "NewMobile:");
		indent += 2;
		super.visitNewMobile(nm);
		indent -= 2;
		return null;
	}
	// ParamDecl
	public AST visitParamDecl(ParamDecl pd) {
		System.out.println(indent(pd.line) + "ParamDecl: ");
		indent += 2;
		super.visitParamDecl(pd);
		indent -= 2;
		return null;
	}
	// ParBlock
	public AST visitParBlock(ParBlock pb) {        
		System.out.println(indent(pb.line) + "ParBlock:");
		indent += 2;
		super.visitParBlock(pb);
		indent -= 2;
		return null;
	}
	// Pragma
	public AST visitPragma(Pragma p) {        
		System.out.println(indent(p.line) + "Pragma:");
		indent += 2;
		super.visitPragma(p);
		indent -= 2;
		return null;
	}
	// PrimitiveLiteral
	public AST visitPrimitiveLiteral(PrimitiveLiteral li) {
		System.out.println(indent(li.line) + "PrimtiveLiteral = " + li);
		indent += 2;
		super.visitPrimitiveLiteral(li);
		indent -= 2;
		return null;
	}	
	// PrimitiveType
	public AST visitPrimitiveType(PrimitiveType pt) {
		System.out.println(indent(pt.line) + "PrimitiveType = " + pt);
		return null;
	}
	// ProcTypeDecl
	public AST visitProcTypeDecl(ProcTypeDecl pd) {
		System.out.println(indent(pd.line) + "ProcTypeDecl:");
		indent += 2;
		super.visitProcTypeDecl(pd);
		indent -= 2;		
		return null;		
	}
	// ProtocolLiteral
	public AST visitProtocolLiteral(ProtocolLiteral pl) {
		System.out.println(indent(pl.line) + "ProtocolLiteral:");
		indent += 2;
		super.visitProtocolLiteral(pl);
		indent -=2;
		return null;
	}
	// ProtocolCase
	public AST visitProtocolCase(ProtocolCase pc) {
		System.out.println(indent(pc.line) + "ProtocolCase:");
		indent += 2;
		super.visitProtocolCase(pc);
		indent -=2;
		return null;
	}
	// ProtocolTypeDecl
	public AST visitProtocolTypeDecl(ProtocolTypeDecl pd) {
		System.out.println(indent(pd.line) + "ProtocolTypeDecl:");
		indent += 2;
		super.visitProtocolTypeDecl(pd);
		indent -= 2;
		return null;
	}
	// RecordAccess
	public AST visitRecordAccess(RecordAccess ra) {
		System.out.println(indent(ra.line) + "RecordAccess:");
		indent += 2;
		super.visitRecordAccess(ra);
		indent -= 2;
		return null;
	}
	// RecordLiteral
	public AST visitRecordLiteral(RecordLiteral rl) {
		System.out.println(indent(rl.line) + "RecordLiteral:");
		indent += 2;
		super.visitRecordLiteral(rl);
		indent -=2;
		return null;
	}
	// RecordMember
	public AST visitRecordMember(RecordMember rm) {
		System.out.println(indent(rm.line) + "RecordMember:");
		indent +=2;
		super.visitRecordMember(rm);
		indent -=2;
		return null;
	}
	// RecordTypeDecl
	public AST visitRecordTypeDecl(RecordTypeDecl rt) {
		System.out.println(indent(rt.line)+ "RecordTypeDecl:");
		indent += 2;
		super.visitRecordTypeDecl(rt);
		indent -= 2;
		return null;
	}
	// ReturnStat
	public AST visitReturnStat(ReturnStat rs) {
		if (rs.expr() == null)
			System.out.println(indent(rs.line) + "Return");
		else
			System.out.println(indent(rs.line) + "Return:");
		indent += 2;
		super.visitReturnStat(rs);
		indent -= 2;
		return null;
	}
	// Sequence
	public AST visitSequence(Sequence se) {
		System.out.println(indent(se.line) + "Sequence:[" + se.size() + " nodes]");
		int i = 0;
		for (Object a : se) {
			AST b = (AST)a;
			if (b != null) {
				System.out.println(indent(b.line) + "Sequence[" + i++ + "]:");
				indent += 2;
				b.visit(this);
				indent -= 2;
			}
		}
		return null;
	}
	// SkipStat
	public AST visitSkipStat(SkipStat ss) {
		System.out.println(indent(ss.line) + "SkipStat");
		return null;
	}
	// StopStat
	public AST visitStopStat(SkipStat ss) {
		System.out.println(indent(ss.line) + "StopStat");
		return null;
	}
	// SuspendStat
	public AST visitSuspendStat(SuspendStat ss) {        
		System.out.println(indent(ss.line) + "SuspendStat:");
		indent += 2;
		super.visitSuspendStat(ss);
		indent -= 2;
		return null;
	}
	// SwitchGroup
	public AST visitSwitchGroup(SwitchGroup sg) {        
		System.out.println(indent(sg.line) + "SwitchGroup:");
		indent += 2;
		super.visitSwitchGroup(sg);
		indent -= 2;
		return null;
	}
	// SwitchLabel
	public AST visitSwitchLabel(SwitchLabel sl) {
		System.out.println(indent(sl.line) + "SwitchLabel:");
		indent += 2;
		super.visitSwitchLabel(sl);
		indent -= 2;
		return null;
	}
	// SwitchStat
	public AST visitSwitchStat(SwitchStat st) {
		System.out.println(indent(st.line) + "SwitchStat:");
		indent += 2;
		super.visitSwitchStat(st);
		indent -= 2;
		return null;
	}
	// SyncStat
	public AST visitSyncStat(SyncStat ss) {
		System.out.println(indent(ss.line) + "SyncStat");
		indent += 2;
		super.visitSyncStat(ss);
		indent -= 2;
		return null;
	}
	// Ternary
	public AST visitTernary(Ternary te) {
		System.out.println(indent(te.line) + "Ternary:");
		indent += 2;
		super.visitTernary(te);
		indent -= 2;
		return null;
	}
	// TimeoutStat
	public AST visitTimeoutStat(TimeoutStat ts) {
		System.out.println(indent(ts.line) + "TimeoutStat:");
		indent += 2;
		super.visitTimeoutStat(ts);
		indent -= 2;
		return null;
	}	
	// UnaryPostExpr
	public AST visitUnaryPostExpr(UnaryPostExpr up) {
		System.out.println(indent(up.line) + "UnaryPostExpr:");
		indent += 2;
		super.visitUnaryPostExpr(up);
		indent -= 2;
		return null;
	}
	// UnaryPreExpr
	public AST visitUnaryPreExpr(UnaryPreExpr up) {
		System.out.println(indent(up.line) + "UnaryPreExpr:");
		indent += 2;
		super.visitUnaryPreExpr(up);
		indent -= 2;
		return null;
	}
	// Var
	public AST visitVar(Var va) {
		System.out.println(indent(va.line) + "Var:"); 
		indent += 2;
		super.visitVar(va);
		indent -= 2;
		return null;
	}
	// WhileStat
	public AST visitWhileStat(WhileStat ws) {
		System.out.println(indent(ws.line) + "WhileStat:");
		indent += 2;
		super.visitWhileStat(ws);
		indent -= 2;
		return null;
	}
}
