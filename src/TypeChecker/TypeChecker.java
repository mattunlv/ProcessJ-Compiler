/**
 * 
 */
package TypeChecker;

import java.math.BigDecimal;
import java.util.HashSet;
import java.util.Hashtable;
import java.util.Set;

import AST.AltCase;
import AST.ArrayAccessExpr;
import AST.ArrayLiteral;
import AST.ArrayType;
import AST.Assignment;
import AST.BinaryExpr;
import AST.BreakStat;
import AST.CastExpr;
import AST.ChannelEndExpr;
import AST.ChannelEndType;
import AST.ChannelReadExpr;
import AST.ChannelType;
import AST.ChannelWriteStat;
import AST.ConstantDecl;
import AST.DoStat;
import AST.ErrorType;
import AST.Expression;
import AST.ForStat;
import AST.IfStat;
import AST.Invocation;
import AST.LocalDecl;
import AST.Modifier;
import AST.Name;
import AST.NameExpr;
import AST.NamedType;
import AST.NewArray;
import AST.ParamDecl;
import AST.PrimitiveLiteral;
import AST.PrimitiveType;
import AST.ProcTypeDecl;
import AST.ProtocolCase;
import AST.ProtocolLiteral;
import AST.ProtocolTypeDecl;
import AST.RecordAccess;
import AST.RecordLiteral;
import AST.RecordMember;
import AST.RecordTypeDecl;
import AST.ReturnStat;
import AST.Sequence;
import AST.SuspendStat;
import AST.SwitchGroup;
import AST.SwitchLabel;
import AST.SwitchStat;
import AST.SyncStat;
import AST.Ternary;
import AST.TimeoutStat;
import AST.Type;
import AST.UnaryPostExpr;
import AST.UnaryPreExpr;
import AST.Var;
import AST.VarDecl;
import AST.WhileStat;
import Utilities.Error;
import Utilities.Log;
import Utilities.SymbolTable;
import Utilities.Visitor;

/**
 * Change Log:
 *
 */

/**
 * @author Matt Pedersen
 *
 */
public class TypeChecker extends Visitor<Type> {
    private ProcTypeDecl currentProcedure = null;
    SymbolTable topLevelDecls = null;

    // Local hooks
    boolean inSwitch = false;

    // other stuff
    // Contains the protocol name and the corresponding tags currently switched on.
    Hashtable<String, ProtocolCase> protocolTagsSwitchedOn = new Hashtable<String, ProtocolCase>();

    public TypeChecker(SymbolTable topLevelDecls) {
        debug = true;
        this.topLevelDecls = topLevelDecls;

        Log.log("======================================");
        Log.log("*       T Y P E   C H E C K E R      *");
        Log.log("======================================");

    }

    // All visit calls must call resolve	
    public Type resolve(Type t) {
        Log.log("  > Resolve: " + t);

        if (t.isErrorType())
            return t;
        if (t.isNamedType()) {
            Log.log("  > Resolving named type: "
                    + ((NamedType) t).name().getname());
            Type tt = t.visit(this); // this resolves all NamedTypes
            Log.log("  > Resolved " + ((NamedType) t).name().getname() + " -> "
                    + tt.typeName());
            return tt;
        } else {
            Log.log("  > Nothing to resolve - type remains: " + t);
            return t;
        }
    }

    /** ALT CASE */
    // ERROR TYPE OK // addError OK
    public Type visitAltCase(AltCase ac) {
        Log.log(ac.line + ": Visiting an alt case.");

        // Check the pre-condition if there is one.
        if (ac.precondition() != null) {
            Type t = ac.precondition().visit(this);
            t = resolve(t.visit(this));
            if (!t.isBooleanType())
                Error.addError(ac.precondition(),
                        "Boolean type expected in precondition of alt case, found: "
                                + t, 3035); // test ok
        }
        ac.guard().visit(this);
        ac.stat().visit(this);
        return null;
    }

    // AltStat -- Nothing to do

    /** ArrayAccessExpr */
    // ERROR TYPE OK // addError OK
    public Type visitArrayAccessExpr(ArrayAccessExpr ae) {
        Log.log(ae.line + ": Visiting ArrayAccessExpr");
        Type t = resolve(ae.target().visit(this));
        if (!t.isArrayType()) {
            ae.type = Error.addError(ae, "Array type required, but found type "
                    + t.typeName() + ".", 3000); // test ok
        } else {
            ArrayType at = (ArrayType) t;
            if (at.getDepth() == 1)
                ae.type = at.baseType();
            else
                ae.type = new ArrayType(at.baseType(), at.getDepth() - 1);
            Log.log(ae.line + ": ArrayAccessExpr has type " + ae.type);
            Type indexType = resolve(ae.index().visit(this));
            // This error does not create an error type cause the baseType is still the 
            // array expression's type
            if (!indexType.isIntegerType())
                Error.addError(ae,
                        "Array access index must be of integral type.", 3001); // test ok
        }
        return ae.type;
    }

    /** ARRAY LITERAL */
    // ERROR TYPE OK
    public Type visitArrayLiteral(ArrayLiteral al) {
        Log.log(al.line + ": visiting an array literal.");
        Error.error(al, "Array literal with the keyword 'new'.", false, 3002); // TODO: not sure what this is and what it means
        return null;
    }

    /** ArrayType */
    // ERROR TYPE OK // addError OK
    public Type visitArrayType(ArrayType at) {
        Log.log(at.line + ": Visiting an ArrayType");
        Log.log(at.line + ": ArrayType type is " + at);
        return at;
    }

