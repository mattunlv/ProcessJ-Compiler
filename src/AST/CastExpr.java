package AST;
import Utilities.Visitor;

import java.math.*;

public class CastExpr extends Expression {

	public CastExpr(Type ct, Expression expr) {
		super(ct);
		nchildren = 2;
		children = new AST[] { ct, expr };
	}

	public Type       type() { return (Type)children[0];       }
	public Expression expr() { return (Expression)children[1]; }

	public boolean isConstant() {
		return expr().isConstant();
	}


	public Object constantValue() {
		if (type().isIntegralType()) 
			return new BigDecimal(((BigDecimal)expr().constantValue()).toBigInteger());
		return expr().constantValue();
	}


        public <S extends AST> S visit(Visitor<S> v) {
		return v.visitCastExpr(this);
	}
}
