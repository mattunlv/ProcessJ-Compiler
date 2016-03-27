package CodeGeneratorJava;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStreamWriter;
import java.io.Writer;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.stringtemplate.v4.ST;
import org.stringtemplate.v4.STGroup;
import org.stringtemplate.v4.STGroupFile;

import AST.AltCase;
import AST.AltStat;
import AST.ArrayAccessExpr;
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
import AST.Compilation;
import AST.ContinueStat;
import AST.DoStat;
import AST.ExprStat;
import AST.Expression;
import AST.ForStat;
import AST.IfStat;
import AST.Invocation;
import AST.LocalDecl;
import AST.Modifier;
import AST.Name;
import AST.NameExpr;
import AST.NewArray;
import AST.ParBlock;
import AST.ParamDecl;
import AST.PrimitiveLiteral;
import AST.PrimitiveType;
import AST.ProcTypeDecl;
import AST.ReturnStat;
import AST.Sequence;
import AST.SkipStat;
import AST.Statement;
import AST.SwitchGroup;
import AST.SwitchLabel;
import AST.SwitchStat;
import AST.Ternary;
import AST.TimeoutStat;
import AST.Type;
import AST.UnaryPostExpr;
import AST.UnaryPreExpr;
import AST.Var;
import AST.WhileStat;
// import NameCollector.NameCollector;
import Utilities.Error;
import Utilities.Log;
import Utilities.Visitor;

public class CodeGeneratorJava<T extends Object> extends Visitor<T> {

	/** Relative location of our group string template file. */
	private final String grammarStFile = "src/StringTemplates/grammarTemplatesJava.stg";

	/** Object containing a group of string templates. */
	private STGroup group;

	private Map<String, String> _gFormalNamesMap = null;
	private Map<String, String> _gLocalNamesMap = null;
	private List<String> _gLocals = null;
	private List<String> _switchCases = null;
	private List<String> _parSwitchCases = null;
	private int _gvarCnt = 0;
	private int _jumpCnt = 0;
	private int _parCnt = 0;
	private boolean _inParBlock = false;
	private String _parName = null;

	private boolean _proc_yields = false;
	private final String MAIN_SIGNATURE = "([T;)V";

	//TODO Cabel look at trying to get this name from Error.filename or from compilation.
	private String originalFilename = null;

	public CodeGeneratorJava() {
		Log.log("==========================================");
		Log.log("* C O D E   G E N E R A T O R  ( Java )  *");
		Log.log("*        F I R S T  P A S S              *");
		Log.log("=========================================");

		// Load our string templates from specified directory.
		this.group = new STGroupFile(grammarStFile);
	}

	public void setOriginalFilename(String n) {
		this.originalFilename = n;
	}

	/**
	 * AltCase
	 * 
	 * FIXME: incomplete
	 */
	public T visitAltCase(AltCase ac) {
		Log.log(ac.line + ": Visiting an AltCase");
		ST template = group.getInstanceOf("AltCase");

		// TODO preconditions.
		Statement caseExprStmt = ac.guard().guard();
		Statement stat = ac.stat();
//		int caseNumber = ac.getCaseNumber();
		String caseExprStr;

		// We treat TimeoutStat differently:
		if (caseExprStmt instanceof TimeoutStat)
			caseExprStr = "/*Timeout do nothing here!*/";
		else
			// Two more posibilities for myExpr: SkipStat | ExprStat (Channel)
			caseExprStr = (String) caseExprStmt.visit(this);

		// Make the actual block of statements to run, we know this must always
		// be a block so,
		// no need to check what is returned.
		String[] statementList = (String[]) stat.visit(this);

//		template.add("number", caseNumber);
		template.add("guardToDo", caseExprStr);
		template.add("statementList", statementList);

		return (T) template.render();
	}

	/**
	 * AltStat FIXME: incomplete and think about PRIALT as well.
	 */
	public T visitAltStat(AltStat as) {
		Log.log(as.line + ": Visiting an AltStat");
		ST template = group.getInstanceOf("AltStat");
		Sequence<AltCase> altCaseList = as.body();
		int count = altCaseList.size();
		boolean hasTimeout = false;

		//If the alt uses timeout we must use a different function to invoke the alt.
		for (int i = 0; i < count; i++) {
			AltCase altCase = altCaseList.child(i);
			hasTimeout = caseIsTimeout(altCase);

			if (hasTimeout == true)
				break;
		}

		//Create the Alt() and AltWait().
		String altTypeStr = "NormalAltType";
		String waitTypeStr = "NormalWaitType";
		if (hasTimeout == true) {
			altTypeStr = "TimerAltType";
			waitTypeStr = "TimerWaitType";
		}

		ST altTypeTemplate = group.getInstanceOf(altTypeStr);
		ST waitTypeTemplate = group.getInstanceOf(waitTypeStr);

		//These lists hold our strings representing the {"AltEnablechannel(<name>)",...}
		//for our alt statement.
		String[] enableList = createEnableDisable(altCaseList, true);
		String[] disableList = createEnableDisable(altCaseList, false);

		//Only thing left to do is to make the switch statement holding our cases to run.
		ST altEndTemplate = group.getInstanceOf("AltEnd");
		ST altSwitchTemplate = group.getInstanceOf("AltSwitch");

		//Create AltEnd part.
		altEndTemplate.add("globalWsName", "workspace");
		String altEndStr = altEndTemplate.render();

		//Iterate over our children setting their number.
		for (int i = 0; i < count; i++) {
			AltCase altCase = altCaseList.child(i);
//			altCase.setCaseNumber(i);
		}

		//Create all the case statements.
		String[] caseList = (String[]) altCaseList.visit(this);

		//Now make our switch string.
		altSwitchTemplate.add("altEnd", altEndStr);
		altSwitchTemplate.add("caseList", caseList);
		String altSwitchStr = altSwitchTemplate.render();

		//Make our wait and alt templates.
		altTypeTemplate.add("globalWsName", "workspace");
		waitTypeTemplate.add("globalWsName", "workspace");

		//Make final string!
		template.add("altType", altTypeTemplate.render());
		template.add("enableChannelList", enableList);
		template.add("waitType", waitTypeTemplate.render());
		template.add("disableChannelList", disableList);
		template.add("switchStatement", altSwitchStr);

		return (T) template.render();
	}

