package AST;

import Utilities.Visitor;

public class ArrayType extends Type {

	private int depth = 0; // How many set of [ ] were there?

	public ArrayType(Type baseType, int depth) {
		super(baseType);
		nchildren = 1;
		this.depth = depth;
		children = new AST[] { baseType };		
	}

	public Type baseType() { 
		return (Type) children[0]; 
	}

    public void setBaseType(Type t) {
        children[0] = t;
        // TODO: be careful about depth .... should it be set back to 0 or should it reflect the correct value.
    }


	public int getDepth() { 
		return depth; 
	}

	public String toString() {
		return "(ArrayType: " + typeName() + ")";
	}

	public String typeName() {
		String s = baseType().typeName();
		for (int i=0; i<depth; i++)
			s = s + "[]";
		return s;
	}

	// TODO: baseType is now never an ArrayType.....   Say what?? sure it is
	public String signature() {
		String s = baseType().signature();
		for (int i=0;i<depth; i++)
			s = "[" + s + ";";
		return s;
	}


        public <S extends AST> S visit(Visitor<S> v) {
		return v.visitArrayType(this);
	}
}             








