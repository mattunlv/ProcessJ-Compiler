package AST;

import Utilities.Visitor;

public class ArrayAccessExpr extends Expression {

	public ArrayAccessExpr(Expression target, Expression index) {
		super(target);
		nchildren = 2;
		children = new AST[] { target, index };
	}

	public Expression target() { return (Expression)children[0]; }
	public Expression index()  { return (Expression)children[1]; }

	public String toString() {
		return target() + " [" + index() + "]";
	}

        public <S extends AST> S visit(Visitor<S> v) {
		return v.visitArrayAccessExpr(this);
	}
}
