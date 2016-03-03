package AST;
import Utilities.Visitor;

public class AltStat extends Statement {

	public boolean pri;

	public AltStat(Sequence<AltCase> body, boolean pri) {
		super(body);
		nchildren = 1;
		this.pri = pri;
		children = new AST[] { body };
	}

	public boolean isPri()  { return pri; }
	public Sequence<AltCase> body()  { return (Sequence<AltCase>)children[0];  }

        public <S extends AST> S visit(Visitor<S> v) {
		return v.visitAltStat(this);
	}
}
