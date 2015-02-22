package AST;
import Utilities.Visitor;

public abstract class AST  {
	/*implements java.io.Serializable*/ // temporarily removed.  
	public int line, charBegin;
	public int nchildren = 0;
	public AST[] children;

	public AST() {
		// this constructor must ONLY be used in ErrorType
	}
	
	
	public AST(int p_line, int p_charBegin) {
		line = p_line;
		charBegin = p_charBegin;
	}

	public AST(Token t) {
		line = t.line;
		charBegin = t.charBegin;
	}

	public AST(AST n) {
		if (n == null) {
			line = 0;
			charBegin = 0;
		} else {
			line = n.line;
			charBegin = n.charBegin;
		}
	}


	public String toString () {
		return "";
	}


	private void tab(java.io.PrintStream out, int amount) {
		int i;
		for (i = 0; i < amount; i++)
			out.print(" ");
	}

	private String intToString(int i, int w) {
		String s = "                    " + Integer.toString(i);
		int length = s.length();
		return s.substring(length - w);
	}

	public void print(java.io.PrintStream out, int depth) {
		out.print("line " + this.intToString(line, 3) + ": ");
		tab(out, depth * 2);
		out.println(this.getClass().getName() + " " + this.toString());
		for (int c = 0; c < nchildren; c++) {
			if (children[c] == null) {
				out.print("line " + this.intToString(line, 3) + ": ");
				tab(out, depth * 2 + 2);
				out.println("empty");
			} else {
				children[c].print(out, depth + 1);
			}
		}
	}

	public String getname () {
		return "Blah.";
	}

	public void print(java.io.PrintStream out) {
		this.print(out, 0);
	}

	public void print() {
		this.print(System.out);
	}

	/* *********************************************************** */
	/* **                                                       ** */
	/* ** Generic Visitor Stuff                                 ** */
	/* **                                                       ** */
	/* *********************************************************** */

	public abstract <T extends AST> T visit(Visitor<T> v);

	/** Visit all children of this node from left to right.  Usually
	called from within a visitor */
	public <T extends AST> T visitChildren(Visitor<T> v) {
	    for (int c = 0; c < nchildren; c++) {		    
		if (children[c] != null) 
		    children[c].visit(v);
	    }
	    return null;
	}
}
