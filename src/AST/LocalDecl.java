package AST;
import Utilities.Visitor;

public class LocalDecl extends Statement implements VarDecl  {

	private boolean constant;

	public LocalDecl(Type type, Var var, boolean constant) {
		super(type);
		nchildren = 2;
		this.constant = constant;
		children = new AST[] { type, var };
	}

	public boolean isConst() { return constant; }
	public Type type() { return (Type)children[0]; }
	public Var  var()  { return (Var)children[1];  }

	public void setType(Type t) {
		children[0] = t;
	}
	
	public String name() {
		return var().name().getname();
	}

	public String toString() {
		return "LocalDecl (Type:" + type() + " " + "Name:" + var() + ")";
	}

        public <S extends AST> S visit(Visitor<S> v) {
		return v.visitLocalDecl(this);
	}
}

