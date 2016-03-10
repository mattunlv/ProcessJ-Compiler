package ProcessJ.runtime;

public abstract class Channel<T> {
	// the data item communicated on the channel
	protected T data;
	// is there any data?
	public boolean ready = false;
	protected boolean reservedForAlt = false;

	Process reservedForReader = null;

	synchronized public void write(Process p, T item) {
	}

	synchronized public T read(Process p) {
		return null;
	}

	synchronized public void addReader(Process p) {
	}

	synchronized public void addWriter(Process p) {
	}

	/*
	  All calls to isReadyToRead and to read() must happen in the same 
	  synchronized block.
	  
	  L2:      
	  synchronized (c) {
	    if (c.isReadyToRead(this)) {
	      ... = c.read(this);
	    } else {
	      setNotReady();
	  yield(2);
	      return;
	    }
	  }
	 */

	synchronized public boolean isReadyToRead(Process p) {
		// data present and reserved for a specific reader.
		if (ready && reservedForReader != null)
			return (reservedForReader == p);
		// data present but is reserved for an alt.
		else if (reservedForAlt)
			return false;
		// not reserved for alt nor for reader, ready determines if ready or not.
		else
			return ready;
	}

	synchronized public boolean isReadyToReadAltAndReserve() {
		if (ready && !reservedForAlt && reservedForReader == null) {
			reservedForAlt = true;
			return true;
		} else
			return false;
	}

	synchronized public void unreserve() {
		reservedForAlt = false;
	}

	synchronized public boolean isReadyToWrite() {
		return !ready;
	}
}