package Generated.simpleParTest;
import java.util.*;
import ProcessJ.runtime.PJProcess;
import ProcessJ.runtime.PJTimer;
import ProcessJ.runtime.*;

public class simpleParTest { 
	public static class reader extends PJProcess {
		PJChannel<Integer> _pd$cr;
		int _ld0$x;

	    public reader(PJChannel<Integer> _pd$cr) {
	    	this._pd$cr = _pd$cr;
	    }

		@Override
		public synchronized void run() {
			switch(this.runLabel) {
				case 0: break;
			    case 1: resume(1); break;
			    case 2: resume(2); break;
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
			terminate();
		}
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

			(new simpleParTest.foo(){
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

		(new simpleParTest.main(args)).schedule();

		System.out.println("Starting the scheduler...");
		PJProcess.scheduler.start();

		System.out.println("Scheduler thread.start() done.");
	}
	public static class foo extends PJProcess {
		PJOne2OneChannel<Integer> _ld0$c;
		int _ld1$x;
		int _ld2$y;

	    public foo() {
	    }

		@Override
		public synchronized void run() {
			switch(this.runLabel) {
				case 0: break;
			    case 1: resume(1); break;
			    case 2: resume(2); break;
			}
			_ld0$c = new PJOne2OneChannel<Integer>();
			label(1);	
			final PJPar par1 = new PJPar(3, this);

			new PJProcess(){
				@Override
				public synchronized void run() {
					switch(this.runLabel) {
						case 0: break;
					    case 3: resume(3); break;
					    case 4: resume(4); break;
					}
					label(3);	
					final PJPar par2 = new PJPar(1, this);

					new PJProcess(){
						@Override
						public synchronized void run() {
							_ld1$x = 10;
					      	terminate();
						}

						@Override
						public void finalize() {
							par2.decrement();	
						}
					}.schedule();

					setNotReady();
					this.runLabel = 4;
					yield();
					label(4);
			      	terminate();
				}

				@Override
				public void finalize() {
					par1.decrement();	
				}
			}.schedule();
			(new simpleParTest.reader( _ld0$c ){
			  public void finalize() {
			    par1.decrement();    
			  }
			}).schedule();
			(new simpleParTest.writer( _ld0$c ){
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


}