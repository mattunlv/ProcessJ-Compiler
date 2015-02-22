package AST;
import Utilities.Visitor;

public class SwitchLabel extends AST {

	private boolean isDefault = false;

	public SwitchLabel(Expression const_expr, boolean def) {
		super(const_expr);
		nchildren = 1;
		children = new AST[] { const_expr };
		isDefault = def;
	}

	public Expression expr()  { return (Expression)children[0]; }

	public boolean isDefault() {
		return isDefault;
	}

        public <S extends AST> S visit(Visitor<S> v) {
		return v.visitSwitchLabel(this);
	}
}

