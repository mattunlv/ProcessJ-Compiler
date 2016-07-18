package ProcessJ.runtime;

import java.util.LinkedList;

public class PJOne2ManyChannel<T> extends PJChannel<T> {
	private PJProcess writer = null;
	private LinkedList<PJProcess> readers = new LinkedList<PJProcess>();

	public PJOne2ManyChannel() {
		this.type = TYPE_ONE2MANY;
	}

	@Override
	synchronized public void write(PJProcess p, T item) {
		data = item;
		writer = p;
		writer.setNotReady();
		ready = true;
		if (readers.size() > 0) {
			PJProcess reader = readers.removeFirst();
			reservedForReader = reader;
			reader.setReady();
		}
	}

	@Override
	synchronized public T read(PJProcess p) {
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
	
	@Override
	synchronized public T readPreRendezvous(PJProcess p) {
		T myData = data;
		data = null;
		return myData;
	}
	
	@Override
	synchronized public void readPostRendezvous(PJProcess p) {
		ready = false;
		reservedForReader = null;
		reservedForAlt = false;
		writer.setReady();
	}

	@Override
	synchronized public void addReader(PJProcess p) {
		readers.add(p);
	}

	@Override
	synchronized public void addWriter(PJProcess p) {
		// TODO Auto-generated method stub
	}
	
}