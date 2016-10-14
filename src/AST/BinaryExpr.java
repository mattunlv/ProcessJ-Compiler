package AST;

import java.math.BigDecimal;

import Utilities.Visitor;

public class BinaryExpr extends Expression {

    public static final int PLUS = 0;
    public static final int MINUS = 1;
    public static final int MULT = 2;
    public static final int DIV = 3;
    public static final int MOD = 4;
    public static final int LSHIFT = 5;
    public static final int RSHIFT = 6;
    public static final int RRSHIFT = 7;
    public static final int LT = 8;
    public static final int GT = 9;
    public static final int LTEQ = 10;
    public static final int GTEQ = 11;
    public static final int INSTANCEOF = 12;
    public static final int EQEQ = 13;
    public static final int NOTEQ = 14;
    public static final int AND = 15;
    public static final int OR = 16;
    public static final int XOR = 17;
    public static final int ANDAND = 18;
    public static final int OROR = 19;

    public static final String[] opSyms = { "+", "-", "*", "/", "%", "<<",
            ">>", ">>>", "<", ">", "<=", ">=", "instanceof", "==", "!=", "&",
            "|", "^", "&&", "||" };

    private int kind;

    public BinaryExpr(Expression left, Expression right, int op) {
        super(left);
        nchildren = 2;
        kind = op;
        children = new AST[] { left, right };
    }

    public Expression left() {
        return (Expression) children[0];
    }

    public Expression right() {
        return (Expression) children[1];
    }

    public int op() {
        return kind;
    }

    public String opString() {
        return opSyms[kind];
    }

    public boolean isConstant() {
        return left().isConstant() && right().isConstant();
    }

    // This method should ONLY be called if both the left and the right expressions
    // are sure to be constant value - otherwise this method will crash!
    public Object constantValue() {
        if (left().type.isBooleanType() && right().type.isBooleanType()) {
            boolean lval = (Boolean) left().constantValue();
            boolean rval = (Boolean) right().constantValue();

            switch (kind) {
                case ANDAND:
                    return new Boolean(lval && rval);
                case OROR:
                    return new Boolean(lval || rval);
                case XOR:
                    return new Boolean(lval ^ rval);
                case AND:
                    return new Boolean(lval & rval);
                case OR:
                    return new Boolean(lval | rval);
                case EQEQ:
                    return new Boolean(lval == rval);
                case NOTEQ:
                    return new Boolean(lval != rval);
            }
        }

        BigDecimal lval = (BigDecimal) left().constantValue();
        BigDecimal rval = (BigDecimal) right().constantValue();

        switch (kind) {
            case PLUS:
                return lval.add(rval);
            case MINUS:
                return lval.subtract(rval);
            case MULT:
                return lval.multiply(rval);
            case DIV:
                if (left().type.isIntegralType()
                        && right().type.isIntegralType())
                    return new BigDecimal(lval.toBigInteger().divide(
                            rval.toBigInteger()));
                new BigDecimal(lval.doubleValue() / rval.doubleValue());
            case LT:
                return new Boolean(lval.compareTo(rval) == -1);
            case GT:
                return new Boolean(lval.compareTo(rval) == 1);
            case LTEQ:
                return new Boolean(lval.compareTo(rval) != 1);
            case GTEQ:
                return new Boolean(lval.compareTo(rval) != -1);
            case MOD:
            case LSHIFT:
            case RSHIFT:
            case RRSHIFT:
            case AND:
            case OR:
            case XOR:
                int lint = lval.intValue();
                int rint = rval.intValue();
                switch (kind) {
                    case MOD:
                        return new BigDecimal(Integer.toString(lint % rint));
                    case LSHIFT:
                        return new BigDecimal(Integer.toString(lint << rint));
                    case RSHIFT:
                        return new BigDecimal(Integer.toString(lint >> rint));
                    case RRSHIFT:
                        return new BigDecimal(Integer.toString(lint >>> rint));
                    case AND:
                        return new BigDecimal(Integer.toString(lint & rint));
                    case OR:
                        return new BigDecimal(Integer.toString(lint | rint));
                    case XOR:
                        return new BigDecimal(Integer.toString(lint ^ rint));
                }
            case EQEQ:
                return new Boolean(lval.equals(rval));
            case NOTEQ:
                return new Boolean(!lval.equals(rval));
        }
        return null;
    }

    public String toString() {
        return left() + " " + opSyms[op()] + " " + right();
    }

    public <S extends Object> S visit(Visitor<S> v) {
        return v.visitBinaryExpr(this);
    }
}
