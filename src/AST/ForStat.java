package AST;
import Utilities.Visitor;

public class ForStat extends Statement {

	/* Note that init() and incr() can be null */
	public boolean par;

	public ForStat(Token t, Sequence<Statement> init, 
			Expression expr, 
			Sequence<ExprStat> incr , 
			Sequence<Expression> barriers,
			Statement stat,
			boolean par) {
		super(t);
		nchildren = 5;
		this.par = par;
		children = new AST[] { init, expr, incr, barriers, stat };
	}

	public boolean isPar()                    { return par; }
	public Sequence<Statement>   init()       { return (Sequence<Statement>)children[0];   }
	public Expression expr()                  { return (Expression)children[1]; }
	public Sequence<ExprStat>   incr()        { return (Sequence<ExprStat>)children[2];   }
	public Sequence<Expression>  barriers()   { return (Sequence<Expression>)children[3];   }
	public Statement  stats()                 { return (Statement)children[4];  }

        public <S extends AST> S visit(Visitor<S> v) {
		return v.visitForStat(this);
	}
}
