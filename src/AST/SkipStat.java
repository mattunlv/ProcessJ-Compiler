package AST;
import Utilities.Visitor;

public class SkipStat extends Statement {

	public SkipStat(Token t) {
		super(t);
		nchildren = 0;
	}

        public <S extends AST> S visit(Visitor<S> v) {
		return v.visitSkipStat(this);
	}
}
