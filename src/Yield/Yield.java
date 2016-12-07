package Yield;

import AST.AltCase;
import AST.AltStat;
import AST.Annotation;
import AST.Annotations;
import AST.ArrayAccessExpr;
import AST.ArrayLiteral;
import AST.ArrayType;
import AST.Assignment;
import AST.BinaryExpr;
import AST.Block;
import AST.BreakStat;
import AST.CastExpr;
import AST.ChannelEndExpr;
import AST.ChannelEndType;
import AST.ChannelReadExpr;
import AST.ChannelType;
import AST.ChannelWriteStat;
import AST.ClaimStat;
import AST.Compilation;
import AST.ConstantDecl;
import AST.ContinueStat;
import AST.DoStat;
import AST.ErrorType;
import AST.ExprStat;
import AST.ExternType;
import AST.ForStat;
import AST.Guard;
import AST.IfStat;
import AST.ImplicitImport;
import AST.Import;
import AST.Invocation;
import AST.LocalDecl;
import AST.Modifier;
import AST.Name;
import AST.NameExpr;
import AST.NamedType;
import AST.NewArray;
import AST.NewMobile;
import AST.ParBlock;
import AST.ParamDecl;
import AST.Pragma;
import AST.PrimitiveLiteral;
import AST.PrimitiveType;
import AST.ProcTypeDecl;
import AST.ProtocolCase;
import AST.ProtocolLiteral;
import AST.ProtocolTypeDecl;
import AST.QualifiedName;
import AST.RecordAccess;
import AST.RecordLiteral;
import AST.RecordMember;
import AST.RecordTypeDecl;
import AST.ReturnStat;
import AST.Sequence;
import AST.SkipStat;
import AST.StopStat;
import AST.SuspendStat;
import AST.SwitchGroup;
import AST.SwitchLabel;
import AST.SwitchStat;
import AST.SyncStat;
import AST.Ternary;
import AST.TimeoutStat;
import AST.UnaryPostExpr;
import AST.UnaryPreExpr;
import AST.Var;
import AST.WhileStat;
import Utilities.Log;
import Utilities.Visitor;

/**
 * Adds the annotation [yield=true] to all procs that may issue a yield call.
 *
 * @author Matt Pedersen
 */

public class Yield extends Visitor<Boolean> {

    protected boolean debug;
    private static final Boolean TRUE = new Boolean(true);
    private static final Boolean FALSE = new Boolean(false);

    public Yield() {
        Log.logHeader("***********************");
        Log.logHeader("* Y I E L D   P A S S *");
        Log.logHeader("***********************");
    }

    public Boolean visitAnnotation(Annotation at) {
        Log.log("visiting an Annotation");
        return FALSE;
    }

    public Boolean visitAnnotations(Annotations as) {
        Log.log("visiting Annotations");
        return FALSE;
    }

    public Boolean visitAltCase(AltCase ac) {
        Log.log("visiting an AltCase");
        return TRUE;
    }

    public Boolean visitAltStat(AltStat as) {
        Log.log("visiting an AltStat");
        return TRUE;
    }

    public Boolean visitArrayAccessExpr(ArrayAccessExpr ae) {
        Log.log("visiting an ArrayAccessExpr");
        return ae.target().visit(this) || ae.index().visit(this);
    }

    public Boolean visitArrayLiteral(ArrayLiteral al) {
        Log.log("visiting an ArrayLiteral");
        boolean b = false;
        for (int i = 0; i < al.elements().size(); i++) {
            if (al.elements().child(i) != null) {
                boolean bb = al.elements().child(i).visit(this);
                b = b || bb;
            }
        }
        return new Boolean(b);
    }

    public Boolean visitArrayType(ArrayType at) {
        Log.log("visiting an ArrayType");
        return FALSE;
    }

    public Boolean visitAssignment(Assignment as) {
        Log.log("visiting an Assignment");
        return new Boolean(as.left().visit(this) || as.right().visit(this));
    }

    public Boolean visitBinaryExpr(BinaryExpr be) {
        Log.log("visiting a BinaryExpr");
        return new Boolean(be.left().visit(this) || be.right().visit(this));
    }

    public Boolean visitBlock(Block bl) {
        Log.log("visiting a Block");
        boolean b = false;
        for (int i = 0; i < bl.stats().size(); i++) {
            boolean bb;
            if (bl.stats().child(i) != null) {
                bb = bl.stats().child(i).visit(this);
                b = b || bb;
            }
        }
        return new Boolean(b);
    }

    public Boolean visitBreakStat(BreakStat bs) {
        Log.log("visiting a BreakStat");
        return FALSE;
    }

    public Boolean visitCastExpr(CastExpr ce) {
        Log.log("visiting a CastExpr");
        return ce.expr().visit(this);
    }