	/**
	 * ArrayAccessExpr
	 */
	public T visitArrayAccessExpr(ArrayAccessExpr ae) {
		Log.log(ae.line + ": Visiting an ArrayAccessExpr!");
		String myArrayTarget = (String) ae.target().visit(this);
		String myIndex = (String) ae.index().visit(this);

		return (T) ("(" + myArrayTarget + "[" + myIndex + "])");
	}

	/**
	 * ArrayLiteral
	 * 
	 * TODO: What to do about this?
	 */

	/**
	 * ArrayType
	 */
	public T visitArrayType(ArrayType at) {
		Log.log(at.line + ": Visiting an ArrayType!");
		String baseType = (String) at.baseType().visit(this);
		return (T) (baseType + "[]");
	}

	/**
	 * ChannelType [cds done]
	 */
	public T visitChannelType(ChannelType ct) {
		Log.log(ct.line + ": Visiting a Channel Type!");

		String typeString;
		switch(ct.shared()) {
			case ChannelType.NOT_SHARED: typeString = "One2OneChannel";break;
			case ChannelType.SHARED_WRITE: typeString = "Many2OneChannel";break;
			case ChannelType.SHARED_READ: typeString = "One2ManyChannel";break;
			case ChannelType.SHARED_READ_WRITE: typeString = "Many2ManyChannel";break;
			default: typeString = "One2OneChannel";
		}
		
		return (T) (typeString + "<" + getWrapperType(ct.baseType()) + ">");
	}

	/**
	 * ChannelEndExpr [cds done]
	 * :this is the proc argument in invocation 
	 */
	public T visitChannelEndExpr(ChannelEndExpr ce) {
		Log.log(ce.line + ": Visiting a Channel End Expression!");
		//TODO: Figure out what else could be in a ChannelEndExpr.
		String channel = (String) ce.channel().visit(this);
		
		return (T) channel;
	}

	/**
	 * ChannelEndType [cds done]
	 * :this is the type in the proc parameter.
	 */
	public T visitChannelEndType(ChannelEndType ct) {
		Log.log(ct.line + ": Visiting a Channel End Type!");

		return (T) ("Channel<" + getWrapperType(ct.baseType()) + ">");
	}

	/**
	 * ChannelReadExpr
	 */
	public T visitChannelReadExpr(ChannelReadExpr cr) {
		Log.log(cr.line + ": Visiting ChannelReadExpr");
		return (T) createChannelReadExpr(null, cr);
	}

	/**
	 * ChannelWriteStat
	 */
	public T visitChannelWriteStat(ChannelWriteStat cw) {
		Log.log(cw.line + ": Visiting a Channel Write Statement!");
		
		ST template = group.getInstanceOf("ChannelWriteStat");

		NameExpr channelNameExpr = null;
	    /*
	     * Can either be NameExpression (chan.write(x)) or
	     * ChannelEndExpr (chan.write.write(x))
	     */
		Expression channelExpr = cw.channel();

	    if (channelExpr instanceof NameExpr) {
	      channelNameExpr = (NameExpr) channelExpr;
	    } else if (channelExpr instanceof ChannelEndExpr) {
	      channelNameExpr = (NameExpr) ((ChannelEndExpr) channelExpr).channel();
	    }
	    
	    Type myType = null;
	    if (channelNameExpr.myDecl instanceof LocalDecl) {
	        //Figure out type of channel and do appropriate code generation based on this.
	        myType = ((LocalDecl) channelNameExpr.myDecl).type();
	    } else if (channelNameExpr.myDecl instanceof ParamDecl) {
	    	//Figure out type of channel and do appropriate code generation based on this.
	    	myType = ((ParamDecl) channelNameExpr.myDecl).type();
	    }
	    
	    if (myType.isChannelEndType()) {
	    	ChannelEndType chanType = (ChannelEndType) myType;
	        if (chanType.isShared()) {
	            template.add("shared", true);
	         } else {
	        	 template.add("shared", false);
	         }
	    } else if (myType.isChannelType()) {
	    	ChannelType chanType = (ChannelType) myType;
	        if (chanType.shared() == ChannelType.SHARED_WRITE || chanType.shared() == ChannelType.SHARED_READ_WRITE) {
	            template.add("shared", true);
	          } else {
	        	  template.add("shared", false);
	          }
	    }

		String expr = (String) cw.expr().visit(this);
		String channel = (String) channelExpr.visit(this);
		template.add("channel", channel);
		template.add("expr", expr);

		for (int i=0; i<2; i++) {
			template.add("jmp" + i, _jumpCnt);
			_switchCases.add(getLookupSwitchCase(_jumpCnt));	
			_jumpCnt++;
		}

		return (T) template.render();
	}
	
	public String getLookupSwitchCase(int jmp) {
		ST template = null;
		if (jmp==0) {
			template = group.getInstanceOf("LookupSwitchInitialCase");
		} else {
			template = group.getInstanceOf("LookupSwitchCase");
		}
		template.add("caseNum", jmp);
		return template.render();
	}

