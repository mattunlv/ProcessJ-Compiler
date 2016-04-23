package ProcessJ.runtime;

import java.util.*;

// Many Writers and Many Readers
public class Many2ManyChannel<T> extends Channel<T> {
	private LinkedList<Process> readers = new LinkedList<Process>();
	private LinkedList<Process> writers = new LinkedList<Process>();

	public Many2ManyChannel() {
		this.type = TYPE_MANY2MANY;
	}

	synchronized public void write(Process p, T item) {
		ready = true;
		data = item;
		writers.addFirst(p);
		p.setNotReady();
		if (readers.size() > 0) {
			Process reader = readers.removeFirst();
			reader.setReady();
		}
	}

	synchronized public T read(Process p) {
		Process writer = writers.removeFirst();
		writer.setReady();
		T myData = data;
		data = null;
		ready = false;
		return myData;
	}
	
	synchronized public T readPreRendezvous() {
		T myData = data;
		data = null;
		return myData;
	}
	
	synchronized public void readPostRendezvous() {
		ready = false;
		Process writer = writers.removeFirst();
		writer.setReady();
	}

	synchronized public void addWriter(Process p) {
		writers.add(p);
	}

	synchronized public void addReader(Process p) {
		readers.add(p);
	}
}