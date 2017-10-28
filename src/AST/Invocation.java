package AST;

import Utilities.SymbolTable;
import Utilities.Visitor;

public class Invocation extends Expression {

    public ProcTypeDecl targetProc = null;
    public SymbolTable candidateMethods = null; // TODO: needs to be filled recursively backwards ..  I think? or perhaps not!

    public Invocation(Expression target, Name name, Sequence<Expression> params) {
        super(name);
        nchildren = 3;
        children = new AST[] { target, name, params };
    }

    public String toString() {
        String s = (target() == null ? "" : target() + ".") + procedureName()
                + "(";
        for (int i = 0; i < params().size(); i++) {
            s += params().child(i);
            if (i < params().size() - 1)
                s += ",";
        }
        s += ")";
        return s;
    }

    public Expression target() {
        return (Expression) children[0];
    }

    public Name procedureName() {
        return (Name) children[1];
    }

    public Sequence<Expression> params() {
        return (Sequence<Expression>) children[2];
    }

    public <S extends Object> S visit(Visitor<S> v) {
        return v.visitInvocation(this);
    }
}
