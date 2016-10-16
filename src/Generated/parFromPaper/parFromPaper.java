package Generated.parFromPaper;
import java.util.*;
import ProcessJ.runtime.PJProcess;
import ProcessJ.runtime.PJTimer;
import ProcessJ.runtime.*;

public class parFromPaper { 
	public static class p1 extends PJProcess {

		PJChannel<Integer> _pd$c;
		int _ld0$y;

	    public p1(PJChannel<Integer> _pd$c) {
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
				_ld0$y = _pd$c.read(this);
				this.runLabel = 2;
				yield();
			} else {
				setNotReady();
				_pd$c.addReader(this);
				this.runLabel = 1;
				yield();
			}
			label(2);
			terminate();
		}
	}


	public static class p2 extends PJProcess {

		PJChannel<Integer> _pd$c;
		int _pd$x;

	    public p2(PJChannel<Integer> _pd$c, int _pd$x) {
	    	this._pd$c = _pd$c;
	    	this._pd$x = _pd$x;
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
				_pd$c.write(this, _pd$x);
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


	public static int p3(int _pd$y) {
		return _pd$y;
	}
	public static int p4(int _pd$z) {
		return _pd$z;
	}
	public static void bar(int _pd$y) {
		_pd$y = (_pd$y + 10);
	}
	public static class main extends PJProcess {

		String[] _pd$args;

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
			label(1);	
			final PJPar par1 = new PJPar(1, this);

			(new parFromPaper.foo( 5 ){
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

		(new parFromPaper.main(args)).schedule();

		System.out.println("Starting the scheduler...");
		PJProcess.scheduler.start();

		System.out.println("Scheduler thread.start() done.");
	}
	public static int compute3(int _pd$x) {
		_pd$x = 5;
		return _pd$x;
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


	public static class foo extends PJProcess {

		int _pd$x;
		PJOne2OneChannel<Integer> _ld0$c;
		int _ld1$temp;
		int _ld2$temp;
		int _ld3$temp;
		int _ld4$p;
		int _ld5$k;

	    public foo(int _pd$x) {
	    	this._pd$x = _pd$x;
	    }

		@Override
		public synchronized void run() {
			switch(this.runLabel) {
				case 0: break;
			    case 1: resume(1); break;
			    case 2: resume(2); break;
			    case 9: resume(9); break;
			    case 10: resume(10); break;
			    case 11: resume(11); break;
			    case 12: resume(12); break;
			}
			_ld0$c = new PJOne2OneChannel<Integer>();
			label(1);	
			final PJPar par1 = new PJPar(5, this);

			new PJProcess(){
				public synchronized void run() {
					switch(this.runLabel) {
						case 0: break;
					    case 3: resume(3); break;
					    case 4: resume(4); break;
					}
					label(3);
					if(_ld0$c.isReadyToRead(this)) {
						_pd$x = _ld0$c.read(this);
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

				public void finalize() {
					par1.decrement();	
				}
			}.schedule();
			new PJProcess(){
				public synchronized void run() {
					if( (_pd$x == 5) ) {
					  _pd$x = 10;
					} else {
					  _pd$x = 100;
					};
			      	terminate();
				}

				public void finalize() {
					par1.decrement();	
				}
			}.schedule();
			new PJProcess(){
				public synchronized void run() {
					while( (_pd$x < 5) ) {
					  _pd$x--;
					};
			      	terminate();
				}

				public void finalize() {
					par1.decrement();	
				}
			}.schedule();
			label(5);
			if(_ld0$c.isReadyToRead(this)) {
				_ld1$temp = _ld0$c.read(this);
				this.runLabel = 6;
				yield();
			} else {
				setNotReady();
				_ld0$c.addReader(this);
				this.runLabel = 5;
				yield();
			}
			label(6);

			label(7);
			if(_ld0$c.isReadyToRead(this)) {
				_ld3$temp = _ld0$c.read(this);
				this.runLabel = 8;
				yield();
			} else {
				setNotReady();
				_ld0$c.addReader(this);
				this.runLabel = 7;
				yield();
			}
			label(8);
			_ld2$temp=parFromPaper.compute3( _ld3$temp );
			(new parFromPaper.compute( _ld1$temp, _ld2$temp, 10 ){
			  public void finalize() {
			    par1.decrement();    
			  }
			}).schedule();
			(new parFromPaper.p1( _ld0$c ){
			  public void finalize() {
			    par1.decrement();    
			  }
			}).schedule();

			setNotReady();
			this.runLabel = 2;
			yield();
			label(2);
			parFromPaper.bar( 10 );
			label(9);	
			final PJPar par2 = new PJPar(1, this);

			(new parFromPaper.p2( _ld0$c, _pd$x ){
			  public void finalize() {
			    par2.decrement();    
			  }
			}).schedule();

			setNotReady();
			this.runLabel = 10;
			yield();
			label(10);
			label(11);	
			final PJPar par3 = new PJPar(3, this);

			new PJProcess(){
				public synchronized void run() {
					_ld4$p=parFromPaper.p3( 4 );
			      	terminate();
				}

				public void finalize() {
					par3.decrement();	
				}
			}.schedule();
			new PJProcess(){
				public synchronized void run() {
					_ld5$k=parFromPaper.p4( 5 );
			      	terminate();
				}

				public void finalize() {
					par3.decrement();	
				}
			}.schedule();
			new PJProcess(){
				public synchronized void run() {
					if( (_pd$x == 5) ) {
					  _pd$x = 10;
					};
			      	terminate();
				}

				public void finalize() {
					par3.decrement();	
				}
			}.schedule();

			setNotReady();
			this.runLabel = 12;
			yield();
			label(12);
			terminate();
		}
	}


}