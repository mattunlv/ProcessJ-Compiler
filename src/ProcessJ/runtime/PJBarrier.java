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
public class PJBarrier {
	
	List<PJProcess> pool = new ArrayList<PJProcess>();
	public int enrolled = 0;

	/*
	 * Any process that declares a barrier
	 * is itself enrolled on it; so count is 1.
	 */
	public PJBarrier() {
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
		System.out.println("resign being called!!!");
		if (this.enrolled > 1) { 
			this.enrolled = this.enrolled - 1;
		}
	}

	public synchronized void sync(PJProcess process) {
//		System.out.println("a process is syncing!");
		process.setNotReady();
		pool.add(process);
//		System.out.println("pool.size=" + pool.size() + " enrolled=" + enrolled);
		if (pool.size() == enrolled) {
			for(PJProcess p : pool) {
				p.setReady();
			}
//			System.out.println("clearning pool after all synced!!");
			pool.clear();
		}
	}
}