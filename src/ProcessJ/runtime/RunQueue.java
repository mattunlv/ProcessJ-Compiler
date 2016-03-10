package ProcessJ.runtime;

import java.util.*;

public class RunQueue {
    int size = 0;
    private LinkedList<Process> queue = new LinkedList<Process>();
    
    synchronized public void insert(Process p) {
	queue.addLast(p);
    }

    synchronized public Process getNext() {
	return queue.removeFirst();
    }
    
    synchronized public int size() {
	return queue.size();
    }
    
}

