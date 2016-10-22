package Yield;

import Utilities.Visitor;
import AST.*;

/**
 * Adds the annotation [yield=true] to all procs that 
 * may issue a yield call.
 *
 * @author Matt Pedersen
 */

public class Yield extends Visitor<Boolean> {

    protected boolean debug;
    private static final Boolean TRUE = new Boolean(true);
    private static final Boolean FALSE = new Boolean(false);

    public Boolean visitAnnotation(Annotation at) {
        return FALSE;
    }

    public Boolean visitAnnotations(Annotations as) {
        return FALSE;
    }

    public Boolean visitAltCase(AltCase ac) {
        return TRUE;
    }

    public Boolean visitAltStat(AltStat as) {
        return TRUE;
    }

    public Boolean visitArrayAccessExpr(ArrayAccessExpr ae) {
        return FALSE;
    }

    public Boolean visitArrayLiteral(ArrayLiteral al) {
        return FALSE;
    }

    public Boolean visitArrayType(ArrayType at) {
        return FALSE;
    }

    public Boolean visitAssignment(Assignment as) {
        return FALSE;
    }

    public Boolean visitBinaryExpr(BinaryExpr be) {
        return FALSE;
    }

    public Boolean visitBlock(Block bl) {
	boolean b = false;
	for (int i=0; i<bl.stats().nchildren; i++)
	    b =b || bl.stats().child(i).visit(this);
        return new Boolean(b);
    }

    public Boolean visitBreakStat(BreakStat bs) {
        return FALSE;
    }

    public Boolean visitCastExpr(CastExpr ce) {
        return FALSE;
    }

    public Boolean visitChannelType(ChannelType ct) {
        return FALSE;
    }

    public Boolean visitChannelEndExpr(ChannelEndExpr ce) {
        return FALSE;
    }

    public Boolean visitChannelEndType(ChannelEndType ct) {
        return FALSE;
    }

    public Boolean visitChannelReadExpr(ChannelReadExpr cr) {
        return TRUE;
    }

    public Boolean visitChannelWriteStat(ChannelWriteStat cw) {
        return TRUE;
    }

    public Boolean visitClaimStat(ClaimStat cs) {
        return TRUE;
    }

    public Boolean visitCompilation(Compilation co) {
        return FALSE;
    }

    public Boolean visitConstantDecl(ConstantDecl cd) {
        return FALSE;
    }

    public Boolean visitContinueStat(ContinueStat cs) {
        return FALSE;
    }

    public Boolean visitDoStat(DoStat ds) {
        return new Boolean(ds.expr().visit(this) || ds.stat().visit(this));
    }

    public Boolean visitErrorType(ErrorType et) {
        return FALSE;
    }

    public Boolean visitExprStat(ExprStat es) {
        return es.expr().visit(this);
    }

    public Boolean visitExternType(ExternType et) {
        return FALSE;
    }

    public Boolean visitForStat(ForStat fs) {
	boolean b = fs.init().visit(this) || fs.expr().visit(this) || 
	    fs.incr().visit(this) || fs.stats().visit(this);
        return new Boolean(b);
    }

    public Boolean visitGuard(Guard gu) {
        return TRUE;
    }

    public Boolean visitIfStat(IfStat is) {
	boolean b = is.expr().visit(this) || is.thenpart().visit(this);
	if (is.elsepart() != null)
	    b = b || is.elsepart().visit(this);
        return new Boolean(b);
    }

    public Boolean visitImplicitImport(ImplicitImport ii) {
        return FALSE;
    }

    public Boolean visitImport(Import im) {
        return FALSE;
    }

    public Boolean visitInvocation(Invocation in) {
        return in.params().visit(this);
    }

    public Boolean visitLocalDecl(LocalDecl ld) {
        return FALSE;
    }

    public Boolean visitModifier(Modifier mo) {
        return FALSE;
    }

