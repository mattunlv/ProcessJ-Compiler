package ProcessJ.runtime;

import java.util.LinkedList;

public class RunQueue {
	private LinkedList<PJProcess> queue = new LinkedList<PJProcess>();

	synchronized public void insert(PJProcess p) {
		queue.addLast(p);
	}

	synchronized public PJProcess getNext() {
		return queue.removeFirst();
	}

	synchronized public int size() {
		return queue.size();
	}
}
