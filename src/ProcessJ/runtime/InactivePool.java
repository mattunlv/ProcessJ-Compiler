package ProcessJ.runtime;

public class InactivePool {

	private int count = 0;
	
	public synchronized void decrement() {
		this.count--;
	}
	
	public synchronized void increment() {
		this.count++;
	}
	
	public int getCount() {
		return this.count;
	}
}
