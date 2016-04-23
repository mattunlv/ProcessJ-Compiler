package ProcessJ.runtime;

public class One2OneChannel<T> extends Channel<T> {
	private Process writer = null;
	private Process reader = null;;

	public One2OneChannel() {
		this.type = TYPE_ONE2ONE;
	}

	// calls to read and write must be 
	// properly controlled by the channel end
	// holders.
	synchronized public void write(Process p, T item) {
		data = item;
		writer = p;
		writer.setNotReady();
		ready = true;
		if (reader != null) {
			reader.setReady();
		}
	}

	synchronized public T read(Process p) {
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
	
	synchronized public T readPreRendezvous(Process p) {
		T myData = data;
		data = null;
		return myData;
	}
	
	synchronized public void readPostRendezvous(Process p) {
		ready = false;
		writer.setReady();
		writer = null;
		reader = null;
	}

	synchronized public void addReader(Process p) {
		reader = p;
	}

}