    /** ASSIGNMENT */
    // ERROR TYPE OK
    public Type visitAssignment(Assignment as) {
        Log.log(as.line + ": Visiting an assignment");

        as.type = null; // gets set to ErrorType if an error happens.

        Type vType = resolve(as.left().visit(this));
        Type eType = resolve(as.right().visit(this));

        // Handle error types in operands
        if (vType.isErrorType() || eType.isErrorType()) {
            as.type = new ErrorType();
            return as.type;
        }

        //as.right().type = eType; // TODO: wouldn't this have been set in the visit call already?

        /** Note: as.left() should be of NameExpr or RecordAccess or ArrayAccessExpr class! */

        if (!vType.assignable())
            Error.error(as, "Left hand side of assignment not assignable.",
                    false, 3036);

        switch (as.op()) {
            case Assignment.EQ: {
                if (!Type.assignmentCompatible(vType, eType))
                    as.type = Error.addError(as, "Cannot assign value of type "
                            + eType.typeName() + " to variable of type "
                            + vType.typeName() + ".", 3003); // test OK
                break;
            }
            case Assignment.MULTEQ:
            case Assignment.DIVEQ:
            case Assignment.MODEQ:
            case Assignment.PLUSEQ:
            case Assignment.MINUSEQ:
                if (!Type.assignmentCompatible(vType, eType))
                    as.type = Error.addError(as, "Cannot assign value of type "
                            + eType.typeName() + " to variable of type "
                            + vType.typeName() + ".", 3004); // test OK
                else if (!eType.isNumericType())
                    as.type = Error.addError(
                            as,
                            "Right hand side operand of operator '"
                                    + as.opString()
                                    + "' must be of numeric type.", 3005); // test OK
                else if (!vType.isNumericType() && as.op() != Assignment.PLUSEQ)
                    as.type = Error.addError(
                            as,
                            "Left hand side operand of operator '"
                                    + as.opString()
                                    + "' must be of numeric type.", 3006);
                else if (as.op() == Assignment.PLUSEQ
                        && !(vType.isNumericType() || vType.isStringType()))
                    as.type = Error.addError(
                            as,
                            "Left hand side operand of operator '"
                                    + as.opString()
                                    + "' must be of numeric or string type.",
                            3036);
                break;
            case Assignment.LSHIFTEQ:
            case Assignment.RSHIFTEQ:
            case Assignment.RRSHIFTEQ:
                if (!vType.isIntegralType())
                    as.type = Error.addError(
                            as,
                            "Left hand side operand of operator '"
                                    + as.opString()
                                    + "' must be of integer type.", 3007);
                if (!eType.isIntegralType())
                    as.type = Error.addError(
                            as,
                            "Right hand side operand of operator '"
                                    + as.opString()
                                    + "' must be of integer type.", 3008);
                break;
            case Assignment.ANDEQ:
            case Assignment.OREQ:
            case Assignment.XOREQ:
                if (!((vType.isIntegralType() && eType.isIntegralType()) || (vType
                        .isBooleanType() && eType.isBooleanType())))
                    as.type = Error
                            .addError(
                                    as,
                                    "Both right and left hand side operands of operator '"
                                            + as.opString()
                                            + "' must be either of boolean or integer type.",
                                    3009);
                break;
        }
        if (as.type == null)
            as.type = vType;
        Log.log(as.line + ": Assignment has type: " + as.type);

        return vType;
    }

    /** BINARY EXPRESSION */
    // ERROR TYPE OK
    public Type visitBinaryExpr(BinaryExpr be) {
        Log.log(be.line + ": Visiting a Binary Expression");

        Type lType = resolve(be.left().visit(this));
        Type rType = resolve(be.right().visit(this));
        String op = be.opString();

        // Handle errors from type checking operands
        if (lType.isErrorType() || rType.isErrorType()) {
            be.type = new ErrorType();
            Log.log(be.line + ": Binary Expression has type: " + be.type);
            return be.type;
        }

        switch (be.op()) {
        // < > <= >= : Type can be Integer only.        
            case BinaryExpr.LT:
            case BinaryExpr.GT:
            case BinaryExpr.LTEQ:
            case BinaryExpr.GTEQ: {
                if (lType.isNumericType() && rType.isNumericType()) {
                    be.type = new PrimitiveType(PrimitiveType.BooleanKind);
                } else
                    be.type = Error.addError(be, "Operator '" + op
                            + "' requires operands of numeric type.", 3010);
                break;
            }
            // == != : Type can be anything but void.
            case BinaryExpr.EQEQ:
            case BinaryExpr.NOTEQ: {

                // TODO: barriers, timers, procs, records and protocols
                // Funny issues with inheritance for records and protocols.
                // should they then get a namedType as a type?
                // extern types cannot be compared at all!

                if (lType.identical(rType))
                    if (lType.isVoidType())
                        be.type = Error.addError(be,
                                "Void type cannot be used here.", 3011);
                    else
                        be.type = new PrimitiveType(PrimitiveType.BooleanKind);
                else if (lType.isNumericType() && rType.isNumericType())
                    be.type = new PrimitiveType(PrimitiveType.BooleanKind);
                else
                    be.type = Error.addError(be, "Operator '" + op
                            + "' requires operands of the same type.", 3012);
                break;
            }
            // && || : Type can be Boolean only.
            case BinaryExpr.ANDAND:
            case BinaryExpr.OROR: {
                if (lType.isBooleanType() && rType.isBooleanType())
                    be.type = lType;
                else
                    be.type = Error.addError(be, "Operator '" + op
                            + "' requires operands of boolean type.", 3013);
                break;
            }
            // & | ^ : Type can be Boolean or Integral
            case BinaryExpr.AND:
            case BinaryExpr.OR:
            case BinaryExpr.XOR: {
                if ((lType.isBooleanType() && rType.isBooleanType())
                        || (lType.isIntegralType() && rType.isIntegralType())) {
                    be.type = PrimitiveType.ceilingType((PrimitiveType) lType,
                            (PrimitiveType) rType);
                    ;
                    // TODO: don't do this anywhere!!
                    // promote byte, short and char to int
                    if (be.type.isByteType() || be.type.isShortType()
                            || be.type.isCharType())
                        be.type = new PrimitiveType(PrimitiveType.IntKind);

                } else
                    be.type = Error
                            .addError(
                                    be,
                                    "Operator '"
                                            + op
                                            + "' requires both operands of either integral or boolean type.",
                                    3014);
                break;
            }
            // + - * / % : Type must be numeric
            case BinaryExpr.PLUS:
            case BinaryExpr.MINUS:
            case BinaryExpr.MULT:
            case BinaryExpr.DIV:
            case BinaryExpr.MOD: {
                if (lType.isNumericType() && rType.isNumericType()) {
                    be.type = new PrimitiveType(PrimitiveType.ceiling(
                            (PrimitiveType) lType, (PrimitiveType) rType));

                    if (be.type.isByteType() || be.type.isShortType()
                            || be.type.isCharType())
                        be.type = new PrimitiveType(PrimitiveType.IntKind);
                } else if ((lType.isStringType() && (rType.isNumericType()
                        || rType.isBooleanType() || rType.isStringType()))
                        || (rType.isStringType() && (lType.isNumericType()
                                || lType.isBooleanType() || lType
                                    .isStringType())))
                    be.type = new PrimitiveType(PrimitiveType.StringKind);
                else
                    be.type = Error.addError(be, "Operator '" + op
                            + "' requires operands of numeric type.", 3015);
                break;
            }
            // << >> >>>: 
            case BinaryExpr.LSHIFT:
            case BinaryExpr.RSHIFT:
            case BinaryExpr.RRSHIFT: {
                if (!lType.isIntegralType())
                    be.type = Error
                            .addError(
                                    be,
                                    "Operator '"
                                            + op
                                            + "' requires left operand of integral type.",
                                    3016);
                if (!rType.isIntegralType())
                    be.type = Error.addError(be, "Operator '" + op
                            + "' requires right operand of integral type.",
                            3017);
                be.type = lType;
                break;
            }
            default:
                be.type = Error.addError(be, "Unknown operator '" + op + "'.",
                        3018);
        }
        Log.log(be.line + ": Binary Expression has type: " + be.type);
        return be.type;
    }

