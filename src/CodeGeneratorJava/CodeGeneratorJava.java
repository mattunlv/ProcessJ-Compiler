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
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import org.stringtemplate.v4.ST;
import org.stringtemplate.v4.STGroup;
import org.stringtemplate.v4.STGroupFile;

import AST.AST;
import AST.AltCase;
import AST.AltStat;
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
import Utilities.Error;
import Utilities.Log;
import Utilities.Settings;
import Utilities.SymbolTable;
import Utilities.Visitor;

public class CodeGeneratorJava<T extends Object> extends Visitor<T> {

	/** Relative location of our group string template file. */
	private final String grammarStFile = "StringTemplates/grammarTemplatesJava.stg";
	private static final int INV_WRAP_PAR = 0;
	private static final int INV_WRAP_PARFOR = 1;

	/** Object containing a group of string templates. */
	private STGroup group;

	private Map<String, String> _gFormalNamesMap = null;
	private Map<String, String> _gLocalNamesMap = null;
	private List<String> _gLocals = null;
	private List<String> _switchCases = null;
	private SymbolTable _topLevelDecls = null;
	private int _gvarCnt = 0;
	private int _jumpCnt = 1;
	private int _parCnt = 0;
	private int _altCnt = 1;
	private String _parName = null;
	private String _currentProcName = null;
	boolean isReadingChannelEnd = false;

	private List<String> barriers = new ArrayList<String>();
	private String _protoTypeDeclName = null;
	private List<String> _gRecMemNames = null;
	private Map<String, String> _gProtoNameMap = new HashMap<String, String>();
	private Map<String, String> _gProtoTagNameMap = new HashMap<String, String>();
	private Map<String, String> _gModifiedTagProtoNameMap = new HashMap<String, String>();
	private Map<String, String> _gRecordNameMap = new HashMap<String, String>();
	 // Contains the protocol name and the corresponding tag that the switch label is currently using as constexpr.
	//used in visit recordaccess to do correct castings.
    private Map<String,String> _protocolTagsSwitchedOn = new HashMap<String,String>();

    private static final int CHAN_READ_END = 1;
    private static final int CHAN_WRITE_END = 2;
    private int endType = 0;
    private Map<String, Integer> _channelEndMap = new HashMap<String, Integer>();


    private boolean addTrueMethod = false;
	
	private final String MAIN_SIGNATURE = "([T;)V";