    public Boolean visitChannelType(ChannelType ct) {
        Log.log("visiting a ChannelType");
        return FALSE;
    }

    public Boolean visitChannelEndExpr(ChannelEndExpr ce) {
        Log.log("visiting a ChannelEndExpr");
        return ce.channel().visit(this);
    }

    public Boolean visitChannelEndType(ChannelEndType ct) {
        Log.log("visitChannelEndType");
        return FALSE;
    }

    public Boolean visitChannelReadExpr(ChannelReadExpr cr) {
        Log.log("visiting a ChannelReadExpr");
        return TRUE;
    }

    public Boolean visitChannelWriteStat(ChannelWriteStat cw) {
        Log.log("visiting a ChannelWriteExpr");
        return TRUE;
    }

    public Boolean visitClaimStat(ClaimStat cs) {
        Log.log("visiting a ClaimStat");
        return TRUE;
    }

    public Boolean visitCompilation(Compilation co) {
        Log.log("visiting a Compilation");
        super.visitCompilation(co);
        return FALSE;
    }

    public Boolean visitConstantDecl(ConstantDecl cd) {
        Log.log("visiting a ConstantDecl");
        return FALSE;
    }

    public Boolean visitContinueStat(ContinueStat cs) {
        Log.log("visiting a ContinueStat");
        return FALSE;
    }

    public Boolean visitDoStat(DoStat ds) {
        Log.log("visiting a DoStat");
        boolean b1 = ds.expr().visit(this);
        boolean b2 = ds.stat().visit(this);
        return new Boolean(b1 || b2);
    }

    public Boolean visitErrorType(ErrorType et) {
        Log.log("visiting an ErrorType");
        return FALSE;
    }

    public Boolean visitExprStat(ExprStat es) {
        Log.log("visiting an ExprStat");
        return es.expr().visit(this);
    }

    public Boolean visitExternType(ExternType et) {
        Log.log("visiting a ExternType");
        return FALSE;
    }

    public Boolean visitForStat(ForStat fs) {
        Log.log("visiting a ForStat");
        boolean b = false;
        if (fs.init() != null) {
            boolean bb = fs.init().visit(this);
            b = b || bb;
        }
        if (fs.expr() != null) {
            boolean bb = fs.expr().visit(this);
            b = b || bb;
        }
        if (fs.incr() != null) {
            boolean bb = fs.incr().visit(this);
            b = b || bb;
        }
        if (fs.stats() != null) {
            boolean bb = fs.stats().visit(this);
            b = b || bb;
        }
        return new Boolean(b);
    }

    public Boolean visitGuard(Guard gu) {
        Log.log("visiting a Guard");
        return TRUE;
    }

    public Boolean visitIfStat(IfStat is) {
        Log.log("visiting an IfStat");
        boolean b = is.expr().visit(this) || is.thenpart().visit(this);
        if (is.elsepart() != null) {
            boolean bb = is.elsepart().visit(this);
            b = b || bb;
        }
        return new Boolean(b);
    }

    public Boolean visitImplicitImport(ImplicitImport ii) {
        Log.log("visiting an ImplicitImport");
        return FALSE;
    }

    public Boolean visitImport(Import im) {
        Log.log("visiting an Import");
        return FALSE;
    }

    public Boolean visitInvocation(Invocation in) {
        Log.log("visiting a Invocation");
        return in.params().visit(this);
    }

    public Boolean visitLocalDecl(LocalDecl ld) {
        Log.log("visiting a LocalDecl");
        return FALSE;
    }

    public Boolean visitModifier(Modifier mo) {
        Log.log("visiting a Modifier");
        return FALSE;
    }

    public Boolean visitName(Name na) {
        Log.log("visiting a Name");
        return FALSE;
    }

    public Boolean visitNamedType(NamedType nt) {
        Log.log("visiting a NamedType");
        return FALSE;
    }

    public Boolean visitNameExpr(NameExpr ne) {
        Log.log("visiting a NameExpr");
        return FALSE;
    }

    public Boolean visitNewArray(NewArray ne) {
        Log.log("visiting a NewArray");
        boolean b = false;
        b = ne.dimsExpr().visit(this);
        if (ne.init() != null)
            b = b || ne.init().visit(this);
        return new Boolean(b);
    }

    public Boolean visitNewMobile(NewMobile nm) {
        Log.log("visiting a NewMobile");
        return FALSE;
    }

    public Boolean visitParamDecl(ParamDecl pd) {
        Log.log("visiting a ParamDecl");
        return FALSE;
    }

    public Boolean visitParBlock(ParBlock pb) {
        Log.log("visiting a ParBlock");
        return TRUE;
    }

    public Boolean visitPragma(Pragma pr) {
        Log.log("visiting a Pragma");
        return FALSE;
    }