    // Block - nothing to do

    // BreakStat

    // CAST EXPRESSION // ERROR TYPE OK
    public Type visitCastExpr(CastExpr ce) {
        Log.log(ce.line + ": Visiting a cast expression");

        Type exprType = resolve(ce.expr().visit(this));
        Type castType = resolve(ce.type()); // Not sure the 'resolve' is needed here

        // Handle errors here
        if (exprType.isErrorType() || castType.isErrorType()) {
            ce.type = new ErrorType();
            return ce.type;
        }

        if (exprType.isNumericType() && castType.isNumericType()) {
            ce.type = castType;
            Log.log(ce.line + ": Cast Expression has type: " + ce.type);
            return castType;
        }
        // Turns out that casts like this are illegal:
        // int a[][];
        // double b[][];
        // a = (int[][])b;
        // b = (double[][])a;

        // BUT record can be cast and probably protocols too!
        if (castType.isRecordType() || castType.isProtocolType())
            Log.log("TODO: TypeChecker.visitCastExpr(): no implementation for protocol and record types.");
        ce.type = castType;

        // TODO: other types here!	

        Log.log(ce.line + ": Cast Expression has type: " + ce.type);
        return ce.type;
    }

    /** CHANNEL TYPE */
    // ERROR TYPE OK
    public Type visitChannelType(ChannelType ct) {
        Log.log(ct.line + ": Visiting a channel type.");
        ct.baseType().visit(this);
        Log.log(ct.line + ": Channel type has type: " + ct);
        return ct;
    }

    /** CHANNEL END EXPRESSION */
    // ERROR TYPE OK
    public Type visitChannelEndExpr(ChannelEndExpr ce) {
        Log.log(ce.line + ": Visiting a channel end expression.");
        Type t = resolve(ce.channel().visit(this));

        // Handle error types
        if (t.isErrorType()) {
            ce.type = t;
            return ce.type;
        }

        if (!t.isChannelType()) {
            ce.type = Error.addError(ce,
                    "Channel end expression requires channel type.", 3019);
            return ce.type;
        }

        ChannelType ct = (ChannelType) t;
        int end = (ce.isRead() ? ChannelEndType.READ_END
                : ChannelEndType.WRITE_END);
        if (ct.shared() == ChannelType.NOT_SHARED)
            ce.type = new ChannelEndType(ChannelEndType.NOT_SHARED,
                    ct.baseType(), end);
        else if (ct.shared() == ChannelType.SHARED_READ_WRITE)
            ce.type = new ChannelEndType(ChannelEndType.SHARED, ct.baseType(),
                    end);
        else if (ct.shared() == ChannelType.SHARED_READ)
            ce.type = new ChannelEndType(
                    (ce.isRead() && ct.shared() == ChannelType.SHARED_READ) ? ChannelEndType.SHARED
                            : ChannelType.NOT_SHARED, ct.baseType(), end);
        else if (ct.shared() == ChannelType.SHARED_WRITE)
            ce.type = new ChannelEndType(
                    (ce.isWrite() && ct.shared() == ChannelType.SHARED_WRITE) ? ChannelEndType.SHARED
                            : ChannelType.NOT_SHARED, ct.baseType(), end);
        else
            ce.type = Error.addError(ce,
                    "Unknown sharing status for channel end expression.", 3020);
        Log.log(ce.line + ": Channel End Expr has type: " + ce.type);
        return ce.type;
    }

    /** CHANNEL END TYPE */
    // ERROR TYPE OK
    public Type visitChannelEndType(ChannelEndType ct) {
        Log.log(ct.line + ": Visiting a channel end type.");

        ct.baseType().visit(this);
        Log.log(ct.line + ": Channel end type " + ct);
        return ct;
    }

    /** CHANNEL READ EXPRESSION */
    // ERROR TYPE OK
    public Type visitChannelReadExpr(ChannelReadExpr cr) {
        Log.log(cr.line + ": Visiting a channel read expression.");

        // TODO: targetType MAY be a channelType:
        // chan<int> c;
        // c.read();   <-------- this does not give the right type

        Type targetType = resolve(cr.channel().visit(this));
        if (!(targetType.isChannelEndType() || targetType.isTimerType() || targetType
                .isChannelType())) {
            cr.type = Error.addError(cr,
                    "Channel or Timer type required in channel/timer read.",
                    3021);
            return cr.type;
        }
        if (targetType.isChannelEndType()) {
            ChannelEndType cet = (ChannelEndType) targetType;
            cr.type = cet.baseType();
        } else if (targetType.isChannelType()) {
            cr.type = ((ChannelType) targetType).baseType();
        } else {
            // must be a tiemr type, and timer read() returns values of type long.
            cr.type = new PrimitiveType(PrimitiveType.LongKind);
        }
        if (targetType.isTimerType() && cr.extRV() != null)
            Error.addError(cr,
                    "Timer read cannot have extended rendez-vous block.", 3022);

        if (cr.extRV() != null)
            cr.extRV().visit(this);
        Log.log(cr.line + ": Channel read expression has type: " + cr.type);
        return cr.type;
    }

    /** CHANNEL WRITE STATEMENT */
    // ERROR TYPE OK
    public Type visitChannelWriteStat(ChannelWriteStat cw) {
        Log.log(cw.line + ": Visiting a channel write stat.");
        Type t = resolve(cw.channel().visit(this));
        // Check that the expression is of channel end type or channel type
        if (!(t.isChannelEndType() || t.isChannelType()))
            Error.error(cw, "Cannot write to a non-channel end.", false, 3023);
        cw.expr().visit(this);
        return null;
    }

    // ClaimStat
    // Compilation -- Probably nothing to 
    // ConstantDecl -- ??

    // ContinueStat - nothing to do here, but further checks are needed. TODO

    /** DO STATEMENT */
    //ERROR TYPE OK - I THINK
    public Type visitDoStat(DoStat ds) {
        Log.log(ds.line + ": Visiting a do statement");

        // Compute the type of the expression
        Type eType = resolve(ds.expr().visit(this));

        // Check that the type of the expression is a boolean
        if (!eType.isBooleanType())
            Error.error(ds,
                    "Non boolean Expression found as test in do-statement.",
                    false, 3024);

        // Type check the statement of the do statement;
        if (ds.stat() != null)
            ds.stat().visit(this);

        return null;
    }

