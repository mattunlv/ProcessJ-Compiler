package ProcessJ.runtime;

import java.util.LinkedList;

/* JVMCSP is maintained at the University of Nevada Las Vegas.
 * 
 * For more information please contact matt.pedersen@unlv.edu
 * or see processj.org
 */

/**
 * The runtime representation of a one-to-many channel.
 *
 * @author Cabel Shrestha
 * @version 1.0
 * @since 2016-05-01
 */

public class PJOne2ManyChannel<T> extends PJChannel<T> {
    /**
     * A reference to the writer of the channel
     */
    private PJProcess writer = null;
    /**
     * A list of references to the readers wishing to read from this channel.
     */
    private LinkedList<PJProcess> readers = new LinkedList<PJProcess>();

    /**
     * Constructor.
     */
    public PJOne2ManyChannel() {
        this.type = TYPE_ONE2MANY;
    }

    /**
     * Writes a data item value of type T to the channel.
     * 
     * @param p
     *            The writing process.
     * @param item
     *            The data item to be exchanged.
     */
    @Override
    synchronized public void write(PJProcess p, T item) {
        data = item;
        writer = p;
        writer.setNotReady();
        ready = true;
        if (readers.size() > 0) {
            PJProcess reader = readers.removeFirst();
            reservedForReader = reader;
            reader.setReady();
        }
    }

    /**
     * Reads a data item value item of type T from the channel.
     * 
     * @param p
     *            The reading process.
     * @return T The read value.
     */
    @Override
    synchronized public T read(PJProcess p) {
        ready = false;
        reservedForReader = null;
        // Might not be reserved at all. Doesn't hurt do it.
        reservedForAlt = false;
        writer.setReady();
        //TODO: why are we not making writer/reader null?
        T myData = data;
        data = null;

        return myData;
    }

    /**
     * First part of an extended rendez-vous read. Returns the data item but does not set the writer ready.
     * 
     * @param p
     *            The reading process.
     * @return T The read value.
     */
    @Override
    synchronized public T readPreRendezvous(PJProcess p) {
        T myData = data;
        data = null;
        return myData;
    }

    /**
     * Second part of an extended rendez-voud read. Sets the writer ready to run.
     * 
     * @param p
     *            The reading process.
     */
    @Override
    synchronized public void readPostRendezvous(PJProcess p) {
        ready = false;
        reservedForReader = null;
        reservedForAlt = false;
        writer.setReady();
    }

    /**
     * Adds a reader to the reader queue.
     * 
     * @param p
     *            The process waiting to read.
     */
    @Override
    synchronized public void addReader(PJProcess p) {
        readers.add(p);
    }

    /**
     * Does nothing.
     * 
     * @param p
     *            A process.
     */
    @Override
    synchronized public void addWriter(PJProcess p) {
        // TODO: Auto-generated method stub
    }

}
