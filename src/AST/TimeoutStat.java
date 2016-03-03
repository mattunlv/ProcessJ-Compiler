package AST;
import Utilities.Visitor;

public class TimeoutStat extends Statement {

	public TimeoutStat(Expression timer,  Expression delay) {
		super(timer);
		nchildren = 2;
		children = new AST [] { timer, delay };	
	}

	public Expression timer() { return (Expression)children[0]; }
	public Expression delay() { return (Expression)children[1]; }

        public <S extends AST> S visit(Visitor<S> v) {
		return v.visitTimeoutStat(this);
	}
}
