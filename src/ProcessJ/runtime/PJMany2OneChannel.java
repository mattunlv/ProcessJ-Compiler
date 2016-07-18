package ProcessJ.runtime;

import java.util.LinkedList;

// Many Writers and One Reader
public class PJMany2OneChannel<T> extends PJChannel<T> {
	private PJProcess reader = null;
	private LinkedList<PJProcess> writers = new LinkedList<PJProcess>();

	public PJMany2OneChannel() {
		this.type = TYPE_MANY2ONE;
	}

	@Override
	synchronized public void write(PJProcess p, T item) {
		ready = true;
		data = item;
		writers.addFirst(p);
		p.setNotReady();

		if (reader != null) {
			reader.setReady();
		}
	}

	@Override
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
	
	@Override
	synchronized public T readPreRendezvous(PJProcess p) {
		T myData = data;
		data = null;
		return myData;
	}
	
	@Override
	synchronized public void readPostRendezvous(PJProcess p) {
		ready = false;
		if (writers.size() > 0) {
			PJProcess writer = writers.removeFirst();
			writer.setReady();
		}
	}

	@Override
	synchronized public void addWriter(PJProcess p) {
		writers.add(p);
	}
	
	@Override
	synchronized public void addReader(PJProcess p) {
		reader = p;
	}
}