/**
 * 
 */
package TypeChecker;

import AST.*;
import Utilities.Log;
import Utilities.Visitor;
import Utilities.Error;
import Utilities.SymbolTable;
import java.util.Set;
import java.util.HashSet;
import java.math.BigDecimal;


/**
 * @author matt
 *
 */
public class TypeChecker extends Visitor<Type> {
	private ProcTypeDecl currentProcedure = null;
	SymbolTable topLevelDecls = null;
	
	public TypeChecker(SymbolTable topLevelDecls) {
        debug = true;
		this.topLevelDecls = topLevelDecls;
    	println("======================================");
    	println("*       T Y P E   C H E C K E R      *");
    	println("======================================");

	}

	// All visit calls must call resolve	
	public Type resolve(Type t) {
		System.out.println("  > Resolve: " + t);

		
			
		
		if (t.isErrorType())
			return t;
		if (t.isNamedType()) {
			println("  > Resolving named type: " + ((NamedType)t).name().getname());
			Type tt =  t.visit(this); // this resolves all NamedTypes
			println("  > Resolved " + ((NamedType)t).name().getname() + " -> " + tt.typeName());
			return tt;
		} else {
            println("  > Nothing to resolve - type remains: " + t);
			return t;
        }
	}
	
	
	/** ALT CASE */ // ERROR TYPE OK
	public Type visitAltCase(AltCase ac) {
		println(ac.line + ": Visiting an alt case.");
		
		// Check the pre-condition is there is one.
		if (ac.precondition() != null) {
			Type t = ac.precondition().visit(this);
			t = resolve(t.visit(this));
			if (!t.isBooleanType())
				Error.error(ac.precondition(), "Boolean type expected in precondition of alt case, found: " + t);
		}
		ac.guard().visit(this);
		ac.stat().visit(this);
		return null;
	}
	
    // AltStat
	
	/** ArrayAccessExpr */ // ERROR TYPE OK
	public Type visitArrayAccessExpr(ArrayAccessExpr ae) {
		println(ae.line + ": Visiting ArrayAccessExpr");
		Type t = resolve(ae.target().visit(this));
		if (!t.isArrayType()) {
			Error.error(ae,"Array type required, but found type " + t.typeName() + ".", false, 3000);
			ae.type = new ErrorType();
		} else {
			ArrayType at = (ArrayType)t;

			if (at.getDepth() == 1)
				ae.type = at.baseType();
			else
				ae.type = new ArrayType(at.baseType(), at.getDepth()-1);
			println(ae.line + ": ArrayAccessExpr has type " + ae.type);
			Type indexType = resolve(ae.index().visit(this));
			// This error does not create an error type cause the baseType is still the 
			// array expression's type
			if (!indexType.isIntegerType()) 
				Error.error(ae,"Array access index must be of integral type.", false, 3001);
		}
		return ae.type;
	}

	
    /** ARRAY LITERAL */ // ERROR TYPE OK
	public Type visitArrayLiteral(ArrayLiteral al) {
		println(al.line + ": visiting an array literal.");
		Error.error(al,"Array literal with the keyword 'new'.", false, 3002);
		return null;
	}

	/** ArrayType */ // ERROR TYPE OK
	public Type visitArrayType(ArrayType at) {
		println(at.line + ": Visiting an ArrayType");
		println(at.line + ": ArrayType type is " + at);
		return at;
	}