	/**
	 * Compilation
	 */
	public T visitCompilation(Compilation c) {
		Log.log(c.line + ": Visiting the Compilation");

		ST template = group.getInstanceOf("Compilation");

		/*
		 * We add our function prototypes here as the Java program will need them.
		 */
		Sequence<Type> typeDecls = c.typeDecls();

		/*
		 * Iterate over the sequence only collecting the procType arguments. This is needed to know the name of the last
		 * functions.
		 */
		//		for (Type type : typeDecls) {
		//			if (type instanceof ProcTypeDecl) {
		//				ProcTypeDecl current = (ProcTypeDecl) type;
		//				// TODO look into aux method
		//				String prototypeName = getPrototypeString(current);
		//				prototypes.add(prototypeName);
		//			}
		//		}

		// TODO add the pragmas, packageName and imports later!

		/*
		 * Recurse to all children getting strings needed for this Class template.
		 */
		String[] typeDeclsStr = (String[]) c.typeDecls().visit(this);

		/*
		 * This is where functions are created as they are procedure type.
		 */
		//		template.add("prototypes", prototypes);

		/*
		 * cds: might need to uncomment this to take in account for other Types. this was commented when adding logic to
		 * create individual files for each proc.
		 */
		template.add("typeDecls", typeDeclsStr);
		template.add("packageName", this.originalFilename);

		/*
		 * Finally write the output to a file
		 */
		String finalOutput = template.render();
		writeToFile(finalOutput, this.originalFilename);

		Log.log("Output written to file " + this.originalFilename);

		return (T) finalOutput;
	}

	/**
	 * Assignment
	 */
	public T visitAssignment(Assignment as) {
		Log.log(as.line + ": Visiting an Assignment");

		ST template = group.getInstanceOf("Assignment");
		String left = (String) as.left().visit(this);
		String op = (String) as.opString();

		if(as.right() instanceof ChannelReadExpr) {
			return (T) createChannelReadExpr(left, (ChannelReadExpr)as.right());
		} else if(as.right() instanceof Invocation) {
			return (T) createInvocation(left, (Invocation)as.right(), false);
		} else {
			String right = (String) as.right().visit(this);
			template.add("left", left);
			template.add("right", right);
			template.add("op", op);
		}

		return (T) template.render();
	}
	
	public T createInvocation(String left, Invocation in, boolean isParamInvocation) {
		Log.log(in.line + ": Creating Invocation ("+ in.procedureName().getname() + ") with LHS as " + left);

		ST template = null;
		ProcTypeDecl pd = in.targetProc;
		String qualifiedPkg = getQualifiedPkg(pd);
		String qualifiedProc = qualifiedPkg + "." + in.procedureName().getname();

		List<String> paramLst = new ArrayList<String>();
		List<String> paramBlocks = new ArrayList<String>();

		/*
		 * Generate param code.
		 */
		Sequence<Expression> params = in.params();
		for(int i = 0; i < params.size(); i++) {
			Expression e = params.child(i);
			if (e != null) {
				if (e instanceof ChannelReadExpr) {

					String typeString = in.targetProc.formalParams().child(i).type().typeName();
					String tempVar = globalize("temp", false);
					_gLocals.add(typeString + " " + tempVar);
					String chanReadBlock = (String) createChannelReadExpr(tempVar, (ChannelReadExpr)e);
					
					paramBlocks.add(chanReadBlock);
					paramLst.add(tempVar);

				} else if (e instanceof Invocation){

					String typeString = ((Invocation)e).targetProc.returnType().typeName();
					String tempVar = globalize("temp", false);
					_gLocals.add(typeString + " " + tempVar);
					String invocationBlock = (String) createInvocation(tempVar, (Invocation) e, true);
					
					paramBlocks.add(invocationBlock);
					paramLst.add(tempVar);

				} else {
					paramLst.add((String)e.visit(this));
				}
			}
		}

		template = group.getInstanceOf("InvocationNormal");
		template.add("qualifiedProc", qualifiedProc);
		template.add("procParams", paramLst);
		template.add("par", _inParBlock);
		template.add("parName", _parName);
		
		/*
		 * If target proc (pd) is a yielding proc, ie. it is a process,
		 * instead of normal invocation, we need to instantiate the process
		 * and schedule it.
		 */
		template.add("isProcess", isYieldingProc(pd));
		
		if (left != null || !paramBlocks.isEmpty()) {

			String invocationBlock = template.render();
			template = group.getInstanceOf("InvocationWithChannelReadExprParam");
			
			template.add("paramBlocks", paramBlocks);
			template.add("left", left);
			template.add("right", invocationBlock);
		}
		
		return (T) template.render();
			
	}

	public T createChannelReadExpr(String left, ChannelReadExpr cr) {
		Log.log(cr.line + ": Creating ChannelReadExpr with LHS as " + left);

		ST template = null;
		Expression channelExpr = cr.channel();
		NameExpr channelNameExpr = null;
		/*
		 * Can either be NameExpression (chan.read()) or
		 * ChannelEndExpr (chan.read.read())
		 */
		if (channelExpr instanceof NameExpr) {
			channelNameExpr = (NameExpr) channelExpr;
		} else if (channelExpr instanceof ChannelEndExpr) {
			channelNameExpr = (NameExpr) ((ChannelEndExpr) channelExpr).channel();
		}

		String channel = (String) channelNameExpr.visit(this);
		Type myType = null;

		//Figure out type of channel and do appropriate code generation based on this.
		if (channelNameExpr.myDecl instanceof LocalDecl) {
			myType = ((LocalDecl) channelNameExpr.myDecl).type();
		} else if (channelNameExpr.myDecl instanceof ParamDecl) {
			myType = ((ParamDecl) channelNameExpr.myDecl).type();
		}

		if (myType.isTimerType()) {
			/*
			 * NOTE: for the moment, timer read expr is of type ChannelReadExpr
			 * just because they look the same. So, we have this here. But, 
			 * hopefully in the future, we can have TimerReadExpr and its own
			 * visitor.
			 */
			template = group.getInstanceOf("TimerReadExpr");
			//TODO: complete this.
		} else { 
			Type baseType = null;
			/*
			 * Possibility Two: This is an actual end: chan<type>.read chan,
			 * chan.read()
			 */
			if (myType.isChannelEndType()) {
				ChannelEndType chanType = (ChannelEndType) myType;
				baseType = chanType.baseType();
			} 
			/*
			 * Possibility Three: This is a channel to be treated as an end to avoid
			 * chan.read.read(). 
			 */
			else if (myType.isChannelType()) {
				ChannelType chanType = (ChannelType) myType;
				baseType = chanType.baseType();
			}

			if (baseType.isIntegerType() || baseType.isBooleanType()) {
				
				template = group.getInstanceOf("ChannelReadExpr");
				for (int i=0; i<2; i++) {
					_switchCases.add(getLookupSwitchCase(_jumpCnt));	
					template.add("jmp" + i, _jumpCnt);
					_jumpCnt++;
				}
				template.add("channel", channel);
				template.add("left", left);
			} else {
				String errorMsg = "Unsupported type: %s for Channel!";
				String error = String.format(errorMsg, baseType.toString());
				Error.error(cr, error);
			}
		}

		return (T)template.render();
	}

