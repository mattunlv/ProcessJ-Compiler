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

	@Override
	synchronized public T read(Process p) {
		T myData = data;
		data = null;
		ready = false;
		if (writers.size() > 0) {
			Process writer = writers.removeFirst();
			writer.setReady();
		}
		return myData;
	}
	
	synchronized public T readPreRendezvous(Process p) {
		T myData = data;
		data = null;
		return myData;
	}
	
	synchronized public void readPostRendezvous(Process p) {
		ready = false;
		if (writers.size() > 0) {
			Process writer = writers.removeFirst();
			writer.setReady();
		}
	}

	synchronized public void addWriter(Process p) {
		writers.add(p);
	}
	
	synchronized public void addReader(Process p) {
		reader = p;
	}
}