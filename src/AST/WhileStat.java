package AST;
import Utilities.Visitor;

public class WhileStat extends LoopStatement {

	public WhileStat(Expression expr, Statement stat) {
		super(expr);
		nchildren = 2;
		children = new AST[] { expr, stat };
	}

	public Expression expr() { return (Expression)children[0]; }
	public Statement  stat() { return (Statement)children[1];  }

	public <S extends Object> S visit(Visitor<S> v) {
		return v.visitWhileStat(this);
	}
}