    // ExprStat - nothing to do

    // ExternType 

    /** FOR STATEMENT */
    // ERROR TYPE OK
    public Type visitForStat(ForStat fs) {
        Log.log(fs.line + ": Visiting a for statement");

        int i = 0;
        // TODO: must block be par to enroll on barriers??
        // Check that all the barrier expressions are of barrier type.
        for (Expression e : fs.barriers()) {
            Type t = resolve(e.visit(this));
            if (!t.isBarrierType())
                Error.error(fs.barriers().child(i),
                        "Barrier type expected, found '" + t + "'.", false,
                        3025);
            i++;
        }

        if (fs.init() != null)
            fs.init().visit(this);
        if (fs.incr() != null)
            fs.incr().visit(this);
        if (fs.expr() != null) {
            Type eType = resolve(fs.expr().visit(this));

            if (!eType.isBooleanType())
                Error.error(fs,
                        "Non boolean Expression found in for-statement.",
                        false, 3026);
        }
        if (fs.stats() != null)
            fs.stats().visit(this);

        return null;
    }

    // Guard -- Nothing to do

    /** IF STATEMENT */
    // ERROR TYPE OK
    public Type visitIfStat(IfStat is) {
        Log.log(is.line + ": Visiting a if statement");

        Type eType = resolve(is.expr().visit(this));

        if (!eType.isBooleanType())
            Error.error(is,
                    "Non boolean Expression found as test in if-statement.",
                    false, 3027);
        if (is.thenpart() != null)
            is.thenpart().visit(this);
        if (is.elsepart() != null)
            is.elsepart().visit(this);

        return null;
    }

    // Import - nothing to do

    // Invocation
    public Type visitInvocation(Invocation in) {
        Log.log(in.line + ": visiting invocation (" + in.procedureName() + ")");

        in.params().visit(this);

        // TODO: this should be redone!!!
        boolean firstTable = true;
        SymbolTable st = topLevelDecls;
        Sequence<ProcTypeDecl> candidateProcs = new Sequence<ProcTypeDecl>();

        // transfer in.candidates to candidateProcs.
        /*	if (in.candidateMethods != null && false)
            for (Object pd : in.candidateMethods.entries.values().toArray()) {
        	ProcTypeDecl ptd = (ProcTypeDecl)pd;
        	if (ptd.formalParams().size() == in.params().size()) {
        	    boolean candidate = true;
        	    Log.log("proc: " + ptd.typeName() + " ( " + ptd.signature() + " ) ");                                                                                                            
        	    for (int i=0; i<in.params().size(); i++) {
        		candidate = candidate && Type.assignmentCompatible(((ParamDecl)ptd.formalParams().child(i)).type(), in.params().child(i).type);
        	    }
        	    if (candidate) {
        		candidateProcs.append(ptd);
        		Log.log("Possible proc: " + ptd.typeName() + " " + ptd.formalParams());
        	    }
        	}
            }
        */

        while (st != null) {

            SymbolTable procs = (SymbolTable) st.getShallow(in.procedureName()
                    .getname());
            if (procs != null)
                for (Object pd : procs.entries.values().toArray()) {
                    ProcTypeDecl ptd = (ProcTypeDecl) pd;

                    // set the qualified name in pd such that we can get at it later.

                    if (ptd.formalParams().size() == in.params().size()) {
                        // TODO: this should store this somwhere 
                        boolean candidate = true;
                        Log.log(" checking if Assignment Compatible proc: "
                                + ptd.typeName() + " ( " + ptd.signature()
                                + " ) ");
                        for (int i = 0; i < in.params().size(); i++) {
                            candidate = candidate
                                    && Type.assignmentCompatible(
                                            ((ParamDecl) ptd.formalParams()
                                                    .child(i)).type(), in
                                                    .params().child(i).type);
                        }
                        if (candidate) {
                            candidateProcs.append(ptd);
                            Log.log("Possible proc: " + ptd.typeName() + " "
                                    + ptd.formalParams());
                        }
                    }
                }

            if (firstTable)
                st = st.getImportParent();
            else
                st = st.getParent();
            firstTable = false;
        }

        Log.log("Found these candidates: ");
        Log.log("| " + candidateProcs.size() + " candidate(s) were found:");
        for (int i = 0; i < candidateProcs.size(); i++) {
            ProcTypeDecl pd = candidateProcs.child(i);
            Log.logNoNewline("|   " + in.procedureName().getname() + "(");
            Log.logNoNewline(pd.signature());
            Log.log(" )");
        }

        Log.log("" + candidateProcs.size());
        int noCandidates = candidateProcs.size();

        if (noCandidates == 0) {
            Error.error(in, "No suitable procedure found.", false, 3037);
            return null;
        } else if (noCandidates > 1) {
            // Iterate through the list of potential candidates	    
            for (int i = 0; i < candidateProcs.size(); i++) {
                // Take the i'th one out
                ProcTypeDecl ptd1 = candidateProcs.child(i);

                // Tf this proc has been removed - continue.
                if (ptd1 == null)
                    continue;
                // Temporarily  remove ptd from candidateprocs so we 
                // don't find it again in the next loop
                candidateProcs.set(i, null);

                // compare to all other candidates ptd2. 
                for (int j = 0; j < candidateProcs.size(); j++) {
                    ProcTypeDecl ptd2 = candidateProcs.child(j);
                    // if the proc was already removed - continue on
                    if (ptd2 == null)
                        continue;
                    //
                    boolean candidate = true;
                    // grab all the parameters of ptd1 and ptd2
                    Sequence<ParamDecl> ptd1Params = ptd1.formalParams();
                    Sequence<ParamDecl> ptd2Params = ptd2.formalParams();

                    // now check is ptd2[k] :> ptd1[k] for all k. If it does remove ptd2.                          
                    // check each parameter in turn
                    for (int k = 0; k < ptd1Params.nchildren; k++) {
                        candidate = candidate
                                && Type.assignmentCompatible(
                                        ((ParamDecl) ptd2Params.child(k))
                                                .type(),
                                        ((ParamDecl) ptd1Params.child(k))
                                                .type());

                        if (!candidate)
                            break;
                    }
                    if (candidate) {
                        // ptd1 is more specialized than ptd2, so throw ptd2 away.                                 
                        Log.logNoNewline("|   " + in.procedureName().getname()
                                + "(");
                        Log.logNoNewline(ptd2.signature());
                        Log.logNoNewline(" ) is less specialized than "
                                + in.procedureName().getname() + "(");
                        Log.logNoNewline(ptd1.signature());
                        Log.log(" ) and is thus thrown away!");
                        // Remove ptd2
                        candidateProcs.set(j, null);
                        noCandidates--;
                    }
                }
                // now put ptd1 back in to candidateProcs                                                               
                candidateProcs.set(i, ptd1);
            }
        }
        if (noCandidates != 1) {
            // we found more than one!
            Log.log("| " + candidateProcs.size() + " candidate(s) were found:");
            for (int i = 0; i < candidateProcs.size(); i++) {
                ProcTypeDecl pd = candidateProcs.child(i);
                if (pd != null) {
                    Log.logNoNewline("|   " + in.procedureName().getname()
                            + "(");
                    Log.logNoNewline(pd.signature());
                    Log.log(" )");
                }
            }
            Error.error(
                    in,
                    "Found more than one candidate - cannot chose between them!",
                    false, 3038);
            return null;
        } else {
            // we found just one!
            Log.log("| We were left with exactly one candidate to call!");
            Log.log("+------------- End of findMethod --------------");
            for (int i = 0; i < candidateProcs.size(); i++)
                if (candidateProcs.child(i) != null) {
                    in.targetProc = candidateProcs.child(i);
                    in.type = in.targetProc.returnType();
                }
        }
        Log.log("myPackage: " + in.targetProc.myPackage);
        Log.log(in.line + ": invocation has type: " + in.type);
        return in.type;

    }

