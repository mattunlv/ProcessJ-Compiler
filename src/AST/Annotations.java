package AST;
import Utilities.Visitor;

public class Annotations extends AST {

    java.util.Hashtable<String,String> annotations;

    public Annotations() {
	annotations = new java.util.Hashtable<String,String>();
    }

    public Annotations(Sequence /* Annotation */  s) {
	annotations = new java.util.Hashtable<String,String>();
	for (int i=0; i<s.size(); i++) {
	    Annotation a = (Annotation)s.child(i);
	    annotations.put(a.getName(), a.getValue());
	}
    }

    
    public void add(String name, String value) {
	annotations.put(name, value);
    }

    public boolean isDefined(String name) {
	return annotations.containsKey(name);
    }

    public String get(String name) {
	return annotations.get(name);
    }

    public String toString() {
	return annotations.toString();
    }
	



    public <S extends AST> S visit(Visitor<S> v) {
		return v.visitAnnotations(this);
	}
}
