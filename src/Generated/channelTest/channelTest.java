package Generated.channelTest;
import java.util.*;
import ProcessJ.runtime.PJProcess;
import ProcessJ.runtime.PJTimer;
import ProcessJ.runtime.*;

public class channelTest { 
	public static class writer extends PJProcess {
		PJChannel<Integer> _pd$chanW;
		int _ld0$x;

	    public writer(PJChannel<Integer> _pd$chanW) {
	    	this._pd$chanW = _pd$chanW;
	    }

		@Override
		public synchronized void run() {
			switch(this.runLabel) {
				case 0: break;
			    case 1: resume(1); break;
			    case 2: resume(2); break;
			}
			_ld0$x = 2;;

			label(1);
			if (_pd$chanW.isReadyToWrite()) {
				_pd$chanW.write(this, _ld0$x);
				this.runLabel = 2;
				yield();
			} else {
				setNotReady();
				this.runLabel = 1;
				yield();
			}

			label(2);;
			_ld0$x++;
			terminate();
		}
	}


	public static class compute extends PJProcess {
		int _pd$x;
		int _pd$y;
		int _pd$z;

	    public compute(int _pd$x, int _pd$y, int _pd$z) {
	    	this._pd$x = _pd$x;
	    	this._pd$y = _pd$y;
	    	this._pd$z = _pd$z;
	    }

		@Override
		public synchronized void run() {
			_pd$x = 2;
			terminate();
		}
	}


	public static int compute1(int _pd$z, int _pd$y) {
		return (_pd$z + 10);
	}
	public static int compute2(int _pd$x) {
		return _pd$x;
	}
	public static int compute3(int _pd$x) {
		_pd$x = 5;
		return _pd$x;
	}
	public static boolean compute4(String _pd$s, int _pd$x, int _pd$y) {
		_pd$x = 5;
		return true;
	}
	public static class reader extends PJProcess {
		PJChannel<Integer> _pd$chanR;
		int _ld0$y;
		int _ld1$temp;
		int _ld2$temp;
		int _ld3$temp;
		int _ld4$temp;
		int _ld5$temp;

	    public reader(PJChannel<Integer> _pd$chanR) {
	    	this._pd$chanR = _pd$chanR;
	    }

		@Override
		public synchronized void run() {
			switch(this.runLabel) {
				case 0: break;
			    case 1: resume(1); break;
			    case 2: resume(2); break;
			    case 3: resume(3); break;
			    case 4: resume(4); break;
			    case 5: resume(5); break;
			    case 6: resume(6); break;
			    case 7: resume(7); break;
			    case 8: resume(8); break;
			}
			_ld0$y = 100;;
			label(1);	
			final PJPar par1 = new PJPar(1, this);

			label(3);
			if(_pd$chanR.isReadyToRead(this)) {
				_ld1$temp = _pd$chanR.read(this);
				this.runLabel = 4;
				yield();
			} else {
				setNotReady();
				_pd$chanR.addReader(this);
				this.runLabel = 3;
				yield();
			}
			label(4);;

			label(5);
			if(_pd$chanR.isReadyToRead(this)) {
				_ld4$temp = _pd$chanR.read(this);
				this.runLabel = 6;
				yield();
			} else {
				setNotReady();
				_pd$chanR.addReader(this);
				this.runLabel = 5;
				yield();
			}
			label(6);;
			_ld3$temp=channelTest.compute2( _ld4$temp );
			_ld2$temp=channelTest.compute1( _ld3$temp, _ld0$y );

			label(7);
			if(_pd$chanR.isReadyToRead(this)) {
				_ld5$temp = _pd$chanR.read(this);
				this.runLabel = 8;
				yield();
			} else {
				setNotReady();
				_pd$chanR.addReader(this);
				this.runLabel = 7;
				yield();
			}
			label(8);;
			(new channelTest.compute( _ld1$temp, _ld2$temp, _ld5$temp ){
			  public void finalize() {
			    par1.decrement();    
			  }
			}).schedule();

			setNotReady();
			this.runLabel = 2;
			yield();
			label(2);
			terminate();
		}
	}


	public static int getValue() {
		return 5;
	}
	public static class anonymous extends PJProcess {
		PJMany2ManyChannel<Integer> _ld0$a;
		int _ld1$x;

	    public anonymous() {
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
			_ld0$a = new PJMany2ManyChannel<Integer>();;
			_ld1$x = 0;;

			label(1);
			if (_ld0$a.isReadyToWrite()) {
				_ld0$a.write(this, 100);
				this.runLabel = 2;
				yield();
			} else {
				_ld0$a.addWriter(this);
				setNotReady();
				this.runLabel = 1;
				yield();
			}

			label(2);;
			label(3);
			if(_ld0$a.isReadyToRead(this)) {
				_ld1$x = _ld0$a.read(this);
				this.runLabel = 4;
				yield();
			} else {
				setNotReady();
				_ld0$a.addReader(this);
				this.runLabel = 3;
				yield();
			}
			label(4);
			terminate();
		}
	}


	public static class main extends PJProcess {
		String[] _pd$args;
		PJOne2OneChannel<Integer> _ld0$intChan;

	    public main(String[] _pd$args) {
	    	this._pd$args = _pd$args;
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
			_ld0$intChan = new PJOne2OneChannel<Integer>();;
			label(1);	
			final PJPar par1 = new PJPar(1, this);

			(new channelTest.writer( _ld0$intChan ){
			  public void finalize() {
			    par1.decrement();    
			  }
			}).schedule();

			setNotReady();
			this.runLabel = 2;
			yield();
			label(2);;
			label(3);	
			final PJPar par2 = new PJPar(1, this);

			(new channelTest.reader( _ld0$intChan ){
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

	public static void main(String[] args) {
		Scheduler scheduler = new Scheduler();

		PJProcess.scheduler = scheduler;
		System.out.println("Added _main process to scheduler...");

		(new channelTest.main(args)).schedule();

		System.out.println("Starting the scheduler...");
		PJProcess.scheduler.start();

		System.out.println("Scheduler thread.start() done.");
	}
}