    // LocalDecl
    // Modifier - nothing to do
    // Name - nothing to do

    /** NAMED TYPE */
    public Type visitNamedType(NamedType nt) {
        Log.log(nt.line + ": visiting a named type (" + nt.name().getname()
                + ").");
        // TODO: not sure how to handle error type here 
        if (nt.type() == null) {
            // go look up the type and set the type field of nt.
            Type t = (Type) topLevelDecls
                    .getIncludeImports(nt.name().getname());
            if (t == null) {
                // check if it was a external packaged type (i.e., something with ::)
                if (nt.name().resolvedPackageAccess != null) {
                    Log.log("FOUND IT. It was a package type accessed with ::");
                    t = (Type) nt.name().resolvedPackageAccess;
                    // TODO: the file should probably be inserted somewhere .....
                    // TODO: what about anything that types imported with :: refers to ? 
                    //       how should that be handled?
                } else
                    Error.error(nt, "Undefined named type '"
                            + nt.name().getname() + "'.", false, 3028);
            }
            nt.setType(t);
        }
        Log.log(nt.line + ": named type has type: " + nt.type());

        return nt.type();
    }

    /** NAME EXPRESSION */
    // ERROR TYPE OK
    public Type visitNameExpr(NameExpr ne) {
        Log.log(ne.line + ": Visiting a Name Expression ("
                + ne.name().getname() + ").");
        if (ne.myDecl instanceof LocalDecl || ne.myDecl instanceof ParamDecl
                || ne.myDecl instanceof ConstantDecl) {
            // TODO: what about ConstantDecls ???
            // TODO: don't think a resolve is needed here
            ne.type = resolve(((VarDecl) ne.myDecl).type());
        } else
            ne.type = Error.addError(ne, "Unknown name expression '"
                    + ne.name().getname() + "'.", 3029);

        Log.log(ne.line + ": Name Expression (" + ne.name().getname()
                + ") has type: " + ne.type);
        return ne.type;
    }

    public boolean arrayAssignmentCompatible(Type t, Expression e) {
        if (t instanceof ArrayType && e instanceof ArrayLiteral) {
            ArrayType at = (ArrayType) t;
            e.type = at; //  we don't know that this is the type - but if we make it through it will be!
            ArrayLiteral al = (ArrayLiteral) e;

            // t is an array type i.e. XXXXXX[ ]
            // e is an array literal, i.e., { }
            if (al.elements().size() == 0) // the array literal is { }
                return true; // any array variable can hold an empty array
            // Now check that XXXXXX can hold value of the elements of al
            // we have to make a new type: either the base type if |dims| = 1
            boolean b = true;
            for (int i = 0; i < al.elements().size(); i++) {
                if (at.getDepth() == 1)
                    b = b
                            && arrayAssignmentCompatible(at.baseType(),
                                    (Expression) al.elements().child(i));
                else {
                    ArrayType at1 = new ArrayType(at.baseType(),
                            at.getDepth() - 1);
                    b = b
                            && arrayAssignmentCompatible(at1, (Expression) al
                                    .elements().child(i));
                }
            }
            return b;
        } else if (t instanceof ArrayType && !(e instanceof ArrayLiteral))
            Error.error(t,
                    "Cannot assign non array to array type `" + t.typeName()
                            + "'", false, 3039);
        else if (!(t instanceof ArrayType) && (e instanceof ArrayLiteral))
            Error.error(t,
                    "Cannot assign value `" + ((ArrayLiteral) e).toString()
                            + "' to type `" + t.typeName() + "'.", false, 3030);
        return Type.assignmentCompatible(t, e.visit(this));
    }

    /** NewArray */
    // ERROR TYPE OK
    public Type visitNewArray(NewArray ne) {
        Log.log(ne.line + ": Visiting a NewArray " + ne.dimsExpr().size() + " "
                + ne.dims().size());

        //  check that each dimension is of integer type
        for (Expression exp : ne.dimsExpr()) {
            Type dimT = resolve(exp.visit(this));
            if (!dimT.isIntegralType())
                Error.error(exp, "Array dimension must be of integral type",
                        false, 3031);
        }
        // if there is an initializer, then make sure it is of proper and equal depth.
        ne.type = new ArrayType(ne.baseType(), ne.dims().size()
                + ne.dimsExpr().size());
        if (ne.init() != null) {
            // The elements of ne.init() get visited in the last line of arrayAssignmentCompatible.
            if (!arrayAssignmentCompatible(ne.type, ne.init()))
                Error.error(ne, "Array Initializer is not compatible with "
                        + ne.type.typeName(), false, 3032);
            ne.init().type = ne.type;
        }
        Log.log(ne.line + ": NewArray type is " + ne.type);
        return ne.type;
    }

    // NewMobile
    // TODO: you can only 'new' stuff that is a mobile -- perhaps this check from the NameChecker should be here!

    // ParamDecl - nothing to do

    // TODO: Cannot be whole channels.

    // ParBlock - nothing to do
    // Pragma - nothing to do

    /** PRIMITIVE LITERAL */
    public Type visitPrimitiveLiteral(PrimitiveLiteral pl) {
        Log.log(pl.line + ": Visiting a primitive literal (" + pl.getText()
                + ").");

        // Remember that the constants in PrimitiveType are defined from the ones                                                      
        // in Literal, so its it ok to just use li.kind! -- except for the null literal.                                               

        //	if (pl.getKind() == PrimitiveLiteral.NullKind)
        //    pl.type = null; // new NullType(li); TODO: Perhaps we need a null type and a null value too ??
        //else {
        Log.log("Setting Primitive Literal Type");
        pl.type = new PrimitiveType(pl.getKind());
        //}

        Log.log(pl.line + ": Primitive literal has type: " + pl.type);
        return pl.type;
    }

