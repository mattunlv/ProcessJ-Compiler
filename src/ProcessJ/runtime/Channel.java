package ProcessJ.runtime;

public abstract class Channel<T> {
	
//	protected final static int TYPE_ONE_TO_ONE = 0; 
//	protected final static int TYPE_ONE_TO_MANY = 1; 
//	protected final static int TYPE_MANY_TO_ONE = 2; 
//	protected final static int TYPE_MANY_TO_MANY = 3; 

	// the data item communicated on the channel
	protected T data;
	// is there any data?
	public boolean ready = false;
	protected boolean reservedForAlt = false;
//	protected int type;

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
			/*
			 * TODO: can we not be sure that the
			 * reserved reader is not this one as this
			 * process is definitely not in ALT as it
			 * is calling this ready method??
			 * 
			 * No!No!No!
			 * When a read end of multi-read channel is held
			 * by a process not in an alt, it still adds itself
			 * to the readers list. Well, if it does add to itself
			 * to the list, then we are good w/o the below comparison.
			 * Because, it means that the process has reached its channel.code
			 * atleast once before, then added itself to list, and yielded. The
			 * only way it would wake up is by the channel writer. then, if it
			 * was awoken by the writer, that also means reservedForReade == itself.
			 * 
			 * Now, lets look at this scenario. Process A has an alt and has c.read
			 * of one2many.Process A get to alt, gets added to list, yields. Process B has
			 * c.read as well. It hasn't reached c.read but it is ahead of A in the
			 * runqueue. That can happen either by B yielding due to some other
			 * sync block or by just coming after A in the initial runqueue. A gets 
			 * rescheduled to the back of the queue.
			 * 
			 * Now, Say C writes to c, sets A=ready, and reservedForRead=A, A is awoken
			 * and waiting to be scheduled so that it can perform the read. But since
			 * B is ahead in the runqueue, it get to c first, finds it ready, hijacks 
			 * the data meant for A and moves on. What happens to A? This is not
			 * acceptable. 
			 * 
			 * So, if we have check the reservedForReader as well, then we avoid
			 * this scenario.
			 * 
			 * New update:
			 * channels inside alt will not add themselves in the list. they only
			 * reserve?? check this one.
			 * 
			 * Alt is continuously running. so when a channel guard is ready, that
			 * means there actually is data to be read. so maybe channel
			 * doesn't even need to setnotready?? check this too.
			 * 
			 * 
			 */
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
		} else
			return false;
	}

	synchronized public void unreserve() {
		reservedForAlt = false;
	}

	synchronized public boolean isReadyToWrite() {
		return !ready;
	}

//	public int getType() {
//		return this.type;
//	}
}