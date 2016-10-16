package Generated.TimerTest;
import java.util.*;
import ProcessJ.runtime.PJProcess;
import ProcessJ.runtime.PJTimer;
import ProcessJ.runtime.*;

public class TimerTest { 
	public static class foo extends PJProcess {

		PJTimer _ld0$t;
		int _ld1$x;

	    public foo() {
	    }

		@Override
		public synchronized void run() {
			switch(this.runLabel) {
				case 0: break;
			    case 1: resume(1); break;
			}
			_ld1$x=10;
			_ld0$t = new PJTimer(this, 10000);
			try {
				_ld0$t.start();
				setNotReady();
				this.runLabel = 1;
				yield();
			} catch (InterruptedException e) {
				System.out.println("Timer Interrupted Exception!");
			}
			label(1);
			std.io.println( ("10 secs has passed!" + 11) );
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

			(new TimerTest.foo(){
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

		(new TimerTest.main(args)).schedule();

		System.out.println("Starting the scheduler...");
		PJProcess.scheduler.start();

		System.out.println("Scheduler thread.start() done.");
	}
}