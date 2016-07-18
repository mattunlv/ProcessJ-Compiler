package ProcessJ.runtime;
import java.util.concurrent.Delayed;
import java.util.concurrent.TimeUnit;

public class PJTimer implements Delayed {
	private PJProcess process;

	private long delay;
	private boolean killed = false;
	public boolean started = false;
	public boolean expired = false;

	public final long timeout;

	public PJTimer() {
		this.timeout = 0L;
	}
	
	public PJTimer(PJProcess process, long timeout) {
		this.process = process;
		this.timeout = timeout;
	}
	
	public void start() throws InterruptedException {
		this.delay = System.currentTimeMillis() + timeout;
		PJProcess.scheduler.insertTimer(this);
		started = true;
	}

	public void expire() {
		expired = true;
	}

	public static long read() {
		return System.currentTimeMillis();
	}

	public void kill() {
		killed = true;
	}

	public PJProcess getProcess() {
		if (killed) {
			return null;
		} else {
			return process;
		}
	}
	
	@Override
	public long getDelay(TimeUnit unit) {
		//FIXME DelayQueue document says getDelay return values should be
		//TimeUnits.NANOSECONDS but we are using mili. verify that we will not 
		//run into any issues.
		long diff = delay - System.currentTimeMillis();
		return unit.convert(diff, TimeUnit.MILLISECONDS);
	}

	@Override
	public int compareTo(Delayed o) {
		if (this.delay < ((PJTimer) o).delay) {
			return -1;
		}
		if (this.delay > ((PJTimer) o).delay) {
			return 1;
		}
		return 0;
	}
}
