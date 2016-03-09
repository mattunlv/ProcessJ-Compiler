package ParallelUsageCheck;
import Utilities.Visitor;
import Utilities.Error;
import java.util.Hashtable;
import AST.*;
import Utilities.Log;

public class ParallelUsageCheck extends Visitor<Object> {

    private Hashtable<String,AST> readSet;
    private Hashtable<String,AST> writeSet;

    private boolean inPar = false;


    public ParallelUsageCheck() {
        Log.logHeader("********************************************");
        Log.logHeader("* P A R A L L E L   U S A G E    C H E C K *");
        Log.logHeader("********************************************");
    }



    public Object visitParBlock(ParBlock pb) {
	Log.log(pb,"Visiting a Par Block.");
	boolean oldInPar = inPar;
	inPar = true;
	
	// if this is an outermost par block - create new tables.
	if (!oldInPar) {
	    Log.log(pb,"Outermost par - creating new read and wrilte sets.");
	    readSet = new Hashtable<String,AST>();
	    writeSet = new Hashtable<String,AST>();
	}
	
	super.visitParBlock(pb);
	
	inPar = oldInPar;
	return null;
    }

    public Object visitAssignment(Assignment as) {
	if (inPar) { 
	    Log.log(as,"Visiting an assignment.");
	    // the left hand side must go into the read set!
	    // can be NameExpr, ArrayAccessExpr, or RecordAccess
	    if (as.left() instanceof NameExpr) {
		String name = ((NameExpr)as.left()).name().getname();
		if (writeSet.containsKey(name))
		    Error.error(as,"Parallel write access to variable `" + name + "` illegal.", false, 0000);
		else {
		    Log.log(as,"NameExpr: '" + name + "' is added to the write set.");
		    writeSet.put(name, as.left());
		}
	    } else if (as.left() instanceof RecordAccess) {
		



		Log.log(as,"RecordAccess: " + as.left());
	    } else if (as.left() instanceof ArrayAccessExpr) {
		Log.log(as,"ArrayAccessExpr: " + as.left());
	    }
	}
	return null;
    }
    
    public Object visitNameExpr(NameExpr ne) {
	if (inPar) { 
	    Log.log(ne,"Visiting a Name Expr.");
	    // This should only be reads!
	    String name = ne.name().getname();
	    if (writeSet.containsKey(name))
		Error.error(ne,"Parallel read and write access to variable '" + name + "' illegal.", false, 0000);
	    else {
		Log.log(ne,"NameExpr: '" + name + "' is added to the read set.");
		readSet.put(name, ne);
	    }
	}
	return null;
    }
	    
}