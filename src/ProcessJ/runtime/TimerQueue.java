package ProcessJ.runtime;

import java.util.concurrent.BlockingQueue;
import java.util.concurrent.DelayQueue;

public class TimerQueue {
//	private BlockingQueue queue;
	public static BlockingQueue<Timer> delayQueue = new DelayQueue<Timer>();

//	public TimerQueue(BlockingQueue queue) {
//		super();
//		this.queue = queue;
//	}
	
	private Thread timerThread = new Thread(new Runnable() {
		public void run() {
			try {
				while (true) {
					//Take out timedout Timer objects from delay queue.
					//Thread will wait here until one is available.
					Timer timer = (Timer) delayQueue.take();

					timer.stopped = true;
					Process p = timer.getProcess();
					if (p != null)
						p.setReady();
				}
			} catch (InterruptedException e) {
				return;
			}
		}
	});
	
	public synchronized void insert(Timer timer) throws InterruptedException {
		this.delayQueue.offer(timer);
	}
	
	public synchronized boolean isEmpty() {
		return this.delayQueue.isEmpty();
	}
	
	public synchronized void kill() {
		this.timerThread.interrupt();
	}

	public void start() {
		System.err.println("[Timer] Timer Queue Running");
		this.timerThread.start();
	}
}