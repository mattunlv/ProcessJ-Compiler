package AST;
import AST.Sequence;
import Utilities.Visitor;

public class Import extends AST {
    // all the compilations that this import perform are stored in compilations.
    private Sequence<Compilation> compilations = new Sequence();	

    public Import(Sequence<Name> imp) {
        super(imp);
        nchildren = 2;
        int size = imp.children.size();
        Name file = imp.children.remove(size-1);
        children = new AST [] { imp, file };
    }

    public Sequence<Name> path() { return (Sequence<Name>)children[0]; }
    public Name file()    { return (Name)children[1]; }

    public boolean importAll() { return file().getname().equals("*"); }

    public String toString() {
	String str = "";

	for (int i=0;i<path().size(); i++) {
	    str += path().child(i);
	    if (i<path().size())
		str += ".";
	}
	str += file().getname();
	return str;
    }

    public void addCompilation(Compilation co) {
        compilations.append(co);
    }

    public Sequence<Compilation> getCompilations() {
        return compilations;
    }

    public <S extends Object> S visit(Visitor<S> v) {
        return v.visitImport(this);
    }
}