	/** ASSIGNMENT */ // ERROR TYPE OK
	public Type visitAssignment(Assignment as) {
		println(as.line + ": Visiting an assignment");

		as.type = null; // gets set to ErrorType if an error happens.
		
		Type vType =  resolve(as.left().visit(this));
		Type eType =  resolve(as.right().visit(this));
		
		// Handle error types in operands
		if (vType.isErrorType() || eType.isErrorType()) {
			as.type = new ErrorType();
			return as.type;
		}
		
		as.right().type = eType; // TODO: wouldn't this have been set in the visit call already?
				
		/** Note: as.left() should be of NameExpr or RecordAccess or ArrayAccessExpr class! */

		if (!vType.assignable())          
			Error.error(as,"Left hand side of assignment not assignable.");
		
		switch (as.op()) {
		case Assignment.EQ : {
			if (!Type.assignmentCompatible(vType,eType))
				Error.error(as,"Cannot assign value of type " + eType.typeName() + " to variable of type " + vType.typeName() + ".", false, 3003);
			break;
		}
		case Assignment.MULTEQ :
		case Assignment.DIVEQ : 
		case Assignment.MODEQ : 
		case Assignment.PLUSEQ :
		case Assignment.MINUSEQ : 
			if (!Type.assignmentCompatible(vType,eType))
				as.type = Error.addError(as,"Cannot assign value of type " + eType.typeName() + " to variable of type " + vType.typeName() + ".", 3004);
			if (!eType.isNumericType())
				as.type = Error.addError(as,"Right hand side operand of operator '" + as.opString() + "' must be of numeric type.", 3005);
			// No can do !
			if (!vType.isNumericType())
				as.type = Error.addError(as,"Left hand side operand of operator '" + as.opString() + "' must be of numeric type.", 3006);
			break;
		case Assignment.LSHIFTEQ :
		case Assignment.RSHIFTEQ :
		case Assignment.RRSHIFTEQ :
			if (!vType.isIntegralType())
				as.type = Error.addError(as,"Left hand side operand of operator '" + as.opString() + "' must be of integer type.", 3007);
			if (!eType.isIntegralType())
				as.type = Error.addError(as,"Right hand side operand of operator '" + as.opString() + "' must be of integer type.", 3008);
			break;
		case Assignment.ANDEQ : 
		case Assignment.OREQ : 
		case Assignment.XOREQ :
			if (!((vType.isIntegralType() && eType.isIntegralType()) ||	(vType.isBooleanType() && eType.isBooleanType())))
				as.type = Error.addError(as,"Both right and left hand side operands of operator '" +	as.opString() + "' must be either of boolean or integer type.", 3009);
			break;
		}
		if (as.type == null)
			as.type = vType;
		println(as.line + ": Assignment has type: " + as.type);

		return vType;
	}
	
	/** BINARY EXPRESSION */ // ERROR TYPE OK
	public Type visitBinaryExpr(BinaryExpr be) {
		println(be.line + ": Visiting a Binary Expression");

		Type lType =  resolve(be.left().visit(this));
		Type rType =  resolve(be.right().visit(this));
		String op = be.opString();

		// Handle errors from type checking operands
		if (lType.isErrorType() || rType.isErrorType()) {
			be.type = new ErrorType();
			println(be.line + ": Binary Expression has type: " + be.type);
			return be.type;
		}
		
		switch(be.op()) {
		// < > <= >= : Type can be Integer only.        
		case BinaryExpr.LT:
		case BinaryExpr.GT:
		case BinaryExpr.LTEQ:
		case BinaryExpr.GTEQ: {
			if (lType.isNumericType() && rType.isNumericType()) {
				be.type = new PrimitiveType(PrimitiveType.BooleanKind); 
			} else
				be.type = Error.addError(be,"Operator '" + op + "' requires operands of numeric type.", 3010);
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
					be.type = Error.addError(be,"Void type cannot be used here.", 3011);
				else 
					be.type = new PrimitiveType(PrimitiveType.BooleanKind);
			else if (lType.isNumericType() && rType.isNumericType()) 
				be.type = new PrimitiveType(PrimitiveType.BooleanKind);
			else
				be.type = Error.addError(be,"Operator '" + op + "' requires operands of the same type.", 3012);
			break;
		}
		// && || : Type can be Boolean only.
		case BinaryExpr.ANDAND:
		case BinaryExpr.OROR: {
			if (lType.isBooleanType() && rType.isBooleanType()) 
				be.type = lType;
			else
				be.type = Error.addError(be,"Operator '" + op + "' requires operands of boolean type.", 3013); 
			break;
		}
		// & | ^ : Type can be Boolean or Integral
		case BinaryExpr.AND:
		case BinaryExpr.OR:
		case BinaryExpr.XOR: {
			if ((lType.isBooleanType() && rType.isBooleanType()) ||	(lType.isIntegralType() && rType.isIntegralType())) {          
				be.type = PrimitiveType.ceilingType((PrimitiveType)lType, (PrimitiveType)rType);;
                // TODO: don't do this anywhere!!
				// promote byte, short and char to int
				if (be.type.isByteType() || be.type.isShortType() || be.type.isCharType())
					be.type = new PrimitiveType(PrimitiveType.IntKind);

			} else
				be.type = Error.addError(be,"Operator '" + op + "' requires both operands of either integral or boolean type.", 3014);
			break;
		}
		// + - * / % : Type must be numeric
		case BinaryExpr.PLUS:
		case BinaryExpr.MINUS:
		case BinaryExpr.MULT:
		case BinaryExpr.DIV:
		case BinaryExpr.MOD: {
			if (lType.isNumericType() && rType.isNumericType()) {
				be.type = new PrimitiveType(PrimitiveType.ceiling((PrimitiveType)lType, (PrimitiveType)rType));

				if (be.type.isByteType() || be.type.isShortType() || be.type.isCharType())
					be.type = new PrimitiveType(PrimitiveType.IntKind);
			}
			else
				be.type = Error.addError(be,"Operator '" + op + "' requires operands of numeric type.", 3015);
			break;
		}
		// << >> >>>: 
		case BinaryExpr.LSHIFT:
		case BinaryExpr.RSHIFT:
		case BinaryExpr.RRSHIFT: {
			if (!lType.isIntegralType())
				be.type = Error.addError(be,"Operator '" + op + "' requires left operand of integral type.", 3016);
			if (!rType.isIntegralType())
				be.type = Error.addError(be,"Operator '" + op + "' requires right operand of integral type.", 3017);
			be.type = lType;
			break;
		}
		default: be.type = Error.addError(be,"Unknown operator '" + op + "'.", 3018);
		}   
		println(be.line + ": Binary Expression has type: " + be.type);
		return be.type;
	}
	
