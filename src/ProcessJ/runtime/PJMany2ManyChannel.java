package ProcessJ.runtime;

import java.util.*;

// Many Writers and Many Readers
public class PJMany2ManyChannel<T> extends PJChannel<T> {
	private LinkedList<PJProcess> readers = new LinkedList<PJProcess>();
	private LinkedList<PJProcess> writers = new LinkedList<PJProcess>();

	public PJMany2ManyChannel() {
		this.type = TYPE_MANY2MANY;
	}

	synchronized public void write(PJProcess p, T item) {
		ready = true;
		data = item;
		writers.addFirst(p);
		p.setNotReady();
		if (readers.size() > 0) {
			PJProcess reader = readers.removeFirst();
			reader.setReady();
		}
	}

	synchronized public T read(PJProcess p) {
		T myData = data;
		data = null;

		ready = false;
		if (writers.size() > 0) {
			PJProcess writer = writers.removeFirst();
			writer.setReady();
		}
		return myData;
	}
	
	synchronized public T readPreRendezvous(PJProcess p) {
		T myData = data;
		data = null;
		return myData;
	}
	
	synchronized public void readPostRendezvous(PJProcess p) {
		ready = false;
		if (writers.size() > 0) {
			PJProcess writer = writers.removeFirst();
			writer.setReady();
		}
	}

	synchronized public void addWriter(PJProcess p) {
		writers.add(p);
	}

	synchronized public void addReader(PJProcess p) {
		readers.add(p);
	}
}