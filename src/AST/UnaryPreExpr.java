package AST;

import Utilities.Visitor;

import java.math.*;

public class UnaryPreExpr extends Expression {

	public static final int PLUSPLUS   = 0; // ++
	public static final int MINUSMINUS = 1; // --
	public static final int PLUS       = 2; // +
	public static final int MINUS      = 3; // -
	public static final int COMP       = 4; // ~
	public static final int NOT        = 5; // !
	private static final String [] opSyms = { "++", "--", "+", "-", "~", "!" };

	private int kind;

	public UnaryPreExpr(Expression expr, int op) {
		super(expr);
		nchildren = 1;
		kind = op;
		children = new AST[] { expr };
	}

	public Expression expr() { return (Expression) children[0];  }
	public int op()          { return kind; }

	public String opString() { 
		return opSyms[kind]; 
	}

	public boolean isConstant() {
		if (kind != PLUSPLUS && kind != MINUSMINUS)
			return expr().isConstant();
		return false;
	}

	public Object constantValue() {
		Object o = expr().constantValue();
		if (o instanceof Boolean)
			return new Boolean(!((Boolean) o).booleanValue());

		BigDecimal val = (BigDecimal) o;
		switch (kind) {
		case PLUS:
			return val;
		case MINUS:
			return val.negate();
		case COMP:
			return new BigDecimal(Integer.toString(~val.intValue()));
		}
		return null;
	}

    public <S extends AST> S visit(Visitor<S> v) {
	return v.visitUnaryPreExpr(this);
    }
    
}
