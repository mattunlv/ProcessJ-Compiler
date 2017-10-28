package AST;

public abstract class LoopStatement extends Statement {
    public boolean hasBreak = false;
    public boolean hasContinue = false;
    public boolean hasReturn = false;
    public boolean foreverLoop = false;

    public LoopStatement(AST a) {
        super(a);
    }

    public LoopStatement(Token a) {
        super(a);
    }

}