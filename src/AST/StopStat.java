package AST;
import Utilities.Visitor;

public class StopStat extends Statement {

	public StopStat(Token t) {
		super(t);
		nchildren = 0;
	}

        public <S extends AST> S visit(Visitor<S> v) {
		return v.visitStopStat(this);
	}
}