	/**
	 * BreakStat 
	 * TODO: Add identifier option.
	 */
	public T visitBreakStat(BreakStat bs) {
		Log.log(bs.line + ": Visiting a BreakStat");
		ST template = group.getInstanceOf("BreakStat");
		// Can be null.
		Name name = bs.target();

		if (name != null) {
			String nameStr = (String) name.visit(this);
			// TODO: Add name option here.
		}

		return (T) template.render();
	}

	/**
	 * BinaryExpr
	 */
	public T visitBinaryExpr(BinaryExpr be) {
		Log.log(be.line + ": Visiting a Binary Expression");
		ST template = group.getInstanceOf("BinaryExpr");

		String left = (String) be.left().visit(this);
		String right = (String) be.right().visit(this);
		String op = (String) be.opString();

		// TODO: Add suport for string concatanation here.

		template.add("left", left);
		template.add("right", right);
		template.add("op", op);

		return (T) template.render();
	}

	/**
	 * Block
	 */
	public T visitBlock(Block bl) {
		Log.log(bl.line + ": Visiting a Block");
		String[] statements = (String[]) bl.stats().visit(this);

		return (T) statements;
	}

	/**
	 * CastExpr
	 */
	public T visitCastExpr(CastExpr ce) {
		Log.log(ce.line + ": Visiting a Cast Expression");
		ST template = group.getInstanceOf("CastExpr");
		// No node for type get actual string.
		String ct = ce.type().typeName();
		String expr = (String) ce.expr().visit(this);

		template.add("ct", ct);
		template.add("expr", expr);

		return (T) template.render();
	}

	/**
	 * ContinueStat 
	 * TODO: add identifier option.
	 */
	public T visitContinueStat(ContinueStat cs) {
		Log.log(cs.line + ": Visiting a ContinueStat");

		ST template = group.getInstanceOf("ContinueStat");
		// Can be null.
		Name name = cs.target();

		if (name != null) {
			String nameStr = (String) name.visit(this);
			// TODO: Add name option here.
		}

		return (T) template.render();
	}

	/**
	 * DoStat TODO: I think this will crash if we do: do <oneStat> while(<expr>); Since this does not return a String[]
	 */
	public T visitDoStat(DoStat ds) {
		Log.log(ds.line + ": Visiting a DoStat");

		ST template = group.getInstanceOf("DoStat");
		String[] stats = (String[]) ds.stat().visit(this);
		String expr = (String) ds.expr().visit(this);

		template.add("stat", stats);
		template.add("expr", expr);

		return (T) template.render();
	}

	/**
	 * ExprStat
	 */
	public T visitExprStat(ExprStat es) {
		Log.log(es.line + ": Visiting a ExprStat");

		return (T) es.expr().visit(this);
	}

	/**
	 * ForStat
	 */
	public T visitForStat(ForStat fs) {
		Log.log(fs.line + ": Visiting a ForStat");
		ST template = group.getInstanceOf("ForStat");

		/*
		 * TODO: this will throw nullpointer when for( ; ;) is used. do we support this??
		 */
		String[] initStr = null;
		String[] incrStr = null;

		Sequence<Statement> init = fs.init();

		// Check for null >:(
		if (init != null)
			initStr = (String[]) init.visit(this);

		Sequence<ExprStat> incr = fs.incr();
		if (incr != null)
			incrStr = (String[]) incr.visit(this);

		String expr = (String) fs.expr().visit(this);
		// TODO: Barriers

		// Depending whether there is curly brackets it may return an array, or
		// maybe just
		// a single object. So we must check what it actually is!
		Object stats = fs.stats().visit(this);

		if (stats instanceof String[])
			template.add("stats", (String[]) stats);
		else
			template.add("stats", (String) stats);

		template.add("init", initStr);
		template.add("incr", incrStr);
		template.add("expr", expr);

		return (T) template.render();
	}

	/**
	 * Invocation.
	 */
	public T visitInvocation(Invocation in) {
		Log.log(in.line + ": Visiting Invocation (" + in.procedureName().getname() + ")");
		return (T) createInvocation(null, in, false);
	}

	private String getQualifiedPkg(ProcTypeDecl pd) {
		String myPkg = pd.myPackage;
		StringBuilder qualifiedPkg = new StringBuilder();

		String[] tokens = myPkg.split("\\.");
		boolean pk_start = false;
		for (int i = 0; i < tokens.length; i++) {
			if (tokens[i].equals(this.originalFilename))
				pk_start = true;

			if (pk_start) {
				qualifiedPkg.append(tokens[i]);

				if (i < tokens.length - 1)
					qualifiedPkg.append(".");
			}
		}
		return qualifiedPkg.toString();
	}

