package ProcessJ.runtime;

import java.util.Collections;
import java.util.LinkedList;

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

	public boolean swap(int i, int j) {
		if (queue.get(i).isReady() || queue.get(j).isReady() ) {
			return false;
		}
		Collections.swap(queue, i, j);
		return true;
	}
	
	public void dump() {
		StringBuilder sb = new StringBuilder();
		for(Process p : queue) {
			if (p.isReady()) {
//				sb.append("R");
				sb.append("*");
			} else {
				sb.append(" ");
			}
		}
		
		System.out.println("RunQueueDump=>" + sb.toString() + "<<");
	}
}
