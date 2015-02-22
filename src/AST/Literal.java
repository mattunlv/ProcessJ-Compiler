package AST;
import Utilities.Error;
import Utilities.Visitor;

public abstract class Literal extends Expression {    

	public Type type;

	public Literal(Token t) {
		super(t);
	}

	public Literal(AST a) {
		super(a);
	}

}









