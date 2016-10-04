package Printers;

import Utilities.Visitor;
import AST.*;
import Utilities.Log;

public class PrettyPrinter<T extends AST> extends Visitor<T> {
    public static int indent = 0;
    
    int lineno = 1;
    
    
    private String tab() {
	
    String s = "";
    if (lineno < 10)
    	s = "00" + lineno;
    else if (lineno < 100)
    	s = "0" + lineno;
    else 
    	s = "" + lineno;
    s = s+ ":  ";
    lineno++;
	for (int i=0;i<indent;i++)
	    s+= " ";
	return s;
    }
    
    private void p(String s) {
    	Log.log(tab() + s);
    }
    
    public PrettyPrinter() {
	Log.log("ProcessJ Pretty Print");
	debug = true;
    }
    
    public T visitAltCase(AltCase ac) {
    	Log.logNoNewline(tab());
    	if (ac.precondition() != null) {
    	  Log.logNoNewline("(");
    	  ac.precondition().visit(this);
    	  Log.logNoNewline(") && ");
    	}
    	ac.guard().visit(this);
    	Log.logNoNewline(" : ");	
    	indent += 2;
    	ac.stat().visit(this);
    	return null;
    }
    public T visitAltStat(AltStat as) {	
    	p("alt {");
		indent += 2;
		as.visitChildren(this);
		indent -= 2;
		p("}");
		return null;
    }
    public T visitArrayAccessExpr(ArrayAccessExpr ae) {
	    ae.target().visit(this);
	    Log.logNoNewline("[");
    	ae.index().visit(this);
    	Log.logNoNewline("]");
    	return null;
    }
    public T visitArrayLiteral(ArrayLiteral al) {
    	// TODO
    	return al.visitChildren(this);
    }
    public T visitArrayType(ArrayType at) {
    	at.baseType().visit(this);
    	for (int i=0;i<at.getDepth();i++)
    		Log.logNoNewline("[]");
    	return null;
    }
    public T visitAssignment(Assignment as) {
    	as.left().visit(this);
    	Log.logNoNewline(" " + as.opString() + " ");
    	as.right().visit(this);
    	return null;
    }
    public T visitBinaryExpr(BinaryExpr be) {
    	be.left().visit(this);
    	Log.logNoNewline(" " + be.opString() + " ");
    	be.right().visit(this);
    	return null;
    }
    public T visitBlock(Block bl) {
	    Log.log("{");
	    indent += 2;
	    for (Statement st : bl.stats()) {
	    	if (st == null)	{
	    		indent += 2;
	    		Log.log(tab() + ";");
	    		indent -= 2;
	    	} else {
	    		st.visit(this);
	    		if (st instanceof LocalDecl)
	    			Log.log(";");
	    	}
	    }
	    indent -= 2;
	    Log.log("}");
    	return null;
    }
    public T visitBreakStat(BreakStat bs) {
    	Log.logNoNewline("break");
        if (bs.target() != null) {
        	Log.logNoNewline(" ");
        	bs.target().visit(this);
        }
    	return null;
    }
    public T visitCastExpr(CastExpr ce) {
    	// TODO
    	return ce.visitChildren(this);
    }    
    public T visitChannelType(ChannelType ct) {
    	String modString = ct.modString();
    	Log.logNoNewline(modString);
    	if (!modString.equals(""))
    		Log.logNoNewline(" ");
    	Log.logNoNewline("chan<");
    	ct.baseType().visit(this);
    	Log.logNoNewline(">");
    	return null;
    }
    public T visitChannelEndExpr(ChannelEndExpr ce) {
    	ce.channel().visit(this);
    	Log.logNoNewline("." + (ce.isRead() ? "read" : "write"));
    	return null;
    }
    public T visitChannelEndType(ChannelEndType ct) {
	  if (ct.isShared())
		  Log.logNoNewline("shared ");
	  Log.logNoNewline("chan<");
	  ct.baseType().visit(this);
	  Log.logNoNewline(">." + (ct.isRead() ? "read" : "write"));
	  return null;
    }
    public T visitChannelReadExpr(ChannelReadExpr cr) {
    	cr.channel().visit(this);
    	Log.logNoNewline(".read(");
    	if(cr.extRV() != null) {
    		Log.log("{");
    		indent += 2;
    		cr.extRV().stats().visit(this);
    		indent -= 2;
    		Log.logNoNewline("}");
    	}
    	Log.logNoNewline(")");
    	return null;
    }
    public T visitChannelWriteStat(ChannelWriteStat cw) {
    	cw.channel().visit(this);
    	Log.logNoNewline(".write(");
    	cw.expr().visit(this);
    	Log.logNoNewline(")");
    	return null;
    }
    public T visitClaimStat(ClaimStat cs) {
    	// TODO
    	return cs.visitChildren(this);
    }
    public T visitCompilation(Compilation co) {
    	return co.visitChildren(this);
    }
    public T visitConstantDecl(ConstantDecl cd) {
    	Log.logNoNewline(tab());
    	printModifierSequence(cd.modifiers());
    	if (cd.modifiers().size() > 0)
    		Log.logNoNewline(" ");
    	cd.type().visit(this);
    	Log.logNoNewline(" ");
    	cd.var().visit(this);
    	Log.log(";");
    	return null;
    }
    public T visitContinueStat(ContinueStat cs) {
    	Log.logNoNewline("continue");
    	if (cs.target() != null) {
    		Log.logNoNewline(" ");
    		cs.target().visit(this);
    	}
    	return null;
    }
    public T visitDoStat(DoStat ds) {
    	Log.logNoNewline(tab() + "do ");
    	if (ds.stat() instanceof Block) {
    		Log.log("{");
    		indent += 2;
    		((Block)ds.stat()).stats().visit(this);
    		indent -= 2;
    		Log.logNoNewline(tab() + "} while (");
    		ds.expr().visit(this);
    		Log.logNoNewline(")");
    	} else {
    		Log.log("");
    		indent += 2;
    		ds.stat().visit(this);
    		indent -= 2;
    		Log.logNoNewline(tab() + "while (");
    		ds.expr().visit(this);
    		Log.logNoNewline(");");    		
    	}
    	Log.log("");
    	return null;	
    }
    public T visitExprStat(ExprStat es) {
    	Log.logNoNewline(tab());
    	es.expr().visit(this);
    	Log.log("");
        return null;
    }
    public T visitForStat(ForStat fs) {
    	Log.logNoNewline(tab());
    	Log.logNoNewline("for (");
    	if (fs.init() != null) {
	    if (fs.init().size() > 0) {
		// there are some children - if the first is a localDecl so are the rest!
		if (fs.init().child(0) instanceof LocalDecl) {
		    LocalDecl ld = (LocalDecl)fs.init().child(0);
		    Log.logNoNewline(ld.type().typeName() + " ");
		    for (int i=0; i<fs.init().size(); i++) {
			ld = (LocalDecl)fs.init().child(i);
			ld.var().visit(this);
			if (i < fs.init().size()-1)
			    Log.logNoNewline(",");
		    }
		} else {
		    for (Statement es : fs.init()) 
			es.visit(this);
		}
	    }
    	}
    	Log.logNoNewline(";");
    	if (fs.expr() != null) 
    		fs.expr().visit(this);
    	Log.logNoNewline(";");
    	if (fs.incr() != null) {
	    for (int i=0; i<fs.incr().size(); i++) {
		if (fs.incr().child(i) instanceof ExprStat) {
		    ExprStat es = (ExprStat)fs.incr().child(i);
		    es.expr().visit(this);
		}
	    }
	    
    	}
    	Log.logNoNewline(")");
    	if (fs.stats() instanceof Block) {
    		Log.log(" {");
    		indent += 2;
    		((Block)fs.stats()).stats().visit(this);
    		indent -= 2;
    		Log.log(tab() + "}");
    	} else {
    		Log.log("");
    		fs.stats().visit(this);
    	}
    	return null;
    }
    public T visitGuard(Guard gu) {
    	if (gu.guard() instanceof ExprStat)
    		((ExprStat)gu.guard()).expr().visit(this);
    	else if (gu.guard() instanceof SkipStat)
    		Log.logNoNewline("skip");
    	else if (gu.guard() instanceof TimeoutStat) {
    		TimeoutStat ts = (TimeoutStat) gu.guard();
    		ts.timer().visit(this);
    		Log.logNoNewline(".timeout(");
    		ts.delay().visit(this);
    		Log.logNoNewline(")");
    	}
    	return null;
    }    		
    public T visitIfStat(IfStat is) {
    	Log.logNoNewline(tab());
    	Log.logNoNewline("if (");
    	is.expr().visit(this);
    	Log.logNoNewline(")");
    	if (is.thenpart() instanceof Block) 
    	  Log.log(" {");
    	else
    	  Log.log("");
    	indent += 2;
    	if (is.thenpart() instanceof Block)
    		((Block)is.thenpart()).stats().visit(this);
    	else 
    		is.thenpart().visit(this);
    	indent -= 2;
    	if (is.thenpart() instanceof Block) 
    		Log.logNoNewline(tab() + "}");

    	if (is.thenpart() instanceof Block && is.elsepart() != null)
    		Log.logNoNewline(" else");
    	if (!(is.thenpart() instanceof Block) && is.elsepart() != null)
    		Log.logNoNewline(tab() + "else");
    	if (is.thenpart() instanceof Block && is.elsepart() == null)
    		Log.log("");
    		
    	if (is.elsepart() != null) {
    		if (is.elsepart() instanceof Block)
    			Log.log(" {");
    		else
    			Log.log("");
    		indent += 2;
    		if (is.elsepart() instanceof Block)
    			((Block)is.elsepart()).stats().visit(this);
    		else
    			is.elsepart().visit(this);
    		indent -= 2;
    		if (is.elsepart() instanceof Block)
    			Log.log(tab() + "}");
    	}
    	return null;
    }
    public T visitImport(Import im) {
    	//Log.logNoNewline(tab() + "import " + im.packageName() + ".");
    //	if (im.all())
   // 		Log.log("*;");
  //  	else 
  //  		Log.log(im.file() + ";");
    	return null;
    }
    public T visitInvocation(Invocation in) {
    	// TODO
    	return in.visitChildren(this);
    }
    public T visitLocalDecl(LocalDecl ld) {
    	if (ld.isConst())
    		Log.logNoNewline("const ");
    	ld.type().visit(this);
    	Log.logNoNewline(" ");
    	ld.var().visit(this);
    	return null;
    }
    public T visitModifier(Modifier mo) {
    	Log.logNoNewline(mo.toString());
    	return null;
    }
    public void printModifierSequence(Sequence<Modifier> mods) {
	int i = 0;
	for (Modifier m : mods) {
	    m.visit(this);
	    if (i<mods.size()-1)
		Log.logNoNewline(" ");
	    i++;
    	}
    }
    public T visitName(Name na) {
    	Log.logNoNewline(na.getname());
    	return null;
    }
    public T visitNamedType(NamedType nt) {
    	nt.name().visit(this);
    	return null;
    }
    public T visitNameExpr(NameExpr ne) {
    	ne.name().visit(this);
    	return null;
    }
    public T visitNewArray(NewArray ne) {
    	// TODO
    	return ne.visitChildren(this);
    }
    public T visitNewMobile(NewMobile nm) {
    	Log.logNoNewline(tab()+"new mobile ");
    	nm.name().visit(this);
    	return null;
	}
    public T visitParamDecl(ParamDecl pd) {
    	if (pd.isConstant())
    		Log.logNoNewline("const ");
    	pd.type().visit(this);
    	Log.logNoNewline(" ");
    	pd.paramName().visit(this);
    	return null;
    }
    public T visitParBlock(ParBlock pb) {
    	// TODO - don't forget that there are barriers to enroll on.
    	return pb.visitChildren(this);
    }
    public T visitPragma(Pragma pr) {
	Log.log(tab() + "#pragma " + pr.pname() + " " + (pr.value() == null ? "" : pr.value()));
	return null;
    }
    public T visitPrimitiveLiteral(PrimitiveLiteral li) {
    	Log.logNoNewline(li.getText());
    	return null;
    }
    public T visitPrimitiveType(PrimitiveType pt) {
    	Log.logNoNewline(pt.typeName());
    	return null;
    }
    public T visitProcTypeDecl(ProcTypeDecl pd) {
	    Log.logNoNewline(tab());
    	printModifierSequence(pd.modifiers());
    	if (pd.modifiers().size() > 0)
    		Log.logNoNewline(" ");
    	pd.returnType().visit(this);
    	Log.logNoNewline(" ");
    	pd.name().visit(this);
    	Log.logNoNewline("(");	
    	for (int i=0; i<pd.formalParams().size(); i++) {
	    pd.formalParams().child(i).visit(this);
	    if (i<pd.formalParams().size()-1)
    			Log.logNoNewline(", ");
    	}
    	Log.logNoNewline(")");
    	if (pd.implement().size() > 0) {
    		Log.logNoNewline(" implements ");
    		for(int i=0;i<pd.implement().size(); i++) {
		    pd.implement().child(i).visit(this);
		    if (i<pd.implement().size()-1)
    				Log.logNoNewline(", ");
    		}
    	}
    	
    	if (pd.body() != null) {
    		Log.log(" {");
    		indent += 2;
        	pd.body().stats().visit(this);
        	indent -= 2;
        	Log.log(tab() + "}");
    	} else 
    	  Log.log(" ;");

    	return null;
    }
    public T visitProtocolLiteral(ProtocolLiteral pl) {
    	// TODO
    	return pl.visitChildren(this);
    }
    public T visitProtocolCase(ProtocolCase pc) {
    	// TODO
    	return pc.visitChildren(this);
    }
    public T visitProtocolTypeDecl(ProtocolTypeDecl pd) {
    	// TODO
    	return pd.visitChildren(this);
    }
    public T visitRecordAccess(RecordAccess ra) {
    	ra.record().visit(this);
    	Log.logNoNewline(".)");
    	ra.field().visit(this);
    	return null;
    }
    public T visitRecordLiteral(RecordLiteral rl) {
    	// TODO
    	return rl.visitChildren(this);
    }
    public T visitRecordMember(RecordMember rm) {
    	Log.logNoNewline(tab());
    	rm.type().visit(this);
    	Log.logNoNewline(" ");
    	rm.name().visit(this);
    	Log.log(";");
    	return null;
    }
    public T visitRecordTypeDecl(RecordTypeDecl rt) {
    	Log.logNoNewline(tab());
    	printModifierSequence(rt.modifiers());
    	if (rt.modifiers().size() > 0)
    		Log.logNoNewline(" ");
    	Log.logNoNewline("record ");
    	rt.name().visit(this);
    	if (rt.extend().size() > 0) {
    		Log.logNoNewline(" extends ");
    		for (int i=0;i<rt.extend().size(); i++) {
		    rt.extend().child(i).visit(this);
		    if (i<rt.extend().size()-1)
    				Log.logNoNewline(", ");
    		}
    	}
    	Log.log(" {");
    	indent += 2;
    	rt.body().visit(this);
    	indent -= 2;
    	Log.log(tab() + "}");
    	return null;
    }
    public T visitReturnStat(ReturnStat rs) {
    	Log.logNoNewline("return");
    	if (rs.expr() != null) {
    		Log.logNoNewline(" ");
    		rs.expr().visit(this);
    	}
    	return null;
    }
    public T visitSequence(Sequence se) {
    	se.visitChildren(this);
    	return null;
    }
    public T visitSkipStat(SkipStat ss) {
    	Log.log("skip;");
    	return null;
    }
    public T visitStopStat(StopStat ss) {
    	Log.log("stop;");
    	return null;
    }
    public T visitSuspendStat(SuspendStat ss) {
    	Log.logNoNewline("suspend resume with (");
    	ss.params().visit(this);
    	Log.logNoNewline(")");
    	return null;
    }
    public T visitSwitchGroup(SwitchGroup sg) {
    	// TODO
    	return sg.visitChildren(this);
    }
    public T visitSwitchLabel(SwitchLabel sl) {
    	// TODO
    	return sl.visitChildren(this);
    }
    public T visitSwitchStat(SwitchStat st) {
    	// TODO
    	return st.visitChildren(this);
    }
    public T visitSyncStat(SyncStat st) {
    	Log.logNoNewline("sync(");
    	st.barrier().visit(this);
    	Log.logNoNewline(")");
    	return null;
    }
    public T visitTernary(Ternary te) {
    	te.expr().visit(this);
    	Log.logNoNewline(" ? ");
    	te.trueBranch().visit(this);
    	Log.logNoNewline(" : ");
    	te.falseBranch().visit(this);
    	return null;
    }
    public T visitTimeoutStat(TimeoutStat ts) {
    	ts.timer().visit(this);
    	Log.logNoNewline(".timeout(");
    	ts.delay().visit(this);
    	Log.logNoNewline(")");
    	return null;
    }
    public T visitUnaryPostExpr(UnaryPostExpr up) {
    	up.expr().visit(this);
    	Log.logNoNewline(up.opString());
    	return null;
    }
    public T visitUnaryPreExpr(UnaryPreExpr up) {
    	Log.logNoNewline(up.opString());
    	up.expr().visit(this);
    	return null;
    }
    public T visitVar(Var va) {
    	Log.logNoNewline(va.name().getname());
    	if (va.init() != null) {
    		Log.logNoNewline(" = ");
    		va.init().visit(this);
    	}
    	return null;
    }
    public T visitWhileStat(WhileStat ws) {
    	Log.logNoNewline(tab() + "while (");
    	ws.expr().visit(this);
    	Log.logNoNewline(")");
    	if (ws.stat() instanceof Block) {
    		Log.log(" {");
    		indent += 2;
    		((Block)ws.stat()).stats().visit(this);
    		indent -= 2;
    		Log.log(tab() + "}");
    	} else
    		ws.stat().visit(this);
    	return null;
    }
} 
