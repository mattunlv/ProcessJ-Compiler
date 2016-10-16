package Generated.AltSimpleTest;
import java.util.*;
import ProcessJ.runtime.PJProcess;
import ProcessJ.runtime.PJTimer;
import ProcessJ.runtime.*;

public class AltSimpleTest { 
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
				System.out.println("wrote 10");
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


	public static boolean getBool() {
		return true;
	}
	public static class main extends PJProcess {
		String[] _pd$args;
		PJOne2OneChannel<Integer> _ld0$c;
		PJTimer _ld1$t;
		int _ld2$x;
		PJAlt alt;
		int chosen;

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
			_ld0$c = new PJOne2OneChannel<Integer>();;
			_ld1$t = new PJTimer();;
			_ld2$x = 10;;
			label(1);	
			final PJPar par1 = new PJPar(2, this);

			(new AltSimpleTest.writer( _ld0$c ){
			  public void finalize() {
			    par1.decrement();    
			  }
			}).schedule();
			new PJProcess(){
				@Override
				public synchronized void run() {
					switch(this.runLabel) {
						case 0: break;
					    case 3: resume(3); break;
					    case 4: resume(4); break;
					}
					alt = new PJAlt(1, this);	
					Object[] guards = {_ld0$c};
					boolean[] boolGuards = {true};	

					boolean bRet = alt.setGuards(boolGuards, guards);

					if (!bRet) {
						System.out.println("RuntimeException: One of the boolean guards needs to be true!!");
						System.exit(1);
					}

					label(3);	

					chosen = alt.getReadyGuardIndex();
//					System.out.println("chosen=" + chosen);
					switch(chosen) {
						case 0: 
						  	_ld2$x = _ld0$c.read(this);
						  	;
						  	_ld2$x += 5;
						  	System.out.println("modified read value=" + _ld2$x);
							break;
					}


					if (chosen == -1) {
						this.runLabel = 3;
						yield();	
					} else {
						this.runLabel = 4;
						yield();
					}

					label(4);;
			      	terminate();
				}

				@Override
				public void finalize() {
					par1.decrement();	
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

		(new AltSimpleTest.main(args)).schedule();

		System.out.println("Starting the scheduler...");
		PJProcess.scheduler.start();

		System.out.println("Scheduler thread.start() done.");
	}
}