package AST;
import Utilities.Visitor;

public class Pragma extends AST {

	private String value;

	public Pragma(Name pname, String value) {
		super(pname);
		nchildren = 1;
		this.value = value;
		children = new AST[] { pname };
	}

	public Name pname()      { return (Name)children[0]; }
	public String value()    { return value; }

        public <S extends AST> S visit(Visitor<S> v) {
		return v.visitPragma(this);
	}
}
