package AST;
import Utilities.Visitor;

public class UnaryPostExpr extends Expression {
	public static final int PLUSPLUS   = 0; // ++
	public static final int MINUSMINUS = 1; // --
	public static final String [] opSyms = { "++", "--" };

	private int kind;    

	public UnaryPostExpr(Expression expr, int op) {
		super(expr);
		nchildren = 1;
		kind = op;
		children = new AST[] { expr };
	}

	public String opString() { 
		return opSyms[kind]; 
	}

	public Expression expr() { return (Expression)children[0]; }
	public int op()          { return kind; }


        public <S extends AST> S visit(Visitor<S> v) {
		return v.visitUnaryPostExpr(this);
	}
}
