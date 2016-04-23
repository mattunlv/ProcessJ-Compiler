package ProcessJ.runtime;

public class Par {
	private Process process;
	private int processCount;

	public Par(int processCount, Process p) {
		this.processCount = processCount;
		this.process = p;
	}
	
	public void setProcessCount(int count) {
		this.processCount = count;
	}

	public void decrement() {
		processCount--;
		if (processCount == 0) {
			process.setReady();
		}
	}
}