    /** PRIMITIVE TYPE */
    // ERROR TYPE OK
    public Type visitPrimitiveType(PrimitiveType pt) {
        Log.log(pt.line + ": Visiting a primitive type.");
        Log.log(pt.line + ": Primitive type has type: " + pt);
        return pt;
    }

    /** PROTOCOL TYPE DELCARATION */
    // ERROR TYPE OK
    public Type visitProcTypeDecl(ProcTypeDecl pd) {
        Log.log(pd.line + ": visiting a procedure type declaration ("
                + pd.name().getname() + ").");
        currentProcedure = pd;
        super.visitProcTypeDecl(pd);
        return null;
    }

    /** PROTOCOL LITERAL */
    public Type visitProtocolLiteral(ProtocolLiteral pl) {
        Log.log(pl.line + ": Visiting a protocol literal");

        // tag already checked in NameChecker

        // TODO: below code is incorrect as it does not take 'extends' into account

        // Name{ tag: exp_1, exp_2, ... ,exp_n }
        ProtocolCase pc = pl.myChosenCase;
        ProtocolTypeDecl pd = pl.myTypeDecl;
        if (pc.body().size() != pl.expressions().size())
            Error.error(pl,
                    "Incorrect number of expressions in protocol literal '"
                            + pd.name().getname() + "'", false, 3033);
        for (int i = 0; i < pc.body().size(); i++) {
            Type eType = resolve(pl.expressions().child(i).visit(this));
            Type vType = resolve(((RecordMember) pc.body().child(i)).type());
            Name name = ((RecordMember) pc.body().child(i)).name();
            if (!Type.assignmentCompatible(vType, eType))
                Error.error(pl, "Cannot assign value of type '" + eType
                        + "' to protocol field '" + name.getname()
                        + "' of type '" + vType + "'.", false, 3034);
        }
        Log.log(pl.line + ": protocol literal has type: " + pl.myTypeDecl);
        return pl.myTypeDecl;
    }

    // ProtocolCase

    /** PROTOCOL TYPE DECLARATION */
    // ERROR TYPE OK
    public Type visitProtocolTypeDecl(ProtocolTypeDecl pt) {
        Log.log(pt.line + ": Visiting a protocol type decl.");
        Log.log(pt.line + ": Protocol type decl has type: " + pt);
        return pt;
    }

    /** RECORD ACCESS */
    // ERROR TYPE OK
    public Type visitRecordAccess(RecordAccess ra) {
        Log.log(ra.line + ": visiting a record access expression ("
                + ra.field().getname() + ")");
        Type tType = resolve(ra.record().visit(this));
        tType = tType.visit(this);

        // TODO: size of strings.... size()? size? or length?   for now: size() -> see visitInvocation

        // Array lengths can be accessed through a length 'field'
        if (tType.isArrayType() && ra.field().getname().equals("size")) {
            ra.type = new PrimitiveType(PrimitiveType.IntKind);
            ra.isArraySize = true;
            Log.log(ra.line + ": Array size expression has type: " + ra.type);
            return ra.type;
        }
        if (tType.isStringType() && ra.field().getname().equals("length")) {
            ra.type = new PrimitiveType(PrimitiveType.IntKind); // TODO: should this be long ???
            ra.isStringLength = true;
            Log.log(ra.line + ": string length expression has type: " + ra.type);
            return ra.type;
        } else {
            if (!(tType.isRecordType() || tType.isProtocolType())) {
                ra.type = Error.addError(ra, "Request for member '"
                        + ra.field().getname()
                        + "' in something not a record orprotocol type.", 3061);
                return ra.type;
            }

            // tType can be a record and it can be a protocol:

            if (tType.isRecordType()) {
                // Now find the field and make the type of the record access equal to the field.
                RecordMember rm = ((RecordTypeDecl) tType).getMember(ra.field()
                        .getname());
                if (rm == null) {
                    ra.type = Error.addError(ra,
                            "Record type '"
                                    + ((RecordTypeDecl) tType).name().getname()
                                    + "' has no member '"
                                    + ra.field().getname() + "'.", 3062);
                    return ra.type;
                }
                Type rmt = resolve(rm.type().visit(this));
                ra.type = rmt;
            } else {
                // Protocol Type

                /* switch statements cannot be nested on the same protocol!!!
                   keep a hashtable of the protocols we have switched on - and their tags!
                   
                   | protocol Name | -> ProtocolCase
                   
                   we have <expr>.<field> that means a field <field> in the tag associaged with the type of <expr>!
                */

                ProtocolTypeDecl pt = (ProtocolTypeDecl) tType;
                // Lookup the appropriate ProtocolCase associated with the protocol's name in
                // protocolTagsSwitchedOn
                ProtocolCase pc = protocolTagsSwitchedOn.get(pt.name()
                        .getname());
                String fieldName = ra.field().getname();
                // there better be a field in pc that has that name!
                boolean found = false;
                for (RecordMember rm : pc.body()) {
                    Log.log("Looking at field " + rm.name().getname());
                    if (rm.name().getname().equals(fieldName)) {
                        // yep we found it; now set the type
                        Type rmt = resolve(rm.type().visit(this));
                        ra.type = rmt;
                        found = true;
                        break;
                    }
                }
                if (!found) {
                    Error.error(ra, "Unknown field reference '" + fieldName
                            + "' in protocol tag '" + pc.name().getname()
                            + "' in protocol '" + pt.name().getname() + "'.",
                            false, 0000);
                    ra.type = new ErrorType();
                }
            }
        }
        Log.log(ra.line + ": record access expression has type: " + ra.type);
        return ra.type;
    }

    /** RECORD LITERAL */
    public Type visitRecordLiteral(RecordLiteral rl) {
        Log.log(rl.line + ": visiting a record literal (" + rl.name().getname()
                + ").");
        RecordTypeDecl rt = rl.myTypeDecl;

        // TODO: be careful here if a record type extends another record type, then the record literal must contains 
        // expressions for that part too!!!

        return rt;
    }

    // RecordMember

    /** RECORD TYPE DECLARATION */
    // ERROR TYPE OK
    public Type visitRecordTypeDecl(RecordTypeDecl rt) {
        Log.log(rt.line + ": Visiting a record type decl.");

        Log.log(rt.line + ": Record type decl has type: " + rt);
        return rt;
    }