	/**
	 * LocalDecl
	 */
	public T visitLocalDecl(LocalDecl ld) {
		Log.log(ld.line + ": Visting LocalDecl (" + ld.type().typeName() + " "
				+ ld.var().name().getname() + ")");

		/*
		 * cds: TODO: should the const keyword with localdecl be taken care of?? 
		 * in java, probably no need as compiler generated code is not going to overwrite any
		 * var whether or not constant.
		 */

		String typeString = (String) ld.type().visit(this);
		
		String name = ld.var().name().getname();
		String gname = globalize(name, false);
		ld.var().name().setName(gname);

		if (_gLocalNamesMap != null) {
			_gLocalNamesMap.put(name, gname);
			_gLocals.add(typeString + " " + ld.var().name().getname());
		}

		ST template = group.getInstanceOf("LocalDecl");
		
		Expression right = ld.var().init();
		if(right instanceof ChannelReadExpr || right instanceof Invocation){
			String assignment = (String)new Assignment(new NameExpr(ld.var().name()), right, Assignment.EQ).visit(this);
			return (T) assignment;
		} else {
			/*
			 * Channels in ProcessJ do not require initialization but in 
			 * Java, we need it. So, the below code makes the grammar template
			 * do it.
			 */
			if (ld.type().isChannelType()) {
				template.add("channelPart", true);
				template.add("type", typeString);
			} else {
				template.add("channelPart", false);
				template.add("type", typeString);
			}
			String var = (String) ld.var().visit(this);
			template.add("var", var);
			template.add("procYields", _proc_yields);
			
			return (T) template.render();
		}
		
	}
	
	/**
	 * IfStat 
	 * TODO: We may want to change where we return either 
	 * a String or a String[] to always return String[] even
	 * if it only has one element.
	 */
	public T visitIfStat(IfStat is) {
		Log.log(is.line + ": Visiting a ifStat");
		ST template = group.getInstanceOf("IfStat");

		String expr = (String) is.expr().visit(this);
		Statement elsePart = is.elsepart();
		Object elsePartStr = null;
		Object thenPart = is.thenpart().visit(this);

		template.add("expr", expr);

		// May be one statement or multiple statements.
		if (thenPart instanceof String[])
			template.add("thenPart", (String[]) thenPart);
		else
			template.add("thenPart", (String) thenPart);

		// May or may not be here!
		if (elsePart != null) {
			elsePartStr = elsePart.visit(this);

			if (elsePartStr instanceof String[])
				template.add("elsePart", (String[]) elsePartStr);
			else
				template.add("elsePart", (String) elsePartStr);
		}

		return (T) template.render();
	}

	/**
	 * Name
	 */
	public T visitName(Name na) {
		Log.log(na.line + ": Visiting a Name");

		String name = na.getname();
		String gname = null;

		if (_gFormalNamesMap != null)
			gname = _gFormalNamesMap.get(name);

		if (_gLocalNamesMap != null && gname == null)
			gname = _gLocalNamesMap.get(name);

		if (gname == null)
			gname = name;

		return (T) gname; // TODO: Fix lower case 'n';
	}

	/**
	 * NameExpr
	 */
	public T visitNameExpr(NameExpr ne) {
		Log.log(ne.line + ": Visiting NameExpr (" + ne.name().getname() + ")");

		return (T) ne.name().visit(this);
	}

	/**
	 * NewArray
	 */
	public T visitNewArray(NewArray ne) {
		Log.log(ne.line + ": Visiting a NewArray!");

		ST template = group.getInstanceOf("NewArray");
		String myType = (String) ne.baseType().visit(this);

		Sequence<Expression> sizeExp = ne.dimsExpr();

		// TODO: Expand to n-dimensional arrays
		String[] sizeString = (String[]) sizeExp.visit(this);

		template.add("type", myType);
		template.add("size", sizeString[0]);

		return (T) template.render();
	}

	/**
	 * NewMobile
	 * 
	 * TODO: cds do it.
	 */

	/**
	 * ParamDecl
	 */
	public T visitParamDecl(ParamDecl pd) {
		// TODO: is constant?
		Log.log(pd.line + ": Visiting a ParamDecl");

		ST template = group.getInstanceOf("ParamDecl");

		String name = pd.paramName().getname();
		String gname = globalize(name, true);

		pd.paramName().setName(name);

		if (_gFormalNamesMap != null) {
			_gFormalNamesMap.put(name, gname);
		}

		name = (String) pd.paramName().visit(this);
		String type = (String) pd.type().visit(this);

		template.add("name", name);
		template.add("type", type);

		return (T) template.render();
	}

