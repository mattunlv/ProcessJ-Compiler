package AST;

import Utilities.Visitor;

public class DoStat extends LoopStatement {

    public DoStat(Statement stat, Expression expr) {
        super(expr);
        nchildren = 2;
        children = new AST[] { stat, expr };
    }

    public Statement stat() {
        return (Statement) children[0];
    }

    public Expression expr() {
        return (Expression) children[1];
    }

    public <S extends Object> S visit(Visitor<S> v) {
        return v.visitDoStat(this);
    }
}
