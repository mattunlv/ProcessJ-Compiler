package AST;
import Utilities.Visitor;

public class ChannelWriteStat extends Statement {

	public ChannelWriteStat(Expression channel, Expression expr) {
		super(channel);
		nchildren = 2;
		children = new AST [] { channel, expr };
	}

	public Expression channel() { return (Expression)children[0]; }
	public Expression expr()    { return (Expression)children[1]; }


        public <S extends AST> S visit(Visitor<S> v) {
		return v.visitChannelWriteStat(this);
	}
}
