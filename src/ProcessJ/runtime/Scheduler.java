package ProcessJ.runtime;

import java.util.Random;

public class Scheduler extends Thread {
	
	private boolean chaotic = false;
	private boolean insanely_chaotic = false;
	private boolean dump = false;

	private final TimerQueue timerQueue = new TimerQueue();
	private final RunQueue rq = new RunQueue();
	
	public static InactivePool inactivePool = new InactivePool();

	synchronized void insert(Process p) {
		rq.insert(p);
	}
	
	synchronized void insertTimer(Timer t) throws InterruptedException {
		timerQueue.insert(t);
	}
	
	synchronized int size() {
		return rq.size();
	}
	
	public void makeInsanelyChaotic() {
		this.chaotic = false;
		this.insanely_chaotic = true;
	}
	
	public void makeChaotic() {
		this.chaotic = true;
		this.insanely_chaotic = false;
	}
	
	public void makeItDump() {
		this.dump = true;
	}

	private void shuffle() {
		int size = rq.size();
		if (size < 2)
			return;
		
		while(true) {
			Random rand = new Random(); 
			int i = rand.nextInt(size()); 
			int j = rand.nextInt(size()); 

			if (rq.swap(i, j)) {
				break;
			}
		}
	}
	
	private void insaneShuffle() {
		int size = rq.size();
		if (size < 2)
			return;
		
		Random rand = new Random(); 
		int i = rand.nextInt(size()); 
		int j = rand.nextInt(size()); 

		rq.swap(i, j);
	}

	public void run() {
		// run the scheduler here and start main!
		System.err.println("[Scheduler] Scheduler running");

		timerQueue.start();
		
//		int notReadyCounter = 0;
		int contextSwitches = 0;
		int swaps = 0;
		int rotation = 0;
		while (rq.size() > 0) {
//			System.err.println("[Scheduler] Run Queue size: [" + rq.size() + "]");
			
			if (chaotic) {
				shuffle();
				swaps++;
			}
			if (insanely_chaotic) {
				insaneShuffle();
				swaps++;
			}

//			if (dump) {
//				rotation++;
//				if (rotation == 100000) {
					rq.dump();
//					rotation = 0;
//				}
//			}
			
			// grab the next process in the run queue
			Process p = rq.getNext();

//			System.out.println("Ready to run? " + p + " " + p.isReady());

			// is it ready to run?
			if (p.isReady()) {
				// yes, so run it
				p.yielded = false;
				p.run();
				contextSwitches++;
				// and reset the notReadyCounter
//				notReadyCounter = 0;
				// result < 0 => process terminated
				if (!p.terminated()) {
					// did not terminate, so insert in run queue
					// Note, it is the process' own job to
					// set the 'ready' flag.
					rq.insert(p);
//					System.out.println("Ran " + p.getId() + " - Next run label is " + p.getNextRunLabel());
				} else {
					// did terminate so do nothing
					p.finalize();
//					System.err.println("[Scheduler] Terminated");
				}
			} else {
				// no, not ready, put it back in the run queue
				// and count it as not ready
				//System.out.println(p + ": not ready to run. Back in the queue!");
				rq.insert(p);
//				notReadyCounter++;
			}
			// if we have seen all the processes
			// an none were ready we have a deadlock
//			System.out.println("rq.size=" + rq.size() + "  " + "notReadyCounter=" + inactivePool.getCount() + "  tq.empty="+ timerQueue.isEmpty());

			if (inactivePool.getCount() == rq.size() && rq.size() > 0 && timerQueue.isEmpty()) {
				System.err.println("No processes ready to run. System is deadlocked");
				System.err.println("remaining processes:" + rq.size());
				timerQueue.kill();
				System.exit(1);
			}
		}
		timerQueue.kill();

		System.err.println("[Scheduler] Total Context Switches: " + contextSwitches);
		if (chaotic) {
			System.err.println("[Scheduler.Chaotic] Total Swaps: " + swaps);
		}
	}
}