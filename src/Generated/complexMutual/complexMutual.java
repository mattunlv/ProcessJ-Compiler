package Generated.complexMutual;
import java.util.*;
import ProcessJ.runtime.PJProcess;
import ProcessJ.runtime.PJTimer;
import ProcessJ.runtime.*;

public class complexMutual { 
	public static class a1 extends PJProcess {

		PJChannel<Integer> _pd$c;
		int _pd$n;

	    public a1(PJChannel<Integer> _pd$c, int _pd$n) {
	    	this._pd$c = _pd$c;
	    	this._pd$n = _pd$n;
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
				_pd$c.write(this, _pd$n);
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


	public static class b1 extends PJProcess {

		PJChannel<Integer> _pd$c;
		String _pd$s;
		int _ld0$val;

	    public b1(PJChannel<Integer> _pd$c, String _pd$s) {
	    	this._pd$c = _pd$c;
	    	this._pd$s = _pd$s;
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
				_ld0$val = _pd$c.read(this);
				this.runLabel = 2;
				yield();
			} else {
				setNotReady();
				_pd$c.addReader(this);
				this.runLabel = 1;
				yield();
			}
			label(2);
			System.out.println( ((_pd$s + ":") + _ld0$val) );
			terminate();
		}
	}


	public static class a extends PJProcess {

		PJChannel<Integer> _pd$out;
		PJChannel<Integer> _pd$in;
		int _pd$n;
		String _pd$s;

	    public a(PJChannel<Integer> _pd$out, PJChannel<Integer> _pd$in, int _pd$n, String _pd$s) {
	    	this._pd$s = _pd$s;
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
			if( (_pd$n > 0) ) {
			  label(1);	
			  final PJPar par1 = new PJPar(1, this);

			  (new complexMutual.a1( _pd$out, _pd$n ){
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

			  (new complexMutual.b( _pd$in, _pd$out, (_pd$n - 1), _pd$s ){
			    public void finalize() {
			      par2.decrement();    
			    }
			  }).schedule();

			  setNotReady();
			  this.runLabel = 4;
			  yield();
			  label(4);
			};
			terminate();
		}
	}


	public static class b extends PJProcess {

		PJChannel<Integer> _pd$in;
		PJChannel<Integer> _pd$out;
		int _pd$n;
		String _pd$s;

	    public b(PJChannel<Integer> _pd$in, PJChannel<Integer> _pd$out, int _pd$n, String _pd$s) {
	    	this._pd$s = _pd$s;
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
			if( (_pd$n > 0) ) {
			  label(1);	
			  final PJPar par1 = new PJPar(1, this);

			  (new complexMutual.b1( _pd$in, _pd$s ){
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

			  (new complexMutual.a( _pd$out, _pd$in, (_pd$n - 1), _pd$s ){
			    public void finalize() {
			      par2.decrement();    
			    }
			  }).schedule();

			  setNotReady();
			  this.runLabel = 4;
			  yield();
			  label(4);
			};
			terminate();
		}
	}


	public static class main extends PJProcess {

		String[] _pd$args;
		PJOne2OneChannel<Integer> _ld0$c1;
		PJOne2OneChannel<Integer> _ld1$c2;

	    public main(String[] _pd$args) {
	    	this._pd$args = _pd$args;
	    }

		@Override
		public synchronized void run() {
			switch(this.runLabel) {
				case 0: break;
			    case 1: resume(1); break;
			    case 2: resume(2); break;
			}
			_ld0$c1 = new PJOne2OneChannel<Integer>();
			_ld1$c2 = new PJOne2OneChannel<Integer>();
			label(1);	
			final PJPar par1 = new PJPar(2, this);

			(new complexMutual.a( _ld0$c1, _ld1$c2, 10, "a" ){
			  public void finalize() {
			    par1.decrement();    
			  }
			}).schedule();
			(new complexMutual.b( _ld0$c1, _ld1$c2, 10, "b" ){
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

	public static void main(String[] args) {
		Scheduler scheduler = new Scheduler();
		PJProcess.scheduler = scheduler;
		System.out.println("Added _main process to scheduler...");

		(new complexMutual.main(args)).schedule();

		System.out.println("Starting the scheduler...");
		PJProcess.scheduler.start();

		System.out.println("Scheduler thread.start() done.");
	}
}