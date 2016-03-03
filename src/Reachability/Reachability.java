package Reachability;

import Utilities.Visitor;
import AST.*;
import Utilities.Error;
import Utilities.Log;
import java.lang.Boolean;

public class Reachability extends Visitor<Boolean> {

    public Boolean visitIfStat(IfStat is) {
	boolean thenBranch = true;
	boolean elseBranch = true;
	thenBranch = is.thenpart().visit(this);
	if (is.elsepart() != null)
	    elseBranch = is.elsepart().visit(this);
	return new Boolean(thenBranch || elseBranch);
    }

    public Boolean visitWhileStat(WhileStat ws) {
	if (ws.expr().isConstant() && (ws.expr() instanceof PrimitiveLiteral) && (((PrimitiveLiteral)ws.expr()).constantValue().equals("true")))
	    return new Boolean(false);
	ws.stat().visit(this);
	return new Boolean(true);
    }

    public Boolean visitDoStat(DoStat ds) {
	if (ds.expr().isConstant() && ds.expr() instanceof PrimitiveLiteral && (((PrimitiveLiteral)ds.expr()).constantValue().equals("true")))
	    return new Boolean(false);
	ds.stat().visit(this);
	return new Boolean(true);
    }


    public Boolean visitBlock(Block bl) {
	boolean b = true;
	for (int i=0; i<bl.stats().size(); i++) {
	    b = bl.stats().child(i).visit(this);
	    if (!b && bl.stats().size()-1 > i) {
		Error.error("Unreachable code for statements following ..."); 
		return new Boolean(false);
	    }
	}
	if (!b) // last statement was a return as previous returns would have produced an error.
	    return new Boolean(false);
	return new Boolean(true);
    }

    public Boolean visitForStat(ForStat fs) {
	if (fs.expr() == null ||
	    (fs.expr().isConstant() && fs.expr() instanceof PrimitiveLiteral && (((PrimitiveLiteral)fs.expr()).constantValue().equals("true")))) 
	    return new Boolean(false);
	if (fs.stats() != null)
	    fs.stats().visit(this);
	return new Boolean(true);
    }

    //    AltStat
    
    public Boolean visitBreakStat(BreakStat bs) {
	return new Boolean(false);
    }
    
    public Boolean visitChannelWriteStat(ChannelWriteStat cws) {
	return new Boolean(true);
    }
    
    public Boolean visitClaimStat(ClaimStat cs) {
	return new Boolean(true);
    }
	
    public Boolean visitContinueStat(ContinueStat cs) {
	return new Boolean(false);
    }
    
    public Boolean visitExprStat(ExprStat es) {
	return new Boolean(true);
    }
    
    public Boolean visitLocalDecl(LocalDecl ld) {
	return new Boolean(true);
    }

    // TODO: no break, continue or return allowed in a parblock
    //ParBlock.java:public class ParBlock extends Statement {
    public Boolean visitReturnStat(ReturnStat rs) {
	return new Boolean(false);
    }
    
    public Boolean visitSkipStat(SkipStat ss) {
	return new Boolean(true);
    }

    public Boolean visitStopStat(StopStat ss) {
	return new Boolean(false);
    }
	
    public Boolean visitSuspendStat(SuspendStat ss) {
	return new Boolean(true);
    }
    
    public Boolean visitSwitchStat(SwitchStat ss) {
	// TODO finish this!
	return new Boolean(true);
    }

    public Boolean visitSyncStat(SyncStat ss) {
	return new Boolean(true);
    }

    public Boolean visitTimeoutStat(TimeoutStat ts) {
	return new Boolean(true);
    }
}