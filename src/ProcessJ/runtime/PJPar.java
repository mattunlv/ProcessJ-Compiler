package ProcessJ.runtime;

public class PJPar {
	private PJProcess process;
	private int processCount;

	public PJPar(int processCount, PJProcess p) {
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
