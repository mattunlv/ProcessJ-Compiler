package Reachability;

import Utilities.Visitor;
import AST.*;
import Utilities.Error;
import Utilities.Log;
import java.lang.Boolean;

/**
 * If a visit returns 'true', then is it because the code represented
 * by that node can sometimes run to completion, i.e., never _always_
 * returns or breaks or continues.
 */


public class Reachability extends Visitor<Boolean> {

    LoopStatement loopConstruct = null;
    SwitchStat switchConstruct = null;

    
    public Reachability() {
	Log.logHeader("****************************************");
	Log.logHeader("*        R E A C H A B I L I T Y       *");
	Log.logHeader("****************************************");
    }


    public Boolean visitIfStat(IfStat is) {
	Log.log(is,"Visiting an if-Statement.");
	// if (true) S1 else S2 - S2 is unreachable
	if (is.expr().isConstant() && 
	    (is.expr() instanceof PrimitiveLiteral) &&
	    (((PrimitiveLiteral)is.expr()).constantValue().equals("true")) &&
	    is.elsepart() != null) 
	    Error.error(is, "Else-part of if statement unreachable", false, 5000);
	// if (false) S1 ... - S1 is unreachable
	if (is.expr().isConstant() && 
	    (is.expr() instanceof PrimitiveLiteral) &&
	    (((PrimitiveLiteral)is.expr()).constantValue().equals("false")))
	    Error.error(is, "Then-part of if statement unreachable", false, 5001);
	boolean thenBranch = true;
	boolean elseBranch = true;
	thenBranch = is.thenpart().visit(this);
	if (is.elsepart() != null)
	    elseBranch = is.elsepart().visit(this);
	return new Boolean(thenBranch || elseBranch);
    }

    public Boolean visitWhileStat(WhileStat ws) {
	Log.log(ws,"Visiting a while-statement.");
	LoopStatement oldLoopConstruct = loopConstruct;
	loopConstruct = ws;

	boolean b = ws.stat().visit(this);
	System.out.println("- Can run to completion: " + b);
		System.out.println("- Has a break..........: " + ws.hasBreak);
	System.out.println("- Has a return.........: " + ws.hasReturn);
	System.out.println("- Has a constant expr..: " + ws.expr().isConstant());
	System.out.println("- Expr is Literal......: " + (ws.expr() instanceof PrimitiveLiteral));
	System.out.println("- Is it true? .........: " + (((PrimitiveLiteral)ws.expr()).constantValue()));
	       
	if (ws.expr().isConstant() &&       
	    (ws.expr() instanceof PrimitiveLiteral) && 
	    (((PrimitiveLiteral)ws.expr()).constantValue().equals("true")) &&
	    b &                         // the statement can run to completion
	    !ws.hasBreak && !ws.hasReturn)  // but has no breaks, so it will loop forever
	    {
		Error.error(ws,"While statement is an infinite loop", false, 5002);
		loopConstruct = oldLoopConstruct;
		return new Boolean(false);
	    }
	loopConstruct = oldLoopConstruct;
	if (ws.hasReturn && !b)
	    return new Boolean(false);
	return new Boolean(true);
    }

    public Boolean visitDoStat(DoStat ds) {
	Log.log(ds,"Visiting a do-statement.");
	LoopStatement oldLoopConstruct = loopConstruct;
	loopConstruct = ds;
	if (ds.expr().isConstant() && 
	    ds.expr() instanceof PrimitiveLiteral && 
	    (((PrimitiveLiteral)ds.expr()).constantValue().equals("true"))) {
	    loopConstruct = oldLoopConstruct;
	    return new Boolean(false);
	}
	ds.stat().visit(this);
	loopConstruct = oldLoopConstruct;
	return new Boolean(true);
    }


