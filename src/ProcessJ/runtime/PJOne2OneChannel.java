package ProcessJ.runtime;

/* JVMCSP is maintained at the University of Nevada Las Vegas.
 * 
 * For more information please contact matt.pedersen@unlv.edu
 * or see processj.org
 */

/**
 * The runtime representation of a one-to-one channel.
 *
 * @author Cabel Shrestha
 * @version 1.0
 * @since 2016-05-01
 */

public class PJOne2OneChannel<T> extends PJChannel<T> {
    /**
     * A reference to the writer of the channel
     */
    private PJProcess writer = null;
    /**
     * A reference to the reader of the channel
     */
    private PJProcess reader = null;

    /**
     * Constructor.
     */
    public PJOne2OneChannel() {
        this.type = TYPE_ONE2ONE;
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
        if (reader != null) {
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
        // We need to set the writer ready as the synchronization has happened 
        // when the data was read.
        writer.setReady();
        // clear the writer
        writer = null;
        reader = null;
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
        writer.setReady();
        writer = null;
        reader = null;
    }

    @Override
    synchronized public void addReader(PJProcess p) {
        reader = p;
    }

    /**
     * Does nothing.
     * 
     * @param p
     *            A process.
     */
    @Override
    synchronized public void addWriter(PJProcess p) {
        // TODO Auto-generated method stub
        // Cabel: why does this not have a body?
    }
}
