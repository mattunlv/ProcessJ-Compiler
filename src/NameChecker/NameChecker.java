package NameChecker;

import Utilities.Visitor;
import AST.*;
import Utilities.SymbolTable;
import Utilities.Error;
import Utilities.Log;
import java.util.HashSet;
import java.util.ArrayList;

// Error message number range: [2200 - 2299]

public class NameChecker<T extends AST> extends Visitor<T> {
	SymbolTable currentScope = null;

	// The topScope is the SymbolTable originally passed to the constructor.
	// It contains the top level declaration of the file being compiled.	
	SymbolTable topScope = null; 

	public NameChecker(SymbolTable currentScope) {
		this.currentScope = currentScope;    	
		this.topScope = currentScope;
		Log.log("======================================");
		Log.log("*       N A M E   C H E C K E R      *");
		Log.log("======================================");
	}

	private Object resolveName(Name na) {
		if (!na.isSimple()) {
			return na.resolvedPackageAccess;
		} else {
			String name = na.getname();

			// look in currentScope
			Object o = currentScope.get(name);
			if (o != null)
				return o;
			// if not found look in topScope
			o = topScope.getIncludeImports(name);
			if (o != null)
				return o;
			return null;
		}
	}




	// AltCase - nothing to do
	// AltStat - nothing to do
	// AltStat - nothing to do
	// ArrayAccessExpr - nothing to do
	// ArrayLiteral - probably nothing to do
	// ArrayType - nothing to do
	// Assignment - nothing to do
	// BinaryExpr - nothing to do

	public T visitBlock(Block bl) { 
		Log.log(bl.line + ": Visting Block (Opening new scope)");
		currentScope = currentScope.openScope();
		super.visitBlock(bl);
		currentScope = currentScope.closeScope();
		Log.log(bl.line + ": Closing scope (end of Block)");
		return null;
	}

	// BreakStat - nothing to do
	// CastExpr - nothing to do
	// ChannelType - nothing to do
	// ChannelEndExpr - nothing to do
	// ChannelEndType - nothing to do
	// ChannelReadExpr - nothing to do
	// ChannelWriteStat - nothing to do
	// ClaimStat - probably nothing to do

	/** COMPILATION */
	public T visitCompilation(Compilation co) {
		super.visitCompilation(co);
		return  null;
	}

	/** CONSTANT DECLARATION */
	public T visitConstantDecl(ConstantDecl cd) {
		Log.log(cd.line + ": Visting ConstantDecl '" + cd.var().name().getname() + "' (Setting myDecl)");
		super.visitConstantDecl(cd);
		cd.var().myDecl = cd;	
		// TODO: cannot be a mobile procedure!!!! or any procedure ! that wouldn't make sense!

		return null;
	}

	// ContinueStat - nothing to do
	// DoStat - nothing to do
	// ExprStat - nothing to do
	// ExternType - nothing to do

	public T visitForStat(ForStat fs) {
		Log.log(fs.line + ": Visting ForStat (Opening new scope)");
		currentScope = currentScope.openScope();
		super.visitForStat(fs);
		currentScope = currentScope.closeScope();
		Log.log(fs.line + ": Closing scope (end of ForStat)");
		return null;
	}

	// Guard - nothing to do
	// IfStat - nothing to do
	// Import - nothing to do

	public T visitInvocation(Invocation in) {
		Log.log(in.line + ": Visiting Invocation (" + in.procedureName().getname() + ")");

		/*
		 * An invocation without a target can look like:
		 *  f()
		 *  F::f()
		 *  P.F::f()
		 *  ...
		 * and with a target
		 *  x.f()
		 *  x.y.f()
		 *  ...
		 *  F::x.f()
		 *  P.F::x.f()
		 *  ... 
		 */
		if (in.target() == null) {
			// f()
			// F::f()
			// P.F::f()
			// ...::f()
			Object o = resolveName(in.procedureName());
			if (o == null)
				Error.error(in, "Procedure '"  + in.procedureName().getname() + "' not found", false, 0000);
			if (!(o instanceof SymbolTable))
				Error.error(in, "Cannot invoke non-procedure '" + in.procedureName().getname() + "'.", false, 0000);
			else
				in.candidateMethods = (SymbolTable)o;	
		} else { // in.target() != null
			// This we cannot do cause Type Checking is required.
			Log.log(in.line + ": Invocation: target not null: too complicated for now!");
		}

		in.params().visit(this);
		return null;
	}

