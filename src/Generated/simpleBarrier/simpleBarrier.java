package Generated.simpleBarrier;
import java.util.*;
import ProcessJ.runtime.*;

public class simpleBarrier {
	public static class foo1 extends PJProcess {
		PJOne2OneChannel<Integer> _ld0$c;
		PJBarrier _ld1$a;
		PJBarrier _ld2$b;

	    public foo1() {
	    }

		@Override
		public synchronized void run() {
			switch(this.runLabel) {
				case 0: break;
			    case 1: resume(1); break;
			    case 2: resume(2); break;
			}
			_ld0$c = new PJOne2OneChannel<Integer>();
			_ld1$a = new PJBarrier();
			_ld2$b = new PJBarrier();
			final PJPar par1 = new PJPar(3, this);
			_ld1$a.enroll(3);
			_ld2$b.enroll(3);

			(new simpleBarrier.reader( _ld0$c, _ld2$b ){
			  public void finalize() {
			    par1.decrement();    
				_ld1$a.resign();
				_ld2$b.resign();
			  }
			}).schedule();

			(new simpleBarrier.writer( _ld0$c ){
			  public void finalize() {
			    par1.decrement();    
				_ld1$a.resign();
				_ld2$b.resign();
			  }
			}).schedule();

			(new simpleBarrier.bar( _ld2$b, _ld1$a ){
			  public void finalize() {
			    par1.decrement();    
				_ld1$a.resign();
				_ld2$b.resign();
			  }
			}).schedule();

			setNotReady();
			this.runLabel = 1;
			yield();
			label(1);
			final PJPar par2 = new PJPar(2, this);
			_ld2$b.enroll(2);

			(new simpleBarrier.foo( _ld2$b ){
			  public void finalize() {
			    par2.decrement();    
				_ld2$b.resign();
			  }
			}).schedule();

			(new simpleBarrier.bar1( _ld2$b ){
			  public void finalize() {
			    par2.decrement();    
				_ld2$b.resign();
			  }
			}).schedule();

			setNotReady();
			this.runLabel = 2;
			yield();
			label(2);
			terminate();
		}
	}


	public static class foo extends PJProcess {
		PJBarrier _pd$b;

	    public foo(PJBarrier _pd$b) {
	    	this._pd$b = _pd$b;
	    }

		@Override
		public synchronized void run() {
			switch(this.runLabel) {
				case 0: break;
			    case 1: resume(1); break;
			}
			_pd$b.sync(this);
			this.runLabel = 1;
			yield();
			label(1);
			terminate();
		}
	}


	public static class bar1 extends PJProcess {
		PJBarrier _pd$b;

	    public bar1(PJBarrier _pd$b) {
	    	this._pd$b = _pd$b;
	    }

		@Override
		public synchronized void run() {
			switch(this.runLabel) {
				case 0: break;
			    case 1: resume(1); break;
			}
			_pd$b.sync(this);
			this.runLabel = 1;
			yield();
			label(1);
			terminate();
		}
	}


	public static class reader extends PJProcess {
		PJChannel<Integer> _pd$cr;
		PJBarrier _pd$b;
		int _ld0$x;

	    public reader(PJChannel<Integer> _pd$cr, PJBarrier _pd$b) {
	    	this._pd$b = _pd$b;
	    	this._pd$cr = _pd$cr;
	    }

		@Override
		public synchronized void run() {
			switch(this.runLabel) {
				case 0: break;
			    case 1: resume(1); break;
			    case 2: resume(2); break;
			    case 3: resume(3); break;
			}
			label(1);
			if(_pd$cr.isReadyToRead(this)) {
				_ld0$x = _pd$cr.read(this);
				this.runLabel = 2;
				yield();
			} else {
				setNotReady();
				_pd$cr.addReader(this);
				this.runLabel = 1;
				yield();
			}
			label(2);
			std.io.println( ("x=" + _ld0$x) );
			std.io.println( "syncing reader" );
			_pd$b.sync(this);
			this.runLabel = 3;
			yield();
			label(3);
			terminate();
		}
	}


	public static class writer extends PJProcess {
		PJChannel<Integer> _pd$cw;

	    public writer(PJChannel<Integer> _pd$cw) {
	    	this._pd$cw = _pd$cw;
	    }

		@Override
		public synchronized void run() {
			switch(this.runLabel) {
				case 0: break;
			    case 1: resume(1); break;
			    case 2: resume(2); break;
			}

			label(1);
			if (_pd$cw.isReadyToWrite()) {
				_pd$cw.write(this, 10);
				this.runLabel = 2;
				yield();
			} else {
				setNotReady();
				this.runLabel = 1;
				yield();
			}
			label(2);
			std.io.println( "syncing writer" );
			terminate();
		}
	}


	public static class bar extends PJProcess {
		PJBarrier _pd$b;
		PJBarrier _pd$a;

	    public bar(PJBarrier _pd$b, PJBarrier _pd$a) {
	    	this._pd$a = _pd$a;
	    	this._pd$b = _pd$b;
	    }

		@Override
		public synchronized void run() {
			switch(this.runLabel) {
				case 0: break;
			    case 1: resume(1); break;
			}
			std.io.println( "syncing bar" );
			_pd$a.sync(this);
			this.runLabel = 1;
			yield();
			label(1);
			terminate();
		}
	}


	public static class main extends PJProcess {
		String[] _pd$args;
		PJBarrier _ld0$b;

	    public main(String[] _pd$args) {
	    	this._pd$args = _pd$args;
	    }

		@Override
		public synchronized void run() {
			switch(this.runLabel) {
				case 0: break;
			    case 1: resume(1); break;
			}
			_ld0$b = new PJBarrier();
			final PJPar par1 = new PJPar(1, this);

			(new simpleBarrier.foo( _ld0$b ){
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

		(new simpleBarrier.main(args)).schedule();

		System.out.println("Starting the scheduler...");
		PJProcess.scheduler.start();

		System.out.println("Scheduler thread.start() done.");
	}
	public static boolean getTrue() {
		return true;
	}
}