package ProcessJ.runtime;

import java.util.concurrent.BlockingQueue;

public class TimerQueue {
	private BlockingQueue queue;

	public TimerQueue(BlockingQueue queue) {
		super();
		this.queue = queue;
	}

	public synchronized void kill() {
		timerThread.interrupt();
	}

	private Thread timerThread = new Thread(new Runnable() {
		public void run() {
			try {
				while (true) {
					// Take elements out from the DelayQueue object.
					Timer timer = (Timer) queue.take();

					Process p = timer.getProcess();
					if (p != null)
						p.setReady();
				}
			} catch (InterruptedException e) {
				return;
			}
		}
	});

	public void start() {
		System.out.println("Timer Queue Running");
		this.timerThread.start();
	}
}