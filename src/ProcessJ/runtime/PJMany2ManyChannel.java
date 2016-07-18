package ProcessJ.runtime;

import java.util.*;

// Many Writers and Many Readers
public class PJMany2ManyChannel<T> extends PJChannel<T> {
	private LinkedList<PJProcess> readers = new LinkedList<PJProcess>();
	private LinkedList<PJProcess> writers = new LinkedList<PJProcess>();

	public PJMany2ManyChannel() {
		this.type = TYPE_MANY2MANY;
	}

	@Override
	synchronized public void write(PJProcess p, T item) {
		ready = true;
		data = item;
		writers.addFirst(p);
		p.setNotReady();
		if (readers.size() > 0) {
			PJProcess reader = readers.removeFirst();
			//FIXME don't we need to do same as one2many
			//set the reservedForReader
			reader.setReady();
		}
	}

	@Override
	synchronized public T read(PJProcess p) {
		T myData = data;
		data = null;

		//FIXME don't we need to do same as one2many
		//set the reservedForReader and reservedForAlt

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
		//FIXME set reservedforreader null
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
		readers.add(p);
	}
}