    public Boolean visitName(Name na) {
        return FALSE;
    }

    public Boolean visitNamedType(NamedType nt) {
        return FALSE;
    }

    public Boolean visitNameExpr(NameExpr ne) {
        return FALSE;
    }

    public Boolean visitNewArray(NewArray ne) {
        return new Boolean(ne.dimsExpr().visit(this) || ne.init().visit(this));
    }

    public Boolean visitNewMobile(NewMobile nm) {
        return FALSE;
    }

    public Boolean visitParamDecl(ParamDecl pd) {
        return FALSE;
    }

    public Boolean visitParBlock(ParBlock pb) {
        return TRUE;
    }

    public Boolean visitPragma(Pragma pr) {
        return FALSE;
    }

    public Boolean visitPrimitiveLiteral(PrimitiveLiteral li) {
        return FALSE;
    }

    public Boolean visitPrimitiveType(PrimitiveType py) {
        return FALSE;
    }

    public Boolean visitProcTypeDecl(ProcTypeDecl pd) {
	boolean b = pd.body().visit(this);
	if (!pd.annotations().isDefined("yield") && b)
	    pd.annotations().add("yield","true");
        return FALSE;
    }

    public Boolean visitProtocolLiteral(ProtocolLiteral pl) {
	return pl.expressions().visit(this);
    }

    public Boolean visitProtocolCase(ProtocolCase pc) {
        return FALSE;
    }

    public Boolean visitProtocolTypeDecl(ProtocolTypeDecl pd) {
        return FALSE;
    }

    public Boolean visitQualifiedName(QualifiedName qn) {
        return FALSE;
    }

    public Boolean visitRecordAccess(RecordAccess ra) {
        return ra.record().visit(this);
    }

    public Boolean visitRecordLiteral(RecordLiteral rl) {
        return rl.members().visit(this);
    }

    public Boolean visitRecordMember(RecordMember rm) {
        return FALSE;
    }

    public Boolean visitRecordTypeDecl(RecordTypeDecl rt) {
        return FALSE;
    }

    public Boolean visitReturnStat(ReturnStat rs) {
        return rs.expr().visit(this);
    }

    public Boolean visitSequence(Sequence se) {
	boolean b = false;
        for (int i = 0; i < se.size(); i++)
            if (se.child(i) != null)
                b = b || se.child(i).visit(this);
        return new Boolean(b);
    }

    public Boolean visitSkipStat(SkipStat ss) {
        return FALSE;
    }

    public Boolean visitStopStat(StopStat ss) {
        return FALSE;
    }

    public Boolean visitSuspendStat(SuspendStat ss) {
        return TRUE;
    }

    public Boolean visitSwitchGroup(SwitchGroup sg) {
        return sg.statements().visit(this);
    }

    public Boolean visitSwitchLabel(SwitchLabel sl) {
        return FALSE;
    }

    public Boolean visitSwitchStat(SwitchStat st) {
        return st.switchBlocks().visit(this);
    }

    public Boolean visitSyncStat(SyncStat st) {
        return TRUE;
    }

    public Boolean visitTernary(Ternary te) {
        return new Boolean(te.expr().visit(this) || te.trueBranch().visit(this) || te.falseBranch().visit(this));
    }

    public Boolean visitTimeoutStat(TimeoutStat ts) {
        return TRUE;
    }

    public Boolean visitUnaryPostExpr(UnaryPostExpr up) {
        return up.expr().visit(this);
    }

    public Boolean visitUnaryPreExpr(UnaryPreExpr up) {
        return up.expr().visit(this);
    }

    public Boolean visitVar(Var va) {
	if (va.init() != null)
	    return va.init().visit(this);
	else
	    return FALSE;
    }

    public Boolean visitWhileStat(WhileStat ws) {
        return new Boolean(ws.expr().visit(this) || (ws.stat() == null? false : ws.stat().visit(this)));
    }
}