	/**
	 * ParBlock
	 */
	public T visitParBlock(ParBlock pb) {
		Log.log(pb.line + ": Visiting a ParBlock");

		ST parBlockTemplate = group.getInstanceOf("ParBlock");

		_inParBlock = true;
		_parName = "par" + ++this._parCnt;

		for (int i=0; i<2; i++) {
			parBlockTemplate.add("jmp" + i, _jumpCnt);
			_switchCases.add(getLookupSwitchCase(_jumpCnt));	
			_jumpCnt++;
		}

		Sequence<Statement> se = pb.stats();
		String[] stats = new String[se.size()];

		for (int k = 0; k < se.size(); k++) {
			String body = null;
			_parSwitchCases = new ArrayList<String>();

			if (se.child(k) != null) {
				if (se.child(k) instanceof ExprStat) {
					ExprStat es = (ExprStat) se.child(k);
					if (es.expr() instanceof Invocation) {
						stats[k] = (String) createInvocation(null, (Invocation) es.expr(), false);
					} else if (es.expr() instanceof ChannelReadExpr) {
						body = (String) createChannelReadExpr(null, (ChannelReadExpr) es.expr());
						
						if (body != null) {
							
							ST template = group.getInstanceOf("ParBlockStat");
							
							if (_parSwitchCases.size() > 0) {
								ST switchTemplate= group.getInstanceOf("LookupSwitchTable");
								switchTemplate.add("cases", _switchCases);
								
								template.add("lookupswitch", switchTemplate.render());
							}
							
							template.add("parName", _parName);
							template.add("body", body);
							
							stats[k] = template.render();
						}
					} else {
						body = (String) se.child(k).visit(this);
						
						if (body != null) {
							
							ST template = group.getInstanceOf("ParBlockStat");
							
							if (_parSwitchCases.size() > 0) {
								ST switchTemplate= group.getInstanceOf("LookupSwitchTable");
								switchTemplate.add("cases", _switchCases);
								
								template.add("lookupswitch", switchTemplate.render());
							}
							
							template.add("parName", _parName);
							template.add("body", body);
							
							stats[k] = template.render();
						}
					}
				} else {
					body = (String) se.child(k).visit(this);
					
					if (body != null) {
						
						ST template = group.getInstanceOf("ParBlockStat");
						
						if (_parSwitchCases.size() > 0) {
							ST switchTemplate= group.getInstanceOf("LookupSwitchTable");
							switchTemplate.add("cases", _switchCases);
							
							template.add("lookupswitch", switchTemplate.render());
						}
						
						template.add("parName", _parName);
						template.add("body", body);
						
						stats[k] = template.render();
					}
				}
			} else {
				body = null;
			}
		}

		parBlockTemplate.add("parCnt", stats.length);
		parBlockTemplate.add("stats", stats);
		parBlockTemplate.add("parName", _parName);
		
		_inParBlock = false;
		return (T) parBlockTemplate.render();
	}

	/**
	 * Auxillary function, given a name it will create the appropriate protoype needed by the equivalent c program. This
	 * is used to create all the function protoypes created from a ParBlock. void <name>(Workspace <globalWsName>)
	 * 
	 * @param name
	 *            : Name of function to create.
	 * @return string of our function.
	 */
	private String getSimplePrototypeString(String name) {
		ST template = group.getInstanceOf("Prototype");

		template.add("name", name);

		return template.render();
	}

	/**
	 * PrimitiveLiteral
	 */
	public T visitPrimitiveLiteral(PrimitiveLiteral li) {
		Log.log(li.line + ": Visiting a Primitive Literal");

		return (T) li.getText();
	}

	/**
	 * PrimitiveType
	 */
	public T visitPrimitiveType(PrimitiveType py) {
		Log.log(py.line + ": Visiting a Primitive Type");

		String typeString = py.toString();
		// Here we list all the primitive types that don't perfectly translate
		// to Java.
		if (py.isStringType() == true)
			typeString = "String";
		if (py.isTimerType() == true)
			typeString = "Timer";
		if (py.isBooleanType() == true)
			typeString = "boolean";
		// TODO: add barrier, timer.
		return (T) typeString;
	}

	/**
	 * ProcTypeDecl
	 */
	public T visitProcTypeDecl(ProcTypeDecl pd) {
		String name = (String) pd.name().visit(this);

		Log.log(pd.line + ": Visiting a Proc Type Decl: " + name);

		ST template = null;

		this._proc_yields = isYieldingProc(pd);

		if ("main".equals(name) && MAIN_SIGNATURE.equals(pd.signature())) {
			template = group.getInstanceOf("ProcTypeDeclToMain");
		} else if (this._proc_yields) {
			template = group.getInstanceOf("ProcTypeDeclToProcess");
		} else {
			template = group.getInstanceOf("ProcTypeDeclToMethod");
		}

		/*
		 * Initializing global var count for new class.
		 */
		_jumpCnt = 0;
		_gvarCnt = 0;
		_parCnt = 0;
		_inParBlock = false;
		_gFormalNamesMap = new HashMap<String, String>();
		_gLocalNamesMap = new HashMap<String, String>();
		_gLocals = new ArrayList<String>();
		_switchCases = new ArrayList<String>();

		
		// TODO: Modifiers?
		Sequence<Modifier> modifiers = pd.modifiers();

		String returnType = (String) pd.returnType().visit(this);
		String[] formals = (String[]) pd.formalParams().visit(this);
		String[] block = (String[]) pd.body().visit(this);
		
		template.add("packageName", this.originalFilename);
		template.add("returnType", returnType);

		template.add("name", name);

		if (formals.length != 0) {
			template.add("formals", formals);
		}
		if (_gFormalNamesMap.values().size() != 0) {
			template.add("formalNames", _gFormalNamesMap.values());
		}
		if (_gLocals.size() != 0) {
			template.add("globals", _gLocals.toArray(new String[_gLocals.size()]));
		}
		template.add("body", block);
		
		if (this._proc_yields && _switchCases.size() > 0) {
			ST switchTemplate= group.getInstanceOf("LookupSwitchTable");
			switchTemplate.add("cases", _switchCases);
			
			template.add("lookupswitch", switchTemplate.render());
		}

		return (T) template.render();
	}

	/**
	 * ReturnStat
	 */
	public T visitReturnStat(ReturnStat rs) {
		Log.log(rs.line + ": Visiting a ReturnStat");

		ST template = group.getInstanceOf("ReturnStat");
		Expression expr = rs.expr();
		String exprStr = "";

		// Can return null so we must check for this!
		if (expr != null) {
			exprStr = (String) expr.visit(this);
			template.add("expr", exprStr);
		}

		return (T) template.render();
	}

