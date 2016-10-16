package Generated.listing;
import java.util.*;
import ProcessJ.runtime.*;

public class listing {
	public static class chantest extends PJProcess {
		PJOne2OneChannel<Integer> _ld0$c;
		PJOne2OneChannel<Integer> _ld1$cr;
		PJOne2OneChannel<Boolean> _ld2$cw;
		PJMany2ManyChannel<Integer> _ld3$cs;
		PJMany2OneChannel<Long> _ld4$csw;
		PJOne2OneChannel<Integer>[] _ld5$carr;

	    public chantest() {
	    }

		@Override
		public synchronized void run() {
			_ld0$c = new PJOne2OneChannel<Integer>();
			_ld3$cs = new PJMany2ManyChannel<Integer>();
			_ld4$csw = new PJMany2OneChannel<Long>();
			_ld5$carr = (PJOne2OneChannel<Integer>[])new Object[100];
			for(int i=0; i < 100; i++) {
				_ld5$carr[i] = new PJOne2OneChannel<Integer>();
			};
			terminate();
		}
	}


	public static class foo extends PJProcess {
		PJBarrier _pd$b;
		PJBarrier _pd$c;

	    public foo(PJBarrier _pd$b, PJBarrier _pd$c) {
	    	this._pd$b = _pd$b;
	    	this._pd$c = _pd$c;
	    }

		@Override
		public synchronized void run() {
	;
			terminate();
		}
	}


	public static class bar extends PJProcess {
		PJBarrier _pd$b;
		PJBarrier _pd$c;

	    public bar(PJBarrier _pd$b, PJBarrier _pd$c) {
	    	this._pd$b = _pd$b;
	    	this._pd$c = _pd$c;
	    }

		@Override
		public synchronized void run() {
	;
			terminate();
		}
	}


	public static class baz extends PJProcess {
		PJBarrier _pd$b;
		PJBarrier _pd$c;

	    public baz(PJBarrier _pd$b, PJBarrier _pd$c) {
	    	this._pd$b = _pd$b;
	    	this._pd$c = _pd$c;
	    }

		@Override
		public synchronized void run() {
	;
			terminate();
		}
	}


	public static class partest extends PJProcess {
		PJChannel<Integer> _pd$out;
		int _ld0$x;

	    public partest(PJChannel<Integer> _pd$out) {
	    	this._pd$out = _pd$out;
	    }

		@Override
		public synchronized void run() {
			switch(this.runLabel) {
				case 0: break;
			    case 1: resume(1); break;
			    case 2: resume(2); break;
			}

			label(1);
			if (_pd$out.isReadyToWrite()) {
				_pd$out.write(this, 10);
				this.runLabel = 2;
				yield();
			} else {
				setNotReady();
				this.runLabel = 1;
				yield();
			}
			label(2);
			terminate();
		}
	}


	public static boolean getTrue() {
		return true;
	}
}