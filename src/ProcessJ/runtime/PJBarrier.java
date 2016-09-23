package ProcessJ.runtime;
import java.util.ArrayList;
import java.util.List;

/* JVMCSP is maintained at the University of Nevada Las Vegas.
 * 
 * For more information please contact matt.pedersen@unlv.edu
 * or see processj.org
 */

/**
 * The runtime representation of a ProcessJ 'barrier'.
 *
 * @author Cabel Shrestha
 * @version 1.0
 * @since 2016-05-01
 */


/*
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
 * X: ...                                                                                                                           
 */
public class PJBarrier {

    /**
     * List of processes that have synced on the barrier.
     */
    public List<PJProcess> sycned = new ArrayList<PJProcess>();
    /*
     * The number of processes enrolled on the barrier.
     */
    public int enrolled = 0;
    
    /**
     * Constructor. Any process that declares a barrier
     * is itself enrolled on it; so enrolled count starts at 1.
     */
    public PJBarrier() {
	this.enrolled = 1;
    }

    /**
     * Enroll on the barrier. (m-1) will be enrolled.
     * @param m the number of processes to enroll.
     */
    public synchronized void enroll(int m) {
	this.enrolled = this.enrolled + m - 1;
    }

    /**
     * Resign from the barrier.
     */
    public synchronized void resign() {
	if (this.enrolled > 1) { 
	    this.enrolled = this.enrolled - 1;
	}
    }

    /**
     * Synchronizes on the barrier.
     *
     * @param process A reference to the process syncing - 
     * this is needed to set all processes ready when everyone
     * has synced
     */
    public synchronized void sync(PJProcess process) {
	process.setNotReady();
	sycned.add(process);
	if (sycned.size() == enrolled) {
	    for(PJProcess p : sycned) {
		p.setReady();
	    }
	    sycned.clear();
	}
    }
}
