package AST;
import Utilities.Visitor;

public class Compilation extends AST {

	public Compilation(Sequence<Pragma> pragmas, 
			   Name packageName, 
			   Sequence<Import> imports, 
			   Sequence<Type> typeDecls) {
		super(typeDecls);
		nchildren = 4;
		children = new AST[] { pragmas, packageName, imports, typeDecls };
	}

	public Sequence<Pragma> pragmas()   { return (Sequence)children[0]; }
	public Name packageName()           { return (Name)children[1]; }
	public Sequence<Import> imports()   { return (Sequence<Import>)children[2]; }
	public Sequence<Type> typeDecls()   { return (Sequence<Type>)children[3]; }

        public <S extends AST> S visit(Visitor<S> v) {
		return v.visitCompilation(this);
	}
}
