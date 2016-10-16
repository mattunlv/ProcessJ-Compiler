package ProcessJ.runtime;

import java.util.concurrent.Delayed;
import java.util.concurrent.TimeUnit;

/* JVMCSP is maintained at the University of Nevada Las Vegas.
 * 
 * For more information please contact matt.pedersen@unlv.edu
 * or see processj.org
 */

/**
 * The runtime representation of the ProcessJ 'timer' type.
 *
 * @author Cabel Shrestha
 * @version 1.0
 * @since 2016-05-01
 */
public class PJTimer implements Delayed {
    private PJProcess process;

    private long delay;
    private boolean killed = false;
    public boolean started = false;
    public boolean expired = false;

    public final long timeout;

    public PJTimer() {
        this.timeout = 0L;
    }

    public PJTimer(PJProcess process, long timeout) {
        this.process = process;
        this.timeout = timeout;
    }

    public void start() throws InterruptedException {
        this.delay = System.currentTimeMillis() + timeout;
        PJProcess.scheduler.insertTimer(this);

        started = true;
    }

    public void expire() {
        expired = true;
    }

    public static long read() {
        return System.currentTimeMillis();
    }

    public void kill() {
        killed = true;
    }

    public PJProcess getProcess() {
        if (killed) {
            return null;
        } else {
            return process;
        }
    }

    @Override
    public long getDelay(TimeUnit unit) {
        long diff = delay - System.currentTimeMillis();
        return unit.convert(diff, TimeUnit.MILLISECONDS);
    }

    @Override
    public int compareTo(Delayed o) {
        int retVal = Long.valueOf(this.delay).compareTo(((PJTimer) o).delay);
        return retVal;
    }
}
