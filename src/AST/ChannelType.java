package AST;
import Utilities.Visitor;

public class ChannelType extends Type {

	public static final int SHARED_READ       = 0;
	public static final int SHARED_WRITE      = 1;
	public static final int SHARED_READ_WRITE = 2;
	public static final int NOT_SHARED        = 3;

	public String modSyms[] = { "shared read", "shared write", "shared", ""};

	private int shared;

	public ChannelType(Type baseType, int shared) {
		super(baseType);
		this.shared = shared;
		nchildren = 1;
		children = new AST [] { baseType };
	}

	public int shared()    { return shared; }
	public Type baseType() { return (Type)children[0]; }

	public String modString() { 
		return modSyms[shared]; 
	}

	public String signature() {
		return "{" + baseType().signature() + ";";
	}

	public String toString() {
		return "chan<" + baseType() + ">";
	}

	// TODO: add sharing stuff
	public String typeName() { return "chan<" + baseType() + ">"; }

        public <S extends AST> S visit(Visitor<S> v) {
		return v.visitChannelType(this);
	}
}