    public Boolean visitPrimitiveLiteral(PrimitiveLiteral li) {
        Log.log("visiting a PrimitiveLiteral");
        return FALSE;
    }

    public Boolean visitPrimitiveType(PrimitiveType py) {
        Log.log("visiting a PrimitiveType");
        return FALSE;
    }

    public Boolean visitProcTypeDecl(ProcTypeDecl pd) {
        Log.log("visiting a ProcTypeDecl");
        boolean b = pd.body().visit(this);
        if (!pd.annotations().isDefined("yield") && b) {
            pd.annotations().add("yield", "true");
            Log.log("  Setting [yield=true] for " + pd.name() + ".");
        } else if (pd.name().getname().equals("main")) {
            pd.annotations().add("yield", "true");
            Log.log("  Setting [yield=true] for " + pd.name() + ".");
        }
        return FALSE;
    }

    public Boolean visitProtocolLiteral(ProtocolLiteral pl) {
        Log.log("visiting a ProtocolLiteral");
        return pl.expressions().visit(this);
    }

    public Boolean visitProtocolCase(ProtocolCase pc) {
        Log.log("visiting a ProtocolCase");
        return FALSE;
    }

    public Boolean visitProtocolTypeDecl(ProtocolTypeDecl pd) {
        Log.log("visiting a ProtocolType");
        return FALSE;
    }

    public Boolean visitQualifiedName(QualifiedName qn) {
        Log.log("visiting a QualifiedName");
        return FALSE;
    }

    public Boolean visitRecordAccess(RecordAccess ra) {
        Log.log("visiting a RecordAccess");
        return ra.record().visit(this);
    }

    public Boolean visitRecordLiteral(RecordLiteral rl) {
        Log.log("visiting a RecordLiteral");
        return rl.members().visit(this);
    }

    public Boolean visitRecordMember(RecordMember rm) {
        Log.log("visiting a RecordMember");
        return FALSE;
    }

    public Boolean visitRecordTypeDecl(RecordTypeDecl rt) {
        Log.log("visiting a RecordTypeDecl");
        return FALSE;
    }

    public Boolean visitReturnStat(ReturnStat rs) {
        Log.log("visiting a ReturnStat");
        if (rs.expr() != null)
            return rs.expr().visit(this);
        else
            return FALSE;
    }

    public Boolean visitSequence(Sequence se) {
        Log.log("visiting a Sequence");
        boolean b = false;
        for (int i = 0; i < se.size(); i++) {
            if (se.child(i) != null) {
                boolean bb = se.child(i).visit(this);
                b = b || bb;
            }
        }
        return new Boolean(b);
    }

    public Boolean visitSkipStat(SkipStat ss) {
        Log.log("visiting a SkipStat");
        return FALSE;
    }

    public Boolean visitStopStat(StopStat ss) {
        Log.log("visiting a StopStat");
        return FALSE;
    }

    public Boolean visitSuspendStat(SuspendStat ss) {
        Log.log("visiting a SuspendStat");
        return TRUE;
    }

    public Boolean visitSwitchGroup(SwitchGroup sg) {
        Log.log("visiting a SwitchGroup");
        return sg.statements().visit(this);
    }

    public Boolean visitSwitchLabel(SwitchLabel sl) {
        Log.log("visiting a SwitchLabel");
        return FALSE;
    }

    public Boolean visitSwitchStat(SwitchStat st) {
        Log.log("visiting a SwitchBlock");
        return st.switchBlocks().visit(this);
    }

    public Boolean visitSyncStat(SyncStat st) {
        Log.log("visiting a SyncStat");
        return TRUE;
    }

    public Boolean visitTernary(Ternary te) {
        Log.log("visiting a Ternary");
        return new Boolean(te.expr().visit(this) || te.trueBranch().visit(this) || te.falseBranch().visit(this));
    }

    public Boolean visitTimeoutStat(TimeoutStat ts) {
        Log.log("visiting a TimeoutStat");
        return TRUE;
    }

    public Boolean visitUnaryPostExpr(UnaryPostExpr up) {
        Log.log("visiting a UnaryPostExpr");
        return up.expr().visit(this);
    }

    public Boolean visitUnaryPreExpr(UnaryPreExpr up) {
        Log.log("visiting a UnaryPreExpr");
        return up.expr().visit(this);
    }

    public Boolean visitVar(Var va) {
        Log.log("visiting a Var");
        if (va.init() != null)
            return va.init().visit(this);
        else
            return FALSE;
    }

    public Boolean visitWhileStat(WhileStat ws) {
        Log.log("visiting a WhileStat");
        return new Boolean(ws.expr().visit(this) || (ws.stat() == null ? false : ws.stat().visit(this)));
    }
}
