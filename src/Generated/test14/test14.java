package Generated.test14;
import java.util.*;
import ProcessJ.runtime.*;

public class test14 {
	public static class f extends PJProcess {
		PJTimer _ld0$t;

	    public f() {
	    }

		@Override
		public synchronized void run() {
			switch(this.runLabel) {
				case 0: break;
			    case 1: resume(1); break;
			}
			_ld0$t = new PJTimer(this, 100);
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
}