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
        System.out.println("visiting an Annotation");
        return FALSE;
    }

    public Boolean visitAnnotations(Annotations as) {
        System.out.println("visiting Annotations");
        return FALSE;
    }

    public Boolean visitAltCase(AltCase ac) {
        System.out.println("visiting an AltCase");
        return TRUE;
    }

    public Boolean visitAltStat(AltStat as) {
        System.out.println("visiting an AltStat");
        return TRUE;
    }

    public Boolean visitArrayAccessExpr(ArrayAccessExpr ae) {
        System.out.println("visiting an ArrayAccessExpr");
        return ae.target().visit(this) || ae.index().visit(this);
    }

    public Boolean visitArrayLiteral(ArrayLiteral al) {
        System.out.println("visiting an ArrayLiteral");
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
        System.out.println("visiting an ArrayType");
        return FALSE;
    }

    public Boolean visitAssignment(Assignment as) {
        System.out.println("visiting an Assignment");
        return new Boolean(as.left().visit(this) || as.right().visit(this));
    }

    public Boolean visitBinaryExpr(BinaryExpr be) {
        System.out.println("visiting a BinaryExpr");
        return new Boolean(be.left().visit(this) || be.right().visit(this));
    }

    public Boolean visitBlock(Block bl) {
        System.out.println("visiting a Block");
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
        System.out.println("visiting a BreakStat");
        return FALSE;
    }

    public Boolean visitCastExpr(CastExpr ce) {
        System.out.println("visiting a CastExpr");
        return ce.expr().visit(this);
    }

    public Boolean visitChannelType(ChannelType ct) {
        System.out.println("visiting a ChannelType");
        return FALSE;
    }

    public Boolean visitChannelEndExpr(ChannelEndExpr ce) {
        System.out.println("visiting a ChannelEndExpr");
        return ce.channel().visit(this);
    }

    public Boolean visitChannelEndType(ChannelEndType ct) {
        System.out.println("visitChannelEndType");
        return FALSE;
    }

    public Boolean visitChannelReadExpr(ChannelReadExpr cr) {
        System.out.println("visiting a ChannelReadExpr");
        return TRUE;
    }

    public Boolean visitChannelWriteStat(ChannelWriteStat cw) {
        System.out.println("visiting a ChannelWriteExpr");
        return TRUE;
    }

    public Boolean visitClaimStat(ClaimStat cs) {
        System.out.println("visiting a ClaimStat");
        return TRUE;
    }

    public Boolean visitCompilation(Compilation co) {
        System.out.println("visiting a Compilation");
        super.visitCompilation(co);
        return FALSE;
    }

    public Boolean visitConstantDecl(ConstantDecl cd) {
        System.out.println("visiting a ConstantDecl");
        return FALSE;
    }

    public Boolean visitContinueStat(ContinueStat cs) {
        System.out.println("visiting a ContinueStat");
        return FALSE;
    }

    public Boolean visitDoStat(DoStat ds) {
        System.out.println("visiting a DoStat");
        boolean b1 = ds.expr().visit(this);
        boolean b2 = ds.stat().visit(this);
        return new Boolean(b1 || b2);
    }

    public Boolean visitErrorType(ErrorType et) {
        System.out.println("visiting an ErrorType");
        return FALSE;
    }

    public Boolean visitExprStat(ExprStat es) {
        System.out.println("visiting an ExprStat");
        return es.expr().visit(this);
    }

    public Boolean visitExternType(ExternType et) {
        System.out.println("visiting a ExternType");
        return FALSE;
    }

    public Boolean visitForStat(ForStat fs) {
        System.out.println("visiting a ForStat");
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
        System.out.println("visiting a Guard");
        return TRUE;
    }

    public Boolean visitIfStat(IfStat is) {
        System.out.println("visiting an IfStat");
        boolean b = is.expr().visit(this) || is.thenpart().visit(this);
        if (is.elsepart() != null) {
            boolean bb = is.elsepart().visit(this);
            b = b || bb;
        }
        return new Boolean(b);
    }

    public Boolean visitImplicitImport(ImplicitImport ii) {
        System.out.println("visiting an ImplicitImport");
        return FALSE;
    }

    public Boolean visitImport(Import im) {
        System.out.println("visiting an Import");
        return FALSE;
    }

    public Boolean visitInvocation(Invocation in) {
        System.out.println("visiting a Invocation");
        return in.params().visit(this);
    }

    public Boolean visitLocalDecl(LocalDecl ld) {
        System.out.println("visiting a LocalDecl");
        return FALSE;
    }

    public Boolean visitModifier(Modifier mo) {
        System.out.println("visiting a Modifier");
        return FALSE;
    }

    public Boolean visitName(Name na) {
        System.out.println("visiting a Name");
        return FALSE;
    }

    public Boolean visitNamedType(NamedType nt) {
        System.out.println("visiting a NamedType");
        return FALSE;
    }

    public Boolean visitNameExpr(NameExpr ne) {
        System.out.println("visiting a NameExpr");
        return FALSE;
    }

    public Boolean visitNewArray(NewArray ne) {
        System.out.println("visiting a NewArray");
        boolean b = false;
        b = ne.dimsExpr().visit(this);
        if (ne.init() != null)
            b = b || ne.init().visit(this);
        return new Boolean(b);
    }

    public Boolean visitNewMobile(NewMobile nm) {
        System.out.println("visiting a NewMobile");
        return FALSE;
    }

    public Boolean visitParamDecl(ParamDecl pd) {
        System.out.println("visiting a ParamDecl");
        return FALSE;
    }

    public Boolean visitParBlock(ParBlock pb) {
        System.out.println("visiting a ParBlock");
        return TRUE;
    }

    public Boolean visitPragma(Pragma pr) {
        System.out.println("visiting a Pragma");
        return FALSE;
    }

    public Boolean visitPrimitiveLiteral(PrimitiveLiteral li) {
        System.out.println("visiting a PrimitiveLiteral");
        return FALSE;
    }

    public Boolean visitPrimitiveType(PrimitiveType py) {
        System.out.println("visiting a PrimitiveType");
        return FALSE;
    }

    public Boolean visitProcTypeDecl(ProcTypeDecl pd) {
        System.out.println("visiting a ProcTypeDecl");
        boolean b = pd.body().visit(this);
        if (!pd.annotations().isDefined("yield") && b) {
            pd.annotations().add("yield", "true");
            System.out.println("  Setting [yield=true] for " + pd.name() + ".");
        } else if (pd.name().getname().equals("main")) {
            pd.annotations().add("yield", "true");
            System.out.println("  Setting [yield=true] for " + pd.name() + ".");
        }
        return FALSE;
    }

    public Boolean visitProtocolLiteral(ProtocolLiteral pl) {
        System.out.println("visiting a ProtocolLiteral");
        return pl.expressions().visit(this);
    }

    public Boolean visitProtocolCase(ProtocolCase pc) {
        System.out.println("visiting a ProtocolCase");
        return FALSE;
    }

    public Boolean visitProtocolTypeDecl(ProtocolTypeDecl pd) {
        System.out.println("visiting a ProtocolType");
        return FALSE;
    }

    public Boolean visitQualifiedName(QualifiedName qn) {
        System.out.println("visiting a QualifiedName");
        return FALSE;
    }

    public Boolean visitRecordAccess(RecordAccess ra) {
        System.out.println("visiting a RecordAccess");
        return ra.record().visit(this);
    }

    public Boolean visitRecordLiteral(RecordLiteral rl) {
        System.out.println("visiting a RecordLiteral");
        return rl.members().visit(this);
    }

    public Boolean visitRecordMember(RecordMember rm) {
        System.out.println("visiting a RecordMember");
        return FALSE;
    }

    public Boolean visitRecordTypeDecl(RecordTypeDecl rt) {
        System.out.println("visiting a RecordTypeDecl");
        return FALSE;
    }

    public Boolean visitReturnStat(ReturnStat rs) {
        System.out.println("visiting a ReturnStat");
        if (rs.expr() != null)
            return rs.expr().visit(this);
        else
            return FALSE;
    }

    public Boolean visitSequence(Sequence se) {
        System.out.println("visiting a Sequence");
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
        System.out.println("visiting a SkipStat");
        return FALSE;
    }

    public Boolean visitStopStat(StopStat ss) {
        System.out.println("visiting a StopStat");
        return FALSE;
    }

    public Boolean visitSuspendStat(SuspendStat ss) {
        System.out.println("visiting a SuspendStat");
        return TRUE;
    }

    public Boolean visitSwitchGroup(SwitchGroup sg) {
        System.out.println("visiting a SwitchGroup");
        return sg.statements().visit(this);
    }

    public Boolean visitSwitchLabel(SwitchLabel sl) {
        System.out.println("visiting a SwitchLabel");
        return FALSE;
    }

    public Boolean visitSwitchStat(SwitchStat st) {
        System.out.println("visiting a SwitchBlock");
        return st.switchBlocks().visit(this);
    }

    public Boolean visitSyncStat(SyncStat st) {
        System.out.println("visiting a SyncStat");
        return TRUE;
    }

    public Boolean visitTernary(Ternary te) {
        System.out.println("visiting a Ternary");
        return new Boolean(te.expr().visit(this) || te.trueBranch().visit(this) || te.falseBranch().visit(this));
    }

    public Boolean visitTimeoutStat(TimeoutStat ts) {
        System.out.println("visiting a TimeoutStat");
        return TRUE;
    }

    public Boolean visitUnaryPostExpr(UnaryPostExpr up) {
        System.out.println("visiting a UnaryPostExpr");
        return up.expr().visit(this);
    }

    public Boolean visitUnaryPreExpr(UnaryPreExpr up) {
        System.out.println("visiting a UnaryPreExpr");
        return up.expr().visit(this);
    }

    public Boolean visitVar(Var va) {
        System.out.println("visiting a Var");
        if (va.init() != null)
            return va.init().visit(this);
        else
            return FALSE;
    }

    public Boolean visitWhileStat(WhileStat ws) {
        System.out.println("visiting a WhileStat");
        return new Boolean(ws.expr().visit(this) || (ws.stat() == null ? false : ws.stat().visit(this)));
    }
}