    /** RETURN STATEMENT */
    // ERROR TYPE OK
    public Type visitReturnStat(ReturnStat rs) {
        Log.log(rs.line + ": visiting a return statement");

        Type returnType = resolve(currentProcedure.returnType());

        // check if the return type is void; if it is rs.expr() should be null.
        // check if the return type is not voidl if it is not rs.expr() should not be null.
        if (returnType instanceof PrimitiveType) {
            PrimitiveType pt = (PrimitiveType) returnType;
            if (pt.isVoidType() && rs.expr() != null)
                Error.error(
                        rs,
                        "Procedure return type is void; return statement cannot return a value.",
                        false, 3040);
            if (!pt.isVoidType() && rs.expr() == null)
                Error.error(rs, "Procedure return type is `" + pt
                        + "' but procedure return type is void.", false, 3041);
            if (pt.isVoidType() && rs.expr() == null)
                return null;
        }

        Type eType = resolve(rs.expr().visit(this));
        if (!Type.assignmentCompatible(returnType, eType))
            Error.error(rs, "Incompatible type in return statement.", false,
                    3042);

        return null;
    }

    // Sequence - nothing to do
    // SkipStat - nothing to do
    // StopStat -- nothing to do

    /** SUSPEND STATEMENT */
    // ERROR TYPE OK
    public Type visitSuspendStat(SuspendStat ss) {
        Log.log(ss.line + ": Visiting a suspend stat.");
        if (!Modifier.hasModifierSet(currentProcedure.modifiers(),
                Modifier.MOBILE))
            Error.error(ss, "Non-mobile procedure cannot suspend.", false, 3043);
        return null;
    }

    // SwitchGroup -- nothing to do - handled in SwitchStat
    // SwitchLabel -- nothing to do - handled in SwitchStat

    ProtocolCase findProtocolCase(ProtocolTypeDecl pt, String switchLabelName) {
        Log.log("fpc" + pt);
        if (pt.body() != null)
            for (ProtocolCase pc : pt.body()) {
                String name = pc.name().getname();
                if (name.equals(switchLabelName))
                    return pc;
            }

        for (int i = 0; i < pt.extend().size(); i++) {
            ProtocolTypeDecl pdt = (ProtocolTypeDecl) ((Name) pt.extend()
                    .child(i)).myDecl;
            ProtocolCase r;
            r = findProtocolCase(pdt, switchLabelName);
            if (r != null)
                return r;
        }
        return null;
    }

    /** SWITCH STATEMENT */
    public Type visitSwitchStat(SwitchStat ss) {
        Log.log(ss.line + ": Visiting a Switch statement");

        boolean inSwitchOld = inSwitch;
        inSwitch = true;

        // TODO: RecordAccess that accesses a ProtocolType should 
        // only be allowed when switching on the tag!

        int i, j;
        Type lType;
        Type eType = resolve(ss.expr().visit(this));
        Set<String> ht = new HashSet<String>();
        // TODO: RecordAccessExpr of protocols is OK!
        if ((!eType.isIntegralType() && !eType.isProtocolType())
                || eType.isLongType())
            Error.error(
                    ss,
                    "Switch statement expects value of type int or a protocol tag.",
                    false, 3043);

        // check if we have alrady switched on a protocol of a similar name - if not add it (after we have the appropriate case.
        if (eType.isProtocolType()) {
            ProtocolTypeDecl pt = (ProtocolTypeDecl) eType;
            if (protocolTagsSwitchedOn.containsKey(pt.name().getname()))
                Error.error(
                        ss.expr(),
                        "Nested switch statements on the same protocol type is not allowed.",
                        false, 0000);
        }

        ProtocolTypeDecl pt = null;
        for (SwitchGroup sg : ss.switchBlocks()) {
            // Only one protocol tag per case, so only one label per switch group. (no fall through)
            if (eType.isProtocolType() && sg.labels().size() > 1)
                Error.error(
                        sg,
                        "Fall-through cases in protocol switch statement not allowed.",
                        false, 0);
            for (SwitchLabel sl : sg.labels()) {
                // No default case in protocol switches 
                if (sl.isDefault()) {
                    if (eType.isProtocolType())
                        Error.error(sl.expr(),
                                "Default case not allowed in protocol switch.",
                                false, 0000);
                    else
                        continue;
                }

                // TODO: protocol switches should not allow fall through!!!

                lType = null;
                if (!(sl.expr() instanceof NameExpr))
                    lType = resolve(sl.expr().visit(this));

                if ((!(sl.expr() instanceof NameExpr))
                        && (!lType.isIntegralType() || lType.isLongType()))
                    Error.error(
                            sl,
                            "Switch labels must be of type int or a protocol tag.",
                            false, 3044);
                else if (sl.expr() instanceof NameExpr
                        && !eType.isProtocolType())
                    Error.error(sl.expr(),
                            "Switch label must be of integer type.", false,
                            0000);
                else if ((!(sl.expr() instanceof NameExpr))
                        && eType.isProtocolType())
                    Error.error(sl.expr(),
                            "Switch label must be a protocol case name.",
                            false, 0000);
                else if ((!(sl.expr() instanceof NameExpr))
                        && !sl.expr().isConstant())
                    Error.error(sl, "Switch labels must be constants.", false,
                            3045);
                else {
                    if (sl.expr() instanceof NameExpr) {
                        pt = (ProtocolTypeDecl) eType;
                        ProtocolCase pc = findProtocolCase(pt,
                                ((NameExpr) sl.expr()).name().getname());
                        if (pc == null)
                            Error.error(sl, "Protocol tag '"
                                    + sl.expr()
                                    + "' not found in protocol '"
                                    + ((ProtocolTypeDecl) eType).name()
                                            .getname() + "'.", false, 3060);
                        else
                            protocolTagsSwitchedOn.put(pt.name().getname(), pc);
                    }
                }

            }
            sg.statements().visit(this);
            // No fall through in switch groups for protocol types
            if (eType.isProtocolType() && sg.statements().size() > 0)
                if (!(sg.statements().child(sg.statements().size() - 1) instanceof BreakStat))
                    Error.error(
                            sg,
                            "Fall-through cases in protocol switch statement not allowed.",
                            false, 0);
            if (eType.isProtocolType())
                protocolTagsSwitchedOn.remove(pt.name().getname());

        }

        for (SwitchGroup sg : ss.switchBlocks()) {
            for (SwitchLabel sl : sg.labels()) {
                if (sl.isDefault()) {
                    if (ht.contains("default"))
                        Error.error(sl, "Duplicate default label.", false, 3046);
                    else
                        ht.add("default");
                    continue;
                }
                String strval;
                if (!(sl.expr() instanceof NameExpr)) {
                    int val = ((BigDecimal) sl.expr().constantValue())
                            .intValue();
                    strval = Integer.toString(val);
                } else
                    strval = ((NameExpr) sl.expr()).name().getname();
                if (ht.contains(strval))
                    Error.error(sl, "Duplicate case label.", false, 3047);
                else {
                    ht.add(strval);
                }
            }
        }
        inSwitch = inSwitchOld;
        if (eType.isProtocolType()) {
            // remove the protocl name from the hash table of protocols we are switching on currently.
            protocolTagsSwitchedOn.remove(pt.name().getname());
        }
        return null;
    }