	/**
	 * Sequence
	 */
	public T visitSequence(Sequence se) {
		Log.log(se.line + ": Visiting a Sequence");
		String[] returnArray = new String[se.size()];

		// Iterate through all children placing results in array.
		for (int i = 0; i < se.size(); i++)
			if (se.child(i) != null)
				returnArray[i] = (String) se.child(i).visit(this);
			else
				returnArray[i] = null;

		return (T) returnArray;
	}

	/**
	 * SwitchGroup
	 */
	public T visitSwitchGroup(SwitchGroup sg) {
		Log.log(sg.line + ": Visiting a SwitchGroup");

		ST template = group.getInstanceOf("SwitchGroup");
		String[] labels = (String[]) sg.labels().visit(this);
		String[] stmts = (String[]) sg.statements().visit(this);

		template.add("labels", labels);
		template.add("stmts", stmts);

		return (T) template.render();
	}

	/**
	 * SwitchLabel
	 */
	public T visitSwitchLabel(SwitchLabel sl) {
		Log.log(sl.line + ": Visiting a SwitchLabel");

		ST template = group.getInstanceOf("SwitchLabel");
		boolean isDefault = sl.isDefault();

		if (isDefault == false) {
			String constExpr = (String) sl.expr().visit(this);
			template.add("constExpr", constExpr);
		} else {
			template.add("defaultExpr", "default");
		}

		return (T) template.render();
	}

	/**
	 * SwitchStat
	 */
	public T visitSwitchStat(SwitchStat st) {
		Log.log(st.line + ": Visiting a SwitchStat");

		ST template = group.getInstanceOf("SwitchStat");
		String expr = (String) st.expr().visit(this);
		String[] switchGroups = (String[]) st.switchBlocks().visit(this);

		template.add("expr", expr);
		template.add("switchGroups", switchGroups);

		return (T) template.render();
	}

	/**
	 * Ternary
	 */
	public T visitTernary(Ternary te) {
		Log.log(te.line + ": Visiting a Ternary");

		ST template = group.getInstanceOf("Ternary");
		String expr = (String) te.expr().visit(this);
		String trueBranch = (String) te.trueBranch().visit(this);
		String falseBranch = (String) te.falseBranch().visit(this);

		template.add("expr", expr);
		template.add("trueBranch", trueBranch);
		template.add("falseBranch", falseBranch);

		return (T) template.render();
	}

	/**
	 * UnaryPostExpr
	 */
	public T visitUnaryPostExpr(UnaryPostExpr up) {
		Log.log(up.line + ": Visiting a UnaryPostExpr");

		ST template = group.getInstanceOf("UnaryPostExpr");
		String expr = (String) up.expr().visit(this);
		String op = up.opString();

		template.add("expr", expr);
		template.add("op", op);
		return (T) template.render();
	}

	/**
	 * UnaryPreExpr
	 */
	public T visitUnaryPreExpr(UnaryPreExpr up) {
		Log.log(up.line + ": Visiting a UnaryPreExpr");

		ST template = group.getInstanceOf("UnaryPreExpr");
		String expr = (String) up.expr().visit(this);
		String op = up.opString();

		template.add("expr", expr);
		template.add("op", op);

		return (T) template.render();
	}

	/**
	 * Var
	 */
	public T visitVar(Var va) {
		Log.log(va.line + ": Visiting a Var (" + va.name().getname() + ").");

		ST template = group.getInstanceOf("Var");
		String name = (String) va.name().visit(this);
		String exprStr = "";
		Expression expr = va.init();

		/*
		 * This is to get the var name inside
		 * code generated for channel read.
		 */
		if (expr instanceof ChannelReadExpr) {
			/*
			 * TODO 03.25.2016 why is this commented cabel?
			 * remove this if not needed coz seems like this might
			 * ignore channel read expr.
			 */
//			((ChannelReadExpr)expr).varname = name;
		} else {
			template.add("name", name);
		}

		// Expr may be null if the variable is not intialized to anything!
		if (expr != null) {
			exprStr = (String) expr.visit(this);
			template.add("init", exprStr);
		}

		return (T) template.render();
	}

	/**
	 * WhileStat
	 */
	public T visitWhileStat(WhileStat ws) {
		Log.log(ws.line + ": Visiting a WhileStat");

		ST template = group.getInstanceOf("WhileStat");
		String expr = (String) ws.expr().visit(this);
		Object stats = ws.stat().visit(this);

		template.add("expr", expr);

		// May be one element or multiple.
		if (stats instanceof String[])
			template.add("stat", (String[]) stats);
		else
			template.add("stat", (String) stats);

		return (T) template.render();
	}

	/**
	 * Auxillary function, given a protocol it will create the appropriate protoype needed by the equivalent c program.
	 * This is used to create all the function protoypes that need to be declared at the top.
	 */
	private String getPrototypeString(ProcTypeDecl procedure) {
		ST template = group.getInstanceOf("Prototype");
		String name = procedure.name().getname();
		String[] formals = (String[]) procedure.formalParams().visit(this);

		template.add("name", name);
		if (formals != null && formals.length != 0)
			template.add("formals", formals);

		return template.render();
	}

