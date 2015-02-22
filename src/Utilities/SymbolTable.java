package Utilities;

import java.util.*;

/** The symbol table class.  Each symbol table contains a Vector that
 * contains the symbols defined in the scope that it corresponds to, and a
 * reference to the symbol table for its enclosing scope (if any) or to
 * the closest import statement going backwards. This reference is the
 * 'parent' and is used for name resolution.
 * A second reference might be needed for imports that themselves import 
 * other files/libraries in order to name check correctly; however,
 * these symbol tables cannot be used for looking up names in the main program,
 * so they are kept separate in an 'importParent' field.
 * 
 */

public class SymbolTable implements AST.TopLevelDecl {
	public static SymbolTable hook = null;
	// this hook is used to hold on the global type table 
	// and to transport the closest table from TopLevelDecls.
	
	
	// Hack
	public boolean isMobileProcedure = false;

	private SymbolTable parent = null;
	private SymbolTable importParent = null; // see description above.
	private String name;
	
	public Hashtable<String, Object> entries;

	public SymbolTable() {
		this("<anonymous>");
	}
	
	public SymbolTable(String name) {
		parent = null;
		this.name = name;
		entries = new Hashtable<String, Object>();
	}

	public SymbolTable(SymbolTable parent, String name) {
		this(name);
		this.parent = parent;
	}
	
	public SymbolTable(SymbolTable parent) {
		this(parent,"<anonymous>");
		this.parent = parent;
	}
	

	// ----------
	
	public void setParent(SymbolTable st) {
		parent = st;
	}

	public void remove(String name) {
		entries.remove(name);
	}

	public SymbolTable getParent() {
		return parent;
	}

	public void setImportParent(SymbolTable st) {
		importParent = st;
	}
	
	public SymbolTable getImportParent() {
		return importParent;
	}
	
	
	
	public String getname() { return name; }
	
	/**
	 * Enters a new entry into the symbol table.
	 * @param name The name of the entry object.
	 * @param entry The entry.
	 */
	public boolean put(String name, Object entry) {
		Object lookup = entries.get(name);
		if (lookup != null) 
			return false;					
		entries.put(name,entry);	
		return true;
	}

	/**
	 * Check if a symbol table (or its parents) contains an entry by the name.
	 * @param name The name of the entry for which we are looking.
	 * @return The associated object - null if no entry is found by that name.
	 */
	public Object get(String name) {
		Object result = entries.get(name);
		if (result!=null)
			return result;
		if (parent==null) {
			return null;
		}
		return parent.get(name);
	}

	
	
	public Object getIncludeImports(String name) {
		/*System.out.println("+=+=+=+=+");
		this.print("          ");
		
		System.out.println("+=+=+=+=+");
*/
		Object result = entries.get(name);
		if (result != null)
			return result;
		if (importParent == null)
			return null;
		return importParent.get(name);
	}
	
	
	public Object getShallow(String name) {
		Object result = entries.get(name);
		return result;
	}

	
	
	public String toString() {
		String s = "Name: " + name + "\n";
		if (parent != null)
			s = "\n" + parent.toString();
		return entries.toString() + s;
	}

	public void print(String indent) {
		if (parent != null) {
			parent.print(indent + "  ");
			System.out.println(indent + "-->");
		}
		Enumeration<String> col = entries.keys();
		System.out.println(indent + "Name: " + name + "(" + entries.size() + ")");
		System.out.println(indent + "Has importParent? " + (importParent != null));
		for (; col.hasMoreElements(); ) {
			String element = col.nextElement();
			Object o = entries.get(element);
			System.out.print(indent + element + "\t" + (o.getClass().getName().equals("Utilities.SymbolTable") ? "Procedure: " : o.getClass().getName()));
			if (o instanceof SymbolTable) {
				System.out.println("\n" + indent+"  --------------------------------");
				((SymbolTable)o).print(indent + "  ");
				System.out.println(indent + "  ================================");
				
			} else { System.out.println(); }
		}
	}


	/**
	 * Opens a new scope and returns it.
	 * @return The new scope.
	 */
	public SymbolTable openScope() {
		return new SymbolTable(this, "<anonymous>");
	}

	public SymbolTable openScope(String name) {
		return new SymbolTable(this, name);
	}
	/** 
	 * Closes the current scope and returns its parent scope
	 * @return the current scope's parent scope.
	 */
	public SymbolTable closeScope() {
		return parent;
	}

	// result of the -sts compiler flag
	public void printStructure(String indent) {
		System.out.println(indent + "name.........: " + this.name);
		System.out.println(indent + "Content " + entries);		
		System.out.println(indent + "parent.......: " + (parent == null ? "--//" : ""));
		if (parent != null)
			parent.printStructure(indent + "|  ");;
		System.out.println(indent + "importParent.: " + (importParent == null ? "--//" : ""));
		if (importParent != null)
			importParent.printStructure(indent + "|  ");
	}
	
}