	public static String workdir = "";
	
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
		State.init();
	}

	public void setOriginalFilename(String n) {
		this.originalFilename = n;
	}

	public T visitAltCase(AltCase ac) {
		Log.log(ac.line + ": Visiting an AltCase");
		ST template = group.getInstanceOf("AltCase");
		
		//FIXME: figure out if this visitor is needed as well as the ST.template.
		Statement caseExprStmt = ac.guard().guard();
		Statement stat = ac.stat();
		String caseExprStr;

		
		return (T) template.render();
	}

	public T visitAltStat(AltStat as) {
		Log.log(as.line + ": Visiting an AltStat");
		
		boolean oldAlt = State.set(State.ALT, true);

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
		altBGArrTemplate.add("altCnt", _altCnt);
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
			String[] stats = null;

			AltCase ac = altCaseList.child(i);
			Statement caseExprStmt = ac.guard().guard();

			if (caseExprStmt instanceof TimeoutStat) {
				TimeoutStat ts = (TimeoutStat) caseExprStmt;
				String tn = (String)ts.timer().visit(this);//name of timer
				guards[i] = tn;
				timerNames.add(tn);

				boolean oldAltGuard = State.set(State.ALT_GUARD, true);
				timers.add((String)ts.visit(this));
				State.set(State.ALT_GUARD, oldAltGuard);
				
		        stats = getStatements(ac.stat());
				statementList.addAll(Arrays.asList(stats)); //the statements in the timer

			} else if (caseExprStmt instanceof ExprStat){ //Channel
				Expression e = ((ExprStat) caseExprStmt).expr();
				ChannelReadExpr cr = null;
				if (e instanceof ChannelReadExpr) {
					cr = (ChannelReadExpr) e;
				} else if (e instanceof Assignment) {
					cr = (ChannelReadExpr)((Assignment)e).right();
				}
				guards[i] = (String) cr.channel().visit(this); //name of channel
				
				boolean oldAltGuard = State.set(State.ALT_GUARD, true);
				String chanRead = (String)caseExprStmt.visit(this);
				State.set(State.ALT_GUARD, oldAltGuard);

				statementList.add(chanRead);

		        stats = getStatements(ac.stat());
				statementList.addAll(Arrays.asList(stats)); //the statements in the channelexpr guard 
				
			} else if (caseExprStmt instanceof SkipStat){
				guards[i] = "PJAlt.SKIP_GUARD";
				
		        stats = getStatements(ac.stat());
				statementList.addAll(Arrays.asList(stats)); //the statements in the skip guard
			} else {
				Error.error("Unknown statement in guards!!!");
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

		String chosen = globalize("chosen", false);
		_gLocals.add("int " + chosen);
		template.add("chosen", chosen);
		
		ST altSwitchGroup = group.getInstanceOf("AltSwitchGroup");
		altSwitchGroup.add("cases", altCases);
		altSwitchGroup.add("chosen", chosen);

		if (guards.length > 0) {
			altGuardArrTemplate.add("guards", guards);
			altGuardArrTemplate.add("altCnt", _altCnt);
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
		
		String altName = globalize("alt", false);
		_gLocals.add("PJAlt " + altName);
		template.add("name", altName);

		template.add("altCnt", _altCnt);
		_altCnt++;

		State.set(State.ALT, oldAlt);

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
			case ChannelType.NOT_SHARED: typeString = "PJOne2OneChannel";break;
			case ChannelType.SHARED_WRITE: typeString = "PJMany2OneChannel";break;
			case ChannelType.SHARED_READ: typeString = "PJOne2ManyChannel";break;
			case ChannelType.SHARED_READ_WRITE: typeString = "PJMany2ManyChannel";break;
			default: typeString = "One2OneChannel";
		}
		
		Type t = ct.baseType();
		//TODO rename this to generictype or template or something
		String basetype = null;
		if (t.isNamedType()) {
			NamedType tt = (NamedType) t;
			
			if (isProtocolType(tt)) {
				basetype = "PJProtocolCase";
			} else {
				basetype = (String)tt.visit(this);
			}
		} else {
			//FIXME maybe do a check for primitive type...and rename the below method.
			basetype = getWrapperType(t);
		}

		return (T) (typeString + "<" + basetype + ">");
	}

	/**
	 * ChannelEndExpr
	 */
	public T visitChannelEndExpr(ChannelEndExpr ce) {
		Log.log(ce.line + ": Visiting a Channel End Expression!");

		String channel = (String) ce.channel().visit(this);
		
		if (ce.isRead()) {
            isReadingChannelEnd = true;
        } else if (ce.isWrite()) {
            isReadingChannelEnd = false;
        }
		
		return (T) channel;
	}

	/**
	 * ChannelEndType
	 */
	public T visitChannelEndType(ChannelEndType ct) {
		Log.log(ct.line + ": Visiting a Channel End Type!");

		//Getting the channel type
		String maintype = "PJChannel";
		if(ct.isShared()) {
			if(ct.isRead()) {
				maintype = "PJOne2ManyChannel";
			} else {
				maintype = "PJMany2OneChannel";
			}
		} else {
			maintype = "PJOne2OneChannel";
		}
		
		//Getting the Channel<'basetype'>
		Type t = ct.baseType();
		String basetype = null;
		if (t.isNamedType()) {
			NamedType tt = (NamedType) t;
			
			if (isProtocolType(tt)) {
				basetype = "PJProtocolCase";
			} else {
				basetype = (String)tt.visit(this);
			}
		} else {
			//FIXME maybe do a check for primitive type...and rename the below method.
			basetype = getWrapperType(t);
		}
		
		String chanEndType = "";
		if (State.is(State.PARAMS)) {
			chanEndType = "PJChannel<" + basetype + ">";
		} else {
			chanEndType = maintype + "<" + basetype + ">";
		}

		if (ct.isRead()) {
		    endType = CHAN_READ_END;
	    } else if (ct.isWrite()) {
	        endType = CHAN_WRITE_END;
	    }

		return (T) chanEndType;
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
			_switchCases.add(getLookupSwitchCase(_jumpCnt));	
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
		
		/*
		 * FIXME: might need to handle other types of infinite loops such as for(;;)
		 * Adding proc for while(true)loops:
		 * proc boolean getTrue() { return true; }
		 */
		if (this.addTrueMethod) {
			typeDeclsStr.add((String)(((ST)group.getInstanceOf("GetTrue")).render()));
		}

		template.add("typeDecls", typeDeclsStr);
		template.add("packageName", this.originalFilename);
		
		String home = System.getProperty("user.home");
		
		System.out.println("home:" + home);

//		String dir = home + File.separator + workdir;
//		dir = dir.substring(1);
//		dir = dir.replace("/", ".");
//		dir = dir.substring(0, dir.length()-1);
//		template.add("packageName", dir);
//		template.add("classname", this.originalFilename);

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

		State.set(State.CLAIMSTAT, true);

		ST template = null;
		
		Map<String, Boolean> chanNameToEndType = new HashMap<String, Boolean>();

		String ldStr = null;
		Sequence<AST> claimExprs = cs.channels();
		
		for(int k=0; k < claimExprs.size(); k++) {
			AST ast = claimExprs.child(k);
			if (ast instanceof LocalDecl) {
				LocalDecl ld = (LocalDecl) ast;
				ldStr = (String)ld.visit(this);
				chanNameToEndType.put(ld.var().name().getname(), isReadingChannelEnd);
			} else {
			    chanNameToEndType.put((String) ast.visit(this), isReadingChannelEnd);
			}
			
		}
		
		String[] stats = getStatements(cs.stat());
		
		template = group.getInstanceOf("ClaimStat");
		template.add("chanNameToReadEndType", chanNameToEndType);
		template.add("ldstr", ldStr);
		template.add("stats", stats);
		
		template.add("jmp", _jumpCnt);
		_switchCases.add(getLookupSwitchCase(_jumpCnt));	
		_jumpCnt++;

		State.set(State.CLAIMSTAT, false);

		return (T) template.render();
	}
	
	private String[] getStatements(AST stat) {
	    String[] stats = null;
	    
	    if (stat instanceof Block) {
            Block b = (Block) stat;
            stats = (String[]) b.visit(this);
        } else {
            stats = new String[1];
            stats[0] = (String) stat.visit(this);
        }
	    
	    return stats;
	}

	/**
	 * Assignment
	 */
	public T visitAssignment(Assignment as) {
		Log.log(as.line + ": Visiting an Assignment");

		ST template = group.getInstanceOf("Assignment");
		String left = (String) as.left().visit(this);
		String op = (String) as.opString();

		if (as.right() instanceof ChannelReadExpr) {
			return (T) createChannelReadExpr(left, (ChannelReadExpr)as.right());
		} else if (as.right() instanceof Invocation) {
			return (T) createInvocation(left, (Invocation)as.right(), false);
		} else if (as.right() instanceof NewArray) {
			return (T) createNewArray(left, (NewArray)as.right());
		} else {
			String right = (String) as.right().visit(this);
			template.add("left", left);
			template.add("right", right);
			template.add("op", op);
		}

		return (T) template.render();
	}
	
	public T createNewArray(String left, NewArray ne) {
		Log.log(ne.line + ": Creating a NewArray");

		ST template = null;
		
		Type bt = ne.baseType();
		if (bt.isChannelType() || bt.isChannelEndType() || (bt.isNamedType() && (isProtocolType((NamedType)bt)))) {
			template = group.getInstanceOf("NewArrayIntializedElements");
		} else {
			template = group.getInstanceOf("NewArray");
		}

		String myType = (String) ne.baseType().visit(this);
		Sequence<Expression> dimsExpr = ne.dimsExpr();
		String[] dims = (String[]) dimsExpr.visit(this);
		
		template.add("left", left);
		template.add("type", myType);
		template.add("dims", dims);

		return (T) template.render();
	}
	
	public T createInvocation(String left, Invocation in, boolean isParamInvocation) {
		return createInvocation(left, in, null, isParamInvocation, -1);
	}

	public T createInvocation(String left, Invocation in, List<String> barriers, boolean isParamInvocation, int invocationWrapper) {
		Log.log(in.line + ": Creating Invocation ("+ in.procedureName().getname() + ") with LHS as " + left);


	    ST template = null;
	    ProcTypeDecl pd = in.targetProc;
	    String qualifiedPkg = getQualifiedPkg(pd);
	    
	    String procName = null;
	    if (pd.myPackage.contains(this.originalFilename)) { //invoking proc in the same file
	        procName = getTemplateProcName(in.procedureName().getname());
	    } else {
	        procName = in.procedureName().getname(); //invoking proc from import
	    }
	    String qualifiedProc = qualifiedPkg + "." + procName;

		List<String> paramLst = new ArrayList<String>();
		List<String> paramBlocks = new ArrayList<String>();

		boolean yields = isYieldingProc(pd);
		/*
		 * For any invocation of yielding proc that's not inside
		 * par block, we will, as inefficient as it, wrap it in
		 * a par that has single exprstat.
		 */
		if (yields && invocationWrapper != INV_WRAP_PAR && invocationWrapper != INV_WRAP_PARFOR) {
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
		
		if (invocationWrapper == INV_WRAP_PAR) {
			template.add("par", true);
		} else if (invocationWrapper == INV_WRAP_PARFOR) {
			template.add("parfor", true);
		}

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
			template = group.getInstanceOf("InvocationWithInvocationParamType");
			
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
			 */
			template = group.getInstanceOf("TimerReadExpr");
			template.add("left", left);
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

			//EXTENDED RENDEZVOUS
			Block b = cr.extRV();
			String[] extRv = null;
			if (b != null) {
				//FIXME looks like ill need to turn off altGuard flag for
				//this block as extRv could have read from the same channel
				//again.
				boolean oldAltGuard = State.set(State.ALT_GUARD, false);
				extRv = (String[])b.visit(this);
//				isAltGuard = oldAltGuard;
				State.set(State.ALT_GUARD, oldAltGuard);
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
			if (!(State.is(State.ALT) && State.is(State.ALT_GUARD))) {
				/*
			     * Adding switch cases for resumption.
			     */
				for (int i=0; i<2; i++) {
					_switchCases.add(getLookupSwitchCase(_jumpCnt));	
					template.add("jmp" + i, _jumpCnt);
					_jumpCnt++;
				}
			}

			template.add("channel", channel);
			template.add("left", left);
			//FIXME I might not even need _inAlt here.
			template.add("alt", ( State.is(State.ALT) && State.is(State.ALT_GUARD)));

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

		// TODO: Add support for string concatenation here.

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

	/**
	 * ForStat
	 */
	public T visitForStat(ForStat fs) {
		Log.log(fs.line + ": Visiting a ForStat");
		
		ST template = group.getInstanceOf("ForStat");

		String[] initStr = null;
		String[] incrStr = null;
		String expr = null;

		Sequence<Statement> init = fs.init();
		if (init != null) {
			initStr = (String[]) init.visit(this);
		}

		Sequence<ExprStat> incr = fs.incr();
		if (incr != null) {
			incrStr = (String[]) incr.visit(this);
		}

		Expression e = fs.expr();
		if (e != null) {
			expr = (String) fs.expr().visit(this);
		}

		String object = null;
		
		if (fs.isPar()) {
			boolean oldVal = State.set(State.PARFOR, true);

			template = group.getInstanceOf("ParForStat");
			
			String nameHolder = _parName;
			_parName = "parfor" + ++_parCnt;
			
			List<String> barriersTemp = null;
			Sequence<Expression> bs = fs.barriers();
			if (bs != null) {
				barriersTemp = new ArrayList<String>();
				barriersTemp.addAll(barriers);
				barriers.clear();
				barriers.addAll(Arrays.asList((String[]) bs.visit(this)));
			}
				
			Annotations a = new Annotations();
			a.add("yield", "true");

			String rendered = null;
			//FIXME serious refactoring needed for the following block. just making it work for now.
			if (fs.stats() instanceof Block)  {
				Block b = (Block) fs.stats();
				if (b.stats().size()==1 && b.stats().child(0) instanceof ExprStat && ((ExprStat)b.stats().child(0)).expr() instanceof Invocation) {
					Invocation in = (Invocation)((ExprStat)b.stats().child(0)).expr();
					rendered = (String) createInvocation(null, in, barriers, false, INV_WRAP_PARFOR);
				} else {
					rendered = (String)(new ProcTypeDecl(new Sequence(), null, new Name("Anonymous"), new Sequence(), new Sequence(), a, b)).visit(this);
				}
			} else if (fs.stats() instanceof ExprStat) {
				ExprStat es = (ExprStat) fs.stats();
				if (es.expr() instanceof Invocation) {
					rendered = (String) createInvocation(null, (Invocation) es.expr(), barriers, false, INV_WRAP_PARFOR);
				} else {
					rendered = (String)(new ProcTypeDecl(new Sequence(), null, new Name("Anonymous"), new Sequence(), new Sequence(), a, new Block(new Sequence(fs.stats())))).visit(this);
				}
			} else {
				rendered = (String)(new ProcTypeDecl(new Sequence(), null, new Name("Anonymous"), new Sequence(), new Sequence(), a, new Block(new Sequence(fs.stats())))).visit(this);
			}
			
			//TODO: fix for empty forstat block
			template.add("stats", rendered);

			template.add("parName", _parName);
			template.add("barriers", barriers);
			//----------------

			
			/*
			 * Adding resumption points
			 */
			template.add("jmp", _jumpCnt);
			_switchCases.add(getLookupSwitchCase(_jumpCnt));	
			_jumpCnt++;

			template.add("init", initStr);
			template.add("incr", incrStr);
			template.add("expr", expr);
			
			object = template.render();
			
			_parName = nameHolder;
			if (barriersTemp != null) {
				barriers.clear();
				barriers.addAll(barriersTemp);
			}
			State.set(State.PARFOR, oldVal);

		} else {
			template = group.getInstanceOf("ForStat");
			/*
			 * Depending whether there is curly brackets (block) it
			 * may return an array, or maybe just a single object. 
			 */
			Statement st = fs.stats();
			Object stats = null;
			if (st != null) {
				stats = fs.stats().visit(this);
			}

			if (stats instanceof String[])
				template.add("stats", (String[]) stats);
			else
				template.add("stats", (String) stats);
			
			template.add("init", initStr);
			template.add("incr", incrStr);
			template.add("expr", expr);
			
			object = template.render();
		}
		
		return (T) object;
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
	
	public T visitConstantDecl(ConstantDecl cd) {
		Log.log(cd.line + ": Visting ConstantDecl (" + cd.type().typeName() + " "
				+ cd.var().name().getname() + ")");
		
		ST template = group.getInstanceOf("ConstantDecl");
		
		template.add("type", cd.type().visit(this));
		template.add("var", cd.var().visit(this));
		
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

		if (ld.type() instanceof ChannelEndType ||
		        (ld.type() instanceof ArrayType && ((ArrayType)ld.type()).baseType() instanceof ChannelEndType)) {
		    _channelEndMap.put(name, endType);
		}

		boolean isTimerType = false; 
		if (ld.type() instanceof PrimitiveType) {
			isTimerType = ((PrimitiveType)ld.type()).isTimerType();
		}
		
		boolean isProtocolType = false;
		boolean isRecordType = false;
		if (ld.type() instanceof NamedType) { //Protocol or Record type
			isProtocolType = isProtocolType((NamedType)ld.type());
			isRecordType = isRecordType((NamedType)ld.type());
		}

		if (isProtocolType) {
			_gLocals.add("PJProtocolCase " + name);
		} else {
			_gLocals.add(typeString + " " + name);
		}

		ST template = null;
		if (State.is(State.PROC_YIELDS)) {
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

		if(State.is(State.PROC_YIELDS)&& (right instanceof ChannelReadExpr || right instanceof Invocation || right instanceof NewArray)){
			String assignment = (String)new Assignment(new NameExpr(ld.var().name()), right, Assignment.EQ).visit(this);
			return (T) assignment;
		} else {
			/*
			 * Resolve type. 
			 */
			Type t = ld.type();
			
			boolean barrierType = false;
			if (t instanceof PrimitiveType && t.isBarrierType()) {
				barrierType = true;
			}
			
			boolean channelType = t.isChannelType();

			template.add("chanType", channelType);
			template.add("protoType", isProtocolType);
			template.add("recType", isRecordType);
			template.add("barrierType", barrierType);
			template.add("timerType", isTimerType);
			template.add("rEmpty", (ld.var().init() == null));

			String var = (String) ld.var().visit(this);
			template.add("var", var);
			template.add("typeStr", typeString);
			
			return (T) template.render();
		}
		
	}
	
	public boolean isProtocolType(NamedType nt) {
		return _gProtoNameMap.containsKey(nt.name().getname());
	}

	public boolean isRecordType(NamedType nt) {
		return _gRecordNameMap.containsKey(nt.name().getname());
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

		String name = (String) ne.name().visit(this);
		if (State.is(State.CLAIMSTAT)) {
		   if (_channelEndMap.containsKey(name)) {
		       if (_channelEndMap.get(name) == CHAN_READ_END) {
		           isReadingChannelEnd = true;
		       } else {
		           isReadingChannelEnd = false;
		       }
		   }
		}

		return (T) name;
	}

	/**
	 * NewArray
	 */
	public T visitNewArray(NewArray ne) {
		Log.log(ne.line + ": Visiting a NewArray");

		return createNewArray(null, ne);
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

		String type = (String) pd.type().visit(this);

		String name = pd.paramName().getname();
		String gname = globalize(name, true);
		pd.paramName().setName(name);
		if (_gFormalNamesMap != null) {
			_gFormalNamesMap.put(name, gname);
		}
		name = (String) pd.paramName().visit(this);
		
		if (pd.type() instanceof ChannelEndType ||
                (pd.type() instanceof ArrayType && ((ArrayType)pd.type()).baseType() instanceof ChannelEndType)) {
            _channelEndMap.put(name, endType);
        }

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

		boolean oldParBlock = State.set(State.PAR_BLOCK, true);
		
		String nameHolder = null;
		nameHolder = _parName;
		_parName = "par" + ++this._parCnt;
		
		 /*
	     * Adding switch cases for Par resumption.
	     */ 
		_switchCases.add(getLookupSwitchCase(_jumpCnt));	
		parBlockTemplate.add("jmp", _jumpCnt);
		_jumpCnt++;
		
		//BARRIER ------------------
		
		List<String> barriersTemp = null;
		Sequence<Expression> bs = pb.barriers();
		if (bs != null) {
			barriersTemp = new ArrayList<String>();
			barriersTemp.addAll(barriers);
			barriers.clear();
			barriers.addAll(Arrays.asList((String[]) bs.visit(this)));
		}
		
		Sequence<Statement> se = pb.stats();
		String[] stats = new String[se.size()];

		for (int k = 0; k < se.size(); k++) {
			Object body = null;

			Statement st = se.child(k);

			if (st != null) {
				if (st instanceof ExprStat && ((ExprStat) st).expr() instanceof Invocation) {

					ExprStat es = (ExprStat) st; 
					
					stats[k] = (String) createInvocation(null, (Invocation) es.expr(), barriers, false, INV_WRAP_PAR);
					
				} else {
					//FIXME I don't think annotation is needed as anonymous is only used for processes
					Annotations a = new Annotations();
					a.add("yield", "true");
					stats[k] = (String)(new ProcTypeDecl(new Sequence(), null, new Name("Anonymous"), new Sequence(), new Sequence(), a, new Block(new Sequence(st))).visit(this));
				}
			}
		}

		parBlockTemplate.add("parCnt", stats.length);
		parBlockTemplate.add("stats", stats);
		parBlockTemplate.add("barriers", barriers);
		parBlockTemplate.add("parName", _parName);
	
		String object = parBlockTemplate.render();
		
		
		
		State.set(State.PAR_BLOCK, oldParBlock);
		_parName = nameHolder;
		if (barriersTemp != null) {
			barriers.clear();
			barriers.addAll(barriersTemp);
		}
		
		return (T) object;
	}

	public String makeLookupSwitch(List<String> cases) {
		ST switchTemplate= group.getInstanceOf("LookupSwitchTable");
		switchTemplate.add("cases", cases);
		return switchTemplate.render();
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
			typeString = "PJTimer";
		} else if (py.isBarrierType()) {
			typeString = "PJBarrier";
		}
		return (T) typeString;
	}
	
	/**
	 * ProcTypeDecl
	 */
	public T visitProcTypeDecl(ProcTypeDecl pd) {
		
		String currentProcName = this._currentProcName;
		this._currentProcName = (String) pd.name().visit(this);
		String templateProcName = getTemplateProcName(this._currentProcName);
		
		boolean anonymous = false;
		if (_currentProcName.equals("Anonymous")) {
			anonymous = true;
		}
		
		Log.log(pd.line + ": Visiting a ProcTypeDecl **(" + _currentProcName + ")**");

		State.set(State.PROC_YIELDS, isYieldingProc(pd));

		ST template = null;
		String rendered = null;
		
		if (anonymous) {
			template = group.getInstanceOf("AnonymousProcess");
			
			/*
			 * Backing up necessary global vars
			 */
			List<String> holder = new ArrayList<String>();
			if (this._switchCases != null && this._switchCases.size() > 0) {
				holder.addAll(this._switchCases);
				this._switchCases = new ArrayList<String>();	
			}
			
			String[] block = (String[]) pd.body().visit(this);

			template.add("body", block);
			template.add("lookupswitch", makeLookupSwitch(_switchCases));
			template.add("parName", _parName);
			template.add("parfor", State.is(State.PARFOR));
			template.add("barriers", barriers);

			rendered = template.render();
			
			_switchCases.clear();
			if (holder.size() > 0) {
				_switchCases.addAll(holder);
			}
			
		} else {
			
			if (State.is(State.PROC_YIELDS)) {
				template = group.getInstanceOf("ProcTypeDeclToProcess");
			} else {
				template = group.getInstanceOf("ProcTypeDeclToMethod");
			}
			
			/*
			 * Initializing global var count for new class.
			 */
			this._jumpCnt = 1;
			this._gvarCnt = 0;
			this._parCnt = 0;
			this._altCnt = 1;
			this._gFormalNamesMap = new HashMap<String, String>();
			this._gLocalNamesMap = new HashMap<String, String>();
			this._gLocals = new ArrayList<String>();
			this._switchCases = new ArrayList<String>();	

			State.set(State.FOREVER_LOOP, false);

			String qualifiedPkg = getQualifiedPkg(pd);
			String qualifiedProc = qualifiedPkg + "." + templateProcName;

			String returnType = (String) pd.returnType().visit(this);
			
			boolean oldFormals = State.set(State.PARAMS, true);
			String[] formals = (String[]) pd.formalParams().visit(this);
			State.set(State.PARAMS, oldFormals);

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
			template.add("name", templateProcName);
			if (formals.length != 0) {
				template.add("formals", formals);
			}
			if (_gFormalNamesMap.values().size() != 0) {
				template.add("formalNames", _gFormalNamesMap.values());
			}
			if (_gLocals.size() != 0) {
				template.add("globals", _gLocals.toArray(new String[_gLocals.size()]));
			}
			
			boolean isMain = "main".equals(_currentProcName) && MAIN_SIGNATURE.equals(pd.signature());
			if (isMain && State.is(State.PROC_YIELDS)) {
				ST mainTemplate = group.getInstanceOf("ProcTypeDeclToMain");
				mainTemplate.add("qualifiedProc", qualifiedProc);
				
				template.add("mainMethod", mainTemplate.render());
			}
			
			template.add("body", block);
			template.add("lookupswitch", makeLookupSwitch(_switchCases));
			template.add("foreverloop", State.is(State.FOREVER_LOOP));

			rendered = template.render();
		}

		/*
		 * Resetting global vars
		 */
		_currentProcName = currentProcName;

		return (T) rendered;
	}
	
	private String getTemplateProcName(String name) {
	    StringBuilder sb = new StringBuilder();
	    sb.append("proc");
	    sb.append(name.substring(0, 1).toUpperCase());
	    sb.append(name.substring(1));
        return sb.toString();
    }

    /**
	 * Protocol Literal
	 */
	public T visitProtocolLiteral(ProtocolLiteral pl) {
		Log.log(pl.line + ": Visiting a ProtocolLiteral");
		ST template = group.getInstanceOf("ProtocolLiteral");
		
		String protocolName = (String) pl.name().visit(this);
		String tag = (String)pl.tag().visit(this);
		String tagName = _gProtoTagNameMap.get(tag);
		String[] params = (String[]) pl.expressions().visit(this);

		template.add("protocolName", protocolName);
		template.add("tagName", tagName);
		template.add("params", params);
		return (T) template.render();
	}
	
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

		//Visiting protocol cases.
		String[] cases = null;
		if (pd.body() != null) {
			cases = (String[]) pd.body().visit(this);
		}

		_protoTypeDeclName = null;
		
		template.add("name", pd.name().getname());
		template.add("cases", cases);

		return (T) template.render();
	}
	
	public void modifyProtocolTagName(Name name) {
		String modified = _protoTypeDeclName + "_" + name.getname();
		_gProtoTagNameMap.put(name.getname(), modified);
		_gModifiedTagProtoNameMap.put(modified, _protoTypeDeclName+"");
		name.setName(modified);
	}
	/**
	 * Protocol Case
	 */
	public T visitProtocolCase(ProtocolCase pc) {
		Log.log(pc.line + ": Visiting a ProtocolCase");

		ST template = group.getInstanceOf("ProtocolCase");
		_gRecMemNames = new ArrayList<String>();

		template.add("tag", pc.name().getname());
		//name: Protocol_A_a1
		modifyProtocolTagName(pc.name());
		
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
		Log.log(rm.line + ": Visiting a RecordMember (" + rm.type().typeName() + " " 
				+ rm.name().getname() + ")");
		
		
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
			Log.log("found a record type");
		} else if (tType.isProtocolType()) {
			ProtocolTypeDecl pt = (ProtocolTypeDecl)ra.record().type;
			String caseName = _gProtoTagNameMap.get(_protocolTagsSwitchedOn.get(pt.name().getname()));
			String protocolName = _gModifiedTagProtoNameMap.get(caseName);

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
		template.add("procYields", State.is(State.PROC_YIELDS));
		
		return (T) template.render();
	}

	/**
	 * Sequence
	 */
	public T visitSequence(Sequence se) {
		Log.log(se.line + ": Visiting a Sequence");
		List<String> children = new ArrayList<String>();
		String[] stats = null;

		for (int i = 0; i < se.size(); i++) {
			if (se.child(i) != null) {
			    stats = getStatements(se.child(i));
				if (stats != null && stats.length != 0) {
					children.addAll(Arrays.asList(stats));
				}
			}
		}
		
		String[] returnArray = new String[children.size()];
		returnArray = children.toArray(returnArray);

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
			if (State.is(State.PROTOCOL_EXPR)) {
				constExpr = "\"" + constExpr + "\"";
			}
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
		
		boolean oldVal = State.set(State.PROTOCOL_EXPR, st.expr().type.isProtocolType());

		String expr = (String) st.expr().visit(this);
		
		List<String> switchGroups = new ArrayList<String>();
		if (State.is(State.PROTOCOL_EXPR)) {

			ProtocolTypeDecl pt = (ProtocolTypeDecl) st.expr().type;

			for (SwitchGroup sg : st.switchBlocks()) {
				//FIXME why am I just visiting one label? Can there not be more than one?
				SwitchLabel sl = sg.labels().child(0);
				
				_protocolTagsSwitchedOn.put(pt.name().getname(), ((NameExpr)sl.expr()).name().getname());

				String label = (String)sl.visit(this);
				
				//This can have nested switchstat with protocol expression
				String[] stmts = (String[]) sg.statements().visit(this);

				ST template1 = group.getInstanceOf("SwitchGroup");
				template1.add("labels", label);
				template1.add("stmts", stmts);

				switchGroups.add(template1.render());

			}
		} else {
			switchGroups = Arrays.asList((String[])st.switchBlocks().visit(this));
		}
		
		template.add("isProtocolExpr", State.is(State.PROTOCOL_EXPR));
		template.add("expr", expr);
		template.add("switchGroups", switchGroups);

		String rendered = template.render();

		State.set(State.PROTOCOL_EXPR, oldVal);
		
		return (T) rendered;
	}
	
	public T visitSyncStat(SyncStat st) {
		Log.log(st.line + ": Visiting a SyncStat");
		ST template = group.getInstanceOf("SyncStat");

		template.add("barrier", st.barrier().visit(this));
		template.add("jmp", _jumpCnt);

		_switchCases.add(getLookupSwitchCase(_jumpCnt));	
		_jumpCnt++;

		return (T) template.render();
	}

	public ProtocolCase findProtocolTag(ProtocolTypeDecl pt, String switchLabelName) {

		String modifiedName = _gProtoTagNameMap.get(switchLabelName);
		for (ProtocolCase pc : pt.body() ) {
		    String name = pc.name().getname();
		    if (name.equals(modifiedName))
		    	return pc;
		}
			
		for (int i = 0; i<pt.extend().size(); i++) {
		    ProtocolTypeDecl pdt = (ProtocolTypeDecl)((Name)pt.extend().child(i)).myDecl;
		    ProtocolCase pc;
		    pc = findProtocolTag(pdt, switchLabelName);
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
		
		template.add("alt", ( State.is(State.ALT) && State.is(State.ALT_GUARD)));;
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
			 * TODO 03.25.2016 why is this commented?
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

		if (ws.foreverLoop) {
			State.set(State.FOREVER_LOOP, true);
			expr = "getTrue()";

			this.addTrueMethod = true;
		}

		Object stats = ws.stat().visit(this);
		
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
	
	public String getQualifiedPkg(ProcTypeDecl pd) {
		String myPkg = pd.myPackage;
		
		if (myPkg.contains(this.originalFilename)) { //invocation from the same file
			return originalFilename;
		} else { //invocation from imported file
	        String includeDirPath = Settings.includeDir + File.separator + Settings.targetLanguage + File.separator;
	        includeDirPath = includeDirPath.replaceAll(File.separator, "\\."); // replace all '/' with '.'
	        myPkg = myPkg.replaceAll(includeDirPath, "");
	        return myPkg;
		}
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
		else if (t.isChannelEndType())
			; //TODO does this need to be handled? as channels can be passed through channels.

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
			String home = System.getProperty("user.home");

			File dir = new File(home + File.separator + workdir);
			if (!dir.exists())
				dir.mkdir();

			Log.log(dir.getAbsolutePath() + this.originalFilename);		

			String javafile = dir.getAbsolutePath() + File.separator + filename + ".java";
			Log.log("JavaFile=" + javafile);

			FileOutputStream fos = new FileOutputStream(javafile);
			
			writer = new BufferedWriter(new OutputStreamWriter(fos, "utf-8"));
			writer.write(finalOutput);
			
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
