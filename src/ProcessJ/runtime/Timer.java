package ProcessJ.runtime;
import java.util.concurrent.Delayed;
import java.util.concurrent.TimeUnit;

public class Timer implements Delayed {
	private Process process;

	private long startTime;
	private boolean amIDead = false;

	public Timer(Process process, long delay) {
		this.process = process;
		this.startTime = System.currentTimeMillis() + delay;
	}

	public long getDelay(TimeUnit unit) {
		long diff = startTime - System.currentTimeMillis();
		return unit.convert(diff, TimeUnit.MILLISECONDS);
	}
	
	public long getTime() {
		return System.currentTimeMillis();
	}

	public int compareTo(Delayed o) {
		if (this.startTime < ((Timer) o).startTime) {
			return -1;
		}
		if (this.startTime > ((Timer) o).startTime) {
			return 1;
		}
		return 0;
	}

	public void kill() {
		amIDead = true;
	}

	public Process getProcess() {
		if (amIDead)
			return null;
		else
			return process;
	}
}
