package AST;
import Utilities.Visitor;

public class RecordLiteral extends Literal {

	public RecordTypeDecl myTypeDecl = null; // set in NameChecker/NameChecker/visitRecordLiteral()

	public RecordLiteral(Name name, Sequence<Expression> members) {
		super(name);
		nchildren = 2;
		children = new AST [] { name, members };	
	}

	public Name name()        { return (Name)children[0]; }
	public Sequence<Expression> members() { return (Sequence<Expression>)children[1]; }

        public <S extends AST> S visit(Visitor<S> v) {
		return v.visitRecordLiteral(this);
	}
}
