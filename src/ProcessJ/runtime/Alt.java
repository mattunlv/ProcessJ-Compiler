package ProcessJ.runtime;

public class Alt {
//	private Object[] channels;
	private Object guards[];
	private boolean[] bGuards;

	private int caseCount;
	private Process process;
	
	public static final String SKIP_GUARD = "skip";

//	public Alt(Object... channels) {
//		this.channels = channels;
//	}

	public Alt(int caseCount, Process p) {
		this.caseCount = caseCount;
		this.process = p;
		this.bGuards = new boolean[caseCount];
		this.guards = new Object[caseCount];
	}
	
	public boolean setGuards(boolean[] bg, Object[] guards) {

		this.bGuards = bg;
		this.guards = guards;
		
		//if one of the bGuards is true, return true, else return false.
		for(boolean b: bg) {
			if (b) {
				return b;
			}
		}
		
		return false;
	}
	
	public int getReadyGuardIndex() {
		//initialize B to all true, since missing boolean 
		//guard means it is true.
		int chosen = -1;
		for (int i = 0; i < bGuards.length; i++) {
			if (bGuards[i]) {
				if (guards[i] instanceof String && SKIP_GUARD.equals(guards[i])) {
					chosen = i;
					break;
				} else if (guards[i] instanceof Timer) {
					Timer t = (Timer)guards[i];
					//if (1st time && timeout amount ==0)
					//or if not 1st time and timeout has happened, chosen = i
					if ((!t.started && t.timeout <= 0L) || t.stopped) {
						chosen=i;
						break;
					}
				} else if (guards[i] instanceof Channel) {
					//if not shared call isReady() and if true
					Channel c = (Channel) guards[i];
					if (c.isSharedRead()){
						//if shared, call isReadyAndReserve() and if true
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

	// -1: no channels are ready
	// remember to do a isReadyToRead after getting the index in the process.
//	public int getReadyIndex() {
//		for (int i = 0; i < channels.length; i++) {
//			if (((Channel<? extends Object>) channels[i]).isReadyToReadAltAndReserve())
//				return i;
//		}
//		return -1;
//	}
}
