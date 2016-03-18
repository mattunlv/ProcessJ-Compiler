package ProcessJ.runtime;

public abstract class Process {
	protected int runLabel = 0; // the next label to restart from
	protected boolean ready = true; // is the process ready to run
	public boolean terminated = false; // has the process terminated

    public static Scheduler scheduler;
    public Object context;
	
    public Process() {
    	
    }

    public Process(Object context) {
    	this.context = context;
    }

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

	public void setReady() {
		ready = true;
	}

	public void setNotReady() {
		ready = false;
	}
	
	public void yield(int label) {}
	public void label(int label) {}
	public void resume(int label) {}
}
