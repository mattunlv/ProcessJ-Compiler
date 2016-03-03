package AST;
import Utilities.Visitor;

public class NameExpr extends Expression {    

    public boolean rewriteToRecordAccess = false;
    // this variable is set in NameChecker.visitNameExpr if the leading name of
    // the qualified name name() resolved to a variable or constant.
    public boolean rewriteToImplicitImport = false;
    // this variable is set in NameChecker.visitNameExpr if the leading name of
    // the qualified name name() did not resolve to a variable or constant.
    
    
	// A name expression can represent a variable name (local, parameter) or

	public AST myDecl = null;

	public NameExpr(Name name) {
		super(name);
		nchildren = 1;
		children = new AST[] { name };
	}

    public String toString() {
        return name().getname();
    }

	public Name name() { return (Name)children[0]; }

        public <S extends AST> S visit(Visitor<S> v) {
		return v.visitNameExpr(this);
	}
}









