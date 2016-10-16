package Generated.invocationSimpleTest;
import java.util.*;
import ProcessJ.runtime.PJProcess;
import ProcessJ.runtime.PJTimer;
import ProcessJ.runtime.*;

public class invocationSimpleTest { 
	public static class writer extends PJProcess {

		PJChannel<Integer> _pd$c;

	    public writer(PJChannel<Integer> _pd$c) {
	    	this._pd$c = _pd$c;
	    }

		@Override
		public synchronized void run() {
			switch(this.runLabel) {
				case 0: break;
			    case 1: resume(1); break;
			    case 2: resume(2); break;
			}

			label(1);
			if (_pd$c.isReadyToWrite()) {
				_pd$c.write(this, 10);
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


	public static int compute1(int _pd$z, int _pd$y) {
		return (_pd$z + 10);
	}
	public static class foo extends PJProcess {

		PJOne2OneChannel<Integer> _ld0$c;
		int _ld1$x;

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
			invocationSimpleTest.compute1( 1, 2 );
			label(1);	
			final PJPar par1 = new PJPar(1, this);

			(new invocationSimpleTest.writer( _ld0$c ){
			  public void finalize() {
			    par1.decrement();    
			  }
			}).schedule();

			setNotReady();
			this.runLabel = 2;
			yield();
			label(2);
			label(3);
			if(_ld0$c.isReadyToRead(this)) {
				_ld1$x = _ld0$c.read(this);
				this.runLabel = 4;
				yield();
			} else {
				setNotReady();
				_ld0$c.addReader(this);
				this.runLabel = 3;
				yield();
			}
			label(4)
	;
			terminate();
		}
	}


}