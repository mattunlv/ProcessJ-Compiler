package AST;
import Utilities.Visitor;

public class SwitchStat extends Statement {

	public SwitchStat(Expression expr,
			Sequence<SwitchGroup> switchGroups) {
		super(expr);
		nchildren = 2;
		children = new AST[] { expr, switchGroups };
	}

	public Expression expr()                    { return (Expression)children[0]; }
	public Sequence<SwitchGroup> switchBlocks() { return (Sequence<SwitchGroup>)children[1]; }

        public <S extends AST> S visit(Visitor<S> v) {
		return v.visitSwitchStat(this);
	}
}

