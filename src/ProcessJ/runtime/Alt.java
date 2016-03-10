package ProcessJ.runtime;

public class Alt {
	private Object[] channels;

	public Alt(Object... channels) {
		this.channels = channels;
	}

	// -1: no channels are ready
	// remember to do a isReadyToRead after getting the index in the process.
	public int getReadyIndex() {
		for (int i = 0; i < channels.length; i++) {
			if (((Channel<? extends Object>) channels[i]).isReadyToReadAltAndReserve())
				return i;
		}
		return -1;
	}
}
