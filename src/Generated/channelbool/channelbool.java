package Generated.channelbool;
import java.util.*;
import ProcessJ.runtime.PJProcess;
import ProcessJ.runtime.PJTimer;
import ProcessJ.runtime.*;

public class channelbool { 
	public static class writer extends PJProcess {

		PJChannel<Boolean> _pd$c;

	    public writer(PJChannel<Boolean> _pd$c) {
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
				_pd$c.write(this, true);
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


	public static class reader extends PJProcess {

		PJChannel<Boolean> _pd$c;
		boolean _ld0$x;

	    public reader(PJChannel<Boolean> _pd$c) {
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
			if(_pd$c.isReadyToRead(this)) {
				_ld0$x = _pd$c.read(this);
				this.runLabel = 2;
				yield();
			} else {
				setNotReady();
				_pd$c.addReader(this);
				this.runLabel = 1;
				yield();
			}
			label(2)
	;
			terminate();
		}
	}


	public static class test extends PJProcess {

		PJOne2OneChannel<Boolean> _ld0$c;

	    public test() {
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
			_ld0$c = new PJOne2OneChannel<Boolean>();
			label(1);	
			final PJPar par1 = new PJPar(1, this);

			(new channelbool.writer( _ld0$c ){
			  public void finalize() {
			    par1.decrement();    
			  }
			}).schedule();

			setNotReady();
			this.runLabel = 2;
			yield();
			label(2);
			label(3);	
			final PJPar par2 = new PJPar(1, this);

			(new channelbool.reader( _ld0$c ){
			  public void finalize() {
			    par2.decrement();    
			  }
			}).schedule();

			setNotReady();
			this.runLabel = 4;
			yield();
			label(4);
			terminate();
		}
	}


}