package AST;

public abstract class Expression extends AST {

	public Type type = null;

	public Expression(Token t) {
		super(t);
	}

	public Expression(AST a) {
		super(a);
	}

	public boolean isConstant() {
		return false;
	}

	public Object constantValue() {
		return null;
	}

}
