package ProcessJ.runtime;

import java.util.concurrent.BlockingQueue;
import java.util.concurrent.DelayQueue;

public class Scheduler extends Thread {
	
	public static BlockingQueue delayQueue = new DelayQueue();
	private TimerQueue timerQueue = new TimerQueue(Scheduler.delayQueue);
	
	final RunQueue rq = new RunQueue();

	synchronized void insert(Process p) {
		rq.insert(p);
	}

	synchronized int size() {
		return rq.size();
	}

	public void run() {
		// run the scheduler here and start main!
		System.out.println("Scheduler running");

		timerQueue.start();
		
		int notReadyCounter = 0;
		while (rq.size() > 0) {
			//       System.out.println("--------------------------------------------------");
			System.out.println("Run Queue size: [" + rq.size() + "]");
			// grab the next process in the run queue
			Process p = rq.getNext();

			System.out.println("Ready to run? " + p + " " + p.isReady());

			// is it ready to run?
			if (p.isReady()) {
				// yes, so run it
//				p.yielded = false;
				p.run();
				// and reset the notReadyCounter
				notReadyCounter = 0;
				// result < 0 => process terminated
				if (!p.terminated()) {
					// did not terminate, so insert in run queue
					// Note, it is the process' own job to
					// set the 'ready' flag.
					rq.insert(p);
					//System.out.println("Ran " + p.getId() + " - Next run label is " + p.getNextRunLabel());
				} else {
					// did terminate so do nothing
					p.finalize();
					//System.out.println(p.getId() + " terminated.");
				}
			} else {
				// no, not ready, put it back in the run queue
				// and count it as not ready
				//System.out.println(p + ": not ready to run. Back in the queue!");
				rq.insert(p);
				notReadyCounter++;
			}
			// if we have seen all the processes
			// an none were ready we have a deadlock
			System.out.println(rq.size() + "  " + notReadyCounter + "  "+ delayQueue.isEmpty());

			if (notReadyCounter == rq.size() && rq.size() > 0 && Scheduler.delayQueue.isEmpty()) {
				System.out.println("No processes ready to run. System is deadlocked");
				System.out.println(rq);
				timerQueue.kill();
				System.exit(1);
			}
		}
		timerQueue.kill();

	}

}