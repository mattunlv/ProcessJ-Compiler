package ProcessJ.runtime;

import java.util.Collections;
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

	public boolean swap(int i, int j) {
		if (queue.get(i).isReady() || queue.get(j).isReady() ) {
			return false;
		}
		Collections.swap(queue, i, j);
		return true;
	}
	
	public void dump() {
		StringBuilder sb = new StringBuilder();
		for(PJProcess p : queue) {
			if (p.isReady()) {
				sb.append("R");
//				sb.append("*");
			} else {
//				sb.append(" ");
				sb.append("N");
			}
		}
		
		System.out.println("RunQueueDump=>" + sb.toString() + "<<");
	}
}
