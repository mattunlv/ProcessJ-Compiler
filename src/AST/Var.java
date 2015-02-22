package AST;
import Utilities.Visitor;

public class Var extends AST {
	public VarDecl myDecl = null;

	/* Note init() can return null */

	public Var(Name name, Expression init) { 
		super(name);
		nchildren = 2;
		children = new AST[] { name, init };
	}

	public Var(Name name) {
		super(name);
		nchildren = 2;
		children = new AST[] { name, null };
	}

	public Name       name() { return (Name)children[0];       }
	public Expression init() { return (Expression)children[1]; }

	public String toString() {
		return name().toString();
	}

	public <S extends AST> S visit(Visitor<S> v) {
		return v.visitVar(this);
	}

}
