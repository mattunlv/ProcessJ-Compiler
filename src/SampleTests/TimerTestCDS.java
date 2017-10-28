package SampleTests;

import ProcessJ.runtime.PJPar;
import ProcessJ.runtime.PJProcess;
import ProcessJ.runtime.Scheduler;
import ProcessJ.runtime.PJTimer;

public class TimerTestCDS {
	
	public static class foo extends PJProcess {
		
		PJTimer t;

		@Override
		public synchronized void run() {
			
			switch(this.runLabel) {
				case 0: break;
				case 1: resume(1);break;
			}

			t = new PJTimer(this, -1);
			try {

				System.out.println("timer started....");

				t.start();

				//some delay
				for(int i=0; i<Integer.MAX_VALUE;i++) {}

				setNotReady();
				this.runLabel = 1;
				yield();
			} catch (InterruptedException e) {
				System.out.println("InterruptedException!");
			}
			label(1);
			
			System.out.println("timer done....");

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

			System.out.println("scheduling foo...");

			(new TimerTestCDS.foo( ){
			  public void finalize() {
			    par1.decrement();    
			  }
			}).schedule();

			setNotReady();
			this.runLabel = 2;
			yield();
			label(2);
			
			System.out.println("done with foo...");

			terminate();
		}
	}

	public static void main(String[] args) {
		Scheduler scheduler = new Scheduler();
		PJProcess.scheduler = scheduler;
		System.out.println("Added _main process to scheduler...");

		(new TimerTestCDS.main(args)).schedule();

		System.out.println("Starting the scheduler...");
		PJProcess.scheduler.start();

		System.out.println("Scheduler thread.start() done.");
	}
	
	
}
