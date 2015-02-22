package AST;
import Utilities.Visitor;

public class RecordAccess extends Expression {

    public boolean isArraySize = false;
    public boolean isStringLength = false;


	public RecordAccess(Expression record, Name field) {
		super(record);
		nchildren = 2;
		children = new AST [] { record, field };
	}

	public Expression record() { return (Expression)children[0]; }
	public Name       field()  { return (Name)children[1]; }

    public String toString() {
        return record() + "." + field();
    }


        public <S extends AST> S visit(Visitor<S> v) {
		return v.visitRecordAccess(this);
	}
}
