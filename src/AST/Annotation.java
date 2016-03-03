package AST;
import Utilities.Visitor;

public class Annotation extends AST {

    private String name, value;

    public Annotation(String name, String value) {
	this.name = name;
	this.value = value;
    }
    
    public String getName() { return name; }
    public String getValue() { return value; }
    
    public <S extends AST> S visit(Visitor<S> v) {
	return v.visitAnnotation(this);
    }
}
