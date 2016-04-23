package ProcessJ.runtime;

import java.util.Collections;
import java.util.LinkedList;
import java.util.Random;

public class RunQueue {
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

	public void swap(int i, int j) {
		Collections.swap(queue, i, j);
	}
}
