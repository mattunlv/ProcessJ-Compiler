package AST;
import Utilities.Visitor;
import Utilities.Error;

public class Modifier extends AST {

	private int modifier = -1;

	public final static int PUBLIC    = 0;
	public final static int PRIVATE   = 1;
	public final static int PROTECTED = 2;
	public final static int NATIVE    = 3;
	public final static int CONST     = 4;
	public final static int MOBILE    = 5;

	public final static int MAX_MODIFIER = MOBILE; // set this to be the biggest number used in the list above.

	public static final String[] names = { "public", "private", "protected", "native", "const", "mobile" };

	public String toString() {
		return names[modifier];
	}


	public Modifier(Token t, int modifier) {
		super(t);
		this.modifier = modifier;
	}

	public Modifier(int modifier) {
		super((AST)null);
		this.modifier = modifier;
	}

	public int getModifier() {
		return this.modifier;
	}

	public static boolean hasModifierSet(Sequence<Modifier> mo, int modifier) {
	    for (Modifier m : mo) {
		if (m.getModifier() == modifier)
		    return true;
	    }
	    return false;
	}

    public static void noRepeats(Sequence<Modifier> mo) {
	int mods[] = new int[MAX_MODIFIER+1];
	for (Modifier m : mo) {
	    if (mods[m.getModifier()] == 1)
		Error.error(mo,"Repeated modifier '" + mo + "'.");
	    else
		mods[m.getModifier()] = 1;
	}
    }

        public <S extends AST> S visit(Visitor<S> v) {
		return v.visitModifier(this);
	}
}
