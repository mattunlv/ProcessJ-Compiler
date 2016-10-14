package ProcessJ.runtime;

/* JVMCSP is maintained at the University of Nevada Las Vegas.
 * 
 * For more information please contact matt.pedersen@unlv.edu
 * or see processj.org
 */

/**
 * The runtime representation of a 'alt' statement.
 *
 * @author Cabel Shrestha
 * @version 1.0
 * @since 2016-05-01
 */

public class PJAlt {
    /**
     * An array of guard objects. A guard is either a skip, a channel read or a timeout.
     */
    private Object guards[];
    /**
     * An array of Boolean pre-guards
     */
    private boolean[] bGuards;

    /**
     * The process in which the alt appears.
     */
    private PJProcess process;

    /**
     * String representing the syntactic representation of a skip statement.
     */
    public static final String SKIP_GUARD = "skip";

    /**
     * Constructs a new instance of an alt.
     * 
     * @param caseCount
     *            The number of guarded statements in this alt.
     * @param p
     *            The process in which this alt appears.
     */
    public PJAlt(int caseCount, PJProcess p) {
        this.process = p;
        this.bGuards = new boolean[caseCount];
        this.guards = new Object[caseCount];
    }

    /**
     * Sets the guards and the Boolean pre-guards and returns true if one or more of the Boolean pre-guards is true --
     * false otherwise.
     * 
     * @param bg
     *            An array of Boolean values representing the values of the Boolean pre-guards.
     * @param guards
     *            An array of guards.
     * @return Returns true if at least one boolean guard is true.
     */
    public boolean setGuards(boolean[] bg, Object[] guards) {
        this.bGuards = bg;
        this.guards = guards;

        // If just one of the bGuards is true, return true, else return false.
        for (boolean b : bg) {
            if (b) {
                return b;
            }
        }
        return false;
    }

    /**
     * Returns the index (of guards[]) of the first guard that is ready. If no guard is ready, -1 is returned.
     * 
     * @return The index of a ready guard.
     */
    public int getReadyGuardIndex() {
        int chosen = -1;
        for (int i = 0; i < bGuards.length; i++) {
            if (bGuards[i]) {
                if (guards[i] instanceof String && SKIP_GUARD.equals(guards[i])) {
                    chosen = i;
                    break;
                } else if (guards[i] instanceof PJTimer) {
                    PJTimer t = (PJTimer) guards[i];
                    // If (1st time && timeout amount ==0)
                    // or if not 1st time and timeout has happened, chosen = i
                    if ((!t.started && t.timeout <= 0L) || t.expired) {
                        chosen = i;
                        break;
                    }
                } else if (guards[i] instanceof PJChannel) {
                    // If not shared call isReady() and if true
                    PJChannel c = (PJChannel) guards[i];
                    if (c.isSharedRead()) {
                        // If shared, call isReadyAndReserve() and if true
                        if (c.isReadyToReadAltAndReserve()) {
                            chosen = i;
                            break;
                        }
                    } else {
                        if (c.isReadyToRead(process)) {
                            chosen = i;
                            break;
                        }
                    }
                }
            }
        }
        return chosen;
    }
}
