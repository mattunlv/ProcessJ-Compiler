package Generated.simpleRecurseTest;
import java.util.*;
import ProcessJ.runtime.PJProcess;
import ProcessJ.runtime.PJTimer;
import ProcessJ.runtime.*;

public class simpleRecurseTest { 
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
			System.out.println( ("Hello! " + _pd$n) );
			if( (_pd$n == 0) ) {
			  terminate();
			  return;
			};
			label(1);	
			final PJPar par1 = new PJPar(1, this);

			(new simpleRecurseTest.f( (_pd$n - 1) ){
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

			(new simpleRecurseTest.f( 10 ){
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

		(new simpleRecurseTest.main(args)).schedule();

		System.out.println("Starting the scheduler...");
		PJProcess.scheduler.start();

		System.out.println("Scheduler thread.start() done.");
	}
}