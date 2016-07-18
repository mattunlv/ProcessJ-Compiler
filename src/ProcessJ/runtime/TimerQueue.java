package ProcessJ.runtime;

import java.util.concurrent.BlockingQueue;
import java.util.concurrent.DelayQueue;

public class TimerQueue {
	public static BlockingQueue<PJTimer> delayQueue = new DelayQueue<PJTimer>();

	private Thread timerThread = new Thread(new Runnable() {
		@Override
		public void run() {
			try {
				while (true) {
					//Take out timedout Timer objects from delay queue.
					//Thread will wait here until one is available.
					PJTimer timer = (PJTimer) delayQueue.take();
					timer.expire();

					PJProcess p = timer.getProcess();
					if (p != null) {
						p.setReady();
					}
				}
			} catch (InterruptedException e) {
				return;
			}
		}
	});

	public synchronized void insert(PJTimer timer) throws InterruptedException {
		delayQueue.offer(timer);
	}
	
	public void start() {
		System.err.println("[Timer] Timer Queue Running");
		this.timerThread.start();
	}

	public synchronized void kill() {
		this.timerThread.interrupt();
	}

	public synchronized boolean isEmpty() {
		return delayQueue.isEmpty();
	}
}