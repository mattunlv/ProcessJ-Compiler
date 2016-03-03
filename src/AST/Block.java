package AST;
import Utilities.Visitor;

public class Block extends Statement {
	public Block(Sequence<Statement> stats) {
		super(stats);
		nchildren = 1;
		children = new AST[] { stats };
	}

	public Sequence<Statement> stats() { return (Sequence<Statement>)children[0]; }

        public <S extends AST> S visit(Visitor<S> v) {
		return v.visitBlock(this);
	}
}
