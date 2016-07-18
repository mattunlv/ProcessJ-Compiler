package ProcessJ.runtime;

public class PJAlt {
	private Object guards[];
	private boolean[] bGuards;

	private PJProcess process;
	
	public static final String SKIP_GUARD = "skip";

	public PJAlt(int caseCount, PJProcess p) {
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
		int chosen = -1;
		for (int i = 0; i < bGuards.length; i++) {
			if (bGuards[i]) {
				if (guards[i] instanceof String && SKIP_GUARD.equals(guards[i])) {
					chosen = i;
					break;
				} else if (guards[i] instanceof PJTimer) {
					PJTimer t = (PJTimer)guards[i];
					//if (1st time && timeout amount ==0)
					//or if not 1st time and timeout has happened, chosen = i
					if ((!t.started && t.timeout <= 0L) || t.expired) {
						chosen=i;
						break;
					}
				} else if (guards[i] instanceof PJChannel) {
					//if not shared call isReady() and if true
					PJChannel c = (PJChannel) guards[i];
					if (c.isSharedRead()){
						//if shared, call isReadyAndReserve() and if true
						if (c.isReadyToReadAltAndReserve()) {
							chosen = i;
							break;
						}
					} else {
//						System.out.println("checking channel w/c is shared write single read");
//						System.out.println("c.isReadyToRead=" + c.isReadyToRead(process));
						if (c.isReadyToRead(process)) {
							chosen = i;
							break;
						} else {
//							System.out.println("not ready!!!!!!");
						}
					}
				}
			}
		}
		
		return chosen;
	}
}
