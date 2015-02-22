package AST;
import Utilities.Visitor;

public class Name extends AST {
	private String id;
	private int arrayDepth = 0; // somewhat of a hack - we keep track of 
	// whether this name is an id in a variable declaration with [] on. 

	// points to what ever packageAccess() resolved to.
	public TopLevelDecl resolvedPackageAccess = null;
	// the package hierarchy caused to be loaded because of the packageAccess
	public Compilation c; 


	public Name(Token p_id) {
		super(p_id);
		nchildren = 1;
		this.id = p_id.lexeme;
		this.arrayDepth = 0;
		children = new AST[] { new Sequence() };
	}

	public Name(String name) {
		super(0,0);
		nchildren = 1;
		this.id = name;
		this.arrayDepth = 0;
		children = new AST[] { new Sequence() };
	}

	public Name(Name n, int arrayDepth) {
		super(n);
		this.id = n.getname(); 
		this.arrayDepth = arrayDepth;
		nchildren = 1;
		children = new AST[] { new Sequence() };
	}

	public Name(Token p_id, Sequence<Name> package_access) {
		super(p_id);
		this.id = p_id.lexeme;
		this.arrayDepth = 0;
		nchildren = 1;
		children = new AST[] { package_access };
	}


	public Sequence<Name> packageAccess() { return (Sequence<Name>)children[0]; }

	public String getname() {
		return toString(); //this.id;  // TODO: changed back to full name 

	}

	public String simplename() {
		return this.id;
	}
	
	
	public boolean isSimple() {
		return packageAccess().size() == 0;
	}
	
	public void setName(String na) {
		id = na;
	}

	public String toString() {
		String s = "";
		if (packageAccess() != null) {
			int size = packageAccess().children.size();
			for (int i =0; i<size; i++) {
				s = s + packageAccess().child(i);
				if (i!= size-1)
					s = s + ".";
				else
					s = s + "::";
			}
		}
		return s + this.id;
	}


	public int getArrayDepth() {
		return this.arrayDepth;
	}
	public void setArrayDepth(int d) {
		this.arrayDepth = d;
	}

	public <S extends AST> S visit(Visitor<S> v) {
		return v.visitName(this);
	}
}




