package ProcessJ.runtime;

import java.util.ArrayList;
import java.util.List;

/**
 * ProcessJ code will have something of this sort:
 * 
 * enroll(b):                                                                                                                     
 *                                                                                                                                 
 * b.addProc(this);                                                                                                               
 *                                                                                                                                  
 * sync(b):                                                                                                                       
 *                                                                                                                                
 * b.decrement();                                                                                                                
 * yield(......., X);                                                                                                            
 *X: ...                                                                                                                           
*/
public class Barrier {
	
	List<Process> pool = new ArrayList<Process>();
	int enrolled = 0;

	/*
	 * Any process that declares a barrier
	 * is itself enrolled on it; so count is 1.
	 */
	public Barrier() {
		this.enrolled = 1;
	}

	public synchronized void enroll(int m) {
		this.enrolled = this.enrolled + m - 1;
	}
	
	public synchronized void resign() {
		/*
		 * So that last guy doesn't decrement
		 * the count. We want the declarer
		 * to still be enrolled.
		 */
		if (this.enrolled > 1) { 
			this.enrolled = this.enrolled - 1;
		}
	}

	public synchronized void sync(Process process) {
		process.setNotReady();
		pool.add(process);
		if (pool.size() == enrolled) {
			for(Process p : pool) {
				p.setReady();
			}
		}
	}
}