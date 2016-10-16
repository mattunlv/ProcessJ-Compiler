package Generated.test11;
import java.util.*;
import ProcessJ.runtime.*;

public class test11 {
	public static class f extends PJProcess {
		PJChannel<Integer> _pd$in;
		int _pd$count;
		int _ld0$sum;
		int _ld1$val;

	    public f(PJChannel<Integer> _pd$in, int _pd$count) {
	    	this._pd$count = _pd$count;
	    	this._pd$in = _pd$in;
	    }

		@Override
		public synchronized void run() {
			switch(this.runLabel) {
				case 0: break;
			    case 1: resume(1); break;
			    case 2: resume(2); break;
			}
			_ld0$sum = 0;
			while( (_pd$count > 0) ) {
			  label(1);
			  if(_pd$in.isReadyToRead(this)) {
			  	_ld1$val = _pd$in.read(this);
			  	this.runLabel = 2;
			  	yield();
			  } else {
			  	setNotReady();
			  	_pd$in.addReader(this);
			  	this.runLabel = 1;
			  	yield();
			  }
			  label(2);
			  _ld0$sum = (_ld0$sum + _ld1$val);
			  _pd$count--;
			};
			std.io.println( _ld0$sum );
			terminate();
		}
	}

}