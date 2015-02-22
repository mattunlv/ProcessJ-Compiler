package AST;

import Utilities.Visitor;

public class ArrayLiteral extends Expression {

	public ArrayLiteral(Sequence<Expression> seq) {
		super(seq);
		nchildren = 1;
		children = new AST[] { seq };
	}

	public Sequence elements() {
		return (Sequence<Expression>)children[0];
	}

	// TODO
	public String toString() {
		return "{.,.,.,.,.}";
	}

        public <S extends AST> S visit(Visitor<S> v) {
		return v.visitArrayLiteral(this);
	}
}
