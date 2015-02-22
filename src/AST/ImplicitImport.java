package AST;

import AST.*;
import Utilities.Visitor;

public class ImplicitImport extends Expression {
	public ImplicitImport(Name packageName, Name fileName, Name typeName) {
		super((AST)null);
		nchildren = 3;
		children = new AST[] { packageName, fileName, typeName };
	}

	public Name packageName() { return (Name)children[0]; }
	public Name fileName() { return (Name)children[1]; }
	public Name typeName() { return (Name)children[2]; }

	public String toString() {
		return ((packageName() == null) ? "" : packageName().getname()) + "." + fileName().getname() + "." + typeName().getname();			
	}
	
	
	public <S extends AST> S visit(Visitor<S> v) {
		return v.visitImplicitImport(this);
	}
}