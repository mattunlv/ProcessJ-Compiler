package AST;
import Utilities.Visitor;

public class RecordTypeDecl extends Type implements TopLevelDecl {

	public RecordTypeDecl(Sequence<Modifier> modifiers, 
			Name name, 
			Sequence<AST> extend,
			Sequence<RecordMember> body) {
		super(name);
		nchildren = 4;
		children = new AST [] { modifiers, name, extend, body };
	}

	public Sequence<Modifier> modifiers() { return (Sequence<Modifier>)children[0]; }
	public Name name()                    { return (Name)children[1]; }
	public Sequence<Name> extend()        { return (Sequence<Name>)children[2]; }
	public Sequence<RecordMember> body()  { return (Sequence<RecordMember>)children[3]; }

	public RecordMember getMember(String name) {
		for (RecordMember rm : body()) 
			if (rm.name().getname().equals(name))
				return rm;
		return null;
	}

	public String toString() {
		return typeName();
	}

	public String signature() {
		return "<R" + name().getname() + ";";
	}

	public String typeName() { return "Record: " + name(); }

	public <S extends AST> S visit(Visitor<S> v) {
		return v.visitRecordTypeDecl(this);
	}
}
