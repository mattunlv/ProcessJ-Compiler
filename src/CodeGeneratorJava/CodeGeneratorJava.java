package CodeGeneratorJava;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStreamWriter;
import java.io.Writer;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.stringtemplate.v4.ST;
import org.stringtemplate.v4.STGroup;
import org.stringtemplate.v4.STGroupFile;

import AST.AST;
import AST.AltCase;
import AST.AltStat;
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
import AST.ExprStat;
import AST.Expression;
import AST.ForStat;
import AST.IfStat;
import AST.Invocation;
import AST.LocalDecl;
import AST.Name;
import AST.NameExpr;
import AST.NamedType;
import AST.NewArray;
import AST.ParBlock;
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
import AST.SkipStat;
import AST.Statement;
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
import AST.WhileStat;
import Utilities.Log;
import Utilities.SymbolTable;
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
	private List<String> _parforSwitchCases = null;
	private SymbolTable _topLevelDecls = null;
	private int _gvarCnt = 0;
	private int _jumpCnt = 1;
	private int _parCnt = 0;
	private boolean _inParBlock = false;
	private String _parName = null;
	private String _currentProcName = null;

	private boolean _proc_yields = false;
	private boolean _foreverLoop = false;
	private final String MAIN_SIGNATURE = "([T;)V";

	//TODO Cabel look at trying to get this name from Error.filename or from compilation.
	private String originalFilename = null;

	public CodeGeneratorJava(SymbolTable topLevelDecls) {
		Log.log("==========================================");
		Log.log("* C O D E   G E N E R A T O R  ( Java )  *");
		Log.log("*        F I R S T  P A S S              *");
		Log.log("=========================================");

		/*
		 * Load StringTemplate grammar template.
		 */
		this.group = new STGroupFile(grammarStFile);
		this._topLevelDecls = topLevelDecls;
	}

	public void setOriginalFilename(String n) {
		this.originalFilename = n;
	}

	/*
	 * All ALT items
	 */
	
	boolean _inAlt = false;
	
	public T visitAltCase(AltCase ac) {
		Log.log(ac.line + ": Visiting an AltCase");
		ST template = group.getInstanceOf("AltCase");
		
		Statement caseExprStmt = ac.guard().guard();
		Statement stat = ac.stat();
		String caseExprStr;

		
		return (T) template.render();
	}

	public T visitAltStat(AltStat as) {
		Log.log(as.line + ": Visiting an AltStat");
		
		this._inAlt = true;

		ST template = group.getInstanceOf("AltStat");

		Sequence<AltCase> altCaseList = as.body();
		int caseCount = altCaseList.size();
		
		/*
		 * Creating boolean guards array. Refactor this.
		 */
		ST altBGArrTemplate = group.getInstanceOf("AltBooleanGuardsArr");
		String[] constants = new String[caseCount];
		List<String> tempExprs = new ArrayList<String>(); 
		for(int i=0; i < caseCount; i++) {
			AltCase ac = altCaseList.child(i);
			if (ac.precondition() == null) {
				constants[i] = String.valueOf(true);
			} else if (ac.precondition().isConstant()) {
				constants[i] = (String)ac.precondition().visit(this);
			} else {
				String tempVar = "bTemp" + i;
				Name n = new Name(tempVar);
				tempExprs.add((String)new LocalDecl(new PrimitiveType(PrimitiveType.BooleanKind), new Var(n, ac.precondition()), false).visit(this));
				constants[i] = (String)n.visit(this); 
			}
		}
		altBGArrTemplate.add("tempExprs", tempExprs);
		altBGArrTemplate.add("constants", constants);
		//------------------------------------------------------
		
		/*
		 * Creating actual guards array
		 */
		ST altGuardArrTemplate = group.getInstanceOf("AltGuardsArr");
		String[] guards = new String[caseCount];	
		List<String> timers = new ArrayList<String>();
		List<String> timerNames = new ArrayList<String>();
		List<String> altCases = new ArrayList<String>();
		
		List<String> statementList = null;

		for(int i=0; i < caseCount; i++) {
			
			ST altCase = group.getInstanceOf("AltCase");

			statementList = new ArrayList<String>();
			AltCase ac = altCaseList.child(i);
			Statement caseExprStmt = ac.guard().guard();

			if (caseExprStmt instanceof TimeoutStat) {
				TimeoutStat ts = (TimeoutStat) caseExprStmt;
				String tn = (String)ts.timer().visit(this);//name of timer
				guards[i] = tn;
				timerNames.add(tn);

				timers.add((String)ts.visit(this));
				
				statementList.addAll(Arrays.asList((String[]) ac.stat().visit(this))); //the statements in the timer

			} else if (caseExprStmt instanceof ExprStat){ //Channel
				Expression e = ((ExprStat) caseExprStmt).expr();
				ChannelReadExpr cr = null;
				if (e instanceof ChannelReadExpr) {
					cr = (ChannelReadExpr) e;
				} else if (e instanceof Assignment) {
					cr = (ChannelReadExpr)((Assignment)e).right();
				}
				guards[i] = (String) cr.channel().visit(this); //name of channel
				
				String chanRead = (String)caseExprStmt.visit(this);
				statementList.add(chanRead);
				statementList.addAll(Arrays.asList((String[]) ac.stat().visit(this))); //the statements in the skip 
				
			} else if (caseExprStmt instanceof SkipStat){
				guards[i] = "Alt.SKIP_GUARD";
				
				statementList.addAll(Arrays.asList((String[]) ac.stat().visit(this))); //the statements in the skip 
			} else {
				System.out.println("Unknown statement in guards!!!");
			}	
			
			
			altCase.add("number", i);
			altCase.add("statementList", statementList);
			
			altCases.add(altCase.render());
		}
		
		/*
		 * Collecting timers and generating start and kill code
		 */
		List<String> timerStarts = new ArrayList<String>();
		List<String> timerKills = new ArrayList<String>();
		for (String t : timerNames) {
			ST timerStart = group.getInstanceOf("AltTimerStart");
			timerStart.add("timer", t);

			timerStarts.add(timerStart.render());
			
			ST timerKill = group.getInstanceOf("AltTimerKill");
			timerKill.add("timer", t);
			
			timerKills.add(timerKill.render());
		}	
		//----------------
		
		/*
		 * Creating timer start cases
		 */
		if ( timerStarts.size() > 0) {
			ST altCase = group.getInstanceOf("AltCase");
			altCase.add("number", -1);
			altCase.add("statementList", timerStarts);
			altCases.add(altCase.render());
		}
		//---------------
		
		
		ST altSwitchGroup = group.getInstanceOf("AltSwitchGroup");
		altSwitchGroup.add("cases", altCases);

		if (guards.length > 0) {
			altGuardArrTemplate.add("guards", guards);
		}

		//--------------------
		
		template.add("timers", timers);
		template.add("caseCount", caseCount);
		template.add("altSwitchGroup", altSwitchGroup.render());
		template.add("initBoolGuards", altBGArrTemplate.render());
		template.add("initGuards", altGuardArrTemplate.render());
		template.add("timerKills", timerKills);

		template.add("jmp1", _jumpCnt);
		_switchCases.add(getLookupSwitchCase(_jumpCnt));	
		_jumpCnt++;
		
		template.add("jmp2", _jumpCnt);
		_switchCases.add(getLookupSwitchCase(_jumpCnt));	
		_jumpCnt++;
		
		this._inAlt = false;

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
	 * TODO: Just the base method for now as a placeholder.
	 * What to do about this?
	 */
	public T visitArrayLiteral(ArrayLiteral al) {
		return al.visitChildren(this);
	}

	/**
	 * ArrayType
	 */
	public T visitArrayType(ArrayType at) {
		Log.log(at.line + ": Visiting an ArrayType!");
		String baseType = (String) at.baseType().visit(this);
		return (T) (baseType + "[]");
	}

	/**
	 * ChannelType
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
		
		Type t = ct.baseType();
		String type = null;
		if (t.isNamedType()) {
			type = (String)((NamedType) t).visit(this);
		} else {
			//FIXME maybe do a check for primitive type...and rename the below method.
			type = getWrapperType(t);
		}

//		return (T) (typeString + "<" + getWrapperType(ct.baseType()) + ">");
		return (T) (typeString + "<" + type + ">");
	}

	/**
	 * ChannelEndExpr
	 */
	public T visitChannelEndExpr(ChannelEndExpr ce) {
		Log.log(ce.line + ": Visiting a Channel End Expression!");
		//TODO: Figure out what else could be in a ChannelEndExpr.
		String channel = (String) ce.channel().visit(this);
		
		return (T) channel;
	}

	/**
	 * ChannelEndType
	 */
	public T visitChannelEndType(ChannelEndType ct) {
		Log.log(ct.line + ": Visiting a Channel End Type!");

		Type t = ct.baseType();
		String type = null;
		if (t.isNamedType()) {
			type = (String)((NamedType) t).visit(this);
		} else {
			//FIXME maybe do a check for primitive type...and rename the below method.
			type = getWrapperType(t);
		}

//		return (T) (typeString + "<" + getWrapperType(ct.baseType()) + ">");
		return (T) ("Channel<" + type + ">");
		
//		return (T) ("Channel<" + getWrapperType(ct.baseType()) + ">");
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
	        myType = ((LocalDecl) channelNameExpr.myDecl).type();
	    } else if (channelNameExpr.myDecl instanceof ParamDecl) {
	    	myType = ((ParamDecl) channelNameExpr.myDecl).type();
	    }
	    
	    if (myType.isChannelEndType()) {
	    	ChannelEndType chanType = (ChannelEndType) myType;
	    	template.add("shared", chanType.isShared());
//	        if (chanType.isShared()) {
//	            template.add("shared", true);
//	         } else {
//	        	 template.add("shared", false);
//	         }
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

		/*
		 * Adding switch cases for resumption.
		 */
		for (int i=0; i<2; i++) {
			if (_inParBlock) {
				_parSwitchCases.add(getLookupSwitchCase(_jumpCnt));	
			} else {
				_switchCases.add(getLookupSwitchCase(_jumpCnt));	
			}
			template.add("jmp" + i, _jumpCnt);
			_jumpCnt++;
		}
		
		return (T) template.render();
	}
	
	/**
	 * Compilation
	 * TODO add the pragmas, packageName and imports later!
	 */
	public T visitCompilation(Compilation c) {
		Log.log(c.line + ": Visiting the Compilation");

		ST template = group.getInstanceOf("Compilation");

		/*
		 * Visiting type decls will be done in ordered sequence
		 * of RecordTypeDecls, ProtocolTypeDecls and ProcTypeDecls
		 * so that forward-referencing for NamedType can be done.
		 */
		List<String> typeDeclsStr = new ArrayList<String>();
		Sequence<Type> individualDecls = new Sequence<Type>();
		Sequence<Type> extendedDecls = new Sequence<Type>();
		
		Sequence<Type> typeDecls = c.typeDecls();

		/*
		 * Collect RecordTypeDecls.
		 */
		Iterator<Type> it1 = typeDecls.iterator();
		while(it1.hasNext()) {
			AST temp = it1.next();
			if (temp instanceof ConstantDecl)
				continue;
//			Type type = (Type)it1.next();
			Type type = (Type)temp;
			if (type instanceof RecordTypeDecl) {
				RecordTypeDecl rd = (RecordTypeDecl) type;
				if( rd.extend() == null || rd.extend().size() == 0) {
					individualDecls.append(rd);
				} else {
					extendedDecls.append(rd);
				}
				it1.remove();
			}
		}
		
		/*
		 * Visit non-extended and then extended records.
		 */
		Collections.sort((List<Type>) extendedDecls.children, new Comparator<Type>() {
			@Override
			public int compare(Type o1, Type o2) {
				return ((RecordTypeDecl)o1).extend().size() - ((RecordTypeDecl)o2).extend().size();
			}
		});	
		typeDeclsStr.addAll(Arrays.asList((String[])individualDecls.visit(this)));
		typeDeclsStr.addAll(Arrays.asList((String[])extendedDecls.visit(this)));

		individualDecls.clear();
		extendedDecls.clear();

		/*
		 * Collect ProtocolTypeDecls.
		 */
		Iterator<Type> it2 = typeDecls.iterator();
		while(it2.hasNext()) {
			AST temp = it2.next();
			if (temp instanceof ConstantDecl)
				continue;
//			Type type = (Type)it2.next();
			Type type = (Type)temp;
			if (type instanceof ProtocolTypeDecl) {
				ProtocolTypeDecl pd = (ProtocolTypeDecl) type;
				if( pd.extend() == null || pd.extend().size() == 0) {
					individualDecls.append(pd);
				} else {
					extendedDecls.append(pd);
				}
				it2.remove();
			}
		}

		/*
		 * Visit non-extended and then extended protocols.
		 */
		Collections.sort((List<Type>) extendedDecls.children, new Comparator<Type>() {
			@Override
			public int compare(Type o1, Type o2) {
				return ((ProtocolTypeDecl)o1).extend().size() - ((ProtocolTypeDecl)o2).extend().size();
			}
		});
		
		typeDeclsStr.addAll(Arrays.asList((String[])individualDecls.visit(this)));
		typeDeclsStr.addAll(Arrays.asList((String[])extendedDecls.visit(this)));
		
		/*
		 * Visit remaining items on the list which are all ProcTypeDecls.
		 */
		typeDeclsStr.addAll(Arrays.asList((String[])typeDecls.visit(this)));

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
	
	public T visitClaimStat(ClaimStat cs) {
		Log.log(cs.line + ": Visiting an ClaimStat");
		ST template = null;
		
//		Sequence<AST> channels = cs.channels();

		List<String> claimparts = new ArrayList<String>();
		String[] channels = (String[])cs.channels().visit(this);
		List<String> claimed = new ArrayList<String>();
//		for(int i=0; i<channels.length; i++) {
//			template = group.getInstanceOf("ClaimStatClaimPart");
//			
//			String c = channels[i];
//
//			template.add("unclaims", claimed);
//			template.add("c", c);
//			template.add("jmp", _jumpCnt);
//			_switchCases.add(getLookupSwitchCase(_jumpCnt));	
//			_jumpCnt++;
//			claimparts.add(template.render());
//
//			//list of channels that next channel needs
//			//to unclaim if that channel claim fails.
//			claimed.add(c);
//		}

//		String stat = (String)cs.stat().visit(this);
		
		
		Object stats = null;
		if (cs.stat() instanceof Block) {
			Block b = (Block) cs.stat();
			stats = (String[]) b.visit(this);
		} else {
			stats = (String) cs.stat().visit(this);
		}
		
		template = group.getInstanceOf("ClaimStat");

		template.add("channels", channels);
		template.add("stats", stats);
		
		template.add("jmp", _jumpCnt);
		_switchCases.add(getLookupSwitchCase(_jumpCnt));	
		_jumpCnt++;
		

		return (T) template.render();
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
		return createInvocation(left, in, null, isParamInvocation);
	}

	public T createInvocation(String left, Invocation in, List<String> barriers, boolean isParamInvocation) {
		String invocationProcName = in.procedureName().getname();
		Log.log(in.line + ": Creating Invocation ("+ invocationProcName + ") with LHS as " + left);

	    ST template = null;
	    ProcTypeDecl pd = in.targetProc;
	    String qualifiedPkg = getQualifiedPkg(pd, invocationProcName);
	    String qualifiedProc = qualifiedPkg + "." + in.procedureName().getname();

		List<String> paramLst = new ArrayList<String>();
		List<String> paramBlocks = new ArrayList<String>();

		boolean yields = isYieldingProc(pd);
		/*
		 * For any invocation of yielding proc that's not inside
		 * par block, we will, as inefficient as it, wrap it in
		 * a par that has single exprstat. By cheating this way,
		 * we are able to make all recursions work out of the box
		 * at the expense of spawning new process for each recursive
		 * call. There is a better way of doing it by wrapping all
		 * recursive calls in a procedure block and running them in
		 * the same thread as the caller process. Which also means
		 * that all the procedure has to yield on the same 'ready' 
		 * flag of the caller process. It's a bit tricky. So, if you 
		 * are the next guy doing this, it is onto you. :P 
		 */
		if (yields && !_inParBlock) {
			return (new ParBlock(new Sequence(new ExprStat(in)), new Sequence())).visit(this);
		}
		
		boolean emptyBarriers = (barriers == null);
		
		/*
		 * Generate param code.
		 */
		Sequence<Expression> params = in.params();
		for(int i = 0; i < params.size(); i++) {
			
			if (emptyBarriers)
				barriers = new ArrayList<String>();

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
					String name = (String) e.visit(this);
					paramLst.add(name);
					
					if (e instanceof NameExpr) {
						NameExpr ne = (NameExpr) e;
					
						Type t = null;

						if (ne.myDecl instanceof LocalDecl) {
							t = ((LocalDecl) ne.myDecl).type();
						} else if (ne.myDecl instanceof ParamDecl) {
							t = ((ParamDecl) ne.myDecl).type();
						}
						
						if (emptyBarriers) {
							if (t.isBarrierType()) {
								barriers.add(name);
							}
							
						}
						
					}
				}
			}
		}

		template = group.getInstanceOf("InvocationNormal");
		template.add("qualifiedProc", qualifiedProc);
		template.add("procParams", paramLst);
		template.add("par", _inParBlock);
		template.add("parName", _parName);
		
		template.add("barriers", barriers);
		
		
		/*
		 * If target proc (pd) is a yielding proc, ie. it is a process,
		 * instead of normal invocation, we need to instantiate the process
		 * and schedule it.
		 */
		template.add("isProcess", yields);
		
		if (left != null || !paramBlocks.isEmpty()) {

			String invocationBlock = template.render();
			//TODO change name of template as this is true for any invocation with
			//another invocation as params.
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

		if (channelNameExpr.myDecl instanceof LocalDecl) {
			myType = ((LocalDecl) channelNameExpr.myDecl).type();
		} else if (channelNameExpr.myDecl instanceof ParamDecl) {
			myType = ((ParamDecl) channelNameExpr.myDecl).type();
		}

		if (myType.isTimerType()) {
			/*
			 * Possibility One: Timer
			 * 
			 * NOTE: for the moment, timer read expr is of type ChannelReadExpr
			 * just because they are very similar. But, if need be, we can have
			 * TimerReadExpr and its own visitor in the future.
			 */
			template = group.getInstanceOf("TimerReadExpr");
			template.add("left", left);
			template.add("timer", channelNameExpr.visit(this));
			System.out.println("Found Timer Read Expression!!!");
			return (T) template.render();
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

//			if (baseType.isIntegerType() || baseType.isBooleanType() || baseType.isProtocolType()) {
				
				//EXTENDED RENDEZVOUS
				Block b = cr.extRV();
				String[] extRv = null;
				if (b != null) {
					extRv = (String[])b.visit(this);
//					System.out.println(Arrays.asList(extRv));
				}
				//-------

				if (extRv == null) {
					template = group.getInstanceOf("ChannelReadExpr");
				} else {
					template = group.getInstanceOf("ChannelReadExprExtRv");
					template.add("extRv", extRv);
				}
				
				//Since channel read in Alts are handled differently, i.e. w/o
				//yields in the generated code but rather in Alt class,
				//we don't want to increment and add jumpCnts to runlabel switch.
				if (!_inAlt) {
					/*
				     * Adding switch cases for resumption.
				     */
					for (int i=0; i<2; i++) {
						if (_inParBlock && !inParFor) {
							_parSwitchCases.add(getLookupSwitchCase(_jumpCnt));	
						} else if (inParFor){
							_parforSwitchCases.add(getLookupSwitchCase(_jumpCnt));	
						} else {
							_switchCases.add(getLookupSwitchCase(_jumpCnt));	
						}
						template.add("jmp" + i, _jumpCnt);
						_jumpCnt++;
					}
				}

				template.add("channel", channel);
				template.add("left", left);
				template.add("alt", _inAlt);

//			} else {
//				String errorMsg = "Unsupported type: %s for Channel!";
//				String error = String.format(errorMsg, baseType.toString());
//				Error.error(cr, error);
//			}
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

		// TODO: Add support for string concatanation here.

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
	 * DoStat 
	 * 
	 * TODO: I think this will crash if we do: do <oneStat> while(<expr>); 
	 * Since this does not return a String[]
	 * 
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

	boolean inParFor = false;
	/**
	 * ForStat
	 */
	public T visitForStat(ForStat fs) {
		Log.log(fs.line + ": Visiting a ForStat");
		
		ST template = group.getInstanceOf("ForStat");

		String[] initStr = null;
		String[] incrStr = null;
		String expr = null;

		//init can be null
		Sequence<Statement> init = fs.init();
		if (init != null) {
			initStr = (String[]) init.visit(this);
		}

		//incr can be null
		Sequence<ExprStat> incr = fs.incr();
		if (incr != null) {
			incrStr = (String[]) incr.visit(this);
		}

		//expr can be null
		Expression e = fs.expr();
		if (e != null) {
			expr = (String) fs.expr().visit(this);
		}

		// TODO: Barriers
		Sequence<Expression> bs = fs.barriers();
		String[] barriers = null;
		if (bs != null) {
			barriers = (String[]) bs.visit(this);
		}
		//----------------

		if (fs.isPar()) {
			inParFor = true;

			template = group.getInstanceOf("ParForStat");
			
			_parforSwitchCases = new ArrayList<String>();
			
			Object stats = null;
			if (fs.stats() instanceof Block) {
				Block b = (Block) fs.stats();
				stats = (String[]) b.visit(this);
			} else {
				stats = (String) fs.stats().visit(this);
			}

			String parName = "par" + ++this._parCnt;
			if (stats != null) {
				ST bodyTemplate = group.getInstanceOf("AnonymousProcess");
				bodyTemplate.add("parName", parName);
				bodyTemplate.add("lookupswitch", makeLookupSwitch(_parforSwitchCases));
				bodyTemplate.add("body", stats);
				bodyTemplate.add("parfor", true);

				//TODO: fix for empty forstat block
				template.add("stats", bodyTemplate.render());
			}

			template.add("parName", parName);
			template.add("barriers", barriers);
			
			/*
			 * Adding resumption points
			 */
			for (int i=0; i<2; i++) {
				template.add("jmp" + i, _jumpCnt);
				_switchCases.add(getLookupSwitchCase(_jumpCnt));	
				_jumpCnt++;
			}
			
			inParFor = false;

		} else {
			template = group.getInstanceOf("ForStat");
			/*
			 * Depending whether there is curly brackets (block) it
			 * may return an array, or maybe just a single object. 
			 */
			Object stats = fs.stats().visit(this);

			if (stats instanceof String[])
				template.add("stats", (String[]) stats);
			else
				template.add("stats", (String) stats);
		}
		

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

	public void modifyLocalDeclName(Name name) {
		String modified = globalize(name.getname(), false);
		_gLocalNamesMap.put(name.getname(), modified);
		name.setName(modified);
	}
	
	//public LocalDecl(Type type, Var var, boolean constant) {
	public T visitConstantDecl(ConstantDecl cd) {
		Log.log(cd.line + ": Visting ConstantDecl (" + cd.type().typeName() + " "
				+ cd.var().name().getname() + ")");
		
		ST template = group.getInstanceOf("ConstantDecl");
		
		template.add("type", cd.type().visit(this));
		template.add("var", cd.var().visit(this));
//		return (T)(new LocalDecl(cd.type(), cd.var(), true)).visit(this);
		
		return (T) template.render();
	}

	/**
	 * LocalDecl
	 */
	public T visitLocalDecl(LocalDecl ld) {
		Log.log(ld.line + ": Visting LocalDecl (" + ld.type().typeName() + " "
				+ ld.var().name().getname() + ")");

		modifyLocalDeclName(ld.var().name());
		
		String typeString = (String) ld.type().visit(this);
		String name = (String)ld.var().name().visit(this);

		_gLocals.add(typeString + " " + name);

		ST template = null;
		if (_proc_yields) {
			template = group.getInstanceOf("LocalDeclYieldingProc");
		} else {
			/*
			 * There's really no need to set chanType and barrierType for
			 * this as the use of those would make the proc a yielding one.
			 * But we have it anyway just to accommodate any declaration
			 * that might be put in by the pjammers.
			 */
			template = group.getInstanceOf("LocalDeclNormalProc");
		}
		
		/*
		 * We want to handle channel reads and invocations
		 * differently as they will have extra code for
		 * yields before the actual declaration/assignment.
		 */
		Expression right = ld.var().init();

		if(right instanceof ChannelReadExpr || right instanceof Invocation){
			String assignment = (String)new Assignment(new NameExpr(ld.var().name()), right, Assignment.EQ).visit(this);
			return (T) assignment;
		} else {
			/*
			 * Resolve type. 
			 */
			Type t = ld.type();
			
			boolean isProtocolOrRecordType = false;
			if (t instanceof NamedType) { //Protocol or Record type
				isProtocolOrRecordType = isProtocolOrRecordType((NamedType)t);
			}
			
			boolean barrierType = false;
			if (t instanceof PrimitiveType && t.isBarrierType()) {
				barrierType = true;
			}
			
			boolean channelType = t.isChannelType();

			template.add("chanType", channelType);
			template.add("protoOrRecType", isProtocolOrRecordType);
			template.add("barrierType", barrierType);
			template.add("rEmpty", (ld.var().init() == null));

			String var = (String) ld.var().visit(this);
			template.add("var", var);
			template.add("typeStr", typeString);
			
			return (T) template.render();
		}
		
	}
	
	public boolean isProtocolOrRecordType(NamedType nt) {
		return _gProtoNameMap.containsKey(nt.name().getname()) 
				|| _gRecordNameMap.containsKey(nt.name().getname());
	}
	
	/**
	 * IfStat 
	 * TODO: We may want to change where we return either 
	 * a String or a String[] to always return String[] even
	 * if it only has one element.
	 */
	public T visitIfStat(IfStat is) {
		Log.log(is.line + ": Visiting a IfStat");
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
		Log.log(na.line + ": Visiting a Name (" + na.getname() + ")");

		String name = na.getname();
		String gname = null;

		if (_gFormalNamesMap != null)
			gname = _gFormalNamesMap.get(name);

		if (_gLocalNamesMap != null && gname == null)
			gname = _gLocalNamesMap.get(name);
		
		if(_gProtoNameMap != null && gname == null)
			gname = _gProtoNameMap.get(name);

		if(_gRecordNameMap != null && gname == null)
			gname = _gRecordNameMap.get(name);

		if (gname == null)
			gname = name;

//		System.out.println("name=" + name + " || gname=" + gname);

		return (T) gname;
	}
	
	public T visitNamedType(NamedType nt) {
		Log.log(nt.line + ": Visiting NamedType (" + nt.name().getname() + ")");
		
		return (T) nt.name().visit(this);
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
		Log.log(ne.line + ": Visiting a NewArray");

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

		 /*
	     * Adding switch cases for Par resumption.
	     */ 
		for (int i=0; i<2; i++) {
			parBlockTemplate.add("jmp" + i, _jumpCnt);
			if (inParFor) {
				_parforSwitchCases.add(getLookupSwitchCase(_jumpCnt));	
			} else {
				_switchCases.add(getLookupSwitchCase(_jumpCnt));	
			}
			_jumpCnt++;
		}	
		

		//BARRIER ------------------
		List<String> barriers = null;

		Sequence<Expression> bs = pb.barriers();
		if (bs != null) {
			barriers = new ArrayList<String>();
			barriers.addAll(Arrays.asList((String[]) bs.visit(this)));
		}
		
		Sequence<Statement> se = pb.stats();
		String[] stats = new String[se.size()];

		for (int k = 0; k < se.size(); k++) {
			Object body = null;
			_parSwitchCases = new ArrayList<String>();

			Statement st = se.child(k);

			if (st != null) {
				if (st instanceof ExprStat && ((ExprStat) st).expr() instanceof Invocation) {

					ExprStat es = (ExprStat) st; 
					
					stats[k] = (String) createInvocation(null, (Invocation) es.expr(), barriers, false);
					
				} else {
					if (st instanceof ExprStat && ((ExprStat) st).expr() instanceof ChannelReadExpr) {
						ExprStat es = (ExprStat) st;
						body = createChannelReadExpr(null, (ChannelReadExpr) es.expr());
					} else {
						body = st.visit(this);
					}
					
					if (body != null) {
						
						ST template = group.getInstanceOf("AnonymousProcess");
						
						/*
					     * Creating switch table for resumption.
					     */
						if (_parSwitchCases.size() > 0) {
							template.add("lookupswitch", makeLookupSwitch(_parSwitchCases));
						}
						
						/*
						 * Depending on if it is a single statement or
						 * block, body could be String or String Array.
						 */
						if (body instanceof String[]) {
							template.add("body", (String[]) body);
						} else {
							template.add("body", (String) body);
						}
						
						template.add("parName", _parName);
						template.add("barriers", barriers);
							
						stats[k] = template.render();
					}
				}
			}
		}

		parBlockTemplate.add("parCnt", stats.length);
		parBlockTemplate.add("stats", stats);
		parBlockTemplate.add("barriers", barriers);
		parBlockTemplate.add("parName", _parName);
		
		_inParBlock = false;
		return (T) parBlockTemplate.render();
	}

	public String makeLookupSwitch(List<String> cases) {
		ST switchTemplate= group.getInstanceOf("LookupSwitchTable");
		switchTemplate.add("cases", cases);
		return switchTemplate.render();
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
		/*
		 * Here we list all the primitive types 
		 * that don't perfectly translate to Java.
		 */
		if (py.isStringType()) {
			typeString = "String";
		} else if (py.isTimerType()) {
			typeString = "Timer";
		} else if (py.isBarrierType()) {
			typeString = "Barrier";
		}
		return (T) typeString;
	}

	/**
	 * ProcTypeDecl
	 */
	public T visitProcTypeDecl(ProcTypeDecl pd) {
		this._currentProcName = (String) pd.name().visit(this);

		Log.log(pd.line + ": Visiting a ProcTypeDecl **(" + _currentProcName + ")**");

		/*
		 * Initializing global var count for new class.
		 */
		this._proc_yields = isYieldingProc(pd);
		this._jumpCnt = 1;
		this._gvarCnt = 0;
		this._parCnt = 0;
		this._inParBlock = false;
		this._foreverLoop = false;
		this._gFormalNamesMap = new HashMap<String, String>();
		this._gLocalNamesMap = new HashMap<String, String>();
		this._gLocals = new ArrayList<String>();
		this._switchCases = new ArrayList<String>();

		String qualifiedPkg = getQualifiedPkg(pd, null);
		String qualifiedProc = qualifiedPkg + "." + _currentProcName;

		ST template = null;
		
		if (this._proc_yields) {
			template = group.getInstanceOf("ProcTypeDeclToProcess");
		} else {
			template = group.getInstanceOf("ProcTypeDeclToMethod");
		}
		
		String returnType = (String) pd.returnType().visit(this);
		String[] formals = (String[]) pd.formalParams().visit(this);
		String[] block = (String[]) pd.body().visit(this);
		
		Statement lastStat = null;
		if (pd.body().stats().size() > 0) {
			lastStat = pd.body().stats().child(pd.body().stats().size()-1);
		}
		if (lastStat != null && lastStat instanceof ReturnStat) {
			template.add("retstatFound", true);
		}
		
		template.add("packageName", this.originalFilename);
		template.add("returnType", returnType);

		template.add("name", _currentProcName);

		if (formals.length != 0) {
			template.add("formals", formals);
		}
		if (_gFormalNamesMap.values().size() != 0) {
			template.add("formalNames", _gFormalNamesMap.values());
		}
		if (_gLocals.size() != 0) {
//			System.out.println("------------- gLocals ----------------");
//			System.out.println(_gLocals);
			template.add("globals", _gLocals.toArray(new String[_gLocals.size()]));
		}
		template.add("body", block);
		
		boolean isMain = "main".equals(_currentProcName) && MAIN_SIGNATURE.equals(pd.signature());
		if (isMain && this._proc_yields) {
			ST mainTemplate = group.getInstanceOf("ProcTypeDeclToMain");
			mainTemplate.add("qualifiedProc", qualifiedProc);
			
			template.add("mainMethod", mainTemplate.render());
		}
		
		/*
		 * FIXME: figure out that proc_yields check is needed. if
		 * needed figure out if it is needed in other areas.
		 * 
	     * Creating switch table for resumption.
	     */
		if (this._proc_yields && _switchCases.size() > 0) {
			ST switchTemplate= group.getInstanceOf("LookupSwitchTable");
			switchTemplate.add("cases", _switchCases);
			
			template.add("lookupswitch", switchTemplate.render());
		}
		
		template.add("foreverloop", _foreverLoop);

		return (T) template.render();
	}
	
	//--------------------------------------------------------------------------
	/**
	 * Protocol Literal
	 */
	public T visitProtocolLiteral(ProtocolLiteral pl) {
		Log.log(pl.line + ": Visiting a ProtocolLiteral");
		ST template = group.getInstanceOf("ProtocolLiteral");
		
		String protocolName = (String) pl.name().visit(this);
		String tag = (String)pl.tag().visit(this);
		String tagName = _gProtoCaseNameMap.get(tag);
		String[] params = (String[]) pl.expressions().visit(this);

		template.add("protocolName", protocolName);
		template.add("tagName", tagName);
		template.add("tag", tag);
		template.add("params", params);
		return (T) template.render();
	}

	String _protoTypeDeclName = null;
	List<String> _gRecMemNames = null;
	//TODO change this to tagNames maybe
	Set<String> _caseNames = null;
	Map<String, Set<String>> _caseMap = new HashMap<String, Set<String>>();
	Map<String, String> _gProtoNameMap = new HashMap<String, String>();
	//TODO maybe change this to tagnamemap 
	Map<String, String> _gProtoCaseNameMap = new HashMap<String, String>();
	Map<String, String> _gRecordNameMap = new HashMap<String, String>();
	
	public void modifyProtocolName(Name name) {
		String modified = "Protocol_"+name.getname(); 
		_gProtoNameMap.put(name.getname(), modified);
		name.setName(modified);
	}
	/**
	 * Protocol Declaration
	 */
	public T visitProtocolTypeDecl(ProtocolTypeDecl pd) {
		Log.log(pd.line + ": Visiting a ProtocolTypeDecl **(" + pd.name().getname() + ")**");

		ST template = group.getInstanceOf("ProtocolTypeDecl");
		
		modifyProtocolName(pd.name());
		
		_protoTypeDeclName = (String) pd.name().visit(this);

		_caseNames = new HashSet<String>();
			
		String[] cases = null;
		if (pd.body() != null) {
			cases = (String[]) pd.body().visit(this);
		}

		_protoTypeDeclName = null;
		
		Set<String> temp = null;
		for(Name n: pd.extend()) {

			modifyProtocolName(n);

			temp = _caseMap.get(n.visit(this));
			if (temp != null) {
				_caseNames.addAll(temp);
			}
		}
		
		template.add("name", pd.name().getname());
		template.add("cases", cases);
		template.add("caseNames", _caseNames);
		
		_caseMap.put(pd.name().getname(), _caseNames);
		return (T) template.render();
	}
	
	public void modifyProtocolCaseName(Name name) {
		String modified = _protoTypeDeclName + "_" + name.getname();
		_gProtoCaseNameMap.put(name.getname(), modified);
		name.setName(modified);
	}
	/**
	 * Protocol Case
	 */
	public T visitProtocolCase(ProtocolCase pc) {
		Log.log(pc.line + ": Visiting a ProtocolCase");

		ST template = group.getInstanceOf("ProtocolCase");
		_gRecMemNames = new ArrayList<String>();

		_caseNames.add(pc.name().getname());

		//name: Protocol_A_a1
		modifyProtocolCaseName(pc.name());
		
		template.add("name", pc.name().getname());

		//recordMembers: int x; int y
		String[] rms = (String[]) pc.body().visit(this);
		template.add("recMems", rms);
		template.add("recMemNames", _gRecMemNames);
		return (T) template.render();
	}
	
	public void modifyRecordName(Name name) {
		String modified =  "Record_" + name.getname();
		_gRecordNameMap.put(name.getname(), modified);
		name.setName(modified);
	}
	
	public T visitRecordTypeDecl(RecordTypeDecl rt) {
		Log.log(rt.line + ": Visiting a RecordTypeDecl (" + rt.name().getname() + ")");
		ST template = group.getInstanceOf("RecordTypeDecl");

		_gRecMemNames = new ArrayList<String>();

		modifyRecordName(rt.name());
		
		List<String> rms = new ArrayList<String>();
		rms.addAll(Arrays.asList((String[])rt.body().visit(this)));
		
		for(Name name: rt.extend()) {
			Type t = (Type)_topLevelDecls.get((String)name.visit(this));
			if (t instanceof RecordTypeDecl) {
				RecordTypeDecl tt = (RecordTypeDecl) t;
				rms.addAll(Arrays.asList((String[])tt.body().visit(this)));
			}
		}

		template.add("name", rt.name().visit(this));
		template.add("recMems", rms);
		template.add("recMemNames", _gRecMemNames);

		return (T)template.render();
	}
	
	public T visitRecordMember(RecordMember rm) {
		Log.log(rm.line + ": Visiting a RecordMember");
		ST template = group.getInstanceOf("RecordMember");

		_gRecMemNames.add((String)rm.name().visit(this));

		template.add("type", rm.type().visit(this));
		template.add("name", rm.name().visit(this));
		return (T) template.render();
	}
	
	public T visitRecordAccess(RecordAccess ra) {
		Log.log(ra.line + ": Visiting a RecordAccess = " + ra.toString());
		
		ST template = group.getInstanceOf("RecordAccess");

		Type tType = ra.record().type;

		if (tType.isRecordType()) {
			System.out.println("found a record type");
		} else if (tType.isProtocolType()) {
			ProtocolTypeDecl pt = (ProtocolTypeDecl)ra.record().type;
			String caseName = _gProtoCaseNameMap.get(protocolTagsSwitchedOn.get(pt.name().getname()));
			String protocolName = caseName.substring(0, caseName.lastIndexOf("_"));

			template.add("protocolName", protocolName);
			template.add("caseName", caseName);
			template.add("record", ra.record().visit(this));
			template.add("field", ra.field().visit(this));
		}

		return (T) template.render();
	}
	
	public T visitRecordLiteral(RecordLiteral rl) {
		Log.log(rl.line + ": Visiting a RecordLiteral");

		ST template = group.getInstanceOf("RecordLiteral");
		
		String name = (String) rl.name().visit(this);
		String[] params = (String[]) rl.members().visit(this);

		template.add("name", name);
		template.add("params", params);
		
		return (T) template.render();
	}
	//--------------------------------------------------------------------------

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
		template.add("procYields", _proc_yields);
		
		return (T) template.render();
	}

	/**
	 * Sequence
	 */
	public T visitSequence(Sequence se) {
		Log.log(se.line + ": Visiting a Sequence");
		String[] returnArray = new String[se.size()];
		String value = null;

		// Iterate through all children placing results in array.
		for (int i = 0; i < se.size(); i++) {
			if (se.child(i) != null) {
				value = (String) se.child(i).visit(this);
				if (value != null && !value.isEmpty()) {
					returnArray[i] = value;
				}
			}else {
				returnArray[i] = null;
			}
		}

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

//	String currentProtocolName = null;
//	boolean isProtocolExpr = false;

    // Contains the protocol name and the corresponding tags currently switched on.
    HashMap<String,String> protocolTagsSwitchedOn = new HashMap<String,String>();

	/**
	 * SwitchStat
	 */
	public T visitSwitchStat(SwitchStat st) {
		Log.log(st.line + ": Visiting a SwitchStat");

		ST template = group.getInstanceOf("SwitchStat");
		
		boolean isProtocolExpr = st.expr().type.isProtocolType();

		String expr = (String) st.expr().visit(this);
		
		List<String> switchGroups = new ArrayList<String>();
		if (isProtocolExpr) {

			ProtocolTypeDecl pt = (ProtocolTypeDecl) st.expr().type;

			for (SwitchGroup sg : st.switchBlocks()) {
				SwitchLabel sl = sg.labels().child(0);
//				ProtocolCase pc = findProtocolCase(pt, ((NameExpr)sl.expr()).name().getname());
				
				protocolTagsSwitchedOn.put(pt.name().getname(), ((NameExpr)sl.expr()).name().getname());

				String label = (String)sl.visit(this);
				String[] stmts = (String[]) sg.statements().visit(this);

				ST template1 = group.getInstanceOf("SwitchGroup");
				template1.add("labels", label);
				template1.add("stmts", stmts);

				switchGroups.add(template1.render());
			}
		} else {
			switchGroups = Arrays.asList((String[])st.switchBlocks().visit(this));
		}

		template.add("isProtocolExpr", isProtocolExpr);
		template.add("expr", expr);
		template.add("switchGroups", switchGroups);

		isProtocolExpr = false;
		
		return (T) template.render();
	}
	
	public T visitSyncStat(SyncStat st) {
		Log.log(st.line + ": Visiting a SyncStat");
		ST template = group.getInstanceOf("SyncStat");

		template.add("barrier", st.barrier().visit(this));
		template.add("jmp", _jumpCnt);

		//FIXME: maybe create a method for this and do the 
		//adding to different lists based on boolean in that
		//method. also look into if adding it to the same list
		//always but swap the lists in different nodes as needed
		//rather than using different lists.
		_switchCases.add(getLookupSwitchCase(_jumpCnt));	
		_jumpCnt++;

		return (T) template.render();
	}

//	public void findSyncedOn(Statement stat, List<String> syncedOn) {
//		
//		if (stat instanceof ExprStat) {
//			if (((ExprStat)stat).expr() instanceof Invocation) {
//				Invocation in = (Invocation) ((ExprStat)stat).expr();
//				findSyncedOn(in.targetProc.body(), syncedOn);
//			} else if (((ExprStat)stat).expr() instanceof ChannelReadExpr) {
//				//Probably nothing to do here.
//			}
//		} else if(stat instanceof Block) {
//			Block b = (Block) stat;
//			boolean x = false;
//			for (Statement st: b.stats()) {
//				findSyncedOn(st, syncedOn);
//			}
//		} else {
//			if (stat instanceof SyncStat) {
//				SyncStat syncStat = (SyncStat) stat;
//				String bn = (String)((NameExpr)syncStat.barrier()).name().visit(this);
//				syncedOn.add(bn);
//			}
//		}
//	}

	public ProtocolCase findProtocolCase(ProtocolTypeDecl pt, String switchLabelName) {

		String modifiedName = _gProtoCaseNameMap.get(switchLabelName);
		for (ProtocolCase pc : pt.body() ) {
		    String name = pc.name().getname();
		    System.out.println("name=" + name + "  " + "switchLabelName=" + modifiedName);
		    if (name.equals(modifiedName))
		    	return pc;
		}
			
		for (int i = 0; i<pt.extend().size(); i++) {
		    ProtocolTypeDecl pdt = (ProtocolTypeDecl)((Name)pt.extend().child(i)).myDecl;
		    ProtocolCase pc;
		    pc = findProtocolCase(pdt, switchLabelName);
		    if (pc != null) 
		    	return pc;
		}

		return null;
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
	 * TimeoutStat
	 */
	public T visitTimeoutStat(TimeoutStat ts) {
		Log.log(ts.line + ": Visiting a TimeoutStat");
		ST template = group.getInstanceOf("TimeoutStat");
		
		template.add("alt", _inAlt);
		template.add("name", ts.timer().visit(this));
		template.add("delay", ts.delay().visit(this));
		template.add("jmp", _jumpCnt);

		_switchCases.add(getLookupSwitchCase(_jumpCnt));	
		_jumpCnt++;

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
		Log.log(va.line + ": Visiting a Var: " + va.name().getname() );

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
		
		if (ws.foreverLoop) {
			this._foreverLoop = true;
		}

		template.add("expr", expr);

		// May be one element or multiple.
		if (stats instanceof String[])
			template.add("stat", (String[]) stats);
		else
			template.add("stat", (String) stats);

		return (T) template.render();
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
	 * Switch cases for runLabel switch table. 
	 */
	public String getLookupSwitchCase(int jmp) {
		ST template = null;
		template = group.getInstanceOf("LookupSwitchCase");
		template.add("caseNum", jmp);
		return template.render();
	}
	
	/*
	 * FIXME: for now this only works for invocations
	 * of target found in the same file. We haven't considered
	 * imported file invocations.
	 * 
	 * Atleast for lib imports, e.g: include.JVM.std.io, it
	 * seems like if we keep the last two pkgs, that is enough.
	 * (though this too has not been implemented below.)
	 * 
	 * But need to make sure it works always.
	 * 
	 * And for god's sake, find a better way to do this.
	 */
	public String getQualifiedPkg(ProcTypeDecl pd, String invName) {
		String myPkg = pd.myPackage;
		StringBuilder qualifiedPkg = new StringBuilder();

		String startPkg = null;
		 if("println".equals(invName)){
			 startPkg = "std";
		 } else {
			 startPkg = this.originalFilename;
		 }
		 
		String[] tokens = myPkg.split("\\.");
		boolean pk_start = false;
		for (int i = 0; i < tokens.length; i++) {
			if (tokens[i].equals(startPkg))
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
				gname = "_pd$" + name;
			} else {
				gname = "_ld" + _gvarCnt++ + "$" + name;
			}
		}
		return gname;
	}

	/**
	 * Given a string it will write to the file as the final output of the compiler.
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
