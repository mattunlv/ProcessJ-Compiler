package AST;
import Utilities.Visitor;

public class ChannelReadExpr extends Expression {

	public ChannelReadExpr(Expression channel, Block extRV) {
		super(channel);
		nchildren = 2;
		children = new AST [] { channel, extRV };
	}

	public Expression channel() { return (Expression)children[0]; }
	public Block extRV()        { return (Block)children[1]; }

        public <S extends AST> S visit(Visitor<S> v) {
		return v.visitChannelReadExpr(this);
	}
}
