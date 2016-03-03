package AST;
import Utilities.Visitor;

public class ParamDecl extends AST implements VarDecl  {

	boolean constant;

	public ParamDecl(Type type, Name name, boolean constant) {
		super(type);
		nchildren = 2;
		this.constant = constant;
		children = new AST[] { type, name };
	}

	public boolean isConstant() { return constant; }
	public Type type()      { return (Type)children[0]; }
	public Name paramName() { return (Name)children[1]; }

	public void setType(Type t) {
		children[0] = t;
	}
	
	public String name() {
		return paramName().getname();
	}

	public String toString() {
		return "ParamDecl (Type:" + type() + " " + "Name:" + name() + ")";
	}

        public <S extends AST> S visit(Visitor<S> v) {
		return v.visitParamDecl(this);
	}
}
