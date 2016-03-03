package AST;
import Utilities.Visitor;

public class Ternary extends Expression {

	public Ternary(Expression expr, Expression trueBranch, Expression falseBranch) {
		super(expr);
		nchildren = 3;
		children = new AST[] { expr, trueBranch, falseBranch };
	}

	public Expression expr()        { return (Expression)children[0]; }
	public Expression trueBranch()  { return (Expression)children[1]; }
	public Expression falseBranch() { return (Expression)children[2]; }

	public boolean isConstant() {
		return expr().isConstant() &&
				trueBranch().isConstant() &&
				falseBranch().isConstant();
	}

	public Object constantValue() {
		Boolean b = (Boolean)expr().constantValue();
		if (b.booleanValue()) 
			return trueBranch().constantValue();
		return falseBranch().constantValue();
	}

        public <S extends AST> S visit(Visitor<S> v) {
		return v.visitTernary(this);
	}
}
