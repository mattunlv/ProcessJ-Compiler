package ProcessJ.runtime;

public class Par {
	private Process process;
	private int processCount;

	public Par(int processCount, Process p) {
		this.processCount = processCount;
		this.process = p;
	}

	public void decrement() {
		processCount--;
		if (processCount == 0)
			process.setReady();
	}
}
