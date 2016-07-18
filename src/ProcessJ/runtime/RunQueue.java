package ProcessJ.runtime;

import java.util.Comparator;
import java.util.PriorityQueue;

public class RunQueue {
//	private LinkedList<PJProcess> queue = new LinkedList<PJProcess>();
	private PriorityQueue<PJProcess> queue = new PriorityQueue<PJProcess>(10, new ReadyComparator());

	synchronized public void insert(PJProcess p) {
//		queue.addLast(p);
		queue.add(p);
	}

	synchronized public PJProcess getNext() {
//		return queue.removeFirst();
		return queue.poll();
	}

	synchronized public int size() {
		return queue.size();
	}

	public boolean swap(int i, int j) {
//		if (queue.get(i).isReady() || queue.get(j).isReady() ) {
//			return false;
//		}
//		Collections.swap(queue, i, j);
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
	
	class ReadyComparator implements Comparator<PJProcess>
	{
	    @Override
	    public int compare(PJProcess obj1, PJProcess obj2)
	    {
	        if (!obj1.isReady() && obj2.isReady())
	        {
	            return -1;
	        }
	        if (obj1.isReady() && !obj2.isReady())
	        {
	            return 1;
	        }
	        return 0;
	    }
	}
}