	/**
	 * This function creates an array of {"AltEnableChannel(...)", ... } or {"AltDisableChannel(...)", ... } based on
	 * the boolean passed in using templates.
	 * 
	 * @param altCaseList
	 *            : Sequence of AltCase to create the strings for.
	 * @param enable
	 *            : Whether to create Enables or Disables.
	 * @return : our array of strings.
	 */
	String[] createEnableDisable(Sequence<AltCase> altCaseList, boolean enable) {
		/* These strings decide which template we grab, either the disable or the enable one. */
		String altTimeoutStr = "AltEnableTimeout";
		String altChannelStr = "AltEnableChannel";
		String altSkipStr = "AltEnableSkip";

		if (enable == false) {
			altTimeoutStr = "AltDisableTimeout";
			altChannelStr = "AltDisableChannel";
			altSkipStr = "AltDisableSkip";
		}

		int count = altCaseList.size();
		String[] listOfEnableDisable = new String[count];

		//Iterate over our children making their statements.
		for (int i = 0; i < count; i++) {
			AltCase altCase = altCaseList.child(i);

			if (caseIsTimeout(altCase) == true) {
				ST altTimeoutT = group.getInstanceOf(altTimeoutStr);
				//Get expression from the Timeout:
				TimeoutStat time = (TimeoutStat) altCase.guard().guard();
				String myExprStr = (String) time.delay().visit(this);
				String name = (String) altCase.guard().visit(this);

				altTimeoutT.add("globalWsName", "workspace");
				altTimeoutT.add("number", i);
				altTimeoutT.add("name", name);
				//What we actually do, is we use our time variable to hold the time and then we
				//pass this number in as this is needed by the CCSP API. Therefore this is
				//acutally a compound statement. See the grammarTemplate.stg file for details...
				if (enable == true)
					altTimeoutT.add("time", myExprStr);

				listOfEnableDisable[i] = altTimeoutT.render();
			}
			if (caseIsChannel(altCase) == true) {
				ST altChannelT = group.getInstanceOf(altChannelStr);
				String name = (String) altCase.guard().visit(this);

				altChannelT.add("globalWsName", "workspace");
				altChannelT.add("number", i);
				altChannelT.add("name", name);

				listOfEnableDisable[i] = altChannelT.render();
			}
			if (caseIsSkip(altCase) == true) {
				ST altSkipT = group.getInstanceOf(altSkipStr);

				altSkipT.add("globalWsName", "workspace");
				altSkipT.add("number", i);
				listOfEnableDisable[i] = altSkipT.render();
			}

		}

		return listOfEnableDisable;
	}

	public boolean isYieldingProc(ProcTypeDecl pd) {
		if (pd == null)
			return false;

		return pd.annotations().isDefined("yield")
				&& Boolean.valueOf(pd.annotations().get("yield"));
	}

	/**
	 * Check if given AltCase is a timeout.
	 * 
	 * @param altCase
	 *            : AltCase to check.
	 * @return was this AltCase a timer?
	 */
	boolean caseIsTimeout(AltCase altCase) {
		Statement stmt = altCase.guard().guard();

		if (stmt instanceof TimeoutStat)
			return true;

		return false;
	}

	/**
	 * Check if given AltCase is a Skip.
	 * 
	 * @param altCase
	 *            : AltCase to check.
	 * @return was this AltCase a Skip?
	 */
	boolean caseIsSkip(AltCase altCase) {
		Statement stmt = altCase.guard().guard();

		if (stmt instanceof SkipStat)
			return true;

		return false;
	}

	/**
	 * Check if given AltCase is a Skip.
	 * 
	 * @param altCase
	 *            : AltCase to check.
	 * @return was this AltCase a Skip?
	 */
	boolean caseIsChannel(AltCase altCase) {
		Statement stmt = altCase.guard().guard();

		if (stmt instanceof ExprStat)
			return true;

		return false;
	}

	/**
	 * Returns the wrapper class name of primitive data types.
	 */
	public String getWrapperType(Type t) {

		String typeStr = "";

		if (t.isIntegerType())
			typeStr = "Integer";
		else if (t.isLongType())
			typeStr = "Long";
		else if (t.isFloatType())
			typeStr = "Float";
		else if (t.isDoubleType())
			typeStr = "Double";
		else if (t.isByteType())
			typeStr = "Byte";
		else if (t.isBooleanType())
			typeStr = "Boolean";
		else if (t.isCharType())
			typeStr = "Char";
		else if (t.isShortType())
			typeStr = "Short";

		return typeStr;
	}

	/**
	 * 
	 * @param name
	 * @param formals
	 * @return
	 */
	public String globalize(String name, boolean formals) {
		String gname = "";
		if (name != null && !name.isEmpty()) {
			if (formals) {
				gname = "_pd_" + name;
			} else {
				gname = "_ld" + _gvarCnt++ + "_" + name;
			}
		}
		return gname;
	}

	/**
	 * Given a string it will write to the file as the final output of the compiler. TODO: Should probably figure out a
	 * way to let user specify name of output file. as of now it always writes to "codeGenerated.c"
	 */
	private void writeToFile(String finalOutput, String filename) {
		Writer writer = null;

		try {
			String basePath = "/Users/cabel/Dropbox/github/ProcessJ-Compiler/src/Generated/";
			File pkg = new File(basePath + this.originalFilename);
			if (!pkg.exists())
				pkg.mkdir();

			FileOutputStream fos = new FileOutputStream(pkg.getAbsolutePath()
					+ File.separator + filename + ".java");
			writer = new BufferedWriter(new OutputStreamWriter(fos, "utf-8"));
			writer.write(finalOutput);
			
			
			//TODO try compiling here?
//			JavaCompiler compiler = ToolProvider.getSystemJavaCompiler();
//			
//			if (compiler == null) {
//			    throw new Exception("JDK required (running inside of JRE)");
//			  }
//			
//		       StandardJavaFileManager fileManager = compiler.getStandardFileManager(null, null, null);
//
//		       Iterable<? extends JavaFileObject> compilationUnits1 =
//		           fileManager.getJavaFileObjectsFromFiles(Arrays.asList(files1));
//		       compiler.getTask(null, fileManager, null, null, null, compilationUnits1).call();
//		       
		} catch (IOException ex) {
			Log.log("IOException: Could not write to file for some reason :/");
		} finally {
			try {
				writer.close();
			} catch (Exception ex) {
				Log.log("Could not close file handle!");
			}
		}

		return;
	}
}