    // Block - nothing to do
	
    // BreakStat
	
	// CAST EXPRESSION // ERROR TYPE OK
	public Type visitCastExpr(CastExpr ce) {
		println(ce.line + ": Visiting a cast expression");

		Type exprType = resolve(ce.expr().visit(this));
		Type castType = resolve(ce.type()); // Not sure the 'resolve' is needed here

		// Handle errors here
		if (exprType.isErrorType() || castType.isErrorType()) {
			ce.type = new ErrorType();
			return ce.type;
		}
		
		if (exprType.isNumericType() && castType.isNumericType()) {
			ce.type = castType;
			println(ce.line + ": Cast Expression has type: " + ce.type);
			return castType;
		}
        // Turns out that casts like this are illegal:
        // int a[][];
        // double b[][];
        // a = (int[][])b;
        // b = (double[][])a;

        // BUT record can be cast and probably protocols too!
        if (castType.isRecordType() || castType.isProtocolType())
            System.out.println("TODO: TypeChecker.visitCastExpr(): no implementation for protocol and record types.");
        ce.type = castType;

		// TODO: other types here!	

		println(ce.line + ": Cast Expression has type: " + ce.type);
		return ce.type;
	}
	
    /** CHANNEL TYPE */ // ERROR TYPE OK
	public Type visitChannelType(ChannelType ct) {
		println(ct.line + ": Visiting a channel type.");
		println(ct.line + ": Channel type has type: " + ct);
		return ct;
	}
	
    /** CHANNEL END EXPRESSION */ // ERROR TYPE OK
	public Type visitChannelEndExpr(ChannelEndExpr ce) {
		println(ce.line + ": Visiting a channel end expression.");
		Type t = resolve(ce.channel().visit(this));
		
		// Handle error types
		if (t.isErrorType()) {
			ce.type = t;
			return ce.type;
		}
		
		if (!t.isChannelType()) {
			ce.type = Error.addError(ce, "Channel end expression requires channel type.", 3019);
			return ce.type;
		}
			
		ChannelType ct = (ChannelType)t;
		int end = (ce.isRead() ? ChannelEndType.READ_END : ChannelEndType.WRITE_END);
		if (ct.shared() == ChannelType.NOT_SHARED)
			ce.type = new ChannelEndType(ChannelEndType.NOT_SHARED, ct.baseType(), end);
		else if (ct.shared() == ChannelType.SHARED_READ_WRITE)
			ce.type = new ChannelEndType(ChannelEndType.SHARED,     ct.baseType(), end);
		else if (ct.shared() == ChannelType.SHARED_READ)
			ce.type = new ChannelEndType((ce.isRead() && ct.shared() == ChannelType.SHARED_READ) ? 
					ChannelEndType.SHARED : ChannelType.NOT_SHARED, ct.baseType(), end);
		else if (ct.shared() == ChannelType.SHARED_WRITE)
			ce.type = new ChannelEndType((ce.isWrite() && ct.shared() == ChannelType.SHARED_WRITE) ? 
					ChannelEndType.SHARED : ChannelType.NOT_SHARED, ct.baseType(), end);
		else
			ce.type = Error.addError(ce,"Unknown sharing status for channel end expression.", 3020);
		println(ce.line + ": Channel End Expr has type: " + ce.type);
		return ce.type;
	}
	
	/** CHANNEL END TYPE */ // ERROR TYPE OK
	public Type visitChannelEndType(ChannelEndType ct) {
		println(ct.line + ": Visiting a channel end type.");
		println(ct.line + ": Channel end type " + ct);
		return ct;
	}
	
