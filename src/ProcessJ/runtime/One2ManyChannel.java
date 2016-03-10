package ProcessJ.runtime;

import java.util.*;

// One Writer and Many Readers
public class One2ManyChannel<T> extends Channel<T> {
    private Process writer = null;
    private LinkedList<Process> readers = new LinkedList<Process>();

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
	    writer.setReady();
	    T myData = data;
	    data = null;
	    return myData;
	}
    
    synchronized public void addReader(Process p) {
            readers.add(p);
        }
}