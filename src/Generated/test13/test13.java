package Generated.test13;
import java.util.*;
import ProcessJ.runtime.*;

public class test13 {
	public static class g extends PJProcess {
		PJChannel<Integer> _pd$in;
		PJBarrier _pd$b;
		int _ld0$v;

	    public g(PJChannel<Integer> _pd$in, PJBarrier _pd$b) {
	    	this._pd$b = _pd$b;
	    	this._pd$in = _pd$in;
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
			label(3);
			if(!_pd$in.claim() ) {
				this.runLabel = 3;
				yield();
			}
			label(1);
			if(_pd$in.isReadyToRead(this)) {
				_ld0$v = _pd$in.read(this);
				this.runLabel = 2;
				yield();
			} else {
				setNotReady();
				_pd$in.addReader(this);
				this.runLabel = 1;
				yield();
			}
			label(2);
			_pd$in.unclaim();
			_pd$b.sync(this);
			this.runLabel = 4;
			yield();
			label(4);
			terminate();
		}
	}


	public static boolean getTrue() {
		return true;
	}
}