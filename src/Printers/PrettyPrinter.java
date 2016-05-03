package Printers;

import Utilities.Visitor;
import AST.*;

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
    	println(tab() + s);
    }
    
    public PrettyPrinter() {
	System.out.println("ProcessJ Pretty Print");
	debug = true;
    }
    
    public T visitAltCase(AltCase ac) {
    	print(tab());
    	if (ac.precondition() != null) {
    	  print("(");
    	  ac.precondition().visit(this);
    	  print(") && ");
    	}
    	ac.guard().visit(this);
    	print(" : ");	
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
	    print("[");
    	ae.index().visit(this);
    	print("]");
    	return null;
    }
    public T visitArrayLiteral(ArrayLiteral al) {
    	// TODO
    	return al.visitChildren(this);
    }
    public T visitArrayType(ArrayType at) {
    	at.baseType().visit(this);
    	for (int i=0;i<at.getDepth();i++)
    		print("[]");
    	return null;
    }
    public T visitAssignment(Assignment as) {
    	as.left().visit(this);
    	print(" " + as.opString() + " ");
    	as.right().visit(this);
    	return null;
    }
    public T visitBinaryExpr(BinaryExpr be) {
    	be.left().visit(this);
    	print(" " + be.opString() + " ");
    	be.right().visit(this);
    	return null;
    }
    public T visitBlock(Block bl) {
	    println("{");
	    indent += 2;
	    for (Statement st : bl.stats()) {
	    	if (st == null)	{
	    		indent += 2;
	    		println(tab() + ";");
	    		indent -= 2;
	    	} else {
	    		st.visit(this);
	    		if (st instanceof LocalDecl)
	    			println(";");
	    	}
	    }
	    indent -= 2;
	    println("}");
    	return null;
    }
    public T visitBreakStat(BreakStat bs) {
    	print("break");
        if (bs.target() != null) {
        	print(" ");
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
    	print(modString);
    	if (!modString.equals(""))
    		print(" ");
    	print("chan<");
    	ct.baseType().visit(this);
    	print(">");
    	return null;
    }
    public T visitChannelEndExpr(ChannelEndExpr ce) {
    	ce.channel().visit(this);
    	print("." + (ce.isRead() ? "read" : "write"));
    	return null;
    }
    public T visitChannelEndType(ChannelEndType ct) {
	  if (ct.isShared())
		  print("shared ");
	  print("chan<");
	  ct.baseType().visit(this);
	  print(">." + (ct.isRead() ? "read" : "write"));
	  return null;
    }
    public T visitChannelReadExpr(ChannelReadExpr cr) {
    	cr.channel().visit(this);
    	print(".read(");
    	if(cr.extRV() != null) {
    		println("{");
    		indent += 2;
    		cr.extRV().stats().visit(this);
    		indent -= 2;
    		print("}");
    	}
    	print(")");
    	return null;
    }
    public T visitChannelWriteStat(ChannelWriteStat cw) {
    	cw.channel().visit(this);
    	print(".write(");
    	cw.expr().visit(this);
    	print(")");
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
    	print(tab());
    	printModifierSequence(cd.modifiers());
    	if (cd.modifiers().size() > 0)
    		print(" ");
    	cd.type().visit(this);
    	print(" ");
    	cd.var().visit(this);
    	println(";");
    	return null;
    }
    public T visitContinueStat(ContinueStat cs) {
    	print("continue");
    	if (cs.target() != null) {
    		print(" ");
    		cs.target().visit(this);
    	}
    	return null;
    }
    public T visitDoStat(DoStat ds) {
    	print(tab() + "do ");
    	if (ds.stat() instanceof Block) {
    		println("{");
    		indent += 2;
    		((Block)ds.stat()).stats().visit(this);
    		indent -= 2;
    		print(tab() + "} while (");
    		ds.expr().visit(this);
    		print(")");
    	} else {
    		println("");
    		indent += 2;
    		ds.stat().visit(this);
    		indent -= 2;
    		print(tab() + "while (");
    		ds.expr().visit(this);
    		print(");");    		
    	}
    	println("");
    	return null;	
    }
    public T visitExprStat(ExprStat es) {
    	print(tab());
    	es.expr().visit(this);
    	println("");
        return null;
    }
    public T visitForStat(ForStat fs) {
    	print(tab());
    	print("for (");
    	if (fs.init() != null) {
	    if (fs.init().size() > 0) {
		// there are some children - if the first is a localDecl so are the rest!
		if (fs.init().child(0) instanceof LocalDecl) {
		    LocalDecl ld = (LocalDecl)fs.init().child(0);
		    print(ld.type().typeName() + " ");
		    for (int i=0; i<fs.init().size(); i++) {
			ld = (LocalDecl)fs.init().child(i);
			ld.var().visit(this);
			if (i < fs.init().size()-1)
			    print(",");
		    }
		} else {
		    for (Statement es : fs.init()) 
			es.visit(this);
		}
	    }
    	}
    	print(";");
    	if (fs.expr() != null) 
    		fs.expr().visit(this);
    	print(";");
    	if (fs.incr() != null) {
	    for (int i=0; i<fs.incr().size(); i++) {
		if (fs.incr().child(i) instanceof ExprStat) {
		    ExprStat es = (ExprStat)fs.incr().child(i);
		    es.expr().visit(this);
		}
	    }
	    
    	}
    	print(")");
    	if (fs.stats() instanceof Block) {
    		println(" {");
    		indent += 2;
    		((Block)fs.stats()).stats().visit(this);
    		indent -= 2;
    		println(tab() + "}");
    	} else {
    		println("");
    		fs.stats().visit(this);
    	}
    	return null;
    }
    public T visitGuard(Guard gu) {
    	if (gu.guard() instanceof ExprStat)
    		((ExprStat)gu.guard()).expr().visit(this);
    	else if (gu.guard() instanceof SkipStat)
    		print("skip");
    	else if (gu.guard() instanceof TimeoutStat) {
    		TimeoutStat ts = (TimeoutStat) gu.guard();
    		ts.timer().visit(this);
    		print(".timeout(");
    		ts.delay().visit(this);
    		print(")");
    	}
    	return null;
    }    		
    public T visitIfStat(IfStat is) {
    	print(tab());
    	print("if (");
    	is.expr().visit(this);
    	print(")");
    	if (is.thenpart() instanceof Block) 
    	  println(" {");
    	else
    	  println("");
    	indent += 2;
    	if (is.thenpart() instanceof Block)
    		((Block)is.thenpart()).stats().visit(this);
    	else 
    		is.thenpart().visit(this);
    	indent -= 2;
    	if (is.thenpart() instanceof Block) 
    		print(tab() + "}");

    	if (is.thenpart() instanceof Block && is.elsepart() != null)
    		print(" else");
    	if (!(is.thenpart() instanceof Block) && is.elsepart() != null)
    		print(tab() + "else");
    	if (is.thenpart() instanceof Block && is.elsepart() == null)
    		println("");
    		
    	if (is.elsepart() != null) {
    		if (is.elsepart() instanceof Block)
    			println(" {");
    		else
    			println("");
    		indent += 2;
    		if (is.elsepart() instanceof Block)
    			((Block)is.elsepart()).stats().visit(this);
    		else
    			is.elsepart().visit(this);
    		indent -= 2;
    		if (is.elsepart() instanceof Block)
    			println(tab() + "}");
    	}
    	return null;
    }
    public T visitImport(Import im) {
    	//print(tab() + "import " + im.packageName() + ".");
    //	if (im.all())
   // 		println("*;");
  //  	else 
  //  		println(im.file() + ";");
    	return null;
    }
    public T visitInvocation(Invocation in) {
    	// TODO
    	return in.visitChildren(this);
    }
    public T visitLocalDecl(LocalDecl ld) {
    	if (ld.isConst())
    		print("const ");
    	ld.type().visit(this);
    	print(" ");
    	ld.var().visit(this);
    	return null;
    }
    public T visitModifier(Modifier mo) {
    	print(mo.toString());
    	return null;
    }
    public void printModifierSequence(Sequence<Modifier> mods) {
	int i = 0;
	for (Modifier m : mods) {
	    m.visit(this);
	    if (i<mods.size()-1)
		print(" ");
	    i++;
    	}
    }
    public T visitName(Name na) {
    	print(na.getname());
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
    	print(tab()+"new mobile ");
    	nm.name().visit(this);
    	return null;
	}
    public T visitParamDecl(ParamDecl pd) {
    	if (pd.isConstant())
    		print("const ");
    	pd.type().visit(this);
    	print(" ");
    	pd.paramName().visit(this);
    	return null;
    }
    public T visitParBlock(ParBlock pb) {
    	// TODO - don't forget that there are barriers to enroll on.
    	return pb.visitChildren(this);
    }
    public T visitPragma(Pragma pr) {
	println(tab() + "#pragma " + pr.pname() + " " + (pr.value() == null ? "" : pr.value()));
	return null;
    }
    public T visitPrimitiveLiteral(PrimitiveLiteral li) {
    	print(li.getText());
    	return null;
    }
    public T visitPrimitiveType(PrimitiveType pt) {
    	print(pt.typeName());
    	return null;
    }
    public T visitProcTypeDecl(ProcTypeDecl pd) {
	    print(tab());
    	printModifierSequence(pd.modifiers());
    	if (pd.modifiers().size() > 0)
    		print(" ");
    	pd.returnType().visit(this);
    	print(" ");
    	pd.name().visit(this);
    	print("(");	
    	for (int i=0; i<pd.formalParams().size(); i++) {
	    pd.formalParams().child(i).visit(this);
	    if (i<pd.formalParams().size()-1)
    			print(", ");
    	}
    	print(")");
    	if (pd.implement().size() > 0) {
    		print(" implements ");
    		for(int i=0;i<pd.implement().size(); i++) {
		    pd.implement().child(i).visit(this);
		    if (i<pd.implement().size()-1)
    				print(", ");
    		}
    	}
    	
    	if (pd.body() != null) {
    		println(" {");
    		indent += 2;
        	pd.body().stats().visit(this);
        	indent -= 2;
        	println(tab() + "}");
    	} else 
    	  println(" ;");

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
    	print(".)");
    	ra.field().visit(this);
    	return null;
    }
    public T visitRecordLiteral(RecordLiteral rl) {
    	// TODO
    	return rl.visitChildren(this);
    }
    public T visitRecordMember(RecordMember rm) {
    	print(tab());
    	rm.type().visit(this);
    	print(" ");
    	rm.name().visit(this);
    	println(";");
    	return null;
    }
    public T visitRecordTypeDecl(RecordTypeDecl rt) {
    	print(tab());
    	printModifierSequence(rt.modifiers());
    	if (rt.modifiers().size() > 0)
    		print(" ");
    	print("record ");
    	rt.name().visit(this);
    	if (rt.extend().size() > 0) {
    		print(" extends ");
    		for (int i=0;i<rt.extend().size(); i++) {
		    rt.extend().child(i).visit(this);
		    if (i<rt.extend().size()-1)
    				print(", ");
    		}
    	}
    	println(" {");
    	indent += 2;
    	rt.body().visit(this);
    	indent -= 2;
    	println(tab() + "}");
    	return null;
    }
    public T visitReturnStat(ReturnStat rs) {
    	print("return");
    	if (rs.expr() != null) {
    		print(" ");
    		rs.expr().visit(this);
    	}
    	return null;
    }
    public T visitSequence(Sequence se) {
    	se.visitChildren(this);
    	return null;
    }
    public T visitSkipStat(SkipStat ss) {
    	println("skip;");
    	return null;
    }
    public T visitStopStat(StopStat ss) {
    	println("stop;");
    	return null;
    }
    public T visitSuspendStat(SuspendStat ss) {
    	print("suspend resume with (");
    	ss.params().visit(this);
    	print(")");
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
    	print("sync(");
    	st.barrier().visit(this);
    	print(")");
    	return null;
    }
    public T visitTernary(Ternary te) {
    	te.expr().visit(this);
    	print(" ? ");
    	te.trueBranch().visit(this);
    	print(" : ");
    	te.falseBranch().visit(this);
    	return null;
    }
    public T visitTimeoutStat(TimeoutStat ts) {
    	ts.timer().visit(this);
    	print(".timeout(");
    	ts.delay().visit(this);
    	print(")");
    	return null;
    }
    public T visitUnaryPostExpr(UnaryPostExpr up) {
    	up.expr().visit(this);
    	print(up.opString());
    	return null;
    }
    public T visitUnaryPreExpr(UnaryPreExpr up) {
    	print(up.opString());
    	up.expr().visit(this);
    	return null;
    }
    public T visitVar(Var va) {
    	print(va.name().getname());
    	if (va.init() != null) {
    		print(" = ");
    		va.init().visit(this);
    	}
    	return null;
    }
    public T visitWhileStat(WhileStat ws) {
    	print(tab() + "while (");
    	ws.expr().visit(this);
    	print(")");
    	if (ws.stat() instanceof Block) {
    		println(" {");
    		indent += 2;
    		((Block)ws.stat()).stats().visit(this);
    		indent -= 2;
    		println(tab() + "}");
    	} else
    		ws.stat().visit(this);
    	return null;
    }
} 