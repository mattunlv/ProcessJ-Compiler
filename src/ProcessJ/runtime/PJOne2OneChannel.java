package ProcessJ.runtime;

public class PJOne2OneChannel<T> extends PJChannel<T> {
	private PJProcess writer = null;
	private PJProcess reader = null;

	public PJOne2OneChannel() {
		this.type = TYPE_ONE2ONE;
	}

	// calls to read and write must be 
	// properly controlled by the channel end
	// holders.
	@Override
	synchronized public void write(PJProcess p, T item) {
		data = item;
		writer = p;
		writer.setNotReady();
		ready = true;
		if (reader != null) {
			reader.setReady();
		}
	}

	@Override
	synchronized public T read(PJProcess p) {
		ready = false;
		// we need to set the writer ready as 
		// the synchronization has happened 
		// when the data was read.
		writer.setReady();
		// clear the writer
		writer = null;
		reader = null;
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
		writer.setReady();
		writer = null;
		reader = null;
	}

	@Override
	synchronized public void addReader(PJProcess p) {
		reader = p;
	}
	
	@Override
	synchronized public void addWriter(PJProcess p) {
		// TODO Auto-generated method stub
	}
	
}