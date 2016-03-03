package AST;
import Utilities.Visitor;

public class AltCase extends AST {

	/* expr can be null */
	public AltCase(Expression expr, Guard guard, Statement stat) {
		super(guard);
		nchildren = 3;
		children = new AST[] { expr, guard, stat };
	}


	public Expression precondition() { return (Expression)children[0]; }
	public Guard guard()     { return (Guard)children[1]; }
	public Statement  stat() { return (Statement)children[2];  }

        public <S extends AST> S visit(Visitor<S> v) {
		return v.visitAltCase(this);
	}
}
