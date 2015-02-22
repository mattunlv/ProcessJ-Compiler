package AST;
import Utilities.Visitor;

public class ProtocolLiteral extends Literal {

	public ProtocolTypeDecl myTypeDecl = null; // set in NameChecker/NameChecker/visitProtocolLiteral()
	public ProtocolCase myChosenCase = null; // set in NameChecker/NameChecker/visitProtocolLiteral()

	public ProtocolLiteral(Name name, Name tag, Sequence<Expression> expressions) {
		super(name);
		nchildren = 3;
		children = new AST [] { name, tag, expressions };	
	}

	public Name name()        { return (Name)children[0]; }
	public Name tag()         { return (Name)children[1]; }
	public Sequence<Expression> expressions() { return (Sequence<Expression>)children[2]; }

        public <S extends AST> S visit(Visitor<S> v) {
		return v.visitProtocolLiteral(this);
	}
}