    /** CHANNEL READ EXPRESSION */ // ERROR TYPE OK
	public Type visitChannelReadExpr(ChannelReadExpr cr) {
		println(cr.line + ": Visiting a channel read expression.");
		
		// TODO: targetType MAY be a channelType:
		// chan<int> c;
		// c.read();
		
		Type targetType = resolve(cr.channel().visit(this));
		if (!(targetType.isChannelEndType() || targetType.isTimerType() || targetType.isChannelType())) {
			cr.type = Error.addError(cr,"Channel or Timer type required in channel/timer read.", 3021);
			return cr.type;
		}
		if (targetType.isChannelEndType()) {
			 ChannelEndType cet = (ChannelEndType)targetType;
			 cr.type = cet.baseType();
		} else {
			// must be a tiemr type, and timer read() returns values of type long.
			cr.type = new PrimitiveType(PrimitiveType.LongKind);
		}
		if (targetType.isTimerType() && cr.extRV() != null)
			Error.addError(cr,"Timer read cannot have extended rendez-vous block.", 3022);
		
		if (cr.extRV() != null)
			cr.extRV().visit(this);
		println(cr.line + ": Channel read expression has type: " + cr.type);
		return cr.type;
	}
	
    /** CHANNEL WRITE STATEMENT */ // ERROR TYPE OK
	public Type visitChannelWriteStat(ChannelWriteStat cw) {
		println(cw.line + ": Visiting a channel write stat.");
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
	
	/** DO STATEMENT */ //ERROR TYPE OK - I THINK
	public Type visitDoStat(DoStat ds) {
		println(ds.line + ": Visiting a do statement");

		// Compute the type of the expression
		Type eType =  resolve(ds.expr().visit(this));
		
		// Check that the type of the expression is a boolean
		if (!eType.isBooleanType())
			Error.error(ds, "Non boolean Expression found as test in do-statement.", false, 3024);

		// Type check the statement of the do statement;
		if (ds.stat() != null)
			ds.stat().visit(this);
		
		return null;
	}

    // ExprStat - nothing to do
	
    // ExternType 
	
	/** FOR STATEMENT */ // ERROR TYPE OK
	public Type visitForStat(ForStat fs) {
		println(fs.line + ": Visiting a for statement");

		int i=0;
		// TODO: must block be par to enroll on barriers??
		// Check that all the barrier expressions are of barrier type.
		for (Expression e : fs.barriers()) {
			Type t = resolve(e.visit(this));
			if (!t.isBarrierType()) 	
				Error.error(fs.barriers().child(i),"Barrier type expected, found '" + t + "'.", false, 3025);
			i++;
		}
		
		if (fs.init() != null) 
			fs.init().visit(this);
		if (fs.incr() != null)
			fs.incr().visit(this);
		if (fs.expr() != null) {
			Type eType =  resolve(fs.expr().visit(this));

			if (!eType.isBooleanType())
				Error.error(fs, "Non boolean Expression found in for-statement.", false, 3026);
		}
		if (fs.stats() != null)
			fs.stats().visit(this);

		return null;
	}
	
    // Guard -- Nothing to do
	
	/** IF STATEMENT */ // ERROR TYPE OK
	public Type visitIfStat(IfStat is) {
		println(is.line + ": Visiting a if statement");

		Type eType =  resolve(is.expr().visit(this));
		
		if (!eType.isBooleanType())
			Error.error(is, "Non boolean Expression found as test in if-statement.", false, 3027);
		if (is.thenpart() != null) 
			is.thenpart().visit(this);
		if (is.elsepart() != null) 
			is.elsepart().visit(this);
		
		return null;
	}
	
    // Import - nothing to do
	
    // Invocation
	public Type visitInvocation(Invocation in) {
		println(in.line + ": visiting invocation (" + in.procedureName() + ")");




        in.params().visit(this);
		// TODO: remember to set in.targetProc;
		// TODO: remember to set in.type;	
		
		// mobile procedures are special ....... all the interfaces of a mobile should be considered
		
		
		
		
		
		//println(in.line + ": invocation has type: " + in.)
		
		return in.type;
	}
	
    // LocalDecl
	// Modifier - nothing to do
    // Name - nothing to do
    
	/** NAMED TYPE */
	public Type visitNamedType(NamedType nt) {
		println(nt.line + ": visiting a named type (" + nt.name().getname() + ").");
		// TODO: not sure how to handle error type here 
		if (nt.type() == null) {
			// go look up the type and set the type field of nt.
			Type t = (Type)topLevelDecls.get(nt.name().getname());
			if (t == null) {
				Error.error(nt,"Undefined named type '" + nt.name().getname() + "'.", false, 3028);
			}
			nt.setType(t);
		}
		println(nt.line + ": named type has type: " + nt.type());
		
		return nt.type();
	}
	
    /** NAME EXPRESSION */ // ERROR TYPE OK
	public Type visitNameExpr(NameExpr ne) {
		println(ne.line + ": Visiting a Name Expression (" + ne.name().getname() + ").");
		if (ne.myDecl instanceof LocalDecl || 
				ne.myDecl instanceof ParamDecl ||
				ne.myDecl instanceof ConstantDecl) {
			// TODO: what about ConstantDecls ???
			// TODO: don't think a resolve is needed here
			ne.type = ((VarDecl)ne.myDecl).type();
		} else
			ne.type = Error.addError(ne,"Unknown name expression '" + ne.name().getname() + "'.", 3029);

		println(ne.line + ": Name Expression (" + ne.name().getname() + ") has type: " + ne.type);
		return ne.type;
	}
	
	public boolean arrayAssignmentCompatible(Type t, Expression e) {
		if (t instanceof ArrayType && e instanceof ArrayLiteral) {
			ArrayType at = (ArrayType)t;
			e.type = at; //  we don't know that this is the type - but if we make it through it will be!
			ArrayLiteral al = (ArrayLiteral)e;

			// t is an array type i.e. XXXXXX[ ]
			// e is an array literal, i.e., { }
			if (al.elements().size() == 0) // the array literal is { }
				return true;   // any array variable can hold an empty array
			// Now check that XXXXXX can hold value of the elements of al
			// we have to make a new type: either the base type if |dims| = 1
			boolean b = true;
			for (int i=0; i<al.elements().size(); i++) {
				if (at.getDepth() == 1) 
				    b = b && arrayAssignmentCompatible(at.baseType(), (Expression)al.elements().child(i));
				else { 
					ArrayType at1 = new ArrayType(at.baseType(), at.getDepth()-1);
					b = b  && arrayAssignmentCompatible(at1, (Expression)al.elements().child(i));
				}
			}
			return b;
		} else if (t instanceof ArrayType && !(e instanceof ArrayLiteral)) 
			Error.error(t, "Error: cannot assign non array to array type " + t.typeName());
		else if (!(t instanceof ArrayType) && (e instanceof ArrayLiteral)) 
			Error.error(t, "Error: cannot assign value " + ((ArrayLiteral)e).toString() + " to type " + t.typeName(), false, 3030);
		return Type.assignmentCompatible(t,e.visit(this));
	}

	/** NewArray */ // ERROR TYPE OK
	public Type visitNewArray(NewArray ne) {
	    println(ne.line + ": Visiting a NewArray " + ne.dimsExpr().size() + " " + ne.dims().size());
	
		//  check that each dimension is of integer type
		for (Expression exp : ne.dimsExpr()) {
			Type dimT = resolve(exp.visit(this));
			if (!dimT.isIntegralType())
			    Error.error(exp, "Array dimension must be of integral type", false, 3031);
		}
		// if there is an initializer, then make sure it is of proper and equal depth.
		ne.type = new ArrayType(ne.baseType(), ne.dims().size()+ne.dimsExpr().size());
		if (ne.init() != null)  {
			// The elements of ne.init() get visited in the last line of arrayAssignmentCompatible.
			if (!arrayAssignmentCompatible(ne.type, ne.init()))
				Error.error(ne, "Array Initializer is not compatible with " + ne.type.typeName(), false, 3032);
			ne.init().type = ne.type;
		}       
		println(ne.line + ": NewArray type is " + ne.type);
		return ne.type;
	}
    
    // NewMobile
	// TODO: you can only 'new' stuff that is a mobile -- perhaps this check from the NameChecker should be here!
	
    // ParamDecl - nothing to do
    // ParBlock - nothing to do
    // Pragma - nothing to do
	
	/** PRIMITIVE LITERAL */
	public Type visitPrimitiveLiteral(PrimitiveLiteral pl) {
		println(pl.line + ": Visiting a primitive literal (" + pl.getText() + ").");
		
		// Remember that the constants in PrimitiveType are defined from the ones                                                      
		// in Literal, so its it ok to just use li.kind! -- except for the null literal.                                               

		if (pl.getKind() == PrimitiveLiteral.NullKind)
			pl.type = null; // new NullType(li); TODO: Perhaps we need a null type and a null value too ??
		else
			pl.type = new PrimitiveType(pl.getKind());
	                                                                                                                       

		println(pl.line + ": Primitive literal has type: " + pl.type);
		return pl.type;
	}

	/** PRIMITIVE TYPE */ // ERROR TYPE OK
	public Type visitPrimitiveType(PrimitiveType pt) {
		println(pt.line + ": Visiting a primitive type.");
		println(pt.line + ": Primitive type has type: " + pt);
		return pt;
	}
	
    /** PROTOCOL TYPE DELCARATION */ // ERROR TYPE OK
	public Type visitProcTypeDecl(ProcTypeDecl pd) {
		println(pd.line + ": visiting a procedure type declaration (" + pd.name().getname() + ").");
		currentProcedure = pd;
		super.visitProcTypeDecl(pd);
		return null;
	}
	
	/** PROTOCOL LITERAL */
	public Type visitProtocolLiteral(ProtocolLiteral pl) {
		println(pl.line + ": Visiting a protocol literal");
		
		// tag already checked in NameChecker

		// TODO: below code is incorrect as it does not take 'extends' into account
		
		// Name{ tag: exp_1, exp_2, ... ,exp_n }
		ProtocolCase pc = pl.myChosenCase;
		ProtocolTypeDecl pd = pl.myTypeDecl;
		if (pc.body().size() != pl.expressions().size()) 
			Error.error(pl, "Incorrect number of expressions in protocol literal '" + pd.name().getname() + "'", false, 3033);
		for (int i=0; i<pc.body().size(); i++) {
		    Type eType = resolve(pl.expressions().child(i).visit(this));
		    Type vType = resolve(((RecordMember)pc.body().child(i)).type());
		    Name name = ((RecordMember)pc.body().child(i)).name();
			if (!Type.assignmentCompatible(vType, eType))
				Error.error(pl,"Cannot assign value of type '" + eType + "' to protocol field '" + name.getname() + "' of type '" +
						vType + "'.", false, 3034);
		}			
		println(pl.line + ": protocol literal has type: " + pl.myTypeDecl);
		return pl.myTypeDecl;
	}
	
    // ProtocolCase
    
	/** PROTOCOL TYPE DECLARATION */ // ERROR TYPE OK
	public Type visitProtocolTypeDecl(ProtocolTypeDecl pt) {
		println(pt.line + ": Visiting a protocol type decl.");
		println(pt.line + ": Protocol type decl has type: " + pt);
		return pt;
	}
	    
	/** RECORD ACCESS */ // ERROR TYPE OK
	public Type visitRecordAccess(RecordAccess ra) {
		println(ra.line + ": visiting a record access expression (" + ra.field().getname() + ")");
		Type tType = resolve(ra.record().visit(this));
		tType = tType.visit(this);

        // TODO: size of strings.... size()? size? or length?   for now: size() -> see visitInvocation

		// Array lengths can be accessed through a length 'field'
		if (tType.isArrayType() && ra.field().getname().equals("size")) {
            ra.type = new PrimitiveType(PrimitiveType.IntKind);
            ra.isArraySize = true;
            println(ra.line + ": Array size expression has type: " + ra.type);
            return ra.type;
		} if (tType.isStringType() && ra.field().getname().equals("length")) {
            ra.type = new PrimitiveType(PrimitiveType.IntKind);      // TODO: should this be long ???
            ra.isStringLength = true;
            println(ra.line + ": string length expression has type: " + ra.type);
            return ra.type;
        } else {
            if (!tType.isRecordType()) {
				ra.type = Error.addError(ra,"Request for member '" + ra.field().getname() + "' in something not a record type.", 0000);
				return ra.type;
			}				
			// Now find the field and make the type of the record access equal to the field.
			RecordMember rm = ((RecordTypeDecl)tType).getMember(ra.field().getname());
			if (rm == null) {
				ra.type = Error.addError(ra,"Record type '" + ((RecordTypeDecl)tType).name().getname() + "' has no member '" + ra.field().getname() + "'.", 0000);
				return ra.type;
			}	
			Type rmt = resolve(rm.type().visit(this));
			ra.type = rmt;
		}
    println(ra.line + ": record access expression has type: " + ra.type);
		return ra.type;
	}
	
	/** RECORD LITERAL */
	public Type visitRecordLiteral(RecordLiteral rl) {
		println(rl.line + ": visiting a record literal (" + rl.name().getname() + ").");
		RecordTypeDecl rt = rl.myTypeDecl;

		// TODO: be careful here if a record type extends another record type, then the record literal must contains 
		// expressions for that part too!!!
		
		return rt;
	}
	
    // RecordMember
	
    /** RECORD TYPE DECLARATION */ // ERROR TYPE OK
	public Type visitRecordTypeDecl(RecordTypeDecl rt) {
		println(rt.line + ": Visiting a record type decl.");
		
		
		println(rt.line + ": Record type decl has type: " + rt);
		return rt;
	}
	
    /** RETURN STATEMENT */ // ERROR TYPE OK
	public Type visitReturnStat(ReturnStat rs) {
		println(rs.line + ": visiting a return statement");
		
		Type returnType = resolve(currentProcedure.returnType());
		
		// check if the return type is void; if it is rs.expr() should be null.
		// check if the return type is not voidl if it is not rs.expr() should not be null.
		if (returnType instanceof PrimitiveType) {
			PrimitiveType pt = (PrimitiveType)returnType;
			if (pt.isVoidType() && rs.expr() != null)
				Error.error(rs, "Procedure return type is void; return statement cannot return a value.", false, 0000);
			if (!pt.isVoidType() && rs.expr() == null)
				Error.error(rs, "Procedure return type is " + pt + " but procedure return type is void.", false, 0000);
			if (pt.isVoidType() && rs.expr() == null)
				return null;
		}
		
		Type eType = resolve(rs.expr().visit(this));
		if (!Type.assignmentCompatible(returnType, eType)) 
			Error.error(rs, "Incompatible type in return statement.", false, 0000);

		return null;
	}
	
    // Sequence - nothing to do
    // SkipStat - nothing to do
    // StopStat -- nothing to do

	/** SUSPEND STATEMENT */ // ERROR TYPE OK
	public Type visitSuspendStat(SuspendStat ss) {
		println(ss.line + ": Visiting a suspend stat.");
		if (!Modifier.hasModifierSet(currentProcedure.modifiers(), Modifier.MOBILE)) 
			Error.error(ss,"Non-mobile procedure cannot suspend.", false, 0000);
		return null;
	}
	
    // SwitchGroup -- nothing to do - handled in SwitchStat
    // SwitchLabel -- nothing to do - handled in SwitchStat
  	
	/** SWITCH STATEMENT */ 
	public Type visitSwitchStat(SwitchStat ss) {
		println(ss.line + ": Visiting a Switch statement");

		// TODO: RecordAccess that accesses a ProtocolType should 
		// only be allowed when switching on the tag!

		int i,j;
		Type lType;
		Type eType =  resolve(ss.expr().visit(this)); 
		Set<String> ht = new HashSet<String>();
		if (!eType.isIntegralType() || eType.isLongType())
			Error.error(ss, "Switch statement expects value of type int.", false, 0000);

		for (SwitchGroup sg : ss.switchBlocks() ) {
		    for (SwitchLabel sl : sg.labels()) {
			if (sl.isDefault())
			    continue;
			lType =  resolve(sl.expr().visit(this));
			if (!lType.isIntegralType() || lType.isLongType())
			    Error.error(sl, "Switch labels must be of type int.", false, 0000);
			if (!sl.expr().isConstant())
			    Error.error(sl, "Switch labels must be constants.", false, 0000);
		    }
		    sg.statements().visit(this);
		}
		for (SwitchGroup sg : ss.switchBlocks() ) {
		    for(SwitchLabel sl : sg.labels()) {
			if (sl.isDefault()) {
			    if (ht.contains("default")) 
				Error.error(sl,"Duplicate default label.", false, 0000);
			    else
				ht.add("default");
			    continue;
			}
			int val = ((BigDecimal)sl.expr().constantValue()).intValue();
			String strval = Integer.toString(val);
			if (ht.contains(strval))
			    Error.error(sl,"Duplicate case label.", false, 0000);
			else {
			    ht.add(strval);
			}
		    }
		}       
		return null;
	}
	
	/** SYNC STAT */ //ERROR TYPE OK
	public Type visitSyncStat(SyncStat ss) {
		println(ss.line + ": visiting a sync stat.");
		Type t = resolve(ss.barrier().visit(this));
		if (!t.isBarrierType())
			Error.error(ss, "Non-barrier type in sync statement.", false, 0000);
		return null;
	}
	
    // Ternary
	public Type visitTernary(Ternary te) {
		println(te.line + ": Visiting a ternary expression");

		Type eType = resolve(te.expr().visit(this));
		Type trueBranchType  = te.trueBranch().visit(this);
		Type falseBranchType = te.falseBranch().visit(this);

		if (!eType.isBooleanType())
			Error.error(te, "Non boolean Expression found as test in ternary expression.");

		if (trueBranchType instanceof PrimitiveType && falseBranchType instanceof PrimitiveType) {
			if (Type.assignmentCompatible(falseBranchType, trueBranchType) ||
					Type.assignmentCompatible(trueBranchType, falseBranchType)) 
				te.type = PrimitiveType.ceilingType((PrimitiveType)trueBranchType, (PrimitiveType)falseBranchType);
			else
				Error.error(te,"Both branches of a ternary expression must be of assignment compatible types.");
		} else if (te == null) { // te is never null! just to fool Eclipse
			//TODO: What about assignments of protocol and records wrt to their inheritance and procedures? 			
		} else
			Error.error(te,"Both branches of a ternary expression must be of assignment compatible types.");       
		
		println(te.line + ": Ternary has type: " + te.type);
		return te.type;
	}
	
    /** TIMEOUT STATEMENT */ // ERROR TYPE OK
	public Type visitTimeoutStat(TimeoutStat ts) {
		println(ts.line + ": visiting a timeout statement.");
		Type dType = resolve( ts.delay().visit(this));
		if (!dType.isIntegralType())
			Error.error(ts,"Invalid type in timeout statement, integral type required.", false, 0000);
		Type eType = resolve(ts.timer().visit(this));
		if (!eType.isTimerType())
			Error.error(ts,"Timer type required in timeout statement.", false, 0000);
		return null;
	}
	
	/** UNARY POST EXPRESSION */ // ERROR TYPE OK
	public Type visitUnaryPostExpr(UnaryPostExpr up) {
		println(up.line + ": Visiting a unary post expression");
		up.type = null;
		Type eType = resolve(up.expr().visit(this));

		// TODO: what about protocol ??
		if (up.expr() instanceof NameExpr || up.expr() instanceof RecordAccess || up.expr() instanceof ArrayAccessExpr) {
			if (!eType.isIntegralType() && !eType.isDoubleType() && !eType.isFloatType())
				up.type = Error.addError(up, "Cannot apply operator '" + up.opString() + "' to something of type " + eType.typeName() + ".", 0000);       
		} else 
			up.type = Error.addError(up, "Variable expected, found value.", 0000);
		
		if (up.type == null)
			up.type = eType;
	
		println(up.line + ": Unary Post Expression has type: " + up.type);
		return up.type;
	}

   /** UnaryPreExpr */ // ERROR TYPE OK
	public Type visitUnaryPreExpr(UnaryPreExpr up) {
		println(up.line + ": Visiting a unary pre expression");
		up.type = null;
		Type eType =  resolve(up.expr().visit(this));

		switch (up.op()) {
		case UnaryPreExpr.PLUS:
		case UnaryPreExpr.MINUS:
			if (!eType.isNumericType())
				up.type = Error.addError(up, "Cannot apply operator '" + up.opString() + "' to something of type " + eType.typeName() + ".", 0000);
			break;
		case UnaryPreExpr.NOT:
			if (!eType.isBooleanType())
				up.type = Error.addError(up, "Cannot apply operator '" + up.opString() +	"' to something of type " + eType.typeName() + ".", 0000);
			break;
		case UnaryPreExpr.COMP:
			if (!eType.isIntegralType())
				up.type = Error.addError(up, "Cannot apply operator '" + up.opString() +	"' to something of type " + eType.typeName() + ".", 0000);
			break;
		case UnaryPreExpr.PLUSPLUS:
		case UnaryPreExpr.MINUSMINUS:
			if (!(up.expr() instanceof NameExpr) &&	!(up.expr() instanceof RecordAccess) && !(up.expr() instanceof ArrayAccessExpr))
				up.type = Error.addError(up, "Variable expected, found value.", 0000);

			if (!eType.isNumericType() && up.type == null)
				up.type = Error.addError(up, "Cannot apply operator '" + up.opString() +	"' to something of type " + eType.typeName() + ".", 0000);
			break;
		}
		if (up.type == null)
			up.type = eType;
		println(up.line + ": Unary Pre Expression has type: " + up.type);
		return up.type;
	}

	/** VAR */ // ERROR TYPE OK
	public Type visitVar(Var va) {
		println(va.line + ": Visiting a var ("+va.name().getname()+").");

		if (va.init() != null) {
			Type vType = resolve(va.myDecl.type());
			Type iType = resolve(va.init().visit(this));

			if (vType.isErrorType() || iType.isErrorType()) 
				return null;
						
			if (!Type.assignmentCompatible(vType,iType))
				Error.error(va, "Cannot assign value of type " + iType.typeName() + " to variable of type " +vType.typeName() + ".", false, 0000);
		}
		return null;
	}
    
	/** WHILE STATEMENT */
	public Type visitWhileStat(WhileStat ws) {
		println(ws.line + ": Visiting a while statement"); 
		Type eType =  resolve(ws.expr().visit(this));

		if (!eType.isBooleanType())
			Error.error(ws, "Non boolean Expression found as test in while-statement.", false, 0000);
		if (ws.stat() != null)
			ws.stat().visit(this);
		return null;
	}
}
