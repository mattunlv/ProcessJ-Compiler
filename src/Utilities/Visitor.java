package Utilities;
import AST.*;

/** Abstract class for the visitor pattern. This abstract class
 *  must be re-implemented for each traversal through the tree.
 * 
 * @author Matt Pedersen
 *
 */
public abstract class Visitor<T extends AST> {

	// The 'debug' field should be set in the constructor of the 
	// extending class.
	protected boolean debug;

	public void print(String info) {
		if (debug)
			System.out.print(info);
	}

	public void println(String info) {
		if (debug) 
			System.out.println(info);
	}

	public T visitAltCase(AltCase ac) {
		return ac.visitChildren(this);
	}
	public T visitAltStat(AltStat as) {
		return as.visitChildren(this);
	}
	public T visitArrayAccessExpr(ArrayAccessExpr ae) {
		return ae.visitChildren(this);
	}
	public T visitArrayLiteral(ArrayLiteral al) {
		return al.visitChildren(this);
	}
	public T visitArrayType(ArrayType at) {
		return at.visitChildren(this);
	}
	public T visitAssignment(Assignment as) {
		return as.visitChildren(this);
	}
	public T visitBinaryExpr(BinaryExpr be) {
		return be.visitChildren(this);
	}
	public T visitBlock(Block bl) {
		return bl.visitChildren(this);
	}
	public T visitBreakStat(BreakStat bs) {
		return null;
	}
	public T visitCastExpr(CastExpr ce) {
		return ce.visitChildren(this);
	}    
	public T visitChannelType(ChannelType ct) {
		return ct.visitChildren(this);
	}
	public T visitChannelEndExpr(ChannelEndExpr ce) {
		return ce.visitChildren(this);
	}
	public T visitChannelEndType(ChannelEndType ct) {
		return ct.visitChildren(this);
	} 
	public T visitChannelReadExpr(ChannelReadExpr cr) {
		return cr.visitChildren(this);
	}
	public T visitChannelWriteStat(ChannelWriteStat cw) {
		return cw.visitChildren(this);
	}
	public T visitClaimStat(ClaimStat cs) {
		return cs.visitChildren(this);
	}
	public T visitCompilation(Compilation co) {
		System.out.println("Visitor.Compilation");
		return co.visitChildren(this);
	}
	public T visitConstantDecl(ConstantDecl cd) {
		return cd.visitChildren(this);
	}
	public T visitContinueStat(ContinueStat cs) {
		return null;
	}
	public T visitDoStat(DoStat ds) {
		return ds.visitChildren(this);
	}
	public T visitErrorType(ErrorType et) {
		return et.visitChildren(this);
	}
	public T visitExprStat(ExprStat es) {
		return es.visitChildren(this);
	}
	public T visitExternType(ExternType et) {
		return null;
	}
	public T visitForStat(ForStat fs) {
		return fs.visitChildren(this);
	}
	public T visitGuard(Guard gu) {
		return gu.visitChildren(this);
	}
	public T visitIfStat(IfStat is) {
		return is.visitChildren(this);
	}
	public T visitImplicitImport(ImplicitImport ii) {
		return ii.visitChildren(this);
	}
	public T visitImport(Import im) {
		return im.visitChildren(this);
	}
	public T visitInvocation(Invocation in) {
		return in.visitChildren(this);
	}
	public T visitLocalDecl(LocalDecl ld) {
		return ld.visitChildren(this);
	}
	public T visitModifier(Modifier mo) {
		return null;
	}
	public T visitName(Name na) {
		return null;
	}
	public T visitNamedType(NamedType nt) {
		return nt.visitChildren(this);
	}
	public T visitNameExpr(NameExpr ne) {
		return ne.visitChildren(this);
	}
	public T visitNewArray(NewArray ne) {
		return ne.visitChildren(this);
	}
	public T visitNewMobile(NewMobile nm) {
		return nm.visitChildren(this);
	}
	public T visitParamDecl(ParamDecl pd) {
		return pd.visitChildren(this);
	}
	public T visitParBlock(ParBlock pb) {
		return pb.visitChildren(this);
	}
	public T visitPragma(Pragma pr) {
		return null;
	}
	public T visitPrimitiveLiteral(PrimitiveLiteral li) {
		return null;
	}
	public T visitPrimitiveType(PrimitiveType py) {
		return null;
	}
	public T visitProcTypeDecl(ProcTypeDecl pd) {
		return pd.visitChildren(this);
	}
	public T visitProtocolLiteral(ProtocolLiteral pl) {
		return pl.visitChildren(this);
	}
	public T visitProtocolCase(ProtocolCase pc) {
		return pc.visitChildren(this);
	}
	public T visitProtocolTypeDecl(ProtocolTypeDecl pd) {
		return pd.visitChildren(this);
	}
	public T visitQualifiedName(QualifiedName qn) {
		return qn.visitChildren(this);
	}
	public T visitRecordAccess(RecordAccess ra) {
		return ra.visitChildren(this);
	}
	public T visitRecordLiteral(RecordLiteral rl) {
		return rl.visitChildren(this);
	}
	public T visitRecordMember(RecordMember rm) {
		return rm.visitChildren(this);
	}
	public T visitRecordTypeDecl(RecordTypeDecl rt) {
		return rt.visitChildren(this);
	}
	public T visitReturnStat(ReturnStat rs) {
		return rs.visitChildren(this);
	}
	public T visitSequence(Sequence se) {
		for (int i = 0; i<se.size(); i++)
			if (se.child(i) != null)
				se.child(i).visit(this);
		return null;
	}
	public T visitSkipStat(SkipStat ss) {
		return null;
	}
	public T visitStopStat(StopStat ss) {
		return null;
	}
	public T visitSuspendStat(SuspendStat ss) {
		return ss.visitChildren(this);
	}
	public T visitSwitchGroup(SwitchGroup sg) {
		return sg.visitChildren(this);
	}
	public T visitSwitchLabel(SwitchLabel sl) {
		return sl.visitChildren(this);
	}
	public T visitSwitchStat(SwitchStat st) {
		return st.visitChildren(this);
	}
	public T visitSyncStat(SyncStat st) {
		return st.visitChildren(this);
	}
	public T visitTernary(Ternary te) {
		return te.visitChildren(this);
	}
	public T visitTimeoutStat(TimeoutStat ts) {
		return ts.visitChildren(this);
	}
	public T visitUnaryPostExpr(UnaryPostExpr up) {
		return up.visitChildren(this);
	}
	public T visitUnaryPreExpr(UnaryPreExpr up) {
		return up.visitChildren(this);
	}
	public T visitVar(Var va) {
		return va.visitChildren(this);
	}
	public T visitWhileStat(WhileStat ws) {
		return ws.visitChildren(this);
	}
}