	public T visitLocalDecl(LocalDecl ld) {
		Log.log(ld.line + ": Visting LocalDecl (" + ld.type().typeName() + " " + ld.var().name().getname() + ")");
		if (!currentScope.put(ld.name(), ld))
			Error.error(ld,ld.name() + " already declared in this scope.",false,2202);
		ld.var().myDecl = ld;
		super.visitLocalDecl(ld);		
		return null;
	}

	// Modifier - nothing to do
	// Name - nothing to do

	// NamedType - TODO: Should probably be looked up in the scope chain and result in one of Procedure, Constant, ExternType, Record or Protocol
	public 	T visitNamedType(NamedType nt) {
		Object o;
		o = resolveName(nt.name());
		if (o == null)
			Error.error(nt,"Symbol '" + nt.name().getname() + "' not found.", false, 2203);	
		if (o instanceof ConstantDecl) {
			Error.error(nt,"Symbol '" + nt.name().getname() + "' is not a type.");
		}
		// TODO: consider the import hierarchy back for similiarly named procs
		/*if (o instanceof SymbolTable) { // we found a procedure so there can only be one and it must be mobile!
			SymbolTable st = (SymbolTable)o;
			if (st.entries.size() > 1)
				Error.error(nt,"Procedure type parameters cannot be used if more than one implementation of the procedure exists!");
			ProcTypeDecl pd = (ProcTypeDecl)(new ArrayList<Object>(st.entries.values()).get(0));
			nt.setType(pd);
		} else */				
		nt.setResolvedTopLevelDecl((TopLevelDecl)o);
		return null;
	}	

	public T visitNameExpr(NameExpr ne) {
		Log.log(ne.line + ": Visiting NameExpr (" + ne.name().getname() + ")");

		Object o = resolveName(ne.name());
		if (o == null)
			Error.error(ne,"Symbol '" + ne.name().getname() + "' not found.", false, 0000);
		else
			ne.myDecl = (AST)o;
		return null;
	}

	// NewArray - nothing to do    

	public T visitNewMobile(NewMobile nm) {
		Log.log(nm.line + ": Visiting NewMobile (" + nm.name().getname() + ").");

		Object o = resolveName(nm.name());
		if (o == null)
			Error.error(nm,"Symbol '" + nm.name().getname() + "' not found.", false, 0);
		else
			nm.myDecl = (TopLevelDecl)o;
		return null;
	}

	public T visitParamDecl(ParamDecl pd) {
		Log.log(pd.line + ": Visiting ParamDecl (" + pd.paramName().getname() + ").");
		// TODO: just delete Object o = currentScope.get(pd.name());
		if (!currentScope.put(pd.name(), pd))
			Error.error(pd,pd.name() + " already declared in this scope.",false,2206);
		super.visitParamDecl(pd);
		return null;
	}    

	// both Protocol and Record Decls can extend . make sure they only extend the right things

	// ParBlock - nothing to do
	// Pragma - nothing to do
	// PrimitiveLiteral - nothing to do
	// PrimitiveType - nothing to do

	public T visitProcTypeDecl(ProcTypeDecl pd) {
		Log.log(pd.line + ": Visiting ProcTypeDecl (" + pd.name().getname() + ").");
		currentScope = currentScope.openScope();
		pd.formalParams().visit(this);

		for (Name name : pd.implement()) {
			Object o = resolveName(name);
			if (o == null) 
				Error.error(pd, "Symbol '" + name.getname() + "' not found.");
			// We do not test for ProcTypeDecl because the top level scope contains a
			// SymbolTable for each procedure as we might have multiple declarations of 
			// procedures with the same name.
			else if (!(o instanceof SymbolTable))
				Error.error(pd, "Symbol '" + name.getname() + "' not a procedure.");
		}
		if (pd.body() != null)
			pd.body().visit(this);
		currentScope = currentScope.closeScope();
		return null; //super.visitProcTypeDecl(pd);
	}

