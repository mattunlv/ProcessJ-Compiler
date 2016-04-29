package ProcessJ.runtime;
import java.util.concurrent.Delayed;
import java.util.concurrent.TimeUnit;

public class Timer implements Delayed {
	private Process process;

	private long delay;
	private boolean killed = false;
	public final long timeout;
	public boolean started = false;
	public boolean stopped = false;

	public Timer() {
		this.timeout = 0L;
	}
	
	public Timer(Process process, long timeout) {
		this.process = process;
		this.timeout = timeout;
	}
	
	public void start() throws InterruptedException {
		this.delay = System.currentTimeMillis() + timeout;
		Process.scheduler.insertTimer(this);
		started = true;
	}

	@Override
	public long getDelay(TimeUnit unit) {
		long diff = delay - System.currentTimeMillis();
		return unit.convert(diff, TimeUnit.MILLISECONDS);
	}

	@Override
	public int compareTo(Delayed o) {
		if (this.delay < ((Timer) o).delay) {
			return -1;
		}
		if (this.delay > ((Timer) o).delay) {
			return 1;
		}
		return 0;
	}

	public static long read() {
		return System.currentTimeMillis();
	}

	public void kill() {
		killed = true;
	}

	public Process getProcess() {
		if (killed)
			return null;
		else
			return process;
	}
}
