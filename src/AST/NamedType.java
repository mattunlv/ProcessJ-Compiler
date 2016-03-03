package AST;
import Utilities.Visitor;

public class NamedType extends Type {

    private TopLevelDecl resolvedTopLevelDecl = null; // could be a SymbolTable
   private Type type = null;

	public NamedType(Name name) {
		super(name);
		nchildren = 1;
		children = new AST [] { name  };
	}

	public NamedType(Name name, Type type) {
		this(name);
		this.type = type;
	}

	public Name name() { return (Name)children[0]; }
	public Type type() { return type; }


    public void setType(Type type) {
		this.type = type;
	}

	public String typeName() { 
		return "NamedType: " + name(); 
	}

    public void setResolvedTopLevelDecl(TopLevelDecl td) {
	this.resolvedTopLevelDecl = td;
	}



	public String toString() {
		return typeName();
	}

	public String signature() {
		return "L" + name().getname() + ";";
	}

        public <S extends AST> S visit(Visitor<S> v) {
		return v.visitNamedType(this);
	}
}
