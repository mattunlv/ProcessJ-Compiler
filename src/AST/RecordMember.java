package AST;
import Utilities.Visitor;

public class RecordMember extends AST {

	public RecordMember(Type type, Name name) {
		super(type);
		nchildren = 2;
		children = new AST [] { type, name };
	}

	public Type type() { return (Type)children[0]; }
	public Name name() { return (Name)children[1]; }

	public <S extends AST> S visit(Visitor<S> v) {
		return v.visitRecordMember(this);
	}
}
