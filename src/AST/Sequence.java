package AST;
import Utilities.Visitor;
import java.util.*;

public class Sequence<T extends AST> extends AST implements Iterable<T> {


	public ArrayList<T> children = new ArrayList<T>();

	public Sequence() {
		super(0,0);
	}

	
	public Sequence(T element) {
		super(element);
		children.add(element);
	}

	public T child(int i) {
		return children.get(i);
	}

	public Sequence<T> append(T element) {
		children.add(element);
		return this;
	}

	public <S extends T> Sequence<T> merge(Sequence<S> others) {
		for (T e : others)
			children.add(e);
		return this;
	}

	public Iterator<T> iterator() {
		return children.iterator();
	}

	public <S extends T> Sequence<T> merge(S other) {
		children.add(other);
		return this;
	}

	public int size() { 
		return children.size();
	}


	public <W extends AST> W visit(Visitor<W> v) {
		return v.visitSequence(this);
	}
}

