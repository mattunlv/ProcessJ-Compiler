package AST;
import Utilities.Visitor;

public class ProtocolCase extends AST {

	public ProtocolCase(Name caseName, Sequence<RecordMember> body) {
		super(caseName);
		nchildren = 2;
		children = new AST [] { caseName, body };
	}

	public Name name()     { return (Name)children[0]; }
	public Sequence<RecordMember> body() { return (Sequence<RecordMember>)children[1]; }

        public <S extends AST> S visit(Visitor<S> v) {
		return v.visitProtocolCase(this);
	}
}
