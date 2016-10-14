package ProcessJ.runtime;

/* JVMCSP is maintained at the University of Nevada Las Vegas.
 * 
 * For more information please contact matt.pedersen@unlv.edu
 * or see processj.org
 */

/**
 * The runtime representation of the ProcessJ 'par' statement.
 *
 * @author Cabel Shrestha
 * @version 1.0
 * @since 2016-05-01
 */

public class PJPar {

    /**
     * A reference to the process in which this par appears.
     */
    private PJProcess process;
    /**
     * The number of processes inside the par
     */
    private int processCount;

    /**
     * Constructor.
     *
     * @param processCount
     *            The number of processes inside the par block.
     * @param p
     *            A reference to the process in which the par appears.
     */
    public PJPar(int processCount, PJProcess p) {
        this.processCount = processCount;
        this.process = p;
    }

    /**
     * Mutator for setting the process count of the par block.
     * 
     * @param count
     *            The number of processes in the par block.
     */
    public void setProcessCount(int count) {
        this.processCount = count;
    }

    /**
     * Decrements the number of processes in the par block. The last process of the par block to call this method causes
     * the process in which the par appers to become ready to run again.
     */
    public void decrement() {
        processCount--;
        if (processCount == 0) {
            process.setReady();
        }
    }
}
