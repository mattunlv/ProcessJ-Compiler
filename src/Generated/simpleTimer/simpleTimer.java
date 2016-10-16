package Generated.simpleTimer;
import java.util.*;
import ProcessJ.runtime.*;

public class simpleTimer {
	public static class foo extends PJProcess {
		PJTimer _ld0$t;
		long _ld1$time;

	    public foo() {
	    }

		@Override
		public synchronized void run() {
			switch(this.runLabel) {
				case 0: break;
			    case 1: resume(1); break;
			}
			_ld1$time = PJTimer.read();
			_ld0$t = new PJTimer(this, 1000000);
			try {
				_ld0$t.start();
				setNotReady();
				this.runLabel = 1;
				yield();
			} catch (InterruptedException e) {
				System.out.println("PJTimer Interrupted Exception!");
			}
			label(1);
			terminate();
		}
	}


	public static boolean getTrue() {
		return true;
	}
}