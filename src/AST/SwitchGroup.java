package AST;
import Utilities.Visitor;

public class SwitchGroup extends AST {
	public SwitchGroup(Sequence<SwitchLabel> labels, 
			Sequence<Statement> stmts) {
		super(labels);
		nchildren = 2;
		children = new AST[] { labels, stmts };
	}

	public Sequence<SwitchLabel> labels()   { return (Sequence<SwitchLabel>)children[0]; }
	public Sequence<Statement> statements() { return (Sequence<Statement>)children[1]; }

        public <S extends AST> S visit(Visitor<S> v) {
		return v.visitSwitchGroup(this);
	}

}

