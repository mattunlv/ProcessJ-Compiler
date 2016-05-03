package AST;

public class Token {
	public int kind;       // Gets its value from sym.java.
	public String lexeme;  // The actual text scanned for this token.
	public int line;       // Tne line number on which this token appears.
	public int charBegin;  // The column number in which the token begins.
	public int charEnd;    // The column number in which the token ends.

	public static final String names[] = {
		"EOF",
		"error",
		""
	};

	public Token(int kind, String text, int line, int charBegin, int charEnd) {
		this.kind = kind;
		this.lexeme = text;
		this.line = line;
		this.charBegin = charBegin;
		this.charEnd = charEnd;
	}

	public String toString() {
		return "Token " + names[kind] + " '" + lexeme + "' @ line: " + line + "[" + charBegin + ".." + charEnd + "]";
	}
}
