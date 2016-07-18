

import java.util.*;
import ProcessJ.runtime.*;

public class commstime {
	public static class Prefix extends PJProcess {
		int _pd$init;
		PJChannel<Integer> _pd$in;
		PJChannel<Integer> _pd$out;
		int _pd$n;
		int _ld0$val;

	    public Prefix(int _pd$init, PJChannel<Integer> _pd$in, PJChannel<Integer> _pd$out, int _pd$n) {
	    	this._pd$init = _pd$init;
	    	this._pd$in = _pd$in;
	    	this._pd$n = _pd$n;
	    	this._pd$out = _pd$out;
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
			}

			label(1);
			if (_pd$out.isReadyToWrite()) {
				_pd$out.write(this, _pd$init);
				this.runLabel = 2;
				yield();
			} else {
				setNotReady();
				this.runLabel = 1;
				yield();
			}
			label(2);
			while( (_pd$n != 0) ) {
			  label(3);
			  if(_pd$in.isReadyToRead(this)) {
			  	_ld0$val = _pd$in.read(this);
			  	this.runLabel = 4;
			  	yield();
			  } else {
			  	setNotReady();
			  	_pd$in.addReader(this);
			  	this.runLabel = 3;
			  	yield();
			  }
			  label(4);

			  label(5);
			  if (_pd$out.isReadyToWrite()) {
			  	_pd$out.write(this, _ld0$val);
			  	this.runLabel = 6;
			  	yield();
			  } else {
			  	setNotReady();
			  	this.runLabel = 5;
			  	yield();
			  }
			  label(6);
			  _pd$n--;
			};
			terminate();
		}
	}


	public static class Delta extends PJProcess {
		PJChannel<Integer> _pd$in;
		PJChannel<Integer> _pd$out1;
		PJChannel<Integer> _pd$out2;
		int _pd$n;
		int _ld0$val;

	    public Delta(PJChannel<Integer> _pd$in, PJChannel<Integer> _pd$out1, PJChannel<Integer> _pd$out2, int _pd$n) {
	    	this._pd$out2 = _pd$out2;
	    	this._pd$in = _pd$in;
	    	this._pd$out1 = _pd$out1;
	    	this._pd$n = _pd$n;
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
			}
			while( (_pd$n != 0) ) {
			  label(1);
			  if(_pd$in.isReadyToRead(this)) {
			  	_ld0$val = _pd$in.read(this);
			  	this.runLabel = 2;
			  	yield();
			  } else {
			  	setNotReady();
			  	_pd$in.addReader(this);
			  	this.runLabel = 1;
			  	yield();
			  }
			  label(2);

			  label(3);
			  if (_pd$out1.isReadyToWrite()) {
			  	_pd$out1.write(this, _ld0$val);
			  	this.runLabel = 4;
			  	yield();
			  } else {
			  	setNotReady();
			  	this.runLabel = 3;
			  	yield();
			  }
			  label(4);

			  label(5);
			  if (_pd$out2.isReadyToWrite()) {
			  	_pd$out2.write(this, _ld0$val);
			  	this.runLabel = 6;
			  	yield();
			  } else {
			  	setNotReady();
			  	this.runLabel = 5;
			  	yield();
			  }
			  label(6);
			  _pd$n--;
			};
			terminate();
		}
	}


	public static class Succ extends PJProcess {
		PJChannel<Integer> _pd$in;
		PJChannel<Integer> _pd$out;
		int _pd$n;
		int _ld0$val;

	    public Succ(PJChannel<Integer> _pd$in, PJChannel<Integer> _pd$out, int _pd$n) {
	    	this._pd$in = _pd$in;
	    	this._pd$n = _pd$n;
	    	this._pd$out = _pd$out;
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
			while( (_pd$n != 0) ) {
			  label(1);
			  if(_pd$in.isReadyToRead(this)) {
			  	_ld0$val = _pd$in.read(this);
			  	this.runLabel = 2;
			  	yield();
			  } else {
			  	setNotReady();
			  	_pd$in.addReader(this);
			  	this.runLabel = 1;
			  	yield();
			  }
			  label(2);

			  label(3);
			  if (_pd$out.isReadyToWrite()) {
			  	_pd$out.write(this, (_ld0$val + 1));
			  	this.runLabel = 4;
			  	yield();
			  } else {
			  	setNotReady();
			  	this.runLabel = 3;
			  	yield();
			  }
			  label(4);
			  _pd$n--;
			};
			terminate();
		}
	}


	public static class Consumer extends PJProcess {
		PJChannel<Integer> _pd$in;
		int _pd$n;
		int _ld0$val;

	    public Consumer(PJChannel<Integer> _pd$in, int _pd$n) {
	    	this._pd$in = _pd$in;
	    	this._pd$n = _pd$n;
	    }

		@Override
		public synchronized void run() {
			switch(this.runLabel) {
				case 0: break;
			    case 1: resume(1); break;
			    case 2: resume(2); break;
			}
			while( (_pd$n != 0) ) {
			  label(1);
			  if(_pd$in.isReadyToRead(this)) {
			  	_ld0$val = _pd$in.read(this);
			  	this.runLabel = 2;
			  	yield();
			  } else {
			  	setNotReady();
			  	_pd$in.addReader(this);
			  	this.runLabel = 1;
			  	yield();
			  }
			  label(2);
			  if( ((_ld0$val % 10000) == 0) ) {
			    std.io.println( _ld0$val );
			  };
			  _pd$n--;
			};
			terminate();
		}
	}


	public static class main extends PJProcess {
		String[] _pd$args;
		PJOne2OneChannel<Integer> _ld0$a;
		PJOne2OneChannel<Integer> _ld1$b;
		PJOne2OneChannel<Integer> _ld2$c;
		PJOne2OneChannel<Integer> _ld3$d;
		int _ld4$n;

	    public main(String[] _pd$args) {
	    	this._pd$args = _pd$args;
	    }

		@Override
		public synchronized void run() {
			switch(this.runLabel) {
				case 0: break;
			    case 1: resume(1); break;
			}
			_ld0$a = new PJOne2OneChannel<Integer>();
			_ld1$b = new PJOne2OneChannel<Integer>();
			_ld2$c = new PJOne2OneChannel<Integer>();
			_ld3$d = new PJOne2OneChannel<Integer>();
			_ld4$n= std.strings.string2int( (_pd$args[1]) );
			final PJPar par1 = new PJPar(4, this);

			(new commstime.Prefix( 0, _ld0$a, _ld2$c, _ld4$n ){
			  public void finalize() {
			    par1.decrement();    
			  }
			}).schedule();

			(new commstime.Delta( _ld2$c, _ld3$d, _ld1$b, _ld4$n ){
			  public void finalize() {
			    par1.decrement();    
			  }
			}).schedule();

			(new commstime.Succ( _ld1$b, _ld0$a, _ld4$n ){
			  public void finalize() {
			    par1.decrement();    
			  }
			}).schedule();

			(new commstime.Consumer( _ld3$d, _ld4$n ){
			  public void finalize() {
			    par1.decrement();    
			  }
			}).schedule();

			setNotReady();
			this.runLabel = 1;
			yield();
			label(1);
			terminate();
		}
	}

	public static void main(String[] args) {
		Scheduler scheduler = new Scheduler();

		PJProcess.scheduler = scheduler;
		System.out.println("Added _main process to scheduler...");

		(new commstime.main(args)).schedule();

		System.out.println("Starting the scheduler...");
		PJProcess.scheduler.start();

		System.out.println("Scheduler thread.start() done.");
	}
	public static boolean getTrue() {
		return true;
	}
}