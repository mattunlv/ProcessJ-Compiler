package Generated.parRecurseTest;
import java.util.*;
import ProcessJ.runtime.PJProcess;
import ProcessJ.runtime.PJTimer;
import ProcessJ.runtime.*;

public class parRecurseTest { 
	public static class f extends PJProcess {
		int _pd$n;

	    public f(int _pd$n) {
	    	this._pd$n = _pd$n;
	    }

		@Override
		public synchronized void run() {
			switch(this.runLabel) {
				case 0: break;
			    case 1: resume(1); break;
			    case 2: resume(2); break;
			}
			std.io.println( ("Hello! " + _pd$n) );
			if( (_pd$n == 0) ) {
			  terminate();
			  return;
			};
			label(1);	
			final PJPar par1 = new PJPar(1, this);

			(new parRecurseTest.f( (_pd$n - 1) ){
			  public void finalize() {
			    par1.decrement();    
			  }
			}).schedule();

			setNotReady();
			this.runLabel = 2;
			yield();
			label(2);
			terminate();
			return;
		}
	}


	public static class main extends PJProcess {
		String[] _pd$args;
		int _ld0$x;
		PJBarrier _ld1$a;
		PJBarrier _ld2$b;

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
			_ld1$a = new PJBarrier();
			_ld2$b = new PJBarrier();
			label(1);	
			final PJPar par1 = new PJPar(1, this);
			_ld1$a.enroll(1);

			new PJProcess(){
				@Override
				public synchronized void run() {
					switch(this.runLabel) {
						case 0: break;
					    case 3: resume(3); break;
					    case 4: resume(4); break;
					}
					label(3);	
					final PJPar par2 = new PJPar(2, this);
					_ld2$b.enroll(2);

					(new parRecurseTest.f( 10 ){
					  public void finalize() {
					    par2.decrement();    
						_ld2$b.resign();
					  }
					}).schedule();
					new PJProcess(){
						@Override
						public synchronized void run() {
							_ld0$x = 10;
					      	terminate();
						}

						@Override
						public void finalize() {
							par2.decrement();	
							_ld2$b.resign();
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
					_ld1$a.resign();
				}
			}.schedule();

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

		(new parRecurseTest.main(args)).schedule();

		System.out.println("Starting the scheduler...");
		PJProcess.scheduler.start();

		System.out.println("Scheduler thread.start() done.");
	}
	public static class channelReadExprTest extends PJProcess {
		int _ld0$x;
		PJOne2OneChannel<Integer> _ld1$cr;
		PJBarrier _ld2$a;
		PJBarrier _ld3$b;
		PJBarrier _ld4$c;

	    public channelReadExprTest() {
	    }

		@Override
		public synchronized void run() {
			switch(this.runLabel) {
				case 0: break;
			    case 1: resume(1); break;
			    case 2: resume(2); break;
			}
			_ld1$cr = new PJOne2OneChannel<Integer>();
			_ld2$a = new PJBarrier();
			_ld3$b = new PJBarrier();
			_ld4$c = new PJBarrier();
			label(1);	
			final PJPar par1 = new PJPar(2, this);
			_ld2$a.enroll(2);

			new PJProcess(){
				@Override
				public synchronized void run() {
					switch(this.runLabel) {
						case 0: break;
					    case 3: resume(3); break;
					    case 4: resume(4); break;
					}
					label(3);
					if(_ld1$cr.isReadyToRead(this)) {
						_ld1$cr.read(this);
						this.runLabel = 4;
						yield();
					} else {
						setNotReady();
						_ld1$cr.addReader(this);
						this.runLabel = 3;
						yield();
					}
					label(4);
			      	terminate();
				}

				@Override
				public void finalize() {
					par1.decrement();	
					_ld2$a.resign();
				}
			}.schedule();
			new PJProcess(){
				@Override
				public synchronized void run() {
					switch(this.runLabel) {
						case 0: break;
					    case 5: resume(5); break;
					    case 6: resume(6); break;
					}
					label(5);	
					final PJPar par2 = new PJPar(1, this);
					_ld3$b.enroll(1);
					_ld4$c.enroll(1);

					new PJProcess(){
						@Override
						public synchronized void run() {
							switch(this.runLabel) {
								case 0: break;
							    case 7: resume(7); break;
							    case 8: resume(8); break;
							}
							label(7);
							if(_ld1$cr.isReadyToRead(this)) {
								_ld1$cr.read(this);
								this.runLabel = 8;
								yield();
							} else {
								setNotReady();
								_ld1$cr.addReader(this);
								this.runLabel = 7;
								yield();
							}
							label(8);
					      	terminate();
						}

						@Override
						public void finalize() {
							par2.decrement();	
							_ld3$b.resign();
							_ld4$c.resign();
						}
					}.schedule();

					setNotReady();
					this.runLabel = 6;
					yield();
					label(6);
			      	terminate();
				}

				@Override
				public void finalize() {
					par1.decrement();	
					_ld2$a.resign();
				}
			}.schedule();

			setNotReady();
			this.runLabel = 2;
			yield();
			label(2);
			terminate();
		}
	}


}