package ProcessJ.runtime;

public class RuntimeSystem {
	private Scheduler[] schedulers;
	private int cores;

	public RuntimeSystem(int cores) {
		schedulers = new Scheduler[cores];
		for (int i = 0; i < cores; i++)
			schedulers[i] = new Scheduler();
		this.cores = cores;
	}

	private int getMin() {
		int min = schedulers[0].size();
		int index = 0;
		for (int i = 1; i < cores; i++) {
			if (schedulers[i].size() < min) {
				min = schedulers[i].size();
				index = i;
			}
		}
		return index;
	}

	public void insert(Process p) {
		int i = getMin();
		System.out.println("Inserting process into run queue of scheduler #"
				+ i);
		schedulers[i].insert(p);
	}

	public void run() {
		for (int i = 0; i < cores; i++) {
			schedulers[i].start();
		}
		try {
			for (int i = 0; i < cores; i++) {
				schedulers[i].join();
			}
		} catch (InterruptedException e) {
			System.out
					.println("Runtime System: InterruptedException in join()");
		}
		System.out.println("Runtime terminating....");
	}

}