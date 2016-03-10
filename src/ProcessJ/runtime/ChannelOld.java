package ProcessJ.runtime;

public class ChannelOld<T> {
	// the data item communicated on the channel
	private T data;
	// is there any data?
	public boolean ready = false;

	private Process writer = null;
	private Process reader = null;;

	// calls to read and write must be 
	// properly controlled by the channel end
	// holders.
	synchronized public void write(Process p, T item) {
		data = item;
		writer = p;
		writer.setNotReady();
		ready = true;
		if (reader != null)
			reader.setReady();
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
		return data;
	}

	synchronized public boolean isReadyToRead(Process p) {
		/*
		 * March 7, 2016
		 * TODO: for shared read channel ends, setting reader=p here will not work.
		 * Coz, there can be more than 1 process holding the read end. So, lets say
		 * 1st reader comes in and sets itself to 'reader', sets itself not ready to run
		 * and deschedules. 2nd reader coming it and overwrites 'reader' by itself. When
		 * this channel is ready to write, it only awakens the second process. 1st process
		 * will go into deadlock.
		 * 
		 * So to fix that, we might need to have a pool for shared channels
		 * and pick one from the pool every time this is ready to write.
		 * 
		 * For that, we want to remove reader setting code from here and maybe have a addToReadPool
		 * kind of method. Think about it more.
		 * 
		 * And for channel code generation, if !channel.isReadyToRead, do not set itself notreadytorun
		 * rather just add itself to the pool and yield to label before isReadyToRead check so that
		 * it keeps checking if it can read....or something like that.
		 */
		reader = p;
		return ready;
	}

	synchronized public boolean isReadyToReadAlt() {
		return ready;
	}

	synchronized public boolean isReadyToWrite() {
		return !ready;
	}

}