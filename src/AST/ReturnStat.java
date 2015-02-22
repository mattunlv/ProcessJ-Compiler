package AST;
import Utilities.Visitor;

public class ReturnStat extends Statement {

	private Type type; // this will be the return type of the method in which the ReturnStat appears 

	/* Note that expr() can return null */

	public ReturnStat(Token r, Expression expr) {
		super(r);
		nchildren = 1;
		children = new AST[] { expr };
	}

	public Expression expr() { return (Expression)children[0]; }

	public void setType(Type t) {
		this.type = t;
	}
	public Type getType() {
		return this.type;
	}

        public <S extends AST> S visit(Visitor<S> v) {
		return v.visitReturnStat(this);
	}
}