    public Boolean visitBlock(Block bl) {
	Log.log(bl,"Visiting a block.");
	boolean canFinish = true;
	boolean b = true;
	for (int i=0; i<bl.stats().size(); i++) {
	    b = bl.stats().child(i).visit(this);
	    //System.out.println("can finish: ? " + b);
	    if (!b && bl.stats().size()-1 > i) {
		Error.error(bl.stats().child(i),"Unreachable code for statements following this line.", false, 5003); 
		canFinish = false;
	    } 
	}
	if (!b)
	    canFinish = b;
	return new Boolean(canFinish);
    }

    public Boolean visitForStat(ForStat fs) {
	Log.log(fs,"Visiting a for-statement.");
	LoopStatement oldLoopConstruct = loopConstruct;
	loopConstruct = fs;

	if (fs.expr() == null ||
	    (fs.expr().isConstant() && 
	     (fs.expr() instanceof PrimitiveLiteral) && 
	     (((PrimitiveLiteral)fs.expr()).constantValue().equals("true")))) {
	    loopConstruct = oldLoopConstruct;
	    return new Boolean(false);
	}
	if (fs.stats() != null)
	    fs.stats().visit(this);
	loopConstruct = oldLoopConstruct;
	return new Boolean(true);
    }

    //    AltStat
    
    public Boolean visitBreakStat(BreakStat bs) {
	Log.log(bs,"Visiting a break-statement.");
	if (loopConstruct == null && switchConstruct == null) {
	    Error.error(bs, "Break statement outside loop or switch construct.", false, 0000);
	    return new Boolean(true); // this break doesn't matter cause it can't be here anyways!
	}
	loopConstruct.hasBreak = true;
	return new Boolean(false);
    }
    
    public Boolean visitChannelWriteStat(ChannelWriteStat cws) {
	Log.log(cws,"Visiting a channel-write-statement.");
	return new Boolean(true);
    }
    
    public Boolean visitClaimStat(ClaimStat cs) {
	Log.log(cs,"Visiting a claim-statement.");
	return new Boolean(true);
    }
	
    public Boolean visitContinueStat(ContinueStat cs) {
	Log.log(cs,"Visiting a continue-statement.");
	if (loopConstruct == null) {
	    Error.error(cs, "Continue statement outside loop or switch construct.", false, 0000); 
	    return new Boolean(true); // this continue doesn't matter cause it can't be here anyways!
	}
	loopConstruct.hasContinue = true;
	return new Boolean(false);
    }
    
    public Boolean visitLocalDecl(LocalDecl ld) {
	Log.log(ld,"Visiting a local-decl-statement.");
	return new Boolean(true);
    }

    // TODO: no break, continue or return allowed in a parblock
    //ParBlock.java:public class ParBlock extends Statement {
    public Boolean visitReturnStat(ReturnStat rs) {
	Log.log(rs,"Visiting a return-statement.");
	if (loopConstruct != null)
	    loopConstruct.hasReturn = true;
	return new Boolean(false);
    }
    
    public Boolean visitSkipStat(SkipStat ss) {
	Log.log(ss,"Visiting a skip-statement.");
	return new Boolean(true);
    }

    public Boolean visitStopStat(StopStat ss) {
	Log.log(ss,"Visiting a stop-statement.");
	return new Boolean(false);
    }
	
    public Boolean visitSuspendStat(SuspendStat ss) {
	Log.log(ss,"Visiting a suspend-statement.");
	return new Boolean(true);
    }
    
    public Boolean visitSwitchStat(SwitchStat ss) {
	Log.log(ss,"Visiting a switch-statement.");
	SwitchStat oldSwitchConstruct = switchConstruct;
	switchConstruct = ss;
	// TODO finish this!
	switchConstruct = oldSwitchConstruct;
	return new Boolean(true);
    }

    public Boolean visitSyncStat(SyncStat ss) {
	Log.log(ss,"Visiting a while-statement.");
	return new Boolean(true);
    }

    public Boolean visitTimeoutStat(TimeoutStat ts) {
	Log.log(ts,"Visiting a timeout-statement.");
	return new Boolean(true);
    }

    public Boolean visitExprStat(ExprStat es) {
	Log.log(es,"Visiting an expr-statement.");
	return new Boolean(true);
    }
}