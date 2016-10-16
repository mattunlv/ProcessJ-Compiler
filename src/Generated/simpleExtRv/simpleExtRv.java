package Generated.simpleExtRv;
import java.util.*;
import ProcessJ.runtime.PJProcess;
import ProcessJ.runtime.PJTimer;
import ProcessJ.runtime.*;

public class simpleExtRv { 
	public static class foo extends PJProcess {

		PJOne2OneChannel<Integer> _ld0$c;
		PJOne2OneChannel<Integer> _ld1$report;
		int _ld2$id;
		int _ld3$x;

	    public foo() {
	    }

		@Override
		public synchronized void run() {
			switch(this.runLabel) {
				case 0: break;
			    case 1: resume(1); break;
			    case 2: resume(2); break;
			    case 3: resume(3); break;
			    case 4: resume(4); break;
			}
			_ld0$c = new PJOne2OneChannel<Integer>();
			_ld1$report = new PJOne2OneChannel<Integer>();
			label(3);
			if(_ld0$c.isReadyToRead(this)) {
				_ld2$id = _ld0$c.readPreRendezvous(this);
				_ld3$x = 5;

				label(1);
				if (_ld1$report.isReadyToWrite()) {
					_ld1$report.write(this, 10);
					this.runLabel = 2;
					yield();
				} else {
					setNotReady();
					this.runLabel = 1;
					yield();
				}

				label(2);
				_ld0$c.readPostRendezvous(this);
				this.runLabel = 4;
				yield();
			} else {
				setNotReady();
				_ld0$c.addReader(this);
				this.runLabel = 3;
				yield();
			}
			label(4);
			terminate();
		}
	}


}