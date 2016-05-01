package ProcessJ.runtime;

import java.util.LinkedList;
import java.util.List;

public abstract class PJChannel<T> {
	
	protected final static int TYPE_ONE2ONE = 0; 
	protected final static int TYPE_ONE2MANY = 1; 
	protected final static int TYPE_MANY2ONE = 2; 
	protected final static int TYPE_MANY2MANY = 3; 
	
	// the data item communicated on the channel
	protected T data;
	// is there any data?
	public boolean ready = false;
	protected boolean reservedForAlt = false;
	public int type;
	protected boolean claimed = false;
	protected LinkedList<PJProcess> claimQueue = new LinkedList<PJProcess>();

	PJProcess reservedForReader = null;

	synchronized public void write(PJProcess p, T item) {
	}

	synchronized public T read(PJProcess p) {
		return null;
	}

	synchronized public T readPreRendezvous(PJProcess p) {
		return null;
	}

	synchronized public void readPostRendezvous(PJProcess p) {
	}
	
	synchronized public void addReader(PJProcess p) {
	}

	synchronized public void addWriter(PJProcess p) {
	}

	synchronized public boolean claim() {
		boolean success = false;
		if (!this.claimed) {
			this.claimed = true;
			success = true;
		}
		return success;
	}
	
	synchronized public void unclaim() {
		this.claimed = false;
//		if (claimQueue.size() > 0) {
//			Process p = claimQueue.removeFirst();
//			p.setReady();
//		}
	}
	
//	synchronized public void awaitClaim(Process p) {
//		claimQueue.add(p);
//	}

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

	synchronized public boolean isReadyToRead(PJProcess p) {
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
			
			/*
			 * NOTE:
			 * Setting this reservedForAlt is necessary so that, in the time space
			 * between 
			 * 	if(c.readyToRead()) and c.read()
			 * Some faster process will not read the data in channel c due to processor
			 * thread interleaving.
			 * 
			 * say call1: A.c.readyToRead (ret true)
			 * 		B.c.readyToReady (ret true)
			 * 		B.c.read
			 * 		A.c.read <= What will happen here??
			 */
			reservedForAlt = true;
			return true;
		} else {
			return false;
		}
	}

	synchronized public void unreserve() {
		reservedForAlt = false;
	}
	
	synchronized public boolean isReadyToWrite() {
		return !ready;
	}

	public boolean isSharedRead() {
		return (this.type == PJChannel.TYPE_ONE2MANY || this.type == PJChannel.TYPE_MANY2MANY);
	}
	
	public boolean isSharedWrite() {
		return (this.type == PJChannel.TYPE_MANY2ONE || this.type == PJChannel.TYPE_MANY2MANY);
	}
	
	public int getType() {
		return this.type;
	}
}