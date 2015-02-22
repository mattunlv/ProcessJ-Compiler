package AST;
import Utilities.Visitor;

public class SuspendStat extends Statement {

	public SuspendStat(Token t, Sequence<ParamDecl> params) {
		super(t);
		nchildren = 1;
		children = new AST [] { params };
	}

	public Sequence<ParamDecl> params() { return (Sequence<ParamDecl>)children[0]; }

	public String signature() {
		String s = "(";
		for (ParamDecl pd : params()) 
			s = s + pd.type().signature();
		s = s + ")V";
		return s;
	}

	public <S extends AST> S visit(Visitor<S> v) {
		return v.visitSuspendStat(this);
	}
}
