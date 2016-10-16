package AST;


public abstract class Literal extends Expression {

    public Literal(Token t) {
        super(t);
    }

    public Literal(AST a) {
        super(a);
    }

}
