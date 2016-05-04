package ProcessJ.runtime;

public abstract class PJProcess {
	protected int runLabel = 0; // the next label to restart from
	protected boolean ready = true; // is the process ready to run
	public boolean yielded = false; // did the process yield
	public boolean terminated = false; // has the process terminated

    public static Scheduler scheduler;
	
	public abstract void run();

	public void schedule() {
	   	this.scheduler.insert(this);
	}
	
	public void finalize() {
	}

	public void terminate() {
		terminated = true;
	}

	public boolean terminated() {
		return terminated;
	}

	public boolean isReady() {
		return ready;
	}

	//making this and run method synchronized because
	//of possibility of very low timeout timers. we want to
	//make sure setReady only happens after run() method ends
	// which could have call to setNotReady. We don't want
	// a process to be set ready by timer expiring and
	// then setnotready at the end of run for yielding purpose.
	//therefore, make run method synchronized as well.
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
	
	public void yield() {
		yielded = true;
	}
	
	public void label(int label) {}
	public void resume(int label) {}
}