    /** SYNC STAT */
    //ERROR TYPE OK
    public Type visitSyncStat(SyncStat ss) {
        Log.log(ss.line + ": visiting a sync stat.");
        Type t = resolve(ss.barrier().visit(this));
        if (!t.isBarrierType())
            Error.error(ss, "Non-barrier type in sync statement.", false, 3048);
        return null;
    }

    // Ternary
    public Type visitTernary(Ternary te) {
        Log.log(te.line + ": Visiting a ternary expression");

        Type eType = resolve(te.expr().visit(this));
        Type trueBranchType = te.trueBranch().visit(this);
        Type falseBranchType = te.falseBranch().visit(this);

        if (!eType.isBooleanType())
            Error.error(te,
                    "Non boolean Expression found as test in ternary expression.");

        if (trueBranchType instanceof PrimitiveType
                && falseBranchType instanceof PrimitiveType) {
            if (Type.assignmentCompatible(falseBranchType, trueBranchType)
                    || Type.assignmentCompatible(trueBranchType,
                            falseBranchType))
                te.type = PrimitiveType.ceilingType(
                        (PrimitiveType) trueBranchType,
                        (PrimitiveType) falseBranchType);
            else
                Error.error(te,
                        "Both branches of a ternary expression must be of assignment compatible types.");
        } else if (te == null) { // te is never null! just to fool Eclipse
            //TODO: What about assignments of protocol and records wrt to their inheritance and procedures? 			
        } else
            Error.error(te,
                    "Both branches of a ternary expression must be of assignment compatible types.");

        Log.log(te.line + ": Ternary has type: " + te.type);
        return te.type;
    }

    /** TIMEOUT STATEMENT */
    // ERROR TYPE OK
    public Type visitTimeoutStat(TimeoutStat ts) {
        Log.log(ts.line + ": visiting a timeout statement.");
        Type dType = resolve(ts.delay().visit(this));
        if (!dType.isIntegralType())
            Error.error(
                    ts,
                    "Invalid type in timeout statement, integral type required.",
                    false, 3049);
        Type eType = resolve(ts.timer().visit(this));
        if (!eType.isTimerType())
            Error.error(ts, "Timer type required in timeout statement.", false,
                    3050);
        return null;
    }

    /** UNARY POST EXPRESSION */
    // ERROR TYPE OK
    public Type visitUnaryPostExpr(UnaryPostExpr up) {
        Log.log(up.line + ": Visiting a unary post expression");
        up.type = null;
        Type eType = resolve(up.expr().visit(this));

        // TODO: what about protocol ??
        if (up.expr() instanceof NameExpr || up.expr() instanceof RecordAccess
                || up.expr() instanceof ArrayAccessExpr) {
            if (!eType.isIntegralType() && !eType.isDoubleType()
                    && !eType.isFloatType())
                up.type = Error.addError(up,
                        "Cannot apply operator '" + up.opString()
                                + "' to something of type " + eType.typeName()
                                + ".", 3051);
        } else
            up.type = Error.addError(up, "Variable expected, found value.",
                    3055);

        if (up.type == null)
            up.type = eType;

        Log.log(up.line + ": Unary Post Expression has type: " + up.type);
        return up.type;
    }

    /** UnaryPreExpr */
    // ERROR TYPE OK
    public Type visitUnaryPreExpr(UnaryPreExpr up) {
        Log.log(up.line + ": Visiting a unary pre expression");
        up.type = null;
        Type eType = resolve(up.expr().visit(this));

        switch (up.op()) {
            case UnaryPreExpr.PLUS:
            case UnaryPreExpr.MINUS:
                if (!eType.isNumericType())
                    up.type = Error.addError(
                            up,
                            "Cannot apply operator '" + up.opString()
                                    + "' to something of type "
                                    + eType.typeName() + ".", 3052);
                break;
            case UnaryPreExpr.NOT:
                if (!eType.isBooleanType())
                    up.type = Error.addError(up,
                            "Cannot apply operator '!' to something of type "
                                    + eType.typeName() + ".", 3053);
                break;
            case UnaryPreExpr.COMP:
                if (!eType.isIntegralType())
                    up.type = Error.addError(up,
                            "Cannot apply operator '~' to something of type "
                                    + eType.typeName() + ".", 3054);
                break;
            case UnaryPreExpr.PLUSPLUS:
            case UnaryPreExpr.MINUSMINUS:
                if (!(up.expr() instanceof NameExpr)
                        && !(up.expr() instanceof RecordAccess)
                        && !(up.expr() instanceof ArrayAccessExpr))
                    up.type = Error.addError(up,
                            "Variable expected, found value.", 3057);

                if (!eType.isNumericType() && up.type == null)
                    up.type = Error.addError(
                            up,
                            "Cannot apply operator '" + up.opString()
                                    + "' to something of type "
                                    + eType.typeName() + ".", 3056);
                break;
        }
        if (up.type == null)
            up.type = eType;
        Log.log(up.line + ": Unary Pre Expression has type: " + up.type);
        return up.type;
    }

    /** VAR */
    // ERROR TYPE OK
    public Type visitVar(Var va) {
        Log.log(va.line + ": Visiting a var (" + va.name().getname() + ").");

        if (va.init() != null) {
            Type vType = resolve(va.myDecl.type());
            Type iType = resolve(va.init().visit(this));

            if (vType.isErrorType() || iType.isErrorType())
                return null;

            if (!Type.assignmentCompatible(vType, iType))
                Error.error(va,
                        "Cannot assign value of type " + iType.typeName()
                                + " to variable of type " + vType.typeName()
                                + ".", false, 3058);
        }
        return null;
    }

    /** WHILE STATEMENT */
    public Type visitWhileStat(WhileStat ws) {
        Log.log(ws.line + ": Visiting a while statement");
        Type eType = resolve(ws.expr().visit(this));

        if (!eType.isBooleanType())
            Error.error(ws,
                    "Non boolean Expression found as test in while-statement.",
                    false, 3059);
        if (ws.stat() != null)
            ws.stat().visit(this);
        return null;
    }
}
