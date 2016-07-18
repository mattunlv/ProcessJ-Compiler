package ProcessJ.runtime;

public class PJProcess {
	
	/**
	 * The next label to restart from.
	 */
	protected int runLabel = 0;

	/**
	 * Is the process ready to run?
	 */
	protected boolean ready = true;

	/**
	 * Has the process terminated?
	 */
	protected boolean terminated = false;

	/**
	 * Scheduler instance.
	 */
    public static Scheduler scheduler;

	public void run() {};

	public void schedule() {
	   	scheduler.insert(this);
	}
	
	public void finalize() {
	}

	public boolean isReady() {
		return ready;
	}

	public synchronized void setReady() {
		if (!ready) {
			ready = true;
			scheduler.inactivePool.decrement();
		}
	}

	public void setNotReady() {
		if (ready) {
			ready = false;
			scheduler.inactivePool.increment();
		}
	}
	
	public void terminate() {
		terminated = true;
	}

	public boolean terminated() {
		return terminated;
	}
	
	public void yield() {}
	public void label(int label) {}
	public void resume(int label) {}
	
}