	// ProtocolLiteral
	public T visitProtocolLiteral(ProtocolLiteral pl) {
		Log.log(pl.line + ": Visiting ProtocolLiteral (" + pl.name().getname() + ").");
		
		Object o = resolveName(pl.name());
		if (o == null) 
			Error.error(pl, "Symbol '" + pl.name().getname() + "' not found.");
		else if (!(o instanceof ProtocolTypeDecl))
			Error.error(pl, "Symbol '" + pl.name().getname() + "' is not a protocol type.");

		// Check if the tag is OK now.
		pl.myTypeDecl = (ProtocolTypeDecl)o;
		ProtocolTypeDecl ptd = pl.myTypeDecl;
		for (ProtocolCase pc : ptd.body()) {
			if (pc.name().getname().equals(pl.tag().getname())) {
				pl.myChosenCase = pc;
				pl.expressions().visit(this);
				return null;
			}
		}
		Error.error(pl,"Undefined protocol tag name '" + pl.tag().getname() + "' in literal of protocol type '" + pl.name().getname());

		return null;
	}

	// ProtocolCase - nothing to do

	public T visitProtocolTypeDecl(ProtocolTypeDecl pd) {
		Log.log(pd.line + ": Visiting ProtocolTypeDecl (" + pd.name().getname() + ").");
		
		for (Name n : pd.extend()) {
			Object o = resolveName(n);
			if (o == null)
				Error.error(n,"Symbol '" + n.getname() + "' not found.");
			else if (!(o instanceof ProtocolTypeDecl))
				Error.error(n, "'" + n.getname() + "' cannot be extended as a protocol as it is not of protocol type.");
		}
		// TODO: make sure we don't repreat names in the 'extend' part.
		return null;
	}	

	// RecordAccess - nothing to do - required type checking

	public T visitRecordLiteral(RecordLiteral rl) {
		Log.log(rl.line + ": Visiting RecordLiteral (" + rl.name().getname() + ").");

		Object o = resolveName(rl.name());
		if (o == null)
			Error.error(rl,"Symbol '" + rl.name().getname() + "' not found.");
		else if (!(o instanceof RecordTypeDecl))
			Error.error(rl, "Symbol '" + rl.name().getname() + "' is not a record type.");
		else 
			rl.myTypeDecl = (RecordTypeDecl)o;
		return null;
	}

	// RecordMember - nothing to do


	public T visitRecordTypeDecl(RecordTypeDecl rt) {	
		Log.log(rt.line + ": Visiting RecordTypeDecl (" + rt.name().getname() + ").");
		for (Name n : rt.extend()) {
			Object o = resolveName(n);
			if (o == null)
				Error.error(n,"Symbol '" + n.getname() + "' not found.");
			else if (!(o instanceof RecordTypeDecl))
				Error.error(n, "'" + n.getname() + "' cannot be extended as a record as it is not of record type.");
		}
		// Make sure we don't repeat names in the 'extends' part.
		HashSet<String> hs = new HashSet<String>();
		for (Name name : rt.extend()) {
			if (hs.contains(name.getname()))
				Error.error(rt,"'" + name.getname() + "' repeated in extends clause of record type '" +rt.name().getname() + "'.");
			hs.add(name.getname());
		}

		// TODO: also check for name clashes (in theory the same record could be imported twice:
		// import T;
		// type record Foo extends T::Bar, Bar { ... }
		return null;
	}

	// ReturnStat - nothing to do
	// Sequence - nothing to do
	// SkipStat - nothing to do
	// StopStat - nothing to do
	// SuspendStat - nothing to do (always resumes a procedure of the same name!)
	// SwitchGroup -- nothing to do
	// SwitchLabel - nothing to do
	// SwitchStat - nothing to do
	// SyncStat - nothing to do
	// Ternary - nothing to do
	// TimeoutStat - nothing to do
	// UnaryPostExpr - nothing to do
	// UnaryPreExpr - nothing to do
	// Var - nothing to do
	// WhileStat - nothing to do
}