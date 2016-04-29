package ProcessJ.runtime;

import java.util.LinkedList;

// One Writer and Many Readers
public class One2ManyChannel<T> extends Channel<T> {
	private Process writer = null;
	private LinkedList<Process> readers = new LinkedList<Process>();

	public One2ManyChannel() {
		this.type = TYPE_ONE2MANY;
	}

	synchronized public void write(Process p, T item) {
		data = item;
		writer = p;
		writer.setNotReady();
		ready = true;
		if (readers.size() > 0) {
			Process reader = readers.removeFirst();
			reservedForReader = reader;
			reader.setReady();
		}
	}

	synchronized public T read(Process p) {
		ready = false;
		reservedForReader = null;
		//might not be reserved at all. doesn't hurt do it.
		reservedForAlt = false;
		writer.setReady();
		//TODO why are we not making writer/reader
		//null?
		T myData = data;
		data = null;

		return myData;
	}
	
	synchronized public T readPreRendezvous(Process p) {
		T myData = data;
		data = null;
		return myData;
	}
	
	synchronized public void readPostRendezvous(Process p) {
		ready = false;
		reservedForReader = null;
		reservedForAlt = false;
		writer.setReady();
	}


	synchronized public void addReader(Process p) {
		readers.add(p);
	}
	
}