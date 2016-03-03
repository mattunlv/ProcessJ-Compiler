package AST;
import Utilities.Visitor;

public class ProcTypeDecl extends Type implements TopLevelDecl {

    public boolean isNative = false;
    public String library;           // Name of the library, e.g. math.h
    public String nativeFunction;    // Name of the native function, e.g. fabs
    
    public ProcTypeDecl(Sequence<Modifier> modifiers,
			Type returnType,
			Name name,
			Sequence<ParamDecl> formals,
			Sequence<Name> implement,
			Annotations annotations,
			Block body) {
	super(name);
	nchildren = 7;
	children = new AST [] { modifiers, returnType, name, formals, implement, annotations, body };
    }
    
	public Sequence<Modifier> modifiers()     { return (Sequence<Modifier>)children[0]; }
	public Type returnType()                  { return (Type)children[1]; }
	public Name name()                        { return (Name)children[2]; }
	public Sequence<ParamDecl> formalParams() { return (Sequence<ParamDecl>)children[3]; }
	public Sequence<Name> implement()         { return (Sequence<Name>)children[4]; }
    public Annotations annotations()             { return (Annotations)children[5]; }
    	public Block body()                       { return (Block)children[6]; }


	public String typeName() { return "Proc: " + name(); }

	public String signature() {
		String s = "(";
		for (ParamDecl pd : formalParams()) 
			s = s + pd.type().signature();
		s = s + ")" + returnType().signature();
		return s;
	}

        public <S extends AST> S visit(Visitor<S> v) {
		return v.visitProcTypeDecl(this);
	}
}
