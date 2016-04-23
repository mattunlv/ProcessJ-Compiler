package ProcessJ.runtime;

import java.util.LinkedList;

// Many Writers and One Reader
public class Many2OneChannel<T> extends Channel<T> {
	private Process reader = null;
	private LinkedList<Process> writers = new LinkedList<Process>();

	public Many2OneChannel() {
		this.type = TYPE_MANY2ONE;
	}

	synchronized public void write(Process p, T item) {
		ready = true;
		data = item;
		writers.addFirst(p);
		p.setNotReady();

		if (reader != null) {
			reader.setReady();
		}
	}

	//TODO add override to all
	@Override
	synchronized public T read(Process p) {
		ready = false;
		Process writer = writers.removeFirst();
		writer.setReady();
		writer = null;
		T myData = data;
		data = null;
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
		writer = null;
	}

	synchronized public void addWriter(Process p) {
		writers.add(p);
	}
	
	synchronized public void addReader(Process p) {
		reader = p;
	}
}