package AST;
import Utilities.Visitor;

public class ChannelEndExpr extends Expression {

	public static final int READ = 0;
	public static final int WRITE = 1;

	private int end;

	public ChannelEndExpr(Expression channel, int end) {
		super(channel);
		this.end = end;
		nchildren = 1;
		children = new AST [] { channel };
	}

	public boolean isRead()  { return end == READ; }
	public boolean isWrite() { return end == WRITE; }
	public Expression channel() { return (Expression)children[0]; }

        public <S extends AST> S visit(Visitor<S> v) {
		return v.visitChannelEndExpr(this);
	}
}
